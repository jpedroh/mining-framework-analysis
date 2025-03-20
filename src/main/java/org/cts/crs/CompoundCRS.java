package org.cts.crs;
import org.cts.op.CoordinateOperation;
import org.cts.Identifier;
import org.cts.op.NonInvertibleOperationException;
import org.cts.units.Unit;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;

/**
 * A compound CoordinateReferenceSystem is a {@link org.cts.crs.CoordinateReferenceSystem}
 * composed by two distinct CoordinateReferenceSystem : a {@link org.cts.crs.GeodeticCRS}
 * for 2D horizontal coordinates and a {@link org.cts.crs.VerticalCRS} for the z
 * coordinate.
 *
 * @author Michaël Michaud
 */
public abstract class CompoundCRS extends GeodeticCRS {
  private GeodeticCRS horizontalCRS;

  private VerticalCRS verticalCRS;

  /**
     * Create a new GeodeticCRS.
     */
  protected CompoundCRS(Identifier identifier, GeodeticCRS horizontalCRS, VerticalCRS verticalCRS) {
    super(identifier, horizontalCRS.getDatum(), new CoordinateSystem(new Axis[] { horizontalCRS.getCoordinateSystem().getAxis(0), horizontalCRS.getCoordinateSystem().getAxis(1), verticalCRS.getCoordinateSystem().getAxis(0) }, new Unit[] { horizontalCRS.getCoordinateSystem().getUnit(0), horizontalCRS.getCoordinateSystem().getUnit(1), verticalCRS.getCoordinateSystem().getUnit(0) }));
    this.verticalCRS = verticalCRS;
  }

  /**
     * Return this CoordinateReferenceSystem Type
     */
  @Override public Type getType() {
    return CoordinateReferenceSystem.Type.COMPOUND;
  }

  /**
     * @return the horizonal part of this CoordinateReferenceSystem
     */
  public GeodeticCRS getHorizontalCRS() {
    return horizontalCRS;
  }

  /**
     * @return the vertical part of this CoordinateReferenceSystem
     */
  public VerticalCRS getVerticalCRS() {
    return verticalCRS;
  }

  /**
     * Returns the number of dimensions of the coordinate system.
     */
  @Override public int getDimension() {
    return 3;
  }

  /**
     * Creates a CoordinateOperation object to convert coordinates from this
     * CoordinateReferenceSystem to a GeographicReferenceSystem based on the
     * same horizonal datum and vertical datum, and using normal SI units in the
     * following order : latitude (rad), longitude (rad) height/altitude (m).
     */
  @Override public CoordinateOperation toGeographicCoordinateConverter() throws NonInvertibleOperationException {
    return null;
  }

  /**
     * Creates a CoordinateOperation object to convert coordinates from a
     * GeographicReferenceSystem based on the same horizonal datum and vertical
     * datum, and using normal SI units in the following order : latitude (rad),
     * longitude (rad) height/altitude (m) to this CoordinateReferenceSystem.
     */
  @Override public CoordinateOperation fromGeographicCoordinateConverter() throws NonInvertibleOperationException {
    return null;
  }

  /**
     * Return a String representation of this Datum.
     */
  @Override public String toString() {
    return "[" + getAuthorityName() + ":" + getAuthorityKey() + "] " + getName() + " (" + getShortName() + ")";
  }
}