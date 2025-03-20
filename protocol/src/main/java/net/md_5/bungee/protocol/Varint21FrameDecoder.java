package net.md_5.bungee.protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import ru.leymooo.botfilter.utils.FastCorruptedFrameException;

public class Varint21FrameDecoder extends ByteToMessageDecoder {
  private boolean fromBackend;

  public void setFromBackend(boolean fromBackend) {
    this.fromBackend = fromBackend;
  }

  @Override protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (!ctx.channel().isActive()) {

<<<<<<< /usr/src/app/output/spigotmc/bungeecord/a00614952fec07ee119140cac78e4d770cb9e068/protocol/src/main/java/net/md_5/bungee/protocol/Varint21FrameDecoder.java/left.java
      super.setSingleDecode(true)
=======
      in.skipBytes(in.readableBytes())
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/a00614952fec07ee119140cac78e4d770cb9e068/protocol/src/main/java/net/md_5/bungee/protocol/Varint21FrameDecoder.java/right.java
      ;
      return;
    }
    int origReaderIndex = in.readerIndex();
    int i = 3;
    while (i-- > 0) {
      if (!in.isReadable()) {
        in.readerIndex(origReaderIndex);
        return;
      }
      byte read = in.readByte();
      if (read >= 0) {
        in.readerIndex(origReaderIndex);
        int packetLength = DefinedPacket.readVarInt(in);
        if (packetLength <= 0 && !fromBackend) {
          super.setSingleDecode(true);
          throw new FastCorruptedFrameException("Empty Packet!");
        }
        if (in.readableBytes() < packetLength) {
          in.readerIndex(origReaderIndex);
          return;
        }
        out.add(in.readRetainedSlice(packetLength));
        return;
      }
    }
    super.setSingleDecode(true);
    throw new FastCorruptedFrameException("length wider than 21-bit");
  }
}