package net.logstash.logback.appender;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import net.logstash.logback.appender.destination.DelegateDestinationConnectionStrategy;
import net.logstash.logback.appender.destination.DestinationConnectionStrategy;
import net.logstash.logback.appender.destination.DestinationParser;
import net.logstash.logback.appender.destination.PreferPrimaryDestinationConnectionStrategy;
import net.logstash.logback.appender.listener.TcpAppenderListener;
import net.logstash.logback.encoder.SeparatorParser;
import net.logstash.logback.encoder.StreamingEncoder;
import com.lmax.disruptor.EventHandler;
import ch.qos.logback.core.encoder.Encoder;
import com.lmax.disruptor.LifecycleAware;
import ch.qos.logback.core.joran.spi.DefaultClass;
import com.lmax.disruptor.RingBuffer;
import ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory;
import ch.qos.logback.core.net.ssl.SSLConfigurableSocket;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import ch.qos.logback.core.util.CloseUtil;
import ch.qos.logback.core.util.Duration;

/**
 * An {@link AsyncDisruptorAppender} appender that writes
 * events to a TCP {@link Socket} outputStream.
 * <p>
 * 
 * The behavior is similar to a {@link ch.qos.logback.classic.net.SocketAppender}, except that:
 * <ul>
 * <li>it uses a {@link RingBuffer} instead of a {@link BlockingQueue}</li>
 * <li>it writes using an {@link Encoder} instead of serialization</li>
 * </ul>
 * <p>
 * 
 * In addition, SSL can be enabled by setting the SSL configuration via {@link #setSsl(SSLConfiguration)}. 
 * See <a href="http://logback.qos.ch/manual/usingSSL.html">the logback manual</a>
 * for details on how to configure client-side SSL.
 *
 * @author <a href="mailto:mirko.bernardoni@gmail.com">Mirko Bernardoni</a> (original, which did not use disruptor)
 * @since 11 Jun 2014 (creation date)
 */
public abstract class AbstractLogstashTcpSocketAppender<Event extends DeferredProcessingAware, Listener extends TcpAppenderListener<Event>> extends AsyncDisruptorAppender<Event, Listener> {
  protected static final String HOST_NAME_FORMAT = "%3$s";

  protected static final String PORT_FORMAT = "%4$d";

  public static final String DEFAULT_THREAD_NAME_FORMAT = "logback-appender-" + APPENDER_NAME_FORMAT + "-" + HOST_NAME_FORMAT + ":" + PORT_FORMAT + "-" + THREAD_INDEX_FORMAT;

  /**
     * The default port number of remote logging server (4560).
     */
  public static final int DEFAULT_PORT = 4560;

  /**
     * The default reconnection delay (30000 milliseconds or 30 seconds).
     */
  public static final int DEFAULT_RECONNECTION_DELAY = 30000;

  /**
     * The default write timeout in milliseconds (0 means no write timeout).
     */
  public static final int DEFAULT_WRITE_TIMEOUT = 0;

  /**
     * Default size of the queue used to hold logging events that are destined
     * for the remote peer.
     * Assuming an average log entry to take 1k, this would result in the application
     * using about 10MB additional memory if the queue is full
     */
  public static final int DEFAULT_QUEUE_SIZE = DEFAULT_RING_BUFFER_SIZE;

  /**
     * Default timeout when waiting for the remote server to accept our
     * connection.
     */
  public static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

  public static final int DEFAULT_WRITE_BUFFER_SIZE = 8192;

  private static final NotConnectedException NOT_CONNECTED_EXCEPTION = new NotConnectedException();

  private static final ShutdownInProgressException SHUTDOWN_IN_PROGRESS_EXCEPTION = new ShutdownInProgressException();

  static {
    NOT_CONNECTED_EXCEPTION.setStackTrace(new StackTraceElement[] { new StackTraceElement(AbstractLogstashTcpSocketAppender.TcpSendingEventHandler.class.getName(), "onEvent(..)", null, -1) });
    SHUTDOWN_IN_PROGRESS_EXCEPTION.setStackTrace(new StackTraceElement[] { new StackTraceElement(AbstractLogstashTcpSocketAppender.TcpSendingEventHandler.class.getName(), "onEvent(..)", null, -1) });
  }

  /**
     * Destinations to which to attempt to send logs, in order of preference.
     * <p>
     * 
     * Logs are only sent to one destination at a time.
     * <p>
     * 
     * The interpretation of this list is up to the current {@link #connectionStrategy}.
     */
  private List<InetSocketAddress> destinations = new ArrayList<>(2);

