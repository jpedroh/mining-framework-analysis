package org.dyn4j.dynamics.contact;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.manifold.ManifoldPointId;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Capacity;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.geometry.Shiftable;

/**
 * Represents a {@link ContactManager} that performs warm starting of contacts
 * based on the previous iteration.
 * @author William Bittle
 * @version 3.3.0
 * @since 3.2.0
 */
public class WarmStartingContactManager extends SimpleContactManager implements ContactManager, Shiftable {
  /** Another map that will be reused */
  Map<ContactConstraintId, ContactConstraint> constraints1 = null;

  /**
	 * Default constructor.
	 * @since 3.2.0
	 */
  public WarmStartingContactManager() {
    this(Capacity.DEFAULT_CAPACITY);
  }

  /**
	 * Full constructor.
	 * <p>
	 * The initial capacity is used to help performance in the event that the developer
	 * knows the number of bodies the world will contain.  The {@link WarmStartingContactManager}
	 * will grow past the initial capacity if necessary.
	 * @param initialCapacity the estimated number of {@link Body}s
	 * @throws NullPointerException if initialCapacity is null
	 * @since 3.2.0
	 */
  public WarmStartingContactManager(Capacity initialCapacity) {
    super(initialCapacity);
    int eSize = Collisions.getEstimatedCollisionPairs(initialCapacity.getBodyCount());
    this.constraints1 = new HashMap<ContactConstraintId, ContactConstraint>(eSize * 4 / 3 + 1, 0.75f);
  }

  public void updateAndNotify(List<ContactListener> listeners, Settings settings) {
    int size = this.constraintQueue.size();
    int lsize = listeners != null ? listeners.size() : 0;
    double warmStartDistanceSquared = settings.getWarmStartDistanceSquared();
    Map<ContactConstraintId, ContactConstraint> newMap = this.constraints1;
    for (int i = 0; i < size; i++) {
      ContactConstraint newContactConstraint = this.constraintQueue.get(i);
      ContactConstraint oldContactConstraint = null;
      List<Contact> contacts = newContactConstraint.contacts;
      int nsize = contacts.size();
      oldContactConstraint = this.constraints.remove(newContactConstraint.id);
      if (oldContactConstraint != null) {
        List<Contact> ocontacts = oldContactConstraint.contacts;
        int osize = ocontacts.size();
        boolean[] persisted = new boolean[osize];
        for (int j = nsize - 1; j >= 0; j--) {
          Contact newContact = contacts.get(j);
          boolean found = false;
          for (int k = 0; k < osize; k++) {
            Contact oldContact = ocontacts.get(k);
            if ((newContact.id == ManifoldPointId.DISTANCE && newContact.p.distanceSquared(oldContact.p) <= warmStartDistanceSquared) || newContact.id.equals(oldContact.id)) {
              newContact.jn = oldContact.jn;
              newContact.jt = oldContact.jt;
              PersistedContactPoint point = new PersistedContactPoint(newContactConstraint, newContact, oldContactConstraint, oldContact);
              boolean allow = true;
              for (int l = 0; l < lsize; l++) {
                ContactListener listener = listeners.get(l);
                if (!listener.persist(point)) {
                  allow = false;
                }
              }
              if (!allow) {
                newContactConstraint.enabled = false;
              }
              persisted[k] = true;
              found = true;
              break;
            }
          }
          if (!found) {
            ContactPoint point = new ContactPoint(newContactConstraint, newContact);
            boolean allow = true;
            for (int l = 0; l < lsize; l++) {
              ContactListener listener = listeners.get(l);
              if (!listener.begin(point)) {
                allow = false;
              }
            }
            if (!allow) {
              newContactConstraint.enabled = false;
            }
          }
        }
        int rsize = persisted.length;
        for (int j = 0; j < rsize; j++) {
          if (!persisted[j]) {
            Contact contact = ocontacts.get(j);
            ContactPoint point = new ContactPoint(newContactConstraint, contact);
            for (int l = 0; l < lsize; l++) {
              ContactListener listener = listeners.get(l);
              listener.end(point);
            }
          }
        }
      } else {
        for (int j = nsize - 1; j >= 0; j--) {
          Contact contact = contacts.get(j);
          ContactPoint point = new ContactPoint(newContactConstraint, contact);
          boolean allow = true;
          for (int l = 0; l < lsize; l++) {
            ContactListener listener = listeners.get(l);
            if (!listener.begin(point)) {
              allow = false;
            }
          }
          if (!allow) {
            newContactConstraint.enabled = false;
          }
        }
      }
      if (newContactConstraint.contacts.size() > 0) {
        newMap.put(newContactConstraint.id, newContactConstraint);
      }
    }
    if (!this.constraints.isEmpty()) {
      Iterator<ContactConstraint> icc = this.constraints.values().iterator();
      while (icc.hasNext()) {
        ContactConstraint contactConstraint = icc.next();
        int rsize = contactConstraint.contacts.size();
        for (int i = 0; i < rsize; i++) {
          Contact contact = contactConstraint.contacts.get(i);
          ContactPoint point = new ContactPoint(contactConstraint, contact);
          for (int l = 0; l < lsize; l++) {
            ContactListener listener = listeners.get(l);
            listener.end(point);
          }
        }
      }
    }
    if (size > 0) {
      this.constraints.clear();
      this.constraints1 = this.constraints;
      this.constraints = newMap;
    } else {
      this.constraints.clear();
    }
    this.constraintQueue.clear();
  }
}