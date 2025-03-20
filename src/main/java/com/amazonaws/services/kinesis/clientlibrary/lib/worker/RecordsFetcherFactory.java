package com.amazonaws.services.kinesis.clientlibrary.lib.worker;
import com.amazonaws.services.kinesis.metrics.interfaces.IMetricsFactory;

/**
 * The Amazon Kinesis Client Library will use this to instantiate a record fetcher per shard.
 * Clients may choose to create separate instantiations, or re-use instantiations.
 */
public interface RecordsFetcherFactory {
  /**
     * Returns a records fetcher processor to be used for processing data records for a (assigned) shard.
     *
     * @return Returns a record fetcher object
     */
  GetRecordsCache createRecordsFetcher(GetRecordsRetrievalStrategy getRecordsRetrievalStrategy, 
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/main/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactory.java/left.java
  IMetricsFactory metricsFactory
=======
  String shardId
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/main/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactory.java/right.java
  );

  /**
     * Sets the maximum number of ProcessRecordsInput objects the GetRecordsCache can hold, before further requests are
     * blocked.
     * 
     * @param maxPendingProcessRecordsInput The maximum number of ProcessRecordsInput objects that the cache will accept
     *                                     before blocking.
     */
  void setMaxPendingProcessRecordsInput(int maxPendingProcessRecordsInput);

  void setMaxByteSize(int maxByteSize);

  void setMaxRecordsCount(int maxRecordsCount);

  void setDataFetchingStrategy(DataFetchingStrategy dataFetchingStrategy);

  void setIdleMillisBetweenCalls(long idleMillisBetweenCalls);
}