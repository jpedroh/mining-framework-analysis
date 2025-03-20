package com.amazonaws.services.kinesis.clientlibrary.lib.worker;

import com.amazonaws.services.kinesis.metrics.impl.NullMetricsFactory;
import com.amazonaws.services.kinesis.metrics.interfaces.IMetricsFactory;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RecordsFetcherFactoryTest {
    private String shardId = "TestShard";
    private RecordsFetcherFactory recordsFetcherFactory;

    @Mock
    private GetRecordsRetrievalStrategy getRecordsRetrievalStrategy;

    @Mock
    private IMetricsFactory metricsFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        recordsFetcherFactory = new SimpleRecordsFetcherFactory(1);
    }

    @Test
    public void createDefaultRecordsFetcherTest() {
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/left.java
        GetRecordsCache recordsCache = recordsFetcherFactory.createRecordsFetcher(getRecordsRetrievalStrategy, metricsFactory);
||||||| /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/base.java
        GetRecordsCache recordsCache = recordsFetcherFactory.createRecordsFetcher(getRecordsRetrievalStrategy);
=======
        GetRecordsCache recordsCache = recordsFetcherFactory.createRecordsFetcher(getRecordsRetrievalStrategy, shardId);
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/right.java
        assertThat(recordsCache, instanceOf(BlockingGetRecordsCache.class));
    }

    @Test
    public void createPrefetchRecordsFetcherTest() {
        recordsFetcherFactory.setDataFetchingStrategy(DataFetchingStrategy.PREFETCH_CACHED);
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/left.java
        GetRecordsCache recordsCache = recordsFetcherFactory.createRecordsFetcher(getRecordsRetrievalStrategy, metricsFactory);
||||||| /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/base.java
        GetRecordsCache recordsCache = recordsFetcherFactory.createRecordsFetcher(getRecordsRetrievalStrategy);
=======
        GetRecordsCache recordsCache = recordsFetcherFactory.createRecordsFetcher(getRecordsRetrievalStrategy, shardId);
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/81c13d2a3566428a51973caa7cd3069f81bf57de/src/test/java/com/amazonaws/services/kinesis/clientlibrary/lib/worker/RecordsFetcherFactoryTest.java/right.java
        assertThat(recordsCache, instanceOf(PrefetchGetRecordsCache.class));
    }

}
