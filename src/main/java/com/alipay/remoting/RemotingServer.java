package com.alipay.remoting;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import com.alipay.remoting.log.BoltLoggerFactory;
import com.alipay.remoting.rpc.protocol.UserProcessor;

/**
 * Server template for remoting.
 * 
 * @author jiangping
 * @version $Id: RemotingServer.java, v 0.1 2015-9-5 PM7:37:48 tao Exp $
 */
public abstract class RemotingServer {
  private static final Logger logger = BoltLoggerFactory.getLogger("CommonDefault");

  protected int port;

  /** server init status */
  private AtomicBoolean inited = new AtomicBoolean(false);

  /** server start status */
  private AtomicBoolean started = new AtomicBoolean(false);

  /**
     * @param port
     */
  public RemotingServer(int port) {
    this.port = port;
  }

  /**
     * Initialize.
     */
  public void init() {
    if (inited.compareAndSet(false, true)) {
      logger.warn("Initialize the server.");
      this.doInit();
    } else {
      logger.warn("Server has been inited already.");
    }
  }

  /**
     * Start the server.
     */
  public boolean start() {
    this.init();
    if (started.compareAndSet(false, true)) {
      try {
        logger.warn("Server started on port: " + port);
        return this.doStart();
      } catch (Throwable t) {
        started.set(false);
        this.stop();
        logger.error("ERROR: Failed to start the Server!", t);
        return false;
      }
    } else {

<<<<<<< /usr/src/app/output/sofastack/sofa-bolt/48c96584e07d2715f0346ea03a50e50c03b8787d/src/main/java/com/alipay/remoting/RemotingServer.java/left.java
      this.stop();
=======
      String errMsg = "ERROR: The server has already started!";
>>>>>>> /usr/src/app/output/sofastack/sofa-bolt/48c96584e07d2715f0346ea03a50e50c03b8787d/src/main/java/com/alipay/remoting/RemotingServer.java/right.java

      logger.error(errMsg);
      throw new IllegalStateException(errMsg);
    }
  }

  /**
     * Start the server with ip and port.
     */
  public boolean start(String ip) {
    this.init();
    if (started.compareAndSet(false, true)) {
      try {
        logger.warn("Server started on " + ip + ":" + port);
        return this.doStart(ip);
      } catch (Throwable t) {
        started.set(false);
        this.stop();
        logger.error("ERROR: Failed to start the Server!", t);
        return false;
      }
    } else {

<<<<<<< /usr/src/app/output/sofastack/sofa-bolt/48c96584e07d2715f0346ea03a50e50c03b8787d/src/main/java/com/alipay/remoting/RemotingServer.java/left.java
      this.stop();
=======
      String errMsg = "ERROR: The server has already started!";
>>>>>>> /usr/src/app/output/sofastack/sofa-bolt/48c96584e07d2715f0346ea03a50e50c03b8787d/src/main/java/com/alipay/remoting/RemotingServer.java/right.java

      logger.error(errMsg);
      throw new IllegalStateException(errMsg);
    }
  }

  /**
     * Stop the server.
     * <p>
     * Notice:<br>
     *   <li>Remoting server can not be used any more after shutdown.
     *   <li>If you need, you should destroy it, and instantiate another one.
     */
  public void stop() {
    if (inited.compareAndSet(true, false) || started.compareAndSet(true, false)) {
      this.doStop();
    } else {
      throw new IllegalStateException("ERROR: The server has already stopped!");
    }
  }

  /**
     * Get the port of the server.
     * 
     * @return
     */
  public int getPort() {
    return port;
  }

  /**
     * Inject initialize logic here.
     */
  protected abstract void doInit();

  /**
     * Inject start logic here.
     * 
     * @throws InterruptedException
     */
  protected abstract boolean doStart() throws InterruptedException;

  /**
     * Inject start logic here.
     * 
     * @param ip
     * @throws InterruptedException
     */
  protected abstract boolean doStart(String ip) throws InterruptedException;

  /**
     * Inject stop logic here.
     */
  protected abstract void doStop();

  /**
     * Register processor for command with the command code.
     * 
     * @param commandCode
     * @param processor
     */
  public abstract void registerProcessor(byte protocolCode, CommandCode commandCode, RemotingProcessor<?> processor);

  /**
     * Register default executor service for server.
     * 
     * @param protocolCode
     * @param executor
     */
  public abstract void registerDefaultExecutor(byte protocolCode, ExecutorService executor);

  /** 
     * Register user processor.
     * 
     * @param processor
     */
  public abstract void registerUserProcessor(UserProcessor<?> processor);
}