package org.cts;
import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.parser.prj.PrjParser;
import org.cts.registry.Registry;
import org.cts.registry.RegistryManager;
import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This factory is in charge of creating new
 * {@link org.cts.crs.CoordinateReferenceSystem}s from an authority name and a
 * code.
 * <p>Creation of the {@link org.cts.crs.CoordinateReferenceSystem} from a text
 * file will be delegated to one of RegistryManager's registries. If
 * RegistryManager don't know the authority of the CRS, an exception is
 * returned.</p>
 * <p>This class also manages a Cache which return
 * {@link org.cts.crs.CoordinateReferenceSystem}s which have already been
 * parsed.</p>
 *
 * @TODO authorityAndSrid is the same as Identifier.getCode()
 *
 * @author Erwan Bocher
 */
public class CRSFactory {
  private RegistryManager registryManager = new RegistryManager();

  protected final CRSCache<String, CoordinateReferenceSystem> CRSPOOL = new CRSCache<String, CoordinateReferenceSystem>(10);

  /**
     * Creates a new factory.
     */
  public CRSFactory() {
  }

  /**
     * Return a
     *
     * @CoordinateReferenceSystem according an authority and a srid ie :
     * EPSG:4326 or IGNF:LAMBE
     *
     * @param authorityAndSrid
     * @return
     * @throws CRSException
     */
  public CoordinateReferenceSystem getCRS(String authorityAndSrid) throws CRSException {
    CoordinateReferenceSystem crs = CRSPOOL.get(authorityAndSrid);
    if (crs == null) {
      if (isRegistrySupported(authorityAndSrid)) {
        String[] registryNameWithCode = authorityAndSrid.split(":");
        Registry registry = getRegistryManager().getRegistry(registryNameWithCode[0]);
        Map<String, String> crsParameters = registry.getParameters(registryNameWithCode[1]);
        if (crsParameters != null) {
          crs = CRSHelper.createCoordinateReferenceSystem(new Identifier(registryNameWithCode[0], registryNameWithCode[1], crsParameters.get("title")), crsParameters);
        }
        if (crs != null) {
          CRSPOOL.put(authorityAndSrid, crs);
        }
      }
    }
    return crs;
  }

  /**
     * Return the registry manager
     *
     * @return
     */
  public RegistryManager getRegistryManager() {
    return registryManager;
  }

  /**
     * Check if the registry name of the crsCode is supported.
     *
     * @param crsCode
     * @return
     */
  public boolean isRegistrySupported(String crsCode) {
    int p = crsCode.indexOf(':');
    if (p >= 0) {
      String auth = crsCode.substring(0, p);
      if (getRegistryManager().contains(auth.toLowerCase())) {
        return true;
      }
    }
    throw new RuntimeException("This registry is not supported");
  }

  /**
     * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT String
     * (PRJ).
     *
     * @param prjString the PRJ String
     * @return a {@link CoordinateReferenceSystem}
     */
  public CoordinateReferenceSystem createFromPrj(String prjString) {
    PrjParser p = new PrjParser();
    Map<String, String> prjParameters = p.getParameters(prjString);
    String name = prjParameters.remove("name");
    String refname = prjParameters.remove("refname");
    CoordinateReferenceSystem crs;
    if (refname != null) {
      crs = CRSPOOL.get(refname);
      if (crs == null) {
        String[] authorityNameWithKey = refname.split(":");
        crs = CRSHelper.createCoordinateReferenceSystem(new Identifier(authorityNameWithKey[0], authorityNameWithKey[1], name), prjParameters);
      }
      if (crs != null) {
        CRSPOOL.put(refname, crs);
      }
    } else {
      crs = CRSHelper.createCoordinateReferenceSystem(new Identifier(name, name, name), prjParameters);
    }
    return crs;
  }

  /**
     * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT String
     * (PRJ).
     *
     * @param stream
     * @param encoding
     * @return a {@link CoordinateReferenceSystem}
     * @throws IOException
     */
  public CoordinateReferenceSystem createFromPrj(InputStream stream, Charset encoding) throws IOException {
    BufferedReader r = new BufferedReader(new InputStreamReader(stream, encoding));
    StringBuilder b = new StringBuilder();
    while (r.ready()) {
      b.append(r.readLine());
    }
    return createFromPrj(b.toString());
  }

  /**
     * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT String
     * (PRJ).
     *
     * @param stream
     * @return
     * @throws IOException
     */
  public CoordinateReferenceSystem createFromPrj(InputStream stream) throws IOException {
    return createFromPrj(stream, Charset.defaultCharset());
  }

  /**
     * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT String
     * (PRJ).
     *
     * @param file
     * @return a {@link CoordinateReferenceSystem}
     * @throws IOException if there is a problem reading the file
     */
  public CoordinateReferenceSystem createFromPrj(File file) throws IOException {
    InputStream i = null;
    CoordinateReferenceSystem crs;
    try {
      i = new FileInputStream(file);
      crs = createFromPrj(i);
    }  finally {
      if (i != null) {
        i.close();
      }
    }
    return crs;
  }

  /**
     * Return a list of supported codes according an registryName
     * @param registeryName
     * @return 
     */
  public Set<String> getSupportedCodes(String registryName) {
    return getRegistryManager().getRegistry(registryName).getSupportedCodes();
  }

  public class CRSCache<K extends java.lang.Object, V extends java.lang.Object> extends LinkedHashMap<K, V> {
    private final int limit;

    public CRSCache(int limit) {
      super(16, 0.75f, true);
      this.limit = limit;
    }

    @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
      return size() > limit;
    }
  }
}