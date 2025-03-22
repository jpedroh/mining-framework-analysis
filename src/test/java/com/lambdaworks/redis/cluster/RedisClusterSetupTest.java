  package    com . lambdaworks . redis . cluster ;   import static       com . google . code . tempusfugit . temporal . Duration . seconds ;  import static       com . google . code . tempusfugit . temporal . Timeout . timeout ;  import static      com . lambdaworks . redis . cluster . ClusterTestUtil . getNodeId ;  import static      com . lambdaworks . redis . cluster . ClusterTestUtil . getOwnPartition ;  import static      org . assertj . core . api . Assertions . assertThat ;  import static      org . assertj . core . api . Assertions . fail ;  import   java . util . List ;  import    java . util . concurrent . TimeUnit ;  import    java . util . concurrent . TimeoutException ;  import  org . junit .  * ;  import  rx . Subscription ;  import   rx . functions . Func1 ;  import   rx . observers . TestSubscriber ;  import      com . google . code . tempusfugit . temporal . Condition ;  import      com . google . code . tempusfugit . temporal . WaitFor ;  import     com . google . common . collect . Lists ;  import   com . lambdaworks . TestClientResources ;  import   com . lambdaworks . redis .  * ;  import      com . lambdaworks . redis . cluster . event . ClusterTopologyChangedEvent ;  import       com . lambdaworks . redis . cluster . models . partitions . ClusterPartitionParser ;  import       com . lambdaworks . redis . cluster . models . partitions . Partitions ;  import       com . lambdaworks . redis . cluster . models . partitions . RedisClusterNode ;  import     com . lambdaworks . redis . event . Event ;  import     com . lambdaworks . redis . resource . ClientResources ;  import static      org . assertj . core . api . Fail . fail ;  import    java . util . concurrent . ExecutionException ;  import   com . lambdaworks . Connections ;  import   com . lambdaworks . Futures ;  import   com . lambdaworks . Wait ;  import    com . lambdaworks . category . SlowTests ;  import      com . lambdaworks . redis . api . async . RedisAsyncCommands ;  import       com . lambdaworks . redis . cluster . api . async . RedisAdvancedClusterAsyncCommands ;  import       com . lambdaworks . redis . cluster . api . async . RedisClusterAsyncCommands ;  import       com . lambdaworks . redis . cluster . api . sync . RedisAdvancedClusterCommands ;  import       com . lambdaworks . redis . cluster . api . sync . RedisClusterCommands ;    @ SuppressWarnings  (  { "unchecked" , "rawtypes" } ) public  @ SlowTests class RedisClusterSetupTest  extends AbstractTest  {   public static final String  host =  TestSettings . hostAddr  ( ) ;   protected String  key = "key" ;   protected String  value = "value" ;   private static RedisClusterClient  clusterClient ;   private  RedisClusterCommands  < String , String >  redis1 ;   private  RedisClusterCommands  < String , String >  redis2 ;   public static ClientResources  clientResources =  TestClientResources . create  ( ) ;    @ Rule public ClusterRule  clusterRule =  new ClusterRule  ( clusterClient ,  AbstractClusterTest . port5 ,  AbstractClusterTest . port6 ) ;    @ BeforeClass public static void setupClient  ( )  {   clusterClient =  RedisClusterClient . create  ( clientResources ,    RedisURI . Builder . redis  ( host , 
<<<<<<<
port1
=======
 AbstractClusterTest . port5
>>>>>>>
 ) . build  ( ) ) ; 
<<<<<<<
  client1 =  RedisClient . create  ( clientResources ,    RedisURI . Builder . redis  ( host , port1 ) . build  ( ) ) ;
=======
>>>>>>>
 
<<<<<<<
  client2 =  RedisClient . create  ( clientResources ,    RedisURI . Builder . redis  ( host , port2 ) . build  ( ) ) ;
=======
>>>>>>>
 }    @ AfterClass public static void shutdownClient  ( )  {   FastShutdown . shutdown  ( clusterClient ) ; 
<<<<<<<
  FastShutdown . shutdown  ( client1 ) ;
=======
>>>>>>>
 
<<<<<<<
  FastShutdown . shutdown  ( client2 ) ;
=======
>>>>>>>
 }    @ Before public void openConnection  ( )  throws Exception  {   redis1 =  
<<<<<<<
client1
=======
 client . connect  (    RedisURI . Builder . redis  (  AbstractClusterTest . host ,  AbstractClusterTest . port5 ) . build  ( ) )
>>>>>>>
 . 
<<<<<<<
connect
=======
sync
>>>>>>>
  ( ) ;   redis2 =  
<<<<<<<
client2
=======
 client . connect  (    RedisURI . Builder . redis  (  AbstractClusterTest . host ,  AbstractClusterTest . port6 ) . build  ( ) )
>>>>>>>
 . 
<<<<<<<
connect
=======
sync
>>>>>>>
  ( ) ;   clusterRule . clusterReset  ( ) ; }    @ After public void closeConnection  ( )  throws Exception  {   redis1 . close  ( ) ;   redis2 . close  ( ) ; }    @ Test public void clusterMeet  ( )  throws Exception  {   clusterRule . clusterReset  ( ) ;  Partitions  partitionsBeforeMeet =  ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) ;    assertThat  (  partitionsBeforeMeet . getPartitions  ( ) ) . hasSize  ( 1 ) ;  String  result =  redis1 . clusterMeet  ( host ,  AbstractClusterTest . port6 ) ;    assertThat  ( result ) . isEqualTo  ( "OK" ) ;    Wait . untilEquals  ( 2 ,   ( ) ->   ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) . size  ( ) ) . waitOrTimeout  ( redis1 ) ;  Partitions  partitionsAfterMeet =  ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) ;    assertThat  (  partitionsAfterMeet . getPartitions  ( ) ) . hasSize  ( 2 ) ; }    @ Test public void clusterForget  ( )  throws Exception  {   clusterRule . clusterReset  ( ) ;  String  result =  redis1 . clusterMeet  ( host ,  AbstractClusterTest . port6 ) ;    assertThat  ( result ) . isEqualTo  ( "OK" ) ;    Wait . untilTrue  (   ( ) ->   redis1 . clusterNodes  ( ) . contains  (  redis2 . clusterMyId  ( ) ) ) . waitOrTimeout  ( ) ; 
<<<<<<<
=======
   Wait . untilTrue  (   ( ) ->   redis2 . clusterNodes  ( ) . contains  (  redis1 . clusterMyId  ( ) ) ) . waitOrTimeout  ( ) ;
>>>>>>>
    Wait . untilTrue  (   ( ) ->  {  Partitions  partitions =  ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) ;  if  (   partitions . size  ( ) != 2 )  {  return false ; }  for ( RedisClusterNode redisClusterNode : partitions )  {  if  (  redisClusterNode . is  (   RedisClusterNode . NodeFlag . HANDSHAKE ) )  {  return false ; } }  return true ; } ) . waitOrTimeout  ( redis1 ) ;   
