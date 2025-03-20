  package     com . grunka . random . fortuna . entropy ;   import      com . grunka . random . fortuna . accumulator . EntropySource ;  import      com . grunka . random . fortuna . accumulator . EventAdder ;  import   java . io . FileInputStream ;  import   java . io . IOException ;  import    java . util . concurrent . TimeUnit ;  import    java . util . concurrent . Future ;  import    java . util . concurrent . ScheduledExecutorService ;  import    java . util . logging . Level ;  import    java . util . logging . Logger ;   public class URandomEntropySource  implements  EntropySource  {   private final   byte  [ ]  bytes =  new  byte  [ 32 ] ;    @ Override public void schedule  (  EventScheduler scheduler )  {   scheduler . schedule  ( 100 ,  TimeUnit . MILLISECONDS ) ; }    @ Override public void event  (  EventAdder adder )  {  try  {  try  (  FileInputStream inputStream =  new FileInputStream  ( 
<<<<<<<
"/dev/urandom"
=======
DEV_URANDOM
>>>>>>>
 ) )  {   int  bytesRead =  inputStream . read  ( bytes ) ;  assert  bytesRead ==  bytes . length ;   adder . add  ( bytes ) ; } }  catch (   IOException e )  { 
<<<<<<<
 throw  new UnsupportedOperationException  ( "Could not open /dev/urandom" , e ) ;
=======
  LOGGER . log  (  Level . WARNING ,  "Cannot read random bytes from " + DEV_URANDOM , e ) ;
>>>>>>>
 } }   private static final Logger  LOGGER =  Logger . getLogger  (   URandomEntropySource . class . getName  ( ) ) ;   private static final String  DEV_URANDOM = "/dev/urandom" ;    @ Override public  Future  <  ? > schedule  (  Runnable runnable ,  ScheduledExecutorService scheduler )  {  return  scheduler . scheduleWithFixedDelay  ( runnable , 0 , 100 ,  TimeUnit . MILLISECONDS ) ; } }