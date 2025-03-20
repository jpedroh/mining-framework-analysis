  package       com . amazonaws . services . kinesis . clientlibrary . lib . worker ;   import       com . amazonaws . services . kinesis . metrics . impl . NullMetricsFactory ;  import       com . amazonaws . services . kinesis . metrics . interfaces . IMetricsFactory ;  import   org . junit . Before ;  import   org . junit . Test ;  import   org . mockito . Mock ;  import   org . mockito . MockitoAnnotations ;  import static    org . hamcrest . CoreMatchers . instanceOf ;  import static    org . hamcrest . MatcherAssert . assertThat ;   public class RecordsFetcherFactoryTest  {   private RecordsFetcherFactory  recordsFetcherFactory ;    @ Mock private GetRecordsRetrievalStrategy  getRecordsRetrievalStrategy ;    @ Mock private IMetricsFactory  metricsFactory ;    @ Before public void setUp  ( )  {   MockitoAnnotations . initMocks  ( this ) ;   recordsFetcherFactory =  new SimpleRecordsFetcherFactory  ( 1 ) ; }    @ Test public void createDefaultRecordsFetcherTest  ( )  {  GetRecordsCache  recordsCache =  recordsFetcherFactory . createRecordsFetcher  ( getRecordsRetrievalStrategy , 
<<<<<<<
metricsFactory
=======
shardId
>>>>>>>
 ) ;   assertThat  ( recordsCache ,  instanceOf  (  BlockingGetRecordsCache . class ) ) ; }    @ Test public void createPrefetchRecordsFetcherTest  ( )  {   recordsFetcherFactory . setDataFetchingStrategy  (  DataFetchingStrategy . PREFETCH_CACHED ) ;  GetRecordsCache  recordsCache =  recordsFetcherFactory . createRecordsFetcher  ( getRecordsRetrievalStrategy , 
<<<<<<<
metricsFactory
=======
shardId
>>>>>>>
 ) ;   assertThat  ( recordsCache ,  instanceOf  (  PrefetchGetRecordsCache . class ) ) ; }   private String  shardId = "TestShard" ; }