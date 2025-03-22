  package    com . lambdaworks . redis . cluster ;   import static       com . google . code . tempusfugit . temporal . Duration . seconds ;  import static       com . google . code . tempusfugit . temporal . Timeout . timeout ;  import static      com . lambdaworks . redis . cluster . ClusterTestUtil . getNodeId ;  import static      com . lambdaworks . redis . cluster . ClusterTestUtil . getOwnPartition ;  import static      org . assertj . core . api . Assertions . assertThat ;  import static    org . hamcrest . CoreMatchers . is ;  import static    org . junit . Assert . fail ;  import static    org . junit . Assume . assumeThat ;  import   java . util . ArrayList ;  import   java . util . List ;  import   java . util . Map ;  import    org . junit . runners . MethodSorters ;  import     com . google . common . base . Optional ;  import     com . google . common . collect . ImmutableList ;  import     com . google . common . collect . Lists ;  import     com . google . common . primitives . Ints ;  import       com . lambdaworks . redis . cluster . models . partitions . Partitions ;  import       com . lambdaworks . redis . cluster . models . partitions . RedisClusterNode ;  import     com . lambdaworks . redis . metrics . CommandLatencyCollector ;  import     com . lambdaworks . redis . metrics . CommandLatencyId ;  import     com . lambdaworks . redis . metrics . CommandMetrics ;  import   org . junit . After ;  import   org . junit . AfterClass ;  import   org . junit . Before ;  import   org . junit . BeforeClass ;  import   org . junit . FixMethodOrder ;  import   org . junit . Test ;  import     org . springframework . test . util . ReflectionTestUtils ;  import    com . lambdaworks . redis . FastShutdown ;  import    com . lambdaworks . redis . ReadFrom ;  import    com . lambdaworks . redis . RedisAsyncConnection ;  import    com . lambdaworks . redis . RedisChannelHandler ;  import    com . lambdaworks . redis . RedisClient ;  import    com . lambdaworks . redis . RedisClusterAsyncConnection ;  import    com . lambdaworks . redis . RedisException ;  import    com . lambdaworks . redis . RedisFuture ;  import    com . lambdaworks . redis . RedisURI ;  import    com . lambdaworks . redis . TestSettings ;  import     com . lambdaworks . redis . api . StatefulRedisConnection ;  import      com . lambdaworks . redis . cluster . api . StatefulRedisClusterConnection ;  import       com . lambdaworks . redis . cluster . api . async . RedisAdvancedClusterAsyncCommands ;  import       com . lambdaworks . redis . cluster . api . sync . RedisAdvancedClusterCommands ;  import       com . lambdaworks . redis . cluster . api . sync . RedisClusterCommands ;  import     com . lambdaworks . redis . protocol . AsyncCommand ;    @ FixMethodOrder  (  MethodSorters . NAME_ASCENDING )  @ SuppressWarnings  ( "unchecked" ) public class RedisClusterClientTest  extends AbstractClusterTest  {   protected static RedisClient  client ;   protected  StatefulRedisConnection  < String , String >  redis1 ;   protected  RedisClusterCommands  < String , String >  redissync1 ;   protected  RedisClusterCommands  < String , String >  redissync2 ;   protected  RedisClusterCommands  < String , String >  redissync3 ;   protected  RedisClusterCommands  < String , String >  redissync4 ;    @ BeforeClass public static void setupClient  ( )  throws Exception  {   setupClusterClient  ( ) ;   client =  RedisClient . create  (    RedisURI . Builder . redis  ( host , port1 ) . build  ( ) ) ;   clusterClient =  RedisClusterClient . create  (  ImmutableList . of  (    RedisURI . Builder . redis  ( host , port1 ) . build  ( ) ) ) ; } 
<<<<<<<
  public static   int  [ ] createSlots  (   int from ,   int to )  {    int  [ ]  result =  new  int  [  to - from ] ;   int  counter = 0 ;  for (   int  i = from ;  i < to ;  i ++ )  {    result [  counter ++ ] = i ; }  return result ; }
=======
>>>>>>>
    @ AfterClass public static void shutdownClient  ( )  {   shutdownClusterClient  ( ) ;   FastShutdown . shutdown  ( client ) ; 
<<<<<<<
=======
  FastShutdown . shutdown  ( clusterClient ) ;
>>>>>>>
 }    @ Before public void before  ( )  throws Exception  {    clusterRule . getClusterClient  ( ) . reloadPartitions  ( ) ;   redis1 =  client . 
<<<<<<<
connectAsync
=======
connect
>>>>>>>
  (    RedisURI . Builder . redis  ( host , port1 ) . build  ( ) ) ;   redis2 =  client . connect  (    RedisURI . Builder . redis  ( host , 
<<<<<<<
port1
=======
port2
>>>>>>>
 ) . build  ( ) ) ;   redis3 =  client . connect  (    RedisURI . Builder . redis  ( host , 
<<<<<<<
port2
=======
port3
>>>>>>>
 ) . build  ( ) ) ;   redis4 =  client . connect  (    RedisURI . Builder . redis  ( host , 
<<<<<<<
port3
=======
port4
>>>>>>>
 ) . build  ( ) ) ; 
<<<<<<<
=======
  redissync1 =  redis1 . sync  ( ) ;
>>>>>>>
 
<<<<<<<
=======
  redissync2 =  redis2 . sync  ( ) ;
>>>>>>>
   =  
<<<<<<<
client
=======
redis3
>>>>>>>
 . 
<<<<<<<
connect
=======
sync
>>>>>>>
  (    RedisURI . Builder . redis  ( host , port4 ) . build  ( ) ) ;  
<<<<<<<
 WaitFor . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  return  clusterRule . isStable  ( ) ; } } ,  timeout  (  seconds  ( 5 ) ) ,  new ThreadSleep  (  Duration . millis  ( 500 ) ) )
