  package    org . movsim . simulator . roadnetwork ;   import   java . util . ArrayList ;  import   java . util . HashSet ;  import   java . util . Iterator ;  import   java . util . List ;  import   java . util . Set ;  import   javax . annotation . CheckForNull ;  import    org . jgrapht . graph . DefaultWeightedEdge ;  import   org . movsim . SimulationScan ;  import    org . movsim . roadmappings . RoadMapping ;  import      org . movsim . simulator . roadnetwork . boundaries . AbstractTrafficSource ;  import      org . movsim . simulator . roadnetwork . boundaries . SimpleRamp ;  import      org . movsim . simulator . roadnetwork . boundaries . TrafficSink ;  import      org . movsim . simulator . roadnetwork . controller . GradientProfile ;  import      org . movsim . simulator . roadnetwork . controller . RoadObject ;  import       org . movsim . simulator . roadnetwork . controller . RoadObject . RoadObjectType ;  import      org . movsim . simulator . roadnetwork . controller . RoadObjects ;  import      org . movsim . simulator . roadnetwork . controller . SpeedLimit ;  import      org . movsim . simulator . roadnetwork . controller . TrafficLight ;  import      org . movsim . simulator . roadnetwork . controller . VariableMessageSignDiversion ;  import      org . movsim . simulator . roadnetwork . predicates . VehicleWithinRange ;  import     org . movsim . simulator . vehicles . Vehicle ;  import    org . movsim . utilities . ExponentialMovingAverage ;  import    org . movsim . utilities . XYDataPoint ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;  import     com . google . common . base . Preconditions ;  import     com . google . common . base . Predicate ;  import     com . google . common . collect . ImmutableList ;  import     com . google . common . collect . Iterators ;   public class RoadSegment  extends DefaultWeightedEdge  implements   Iterable  < Vehicle >  {   private static final  long  serialVersionUID =  - 2991922063982378462L ;   private static final Logger  LOG =  LoggerFactory . getLogger  (  RoadSegment . class ) ;   static final  int  ID_NOT_SET =  - 1 ;   static final  int  INITIAL_ID = 1 ;   private static  int  nextId = INITIAL_ID ;   private RoadSegmentDirection  directionType =  RoadSegmentDirection . FORWARD ;   private final  int  id ;   private String  userId ;   private String  roadName ;   private final  double  roadLength ;   private final  int  laneCount ;   private final LaneSegment  laneSegments  [ ] ;   private  int  sizeSourceRoadSegments =  - 1 ;   private  int  sizeSinkRoadSegments =  - 1 ;   private final RoadObjects  roadObjects ;   private final SignalPoints  signalPoints =  new SignalPoints  ( ) ;   private final LaneSegment  overtakingSegment ;   private boolean  overtakingSegmentInitialized = false ;   private AbstractTrafficSource  trafficSource ;   private TrafficSink  sink ;   private RoadMapping  roadMapping ;   private RoadSegment  peerRoadSegment ;   private Node  origin =  new NodeImpl  ( "origin" ) ;   private Node  destination =  new NodeImpl  ( "destination" ) ;   private SimpleRamp  simpleRamp ;   private  double  meanFreeFlowSpeed =  - 1 ;   private  double  freeFlowSpeed =   RoadTypeSpeeds . INSTANCE . getDefaultFreeFlowSpeed  ( ) ;   public static class TestCar  {   public  double  s = 0.0 ;   public  double  vdiff = 0.0 ;   public  double  vel = 0.0 ;   public  double  acc = 0.0 ; }   public static void resetNextId  ( )  {   nextId = INITIAL_ID ; }   public static  int count  ( )  {  return  nextId - INITIAL_ID ; }   public RoadSegment  (   double roadLength ,   int laneCount )  {  assert  roadLength > 0.0 ;  assert  laneCount >= 1 :  "laneCount=" + laneCount ;   laneSegments =  new LaneSegment  [ laneCount ] ;  for (   int  index = 0 ;  index < laneCount ;  ++ index )  {    laneSegments [ index ] =  new LaneSegment  ( this ,  index + 1 ) ; }   id =  nextId ++ ;  assert  roadLength > 0 ;    this . roadLength = roadLength ;    this . laneCount = laneCount ;    this . roadObjects =  new RoadObjects  ( this ) ;   overtakingSegment =  new LaneSegment  ( this ,  Lanes . OVERTAKING ) ; }   public RoadSegment  (   double roadLength ,   int laneCount ,  RoadMapping roadMapping ,  RoadSegmentDirection roadSegmentDirection )  {  this  ( roadLength , laneCount ) ;    this . directionType = roadSegmentDirection ;    this . roadMapping =  Preconditions . checkNotNull  ( roadMapping ) ; }   public final void addDefaultSink  ( )  {  if  (  sink != null )  {   LOG . warn  (  "sink already set on road=" +  userId  ( ) ) ; }   sink =  new TrafficSink  ( this ) ; }   public final  int id  ( )  {  return id ; }   public final void setUserId  (  String userId )  {    this . userId = userId ; }   public final String userId  ( )  {  return   userId == null ?  Integer . toString  ( id ) : userId ; }   public final RoadSegmentDirection directionType  ( )  {  return directionType ; }   public final RoadMapping roadMapping  ( )  {  assert  roadMapping != null ;  return roadMapping ; }   public final void setRoadMapping  (  RoadMapping roadMapping )  {    this . roadMapping = roadMapping ; }   public final AbstractTrafficSource trafficSource  ( )  {  return trafficSource ; }   public final void setTrafficSource  (  AbstractTrafficSource trafficSource )  {   Preconditions . checkArgument  (   this . trafficSource == null ,   "roadSegment=" +  id  ( ) + " already has a traffic source." ) ;    this . trafficSource = trafficSource ; }   public final TrafficSink sink  ( )  {  return sink ; }   public final boolean hasSink  ( )  {  return  sink != null ; }   public final  double roadLength  ( )  {  return roadLength ; }   public final  int laneCount  ( )  {  return laneCount ; }   public void setLaneType  (   int lane ,   Lanes . Type laneType )  {    laneSegments [  lane - 1 ] . setType  ( laneType ) ;  if  (  roadMapping != null )  { } }   public  Lanes . Type laneType  (   int lane )  {  return   laneSegments [  lane - 1 ] . type  ( ) ; }   public  int trafficLaneMin  ( )  {   int  trafficLaneMin =  Lanes . MOST_INNER_LANE ;  while  (    laneSegments [  trafficLaneMin - 1 ] . type  ( ) !=   Lanes . Type . TRAFFIC )  {   ++ trafficLaneMin ; }  return trafficLaneMin ; }   public  int trafficLaneMax  ( )  {   int  trafficLaneMax = laneCount ;  while  (    laneSegments [  trafficLaneMax - 1 ] . type  ( ) !=   Lanes . Type . TRAFFIC )  {   -- trafficLaneMax ; }  return trafficLaneMax ; }   public final LaneSegment laneSegment  (   int lane )  {   Preconditions . checkArgument  (   lane >=  Lanes . LANE1 &&  lane <= laneCount ,  "lane=" + lane ) ;  return  laneSegments [  lane - 1 ] ; }   final void setSourceLaneSegmentForLane  (  LaneSegment sourceLaneSegment ,   int lane )  {   Preconditions . checkNotNull  ( sourceLaneSegment ) ;   Preconditions . checkArgument  (   lane >=  Lanes . LANE1 &&  lane <= laneCount ,  "lane=" + lane ) ;    laneSegments [  lane - 1 ] . setSourceLaneSegment  ( sourceLaneSegment ) ; }   public final LaneSegment sourceLaneSegment  (   int lane )  {   Preconditions . checkArgument  (   lane >=  Lanes . LANE1 &&  lane <= laneCount ,    "lane=" + lane + " not defined for roadId=" +  userId  ( ) ) ;  return   laneSegments [  lane - 1 ] . sourceLaneSegment  ( ) ; }   public final RoadSegment sourceRoadSegment  (   int lane )  {   Preconditions . checkArgument  (   lane >=  Lanes . LANE1 &&  lane <= laneCount ) ;  if  (    laneSegments [  lane - 1 ] . sourceLaneSegment  ( ) == null )  {  return null ; }  return    laneSegments [  lane - 1 ] . sourceLaneSegment  ( ) . roadSegment  ( ) ; }   public final  int sourceLane  (   int lane )  {   Preconditions . checkArgument  (   lane >=  Lanes . LANE1 &&  lane <= laneCount ) ;  if  (    laneSegments [  lane - 1 ] . sourceLaneSegment  ( ) == null )  {  return  Lanes . NONE ; }  return    laneSegments [  lane - 1 ] . sourceLaneSegment  ( ) . lane  ( ) ; }   final void setSinkLaneSegmentForLane  (  LaneSegment sinkLaneSegment ,   int lane )  {   Preconditions . checkNotNull  ( sinkLaneSegment ) ;   Preconditions . checkArgument  (   lane >=  Lanes . LANE1 &&  lane <= laneCount ) ;    laneSegments [  lane - 1 ] . setSinkLaneSegment  ( sinkLaneSegment ) ; }   final LaneSegment sinkLaneSegment  (   int lane )  {   Preconditions . checkArgument  (   lane >=  Lanes . LANE1 &&  lane <= laneCount ) ;  return   laneSegments [  lane - 1 ] . sinkLaneSegment  ( ) ; }   public final RoadSegment sinkRoadSegment  (   int lane )  {   Preconditions . checkArgument  (   lane >=  Lanes . LANE1 &&  lane <= laneCount ,    "lane=" + lane + " but laneCount=" + laneCount ) ;  if  (    laneSegments [  lane - 1 ] . sinkLaneSegment  ( ) == null )  {  return null ; }  return    laneSegments [  lane - 1 ] . sinkLaneSegment  ( ) . roadSegment  ( ) ; }    @ CheckForNull public RoadSegment sinkRoadSegmentPerId  (   int exitRoadSegmentId )  {  for ( LaneSegment laneSegment : laneSegments )  {  if  (  laneSegment . hasSinkLaneSegment  ( ) )  {  RoadSegment  sinkRoadSegment =   laneSegment . sinkLaneSegment  ( ) . roadSegment  ( ) ;  if  (   sinkRoadSegment . id  ( ) == exitRoadSegmentId )  {  return sinkRoadSegment ; } } }  return null ; }   final  int sinkLane  (   int lane )  {   Preconditions . checkArgument  (   lane >=  Lanes . LANE1 &&  lane <= laneCount ) ;  if  (    laneSegments [  lane - 1 ] . sinkLaneSegment  ( ) == null )  {  return  Lanes . NONE ; }  return    laneSegments [  lane - 1 ] . sinkLaneSegment  ( ) . lane  ( ) ; }   public final boolean hasUpstreamConnection  ( )  {  return   getSizeSourceRoadSegments  ( ) > 0 ; }   public final boolean hasDownstreamConnection  ( )  {  return   getSizeSinkRoadSegments  ( ) > 0 ; }   public final  int getSizeSinkRoadSegments  ( )  {  if  (  sizeSinkRoadSegments < 0 )  {   Set  < RoadSegment >  sinkRoadSegments =  new  HashSet  < >  ( ) ;  for ( LaneSegment laneSegment : laneSegments )  {  if  (  laneSegment . hasSinkLaneSegment  ( ) )  {   sinkRoadSegments . add  (   laneSegment . sinkLaneSegment  ( ) . roadSegment  ( ) ) ; } }   sizeSinkRoadSegments =  sinkRoadSegments . size  ( ) ; }  return sizeSinkRoadSegments ; }   public final  int getSizeSourceRoadSegments  ( )  {  if  (  sizeSourceRoadSegments < 0 )  {   Set  < RoadSegment >  sourceRoadSegments =  new  HashSet  < >  ( ) ;  for ( LaneSegment laneSegment : laneSegments )  {  if  (  laneSegment . hasSourceLaneSegment  ( ) )  {   sourceRoadSegments . add  (   laneSegment . sourceLaneSegment  ( ) . roadSegment  ( ) ) ; } }   sizeSourceRoadSegments =  sourceRoadSegments . size  ( ) ; }  return sizeSourceRoadSegments ; }   public boolean exitsOnto  (   int exitRoadSegmentId )  {  for (  final LaneSegment laneSegment : laneSegments )  {  if  (   laneSegment . type  ( ) ==   Lanes . Type . EXIT )  {  assert   laneSegment . sinkLaneSegment  ( ) != null :     "roadSegment=" +  userId  ( ) + " with lane=" +  laneSegment . lane  ( ) + " has no downstream connection." ;  if  (     laneSegment . sinkLaneSegment  ( ) . roadSegment  ( ) . id  ( ) == exitRoadSegmentId )  {  return true ; } } }  return false ; }   public void clearVehicles  ( )  {  for (  final LaneSegment laneSegment : laneSegments )  {   laneSegment . clearVehicles  ( ) ; } }   public  int getVehicleCount  ( )  {   int  vehicleCount = 0 ;  for (  final LaneSegment laneSegment : laneSegments )  {   vehicleCount +=  laneSegment . vehicleCount  ( ) ; }  return vehicleCount ; }   public  int getStoppedVehicleCount  ( )  {   int  stoppedVehicleCount = 0 ;  for (  final LaneSegment laneSegment : laneSegments )  {   stoppedVehicleCount +=  laneSegment . stoppedVehicleCount  ( ) ; }  return stoppedVehicleCount ; }   public  int getObstacleCount  ( )  {   int  obstacleCount = 0 ;  for (  final LaneSegment laneSegment : laneSegments )  {   obstacleCount +=  laneSegment . obstacleCount  ( ) ; }  return obstacleCount ; }   public  int getVehicleCount  (   int lane )  {   Preconditions . checkArgument  (   lane >=  Lanes . LANE1 &&  lane <= laneCount ) ;  return   laneSegments [  lane - 1 ] . vehicleCount  ( ) ; }   protected  double totalVehicleTravelTime  ( )  {   double  totalVehicleTravelTime = 0 ;  for (  final LaneSegment laneSegment : laneSegments )  {   totalVehicleTravelTime +=  laneSegment . totalVehicleTravelTime  ( ) ; }  return totalVehicleTravelTime ; }   protected  double totalVehicleTravelDistance  ( )  {   double  totalVehicleTravelDistance = 0 ;  for (  final LaneSegment laneSegment : laneSegments )  {   totalVehicleTravelDistance +=  laneSegment . totalVehicleTravelDistance  ( ) ; }  return totalVehicleTravelDistance ; }   protected  double totalVehicleFuelUsedLiters  ( )  {   double  totalVehicleFuelUsedLiters = 0 ;  for (  final LaneSegment laneSegment : laneSegments )  {   totalVehicleFuelUsedLiters +=  laneSegment . totalVehicleFuelUsedLiters  ( ) ; }  return totalVehicleFuelUsedLiters ; }   protected  double instantaneousConsumptionLitersPerSecond  ( )  {   double  vehicleFuelUsedLiters = 0 ;  for (  final LaneSegment laneSegment : laneSegments )  {   vehicleFuelUsedLiters +=  laneSegment . instantaneousFuelUsedLitersPerS  ( ) ; }  return vehicleFuelUsedLiters ; }   public  double meanSpeed  ( )  {   double  sumSpeed = 0 ;   int  vehCount = 0 ;  for (  final LaneSegment laneSegment : laneSegments )  {  for ( Vehicle veh : laneSegment )  {  if  (   veh . type  ( ) ==   Vehicle . Type . OBSTACLE )  {  continue ; }   sumSpeed +=  veh . getSpeed  ( ) ;   ++ vehCount ; } }  return   (  vehCount > 0 ) ?  sumSpeed / vehCount :  getMeanFreeflowSpeed  ( ) ; }   private  double getMeanFreeflowSpeed  ( )  {  if  (  meanFreeFlowSpeed < 0 )  {   double  sum = 0 ;   double  currentPosition = 0 ;   double  speedLimitPosition = 0 ;   double  currentSpeedLimit = freeFlowSpeed ;  for ( SpeedLimit speedLimit :  speedLimits  ( ) )  {   speedLimitPosition =  speedLimit . position  ( ) ;   sum +=  currentSpeedLimit *  (  speedLimitPosition - currentPosition ) ;   currentSpeedLimit =   speedLimit . getSpeedLimitKmh  ( ) / 3.6 ;   currentPosition = speedLimitPosition ; }   sum +=  currentSpeedLimit *  (  roadLength - speedLimitPosition ) ;   meanFreeFlowSpeed =  sum / roadLength ; }  return meanFreeFlowSpeed ; }   public  double instantaneousTravelTime  ( )  {  return  roadLength /  meanSpeed  ( ) ; }   int  i = 0 ;   public  double calcInstantaneousTravelTime  ( )  {   List  < XYDataPoint >  dataPoints =  new  ArrayList  < >  ( ) ;   int  step = 10 ;   double  minPos = roadLength ;   double  deltaMin = 0 ;   double  maxPos = 0 ;   double  deltaMax = 0 ;   double  a = 1.5 ;  for (  final LaneSegment laneSegment : laneSegments )  {  for ( Vehicle veh : laneSegment )  {  if  (   veh . type  ( ) ==   Vehicle . Type . OBSTACLE )  {  continue ; }   double  position =  roadLength -  veh . getDistanceToRoadSegmentEnd  ( ) ;   double  speed =  veh . getSpeed  ( ) ;   dataPoints . add  (  new XYDataPoint  ( position , speed ) ) ;  if  (  minPos > position )  {   minPos = position ;   deltaMin =  Math . abs  (   (   Math . pow  (  getSpeedLimit  (  position - step ) , 2 ) -  Math . pow  ( speed , 2 ) ) /  (  2 * a ) ) ; }  if  (  maxPos < position )  {   maxPos = position ;   deltaMax =  Math . abs  (   (   Math . pow  (  getSpeedLimit  (  position + step ) , 2 ) -  Math . pow  ( speed , 2 ) ) /  (  2 * a ) ) ; } } }  for (   int  x = 0 ;  x <  minPos - deltaMin ;  x =  x + step )  {   dataPoints . add  (  new XYDataPoint  ( x ,  getSpeedLimit  ( x ) ) ) ; }  for (   int  x =  (  int )  (  maxPos + deltaMax ) ;  x < roadLength ;  x =  x + step )  {   dataPoints . add  (  new XYDataPoint  ( x ,  getSpeedLimit  ( x ) ) ) ; }   double  time = 0 ;   double  v = 35 ;  for (   double  x = 0 ;  x < roadLength ;  x =  x + step )  {   double  vNew =  ExponentialMovingAverage . calcEMA  ( x , dataPoints , 20 ) ;   time +=  step /  (   (  v + vNew ) / 2 ) ;   v = vNew ; }  return time ; }   public  double getSpeedLimit  (   double position )  {   double  speedLimit = 35 ;  for ( SpeedLimit sl :  speedLimits  ( ) )  {  if  (  position <  sl . position  ( ) )  {  return speedLimit ; }   speedLimit =   sl . getSpeedLimitKmh  ( ) / 3.6 ; }  return speedLimit ; }   protected  int obstacleCount  ( )  {   int  obstacleCount = 0 ;  for (  final LaneSegment laneSegment : laneSegments )  {   obstacleCount +=  laneSegment . obstacleCount  ( ) ; }  return obstacleCount ; }   public Vehicle getVehicle  (   int lane ,   int index )  {  return   laneSegments [  lane - 1 ] . getVehicle  ( index ) ; }   public void removeFrontVehicleOnLane  (   int lane )  {    laneSegments [  lane - 1 ] . removeFrontVehicleOnLane  ( ) ; }   public  int removeVehiclesPastEnd  ( )  {   int  removedVehicleCount = 0 ;  for (  final LaneSegment laneSegment : laneSegments )  {   removedVehicleCount +=  laneSegment . removeVehiclesPastEnd  ( sink ) ; }  return removedVehicleCount ; }   public  Iterable  < Vehicle > getVehiclesPastEnd  ( )  {   ArrayList  < Vehicle >  vehiclesPastEnd =  new  ArrayList  < >  ( ) ;  for (  final LaneSegment laneSegment : laneSegments )  {   vehiclesPastEnd . addAll  (  laneSegment . getVehiclesPastEnd  ( sink ) ) ; }  return vehiclesPastEnd ; }   public void addObstacle  (  Vehicle obstacle )  {  assert   obstacle . type  ( ) ==   Vehicle . Type . OBSTACLE ;   obstacle . setRoadSegment  ( id , roadLength ) ;   addVehicle  ( obstacle ) ; }   public void addVehicle  (  Vehicle vehicle )  {   vehicle . setRoadSegment  ( id , roadLength ) ;    laneSegments [   vehicle . lane  ( ) - 1 ] . addVehicle  ( vehicle ) ; }   public void appendVehicle  (  Vehicle vehicle )  {   vehicle . setRoadSegment  ( id , roadLength ) ;    laneSegments [   vehicle . lane  ( ) - 1 ] . appendVehicle  ( vehicle ) ; }   public void updateRoadConditions  (   double dt ,   double simulationTime ,   long iterationCount )  {  for ( RoadObject roadObject : roadObjects )  {   roadObject . timeStep  ( dt , simulationTime , iterationCount ) ; } }   private boolean  updateSignalPointsBeforeOutflowCalled ;   protected void updateSignalPointsBeforeOutflow  (   double simulationTime )  {   updateSignalPointsBeforeOutflowCalled = true ;  for ( SignalPoint signalPoint : signalPoints )  {   signalPoint . clear  ( ) ;   signalPoint . registerPassingVehicles  ( simulationTime ,  iterator  ( ) ) ; } }   public void updateSignalPointsAfterOutflowAndInflow  (   double simulationTime )  {  assert updateSignalPointsBeforeOutflowCalled ;  for ( SignalPoint signalPoint : signalPoints )  {   signalPoint . registerPassingVehicles  ( simulationTime ,  iterator  ( ) ) ; }   updateSignalPointsBeforeOutflowCalled = false ; }   public  Iterator  < Vehicle > vehiclesWithinRange  (   double begin ,   double end )  {  return  Iterators . filter  (  iterator  ( ) ,  new VehicleWithinRange  ( begin , end ) ) ; }   public  Iterator  < Vehicle > filteredVehicles  (   Predicate  < Vehicle > predicate )  {  return  Iterators . filter  (  iterator  ( ) , predicate ) ; }   public void makeLaneChanges  (   double dt ,   double simulationTime ,   long iterationCount )  {  if  (   !  hasPeer  ( ) &&  laneCount < 2 )  {  return ; }  if  (  ! overtakingSegmentInitialized )  {   initOvertakingLane  ( ) ; }  for (  final LaneSegment laneSegment : laneSegments )  {  assert  laneSegment . assertInvariant  ( ) ;  for (   Iterator  < Vehicle >  vehIterator =  laneSegment . iterator  ( ) ;  vehIterator . hasNext  ( ) ; )  {  Vehicle  vehicle =  vehIterator . next  ( ) ;  assert   vehicle . roadSegmentId  ( ) == id ;  if  (  vehicle . inProcessOfLaneChange  ( ) )  {   vehicle . updateLaneChangeDelay  ( dt ) ; } else  if  (  vehicle . considerLaneChange  ( dt , this ) )  {   final  int  targetLane =  vehicle . getTargetLane  ( ) ;  assert  targetLane !=  Lanes . NONE ;  assert    laneSegment  ( targetLane ) . type  ( ) !=   Lanes . Type . ENTRANCE ;   vehIterator . remove  ( ) ;   vehicle . setLane  ( targetLane ) ;    laneSegment  ( targetLane ) . addVehicle  ( vehicle ) ; } else  if  (  vehicle . considerOvertakingViaPeer  ( dt , this ) )  {   LOG . debug  ( "### perform overtaking: vehicle={}" , vehicle ) ;   int  targetLane =  vehicle . getTargetLane  ( ) ;  assert  targetLane ==  Lanes . OVERTAKING ;   vehIterator . remove  ( ) ;   vehicle . setLane  ( targetLane ) ;   overtakingSegment . addVehicle  ( vehicle ) ; } } }   checkFinishingOvertaking  ( dt ) ; }   private void initOvertakingLane  ( )  {  LaneSegment  sinkLane1 =   laneSegment  (  Lanes . MOST_INNER_LANE ) . sinkLaneSegment  ( ) ;  if  (  sinkLane1 != null )  {   overtakingSegment . setSinkLaneSegment  (   sinkLane1 . roadSegment  ( ) . overtakingSegment ) ; }  LaneSegment  sourceLane1 =   laneSegment  (  Lanes . MOST_INNER_LANE ) . sourceLaneSegment  ( ) ;  if  (  sourceLane1 != null )  {   overtakingSegment . setSourceLaneSegment  (   sourceLane1 . roadSegment  ( ) . overtakingSegment ) ; }   overtakingSegmentInitialized = true ; }   private void checkFinishingOvertaking  (   double dt )  {  for (   Iterator  < Vehicle >  vehIterator =  overtakingSegment . iterator  ( ) ;  vehIterator . hasNext  ( ) ; )  {  Vehicle  vehicle =  vehIterator . next  ( ) ;  if  (  vehicle . inProcessOfLaneChange  ( ) )  {   vehicle . updateLaneChangeDelay  ( dt ) ; } else  if  (  vehicle . considerFinishOvertaking  ( dt ,  laneSegment  (  Lanes . MOST_INNER_LANE ) ) )  {   LOG . debug  ( "vehicle turns back into lane after overtaking: vehicle={}" , vehicle ) ;   int  targetLane =  vehicle . getTargetLane  ( ) ;  assert  targetLane ==  Lanes . MOST_INNER_LANE ;   vehIterator . remove  ( ) ;   vehicle . setLane  ( targetLane ) ;    laneSegment  (  Lanes . MOST_INNER_LANE ) . addVehicle  ( vehicle ) ; } } }   public void updateVehicleAccelerations  (   double dt ,   double simulationTime ,   long iterationCount )  {  for (  final LaneSegment laneSegment : laneSegments )  {  assert  laneSegment . laneIsSorted  ( ) ;  assert  laneSegment . assertInvariant  ( ) ;   final LaneSegment  leftLaneSegment =  getLeftLane  ( laneSegment ) ;  for (  final Vehicle vehicle : laneSegment )  {   vehicle . updateAcceleration  ( dt , this , laneSegment , leftLaneSegment ) ; } }  for (  final Vehicle vehicle : overtakingSegment )  {   vehicle . updateAcceleration  ( dt , this , overtakingSegment , null ) ; } }   private LaneSegment getLeftLane  (  LaneSegment laneSegment )  {  if  (    laneSegment . lane  ( ) +  Lanes . TO_LEFT >=  Lanes . MOST_INNER_LANE )  {  return  laneSegments [   laneSegment . lane  ( ) +  Lanes . TO_LEFT ] ; }  return null ; }   public void updateVehiclePositionsAndSpeeds  (   double dt ,   double simulationTime ,   long iterationCount )  {  for (  final LaneSegment laneSegment : laneSegments )  {  assert  laneSegment . laneIsSorted  ( ) ;  for (  final Vehicle vehicle : laneSegment )  {   vehicle . updatePositionAndSpeed  ( dt ) ; } }  for (  final Vehicle vehicle : overtakingSegment )  {   vehicle . updatePositionAndSpeed  ( dt ) ; } }   public void outFlow  (   double dt ,   double simulationTime ,   long iterationCount )  {   updateSignalPointsBeforeOutflow  ( simulationTime ) ;  for (  final LaneSegment laneSegment : laneSegments )  {   laneSegment . outFlow  ( dt , simulationTime , iterationCount ) ;  assert  laneSegment . assertInvariant  ( ) ; }   overtakingSegment . outFlow  ( dt , simulationTime , iterationCount ) ;  if  (  sink != null )  {   sink . timeStep  ( dt , simulationTime , iterationCount ) ; } }   public void inFlow  (   double dt ,   double simulationTime ,   long iterationCount )  {  assert  eachLaneIsSorted  ( ) ;  if  (  trafficSource != null )  {   trafficSource . timeStep  ( dt , simulationTime , iterationCount ) ;  assert  assertInvariant  ( ) ; }  if  (  simpleRamp != null )  {   simpleRamp . timeStep  ( dt , simulationTime , iterationCount ) ; } }   public Vehicle rearVehicleOnLane  (   int lane )  {  return   laneSegments [  lane - 1 ] . rearVehicle  ( ) ; }   public Vehicle rearVehicle  (   int lane ,   double vehiclePos )  {  return   laneSegments [  lane - 1 ] . rearVehicle  ( vehiclePos ) ; }   public Vehicle frontVehicleOnLane  (   int lane )  {  return   laneSegments [  lane - 1 ] . frontVehicle  ( ) ; }   public Vehicle frontVehicleOnLane  (  Vehicle vehicle )  {  return   laneSegments [   vehicle . lane  ( ) - 1 ] . frontVehicle  ( vehicle ) ; }   public Vehicle frontVehicle  (   int lane ,   double vehiclePos )  {  return   laneSegments [  lane - 1 ] . frontVehicle  ( vehiclePos ) ; }   public boolean eachLaneIsSorted  ( )  {  for (  final LaneSegment laneSegment : laneSegments )  {  if  (   laneSegment . laneIsSorted  ( ) == false )  {  return false ; } }  return true ; }    @ SuppressWarnings  ( "synthetic-access" ) private class VehicleIterator  implements   Iterator  < Vehicle > ,  Iterable  < Vehicle >  {   int  laneIndex ;   int  index ;   int  count ;   public VehicleIterator  ( )  { }    @ Override public boolean hasNext  ( )  {  if  (  index <   laneSegments [ laneIndex ] . vehicleCount  ( ) )  {  return true ; }   int  nextLane =  laneIndex + 1 ;  while  (  nextLane < laneCount )  {  if  (    laneSegments [ nextLane ] . vehicleCount  ( ) > 0 )  {  return true ; }   ++ nextLane ; }   final  int  vc =  getVehicleCount  ( ) ;  if  (  vc != count )  {  assert false ; }  return false ; }    @ Override public Vehicle next  ( )  {  if  (  index <   laneSegments [ laneIndex ] . vehicleCount  ( ) )  {   ++ count ;  return   laneSegments [ laneIndex ] . getVehicle  (  index ++ ) ; }   int  nextLane =  laneIndex + 1 ;  while  (  nextLane < laneCount )  {  if  (    laneSegments [ nextLane ] . vehicleCount  ( ) > 0 )  {   laneIndex = nextLane ;   index = 0 ;   ++ count ;  return   laneSegments [ laneIndex ] . getVehicle  (  index ++ ) ; }   ++ nextLane ; }  return null ; }    @ Override public void remove  ( )  {  throw  new UnsupportedOperationException  ( "remove() not implemented." ) ; }    @ Override public  Iterator  < Vehicle > iterator  ( )  {  return  new VehicleIterator  ( ) ; } }    @ Override public final  Iterator  < Vehicle > iterator  ( )  {  return  new VehicleIterator  ( ) ; }   public final  Iterator  < Vehicle > overtakingVehicles  ( )  {  return  overtakingSegment . iterator  ( ) ; }   final  Iterator  < Vehicle > iteratorAllVehicles  ( )  {  return  Iterators . concat  (  iterator  ( ) ,  overtakingVehicles  ( ) ) ; }   public void checkForInconsistencies  (   double time ,   long iterationCount ,  boolean isWithCrashExit )  {  for (  final LaneSegment laneSegment : laneSegments )  {   int  index =  - 1 ;  for ( Vehicle vehicle : laneSegment )  {   index ++ ;  if  (   vehicle . type  ( ) ==   Vehicle . Type . OBSTACLE )  {  continue ; }   final Vehicle  vehFront =  laneSegment . frontVehicle  ( vehicle ) ;   final  double  netDistance =  vehicle . getNetDistance  ( vehFront ) ;  if  (  netDistance < 0 )  {   LOG . error  ( "Crash happened!!!" ) ;   final StringBuilder  sb =  new StringBuilder  ( "\n" ) ;   sb . append  (  String . format  ( "Crash of Vehicle i=%d (vehId=%d) at x=%.4f " , index ,  vehicle . getId  ( ) ,  vehicle . getFrontPosition  ( ) ) ) ;  if  (  vehFront != null )  {   sb . append  (  String . format  ( "with veh (vehId=%d) in front at x=%.4f on lane=%d\n" ,  vehFront . getId  ( ) ,  vehFront . getFrontPosition  ( ) ,  vehicle . lane  ( ) ) ) ; }    sb . append  ( "internal nodeId=" ) . append  ( id ) ;    sb . append  ( ", roadId=" ) . append  ( userId ) ;    sb . append  ( ", net distance=" ) . append  ( netDistance ) ;    sb . append  ( ", lane=" ) . append  (  laneSegment . lane  ( ) ) ;    sb . append  ( ", container.size=" ) . append  (  laneSegment . vehicleCount  ( ) ) ;    sb . append  ( ", obstacles=" ) . append  (  laneSegment . obstacleCount  ( ) ) ;   sb . append  ( "\n" ) ;  for (   int  j =  Math . max  ( 0 ,  index - 8 ) ,  M =  laneSegment . vehicleCount  ( ) ;  j <=  Math . min  (  index + 8 ,  M - 1 ) ;  j ++ )  {   final Vehicle  veh =  laneSegment . getVehicle  ( j ) ;   sb . append  (  String . format  ( "veh=%d, pos=%6.2f, speed=%4.2f, accModel=%4.3f, acc=%4.3f, length=%3.1f, lane=%d, nodeId=%d%n" , j ,  veh . getFrontPosition  ( ) ,  veh . getSpeed  ( ) ,  veh . accModel  ( ) ,  veh . getAcc  ( ) ,  veh . getLength  ( ) ,  veh . lane  ( ) ,  veh . getId  ( ) ) ) ; }   LOG . error  (  sb . toString  ( ) ) ;  if  ( isWithCrashExit )  {   LOG . error  ( " !!! exit after crash !!! " ) ;   System . exit  (  - 99 ) ; } } } } }    @ SuppressWarnings  ( "synthetic-access" ) private class LaneSegmentIterator  implements   Iterator  < LaneSegment >  {   int  index ;   public LaneSegmentIterator  ( )  { }    @ Override public boolean hasNext  ( )  {  if  (  index < laneCount )  {  return true ; }  return false ; }    @ Override public LaneSegment next  ( )  {  if  (  index < laneCount )  {  return  laneSegments [  index ++ ] ; }  return null ; }    @ Override public void remove  ( )  {  throw  new UnsupportedOperationException  ( "remove() not implemented." ) ; } }   public final  Iterator  < LaneSegment > laneSegmentIterator  ( )  {  return  new LaneSegmentIterator  ( ) ; }   public  Iterable  < LaneSegment > laneSegments  ( )  {  return  ImmutableList . copyOf  (  laneSegmentIterator  ( ) ) ; }   public  Iterable  < TrafficLight > trafficLights  ( )  {  return  roadObjects . values  (  RoadObjectType . TRAFFICLIGHT ) ; }   public  Iterable  < SpeedLimit > speedLimits  ( )  {  return  roadObjects . values  (  RoadObjectType . SPEEDLIMIT ) ; }   public  Iterable  < VariableMessageSignDiversion > variableMessageSignDiversions  ( )  {  return  roadObjects . values  (  RoadObjectType . VMS_DIVERSION ) ; }   public  Iterable  < GradientProfile > gradientProfiles  ( )  {  return  roadObjects . values  (  RoadObjectType . GRADIENT_PROFILE ) ; }   public boolean assertInvariant  ( )  {   final RoadMapping  roadMapping =  roadMapping  ( ) ;  if  (  roadMapping != null )  {  assert   Math . abs  (   roadMapping . roadLength  ( ) -  roadLength  ( ) ) < 0.1 ; }  for (  final LaneSegment laneSegment : laneSegments )  {   laneSegment . assertInvariant  ( ) ; }  return true ; }   public void setSimpleRamp  (  SimpleRamp simpleRamp )  {    this . simpleRamp = simpleRamp ; }   public void setUserRoadname  (  String name )  {    this . roadName = name ; }   public RoadObjects roadObjects  ( )  {  return roadObjects ; }   public SignalPoints signalPoints  ( )  {  return signalPoints ; }   public Node getOriginNode  ( )  {  return origin ; }   public Node getDestinationNode  ( )  {  return destination ; }    @ Override public String toString  ( )  {  return               "RoadSegment [nodeId=" + id + ", userId=" + userId + ", roadName=" + roadName + ", roadLength=" + roadLength + ", laneCount=" + laneCount + ", " +  getOriginNode  ( ) + ", " +  getDestinationNode  ( ) + "]" ; }   public RoadSegment getPeerRoadSegment  ( )  {  return peerRoadSegment ; }   public final boolean hasPeer  ( )  {  return  peerRoadSegment != null ; }   public void setPeerRoadSegment  (  RoadSegment peerRoadSegment )  {   Preconditions . checkNotNull  ( peerRoadSegment ) ;   Preconditions . checkArgument  (  !  peerRoadSegment . equals  ( this ) ) ;    this . peerRoadSegment = peerRoadSegment ; }   double getFreeFlowSpeed  ( )  {  return freeFlowSpeed ; }  void setFreeFlowSpeed  (   double freeFlowSpeed )  {    this . freeFlowSpeed = freeFlowSpeed ; } }