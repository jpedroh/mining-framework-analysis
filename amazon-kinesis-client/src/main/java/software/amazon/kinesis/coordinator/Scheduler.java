package software.amazon.kinesis.coordinator;
import java.util.Collection;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import lombok.AccessLevel;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import java.util.concurrent.Callable;
import lombok.NoArgsConstructor;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import java.util.concurrent.ConcurrentMap;
import lombok.experimental.Accessors;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ExecutorService;
import software.amazon.kinesis.checkpoint.CheckpointConfig;
import java.util.concurrent.Future;
import software.amazon.kinesis.checkpoint.ShardRecordProcessorCheckpointer;
import java.util.concurrent.TimeUnit;
import software.amazon.kinesis.leases.HierarchicalShardSyncer;
import java.util.function.Function;
import java.util.stream.Collectors;
import software.amazon.awssdk.utils.Validate;
import software.amazon.kinesis.common.StreamConfig;
import software.amazon.kinesis.common.StreamIdentifier;
import software.amazon.kinesis.leases.Lease;
import software.amazon.kinesis.leases.LeaseCoordinator;
import software.amazon.kinesis.leases.LeaseManagementConfig;
import software.amazon.kinesis.leases.LeaseRefresher;
import software.amazon.kinesis.leases.LeaseSerializer;
import software.amazon.kinesis.leases.ShardDetector;
import software.amazon.kinesis.leases.ShardInfo;
import software.amazon.kinesis.leases.ShardPrioritization;
import software.amazon.kinesis.leases.ShardSyncTask;
import software.amazon.kinesis.leases.ShardSyncTaskManager;
import software.amazon.kinesis.leases.dynamodb.DynamoDBLeaseCoordinator;
import software.amazon.kinesis.leases.dynamodb.DynamoDBLeaseSerializer;
import software.amazon.kinesis.leases.exceptions.DependencyException;
import software.amazon.kinesis.leases.dynamodb.DynamoDBMultiStreamLeaseSerializer;
import software.amazon.kinesis.leases.exceptions.InvalidStateException;
import software.amazon.kinesis.leases.exceptions.LeasingException;
import software.amazon.kinesis.leases.exceptions.ProvisionedThroughputException;
import software.amazon.kinesis.lifecycle.LifecycleConfig;
import software.amazon.kinesis.lifecycle.ShardConsumer;
import software.amazon.kinesis.lifecycle.ShardConsumerArgument;
import software.amazon.kinesis.lifecycle.ShardConsumerShutdownNotification;
import software.amazon.kinesis.lifecycle.ShutdownNotification;
import software.amazon.kinesis.lifecycle.ShutdownReason;
import software.amazon.kinesis.lifecycle.TaskResult;
import software.amazon.kinesis.metrics.CloudWatchMetricsFactory;
import software.amazon.kinesis.metrics.MetricsConfig;
import software.amazon.kinesis.metrics.MetricsFactory;
import software.amazon.kinesis.processor.Checkpointer;
import software.amazon.kinesis.processor.ProcessorConfig;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;
import software.amazon.kinesis.processor.ShutdownNotificationAware;
import software.amazon.kinesis.retrieval.AggregatorUtil;
import software.amazon.kinesis.retrieval.RecordsPublisher;
import software.amazon.kinesis.retrieval.RetrievalConfig;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
@Getter @Accessors(fluent = true) @Slf4j public class Scheduler implements Runnable {
  private SchedulerLog slog = new SchedulerLog();

  private static final 
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/left.java
  boolean
=======
  int
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/right.java
   
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/left.java
  isMultiStreamMode
=======
  PERIODIC_SHARD_SYNC_MAX_WORKERS_DEFAULT = 1
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/right.java
  ;

  private final CheckpointConfig checkpointConfig;


<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/left.java
  private final Function<StreamIdentifier, ShardSyncTaskManager> shardSyncTaskManagerProvider;
=======
  private static final long LEASE_TABLE_CHECK_FREQUENCY_MILLIS = 3 * 1000L;
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/right.java


  private final CoordinatorConfig coordinatorConfig;


<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/left.java
  private final Map<StreamIdentifier, ShardSyncTaskManager> streamToShardSyncTaskManagerMap = new HashMap<>();
=======
  private static final long MIN_WAIT_TIME_FOR_LEASE_TABLE_CHECK_MILLIS = 5 * 1000L;
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/right.java


