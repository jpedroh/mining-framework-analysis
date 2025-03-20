  package      com . graphhopper . jsprit . core . problem . constraint ;   import       com . graphhopper . jsprit . core . algorithm . state . InternalStates ;  import      com . graphhopper . jsprit . core . problem . SizeDimension ;  import       com . graphhopper . jsprit . core . problem . misc . JobInsertionContext ;  import         com . graphhopper . jsprit . core . problem . solution . route . state . RouteAndActivityStateGetter ;  import       com . graphhopper . jsprit . core . problem . job . AbstractJob ;   public class ServiceLoadRouteLevelConstraint  implements  HardRouteConstraint  {   private RouteAndActivityStateGetter  stateManager ; 
<<<<<<<
  private SizeDimension  defaultValue ;
=======
>>>>>>>
   public ServiceLoadRouteLevelConstraint  (  RouteAndActivityStateGetter stateManager )  {  super  ( ) ;    this . stateManager = stateManager ; 
<<<<<<<
  defaultValue =    SizeDimension . Builder . newInstance  ( ) . build  ( ) ;
=======
>>>>>>>
 }    @ Override public boolean fulfilled  (  JobInsertionContext insertionContext )  {  SizeDimension  maxLoadAtRoute =  stateManager . getRouteState  (  insertionContext . getRoute  ( ) ,  InternalStates . MAXLOAD ,  SizeDimension . class ) ;   maxLoadAtRoute =   (  maxLoadAtRoute != null ) ? maxLoadAtRoute :  Capacity . EMPTY ;  Capacity  capacityOfNewVehicle =    insertionContext . getNewVehicle  ( ) . getType  ( ) . getCapacityDimensions  ( ) ;  if  (  !  maxLoadAtRoute . isLessOrEqual  ( capacityOfNewVehicle ) )  {  return false ; }  AbstractJob  job =  ( AbstractJob )  insertionContext . getJob  ( ) ;  SizeDimension  loadAtDepot =  stateManager . getRouteState  (  insertionContext . getRoute  ( ) ,  InternalStates . LOAD_AT_BEGINNING ,  Capacity . class ) ;   loadAtDepot =   (  loadAtDepot != null ) ? loadAtDepot :  Capacity . EMPTY ;  if  (  !  (   loadAtDepot . add  (  job . getSizeAtStart  ( ) ) . isLessOrEqual  ( capacityOfNewVehicle ) ) )  {  return false ; }  Capacity  loadAtEnd =  stateManager . getRouteState  (  insertionContext . getRoute  ( ) ,  InternalStates . LOAD_AT_END ,  Capacity . class ) ;   loadAtEnd =   (  loadAtEnd != null ) ? loadAtEnd :  Capacity . EMPTY ;  if  (  !  (   loadAtEnd . add  (  job . getSizeAtEnd  ( ) ) . isLessOrEqual  ( capacityOfNewVehicle ) ) )  { 
<<<<<<<
 SizeDimension  loadAtDepot =  stateManager . getRouteState  (  insertionContext . getRoute  ( ) ,  InternalStates . LOAD_AT_BEGINNING ,  SizeDimension . class ) ;
=======
 return false ;
>>>>>>>
 } else  if  (    insertionContext . getJob  ( ) instanceof Pickup ||   insertionContext . getJob  ( ) instanceof Service )  {  SizeDimension  loadAtEnd =  stateManager . getRouteState  (  insertionContext . getRoute  ( ) ,  InternalStates . LOAD_AT_END ,  SizeDimension . class ) ;  if  (  loadAtEnd == null )  {   loadAtEnd = defaultValue ; }  if  (  !   loadAtEnd . add  (   insertionContext . getJob  ( ) . getSize  ( ) ) . isLessOrEqual  ( capacityDimensions ) )  {  return false ; } }  return true ; } }