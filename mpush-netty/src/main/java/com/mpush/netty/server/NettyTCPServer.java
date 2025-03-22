package com.mpush.netty.server;
import com.mpush.api.service.BaseService;
import com.mpush.api.service.Listener;
import com.mpush.api.service.Server;
import com.mpush.api.service.ServiceException;
import com.mpush.netty.codec.PacketDecoder;
import com.mpush.netty.codec.PacketEncoder;
import com.mpush.tools.config.CC;
import com.mpush.tools.thread.ThreadNames;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.Native;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by ohun on 2015/12/22.
 *
 * @author ohun@live.cn
 */
public abstract class NettyTCPServer extends BaseService implements Server {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public enum State {
    Created,
    Initialized,
    Starting,
    Started,
    Shutdown
  }

  protected final AtomicReference<State> serverState = new AtomicReference<>(State.Created);

  protected final int port;

  protected EventLoopGroup bossGroup;

  protected EventLoopGroup workerGroup;

  public NettyTCPServer(int port) {
    this.port = port;
  }

  public void init() {
    if (!serverState.compareAndSet(State.Created, State.Initialized)) {
      throw new ServiceException("Server already init");
    }
  }

  @Override public boolean isRunning() {
    return serverState.get() == State.Started;
  }

  @Override public void stop(Listener listener) {
    if (!serverState.compareAndSet(State.Started, State.Shutdown)) {
      if (listener != null) {
        listener.onFailure(new ServiceException("server was already shutdown."));
      }
      logger.error("{} was already shutdown.", this.getClass().getSimpleName());
      return;
    }
    logger.info("try shutdown {}...", this.getClass().getSimpleName());
    if (bossGroup != null) {
      bossGroup.shutdownGracefully().syncUninterruptibly();
    }
    if (workerGroup != null) {
      workerGroup.shutdownGracefully().syncUninterruptibly();
    }
    logger.info("{} shutdown success.", this.getClass().getSimpleName());
    if (listener != null) {
      listener.onSuccess(port);
    }
  }

  @Override public void start(final Listener listener) {
    if (!serverState.compareAndSet(State.Initialized, State.Starting)) {
      throw new ServiceException("Server already started or have not init");
    }
    if (useNettyEpoll()) {
      createEpollServer(listener);
    } else {
      createNioServer(listener);
    }
  }

  private void createServer(Listener listener, EventLoopGroup boss, EventLoopGroup work, ChannelFactory<? extends ServerChannel> channelFactory) {
    this.bossGroup = boss;
    this.workerGroup = work;
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup);
      b.channelFactory(channelFactory);
      b.childHandler(new ChannelInitializer<Channel>() {
        @Override public void initChannel(Channel ch) throws Exception {
          initPipeline(ch.pipeline());
        }
      });
      initOptions(b);
      b.bind(port).addListener((future) -> {
        if (future.isSuccess()) {
          serverState.set(State.Started);
          logger.info("server start success on:{}", port);
          if (listener != null) {
            listener.onSuccess(port);
          }
        } else {
          logger.error("server start failure on:{}", port, future.cause());
          if (listener != null) {
            listener.onFailure(future.cause());
          }
        }
      });
    } catch (Exception e) {
      logger.error("server start exception", e);
      if (listener != null) {
        listener.onFailure(e);
      }
      throw new ServiceException("server start exception, port=" + port, e);
    }
  }

  private void createNioServer(Listener listener) {
    EventLoopGroup bossGroup = getBossGroup();
    EventLoopGroup workerGroup = getWorkerGroup();
    if (bossGroup == null) {
      NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(getBossThreadNum(), getBossThreadFactory(), getSelectorProvider());
      nioEventLoopGroup.setIoRatio(100);
      bossGroup = nioEventLoopGroup;
    }
    if (workerGroup == null) {
      NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(getWorkThreadNum(), getWorkThreadFactory(), getSelectorProvider());
      nioEventLoopGroup.setIoRatio(getIoRate());
      workerGroup = nioEventLoopGroup;
    }
    createServer(listener, bossGroup, workerGroup, getChannelFactory());
  }

  private void createEpollServer(Listener listener) {
    EventLoopGroup bossGroup = getBossGroup();
    EventLoopGroup workerGroup = getWorkerGroup();
    if (bossGroup == null) {
      EpollEventLoopGroup epollEventLoopGroup = new EpollEventLoopGroup(getBossThreadNum(), getBossThreadFactory());
      epollEventLoopGroup.setIoRatio(100);
      bossGroup = epollEventLoopGroup;
    }
    if (workerGroup == null) {
      EpollEventLoopGroup epollEventLoopGroup = new EpollEventLoopGroup(getWorkThreadNum(), getWorkThreadFactory());
      epollEventLoopGroup.setIoRatio(getIoRate());
      workerGroup = epollEventLoopGroup;
    }
    createServer(listener, bossGroup, workerGroup, EpollServerSocketChannel::new);
  }

  /***
     * option()是提供给NioServerSocketChannel用来接收进来的连接。
     * childOption()是提供给由父管道ServerChannel接收到的连接，
     * 在这个例子中也是NioServerSocketChannel。
     */
  protected void initOptions(ServerBootstrap b) {
    b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
  }

  public abstract ChannelHandler getChannelHandler();

  protected ChannelHandler getDecoder() {
    return new PacketDecoder();
  }

  protected ChannelHandler getEncoder() {
    return PacketEncoder.INSTANCE;
  }

  /**
     * 每连上一个链接调用一次
     *
     * @param pipeline
     */
  protected void initPipeline(ChannelPipeline pipeline) {
    pipeline.addLast("decoder", getDecoder());
    pipeline.addLast("encoder", getEncoder());
    pipeline.addLast("handler", getChannelHandler());
  }

  /**
     * netty 默认的Executor为ThreadPerTaskExecutor
     * 线程池的使用在SingleThreadEventExecutor#doStartThread
     * <p>
     * eventLoop.execute(runnable);
     * 是比较重要的一个方法。在没有启动真正线程时，
     * 它会启动线程并将待执行任务放入执行队列里面。
     * 启动真正线程(startThread())会判断是否该线程已经启动，
     * 如果已经启动则会直接跳过，达到线程复用的目的
     *
     * @return
     */
  protected ThreadFactory getBossThreadFactory() {
    return new DefaultThreadFactory(getBossThreadName());
  }

  protected ThreadFactory getWorkThreadFactory() {
    return new DefaultThreadFactory(getWorkThreadName());
  }

  protected int getBossThreadNum() {
    return 1;
  }

  protected int getWorkThreadNum() {
    return 0;
  }

  protected String getBossThreadName() {
    return ThreadNames.T_BOSS;
  }

  protected String getWorkThreadName() {
    return ThreadNames.T_WORKER;
  }

  protected int getIoRate() {
    return 70;
  }

  protected boolean useNettyEpoll() {
    if (CC.mp.core.useNettyEpoll()) {
      try {
        Native.offsetofEpollData();
        return true;
      } catch (UnsatisfiedLinkError error) {
        logger.warn("can not load netty epoll, switch nio model.");
      }
    }
    return false;
  }

  public EventLoopGroup getBossGroup() {
    return bossGroup;
  }

  public EventLoopGroup getWorkerGroup() {
    return workerGroup;
  }

  public ChannelFactory<? extends ServerChannel> getChannelFactory() {
    return NioServerSocketChannel::new;
  }

  public SelectorProvider getSelectorProvider() {
    return SelectorProvider.provider();
  }
}