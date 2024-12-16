  package    com . datastax . driver . core ;   import     java . util . concurrent . atomic . AtomicInteger ;  import     java . util . concurrent . atomic . AtomicReference ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;  import      com . google . common . util . concurrent . MoreExecutors ;  import      com . datastax . driver . core . exceptions . AuthenticationException ;  import   java . util . ArrayList ;  import   java . util . List ;  import   java . util . Set ;  import    java . util . concurrent . CopyOnWriteArrayList ;  import    java . util . concurrent . CopyOnWriteArraySet ;  import    java . util . concurrent . TimeUnit ;  import    java . util . concurrent . TimeoutException ;  import     java . util . concurrent . locks . Condition ;  import     java . util . concurrent . locks . Lock ;  import     java . util . concurrent . locks . ReentrantLock ;  class HostConnectionPool  {   private static final Logger  logger =  LoggerFactory . getLogger  (  HostConnectionPool . class ) ;   private static final  int  MAX_SIMULTANEOUS_CREATION = 1 ;   private static final  int  MIN_AVAILABLE_STREAMS = 96 ;   public final Host  host ;   public volatile HostDistance  hostDistance ;   private final SessionManager  manager ;   private final  List  < Connection >  connections ;   private final AtomicInteger  open ;   private final  Set  < Connection >  trash =  new  CopyOnWriteArraySet  < Connection >  ( ) ;   private volatile  int  waiter = 0 ;   private final Lock  waitLock =  new ReentrantLock  ( true ) ;   private final Condition  hasAvailableConnection =  waitLock . newCondition  ( ) ;   private final Runnable  newConnectionTask ;   private final AtomicInteger  scheduledForCreation =  new AtomicInteger  ( ) ;   private final  AtomicReference  < ShutdownFuture >  shutdownFuture =  new  AtomicReference  < ShutdownFuture >  ( ) ;   public HostConnectionPool  (  Host host ,  HostDistance hostDistance ,  SessionManager manager )  throws ConnectionException  {  assert  hostDistance !=  HostDistance . IGNORED ;    this . host = host ;    this . hostDistance = hostDistance ;    this . manager = manager ;    this . newConnectionTask =  new Runnable  ( )  {    @ Override public void run  ( )  {   addConnectionIfUnderMaximum  ( ) ;   scheduledForCreation . decrementAndGet  ( ) ; } } ;   List  < Connection >  l =  new  ArrayList  < Connection >  (   options  ( ) . getCoreConnectionsPerHost  ( hostDistance ) ) ;  try  {  for (   int  i = 0 ;  i <   options  ( ) . getCoreConnectionsPerHost  ( hostDistance ) ;  i ++ )   l . add  (   manager . connectionFactory  ( ) . open  ( host ) ) ; }  catch (   InterruptedException e )  {    Thread . currentThread  ( ) . interrupt  ( ) ; }    this . connections =  new  CopyOnWriteArrayList  < Connection >  ( l ) ;    this . open =  new AtomicInteger  (  connections . size  ( ) ) ;   logger . trace  ( "Created connection pool to host {}" , host ) ; }   private PoolingOptions options  ( )  {  return   manager . configuration  ( ) . getPoolingOptions  ( ) ; }   public Connection borrowConnection  (   long timeout ,  TimeUnit unit )  throws ConnectionException , TimeoutException  {  if  (  isShutdown  ( ) )  throw  new ConnectionException  (  host . getAddress  ( ) , "Pool is shutdown" ) ;  if  (  connections . isEmpty  ( ) )  {  for (   int  i = 0 ;  i <   options  ( ) . getCoreConnectionsPerHost  ( hostDistance ) ;  i ++ )  {   scheduledForCreation . incrementAndGet  ( ) ;    manager . blockingExecutor  ( ) . submit  ( newConnectionTask ) ; }  Connection  c =  waitForConnection  ( timeout , unit ) ;   c . setKeyspace  (   manager . poolsState . keyspace ) ;  return c ; }   int  minInFlight =  Integer . MAX_VALUE ;  Connection  leastBusy = null ;  for ( Connection connection : connections )  {   int  inFlight =   connection . inFlight . get  ( ) ;  if  (  inFlight < minInFlight )  {   minInFlight = inFlight ;   leastBusy = connection ; } }  if  (   minInFlight >=   options  ( ) . getMaxSimultaneousRequestsPerConnectionThreshold  ( hostDistance ) &&   connections . size  ( ) <   options  ( ) . getMaxConnectionsPerHost  ( hostDistance ) )   maybeSpawnNewConnection  ( ) ;  while  ( true )  {   int  inFlight =   leastBusy . inFlight . get  ( ) ;  if  (  inFlight >=  leastBusy . maxAvailableStreams  ( ) )  {   leastBusy =  waitForConnection  ( timeout , unit ) ;  break ; }  if  (   leastBusy . inFlight . compareAndSet  ( inFlight ,  inFlight + 1 ) )  break ; }   leastBusy . setKeyspace  (   manager . poolsState . keyspace ) ;  return leastBusy ; }   private void awaitAvailableConnection  (   long timeout ,  TimeUnit unit )  throws InterruptedException  {   waitLock . lock  ( ) ;   waiter ++ ;  try  {   hasAvailableConnection . await  ( timeout , unit ) ; }  finally  {   waiter -- ;   waitLock . unlock  ( ) ; } }   private void signalAvailableConnection  ( )  {  if  (  waiter == 0 )  return ;   waitLock . lock  ( ) ;  try  {   hasAvailableConnection . signal  ( ) ; }  finally  {   waitLock . unlock  ( ) ; } }   private void signalAllAvailableConnection  ( )  {  if  (  waiter == 0 )  return ;   waitLock . lock  ( ) ;  try  {   hasAvailableConnection . signalAll  ( ) ; }  finally  {   waitLock . unlock  ( ) ; } }   private Connection waitForConnection  (   long timeout ,  TimeUnit unit )  throws ConnectionException , TimeoutException  {   long  start =  System . nanoTime  ( ) ;   long  remaining = timeout ;  do  {  try  {   awaitAvailableConnection  ( remaining , unit ) ; }  catch (   InterruptedException e )  {    Thread . currentThread  ( ) . interrupt  ( ) ;   timeout = 0 ; }  if  (  isShutdown  ( ) )  throw  new ConnectionException  (  host . getAddress  ( ) , "Pool is shutdown" ) ;   int  minInFlight =  Integer . MAX_VALUE ;  Connection  leastBusy = null ;  for ( Connection connection : connections )  {   int  inFlight =   connection . inFlight . get  ( ) ;  if  (  inFlight < minInFlight )  {   minInFlight = inFlight ;   leastBusy = connection ; } }  while  ( true )  {   int  inFlight =   leastBusy . inFlight . get  ( ) ;  if  (  inFlight >=  leastBusy . maxAvailableStreams  ( ) )  break ;  if  (   leastBusy . inFlight . compareAndSet  ( inFlight ,  inFlight + 1 ) )  return leastBusy ; }   remaining =  timeout -  Cluster . timeSince  ( start , unit ) ; } while  (  remaining > 0 ) ;  throw  new TimeoutException  ( ) ; }   public void returnConnection  (  Connection connection )  {   int  inFlight =   connection . inFlight . decrementAndGet  ( ) ;  if  (  connection . isDefunct  ( ) )  {  if  (    manager . cluster . manager . signalConnectionFailure  ( host ,  connection . lastException  ( ) , false ) )   shutdown  ( ) ; else   replace  ( connection ) ; } else  {  if  (   trash . contains  ( connection ) &&  inFlight == 0 )  {  if  (  trash . remove  ( connection ) )   close  ( connection ) ;  return ; }  if  (    connections . size  ( ) >   options  ( ) . getCoreConnectionsPerHost  ( hostDistance ) &&  inFlight <=   options  ( ) . getMinSimultaneousRequestsPerConnectionThreshold  ( hostDistance ) )  {   trashConnection  ( connection ) ; } else  if  (   connection . maxAvailableStreams  ( ) < MIN_AVAILABLE_STREAMS )  {   replaceConnection  ( connection ) ; } else  {   signalAvailableConnection  ( ) ; } } }   private void replaceConnection  (  Connection connection )  {   open . decrementAndGet  ( ) ;   maybeSpawnNewConnection  ( ) ;   doTrashConnection  ( connection ) ; }   private boolean trashConnection  (  Connection connection )  {  for ( ; ; )  {   int  opened =  open . get  ( ) ;  if  (  opened <=   options  ( ) . getCoreConnectionsPerHost  ( hostDistance ) )  return false ;  if  (  open . compareAndSet  ( opened ,  opened - 1 ) )  break ; }   doTrashConnection  ( connection ) ;  return true ; }   private void doTrashConnection  (  Connection connection )  {   trash . add  ( connection ) ;   connections . remove  ( connection ) ;  if  (     connection . inFlight . get  ( ) == 0 &&  trash . remove  ( connection ) )   close  ( connection ) ; }   private boolean addConnectionIfUnderMaximum  ( )  {  for ( ; ; )  {   int  opened =  open . get  ( ) ;  if  (  opened >=   options  ( ) . getMaxConnectionsPerHost  ( hostDistance ) )  return false ;  if  (  open . compareAndSet  ( opened ,  opened + 1 ) )  break ; }  if  (  isShutdown  ( ) )  {   open . decrementAndGet  ( ) ;  return false ; }  try  {   connections . add  (   manager . connectionFactory  ( ) . open  ( host ) ) ;   signalAvailableConnection  ( ) ;  return true ; }  catch (   InterruptedException e )  {    Thread . currentThread  ( ) . interrupt  ( ) ;   open . decrementAndGet  ( ) ;  return false ; }  catch (   ConnectionException e )  {   open . decrementAndGet  ( ) ;   logger . debug  ( "Connection error to {} while creating additional connection" , host ) ;  if  (    manager . cluster . manager . signalConnectionFailure  ( host , e , false ) )   shutdown  ( ) ;  return false ; }  catch (   AuthenticationException e )  {   open . decrementAndGet  ( ) ;   logger . error  ( "Authentication error while creating additional connection (error is: {})" ,  e . getMessage  ( ) ) ;   shutdown  ( ) ;  return false ; } }   private void maybeSpawnNewConnection  ( )  {  while  ( true )  {   int  inCreation =  scheduledForCreation . get  ( ) ;  if  (  inCreation >= MAX_SIMULTANEOUS_CREATION )  return ;  if  (  scheduledForCreation . compareAndSet  ( inCreation ,  inCreation + 1 ) )  break ; }   logger . debug  ( "Creating new connection on busy pool to {}" , host ) ;    manager . blockingExecutor  ( ) . submit  ( newConnectionTask ) ; }   private void replace  (   final Connection connection )  {   connections . remove  ( connection ) ;    manager . blockingExecutor  ( ) . submit  (  new Runnable  ( )  {    @ Override public void run  ( )  {   connection . close  ( ) ;   addConnectionIfUnderMaximum  ( ) ; } } ) ; }   private void close  (   final Connection connection )  {    manager . blockingExecutor  ( ) . submit  (  new Runnable  ( )  {    @ Override public void run  ( )  {   connection . close  ( ) ; } } ) ; }   public boolean isShutdown  ( )  {  return   shutdownFuture . get  ( ) != null ; }   public ShutdownFuture shutdown  ( )  {  ShutdownFuture  future =  shutdownFuture . get  ( ) ;  if  (  future != null )  return future ;   logger . debug  ( "Shutting down pool" ) ;   signalAllAvailableConnection  ( ) ;   future =  new  ShutdownFuture . Forwarding  (  discardAvailableConnections  ( ) ) ;  return   shutdownFuture . compareAndSet  ( null , future ) ? future :  shutdownFuture . get  ( ) ; }   public  int opened  ( )  {  return  open . get  ( ) ; }   private  List  < ShutdownFuture > discardAvailableConnections  ( )  {   List  < ShutdownFuture >  futures =  new  ArrayList  < ShutdownFuture >  (  connections . size  ( ) ) ;  for ( Connection connection : connections )  {  ShutdownFuture  future =  connection . close  ( ) ;   future . addListener  (  new Runnable  ( )  {   public void run  ( )  {   open . decrementAndGet  ( ) ; } } ,  MoreExecutors . sameThreadExecutor  ( ) ) ;   futures . add  ( future ) ; }  return futures ; }   public void ensureCoreConnections  ( )  {  if  (  isShutdown  ( ) )  return ;   int  opened =  open . get  ( ) ;  for (   int  i = opened ;  i <   options  ( ) . getCoreConnectionsPerHost  ( hostDistance ) ;  i ++ )  {   scheduledForCreation . incrementAndGet  ( ) ;    manager . blockingExecutor  ( ) . submit  ( newConnectionTask ) ; } }   static class PoolState  {   volatile String  keyspace ;   public void setKeyspace  (  String keyspace )  {    this . keyspace = keyspace ; } } }