package org.dyn4j.collision.narrowphase;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;
import junit.framework.TestCase;

/**
 * Test case for the {@link SegmentDetector} class
 * @author William Bittle
 * @version 4.1.0
 * @since 3.4.0
 */
public class SegmentDetectorTest {
  /**
	 * Tests the non-intersection case of raycasting a hoizontal segment.
	 */
  @Test public void raycastHorizontalSegmentNoIntersection() {
    Ray ray = new Ray(new Vector2(-0.85, 0.48), Math.PI * 0.25);
    Segment c = new Segment(new Vector2(-0.59, 0.68), new Vector2(-0.40, 0.68));
    Transform t = new Transform();
    Raycast raycast = new Raycast();
    boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
    TestCase.assertEquals(raycast.getNormal().x, 0.0);
    TestCase.assertEquals(raycast.getNormal().y, 0.0);
    TestCase.assertEquals(raycast.getPoint().x, 0.0);
    TestCase.assertEquals(raycast.getPoint().y, 0.0);
    TestCase.assertEquals(0.0, raycast.getDistance());
  }

  /**
	 * Tests the intersection case of raycasting a hoizontal segment.
	 */
  @Test public void raycastHorizontalSegmentWithIntersection() {
    Ray ray = new Ray(new Vector2(-0.85, 0.48), Math.PI * 0.25);
    Segment c = new Segment(new Vector2(-0.68, 0.68), new Vector2(-0.53, 0.68));
    Transform t = new Transform();
    Raycast raycast = new Raycast();
    boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertTrue(collision);
    Vector2 point = raycast.getPoint();
    Vector2 normal = raycast.getNormal();
    TestCase.assertEquals(-0.649, point.x, 1.0e-3);
    TestCase.assertEquals(0.680, point.y, 1.0e-3);
    TestCase.assertEquals(0.000, normal.x, 1.0e-3);
    TestCase.assertEquals(-1.000, normal.y, 1.0e-3);
    TestCase.assertEquals(0.282, raycast.getDistance(), 1.0e-3);
  }

  /**
	 * Tests the non-intersection case of raycasting a vertical segment.
	 */
  @Test public void raycastVerticalSegmentNoIntersection() {
    Ray ray = new Ray(new Vector2(-0.85, 0.48), Math.PI * 0.25);
    Segment c = new Segment(new Vector2(-0.58, 0.68), new Vector2(-0.58, 0.41));
    Transform t = new Transform();
    Raycast raycast = new Raycast();
    boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
    TestCase.assertEquals(raycast.getNormal().x, 0.0);
    TestCase.assertEquals(raycast.getNormal().y, 0.0);
    TestCase.assertEquals(raycast.getPoint().x, 0.0);
    TestCase.assertEquals(raycast.getPoint().y, 0.0);
    TestCase.assertEquals(0.0, raycast.getDistance());
  }

  /**
	 * Tests the intersection case of raycasting a vertical segment.
	 */
  @Test public void raycastVerticalSegmentWithIntersection() {
    Ray ray = new Ray(new Vector2(-0.85, 0.48), Math.PI * 0.25);
    Segment c = new Segment(new Vector2(-0.58, 1.2), new Vector2(-0.58, 0.41));
    Transform t = new Transform();
    Raycast raycast = new Raycast();
    boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertTrue(collision);
    Vector2 point = raycast.getPoint();
    Vector2 normal = raycast.getNormal();
    TestCase.assertEquals(-0.58, point.x, 1.0e-3);
    TestCase.assertEquals(0.75, point.y, 1.0e-3);
    TestCase.assertEquals(-1.000, normal.x, 1.0e-3);
    TestCase.assertEquals(0.000, normal.y, 1.0e-3);
    TestCase.assertEquals(0.381, raycast.getDistance(), 1.0e-3);
  }

  /**
	 * Tests the non-intersection case where ray is in front of the segment.
	 */
  @Test public void raycastParallelSegmentBehindRay() {
    Ray ray = new Ray(new Vector2(3.0, 0.0), new Vector2(1.0, 0.0));
    Segment c = new Segment(new Vector2(0.0, 0.0), new Vector2(2.0, 0.0));
    Transform t = new Transform();
    Raycast raycast = new Raycast();
    boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
    ray = new Ray(new Vector2(-3.0, 0.0), new Vector2(-1.0, 0.0));
    c = new Segment(new Vector2(0.0, 0.0), new Vector2(-2.0, 0.0));
    collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
    c = new Segment(new Vector2(-2.0, 0.0), new Vector2(0.0, 0.0));
    collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
  }

