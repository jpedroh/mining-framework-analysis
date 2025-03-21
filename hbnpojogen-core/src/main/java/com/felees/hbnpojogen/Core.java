package com.felees.hbnpojogen;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import com.felees.hbnpojogen.db.FieldObj;
import com.felees.hbnpojogen.db.KeyObj;
import com.felees.hbnpojogen.db.TableObj;
import com.felees.hbnpojogen.obj.Clazz;
import com.felees.hbnpojogen.obj.GeneratorEnum;
import com.felees.hbnpojogen.obj.JoinTable;
import com.felees.hbnpojogen.obj.PropertyObj;

/**
 * Main routines.
 *
 * @author wallacew
 *
 */
public class Core {
  /**
	 * One of the main functions - builds a table object model of the database
	 *
	 * @param dbmd metadata connection
	 * @param dbCatalog Catalog in use
	 * @param connection Connection handle
	 * @param commitOrder List of tables
	 * @throws SQLException
	 */
  public static void parseTables(final DatabaseMetaData dbmd, final String dbCatalog, final Connection connection, LinkedList<String> commitOrder) throws SQLException {
    TreeSet<String> commitOrderCleanup = new TreeSet<String>(new CaseInsensitiveComparator());
    TreeSet<String> fakeEnumTables = new TreeSet<String>(new CaseInsensitiveComparator());
    boolean inError = false;
    for (String tableName : commitOrder) {
      if (!(checkInIgnoreList(tableName))) {
        State.getInstance().tables.put(SyncUtils.upfirstChar(tableName), new TableObj());
      }
    }
    for (String tableName : commitOrder) {
      if (checkInIgnoreList(tableName)) {
        continue;
      }
      TableObj tableObj = State.getInstance().tables.get(tableName);
      tableObj.setDbName(SyncUtils.getTableName(tableName));
      tableObj.setDbCat(SyncUtils.getTableCatalog(tableName));
      tableObj.setDbSchema(SyncUtils.getTableSchema(tableName));
      tableObj.setName(SyncUtils.upfirstChar(SyncUtils.getTableName(tableName)));
      tableObj.setTestHandle(SyncUtils.getTableName(tableName));
      tableObj.setViewTable(SyncUtils.getViewSet().contains(tableName));
      ResultSet table = dbmd.getTables(tableObj.getDbCat(), tableObj.getDbSchema(), SyncUtils.getTableName(tableName), new String[] { "TABLE" });
      if (table.next()) {
        String remarks = table.getString("REMARKS");
        tableObj.setComment(remarks == null ? "" : remarks);
      }
      table.close();
      if (State.getInstance().isVersionCheckEnabled() && !skipSchemaWrite(SyncUtils.getTableCatalog(tableName))) {
        extractVersionInfo(dbmd, tableObj);
      }
      TreeSet<String> natKeys = State.getInstance().naturalKeys.get(tableObj.getFullTableName());
      ResultSet indexes = dbmd.getPrimaryKeys(tableObj.getDbCat(), tableObj.getDbSchema(), tableObj.getDbName());
      int indices = 0;
      while (indexes.next()) {
        String col = indexes.getString(Constants.COLUMN_NAME);
        tableObj.getPrimaryKeys().add(col);
        ResultSet seqs = dbmd.getTables(tableObj.getDbCat(), tableObj.getDbSchema(), tableObj.getDbName() + "_" + col + "_seq", new String[] { "SEQUENCE" });
        while (seqs.next()) {
          String sequenceName = seqs.getString("table_name");
          tableObj.getPrimaryKeySequences().put(col, tableObj.getDbCat() + "." + tableObj.getDbSchema() + "." + sequenceName);
        }
        indices++;
      }
      if (State.getInstance().linkTables.get(tableName) != null) {
        int primaryKeysCount = tableObj.getPrimaryKeys().size();
        if (primaryKeysCount == 2) {
          tableObj.getPrimaryKeys().clear();
        }
      }
      indexes.close();
      String tmpTableName = tableName;
      String rsQuery = String.format("SELECT * FROM `%s`.`%s` WHERE 1=2", SyncUtils.getTableCatalog(tmpTableName), SyncUtils.getTableName(tmpTableName));
      if (State.getInstance().dbMode == 1) {
        tmpTableName = SyncUtils.getTableName(tmpTableName);
        rsQuery = String.format("SELECT * FROM %s  WHERE 1=2 ", tmpTableName);
      }
      if (State.getInstance().dbMode == 2) {
        rsQuery = String.format("SELECT * FROM %s.%s.%s WHERE 1=2", SyncUtils.getTableCatalog(tmpTableName), SyncUtils.getTableSchema(tmpTableName), SyncUtils.getTableName(tmpTableName));
      }
      ResultSet rs;
      rs = dbmd.getConnection().createStatement().executeQuery(rsQuery);
      ResultSetMetaData rsmd = rs.getMetaData();
      int numberOfColumns = rsmd.getColumnCount();
      ResultSet fieldNames = dbmd.getColumns(SyncUtils.getTableCatalog(tableName), SyncUtils.getTableSchema(tableName), SyncUtils.getTableName(tableName), "%");
      for (int i = 1; i <= numberOfColumns; i++) {
        String typeName = "";
        int sqlType = 0;
        String defaultValue = null;
        String remarks = "";
        fieldNames.next();
        if (State.getInstance().dbMode == 0) {
          typeName = fieldNames.getString(Constants.TYPE_NAME).toUpperCase();
        } else {
          typeName = rsmd.getColumnTypeName(i);
          sqlType = rsmd.getColumnType(i);
        }
        defaultValue = fieldNames.getString("COLUMN_DEF");
        remarks = fieldNames.getString("REMARKS");
        String colName = rsmd.getColumnName(i).toLowerCase();
        if (!Character.isJavaIdentifierStart(colName.charAt(0))) {
          HbnPojoGen.logE("Field name cannot be mapped to a valid java identifier. Field: " + colName + ", table: " + tableName);
        }
        if (indices == 0 && tableObj.isViewTable()) {
          tableObj.getPrimaryKeys().add(colName);
          HbnPojoGen.logE("Found a view without any keys. Marking all fields as part of the id because otherwise Hibernate wouldn\'t work. View: " + tableName);
        }
        if (State.getInstance().ignoreFieldList.containsKey("*.*." + colName) || State.getInstance().ignoreFieldList.containsKey(tableName + ".*") || State.getInstance().ignoreFieldList.containsKey(tableName + "." + colName)) {
          List<String> exceptions = State.getInstance().ignoreFieldList.get("*.*." + colName);
          if (exceptions == null) {
            exceptions = State.getInstance().ignoreFieldList.get(tableName + ".*");
            if (exceptions == null) {
              exceptions = State.getInstance().ignoreFieldList.get(tableName + "." + colName);
            }
          }
          if (exceptions == null) {
            continue;
          }
          String tblCat = SyncUtils.getTableCatalog(tableName);
          String tblName = SyncUtils.getTableName(tableName);
          boolean matched = true;
          for (String e : exceptions) {
            if (e.equalsIgnoreCase(tblCat + "." + tblName) || e.equalsIgnoreCase(tblCat + ".*") || e.equalsIgnoreCase("*." + tblName)) {
              matched = false;
              break;
            }
          }
          if (matched) {
            continue;
          }
        }
        FieldObj fo = new FieldObj();
        boolean defValue = defaultValue != null && !defaultValue.equalsIgnoreCase("") && !defaultValue.equalsIgnoreCase("NULL");
        fo.setDefaultValue(defValue);
        if (defValue && defaultValue.startsWith("nextval(")) {
          String sequenceName = defaultValue.substring(defaultValue.indexOf('\'') + 1, defaultValue.lastIndexOf("\'"));
          tableObj.getPrimaryKeySequences().put(colName, sequenceName.indexOf(".") > -1 ? tableObj.getDbCat() + "." + sequenceName : sequenceName);
        }
        fo.setTableObj(tableObj);
        fo.setFieldType(rsmd.getColumnType(i));
        fo.setLength(rsmd.getColumnDisplaySize(i));
        fo.setPrecision(rsmd.getPrecision(i));
        fo.setScale(rsmd.getScale(i));
        fo.setFieldColumnType(rsmd.getColumnTypeName(i));
        fo.setFieldTypeUnsigned(!rsmd.isSigned(i));
        fo.setNullable(rsmd.isNullable(i) == ResultSetMetaData.columnNullable);
        fo.setAutoInc(rsmd.isAutoIncrement(i));
        fo.setComment(remarks == null ? "" : remarks);
        if ((natKeys != null) && natKeys.contains(colName)) {
          tableObj.getPrimaryKeys().add(colName);
          tableObj.getNaturalKeys().put(colName, fo);
        }
        fo.setPrimaryKey(tableObj.getPrimaryKeys().contains(colName));
        fo.setName(colName);
        TreeMap<String, String> renameLinks = State.getInstance().renameFieldMap.get(SyncUtils.removeUnderscores(tableObj.getDbCat()));
        if (renameLinks != null) {
          String dstName = renameLinks.get(SyncUtils.removeUnderscores(tableObj.getDbName()) + "." + SyncUtils.removeUnderscores(fo.getName()));
          if (dstName != null) {
            String[] tmpRename = dstName.split(":");
            fo.setAlias(tmpRename[0]);
            fo.setAliasInverse(tmpRename[1]);
          }
        }
        TreeMap<String, EnumMapping> enumLinks = State.getInstance().enumAsLinkMaps.get(SyncUtils.removeUnderscores(tableObj.getDbCat() + "." + (tableObj.getDbSchema() == null ? "" : tableObj.getDbSchema())));
        boolean fakeEnum = false;
        EnumMapping dstTableMap = null;
        if (enumLinks != null) {
          dstTableMap = enumLinks.get(SyncUtils.removeUnderscores(tableObj.getDbName()) + "." + SyncUtils.removeUnderscores(fo.getName()));
          fakeEnum = (dstTableMap != null);
        }
        if (!typeName.toUpperCase().equals("UUID") && (Constants.ENUM.equals(typeName.toUpperCase()) || (sqlType == Types.OTHER && !typeName.equalsIgnoreCase("JSON")) || fakeEnum)) {
          Boolean[] enumsScrubbed = { false };
          if (fakeEnum) {
            EnumResult enumResult = SyncUtils.getEnumValues(connection, dstTableMap.getDsttableFieldname(), colName, enumsScrubbed, true, dstTableMap.getKeyColumnLabel(), dstTableMap.getValueColumnLabel(), dstTableMap.getOtherColumnLabels());
            fo.setEnumValues(enumResult.getEnumText());
            fo.setEnumOtherCols(enumResult.getOtherColumns());
            fo.setFakeEnum(true);
            tableObj.setContainsScrubbedEnum(true);
            String tmp = dstTableMap.getDsttableFieldname();
            if (dstTableMap.getDsttableFieldname() != null && dstTableMap.getDsttableFieldname().indexOf(".") == -1) {
              tmp = fo.getTableObj().getDbCat() + "." + dstTableMap.getDsttableFieldname();
            }
            fakeEnumTables.add(tmp);
            commitOrderCleanup.add(tmp);
          } else {
            fo.setEnumValues(SyncUtils.getEnumValues(connection, tableName, State.getInstance().dbMode == 2 ? typeName : colName, enumsScrubbed));
          }
          tableObj.setContainsScrubbedEnum(enumsScrubbed[0] || tableObj.isContainsScrubbedEnum());
          String name = SyncUtils.upfirstChar(SyncUtils.getTableName(tableName)) + SyncUtils.upfirstChar(colName) + "Enum";
          fo.setEnumName(SyncUtils.removeUnderscores(name));
          fo.setEnumFilename(SyncUtils.removeUnderscores(name));
          TreeMap<String, String> map = State.getInstance().enumMappings.get(tableObj.getDbCat() + "." + (tableObj.getDbSchema() == null ? "" : tableObj.getDbSchema()));
          String tmp = tableObj.getDbName() + "." + fo.getName();
          if ((map != null) && (map.get(tmp) != null)) {
            fo.setEnumFilename(map.get(tmp));
            fo.setEnumName(SyncUtils.removeUnderscores(map.get(tmp)));
          }
        } else {
          fo.setEnumName(null);
        }
        tableObj.getFields().put(colName, fo);
      }
      State.getInstance().tables.put(SyncUtils.upfirstChar(tableName), tableObj);
      rs.close();
    }
    for (TableObj tableObj : State.getInstance().tables.values()) {
      HashSet<RelationItem> relList = State.getInstance().getFakeFKmatched().get(tableObj.getDbCat() + "." + (tableObj.getDbSchema() == null ? "" : tableObj.getDbSchema() + ".") + tableObj.getDbName());
      if (relList == null) {
        relList = new HashSet<RelationItem>();
      }
      ResultSet importedKeys = dbmd.getImportedKeys(tableObj.getDbCat(), tableObj.getDbSchema(), tableObj.getDbName());
      while (importedKeys.next()) {
        RelationItem relItem = new RelationItem();
        relItem.setFkColumnName(importedKeys.getString(Constants.FKCOLUMN_NAME));
        relItem.setPkColumnName(importedKeys.getString(Constants.PKCOLUMN_NAME));
        relItem.setSchema(importedKeys.getString(Constants.PKTABLE_SCHEM));
        relItem.setFkSchema(importedKeys.getString(Constants.FKTABLE_SCHEM));
        relItem.setFkName(importedKeys.getString(Constants.FK_NAME));
        relItem.setCatalog(importedKeys.getString(Constants.PKTABLE_CAT));
        if (relItem.getCatalog() == null) {
          relItem.setCatalog(tableObj.getDbCat());
        }
        relItem.setTableName(importedKeys.getString(Constants.PKTABLE_NAME));
        relItem.setKeySeq(Integer.parseInt(importedKeys.getString(Constants.KEY_SEQ)));
        relItem.setFkCatalog(importedKeys.getString(Constants.FKTABLE_CAT));
        if (relItem.getFkCatalog() == null) {
          relItem.setFkCatalog(tableObj.getDbCat());
        }
        relItem.setFkTableName(importedKeys.getString(Constants.FKTABLE_NAME));
        relList.add(relItem);
      }
      importedKeys.close();
      for (RelationItem relItem : relList) {
        String fkColName = relItem.getFkColumnName();
        String pkColName = relItem.getPkColumnName();
        relItem.getFkSchema();
        String pkTableCat = relItem.getCatalog();
        String pkTableSchema = relItem.getSchema();
        String pkTableName = relItem.getTableName();
        String fkName = relItem.getFkName();
        Integer keySeq = relItem.getKeySeq();
        String pkFullTableName = pkTableCat + "." + (pkTableSchema == null ? "" : pkTableSchema + ".") + pkTableName;
        if ((pkTableCat != null && !dbCatalog.equalsIgnoreCase(pkTableCat) && (State.getInstance().schemaRestrict == 0)) || (State.getInstance().ignoreTableList.contains(pkTableCat + "." + pkTableName)) || (State.getInstance().ignoreTableList.contains(pkTableCat + ".*")) || (State.getInstance().ignoreTableList.contains(pkTableName)) || (State.getInstance().ignoreTableList.contains("*." + pkTableName))) {
          continue;
        }
        if (!State.getInstance().ignoreEverythingExceptList.isEmpty()) {
          if (!(State.getInstance().ignoreEverythingExceptList.contains(pkTableCat + "." + pkTableName) || State.getInstance().ignoreEverythingExceptList.contains(pkTableCat + ".*") || State.getInstance().ignoreEverythingExceptList.contains("*." + pkTableName) || matchIgnoreWildcard(pkTableCat, pkTableName))) {
            continue;
          }
        }
        TreeMap<String, TreeSet<String>> disableLink = State.getInstance().getNoFollowTables().get(tableObj.getDbCat() + "." + tableObj.getDbName());
        if (disableLink == null) {
          disableLink = State.getInstance().getNoFollowTables().get(tableObj.getDbCat() + ".*");
          if (disableLink == null) {
            disableLink = State.getInstance().getNoFollowTables().get("*." + tableObj.getDbName());
          }
        }
        if (disableLink != null) {
          TreeSet<String> toTable = disableLink.get(pkTableCat + "." + pkTableName);
          if (toTable == null) {
            toTable = disableLink.get("*." + pkTableName);
            if (toTable == null) {
              toTable = disableLink.get(pkTableCat + ".*");
              if (toTable == null) {
                toTable = disableLink.get("*.*");
              }
            }
          }
          if (toTable != null) {
            if (toTable.contains("*") || toTable.contains(fkColName)) {
              continue;
            }
          }
        }
        if (tableObj.getImportedKeys().get(fkName) == null) {
          tableObj.getImportedKeys().put(fkName, new KeyObj(fkColName, State.getInstance().tables.get(pkFullTableName).getName(), pkTableCat, pkTableSchema, pkColName));
        } else {
          tableObj.getImportedKeys().get(fkName).getKeyLinks().put(fkColName, pkColName);
        }
        TableObj tableFK = State.getInstance().tables.get(pkFullTableName);
        TreeMap<String, LinkedList<KeyObj>> exportedKeys = tableFK.getExportedKeys();
        if (exportedKeys.get(pkColName) == null) {
          exportedKeys.put(pkColName, new LinkedList<KeyObj>());
        }
        String tbl = relItem.getFkCatalog() + "." + (relItem.getFkSchema() == null ? "" : relItem.getFkSchema() + ".") + relItem.getFkTableName();
        String field = relItem.getFkColumnName();
        exportedKeys.get(pkColName).add(new KeyObj(tbl, field));
        tableObj.getFields().get(fkColName).setForeignKey(true);
        tableObj.getFields().get(fkColName).setForeignColumn(State.getInstance().tables.get(pkFullTableName).getFields().get(pkColName));
        if (fakeEnumTables.contains(pkTableCat + "." + pkTableName) && !tableObj.getFields().get(fkColName).isFakeEnum() && !checkInIgnoreList(tableObj.getFullTableName())) {
          HbnPojoGen.logE("ERROR: Table " + pkTableCat + "." + pkTableName + " was marked as a fake enum table, but " + tableObj.getFullTableName() + " (" + fkColName + " field)" + " is not marked to use it. Go fix your config!");
          inError = true;
          commitOrderCleanup.remove(pkTableCat + "." + pkTableName);
        }
      }
    }
    inError = enumSanityCheck(inError);
    commitOrder.removeAll(commitOrderCleanup);
    if (inError) {
      HbnPojoGen.logE("Errors detected. Halting generation.");
      System.exit(1);
    }
  }

