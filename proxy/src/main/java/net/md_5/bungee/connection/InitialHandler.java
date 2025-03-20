package net.md_5.bungee.connection;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.EncryptionUtil;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection.Unsafe;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.leymooo.botfilter.event.BotFilterCheckStartingEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.http.HttpClient;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.netty.cipher.CipherDecoder;
import net.md_5.bungee.netty.cipher.CipherEncoder;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.LegacyHandshake;
import net.md_5.bungee.protocol.packet.LegacyPing;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PingPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import net.md_5.bungee.util.BoundedArrayList;
import ru.leymooo.botfilter.BotFilter;
import ru.leymooo.botfilter.Config;
import ru.leymooo.botfilter.Connector;
import ru.leymooo.botfilter.BFConnector;
import ru.leymooo.botfilter.caching.PacketUtil;
import ru.leymooo.botfilter.utils.GeoIpUtils;
import ru.leymooo.botfilter.caching.PacketUtil.KickType;
import ru.leymooo.botfilter.config.Settings;
import ru.leymooo.botfilter.utils.ManyChecksUtils;
import ru.leymooo.botfilter.utils.ServerPingUtils;
import ru.leymooo.botfilter.utils.Utils;

@RequiredArgsConstructor public class InitialHandler extends PacketHandler implements PendingConnection {
  private final BungeeCord bungee;

  private ChannelWrapper ch;

  @Getter private final ListenerInfo listener;

  @Getter private Handshake handshake;

  @Getter private LoginRequest loginRequest;

  private EncryptionRequest request;

  @Getter private final List<PluginMessage> relayMessages = new BoundedArrayList<>(128);

  private State thisState = State.HANDSHAKE;

  private final Unsafe unsafe = new Unsafe() {
    @Override public void sendPacket(DefinedPacket packet) {
      ch.write(packet);
    }
  };

  @Getter private boolean onlineMode = BungeeCord.getInstance().config.isOnlineMode();

  @Getter private InetSocketAddress virtualHost;

  private String name;

  @Getter private UUID uniqueId;

  @Getter private UUID offlineId;

  @Getter private LoginResult loginProfile;

  @Getter private boolean legacy;

  @Getter private String extraDataInHandshake = "";

  @Override public boolean shouldHandle(PacketWrapper packet) throws Exception {
    return !ch.isClosing();
  }

  private enum State {
    HANDSHAKE,
    STATUS,
    PING,
    USERNAME,
    ENCRYPT,
    FINISHED
  }

  @Override public void connected(ChannelWrapper channel) throws Exception {
    this.ch = channel;
  }

  @Override public void exception(Throwable t) throws Exception {
    disconnect(ChatColor.RED + Util.exception(t));
  }

  @Override public void handle(PluginMessage pluginMessage) throws Exception {
    if (PluginMessage.SHOULD_RELAY.apply(pluginMessage)) {
      relayMessages.add(pluginMessage);
    }
  }

  @Override public void handle(LegacyHandshake legacyHandshake) throws Exception {
    this.legacy = true;
    ch.close(bungee.getTranslation("outdated_client"));
  }

  @Override public void handle(LegacyPing ping) throws Exception {
    this.legacy = true;
    final boolean v1_5 = ping.isV1_5();
    ServerPing legacy = new ServerPing(new ServerPing.Protocol(
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
    bungee.getCustomBungeeName()
=======
    "BotFilter " + bungee.getGameVersion() + " by vk.com Leymooo_s"
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
    , bungee.getProtocolVersion()), new ServerPing.Players(listener.getMaxPlayers(), bungee.
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
    getOnlineCountBF(true)
=======
    getOnlineCountAuto()
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
    , null), new TextComponent(TextComponent.fromLegacyText(listener.getMotd())), (Favicon) null);
    Callback<ProxyPingEvent> callback = new Callback<ProxyPingEvent>() {
      @Override public void done(ProxyPingEvent result, Throwable error) {
        if (ch.isClosed()) {
          return;
        }
        ServerPing legacy = result.getResponse();
        String kickMessage;
        if (v1_5) {
          kickMessage = ChatColor.DARK_BLUE + "\u0000" + 127 + '\u0000' + legacy.getVersion().getName() + '\u0000' + getFirstLine(legacy.getDescription()) + '\u0000' + legacy.getPlayers().getOnline() + '\u0000' + legacy.getPlayers().getMax();
        } else {
          kickMessage = ChatColor.stripColor(getFirstLine(legacy.getDescription())) + '\u00a7' + legacy.getPlayers().getOnline() + '\u00a7' + legacy.getPlayers().getMax();
        }
        ch.close(kickMessage);
      }
    };
    bungee.getPluginManager().callEvent(new ProxyPingEvent(this, legacy, callback));
  }

