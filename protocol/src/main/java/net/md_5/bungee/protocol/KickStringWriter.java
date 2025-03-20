package net.md_5.bungee.protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/3c8bdedc27c6e7e1e4a8df2747f49bbc0a379dc1/protocol/src/main/java/net/md_5/bungee/protocol/KickStringWriter.java/left.java
Sharable
=======
ChannelHandler.Sharable
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/3c8bdedc27c6e7e1e4a8df2747f49bbc0a379dc1/protocol/src/main/java/net/md_5/bungee/protocol/KickStringWriter.java/right.java
 public class KickStringWriter extends MessageToByteEncoder<String> {
  @Override protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
    out.writeByte(0xFF);
    out.writeShort(msg.length());
    for (char c : msg.toCharArray()) {
      out.writeChar(c);
    }
  }
}