<<<<<<<
WaitFor
=======
redis1
>>>>>>>
 . 
<<<<<<<
waitOrTimeout
=======
clusterForget
>>>>>>>
  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  Partitions  partitions =  ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) ;  for ( RedisClusterNode redisClusterNode :  partitions . getPartitions  ( ) )  {  if  (   redisClusterNode . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . HANDSHAKE ) )  {  return false ; } }  return true ; } } ,  
<<<<<<<
timeout
=======
redis2
>>>>>>>
 . clusterMyId  (  seconds  ( 5 ) ) ) ; 
<<<<<<<
 Partitions  partitions =  ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) ;
=======
>>>>>>>
  Partitions  partitionsAfterForget =  ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) ;    assertThat  (  partitionsAfterForget . getPartitions  ( ) ) . hasSize  ( 1 ) ; }    @ Test public void clusterDelSlots  ( )  throws Exception  {   ClusterSetup . setup2Masters  ( clusterRule ) ;   redis1 . clusterDelSlots  ( 1 , 2 , 5 , 6 ) ;   
<<<<<<<
WaitFor
=======
Wait
>>>>>>>
 . 
<<<<<<<
waitOrTimeout
=======
untilEquals
>>>>>>>
  ( 
<<<<<<<
 new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  return     getOwnPartition  ( redis1 ) . getSlots  ( ) . size  ( ) == 11996 ; } }
=======
16380
>>>>>>>
 , 
<<<<<<<
 timeout  (  seconds  ( 5 ) )
=======
  ( ) ->    getOwnPartition  ( redis1 ) . getSlots  ( ) . size  ( )
>>>>>>>
 ) ; }    @ Test public void clusterSetSlots  ( )  throws Exception  {   ClusterSetup setup2Masters  ( clusterRule ) ;   redis1 . clusterSetSlotNode  ( 6 ,  getNodeId  ( redis2 ) ) ;   waitForSlots  ( redis1 , 11999 ) ;   waitForSlots  ( redis2 , 4384 ) ;  Partitions  partitions =  ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) ;  for ( RedisClusterNode redisClusterNode :  partitions . getPartitions  ( ) )  {  if  (   redisClusterNode . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . MYSELF ) )  {     assertThat  (  redisClusterNode . getSlots  ( ) ) . contains  ( 1 , 2 , 3 , 4 , 5 ) . doesNotContain  ( 6 ) ; } } }   private void waitForCluster  (   final  RedisClusterConnection  < String , String > connection )  throws InterruptedException , TimeoutException  {   WaitFor . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  Partitions  partitionsAfterMeet =  ClusterPartitionParser . parse  (  connection . clusterNodes  ( ) ) ;  return    partitionsAfterMeet . getPartitions  ( ) . size  ( ) == 2 ; } } ,  timeout  (  seconds  ( 5 ) ) ) ; } 
<<<<<<<
   @ Test public void clusterAddDelSlots  ( )  throws Exception  {   redis1 . clusterMeet  ( host , port2 ) ;   waitForCluster  ( redis1 ) ;   waitForCluster  ( redis2 ) ;   add6SlotsEach  ( ) ;   waitForSlots  ( redis1 , 6 ) ;   waitForSlots  ( redis2 , 6 ) ;   final  Set  < Integer >  set1 =  ImmutableSet . of  ( 1 , 2 , 3 , 4 , 5 , 6 ) ;   final  Set  < Integer >  set2 =  ImmutableSet . of  ( 7 , 8 , 9 , 10 , 11 , 12 ) ;   deleteSlots  ( redis1 , set1 ) ;   deleteSlots  ( redis2 , set2 ) ;   verifyDeleteSlots  ( redis1 , set1 ) ;   verifyDeleteSlots  ( redis2 , set2 ) ; }
