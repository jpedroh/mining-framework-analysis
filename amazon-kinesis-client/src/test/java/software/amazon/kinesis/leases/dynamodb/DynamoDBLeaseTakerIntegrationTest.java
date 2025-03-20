package software.amazon.kinesis.leases.dynamodb;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import software.amazon.kinesis.leases.Lease;
import software.amazon.kinesis.leases.LeaseIntegrationTest;
import software.amazon.kinesis.leases.exceptions.LeasingException;
import software.amazon.kinesis.metrics.NullMetricsFactory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(value = MockitoJUnitRunner.class) public class DynamoDBLeaseTakerIntegrationTest extends LeaseIntegrationTest {
  private static final long LEASE_DURATION_MILLIS = 1000L;

  private DynamoDBLeaseTaker taker;

  @Before public void setup() {
    taker = new DynamoDBLeaseTaker(leaseRefresher, "foo", LEASE_DURATION_MILLIS, new NullMetricsFactory());
  }

  @Test public void testSimpleLeaseTake() throws LeasingException {
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    builder.withLease("1", null).build();
    builder.takeMutateAssert(taker, "1");
  }

  @Test public void testNotTakeUpdatedLease() throws LeasingException {
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    builder.withLease("1", "bar").build();
    builder.takeMutateAssert(taker);
    builder.renewAllLeases();
    builder.passTime(LEASE_DURATION_MILLIS + 1);
    builder.takeMutateAssert(taker);
  }

  @Test public void testTakeOwnLease() throws LeasingException {
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    builder.withLease("1", taker.getWorkerIdentifier()).build();
    builder.takeMutateAssert(taker);
    builder.passTime(LEASE_DURATION_MILLIS + 1);
    builder.takeMutateAssert(taker, "1");
  }

  @Test public void testNotTakeNewOwnedLease() throws LeasingException, InterruptedException {
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    builder.withLease("1", "bar").build();
    builder.takeMutateAssert(taker);
    builder.passTime(LEASE_DURATION_MILLIS + 1);
    builder.takeMutateAssert(taker, "1");
  }

  @Test public void testTakeEvictedLease() throws LeasingException, InterruptedException {
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    builder.withLease("1", "bar").build();
    builder.takeMutateAssert(taker);
    builder.evictLease("1");
    builder.takeMutateAssert(taker, "1");
  }

  /**
     * Verify that we take leases non-greedily by setting up an environment where there are 4 leases and 2 workers,
     * only one of which holds a lease. This leaves 3 free leases, but LeaseTaker should decide it needs 2 leases and
     * only take 2.
     */
  @Test public void testNonGreedyTake() throws LeasingException {
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    for (int i = 0; i < 3; i++) {
      builder.withLease(Integer.toString(i), null);
    }
    builder.withLease("4", "bar").build();
    taker.withVeryOldLeaseDurationNanosMultipler(5000000000L);
    builder.takeMutateAssert(taker, 2);
  }

  /**
     * Verify that we take all very old leases by setting up an environment where there are 4 leases and 2 workers,
     * only one of which holds a lease. This leaves 3 free leases. LeaseTaker should take all 3 leases since they
     * are denoted as very old.
     */
  @Test public void testVeryOldLeaseTaker() throws LeasingException {
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    for (int i = 0; i < 3; i++) {
      builder.withLease(Integer.toString(i), null);
    }
    builder.withLease("4", "bar").build();
    builder.takeMutateAssert(taker, 3);
  }

  /**
     * Verify that when getAllLeases() is called, DynamoDBLeaseTaker
     * - does not call listLeases()
     * - returns cached result was built during takeLeases() operation to return result
     */
  @Test public void testGetAllLeases() throws LeasingException {
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    Map<String, Lease> addedLeases = builder.withLease("1", "bar").withLease("2", "bar").withLease("3", "baz").withLease("4", "baz").withLease("5", "foo").build();
    assertThat(taker.allLeases().size(), equalTo(0));
    taker.takeLeases();
    Collection<Lease> allLeases = taker.allLeases();
    assertThat(allLeases.size(), equalTo(addedLeases.size()));
    assertThat(addedLeases.values().containsAll(allLeases), equalTo(true));
  }

  /**
     * Sets the leaseDurationMillis to 0, ensuring a get request to update the existing lease after computing
     * leases to take
     */
  @Test public void testSlowGetAllLeases() throws LeasingException {
    long leaseDurationMillis = 0;
    taker = new DynamoDBLeaseTaker(leaseRefresher, "foo", leaseDurationMillis, new NullMetricsFactory());
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    Map<String, Lease> addedLeases = builder.withLease("1", "bar").withLease("2", "bar").withLease("5", "foo").build();
    assertThat(taker.allLeases().size(), equalTo(0));
    taker.takeLeases();
    Collection<Lease> allLeases = taker.allLeases();
    assertThat(allLeases.size(), equalTo(addedLeases.size()));
    assertEquals(addedLeases.values().size(), allLeases.size());
  }

  /**
     * Verify that LeaseTaker does not steal when it's only short 1 lease and the other worker is at target. Set up a
     * scenario where there are 4 leases held by two servers, and a third server with one lease. The third server should
     * not steal.
     */
  @Test public void testNoStealWhenOffByOne() throws LeasingException {
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    builder.withLease("1", "bar").withLease("2", "bar").withLease("3", "baz").withLease("4", "baz").withLease("5", "foo").build();
    builder.takeMutateAssert(taker);
  }

  /**
     * Verify that one activity is stolen from the highest loaded server when a server needs more than one lease and no
     * expired leases are available. Setup: 4 leases, server foo holds 0, bar holds 1, baz holds 5.
     *
     * Foo should steal from baz.
     */
  @Test public void testSteal() throws LeasingException {
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    builder.withLease("1", "bar");
    for (int i = 2; i <= 6; i++) {
      String shardId = Integer.toString(i);
      builder.withLease(shardId, "baz");
    }
    builder.build();
    Map<String, Lease> takenLeases = builder.stealMutateAssert(taker, 1);
    String shardIdStolen = takenLeases.keySet().iterator().next();
    Assert.assertFalse(shardIdStolen.equals("1"));
  }

  /**
     * Verify that stealing does not happen if LeaseTaker takes at least one expired lease, even if it needs more than
     * one.
     */
  @Test public void testNoStealWhenExpiredLeases() throws LeasingException {
    TestHarnessBuilder builder = new TestHarnessBuilder(leaseRefresher);
    builder.withLease("1", null);
    for (int i = 2; i <= 4; i++) {
      String shardId = Integer.toString(i);
      builder.withLease(shardId, "bar");
    }
    builder.build();
    builder.takeMutateAssert(taker, "1");
  }
}