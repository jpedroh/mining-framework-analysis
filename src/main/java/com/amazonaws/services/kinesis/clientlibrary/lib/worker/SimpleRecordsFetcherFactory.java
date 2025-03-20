package com.amazonaws.services.kinesis.clientlibrary.lib.worker;
import java.util.concurrent.Executors;
import com.amazonaws.services.kinesis.metrics.interfaces.IMetricsFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog public class SimpleRecordsFetcherFactory implements RecordsFetcherFactory {
  private final int maxRecords;

  private int maxPendingProcessRecordsInput = 3;

  private int maxByteSize = 8 * 1024 * 1024;

  private int maxRecordsCount = 30000;

  private long idleMillisBetweenCalls = 1500L;

  private DataFetchingStrategy dataFetchingStrategy = DataFetchingStrategy.DEFAULT;

  private IMetricsFactory metricsFactory;

  public SimpleRecordsFetcherFactory(int maxRecords) {
    this.maxRecords = maxRecords;
  }

  @Override public GetRecordsCache createRecordsFetcher(GetRecordsRetrievalStrategy getRecordsRetrievalStrategy, 
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/main/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/SimpleRecordsFetcherFactory.java/left.java
  IMetricsFactory metricsFactory
=======
  String shardId
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/main/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/SimpleRecordsFetcherFactory.java/right.java
  ) {
    if (dataFetchingStrategy.equals(DataFetchingStrategy.DEFAULT)) {
      return new BlockingGetRecordsCache(maxRecords, getRecordsRetrievalStrategy, idleMillisBetweenCalls);
    } else {
      return new PrefetchGetRecordsCache(maxPendingProcessRecordsInput, maxByteSize, maxRecordsCount, maxRecords, getRecordsRetrievalStrategy, Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("prefetch-cache-" + shardId + "-%04d").build()), metricsFactory, idleMillisBetweenCalls);
    }
  }

  @Override public void setMaxPendingProcessRecordsInput(int maxPendingProcessRecordsInput) {
    this.maxPendingProcessRecordsInput = maxPendingProcessRecordsInput;
  }

  @Override public void setMaxByteSize(int maxByteSize) {
    this.maxByteSize = maxByteSize;
  }

  @Override public void setMaxRecordsCount(int maxRecordsCount) {
    this.maxRecordsCount = maxRecordsCount;
  }

  @Override public void setDataFetchingStrategy(DataFetchingStrategy dataFetchingStrategy) {
    this.dataFetchingStrategy = dataFetchingStrategy;
  }

  public void setIdleMillisBetweenCalls(final long idleMillisBetweenCalls) {
    this.idleMillisBetweenCalls = idleMillisBetweenCalls;
  }
}