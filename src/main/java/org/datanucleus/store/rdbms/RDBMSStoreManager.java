package org.datanucleus.store.rdbms;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.datanucleus.ClassConstants;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.NucleusContext;
import org.datanucleus.PersistenceNucleusContext;
import org.datanucleus.PropertyNames;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.flush.FlushOrdered;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.identity.SCOID;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.metadata.DatastoreIdentityMetaData;
import org.datanucleus.metadata.ValueGenerationStrategy;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.InheritanceMetaData;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.QueryLanguage;
import org.datanucleus.metadata.SequenceMetaData;
import org.datanucleus.metadata.TableGeneratorMetaData;
import org.datanucleus.state.DNStateManager;
import org.datanucleus.state.ReferentialStateManagerImpl;
import org.datanucleus.store.AbstractStoreManager;
import org.datanucleus.store.BackedSCOStoreManager;
import org.datanucleus.store.NucleusSequence;
import org.datanucleus.store.StoreData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.autostart.AutoStartMechanism;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.query.Query;
import org.datanucleus.store.rdbms.SQLController.StatementLoggingType;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapterFactory;
import org.datanucleus.store.rdbms.autostart.SchemaAutoStarter;
import org.datanucleus.store.rdbms.discriminatordefiner.DiscriminatorDefiner;
import org.datanucleus.store.rdbms.exceptions.NoTableManagedException;
import org.datanucleus.store.rdbms.exceptions.UnsupportedDataTypeException;
import org.datanucleus.store.rdbms.identifier.DN2IdentifierFactory;
import org.datanucleus.store.rdbms.identifier.DNIdentifierFactory;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import org.datanucleus.store.rdbms.identifier.JPAIdentifierFactory;
import org.datanucleus.store.rdbms.identifier.JPOXIdentifierFactory;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.store.rdbms.mapping.java.ArrayMapping;
import org.datanucleus.store.rdbms.mapping.java.CollectionMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.mapping.java.MapMapping;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.query.JDOQLQuery;
import org.datanucleus.store.rdbms.query.JPQLQuery;
import org.datanucleus.store.rdbms.query.SQLQuery;
import org.datanucleus.store.rdbms.query.StoredProcedureQuery;
import org.datanucleus.store.rdbms.schema.JDBCTypeInfo;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import org.datanucleus.store.rdbms.schema.RDBMSSchemaHandler;
import org.datanucleus.store.rdbms.schema.RDBMSSchemaInfo;
import org.datanucleus.store.rdbms.schema.RDBMSTableInfo;
import org.datanucleus.store.rdbms.schema.RDBMSTypesInfo;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.scostore.FKArrayStore;
import org.datanucleus.store.rdbms.scostore.FKListStore;
import org.datanucleus.store.rdbms.scostore.FKMapStore;
import org.datanucleus.store.rdbms.scostore.FKSetStore;
import org.datanucleus.store.rdbms.scostore.JoinArrayStore;
import org.datanucleus.store.rdbms.scostore.JoinListStore;
import org.datanucleus.store.rdbms.scostore.JoinMapStore;
import org.datanucleus.store.rdbms.scostore.JoinPersistableRelationStore;
import org.datanucleus.store.rdbms.scostore.JoinSetStore;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.rdbms.table.ArrayTable;
import org.datanucleus.store.rdbms.table.ClassTable;
import org.datanucleus.store.rdbms.table.ClassView;
import org.datanucleus.store.rdbms.table.CollectionTable;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.store.rdbms.table.MapTable;
import org.datanucleus.store.rdbms.table.PersistableJoinTable;
import org.datanucleus.store.rdbms.table.ProbeTable;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.table.TableImpl;
import org.datanucleus.store.rdbms.table.ViewImpl;
import org.datanucleus.store.rdbms.valuegenerator.SequenceTable;
import org.datanucleus.store.rdbms.valuegenerator.TableGenerator;
import org.datanucleus.store.schema.SchemaAwareStoreManager;
import org.datanucleus.store.schema.SchemaScriptAwareStoreManager;
import org.datanucleus.store.schema.StoreSchemaData;
import org.datanucleus.store.types.IncompatibleFieldTypeException;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.store.types.scostore.Store;
import org.datanucleus.store.valuegenerator.AbstractConnectedGenerator;
import org.datanucleus.store.valuegenerator.AbstractConnectedGenerator.ConnectionPreference;
import org.datanucleus.store.valuegenerator.ValueGenerationConnectionProvider;
import org.datanucleus.store.valuegenerator.ValueGenerator;
import org.datanucleus.transaction.TransactionIsolation;
import org.datanucleus.transaction.TransactionUtils;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.util.Localiser;
import org.datanucleus.util.MacroString;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.StringUtils;

/**
 * StoreManager for RDBMS datastores. 
 * Provided by the "store-manager" extension key "rdbms" and accepts datastore URLs valid for JDBC.
 * <p>
 * The RDBMS manager's responsibilities extend those for StoreManager to add :
 * <ul>
 * <li>creates and controls access to the data sources required for this datastore instance</li>
 * <li>implements insert(), fetch(), update(), delete() in the interface to StateManager.</li>
 * <li>Providing cached access to JDBC database metadata (in particular column information).</li>
 * <li>Resolving SQL identifier macros to actual SQL identifiers.</li>
 * </ul>
 * TODO Change RDBMSManager to share schema information (DatabaseMetaData) with other RDBMSManager.
 */