  private static String getFirstLine(String str) {
    int pos = str.indexOf('\n');
    return pos == -1 ? str : str.substring(0, pos);
  }

  @Override public void handle(StatusRequest statusRequest) throws Exception {
    Preconditions.checkState(thisState == State.STATUS, "Not expecting STATUS");
    ServerInfo forced = AbstractReconnectHandler.getForcedHost(this);
    final String motd = (forced != null) ? forced.getMotd() : listener.getMotd();
    Callback<ServerPing> pingBack = new Callback<ServerPing>() {
      @Override public void done(ServerPing result, Throwable error) {
        if (error != null) {
          result = new ServerPing();
          result.setDescription(bungee.getTranslation("ping_cannot_connect"));
          bungee.getLogger().log(Level.WARNING, "Error pinging remote server", error);
        }
        Callback<ProxyPingEvent> callback = new Callback<ProxyPingEvent>() {
          @Override public void done(ProxyPingEvent pingResult, Throwable error) {
            Gson gson = BungeeCord.getInstance().gson;
            unsafe.sendPacket(new StatusResponse(gson.toJson(pingResult.getResponse())));
          }
        };
        bungee.getPluginManager().callEvent(new ProxyPingEvent(InitialHandler.this, result, callback));
      }
    };
    if (forced != null && listener.isPingPassthrough()) {
      ((BungeeServerInfo) forced).ping(pingBack, handshake.getProtocolVersion());
    } else {
      int protocol = (ProtocolConstants.SUPPORTED_VERSION_IDS.contains(handshake.getProtocolVersion())) ? handshake.getProtocolVersion() : bungee.getProtocolVersion();
      pingBack.done(new ServerPing(new ServerPing.Protocol(
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
      bungee.getCustomBungeeName()
=======
      "BotFilter " + bungee.getGameVersion() + " by vk.com Leymooo_s"
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
      , protocol), new ServerPing.Players(listener.getMaxPlayers(), bungee.
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
      getOnlineCountBF(true)
=======
      getOnlineCountAuto()
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
      , null), motd, BungeeCord.getInstance().config.getFaviconObject()), null);
    }
    thisState = State.PING;
  }

  @Override public void handle(PingPacket ping) throws Exception {
    Preconditions.checkState(thisState == State.PING, "Not expecting PING");
    unsafe.sendPacket(ping);
    disconnect("");
  }

