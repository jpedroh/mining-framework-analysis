package org.lionsoul.jcseg.server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.lionsoul.jcseg.server.controller.MainController;
import org.lionsoul.jcseg.server.controller.KeyphraseController;
import org.lionsoul.jcseg.server.controller.KeywordsController;
import org.lionsoul.jcseg.server.controller.SentenceController;
import org.lionsoul.jcseg.server.controller.SummaryController;
import org.lionsoul.jcseg.server.controller.TokenizerController;
import org.lionsoul.jcseg.server.core.AbstractRouter;
import org.lionsoul.jcseg.server.core.DynamicRestRouter;
import org.lionsoul.jcseg.server.core.StandardHandler;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

/**
 * Jcseg RESTful api server
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class JcsegServer {
  /**
	 * jcseg server config 
	*/
  private JcsegServerConfig config;

  /**
	 * jetty server instance 
	*/
  private Server server;

  /**
	 * global resource pool 
	*/
  private GlobalResourcePool resourcePool = null;

  /**
	 * construct method
	 * 
	 * @param	config
	*/
  public JcsegServer(JcsegServerConfig config) {
    this.config = config;
    resourcePool = new GlobalResourcePool();
    init();
  }

  /**
	 * initialize the server and register the basic context handler
	*/
  private void init() {
    QueuedThreadPool threadPool = new QueuedThreadPool();
    threadPool.setMaxThreads(config.getMaxThreadPoolSize());
    threadPool.setIdleTimeout(config.getThreadIdleTimeout());
    server = new Server(threadPool);
    HttpConfiguration http_config = new HttpConfiguration();
    http_config.setOutputBufferSize(config.getOutputBufferSize());
    http_config.setRequestHeaderSize(config.getRequestHeaderSize());
    http_config.setResponseHeaderSize(config.getRequestHeaderSize());
    http_config.setSendServerVersion(false);
    http_config.setSendDateHeader(false);
    ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(http_config));
    connector.setPort(config.getPort());
    connector.setHost(config.getHost());
    connector.setIdleTimeout(config.getHttpIdleTimeout());
    server.addConnector(connector);
  }

  /**
	 * register handler service 
	*/
  public JcsegServer registerHandler() {
    String basePath = this.getClass().getPackage().getName() + ".controller";
    AbstractRouter router = new DynamicRestRouter(basePath, MainController.class);
    router.addMapping("/extractor/keywords", KeywordsController.class);
    router.addMapping("/extractor/keyphrase", KeyphraseController.class);
    router.addMapping("/extractor/sentence", SentenceController.class);
    router.addMapping("/extractor/summary", SummaryController.class);
    router.addMapping("/tokenizer/default", TokenizerController.class);
    GlobalProjectSetting setting = new GlobalProjectSetting();
    setting.setCharset(config.getCharset());
    StandardHandler stdHandler = new StandardHandler(config, setting, resourcePool, router);
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setWelcomeFiles(new String[] { "index.html" });
    if (config.getAppBasePath() != null) {
      resourceHandler.setResourceBase(config.getAppBasePath());
    }
    GzipHandler gzipHandler = new GzipHandler();
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { stdHandler, resourceHandler });
    gzipHandler.setHandler(handlers);
    server.setHandler(gzipHandler);
    return this;
  }

  /**
	 * register global resource (global resource initialize)
	 * 
	 * @return JcsegServer
	 * @throws CloneNotSupportedException 
	 * @throws JcsegException 
	*/
  public JcsegServer registerGlobalResource() throws CloneNotSupportedException, JcsegException {
    String configFile = "/java/test/jcseg.properties";
    JcsegTaskConfig tokenizerConfig = new JcsegTaskConfig(configFile);
    JcsegTaskConfig extractorConfig = tokenizerConfig.clone();
    ADictionary dic = DictionaryFactory.createDefaultDictionary(tokenizerConfig);
    extractorConfig.setAppendCJKPinyin(false);
    extractorConfig.setClearStopwords(true);
    extractorConfig.setKeepUnregWords(false);
    resourcePool.addDict("main", dic);
    resourcePool.addConfig("default", tokenizerConfig);
    resourcePool.addConfig("extractor", extractorConfig);
    TokenizerEntry defaultTokenizer = new TokenizerEntry(tokenizerConfig, dic);
    resourcePool.addTokenizerEntry("default", defaultTokenizer);
    resourcePool.addTokenizerEntry("sensitive", defaultTokenizer);
    return this;
  }

  /**
	 * start the server 
	 * 
	 * @throws Exception 
	*/
  public void start() throws Exception {
    if (server != null) {
      server.start();
      server.join();
    }
  }

  /**
	 * stop the server 
	 * 
	 * @throws Exception 
	*/
  public void stop() throws Exception {
    if (server != null) {
      server.stop();
    }
  }

  public static void main(String[] args) {
    JcsegServerConfig config = JcsegServerConfig.create();
    JcsegServer server = new JcsegServer(config);
    try {
      System.out.print("+--[Info]: Register handler ... ");
      server.registerHandler();
      System.out.println(" --[Ok]");
      System.out.print("+--[Info]: Register global resource ... ");
      server.registerGlobalResource();
      System.out.println(" --[Ok]");
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}