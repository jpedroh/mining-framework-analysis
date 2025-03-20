package de.hochschuletrier.gdw.ss14.sandbox.Test.Entity;
import com.badlogic.gdx.math.Vector2;
import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixManager;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.CatPhysicsComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.EnemyComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.HolePhysicsComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.InputComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.MovementComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.PlayerComponent;
import de.hochschuletrier.gdw.ss14.sandbox.ecs.EntityManager;

public class EntityFactory {
  public static EntityManager manager;

  public static PhysixManager phyManager;

  public static AssetManagerX assetManager;

  public EntityFactory(EntityManager manager, PhysixManager phyManager, AssetManagerX assetManager) {
    this.manager = manager;
    this.phyManager = phyManager;
    this.assetManager = assetManager;
  }

  public static void constructCat(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration) {
    int entity = manager.createEntity();
    CatPhysicsComponent catPhysix = new CatPhysicsComponent(pos, 50, 100, 0, 1, 0);
    MovementComponent catMove = new MovementComponent(maxVelocity, middleVelocity, minVelocity, acceleration);
    InputComponent catInput = new InputComponent();
    catPhysix.initPhysics(phyManager);
    manager.addComponent(entity, catPhysix);
    manager.addComponent(entity, catMove);
    manager.addComponent(entity, catInput);
    manager.addComponent(entity, new PlayerComponent());
  }

  public static void constructDog(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration) {
    int entity = manager.createEntity();
    CatPhysicsComponent dogPhysix = new CatPhysicsComponent();
    MovementComponent dogMove = new MovementComponent(maxVelocity, middleVelocity, minVelocity, acceleration);
    InputComponent dogInput = new InputComponent();
    dogPhysix.initPhysics(phyManager);
    manager.addComponent(entity, dogPhysix);
    manager.addComponent(entity, dogMove);
    manager.addComponent(entity, dogInput);
    manager.addComponent(entity, new EnemyComponent());
  }

  public static void constructHole(Vector2 pos) {
    int entity = manager.createEntity();
    HolePhysicsComponent holePhysix = new HolePhysicsComponent();
    holePhysix.initPhysics(phyManager);
    manager.addComponent(entity, holePhysix);
  }
}