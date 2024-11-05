package com.alibaba.otter.canal.parse.inbound.mysql;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import com.alibaba.otter.canal.parse.inbound.*;
import com.alibaba.otter.canal.parse.inbound.mysql.tablemeta.TableMetaCacheInterface;
import com.alibaba.otter.canal.parse.inbound.mysql.tablemeta.TableMetaCacheWithStorage;
import com.alibaba.otter.canal.parse.inbound.mysql.tablemeta.TableMetaStorage;
import com.alibaba.otter.canal.parse.inbound.mysql.tablemeta.TableMetaStorageFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import com.alibaba.otter.canal.common.utils.JsonUtils;
import com.alibaba.otter.canal.parse.CanalEventParser;
import com.alibaba.otter.canal.parse.CanalHASwitchable;
import com.alibaba.otter.canal.parse.driver.mysql.packets.server.FieldPacket;
import com.alibaba.otter.canal.parse.driver.mysql.packets.server.ResultSetPacket;
import com.alibaba.otter.canal.parse.exception.CanalParseException;
import com.alibaba.otter.canal.parse.ha.CanalHAController;
import com.alibaba.otter.canal.parse.inbound.mysql.MysqlConnection.BinlogFormat;
import com.alibaba.otter.canal.parse.inbound.mysql.MysqlConnection.BinlogImage;
import com.alibaba.otter.canal.parse.inbound.mysql.dbsync.LogEventConvert;
import com.alibaba.otter.canal.parse.inbound.mysql.dbsync.TableMetaCache;
import com.alibaba.otter.canal.parse.inbound.mysql.tsdb.DatabaseTableMeta;
import com.alibaba.otter.canal.parse.support.AuthenticationInfo;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.position.EntryPosition;
import com.alibaba.otter.canal.protocol.position.LogPosition;
import com.taobao.tddl.dbsync.binlog.LogEvent;

public class MysqlEventParser extends AbstractMysqlEventParser implements CanalEventParser, CanalHASwitchable {
  private CanalHAController haController = null;

  private int defaultConnectionTimeoutInSeconds = 30;

  private int receiveBufferSize = 64 * 1024;

  private int sendBufferSize = 64 * 1024;

  private AuthenticationInfo masterInfo;

  private AuthenticationInfo standbyInfo;

  private EntryPosition masterPosition;

  private EntryPosition standbyPosition;

  private long slaveId;

  private String detectingSQL;

  private MysqlConnection metaConnection;

  private TableMetaCacheInterface tableMetaCache;

  private int fallbackIntervalInSeconds = 60;

  private BinlogFormat[] supportBinlogFormats;

  private BinlogImage[] supportBinlogImages;

  private int dumpErrorCount = 0;

  private int dumpErrorCountThreshold = 2;

  private TableMetaStorageFactory tableMetaStorageFactory;

  protected ErosaConnection buildErosaConnection() {
    return buildMysqlConnection(this.runningInfo);
  }

  protected void preDump(ErosaConnection connection) {
    if (!(connection instanceof MysqlConnection)) {
      throw new CanalParseException("Unsupported connection type : " + connection.getClass().getSimpleName());
    }
    if (binlogParser != null && binlogParser instanceof LogEventConvert) {
      metaConnection = (MysqlConnection) connection.fork();
      try {
        metaConnection.connect();
      } catch (IOException e) {
        throw new CanalParseException(e);
      }
      if (supportBinlogFormats != null && supportBinlogFormats.length > 0) {
        BinlogFormat format = ((MysqlConnection) metaConnection).getBinlogFormat();
        boolean found = false;
        for (BinlogFormat supportFormat : supportBinlogFormats) {
          if (supportFormat != null && format == supportFormat) {
            found = true;
            break;
          }
        }
        if (!found) {
          throw new CanalParseException("Unsupported BinlogFormat " + format);
        }
      }
      if (supportBinlogImages != null && supportBinlogImages.length > 0) {
        BinlogImage image = ((MysqlConnection) metaConnection).getBinlogImage();
        boolean found = false;
        for (BinlogImage supportImage : supportBinlogImages) {
          if (supportImage != null && image == supportImage) {
            found = true;
            break;
          }
        }
        if (!found) {
          throw new CanalParseException("Unsupported BinlogImage " + image);
        }
      }
      if (tableMetaTSDB != null && tableMetaTSDB instanceof DatabaseTableMeta) {
        ((DatabaseTableMeta) tableMetaTSDB).setConnection(metaConnection);
        ((DatabaseTableMeta) tableMetaTSDB).setFilter(eventFilter);
        ((DatabaseTableMeta) tableMetaTSDB).setBlackFilter(eventBlackFilter);
      }
      TableMetaStorage storage = null;
      if (tableMetaStorageFactory != null) {
        storage = tableMetaStorageFactory.getTableMetaStorage();
      }
      tableMetaCache = new TableMetaCacheWithStorage(metaConnection, storage);
      ((LogEventConvert) binlogParser).setTableMetaCache(tableMetaCache);
    }
  }

