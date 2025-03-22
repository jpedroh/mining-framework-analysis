package org.movsim.roadmappings;
import java.util.ArrayList;
import org.movsim.simulator.vehicles.Vehicle;


<<<<<<< /usr/src/app/output/movsim/movsim/633cf8a7804a537c3b908eff2ec21dc4213d9b77/core/src/main/java/org/movsim/roadmappings/RoadMapping.java/left.java
public interface RoadMapping {
  /**
     * 
     * @param roadPos
     * @param lateralOffset
     *            offset from center of road, used mainly for drawing roadlines and road edges
     * @return a PosTheta object giving position and direction
     */
  PosTheta map(double roadPos, double lateralOffset);

  /**
     * Map a longitudinal position on the road onto a position and direction in real space.
     * 
     * @param roadPos
     * @return posTheta giving position and direction in real space
     */
  PosTheta map(double roadPos);

  /**
     * Convenience function, returns the start position of the road.
     * 
     * @return start position of the road
     */
  PosTheta startPos();

  /**
     * Convenience function, returns the start position of the road for a given lateral offset.
     * 
     * @param lateralOffset
     * 
     * @return start position of the road for given lateral offset
     */
  PosTheta startPos(double lateralOffset);

  /**
     * Convenience function, returns the end position of the road.
     * 
     * @return end position of the road
     */
  PosTheta endPos();

  /**
     * Convenience function, returns the end position of the road for a given lateral offset.
     * 
     * @param lateralOffset
     * 
     * @return end position of the road for given lateral offset
     */
  PosTheta endPos(double lateralOffset);

  /**
     * Returns the length of the road.
     * 
     * @return road length, in meters
     */
  double roadLength();

  /**
     * Returns the width of the road.
     * 
     * @return road width, in meters
     */
  double roadWidth();

  /**
     * Sets the road color.
     * 
     * @param color
     */
  void setRoadColor(int color);

  /**
     * Returns the road color.
     * 
     * @return road color
     */
  int roadColor();

  /**
     * Returns the width of the lanes.
     * 
     * @return the width of the lanes, in meters
     */
  double laneWidth();

  /**
     * Returns the number of lanes.
     * 
     * @return number of lanes
     */
  int laneCount();

  /**
     * Returns the offset of the center of the lane.
     * 
     * @param lane
     * @return the offset of the center of the lane
     */
  double laneOffset(int lane);

  /**
     * Returns the offset of the inside edge of the lane.
     * 
     * @param lane
     * @return the offset of the inside edge of the lane
     */
  double laneInsideEdgeOffset(int lane);

  /**
     * Set a clipping region based on the road position and length. Simple implementation at the moment: only one
     * clipping region is supported.
     * 
     * @param pos
     *            position of the clipping region on the road
     * @param length
     *            length of the clipping region
     */
  void addClippingRegion(double pos, double length);

  /**
     * Returns an arraylist of the clipping polygons, or null if no clipping set.
     * 
     * @return arraylist of the clipping polygons, or null if no clipping set.
     */
  ArrayList<PolygonFloat> clippingPolygons();

  /**
     * Returns the outside clipping polygon.
     * 
     * @return the outside clipping polygon
     */
  PolygonFloat outsideClippingPolygon();

  PolygonFloat mapFloat(PosTheta posTheta, double length, double width);

  /**
     * Returns a polygon with its vertices at the corners of the subject vehicle.
     * 
     * @param vehicle
     * @param time
     *            current simulation time
     * @return polygon representing vehicle
     */
  PolygonFloat mapFloat(Vehicle vehicle, double time);

  boolean isPeer();

  class Polygon {
    /**
         * Number of points in the polygon.
         */
    public int pointCount;

    /**
         * Array of x-coordinates of the polygon.
         */
    public int xPoints[];

    /**
         * Array of y-coordinates of the polygon.
         */
    public int yPoints[];

    /**
         * Constructor, allocate arrays for polygon points.
         * 
         * @param pointCount
         *            number of points in the polygon.
         */
    Polygon(int pointCount) {
      this.pointCount = pointCount;
      xPoints = new int[pointCount];
      yPoints = new int[pointCount];
    }
  }

  public static class PolygonFloat {
    /**
         * Number of points in the polygon.
         */
    public int pointCount;

    /**
         * Array of x-coordinates of the polygon.
         */
    public float xPoints[];

    /**
         * Array of y-coordinates of the polygon.
         */
    public float yPoints[];

    /**
         * Constructor, allocate arrays for polygon points.
         * 
         * @param pointCount
         *            number of points in the polygon.
         */
    PolygonFloat(int pointCount) {
      this.pointCount = pointCount;
      xPoints = new float[pointCount];
      yPoints = new float[pointCount];
    }
  }