  private static boolean enumSanityCheck(boolean inError) {
    for (Entry<String, TreeMap<String, String>> entry : State.getInstance().enumMappings.entrySet()) {
      String schema = entry.getKey();
      TreeMap<String, String> map = entry.getValue();
      TreeMap<String, TreeSet<String>> search = new TreeMap<String, TreeSet<String>>(new CaseInsensitiveComparator());
      for (Entry<String, String> mapping : map.entrySet()) {
        String definition = mapping.getKey();
        String enumName = mapping.getValue();
        TreeSet<String> enumDefinition = search.get(enumName);
        if (enumDefinition == null) {
          enumDefinition = new TreeSet<String>(new CaseInsensitiveComparator());
          TableObj table = State.getInstance().tables.get(schema + "." + SyncUtils.getTableCatalog(definition));
          if (table != null) {
            FieldObj field = table.getFields().get(SyncUtils.getTableName(definition));
            if (field != null) {
              if (field.isEnum()) {
                TreeSet<String> entries = new TreeSet<String>(new CaseInsensitiveComparator());
                for (String s : field.getEnumValues()) {
                  entries.add(s);
                }
                search.put(enumName, entries);
              }
            }
          }
        } else {
          TableObj table = State.getInstance().tables.get(schema + "." + SyncUtils.getTableCatalog(definition));
          if (table != null) {
            FieldObj field = table.getFields().get(SyncUtils.getTableName(definition));
            if (field != null) {
              if (field.isEnum()) {
                boolean match = true;
                for (String s : field.getEnumValues()) {
                  match = match && enumDefinition.contains(s);
                }
                if (!match) {
                  HbnPojoGen.logE("Enum values are incompatible (coalescing not possible). Fix your config!");
                  String error = "[";
                  for (String s : field.getEnumValues()) {
                    error += s + ", ";
                  }
                  error += "]";
                  HbnPojoGen.logE("Enum #1: " + enumDefinition.toString());
                  HbnPojoGen.logE("Enum #2: " + error);
                  HbnPojoGen.logE("Schema: " + schema + ", Enum name = " + enumName);
                  inError = true;
                }
              }
            }
          }
        }
      }
    }
    return inError;
  }

