/*
 * Copyright (c) 2012-2013 Spotify AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.spotify.netty.handler.codec.zmtp;

import com.google.common.base.Charsets;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ProtocolViolationTests {

  private EmbeddedChannel serverChannel;
  private String identity = "identity";
  private ChannelInboundHandler mockHandler = mock(ChannelInboundHandler.class);

  @Before
  public void setup() {
<<<<<<< /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ProtocolViolationTests.java/left.java
    ZMTPSession session = new ZMTPSession(Addressed, identity.getBytes());
    serverChannel = new EmbeddedChannel(
        new ZMTPFramingDecoder(session),
        new ZMTPFramingEncoder(session),
        mockHandler);
||||||| /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ProtocolViolationTests.java/base.java
    serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
        Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

    serverBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      Executor executor = new OrderedMemoryAwareThreadPoolExecutor(
          Runtime.getRuntime().availableProcessors(),
          1024 * 1024,
          128 * 1024 * 1024
      );

      public ChannelPipeline getPipeline() throws Exception {
        final ZMTPSession session = new ZMTPSession(Addressed, identity.getBytes());

        return Channels.pipeline(
            new ExecutionHandler(executor),
            new ZMTPFramingDecoder(session),
            new ZMTPFramingEncoder(session),
            new SimpleChannelUpstreamHandler() {

              @Override
              public void channelConnected(final ChannelHandlerContext ctx,
                                           final ChannelStateEvent e) throws Exception {
                mockHandler.channelConnected(ctx, e);
              }

              @Override
              public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
                  throws Exception {
                mockHandler.messageReceived(ctx, e);
              }
            });
      }
    });

    serverChannel = serverBootstrap.bind(new InetSocketAddress("localhost", 0));
    serverAddress = (InetSocketAddress) serverChannel.getLocalAddress();
=======
    serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
        Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

    serverBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      Executor executor = new OrderedMemoryAwareThreadPoolExecutor(
          Runtime.getRuntime().availableProcessors(),
          1024 * 1024,
          128 * 1024 * 1024
      );

      public ChannelPipeline getPipeline() throws Exception {

        return Channels.pipeline(
            new ExecutionHandler(executor),
            new ZMTP10Codec(new ZMTPSession(ZMTPConnectionType.Addressed, identity.getBytes())),
            new SimpleChannelUpstreamHandler() {

              @Override
              public void channelConnected(final ChannelHandlerContext ctx,
                                           final ChannelStateEvent e) throws Exception {
                mockHandler.channelConnected(ctx, e);
              }

              @Override
              public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
                  throws Exception {
                mockHandler.messageReceived(ctx, e);
              }
            });
      }
    });

    serverChannel = serverBootstrap.bind(new InetSocketAddress("localhost", 0));
    serverAddress = (InetSocketAddress) serverChannel.getLocalAddress();
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ProtocolViolationTests.java/right.java
  }

  @After
  public void teardown() {
    if (serverChannel != null) {
      serverChannel.close();
    }
  }

  @Test
  public void testBadConnection() throws Exception {
    for (int i = 0; i < 32; i++) {
      testConnect(i);
    }
  }

<<<<<<< /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ProtocolViolationTests.java/left.java
  private void testConnect(final int payloadSize) throws Exception {
    System.out.println("payloadSize=" + payloadSize);
||||||| /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ProtocolViolationTests.java/base.java
  private void testConnect(final int payloadSize) throws InterruptedException {
    final ClientBootstrap clientBootstrap =
        new ClientBootstrap(new NioClientSocketChannelFactory());
    clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      @Override
      public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(new SimpleChannelUpstreamHandler());
      }
    });
    final ChannelFuture future = clientBootstrap.connect(serverAddress);
    future.awaitUninterruptibly();

    final Channel channel = future.getChannel();

    System.out.println("payloadSize=" + payloadSize);
=======
  private void testConnect(final int payloadSize) throws InterruptedException {
    final ClientBootstrap clientBootstrap =
        new ClientBootstrap(new NioClientSocketChannelFactory());
    clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      @Override
      public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(new SimpleChannelUpstreamHandler());
      }
    });
    final ChannelFuture future = clientBootstrap.connect(serverAddress);
    future.awaitUninterruptibly();

    final Channel channel = future.getChannel();
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/test/java/com/spotify/netty/handler/codec/zmtp/ProtocolViolationTests.java/right.java

    StringBuilder payload = new StringBuilder();
    for (int i = 0; i < payloadSize; i++) {
      payload.append('0');
    }

    serverChannel.writeInbound(Unpooled.copiedBuffer(payload, Charsets.UTF_8));

	  // TODO- verify it's ok to remove this
//    verify(mockHandler, never())
//        .channelActive(any(ChannelHandlerContext.class));
    Assert.assertNull(serverChannel.readInbound());
  }
}
