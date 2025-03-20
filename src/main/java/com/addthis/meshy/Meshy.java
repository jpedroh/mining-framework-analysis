package com.addthis.meshy;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.nio.channels.ClosedChannelException;
import java.text.DecimalFormat;
import com.addthis.basis.util.JitterClock;
import com.addthis.basis.util.Parameter;
import com.addthis.meshy.service.file.FileSource;
import com.addthis.meshy.service.file.FileTarget;
import com.addthis.meshy.service.host.HostSource;
import com.addthis.meshy.service.host.HostTarget;
import com.addthis.meshy.service.message.MessageSource;
import com.addthis.meshy.service.message.MessageTarget;
import com.addthis.meshy.service.peer.PeerSource;
import com.addthis.meshy.service.peer.PeerTarget;
import com.addthis.meshy.service.stream.StreamSource;
import com.addthis.meshy.service.stream.StreamTarget;
import com.google.common.base.Splitter;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.VirtualMachineMetrics;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * full meshy nodes are both clients and servers. so the client logic is not exclusive to client-only nodes.
 * try not to let this confuse you when reading the code. this is a partial explanation as to why there are
 * client and server codes co-mingled.
 */
public abstract class Meshy implements ChannelMaster, Closeable {
  private static final Logger log = LoggerFactory.getLogger(Meshy.class);

  static final boolean THROTTLE_LOG = Parameter.boolValue("meshy.throttleLog", true);

  static final Map<Integer, Class<? extends SessionHandler>> idHandlerMap = new HashMap<>();

  static final int STATS_INTERVAL = Parameter.intValue("meshy.stats.time", 1) * 1000;

  static final Map<Class<? extends SessionHandler>, Integer> handlerIdMap = new HashMap<>();

  static final AtomicInteger nextHandlerID = new AtomicInteger(1);

  static final DecimalFormat numbers = new DecimalFormat("#,###");

  static final VirtualMachineMetrics vmMetrics = VirtualMachineMetrics.getInstance();

  static final AtomicInteger nextSession = new AtomicInteger(0);

  static final Enumeration<NetworkInterface> netIfEnum;

  private static final Meter bytesInMeter = Metrics.newMeter(Meshy.class, "bytesIn", "bytesIn", TimeUnit.SECONDS);

  private static final Meter bytesOutMeter = Metrics.newMeter(Meshy.class, "bytesOut", "bytesOut", TimeUnit.SECONDS);

  private static final String HOSTNAME = getShortHostName();

  static void registerHandlerClass(Class<? extends SessionHandler> clazz) {
    if (!handlerIdMap.containsKey(clazz)) {
      int id = nextHandlerID.getAndIncrement();
      idHandlerMap.put(id, clazz);
      handlerIdMap.put(clazz, id);
    }
  }