public class RDBMSStoreManager extends AbstractStoreManager implements BackedSCOStoreManager, SchemaAwareStoreManager, SchemaScriptAwareStoreManager {
  static {
    Localiser.registerBundle("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
  }

  public static final String METADATA_NONDURABLE_REQUIRES_TABLE = "requires-table";

  /** Adapter for the datastore being used. */
  protected DatastoreAdapter dba;

  /** Factory for identifiers for this datastore. */
  protected IdentifierFactory identifierFactory;

  /** Default catalog name for the datastore. */
  protected String catalogName = null;

  /** Default schema name for the datastore. */
  protected String schemaName = null;

  /** Manager for the mapping between Java and datastore types. */
  protected MappingManager mappingManager;

  /**
     * Map of DatastoreClass keyed by StateManager, for objects currently being inserted.
     * Defines to what level an object is inserted in the datastore.
     */
  protected Map<DNStateManager, DatastoreClass> insertedDatastoreClassByStateManager = new ConcurrentHashMap<>();

  /** 
     * Lock object aimed at providing a lock on the schema definition managed here, preventing
     * reads while it is being updated etc.
     */
  protected ReadWriteLock schemaLock = new ReentrantReadWriteLock();

  /** Controller for SQL executed on this store. */
  private SQLController sqlController = null;

  /** Factory for expressions using the generic query SQL mechanism. */
  protected SQLExpressionFactory expressionFactory;

  /** Calendar for this datastore. */
  private transient Calendar dateTimezoneCalendar = null;

  /**
     * The active class adder transaction, if any. Some RDBMSManager methods are called recursively in the course
     * of adding new classes. This field allows such methods to coordinate with the active ClassAdder transaction.
     * Write access is controlled via the "lock" object.
     */
  private ClassAdder classAdder = null;

  /** Writer for use when this RDBMSManager is configured to write DDL. */
  private Writer ddlWriter = null;

  /** Flag for when generating schema as DDL and wanting complete DDL (as opposed to upgrade DDL). */
  private boolean completeDDL = false;

  /**  DDL statements already written when in DDL mode. Used to eliminate dupe statements from bidir relations. */
  private Set<String> writtenDdlStatements = null;

  /** State variable for schema generation of the callback information to be processed. TODO Move to ClassTable. */
  private Map<String, Collection<AbstractMemberMetaData>> schemaCallbacks = new HashMap<>();

  private Map<String, Store> backingStoreByMemberName = new ConcurrentHashMap<>();

  private static final ConcurrentFixedCache<String, NoTableManagedException> noTableManagedExceptionCache = new ConcurrentFixedCache<>(NoTableManagedException::new);

  private static Map<String, DiscriminatorDefiner> discriminatorDefinerMap = new ConcurrentHashMap<>();

  /**
     * Constructs a new RDBMSManager. 
     * On successful return the new RDBMSManager will have successfully connected to the database with the given
     * credentials and determined the schema name, but will not have inspected the schema contents any further. 
     * The contents (tables, views, etc.) will be subsequently created and/or validated on-demand as the application
     * accesses persistent classes.
     * @param clr the ClassLoaderResolver
     * @param ctx The corresponding Context. This factory's non-tx data source will be used to get database connections as needed to perform management functions.
     * @param props Properties for the datastore
     * @exception NucleusDataStoreException If the database could not be accessed or the name of the schema could not be determined.
     */
  public RDBMSStoreManager(ClassLoaderResolver clr, PersistenceNucleusContext ctx, Map<String, Object> props) {
    super("rdbms", clr, ctx, props);

<<<<<<< Unknown file: This is a bug in JDime.
=======
    persistenceHandler = createPersistenceHandler();
>>>>>>> /usr/src/app/output/datanucleus/datanucleus-rdbms/b4f243ee7357b22c82759725b42e2532be164f33/src/main/java/org/datanucleus/store/rdbms/RDBMSStoreManager.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    flushProcess = createFlushProcess();
>>>>>>> /usr/src/app/output/datanucleus/datanucleus-rdbms/b4f243ee7357b22c82759725b42e2532be164f33/src/main/java/org/datanucleus/store/rdbms/RDBMSStoreManager.java/right.java


<<<<<<< /usr/src/app/output/datanucleus/datanucleus-rdbms/b4f243ee7357b22c82759725b42e2532be164f33/src/main/java/org/datanucleus/store/rdbms/RDBMSStoreManager.java/left.java
    initRDBMSStoreManager(clr, ctx, props)
=======
    schemaHandler = createSchemaHandler()
>>>>>>> /usr/src/app/output/datanucleus/datanucleus-rdbms/b4f243ee7357b22c82759725b42e2532be164f33/src/main/java/org/datanucleus/store/rdbms/RDBMSStoreManager.java/right.java
    ;
  }

  protected void initRDBMSStoreManager(ClassLoaderResolver clr, PersistenceNucleusContext ctx, Map<String, Object> props) {
    persistenceHandler = createPersistenceHandler();
    flushProcess = createFlushProcess();
    schemaHandler = createSchemaHandler();
    try {
      ManagedConnection mc = connectionMgr.getConnection(-1);
      Connection conn = (Connection) mc.getConnection();
      if (conn == null) {
        throw new NucleusDataStoreException(Localiser.msg("050007"));
      }
      try {
        dba = DatastoreAdapterFactory.getInstance().getDatastoreAdapter(clr, conn, getStringProperty(RDBMSPropertyNames.PROPERTY_RDBMS_DATASTORE_ADAPTER_CLASS_NAME), ctx.getPluginManager());
        dba.initialise(schemaHandler, mc);
        if (hasPropertyNotNull(PropertyNames.PROPERTY_MAPPING_CATALOG)) {
          if (!dba.supportsOption(DatastoreAdapter.CATALOGS_IN_TABLE_DEFINITIONS)) {
            NucleusLogger.DATASTORE.warn(Localiser.msg("050002", getStringProperty(PropertyNames.PROPERTY_MAPPING_CATALOG)));
          } else {
            catalogName = getStringProperty(PropertyNames.PROPERTY_MAPPING_CATALOG);
          }
        }
        if (hasPropertyNotNull(PropertyNames.PROPERTY_MAPPING_SCHEMA)) {
          if (!dba.supportsOption(DatastoreAdapter.SCHEMAS_IN_TABLE_DEFINITIONS)) {
            NucleusLogger.DATASTORE.warn(Localiser.msg("050003", getStringProperty(PropertyNames.PROPERTY_MAPPING_SCHEMA)));
          } else {
            schemaName = getStringProperty(PropertyNames.PROPERTY_MAPPING_SCHEMA);
          }
        }
        initialiseIdentifierFactory(ctx);
        if (schemaName != null) {
          String validSchemaName = identifierFactory.getIdentifierInAdapterCase(schemaName);
          if (!validSchemaName.equals(schemaName)) {
            NucleusLogger.DATASTORE_SCHEMA.warn(Localiser.msg("020192", "schema", schemaName, validSchemaName));
            schemaName = validSchemaName;
          }
        }
        if (catalogName != null) {
          String validCatalogName = identifierFactory.getIdentifierInAdapterCase(catalogName);
          if (!validCatalogName.equals(catalogName)) {
            NucleusLogger.DATASTORE_SCHEMA.warn(Localiser.msg("020192", "catalog", catalogName, validCatalogName));
            catalogName = validCatalogName;
          }
        }
        sqlController = new SQLController(dba.supportsOption(DatastoreAdapter.STATEMENT_BATCHING), getIntProperty(RDBMSPropertyNames.PROPERTY_RDBMS_STATEMENT_BATCH_LIMIT), getIntProperty(PropertyNames.PROPERTY_DATASTORE_READ_TIMEOUT), StatementLoggingType.valueOf(getStringProperty(RDBMSPropertyNames.PROPERTY_RDBMS_STATEMENT_LOGGING)));
        initialiseSchema(conn, clr);
        logConfiguration();
      } catch (Exception e) {
        NucleusLogger.GENERAL.info("Error in initialisation of RDBMSStoreManager", e);
        throw e;
      } finally {
        mc.release();
      }
    } catch (NucleusException ne) {
      NucleusLogger.DATASTORE_SCHEMA.error(Localiser.msg("050004"), ne);
      throw ne.setFatal();
    } catch (Exception e1) {
      String msg = Localiser.msg("050004") + ' ' + Localiser.msg("050006") + ' ' + Localiser.msg("048000", e1);
      NucleusLogger.DATASTORE_SCHEMA.error(msg, e1);
      throw new NucleusUserException(msg, e1).setFatal();
    }
  }

  protected RDBMSSchemaHandler createSchemaHandler() {
    return new RDBMSSchemaHandler(this);
  }

  protected static FlushOrdered createFlushProcess() {
    return new FlushOrdered();
  }

  protected RDBMSPersistenceHandler createPersistenceHandler() {
    return new RDBMSPersistenceHandler(this);
  }

  public String getQueryCacheKey() {
    return getStoreManagerKey() + "-" + getDatastoreAdapter().getVendorID();
  }

  /**
     * Method to create the IdentifierFactory to be used by this store.
     * Relies on the datastore adapter existing before creation
     * @param nucleusContext context
     */
  protected void initialiseIdentifierFactory(NucleusContext nucleusContext) {
    if (dba == null) {
      throw new NucleusException("DatastoreAdapter not yet created so cannot create IdentifierFactory!");
    }
    String idFactoryName = getStringProperty(PropertyNames.PROPERTY_IDENTIFIER_FACTORY);
    try {
      Map<Object, Object> props = new HashMap<>();
      if (catalogName != null) {
        props.put(IdentifierFactory.PROPERTY_DEFAULT_CATALOG, catalogName);
      }
      if (schemaName != null) {
        props.put(IdentifierFactory.PROPERTY_DEFAULT_SCHEMA, schemaName);
      }
      String val = getStringProperty(PropertyNames.PROPERTY_IDENTIFIER_CASE);
      props.put(IdentifierFactory.PROPERTY_REQUIRED_CASE, val != null ? val : getDefaultIdentifierCase());
      val = getStringProperty(PropertyNames.PROPERTY_IDENTIFIER_WORD_SEPARATOR);
      if (val != null) {
        props.put(IdentifierFactory.PROPERTY_WORD_SEPARATOR, val);
      }
      val = getStringProperty(PropertyNames.PROPERTY_IDENTIFIER_TABLE_PREFIX);
      if (val != null) {
        props.put(IdentifierFactory.PROPERTY_TABLE_PREFIX, val);
      }
      val = getStringProperty(PropertyNames.PROPERTY_IDENTIFIER_TABLE_SUFFIX);
      if (val != null) {
        props.put(IdentifierFactory.PROPERTY_TABLE_SUFFIX, val);
      }
      props.put(IdentifierFactory.PROPERTY_NAMING_FACTORY, getNamingFactory());
      ClassLoaderResolver clr = nucleusContext.getClassLoaderResolver(null);
      if ("datanucleus2".equalsIgnoreCase(idFactoryName)) {
        identifierFactory = new DN2IdentifierFactory(dba, clr, props);
      } else {
        if ("jpa".equalsIgnoreCase(idFactoryName) || "jakarta".equalsIgnoreCase(idFactoryName)) {
          identifierFactory = new JPAIdentifierFactory(dba, clr, props);
        } else {
          if ("datanucleus1".equalsIgnoreCase(idFactoryName)) {
            identifierFactory = new DNIdentifierFactory(dba, clr, props);
          } else {
            if ("jpox".equalsIgnoreCase(idFactoryName)) {
              identifierFactory = new JPOXIdentifierFactory(dba, clr, props);
            } else {
              Class[] argTypes = new Class[] { DatastoreAdapter.class, ClassConstants.CLASS_LOADER_RESOLVER, Map.class };
              Object[] args = new Object[] { dba, nucleusContext.getClassLoaderResolver(null), props };
              identifierFactory = (IdentifierFactory) nucleusContext.getPluginManager().createExecutableExtension("org.datanucleus.store.rdbms.identifierfactory", "name", idFactoryName, "class-name", argTypes, args);
            }
          }
        }
      }
    } catch (ClassNotFoundException cnfe) {
      throw new NucleusUserException(Localiser.msg("039004", idFactoryName), cnfe).setFatal();
    } catch (Exception e) {
      NucleusLogger.PERSISTENCE.error("Exception creating IdentifierFactory", e);
      throw new NucleusException(Localiser.msg("039005", idFactoryName), e).setFatal();
    }
  }

  /**
     * Accessor for the factory for creating identifiers (table/column names etc).
     * @return Identifier factory
     */
  public IdentifierFactory getIdentifierFactory() {
    return identifierFactory;
  }

  /**
     * Gets the DatastoreAdapter to use for this store.
     * @return Returns the DatastoreAdapter
     */
  public DatastoreAdapter getDatastoreAdapter() {
    return dba;
  }

  /**
     * Gets the MappingManager to use for this store.
     * @return Returns the MappingManager.
     */
  public MappingManager getMappingManager() {
    if (mappingManager == null) {
      mappingManager = dba.getMappingManager(this);
    }
    return mappingManager;
  }

  @Override public String getDefaultStateManagerClassName() {
    return ReferentialStateManagerImpl.class.getName();
  }

  /**
     * Utility to return all StoreData for a Datastore Container identifier.
     * Returns StoreData with this table identifier and where the class is the owner of the table.
     * @param tableIdentifier Identifier for the table
     * @return The StoreData for this table (if managed).
     */
  public StoreData[] getStoreDataForDatastoreContainerObject(DatastoreIdentifier tableIdentifier) {
    schemaLock.readLock().lock();
    try {
      return storeDataMgr.getStoreDataForProperties("tableId", tableIdentifier, "table-owner", "true");
    }  finally {
      schemaLock.readLock().unlock();
    }
  }

  /**
     * Returns the datastore container (table) for the specified field. 
     * Returns 'null' if the field is not (yet) known to the store manager.
     * Note : if we have an embedded object (embedded into something else) that has a member which requires a join table, then this method will not cater
     * for having different tables for the different places that the object can be embedded.
     * @param mmd The metadata for the field.
     * @return The corresponding datastore container, or 'null'.
     */
  public Table getTable(AbstractMemberMetaData mmd) {
    schemaLock.readLock().lock();
    try {
      StoreData sd = storeDataMgr.get(mmd);
      if (sd != null && sd instanceof RDBMSStoreData) {
        return (Table) sd.getTable();
      }
      return null;
    }  finally {
      schemaLock.readLock().unlock();
    }
  }

  /**
     * Returns the primary datastore table serving as backing for the given class. 
     * If the class is not yet known to the store manager, {@link #manageClasses} is called
     * to add it. Classes which have inheritance strategy of "new-table" and
     * "superclass-table" will return a table here, whereas "subclass-table" will
     * return null since it doesn't have a table as such.
     * <p>
     * @param className Name of the class whose table is be returned.
     * @param clr The ClassLoaderResolver
     * @return The corresponding class table.
     * @exception NoTableManagedException If the given class has no table managed in the database.
     */
  public DatastoreClass getDatastoreClass(String className, ClassLoaderResolver clr) {
    DatastoreClass ct = null;
    if (className == null) {
      NucleusLogger.PERSISTENCE.error(Localiser.msg("032015"));
      return null;
    }
    schemaLock.readLock().lock();
    try {
      StoreData sd = storeDataMgr.get(className);
      if (sd != null && sd instanceof RDBMSStoreData) {
        ct = (DatastoreClass) sd.getTable();
        if (ct != null) {
          return ct;
        }
      }
    }  finally {
      schemaLock.readLock().unlock();
    }
    boolean toBeAdded = false;
    if (clr != null) {
      Class cls = clr.classForName(className);
      ApiAdapter api = getApiAdapter();
      if (cls != null && !cls.isInterface() && api.isPersistable(cls)) {
        toBeAdded = true;
      }
    } else {
      toBeAdded = true;
    }
    boolean classKnown = false;
    if (toBeAdded) {
      manageClasses(clr, className);
      schemaLock.readLock().lock();
      try {
        StoreData sd = storeDataMgr.get(className);
        if (sd != null && sd instanceof RDBMSStoreData) {
          classKnown = true;
          ct = (DatastoreClass) sd.getTable();
        }
      }  finally {
        schemaLock.readLock().unlock();
      }
    }
    if (!classKnown && ct == null) {
      throw noTableManagedExceptionCache.get(className);
    }
    return ct;
  }

  /**
     * Returns the datastore table having the given identifier.
     * Returns 'null' if no such table is (yet) known to the store manager.
     * @param name The identifier name of the table.
     * @return The corresponding table, or 'null'
     */
  public DatastoreClass getDatastoreClass(DatastoreIdentifier name) {
    schemaLock.readLock().lock();
    try {
      for (StoreData sd : storeDataMgr.getManagedStoreData()) {
        if (sd instanceof RDBMSStoreData) {
          RDBMSStoreData tsd = (RDBMSStoreData) sd;
          if (tsd.hasTable() && tsd.getDatastoreIdentifier().equals(name)) {
            return (DatastoreClass) tsd.getTable();
          }
        }
      }
      return null;
    }  finally {
      schemaLock.readLock().unlock();
    }
  }

  /**
     * Method to return the class(es) that has a table managing the persistence of the fields of the supplied class. 
     * For the 3 inheritance strategies, the following occurs :-
     * <UL>
     * <LI>new-table : will return the same ClassMetaData</LI>
     * <LI>subclass-table : will return all subclasses that have a table managing its fields</LI>
     * <LI>superclass-table : will return the next superclass that has a table</LI>
     * </UL>
     * @param cmd The supplied class.
     * @param clr ClassLoader resolver
     * @return The ClassMetaData's managing the fields of the supplied class
     */
  public AbstractClassMetaData[] getClassesManagingTableForClass(AbstractClassMetaData cmd, ClassLoaderResolver clr) {
    if (cmd == null) {
      return null;
    }
    if (cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.COMPLETE_TABLE || cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.NEW_TABLE) {
      return new AbstractClassMetaData[] { cmd };
    } else {
      if (cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
        String[] subclasses = getMetaDataManager().getSubclassesForClass(cmd.getFullClassName(), true);
        if (subclasses != null) {
          for (int i = 0; i < subclasses.length; i++) {
            if (!storeDataMgr.managesClass(subclasses[i])) {
              manageClasses(clr, subclasses[i]);
            }
          }
        }
        Set<AbstractClassMetaData> managingClasses = new HashSet<>();
        for (StoreData sd : storeDataMgr.getManagedStoreData()) {
          if (sd.isFCO() && ((AbstractClassMetaData) sd.getMetaData()).getSuperAbstractClassMetaData() != null && ((AbstractClassMetaData) sd.getMetaData()).getSuperAbstractClassMetaData().getFullClassName().equals(cmd.getFullClassName())) {
            AbstractClassMetaData[] superCmds = getClassesManagingTableForClass((AbstractClassMetaData) sd.getMetaData(), clr);
            if (superCmds != null) {
              for (int i = 0; i < superCmds.length; i++) {
                managingClasses.add(superCmds[i]);
              }
            }
          }
        }
        Iterator<AbstractClassMetaData> managingClassesIter = managingClasses.iterator();
        AbstractClassMetaData managingCmds[] = new AbstractClassMetaData[managingClasses.size()];
        int i = 0;
        while (managingClassesIter.hasNext()) {
          managingCmds[i++] = managingClassesIter.next();
        }
        return managingCmds;
      } else {
        if (cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
          return getClassesManagingTableForClass(cmd.getSuperAbstractClassMetaData(), clr);
        }
      }
    }
    return null;
  }

  /**
     * Accessor for whether the specified field of the object is inserted in the datastore yet.
     * @param sm StateManager for the object
     * @param fieldNumber (Absolute) field number for the object
     * @return Whether it is persistent
     */
  public boolean isObjectInserted(DNStateManager sm, int fieldNumber) {
    if (sm == null) {
      return false;
    }
    if (!sm.isInserting()) {
      return true;
    }
    DatastoreClass latestTable = insertedDatastoreClassByStateManager.get(sm);
    if (latestTable == null) {
      return false;
    }
    AbstractMemberMetaData mmd = sm.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
    if (mmd == null) {
      return false;
    }
    String className = mmd.getClassName();
    if (mmd.isPrimaryKey()) {
      className = sm.getObject().getClass().getName();
    }
    DatastoreClass datastoreCls = latestTable;
    while (datastoreCls != null) {
      if (datastoreCls.managesClass(className)) {
        return true;
      }
      datastoreCls = datastoreCls.getSuperDatastoreClass();
    }
    return false;
  }

  /**
     * Returns whether this object is inserted in the datastore far enough to be considered to be the supplied type. 
     * For example if we have base class A, B extends A and this object is a B, and we pass in A here then this returns 
     * whether the A part of the object is now inserted.
     * @param sm StateManager for the object
     * @param className Name of class that we want to check the insertion level for.
     * @return Whether the object is inserted in the datastore to this level
     */
  public boolean isObjectInserted(DNStateManager sm, String className) {
    if (sm == null) {
      return false;
    }
    if (!sm.isInserting()) {
      return false;
    }
    DatastoreClass latestTable = insertedDatastoreClassByStateManager.get(sm);
    if (latestTable != null) {
      DatastoreClass datastoreCls = latestTable;
      while (datastoreCls != null) {
        if (datastoreCls.managesClass(className)) {
          return true;
        }
        datastoreCls = datastoreCls.getSuperDatastoreClass();
      }
    }
    return false;
  }

  /**
     * Method to set that the specified object is inserted down to the defined datastore class.
     * When the object is fully inserted (the table is the primary table for this object type)
     * it is removed from the map of objects being inserted.
     * @param sm StateManager for the object
     * @param table Table to which it is now inserted
     */
  public void setObjectIsInsertedToLevel(DNStateManager sm, DatastoreClass table) {
    insertedDatastoreClassByStateManager.put(sm, table);
    if (table.managesClass(sm.getClassMetaData().getFullClassName())) {
      sm.setInsertingCallbacks();
      insertedDatastoreClassByStateManager.remove(sm);
    }
  }

  public Store getExistingBackingStoreForMember(AbstractMemberMetaData mmd) {
    return backingStoreByMemberName.get(mmd.getFullFieldName());
  }

  /**
     * Accessor for the backing store for the specified member.
     * Note : if we have an embedded object that is embedded into some other type and the object has a member that requires a join table (backing store), this method
     * will not cater for the different places that can be embedded.
     * @param clr The ClassLoaderResolver
     * @param mmd metadata for the member to be persisted by this Store
     * @param type instantiated type or prefered type
     * @return The backing store
     */
  public Store getBackingStoreForField(ClassLoaderResolver clr, AbstractMemberMetaData mmd, Class type) {
    if (mmd == null || mmd.isSerialized()) {
      return null;
    }
    Store store = backingStoreByMemberName.get(mmd.getFullFieldName());
    if (store != null) {
      return store;
    }
    synchronized (backingStoreByMemberName) {
      store = backingStoreByMemberName.get(mmd.getFullFieldName());
      if (store != null) {
        return store;
      }
      Class expectedMappingType = null;
      if (mmd.hasMap()) {
        expectedMappingType = MapMapping.class;
      } else {
        if (mmd.hasArray()) {
          expectedMappingType = ArrayMapping.class;
        } else {
          if (mmd.hasCollection()) {
            expectedMappingType = CollectionMapping.class;
          } else {
            expectedMappingType = PersistableMapping.class;
          }
        }
      }
      try {
        DatastoreClass ownerTable = getDatastoreClass(mmd.getClassName(), clr);
        if (ownerTable == null) {
          AbstractClassMetaData fieldTypeCmd = getMetaDataManager().getMetaDataForClass(mmd.getClassName(), clr);
          AbstractClassMetaData[] tableOwnerCmds = getClassesManagingTableForClass(fieldTypeCmd, clr);
          if (tableOwnerCmds != null && tableOwnerCmds.length == 1) {
            ownerTable = getDatastoreClass(tableOwnerCmds[0].getFullClassName(), clr);
          }
        }
        if (ownerTable != null) {
          JavaTypeMapping m = ownerTable.getMemberMapping(mmd);
          if (!expectedMappingType.isAssignableFrom(m.getClass())) {
            String requiredType = type != null ? type.getName() : mmd.getTypeName();
            NucleusLogger.PERSISTENCE.warn("Member " + mmd.getFullFieldName() + " in table=" + ownerTable + " has mapping=" + m + " but expected mapping type=" + expectedMappingType);
            throw new IncompatibleFieldTypeException(mmd.getFullFieldName(), requiredType, m.getType());
          }
        }
      } catch (NoTableManagedException ntme) {
      }
      if (mmd.hasMap()) {
        Table datastoreTable = getTable(mmd);
        if (datastoreTable == null) {
          store = new FKMapStore(mmd, this, clr);
        } else {
          store = new JoinMapStore((MapTable) datastoreTable, clr);
        }
      } else {
        if (mmd.hasArray()) {
          Table datastoreTable = getTable(mmd);
          if (datastoreTable != null) {
            store = new JoinArrayStore(mmd, (ArrayTable) datastoreTable, clr);
          } else {
            store = new FKArrayStore(mmd, this, clr);
          }
        } else {
          if (mmd.hasCollection()) {
            Table datastoreTable = getTable(mmd);
            if (type == null) {
              if (datastoreTable == null) {
                if (Set.class.isAssignableFrom(mmd.getType())) {
                  store = new FKSetStore(mmd, this, clr);
                } else {
                  if (List.class.isAssignableFrom(mmd.getType()) || Queue.class.isAssignableFrom(mmd.getType())) {
                    store = new FKListStore(mmd, this, clr);
                  } else {
                    if (mmd.getOrderMetaData() != null) {
                      store = new FKListStore(mmd, this, clr);
                    } else {
                      store = new FKSetStore(mmd, this, clr);
                    }
                  }
                }
              } else {
                if (Set.class.isAssignableFrom(mmd.getType())) {
                  store = new JoinSetStore(mmd, (CollectionTable) datastoreTable, clr);
                } else {
                  if (List.class.isAssignableFrom(mmd.getType()) || Queue.class.isAssignableFrom(mmd.getType())) {
                    store = new JoinListStore(mmd, (CollectionTable) datastoreTable, clr);
                  } else {
                    if (mmd.getOrderMetaData() != null) {
                      store = new JoinListStore(mmd, (CollectionTable) datastoreTable, clr);
                    } else {
                      store = new JoinSetStore(mmd, (CollectionTable) datastoreTable, clr);
                    }
                  }
                }
              }
            } else {
              if (datastoreTable == null) {
                if (SCOUtils.isListBased(type)) {
                  store = new FKListStore(mmd, this, clr);
                } else {
                  store = new FKSetStore(mmd, this, clr);
                }
              } else {
                if (SCOUtils.isListBased(type)) {
                  store = new JoinListStore(mmd, (CollectionTable) datastoreTable, clr);
                } else {
                  store = new JoinSetStore(mmd, (CollectionTable) datastoreTable, clr);
                }
              }
            }
          } else {
            store = new JoinPersistableRelationStore(mmd, (PersistableJoinTable) getTable(mmd), clr);
          }
        }
      }
      backingStoreByMemberName.put(mmd.getFullFieldName(), store);
      return store;
    }
  }

  /**
     * Method to return the default identifier case.
     * @return Identifier case to use if not specified by the user
     */
  public String getDefaultIdentifierCase() {
    return "UPPERCASE";
  }

  public Map<String, Collection<AbstractMemberMetaData>> getSchemaCallbacks() {
    return schemaCallbacks;
  }

  public void addSchemaCallback(String className, AbstractMemberMetaData mmd) {
    Collection<AbstractMemberMetaData> coll = schemaCallbacks.get(className);
    if (coll == null) {
      coll = new HashSet<>();
      coll.add(mmd);
      schemaCallbacks.put(className, coll);
    } else {
      if (!coll.contains(mmd)) {
        coll.add(mmd);
      } else {
        NucleusLogger.DATASTORE_SCHEMA.debug("RDBMSStoreManager.addSchemaCallback called for " + mmd.getFullFieldName() + " on class=" + className + " but already registered");
      }
    }
  }

  @Override public boolean isJdbcStore() {
    return true;
  }

  @Override public String getNativeQueryLanguage() {
    return QueryLanguage.SQL.name();
  }

  @Override public Collection<String> getSupportedQueryLanguages() {
    Collection<String> languages = super.getSupportedQueryLanguages();
    languages.add(QueryLanguage.SQL.name());
    languages.add(QueryLanguage.STOREDPROC.name());
    return languages;
  }

  @Override public boolean supportsQueryLanguage(String language) {
    if (language != null && (language.equals(QueryLanguage.JDOQL.name()) || language.equals(QueryLanguage.JPQL.name()) || language.equals(QueryLanguage.SQL.name()) || language.equals(QueryLanguage.STOREDPROC.name()))) {
      return true;
    }
    return false;
  }

  @Override public Query newQuery(String language, ExecutionContext ec) {
    if (language.equals(QueryLanguage.JDOQL.name())) {
      return new JDOQLQuery(this, ec);
    } else {
      if (language.equals(QueryLanguage.JPQL.name())) {
        return new JPQLQuery(this, ec);
      } else {
        if (language.equals(QueryLanguage.SQL.name())) {
          return new SQLQuery(this, ec);
        } else {
          if (language.equals(QueryLanguage.STOREDPROC.name())) {
            return new StoredProcedureQuery(this, ec);
          }
        }
      }
    }
    throw new NucleusException("Error creating query for language " + language);
  }

  @Override public Query newQuery(String language, ExecutionContext ec, String queryString) {
    if (language.equals(QueryLanguage.JDOQL.name())) {
      return new JDOQLQuery(this, ec, queryString);
    } else {
      if (language.equals(QueryLanguage.JPQL.name())) {
        return new JPQLQuery(this, ec, queryString);
      } else {
        if (language.equals(QueryLanguage.SQL.name())) {
          return new SQLQuery(this, ec, queryString);
        } else {
          if (language.equals(QueryLanguage.STOREDPROC.name())) {
            return new StoredProcedureQuery(this, ec, queryString);
          }
        }
      }
    }
    throw new NucleusException("Error creating query for language " + language);
  }

  @Override public Query newQuery(String language, ExecutionContext ec, Query q) {
    if (language.equals(QueryLanguage.JDOQL.name())) {
      return new JDOQLQuery(this, ec, (JDOQLQuery) q);
    } else {
      if (language.equals(QueryLanguage.JPQL.name())) {
        return new JPQLQuery(this, ec, (JPQLQuery) q);
      } else {
        if (language.equals(QueryLanguage.SQL.name())) {
          return new SQLQuery(this, ec, (SQLQuery) q);
        } else {
          if (language.equals(QueryLanguage.STOREDPROC.name())) {
            return new StoredProcedureQuery(this, ec, (StoredProcedureQuery) q);
          }
        }
      }
    }
    throw new NucleusException("Error creating query for language " + language);
  }

  /**
     * Convenience method to log the configuration of this store manager.
     */
  protected void logConfiguration() {
    super.logConfiguration();
    if (NucleusLogger.DATASTORE.isDebugEnabled()) {
      NucleusLogger.DATASTORE.debug("Datastore Adapter : " + dba.getClass().getName());
      NucleusLogger.DATASTORE.debug("Datastore : name=\"" + dba.getDatastoreProductName() + "\" version=\"" + dba.getDatastoreProductVersion() + "\"");
      NucleusLogger.DATASTORE.debug("Datastore Driver : name=\"" + dba.getDatastoreDriverName() + "\" version=\"" + dba.getDatastoreDriverVersion() + "\"");
      String primaryDS = null;
      if (getConnectionFactory() != null) {
        primaryDS = "DataSource[input DataSource]";
      } else {
        if (getConnectionFactoryName() != null) {
          primaryDS = "JNDI[" + getConnectionFactoryName() + "]";
        } else {
          primaryDS = "URL[" + getConnectionURL() + "]";
        }
      }
      NucleusLogger.DATASTORE.debug("Primary Connection Factory : " + primaryDS);
      String secondaryDS = null;
      if (getConnectionFactory2() != null) {
        secondaryDS = "DataSource[input DataSource]";
      } else {
        if (getConnectionFactory2Name() != null) {
          secondaryDS = "JNDI[" + getConnectionFactory2Name() + "]";
        } else {
          if (getConnectionURL() != null) {
            secondaryDS = "URL[" + getConnectionURL() + "]";
          } else {
            secondaryDS = primaryDS;
          }
        }
      }
      NucleusLogger.DATASTORE.debug("Secondary Connection Factory : " + secondaryDS);
      if (identifierFactory != null) {
        NucleusLogger.DATASTORE.debug("Datastore Identifiers :" + " factory=\"" + getStringProperty(PropertyNames.PROPERTY_IDENTIFIER_FACTORY) + "\"" + " case=" + identifierFactory.getNamingCase().toString() + (catalogName != null ? (" catalog=" + catalogName) : "") + (schemaName != null ? (" schema=" + schemaName) : ""));
        NucleusLogger.DATASTORE.debug("Supported Identifier Cases : " + (dba.supportsOption(DatastoreAdapter.IDENTIFIERS_LOWERCASE) ? "lowercase " : "") + (dba.supportsOption(DatastoreAdapter.IDENTIFIERS_LOWERCASE_QUOTED) ? "\"lowercase\" " : "") + (dba.supportsOption(DatastoreAdapter.IDENTIFIERS_MIXEDCASE) ? "MixedCase " : "") + (dba.supportsOption(DatastoreAdapter.IDENTIFIERS_MIXEDCASE_QUOTED) ? "\"MixedCase\" " : "") + (dba.supportsOption(DatastoreAdapter.IDENTIFIERS_UPPERCASE) ? "UPPERCASE " : "") + (dba.supportsOption(DatastoreAdapter.IDENTIFIERS_UPPERCASE_QUOTED) ? "\"UPPERCASE\" " : "") + (dba.supportsOption(DatastoreAdapter.IDENTIFIERS_MIXEDCASE_SENSITIVE) ? "MixedCase-Sensitive " : "") + (dba.supportsOption(DatastoreAdapter.IDENTIFIERS_MIXEDCASE_QUOTED_SENSITIVE) ? "\"MixedCase-Sensitive\" " : ""));
        NucleusLogger.DATASTORE.debug("Supported Identifier Lengths (max) :" + " Table=" + dba.getDatastoreIdentifierMaxLength(IdentifierType.TABLE) + " Column=" + dba.getDatastoreIdentifierMaxLength(IdentifierType.COLUMN) + " Constraint=" + dba.getDatastoreIdentifierMaxLength(IdentifierType.CANDIDATE_KEY) + " Index=" + dba.getDatastoreIdentifierMaxLength(IdentifierType.INDEX) + " Delimiter=" + dba.getIdentifierQuoteString());
        NucleusLogger.DATASTORE.debug("Support for Identifiers in DDL :" + " catalog=" + dba.supportsOption(DatastoreAdapter.CATALOGS_IN_TABLE_DEFINITIONS) + " schema=" + dba.supportsOption(DatastoreAdapter.SCHEMAS_IN_TABLE_DEFINITIONS));
      }
      NucleusLogger.DATASTORE.debug("Datastore : " + "rdbmsConstraintCreateMode=" + getStringProperty(RDBMSPropertyNames.PROPERTY_RDBMS_CONSTRAINT_CREATE_MODE) + ", initialiseColumnInfo=" + getStringProperty(RDBMSPropertyNames.PROPERTY_RDBMS_INIT_COLUMN_INFO) + (getBooleanProperty(RDBMSPropertyNames.PROPERTY_RDBMS_CHECK_EXISTS_TABLES_VIEWS) ? ", checkTableViewExistence" : ""));
      int batchLimit = getIntProperty(RDBMSPropertyNames.PROPERTY_RDBMS_STATEMENT_BATCH_LIMIT);
      boolean supportBatching = dba.supportsOption(DatastoreAdapter.STATEMENT_BATCHING);
      if (supportBatching) {
        NucleusLogger.DATASTORE.debug("Support Statement Batching : yes (max-batch-size=" + (batchLimit == -1 ? "UNLIMITED" : "" + batchLimit) + ")");
      } else {
        NucleusLogger.DATASTORE.debug("Support Statement Batching : no");
      }
      NucleusLogger.DATASTORE.debug("Queries : Results " + "direction=" + getStringProperty(RDBMSPropertyNames.PROPERTY_RDBMS_QUERY_FETCH_DIRECTION) + ", type=" + getStringProperty(RDBMSPropertyNames.PROPERTY_RDBMS_QUERY_RESULT_SET_TYPE) + ", concurrency=" + getStringProperty(RDBMSPropertyNames.PROPERTY_RDBMS_QUERY_RESULT_SET_CONCURRENCY));
      NucleusLogger.DATASTORE.debug("Java-Types : string-default-length=" + getIntProperty(RDBMSPropertyNames.PROPERTY_RDBMS_STRING_DEFAULT_LENGTH));
      RDBMSTypesInfo typesInfo = (RDBMSTypesInfo) schemaHandler.getSchemaData(null, RDBMSSchemaHandler.TYPE_TYPES, null);
      if (typesInfo != null && typesInfo.getNumberOfChildren() > 0) {
        StringBuilder typeStr = new StringBuilder();
        Iterator<String> jdbcTypesIter = typesInfo.getChildren().keySet().iterator();
        while (jdbcTypesIter.hasNext()) {
          String jdbcTypeStr = jdbcTypesIter.next();
          int jdbcTypeNumber = 0;
          try {
            jdbcTypeNumber = Short.parseShort(jdbcTypeStr);
          } catch (NumberFormatException nfe) {
          }
          String typeName = dba.getNameForJDBCType(jdbcTypeNumber);
          if (typeName == null) {
            typeName = "[id=" + jdbcTypeNumber + "]";
          }
          typeStr.append(typeName);
          if (jdbcTypesIter.hasNext()) {
            typeStr.append(", ");
          }
        }
        NucleusLogger.DATASTORE.debug("JDBC-Types : " + typeStr);
      }
      NucleusLogger.DATASTORE.debug("===========================================================");
    }
  }

  /**
     * Release of resources
     */
  public synchronized void close() {
    dba = null;
    super.close();
    classAdder = null;
  }

  /**
     * Method to return a datastore sequence for this datastore matching the passed sequence MetaData.
     * @param ec execution context
     * @param seqmd SequenceMetaData
     * @return The Sequence
     */
  public NucleusSequence getNucleusSequence(ExecutionContext ec, SequenceMetaData seqmd) {
    return new NucleusSequenceImpl(ec, this, seqmd);
  }

  /**
     * Accessor for the SQL controller.
     * @return The SQL controller
     */
  public SQLController getSQLController() {
    return sqlController;
  }

  /**
     * Accessor for the SQL expression factory to use when generating SQL statements.
     * @return SQL expression factory
     */
  public SQLExpressionFactory getSQLExpressionFactory() {
    if (expressionFactory == null) {
      expressionFactory = new SQLExpressionFactory(this);
    }
    return expressionFactory;
  }

  /**
     * Initialises the schema name for the datastore, and (optionally) the schema table.
     * @param conn A connection to the database
     * @param clr ClassLoader resolver
     */
  private void initialiseSchema(Connection conn, ClassLoaderResolver clr) throws Exception {
    if (schemaName == null && catalogName == null) {
      if (dba.supportsOption(DatastoreAdapter.CATALOGS_IN_TABLE_DEFINITIONS) || dba.supportsOption(DatastoreAdapter.SCHEMAS_IN_TABLE_DEFINITIONS)) {
        try {
          try {
            catalogName = dba.getCatalogName(conn);
            schemaName = dba.getSchemaName(conn);
          } catch (UnsupportedOperationException e) {
            if (!getBooleanProperty(PropertyNames.PROPERTY_DATASTORE_READONLY) && getSchemaHandler().isAutoCreateTables()) {
              NucleusLogger.DATASTORE_SCHEMA.debug(Localiser.msg("020026"));
              ProbeTable pt = new ProbeTable(this);
              pt.initialize(clr);
              pt.create(conn);
              try {
                String[] schema_details = pt.findSchemaDetails(conn);
                if (schema_details != null) {
                  catalogName = schema_details[0];
                  schemaName = schema_details[1];
                }
              }  finally {
                pt.drop(conn);
              }
            }
          }
        } catch (SQLException e) {
          String msg = Localiser.msg("050005", e.getMessage()) + ' ' + Localiser.msg("050006");
          NucleusLogger.DATASTORE_SCHEMA.warn(msg);
        }
      }
    }
    if (getBooleanProperty(PropertyNames.PROPERTY_DATASTORE_READONLY)) {
      String autoStartMechanismName = nucleusContext.getConfiguration().getStringProperty(PropertyNames.PROPERTY_AUTOSTART_MECHANISM);
      if ("SchemaTable".equals(autoStartMechanismName)) {
        nucleusContext.getConfiguration().setProperty(PropertyNames.PROPERTY_AUTOSTART_MECHANISM, "None");
      }
    } else {
      dba.initialiseDatastore(conn);
    }
  }

  /**
     * Clears all knowledge of tables, cached requests, metadata, etc and resets
     * the store manager to its initial state.
     */
  private void clearSchemaData() {
    deregisterAllStoreData();
    schemaHandler.clear();
    ManagedConnection mc = connectionMgr.getConnection(-1);
    try {
      dba.initialiseTypes(schemaHandler, mc);
    }  finally {
      mc.release();
    }
    ((RDBMSPersistenceHandler) persistenceHandler).removeAllRequests();
  }

  @Override public String getDefaultCatalogName() {
    return catalogName;
  }

  @Override public String getDefaultSchemaName() {
    return schemaName;
  }

  /**
     * Get the date/time of the datastore.
     * @return Date/time of the datastore
     */
  public Date getDatastoreDate() {
    Date serverDate = null;
    String dateStmt = dba.getDatastoreDateStatement();
    ManagedConnection mconn = null;
    try {
      mconn = connectionMgr.getConnection(TransactionIsolation.NONE);
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
        ps = getSQLController().getStatementForQuery(mconn, dateStmt);
        rs = getSQLController().executeStatementQuery(null, mconn, dateStmt, ps);
        if (rs.next()) {
          Timestamp time = rs.getTimestamp(1, getCalendarForDateTimezone());
          serverDate = new Date(time.getTime());
        } else {
          return null;
        }
      } catch (SQLException sqle) {
        String msg = Localiser.msg("050052", sqle.getMessage());
        NucleusLogger.DATASTORE.warn(msg, sqle);
        throw new NucleusUserException(msg, sqle).setFatal();
      } finally {
        if (rs != null) {
          rs.close();
        }
        if (ps != null) {
          getSQLController().closeStatement(mconn, ps);
        }
      }
    } catch (SQLException sqle) {
      String msg = Localiser.msg("050052", sqle.getMessage());
      NucleusLogger.DATASTORE.warn(msg, sqle);
      throw new NucleusException(msg, sqle).setFatal();
    } finally {
      if (mconn != null) {
        mconn.release();
      }
    }
    return serverDate;
  }