  /**
     * When connected, this is the index into {@link #destinations}
     * to the currently connected destination.
     * <p>
     * 
     * When a connection has never been established, the value is 0.
     * <p>
     * 
     * When a connection has been established, but lost, the value is the
     * previously connected index.
     */
  private volatile int connectedDestinationIndex = 0;

  /**
     * When connected, this is the connected destination address.
     * When not connected, this is null.
     */
  private volatile InetSocketAddress connectedDestination;

  /**
     * Strategy used to determine to which destination to connect, and when to reconnect. 
     * Default is {@link PreferPrimaryDestinationConnectionStrategy}.
     */
  private DestinationConnectionStrategy connectionStrategy = new PreferPrimaryDestinationConnectionStrategy();

  /**
     * Time period for which to wait after a connection fails to a specific destination
     * before attempting to reconnect to that destination.
     * Default is {@value #DEFAULT_RECONNECTION_DELAY} milliseconds.
     */
  private Duration reconnectionDelay = new Duration(DEFAULT_RECONNECTION_DELAY);

  /**
     * Socket connection timeout in milliseconds. 
     */
  private int acceptConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

  /**
     * Human readable identifier of the client (used for logback status messages) 
     */
  private String peerId;

  /**
     * The encoder which is ultimately responsible for writing the event
     * to the socket's {@link java.io.OutputStream}.
     */
  private Encoder<Event> encoder;

  /**
     * The number of bytes available in the write buffer.
     * Defaults to {@value #DEFAULT_WRITE_BUFFER_SIZE}
     * 
     * If less than or equal to zero, buffering the output stream will be disabled.
     * If buffering is disabled, the writer thread can slow down, but
     * it will also can prevent dropping events in the buffer on flaky connections.
     */
  private int writeBufferSize = DEFAULT_WRITE_BUFFER_SIZE;

  /**
     * Used to create client {@link Socket}s to which to communicate.
     * 
     * If set prior to startup, it will be used.
     * <p>
     * 
     * If not set prior to startup, and {@link #sslConfiguration} is null,
     * then the default socket factory ({@link SocketFactory#getDefault()}) will be used.
     * <p>
     * 
     * If not set prior to startup, and {@link #sslConfiguration} is not null,
     * then a socket factory created from the
     * {@link SSLConfiguration#createContext(ch.qos.logback.core.spi.ContextAware)} will be used.
     */
  private SocketFactory socketFactory;

  /**
     * Set this to non-null to use SSL.
     * See <a href="http://logback.qos.ch/manual/usingSSL.html"> the logback manual</a>
     * for details on how to configure SSL for a client.
     */
  private SSLConfiguration sslConfiguration;

  /**
     * If this duration elapses without an event being sent,
     * then the {@link #keepAliveMessage} will be sent to the socket in
     * order to keep the connection alive.
     * 
     * When null (the default), no keepAlive messages will be sent.
     */
  private Duration keepAliveDuration;

  /**
     * Message to send for keeping the connection alive
     * if {@link #keepAliveDuration} is non-null.
     */
  private String keepAliveMessage = System.getProperty("line.separator");

  /**
     * The charset to use when writing the {@link #keepAliveMessage}.
     * Defaults to UTF-8.
     */
  private Charset keepAliveCharset = StandardCharsets.UTF_8;

  /**
     * The {@link #keepAliveMessage} translated to bytes using the {@link #keepAliveCharset}.
     * Populated at startup time.
     */
  private byte[] keepAliveBytes;

  /**
     * Time period for which to wait for a write to complete before timing out
     * and attempting to reconnect to that destination.
     * Zero (the default) means no write timeout.
     *
     * <p>Used to detect connections where the receiver stops reading.</p>
     *
     * <p>Note that since a blocking java socket output stream
     * does not have a concept of a write timeout,
     * a task will be scheduled on the {@link #getExecutorService()}
     * with the same frequency as the write timeout
     * in order to detect stuck writes.
     * It is recommended to use longer write timeouts (e.g. &gt; 30s, or minutes),
     * rather than short write timeouts, so that this task does not execute too frequently.
     * Also, this approach means that it could take up to two times the write timeout
     * before a write timeout is detected.</p>
     */
  private Duration writeTimeout = new Duration(DEFAULT_WRITE_TIMEOUT);

  /**
     * Used to signal the socket reconnect thread that the shutdown has occurred.
     * The latch will be non-zero when started, and zero when shutdown.  
     */
  private volatile CountDownLatch shutdownLatch;

  private class TcpSendingEventHandler implements EventHandler<LogEvent<Event>>, LifecycleAware {
    /**
         * Max number of consecutive failed connection attempts for which
         * logback status messages will be logged.
         * 
         * After this many failed attempts, reconnection will still
         * be attempted, but failures will not be logged again
         * (until after the connection is successful, and then fails again.)
         */
    private static final int MAX_REPEAT_CONNECTION_ERROR_LOG = 5;

