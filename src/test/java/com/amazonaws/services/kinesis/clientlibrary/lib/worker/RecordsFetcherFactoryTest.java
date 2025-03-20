package com.amazonaws.services.kinesis.clientlibrary.lib.worker;
import com.amazonaws.services.kinesis.metrics.impl.NullMetricsFactory;
import com.amazonaws.services.kinesis.metrics.interfaces.IMetricsFactory;
import org.junit.Before;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RecordsFetcherFactoryTest {
  private RecordsFetcherFactory recordsFetcherFactory;

  @Mock private 
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/left.java
  IMetricsFactory
=======
  String
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/right.java
   
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/left.java
  metricsFactory
=======
  shardId = "TestShard"
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/right.java
  ;

  @Mock private GetRecordsRetrievalStrategy getRecordsRetrievalStrategy;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
    recordsFetcherFactory = new SimpleRecordsFetcherFactory(1);
  }

  @Test public void createDefaultRecordsFetcherTest() {
    GetRecordsCache recordsCache = recordsFetcherFactory.createRecordsFetcher(getRecordsRetrievalStrategy, 
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/left.java
    metricsFactory
=======
    shardId
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/right.java
    );
    assertThat(recordsCache, instanceOf(BlockingGetRecordsCache.class));
  }

  @Test public void createPrefetchRecordsFetcherTest() {
    recordsFetcherFactory.setDataFetchingStrategy(DataFetchingStrategy.PREFETCH_CACHED);
    GetRecordsCache recordsCache = recordsFetcherFactory.createRecordsFetcher(getRecordsRetrievalStrategy, 
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/left.java
    metricsFactory
=======
    shardId
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/right.java
    );
    assertThat(recordsCache, instanceOf(PrefetchGetRecordsCache.class));
  }
}