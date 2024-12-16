  package    org . openpnp . machine . reference ;   import   java . text . DecimalFormat ;  import   java . util . ArrayList ;  import   java . util . Collections ;  import   java . util . Comparator ;  import   java . util . HashMap ;  import   java . util . HashSet ;  import   java . util . List ;  import   java . util . Set ;  import    java . util . stream . Collectors ;  import    java . util . stream . Stream ;  import     org . openpnp . gui . support . Wizard ;  import      org . openpnp . machine . reference . wizards . ReferencePnpJobProcessorConfigurationWizard ;  import    org . openpnp . model . BoardLocation ;  import    org . openpnp . model . Configuration ;  import    org . openpnp . model . Job ;  import    org . openpnp . model . LengthUnit ;  import    org . openpnp . model . Location ;  import    org . openpnp . model . Panel ;  import    org . openpnp . model . Part ;  import    org . openpnp . model . Placement ;  import    org . openpnp . spi . Feeder ;  import    org . openpnp . spi . FiducialLocator ;  import    org . openpnp . spi . Head ;  import    org . openpnp . spi . Machine ;  import    org . openpnp . spi . Nozzle ;  import    org . openpnp . spi . NozzleTip ;  import    org . openpnp . spi . PartAlignment ;  import    org . openpnp . spi . PnpJobPlanner ;  import      org . openpnp . spi . PnpJobProcessor . JobPlacement . Status ;  import     org . openpnp . spi . base . AbstractJobProcessor ;  import     org . openpnp . spi . base . AbstractPnpJobProcessor ;  import    org . openpnp . util . Collect ;  import    org . openpnp . util . MovableUtils ;  import    org . openpnp . util . Utils2D ;  import    org . openpnp . util . VisionUtils ;  import    org . pmw . tinylog . Logger ;  import    org . simpleframework . xml . Attribute ;  import    org . simpleframework . xml . Element ;  import    org . simpleframework . xml . Root ;  import     org . simpleframework . xml . core . Commit ;    @ Root public class ReferencePnpJobProcessor  extends AbstractPnpJobProcessor  {   public enum JobOrderHint  {  PartHeight ,  Part }   public static class PlannedPlacement  {   public final JobPlacement  jobPlacement ;   public final Nozzle  nozzle ;   public Feeder  feeder ;   public  PartAlignment . PartAlignmentOffset  alignmentOffsets ;   public PlannedPlacement  (  Nozzle nozzle ,  JobPlacement jobPlacement )  {    this . nozzle = nozzle ;    this . jobPlacement = jobPlacement ; }    @ Override public String toString  ( )  {  return   nozzle + " -> " +  jobPlacement . toString  ( ) ; } }    @ Attribute  (  required = false ) protected  @ Deprecated Boolean  parkWhenComplete = null ;    @ Element  (  required = false ) protected  @ Deprecated Boolean  autoSaveJob = null ;    @ Element  (  required = false )  @ Deprecated protected Boolean  autoSaveConfiguration = null ;    @ Element  (  required = false )  @ Deprecated protected Long  configSaveFrequencyMs = null ;    @ Attribute  (  required = false ) protected JobOrderHint  jobOrder =  JobOrderHint . PartHeight ;    @ Element  (  required = false ) public PnpJobPlanner  planner =  new SimplePnpJobPlanner  ( ) ;   protected Job  job ;   protected Machine  machine ;   protected Head  head ;   protected  List  < JobPlacement >  jobPlacements =  new  ArrayList  < >  ( ) ;   long  startTime ;   int  totalPartsPlaced ;   public ReferencePnpJobProcessor  ( )  { }   public synchronized void initialize  (  Job job )  throws Exception  {  if  (  job == null )  {  throw  new Exception  ( "Can't initialize with a null Job." ) ; }    this . job = job ;   currentStep =  new PreFlight  ( ) ;   this . fireJobState  (    Configuration . get  ( ) . getMachine  ( ) . getSignalers  ( ) ,   AbstractJobProcessor . State . STOPPED ) ; }   public synchronized  @ Override boolean next  ( )  throws JobProcessorException  {   this . fireJobState  (    Configuration . get  ( ) . getMachine  ( ) . getSignalers  ( ) ,   AbstractJobProcessor . State . RUNNING ) ;  try  {   currentStep =  currentStep . step  ( ) ; }  catch (   Exception e )  {   this . fireJobState  (    Configuration . get  ( ) . getMachine  ( ) . getSignalers  ( ) ,   AbstractJobProcessor . State . ERROR ) ;  throw e ; }  if  (  currentStep == null )  {   this . fireJobState  (    Configuration . get  ( ) . getMachine  ( ) . getSignalers  ( ) ,   AbstractJobProcessor . State . FINISHED ) ; } else  if  (    fsm . getState  ( ) ==  State . Plan &&  isJobComplete  ( ) )  {   fsm . send  (  Message . Complete ) ;   this . fireJobState  (   this . machine . getSignalers  ( ) ,   AbstractJobProcessor . State . FINISHED ) ;  return false ; }  return  currentStep != null ; }   public synchronized void abort  ( )  throws JobProcessorException  {  try  {    new Cleanup  ( ) . step  ( ) ; }  catch (   Exception e )  {   Logger . error  ( e ) ; }   this . fireJobState  (    Configuration . get  ( ) . getMachine  ( ) . getSignalers  ( ) ,   AbstractJobProcessor . State . STOPPED ) ;   currentStep = null ; } 
<<<<<<<
  protected void doChangeNozzleTip  ( )  throws Exception  {   fireTextStatus  ( "Checking nozzle tips." ) ;  for ( PlannedPlacement plannedPlacement : plannedPlacements )  {  if  (  plannedPlacement . stepComplete )  {  continue ; }  Nozzle  nozzle =  plannedPlacement . nozzle ;  JobPlacement  jobPlacement =  plannedPlacement . jobPlacement ;  Placement  placement =  jobPlacement . placement ;  Part  part =  placement . getPart  ( ) ;  if  (    nozzle . getNozzleTip  ( ) != null &&   nozzle . getNozzleTip  ( ) . canHandle  ( part ) )  {   Logger . debug  ( "No nozzle change needed for nozzle {}" , nozzle ) ;    plannedPlacement . stepComplete = true ;  continue ; }   fireTextStatus  ( "Changing nozzle tip on nozzle %s." ,  nozzle . getId  ( ) ) ;  NozzleTip  nozzleTip =  findNozzleTip  ( nozzle , part ) ;   fireTextStatus  ( "Change NozzleTip on Nozzle %s to %s." ,  nozzle . getId  ( ) ,  nozzleTip . getName  ( ) ) ;   Logger . debug  ( "Change NozzleTip on Nozzle {} from {} to {}" ,  new Object  [ ]  { nozzle ,  nozzle . getNozzleTip  ( ) , nozzleTip } ) ;   nozzle . unloadNozzleTip  ( ) ;   nozzle . loadNozzleTip  ( nozzleTip ) ;  if  (  nozzleTip != null )  {  if  (  !  nozzleTip . isCalibrated  ( ) )  {   Logger . debug  ( "Calibrating nozzle tip {} after change." , nozzleTip ) ;   nozzleTip . calibrate  ( ) ; } }    plannedPlacement . stepComplete = true ;   nozzleTipChanges ++ ; }   clearStepComplete  ( ) ; }
=======
>>>>>>>
   protected  List  < JobPlacement > getPendingJobPlacements  ( )  {  return     this . jobPlacements . stream  ( ) . filter  (   ( jobPlacement ) ->  {  return   jobPlacement . getStatus  ( ) ==  Status . Pending ; } ) . collect  (  Collectors . toList  ( ) ) ; }   protected boolean isJobComplete  ( )  {  return   getPendingJobPlacements  ( ) . isEmpty  ( ) ; }    @ Override public Wizard getConfigurationWizard  ( )  {  return  new ReferencePnpJobProcessorConfigurationWizard  ( this ) ; }   public JobOrderHint getJobOrder  ( )  {  return jobOrder ; }   public void setJobOrder  (  JobOrderHint newJobOrder )  {    this . jobOrder = newJobOrder ; }    @ Root public static class StandardPnpJobPlanner  implements  PnpJobPlanner  {  Head  head ;   public  List  < JobPlacement > plan  (  Head head ,   List  < JobPlacement > jobPlacements )  {    this . head = head ;   List  <  List  < JobPlacement > >  solutions =     head . getNozzles  ( ) . stream  ( ) . map  (  nozzle ->  {  return   Stream . concat  (   jobPlacements . stream  ( ) . filter  (  jobPlacement ->  {  return  nozzleCanHandle  ( nozzle ,   jobPlacement . getPlacement  ( ) . getPart  ( ) ) ; } ) ,  Stream . of  (  ( JobPlacement ) null ) ) . collect  (  Collectors . toList  ( ) ) ; } ) . collect  (  Collectors . toList  ( ) ) ;   List  < JobPlacement >  result =       Collect . cartesianProduct  ( solutions ) . stream  ( ) . filter  (  list ->  {   HashSet  < JobPlacement >  set =  new  HashSet  < >  ( ) ;  for ( JobPlacement jp : list )  {  if  (  jp == null )  {  continue ; }  if  (  set . contains  ( jp ) )  {  return false ; }   set . add  ( jp ) ; }  return true ; } ) . sorted  (  byFewestNulls . thenComparing  ( byFewestNozzleChanges ) ) . findFirst  ( ) . orElse  ( null ) ;  return result ; }   Comparator  <  List  < JobPlacement > >  byFewestNulls =   ( a , b ) ->  {  return   Collections . frequency  ( a , null ) -  Collections . frequency  ( b , null ) ; } ;   Comparator  <  List  < JobPlacement > >  byFewestNozzleChanges =   ( a , b ) ->  {   int  countA = 0 ,  countB = 0 ;  for (   int  i = 0 ;  i <   head . getNozzles  ( ) . size  ( ) ;  i ++ )  {  Nozzle  nozzle =   head . getNozzles  ( ) . get  ( i ) ;  JobPlacement  jpA =  a . get  ( i ) ;  JobPlacement  jpB =  b . get  ( i ) ;  if  (   nozzle . getNozzleTip  ( ) == null )  {   countA ++ ;   countB ++ ;  continue ; }  if  (   jpA != null &&  !   nozzle . getNozzleTip  ( ) . canHandle  (   jpA . getPlacement  ( ) . getPart  ( ) ) )  {   countA ++ ; }  if  (   jpB != null &&  !   nozzle . getNozzleTip  ( ) . canHandle  (   jpB . getPlacement  ( ) . getPart  ( ) ) )  {   countB ++ ; } }  return  countA - countB ; } ; }    @ Root public static class SimplePnpJobPlanner  implements  PnpJobPlanner  {    @ Override public  List  < JobPlacement > plan  (  Head head ,   List  < JobPlacement > jobPlacements )  {   jobPlacements . sort  (  new  Comparator  < JobPlacement >  ( )  {    @ Override public  int compare  (  JobPlacement o1 ,  JobPlacement o2 )  {   int  c1 = 0 ;  for ( Nozzle nozzle :  head . getNozzles  ( ) )  {  if  (  AbstractPnpJobProcessor . nozzleCanHandle  ( nozzle ,   o1 . getPlacement  ( ) . getPart  ( ) ) )  {   c1 ++ ; } }   int  c2 = 0 ;  for ( Nozzle nozzle :  head . getNozzles  ( ) )  {  if  (  AbstractPnpJobProcessor . nozzleCanHandle  ( nozzle ,   o2 . getPlacement  ( ) . getPart  ( ) ) )  {   c2 ++ ; } }  return  c1 - c2 ; } } ) ;   List  < JobPlacement >  result =  new  ArrayList  < >  ( ) ;  for ( Nozzle nozzle :  head . getNozzles  ( ) )  {  JobPlacement  solution = null ;  if  (   nozzle . getNozzleTip  ( ) != null )  {  for ( JobPlacement jobPlacement : jobPlacements )  {  Placement  placement =  jobPlacement . getPlacement  ( ) ;  Part  part =  placement . getPart  ( ) ;  if  (   nozzle . getNozzleTip  ( ) . canHandle  ( part ) )  {   solution = jobPlacement ;  break ; } } }  if  (  solution != null )  {   jobPlacements . remove  ( solution ) ;   result . add  ( solution ) ;  continue ; }  for ( JobPlacement jobPlacement : jobPlacements )  {  Placement  placement =  jobPlacement . getPlacement  ( ) ;  Part  part =  placement . getPart  ( ) ;  if  (  nozzleCanHandle  ( nozzle , part ) )  {   solution = jobPlacement ;  break ; } }  if  (  solution != null )  {   jobPlacements . remove  ( solution ) ;   result . add  ( solution ) ;  continue ; }   result . add  ( null ) ; }  return result ; } }  interface Step  {   public Step step  ( )  throws JobProcessorException ; }   private Step  currentStep = null ;    @ Commit public void commit  ( )  {   parkWhenComplete = null ;   autoSaveJob = null ;   autoSaveConfiguration = null ;   configSaveFrequencyMs = null ; }   protected class PreFlight  implements  Step  {   public Step step  ( )  throws JobProcessorException  {   startTime =  System . currentTimeMillis  ( ) ;   totalPartsPlaced = 0 ;   jobPlacements . clear  ( ) ;   machine =   Configuration . get  ( ) . getMachine  ( ) ;  try  {   head =  machine . getDefaultHead  ( ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( machine , e ) ; }   checkSetupErrors  ( ) ;   prepMachine  ( ) ;   scriptJobStarting  ( ) ;  return  new PanelFiducialCheck  ( ) ; }   private void checkSetupErrors  ( )  throws JobProcessorException  {   fireTextStatus  ( "Checking job for setup errors." ) ;  for ( BoardLocation boardLocation :  job . getBoardLocations  ( ) )  {  if  (  !  boardLocation . isEnabled  ( ) )  {  continue ; }   checkDuplicateRefs  ( boardLocation ) ;  for ( Placement placement :   boardLocation . getBoard  ( ) . getPlacements  ( ) )  {  if  (   placement . getType  ( ) !=   Placement . Type . Placement )  {  continue ; }  if  (  !  placement . isEnabled  ( ) )  {  continue ; }  if  (  boardLocation . getPlaced  (  placement . getId  ( ) ) )  {  continue ; }  if  (   placement . getSide  ( ) !=  boardLocation . getSide  ( ) )  {  continue ; }  JobPlacement  jobPlacement =  new JobPlacement  ( boardLocation , placement ) ;   checkJobPlacement  ( jobPlacement ) ;   jobPlacements . add  ( jobPlacement ) ; } } }   private void checkJobPlacement  (  JobPlacement jobPlacement )  throws JobProcessorException  {  BoardLocation  boardLocation =  jobPlacement . getBoardLocation  ( ) ;  Placement  placement =  jobPlacement . getPlacement  ( ) ;  if  (   placement . getPart  ( ) == null )  {  throw  new JobProcessorException  ( placement ,  String . format  ( "Part not found for board %s, placement %s." ,   boardLocation . getBoard  ( ) . getName  ( ) ,  placement . getId  ( ) ) ) ; }  if  (     placement . getPart  ( ) . getHeight  ( ) . getValue  ( ) <= 0D )  {  throw  new JobProcessorException  (  placement . getPart  ( ) ,  String . format  ( "Part height for %s must be greater than 0." ,   placement . getPart  ( ) . getId  ( ) ) ) ; }   findNozzleTip  ( head ,  placement . getPart  ( ) ) ;   findFeeder  ( machine ,  placement . getPart  ( ) ) ; }   private void scriptJobStarting  ( )  throws JobProcessorException  {   HashMap  < String , Object >  params =  new  HashMap  < >  ( ) ;   params . put  ( "job" , job ) ;   params . put  ( "jobProcessor" , this ) ;  try  {     Configuration . get  ( ) . getScripting  ( ) . on  ( "Job.Starting" , params ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( null , e ) ; } }   private void prepMachine  ( )  throws JobProcessorException  {   fireTextStatus  ( "Preparing machine." ) ;  try  {   head . moveToSafeZ  ( ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( head , e ) ; }   discardAll  ( head ) ; }   private void checkDuplicateRefs  (  BoardLocation boardLocation )  throws JobProcessorException  {   HashSet  < String >  idlist =  new  HashSet  < String >  ( ) ;  for ( Placement placement :   boardLocation . getBoard  ( ) . getPlacements  ( ) )  {  if  (  idlist . contains  (  placement . getId  ( ) ) )  {  throw  new JobProcessorException  ( boardLocation ,  String . format  ( "This board contains at least one duplicate ID entry: %s " ,  placement . getId  ( ) ) ) ; } else  {   idlist . add  (  placement . getId  ( ) ) ; } } } }   protected class PanelFiducialCheck  implements  Step  {   public Step step  ( )  throws JobProcessorException  {  FiducialLocator  locator =    Configuration . get  ( ) . getMachine  ( ) . getFiducialLocator  ( ) ;  if  (   job . isUsingPanel  ( ) &&    job . getPanels  ( ) . get  ( 0 ) . isCheckFiducials  ( ) )  {  Panel  p =   job . getPanels  ( ) . get  ( 0 ) ;  BoardLocation  boardLocation =   job . getBoardLocations  ( ) . get  ( 0 ) ;   fireTextStatus  ( "Panel fiducial check on %s" , boardLocation ) ;  try  {   locator . locateBoard  ( boardLocation ,  p . isCheckFiducials  ( ) ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( boardLocation , e ) ; } }  return  new BoardLocationFiducialCheck  ( ) ; } }   protected class BoardLocationFiducialCheck  implements  Step  {   protected  Set  < BoardLocation >  completed =  new  HashSet  < >  ( ) ;   public Step step  ( )  throws JobProcessorException  {  FiducialLocator  locator =    Configuration . get  ( ) . getMachine  ( ) . getFiducialLocator  ( ) ;  for ( BoardLocation boardLocation :  job . getBoardLocations  ( ) )  {  if  (  !  boardLocation . isEnabled  ( ) )  {  continue ; }  if  (  !  boardLocation . isCheckFiducials  ( ) )  {  continue ; }  if  (  completed . contains  ( boardLocation ) )  {  continue ; }   fireTextStatus  ( "Fiducial check for %s" , boardLocation ) ;  try  {   locator . locateBoard  ( boardLocation ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( boardLocation , e ) ; }   completed . add  ( boardLocation ) ;  return this ; }  return  new Plan  ( ) ; } }   protected class Plan  implements  Step  {   public Step step  ( )  throws JobProcessorException  {   List  < PlannedPlacement >  plannedPlacements =  new  ArrayList  < >  ( ) ;   fireTextStatus  ( "Planning placements." ) ;   List  < JobPlacement >  jobPlacements ;  if  (  jobOrder . equals  (  JobOrderHint . Part ) )  {   jobPlacements =     getPendingJobPlacements  ( ) . stream  ( ) . sorted  (  Comparator . comparing  (  JobPlacement :: getPartId ) ) . collect  (  Collectors . toList  ( ) ) ; } else  {   jobPlacements =     getPendingJobPlacements  ( ) . stream  ( ) . sorted  (  Comparator . comparing  (  JobPlacement :: getPartHeight ) ) . collect  (  Collectors . toList  ( ) ) ; }  if  (  jobPlacements . isEmpty  ( ) )  {  return  new Finish  ( ) ; }   long  t =  System . currentTimeMillis  ( ) ;   List  < JobPlacement >  result =  planner . plan  ( head , jobPlacements ) ;   Logger . debug  ( "Planner complete in {}ms: {}" ,  (   System . currentTimeMillis  ( ) - t ) , result ) ;  for ( Nozzle nozzle :  head . getNozzles  ( ) )  {  JobPlacement  jobPlacement =  result . remove  ( 0 ) ;  if  (  jobPlacement == null )  {  continue ; }   jobPlacement . setStatus  (  Status . Processing ) ;   plannedPlacements . add  (  new PlannedPlacement  ( nozzle , jobPlacement ) ) ; }  if  (   plannedPlacements . size  ( ) == 0 )  {  throw  new JobProcessorException  ( planner , "Planner failed to plan any placements. Please contact support." ) ; }   Logger . debug  ( "Planned placements {}" , plannedPlacements ) ;  return  new ChangeNozzleTips  ( plannedPlacements ) ; } }   protected class ChangeNozzleTips  extends PlannedPlacementStep  {   public ChangeNozzleTips  (   List  < PlannedPlacement > plannedPlacements )  {  super  ( plannedPlacements ) ; }    @ Override public Step stepImpl  (  PlannedPlacement plannedPlacement )  throws JobProcessorException  {  if  (  plannedPlacement == null )  {  return  new CalibrateNozzleTips  ( plannedPlacements ) ; }   final Nozzle  nozzle =  plannedPlacement . nozzle ;   final Part  part =    plannedPlacement . jobPlacement . getPlacement  ( ) . getPart  ( ) ;  if  (    nozzle . getNozzleTip  ( ) != null &&   nozzle . getNozzleTip  ( ) . canHandle  ( part ) )  {   Logger . debug  ( "No nozzle tip change needed for nozzle {}" , nozzle ) ;  return this ; }   fireTextStatus  ( "Locate nozzle tip on nozzle %s for part %s." ,  nozzle . getId  ( ) ,  part . getId  ( ) ) ;  NozzleTip  nozzleTip =  findNozzleTip  ( nozzle , part ) ;   fireTextStatus  ( "Change nozzle tip on nozzle %s to %s." ,  nozzle . getId  ( ) ,  nozzleTip . getName  ( ) ) ;  try  {   nozzle . unloadNozzleTip  ( ) ;   nozzle . loadNozzleTip  ( nozzleTip ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( nozzleTip , e ) ; }  return this ; } }   protected class CalibrateNozzleTips  extends PlannedPlacementStep  {   public CalibrateNozzleTips  (   List  < PlannedPlacement > plannedPlacements )  {  super  ( plannedPlacements ) ; }    @ Override public Step stepImpl  (  PlannedPlacement plannedPlacement )  throws JobProcessorException  {  if  (  plannedPlacement == null )  {  return  new Pick  ( plannedPlacements ) ; }   final NozzleTip  nozzleTip =   plannedPlacement . nozzle . getNozzleTip  ( ) ;  if  (  nozzleTip == null )  {  return this ; }  if  (  nozzleTip . isCalibrated  ( ) )  {  return this ; }   fireTextStatus  ( "Calibrate nozzle tip %s" , nozzleTip ) ;  try  {   nozzleTip . calibrate  ( ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( nozzleTip , e ) ; }  return this ; } }   protected class Pick  extends PlannedPlacementStep  {   HashMap  < PlannedPlacement , Integer >  retries =  new  HashMap  < >  ( ) ;   public Pick  (   List  < PlannedPlacement > plannedPlacements )  {  super  ( plannedPlacements ) ; }    @ Override public Step stepImpl  (  PlannedPlacement plannedPlacement )  throws JobProcessorException  {  if  (  plannedPlacement == null )  {  return  new Align  ( plannedPlacements ) ; }   final Nozzle  nozzle =  plannedPlacement . nozzle ;   final JobPlacement  jobPlacement =  plannedPlacement . jobPlacement ;   final Placement  placement =  jobPlacement . getPlacement  ( ) ;   final Part  part =  placement . getPart  ( ) ;   final Feeder  feeder =  findFeeder  ( machine , part ) ;   feed  ( feeder , nozzle ) ;   pick  ( nozzle , feeder , placement , part ) ;  try  {   postPick  ( feeder , nozzle ) ;   checkPartOn  ( nozzle ) ; }  catch (   JobProcessorException e )  {  if  (   retryIncrementAndGet  ( plannedPlacement ) >=  feeder . getPickRetryCount  ( ) )  {   retries . remove  ( plannedPlacement ) ;  throw e ; } else  {   discard  ( nozzle ) ;  return this ; } }  return this ; }   private  int retryIncrementAndGet  (  PlannedPlacement plannedPlacement )  {  Integer  retry =  retries . get  ( plannedPlacement ) ;  if  (  retry == null )  {   retry = 0 ; }   retry ++ ;   retries . put  ( plannedPlacement , retry ) ;  return retry ; }   private void feed  (  Feeder feeder ,  Nozzle nozzle )  throws JobProcessorException  {  Exception  lastException = null ;  for (   int  i = 0 ;  i <  Math . max  ( 1 ,  feeder . getFeedRetryCount  ( ) ) ;  i ++ )  {  try  {   fireTextStatus  ( "Feed %s on %s." ,  feeder . getName  ( ) ,   feeder . getPart  ( ) . getId  ( ) ) ;   feeder . feed  ( nozzle ) ;  return ; }  catch (   Exception e )  {   lastException = e ; } }  throw  new JobProcessorException  ( feeder , lastException ) ; }   private void pick  (  Nozzle nozzle ,  Feeder feeder ,  Placement placement ,  Part part )  throws JobProcessorException  {  try  {   fireTextStatus  ( "Pick %s from %s for %s." ,  part . getId  ( ) ,  feeder . getName  ( ) ,  placement . getId  ( ) ) ;   MovableUtils . moveToLocationAtSafeZ  ( nozzle ,  feeder . getPickLocation  ( ) ) ;   nozzle . pick  ( part ) ;   nozzle . moveToSafeZ  ( ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( nozzle , e ) ; } }   private void postPick  (  Feeder feeder ,  Nozzle nozzle )  throws JobProcessorException  {  try  {   feeder . postPick  ( nozzle ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( feeder , e ) ; } }   private void checkPartOn  (  Nozzle nozzle )  throws JobProcessorException  {  if  (  !  nozzle . isPartDetectionEnabled  ( ) )  {  return ; }  try  {  if  (  !  nozzle . isPartOn  ( ) )  {  throw  new JobProcessorException  ( nozzle , "No part detected after pick." ) ; } }  catch (   JobProcessorException e )  {  throw e ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( nozzle , e ) ; } } }   protected class Align  extends PlannedPlacementStep  {   public Align  (   List  < PlannedPlacement > plannedPlacements )  {  super  ( plannedPlacements ) ; }    @ Override public Step stepImpl  (  PlannedPlacement plannedPlacement )  throws JobProcessorException  {  if  (  plannedPlacement == null )  {  return  new Place  ( plannedPlacements ) ; }   final Nozzle  nozzle =  plannedPlacement . nozzle ;   final JobPlacement  jobPlacement =  plannedPlacement . jobPlacement ;   final Placement  placement =  jobPlacement . getPlacement  ( ) ;   final Part  part =  placement . getPart  ( ) ;   final PartAlignment  partAlignment =  findPartAligner  ( machine , part ) ;  if  (  partAlignment == null )  {    plannedPlacement . alignmentOffsets = null ;   Logger . debug  ( "Not aligning {} as no compatible enabled aligners defined" , part ) ;  return this ; }   align  ( plannedPlacement , partAlignment ) ;   checkPartOn  ( nozzle ) ;  return this ; }   private void align  (  PlannedPlacement plannedPlacement ,  PartAlignment partAlignment )  throws JobProcessorException  {   final Nozzle  nozzle =  plannedPlacement . nozzle ;   final JobPlacement  jobPlacement =  plannedPlacement . jobPlacement ;   final Placement  placement =  jobPlacement . getPlacement  ( ) ;   final BoardLocation  boardLocation =  jobPlacement . getBoardLocation  ( ) ;   final Part  part =  placement . getPart  ( ) ;  Exception  lastException = null ;  for (   int  i = 0 ;  i < 3 ;  i ++ )  {   fireTextStatus  ( "Aligning %s for %s." ,  part . getId  ( ) ,  placement . getId  ( ) ) ;  try  {    plannedPlacement . alignmentOffsets =  VisionUtils . findPartAlignmentOffsets  ( partAlignment , part , boardLocation ,  placement . getLocation  ( ) , nozzle ) ;   Logger . debug  ( "Align {} with {}, offsets {}" , part , nozzle ,  plannedPlacement . alignmentOffsets ) ;  return ; }  catch (   Exception e )  {   lastException = e ; } }  throw  new JobProcessorException  ( part , lastException ) ; }   private void checkPartOn  (  Nozzle nozzle )  throws JobProcessorException  {  if  (  !  nozzle . isPartDetectionEnabled  ( ) )  {  return ; }  try  {  if  (  !  nozzle . isPartOn  ( ) )  {  throw  new JobProcessorException  ( nozzle , "No part detected after alignment. Part may have been lost in transit." ) ; } }  catch (   JobProcessorException e )  {  throw e ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( nozzle , e ) ; } } }   protected class Place  extends PlannedPlacementStep  {   public Place  (   List  < PlannedPlacement > plannedPlacements )  {  super  ( plannedPlacements ) ; }    @ Override public Step stepImpl  (  PlannedPlacement plannedPlacement )  throws JobProcessorException  {  if  (  plannedPlacement == null )  {  return  new FinishCycle  ( ) ; }   final Nozzle  nozzle =  plannedPlacement . nozzle ;   final JobPlacement  jobPlacement =  plannedPlacement . jobPlacement ;   final Placement  placement =  jobPlacement . getPlacement  ( ) ;   final Part  part =  placement . getPart  ( ) ;   final BoardLocation  boardLocation =   plannedPlacement . jobPlacement . getBoardLocation  ( ) ;  Location  placementLocation =  getPlacementLocation  ( plannedPlacement ) ;   scriptBeforeAssembly  ( plannedPlacement , placementLocation ) ;   checkPartOn  ( nozzle ) ;   place  ( nozzle , part , placement , placementLocation ) ;   checkPartOff  ( nozzle , part ) ;   jobPlacement . setStatus  (  Status . Complete ) ;   boardLocation . setPlaced  (   jobPlacement . getPlacement  ( ) . getId  ( ) , true ) ;   totalPartsPlaced ++ ;   scriptComplete  ( plannedPlacement , placementLocation ) ;  return this ; }   private void place  (  Nozzle nozzle ,  Part part ,  Placement placement ,  Location placementLocation )  throws JobProcessorException  {   fireTextStatus  ( "Placing %s for %s." ,  part . getId  ( ) ,  placement . getId  ( ) ) ;  try  {   MovableUtils . moveToLocationAtSafeZ  ( nozzle , placementLocation ) ;   nozzle . place  ( ) ;   nozzle . moveToSafeZ  ( ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( nozzle , e ) ; } }   private void checkPartOn  (  Nozzle nozzle )  throws JobProcessorException  {  if  (  !  nozzle . isPartDetectionEnabled  ( ) )  {  return ; }  try  {  if  (  !  nozzle . isPartOn  ( ) )  {  throw  new JobProcessorException  ( nozzle , "No part detected on nozzle before place." ) ; } }  catch (   JobProcessorException e )  {  throw e ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( nozzle , e ) ; } }   private void checkPartOff  (  Nozzle nozzle ,  Part part )  throws JobProcessorException  {  if  (  !  nozzle . isPartDetectionEnabled  ( ) )  {  return ; }  try  {   nozzle . pick  ( part ) ;  boolean  partOff =  nozzle . isPartOff  ( ) ;   nozzle . place  ( ) ;  if  (  ! partOff )  {  throw  new JobProcessorException  ( nozzle , "Part detected on nozzle after place." ) ; } }  catch (   JobProcessorException e )  {  throw e ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( nozzle , e ) ; } }   private void scriptBeforeAssembly  (  PlannedPlacement plannedPlacement ,  Location placementLocation )  throws JobProcessorException  {   final Nozzle  nozzle =  plannedPlacement . nozzle ;   final JobPlacement  jobPlacement =  plannedPlacement . jobPlacement ;   final Placement  placement =  jobPlacement . getPlacement  ( ) ;   final Part  part =  placement . getPart  ( ) ;   final BoardLocation  boardLocation =   plannedPlacement . jobPlacement . getBoardLocation  ( ) ;  try  {   HashMap  < String , Object >  params =  new  HashMap  < >  ( ) ;   params . put  ( "job" , job ) ;   params . put  ( "jobProcessor" , this ) ;   params . put  ( "part" , part ) ;   params . put  ( "nozzle" , nozzle ) ;   params . put  ( "placement" , placement ) ;   params . put  ( "boardLocation" , boardLocation ) ;   params . put  ( "placementLocation" , placementLocation ) ;   params . put  ( "alignmentOffsets" ,  plannedPlacement . alignmentOffsets ) ;     Configuration . get  ( ) . getScripting  ( ) . on  ( "Job.Placement.BeforeAssembly" , params ) ; }  catch (   Exception e )  { } }   private void scriptComplete  (  PlannedPlacement plannedPlacement ,  Location placementLocation )  throws JobProcessorException  {   final Nozzle  nozzle =  plannedPlacement . nozzle ;   final JobPlacement  jobPlacement =  plannedPlacement . jobPlacement ;   final Placement  placement =  jobPlacement . getPlacement  ( ) ;   final Part  part =  placement . getPart  ( ) ;   final BoardLocation  boardLocation =   plannedPlacement . jobPlacement . getBoardLocation  ( ) ;  try  {   HashMap  < String , Object >  params =  new  HashMap  < >  ( ) ;   params . put  ( "job" , job ) ;   params . put  ( "jobProcessor" , this ) ;   params . put  ( "part" , part ) ;   params . put  ( "nozzle" , nozzle ) ;   params . put  ( "placement" , placement ) ;   params . put  ( "boardLocation" , boardLocation ) ;   params . put  ( "placementLocation" , placementLocation ) ;     Configuration . get  ( ) . getScripting  ( ) . on  ( "Job.Placement.Complete" , params ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( null , e ) ; } }   private Location getPlacementLocation  (  PlannedPlacement plannedPlacement )  {   final JobPlacement  jobPlacement =  plannedPlacement . jobPlacement ;   final Placement  placement =  jobPlacement . getPlacement  ( ) ;   final Part  part =  placement . getPart  ( ) ;   final BoardLocation  boardLocation =   plannedPlacement . jobPlacement . getBoardLocation  ( ) ;  Location  placementLocation =  Utils2D . calculateBoardPlacementLocation  ( boardLocation ,  placement . getLocation  ( ) ) ;  if  (   plannedPlacement . alignmentOffsets != null )  {  if  (   plannedPlacement . alignmentOffsets . getPreRotated  ( ) )  {   placementLocation =  placementLocation . subtractWithRotation  (   plannedPlacement . alignmentOffsets . getLocation  ( ) ) ; } else  {  Location  alignmentOffsets =   plannedPlacement . alignmentOffsets . getLocation  ( ) ;  Location  location =   new Location  (  LengthUnit . Millimeters ) . rotateXyCenterPoint  ( alignmentOffsets ,   placementLocation . getRotation  ( ) -  alignmentOffsets . getRotation  ( ) ) ;   location =  location . derive  ( null , null , null ,   placementLocation . getRotation  ( ) -  alignmentOffsets . getRotation  ( ) ) ;   location =  location . add  ( placementLocation ) ;   location =  location . subtract  ( alignmentOffsets ) ;   placementLocation = location ; } }   placementLocation =  placementLocation . add  (  new Location  (   part . getHeight  ( ) . getUnits  ( ) , 0 , 0 ,   part . getHeight  ( ) . getValue  ( ) , 0 ) ) ;  return placementLocation ; } }   protected class FinishCycle  implements  Step  {   public Step step  ( )  throws JobProcessorException  {   discardAll  ( head ) ;  return  new Plan  ( ) ; } }   protected class Cleanup  implements  Step  {   public Step step  ( )  throws JobProcessorException  {   fireTextStatus  ( "Cleaning up." ) ;  try  {   head . moveToSafeZ  ( ) ;   discardAll  ( head ) ;   head . moveToSafeZ  ( ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( head , e ) ; }   fireTextStatus  ( "Park head." ) ;  try  {   MovableUtils . park  ( head ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( head , e ) ; }  return null ; } }   protected class Finish  implements  Step  {   public Step step  ( )  throws JobProcessorException  {    new Cleanup  ( ) . step  ( ) ;   double  dtSec =   (   System . currentTimeMillis  ( ) - startTime ) / 1000.0 ;  DecimalFormat  df =  new DecimalFormat  ( "###,###.0" ) ;   List  < JobPlacement >  erroredPlacements =    jobPlacements . stream  ( ) . filter  (  jp ->  {  return   jp . getStatus  ( ) ==   JobPlacement . Status . Errored ; } ) . collect  (  Collectors . toList  ( ) ) ;   Logger . info  ( "Job finished {} parts in {} sec. This is {} CPH" , totalPartsPlaced ,  df . format  ( dtSec ) ,  df . format  (  totalPartsPlaced /  (  dtSec / 3600.0 ) ) ) ;  try  {   HashMap  < String , Object >  params =  new  HashMap  < >  ( ) ;   params . put  ( "job" , job ) ;   params . put  ( "jobProcessor" , this ) ;     Configuration . get  ( ) . getScripting  ( ) . on  ( "Job.Finished" , params ) ; }  catch (   Exception e )  {  throw  new JobProcessorException  ( null , e ) ; }  if  (  !  erroredPlacements . isEmpty  ( ) )  {   fireTextStatus  ( "Job finished with %d errors, placed %s parts in %s sec. (%s CPH)" ,  erroredPlacements . size  ( ) , totalPartsPlaced ,  df . format  ( dtSec ) ,  df . format  (  totalPartsPlaced /  (  dtSec / 3600.0 ) ) ) ; } else  {   fireTextStatus  ( "Job finished without error, placed %s parts in %s sec. (%s CPH)" , totalPartsPlaced ,  df . format  ( dtSec ) ,  df . format  (  totalPartsPlaced /  (  dtSec / 3600.0 ) ) ) ; }   Logger . info  ( "Errored Placements:" ) ;  for ( JobPlacement jobPlacement : erroredPlacements )  {   Logger . info  ( "{}: {}" , jobPlacement ,   jobPlacement . getError  ( ) . getMessage  ( ) ) ; }  return null ; } }   protected class Abort  implements  Step  {   public Step step  ( )  throws JobProcessorException  {    new Cleanup  ( ) . step  ( ) ;   fireTextStatus  ( "Aborted." ) ;  return null ; } }   protected abstract class PlannedPlacementStep  implements  Step  {   protected final  List  < PlannedPlacement >  plannedPlacements ;   private  Set  < PlannedPlacement >  completed =  new  HashSet  < >  ( ) ;   protected PlannedPlacementStep  (   List  < PlannedPlacement > plannedPlacements )  {    this . plannedPlacements = plannedPlacements ; }   protected abstract Step stepImpl  (  PlannedPlacement plannedPlacement )  throws JobProcessorException ;   public Step step  ( )  throws JobProcessorException  {  PlannedPlacement  plannedPlacement =      plannedPlacements . stream  ( ) . filter  (  p ->  {  return    p . jobPlacement . getStatus  ( ) ==  Status . Processing ; } ) . filter  (  p ->  {  return  !  completed . contains  ( p ) ; } ) . findFirst  ( ) . orElse  ( null ) ;  try  {  Step  result =  stepImpl  ( plannedPlacement ) ;   completed . add  ( plannedPlacement ) ;  return result ; }  catch (   JobProcessorException e )  {  switch  (    plannedPlacement . jobPlacement . getPlacement  ( ) . getErrorHandling  ( ) )  {   case Alert :  throw e ;   case Defer :    plannedPlacement . jobPlacement . setError  ( e ) ;  return this ;   default :  throw  new Error  (  "Unhandled Error Handling case " +    plannedPlacement . jobPlacement . getPlacement  ( ) . getErrorHandling  ( ) ) ; } } } } }