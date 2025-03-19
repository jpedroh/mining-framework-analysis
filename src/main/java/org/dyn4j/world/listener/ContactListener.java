package org.dyn4j.world.listener;
import org.dyn4j.collision.manifold.ManifoldPointId;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;

/**
 * Represents an object that is notified of contact events.
 * <p>
 * Implement this interface and register it with a {@link PhysicsWorld}
 * to be notified when contact events occur.  Contact events occur after all 
 * {@link CollisionListener} events have been raised.
 * @author William Bittle
 * @version 4.1.0
 * @since 1.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public interface ContactListener<T extends PhysicsBody> extends WorldEventListener {
  /**
	 * Called when two {@link BodyFixture}s begin to overlap, generating a contact point.
	 * @param collision the collision data
	 * @param contact the contact
	 */
  public abstract void begin(ContactCollisionData<T> collision, Contact contact);

  /**
	 * Called when two {@link BodyFixture}s begin to separate and the contact point is no longer valid.
	 * <p>
	 * This can happen in one of two ways. First, the fixtures in question have separated such that there's
	 * no longer any collision between them. Second, the fixtures could still be in collision, but the features
	 * that are in collision on those fixtures have changed.
	 * @param collision the collision data
	 * @param contact the contact
	 */
  public abstract void end(ContactCollisionData<T> collision, Contact contact);

  /**
	 * Called when two {@link BodyFixture}s remain in contact.
	 * <p>
	 * For a {@link Contact} to persist, the {@link Settings#isWarmStartingEnabled()} must be true and the
	 * {@link ManifoldPointId}s must match.
	 * <p>
	 * For shapes with vertices only, the manifold ids will be identical when the features of the colliding
	 * fixtures are the same.  For rounded shapes, the manifold points must be within a specified tolerance
	 * defined in {@link Settings#getMaximumWarmStartDistance()}.
	 * <p>
	 * NOTE: The {@link ContactConstraint} stored in the <code>collision</code> parameter
	 * is being updated when this method is called. As a result, the data stored in the 
	 * contact constraint may not be accurate. If you need to access the final state of the 
	 * contact constraint, use the {@link #collision(ContactCollisionData, ContactConstraint)} 
	 * method.
	 * @param collision the collision data
	 * @param oldContact the old contact
	 * @param newContact the new contact
	 */
  public abstract void persist(ContactCollisionData<T> collision, Contact oldContact, Contact newContact);

  /**
	 * Called when a body or fixture is removed from the world that had existing contacts.
	 * <p>
	 * This is different than the {@link #end(ContactCollisionData, Contact)} event. This will only be
	 * called when a user removes a fixture or body that's currently in collision.  The 
	 * {@link #end(ContactCollisionData, Contact)} applies when the fixtures separate and are no longer
	 * in collision.
	 * <p>
	 * This is called before the {@link DestructionListener#destroyed(org.dyn4j.dynamics.contact.ContactConstraint)}
	 * method in the event processing needed to occur by both listeners. There's no requirement that it must be
	 * processed in both (or at all) though.
	 * @param collision the collision data
	 * @param contact the contact
	 */
  public abstract void destroyed(ContactCollisionData<T> collision, Contact contact);

  /**
	 * Called when two {@link BodyFixture}s generating a contact constraint.
	 * <p>
	 * This method is called after the {@link #begin(ContactCollisionData, Contact)}, 
	 * {@link #persist(ContactCollisionData, Contact, Contact)}, and {@link #end(ContactCollisionData, Contact)} 
	 * methods. When this method is called the state of the ContactConstraint will represent
	 * what will be solved.
	 * <p>
	 * This method will be called for all collisions where there was a manifold collision. This applies to
	 * sensor collisions as well.
	 * <p>
	 * Use this method to modify the information in the given {@link ContactConstraint} before it's further
	 * processed by the pipeline.
	 * @param collision the collision data
	 * @param contactConstraint the contact constraint
	 */
  public abstract void collision(ContactCollisionData<T> collision, ContactConstraint<T> contactConstraint);

  /**
	 * Called before contact constraints are solved.
	 * @param collision the collision data
	 * @param contact the contact
	 */
  public abstract void preSolve(ContactCollisionData<T> collision, Contact contact);

  /**
	 * Called after contacts have been solved.
	 * <p>
	 * NOTE: This method will be called for {@link SolvedContact}s even when the {@link SolvedContact#isSolved()}
	 * is false. This should only occur in situations with multiple contact points that produce a linearly
	 * dependent system. These contacts are thus ignored during solving, but still reported here.
	 * @param collision the collision data
	 * @param contact the contact
	 */
  public abstract void postSolve(ContactCollisionData<T> collision, SolvedContact contact);
}