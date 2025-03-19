package org.dyn4j.dynamics.joint;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;
import junit.framework.TestCase;

/**
 * Used to test the {@link WeldJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.2
 */
public class WeldJointTest extends AbstractJointTest {
  /**
	 * Tests the successful creation case.
	 */
  @Test public void createSuccess() {
    new WeldJoint<Body>(b1, b2, new Vector2());
  }

  /**
	 * Tests the create method passing a null body1.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullBody1() {
    new WeldJoint<Body>(null, b2, new Vector2());
  }

  /**
	 * Tests the create method passing a null body2.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullBody2() {
    new WeldJoint<Body>(b1, null, new Vector2());
  }

  /**
	 * Tests the create method passing a null anchor.
	 */
  @Test(expected = NullPointerException.class) public void createWithNullAnchor() {
    new WeldJoint<Body>(b1, b2, null);
  }

  /**
	 * Tests the create method passing the same body.
	 */
  @Test(expected = IllegalArgumentException.class) public void createWithSameBody() {
    new WeldJoint<Body>(b1, b1, new Vector2());
  }

  /**
	 * Tests the isSpring method.
	 * @since 3.0.2
	 */
  @Test public void isSpring() {
    WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
    TestCase.assertFalse(wj.isSpringEnabled());
    wj.setFrequency(0.0);
    TestCase.assertFalse(wj.isSpringEnabled());
    wj.setFrequency(1.0);
    TestCase.assertTrue(wj.isSpringEnabled());
    wj.setFrequency(15.24);
    TestCase.assertTrue(wj.isSpringEnabled());
    wj.setFrequency(0.0);
    TestCase.assertFalse(wj.isSpringEnabled());
  }

  /**
	 * Tests the isSpringDamper method.
	 * @since 3.0.2
	 */
  @Test public void isSpringDamper() {
    WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
    TestCase.assertFalse(wj.isSpringDamperEnabled());
    wj.setFrequency(0.0);
    TestCase.assertFalse(wj.isSpringDamperEnabled());
    wj.setFrequency(1.0);
    TestCase.assertFalse(wj.isSpringDamperEnabled());
    wj.setFrequency(15.24);
    TestCase.assertFalse(wj.isSpringDamperEnabled());
    wj.setDampingRatio(0.4);
    TestCase.assertTrue(wj.isSpringDamperEnabled());
    wj.setDampingRatio(0.0);
    TestCase.assertFalse(wj.isSpringDamperEnabled());
    wj.setDampingRatio(0.61);
    wj.setFrequency(0.0);
    TestCase.assertFalse(wj.isSpringDamperEnabled());
  }

  /**
	 * Tests valid damping ratio values.
	 * @since 3.0.2
	 */
  @Test public void setDampingRatio() {
    WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
    wj.setDampingRatio(0.0);
    TestCase.assertEquals(0.0, wj.getDampingRatio());
    wj.setDampingRatio(1.0);
    TestCase.assertEquals(1.0, wj.getDampingRatio());
    wj.setDampingRatio(0.2);
    TestCase.assertEquals(0.2, wj.getDampingRatio());
  }

  /**
	 * Tests a negative damping ratio value.
	 * @since 3.0.2
	 */
  @Test(expected = IllegalArgumentException.class) public void setDampingRatioNegative() {
    WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
    wj.setDampingRatio(-1.0);
  }

  /**
	 * Tests a greater than one damping ratio value.
	 * @since 3.0.2
	 */
  @Test(expected = IllegalArgumentException.class) public void setDampingRatioGreaterThan1() {
    WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
    wj.setDampingRatio(2.0);
  }

  /**
	 * Tests valid frequency values.
	 * @since 3.0.2
	 */
  @Test public void setFrequency() {
    WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
    wj.setFrequency(0.0);
    TestCase.assertEquals(0.0, wj.getFrequency());
    wj.setFrequency(1.0);
    TestCase.assertEquals(1.0, wj.getFrequency());
    wj.setFrequency(29.0);
    TestCase.assertEquals(29.0, wj.getFrequency());
  }

  /**
	 * Tests a negative frequency value.
	 * @since 3.0.2
	 */
  @Test(expected = IllegalArgumentException.class) public void setFrequencyNegative() {
    WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
    wj.setFrequency(-0.3);
  }
}