=======
>>>>>>>
 ;   clusterClient . reloadPartitions  ( ) ;   sync =  clusterClient . connectCluster  ( ) ; }    @ After public void after  ( )  throws Exception  {   sync . close  ( ) ;   redis1 . close  ( ) ;   redissync1 . close  ( ) ;   redissync2 . close  ( ) ;   redissync3 . close  ( ) ;   redissync4 . close  ( ) ; } 
<<<<<<<
   @ Test public void testClusterSlaves  ( )  throws Exception  {   setNode4SlaveOfNode1  ( ) ;   RedisFuture  < Long >  replication =  redis1 . waitForReplication  ( 1 , 5 ) ;    assertThat  (  replication . get  ( ) ) . isGreaterThanOrEqualTo  ( 0L ) ; }
=======
>>>>>>>
    @ Test public void testClusteredOperations  ( )  throws Exception  {   SlotHash . getSlot  (  KEY_B . getBytes  ( ) ) ;   SlotHash . getSlot  (  KEY_A . getBytes  ( ) ) ;   RedisFuture  < String >  result =   redis1 . async  ( ) . set  ( KEY_B , value ) ;    assertThat  (  result . getError  ( ) ) . isEqualTo  ( null ) ;    assertThat  (  redissync1 . set  ( 
<<<<<<<
KEY_A
=======
KEY_B
>>>>>>>
 , "value" ) ) . isEqualTo  ( "OK" ) ;   RedisFuture  < String >  resultMoved =   redis1 . async  ( ) . set  ( KEY_A , value ) ;  try  {   resultMoved . get  ( ) ; }  catch (   Exception e )  {    assertThat  (  e . getMessage  ( ) ) . contains  ( "MOVED 15495" ) ; }   clusterClient . reloadPartitions  ( ) ;   RedisClusterAsyncConnection  < String , String >  connection =  clusterClient . connectClusterAsync  ( ) ;   RedisFuture  < String >  setA =  connection . set  ( KEY_A , value ) ;   setA . get  ( ) ;    assertThat  (  setA . getError  ( ) ) . isNull  ( ) ;    assertThat  (  setA . get  ( ) ) . isEqualTo  ( "OK" ) ;   RedisFuture  < String >  setB =  connection . set  ( KEY_B , "myValue2" ) ;    assertThat  (  setB . get  ( ) ) . isEqualTo  ( "OK" ) ;   RedisFuture  < String >  setD =  connection . set  ( "d" , "myValue2" ) ;    assertThat  (  setD . get  ( ) ) . isEqualTo  ( "OK" ) ;   connection . close  ( ) ; }    @ Test public void testReset  ( )  throws Exception  {   clusterClient . reloadPartitions  ( ) ;   RedisAdvancedClusterAsyncCommandsImpl  < String , String >  connection =  ( RedisAdvancedClusterAsyncCommandsImpl )  clusterClient . connectClusterAsync  ( ) ;   RedisFuture  < String >  setA =  connection . set  ( KEY_A , value ) ;   setA . get  ( ) ; 
<<<<<<<
  RedisFuture  < String >  setB =  connection . set  ( KEY_B , "myValue2" ) ;
=======
>>>>>>>
   connection . reset  ( ) ;   setA =  connection . set  ( KEY_A , "myValue1" ) ;    assertThat  (  setA . getError  ( ) ) . isNull  ( ) ;    assertThat  (  setA . get  ( ) ) . isEqualTo  ( "OK" ) ; 
<<<<<<<
  setB =  connection . set  ( KEY_B , "myValue2" ) ;
=======
>>>>>>>
   connection . close  ( ) ; }    @ Test  @ SuppressWarnings  (  { "rawtypes" } ) public void testClusterRedirection  ( )  throws Exception  {   
<<<<<<<
RedisAdvancedClusterAsyncConnectionImpl
=======
RedisAdvancedClusterAsyncCommands
>>>>>>>
  < String , String >  connection = 
<<<<<<<
 ( RedisAdvancedClusterAsyncConnectionImpl )  clusterClient . connectClusterAsync  ( )
=======
  clusterClient . connect  ( ) . async  ( )
>>>>>>>
 ;  Partitions  partitions =  clusterClient . getPartitions  ( ) ;  for ( RedisClusterNode partition : partitions )  {   partition . setSlots  (  Lists .  < Integer > newArrayList  ( ) ) ;  if  (   partition . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . MYSELF ) )  {    partition . getSlots  ( ) . addAll  (  Ints . asList  (  createSlots  ( 0 , 16384 ) ) ) ; } else  {   partition . setSlots  (  new  ArrayList  < Integer >  ( ) ) ; } }   partitions . updateCache  ( ) ;   connection . setPartitions  ( partitions ) ;   RedisFuture  < String >  setB =  connection . set  ( KEY_B , value ) ;    assertThat  ( setB ) . isInstanceOf  (  AsyncCommand . class ) ;   setB . get  ( ) ;    assertThat  (  setB . getError  ( ) ) . isNull  ( ) ;    assertThat  (  setB . get  ( ) ) . isEqualTo  ( "OK" ) ;   RedisFuture  < String >  setA =  connection . set  ( KEY_A , value ) ;    assertThat  (  setA instanceof AsyncCommand ) . isTrue  ( ) ;   setA . get  ( ) ;    assertThat  (  setA . getError  ( ) ) . isNull  ( ) ;    assertThat  (  setA . get  ( ) ) . isEqualTo  ( "OK" ) ;   connection . close  ( ) ; }    @ Test public void testClusterLatencyMetrics  ( )  throws Exception  {  CommandLatencyCollector  commandLatencyCollector =   clusterClient . getResources  ( ) . commandLatencyCollector  ( ) ;   commandLatencyCollector . retrieveMetrics  ( ) ;   testClusterRedirection  ( ) ;   Map  < CommandLatencyId , CommandMetrics >  metrics =  commandLatencyCollector . retrieveMetrics  ( ) ;  CommandLatencyId  node1 =  findId  ( metrics , port1 ) ;  CommandLatencyId  node3 =  findId  ( metrics , port3 ) ;  CommandMetrics  node1Metrics =  metrics . get  ( node1 ) ;    assertThat  (  node1Metrics . getCount  ( ) ) . isEqualTo  ( 2 ) ;  CommandMetrics  node3Metrics =  metrics . get  ( node3 ) ;    assertThat  (  node3Metrics . getCount  ( ) ) . isEqualTo  ( 1 ) ; }   protected CommandLatencyId findId  (   Map  < CommandLatencyId , CommandMetrics > metrics ,   final  int port )  {   Optional  < CommandLatencyId >  optional =  Iterables . tryFind  (  metrics . keySet  ( ) ,  new  Predicate  < CommandLatencyId >  ( )  {    @ Override public boolean apply  (  CommandLatencyId input )  {  return    input . remoteAddress  ( ) . toString  ( ) . contains  (  ":" + port ) ; } } ) ;  return  optional . orNull  ( ) ; } 