  protected void afterDump(ErosaConnection connection) {
    super.afterDump(connection);
    if (!(connection instanceof MysqlConnection)) {
      throw new CanalParseException("Unsupported connection type : " + connection.getClass().getSimpleName());
    }
    if (metaConnection != null) {
      try {
        metaConnection.disconnect();
      } catch (IOException e) {
        logger.error("ERROR # disconnect meta connection for address:{}", metaConnection.getConnector().getAddress(), e);
      }
    }
  }

  public void start() throws CanalParseException {
    if (runningInfo == null) {
      runningInfo = masterInfo;
    }
    super.start();
  }

  public void stop() throws CanalParseException {
    if (metaConnection != null) {
      try {
        metaConnection.disconnect();
      } catch (IOException e) {
        logger.error("ERROR # disconnect meta connection for address:{}", metaConnection.getConnector().getAddress(), e);
      }
    }
    if (tableMetaCache != null) {
      tableMetaCache.clearTableMeta();
    }
    super.stop();
  }

  protected TimerTask buildHeartBeatTimeTask(ErosaConnection connection) {
    if (!(connection instanceof MysqlConnection)) {
      throw new CanalParseException("Unsupported connection type : " + connection.getClass().getSimpleName());
    }
    if (detectingEnable && StringUtils.isNotBlank(detectingSQL)) {
      return new MysqlDetectingTimeTask((MysqlConnection) connection.fork());
    } else {
      return super.buildHeartBeatTimeTask(connection);
    }
  }

  protected void stopHeartBeat() {
    TimerTask heartBeatTimerTask = this.heartBeatTimerTask;
    super.stopHeartBeat();
    if (heartBeatTimerTask != null && heartBeatTimerTask instanceof MysqlDetectingTimeTask) {
      MysqlConnection mysqlConnection = ((MysqlDetectingTimeTask) heartBeatTimerTask).getMysqlConnection();
      try {
        mysqlConnection.disconnect();
      } catch (IOException e) {
        logger.error("ERROR # disconnect heartbeat connection for address:{}", mysqlConnection.getConnector().getAddress(), e);
      }
    }
  }

  class MysqlDetectingTimeTask extends TimerTask {
    private boolean reconnect = false;

    private MysqlConnection mysqlConnection;

    public MysqlDetectingTimeTask(MysqlConnection mysqlConnection) {
      this.mysqlConnection = mysqlConnection;
    }

