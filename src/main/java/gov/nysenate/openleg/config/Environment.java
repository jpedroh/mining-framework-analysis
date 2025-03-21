package gov.nysenate.openleg.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * The Environment class contains various configuration options to be used throughout the application.
 * This class is mutable during runtime so that hot config changes can be made to the fields here whereas
 * the property file is only checked during initialization.
 *
 * The fields in this class are primarily wired directly to values in the property file. Many fields
 * have setters to allow for changes while the application is running.
 */
@Component public class Environment {
  /** The database schema where the legislative data is stored. */
  @Value(value = "${env.schema:master}") private String schema;

  /** The root directory url where all data files are contained within. */
  @Value(value = "${env.base}") private String envDirPath;

  /** The directory url where all incoming data files are contained. */
  @Value(value = "${env.staging}") private String stagingDirPath;

  /** The directory url where all archived data files are contained. */
  @Value(value = "${env.archive}") private String archiveDirPath;

  private File baseDir;

  private File stagingDir;

  private File archiveDir;

  private File scrapedStagingDir;

  private LocalDateTime deployedDateTime;

  /** A secret key used to allow access to the API through the front-end. */
  @Value(value = "${api.secret}") private String apiSecret;

  /** --- Admin Auth --- */
  @Value(value = "${default.admin.user}") private String defaultAdminName;

  @Value(value = "${default.admin.password}") private String defaultAdminPass;

  /** Allow elastic search to index documents. */
  @Value(value = "${elastic.search.enabled}") private boolean elasticIndexing;

  /** Enable processing of data. */
  @Value(value = "${sobi.process.enabled:true}") private boolean sobiProcessEnabled;

  @Value(value = "${data.process.enabled}") private boolean processingEnabled;

  /** Allows for the option to enable/disable logging. */
  @Value(value = "${data.process.log.enabled}") private boolean processLoggingEnabled;

  /** Enable batch processing of SOBI files. */
  @Value(value = "${sobi.batch.process.enabled}") private boolean sobiBatchEnabled;

  /** If SOBI batch is enabled, this specifies the maximum batch size. */
  @Value(value = "${sobi.batch.process.size}") private int sobiBatchSize;

  /** Enable processing of data at scheduled intervals. */
  @Value(value = "${scheduler.process.enabled}") private boolean processingScheduled;

  /** Enable spot-check report runs at scheduled intervals. */
  @Value(value = "${scheduler.spotcheck.enabled}") private boolean spotcheckScheduled;

  /** --- Spotcheck Settings --- */
  @Value(value = "${spotcheck.alert.grace.period}") private int rawAlertGracePeriod;

  private Duration spotcheckAlertGracePeriod;

  /** Allows bills to be automatically added to the scrape queue if true */
  @Value(value = "${bill.scrape.queue.enabled}") private boolean billScrapeQueueEnabled;

  /** Enables periodic checking for email spotcheck references */
  @Value(value = "${spotcheck.checkmail.enabled:true}") private boolean checkmailEnabled;

  /** ---- Openleg Reference ---*/
  @Value(value = 
<<<<<<< /usr/src/app/output/nysenate/openlegislation/9cc1c656afa6aa6ec7e251dec2955385ab748879/src/main/java/gov/nysenate/openleg/config/Environment.java/left.java
  "${spotcheck.openleg_ref.api.key}"
=======
  "${mail.smtp.from}"
>>>>>>> /usr/src/app/output/nysenate/openlegislation/9cc1c656afa6aa6ec7e251dec2955385ab748879/src/main/java/gov/nysenate/openleg/config/Environment.java/right.java
  ) private String 
<<<<<<< /usr/src/app/output/nysenate/openlegislation/9cc1c656afa6aa6ec7e251dec2955385ab748879/src/main/java/gov/nysenate/openleg/config/Environment.java/left.java
  openlegRefApiKey
=======
  emailFromAddress
>>>>>>> /usr/src/app/output/nysenate/openlegislation/9cc1c656afa6aa6ec7e251dec2955385ab748879/src/main/java/gov/nysenate/openleg/config/Environment.java/right.java
  ;