  @Override public void handle(Handshake handshake) throws Exception {
    Preconditions.checkState(thisState == State.HANDSHAKE, "Not expecting HANDSHAKE");
    this.handshake = handshake;
    ch.setVersion(handshake.getProtocolVersion());
    if (handshake.getHost().contains("\u0000")) {
      String[] split = handshake.getHost().split("\u0000", 2);
      handshake.setHost(split[0]);
      extraDataInHandshake = "\u0000" + split[1];
    }
    if (handshake.getHost().endsWith(".")) {
      handshake.setHost(handshake.getHost().substring(0, handshake.getHost().length() - 1));
    }
    this.virtualHost = InetSocketAddress.createUnresolved(handshake.getHost(), handshake.getPort());
    bungee.getPluginManager().callEvent(new PlayerHandshakeEvent(InitialHandler.this, handshake));
    switch (handshake.getRequestedProtocol()) {
      case 1:
      thisState = State.STATUS;
      ch.setProtocol(Protocol.STATUS);

<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
      BotFilter
=======
      ServerPingUtils
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
      .
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
      getInstance().getServerPingUtils().add(getAddress().getAddress())
=======
      getInstance().add(getAddress().getAddress())
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
      ;
      break;
      case 2:
      thisState = State.USERNAME;
      ch.setProtocol(Protocol.LOGIN);
      if (!ProtocolConstants.SUPPORTED_VERSION_IDS.contains(handshake.getProtocolVersion())) {
        if (handshake.getProtocolVersion() > bungee.getProtocolVersion()) {
          disconnect(bungee.getTranslation("outdated_server"));
        } else {
          disconnect(bungee.getTranslation("outdated_client"));
        }
        return;
      }
      if (
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
      ManyChecksUtils
=======
      Utils
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
      .isManyChecks(
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
      getAddress().getAddress()
=======
      getAddress().getAddress().getHostAddress()
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
      , false, false)) {
        PacketUtil.kickPlayer(KickType.MANYCHECKS, Protocol.LOGIN, ch, getVersion());

<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
        bungee.getLogger().log(Level.INFO, "[{0}] disconnected: Too many checks in 10 min", getAddress().getAddress().getHostAddress());
=======
        disconnect(Config.getConfig().getErrorManyChecks());
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java

        return;
      }
      ServerPingUtils ping = BotFilter.getInstance().getServerPingUtils();
      if (ping.needCheck() && ping.needKickOrRemove(getAddress().getAddress())) {
        disconnect(Settings.IMP.SERVER_PING_CHECK.KICK_MESSAGE);
        bungee.getLogger().log(Level.INFO, "[{0}] disconnected: The player did not ping the server", getAddress().getAddress().getHostAddress());
        return;
      }
      if (bungee.getConnectionThrottle() != null && bungee.getConnectionThrottle().throttle(getAddress().getAddress())) {
        PacketUtil.kickPlayer(KickType.THROTTLE, Protocol.LOGIN, ch, getVersion());
        bungee.getLogger().log(Level.INFO, "[{0}] disconnected: Connection is throttled", getAddress().getAddress().getHostAddress());
      }
      break;
      default:
      throw new IllegalArgumentException("Cannot request protocol " + handshake.getRequestedProtocol());
    }
  }

  @Override public void handle(LoginRequest loginRequest) throws Exception {
    Preconditions.checkState(thisState == State.USERNAME, "Not expecting USERNAME");
    this.loginRequest = loginRequest;
    if (getName().contains(".")) {
      disconnect(bungee.getTranslation("name_invalid"));
      return;
    }
    if (getName().length() > 16) {
      disconnect(bungee.getTranslation("name_too_long"));
      return;
    }
    int limit = BungeeCord.getInstance().config.getPlayerLimit();
    if (limit > 0 && bungee.
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
    getOnlineCountBF(false)
=======
    getOnlineCountWithGG()
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
     > limit) {
      disconnect(bungee.getTranslation("proxy_full"));
      return;
    }
    Config config = Config.getConfig();
    InetAddress address = getAddress().getAddress();
    boolean needCheck = config.needCheck(getName(), address.getHostAddress());

<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
    KickType
=======
    ServerPingUtils
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
     
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
    kickType = BotFilter.getInstance().checkIpAddress(getAddress().getAddress())
=======
    pingUtils = ServerPingUtils.getInstance()
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
    ;

<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
    if (kickType != null) {
      PacketUtil.kickPlayer(KickType.MANYCHECKS, Protocol.LOGIN, ch, getVersion());
      bungee.getLogger().log(Level.INFO, "[{0}] disconnected: ".concat((kickType == KickType.COUNTRY ? "Country is not allowed" : "Proxy detected")), getAddress().getAddress().getHostAddress());
    }
=======
    if (needCheck && pingUtils.needKickAndRemove(address)) {
      disconnect(pingUtils.getMessage());
      return;
    }
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java

    GeoIpUtils geo = config.getGeoUtils();
    boolean proxy = config.getProxy().isProxy(address.getHostAddress());
    if (config.isProtectionEnabled() && config.isForceKick() && needCheck) {
      if (proxy || !geo.isAllowed(geo.getCountryCode(address), false)) {
        disconnect(proxy ? config.getErrorProxy() : config.getErrorConutry());
        return;
      }
    }
    if (!isOnlineMode() && bungee.getPlayer(getUniqueId()) != null) {
      disconnect(bungee.getTranslation("already_connected_proxy"));
      return;
    }
    Callback<PreLoginEvent> callback = new Callback<PreLoginEvent>() {
      @Override public void done(PreLoginEvent result, Throwable error) {
        if (result.isCancelled()) {
          disconnect(result.getCancelReasonComponents());
          return;
        }
        if (ch.isClosed()) {
          return;
        }
        if (onlineMode) {
          unsafe().sendPacket(request = EncryptionUtil.encryptRequest());
        } else {
          finish();
        }
        thisState = State.ENCRYPT;
      }
    };
    bungee.getPluginManager().callEvent(new PreLoginEvent(InitialHandler.this, callback));
  }