    public void run() {
      try {
        if (reconnect) {
          reconnect = false;
          mysqlConnection.reconnect();
        } else {
          if (!mysqlConnection.isConnected()) {
            mysqlConnection.connect();
          }
        }
        Long startTime = System.currentTimeMillis();
        if (StringUtils.startsWithIgnoreCase(detectingSQL.trim(), "select") || StringUtils.startsWithIgnoreCase(detectingSQL.trim(), "show") || StringUtils.startsWithIgnoreCase(detectingSQL.trim(), "explain") || StringUtils.startsWithIgnoreCase(detectingSQL.trim(), "desc")) {
          mysqlConnection.query(detectingSQL);
        } else {
          mysqlConnection.update(detectingSQL);
        }
        Long costTime = System.currentTimeMillis() - startTime;
        if (haController != null && haController instanceof HeartBeatCallback) {
          ((HeartBeatCallback) haController).onSuccess(costTime);
        }
      } catch (SocketTimeoutException e) {
        if (haController != null && haController instanceof HeartBeatCallback) {
          ((HeartBeatCallback) haController).onFailed(e);
        }
        reconnect = true;
        logger.warn("connect failed by ", e);
      } catch (IOException e) {
        if (haController != null && haController instanceof HeartBeatCallback) {
          ((HeartBeatCallback) haController).onFailed(e);
        }
        reconnect = true;
        logger.warn("connect failed by ", e);
      } catch (Throwable e) {
        if (haController != null && haController instanceof HeartBeatCallback) {
          ((HeartBeatCallback) haController).onFailed(e);
        }
        reconnect = true;
        logger.warn("connect failed by ", e);
      }
    }

    public MysqlConnection getMysqlConnection() {
      return mysqlConnection;
    }
  }

  public void doSwitch() {
    AuthenticationInfo newRunningInfo = (runningInfo.equals(masterInfo) ? standbyInfo : masterInfo);
    this.doSwitch(newRunningInfo);
  }

  public void doSwitch(AuthenticationInfo newRunningInfo) {
    String alarmMessage = null;
    if (this.runningInfo.equals(newRunningInfo)) {
      alarmMessage = "same runingInfo switch again : " + runningInfo.getAddress().toString();
      logger.warn(alarmMessage);
      return;
    }
    if (newRunningInfo == null) {
      alarmMessage = "no standby config, just do nothing, will continue try:" + runningInfo.getAddress().toString();
      logger.warn(alarmMessage);
      sendAlarm(destination, alarmMessage);
      return;
    } else {
      stop();
      alarmMessage = "try to ha switch, old:" + runningInfo.getAddress().toString() + ", new:" + newRunningInfo.getAddress().toString();
      logger.warn(alarmMessage);
      sendAlarm(destination, alarmMessage);
      runningInfo = newRunningInfo;
      start();
    }
  }

  private MysqlConnection buildMysqlConnection(AuthenticationInfo runningInfo) {
    MysqlConnection connection = new MysqlConnection(runningInfo.getAddress(), runningInfo.getUsername(), runningInfo.getPassword(), connectionCharsetNumber, runningInfo.getDefaultDatabaseName());
    connection.getConnector().setReceiveBufferSize(receiveBufferSize);
    connection.getConnector().setSendBufferSize(sendBufferSize);
    connection.getConnector().setSoTimeout(defaultConnectionTimeoutInSeconds * 1000);
    connection.setCharset(connectionCharset);
    if (this.slaveId <= 0) {
      this.slaveId = generateUniqueServerId();
    }
    connection.setSlaveId(this.slaveId);
    return connection;
  }

  private final long generateUniqueServerId() {
    try {
      InetAddress localHost = InetAddress.getLocalHost();
      byte[] addr = localHost.getAddress();
      int salt = (destination != null) ? destination.hashCode() : 0;
      return ((0x7f & salt) << 24) + ((0xff & (int) addr[1]) << 16) + ((0xff & (int) addr[2]) << 8) + (0xff & (int) addr[3]);
    } catch (UnknownHostException e) {
      throw new CanalParseException("Unknown host", e);
    }
  }

  protected EntryPosition findStartPosition(ErosaConnection connection) throws IOException {
    if (isGTIDMode()) {
      LogPosition logPosition = getLogPositionManager().getLatestIndexBy(destination);
      if (logPosition != null) {
        return logPosition.getPostion();
      }
      if (StringUtils.isNotEmpty(masterPosition.getGtid())) {
        return masterPosition;
      }
    }
    EntryPosition startPosition = findStartPositionInternal(connection);
    if (needTransactionPosition.get()) {
      logger.warn("prepare to find last position : {}", startPosition.toString());
      Long preTransactionStartPosition = findTransactionBeginPosition(connection, startPosition);
      if (!preTransactionStartPosition.equals(startPosition.getPosition())) {
        logger.warn("find new start Transaction Position , old : {} , new : {}", startPosition.getPosition(), preTransactionStartPosition);
        startPosition.setPosition(preTransactionStartPosition);
      }
      needTransactionPosition.compareAndSet(true, false);
    }
    return startPosition;
  }

