package org.enderstone.server.packet.play;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EntityItem;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.regions.BlockId;

public class PacketInPlayerDigging extends Packet {
  private byte status;

  private Location loc;

  private byte face;

  @Override public void read(ByteBuf buf) throws IOException {
    this.status = buf.readByte();
    this.loc = readLocation(buf);
    this.face = buf.readByte();
  }

  @Override public void write(ByteBuf buf) throws IOException {
    throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
  }

  @Override public int getSize() throws IOException {
    return 2 + getLocationSize() + getVarIntSize(getId());
  }

  @Override public byte getId() {
    return 0x07;
  }

  @Override public void onRecieve(final NetworkManager networkManager) {
    Main.getInstance().sendToMainThread(new Runnable() {
      @Override public void run() {
        int x = getLocation().getBlockX();
        int y = getLocation().getBlockY();
        int z = getLocation().getBlockZ();
        short blockId = Main.getInstance().mainWorld.getBlockIdAt(x, y, z).getId();

<<<<<<< /usr/src/app/output/sander2798/enderstone/febdb02eb304ada845e62272290b13d31e533cf2/src/org/enderstone/server/packet/play/PacketInPlayerDigging.java/left.java
        if (getStatus() == 2) {
          if (networkManager.player.getLocation().isInRange(6, loc)) {
            Main.getInstance().mainWorld.setBlockAt(x, y, z, BlockId.AIR, (byte) 0);
          }
          Main.getInstance().mainWorld.broadcastSound("dig.grass", x, y, z, 1F, (byte) 63, loc, networkManager.player);
          Main.getInstance().mainWorld.addEntity(new EntityItem(loc, new ItemStack(blockId, (byte) 1, (short) 0, false)));
        }
=======
        switch (getStatus()) {
          case 2:
          {
            if (networkManager.player.getLocation().isInRange(6, loc)) {
              Main.getInstance().mainWorld.setBlockAt(x, y, z, BlockId.AIR, (byte) 0);
            }
            Main.getInstance().mainWorld.broadcastSound("dig.grass", x, y, z, 1F, (byte) 63, loc, networkManager.player);
            Main.getInstance().mainWorld.addEntity(new EntityItem(loc, new ItemStack(blockId, (byte) 1, (short) networkManager.player.world.getBlockDataAt(x, y, z))));
          }
          break;
          case 3:
          case 4:
          {
            networkManager.player.getInventoryHandler().recievePacket(PacketInPlayerDigging.this);
          }
          break;
        }
>>>>>>> /usr/src/app/output/sander2798/enderstone/febdb02eb304ada845e62272290b13d31e533cf2/src/org/enderstone/server/packet/play/PacketInPlayerDigging.java/right.java
      }
    });
  }

  public byte getStatus() {
    return status;
  }

  public Location getLocation() {
    return loc;
  }

  public byte getFace() {
    return face;
  }

  @Override public String toString() {
    return "PacketInPlayerDigging{" + "status=" + status + ", loc=" + loc + ", face=" + face + '}';
  }
}