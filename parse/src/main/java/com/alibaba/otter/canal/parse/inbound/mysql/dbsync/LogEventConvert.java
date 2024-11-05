package com.alibaba.otter.canal.parse.inbound.mysql.dbsync;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.Types;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import com.alibaba.otter.canal.parse.inbound.mysql.tablemeta.TableMetaCacheInterface;
import com.alibaba.otter.canal.parse.inbound.mysql.tablemeta.TableMetaStorage;
import com.alibaba.otter.canal.parse.inbound.mysql.tablemeta.exception.NoHistoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.otter.canal.common.AbstractCanalLifeCycle;
import com.alibaba.otter.canal.filter.aviater.AviaterRegexFilter;
import com.alibaba.otter.canal.parse.driver.mysql.packets.GTIDSet;
import com.alibaba.otter.canal.parse.exception.CanalParseException;
import com.alibaba.otter.canal.parse.exception.TableIdNotFoundException;
import com.alibaba.otter.canal.parse.inbound.BinlogParser;
import com.alibaba.otter.canal.parse.inbound.TableMeta;
import com.alibaba.otter.canal.parse.inbound.TableMeta.FieldMeta;
import com.alibaba.otter.canal.parse.inbound.mysql.ddl.DdlResult;
import com.alibaba.otter.canal.parse.inbound.mysql.ddl.DruidDdlParser;
import com.alibaba.otter.canal.parse.inbound.mysql.ddl.SimpleDdlParser;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.Header;
import com.alibaba.otter.canal.protocol.CanalEntry.Pair;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.protocol.CanalEntry.TransactionBegin;
import com.alibaba.otter.canal.protocol.CanalEntry.TransactionEnd;
import com.alibaba.otter.canal.protocol.CanalEntry.Type;
import com.alibaba.otter.canal.protocol.position.EntryPosition;
import com.google.protobuf.ByteString;
import com.taobao.tddl.dbsync.binlog.LogEvent;
import com.taobao.tddl.dbsync.binlog.event.DeleteRowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.GtidLogEvent;
import com.taobao.tddl.dbsync.binlog.event.IntvarLogEvent;
import com.taobao.tddl.dbsync.binlog.event.LogHeader;
import com.taobao.tddl.dbsync.binlog.event.QueryLogEvent;
import com.taobao.tddl.dbsync.binlog.event.RandLogEvent;
import com.taobao.tddl.dbsync.binlog.event.RowsLogBuffer;
import com.taobao.tddl.dbsync.binlog.event.RowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.RowsQueryLogEvent;
import com.taobao.tddl.dbsync.binlog.event.TableMapLogEvent;
import com.taobao.tddl.dbsync.binlog.event.TableMapLogEvent.ColumnInfo;
import com.taobao.tddl.dbsync.binlog.event.UnknownLogEvent;
import com.taobao.tddl.dbsync.binlog.event.UpdateRowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.UserVarLogEvent;
import com.taobao.tddl.dbsync.binlog.event.WriteRowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.XidLogEvent;
import com.taobao.tddl.dbsync.binlog.event.mariadb.AnnotateRowsEvent;

public class LogEventConvert extends AbstractCanalLifeCycle implements BinlogParser<LogEvent> {
  public static final String XA_XID = "XA_XID";

  public static final String XA_TYPE = "XA_TYPE";

  public static final String XA_START = "XA START";

  public static final String XA_END = "XA END";

  public static final String XA_COMMIT = "XA COMMIT";

  public static final String XA_ROLLBACK = "XA ROLLBACK";

  public static final String ISO_8859_1 = "ISO-8859-1";

  public static final String UTF_8 = "UTF-8";

  public static final int TINYINT_MAX_VALUE = 256;

  public static final int SMALLINT_MAX_VALUE = 65536;

  public static final int MEDIUMINT_MAX_VALUE = 16777216;

  public static final long INTEGER_MAX_VALUE = 4294967296L;

  public static final BigInteger BIGINT_MAX_VALUE = new BigInteger("18446744073709551616");

  public static final int version = 1;

  public static final String BEGIN = "BEGIN";

  public static final String COMMIT = "COMMIT";

  public static final Logger logger = LoggerFactory.getLogger(LogEventConvert.class);

  private volatile AviaterRegexFilter nameFilter;

  private volatile AviaterRegexFilter nameBlackFilter;

  private TableMetaCacheInterface tableMetaCache;

  private Charset charset = Charset.defaultCharset();

  private boolean filterQueryDcl = false;

  private boolean filterQueryDml = false;