  private static final long MAX_WAIT_TIME_FOR_LEASE_TABLE_CHECK_MILLIS = 30 * 1000L;

  private static final long HASH_RANGE_COVERAGE_CHECK_FREQUENCY_MILLIS = 5000L;

  private final LeaseManagementConfig leaseManagementConfig;

  private final LifecycleConfig lifecycleConfig;

  private final MetricsConfig metricsConfig;

  private final ProcessorConfig processorConfig;

  private final RetrievalConfig retrievalConfig;

  private final String applicationName;

  private final int maxInitializationAttempts;

  private final Checkpointer checkpoint;

  private final long shardConsumerDispatchPollIntervalMillis;

  private final long parentShardPollIntervalMillis;

  private final ExecutorService executorService;

  private final DiagnosticEventFactory diagnosticEventFactory;

  private final DiagnosticEventHandler diagnosticEventHandler;

  private final LeaseCoordinator leaseCoordinator;

  private final PeriodicShardSyncManager leaderElectedPeriodicShardSyncManager;

  private final ShardPrioritization shardPrioritization;

  private final boolean cleanupLeasesUponShardCompletion;

  private final boolean skipShardSyncAtWorkerInitializationIfLeasesExist;

  private final GracefulShutdownCoordinator gracefulShutdownCoordinator;

  private final WorkerStateChangeListener workerStateChangeListener;

  private final MetricsFactory metricsFactory;

  private final long failoverTimeMillis;

  private final long taskBackoffTimeMillis;

  private final Map<StreamIdentifier, StreamConfig> currentStreamConfigMap;

  private final long listShardsBackoffTimeMillis;

  private final int maxListShardsRetryAttempts;

  private final LeaseRefresher leaseRefresher;


<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/left.java
  private final Function<StreamIdentifier, ShardDetector> shardDetectorProvider;
=======
  private final LeaderDecider leaderDecider;
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/right.java


  private final boolean ignoreUnexpetedChildShards;

  private final AggregatorUtil aggregatorUtil;

  private final HierarchicalShardSyncer hierarchicalShardSyncer;

  private final long schedulerInitializationBackoffTimeMillis;

  private ConcurrentMap<ShardInfo, ShardConsumer> shardInfoShardConsumerMap = new ConcurrentHashMap<>();

  private volatile boolean shutdown;

  private volatile long shutdownStartTimeMillis;

  private volatile boolean shutdownComplete = false;

  private final Object lock = new Object();

  /**
     * Used to ensure that only one requestedShutdown is in progress at a time.
     */
  private Future<Boolean> gracefulShutdownFuture;

  @VisibleForTesting protected boolean gracefuleShutdownStarted = false;

  public Scheduler(@NonNull final CheckpointConfig checkpointConfig, @NonNull final CoordinatorConfig coordinatorConfig, @NonNull final LeaseManagementConfig leaseManagementConfig, @NonNull final LifecycleConfig lifecycleConfig, @NonNull final MetricsConfig metricsConfig, @NonNull final ProcessorConfig processorConfig, @NonNull final RetrievalConfig retrievalConfig) {
    this(checkpointConfig, coordinatorConfig, leaseManagementConfig, lifecycleConfig, metricsConfig, processorConfig, retrievalConfig, new DiagnosticEventFactory());
  }

