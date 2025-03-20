package org.cts.registry;
import java.util.Map;
import org.cts.CTSTestCase;
import org.cts.parser.proj.ProjKeyParameters;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Erwan Bocher
 */
public class RegistryParserTest extends CTSTestCase {
  @Test public void testEPSG() throws Exception {
    Map<String, String> parameters = getParameters("epsg", "4326");
    assertTrue(parameters.get(ProjKeyParameters.proj).equals("longlat"));
    assertTrue(parameters.get(ProjKeyParameters.ellps).equals("WGS84"));
    assertTrue(parameters.get(ProjKeyParameters.datum).equals("WGS84"));
  }

  @Test public void testReadEPSGFile2() throws Exception {
    Map<String, String> parameters = getParameters("epsg", "2736");
    assertTrue(parameters.get(ProjKeyParameters.proj).equals("utm"));
    assertTrue(parameters.get(ProjKeyParameters.zone).equals("36"));
    assertTrue(parameters.get(ProjKeyParameters.south) == null);
    assertTrue(parameters.get(ProjKeyParameters.ellps).equals("clrk66"));
    assertTrue(parameters.get(ProjKeyParameters.towgs84).equals("-115.064,-87.39,-101.716,-0.058,4.001,-2.062,9.366"));
    assertTrue(parameters.get(ProjKeyParameters.units).equals("m"));
  }

  @Test public void testReadEPSGFileWrongCode() throws Exception {
    Map<String, String> parameters = getParameters("EPSG", "300000");
    assertTrue(parameters == null);
  }

  /**
     * Return parameters from a registry and a code
     *
     * @param registry
     * @param code
     * @return
     */
  public Map<String, String> getParameters(String registry, String code) throws Exception {
    Map<String, String> parameters = cRSFactory.getRegistryManager().getRegistry(registry).getParameters(code);
    return parameters;
  }

  @Test public void testReadIGNFFile() throws Exception {
    Map<String, String> parameters = getParameters("IGNF", "RGF93");
    assertTrue(parameters.get(ProjKeyParameters.title).equals("Reseau geodesique francais 1993"));
    assertTrue(parameters.get(ProjKeyParameters.proj).equals("geocent"));
    assertTrue(parameters.get(ProjKeyParameters.towgs84).equals("0.0000,0.0000,0.0000"));
    assertTrue(parameters.get(ProjKeyParameters.a).equals("6378137.0000"));
    assertTrue(parameters.get(ProjKeyParameters.rf).equals("298.2572221010000"));
    assertTrue(parameters.get(ProjKeyParameters.units).equals("m"));
    assertTrue(parameters.get(ProjKeyParameters.no_defs) == null);
  }

  @Test public void testReadIGNFNadGrids() throws Exception {
    Map<String, String> parameters = getParameters("IGNF", "NTF");
    assertTrue(parameters.get(ProjKeyParameters.title).equals("Nouvelle Triangulation Francaise"));
    assertTrue(parameters.get(ProjKeyParameters.proj).equals("geocent"));
    assertTrue(parameters.get(ProjKeyParameters.nadgrids).equals("ntf_r93.gsb,null"));
    assertTrue(parameters.get(ProjKeyParameters.towgs84).equals("-168.0000,-60.0000,320.0000"));
    assertTrue(parameters.get(ProjKeyParameters.a).equals("6378249.2000"));
    assertTrue(parameters.get(ProjKeyParameters.rf).equals("293.4660210000000"));
    assertTrue(parameters.get(ProjKeyParameters.units).equals("m"));
  }

  @Test public void testReadESRIFile() throws Exception {
    Map<String, String> parameters = getParameters("ESRI", "102632");
    assertTrue(parameters.get(ProjKeyParameters.proj).equals("tmerc"));
    assertTrue(parameters.get(ProjKeyParameters.lat_0).equals("54"));
    assertTrue(parameters.get(ProjKeyParameters.lon_0).equals("-142"));
    assertTrue(parameters.get(ProjKeyParameters.k).equals("0.999900"));
    assertTrue(parameters.get(ProjKeyParameters.x_0).equals("500000.0000000002"));
    assertTrue(parameters.get(ProjKeyParameters.y_0).equals("0"));
    assertTrue(parameters.get(ProjKeyParameters.ellps).equals("GRS80"));
    assertTrue(parameters.get(ProjKeyParameters.datum).equals("NAD83"));
    assertTrue(parameters.get(ProjKeyParameters.to_meter).equals("0.3048006096012192"));
  }

  @Test public void testRegisteryCaseInsensitive() throws Exception {
    Map<String, String> parameters = getParameters("IGnF", "AmSt63");
    assertTrue(parameters != null);
    parameters = getParameters("EPsg", "4326");
    assertTrue(parameters != null);
  }
}