    /**
         * Number of times we try to write an event before it is discarded.
         * Between each attempt, the socket will be reconnected.
         */
    private static final int MAX_REPEAT_WRITE_ATTEMPTS = 5;

    /**
         * The destination socket to which to send events.
         */
    private volatile Socket socket;

    /**
         * The destination output stream to which to send events.
         * If {@link AbstractLogstashTcpSocketAppender#writeBufferSize} is greater than zero, this will be a buffered wrapper of the socket output stream.
         * Otherwise, it will be the socket output stream.
         */
    private volatile OutputStream outputStream;

    /**
         * Time at which the last event send was started (e.g. before write/flush).
         * Used to detect write timeouts.
         */
    private volatile long lastSendStartNanoTime;

    /**
         * Time at which the last event send was completed (e.g. after write/flush).
         * Used to calculate if a keep alive message
         * needs to be scheduled/sent.
         */
    private volatile long lastSendEndNanoTime;

    /**
         * The most recent time that a connection to each destination was attempted.
         */
    private long[] destinationAttemptStartTimes;

    /**
         * Future for the currently scheduled {@link #keepAliveRunnable}.
         */
    private ScheduledFuture<?> keepAliveFuture;

    /**
         * See {@link KeepAliveRunnable}.
         * Initialized on startup if keep alive is enabled.
         */
    private KeepAliveRunnable keepAliveRunnable;

    /**
         * Future for the currently scheduled {@link #writeTimeoutRunnable}.
         */
    private ScheduledFuture<?> writeTimeoutFuture;

    /**
         * See {@link WriteTimeoutRunnable}.
         * Initialized on startup if write timeout is enabled.
         */
    private WriteTimeoutRunnable writeTimeoutRunnable;

    /**
         * See {@link ReaderCallable}.
         * Initialized when a socket is opened.
         */
    private Future<?> readerFuture;

    private class KeepAliveRunnable implements Runnable {
      private int previousDestinationIndex = connectedDestinationIndex;

      @Override public void run() {
        long lastSendEnd = lastSendEndNanoTime;
        long currentNanoTime = System.nanoTime();
        if (hasKeepAliveDurationElapsed(lastSendEnd, currentNanoTime)) {
          getDisruptor().getRingBuffer().tryPublishEvent(getEventTranslator(), null);
          scheduleKeepAlive(currentNanoTime);
        } else {
          scheduleKeepAlive(lastSendEnd);
        }
        if (previousDestinationIndex != connectedDestinationIndex) {
          updateCurrentThreadName();
        }
        previousDestinationIndex = connectedDestinationIndex;
      }
    }

    private class ReaderCallable implements Callable<Void> {
      private final InputStream inputStream;

      ReaderCallable(InputStream inputStream) {
        super();
        this.inputStream = inputStream;
      }

      @Override public Void call() throws Exception {
        updateCurrentThreadName();
        try {
          while (true) {
            try {
              if (inputStream.read() == -1) {
                return null;
              }
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
              throw e;
            }
          }
        }  finally {
          if (!Thread.currentThread().isInterrupted()) {
            getExecutorService().submit(() -> {
              getDisruptor().getRingBuffer().tryPublishEvent(getEventTranslator(), null);
            });
          }
        }
      }
    }

    private class WriteTimeoutRunnable implements Runnable {
      /**
             * The lastSendStartNanoTime of the last detected timeout.
             * Used to ensure we only detect a write timeout for a single write once
             * (especially if the log rate is very low).
             */
      private volatile long lastDetectedStartNanoTime;

      @Override public void run() {
        long lastSendStart = lastSendStartNanoTime;
        long lastSendEnd = lastSendEndNanoTime;
        if (lastSendStart > lastSendEnd && lastSendStart != lastDetectedStartNanoTime) {
          long elapsedSendTimeInMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - lastSendStart);
          if (elapsedSendTimeInMillis > writeTimeout.getMilliseconds()) {
            lastDetectedStartNanoTime = lastSendStart;
            addWarn(peerId + "Detected write timeout after " + elapsedSendTimeInMillis + "ms.  Write timeout=" + getWriteTimeout() + ".  Closing socket to force reconnect");
            closeSocket();
          }
        }
      }
    }