  public class PosTheta {
    /**
         * x-coordinate of point.
         */
    public double x;

    /**
         * y-coordinate of point.
         */
    public double y;

    /**
         * cosine of angle.
         */
    public double cosTheta;

    /**
         * sine of angle.
         */
    public double sinTheta;

    /**
         * Returns angle, in radians, measured counter-clockwise from x-axis.
         * 
         * @return angle, in radians, measured counter-clockwise from x-axis
         */
    public double theta() {
      return Math.atan2(sinTheta, cosTheta);
    }
  }
}
=======
/**
 * A RoadMapping maps a logical road position (given by a lane and a position on a road segment) onto a physical
 * position, that is an x,y coordinate (given in meters).
 */
public abstract class RoadMapping {
  /**
     * 
     * @param roadPos
     * @param lateralOffset
     *            offset from center of road, used mainly for drawing roadlines and road edges
     * @return a PosTheta object giving position and direction
     */
  public abstract RoadMapping.PosTheta map(double roadPos, double lateralOffset);

  public static class Polygon {
    /**
         * Number of points in the polygon.
         */
    public int pointCount;

    /**
         * Array of x-coordinates of the polygon.
         */
    public int xPoints[];

    /**
         * Array of y-coordinates of the polygon.
         */
    public int yPoints[];

    /**
         * Constructor, allocate arrays for polygon points.
         * 
         * @param pointCount
         *            number of points in the polygon.
         */
    public Polygon(int pointCount) {
      this.pointCount = pointCount;
      xPoints = new int[pointCount];
      yPoints = new int[pointCount];
    }
  }

  public static class PolygonFloat {
    /**
         * Number of points in the polygon.
         */
    public int pointCount;

    /**
         * Array of x-coordinates of the polygon.
         */
    public float xPoints[];

    /**
         * Array of y-coordinates of the polygon.
         */
    public float yPoints[];

    /**
         * Constructor, allocate arrays for polygon points.
         * 
         * @param pointCount
         *            number of points in the polygon.
         */
    public PolygonFloat(int pointCount) {
      this.pointCount = pointCount;
      xPoints = new float[pointCount];
      yPoints = new float[pointCount];
    }
  }

  public static class PosTheta {
    /**
         * x-coordinate of point.
         */
    public double x;

    /**
         * y-coordinate of point.
         */
    public double y;

    /**
         * cosine of angle.
         */
    public double cosTheta;

    /**
         * sine of angle.
         */
    public double sinTheta;

    /**
         * Returns angle, in radians, measured counter-clockwise from x-axis.
         * 
         * @return angle, in radians, measured counter-clockwise from x-axis
         */
    public double theta() {
      return Math.atan2(sinTheta, cosTheta);
    }
  }

  protected final int laneCount;

  protected double laneWidth;

  protected double roadWidth;

  private int trafficLaneMin;

  private int trafficLaneMax;

  protected double roadLength;

  protected int roadColor;

  protected static int defaultRoadColor = 8421505;

  protected final PosTheta posTheta = new PosTheta();

  protected double x0;

  protected double y0;

  protected static final int POINT_COUNT = 4;

  protected final PolygonFloat polygonFloat = new PolygonFloat(POINT_COUNT);

  protected ArrayList<PolygonFloat> clippingPolygons;

  protected PolygonFloat outsideClippingPolygon;

  public static final double DEFAULT_LANE_WIDTH = 2;

  /**
     * Constructor.
     * 
     * @param laneCount
     * @param x0
     * @param y0
     */
  protected RoadMapping(int laneCount, double laneWidth, double x0, double y0) {
    this.laneCount = laneCount;
    this.x0 = x0;
    this.y0 = y0;
    trafficLaneMin = Lanes.LANE1;
    trafficLaneMax = laneCount;
    this.laneWidth = laneWidth;
    roadWidth = laneWidth * laneCount;
    roadColor = defaultRoadColor;
  }

  /**
     * Constructor.
     * 
     * @param laneCount
     * @param x0
     * @param y0
     */
  protected RoadMapping(int laneCount, double x0, double y0) {
    this(laneCount, DEFAULT_LANE_WIDTH, x0, y0);
  }

  /**
     * Called when the system is running low on memory, and would like actively running process to try to tighten their
     * belts.
     */
  protected void onLowMemory() {
  }

  /**
     * Returns the default road color.
     * 
     * @return the default road color
     */
  public static int defaultRoadColor() {
    return RoadMapping.defaultRoadColor;
  }

