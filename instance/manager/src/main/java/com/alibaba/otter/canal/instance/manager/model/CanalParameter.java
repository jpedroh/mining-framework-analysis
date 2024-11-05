package com.alibaba.otter.canal.instance.manager.model;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.otter.canal.parse.inbound.mysql.tablemeta.TableMetaStorageFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.alibaba.otter.canal.common.utils.CanalToStringStyle;

public class CanalParameter implements Serializable {
  private static final long serialVersionUID = -5893459662315430900L;

  private Long canalId;

  private RunMode runMode = RunMode.EMBEDDED;

  private ClusterMode clusterMode = ClusterMode.STANDALONE;

  private Long zkClusterId;

  private List<String> zkClusters;

  private MetaMode metaMode = MetaMode.MEMORY;

  private Integer transactionSize = 1024;

  private StorageMode storageMode = StorageMode.MEMORY;

  private BatchMode storageBatchMode = BatchMode.MEMSIZE;

  private Integer memoryStorageBufferSize = 16 * 1024;

  private Integer memoryStorageBufferMemUnit = 1024;

  private String fileStorageDirectory;

  private Integer fileStorageStoreCount;

  private Integer fileStorageRollverCount;

  private Integer fileStoragePercentThresold;

  private StorageScavengeMode storageScavengeMode = StorageScavengeMode.ON_ACK;

  private String scavengeSchdule;

  private SourcingType sourcingType = SourcingType.MYSQL;

  private String localBinlogDirectory;

  private HAMode haMode = HAMode.HEARTBEAT;

  private Integer port = 11111;

  private Integer defaultConnectionTimeoutInSeconds = 30;

  private Integer receiveBufferSize = 64 * 1024;

  private Integer sendBufferSize = 64 * 1024;

  private Byte connectionCharsetNumber = (byte) 33;

  private String connectionCharset = "UTF-8";

  private List<InetSocketAddress> dbAddresses;

  private List<List<DataSourcing>> groupDbAddresses;

  private String dbUsername;

  private String dbPassword;

  private IndexMode indexMode;

  private List<String> positions;

  private String defaultDatabaseName;

  private Long slaveId;

  private Integer fallbackIntervalInSeconds = 60;

  private Boolean detectingEnable = true;

  private Boolean heartbeatHaEnable = false;

  private String detectingSQL;

  private Integer detectingIntervalInSeconds = 3;

  private Integer detectingTimeoutThresholdInSeconds = 30;

  private Integer detectingRetryTimes = 3;

  private String app;

  private String group;

  private String mediaGroup;

  private String metaqStoreUri;

  private Boolean ddlIsolation = Boolean.FALSE;

  private Boolean filterTableError = Boolean.FALSE;

  private String blackFilter = null;

  private InetSocketAddress masterAddress;

  private String masterUsername;

  private String masterPassword;

  private InetSocketAddress standbyAddress;

  private String standbyUsername;

  private String standbyPassword;

  private String masterLogfileName = null;

  private Long masterLogfileOffest = null;

  private Long masterTimestamp = null;

  private String standbyLogfileName = null;

  private Long standbyLogfileOffest = null;

  private Long standbyTimestamp = null;

  TableMetaStorageFactory tableMetaStorageFactory;

  public static enum RunMode {
    EMBEDDED,
    SERVICE
    ;

    public boolean isEmbedded() {
      return this.equals(RunMode.EMBEDDED);
    }

    public boolean isService() {
      return this.equals(RunMode.SERVICE);
    }
  }

  public static enum ClusterMode {
    STANDALONE,
    STANDBY,
    ACTIVE
    ;

    public boolean isStandalone() {
      return this.equals(ClusterMode.STANDALONE);
    }

    public boolean isStandby() {
      return this.equals(ClusterMode.STANDBY);
    }

    public boolean isActive() {
      return this.equals(ClusterMode.ACTIVE);
    }
  }

  public static enum HAMode {
    HEARTBEAT,
    MEDIA
    ;

    public boolean isHeartBeat() {
      return this.equals(HAMode.HEARTBEAT);
    }

    public boolean isMedia() {
      return this.equals(HAMode.MEDIA);
    }
  }

  public static enum StorageMode {
    MEMORY,
    FILE,
    MIXED
    ;

    public boolean isMemory() {
      return this.equals(StorageMode.MEMORY);
    }

    public boolean isFile() {
      return this.equals(StorageMode.FILE);
    }

    public boolean isMixed() {
      return this.equals(StorageMode.MIXED);
    }
  }

