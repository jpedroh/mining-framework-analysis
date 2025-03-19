package org.dyn4j.dynamics.joint;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;
import junit.framework.TestCase;

/**
 * Test case for the {@link RopeJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 2.2.2
 */
public class RopeJointTest extends AbstractJointTest {
  /**
	 * Tests the successful creation of an rope joint.
	 */
  @Test public void createSuccess() {
    new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2());
  }

  /**
	 * Tests the failed creation of an rope joint.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullBody1() {
    new RopeJoint<Body>(null, b2, new Vector2(), new Vector2());
  }

  /**
	 * Tests the failed creation of an rope joint.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullBody2() {
    new RopeJoint<Body>(b1, null, new Vector2(), new Vector2());
  }

  /**
	 * Tests the failed creation of an rope joint.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullAnchor1() {
    new RopeJoint<Body>(b1, b2, null, new Vector2());
  }

  /**
	 * Tests the failed creation of an rope joint.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullAnchor2() {
    new RopeJoint<Body>(b1, b2, new Vector2(), null);
  }

  /**
	 * Tests the failed creation of an rope joint.
	 */
  @Test(expected = IllegalArgumentException.class) public void createWithSameBody() {
    new RopeJoint<Body>(b1, b1, new Vector2(), new Vector2());
  }

  /**
	 * Tests the successful setting of the upper limit.
	 */
  @Test public void setUpperLimitSuccess() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertEquals(1.0, rj.getUpperLimit());
    rj.setUpperLimit(2.0);
    TestCase.assertEquals(2.0, rj.getUpperLimit(), 1e-6);
  }

  /**
	 * Tests the failed setting of the upper limit.
	 */
  @Test(expected = IllegalArgumentException.class) public void setUpperLimitNegative() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertEquals(1.0, rj.getUpperLimit());
    rj.setUpperLimit(-1.0);
  }

  /**
	 * Tests the successful setting of the lower limit.
	 */
  @Test public void setLowerLimit() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertEquals(1.0, rj.getLowerLimit());
    rj.setLowerLimit(0.0);
    TestCase.assertEquals(0.0, rj.getLowerLimit(), 1e-6);
  }

  /**
	 * Tests the failed setting of the lower limit.
	 */
  @Test(expected = IllegalArgumentException.class) public void setLowerLimitNegative() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertEquals(1.0, rj.getLowerLimit());
    rj.setLowerLimit(-1.0);
  }

  /**
	 * Tests the successful setting of the lower and upper limits.
	 */
  @Test public void setUpperAndLowerLimits() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertEquals(1.0, rj.getLowerLimit());
    TestCase.assertEquals(1.0, rj.getUpperLimit());
    rj.setLimits(0.0, 1.0);
    TestCase.assertEquals(0.0, rj.getLowerLimit(), 1e-6);
    TestCase.assertEquals(1.0, rj.getUpperLimit(), 1e-6);
    rj.setLimits(1.0, 2.0);
    TestCase.assertEquals(1.0, rj.getLowerLimit(), 1e-6);
    TestCase.assertEquals(2.0, rj.getUpperLimit(), 1e-6);
    rj.setLimits(1.0, 1.0);
    TestCase.assertEquals(1.0, rj.getLowerLimit());
    TestCase.assertEquals(1.0, rj.getUpperLimit());
  }

  /**
	 * Tests the failed setting of the lower and upper limits.
	 */
  @Test(expected = IllegalArgumentException.class) public void setUpperAndLowerLimitsInvalid() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertEquals(1.0, rj.getLowerLimit());
    TestCase.assertEquals(1.0, rj.getUpperLimit());
    rj.setLimits(1.0, 0.0);
  }

  /**
	 * Tests the failed setting of the lower and upper limits.
	 */
  @Test(expected = IllegalArgumentException.class) public void setUpperAndLowerLimitsNegative() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertEquals(1.0, rj.getLowerLimit());
    TestCase.assertEquals(1.0, rj.getUpperLimit());
    rj.setLimits(-1.0, 0.0);
  }

  /**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
  @Test public void setLimitsEnabledSleep() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    rj.setLimitsEnabled(false);
    TestCase.assertFalse(rj.isUpperLimitEnabled());
    TestCase.assertFalse(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimitsEnabled(false);
    TestCase.assertFalse(rj.isUpperLimitEnabled());
    TestCase.assertFalse(rj.isLowerLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    rj.setLimitsEnabled(true);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimitsEnabled(true);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    rj.setLimitsEnabled(false);
    TestCase.assertFalse(rj.isUpperLimitEnabled());
    TestCase.assertFalse(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
  }

  /**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
  @Test public void setLimitsSameSleep() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    double defaultLowerLimit = rj.getLowerLimit();
    double defaultUpperLimit = rj.getUpperLimit();
    TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setLimits(2.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(2.0, rj.getLowerLimit());
    TestCase.assertEquals(2.0, rj.getUpperLimit());
    rj.setLowerLimit(0.0);
    TestCase.assertEquals(0.0, rj.getLowerLimit());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(2.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(2.0, rj.getLowerLimit());
    TestCase.assertEquals(2.0, rj.getUpperLimit());
    rj.setUpperLimit(3.0);
    TestCase.assertEquals(3.0, rj.getUpperLimit());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(2.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(2.0, rj.getLowerLimit());
    TestCase.assertEquals(2.0, rj.getUpperLimit());
    rj.setLimitsEnabled(false);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(1.0);
    TestCase.assertFalse(rj.isUpperLimitEnabled());
    TestCase.assertFalse(rj.isLowerLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(1.0, rj.getLowerLimit());
    TestCase.assertEquals(1.0, rj.getUpperLimit());
  }

  /**
	 * Tests the sleep interaction when changing the limits to different values.
	 */
  @Test public void setLimitsDifferentSleep() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    double defaultLowerLimit = rj.getLowerLimit();
    double defaultUpperLimit = rj.getUpperLimit();
    TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setLimits(2.0, 3.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(2.0, rj.getLowerLimit());
    TestCase.assertEquals(3.0, rj.getUpperLimit());
    rj.setLowerLimit(1.0);
    TestCase.assertEquals(1.0, rj.getLowerLimit());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(0.0, 3.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(0.0, rj.getLowerLimit());
    TestCase.assertEquals(3.0, rj.getUpperLimit());
    rj.setUpperLimit(2.0);
    TestCase.assertEquals(2.0, rj.getUpperLimit());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(0.0, 1.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(0.0, rj.getLowerLimit());
    TestCase.assertEquals(1.0, rj.getUpperLimit());
    rj.setLimitsEnabled(false);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimits(1.0, 2.0);
    TestCase.assertFalse(rj.isUpperLimitEnabled());
    TestCase.assertFalse(rj.isLowerLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(1.0, rj.getLowerLimit());
    TestCase.assertEquals(2.0, rj.getUpperLimit());
  }

  /**
	 * Tests the sleep interaction when changing the lower limit.
	 */
  @Test public void setLowerLimitSleep() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    double defaultLowerLimit = rj.getLowerLimit();
    double defaultUpperLimit = rj.getUpperLimit();
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLowerLimit(defaultLowerLimit);
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setLowerLimit(0.5);
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(0.5, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setLimitsEnabled(false);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLowerLimit(0.2);
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(0.2, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
  }

  /**
	 * Tests the sleep interaction when changing the upper limit.
	 */
  @Test public void setUpperLimitSleep() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    double defaultLowerLimit = rj.getLowerLimit();
    double defaultUpperLimit = rj.getUpperLimit();
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setUpperLimit(defaultUpperLimit);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setUpperLimit(2.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(2.0, rj.getUpperLimit());
    rj.setLimitsEnabled(false);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setUpperLimit(3.0);
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(3.0, rj.getUpperLimit());
  }

  /**
	 * Tests the sleep interaction when changing the limits and enabling them.
	 */
  @Test public void setLimitsEnabledSameSleep() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    double defaultLowerLimit = rj.getLowerLimit();
    double defaultUpperLimit = rj.getUpperLimit();
    TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setLimitsEnabled(2.0, 2.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(2.0, rj.getLowerLimit());
    TestCase.assertEquals(2.0, rj.getUpperLimit());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimitsEnabled(false);
    rj.setLimitsEnabled(1.0, 1.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(1.0, rj.getLowerLimit());
    TestCase.assertEquals(1.0, rj.getUpperLimit());
  }

  /**
	 * Tests the sleep interaction when changing the limits to different values and enabling them.
	 */
  @Test public void setLimitsEnabledDifferentSleep() {
    RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    double defaultLowerLimit = rj.getLowerLimit();
    double defaultUpperLimit = rj.getUpperLimit();
    TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertTrue(b1.isAtRest());
    TestCase.assertTrue(b2.isAtRest());
    TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
    TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
    rj.setLimitsEnabled(0.0, 2.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(0.0, rj.getLowerLimit());
    TestCase.assertEquals(2.0, rj.getUpperLimit());
    rj.setLowerLimit(0.5);
    TestCase.assertEquals(0.5, rj.getLowerLimit());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimitsEnabled(0.0, 2.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(0.0, rj.getLowerLimit());
    TestCase.assertEquals(2.0, rj.getUpperLimit());
    rj.setUpperLimit(3.0);
    TestCase.assertEquals(3.0, rj.getUpperLimit());
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimitsEnabled(0.0, 2.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(0.0, rj.getLowerLimit());
    TestCase.assertEquals(2.0, rj.getUpperLimit());
    rj.setLimitsEnabled(false);
    b1.setAtRest(true);
    b2.setAtRest(true);
    rj.setLimitsEnabled(0.5, 4.0);
    TestCase.assertTrue(rj.isUpperLimitEnabled());
    TestCase.assertTrue(rj.isLowerLimitEnabled());
    TestCase.assertFalse(b1.isAtRest());
    TestCase.assertFalse(b2.isAtRest());
    TestCase.assertEquals(0.5, rj.getLowerLimit());
    TestCase.assertEquals(4.0, rj.getUpperLimit());
  }
}