  /**
     * Customers do not currently have the ability to customize the DiagnosticEventFactory, but this visibility
     * is desired for testing. This constructor is only used for testing to provide a mock DiagnosticEventFactory.
     */
  @VisibleForTesting protected Scheduler(@NonNull final CheckpointConfig checkpointConfig, @NonNull final CoordinatorConfig coordinatorConfig, @NonNull final LeaseManagementConfig leaseManagementConfig, @NonNull final LifecycleConfig lifecycleConfig, @NonNull final MetricsConfig metricsConfig, @NonNull final ProcessorConfig processorConfig, @NonNull final RetrievalConfig retrievalConfig, @NonNull final DiagnosticEventFactory diagnosticEventFactory) {
    this.checkpointConfig = checkpointConfig;
    this.coordinatorConfig = coordinatorConfig;
    this.leaseManagementConfig = leaseManagementConfig;
    this.lifecycleConfig = lifecycleConfig;
    this.metricsConfig = metricsConfig;
    this.processorConfig = processorConfig;
    this.retrievalConfig = retrievalConfig;
    this.applicationName = this.coordinatorConfig.applicationName();
    this.isMultiStreamMode = this.retrievalConfig.appStreamTracker().map((multiStreamTracker) -> true, (streamConfig) -> false);
    this.currentStreamConfigMap = this.retrievalConfig.appStreamTracker().map((multiStreamTracker) -> multiStreamTracker.streamConfigList().stream().collect(Collectors.toMap((sc) -> sc.streamIdentifier(), (sc) -> sc)), (streamConfig) -> Collections.singletonMap(streamConfig.streamIdentifier(), streamConfig));
    this.maxInitializationAttempts = this.coordinatorConfig.maxInitializationAttempts();
    this.metricsFactory = this.metricsConfig.metricsFactory();
    final LeaseSerializer leaseSerializer = isMultiStreamMode ? new DynamoDBMultiStreamLeaseSerializer() : new DynamoDBLeaseSerializer();
    this.leaseCoordinator = this.leaseManagementConfig.leaseManagementFactory(leaseSerializer, isMultiStreamMode).createLeaseCoordinator(this.metricsFactory);
    this.leaseRefresher = this.leaseCoordinator.leaseRefresher();
    this.checkpoint = this.checkpointConfig.checkpointFactory().createCheckpointer(this.leaseCoordinator, this.leaseRefresher);
    this.shardConsumerDispatchPollIntervalMillis = this.coordinatorConfig.shardConsumerDispatchPollIntervalMillis();
    this.parentShardPollIntervalMillis = this.coordinatorConfig.parentShardPollIntervalMillis();
    this.executorService = this.coordinatorConfig.coordinatorFactory().createExecutorService();
    this.diagnosticEventFactory = diagnosticEventFactory;
    this.diagnosticEventHandler = new DiagnosticEventLogger();
    this.shardSyncTaskManagerProvider = (streamIdentifier) -> this.leaseManagementConfig.leaseManagementFactory(leaseSerializer, isMultiStreamMode).createShardSyncTaskManager(this.metricsFactory, this.currentStreamConfigMap.get(streamIdentifier));
    this.shardPrioritization = this.coordinatorConfig.shardPrioritization();
    this.cleanupLeasesUponShardCompletion = this.leaseManagementConfig.cleanupLeasesUponShardCompletion();
    this.skipShardSyncAtWorkerInitializationIfLeasesExist = this.coordinatorConfig.skipShardSyncAtWorkerInitializationIfLeasesExist();
    if (coordinatorConfig.gracefulShutdownCoordinator() != null) {
      this.gracefulShutdownCoordinator = coordinatorConfig.gracefulShutdownCoordinator();
    } else {
      this.gracefulShutdownCoordinator = this.coordinatorConfig.coordinatorFactory().createGracefulShutdownCoordinator();
    }
    if (coordinatorConfig.workerStateChangeListener() != null) {
      this.workerStateChangeListener = coordinatorConfig.workerStateChangeListener();
    } else {
      this.workerStateChangeListener = this.coordinatorConfig.coordinatorFactory().createWorkerStateChangeListener();
    }
    this.leaderDecider = new DeterministicShuffleShardSyncLeaderDecider(leaseRefresher, Executors.newSingleThreadScheduledExecutor(), PERIODIC_SHARD_SYNC_MAX_WORKERS_DEFAULT);
    this.failoverTimeMillis = this.leaseManagementConfig.failoverTimeMillis();
    this.taskBackoffTimeMillis = this.lifecycleConfig.taskBackoffTimeMillis();
    this.listShardsBackoffTimeMillis = this.retrievalConfig.listShardsBackoffTimeInMillis();
    this.maxListShardsRetryAttempts = this.retrievalConfig.maxListShardsRetryAttempts();
    this.shardDetectorProvider = (streamIdentifier) -> createOrGetShardSyncTaskManager(streamIdentifier).shardDetector();
    this.ignoreUnexpetedChildShards = this.leaseManagementConfig.ignoreUnexpectedChildShards();
    this.aggregatorUtil = this.lifecycleConfig.aggregatorUtil();
    this.hierarchicalShardSyncer = leaseManagementConfig.hierarchicalShardSyncer(isMultiStreamMode);
    this.schedulerInitializationBackoffTimeMillis = this.coordinatorConfig.schedulerInitializationBackoffTimeMillis();
    this.leaderElectedPeriodicShardSyncManager = buildPeriodicShardSyncManager();
  }

