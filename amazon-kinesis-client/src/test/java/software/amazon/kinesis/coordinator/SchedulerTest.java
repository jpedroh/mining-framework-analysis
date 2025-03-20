package software.amazon.kinesis.coordinator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.atMost;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;
import io.reactivex.plugins.RxJavaPlugins;
import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.checkpoint.Checkpoint;
import software.amazon.kinesis.checkpoint.CheckpointConfig;
import software.amazon.kinesis.checkpoint.CheckpointFactory;
import software.amazon.kinesis.common.InitialPositionInStream;
import software.amazon.kinesis.common.InitialPositionInStreamExtended;
import software.amazon.kinesis.common.StreamConfig;
import software.amazon.kinesis.common.StreamIdentifier;
import software.amazon.kinesis.exceptions.KinesisClientLibException;
import software.amazon.kinesis.exceptions.KinesisClientLibNonRetryableException;
import software.amazon.kinesis.leases.LeaseCoordinator;
import software.amazon.kinesis.leases.LeaseManagementConfig;
import software.amazon.kinesis.leases.LeaseManagementFactory;
import software.amazon.kinesis.leases.LeaseRefresher;
import software.amazon.kinesis.leases.ShardDetector;
import software.amazon.kinesis.leases.ShardInfo;
import software.amazon.kinesis.leases.ShardSyncTaskManager;
import software.amazon.kinesis.leases.dynamodb.DynamoDBLeaseRefresher;
import software.amazon.kinesis.leases.exceptions.DependencyException;
import software.amazon.kinesis.leases.exceptions.ProvisionedThroughputException;
import software.amazon.kinesis.lifecycle.LifecycleConfig;
import software.amazon.kinesis.lifecycle.ShardConsumer;
import software.amazon.kinesis.lifecycle.events.InitializationInput;
import software.amazon.kinesis.lifecycle.events.LeaseLostInput;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.lifecycle.events.ShardEndedInput;
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput;
import software.amazon.kinesis.metrics.MetricsFactory;
import software.amazon.kinesis.metrics.MetricsConfig;
import software.amazon.kinesis.processor.Checkpointer;
import software.amazon.kinesis.processor.MultiStreamTracker;
import software.amazon.kinesis.processor.ProcessorConfig;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.RecordsPublisher;
import software.amazon.kinesis.retrieval.RetrievalConfig;
import software.amazon.kinesis.retrieval.RetrievalFactory;
import software.amazon.kinesis.retrieval.kpl.ExtendedSequenceNumber;

/**
 *
 */
@RunWith(value = MockitoJUnitRunner.class) public class SchedulerTest {
  private final String tableName = "tableName";

  private final String workerIdentifier = "workerIdentifier";

  private final String applicationName = "applicationName";

  private final String streamName = "streamName";

  private final String namespace = "testNamespace";

  private Scheduler scheduler;


<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/test/java/software/amazon/kinesis/coordinator/SchedulerTest.java/left.java
  @Mock private MultiStreamTracker multiStreamTracker;
=======
  private static final long MIN_WAIT_TIME_FOR_LEASE_TABLE_CHECK_MILLIS = 5 * 1000L;
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/test/java/software/amazon/kinesis/coordinator/SchedulerTest.java/right.java


  private ShardRecordProcessorFactory shardRecordProcessorFactory;


<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/test/java/software/amazon/kinesis/coordinator/SchedulerTest.java/left.java
  private Map<StreamIdentifier, ShardSyncTaskManager> shardSyncTaskManagerMap = new HashMap<>();
=======
  private static final long MAX_WAIT_TIME_FOR_LEASE_TABLE_CHECK_MILLIS = 30 * 1000L;
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/test/java/software/amazon/kinesis/coordinator/SchedulerTest.java/right.java


  private CheckpointConfig checkpointConfig;


<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/test/java/software/amazon/kinesis/coordinator/SchedulerTest.java/left.java
  private Map<StreamIdentifier, ShardDetector> shardDetectorMap = new HashMap<>();
=======
  private static final long LEASE_TABLE_CHECK_FREQUENCY_MILLIS = 3 * 1000L;
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/test/java/software/amazon/kinesis/coordinator/SchedulerTest.java/right.java


  private CoordinatorConfig coordinatorConfig;

  private LeaseManagementConfig leaseManagementConfig;