  /**
	 *
	 *
	 * @param tableName
	 * @return
	 */
  public static boolean checkInIgnoreList(String tableName) {
    if (State.getInstance().ignoreTableList.contains(tableName)) {
      return true;
    }
    String etbl = SyncUtils.getTableName(tableName);
    String ecat = SyncUtils.getTableCatalog(tableName);
    if (!State.getInstance().ignoreEverythingExceptList.isEmpty()) {
      if (!(State.getInstance().ignoreEverythingExceptList.contains(ecat + "." + etbl) || State.getInstance().ignoreEverythingExceptList.contains(ecat + ".*") || State.getInstance().ignoreEverythingExceptList.contains("*." + etbl) || matchIgnoreWildcard(ecat, etbl))) {
        return true;
      }
    }
    return false;
  }

  /**
	 *
	 *
	 * @param pkTableCat
	 * @param pkTableName
	 * @return true if we have a match
	 */
  private static boolean matchIgnoreWildcard(String pkTableCat, String pkTableName) {
    for (String entry : State.getInstance().ignoreEverythingExceptList) {
      String[] cat = entry.split("\\.");
      cat[1] = cat[1].replaceAll("\\*", "\\.\\*");
      if (pkTableCat.equalsIgnoreCase(cat[0]) && pkTableName.toUpperCase().matches(cat[1].toUpperCase())) {
        return true;
      }
    }
    return false;
  }

  private static String filterSign(String clause) {
    String result = clause;
    if (clause.startsWith(">=")) {
      result = clause.substring(2);
    }
    return result;
  }

  /**
	 * @param dbmd
	 * @param tableObj
	 * @throws SQLException
	 */
  private static void extractVersionInfo(final DatabaseMetaData dbmd, TableObj tableObj) throws SQLException {
    ObjectPair<String, Set<String>> defVerMatch = State.getInstance().getVersionCheck().get("*");
    String verCat = tableObj.getDbCat();
    if (State.getInstance().dbMode == 2) {
      verCat += "." + tableObj.getDbSchema();
    }
    if (defVerMatch != null) {
      ObjectPair<String, Set<String>> verMatch = State.getInstance().getVersionCheck().get(tableObj.getDbCat());
      if (verMatch == null) {
        verMatch = defVerMatch;
      }
      if (State.getInstance().versionsRead.get(verCat + "." + verMatch.getKey()) == null) {
        if (!verMatch.getKey().equals(Config.DISABLED_FROM_CONFIG)) {
          String fieldList = "";
          for (String s : verMatch.getValue()) {
            if (s.startsWith(">=")) {
              Set<String> gteSet = State.getInstance().versionGTE.get(verCat + "." + verMatch.getKey());
              if (gteSet == null) {
                gteSet = new HashSet<String>();
                State.getInstance().versionGTE.put(verCat + "." + verMatch.getKey(), gteSet);
              }
              gteSet.add(filterSign(s));
            }
            fieldList += filterSign(s) + ", ";
          }
          fieldList = fieldList.substring(0, fieldList.length() - 2);
          String query = "";
          ResultSet rs = null;
          try {
            String whereClause = State.getInstance().getVersionCheckWhereClause().get(verCat + "." + verMatch.getKey());
            String orderBy = State.getInstance().getVersionCheckOrderBy().get(verCat + "." + verMatch.getKey());
            if (whereClause == null || whereClause.equals("")) {
              whereClause = State.getInstance().getVersionCheckWhereClause().get("*");
            }
            if (orderBy == null || orderBy.equals("")) {
              orderBy = State.getInstance().getVersionCheckOrderBy().get("*");
            }
            if (whereClause != null && !whereClause.equals("")) {
              whereClause = "WHERE " + whereClause;
            }
            if (orderBy != null && !orderBy.equals("")) {
              orderBy = "ORDER BY " + orderBy;
            }
            if (State.getInstance().dbMode == 2) {
              query = String.format("SELECT %s FROM %s.%s %s %s", fieldList, verCat, verMatch.getKey(), whereClause, orderBy);
            } else {
              query = String.format("SELECT %s FROM `%s`.`%s` %s %s", fieldList, verCat, verMatch.getKey(), whereClause, orderBy);
            }
            rs = dbmd.getConnection().createStatement().executeQuery(query);
          } catch (Exception e) {
            HbnPojoGen.logE("Error while attempting to obtain version info. Tried: " + query);
            HbnPojoGen.logE("Go fix your config (versionCheck section). Terminating app.");
            e.printStackTrace();
            System.exit(1);
          }
          if (rs != null) {
            ResultSetMetaData rsmd = rs.getMetaData();
            new TreeMap<String, List<String>>();
            int numberOfColumns = rsmd.getColumnCount();
            List<String> colNames = new LinkedList<String>();
            for (int i = 1; i <= numberOfColumns; i++) {
              String colName = rsmd.getColumnName(i);
              colNames.add(colName);
            }
            State.getInstance().versionColumnsRead.put(verCat + "." + verMatch.getKey(), colNames);
            boolean foundSomething = false;
            List<List<String>> versionRows = new LinkedList<List<String>>();
            while (rs.next()) {
              foundSomething = true;
              List<String> rowValues = new LinkedList<String>();
              for (int i = 1; i <= numberOfColumns; i++) {
                String val = rs.getString(i);
                rowValues.add(val);
              }
              versionRows.add(rowValues);
            }
            State.getInstance().versionsRead.put(verCat + "." + verMatch.getKey(), versionRows);
            if (!foundSomething) {
              HbnPojoGen.logE("Error while attempting to obtain version info. Tried: " + query);
              HbnPojoGen.logE("Go fix your config (versionCheck section) or check to see that you\'ve got some data in the table. Terminating app.");
              System.exit(1);
            }
          }
        }
      }
    }
  }

