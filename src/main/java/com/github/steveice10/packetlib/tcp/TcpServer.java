package com.github.steveice10.packetlib.tcp;
import com.github.steveice10.packetlib.AbstractServer;
import com.github.steveice10.packetlib.BuiltinFlags;
import com.github.steveice10.packetlib.helper.TransportHelper;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.incubator.channel.uring.IOUringEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.incubator.channel.uring.IOUringServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;

public class TcpServer extends AbstractServer {
  private EventLoopGroup group;

  private Class<? extends ServerSocketChannel> serverSocketChannel;

  private Channel channel;

  public TcpServer(String host, int port, Class<? extends PacketProtocol> protocol) {
    super(host, port, protocol);
  }

  @Override public boolean isListening() {
    return this.channel != null && this.channel.isOpen();
  }

  @Override public void bindImpl(boolean wait, final Runnable callback) {
    if (this.group != null || this.channel != null) {
      return;
    }

<<<<<<< /usr/src/app/output/steveice10/packetlib/6fe86c063737c0d01d8ee7a1df92a887d4aab3fb/src/main/java/com/github/steveice10/packetlib/tcp/TcpServer.java/left.java
    switch (TransportHelper.determineTransportMethod()) {
      case IO_URING:
      this.group = new IOUringEventLoopGroup();
      this.serverSocketChannel = IOUringServerSocketChannel.class;
      break;
      case EPOLL:
      this.group = new EpollEventLoopGroup();
      this.serverSocketChannel = EpollServerSocketChannel.class;
      break;
      case NIO:
      this.group = new NioEventLoopGroup();
      this.serverSocketChannel = NioServerSocketChannel.class;
      break;
    }
=======
    boolean debug = getGlobalFlag(BuiltinFlags.PRINT_DEBUG, false);
>>>>>>> /usr/src/app/output/steveice10/packetlib/6fe86c063737c0d01d8ee7a1df92a887d4aab3fb/src/main/java/com/github/steveice10/packetlib/tcp/TcpServer.java/right.java

    Class<? extends ServerChannel> channelClass;
    if (Epoll.isAvailable()) {
      channelClass = EpollServerSocketChannel.class;
    } else {
      if (debug) {
        System.out.println("[PacketLib] Not using Epoll: " + Epoll.unavailabilityCause().getMessage());
      }
      channelClass = NioServerSocketChannel.class;
    }
    if (debug) {
      System.out.println("[PacketLib] Channel class: " + channelClass.getName());
    }
    ChannelFuture future = new ServerBootstrap().channel(
<<<<<<< /usr/src/app/output/steveice10/packetlib/6fe86c063737c0d01d8ee7a1df92a887d4aab3fb/src/main/java/com/github/steveice10/packetlib/tcp/TcpServer.java/left.java
    this.serverSocketChannel
=======
    channelClass
>>>>>>> /usr/src/app/output/steveice10/packetlib/6fe86c063737c0d01d8ee7a1df92a887d4aab3fb/src/main/java/com/github/steveice10/packetlib/tcp/TcpServer.java/right.java
    ).childHandler(new ChannelInitializer<Channel>() {
      @Override public void initChannel(Channel channel) throws Exception {
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        PacketProtocol protocol = createPacketProtocol();
        TcpSession session = new TcpServerSession(address.getHostName(), address.getPort(), protocol, TcpServer.this);
        session.getPacketProtocol().newServerSession(TcpServer.this, session);
        channel.config().setOption(ChannelOption.IP_TOS, 0x18);
        try {
          channel.config().setOption(ChannelOption.TCP_NODELAY, true);
        } catch (ChannelException ignored) {
        }
        ChannelPipeline pipeline = channel.pipeline();
        session.refreshReadTimeoutHandler(channel);
        session.refreshWriteTimeoutHandler(channel);
        pipeline.addLast("encryption", new TcpPacketEncryptor(session));
        pipeline.addLast("sizer", new TcpPacketSizer(session));
        pipeline.addLast("codec", new TcpPacketCodec(session));
        pipeline.addLast("manager", session);
      }
    }).group(this.group).localAddress(this.getHost(), this.getPort()).bind();
    if (wait) {
      try {
        future.sync();
      } catch (InterruptedException e) {
      }
      channel = future.channel();
      if (callback != null) {
        callback.run();
      }
    } else {
      future.addListener(new ChannelFutureListener() {
        @Override public void operationComplete(ChannelFuture future) throws Exception {
          if (future.isSuccess()) {
            channel = future.channel();
            if (callback != null) {
              callback.run();
            }
          } else {
            System.err.println("[ERROR] Failed to asynchronously bind connection listener.");
            if (future.cause() != null) {
              future.cause().printStackTrace();
            }
          }
        }
      });
    }
  }

  @Override public void closeImpl(boolean wait, final Runnable callback) {
    if (this.channel != null) {
      if (this.channel.isOpen()) {
        ChannelFuture future = this.channel.close();
        if (wait) {
          try {
            future.sync();
          } catch (InterruptedException e) {
          }
          if (callback != null) {
            callback.run();
          }
        } else {
          future.addListener((ChannelFutureListener) (future1) -> {
            if (future1.isSuccess()) {
              if (callback != null) {
                callback.run();
              }
            } else {
              System.err.println("[ERROR] Failed to asynchronously close connection listener.");
              if (future1.cause() != null) {
                future1.cause().printStackTrace();
              }
            }
          });
        }
      }
      this.channel = null;
    }
    if (this.group != null) {
      Future<?> future = this.group.shutdownGracefully();
      if (wait) {
        try {
          future.sync();
        } catch (InterruptedException e) {
        }
      } else {
        future.addListener(new GenericFutureListener() {
          @Override public void operationComplete(Future future) {
            if (!future.isSuccess() && getGlobalFlag(BuiltinFlags.PRINT_DEBUG, false)) {
              System.err.println("[ERROR] Failed to asynchronously close connection listener.");
              if (future.cause() != null) {
                future.cause().printStackTrace();
              }
            }
          }
        });
      }
      this.group = null;
    }
  }
}