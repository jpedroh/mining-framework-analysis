package org.enderstone.server.entity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.Utill;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutEntityHeadLook;
import org.enderstone.server.packet.play.PacketOutEntityLook;
import org.enderstone.server.packet.play.PacketOutEntityRelativeMove;
import org.enderstone.server.packet.play.PacketOutEntityTeleport;
import org.enderstone.server.packet.play.PacketOutPlayerListItem;
import org.enderstone.server.packet.play.PacketOutSoundEffect;
import org.enderstone.server.packet.play.PacketOutSpawnPlayer;
import org.enderstone.server.regions.EnderChunk;
import org.enderstone.server.regions.EnderWorld.ChunkInformer;
import org.enderstone.server.chat.Message;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.packet.play.PacketOutChatMessage;

public class EnderPlayer extends Entity implements CommandSender {
  public final NetworkManager networkManager;

  public final String playerName;

  public DataWatcher dataWatcher;

  public HashSet<String> visiblePlayers = new HashSet<>();

  public final String uuid;

  public String locale;

  public byte renderDistance;

  public byte chatFlags;

  public boolean chatColors;

  public byte difficulty;

  public boolean showCapes;

  public boolean isOnline = true;

  public boolean isCreative = true;

  public boolean godMode = true;

  public boolean canFly = true;

  public boolean isFlying = true;

  public boolean isOnFire = false;

  public boolean isSneaking = false;

  public boolean isSprinting = false;

  public boolean isEating = false;

  private boolean isInvisible = false;

  private volatile String textureValue = "eyJ0aW1lc3RhbXAiOjE0MDkwODUzMTUyOTUsInByb2ZpbGVJZCI6IjY3NDNhODE0OWQ0MTRkMzNhZjllZTE0M2JjMmQ0NjJjIiwicHJvZmlsZU5hbWUiOiJzYW5kZXIyNzk4IiwiaXNQdWJsaWMiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jYTgwYTQyMzVkMzc1N2Q0YWI0Nzg2ZGY0NzQxYzE1MmExMWM5ZGVjMGU1YWM5ZmJlOGVmMmM0MjA4YWM2In19fQ==";

  private volatile String textureSignature = "T/S5/8yNblHMtt5KCnFwymwHOF9RCPh223CwCc3wAUoBRDmYJR2jtlkoLltKp24YZa/s/NTtuaji9g4Dq6hkDC+WvAHJ3UxWHSixumG78EJQxUIHW0QD7wmkeAb2RfipuXG84gnzJ6gFz3aYz7vNM7eZ1dO0KCDKVawsvMkHUvM2BoRUh/rSj0ji6BlQ611FU1peMXep9oAPOcKZFK0snH4Su0qZt8n3dw5087RuhaBGmkT4nYrD7eH43uGDdXs5SLWzLd1d3oQzj0cGL7GiM1Jrg8DcaQoXXqMMuMThviHVi1YVM/sZ7eWVj5Ui4BVOTu2nGSH5Avegq4UOdBILfHadlFroKPEX5uRA3Od+/3hF7ZGBYv+W9/oA8P6gUsnEvAYC4TnM5KWViCg/aJ/7hDYeW6Nv0CjHHz7o3iNy2OxeL3X4jhLSlYRg4gEkejohN5NUeFi1ZRxvhPgJLr2aVKYsMNtKcLfRI567NxuRpLt4KAd62zxB5AzfWJd3qIK8q8a9fIfqiDJ8UHdW801Dhg2HSqmf9xzw3RPqOTkAX3gCpxBsfHedPzScW7RBEoyqIk9LEx5dZuVUBHOlPS2kk/8zTvKWGhFfJKmyrL159ZElPR9DjZoNN1LBmIJEAZ3jRfwZBDZVux8xUYpsrh1vT3DTP+lUMoD0oql3M3i/Lgg=";

  public int keepAliveID = 0;

  public ChunkInformer chunkInformer = new ChunkInformer() {
    @Override public boolean sendChunk(EnderChunk chunk) {
      networkManager.sendPacket(chunk.getCompressedChunk().toPacket(chunk.getX(), chunk.getZ()));
      return true;
    }

    @Override public boolean removeChunk(EnderChunk chunk) {
      return true;
    }
  };

