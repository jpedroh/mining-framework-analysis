  package      de . hochschuletrier . gdw . ss14 . ecs . systems ;   import   java . util . List ;  import    com . badlogic . gdx . Gdx ;  import     com . badlogic . gdx . graphics . Color ;  import     com . badlogic . gdx . graphics . GL20 ;  import     com . badlogic . gdx . graphics . Texture ;  import      com . badlogic . gdx . graphics . g2d . TextureRegion ;  import     com . badlogic . gdx . utils . Array ;  import       de . hochschuletrier . gdw . commons . gdx . utils . DrawUtil ;  import        de . hochschuletrier . gdw . commons . gdx . utils . DrawUtil . Mode ;  import      de . hochschuletrier . gdw . ss14 . ecs . EntityManager ;  import       de . hochschuletrier . gdw . ss14 . ecs . components . CatPropertyComponent ;  import       de . hochschuletrier . gdw . ss14 . ecs . components . PhysicsComponent ;  import       de . hochschuletrier . gdw . ss14 . ecs . components . RenderComponent ;  import       de . hochschuletrier . gdw . ss14 . ecs . components . ShadowComponent ;  import      de . hochschuletrier . gdw . ss14 . states . CatStateEnum ;  import       de . hochschuletrier . gdw . ss14 . ecs . components . JumpDataComponent ;   public class ShadowSystem  extends ECSystem  {   public ShadowSystem  (  EntityManager entityManager )  {  super  ( entityManager ) ; }   public ShadowSystem  (  EntityManager entityManager ,   int priority )  {  super  ( entityManager , priority ) ; }    @ Override public void update  (   float delta )  { }    @ Override public void render  ( )  {   Array  < Integer >  entities =  entityManager . getAllEntitiesWithComponents  (  RenderComponent . class ,  PhysicsComponent . class ,  ShadowComponent . class ) ;  for ( Integer currentEnt : entities )  {  RenderComponent  renderComp =  entityManager . getComponent  ( currentEnt ,  RenderComponent . class ) ;  PhysicsComponent  physicsComp =  entityManager . getComponent  ( currentEnt ,  PhysicsComponent . class ) ;  ShadowComponent  shadowComp =  entityManager . getComponent  ( currentEnt ,  ShadowComponent . class ) ;  if  (   shadowComp . alpha > 0f )  {    DrawUtil . batch . end  ( ) ;    Gdx . gl . glEnable  (  GL20 . GL_BLEND ) ;    Gdx . gl . glBlendFunc  (  GL20 . GL_SRC_ALPHA ,  GL20 . GL_ONE_MINUS_SRC_ALPHA ) ;   DrawUtil . setColor  (  new Color  ( 0 , 0 , 0 ,  shadowComp . alpha ) ) ;    DrawUtil . batch . begin  ( ) ;   float  shadowWidth =    renderComp . texture . getRegionWidth  ( ) *  shadowComp . z ;   float  shadowHeight =    renderComp . texture . getRegionHeight  ( ) *  shadowComp . z ;  JumpDataComponent  jumpComp =  entityManager . getComponent  ( currentEnt ,  JumpDataComponent . class ) ;  if  (  jumpComp != null )  { 
<<<<<<<
 switch  (  catPropComp . getState  ( ) )  {   case JUMP :   shadowWidth *= 1.35f ;   shadowHeight *= 1.35f ; }
=======
  float  factorShadowSize =  (   jumpComp . currentJumpTime /  jumpComp . maxJumpTime ) ;
>>>>>>>
 
<<<<<<<
   DrawUtil . batch . draw  (  renderComp . texture ,     physicsComp . getPosition  ( ) . x -  (  shadowWidth / 2 ) +  shadowComp . shadowOffsetX ,     physicsComp . getPosition  ( ) . y -  (  shadowHeight / 2 ) +  shadowComp . shadowOffsetY ,  shadowWidth / 2 ,  shadowHeight / 2 , shadowWidth , shadowHeight , 1f , 1f ,  (  float )  (    physicsComp . getRotation  ( ) * 180 /  Math . PI ) ) ;
=======
 if  (  factorShadowSize >=   jumpComp . maxJumpTime / 2 )  {   factorShadowSize =  1 - factorShadowSize ; }
>>>>>>>
    DrawUtil . batch . end  ( ) ;  
<<<<<<<
 DrawUtil . resetColor  ( )
=======
 factorShadowSize += 1
>>>>>>>
 ;  
<<<<<<<
  Gdx . gl . glDisable  (  GL20 . GL_BLEND )
=======
 shadowWidth *= factorShadowSize
>>>>>>>
 ;  
<<<<<<<
  DrawUtil . batch . begin  ( )
=======
 shadowHeight *= factorShadowSize
>>>>>>>
 ; } } } }   public void setShadowOffset  (   int offset )  { } }