package de.uniluebeck.itm.spitfire.nCoap.application;
import de.uniluebeck.itm.spitfire.nCoap.communication.callback.ResponseCallback;
import de.uniluebeck.itm.spitfire.nCoap.communication.core.CoapClientDatagramChannelFactory;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapRequest;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;

/**
 * This is the abstract class to be extended by a CoAP client.
 * By {@link #writeCoapRequest(de.uniluebeck.itm.spitfire.nCoap.message.CoapRequest)}it provides an
 * easy-to-use method to write CoAP requests to a server.
 *
 * @author Oliver Kleine
 */
public abstract class CoapClientApplication extends ResponseCallback {
  private final DatagramChannel channel = CoapClientDatagramChannelFactory.getInstance().getChannel();

  private Logger log = LoggerFactory.getLogger(CoapClientApplication.class.getName());

  /**
     * This method should be used by extending client implementation to send a CoAP request to a remote recipient. All
     * necessary information to send the message (like the recipient IP address or port) is automatically extracted
     * from the given {@link CoapRequest} object.
     * @param coapRequest The {@link CoapRequest} object to be sent
     */
  public final void writeCoapRequest(CoapRequest coapRequest) {
    final InetSocketAddress rcptSocketAddress = new InetSocketAddress(coapRequest.getTargetUri().getHost(), coapRequest.getTargetUri().getPort());
    ChannelFuture future = Channels.write(channel, coapRequest, rcptSocketAddress);
    future.addListener(new ChannelFutureListener() {
      @Override public void operationComplete(ChannelFuture future) throws Exception {
        log.info("CoAP Request sent to " + rcptSocketAddress.getAddress().getHostAddress() + ":" + rcptSocketAddress.getPort());
      }
    });
  }
}