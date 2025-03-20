package de.hochschuletrier.gdw.ss14.sandbox.Test.Component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixBody;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixBodyDef;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixFixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import de.hochschuletrier.gdw.commons.gdx.physix.PhysixManager;
import de.hochschuletrier.gdw.ss14.sandbox.ecs.components.PhysicsComponent;

public class CatPhysicsComponent extends PhysicsComponent{
	
	// TODO: Für Physik Team
	
	public Vector2 			mPosition;
	public float 			mWidth;
	public float 			mHeight;
	public float 			mFriction;
	public float 			mRotation;
	public float			mRestitution;
	
	private Fixture 	[]	mFixtures;
	private PhysixBody		mBody;
	
	/**
	 * 
	 * @param position 		central position of the object
	 * @param width			the width of the object
	 * @param height		the height of the object
	 * @param rotation		the rotation in radians [0 .. 2*PI]
	 * @param friction  	the friction of the object 
	 * @param restitution	the restitution (elastitcy)
	 */
	public CatPhysicsComponent(Vector2 position, float width, float height, float rotation, float friciton, float restitutioin) {
		mPosition = position;
		mWidth = width;
		mHeight = height;
		mRotation = rotation;
		mFriction = friciton;
		mRestitution = restitutioin;
		
		mFixtures = new Fixture[3];
	}
	
	public CatPhysicsComponent(){
		this(new Vector2(0,0), 100f, 100f, 0f, 1f, 0f);
	}
	
	@Override
    public void initPhysics(PhysixManager manager){
<<<<<<< /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Component/CatPhysicsComponent.java/left.java
		super.initPhysics(manager);

        this.physicsBody = new PhysixBodyDef(BodyDef.BodyType.DynamicBody, manager).position(new Vector2(0,0))
                .fixedRotation(false).create();
        physicsBody.createFixture(new PhysixFixtureDef(manager).density(5).friction(0.2f).restitution(0.4f).shapeCircle(50));
        setPhysicsBody(physicsBody);
||||||| /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Component/CatPhysicsComponent.java/base.java
=======
		PhysixFixtureDef fixturedef = new PhysixFixtureDef(manager).density(1)
				.friction(mFriction).restitution(mRestitution);
		
		mBody = new PhysixBodyDef(BodyType.DynamicBody, manager).position(mPosition)
				.fixedRotation(true).angle(mRotation)
				.create();
		
		mFixtures[0] = mBody.createFixture(fixturedef.shapeBox(mWidth, mHeight-mWidth));
		mFixtures[1] = mBody.createFixture(fixturedef.shapeCircle(mWidth, new Vector2(mPosition.x + mHeight-mWidth, mPosition.y) ));
		mFixtures[2] = mBody.createFixture(fixturedef.shapeCircle(mWidth, new Vector2(mPosition.x - mHeight-mWidth, mPosition.y) ));
>>>>>>> /usr/src/app/output/lusito/gamedevweek/b9d0b595eaae93747ce9081072973fa34c8b8764/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/sandbox/Test/Component/CatPhysicsComponent.java/right.java
		
    }

	
}
