package de.uniluebeck.itm.spitfire.nCoap.communication.reliability;
import com.google.common.collect.HashBasedTable;
import de.uniluebeck.itm.spitfire.nCoap.communication.core.CoapClientDatagramChannelFactory;
import de.uniluebeck.itm.spitfire.nCoap.communication.internal.InternalAcknowledgementMessage;
import de.uniluebeck.itm.spitfire.nCoap.communication.internal.InternalErrorMessage;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapMessage;
import de.uniluebeck.itm.spitfire.nCoap.message.header.Code;
import de.uniluebeck.itm.spitfire.nCoap.message.header.MsgType;
import de.uniluebeck.itm.spitfire.nCoap.toolbox.ByteArrayWrapper;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Oliver Kleine
 */
public class OutgoingMessageReliabilityHandler extends SimpleChannelHandler {
  private static Logger log = LoggerFactory.getLogger(OutgoingMessageReliabilityHandler.class.getName());

  private static int TIMEOUT_MILLIS = 2000;

  private static int MAX_RETRANSMITS = 4;

  private static Random random = new Random(System.currentTimeMillis());

  private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

  private final HashBasedTable<InetSocketAddress, Integer, byte[]> openOutgoingConMsg = HashBasedTable.create();

  private MessageIDFactory messageIDFactory = MessageIDFactory.getInstance();

  private static OutgoingMessageReliabilityHandler instance = new OutgoingMessageReliabilityHandler();

  /**
     * Returns the one and only instance of class OutgoingMessageReliabilityHandler (Singleton)
     * @return the one and only instance of class OutgoingMessageReliabilityHandle
     */
  public static OutgoingMessageReliabilityHandler getInstance() {
    return instance;
  }

  private OutgoingMessageReliabilityHandler() {
  }

  /**
     * This method is invoked with an upstream message event. If the message has one of the codes ACK or RESET it is
     * most likely a response for a request waiting for a response. Thus the corresponding request is removed from
     * the list of open requests and the request will not be retransmitted anymore.
     * @param ctx The {@link ChannelHandlerContext}
     * @param me The {@link MessageEvent}
     * @throws Exception
     */
  @Override public void messageReceived(ChannelHandlerContext ctx, MessageEvent me) throws Exception {
    if (me.getMessage() instanceof CoapMessage) {
      CoapMessage coapMessage = (CoapMessage) me.getMessage();
      InetSocketAddress remoteAddress = (InetSocketAddress) me.getRemoteAddress();
      if (coapMessage.getMessageType() == MsgType.ACK || coapMessage.getMessageType() == MsgType.RST) {
        log.debug("Incoming " + coapMessage.getMessageType() + " message with ID " + coapMessage.getMessageID() + " from remote address " + remoteAddress);
        byte[] removedToken;
        synchronized (openOutgoingConMsg) {
          removedToken = openOutgoingConMsg.remove(me.getRemoteAddress(), coapMessage.getMessageID());
        }
        if (removedToken != null) {
          log.debug(" Matching not yet confirmed message found (" + " remote address: " + remoteAddress + ", message ID " + coapMessage.getMessageID() + " ).");
          if (coapMessage.getMessageType() == MsgType.ACK && coapMessage.getCode() == Code.EMPTY) {
            InternalAcknowledgementMessage ack = new InternalAcknowledgementMessage(new ByteArrayWrapper(removedToken));
            MessageEvent emptyAckReceived = new UpstreamMessageEvent(ctx.getChannel(), ack, remoteAddress);
            ctx.sendUpstream(emptyAckReceived);
            return;
          }
        }
      }
    }
    ctx.sendUpstream(me);
  }

  /**
     * This method is invoked with a downstream message event. If it is a new message (i.e. to be
     * transmitted the first time) of type CON , it is added to the list of open requests waiting for a response.
     * @param ctx The {@link ChannelHandlerContext}
     * @param me The {@link MessageEvent}
     * @throws Exception
     */
  @Override public void writeRequested(ChannelHandlerContext ctx, MessageEvent me) throws Exception {
    log.debug("Handle Downstream Message Event.");
    if (!(me.getMessage() instanceof CoapMessage)) {
      ctx.sendDownstream(me);
      return;
    }
    CoapMessage coapMessage = (CoapMessage) me.getMessage();
    log.debug("Handle downstream event for message with ID " + coapMessage.getMessageID() + " for " + me.getRemoteAddress());
    if (coapMessage.getMessageID() == -1) {
      coapMessage.setMessageID(messageIDFactory.nextMessageID());
      if (coapMessage.getMessageType() == MsgType.CON) {
        if (!openOutgoingConMsg.contains(me.getRemoteAddress(), coapMessage.getMessageID())) {
          synchronized (openOutgoingConMsg) {
            openOutgoingConMsg.put((InetSocketAddress) me.getRemoteAddress(), coapMessage.getMessageID(), coapMessage.getToken());
          }
          MessageRetransmitter messageRetransmitter = new MessageRetransmitter((InetSocketAddress) me.getRemoteAddress(), coapMessage);
          int delay = (int) (TIMEOUT_MILLIS * messageRetransmitter.randomFactor);
          executorService.schedule(messageRetransmitter, delay, TimeUnit.MILLISECONDS);
          log.debug("First retransmit for " + coapMessage.getMessageType() + " message with ID " + coapMessage.getMessageID() + " to be confirmed by " + me.getRemoteAddress() + " scheduled with a delay of " + delay + " millis.");
        }
      }
    }
    ctx.sendDownstream(me);
  }


<<<<<<< /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/reliability/OutgoingMessageReliabilityHandler.java/left.java
  private class Retransmitter implements Runnable {
    private DatagramChannel datagramChannel = CoapClientDatagramChannelFactory.getInstance().getChannel();

