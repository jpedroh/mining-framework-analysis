package de.hochschuletrier.gdw.ss14.ecs;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import de.hochschuletrier.gdw.commons.gdx.assets.AnimationExtended;
import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixManager;
import de.hochschuletrier.gdw.ss14.ecs.components.AnimationComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.CameraComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.CatPhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.CatPropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.DogPropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.EnemyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.InputComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.LaserPointerComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.MovementComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.RenderComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.ShadowComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.WoolPhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.systems.CatContactSystem;
import de.hochschuletrier.gdw.ss14.game.Game;
import de.hochschuletrier.gdw.ss14.states.CatStateEnum;

public class EntityFactory {
  public static void constructBalk() {
    int entity = manager.createEntity();
  }

  public static void constructBox() {
    int entity = manager.createEntity();
  }

  public static void constructBroom() {
    int entity = manager.createEntity();
  }

  public static int constructCat(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration) {
    int entity = manager.createEntity();
    CatPhysicsComponent catPhysix = new CatPhysicsComponent(pos, 25, 50, 0, 0f, 0f);
    ICollisionListener contactSystem = (CatContactSystem) Game.engine.getSystemOfType(CatContactSystem.class);
    catPhysix.collisionListeners.add(contactSystem);
    MovementComponent catMove = new MovementComponent(maxVelocity, middleVelocity, minVelocity, acceleration);
    InputComponent catInput = new InputComponent();
    catPhysix.initPhysics(phyManager);
    CatPropertyComponent catProperty = new CatPropertyComponent();
    catProperty.lastCheckPoint = pos;
    AnimationComponent catAnimation = new AnimationComponent();
    catAnimation.animation = new AnimationExtended[11];
    catAnimation.animation[CatStateEnum.HIT.ordinal()] = assetManager.getAnimation("hit");
    catAnimation.animation[CatStateEnum.IDLE.ordinal()] = assetManager.getAnimation("idle");
    catAnimation.animation[CatStateEnum.WALK.ordinal()] = assetManager.getAnimation("walk");
    catAnimation.animation[CatStateEnum.RUN.ordinal()] = assetManager.getAnimation("run");
    catAnimation.animation[CatStateEnum.SLIDE_LEFT.ordinal()] = assetManager.getAnimation("slide_left");
    catAnimation.animation[CatStateEnum.SLIDE_RIGHT.ordinal()] = assetManager.getAnimation("slide_right");
    catAnimation.animation[CatStateEnum.CRASH.ordinal()] = assetManager.getAnimation("crash");
    catAnimation.animation[CatStateEnum.FALL.ordinal()] = assetManager.getAnimation("fall");
    catAnimation.animation[CatStateEnum.DIE.ordinal()] = assetManager.getAnimation("die");
    catAnimation.animation[CatStateEnum.DIE2.ordinal()] = assetManager.getAnimation("die2");
    catAnimation.animation[CatStateEnum.JUMP.ordinal()] = assetManager.getAnimation("jump");
    CameraComponent cam = new CameraComponent();
    cam.cameraZoom = 1.0f;
    CatPropertyComponent catProperties = new CatPropertyComponent();
    catProperties.state = CatStateEnum.IDLE;

<<<<<<< /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/left.java
    ParticleEmitterComponent
=======
    ShadowComponent
>>>>>>> /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/right.java
     
<<<<<<< /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/left.java
    particleEmitComp = new ParticleEmitterComponent()
=======
    shadow = new ShadowComponent()
>>>>>>> /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/right.java
    ;

<<<<<<< /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/left.java
    particleEmitComp.particleTintColor = new Color(1, 0, 0, 1)
=======
    shadow.alpha = 0.5f
>>>>>>> /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/right.java
    ;

<<<<<<< /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/left.java
    particleEmitComp
=======
    shadow
>>>>>>> /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/right.java
    .
<<<<<<< /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/left.java
    emitRadius
=======
    z
>>>>>>> /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/right.java
     = 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/left.java
    10f
=======
    1.0f
>>>>>>> /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/right.java
    ;
    manager.addComponent(entity, catProperties);
    manager.addComponent(entity, catAnimation);
    manager.addComponent(entity, new RenderComponent());
    manager.addComponent(entity, catProperty);
    manager.addComponent(entity, catPhysix);
    manager.addComponent(entity, catMove);
    manager.addComponent(entity, catInput);
    manager.addComponent(entity, new PlayerComponent());
    manager.addComponent(entity, cam);
    manager.addComponent(entity, 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/left.java
    particleEmitComp
=======
    shadow
>>>>>>> /usr/src/app/output/lusito/gamedevweek/6873b1049deaa3df2ece0db0f0e0c23b052f668c/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/EntityFactory.java/right.java
    );
    return entity;
  }

  public static void constructCatbox() {
    int entity = manager.createEntity();
  }

  public static int constructDog(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration) {
    int entity = manager.createEntity();
    CatPhysicsComponent dogPhysix = new CatPhysicsComponent(pos, 50, 100, 0, 1, 0);
    MovementComponent dogMove = new MovementComponent(maxVelocity, middleVelocity, minVelocity, acceleration);
    InputComponent dogInput = new InputComponent();
    DogPropertyComponent dogState = new DogPropertyComponent();
    dogPhysix.initPhysics(phyManager);
    manager.addComponent(entity, dogState);
    manager.addComponent(entity, dogPhysix);
    manager.addComponent(entity, dogMove);
    manager.addComponent(entity, dogInput);
    manager.addComponent(entity, new EnemyComponent());
    return entity;
  }

  public static void constructLaserPointer(Vector2 pos) {
    int entity = manager.createEntity();
    LaserPointerComponent laser = new LaserPointerComponent(pos);
    manager.addComponent(entity, laser);
  }

  public static void constructDoor() {
    int entity = manager.createEntity();
  }

  public static void constructFood() {
    int entity = manager.createEntity();
  }

  public static void constructLamp() {
    int entity = manager.createEntity();
  }

  public static void constructPuddleOfBlood() {
    int entity = manager.createEntity();
  }

  public static void constructPuddleOfWater() {
    int entity = manager.createEntity();
  }

  public static void constructStairs() {
    int entity = manager.createEntity();
  }

  public static void constructVase() {
    int entity = manager.createEntity();
  }

  public static void constructWool(Vector2 pos) {
    int entity = manager.createEntity();
    WoolPhysicsComponent woolPhysicsComponent = new WoolPhysicsComponent(pos, 50.0f, 50.0f, 0.0f);
    woolPhysicsComponent.initPhysics(phyManager);
    manager.addComponent(entity, woolPhysicsComponent);
  }

  public static EntityManager manager;

  public static PhysixManager phyManager;

  public static AssetManagerX assetManager;
}