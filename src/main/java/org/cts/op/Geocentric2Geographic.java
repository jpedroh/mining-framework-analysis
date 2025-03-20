package org.cts.op;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import org.cts.CoordinateDimensionException;
import org.cts.datum.Ellipsoid;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;

/**
 * <p>Transform geographic coordinates (latitude, longitude, ellipsoidal height
 * into geocentric coordinates.</p>
 * <p>Geographic coordinates and geocentric coordinates are supposed to use
 * the same reference datum and to be standardized :</p>
 * <ul>
 * <li>Geographic coordinates are given in the following order : latitude
 * (radians), longitude (radians from Greenwich) and optionnaly ellipsoidal
 * height (default = 0.0).</li>
 * <li>The center of the geocentric system (center of the mass) is equal to
 * the geographic coordinates reference ellipsoid.</p>
 * <li>Z axis is oriented from origin to North Pole</li>
 * <li>Y axis is oriented from origin to intersection of equator and Greenwich
 * Meridian</li>
 * <li>OXYZ is direct</li>
 * <li>Units = radian, meter (to facilitate transformation operations).</li>
 * </ul>
 * @author Michaël Michaud
 */
public class Geocentric2Geographic extends AbstractCoordinateOperation {
  private static final Identifier opId = new Identifier("EPSG", "9602", "Geocentric to geographic conversion", "Geocentric to geographic");

  private Ellipsoid ellipsoid;

  private double epsilon;

  /**
     * <p>Create a new Geographic2Geocentric transformation for a specific
     * ellipsoid. The reference datum for both geographic and geocentric
     * coordinates is the same.</p>
     *
     * @param ellipsoid the ellipsoid used to define geographic coordinates
     */
  public Geocentric2Geographic(Ellipsoid ellipsoid) {
    super(opId);
    this.ellipsoid = ellipsoid;
    this.precision = 1E-4;
    this.epsilon = 1E-11;
  }

  /**
     * <p>Create a new Geographic2Geocentric transformation for a specific
     * ellipsoid. The reference datum for both geographic and geocentric
     * coordinates is the same.</p>
     *
     * @param ellipsoid the ellipsoid used to define geographic coordinates
     * @param epsilon stop condition for the Geocentric to Geographic
     * transformation algorithm (epsilon is a value in radian, 1E-11 is the
     * default epsilon and it means that error is less than 1E-4 m)
     */
  public Geocentric2Geographic(Ellipsoid ellipsoid, double epsilon) {
    super(opId);
    this.ellipsoid = ellipsoid;
    this.precision = 1E-4;
    this.epsilon = epsilon;
  }

  /**
     * <p>Return coordinates representing the same point in a standard
     * geocentric coordinate system.</p>
     *
     * @param coord is an array containing 2 or 3 double representing geographic
     * coordinates in the following order : latitude (radians), longitude
     * (radians from Greenwich) and optionnaly ellipsoidal height (if coord
     * contains only 2 double's, height is set to 0).
     * @throws IllegalCoordinateException if
     * <code>coord</code> is not compatible with this
     * <code>CoordinateOperation</code>.
     */
  @Override public double[] transform(double[] coord) throws IllegalCoordinateException {
    if (coord.length != 3) {
      throw new CoordinateDimensionException(coord, 3);
    }
    double X = coord[0];
    double Y = coord[1];
    double Z = coord[2];
    double a = ellipsoid.getSemiMajorAxis();
    double e2 = ellipsoid.getSquareEccentricity();
    double lon = atan2(Y, X);
    double XY2 = sqrt(X * X + Y * Y);
    double lat0 = atan(Z / (XY2 * (1 - (a * e2 / sqrt(X * X + Y * Y + Z * Z)))));
    double lati = lat0;
    double lati1 = 0;
    while (abs(lati1 - lati) > epsilon) {
      lati = lati1;
      double exp1 = a * e2 * cos(lati);
      double exp2 = sqrt(1 - (e2 * sin(lati) * sin(lati)));
      lati1 = atan((Z / XY2) / (1 - (exp1 / (XY2 * exp2))));
    }
    double lat = lati1;
    double height = XY2 / cos(lat) - a / sqrt(1 - (e2 * sin(lat) * sin(lat)));
    coord[0] = lat;
    coord[1] = lon;
    coord[2] = height;
    return coord;
  }

  /**
     * Creates the inverse CoordinateOperation.
     */
  @Override public CoordinateOperation inverse() throws NonInvertibleOperationException {
    return new Geographic2Geocentric(ellipsoid);
  }

  /**
     * Return a String representation of this Geographic/Geocentric converter.
     */
  @Override public String toString() {
    return getName() + " (" + ellipsoid.getName() + ")";
  }

  public Ellipsoid getEllipsoid() {
    return ellipsoid;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Geocentric2Geographic) {
      Geocentric2Geographic gc2gg = (Geocentric2Geographic) o;
      return getEllipsoid().equals(gc2gg.getEllipsoid());
    }
    return false;
  }

  @Override public int hashCode() {
    int hash = 5;
    hash = 89 * hash + (this.ellipsoid != null ? this.ellipsoid.hashCode() : 0);
    return hash;
  }
}