  public EnderPlayer(String name, NetworkManager networkManager, String uuid, String textureValue, String textureSignature) {
    super(-1, new Location());
    this.networkManager = networkManager;
    this.playerName = name;
    this.uuid = uuid;
    if (textureValue != null && textureSignature != null) {
      this.textureValue = textureValue;
      this.textureSignature = textureSignature;
    }
    EnderLogger.info(name + " logged in.");
  }

  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    return result;
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    EnderPlayer other = (EnderPlayer) obj;
    if (uuid == null) {
      if (other.uuid != null) {
        return false;
      }
    } else {
      if (!uuid.equals(other.uuid)) {
        return false;
      }
    }
    return true;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public byte getRenderDistance() {
    return renderDistance;
  }

  public void setRenderDistance(byte renderDistance) {
    this.renderDistance = renderDistance;
  }

  public byte getChatFlags() {
    return chatFlags;
  }

  public void setChatFlags(byte chatFlags) {
    this.chatFlags = chatFlags;
  }

  public boolean hasChatColors() {
    return chatColors;
  }

  public void setChatColors(boolean chatColors) {
    this.chatColors = chatColors;
  }

  public byte getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(byte difficulty) {
    this.difficulty = difficulty;
  }

  public boolean getShowCapes() {
    return showCapes;
  }

  public void setShowCapes(boolean showCapes) {
    this.showCapes = showCapes;
  }

  public NetworkManager getNetworkManager() {
    return networkManager;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void onJoin() {
    this.updateDataWatcher();
    PacketOutPlayerListItem packet = new PacketOutPlayerListItem(this.getPlayerName(), this.isOnline, (short) 1);
    for (EnderPlayer player : Main.getInstance().onlinePlayers) {
      player.getNetworkManager().sendPacket(packet);
      this.getNetworkManager().sendPacket(new PacketOutPlayerListItem(player.getPlayerName(), true, (short) 1));
    }
    Utill.broadcastMessage(ChatColor.YELLOW + this.getPlayerName() + " joined the game!");
  }

  private void updateDataWatcher() {
    this.dataWatcher = new DataWatcher();
    int meaning = 0;
    if (isOnFire) {
      meaning = (byte) (meaning | 0x01);
    }
    if (isSneaking) {
      meaning = (byte) (meaning | 0x02);
    }
    if (isSprinting) {
      meaning = (byte) (meaning | 0x08);
    }
    if (isEating) {
      meaning = (byte) (meaning | 0x10);
    }
    if (isInvisible) {
      meaning = (byte) (meaning | 0x20);
    }
    this.dataWatcher.watch(0, (byte) meaning);
    this.dataWatcher.watch(1, (short) 0);
    this.dataWatcher.watch(6, 20F);
    this.dataWatcher.watch(8, (byte) 0);
  }

  @Override public Packet getSpawnPacket() {
    List<ProfileProperty> list = new ArrayList<>();
    ProfileProperty prop = new ProfileProperty("textures", this.textureValue, this.textureSignature);
    list.add(prop);
    return new PacketOutSpawnPlayer(this.getEntityId(), this.uuid, this.getPlayerName(), list, this.getLocation().getBlockX(), this.getLocation().getBlockY(), this.getLocation().getBlockZ(), (byte) this.getLocation().getYaw(), (byte) this.getLocation().getPitch(), (short) 0, this.dataWatcher);
  }

  public void onPlayerChat(String message) {
    if (message.startsWith("/")) {
      final String fullCommand = message.substring(1);
      final String[] split = fullCommand.split(" ");
      final String[] args;
      if (split.length != 1) {
        args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, args.length);
      } else {
        args = new String[0];
      }
      Main.getInstance().sendToMainThread(new Runnable() {
        @Override public void run() {
          Main.getInstance().commands.executeCommand(null, split[0], EnderPlayer.this, args);
        }
      });
    } else {
      Utill.broadcastMessage("<" + this.getPlayerName() + "> " + message);
    }
  }

  public void onDisconnect() throws Exception {
    this.isOnline = false;
    Utill.broadcastMessage(ChatColor.YELLOW + this.getPlayerName() + " left the game!");
    Main.getInstance().mainWorld.players.remove(this);
    for (EnderPlayer p : Main.getInstance().onlinePlayers) {
      p.getNetworkManager().sendPacket(new PacketOutPlayerListItem(this.getPlayerName(), false, (short) 1));
    }
  }