  /**
     * Sets the default road color.
     * 
     * @param defaultRoadColor
     */
  public static void setDefaultRoadColor(int defaultRoadColor) {
    RoadMapping.defaultRoadColor = defaultRoadColor;
  }

  /**
     * Sets the minimum traffic lane. Lanes with <code>lane &lt; trafficLaneMin</code> are not traffic lanes and may be
     * treated differently, especially for lane changes.
     * 
     * @param trafficLaneMin
     */
  public final void setTrafficLaneMin(int trafficLaneMin) {
    assert trafficLaneMin >= Lanes.MOST_INNER_LANE;
    this.trafficLaneMin = trafficLaneMin;
  }

  /**
     * Returns the minimum traffic lane.
     * 
     * @return the minimum traffic lane
     */
  public final int trafficLaneMin() {
    return trafficLaneMin;
  }

  /**
     * Sets the maximum traffic lane. Lanes with <code>lane &gt; trafficLaneMax</code> are not traffic lanes and may be
     * treated differently, especially for lane changes.
     * 
     * @param trafficLaneMax
     */
  public final void setTrafficLaneMax(int trafficLaneMax) {
    this.trafficLaneMax = trafficLaneMax;
  }

  /**
     * Returns the maximum traffic lane.
     * 
     * @return the maximum traffic lane
     */
  public final int trafficLaneMax() {
    return trafficLaneMax;
  }

  /**
     * Convenience function, returns the start position of the road.
     * 
     * @return start position of the road
     */
  public RoadMapping.PosTheta startPos() {
    return map(0.0, 0.0);
  }

  /**
     * Convenience function, returns the start position of the road for a given lateral offset.
     * 
     * @param lateralOffset
     * 
     * @return start position of the road for given lateral offset
     */
  public RoadMapping.PosTheta startPos(double lateralOffset) {
    return map(0.0, lateralOffset);
  }

  /**
     * Convenience function, returns the end position of the road.
     * 
     * @return end position of the road
     */
  public RoadMapping.PosTheta endPos() {
    return map(roadLength, 0.0);
  }

  /**
     * Convenience function, returns the end position of the road for a given lateral offset.
     * 
     * @param lateralOffset
     * 
     * @return end position of the road for given lateral offset
     */
  public RoadMapping.PosTheta endPos(double lateralOffset) {
    return map(roadLength, lateralOffset);
  }

  /**
     * Returns the end position of the ramp lane.
     * 
     * @return end position of the ramp lane
     */
  public RoadMapping.PosTheta endPosRamp() {
    final double lateralOffset = laneOffset(laneCount);
    return map(roadLength, lateralOffset);
  }

  /**
     * Map a longitudinal position on the road onto a position and direction in real space.
     * 
     * @param roadPos
     * @return posTheta giving position and direction in real space
     */
  public RoadMapping.PosTheta map(double roadPos) {
    return map(roadPos, 0.0);
  }

  /**
     * Returns the length of the road.
     * 
     * @return road length, in meters
     */
  public final double roadLength() {
    return roadLength;
  }

  /**
     * Returns the width of the road.
     * 
     * @return road width, in meters
     */
  public final double roadWidth() {
    return roadWidth;
  }

  /**
     * Sets the road color.
     * 
     * @param color
     */
  public final void setRoadColor(int color) {
    this.roadColor = color;
  }

  /**
     * Returns the road color.
     * 
     * @return road color
     */
  public final int roadColor() {
    return roadColor;
  }

  /**
     * Returns the width of the lanes.
     * 
     * @return the width of the lanes, in meters
     */
  public final double laneWidth() {
    return laneWidth;
  }

  /**
     * Returns the number of lanes.
     * 
     * @return number of lanes
     */
  public final int laneCount() {
    return laneCount;
  }

  /**
     * Returns the offset of the center of the lane. Fractional lanes are supported to facilitate the drawing of
     * vehicles in the process of changing lanes.
     * 
     * @param lane
     * @return the offset of the center of the lane
     */
  private final double laneOffset(double lane) {
    return (lane == Lanes.NONE) ? 0.0 : (0.5 * (1 - laneCount) + (lane - 1)) * laneWidth;
  }

  /**
     * Returns the offset of the center of the lane.
     * 
     * @param lane
     * @return the offset of the center of the lane
     */
  public final double laneOffset(int lane) {
    return laneOffset((double) lane);
  }

  /**
     * Returns the offset of the inside edge of the lane.
     * 
     * @param lane
     * @return the offset of the inside edge of the lane
     */
  public final double laneInsideEdgeOffset(int lane) {
    return (0.5 * (1 - laneCount + 1) + (lane - 1)) * laneWidth;
  }