  private boolean filterQueryDdl = false;

  private boolean filterTableError = false;

  private boolean filterRows = false;

  private boolean useDruidDdlFilter = true;

  private GTIDSet gtidSet;

  public LogEventConvert(GTIDSet gtidSet) {
    this.gtidSet = gtidSet;
  }

  public LogEventConvert() {
  }

  @Override public Entry parse(LogEvent logEvent, boolean isSeek) throws CanalParseException {
    if (logEvent == null || logEvent instanceof UnknownLogEvent) {
      return null;
    }
    int eventType = logEvent.getHeader().getType();
    switch (eventType) {
      case LogEvent.QUERY_EVENT:
      return parseQueryEvent((QueryLogEvent) logEvent, isSeek);
      case LogEvent.XID_EVENT:
      return parseXidEvent((XidLogEvent) logEvent);
      case LogEvent.TABLE_MAP_EVENT:
      break;
      case LogEvent.WRITE_ROWS_EVENT_V1:
      case LogEvent.WRITE_ROWS_EVENT:
      return parseRowsEvent((WriteRowsLogEvent) logEvent);
      case LogEvent.UPDATE_ROWS_EVENT_V1:
      case LogEvent.UPDATE_ROWS_EVENT:
      return parseRowsEvent((UpdateRowsLogEvent) logEvent);
      case LogEvent.DELETE_ROWS_EVENT_V1:
      case LogEvent.DELETE_ROWS_EVENT:
      return parseRowsEvent((DeleteRowsLogEvent) logEvent);
      case LogEvent.ROWS_QUERY_LOG_EVENT:
      return parseRowsQueryEvent((RowsQueryLogEvent) logEvent);
      case LogEvent.ANNOTATE_ROWS_EVENT:
      return parseAnnotateRowsEvent((AnnotateRowsEvent) logEvent);
      case LogEvent.USER_VAR_EVENT:
      return parseUserVarLogEvent((UserVarLogEvent) logEvent);
      case LogEvent.INTVAR_EVENT:
      return parseIntrvarLogEvent((IntvarLogEvent) logEvent);
      case LogEvent.RAND_EVENT:
      return parseRandLogEvent((RandLogEvent) logEvent);
      case LogEvent.GTID_LOG_EVENT:
      return parseGTIDLogEvent((GtidLogEvent) logEvent);
      default:
      break;
    }
    return null;
  }

  public void reset() {
    if (tableMetaCache != null) {
      tableMetaCache.clearTableMeta();
    }
  }

  private Entry parseGTIDLogEvent(GtidLogEvent logEvent) {
    LogHeader logHeader = logEvent.getHeader();
    String value = logEvent.getSid().toString() + ":" + logEvent.getGno();
    Pair.Builder builder = Pair.newBuilder();
    builder.setKey("gtid");
    builder.setValue(value);
    if (gtidSet != null) {
      gtidSet.update(value);
    }
    if (logEvent.getLastCommitted() != null) {
      builder.setKey("lastCommitted");
      builder.setValue(String.valueOf(logEvent.getLastCommitted()));
      builder.setKey("sequenceNumber");
      builder.setValue(String.valueOf(logEvent.getSequenceNumber()));
    }
    Header header = createHeader(logHeader, "", "", EventType.GTID);
    return createEntry(header, EntryType.GTIDLOG, builder.build().toByteString());
  }

