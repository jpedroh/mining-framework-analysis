package org.cts.op;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
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
public class Geographic2Geocentric extends AbstractCoordinateOperation {
  private static final Identifier opId = new Identifier("EPSG", "9602", "Geographic to geocentric conversion", "Geographic to geocentric");

  private Ellipsoid ellipsoid;

  private double epsilon;

  /**
	 * <p>Create a new Geographic2Geocentric transformation for a specific
	 * ellipsoid. The reference datum for both geographic and geocentric
	 * coordinates is the same.</p>
	 * @param ellipsoid the ellipsoid used to define geographic coordinates
	 */
  public Geographic2Geocentric(Ellipsoid ellipsoid) {
    super(opId);
    this.ellipsoid = ellipsoid;
    this.precision = 1E-4;
    this.epsilon = 1E-11;
  }

  /**
	 * <p>Create a new Geographic2Geocentric transformation for a specific
	 * ellipsoid. The reference datum for both geographic and geocentric
	 * coordinates is the same.</p>
	 * @param ellipsoid the ellipsoid used to define geographic coordinates
	 * @param epsilon stop condition for the Geocentric to Geographic
	 * transformation algorithm (epsilon is a value in radian, 1E-11
	 * is the default epsilon and it means that error is less than 1E-4 m)
	 */
  public Geographic2Geocentric(Ellipsoid ellipsoid, double epsilon) {
    super(opId);
    this.ellipsoid = ellipsoid;
    this.precision = 1E-4;
    this.epsilon = epsilon;
  }

  /**
	 * Return coordinates representing the same point in a standard
	 * geocentric coordinate system.<p>
	 * @param coord is an array containing 2 or 3 double representing geographic
	 * coordinates in the following order : latitude (radians), longitude
	 * (radians from Greenwich) and optionnaly ellipsoidal height (if coord
	 * contains only 2 double's, height is set to 0).
	 * @throws IllegalCoordinateException if <code>coord</code> is not
	 * compatible with this <code>CoordinateOperation</code>.
	 */
  @Override public double[] transform(double[] coord) throws IllegalCoordinateException {
    if (coord.length < 2 || coord.length > 3) {
      throw new CoordinateDimensionException(coord, 3);
    } else {
      if (coord.length == 2) {
        coord = new double[] { coord[0], coord[1], 0.0 };
      }
    }
    double lat = coord[0];
    double lon = coord[1];
    double height = 0.0;
    if (coord.length == 3 && !Double.isNaN(coord[2])) {
      height = coord[2];
    }
    double N = ellipsoid.transverseRadiusOfCurvature(lat);
    coord[0] = (N + height) * cos(lat) * cos(lon);
    coord[1] = (N + height) * cos(lat) * sin(lon);
    coord[2] = (N * (1 - ellipsoid.getSquareEccentricity()) + height) * sin(lat);
    return coord;
  }

  /**
	 * Creates the inverse CoordinateOperation.
	 */
  @Override public CoordinateOperation inverse() throws NonInvertibleOperationException {
    return new Geocentric2Geographic(ellipsoid, epsilon);
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
    if (o instanceof Geographic2Geocentric) {
      Geographic2Geocentric gg2gc = (Geographic2Geocentric) o;
      return getEllipsoid().equals(gg2gc.getEllipsoid());
    }
    return false;
  }

  @Override public int hashCode() {
    int hash = 5;
    hash = 19 * hash + (this.ellipsoid != null ? this.ellipsoid.hashCode() : 0);
    return hash;
  }
}