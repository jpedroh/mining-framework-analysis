package com.spotify.netty.handler.codec.zmtp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Netty encoder for ZMTP messages.
 */
class ZMTPFramingEncoder extends MessageToByteEncoder<ZMTPMessage> {
  private final ZMTPSession session;

  public ZMTPFramingEncoder(final ZMTPSession session) {
    this.session = session;
  }

  @Override protected void encode(ChannelHandlerContext ctx, ZMTPMessage msg, ByteBuf out) throws Exception {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    final int size = ZMTPUtils.messageSize(message, session.isEnveloped(), session.getActualVersion());
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/main/java/com/spotify/netty/handler/codec/zmtp/ZMTPFramingEncoder.java/right.java

    ZMTPUtils.writeMessage(msg, out, session.isEnveloped(), session.getActualVersion());
  }
}