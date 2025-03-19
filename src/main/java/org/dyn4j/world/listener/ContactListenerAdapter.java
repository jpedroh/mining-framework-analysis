package org.dyn4j.world.listener;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.world.ContactCollisionData;

/**
 * Convenience class for implementing the {@link ContactListener} interface.
 * @author William Bittle
 * @version 4.1.0
 * @since 1.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public class ContactListenerAdapter<T extends PhysicsBody> implements ContactListener<T> {
  @Override public void begin(ContactCollisionData<T> collision, Contact contact) {
  }

  @Override public void end(ContactCollisionData<T> collision, Contact contact) {
  }

  @Override public void persist(ContactCollisionData<T> collision, Contact oldContact, Contact newContact) {
  }

  @Override public void destroyed(ContactCollisionData<T> collision, Contact contact) {
  }

  @Override public void collision(ContactCollisionData<T> collision, ContactConstraint<T> contactConstraint) {
  }

  @Override public void preSolve(ContactCollisionData<T> collision, Contact contact) {
  }

  @Override public void postSolve(ContactCollisionData<T> collision, SolvedContact contact) {
  }
}