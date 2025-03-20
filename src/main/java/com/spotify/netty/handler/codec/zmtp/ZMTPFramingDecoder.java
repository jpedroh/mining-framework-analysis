package com.spotify.netty.handler.codec.zmtp;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * Netty FrameDecoder for zmtp protocol
 *
 * Decodes ZMTP frames into a ZMTPMessage - will return a ZMTPMessage as a message event
 */
class ZMTPFramingDecoder extends ByteToMessageDecoder {
  private final ZMTPMessageParser parser;

  private final ZMTPSession session;

  public ZMTPFramingDecoder(final ZMTPSession session) {
    this.parser = new ZMTPMessageParser(session.isEnveloped(), session.getSizeLimit(), session.getActualVersion());
    this.session = session;
  }


<<<<<<< /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/main/java/com/spotify/netty/handler/codec/zmtp/ZMTPFramingDecoder.java/left.java
  /**
   * Sends my local identity
   */
  private ChannelFuture sendIdentity(final Channel channel) {
    final ByteBuf msg;
    if (session.useLocalIdentity()) {
      msg = Unpooled.buffer(2 + session.getLocalIdentity().length);
      ZMTPUtils.encodeLength(1 + session.getLocalIdentity().length, msg);
      msg.writeByte(FINAL_FLAG);
      msg.writeBytes(session.getLocalIdentity());
    } else {
      msg = Unpooled.buffer(2);
      msg.writeByte(1);
      msg.writeByte(FINAL_FLAG);
    }
    return channel.writeAndFlush(msg);
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.



<<<<<<< /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/main/java/com/spotify/netty/handler/codec/zmtp/ZMTPFramingDecoder.java/left.java
  /**
   * Parses the remote zmtp identity received
   */
  private boolean handleRemoteIdentity(final ByteBuf buffer) throws ZMTPException {
    buffer.markReaderIndex();
    final long len = ZMTPUtils.decodeLength(buffer);
    if (len > 256) {
      throw new ZMTPException("Remote identity longer than the allowed 255 octets");
    }
    if (len == -1 || buffer.readableBytes() < len) {
      buffer.resetReaderIndex();
      return false;
    }
    buffer.readByte();
    if (len == 1) {
      session.setRemoteIdentity(null);
    } else {
      final byte[] identity = new byte[(int) len - 1];
      buffer.readBytes(identity);
      session.setRemoteIdentity(identity);
    }
    return true;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public void channelActive(final ChannelHandlerContext ctx) throws Exception {
    this.session.setChannel(ctx.channel());
    sendIdentity(ctx.channel()).addListener(new ChannelFutureListener() {
      @Override public void operationComplete(final ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
          ctx.fireChannelActive();
        } else {
          throw new ZMTPException("handshake failed", future.cause());
        }
      }
    });
  }

  @Override protected void decode(ChannelHandlerContext ctx, 
<<<<<<< /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/main/java/com/spotify/netty/handler/codec/zmtp/ZMTPFramingDecoder.java/left.java
  ByteBuf in
=======
  Channel channel
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/main/java/com/spotify/netty/handler/codec/zmtp/ZMTPFramingDecoder.java/right.java
  , 
<<<<<<< /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/main/java/com/spotify/netty/handler/codec/zmtp/ZMTPFramingDecoder.java/left.java
  List<Object> out
=======
  ChannelBuffer buffer
>>>>>>> /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/main/java/com/spotify/netty/handler/codec/zmtp/ZMTPFramingDecoder.java/right.java
  ) throws Exception {

<<<<<<< /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/main/java/com/spotify/netty/handler/codec/zmtp/ZMTPFramingDecoder.java/left.java
    if (in.readableBytes() < 2) {
      return;
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/main/java/com/spotify/netty/handler/codec/zmtp/ZMTPFramingDecoder.java/left.java
    if (session.getRemoteIdentity() == null) {
      if (!handleRemoteIdentity(in)) {
        return;
      }
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    ZMTPParsedMessage msg = parser.parse(in);
    if (msg == null) {
      return;
    }
    out.add(new ZMTPIncomingMessage(session, msg.getMessage(), msg.isTruncated(), msg.getByteSize()));
  }
}