  /**
	 * Another core method. Builds the object model from the parsed tables. At the end of this
	 * method we have our class model in place, ready to be written.
	 *
	 * @param classes List of classes
	 * @param commitOrder Mostly to obtain the classes in the order of dependencies
	 * @throws Exception
	 */
  public static void buildObjectModel(TreeMap<String, Clazz> classes, LinkedList<String> commitOrder) throws Exception {
    boolean inEmbedMode = false;
    for (String tableName : commitOrder) {
      if (checkInIgnoreList(tableName)) {
        continue;
      }
      Clazz co = new Clazz();
      classes.put(tableName, co);
      TableObj tobj = State.getInstance().tables.get(tableName);
      co.setTableObj(tobj);
      tobj.setClazz(co);
      co.setTableIsAView(tobj.isViewTable());
    }
    for (String tableName : commitOrder) {
      if (checkInIgnoreList(tableName)) {
        continue;
      }
      String tableNameNoCat = SyncUtils.getTableName(tableName.toLowerCase());
      String tableCat = SyncUtils.getTableCatalog(tableName.toLowerCase());
      String tableSchema = SyncUtils.getTableSchema(tableName.toLowerCase());
      Clazz co = classes.get(tableName);
      co.setClassPackage(tableSchema);
      co.setClassName(SyncUtils.upfirstChar(tableNameNoCat));
      if (State.getInstance().abstractTables.contains(tableName)) {
        co.setClassType("abstract");
      }
      co.setCyclicExclusionTable(State.getInstance().cyclicTableExclusionListTables.containsKey(tableName));
      TableObj tobj = State.getInstance().tables.get(tableName);
      co.setTableObj(tobj);
      tobj.setClazz(co);
      co.getImports().add("javax.persistence.Entity");
      if ((State.getInstance().schemaRestrict != 0) || (co.getTableObj().getDbName().indexOf('_') >= 0) || !co.getClassName().equals(co.getTableObj().getDbName())) {
        co.getImports().add("javax.persistence.Table");
      }
      Iterator<Entry<String, FieldObj>> fields = tobj.getFields().entrySet().iterator();
      while (fields.hasNext()) {
        inEmbedMode = false;
        Entry<String, FieldObj> field = fields.next();
        String fieldName = field.getKey();
        FieldObj fieldObj = field.getValue();
        if (fieldObj.isAliased()) {
          fieldName = fieldObj.getAlias();
        }
        co = classes.get(tableName);
        PropertyObj property = co.getProperties().get(fieldName);
        if (property == null) {
          property = new PropertyObj();
        }
        property.setFieldObj(fieldObj);
        fieldObj.setProperty(property);
        property.setJavaName(SyncUtils.upfirstChar(fieldName));
        if (co.hasCompositeKey() && (tobj.getPrimaryKeys().contains(field.getKey()))) {
          Clazz embed;
          embed = co.getEmbeddableClass();
          if (embed == null) {
            co.getProperties().put("id", property);
            co.setCompositePrimaryKey(true);
            property.setJavaType(co.getClassName() + "PK");
            property.setIdField(true);
            property.setJavaName("Id");
            property.setPropertyName("id");
            property.setClazz(co);
            property.setComposite(true);
            co.getImports().add("javax.persistence.Id");
            embed = new Clazz();
            embed.setEmbeddedFrom(co);
            embed.getImports().add("java.io.Serializable");
            embed.getImports().add("javax.persistence.Embeddable");
            co.setEmbeddableClass(embed);
            embed.setEmbeddable(true);
            embed.setTableObj(co.getTableObj());
            embed.setClassName(co.getClassName() + "PK");
            classes.put(tableName + "PK", embed);
            property.setCompositeLink(embed);
            embed.setClassName(SyncUtils.upfirstChar(SyncUtils.getTableName(tableName.toLowerCase()) + "PK"));
            embed.setClassPackage(tableCat);
            property = new PropertyObj();
            property.setFieldObj(fieldObj);
            fieldObj.setProperty(property);
            property.setJavaName(SyncUtils.upfirstChar(fieldName));
          }
          inEmbedMode = true;
          co = embed;
        }
        property.setClazz(co);
        switch (property.getPropertyMeta(inEmbedMode)) {
          case PRIMARY_FOREIGN_KEY:
          co.getImports().add("javax.persistence.PrimaryKeyJoinColumn");
          co.setExtendingProperty(property);
          property.setIdField(true);
          property.setJavaType(SyncUtils.mapSQLType(fieldObj));
          Iterator<String> targetTableKeys = tobj.getExportedKeys().navigableKeySet().iterator();
          while (targetTableKeys.hasNext()) {
            String myFieldName = targetTableKeys.next();
            for (KeyObj targetTable : tobj.getExportedKeys().get(myFieldName)) {
              doExternalProperty(tableName, co, field, targetTable.getField(), targetTable.getPkTableName());
            }
          }
          break;
          case PRIMARY_FIELD:
          if (property.isAutoInc()) {
            co.getImports().add("javax.persistence.GeneratedValue");
            Entry<String, GeneratorEnum> defaultGenerator = State.getInstance().generators.get("DEFAULT").getTables().get("*").getFields().firstEntry();
            String defaultPattern = defaultGenerator.getKey().replace("${DB}", property.getClazz().getTableObj().getDbName());
            GeneratorEnum defaultGeneratorType = defaultGenerator.getValue();
            if (property.getFieldObj().getName().equalsIgnoreCase(defaultPattern)) {
              property.setGeneratorType(defaultGeneratorType);
            }
            String seq = property.getClazz().getTableObj().getPrimaryKeySequences().get(property.getFieldObj().getName());
            if (State.getInstance().dbMode == 2 && seq != null) {
              property.setGeneratorType(GeneratorEnum.SEQUENCE);
              property.setSequenceName(seq);
              property.setSequenceHibernateRef(property.getClazz().getClassPropertyName() + SyncUtils.upfirstChar(property.getFieldObj().getName()) + "Generator");
              co.getImports().add("javax.persistence.SequenceGenerator");
              co.getImports().add("javax.persistence.GenerationType");
            } else {
              property.setGeneratorType(GeneratorEnum.AUTO);
            }
            property.setGeneratedValue(true);
          }
          if (tobj.getExportedKeys().containsKey(fieldName)) {
            for (KeyObj targetTable : tobj.getExportedKeys().get(fieldName)) {
              doExternalProperty(tableName, co, field, targetTable.getField(), targetTable.getPkTableName());
            }
          }
          if (!inEmbedMode) {
            fieldName = "id";
            property.setIdField(true);
            co.getImports().add("javax.persistence.Id");
          }
          property.setJavaType(SyncUtils.mapSQLType(fieldObj));
          if (property.getJavaType().equals("String")) {
            property.setLength(fieldObj.getLength());
            if (property.isGeneratedValue()) {
              HbnPojoGen.logE("PK with generated value with java type String detected. This is not supported by Hibernate unless you assign the ID manually. Expect unit tests to fail " + property);
            }
          }
          property.setJavaName(SyncUtils.upfirstChar(fieldName));
          break;
          case ENUM_FIELD:
          property.setJavaType(fieldObj.getEnumName());
          if (field.getValue().isFakeEnum()) {
            String tmp = Core.fixIdName(fieldName);
            property.setJavaName(SyncUtils.upfirstChar(tmp));
          } else {
            property.setJavaName(SyncUtils.upfirstChar(fieldName));
          }
          property.setEnumType(true);
          break;
          case COMPOSITE_MANY_TO_ONE:
          property.setManyToOne(true);
          property.setCompositeManyToOne(true);
          co.getImports().add("javax.persistence.FetchType");
          co.getImports().add("javax.persistence.ManyToOne");
          for (KeyObj keyObj : tobj.getImportedKeys().values()) {
            if (keyObj.getKeyLinks().containsKey(field.getKey())) {
              property.setJavaType(SyncUtils.upfirstChar(keyObj.getPkTableName()));
              property.setJavaName(property.getJavaType());
              fieldName = property.getLowerCaseFriendlyName();
              property.setPropertyName(fieldName);
              String targetTable = property.getClazz().getTableObj().getFullTableName();
              Clazz parentClass = classes.get(keyObj.getPKFullTableName());
              PropertyObj externalProperty = new PropertyObj();
              externalProperty.setFieldObj(property.getFieldObj());
              externalProperty.setJavaType(property.getClazz().getClassName());
              externalProperty.setJavaName(property.getClazz().getClassName());
              externalProperty.setPropertyName(property.getClazz().getClassPropertyName());
              externalProperty.setClazz(parentClass);
              externalProperty.setOneToMany(true);
              externalProperty.setOneToManyLink(property);
              if (property == null) {
                System.err.println("Property is null!!");
              }
              assert (property != null);
              State.getInstance().tables.get(keyObj.getPKFullTableName()).getClazz().getImports().add(State.getInstance().doObjectImport(targetTable));
              parentClass.getProperties().put(targetTable, externalProperty);
              parentClass.getImports().add(State.getInstance().doObjectImport(targetTable));
              parentClass.getImports().add("javax.persistence.OneToMany");
              parentClass.getImports().add("javax.persistence.FetchType");
              if (externalProperty.isOneToManyCascadeEnabledByConfig()) {
                parentClass.getImports().add("javax.persistence.CascadeType");
              }
              parentClass.getImports().add("java.util.HashSet");
              parentClass.getImports().add("java.util.Set");
              break;
            }
          }
          if (property.isManyToOneCascadeEnabledByConfig()) {
            co.getImports().add("javax.persistence.CascadeType");
          }
          break;
          case ONE_TO_ONE_FIELD:
          property.setOneToOne(true);
          property.setManyToOne(true);
          co.getImports().add("javax.persistence.FetchType");
          co.getImports().add("javax.persistence.OneToOne");
          String oname = field.getKey();
          if (oname.toUpperCase().endsWith(Constants.IDCONST)) {
            oname = oname.substring(0, oname.toUpperCase(Locale.getDefault()).lastIndexOf((Constants.IDCONST)));
          } else {
            if (oname.toUpperCase().endsWith(Constants.ID)) {
              oname = oname.substring(0, oname.toUpperCase(Locale.getDefault()).lastIndexOf((Constants.ID)));
            }
          }
          fieldName = Core.fixConflictingInheritedNames(oname, co);
          property.setJavaName(SyncUtils.upfirstChar(oname));
          for (KeyObj keyObj : tobj.getImportedKeys().values()) {
            if (keyObj.getKeyLinks().containsKey(field.getKey())) {
              property.setJavaType(SyncUtils.upfirstChar(keyObj.getPkTableName()));
              break;
            }
          }
          break;
          case MANY_TO_ONE_FIELD:
          property.setManyToOne(true);
          co.getImports().add("javax.persistence.FetchType");
          co.getImports().add("javax.persistence.ManyToOne");
          for (KeyObj keyObj : tobj.getImportedKeys().values()) {
            if (keyObj.getKeyLinks().containsKey(field.getKey())) {
              property.setJavaType(SyncUtils.upfirstChar(keyObj.getPkTableName().toLowerCase()));
              break;
            }
          }
          if (co.getProperties().get(property.getJavaType()) != null) {
            String name = field.getKey();
            if (name.toUpperCase().endsWith(Constants.IDCONST)) {
              name = name.substring(0, name.toUpperCase(Locale.getDefault()).lastIndexOf((Constants.IDCONST)));
            } else {
              if (name.toUpperCase().endsWith(Constants.ID)) {
                name = name.substring(0, name.toUpperCase(Locale.getDefault()).lastIndexOf((Constants.ID)));
              }
            }
            fieldName = Core.fixConflictingInheritedNames(name, co);
            property.setJavaName(SyncUtils.upfirstChar(name));
          } else {
            String name = Core.fixIdName(fieldName);
            fieldName = Core.fixConflictingInheritedNames(name, co);
            property.setJavaName(SyncUtils.upfirstChar(name));
          }
          break;
          case NORMAL_FIELD:
          property.setJavaType(SyncUtils.mapSQLType(fieldObj));
          if (fieldObj.getName().endsWith("_currency")) {
            FieldObj f = tobj.getFields().get(fieldObj.getName().substring(0, fieldObj.getName().lastIndexOf("_currency")));
            if (f != null && f.isMoneyType()) {
              property.setHiddenCurrencyField(true);
            }
          }
          if (fieldObj.getName().endsWith("_currency_code")) {
            FieldObj f = tobj.getFields().get(fieldObj.getName().substring(0, fieldObj.getName().lastIndexOf("_currency_code")));
            if (f != null && f.isMoneyType()) {
              property.setHiddenCurrencyField(true);
            }
          }
          if (fieldObj.isEncryptedType() || fieldObj.isMoneyType() || fieldObj.isCurrencyType()) {
            co.getImports().add("org.hibernate.annotations.TypeDef");
            co.getImports().add("org.hibernate.annotations.TypeDefs");
            co.getImports().add("org.hibernate.annotations.Type");
            co.getImports().add("org.hibernate.annotations.Columns");
            co.getImports().add("javax.persistence.Column");
          }
          if (fieldObj.isEncryptedType()) {
            co.getImports().add("org.jasypt.hibernate4.type.EncryptedStringType");
            property.setEncrypted(true);
          }
          if (fieldObj.isMoneyType()) {
            co.getImports().add(State.getInstance().getCustomMoneyType());
            co.getImports().add("org.javamoney.moneta.Money");
            property.setMoneyType(true);
            property.setJavaType("Money");
          }
          if (fieldObj.isCurrencyType()) {
            co.getImports().add(State.getInstance().getCustomCurrencyUnitType());
            co.getImports().add("javax.money.CurrencyUnit");
            property.setCurrencyType(true);
            property.setJavaType("CurrencyUnit");
          }
          if (property.getJavaType().equals("String")) {
            property.setLength(fieldObj.getLength());
          }
          if (property.isAutoInc()) {
            property.setGeneratedValue(true);
            Entry<String, GeneratorEnum> defaultGenerator = State.getInstance().generators.get("DEFAULT").getTables().get("*").getFields().firstEntry();
            String defaultPattern = defaultGenerator.getKey().replace("${DB}", property.getClazz().getTableObj().getDbName());
            GeneratorEnum defaultGeneratorType = defaultGenerator.getValue();
            if (property.getFieldObj().getName().equalsIgnoreCase(defaultPattern)) {
              property.setGeneratorType(defaultGeneratorType);
            } else {
              property.setGeneratorType(GeneratorEnum.AUTO);
            }
            co.getImports().add("javax.persistence.GeneratedValue");
          }
          String name = Core.fixConflictingInheritedNames(property.getJavaName(), co);
          property.setJavaName(SyncUtils.upfirstChar(name));
          break;
          default:
          HbnPojoGen.logE("???? huh ??? ");
          assert false;
        }
        if (property.getFieldObj().isFakeEnum()) {
          property.setPropertyName(Core.fixIdName(fieldName));
        } else {
          property.setPropertyName(fieldName);
        }
        if (!property.isHiddenCurrencyField()) {
          co.getProperties().put(fieldName, property);
        } else {
          co.getHiddenCurrencyProperties().put(fieldName, property);
        }
      }
    }
    LinkedList<Entry<String, PropertyObj>> changedKeys = new LinkedList<Entry<String, PropertyObj>>();
    fixBackLinks(classes, changedKeys);
    fixOneToMany(classes);
    TreeMap<String, Clazz> clashMap = new TreeMap<String, Clazz>(new CaseInsensitiveComparator());
    for (Clazz clazz : classes.values()) {
      Clazz clash = clashMap.get(clazz.getClassName());
      if (clash != null) {
        clash.setNameAmbiguityPossible(true);
        clazz.setNameAmbiguityPossible(true);
        for (Clazz fixClass : classes.values()) {
          Core.fixNameClash(clazz, fixClass);
          Core.fixNameClash(clash, fixClass);
        }
      } else {
        clashMap.put(clazz.getClassName(), clazz);
      }
      if (clazz.getExtendsFrom() != null) {
        TreeMap<String, PropertyObj> extendsProperties = clazz.getExtendsFrom().getClazz().getAllProperties();
        for (Entry<String, PropertyObj> property : clazz.getProperties().entrySet()) {
          if (extendsProperties.get(property.getKey()) != null) {
            property.getValue().getMethodLevelAnnotationsOnGetters().add("@Override");
            property.getValue().getMethodLevelAnnotationsOnSetters().add("@Override");
          }
        }
      }
    }
    for (Clazz clazz : classes.values()) {
      TreeMap<String, PropertyObj> seen = new TreeMap<String, PropertyObj>(new CaseInsensitiveComparator());
      for (PropertyObj property : clazz.getProperties().values()) {
        if (property.isOneToMany() || property.isOneToOne()) {
          PropertyObj tmp = seen.get(property.getPropertyName());
          if (tmp == null) {
            seen.put(property.getPropertyName(), property);
          } else {
            property.setClashResolved(true);
            tmp.setClashResolved(true);
          }
        }
      }
      for (PropertyObj prop : clazz.getProperties().values()) {
        if (prop.isClashResolved()) {
          String resolved = SyncUtils.upfirstChar(prop.getOneToManyLink().getClazz().getClassPackage()) + prop.getJavaName();
          prop.setJavaName(resolved);
          prop.setPropertyName(resolved.substring(0, 1).toLowerCase() + resolved.substring(1));
        }
      }
    }
    for (String tableName : commitOrder) {
      if (Core.checkInIgnoreList(tableName)) {
        continue;
      }
      TreeMap<String, String> propertyMap = State.getInstance().cyclicTableExclusionListTables.get(tableName);
      if (propertyMap != null) {
        for (PropertyObj property : State.getInstance().getTables().get(tableName).getClazz().getAllProperties().values()) {
          if (propertyMap.containsKey(property.getFieldObj().getName())) {
            property.setCyclicDependencyProperty(true);
          }
        }
      }
    }
    for (Clazz clazz : classes.values()) {
      if (clazz.isCyclicExclusionTable()) {
        for (PropertyObj property : clazz.getProperties().values()) {
          if (property.isCyclicDependencyProperty()) {
            String fullTableName = clazz.getTableObj().getFullTableName();
            String replacmentTableName = State.getInstance().cyclicTableExclusionListTables.get(fullTableName).get(property.getFieldObj().getName());
            TableObj tObj = State.getInstance().tables.get(replacmentTableName);
            if (tObj != null) {
              Clazz co = tObj.getClazz();
              property.setCyclicDependencyReplacementClazz(co);
              co.setCyclicExclusionReplacementTable(true);
            } else {
              property.setCyclicDependencyProperty(false);
            }
          }
        }
      }
    }
    addSuffixes(classes);
    processLinkTables();
    disableBacklinks(classes);
    if (!State.getInstance().disableSubtypeEnumGeneration) {
      addSubclassEnums(classes);
    }
    addImports(classes);
    for (Entry<String, PropertyObj> broken : changedKeys) {
      PropertyObj prop = broken.getValue().getClazz().getProperties().get(broken.getKey());
      broken.getValue().getClazz().getProperties().remove(broken.getKey());
      prop.getClazz().getProperties().put(prop.getJavaName(), prop);
    }
  }

