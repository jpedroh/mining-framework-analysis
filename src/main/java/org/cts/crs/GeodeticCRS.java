package org.cts.crs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cts.op.CoordinateOperation;
import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.op.NonInvertibleOperationException;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.op.projection.Projection;

/**
 * A geodetic {@link org.cts.crs.CoordinateReferenceSystem} is a coordinate
 * system based on a {@link org.cts.datum.GeodeticDatum}, a
 * {@link org.cts.datum.PrimeMeridian} and an {@link org.cts.Ellipsoid}.
 * It is an abstract class including Geographic3D, Geographic2D and Projected
 * CoordinateReferenceSystems.
 *
 * @author Michaël Michaud
 */
public abstract class GeodeticCRS extends IdentifiableComponent implements CoordinateReferenceSystem {
  private GeodeticDatum geodeticDatum;

  private Map<GeodeticDatum, List<CoordinateOperation>> nadgridsTransformations = new HashMap<GeodeticDatum, List<CoordinateOperation>>();

  private Map<CoordinateReferenceSystem, List<CoordinateOperation>> crsTransformation = new HashMap<CoordinateReferenceSystem, List<CoordinateOperation>>();

  @Override public Projection getProjection() {
    return null;
  }

  protected CoordinateSystem coordinateSystem;

  /**
     * Create a new GeodeticCRS.
     */
  protected GeodeticCRS(Identifier identifier, GeodeticDatum datum, CoordinateSystem coordinateSystem) {
    super(identifier);
    this.geodeticDatum = datum;
    this.coordinateSystem = coordinateSystem;
  }

  /**
     * Return this CoordinateReferenceSystem Type
     */
  @Override abstract public Type getType();

  /**
     * Returns the coordinate system of this CoordinateReferenceSystem.
     */
  @Override public CoordinateSystem getCoordinateSystem() {
    return coordinateSystem;
  }

  /**
     * Returns the number of dimensions of the coordinate system.
     */
  public int getDimension() {
    return coordinateSystem.getDimension();
  }

  /**
     * Return the {@link org.cts.datum.GeodeticDatum}.
     */
  @Override public GeodeticDatum getDatum() {
    return geodeticDatum;
  }

  /**
     * Return whether this coord is a valid coord in this
     * CoordinateReferenceSystem.
     *
     * @param coord standard coordinate for this CoordinateReferenceSystem
     * datums (ex. decimal degrees for geographic datums and meters for vertical
     * datums).
     */
  public boolean isValid(double[] coord) {
    return geodeticDatum.getExtent().isInside(coord);
  }

  /**
     * Add a Nadgrids Transformation for this CRS to the CRS using the key datum.
     */
  public void addGridTransformation(GeodeticDatum gd, CoordinateOperation coordOp) {
    if (nadgridsTransformations.get(gd) == null) {
      nadgridsTransformations.put(gd, new ArrayList<CoordinateOperation>());
    }
    nadgridsTransformations.get(gd).add(coordOp);
  }

  /**
     * Return the list of nadgrids transformation defined for this CRS.
     */
  public Map<GeodeticDatum, 
<<<<<<< /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/GeodeticCRS.java/left.java
  List<CoordinateOperation>
=======
  CoordinateOperation
>>>>>>> /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/GeodeticCRS.java/right.java
  > getGridTransformations() {
    return 
<<<<<<< /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/GeodeticCRS.java/left.java
    nadgridsTransformations
=======
    nadgridsTransformation
>>>>>>> /usr/src/app/output/irstv/cts/e5ac2fb4e8ef1b4418f9d55477b664e02193101f/src/main/java/org/cts/crs/GeodeticCRS.java/right.java
    ;
  }

  /**
     * Return the list of nadgrids transformation defined for this CRS that used the datum in parameter as target datum.
     */
  public List<CoordinateOperation> getGridTransformations(GeodeticDatum datum) {
    return nadgridsTransformations.get(datum);
  }

  /**
     * Add a transformation for this CRS to the CRS in parameter.
     */
  public void addCRSTransformation(CoordinateReferenceSystem crs, List<CoordinateOperation> opList) {
    crsTransformation.put(crs, opList);
  }

  /**
     * Return the list of nadgrids transformation defined for this CRS.
     */
  public Map<CoordinateReferenceSystem, List<CoordinateOperation>> getCRSTransformations() {
    return crsTransformation;
  }

  /**
     * Return the list of transformation defined for this CRS to the CRS in parameter.
     */
  public List<CoordinateOperation> getCRSTransformations(CoordinateReferenceSystem crs) {
    return crsTransformation.get(crs);
  }

  /**
     * Creates a CoordinateOperation object to convert coordinates from this
     * CoordinateReferenceSystem to a GeographicReferenceSystem based on the
     * same horizonal datum and vertical datum, and using normal SI units in the
     * following order : latitude (rad), longitude (rad) height/altitude (m).
     */
  abstract public CoordinateOperation toGeographicCoordinateConverter() throws NonInvertibleOperationException;

  /**
     * Creates a CoordinateOperation object to convert coordinates from a
     * GeographicReferenceSystem based on the same horizonal datum and vertical
     * datum, and using normal SI units in the following order : latitude (rad),
     * longitude (rad) height/altitude (m) to this CoordinateReferenceSystem.
     */
  abstract public CoordinateOperation fromGeographicCoordinateConverter() throws NonInvertibleOperationException;

  /**
     * Return a String representation of this Datum.
     */
  @Override public String toString() {
    return "[" + getAuthorityName() + ":" + getAuthorityKey() + "] " + getName();
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof GeodeticCRS) {
      GeodeticCRS crs = (GeodeticCRS) o;
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
      return getDatum().equals(crs.getDatum()) && getProjection().equals(crs.getProjection()) && getCoordinateSystem().equals(crs.getCoordinateSystem()) && nadgrids && crstransf;
    } else {
      return false;
    }
  }

  @Override public int hashCode() {
    int hash = 7;
    hash = 29 * hash + (this.geodeticDatum != null ? this.geodeticDatum.hashCode() : 0);
    hash = 29 * hash + (this.coordinateSystem != null ? this.coordinateSystem.hashCode() : 0);
    return hash;
  }
}