  package    com . lambdaworks . redis . cluster ;   import   java . net . InetSocketAddress ;  import   java . util . List ;  import   java . util . Map ;  import    java . util . concurrent . Future ;  import    java . util . concurrent . TimeUnit ;  import    org . junit . rules . TestRule ;  import    org . junit . runner . Description ;  import     org . junit . runners . model . Statement ;  import     com . google . common . collect . Lists ;  import     com . google . common . collect . Maps ;  import    com . lambdaworks . redis . TestSettings ;  import       com . lambdaworks . redis . cluster . models . partitions . ClusterPartitionParser ;  import       com . lambdaworks . redis . cluster . models . partitions . Partitions ;  import       com . lambdaworks . redis . cluster . models . partitions . RedisClusterNode ;  import    java . util . concurrent . ExecutionException ;  import    java . util . concurrent . TimeoutException ;  import    java . util . function . Function ;  import      com . lambdaworks . redis . api . async . RedisAsyncCommands ;  import      com . lambdaworks . redis . api . sync . RedisCommands ;  import       com . lambdaworks . redis . cluster . api . async . RedisClusterAsyncCommands ;   public class ClusterRule  implements  TestRule  {   private RedisClusterClient  clusterClient ;   private   int  [ ]  ports ;   private  Map  < Integer ,  RedisAsyncCommands  < String , String > >  connectionCache =  Maps . newHashMap  ( ) ;   public ClusterRule  (  RedisClusterClient clusterClient ,   int ...  ports )  {    this . clusterClient = clusterClient ;    this . ports = ports ;  for (  int port : ports )  {   RedisAsyncCommands  < String , String >  connection =   clusterClient . connectToNode  (  new InetSocketAddress  ( "localhost" , port ) ) . async  ( ) ;   connectionCache . put  ( port , connection ) ; } }    @ Override public Statement apply  (   final Statement base ,  Description description )  {   final Statement  beforeCluster =  new Statement  ( )  {    @ Override public void evaluate  ( )  throws Throwable  { 
<<<<<<<
  List  <  Future  <  ? > >  futures =  Lists . newArrayList  ( ) ;
=======
>>>>>>>
   
<<<<<<<
await
=======
flushdb
>>>>>>>
  ( futures ) ; } } ;  return  new Statement  ( )  {    @ Override public void evaluate  ( )  throws Throwable  {   beforeCluster . evaluate  ( ) ;   base . evaluate  ( ) ; } } ; }   private void await  (   List  <  Future  <  ? > > futures )  throws InterruptedException ,    java . util . concurrent . ExecutionException ,    java . util . concurrent . TimeoutException  {  for (  Future  <  ? > future : futures )  {   future . get  ( 10 ,  TimeUnit . SECONDS ) ; } }   public boolean isStable  ( )  {  for ( 
<<<<<<<
 int
=======
 RedisAsyncCommands  < String , String >
>>>>>>>
 
<<<<<<<
port
=======
commands
>>>>>>>
 : 
<<<<<<<
ports
=======
 connectionCache . values  ( )
>>>>>>>
 )  {   RedisAsyncConnectionImpl  < String , String >  connection =  clusterClient . connectAsyncImpl  (  new InetSocketAddress  (  TestSettings . host  ( ) , port ) ) ;  try  {   RedisCommands  < String , String >  sync =   commands . getStatefulConnection  ( ) . sync  ( ) ;  String  info =  
<<<<<<<
 connection . clusterInfo  ( )
=======
sync
>>>>>>>
 . 
<<<<<<<
get
=======
clusterInfo
>>>>>>>
  ( ) ;  if  (   info != null &&  info . contains  ( "cluster_state:ok" ) )  {  String  s =  
<<<<<<<
 connection . clusterNodes  ( )
=======
sync
>>>>>>>
 . 
<<<<<<<
get
=======
clusterNodes
>>>>>>>
  ( ) ;  Partitions  parse =  ClusterPartitionParser . parse  ( s ) ;  for ( RedisClusterNode redisClusterNode : parse )  {  if  (     redisClusterNode . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . FAIL ) ||   redisClusterNode . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . EVENTUAL_FAIL ) ||   redisClusterNode . getFlags  ( ) . contains  (   RedisClusterNode . NodeFlag . HANDSHAKE ) )  {  return false ; } } } else  {  return false ; } }  catch (   Exception e )  { }  finally  {   connection . close  ( ) ; } }  return true ; }   public void flushdb  ( )  { 
<<<<<<<
 try  {  for (  RedisAsyncConnection  <  ? ,  ? > connection :  connectionCache . values  ( ) )  {    connection . flushdb  ( ) . get  ( 10 ,  TimeUnit . SECONDS ) ; } }  catch (   Exception e )  {  throw  new IllegalStateException  ( e ) ; }
=======
  onAllConnections  (  c ->  c . flushdb  ( ) , true ) ;
>>>>>>>
 }   public void clusterReset  ( )  { 
<<<<<<<
 try  {  for (  RedisAsyncConnectionImpl  <  ? ,  ? > connection :  connectionCache . values  ( ) )  {    connection . clusterReset  ( false ) . get  ( 10 ,  TimeUnit . SECONDS ) ;    connection . clusterReset  ( true ) . get  ( 10 ,  TimeUnit . SECONDS ) ;    connection . clusterFlushslots  ( ) . get  ( 10 ,  TimeUnit . SECONDS ) ; } }  catch (   Exception e )  {  throw  new IllegalStateException  ( e ) ; }
=======
  onAllConnections  (  c ->  c . clusterReset  ( true ) ) ;
>>>>>>>
   onAllConnections  (  RedisClusterAsyncCommands :: clusterFlushslots ) ; }   public void meet  (  String host ,   int port )  { 
<<<<<<<
  List  <  Future  <  ? > >  futures =  Lists . newArrayList  ( ) ;
=======
  onAllConnections  (  c ->  c . clusterMeet  ( host , port ) ) ;
>>>>>>>
  for (  RedisAsyncConnectionImpl  <  ? ,  ? > redisAsyncConnection :  connectionCache . values  ( ) )  {   futures . add  (  redisAsyncConnection . clusterMeet  ( host , port ) ) ; }  for (  Future  <  ? > future : futures )  {  try  {   future . get  ( 10 ,  TimeUnit . SECONDS ) ; }  catch (   Exception ignore )  { } } }   public RedisClusterClient getClusterClient  ( )  {  return clusterClient ; }    @ SuppressWarnings  ( "rawtypes" ) private  <  T > void onAllConnections  (   Function  <  RedisClusterAsyncCommands  <  ? ,  ? > ,  Future  < T > > function )  {   onAllConnections  ( function , false ) ; }    @ SuppressWarnings  ( "rawtypes" ) private  <  T > void onAllConnections  (   Function  <  RedisClusterAsyncCommands  <  ? ,  ? > ,  Future  < T > > function ,  boolean ignoreExecutionException )  {   List  <  Future  <  ? > >  futures =  Lists . newArrayList  ( ) ;  for (  RedisClusterAsyncCommands  <  ? ,  ? > connection :  connectionCache . values  ( ) )  {   futures . add  (  function . apply  ( connection ) ) ; }  try  {   await  (  ( List ) futures , ignoreExecutionException ) ; }  catch (   InterruptedException | ExecutionException | TimeoutException e )  {  throw  new IllegalStateException  ( e ) ; } }   private void await  (   List  <  Future  <  ? > > futures ,  boolean ignoreExecutionException )  throws InterruptedException ,    java . util . concurrent . ExecutionException ,    java . util . concurrent . TimeoutException  {  for (  Future  <  ? > future : futures )  {  try  {   future . get  ( 10 ,  TimeUnit . SECONDS ) ; }  catch (   ExecutionException e )  {  if  (  ! ignoreExecutionException )  {  throw e ; } } } } }