  private Entry parseQueryEvent(QueryLogEvent event, boolean isSeek) {
    String queryString = event.getQuery();
    if (StringUtils.startsWithIgnoreCase(queryString, XA_START)) {
      TransactionBegin.Builder beginBuilder = TransactionBegin.newBuilder();
      beginBuilder.setThreadId(event.getSessionId());
      beginBuilder.addProps(createSpecialPair(XA_TYPE, XA_START));
      beginBuilder.addProps(createSpecialPair(XA_XID, getXaXid(queryString, XA_START)));
      TransactionBegin transactionBegin = beginBuilder.build();
      Header header = createHeader(event.getHeader(), "", "", null);
      return createEntry(header, EntryType.TRANSACTIONBEGIN, transactionBegin.toByteString());
    } else {
      if (StringUtils.startsWithIgnoreCase(queryString, XA_END)) {
        TransactionEnd.Builder endBuilder = TransactionEnd.newBuilder();
        endBuilder.setTransactionId(String.valueOf(0L));
        endBuilder.addProps(createSpecialPair(XA_TYPE, XA_END));
        endBuilder.addProps(createSpecialPair(XA_XID, getXaXid(queryString, XA_END)));
        TransactionEnd transactionEnd = endBuilder.build();
        Header header = createHeader(event.getHeader(), "", "", null);
        return createEntry(header, EntryType.TRANSACTIONEND, transactionEnd.toByteString());
      } else {
        if (StringUtils.startsWithIgnoreCase(queryString, XA_COMMIT)) {
          Header header = createHeader(event.getHeader(), "", "", EventType.XACOMMIT);
          RowChange.Builder rowChangeBuider = RowChange.newBuilder();
          rowChangeBuider.setSql(queryString);
          rowChangeBuider.addProps(createSpecialPair(XA_TYPE, XA_COMMIT));
          rowChangeBuider.addProps(createSpecialPair(XA_XID, getXaXid(queryString, XA_COMMIT)));
          rowChangeBuider.setEventType(EventType.XACOMMIT);
          return createEntry(header, EntryType.ROWDATA, rowChangeBuider.build().toByteString());
        } else {
          if (StringUtils.startsWithIgnoreCase(queryString, XA_ROLLBACK)) {
            Header header = createHeader(event.getHeader(), "", "", EventType.XAROLLBACK);
            RowChange.Builder rowChangeBuider = RowChange.newBuilder();
            rowChangeBuider.setSql(queryString);
            rowChangeBuider.addProps(createSpecialPair(XA_TYPE, XA_ROLLBACK));
            rowChangeBuider.addProps(createSpecialPair(XA_XID, getXaXid(queryString, XA_ROLLBACK)));
            rowChangeBuider.setEventType(EventType.XAROLLBACK);
            return createEntry(header, EntryType.ROWDATA, rowChangeBuider.build().toByteString());
          } else {
            if (StringUtils.endsWithIgnoreCase(queryString, BEGIN)) {
              TransactionBegin transactionBegin = createTransactionBegin(event.getSessionId());
              Header header = createHeader(event.getHeader(), "", "", null);
              return createEntry(header, EntryType.TRANSACTIONBEGIN, transactionBegin.toByteString());
            } else {
              if (StringUtils.endsWithIgnoreCase(queryString, COMMIT)) {
                TransactionEnd transactionEnd = createTransactionEnd(0L);
                Header header = createHeader(event.getHeader(), "", "", null);
                return createEntry(header, EntryType.TRANSACTIONEND, transactionEnd.toByteString());
              } else {
                boolean notFilter = false;
                EventType type = EventType.QUERY;
                String tableName = null;
                String schemaName = null;
                if (useDruidDdlFilter) {
                  List<DdlResult> results = DruidDdlParser.parse(queryString, event.getDbName());
                  for (DdlResult result : results) {
                    if (!processFilter(queryString, result)) {
                      notFilter = true;
                    }
                  }
                  if (results.size() > 0) {
                    type = results.get(0).getType();
                    schemaName = results.get(0).getSchemaName();
                    tableName = results.get(0).getTableName();
                  }
                } else {
                  DdlResult result = SimpleDdlParser.parse(queryString, event.getDbName());
                  if (!processFilter(queryString, result)) {
                    notFilter = true;
                  }
                  type = result.getType();
                  schemaName = result.getSchemaName();
                  tableName = result.getTableName();
                }
                if (!notFilter) {
                  return null;
                }
                if (!isSeek) {
                  EntryPosition position = createPosition(event.getHeader());
                  String fulltbName = schemaName + "." + tableName;
                  tableMetaCache.apply(position, fulltbName, queryString, null);
                }
                Header header = createHeader(event.getHeader(), schemaName, tableName, type);
                RowChange.Builder rowChangeBuider = RowChange.newBuilder();
                if (type != EventType.QUERY && type != EventType.INSERT && type != EventType.UPDATE && type != EventType.DELETE) {
                  rowChangeBuider.setIsDdl(true);
                }
                rowChangeBuider.setSql(queryString);
                if (StringUtils.isNotEmpty(event.getDbName())) {
                  rowChangeBuider.setDdlSchemaName(event.getDbName());
                }
                rowChangeBuider.setEventType(type);
                return createEntry(header, EntryType.ROWDATA, rowChangeBuider.build().toByteString());
              }
            }
          }
        }
      }
    }
  }

  private String getXaXid(String queryString, String type) {
    return StringUtils.substringAfter(queryString, type);
  }

