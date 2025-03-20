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
	public static AssetManagerX assetManager;
	public EntityFactory(EntityManager manager, PhysixManager phyManager, AssetManagerX assetManager){
		this.manager = manager;
		this.phyManager = phyManager;
		this.assetManager = assetManager;
	}
	public static void constructCat(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration) {
<<<<<<< /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/left.java
		int entity = manager.createEntity();
	    CatPhysicsComponent catPhysix = new CatPhysicsComponent();
	    //PositionComponent catPosition = new PositionComponent(new Vector2((int)pos.x,(int)pos.y));
	    MovementComponent catMove = new MovementComponent(maxVelocity,middleVelocity,minVelocity,acceleration,new Vector2(1,1));
	   //	PhysicsComponent catPhysix = new PhysicsComponent();
||||||| /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/base.java
		int entity = manager.createEntity();
	    CatPhysicsComponent catPhysix = new CatPhysicsComponent();
	    PositionComponent catPosition = new PositionComponent(new Vector2((int)pos.x,(int)pos.y));
	    MovementComponent catMove = new MovementComponent(maxVelocity,middleVelocity,minVelocity,acceleration,new Vector2(0,0));
=======
	    final int entity = manager.createEntity();
	    final CatPhysicsComponent catPhysix = new CatPhysicsComponent();
	    final PositionComponent catPosition = new PositionComponent(
	            new Vector2((int) pos.x, (int) pos.y));
	    final MovementComponent catMove = new MovementComponent(maxVelocity,
	            middleVelocity, minVelocity, acceleration, new Vector2(0, 0));
	    final CatPropertyComponent catProperty = new CatPropertyComponent();
>>>>>>> /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/right.java
	    catPhysix.initPhysics(phyManager);
<<<<<<< /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/left.java
	    catPhysix.physicsBody.setX(pos.x);
	    catPhysix.physicsBody.setY(pos.y);
	    catPhysix.physicsBody.setLinearVelocity(catMove.velocity, catMove.velocity);
||||||| /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/base.java
=======
	    manager.addComponent(entity, catProperty);
>>>>>>> /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/right.java
	    manager.addComponent(entity, catPhysix);
	    manager.addComponent(entity, catMove);
	}

    public static void constructDog(Vector2 pos, float maxVelocity,
            float middleVelocity, float minVelocity, float acceleration) {
        final int entity = manager.createEntity();
        final DogPhysicsComponent dogPhysix = new DogPhysicsComponent();
        final PositionComponent dogPosition = new PositionComponent(
                new Vector2((int) pos.x, (int) pos.y));
        final MovementComponent dogMove = new MovementComponent(maxVelocity,
                middleVelocity, minVelocity, acceleration, new Vector2(0, 0));
        dogPhysix.initPhysics(phyManager);
        manager.addComponent(entity, dogPhysix);
        manager.addComponent(entity, dogPosition);
        manager.addComponent(entity, dogMove);
    }

    public static void constructHole(Vector2 pos) {
        final int entity = manager.createEntity();
        final HolePhysicsComponent holePhysix = new HolePhysicsComponent();
        final PositionComponent holePosition = new PositionComponent(
                new Vector2((int) pos.x, (int) pos.y));
        holePhysix.initPhysics(phyManager);
        manager.addComponent(entity, holePhysix);
        manager.addComponent(entity, holePosition);
    }

    public static EntityManager manager;

    public static PhysixManager phyManager;

    public EntityFactory() {

    }

    // public static void constructDog(Vector2 pos, float maxVelocity, float
    // middleVelocity, float minVelocity, float acceleration){
    // int entity = manager.createEntity();
    // DogPhysicsComponent dogPhysix = new DogPhysicsComponent();
    // PositionComponent dogPosition = new PositionComponent(new
    // Vector2((int)pos.x,(int)pos.y));
    // MovementComponent dogMove = new
    // MovementComponent(maxVelocity,middleVelocity,minVelocity,acceleration,new
    // Vector2(0,0));
    // dogPhysix.initPhysics(phyManager);
    // manager.addComponent(entity, dogPhysix);
    // manager.addComponent(entity, dogPosition);
    // manager.addComponent(entity, dogMove);
    // }
}