<<<<<<<
   @ Test public void testClusterConnectionStability  ( )  throws Exception  {   RedisAsyncConnectionImpl  < String , String >  connection =  (  RedisAsyncConnectionImpl  < String , String > )  clusterClient . connectClusterAsync  ( ) ;   connection . set  ( KEY_A , KEY_B ) ;   ClusterDistributionChannelWriter  < String , String >  writer =  (  ClusterDistributionChannelWriter  < String , String > )  connection . getChannelWriter  ( ) ;   final  RedisAsyncConnectionImpl  < Object , Object >  backendConnection =   writer . getClusterConnectionProvider  ( ) . getConnection  (   ClusterConnectionProvider . Intent . WRITE , 3300 ) ;   backendConnection . set  ( KEY_A , KEY_B ) ;   backendConnection . close  ( ) ;   WaitFor . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  return   backendConnection . isClosed  ( ) &&  !  backendConnection . isOpen  ( ) ; } } ,  timeout  (  seconds  ( 5 ) ) ) ;    assertThat  (  backendConnection . isClosed  ( ) ) . isTrue  ( ) ;    assertThat  (  backendConnection . isOpen  ( ) ) . isFalse  ( ) ;    assertThat  (  connection . isOpen  ( ) ) . isTrue  ( ) ;    assertThat  (  connection . isClosed  ( ) ) . isFalse  ( ) ;   connection . set  ( KEY_A , KEY_B ) ;   RedisAsyncConnectionImpl  < Object , Object >  backendConnection2 =   writer . getClusterConnectionProvider  ( ) . getConnection  (   ClusterConnectionProvider . Intent . WRITE , 3300 ) ;    assertThat  (  backendConnection2 . isOpen  ( ) ) . isFalse  ( ) ;    assertThat  (  backendConnection2 . isClosed  ( ) ) . isTrue  ( ) ;    assertThat  ( backendConnection2 ) . isSameAs  ( backendConnection ) ;   connection . close  ( ) ; }
