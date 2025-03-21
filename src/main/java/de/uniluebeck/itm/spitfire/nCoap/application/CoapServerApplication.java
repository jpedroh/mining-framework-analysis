package de.uniluebeck.itm.spitfire.nCoap.application;
import de.uniluebeck.itm.spitfire.nCoap.communication.core.CoapServerDatagramChannelFactory;
import de.uniluebeck.itm.spitfire.nCoap.helper.Helper;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapRequest;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapResponse;
import de.uniluebeck.itm.spitfire.nCoap.message.header.InvalidHeaderException;
import de.uniluebeck.itm.spitfire.nCoap.message.options.InvalidOptionException;
import de.uniluebeck.itm.spitfire.nCoap.message.options.ToManyOptionsException;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.DatagramChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Abstract class to be extended by a CoAP server application. Even though the communication is based on the Netty
 * framework, a developer of such a server doesn't have to go into details regarding the architecture. The whole
 * architecture is hidden from the users perspective. Technically speaking, the extending class will be the
 * topmost {@link ChannelUpstreamHandler} of the automatically generated netty handler stack.
 *
 * @author Oliver Kleine
 */
public abstract class CoapServerApplication extends SimpleChannelUpstreamHandler {
  private static Logger log = Logger.getLogger(CoapServerApplication.class.getName());

  protected final DatagramChannel channel = new CoapServerDatagramChannelFactory(this).getChannel();

  private final ExecutorService executorService = Executors.newCachedThreadPool();

  /**
     * This method is called by the Netty framework whenever a new message is received to be processed by the server.
     * For each incoming request a new Thread is created to handle the request (by invoking the method
     * <code>receiveCoapRequest</code>).
     *
     * @param ctx The {@link ChannelHandlerContext} connecting relating this class (which implements the
     * {@link ChannelUpstreamHandler} interface) to the channel that received the message.
     * @param me the {@link MessageEvent} containing the actual message
     */
  @Override public final void messageReceived(ChannelHandlerContext ctx, MessageEvent me) {
    if (log.isDebugEnabled()) {
      log.debug("[CoapServerApplication] Handle Upstream Message Event.");
    }
    if (me.getMessage() instanceof CoapRequest) {
      CoapRequest coapRequest = (CoapRequest) me.getMessage();
      executorService.execute(new CoapRequestExecutor(coapRequest, (InetSocketAddress) me.getRemoteAddress()));
      return;
    }
    ctx.sendUpstream(me);
  }

  /**
     * Shuts the server down by closing the channel which includes to unbind the channel from a listening port and
     * by this means free the port. All blocked or bound external resources are released.
     */
  public void shutdown() {
    ChannelFuture future = channel.close();
    future.addListener(new ChannelFutureListener() {
      @Override public void operationComplete(ChannelFuture future) throws Exception {
        log.info("[ServerApplication] Channel closed.");
        channel.getFactory().releaseExternalResources();
        log.info("[ServerApplication] Externeal resources released. Shutdown completed.");
      }
    });
  }

  /**
     * Method to be overridden by extending classes to handle incoming coapRequests
     * @param coapRequest The incoming {@link CoapRequest} object
     * @return the {@link CoapResponse} object to be sent back
     */
  public abstract CoapResponse receiveCoapRequest(CoapRequest coapRequest, InetSocketAddress senderAddress);

  private class CoapRequestExecutor implements Runnable {
    private CoapRequest coapRequest;

    private InetSocketAddress remoteAddress;

    public CoapRequestExecutor(CoapRequest coapRequest, InetSocketAddress remoteAddress) {
      this.coapRequest = coapRequest;
      this.remoteAddress = remoteAddress;
    }

    @Override public void run() {
      try {
        CoapResponse coapResponse = receiveCoapRequest(coapRequest, remoteAddress);
        coapResponse.setMessageID(coapRequest.getMessageID());
        if (coapRequest.getToken().length > 0) {
          coapResponse.setToken(coapRequest.getToken());
        }
        ChannelFuture future = Channels.write(channel, coapResponse, remoteAddress);
        if (log.isDebugEnabled()) {
          future.addListener(new ChannelFutureListener() {
            @Override public void operationComplete(ChannelFuture future) throws Exception {
              log.debug("[ServerApplication | CoapRequestExecutor] Sending of response to recipient " + remoteAddress + " with message ID " + coapRequest.getMessageID() + " and " + "token " + Helper.toHexString(coapRequest.getToken()) + " completed.");
            }
          });
        }
      } catch (InvalidHeaderException e) {
        log.fatal("[ServerApplication | CoapRequestExecutor] Error while setting message ID or token for " + " response.", e);
      } catch (ToManyOptionsException e) {
        log.fatal("[ServerApplication | CoapRequestExecutor] Error while setting message ID or token for " + " response.", e);
      } catch (InvalidOptionException e) {
        log.fatal("[ServerApplication | CoapRequestExecutor] Error while setting message ID or token for " + " response.", e);
      }
    }
  }
}