package org.cts.op;
import org.cts.CRSFactory;
import org.cts.CTSTestCase;
import org.cts.CoordinateOperation;
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
  CRSFactory crsf = new CRSFactory();

  protected boolean verbose = false;

  /**
     * Return the crs from an authority and a srid ie : EPSG:4326
     *
     * @param authorityAndSrid
     * @return
     * @throws CRSException
     */
  public CoordinateReferenceSystem createCRS(String authorityAndSrid) throws CRSException {
    return crsf.getCRS(authorityAndSrid);
  }

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