=======
>>>>>>>
    @ Test public void clusterTopologyRefresh  ( )  throws Exception  {   clusterClient . setOptions  (     new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( true ) . refreshPeriod  ( 5 ,  TimeUnit . SECONDS ) . build  ( ) ) ;   clusterClient . reloadPartitions  ( ) ;   
<<<<<<<
RedisAdvancedClusterAsyncConnection
=======
RedisAdvancedClusterAsyncCommands
>>>>>>>
  < String , String >  clusterConnection =  
<<<<<<<
clusterClient
=======
 clusterClient . connect  ( )
>>>>>>>
 . 
<<<<<<<
connectClusterAsync
=======
async
>>>>>>>
  ( ) ;    assertThat  (  clusterClient . getPartitions  ( ) ) . hasSize  ( 1 ) ;   ClusterSetup . setup2Masters  ( clusterRule ) ;    assertThat  (  clusterClient . getPartitions  ( ) ) . hasSize  ( 2 ) ;   clusterConnection . close  ( ) ; }   protected void setup2Masters  ( )  throws InterruptedException , TimeoutException  {   redis1 . clusterMeet  ( host , port2 ) ;   waitForCluster  ( redis1 ) ;   waitForCluster  ( redis2 ) ;  for (   int  i = 0 ;  i < 12000 ;  i += 5 )  {   redis1 . clusterAddSlots  ( i ,  i + 1 ,  i + 2 ,  i + 3 ,  i + 4 ) ; }  for (   int  i = 12000 ;  i < 16384 ;  i += 4 )  {   redis2 . clusterAddSlots  ( i ,  i + 1 ,  i + 2 ,  i + 3 ) ; }   waitForSlots  ( redis1 , 12000 ) ;   waitForSlots  ( redis2 , 4384 ) ;   WaitFor . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {   clusterClient . reloadPartitions  ( ) ;  if  (    clusterClient . getPartitions  ( ) . size  ( ) == 2 )  {  for ( RedisClusterNode redisClusterNode :  clusterClient . getPartitions  ( ) )  {  if  (    redisClusterNode . getSlots  ( ) . size  ( ) < 4381 )  {  return false ; } }  if  (  !   redis1 . clusterInfo  ( ) . contains  ( "cluster_state:ok" ) )  {  return false ; }  if  (  !   redis2 . clusterInfo  ( ) . contains  ( "cluster_state:ok" ) )  {  return false ; }  return true ; }  return false ; } } ,  timeout  (  seconds  ( 6 ) ) ) ; }    @ Test public void changeTopologyWhileOperations  ( )  throws Exception  {   clusterClient . setOptions  (     new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( true ) . refreshPeriod  ( 1 ,  TimeUnit . SECONDS ) . build  ( ) ) ;   
<<<<<<<
RedisAdvancedClusterAsyncConnection
=======
RedisAdvancedClusterAsyncCommands
>>>>>>>
  < String , String >  clusterConnection =  
<<<<<<<
clusterClient
=======
 clusterClient . connect  ( )
>>>>>>>
 . 
<<<<<<<
connectClusterAsync
=======
async
>>>>>>>
  ( ) ;   ClusterSetup . setup2Masters  ( clusterRule ) ;   assertRoutedExecution  ( clusterConnection ) ;   clusterClient . setOptions  (    new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( false ) . build  ( ) ) ;   shiftAllSlotsToNode1  ( ) ;   assertExecuted  (  clusterConnection . set  ( "A" , "value" ) , 1 ) ;   assertExecuted  (  clusterConnection . set  ( "t" , "value" ) , 2 ) ;   
<<<<<<<
assertExecuted
=======
assertRoutedExecution
>>>>>>>
  ( 
<<<<<<<
 clusterConnection . set  ( "p" , "value" )
=======
clusterConnection
>>>>>>>
 , 2 ) ;   clusterClient . setOptions  (    new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( true ) . build  ( ) ) ;   
<<<<<<<
waitUntilPartition1HasAllSlots
=======
 Wait . untilTrue  (   ( ) ->  {  if  (    clusterClient . getPartitions  ( ) . size  ( ) == 2 )  {  for ( RedisClusterNode redisClusterNode :  clusterClient . getPartitions  ( ) )  {  if  (    redisClusterNode . getSlots  ( ) . size  ( ) > 16380 )  {  return true ; } } }  return false ; } )
>>>>>>>
 . waitOrTimeout  ( ) ;   assertRoutedExecution  ( clusterConnection ) ;   clusterConnection . close  ( ) ; }    @ Test public void changeTopologyEvents  ( )  throws Exception  {   clusterClient . setOptions  (     new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( true ) . refreshPeriod  ( 1 ,  TimeUnit . SECONDS ) . build  ( ) ) ;   TestSubscriber  < ClusterTopologyChangedEvent >  subscriber =  new  TestSubscriber  < ClusterTopologyChangedEvent >  ( ) ;  Subscription  subscription =       clusterClient . getResources  ( ) . eventBus  ( ) . get  ( ) . filter  (  new  Func1  < Event , Boolean >  ( )  {    @ Override public Boolean call  (  Event event )  {  return  event instanceof ClusterTopologyChangedEvent ; } } ) . cast  (  ClusterTopologyChangedEvent . class ) . subscribe  ( subscriber ) ;   RedisAdvancedClusterAsyncConnection  < String , String >  clusterConnection =  clusterClient . connectClusterAsync  ( ) ;   setup2Masters  ( ) ;   assertRoutedExecution  ( clusterConnection ) ;   shiftAllSlotsToNode1  ( ) ;   waitUntilPartition1HasAllSlots  ( ) ;   assertRoutedExecution  ( clusterConnection ) ;   List  < ClusterTopologyChangedEvent >  topologyChangedEvents =  subscriber . getOnNextEvents  ( ) ;    assertThat  (  topologyChangedEvents . size  ( ) ) . isGreaterThan  ( 0 ) ;  ClusterTopologyChangedEvent  event =  topologyChangedEvents . get  ( 0 ) ;    assertThat  (   event . before  ( ) . size  ( ) ) . isGreaterThan  ( 0 ) ;    assertThat  (   event . after  ( ) . size  ( ) ) . isGreaterThan  ( 0 ) ;    assertThat  (  event . toString  ( ) ) . contains  (  "before=" +   event . before  ( ) . size  ( ) ) ;   subscription . unsubscribe  ( ) ; }    @ Test public void atLeastOnceForgetNodeFailover  ( )  throws Exception  {   clusterClient . setOptions  (     new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( true ) . refreshPeriod  ( 1 ,  TimeUnit . SECONDS ) . build  ( ) ) ;   
<<<<<<<
RedisAdvancedClusterAsyncConnection
=======
RedisAdvancedClusterAsyncCommands
>>>>>>>
  < String , String >  clusterConnection =  clusterClient . connectClusterAsync  ( ) ;   setup2Masters  ( ) ;   assertRoutedExecution  ( clusterConnection ) ;   clusterClient . setOptions  (    new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( false ) . build  ( ) ) ;   ClusterSetup . setup2Masters  ( clusterRule ) ;   assertRoutedExecution  ( clusterConnection ) ;  RedisClusterNode  partition1 =  getOwnPartition  ( redis1 ) ;  RedisClusterNode  partition2 =  getOwnPartition  ( redis2 ) ;   
<<<<<<<
RedisClusterAsyncConnection
=======
RedisClusterAsyncCommands
>>>>>>>
  < String , String >  node2Connection =  clusterConnection . getConnection  (   partition2 . getUri  ( ) . getHost  ( ) ,   partition2 . getUri  ( ) . getPort  ( ) ) ;   shiftAllSlotsToNode1  ( ) ;   suspendConnection  ( node2Connection ) ;   final  List  <  RedisFuture  < String > >  futures =  Lists . newArrayList  ( ) ;   futures . add  (  clusterConnection . set  ( "t" , "value" ) ) ;   futures . add  (  clusterConnection . set  ( "p" , "value" ) ) ;    clusterConnection . set  ( "A" , "value" ) . get  ( 1 ,  TimeUnit . SECONDS ) ;  for (  RedisFuture  < String > future : futures )  {    assertThat  (  future . isDone  ( ) ) . isFalse  ( ) ;    assertThat  (  future . isCancelled  ( ) ) . isFalse  ( ) ; }   redis1 . clusterForget  (  partition2 . getNodeId  ( ) ) ;   redis2 . clusterForget  (  partition1 . getNodeId  ( ) ) ;   clusterClient . setOptions  (    new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( true ) . build  ( ) ) ;   waitUntilOnlyOnePartition  ( ) ;   
<<<<<<<
WaitFor
=======
 Wait . untilTrue  (   ( ) ->  Futures . areAllCompleted  ( futures ) )
>>>>>>>
 . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  for (  RedisFuture  < String > future : futures )  {  if  (  !  future . isDone  ( ) )  {  return false ; } }  return true ; } } ,  timeout  (  seconds  ( 6 ) ) ) ;   assertRoutedExecution  ( clusterConnection ) ;   clusterConnection . close  ( ) ; }   private void waitUntilOnlyOnePartition  ( )  throws InterruptedException , TimeoutException  {    Wait . untilEquals  ( 1 ,   ( ) ->   clusterClient . getPartitions  ( ) . size  ( ) ) . waitOrTimeout  ( ) ;   
<<<<<<<
WaitFor
=======
 Wait . untilTrue  (   ( ) ->  {  for ( RedisClusterNode redisClusterNode :  clusterClient . getPartitions  ( ) )  {  if  (    redisClusterNode . getSlots  ( ) . size  ( ) > 16380 )  {  return true ; } }  return false ; } )
>>>>>>>
 . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  if  (    clusterClient . getPartitions  ( ) . size  ( ) == 1 )  {  for ( RedisClusterNode redisClusterNode :  clusterClient . getPartitions  ( ) )  {  if  (    redisClusterNode . getSlots  ( ) . size  ( ) > 16380 )  {  return true ; } } }  return false ; } } ,  timeout  (  seconds  ( 6 ) ) ) ; }   private void waitUntilPartition1HasAllSlots  ( )  throws InterruptedException , TimeoutException  {   WaitFor . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  if  (    clusterClient . getPartitions  ( ) . size  ( ) == 2 )  {  for ( RedisClusterNode redisClusterNode :  clusterClient . getPartitions  ( ) )  {  if  (    redisClusterNode . getSlots  ( ) . size  ( ) > 16380 )  {  return true ; } } }  return false ; } } ,  timeout  (  seconds  ( 6 ) ) ) ; }   private void suspendConnection  (   final  RedisClusterAsyncConnection  < String , String > node2Connection )  throws InterruptedException , TimeoutException  {   suspendAutoReconnect  ( node2Connection ) ;   node2Connection . quit  ( ) ;   WaitFor . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  return  !  node2Connection . isOpen  ( ) ; } } ,  timeout  (  seconds  ( 6 ) ) ) ; }   private void assertRoutedExecution  (   RedisAdvancedClusterAsyncConnection  < String , String > clusterConnection )  throws Exception  {   assertExecuted  (  clusterConnection . set  ( "A" , "value" ) , 1 ) ;   assertExecuted  (  clusterConnection . set  ( "t" , "value" ) , 1 ) ;   assertExecuted  (  clusterConnection . set  ( "p" , "value" ) , 1 ) ; }   private void suspendAutoReconnect  (   RedisClusterAsyncConnection  < String , String > connection )  {   ClusterNodeCommandHandler  <  ? ,  ? >  channelWriter =  (  ClusterNodeCommandHandler  <  ? ,  ? > )   (  (  RedisChannelHandler  <  ? ,  ? > ) connection ) . getChannelWriter  ( ) ;   channelWriter . prepareClose  ( ) ; }   protected void shiftAllSlotsToNode1  ( )  throws InterruptedException , TimeoutException  {   redis1 . clusterDelSlots  (  
<<<<<<<
RedisClusterClientTest
=======
AbstractClusterTest
>>>>>>>
 . createSlots  ( 12000 , 16384 ) ) ;   redis2 . clusterDelSlots  (  
<<<<<<<
RedisClusterClientTest
=======
AbstractClusterTest
>>>>>>>
 . createSlots  ( 12000 , 16384 ) ) ;   waitForSlots  ( redis2 , 0 ) ;   final RedisClusterNode  redis2Partition =  getOwnPartition  ( redis2 ) ;   WaitFor . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  Partitions  partitions =  ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) ;  RedisClusterNode  partition =  partitions . getPartitionByNodeId  (  redis2Partition . getNodeId  ( ) ) ;  if  (  !   partition . getSlots  ( ) . isEmpty  ( ) )  {   removeRemaining  ( partition ) ; }  return    partition . getSlots  ( ) . size  ( ) == 0 ; }   private void removeRemaining  (  RedisClusterNode partition )  {  try  {    int  [ ]  ints =  toIntArray  (  partition . getSlots  ( ) ) ;   redis1 . clusterDelSlots  ( 
<<<<<<<
ints
=======
 toIntArray  (  partition . getSlots  ( ) )
>>>>>>>
 ) ; }  catch (   Exception 
<<<<<<<
e
=======
o_O
>>>>>>>
 )  { } } } ,  timeout  (  seconds  ( 10 ) ) ) ;   redis1 . clusterAddSlots  (  RedisClusterClientTest . createSlots  ( 12000 , 16384 ) ) ;   waitForSlots  ( redis1 , 16384 ) ;   