    @Override public void onEvent(LogEvent<Event> logEvent, long sequence, boolean endOfBatch) throws Exception {
      Exception sendFailureException = null;
      for (int i = 0; i < MAX_REPEAT_WRITE_ATTEMPTS; i++) {
        Socket socket = this.socket;
        OutputStream outputStream = this.outputStream;
        if (socket == null && (!isStarted() || Thread.currentThread().isInterrupted())) {
          sendFailureException = SHUTDOWN_IN_PROGRESS_EXCEPTION;
          break;
        }
        Future<?> readerFuture = this.readerFuture;
        if (readerFuture.isDone() || socket == null) {
          addInfo(peerId + "destination terminated the connection. Reconnecting.");
          reopenSocket();
          try {
            readerFuture.get();
            sendFailureException = NOT_CONNECTED_EXCEPTION;
          } catch (Exception e) {
            sendFailureException = e;
          }
          continue;
        }
        try {
          writeEvent(socket, outputStream, logEvent, endOfBatch);
          return;
        } catch (Exception e) {
          sendFailureException = e;
          addWarn(peerId + "unable to send event: " + e.getMessage() + " Reconnecting.", e);
          reopenSocket();
        }
      }
      if (logEvent.event != null) {
        fireEventSendFailure(logEvent.event, sendFailureException);
      }
    }

    private void writeEvent(Socket socket, OutputStream outputStream, LogEvent<Event> logEvent, boolean endOfBatch) throws IOException {
      long startWallTime = System.currentTimeMillis();
      long startNanoTime = System.nanoTime();
      lastSendStartNanoTime = startNanoTime;
      if (logEvent.event != null) {
        encode(logEvent.event, outputStream);
      } else {
        if (hasKeepAliveDurationElapsed(lastSendEndNanoTime, startNanoTime)) {
          outputStream.write(keepAliveBytes);
        }
      }
      if (endOfBatch) {
        outputStream.flush();
      }
      long endNanoTime = System.nanoTime();
      lastSendEndNanoTime = endNanoTime;
      if (logEvent.event != null) {
        fireEventSent(socket, logEvent.event, endNanoTime - startNanoTime);
      }
      if (connectionStrategy.shouldReconnect(startWallTime, connectedDestinationIndex, destinations.size())) {
        addInfo(peerId + "reestablishing connection.");
        outputStream.flush();
        reopenSocket();
      }
    }

    @SuppressWarnings(value = { "unchecked" }) private void encode(Event event, OutputStream outputStream) throws IOException {
      if (encoder instanceof StreamingEncoder) {
        ((StreamingEncoder<Event>) encoder).encode(event, outputStream);
      } else {
        byte[] data = encoder.encode(event);
        if (data != null) {
          outputStream.write(data);
        }
      }
    }

    private boolean hasKeepAliveDurationElapsed(long lastSentNanoTime, long currentNanoTime) {
      return isKeepAliveEnabled() && lastSentNanoTime + TimeUnit.MILLISECONDS.toNanos(keepAliveDuration.getMilliseconds()) < currentNanoTime;
    }

    @Override public void onStart() {
      this.destinationAttemptStartTimes = new long[destinations.size()];
      openSocket();
      scheduleKeepAlive(System.nanoTime());
      scheduleWriteTimeout();
    }

    @Override public void onShutdown() {
      unscheduleWriteTimeout();
      unscheduleKeepAlive();
      closeEncoder();
      closeSocket();
    }

    private synchronized void reopenSocket() {
      closeSocket();
      openSocket();
    }

