package org.cts.op;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import org.cts.Identifier;
import java.util.List;
import org.cts.crs.*;
import org.cts.datum.GeodeticDatum;
import org.cts.op.transformation.NTv2GridShiftTransformation;

/**
 * CoordinateOperationFactory is a factory used to create
 * {@link org.cts.CoordinateOperation}s from source and target {@link org.cts.crs.CoordinateReferenceSystem}s.
 * @author Michaël Michaud, Jules Party
 */
public final class CoordinateOperationFactory {
  private static final Logger LOG = Logger.getLogger(CoordinateOperationFactory.class);

  public final static int GDATUM_OP = 1;

  public final static int VDATUM_OP = 2;

  public final static int ELLIPSOID_OP = 4;

  public final static int PRIME_MERIDIAN_OP = 8;

  public final static int GEOGRAPHIC_OP = 16;

  public final static int PROJECTION_OP = 32;

  public final static int DIMENSION_OP = 64;

  public final static int AXIS_ORDER_OP = 128;

  public final static int UNIT_OP = 256;

  /**
     * Create a {@link org.cts.CoordinateOperation} from a source {@link org.cts.crs.CompoundCRS}
     * to a target {@link org.cts.crs.CompoundCRS}.
     */
  public static List<CoordinateOperation> createCoordinateOperations(CompoundCRS source, CompoundCRS target) {
    System.out.println("createCoordinateOperations() for compound CRS is not yet implemented");
    return new ArrayList<CoordinateOperation>();
  }

  /**
     * Create a CoordinateOperation from a source {@link org.cts.crs.GeodeticCRS}
     * to a target {@link org.cts.crs.GeodeticCRS}.
     * Remember that {@link org.cts.crs.GeodeticCRS} includes {@link org.cts.crs.GeocentricCRS},
     * {@link Geographic2DCRS}, {@link org.cts.crs.Geographic3DCRS} and {@link org.cts.crs.ProjectedCRS}.
     * @param source the (non null) source geodetic coordinate reference system
     * @param target the (non null) target geodetic coordinate reference system
     */
  public static List<CoordinateOperation> createCoordinateOperations(GeodeticCRS source, GeodeticCRS target) {
    if (source == null) {
      throw new IllegalArgumentException("The source CRS must not be null");
    }
    if (target == null) {
      throw new IllegalArgumentException("The target CRS must not be null");
    }
    List<CoordinateOperation> opList = source.getCRSTransformations(target);
    if (opList != null) {
      return opList;
    } else {
      opList = new ArrayList<CoordinateOperation>();
      GeodeticDatum sourceDatum = source.getDatum();
      if (sourceDatum == null) {
        LOG.warn(source.getName() + " has no Geodetic Datum");
        throw new IllegalArgumentException("The source datum must not be null");
      }
      GeodeticDatum targetDatum = target.getDatum();
      if (targetDatum == null) {
        LOG.warn(target.getName() + " has no Geodetic Datum");
        throw new IllegalArgumentException("The target datum must not be null");
      }
      if (source.getGridTransformations(targetDatum) != null) {
        addNadgridsOperationDir(sourceDatum, source, targetDatum, target, source.getGridTransformations(targetDatum), opList);
      } else {
        if (target.getGridTransformations(sourceDatum) != null) {
          addNadgridsOperationInv(sourceDatum, source, targetDatum, target, target.getGridTransformations(sourceDatum), opList);
        }
      }
      if (sourceDatum.equals(targetDatum)) {
        addCoordinateOperations(sourceDatum, source, target, opList);
      } else {
        addCoordinateOperations(sourceDatum, source, targetDatum, target, opList);
      }
      source.addCRSTransformation(target, opList);
    }
    return opList;
  }