  /** Sets queue sizes for nysenate.gov bill report */
  @Value(value = "${spotcheck.website.bill.ref_queue_size:500}") private int sensiteBillRefQueueSize;

  @Value(value = "${spotcheck.openleg_ref.url}") private String openlegRefUrl;

  /** Sets queue sizes for nysenate.gov bill report */
  @Value(value = "${spotcheck.website.bill.data_queue_size:500}") private int sensiteBillDataQueueSize;

  /** Imaps host, username, and password for the application's email account*/
  @Value(value = "${checkmail.host}") private String emailHost;

  @Value(value = "${checkmail.user}") private String emailUser;

  @Value(value = "${checkmail.pass}") private String emailPass;

  /** Incoming emails are stored in the receiving folder and archived in the processed folder */
  @Value(value = "${checkmail.receiving}") private String emailReceivingFolder;

  @Value(value = "${checkmail.processed}") private String emailProcessedFolder;

  /** --- Notifications --- */
  @Value(value = "${notifications.enabled}") private boolean notificationsEnabled;

  @Value(value = "${slack.notification.line.limit}") private int slackLineLimit;

  /** The domain and the context path of the application */
  @Value(value = "${domain.url}") private String url;

  /** The base url of the NYSenate.gov public website */
  @Value(value = "${nysenate.gov.url:https://www.NYSenate.gov}") private String senSiteUrl;

  /** --- Constructors --- */
  public Environment() {
  }

  @PostConstruct private void init() {
    deployedDateTime = LocalDateTime.now();
    this.baseDir = new File(envDirPath);
    this.stagingDir = new File(stagingDirPath);
    this.archiveDir = new File(archiveDirPath);
    this.scrapedStagingDir = new File(stagingDir, "scraped");
    this.spotcheckAlertGracePeriod = Duration.ofMinutes(rawAlertGracePeriod);
  }

  /** --- Basic Getters/Setters --- */
  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public File getBaseDir() {
    return baseDir;
  }

  public File getStagingDir() {
    return stagingDir;
  }

  public File getArchiveDir() {
    return archiveDir;
  }

  public boolean isElasticIndexing() {
    return elasticIndexing;
  }

  public void setElasticIndexing(boolean elasticIndexing) {
    this.elasticIndexing = elasticIndexing;
  }

  public boolean isProcessingEnabled() {
    return processingEnabled;
  }

  public void setProcessingEnabled(boolean processingEnabled) {
    this.processingEnabled = processingEnabled;
  }

  public boolean isProcessLoggingEnabled() {
    return processLoggingEnabled;
  }

  public void setProcessLoggingEnabled(boolean processLoggingEnabled) {
    this.processLoggingEnabled = processLoggingEnabled;
  }

  public boolean isSobiBatchEnabled() {
    return sobiBatchEnabled;
  }

  public void setSobiBatchEnabled(boolean sobiBatchEnabled) {
    this.sobiBatchEnabled = sobiBatchEnabled;
  }

  public int getSobiBatchSize() {
    return sobiBatchSize;
  }

  public void setSobiBatchSize(int sobiBatchSize) {
    this.sobiBatchSize = sobiBatchSize;
  }

  public String getApiSecret() {
    return apiSecret;
  }

  public boolean isProcessingScheduled() {
    return processingScheduled;
  }

  public void setProcessingScheduled(boolean processingScheduled) {
    this.processingScheduled = processingScheduled;
  }

  public boolean isSpotcheckScheduled() {
    return spotcheckScheduled;
  }

  public void setSpotcheckScheduled(boolean spotcheckScheduled) {
    this.spotcheckScheduled = spotcheckScheduled;
  }