  private boolean processFilter(String queryString, DdlResult result) {
    String schemaName = result.getSchemaName();
    String tableName = result.getTableName();
    if (tableMetaCache != null && (result.getType() == EventType.ALTER || result.getType() == EventType.ERASE || result.getType() == EventType.RENAME)) {
      for (DdlResult renameResult = result; renameResult != null; renameResult = renameResult.getRenameTableResult()) {
        String schemaName0 = renameResult.getSchemaName();
        String tableName0 = renameResult.getTableName();
        if (StringUtils.isNotEmpty(tableName0)) {
          tableMetaCache.clearTableMeta(schemaName0, tableName0);
        } else {
          tableMetaCache.clearTableMetaWithSchemaName(schemaName0);
        }
      }
    }
    if (result.getType() == EventType.ALTER || result.getType() == EventType.ERASE || result.getType() == EventType.CREATE || result.getType() == EventType.TRUNCATE || result.getType() == EventType.RENAME || result.getType() == EventType.CINDEX || result.getType() == EventType.DINDEX) {
      if (filterQueryDdl) {
        return true;
      }
      if (StringUtils.isEmpty(tableName) || (result.getType() == EventType.RENAME && StringUtils.isEmpty(result.getOriTableName()))) {
        throw new CanalParseException("SimpleDdlParser process query failed. pls submit issue with this queryString: " + queryString + " , and DdlResult: " + result.toString());
      } else {
        String name = schemaName + "." + tableName;
        if (nameFilter != null && !nameFilter.filter(name)) {
          if (result.getType() == EventType.RENAME) {
            if (nameFilter != null && !nameFilter.filter(result.getOriSchemaName() + "." + result.getOriTableName())) {
              return true;
            }
          } else {
            return true;
          }
        }
        if (nameBlackFilter != null && nameBlackFilter.filter(name)) {
          if (result.getType() == EventType.RENAME) {
            if (nameBlackFilter != null && nameBlackFilter.filter(result.getOriSchemaName() + "." + result.getOriTableName())) {
              return true;
            }
          } else {
            return true;
          }
        }
      }
    } else {
      if (result.getType() == EventType.INSERT || result.getType() == EventType.UPDATE || result.getType() == EventType.DELETE) {
        if (filterQueryDml) {
          return true;
        }
      } else {
        if (filterQueryDcl) {
          return true;
        }
      }
    }
    return false;
  }

  private Entry parseRowsQueryEvent(RowsQueryLogEvent event) {
    if (filterQueryDml) {
      return null;
    }
    String queryString = null;
    try {
      queryString = new String(event.getRowsQuery().getBytes(ISO_8859_1), charset.name());
      String tableName = null;
      if (useDruidDdlFilter) {
        List<DdlResult> results = DruidDdlParser.parse(queryString, null);
        if (results.size() > 0) {
          tableName = results.get(0).getTableName();
        }
      }
      return buildQueryEntry(queryString, event.getHeader(), tableName);
    } catch (UnsupportedEncodingException e) {
      throw new CanalParseException(e);
    }
  }

  private Entry parseAnnotateRowsEvent(AnnotateRowsEvent event) {
    if (filterQueryDml) {
      return null;
    }
    String queryString = null;
    try {
      queryString = new String(event.getRowsQuery().getBytes(ISO_8859_1), charset.name());
      return buildQueryEntry(queryString, event.getHeader());
    } catch (UnsupportedEncodingException e) {
      throw new CanalParseException(e);
    }
  }

  private Entry parseUserVarLogEvent(UserVarLogEvent event) {
    if (filterQueryDml) {
      return null;
    }
    return buildQueryEntry(event.getQuery(), event.getHeader());
  }

  private Entry parseIntrvarLogEvent(IntvarLogEvent event) {
    if (filterQueryDml) {
      return null;
    }
    return buildQueryEntry(event.getQuery(), event.getHeader());
  }

  private Entry parseRandLogEvent(RandLogEvent event) {
    if (filterQueryDml) {
      return null;
    }
    return buildQueryEntry(event.getQuery(), event.getHeader());
  }

  private Entry parseXidEvent(XidLogEvent event) {
    TransactionEnd transactionEnd = createTransactionEnd(event.getXid());
    Header header = createHeader(event.getHeader(), "", "", null);
    return createEntry(header, EntryType.TRANSACTIONEND, transactionEnd.toByteString());
  }

  public Entry parseRowsEvent(RowsLogEvent event) {
    return parseRowsEvent(event, null);
  }

  private EntryPosition createPosition(LogHeader logHeader) {
    return new EntryPosition(logHeader.getLogFileName(), logHeader.getLogPos(), logHeader.getWhen() * 1000L, logHeader.getServerId());
  }