  private LifecycleConfig lifecycleConfig;

  private MetricsConfig metricsConfig;

  private ProcessorConfig processorConfig;

  private RetrievalConfig retrievalConfig;

  @Mock private KinesisAsyncClient kinesisClient;

  @Mock private DynamoDbAsyncClient dynamoDBClient;

  @Mock private CloudWatchAsyncClient cloudWatchClient;

  @Mock private RetrievalFactory retrievalFactory;

  @Mock private RecordsPublisher recordsPublisher;

  @Mock private LeaseCoordinator leaseCoordinator;

  @Mock private ShardSyncTaskManager shardSyncTaskManager;

  @Mock private DynamoDBLeaseRefresher dynamoDBLeaseRefresher;

  @Mock private ShardDetector shardDetector;

  @Mock private Checkpointer checkpoint;

  @Mock private WorkerStateChangeListener workerStateChangeListener;

  @Before public void setup() {
    shardRecordProcessorFactory = new TestShardRecordProcessorFactory();
    checkpointConfig = new CheckpointConfig().checkpointFactory(new TestKinesisCheckpointFactory());
    coordinatorConfig = new CoordinatorConfig(applicationName).parentShardPollIntervalMillis(100L).workerStateChangeListener(workerStateChangeListener);
    leaseManagementConfig = new LeaseManagementConfig(tableName, dynamoDBClient, kinesisClient, streamName, workerIdentifier).leaseManagementFactory(new TestKinesisLeaseManagementFactory(false, false));
    lifecycleConfig = new LifecycleConfig();
    metricsConfig = new MetricsConfig(cloudWatchClient, namespace);
    processorConfig = new ProcessorConfig(shardRecordProcessorFactory);
    retrievalConfig = new RetrievalConfig(kinesisClient, streamName, applicationName).retrievalFactory(retrievalFactory);
    final List<StreamConfig> streamConfigList = new ArrayList<StreamConfig>() {
      {
        add(new StreamConfig(StreamIdentifier.multiStreamInstance("acc1:stream1:1"), InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.LATEST)));
        add(new StreamConfig(StreamIdentifier.multiStreamInstance("acc1:stream2:2"), InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.LATEST)));
        add(new StreamConfig(StreamIdentifier.multiStreamInstance("acc2:stream1:1"), InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.LATEST)));
        add(new StreamConfig(StreamIdentifier.multiStreamInstance("acc2:stream2:3"), InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.LATEST)));
      }
    };
    when(multiStreamTracker.streamConfigList()).thenReturn(streamConfigList);
    when(leaseCoordinator.leaseRefresher()).thenReturn(dynamoDBLeaseRefresher);
    when(shardSyncTaskManager.shardDetector()).thenReturn(shardDetector);
    when(retrievalFactory.createGetRecordsCache(any(ShardInfo.class), any(MetricsFactory.class))).thenReturn(recordsPublisher);
    scheduler = new Scheduler(checkpointConfig, coordinatorConfig, leaseManagementConfig, lifecycleConfig, metricsConfig, processorConfig, retrievalConfig);
  }

  /**
     * Test method for {@link Scheduler#applicationName()}.
     */
  @Test public void testGetStageName() {
    final String stageName = "testStageName";
    coordinatorConfig = new CoordinatorConfig(stageName);
    scheduler = new Scheduler(checkpointConfig, coordinatorConfig, leaseManagementConfig, lifecycleConfig, metricsConfig, processorConfig, retrievalConfig);
    assertEquals(stageName, scheduler.applicationName());
  }

  @Test public final void testCreateOrGetShardConsumer() {
    final String shardId = "shardId-000000000000";
    final String concurrencyToken = "concurrencyToken";
    final ShardInfo shardInfo = new ShardInfo(shardId, concurrencyToken, null, ExtendedSequenceNumber.TRIM_HORIZON);
    final ShardConsumer shardConsumer1 = scheduler.createOrGetShardConsumer(shardInfo, shardRecordProcessorFactory);
    assertNotNull(shardConsumer1);
    final ShardConsumer shardConsumer2 = scheduler.createOrGetShardConsumer(shardInfo, shardRecordProcessorFactory);
    assertNotNull(shardConsumer2);
    assertSame(shardConsumer1, shardConsumer2);
    final String anotherConcurrencyToken = "anotherConcurrencyToken";
    final ShardInfo shardInfo2 = new ShardInfo(shardId, anotherConcurrencyToken, null, ExtendedSequenceNumber.TRIM_HORIZON);
    final ShardConsumer shardConsumer3 = scheduler.createOrGetShardConsumer(shardInfo2, shardRecordProcessorFactory);
    assertNotNull(shardConsumer3);
    assertNotSame(shardConsumer1, shardConsumer3);
  }

  @Test public void testWorkerLoopWithCheckpoint() throws Exception {
    final String shardId = "shardId-000000000000";
    final String concurrencyToken = "concurrencyToken";
    final ExtendedSequenceNumber firstSequenceNumber = ExtendedSequenceNumber.TRIM_HORIZON;
    final ExtendedSequenceNumber secondSequenceNumber = new ExtendedSequenceNumber("1000");
    final ExtendedSequenceNumber finalSequenceNumber = new ExtendedSequenceNumber("2000");
    final List<ShardInfo> initialShardInfo = Collections.singletonList(new ShardInfo(shardId, concurrencyToken, null, firstSequenceNumber));
    final List<ShardInfo> firstShardInfo = Collections.singletonList(new ShardInfo(shardId, concurrencyToken, null, secondSequenceNumber));
    final List<ShardInfo> secondShardInfo = Collections.singletonList(new ShardInfo(shardId, concurrencyToken, null, finalSequenceNumber));
    final Checkpoint firstCheckpoint = new Checkpoint(firstSequenceNumber, null);
    when(leaseCoordinator.getCurrentAssignments()).thenReturn(initialShardInfo, firstShardInfo, secondShardInfo);
    when(checkpoint.getCheckpointObject(eq(shardId))).thenReturn(firstCheckpoint);
    Scheduler schedulerSpy = spy(scheduler);
    schedulerSpy.runProcessLoop();
    schedulerSpy.runProcessLoop();
    schedulerSpy.runProcessLoop();
    verify(schedulerSpy).buildConsumer(same(initialShardInfo.get(0)), eq(shardRecordProcessorFactory));
    verify(schedulerSpy, never()).buildConsumer(same(firstShardInfo.get(0)), eq(shardRecordProcessorFactory));
    verify(schedulerSpy, never()).buildConsumer(same(secondShardInfo.get(0)), eq(shardRecordProcessorFactory));
    verify(checkpoint).getCheckpointObject(eq(shardId));
  }

  @Test public final void testCleanupShardConsumers() {
    final String shard0 = "shardId-000000000000";
    final String shard1 = "shardId-000000000001";
    final String concurrencyToken = "concurrencyToken";
    final String anotherConcurrencyToken = "anotherConcurrencyToken";
    final ShardInfo shardInfo0 = new ShardInfo(shard0, concurrencyToken, null, ExtendedSequenceNumber.TRIM_HORIZON);
    final ShardInfo shardInfo0WithAnotherConcurrencyToken = new ShardInfo(shard0, anotherConcurrencyToken, null, ExtendedSequenceNumber.TRIM_HORIZON);
    final ShardInfo shardInfo1 = new ShardInfo(shard1, concurrencyToken, null, ExtendedSequenceNumber.TRIM_HORIZON);
    final ShardConsumer shardConsumer0 = scheduler.createOrGetShardConsumer(shardInfo0, shardRecordProcessorFactory);
    final ShardConsumer shardConsumer0WithAnotherConcurrencyToken = scheduler.createOrGetShardConsumer(shardInfo0WithAnotherConcurrencyToken, shardRecordProcessorFactory);
    final ShardConsumer shardConsumer1 = scheduler.createOrGetShardConsumer(shardInfo1, shardRecordProcessorFactory);
    Set<ShardInfo> shards = new HashSet<>();
    shards.add(shardInfo0);
    shards.add(shardInfo1);
    scheduler.cleanupShardConsumers(shards);
    assertTrue(shardConsumer0WithAnotherConcurrencyToken.isShutdownRequested());
    assertFalse(shardConsumer0.isShutdownRequested());
    assertFalse(shardConsumer1.isShutdownRequested());
  }

  @Test public final void testInitializationFailureWithRetries() throws Exception {
    doNothing().when(leaseCoordinator).initialize();
    when(shardDetector.listShards()).thenThrow(new RuntimeException());
    leaseManagementConfig = new LeaseManagementConfig(tableName, dynamoDBClient, kinesisClient, streamName, workerIdentifier).leaseManagementFactory(new TestKinesisLeaseManagementFactory(false, true));
    scheduler = new Scheduler(checkpointConfig, coordinatorConfig, leaseManagementConfig, lifecycleConfig, metricsConfig, processorConfig, retrievalConfig);
    scheduler.run();
    verify(shardDetector, times(coordinatorConfig.maxInitializationAttempts())).listShards();
  }

  @Test public final void testInitializationFailureWithRetriesWithConfiguredMaxInitializationAttempts() throws Exception {
    final int maxInitializationAttempts = 5;
    coordinatorConfig.maxInitializationAttempts(maxInitializationAttempts);
    leaseManagementConfig = new LeaseManagementConfig(tableName, dynamoDBClient, kinesisClient, streamName, workerIdentifier).leaseManagementFactory(new TestKinesisLeaseManagementFactory(false, true));
    scheduler = new Scheduler(checkpointConfig, coordinatorConfig, leaseManagementConfig, lifecycleConfig, metricsConfig, processorConfig, retrievalConfig);
    doNothing().when(leaseCoordinator).initialize();
    when(shardDetector.listShards()).thenThrow(new RuntimeException());
    scheduler.run();
    verify(shardDetector, times(maxInitializationAttempts)).listShards();
  }

  @Test public final void testMultiStreamInitialization() throws ProvisionedThroughputException, DependencyException {
    retrievalConfig = new RetrievalConfig(kinesisClient, multiStreamTracker, applicationName).retrievalFactory(retrievalFactory);
    scheduler = new Scheduler(checkpointConfig, coordinatorConfig, leaseManagementConfig, lifecycleConfig, metricsConfig, processorConfig, retrievalConfig);
    scheduler.initialize();
    shardDetectorMap.values().stream().forEach((shardDetector) -> verify(shardDetector, times(1)).listShards());
  }

  @Test public final void testInitializationWaitsWhenLeaseTableIsEmpty() throws Exception {
    final int maxInitializationAttempts = 1;
    coordinatorConfig.maxInitializationAttempts(maxInitializationAttempts);
    coordinatorConfig.skipShardSyncAtWorkerInitializationIfLeasesExist(false);
    scheduler = new Scheduler(checkpointConfig, coordinatorConfig, leaseManagementConfig, lifecycleConfig, metricsConfig, processorConfig, retrievalConfig);
    doNothing().when(leaseCoordinator).initialize();
    when(dynamoDBLeaseRefresher.isLeaseTableEmpty()).thenReturn(true);
    long startTime = System.currentTimeMillis();
    scheduler.waitUntilLeaseTableIsReady();
    long endTime = System.currentTimeMillis();
    assertTrue(endTime - startTime > MIN_WAIT_TIME_FOR_LEASE_TABLE_CHECK_MILLIS);
    assertTrue(endTime - startTime < (MAX_WAIT_TIME_FOR_LEASE_TABLE_CHECK_MILLIS + LEASE_TABLE_CHECK_FREQUENCY_MILLIS));
  }

  @Test public final void testMultiStreamInitializationWithFailures() {
    retrievalConfig = new RetrievalConfig(kinesisClient, multiStreamTracker, applicationName).retrievalFactory(retrievalFactory);
    leaseManagementConfig = new LeaseManagementConfig(tableName, dynamoDBClient, kinesisClient, workerIdentifier).leaseManagementFactory(new TestKinesisLeaseManagementFactory(true, false));
    scheduler = new Scheduler(checkpointConfig, coordinatorConfig, leaseManagementConfig, lifecycleConfig, metricsConfig, processorConfig, retrievalConfig);
    scheduler.initialize();
    shardDetectorMap.values().stream().forEach((shardDetector) -> verify(shardDetector, atLeast(2)).listShards());
    shardDetectorMap.values().stream().forEach((shardDetector) -> verify(shardDetector, atMost(5)).listShards());
  }

  @Test public final void testInitializationDoesntWaitWhenLeaseTableIsNotEmpty() throws Exception {
    final int maxInitializationAttempts = 1;
    coordinatorConfig.maxInitializationAttempts(maxInitializationAttempts);
    coordinatorConfig.skipShardSyncAtWorkerInitializationIfLeasesExist(false);
    scheduler = new Scheduler(checkpointConfig, coordinatorConfig, leaseManagementConfig, lifecycleConfig, metricsConfig, processorConfig, retrievalConfig);
    doNothing().when(leaseCoordinator).initialize();
    when(dynamoDBLeaseRefresher.isLeaseTableEmpty()).thenReturn(false);
    long startTime = System.currentTimeMillis();
    scheduler.waitUntilLeaseTableIsReady();
    long endTime = System.currentTimeMillis();
    assertTrue(endTime - startTime < MIN_WAIT_TIME_FOR_LEASE_TABLE_CHECK_MILLIS);
  }

  @Test public final void testMultiStreamConsumersAreBuiltOncePerAccountStreamShard() throws KinesisClientLibException {
    final String shardId = "shardId-000000000000";
    final String concurrencyToken = "concurrencyToken";
    final ExtendedSequenceNumber firstSequenceNumber = ExtendedSequenceNumber.TRIM_HORIZON;
    final ExtendedSequenceNumber secondSequenceNumber = new ExtendedSequenceNumber("1000");
    final ExtendedSequenceNumber finalSequenceNumber = new ExtendedSequenceNumber("2000");
    final List<ShardInfo> initialShardInfo = multiStreamTracker.streamConfigList().stream().map((sc) -> new ShardInfo(shardId, concurrencyToken, null, firstSequenceNumber, sc.streamIdentifier().serialize())).collect(Collectors.toList());
    final List<ShardInfo> firstShardInfo = multiStreamTracker.streamConfigList().stream().map((sc) -> new ShardInfo(shardId, concurrencyToken, null, secondSequenceNumber, sc.streamIdentifier().serialize())).collect(Collectors.toList());
    final List<ShardInfo> secondShardInfo = multiStreamTracker.streamConfigList().stream().map((sc) -> new ShardInfo(shardId, concurrencyToken, null, finalSequenceNumber, sc.streamIdentifier().serialize())).collect(Collectors.toList());
    final Checkpoint firstCheckpoint = new Checkpoint(firstSequenceNumber, null);
    when(leaseCoordinator.getCurrentAssignments()).thenReturn(initialShardInfo, firstShardInfo, secondShardInfo);
    when(checkpoint.getCheckpointObject(anyString())).thenReturn(firstCheckpoint);
    retrievalConfig = new RetrievalConfig(kinesisClient, multiStreamTracker, applicationName).retrievalFactory(retrievalFactory);
    scheduler = new Scheduler(checkpointConfig, coordinatorConfig, leaseManagementConfig, lifecycleConfig, metricsConfig, processorConfig, retrievalConfig);
    Scheduler schedulerSpy = spy(scheduler);
    schedulerSpy.runProcessLoop();
    schedulerSpy.runProcessLoop();
    schedulerSpy.runProcessLoop();
    initialShardInfo.stream().forEach((shardInfo) -> verify(schedulerSpy).buildConsumer(same(shardInfo), eq(shardRecordProcessorFactory)));
    firstShardInfo.stream().forEach((shardInfo) -> verify(schedulerSpy, never()).buildConsumer(same(shardInfo), eq(shardRecordProcessorFactory)));
    secondShardInfo.stream().forEach((shardInfo) -> verify(schedulerSpy, never()).buildConsumer(same(shardInfo), eq(shardRecordProcessorFactory)));
  }

  @Test public final void testSchedulerShutdown() {
    scheduler.shutdown();
    verify(workerStateChangeListener, times(1)).onWorkerStateChange(WorkerStateChangeListener.WorkerState.SHUT_DOWN_STARTED);
    verify(leaseCoordinator, times(1)).stop();
    verify(workerStateChangeListener, times(1)).onWorkerStateChange(WorkerStateChangeListener.WorkerState.SHUT_DOWN);
  }

  @Test public void testErrorHandlerForUndeliverableAsyncTaskExceptions() {
    DiagnosticEventFactory eventFactory = mock(DiagnosticEventFactory.class);
    ExecutorStateEvent executorStateEvent = mock(ExecutorStateEvent.class);
    RejectedTaskEvent rejectedTaskEvent = mock(RejectedTaskEvent.class);
    when(eventFactory.rejectedTaskEvent(any(), any())).thenReturn(rejectedTaskEvent);
    when(eventFactory.executorStateEvent(any(), any())).thenReturn(executorStateEvent);
    Scheduler testScheduler = new Scheduler(checkpointConfig, coordinatorConfig, leaseManagementConfig, lifecycleConfig, metricsConfig, processorConfig, retrievalConfig, eventFactory);
    Scheduler schedulerSpy = spy(testScheduler);
    doCallRealMethod().doCallRealMethod().doAnswer((invocation) -> {
      RxJavaPlugins.onError(new RejectedExecutionException("Test exception."));
      return null;
    }).when(schedulerSpy).runProcessLoop();
    schedulerSpy.initialize();
    schedulerSpy.runProcessLoop();
    schedulerSpy.runProcessLoop();
    schedulerSpy.runProcessLoop();
    verify(eventFactory, times(1)).rejectedTaskEvent(eq(executorStateEvent), any());
    verify(rejectedTaskEvent, times(1)).accept(any());
  }

  private static class TestShardRecordProcessorFactory implements ShardRecordProcessorFactory {
    @Override public ShardRecordProcessor shardRecordProcessor() {
      return new ShardRecordProcessor() {
        @Override public void initialize(final InitializationInput initializationInput) {
        }

        @Override public void processRecords(final ProcessRecordsInput processRecordsInput) {
          try {
            processRecordsInput.checkpointer().checkpoint();
          } catch (KinesisClientLibNonRetryableException e) {
            throw new RuntimeException(e);
          }
        }

        @Override public void leaseLost(LeaseLostInput leaseLostInput) {
        }

        @Override public void shardEnded(ShardEndedInput shardEndedInput) {
          try {
            shardEndedInput.checkpointer().checkpoint();
          } catch (KinesisClientLibNonRetryableException e) {
            throw new RuntimeException(e);
          }
        }

        @Override public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
        }
      };
    }

    @Override public ShardRecordProcessor shardRecordProcessor(StreamIdentifier streamIdentifier) {
      return shardRecordProcessor();
    }
  }

  @RequiredArgsConstructor private class TestKinesisLeaseManagementFactory implements LeaseManagementFactory {
    private final boolean shardSyncFirstAttemptFailure;

    private final boolean shouldReturnDefaultShardSyncTaskmanager;

    @Override public LeaseCoordinator createLeaseCoordinator(MetricsFactory metricsFactory) {
      return leaseCoordinator;
    }

    @Override public ShardSyncTaskManager createShardSyncTaskManager(MetricsFactory metricsFactory) {
      return shardSyncTaskManager;
    }

    @Override public ShardSyncTaskManager createShardSyncTaskManager(MetricsFactory metricsFactory, StreamConfig streamConfig) {
      if (shouldReturnDefaultShardSyncTaskmanager) {
        return shardSyncTaskManager;
      }
      final ShardSyncTaskManager shardSyncTaskManager = mock(ShardSyncTaskManager.class);
      final ShardDetector shardDetector = mock(ShardDetector.class);
      shardSyncTaskManagerMap.put(streamConfig.streamIdentifier(), shardSyncTaskManager);
      shardDetectorMap.put(streamConfig.streamIdentifier(), shardDetector);
      when(shardSyncTaskManager.shardDetector()).thenReturn(shardDetector);
      if (shardSyncFirstAttemptFailure) {
        when(shardDetector.listShards()).thenThrow(new RuntimeException("Service Exception")).thenReturn(Collections.EMPTY_LIST);
      }
      return shardSyncTaskManager;
    }

    @Override public DynamoDBLeaseRefresher createLeaseRefresher() {
      return dynamoDBLeaseRefresher;
    }

    @Override public ShardDetector createShardDetector() {
      return shardDetector;
    }

    @Override public ShardDetector createShardDetector(StreamConfig streamConfig) {
      return shardDetectorMap.get(streamConfig.streamIdentifier());
    }
  }

  private class TestKinesisCheckpointFactory implements CheckpointFactory {
    @Override public Checkpointer createCheckpointer(final LeaseCoordinator leaseCoordinator, final LeaseRefresher leaseRefresher) {
      return checkpoint;
    }
  }
}