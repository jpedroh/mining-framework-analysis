package org.enderstone.server.packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.NetworkManager.Stage;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;

public class NetworkManager extends ReplayingDecoder<Stage> {
  public ChannelHandlerContext ctx;

  private Channel channel;

  public EnderPlayer player;

  public PacketHandshake latestHandshakePacket;

  public volatile int handShakeStatus = -1;

  private int length;

  public NetworkManager() {
    super(Stage.LENGTH);
  }

  @Override protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> args) throws Exception {
    if (this.channel == null) {
      this.channel = ctx.channel();
    }
    if (this.ctx == null) {
      this.ctx = ctx;
    }
    if (this.state() == Stage.LENGTH) {
      this.length = Packet.readVarInt(buf);
      this.checkpoint(Stage.DATA);
    } else {
      if (this.state() == Stage.DATA) {
        buf.markReaderIndex();
        buf.readBytes(this.length);
        buf.resetReaderIndex();
        int id = Packet.readVarInt(buf);
        if (handShakeStatus == -1) {
          PacketHandshake packet = (PacketHandshake) PacketManager.handshake.newInstance();
          packet.read(buf);
          this.handShakeStatus = packet.getNextState();
          latestHandshakePacket = packet;
        } else {
          if (handShakeStatus == 1) {
            Packet packet = PacketManager.getPacket(this, id, HandshakeState.STATUS).newInstance();
            packet.read(buf);
            packet.onRecieve(this);
          } else {
            if (handShakeStatus == 2) {
              Packet packet = PacketManager.getPacket(this, id, HandshakeState.LOGIN).newInstance();
              packet.read(buf);
              packet.onRecieve(this);
            } else {
              if (handShakeStatus == 3) {
                Packet packet = PacketManager.getPacket(this, id, HandshakeState.PLAY).newInstance();
                packet.read(buf);
                packet.onRecieve(this);
              }
            }
          }
        }
        this.state(Stage.LENGTH);
      }
    }
  }

  @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    this.channelInactive(ctx);
    super.exceptionCaught(ctx, cause);
  }

  @Override public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    if (player != null) {
      final EnderPlayer subPlayer = player;
      Main.getInstance().sendToMainThread(new Runnable() {
        @Override public void run() {
          try {
            subPlayer.onDisconnect();
          } catch (Exception e) {
            e.printStackTrace();
          }
          if (Main.getInstance().onlinePlayers.contains(subPlayer)) {
            Main.getInstance().onlinePlayers.remove(subPlayer);
            for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
              for (String name : ep.visiblePlayers) {
                if (name.equals(subPlayer.getPlayerName()) && !subPlayer.getPlayerName().equals(ep.getPlayerName())) {
                  ep.getNetworkManager().sendPacket(new PacketOutEntityDestroy(new Integer[] { subPlayer.getEntityId() }));
                }
              }
            }
          }
        }
      });
      player = null;
    }
    super.channelInactive(ctx);
  }

  enum Stage {
    LENGTH,
    DATA
  }

  public synchronized void sendPacket(Packet... packets) {
    try {
      ByteBuf buf = Unpooled.buffer();
      for (Packet packet : packets) {
        Packet.writeVarInt(packet.getSize(), buf);
        Packet.writeVarInt(packet.getId(), buf);
        packet.write(buf);
      }
      this.channel.writeAndFlush(buf);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}