  /**
	 *
	 *
	 * @param classPackage
	 * @param className
	 * @return the classname with a suffix as specified in config
	 */
  private static String suffixMap(String classPackage, String className) {
    String suffix = State.getInstance().getClassSuffixes().get(classPackage + "." + className);
    if (suffix == null) {
      suffix = State.getInstance().getClassSuffixes().get("*." + className);
      if (suffix == null) {
        suffix = State.getInstance().getClassSuffixes().get(classPackage + ".*");
        if (suffix == null) {
          suffix = State.getInstance().getClassSuffixes().get("*");
        }
      }
    }
    if (suffix == null) {
      suffix = "";
    }
    return suffix;
  }

  /**
	 *
	 *
	 */
  private static void addSuffixes(TreeMap<String, Clazz> classes) {
    for (Clazz clazz : classes.values()) {
      String name = clazz.getClassName();
      String suffix = "";
      clazz.setSuffix(suffixMap(clazz.getClassPackage(), name));
      clazz.setClassNameNoSuffix(name);
      clazz.setClassName(name + clazz.getSuffix());
      if (clazz.hasEmbeddableClass()) {
        PropertyObj prop = clazz.getProperties().get("id");
        prop.setJavaType(prop.getJavaType() + clazz.getSuffix());
      }
      for (PropertyObj prop : clazz.getProperties().values()) {
        if (prop.isOneToMany()) {
          suffix = suffixMap(prop.getOneToManyLink().getClazz().getClassPackage(), prop.getOneToManyLink().getClazz().getClassNameNoSuffix());
        }
        if (prop.isManyToOne()) {
          suffix = suffixMap(prop.getManyToOneLink().getClazz().getClassPackage(), prop.getManyToOneLink().getClazz().getClassNameNoSuffix());
        }
        if (prop.isOneToOne()) {
          suffix = suffixMap(prop.getOneToOneLink().getClazz().getClassPackage(), prop.getOneToOneLink().getClazz().getClassNameNoSuffix());
        }
        if (prop.isManyToMany()) {
          suffix = suffixMap(prop.getManyToManyLink().getDstProperty().getClazz().getClassPackage(), prop.getManyToManyLink().getDstProperty().getClazz().getClassNameNoSuffix());
        }
        if (prop.isOneToMany() || prop.isManyToOne() || prop.isManyToMany() || prop.isOneToOne()) {
          prop.setJavaType(prop.getJavaType() + suffix);
          prop.setJavaName(prop.getJavaName() + suffix);
          prop.setPropertyName(prop.getPropertyName() + suffix);
        }
      }
    }
  }

  /**
	 *
	 *
	 * @param classes
	 */
  private static void addSubclassEnums(TreeMap<String, Clazz> classes) {
    for (Clazz clazz : classes.values()) {
      if (clazz.isSubclass()) {
        String s = Core.doSubtypeEnumImport(clazz.getExtendsFrom().getClazz().getTableObj().getDbCat(), clazz.getExtendsFrom().getClazz().getClassName() + "SubclassType");
        clazz.getExtendsFrom().getClazz().getSubclassEnum().add(SyncUtils.convertCamelCaseToEnum(clazz.getClassName()));
        clazz.getImports().add(s);
        VelocityWriters.getNotForInterfaceImports().add(s);
      }
      if (clazz.isSuperclass()) {
        String s = Core.doSubtypeEnumImport(clazz.getTableObj().getDbCat(), clazz.getClassName() + "SubclassType");
        clazz.getImports().add(s);
        VelocityWriters.getNotForInterfaceImports().add(s);
        clazz.getSubclassEnum().add("NOT_A_CHILD");
      }
    }
  }

  /**
	 * Markup object model to flag disabled backlinks (via config). Backlinks will still exist in
	 * this object model but we suppress it during code generation
	 *
	 * @param classes
	 */
  private static void disableBacklinks(TreeMap<String, Clazz> classes) {
    for (Clazz clazz : classes.values()) {
      String tableName = clazz.getTableObj().getFullTableName();
      TreeMap<String, TreeSet<String>> disableLink = State.getInstance().getDisableBackLinkTables().get(tableName);
      if (disableLink == null) {
        disableLink = State.getInstance().getDisableBackLinkTables().get(clazz.getTableObj().getDbCat() + ".*");
        if (disableLink == null) {
          disableLink = State.getInstance().getDisableBackLinkTables().get("*." + clazz.getTableObj().getDbName());
        }
      }
      if (disableLink != null) {
        for (PropertyObj property : clazz.getAllProperties().values()) {
          if (property.isManyToOne()) {
            disableBackLink(disableLink, property, property.getManyToOneLink());
          }
          if (property.isManyToMany()) {
            disableBackLink(disableLink, property, property.getManyToManyLink().getDstProperty());
          }
          if (property.isOneToOne()) {
            disableBackLink(disableLink, property, property.getOneToOneLink());
          }
        }
      }
    }
  }

  /**
	 * @param disableLink
	 * @param property
	 * @param link link to property
	 */
  private static void disableBackLink(TreeMap<String, TreeSet<String>> disableLink, PropertyObj property, PropertyObj link) {
    TreeSet<String> fieldName = disableLink.get(link.getClazz().getTableObj().getFullTableName());
    if (fieldName == null) {
      fieldName = disableLink.get(link.getClazz().getTableObj().getDbCat() + ".*");
      if (fieldName == null) {
        fieldName = disableLink.get("*." + link.getClazz().getTableObj().getDbName());
      }
    }
    if (fieldName != null) {
      if (fieldName.contains("*") || fieldName.contains(property.getFieldObj().getName())) {
        for (PropertyObj search : link.getClazz().getProperties().values()) {
          if ((search.isOneToMany() && search.getOneToManyLink().equals(property)) || (search.isManyToMany() && search.getManyToManyLink().getDstProperty().equals(property)) || (search.isOneToOne() && search.isOneTooneInverseSide() && search.getOneToOneLink().equals(property))) {
            search.setOneToNBackLinkDisabled(true);
          }
        }
      }
    }
  }

