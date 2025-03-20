package org.cts.op;
import org.cts.CoordinateDimensionException;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.datum.PrimeMeridian;

/**
 * Longitude rotation is a simple transformation which shift the longitude
 * parameter of a geographic coordinate.
 *
 * @author Michaël Michaud
 */
public class LongitudeRotation extends AbstractCoordinateOperation {
  private static final Identifier opId = new Identifier("EPSG", "9601", "Longitude Rotation", "Rotation");

  private double rotationAngle;

  /**
     * <p>Create a new LongitudeRotation converter.</p>
     *
     * @param rotation rotation angle in radians
     */
  public LongitudeRotation(double rotation) {
    super(opId);
    this.rotationAngle = rotation;
    this.precision = 1E-9;
  }

  /**
     * <p>Return a coordinate representing the same point as coord but in a
     * geographic coordinate system based on a different prime meridian.</p>
     *
     * @param coord is an array containing 2 or 3 double representing geographic
     * coordinate in the following order : latitude (radians), longitude
     * (radians from Greenwich) and optionnaly ellipsoidal height.
     * @throws IllegalCoordinateException if
     * <code>coord</code> is not compatible with this
     * <code>CoordinateOperation</code>.
     */
  @Override public double[] transform(double[] coord) throws IllegalCoordinateException {
    if (coord == null || coord.length < 2) {
      throw new CoordinateDimensionException(coord, 2);
    }
    coord[1] = coord[1] + rotationAngle;
    return coord;
  }

  /**
     * Creates the inverse CoordinateOperation.
     */
  @Override public CoordinateOperation inverse() throws NonInvertibleOperationException {
    return new LongitudeRotation(-rotationAngle);
  }

  /**
     * Creates a new LongitudeRotation from Greenwich to this PrimeMeridian.
     *
     * @param targetPM target prime meridian
     */
  public static LongitudeRotation getLongitudeRotationTo(PrimeMeridian targetPM) {
    return new LongitudeRotation(-targetPM.getLongitudeFromGreenwichInRadians());
  }

  /**
     * Creates a new LongitudeRotation from this PrimeMeridian to Greenwich.
     *
     * @param targetPM target prime meridian
     */
  public static LongitudeRotation getLongitudeRotationFrom(PrimeMeridian targetPM) {
    return new LongitudeRotation(targetPM.getLongitudeFromGreenwichInRadians());
  }

  /**
     * Return a String representation of this Geographic/Geocentric converter.
     */
  @Override public String toString() {
    return getName() + " ( " + rotationAngle * 180 / Math.PI + "\u00b0 )";
  }

  public double getRotationAngle() {
    return rotationAngle;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof LongitudeRotation) {
      LongitudeRotation lr = (LongitudeRotation) o;
      return (getRotationAngle() == lr.getRotationAngle());
    }
    return false;
  }

  @Override public int hashCode() {
    int hash = 7;
    hash = 97 * hash + (int) (Double.doubleToLongBits(this.rotationAngle) ^ (Double.doubleToLongBits(this.rotationAngle) >>> 32));
    return hash;
  }
}