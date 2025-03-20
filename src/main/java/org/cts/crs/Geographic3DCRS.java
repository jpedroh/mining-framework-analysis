package org.cts.crs;
import java.util.ArrayList;
import java.util.List;
import org.cts.Identifier;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.CoordinateSwitch;
import org.cts.op.UnitConversion;
import org.cts.op.projection.Projection;
import org.cts.units.Unit;
import static org.cts.cs.Axis.*;
import static org.cts.units.Unit.*;

/**
 * <p> A Geographic CoordinateReferenceSystem is a reference system based on a
 * GeodeticDatum and a 2D or 3D Ellipsoidal Coordinate System. </p> <p>
 *
 * @author Michaël Michaud
 */
public class Geographic3DCRS extends GeodeticCRS {
  /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains latitude,
     * second {@link Axis} contains longitude and third axis contains
     * ellipsoidal height. The units used by these axes are radian and meter.
     */
  public static CoordinateSystem LATLONH_RRM_CS = new CoordinateSystem(new Axis[] { LATITUDE, LONGITUDE, HEIGHT }, new Unit[] { RADIAN, RADIAN, METER });

  /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains longitude,
     * second {@link Axis} contains latitude and third axis contains
     * ellipsoidal height. The units used by these axes are radian and meter.
     */
  public static CoordinateSystem LONLATH_RRM_CS = new CoordinateSystem(new Axis[] { LONGITUDE, LATITUDE, HEIGHT }, new Unit[] { RADIAN, RADIAN, METER });

  /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains latitude,
     * second {@link Axis} contains longitude and third axis contains
     * ellipsoidal height. The units used by these axes are decimal degree and
     * meter.
     */
  public static CoordinateSystem LATLONH_DDM_CS = new CoordinateSystem(new Axis[] { LATITUDE, LONGITUDE, HEIGHT }, new Unit[] { DEGREE, DEGREE, METER });

  /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains
     * longitude, second {@link Axis} contains latitude and third axis contains
     * ellipsoidal height. The units used by these axes are decimal degree and
     * meter.
     */
  public static CoordinateSystem LONLATH_DDM_CS = new CoordinateSystem(new Axis[] { LONGITUDE, LATITUDE, HEIGHT }, new Unit[] { DEGREE, DEGREE, METER });

  /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains latitude,
     * second {@link Axis} contains longitude and third axis contains
     * ellipsoidal height. The units used by these axes are grad and meter.
     */
  public static CoordinateSystem LATLONH_GGM_CS = new CoordinateSystem(new Axis[] { LATITUDE, LONGITUDE, HEIGHT }, new Unit[] { GRAD, GRAD, METER });

  /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains longitude,
     * second {@link Axis} contains latitude and third axis contains
     * ellipsoidal height. The units used by these axes are grad and meter.
     */
  public static CoordinateSystem LONLATH_GGM_CS = new CoordinateSystem(new Axis[] { LONGITUDE, LATITUDE, HEIGHT }, new Unit[] { GRAD, GRAD, METER });

  /**
     * Create a new Geographic3DCRS.
     *
     * @param identifier the identifier of the Geographic3DCRS
     * @param datum the datum associated with the Geographic3DCRS
     * @param coordSys the coordinate system associated with the Geographic3DCRS
     */
  public Geographic3DCRS(Identifier identifier, GeodeticDatum datum, CoordinateSystem coordSys) {
    super(identifier, datum, coordSys);
  }

  /**
     * Create a new Geographic2DCRS. The first {@link Axis} of the associated
     * {@link CoordinateSystem} contains latitude, the second {@link Axis}
     * contains longitude and the third contains the ellipsoidal height in
     * meters.
     *
     * @param identifier the identifier of the Geographic3DCRS
     * @param datum the datum associated with the Geographic3DCRS
     * @param unit the angular unit to use for the two first axis of the
     * coordinate system associated with the Geographic3DCRS
     */
  public Geographic3DCRS(Identifier identifier, GeodeticDatum datum, Unit unit) {
    super(identifier, datum, LATLONH_DDM_CS);
    if (unit == RADIAN) {
      this.coordinateSystem = LATLONH_RRM_CS;
    } else {
      if (unit == DEGREE) {
        this.coordinateSystem = LATLONH_DDM_CS;
      } else {
        if (unit == GRAD) {
          this.coordinateSystem = LATLONH_GGM_CS;
        } else {
          ;
        }
      }
    }
  }

  /**
     * Create a new Geographic3DCRS. The first {@link Axis} of the associated
     * {@link CoordinateSystem} contains latitude, the second {@link Axis}
     * contains longitude and the third contains the ellipsoidal height in
     * meters.
     *
     * @param identifier the identifier of the Geographic2DCRS
     * @param datum the datum associated with the Geographic2DCRS
     */
  public Geographic3DCRS(Identifier identifier, GeodeticDatum datum) {
    super(identifier, datum, LATLONH_DDM_CS);
  }

  /**
     * @see GeodeticCRS#getType()
     */
  @Override public Type getType() {
    return Type.GEOGRAPHIC3D;
  }

  /**
     * @see GeodeticCRS#getProjection()
     */
  @Override public Projection getProjection() {
    return null;
  }

  /**
     * @see GeodeticCRS#toGeographicCoordinateConverter()
     */
  @Override public CoordinateOperation toGeographicCoordinateConverter() {
    List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
    ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), Unit.RADIAN, getCoordinateSystem().getUnit(2), Unit.METER));
    if (getCoordinateSystem().getAxis(0) == Axis.LONGITUDE) {
      ops.add(CoordinateSwitch.SWITCH_LAT_LON);
    }
    return new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class), ops);
  }

  /**
     * @see GeodeticCRS#fromGeographicCoordinateConverter()
     */
  @Override public CoordinateOperation fromGeographicCoordinateConverter() {
    List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
    if (getCoordinateSystem().getAxis(0) == Axis.LONGITUDE) {
      ops.add(CoordinateSwitch.SWITCH_LAT_LON);
    }
    ops.add(UnitConversion.createUnitConverter(Unit.RADIAN, getCoordinateSystem().getUnit(0), Unit.METER, getCoordinateSystem().getUnit(2)));
    return new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class), ops);
  }
}