  /**
     * Start consuming data from the stream, and pass it to the application record processors.
     */
  @Override public void run() {
    if (shutdown) {
      return;
    }
    try {
      initialize();
      log.info("Initialization complete. Starting worker loop.");
    } catch (RuntimeException e) {
      log.error("Unable to initialize after {} attempts. Shutting down.", maxInitializationAttempts, e);
      workerStateChangeListener.onAllInitializationAttemptsFailed(e);
      shutdown();
    }
    while (!shouldShutdown()) {
      runProcessLoop();
    }
    finalShutdown();
    log.info("Worker loop is complete. Exiting from worker.");
  }

  @VisibleForTesting void initialize() {
    synchronized (lock) {
      registerErrorHandlerForUndeliverableAsyncTaskExceptions();
      workerStateChangeListener.onWorkerStateChange(WorkerStateChangeListener.WorkerState.INITIALIZING);
      boolean isDone = false;
      Exception lastException = null;
      for (int i = 0; (!isDone) && (i < maxInitializationAttempts); i++) {
        try {
          log.info("Initialization attempt {}", (i + 1));
          log.info("Initializing LeaseCoordinator");
          leaseCoordinator.initialize();
          TaskResult result;
          if (!skipShardSyncAtWorkerInitializationIfLeasesExist || leaseRefresher.isLeaseTableEmpty()) {
            for (Map.Entry<StreamIdentifier, StreamConfig> streamConfigEntry : currentStreamConfigMap.entrySet()) {
              final StreamIdentifier streamIdentifier = streamConfigEntry.getKey();
              log.info("Syncing Kinesis shard info for " + streamIdentifier);
              final StreamConfig streamConfig = streamConfigEntry.getValue();
              ShardSyncTask shardSyncTask = new ShardSyncTask(shardDetectorProvider.apply(streamIdentifier), leaseRefresher, streamConfig.initialPositionInStreamExtended(), cleanupLeasesUponShardCompletion, ignoreUnexpetedChildShards, 0L, hierarchicalShardSyncer, metricsFactory);
              result = new MetricsCollectingTaskDecorator(shardSyncTask, metricsFactory).call();
              if (result.getException() != null) {
                log.error("Caught exception when sync\'ing info for " + streamIdentifier, result.getException());
                throw result.getException();
              }
            }

<<<<<<< Unknown file: This is a bug in JDime.
=======
            result = leaderElectedPeriodicShardSyncManager.syncShardsOnce();
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/right.java
          } else {
            log.info("Skipping shard sync per configuration setting (and lease table is not empty)");
          }
          if (!leaseCoordinator.isRunning()) {
            log.info(
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/left.java
            "Starting LeaseCoordinator"
=======
            "Scheduling periodicShardSync)"
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/6c4297f5b37241db392f5a8e9bc2f3c02eedc5f6/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/Scheduler.java/right.java
            );
            waitUntilHashRangeCovered();
            leaseCoordinator.start();
          } else {
            log.info("LeaseCoordinator is already running. No need to start it.");
          }
          isDone = true;
        } catch (LeasingException e) {
          log.error("Caught exception when initializing LeaseCoordinator", e);
          lastException = e;
        } catch (Exception e) {
          lastException = e;
        }
        if (!isDone) {
          try {
            Thread.sleep(schedulerInitializationBackoffTimeMillis);
            leaderElectedPeriodicShardSyncManager.stop();
          } catch (InterruptedException e) {
            log.debug("Sleep interrupted while initializing worker.");
          }
        }
      }
      if (!isDone) {
        throw new RuntimeException(lastException);
      }
      workerStateChangeListener.onWorkerStateChange(WorkerStateChangeListener.WorkerState.STARTED);
    }
  }

  @VisibleForTesting void waitUntilLeaseTableIsReady() throws InterruptedException, DependencyException, ProvisionedThroughputException, InvalidStateException {
    long waitTime = ThreadLocalRandom.current().nextLong(MIN_WAIT_TIME_FOR_LEASE_TABLE_CHECK_MILLIS, MAX_WAIT_TIME_FOR_LEASE_TABLE_CHECK_MILLIS);
    long waitUntil = System.currentTimeMillis() + waitTime;
    while (System.currentTimeMillis() < waitUntil && leaseRefresher.isLeaseTableEmpty()) {
      log.info("Lease table is still empty. Checking again in {} ms", LEASE_TABLE_CHECK_FREQUENCY_MILLIS);
      Thread.sleep(LEASE_TABLE_CHECK_FREQUENCY_MILLIS);
    }
  }

