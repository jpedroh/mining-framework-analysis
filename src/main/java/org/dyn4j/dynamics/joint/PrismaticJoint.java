package org.dyn4j.dynamics.joint;
import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Matrix33;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Vector3;
import org.dyn4j.resources.Messages;

/**
 * Implementation of a prismatic joint.
 * <p>
 * A prismatic joint constrains the linear motion of two bodies along an axis
 * and prevents relative rotation.  The whole system can rotate and translate 
 * freely.
 * <p>
 * The initial relative rotation of the bodies will remain unchanged unless 
 * updated by calling {@link #setReferenceAngle(double)} method.  The bodies
 * are not required to be aligned in any particular way.
 * <p>
 * The world space anchor point can be any point but is typically a point on
 * the axis of allowed motion, usually the world center of either of the joined
 * bodies.
 * <p>
 * The limits are linear limits along the axis.  The limits are checked against
 * the separation of the local anchor points, rather than the separation of the
 * bodies.  This can have the effect of offsetting the limit values.  The best
 * way to describe the effect is to examine the "0 to 0" limit case.  This case
 * specifies that the bodies should not move along the axis, forcing them to 
 * stay at their <em>initial location</em> along the axis.  So if the bodies 
 * were initially separated when they were joined, they will stay separated at
 * that initial distance.
 * <p>
 * This joint also supports a motor.  The motor is a linear motor along the
 * axis.  The motor speed can be positive or negative to indicate motion along
 * or opposite the axis direction.  The maximum motor force must be greater 
 * than zero for the motor to apply any motion.
 * @author William Bittle
 * @version 4.1.0
 * @since 1.0.0
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Prismatic_Joint" target="_blank">Documentation</a>
 * @see <a href="http://www.dyn4j.org/2011/03/prismatic-constraint/" target="_blank">Prismatic Constraint</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class PrismaticJoint<T extends PhysicsBody> extends Joint<T> implements Shiftable, DataContainer {
  /** The local anchor point on the first {@link PhysicsBody} */
  protected final Vector2 localAnchor1;

  /** The local anchor point on the second {@link PhysicsBody} */
  protected final Vector2 localAnchor2;

  /** The axis representing the allowed line of motion */
  private final Vector2 xAxis;

  /** The perpendicular axis of the line of motion */
  private final Vector2 yAxis;

  /** The initial angle between the two {@link PhysicsBody}s */
  protected double referenceAngle;

  /** Whether the limit is enabled or not */
  protected boolean limitEnabled;

  /** The upper limit in meters */
  protected double upperLimit;

  /** The lower limit in meters */
  protected double lowerLimit;

  /** Whether the motor is enabled or not */
  protected boolean motorEnabled;

  /** The target velocity in meters / second */
  protected double motorSpeed;

  /** The maximum force the motor can apply in newtons */
  protected double maximumMotorForce;

  /** The constraint mass; K = J * Minv * Jtrans */
  private final Matrix22 K;

  /** The mass of the motor */
  private double axialMass;

  /** The world space yAxis  */
  private Vector2 perp;

  /** The world space xAxis */
  private Vector2 axis;

  /** s1 = (r1 + d).cross(perp) */
  private double s1;

  /** s2 = r2.cross(perp) */
  private double s2;

  /** a1 = (r1 + d).cross(axis) */
  private double a1;

  /** a2 = r2.cross(axis) */
  private double a2;

  /** The current translation */
  private double translation;

  /** The accumulated impulse for warm starting */
  private Vector2 impulse;

  /** The impulse applied by the motor */
  private double motorImpulse;

  /** The impulse applied by the lower limit */
  private double lowerImpulse;

  /** The impulse applied by the upper limit */
  private double upperImpulse;

  /**
	 * Minimal constructor.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @param anchor the anchor point in world coordinates
	 * @param axis the axis of allowed motion
	 * @throws NullPointerException if body1, body2, anchor or axis is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
  public PrismaticJoint(T body1, T body2, Vector2 anchor, Vector2 axis) {
    super(body1, body2, false);
    if (body1 == body2) {
      throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
    }
    if (anchor == null) {
      throw new NullPointerException(Messages.getString("dynamics.joint.nullAnchor"));
    }
    if (axis == null) {
      throw new NullPointerException(Messages.getString("dynamics.joint.nullAxis"));
    }
    this.localAnchor1 = body1.getLocalPoint(anchor);
    this.localAnchor2 = body2.getLocalPoint(anchor);
    this.limitEnabled = false;
    this.lowerLimit = 0.0;
    this.upperLimit = 0.0;
    this.motorEnabled = false;
    this.motorSpeed = 0.0;
    this.maximumMotorForce = 1000.0;
    Vector2 n = axis.getNormalized();
    this.xAxis = body2.getLocalVector(n);
    this.yAxis = this.xAxis.getRightHandOrthogonalVector();
    this.referenceAngle = body1.getTransform().getRotationAngle() - body2.getTransform().getRotationAngle();
    this.K = new Matrix22();
    this.axialMass = 0.0;
    this.perp = null;
    this.axis = null;
    this.s1 = 0.0;
    this.s2 = 0.0;
    this.a1 = 0.0;
    this.a2 = 0.0;
    this.translation = 0.0;
    this.impulse = new Vector2();
    this.motorImpulse = 0.0;
    this.lowerImpulse = 0.0;
    this.upperImpulse = 0.0;
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("PrismaticJoint[").append(super.toString()).append("|Anchor=").append(this.getAnchor1()).append("|Axis=").append(this.getAxis()).append("|IsMotorEnabled=").append(this.motorEnabled).append("|MotorSpeed=").append(this.motorSpeed).append("|MaximumMotorForce=").append(this.maximumMotorForce).append("|ReferenceAngle=").append(this.referenceAngle).append("|IsLimitEnabled=").append(this.limitEnabled).append("|LowerLimit=").append(this.lowerLimit).append("|UpperLimit=").append(this.upperLimit).append("]");
    return sb.toString();
  }

  @Override public void initializeConstraints(TimeStep step, Settings settings) {
    Transform t1 = this.body1.getTransform();
    Transform t2 = this.body2.getTransform();
    Mass m1 = this.body1.getMass();
    Mass m2 = this.body2.getMass();
    double invM1 = m1.getInverseMass();
    double invM2 = m2.getInverseMass();
    double invI1 = m1.getInverseInertia();
    double invI2 = m2.getInverseInertia();
    Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
    Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
    Vector2 d = this.body1.getWorldCenter().sum(r1).subtract(this.body2.getWorldCenter().sum(r2));
    this.axis = this.body2.getWorldVector(this.xAxis);
    this.a1 = r1.cross(this.axis);
    this.a2 = r2.sum(d).cross(this.axis);
    this.axialMass = invM1 + invM2 + this.a1 * this.a1 * invI1 + this.a2 * this.a2 * invI2;
    if (this.axialMass > Epsilon.E) {
      this.axialMass = 1.0 / this.axialMass;
    }
    this.perp = this.body2.getWorldVector(this.yAxis);
    this.s1 = r1.cross(this.perp);
    this.s2 = r2.sum(d).cross(this.perp);
    this.K.m00 = invM1 + invM2 + this.s1 * this.s1 * invI1 + this.s2 * this.s2 * invI2;
    this.K.m01 = this.s1 * invI1 + this.s2 * invI2;
    this.K.m10 = this.K.m01;
    this.K.m11 = invI1 + invI2;
    if (this.K.m11 <= Epsilon.E) {
      this.K.m11 = 1.0;
    }
    if (this.limitEnabled) {
      this.translation = this.axis.dot(d);
    } else {
      this.lowerImpulse = 0.0;
      this.upperImpulse = 0.0;
    }
    if (!this.motorEnabled) {
      this.motorImpulse = 0.0;
    }
    if (settings.isWarmStartingEnabled()) {
      double dtr = step.getDeltaTimeRatio();
      this.impulse.multiply(dtr);
      this.motorImpulse *= dtr;
      this.lowerImpulse *= dtr;
      this.upperImpulse *= dtr;
      double axialImpulse = this.motorImpulse + this.lowerImpulse - this.upperImpulse;
      Vector2 P = new Vector2();
      P.x = this.perp.x * this.impulse.x + axialImpulse * this.axis.x;
      P.y = this.perp.y * this.impulse.x + axialImpulse * this.axis.y;
      double l1 = this.impulse.x * this.s1 + this.impulse.y + axialImpulse * this.a1;
      double l2 = this.impulse.x * this.s2 + this.impulse.y + axialImpulse * this.a2;
      this.body1.getLinearVelocity().add(P.product(invM1));
      this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * l1);
      this.body2.getLinearVelocity().subtract(P.product(invM2));
      this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * l2);
    } else {
      this.impulse.zero();
      this.motorImpulse = 0.0;
      this.lowerImpulse = 0.0;
      this.upperImpulse = 0.0;
    }
  }

  @Override public void solveVelocityConstraints(TimeStep step, Settings settings) {
    Mass m1 = this.body1.getMass();
    Mass m2 = this.body2.getMass();
    double invM1 = m1.getInverseMass();
    double invM2 = m2.getInverseMass();
    double invI1 = m1.getInverseInertia();
    double invI2 = m2.getInverseInertia();
    Vector2 v1 = this.body1.getLinearVelocity();
    Vector2 v2 = this.body2.getLinearVelocity();
    double w1 = this.body1.getAngularVelocity();
    double w2 = this.body2.getAngularVelocity();
    if (this.motorEnabled) {
      double Cdt = this.axis.dot(v1.difference(v2)) + this.a1 * w1 - this.a2 * w2;
      double impulse = this.axialMass * (this.motorSpeed - Cdt);
      double oldImpulse = this.motorImpulse;
      double maxImpulse = this.maximumMotorForce * step.getDeltaTime();
      this.motorImpulse = Interval.clamp(this.motorImpulse + impulse, -maxImpulse, maxImpulse);
      impulse = this.motorImpulse - oldImpulse;
      Vector2 P = this.axis.product(impulse);
      double l1 = impulse * this.a1;
      double l2 = impulse * this.a2;
      v1.add(P.product(invM1));
      w1 += l1 * invI1;
      v2.subtract(P.product(invM2));
      w2 -= l2 * invI2;
    }
    double invdt = step.getInverseDeltaTime();
    if (this.limitEnabled) {
      {
        double C = this.translation - this.lowerLimit;
        double Cdot = this.axis.dot(v1.difference(v2)) + this.a1 * w1 - this.a2 * w2;
        double impulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * invdt);
        double oldImpulse = this.lowerImpulse;
        this.lowerImpulse = Math.max(this.lowerImpulse + impulse, 0.0);
        impulse = this.lowerImpulse - oldImpulse;
        Vector2 P = this.axis.product(impulse);
        double l1 = impulse * this.a1;
        double l2 = impulse * this.a2;
        v1.add(P.product(invM1));
        w1 += l1 * invI1;
        v2.subtract(P.product(invM2));
        w2 -= l2 * invI2;
      }
      {
        double C = this.upperLimit - this.translation;
        double Cdot = this.axis.dot(v2.difference(v1)) + this.a2 * w2 - this.a1 * w1;
        double impulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * invdt);
        double oldImpulse = this.upperImpulse;
        this.upperImpulse = Math.max(this.upperImpulse + impulse, 0.0);
        impulse = this.upperImpulse - oldImpulse;
        Vector2 P = this.axis.product(impulse);
        double l1 = impulse * this.a1;
        double l2 = impulse * this.a2;
        v1.subtract(P.product(invM1));
        w1 -= l1 * invI1;
        v2.add(P.product(invM2));
        w2 += l2 * invI2;
      }
    }
    Vector2 Cdt = new Vector2();
    Cdt.x = this.perp.dot(v1.difference(v2)) + this.s1 * w1 - this.s2 * w2;
    Cdt.y = w1 - w2;
    Vector2 f2r = this.K.solve(Cdt.negate());
    this.impulse.x += f2r.x;
    this.impulse.y += f2r.y;
    Vector2 P = this.perp.product(f2r.x);
    double l1 = f2r.x * this.s1 + f2r.y;
    double l2 = f2r.x * this.s2 + f2r.y;
    v1.add(P.product(invM1));
    w1 += l1 * invI1;
    v2.subtract(P.product(invM2));
    w2 -= l2 * invI2;
    this.body1.setAngularVelocity(w1);
    this.body2.setAngularVelocity(w2);
  }

  @Override public boolean solvePositionConstraints(TimeStep step, Settings settings) {
    double linearTolerance = settings.getLinearTolerance();
    double angularTolerance = settings.getAngularTolerance();
    Transform t1 = this.body1.getTransform();
    Transform t2 = this.body2.getTransform();
    Mass m1 = this.body1.getMass();
    Mass m2 = this.body2.getMass();
    double invM1 = m1.getInverseMass();
    double invM2 = m2.getInverseMass();
    double invI1 = m1.getInverseInertia();
    double invI2 = m2.getInverseInertia();
    Vector2 c1 = this.body1.getWorldCenter();
    Vector2 c2 = this.body2.getWorldCenter();
    Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
    Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
    Vector2 d = c1.sum(r1).subtract(c2.sum(r2));
    Vector2 axis = this.body2.getWorldVector(this.xAxis);
    double a1 = r1.cross(axis);
    double a2 = r2.sum(d).cross(axis);
    Vector2 perp = this.body2.getWorldVector(this.yAxis);
    double s1 = r1.cross(perp);
    double s2 = r2.sum(d).cross(perp);
    Vector2 C = new Vector2();
    C.x = perp.dot(d);
    C.y = this.getRelativeRotation();
    double C2 = 0.0;
    double linearError = Math.abs(C.x);
    double angularError = Math.abs(C.y);
    boolean limitActive = false;
    if (this.limitEnabled) {
      double translation = axis.dot(d);
      if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * linearTolerance) {
        C2 = translation;
        linearError = Math.abs(translation);
        limitActive = true;
      } else {
        if (translation <= this.lowerLimit) {
          C2 = Math.min(translation - this.lowerLimit, 0.0);
          linearError = Math.max(linearError, this.lowerLimit - translation);
          limitActive = true;
        } else {
          if (translation >= this.upperLimit) {
            C2 = Math.max(translation - this.upperLimit, 0.0);
            linearError = Math.max(linearError, translation - this.upperLimit);
            limitActive = true;
          }
        }
      }
    }
    Vector3 impulse;
    if (limitActive) {
      Matrix33 K = new Matrix33();
      K.m00 = invM1 + invM2 + s1 * s1 * invI1 + s2 * s2 * invI2;
      K.m01 = s1 * invI1 + s2 * invI2;
      K.m02 = s1 * a1 * invI1 + s2 * a2 * invI2;
      K.m10 = K.m01;
      K.m11 = invI1 + invI2;
      if (K.m11 <= Epsilon.E) {
        K.m11 = 1.0;
      }
      K.m12 = a1 * invI1 + a2 * invI2;
      K.m20 = K.m02;
      K.m21 = K.m12;
      K.m22 = invM1 + invM2 + a1 * a1 * invI1 + a2 * a2 * invI2;
      Vector3 Clim = new Vector3(C.x, C.y, C2);
      impulse = K.solve33(Clim.negate());
    } else {
      Matrix22 K = new Matrix22();
      K.m00 = invM1 + invM2 + s1 * s1 * invI1 + s2 * s2 * invI2;
      K.m01 = s1 * invI1 + s2 * invI2;
      K.m10 = K.m01;
      K.m11 = invI1 + invI2;
      if (K.m11 <= Epsilon.E) {
        K.m11 = 1.0;
      }
      Vector2 impulsec = K.solve(C.negate());
      impulse = new Vector3(impulsec.x, impulsec.y, 0.0);
    }
    Vector2 P = new Vector2();
    P.x = perp.x * impulse.x + impulse.z * axis.x;
    P.y = perp.y * impulse.x + impulse.z * axis.y;
    double l1 = impulse.x * s1 + impulse.y + impulse.z * a1;
    double l2 = impulse.x * s2 + impulse.y + impulse.z * a2;
    this.body1.translate(P.product(invM1));
    this.body1.rotateAboutCenter(l1 * invI1);
    this.body2.translate(P.product(-invM2));
    this.body2.rotateAboutCenter(-l2 * invI2);
    return linearError <= linearTolerance && angularError <= angularTolerance;
  }

  /**
	 * Returns the relative angle between the two bodies given the reference angle.
	 * @return double
	 */
  private double getRelativeRotation() {
    double rr = this.body1.getTransform().getRotationAngle() - this.body2.getTransform().getRotationAngle() - this.referenceAngle;
    if (rr < -Math.PI) {
      rr += Geometry.TWO_PI;
    }
    if (rr > Math.PI) {
      rr -= Geometry.TWO_PI;
    }
    return rr;
  }

  @Override public Vector2 getAnchor1() {
    return this.body1.getWorldPoint(this.localAnchor1);
  }

  @Override public Vector2 getAnchor2() {
    return this.body2.getWorldPoint(this.localAnchor2);
  }

  @Override public Vector2 getReactionForce(double invdt) {
    Vector2 force = new Vector2();
    force.x = this.impulse.x * this.perp.x + (this.motorImpulse + this.lowerImpulse - this.upperImpulse) * this.axis.x;
    force.y = this.impulse.x * this.perp.y + (this.motorImpulse + this.lowerImpulse - this.upperImpulse) * this.axis.y;
    force.multiply(invdt);
    return force;
  }

  @Override public double getReactionTorque(double invdt) {
    return invdt * this.impulse.y;
  }

  @Override public void shift(Vector2 shift) {
  }

  /**
	 * Returns the current joint speed.
	 * @return double
	 */
  public double getJointSpeed() {
    Transform t1 = this.body1.getTransform();
    Transform t2 = this.body2.getTransform();
    Vector2 c1 = this.body1.getWorldCenter();
    Vector2 c2 = this.body2.getWorldCenter();
    Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
    Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
    Vector2 d = c1.sum(r1).subtract(c2.sum(r2));
    Vector2 axis = this.body2.getWorldVector(this.xAxis);
    Vector2 v1 = this.body1.getLinearVelocity();
    Vector2 v2 = this.body2.getLinearVelocity();
    double w1 = this.body1.getAngularVelocity();
    double w2 = this.body2.getAngularVelocity();
    double speed = d.dot(axis.cross(w2)) + axis.dot(v1.sum(r1.cross(w1)).subtract(v2.sum(r2.cross(w2))));
    return speed;
  }

  /**
	 * Returns the current joint translation.
	 * @return double
	 */
  public double getJointTranslation() {
    Vector2 p1 = this.body1.getWorldPoint(this.localAnchor1);
    Vector2 p2 = this.body2.getWorldPoint(this.localAnchor2);
    Vector2 d = p1.difference(p2);
    Vector2 axis = this.body2.getWorldVector(this.xAxis);
    return d.dot(axis);
  }

  /**
	 * Returns true if the motor is enabled.
	 * @return boolean
	 */
  public boolean isMotorEnabled() {
    return this.motorEnabled;
  }

  /**
	 * Enables or disables the motor.
	 * @param motorEnabled true if the motor should be enabled
	 */
  public void setMotorEnabled(boolean motorEnabled) {
    if (this.motorEnabled != motorEnabled) {
      this.body1.setAtRest(false);
      this.body2.setAtRest(false);
      this.motorEnabled = motorEnabled;
    }
  }

  /**
	 * Returns the target motor speed in meters / second.
	 * @return double
	 */
  public double getMotorSpeed() {
    return this.motorSpeed;
  }

  /**
	 * Sets the target motor speed.
	 * @param motorSpeed the target motor speed in meters / second
	 * @see #setMaximumMotorForce(double)
	 */
  public void setMotorSpeed(double motorSpeed) {
    if (this.motorSpeed != motorSpeed) {
      if (this.motorEnabled) {
        this.body1.setAtRest(false);
        this.body2.setAtRest(false);
      }
      this.motorSpeed = motorSpeed;
    }
  }

  /**
	 * Returns the maximum force the motor can apply to the joint
	 * to achieve the target speed.
	 * @return double
	 */
  public double getMaximumMotorForce() {
    return this.maximumMotorForce;
  }

  /**
	 * Sets the maximum force the motor can apply to the joint
	 * to achieve the target speed.
	 * @param maximumMotorForce the maximum force in newtons; must be greater than zero
	 * @throws IllegalArgumentException if maxMotorForce is less than zero
	 * @see #setMotorSpeed(double)
	 */
  public void setMaximumMotorForce(double maximumMotorForce) {
    if (maximumMotorForce < 0.0) {
      throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidMaximumMotorForce"));
    }
    if (this.maximumMotorForce != maximumMotorForce) {
      if (this.motorEnabled) {
        this.body1.setAtRest(false);
        this.body2.setAtRest(false);
      }
      this.maximumMotorForce = maximumMotorForce;
    }
  }

  /**
	 * Returns the applied motor force.
	 * @param invdt the inverse delta time
	 * @return double
	 */
  public double getMotorForce(double invdt) {
    return this.motorImpulse * invdt;
  }

  /**
	 * Returns true if the limit is enabled.
	 * @return boolean
	 */
  public boolean isLimitEnabled() {
    return this.limitEnabled;
  }

  /**
	 * Enables or disables the limits.
	 * @param limitEnabled true if the limit should be enabled.
	 */
  public void setLimitEnabled(boolean limitEnabled) {
    if (this.limitEnabled != limitEnabled) {
      this.body1.setAtRest(false);
      this.body2.setAtRest(false);
      this.limitEnabled = limitEnabled;
    }
  }

  /**
	 * Returns the lower limit in meters.
	 * @return double
	 */
  public double getLowerLimit() {
    return this.lowerLimit;
  }

  /**
	 * Sets the lower limit.
	 * @param lowerLimit the lower limit in meters
	 * @throws IllegalArgumentException if lowerLimit is greater than the current upper limit
	 */
  public void setLowerLimit(double lowerLimit) {
    if (lowerLimit > this.upperLimit) {
      throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLowerLimit"));
    }
    if (this.lowerLimit != lowerLimit) {
      if (this.limitEnabled) {
        this.body1.setAtRest(false);
        this.body2.setAtRest(false);
        this.lowerImpulse = 0.0;
      }
      this.lowerLimit = lowerLimit;
    }
  }

  /**
	 * Returns the upper limit in meters.
	 * @return double
	 */
  public double getUpperLimit() {
    return this.upperLimit;
  }

  /**
	 * Sets the upper limit.
	 * @param upperLimit the upper limit in meters
	 * @throws IllegalArgumentException if upperLimit is less than the current lower limit
	 */
  public void setUpperLimit(double upperLimit) {
    if (upperLimit < this.lowerLimit) {
      throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidUpperLimit"));
    }
    if (this.upperLimit != upperLimit) {
      if (this.limitEnabled) {
        this.body1.setAtRest(false);
        this.body2.setAtRest(false);
        this.upperImpulse = 0.0;
      }
      this.upperLimit = upperLimit;
    }
  }

  /**
	 * Sets the upper and lower limits.
	 * <p>
	 * The lower limit must be less than or equal to the upper limit.
	 * @param lowerLimit the lower limit in meters
	 * @param upperLimit the upper limit in meters
	 * @throws IllegalArgumentException if lowerLimit is greater than upperLimit
	 */
  public void setLimits(double lowerLimit, double upperLimit) {
    if (lowerLimit > upperLimit) {
      throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLimits"));
    }
    if (this.lowerLimit != lowerLimit || this.upperLimit != upperLimit) {
      if (this.limitEnabled) {
        this.body1.setAtRest(false);
        this.body2.setAtRest(false);
      }
      this.lowerImpulse = 0.0;
      this.upperImpulse = 0.0;
      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
    }
  }

  /**
	 * Sets the upper and lower limits and enables the limits.
	 * <p>
	 * The lower limit must be less than or equal to the upper limit.
	 * @param lowerLimit the lower limit in meters
	 * @param upperLimit the upper limit in meters
	 * @throws IllegalArgumentException if lowerLimit is greater than upperLimit
	 * @since 2.2.2
	 */
  public void setLimitsEnabled(double lowerLimit, double upperLimit) {
    if (lowerLimit > upperLimit) {
      throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLimits"));
    }
    this.setLimitEnabled(true);
    this.setLimits(lowerLimit, upperLimit);
  }

  /**
	 * Returns the axis in which the joint is allowed move along in world coordinates.
	 * @return {@link Vector2}
	 * @since 3.0.0
	 */
  public Vector2 getAxis() {
    return this.body2.getWorldVector(this.xAxis);
  }

  /**
	 * Returns the reference angle.
	 * <p>
	 * The reference angle is the angle calculated when the joint was created from the
	 * two joined bodies.  The reference angle is the angular difference between the
	 * bodies.
	 * @return double
	 * @since 3.0.1
	 */
  public double getReferenceAngle() {
    return this.referenceAngle;
  }

  /**
	 * Sets the reference angle.
	 * <p>
	 * This method can be used to set the reference angle to override the computed
	 * reference angle from the constructor.  This is useful in recreating the joint
	 * from a current state.
	 * <p>
	 * This can also be used to override the initial angle between the bodies.
	 * @param angle the reference angle
	 * @see #getReferenceAngle()
	 * @since 3.0.1
	 */
  public void setReferenceAngle(double angle) {
    this.referenceAngle = angle;
  }
}