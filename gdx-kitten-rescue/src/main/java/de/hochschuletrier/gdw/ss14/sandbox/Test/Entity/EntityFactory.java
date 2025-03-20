<<<<<<< /usr/src/app/output/lusito/gamedevweek/ac33df42e0ef02c8719159f4a92d0d95565133ff/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/left.java
fatal: path 'gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java' exists on disk, but not in '06f7dbc47dc5c89b788c3066ca545b7861668ddd'
||||||| /usr/src/app/output/lusito/gamedevweek/ac33df42e0ef02c8719159f4a92d0d95565133ff/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/base.java
package de.hochschuletrier.gdw.ss14.sandbox.Test.Entity;

import com.badlogic.gdx.math.Vector2;

import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixManager;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.CatPhysicsComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.DogPhysicsComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.HolePhysicsComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.MovementComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.PositionComponent;
import de.hochschuletrier.gdw.ss14.sandbox.ecs.EntityManager;

public class EntityFactory {
	
	public static EntityManager manager;
	public static PhysixManager phyManager;
	public static AssetManagerX assetManager;
	
	public EntityFactory(EntityManager manager, PhysixManager phyManager, AssetManagerX assetManager){
		this.manager = manager;
		this.phyManager = phyManager;
		this.assetManager = assetManager;
	}
	
	public static void constructCat(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration){
		int entity = manager.createEntity();
	    CatPhysicsComponent catPhysix = new CatPhysicsComponent();
	    //PositionComponent catPosition = new PositionComponent(new Vector2((int)pos.x,(int)pos.y));
	    MovementComponent catMove = new MovementComponent(maxVelocity,middleVelocity,minVelocity,acceleration,new Vector2(1,1));
	   //	PhysicsComponent catPhysix = new PhysicsComponent();
	    catPhysix.initPhysics(phyManager);
	    catPhysix.physicsBody.setX(pos.x);
	    catPhysix.physicsBody.setY(pos.y);
	    catPhysix.physicsBody.setLinearVelocity(catMove.velocity, catMove.velocity);
	    manager.addComponent(entity, catPhysix);
	    manager.addComponent(entity, catMove);
	}
	
	public static void constructDog(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration){
		int entity = manager.createEntity();
		DogPhysicsComponent dogPhysix = new DogPhysicsComponent();
		PositionComponent dogPosition = new PositionComponent(new Vector2((int)pos.x,(int)pos.y));
	    MovementComponent dogMove = new MovementComponent(maxVelocity,middleVelocity,minVelocity,acceleration,new Vector2(0,0));
		dogPhysix.initPhysics(phyManager);
	    manager.addComponent(entity, dogPhysix);
	    manager.addComponent(entity, dogPosition);
	    manager.addComponent(entity, dogMove);
	}
	