  /**
	 * Tests the intersection case where ray is in front of (and parallel to) the segment.
	 */
  @Test public void raycastParallelSegmentInFrontOfRay() {
    Ray ray = new Ray(new Vector2(-1.0, 0.0), new Vector2(1.0, 0.0));
    Segment c = new Segment(new Vector2(0.0, 0.0), new Vector2(2.0, 0.0));
    Transform t = new Transform();
    Raycast raycast = new Raycast();
    boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertTrue(collision);
    Vector2 point = raycast.getPoint();
    Vector2 normal = raycast.getNormal();
    TestCase.assertEquals(0.0, point.x, 1.0e-3);
    TestCase.assertEquals(0.0, point.y, 1.0e-3);
    TestCase.assertEquals(-1.000, normal.x, 1.0e-3);
    TestCase.assertEquals(0.000, normal.y, 1.0e-3);
    TestCase.assertEquals(1.000, raycast.getDistance(), 1.0e-3);
    ray = new Ray(new Vector2(3.0, 0.0), new Vector2(-1.0, 0.0));
    c = new Segment(new Vector2(0.0, 0.0), new Vector2(2.0, 0.0));
    collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertTrue(collision);
    point = raycast.getPoint();
    normal = raycast.getNormal();
    TestCase.assertEquals(2.0, point.x, 1.0e-3);
    TestCase.assertEquals(0.0, point.y, 1.0e-3);
    TestCase.assertEquals(1.000, normal.x, 1.0e-3);
    TestCase.assertEquals(0.000, normal.y, 1.0e-3);
    TestCase.assertEquals(1.000, raycast.getDistance(), 1.0e-3);
    c = new Segment(new Vector2(2.0, 0.0), new Vector2(0.0, 0.0));
    collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertTrue(collision);
    point = raycast.getPoint();
    normal = raycast.getNormal();
    TestCase.assertEquals(2.0, point.x, 1.0e-3);
    TestCase.assertEquals(0.0, point.y, 1.0e-3);
    TestCase.assertEquals(1.000, normal.x, 1.0e-3);
    TestCase.assertEquals(0.000, normal.y, 1.0e-3);
    TestCase.assertEquals(1.000, raycast.getDistance(), 1.0e-3);
  }

  /**
	 * Tests the non-intersection case where ray is starting in the middle of the segment.
	 */
  @Test public void raycastWithRayStartingInsideParallelSegment() {
    Ray ray = new Ray(new Vector2(1.0, 0.0), new Vector2(1.0, 0.0));
    Segment c = new Segment(new Vector2(0.0, 0.0), new Vector2(2.0, 0.0));
    Transform t = new Transform();
    Raycast raycast = new Raycast();
    boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
    ray = new Ray(new Vector2(1.0, 0.0), new Vector2(-1.0, 0.0));
    collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
    ray = new Ray(new Vector2(-1.0, 0.0), new Vector2(-1.0, 0.0));
    c = new Segment(new Vector2(0.0, 0.0), new Vector2(-2.0, 0.0));
    collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
    ray = new Ray(new Vector2(-1.0, 0.0), new Vector2(1.0, 0.0));
    collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
    c = new Segment(new Vector2(-2.0, 0.0), new Vector2(0.0, 0.0));
    collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
    ray = new Ray(new Vector2(-1.0, 0.0), new Vector2(-1.0, 0.0));
    collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
  }

  /**
	 * Tests a degenerate line segment.
	 */
  @Test public void raycastDegenerateSegment() {
    Ray ray = new Ray(new Vector2(1.0, 0.0), new Vector2(1.0, 0.0));
    Segment c = new Segment(new Vector2(1.0, 0.0), new Vector2(0.0, 0.0));
    c.getPoint1().set(0.0, 0.0);
    Transform t = new Transform();
    Raycast raycast = new Raycast();
    boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
    TestCase.assertFalse(collision);
  }
}