=======
>>>>>>>
 
<<<<<<<
   @ Test  (  timeout = 20000 ) public void distributedClusteredAccessAsync  ( )  throws Exception  {   RedisClusterAsyncConnection  < String , String >  connection =  clusterClient . connectClusterAsync  ( ) ;   List  <  RedisFuture  <  ? > >  futures =  Lists . newArrayList  ( ) ;  for (   int  i = 0 ;  i < 100 ;  i ++ )  {   futures . add  (  connection . set  (  KEY_A + i ,  "myValue1" + i ) ) ;   futures . add  (  connection . set  (  KEY_B + i ,  "myValue2" + i ) ) ;   futures . add  (  connection . set  (  "d" + i ,  "myValue3" + i ) ) ; }  for (  RedisFuture  <  ? > future : futures )  {   future . get  ( ) ; }  for (   int  i = 0 ;  i < 100 ;  i ++ )  {   RedisFuture  < String >  setA =  connection . get  (  KEY_A + i ) ;   RedisFuture  < String >  setB =  connection . get  (  KEY_B + i ) ;   RedisFuture  < String >  setD =  connection . get  (  "d" + i ) ;   setA . get  ( ) ;   setB . get  ( ) ;   setD . get  ( ) ;    assertThat  (  setA . getError  ( ) ) . isNull  ( ) ;    assertThat  (  setB . getError  ( ) ) . isNull  ( ) ;    assertThat  (  setD . getError  ( ) ) . isNull  ( ) ;    assertThat  (  setA . get  ( ) ) . isEqualTo  (  "myValue1" + i ) ;    assertThat  (  setB . get  ( ) ) . isEqualTo  (  "myValue2" + i ) ;    assertThat  (  setD . get  ( ) ) . isEqualTo  (  "myValue3" + i ) ; }   connection . close  ( ) ; }
=======
>>>>>>>
 
