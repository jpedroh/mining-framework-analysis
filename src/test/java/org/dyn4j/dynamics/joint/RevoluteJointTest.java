package org.dyn4j.dynamics.joint;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;
import junit.framework.TestCase;

/**
 * Used to test the {@link RevoluteJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.2
 */
public class RevoluteJointTest extends AbstractJointTest {
  /**
	 * Tests the successful creation case.
	 */
  @Test public void createSuccess() {
    new RevoluteJoint<Body>(b1, b2, new Vector2());
  }

  /**
	 * Tests the create with a null body1.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullBody1() {
    new RevoluteJoint<Body>(null, b2, new Vector2());
  }

  /**
	 * Tests the create with a null body2.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullBody2() {
    new RevoluteJoint<Body>(b1, null, new Vector2());
  }

  /**
	 * Tests the create method passing a null anchor.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullAnchor() {
    new RevoluteJoint<Body>(b1, b2, null);
  }

  /**
	 * Tests the create method passing the same body.
	 */
  @Test(expected = IllegalArgumentException.class) public void createWithSameBody() {
    new RevoluteJoint<Body>(b1, b1, new Vector2());
  }

  /**
	 * Tests valid maximum torque values.
	 */
  @Test public void setMaximumMotorTorque() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    rj.setMaximumMotorTorque(0.0);
    TestCase.assertEquals(0.0, rj.getMaximumMotorTorque());
    rj.setMaximumMotorTorque(10.0);
    TestCase.assertEquals(10.0, rj.getMaximumMotorTorque());
    rj.setMaximumMotorTorque(2548.0);
    TestCase.assertEquals(2548.0, rj.getMaximumMotorTorque());
  }

  /**
	 * Tests a negative maximum torque value.
	 */
  @Test(expected = IllegalArgumentException.class) public void setMaximumMotorTorqueNegative() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    rj.setMaximumMotorTorque(-2.0);
  }

  /**
	 * Tests the setting the maximum motor torque wrt. sleeping.
	 */
  @Test public void setMaximumMotorTorqueSleep() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    TestCase.assertFalse(rj.isMotorEnabled());
    TestCase.assertEquals(1000.0, rj.getMaximumMotorTorque());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    rj.setMotorEnabled(true);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setMaximumMotorTorque(1000.0);
    TestCase.assertEquals(1000.0, rj.getMaximumMotorTorque());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    rj.setMaximumMotorTorque(2.0);
    TestCase.assertEquals(2.0, rj.getMaximumMotorTorque());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    rj.setMotorEnabled(false);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setMaximumMotorTorque(1.0);
    TestCase.assertEquals(1.0, rj.getMaximumMotorTorque());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
  }

  /**
	 * Tests the enabling of the motor.
	 */
  @Test public void setMotorEnabled() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    TestCase.assertFalse(rj.isMotorEnabled());
    rj.setMotorEnabled(true);
    TestCase.assertTrue(rj.isMotorEnabled());
    rj.setMotorEnabled(false);
    TestCase.assertFalse(rj.isMotorEnabled());
  }

  /**
	 * Tests the enabling of the motor wrt sleeping.
	 */
  @Test public void setMotorEnabledSleep() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    TestCase.assertFalse(rj.isMotorEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setMotorEnabled(false);
    TestCase.assertFalse(rj.isMotorEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    rj.setMotorEnabled(true);
    TestCase.assertTrue(rj.isMotorEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setMotorEnabled(true);
    TestCase.assertTrue(rj.isMotorEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    rj.setMotorEnabled(false);
    TestCase.assertFalse(rj.isMotorEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
  }

  /**
	 * Tests the setting the motor speed.
	 */
  @Test public void setMotorSpeed() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    TestCase.assertEquals(0.0, rj.getMotorSpeed());
    rj.setMotorSpeed(2.0);
    TestCase.assertEquals(2.0, rj.getMotorSpeed());
    rj.setMotorSpeed(-1.0);
    TestCase.assertEquals(-1.0, rj.getMotorSpeed());
    rj.setMotorSpeed(0.0);
    TestCase.assertEquals(0.0, rj.getMotorSpeed());
  }

  /**
	 * Tests the setting the motor speed wrt. sleeping.
	 */
  @Test public void setMotorSpeedSleep() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    TestCase.assertFalse(rj.isMotorEnabled());
    TestCase.assertEquals(0.0, rj.getMotorSpeed());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    rj.setMotorEnabled(true);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setMotorSpeed(0.0);
    TestCase.assertEquals(0.0, rj.getMotorSpeed());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    rj.setMotorSpeed(2.0);
    TestCase.assertEquals(2.0, rj.getMotorSpeed());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    rj.setMotorEnabled(false);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setMotorSpeed(-1.0);
    TestCase.assertEquals(-1.0, rj.getMotorSpeed());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
  }

  /**
	 * Tests the successful setting of the maximum angle.
	 */
  @Test public void setUpperLimitSuccess() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    rj.setUpperLimit(Math.toRadians(10));
    TestCase.assertEquals(Math.toRadians(10), rj.getUpperLimit(), 1e-6);
  }

  /**
	 * Tests the failed setting of the maximum angle.
	 */
  @Test(expected = IllegalArgumentException.class) public void setUpperLimitInvalid() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    rj.setUpperLimit(Math.toRadians(-10));
  }

  /**
	 * Tests the successful setting of the minimum angle.
	 */
  @Test public void setLowerLimit() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    rj.setLowerLimit(Math.toRadians(-10));
    TestCase.assertEquals(Math.toRadians(-10), rj.getLowerLimit(), 1e-6);
  }

  /**
	 * Tests the failed setting of the maximum angle.
	 */
  @Test(expected = IllegalArgumentException.class) public void setLowerLimitInvalid() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    rj.setLowerLimit(Math.toRadians(10));
  }

  /**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
  @Test public void setUpperAndLowerLimits() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    rj.setLimits(Math.toRadians(-30), Math.toRadians(20));
    TestCase.assertEquals(Math.toRadians(-30), rj.getLowerLimit(), 1e-6);
    TestCase.assertEquals(Math.toRadians(20), rj.getUpperLimit(), 1e-6);
  }

  /**
	 * Tests the failed setting of the minimum and maximum angle.
	 */
  @Test(expected = IllegalArgumentException.class) public void setUpperAndLowerLimitsInvalid() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    rj.setLimits(Math.toRadians(30), Math.toRadians(20));
  }

  /**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
  @Test public void setUpperAndLowerLimitsToSameValue() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    rj.setLimits(Math.toRadians(30), Math.toRadians(30));
    TestCase.assertEquals(Math.toRadians(30), rj.getLowerLimit(), 1e-6);
    TestCase.assertEquals(Math.toRadians(30), rj.getUpperLimit(), 1e-6);
  }

  /**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
  @Test public void setLimitEnabledSleep() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    TestCase.assertFalse(rj.isLimitEnabled());
    rj.setLimitEnabled(true);
    rj.setLimitEnabled(false);
    TestCase.assertFalse(rj.isLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimitEnabled(false);
    TestCase.assertFalse(rj.isLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    rj.setLimitEnabled(true);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimitEnabled(true);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    rj.setLimitEnabled(false);
    TestCase.assertFalse(rj.isLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
  }

  /**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
  @Test public void setLimitsSameSleep() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    TestCase.assertFalse(rj.isLimitEnabled());
    rj.setLimitEnabled(true);
    double defaultLowerLimit = rj.getLowerLimit();
    double defaultUpperLimit = rj.getUpperLimit();
    TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setLimits(Math.PI, Math.PI);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(Math.PI, rj.getLowerLimit());
    TestCase.assertEquals(Math.PI, rj.getUpperLimit());
    rj.setLowerLimit(-Math.PI);
    TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(Math.PI, Math.PI);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(Math.PI, rj.getLowerLimit());
    TestCase.assertEquals(Math.PI, rj.getUpperLimit());
    rj.setUpperLimit(2 * Math.PI);
    TestCase.assertEquals(2 * Math.PI, rj.getUpperLimit());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(Math.PI, Math.PI);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(Math.PI, rj.getLowerLimit());
    TestCase.assertEquals(Math.PI, rj.getUpperLimit());
    rj.setLimitEnabled(false);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(-Math.PI, -Math.PI);
    TestCase.assertFalse(rj.isLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
    TestCase.assertEquals(-Math.PI, rj.getUpperLimit());
  }

  /**
	 * Tests the sleep interaction when changing the limits to different values.
	 */
  @Test public void setLimitsDifferentSleep() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    TestCase.assertFalse(rj.isLimitEnabled());
    rj.setLimitEnabled(true);
    double defaultLowerLimit = rj.getLowerLimit();
    double defaultUpperLimit = rj.getUpperLimit();
    TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setLimits(-Math.PI, Math.PI);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
    TestCase.assertEquals(Math.PI, rj.getUpperLimit());
    rj.setLowerLimit(-2 * Math.PI);
    TestCase.assertEquals(-2 * Math.PI, rj.getLowerLimit());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(-Math.PI, Math.PI);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
    TestCase.assertEquals(Math.PI, rj.getUpperLimit());
    rj.setUpperLimit(2 * Math.PI);
    TestCase.assertEquals(2 * Math.PI, rj.getUpperLimit());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(-Math.PI, Math.PI);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
    TestCase.assertEquals(Math.PI, rj.getUpperLimit());
    rj.setLimitEnabled(false);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(Math.PI, 2 * Math.PI);
    TestCase.assertFalse(rj.isLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(Math.PI, rj.getLowerLimit());
    TestCase.assertEquals(2 * Math.PI, rj.getUpperLimit());
  }

  /**
	 * Tests the sleep interaction when changing the lower limit.
	 */
  @Test public void setLowerLimitSleep() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    TestCase.assertFalse(rj.isLimitEnabled());
    rj.setLimitEnabled(true);
    double defaultLowerLimit = rj.getLowerLimit();
    double defaultUpperLimit = rj.getUpperLimit();
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLowerLimit(defaultLowerLimit);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setLowerLimit(-Math.PI);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setLimitEnabled(false);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLowerLimit(-2 * Math.PI);
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(-2 * Math.PI, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
  }

  /**
	 * Tests the sleep interaction when changing the upper limit.
	 */
  @Test public void setUpperLimitSleep() {
    RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
    TestCase.assertFalse(rj.isLimitEnabled());
    rj.setLimitEnabled(true);
    double defaultLowerLimit = rj.getLowerLimit();
    double defaultUpperLimit = rj.getUpperLimit();
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setUpperLimit(defaultUpperLimit);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setUpperLimit(Math.PI);
    TestCase.assertTrue(rj.isLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(Math.PI, rj.getUpperLimit());
    rj.setLimitEnabled(false);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setUpperLimit(2 * Math.PI);
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(2 * Math.PI, rj.getUpperLimit());
  }
}