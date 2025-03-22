  package    com . lambdaworks . redis . cluster ;   import   java . net . InetSocketAddress ;  import   java . net . SocketAddress ;  import   java . util . Arrays ;  import   java . util . Iterator ;  import   java . util . List ;  import   java . util . Map ;  import   java . util . Set ;  import     com . google . common . base . Supplier ;  import     com . google . common . cache . CacheBuilder ;  import     com . google . common . cache . CacheLoader ;  import     com . google . common . cache . LoadingCache ;  import     com . google . common . collect . ImmutableMap ;  import     com . google . common . collect . Lists ;  import     com . google . common . collect . Maps ;  import     com . google . common . collect . Sets ;  import     com . google . common . net . HostAndPort ;  import      com . google . common . util . concurrent . UncheckedExecutionException ;  import   com . lambdaworks . redis .  * ;  import    com . lambdaworks . redis . ReadFrom ;  import    com . lambdaworks . redis . RedisChannelHandler ;  import    com . lambdaworks . redis . RedisChannelWriter ;  import       com . lambdaworks . redis . cluster . models . partitions . Partitions ;  import       com . lambdaworks . redis . cluster . models . partitions . RedisClusterNode ;  import     com . lambdaworks . redis . codec . RedisCodec ;  import      com . lambdaworks . redis . models . role . RedisInstance ;  import      com . lambdaworks . redis . models . role . RedisNodeDescription ;  import      io . netty . util . internal . logging . InternalLogger ;  import      io . netty . util . internal . logging . InternalLoggerFactory ;  import  java . util .  * ;  import    java . util . concurrent . ExecutionException ;  import    com . google . common . collect .  * ;  import     com . lambdaworks . redis . api . StatefulConnection ;  import     com . lambdaworks . redis . api . StatefulRedisConnection ;    @ SuppressWarnings  (  { "unchecked" , "rawtypes" } ) class PooledClusterConnectionProvider  <  K ,  V >  implements  ClusterConnectionProvider  {   private static final InternalLogger  logger =  InternalLoggerFactory . getInstance  (  PooledClusterConnectionProvider . class ) ;   private final  LoadingCache  < ConnectionKey ,  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V > >  connections ;   private final boolean  debugEnabled ;   private final  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V >  writers  [ ] =  new 
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  [  SlotHash . SLOT_COUNT ] ;   private final  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V >  readers  [ ] [ ] =  new 
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  [  SlotHash . SLOT_COUNT ]  [ ] ;   private final RedisClusterClient  redisClusterClient ;   private Partitions  partitions ;   private boolean  autoFlushCommands = true ;   private Object  stateLock =  new Object  ( ) ;   private ReadFrom  readFrom ;   public PooledClusterConnectionProvider  (  RedisClusterClient redisClusterClient ,   RedisChannelWriter  < K , V > clusterWriter ,   RedisCodec  < K , V > redisCodec )  {    this . redisClusterClient = redisClusterClient ;    this . debugEnabled =  logger . isDebugEnabled  ( ) ;    this . connections =   CacheBuilder . newBuilder  ( ) . build  (  new  ConnectionFactory  < K , V >  ( redisClusterClient , redisCodec , clusterWriter ) ) ; }    @ Override public  StatefulRedisConnection  < K , V > getConnection  (  Intent intent ,   int slot )  {  if  ( debugEnabled )  {   logger . debug  (     "getConnection(" + intent + ", " + slot + ")" ) ; } 
<<<<<<<
 if  (   intent ==  Intent . READ &&  readFrom != null )  {  return  getReadConnection  ( slot ) ; }
=======
 try  {  if  (   intent ==  Intent . READ &&  readFrom != null )  {  return  getReadConnection  ( slot ) ; }  return  getWriteConnection  ( slot ) ; }  catch (   RedisException e )  {  throw e ; }  catch (   UncheckedExecutionException | ExecutionException e )  {  throw  new RedisException  (  e . getCause  ( ) ) ; }  catch (   RuntimeException e )  {  throw  new RedisException  ( e ) ; }
>>>>>>>
  return  getWriteConnection  ( slot ) ; }   protected  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V > getWriteConnection  (   int slot )  throws ExecutionException  {   
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V >  writer ;  synchronized  ( stateLock )  {   writer =  writers [ slot ] ; }  if  (  writer == null )  {  RedisClusterNode  partition =  partitions . getPartitionBySlot  ( slot ) ;  if  (  partition == null )  {  throw  new RedisException  (     "Cannot determine a partition for slot " + slot + " (Partitions: " + partitions + ")" ) ; } 
<<<<<<<
 try  {  RedisURI  uri =  partition . getUri  ( ) ;  ConnectionKey  key =  new ConnectionKey  (  Intent . WRITE ,  uri . getHost  ( ) ,  uri . getPort  ( ) ) ;  return   writers [ slot ] =  connections . get  ( key ) ; }  catch (   UncheckedExecutionException e )  {  throw  new RedisException  (  e . getCause  ( ) ) ; }  catch (   Exception e )  {  throw  new RedisException  ( e ) ; }
=======
 RedisURI  uri =  partition . getUri  ( ) ;
>>>>>>>
  ConnectionKey  key =  new ConnectionKey  (  Intent . WRITE ,  uri . getHost  ( ) ,  uri . getPort  ( ) ) ;  return   writers [ slot ] =  connections . get  ( key ) ; }  return writer ; }   protected  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V > getReadConnection  (   int slot )  throws ExecutionException  {   
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V >  readerCandidates  [ ] ;  synchronized  ( stateLock )  {   readerCandidates =  readers [ slot ] ; }  if  (  readerCandidates == null )  {  RedisClusterNode  master =  partitions . getPartitionBySlot  ( slot ) ;  if  (  master == null )  {  throw  new RedisException  (     "Cannot determine a partition to read for slot " + slot + " (Partitions: " + partitions + ")" ) ; }   final  List  < RedisNodeDescription >  candidates =  getReadCandidates  ( master ) ;   List  < RedisNodeDescription >  selection =  readFrom . select  (  new  ReadFrom . Nodes  ( )  {    @ Override public  List  < RedisNodeDescription > getNodes  ( )  {  return candidates ; }    @ Override public  Iterator  < RedisNodeDescription > iterator  ( )  {  return  candidates . iterator  ( ) ; } } ) ;  if  (  selection . isEmpty  ( ) )  {  throw  new RedisException  (      "Cannot determine a partition to read for slot " + slot + " (Partitions: " + partitions + ") with setting " + readFrom ) ; }   readerCandidates =  getReadFromConnections  ( selection ) ;    readers [ slot ] = readerCandidates ; }  for (  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V > readerCandidate : readerCandidates )  {  if  (  !  readerCandidate . isOpen  ( ) )  {  continue ; }  return readerCandidate ; }  return  readerCandidates [ 0 ] ; }   private   
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V >  [ ] getReadFromConnections  (   List  < RedisNodeDescription > selection )  throws ExecutionException  {    
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V >  [ ]  readerCandidates ; 
<<<<<<<
 try  {   readerCandidates =  new RedisAsyncConnectionImpl  [  selection . size  ( ) ] ;  for (   int  i = 0 ;  i <  selection . size  ( ) ;  i ++ )  {  RedisNodeDescription  redisClusterNode =  selection . get  ( i ) ;  RedisURI  uri =  redisClusterNode . getUri  ( ) ;  ConnectionKey  key =  new ConnectionKey  (    redisClusterNode . getRole  ( ) ==   RedisInstance . Role . MASTER ?  Intent . WRITE :  Intent . READ ,  uri . getHost  ( ) ,  uri . getPort  ( ) ) ;    readerCandidates [ i ] =  connections . get  ( key ) ; } }  catch (   UncheckedExecutionException e )  {  throw  new RedisException  (  e . getCause  ( ) ) ; }  catch (   Exception e )  {  throw  new RedisException  ( e ) ; }
=======
  readerCandidates =  new StatefulRedisConnection  [  selection . size  ( ) ] ;
>>>>>>>
  for (   int  i = 0 ;  i <  selection . size  ( ) ;  i ++ )  {  RedisNodeDescription  redisClusterNode =  selection . get  ( i ) ;  RedisURI  uri =  redisClusterNode . getUri  ( ) ;  ConnectionKey  key =  new ConnectionKey  (    redisClusterNode . getRole  ( ) ==   RedisInstance . Role . MASTER ?  Intent . WRITE :  Intent . READ ,  uri . getHost  ( ) ,  uri . getPort  ( ) ) ;    readerCandidates [ i ] =  connections . get  ( key ) ; }  return readerCandidates ; }   private  List  < RedisNodeDescription > getReadCandidates  (  RedisClusterNode master )  {   List  < RedisNodeDescription >  candidates =  Lists . newArrayList  ( ) ;  for ( RedisClusterNode partition : partitions )  {  if  (    master . getNodeId  ( ) . equals  (  partition . getNodeId  ( ) ) ||   master . getNodeId  ( ) . equals  (  partition . getSlaveOf  ( ) ) )  {   candidates . add  ( partition ) ; } }  return candidates ; }    @ Override public  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V > getConnection  (  Intent intent ,  String nodeId )  {  if  ( debugEnabled )  {   logger . debug  (     "getConnection(" + intent + ", " + nodeId + ")" ) ; }  try  {  ConnectionKey  key =  new ConnectionKey  ( intent , nodeId ) ;  return  connections . get  ( key ) ; }  catch (   
<<<<<<<
Exception
=======
UncheckedExecutionException
>>>>>>>
 | ExecutionException e )  {  throw  new RedisException  ( 
<<<<<<<
e
=======
 e . getCause  ( )
>>>>>>>
 ) ; } }    @ Override  @ SuppressWarnings  (  { "unchecked" , "hiding" , "rawtypes" } ) public  StatefulRedisConnection  < K , V > getConnection  (  Intent intent ,  String host ,   int port )  {  try  {  if  ( debugEnabled )  {   logger . debug  (       "getConnection(" + intent + ", " + host + ", " + port + ")" ) ; }  if  (  validateClusterNodeMembership  ( ) )  {  RedisClusterNode  redisClusterNode =  getPartition  ( host , port ) ;  if  (  redisClusterNode == null )  {  HostAndPort  hostAndPort =  HostAndPort . fromParts  ( host , port ) ;  throw  invalidConnectionPoint  (  hostAndPort . toString  ( ) ) ; } }  ConnectionKey  key =  new ConnectionKey  ( intent , host , port ) ;  return  connections . get  ( key ) ; }  catch (   RedisException e )  {  throw e ; }  catch (   UncheckedExecutionException | ExecutionException e )  {  throw  new RedisException  (  e . getCause  ( ) ) ; }  catch (   RuntimeException e )  {  throw  new RedisException  ( e ) ; } }   private RedisClusterNode getPartition  (  String host ,   int port )  {  for ( RedisClusterNode partition : partitions )  {  RedisURI  uri =  partition . getUri  ( ) ;  if  (   port ==  uri . getPort  ( ) &&  host . equals  (  uri . getHost  ( ) ) )  {  return partition ; } }  return null ; }    @ Override public void close  ( )  {   ImmutableMap  < ConnectionKey ,  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V > >  copy =  ImmutableMap . copyOf  (   this . connections . asMap  ( ) ) ;    this . connections . invalidateAll  ( ) ;   resetFastConnectionCache  ( ) ;  for (  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V > kvRedisAsyncConnection :  copy . values  ( ) )  {  if  (  kvRedisAsyncConnection . isOpen  ( ) )  {   kvRedisAsyncConnection . close  ( ) ; } } }    @ Override public void reset  ( )  { 
<<<<<<<
  ImmutableMap  < ConnectionKey ,  RedisAsyncConnectionImpl  < K , V > >  copy =  ImmutableMap . copyOf  (   this . connections . asMap  ( ) ) ;
=======
   allConnections  ( ) . forEach  (  StatefulRedisConnection :: reset ) ;
>>>>>>>
  for (  RedisAsyncConnectionImpl  < K , V > kvRedisAsyncConnection :  copy . values  ( ) )  {   kvRedisAsyncConnection . reset  ( ) ; } }    @ Override public void setPartitions  (  Partitions partitions )  {  synchronized  ( stateLock )  {    this . partitions = partitions ;   reconfigurePartitions  ( ) ; } }   private void reconfigurePartitions  ( )  {   Set  < ConnectionKey >  staleConnections =  getStaleConnectionKeys  ( ) ;  for ( ConnectionKey key : staleConnections )  {   
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V >  connection =  connections . getIfPresent  ( key ) ;   RedisChannelHandler  < K , V >  redisChannelHandler =  (  RedisChannelHandler  < K , V > ) connection ;  if  (   
<<<<<<<
connection
=======
redisChannelHandler
>>>>>>>
 . getChannelWriter  ( ) instanceof ClusterNodeCommandHandler )  {   ClusterNodeCommandHandler  <  ? ,  ? >  clusterNodeCommandHandler =  (  ClusterNodeCommandHandler  <  ? ,  ? > )  
<<<<<<<
connection
=======
redisChannelHandler
>>>>>>>
 . getChannelWriter  ( ) ;   clusterNodeCommandHandler . prepareClose  ( ) ; } }   resetFastConnectionCache  ( ) ;  if  (  redisClusterClient . expireStaleConnections  ( ) )  {   closeStaleConnections  ( ) ; } }    @ Override public void closeStaleConnections  ( )  {   logger . debug  ( "closeStaleConnections() count before expiring: {}" ,  getConnectionCount  ( ) ) ;   Set  < ConnectionKey >  stale =  getStaleConnectionKeys  ( ) ;  for ( ConnectionKey connectionKey : stale )  {   
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V >  connection =  connections . getIfPresent  ( connectionKey ) ;  if  (  connection != null )  {   connections . invalidate  ( connectionKey ) ;   connection . close  ( ) ; } }   logger . debug  ( "closeStaleConnections() count after expiring: {}" ,  getConnectionCount  ( ) ) ; }   private  Set  < ConnectionKey > getStaleConnectionKeys  ( )  {   Map  < ConnectionKey ,  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V > >  map =  Maps . newHashMap  (  connections . asMap  ( ) ) ;   Set  < ConnectionKey >  stale =  Sets . newHashSet  ( ) ;  for ( ConnectionKey connectionKey :  map . keySet  ( ) )  {  if  (    connectionKey . nodeId != null &&   partitions . getPartitionByNodeId  (  connectionKey . nodeId ) != null )  {  continue ; }  if  (    connectionKey . host != null &&   getPartition  (  connectionKey . host ,  connectionKey . port ) != null )  {  continue ; }   stale . add  ( connectionKey ) ; }  return stale ; }    @ Override public void setAutoFlushCommands  (  boolean autoFlush )  {  synchronized  ( stateLock )  {    this . autoFlushCommands = autoFlush ; } 
<<<<<<<
 for (  RedisAsyncConnectionImpl  < K , V > connection :   connections . asMap  ( ) . values  ( ) )  {    connection . getChannelWriter  ( ) . setAutoFlushCommands  ( autoFlush ) ; }
=======
   allConnections  ( ) . forEach  (  connection ->  connection . setAutoFlushCommands  ( autoFlush ) ) ;
>>>>>>>
 }    @ Override public void flushCommands  ( )  { 
<<<<<<<
 for (  RedisAsyncConnectionImpl  < K , V > connection :   connections . asMap  ( ) . values  ( ) )  {    connection . getChannelWriter  ( ) . flushCommands  ( ) ; }
=======
   allConnections  ( ) . forEach  (  StatefulConnection :: flushCommands ) ;
>>>>>>>
 }    @ Override public void setReadFrom  (  ReadFrom readFrom )  {  synchronized  ( stateLock )  {    this . readFrom = readFrom ;   Arrays . fill  ( readers , null ) ; } }    @ Override public ReadFrom getReadFrom  ( )  {  return  this . readFrom ; }   protected  long getConnectionCount  ( )  {  return  connections . size  ( ) ; }   protected void resetFastConnectionCache  ( )  {  synchronized  ( stateLock )  {   Arrays . fill  ( writers , null ) ;   Arrays . fill  ( readers , null ) ; } }   private RuntimeException invalidConnectionPoint  (  String message )  {  return  new IllegalArgumentException  (   "Connection to " + message + " not allowed. This connection point is not known in the cluster view" ) ; }   private  Supplier  < SocketAddress > getSocketAddressSupplier  (   final ConnectionKey connectionKey )  {  return 
<<<<<<<
 new  Supplier  < SocketAddress >  ( )  {    @ Override public SocketAddress get  ( )  {  if  (   connectionKey . nodeId != null )  {  return  getSocketAddress  (  connectionKey . nodeId ) ; }  return  new InetSocketAddress  (  connectionKey . host ,  connectionKey . port ) ; } }
=======
  ( ) ->  {  if  (   connectionKey . nodeId != null )  {  return  getSocketAddress  (  connectionKey . nodeId ) ; }  return  new InetSocketAddress  (  connectionKey . host ,  connectionKey . port ) ; }
>>>>>>>
 ; }   protected SocketAddress getSocketAddress  (  String nodeId )  {  for ( RedisClusterNode partition : partitions )  {  if  (   partition . getNodeId  ( ) . equals  ( nodeId ) )  {  return   partition . getUri  ( ) . getResolvedAddress  ( ) ; } }  return null ; }   private static class ConnectionKey  {   private final  ClusterConnectionProvider . Intent  intent ;   private final String  nodeId ;   private final String  host ;   private final  int  port ;   public ConnectionKey  (  Intent intent ,  String nodeId )  {    this . intent = intent ;    this . nodeId = nodeId ;    this . host = null ;    this . port = 0 ; }   public ConnectionKey  (  Intent intent ,  String host ,   int port )  {    this . intent = intent ;    this . host = host ;    this . port = port ;    this . nodeId = null ; }    @ Override public boolean equals  (  Object o )  {  if  (  this == o )  return true ;  if  (  !  (  o instanceof ConnectionKey ) )  return false ;  ConnectionKey  key =  ( ConnectionKey ) o ;  if  (  port !=  key . port )  return false ;  if  (  intent !=  key . intent )  return false ;  if  (   nodeId != null ?  !  nodeId . equals  (  key . nodeId ) :   key . nodeId != null )  return false ;  return  !  (   host != null ?  !  host . equals  (  key . host ) :   key . host != null ) ; }    @ Override public  int hashCode  ( )  {   int  result =   intent != null ?   intent . name  ( ) . hashCode  ( ) : 0 ;   result =   31 * result +  (   nodeId != null ?  nodeId . hashCode  ( ) : 0 ) ;   result =   31 * result +  (   host != null ?  host . hashCode  ( ) : 0 ) ;   result =   31 * result + port ;  return result ; } }   private boolean validateClusterNodeMembership  ( )  {  return    redisClusterClient . getClusterClientOptions  ( ) == null ||   redisClusterClient . getClusterClientOptions  ( ) . isValidateClusterNodeMembership  ( ) ; }   private class ConnectionFactory  <  K ,  V >  extends  CacheLoader  < ConnectionKey ,  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V > >  {   private final RedisClusterClient  redisClusterClient ;   private final  RedisCodec  < K , V >  redisCodec ;   private final  RedisChannelWriter  < K , V >  clusterWriter ;   public ConnectionFactory  (  RedisClusterClient redisClusterClient ,   RedisCodec  < K , V > redisCodec ,   RedisChannelWriter  < K , V > clusterWriter )  {    this . redisClusterClient = redisClusterClient ;    this . redisCodec = redisCodec ;    this . clusterWriter = clusterWriter ; }    @ Override public  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V > load  (  ConnectionKey key )  throws Exception  {   
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V >  connection = null ;  if  (   key . nodeId != null )  {  if  (   partitions . getPartitionByNodeId  (  key . nodeId ) == null )  {  throw  invalidConnectionPoint  (  "node id " +  key . nodeId ) ; }   connection =  redisClusterClient . 
<<<<<<<
connectNode
=======
connectToNode
>>>>>>>
  ( redisCodec ,  key . nodeId , null ,  getSocketAddressSupplier  ( key ) ) ; }  if  (   key . host != null )  {  if  (  validateClusterNodeMembership  ( ) )  {  if  (   getPartition  (  key . host ,  key . port ) == null )  {  throw  invalidConnectionPoint  (    key . host + ":" +  key . port ) ; } }   connection =  redisClusterClient . 
<<<<<<<
connectNode
=======
connectToNode
>>>>>>>
  ( redisCodec ,    key . host + ":" +  key . port , clusterWriter ,  getSocketAddressSupplier  ( key ) ) ; }  if  (   key . intent ==  Intent . READ )  {   
<<<<<<<
connection
=======
 connection . sync  ( )
>>>>>>>
 . readOnly  ( ) ; }  synchronized  ( stateLock )  {   
<<<<<<<
 connection . getChannelWriter  ( )
=======
connection
>>>>>>>
 . setAutoFlushCommands  ( autoFlushCommands ) ; }  return connection ; } }   protected  Collection  <  StatefulRedisConnection  < K , V > > allConnections  ( )  {  return  ImmutableList . copyOf  (   connections . asMap  ( ) . values  ( ) ) ; } }