<<<<<<<
   @ Test  (  timeout = 20000 ) public void distributedClusteredAccessSync  ( )  throws Exception  {   RedisClusterConnection  < String , String >  connection =  clusterClient . connectCluster  ( ) ;  for (   int  i = 0 ;  i < 100 ;  i ++ )  {   connection . set  (  KEY_A + i ,  "myValue1" + i ) ;   connection . set  (  KEY_B + i ,  "myValue2" + i ) ;   connection . set  (  "d" + i ,  "myValue3" + i ) ; }  for (   int  i = 0 ;  i < 100 ;  i ++ )  {    assertThat  (  connection . get  (  KEY_A + i ) ) . isEqualTo  (  "myValue1" + i ) ;    assertThat  (  connection . get  (  KEY_B + i ) ) . isEqualTo  (  "myValue2" + i ) ;    assertThat  (  connection . get  (  "d" + i ) ) . isEqualTo  (  "myValue3" + i ) ; }   connection . close  ( ) ; }
=======
>>>>>>>
 
<<<<<<<
   @ Test public void readOnlyReadWrite  ( )  throws Exception  {   setNode4SlaveOfNode1  ( ) ;   redis1 . set  ( KEY_B , value ) ;  String  resultB =   redis1 . get  ( KEY_B ) . get  ( ) ;    assertThat  ( resultB ) . isEqualTo  ( value ) ;   Thread . sleep  ( 500 ) ;   final  RedisConnection  < String , String >  connect4 =  client . connect  (    RedisURI . Builder . redis  ( host , port4 ) . build  ( ) ) ;  try  {   connect4 . get  ( KEY_B ) ; }  catch (   Exception e )  {    assertThat  ( e ) . hasMessageContaining  ( "MOVED" ) ; }  String  readOnly =  connect4 . readOnly  ( ) ;    assertThat  ( readOnly ) . isEqualTo  ( "OK" ) ;   WaitFor . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  return   connect4 . get  ( KEY_B ) != null ; } } ,  timeout  (  seconds  ( 5 ) ) ) ;  String  resultBViewedBySlave =  connect4 . get  ( KEY_B ) ;    assertThat  ( resultBViewedBySlave ) . isEqualTo  ( value ) ;   connect4 . quit  ( ) ;   resultBViewedBySlave =  connect4 . get  ( KEY_B ) ;    assertThat  ( resultBViewedBySlave ) . isEqualTo  ( value ) ;   connect4 . readWrite  ( ) ;  try  {   connect4 . get  ( KEY_B ) ; }  catch (   Exception e )  {    assertThat  ( e ) . hasMessageContaining  ( "MOVED" ) ; } }
=======
>>>>>>>
 
<<<<<<<
   @ Test public void testNoClusterNodeAvailable  ( )  throws Exception  {  RedisClusterClient  clusterClient =  new RedisClusterClient  (    RedisURI . Builder . redis  ( host , 40400 ) . build  ( ) ) ;  try  {   clusterClient . connectCluster  ( ) ;   fail  ( "Missing RedisException" ) ; }  catch (   RedisException e )  {    assertThat  ( e ) . isInstanceOf  (  RedisException . class ) ; } }
