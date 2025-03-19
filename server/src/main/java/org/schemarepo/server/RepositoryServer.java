package org.schemarepo.server;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.schemarepo.Repository;
import org.schemarepo.config.Config;
import org.schemarepo.config.ConfigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * A {@link RepositoryServer} is a stand-alone server for running a
 * {@link RESTRepository}. {@link #main(String...)} takes a single argument
 * containing a property file for configuration. <br/>
 * <br/>
 *
 */
public class RepositoryServer {
  private final Server server;

  private final Injector injector;

  /**
   * Constructs an instance of this class, overlaying the default properties
   * with any identically-named properties in the supplied {@link Properties}
   * instance.
   *
   * @param props
   *          Property values for overriding the defaults.
   *          <p>
   *          <b><i>Any overriding properties must be supplied as type </i>
   *          <code>String</code><i> or they will not work and the default
   *          values will be used.</i></b>
   *
   */
  public RepositoryServer(Properties props) {

<<<<<<< /usr/src/app/output/schema-repo/schema-repo/f85f90da3d8305f4cd40870cf736123b49ae379f/server/src/main/java/org/schemarepo/server/RepositoryServer.java/left.java
    SLF4JBridgeHandler.removeHandlersForRootLogger();
=======
    final Logger logger = LoggerFactory.getLogger(getClass());
>>>>>>> /usr/src/app/output/schema-repo/schema-repo/f85f90da3d8305f4cd40870cf736123b49ae379f/server/src/main/java/org/schemarepo/server/RepositoryServer.java/right.java


<<<<<<< /usr/src/app/output/schema-repo/schema-repo/f85f90da3d8305f4cd40870cf736123b49ae379f/server/src/main/java/org/schemarepo/server/RepositoryServer.java/left.java
    SLF4JBridgeHandler.install();
=======
    final String julToSlf4jDep = "jul-to-slf4j dependency";
>>>>>>> /usr/src/app/output/schema-repo/schema-repo/f85f90da3d8305f4cd40870cf736123b49ae379f/server/src/main/java/org/schemarepo/server/RepositoryServer.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    final String julPropName = Config.LOGGING_ROUTE_JUL_TO_SLF4J;
>>>>>>> /usr/src/app/output/schema-repo/schema-repo/f85f90da3d8305f4cd40870cf736123b49ae379f/server/src/main/java/org/schemarepo/server/RepositoryServer.java/right.java

    if (Boolean.parseBoolean(props.getProperty(julPropName, Config.getDefault(julPropName)))) {
      final String slf4jBridgeHandlerName = "org.slf4j.bridge.SLF4JBridgeHandler";
      try {
        final Class slf4jBridgeHandler = Class.forName(slf4jBridgeHandlerName, true, Thread.currentThread().getContextClassLoader());
        slf4jBridgeHandler.getMethod("removeHandlersForRootLogger").invoke(null);
        slf4jBridgeHandler.getMethod("install").invoke(null);
        logger.info("Routing java.util.logging traffic through SLF4J");
      } catch (Exception e) {
        logger.error("Failed to install {}, java.util.logging is unaffected. Perhaps you need to add {}", slf4jBridgeHandlerName, julToSlf4jDep, e);
      }
    } else {
      logger.info("java.util.logging is NOT routed through SLF4J. Set {} property to true and add {} if you want otherwise", julPropName, julToSlf4jDep);
    }
    this.injector = Guice.createInjector(new ConfigModule(props), new ServerModule());
    this.server = injector.getInstance(Server.class);
  }

  public static void main(String... args) throws Exception {
    if (args.length != 1) {
      printHelp();
      System.exit(1);
    }
    File config = new File(args[0]);
    if (!config.canRead()) {
      System.err.println("Cannot read file: " + config);
      printHelp();
      System.exit(1);
    }
    Properties props = new Properties();
    props.load(new BufferedInputStream(new FileInputStream(config)));
    RepositoryServer server = new RepositoryServer(props);
    try {
      server.start();
      server.join();
    }  finally {
      server.stop();
    }
  }

  public void start() throws Exception {
    server.start();
  }

  public void join() throws InterruptedException {
    server.join();
  }

  public void stop() throws Exception {
    server.stop();
  }

  private static void printHelp() {
    System.err.println("One argument expected containing a configuration " + "properties file.  Default properties are:");
    ConfigModule.printDefaults(System.err);
  }

  private static class ShutDownListener extends AbstractLifeCycle.AbstractLifeCycleListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Repository repo;

    private final Integer gracefulShutdown;

    ShutDownListener(Repository repo, Integer gracefulShutdown) {
      this.repo = repo;
      this.gracefulShutdown = gracefulShutdown;
    }

    @Override public void lifeCycleStopping(LifeCycle event) {
      logger.info("Going to wait {} ms to drain requests, then close the repo and exit.", gracefulShutdown);
    }

    @Override public void lifeCycleStopped(LifeCycle event) {
      logger.info(
<<<<<<< /usr/src/app/output/schema-repo/schema-repo/f85f90da3d8305f4cd40870cf736123b49ae379f/server/src/main/java/org/schemarepo/server/RepositoryServer.java/left.java
      "Closing the repo."
=======
      "Waited {} ms to drain requests before closing the repo and exiting. " + "This wait time can be adjusted with the {} config property."
>>>>>>> /usr/src/app/output/schema-repo/schema-repo/f85f90da3d8305f4cd40870cf736123b49ae379f/server/src/main/java/org/schemarepo/server/RepositoryServer.java/right.java
      , gracefulShutdown, Config.JETTY_GRACEFUL_SHUTDOWN);
      try {
        repo.close();
        logger.info("Successfully closed the repo.");
      } catch (IOException e) {
        logger.warn("Failed to properly close repo", e);
      }
    }
  }

  private static class ServerModule extends JerseyServletModule {
    @Override protected void configureServlets() {
      bind(Connector.class).to(SelectChannelConnector.class);
      serve("/*").with(GuiceContainer.class);
      bind(RESTRepository.class);
    }

    @Provides @Singleton public Server provideServer(@Named(value = Config.JETTY_HOST) String host, @Named(value = Config.JETTY_PORT) Integer port, @Named(value = Config.JETTY_PATH) String path, @Named(value = Config.JETTY_HEADER_SIZE) Integer headerSize, @Named(value = Config.JETTY_BUFFER_SIZE) Integer bufferSize, @Named(value = Config.JETTY_STOP_AT_SHUTDOWN) Boolean stopAtShutdown, @Named(value = Config.JETTY_GRACEFUL_SHUTDOWN) Integer gracefulShutdown, Repository repo, Connector connector, GuiceFilter guiceFilter, ServletContextHandler handler) {
      Server server = new Server();
      if (null != host && !host.isEmpty()) {
        connector.setHost(host);
      }
      connector.setPort(port);
      connector.setRequestHeaderSize(headerSize);
      connector.setRequestBufferSize(bufferSize);
      server.setConnectors(new Connector[] { connector });
      FilterHolder holder = new FilterHolder(guiceFilter);
      handler.addFilter(holder, "/*", null);
      handler.addServlet(NoneServlet.class, "/");
      handler.setContextPath(path);
      handler.addLifeCycleListener(new ShutDownListener(repo, gracefulShutdown));
      server.setHandler(handler);
      server.dumpStdErr();
      server.setStopAtShutdown(stopAtShutdown);
      server.setGracefulShutdown(gracefulShutdown);
      return server;
    }

    private static final class NoneServlet extends HttpServlet {
      private static final long serialVersionUID = 4560115319373180139L;
    }
  }
}