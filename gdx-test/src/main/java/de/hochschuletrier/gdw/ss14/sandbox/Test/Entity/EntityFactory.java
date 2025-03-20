package de.hochschuletrier.gdw.ss14.sandbox.Test.Entity;
import com.badlogic.gdx.math.Vector2;
import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixManager;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.CatPhysicsComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.CatPropertyComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.DogPhysicsComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.HolePhysicsComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.MovementComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.PositionComponent;
import de.hochschuletrier.gdw.ss14.sandbox.ecs.EntityManager;
import de.hochschuletrier.gdw.ss14.sandbox.ecs.components.PhysicsComponent;

public class EntityFactory {
  public static EntityManager manager;

  public static void constructCat(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration) {
    final int entity = manager.createEntity();
    final CatPhysicsComponent catPhysix = new CatPhysicsComponent();

<<<<<<< Unknown file: This is a bug in JDime.
=======
    final PositionComponent catPosition = new PositionComponent(new Vector2((int) pos.x, (int) pos.y));
>>>>>>> /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/right.java

    final MovementComponent catMove = new MovementComponent(maxVelocity, middleVelocity, minVelocity, acceleration, new Vector2(1, 1));
    final CatPropertyComponent catProperty = new CatPropertyComponent();
    catPhysix.initPhysics(phyManager);
    catPhysix.physicsBody.setX(pos.x);

<<<<<<< /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/left.java
    catPhysix.physicsBody.setY(pos.y)
=======
    manager.addComponent(entity, catProperty)
>>>>>>> /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/right.java
    ;
    catPhysix.physicsBody.setLinearVelocity(catMove.velocity, catMove.velocity);
    manager.addComponent(entity, catPhysix);
    manager.addComponent(entity, catMove);
  }

  public static PhysixManager phyManager;

  public static void constructDog(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration) {
    final int entity = manager.createEntity();
    final DogPhysicsComponent dogPhysix = new DogPhysicsComponent();
    final PositionComponent dogPosition = new PositionComponent(new Vector2((int) pos.x, (int) pos.y));
    final MovementComponent dogMove = new MovementComponent(maxVelocity, middleVelocity, minVelocity, acceleration, new Vector2(0, 0));
    dogPhysix.initPhysics(phyManager);
    manager.addComponent(entity, dogPhysix);
    manager.addComponent(entity, dogPosition);
    manager.addComponent(entity, dogMove);
  }

  public static AssetManagerX assetManager;

  public EntityFactory(
<<<<<<< /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/left.java
  EntityManager manager
=======
>>>>>>> Unknown file: This is a bug in JDime.
  , 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/left.java
  PhysixManager phyManager
=======
>>>>>>> Unknown file: This is a bug in JDime.
  , 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/left.java
  AssetManagerX assetManager
=======
>>>>>>> Unknown file: This is a bug in JDime.
  ) {

<<<<<<< /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/left.java
    this.manager = manager;
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/left.java
    this.phyManager = phyManager;
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/left.java
    this.assetManager = assetManager;
=======
>>>>>>> Unknown file: This is a bug in JDime.
  }

  public static void constructHole(Vector2 pos) {
    final int entity = manager.createEntity();
    final HolePhysicsComponent holePhysix = new HolePhysicsComponent();
    final PositionComponent holePosition = new PositionComponent(new Vector2((int) pos.x, (int) pos.y));
    holePhysix.initPhysics(phyManager);
    manager.addComponent(entity, holePhysix);
    manager.addComponent(entity, holePosition);
  }
}