  /**
     * Set a clipping region based on the road position and length. Simple implementation at the moment: only one
     * clipping region is supported.
     * 
     * @param pos
     *            position of the clipping region on the road
     * @param length
     *            length of the clipping region
     */
  public void addClippingRegion(double pos, double length) {
    if (clippingPolygons == null) {
      clippingPolygons = new ArrayList<>();
    }
    if (outsideClippingPolygon == null) {
      final float LARGE_NUMBER = 100000.0f;
      outsideClippingPolygon = new PolygonFloat(POINT_COUNT);
      outsideClippingPolygon.xPoints[0] = -LARGE_NUMBER;
      outsideClippingPolygon.yPoints[0] = -LARGE_NUMBER;
      outsideClippingPolygon.xPoints[1] = LARGE_NUMBER;
      outsideClippingPolygon.yPoints[1] = -LARGE_NUMBER;
      outsideClippingPolygon.xPoints[2] = LARGE_NUMBER;
      outsideClippingPolygon.yPoints[2] = LARGE_NUMBER;
      outsideClippingPolygon.xPoints[3] = -LARGE_NUMBER;
      outsideClippingPolygon.yPoints[3] = LARGE_NUMBER;
    }
    final PolygonFloat clippingPolygon = new PolygonFloat(POINT_COUNT);
    final double offset = 1.5 * laneCount * laneWidth;
    PosTheta posTheta;
    posTheta = map(pos + length, -offset);
    clippingPolygon.xPoints[0] = (float) posTheta.x;
    clippingPolygon.yPoints[0] = (float) posTheta.y;
    posTheta = map(pos + length, offset);
    clippingPolygon.xPoints[1] = (float) posTheta.x;
    clippingPolygon.yPoints[1] = (float) posTheta.y;
    posTheta = map(pos, offset);
    clippingPolygon.xPoints[2] = (float) posTheta.x;
    clippingPolygon.yPoints[2] = (float) posTheta.y;
    posTheta = map(pos, -offset);
    clippingPolygon.xPoints[3] = (float) posTheta.x;
    clippingPolygon.yPoints[3] = (float) posTheta.y;
    clippingPolygons.add(clippingPolygon);
  }

  /**
     * Returns an arraylist of the clipping polygons, or null if no clipping set.
     * 
     * @return arraylist of the clipping polygons, or null if no clipping set.
     */
  public ArrayList<RoadMapping.PolygonFloat> clippingPolygons() {
    return clippingPolygons;
  }

  /**
     * Returns the outside clipping polygon.
     * 
     * @return the outside clipping polygon
     */
  public RoadMapping.PolygonFloat outsideClippingPolygon() {
    return outsideClippingPolygon;
  }

  public RoadMapping.PolygonFloat mapFloat(RoadMapping.PosTheta posTheta, double length, double width) {
    final double lca = length * posTheta.cosTheta;
    final double wsa = width * posTheta.sinTheta;
    final double xbr = posTheta.x - 0.5 * (lca - wsa);
    polygonFloat.xPoints[0] = (float) (xbr + lca);
    polygonFloat.xPoints[1] = (float) (xbr + lca - wsa);
    polygonFloat.xPoints[2] = (float) (xbr - wsa);
    polygonFloat.xPoints[3] = (float) xbr;
    final double lsa = length * posTheta.sinTheta;
    final double wca = width * posTheta.cosTheta;
    final double ybr = posTheta.y + 0.5 * (lsa + wca);
    polygonFloat.yPoints[0] = (float) (ybr - lsa);
    polygonFloat.yPoints[1] = (float) (ybr - wca - lsa);
    polygonFloat.yPoints[2] = (float) (ybr - wca);
    polygonFloat.yPoints[3] = (float) ybr;
    return polygonFloat;
  }

  /**
     * Returns a polygon with its vertices at the corners of the subject vehicle.
     * 
     * @param vehicle
     * @param time
     *            current simulation time
     * @return polygon representing vehicle
     */
  public RoadMapping.PolygonFloat mapFloat(Vehicle vehicle, double time) {
    final RoadMapping.PosTheta posTheta = map(vehicle.physicalQuantities().getMidPosition(), laneOffset(vehicle.getContinousLane()));
    return mapFloat(posTheta, vehicle.physicalQuantities().getLength(), vehicle.physicalQuantities().getWidth());
  }
}
>>>>>>> /usr/src/app/output/movsim/movsim/633cf8a7804a537c3b908eff2ec21dc4213d9b77/core/src/main/java/org/movsim/roadmappings/RoadMapping.java/right.java