  public boolean isNotificationsEnabled() {
    return notificationsEnabled;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getSlackLineLimit() {
    return slackLineLimit;
  }

  public void setSlackLineLimit(int slackLineLimit) {
    this.slackLineLimit = slackLineLimit;
  }

  public void setNotificationsEnabled(boolean notificationsEnabled) {
    this.notificationsEnabled = notificationsEnabled;
  }

  public void setBaseDir(File baseDir) {
    this.baseDir = baseDir;
  }

  public void setStagingDir(File stagingDir) {
    this.stagingDir = stagingDir;
  }

  public void setArchiveDir(File archiveDir) {
    this.archiveDir = archiveDir;
  }

  public void setApiSecret(String apiSecret) {
    this.apiSecret = apiSecret;
  }

  public String getEmailHost() {
    return emailHost;
  }

  public void setEmailHost(String emailHost) {
    this.emailHost = emailHost;
  }

  public String getEmailUser() {
    return emailUser;
  }

  public void setEmailUser(String emailUser) {
    this.emailUser = emailUser;
  }

  public String getEmailPass() {
    return emailPass;
  }

  public void setEmailPass(String emailPass) {
    this.emailPass = emailPass;
  }

  public String getEmailReceivingFolder() {
    return emailReceivingFolder;
  }

  public void setEmailReceivingFolder(String emailReceivingFolder) {
    this.emailReceivingFolder = emailReceivingFolder;
  }

  public String getEmailProcessedFolder() {
    return emailProcessedFolder;
  }

  public void setEmailProcessedFolder(String emailProcessedFolder) {
    this.emailProcessedFolder = emailProcessedFolder;
  }

  public Duration getSpotcheckAlertGracePeriod() {
    return spotcheckAlertGracePeriod;
  }

  public void setSpotcheckAlertGracePeriod(Duration spotcheckAlertGracePeriod) {
    this.spotcheckAlertGracePeriod = spotcheckAlertGracePeriod;
  }

  public File getScrapedStagingDir() {
    return scrapedStagingDir;
  }

  public void setScrapedStagingDir(File scrapedStagingDir) {
    this.scrapedStagingDir = scrapedStagingDir;
  }

  public String getDefaultAdminName() {
    return defaultAdminName;
  }

  public void setDefaultAdminName(String defaultAdminName) {
    this.defaultAdminName = defaultAdminName;
  }

  public String getDefaultAdminPass() {
    return defaultAdminPass;
  }

  public void setDefaultAdminPass(String defaultAdminPass) {
    this.defaultAdminPass = defaultAdminPass;
  }

  public boolean isBillScrapeQueueEnabled() {
    return billScrapeQueueEnabled;
  }

  public void setBillScrapeQueueEnabled(boolean billScrapeQueueEnabled) {
    this.billScrapeQueueEnabled = billScrapeQueueEnabled;
  }

  public LocalDateTime getDeployedDateTime() {
    return deployedDateTime;
  }

  public boolean getSobiProcessEnabled() {
    return sobiProcessEnabled;
  }

  public String getOpenlegRefApiKey() {
    return openlegRefApiKey;
  }

  public void setOpenlegRefApiKey(String refApiKey) {
    this.openlegRefApiKey = refApiKey;
  }

  public String getOpenlegRefUrl() {
    return openlegRefUrl;
  }

  public void setOpenlegRefUrl(String refUrl) {
    this.openlegRefUrl = refUrl;
  }

  public String getSenSiteUrl() {
    return senSiteUrl;
  }

  public void setSenSiteUrl(String senSiteUrl) {
    this.senSiteUrl = senSiteUrl;
  }

  public boolean isCheckmailEnabled() {
    return checkmailEnabled;
  }

  public void setCheckmailEnabled(boolean checkmailEnabled) {
    this.checkmailEnabled = checkmailEnabled;
  }

  public int getSensiteBillRefQueueSize() {
    return sensiteBillRefQueueSize;
  }

  public void setSensiteBillRefQueueSize(int sensiteBillRefQueueSize) {
    this.sensiteBillRefQueueSize = sensiteBillRefQueueSize;
  }

  public int getSensiteBillDataQueueSize() {
    return sensiteBillDataQueueSize;
  }

  public void setSensiteBillDataQueueSize(int sensiteBillDataQueueSize) {
    this.sensiteBillDataQueueSize = sensiteBillDataQueueSize;
  }

  public String getEmailFromAddress() {
    return emailFromAddress;
  }

  public void setEmailFromAddress(String emailFromAddress) {
    this.emailFromAddress = emailFromAddress;
  }
}