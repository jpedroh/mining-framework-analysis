package org.asteriskjava.fastagi;
import org.asteriskjava.fastagi.internal.AgiChannelFactory;
import org.asteriskjava.fastagi.internal.AgiConnectionHandler;
import org.asteriskjava.fastagi.internal.DefaultAgiChannelFactory;
import org.asteriskjava.fastagi.internal.FastAgiConnectionHandler;
import org.asteriskjava.util.Log;
import org.asteriskjava.util.LogFactory;
import org.asteriskjava.util.ReflectionUtil;
import org.asteriskjava.util.ServerSocketFacade;
import org.asteriskjava.util.SocketConnectionFacade;
import org.asteriskjava.util.internal.ServerSocketFacadeImpl;
import java.io.IOException;
import java.net.InetAddress;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.RejectedExecutionException;

/**
 * Default implementation of the {@link org.asteriskjava.fastagi.AgiServer} interface for FastAGI.
 *
 * @author srt
 * @version $Id$
 */
public class DefaultAgiServer extends AbstractAgiServer implements AgiServer {
  private final Log logger = LogFactory.getLog(getClass());

  /**
     * The default name of the resource bundle that contains the config.
     */
  private static final String DEFAULT_CONFIG_RESOURCE_BUNDLE_NAME = "fastagi";

  /**
     * The default bind port.
     */
  private static final int DEFAULT_BIND_PORT = 4573;

  /** Default 50?  Windows server max 200? */
  private static final int BACKLOG = 200;

  private ServerSocketFacade serverSocket;

  private String configResourceBundleName = DEFAULT_CONFIG_RESOURCE_BUNDLE_NAME;

  private int port = DEFAULT_BIND_PORT;

  private InetAddress address = null;

  /**
     * Creates a new DefaultAgiServer.
     */
  public DefaultAgiServer() {
    this(null, null);
  }

  /**
     * Creates a new DefaultAgiServer and set a custom factory for creating AgiChannels
     *
     * @param agiChannelFactory The factory to use for creating new AgiChannel instances.
     */
  public DefaultAgiServer(AgiChannelFactory agiChannelFactory) {
    this(null, null, agiChannelFactory);
  }

  /**
     * Creates a new DefaultAgiServer and loads its configuration from an alternative resource bundle.
     *
     * @param configResourceBundleName the name of the conifiguration resource bundle (default is "fastagi").
     */
  public DefaultAgiServer(String configResourceBundleName) {
    this(configResourceBundleName, null);
  }

  /**
     * Creates a new DefaultAgiServer that uses the given {@link MappingStrategy}.
     *
     * @param mappingStrategy the MappingStrategy to use to determine the AgiScript to run.
     * @since 1.0.0
     */
  public DefaultAgiServer(MappingStrategy mappingStrategy) {
    this(null, mappingStrategy);
  }

  /**
     * Creates a new DefaultAgiServer that runs the given {@link AgiScript} for all requests.
     *
     * @param agiScript the AgiScript to run.
     * @since 1.0.0
     */
  public DefaultAgiServer(AgiScript agiScript) {
    this(null, new StaticMappingStrategy(agiScript));
  }

  /**
     * Creates a new DefaultAgiServer and loads its configuration from an alternative resource bundle and
     * uses the given {@link MappingStrategy}.
     *
     * @param configResourceBundleName the name of the conifiguration resource bundle (default is "fastagi").
     * @param mappingStrategy          the MappingStrategy to use to determine the AgiScript to run.
     * @since 1.0.0
     */
  public DefaultAgiServer(String configResourceBundleName, MappingStrategy mappingStrategy) {
    this(configResourceBundleName, mappingStrategy, new DefaultAgiChannelFactory());
  }

  /**
     * Creates a new DefaultAgiServer and loads its configuration from an alternative resource bundle and
     * uses the given {@link MappingStrategy}.
     *
     * @param configResourceBundleName the name of the conifiguration resource bundle (default is "fastagi").
     * @param mappingStrategy          the MappingStrategy to use to determine the AgiScript to run.
     * @param agiChannelFactory        The factory to use for creating new AgiChannel instances.
     * @since 1.0.0
     */
  public DefaultAgiServer(String configResourceBundleName, MappingStrategy mappingStrategy, AgiChannelFactory agiChannelFactory) {
    super(agiChannelFactory);
    if (mappingStrategy == null) {
      final CompositeMappingStrategy compositeMappingStrategy = new CompositeMappingStrategy();
      compositeMappingStrategy.addStrategy(new ResourceBundleMappingStrategy());
      compositeMappingStrategy.addStrategy(new ClassNameMappingStrategy());
      if (ReflectionUtil.isClassAvailable("javax.script.ScriptEngineManager")) {
        MappingStrategy scriptEngineMappingStrategy = (MappingStrategy) ReflectionUtil.newInstance("org.asteriskjava.fastagi.ScriptEngineMappingStrategy");
        if (scriptEngineMappingStrategy != null) {
          compositeMappingStrategy.addStrategy(scriptEngineMappingStrategy);
        }
      } else {
        logger.warn("ScriptEngine support disabled: It is only availble when running at least Java 6");
      }
      setMappingStrategy(compositeMappingStrategy);
    } else {
      setMappingStrategy(mappingStrategy);
    }
    if (configResourceBundleName != null) {
      this.configResourceBundleName = configResourceBundleName;
    }
    loadConfig();
  }

