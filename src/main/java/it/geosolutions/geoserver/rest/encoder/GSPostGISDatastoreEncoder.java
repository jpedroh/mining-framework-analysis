package it.geosolutions.geoserver.rest.encoder;
import it.geosolutions.geoserver.rest.encoder.utils.NestedElementEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

/**
 * Geoserver datastore XML encoder.
 *  
 * @author Eric Grosso
 * @author ETj
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSPostGISDatastoreEncoder extends PropertyXMLEncoder {
  private NestedElementEncoder connectionParameters = new NestedElementEncoder("connectionParameters");

  public GSPostGISDatastoreEncoder() {
    super("dataStore");
    addType("PostGIS");
    addDatabaseType("postgis");
    addContent(connectionParameters.getRoot());
  }

  /**
     * Set some initial defaults.
     * <br/><br/>
     * The default parameters are as follows: <ul>
     * <li>maximum connections: 10, </li>
     * <li>minimum connections: 1,</li>
     * <li>fetch size: 1000, </li>
     * <li>connection timeout: 20 seconds, </li>
     * <li>loose BBox: true, </li>
     * <li>prepared statements: false,</li>
     * <li>maximum open prepared statements: 50.    </li>
     * </ul>
     */
  public void defaultInit() {
    addMinConnections(1);
    addMaxConnections(10);
    addFetchSize(1000);
    addConnectionTimeout(20);
    addLooseBBox(true);
    addPreparedStatements(false);
    addMaxOpenPreparedStatements(50);
  }

  public void addName(String name) {
    add("name", name);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setName(String name) {
    set("name", name);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addDescription(String description) {
    add("description", description);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setDescription(String description) {
    set("description", description);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addType(String type) {
    add("type", type);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setType(String type) {
    set("type", type);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addEnabled(boolean enabled) {
    add("enabled", Boolean.toString(enabled));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setEnabled(boolean enabled) {
    set("enabled", Boolean.toString(enabled));
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addNamespace(String namespace) {
    connectionParameters.add("namespace", namespace);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setNamespace(String namespace) {
    connectionParameters.set("namespace", namespace);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addHost(String host) {
    connectionParameters.add("host", host);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setHost(String host) {
    connectionParameters.set("host", host);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addPort(int port) {
    connectionParameters.add("port", Integer.toString(port));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setPort(int port) {
    connectionParameters.set("port", Integer.toString(port));
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addDatabase(String database) {
    connectionParameters.add("database", database);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setDatabase(String database) {
    connectionParameters.set("database", database);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addSchema(String schema) {
    connectionParameters.add("schema", schema);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setSchema(String schema) {
    connectionParameters.set("schema", schema);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addUser(String user) {
    connectionParameters.add("user", user);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setUser(String user) {
    connectionParameters.set("user", user);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addPassword(String password) {
    connectionParameters.add("passwd", password);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setPassword(String password) {
    connectionParameters.set("passwd", password);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addDatabaseType(String dbtype) {
    connectionParameters.add("dbtype", dbtype);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setDatabaseType(String dbtype) {
    connectionParameters.set("dbtype", dbtype);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addJndiReferenceName(String jndiReferenceName) {
    connectionParameters.add("jndiReferenceName", jndiReferenceName);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setJndiReferenceName(String jndiReferenceName) {
    connectionParameters.set("jndiReferenceName", jndiReferenceName);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addExposePrimaryKeys(boolean exposePrimaryKeys) {
    connectionParameters.add("Expose primary keys", Boolean.toString(exposePrimaryKeys));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setExposePrimaryKeys(boolean exposePrimaryKeys) {
    connectionParameters.set("Expose primary keys", Boolean.toString(exposePrimaryKeys));
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addMaxConnections(int maxConnections) {
    connectionParameters.add("max connections", Integer.toString(maxConnections));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setMaxConnections(int maxConnections) {
    connectionParameters.set("max connections", Integer.toString(maxConnections));
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addMinConnections(int minConnections) {
    connectionParameters.add("min connections", Integer.toString(minConnections));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setMinConnections(int minConnections) {
    connectionParameters.set("min connections", Integer.toString(minConnections));
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addFetchSize(int fetchSize) {
    connectionParameters.add("fetch size", Integer.toString(fetchSize));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setFetchSize(int fetchSize) {
    connectionParameters.set("fetch size", Integer.toString(fetchSize));
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addConnectionTimeout(int seconds) {
    connectionParameters.add("Connection timeout", Integer.toString(seconds));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setConnectionTimeout(int seconds) {
    connectionParameters.set("Connection timeout", Integer.toString(seconds));
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addValidateConnections(boolean validateConnections) {
    connectionParameters.add("validate connections", Boolean.toString(validateConnections));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setValidateConnections(boolean validateConnections) {
    connectionParameters.set("validate connections", Boolean.toString(validateConnections));
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addPrimaryKeyMetadataTable(String primaryKeyMetadataTable) {
    connectionParameters.add("Primary key metadata table", primaryKeyMetadataTable);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setPrimaryKeyMetadataTable(String primaryKeyMetadataTable) {
    connectionParameters.set("Primary key metadata table", primaryKeyMetadataTable);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addLooseBBox(boolean looseBBox) {
    connectionParameters.add("Loose bbox", Boolean.toString(looseBBox));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setLooseBBox(boolean looseBBox) {
    connectionParameters.set("Loose bbox", Boolean.toString(looseBBox));
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addPreparedStatements(boolean preparedStatements) {
    connectionParameters.add("preparedStatements", Boolean.toString(preparedStatements));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setPreparedStatements(boolean preparedStatements) {
    connectionParameters.set("preparedStatements", Boolean.toString(preparedStatements));
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java


  public void addMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
    connectionParameters.add("Max open prepared statements", Integer.toString(maxOpenPreparedStatements));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
    connectionParameters.set("Max open prepared statements", Integer.toString(maxOpenPreparedStatements));
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSPostGISDatastoreEncoder.java/right.java
}