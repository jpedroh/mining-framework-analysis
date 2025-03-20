package org.cts.op;
import org.cts.CRSFactory;
import org.cts.CTSTestCase;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;
import org.cts.parser.prj.PrjWriter;
import java.util.List;

/**
 *
 * @author Erwan Bocher
 */
public class BaseCoordinateTransformTest extends CTSTestCase {
  protected boolean verbose = false;

  /**
     * Transform a point from a CRS to another CRS
     *
     * @param sourceCRS
     * @param targetCRS
     * @param inputPoint
     * @return
     * @throws IllegalCoordinateException
     */
  public double[] transform(GeodeticCRS sourceCRS, GeodeticCRS targetCRS, double[] inputPoint) throws IllegalCoordinateException {
    List<CoordinateOperation> ops = CoordinateOperationFactory.createCoordinateOperations(sourceCRS, targetCRS);
    if (!ops.isEmpty()) {
      if (verbose) {
        System.out.println(ops.get(0));
      }
      return ops.get(0).transform(new double[] { inputPoint[0], inputPoint[1], inputPoint[2] });
    } else {
      return new double[] { 0.0d, 0.0d, 0.0d };
    }
  }

  /**
     * Display the CRS in a WKT representation
     * @param crs 
     */
  public void printCRStoWKT(CoordinateReferenceSystem crs) {
    System.out.println(PrjWriter.crsToWKT(crs));
  }
}