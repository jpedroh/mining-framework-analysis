package com.spotify.netty.handler.codec.zmtp;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import org.jeromq.ZFrame;
import org.junit.After;
import org.jeromq.ZMQ;
import org.junit.Before;
import org.jeromq.ZMsg;
import org.junit.Test;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ZMQIntegrationTest {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  private ServerBootstrap serverBootstrap;
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ZMQIntegrationTest.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  private Channel serverChannel;
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ZMQIntegrationTest.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  private InetSocketAddress serverAddress;
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ZMQIntegrationTest.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  private String identity = "identity";
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ZMQIntegrationTest.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  private BlockingQueue<ZMTPIncomingMessage> incomingMessages = new LinkedBlockingQueue<ZMTPIncomingMessage>();
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ZMQIntegrationTest.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  private BlockingQueue<Channel> channelsConnected = new LinkedBlockingQueue<Channel>();
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ZMQIntegrationTest.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Before public void setup() {
    serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
    serverBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      Executor executor = new OrderedMemoryAwareThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 1024 * 1024, 128 * 1024 * 1024);

      public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(new ExecutionHandler(executor), new ZMTP20Codec(new ZMTPSession(ZMTPConnectionType.Addressed, 1024, identity.getBytes(), ZMTPSocketType.REQ), false), new SimpleChannelUpstreamHandler() {
          @Override public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
            super.channelConnected(ctx, e);
            channelsConnected.add(ctx.getChannel());
          }

          @Override public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
            incomingMessages.put((ZMTPIncomingMessage) e.getMessage());
          }
        });
      }
    });
    serverChannel = serverBootstrap.bind(new InetSocketAddress("localhost", 0));
    serverAddress = (InetSocketAddress) serverChannel.getLocalAddress();
  }
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ZMQIntegrationTest.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @After public void teardown() {
    if (serverChannel != null) {
      serverChannel.close();
      serverChannel.getCloseFuture().awaitUninterruptibly();
    }
    if (serverBootstrap != null) {
      serverBootstrap.releaseExternalResources();
    }
  }
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ZMQIntegrationTest.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void testZmqDealer() throws Exception {
    final ZMQ.Context context = ZMQ.context(1);
    final ZMQ.Socket socket = context.socket(ZMQ.DEALER);
    socket.connect("tcp://" + serverAddress.getHostName() + ":" + serverAddress.getPort());
    final ZMsg request = ZMsg.newStringMsg("envelope", "", "hello", "world");
    request.send(socket, false);
    final ZMTPIncomingMessage receivedRequest = incomingMessages.take();
    final ZMTPMessage receivedMessage = receivedRequest.getMessage();
    receivedRequest.getSession().getChannel().write(receivedMessage);
    final ZMsg reply = ZMsg.recvMsg(socket);
    Iterator<ZFrame> reqIter = request.iterator();
    Iterator<ZFrame> replyIter = reply.iterator();
    while (reqIter.hasNext()) {
      assertTrue(replyIter.hasNext());
      assertArrayEquals(reqIter.next().data(), replyIter.next().data());
    }
    assertFalse(replyIter.hasNext());
    assertEquals(1, receivedMessage.getEnvelope().size());
    assertEquals(2, receivedMessage.getContent().size());
    assertArrayEquals("envelope".getBytes(), receivedMessage.getEnvelope().get(0).getData());
    assertArrayEquals("hello".getBytes(), receivedMessage.getContent().get(0).getData());
    assertArrayEquals("world".getBytes(), receivedMessage.getContent().get(1).getData());
  }
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ZMQIntegrationTest.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void testZmqRouter() throws Exception {
    final ZMQ.Context context = ZMQ.context(1);
    final ZMQ.Socket socket = context.socket(ZMQ.ROUTER);
    socket.connect("tcp://" + serverAddress.getHostName() + ":" + serverAddress.getPort());
    final ZMTPMessage request = new ZMTPMessage(asList(ZMTPFrame.create("envelope")), asList(ZMTPFrame.create("hello"), ZMTPFrame.create("world")));
    final Channel channel = channelsConnected.take();
    channel.write(request);
    final ZMsg receivedReply = ZMsg.recvMsg(socket);
    assertEquals(ZMsg.newStringMsg(identity, "envelope", "", "hello", "world"), receivedReply);
  }
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ZMQIntegrationTest.java/right.java
}