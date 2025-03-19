package org.dyn4j.dynamics.contact;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a persisted contact point.
 * <p>
 * A persisted contact point is a contact point that was retained
 * from the last iteration and therefore contains the previous point,
 * normal, and depth.
 * @author William Bittle
 * @see ContactPoint
 * @version 3.3.0
 * @since 1.0.0
 */
public class PersistedContactPoint extends ContactPoint {
  /** The previous contact point */
  protected final Vector2 oldPoint;

  /** The previous contact normal */
  protected final Vector2 oldNormal;

  /** The previous penetration depth */
  protected final double oldDepth;

  /**
	 * Full constructor.
	 * @param id the contact point id
	 * @param body1 the first {@link Body} in contact
	 * @param fixture1 the first {@link Body}'s {@link BodyFixture}
	 * @param body2 the second {@link Body} in contact
	 * @param fixture2 the second {@link Body}'s {@link BodyFixture}
	 * @param point the world space contact point
	 * @param normal the world space contact normal
	 * @param depth the penetration depth
	 * @param oldPoint the previous world space contact point
	 * @param oldNormal the previous world space contact normal
	 * @param oldDepth the previous penetration depth
	 * @param sensor true if the contact is a sensor contact
	 */
  public PersistedContactPoint(ContactPointId id, Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Vector2 point, Vector2 normal, double depth, Vector2 oldPoint, Vector2 oldNormal, double oldDepth, boolean sensor) {
    super(id, body1, fixture1, body2, fixture2, point, normal, depth, sensor);
    this.oldPoint = oldPoint;
    this.oldNormal = oldNormal;
    this.oldDepth = oldDepth;
  }

  /**
	 * Helper constructor for a contact constraint and contact.
	 * @param newConstraint the new constraint
	 * @param newContact the new contact
	 * @param oldConstraint the old constraint
	 * @param oldContact the old contact
	 */
  public PersistedContactPoint(ContactConstraint newConstraint, Contact newContact, ContactConstraint oldConstraint, Contact oldContact) {
    super(newConstraint, newContact);
    this.oldDepth = oldContact.depth;
    this.oldNormal = oldConstraint.normal;
    this.oldPoint = oldContact.p;
  }

  /**
	 * Copy constructor (shallow).
	 * @param pcp the {@link PersistedContactPoint} to copy
	 */
  public PersistedContactPoint(PersistedContactPoint pcp) {
    super(pcp);
    this.oldPoint = pcp.oldPoint;
    this.oldNormal = pcp.oldNormal;
    this.oldDepth = pcp.oldDepth;
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("PersistedContactPoint[Id=").append(this.id).append("|Body1=").append(this.body1.getId()).append("|Fixture1=").append(this.fixture1.getId()).append("|Body2=").append(this.body2.getId()).append("|Fixture2=").append(this.fixture2.getId()).append("|Point=").append(this.point).append("|Normal=").append(this.normal).append("|Depth=").append(this.depth).append("|PreviousPoint=").append(this.oldPoint).append("|PreviousNormal=").append(this.oldNormal).append("|PreviousDepth=").append(this.oldDepth).append("]");
    return sb.toString();
  }

  /**
	 * Returns the old contact point.
	 * @return {@link Vector2}
	 */
  public Vector2 getOldPoint() {
    return this.oldPoint;
  }

  /**
	 * Returns the old contact normal.
	 * @return {@link Vector2}
	 */
  public Vector2 getOldNormal() {
    return this.oldNormal;
  }

  /**
	 * Returns the old depth.
	 * @return double
	 */
  public double getOldDepth() {
    return this.oldDepth;
  }
}