  @Override public void handle(final EncryptionResponse encryptResponse) throws Exception {
    Preconditions.checkState(thisState == State.ENCRYPT, "Not expecting ENCRYPT");
    SecretKey sharedKey = EncryptionUtil.getSecret(encryptResponse, request);
    BungeeCipher decrypt = EncryptionUtil.getCipher(false, sharedKey);
    ch.addBefore(PipelineUtils.FRAME_DECODER, PipelineUtils.DECRYPT_HANDLER, new CipherDecoder(decrypt));
    BungeeCipher encrypt = EncryptionUtil.getCipher(true, sharedKey);
    ch.addBefore(PipelineUtils.FRAME_PREPENDER, PipelineUtils.ENCRYPT_HANDLER, new CipherEncoder(encrypt));
    String encName = URLEncoder.encode(InitialHandler.this.getName(), "UTF-8");
    MessageDigest sha = MessageDigest.getInstance("SHA-1");
    for (byte[] bit : new byte[][] { request.getServerId().getBytes("ISO_8859_1"), sharedKey.getEncoded(), EncryptionUtil.keys.getPublic().getEncoded() }) {
      sha.update(bit);
    }
    String encodedHash = URLEncoder.encode(new BigInteger(sha.digest()).toString(16), "UTF-8");
    String preventProxy = ((BungeeCord.getInstance().config.isPreventProxyConnections()) ? "&ip=" + URLEncoder.encode(getAddress().getAddress().getHostAddress(), "UTF-8") : "");
    String authURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + encName + "&serverId=" + encodedHash + preventProxy;
    Callback<String> handler = new Callback<String>() {
      @Override public void done(String result, Throwable error) {
        if (error == null) {
          LoginResult obj = BungeeCord.getInstance().gson.fromJson(result, LoginResult.class);
          if (obj != null && obj.getId() != null) {
            loginProfile = obj;
            name = obj.getName();
            uniqueId = Util.getUUID(obj.getId());
            finish();
            return;
          }
          disconnect(bungee.getTranslation("offline_mode_player"));
        } else {
          disconnect(bungee.getTranslation("mojang_fail"));
          bungee.getLogger().log(Level.SEVERE, "Error authenticating " + getName() + " with minecraft.net", error);
        }
      }
    };
    HttpClient.get(authURL, ch.getHandle().eventLoop(), handler);
  }

  private void finish() {
    if (isOnlineMode()) {
      ProxiedPlayer oldName = bungee.getPlayer(getName());
      if (oldName != null) {
        oldName.disconnect(bungee.getTranslation("already_connected_proxy"));
      }
      ProxiedPlayer oldID = bungee.getPlayer(getUniqueId());
      if (oldID != null) {
        oldID.disconnect(bungee.getTranslation("already_connected_proxy"));
      }
    } else {
      ProxiedPlayer oldName = bungee.getPlayer(getName());
      if (oldName != null) {
        disconnect(bungee.getTranslation("already_connected_proxy"));
        return;
      }
    }
    if (BotFilter.getInstance().isOnChecking(getName())) {
      disconnect(bungee.getTranslation("already_connected_proxy"));
      return;
    }
    offlineId = java.util.UUID.nameUUIDFromBytes(("OfflinePlayer:" + getName()).getBytes(Charsets.UTF_8));
    if (uniqueId == null) {
      uniqueId = offlineId;
    }
    UserConnection userCon = new UserConnection(bungee, ch, getName(), InitialHandler.this);
    userCon.setCompressionThreshold(BungeeCord.getInstance().config.getCompressionThreshold());
    userCon.init();
    bungee.getLogger().log(Level.INFO, "{0} has connected", InitialHandler.this);
    unsafe.sendPacket(new LoginSuccess(getUniqueId().toString(), getName()));
    ch.setProtocol(Protocol.GAME);
    if (
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
    BotFilter
=======
    Config
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
    .
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
    getInstance()
=======
    getConfig()
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
    .needCheck(getName(), 
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
    getAddress().getAddress()
=======
    getAddress().getAddress().getHostAddress()
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
    )) {

<<<<<<< /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/left.java
      ch.getHandle().pipeline().get(HandlerBoss.class).setHandler(new Connector(userCon));
=======
      callCheckEvent(userCon);
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/78efbaebfd96d0a09b441c0b0efdfcbfd282aa1e/proxy/src/main/java/net/md_5/bungee/connection/InitialHandler.java/right.java
    } else {
      finishLogin(userCon);
    }
  }