  static {
    registerHandlerClass(HostSource.class);
    registerHandlerClass(HostTarget.class);
    registerHandlerClass(FileSource.class);
    registerHandlerClass(FileTarget.class);
    registerHandlerClass(PeerSource.class);
    registerHandlerClass(PeerTarget.class);
    registerHandlerClass(StreamSource.class);
    registerHandlerClass(StreamTarget.class);
    registerHandlerClass(MessageSource.class);
    registerHandlerClass(MessageTarget.class);
    try {
      netIfEnum = NetworkInterface.getNetworkInterfaces();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
     * utility
     */
  public static byte[] getBytes(int length, ChannelBuffer buffer) {
    byte request[] = new byte[length];
    buffer.readBytes(request);
    return request;
  }

  protected final HashSet<ChannelState> connectedChannels = new HashSet<>();

  public static InputStream getInput(int length, ChannelBuffer buffer) {
    return new ByteArrayInputStream(getBytes(length, buffer));
  }

  protected final HashSet<InetSocketAddress> needsPeering = new HashSet<>();

  protected Meshy() {
    if (HOSTNAME != null) {
      uuid = HOSTNAME + "-" + Long.toHexString(System.currentTimeMillis() & 0xffffff);
    } else {
      uuid = Long.toHexString(UUID.randomUUID().getMostSignificantBits());
    }
    clientFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
    clientBootstrap = new ClientBootstrap(clientFactory);
    clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(new MeshyChannelHandler());
      }
    });
    clientBootstrap.setOption("tcpNoDelay", true);
    clientBootstrap.setOption("keepAlive", true);
    clientBootstrap.setOption("connectTimeoutMillis", 30000);
    updateLastEventTime();
  }

  protected final HashSet<String> inPeering = new HashSet<>();

  private final ChannelFactory clientFactory;

  private final ClientBootstrap clientBootstrap;

  private final String uuid;

  private final AtomicLong lastEvent = new AtomicLong(0);

  private final ChannelGroup allChannels = new DefaultChannelGroup();

  private final AtomicInteger bytesIn = new AtomicInteger(0);

  private final AtomicInteger bytesOut = new AtomicInteger(0);

  private final Collection<ChannelCloseListener> channelCloseListeners = new ArrayList<>();

  @Override public void close() {
    synchronized (connectedChannels) {
      for (ChannelState state : connectedChannels) {
        state.debugSessions();
      }
    }
    allChannels.close().awaitUninterruptibly();
    clientFactory.releaseExternalResources();
  }

  protected void updateLastEventTime() {
    lastEvent.set(JitterClock.globalTime());
  }

  private static String getShortHostName() {
    try {
      String hostName = Splitter.on('.').split(InetAddress.getLocalHost().getHostName()).iterator().next();
      log.debug("Local host name resolved to {}", hostName);
      return hostName;
    } catch (Exception ex) {
      log.warn("Unable to resolve local host name");
      log.debug("Local host name resolution stack trace", ex);
      return null;
    }
  }

  protected ChannelFuture connect(InetSocketAddress addr) {
    return clientBootstrap.connect(addr);
  }

  public int getChannelCount() {
    return allChannels.size();
  }

  public int getPeeredCount() {
    return connectedChannels.size();
  }

  @Override public void sentBytes(int size) {
    bytesOutMeter.mark(size);
    bytesOut.addAndGet(size);
  }

  protected int getAndClearSent() {
    return bytesOut.getAndSet(0);
  }

  @Override public void recvBytes(int size) {
    bytesInMeter.mark(size);
    bytesIn.addAndGet(size);
  }

  protected int getAndClearRecv() {
    return bytesIn.getAndSet(0);
  }

  @Override public String getUUID() {
    return uuid;
  }

  /**
     * @param nameFilter null = all channels, empty = named channels, non-empty = exact match
     * @return Iterator of ChannelState objects
     */
  @Override public Collection<ChannelState> getChannels(final String nameFilter) {
    HashSet<ChannelState> set = new HashSet<>();
    synchronized (connectedChannels) {
      for (ChannelState state : connectedChannels) {
        if ((nameFilter == MeshyConstants.LINK_ALL) || (state.getRemoteAddress() != null && (nameFilter == MeshyConstants.LINK_NAMED || nameFilter.equals(state.getName())))) {
          set.add(state);
        }
      }
    }
    return set;
  }

  @Override public TargetHandler createHandler(int type) {
    try {
      return (TargetHandler) idHandlerMap.get(type).newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override public long lastEventTime() {
    return lastEvent.get();
  }

  @Override public void createSession(SourceHandler sourceHandler, Class<? extends TargetHandler> targetHandler, String targetUuid) {
    Set<Channel> group;
    HashSet<String> uuids = new HashSet<>();
    final boolean breakOnMatch = targetUuid != null && !targetUuid.isEmpty();
    int sessionID = nextSession.incrementAndGet();
    synchronized (connectedChannels) {
      group = new HashSet<>(breakOnMatch ? 1 : connectedChannels.size());
      for (ChannelState state : connectedChannels) {
        if ((targetUuid == MeshyConstants.LINK_ALL) || (targetUuid == MeshyConstants.LINK_NAMED && state.getRemoteAddress() != null) || (state.getName() != null && targetUuid.equals(state.getName()))) {
          if (state.getName() != null && !uuids.add(state.getName())) {
            continue;
          }
          group.add(state.getChannel());
          state.addSourceHandler(sessionID, sourceHandler);
          if (breakOnMatch) {
            break;
          }
        }
      }
    }
    group = Collections.synchronizedSet(group);
    sourceHandler.init(sessionID, handlerIdMap.get(targetHandler), group);
    log.debug("{} createSession {} target={} uuid={} group={} sessionID={}", this, sourceHandler, targetHandler, targetUuid != null ? "\'" + targetUuid + "\'" : null, group, sessionID);
  }

  protected void connectChannel(Channel channel, ChannelState channelState) {
    allChannels.add(channel);
    synchronized (connectedChannels) {
      connectedChannels.add(channelState);
    }
    log.debug("{} connectChannel @ {}", this, channel.getRemoteAddress());
  }

  private void closeChannel(Channel channel) {
    allChannels.remove(channel);
    if (channel.getId() != null) {
      synchronized (connectedChannels) {
        ChannelState match = null;
        for (ChannelState state : connectedChannels) {
          if (state.getChannel() == channel) {
            match = state;
            break;
          }
        }
        if (match != null) {
          connectedChannels.remove(match);
          inPeering.remove(match.getName());
        }
      }
      synchronized (channelCloseListeners) {
        for (ChannelCloseListener channelCloseListener : channelCloseListeners) {
          channelCloseListener.channelClosed(channel.getId());
        }
      }
    }
    log.debug("{} closeChannel @ {}", this, channel.getRemoteAddress());
  }

  public boolean addChannelCloseListener(ChannelCloseListener channelCloseListener) {
    synchronized (this.channelCloseListeners) {
      return this.channelCloseListeners.add(channelCloseListener);
    }
  }

  public boolean removeChannelCloseListener(ChannelCloseListener channelCloseListener) {
    synchronized (this.channelCloseListeners) {
      return this.channelCloseListeners.remove(channelCloseListener);
    }
  }

  class MeshyChannelHandler extends SimpleChannelHandler {
    private ChannelState getAttachState(ChannelHandlerContext ctx) {
      synchronized (ctx) {
        ChannelState state = (ChannelState) ctx.getAttachment();
        if (state == null) {
          state = new ChannelState(Meshy.this, ctx.getChannel());
          log.trace("{} created for {}", state, ctx.hashCode());
          ctx.setAttachment(state);
        }
        return state;
      }
    }

    @Override public void messageReceived(ChannelHandlerContext ctx, MessageEvent msg) {
      updateLastEventTime();
      getAttachState(ctx).messageReceived(msg);
    }

    @Override public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
      updateLastEventTime();
      connectChannel(ctx.getChannel(), getAttachState(ctx));
      getAttachState(ctx).channelConnected(e);
    }

    @Override public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent ex) {
      updateLastEventTime();
      if (ex.getCause() instanceof ClosedChannelException) {
        log.warn("{} exception = {}", ex, ctx.getAttachment());
      } else {
        if (ex.getCause() instanceof ConnectException) {
          log.debug("{} exception = {}", ex, ctx.getAttachment());
        } else {
          log.warn("Netty exception caught. Closing channel. ChannelState: {}", ctx.getAttachment(), ex.getCause());
          ctx.getChannel().close();
        }
      }
      try {
        channelClosed(ctx, null);
      } catch (Exception ee) {
        log.error("Mystery exception we are swallowing", ee);
      }
    }

    @Override public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
      updateLastEventTime();
      getAttachState(ctx).channelClosed(e);
      closeChannel(ctx.getChannel());
    }
  }
}