	public static void constructHole(Vector2 pos){
		int entity = manager.createEntity();
		HolePhysicsComponent holePhysix = new HolePhysicsComponent();
		PositionComponent holePosition = new PositionComponent(new Vector2((int)pos.x,(int)pos.y));
		holePhysix.initPhysics(phyManager);
	    manager.addComponent(entity, holePhysix);
	    manager.addComponent(entity, holePosition);
	}
	
//	public static void constructDog(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration){
//		int entity = manager.createEntity();
//		DogPhysicsComponent dogPhysix = new DogPhysicsComponent();
//		PositionComponent dogPosition = new PositionComponent(new Vector2((int)pos.x,(int)pos.y));
//	    MovementComponent dogMove = new MovementComponent(maxVelocity,middleVelocity,minVelocity,acceleration,new Vector2(0,0));
//		dogPhysix.initPhysics(phyManager);
//	    manager.addComponent(entity, dogPhysix);
//	    manager.addComponent(entity, dogPosition);
//	    manager.addComponent(entity, dogMove);
//	}
	
//=======
//    public static void constructCat(Vector2 pos, float maxVelocity,
//            float middleVelocity, float minVelocity, float acceleration) {
//        final int entity = manager.createEntity();
//        final CatPhysicsComponent catPhysix = new CatPhysicsComponent();
//        final PositionComponent catPosition = new PositionComponent(
//                new Vector2((int) pos.x, (int) pos.y));
//        final MovementComponent catMove = new MovementComponent(maxVelocity,
//                middleVelocity, minVelocity, acceleration, new Vector2(0, 0));
//        final CatPropertyComponent catProperty = new CatPropertyComponent();
//        catPhysix.initPhysics(phyManager);
//        manager.addComponent(entity, catProperty);
//        manager.addComponent(entity, catPhysix);
//        manager.addComponent(entity, catPosition);
//        manager.addComponent(entity, catMove);
//    }
//>>>>>>> /usr/src/app/output/lusito/gamedevweek/ac33df42e0ef02c8719159f4a92d0d95565133ff/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Entity/EntityFactory.java/right.java
//
//    public static void constructDog(Vector2 pos, float maxVelocity,
//            float middleVelocity, float minVelocity, float acceleration) {
//        final int entity = manager.createEntity();
//        final DogPhysicsComponent dogPhysix = new DogPhysicsComponent();
//        final PositionComponent dogPosition = new PositionComponent(
//                new Vector2((int) pos.x, (int) pos.y));
//        final MovementComponent dogMove = new MovementComponent(maxVelocity,
//                middleVelocity, minVelocity, acceleration, new Vector2(0, 0));
//        dogPhysix.initPhysics(phyManager);
//        manager.addComponent(entity, dogPhysix);
//        manager.addComponent(entity, dogPosition);
//        manager.addComponent(entity, dogMove);
//    }
//
//    public static void constructHole(Vector2 pos) {
//        final int entity = manager.createEntity();
//        final HolePhysicsComponent holePhysix = new HolePhysicsComponent();
//        final PositionComponent holePosition = new PositionComponent(
//                new Vector2((int) pos.x, (int) pos.y));
//        holePhysix.initPhysics(phyManager);
//        manager.addComponent(entity, holePhysix);
//        manager.addComponent(entity, holePosition);
//    }
//
//    public static EntityManager manager;
//
//    public static PhysixManager phyManager;
//
//    public EntityFactory() {
//
//    }
//
//    // public static void constructDog(Vector2 pos, float maxVelocity, float
//    // middleVelocity, float minVelocity, float acceleration){
//    // int entity = manager.createEntity();
//    // DogPhysicsComponent dogPhysix = new DogPhysicsComponent();
//    // PositionComponent dogPosition = new PositionComponent(new
//    // Vector2((int)pos.x,(int)pos.y));
//    // MovementComponent dogMove = new
//    // MovementComponent(maxVelocity,middleVelocity,minVelocity,acceleration,new
//    // Vector2(0,0));
//    // dogPhysix.initPhysics(phyManager);
//    // manager.addComponent(entity, dogPhysix);
//    // manager.addComponent(entity, dogPosition);
//    // manager.addComponent(entity, dogMove);
//    // }
}
=======
package de.hochschuletrier.gdw.ss14.sandbox.Test.Entity;

import com.badlogic.gdx.math.Vector2;

import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixManager;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.CatPhysicsComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.DogPhysicsComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.HolePhysicsComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.MovementComponent;
import de.hochschuletrier.gdw.ss14.sandbox.Test.Component.PositionComponent;
import de.hochschuletrier.gdw.ss14.sandbox.ecs.EntityManager;

public class EntityFactory {
	
	public static EntityManager manager;
	public static PhysixManager phyManager;
	public static AssetManagerX assetManager;
	
	public EntityFactory(EntityManager manager, PhysixManager phyManager, AssetManagerX assetManager){
		this.manager = manager;
		this.phyManager = phyManager;
		this.assetManager = assetManager;
	}
	
	public static void constructCat(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration){
		int entity = manager.createEntity();
		System.err.println("create cat");
	    CatPhysicsComponent catPhysix = new CatPhysicsComponent();
	    //PositionComponent catPosition = new PositionComponent(new Vector2((int)pos.x,(int)pos.y));
	    MovementComponent catMove = new MovementComponent(maxVelocity,middleVelocity,minVelocity,acceleration,new Vector2(1,1));
	   //	PhysicsComponent catPhysix = new PhysicsComponent();
	    catPhysix.initPhysics(phyManager);
	    catPhysix.physicsBody.setX(pos.x);
	    catPhysix.physicsBody.setY(pos.y);
	    catPhysix.physicsBody.setLinearVelocity(catMove.velocity, catMove.velocity);
	    manager.addComponent(entity, catPhysix);
	    manager.addComponent(entity, catMove);
	}
	
	public static void constructDog(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration){
		int entity = manager.createEntity();
		DogPhysicsComponent dogPhysix = new DogPhysicsComponent();
		PositionComponent dogPosition = new PositionComponent(new Vector2((int)pos.x,(int)pos.y));
	    MovementComponent dogMove = new MovementComponent(maxVelocity,middleVelocity,minVelocity,acceleration,new Vector2(0,0));
		dogPhysix.initPhysics(phyManager);
	    manager.addComponent(entity, dogPhysix);
	    manager.addComponent(entity, dogPosition);
	    manager.addComponent(entity, dogMove);
	}
	
