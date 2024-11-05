package org.restheart;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.google.common.collect.Maps;
import com.mongodb.MongoClientURI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.restheart.handlers.RequestContext;
import org.restheart.handlers.RequestContext.ETAG_CHECK_POLICY;
import org.restheart.handlers.RequestContext.REPRESENTATION_FORMAT;
import org.restheart.utils.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class Configuration {
  public static final String RESTHEART_VERSION = Configuration.class.getPackage().getImplementationVersion() == null ? "unknown, not packaged" : Configuration.class.getPackage().getImplementationVersion();

  public static final String RESTHEART_ONLINE_DOC_URL = "http://restheart.org/curies/3.0";

  private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

  public static final String DEFAULT_MONGO_URI = "mongodb://127.0.0.1";

  public static final String DEFAULT_ROUTE = "0.0.0.0";

  public static final String DEFAULT_AJP_HOST = DEFAULT_ROUTE;

  public static final int DEFAULT_AJP_PORT = 8009;

  public static final String DEFAULT_HTTP_HOST = DEFAULT_ROUTE;

  public static final int DEFAULT_HTTP_PORT = 8080;

  public static final String DEFAULT_HTTPS_HOST = DEFAULT_ROUTE;

  public static final int DEFAULT_HTTPS_PORT = 4443;

  public static final String DEFAULT_INSTANCE_NAME = "default";

  public static final REPRESENTATION_FORMAT DEFAULT_REPRESENTATION_FORMAT = REPRESENTATION_FORMAT.PLAIN_JSON;

  public static final String DEFAULT_AM_IMPLEMENTATION_CLASS = null;

  public static final String DEFAULT_IDM_IMPLEMENTATION_CLASS = null;

  public static final String DEFAULT_AUTH_MECHANISM_IMPLEMENTATION_CLASS = null;

  public static final ETAG_CHECK_POLICY DEFAULT_DB_ETAG_CHECK_POLICY = ETAG_CHECK_POLICY.REQUIRED_FOR_DELETE;

  public static final ETAG_CHECK_POLICY DEFAULT_COLL_ETAG_CHECK_POLICY = ETAG_CHECK_POLICY.REQUIRED_FOR_DELETE;

  public static final ETAG_CHECK_POLICY DEFAULT_DOC_ETAG_CHECK_POLICY = ETAG_CHECK_POLICY.OPTIONAL;

  public static final int DEFAULT_MAX_DOC_ETAG_CHECK_POLICY = 1000;

  public static final int DEFAULT_MAX_PAGESIZE = 1000;

  public static final int DEFAULT_DEFAULT_PAGESIZE = 100;

  public static final int DEFAULT_CURSOR_BATCH_SIZE = 1000;

  public static final String LOCAL_CACHE_ENABLED_KEY = "local-cache-enabled";

  public static final String LOCAL_CACHE_TTL_KEY = "local-cache-ttl";

  public static final String SCHEMA_CACHE_ENABLED_KEY = "schema-cache-enabled";

  public static final String SCHEMA_CACHE_TTL_KEY = "schema-cache-ttl";

  public static final String FORCE_GZIP_ENCODING_KEY = "force-gzip-encoding";

  public static final String DIRECT_BUFFERS_KEY = "direct-buffers";

  public static final String BUFFERS_PER_REGION_KEY = "buffers-per-region";

  public static final String BUFFER_SIZE_KEY = "buffer-size";

  public static final String WORKER_THREADS_KEY = "worker-threads";

  public static final String IO_THREADS_KEY = "io-threads";

  public static final String REQUESTS_LIMIT_KEY = "requests-limit";

  public static final String QUERY_TIME_LIMIT_KEY = "query-time-limit";

  private static final String AGGREGATION_TIME_LIMIT_KEY = "aggregation-time-limit";

  public static final String AGGREGATION_CHECK_OPERATORS = "aggregation-check-operators";

  public static final String ENABLE_LOG_FILE_KEY = "enable-log-file";

  public static final String ENABLE_LOG_CONSOLE_KEY = "enable-log-console";

  public static final String LOG_LEVEL_KEY = "log-level";

  public static final String LOG_FILE_PATH_KEY = "log-file-path";

  public static final String IMPLEMENTATION_CLASS_KEY = "implementation-class";

  public static final String ACCESS_MANAGER_KEY = "access-manager";

  public static final String IDM_KEY = "idm";

  public static final String AUTH_MECHANISM_KEY = "auth-mechanism";

  public static final String MONGO_URI_KEY = "mongo-uri";

  public static final String MONGO_MOUNTS_KEY = "mongo-mounts";

  public static final String MONGO_MOUNT_WHAT_KEY = "what";

  public static final String MONGO_MOUNT_WHERE_KEY = "where";

  public static final String MONGO_AUTH_DB_KEY = "auth-db";

  public static final String MONGO_PASSWORD_KEY = "password";

  public static final String MONGO_USER_KEY = "user";

  public static final String APPLICATION_LOGIC_MOUNTS_KEY = "application-logic-mounts";

  public static final String METADATA_NAMED_SINGLETONS_KEY = "metadata-named-singletons";

  public static final String APPLICATION_LOGIC_MOUNT_ARGS_KEY = "args";

  public static final String APPLICATION_LOGIC_MOUNT_WHAT_KEY = "what";

  public static final String APPLICATION_LOGIC_MOUNT_WHERE_KEY = "where";

  public static final String APPLICATION_LOGIC_MOUNT_SECURED_KEY = "secured";

  public static final String STATIC_RESOURCES_MOUNTS_KEY = "static-resources-mounts";

  public static final String STATIC_RESOURCES_MOUNT_WHAT_KEY = "what";

  public static final String STATIC_RESOURCES_MOUNT_WHERE_KEY = "where";

  public static final String STATIC_RESOURCES_MOUNT_WELCOME_FILE_KEY = "welcome-file";

  public static final String STATIC_RESOURCES_MOUNT_EMBEDDED_KEY = "embedded";

  public static final String STATIC_RESOURCES_MOUNT_SECURED_KEY = "secured";

  public static final String CERT_PASSWORD_KEY = "certpassword";

  public static final String KEYSTORE_PASSWORD_KEY = "keystore-password";

  public static final String KEYSTORE_FILE_KEY = "keystore-file";

  public static final String USE_EMBEDDED_KEYSTORE_KEY = "use-embedded-keystore";

  public static final String AJP_HOST_KEY = "ajp-host";

  public static final String AJP_PORT_KEY = "ajp-port";

  public static final String AJP_LISTENER_KEY = "ajp-listener";

  public static final String HTTP_HOST_KEY = "http-host";

  public static final String HTTP_PORT_KEY = "http-port";

  public static final String HTTP_LISTENER_KEY = "http-listener";

  private static final String HTTPS_HOST_KEY = "https-host";

  private static final String HTTPS_PORT_KEY = "https-port";

  public static final String HTTPS_LISTENER = "https-listener";

  public static final String INSTANCE_NAME_KEY = "instance-name";

  public static final String REPRESENTATION_FORMAT_KEY = "default-representation-format";

  public static final String EAGER_POOL_SIZE = "eager-cursor-allocation-pool-size";

  public static final String EAGER_LINEAR_SLICE_WIDHT = "eager-cursor-allocation-linear-slice-width";

  public static final String EAGER_LINEAR_SLICE_DELTA = "eager-cursor-allocation-linear-slice-delta";

  public static final String EAGER_LINEAR_HEIGHTS = "eager-cursor-allocation-linear-slice-heights";

  public static final String EAGER_RND_SLICE_MIN_WIDHT = "eager-cursor-allocation-random-slice-min-width";

  public static final String EAGER_RND_MAX_CURSORS = "eager-cursor-allocation-random-max-cursors";

  public static final String AUTH_TOKEN_ENABLED = "auth-token-enabled";

  public static final String AUTH_TOKEN_TTL = "auth-token-ttl";

  public static final String ETAG_CHECK_POLICY_KEY = "etag-check-policy";

  public static final String ETAG_CHECK_POLICY_DB_KEY = "db";

  public static final String ETAG_CHECK_POLICY_COLL_KEY = "coll";

  public static final String ETAG_CHECK_POLICY_DOC_KEY = "doc";

  public static final String LOG_REQUESTS_LEVEL_KEY = "requests-log-level";

  public static final String METRICS_GATHERING_LEVEL_KEY = "metrics-gathering-level";

  public static final String ANSI_CONSOLE_KEY = "ansi-console";

  public static final String INITIALIZER_CLASS_KEY = "initializer-class";

  public static final String MAX_PAGESIZE_KEY = "max-pagesize";

  public static final String DEFAULT_PAGESIZE_KEY = "default-pagesize";

  public static final String CURSOR_BATCH_SIZE_KEY = "cursor-batch-size";

  public static final String ALLOW_UNESCAPED_CHARACTERS_IN_URL = "allow-unescaped-characters-in-url";

  public static final String CONNECTION_OPTIONS_KEY = "connection-options";

  @SuppressWarnings(value = { "unchecked" }) private static Map<String, Object> getConfigurationFromFile(final Path confFilePath) throws ConfigurationException {
    Yaml yaml = new Yaml();
    Map<String, Object> conf = null;
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(confFilePath.toFile());
      conf = (Map<String, Object>) yaml.load(fis);
    } catch (FileNotFoundException fne) {
      throw new ConfigurationException("configuration file not found", fne);
    } catch (Throwable t) {
      throw new ConfigurationException("error parsing the configuration file", t);
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException ioe) {
          LOGGER.warn("Can\'t close the FileInputStream", ioe);
        }
      }
    }
    return conf;
  }

  public static int[] convertListToIntArray(List<Object> integers) {
    int[] ret = new int[integers.size()];
    Iterator<Object> iterator = integers.iterator();
    for (int i = 0; i < ret.length; i++) {
      Object o = iterator.next();
      if (o instanceof Integer) {
        ret[i] = (Integer) o;
      } else {
        return new int[0];
      }
    }
    return ret;
  }

  private boolean silent = false;

  private final boolean httpsListener;

  private final int httpsPort;

  private final String httpsHost;

  private final boolean httpListener;

  private final int httpPort;

  private final String httpHost;

  private final boolean ajpListener;

  private final int ajpPort;

  private final String ajpHost;

  private final String instanceName;

  private final RequestContext.REPRESENTATION_FORMAT defaultRepresentationFromat;

  private final boolean useEmbeddedKeystore;

  private final String keystoreFile;

  private final String keystorePassword;

  private final String certPassword;

  private final MongoClientURI mongoUri;

  private final List<Map<String, Object>> mongoMounts;

  private final List<Map<String, Object>> staticResourcesMounts;

  private final List<Map<String, Object>> applicationLogicMounts;

  private final List<Map<String, Object>> metadataNamedSingletons;

  private final String idmImpl;

  private final Map<String, Object> idmArgs;

  private final String authMechanismImpl;

  private final Map<String, Object> authMechanismArgs;

  private final String amImpl;

  private final Map<String, Object> amArgs;

  private final String logFilePath;

  private final Level logLevel;

  private final boolean logToConsole;

  private final boolean logToFile;

  private final boolean localCacheEnabled;

  private final long localCacheTtl;

  private final boolean schemaCacheEnabled;

  private final long schemaCacheTtl;

  private final int requestsLimit;

  private final int ioThreads;

  private final int workerThreads;

  private final int bufferSize;

  private final int buffersPerRegion;

  private final boolean directBuffers;

  private final boolean forceGzipEncoding;

  private final int eagerPoolSize;

  private final int eagerLinearSliceWidht;

  private final int eagerLinearSliceDelta;

  private final int[] eagerLinearSliceHeights;

  private final int eagerRndSliceMinWidht;

  private final int eagerRndMaxCursors;

  private final boolean authTokenEnabled;

  private final int authTokenTtl;

  private final ETAG_CHECK_POLICY dbEtagCheckPolicy;

  private final ETAG_CHECK_POLICY collEtagCheckPolicy;

  private final ETAG_CHECK_POLICY docEtagCheckPolicy;

  private final Map<String, Object> connectionOptions;

  private final Integer logExchangeDump;

  private final METRICS_GATHERING_LEVEL metricsGatheringLevel;

  private final long queryTimeLimit;

  private final long aggregationTimeLimit;

  private final boolean aggregationCheckOperators;

  private final boolean ansiConsole;

  private final String initializerClass;

  private final int cursorBatchSize;

  private final int defaultPagesize;

  private final int maxPagesize;

  private final boolean allowUnescapedCharactersInUrl;

  private final Map<String, Object> configurationFileMap;

  public Configuration() {
    this.configurationFileMap = null;
    ansiConsole = true;
    httpsListener = true;
    httpsPort = DEFAULT_HTTPS_PORT;
    httpsHost = DEFAULT_HTTPS_HOST;
    httpListener = true;
    httpPort = DEFAULT_HTTP_PORT;
    httpHost = DEFAULT_HTTP_HOST;
    ajpListener = false;
    ajpPort = DEFAULT_AJP_PORT;
    ajpHost = DEFAULT_AJP_HOST;
    instanceName = DEFAULT_INSTANCE_NAME;
    defaultRepresentationFromat = DEFAULT_REPRESENTATION_FORMAT;
    useEmbeddedKeystore = true;
    keystoreFile = null;
    keystorePassword = null;
    certPassword = null;
    mongoUri = new MongoClientURI(DEFAULT_MONGO_URI);
    mongoMounts = new ArrayList<>();
    Map<String, Object> defaultMongoMounts = new HashMap<>();
    defaultMongoMounts.put(MONGO_MOUNT_WHAT_KEY, "*");
    defaultMongoMounts.put(MONGO_MOUNT_WHERE_KEY, "/");
    mongoMounts.add(defaultMongoMounts);
    applicationLogicMounts = new ArrayList<>();
    staticResourcesMounts = new ArrayList<>();
    metadataNamedSingletons = new ArrayList<>();
    HashMap<String, Object> browserStaticResourcesMountArgs = new HashMap<>();
    browserStaticResourcesMountArgs.put(STATIC_RESOURCES_MOUNT_WHAT_KEY, "browser");
    browserStaticResourcesMountArgs.put(STATIC_RESOURCES_MOUNT_WHERE_KEY, "/browser");
    browserStaticResourcesMountArgs.put(STATIC_RESOURCES_MOUNT_WELCOME_FILE_KEY, "browser.html");
    browserStaticResourcesMountArgs.put(STATIC_RESOURCES_MOUNT_SECURED_KEY, false);
    browserStaticResourcesMountArgs.put(STATIC_RESOURCES_MOUNT_EMBEDDED_KEY, true);
    staticResourcesMounts.add(browserStaticResourcesMountArgs);
    idmImpl = null;
    idmArgs = null;
    authMechanismImpl = null;
    authMechanismArgs = null;
    amImpl = null;
    amArgs = null;
    logFilePath = URLUtils.removeTrailingSlashes(System.getProperty("java.io.tmpdir")).concat(File.separator + "restheart.log");
    logToConsole = true;
    logToFile = true;
    logLevel = Level.INFO;
    traceHeaders = Collections.emptyList();
    localCacheEnabled = true;
    localCacheTtl = 1000;
    schemaCacheEnabled = false;
    schemaCacheTtl = 1000;
    requestsLimit = 100;
    queryTimeLimit = 0;
    aggregationTimeLimit = 0;
    aggregationCheckOperators = true;
    ioThreads = 2;
    workerThreads = 32;
    bufferSize = 16384;
    buffersPerRegion = 20;
    directBuffers = true;
    forceGzipEncoding = false;
    eagerPoolSize = 100;
    eagerLinearSliceWidht = 1000;
    eagerLinearSliceDelta = 100;
    eagerLinearSliceHeights = new int[] { 4, 2, 1 };
    eagerRndSliceMinWidht = 1000;
    eagerRndMaxCursors = 50;
    authTokenEnabled = true;
    authTokenTtl = 15;
    dbEtagCheckPolicy = DEFAULT_DB_ETAG_CHECK_POLICY;
    collEtagCheckPolicy = DEFAULT_COLL_ETAG_CHECK_POLICY;
    docEtagCheckPolicy = DEFAULT_DOC_ETAG_CHECK_POLICY;
    logExchangeDump = 0;
    metricsGatheringLevel = METRICS_GATHERING_LEVEL.ROOT;
    connectionOptions = Maps.newHashMap();
    initializerClass = null;
    cursorBatchSize = DEFAULT_CURSOR_BATCH_SIZE;
    defaultPagesize = DEFAULT_DEFAULT_PAGESIZE;
    maxPagesize = DEFAULT_MAX_PAGESIZE;
    allowUnescapedCharactersInUrl = true;
  }

  public Configuration(final Path confFilePath) throws ConfigurationException {
    this(confFilePath, false);
  }

  public Configuration(final Path confFilePath, boolean silent) throws ConfigurationException {
    this(getConfigurationFromFile(confFilePath), silent);
  }

  public Configuration(Map<String, Object> conf, boolean silent) throws ConfigurationException {
    this.configurationFileMap = conf;
    this.silent = silent;
    ansiConsole = getAsBooleanOrDefault(conf, ANSI_CONSOLE_KEY, true);
    httpsListener = getAsBooleanOrDefault(conf, HTTPS_LISTENER, true);
    httpsPort = getAsIntegerOrDefault(conf, HTTPS_PORT_KEY, DEFAULT_HTTPS_PORT);
    httpsHost = getAsStringOrDefault(conf, HTTPS_HOST_KEY, DEFAULT_HTTPS_HOST);
    httpListener = getAsBooleanOrDefault(conf, HTTP_LISTENER_KEY, false);
    httpPort = getAsIntegerOrDefault(conf, HTTP_PORT_KEY, DEFAULT_HTTP_PORT);
    httpHost = getAsStringOrDefault(conf, HTTP_HOST_KEY, DEFAULT_HTTP_HOST);
    ajpListener = getAsBooleanOrDefault(conf, AJP_LISTENER_KEY, false);
    ajpPort = getAsIntegerOrDefault(conf, AJP_PORT_KEY, DEFAULT_AJP_PORT);
    ajpHost = getAsStringOrDefault(conf, AJP_HOST_KEY, DEFAULT_AJP_HOST);
    instanceName = getAsStringOrDefault(conf, INSTANCE_NAME_KEY, DEFAULT_INSTANCE_NAME);
    String _representationFormat = getAsStringOrDefault(conf, REPRESENTATION_FORMAT_KEY, DEFAULT_REPRESENTATION_FORMAT.name());
    REPRESENTATION_FORMAT rf = REPRESENTATION_FORMAT.PLAIN_JSON;
    try {
      rf = REPRESENTATION_FORMAT.valueOf(_representationFormat);
    } catch (IllegalArgumentException iar) {
      LOGGER.warn("wrong value for {}. allowed values are {}; " + "setting it to {}", REPRESENTATION_FORMAT_KEY, REPRESENTATION_FORMAT.values(), REPRESENTATION_FORMAT.PLAIN_JSON);
    } finally {
      defaultRepresentationFromat = rf;
    }
    useEmbeddedKeystore = getAsBooleanOrDefault(conf, USE_EMBEDDED_KEYSTORE_KEY, true);
    keystoreFile = getAsStringOrDefault(conf, KEYSTORE_FILE_KEY, null);
    keystorePassword = getAsStringOrDefault(conf, KEYSTORE_PASSWORD_KEY, null);
    certPassword = getAsStringOrDefault(conf, CERT_PASSWORD_KEY, null);
    try {
      mongoUri = new MongoClientURI(getAsStringOrDefault(conf, MONGO_URI_KEY, DEFAULT_MONGO_URI));
    } catch (IllegalArgumentException iae) {
      throw new ConfigurationException("wrong parameter " + MONGO_URI_KEY, iae);
    }
    List<Map<String, Object>> mongoMountsDefault = new ArrayList<>();
    Map<String, Object> defaultMongoMounts = new HashMap<>();
    defaultMongoMounts.put(MONGO_MOUNT_WHAT_KEY, "*");
    defaultMongoMounts.put(MONGO_MOUNT_WHERE_KEY, "/");
    mongoMountsDefault.add(defaultMongoMounts);
    mongoMounts = getAsListOfMaps(conf, MONGO_MOUNTS_KEY, mongoMountsDefault);
    applicationLogicMounts = getAsListOfMaps(conf, APPLICATION_LOGIC_MOUNTS_KEY, new ArrayList<>());
    staticResourcesMounts = getAsListOfMaps(conf, STATIC_RESOURCES_MOUNTS_KEY, new ArrayList<>());
    metadataNamedSingletons = getAsListOfMaps(conf, METADATA_NAMED_SINGLETONS_KEY, new ArrayList<>());
    Map<String, Object> idm = getAsMap(conf, IDM_KEY);
    Map<String, Object> authMech = getAsMap(conf, AUTH_MECHANISM_KEY);
    Map<String, Object> am = getAsMap(conf, ACCESS_MANAGER_KEY);
    idmImpl = getAsStringOrDefault(idm, IMPLEMENTATION_CLASS_KEY, DEFAULT_IDM_IMPLEMENTATION_CLASS);
    idmArgs = idm;
    authMechanismImpl = getAsStringOrDefault(authMech, IMPLEMENTATION_CLASS_KEY, DEFAULT_AUTH_MECHANISM_IMPLEMENTATION_CLASS);
    authMechanismArgs = authMech;
    amImpl = getAsStringOrDefault(am, IMPLEMENTATION_CLASS_KEY, DEFAULT_AM_IMPLEMENTATION_CLASS);
    amArgs = am;
    logFilePath = getAsStringOrDefault(conf, LOG_FILE_PATH_KEY, URLUtils.removeTrailingSlashes(System.getProperty("java.io.tmpdir")).concat(File.separator + "restheart.log"));
    String _logLevel = getAsStringOrDefault(conf, LOG_LEVEL_KEY, "INFO");
    logToConsole = getAsBooleanOrDefault(conf, ENABLE_LOG_CONSOLE_KEY, true);
    logToFile = getAsBooleanOrDefault(conf, ENABLE_LOG_FILE_KEY, true);
    traceHeaders = getAsListOfStrings(conf, REQUESTS_LOG_TRACE_HEADERS_KEY, Collections.emptyList());
    Level level;
    try {
      level = Level.valueOf(_logLevel);
    } catch (Exception e) {
      if (!silent) {
        LOGGER.info("wrong value for parameter {}: {}. using its default value {}", "log-level", _logLevel, "INFO");
      }
      level = Level.INFO;
    }
    logLevel = level;
    requestsLimit = getAsIntegerOrDefault(conf, REQUESTS_LIMIT_KEY, 100);
    queryTimeLimit = getAsLongOrDefault(conf, QUERY_TIME_LIMIT_KEY, (long) 0);
    aggregationTimeLimit = getAsLongOrDefault(conf, AGGREGATION_TIME_LIMIT_KEY, (long) 0);
    aggregationCheckOperators = getAsBooleanOrDefault(conf, AGGREGATION_CHECK_OPERATORS, true);
    localCacheEnabled = getAsBooleanOrDefault(conf, LOCAL_CACHE_ENABLED_KEY, true);
    localCacheTtl = getAsLongOrDefault(conf, LOCAL_CACHE_TTL_KEY, (long) 1000);
    schemaCacheEnabled = getAsBooleanOrDefault(conf, SCHEMA_CACHE_ENABLED_KEY, true);
    schemaCacheTtl = getAsLongOrDefault(conf, SCHEMA_CACHE_TTL_KEY, (long) 1000);
    ioThreads = getAsIntegerOrDefault(conf, IO_THREADS_KEY, 2);
    workerThreads = getAsIntegerOrDefault(conf, WORKER_THREADS_KEY, 32);
    bufferSize = getAsIntegerOrDefault(conf, BUFFER_SIZE_KEY, 16384);
    buffersPerRegion = getAsIntegerOrDefault(conf, BUFFERS_PER_REGION_KEY, 20);
    directBuffers = getAsBooleanOrDefault(conf, DIRECT_BUFFERS_KEY, true);
    forceGzipEncoding = getAsBooleanOrDefault(conf, FORCE_GZIP_ENCODING_KEY, false);
    eagerPoolSize = getAsIntegerOrDefault(conf, EAGER_POOL_SIZE, 100);
    eagerLinearSliceWidht = getAsIntegerOrDefault(conf, EAGER_LINEAR_SLICE_WIDHT, 1000);
    eagerLinearSliceDelta = getAsIntegerOrDefault(conf, EAGER_LINEAR_SLICE_DELTA, 100);
    eagerLinearSliceHeights = getAsArrayOfInts(conf, EAGER_LINEAR_HEIGHTS, new int[] { 4, 2, 1 });
    eagerRndSliceMinWidht = getAsIntegerOrDefault(conf, EAGER_RND_SLICE_MIN_WIDHT, 1000);
    eagerRndMaxCursors = getAsIntegerOrDefault(conf, EAGER_RND_MAX_CURSORS, 50);
    authTokenEnabled = getAsBooleanOrDefault(conf, AUTH_TOKEN_ENABLED, true);
    authTokenTtl = getAsIntegerOrDefault(conf, AUTH_TOKEN_TTL, 15);
    Map<String, Object> etagCheckPolicies = getAsMap(conf, ETAG_CHECK_POLICY_KEY);
    if (etagCheckPolicies != null) {
      String _dbEtagCheckPolicy = getAsStringOrDefault(etagCheckPolicies, ETAG_CHECK_POLICY_DB_KEY, DEFAULT_DB_ETAG_CHECK_POLICY.name());
      String _collEtagCheckPolicy = getAsStringOrDefault(etagCheckPolicies, ETAG_CHECK_POLICY_COLL_KEY, DEFAULT_COLL_ETAG_CHECK_POLICY.name());
      String _docEtagCheckPolicy = getAsStringOrDefault(etagCheckPolicies, ETAG_CHECK_POLICY_DOC_KEY, DEFAULT_DOC_ETAG_CHECK_POLICY.name());
      ETAG_CHECK_POLICY validDbValue;
      ETAG_CHECK_POLICY validCollValue;
      ETAG_CHECK_POLICY validDocValue;
      try {
        validDbValue = ETAG_CHECK_POLICY.valueOf(_dbEtagCheckPolicy);
      } catch (IllegalArgumentException iae) {
        LOGGER.warn("wrong value for parameter {} setting it to default value {}", ETAG_CHECK_POLICY_DB_KEY, DEFAULT_DB_ETAG_CHECK_POLICY);
        validDbValue = DEFAULT_DB_ETAG_CHECK_POLICY;
      }
      dbEtagCheckPolicy = validDbValue;
      try {
        validCollValue = ETAG_CHECK_POLICY.valueOf(_collEtagCheckPolicy);
      } catch (IllegalArgumentException iae) {
        LOGGER.warn("wrong value for parameter {} setting it to default value {}", ETAG_CHECK_POLICY_COLL_KEY, DEFAULT_COLL_ETAG_CHECK_POLICY);
        validCollValue = DEFAULT_COLL_ETAG_CHECK_POLICY;
      }
      collEtagCheckPolicy = validCollValue;
      try {
        validDocValue = ETAG_CHECK_POLICY.valueOf(_docEtagCheckPolicy);
      } catch (IllegalArgumentException iae) {
        LOGGER.warn("wrong value for parameter {} setting it to default value {}", ETAG_CHECK_POLICY_COLL_KEY, DEFAULT_COLL_ETAG_CHECK_POLICY);
        validDocValue = DEFAULT_DOC_ETAG_CHECK_POLICY;
      }
      docEtagCheckPolicy = validDocValue;
    } else {
      dbEtagCheckPolicy = DEFAULT_DB_ETAG_CHECK_POLICY;
      collEtagCheckPolicy = DEFAULT_COLL_ETAG_CHECK_POLICY;
      docEtagCheckPolicy = DEFAULT_DOC_ETAG_CHECK_POLICY;
    }
    logExchangeDump = getAsIntegerOrDefault(conf, LOG_REQUESTS_LEVEL_KEY, 0);
    {
      METRICS_GATHERING_LEVEL mglevel;
      try {
        String value = getAsStringOrDefault(conf, METRICS_GATHERING_LEVEL_KEY, "ROOT");
        mglevel = METRICS_GATHERING_LEVEL.valueOf(value.toUpperCase(Locale.getDefault()));
      } catch (IllegalArgumentException iae) {
        mglevel = METRICS_GATHERING_LEVEL.ROOT;
      }
      metricsGatheringLevel = mglevel;
    }
    connectionOptions = getAsMap(conf, CONNECTION_OPTIONS_KEY);
    initializerClass = getAsStringOrDefault(conf, INITIALIZER_CLASS_KEY, null);
    cursorBatchSize = getAsIntegerOrDefault(conf, CURSOR_BATCH_SIZE_KEY, DEFAULT_CURSOR_BATCH_SIZE);
    defaultPagesize = getAsIntegerOrDefault(conf, DEFAULT_PAGESIZE_KEY, DEFAULT_DEFAULT_PAGESIZE);
    maxPagesize = getAsIntegerOrDefault(conf, MAX_PAGESIZE_KEY, DEFAULT_MAX_PAGESIZE);
    allowUnescapedCharactersInUrl = getAsBooleanOrDefault(conf, ALLOW_UNESCAPED_CHARACTERS_IN_URL, true);
  }

  @Override public String toString() {
    return "Configuration{" + "silent=" + silent + ", httpsListener=" + httpsListener + ", httpsPort=" + httpsPort + ", httpsHost=" + httpsHost + ", httpListener=" + httpListener + ", httpPort=" + httpPort + ", httpHost=" + httpHost + ", ajpListener=" + ajpListener + ", ajpPort=" + ajpPort + ", ajpHost=" + ajpHost + ", instanceName=" + instanceName + ", defaultRepresentationFromat=" + defaultRepresentationFromat + ", useEmbeddedKeystore=" + useEmbeddedKeystore + ", keystoreFile=" + keystoreFile + ", keystorePassword=" + keystorePassword + ", certPassword=" + certPassword + ", mongoUri=" + mongoUri + ", mongoMounts=" + mongoMounts + ", staticResourcesMounts=" + staticResourcesMounts + ", applicationLogicMounts=" + applicationLogicMounts + ", metadataNamedSingletons=" + metadataNamedSingletons + ", idmImpl=" + idmImpl + ", idmArgs=" + idmArgs + ", authMechanismImpl=" + authMechanismImpl + ", authMechanismArgs=" + authMechanismArgs + ", amImpl=" + amImpl + ", amArgs=" + amArgs + ", logFilePath=" + logFilePath + ", logLevel=" + logLevel + ", logToConsole=" + logToConsole + ", logToFile=" + logToFile + ", localCacheEnabled=" + localCacheEnabled + ", localCacheTtl=" + localCacheTtl + ", schemaCacheEnabled=" + schemaCacheEnabled + ", schemaCacheTtl=" + schemaCacheTtl + ", requestsLimit=" + requestsLimit + ", ioThreads=" + ioThreads + ", workerThreads=" + workerThreads + ", bufferSize=" + bufferSize + ", buffersPerRegion=" + buffersPerRegion + ", directBuffers=" + directBuffers + ", forceGzipEncoding=" + forceGzipEncoding + ", eagerPoolSize=" + eagerPoolSize + ", eagerLinearSliceWidht=" + eagerLinearSliceWidht + ", eagerLinearSliceDelta=" + eagerLinearSliceDelta + ", eagerLinearSliceHeights=" + Arrays.toString(eagerLinearSliceHeights) + ", eagerRndSliceMinWidht=" + eagerRndSliceMinWidht + ", eagerRndMaxCursors=" + eagerRndMaxCursors + ", authTokenEnabled=" + authTokenEnabled + ", authTokenTtl=" + authTokenTtl + ", dbEtagCheckPolicy=" + dbEtagCheckPolicy + ", collEtagCheckPolicy=" + collEtagCheckPolicy + ", docEtagCheckPolicy=" + docEtagCheckPolicy + ", connectionOptions=" + connectionOptions + ", logExchangeDump=" + logExchangeDump + ", metricsGatheringLevel=" + metricsGatheringLevel + ", queryTimeLimit=" + queryTimeLimit + ", aggregationTimeLimit=" + aggregationTimeLimit + ", aggregationCheckOperators=" + aggregationCheckOperators + ", ansiConsole=" + ansiConsole + ", initializerClass=" + initializerClass + ", cursorBatchSize=" + cursorBatchSize + ", defaultPagesize=" + defaultPagesize + ", maxPagesize=" + maxPagesize + ", allowUnescapedCharactersInUrl=" + allowUnescapedCharactersInUrl + ", configurationFileMap=" + configurationFileMap + '}';
  }

  public boolean isAnsiConsole() {
    return ansiConsole;
  }

  @SuppressWarnings(value = { "unchecked" }) private List<Map<String, Object>> getAsListOfMaps(final Map<String, Object> conf, final String key, final List<Map<String, Object>> defaultValue) {
    if (conf == null) {
      if (!silent) {
        LOGGER.debug("parameters group {} not specified in the configuration file. using its default value {}", key, defaultValue);
      }
      return defaultValue;
    }
    Object o = conf.get(key);
    if (o instanceof List) {
      return (List<Map<String, Object>>) o;
    } else {
      if (!silent) {
        LOGGER.debug("parameters group {} not specified in the configuration file, using its default value {}", key, defaultValue);
      }
      return defaultValue;
    }
  }

  @SuppressWarnings(value = { "unchecked" }) private Map<String, Object> getAsMap(final Map<String, Object> conf, final String key) {
    if (conf == null) {
      if (!silent) {
        LOGGER.debug("parameters group {} not specified in the configuration file.", key);
      }
      return null;
    }
    Object o = conf.get(key);
    if (o instanceof Map) {
      return (Map<String, Object>) o;
    } else {
      if (!silent) {
        LOGGER.debug("parameters group {} not specified in the configuration file.", key);
      }
      return null;
    }
  }

  private Boolean getAsBooleanOrDefault(final Map<String, Object> conf, final String key, final Boolean defaultValue) {
    if (conf == null) {
      if (!silent) {
        LOGGER.debug("tried to get paramenter {} from a null configuration map. using its default value {}", key, defaultValue);
      }
      return defaultValue;
    }
    Object o = conf.get(key);
    if (o == null) {
      if (defaultValue && !silent) {
        LOGGER.debug("parameter {} not specified in the configuration file. using its default value {}", key, defaultValue);
      }
      return defaultValue;
    } else {
      if (o instanceof Boolean) {
        if (!silent) {
          LOGGER.debug("paramenter {} set to {}", key, o);
        }
        return (Boolean) o;
      } else {
        if (!silent) {
          LOGGER.warn("wrong value for parameter {}: {}. using its default value {}", key, o, defaultValue);
        }
        return defaultValue;
      }
    }
  }

  private String getAsStringOrDefault(final Map<String, Object> conf, final String key, final String defaultValue) {
    if (conf == null || conf.get(key) == null) {
      if (defaultValue != null && !silent) {
        LOGGER.debug("parameter {} not specified in the configuration file. using its default value {}", key, defaultValue);
      }
      return defaultValue;
    } else {
      if (conf.get(key) instanceof String) {
        if (!silent) {
          LOGGER.debug("paramenter {} set to {}", key, conf.get(key));
        }
        return (String) conf.get(key);
      } else {
        if (!silent) {
          LOGGER.warn("wrong value for parameter {}: {}. using its default value {}", key, conf.get(key), defaultValue);
        }
        return defaultValue;
      }
    }
  }

  private Integer getAsIntegerOrDefault(final Map<String, Object> conf, final String key, final Integer defaultValue) {
    if (conf == null || conf.get(key) == null) {
      if (defaultValue != null && !silent) {
        LOGGER.debug("parameter {} not specified in the configuration file. using its default value {}", key, defaultValue);
      }
      return defaultValue;
    } else {
      if (conf.get(key) instanceof Integer) {
        if (!silent) {
          LOGGER.debug("paramenter {} set to {}", key, conf.get(key));
        }
        return (Integer) conf.get(key);
      } else {
        if (!silent) {
          LOGGER.warn("wrong value for parameter {}: {}. using its default value {}", key, conf.get(key), defaultValue);
        }
        return defaultValue;
      }
    }
  }

  private Long getAsLongOrDefault(final Map<String, Object> conf, final String key, final Long defaultValue) {
    if (conf == null || conf.get(key) == null) {
      if (defaultValue != null && !silent) {
        LOGGER.debug("parameter {} not specified in the configuration file. using its default value {}", key, defaultValue);
      }
      return defaultValue;
    } else {
      if (conf.get(key) instanceof Number) {
        if (!silent) {
          LOGGER.debug("paramenter {} set to {}", key, conf.get(key));
        }
        try {
          return Long.parseLong(conf.get(key).toString());
        } catch (NumberFormatException nfe) {
          if (!silent) {
            LOGGER.warn("wrong value for parameter {}: {}. using its default value {}", key, conf.get(key), defaultValue);
          }
          return defaultValue;
        }
      } else {
        if (!silent) {
          LOGGER.warn("wrong value for parameter {}: {}. using its default value {}", key, conf.get(key), defaultValue);
        }
        return defaultValue;
      }
    }
  }

  @SuppressWarnings(value = { "unchecked" }) private int[] getAsArrayOfInts(final Map<String, Object> conf, final String key, final int[] defaultValue) {
    if (conf == null || conf.get(key) == null) {
      if (defaultValue != null && !silent) {
        LOGGER.debug("parameter {} not specified in the configuration file. using its default value {}", key, defaultValue);
      }
      return defaultValue;
    } else {
      if (conf.get(key) instanceof List) {
        if (!silent) {
          LOGGER.debug("paramenter {} set to {}", key, conf.get(key));
        }
        int ret[] = convertListToIntArray((List<Object>) conf.get(key));
        if (ret.length == 0) {
          if (!silent) {
            LOGGER.warn("wrong value for parameter {}: {}. using its default value {}", key, conf.get(key), defaultValue);
          }
          return defaultValue;
        } else {
          return ret;
        }
      } else {
        if (!silent) {
          LOGGER.warn("wrong value for parameter {}: {}. using its default value {}", key, conf.get(key), defaultValue);
        }
        return defaultValue;
      }
    }
  }

  public boolean isHttpsListener() {
    return httpsListener;
  }

  public int getHttpsPort() {
    return httpsPort;
  }

  public String getHttpsHost() {
    return httpsHost;
  }

  public boolean isHttpListener() {
    return httpListener;
  }

  public int getHttpPort() {
    return httpPort;
  }

  public String getHttpHost() {
    return httpHost;
  }

  public boolean isAjpListener() {
    return ajpListener;
  }

  public int getAjpPort() {
    return ajpPort;
  }

  public String getAjpHost() {
    return ajpHost;
  }

  public boolean isUseEmbeddedKeystore() {
    return useEmbeddedKeystore;
  }

  public String getKeystoreFile() {
    return keystoreFile;
  }

  public String getKeystorePassword() {
    return keystorePassword;
  }

  public String getCertPassword() {
    return certPassword;
  }

  public String getLogFilePath() {
    return logFilePath;
  }

  public Level getLogLevel() {
    String logbackConfigurationFile = System.getProperty("logback.configurationFile");
    if (logbackConfigurationFile != null && !logbackConfigurationFile.isEmpty()) {
      LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
      ch.qos.logback.classic.Logger logger = loggerContext.getLogger("org.restheart");
      return logger.getLevel();
    }
    return logLevel;
  }

  public boolean isLogToConsole() {
    return logToConsole;
  }

  public boolean isLogToFile() {
    return logToFile;
  }

  public int getIoThreads() {
    return ioThreads;
  }

  public int getWorkerThreads() {
    return workerThreads;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public int getBuffersPerRegion() {
    return buffersPerRegion;
  }

  public boolean isDirectBuffers() {
    return directBuffers;
  }

  public boolean isForceGzipEncoding() {
    return forceGzipEncoding;
  }

  public String getIdmImpl() {
    return idmImpl;
  }

  public Map<String, Object> getIdmArgs() {
    return Collections.unmodifiableMap(idmArgs);
  }

  public String getAuthMechanism() {
    return authMechanismImpl;
  }

  public Map<String, Object> getAuthMechanismArgs() {
    return Collections.unmodifiableMap(authMechanismArgs);
  }

  public String getAmImpl() {
    return amImpl;
  }

  public Map<String, Object> getAmArgs() {
    return Collections.unmodifiableMap(amArgs);
  }

  public int getRequestLimit() {
    return getRequestsLimit();
  }

  public List<Map<String, Object>> getMongoMounts() {
    return Collections.unmodifiableList(mongoMounts);
  }

  public boolean isLocalCacheEnabled() {
    return localCacheEnabled;
  }

  public long getLocalCacheTtl() {
    return localCacheTtl;
  }

  public int getRequestsLimit() {
    return requestsLimit;
  }

  public long getQueryTimeLimit() {
    return queryTimeLimit;
  }

  public long getAggregationTimeLimit() {
    return aggregationTimeLimit;
  }

  public boolean getAggregationCheckOperators() {
    return aggregationCheckOperators;
  }

  public List<Map<String, Object>> getApplicationLogicMounts() {
    return Collections.unmodifiableList(applicationLogicMounts);
  }

  public List<Map<String, Object>> getStaticResourcesMounts() {
    return Collections.unmodifiableList(staticResourcesMounts);
  }

  public List<Map<String, Object>> getMetadataNamedSingletons() {
    return Collections.unmodifiableList(metadataNamedSingletons);
  }

  public int getEagerLinearSliceWidht() {
    return eagerLinearSliceWidht;
  }

  public int getEagerLinearSliceDelta() {
    return eagerLinearSliceDelta;
  }

  public int[] getEagerLinearSliceHeights() {
    return eagerLinearSliceHeights;
  }

  public int getEagerRndSliceMinWidht() {
    return eagerRndSliceMinWidht;
  }

  public int getEagerRndMaxCursors() {
    return eagerRndMaxCursors;
  }

  public int getEagerPoolSize() {
    return eagerPoolSize;
  }

  public boolean isAuthTokenEnabled() {
    return authTokenEnabled;
  }

  public int getAuthTokenTtl() {
    return authTokenTtl;
  }

  public MongoClientURI getMongoUri() {
    return mongoUri;
  }

  public boolean isSchemaCacheEnabled() {
    return schemaCacheEnabled;
  }

  public long getSchemaCacheTtl() {
    return schemaCacheTtl;
  }

  public ETAG_CHECK_POLICY getDbEtagCheckPolicy() {
    return dbEtagCheckPolicy;
  }

  public ETAG_CHECK_POLICY getCollEtagCheckPolicy() {
    return collEtagCheckPolicy;
  }

  public ETAG_CHECK_POLICY getDocEtagCheckPolicy() {
    return docEtagCheckPolicy;
  }

  public Integer logExchangeDump() {
    return logExchangeDump;
  }

  public Map<String, Object> getConnectionOptions() {
    return Collections.unmodifiableMap(connectionOptions);
  }

  public String getInstanceName() {
    return instanceName;
  }

  public REPRESENTATION_FORMAT getDefaultRepresentationFormat() {
    return defaultRepresentationFromat;
  }

  public Map<String, Object> getConfigurationFileMap() {
    return Collections.unmodifiableMap(configurationFileMap);
  }

  public METRICS_GATHERING_LEVEL getMetricsGatheringLevel() {
    return metricsGatheringLevel;
  }

  public boolean gatheringAboveOrEqualToLevel(METRICS_GATHERING_LEVEL level) {
    return getMetricsGatheringLevel().compareTo(level) >= 0;
  }

  public enum METRICS_GATHERING_LEVEL {
    OFF,
    ROOT,
    DATABASE,
    COLLECTION
  }

  public String getInitializerClass() {
    return initializerClass;
  }

  public int getCursorBatchSize() {
    return cursorBatchSize;
  }

  public int getMaxPagesize() {
    return maxPagesize;
  }

  public int getDefaultPagesize() {
    return defaultPagesize;
  }

  public boolean isAllowUnescapedCharactersInUrl() {
    return allowUnescapedCharactersInUrl;
  }

  public static final String REQUESTS_LOG_TRACE_HEADERS_KEY = "requests-log-trace-headers";

  private final List<String> traceHeaders;

  @SuppressWarnings(value = { "unchecked" }) private List<String> getAsListOfStrings(final Map<String, Object> conf, final String key, final List<String> defaultValue) {
    if (conf == null || conf.get(key) == null) {
      if (defaultValue != null && !silent) {
        LOGGER.debug("parameter {} not specified in the configuration file. using its default value {}", key, defaultValue);
      }
      return defaultValue;
    } else {
      if (conf.get(key) instanceof List) {
        if (!silent) {
          LOGGER.debug("paramenter {} set to {}", key, conf.get(key));
        }
        List<String> ret = ((List<String>) conf.get(key));
        if (ret.isEmpty()) {
          if (!silent) {
            LOGGER.warn("wrong value for parameter {}: {}. using its default value {}", key, conf.get(key), defaultValue);
          }
          return defaultValue;
        } else {
          return ret;
        }
      } else {
        if (!silent) {
          LOGGER.warn("wrong value for parameter {}: {}. using its default value {}", key, conf.get(key), defaultValue);
        }
        return defaultValue;
      }
    }
  }

  public List<String> getTraceHeaders() {
    return traceHeaders;
  }
}