<<<<<<<
WaitFor
=======
 Wait . untilTrue  (  clusterRule :: isStable )
>>>>>>>
 . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  return  clusterRule . isStable  ( ) ; } } ,  timeout  (  seconds  ( 6 ) ) ) ; }   private   int  [ ] toIntArray  (   List  < Integer > 
<<<<<<<
source
=======
list
>>>>>>>
 )  {    int  [ ]  result =  new  int  [  source . size  ( ) ] ;  for (   int  i = 0 ;  i <  source . size  ( ) ;  i ++ )  {    result [ i ] =  source . get  ( i ) ; }  return 
<<<<<<<
result
=======
   list . parallelStream  ( ) . mapToInt  (  Integer :: intValue ) . toArray  ( )
>>>>>>>
 ; }    @ Test public void expireStaleNodeIdConnections  ( )  throws Exception  {   clusterClient . setOptions  (     new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( true ) . refreshPeriod  ( 1 ,  TimeUnit . SECONDS ) . build  ( ) ) ;   
<<<<<<<
RedisAdvancedClusterAsyncConnection
=======
RedisAdvancedClusterAsyncCommands
>>>>>>>
  < String , String >  clusterConnection =  clusterClient . connectClusterAsync  ( ) ;   ClusterSetup . setup2Masters  ( clusterRule ) ;   final  PooledClusterConnectionProvider  < 
<<<<<<<
 ?
=======
String
>>>>>>>
 , 
<<<<<<<
 ?
=======
String
>>>>>>>
 >  clusterConnectionProvider =  getPooledClusterConnectionProvider  ( clusterConnection ) ;    assertThat  (  clusterConnectionProvider . getConnectionCount  ( ) ) . isEqualTo  ( 0 ) ;   assertRoutedExecution  ( clusterConnection ) ;    assertThat  (  clusterConnectionProvider . getConnectionCount  ( ) ) . isEqualTo  ( 2 ) ;  Partitions  partitions =  ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) ;  for ( RedisClusterNode redisClusterNode :  partitions . getPartitions  ( ) )  {  if  (  !   redisClusterNode . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . MYSELF ) )  {   redis1 . clusterForget  (  redisClusterNode . getNodeId  ( ) ) ; } }   partitions =  ClusterPartitionParser . parse  (  redis2 . clusterNodes  ( ) ) ;  for ( RedisClusterNode redisClusterNode :  partitions . getPartitions  ( ) )  {  if  (  !   redisClusterNode . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . MYSELF ) )  {   redis2 . clusterForget  (  redisClusterNode . getNodeId  ( ) ) ; } }   
