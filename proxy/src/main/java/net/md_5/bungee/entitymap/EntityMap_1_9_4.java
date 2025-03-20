package net.md_5.bungee.entitymap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.BungeeCord;
import java.util.UUID;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

class EntityMap_1_9_4 extends EntityMap {
  static final EntityMap_1_9_4 INSTANCE = new EntityMap_1_9_4();

  EntityMap_1_9_4() {
    addRewrite(0x00, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x01, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x03, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x04, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x05, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x06, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x08, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x1B, ProtocolConstants.Direction.TO_CLIENT, false);
    addRewrite(0x25, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x26, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x27, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x28, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x2F, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x31, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x34, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x36, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x39, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x3A, ProtocolConstants.Direction.TO_CLIENT, false);
    addRewrite(0x3B, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x3C, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x40, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x48, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x49, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x4A, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x4B, ProtocolConstants.Direction.TO_CLIENT, true);
    addRewrite(0x0A, ProtocolConstants.Direction.TO_SERVER, true);
    addRewrite(0x14, ProtocolConstants.Direction.TO_SERVER, true);
  }

  @Override @SuppressFBWarnings(value = "DLS_DEAD_LOCAL_STORE") public void rewriteClientbound(ByteBuf packet, int oldId, int newId) {
    super.rewriteClientbound(packet, oldId, newId);
    int readerIndex = packet.readerIndex();
    int packetId = DefinedPacket.readVarInt(packet);
    int packetIdLength = packet.readerIndex() - readerIndex;
    int jumpIndex = packet.readerIndex();
    switch (packetId) {
      case 0x3A:
      rewriteInt(packet, oldId, newId, readerIndex + packetIdLength + 4);
      break;
      case 0x48:
      DefinedPacket.readVarInt(packet);
      rewriteVarInt(packet, oldId, newId, packet.readerIndex());
      break;
      case 0x40:
      DefinedPacket.readVarInt(packet);
      jumpIndex = packet.readerIndex();
      case 0x30:
      int count = DefinedPacket.readVarInt(packet);
      int[] ids = new int[count];
      for (int i = 0; i < count; i++) {
        ids[i] = DefinedPacket.readVarInt(packet);
      }
      packet.readerIndex(jumpIndex);
      packet.writerIndex(jumpIndex);
      DefinedPacket.writeVarInt(count, packet);
      for (int id : ids) {
        if (id == oldId) {
          id = newId;
        } else {
          if (id == newId) {
            id = oldId;
          }
        }
        DefinedPacket.writeVarInt(id, packet);
      }
      break;
      case 0x00:
      DefinedPacket.readVarInt(packet);
      DefinedPacket.readUUID(packet);
      int type = packet.readUnsignedByte();
      if (type == 60 || type == 90 || type == 91) {
        if (type == 60 || type == 91) {
          oldId = oldId + 1;
          newId = newId + 1;
        }
        packet.skipBytes(26);
        int position = packet.readerIndex();
        int readId = packet.readInt();
        if (readId == oldId) {
          packet.setInt(position, newId);
        } else {
          if (readId == newId) {
            packet.setInt(position, oldId);
          }
        }
      }
      break;
      case 0x05:
      DefinedPacket.readVarInt(packet);
      int idLength = packet.readerIndex() - readerIndex - packetIdLength;
      UUID uuid = DefinedPacket.readUUID(packet);
      ProxiedPlayer player;
      if ((player = BungeeCord.getInstance().getPlayerByOfflineUUID(uuid)) != null) {
        int previous = packet.writerIndex();
        packet.readerIndex(readerIndex);
        packet.writerIndex(readerIndex + packetIdLength + idLength);
        DefinedPacket.writeUUID(player.getUniqueId(), packet);
        packet.writerIndex(previous);
      }
      break;
      case 0x2C:
      int event = packet.readUnsignedByte();
      if (event == 1) {
        DefinedPacket.readVarInt(packet);
        rewriteInt(packet, oldId, newId, packet.readerIndex());
      } else {
        if (event == 2) {
          int position = packet.readerIndex();
          rewriteVarInt(packet, oldId, newId, packet.readerIndex());
          packet.readerIndex(position);
          DefinedPacket.readVarInt(packet);
          rewriteInt(packet, oldId, newId, packet.readerIndex());
        }
      }
      break;
      case 0x39:
      DefinedPacket.readVarInt(packet);
      rewriteMetaVarInt(packet, oldId + 1, newId + 1, 5);
      break;
    }
    packet.readerIndex(readerIndex);
  }

  @Override public void rewriteServerbound(ByteBuf packet, int oldId, int newId) {
    super.rewriteServerbound(packet, oldId, newId);
    int readerIndex = packet.readerIndex();
    int packetId = DefinedPacket.readVarInt(packet);
    int packetIdLength = packet.readerIndex() - readerIndex;
    if (packetId == 0x1B && !BungeeCord.getInstance().getConfig().isIpForward()) {
      UUID uuid = DefinedPacket.readUUID(packet);
      ProxiedPlayer player;
      if ((player = BungeeCord.getInstance().getPlayer(uuid)) != null) {
        int previous = packet.writerIndex();
        packet.readerIndex(readerIndex);
        packet.writerIndex(readerIndex + packetIdLength);
        DefinedPacket.writeUUID(((UserConnection) player).getPendingConnection().getOfflineId(), packet);
        packet.writerIndex(previous);
      }
    }
    packet.readerIndex(readerIndex);
  }
}