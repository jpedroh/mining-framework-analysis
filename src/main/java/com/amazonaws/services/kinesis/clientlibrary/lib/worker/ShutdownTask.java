package com.amazonaws.services.kinesis.clientlibrary.lib.worker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.proxies.IKinesisProxy;
import com.amazonaws.services.kinesis.clientlibrary.types.ExtendedSequenceNumber;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.leases.impl.KinesisClientLease;
import com.amazonaws.services.kinesis.leases.interfaces.ILeaseManager;
import com.amazonaws.services.kinesis.metrics.impl.MetricsHelper;
import com.amazonaws.services.kinesis.metrics.interfaces.MetricsLevel;
import com.google.common.annotations.VisibleForTesting;

/**
 * Task for invoking the RecordProcessor shutdown() callback.
 */
class ShutdownTask implements ITask {
  private static final Log LOG = LogFactory.getLog(ShutdownTask.class);

  private static final String RECORD_PROCESSOR_SHUTDOWN_METRIC = "RecordProcessor.shutdown";

  private final ShardInfo shardInfo;

  private final IRecordProcessor recordProcessor;

  private final RecordProcessorCheckpointer recordProcessorCheckpointer;

  private final ShutdownReason reason;

  private final IKinesisProxy kinesisProxy;

  private final ILeaseManager<KinesisClientLease> leaseManager;

  private final InitialPositionInStreamExtended initialPositionInStream;

  private final boolean cleanupLeasesOfCompletedShards;


<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/8358322835a391c38d975fc4c708b19d62e523dd/src/main/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/ShutdownTask.java/left.java
  private final boolean ignoreUnexpectedChildShards;
=======
  private final GetRecordsCache getRecordsCache;
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/8358322835a391c38d975fc4c708b19d62e523dd/src/main/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/ShutdownTask.java/right.java


  private final TaskType taskType = TaskType.SHUTDOWN;

  private final long backoffTimeMillis;

  /**
     * Constructor.
     */
  ShutdownTask(ShardInfo shardInfo, IRecordProcessor recordProcessor, RecordProcessorCheckpointer recordProcessorCheckpointer, ShutdownReason reason, IKinesisProxy kinesisProxy, InitialPositionInStreamExtended initialPositionInStream, boolean cleanupLeasesOfCompletedShards, boolean ignoreUnexpectedChildShards, ILeaseManager<KinesisClientLease> leaseManager, long backoffTimeMillis, GetRecordsCache getRecordsCache) {
    this.shardInfo = shardInfo;
    this.recordProcessor = recordProcessor;
    this.recordProcessorCheckpointer = recordProcessorCheckpointer;
    this.reason = reason;
    this.kinesisProxy = kinesisProxy;
    this.initialPositionInStream = initialPositionInStream;
    this.cleanupLeasesOfCompletedShards = cleanupLeasesOfCompletedShards;
    this.ignoreUnexpectedChildShards = ignoreUnexpectedChildShards;
    this.leaseManager = leaseManager;
    this.backoffTimeMillis = backoffTimeMillis;
    this.getRecordsCache = getRecordsCache;
  }

  @Override public TaskResult call() {
    Exception exception;
    boolean applicationException = false;
    try {
      if (reason == ShutdownReason.TERMINATE) {
        recordProcessorCheckpointer.setSequenceNumberAtShardEnd(recordProcessorCheckpointer.getLargestPermittedCheckpointValue());
        recordProcessorCheckpointer.setLargestPermittedCheckpointValue(ExtendedSequenceNumber.SHARD_END);
      }
      LOG.debug("Invoking shutdown() for shard " + shardInfo.getShardId() + ", concurrencyToken " + shardInfo.getConcurrencyToken() + ". Shutdown reason: " + reason);
      final ShutdownInput shutdownInput = new ShutdownInput().withShutdownReason(reason).withCheckpointer(recordProcessorCheckpointer);
      final long recordProcessorStartTimeMillis = System.currentTimeMillis();
      try {
        recordProcessor.shutdown(shutdownInput);
        ExtendedSequenceNumber lastCheckpointValue = recordProcessorCheckpointer.getLastCheckpointValue();
        if (reason == ShutdownReason.TERMINATE) {
          if ((lastCheckpointValue == null) || (!lastCheckpointValue.equals(ExtendedSequenceNumber.SHARD_END))) {
            throw new IllegalArgumentException("Application didn\'t checkpoint at end of shard " + shardInfo.getShardId());
          }
        }
        LOG.debug("Shutting down retrieval strategy.");
        getRecordsCache.shutdown();
        LOG.debug("Record processor completed shutdown() for shard " + shardInfo.getShardId());
      } catch (Exception e) {
        applicationException = true;
        throw e;
      } finally {
        MetricsHelper.addLatency(RECORD_PROCESSOR_SHUTDOWN_METRIC, recordProcessorStartTimeMillis, MetricsLevel.SUMMARY);
      }
      if (reason == ShutdownReason.TERMINATE) {
        LOG.debug("Looking for child shards of shard " + shardInfo.getShardId());
        ShardSyncer.checkAndCreateLeasesForNewShards(kinesisProxy, leaseManager, initialPositionInStream, cleanupLeasesOfCompletedShards, ignoreUnexpectedChildShards);
        LOG.debug("Finished checking for child shards of shard " + shardInfo.getShardId());
      }
      return new TaskResult(null);
    } catch (Exception e) {
      if (applicationException) {
        LOG.error("Application exception. ", e);
      } else {
        LOG.error("Caught exception: ", e);
      }
      exception = e;
      try {
        Thread.sleep(this.backoffTimeMillis);
      } catch (InterruptedException ie) {
        LOG.debug("Interrupted sleep", ie);
      }
    }
    return new TaskResult(exception);
  }

  @Override public TaskType getTaskType() {
    return taskType;
  }

  @VisibleForTesting ShutdownReason getReason() {
    return reason;
  }
}