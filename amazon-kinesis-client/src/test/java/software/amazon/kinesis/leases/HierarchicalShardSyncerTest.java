package software.amazon.kinesis.leases;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import software.amazon.awssdk.services.kinesis.model.HashKeyRange;
import software.amazon.awssdk.services.kinesis.model.SequenceNumberRange;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.kinesis.common.InitialPositionInStream;
import software.amazon.kinesis.common.InitialPositionInStreamExtended;
import software.amazon.kinesis.exceptions.internal.KinesisClientLibIOException;
import software.amazon.kinesis.leases.dynamodb.DynamoDBLeaseRefresher;
import software.amazon.kinesis.leases.exceptions.DependencyException;
import software.amazon.kinesis.metrics.MetricsScope;
import software.amazon.kinesis.metrics.NullMetricsScope;
import software.amazon.kinesis.retrieval.kpl.ExtendedSequenceNumber;

@RunWith(value = MockitoJUnitRunner.class) public class HierarchicalShardSyncerTest {
  private static final InitialPositionInStreamExtended INITIAL_POSITION_LATEST = InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.LATEST);

  private static final InitialPositionInStreamExtended INITIAL_POSITION_TRIM_HORIZON = InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.TRIM_HORIZON);

  private static final InitialPositionInStreamExtended INITIAL_POSITION_AT_TIMESTAMP = InitialPositionInStreamExtended.newInitialPositionAtTimestamp(new Date(1000L));

  private static final int EXPONENT = 128;

  private static final String LEASE_OWNER = "TestOwnere";

  private static final MetricsScope SCOPE = new NullMetricsScope();

  private final boolean cleanupLeasesOfCompletedShards = true;

  private final boolean ignoreUnexpectedChildShards = false;

  private HierarchicalShardSyncer hierarchicalShardSyncer;

  /**
     * Old/Obsolete max value of a sequence number (2^128 -1).
     */
  public static final BigInteger MAX_SEQUENCE_NUMBER = new BigInteger("2").pow(EXPONENT).subtract(BigInteger.ONE);

  @Mock private ShardDetector shardDetector;

  @Mock private DynamoDBLeaseRefresher dynamoDBLeaseRefresher;

  @Before public void setup() {
    hierarchicalShardSyncer = new HierarchicalShardSyncer();
  }

  /**
     * Test determineNewLeasesToCreate() where there are no shards
     */
  @Test public void testDetermineNewLeasesToCreateNoShards() {
    final List<Shard> shards = Collections.emptyList();
    final List<Lease> leases = Collections.emptyList();
    assertThat(HierarchicalShardSyncer.determineNewLeasesToCreate(shards, leases, INITIAL_POSITION_LATEST).isEmpty(), equalTo(true));
  }

  /**
     * Test determineNewLeasesToCreate() where there are no leases and no resharding operations have been performed
     */
  @Test public void testDetermineNewLeasesToCreate0Leases0Reshards() {
    final String shardId0 = "shardId-0";
    final String shardId1 = "shardId-1";
    final SequenceNumberRange sequenceRange = ShardObjectHelper.newSequenceNumberRange("342980", null);
    final List<Shard> shards = Arrays.asList(ShardObjectHelper.newShard(shardId0, null, null, sequenceRange), ShardObjectHelper.newShard(shardId1, null, null, sequenceRange));
    final List<Lease> currentLeases = Collections.emptyList();
    final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, INITIAL_POSITION_LATEST);
    final Set<String> newLeaseKeys = newLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final Set<String> expectedLeaseShardIds = new HashSet<>(Arrays.asList(shardId0, shardId1));
    assertThat(newLeases.size(), equalTo(expectedLeaseShardIds.size()));
    assertThat(newLeaseKeys, equalTo(expectedLeaseShardIds));
  }

  /**
     * Test determineNewLeasesToCreate() where there are no leases and no resharding operations have been performed, but
     * one of the shards was marked as inconsistent.
     */
  @Test public void testDetermineNewLeasesToCreate0Leases0Reshards1Inconsistent() {
    final String shardId0 = "shardId-0";
    final String shardId1 = "shardId-1";
    final String shardId2 = "shardId-2";
    final SequenceNumberRange sequenceRange = ShardObjectHelper.newSequenceNumberRange("342980", null);
    final List<Shard> shards = Arrays.asList(ShardObjectHelper.newShard(shardId0, null, null, sequenceRange), ShardObjectHelper.newShard(shardId1, null, null, sequenceRange), ShardObjectHelper.newShard(shardId2, shardId1, null, sequenceRange));
    final List<Lease> currentLeases = Collections.emptyList();
    final Set<String> inconsistentShardIds = new HashSet<>(Collections.singletonList(shardId2));
    final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, INITIAL_POSITION_LATEST, inconsistentShardIds);
    final Set<String> newLeaseKeys = newLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final Set<String> expectedLeaseShardIds = new HashSet<>(Arrays.asList(shardId0, shardId1));
    assertThat(newLeases.size(), equalTo(expectedLeaseShardIds.size()));
    assertThat(newLeaseKeys, equalTo(expectedLeaseShardIds));
  }

  /**
     * Test bootstrapShardLeases() starting at TRIM_HORIZON ("beginning" of stream)
     */
  @Test public void testBootstrapShardLeasesAtTrimHorizon() throws Exception {
    testCheckAndCreateLeasesForShardsIfMissing(INITIAL_POSITION_TRIM_HORIZON);
  }

  /**
     * Test bootstrapShardLeases() starting at LATEST (tip of stream)
     */
  @Test public void testBootstrapShardLeasesAtLatest() throws Exception {
    testCheckAndCreateLeasesForShardsIfMissing(INITIAL_POSITION_LATEST);
  }

  @Test public void testCheckAndCreateLeasesForShardsIfMissingAtLatest() throws Exception {
    final List<Shard> shards = constructShardListForGraphA();
    final ArgumentCaptor<Lease> leaseCaptor = ArgumentCaptor.forClass(Lease.class);
    when(shardDetector.listShards()).thenReturn(shards);
    when(dynamoDBLeaseRefresher.listLeases()).thenReturn(Collections.emptyList());
    when(dynamoDBLeaseRefresher.createLeaseIfNotExists(leaseCaptor.capture())).thenReturn(true);
    hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, INITIAL_POSITION_LATEST, cleanupLeasesOfCompletedShards, false, SCOPE);
    final Set<String> expectedShardIds = new HashSet<>(Arrays.asList("shardId-4", "shardId-8", "shardId-9", "shardId-10"));
    final List<Lease> requestLeases = leaseCaptor.getAllValues();
    final Set<String> requestLeaseKeys = requestLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final Set<ExtendedSequenceNumber> extendedSequenceNumbers = requestLeases.stream().map(Lease::checkpoint).collect(Collectors.toSet());
    assertThat(requestLeases.size(), equalTo(expectedShardIds.size()));
    assertThat(requestLeaseKeys, equalTo(expectedShardIds));
    assertThat(extendedSequenceNumbers.size(), equalTo(1));
    extendedSequenceNumbers.forEach((seq) -> assertThat(seq, equalTo(ExtendedSequenceNumber.LATEST)));
    verify(shardDetector).listShards();
    verify(dynamoDBLeaseRefresher, times(expectedShardIds.size())).createLeaseIfNotExists(any(Lease.class));
    verify(dynamoDBLeaseRefresher, never()).deleteLease(any(Lease.class));
  }

  @Test public void testCheckAndCreateLeasesForShardsWithShardList() throws Exception {
    final List<Shard> 
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/4a5a42f8359123d49e1752a7a488564c578671a9/amazon-kinesis-client/src/test/java/software/amazon/kinesis/leases/HierarchicalShardSyncerTest.java/left.java
    shards = constructShardListForGraphA()
=======
    latestShards = constructShardListForGraphA()
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/4a5a42f8359123d49e1752a7a488564c578671a9/amazon-kinesis-client/src/test/java/software/amazon/kinesis/leases/HierarchicalShardSyncerTest.java/right.java
    ;
    final ArgumentCaptor<Lease> leaseCaptor = ArgumentCaptor.forClass(Lease.class);
    when(shardDetector.listShards()).thenReturn(
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/4a5a42f8359123d49e1752a7a488564c578671a9/amazon-kinesis-client/src/test/java/software/amazon/kinesis/leases/HierarchicalShardSyncerTest.java/left.java
    shards
=======
    latestShards
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/4a5a42f8359123d49e1752a7a488564c578671a9/amazon-kinesis-client/src/test/java/software/amazon/kinesis/leases/HierarchicalShardSyncerTest.java/right.java
    );
    when(dynamoDBLeaseRefresher.listLeases()).thenReturn(Collections.emptyList());
    when(dynamoDBLeaseRefresher.createLeaseIfNotExists(leaseCaptor.capture())).thenReturn(true);
    hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shards, shardDetector, dynamoDBLeaseRefresher, INITIAL_POSITION_LATEST, cleanupLeasesOfCompletedShards, false, SCOPE, latestShards);
    final Set<String> expectedShardIds = new HashSet<>(Arrays.asList("shardId-4", "shardId-8", "shardId-9", "shardId-10"));
    final List<Lease> requestLeases = leaseCaptor.getAllValues();
    final Set<String> requestLeaseKeys = requestLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final Set<ExtendedSequenceNumber> extendedSequenceNumbers = requestLeases.stream().map(Lease::checkpoint).collect(Collectors.toSet());
    assertThat(requestLeases.size(), equalTo(expectedShardIds.size()));
    assertThat(requestLeaseKeys, equalTo(expectedShardIds));
    assertThat(extendedSequenceNumbers.size(), equalTo(1));
    extendedSequenceNumbers.forEach((seq) -> assertThat(seq, equalTo(ExtendedSequenceNumber.LATEST)));
    verify(shardDetector, never()).listShards();
    verify(dynamoDBLeaseRefresher, times(expectedShardIds.size())).createLeaseIfNotExists(any(Lease.class));
    verify(dynamoDBLeaseRefresher, never()).deleteLease(any(Lease.class));
  }

  @Test public void testCheckAndCreateLeasesForShardsWithEmptyShardList() throws Exception {
    final List<Shard> shards = constructShardListForGraphA();
    final ArgumentCaptor<Lease> leaseCaptor = ArgumentCaptor.forClass(Lease.class);
    when(shardDetector.listShards()).thenReturn(shards);
    when(dynamoDBLeaseRefresher.listLeases()).thenReturn(Collections.emptyList());
    when(dynamoDBLeaseRefresher.createLeaseIfNotExists(leaseCaptor.capture())).thenReturn(true);
    hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, INITIAL_POSITION_LATEST, cleanupLeasesOfCompletedShards, false, SCOPE, new ArrayList<Shard>(), shardDetector, dynamoDBLeaseRefresher, INITIAL_POSITION_LATEST, cleanupLeasesOfCompletedShards, false, SCOPE);
    final Set<String> expectedShardIds = new HashSet<>(Arrays.asList("shardId-4", "shardId-8", "shardId-9", "shardId-10"));
    final List<Lease> requestLeases = leaseCaptor.getAllValues();
    final Set<String> requestLeaseKeys = requestLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final Set<ExtendedSequenceNumber> extendedSequenceNumbers = requestLeases.stream().map(Lease::checkpoint).collect(Collectors.toSet());
    assertThat(requestLeases.size(), equalTo(expectedShardIds.size()));
    assertThat(requestLeaseKeys, equalTo(expectedShardIds));
    assertThat(extendedSequenceNumbers.size(), equalTo(
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/4a5a42f8359123d49e1752a7a488564c578671a9/amazon-kinesis-client/src/test/java/software/amazon/kinesis/leases/HierarchicalShardSyncerTest.java/left.java
    1
=======
    0
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/4a5a42f8359123d49e1752a7a488564c578671a9/amazon-kinesis-client/src/test/java/software/amazon/kinesis/leases/HierarchicalShardSyncerTest.java/right.java
    ));
    extendedSequenceNumbers.forEach((seq) -> assertThat(seq, equalTo(ExtendedSequenceNumber.LATEST)));
    verify(shardDetector, never()).listShards();
    verify(dynamoDBLeaseRefresher, 
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/4a5a42f8359123d49e1752a7a488564c578671a9/amazon-kinesis-client/src/test/java/software/amazon/kinesis/leases/HierarchicalShardSyncerTest.java/left.java
    times(expectedShardIds.size())
=======
    never()
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/4a5a42f8359123d49e1752a7a488564c578671a9/amazon-kinesis-client/src/test/java/software/amazon/kinesis/leases/HierarchicalShardSyncerTest.java/right.java
    ).createLeaseIfNotExists(any(Lease.class));
    verify(dynamoDBLeaseRefresher, never()).deleteLease(any(Lease.class));
  }

  @Test public void testCheckAndCreateLeasesForNewShardsAtTrimHorizon() throws Exception {
    testCheckAndCreateLeaseForShardsIfMissing(constructShardListForGraphA(), INITIAL_POSITION_TRIM_HORIZON);
  }

  @Test public void testCheckAndCreateLeasesForNewShardsAtTimestamp() throws Exception {
    testCheckAndCreateLeaseForShardsIfMissing(constructShardListForGraphA(), INITIAL_POSITION_AT_TIMESTAMP);
  }

  @Test(expected = KinesisClientLibIOException.class) public void testCheckAndCreateLeasesForNewShardsWhenParentIsOpen() throws Exception {
    final List<Shard> shards = new ArrayList<>(constructShardListForGraphA());
    final SequenceNumberRange range = shards.get(0).sequenceNumberRange().toBuilder().endingSequenceNumber(null).build();
    final Shard shard = shards.get(3).toBuilder().sequenceNumberRange(range).build();
    shards.remove(3);
    shards.add(3, shard);
    when(shardDetector.listShards()).thenReturn(shards);
    try {
      hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, INITIAL_POSITION_TRIM_HORIZON, cleanupLeasesOfCompletedShards, false, SCOPE);
    }  finally {
      verify(shardDetector).listShards();
      verify(dynamoDBLeaseRefresher, never()).listLeases();
    }
  }

  /**
     * Test checkAndCreateLeasesForNewShards() when a parent is open and children of open parents are being ignored.
     */
  @Test public void testCheckAndCreateLeasesForNewShardsWhenParentIsOpenAndIgnoringInconsistentChildren() throws Exception {
    final List<Shard> shards = new ArrayList<>(constructShardListForGraphA());
    final Shard shard = shards.get(5);
    assertThat(shard.shardId(), equalTo("shardId-5"));
    shards.remove(5);
    shards.add(5, shard.toBuilder().sequenceNumberRange(shard.sequenceNumberRange().toBuilder().endingSequenceNumber(null).build()).build());
    final ArgumentCaptor<Lease> leaseCaptor = ArgumentCaptor.forClass(Lease.class);
    when(shardDetector.listShards()).thenReturn(shards);
    when(dynamoDBLeaseRefresher.listLeases()).thenReturn(Collections.emptyList());
    when(dynamoDBLeaseRefresher.createLeaseIfNotExists(leaseCaptor.capture())).thenReturn(true);
    hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, INITIAL_POSITION_LATEST, cleanupLeasesOfCompletedShards, true, SCOPE);
    final List<Lease> leases = leaseCaptor.getAllValues();
    final Set<String> leaseKeys = leases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final Set<ExtendedSequenceNumber> leaseSequenceNumbers = leases.stream().map(Lease::checkpoint).collect(Collectors.toSet());
    final Set<String> expectedShardIds = new HashSet<>(Arrays.asList("shardId-4", "shardId-5", "shardId-8"));
    assertThat(leaseKeys.size(), equalTo(expectedShardIds.size()));
    assertThat(leaseKeys, equalTo(expectedShardIds));
    assertThat(leaseSequenceNumbers.size(), equalTo(1));
    leaseSequenceNumbers.forEach((seq) -> assertThat(seq, equalTo(ExtendedSequenceNumber.LATEST)));
    verify(shardDetector).listShards();
    verify(dynamoDBLeaseRefresher, times(expectedShardIds.size())).createLeaseIfNotExists(any(Lease.class));
    verify(dynamoDBLeaseRefresher, never()).deleteLease(any(Lease.class));
  }

  @Test public void testCheckAndCreateLeasesForNewShardsAtTrimHorizonAndClosedShard() throws Exception {
    testCheckAndCreateLeasesForNewShardsAndClosedShard(ExtendedSequenceNumber.TRIM_HORIZON, INITIAL_POSITION_TRIM_HORIZON);
  }

  @Test public void testCheckAndCreateLeasesForNewShardsAtTimestampAndClosedShard() throws Exception {
    testCheckAndCreateLeasesForNewShardsAndClosedShard(ExtendedSequenceNumber.AT_TIMESTAMP, INITIAL_POSITION_AT_TIMESTAMP);
  }

  private void testCheckAndCreateLeasesForNewShardsAndClosedShard(final ExtendedSequenceNumber sequenceNumber, final InitialPositionInStreamExtended position) throws Exception {
    final String shardIdPrefix = "shardId-%d";
    final List<Shard> shards = constructShardListForGraphA();
    final List<Lease> leases = createLeasesFromShards(shards, sequenceNumber, LEASE_OWNER);
    leases.stream().filter((lease) -> String.format(shardIdPrefix, 0).equals(lease.leaseKey())).findFirst().ifPresent((lease) -> lease.checkpoint(ExtendedSequenceNumber.SHARD_END));
    leases.stream().filter((lease) -> String.format(shardIdPrefix, 6).equals(lease.leaseKey())).findFirst().ifPresent((lease) -> lease.checkpoint(new ExtendedSequenceNumber("1")));
    final ArgumentCaptor<Lease> leaseCreateCaptor = ArgumentCaptor.forClass(Lease.class);
    final ArgumentCaptor<Lease> leaseDeleteCaptor = ArgumentCaptor.forClass(Lease.class);
    when(shardDetector.listShards()).thenReturn(shards);
    when(dynamoDBLeaseRefresher.listLeases()).thenReturn(Collections.emptyList()).thenReturn(leases);
    when(dynamoDBLeaseRefresher.createLeaseIfNotExists(leaseCreateCaptor.capture())).thenReturn(true);
    doNothing().when(dynamoDBLeaseRefresher).deleteLease(leaseDeleteCaptor.capture());
    hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, position, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
    final Set<Lease> createLeases = new HashSet<>(leaseCreateCaptor.getAllValues());
    final Set<Lease> expectedCreateLeases = new HashSet<>(createLeasesFromShards(shards, sequenceNumber, null));
    assertThat(createLeases, equalTo(expectedCreateLeases));
    verify(shardDetector, times(1)).listShards();
    verify(dynamoDBLeaseRefresher, times(1)).listLeases();
    verify(dynamoDBLeaseRefresher, times(expectedCreateLeases.size())).createLeaseIfNotExists(any(Lease.class));
    verify(dynamoDBLeaseRefresher, never()).deleteLease(any(Lease.class));
    hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, position, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
    final List<Lease> deleteLeases = leaseDeleteCaptor.getAllValues();
    final Set<String> shardIds = deleteLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final Set<ExtendedSequenceNumber> sequenceNumbers = deleteLeases.stream().map(Lease::checkpoint).collect(Collectors.toSet());
    final Set<String> expectedShardIds = new HashSet<>(Collections.singletonList(String.format(shardIdPrefix, 0)));
    final Set<ExtendedSequenceNumber> expectedSequenceNumbers = new HashSet<>(Collections.singletonList(ExtendedSequenceNumber.SHARD_END));
    assertThat(deleteLeases.size(), equalTo(1));
    assertThat(shardIds, equalTo(expectedShardIds));
    assertThat(sequenceNumbers, equalTo(expectedSequenceNumbers));
    verify(shardDetector, times(2)).listShards();
    verify(dynamoDBLeaseRefresher, times(expectedCreateLeases.size())).createLeaseIfNotExists(any(Lease.class));
    verify(dynamoDBLeaseRefresher, times(2)).listLeases();
    verify(dynamoDBLeaseRefresher, times(1)).deleteLease(any(Lease.class));
  }

  @Test(expected = DependencyException.class) public void testCheckAndCreateLeasesForNewShardsAtTrimHorizonAndClosedShardWithDeleteLeaseExceptions() throws Exception {
    testCheckAndCreateLeasesForNewShardsAndClosedShardWithDeleteLeaseExceptions(ExtendedSequenceNumber.TRIM_HORIZON, INITIAL_POSITION_TRIM_HORIZON);
  }

  @Test(expected = DependencyException.class) public void testCheckAndCreateLeasesForNewShardsAtTimestampAndClosedShardWithDeleteLeaseExceptions() throws Exception {
    testCheckAndCreateLeasesForNewShardsAndClosedShardWithDeleteLeaseExceptions(ExtendedSequenceNumber.AT_TIMESTAMP, INITIAL_POSITION_AT_TIMESTAMP);
  }

  private void testCheckAndCreateLeasesForNewShardsAndClosedShardWithDeleteLeaseExceptions(final ExtendedSequenceNumber sequenceNumber, final InitialPositionInStreamExtended position) throws Exception {
    final String shardIdPrefix = "shardId-%d";
    final List<Shard> shards = constructShardListForGraphA();
    final List<Lease> leases = createLeasesFromShards(shards, sequenceNumber, LEASE_OWNER);
    leases.stream().filter((lease) -> String.format(shardIdPrefix, 0).equals(lease.leaseKey())).findFirst().ifPresent((lease) -> lease.checkpoint(ExtendedSequenceNumber.SHARD_END));
    leases.stream().filter((lease) -> String.format(shardIdPrefix, 6).equals(lease.leaseKey())).findFirst().ifPresent((lease) -> lease.checkpoint(new ExtendedSequenceNumber("1")));
    final ArgumentCaptor<Lease> leaseCreateCaptor = ArgumentCaptor.forClass(Lease.class);
    final ArgumentCaptor<Lease> leaseDeleteCaptor = ArgumentCaptor.forClass(Lease.class);
    when(shardDetector.listShards()).thenReturn(shards);
    when(dynamoDBLeaseRefresher.listLeases()).thenReturn(Collections.emptyList()).thenReturn(leases);
    when(dynamoDBLeaseRefresher.createLeaseIfNotExists(leaseCreateCaptor.capture())).thenReturn(true);
    doThrow(new DependencyException(new Throwable("Throw for DeleteLease"))).doNothing().when(dynamoDBLeaseRefresher).deleteLease(leaseDeleteCaptor.capture());
    hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, position, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
    final Set<Lease> createLeases = new HashSet<>(leaseCreateCaptor.getAllValues());
    final Set<Lease> expectedCreateLeases = new HashSet<>(createLeasesFromShards(shards, sequenceNumber, null));
    assertThat(createLeases, equalTo(expectedCreateLeases));
    verify(shardDetector, times(1)).listShards();
    verify(dynamoDBLeaseRefresher, times(1)).listLeases();
    verify(dynamoDBLeaseRefresher, times(expectedCreateLeases.size())).createLeaseIfNotExists(any(Lease.class));
    verify(dynamoDBLeaseRefresher, never()).deleteLease(any(Lease.class));
    try {
      hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, position, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
    }  finally {
      List<Lease> deleteLeases = leaseDeleteCaptor.getAllValues();
      Set<String> shardIds = deleteLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
      Set<ExtendedSequenceNumber> sequenceNumbers = deleteLeases.stream().map(Lease::checkpoint).collect(Collectors.toSet());
      final Set<String> expectedShardIds = new HashSet<>(Collections.singletonList(String.format(shardIdPrefix, 0)));
      final Set<ExtendedSequenceNumber> expectedSequenceNumbers = new HashSet<>(Collections.singletonList(ExtendedSequenceNumber.SHARD_END));
      assertThat(deleteLeases.size(), equalTo(1));
      assertThat(shardIds, equalTo(expectedShardIds));
      assertThat(sequenceNumbers, equalTo(expectedSequenceNumbers));
      verify(shardDetector, times(2)).listShards();
      verify(dynamoDBLeaseRefresher, times(expectedCreateLeases.size())).createLeaseIfNotExists(any(Lease.class));
      verify(dynamoDBLeaseRefresher, times(2)).listLeases();
      verify(dynamoDBLeaseRefresher, times(1)).deleteLease(any(Lease.class));
      hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, position, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
      deleteLeases = leaseDeleteCaptor.getAllValues();
      shardIds = deleteLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
      sequenceNumbers = deleteLeases.stream().map(Lease::checkpoint).collect(Collectors.toSet());
      assertThat(deleteLeases.size(), equalTo(2));
      assertThat(shardIds, equalTo(expectedShardIds));
      assertThat(sequenceNumbers, equalTo(expectedSequenceNumbers));
      verify(shardDetector, times(3)).listShards();
      verify(dynamoDBLeaseRefresher, times(expectedCreateLeases.size())).createLeaseIfNotExists(any(Lease.class));
      verify(dynamoDBLeaseRefresher, times(3)).listLeases();
      verify(dynamoDBLeaseRefresher, times(2)).deleteLease(any(Lease.class));
    }
  }

  @Test(expected = DependencyException.class) public void testCheckAndCreateLeasesForNewShardsAtTrimHorizonAndClosedShardWithListLeasesExceptions() throws Exception {
    testCheckAndCreateLeasesForNewShardsAndClosedShardWithListLeasesExceptions(ExtendedSequenceNumber.TRIM_HORIZON, INITIAL_POSITION_TRIM_HORIZON);
  }

  @Test(expected = DependencyException.class) public void testCheckAndCreateLeasesForNewShardsAtTimestampAndClosedShardWithListLeasesExceptions() throws Exception {
    testCheckAndCreateLeasesForNewShardsAndClosedShardWithListLeasesExceptions(ExtendedSequenceNumber.AT_TIMESTAMP, INITIAL_POSITION_AT_TIMESTAMP);
  }

  private void testCheckAndCreateLeasesForNewShardsAndClosedShardWithListLeasesExceptions(final ExtendedSequenceNumber sequenceNumber, final InitialPositionInStreamExtended position) throws Exception {
    final String shardIdPrefix = "shardId-%d";
    final List<Shard> shards = constructShardListForGraphA();
    final List<Lease> leases = createLeasesFromShards(shards, sequenceNumber, LEASE_OWNER);
    leases.stream().filter((lease) -> String.format(shardIdPrefix, 0).equals(lease.leaseKey())).findFirst().ifPresent((lease) -> lease.checkpoint(ExtendedSequenceNumber.SHARD_END));
    leases.stream().filter((lease) -> String.format(shardIdPrefix, 6).equals(lease.leaseKey())).findFirst().ifPresent((lease) -> lease.checkpoint(new ExtendedSequenceNumber("1")));
    final ArgumentCaptor<Lease> leaseCreateCaptor = ArgumentCaptor.forClass(Lease.class);
    final ArgumentCaptor<Lease> leaseDeleteCaptor = ArgumentCaptor.forClass(Lease.class);
    when(shardDetector.listShards()).thenReturn(shards);
    when(dynamoDBLeaseRefresher.listLeases()).thenThrow(new DependencyException(new Throwable("Throw for ListLeases"))).thenReturn(Collections.emptyList()).thenReturn(leases);
    when(dynamoDBLeaseRefresher.createLeaseIfNotExists(leaseCreateCaptor.capture())).thenReturn(true);
    doNothing().when(dynamoDBLeaseRefresher).deleteLease(leaseDeleteCaptor.capture());
    try {
      hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, position, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
    }  finally {
      verify(shardDetector, times(1)).listShards();
      verify(dynamoDBLeaseRefresher, times(1)).listLeases();
      verify(dynamoDBLeaseRefresher, never()).createLeaseIfNotExists(any(Lease.class));
      verify(dynamoDBLeaseRefresher, never()).deleteLease(any(Lease.class));
      hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, position, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
      final Set<Lease> createLeases = new HashSet<>(leaseCreateCaptor.getAllValues());
      final Set<Lease> expectedCreateLeases = new HashSet<>(createLeasesFromShards(shards, sequenceNumber, null));
      assertThat(createLeases, equalTo(expectedCreateLeases));
      verify(shardDetector, times(2)).listShards();
      verify(dynamoDBLeaseRefresher, times(2)).listLeases();
      verify(dynamoDBLeaseRefresher, times(expectedCreateLeases.size())).createLeaseIfNotExists(any(Lease.class));
      verify(dynamoDBLeaseRefresher, never()).deleteLease(any(Lease.class));
      hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, position, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
      final List<Lease> deleteLeases = leaseDeleteCaptor.getAllValues();
      final Set<String> shardIds = deleteLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
      final Set<ExtendedSequenceNumber> sequenceNumbers = deleteLeases.stream().map(Lease::checkpoint).collect(Collectors.toSet());
      final Set<String> expectedShardIds = new HashSet<>(Collections.singletonList(String.format(shardIdPrefix, 0)));
      final Set<ExtendedSequenceNumber> expectedSequenceNumbers = new HashSet<>(Collections.singletonList(ExtendedSequenceNumber.SHARD_END));
      assertThat(deleteLeases.size(), equalTo(1));
      assertThat(shardIds, equalTo(expectedShardIds));
      assertThat(sequenceNumbers, equalTo(expectedSequenceNumbers));
      verify(shardDetector, times(3)).listShards();
      verify(dynamoDBLeaseRefresher, times(expectedCreateLeases.size())).createLeaseIfNotExists(any(Lease.class));
      verify(dynamoDBLeaseRefresher, times(3)).listLeases();
      verify(dynamoDBLeaseRefresher, times(1)).deleteLease(any(Lease.class));
    }
  }

  @Test(expected = DependencyException.class) public void testCheckAndCreateLeasesForNewShardsAtTrimHorizonAndClosedShardWithCreateLeaseExceptions() throws Exception {
    testCheckAndCreateLeasesForNewShardsAndClosedShardWithCreateLeaseExceptions(ExtendedSequenceNumber.TRIM_HORIZON, INITIAL_POSITION_TRIM_HORIZON);
  }

  @Test(expected = DependencyException.class) public void testCheckAndCreateLeasesForNewShardsAtTimestampAndClosedShardWithCreateLeaseExceptions() throws Exception {
    testCheckAndCreateLeasesForNewShardsAndClosedShardWithCreateLeaseExceptions(ExtendedSequenceNumber.AT_TIMESTAMP, INITIAL_POSITION_AT_TIMESTAMP);
  }

  private void testCheckAndCreateLeasesForNewShardsAndClosedShardWithCreateLeaseExceptions(final ExtendedSequenceNumber sequenceNumber, final InitialPositionInStreamExtended position) throws Exception {
    final String shardIdPrefix = "shardId-%d";
    final List<Shard> shards = constructShardListForGraphA();
    final List<Lease> leases = createLeasesFromShards(shards, sequenceNumber, LEASE_OWNER);
    leases.stream().filter((lease) -> String.format(shardIdPrefix, 0).equals(lease.leaseKey())).findFirst().ifPresent((lease) -> lease.checkpoint(ExtendedSequenceNumber.SHARD_END));
    leases.stream().filter((lease) -> String.format(shardIdPrefix, 6).equals(lease.leaseKey())).findFirst().ifPresent((lease) -> lease.checkpoint(new ExtendedSequenceNumber("1")));
    final ArgumentCaptor<Lease> leaseCreateCaptor = ArgumentCaptor.forClass(Lease.class);
    final ArgumentCaptor<Lease> leaseDeleteCaptor = ArgumentCaptor.forClass(Lease.class);
    when(shardDetector.listShards()).thenReturn(shards);
    when(dynamoDBLeaseRefresher.listLeases()).thenReturn(Collections.emptyList()).thenReturn(Collections.emptyList()).thenReturn(leases);
    when(dynamoDBLeaseRefresher.createLeaseIfNotExists(leaseCreateCaptor.capture())).thenThrow(new DependencyException(new Throwable("Throw for CreateLease"))).thenReturn(true);
    doNothing().when(dynamoDBLeaseRefresher).deleteLease(leaseDeleteCaptor.capture());
    try {
      hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, position, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
    }  finally {
      verify(shardDetector, times(1)).listShards();
      verify(dynamoDBLeaseRefresher, times(1)).listLeases();
      verify(dynamoDBLeaseRefresher, times(1)).createLeaseIfNotExists(any(Lease.class));
      verify(dynamoDBLeaseRefresher, never()).deleteLease(any(Lease.class));
      hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, position, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
      final Set<Lease> createLeases = new HashSet<>(leaseCreateCaptor.getAllValues());
      final Set<Lease> expectedCreateLeases = new HashSet<>(createLeasesFromShards(shards, sequenceNumber, null));
      assertThat(createLeases, equalTo(expectedCreateLeases));
      verify(shardDetector, times(2)).listShards();
      verify(dynamoDBLeaseRefresher, times(2)).listLeases();
      verify(dynamoDBLeaseRefresher, times(1 + expectedCreateLeases.size())).createLeaseIfNotExists(any(Lease.class));
      verify(dynamoDBLeaseRefresher, never()).deleteLease(any(Lease.class));
      hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, position, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
      final List<Lease> deleteLeases = leaseDeleteCaptor.getAllValues();
      final Set<String> shardIds = deleteLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
      final Set<ExtendedSequenceNumber> sequenceNumbers = deleteLeases.stream().map(Lease::checkpoint).collect(Collectors.toSet());
      final Set<String> expectedShardIds = new HashSet<>(Collections.singletonList(String.format(shardIdPrefix, 0)));
      final Set<ExtendedSequenceNumber> expectedSequenceNumbers = new HashSet<>(Collections.singletonList(ExtendedSequenceNumber.SHARD_END));
      assertThat(deleteLeases.size(), equalTo(1));
      assertThat(shardIds, equalTo(expectedShardIds));
      assertThat(sequenceNumbers, equalTo(expectedSequenceNumbers));
      verify(shardDetector, times(3)).listShards();
      verify(dynamoDBLeaseRefresher, times(1 + expectedCreateLeases.size())).createLeaseIfNotExists(any(Lease.class));
      verify(dynamoDBLeaseRefresher, times(3)).listLeases();
      verify(dynamoDBLeaseRefresher, times(1)).deleteLease(any(Lease.class));
    }
  }

  private Lease createLeaseFromShard(final Shard shard, final ExtendedSequenceNumber checkpoint, final String leaseOwner) {
    return createLeasesFromShards(Collections.singletonList(shard), checkpoint, leaseOwner).get(0);
  }

  private List<Lease> createLeasesFromShards(final List<Shard> shards, final ExtendedSequenceNumber checkpoint, final String leaseOwner) {
    return shards.stream().map((shard) -> {
      final Set<String> parentShardIds = new HashSet<>();
      if (StringUtils.isNotEmpty(shard.parentShardId())) {
        parentShardIds.add(shard.parentShardId());
      }
      if (StringUtils.isNotEmpty(shard.adjacentParentShardId())) {
        parentShardIds.add(shard.adjacentParentShardId());
      }
      return new Lease(shard.shardId(), leaseOwner, 0L, UUID.randomUUID(), 0L, checkpoint, null, 0L, parentShardIds);
    }).collect(Collectors.toList());
  }

  @Test public void testCleanUpGarbageLeaseForNonExistentShard() throws Exception {
    final List<Shard> shards = constructShardListForGraphA();
    final String garbageShardId = "shardId-garbage-001";
    final Shard garbageShard = ShardObjectHelper.newShard(garbageShardId, null, null, ShardObjectHelper.newSequenceNumberRange("101", null));
    final Lease garbageLease = createLeaseFromShard(garbageShard, new ExtendedSequenceNumber("99"), LEASE_OWNER);
    final List<Lease> leases = new ArrayList<>(createLeasesFromShards(shards, ExtendedSequenceNumber.TRIM_HORIZON, LEASE_OWNER));
    leases.add(garbageLease);
    final ArgumentCaptor<Lease> leaseCaptor = ArgumentCaptor.forClass(Lease.class);
    when(shardDetector.listShards()).thenReturn(shards);
    when(dynamoDBLeaseRefresher.listLeases()).thenReturn(leases);
    doNothing().when(dynamoDBLeaseRefresher).deleteLease(leaseCaptor.capture());
    hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, INITIAL_POSITION_TRIM_HORIZON, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards, SCOPE);
    assertThat(leaseCaptor.getAllValues().size(), equalTo(1));
    assertThat(leaseCaptor.getValue(), equalTo(garbageLease));
    verify(shardDetector, times(2)).listShards();
    verify(dynamoDBLeaseRefresher).listLeases();
    verify(dynamoDBLeaseRefresher).deleteLease(any(Lease.class));
    verify(dynamoDBLeaseRefresher, never()).createLeaseIfNotExists(any(Lease.class));
  }

  private void testCheckAndCreateLeasesForShardsIfMissing(InitialPositionInStreamExtended initialPosition) throws Exception {
    final String shardId0 = "shardId-0";
    final String shardId1 = "shardId-1";
    final SequenceNumberRange sequenceRange = ShardObjectHelper.newSequenceNumberRange("342980", null);
    final List<Shard> shards = Arrays.asList(ShardObjectHelper.newShard(shardId0, null, null, sequenceRange), ShardObjectHelper.newShard(shardId1, null, null, sequenceRange));
    testCheckAndCreateLeaseForShardsIfMissing(shards, initialPosition);
  }

  private void testCheckAndCreateLeaseForShardsIfMissing(final List<Shard> shards, final InitialPositionInStreamExtended initialPosition) throws Exception {
    final ArgumentCaptor<Lease> leaseCaptor = ArgumentCaptor.forClass(Lease.class);
    when(shardDetector.listShards()).thenReturn(shards);
    when(dynamoDBLeaseRefresher.listLeases()).thenReturn(Collections.emptyList());
    when(dynamoDBLeaseRefresher.createLeaseIfNotExists(leaseCaptor.capture())).thenReturn(true);
    hierarchicalShardSyncer.checkAndCreateLeaseForNewShards(shardDetector, dynamoDBLeaseRefresher, initialPosition, cleanupLeasesOfCompletedShards, false, SCOPE);
    final List<Lease> leases = leaseCaptor.getAllValues();
    final Set<String> leaseKeys = leases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final Set<ExtendedSequenceNumber> leaseSequenceNumbers = leases.stream().map(Lease::checkpoint).collect(Collectors.toSet());
    final Set<String> expectedLeaseKeys = shards.stream().map(Shard::shardId).collect(Collectors.toSet());
    final Set<ExtendedSequenceNumber> expectedSequenceNumbers = new HashSet<>(Collections.singletonList(new ExtendedSequenceNumber(initialPosition.getInitialPositionInStream().name())));
    assertThat(leases.size(), equalTo(shards.size()));
    assertThat(leaseKeys, equalTo(expectedLeaseKeys));
    assertThat(leaseSequenceNumbers, equalTo(expectedSequenceNumbers));
    verify(shardDetector).listShards();
    verify(dynamoDBLeaseRefresher, times(shards.size())).createLeaseIfNotExists(any(Lease.class));
    verify(dynamoDBLeaseRefresher, never()).deleteLease(any(Lease.class));
  }

  @Test public void testDetermineNewLeasesToCreateStartingPosition() {
    final String shardId0 = "shardId-0";
    final String shardId1 = "shardId-1";
    final List<Lease> currentLeases = new ArrayList<>();
    final SequenceNumberRange sequenceRange = ShardObjectHelper.newSequenceNumberRange("342980", null);
    final List<Shard> shards = Arrays.asList(ShardObjectHelper.newShard(shardId0, null, null, sequenceRange), ShardObjectHelper.newShard(shardId1, null, null, sequenceRange));
    final Set<InitialPositionInStreamExtended> initialPositions = new HashSet<>(Arrays.asList(INITIAL_POSITION_LATEST, INITIAL_POSITION_TRIM_HORIZON));
    final Set<String> expectedLeaseShardIds = new HashSet<>(Arrays.asList(shardId0, shardId1));
    for (InitialPositionInStreamExtended initialPosition : initialPositions) {
      final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, initialPosition);
      assertThat(newLeases.size(), equalTo(2));
      for (Lease lease : newLeases) {
        assertThat(expectedLeaseShardIds.contains(lease.leaseKey()), equalTo(true));
        assertThat(lease.checkpoint(), equalTo(new ExtendedSequenceNumber(initialPosition.getInitialPositionInStream().toString())));
      }
    }
  }

  @Test public void testDetermineNewLeasesToCreateIgnoreClosedShard() {
    final String lastShardId = "shardId-1";
    final List<Lease> currentLeases = new ArrayList<>();
    final List<Shard> shards = Arrays.asList(ShardObjectHelper.newShard("shardId-0", null, null, ShardObjectHelper.newSequenceNumberRange("303", "404")), ShardObjectHelper.newShard(lastShardId, null, null, ShardObjectHelper.newSequenceNumberRange("405", null)));
    final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, INITIAL_POSITION_LATEST);
    assertThat(newLeases.size(), equalTo(1));
    assertThat(newLeases.get(0).leaseKey(), equalTo(lastShardId));
  }

  @Test public void testDetermineNewLeasesToCreateSplitMergeLatest1() {
    final List<Shard> shards = constructShardListForGraphA();
    final List<Lease> currentLeases = Arrays.asList(newLease("shardId-3"), newLease("shardId-4"), newLease("shardId-5"));
    final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, INITIAL_POSITION_LATEST);
    final Map<String, ExtendedSequenceNumber> expectedShardIdCheckpointMap = new HashMap<>();
    expectedShardIdCheckpointMap.put("shardId-8", ExtendedSequenceNumber.TRIM_HORIZON);
    expectedShardIdCheckpointMap.put("shardId-9", ExtendedSequenceNumber.TRIM_HORIZON);
    expectedShardIdCheckpointMap.put("shardId-10", ExtendedSequenceNumber.TRIM_HORIZON);
    expectedShardIdCheckpointMap.put("shardId-6", ExtendedSequenceNumber.LATEST);
    expectedShardIdCheckpointMap.put("shardId-2", ExtendedSequenceNumber.LATEST);
    expectedShardIdCheckpointMap.put("shardId-7", ExtendedSequenceNumber.TRIM_HORIZON);
    assertThat(newLeases.size(), equalTo(expectedShardIdCheckpointMap.size()));
    for (Lease lease : newLeases) {
      assertThat("Unexpected lease: " + lease, expectedShardIdCheckpointMap.containsKey(lease.leaseKey()), equalTo(true));
      assertThat(lease.checkpoint(), equalTo(expectedShardIdCheckpointMap.get(lease.leaseKey())));
    }
  }

  @Test public void testDetermineNewLeasesToCreateSplitMergeLatest2() {
    final List<Shard> shards = constructShardListForGraphA();
    final List<Lease> currentLeases = Arrays.asList(newLease("shardId-4"), newLease("shardId-5"), newLease("shardId-7"));
    final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, INITIAL_POSITION_LATEST);
    final Map<String, ExtendedSequenceNumber> expectedShardIdCheckpointMap = new HashMap<>();
    expectedShardIdCheckpointMap.put("shardId-8", ExtendedSequenceNumber.TRIM_HORIZON);
    expectedShardIdCheckpointMap.put("shardId-9", ExtendedSequenceNumber.TRIM_HORIZON);
    expectedShardIdCheckpointMap.put("shardId-10", ExtendedSequenceNumber.TRIM_HORIZON);
    expectedShardIdCheckpointMap.put("shardId-6", ExtendedSequenceNumber.LATEST);
    assertThat(newLeases.size(), equalTo(expectedShardIdCheckpointMap.size()));
    for (Lease lease : newLeases) {
      assertThat("Unexpected lease: " + lease, expectedShardIdCheckpointMap.containsKey(lease.leaseKey()), equalTo(true));
      assertThat(lease.checkpoint(), equalTo(expectedShardIdCheckpointMap.get(lease.leaseKey())));
    }
  }

  @Test public void testDetermineNewLeasesToCreateSplitMergeHorizon1() {
    final List<Shard> shards = constructShardListForGraphA();
    final List<Lease> currentLeases = Arrays.asList(newLease("shardId-3"), newLease("shardId-4"), newLease("shardId-5"));
    final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, INITIAL_POSITION_TRIM_HORIZON);
    final Set<String> leaseKeys = newLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final List<ExtendedSequenceNumber> checkpoints = newLeases.stream().map(Lease::checkpoint).collect(Collectors.toList());
    final Set<ExtendedSequenceNumber> checkpoint = new HashSet<>(checkpoints);
    final Set<String> expectedLeaseKeys = new HashSet<>(Arrays.asList("shardId-0", "shardId-1", "shardId-2", "shardId-6", "shardId-7", "shardId-8", "shardId-9", "shardId-10"));
    final Set<ExtendedSequenceNumber> expectedCheckpoint = new HashSet<>(Collections.singletonList(ExtendedSequenceNumber.TRIM_HORIZON));
    assertThat(newLeases.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(checkpoints.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(leaseKeys, equalTo(expectedLeaseKeys));
    assertThat(checkpoint, equalTo(expectedCheckpoint));
  }

  @Test public void testDetermineNewLeasesToCreateSplitMergeHorizon2() {
    final List<Shard> shards = constructShardListForGraphA();
    final List<Lease> currentLeases = Arrays.asList(newLease("shardId-4"), newLease("shardId-5"), newLease("shardId-7"));
    final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, INITIAL_POSITION_TRIM_HORIZON);
    final Set<String> leaseKeys = newLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final List<ExtendedSequenceNumber> checkpoints = newLeases.stream().map(Lease::checkpoint).collect(Collectors.toList());
    final Set<ExtendedSequenceNumber> checkpoint = new HashSet<>(checkpoints);
    final Set<String> expectedLeaseKeys = new HashSet<>(Arrays.asList("shardId-8", "shardId-9", "shardId-10", "shardId-6", "shardId-0", "shardId-1"));
    final Set<ExtendedSequenceNumber> expectedCheckpoint = new HashSet<>(Collections.singletonList(ExtendedSequenceNumber.TRIM_HORIZON));
    assertThat(newLeases.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(checkpoints.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(leaseKeys, equalTo(expectedLeaseKeys));
    assertThat(checkpoint, equalTo(expectedCheckpoint));
  }

  @Test public void testDetermineNewLeasesToCreateGraphBNoInitialLeasesTrim() {
    final List<Shard> shards = constructShardListForGraphB();
    final List<Lease> currentLeases = new ArrayList<>();
    final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, INITIAL_POSITION_TRIM_HORIZON);
    final Set<String> leaseKeys = newLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final List<ExtendedSequenceNumber> checkpoints = newLeases.stream().map(Lease::checkpoint).collect(Collectors.toList());
    final Set<ExtendedSequenceNumber> checkpoint = new HashSet<>(checkpoints);
    final Set<ExtendedSequenceNumber> expectedCheckpoint = new HashSet<>(Collections.singletonList(ExtendedSequenceNumber.TRIM_HORIZON));
    final Set<String> expectedLeaseKeys = IntStream.range(0, 11).mapToObj((id) -> String.format("shardId-%d", id)).collect(Collectors.toSet());
    assertThat(newLeases.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(checkpoints.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(leaseKeys, equalTo(expectedLeaseKeys));
    assertThat(checkpoint, equalTo(expectedCheckpoint));
  }

  @Test public void testDetermineNewLeasesToCreateSplitMergeAtTimestamp1() {
    final List<Shard> shards = constructShardListForGraphA();
    final List<Lease> currentLeases = Arrays.asList(newLease("shardId-3"), newLease("shardId-4"), newLease("shardId-5"));
    final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, INITIAL_POSITION_AT_TIMESTAMP);
    final Set<String> leaseKeys = newLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final List<ExtendedSequenceNumber> checkpoints = newLeases.stream().map(Lease::checkpoint).collect(Collectors.toList());
    final Set<ExtendedSequenceNumber> checkpoint = new HashSet<>(checkpoints);
    final Set<String> expectedLeaseKeys = new HashSet<>(Arrays.asList("shardId-0", "shardId-1", "shardId-2", "shardId-6", "shardId-7", "shardId-8", "shardId-9", "shardId-10"));
    final Set<ExtendedSequenceNumber> expectedCheckpoint = new HashSet<>(Collections.singletonList(ExtendedSequenceNumber.AT_TIMESTAMP));
    assertThat(newLeases.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(checkpoints.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(leaseKeys, equalTo(expectedLeaseKeys));
    assertThat(checkpoint, equalTo(expectedCheckpoint));
  }

  @Test public void testDetermineNewLeasesToCreateSplitMergeAtTimestamp2() {
    final List<Shard> shards = constructShardListForGraphA();
    final List<Lease> currentLeases = Arrays.asList(newLease("shardId-4"), newLease("shardId-5"), newLease("shardId-7"));
    final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, INITIAL_POSITION_AT_TIMESTAMP);
    final Set<String> leaseKeys = newLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final List<ExtendedSequenceNumber> checkpoints = newLeases.stream().map(Lease::checkpoint).collect(Collectors.toList());
    final Set<ExtendedSequenceNumber> checkpoint = new HashSet<>(checkpoints);
    final Set<String> expectedLeaseKeys = new HashSet<>(Arrays.asList("shardId-0", "shardId-1", "shardId-6", "shardId-8", "shardId-9", "shardId-10"));
    final Set<ExtendedSequenceNumber> expectedCheckpoint = new HashSet<>(Collections.singletonList(ExtendedSequenceNumber.AT_TIMESTAMP));
    assertThat(newLeases.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(checkpoints.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(leaseKeys, equalTo(expectedLeaseKeys));
    assertThat(checkpoint, equalTo(expectedCheckpoint));
  }

  /**
     * Test CheckIfDescendantAndAddNewLeasesForAncestors (initial position AT_TIMESTAMP) For shard graph B (see the
     * construct method doc for structure). Current leases: empty set
     */
  @Test public void testDetermineNewLeasesToCreateGraphBNoInitialLeasesAtTimestamp() {
    final List<Shard> shards = constructShardListForGraphB();
    final List<Lease> currentLeases = new ArrayList<>();
    final List<Lease> newLeases = HierarchicalShardSyncer.determineNewLeasesToCreate(shards, currentLeases, INITIAL_POSITION_AT_TIMESTAMP);
    final Set<String> leaseKeys = newLeases.stream().map(Lease::leaseKey).collect(Collectors.toSet());
    final List<ExtendedSequenceNumber> checkpoints = newLeases.stream().map(Lease::checkpoint).collect(Collectors.toList());
    final Set<ExtendedSequenceNumber> checkpoint = new HashSet<>(checkpoints);
    final Set<String> expectedLeaseKeys = IntStream.range(0, shards.size()).mapToObj((id) -> String.format("shardId-%d", id)).collect(Collectors.toSet());
    final Set<ExtendedSequenceNumber> expectedCheckpoint = new HashSet<>(Collections.singletonList(ExtendedSequenceNumber.AT_TIMESTAMP));
    assertThat(newLeases.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(checkpoints.size(), equalTo(expectedLeaseKeys.size()));
    assertThat(leaseKeys, equalTo(expectedLeaseKeys));
    assertThat(checkpoint, equalTo(expectedCheckpoint));
  }

  private List<Shard> constructShardListForGraphA() {
    final SequenceNumberRange range0 = ShardObjectHelper.newSequenceNumberRange("11", "102");
    final SequenceNumberRange range1 = ShardObjectHelper.newSequenceNumberRange("11", null);
    final SequenceNumberRange range2 = ShardObjectHelper.newSequenceNumberRange("11", "205");
    final SequenceNumberRange range3 = ShardObjectHelper.newSequenceNumberRange("103", "205");
    final SequenceNumberRange range4 = ShardObjectHelper.newSequenceNumberRange("206", null);
    return Arrays.asList(ShardObjectHelper.newShard("shardId-0", null, null, range0, ShardObjectHelper.newHashKeyRange("0", "99")), ShardObjectHelper.newShard("shardId-1", null, null, range0, ShardObjectHelper.newHashKeyRange("100", "199")), ShardObjectHelper.newShard("shardId-2", null, null, range0, ShardObjectHelper.newHashKeyRange("200", "299")), ShardObjectHelper.newShard("shardId-3", null, null, range0, ShardObjectHelper.newHashKeyRange("300", "399")), ShardObjectHelper.newShard("shardId-4", null, null, range1, ShardObjectHelper.newHashKeyRange("400", "499")), ShardObjectHelper.newShard("shardId-5", null, null, range2, ShardObjectHelper.newHashKeyRange("500", ShardObjectHelper.MAX_HASH_KEY)), ShardObjectHelper.newShard("shardId-6", "shardId-0", "shardId-1", range3, ShardObjectHelper.newHashKeyRange("0", "199")), ShardObjectHelper.newShard("shardId-7", "shardId-2", "shardId-3", range3, ShardObjectHelper.newHashKeyRange("200", "399")), ShardObjectHelper.newShard("shardId-8", "shardId-6", "shardId-7", range4, ShardObjectHelper.newHashKeyRange("0", "399")), ShardObjectHelper.newShard("shardId-9", "shardId-5", null, range4, ShardObjectHelper.newHashKeyRange("500", "799")), ShardObjectHelper.newShard("shardId-10", null, "shardId-5", range4, ShardObjectHelper.newHashKeyRange("800", ShardObjectHelper.MAX_HASH_KEY)));
  }

  private List<Shard> constructShardListForGraphB() {
    final SequenceNumberRange range0 = ShardObjectHelper.newSequenceNumberRange("1000", "1049");
    final SequenceNumberRange range1 = ShardObjectHelper.newSequenceNumberRange("1050", "1099");
    final SequenceNumberRange range2 = ShardObjectHelper.newSequenceNumberRange("1100", "1149");
    final SequenceNumberRange range3 = ShardObjectHelper.newSequenceNumberRange("1150", "1199");
    final SequenceNumberRange range4 = ShardObjectHelper.newSequenceNumberRange("1200", "1249");
    final SequenceNumberRange range5 = ShardObjectHelper.newSequenceNumberRange("1250", "1299");
    final SequenceNumberRange range6 = ShardObjectHelper.newSequenceNumberRange("1300", null);
    final HashKeyRange hashRange0 = ShardObjectHelper.newHashKeyRange("0", "499");
    final HashKeyRange hashRange1 = ShardObjectHelper.newHashKeyRange("500", ShardObjectHelper.MAX_HASH_KEY);
    final HashKeyRange hashRange2 = ShardObjectHelper.newHashKeyRange("0", ShardObjectHelper.MAX_HASH_KEY);
    return Arrays.asList(ShardObjectHelper.newShard("shardId-0", null, null, range0, hashRange0), ShardObjectHelper.newShard("shardId-1", null, null, range0, hashRange1), ShardObjectHelper.newShard("shardId-2", "shardId-0", "shardId-1", range1, hashRange2), ShardObjectHelper.newShard("shardId-3", "shardId-2", null, range2, hashRange0), ShardObjectHelper.newShard("shardId-4", "shardId-2", null, range2, hashRange1), ShardObjectHelper.newShard("shardId-5", "shardId-3", "shardId-4", range3, hashRange2), ShardObjectHelper.newShard("shardId-6", "shardId-5", null, range4, hashRange0), ShardObjectHelper.newShard("shardId-7", "shardId-5", null, range4, hashRange1), ShardObjectHelper.newShard("shardId-8", "shardId-6", "shardId-7", range5, hashRange2), ShardObjectHelper.newShard("shardId-9", "shardId-8", null, range6, hashRange0), ShardObjectHelper.newShard("shardId-10", null, "shardId-8", range6, hashRange1));
  }

  /**
     * Test CheckIfDescendantAndAddNewLeasesForAncestors when shardId is null
     */
  @Test public void testCheckIfDescendantAndAddNewLeasesForAncestorsNullShardId() {
    final Map<String, Boolean> memoizationContext = new HashMap<>();
    assertThat(HierarchicalShardSyncer.checkIfDescendantAndAddNewLeasesForAncestors(null, INITIAL_POSITION_LATEST, null, null, null, memoizationContext), equalTo(false));
  }

  /**
     * Test CheckIfDescendantAndAddNewLeasesForAncestors when shard has been trimmed
     */
  @Test public void testCheckIfDescendantAndAddNewLeasesForAncestorsTrimmedShard() {
    final String shardId = "shardId-trimmed";
    final Map<String, Boolean> memoizationContext = new HashMap<>();
    assertThat(HierarchicalShardSyncer.checkIfDescendantAndAddNewLeasesForAncestors(shardId, INITIAL_POSITION_LATEST, null, new HashMap<>(), null, memoizationContext), equalTo(false));
  }

  /**
     * Test CheckIfDescendantAndAddNewLeasesForAncestors when there is a current lease for the shard
     */
  @Test public void testCheckIfDescendantAndAddNewLeasesForAncestorsForShardWithCurrentLease() {
    final String shardId = "shardId-current";
    final Set<String> shardIdsOfCurrentLeases = new HashSet<>(Collections.singletonList(shardId));
    final Map<String, Lease> newLeaseMap = Collections.emptyMap();
    final Map<String, Boolean> memoizationContext = new HashMap<>();
    final Map<String, Shard> kinesisShards = new HashMap<>();
    kinesisShards.put(shardId, ShardObjectHelper.newShard(shardId, null, null, null));
    assertThat(HierarchicalShardSyncer.checkIfDescendantAndAddNewLeasesForAncestors(shardId, INITIAL_POSITION_LATEST, shardIdsOfCurrentLeases, kinesisShards, newLeaseMap, memoizationContext), equalTo(true));
    assertThat(newLeaseMap.isEmpty(), equalTo(true));
  }

  /**
     * Test CheckIfDescendantAndAddNewLeasesForAncestors - two parents, two ancestors, not descendant
     */
  @Test public void testCheckIfDescendantAndAddNewLeasesForAncestors2P2ANotDescendant() {
    final String parentShardId = "shardId-parent";
    final String adjacentParentShardId = "shardId-adjacentParent";
    final String shardId = "shardId-9-1";
    final Set<String> shardIdsOfCurrentLeases = Collections.emptySet();
    final Map<String, Lease> newLeaseMap = Collections.emptyMap();
    final Map<String, Boolean> memoizationContext = new HashMap<>();
    final Map<String, Shard> kinesisShards = new HashMap<>();
    kinesisShards.put(parentShardId, ShardObjectHelper.newShard(parentShardId, null, null, null));
    kinesisShards.put(adjacentParentShardId, ShardObjectHelper.newShard(adjacentParentShardId, null, null, null));
    kinesisShards.put(shardId, ShardObjectHelper.newShard(shardId, parentShardId, adjacentParentShardId, null));
    assertThat(HierarchicalShardSyncer.checkIfDescendantAndAddNewLeasesForAncestors(shardId, INITIAL_POSITION_LATEST, shardIdsOfCurrentLeases, kinesisShards, newLeaseMap, memoizationContext), equalTo(false));
    assertThat(newLeaseMap.isEmpty(), equalTo(true));
  }

  /**
     * Helper method.
     *
     * @param shardId
     * @return
     */
  private static Lease newLease(final String shardId) {
    final Lease lease = new Lease();
    lease.leaseKey(shardId);
    return lease;
  }
}