  private void waitUntilHashRangeCovered() throws InterruptedException {
    while (!leaderElectedPeriodicShardSyncManager.hashRangeCovered()) {
      log.info("Hash range is not covered yet. Checking again in {} ms", HASH_RANGE_COVERAGE_CHECK_FREQUENCY_MILLIS);
      Thread.sleep(HASH_RANGE_COVERAGE_CHECK_FREQUENCY_MILLIS);
    }
  }

  @VisibleForTesting void runProcessLoop() {
    try {
      Set<ShardInfo> assignedShards = new HashSet<>();
      final Set<ShardInfo> completedShards = new HashSet<>();
      for (ShardInfo shardInfo : getShardInfoForAssignments()) {
        ShardConsumer shardConsumer = createOrGetShardConsumer(shardInfo, processorConfig.shardRecordProcessorFactory());
        if (shardConsumer.isShutdown() && shardConsumer.shutdownReason().equals(ShutdownReason.SHARD_END)) {
          completedShards.add(shardInfo);
        } else {
          shardConsumer.executeLifecycle();
        }
        assignedShards.add(shardInfo);
      }
      for (ShardInfo completedShard : completedShards) {
        final StreamIdentifier streamIdentifier = getStreamIdentifier(completedShard.streamIdentifierSerOpt());
        if (createOrGetShardSyncTaskManager(streamIdentifier).syncShardAndLeaseInfo()) {
          log.info("Found completed shard, initiated new ShardSyncTak for " + completedShard.toString());
        }
      }
      cleanupShardConsumers(assignedShards);
      logExecutorState();
      slog.info("Sleeping ...");
      Thread.sleep(shardConsumerDispatchPollIntervalMillis);
    } catch (Exception e) {
      log.error("Worker.run caught exception, sleeping for {} milli seconds!", String.valueOf(shardConsumerDispatchPollIntervalMillis), e);
      try {
        Thread.sleep(shardConsumerDispatchPollIntervalMillis);
      } catch (InterruptedException ex) {
        log.info("Worker: sleep interrupted after catching exception ", ex);
      }
    }
    slog.resetInfoLogging();
  }

  /**
     * Returns whether worker can shutdown immediately. Note that this method is called from Worker's {{@link #run()}
     * method before every loop run, so method must do minimum amount of work to not impact shard processing timings.
     *
     * @return Whether worker should shutdown immediately.
     */
  @VisibleForTesting boolean shouldShutdown() {
    if (executorService.isShutdown()) {
      log.error("Worker executor service has been shutdown, so record processors cannot be shutdown.");
      return true;
    }
    if (shutdown) {
      if (shardInfoShardConsumerMap.isEmpty()) {
        log.info("All record processors have been shutdown successfully.");
        return true;
      }
      if ((System.currentTimeMillis() - shutdownStartTimeMillis) >= failoverTimeMillis) {
        log.info("Lease failover time is reached, so forcing shutdown.");
        return true;
      }
    }
    return false;
  }

  /**
     * Requests a graceful shutdown of the worker, notifying record processors, that implement
     * {@link ShutdownNotificationAware}, of the impending shutdown. This gives the record processor a final chance to
     * checkpoint.
     *
     * This will only create a single shutdown future. Additional attempts to start a graceful shutdown will return the
     * previous future.
     *
     * <b>It's possible that a record processor won't be notify before being shutdown. This can occur if the lease is
     * lost after requesting shutdown, but before the notification is dispatched.</b>
     *
     * <h2>Requested Shutdown Process</h2> When a shutdown process is requested it operates slightly differently to
     * allow the record processors a chance to checkpoint a final time.
     * <ol>
     * <li>Call to request shutdown invoked.</li>
     * <li>Worker stops attempting to acquire new leases</li>
     * <li>Record Processor Shutdown Begins
     * <ol>
     * <li>Record processor is notified of the impending shutdown, and given a final chance to checkpoint</li>
     * <li>The lease for the record processor is then dropped.</li>
     * <li>The record processor enters into an idle state waiting for the worker to complete final termination</li>
     * <li>The worker will detect a record processor that has lost it's lease, and will terminate the record processor
     * with {@link ShutdownReason#LEASE_LOST}</li>
     * </ol>
     * </li>
     * <li>The worker will shutdown all record processors.</li>
     * <li>Once all record processors have been terminated, the worker will terminate all owned resources.</li>
     * <li>Once the worker shutdown is complete, the returned future is completed.</li>
     * </ol>
     *
     * @return a future that will be set once the shutdown has completed. True indicates that the graceful shutdown
     *         completed successfully. A false value indicates that a non-exception case caused the shutdown process to
     *         terminate early.
     */
  public Future<Boolean> startGracefulShutdown() {
    synchronized (this) {
      if (gracefulShutdownFuture == null) {
        gracefulShutdownFuture = gracefulShutdownCoordinator.startGracefulShutdown(createGracefulShutdownCallable());
      }
    }
    return gracefulShutdownFuture;
  }

