package com.mpush.netty.codec;
import com.mpush.api.protocol.Packet;
import com.mpush.api.protocol.UDPPacket;
import com.mpush.tools.config.CC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;

/**
 * Created by ohun on 2015/12/19.
 * length(4)+cmd(1)+cc(2)+flags(1)+sessionId(4)+lrc(1)+body(n)
 *
 * @author ohun@live.cn
 */
public final class PacketDecoder extends ByteToMessageDecoder {
  private static final int maxPacketSize = CC.mp.core.max_packet_size;

  @Override protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    decodeHeartbeat(in, out);
    decodeFrames(in, out);
  }

  private void decodeHeartbeat(ByteBuf in, List<Object> out) {
    while (in.isReadable()) {
      if (in.readByte() == Packet.HB_PACKET_BYTE) {
        out.add(Packet.HB_PACKET);
      } else {
        in.readerIndex(in.readerIndex() - 1);
        break;
      }
    }
  }

  private void decodeFrames(ByteBuf in, List<Object> out) throws Exception {
    if (in.readableBytes() >= Packet.HEADER_LEN) {
      in.markReaderIndex();
      Packet packet = decodeFrame(in);
      if (packet != null) {
        out.add(packet);
      } else {
        in.resetReaderIndex();
      }
    }
  }

  private Packet decodeFrame(ByteBuf in) throws Exception {
    int readableBytes = in.readableBytes();
    int bodyLength = in.readInt();
    if (readableBytes < (bodyLength + Packet.HEADER_LEN)) {
      return null;
    }
    return readPacket(new Packet(in.readByte()), in, bodyLength);
  }

  public static Packet decodeFrame(DatagramPacket datagram) throws Exception {
    ByteBuf in = datagram.content();
    int readableBytes = in.readableBytes();
    int bodyLength = in.readInt();
    if (readableBytes < (bodyLength + Packet.HEADER_LEN)) {
      return null;
    }
    return readPacket(new UDPPacket(in.readByte(), datagram.sender()), in, bodyLength);
  }

  private static Packet readPacket(Packet packet, ByteBuf in, int bodyLength) {
    packet.cc = in.readShort();
    packet.flags = in.readByte();
    packet.sessionId = in.readInt();
    packet.lrc = in.readByte();
    if (bodyLength > 0) {
      if (bodyLength > maxPacketSize) {
        throw new TooLongFrameException("packet body length over limit:" + bodyLength);
      }
      in.readBytes(packet.body = new byte[bodyLength]);
    }
    return packet;
  }
}