<<<<<<<
WaitFor
=======
 Wait . untilEquals  ( 1 ,   ( ) ->   clusterClient . getPartitions  ( ) . size  ( ) )
>>>>>>>
 . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  return     clusterClient . getPartitions  ( ) . size  ( ) == 1 &&   clusterConnectionProvider . getConnectionCount  ( ) == 1 ; } } ,  timeout  (  seconds  ( 6 ) ) ) ;    
<<<<<<<
assertThat
=======
Wait
>>>>>>>
 . untilEquals  ( 
<<<<<<<
 clusterConnectionProvider . getConnectionCount  ( )
=======
1
>>>>>>>
 ,   ( ) ->  clusterConnectionProvider . getConnectionCount  ( ) ) . 
<<<<<<<
isEqualTo
=======
waitOrTimeout
>>>>>>>
  ( 1 ) ;   clusterConnection . close  ( ) ; }    @ Test public void doNotExpireStaleNodeIdConnections  ( )  throws Exception  {   clusterClient . setOptions  (      new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( true ) . closeStaleConnections  ( false ) . refreshPeriod  ( 1 ,  TimeUnit . SECONDS ) . build  ( ) ) ;   
<<<<<<<
RedisAdvancedClusterAsyncConnection
=======
RedisAdvancedClusterAsyncCommands
>>>>>>>
  < String , String >  clusterConnection =  
<<<<<<<
clusterClient
=======
 clusterClient . connect  ( )
>>>>>>>
 . 
<<<<<<<
connectClusterAsync
=======
async
>>>>>>>
  ( ) ;   ClusterSetup . setup2Masters  ( clusterRule ) ;   PooledClusterConnectionProvider  < 
<<<<<<<
 ?
=======
String
>>>>>>>
 , 
<<<<<<<
 ?
=======
String
>>>>>>>
 >  clusterConnectionProvider =  getPooledClusterConnectionProvider  ( clusterConnection ) ;    assertThat  (  clusterConnectionProvider . getConnectionCount  ( ) ) . isEqualTo  ( 0 ) ;   assertRoutedExecution  ( clusterConnection ) ;    assertThat  (  clusterConnectionProvider . getConnectionCount  ( ) ) . isEqualTo  ( 2 ) ;  Partitions  partitions =  ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) ;  for ( RedisClusterNode redisClusterNode :  partitions . getPartitions  ( ) )  {  if  (  !   redisClusterNode . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . MYSELF ) )  {   redis1 . clusterForget  (  redisClusterNode . getNodeId  ( ) ) ; } }   partitions =  ClusterPartitionParser . parse  (  redis2 . clusterNodes  ( ) ) ;  for ( RedisClusterNode redisClusterNode :  partitions . getPartitions  ( ) )  {  if  (  !   redisClusterNode . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . MYSELF ) )  {   redis2 . clusterForget  (  redisClusterNode . getNodeId  ( ) ) ; } }   Thread . sleep  ( 2000 ) ;    assertThat  (  clusterConnectionProvider . getConnectionCount  ( ) ) . isEqualTo  ( 2 ) ;   clusterConnection . close  ( ) ; }    @ Test public void expireStaleHostAndPortConnections  ( )  throws Exception  {   clusterClient . setOptions  (     new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( true ) . refreshPeriod  ( 1 ,  TimeUnit . SECONDS ) . build  ( ) ) ;   
<<<<<<<
RedisAdvancedClusterAsyncConnection
=======
RedisAdvancedClusterAsyncCommands
>>>>>>>
  < String , String >  clusterConnection =  clusterClient . connectClusterAsync  ( ) ;   ClusterSetup . setup2Masters  ( clusterRule ) ;   final 
<<<<<<<
PooledClusterConnectionProvider
=======
 PooledClusterConnectionProvider  < String , String >
>>>>>>>
  clusterConnectionProvider =  getPooledClusterConnectionProvider  ( clusterConnection ) ;    assertThat  (  clusterConnectionProvider . getConnectionCount  ( ) ) . isEqualTo  ( 0 ) ;   assertRoutedExecution  ( clusterConnection ) ;    assertThat  (  clusterConnectionProvider . getConnectionCount  ( ) ) . isEqualTo  ( 2 ) ;  for ( RedisClusterNode redisClusterNode :  clusterClient . getPartitions  ( ) )  {   clusterConnection . getConnection  (   redisClusterNode . getUri  ( ) . getHost  ( ) ,   redisClusterNode . getUri  ( ) . getPort  ( ) ) ;   clusterConnection . getConnection  (  redisClusterNode . getNodeId  ( ) ) ; }    assertThat  (  clusterConnectionProvider . getConnectionCount  ( ) ) . isEqualTo  ( 
<<<<<<<
2
=======
4
>>>>>>>
 ) ;  Partitions  partitions =  ClusterPartitionParser . parse  (  redis1 . clusterNodes  ( ) ) ;  for ( RedisClusterNode redisClusterNode :  partitions . getPartitions  ( ) )  {  if  (  !   redisClusterNode . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . MYSELF ) )  {   redis1 . clusterForget  (  redisClusterNode . getNodeId  ( ) ) ; } }   partitions =  ClusterPartitionParser . parse  (  redis2 . clusterNodes  ( ) ) ;  for ( RedisClusterNode redisClusterNode :  partitions . getPartitions  ( ) )  {  if  (  !   redisClusterNode . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . MYSELF ) )  {   redis2 . clusterForget  (  redisClusterNode . getNodeId  ( ) ) ; } }   