  /**
	 * @param tableName
	 * @param co
	 * @param field
	 * @param fieldName
	 * @param targetTable
	 */
  private static void doExternalProperty(String tableName, Clazz co, Entry<String, FieldObj> field, String fieldName, String targetTable) {
    FieldObj tmpField = State.getInstance().tables.get(targetTable).getFields().get(fieldName);
    if (!(tmpField.isPFKAlone())) {
      PropertyObj externalProperty = new PropertyObj();
      externalProperty.setFieldObj(field.getValue());
      externalProperty.setJavaType(SyncUtils.upfirstChar(SyncUtils.getTableName(targetTable.toLowerCase())));
      FieldObj targetField = State.getInstance().getTables().get(targetTable).getFields().get(fieldName);
      if (targetField != null && targetField.isAliased()) {
        externalProperty.setJavaName(targetField.getAliasInverse());
        externalProperty.setPropertyName(targetField.getAliasInverse());
      } else {
        externalProperty.setJavaName(SyncUtils.upfirstChar(State.getInstance().tables.get(targetTable).getName().toLowerCase()));
        externalProperty.setPropertyName(SyncUtils.getTableName(targetTable.toLowerCase()));
      }
      externalProperty.setClazz(State.getInstance().tables.get(tableName).getClazz());
      TreeSet<String> one2one = State.getInstance().getOneToOneTables().get(targetTable);
      if ((one2one != null) && one2one.contains(fieldName)) {
        externalProperty.setOneToOne(true);
      } else {
        externalProperty.setOneToMany(true);
      }
      String clazzCat = State.getInstance().tables.get(targetTable).getDbCat();
      if (!SyncUtils.getTableCatalog(tableName).equalsIgnoreCase(clazzCat)) {
        String tmp = State.getInstance().doObjectImport(tableName);
        State.getInstance().tables.get(targetTable).getClazz().getImports().add(tmp);
      }
      PropertyObj oldProperty = co.getProperties().get(externalProperty.getJavaName());
      if (oldProperty == null) {
        co.getProperties().put(externalProperty.getJavaName(), externalProperty);
      }
    }
  }

  /**
	 * Make backlinks of many-to-one + subclass stuff.
	 *
	 * @param classes
	 * @param changedKeys
	 */
  private static void fixBackLinks(TreeMap<String, Clazz> classes, LinkedList<Entry<String, PropertyObj>> changedKeys) {
    for (Clazz clazz : classes.values()) {
      for (Entry<String, PropertyObj> property : clazz.getProperties().entrySet()) {
        PropertyObj propertyObj = property.getValue();
        GeneratedValueSchemas genSchema = State.getInstance().generators.get(clazz.getTableObj().getDbCat());
        if (genSchema != null) {
          GeneratedValueFields genField = genSchema.getTables().get(clazz.getTableObj().getDbName());
          if (genField != null) {
            GeneratorEnum genType = genField.getFields().get(propertyObj.getFieldObj().getName());
            if (genType != null) {
              propertyObj.setGeneratedValue(true);
              propertyObj.setGeneratorType(genType);
            }
          }
        }
        if (!propertyObj.isGeneratedValue()) {
          setGeneratorAsPerConfig(propertyObj);
        }
        if (propertyObj.isManyToOne() || propertyObj.isPFK()) {
          FieldObj field = propertyObj.getFieldObj();
          TableObj table = propertyObj.getFieldObj().getTableObj();
          String fieldFK = null;
          String pkName = null;
          for (Entry<String, KeyObj> keyObj : table.getImportedKeys().entrySet()) {
            fieldFK = keyObj.getValue().getKeyLinks().get(field.getName());
            if (fieldFK != null) {
              pkName = keyObj.getKey();
              break;
            }
          }
          assert pkName != null : "pkName is Null";
          String tableFK = table.getImportedKeys().get(pkName).getPKFullTableName();
          if ((!propertyObj.isCompositeManyToOne()) && propertyObj.isPFK() && !(clazz.hasCompositeKey() && (clazz.getTableObj().getPrimaryKeys().contains(field.getName())))) {
            clazz.setSubclass(true);
            clazz.setExtendsFrom(State.getInstance().tables.get(tableFK).getFields().get(fieldFK).getProperty());
            assert (clazz.getExtendsFrom() != null) : "extendsFrom is null!";
            Clazz superClass = clazz.getExtendsFrom().getClazz();
            superClass.setSuperclass(true);
            superClass.getImports().add("javax.persistence.Inheritance");
            superClass.getImports().add("javax.persistence.InheritanceType");
          }
          if (propertyObj.isManyToOne()) {
            if (!propertyObj.isCompositeManyToOne()) {
              PropertyObj tmpProperty = State.getInstance().tables.get(SyncUtils.upfirstChar(tableFK)).getFields().get(fieldFK).getProperty();
              propertyObj.setManyToOneLink(tmpProperty);
              if (propertyObj.isOneToOne()) {
                tmpProperty = State.getInstance().tables.get(SyncUtils.upfirstChar(tableFK)).getFields().get(fieldFK).getProperty();
                propertyObj.getManyToOneLink().setOneToOneLink(tmpProperty);
                propertyObj.getManyToOneLink().setOneToOneKey(table.getImportedKeys().get(pkName));
              }
            } else {
              propertyObj.setManyToOneLink(State.getInstance().tables.get(tableFK).getClazz().getProperties().get("Id"));
            }
            propertyObj.setManyToOneKey(table.getImportedKeys().get(pkName));
            if (!propertyObj.getManyToOneLink().getClazz().getClassPackage().equalsIgnoreCase(propertyObj.getClazz().getClassPackage())) {
              propertyObj.getClazz().getImports().add(propertyObj.getManyToOneLink().getClazz().getFullClassName());
            }
            if (clazz.isSubclass()) {
              for (Entry<String, PropertyObj> p : clazz.getAllProperties().entrySet()) {
                if (p.getValue().getJavaName().equals(propertyObj.getJavaName()) && p.getValue().getClazz() != propertyObj.getClazz()) {
                  propertyObj.setJavaName(propertyObj.getJavaName() + propertyObj.getClazz().getClassName());
                  propertyObj.setPropertyName(propertyObj.getLowerCaseFriendlyName());
                  changedKeys.add(property);
                }
              }
            }
          }
        }
      }
    }
  }

  /**
	 *
	 *
	 * @param propertyObj
	 */
  private static void setGeneratorAsPerConfig(PropertyObj propertyObj) {
    Entry<String, GeneratorEnum> defaultGenerator = State.getInstance().generators.get("DEFAULT").getTables().get("*").getFields().firstEntry();
    String defaultPattern = defaultGenerator.getKey().replace("${DB}", propertyObj.getClazz().getTableObj().getDbName());
    GeneratorEnum defaultGeneratorType = defaultGenerator.getValue();
    if (propertyObj.getFieldObj().getName().equalsIgnoreCase(defaultPattern)) {
      if (!(defaultGeneratorType == GeneratorEnum.AUTO && !propertyObj.isAutoInc()) && !propertyObj.isOneToMany()) {
        propertyObj.setGeneratedValue(true);
        propertyObj.setGeneratorType(defaultGeneratorType);
      }
    }
  }

  /**
	 * @param classes
	 */
  private static void fixOneToMany(TreeMap<String, Clazz> classes) {
    for (Clazz clazz : classes.values()) {
      for (PropertyObj property : clazz.getProperties().values()) {
        if (property.isAutoInc() && clazz.isEmbeddable()) {
          System.err.println("Warning: Found a table with autoincrement as one of its composite keys. This will not work (if it\'s autoincremented, you don\'t need the composite key -- it\'s already unique)");
          HbnPojoGen.logE("Table: " + clazz.getTableObj().getFullTableName() + ". Field: " + property.getFieldObj().getName());
        }
        if (property.isManyToOne()) {
          String searchField = null;
          searchField = property.isCompositeManyToOne() ? "Id" : property.getManyToOneKey().getKeyLinks().values().iterator().next();
          if (property.isOneToOne()) {
            property.setManyToOne(false);
            for (PropertyObj search : property.getManyToOneLink().getClazz().getProperties().values()) {
              if (search.isOneToOne() && search.getJavaType().equalsIgnoreCase(property.getClazz().getClassName()) && search.getFieldObj().getName().equalsIgnoreCase(searchField)) {
                search.setOneToOneLink(property);
                property.setOneToOneLink(search);
                search.setOneTooneInverseSide(true);
                search.setOneToOneKey(property.getManyToOneKey());
                property.setOneToOneKey(property.getManyToOneKey());
                property.setManyToOneLink(null);
                break;
              }
            }
          } else {
            String searchClassName = property.getClazz().getClassName();
            if (property.getClazz().isEmbeddable()) {
              searchClassName = property.getClazz().getEmbeddedFrom().getClassName();
            }
            for (PropertyObj search : property.getManyToOneLink().getClazz().getProperties().values()) {
              if (search.isOneToMany() && (search.getOneToManyLink() == null) && search.getJavaType().equalsIgnoreCase(searchClassName) && search.getFieldObj().getName().equalsIgnoreCase(searchField)) {
                search.setOneToManyLink(property);
                property.setInverseLink(search);
              }
            }
          }
        }
      }
    }
  }

