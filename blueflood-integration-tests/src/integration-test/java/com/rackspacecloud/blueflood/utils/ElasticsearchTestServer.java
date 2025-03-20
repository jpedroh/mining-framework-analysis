package com.rackspacecloud.blueflood.utils;
import com.github.tlrx.elasticsearch.test.EsSetup;
import com.rackspacecloud.blueflood.io.ElasticIO;
import com.rackspacecloud.blueflood.io.ElasticTokensIO;
import com.rackspacecloud.blueflood.io.EventElasticSearchIO;
import com.rackspacecloud.blueflood.service.Configuration;
import com.rackspacecloud.blueflood.service.ElasticIOConfig;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;
import java.io.IOException;
import java.util.Arrays;

/**
 * Manages embedded Elasticsearch servers for tests. An instance of this class manages its own, separate Elasticsearch.
 * In theory, multiple instances could be active simultaneously, but at the moment, the tlrx implementation always binds
 * to port 9200, and that's where tests know to find it. To run concurrent instances, that would have to be fixed. The
 * server should bind a random, available port, and each test should ask the instance of this class where to find it.
 */
public class ElasticsearchTestServer {
  private enum EsInitMethod {
    TEST_CONTAINERS,
    TLRX,
    EXTERNAL
  }

  /**
     * The selected way to start Elasticsearch for this test class. This will change over time as we update
     * Elasticsearch and change to new testing mechanisms. It's useful to declare here so that you can easily test
     * against different variants in a dev environment.
     */
  private static final EsInitMethod esInitMethod = EsInitMethod.TLRX;

  private ElasticsearchContainer elasticsearchContainer;

  /**
     * ONLY IF using TEST_CONTAINERS as the init method, sets the version of Elasticsearch to test with. This must be
     * a valid Elasticsearch Docker image version.
     */
  private static final 
<<<<<<< /usr/src/app/output/rackerlabs/blueflood/d158fbfcd5bfcaeaff7b4d1b62984ea5fe3a4f4d/blueflood-integration-tests/src/integration-test/java/com/rackspacecloud/blueflood/utils/ElasticsearchTestServer.java/left.java
  ElasticsearchTestServer
=======
  String
>>>>>>> /usr/src/app/output/rackerlabs/blueflood/d158fbfcd5bfcaeaff7b4d1b62984ea5fe3a4f4d/blueflood-integration-tests/src/integration-test/java/com/rackspacecloud/blueflood/utils/ElasticsearchTestServer.java/right.java
   
<<<<<<< /usr/src/app/output/rackerlabs/blueflood/d158fbfcd5bfcaeaff7b4d1b62984ea5fe3a4f4d/blueflood-integration-tests/src/integration-test/java/com/rackspacecloud/blueflood/utils/ElasticsearchTestServer.java/left.java
  INSTANCE = new ElasticsearchTestServer()
=======
  testContainersEsVersion = "1.7"
>>>>>>> /usr/src/app/output/rackerlabs/blueflood/d158fbfcd5bfcaeaff7b4d1b62984ea5fe3a4f4d/blueflood-integration-tests/src/integration-test/java/com/rackspacecloud/blueflood/utils/ElasticsearchTestServer.java/right.java
  ;

  private EsSetup esSetup;

  public static final ElasticsearchTestServer getInstance() {
    return INSTANCE;
  }

  /**
     * Starts an in-memory Elasticsearch that's configured for Blueflood. If such a server is already running, this is a
     * no-op. The configuration mimics that found in init-es.sh as closely as I can figure out how to.
     */
  public void ensureStarted() {
    if (esInitMethod.equals(EsInitMethod.TEST_CONTAINERS)) {
      if (elasticsearchContainer == null || !elasticsearchContainer.isRunning()) {
        startTestContainer();
      }
    } else {
      if (esInitMethod.equals(EsInitMethod.TLRX)) {
        if (esSetup == null) {
          startTlrx();
        }
      } else {
        if (esInitMethod.equals(EsInitMethod.EXTERNAL)) {
          System.out.println("Using external Elasticsearch");
        } else {
          throw new IllegalStateException("Illegal value set for Elasticsearch init in tests: " + esInitMethod);
        }
      }
    }
  }

  public void startTestContainer() {
    DockerImageName myImage = DockerImageName.parse("elasticsearch:" + testContainersEsVersion).asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch");
    elasticsearchContainer = new ElasticsearchContainer(myImage);
    elasticsearchContainer.setPortBindings(Arrays.asList("9200:9200", "9300:9300"));
    elasticsearchContainer.start();
    initIt();
  }

  public void startTlrx() {
    esSetup = new EsSetup();
    esSetup.execute(EsSetup.deleteAll());
    esSetup.execute(EsSetup.createIndex(ElasticIO.ELASTICSEARCH_INDEX_NAME_WRITE).withSettings(EsSetup.fromClassPath("index_settings.json")).withMapping("metrics", EsSetup.fromClassPath("metrics_mapping.json")));
    esSetup.execute(EsSetup.createIndex(ElasticTokensIO.ELASTICSEARCH_TOKEN_INDEX_NAME_WRITE).withMapping("tokens", EsSetup.fromClassPath("tokens_mapping.json")));
    esSetup.execute(EsSetup.createIndex(EventElasticSearchIO.EVENT_INDEX).withMapping("graphite_event", EsSetup.fromClassPath("events_mapping.json")));
    esSetup.execute(EsSetup.createIndex("blueflood_initialized_marker"));
  }

  private void initIt() {
    String initScript;
    if (testContainersEsVersion.startsWith("1.")) {
      initScript = "init-es.sh";
    } else {
      if (testContainersEsVersion.startsWith("6.")) {
        initScript = "init-es-6/init-es.sh";
      } else {
        throw new IllegalStateException("I don\'t know which ES init script to use for ES version " + testContainersEsVersion);
      }
    }
    try {
      initIt(initScript);
    } catch (IOException | InterruptedException e) {
      throw new IllegalStateException("Failed to run Elasticsearch init script", e);
    }
  }

  private void initIt(String whichScript) throws IOException, InterruptedException {
    String resourceInThisModule = getClass().getResource("/blueflood.properties").getFile();
    String thisModuleResourcesDir = FilenameUtils.getFullPath(resourceInThisModule);
    String esResourcesDir = thisModuleResourcesDir + "../../../blueflood-elasticsearch/src/main/resources/";
    String initScript = FilenameUtils.concat(esResourcesDir, whichScript);
    String command = initScript + " -u localhost:9200";
    System.out.println("Initialize Elasticsearch for tests with \'" + command + "\'");
    Process process = Runtime.getRuntime().exec(command);
    IOUtils.copy(process.getInputStream(), System.out);
    int exit = process.waitFor();
    if (exit != 0) {
      throw new IllegalStateException("Elasticsearch init script exited with non-zero status");
    }
  }

  /**
     * Stops the in-memory Elasticsearch managed by this object. It's expected, though unproven, that this would release
     * all resources in use by that Elasticsearch.
     */
  public void stop() {
    if (esInitMethod.equals(EsInitMethod.TEST_CONTAINERS)) {
      elasticsearchContainer.stop();
      elasticsearchContainer = null;
    } else {
      if (esInitMethod.equals(EsInitMethod.TLRX)) {
        esSetup.terminate();
        esSetup = null;
      } else {
        if (esInitMethod.equals(EsInitMethod.EXTERNAL)) {
          System.out.println("Done with external Elasticsearch");
        } else {
          throw new IllegalStateException("Illegal value set for Elasticsearch init in tests: " + esInitMethod);
        }
      }
    }
  }
}