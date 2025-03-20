package org.cts.datum;
import java.util.*;
import org.cts.op.CoordinateOperation;
import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.cs.Extent;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.NonInvertibleOperationException;

/**
 * A datum (plural datums) is a reference from which measurements are made.<p>
 * In surveying and geodesy, a datum is a reference point on the earth's surface
 * against which position measurements are made, and an associated model of the
 * shape of the earth for computing positions. Horizontal datums are used for
 * describing a point on the earth's surface, in latitude and longitude or
 * another coordinate system. Vertical datums are used to measure elevations or
 * underwater depths. In engineering and drafting, a datum is a reference point,
 * surface, or axis on an object against which measurements are made.<p> (Taken
 * from <a ref="http://en.wikipedia.org/wiki/Datum">wikipedia</a> on
 * 2006-10-06)</p>
 *
 * @author Michaël Michaud
 */
public abstract class AbstractDatum extends IdentifiableComponent implements Datum {
  private Extent extent;

  private String origin;

  private String epoch;

  private Map<Datum, List<CoordinateOperation>> datumTransformations = new HashMap<Datum, List<CoordinateOperation>>();

  /**
     * Creates a new Datum.
     *
     * @param identifier
     * @param extent valid domain extent (extent definition depends on the kind
     * of Datum)
     * @param origin description of the origin or anchor point of this Datum.
     * @param epoch epoch of this Datum realization
     */
  protected AbstractDatum(Identifier identifier, Extent extent, String origin, String epoch) {
    super(identifier);
    this.extent = extent;
    this.origin = origin;
    this.epoch = epoch;
  }

  /**
     * Returns the valid extent of this Datum
     */
  @Override public Extent getExtent() {
    return extent;
  }

  /**
     * Returns the description of this Datum origin
     */
  @Override public String getOrigin() {
    return origin;
  }

  /**
     * Returns the realization epoch of this Datum as a String
     */
  @Override public String getEpoch() {
    return epoch;
  }

  /**
     * Add a Transformation to another Datum.
     */
  public void addCoordinateOperation(Datum datum, CoordinateOperation coordOp) {
    if (datumTransformations.get(datum) == null) {
      datumTransformations.put(datum, new ArrayList<CoordinateOperation>());
    }
    if (!datumTransformations.get(datum).contains(coordOp)) {
      datumTransformations.get(datum).add(coordOp);
    }
  }

  /**
     * Get a transformation to another datum.
     */
  public List<CoordinateOperation> getCoordinateOperations(Datum datum) {
    if (datumTransformations.get(datum) == null) {
      if (!getCoordinateOperations(GeodeticDatum.WGS84).isEmpty() && !GeodeticDatum.WGS84.getCoordinateOperations(datum).isEmpty()) {
        CoordinateOperation op = new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class, getName() + "to" + datum.getName() + "throughWGS84"), getCoordinateOperations(GeodeticDatum.WGS84).get(0), GeodeticDatum.WGS84.getCoordinateOperations(datum).get(0));
        addCoordinateOperation(datum, op);
        try {
          ((AbstractDatum) datum).addCoordinateOperation(datum, op.inverse());
        } catch (NonInvertibleOperationException e) {
        }
      } else {
        datumTransformations.put(datum, new ArrayList<CoordinateOperation>());
      }
    }
    return datumTransformations.get(datum);
  }

  /**
     * Returns a String representation of this Datum.
     */
  @Override public String toString() {
    StringBuilder sb = new StringBuilder(getIdentifier().toString());
    sb.append(" [");
    for (Iterator<Datum> it = datumTransformations.keySet().iterator(); it.hasNext(); ) {
      sb.append("").append(it.next().getShortName());
      if (it.hasNext()) {
        sb.append(" - ");
      }
    }
    sb.append("]");
    return sb.toString();
  }
}