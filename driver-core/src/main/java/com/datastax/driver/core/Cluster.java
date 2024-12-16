  package    com . datastax . driver . core ;   import   java . net . InetAddress ;  import   java . net . UnknownHostException ;  import  java . util .  * ;  import   java . util . concurrent .  * ;  import     java . util . concurrent . atomic . AtomicBoolean ;  import     java . util . concurrent . atomic . AtomicInteger ;  import     java . util . concurrent . atomic . AtomicReference ;  import     com . google . common . base . Predicates ;  import     com . google . common . collect . Iterables ;  import     com . google . common . collect . HashMultimap ;  import     com . google . common . collect . SetMultimap ;  import      com . google . common . util . concurrent . Futures ;  import      com . google . common . util . concurrent . FutureCallback ;  import      com . google . common . util . concurrent . ListenableFuture ;  import      com . google . common . util . concurrent . ListeningExecutorService ;  import      com . google . common . util . concurrent . MoreExecutors ;  import      com . google . common . util . concurrent . ThreadFactoryBuilder ;  import     com . datastax . driver . core . exceptions .  * ;  import     com . datastax . driver . core . policies .  * ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;   public class Cluster  {   private static final Logger  logger =  LoggerFactory . getLogger  (  Cluster . class ) ;   private static final AtomicInteger  CLUSTER_ID =  new AtomicInteger  ( 0 ) ;   final Manager  manager ;   private Cluster  (  String name ,   List  < InetAddress > contactPoints ,  Configuration configuration ,   Collection  <  Host . StateListener > listeners )  {    this . manager =  new Manager  ( name , contactPoints , configuration , listeners ) ; }   public Cluster init  ( )  {    this . manager . init  ( ) ;  return this ; }   public static Cluster buildFrom  (  Initializer initializer )  {   List  < InetAddress >  contactPoints =  initializer . getContactPoints  ( ) ;  if  (  contactPoints . isEmpty  ( ) )  throw  new IllegalArgumentException  ( "Cannot build a cluster without contact points" ) ;  return  new Cluster  (  initializer . getClusterName  ( ) , contactPoints ,  initializer . getConfiguration  ( ) ,  initializer . getInitialListeners  ( ) ) ; }   public static  Cluster . Builder builder  ( )  {  return  new  Cluster . Builder  ( ) ; }   public Session connect  ( )  {  return  manager . newSession  ( ) ; }   public Session connect  (  String keyspace )  {  Session  session =  connect  ( ) ;    session . manager . setKeyspace  ( keyspace ) ;  return session ; }   public String getClusterName  ( )  {  return  manager . clusterName ; }   public Metadata getMetadata  ( )  {  return  manager . metadata ; }   public Configuration getConfiguration  ( )  {  return  manager . configuration ; }   public Metrics getMetrics  ( )  {  return  manager . metrics ; }   public Cluster register  (   Host . StateListener listener )  {    manager . listeners . add  ( listener ) ;  return this ; }   public Cluster unregister  (   Host . StateListener listener )  {    manager . listeners . remove  ( listener ) ;  return this ; }   public Cluster register  (  LatencyTracker tracker )  {    manager . trackers . add  ( tracker ) ;  return this ; }   public Cluster unregister  (  LatencyTracker tracker )  {    manager . trackers . remove  ( tracker ) ;  return this ; }   public ShutdownFuture shutdown  ( )  {  return  manager . shutdown  ( ) ; }   public interface Initializer  {   public String getClusterName  ( ) ;   public  List  < InetAddress > getContactPoints  ( ) ;   public Configuration getConfiguration  ( ) ;   public  Collection  <  Host . StateListener > getInitialListeners  ( ) ; }   public static class Builder  implements  Initializer  {   private String  clusterName ;   private final  List  < InetAddress >  addresses =  new  ArrayList  < InetAddress >  ( ) ;   private  int  port =  ProtocolOptions . DEFAULT_PORT ;   private AuthProvider  authProvider =  AuthProvider . NONE ;   private LoadBalancingPolicy  loadBalancingPolicy ;   private ReconnectionPolicy  reconnectionPolicy ;   private RetryPolicy  retryPolicy ;   private  ProtocolOptions . Compression  compression =   ProtocolOptions . Compression . NONE ;   private SSLOptions  sslOptions = null ;   private boolean  metricsEnabled = true ;   private boolean  jmxEnabled = true ;   private PoolingOptions  poolingOptions =  new PoolingOptions  ( ) ;   private SocketOptions  socketOptions =  new SocketOptions  ( ) ;   private QueryOptions  queryOptions ;   private  Collection  <  Host . StateListener >  listeners ;    @ Override public String getClusterName  ( )  {  return clusterName ; }    @ Override public  List  < InetAddress > getContactPoints  ( )  {  return addresses ; }   public Builder withClusterName  (  String name )  {    this . clusterName = name ;  return this ; }   public Builder withPort  (   int port )  {    this . port = port ;  return this ; }   public Builder addContactPoint  (  String address )  {  try  {    this . addresses . add  (  InetAddress . getByName  ( address ) ) ;  return this ; }  catch (   UnknownHostException e )  {  throw  new IllegalArgumentException  (  e . getMessage  ( ) ) ; } }   public Builder addContactPoints  (  String ...  addresses )  {  for ( String address : addresses )   addContactPoint  ( address ) ;  return this ; }   public Builder addContactPoints  (  InetAddress ...  addresses )  {  for ( InetAddress address : addresses )    this . addresses . add  ( address ) ;  return this ; }   public Builder withLoadBalancingPolicy  (  LoadBalancingPolicy policy )  {    this . loadBalancingPolicy = policy ;  return this ; }   public Builder withReconnectionPolicy  (  ReconnectionPolicy policy )  {    this . reconnectionPolicy = policy ;  return this ; }   public Builder withRetryPolicy  (  RetryPolicy policy )  {    this . retryPolicy = policy ;  return this ; }   public Builder withCredentials  (  String username ,  String password )  {    this . authProvider =  new PlainTextAuthProvider  ( username , password ) ;  return this ; }   public Builder withAuthProvider  (  AuthProvider authProvider )  {    this . authProvider = authProvider ;  return this ; }   public Builder withCompression  (   ProtocolOptions . Compression compression )  {    this . compression = compression ;  return this ; }   public Builder withoutMetrics  ( )  {    this . metricsEnabled = false ;  return this ; }   public Builder withSSL  ( )  {    this . sslOptions =  new SSLOptions  ( ) ;  return this ; }   public Builder withSSL  (  SSLOptions sslOptions )  {    this . sslOptions = sslOptions ;  return this ; }   public Builder withInitialListeners  (   Collection  <  Host . StateListener > listeners )  {    this . listeners = listeners ;  return this ; }   public Builder withoutJMXReporting  ( )  {    this . jmxEnabled = false ;  return this ; }   public Builder withPoolingOptions  (  PoolingOptions options )  {    this . poolingOptions = options ;  return this ; }   public Builder withSocketOptions  (  SocketOptions options )  {    this . socketOptions = options ;  return this ; }   public Builder withQueryOptions  (  QueryOptions options )  {    this . queryOptions = options ;  return this ; }    @ Override public Configuration getConfiguration  ( )  {  Policies  policies =  new Policies  (   loadBalancingPolicy == null ?  Policies . defaultLoadBalancingPolicy  ( ) : loadBalancingPolicy ,   reconnectionPolicy == null ?  Policies . defaultReconnectionPolicy  ( ) : reconnectionPolicy ,   retryPolicy == null ?  Policies . defaultRetryPolicy  ( ) : retryPolicy ) ;  return  new Configuration  ( policies ,   new ProtocolOptions  ( port , sslOptions , authProvider ) . setCompression  ( compression ) ,   poolingOptions == null ?  new PoolingOptions  ( ) : poolingOptions ,   socketOptions == null ?  new SocketOptions  ( ) : socketOptions ,  metricsEnabled ?  new MetricsOptions  ( jmxEnabled ) : null ,   queryOptions == null ?  new QueryOptions  ( ) : queryOptions ) ; }    @ Override public  Collection  <  Host . StateListener > getInitialListeners  ( )  {  return   listeners == null ?  Collections .  <  Host . StateListener > emptySet  ( ) : listeners ; }   public Cluster build  ( )  {  return  Cluster . buildFrom  ( this ) ; } }   private static ThreadFactory threadFactory  (  String nameFormat )  {  return    new ThreadFactoryBuilder  ( ) . setNameFormat  ( nameFormat ) . build  ( ) ; }   static  long timeSince  (   long startNanos ,  TimeUnit destUnit )  {  return  destUnit . convert  (   System . nanoTime  ( ) - startNanos ,  TimeUnit . NANOSECONDS ) ; }   private static String generateClusterName  ( )  {  return  "cluster" +  CLUSTER_ID . incrementAndGet  ( ) ; }  class Manager  implements   Host . StateListener ,  Connection . DefaultResponseHandler  {   final String  clusterName ;   private final AtomicBoolean  isInit =  new AtomicBoolean  ( false ) ;   final  List  < InetAddress >  contactPoints ;   final  Set  < Session >  sessions =  new  CopyOnWriteArraySet  < Session >  ( ) ;   final Metadata  metadata ;   final Configuration  configuration ;   final Metrics  metrics ;   final  Connection . Factory  connectionFactory ;   final ControlConnection  controlConnection ;   final  ConvictionPolicy . Factory  convictionPolicyFactory =  new   ConvictionPolicy . Simple . Factory  ( ) ;   final ScheduledExecutorService  reconnectionExecutor =  Executors . newScheduledThreadPool  ( 2 ,  threadFactory  ( "Reconnection-%d" ) ) ;   final ScheduledExecutorService  scheduledTasksExecutor =  Executors . newScheduledThreadPool  ( 1 ,  threadFactory  ( "Scheduled Tasks-%d" ) ) ;   final ListeningExecutorService  executor =  MoreExecutors . listeningDecorator  (  Executors . newCachedThreadPool  (  threadFactory  ( "Cassandra Java Driver worker-%d" ) ) ) ;   final  AtomicReference  < ShutdownFuture >  shutdownFuture =  new  AtomicReference  < ShutdownFuture >  ( ) ;   final  Map  < MD5Digest , PreparedStatement >  preparedQueries =  new  ConcurrentHashMap  < MD5Digest , PreparedStatement >  ( ) ;   final  Set  <  Host . StateListener >  listeners =  new  CopyOnWriteArraySet  <  Host . StateListener >  ( ) ;   final  Set  < LatencyTracker >  trackers =  new  CopyOnWriteArraySet  < LatencyTracker >  ( ) ;   private Manager  (  String clusterName ,   List  < InetAddress > contactPoints ,  Configuration configuration ,   Collection  <  Host . StateListener > listeners )  {   logger . debug  (  "Starting new cluster with contact points " + contactPoints ) ;    this . clusterName =   clusterName == null ?  generateClusterName  ( ) : clusterName ;    this . configuration = configuration ;    this . metadata =  new Metadata  ( this ) ;    this . contactPoints = contactPoints ;    this . connectionFactory =  new  Connection . Factory  ( this ,   configuration . getProtocolOptions  ( ) . getAuthProvider  ( ) ) ;    this . controlConnection =  new ControlConnection  ( this ) ;    this . metrics =    configuration . getMetricsOptions  ( ) == null ? null :  new Metrics  ( this ) ;    this . configuration . register  ( this ) ;    this . listeners =  new  CopyOnWriteArraySet  <  Host . StateListener >  ( listeners ) ; }   private void init  ( )  {  if  (  !  isInit . compareAndSet  ( false , true ) )  return ;  for ( InetAddress address : contactPoints )  {  Host  host =  addHost  ( address , false ) ;   host . setUp  ( ) ;  if  (  host != null ) 
<<<<<<<
 {  for (  Host . StateListener listener : listeners )   listener . onAdd  ( host ) ; }
=======
  host . setUp  ( ) ;
>>>>>>>
 }    loadBalancingPolicy  ( ) . init  (  Cluster . this ,  metadata . allHosts  ( ) ) ;  try  {   controlConnection . connect  ( ) ; }  catch (   NoHostAvailableException e )  {   shutdown  ( ) ;  throw e ; } }  Cluster getCluster  ( )  {  return  Cluster . this ; }  LoadBalancingPolicy loadBalancingPolicy  ( )  {  return   configuration . getPolicies  ( ) . getLoadBalancingPolicy  ( ) ; }  ReconnectionPolicy reconnectionPolicy  ( )  {  return   configuration . getPolicies  ( ) . getReconnectionPolicy  ( ) ; }   private Session newSession  ( )  {   init  ( ) ;  Session  session =  new Session  (  Cluster . this ,  metadata . allHosts  ( ) ) ;   sessions . add  ( session ) ;  return session ; }  void reportLatency  (  Host host ,   long latencyNanos )  {  for ( LatencyTracker tracker : trackers )  {   tracker . update  ( host , latencyNanos ) ; } }  boolean isShutdown  ( )  {  return   shutdownFuture . get  ( ) != null ; }   private ShutdownFuture shutdown  ( )  {  ShutdownFuture  future =  shutdownFuture . get  ( ) ;  if  (  future != null )  return future ;   logger . debug  ( "Shutting down" ) ;   reconnectionExecutor . shutdown  ( ) ;   scheduledTasksExecutor . shutdown  ( ) ;   executor . shutdown  ( ) ;  if  (  metrics != null )   metrics . shutdown  ( ) ;   List  < ShutdownFuture >  futures =  new  ArrayList  < ShutdownFuture >  (   sessions . size  ( ) + 1 ) ;   futures . add  (  controlConnection . shutdown  ( ) ) ;  for ( Session session : sessions )   futures . add  (  session . shutdown  ( ) ) ;   future =  new ClusterShutdownFuture  ( futures ) ;  return   shutdownFuture . compareAndSet  ( null , future ) ? future :  shutdownFuture . get  ( ) ; }    @ Override public void onUp  (   final Host host )  {   logger . trace  ( "Host {} is UP" , host ) ;  if  (  isShutdown  ( ) )  return ;  if  (  host . isUp  ( ) )  return ;   ScheduledFuture  <  ? >  scheduledAttempt =   host . reconnectionAttempt . getAndSet  ( null ) ;  if  (  scheduledAttempt != null )  {   logger . debug  ( "Cancelling reconnection attempt since node is UP" ) ;   scheduledAttempt . cancel  ( false ) ; }  try  {   prepareAllQueries  ( host ) ; }  catch (   InterruptedException e )  {    Thread . currentThread  ( ) . interrupt  ( ) ; }  for ( Session s : sessions )    s . manager . removePool  ( host ) ;    loadBalancingPolicy  ( ) . onUp  ( host ) ;   controlConnection . onUp  ( host ) ;   List  <  ListenableFuture  < Boolean > >  futures =  new  ArrayList  <  ListenableFuture  < Boolean > >  (  sessions . size  ( ) ) ;  for ( Session s : sessions )   futures . add  (   s . manager . addOrRenewPool  ( host , false ) ) ;   Futures . addCallback  (  Futures . allAsList  ( futures ) ,  new  FutureCallback  <  List  < Boolean > >  ( )  {   public void onSuccess  (   List  < Boolean > poolCreationResults )  {  if  (  Iterables . any  ( poolCreationResults ,  Predicates . equalTo  ( false ) ) )  {   logger . debug  ( "Connection pool cannot be created, not marking {} UP" , host ) ;  return ; }   host . setUp  ( ) ;  for (  Host . StateListener listener : listeners )   listener . onUp  ( host ) ;  for ( Session s : sessions )    s . manager . updateCreatedPools  ( ) ; }   public void onFailure  (  Throwable t )  {  if  (  !  (  t instanceof InterruptedException ) )   logger . error  ( "Unexpected error while marking node UP: while this shouldn't happen, this shouldn't be critical" , t ) ; } } ) ; }    @ Override public void onDown  (   final Host host )  {   onDown  ( host , false ) ; }   public void onDown  (   final Host host ,   final boolean isHostAddition )  {   logger . trace  ( "Host {} is DOWN" , host ) ;  if  (  isShutdown  ( ) )  return ;  if  (    host . reconnectionAttempt . get  ( ) != null )  return ;  boolean  wasUp =  host . isUp  ( ) ;   host . setDown  ( ) ;    loadBalancingPolicy  ( ) . onDown  ( host ) ;   controlConnection . onDown  ( host ) ;  for ( Session s : sessions )    s . manager . onDown  ( host ) ;  if  ( wasUp )  {  for (  Host . StateListener listener : listeners )   listener . onDown  ( host ) ; }   logger . debug  ( "{} is down, scheduling connection retries" , host ) ;    new AbstractReconnectionHandler  ( reconnectionExecutor ,   reconnectionPolicy  ( ) . newSchedule  ( ) ,  host . reconnectionAttempt )  {   protected Connection tryReconnect  ( )  throws ConnectionException , InterruptedException  {  return  connectionFactory . open  ( host ) ; }   protected void onReconnection  (  Connection connection )  {   logger . debug  ( "Successful reconnection to {}, setting host UP" , host ) ;  if  ( isHostAddition )   onAdd  ( host ) ; else   onUp  ( host ) ; }   protected boolean onConnectionException  (  ConnectionException e ,   long nextDelayMs )  {  if  (  logger . isDebugEnabled  ( ) )   logger . debug  ( "Failed reconnection to {} ({}), scheduling retry in {} milliseconds" ,  new Object  [ ]  { host ,  e . getMessage  ( ) , nextDelayMs } ) ;  return true ; }   protected boolean onUnknownException  (  Exception e ,   long nextDelayMs )  {   logger . error  (  String . format  ( "Unknown error during control connection reconnection, scheduling retry in %d milliseconds" , nextDelayMs ) , e ) ;  return true ; } } . start  ( ) ; }    @ Override public void onAdd  (   final Host host )  {   logger . trace  ( "Adding new host {}" , host ) ;  if  (  isShutdown  ( ) )  return ;  try  {   prepareAllQueries  ( host ) ; }  catch (   InterruptedException e )  {    Thread . currentThread  ( ) . interrupt  ( ) ; }    loadBalancingPolicy  ( ) . onAdd  ( host ) ;   controlConnection . onAdd  ( host ) ;   List  <  ListenableFuture  < Boolean > >  futures =  new  ArrayList  <  ListenableFuture  < Boolean > >  (  sessions . size  ( ) ) ;  for ( Session s : sessions )   futures . add  (   s . manager . addOrRenewPool  ( host , true ) ) ;   Futures . addCallback  (  Futures . allAsList  ( futures ) ,  new  FutureCallback  <  List  < Boolean > >  ( )  {   public void onSuccess  (   List  < Boolean > poolCreationResults )  {  if  (  Iterables . any  ( poolCreationResults ,  Predicates . equalTo  ( false ) ) )  {   logger . debug  ( "Connection pool cannot be created, not marking {} UP" , host ) ;  return ; }   host . setUp  ( ) ;  for (  Host . StateListener listener : listeners )   listener . onAdd  ( host ) ;  for ( Session s : sessions )    s . manager . updateCreatedPools  ( ) ; }   public void onFailure  (  Throwable t )  {  if  (  !  (  t instanceof InterruptedException ) )   logger . error  ( "Unexpected error while adding node: while this shouldn't happen, this shouldn't be critical" , t ) ; } } ) ; }    @ Override public void onRemove  (  Host host )  {  if  (  isShutdown  ( ) )  return ;   host . setDown  ( ) ;   logger . trace  ( "Removing host {}" , host ) ;    loadBalancingPolicy  ( ) . onRemove  ( host ) ;   controlConnection . onRemove  ( host ) ;  for ( Session s : sessions )    s . manager . onRemove  ( host ) ;  for (  Host . StateListener listener : listeners )   listener . onRemove  ( host ) ; }   public boolean signalConnectionFailure  (  Host host ,  ConnectionException exception ,  boolean isHostAddition )  {  boolean  isDown =  host . signalConnectionFailure  ( exception ) ;  if  ( isDown )   onDown  ( host , isHostAddition ) ;  return isDown ; }   public Host addHost  (  InetAddress address ,  boolean signal )  {  Host  newHost =  metadata . add  ( address ) ;  if  (   newHost != null && signal )  {   logger . info  ( "New Cassandra host {} added" , newHost ) ;   onAdd  ( newHost ) ; }  return newHost ; }   public void removeHost  (  Host host )  {  if  (  host == null )  return ;  if  (  metadata . remove  ( host ) )  {   logger . info  ( "Cassandra host {} removed" , host ) ;   onRemove  ( host ) ; } }   public void ensurePoolsSizing  ( )  {  for ( Session session : sessions )  {  for ( HostConnectionPool pool :    session . manager . pools . values  ( ) )   pool . ensureCoreConnections  ( ) ; } }   public void prepare  (  MD5Digest digest ,  PreparedStatement stmt ,  InetAddress toExclude )  throws InterruptedException  {   preparedQueries . put  ( digest , stmt ) ;  for ( Session s : sessions )    s . manager . prepare  (  stmt . getQueryString  ( ) , toExclude ) ; }   private void prepareAllQueries  (  Host host )  throws InterruptedException  {  if  (  preparedQueries . isEmpty  ( ) )  return ;   logger . debug  ( "Preparing {} prepared queries on newly up node {}" ,  preparedQueries . size  ( ) , host ) ;  try  {  Connection  connection =  connectionFactory . open  ( host ) ;  try  {  try  {   ControlConnection . waitForSchemaAgreement  ( connection , metadata ) ; }  catch (   ExecutionException e )  { }   SetMultimap  < String , String >  perKeyspace =  HashMultimap . create  ( ) ;  for ( PreparedStatement ps :  preparedQueries . values  ( ) )  {  String  keyspace =    ps . getQueryKeyspace  ( ) == null ? "" :  ps . getQueryKeyspace  ( ) ;   perKeyspace . put  ( keyspace ,  ps . getQueryString  ( ) ) ; }  for ( String keyspace :  perKeyspace . keySet  ( ) )  {  if  (  !  keyspace . isEmpty  ( ) )   connection . setKeyspace  ( keyspace ) ;    List <  Connection . Future >  futures =  new  ArrayList  <  Connection . Future >  (  preparedQueries . size  ( ) ) ;  for ( String query :  perKeyspace . get  ( keyspace ) )  {   futures . add  (  connection . write  (  new  Requests . Prepare  ( query ) ) ) ; }  for (  Connection . Future future : futures )  {  try  {   future . get  ( ) ; }  catch (   ExecutionException e )  {   logger . debug  ( "Unexpected error while preparing queries on new/newly up host" , e ) ; } } } }  finally  {   connection . close  ( ) ; } }  catch (   ConnectionException e )  { }  catch (   AuthenticationException e )  { }  catch (   BusyConnectionException e )  { } }   public void submitSchemaRefresh  (   final String keyspace ,   final String table )  {   logger . trace  ( "Submitting schema refresh" ) ;   executor . submit  (  new Runnable  ( )  {    @ Override public void run  ( )  {  try  {   controlConnection . refreshSchema  ( keyspace , table ) ; }  catch (   InterruptedException e )  {    Thread . currentThread  ( ) . interrupt  ( ) ; } } } ) ; }   public void refreshSchema  (   final Connection connection ,   final ResultSetFuture future ,   final ResultSet rs ,   final String keyspace ,   final String table )  {  if  (  logger . isDebugEnabled  ( ) )   logger . debug  ( "Refreshing schema for {}{}" ,   keyspace == null ? "" : keyspace ,   table == null ? "" :  "." + table ) ;   executor . submit  (  new Runnable  ( )  {    @ Override public void run  ( )  {  try  {  if  (  !  ControlConnection . waitForSchemaAgreement  ( connection , metadata ) )   logger . warn  ( "No schema agreement from live replicas after {} ms. The schema may not be up to date on some nodes." ,  ControlConnection . MAX_SCHEMA_AGREEMENT_WAIT_MS ) ;   ControlConnection . refreshSchema  ( connection , keyspace , table ,   Cluster . Manager . this ) ; }  catch (   Exception e )  {   logger . error  ( "Error during schema refresh ({}). The schema from Cluster.getMetadata() might appear stale. Asynchronously submitting job to fix." ,  e . getMessage  ( ) ) ;   submitSchemaRefresh  ( keyspace , table ) ; }  finally  {   future . setResult  ( rs ) ; } } } ) ; }    @ Override public void handle  (   Message . Response response )  {  if  (  !  (  response instanceof  Responses . Event ) )  {   logger . error  ( "Received an unexpected message from the server: {}" , response ) ;  return ; }   final ProtocolEvent  event =   (  (  Responses . Event ) response ) . event ;   logger . debug  ( "Received event {}, scheduling delivery" , response ) ;   scheduledTasksExecutor . schedule  (  new Runnable  ( )  {    @ Override public void run  ( )  {  switch  (  event . type )  {   case TOPOLOGY_CHANGE :   ProtocolEvent . TopologyChange  tpc =  (  ProtocolEvent . TopologyChange ) event ;  switch  (  tpc . change )  {   case NEW_NODE :   addHost  (   tpc . node . getAddress  ( ) , true ) ;  break ;   case REMOVED_NODE :   removeHost  (  metadata . getHost  (   tpc . node . getAddress  ( ) ) ) ;  break ;   case MOVED_NODE :   controlConnection . refreshNodeListAndTokenMap  ( ) ;  break ; }  break ;   case STATUS_CHANGE :   ProtocolEvent . StatusChange  stc =  (  ProtocolEvent . StatusChange ) event ;  switch  (  stc . status )  {   case UP :  Host  hostUp =  metadata . getHost  (   stc . node . getAddress  ( ) ) ;  if  (  hostUp == null )  {   addHost  (   stc . node . getAddress  ( ) , true ) ; } else  {   onUp  ( hostUp ) ; }  break ;   case DOWN :  Host  hostDown =  metadata . getHost  (   stc . node . getAddress  ( ) ) ;  if  (  hostDown != null )  {   onDown  ( hostDown ) ; }  break ; }  break ;   case SCHEMA_CHANGE :   ProtocolEvent . SchemaChange  scc =  (  ProtocolEvent . SchemaChange ) event ;  switch  (  scc . change )  {   case CREATED :  if  (   scc . table . isEmpty  ( ) )   submitSchemaRefresh  ( null , null ) ; else   submitSchemaRefresh  (  scc . keyspace , null ) ;  break ;   case DROPPED :  if  (   scc . table . isEmpty  ( ) )   submitSchemaRefresh  ( null , null ) ; else   submitSchemaRefresh  (  scc . keyspace , null ) ;  break ;   case UPDATED :  if  (   scc . table . isEmpty  ( ) )   submitSchemaRefresh  (  scc . keyspace , null ) ; else   submitSchemaRefresh  (  scc . keyspace ,  scc . table ) ;  break ; }  break ; } } } ,  delayForEvent  ( event ) ,  TimeUnit . SECONDS ) ; }   private  int delayForEvent  (  ProtocolEvent event )  {  switch  (  event . type )  {   case TOPOLOGY_CHANGE :  return 1 ;   case STATUS_CHANGE :   ProtocolEvent . StatusChange  stc =  (  ProtocolEvent . StatusChange ) event ;  if  (   stc . status ==    ProtocolEvent . StatusChange . Status . UP )  return 1 ;  break ; }  return 0 ; }   private class ClusterShutdownFuture  extends  ShutdownFuture . Forwarding  {  ClusterShutdownFuture  (   List  < ShutdownFuture > futures )  {  super  ( futures ) ; }    @ Override public ShutdownFuture force  ( )  {   reconnectionExecutor . shutdownNow  ( ) ;   scheduledTasksExecutor . shutdownNow  ( ) ;   executor . shutdownNow  ( ) ;  return  super . force  ( ) ; }    @ Override protected void onFuturesDone  ( )  {    (  new Thread  ( "Shutdown-checker" )  {   public void run  ( )  {   connectionFactory . shutdown  ( ) ;  try  {   reconnectionExecutor . awaitTermination  (  Long . MAX_VALUE ,  TimeUnit . SECONDS ) ;   scheduledTasksExecutor . awaitTermination  (  Long . MAX_VALUE ,  TimeUnit . SECONDS ) ;   executor . awaitTermination  (  Long . MAX_VALUE ,  TimeUnit . SECONDS ) ;   set  ( null ) ; }  catch (   InterruptedException e )  {    Thread . currentThread  ( ) . interrupt  ( ) ;   setException  ( e ) ; } } } ) . start  ( ) ; } } } }