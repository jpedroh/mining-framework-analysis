package de.uniluebeck.itm.spitfire.nCoap.communication.core;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapMessage;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictor;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import java.util.concurrent.Executors;
import org.slf4j.LoggerFactory;

/**
 * @author Oliver Kleine
 */
public class CoapClientDatagramChannelFactory {
  public static int COAP_CLIENT_PORT = 5682;

  private static Logger log = LoggerFactory.getLogger(CoapClientDatagramChannelFactory.class.getName());

  private DatagramChannel channel;

  private static CoapClientDatagramChannelFactory instance = new CoapClientDatagramChannelFactory();

  static {
    FixedReceiveBufferSizePredictor predictor = new FixedReceiveBufferSizePredictor(34000);
    instance.getChannel().getConfig().setReceiveBufferSizePredictor(predictor);
  }

  public static synchronized CoapClientDatagramChannelFactory getInstance() {
    return instance;
  }

  private CoapClientDatagramChannelFactory() {
    ChannelFactory channelFactory = new NioDatagramChannelFactory(CoapExecutorService.getExecutorService());
    ConnectionlessBootstrap bootstrap = new ConnectionlessBootstrap(channelFactory);
    bootstrap.setPipelineFactory(new CoapClientPipelineFactory());
    int triesLeft = 10;
    for (int port = COAP_CLIENT_PORT; channel == null && triesLeft > 0; port++, triesLeft--) {
      try {
        channel = (DatagramChannel) bootstrap.bind(new InetSocketAddress(port));
      } catch (Exception e) {
        log.info(String.format("Exception while binding client on port %d. %d tries left." + "(Port maybe port already in use?) Message: %s", port, triesLeft, e.getMessage()));
      }
    }
  }

  public DatagramChannel getChannel() {
    return channel;
  }
}