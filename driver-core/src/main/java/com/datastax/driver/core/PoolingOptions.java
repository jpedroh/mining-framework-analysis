  package    com . datastax . driver . core ;   public class PoolingOptions  {   private static final  int  DEFAULT_MIN_REQUESTS_PER_CONNECTION = 25 ;   private static final  int  DEFAULT_MAX_REQUESTS_PER_CONNECTION = 100 ;   private static final  int  DEFAULT_CORE_POOL_LOCAL = 8 ;   private static final  int  DEFAULT_CORE_POOL_REMOTE = 2 ;   private static final  int  DEFAULT_MAX_POOL_LOCAL = 8 ;   private static final  int  DEFAULT_MAX_POOL_REMOTE = 2 ;   private static final  int  DEFAULT_MAX_REQUESTS_PER_HOST_LOCAL = 1024 ;   private static final  int  DEFAULT_MAX_REQUESTS_PER_HOST_REMOTE = 256 ;   private static final  int  DEFAULT_POOL_TIMEOUT_MILLIS = 5000 ;   private volatile  Cluster . Manager  manager ;   private final   int  [ ]  minSimultaneousRequestsPerConnection =  new  int  [ ]  { DEFAULT_MIN_REQUESTS_PER_CONNECTION , DEFAULT_MIN_REQUESTS_PER_CONNECTION , 0 } ;   private final   int  [ ]  maxSimultaneousRequestsPerConnection =  new  int  [ ]  { DEFAULT_MAX_REQUESTS_PER_CONNECTION , DEFAULT_MAX_REQUESTS_PER_CONNECTION , 0 } ;   private final   int  [ ]  coreConnections =  new  int  [ ]  { DEFAULT_CORE_POOL_LOCAL , DEFAULT_CORE_POOL_REMOTE , 0 } ;   private final   int  [ ]  maxConnections =  new  int  [ ]  { DEFAULT_MAX_POOL_LOCAL , DEFAULT_MAX_POOL_REMOTE , 0 } ;   private volatile  int  maxSimultaneousRequestsPerHostLocal = DEFAULT_MAX_REQUESTS_PER_HOST_LOCAL ;   private volatile  int  maxSimultaneousRequestsPerHostRemote = DEFAULT_MAX_REQUESTS_PER_HOST_REMOTE ;   private volatile  int  poolTimeoutMillis = DEFAULT_POOL_TIMEOUT_MILLIS ;   public PoolingOptions  ( )  { }  void register  (   Cluster . Manager manager )  {    this . manager = manager ; }   public  int getMinSimultaneousRequestsPerConnectionThreshold  (  HostDistance distance )  {  return  minSimultaneousRequestsPerConnection [  distance . ordinal  ( ) ] ; }   public synchronized PoolingOptions setMinSimultaneousRequestsPerConnectionThreshold  (  HostDistance distance ,   int newMinSimultaneousRequests )  {  if  (  distance ==  HostDistance . IGNORED )  throw  new IllegalArgumentException  (   "Cannot set min simultaneous requests per connection threshold for " + distance + " hosts" ) ;   checkRequestsPerConnectionRange  ( newMinSimultaneousRequests , "Min simultaneous requests per connection" , distance ) ;   checkRequestsPerConnectionOrder  ( newMinSimultaneousRequests ,  maxSimultaneousRequestsPerConnection [  distance . ordinal  ( ) ] , distance ) ;    minSimultaneousRequestsPerConnection [  distance . ordinal  ( ) ] = newMinSimultaneousRequests ;  return this ; }   public  int getMaxSimultaneousRequestsPerConnectionThreshold  (  HostDistance distance )  {  return  maxSimultaneousRequestsPerConnection [  distance . ordinal  ( ) ] ; }   public synchronized PoolingOptions setMaxSimultaneousRequestsPerConnectionThreshold  (  HostDistance distance ,   int newMaxSimultaneousRequests )  {  if  (  distance ==  HostDistance . IGNORED )  throw  new IllegalArgumentException  (   "Cannot set max simultaneous requests per connection threshold for " + distance + " hosts" ) ;   checkRequestsPerConnectionRange  ( newMaxSimultaneousRequests , "Max simultaneous requests per connection" , distance ) ;   checkRequestsPerConnectionOrder  (  minSimultaneousRequestsPerConnection [  distance . ordinal  ( ) ] , newMaxSimultaneousRequests , distance ) ;    maxSimultaneousRequestsPerConnection [  distance . ordinal  ( ) ] = newMaxSimultaneousRequests ;  return this ; }   public  int getCoreConnectionsPerHost  (  HostDistance distance )  {  return  coreConnections [  distance . ordinal  ( ) ] ; }   public synchronized PoolingOptions setCoreConnectionsPerHost  (  HostDistance distance ,   int newCoreConnections )  {  if  (  distance ==  HostDistance . IGNORED )  throw  new IllegalArgumentException  (   "Cannot set core connections per host for " + distance + " hosts" ) ;   checkConnectionsPerHostOrder  ( newCoreConnections ,  maxConnections [  distance . ordinal  ( ) ] , distance ) ;   int  oldCore =  coreConnections [  distance . ordinal  ( ) ] ;    coreConnections [  distance . ordinal  ( ) ] = newCoreConnections ;  if  (   oldCore < newCoreConnections &&  manager != null )   manager . ensurePoolsSizing  ( ) ;  return this ; }   public  int getMaxConnectionsPerHost  (  HostDistance distance )  {  return  maxConnections [  distance . ordinal  ( ) ] ; }   public synchronized PoolingOptions setMaxConnectionsPerHost  (  HostDistance distance ,   int newMaxConnections )  {  if  (  distance ==  HostDistance . IGNORED )  throw  new IllegalArgumentException  (   "Cannot set max connections per host for " + distance + " hosts" ) ;   checkConnectionsPerHostOrder  (  coreConnections [  distance . ordinal  ( ) ] , newMaxConnections , distance ) ;    maxConnections [  distance . ordinal  ( ) ] = newMaxConnections ;  return this ; }   public  int getPoolTimeoutMillis  ( )  {  return poolTimeoutMillis ; }   public PoolingOptions setPoolTimeoutMillis  (   int poolTimeoutMillis )  {  if  (  poolTimeoutMillis < 0 )  throw  new IllegalArgumentException  ( "Pool timeout must be positive" ) ;    this . poolTimeoutMillis = poolTimeoutMillis ;  return this ; }   public  int getMaxSimultaneousRequestsPerHostThreshold  (  HostDistance distance )  {  switch  ( distance )  {   case LOCAL :  return maxSimultaneousRequestsPerHostLocal ;   case REMOTE :  return maxSimultaneousRequestsPerHostRemote ;   default :  return 0 ; } }   public PoolingOptions setMaxSimultaneousRequestsPerHostThreshold  (  HostDistance distance ,   int newMaxRequests )  {  if  (   newMaxRequests <= 0 ||  newMaxRequests >  StreamIdGenerator . MAX_STREAM_PER_CONNECTION_V3 )  throw  new IllegalArgumentException  (  String . format  ( "Max requests must be in the range (1, %d)" ,  StreamIdGenerator . MAX_STREAM_PER_CONNECTION_V3 ) ) ;  switch  ( distance )  {   case LOCAL :   maxSimultaneousRequestsPerHostLocal = newMaxRequests ;  break ;   case REMOTE :   maxSimultaneousRequestsPerHostRemote = newMaxRequests ;  break ;   default :  throw  new IllegalArgumentException  (   "Cannot set max requests per host for " + distance + " hosts" ) ; }  return this ; }   public void refreshConnectedHosts  ( )  {   manager . refreshConnectedHosts  ( ) ; }   public void refreshConnectedHost  (  Host host )  {   manager . refreshConnectedHost  ( host ) ; }   private static void checkRequestsPerConnectionRange  (   int value ,  String description ,  HostDistance distance )  {  if  (   value < 0 ||  value >  StreamIdGenerator . MAX_STREAM_PER_CONNECTION_V2 )  throw  new IllegalArgumentException  (  String . format  ( "%s for %s hosts must be in the range (0, %d)" , description , distance ,  StreamIdGenerator . MAX_STREAM_PER_CONNECTION_V2 ) ) ; }   private static void checkRequestsPerConnectionOrder  (   int min ,   int max ,  HostDistance distance )  {  if  (  min > max )  throw  new IllegalArgumentException  (  String . format  ( "Min simultaneous requests per connection for %s hosts must be less than max (%d > %d)" , distance , min , max ) ) ; }   private static void checkConnectionsPerHostOrder  (   int core ,   int max ,  HostDistance distance )  {  if  (  core > max )  throw  new IllegalArgumentException  (  String . format  ( "Core connections for %s hosts must be less than max (%d > %d)" , distance , core , max ) ) ; } }