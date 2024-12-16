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

	private int x;

	private byte y;

	private int z;

	private byte face;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.status = buf.readByte();
		this.x = buf.readInt();
		this.y = buf.readByte();
		this.z = buf.readInt();
		this.face = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException(((("Packet " + this.getClass().getSimpleName()) + " with ID 0x") + Integer.toHexString(getId())) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return (3 + (2 * getIntSize())) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x07;
	}

	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {
			@Override
			public void run() {
				Location loc = new Location("", getX(), getY(), getZ(), 0.0F, 0.0F);
				short blockId = Main.getInstance().mainWorld.getBlockIdAt(x, y, z).getId();
				if (getStatus() == 2) {
					if (networkManager.player.getLocation().isInRange(6, loc)) {
						Main.getInstance().mainWorld.setBlockAt(getX(), getY(), getZ(), BlockId.AIR, ((byte) (0)));
					}
					Main.getInstance().mainWorld.broadcastSound("dig.grass", x, y, z, 1.0F, ((byte) (63)), loc, networkManager.player);
					Main.getInstance().mainWorld.addEntity(new EntityItem(loc, new ItemStack(((short) (2)), ((byte) (4)), ((short) (0)))));
				}
			}
		});
	}

	public byte getStatus() {
		return status;
	}

	public int getX() {
		return x;
	}

	public byte getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public byte getFace() {
		return face;
	}
}