  public static enum StorageScavengeMode {
    ON_FULL,
    ON_ACK,
    ON_SCHEDULE,
    NO_OP
    ;

    public boolean isOnFull() {
      return this.equals(StorageScavengeMode.ON_FULL);
    }

    public boolean isOnAck() {
      return this.equals(StorageScavengeMode.ON_ACK);
    }

    public boolean isOnSchedule() {
      return this.equals(StorageScavengeMode.ON_SCHEDULE);
    }

    public boolean isNoop() {
      return this.equals(StorageScavengeMode.NO_OP);
    }
  }

  public static enum SourcingType {
    MYSQL,
    LOCALBINLOG,
    ORACLE,
    GROUP
    ;

    public boolean isMysql() {
      return this.equals(SourcingType.MYSQL);
    }

    public boolean isLocalBinlog() {
      return this.equals(SourcingType.LOCALBINLOG);
    }

    public boolean isOracle() {
      return this.equals(SourcingType.ORACLE);
    }

    public boolean isGroup() {
      return this.equals(SourcingType.GROUP);
    }
  }

  public static enum MetaMode {
    MEMORY,
    ZOOKEEPER,
    MIXED,
    LOCAL_FILE
    ;

    public boolean isMemory() {
      return this.equals(MetaMode.MEMORY);
    }

    public boolean isZookeeper() {
      return this.equals(MetaMode.ZOOKEEPER);
    }

    public boolean isMixed() {
      return this.equals(MetaMode.MIXED);
    }

    public boolean isLocalFile() {
      return this.equals(MetaMode.LOCAL_FILE);
    }
  }

  public static enum IndexMode {
    MEMORY,
    ZOOKEEPER,
    MIXED,
    META,
    MEMORY_META_FAILBACK
    ;

    public boolean isMemory() {
      return this.equals(IndexMode.MEMORY);
    }

    public boolean isZookeeper() {
      return this.equals(IndexMode.ZOOKEEPER);
    }

    public boolean isMixed() {
      return this.equals(IndexMode.MIXED);
    }

    public boolean isMeta() {
      return this.equals(IndexMode.META);
    }

    public boolean isMemoryMetaFailback() {
      return this.equals(IndexMode.MEMORY_META_FAILBACK);
    }
  }

  public static enum BatchMode {
    ITEMSIZE,
    MEMSIZE
    ;

    public boolean isItemSize() {
      return this == BatchMode.ITEMSIZE;
    }

    public boolean isMemSize() {
      return this == BatchMode.MEMSIZE;
    }
  }

  public static class DataSourcing implements Serializable {
    private static final long serialVersionUID = -1770648468678085234L;

    private SourcingType type;

    private InetSocketAddress dbAddress;

    public DataSourcing() {
    }

    public DataSourcing(SourcingType type, InetSocketAddress dbAddress) {
      this.type = type;
      this.dbAddress = dbAddress;
    }

    public SourcingType getType() {
      return type;
    }

    public void setType(SourcingType type) {
      this.type = type;
    }

    public InetSocketAddress getDbAddress() {
      return dbAddress;
    }

    public void setDbAddress(InetSocketAddress dbAddress) {
      this.dbAddress = dbAddress;
    }
  }

  public Long getCanalId() {
    return canalId;
  }

  public void setCanalId(Long canalId) {
    this.canalId = canalId;
  }

  public RunMode getRunMode() {
    return runMode;
  }

  public void setRunMode(RunMode runMode) {
    this.runMode = runMode;
  }

  public ClusterMode getClusterMode() {
    return clusterMode;
  }

  public void setClusterMode(ClusterMode clusterMode) {
    this.clusterMode = clusterMode;
  }

  public List<String> getZkClusters() {
    return zkClusters;
  }

  public void setZkClusters(List<String> zkClusters) {
    this.zkClusters = zkClusters;
  }

  public MetaMode getMetaMode() {
    return metaMode;
  }

  public void setMetaMode(MetaMode metaMode) {
    this.metaMode = metaMode;
  }

  public StorageMode getStorageMode() {
    return storageMode;
  }

  public void setStorageMode(StorageMode storageMode) {
    this.storageMode = storageMode;
  }

  public Integer getMemoryStorageBufferSize() {
    return memoryStorageBufferSize;
  }

  public void setMemoryStorageBufferSize(Integer memoryStorageBufferSize) {
    this.memoryStorageBufferSize = memoryStorageBufferSize;
  }

  public String getFileStorageDirectory() {
    return fileStorageDirectory;
  }

