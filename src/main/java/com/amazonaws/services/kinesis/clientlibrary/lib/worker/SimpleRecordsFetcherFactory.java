package com.amazonaws.services.kinesis.clientlibrary.lib.worker;
import java.util.concurrent.Executors;
import com.amazonaws.services.kinesis.metrics.interfaces.IMetricsFactory;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog public class SimpleRecordsFetcherFactory implements RecordsFetcherFactory {
  private final int maxRecords;

  private int maxSize = 3;

  private int maxByteSize = 8 * 1024 * 1024;

  private int maxRecordsCount = 30000;

  private DataFetchingStrategy dataFetchingStrategy = DataFetchingStrategy.DEFAULT;


<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/2fc4267b832ae2088fe8f2f84187b9e3ec73677d/src/main/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/SimpleRecordsFetcherFactory.java/left.java
  private IMetricsFactory metricsFactory;
=======
  private long idleMillisBetweenCalls = 1500L;
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/2fc4267b832ae2088fe8f2f84187b9e3ec73677d/src/main/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/SimpleRecordsFetcherFactory.java/right.java


  public SimpleRecordsFetcherFactory(int maxRecords) {
    this.maxRecords = maxRecords;
  }

  @Override public GetRecordsCache createRecordsFetcher(GetRecordsRetrievalStrategy getRecordsRetrievalStrategy) {
    if (dataFetchingStrategy.equals(DataFetchingStrategy.DEFAULT)) {
      return new BlockingGetRecordsCache(maxRecords, getRecordsRetrievalStrategy, idleMillisBetweenCalls);
    } else {
      return new PrefetchGetRecordsCache(maxSize, maxByteSize, maxRecordsCount, maxRecords, getRecordsRetrievalStrategy, Executors.newFixedThreadPool(1), 
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/2fc4267b832ae2088fe8f2f84187b9e3ec73677d/src/main/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/SimpleRecordsFetcherFactory.java/left.java
      metricsFactory
=======
      idleMillisBetweenCalls
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/2fc4267b832ae2088fe8f2f84187b9e3ec73677d/src/main/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/SimpleRecordsFetcherFactory.java/right.java
      );
    }
  }

  @Override public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
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

  @Override public void setMetricsFactory(IMetricsFactory metricsFactory) {
    this.metricsFactory = metricsFactory;
  }

  @Override public void setIdleMillisBetweenCalls(final long idleMillisBetweenCalls) {
    this.idleMillisBetweenCalls = idleMillisBetweenCalls;
  }
}