  protected EntryPosition findEndPosition(ErosaConnection connection) throws IOException {
    MysqlConnection mysqlConnection = (MysqlConnection) connection;
    EntryPosition endPosition = findEndPosition(mysqlConnection);
    return endPosition;
  }

  protected EntryPosition findEndPositionWithMasterIdAndTimestamp(MysqlConnection connection) {
    MysqlConnection mysqlConnection = (MysqlConnection) connection;
    final EntryPosition endPosition = findEndPosition(mysqlConnection);
    if (tableMetaTSDB != null) {
      long startTimestamp = System.currentTimeMillis();
      return findAsPerTimestampInSpecificLogFile(mysqlConnection, startTimestamp, endPosition, endPosition.getJournalName(), true);
    } else {
      return endPosition;
    }
  }

  protected EntryPosition findPositionWithMasterIdAndTimestamp(MysqlConnection connection, EntryPosition fixedPosition) {
    MysqlConnection mysqlConnection = (MysqlConnection) connection;
    if (tableMetaTSDB != null && (fixedPosition.getTimestamp() == null || fixedPosition.getTimestamp() <= 0)) {
      long startTimestamp = System.currentTimeMillis() + 102L * 365 * 24 * 3600 * 1000;
      EntryPosition entryPosition = findAsPerTimestampInSpecificLogFile(mysqlConnection, startTimestamp, fixedPosition, fixedPosition.getJournalName(), true);
      if (entryPosition == null) {
        throw new CanalParseException("[fixed timestamp] can\'t found begin/commit position before with fixed position" + fixedPosition.getJournalName() + ":" + fixedPosition.getPosition());
      }
      return entryPosition;
    } else {
      return fixedPosition;
    }
  }

  protected EntryPosition findStartPositionInternal(ErosaConnection connection) {
    MysqlConnection mysqlConnection = (MysqlConnection) connection;
    LogPosition logPosition = logPositionManager.getLatestIndexBy(destination);
    if (logPosition == null) {
      EntryPosition entryPosition = null;
      if (masterInfo != null && mysqlConnection.getConnector().getAddress().equals(masterInfo.getAddress())) {
        entryPosition = masterPosition;
      } else {
        if (standbyInfo != null && mysqlConnection.getConnector().getAddress().equals(standbyInfo.getAddress())) {
          entryPosition = standbyPosition;
        }
      }
      if (entryPosition == null) {
        entryPosition = findEndPositionWithMasterIdAndTimestamp(mysqlConnection);
      }
      if (StringUtils.isEmpty(entryPosition.getJournalName())) {
        if (entryPosition.getTimestamp() != null && entryPosition.getTimestamp() > 0L) {
          logger.warn("prepare to find start position {}:{}:{}", new Object[] { "", "", entryPosition.getTimestamp() });
          return findByStartTimeStamp(mysqlConnection, entryPosition.getTimestamp());
        } else {
          logger.warn("prepare to find start position just show master status");
          return findEndPositionWithMasterIdAndTimestamp(mysqlConnection);
        }
      } else {
        if (entryPosition.getPosition() != null && entryPosition.getPosition() > 0L) {
          entryPosition = findPositionWithMasterIdAndTimestamp(mysqlConnection, entryPosition);
          logger.warn("prepare to find start position {}:{}:{}", new Object[] { entryPosition.getJournalName(), entryPosition.getPosition(), entryPosition.getTimestamp() });
          return entryPosition;
        } else {
          EntryPosition specificLogFilePosition = null;
          if (entryPosition.getTimestamp() != null && entryPosition.getTimestamp() > 0L) {
            EntryPosition endPosition = findEndPosition(mysqlConnection);
            if (endPosition != null) {
              logger.warn("prepare to find start position {}:{}:{}", new Object[] { entryPosition.getJournalName(), "", entryPosition.getTimestamp() });
              specificLogFilePosition = findAsPerTimestampInSpecificLogFile(mysqlConnection, entryPosition.getTimestamp(), endPosition, entryPosition.getJournalName(), true);
            }
          }
          if (specificLogFilePosition == null) {
            entryPosition.setPosition(BINLOG_START_OFFEST);
            return entryPosition;
          } else {
            return specificLogFilePosition;
          }
        }
      }
    } else {
      if (logPosition.getIdentity().getSourceAddress().equals(mysqlConnection.getConnector().getAddress())) {
        if (dumpErrorCountThreshold >= 0 && dumpErrorCount > dumpErrorCountThreshold) {
          boolean case2 = (standbyInfo == null || standbyInfo.getAddress() == null) && logPosition.getPostion().getServerId() != null && !logPosition.getPostion().getServerId().equals(findServerId(mysqlConnection));
          if (case2) {
            long timestamp = logPosition.getPostion().getTimestamp();
            long newStartTimestamp = timestamp - fallbackIntervalInSeconds * 1000;
            logger.warn("prepare to find start position by last position {}:{}:{}", new Object[] { "", "", logPosition.getPostion().getTimestamp() });
            EntryPosition findPosition = findByStartTimeStamp(mysqlConnection, newStartTimestamp);
            dumpErrorCount = 0;
            return findPosition;
          }
        }
        logger.warn("prepare to find start position just last position\n {}", JsonUtils.marshalToString(logPosition));
        return logPosition.getPostion();
      } else {
        long newStartTimestamp = logPosition.getPostion().getTimestamp() - fallbackIntervalInSeconds * 1000;
        logger.warn("prepare to find start position by switch {}:{}:{}", new Object[] { "", "", logPosition.getPostion().getTimestamp() });
        return findByStartTimeStamp(mysqlConnection, newStartTimestamp);
      }
    }
  }