	public static void constructHole(Vector2 pos){
		int entity = manager.createEntity();
		HolePhysicsComponent holePhysix = new HolePhysicsComponent();
		PositionComponent holePosition = new PositionComponent(new Vector2((int)pos.x,(int)pos.y));
		holePhysix.initPhysics(phyManager);
	    manager.addComponent(entity, holePhysix);
	    manager.addComponent(entity, holePosition);
	}
	
//	public static void constructDog(Vector2 pos, float maxVelocity, float middleVelocity, float minVelocity, float acceleration){
//		int entity = manager.createEntity();
//		DogPhysicsComponent dogPhysix = new DogPhysicsComponent();
//		PositionComponent dogPosition = new PositionComponent(new Vector2((int)pos.x,(int)pos.y));
//	    MovementComponent dogMove = new MovementComponent(maxVelocity,middleVelocity,minVelocity,acceleration,new Vector2(0,0));
//		dogPhysix.initPhysics(phyManager);
//	    manager.addComponent(entity, dogPhysix);
//	    manager.addComponent(entity, dogPosition);
//	    manager.addComponent(entity, dogMove);
//	}
	
//=======
//    public static void constructCat(Vector2 pos, float maxVelocity,
//            float middleVelocity, float minVelocity, float acceleration) {
//        final int entity = manager.createEntity();
//        final CatPhysicsComponent catPhysix = new CatPhysicsComponent();
//        final PositionComponent catPosition = new PositionComponent(
//                new Vector2((int) pos.x, (int) pos.y));
//        final MovementComponent catMove = new MovementComponent(maxVelocity,
//                middleVelocity, minVelocity, acceleration, new Vector2(0, 0));
//        final CatPropertyComponent catProperty = new CatPropertyComponent();
//        catPhysix.initPhysics(phyManager);
//        manager.addComponent(entity, catProperty);
//        manager.addComponent(entity, catPhysix);
//        manager.addComponent(entity, catPosition);
//        manager.addComponent(entity, catMove);
//    }
//>>>>>>> eb05791cc2829b2ac4d6cc36dedcf0604c658cf3
//
//    public static void constructDog(Vector2 pos, float maxVelocity,
//            float middleVelocity, float minVelocity, float acceleration) {
//        final int entity = manager.createEntity();
//        final DogPhysicsComponent dogPhysix = new DogPhysicsComponent();
//        final PositionComponent dogPosition = new PositionComponent(
//                new Vector2((int) pos.x, (int) pos.y));
//        final MovementComponent dogMove = new MovementComponent(maxVelocity,
//                middleVelocity, minVelocity, acceleration, new Vector2(0, 0));
//        dogPhysix.initPhysics(phyManager);
//        manager.addComponent(entity, dogPhysix);
//        manager.addComponent(entity, dogPosition);
//        manager.addComponent(entity, dogMove);
//    }
//
//    public static void constructHole(Vector2 pos) {
//        final int entity = manager.createEntity();
//        final HolePhysicsComponent holePhysix = new HolePhysicsComponent();
//        final PositionComponent holePosition = new PositionComponent(
//                new Vector2((int) pos.x, (int) pos.y));
//        holePhysix.initPhysics(phyManager);
//        manager.addComponent(entity, holePhysix);
//        manager.addComponent(entity, holePosition);
//    }
//
//    public static EntityManager manager;
//
//    public static PhysixManager phyManager;
//
//    public EntityFactory() {
//
//    }
//
//    // public static void constructDog(Vector2 pos, float maxVelocity, float
//    // middleVelocity, float minVelocity, float acceleration){
//    // int entity = manager.createEntity();
//    // DogPhysicsComponent dogPhysix = new DogPhysicsComponent();
//    // PositionComponent dogPosition = new PositionComponent(new
//    // Vector2((int)pos.x,(int)pos.y));
//    // MovementComponent dogMove = new
//    // MovementComponent(maxVelocity,middleVelocity,minVelocity,acceleration,new
//    // Vector2(0,0));
//    // dogPhysix.initPhysics(phyManager);
//    // manager.addComponent(entity, dogPhysix);
//    // manager.addComponent(entity, dogPosition);
//    // manager.addComponent(entity, dogMove);
//    // }
}
>>>>>>> theirs
