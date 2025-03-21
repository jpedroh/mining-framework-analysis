package de.uniluebeck.itm.spitfire.nCoap.communication.core;
import de.uniluebeck.itm.spitfire.nCoap.application.CoapServerApplication;
import de.uniluebeck.itm.spitfire.nCoap.configuration.Configuration;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import java.net.*;
import java.util.concurrent.Executors;

/**
 * @author Oliver Kleine
 */
public class CoapServerDatagramChannelFactory {

<<<<<<< /usr/src/app/output/okleine/ncoap/805f73110b467c848fed1ff26471fff425614f01/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/core/CoapServerDatagramChannelFactory.java/left.java
  private static Logger log = Logger.getLogger(CoapServerDatagramChannelFactory.class.getName());
=======
>>>>>>> Unknown file: This is a bug in JDime.


  public static int COAP_SERVER_PORT = Configuration.getInstance().getInt("server.port", 5683);

  private DatagramChannel channel;

  public CoapServerDatagramChannelFactory(
<<<<<<< /usr/src/app/output/okleine/ncoap/805f73110b467c848fed1ff26471fff425614f01/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/core/CoapServerDatagramChannelFactory.java/left.java
  CoapServerApplication coapServerApplication
=======
  CoapServerApplication serverApp
>>>>>>> /usr/src/app/output/okleine/ncoap/805f73110b467c848fed1ff26471fff425614f01/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/core/CoapServerDatagramChannelFactory.java/right.java
  ) {
    ChannelFactory channelFactory = new NioDatagramChannelFactory(Executors.newCachedThreadPool());
    ConnectionlessBootstrap bootstrap = new ConnectionlessBootstrap(channelFactory);
    bootstrap.setPipelineFactory(new CoapServerPipelineFactory(
<<<<<<< /usr/src/app/output/okleine/ncoap/805f73110b467c848fed1ff26471fff425614f01/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/core/CoapServerDatagramChannelFactory.java/left.java
    coapServerApplication
=======
    serverApp
>>>>>>> /usr/src/app/output/okleine/ncoap/805f73110b467c848fed1ff26471fff425614f01/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/core/CoapServerDatagramChannelFactory.java/right.java
    ));
    InetAddress localAddress = null;
    try {
      localAddress = NetworkInterface.getByName("eth4").getInetAddresses().nextElement();
    } catch (SocketException e) {
      log.fatal("[" + this.getClass().getName() + "] " + e.getClass().getName(), e);
    }
    channel = (DatagramChannel) bootstrap.bind(new InetSocketAddress(localAddress, COAP_SERVER_PORT));
  }

  public DatagramChannel getChannel() {
    return channel;
  }
}