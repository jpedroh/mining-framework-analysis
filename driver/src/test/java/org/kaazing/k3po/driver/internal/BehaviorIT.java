package org.kaazing.k3po.driver.internal;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.internal.test.utils.K3poTestRule;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

public class BehaviorIT {
  private final K3poTestRule k3po = new K3poTestRule();

  private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

  @Rule public final ExpectedException expectedExceptions = ExpectedException.none();

  @Rule public final TestRule chain = outerRule(k3po).around(timeout);

  @Test @TestSpecification(value = { "many.el.expressions" }) public void testManyReadsAndWriteExpressions() throws Exception {
    k3po.finish();
  }

  @Test @TestSpecification(value = { "delayed.connect" }) public void testDelayedClientConnect() throws Exception {
    k3po.finish();
  }

  @Test @TestSpecification(value = { "notifying.accept" }) public void testNotifyingAccept() throws Exception {
    k3po.finish();
  }

  @Test @TestSpecification(value = { "delayed.connect.via.testframework" }) public void testDelayedClientConnectViaTestFramework() throws Exception {
    k3po.notifyBarrier("NOTIFY_FROM_FRAMEWORK");
    k3po.finish();
  }

  @Test @TestSpecification(value = { "duplicate.awaits.notified.from.test.framework" }) public void testDuplicateAwaitsNotifiedFromTestFramework() throws Exception {
    k3po.notifyBarrier("NOTIFY_FROM_FRAMEWORK");
    k3po.finish();
  }

  @Test @TestSpecification(value = { "delayed.http.connect" }) public void testDelayedHttpClientConnect() throws Exception {
    k3po.finish();
  }

  @Test @TestSpecification(value = { "connect.expression" }) public void testConnectWithExpression() throws Exception {
    k3po.finish();
  }

  @Test @TestSpecification(value = { "accept.expression" }) public void testAcceptWithExpression() throws Exception {
    k3po.finish();
  }

  @TestSpecification(value = "test.barrier.passing.from.test.framework") @Test public void testPassingBarriers() throws Exception {
    k3po.notifyBarrier("AWAITING_BARRIER");
    k3po.awaitBarrier("NOTIFYING_BARRIER");
    k3po.finish();
  }
}