  private static void addNadgridsOperationDir(GeodeticDatum sourceDatum, GeodeticCRS source, GeodeticDatum targetDatum, GeodeticCRS target, List<CoordinateOperation> nadgridsTransformations, List<CoordinateOperation> opList) {
    for (CoordinateOperation coordOp : nadgridsTransformations) {
      try {
        if (!(coordOp instanceof NTv2GridShiftTransformation) || (sourceDatum.getShortName().equals(((NTv2GridShiftTransformation) coordOp).getFromDatum()))) {
          opList.add(new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class, source.getName() + " to " + target.getName()), source.toGeographicCoordinateConverter(), coordOp, target.fromGeographicCoordinateConverter()));
        } else {
          NTv2GridShiftTransformation gt = (NTv2GridShiftTransformation) coordOp;
          GeodeticDatum gtSource = GeodeticDatum.datumFromName.get(gt.getFromDatum());
          opList.add(new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class, sourceDatum.getName() + " to " + targetDatum.getName() + " through " + gt.getName() + " transformation"), source.toGeographicCoordinateConverter(), sourceDatum.getCoordinateOperations(gtSource).get(0), gt, target.fromGeographicCoordinateConverter()));
        }
      } catch (NonInvertibleOperationException e) {
        LOG.warn("Operation from " + source.getName() + " to " + target.getName() + " could not be created");
        LOG.error("CoordinateOperationFactory", e);
      }
    }
  }

  private static void addNadgridsOperationInv(GeodeticDatum sourceDatum, GeodeticCRS source, GeodeticDatum targetDatum, GeodeticCRS target, List<CoordinateOperation> nadgridsTransformations, List<CoordinateOperation> opList) {
    for (CoordinateOperation coordOp : nadgridsTransformations) {
      try {
        if (!(coordOp instanceof NTv2GridShiftTransformation) || sourceDatum.getShortName().equals(((NTv2GridShiftTransformation) coordOp).getFromDatum())) {
          opList.add(new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class, source.getName() + " to " + target.getName()), source.toGeographicCoordinateConverter(), coordOp.inverse(), target.fromGeographicCoordinateConverter()));
        } else {
          NTv2GridShiftTransformation gt = (NTv2GridShiftTransformation) coordOp;
          GeodeticDatum gtSource = GeodeticDatum.datumFromName.get(gt.getFromDatum());
          opList.add(new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class, source.getName() + " to " + target.getName()), source.toGeographicCoordinateConverter(), gt.inverse(), gtSource.getCoordinateOperations(targetDatum).get(0), target.fromGeographicCoordinateConverter()));
        }
      } catch (NonInvertibleOperationException e) {
        LOG.warn("Operation from " + source.getName() + " to " + target.getName() + " could not be created");
        LOG.error("CoordinateOperationFactory", e);
      }
    }
  }

  /**
     * Create a CoordinateOperation from a source {@link org.cts.crs.GeodeticCRS}
     * to a target {@link GeodeticCRS} using the same {@link org.cts.datum.GeodeticDatum}.
     * Remember that {@link GeodeticCRS} includes {@link GeocentricCRS},
     * {@link Geographic2DCRS}, {@link Geographic3DCRS} and {@link ProjectedCRS}.
     * @param datum the (non null) common datum of source and target CRS
     * @param source the source geodetic coordinate reference system
     * @param target the target geodetic coordinate reference system
     */
  private static void addCoordinateOperations(GeodeticDatum datum, GeodeticCRS source, GeodeticCRS target, List<CoordinateOperation> opList) {
    try {
      opList.add(new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class, source.getName() + " to " + target.getName()), source.toGeographicCoordinateConverter(), target.fromGeographicCoordinateConverter()));
    } catch (NonInvertibleOperationException e) {
      LOG.warn("Operation from " + source.getName() + " to " + target.getName() + " could not be created");
      LOG.error("CoordinateOperationFactory", e);
    }
  }

  /**
     * Create a CoordinateOperation from a source {@link GeodeticCRS}
     * to a target {@link GeodeticCRS} based on different {@link Datum}.
     * Remember that {@link GeodeticCRS} includes {@link GeocentricCRS},
     * {@link Geographic2DCRS}, {@link Geographic3DCRS} and {@link ProjectedCRS}.
     * @param sourceDatum the (non null) datum used by source CRS
     * @param source the source geodetic coordinate reference system
     * @param targetDatum the (non null) datum used by target CRS
     * @param target the target geodetic coordinate reference system
     */
  private static void addCoordinateOperations(GeodeticDatum sourceDatum, GeodeticCRS source, GeodeticDatum targetDatum, GeodeticCRS target, List<CoordinateOperation> opList) {
    List<CoordinateOperation> datumTransformations = sourceDatum.getCoordinateOperations(targetDatum);
    for (CoordinateOperation datumTf : datumTransformations) {
      try {
        opList.add(new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class, source.getName() + " to " + target.getName() + " through " + datumTf.getName()), source.toGeographicCoordinateConverter(), datumTf, target.fromGeographicCoordinateConverter()));
      } catch (NonInvertibleOperationException e) {
        LOG.warn("Operation from " + source.getName() + " to " + target.getName() + " through " + datumTf.getName() + " could not be created");
        LOG.error("CoordinateOperationFactory", e);
      }
    }
  }
}