  /**
     * Method to add several persistable classes to the store manager's set of supported classes.
     * This will create any necessary database objects (tables, views, constraints, indexes etc). 
     * This will also cause the addition of any related classes.
     * @param clr The ClassLoaderResolver
     * @param classNames Name of the class(es) to be added.
     */
  public void manageClasses(ClassLoaderResolver clr, String... classNames) {
    if (classNames == null || classNames.length == 0) {
      return;
    }
    boolean allManaged = true;
    for (int i = 0; i < classNames.length; i++) {
      if (!managesClass(classNames[i])) {
        allManaged = false;
        break;
      }
    }
    if (allManaged) {
      return;
    }
    classNames = getNucleusContext().getTypeManager().filterOutSupportedSecondClassNames(classNames);
    if (classNames.length == 0) {
      return;
    }
    try {
      schemaLock.writeLock().lock();
      if (classAdder != null) {
        classAdder.addClassTables(classNames, clr);
        return;
      }
      int isolationLevel = hasProperty(PropertyNames.PROPERTY_SCHEMA_TXN_ISOLATION) ? TransactionUtils.getTransactionIsolationLevelForName(getStringProperty(PropertyNames.PROPERTY_SCHEMA_TXN_ISOLATION)) : dba.getTransactionIsolationForSchemaCreation();
      new ClassAdder(classNames, null, isolationLevel).execute(clr);
    }  finally {
      schemaLock.writeLock().unlock();
    }
  }