  /**
     * Creates a callable that will execute the graceful shutdown process. This callable can be used to execute graceful
     * shutdowns in your own executor, or execute the shutdown synchronously.
     *
     * @return a callable that run the graceful shutdown process. This may return a callable that return true if the
     *         graceful shutdown has already been completed.
     * @throws IllegalStateException
     *             thrown by the callable if another callable has already started the shutdown process.
     */
  public Callable<Boolean> createGracefulShutdownCallable() {
    if (shutdownComplete()) {
      return () -> true;
    }
    Callable<GracefulShutdownContext> startShutdown = createWorkerShutdownCallable();
    return gracefulShutdownCoordinator.createGracefulShutdownCallable(startShutdown);
  }

  public boolean hasGracefulShutdownStarted() {
    return gracefuleShutdownStarted;
  }

  @VisibleForTesting Callable<GracefulShutdownContext> createWorkerShutdownCallable() {
    return () -> {
      synchronized (this) {
        if (this.gracefuleShutdownStarted) {
          throw new IllegalStateException("Requested shutdown has already been started");
        }
        this.gracefuleShutdownStarted = true;
      }
      leaseCoordinator.stopLeaseTaker();
      Collection<Lease> leases = leaseCoordinator.getAssignments();
      if (leases == null || leases.isEmpty()) {
        this.shutdown();
        return GracefulShutdownContext.SHUTDOWN_ALREADY_COMPLETED;
      }
      CountDownLatch shutdownCompleteLatch = new CountDownLatch(leases.size());
      CountDownLatch notificationCompleteLatch = new CountDownLatch(leases.size());
      for (Lease lease : leases) {
        ShutdownNotification shutdownNotification = new ShardConsumerShutdownNotification(leaseCoordinator, lease, notificationCompleteLatch, shutdownCompleteLatch);
        ShardInfo shardInfo = DynamoDBLeaseCoordinator.convertLeaseToAssignment(lease);
        ShardConsumer consumer = shardInfoShardConsumerMap.get(shardInfo);
        if (consumer != null) {
          consumer.gracefulShutdown(shutdownNotification);
        } else {
          notificationCompleteLatch.countDown();
          shutdownCompleteLatch.countDown();
        }
      }
      return new GracefulShutdownContext(shutdownCompleteLatch, notificationCompleteLatch, this);
    };
  }

  /**
     * Signals worker to shutdown. Worker will try initiating shutdown of all record processors. Note that if executor
     * services were passed to the worker by the user, worker will not attempt to shutdown those resources.
     *
     * <h2>Shutdown Process</h2> When called this will start shutdown of the record processor, and eventually shutdown
     * the worker itself.
     * <ol>
     * <li>Call to start shutdown invoked</li>
     * <li>Lease coordinator told to stop taking leases, and to drop existing leases.</li>
     * <li>Worker discovers record processors that no longer have leases.</li>
     * <li>Worker triggers shutdown with state {@link ShutdownReason#LEASE_LOST}.</li>
     * <li>Once all record processors are shutdown, worker terminates owned resources.</li>
     * <li>Shutdown complete.</li>
     * </ol>
     */
  public void shutdown() {
    synchronized (lock) {
      if (shutdown) {
        log.warn("Shutdown requested a second time.");
        return;
      }
      workerStateChangeListener.onWorkerStateChange(WorkerStateChangeListener.WorkerState.SHUT_DOWN_STARTED);
      log.info("Worker shutdown requested.");
      shutdown = true;
      shutdownStartTimeMillis = System.currentTimeMillis();
      leaseCoordinator.stop();
      leaderElectedPeriodicShardSyncManager.stop();
      workerStateChangeListener.onWorkerStateChange(WorkerStateChangeListener.WorkerState.SHUT_DOWN);
    }
  }