  /**
     * Sets the TCP port to listen on for new connections.
     * <br>
     * The default port is 4573.
     *
     * @param bindPort the port to bind to.
     * @deprecated use {@link #setPort(int)} instead
     */
  @Deprecated public void setBindPort(int bindPort) {
    this.port = bindPort;
  }

  /**
     * Sets the TCP port to listen on for new connections.
     * <br>
     * The default port is 4573.
     *
     * @param port the port to bind to.
     * @since 0.2
     */
  public void setPort(int port) {
    this.port = port;
  }

  /**
     * Returns the TCP port this server is configured to bind to.
     *
     * @return the TCP port this server is configured to bind to.
     * @since 1.0.0
     */
  public int getPort() {
    return port;
  }

  /**
     * Returns the address this server is configured to bind to.
     * @return the address this server is configured to bind to.
     */
  public InetAddress getAddress() {
    return address;
  }

  /**
     * Sets the address to bind server.
     * @param address the address to bind to.
     */
  public void setAddress(InetAddress address) {
    this.address = address;
  }

  private void loadConfig() {
    final ResourceBundle resourceBundle;
    try {
      resourceBundle = ResourceBundle.getBundle(configResourceBundleName);
    } catch (MissingResourceException e) {
      return;
    }
    try {
      String portString;
      try {
        portString = resourceBundle.getString("port");
      } catch (MissingResourceException e) {
        portString = resourceBundle.getString("bindPort");
      }
      port = Integer.parseInt(portString);
    } catch (Exception e) {
    }
    try {
      setPoolSize(Integer.parseInt(resourceBundle.getString("poolSize")));
    } catch (Exception e) {
    }
    try {
      setMaximumPoolSize(Integer.parseInt(resourceBundle.getString("maximumPoolSize")));
    } catch (Exception e) {
    }
  }

  protected ServerSocketFacade createServerSocket() throws IOException {
    return new ServerSocketFacadeImpl(port, BACKLOG, address);
  }

  public void startup() throws IOException, IllegalStateException {
    try {
      serverSocket = createServerSocket();
    } catch (IOException e) {
      logger.error("Unable start AgiServer: cannot to bind to *:" + port + ".", e);
      throw e;
    }
    logger.info("Listening on *:" + port + ".");
    while (true) {
      final SocketConnectionFacade socket;
      try {
        socket = serverSocket.accept();
      } catch (IOException e) {
        if (isDie()) {
          break;
        } else {
          handleException("IOException while waiting for connections.", e);
          continue;
        }
      }
      logger.debug("Received connection from " + socket.getRemoteAddress());
      final AgiConnectionHandler connectionHandler = new FastAgiConnectionHandler(getMappingStrategy(), socket, this.getAgiChannelFactory());
      try {
        execute(connectionHandler);
      } catch (RejectedExecutionException e) {
        logger.warn("Execution was rejected by pool. Try to increase the pool size.");
        connectionHandler.release();
      }
    }
    logger.info("AgiServer shut down.");
  }

  /**
     * @deprecated use {@link #startup()} instead.
     */
  @Deprecated public void run() {
    try {
      startup();
    } catch (IOException e) {
    }
  }

  @Override public void shutdown() throws IllegalStateException {
    super.shutdown();
    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        logger.warn("IOException while closing server socket.", e);
      }
    }
  }

  @Override protected void finalize() throws Throwable {
    super.finalize();
    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (IOException e) {
      }
    }
  }

  /**
     * Creates a new DefaultAgiServer and starts it.
     *
     * @param args not used
     * @throws Exception if the server can't be started
     * @deprecated since 1.0.0 use {@link org.asteriskjava.Cli} instead.
     */
  @Deprecated public static void main(String[] args) throws Exception {
    final AgiServer server;
    server = new DefaultAgiServer();
    server.startup();
  }
}