  public void setFileStorageDirectory(String fileStorageDirectory) {
    this.fileStorageDirectory = fileStorageDirectory;
  }

  public Integer getFileStorageStoreCount() {
    return fileStorageStoreCount;
  }

  public void setFileStorageStoreCount(Integer fileStorageStoreCount) {
    this.fileStorageStoreCount = fileStorageStoreCount;
  }

  public Integer getFileStorageRollverCount() {
    return fileStorageRollverCount;
  }

  public void setFileStorageRollverCount(Integer fileStorageRollverCount) {
    this.fileStorageRollverCount = fileStorageRollverCount;
  }

  public Integer getFileStoragePercentThresold() {
    return fileStoragePercentThresold;
  }

  public void setFileStoragePercentThresold(Integer fileStoragePercentThresold) {
    this.fileStoragePercentThresold = fileStoragePercentThresold;
  }

  public SourcingType getSourcingType() {
    return sourcingType;
  }

  public void setSourcingType(SourcingType sourcingType) {
    this.sourcingType = sourcingType;
  }

  public String getLocalBinlogDirectory() {
    return localBinlogDirectory;
  }

  public void setLocalBinlogDirectory(String localBinlogDirectory) {
    this.localBinlogDirectory = localBinlogDirectory;
  }

  public HAMode getHaMode() {
    return haMode;
  }