  /**
     * Perform final shutdown related tasks for the worker including shutting down worker owned executor services,
     * threads, etc.
     */
  private void finalShutdown() {
    log.info("Starting worker\'s final shutdown.");
    if (executorService instanceof SchedulerCoordinatorFactory.SchedulerThreadPoolExecutor) {
      executorService.shutdownNow();
    }
    if (metricsFactory instanceof CloudWatchMetricsFactory) {
      ((CloudWatchMetricsFactory) metricsFactory).shutdown();
    }
    shutdownComplete = true;
  }

  private List<ShardInfo> getShardInfoForAssignments() {
    List<ShardInfo> assignedStreamShards = leaseCoordinator.getCurrentAssignments();
    List<ShardInfo> prioritizedShards = shardPrioritization.prioritize(assignedStreamShards);
    if ((prioritizedShards != null) && (!prioritizedShards.isEmpty())) {
      if (slog.isInfoEnabled()) {
        StringBuilder builder = new StringBuilder();
        boolean firstItem = true;
        for (ShardInfo shardInfo : prioritizedShards) {
          if (!firstItem) {
            builder.append(", ");
          }
          builder.append(shardInfo.shardId());
          firstItem = false;
        }
        slog.info("Current stream shard assignments: " + builder.toString());
      }
    } else {
      slog.info("No activities assigned");
    }
    return prioritizedShards;
  }

  /**
     * NOTE: This method is internal/private to the Worker class. It has package access solely for testing.
     *
     * @param shardInfo
     *            Kinesis shard info
     * @return ShardConsumer for the shard
     */
  ShardConsumer createOrGetShardConsumer(@NonNull final ShardInfo shardInfo, @NonNull final ShardRecordProcessorFactory shardRecordProcessorFactory) {
    ShardConsumer consumer = shardInfoShardConsumerMap.get(shardInfo);
    if ((consumer == null) || (consumer.isShutdown() && consumer.shutdownReason().equals(ShutdownReason.LEASE_LOST))) {
      consumer = buildConsumer(shardInfo, shardRecordProcessorFactory);
      shardInfoShardConsumerMap.put(shardInfo, consumer);
      slog.infoForce("Created new shardConsumer for : " + shardInfo);
    }
    return consumer;
  }

  private ShardSyncTaskManager createOrGetShardSyncTaskManager(StreamIdentifier streamIdentifier) {
    return streamToShardSyncTaskManagerMap.computeIfAbsent(streamIdentifier, (s) -> shardSyncTaskManagerProvider.apply(s));
  }

  protected ShardConsumer buildConsumer(@NonNull final ShardInfo shardInfo, @NonNull final ShardRecordProcessorFactory shardRecordProcessorFactory) {
    RecordsPublisher cache = retrievalConfig.retrievalFactory().createGetRecordsCache(shardInfo, metricsFactory);
    ShardRecordProcessorCheckpointer checkpointer = coordinatorConfig.coordinatorFactory().createRecordProcessorCheckpointer(shardInfo, checkpoint);
    final StreamIdentifier streamIdentifier = getStreamIdentifier(shardInfo.streamIdentifierSerOpt());
    final StreamConfig streamConfig = currentStreamConfigMap.get(streamIdentifier);
    Validate.notNull(streamConfig, "StreamConfig should not be empty");
    ShardConsumerArgument argument = new ShardConsumerArgument(shardInfo, streamConfig.streamIdentifier(), leaseCoordinator, executorService, cache, shardRecordProcessorFactory.shardRecordProcessor(streamIdentifier), checkpoint, checkpointer, parentShardPollIntervalMillis, taskBackoffTimeMillis, skipShardSyncAtWorkerInitializationIfLeasesExist, listShardsBackoffTimeMillis, maxListShardsRetryAttempts, processorConfig.callProcessRecordsEvenForEmptyRecordList(), shardConsumerDispatchPollIntervalMillis, streamConfig.initialPositionInStreamExtended(), cleanupLeasesUponShardCompletion, ignoreUnexpetedChildShards, shardDetectorProvider.apply(streamConfig.streamIdentifier()), aggregatorUtil, hierarchicalShardSyncer, metricsFactory);
    return new ShardConsumer(cache, executorService, shardInfo, lifecycleConfig.logWarningForTaskAfterMillis(), argument, lifecycleConfig.taskExecutionListener(), lifecycleConfig.readTimeoutsToIgnoreBeforeWarning());
  }