  /**
	 * @param classes
	 */
  private static void addImports(TreeMap<String, Clazz> classes) {
    for (Clazz clazz : classes.values()) {
      clazz.getImports().add("javax.persistence.Transient");
      List<String> keySearch = new LinkedList<String>();
      String tmp = clazz.getClassPackage() + "." + clazz.getClassName();
      keySearch.add(tmp);
      keySearch.add("*." + clazz.getClassName());
      keySearch.add(clazz.getClassPackage() + ".*");
      keySearch.add("*.*");
      String classannotation = "";
      for (String key : keySearch) {
        String ann = State.getInstance().customClassAnnotations.get(key);
        if (ann != null) {
          classannotation = String.format("%s\n%s", classannotation, ann);
        }
      }
      State.getInstance().customClassAnnotations.put(tmp, classannotation);
      for (JoinTable joinTable : clazz.getJoinMappings()) {
        clazz.getImports().add(joinTable.getDstProperty().getClazz().getFullClassName());
      }
      if (!clazz.isSubclass()) {
        if (!clazz.isEmbeddable()) {
          if (State.getInstance().isEnableSpringData()) {
            clazz.getImports().add(SyncUtils.getConfigPackage("", PackageTypeEnum.UTIL) + ".IPojoGenEntity");
          } else {
            clazz.getImports().add("com.felees.hbnpojogen.persistence.IPojoGenEntity");
          }
        }
        clazz.getImports().add("java.io.Serializable");
      }
      if (!clazz.isEmbeddable() && !clazz.hasEmbeddableClass()) {
        if (!clazz.isSubclass()) {
          clazz.getImports().add("java.util.Map");
          clazz.getImports().add("java.util.Collections");
          clazz.getImports().add("java.util.WeakHashMap");
        }
        clazz.getImports().add("org.hibernate.proxy.HibernateProxy");
      }
      boolean didScrubbedEnum = false;
      for (PropertyObj property : clazz.getProperties().values()) {
        if (property.isDefaultValue()) {
          clazz.setDynamicUpdatesInserts(true);
        }
        if (property.isGeneratedValue()) {
          switch (property.getGeneratorType()) {
            case AUTO:
            if (!property.isComposite()) {
              clazz.getImports().add("javax.persistence.GenerationType");
              clazz.getImports().add("javax.persistence.GeneratedValue");
            }
            break;
            case GUID:
            clazz.setGeneratedValueGUID(true);
            clazz.getImports().add("javax.persistence.GeneratedValue");
            break;
            case CUSTOM:
            clazz.setGeneratedValueCustom(true);
            clazz.getImports().add("javax.persistence.GeneratedValue");
            break;
            case PKS:
            clazz.setGeneratedValuePKS(true);
            clazz.getImports().add("javax.persistence.GeneratedValue");
            break;
            case IDAWARE:
            clazz.setGeneratedValueIDAware(true);
            clazz.getImports().add("javax.persistence.GeneratedValue");
            break;
            case UUID:
            clazz.setGeneratedValueUUID(true);
            clazz.getImports().add("javax.persistence.GeneratedValue");
            clazz.getImports().add("org.hibernate.annotations.Parameter");
            break;
            case UUIDWithoutDashes:
            clazz.setGeneratedValueUUIDWithoutDashes(true);
            clazz.getImports().add("javax.persistence.GeneratedValue");
            break;
            default:
            break;
          }
        }
        if (property.isOneToMany() && !property.isOneToNBackLinkDisabled()) {
          clazz.getImports().add("javax.persistence.OneToMany");
          clazz.getImports().add("javax.persistence.FetchType");
          if (property.isOneToManyCascadeEnabledByConfig()) {
            clazz.getImports().add("javax.persistence.CascadeType");
          }
          clazz.getImports().add("java.util.HashSet");
          clazz.getImports().add("java.util.Set");
          if (!property.getOneToManyLink().getClazz().isEmbeddable()) {
            clazz.getImports().add(property.getOneToManyLink().getClazz().getFullClassName());
          } else {
            clazz.getImports().add(property.getOneToManyLink().getClazz().getEmbeddedFrom().getFullClassName());
          }
        }
        if (property.isManyToMany() && !property.isOneToNBackLinkDisabled()) {
          clazz.getImports().add("java.util.HashSet");
          clazz.getImports().add("java.util.Set");
          clazz.getImports().add("javax.persistence.FetchType");
          clazz.getImports().add("javax.persistence.ManyToMany");
          if (property.isManyToManyCascadeEnabledByConfig()) {
            clazz.getImports().add("javax.persistence.CascadeType");
          }
        }
        if (property.isManyToOne() && !property.isManyToMany()) {
          clazz.getImports().add("javax.persistence.JoinColumn");
        }
        if (property.isOneToOne()) {
          clazz.getImports().add("javax.persistence.OneToOne");
          clazz.getImports().add("javax.persistence.FetchType");
          if (!property.isOneTooneInverseSide()) {
            clazz.getImports().add("javax.persistence.JoinColumn");
          }
          if (property.isOneToOneCascadeEnabledByConfig()) {
            clazz.getImports().add("javax.persistence.CascadeType");
          }
        }
        if (property.isManyToOne() && property.isManyToOneCascadeEnabledByConfig()) {
          clazz.getImports().add("javax.persistence.CascadeType");
        }
        if (property.isEnumType()) {
          clazz.getImports().add(Core.doEnumImport(clazz.getTableObj().getDbCat(), property.getFieldObj().getEnumName()));
          if (clazz.getTableObj().isContainsScrubbedEnum()) {
            if (!didScrubbedEnum) {
              didScrubbedEnum = true;
              enumTypedefImport(classes, clazz, tmp);
            }
          } else {
            clazz.getImports().add("javax.persistence.Enumerated");
            clazz.getImports().add("javax.persistence.EnumType");
          }
        }
        if (property.isArrayType()) {
          clazz.getImports().add("java.util.Arrays");
        }
        if (property.isTransientField()) {
          clazz.getImports().add("javax.persistence.Transient");
        }
        if ((property.getJavaType() != null) && property.getJavaType().equalsIgnoreCase(Constants.DATE)) {
          clazz.getImports().add("java.util.Date");
        }
        if ((property.getJavaType() != null) && property.getJavaType().equalsIgnoreCase(Constants.LOCALDATE)) {
          if (State.getInstance().isEnableJDK8Support()) {
            clazz.getImports().add("java.time.LocalDate");
          } else {
            clazz.getImports().add("org.joda.time.LocalDate");
          }
          clazz.getImports().add("org.hibernate.annotations.Type");
        }
        if ((property.getJavaType() != null) && (property.getJavaType().equalsIgnoreCase(Constants.DATETIME) || property.getJavaType().equalsIgnoreCase(Constants.LOCALDATETIME))) {
          if (State.getInstance().isEnableJDK8Support()) {
            clazz.getImports().add("java.time.LocalDateTime");
          } else {
            clazz.getImports().add("org.joda.time.DateTime");
          }
          clazz.getImports().add("org.hibernate.annotations.Type");
        }
        if (!property.getColumnAnnotation().equals("")) {
          clazz.getImports().add("javax.persistence.Column");
        }
        if (!property.isNullable()) {
          clazz.getImports().add("javax.persistence.Basic");
        }
        if (State.getInstance().isEnableHibernateValidator()) {
          if (!property.getValidatorAnnotations().isEmpty()) {
            if (!property.isManyToOne() && !property.isManyToMany() && !property.isPFK() && !property.isOneToOne()) {
              if (!property.isNullable()) {
                clazz.getImports().add("com.felees.hbnpojogen.persistence.validator.Mandatory");
              }
            }
            if (property.isValidatorAnnotated()) {
              clazz.getImports().add("org.hibernate.validator.Valid");
            }
            if (property.hasLength()) {
              clazz.getImports().add("org.hibernate.validator.Length");
            }
          }
        }
      }
      String classCustomCode = State.getInstance().customClassCode.get(clazz.getClassPackage() + "." + clazz.getClassName());
      if (classCustomCode != null) {
        clazz.setClassCustomCode(classCustomCode);
      } else {
        clazz.setClassCustomCode("");
      }
      String classCustomCodeFields = State.getInstance().customClassCodeFields.get(clazz.getClassPackage() + "." + clazz.getClassName());
      if (classCustomCode != null) {
        clazz.setClassCustomCodeFields(classCustomCodeFields);
      } else {
        clazz.setClassCustomCodeFields("");
      }
      for (String key : keySearch) {
        TreeSet<String> customClassImports = State.getInstance().customClassImports.get(key);
        if (customClassImports != null) {
          clazz.getImports().addAll(customClassImports);
        }
      }
      TreeSet<String> customInterfaces = State.getInstance().customClassInterfaces.get(clazz.getClassPackage() + "." + clazz.getClassName());
      if (customInterfaces != null) {
        clazz.getCustomInterfaces().addAll(customInterfaces);
      }
      customInterfaces = State.getInstance().customClassInterfaces.get(clazz.getClassPackage() + ".*");
      if (customInterfaces != null) {
        clazz.getCustomInterfaces().addAll(customInterfaces);
      }
      customInterfaces = State.getInstance().customClassInterfaces.get("*." + clazz.getClassName());
      if (customInterfaces != null) {
        clazz.getCustomInterfaces().addAll(customInterfaces);
      }
      customInterfaces = State.getInstance().customClassInterfaces.get("*.*");
      if (customInterfaces != null) {
        clazz.getCustomInterfaces().addAll(customInterfaces);
      }
      TreeSet<String> customExtends = State.getInstance().customClassExtends.get(clazz.getClassPackage() + "." + clazz.getClassName());
      if (customExtends != null) {
        clazz.getCustomExtends().addAll(customExtends);
      }
      customExtends = State.getInstance().customClassExtends.get(clazz.getClassPackage() + ".*");
      if (customExtends != null) {
        clazz.getCustomExtends().addAll(customExtends);
      }
      customExtends = State.getInstance().customClassExtends.get("*." + clazz.getClassName());
      if (customExtends != null) {
        clazz.getCustomExtends().addAll(customExtends);
      }
      customExtends = State.getInstance().customClassExtends.get("*.*");
      if (customExtends != null) {
        clazz.getCustomExtends().addAll(customExtends);
      }
      TreeMap<String, CustomAnnotations> annotation = State.getInstance().customAnnotations.get(clazz.getClassPackage() + "." + clazz.getClassName());
      if (annotation == null) {
        annotation = State.getInstance().customAnnotations.get("*." + clazz.getClassName());
        if (annotation == null) {
          annotation = State.getInstance().customAnnotations.get(clazz.getClassPackage() + ".*");
          if (annotation == null) {
            annotation = State.getInstance().customAnnotations.get("*.*");
          }
        }
      }
      if (annotation != null) {
        for (Entry<String, PropertyObj> property : clazz.getProperties().entrySet()) {
          CustomAnnotations settings = annotation.get(property.getValue().getJavaName());
          if (settings != null) {
            property.getValue().getPropertyLevelAnnotations().addAll(settings.getPropertyLevelAnnotations());
            property.getValue().getMethodLevelAnnotationsOnGetters().addAll(settings.getMethodLevelAnnotationsOnGetters());
            property.getValue().getMethodLevelAnnotationsOnSetters().addAll(settings.getMethodLevelAnnotationsOnSetters());
            property.getValue().getMethodLevelSettersPrecondition().addAll(settings.getMethodLevelSetterPrecondition());
            property.getValue().getMethodLevelGettersPrecondition().addAll(settings.getMethodLevelGetterPrecondition());
            property.getValue().getMethodLevelSettersPostcondition().addAll(settings.getMethodLevelSetterPostcondition());
            property.getValue().getMethodLevelGettersPostcondition().addAll(settings.getMethodLevelGetterPostcondition());
          }
        }
      }
      String classAnnotation = State.getInstance().customClassAnnotations.get(clazz.getClassPackage() + "." + clazz.getClassName());
      if (classAnnotation != null) {
        clazz.getClassAnnotation().add(classAnnotation);
      }
      String typeDefAnnotation = State.getInstance().classTypeDefsAnnotations.get(clazz.getClassPackage() + "." + clazz.getClassName());
      if (typeDefAnnotation != null) {
        clazz.getClassTypedefsAnnotation().add(typeDefAnnotation);
      }
    }
  }

