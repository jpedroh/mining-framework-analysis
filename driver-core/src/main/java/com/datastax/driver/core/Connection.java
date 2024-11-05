package com.datastax.driver.core;
import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.Uninterruptibles;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.core.exceptions.AuthenticationException;
import com.datastax.driver.core.exceptions.DriverInternalError;

class Connection {
  private static final Logger logger = LoggerFactory.getLogger(Connection.class);

  private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

  public final InetSocketAddress address;

  private final String name;

  private final Channel channel;

  private final Factory factory;

  private final Dispatcher dispatcher = new Dispatcher();

  public final AtomicInteger inFlight = new AtomicInteger(0);

  private final AtomicInteger writer = new AtomicInteger(0);

  private volatile String keyspace;

  private volatile boolean isInitialized;

  private volatile boolean isDefunct;

  private final AtomicReference<ConnectionCloseFuture> closeFuture = new AtomicReference<ConnectionCloseFuture>();

  private final Object terminationLock = new Object();

  protected Connection(String name, InetSocketAddress address, Factory factory) throws ConnectionException, InterruptedException, UnsupportedProtocolVersionException, ClusterNameMismatchException {
    this.address = address;
    this.factory = factory;
    this.dispatcher = new Dispatcher();
    this.name = name;
    ClientBootstrap bootstrap = factory.newBootstrap();
    ProtocolOptions protocolOptions = factory.configuration.getProtocolOptions();
    ProtocolVersion protocolVersion = factory.protocolVersion == null ? ProtocolVersion.NEWEST_SUPPORTED : factory.protocolVersion;
    bootstrap.setPipelineFactory(new PipelineFactory(this, protocolVersion, protocolOptions.getCompression().compressor, protocolOptions.getSSLOptions()));
    ChannelFuture future = bootstrap.connect(address);
    writer.incrementAndGet();
    try {
      this.channel = future.awaitUninterruptibly().getChannel();
      this.factory.allChannels.add(this.channel);
      if (!future.isSuccess()) {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("%s Error connecting to %s%s", this, address, extractMessage(future.getCause())));
        }
        throw defunct(new TransportException(address, "Cannot connect", future.getCause()));
      }
    }  finally {
      writer.decrementAndGet();
    }
    logger.trace("{} Connection opened successfully", this);
    initializeTransport(protocolVersion, factory.manager.metadata.clusterName);
    logger.debug("{} Transport initialized and ready", this);
    isInitialized = true;
  }

  private static String extractMessage(Throwable t) {
    if (t == null) {
      return "";
    }
    String msg = t.getMessage() == null || t.getMessage().isEmpty() ? t.toString() : t.getMessage();
    return " (" + msg + ')';
  }

  private void initializeTransport(ProtocolVersion version, String clusterName) throws ConnectionException, InterruptedException, UnsupportedProtocolVersionException, ClusterNameMismatchException {
    try {
      ProtocolOptions.Compression compression = factory.configuration.getProtocolOptions().getCompression();
      Message.Response response = write(new Requests.Startup(compression)).get();
      switch (response.type) {
        case READY:
        break;
        case ERROR:
        Responses.Error error = (Responses.Error) response;
        if (error.code == ExceptionCode.PROTOCOL_ERROR && error.message.contains("Invalid or unsupported protocol version")) {
          throw unsupportedProtocolVersionException(version, error.serverProtocolVersion);
        }
        throw defunct(new TransportException(address, String.format("Error initializing connection: %s", error.message)));
        case AUTHENTICATE:
        Authenticator authenticator = factory.authProvider.newAuthenticator(address);
        switch (version) {
          case V1:
          if (authenticator instanceof ProtocolV1Authenticator) {
            authenticateV1(authenticator);
          } else {
            authenticateV2(authenticator);
          }
          break;
          case V2:
          case V3:
          authenticateV2(authenticator);
          break;
          default:
          throw defunct(version.unsupported());
        }
        break;
        default:
        throw defunct(new TransportException(address, String.format("Unexpected %s response message from server to a STARTUP message", response.type)));
      }
      checkClusterName(version, clusterName);
    } catch (BusyConnectionException e) {
      throw defunct(new DriverInternalError("Newly created connection should not be busy"));
    } catch (ExecutionException e) {
      throw defunct(new ConnectionException(address, String.format("Unexpected error during transport initialization (%s)", e.getCause()), e.getCause()));
    }
  }

  private UnsupportedProtocolVersionException unsupportedProtocolVersionException(ProtocolVersion triedVersion, ProtocolVersion serverProtocolVersion) {
    logger.debug("Got unsupported protocol version error from {} for version {} server supports version {}", address, triedVersion, serverProtocolVersion);
    UnsupportedProtocolVersionException exc = new UnsupportedProtocolVersionException(address, triedVersion, serverProtocolVersion);
    defunct(new TransportException(address, "Cannot initialize transport", exc));
    return exc;
  }

  private void authenticateV1(Authenticator authenticator) throws ConnectionException, BusyConnectionException, ExecutionException, InterruptedException {
    Requests.Credentials creds = new Requests.Credentials(((ProtocolV1Authenticator) authenticator).getCredentials());
    Message.Response authResponse = write(creds).get();
    switch (authResponse.type) {
      case READY:
      break;
      case ERROR:
      throw defunct(new AuthenticationException(address, ((Responses.Error) authResponse).message));
      default:
      throw defunct(new TransportException(address, String.format("Unexpected %s response message from server to a CREDENTIALS message", authResponse.type)));
    }
  }

  private void authenticateV2(Authenticator authenticator) throws ConnectionException, BusyConnectionException, ExecutionException, InterruptedException {
    byte[] initialResponse = authenticator.initialResponse();
    if (null == initialResponse) {
      initialResponse = EMPTY_BYTE_ARRAY;
    }
    Message.Response authResponse = write(new Requests.AuthResponse(initialResponse)).get();
    waitForAuthCompletion(authResponse, authenticator);
  }

  private void waitForAuthCompletion(Message.Response authResponse, Authenticator authenticator) throws ConnectionException, BusyConnectionException, ExecutionException, InterruptedException {
    switch (authResponse.type) {
      case AUTH_SUCCESS:
      logger.trace("{} Authentication complete", this);
      authenticator.onAuthenticationSuccess(((Responses.AuthSuccess) authResponse).token);
      break;
      case AUTH_CHALLENGE:
      byte[] responseToServer = authenticator.evaluateChallenge(((Responses.AuthChallenge) authResponse).token);
      if (responseToServer == null) {
        logger.trace("{} Authentication complete (No response to server)", this);
        return;
      } else {
        logger.trace("{} Sending Auth response to challenge", this);
        waitForAuthCompletion(write(new Requests.AuthResponse(responseToServer)).get(), authenticator);
      }
      break;
      case ERROR:
      String message = ((Responses.Error) authResponse).message;
      if (message.startsWith("java.lang.ArrayIndexOutOfBoundsException: 15")) {
        message = String.format("Cannot use authenticator %s with protocol version 1, " + "only plain text authentication is supported with this protocol version", authenticator);
      }
      throw defunct(new AuthenticationException(address, message));
      default:
      throw defunct(new TransportException(address, String.format("Unexpected %s response message from server to authentication message", authResponse.type)));
    }
  }

  private void checkClusterName(ProtocolVersion version, String expected) throws ClusterNameMismatchException, ConnectionException, BusyConnectionException, ExecutionException, InterruptedException {
    if (expected == null) {
      return;
    }
    DefaultResultSetFuture future = new DefaultResultSetFuture(null, version, new Requests.Query("select cluster_name from system.local"));
    write(future);
    Row row = future.get().one();
    String actual = row.getString("cluster_name");
    if (!expected.equals(actual)) {
      throw new ClusterNameMismatchException(address, actual, expected);
    }
  }

  public boolean isDefunct() {
    return isDefunct;
  }

  public int maxAvailableStreams() {
    return dispatcher.streamIdHandler.maxAvailableStreams();
  }

  <E extends Exception> E defunct(E e) {
    if (logger.isDebugEnabled()) {
      logger.debug("Defuncting connection to " + address, e);
    }
    isDefunct = true;
    ConnectionException ce = e instanceof ConnectionException ? (ConnectionException) e : new ConnectionException(address, "Connection problem", e);
    Host host = factory.manager.metadata.getHost(address);
    if (host != null) {
      boolean isDown = factory.manager.signalConnectionFailure(host, ce, host.wasJustAdded(), isInitialized);
      notifyOwnerWhenDefunct(isDown);
    }
    closeAsync().force();
    return e;
  }

  protected void notifyOwnerWhenDefunct(boolean hostIsDown) {
  }

  public String keyspace() {
    return keyspace;
  }

  public void setKeyspace(String keyspace) throws ConnectionException {
    if (keyspace == null) {
      return;
    }
    if (this.keyspace != null && this.keyspace.equals(keyspace)) {
      return;
    }
    try {
      logger.trace("{} Setting keyspace {}", this, keyspace);
      long timeout = factory.getConnectTimeoutMillis();
      Future future = write(new Requests.Query("USE \"" + keyspace + '\"'));
      Message.Response response = Uninterruptibles.getUninterruptibly(future, timeout, TimeUnit.MILLISECONDS);
      switch (response.type) {
        case RESULT:
        this.keyspace = keyspace;
        break;
        default:
        defunct(new ConnectionException(address, String.format("Problem while setting keyspace, got %s as response", response)));
        break;
      }
    } catch (ConnectionException e) {
      throw defunct(e);
    } catch (TimeoutException e) {
      logger.warn(String.format("Timeout while setting keyspace on connection to %s. This should not happen but is not critical (it will retried)", address));
    } catch (BusyConnectionException e) {
      logger.warn(String.format("Tried to set the keyspace on busy connection to %s. This should not happen but is not critical (it will retried)", address));
    } catch (ExecutionException e) {
      throw defunct(new ConnectionException(address, "Error while setting keyspace", e));
    }
  }

  public Future write(Message.Request request) throws ConnectionException, BusyConnectionException {
    Future future = new Future(request);
    write(future);
    return future;
  }

  public ResponseHandler write(ResponseCallback callback) throws ConnectionException, BusyConnectionException {
    Message.Request request = callback.request();
    ResponseHandler handler = new ResponseHandler(this, callback);
    dispatcher.add(handler);
    request.setStreamId(handler.streamId);
    if (isDefunct) {
      dispatcher.removeHandler(handler.streamId, true);
      throw new ConnectionException(address, "Write attempt on defunct connection");
    }
    if (isClosed()) {
      dispatcher.removeHandler(handler.streamId, true);
      throw new ConnectionException(address, "Connection has been closed");
    }
    logger.trace("{} writing request {}", this, request);
    writer.incrementAndGet();
    channel.write(request).addListener(writeHandler(request, handler));
    return handler;
  }

  private ChannelFutureListener writeHandler(final Message.Request request, final ResponseHandler handler) {
    return new ChannelFutureListener() {
      @Override public void operationComplete(ChannelFuture writeFuture) {
        writer.decrementAndGet();
        if (!writeFuture.isSuccess()) {
          logger.debug("{} Error writing request {}", Connection.this, request);
          dispatcher.removeHandler(handler.streamId, true);
          ConnectionException ce;
          if (writeFuture.getCause() instanceof java.nio.channels.ClosedChannelException) {
            ce = new TransportException(address, "Error writing: Closed channel");
          } else {
            ce = new TransportException(address, "Error writing", writeFuture.getCause());
          }
          handler.callback.onException(Connection.this, defunct(ce), System.nanoTime() - handler.startTime, handler.retryCount);
        } else {
          logger.trace("{} request sent successfully", Connection.this);
        }
      }
    };
  }

  public boolean isClosed() {
    return closeFuture.get() != null;
  }

  public CloseFuture closeAsync() {
    ConnectionCloseFuture future = new ConnectionCloseFuture();
    if (!closeFuture.compareAndSet(null, future)) {
      return closeFuture.get();
    }
    logger.debug("{} closing connection", this);
    boolean terminated = terminate(false, false);
    if (!terminated) {
      factory.reaper.register(this);
    }
    return future;
  }

  boolean terminate(boolean evenIfPending, boolean logWarnings) {
    assert isClosed();
    ConnectionCloseFuture future = closeFuture.get();
    if (future.isDone()) {
      logger.debug("{} has already terminated", this);
      return true;
    } else {
      synchronized (terminationLock) {
        if (evenIfPending || dispatcher.pending.isEmpty()) {
          if (logWarnings) {
            logger.warn("Forcing termination of {}. This should not happen and is likely a bug, please report.", this);
          }
          future.force();
          return true;
        } else {
          logger.debug("Not terminating {}: there are still pending requests", this);
          return false;
        }
      }
    }
  }

  @Override public String toString() {
    return String.format("Connection[%s, inFlight=%d, closed=%b]", name, inFlight.get(), isClosed());
  }

  public static class Factory {
    private final ExecutorService bossExecutor = Executors.newCachedThreadPool();

    private final ExecutorService workerExecutor = Executors.newCachedThreadPool();

    public final HashedWheelTimer timer = new HashedWheelTimer(new ThreadFactoryBuilder().setNameFormat("Timeouter-%d").build());

    private final ChannelFactory channelFactory = new NioClientSocketChannelFactory(bossExecutor, workerExecutor);

    private final ChannelGroup allChannels = new DefaultChannelGroup();

    private final ConcurrentMap<Host, AtomicInteger> idGenerators = new ConcurrentHashMap<Host, AtomicInteger>();

    public final DefaultResponseHandler defaultHandler;

    final Cluster.Manager manager;

    final Cluster.ConnectionReaper reaper;

    public final Configuration configuration;

    public final AuthProvider authProvider;

    private volatile boolean isShutdown;

    volatile ProtocolVersion protocolVersion;

    Factory(Cluster.Manager manager, Configuration configuration) {
      this.defaultHandler = manager;
      this.manager = manager;
      this.reaper = manager.reaper;
      this.configuration = configuration;
      this.authProvider = configuration.getProtocolOptions().getAuthProvider();
      this.protocolVersion = configuration.getProtocolOptions().initialProtocolVersion;
    }

    public int getPort() {
      return configuration.getProtocolOptions().getPort();
    }

    public Connection open(Host host) throws ConnectionException, InterruptedException, UnsupportedProtocolVersionException, ClusterNameMismatchException {
      InetSocketAddress address = host.getSocketAddress();
      if (isShutdown) {
        throw new ConnectionException(address, "Connection factory is shut down");
      }
      String name = address.toString() + '-' + getIdGenerator(host).getAndIncrement();
      return new Connection(name, address, this);
    }

    public PooledConnection open(HostConnectionPool pool) throws ConnectionException, InterruptedException, UnsupportedProtocolVersionException, ClusterNameMismatchException {
      InetSocketAddress address = pool.host.getSocketAddress();
      if (isShutdown) {
        throw new ConnectionException(address, "Connection factory is shut down");
      }
      String name = address.toString() + '-' + getIdGenerator(pool.host).getAndIncrement();
      return new PooledConnection(name, address, this, pool);
    }

    private AtomicInteger getIdGenerator(Host host) {
      AtomicInteger g = idGenerators.get(host);
      if (g == null) {
        g = new AtomicInteger(1);
        AtomicInteger old = idGenerators.putIfAbsent(host, g);
        if (old != null) {
          g = old;
        }
      }
      return g;
    }

    public long getConnectTimeoutMillis() {
      return configuration.getSocketOptions().getConnectTimeoutMillis();
    }

    public long getReadTimeoutMillis() {
      return configuration.getSocketOptions().getReadTimeoutMillis();
    }

    private ClientBootstrap newBootstrap() {
      ClientBootstrap b = new ClientBootstrap(channelFactory);
      SocketOptions options = configuration.getSocketOptions();
      b.setOption("connectTimeoutMillis", options.getConnectTimeoutMillis());
      Boolean keepAlive = options.getKeepAlive();
      if (keepAlive != null) {
        b.setOption("keepAlive", keepAlive);
      }
      Boolean reuseAddress = options.getReuseAddress();
      if (reuseAddress != null) {
        b.setOption("reuseAddress", reuseAddress);
      }
      Integer soLinger = options.getSoLinger();
      if (soLinger != null) {
        b.setOption("soLinger", soLinger);
      }
      Boolean tcpNoDelay = options.getTcpNoDelay();
      if (tcpNoDelay != null) {
        b.setOption("tcpNoDelay", tcpNoDelay);
      }
      Integer receiveBufferSize = options.getReceiveBufferSize();
      if (receiveBufferSize != null) {
        b.setOption("receiveBufferSize", receiveBufferSize);
      }
      Integer sendBufferSize = options.getSendBufferSize();
      if (sendBufferSize != null) {
        b.setOption("sendBufferSize", sendBufferSize);
      }
      return b;
    }

    public void shutdown() {
      isShutdown = true;
      allChannels.close().awaitUninterruptibly();
      channelFactory.releaseExternalResources();
      timer.stop();
    }
  }

  private class Dispatcher extends SimpleChannelUpstreamHandler {
    public final StreamIdGenerator streamIdHandler = new StreamIdGenerator();

    private final ConcurrentMap<Integer, ResponseHandler> pending = new ConcurrentHashMap<Integer, ResponseHandler>();

    Dispatcher() {
      ProtocolVersion protocolVersion = factory.protocolVersion;
      if (protocolVersion == null) {
        assert !(Connection.this instanceof PooledConnection);
        protocolVersion = ProtocolVersion.V2;
      }
      streamIdHandler = StreamIdGenerator.newInstance(protocolVersion);
    }

    public void add(ResponseHandler handler) {
      ResponseHandler old = pending.put(handler.streamId, handler);
      assert old == null;
    }

    public void removeHandler(int streamId, boolean releaseStreamId) {
      if (!releaseStreamId) {
        streamIdHandler.mark(streamId);
      }
      ResponseHandler handler = pending.remove(streamId);
      if (handler != null) {
        handler.cancelTimeout();
      }
      if (releaseStreamId) {
        streamIdHandler.release(streamId);
      }
      if (isClosed()) {
        terminate(false, false);
      }
    }

    @Override public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
      if (!(e.getMessage() instanceof Message.Response)) {
        String msg = asDebugString(e.getMessage());
        logger.error("{} Received unexpected message: {}", Connection.this, msg);
        defunct(new TransportException(address, "Unexpected message received: " + msg));
      } else {
        Message.Response response = (Message.Response) e.getMessage();
        int streamId = response.getStreamId();
        logger.trace("{} received: {}", Connection.this, e.getMessage());
        if (streamId < 0) {
          factory.defaultHandler.handle(response);
          return;
        }
        ResponseHandler handler = pending.remove(streamId);
        streamIdHandler.release(streamId);
        if (handler == null) {
          streamIdHandler.unmark(streamId);
          if (logger.isDebugEnabled()) {
            logger.debug("{} Response received on stream {} but no handler set anymore (either the request has " + "timed out or it was closed due to another error). Received message is {}", Connection.this, streamId, asDebugString(response));
          }
          return;
        }
        handler.cancelTimeout();
        handler.callback.onSet(Connection.this, response, System.nanoTime() - handler.startTime, handler.retryCount);
        if (isClosed()) {
          terminate(false, false);
        }
      }
    }

    private String asDebugString(Object obj) {
      if (obj == null) {
        return "null";
      }
      String msg = obj.toString();
      if (msg.length() < 500) {
        return msg;
      }
      return msg.substring(0, 500) + "... [message of size " + msg.length() + " truncated]";
    }

    @Override public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
      if (logger.isDebugEnabled()) {
        logger.debug(String.format("%s connection error", Connection.this), e.getCause());
      }
      if (writer.get() > 0) {
        return;
      }
      defunct(new TransportException(address, String.format("Unexpected exception triggered (%s)", e.getCause()), e.getCause()));
    }

    public void errorOutAllHandler(ConnectionException ce) {
      Iterator<ResponseHandler> iter = pending.values().iterator();
      while (iter.hasNext()) {
        ResponseHandler handler = iter.next();
        handler.cancelTimeout();
        handler.callback.onException(Connection.this, ce, System.nanoTime() - handler.startTime, handler.retryCount);
        iter.remove();
      }
    }

    @Override public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
      if (!isInitialized || isClosed()) {
        errorOutAllHandler(new TransportException(address, "Channel has been closed"));
        Connection.this.closeAsync().force();
      } else {
        defunct(new TransportException(address, "Channel has been closed"));
      }
    }
  }

  private class ConnectionCloseFuture extends CloseFuture {
    @Override public ConnectionCloseFuture force() {
      if (channel == null) {
        set(null);
        return this;
      }
      dispatcher.errorOutAllHandler(new TransportException(address, "Connection has been closed"));
      ChannelFuture future = channel.close();
      future.addListener(new ChannelFutureListener() {
        public void operationComplete(ChannelFuture future) {
          if (future.getCause() != null) {
            ConnectionCloseFuture.this.setException(future.getCause());
          } else {
            ConnectionCloseFuture.this.set(null);
          }
        }
      });
      return this;
    }
  }

  static class Future extends AbstractFuture<Message.Response> implements RequestHandler.Callback {
    private final Message.Request request;

    private volatile InetSocketAddress address;

    public Future(Message.Request request) {
      this.request = request;
    }

    @Override public void register(RequestHandler handler) {
    }

    @Override public Message.Request request() {
      return request;
    }

    @Override public void onSet(Connection connection, Message.Response response, ExecutionInfo info, Statement statement, long latency) {
      onSet(connection, response, latency, 0);
    }

    public InetSocketAddress getAddress() {
      return address;
    }

    @Override public int retryCount() {
      return 0;
    }

    @Override public void onSet(Connection connection, Message.Response response, long latency, int retryCount) {
      this.address = connection.address;
      super.set(response);
    }

    @Override public void onException(Connection connection, Exception exception, long latency, int retryCount) {
      if (connection != null) {
        this.address = connection.address;
      }
      super.setException(exception);
    }

    @Override public boolean onTimeout(Connection connection, long latency, int retryCount) {
      assert connection != null;
      this.address = connection.address;
      super.setException(new ConnectionException(connection.address, "Operation timed out"));
      return true;
    }
  }

  interface ResponseCallback {
    public Message.Request request();

    public int retryCount();

    public void onSet(Connection connection, Message.Response response, long latency, int retryCount);

    public void onException(Connection connection, Exception exception, long latency, int retryCount);

    public boolean onTimeout(Connection connection, long latency, int retryCount);
  }

  static class ResponseHandler {
    public final Connection connection;

    public final int streamId;

    public final ResponseCallback callback;

    private final Timeout timeout;

    private final long startTime;

    public ResponseHandler(Connection connection, ResponseCallback callback) throws BusyConnectionException {
      this.connection = connection;
      this.streamId = connection.dispatcher.streamIdHandler.next();
      this.callback = callback;
      this.retryCount = callback.retryCount();
      long timeoutMs = connection.factory.getReadTimeoutMillis();
      this.timeout = timeoutMs <= 0 ? null : connection.factory.timer.newTimeout(onTimeoutTask(), timeoutMs, TimeUnit.MILLISECONDS);
      this.startTime = System.nanoTime();
    }

    void cancelTimeout() {
      if (timeout != null) {
        timeout.cancel();
      }
    }

    public void cancelHandler() {
      connection.dispatcher.removeHandler(streamId, false);
      if (connection instanceof PooledConnection) {
        ((PooledConnection) connection).release();
      }
    }

    private TimerTask onTimeoutTask() {
      return new TimerTask() {
        @Override public void run(Timeout timeout) {
          if (callback.onTimeout(connection, System.nanoTime() - startTime, retryCount)) {
            cancelHandler();
          }
        }
      };
    }

    public final int retryCount;
  }

  public interface DefaultResponseHandler {
    public void handle(Message.Response response);
  }

  private static class PipelineFactory implements ChannelPipelineFactory {
    private static final Message.ProtocolDecoder messageDecoder = new Message.ProtocolDecoder();

    private static final Message.ProtocolEncoder messageEncoderV1 = new Message.ProtocolEncoder(ProtocolVersion.V1);

    private static final Message.ProtocolEncoder messageEncoderV2 = new Message.ProtocolEncoder(ProtocolVersion.V2);

    private static final Message.ProtocolEncoder messageEncoderV3 = new Message.ProtocolEncoder(ProtocolVersion.V3);

    private static final Frame.Encoder frameEncoder = new Frame.Encoder();

    private final ProtocolVersion protocolVersion;

    private final Connection connection;

    private final FrameCompressor compressor;

    private final SSLOptions sslOptions;

    public PipelineFactory(Connection connection, ProtocolVersion protocolVersion, FrameCompressor compressor, SSLOptions sslOptions) {
      this.connection = connection;
      this.protocolVersion = protocolVersion;
      this.compressor = compressor;
      this.sslOptions = sslOptions;
    }

    @Override public ChannelPipeline getPipeline() throws Exception {
      ChannelPipeline pipeline = Channels.pipeline();
      if (sslOptions != null) {
        SSLEngine engine = sslOptions.context.createSSLEngine();
        engine.setUseClientMode(true);
        engine.setEnabledCipherSuites(sslOptions.cipherSuites);
        SslHandler handler = new SslHandler(engine);
        handler.setCloseOnSSLException(true);
        pipeline.addLast("ssl", handler);
      }
      pipeline.addLast("frameDecoder", new Frame.Decoder());
      pipeline.addLast("frameEncoder", frameEncoder);
      if (compressor != null) {
        pipeline.addLast("frameDecompressor", new Frame.Decompressor(compressor));
        pipeline.addLast("frameCompressor", new Frame.Compressor(compressor));
      }
      pipeline.addLast("messageDecoder", messageDecoder);
      pipeline.addLast("messageEncoder", messageEncoderFor(protocolVersion));
      pipeline.addLast("dispatcher", connection.dispatcher);
      return pipeline;
    }

    private Message.ProtocolEncoder messageEncoderFor(ProtocolVersion version) {
      switch (version) {
        case V1:
        return messageEncoderV1;
        case V2:
        return messageEncoderV2;
        case V3:
        return messageEncoderV3;
        default:
        throw new DriverInternalError("Unsupported protocol version " + protocolVersion);
      }
    }
  }
}