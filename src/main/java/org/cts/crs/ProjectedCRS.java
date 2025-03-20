package org.cts.crs;
import org.cts.op.CoordinateOperation;
import java.util.ArrayList;
import org.cts.Identifier;
import java.util.List;
import org.cts.op.NonInvertibleOperationException;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.op.ChangeCoordinateDimension;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.CoordinateSwitch;
import org.cts.op.UnitConversion;
import org.cts.op.projection.Projection;
import org.cts.units.Unit;
import static org.cts.cs.Axis.EASTING;
import static org.cts.cs.Axis.NORTHING;
import static org.cts.units.Unit.METER;

/**
 * A Projected {@link org.cts.crs.CoordinateReferenceSystem} is a CoordinateReferenceSystem
 * based on a GeodeticDatum and a Projection operation.
 *
 * @author Michaël Michaud
 */
public class ProjectedCRS extends GeodeticCRS {
  public static CoordinateSystem EN_CS = new CoordinateSystem(new Axis[] { EASTING, NORTHING }, new Unit[] { METER, METER });

  public static CoordinateSystem NE_CS = new CoordinateSystem(new Axis[] { NORTHING, EASTING }, new Unit[] { METER, METER });

  private Projection projection;

  public ProjectedCRS(Identifier identifier, GeodeticDatum datum, CoordinateSystem coordSys, Projection projection) {
    super(identifier, datum, coordSys);
    this.projection = projection;
  }

  public ProjectedCRS(Identifier identifier, GeodeticDatum datum, Projection projection, Unit unit) {
    super(identifier, datum, new CoordinateSystem(new Axis[] { EASTING, NORTHING }, new Unit[] { unit, unit }));
    this.projection = projection;
  }

  public ProjectedCRS(Identifier identifier, GeodeticDatum datum, Projection projection) {
    super(identifier, datum, EN_CS);
    this.projection = projection;
  }

  @Override public Projection getProjection() {
    return projection;
  }

  /**
     * Return this CoordinateReferenceSystem Type
     */
  @Override public Type getType() {
    return Type.PROJECTED;
  }

  /**
     * @see GeodeticCRS#toGeographicCoordinateConverter()
     */
  @Override public CoordinateOperation toGeographicCoordinateConverter() throws NonInvertibleOperationException {
    List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
    if (getCoordinateSystem().getUnit(0) != Unit.METER) {
      ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), METER));
    }
    ops.add(ChangeCoordinateDimension.TO3D);
    if (getCoordinateSystem().getAxis(0) != EASTING) {
      ops.add(CoordinateSwitch.SWITCH_LAT_LON);
    }
    ops.add(projection.inverse());
    return new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class), ops);
  }

  /**
     * @see GeodeticCRS#fromGeographicCoordinateConverter()
     */
  @Override public CoordinateOperation fromGeographicCoordinateConverter() {
    List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
    ops.add(ChangeCoordinateDimension.TO2D);
    ops.add(projection);
    if (getCoordinateSystem().getAxis(0) != EASTING) {
      ops.add(CoordinateSwitch.SWITCH_LAT_LON);
    }
    if (getCoordinateSystem().getUnit(0) != Unit.METER) {
      ops.add(UnitConversion.createUnitConverter(Unit.METER, getCoordinateSystem().getUnit(0)));
    }
    return new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class), ops);
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof 
<<<<<<< /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/ProjectedCRS.java/left.java
    GeodeticCRS
=======
    ProjectedCRS
>>>>>>> /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/ProjectedCRS.java/right.java
    ) {

<<<<<<< /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/ProjectedCRS.java/left.java
      GeodeticCRS
=======
      ProjectedCRS
>>>>>>> /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/ProjectedCRS.java/right.java
       crs = (
<<<<<<< /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/ProjectedCRS.java/left.java
      GeodeticCRS
=======
      ProjectedCRS
>>>>>>> /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/ProjectedCRS.java/right.java
      ) o;
      System.out.println(this);
      System.out.println(crs);
      if (!getType().equals(crs.getType())) {
        return false;
      }
      if (getIdentifier().equals(crs.getIdentifier())) {
        return true;
      }
      boolean nadgrids;
      if (getGridTransformations() == null) {
        if (crs.getGridTransformations() == null) {
          nadgrids = true;
        } else {
          nadgrids = false;
        }
      } else {
        nadgrids = getGridTransformations().equals(crs.getGridTransformations());
      }
      boolean crstransf;
      if (getCRSTransformations() == null) {
        if (crs.getCRSTransformations() == null) {
          crstransf = true;
        } else {
          crstransf = false;
        }
      } else {
        crstransf = getCRSTransformations().equals(crs.getGridTransformations());
      }
      return 
<<<<<<< /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/ProjectedCRS.java/left.java
      getDatum().equals(crs.getDatum()) && getProjection().equals(crs.getProjection()) && getCoordinateSystem().equals(crs.getCoordinateSystem()) && nadgrids && crstransf && getProjection().equals(crs.getProjection())
=======
      getDatum().equals(crs.getDatum()) && getProjection().equals(crs.getProjection()) && getCoordinateSystem().equals(crs.getCoordinateSystem()) && nadgrids
>>>>>>> /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/ProjectedCRS.java/right.java
      ;
    } else {
      return false;
    }
  }

  @Override public int hashCode() {
    int hash = 3;
    hash = 
<<<<<<< /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/ProjectedCRS.java/left.java
    59
=======
    97
>>>>>>> /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/ProjectedCRS.java/right.java
     * hash + (this.projection != null ? this.projection.hashCode() : 0);
    return hash;
  }
}