  /**
     * Utility to remove all classes that we are managing.
     * @param clr The ClassLoaderResolver
     */
  public void unmanageAllClasses(ClassLoaderResolver clr) {
    int isolationLevel = hasProperty(PropertyNames.PROPERTY_SCHEMA_TXN_ISOLATION) ? TransactionUtils.getTransactionIsolationLevelForName(getStringProperty(PropertyNames.PROPERTY_SCHEMA_TXN_ISOLATION)) : Connection.TRANSACTION_READ_COMMITTED;
    DeleteTablesSchemaTransaction deleteTablesTxn = new DeleteTablesSchemaTransaction(this, isolationLevel, storeDataMgr);
    boolean success = true;
    try {
      deleteTablesTxn.execute(clr);
    } catch (NucleusException ne) {
      success = false;
      throw ne;
    } finally {
      if (success) {
        clearSchemaData();
      }
    }
  }

  /**
     * Accessor for the writer for DDL (if set).
     * @return DDL writer
     */
  public Writer getDdlWriter() {
    return ddlWriter;
  }

  /**
     * Accessor for whether we should generate complete DDL when in that mode.
     * Otherwise will generate "upgrade DDL".
     * @return Generate complete DDL ?
     */
  public boolean getCompleteDDL() {
    return completeDDL;
  }

  /**
     * When we are in SchemaTool DDL mode, return if the supplied statement is already present.
     * This is used to eliminate duplicate statements from a bidirectional relation.
     * @param stmt The statement
     * @return Whether we have that statement already
     */
  public boolean hasWrittenDdlStatement(String stmt) {
    return writtenDdlStatements != null && writtenDdlStatements.contains(stmt);
  }

  /**
     * When we are in SchemaTool DDL mode, add a new DDL statement.
     * @param stmt The statement
     */
  public void addWrittenDdlStatement(String stmt) {
    if (writtenDdlStatements != null) {
      writtenDdlStatements.add(stmt);
    }
  }

  /**
     * Utility to validate the specified table.
     * This is useful where we have made an update to the columns in a table and want to apply the updates to the datastore.
     * @param table The table to validate
     * @param clr The ClassLoaderResolver
     */
  public void validateTable(final TableImpl table, ClassLoaderResolver clr) {
    int isolationLevel = hasProperty(PropertyNames.PROPERTY_SCHEMA_TXN_ISOLATION) ? TransactionUtils.getTransactionIsolationLevelForName(getStringProperty(PropertyNames.PROPERTY_SCHEMA_TXN_ISOLATION)) : Connection.TRANSACTION_READ_COMMITTED;
    ValidateTableSchemaTransaction validateTblTxn = new ValidateTableSchemaTransaction(this, isolationLevel, table);
    validateTblTxn.execute(clr);
  }

