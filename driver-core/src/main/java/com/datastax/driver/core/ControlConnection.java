  package    com . datastax . driver . core ;   import   java . net . InetAddress ;  import   java . net . UnknownHostException ;  import  java . util .  * ;  import    java . util . concurrent . ExecutionException ;  import    java . util . concurrent . ScheduledFuture ;  import    java . util . concurrent . TimeUnit ;  import     java . util . concurrent . atomic . AtomicReference ;  import     com . google . common . base . Objects ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;  import      com . datastax . driver . core . exceptions . DriverException ;  import      com . datastax . driver . core . exceptions . DriverInternalError ;  import      com . datastax . driver . core . exceptions . NoHostAvailableException ;  class ControlConnection  implements   Host . StateListener  {   private static final Logger  logger =  LoggerFactory . getLogger  (  ControlConnection . class ) ;   static final  long  MAX_SCHEMA_AGREEMENT_WAIT_MS = 10000 ;   private static final InetAddress  bindAllAddress ;  static  {  try  {   bindAllAddress =  InetAddress . getByAddress  (  new  byte  [ 4 ] ) ; }  catch (   UnknownHostException e )  {  throw  new RuntimeException  ( e ) ; } }   private static final String  SELECT_KEYSPACES = "SELECT * FROM system.schema_keyspaces" ;   private static final String  SELECT_COLUMN_FAMILIES = "SELECT * FROM system.schema_columnfamilies" ;   private static final String  SELECT_COLUMNS = "SELECT * FROM system.schema_columns" ;   private static final String  SELECT_PEERS = "SELECT * FROM system.peers" ;   private static final String  SELECT_LOCAL = "SELECT * FROM system.local WHERE key='local'" ;   private static final String  SELECT_SCHEMA_PEERS = "SELECT peer, rpc_address, schema_version FROM system.peers" ;   private static final String  SELECT_SCHEMA_LOCAL = "SELECT schema_version FROM system.local WHERE key='local'" ;   private final  AtomicReference  < Connection >  connectionRef =  new  AtomicReference  < Connection >  ( ) ;   private final  Cluster . Manager  cluster ;   private final  AtomicReference  <  ScheduledFuture  <  ? > >  reconnectionAttempt =  new  AtomicReference  <  ScheduledFuture  <  ? > >  ( ) ;   private volatile boolean  isShutdown ;   public ControlConnection  (   Cluster . Manager manager )  {    this . cluster = manager ; }   public void connect  ( )  throws UnsupportedProtocolVersionException  {  if  ( isShutdown )  return ;   setNewConnection  (  reconnectInternal  ( ) ) ; }   public CloseFuture closeAsync  ( )  {   isShutdown = true ;  Connection  connection =  connectionRef . get  ( ) ;  return   connection == null ?  CloseFuture . immediateFuture  ( ) :  connection . closeAsync  ( ) ; }   private void reconnect  ( )  {  if  ( isShutdown )  return ;  try  {   setNewConnection  (  reconnectInternal  ( ) ) ; }  catch (   NoHostAvailableException e )  {   logger . error  ( "[Control connection] Cannot connect to any host, scheduling retry" ) ;    new AbstractReconnectionHandler  (  cluster . reconnectionExecutor ,   cluster . reconnectionPolicy  ( ) . newSchedule  ( ) , reconnectionAttempt )  {    @ Override protected Connection tryReconnect  ( )  throws ConnectionException  {  try  {  return  reconnectInternal  ( ) ; }  catch (   NoHostAvailableException e )  {  throw  new ConnectionException  ( null ,  e . getMessage  ( ) ) ; }  catch (   UnsupportedProtocolVersionException e )  {  throw  new AssertionError  ( ) ; } }    @ Override protected void onReconnection  (  Connection connection )  {   setNewConnection  ( connection ) ; }    @ Override protected boolean onConnectionException  (  ConnectionException e ,   long nextDelayMs )  {   logger . error  ( "[Control connection] Cannot connect to any host, scheduling retry in {} milliseconds" , nextDelayMs ) ;  return true ; }    @ Override protected boolean onUnknownException  (  Exception e ,   long nextDelayMs )  {   logger . error  (  String . format  ( "[Control connection] Unknown error during reconnection, scheduling retry in %d milliseconds" , nextDelayMs ) , e ) ;  return true ; } } . start  ( ) ; }  catch (   UnsupportedProtocolVersionException e )  {  throw  new AssertionError  ( ) ; } }   private void signalError  ( )  {  Connection  connection =  connectionRef . get  ( ) ;  if  (   connection != null &&  connection . isDefunct  ( ) )  {  Host  host =   cluster . metadata . getHost  (  connection . address ) ;  if  (  host != null )  {   cluster . signalConnectionFailure  ( host ,  connection . lastException  ( ) , false ) ;  return ; } }   reconnect  ( ) ; }   private void setNewConnection  (  Connection newConnection )  {   logger . debug  ( "[Control connection] Successfully connected to {}" ,  newConnection . address ) ;  Connection  old =  connectionRef . getAndSet  ( newConnection ) ;  if  (   old != null &&  !  old . isClosed  ( ) )   old . closeAsync  ( ) ; }   private Connection reconnectInternal  ( )  throws UnsupportedProtocolVersionException  {   Iterator  < Host >  iter =   cluster . loadBalancingPolicy  ( ) . newQueryPlan  ( null ,  Statement . DEFAULT ) ;   Map  < InetAddress , Throwable >  errors = null ;  Host  host = null ;  try  {  while  (  iter . hasNext  ( ) )  {   host =  iter . next  ( ) ;  try  {  return  tryConnect  ( host ) ; }  catch (   ConnectionException e )  {   errors =  logError  ( host , e , errors , iter ) ;   cluster . signalConnectionFailure  ( host , e , false ) ; }  catch (   ExecutionException e )  {   errors =  logError  ( host ,  e . getCause  ( ) , errors , iter ) ; }  catch (   UnsupportedProtocolVersionException e )  {  if  (   cluster . protocolVersion  ( ) < 1 )  throw e ;   logger . debug  ( "Ignoring host {}: {}" , host ,  e . getMessage  ( ) ) ;   errors =  logError  ( host ,  e . getCause  ( ) , errors , iter ) ; } } }  catch (   InterruptedException e )  {    Thread . currentThread  ( ) . interrupt  ( ) ;  if  (  host != null )   errors =  logError  ( host ,  new DriverException  ( "Connection thread interrupted" ) , errors , iter ) ;  while  (  iter . hasNext  ( ) )   errors =  logError  (  iter . next  ( ) ,  new DriverException  ( "Connection thread interrupted" ) , errors , iter ) ; }  throw  new NoHostAvailableException  (   errors == null ?  Collections .  < InetAddress , Throwable > emptyMap  ( ) : errors ) ; }   private static  Map  < InetAddress , Throwable > logError  (  Host host ,  Throwable exception ,   Map  < InetAddress , Throwable > errors ,   Iterator  < Host > iter )  {  if  (  errors == null )   errors =  new  HashMap  < InetAddress , Throwable >  ( ) ;   errors . put  (  host . getAddress  ( ) , exception ) ;  if  (  logger . isDebugEnabled  ( ) )  {  if  (  iter . hasNext  ( ) )  {   logger . debug  ( "[Control connection] error on {} connection ({}), trying next host" , host ,  exception . getMessage  ( ) ) ; } else  {   logger . debug  ( "[Control connection] error on {} connection ({}), no more host to try" , host ,  exception . getMessage  ( ) ) ; } }  return errors ; }   private Connection tryConnect  (  Host host )  throws ConnectionException , ExecutionException , InterruptedException , UnsupportedProtocolVersionException  {  Connection  connection =   cluster . connectionFactory . open  ( host ) ;  try  {   logger . trace  ( "[Control connection] Registering for events" ) ;    List <  ProtocolEvent . Type >  evs =  Arrays . asList  (   ProtocolEvent . Type . TOPOLOGY_CHANGE ,   ProtocolEvent . Type . STATUS_CHANGE ,   ProtocolEvent . Type . SCHEMA_CHANGE ) ;   connection . write  (  new  Requests . Register  ( evs ) ) ;   logger . debug  ( "[Control connection] Refreshing node list and token map" ) ;   refreshNodeListAndTokenMap  ( connection , cluster ) ;   logger . debug  ( "[Control connection] Refreshing schema" ) ;   refreshSchema  ( connection , null , null , cluster ) ;  return connection ; }  catch (   BusyConnectionException e )  {    connection . closeAsync  ( ) . get  ( ) ;  throw  new DriverInternalError  ( "Newly created connection should not be busy" ) ; }  catch (   RuntimeException e )  {    connection . closeAsync  ( ) . get  ( ) ;  throw e ; } }   public void refreshSchema  (  String keyspace ,  String table )  throws InterruptedException  {   logger . debug  ( "[Control connection] Refreshing schema for {}{}" ,   keyspace == null ? "" : keyspace ,   table == null ? "" :  '.' + table ) ;  try  {   refreshSchema  (  connectionRef . get  ( ) , keyspace , table , cluster ) ; }  catch (   ConnectionException e )  {   logger . debug  ( "[Control connection] Connection error while refreshing schema ({})" ,  e . getMessage  ( ) ) ;   signalError  ( ) ; }  catch (   ExecutionException e )  {  if  (  ! isShutdown )   logger . error  ( "[Control connection] Unexpected error while refreshing schema" , e ) ;   signalError  ( ) ; }  catch (   BusyConnectionException e )  {   logger . debug  ( "[Control connection] Connection is busy, reconnecting" ) ;   signalError  ( ) ; } }   static void refreshSchema  (  Connection connection ,  String keyspace ,  String table ,   Cluster . Manager cluster )  throws ConnectionException , BusyConnectionException , ExecutionException , InterruptedException  {  String  whereClause = "" ;  if  (  keyspace != null )  {   whereClause =   " WHERE keyspace_name = '" + keyspace + '\'' ;  if  (  table != null )   whereClause +=   " AND columnfamily_name = '" + table + '\'' ; }  DefaultResultSetFuture  ksFuture =   table == null ?  new DefaultResultSetFuture  ( null ,  new  Requests . Query  (  SELECT_KEYSPACES + whereClause ) ) : null ;  DefaultResultSetFuture  cfFuture =  new DefaultResultSetFuture  ( null ,  new  Requests . Query  (  SELECT_COLUMN_FAMILIES + whereClause ) ) ;  DefaultResultSetFuture  colsFuture =  new DefaultResultSetFuture  ( null ,  new  Requests . Query  (  SELECT_COLUMNS + whereClause ) ) ;  if  (  ksFuture != null )   connection . write  ( ksFuture ) ;   connection . write  ( cfFuture ) ;   connection . write  ( colsFuture ) ;  Host  host =   cluster . metadata . getHost  (  connection . address ) ;  VersionNumber  cassandraVersion ;  if  (   host == null ||   host . getCassandraVersion  ( ) == null )  {   cassandraVersion =    cluster . protocolVersion  ( ) == 1 ?  VersionNumber . parse  ( "1.2.0" ) :  VersionNumber . parse  ( "2.0.0" ) ;   logger . warn  (  "Cannot find Cassandra version for host {} to parse the schema, using {} based on protocol version in use. " + "If parsing the schema fails, this could be the cause" ,  connection . address , cassandraVersion ) ; } else  {   cassandraVersion =  host . getCassandraVersion  ( ) ; }  try  {    cluster . metadata . rebuildSchema  ( keyspace , table ,   ksFuture == null ? null :  ksFuture . get  ( ) ,  cfFuture . get  ( ) ,  colsFuture . get  ( ) , cassandraVersion ) ; }  catch (   RuntimeException e )  {   logger . error  ( "Error parsing schema from Cassandra system tables: the schema in Cluster#getMetadata() will appear incomplete or stale" , e ) ; }  if  (  table == null )   refreshNodeListAndTokenMap  ( connection , cluster ) ; }   public void refreshNodeListAndTokenMap  ( )  {  Connection  c =  connectionRef . get  ( ) ;  if  (  c == null )  return ;   logger . debug  ( "[Control connection] Refreshing node list and token map" ) ;  try  {   refreshNodeListAndTokenMap  ( c , cluster ) ; }  catch (   ConnectionException e )  {   logger . debug  ( "[Control connection] Connection error while refreshing node list and token map ({})" ,  e . getMessage  ( ) ) ;   signalError  ( ) ; }  catch (   ExecutionException e )  {  if  (  ! isShutdown )   logger . error  ( "[Control connection] Unexpected error while refreshing node list and token map" , e ) ;   signalError  ( ) ; }  catch (   BusyConnectionException e )  {   logger . debug  ( "[Control connection] Connection is busy, reconnecting" ) ;   signalError  ( ) ; }  catch (   InterruptedException e )  {    Thread . currentThread  ( ) . interrupt  ( ) ;   logger . debug  ( "[Control connection] Interrupted while refreshing node list and token map, skipping it." ) ; } }   public void refreshNodeInfo  (  Host host )  {  Connection  c =  connectionRef . get  ( ) ;  if  (  c == null )  return ;   logger . debug  ( "[Control connection] Refreshing node info on {}" , host ) ;  try  {  DefaultResultSetFuture  future =    c . address . equals  (  host . getAddress  ( ) ) ?  new DefaultResultSetFuture  ( null ,  new  Requests . Query  ( SELECT_LOCAL ) ) :  new DefaultResultSetFuture  ( null ,  new  Requests . Query  (    SELECT_PEERS + " WHERE peer='" +   host . getAddress  ( ) . getHostAddress  ( ) + '\'' ) ) ;   c . write  ( future ) ;  ResultSet  rs =  future . get  ( ) ;  if  (  rs . isExhausted  ( ) )  {   logger . debug  ( "[control connection] Asked to refresh node info for {} but host not found in {} system table (this can happen)" ,  host . getAddress  ( ) ,  c . address ) ;  return ; }  Row  row =  rs . one  ( ) ;  if  (   !  row . isNull  ( "data_center" ) ||  !  row . isNull  ( "rack" ) )   updateLocationInfo  ( host ,  row . getString  ( "data_center" ) ,  row . getString  ( "rack" ) , cluster ) ;  if  (  !  row . isNull  ( "release_version" ) )   host . setVersion  (  row . getString  ( "release_version" ) ) ; }  catch (   ConnectionException e )  {   logger . debug  ( "[Control connection] Connection error while refreshing node info ({})" ,  e . getMessage  ( ) ) ;   signalError  ( ) ; }  catch (   ExecutionException e )  {  if  (  ! isShutdown )   logger . debug  ( "[Control connection] Unexpected error while refreshing node info" , e ) ;   signalError  ( ) ; }  catch (   BusyConnectionException e )  {   logger . debug  ( "[Control connection] Connection is busy, reconnecting" ) ;   signalError  ( ) ; }  catch (   InterruptedException e )  {    Thread . currentThread  ( ) . interrupt  ( ) ;   logger . debug  ( "[Control connection] Interrupted while refreshing node list and token map, skipping it." ) ; } }   private static void updateLocationInfo  (  Host host ,  String datacenter ,  String rack ,   Cluster . Manager cluster )  {  if  (   Objects . equal  (  host . getDatacenter  ( ) , datacenter ) &&  Objects . equal  (  host . getRack  ( ) , rack ) )  return ;    cluster . loadBalancingPolicy  ( ) . onDown  ( host ) ;   host . setLocationInfo  ( datacenter , rack ) ;    cluster . loadBalancingPolicy  ( ) . onAdd  ( host ) ; }   private static void refreshNodeListAndTokenMap  (  Connection connection ,   Cluster . Manager cluster )  throws ConnectionException , BusyConnectionException , ExecutionException , InterruptedException  {  DefaultResultSetFuture  peersFuture =  new DefaultResultSetFuture  ( null ,  new  Requests . Query  ( SELECT_PEERS ) ) ;  DefaultResultSetFuture  localFuture =  new DefaultResultSetFuture  ( null ,  new  Requests . Query  ( SELECT_LOCAL ) ) ;   connection . write  ( peersFuture ) ;   connection . write  ( localFuture ) ;  String  partitioner = null ;   Map  < Host ,  Collection  < String > >  tokenMap =  new  HashMap  < Host ,  Collection  < String > >  ( ) ;  Row  localRow =   localFuture . get  ( ) . one  ( ) ;  if  (  localRow != null )  {  String  clusterName =  localRow . getString  ( "cluster_name" ) ;  if  (  clusterName != null )     cluster . metadata . clusterName = clusterName ;   partitioner =  localRow . getString  ( "partitioner" ) ;  Host  host =   cluster . metadata . getHost  (  connection . address ) ;  if  (  host == null )  {   logger . debug  ( "Host in local system table ({}) unknown to us (ok if said host just got removed)" ,  connection . address ) ; } else  {  if  (   !  localRow . isNull  ( "data_center" ) ||  !  localRow . isNull  ( "rack" ) )   updateLocationInfo  ( host ,  localRow . getString  ( "data_center" ) ,  localRow . getString  ( "rack" ) , cluster ) ;  if  (  !  localRow . isNull  ( "release_version" ) )   host . setVersion  (  localRow . getString  ( "release_version" ) ) ;   Set  < String >  tokens =  localRow . getSet  ( "tokens" ,  String . class ) ;  if  (   partitioner != null &&  !  tokens . isEmpty  ( ) )   tokenMap . put  ( host , tokens ) ; } }   List  < InetAddress >  foundHosts =  new  ArrayList  < InetAddress >  ( ) ;   List  < String >  dcs =  new  ArrayList  < String >  ( ) ;   List  < String >  racks =  new  ArrayList  < String >  ( ) ;   List  < String >  cassandraVersions =  new  ArrayList  < String >  ( ) ;   List  <  Set  < String > >  allTokens =  new  ArrayList  <  Set  < String > >  ( ) ;  for ( Row row :  peersFuture . get  ( ) )  {  InetAddress  peer =  row . getInet  ( "peer" ) ;  InetAddress  addr =  row . getInet  ( "rpc_address" ) ;  if  (   peer . equals  (  connection . address ) ||  (   addr != null &&  addr . equals  (  connection . address ) ) )  {   logger . debug  ( "System.peers on node {} has a line for itself. This is not normal but is a known problem of some DSE version. Ignoring the entry." ,  connection . address ) ;  continue ; } else  if  (  addr == null )  {   logger . error  ( "No rpc_address found for host {} in {}'s peers system table. That should not happen but using address {} instead" , peer ,  connection . address , peer ) ;   addr = peer ; } else  if  (  addr . equals  ( bindAllAddress ) )  {   logger . warn  ( "Found host with 0.0.0.0 as rpc_address, using listen_address ({}) to contact it instead. If this is incorrect you should avoid the use of 0.0.0.0 server side." , peer ) ;   addr = peer ; }   foundHosts . add  ( addr ) ;   dcs . add  (  row . getString  ( "data_center" ) ) ;   racks . add  (  row . getString  ( "rack" ) ) ;   cassandraVersions . add  (  row . getString  ( "release_version" ) ) ;   allTokens . add  (  row . getSet  ( "tokens" ,  String . class ) ) ; }  for (   int  i = 0 ;  i <  foundHosts . size  ( ) ;  i ++ )  {  Host  host =   cluster . metadata . getHost  (  foundHosts . get  ( i ) ) ;  boolean  isNew = false ;  if  (  host == null )  {   host =   cluster . metadata . add  (  foundHosts . get  ( i ) ) ;   isNew = true ; }  if  (    dcs . get  ( i ) != null ||   racks . get  ( i ) != null )   updateLocationInfo  ( host ,  dcs . get  ( i ) ,  racks . get  ( i ) , cluster ) ;  if  (   cassandraVersions . get  ( i ) != null )   host . setVersion  (  cassandraVersions . get  ( i ) ) ;  if  (   partitioner != null &&  !   allTokens . get  ( i ) . isEmpty  ( ) )   tokenMap . put  ( host ,  allTokens . get  ( i ) ) ;  if  ( isNew )   cluster . onAdd  ( host ) ; }   Set  < InetAddress >  foundHostsSet =  new  HashSet  < InetAddress >  ( foundHosts ) ;  for ( Host host :   cluster . metadata . allHosts  ( ) )  if  (   !   host . getAddress  ( ) . equals  (  connection . address ) &&  !  foundHostsSet . contains  (  host . getAddress  ( ) ) )   cluster . removeHost  ( host ) ;    cluster . metadata . rebuildTokenMap  ( partitioner , tokenMap ) ; }   static boolean waitForSchemaAgreement  (  Connection connection ,  Metadata metadata )  throws ConnectionException , BusyConnectionException , ExecutionException , InterruptedException  {   long  start =  System . nanoTime  ( ) ;   long  elapsed = 0 ;  while  (  elapsed < MAX_SCHEMA_AGREEMENT_WAIT_MS )  {  DefaultResultSetFuture  peersFuture =  new DefaultResultSetFuture  ( null ,  new  Requests . Query  ( SELECT_SCHEMA_PEERS ) ) ;  DefaultResultSetFuture  localFuture =  new DefaultResultSetFuture  ( null ,  new  Requests . Query  ( SELECT_SCHEMA_LOCAL ) ) ;   connection . write  ( peersFuture ) ;   connection . write  ( localFuture ) ;   Set  < UUID >  versions =  new  HashSet  < UUID >  ( ) ;  Row  localRow =   localFuture . get  ( ) . one  ( ) ;  if  (   localRow != null &&  !  localRow . isNull  ( "schema_version" ) )   versions . add  (  localRow . getUUID  ( "schema_version" ) ) ;  for ( Row row :  peersFuture . get  ( ) )  {  if  (   row . isNull  ( "rpc_address" ) ||  row . isNull  ( "schema_version" ) )  continue ;  InetAddress  rpc =  row . getInet  ( "rpc_address" ) ;  if  (  rpc . equals  ( bindAllAddress ) )   rpc =  row . getInet  ( "peer" ) ;  Host  peer =  metadata . getHost  ( rpc ) ;  if  (   peer != null &&  peer . isUp  ( ) )   versions . add  (  row . getUUID  ( "schema_version" ) ) ; }   logger . debug  ( "Checking for schema agreement: versions are {}" , versions ) ;  if  (   versions . size  ( ) <= 1 )  return true ;   Thread . sleep  ( 200 ) ;   elapsed =  Cluster . timeSince  ( start ,  TimeUnit . MILLISECONDS ) ; }  return false ; }  boolean isOpen  ( )  {  Connection  c =  connectionRef . get  ( ) ;  return   c != null &&  !  c . isClosed  ( ) ; }    @ Override public void onUp  (  Host host )  { }    @ Override public void onDown  (  Host host )  {  Connection  current =  connectionRef . get  ( ) ;  if  (  logger . isTraceEnabled  ( ) )   logger . trace  ( "[Control connection] {} is down, currently connected to {}" , host ,   current == null ? "nobody" :  current . address ) ;  if  (    current != null &&   current . address . equals  (  host . getAddress  ( ) ) &&   reconnectionAttempt . get  ( ) == null )  {    cluster . blockingTasksExecutor . submit  (  new Runnable  ( )  {    @ Override public void run  ( )  {   reconnect  ( ) ; } } ) ; } }    @ Override public void onAdd  (  Host host )  {   refreshNodeListAndTokenMap  ( ) ; }    @ Override public void onRemove  (  Host host )  {   refreshNodeListAndTokenMap  ( ) ; } 
<<<<<<<
=======
  private void refreshNodeListAndTokenMap  (  Connection connection )  throws ConnectionException , BusyConnectionException , ExecutionException , InterruptedException  {  DefaultResultSetFuture  peersFuture =  new DefaultResultSetFuture  ( null ,  new QueryMessage  ( SELECT_PEERS ,  ConsistencyLevel . DEFAULT_CASSANDRA_CL ) ) ;  DefaultResultSetFuture  localFuture =  new DefaultResultSetFuture  ( null ,  new QueryMessage  ( SELECT_LOCAL ,  ConsistencyLevel . DEFAULT_CASSANDRA_CL ) ) ;   connection . write  ( peersFuture ) ;   connection . write  ( localFuture ) ;  String  partitioner = null ;   Map  < Host ,  Collection  < String > >  tokenMap =  new  HashMap  < Host ,  Collection  < String > >  ( ) ;  Row  localRow =   localFuture . get  ( ) . one  ( ) ;  if  (  localRow != null )  {  String  clusterName =  localRow . getString  ( "cluster_name" ) ;  if  (  clusterName != null )     cluster . metadata . clusterName = clusterName ;   partitioner =  localRow . getString  ( "partitioner" ) ;  if  (  partitioner != null )     cluster . metadata . partitioner = partitioner ;  Host  host =   cluster . metadata . getHost  (  connection . address ) ;  if  (  host == null )  {   logger . debug  ( "Host in local system table ({}) unknown to us (ok if said host just got removed)" ,  connection . address ) ; } else  {   updateLocationInfo  ( host ,  localRow . getString  ( "data_center" ) ,  localRow . getString  ( "rack" ) ) ;   Set  < String >  tokens =  localRow . getSet  ( "tokens" ,  String . class ) ;  if  (   partitioner != null &&  !  tokens . isEmpty  ( ) )   tokenMap . put  ( host , tokens ) ; } }   List  < InetAddress >  foundHosts =  new  ArrayList  < InetAddress >  ( ) ;   List  < String >  dcs =  new  ArrayList  < String >  ( ) ;   List  < String >  racks =  new  ArrayList  < String >  ( ) ;   List  <  Set  < String > >  allTokens =  new  ArrayList  <  Set  < String > >  ( ) ;  for ( Row row :  peersFuture . get  ( ) )  {  InetAddress  peer =  row . getInet  ( "peer" ) ;  InetAddress  addr =  row . getInet  ( "rpc_address" ) ;  if  (   peer . equals  (  connection . address ) ||  (   addr != null &&  addr . equals  (  connection . address ) ) )  {   logger . debug  ( "System.peers on node {} has a line for itself. This is not normal but is a known problem of some DSE version. Ignoring the entry." ,  connection . address ) ;  continue ; } else  if  (  addr == null )  {   logger . error  ( "No rpc_address found for host {} in {}'s peers system table. That should not happen but using address {} instead" , peer ,  connection . address , peer ) ;   addr = peer ; } else  if  (  addr . equals  ( bindAllAddress ) )  {   logger . warn  ( "Found host with 0.0.0.0 as rpc_address, using listen_address ({}) to contact it instead. If this is incorrect you should avoid the use of 0.0.0.0 server side." , peer ) ;   addr = peer ; }   foundHosts . add  ( addr ) ;   dcs . add  (  row . getString  ( "data_center" ) ) ;   racks . add  (  row . getString  ( "rack" ) ) ;   allTokens . add  (  row . getSet  ( "tokens" ,  String . class ) ) ; }  for (   int  i = 0 ;  i <  foundHosts . size  ( ) ;  i ++ )  {  Host  host =   cluster . metadata . getHost  (  foundHosts . get  ( i ) ) ;  if  (  host == null )  {   host =  cluster . addHost  (  foundHosts . get  ( i ) , true ) ; }   updateLocationInfo  ( host ,  dcs . get  ( i ) ,  racks . get  ( i ) ) ;  if  (   partitioner != null &&  !   allTokens . get  ( i ) . isEmpty  ( ) )   tokenMap . put  ( host ,  allTokens . get  ( i ) ) ; }   Set  < InetAddress >  foundHostsSet =  new  HashSet  < InetAddress >  ( foundHosts ) ;  for ( Host host :   cluster . metadata . allHosts  ( ) )  if  (   !   host . getAddress  ( ) . equals  (  connection . address ) &&  !  foundHostsSet . contains  (  host . getAddress  ( ) ) )   cluster . removeHost  ( host ) ;  if  (  partitioner != null )    cluster . metadata . rebuildTokenMap  ( partitioner , tokenMap ) ; }
>>>>>>>
 }