    /**
         * Repeatedly tries to open a socket until it is successful,
         * or the hander is stopped, or the handler thread is interrupted.
         * 
         * If the socket is non-null when this method returns,
         * then it should be able to be used to send.
         */
    private synchronized void openSocket() {
      int errorCount = 0;
      int destinationIndex = connectedDestinationIndex;
      while (isStarted() && !Thread.currentThread().isInterrupted()) {
        destinationIndex = connectionStrategy.selectNextDestinationIndex(destinationIndex, destinations.size());
        long startWallTime = System.currentTimeMillis();
        Socket tempSocket = null;
        OutputStream tempOutputStream = null;
        InetSocketAddress currentDestination = destinations.get(destinationIndex);
        try {
          peerId = "Log destination " + currentDestination + ": ";
          final long millisSinceLastAttempt = startWallTime - destinationAttemptStartTimes[destinationIndex];
          if (millisSinceLastAttempt < reconnectionDelay.getMilliseconds()) {
            final long sleepTime = reconnectionDelay.getMilliseconds() - millisSinceLastAttempt;
            if (errorCount < MAX_REPEAT_CONNECTION_ERROR_LOG * destinations.size()) {
              addWarn(peerId + "Waiting " + sleepTime + "ms before attempting reconnection.");
            }
            try {
              shutdownLatch.await(sleepTime, TimeUnit.MILLISECONDS);
              if (!isStarted()) {
                return;
              }
            } catch (InterruptedException ie) {
              Thread.currentThread().interrupt();
              addWarn(peerId + "connection interrupted. Will no longer attempt reconnection.");
              return;
            }
            startWallTime = System.currentTimeMillis();
          }
          destinationAttemptStartTimes[destinationIndex] = startWallTime;
          tempSocket = socketFactory.createSocket();
          tempSocket.setSoTimeout(acceptConnectionTimeout);
          tempSocket.connect(new InetSocketAddress(getHostString(currentDestination), currentDestination.getPort()), acceptConnectionTimeout);
          if (tempSocket instanceof SSLSocket) {
            ((SSLSocket) tempSocket).startHandshake();
          }
          tempOutputStream = writeBufferSize > 0 ? new BufferedOutputStream(tempSocket.getOutputStream(), writeBufferSize) : tempSocket.getOutputStream();
          addInfo(peerId + "connection established.");
          this.socket = tempSocket;
          this.outputStream = tempOutputStream;
          boolean shouldUpdateThreadName = (destinationIndex != connectedDestinationIndex);
          connectedDestinationIndex = destinationIndex;
          connectedDestination = currentDestination;
          connectionStrategy.connectSuccess(startWallTime, destinationIndex, destinations.size());
          if (shouldUpdateThreadName) {
            updateCurrentThreadName();
          }
          this.readerFuture = scheduleReaderCallable(new ReaderCallable(tempSocket.getInputStream()));
          fireConnectionOpened(this.socket);
          return;
        } catch (Exception e) {
          CloseUtil.closeQuietly(tempOutputStream);
          CloseUtil.closeQuietly(tempSocket);
          connectionStrategy.connectFailed(startWallTime, destinationIndex, destinations.size());
          fireConnectionFailed(currentDestination, e);
          if (errorCount++ < MAX_REPEAT_CONNECTION_ERROR_LOG * destinations.size()) {
            addWarn(peerId + "connection failed.", e);
          }
        }
      }
    }

    private synchronized void closeSocket() {
      connectedDestination = null;
      CloseUtil.closeQuietly(outputStream);
      outputStream = null;
      CloseUtil.closeQuietly(socket);
      fireConnectionClosed(socket);
      socket = null;
      if (this.readerFuture != null) {
        this.readerFuture.cancel(true);
      }
    }

    private void closeEncoder() {
      encoder.stop();
    }

    private synchronized void scheduleKeepAlive(long basedOnNanoTime) {
      if (isKeepAliveEnabled() && !Thread.currentThread().isInterrupted()) {
        if (keepAliveRunnable == null) {
          keepAliveRunnable = new KeepAliveRunnable();
        }
        long delay = TimeUnit.MILLISECONDS.toNanos(keepAliveDuration.getMilliseconds()) - (System.nanoTime() - basedOnNanoTime);
        try {
          keepAliveFuture = getExecutorService().schedule(keepAliveRunnable, delay, TimeUnit.NANOSECONDS);
        } catch (RejectedExecutionException e) {
          keepAliveFuture = null;
        }
      }
    }