  /**
	 * @param classes
	 * @param clazz
	 * @param tmp
	 */
  private static void enumTypedefImport(TreeMap<String, Clazz> classes, Clazz clazz, String tmp) {
    String classannotation;
    classannotation = State.getInstance().classTypeDefsAnnotations.get(tmp);
    if (classannotation == null) {
      classannotation = "";
    }
    String valueType;
    if (State.getInstance().isEnableSpringData()) {
      valueType = SyncUtils.getConfigPackage("", PackageTypeEnum.UTIL) + ".StringValuedEnumType.class";
    } else {
      valueType = "com.felees.hbnpojogen.persistence.impl.StringValuedEnumType.class";
    }
    classannotation = String.format("%s@TypeDef(name = \"enumType\", typeClass = " + valueType + ")", classannotation);
    State.getInstance().classTypeDefsAnnotations.put(tmp, classannotation);
    clazz.getImports().add("org.hibernate.annotations.TypeDef");
    clazz.getImports().add("org.hibernate.annotations.Parameter");
    clazz.getImports().add("org.hibernate.annotations.Type");
    if (clazz.isEmbeddable()) {
      String parent = clazz.getEmbeddedFrom().getClassPackage() + "." + clazz.getEmbeddedFrom().getClassName();
      classannotation = State.getInstance().classTypeDefsAnnotations.get(parent);
      classannotation = String.format("%s@TypeDef(name = \"enumType\", typeClass = " + valueType + ") )", classannotation);
      State.getInstance().classTypeDefsAnnotations.put(parent, classannotation);
      clazz.getEmbeddedFrom().getClassTypedefsAnnotation().add(classannotation);
      clazz.getEmbeddedFrom().getImports().add("org.hibernate.annotations.TypeDef");
    }
    for (Clazz c : classes.values()) {
      if (c.isSubclass() && c.getExtendsFrom().getClazz().getClassName().equals(clazz.getClassName())) {
        enumTypedefImport(classes, c, c.getClassPackage() + "." + c.getClassName());
      }
    }
  }

  /**
	 *
	 */
  private static void processLinkTables() {
    for (Entry<String, TreeMap<String, TreeSet<String>>> linkTable : State.getInstance().linkTables.entrySet()) {
      TableObj tableObj = State.getInstance().tables.get(linkTable.getKey());
      if (tableObj != null) {
        Clazz bounceClass = tableObj.getClazz();
        int bounceMappings = 0;
        for (PropertyObj property : bounceClass.getProperties().values()) {
          if (!property.isGeneratedValue()) {
            bounceMappings++;
          }
        }
        for (Entry<String, TreeSet<String>> fields : linkTable.getValue().entrySet()) {
          String srcField = fields.getKey();
          TreeSet<String> dstFields = fields.getValue();
          KeyObj srcKey = null;
          for (KeyObj key : tableObj.getImportedKeys().values()) {
            if (key.getKeyLinks().get(srcField) != null) {
              srcKey = key;
              break;
            }
          }
          if (srcKey != null) {
            PropertyObj srcProperty = null;
            PropertyObj srcPropertyBounce = bounceClass.getTableObj().getFields().get(srcField).getProperty();
            for (PropertyObj search : srcPropertyBounce.getManyToOneLink().getClazz().getProperties().values()) {
              if (srcPropertyBounce.equals(search.getOneToManyLink())) {
                srcProperty = search;
                break;
              }
            }
            for (String dstField : dstFields) {
              KeyObj dstKey = null;
              for (KeyObj key : tableObj.getImportedKeys().values()) {
                if (key.getKeyLinks().get(dstField) != null) {
                  dstKey = key;
                  break;
                }
              }
              if (dstKey != null) {
                PropertyObj dstProperty = null;
                PropertyObj dstPropertyBounce = bounceClass.getTableObj().getFields().get(dstField).getProperty();
                for (PropertyObj search : dstPropertyBounce.getManyToOneLink().getClazz().getProperties().values()) {
                  if (dstPropertyBounce.equals(search.getOneToManyLink())) {
                    dstProperty = search;
                    break;
                  }
                }
                doManyToMany(bounceClass, bounceMappings, srcProperty, srcPropertyBounce, dstProperty, dstPropertyBounce);
              }
            }
          }
        }
      }
    }
  }

  /**
	 * @param bounceClass
	 * @param bounceMappings
	 * @param srcProperty
	 * @param srcPropertyBounce
	 * @param dstProperty
	 * @param dstPropertyBounce
	 */
  private static void doManyToMany(Clazz bounceClass, int tmpBounceMappings, PropertyObj srcProperty, PropertyObj srcPropertyBounce, PropertyObj dstProperty, PropertyObj dstPropertyBounce) {
    int bounceMappings = tmpBounceMappings;
    if ((srcProperty != null) || (dstProperty != null)) {
      bounceMappings = bounceMappings - 2;
      bounceClass.setJoinTable(true);
      JoinTable srcJoin = new JoinTable(srcProperty, dstProperty, bounceClass);
      JoinTable dstJoin = new JoinTable(dstProperty, srcProperty, bounceClass);
      if ((srcProperty != null) && (dstProperty != null)) {
        if ((bounceMappings == 0)) {
          bounceClass.setHiddenJoinTable(true);
          srcProperty.setManyToMany(true);
          dstProperty.setManyToMany(true);
          srcProperty.setJavaType(dstPropertyBounce.getJavaType());
          srcProperty.setJavaName(dstPropertyBounce.getJavaName());
          srcProperty.setPropertyName(dstPropertyBounce.getPropertyName());
          srcJoin.setSrcPropertyBounce(srcPropertyBounce);
          srcJoin.setDstPropertyBounce(dstPropertyBounce);
          dstJoin.setSrcPropertyBounce(srcPropertyBounce);
          dstJoin.setDstPropertyBounce(dstPropertyBounce);
          dstProperty.setJavaType(srcPropertyBounce.getJavaType());
          dstProperty.setJavaName(srcPropertyBounce.getJavaName());
          dstProperty.setPropertyName(srcPropertyBounce.getPropertyName());
          srcProperty.setOneToMany(false);
          srcProperty.setOneToManyLink(null);
          dstProperty.setOneToMany(false);
          dstProperty.setOneToManyLink(null);
          String tmp = State.getInstance().doObjectImport(srcProperty.getClazz().getClassPackage(), bounceClass.getTableObj().getDbName());
          srcProperty.getClazz().getImports().remove(tmp);
          tmp = State.getInstance().doObjectImport(srcProperty.getClazz().getClassPackage(), bounceClass.getTableObj().getDbName());
          dstProperty.getClazz().getImports().remove(tmp);
          if (!dstProperty.isOneToNBackLinkDisabled()) {
            String srcClassName = srcProperty.getClazz().getClassName();
            dstProperty.getClazz().getImports().add(State.getInstance().doObjectImport(srcProperty.getClazz().getClassPackage(), srcClassName));
          }
          String dstClassName = dstProperty.getClazz().getClassName();
          srcProperty.getClazz().getImports().add(State.getInstance().doObjectImport(dstProperty.getClazz().getClassPackage(), dstClassName));
          srcProperty.getClazz().getImports().add("javax.persistence.JoinColumn");
          srcProperty.getClazz().getImports().add("javax.persistence.JoinTable");
          dstProperty.setManyToManyInverseSide(true);
          srcProperty.setManyToManyLink(srcJoin);
          dstProperty.setManyToManyLink(dstJoin);
        } else {
          srcProperty.getClazz().getJoinMappings().add(srcJoin);
          dstProperty.getClazz().getJoinMappings().add(dstJoin);
        }
      }
    }
  }

  /**
	 * When we have a name clash in different packages, make sure the property types reflect the
	 * full classpath name
	 *
	 * @param clazz
	 * @param clash
	 */
  private static void fixNameClash(Clazz clazz, Clazz clash) {
    clash.getImports().remove(clazz.getFullClassName());
    for (PropertyObj property : clash.getProperties().values()) {
      if (property.isOneToMany()) {
        if (property.getOneToManyLink().getClazz().equals(clazz)) {
          property.setJavaType(clazz.getFullClassName());
        }
      }
      if (property.isOneToOne()) {
        if (property.getOneToOneLink().getClazz().equals(clazz)) {
          property.setJavaType(clazz.getFullClassName());
        }
      }
      if (property.isManyToOne()) {
        if (property.getManyToOneLink().getClazz().equals(clazz)) {
          property.setJavaType(clazz.getFullClassName());
        }
      }
      if (property.isManyToMany()) {
        if (property.getManyToManyLink().getDstProperty().getClazz().equals(clazz)) {
          property.setJavaType(clazz.getFullClassName());
        }
      }
    }
  }

  /**
	 * Syntactic sugaring
	 *
	 * @param fixId
	 * @return A cleaned name
	 */
  private static String fixIdName(String fixId) {
    String name = fixId;
    if (name.toUpperCase().endsWith(Constants.IDCONST)) {
      name = name.substring(0, name.toUpperCase(Locale.getDefault()).lastIndexOf((Constants.IDCONST)));
    }
    return name;
  }

  /**
	 * If we have a name clash in different packages, we'll expand to the full package name
	 *
	 * @param name
	 * @param co
	 * @return a cleaned name
	 */
  private static String fixConflictingInheritedNames(String name, Clazz co) {
    String result = name;
    if ((co.getExtendsFrom() != null) && (co.getExtendsFrom().getClazz().getProperties() != null) && co.getExtendsFrom().getClazz().getProperties().containsKey(name)) {
      result = name + co.getClassName();
    }
    return result;
  }

  /**
	 * @param cat
	 * @param enumName
	 * @return Enum import
	 */
  private static String doEnumImport(String cat, String enumName) {
    return SyncUtils.getConfigPackage(cat, PackageTypeEnum.ENUM) + "." + enumName;
  }

  /**
	 * @param cat
	 * @param enumName
	 * @return Enum import
	 */
  private static String doSubtypeEnumImport(String cat, String enumName) {
    return SyncUtils.getConfigPackage(cat, PackageTypeEnum.SUBTYPE_ENUM) + "." + enumName;
  }

  /**
     * @param clazz
     * @return t/f
     */
  public static boolean skipSchemaWrite(Clazz clazz) {
    return skipSchemaWrite(clazz.getTableObj().getDbCat());
  }

  /**
     * @param catalog
     * @return t/f
     */
  public static boolean skipSchemaWrite(String catalog) {
    if (!State.getInstance().getNoOutPutForExceptSchemaList().isEmpty()) {
      return !State.getInstance().getNoOutPutForExceptSchemaList().contains(catalog);
    }
    return State.getInstance().getNoOutPutForSchemaList().contains(catalog);
  }
}