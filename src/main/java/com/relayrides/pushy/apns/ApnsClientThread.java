package com.relayrides.pushy.apns;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>A worker thread that connects to an APNs server and transmits notifications from a {@code PushManager}'s
 * queue.</p>
 * 
 * <p>Generally, users of Pushy should <em>not</em> instantiate an {@code ApnsClientThread} directly, but should
 * instead construct a {@link PushManager}, which will manage the lifecycle of one or more client threads.</p>
 * 
 * @author <a href="mailto:jon@relayrides.com">Jon Chambers</a>
 */
class ApnsClientThread<T extends ApnsPushNotification> extends Thread {
  private enum ClientState {
    CONNECT,
    READY,
    RECONNECT,
    SHUTDOWN_WRITE,
    SHUTDOWN_WAIT,
    SHUTDOWN_FINISH,
    EXIT
  }



  private final PushManager<T> pushManager;

  private volatile boolean shouldReconnect;

  private final Bootstrap bootstrap;

  private Channel channel;

  private int sequenceNumber = 0;

  private volatile boolean shouldShutDown;

  private volatile boolean shouldShutDownImmediately;

  private volatile boolean shutdownNotificationWritten;

  private volatile boolean notificationRejectedAfterShutdownRequest;

  private ChannelFuture connectFuture;

  private Future<Channel> handshakeFuture;

  private boolean hasEverSentNotification;

  private final Object shutdownMutex = new Object();

  private SendableApnsPushNotification<KnownBadPushNotification> shutdownNotification;

  private ChannelFuture shutdownWriteFuture;

  private Future<?> workerShutdownFuture;

  private final SentNotificationBuffer<T> sentNotificationBuffer;

  private static final int SENT_NOTIFICATION_BUFFER_SIZE = 2048;

  private static final long POLL_TIMEOUT = 50;

  private static final TimeUnit POLL_TIME_UNIT = TimeUnit.MILLISECONDS;

  private static final int BATCH_SIZE = 32;

  private int writesSinceLastFlush = 0;

  private static AtomicInteger threadCounter = new AtomicInteger(0);

  private final Logger log = LoggerFactory.getLogger(ApnsClientThread.class);

  private class RejectedNotificationDecoder extends ByteToMessageDecoder {
    private static final int EXPECTED_BYTES = 6;

    private static final byte EXPECTED_COMMAND = 8;

    @Override protected void decode(final ChannelHandlerContext context, final ByteBuf in, final List<Object> out) {
      if (in.readableBytes() >= EXPECTED_BYTES) {
        final byte command = in.readByte();
        final byte code = in.readByte();
        final int notificationId = in.readInt();
        if (command != EXPECTED_COMMAND) {
          log.error(String.format("Unexpected command: %d", command));
        }
        final RejectedNotificationReason errorCode = RejectedNotificationReason.getByErrorCode(code);
        out.add(new RejectedNotification(notificationId, errorCode));
      }
    }
  }

  private class ApnsPushNotificationEncoder extends MessageToByteEncoder<SendableApnsPushNotification<T>> {
    private static final byte ENHANCED_PUSH_NOTIFICATION_COMMAND = 1;

    private static final int EXPIRE_IMMEDIATELY = 0;

    private final Charset utf8 = Charset.forName("UTF-8");

    @Override protected void encode(final ChannelHandlerContext context, final SendableApnsPushNotification<T> sendablePushNotification, final ByteBuf out) throws Exception {
      out.writeByte(ENHANCED_PUSH_NOTIFICATION_COMMAND);
      out.writeInt(sendablePushNotification.getSequenceNumber());
      if (sendablePushNotification.getPushNotification().getDeliveryInvalidationTime() != null) {
        out.writeInt(this.getTimestampInSeconds(sendablePushNotification.getPushNotification().getDeliveryInvalidationTime()));
      } else {
        out.writeInt(EXPIRE_IMMEDIATELY);
      }
      out.writeShort(sendablePushNotification.getPushNotification().getToken().length);
      out.writeBytes(sendablePushNotification.getPushNotification().getToken());
      final byte[] payloadBytes = sendablePushNotification.getPushNotification().getPayload().getBytes(utf8);
      out.writeShort(payloadBytes.length);
      out.writeBytes(payloadBytes);
    }

    private int getTimestampInSeconds(final Date date) {
      return (int) (date.getTime() / 1000);
    }
  }

  private class ApnsErrorHandler extends SimpleChannelInboundHandler<RejectedNotification> {
    private final ApnsClientThread<T> clientThread;