  private boolean parseOneRow(RowData.Builder rowDataBuilder, RowsLogEvent event, RowsLogBuffer buffer, BitSet cols, boolean isAfter, TableMeta tableMeta) throws UnsupportedEncodingException {
    int columnCnt = event.getTable().getColumnCnt();
    ColumnInfo[] columnInfo = event.getTable().getColumnInfo();
    boolean tableError = false;
    boolean existRDSNoPrimaryKey = false;
    if (tableMeta != null && columnInfo.length > tableMeta.getFields().size()) {
      if (tableMetaCache.isOnRDS()) {
        List<FieldMeta> primaryKeys = tableMeta.getPrimaryFields();
        if (primaryKeys == null || primaryKeys.isEmpty()) {
          if (columnInfo.length == tableMeta.getFields().size() + 1 && columnInfo[columnInfo.length - 1].type == LogEvent.MYSQL_TYPE_LONGLONG) {
            existRDSNoPrimaryKey = true;
          }
        }
      }
      EntryPosition position = createPosition(event.getHeader());
      if (!existRDSNoPrimaryKey) {
        tableMeta = getTableMeta(event.getTable().getDbName(), event.getTable().getTableName(), false, position);
        if (tableMeta == null) {
          tableError = true;
          if (!filterTableError) {
            throw new CanalParseException("not found [" + event.getTable().getDbName() + "." + event.getTable().getTableName() + "] in db , pls check!");
          }
        }
        if (tableMeta != null && columnInfo.length > tableMeta.getFields().size()) {
          tableError = true;
          if (!filterTableError) {
            throw new CanalParseException("column size is not match for table:" + tableMeta.getFullName() + "," + columnInfo.length + " vs " + tableMeta.getFields().size());
          }
        }
      } else {
        logger.warn("[" + event.getTable().getDbName() + "." + event.getTable().getTableName() + "] is no primary key , skip alibaba_rds_row_id column");
      }
    }
    for (int i = 0; i < columnCnt; i++) {
      ColumnInfo info = columnInfo[i];
      if (!cols.get(i)) {
        continue;
      }
      if (existRDSNoPrimaryKey && i == columnCnt - 1 && info.type == LogEvent.MYSQL_TYPE_LONGLONG) {
        buffer.nextValue(info.type, info.meta, false);
        continue;
      }
      Column.Builder columnBuilder = Column.newBuilder();
      FieldMeta fieldMeta = null;
      if (tableMeta != null && !tableError) {
        fieldMeta = tableMeta.getFields().get(i);
        columnBuilder.setName(fieldMeta.getColumnName());
        columnBuilder.setIsKey(fieldMeta.isKey());
        columnBuilder.setMysqlType(fieldMeta.getColumnType());
      }
      columnBuilder.setIndex(i);
      columnBuilder.setIsNull(false);
      boolean isBinary = false;
      boolean isSingleBit = false;
      if (fieldMeta != null) {
        if (StringUtils.containsIgnoreCase(fieldMeta.getColumnType(), "VARBINARY")) {
          isBinary = true;
        } else {
          if (StringUtils.containsIgnoreCase(fieldMeta.getColumnType(), "BINARY")) {
            isBinary = true;
          } else {
            if (StringUtils.containsIgnoreCase(fieldMeta.getColumnType(), "TINYINT(1)")) {
              isSingleBit = true;
            }
          }
        }
      }
      buffer.nextValue(info.type, info.meta, isBinary);
      if (existRDSNoPrimaryKey && i == columnCnt - 1 && info.type == LogEvent.MYSQL_TYPE_LONGLONG) {
        continue;
      }
      int javaType = buffer.getJavaType();
      if (isSingleBit && javaType == Types.TINYINT) {
        javaType = Types.BIT;
      }
      if (buffer.isNull()) {
        columnBuilder.setIsNull(true);
      } else {
        final Serializable value = buffer.getValue();
        switch (javaType) {
          case Types.INTEGER:
          case Types.TINYINT:
          case Types.SMALLINT:
          case Types.BIGINT:
          Number number = (Number) value;
          if (fieldMeta != null && fieldMeta.isUnsigned() && number.longValue() < 0) {
            switch (buffer.getLength()) {
              case 1:
              columnBuilder.setValue(String.valueOf(Integer.valueOf(TINYINT_MAX_VALUE + number.intValue())));
              javaType = Types.SMALLINT;
              break;
              case 2:
              columnBuilder.setValue(String.valueOf(Integer.valueOf(SMALLINT_MAX_VALUE + number.intValue())));
              javaType = Types.INTEGER;
              break;
              case 3:
              columnBuilder.setValue(String.valueOf(Integer.valueOf(MEDIUMINT_MAX_VALUE + number.intValue())));
              javaType = Types.INTEGER;
              break;
              case 4:
              columnBuilder.setValue(String.valueOf(Long.valueOf(INTEGER_MAX_VALUE + number.longValue())));
              javaType = Types.BIGINT;
              break;
              case 8:
              columnBuilder.setValue(BIGINT_MAX_VALUE.add(BigInteger.valueOf(number.longValue())).toString());
              javaType = Types.DECIMAL;
              break;
            }
          } else {
            columnBuilder.setValue(String.valueOf(value));
          }
          break;
          case Types.REAL:
          case Types.DOUBLE:
          columnBuilder.setValue(String.valueOf(value));
          break;
          case Types.BIT:
          columnBuilder.setValue(String.valueOf(value));
          break;
          case Types.DECIMAL:
          columnBuilder.setValue(((BigDecimal) value).toPlainString());
          break;
          case Types.TIMESTAMP:
          case Types.TIME:
          case Types.DATE:
          columnBuilder.setValue(value.toString());
          break;
          case Types.BINARY:
          case Types.VARBINARY:
          case Types.LONGVARBINARY:
          if (fieldMeta != null && isText(fieldMeta.getColumnType())) {
            columnBuilder.setValue(new String((byte[]) value, charset));
            javaType = Types.CLOB;
          } else {
            columnBuilder.setValue(new String((byte[]) value, ISO_8859_1));
            javaType = Types.BLOB;
          }
          break;
          case Types.CHAR:
          case Types.VARCHAR:
          columnBuilder.setValue(value.toString());
          break;
          default:
          columnBuilder.setValue(value.toString());
        }
      }
      columnBuilder.setSqlType(javaType);
      columnBuilder.setUpdated(isAfter && isUpdate(rowDataBuilder.getBeforeColumnsList(), columnBuilder.getIsNull() ? null : columnBuilder.getValue(), i));
      if (isAfter) {
        rowDataBuilder.addAfterColumns(columnBuilder.build());
      } else {
        rowDataBuilder.addBeforeColumns(columnBuilder.build());
      }
    }
    return tableError;
  }

