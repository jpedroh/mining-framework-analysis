package com.datumbox.framework.common.persistentstorage.mapdb;
import com.datumbox.framework.common.persistentstorage.abstracts.AbstractDatabaseConnector;
import com.datumbox.framework.common.persistentstorage.interfaces.DatabaseConnector;
import org.mapdb.Atomic;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * The MapDBConnector is responsible for saving and loading data from MapDB files,
 * creating BigMaps which are backed by files and persisting data. The MapDBConnector 
 * does not load all the contents of BigMaps in memory, maintains an LRU cache
 * to speed up data retrieval and persists all data in MapDB files.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class MapDBConnector extends AbstractDatabaseConnector {
  private final String database;

  private final MapDBConfiguration dbConf;

  private enum DBType {
    PRIMARY_DB,
    TEMP_DB_CACHED,
    TEMP_DB_UNCACHED
  }

  /**
     * This list stores all the DB objects which are used to persist the data. This
     * library uses one default and one temporary db.
     */
  private final Map<DBType, DB> dbRegistry = new HashMap<>();

  /** 
     * @param database
     * @param dbConf
     * @see AbstractDatabaseConnector#AbstractDatabaseConnector()
     */
  protected MapDBConnector(String database, MapDBConfiguration dbConf) {
    super();
    this.database = database;
    this.dbConf = dbConf;
    logger.trace("Opened db " + database);
  }

  /** {@inheritDoc} */
  @Override public <T extends Serializable> void saveObject(String name, T serializableObject) {
    assertConnectionOpen();
    DB db = openDB(DBType.PRIMARY_DB);
    Atomic.Var<Object> atomicVar = db.atomicVar(name).createOrOpen();
    Map<String, Object> objRefs = preSerializer(serializableObject);
    atomicVar.set(serializableObject);
    db.commit();
    postSerializer(serializableObject, objRefs);
  }

  /** {@inheritDoc} */
  @Override @SuppressWarnings(value = { "unchecked" }) public <T extends Serializable> T loadObject(String name, Class<T> klass) {
    assertConnectionOpen();
    DB db = openDB(DBType.PRIMARY_DB);
    Atomic.Var<Object> atomicVar = db.atomicVar(name).createOrOpen();
    T serializableObject = klass.cast(atomicVar.get());
    postDeserializer(serializableObject);
    return serializableObject;
  }

  /** {@inheritDoc} */
  @Override public void close() {
    if (isClosed()) {
      return;
    }
    super.close();
    closeDBRegistry();
    logger.trace("Closed db " + database);
  }

  /** {@inheritDoc} */
  @Override public void clear() {
    assertConnectionOpen();
    closeDBRegistry();
    try {
      Path defaultPath = getDefaultPath();
      deleteIfExistsRecursively(defaultPath);
      deleteIfExistsRecursively(Paths.get(defaultPath.toString() + ".p"));
      deleteIfExistsRecursively(Paths.get(defaultPath.toString() + ".t"));
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  /** {@inheritDoc} */
  @Override public <K extends java.lang.Object, V extends java.lang.Object> Map<K, V> getBigMap(String name, DatabaseConnector.MapType type, DatabaseConnector.StorageHint storageHint, boolean isConcurrent, boolean isTemporary) {
    assertConnectionOpen();
    if (storageHint == DatabaseConnector.StorageHint.IN_MEMORY && dbConf.isHybridized()) {
      if (DatabaseConnector.MapType.HASHMAP.equals(type)) {
        return isConcurrent ? new ConcurrentHashMap<>() : new HashMap<>();
      } else {
        if (DatabaseConnector.MapType.TREEMAP.equals(type)) {
          return isConcurrent ? new ConcurrentSkipListMap<>() : new TreeMap<>();
        } else {
          throw new IllegalArgumentException("Unsupported MapType.");
        }
      }
    } else {
      DBType dbType = getDatabaseTypeFromName(name);
      if (dbType == null) {
        if (isTemporary == false) {
          dbType = DBType.PRIMARY_DB;
        } else {
          if (storageHint == DatabaseConnector.StorageHint.IN_MEMORY || storageHint == DatabaseConnector.StorageHint.IN_CACHE) {
            dbType = DBType.TEMP_DB_CACHED;
          } else {
            if (storageHint == DatabaseConnector.StorageHint.IN_DISK) {
              dbType = DBType.TEMP_DB_UNCACHED;
            } else {
              throw new IllegalArgumentException("Unsupported StorageHint.");
            }
          }
        }
      }
      DB db = openDB(dbType);
      Map<K, V> map;
      if (DatabaseConnector.MapType.HASHMAP.equals(type)) {
        map = (Map<K, V>) db.hashMap(name).counterEnable().createOrOpen();
      } else {
        if (DatabaseConnector.MapType.TREEMAP.equals(type)) {
          map = (Map<K, V>) db.treeMap(name).counterEnable().createOrOpen();
        } else {
          throw new IllegalArgumentException("Unsupported MapType.");
        }
      }
      return map;
    }
  }

  /** {@inheritDoc} */
  @Override public <T extends Map> void dropBigMap(String name, T map) {
    assertConnectionOpen();
    DBType dbType = getDatabaseTypeFromName(name);
    if (dbType != null) {
      DB db = dbRegistry.get(dbType);
      if (isOpenDB(db)) {
      }
    } else {
      map.clear();
    }
  }

  /** {@inheritDoc} */
  @Override public String getDatabaseName() {
    return database;
  }

  private boolean isOpenDB(DB db) {
    return !(db == null || db.isClosed());
  }

  /**
     * Opens the DB (if not already open) and returns the DB object.
     * 
     * @param dbType
     * @return 
     */
  private DB openDB(DBType dbType) {
    DB db = dbRegistry.get(dbType);
    if (!isOpenDB(db)) {
      DBMaker.Maker m;
      boolean permitCaching = true;
      if (dbType == DBType.PRIMARY_DB) {
        m = DBMaker.fileDB(getDefaultPath().toFile());
      } else {
        if (dbType == DBType.TEMP_DB_CACHED || dbType == DBType.TEMP_DB_UNCACHED) {
          m = DBMaker.tempFileDB().deleteFilesAfterClose();
          if (dbType == DBType.TEMP_DB_UNCACHED) {
            permitCaching = false;
          }
        } else {
          throw new IllegalArgumentException("Unsupported DatabaseType.");
        }
      }
      if (dbConf.isCompressed()) {
      }
      if (permitCaching && dbConf.getCacheSize() > 0) {
      }
      m = m.closeOnJvmShutdown();
      db = m.make();
      dbRegistry.put(dbType, db);
    }
    return db;
  }

  /**
     * Returns the DatabaseType using the name of the map. It assumes that names 
     * are unique across all DatabaseType. If not found null is returned.
     * 
     * @param name
     * @return 
     */
  private DBType getDatabaseTypeFromName(String name) {
    for (Map.Entry<DBType, DB> entry : dbRegistry.entrySet()) {
      DB db = entry.getValue();
      if (isOpenDB(db) && db.exists(name)) {
        return entry.getKey();
      }
    }
    return null;
  }

  /**
     * It closes all the DBs stored in the registry.
     */
  private void closeDBRegistry() {
    for (DB db : dbRegistry.values()) {
      if (isOpenDB(db)) {
        db.close();
      }
    }
    dbRegistry.clear();
  }

  private Path getDefaultPath() {
    String outputFolder = this.dbConf.getOutputFolder();
    if (outputFolder == null || outputFolder.isEmpty()) {
      outputFolder = System.getProperty("java.io.tmpdir");
    }
    return Paths.get(outputFolder + File.separator + database);
  }
}