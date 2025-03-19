package org.dyn4j.dynamics.contact;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a solved contact point.
 * <p>
 * A solved contact point is a contact that has been handled by the
 * contact constraint solver and therefore has the normal and tangential
 * impulses computed.
 * @author William Bittle
 * @see ContactPoint
 * @version 3.3.0
 * @since 1.0.0
 */
public class SolvedContactPoint extends ContactPoint {
  /** The accumulated normal impulse */
  protected final double normalImpulse;

  /** The accumulated tangential impulse */
  protected final double tangentialImpulse;

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
	 * @param normalImpulse the accumulated normal impulse
	 * @param tangentialImpulse the accumulated tangential impulse
	 */
  public SolvedContactPoint(ContactPointId id, Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Vector2 point, Vector2 normal, double depth, double normalImpulse, double tangentialImpulse) {
    super(id, body1, fixture1, body2, fixture2, point, normal, depth, false);
    this.normalImpulse = normalImpulse;
    this.tangentialImpulse = tangentialImpulse;
  }

  /**
	 * Helper constructor for a contact constraint and contact.
	 * @param constraint the constraint
	 * @param contact the contact
	 */
  public SolvedContactPoint(ContactConstraint constraint, Contact contact) {
    super(constraint, contact);
    this.normalImpulse = contact.jn;
    this.tangentialImpulse = contact.jt;
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("SolvedContactPoint[Id=").append(this.id).append("|Body1=").append(this.body1.getId()).append("|Fixture1=").append(this.fixture1.getId()).append("|Body2=").append(this.body2.getId()).append("|Fixture2=").append(this.fixture2.getId()).append("|Point=").append(this.point).append("|Normal=").append(this.normal).append("|Depth=").append(this.depth).append("|NormalImpulse=").append(this.normalImpulse).append("|TangentImpulse=").append(this.tangentialImpulse).append("]");
    return sb.toString();
  }

  /**
	 * Copy constructor (shallow).
	 * @param scp the {@link SolvedContactPoint} to copy
	 */
  public SolvedContactPoint(SolvedContactPoint scp) {
    super(scp);
    this.normalImpulse = scp.normalImpulse;
    this.tangentialImpulse = scp.tangentialImpulse;
  }

  /**
	 * Returns the accumulated normal impulse.
	 * @return double
	 */
  public double getNormalImpulse() {
    return this.normalImpulse;
  }

  /**
	 * Returns the accumulated tangential impulse.
	 * @return double
	 */
  public double getTangentialImpulse() {
    return this.tangentialImpulse;
  }
}