<<<<<<<
WaitFor
=======
 Wait . untilEquals  ( 1 ,   ( ) ->   clusterClient . getPartitions  ( ) . size  ( ) )
>>>>>>>
 . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  return    clusterClient . getPartitions  ( ) . size  ( ) == 1 ; } } ,  timeout  (  seconds  ( 6 ) ) ) ;   
<<<<<<<
WaitFor
=======
 Wait . untilEquals  ( 2L ,   ( ) ->  clusterConnectionProvider . getConnectionCount  ( ) )
>>>>>>>
 . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  return   clusterConnectionProvider . getConnectionCount  ( ) == 1 ; } } ,  timeout  (  seconds  ( 6 ) ) ) ;   clusterConnection . close  ( ) ; }    @ Test public void readFromSlaveTest  ( )  throws Exception  {   ClusterSetup . setup2Masters  ( clusterRule ) ;   
<<<<<<<
RedisAdvancedClusterConnection
=======
RedisAdvancedClusterAsyncCommands
>>>>>>>
  < String , String >  clusterConnection =  
<<<<<<<
clusterClient
=======
 clusterClient . connect  ( )
>>>>>>>
 . 
<<<<<<<
connectCluster
=======
async
>>>>>>>
  ( ) ;   
<<<<<<<
clusterConnection
=======
 clusterConnection . getStatefulConnection  ( )
>>>>>>>
 . setReadFrom  (  ReadFrom . SLAVE ) ;   
<<<<<<<
clusterConnection
=======
 clusterConnection . set  ( key , value )
>>>>>>>
 . 
<<<<<<<
set
=======
get
>>>>>>>
  ( key , value ) ;  try  {   clusterConnection . get  ( key ) ; }  catch (   RedisException e )  {    assertThat  ( e ) . hasMessageContaining  ( "Cannot determine a partition to read for slot" ) ; }   clusterConnection . close  ( ) ; }    @ Test public void readFromNearestTest  ( )  throws Exception  {   ClusterSetup . setup2Masters  ( clusterRule ) ;   
<<<<<<<
RedisAdvancedClusterConnection
=======
RedisAdvancedClusterCommands
>>>>>>>
  < String , String >  clusterConnection =  
<<<<<<<
clusterClient
=======
 clusterClient . connect  ( )
>>>>>>>
 . 
<<<<<<<
connectCluster
=======
sync
>>>>>>>
  ( ) ;   
<<<<<<<
clusterConnection
=======
 clusterConnection . getStatefulConnection  ( )
>>>>>>>
 . setReadFrom  (  ReadFrom . NEAREST ) ;   clusterConnection . set  ( key , value ) ;    assertThat  (  clusterConnection . get  ( key ) ) . isEqualTo  ( value ) ;   clusterConnection . close  ( ) ; }   protected  PooledClusterConnectionProvider  <  ? ,  ? > getPooledClusterConnectionProvider  (  RedisAdvancedClusterAsyncConnection clusterAsyncConnection )  {   RedisAdvancedClusterAsyncConnectionImpl  <  ? ,  ? >  clusterConnection =  (  RedisAdvancedClusterAsyncConnectionImpl  <  ? ,  ? > ) clusterAsyncConnection ;   ClusterDistributionChannelWriter  <  ? ,  ? >  writer =  clusterConnection . getWriter  ( ) ;  return  (  PooledClusterConnectionProvider  <  ? ,  ? > )  writer . getClusterConnectionProvider  ( ) ; }   private void assertExecuted  (   RedisFuture  < String > set ,   int execCount )  throws Exception  {   set . get  ( ) ;    assertThat  (  set . getError  ( ) ) . isNull  ( ) ;    assertThat  (  set . get  ( ) ) . isEqualTo  ( "OK" ) ;   ClusterCommand  <  ? ,  ? ,  ? >  command =  (  ClusterCommand  <  ? ,  ? ,  ? > ) set ;    assertThat  (  command . getExecutions  ( ) ) . isEqualTo  ( execCount ) ; }   protected void verifyDeleteSlots  (   final  RedisClusterConnection  < String , String > connection ,   final  Set  < Integer > slots )  {  try  {   WaitFor . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  RedisClusterNode  ownPartition =  getOwnPartition  ( connection ) ;  boolean  condition =   ownPartition . getSlots  ( ) . isEmpty  ( ) ;  if  (  !   ownPartition . getSlots  ( ) . isEmpty  ( ) )  {   deleteSlots  ( connection , slots ) ; }  return condition ; } } ,  timeout  (  seconds  ( 5 ) ) ) ; }  catch (   Exception e )  {  RedisClusterNode  ownPartition =  getOwnPartition  ( connection ) ;   fail  (    "Slots not deleted, Slots on " +  ownPartition . getUri  ( ) + ":" +  ownPartition . getSlots  ( ) , e ) ; } } 