=======
>>>>>>>
    @ Test public void getKeysInSlot  ( )  throws Exception  {   RedisAdvancedClusterConnection  < String , String >  connection =  clusterClient . connectCluster  ( ) ;   
<<<<<<<
connection
=======
sync
>>>>>>>
 . set  ( KEY_A , value ) ;   
<<<<<<<
connection
=======
sync
>>>>>>>
 . set  ( KEY_B , value ) ;   List  < String >  keysA =  
<<<<<<<
connection
=======
sync
>>>>>>>
 . clusterGetKeysInSlot  ( SLOT_A , 10 ) ;    assertThat  ( keysA ) . isEqualTo  (  ImmutableList . of  ( KEY_A ) ) ;   List  < String >  keysB =  
<<<<<<<
connection
=======
sync
>>>>>>>
 . clusterGetKeysInSlot  ( SLOT_B , 10 ) ;    assertThat  ( keysB ) . isEqualTo  (  ImmutableList . of  ( KEY_B ) ) ;   connection . close  ( ) ; }    @ Test public void countKeysInSlot  ( )  throws Exception  {   RedisAdvancedClusterConnection  < String , String >  connection =  clusterClient . connectCluster  ( ) ;   
<<<<<<<
connection
=======
sync
>>>>>>>
 . set  ( KEY_A , value ) ;   
<<<<<<<
connection
=======
sync
>>>>>>>
 . set  ( KEY_B , value ) ;  Long  result =  
<<<<<<<
connection
=======
sync
>>>>>>>
 . clusterCountKeysInSlot  ( SLOT_A ) ;    assertThat  ( result ) . isEqualTo  ( 1L ) ;   result =  
<<<<<<<
connection
=======
sync
>>>>>>>
 . clusterCountKeysInSlot  ( SLOT_B ) ;    assertThat  ( result ) . isEqualTo  ( 1L ) ;   int  slotZZZ =  SlotHash . getSlot  (  "ZZZ" . getBytes  ( ) ) ;   result =  
<<<<<<<
connection
=======
sync
>>>>>>>
 . clusterCountKeysInSlot  ( slotZZZ ) ;    assertThat  ( result ) . isEqualTo  ( 0L ) ;   connection . close  ( ) ; }    @ Test public void testClusterCountFailureReports  ( )  throws Exception  {  RedisClusterNode  ownPartition =  getOwnPartition  ( redissync1 ) ;    assertThat  (  redissync1 . clusterCountFailureReports  (  ownPartition . getNodeId  ( ) ) ) . isGreaterThanOrEqualTo  ( 0 ) ; }    @ Test public void testClusterKeyslot  ( )  throws Exception  {    assertThat  (  redissync1 . clusterKeyslot  ( KEY_A ) ) . isEqualTo  ( SLOT_A ) ;    assertThat  (  SlotHash . getSlot  ( KEY_A ) ) . isEqualTo  ( SLOT_A ) ; }    @ Test public void testClusterSaveconfig  ( )  throws Exception  {    assertThat  (  redissync1 . clusterSaveconfig  ( ) ) . isEqualTo  ( "OK" ) ; }    @ Test public void testClusterSetConfigEpoch  ( )  throws Exception  {  try  {   redissync1 . clusterSetConfigEpoch  ( 1L ) ; }  catch (   RedisException e )  {    assertThat  ( e ) . hasMessageContaining  ( "ERR The user can assign a config epoch only" ) ; } }    @ Test public void testReadFrom  ( )  throws Exception  {   
<<<<<<<
RedisAdvancedClusterConnection
=======
StatefulRedisClusterConnection
>>>>>>>
  < String , String > 
<<<<<<<
 connection =  clusterClient . connectCluster  ( )
=======
 statefulConnection =  sync . getStatefulConnection  ( )
>>>>>>>
 ;    assertThat  (  
<<<<<<<
connection
=======
statefulConnection
>>>>>>>
 . getReadFrom  ( ) ) . isEqualTo  (  ReadFrom . MASTER ) ;   
<<<<<<<
connection
=======
statefulConnection
>>>>>>>
 . setReadFrom  (  ReadFrom . NEAREST ) ;    assertThat  (  
<<<<<<<
connection
=======
statefulConnection
>>>>>>>
 . getReadFrom  ( ) ) . isEqualTo  (  ReadFrom . NEAREST ) ;   connection . close  ( ) ; }    @ Test  (  expected =  IllegalArgumentException . class ) public void testReadFromNull  ( )  throws Exception  {   RedisAdvancedClusterConnection  < String , String >  connection =  clusterClient . connectCluster  ( ) ;   
<<<<<<<
connection
=======
 sync . getStatefulConnection  ( )
>>>>>>>
 . setReadFrom  ( null ) ;   connection . close  ( ) ; }    @ Test public void testPfmerge  ( )  throws Exception  {   RedisAdvancedClusterConnection  < String , String >  connection =  clusterClient . connectCluster  ( ) ;     assertThat  (  SlotHash . getSlot  ( "key2660" ) ) . isEqualTo  (  SlotHash . getSlot  ( "key7112" ) ) . isEqualTo  (  SlotHash . getSlot  ( "key8885" ) ) ;   connection . pfadd  ( "key2660" , "rand" , "mat" ) ;   connection . pfadd  ( "key7112" , "mat" , "perrin" ) ;   connection . pfmerge  ( "key8885" , "key2660" , "key7112" ) ;    assertThat  (  connection . pfcount  ( "key8885" ) ) . isEqualTo  ( 3 ) ;   connection . close  ( ) ; }   protected  StatefulRedisConnection  < String , String >  redis2 ;   protected  StatefulRedisConnection  < String , String >  redis3 ;   protected  StatefulRedisConnection  < String , String >  redis4 ;   protected  RedisAdvancedClusterCommands  < String , String >  sync ;    @ Test public void statefulConnectionFromSync  ( )  throws Exception  {   RedisAdvancedClusterConnection  < String , String >  sync =  clusterClient . connectCluster  ( ) ;    assertThat  (   sync . getStatefulConnection  ( ) . sync  ( ) ) . isSameAs  ( sync ) ; }    @ Test public void statefulConnectionFromAsync  ( )  throws Exception  {   RedisAsyncConnection  < String , String >  async =  client . connectAsync  ( ) ;    assertThat  (   async . getStatefulConnection  ( ) . async  ( ) ) . isSameAs  ( async ) ; }    @ Test public void reloadPartitions  ( )  throws Exception  {    assertThat  (  clusterClient . getPartitions  ( ) ) . hasSize  ( 4 ) ;   assertThat  (    clusterClient . getPartitions  ( ) . getPartition  ( 0 ) . getUri  ( ) ) ;   assertThat  (    clusterClient . getPartitions  ( ) . getPartition  ( 1 ) . getUri  ( ) ) ;   assertThat  (    clusterClient . getPartitions  ( ) . getPartition  ( 2 ) . getUri  ( ) ) ;   assertThat  (    clusterClient . getPartitions  ( ) . getPartition  ( 3 ) . getUri  ( ) ) ;   clusterClient . reloadPartitions  ( ) ;   assertThat  (    clusterClient . getPartitions  ( ) . getPartition  ( 0 ) . getUri  ( ) ) ;   assertThat  (    clusterClient . getPartitions  ( ) . getPartition  ( 1 ) . getUri  ( ) ) ;   assertThat  (    clusterClient . getPartitions  ( ) . getPartition  ( 2 ) . getUri  ( ) ) ;   assertThat  (    clusterClient . getPartitions  ( ) . getPartition  ( 3 ) . getUri  ( ) ) ; }    @ Test  @ SuppressWarnings  (  { "rawtypes" } ) public void testClusterCommandRedirection  ( )  throws Exception  {   RedisAdvancedClusterAsyncCommands  < String , String >  connection =   clusterClient . connect  ( ) . async  ( ) ;    assertThat  (   connection . set  ( KEY_B , value ) . get  ( ) ) . isEqualTo  ( "OK" ) ;    assertThat  (   connection . set  ( KEY_A , value ) . get  ( ) ) . isEqualTo  ( "OK" ) ;   connection . close  ( ) ; }    @ Test  (  expected =  RedisException . class ) public void closeConnection  ( )  throws Exception  {  try  (   RedisAdvancedClusterCommands  < String , String > connection =   clusterClient . connect  ( ) . sync  ( ) )  {   List  < String >  time =  connection . time  ( ) ;    assertThat  ( time ) . hasSize  ( 2 ) ;   connection . close  ( ) ;   connection . time  ( ) ; } }    @ Test public void clusterAuth  ( )  throws Exception  {  RedisClusterClient  clusterClient =  new RedisClusterClient  (     RedisURI . Builder . redis  (  TestSettings . host  ( ) , port7 ) . withPassword  ( "foobared" ) . build  ( ) ) ;  try  (   RedisAdvancedClusterConnection  < String , String > connection =  clusterClient . connectCluster  ( ) )  {   List  < String >  time =  connection . time  ( ) ;    assertThat  ( time ) . hasSize  ( 2 ) ;      connection . getStatefulConnection  ( ) . async  ( ) . quit  ( ) . get  ( ) ;   time =  connection . time  ( ) ;    assertThat  ( time ) . hasSize  ( 2 ) ;    char  [ ]  password =  (   char  [ ] )  ReflectionTestUtils . getField  (  connection . getStatefulConnection  ( ) , "password" ) ;    assertThat  (  new String  ( password ) ) . isEqualTo  ( "foobared" ) ; }  finally  {   FastShutdown . shutdown  ( clusterClient ) ; } }    @ Test  (  expected =  RedisException . class ) public void clusterNeedsAuthButNotSupplied  ( )  throws Exception  {  RedisClusterClient  clusterClient =  new RedisClusterClient  (    RedisURI . Builder . redis  (  TestSettings . host  ( ) , port7 ) . build  ( ) ) ;  try  (   RedisClusterCommands  < String , String > connection =  clusterClient . connectCluster  ( ) )  {   List  < String >  time =  connection . time  ( ) ;    assertThat  ( time ) . hasSize  ( 2 ) ; }  finally  {   FastShutdown . shutdown  ( clusterClient ) ; } }    @ Test public void noClusterNodeAvailable  ( )  throws Exception  {  RedisClusterClient  clusterClient =  new RedisClusterClient  (    RedisURI . Builder . redis  ( host , 40400 ) . build  ( ) ) ;  try  {   clusterClient . connectCluster  ( ) ;   fail  ( "Missing RedisException" ) ; }  catch (   RedisException e )  {    assertThat  ( e ) . isInstanceOf  (  RedisException . class ) ; } }    @ Test public void getClusterNodeConnection  ( )  throws Exception  {  RedisClusterNode  redis1Node =  getOwnPartition  ( redissync2 ) ;   RedisClusterCommands  < String , String >  connection =  sync . getConnection  (  TestSettings . hostAddr  ( ) , port2 ) ;  String  result =  connection . clusterMyId  ( ) ;    assertThat  ( result ) . isEqualTo  (  redis1Node . getNodeId  ( ) ) ; }    @ Test public void operateOnNodeConnection  ( )  throws Exception  {   sync . set  ( KEY_A , value ) ;   sync . set  ( KEY_B , "d" ) ;   StatefulRedisConnection  < String , String >  statefulRedisConnection =   sync . getStatefulConnection  ( ) . getConnection  (  TestSettings . hostAddr  ( ) , port2 ) ;   RedisClusterCommands  < String , String >  connection =  statefulRedisConnection . sync  ( ) ;    assertThat  (  connection . get  ( KEY_A ) ) . isEqualTo  ( value ) ;  try  {   connection . get  ( KEY_B ) ;   fail  ( "missing RedisCommandExecutionException: MOVED" ) ; }  catch (   RedisException e )  {    assertThat  ( e ) . hasMessageContaining  ( "MOVED" ) ; } }    @ Test public void testStatefulConnection  ( )  throws Exception  {   RedisAdvancedClusterAsyncCommands  < String , String >  async =   sync . getStatefulConnection  ( ) . async  ( ) ;    assertThat  (   async . ping  ( ) . get  ( ) ) . isEqualTo  ( "PONG" ) ; }    @ Test  (  expected =  RedisException . class ) public void getButNoPartitionForSlothash  ( )  throws Exception  {  for ( RedisClusterNode redisClusterNode :  clusterClient . getPartitions  ( ) )  {   redisClusterNode . setSlots  (  new  ArrayList  < >  ( ) ) ; }  RedisChannelHandler  rch =  ( RedisChannelHandler )  sync . getStatefulConnection  ( ) ;   ClusterDistributionChannelWriter  < String , String >  writer =  (  ClusterDistributionChannelWriter  < String , String > )  rch . getChannelWriter  ( ) ;   writer . setPartitions  (  clusterClient . getPartitions  ( ) ) ;    clusterClient . getPartitions  ( ) . reload  (   clusterClient . getPartitions  ( ) . getPartitions  ( ) ) ;   sync . get  ( key ) ; }    @ Test public void readOnlyOnCluster  ( )  throws Exception  {   sync . readOnly  ( ) ;   sync . set  ( KEY_B , value ) ;      sync . getStatefulConnection  ( ) . async  ( ) . quit  ( ) . get  ( ) ;    assertThat  (  ReflectionTestUtils . getField  (  sync . getStatefulConnection  ( ) , "readOnly" ) ) . isEqualTo  (  Boolean . TRUE ) ;   sync . readWrite  ( ) ;    assertThat  (  ReflectionTestUtils . getField  (  sync . getStatefulConnection  ( ) , "readOnly" ) ) . isEqualTo  (  Boolean . FALSE ) ;  RedisClusterClient  clusterClient =  new RedisClusterClient  (    RedisURI . Builder . redis  ( host , 40400 ) . build  ( ) ) ;  try  {   clusterClient . connectCluster  ( ) ;   fail  ( "Missing RedisException" ) ; }  catch (   RedisException e )  {    assertThat  ( e ) . isInstanceOf  (  RedisException . class ) ; } } }