    private InetSocketAddress rcptAddress;

    private CoapMessage coapMessage;

    private double randomFactor;

    private int retransmits;

    public Retransmitter(InetSocketAddress rcptAddress, CoapMessage coapMessage) {
      this.rcptAddress = rcptAddress;
      this.coapMessage = coapMessage;
      this.randomFactor = 1 + random.nextDouble() * 0.5;
      this.retransmits = 0;
    }

    @Override public void run() {
      if (openOutgoingConMsg.contains(rcptAddress, coapMessage.getMessageID())) {
        if (retransmits == MAX_RETRANSMITS) {
          byte[] removedToken;
          synchronized (openOutgoingConMsg) {
            removedToken = openOutgoingConMsg.remove(rcptAddress, coapMessage.getMessageID());
          }
          if (removedToken != null) {
            log.debug("Message with ID " + coapMessage.getMessageID() + " for recipient " + rcptAddress + " reached the maximum number of retransmits.");
          }
        } else {
          ChannelFuture future = Channels.future(datagramChannel);
          Channels.write(datagramChannel.getPipeline().getContext("OutgoingMessageReliabilityHandler"), future, coapMessage, rcptAddress);
          retransmits += 1;
          future.addListener(new ChannelFutureListener() {
            @Override public void operationComplete(ChannelFuture future) throws Exception {
              log.debug("Retransmit no " + retransmits + " for message " + "with ID " + coapMessage.getMessageID() + " for recipient " + rcptAddress + " finished.");
            }
          });
          int delay = (int) (Math.pow(2, retransmits) * TIMEOUT_MILLIS * randomFactor);
          executorService.schedule(this, delay, TimeUnit.MILLISECONDS);
          if (retransmits + 1 <= MAX_RETRANSMITS) {
            log.debug("Retransmit no " + (retransmits + 1) + " for " + coapMessage.getMessageType() + " message with ID " + coapMessage.getMessageID() + " to be confirmed by " + rcptAddress + " scheduled with a delay of " + delay + " millis.");
          } else {
            log.debug("Removal of " + coapMessage.getMessageType() + " message with ID " + coapMessage.getMessageID() + " to be confirmed by " + rcptAddress + " from the list of messages to be confirmed scheduled with a delay " + "of " + delay + " millis.");
          }
        }
      }
    }
  }
=======
  private class MessageRetransmitter implements Runnable {
    private DatagramChannel datagramChannel = CoapClientDatagramChannelFactory.getInstance().getChannel();

    private InetSocketAddress rcptAddress;

    private CoapMessage coapMessage;

    private double randomFactor;

    private int retransmits;

    public MessageRetransmitter(InetSocketAddress rcptAddress, CoapMessage coapMessage) {
      this.rcptAddress = rcptAddress;
      this.coapMessage = coapMessage;
      this.randomFactor = 1 + random.nextDouble() * 0.5;
      this.retransmits = 0;
    }

    @Override public void run() {
      if (openOutgoingConMsg.contains(rcptAddress, coapMessage.getMessageID())) {
        if (retransmits == MAX_RETRANSMITS) {
          byte[] removedToken;
          synchronized (openOutgoingConMsg) {
            removedToken = openOutgoingConMsg.remove(rcptAddress, coapMessage.getMessageID());
          }
          if (removedToken != null) {
            log.debug("Message with ID " + coapMessage.getMessageID() + " for recipient " + rcptAddress + " reached the maximum number of retransmits.");
          }
          String errorMessage = "Despite " + MAX_RETRANSMITS + " retransmits of the message with ID " + coapMessage.getMessageID() + " there was no response received from " + rcptAddress + ". Request timed out.";
          log.error(errorMessage);
          UpstreamMessageEvent ume = new UpstreamMessageEvent(datagramChannel, new InternalErrorMessage(errorMessage, removedToken), rcptAddress);
          datagramChannel.getPipeline().getContext("OutgoingMessageReliabilityHandler").sendUpstream(ume);
          return;
        } else {
          ChannelFuture future = Channels.future(datagramChannel);
          Channels.write(datagramChannel.getPipeline().getContext("OutgoingMessageReliabilityHandler"), future, coapMessage, rcptAddress);
          retransmits += 1;
          future.addListener(new ChannelFutureListener() {
            @Override public void operationComplete(ChannelFuture future) throws Exception {
              log.debug("Retransmit no " + retransmits + " for message " + "with ID " + coapMessage.getMessageID() + " for recipient " + rcptAddress + " finished.");
            }
          });
          int delay = (int) (Math.pow(2, retransmits) * TIMEOUT_MILLIS * randomFactor);
          executorService.schedule(this, delay, TimeUnit.MILLISECONDS);
          if (retransmits + 1 <= MAX_RETRANSMITS) {
            log.debug("Retransmit no " + (retransmits + 1) + " for " + coapMessage.getMessageType() + " message with ID " + coapMessage.getMessageID() + " to be confirmed by " + rcptAddress + " scheduled with a delay of " + delay + " millis.");
          } else {
            log.debug("Removal of " + coapMessage.getMessageType() + " message with ID " + coapMessage.getMessageID() + " to be confirmed by " + rcptAddress + " from the list of messages to be confirmed scheduled with a delay " + "of " + delay + " millis.");
          }
        }
      }
    }
  }
>>>>>>> /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/reliability/OutgoingMessageReliabilityHandler.java/right.java
}