  private Entry buildQueryEntry(String queryString, LogHeader logHeader, String tableName) {
    Header header = createHeader(logHeader, "", tableName, EventType.QUERY);
    RowChange.Builder rowChangeBuider = RowChange.newBuilder();
    rowChangeBuider.setSql(queryString);
    rowChangeBuider.setEventType(EventType.QUERY);
    return createEntry(header, EntryType.ROWDATA, rowChangeBuider.build().toByteString());
  }

  private Entry buildQueryEntry(String queryString, LogHeader logHeader) {
    Header header = createHeader(logHeader, "", "", EventType.QUERY);
    RowChange.Builder rowChangeBuider = RowChange.newBuilder();
    rowChangeBuider.setSql(queryString);
    rowChangeBuider.setEventType(EventType.QUERY);
    return createEntry(header, EntryType.ROWDATA, rowChangeBuider.build().toByteString());
  }

  private boolean isUpdate(List<Column> bfColumns, String newValue, int index) {
    if (bfColumns == null) {
      throw new CanalParseException("ERROR ## the bfColumns is null");
    }
    if (index < 0) {
      return false;
    }
    for (Column column : bfColumns) {
      if (column.getIndex() == index) {
        if (column.getIsNull() && newValue == null) {
          return false;
        } else {
          if (newValue != null && (!column.getIsNull() && column.getValue().equals(newValue))) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private TableMeta getTableMeta(String dbName, String tbName, boolean useCache, EntryPosition position) {
    try {
      return tableMetaCache.getTableMeta(dbName, tbName, useCache, position);
    } catch (Exception e) {
      String message = ExceptionUtils.getRootCauseMessage(e);
      if (filterTableError) {
        if (StringUtils.contains(message, "errorNumber=1146") && StringUtils.contains(message, "doesn\'t exist")) {
          return null;
        }
      }
      throw new CanalParseException(e);
    }
  }

  private boolean isText(String columnType) {
    return "LONGTEXT".equalsIgnoreCase(columnType) || "MEDIUMTEXT".equalsIgnoreCase(columnType) || "TEXT".equalsIgnoreCase(columnType) || "TINYTEXT".equalsIgnoreCase(columnType);
  }

  private boolean isAliSQLHeartBeat(String schema, String table) {
    return "test".equalsIgnoreCase(schema) && "heartbeat".equalsIgnoreCase(table);
  }

  private boolean isRDSHeartBeat(String schema, String table) {
    return "mysql".equalsIgnoreCase(schema) && "ha_health_check".equalsIgnoreCase(table);
  }

  public static TransactionBegin createTransactionBegin(long threadId) {
    TransactionBegin.Builder beginBuilder = TransactionBegin.newBuilder();
    beginBuilder.setThreadId(threadId);
    return beginBuilder.build();
  }

  public static TransactionEnd createTransactionEnd(long transactionId) {
    TransactionEnd.Builder endBuilder = TransactionEnd.newBuilder();
    endBuilder.setTransactionId(String.valueOf(transactionId));
    return endBuilder.build();
  }

  public static Pair createSpecialPair(String key, String value) {
    Pair.Builder pairBuilder = Pair.newBuilder();
    pairBuilder.setKey(key);
    pairBuilder.setValue(value);
    return pairBuilder.build();
  }

  public static Entry createEntry(Header header, EntryType entryType, ByteString storeValue) {
    Entry.Builder entryBuilder = Entry.newBuilder();
    entryBuilder.setHeader(header);
    entryBuilder.setEntryType(entryType);
    entryBuilder.setStoreValue(storeValue);
    return entryBuilder.build();
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  public void setNameFilter(AviaterRegexFilter nameFilter) {
    this.nameFilter = nameFilter;
  }

  public void setNameBlackFilter(AviaterRegexFilter nameBlackFilter) {
    this.nameBlackFilter = nameBlackFilter;
  }

  public void setTableMetaCache(TableMetaCacheInterface tableMetaCache) {
    this.tableMetaCache = tableMetaCache;
  }

  public void setFilterQueryDcl(boolean filterQueryDcl) {
    this.filterQueryDcl = filterQueryDcl;
  }

  public void setFilterQueryDml(boolean filterQueryDml) {
    this.filterQueryDml = filterQueryDml;
  }

  public void setFilterQueryDdl(boolean filterQueryDdl) {
    this.filterQueryDdl = filterQueryDdl;
  }

  public void setFilterTableError(boolean filterTableError) {
    this.filterTableError = filterTableError;
  }

  public void setFilterRows(boolean filterRows) {
    this.filterRows = filterRows;
  }

  public void setGtidSet(GTIDSet gtidSet) {
    this.gtidSet = gtidSet;
  }

  public TableMeta parseRowsEventForTableMeta(RowsLogEvent event) {
    TableMapLogEvent table = event.getTable();
    if (table == null) {
      throw new TableIdNotFoundException("not found tableId:" + event.getTableId());
    }
    boolean isHeartBeat = isAliSQLHeartBeat(table.getDbName(), table.getTableName());
    boolean isRDSHeartBeat = tableMetaCache.isOnRDS() && isRDSHeartBeat(table.getDbName(), table.getTableName());
    String fullname = table.getDbName() + "." + table.getTableName();
    if (nameFilter != null && !nameFilter.filter(fullname)) {
      return null;
    }
    if (nameBlackFilter != null && nameBlackFilter.filter(fullname)) {
      return null;
    }
    TableMeta tableMeta = null;
    if (isRDSHeartBeat) {
      FieldMeta idMeta = new FieldMeta("id", "bigint(20)", true, false, "0");
      FieldMeta typeMeta = new FieldMeta("type", "char(1)", false, true, "0");
      tableMeta = new TableMeta(table.getDbName(), table.getTableName(), Arrays.asList(idMeta, typeMeta));
    } else {
      if (isHeartBeat) {
        FieldMeta idMeta = new FieldMeta("id", "smallint(6)", false, true, null);
        FieldMeta typeMeta = new FieldMeta("ts", "int(11)", true, false, null);
        tableMeta = new TableMeta(table.getDbName(), table.getTableName(), Arrays.asList(idMeta, typeMeta));
      }
    }
    EntryPosition position = createPosition(event.getHeader());
    if (tableMetaCache != null && tableMeta == null) {
      tableMeta = getTableMeta(table.getDbName(), table.getTableName(), true, position);
      if (tableMeta == null) {
        if (!filterTableError) {
          throw new CanalParseException("not found [" + fullname + "] in db , pls check!");
        }
      }
    }
    return tableMeta;
  }

  public Entry parseRowsEvent(RowsLogEvent event, TableMeta tableMeta) {
    if (filterRows) {
      return null;
    }
    try {
      if (tableMeta == null) {
        tableMeta = parseRowsEventForTableMeta(event);
      }
      if (tableMeta == null) {
        return null;
      }
      EventType eventType = null;
      int type = event.getHeader().getType();
      if (LogEvent.WRITE_ROWS_EVENT_V1 == type || LogEvent.WRITE_ROWS_EVENT == type) {
        eventType = EventType.INSERT;
      } else {
        if (LogEvent.UPDATE_ROWS_EVENT_V1 == type || LogEvent.UPDATE_ROWS_EVENT == type) {
          eventType = EventType.UPDATE;
        } else {
          if (LogEvent.DELETE_ROWS_EVENT_V1 == type || LogEvent.DELETE_ROWS_EVENT == type) {
            eventType = EventType.DELETE;
          } else {
            throw new CanalParseException("unsupport event type :" + event.getHeader().getType());
          }
        }
      }
      RowChange.Builder rowChangeBuider = RowChange.newBuilder();
      rowChangeBuider.setTableId(event.getTableId());
      rowChangeBuider.setIsDdl(false);
      rowChangeBuider.setEventType(eventType);
      RowsLogBuffer buffer = event.getRowsBuf(charset.name());
      BitSet columns = event.getColumns();
      BitSet changeColumns = event.getChangeColumns();
      boolean tableError = false;
      int rowsCount = 0;
      while (buffer.nextOneRow(columns)) {
        RowData.Builder rowDataBuilder = RowData.newBuilder();
        if (EventType.INSERT == eventType) {
          tableError |= parseOneRow(rowDataBuilder, event, buffer, columns, true, tableMeta);
        } else {
          if (EventType.DELETE == eventType) {
            tableError |= parseOneRow(rowDataBuilder, event, buffer, columns, false, tableMeta);
          } else {
            tableError |= parseOneRow(rowDataBuilder, event, buffer, columns, false, tableMeta);
            if (!buffer.nextOneRow(changeColumns)) {
              rowChangeBuider.addRowDatas(rowDataBuilder.build());
              break;
            }
            tableError |= parseOneRow(rowDataBuilder, event, buffer, changeColumns, true, tableMeta);
          }
        }
        rowsCount++;
        rowChangeBuider.addRowDatas(rowDataBuilder.build());
      }
      TableMapLogEvent table = event.getTable();
      Header header = createHeader(event.getHeader(), table.getDbName(), table.getTableName(), eventType, rowsCount);
      RowChange rowChange = rowChangeBuider.build();
      if (tableError) {
        Entry entry = createEntry(header, EntryType.ROWDATA, ByteString.EMPTY);
        logger.warn("table parser error : {}storeValue: {}", entry.toString(), rowChange.toString());
        return null;
      } else {
        Entry entry = createEntry(header, EntryType.ROWDATA, rowChange.toByteString());
        return entry;
      }
    } catch (Exception e) {
      throw new CanalParseException("parse row data failed.", e);
    }
  }

  private Header createHeader(LogHeader logHeader, String schemaName, String tableName, EventType eventType) {
    return createHeader(logHeader, schemaName, tableName, eventType, -1);
  }

  private Header createHeader(LogHeader logHeader, String schemaName, String tableName, EventType eventType, Integer rowsCount) {
    Header.Builder headerBuilder = Header.newBuilder();
    headerBuilder.setVersion(version);
    headerBuilder.setLogfileName(logHeader.getLogFileName());
    headerBuilder.setLogfileOffset(logHeader.getLogPos() - logHeader.getEventLen());
    headerBuilder.setServerId(logHeader.getServerId());
    headerBuilder.setServerenCode(UTF_8);
    headerBuilder.setExecuteTime(logHeader.getWhen() * 1000L);
    headerBuilder.setSourceType(Type.MYSQL);
    if (eventType != null) {
      headerBuilder.setEventType(eventType);
    }
    if (schemaName != null) {
      headerBuilder.setSchemaName(schemaName);
    }
    if (tableName != null) {
      headerBuilder.setTableName(tableName);
    }
    headerBuilder.setEventLength(logHeader.getEventLen());
    if (gtidSet != null) {
      String gtid = gtidSet.toString();
      headerBuilder.setGtid(gtid);
    }
    if (rowsCount > 0) {
      Pair pair = createSpecialPair("rowsCount", String.valueOf(rowsCount));
      headerBuilder.addProps(pair);
    }
    return headerBuilder.build();
  }
}