<<<<<<<
  private void deleteSlots  (   RedisClusterConnection  < String , String > connection ,   Set  < Integer > slots )  {  for ( Integer slot : slots )  {   connection . clusterDelSlots  ( slot ) ; } }
=======
>>>>>>>
   private void waitForSlots  (   final  RedisClusterConnection  < String , String > nodeConnection ,   final  int expectedCount )  throws InterruptedException , TimeoutException  {  try  {   WaitFor . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  RedisClusterNode  ownPartition =  getOwnPartition  ( nodeConnection ) ;  return    ownPartition . getSlots  ( ) . size  ( ) == expectedCount ; } } ,  timeout  (  seconds  ( 10 ) ) ) ; }  catch (   Exception e )  {  RedisClusterNode  ownPartition =  getOwnPartition  ( nodeConnection ) ;   fail  (         "Fail on waiting for slots on " +  ownPartition . getUri  ( ) + ", expected count " + expectedCount + ", actual: " +   ownPartition . getSlots  ( ) . size  ( ) + " (" +  ownPartition . getSlots  ( ) + ")" , e ) ; } }    @ Test public void clusterSlotMigrationImport  ( )  throws Exception  {   ClusterSetup . setup2Masters  ( clusterRule ) ;   waitForCluster  ( redis1 ) ; 
<<<<<<<
  waitForCluster  ( redis2 ) ;
=======
>>>>>>>
   waitForSlots  ( redis1 , 6 ) ; 
<<<<<<<
  waitForSlots  ( redis2 , 6 ) ;
=======
>>>>>>>
  String  nodeId2 =  getNodeId  ( redis2 ) ;    assertThat  (  redis1 . clusterSetSlotMigrating  ( 6 , nodeId2 ) ) . isEqualTo  ( "OK" ) ;    assertThat  (  redis2 . clusterSetSlotImporting  ( 
<<<<<<<
6
=======
15000
>>>>>>>
 , nodeId2 ) ) . isEqualTo  ( "OK" ) ;    assertThat  (  redis1 . clusterSetSlotStable  ( 6 ) ) . isEqualTo  ( "OK" ) ; }    @ Test public void clusterSlotMigrationImportWhileOperations  ( )  throws Exception  {   RedisAdvancedClusterAsyncConnection  < String , String >  clusterConnection =  clusterClient . connectClusterAsync  ( ) ;   setup2Masters  ( ) ;  String  nodeId1 =  getNodeId  ( redis1 ) ;  String  nodeId2 =  getNodeId  ( redis2 ) ;    assertThat  (  redis1 . clusterSetSlotMigrating  ( 6373 , nodeId2 ) ) . isEqualTo  ( "OK" ) ;    assertThat  (  redis2 . clusterSetSlotImporting  ( 6373 , nodeId2 ) ) . isEqualTo  ( "OK" ) ;   assertExecuted  (  clusterConnection . set  ( "A" , "value" ) , 2 ) ; }   private static RedisClient  client =  DefaultRedisClient . get  ( ) ;    @ Test public void disconnectedConnectionRejectTest  ( )  throws Exception  {   clusterClient . setOptions  (      new  ClusterClientOptions . Builder  ( ) . refreshClusterView  ( true ) . refreshPeriod  ( 1 ,  TimeUnit . SECONDS ) . disconnectedBehavior  (   ClientOptions . DisconnectedBehavior . REJECT_COMMANDS ) . build  ( ) ) ;   RedisAdvancedClusterAsyncCommands  < String , String >  clusterConnection =   clusterClient . connect  ( ) . async  ( ) ;   clusterClient . setOptions  (     new  ClusterClientOptions . Builder  ( ) . disconnectedBehavior  (   ClientOptions . DisconnectedBehavior . REJECT_COMMANDS ) . refreshClusterView  ( false ) . build  ( ) ) ;   ClusterSetup . setup2Masters  ( clusterRule ) ;   assertRoutedExecution  ( clusterConnection ) ;  RedisClusterNode  partition1 =  getOwnPartition  ( redis1 ) ;   RedisClusterAsyncCommands  < String , String >  node1Connection =  clusterConnection . getConnection  (   partition1 . getUri  ( ) . getHost  ( ) ,   partition1 . getUri  ( ) . getPort  ( ) ) ;   shiftAllSlotsToNode1  ( ) ;   suspendConnection  ( node1Connection ) ;   RedisFuture  < String >  set =  clusterConnection . set  ( "t" , "value" ) ;   set . await  ( 5 ,  TimeUnit . SECONDS ) ;  try  {   set . get  ( ) ;   fail  ( "Missing RedisException" ) ; }  catch (   ExecutionException e )  {     assertThat  ( e ) . hasRootCauseInstanceOf  (  RedisException . class ) . hasMessageContaining  ( "not connected" ) ; }  finally  {   clusterConnection . close  ( ) ; } }   private void assertRoutedExecution  (   RedisClusterAsyncCommands  < String , String > clusterConnection )  throws Exception  {   assertExecuted  (  clusterConnection . set  ( "A" , "value" ) ) ;   assertExecuted  (  clusterConnection . set  ( "t" , "value" ) ) ;   assertExecuted  (  clusterConnection . set  ( "p" , "value" ) ) ; }   protected  PooledClusterConnectionProvider  < String , String > getPooledClusterConnectionProvider  (   RedisAdvancedClusterAsyncCommands  < String , String > clusterAsyncConnection )  {   RedisChannelHandler  < String , String >  channelHandler =  getChannelHandler  ( clusterAsyncConnection ) ;  ClusterDistributionChannelWriter  writer =  ( ClusterDistributionChannelWriter )  channelHandler . getChannelWriter  ( ) ;  return  (  PooledClusterConnectionProvider  < String , String > )  writer . getClusterConnectionProvider  ( ) ; }   private  RedisChannelHandler  < String , String > getChannelHandler  (   RedisAdvancedClusterAsyncCommands  < String , String > clusterAsyncConnection )  {  return  (  RedisChannelHandler  < String , String > )  clusterAsyncConnection . getStatefulConnection  ( ) ; }   private void assertExecuted  (   RedisFuture  < String > set )  throws Exception  {   set . get  ( 5 ,  TimeUnit . SECONDS ) ;    assertThat  (  set . getError  ( ) ) . isNull  ( ) ;    assertThat  (  set . get  ( ) ) . isEqualTo  ( "OK" ) ; }   private void suspendConnection  (   RedisClusterAsyncCommands  < String , String > asyncCommands )  throws InterruptedException , TimeoutException  {    Connections . getConnectionWatchdog  (   (  (  RedisAsyncCommands  <  ? ,  ? > ) asyncCommands ) . getStatefulConnection  ( ) ) . setReconnectSuspended  ( true ) ;   asyncCommands . quit  ( ) ;   WaitFor . waitOrTimeout  (   ( ) ->  !  asyncCommands . isOpen  ( ) ,  timeout  (  seconds  ( 6 ) ) ) ; }   private void waitForSlots  (   RedisClusterCommands  < String , String > connection ,   int slotCount )  throws InterruptedException , TimeoutException  {    Wait . untilEquals  ( slotCount ,   ( ) ->    getOwnPartition  ( connection ) . getSlots  ( ) . size  ( ) ) . waitOrTimeout  ( ) ; } }