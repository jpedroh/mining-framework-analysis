package org.dyn4j.dynamics.joint;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;
import junit.framework.TestCase;

/**
 * Used to test the {@link FrictionJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.2
 */
public class FrictionJointTest extends AbstractJointTest {
  /**
	 * Tests the successful creation case.
	 */
  @Test public void createWithTwoDifferentBodies() {
    new FrictionJoint<Body>(b1, b2, new Vector2());
  }

  /**
	 * Tests the failed creation with a null body1.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullBody1() {
    new FrictionJoint<Body>(null, b2, new Vector2());
  }

  /**
	 * Tests the failed creation with a null body2.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullBody2() {
    new FrictionJoint<Body>(b1, null, new Vector2());
  }

  /**
	 * Tests the create method passing a null anchor.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullAnchor() {
    new FrictionJoint<Body>(b1, b2, null);
  }

  /**
	 * Tests the create method passing the same body.
	 */
  @Test(expected = IllegalArgumentException.class) public void createWithSameBody() {
    new FrictionJoint<Body>(b1, b1, new Vector2());
  }

  /**
	 * Tests valid maximum torque values.
	 */
  @Test public void setMaximumTorque() {
    FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
    fj.setMaximumTorque(0.0);
    TestCase.assertEquals(0.0, fj.getMaximumTorque());
    fj.setMaximumTorque(10.0);
    TestCase.assertEquals(10.0, fj.getMaximumTorque());
    fj.setMaximumTorque(2548.0);
    TestCase.assertEquals(2548.0, fj.getMaximumTorque());
  }

  /**
	 * Tests a negative maximum torque value.
	 */
  @Test(expected = IllegalArgumentException.class) public void setNegativeMaximumTorque() {
    FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
    fj.setMaximumTorque(-2.0);
  }

  /**
	 * Tests valid maximum force values.
	 */
  @Test public void setMaximumForce() {
    FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
    fj.setMaximumForce(0.0);
    TestCase.assertEquals(0.0, fj.getMaximumForce());
    fj.setMaximumForce(10.0);
    TestCase.assertEquals(10.0, fj.getMaximumForce());
    fj.setMaximumForce(2548.0);
    TestCase.assertEquals(2548.0, fj.getMaximumForce());
  }

  /**
	 * Tests a negative maximum force value.
	 */
  @Test(expected = IllegalArgumentException.class) public void setNegativeMaximumForce() {
    FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
    fj.setMaximumForce(-2.0);
  }
}