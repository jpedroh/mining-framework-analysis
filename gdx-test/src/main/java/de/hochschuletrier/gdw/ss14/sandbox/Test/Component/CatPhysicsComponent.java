  package       de . hochschuletrier . gdw . ss14 . sandbox . Test . Component ;   import     com . badlogic . gdx . math . Vector2 ;  import      com . badlogic . gdx . physics . box2d . BodyDef ;  import       de . hochschuletrier . gdw . commons . gdx . physix . PhysixBody ;  import       de . hochschuletrier . gdw . commons . gdx . physix . PhysixBodyDef ;  import       de . hochschuletrier . gdw . commons . gdx . physix . PhysixFixtureDef ;  import       de . hochschuletrier . gdw . commons . gdx . physix . PhysixManager ;  import        de . hochschuletrier . gdw . ss14 . sandbox . ecs . components . PhysicsComponent ;  import       com . badlogic . gdx . physics . box2d . BodyDef . BodyType ;  import      com . badlogic . gdx . physics . box2d . Fixture ;  import      com . badlogic . gdx . physics . box2d . FixtureDef ;   public class CatPhysicsComponent  extends PhysicsComponent  {   public CatPhysicsComponent  ( )  {  this  (  new Vector2  ( 0 , 0 ) , 100f , 100f , 0f , 1f , 0f ) ; }    @ Override public void initPhysics  (  PhysixManager manager )  { 
<<<<<<<
  super . initPhysics  ( manager ) ;
=======
 PhysixFixtureDef  fixturedef =     new PhysixFixtureDef  ( manager ) . density  ( 1 ) . friction  ( mFriction ) . restitution  ( mRestitution ) ;
>>>>>>>
   
<<<<<<<
 this . physicsBody
=======
mBody
>>>>>>>
 =    
<<<<<<<
 new PhysixBodyDef  (   BodyDef . BodyType . DynamicBody , manager )
=======
  new PhysixBodyDef  (  BodyType . DynamicBody , manager ) . position  ( mPosition )
>>>>>>>
 . 
<<<<<<<
position
=======
fixedRotation
>>>>>>>
  ( 
<<<<<<<
 new Vector2  ( 0 , 0 )
=======
true
>>>>>>>
 ) . 
<<<<<<<
fixedRotation
=======
angle
>>>>>>>
  ( 
<<<<<<<
false
=======
mRotation
>>>>>>>
 ) . create  ( ) ;    mFixtures [ 0 ] =  mBody . createFixture  (  fixturedef . shapeBox  ( mWidth ,  mHeight - mWidth ) ) ;  
<<<<<<<
 physicsBody . createFixture  (      new PhysixFixtureDef  ( manager ) . density  ( 5 ) . friction  ( 0.2f ) . restitution  ( 0.4f ) . shapeCircle  ( 50 ) )
=======
  mFixtures [ 1 ] =  mBody . createFixture  (  fixturedef . shapeCircle  ( mWidth ,  new Vector2  (    mPosition . x + mHeight - mWidth ,  mPosition . y ) ) )
>>>>>>>
 ;  
<<<<<<<
 setPhysicsBody  ( physicsBody )
=======
  mFixtures [ 2 ] =  mBody . createFixture  (  fixturedef . shapeCircle  ( mWidth ,  new Vector2  (    mPosition . x - mHeight - mWidth ,  mPosition . y ) ) )
>>>>>>>
 ; }   public Vector2  mPosition ;   public  float  mWidth ;   public  float  mHeight ;   public  float  mFriction ;   public  float  mRotation ;   public  float  mRestitution ;   private  Fixture  [ ]  mFixtures ;   private PhysixBody  mBody ;   public CatPhysicsComponent  (  Vector2 position ,   float width ,   float height ,   float rotation ,   float friciton ,   float restitutioin )  {   mPosition = position ;   mWidth = width ;   mHeight = height ;   mRotation = rotation ;   mFriction = friciton ;   mRestitution = restitutioin ;   mFixtures =  new Fixture  [ 3 ] ; } }