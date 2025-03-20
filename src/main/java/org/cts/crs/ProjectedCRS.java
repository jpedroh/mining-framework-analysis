  package   org . cts . crs ;   import    org . cts . op . CoordinateOperation ;  import   org . cts . Identifier ;  import    org . cts . op . NonInvertibleOperationException ;  import    org . cts . cs . Axis ;  import    org . cts . cs . CoordinateSystem ;  import    org . cts . datum . GeodeticDatum ;  import    org . cts . op . ChangeCoordinateDimension ;  import    org . cts . op . CoordinateOperationSequence ;  import    org . cts . op . CoordinateSwitch ;  import    org . cts . op . UnitConversion ;  import     org . cts . op . projection . Projection ;  import    org . cts . units . Unit ;  import   java . util . ArrayList ;  import   java . util . List ;  import static     org . cts . cs . Axis . EASTING ;  import static     org . cts . cs . Axis . NORTHING ;  import static     org . cts . units . Unit . METER ;   public class ProjectedCRS  extends GeodeticCRS  {   public static CoordinateSystem  EN_CS =  new CoordinateSystem  (  new Axis  [ ]  { EASTING , NORTHING } ,  new Unit  [ ]  { METER , METER } ) ;   public static CoordinateSystem  NE_CS =  new CoordinateSystem  (  new Axis  [ ]  { NORTHING , EASTING } ,  new Unit  [ ]  { METER , METER } ) ;   private Projection  projection ;   public ProjectedCRS  (  Identifier identifier ,  GeodeticDatum datum ,  CoordinateSystem coordSys ,  Projection projection )  {  super  ( identifier , datum , coordSys ) ;    this . projection = projection ; }   public ProjectedCRS  (  Identifier identifier ,  GeodeticDatum datum ,  Projection projection )  {  super  ( identifier , datum , EN_CS ) ;    this . projection = projection ; }    @ Override public Projection getProjection  ( )  {  return projection ; }    @ Override public Type getType  ( )  {  return  Type . PROJECTED ; }    @ Override public CoordinateOperation toGeographicCoordinateConverter  ( )  throws NonInvertibleOperationException  {   List  < CoordinateOperation >  ops =  new  ArrayList  < CoordinateOperation >  ( ) ;  if  (    getCoordinateSystem  ( ) . getUnit  ( 0 ) !=  Unit . METER )  {   ops . add  (  UnitConversion . createUnitConverter  (   getCoordinateSystem  ( ) . getUnit  ( 0 ) , METER ) ) ; }   ops . add  (  ChangeCoordinateDimension . TO3D ) ;  if  (    getCoordinateSystem  ( ) . getAxis  ( 0 ) != EASTING )  {   ops . add  (  CoordinateSwitch . SWITCH_LAT_LON ) ; }   ops . add  (  projection . inverse  ( ) ) ;  return  new CoordinateOperationSequence  (  new Identifier  (  CoordinateOperationSequence . class ) , ops ) ; }    @ Override public CoordinateOperation fromGeographicCoordinateConverter  ( )  {   List  < CoordinateOperation >  ops =  new  ArrayList  < CoordinateOperation >  ( ) ;   ops . add  (  ChangeCoordinateDimension . TO2D ) ;   ops . add  ( projection ) ;  if  (    getCoordinateSystem  ( ) . getAxis  ( 0 ) != EASTING )  {   ops . add  (  CoordinateSwitch . SWITCH_LAT_LON ) ; }  if  (    getCoordinateSystem  ( ) . getUnit  ( 0 ) !=  Unit . METER )  {   ops . add  (  UnitConversion . createUnitConverter  (  Unit . METER ,   getCoordinateSystem  ( ) . getUnit  ( 0 ) ) ) ; }  return  new CoordinateOperationSequence  (  new Identifier  (  CoordinateOperationSequence . class ) , ops ) ; }    @ Override public boolean equals  (  Object o )  {  if  (  this == o )  {  return true ; }  if  (  o instanceof 
<<<<<<<
GeodeticCRS
=======
ProjectedCRS
>>>>>>>
 )  {  
<<<<<<<
GeodeticCRS
=======
ProjectedCRS
>>>>>>>
  crs =  ( 
<<<<<<<
GeodeticCRS
=======
ProjectedCRS
>>>>>>>
 ) o ;    System . out . println  ( this ) ;    System . out . println  ( crs ) ;  if  (  !   getType  ( ) . equals  (  crs . getType  ( ) ) )  {  return false ; }  if  (   getIdentifier  ( ) . equals  (  crs . getIdentifier  ( ) ) )  {  return true ; }  boolean  nadgrids ;  if  (   getGridTransformations  ( ) == null )  {  if  (   crs . getGridTransformations  ( ) == null )  {   nadgrids = true ; } else  {   nadgrids = false ; } } else  {   nadgrids =   getGridTransformations  ( ) . equals  (  crs . getGridTransformations  ( ) ) ; }  boolean  crstransf ;  if  (   getCRSTransformations  ( ) == null )  {  if  (   crs . getCRSTransformations  ( ) == null )  {   crstransf = true ; } else  {   crstransf = false ; } } else  {   crstransf =   getCRSTransformations  ( ) . equals  (  crs . getGridTransformations  ( ) ) ; }  return    
<<<<<<<
    getDatum  ( ) . equals  (  crs . getDatum  ( ) ) &&   getProjection  ( ) . equals  (  crs . getProjection  ( ) ) &&   getCoordinateSystem  ( ) . equals  (  crs . getCoordinateSystem  ( ) )
=======
  getDatum  ( ) . equals  (  crs . getDatum  ( ) )
>>>>>>>
 && 
<<<<<<<
nadgrids
=======
  getProjection  ( ) . equals  (  crs . getProjection  ( ) )
>>>>>>>
 && 
<<<<<<<
crstransf
=======
  getCoordinateSystem  ( ) . equals  (  crs . getCoordinateSystem  ( ) )
>>>>>>>
 && 
<<<<<<<
  getProjection  ( ) . equals  (  crs . getProjection  ( ) )
=======
nadgrids
>>>>>>>
 ; } else  {  return false ; } }    @ Override public  int hashCode  ( )  {   int  hash = 3 ;   hash =   
<<<<<<<
59
=======
97
>>>>>>>
 * hash +  (    this . projection != null ?   this . projection . hashCode  ( ) : 0 ) ;  return hash ; }   public ProjectedCRS  (  Identifier identifier ,  GeodeticDatum datum ,  Projection projection ,  Unit unit )  {  super  ( identifier , datum ,  new CoordinateSystem  (  new Axis  [ ]  { EASTING , NORTHING } ,  new Unit  [ ]  { unit , unit } ) ) ;    this . projection = projection ; } }