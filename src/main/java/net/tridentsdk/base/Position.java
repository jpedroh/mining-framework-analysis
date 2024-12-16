  package   net . tridentsdk . base ;   import    net . tridentsdk . world . World ;  import   javax . annotation . Nonnull ;  import    javax . annotation . concurrent . ThreadSafe ;    @ ThreadSafe public final class Position  extends  AbstractVector  < Position >  implements  Cloneable  {   private static final  long  serialVersionUID = 5910507790866074403L ;   private final World  world ;   private  float  yaw ;   private  float  pitch ;    @ Override protected void writeFields  (  Position vector )  {    this . pitch =  vector . pitch ;    this . yaw =  vector . yaw ; }   public Position  (    @ Nonnull World world )  {  this  ( world , 0D , 0D , 0D , 0F , 0F ) ; }   public Position  (    @ Nonnull World world ,   int x ,   int y ,   int z )  {  this  ( world ,  (  double ) x ,  (  double ) y ,  (  double ) z , 0F , 0F ) ; }   public Position  (    @ Nonnull World world ,   double x ,   double y ,   double z )  {  this  ( world , x , y , z , 0F , 0F ) ; }   public Position  (    @ Nonnull World world ,   double x ,   double y ,   double z ,   float yaw ,   float pitch )  {  super  ( x , y , z ) ;    this . world = world ;    this . yaw = yaw ;    this . pitch = pitch ; }   public World world  ( )  {  return  this . world ; }   public void setYaw  (   float yaw )  {  synchronized  (  this . lock )  {    this . yaw = yaw ; } }   public void setPitch  (   float pitch )  {  synchronized  (  this . lock )  {    this . pitch = pitch ; } }   public ImmutableWorldVector toWorldVector  ( )  {  synchronized  (  this . lock )  {  return  new ImmutableWorldVector  (  this . world ,  this . getIntX  ( ) ,  this . getIntY  ( ) ,  this . getIntZ  ( ) ) ; } }   public  double distanceSquared  (  Position position )  {   double  dX ;   double  dY ;   double  dZ ;  synchronized  (  this . lock )  {  synchronized  (  position . lock )  {   dX =   this . x -  position . x ;   dY =   this . y -  position . y ;   dZ =   this . z -  position . z ; } }  return    square  ( dX ) +  square  ( dY ) +  square  ( dZ ) ; }   public  double distance  (  Position position )  {  return  Math . sqrt  (  this . distanceSquared  ( position ) ) ; }   private static boolean eq  (   float f0 ,   float f1 )  {  return   Float . compare  ( f0 , f1 ) == 0 ; }   public  int getChunkX  ( )  {  return   this . getIntX  ( ) >> 4 ; }   public  int getChunkZ  ( )  {  return   this . getIntZ  ( ) >> 4 ; }    @ Override public boolean equals  (  Object obj )  {  if  (  obj instanceof Position )  {  Position  v =  ( Position ) obj ;  synchronized  (  this . lock )  {  return       eq  (  this . x ,  v . x ) &&  eq  (  this . y ,  v . y ) &&  eq  (  this . z ,  v . z ) &&   this . world . equals  (  v . world ) &&  eq  (  this . pitch ,  v . pitch ) &&  eq  (  this . yaw ,  v . yaw ) ; } }  return false ; }    @ Override public  int hashCode  ( )  {  synchronized  (  this . lock )  {   int  hash =  super . hashCode  ( ) ;   hash =   31 * hash +   this . world . hashCode  ( ) ;   hash =   31 * hash +  Float . floatToIntBits  (  this . pitch ) ;   hash =   31 * hash +  Float . floatToIntBits  (  this . yaw ) ;  return hash ; } }    @ Override public String toString  ( )  { 
<<<<<<<
 synchronized  (  this . lock )  {  return  String . format  ( "Position{%s-%f,%f,%f-pitch=%f,yaw=%f}" ,   this . world . name  ( ) ,  this . x ,  this . y ,  this . z ,  this . pitch ,  this . yaw ) ; }
=======
 return  String . format  ( "Position{%s-%f,%f,%f-pitch=%f,yaw=%f}" ,   this . world . getName  ( ) ,  this . x ,  this . y ,  this . z ,  this . pitch ,  this . yaw ) ;
>>>>>>>
 }    @ Override public Position clone  ( )  {  synchronized  (  this . lock )  {  return  new Position  (  this . world ,  this . x ,  this . y ,  this . z ,  this . pitch ,  this . yaw ) ; } }   public  float getYaw  ( )  {  synchronized  (  this . lock )  {  return  this . yaw ; } }   public  float getPitch  ( )  {  synchronized  (  this . lock )  {  return  this . pitch ; } }   public Block getBlock  ( )  {  return   this . world . getBlockAt  ( this ) ; } }