  public void setHaMode(HAMode haMode) {
    this.haMode = haMode;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public Integer getDefaultConnectionTimeoutInSeconds() {
    return defaultConnectionTimeoutInSeconds;
  }

  public void setDefaultConnectionTimeoutInSeconds(Integer defaultConnectionTimeoutInSeconds) {
    this.defaultConnectionTimeoutInSeconds = defaultConnectionTimeoutInSeconds;
  }

  public Integer getReceiveBufferSize() {
    return receiveBufferSize;
  }

  public void setReceiveBufferSize(Integer receiveBufferSize) {
    this.receiveBufferSize = receiveBufferSize;
  }

  public Integer getSendBufferSize() {
    return sendBufferSize;
  }

  public void setSendBufferSize(Integer sendBufferSize) {
    this.sendBufferSize = sendBufferSize;
  }

  public Byte getConnectionCharsetNumber() {
    return connectionCharsetNumber;
  }

  public void setConnectionCharsetNumber(Byte connectionCharsetNumber) {
    this.connectionCharsetNumber = connectionCharsetNumber;
  }

  public String getConnectionCharset() {
    return connectionCharset;
  }

  public void setConnectionCharset(String connectionCharset) {
    this.connectionCharset = connectionCharset;
  }

  public IndexMode getIndexMode() {
    return indexMode;
  }

  public void setIndexMode(IndexMode indexMode) {
    this.indexMode = indexMode;
  }

  public String getDefaultDatabaseName() {
    return defaultDatabaseName;
  }

  public void setDefaultDatabaseName(String defaultDatabaseName) {
    this.defaultDatabaseName = defaultDatabaseName;
  }

  public Long getSlaveId() {
    return slaveId;
  }

  public void setSlaveId(Long slaveId) {
    this.slaveId = slaveId;
  }

  public Boolean getDetectingEnable() {
    return detectingEnable;
  }

  public void setDetectingEnable(Boolean detectingEnable) {
    this.detectingEnable = detectingEnable;
  }

  public String getDetectingSQL() {
    return detectingSQL;
  }

  public void setDetectingSQL(String detectingSQL) {
    this.detectingSQL = detectingSQL;
  }

  public Integer getDetectingIntervalInSeconds() {
    return detectingIntervalInSeconds;
  }

  public void setDetectingIntervalInSeconds(Integer detectingIntervalInSeconds) {
    this.detectingIntervalInSeconds = detectingIntervalInSeconds;
  }

  public Integer getDetectingTimeoutThresholdInSeconds() {
    return detectingTimeoutThresholdInSeconds;
  }

  public void setDetectingTimeoutThresholdInSeconds(Integer detectingTimeoutThresholdInSeconds) {
    this.detectingTimeoutThresholdInSeconds = detectingTimeoutThresholdInSeconds;
  }

  public Integer getDetectingRetryTimes() {
    return detectingRetryTimes;
  }

  public void setDetectingRetryTimes(Integer detectingRetryTimes) {
    this.detectingRetryTimes = detectingRetryTimes;
  }

  public StorageScavengeMode getStorageScavengeMode() {
    return storageScavengeMode;
  }

  public void setStorageScavengeMode(StorageScavengeMode storageScavengeMode) {
    this.storageScavengeMode = storageScavengeMode;
  }

  public String getScavengeSchdule() {
    return scavengeSchdule;
  }

  public void setScavengeSchdule(String scavengeSchdule) {
    this.scavengeSchdule = scavengeSchdule;
  }

  public String getApp() {
    return app;
  }

  public String getGroup() {
    return group;
  }

  public void setApp(String app) {
    this.app = app;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getMetaqStoreUri() {
    return metaqStoreUri;
  }

  public void setMetaqStoreUri(String metaqStoreUri) {
    this.metaqStoreUri = metaqStoreUri;
  }

  public Integer getTransactionSize() {
    return transactionSize != null ? transactionSize : 1024;
  }

  public void setTransactionSize(Integer transactionSize) {
    this.transactionSize = transactionSize;
  }

  public List<InetSocketAddress> getDbAddresses() {
    if (dbAddresses == null) {
      dbAddresses = new ArrayList<InetSocketAddress>();
      if (masterAddress != null) {
        dbAddresses.add(masterAddress);
      }
      if (standbyAddress != null) {
        dbAddresses.add(standbyAddress);
      }
    }
    return dbAddresses;
  }

  public List<List<DataSourcing>> getGroupDbAddresses() {
    if (groupDbAddresses == null) {
      groupDbAddresses = new ArrayList<List<DataSourcing>>();
      if (dbAddresses != null) {
        for (InetSocketAddress address : dbAddresses) {
          List<DataSourcing> groupAddresses = new ArrayList<DataSourcing>();
          groupAddresses.add(new DataSourcing(sourcingType, address));
          groupDbAddresses.add(groupAddresses);
        }
      } else {
        if (masterAddress != null) {
          List<DataSourcing> groupAddresses = new ArrayList<DataSourcing>();
          groupAddresses.add(new DataSourcing(sourcingType, masterAddress));
          groupDbAddresses.add(groupAddresses);
        }
        if (standbyAddress != null) {
          List<DataSourcing> groupAddresses = new ArrayList<DataSourcing>();
          groupAddresses.add(new DataSourcing(sourcingType, standbyAddress));
          groupDbAddresses.add(groupAddresses);
        }
      }
    }
    return groupDbAddresses;
  }

  public void setGroupDbAddresses(List<List<DataSourcing>> groupDbAddresses) {
    this.groupDbAddresses = groupDbAddresses;
  }

  public void setDbAddresses(List<InetSocketAddress> dbAddresses) {
    this.dbAddresses = dbAddresses;
  }

  public String getDbUsername() {
    if (dbUsername == null) {
      dbUsername = (masterUsername != null ? masterUsername : standbyUsername);
    }
    return dbUsername;
  }

  public void setDbUsername(String dbUsername) {
    this.dbUsername = dbUsername;
  }

  public String getDbPassword() {
    if (dbPassword == null) {
      dbPassword = (masterPassword != null ? masterPassword : standbyPassword);
    }
    return dbPassword;
  }

  public void setDbPassword(String dbPassword) {
    this.dbPassword = dbPassword;
  }

  public List<String> getPositions() {
    if (positions == null) {
      positions = new ArrayList<String>();
      String masterPosition = buildPosition(masterLogfileName, masterLogfileOffest, masterTimestamp);
      if (masterPosition != null) {
        positions.add(masterPosition);
      }
      String standbyPosition = buildPosition(standbyLogfileName, standbyLogfileOffest, standbyTimestamp);
      if (standbyPosition != null) {
        positions.add(standbyPosition);
      }
    }
    return positions;
  }

  public void setPositions(List<String> positions) {
    this.positions = positions;
  }

  private String buildPosition(String journalName, Long position, Long timestamp) {
    StringBuilder masterBuilder = new StringBuilder();
    if (StringUtils.isNotEmpty(journalName) || position != null || timestamp != null) {
      masterBuilder.append('{');
      if (StringUtils.isNotEmpty(journalName)) {
        masterBuilder.append("\"journalName\":\"").append(journalName).append("\"");
      }
      if (position != null) {
        if (masterBuilder.length() > 1) {
          masterBuilder.append(",");
        }
        masterBuilder.append("\"position\":").append(position);
      }
      if (timestamp != null) {
        if (masterBuilder.length() > 1) {
          masterBuilder.append(",");
        }
        masterBuilder.append("\"timestamp\":").append(timestamp);
      }
      masterBuilder.append('}');
      return masterBuilder.toString();
    } else {
      return null;
    }
  }

  public void setMasterUsername(String masterUsername) {
    this.masterUsername = masterUsername;
  }

  public void setMasterPassword(String masterPassword) {
    this.masterPassword = masterPassword;
  }

  public void setStandbyAddress(InetSocketAddress standbyAddress) {
    this.standbyAddress = standbyAddress;
  }

  public void setStandbyUsername(String standbyUsername) {
    this.standbyUsername = standbyUsername;
  }

  public void setStandbyPassword(String standbyPassword) {
    this.standbyPassword = standbyPassword;
  }

  public void setMasterLogfileName(String masterLogfileName) {
    this.masterLogfileName = masterLogfileName;
  }

  public void setMasterLogfileOffest(Long masterLogfileOffest) {
    this.masterLogfileOffest = masterLogfileOffest;
  }

  public void setMasterTimestamp(Long masterTimestamp) {
    this.masterTimestamp = masterTimestamp;
  }

  public void setStandbyLogfileName(String standbyLogfileName) {
    this.standbyLogfileName = standbyLogfileName;
  }

  public void setStandbyLogfileOffest(Long standbyLogfileOffest) {
    this.standbyLogfileOffest = standbyLogfileOffest;
  }

  public void setStandbyTimestamp(Long standbyTimestamp) {
    this.standbyTimestamp = standbyTimestamp;
  }

  public void setMasterAddress(InetSocketAddress masterAddress) {
    this.masterAddress = masterAddress;
  }

  public Integer getFallbackIntervalInSeconds() {
    return fallbackIntervalInSeconds == null ? 60 : fallbackIntervalInSeconds;
  }

  public void setFallbackIntervalInSeconds(Integer fallbackIntervalInSeconds) {
    this.fallbackIntervalInSeconds = fallbackIntervalInSeconds;
  }

  public Boolean getHeartbeatHaEnable() {
    return heartbeatHaEnable == null ? false : heartbeatHaEnable;
  }

  public void setHeartbeatHaEnable(Boolean heartbeatHaEnable) {
    this.heartbeatHaEnable = heartbeatHaEnable;
  }

  public BatchMode getStorageBatchMode() {
    return storageBatchMode == null ? BatchMode.MEMSIZE : storageBatchMode;
  }

  public void setStorageBatchMode(BatchMode storageBatchMode) {
    this.storageBatchMode = storageBatchMode;
  }

  public Integer getMemoryStorageBufferMemUnit() {
    return memoryStorageBufferMemUnit == null ? 1024 : memoryStorageBufferMemUnit;
  }

  public void setMemoryStorageBufferMemUnit(Integer memoryStorageBufferMemUnit) {
    this.memoryStorageBufferMemUnit = memoryStorageBufferMemUnit;
  }

  public String getMediaGroup() {
    return mediaGroup;
  }

  public void setMediaGroup(String mediaGroup) {
    this.mediaGroup = mediaGroup;
  }

  public Long getZkClusterId() {
    return zkClusterId;
  }

  public void setZkClusterId(Long zkClusterId) {
    this.zkClusterId = zkClusterId;
  }

  public Boolean getDdlIsolation() {
    return ddlIsolation == null ? false : ddlIsolation;
  }

  public void setDdlIsolation(Boolean ddlIsolation) {
    this.ddlIsolation = ddlIsolation;
  }

  public Boolean getFilterTableError() {
    return filterTableError == null ? false : filterTableError;
  }

  public void setFilterTableError(Boolean filterTableError) {
    this.filterTableError = filterTableError;
  }

  public String getBlackFilter() {
    return blackFilter;
  }

  public void setBlackFilter(String blackFilter) {
    this.blackFilter = blackFilter;
  }

  public TableMetaStorageFactory getTableMetaStorageFactory() {
    return tableMetaStorageFactory;
  }

  public void setTableMetaStorageFactory(TableMetaStorageFactory tableMetaStorageFactory) {
    this.tableMetaStorageFactory = tableMetaStorageFactory;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this, CanalToStringStyle.DEFAULT_STYLE);
  }

  private String dataDir = "../conf";

  private Integer metaFileFlushPeriod = 1000;

  public String getDataDir() {
    return dataDir;
  }

  public void setDataDir(String dataDir) {
    this.dataDir = dataDir;
  }

  public Integer getMetaFileFlushPeriod() {
    return metaFileFlushPeriod;
  }

  public void setMetaFileFlushPeriod(Integer metaFileFlushPeriod) {
    this.metaFileFlushPeriod = metaFileFlushPeriod;
  }
}