  public void updatePlayers(List<EnderPlayer> onlinePlayers) {
    Set<Integer> toDespawn = new HashSet<>();
    for (EnderPlayer pl : onlinePlayers) {
      if (!pl.getPlayerName().equals(this.getPlayerName()) && !this.visiblePlayers.contains(pl.getPlayerName()) && pl.getLocation().isInRange(50, this.getLocation())) {
        this.visiblePlayers.add(pl.getPlayerName());
        this.networkManager.sendPacket(pl.getSpawnPacket());
      }
      if (!pl.getPlayerName().equals(this.getPlayerName()) && this.visiblePlayers.contains(pl.getPlayerName()) && !pl.getLocation().isInRange(50, this.getLocation())) {
        this.visiblePlayers.remove(pl.getPlayerName());
        toDespawn.add(pl.getEntityId());
      }
    }
    if (!toDespawn.isEmpty()) {
      this.networkManager.sendPacket(new PacketOutEntityDestroy(toDespawn.toArray(new Integer[0])));
    }
  }

  private int moveUpdates = 0;

  public void broadcastLocation(Location newLocation) {
    double dx = (newLocation.getX() - this.getLocation().getX()) * 32;
    double dy = (newLocation.getY() - this.getLocation().getY()) * 32;
    double dz = (newLocation.getZ() - this.getLocation().getZ()) * 32;
    Packet packet;
    if (moveUpdates++ % 40 == 0 || dx > 127 || dx < -127 || dy > 127 || dy < -127 || dz > 127 || dz < -127) {
      packet = new PacketOutEntityTeleport(this.getEntityId(), (int) (this.getLocation().getX() * 32.0D), (int) (this.getLocation().getY() * 32.0D), (int) (this.getLocation().getZ() * 32.0D), (byte) this.getLocation().getYaw(), (byte) this.getLocation().getPitch());
    } else {
      packet = new PacketOutEntityRelativeMove(this.getEntityId(), (byte) dx, (byte) dy, (byte) dz);
    }
    Iterator<String> players = this.visiblePlayers.iterator();
    while (players.hasNext()) {
      EnderPlayer ep = Main.getInstance().getPlayer(players.next());
      if (ep == null) {
        players.remove();
        continue;
      }
      if (ep.getLocation().isInRange(50, this.getLocation())) {
        ep.networkManager.sendPacket(packet);
      }
    }
  }

  public void broadcastRotation(float pitch, float yaw) {
    Iterator<String> players = this.visiblePlayers.iterator();
    Packet pack1 = new PacketOutEntityLook(this.getEntityId(), (byte) Utill.calcYaw(yaw * 256.0F / 360.0F), (byte) Utill.calcYaw(pitch * 256.0F / 360.0F));
    Packet pack2 = new PacketOutEntityHeadLook(this.getEntityId(), (byte) Utill.calcYaw(yaw * 256.0F / 360.0F));
    while (players.hasNext()) {
      EnderPlayer ep = Main.getInstance().getPlayer(players.next());
      if (ep == null) {
        players.remove();
        continue;
      }
      if (ep.getLocation().isInRange(50, this.getLocation())) {
        ep.networkManager.sendPacket(pack1);
        ep.networkManager.sendPacket(pack2);
      }
    }
  }

  public void playSound(String soundName, float volume, byte pitch) {
    networkManager.sendPacket(new PacketOutSoundEffect(soundName, getLocation().getBlockX(), getLocation().getBlockY(), getLocation().getBlockZ(), volume, pitch));
  }

  @Override public boolean isOnline() {
    return this.isOnline;
  }

  @Override public boolean sendMessage(Message message) {
    return this.sendRawMessage(message);
  }

  @Override public boolean sendRawMessage(Message message) {
    if (!this.isOnline) {
      return false;
    }
    try {
      this.networkManager.sendPacket(new PacketOutChatMessage(message.toMessageJson(), true));
      return true;
    } catch (Exception ex) {
      try {
        this.networkManager.channelInactive(networkManager.ctx);
        EnderLogger.logger.throwing(null, null, ex);
      } catch (Exception ex1) {
        ex.addSuppressed(ex1);
        EnderLogger.logger.throwing(null, null, ex);
      }
    }
    return false;
  }

  @Override public String getName() {
    return this.getPlayerName();
  }
}