    public ApnsErrorHandler(final ApnsClientThread<T> clientThread) {
      this.clientThread = clientThread;
    }

    @Override protected void channelRead0(final ChannelHandlerContext context, final RejectedNotification rejectedNotification) throws Exception {
      this.clientThread.handleRejectedNotification(rejectedNotification);
    }

    @Override public void exceptionCaught(final ChannelHandlerContext context, final Throwable cause) {
      this.clientThread.requestReconnection();
    }
  }

  /**
	 * Constructs a new APNs client thread. The thread connects to the APNs gateway in the given {@code PushManager}'s
	 * environment and reads notifications from the {@code PushManager}'s queue.
	 * 
	 * @param pushManager the {@code PushManager} from which this client thread should read environment settings and
	 * notifications
	 */
  public ApnsClientThread(final PushManager<T> pushManager) {
    super(String.format("ApnsClientThread-%d", ApnsClientThread.threadCounter.incrementAndGet()));
    this.pushManager = pushManager;
    this.sentNotificationBuffer = new SentNotificationBuffer<T>(SENT_NOTIFICATION_BUFFER_SIZE);
    this.bootstrap = new Bootstrap();
    this.bootstrap.group(new NioEventLoopGroup(1));
    this.bootstrap.channel(NioSocketChannel.class);
    this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    final ApnsClientThread<T> clientThread = this;
    this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
      @Override protected void initChannel(final SocketChannel channel) throws Exception {
        final ChannelPipeline pipeline = channel.pipeline();
        if (pushManager.getEnvironment().isTlsRequired()) {
          pipeline.addLast("ssl", SslHandlerUtil.createSslHandler(pushManager.getKeyStore(), pushManager.getKeyStorePassword()));
        }
        pipeline.addLast("decoder", new RejectedNotificationDecoder());
        pipeline.addLast("encoder", new ApnsPushNotificationEncoder());
        pipeline.addLast("handler", new ApnsErrorHandler(clientThread));
      }
    });
  }

  /**
	 * Continually polls this thread's {@code PushManager}'s queue for new messages and sends them to the APNs
	 * gateway. Automatically reconnects as needed.
	 */
  @Override public void run() {
    ClientState clientState = ClientState.CONNECT;
    while (clientState != ClientState.EXIT) {
      final ClientState nextClientState;
      switch (clientState) {
        case CONNECT:
        {
          boolean finishedConnecting = false;
          try {
            finishedConnecting = this.connectOrContinueConnecting();
          } catch (InterruptedException e) {
            continue;
          }
          if (finishedConnecting) {
            if (this.shouldShutDownImmediately) {
              nextClientState = ClientState.SHUTDOWN_FINISH;
            } else {
              if (this.shouldShutDown) {
                if (this.shutdownNotificationWritten) {
                  nextClientState = ClientState.SHUTDOWN_WAIT;
                } else {
                  nextClientState = ClientState.SHUTDOWN_WRITE;
                }
              } else {
                nextClientState = ClientState.READY;
              }
            }
          } else {
            nextClientState = (this.shouldShutDown && !this.hasEverSentNotification) ? ClientState.SHUTDOWN_FINISH : ClientState.CONNECT;
          }
          break;
        }
        case READY:
        {
          try {
            this.sendNextNotification(POLL_TIMEOUT, POLL_TIME_UNIT);
          } catch (InterruptedException e) {
            this.channel.flush();
          }
          if (this.shouldShutDownImmediately) {
            nextClientState = ClientState.SHUTDOWN_FINISH;
          } else {
            if (this.shouldReconnect) {
              nextClientState = ClientState.RECONNECT;
            } else {
              if (this.shouldShutDown) {
                nextClientState = ClientState.SHUTDOWN_WRITE;
              } else {
                nextClientState = ClientState.READY;
              }
            }
          }
          break;
        }
        case RECONNECT:
        {
          boolean finishedDisconnecting = false;
          try {
            this.disconnectOrContinueDisconnecting();
            finishedDisconnecting = true;
          } catch (InterruptedException e) {
            log.warn(String.format("%s interrupted while waiting for connection to close.", this.getName()));
          }
          if (this.shouldShutDownImmediately) {
            nextClientState = ClientState.SHUTDOWN_FINISH;
          } else {
            if (finishedDisconnecting) {
              this.shouldReconnect = false;
              nextClientState = ClientState.CONNECT;
            } else {
              nextClientState = ClientState.RECONNECT;
            }
          }
          break;
        }
        case SHUTDOWN_WRITE:
        {
          if (!this.hasEverSentNotification) {
            nextClientState = ClientState.SHUTDOWN_FINISH;
          } else {
            if (this.shouldShutDownImmediately) {
              nextClientState = ClientState.SHUTDOWN_FINISH;
            } else {
              if (this.notificationRejectedAfterShutdownRequest) {
                nextClientState = ClientState.SHUTDOWN_WAIT;
              } else {
                if (this.shutdownNotification == null) {
                  this.shutdownNotification = new SendableApnsPushNotification<KnownBadPushNotification>(new KnownBadPushNotification(), this.sequenceNumber++);
                }
                if (this.shutdownWriteFuture == null) {
                  this.shutdownWriteFuture = this.channel.writeAndFlush(this.shutdownNotification);
                }
                try {
                  this.shutdownWriteFuture.await();
                } catch (InterruptedException e) {
                  log.debug(String.format("%s interrupted while waiting for shutdown notification write to complete.", this.getName()));
                }
                if (this.shutdownWriteFuture.isDone()) {
                  if (this.shutdownWriteFuture.isSuccess()) {
                    this.shutdownNotificationWritten = true;
                    nextClientState = ClientState.SHUTDOWN_WAIT;
                  } else {
                    if (this.shutdownWriteFuture.cause() != null) {
                      log.debug(String.format("Shutdown notification write failed in %s.", this.getName()), this.shutdownWriteFuture.cause());
                      this.shutdownWriteFuture = null;
                      nextClientState = ClientState.RECONNECT;
                    } else {
                      log.warn(String.format("Shutdown notification write cancelled in %s", this.getName()));
                      this.shutdownWriteFuture = null;
                      nextClientState = ClientState.RECONNECT;
                    }
                  }
                } else {
                  nextClientState = this.shouldReconnect ? ClientState.RECONNECT : ClientState.SHUTDOWN_WRITE;
                }
              }
            }
          }
          break;
        }
        case SHUTDOWN_WAIT:
        {
          synchronized (this.shutdownMutex) {
            if (!this.notificationRejectedAfterShutdownRequest) {
              try {
                this.shutdownMutex.wait();
              } catch (InterruptedException e) {
                log.debug(String.format("%s interrupted while waiting for notification rejection.", this.getName()));
              }
            }
          }
          if (this.shouldShutDownImmediately) {
            nextClientState = ClientState.SHUTDOWN_FINISH;
          } else {
            if (this.notificationRejectedAfterShutdownRequest) {
              boolean finishedDisconnecting = false;
              try {
                this.disconnectOrContinueDisconnecting();
                finishedDisconnecting = true;
              } catch (InterruptedException e) {
                log.debug(String.format("%s interrupted while waiting to disconnect after rejected notification.", this.getName()));
              }
              nextClientState = finishedDisconnecting ? ClientState.SHUTDOWN_FINISH : ClientState.SHUTDOWN_WAIT;
            } else {
              nextClientState = this.shouldReconnect ? ClientState.RECONNECT : ClientState.SHUTDOWN_WAIT;
            }
          }
          break;
        }
        case SHUTDOWN_FINISH:
        {
          if (this.workerShutdownFuture == null) {
            this.workerShutdownFuture = this.bootstrap.group().shutdownGracefully();
          }
          boolean shutdownFinished = false;
          try {
            this.workerShutdownFuture.await();
            shutdownFinished = true;
          } catch (InterruptedException e) {
            log.debug(String.format("%s interrupted while waiting for worker group to shut down gracefully", this.getName()));
          }
          nextClientState = shutdownFinished ? ClientState.EXIT : ClientState.SHUTDOWN_FINISH;
          break;
        }
        case EXIT:
        {
          nextClientState = ClientState.EXIT;
          break;
        }
        default:
        {
          throw new IllegalStateException(String.format("Unexpected state: %s", this.getState()));
        }
      }
      clientState = nextClientState;
    }
  }

  private boolean connectOrContinueConnecting() throws InterruptedException {
    if (this.connectFuture == null) {
      log.debug(String.format("%s beginning connection process.", this.getName()));
      this.connectFuture = this.bootstrap.connect(this.pushManager.getEnvironment().getApnsGatewayHost(), this.pushManager.getEnvironment().getApnsGatewayPort());
    }
    this.connectFuture.await();
    if (this.connectFuture.isSuccess()) {
      log.debug(String.format("%s connected.", this.getName()));
      this.channel = this.connectFuture.channel();
      if (this.pushManager.getEnvironment().isTlsRequired()) {
        if (this.handshakeFuture == null) {
          log.debug(String.format("%s waiting for TLS handshake.", this.getName()));
          this.handshakeFuture = this.channel.pipeline().get(SslHandler.class).handshakeFuture();
        }
        this.handshakeFuture.await();
        if (this.handshakeFuture.isSuccess()) {
          log.debug(String.format("%s successfully completed TLS handshake.", this.getName()));
          this.connectFuture = null;
          this.handshakeFuture = null;
          return true;
        } else {
          log.error(String.format("%s failed to complete TLS handshake with APNs gateway.", this.getName()), this.handshakeFuture.cause());
          this.connectFuture = null;
          this.handshakeFuture = null;
          return false;
        }
      } else {
        log.debug(String.format("%s does not require a TLS handshake.", this.getName()));
        this.connectFuture = null;
        return true;
      }
    } else {
      log.error(String.format("%s failed to connect to APNs gateway.", this.getName()), connectFuture.cause());
      this.connectFuture = null;
      return false;
    }
  }

  private void disconnectOrContinueDisconnecting() throws InterruptedException {
    if (this.channel != null && this.channel.isOpen()) {
      this.channel.close();
    }
    log.debug(String.format("%s waiting for connection to close.", this.getName()));
    this.channel.closeFuture().await();
    if (this.channel.closeFuture().cause() != null) {
      log.warn(String.format("%s failed to cleanly close its connection.", this.getName()), this.channel.closeFuture().cause());
    }
  }

  private void sendNextNotification(final long timeout, final TimeUnit timeUnit) throws InterruptedException {
    final T notification = this.pushManager.getQueue().poll(timeout, timeUnit);
    if (this.isInterrupted()) {
      this.pushManager.enqueuePushNotification(notification);
    } else {
      if (notification != null) {
        final SendableApnsPushNotification<T> sendableNotification = new SendableApnsPushNotification<T>(notification, this.sequenceNumber++);
        final String threadName = this.getName();
        if (log.isTraceEnabled()) {
          log.trace(String.format("%s sending %s", threadName, sendableNotification));
        }
        this.sentNotificationBuffer.addSentNotification(sendableNotification);
        this.channel.write(sendableNotification).addListener(new GenericFutureListener<ChannelFuture>() {
          public void operationComplete(final ChannelFuture future) {
            if (future.cause() != null) {
              if (log.isTraceEnabled()) {
                log.trace(String.format("%s failed to write notification %s", threadName, sendableNotification), future.cause());
              }
              requestReconnection();
              final T failedNotification = sentNotificationBuffer.getAndRemoveNotificationWithSequenceNumber(sendableNotification.getSequenceNumber());
              if (failedNotification != null) {
                pushManager.enqueuePushNotification(failedNotification);
              }
            } else {
              if (log.isTraceEnabled()) {
                log.trace(String.format("%s successfully wrote notification %d", threadName, sendableNotification.getSequenceNumber()));
              }
            }
          }
        });
        this.hasEverSentNotification = true;
        if (++this.writesSinceLastFlush >= ApnsClientThread.BATCH_SIZE) {
          this.channel.flush();
          this.writesSinceLastFlush = 0;
        }
      } else {
        if (this.writesSinceLastFlush > 0) {
          this.channel.flush();
          this.writesSinceLastFlush = 0;
        }
      }
    }
  }

  private void handleRejectedNotification(final RejectedNotification rejectedNotification) {
    if (this.shutdownNotification == null || rejectedNotification.getSequenceNumber() != this.shutdownNotification.getSequenceNumber()) {
      if (rejectedNotification.getReason() != RejectedNotificationReason.SHUTDOWN) {
        this.pushManager.notifyListenersOfRejectedNotification(this.sentNotificationBuffer.getAndRemoveNotificationWithSequenceNumber(rejectedNotification.getSequenceNumber()), rejectedNotification.getReason());
      }
    }
    if (this.shouldShutDown) {
      synchronized (this.shutdownMutex) {
        this.notificationRejectedAfterShutdownRequest = true;
        this.shutdownMutex.notify();
      }
      this.interrupt();
    } else {
      this.requestReconnection();
    }
    this.pushManager.enqueueAllNotifications(this.sentNotificationBuffer.getAndRemoveAllNotificationsAfterSequenceNumber(rejectedNotification.getSequenceNumber()));
  }

  private void requestReconnection() {
    this.shouldReconnect = true;
    this.interrupt();
  }

  /**
	 * Gracefully and asynchronously shuts down this client thread.
	 */
  protected void requestShutdown() {
    this.shouldShutDown = true;
    this.interrupt();
  }

  protected void shutdownImmediately() {
    this.shouldShutDownImmediately = true;
    this.interrupt();
  }
}