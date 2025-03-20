package nl.techop.kafka;
import com.yammer.metrics.reporting.*;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import java.net.InetSocketAddress;

/**
 * Class KafkaHttpMetricsServer
 * &lt;p/&gt;
 * Author: arnobroekhof
 * &lt;p/&gt;
 * Purpose: Class for starting a Embedded Jetty server with the codahale metrics servlets loaded
 * &lt;p/&gt;
 * Interfaces: None
 */
public class KafkaHttpMetricsServer {
  private static final Logger LOG = Logger.getLogger(KafkaHttpMetricsServer.class);

  private Server server;

  private int port;

  private String bindAddress;

  /**
   * Method: KafkaHttpMetricsServer
   * &lt;p/&gt;
   * Purpose: Method for constructing the the metrics server.
   *
   * @param bindAddress the name or address to bind on ( defaults to localhost )
   * @param port            the port to bind on ( defaults to 8080 )
   */
  public KafkaHttpMetricsServer(final String bindAddress, final int port) {
    this.port = port;
    this.bindAddress = bindAddress;
    this.init();
  }

  /**
   * Method: init
   * &lt;p/&gt;
   * Purpose: Initializes the embedded Jetty Server with including the metrics servlets.
   */
  private void init() {
    LOG.info("Initializing Kafka Http Metrics Reporter");
    InetSocketAddress inetSocketAddress = new InetSocketAddress(bindAddress, port);
    server = new Server(inetSocketAddress);
    ServletContextHandler servletContextHandler = new ServletContextHandler();
    servletContextHandler.setContextPath("/");
    servletContextHandler.addServlet(new ServletHolder(new AdminServlet()), "/api");
    servletContextHandler.addServlet(new ServletHolder(new MetricsServlet()), "/api/metrics");
    servletContextHandler.addServlet(new ServletHolder(new ThreadDumpServlet()), "/api/threads");
    servletContextHandler.addServlet(new ServletHolder(new HealthCheckServlet()), "/api/healthcheck");
    servletContextHandler.addServlet(new ServletHolder(new PingServlet()), "/api/ping");
    server.setHandler(servletContextHandler);
    LOG.info("Finished initializing Kafka Http Metrics Reporter");
  }

  /**
   * Method: start
   * &lt;p/&gt;
   * Purpose: starting the metrics server
   */
  public void start() {
    try {
      LOG.info("Starting Kafka Http Metrics Reporter");
      server.start();
      LOG.info("Started Kafka Http Metrics Reporter on: " + bindAddress + ":" + port);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Method: stop
   * &lt;p/&gt;
   * Purpose: Stopping the metrics server
   */
  public void stop() {
    try {
      LOG.info("Stopping Kafka Http Metrics Reporter");
      server.stop();
      LOG.info("Kafka Http Metrics Reporter stopped");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}