  /**
     * Returns the class corresponding to the given object identity.
     * If the identity is a SCOID, return the SCO class.
     * If the identity is a DatastoreId then returns the associated persistable class name.
     * If the identity is a SingleFieldIdentity then returns the associated persistable class name.
     * If the object is an AppID PK, returns the associated PC class (as far as determinable).
     * If the object id is an application id and the user supplies the "ec" argument then a check can be performed in the datastore where necessary.
     * @param id The identity of some object.
     * @param clr ClassLoader resolver
     * @param ec execution context (optional - to allow check inheritance level in datastore)
     * @return For datastore identity, return the class of the corresponding object.
     *      For application identity, return the class of the corresponding object.
     *      Otherwise returns null if unable to tie as the identity of a particular class.
     */
  public String getClassNameForObjectID(Object id, ClassLoaderResolver clr, ExecutionContext ec) {
    if (id instanceof SCOID) {
      return ((SCOID) id).getSCOClass();
    }
    List<AbstractClassMetaData> rootCmds = new ArrayList<>();
    String className = IdentityUtils.getTargetClassNameForIdentity(id);
    if (className != null) {
      AbstractClassMetaData cmd = getMetaDataManager().getMetaDataForClass(className, clr);
      if (IdentityUtils.isDatastoreIdentity(id) && cmd.getIdentityType() != IdentityType.DATASTORE) {
        throw new NucleusUserException(Localiser.msg("002004", id, cmd.getFullClassName()));
      }
      if (IdentityUtils.isSingleFieldIdentity(id) && (cmd.getIdentityType() != IdentityType.APPLICATION || !cmd.getObjectidClass().equals(id.getClass().getName()))) {
        throw new NucleusUserException(Localiser.msg("002004", id, cmd.getFullClassName()));
      }
      rootCmds.add(cmd);
    } else {
      Collection<AbstractClassMetaData> pkCmds = getMetaDataManager().getClassMetaDataWithApplicationId(id.getClass().getName());
      if (pkCmds != null && pkCmds.size() > 0) {
        Iterator<AbstractClassMetaData> iter = pkCmds.iterator();
        while (iter.hasNext()) {
          AbstractClassMetaData pkCmd = iter.next();
          boolean toAdd = true;
          Iterator<AbstractClassMetaData> rootCmdIterator = rootCmds.iterator();
          while (rootCmdIterator.hasNext()) {
            AbstractClassMetaData rootCmd = rootCmdIterator.next();
            if (rootCmd.isDescendantOf(pkCmd)) {
              rootCmdIterator.remove();
            } else {
              if (pkCmd.isDescendantOf(rootCmd)) {
                toAdd = false;
              }
            }
          }
          if (toAdd) {
            rootCmds.add(pkCmd);
          }
        }
      }
      if (rootCmds.size() == 0) {
        return null;
      }
    }
    AbstractClassMetaData rootCmd = rootCmds.get(0);
    if (ec != null) {
      if (rootCmds.size() == 1) {
        Collection<String> subclasses = getSubClassesForClass(rootCmd.getFullClassName(), true, clr);
        if (!rootCmd.isImplementationOfPersistentDefinition()) {
          if (subclasses == null || subclasses.isEmpty()) {
            return rootCmd.getFullClassName();
          }
        }
        int numConcrete = 0;
        String concreteClassName = null;
        Class rootCls = clr.classForName(rootCmd.getFullClassName());
        if (!Modifier.isAbstract(rootCls.getModifiers())) {
          concreteClassName = rootCmd.getFullClassName();
          numConcrete++;
        }
        if (subclasses != null) {
          for (String subclassName : subclasses) {
            Class subcls = clr.classForName(subclassName);
            if (!Modifier.isAbstract(subcls.getModifiers())) {
              if (concreteClassName == null) {
                concreteClassName = subclassName;
              }
              numConcrete++;
            }
          }
        }
        if (numConcrete == 1) {
          return concreteClassName;
        }
        if (rootCmd.hasDiscriminatorStrategy()) {
          if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug("Performing query using discriminator on " + rootCmd.getFullClassName() + " and its subclasses to find the class of " + id);
          }
          return RDBMSStoreHelper.getClassNameForIdUsingDiscriminator(this, ec, id, rootCmd);
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
          NucleusLogger.PERSISTENCE.debug("Performing query using UNION on " + rootCmd.getFullClassName() + " and its subclasses to find the class of " + id);
        }
        return RDBMSStoreHelper.getClassNameForIdUsingUnion(this, ec, id, rootCmds);
      }
      if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
        StringBuilder str = new StringBuilder();
        Iterator<AbstractClassMetaData> rootCmdIter = rootCmds.iterator();
        while (rootCmdIter.hasNext()) {
          AbstractClassMetaData cmd = rootCmdIter.next();
          str.append(cmd.getFullClassName());
          if (rootCmdIter.hasNext()) {
            str.append(",");
          }
        }
        NucleusLogger.PERSISTENCE.debug("Performing query using UNION on " + str.toString() + " and their subclasses to find the class of " + id);
      }
      return RDBMSStoreHelper.getClassNameForIdUsingUnion(this, ec, id, rootCmds);
    }
    if (rootCmds.size() > 1) {
      if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
        NucleusLogger.PERSISTENCE.debug("Id \"" + id + "\" has been determined to be the id of class " + rootCmd.getFullClassName() + " : this is the first of " + rootCmds.size() + " possible, but unable to determine further");
      }
      return rootCmd.getFullClassName();
    }
    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
      NucleusLogger.PERSISTENCE.debug("Id \"" + id + "\" has been determined to be the id of class " + rootCmd.getFullClassName() + " : unable to determine if actually of a subclass");
    }
    return rootCmd.getFullClassName();
  }

  public DiscriminatorDefiner getDiscriminatorDefiner(AbstractClassMetaData cmd, ClassLoaderResolver clr) {
    final String discrDefinerClassName = cmd.getValueForExtension(DiscriminatorDefiner.METADATA_EXTENSION_DISCRIMINATOR_DEFINER);
    if (discrDefinerClassName != null && !discrDefinerClassName.isEmpty()) {
      return discriminatorDefinerMap.computeIfAbsent(discrDefinerClassName, (className) -> {
        final Class<DiscriminatorDefiner> discrDefinerClass = clr.classForName(discrDefinerClassName);
        try {
          return discrDefinerClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          throw new RuntimeException(e);
        }
      });
    }
    return null;
  }

  /**
     * Method to return the value from the results for the mapping at the specified position.
     * @param rs The results
     * @param mapping The mapping
     * @param position The position in the results
     * @return The value at that position
     * @throws NucleusDataStoreException if an error occurs accessing the results
     */
  public Object getResultValueAtPosition(ResultSet rs, JavaTypeMapping mapping, int position) {
    try {
      return rs.getObject(position);
    } catch (SQLException sqle) {
      throw new NucleusDataStoreException(sqle.getMessage(), sqle);
    }
  }

  /**
     * Accessor for the next value from the specified ValueGenerator.
     * This implementation caters for datastore-specific generators and provides synchronisation on the connection to the datastore.
     * @param generator The generator
     * @param ec execution context
     * @return The next value.
     */
  @Override protected Object getNextValueForValueGenerator(ValueGenerator generator, final ExecutionContext ec) {
    Object value = null;
    synchronized (generator) {
      if (generator instanceof AbstractConnectedGenerator) {
        ConnectionPreference connPref = ((AbstractConnectedGenerator) generator).getConnectionPreference();
        final boolean newConnection;
        if (connPref == ConnectionPreference.NONE) {
          newConnection = !getStringProperty(PropertyNames.PROPERTY_VALUEGEN_TXN_ATTRIBUTE).equalsIgnoreCase("EXISTING");
        } else {
          newConnection = connPref == ConnectionPreference.NEW;
        }
        ValueGenerationConnectionProvider connProvider = new ValueGenerationConnectionProvider() {
          ManagedConnection mconn;

          public ManagedConnection retrieveConnection() {
            if (newConnection) {
              mconn = connectionMgr.getConnection(TransactionUtils.getTransactionIsolationLevelForName(getStringProperty(PropertyNames.PROPERTY_VALUEGEN_TXN_ISOLATION)));
            } else {
              mconn = connectionMgr.getConnection(ec);
            }
            return mconn;
          }

          public void releaseConnection() {
            try {
              mconn.release();
              mconn = null;
            } catch (NucleusException e) {
              String msg = Localiser.msg("050025", e);
              NucleusLogger.VALUEGENERATION.error(msg);
              throw new NucleusDataStoreException(msg, e);
            }
          }
        };
        ((AbstractConnectedGenerator) generator).setConnectionProvider(connProvider);
      }
      value = generator.next();
    }
    return value;
  }

  /**
     * Method to return the properties to pass to the ValueGenerator for the specified field.
     * @param cmd MetaData for the class
     * @param absoluteFieldNumber Number of the field (-1 = datastore identity)
     * @param clr ClassLoader resolver
     * @param seqmd Any sequence metadata
     * @param tablegenmd Any table generator metadata
     * @return The properties to use for this field
     */
  protected Properties getPropertiesForValueGenerator(AbstractClassMetaData cmd, int absoluteFieldNumber, ClassLoaderResolver clr, SequenceMetaData seqmd, TableGeneratorMetaData tablegenmd) {
    Properties properties = new Properties();
    properties.setProperty(ValueGenerator.PROPERTY_CLASS_NAME, cmd.getFullClassName());
    properties.setProperty(ValueGenerator.PROPERTY_ROOT_CLASS_NAME, cmd.getBaseAbstractClassMetaData().getFullClassName());
    if (cmd.getCatalog() != null) {
      properties.setProperty(ValueGenerator.PROPERTY_CATALOG_NAME, cmd.getCatalog());
    } else {
      if (!StringUtils.isWhitespace(catalogName)) {
        properties.setProperty(ValueGenerator.PROPERTY_CATALOG_NAME, catalogName);
      }
    }
    if (cmd.getSchema() != null) {
      properties.setProperty(ValueGenerator.PROPERTY_SCHEMA_NAME, cmd.getSchema());
    } else {
      if (!StringUtils.isWhitespace(schemaName)) {
        properties.setProperty(ValueGenerator.PROPERTY_SCHEMA_NAME, schemaName);
      }
    }
    AbstractMemberMetaData mmd = null;
    ValueGenerationStrategy strategy = null;
    String sequence = null;
    Map<String, String> extensions = null;
    if (absoluteFieldNumber >= 0) {
      mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(absoluteFieldNumber);
      properties.setProperty(ValueGenerator.PROPERTY_FIELD_NAME, mmd.getFullFieldName());
      strategy = mmd.getValueStrategy();
      if (strategy.equals(ValueGenerationStrategy.NATIVE)) {
        strategy = ValueGenerationStrategy.getIdentityStrategy(getValueGenerationStrategyForNative(mmd));
      }
      sequence = mmd.getSequence();
      if (sequence != null) {
        properties.setProperty(ValueGenerator.PROPERTY_SEQUENCE_NAME, sequence);
      }
      extensions = mmd.getExtensions();
      if (extensions != null && extensions.size() > 0) {
        properties.putAll(extensions);
      }
    } else {
      DatastoreIdentityMetaData idmd = cmd.getBaseDatastoreIdentityMetaData();
      strategy = idmd.getValueStrategy();
      if (strategy.equals(ValueGenerationStrategy.NATIVE)) {
        strategy = ValueGenerationStrategy.getIdentityStrategy(getValueGenerationStrategyForNative(cmd));
      }
      sequence = idmd.getSequence();
      if (sequence != null) {
        properties.setProperty(ValueGenerator.PROPERTY_SEQUENCE_NAME, sequence);
      }
      extensions = idmd.getExtensions();
      if (extensions != null && extensions.size() > 0) {
        properties.putAll(extensions);
      }
    }
    if (strategy != ValueGenerationStrategy.INCREMENT || tablegenmd == null) {
      DatastoreClass tbl = getDatastoreClass(cmd.getBaseAbstractClassMetaData().getFullClassName(), clr);
      if (tbl == null) {
        tbl = getTableForStrategy(cmd, absoluteFieldNumber, clr);
      }
      JavaTypeMapping m = null;
      if (mmd != null) {
        m = tbl.getMemberMapping(mmd);
        if (m == null) {
          tbl = getTableForStrategy(cmd, absoluteFieldNumber, clr);
          m = tbl.getMemberMapping(mmd);
        }
      } else {
        m = tbl.getIdMapping();
      }
      StringBuilder columnsName = new StringBuilder();
      for (int i = 0; i < m.getNumberOfColumnMappings(); i++) {
        if (i > 0) {
          columnsName.append(",");
        }
        columnsName.append(m.getColumnMapping(i).getColumn().getIdentifier().toString());
      }
      properties.setProperty(ValueGenerator.PROPERTY_TABLE_NAME, tbl.getIdentifier().toString());
      properties.setProperty(ValueGenerator.PROPERTY_COLUMN_NAME, columnsName.toString());
    }
    if (strategy == ValueGenerationStrategy.INCREMENT) {
      addValueGenerationPropertiesForIncrement(properties, tablegenmd);
    } else {
      if (strategy == ValueGenerationStrategy.SEQUENCE) {
        addValueGenerationPropertiesForSequence(properties, seqmd);
      }
    }
    return properties;
  }

  private DatastoreClass getTableForStrategy(AbstractClassMetaData cmd, int fieldNumber, ClassLoaderResolver clr) {
    DatastoreClass t = getDatastoreClass(cmd.getFullClassName(), clr);
    if (t == null && cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
      throw new NucleusUserException(Localiser.msg("032013", cmd.getFullClassName()));
    }
    if (t == null) {
      throw new NucleusUserException("Attempt to find table for class=" + cmd.getFullClassName() + " but no table found! Check the metadata");
    }
    if (fieldNumber >= 0) {
      AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
      t = t.getBaseDatastoreClassWithMember(mmd);
    } else {
      boolean hasSuperclass = true;
      while (hasSuperclass) {
        DatastoreClass supert = t.getSuperDatastoreClass();
        if (supert != null) {
          t = supert;
        } else {
          hasSuperclass = false;
        }
      }
    }
    return t;
  }

  /**
     * Accessor for whether this value strategy is supported.
     * Overrides the setting in the superclass for identity/sequence if the adapter doesn't support them.
     * @param strategy The strategy
     * @return Whether it is supported.
     */
  @Override public boolean supportsValueGenerationStrategy(String strategy) {
    if (strategy.equalsIgnoreCase("IDENTITY") || super.supportsValueGenerationStrategy(strategy)) {
      if (strategy.equalsIgnoreCase("IDENTITY") && !dba.supportsOption(DatastoreAdapter.IDENTITY_COLUMNS)) {
        return false;
      } else {
        if (strategy.equalsIgnoreCase("SEQUENCE") && !dba.supportsOption(DatastoreAdapter.SEQUENCES)) {
          return false;
        } else {
          if (strategy.equalsIgnoreCase("uuid-string")) {
            return dba.supportsOption(DatastoreAdapter.VALUE_GENERATION_UUID_STRING);
          }
        }
      }
      return true;
    }
    return false;
  }

  @Override public String getValueGenerationStrategyForNative(AbstractClassMetaData cmd) {
    if (getBooleanProperty(RDBMSPropertyNames.PROPERTY_RDBMS_LEGACY_NATIVE_VALUE_STRATEGY)) {
      String sequence = cmd.getDatastoreIdentityMetaData().getSequence();
      if (dba.supportsOption(DatastoreAdapter.SEQUENCES) && sequence != null) {
        return ValueGenerationStrategy.SEQUENCE.toString();
      }
      return ValueGenerationStrategy.INCREMENT.toString();
    }
    return super.getValueGenerationStrategyForNative(cmd);
  }

  @Override public String getValueGenerationStrategyForNative(AbstractMemberMetaData mmd) {
    if (getBooleanProperty(RDBMSPropertyNames.PROPERTY_RDBMS_LEGACY_NATIVE_VALUE_STRATEGY)) {
      String sequence = mmd.getSequence();
      if (dba.supportsOption(DatastoreAdapter.SEQUENCES) && sequence != null) {
        return ValueGenerationStrategy.SEQUENCE.toString();
      }
      return ValueGenerationStrategy.INCREMENT.toString();
    }
    return super.getValueGenerationStrategyForNative(mmd);
  }

  /**
     * Accessor for the SQL type info for the specified JDBC type
     * @param jdbcType JDBC type
     * @return (default) SQL type info
     * @throws UnsupportedDataTypeException If the JDBC type is not found
     */
  public SQLTypeInfo getSQLTypeInfoForJDBCType(int jdbcType) throws UnsupportedDataTypeException {
    return this.getSQLTypeInfoForJDBCType(jdbcType, "DEFAULT");
  }

  /**
     * Accessor for the SQL type info for the specified JDBC type.
     * @param jdbcType JDBC type
     * @param sqlType The SQL type name (if known, otherwise uses the default for this JDBC type).
     * @return SQL type info
     * @throws UnsupportedDataTypeException If the JDBC type is not found
     */
  public SQLTypeInfo getSQLTypeInfoForJDBCType(int jdbcType, String sqlType) throws UnsupportedDataTypeException {
    RDBMSTypesInfo typesInfo = (RDBMSTypesInfo) schemaHandler.getSchemaData(null, RDBMSSchemaHandler.TYPE_TYPES, null);
    JDBCTypeInfo jdbcTypeInfo = (JDBCTypeInfo) typesInfo.getChild("" + jdbcType);
    if (jdbcTypeInfo.getNumberOfChildren() == 0) {
      throw new UnsupportedDataTypeException(Localiser.msg("051005", dba.getNameForJDBCType(jdbcType)));
    }
    SQLTypeInfo sqlTypeInfo = (SQLTypeInfo) jdbcTypeInfo.getChild(sqlType != null ? sqlType : "DEFAULT");
    if (sqlTypeInfo == null && sqlType != null) {
      sqlTypeInfo = (SQLTypeInfo) jdbcTypeInfo.getChild(sqlType.toUpperCase());
      if (sqlTypeInfo == null) {
        sqlTypeInfo = (SQLTypeInfo) jdbcTypeInfo.getChild(sqlType.toLowerCase());
        if (sqlTypeInfo == null) {
          NucleusLogger.DATASTORE_SCHEMA.debug("Attempt to find JDBC driver \'typeInfo\' for jdbc-type=" + dba.getNameForJDBCType(jdbcType) + " but sql-type=" + sqlType + " is not found. Using default sql-type for this jdbc-type.");
          sqlTypeInfo = (SQLTypeInfo) jdbcTypeInfo.getChild("DEFAULT");
        }
      }
    }
    return sqlTypeInfo;
  }

  /**
     * Returns the column info for a column name. This should be used instead
     * of making direct calls to DatabaseMetaData.getColumns().
     * <p>
     * Where possible, this method loads and caches column info for more than
     * just the table being requested, improving performance by reducing the
     * overall number of calls made to DatabaseMetaData.getColumns() (each of
     * which usually results in one or more database queries).
     * </p>
     * @param table The table/view
     * @param conn JDBC connection to the database.
     * @param column the column
     * @return The ColumnInfo objects describing the column.
     * @throws SQLException Thrown if an error occurs
     */
  public RDBMSColumnInfo getColumnInfoForColumnName(Table table, Connection conn, DatastoreIdentifier column) throws SQLException {
    return (RDBMSColumnInfo) schemaHandler.getSchemaData(conn, RDBMSSchemaHandler.TYPE_COLUMN, new Object[] { table, column.getName() });
  }

  /**
     * Returns the column info for a database table. This should be used instead
     * of making direct calls to DatabaseMetaData.getColumns().
     * <p>
     * Where possible, this method loads and caches column info for more than
     * just the table being requested, improving performance by reducing the
     * overall number of calls made to DatabaseMetaData.getColumns() (each of
     * which usually results in one or more database queries).
     * </p>
     * @param table The table/view
     * @param conn JDBC connection to the database.
     * @return A list of ColumnInfo objects describing the columns of the table.
     * The list is in the same order as was supplied by getColumns(). If no
     * column info is found for the given table, an empty list is returned.
     * @throws SQLException Thrown if an error occurs
     */
  public List<StoreSchemaData> getColumnInfoForTable(Table table, Connection conn) throws SQLException {
    RDBMSTableInfo tableInfo = (RDBMSTableInfo) schemaHandler.getSchemaData(conn, RDBMSSchemaHandler.TYPE_COLUMNS, new Object[] { table });
    if (tableInfo == null) {
      return Collections.EMPTY_LIST;
    }
    List<StoreSchemaData> cols = new ArrayList<>(tableInfo.getNumberOfChildren());
    cols.addAll(tableInfo.getChildren());
    return cols;
  }

  /**
     * Method to invalidate the cached column info for a table.
     * This is called when we have just added columns to the table in the schema
     * has the effect of a reload of the tables information the next time it is needed.
     * @param table The table
     */
  public void invalidateColumnInfoForTable(Table table) {
    RDBMSSchemaInfo schemaInfo = (RDBMSSchemaInfo) schemaHandler.getSchemaData(null, RDBMSSchemaHandler.TYPE_TABLES, null);
    if (schemaInfo != null && schemaInfo.getNumberOfChildren() > 0) {
      schemaInfo.getChildren().remove(table.getIdentifier().getFullyQualifiedName(true));
    }
  }

  /**
     * Convenience accessor of the Table objects managed in this datastore at this point.
     * @param catalog Name of the catalog to restrict the collection by (or null to not restrict)
     * @param schema Name of the schema to restrict the collection by (or null to not restrict)
     * @return Collection of tables
     */
  public Collection<Table> getManagedTables(String catalog, String schema) {
    if (storeDataMgr == null) {
      return Collections.EMPTY_SET;
    }
    Collection<Table> tables = new HashSet<>();
    for (StoreData sd : storeDataMgr.getManagedStoreData()) {
      if (sd.getTable() != null) {
        DatastoreIdentifier identifier = ((Table) sd.getTable()).getIdentifier();
        boolean catalogMatches = true;
        boolean schemaMatches = true;
        if (catalog != null && identifier.getCatalogName() != null && !catalog.equals(identifier.getCatalogName())) {
          catalogMatches = false;
        }
        if (schema != null && identifier.getSchemaName() != null && !schema.equals(identifier.getSchemaName())) {
          schemaMatches = false;
        }
        if (catalogMatches && schemaMatches) {
          tables.add((Table) sd.getTable());
        }
      }
    }
    return tables;
  }

  /**
     * Resolves an identifier macro. The public fields <var>className</var>, <var>fieldName </var>,
     * and <var>subfieldName </var> of the given macro are taken as inputs, and the public
     * <var>value </var> field is set to the SQL identifier of the corresponding database table or column.
     * @param im The macro to resolve.
     * @param clr The ClassLoaderResolver
     */
  public void resolveIdentifierMacro(MacroString.IdentifierMacro im, ClassLoaderResolver clr) {
    DatastoreClass ct = getDatastoreClass(im.className, clr);
    if (im.fieldName == null) {
      im.value = ct.getIdentifier().toString();
      return;
    }
    JavaTypeMapping m;
    if (im.fieldName.equals("this")) {
      if (!(ct instanceof ClassTable)) {
        throw new NucleusUserException(Localiser.msg("050034", im.className));
      }
      if (im.subfieldName != null) {
        throw new NucleusUserException(Localiser.msg("050035", im.className, im.fieldName, im.subfieldName));
      }
      m = ((Table) ct).getIdMapping();
    } else {
      AbstractClassMetaData cmd = getMetaDataManager().getMetaDataForClass(im.className, clr);
      AbstractMemberMetaData mmd = cmd.getMetaDataForMember(im.fieldName);
      m = ct.getMemberMapping(mmd);
      Table t = getTable(mmd);
      if (im.subfieldName == null) {
        if (t != null) {
          im.value = t.getIdentifier().toString();
          return;
        }
      } else {
        if (t instanceof CollectionTable) {
          CollectionTable collTable = (CollectionTable) t;
          if (im.subfieldName.equals("owner")) {
            m = collTable.getOwnerMapping();
          } else {
            if (im.subfieldName.equals("element")) {
              m = collTable.getElementMapping();
            } else {
              if (im.subfieldName.equals("index")) {
                m = collTable.getOrderMapping();
              } else {
                throw new NucleusUserException(Localiser.msg("050036", im.subfieldName, im));
              }
            }
          }
        } else {
          if (t instanceof MapTable) {
            MapTable mt = (MapTable) t;
            if (im.subfieldName.equals("owner")) {
              m = mt.getOwnerMapping();
            } else {
              if (im.subfieldName.equals("key")) {
                m = mt.getKeyMapping();
              } else {
                if (im.subfieldName.equals("value")) {
                  m = mt.getValueMapping();
                } else {
                  throw new NucleusUserException(Localiser.msg("050037", im.subfieldName, im));
                }
              }
            }
          } else {
            throw new NucleusUserException(Localiser.msg("050035", im.className, im.fieldName, im.subfieldName));
          }
        }
      }
    }
    im.value = m.getColumnMapping(0).getColumn().getIdentifier().toString();
  }

  /**
     * Method to output particular information owned by this datastore.
     * Supports "DATASTORE" and "SCHEMA" categories.
     * @param category Category of information
     * @param ps PrintStream
     * @throws Exception Thrown if an error occurs in the output process
     */
  public void printInformation(String category, PrintStream ps) throws Exception {
    DatastoreAdapter dba = getDatastoreAdapter();
    super.printInformation(category, ps);
    if (category.equals("DATASTORE")) {
      ps.println(dba.toString());
      ps.println();
      ps.println("Database TypeInfo");
      RDBMSTypesInfo typesInfo = (RDBMSTypesInfo) schemaHandler.getSchemaData(null, RDBMSSchemaHandler.TYPE_TYPES, null);
      if (typesInfo != null) {
        Iterator iter = typesInfo.getChildren().keySet().iterator();
        while (iter.hasNext()) {
          String jdbcTypeStr = (String) iter.next();
          short jdbcTypeNumber = 0;
          try {
            jdbcTypeNumber = Short.parseShort(jdbcTypeStr);
          } catch (NumberFormatException nfe) {
          }
          JDBCTypeInfo jdbcType = (JDBCTypeInfo) typesInfo.getChild(jdbcTypeStr);
          Collection<String> sqlTypeNames = jdbcType.getChildren().keySet();
          StringBuilder sqlTypesName = new StringBuilder();
          String defaultSqlTypeName = null;
          for (String sqlTypeName : sqlTypeNames) {
            if (!sqlTypeName.equals("DEFAULT")) {
              if (sqlTypesName.length() > 0) {
                sqlTypesName.append(',');
              }
              sqlTypesName.append(sqlTypeName);
            } else {
              defaultSqlTypeName = ((SQLTypeInfo) jdbcType.getChild(sqlTypeName)).getTypeName();
            }
          }
          String typeStr = "JDBC Type=" + dba.getNameForJDBCType(jdbcTypeNumber) + " sqlTypes=" + sqlTypesName + (defaultSqlTypeName != null ? (" (default=" + defaultSqlTypeName + ")") : "");
          ps.println(typeStr);
          for (String sqlTypeName : sqlTypeNames) {
            if (!sqlTypeName.equals("DEFAULT")) {
              SQLTypeInfo sqlType = (SQLTypeInfo) jdbcType.getChild(sqlTypeName);
              ps.println(sqlType.toString("    "));
            }
          }
        }
      }
      ps.println("");
      ps.println("Database Keywords");
      for (String word : dba.getReservedWords()) {
        ps.println(word);
      }
      ps.println("");
    } else {
      if (category.equals("SCHEMA")) {
        ps.println(dba.toString());
        ps.println();
        ps.println("TABLES");
        ManagedConnection mc = connectionMgr.getConnection(-1);
        try {
          Connection conn = (Connection) mc.getConnection();
          RDBMSSchemaInfo schemaInfo = (RDBMSSchemaInfo) schemaHandler.getSchemaData(conn, RDBMSSchemaHandler.TYPE_TABLES, new Object[] { this.catalogName, this.schemaName });
          if (schemaInfo != null) {
            Iterator tableIter = schemaInfo.getChildren().values().iterator();
            while (tableIter.hasNext()) {
              RDBMSTableInfo tableInfo = (RDBMSTableInfo) tableIter.next();
              ps.println(tableInfo);
              Iterator<StoreSchemaData> columnIter = tableInfo.getChildren().iterator();
              while (columnIter.hasNext()) {
                RDBMSColumnInfo colInfo = (RDBMSColumnInfo) columnIter.next();
                ps.println(colInfo);
              }
            }
          }
        }  finally {
          if (mc != null) {
            mc.release();
          }
        }
        ps.println("");
      }
    }
  }

  public void executeScript(String script) {
    script = StringUtils.replaceAll(script, "\n", " ");
    script = StringUtils.replaceAll(script, "\t", " ");
    ManagedConnection mc = connectionMgr.getConnection(-1);
    try {
      Connection conn = (Connection) mc.getConnection();
      Statement stmt = conn.createStatement();
      try {
        StringTokenizer tokeniser = new StringTokenizer(script, ";");
        while (tokeniser.hasMoreTokens()) {
          String token = tokeniser.nextToken().trim();
          if (!StringUtils.isWhitespace(token)) {
            NucleusLogger.DATASTORE_NATIVE.debug("Executing script statement : " + token);
            stmt.execute(token + ";");
          }
        }
      }  finally {
        stmt.close();
      }
    } catch (SQLException e) {
      NucleusLogger.DATASTORE_NATIVE.error("Exception executing user script", e);
      throw new NucleusUserException("Exception executing user script. See nested exception for details", e);
    } finally {
      mc.release();
    }
  }

  @Override public Collection<String> getSupportedOptions() {
    Set<String> set = new HashSet<>();
    set.add(StoreManager.OPTION_APPLICATION_ID);
    set.add(StoreManager.OPTION_APPLICATION_COMPOSITE_ID);
    set.add(StoreManager.OPTION_DATASTORE_ID);
    set.add(StoreManager.OPTION_NONDURABLE_ID);
    set.add(StoreManager.OPTION_TRANSACTION_ACID);
    set.add(StoreManager.OPTION_ORM);
    set.add(StoreManager.OPTION_ORM_SECONDARY_TABLE);
    set.add(StoreManager.OPTION_ORM_EMBEDDED_PC);
    set.add(StoreManager.OPTION_ORM_EMBEDDED_COLLECTION);
    set.add(StoreManager.OPTION_ORM_EMBEDDED_MAP);
    set.add(StoreManager.OPTION_ORM_EMBEDDED_ARRAY);
    set.add(StoreManager.OPTION_ORM_FOREIGN_KEYS);
    set.add(StoreManager.OPTION_ORM_SERIALISED_PC);
    set.add(StoreManager.OPTION_ORM_SERIALISED_COLLECTION_ELEMENT);
    set.add(StoreManager.OPTION_ORM_SERIALISED_ARRAY_ELEMENT);
    set.add(StoreManager.OPTION_ORM_SERIALISED_MAP_KEY);
    set.add(StoreManager.OPTION_ORM_SERIALISED_MAP_VALUE);
    if (dba.supportsOption(DatastoreAdapter.TX_ISOLATION_READ_COMMITTED)) {
      set.add(StoreManager.OPTION_TXN_ISOLATION_READ_COMMITTED);
    }
    if (dba.supportsOption(DatastoreAdapter.TX_ISOLATION_READ_UNCOMMITTED)) {
      set.add(StoreManager.OPTION_TXN_ISOLATION_READ_UNCOMMITTED);
    }
    if (dba.supportsOption(DatastoreAdapter.TX_ISOLATION_REPEATABLE_READ)) {
      set.add(StoreManager.OPTION_TXN_ISOLATION_REPEATABLE_READ);
    }
    if (dba.supportsOption(DatastoreAdapter.TX_ISOLATION_SERIALIZABLE)) {
      set.add(StoreManager.OPTION_TXN_ISOLATION_SERIALIZABLE);
    }
    set.add(StoreManager.OPTION_QUERY_CANCEL);
    set.add(StoreManager.OPTION_DATASTORE_TIMEOUT);
    if (dba.supportsOption(DatastoreAdapter.DATETIME_STORES_MILLISECS)) {
      set.add(StoreManager.OPTION_DATASTORE_TIME_STORES_MILLISECS);
    }
    if (dba.supportsOption(DatastoreAdapter.OPERATOR_BITWISE_AND)) {
      set.add(StoreManager.OPTION_QUERY_JDOQL_BITWISE_OPS);
    }
    set.add(StoreManager.OPTION_QUERY_JPQL_BULK_INSERT);
    set.add(StoreManager.OPTION_QUERY_JPQL_BULK_UPDATE);
    set.add(StoreManager.OPTION_QUERY_JPQL_BULK_DELETE);
    set.add(StoreManager.OPTION_QUERY_JDOQL_BULK_UPDATE);
    set.add(StoreManager.OPTION_QUERY_JDOQL_BULK_DELETE);
    return set;
  }

  /**
     * Convenience method to return if the datastore supports batching and the user wants batching.
     * @return If batching of statements is permissible
     */
  public boolean allowsBatching() {
    return dba.supportsOption(DatastoreAdapter.STATEMENT_BATCHING) && getIntProperty(RDBMSPropertyNames.PROPERTY_RDBMS_STATEMENT_BATCH_LIMIT) != 0;
  }

  public boolean usesBackedSCOWrappers() {
    return true;
  }

  @Override public boolean useBackedSCOWrapperForMember(AbstractMemberMetaData mmd, ExecutionContext ec) {
    if ((mmd.hasCollection() || mmd.hasMap()) && mmd.hasExtension(MetaData.EXTENSION_MEMBER_TYPE_CONVERTER_NAME)) {
      return false;
    }
    return true;
  }

  /**
     * Accessor for the Calendar to be used in handling all timezone issues with the datastore.
     * Utilises the "serverTimeZoneID" in providing this Calendar used in time/date conversions.
     * @return The calendar to use for dateTimezone issues.
     */
  public Calendar getCalendarForDateTimezone() {
    if (dateTimezoneCalendar == null) {
      TimeZone tz;
      String serverTimeZoneID = getStringProperty(PropertyNames.PROPERTY_SERVER_TIMEZONE_ID);
      if (serverTimeZoneID != null) {
        tz = TimeZone.getTimeZone(serverTimeZoneID);
      } else {
        tz = TimeZone.getDefault();
      }
      dateTimezoneCalendar = new GregorianCalendar(tz);
    }
    if (getBooleanProperty(RDBMSPropertyNames.PROPERTY_RDBMS_CLONE_CALENDAR_FOR_DATE_TIMEZONE)) {
      return (Calendar) dateTimezoneCalendar.clone();
    }
    return dateTimezoneCalendar;
  }

  /**
     * Called by (container) Mapping objects to request the creation of a join table.
     * If the specified field doesn't require a join table then this returns null.
     * If the join table already exists, then this returns it.
     * @param ownerTable The table that owns this member.
     * @param mmd The metadata describing the field/property.
     * @param clr The ClassLoaderResolver
     * @return The table (SetTable/ListTable/MapTable/ArrayTable)
     */
  public Table newJoinTable(Table ownerTable, AbstractMemberMetaData mmd, ClassLoaderResolver clr) {
    if (mmd.getJoinMetaData() == null) {
      AbstractMemberMetaData[] relatedMmds = mmd.getRelatedMemberMetaData(clr);
      if (relatedMmds != null && relatedMmds[0].getJoinMetaData() != null) {
      } else {
        Class element_class;
        if (mmd.hasCollection()) {
          element_class = clr.classForName(mmd.getCollection().getElementType());
        } else {
          if (mmd.hasMap()) {
            MapMetaData mapmd = (MapMetaData) mmd.getContainer();
            if (mmd.getValueMetaData() != null && mmd.getValueMetaData().getMappedBy() != null) {
              element_class = clr.classForName(mapmd.getKeyType());
            } else {
              if (mmd.getKeyMetaData() != null && mmd.getKeyMetaData().getMappedBy() != null) {
                element_class = clr.classForName(mapmd.getValueType());
              } else {
                throw new NucleusUserException(Localiser.msg("050050", mmd.getFullFieldName()));
              }
            }
          } else {
            if (mmd.hasArray()) {
              element_class = clr.classForName(mmd.getTypeName()).getComponentType();
            } else {
              return null;
            }
          }
        }
        if (getMetaDataManager().getMetaDataForClass(element_class, clr) != null) {
          return null;
        } else {
          if (ClassUtils.isReferenceType(element_class)) {
            return null;
          }
        }
        throw new NucleusUserException(Localiser.msg("050049", mmd.getFullFieldName(), mmd.toString()));
      }
    }
    Table joinTable = getTable(mmd);
    if (joinTable != null) {
      return joinTable;
    }
    if (classAdder == null) {
      throw new IllegalStateException(Localiser.msg("050016"));
    }
    if (mmd.getType().isArray()) {
      return classAdder.addJoinTableForContainer(ownerTable, mmd, clr, ClassAdder.JOIN_TABLE_ARRAY);
    } else {
      if (Map.class.isAssignableFrom(mmd.getType())) {
        return classAdder.addJoinTableForContainer(ownerTable, mmd, clr, ClassAdder.JOIN_TABLE_MAP);
      } else {
        if (Collection.class.isAssignableFrom(mmd.getType())) {
          return classAdder.addJoinTableForContainer(ownerTable, mmd, clr, ClassAdder.JOIN_TABLE_COLLECTION);
        } else {
          return classAdder.addJoinTableForContainer(ownerTable, mmd, clr, ClassAdder.JOIN_TABLE_PERSISTABLE);
        }
      }
    }
  }

  public void registerTableInitialized(Table table) {
    if (classAdder != null) {
      classAdder.tablesRecentlyInitialized.add(table);
    }
  }

  private class ClassAdder extends AbstractSchemaTransaction {
    /** join table for Collection. **/
    public static final int JOIN_TABLE_COLLECTION = 1;

    /** join table for Map. **/
    public static final int JOIN_TABLE_MAP = 2;

    /** join table for Array. **/
    public static final int JOIN_TABLE_ARRAY = 3;

    /** join table for persistable. **/
    public static final int JOIN_TABLE_PERSISTABLE = 4;

    /** Optional writer to dump the DDL for any classes being added. */
    private Writer ddlWriter = null;

    /** Whether to check if table/view exists */
    private final boolean checkExistTablesOrViews;

    /** tracks the SchemaData currrently being added - used to rollback the AutoStart added classes **/
    private Set<RDBMSStoreData> schemaDataAdded = new HashSet<>();

    private final String[] classNames;

    private List<Table> tablesRecentlyInitialized = new ArrayList<>();

    /**
         * Constructs a new class adder transaction that will add the given classes to the RDBMSManager.
         * @param classNames Names of the (initial) class(es) to be added.
         * @param writer Optional writer for DDL when we want the DDL outputting to file instead of creating the tables
         * @param isolationLevel Txn isolation level to use for schema "class" addition
         */
    private ClassAdder(String[] classNames, Writer writer, int isolationLevel) {
      super(RDBMSStoreManager.this, isolationLevel);
      this.ddlWriter = writer;
      this.classNames = classNames;
      checkExistTablesOrViews = RDBMSStoreManager.this.getBooleanProperty(RDBMSPropertyNames.PROPERTY_RDBMS_CHECK_EXISTS_TABLES_VIEWS);
    }

    /**
         * Method to give a string version of this object.
         * @return The String version of this object.
         */
    public String toString() {
      return Localiser.msg("050038", catalogName, schemaName);
    }

    /**
         * Method to perform the class addition.
         * @param clr the ClassLoaderResolver
         * @throws SQLException Thrown if an error occurs in execution.
         */
    protected void run(ClassLoaderResolver clr) throws SQLException {
      if (classNames == null || classNames.length == 0) {
        return;
      }
      try {
        schemaLock.writeLock().lock();
        classAdder = this;
        try {
          storeDataMgr.begin();
          boolean completed = false;
          List<Table> tablesCreated = null;
          List<Table> tableConstraintsCreated = null;
          List<Table> viewsCreated = null;
          try {
            List<Throwable> autoCreateErrors = new ArrayList<>();
            addClassTables(classNames, clr);
            List<Table>[] toValidate = initializeClassTables(classNames, clr);
            if (!performingDeleteSchemaForClasses) {
              if (toValidate[0] != null && toValidate[0].size() > 0) {
                List<Table>[] result = performTablesValidation(toValidate[0], clr, autoCreateErrors);
                tablesCreated = result[0];
                tableConstraintsCreated = result[1];
              }
              if (toValidate[1] != null && toValidate[1].size() > 0) {
                viewsCreated = performViewsValidation(toValidate[1], autoCreateErrors);
              }
              if (!autoCreateErrors.isEmpty()) {
                Iterator<Throwable> errorsIter = autoCreateErrors.iterator();
                while (errorsIter.hasNext()) {
                  Throwable exc = errorsIter.next();
                  if (rdbmsMgr.getSchemaHandler().isAutoCreateWarnOnError()) {
                    NucleusLogger.DATASTORE.warn(Localiser.msg("050044", exc));
                  } else {
                    NucleusLogger.DATASTORE.error(Localiser.msg("050044", exc));
                  }
                }
                if (!rdbmsMgr.getSchemaHandler().isAutoCreateWarnOnError()) {
                  throw new NucleusDataStoreException(Localiser.msg("050043"), autoCreateErrors.toArray(new Throwable[autoCreateErrors.size()]));
                }
              }
            }
            completed = true;
          } catch (SQLException sqle) {
            String msg = Localiser.msg("050044", sqle);
            NucleusLogger.DATASTORE_SCHEMA.error(msg);
            throw new NucleusDataStoreException(msg, sqle);
          } catch (Throwable e) {
            NucleusLogger.DATASTORE_SCHEMA.error(Localiser.msg("050044", e));
            if (NucleusException.class.isAssignableFrom(e.getClass())) {
              throw (NucleusException) e;
            }
            throw new NucleusException(e.toString(), e).setFatal();
          } finally {
            if (!completed) {
              storeDataMgr.rollback();
              rollbackSchemaCreation(viewsCreated, tableConstraintsCreated, tablesCreated);
            } else {
              storeDataMgr.commit();
            }
            schemaDataAdded.clear();
          }
        }  finally {
          classAdder = null;
        }
      }  finally {
        schemaLock.writeLock().unlock();
      }
    }

    private int addClassTablesRecursionCounter = 0;

    /**
         * Adds a new table object (ie ClassTable or ClassView) for every class in the given list. These classes
         * <ol>
         * <li>require a table</li>
         * <li>do not yet have a table initialized in the store manager.</li>
         * </ol>
         * <p>
         * This doesn't initialize or validate the tables, it just adds the table objects to the RDBMSManager's internal data structures.
         * </p>
         * @param classNames Names of class(es) whose tables are to be added.
         * @param clr the ClassLoaderResolver
         */
    public void addClassTables(String[] classNames, ClassLoaderResolver clr) {
      addClassTablesRecursionCounter += 1;
      try {
        AutoStartMechanism starter = rdbmsMgr.getNucleusContext().getAutoStartMechanism();
        try {
          if (starter != null && !starter.isOpen()) {
            starter.open();
          }
          Iterator iter = getMetaDataManager().getReferencedClasses(classNames, clr).iterator();
          while (iter.hasNext()) {
            addClassTable((ClassMetaData) iter.next(), clr);
          }
          Iterator<RDBMSStoreData> addedIter = new HashSet<>(this.schemaDataAdded).iterator();
          while (addedIter.hasNext()) {
            RDBMSStoreData data = addedIter.next();
            if (data.getTable() == null && data.isFCO()) {
              AbstractClassMetaData cmd = (AbstractClassMetaData) data.getMetaData();
              InheritanceMetaData imd = cmd.getInheritanceMetaData();
              if (imd.getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
                AbstractClassMetaData[] managingCmds = getClassesManagingTableForClass(cmd, clr);
                DatastoreClass superTable = null;
                if (managingCmds != null && managingCmds.length == 1) {
                  RDBMSStoreData superData = (RDBMSStoreData) storeDataMgr.get(managingCmds[0].getFullClassName());
                  if (superData == null) {
                    this.addClassTables(new String[] { managingCmds[0].getFullClassName() }, clr);
                    superData = (RDBMSStoreData) storeDataMgr.get(managingCmds[0].getFullClassName());
                  }
                  if (superData == null) {
                    String msg = Localiser.msg("050013", cmd.getFullClassName());
                    NucleusLogger.PERSISTENCE.error(msg);
                    throw new NucleusUserException(msg);
                  }
                  superTable = (DatastoreClass) superData.getTable();
                  data.setDatastoreContainerObject(superTable);
                }
              }
            }
          }
        }  finally {
          if (starter != null && starter.isOpen() && addClassTablesRecursionCounter <= 1) {
            starter.close();
          }
        }
      }  finally {
        addClassTablesRecursionCounter -= 1;
      }
    }

    /**
         * Method to add a new table object (ie ClassTable or ClassView).
         * Doesn't initialize or validate the tables, just adding the table objects to the internal data structures.
         * @param cmd the ClassMetaData
         * @param clr the ClassLoaderResolver
         */
    private void addClassTable(ClassMetaData cmd, ClassLoaderResolver clr) {
      if (cmd.getPersistenceModifier() != ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
        return;
      } else {
        if (cmd.getIdentityType() == IdentityType.NONDURABLE) {
          if (cmd.hasExtension(METADATA_NONDURABLE_REQUIRES_TABLE) && cmd.getValueForExtension(METADATA_NONDURABLE_REQUIRES_TABLE) != null && cmd.getValueForExtension(METADATA_NONDURABLE_REQUIRES_TABLE).equalsIgnoreCase("false")) {
            return;
          }
        }
      }
      if (!storeDataMgr.managesClass(cmd.getFullClassName())) {
        if (cmd.getIdentityType() == IdentityType.APPLICATION) {
          if (!cmd.usesSingleFieldIdentityClass()) {
            String baseClassWithMetaData = cmd.getBaseAbstractClassMetaData().getFullClassName();
            Collection<AbstractClassMetaData> pkCmds = getMetaDataManager().getClassMetaDataWithApplicationId(cmd.getObjectidClass());
            if (pkCmds != null && pkCmds.size() > 0) {
              boolean inSameTree = false;
              String sampleClassInOtherTree = null;
              for (AbstractClassMetaData pkCmd : pkCmds) {
                String otherClassBaseClass = pkCmd.getBaseAbstractClassMetaData().getFullClassName();
                if (otherClassBaseClass.equals(baseClassWithMetaData)) {
                  inSameTree = true;
                  break;
                }
                sampleClassInOtherTree = pkCmd.getFullClassName();
              }
              if (!inSameTree) {
                String errorMsg = Localiser.msg("050021", cmd.getFullClassName(), cmd.getObjectidClass(), sampleClassInOtherTree);
                NucleusLogger.DATASTORE.error(errorMsg);
                throw new NucleusUserException(errorMsg);
              }
            }
          }
        }
        if (cmd.isEmbeddedOnly()) {
          NucleusLogger.DATASTORE.debug(Localiser.msg("032012", cmd.getFullClassName()));
        } else {
          InheritanceMetaData imd = cmd.getInheritanceMetaData();
          RDBMSStoreData sdNew = null;
          if (imd.getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
            sdNew = new RDBMSStoreData(cmd, null, false);
            registerStoreData(sdNew);
          } else {
            if (imd.getStrategy() == InheritanceStrategy.COMPLETE_TABLE && cmd.isAbstract()) {
              sdNew = new RDBMSStoreData(cmd, null, false);
              registerStoreData(sdNew);
            } else {
              if (imd.getStrategy() == InheritanceStrategy.NEW_TABLE || imd.getStrategy() == InheritanceStrategy.COMPLETE_TABLE) {
                DatastoreIdentifier tableName = null;
                RDBMSStoreData tmpData = (RDBMSStoreData) storeDataMgr.get(cmd.getFullClassName());
                if (tmpData != null && tmpData.getDatastoreIdentifier() != null) {
                  tableName = tmpData.getDatastoreIdentifier();
                } else {
                  tableName = rdbmsMgr.getIdentifierFactory().newTableIdentifier(cmd);
                }
                StoreData[] existingStoreData = getStoreDataForDatastoreContainerObject(tableName);
                if (existingStoreData != null) {
                  String existingClass = null;
                  for (int j = 0; j < existingStoreData.length; j++) {
                    if (!existingStoreData[j].getName().equals(cmd.getFullClassName())) {
                      existingClass = existingStoreData[j].getName();
                      break;
                    }
                  }
                  if (existingClass != null) {
                    NucleusLogger.DATASTORE.warn(Localiser.msg("050015", cmd.getFullClassName(), tableName.getName(), existingClass));
                  }
                }
                DatastoreClass t = null;
                boolean hasViewDef = false;
                if (dba.getVendorID() != null) {
                  hasViewDef = cmd.hasExtension(MetaData.EXTENSION_CLASS_VIEW_DEFINITION + '-' + dba.getVendorID());
                }
                if (!hasViewDef) {
                  hasViewDef = cmd.hasExtension(MetaData.EXTENSION_CLASS_VIEW_DEFINITION);
                }
                t = (hasViewDef) ? new ClassView(tableName, RDBMSStoreManager.this, cmd) : new ClassTable(tableName, RDBMSStoreManager.this, cmd);
                sdNew = new RDBMSStoreData(cmd, t, true);
                rdbmsMgr.registerStoreData(sdNew);
                ((Table) t).preInitialize(clr);
              } else {
                if (imd.getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
                  AbstractClassMetaData[] managingCmds = getClassesManagingTableForClass(cmd, clr);
                  Table superTable = null;
                  if (managingCmds != null && managingCmds.length == 1) {
                    RDBMSStoreData superData = (RDBMSStoreData) storeDataMgr.get(managingCmds[0].getFullClassName());
                    if (superData != null) {
                      superTable = (Table) superData.getTable();
                    }
                    sdNew = new RDBMSStoreData(cmd, superTable, false);
                    rdbmsMgr.registerStoreData(sdNew);
                  } else {
                    String msg = Localiser.msg("050013", cmd.getFullClassName());
                    NucleusLogger.PERSISTENCE.error(msg);
                    throw new NucleusUserException(msg);
                  }
                }
              }
            }
          }
          schemaDataAdded.add(sdNew);
        }
      }
    }

    /**
         * Initialisation of tables. Updates the internal representation of the table to match what is 
         * required for the class(es). Each time a table object is initialized, it may cause other associated 
         * table objects to be added (via callbacks to addClasses()) so the loop is repeated until no more 
         * initialisation is needed.
         * @param classNames String array of class names
         * @param clr the ClassLoaderResolver
         * @return an array of List where index == 0 is list of the tables created, index == 1 is list of the views created
         */
    private List<Table>[] initializeClassTables(String[] classNames, ClassLoaderResolver clr) {
      List<Table> tablesToValidate = new ArrayList<>();
      List<Table> viewsToValidate = new ArrayList<>();
      tablesRecentlyInitialized.clear();
      int numTablesInitializedInit = 0;
      int numStoreDataInit = 0;
      RDBMSStoreData[] rdbmsStoreData = storeDataMgr.getManagedStoreData().toArray(new RDBMSStoreData[storeDataMgr.size()]);
      do {
        numStoreDataInit = rdbmsStoreData.length;
        numTablesInitializedInit = tablesRecentlyInitialized.size();
        for (int i = 0; i < rdbmsStoreData.length; i++) {
          RDBMSStoreData currentStoreData = rdbmsStoreData[i];
          if (currentStoreData.hasTable()) {
            Table t = (Table) currentStoreData.getTable();
            if (t instanceof DatastoreClass) {
              ((RDBMSPersistenceHandler) rdbmsMgr.getPersistenceHandler()).removeRequestsForTable((DatastoreClass) t);
            }
            if (!t.isInitialized()) {
              t.initialize(clr);
            }
            if (!currentStoreData.isTableOwner() && !((ClassTable) t).managesClass(currentStoreData.getName())) {
              ((ClassTable) t).manageClass((ClassMetaData) currentStoreData.getMetaData(), clr);
              if (!tablesToValidate.contains(t)) {
                tablesToValidate.add(t);
              }
            }
          }
        }
        rdbmsStoreData = storeDataMgr.getManagedStoreData().toArray(new RDBMSStoreData[storeDataMgr.size()]);
      } while((tablesRecentlyInitialized.size() > numTablesInitializedInit) || (rdbmsStoreData.length > numStoreDataInit));
      for (int j = 0; j < tablesRecentlyInitialized.size(); j++) {
        tablesRecentlyInitialized.get(j).postInitialize(clr);
      }
      for (Table t : tablesRecentlyInitialized) {
        if (t instanceof ViewImpl) {
          viewsToValidate.add(t);
        } else {
          if (!tablesToValidate.contains(t)) {
            tablesToValidate.add(t);
          }
        }
      }
      return new List[] { tablesToValidate, viewsToValidate };
    }

    /**
         * Validate tables.
         * @param tablesToValidate list of TableImpl to validate
         * @param clr the ClassLoaderResolver
         * @return an array of List where index == 0 has a list of the tables created, index == 1 has a list of the contraints created
         * @throws SQLException When an error occurs in validation
         */
    private List[] performTablesValidation(List<Table> tablesToValidate, ClassLoaderResolver clr, List<Throwable> autoCreateErrors) throws SQLException {
      List<Table> tableConstraintsCreated = new ArrayList<>();
      List<Table> tablesCreated = new ArrayList<>();
      if (ddlWriter != null) {
        List<Table> tmpTablesToValidate = new ArrayList<>();
        for (Table tbl : tablesToValidate) {
          if (!tmpTablesToValidate.contains(tbl)) {
            tmpTablesToValidate.add(tbl);
          }
        }
        tablesToValidate = tmpTablesToValidate;
      }
      Iterator<Table> i = tablesToValidate.iterator();
      while (i.hasNext()) {
        TableImpl t = (TableImpl) i.next();
        boolean columnsValidated = false;
        boolean columnsInitialised = false;
        if (checkExistTablesOrViews) {
          if (ddlWriter != null) {
            try {
              if (t instanceof ClassTable) {
                ddlWriter.write("-- Table " + t.toString() + " for classes " + StringUtils.objectArrayToString(((ClassTable) t).getManagedClasses()) + "\n");
              } else {
                if (t instanceof JoinTable) {
                  ddlWriter.write("-- Table " + t.toString() + " for join relationship\n");
                }
              }
            } catch (IOException ioe) {
              NucleusLogger.DATASTORE_SCHEMA.error("error writing DDL into file for table " + t, ioe);
            }
          }
          if (!tablesCreated.contains(t) && t.exists(getCurrentConnection(), rdbmsMgr.getSchemaHandler().isAutoCreateTables())) {
            tablesCreated.add(t);
            columnsValidated = true;
          } else {
            if (t.isInitializedModified() || rdbmsMgr.getSchemaHandler().isAutoCreateColumns()) {
              t.validateColumns(getCurrentConnection(), false, rdbmsMgr.getSchemaHandler().isAutoCreateColumns(), autoCreateErrors);
              columnsValidated = true;
            }
          }
        }
        if (rdbmsMgr.getSchemaHandler().isValidateTables() && !columnsValidated) {
          t.validate(getCurrentConnection(), rdbmsMgr.getSchemaHandler().isValidateColumns(), false, autoCreateErrors);
          columnsInitialised = rdbmsMgr.getSchemaHandler().isValidateColumns();
        }
        if (!columnsInitialised) {
          String initInfo = getStringProperty(RDBMSPropertyNames.PROPERTY_RDBMS_INIT_COLUMN_INFO).toUpperCase();
          if (initInfo.equals("PK")) {
            t.initializeColumnInfoForPrimaryKeyColumns(getCurrentConnection());
          } else {
            if (initInfo.equals("ALL")) {
              t.initializeColumnInfoFromDatastore(getCurrentConnection());
            }
          }
        }
        invalidateColumnInfoForTable(t);
      }
      i = tablesToValidate.iterator();
      while (i.hasNext()) {
        TableImpl t = (TableImpl) i.next();
        if (rdbmsMgr.getSchemaHandler().isValidateConstraints() || rdbmsMgr.getSchemaHandler().isAutoCreateConstraints()) {
          if (ddlWriter != null) {
            try {
              if (t instanceof ClassTable) {
                ddlWriter.write("-- Constraints for table " + t.toString() + " for class(es) " + StringUtils.objectArrayToString(((ClassTable) t).getManagedClasses()) + "\n");
              } else {
                ddlWriter.write("-- Constraints for table " + t.toString() + "\n");
              }
            } catch (IOException ioe) {
              NucleusLogger.DATASTORE_SCHEMA.error("error writing DDL into file for table " + t, ioe);
            }
          }
          if (tablesCreated.contains(t) && !hasDuplicateTablesFromList(tablesToValidate)) {
            if (t.createConstraints(getCurrentConnection(), autoCreateErrors, clr)) {
              tableConstraintsCreated.add(t);
            }
          } else {
            if (t.validateConstraints(getCurrentConnection(), rdbmsMgr.getSchemaHandler().isAutoCreateConstraints(), autoCreateErrors, clr)) {
              tableConstraintsCreated.add(t);
            }
          }
          if (ddlWriter != null) {
            try {
              ddlWriter.write("\n");
            } catch (IOException ioe) {
              NucleusLogger.DATASTORE_SCHEMA.error("error writing DDL into file for table " + t, ioe);
            }
          }
        }
      }
      return new List[] { tablesCreated, tableConstraintsCreated };
    }

    /**
         * Check if duplicated tables are in the list.
         * @param newTables the list of DatastoreContainerObject
         * @return true if duplicated tables are in the list
         */
    private boolean hasDuplicateTablesFromList(List<Table> newTables) {
      Map<String, Table> map = new HashMap<>();
      for (int i = 0; i < newTables.size(); i++) {
        Table t1 = newTables.get(i);
        if (map.containsKey(t1.getIdentifier().getName())) {
          return true;
        }
        map.put(t1.getIdentifier().getName(), t1);
      }
      return false;
    }

    /**
         * Validate the supplied views.
         * @param viewsToValidate list of ViewImpl to validate
         * @return list of the views created
         * @throws SQLException
         */
    private List<Table> performViewsValidation(List<Table> viewsToValidate, List<Throwable> autoCreateErrors) throws SQLException {
      List<Table> viewsCreated = new ArrayList<>();
      Iterator i = viewsToValidate.iterator();
      while (i.hasNext()) {
        ViewImpl v = (ViewImpl) i.next();
        if (checkExistTablesOrViews) {
          if (v.exists(getCurrentConnection(), rdbmsMgr.getSchemaHandler().isAutoCreateTables())) {
            viewsCreated.add(v);
          }
        }
        if (rdbmsMgr.getSchemaHandler().isValidateTables()) {
          v.validate(getCurrentConnection(), true, false, autoCreateErrors);
        }
        invalidateColumnInfoForTable(v);
      }
      return viewsCreated;
    }

    /**
         * Rollback / Compensate schema creation by dropping tables, views, constraints and
         * deleting entries in the auto start mechanism.
         * @param viewsCreated the views created that must be dropped
         * @param tableConstraintsCreated the constraints created that must be dropped
         * @param tablesCreated the tables created that must be dropped
         */
    private void rollbackSchemaCreation(List<Table> viewsCreated, List<Table> tableConstraintsCreated, List<Table> tablesCreated) {
      if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
        NucleusLogger.DATASTORE_SCHEMA.debug(Localiser.msg("050040"));
      }
      try {
        if (viewsCreated != null) {
          ListIterator li = viewsCreated.listIterator(viewsCreated.size());
          while (li.hasPrevious()) {
            ((ViewImpl) li.previous()).drop(getCurrentConnection());
          }
        }
        if (tableConstraintsCreated != null) {
          ListIterator li = tableConstraintsCreated.listIterator(tableConstraintsCreated.size());
          while (li.hasPrevious()) {
            ((TableImpl) li.previous()).dropConstraints(getCurrentConnection());
          }
        }
        if (tablesCreated != null) {
          ListIterator li = tablesCreated.listIterator(tablesCreated.size());
          while (li.hasPrevious()) {
            ((TableImpl) li.previous()).drop(getCurrentConnection());
          }
        }
      } catch (Exception e) {
        NucleusLogger.DATASTORE_SCHEMA.warn(Localiser.msg("050041", e));
      }
      AutoStartMechanism starter = rdbmsMgr.getNucleusContext().getAutoStartMechanism();
      if (starter != null) {
        try {
          if (!starter.isOpen()) {
            starter.open();
          }
          for (RDBMSStoreData sd : schemaDataAdded) {
            starter.deleteClass(sd.getName());
          }
        }  finally {
          if (starter.isOpen()) {
            starter.close();
          }
        }
      }
    }

    /**
         * Called by Mapping objects in the midst of RDBMSManager.addClasses()
         * to request the creation of a join table to hold a containers' contents.
         * @param ownerTable Table of the owner of this member
         * @param mmd The member metadata for this member
         * @param type The type of the join table
         */
    private Table addJoinTableForContainer(Table ownerTable, AbstractMemberMetaData mmd, ClassLoaderResolver clr, int type) {
      RDBMSStoreData sd = (RDBMSStoreData) storeDataMgr.get(mmd);
      DatastoreIdentifier tableName = (sd != null && sd.getDatastoreIdentifier() != null) ? tableName = sd.getDatastoreIdentifier() : identifierFactory.newTableIdentifier(mmd);
      Table join = null;
      if (type == JOIN_TABLE_COLLECTION) {
        join = new CollectionTable(ownerTable, tableName, mmd, RDBMSStoreManager.this);
      } else {
        if (type == JOIN_TABLE_MAP) {
          join = new MapTable(ownerTable, tableName, mmd, RDBMSStoreManager.this);
        } else {
          if (type == JOIN_TABLE_ARRAY) {
            join = new ArrayTable(ownerTable, tableName, mmd, RDBMSStoreManager.this);
          } else {
            if (type == JOIN_TABLE_PERSISTABLE) {
              join = new PersistableJoinTable(ownerTable, tableName, mmd, RDBMSStoreManager.this);
            }
          }
        }
      }
      AutoStartMechanism starter = rdbmsMgr.getNucleusContext().getAutoStartMechanism();
      try {
        if (starter != null && !starter.isOpen()) {
          starter.open();
        }
        RDBMSStoreData data = new RDBMSStoreData(mmd, join);
        schemaDataAdded.add(data);
        rdbmsMgr.registerStoreData(data);
      }  finally {
        if (starter != null && starter.isOpen()) {
          starter.close();
        }
      }
      return join;
    }
  }

  public void createDatabase(String catalogName, String schemaName, Properties props) {
    schemaHandler.createDatabase(catalogName, schemaName, props, null);
  }

  public void deleteDatabase(String catalogName, String schemaName, Properties props) {
    schemaHandler.deleteDatabase(catalogName, schemaName, props, null);
  }

  public void createSchemaForClasses(Set<String> classNames, Properties props) {
    String ddlFilename = props != null ? props.getProperty("ddlFilename") : null;
    String completeDdlProp = props != null ? props.getProperty("completeDdl") : null;
    boolean completeDdl = Boolean.valueOf(completeDdlProp);
    String autoStartProp = props != null ? props.getProperty("autoStartTable") : null;
    boolean autoStart = Boolean.valueOf(autoStartProp);
    if (classNames != null && !classNames.isEmpty()) {
      ClassLoaderResolver clr = nucleusContext.getClassLoaderResolver(null);
      FileWriter ddlFileWriter = null;
      try {
        if (ddlFilename != null) {
          File ddlFile = StringUtils.getFileForFilename(ddlFilename);
          if (ddlFile.exists()) {
            ddlFile.delete();
          }
          if (ddlFile.getParentFile() != null && !ddlFile.getParentFile().exists()) {
            ddlFile.getParentFile().mkdirs();
          }
          ddlFile.createNewFile();
          ddlFileWriter = new FileWriter(ddlFile);
          SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
          ddlFileWriter.write("-- ----------------------------------------------------------------\n");
          ddlFileWriter.write("-- DataNucleus SchemaTool " + "(ran at " + fmt.format(new java.util.Date()) + ")\n");
          ddlFileWriter.write("-- ----------------------------------------------------------------\n");
          if (completeDdl) {
            ddlFileWriter.write("-- Complete schema required for the following classes:-\n");
          } else {
            ddlFileWriter.write("-- Schema diff for " + getConnectionURL() + " and the following classes:-\n");
          }
          Iterator classNameIter = classNames.iterator();
          while (classNameIter.hasNext()) {
            ddlFileWriter.write("--     " + classNameIter.next() + "\n");
          }
          ddlFileWriter.write("--\n");
        }
        try {
          if (ddlFileWriter != null) {
            this.ddlWriter = ddlFileWriter;
            this.completeDDL = completeDdl;
            this.writtenDdlStatements = new HashSet<>();
          }
          String[] classNamesArr = getNucleusContext().getTypeManager().filterOutSupportedSecondClassNames(classNames.toArray(new String[classNames.size()]));
          if (classNamesArr.length > 0) {
            int isolationLevel = hasProperty(PropertyNames.PROPERTY_SCHEMA_TXN_ISOLATION) ? TransactionUtils.getTransactionIsolationLevelForName(getStringProperty(PropertyNames.PROPERTY_SCHEMA_TXN_ISOLATION)) : dba.getTransactionIsolationForSchemaCreation();
            new ClassAdder(classNamesArr, ddlFileWriter, isolationLevel).execute(clr);
          }
          if (autoStart) {
            if (ddlFileWriter != null) {
              try {
                ddlFileWriter.write("\n");
                ddlFileWriter.write("-- ----------------------------------------------------------------\n");
                ddlFileWriter.write("-- Table for SchemaTable auto-starter\n");
              } catch (IOException ioe) {
              }
            }
            new SchemaAutoStarter(this, clr);
          }
          if (ddlFileWriter != null) {
            this.ddlWriter = null;
            this.completeDDL = false;
            this.writtenDdlStatements.clear();
            this.writtenDdlStatements = null;
          }
          if (ddlFileWriter != null) {
            ddlFileWriter.write("\n");
            ddlFileWriter.write("-- ----------------------------------------------------------------\n");
            ddlFileWriter.write("-- Sequences and SequenceTables\n");
          }
          createSchemaSequences(classNames, clr, ddlFileWriter);
        }  finally {
          if (ddlFileWriter != null) {
            ddlFileWriter.close();
          }
        }
      } catch (IOException ioe) {
        NucleusLogger.DATASTORE_SCHEMA.error("Exception thrown writing DDL file", ioe);
      }
    } else {
      String msg = Localiser.msg("014039");
      NucleusLogger.DATASTORE_SCHEMA.error(msg);
      System.out.println(msg);
      throw new NucleusException(msg);
    }
  }

  protected void createSchemaSequences(Set<String> classNames, ClassLoaderResolver clr, FileWriter ddlWriter) {
    if (classNames != null && classNames.size() > 0) {
      Set<String> seqTablesGenerated = new HashSet<>();
      Set<String> sequencesGenerated = new HashSet<>();
      for (String className : classNames) {
        AbstractClassMetaData cmd = getMetaDataManager().getMetaDataForClass(className, clr);
        if (cmd.getDatastoreIdentityMetaData() != null && cmd.getDatastoreIdentityMetaData().getValueStrategy() != null) {
          if (cmd.getDatastoreIdentityMetaData().getValueStrategy() == ValueGenerationStrategy.INCREMENT) {
            addSequenceTableForMetaData(cmd.getDatastoreIdentityMetaData(), clr, seqTablesGenerated);
          } else {
            if (cmd.getDatastoreIdentityMetaData().getValueStrategy() == ValueGenerationStrategy.SEQUENCE) {
              String seqName = cmd.getDatastoreIdentityMetaData().getSequence();
              if (StringUtils.isWhitespace(seqName)) {
                seqName = cmd.getDatastoreIdentityMetaData().getValueGeneratorName();
              }
              if (!StringUtils.isWhitespace(seqName)) {
                addSequenceForMetaData(cmd.getDatastoreIdentityMetaData(), seqName, clr, sequencesGenerated, ddlWriter);
              }
            }
          }
        }
        int[] valueGenMemberPositions = cmd.getValueGenerationMemberPositions();
        if (valueGenMemberPositions != null) {
          for (int i = 0; i < valueGenMemberPositions.length; i++) {
            AbstractMemberMetaData valueGenMmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(valueGenMemberPositions[i]);
            if (valueGenMmd.getValueStrategy() == ValueGenerationStrategy.INCREMENT) {
              addSequenceTableForMetaData(valueGenMmd, clr, seqTablesGenerated);
            } else {
              if (valueGenMmd.getValueStrategy() == ValueGenerationStrategy.SEQUENCE) {
                String seqName = valueGenMmd.getSequence();
                if (StringUtils.isWhitespace(seqName)) {
                  seqName = valueGenMmd.getValueGeneratorName();
                }
                if (!StringUtils.isWhitespace(seqName)) {
                  addSequenceForMetaData(valueGenMmd, seqName, clr, sequencesGenerated, ddlWriter);
                }
              }
            }
          }
        }
      }
    }
  }

  protected void addSequenceTableForMetaData(MetaData md, ClassLoaderResolver clr, Set<String> seqTablesGenerated) {
    String catName = null;
    String schName = null;
    String tableName = TableGenerator.DEFAULT_TABLE_NAME;
    String seqColName = TableGenerator.DEFAULT_SEQUENCE_COLUMN_NAME;
    String nextValColName = TableGenerator.DEFAULT_NEXTVALUE_COLUMN_NAME;
    if (md.hasExtension(ValueGenerator.PROPERTY_SEQUENCETABLE_CATALOG)) {
      catName = md.getValueForExtension(ValueGenerator.PROPERTY_SEQUENCETABLE_CATALOG);
    }
    if (md.hasExtension(ValueGenerator.PROPERTY_SEQUENCETABLE_SCHEMA)) {
      schName = md.getValueForExtension(ValueGenerator.PROPERTY_SEQUENCETABLE_SCHEMA);
    }
    if (md.hasExtension(ValueGenerator.PROPERTY_SEQUENCETABLE_TABLE)) {
      tableName = md.getValueForExtension(ValueGenerator.PROPERTY_SEQUENCETABLE_TABLE);
    }
    if (md.hasExtension(ValueGenerator.PROPERTY_SEQUENCETABLE_NAME_COLUMN)) {
      seqColName = md.getValueForExtension(ValueGenerator.PROPERTY_SEQUENCETABLE_NAME_COLUMN);
    }
    if (md.hasExtension(ValueGenerator.PROPERTY_SEQUENCETABLE_NEXTVAL_COLUMN)) {
      nextValColName = md.getValueForExtension(ValueGenerator.PROPERTY_SEQUENCETABLE_NEXTVAL_COLUMN);
    }
    if (!seqTablesGenerated.contains(tableName)) {
      ManagedConnection mconn = connectionMgr.getConnection(TransactionIsolation.NONE);
      Connection conn = (Connection) mconn.getConnection();
      try {
        DatastoreIdentifier tableIdentifier = identifierFactory.newTableIdentifier(tableName);
        if (catName != null) {
          tableIdentifier.setCatalogName(catName);
        }
        if (schName != null) {
          tableIdentifier.setSchemaName(schName);
        }
        SequenceTable seqTable = new SequenceTable(tableIdentifier, this, seqColName, nextValColName);
        seqTable.initialize(clr);
        seqTable.exists(conn, true);
      } catch (Exception e) {
      } finally {
        mconn.release();
      }
      seqTablesGenerated.add(tableName);
    }
  }

  protected void addSequenceForMetaData(MetaData md, String seq, ClassLoaderResolver clr, Set<String> sequencesGenerated, FileWriter ddlWriter) {
    String seqName = seq;
    Integer min = null;
    Integer max = null;
    Integer start = null;
    Integer increment = null;
    Integer cacheSize = null;
    SequenceMetaData seqmd = getMetaDataManager().getMetaDataForSequence(clr, seq);
    if (seqmd != null) {
      seqName = seqmd.getDatastoreSequence();
      if (seqmd.getAllocationSize() > 0) {
        increment = Integer.valueOf(seqmd.getAllocationSize());
      }
      if (seqmd.getInitialValue() >= 0) {
        start = Integer.valueOf(seqmd.getInitialValue());
      }
      md = seqmd;
    }
    if (md.hasExtension(ValueGenerator.PROPERTY_KEY_MIN_VALUE)) {
      min = Integer.valueOf(md.getValueForExtension(ValueGenerator.PROPERTY_KEY_MIN_VALUE));
    }
    if (md.hasExtension(ValueGenerator.PROPERTY_KEY_MAX_VALUE)) {
      max = Integer.valueOf(md.getValueForExtension(ValueGenerator.PROPERTY_KEY_MAX_VALUE));
    }
    if (md.hasExtension(ValueGenerator.PROPERTY_KEY_CACHE_SIZE)) {
      increment = Integer.valueOf(md.getValueForExtension(ValueGenerator.PROPERTY_KEY_CACHE_SIZE));
    }
    if (md.hasExtension(ValueGenerator.PROPERTY_KEY_INITIAL_VALUE)) {
      start = Integer.valueOf(md.getValueForExtension(ValueGenerator.PROPERTY_KEY_INITIAL_VALUE));
    }
    if (md.hasExtension(ValueGenerator.PROPERTY_KEY_DATABASE_CACHE_SIZE)) {
      cacheSize = Integer.valueOf(md.getValueForExtension(ValueGenerator.PROPERTY_KEY_DATABASE_CACHE_SIZE));
    }
    if (!sequencesGenerated.contains(seqName)) {
      String stmt = getDatastoreAdapter().getSequenceCreateStmt(seqName, min, max, start, increment, cacheSize);
      if (ddlWriter != null) {
        try {
          ddlWriter.write(stmt + ";\n");
        } catch (IOException ioe) {
        }
      } else {
        PreparedStatement ps = null;
        ManagedConnection mconn = connectionMgr.getConnection(TransactionIsolation.NONE);
        try {
          ps = sqlController.getStatementForUpdate(mconn, stmt, false);
          sqlController.executeStatementUpdate(null, mconn, stmt, ps, true);
        } catch (SQLException e) {
        } finally {
          try {
            if (ps != null) {
              sqlController.closeStatement(mconn, ps);
            }
          } catch (SQLException e) {
          }
          mconn.release();
        }
      }
      sequencesGenerated.add(seqName);
    }
  }

  boolean performingDeleteSchemaForClasses = false;

  public void deleteSchemaForClasses(Set<String> classNames, Properties props) {
    if (!classNames.isEmpty()) {
      String ddlFilename = props != null ? props.getProperty("ddlFilename") : null;
      String completeDdlProp = props != null ? props.getProperty("completeDdl") : null;
      boolean completeDdl = completeDdlProp != null && completeDdlProp.equalsIgnoreCase("true");
      String autoStartProp = props != null ? props.getProperty("autoStartTable") : null;
      boolean autoStart = autoStartProp != null && autoStartProp.equalsIgnoreCase("true");
      ClassLoaderResolver clr = nucleusContext.getClassLoaderResolver(null);
      FileWriter ddlFileWriter = null;
      try {
        performingDeleteSchemaForClasses = true;
        if (ddlFilename != null) {
          File ddlFile = StringUtils.getFileForFilename(ddlFilename);
          if (ddlFile.exists()) {
            ddlFile.delete();
          }
          if (ddlFile.getParentFile() != null && !ddlFile.getParentFile().exists()) {
            ddlFile.getParentFile().mkdirs();
          }
          ddlFile.createNewFile();
          ddlFileWriter = new FileWriter(ddlFile);
          SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
          ddlFileWriter.write("------------------------------------------------------------------\n");
          ddlFileWriter.write("-- DataNucleus SchemaTool " + "(ran at " + fmt.format(new java.util.Date()) + ")\n");
          ddlFileWriter.write("------------------------------------------------------------------\n");
          ddlFileWriter.write("-- Delete schema required for the following classes:-\n");
          Iterator classNameIter = classNames.iterator();
          while (classNameIter.hasNext()) {
            ddlFileWriter.write("--     " + classNameIter.next() + "\n");
          }
          ddlFileWriter.write("--\n");
        }
        try {
          if (ddlFileWriter != null) {
            this.ddlWriter = ddlFileWriter;
            this.completeDDL = completeDdl;
            this.writtenDdlStatements = new HashSet<>();
          }
          String[] classNameArray = classNames.toArray(new String[classNames.size()]);
          manageClasses(clr, classNameArray);
          int isolationLevel = hasProperty(PropertyNames.PROPERTY_SCHEMA_TXN_ISOLATION) ? TransactionUtils.getTransactionIsolationLevelForName(getStringProperty(PropertyNames.PROPERTY_SCHEMA_TXN_ISOLATION)) : Connection.TRANSACTION_READ_COMMITTED;
          DeleteTablesSchemaTransaction deleteTablesTxn = new DeleteTablesSchemaTransaction(this, isolationLevel, storeDataMgr);
          deleteTablesTxn.setWriter(ddlWriter);
          boolean success = true;
          try {
            deleteTablesTxn.execute(clr);
          } catch (NucleusException ne) {
            success = false;
            throw ne;
          } finally {
            if (success) {
              clearSchemaData();
            }
          }
          if (autoStart) {
          }
        }  finally {
          performingDeleteSchemaForClasses = false;
          if (ddlFileWriter != null) {
            this.ddlWriter = null;
            this.completeDDL = false;
            this.writtenDdlStatements.clear();
            this.writtenDdlStatements = null;
            ddlFileWriter.close();
          }
        }
      } catch (IOException ioe) {
      }
    } else {
      String msg = Localiser.msg("014039");
      NucleusLogger.DATASTORE_SCHEMA.error(msg);
      System.out.println(msg);
      throw new NucleusException(msg);
    }
  }

  public void validateSchemaForClasses(Set<String> classNames, Properties props) {
    if (classNames != null && !classNames.isEmpty()) {
      manageClasses(nucleusContext.getClassLoaderResolver(null), classNames.toArray(new String[classNames.size()]));
    } else {
      String msg = Localiser.msg("014039");
      NucleusLogger.DATASTORE_SCHEMA.error(msg);
      System.out.println(msg);
      throw new NucleusException(msg);
    }
  }
}