  private Long findTransactionBeginPosition(ErosaConnection mysqlConnection, final EntryPosition entryPosition) throws IOException {
    final AtomicLong preTransactionStartPosition = new AtomicLong(0L);
    mysqlConnection.reconnect();
    mysqlConnection.seek(entryPosition.getJournalName(), 4L, new SinkFunction<LogEvent>() {
      private LogPosition lastPosition;

      public boolean sink(LogEvent event) {
        try {
          CanalEntry.Entry entry = parseAndProfilingIfNecessary(event, true);
          if (entry == null) {
            return true;
          }
          if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN && entry.getHeader().getLogfileOffset() < entryPosition.getPosition()) {
            preTransactionStartPosition.set(entry.getHeader().getLogfileOffset());
          }
          if (entry.getHeader().getLogfileOffset() >= entryPosition.getPosition()) {
            return false;
          }
          lastPosition = buildLastPosition(entry);
        } catch (Exception e) {
          processSinkError(e, lastPosition, entryPosition.getJournalName(), entryPosition.getPosition());
          return false;
        }
        return running;
      }
    });
    if (preTransactionStartPosition.get() > entryPosition.getPosition()) {
      logger.error("preTransactionEndPosition greater than startPosition from zk or localconf, maybe lost data");
      throw new CanalParseException("preTransactionStartPosition greater than startPosition from zk or localconf, maybe lost data");
    }
    return preTransactionStartPosition.get();
  }

  private EntryPosition findByStartTimeStamp(MysqlConnection mysqlConnection, Long startTimestamp) {
    EntryPosition endPosition = findEndPosition(mysqlConnection);
    EntryPosition startPosition = findStartPosition(mysqlConnection);
    String maxBinlogFileName = endPosition.getJournalName();
    String minBinlogFileName = startPosition.getJournalName();
    logger.info("show master status to set search end condition:{} ", endPosition);
    String startSearchBinlogFile = endPosition.getJournalName();
    boolean shouldBreak = false;
    while (running && !shouldBreak) {
      try {
        EntryPosition entryPosition = findAsPerTimestampInSpecificLogFile(mysqlConnection, startTimestamp, endPosition, startSearchBinlogFile, false);
        if (entryPosition == null) {
          if (StringUtils.equalsIgnoreCase(minBinlogFileName, startSearchBinlogFile)) {
            shouldBreak = true;
            logger.warn("Didn\'t find the corresponding binlog files from {} to {}", minBinlogFileName, maxBinlogFileName);
          } else {
            int binlogSeqNum = Integer.parseInt(startSearchBinlogFile.substring(startSearchBinlogFile.indexOf(".") + 1));
            if (binlogSeqNum <= 1) {
              logger.warn("Didn\'t find the corresponding binlog files");
              shouldBreak = true;
            } else {
              int nextBinlogSeqNum = binlogSeqNum - 1;
              String binlogFileNamePrefix = startSearchBinlogFile.substring(0, startSearchBinlogFile.indexOf(".") + 1);
              String binlogFileNameSuffix = String.format("%06d", nextBinlogSeqNum);
              startSearchBinlogFile = binlogFileNamePrefix + binlogFileNameSuffix;
            }
          }
        } else {
          logger.info("found and return:{} in findByStartTimeStamp operation.", entryPosition);
          return entryPosition;
        }
      } catch (Exception e) {
        logger.warn(String.format("the binlogfile:%s doesn\'t exist, to continue to search the next binlogfile , caused by", startSearchBinlogFile), e);
        int binlogSeqNum = Integer.parseInt(startSearchBinlogFile.substring(startSearchBinlogFile.indexOf(".") + 1));
        if (binlogSeqNum <= 1) {
          logger.warn("Didn\'t find the corresponding binlog files");
          shouldBreak = true;
        } else {
          int nextBinlogSeqNum = binlogSeqNum - 1;
          String binlogFileNamePrefix = startSearchBinlogFile.substring(0, startSearchBinlogFile.indexOf(".") + 1);
          String binlogFileNameSuffix = String.format("%06d", nextBinlogSeqNum);
          startSearchBinlogFile = binlogFileNamePrefix + binlogFileNameSuffix;
        }
      }
    }
    return null;
  }

  private Long findServerId(MysqlConnection mysqlConnection) {
    try {
      ResultSetPacket packet = mysqlConnection.query("show variables like \'server_id\'");
      List<String> fields = packet.getFieldValues();
      if (CollectionUtils.isEmpty(fields)) {
        throw new CanalParseException("command : show variables like \'server_id\' has an error! pls check. you need (at least one of) the SUPER,REPLICATION CLIENT privilege(s) for this operation");
      }
      return Long.valueOf(fields.get(1));
    } catch (IOException e) {
      throw new CanalParseException("command : show variables like \'server_id\' has an error!", e);
    }
  }

  private EntryPosition findEndPosition(MysqlConnection mysqlConnection) {
    try {
      ResultSetPacket packet = mysqlConnection.query("show master status");
      List<String> fields = packet.getFieldValues();
      if (CollectionUtils.isEmpty(fields)) {
        throw new CanalParseException("command : \'show master status\' has an error! pls check. you need (at least one of) the SUPER,REPLICATION CLIENT privilege(s) for this operation");
      }
      EntryPosition endPosition = new EntryPosition(fields.get(0), Long.valueOf(fields.get(1)));
      if (isGTIDMode && fields.size() > 4) {
        endPosition.setGtid(fields.get(4));
      }
      return endPosition;
    } catch (IOException e) {
      throw new CanalParseException("command : \'show master status\' has an error!", e);
    }
  }

  private EntryPosition findStartPosition(MysqlConnection mysqlConnection) {
    try {
      ResultSetPacket packet = mysqlConnection.query("show binlog events limit 1");
      List<String> fields = packet.getFieldValues();
      if (CollectionUtils.isEmpty(fields)) {
        throw new CanalParseException("command : \'show binlog events limit 1\' has an error! pls check. you need (at least one of) the SUPER,REPLICATION CLIENT privilege(s) for this operation");
      }
      EntryPosition endPosition = new EntryPosition(fields.get(0), Long.valueOf(fields.get(1)));
      return endPosition;
    } catch (IOException e) {
      throw new CanalParseException("command : \'show binlog events limit 1\' has an error!", e);
    }
  }

  @SuppressWarnings(value = { "unused" }) private SlaveEntryPosition findSlavePosition(MysqlConnection mysqlConnection) {
    try {
      ResultSetPacket packet = mysqlConnection.query("show slave status");
      List<FieldPacket> names = packet.getFieldDescriptors();
      List<String> fields = packet.getFieldValues();
      if (CollectionUtils.isEmpty(fields)) {
        return null;
      }
      int i = 0;
      Map<String, String> maps = new HashMap<String, String>(names.size(), 1f);
      for (FieldPacket name : names) {
        maps.put(name.getName(), fields.get(i));
        i++;
      }
      String errno = maps.get("Last_Errno");
      String slaveIORunning = maps.get("Slave_IO_Running");
      String slaveSQLRunning = maps.get("Slave_SQL_Running");
      if ((!"0".equals(errno)) || (!"Yes".equalsIgnoreCase(slaveIORunning)) || (!"Yes".equalsIgnoreCase(slaveSQLRunning))) {
        logger.warn("Ignoring failed slave: " + mysqlConnection.getConnector().getAddress() + ", Last_Errno = " + errno + ", Slave_IO_Running = " + slaveIORunning + ", Slave_SQL_Running = " + slaveSQLRunning);
        return null;
      }
      String masterHost = maps.get("Master_Host");
      String masterPort = maps.get("Master_Port");
      String binlog = maps.get("Master_Log_File");
      String position = maps.get("Exec_Master_Log_Pos");
      return new SlaveEntryPosition(binlog, Long.valueOf(position), masterHost, masterPort);
    } catch (IOException e) {
      logger.error("find slave position error", e);
    }
    return null;
  }

  private EntryPosition findAsPerTimestampInSpecificLogFile(MysqlConnection mysqlConnection, final Long startTimestamp, final EntryPosition endPosition, final String searchBinlogFile, final Boolean justForPositionTimestamp) {
    final LogPosition logPosition = new LogPosition();
    try {
      mysqlConnection.reconnect();
      mysqlConnection.seek(searchBinlogFile, 4L, new SinkFunction<LogEvent>() {
        private LogPosition lastPosition;

        public boolean sink(LogEvent event) {
          EntryPosition entryPosition = null;
          try {
            CanalEntry.Entry entry = parseAndProfilingIfNecessary(event, true);
            if (justForPositionTimestamp && logPosition.getPostion() == null && event.getWhen() > 0) {
              entryPosition = new EntryPosition(searchBinlogFile, event.getLogPos(), event.getWhen() * 1000, event.getServerId());
              logPosition.setPostion(entryPosition);
            }
            if (entry == null) {
              return true;
            }
            String logfilename = entry.getHeader().getLogfileName();
            Long logfileoffset = entry.getHeader().getLogfileOffset();
            Long logposTimestamp = entry.getHeader().getExecuteTime();
            Long serverId = entry.getHeader().getServerId();
            if (CanalEntry.EntryType.TRANSACTIONBEGIN.equals(entry.getEntryType()) || CanalEntry.EntryType.TRANSACTIONEND.equals(entry.getEntryType())) {
              if (logger.isDebugEnabled()) {
                logger.debug("compare exit condition:{},{},{}, startTimestamp={}...", new Object[] { logfilename, logfileoffset, logposTimestamp, startTimestamp });
              }
              if (logposTimestamp >= startTimestamp) {
                return false;
              }
            }
            if (StringUtils.equals(endPosition.getJournalName(), logfilename) && endPosition.getPosition() <= logfileoffset) {
              return false;
            }
            if (CanalEntry.EntryType.TRANSACTIONEND.equals(entry.getEntryType())) {
              entryPosition = new EntryPosition(logfilename, logfileoffset, logposTimestamp, serverId);
              if (logger.isDebugEnabled()) {
                logger.debug("set {} to be pending start position before finding another proper one...", entryPosition);
              }
              logPosition.setPostion(entryPosition);
            } else {
              if (CanalEntry.EntryType.TRANSACTIONBEGIN.equals(entry.getEntryType())) {
                entryPosition = new EntryPosition(logfilename, logfileoffset, logposTimestamp, serverId);
                if (logger.isDebugEnabled()) {
                  logger.debug("set {} to be pending start position before finding another proper one...", entryPosition);
                }
                logPosition.setPostion(entryPosition);
              }
            }
            lastPosition = buildLastPosition(entry);
          } catch (Throwable e) {
            processSinkError(e, lastPosition, searchBinlogFile, 4L);
          }
          return running;
        }
      });
    } catch (IOException e) {
      logger.error("ERROR ## findAsPerTimestampInSpecificLogFile has an error", e);
    }
    if (logPosition.getPostion() != null) {
      return logPosition.getPostion();
    } else {
      return null;
    }
  }

  @Override protected void processDumpError(Throwable e) {
    if (e instanceof IOException) {
      String message = e.getMessage();
      if (StringUtils.contains(message, "errno = 1236")) {
        dumpErrorCount++;
      }
    }
    super.processDumpError(e);
  }

  public void setSupportBinlogFormats(String formatStrs) {
    String[] formats = StringUtils.split(formatStrs, ',');
    if (formats != null) {
      BinlogFormat[] supportBinlogFormats = new BinlogFormat[formats.length];
      int i = 0;
      for (String format : formats) {
        supportBinlogFormats[i++] = BinlogFormat.valuesOf(format);
      }
      this.supportBinlogFormats = supportBinlogFormats;
    }
  }

  public void setSupportBinlogImages(String imageStrs) {
    String[] images = StringUtils.split(imageStrs, ',');
    if (images != null) {
      BinlogImage[] supportBinlogImages = new BinlogImage[images.length];
      int i = 0;
      for (String image : images) {
        supportBinlogImages[i++] = BinlogImage.valuesOf(image);
      }
      this.supportBinlogImages = supportBinlogImages;
    }
  }

  public void setDefaultConnectionTimeoutInSeconds(int defaultConnectionTimeoutInSeconds) {
    this.defaultConnectionTimeoutInSeconds = defaultConnectionTimeoutInSeconds;
  }

  public void setReceiveBufferSize(int receiveBufferSize) {
    this.receiveBufferSize = receiveBufferSize;
  }

  public void setSendBufferSize(int sendBufferSize) {
    this.sendBufferSize = sendBufferSize;
  }

  public void setMasterInfo(AuthenticationInfo masterInfo) {
    this.masterInfo = masterInfo;
  }

  public void setStandbyInfo(AuthenticationInfo standbyInfo) {
    this.standbyInfo = standbyInfo;
  }

  public void setMasterPosition(EntryPosition masterPosition) {
    this.masterPosition = masterPosition;
  }

  public void setStandbyPosition(EntryPosition standbyPosition) {
    this.standbyPosition = standbyPosition;
  }

  public void setSlaveId(long slaveId) {
    this.slaveId = slaveId;
  }

  public void setDetectingSQL(String detectingSQL) {
    this.detectingSQL = detectingSQL;
  }

  public void setDetectingIntervalInSeconds(Integer detectingIntervalInSeconds) {
    this.detectingIntervalInSeconds = detectingIntervalInSeconds;
  }

  public void setDetectingEnable(boolean detectingEnable) {
    this.detectingEnable = detectingEnable;
  }

  public void setFallbackIntervalInSeconds(int fallbackIntervalInSeconds) {
    this.fallbackIntervalInSeconds = fallbackIntervalInSeconds;
  }

  public CanalHAController getHaController() {
    return haController;
  }

  public void setHaController(CanalHAController haController) {
    this.haController = haController;
  }

  public void setDumpErrorCountThreshold(int dumpErrorCountThreshold) {
    this.dumpErrorCountThreshold = dumpErrorCountThreshold;
  }

  public TableMetaStorageFactory getTableMetaStorageFactory() {
    return tableMetaStorageFactory;
  }

  public void setTableMetaStorageFactory(TableMetaStorageFactory tableMetaStorageFactory) {
    this.tableMetaStorageFactory = tableMetaStorageFactory;
  }
}