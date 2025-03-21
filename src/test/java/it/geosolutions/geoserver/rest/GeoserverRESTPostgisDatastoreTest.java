package it.geosolutions.geoserver.rest;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.encoder.GSPostGISDatastoreEncoder;
import org.apache.log4j.Logger;

/**
 * Testcase for creating postgis-based resources on geoserver.
 * <P>
 * Since these tests require a running postgis instance, this is more like integration tests.<br/>
 * You may skip them by defining<tt> <pre>
 *        -DpgIgnore=true </pre></tt>
 * When <tt>pgIgnore</tt> is defined that way, failing tests will not break
 * the build: they will be logged as errors instead.
 *
 * <P>
 * The target postgis instance can be customized by defining the following env vars: <ul>
 * <LI><TT>pgHost</TT> (default <TT>localhost</TT>)</LI>
 * <LI><TT>pgPort</TT> (default: <TT>5432</TT>)</LI>
 * <LI><TT>pgDatabase</TT> (default: <TT>test</TT>)</LI>
 * <LI><TT>pgSchema</TT> (default: <TT>public</TT>)</LI>
 * <LI><TT>pgUser</TT> (default: <TT>utest</TT>)</LI>
 * <LI><TT>pgPassword</TT> (default: <TT>ptest</TT>)</LI>
 * </ul>
 *
 * @author etj
 * @author Eric Grosso
 *
 * @see GeoserverRESTTest
 */
public class GeoserverRESTPostgisDatastoreTest extends GeoserverRESTTest {
  private final static Logger LOGGER = Logger.getLogger(GeoserverRESTPostgisDatastoreTest.class);

  private static final String DEFAULT_WS = "it.geosolutions";

  private final boolean pgIgnore;

  private final String pgHost;

  private final int pgPort;

  private final String pgDatabase;

  private final String pgSchema;

  private final String pgUser;

  private final String pgPassword;

  public GeoserverRESTPostgisDatastoreTest(String testName) {
    super(testName);
    pgIgnore = System.getProperty("pgIgnore", "false").equalsIgnoreCase("true");
    pgHost = System.getProperty("pgHost", "localhost");
    pgPort = Integer.parseInt(System.getProperty("pgPort", "5432"));
    pgDatabase = System.getProperty("pgDatabase", "test");
    pgSchema = System.getProperty("pgSchema", "public");
    pgUser = System.getProperty("pgUser", "utest");
    pgPassword = System.getProperty("pgPassword", "ptest");
  }

  public void testCreateDeletePostGISDatastore() {
    if (!enabled()) {
      return;
    }
    deleteAll();
    String wsName = DEFAULT_WS;
    String datastoreName = "resttestpostgis";
    String description = "description";
    String dsNamespace = "http://www.geo-solutions.it";
    boolean exposePrimaryKeys = true;
    boolean validateConnections = false;
    String primaryKeyMetadataTable = "test";
    GSPostGISDatastoreEncoder datastoreEncoder = new GSPostGISDatastoreEncoder();
    datastoreEncoder.defaultInit();
    datastoreEncoder.addName(datastoreName);
    datastoreEncoder.addDescription(description);
    datastoreEncoder.addNamespace(dsNamespace);
    datastoreEncoder.addHost(pgHost);
    datastoreEncoder.addPort(pgPort);
    datastoreEncoder.addDatabase(pgDatabase);
    datastoreEncoder.addSchema(pgSchema);
    datastoreEncoder.addUser(pgUser);
    datastoreEncoder.addPassword(pgPassword);
    datastoreEncoder.addExposePrimaryKeys(exposePrimaryKeys);
    datastoreEncoder.addValidateConnections(validateConnections);
    datastoreEncoder.addPrimaryKeyMetadataTable(primaryKeyMetadataTable);
    assertTrue(publisher.createWorkspace(wsName));
    boolean created = publisher.createPostGISDatastore(wsName, datastoreEncoder);
    if (!pgIgnore) {
      assertTrue("PostGIS datastore not created", created);
    } else {
      if (!created) {
        LOGGER.error("*** Datastore " + datastoreName + " has not been created.");
      }
    }
    RESTDataStore datastore = reader.getDatastore(wsName, datastoreName);
    LOGGER.info("The type of the created datastore is: " + datastore.getType());
    boolean removed = publisher.removeDatastore(wsName, datastoreName);
    if (!pgIgnore) {
      assertTrue("PostGIS datastore not removed", removed);
    } else {
      if (!removed) {
        LOGGER.error("*** Datastore " + datastoreName + " has not been removed.");
      }
    }
    assertTrue(publisher.removeWorkspace(wsName));
  }
}