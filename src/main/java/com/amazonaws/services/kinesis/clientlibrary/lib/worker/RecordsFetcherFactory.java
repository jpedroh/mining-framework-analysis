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
  GetRecordsCache createRecordsFetcher(GetRecordsRetrievalStrategy getRecordsRetrievalStrategy);

  void setMaxSize(int maxSize);

  void setMaxByteSize(int maxByteSize);

  void setMaxRecordsCount(int maxRecordsCount);

  void setDataFetchingStrategy(DataFetchingStrategy dataFetchingStrategy);

  void setMetricsFactory(IMetricsFactory metricsFactory);

  void setIdleMillisBetweenCalls(long idleMillisBetweenCalls);
}