package strat.mining.stratum.proxy;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.CompressionConfig.CompressionMode;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.http.server.StaticHttpHandlerBase;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import strat.mining.stratum.proxy.configuration.ConfigurationManager;
import strat.mining.stratum.proxy.constant.Constants;
import strat.mining.stratum.proxy.database.DatabaseManager;
import strat.mining.stratum.proxy.manager.HashrateRecorder;
import strat.mining.stratum.proxy.manager.StratumProxyManager;
import strat.mining.stratum.proxy.pool.Pool;
import strat.mining.stratum.proxy.rest.ProxyResources;
import strat.mining.stratum.proxy.utils.Timer;
import strat.mining.stratum.proxy.worker.GetworkRequestHandler;

public class Launcher {
  public static Logger LOGGER = null;

  public static final String THREAD_MONITOR = "";

  private static HttpServer apiHttpServer;

  private static HttpServer getWorkHttpServer;

  public static void main(String[] args) {
    initShutdownHook();
    try {
      ConfigurationManager configurationManager = ConfigurationManager.getInstance();
      configurationManager.loadConfiguration(args);
      LOGGER = LoggerFactory.getLogger(Launcher.class);
      initDatabaseManager();
      initProxyManager(configurationManager);
      initGetwork(configurationManager);
      initHttpServices(configurationManager);
      initHashrateRecorder();
      waitInfinite();
    } catch (Exception e) {
      if (LOGGER != null) {
        LOGGER.error("Failed to start the proxy.", e);
      } else {
        System.out.println("Failed to start the proxy: ");
        e.printStackTrace();
      }
    }
  }

  /**
	 * Initialize the shutdown hook to close gracefully all connections.
	 */
  private static void initShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        Timer.getInstance().schedule(new Timer.Task() {
          public void run() {
            if (LOGGER != null) {
              LOGGER.error("Force killing of the proxy...");
            } else {
              System.err.println("Force killing of the proxy...");
            }
            System.exit(0);
          }
        }, 1000);
        if (StratumProxyManager.getInstance() != null) {
          if (LOGGER != null) {
            LOGGER.info("User requested shutdown... Gracefuly kill all connections...");
          } else {
            System.out.println("User requested shutdown... Gracefuly kill all connections...");
          }
          StratumProxyManager.getInstance().stopListeningIncomingConnections();
          StratumProxyManager.getInstance().closeAllWorkerConnections();
          StratumProxyManager.getInstance().stopPools();
        }
        if (apiHttpServer != null) {
          apiHttpServer.shutdownNow();
        }
        if (getWorkHttpServer != null) {
          getWorkHttpServer.shutdownNow();
        }
        if (LOGGER != null) {
          LOGGER.info("Shutdown !");
        } else {
          System.out.println("Shutdown !");
        }
      }
    });
  }

  /**
	 * Initialize the hashrate recoder.
	 */
  private static void initHashrateRecorder() {
    HashrateRecorder.getInstance().startCapture();
  }

  /**
	 * Initialize the database manager.
	 */
  private static void initDatabaseManager() {
    DatabaseManager.getInstance();
  }

  /**
	 * Initialize the HTTP services.
	 * 
	 * @param configurationManager
	 */
  private static void initHttpServices(ConfigurationManager configurationManager) {
    URI baseUri = UriBuilder.fromUri("http://" + configurationManager.getRestBindAddress()).port(configurationManager.getRestListenPort()).build();
    ResourceConfig config = new ResourceConfig(ProxyResources.class);
    config.register(JacksonFeature.class);
    apiHttpServer = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
    ServerConfiguration serverConfiguration = apiHttpServer.getServerConfiguration();
    apiHttpServer.getListener("grizzly").getCompressionConfig().setCompressionMode(CompressionMode.ON);
    HttpHandler staticHandler = getStaticHandler();
    if (staticHandler != null) {
      serverConfiguration.addHttpHandler(staticHandler, "/");
    }
    apiHttpServer.getListener("grizzly").getKeepAlive().setIdleTimeoutInSeconds(1);
  }

  /**
	 * Return the handler to serve static content.
	 * 
	 * @return
	 */
  private static HttpHandler getStaticHandler() {
    StaticHttpHandlerBase handler = null;
    if (ConfigurationManager.isRunningFromJar()) {
      try {
        File stratumProxyWebappJarFile = new File(ConfigurationManager.getInstallDirectory(), "lib/stratum-proxy-webapp.jar");
        if (stratumProxyWebappJarFile.exists()) {
          handler = new CLStaticHttpHandler(new URLClassLoader(new URL[] { new URL("file://" + stratumProxyWebappJarFile.getAbsolutePath()) }), "/") {
            protected boolean handle(String resourcePath, Request request, Response response) throws Exception {
              String resourcePathFiltered = resourcePath;
              if ("/".equals(resourcePath)) {
                resourcePathFiltered = "/index.html";
              }
              return super.handle(resourcePathFiltered, request, response);
            }
          };
        } else {
          LOGGER.warn("lib/stratum-proxy-webapp.jar not found. GUI will not be available.");
        }
      } catch (Exception e) {
        LOGGER.warn("Failed to initialize the Web content loader. GUI will not be available.", e);
      }
    } else {
      File installPath = new File(ConfigurationManager.getInstallDirectory());
      File docRootPath = new File(installPath.getParentFile(), "src/main/resources/webapp");
      handler = new StaticHttpHandler(docRootPath.getAbsolutePath());
    }
    handler.setFileCacheEnabled(ConfigurationManager.getVersion().equals("Dev"));
    return handler;
  }

  /**
	 * Initialize the Getwork system.
	 * 
	 * @param configurationManager
	 */
  private static void initGetwork(ConfigurationManager configurationManager) {
    URI baseUri = UriBuilder.fromUri("http://" + configurationManager.getGetworkBindAddress()).port(configurationManager.getGetworkListenPort()).build();
    getWorkHttpServer = GrizzlyHttpServerFactory.createHttpServer(baseUri);
    ServerConfiguration serverConfiguration = getWorkHttpServer.getServerConfiguration();
    serverConfiguration.addHttpHandler(new GetworkRequestHandler(), "/", Constants.DEFAULT_GETWORK_LONG_POLLING_URL);
  }

  /**
	 * Initialize the proxy manager
	 * 
	 * @param configurationManager
	 * @throws IOException
	 * @throws CmdLineException
	 */
  private static void initProxyManager(ConfigurationManager configurationManager) throws IOException, CmdLineException {
    List<Pool> pools = configurationManager.getPools();
    LOGGER.info("Using pools: {}.", pools);
    StratumProxyManager.getInstance().startPools(pools);
    StratumProxyManager.getInstance().startListeningIncomingConnections(configurationManager.getStratumBindAddress(), configurationManager.getStratumListeningPort());
  }

  /**
	 * Wait and never return
	 */
  private static void waitInfinite() {
    try {
      synchronized (THREAD_MONITOR) {
        THREAD_MONITOR.wait();
      }
    } catch (Exception e) {
      LOGGER.info("Closing proxy...");
    }
  }
}