    private synchronized void unscheduleKeepAlive() {
      if (keepAliveFuture != null) {
        keepAliveFuture.cancel(true);
        try {
          keepAliveFuture.get();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (Exception e) {
        }
      }
    }

    private synchronized void scheduleWriteTimeout() {
      if (isWriteTimeoutEnabled() && !Thread.currentThread().isInterrupted()) {
        if (writeTimeoutRunnable == null) {
          writeTimeoutRunnable = new WriteTimeoutRunnable();
        }
        long delay = writeTimeout.getMilliseconds();
        try {
          writeTimeoutFuture = getExecutorService().scheduleWithFixedDelay(writeTimeoutRunnable, delay, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
          writeTimeoutFuture = null;
        }
      }
    }

    private synchronized void unscheduleWriteTimeout() {
      if (writeTimeoutFuture != null) {
        writeTimeoutFuture.cancel(true);
        try {
          writeTimeoutFuture.get();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (Exception e) {
        }
      }
    }
  }

  private static class UnconnectedConfigurableSSLSocketFactory extends ConfigurableSSLSocketFactory {
    private final SSLParametersConfiguration parameters;

    private final SSLSocketFactory delegate;

    UnconnectedConfigurableSSLSocketFactory(SSLParametersConfiguration parameters, SSLSocketFactory delegate) {
      super(parameters, delegate);
      this.parameters = parameters;
      this.delegate = delegate;
    }

    @Override public Socket createSocket() throws IOException {
      SSLSocket socket = (SSLSocket) delegate.createSocket();
      parameters.configure(new SSLConfigurableSocket(socket));
      return socket;
    }
  }

  public AbstractLogstashTcpSocketAppender() {
    super();
    setEventHandler(new TcpSendingEventHandler());
    setThreadNameFormat(DEFAULT_THREAD_NAME_FORMAT);
  }

  @Override public boolean isStarted() {
    CountDownLatch latch = this.shutdownLatch;
    return latch != null && latch.getCount() != 0;
  }

  @Override public synchronized void start() {
    if (isStarted()) {
      return;
    }
    int errorCount = 0;
    if (encoder == null) {
      errorCount++;
      addError("No encoder was configured. Use <encoder> to specify the fully qualified class name of the encoder to use");
    }
    if (destinations.isEmpty()) {
      errorCount++;
      addError("No destination was configured. Use <destination> to add one or more destinations to the appender");
    }
    if (errorCount == 0 && socketFactory == null) {
      if (sslConfiguration == null) {
        socketFactory = SocketFactory.getDefault();
      } else {
        try {
          SSLContext sslContext = getSsl().createContext(this);
          SSLParametersConfiguration parameters = getSsl().getParameters();
          parameters.setContext(getContext());
          socketFactory = new UnconnectedConfigurableSSLSocketFactory(parameters, sslContext.getSocketFactory());
        } catch (Exception e) {
          addError("Unable to create ssl context", e);
          errorCount++;
        }
      }
    }
    if (keepAliveMessage != null && keepAliveCharset != null) {
      keepAliveBytes = keepAliveMessage.getBytes(keepAliveCharset);
    }
    if (errorCount == 0) {
      encoder.setContext(getContext());
      if (!encoder.isStarted()) {
        encoder.start();
      }
      int threadPoolCoreSize = getThreadPoolCoreSize() + 1;
      if (keepAliveDuration != null) {
        threadPoolCoreSize++;
      }
      if (isWriteTimeoutEnabled()) {
        threadPoolCoreSize++;
      }
      setThreadPoolCoreSize(threadPoolCoreSize);
      this.shutdownLatch = new CountDownLatch(1);
      super.start();
    }
  }

  @Override public synchronized void stop() {
    if (!isStarted()) {
      return;
    }
    this.shutdownLatch.countDown();
    super.stop();
  }

  protected Future<?> scheduleReaderCallable(Callable<Void> readerCallable) {
    return getExecutorService().submit(readerCallable);
  }

  protected void fireEventSent(Socket socket, Event event, long durationInNanos) {
    for (Listener listener : listeners) {
      listener.eventSent(this, socket, event, durationInNanos);
    }
  }

  protected void fireEventSendFailure(Event event, Throwable reason) {
    for (Listener listener : listeners) {
      listener.eventSendFailure(this, event, reason);
    }
  }

  protected void fireConnectionOpened(Socket socket) {
    for (Listener listener : listeners) {
      listener.connectionOpened(this, socket);
    }
  }

  protected void fireConnectionClosed(Socket socket) {
    for (Listener listener : listeners) {
      listener.connectionClosed(this, socket);
    }
  }

  protected void fireConnectionFailed(InetSocketAddress address, Throwable throwable) {
    for (Listener listener : listeners) {
      listener.connectionFailed(this, address, throwable);
    }
  }

  public Encoder<Event> getEncoder() {
    return encoder;
  }

  public void setEncoder(Encoder<Event> encoder) {
    this.encoder = encoder;
  }

  public SocketFactory getSocketFactory() {
    return socketFactory;
  }

  /**
     * Used to create client {@link Socket}s to which to communicate.
     * By default, it is the system default SocketFactory.
     */
  public void setSocketFactory(SocketFactory socketFactory) {
    this.socketFactory = socketFactory;
  }

  /**
     * Adds the given destination (or destinations) to the list of potential destinations
     * to which to send logs.
     * <p>
     *  
     * The string is a comma separated list of destinations in the form of hostName[:portNumber].
     * <p>
     * If portNumber is not provided, then the configured {@link #port} will be used,
     * which defaults to {@value #DEFAULT_PORT}
     * <p>
     * 
     * For example, "host1.domain.com,host2.domain.com:5560"
     */
  public void addDestination(final String destination) throws IllegalArgumentException {
    List<InetSocketAddress> parsedDestinations = DestinationParser.parse(destination, DEFAULT_PORT);
    addDestinations(parsedDestinations.toArray(new InetSocketAddress[0]));
  }

  /**
     * Adds the given destinations to the list of potential destinations.
     */
  public void addDestinations(InetSocketAddress... destinations) throws IllegalArgumentException {
    if (destinations == null) {
      return;
    }
    for (InetSocketAddress destination : destinations) {
      try {
        InetAddress.getByName(getHostString(destination));
      } catch (UnknownHostException ex) {
        addWarn("Invalid destination \'" + getHostString(destination) + "\': host unknown (was \'" + getHostString(destination) + "\').");
      }
      this.destinations.add(destination);
    }
  }

  /**
     * Returns the host string from the given destination,
     * avoiding a DNS hit if possible.
     */
  protected String getHostString(InetSocketAddress destination) {
    return destination.getHostString();
  }

  protected void updateCurrentThreadName() {
    Thread.currentThread().setName(calculateThreadName());
  }

  @Override protected List<Object> getThreadNameFormatParams() {
    List<Object> superThreadNameFormatParams = super.getThreadNameFormatParams();
    List<Object> threadNameFormatParams = new ArrayList<Object>(superThreadNameFormatParams.size() + 2);
    threadNameFormatParams.addAll(superThreadNameFormatParams);
    InetSocketAddress currentDestination = this.destinations.get(connectedDestinationIndex);
    threadNameFormatParams.add(getHostString(currentDestination));
    threadNameFormatParams.add(currentDestination.getPort());
    return threadNameFormatParams;
  }

  /**
     * Return the destinations in which to attempt to send logs.
     */
  public List<InetSocketAddress> getDestinations() {
    return Collections.unmodifiableList(destinations);
  }

  /**
     * Time period for which to wait after failing to connect to all servers,
     * before attempting to reconnect.
     * Default is {@value #DEFAULT_RECONNECTION_DELAY} milliseconds.
     */
  public void setReconnectionDelay(Duration delay) {
    if (delay == null || delay.getMilliseconds() <= 0) {
      throw new IllegalArgumentException("reconnectionDelay must be > 0");
    }
    this.reconnectionDelay = delay;
  }

  public Duration getReconnectionDelay() {
    return reconnectionDelay;
  }

  /**
     * Convenience method for setting {@link PreferPrimaryDestinationConnectionStrategy#setSecondaryConnectionTTL(Duration)}.
     * 
     * When the {@link #connectionStrategy} is a {@link PreferPrimaryDestinationConnectionStrategy},
     * this will set its {@link PreferPrimaryDestinationConnectionStrategy#setSecondaryConnectionTTL(Duration)}.
     * 
     * @see PreferPrimaryDestinationConnectionStrategy#setSecondaryConnectionTTL(Duration)
     * @throws IllegalStateException if the {@link #connectionStrategy} is not a {@link PreferPrimaryDestinationConnectionStrategy}
     */
  public void setSecondaryConnectionTTL(Duration secondaryConnectionTTL) {
    if (connectionStrategy instanceof PreferPrimaryDestinationConnectionStrategy) {
      ((PreferPrimaryDestinationConnectionStrategy) connectionStrategy).setSecondaryConnectionTTL(secondaryConnectionTTL);
    } else {
      throw new IllegalStateException(String.format("When setting the secondaryConnectionTTL, the strategy must be a %s.  It is currently a %s", PreferPrimaryDestinationConnectionStrategy.class, connectionStrategy));
    }
  }

  public Duration getSecondaryConnectionTTL() {
    if (connectionStrategy instanceof PreferPrimaryDestinationConnectionStrategy) {
      return ((PreferPrimaryDestinationConnectionStrategy) connectionStrategy).getSecondaryConnectionTTL();
    }
    return null;
  }

  /**
     * Socket connection timeout in milliseconds. 
     */
  void setAcceptConnectionTimeout(int acceptConnectionTimeout) {
    this.acceptConnectionTimeout = acceptConnectionTimeout;
  }

  public int getWriteBufferSize() {
    return writeBufferSize;
  }

  /**
     * The number of bytes available in the write buffer.
     * Defaults to {@value #DEFAULT_WRITE_BUFFER_SIZE}
     * 
     * If less than or equal to zero, buffering the output stream will be disabled.
     * If buffering is disabled, the writer thread can slow down, but
     * it will also can prevent dropping events in the buffer on flaky connections.
     */
  public void setWriteBufferSize(int writeBufferSize) {
    this.writeBufferSize = writeBufferSize;
  }

  /**
     * Returns the maximum number of events in the queue.
     */
  public int getQueueSize() {
    return getRingBufferSize();
  }

  /**
     * Sets the maximum number of events in the queue. Once the queue is full
     * additional events will be dropped.
     * 
     * <p>
     * Must be a positive power of 2.
     *
     * @param queueSize the maximum number of entries in the queue.
     */
  public void setQueueSize(int queueSize) {
    setRingBufferSize(queueSize);
  }

  public SSLConfiguration getSsl() {
    return sslConfiguration;
  }

  /**
     * Set this to non-null to use SSL.
     * See <a href="http://logback.qos.ch/manual/usingSSL.html"> the logback manual</a>
     * for details on how to configure SSL for a client.
     */
  public void setSsl(SSLConfiguration sslConfiguration) {
    this.sslConfiguration = sslConfiguration;
  }

  public Duration getKeepAliveDuration() {
    return keepAliveDuration;
  }

  /**
     * If this duration elapses without an event being sent,
     * then the {@link #keepAliveMessage} will be sent to the socket in
     * order to keep the connection alive.
     * 
     * When null, no keepAlive messages will be sent.
     */
  public void setKeepAliveDuration(Duration keepAliveDuration) {
    this.keepAliveDuration = keepAliveDuration;
  }

  public String getKeepAliveMessage() {
    return keepAliveMessage;
  }

  /**
     * Message to send for keeping the connection alive
     * if {@link #keepAliveDuration} is non-null.
     * 
     * The following values have special meaning:
     * <ul>
     * <li><tt>null</tt> or empty string = no keep alive.</li>
     * <li>"<tt>SYSTEM</tt>" = operating system new line (default).</li>
     * <li>"<tt>UNIX</tt>" = unix line ending (\n).</li>
     * <li>"<tt>WINDOWS</tt>" = windows line ending (\r\n).</li>
     * </ul>
     * <p>
     * Any other value will be used as-is.
     */
  public void setKeepAliveMessage(String keepAliveMessage) {
    this.keepAliveMessage = SeparatorParser.parseSeparator(keepAliveMessage);
  }

  public boolean isKeepAliveEnabled() {
    return this.keepAliveDuration != null && this.keepAliveMessage != null;
  }

  public boolean isWriteTimeoutEnabled() {
    return this.writeTimeout.getMilliseconds() > 0;
  }

  public Charset getKeepAliveCharset() {
    return keepAliveCharset;
  }

  /**
     * The charset to use when writing the {@link #keepAliveMessage}.
     * Defaults to UTF-8.
     */
  public void setKeepAliveCharset(Charset keepAliveCharset) {
    this.keepAliveCharset = keepAliveCharset;
  }

  /**
     * Pattern used by the to set the handler thread name.
     * Defaults to {@value #DEFAULT_THREAD_NAME_FORMAT}.
     * <p>
     * 
     * If you change the {@link #threadFactory}, then this
     * value may not be honored.
     * <p>
     * 
     * The string is a format pattern understood by {@link Formatter#format(String, Object...)}.
     * {@link Formatter#format(String, Object...)} is used to
     * construct the actual thread name prefix.
     * The first argument (%1$s) is the string appender name.
     * The second argument (%2$d) is the numerical thread index.
     * The third argument (%3$s) is the string hostname of the currently connected destination.
     * The fourth argument (%4$d) is the numerical port of the currently connected destination.
     * Other arguments can be made available by subclasses.
     */
  @Override public void setThreadNameFormat(String threadNameFormat) {
    super.setThreadNameFormat(threadNameFormat);
  }

  public DestinationConnectionStrategy getConnectionStrategy() {
    return connectionStrategy;
  }

  @DefaultClass(value = DelegateDestinationConnectionStrategy.class) public void setConnectionStrategy(DestinationConnectionStrategy destinationConnectionStrategy) {
    this.connectionStrategy = destinationConnectionStrategy;
  }

  /**
     * Returns the currently connected destination as an {@link Optional}.
     * The {@link Optional} will be absent if the appender is not currently connected.
     *
     * @return the currently connected destination as an {@link Optional}.
     *         The {@link Optional} will be absent if the appender is not currently connected.
     */
  public Optional<InetSocketAddress> getConnectedDestination() {
    return Optional.ofNullable(this.connectedDestination);
  }

  public Duration getWriteTimeout() {
    return writeTimeout;
  }

  /**
     * Sets the time period for which to wait for a write to complete before timing out
     * and attempting to reconnect to that destination.
     * Zero (the default) means no write timeout.
     *
     * <p>Used to detect connections where the receiver stops reading.</p>
     *
     * <p>Note that since a blocking java socket output stream
     * does not have a concept of a write timeout,
     * a task will be scheduled on the {@link #getExecutorService()}
     * with the same frequency as the write timeout
     * in order to detect stuck writes.
     * It is recommended to use longer write timeouts (e.g. &gt; 30s, or minutes),
     * rather than short write timeouts, so that this task does not execute too frequently.
     * Also, this approach means that it could take up to two times the write timeout
     * before a write timeout is detected.</p>
     */
  public void setWriteTimeout(Duration writeTimeout) {
    this.writeTimeout = writeTimeout == null ? new Duration(DEFAULT_WRITE_TIMEOUT) : writeTimeout;
  }
}