  package     com . grunka . random . fortuna . accumulator ;   import     com . grunka . random . fortuna . Pool ;  import   java . util . ArrayList ;  import   java . util . List ;  import    java . util . concurrent . ScheduledExecutorService ;  import    java . util . concurrent . ScheduledFuture ;  import     java . util . concurrent . atomic . AtomicBoolean ;  import     java . util . concurrent . atomic . AtomicInteger ;  import   java . util . HashSet ;  import   java . util . Set ;  import    java . util . concurrent . Future ;   public class Accumulator  {   private final AtomicInteger  sourceCount =  new AtomicInteger  ( 0 ) ;   private final  List  <  ScheduledFuture  <  ? > >  entropyFutures =  new  ArrayList  < >  ( ) ;   private final  Pool  [ ]  pools ;   private final ScheduledExecutorService  scheduler ;   public Accumulator  (   Pool  [ ] pools ,  ScheduledExecutorService scheduler )  {    this . pools = pools ;    this . scheduler = scheduler ; }   public  Pool  [ ] getPools  ( )  {  return pools ; }   public void addSource  (  EntropySource entropySource )  {   int  sourceId =  sourceCount . getAndIncrement  ( ) ;  EventAdder  eventAdder =  new EventAdderImpl  ( sourceId , pools ,  entropySource . getClass  ( ) ) ; 
<<<<<<<
 AtomicBoolean  scheduled =  new AtomicBoolean  ( ) ;
=======
>>>>>>>
   
<<<<<<<
entropySource
=======
futures
>>>>>>>
 . add  ( 
<<<<<<<
 (   ( delay , timeUnit ) ->  {   entropyFutures . add  (  scheduler . scheduleWithFixedDelay  (   ( ) ->  entropySource . event  ( eventAdder ) , 0 , delay , timeUnit ) ) ;   scheduled . set  ( true ) ; } )
=======
 entropySource . schedule  (   ( ) ->  entropySource . event  ( eventAdder ) , scheduler )
>>>>>>>
 ) ;  if  (  !  scheduled . get  ( ) )  {  throw  new IllegalStateException  (   "Entropy source " +   entropySource . getClass  ( ) . getName  ( ) + " was not scheduled to run" ) ; } }   public void shutdownSources  ( )  {   entropyFutures . forEach  (  f ->  f . cancel  ( false ) ) ;   entropyFutures . clear  ( ) ; }   private final  Set  <  Future  <  ? > >  futures =  new  HashSet  < >  ( ) ;   public void shutdown  (   long timeout ,  TimeUnit unit ,  boolean shutdownExecutor )  throws InterruptedException  {  for (  Future  <  ? > future : futures )  {   future . cancel  ( true ) ; }   futures . clear  ( ) ;  if  ( shutdownExecutor )  {   scheduler . shutdown  ( ) ;  if  (  !  scheduler . awaitTermination  ( timeout , unit ) )  {   scheduler . shutdownNow  ( ) ; } } } }