  private PeriodicShardSyncManager buildPeriodicShardSyncManager() {
    final ShardSyncTask shardSyncTask = new ShardSyncTask(shardDetector, leaseRefresher, initialPosition, cleanupLeasesUponShardCompletion, ignoreUnexpetedChildShards, 0L, hierarchicalShardSyncer, metricsFactory);
    return new PeriodicShardSyncManager(leaseManagementConfig.workerIdentifier(), leaderDecider, shardSyncTask, metricsFactory);
  }

  /**
     * NOTE: This method is internal/private to the Worker class. It has package access solely for testing.
     *
     * This method relies on ShardInfo.equals() method returning true for ShardInfo objects which may have been
     * instantiated with parentShardIds in a different order (and rest of the fields being the equal). For example
     * shardInfo1.equals(shardInfo2) should return true with shardInfo1 and shardInfo2 defined as follows. ShardInfo
     * shardInfo1 = new ShardInfo(shardId1, concurrencyToken1, Arrays.asList("parent1", "parent2")); ShardInfo
     * shardInfo2 = new ShardInfo(shardId1, concurrencyToken1, Arrays.asList("parent2", "parent1"));
     */
  void cleanupShardConsumers(Set<ShardInfo> assignedShards) {
    for (ShardInfo shard : shardInfoShardConsumerMap.keySet()) {
      if (!assignedShards.contains(shard)) {
        ShardConsumer consumer = shardInfoShardConsumerMap.get(shard);
        if (consumer.leaseLost()) {
          shardInfoShardConsumerMap.remove(shard);
          log.debug("Removed consumer for {} as lease has been lost", shard.shardId());
        } else {
          consumer.executeLifecycle();
        }
      }
    }
  }

  /**
     * Exceptions in the RxJava layer can fail silently unless an error handler is set to propagate these exceptions
     * back to the KCL, as is done below.
     */
  private void registerErrorHandlerForUndeliverableAsyncTaskExceptions() {
    RxJavaPlugins.setErrorHandler((t) -> {
      ExecutorStateEvent executorStateEvent = diagnosticEventFactory.executorStateEvent(executorService, leaseCoordinator);
      RejectedTaskEvent rejectedTaskEvent = diagnosticEventFactory.rejectedTaskEvent(executorStateEvent, t);
      rejectedTaskEvent.accept(diagnosticEventHandler);
    });
  }

  private void logExecutorState() {
    ExecutorStateEvent executorStateEvent = diagnosticEventFactory.executorStateEvent(executorService, leaseCoordinator);
    executorStateEvent.accept(diagnosticEventHandler);
  }

  private StreamIdentifier getStreamIdentifier(Optional<String> streamIdentifierString) {
    final StreamIdentifier streamIdentifier;
    if (streamIdentifierString.isPresent()) {
      streamIdentifier = StreamIdentifier.multiStreamInstance(streamIdentifierString.get());
    } else {
      Validate.isTrue(!isMultiStreamMode, "Should not be in MultiStream Mode");
      streamIdentifier = this.currentStreamConfigMap.values().iterator().next().streamIdentifier();
    }
    Validate.notNull(streamIdentifier, "Stream identifier should not be empty");
    return streamIdentifier;
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE) private static class SchedulerLog {
    private long reportIntervalMillis = TimeUnit.MINUTES.toMillis(1);

    private long nextReportTime = System.currentTimeMillis() + reportIntervalMillis;

    private boolean infoReporting;

    void info(Object message) {
      if (this.isInfoEnabled()) {
        log.info("{}", message);
      }
    }

    void infoForce(Object message) {
      log.info("{}", message);
    }

    private boolean isInfoEnabled() {
      return infoReporting;
    }

    private void resetInfoLogging() {
      if (infoReporting) {
        if (log.isInfoEnabled()) {
          infoReporting = false;
          nextReportTime = System.currentTimeMillis() + reportIntervalMillis;
        }
      } else {
        if (nextReportTime <= System.currentTimeMillis()) {
          infoReporting = true;
        }
      }
    }
  }

  @Deprecated public Future<Void> requestShutdown() {
    return null;
  }
}