  public void finishLogin(UserConnection userCon) {
    Callback<LoginEvent> complete = new Callback<LoginEvent>() {
      @Override public void done(LoginEvent result, Throwable error) {
        if (result.isCancelled()) {
          disconnect(result.getCancelReasonComponents());
          return;
        }
        if (ch.isClosed()) {
          return;
        }
        if (ch.getHandle().eventLoop().inEventLoop()) {
          finnalyFinishLogin(userCon);
        } else {
          ch.getHandle().eventLoop().execute(() -> {
            if (!ch.isClosing()) {
              finnalyFinishLogin(userCon);
            }
          });
        }
      }
    };
    bungee.getPluginManager().callEvent(new LoginEvent(InitialHandler.this, complete));
  }

  private void finnalyFinishLogin(UserConnection userCon) {
    ch.getHandle().pipeline().get(HandlerBoss.class).setHandler(new UpstreamBridge(bungee, userCon));
    bungee.getPluginManager().callEvent(new PostLoginEvent(userCon));
    ServerInfo server;
    if (bungee.getReconnectHandler() != null) {
      server = bungee.getReconnectHandler().getServer(userCon);
    } else {
      server = AbstractReconnectHandler.getForcedHost(InitialHandler.this);
    }
    if (server == null) {
      server = bungee.getServerInfo(listener.getDefaultServer());
    }
    userCon.connect(server, null, true);
    thisState = State.FINISHED;
  }

  private void callCheckEvent(UserConnection userCon) {
    Callback<BotFilterCheckStartingEvent> complete = new Callback<BotFilterCheckStartingEvent>() {
      @Override public void done(BotFilterCheckStartingEvent result, Throwable error) {
        if (result.isCancelled()) {
          disconnect(result.getCancelReasonComponents());
          return;
        }
        if (ch.isClosed()) {
          return;
        }
        if (ch.getHandle().eventLoop().inEventLoop()) {
          ch.getHandle().pipeline().get(HandlerBoss.class).setHandler(new BFConnector(userCon));
        } else {
          ch.getHandle().eventLoop().execute(() -> {
            if (!ch.isClosing()) {
              ch.getHandle().pipeline().get(HandlerBoss.class).setHandler(new BFConnector(userCon));
            }
          });
        }
      }
    };
    bungee.getPluginManager().callEvent(new BotFilterCheckStartingEvent(userCon, complete));
  }

  @Override public void disconnect(String reason) {
    disconnect(TextComponent.fromLegacyText(reason));
  }

  @Override public void disconnect(final BaseComponent... reason) {
    if (thisState != State.STATUS && thisState != State.PING) {
      ch.delayedClose(new Kick(ComponentSerializer.toString(reason)));
    } else {
      ch.close();
    }
  }

  @Override public void disconnect(BaseComponent reason) {
    disconnect(new BaseComponent[] { reason });
  }

  @Override public String getName() {
    return (name != null) ? name : (loginRequest == null) ? null : loginRequest.getData();
  }

  @Override public int getVersion() {
    return (handshake == null) ? -1 : handshake.getProtocolVersion();
  }

  @Override public InetSocketAddress getAddress() {
    return ch.getRemoteAddress();
  }

  @Override public Unsafe unsafe() {
    return unsafe;
  }

  @Override public void setOnlineMode(boolean onlineMode) {
    Preconditions.checkState(thisState == State.USERNAME, "Can only set online mode status whilst state is username");
    this.onlineMode = onlineMode;
  }

  @Override public void setUniqueId(UUID uuid) {
    Preconditions.checkState(thisState == State.USERNAME, "Can only set uuid while state is username");
    Preconditions.checkState(!onlineMode, "Can only set uuid when online mode is false");
    this.uniqueId = uuid;
  }

  @Override public String getUUID() {
    return uniqueId.toString().replaceAll("-", "");
  }

  @Override public String toString() {
    return "[" + ((getName() != null) ? getName() : getAddress()) + "] <-> InitialHandler";
  }

  @Override public boolean isConnected() {
    return !ch.isClosed();
  }
}