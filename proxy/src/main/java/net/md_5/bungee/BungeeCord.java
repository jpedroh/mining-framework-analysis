package net.md_5.bungee;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.ResourceLeakDetector;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.KeybindComponentSerializer;
import net.md_5.bungee.chat.ScoreComponentSerializer;
import net.md_5.bungee.chat.SelectorComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import net.md_5.bungee.chat.TranslatableComponentSerializer;
import net.md_5.bungee.command.CommandBungee;
import net.md_5.bungee.command.CommandEnd;
import net.md_5.bungee.command.CommandIP;
import net.md_5.bungee.command.CommandPerms;
import net.md_5.bungee.command.CommandReload;
import net.md_5.bungee.command.CommandReloadServers;
import net.md_5.bungee.command.ConsoleCommandCompleter;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.compress.CompressFactory;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.forge.ForgeConstants;
import net.md_5.bungee.log.BungeeLogger;
import net.md_5.bungee.log.LoggingOutputStream;
import net.md_5.bungee.module.ModuleManager;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.query.RemoteQuery;
import net.md_5.bungee.scheduler.BungeeScheduler;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.fusesource.jansi.AnsiConsole;
import ru.leymooo.botfilter.BotFilter;
import ru.leymooo.botfilter.BotFilterCommand;
import ru.leymooo.botfilter.BotFilterThread;
import ru.leymooo.botfilter.config.Settings;
import ru.leymooo.botfilter.utils.FakeOnlineUtils;
import org.slf4j.impl.JDK14LoggerFactory;

public class BungeeCord extends ProxyServer {
  public volatile boolean isRunning;

  @Getter public final Configuration config = new Configuration();

  private ResourceBundle baseBundle;

  private ResourceBundle customBundle;

  public EventLoopGroup eventLoops;

  public EventLoopGroup bossEventLoopGroup, workerEventLoopGroup, queryEventLoopGroup;

  private final Timer saveThread = new Timer("Reconnect Saver");

  private final Collection<Channel> listeners = new HashSet<>();

  private final Map<String, UserConnection> connections = new CaseInsensitiveMap<>();

  private final Map<UUID, UserConnection> connectionsByOfflineUUID = new HashMap<>();

  private final Map<UUID, UserConnection> connectionsByUUID = new HashMap<>();

  private final ReadWriteLock connectionLock = new ReentrantReadWriteLock();

  private final ReentrantLock shutdownLock = new ReentrantLock();

  @Getter public final PluginManager pluginManager;

  @Getter @Setter private ReconnectHandler reconnectHandler;

  @Getter @Setter private ConfigurationAdapter configurationAdapter = new YamlConfig();

  private final Collection<String> pluginChannels = new HashSet<>();

  @Getter private final File pluginsFolder = new File("plugins");

  @Getter private final BungeeScheduler scheduler = new BungeeScheduler();

  @Getter private final ConsoleReader consoleReader;

  @Getter private final Logger logger;

  public final Gson gson = new GsonBuilder().registerTypeAdapter(BaseComponent.class, new ComponentSerializer()).registerTypeAdapter(TextComponent.class, new TextComponentSerializer()).registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer()).registerTypeAdapter(KeybindComponent.class, new KeybindComponentSerializer()).registerTypeAdapter(ScoreComponent.class, new ScoreComponentSerializer()).registerTypeAdapter(SelectorComponent.class, new SelectorComponentSerializer()).registerTypeAdapter(ServerPing.PlayerInfo.class, new PlayerInfoSerializer()).registerTypeAdapter(Favicon.class, Favicon.getFaviconTypeAdapter()).create();

  @Getter private ConnectionThrottle connectionThrottle;

  private final ModuleManager moduleManager = new ModuleManager();

  @Getter private String customBungeeName;

  @Getter @Setter private BotFilter botFilter;

  {
    registerChannel("BungeeCord");
  }

  public static BungeeCord getInstance() {
    return (BungeeCord) ProxyServer.getInstance();
  }

  @SuppressFBWarnings(value = "DM_DEFAULT_ENCODING") public BungeeCord() throws IOException {
    Preconditions.checkState(new File(".").getAbsolutePath().indexOf('!') == -1, "Cannot use BungeeCord in directory with ! in path.");
    System.setSecurityManager(new BungeeSecurityManager());
    try {
      baseBundle = ResourceBundle.getBundle("messages");
    } catch (MissingResourceException ex) {
      baseBundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
    }
    reloadMessages();
    System.setProperty("library.jansi.version", "BungeeCord");
    AnsiConsole.systemInstall();
    consoleReader = new ConsoleReader();
    consoleReader.setExpandEvents(false);
    consoleReader.addCompleter(new ConsoleCommandCompleter(this));
    logger = new BungeeLogger("BungeeCord", "proxy.log", consoleReader);
    JDK14LoggerFactory.LOGGER = logger;
    System.setErr(new PrintStream(new LoggingOutputStream(logger, Level.SEVERE), true));
    System.setOut(new PrintStream(new LoggingOutputStream(logger, Level.INFO), true));
    pluginManager = new PluginManager(this);
    getPluginManager().registerCommand(null, new CommandReload());
    getPluginManager().registerCommand(null, new CommandEnd());
    getPluginManager().registerCommand(null, new CommandIP());
    getPluginManager().registerCommand(null, new CommandBungee());
    getPluginManager().registerCommand(null, new CommandPerms());
    getPluginManager().registerCommand(null, new BotFilterCommand());
    getPluginManager().registerCommand(null, new CommandReloadServers());
    if (!Boolean.getBoolean("net.md_5.bungee.native.disable")) {
      if (EncryptionUtil.nativeFactory.load()) {
        logger.info("Using mbed TLS based native cipher.");
      } else {
        logger.info("Using standard Java JCE cipher.");
      }
      if (CompressFactory.zlib.load()) {
        logger.info("Using zlib based native compressor.");
      } else {
        logger.info("Using standard Java compressor.");
      }
    }
  }

  @SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE") public void start() throws Exception {
    System.setProperty("io.netty.selectorAutoRebuildThreshold", "0");
    if (System.getProperty("io.netty.leakDetectionLevel") == null) {
      ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
    }
    String nameProperty = System.getProperty("bungeeName");
    customBungeeName = (nameProperty == null ? getName() : nameProperty) + " " + getGameVersion();
    this.botFilter = new BotFilter(true);
    new FakeOnlineUtils();
    BotFilterThread.startCleanUpThread();
    bossEventLoopGroup = PipelineUtils.newEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Boss IO Thread #%1$d").build());
    eventLoops = workerEventLoopGroup = PipelineUtils.newEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Worker IO Thread #%1$d").build());
    queryEventLoopGroup = PipelineUtils.newEventLoopGroup(1, new ThreadFactoryBuilder().setNameFormat("Query Netty IO Thread #%1$d").build());
    File moduleDirectory = new File("modules");
    moduleManager.load(this, moduleDirectory);
    pluginManager.detectPlugins(moduleDirectory);
    pluginsFolder.mkdir();
    pluginManager.detectPlugins(pluginsFolder);
    pluginManager.loadPlugins();
    config.load();
    if (config.isForgeSupport()) {
      registerChannel(ForgeConstants.FML_TAG);
      registerChannel(ForgeConstants.FML_HANDSHAKE_TAG);
      registerChannel(ForgeConstants.FORGE_REGISTER);
      getLogger().warning("MinecraftForge support is currently unmaintained and may have unresolved issues. Please use at your own risk.");
    }
    isRunning = true;
    pluginManager.enablePlugins();
    if (config.getThrottle() > 0) {
      connectionThrottle = new ConnectionThrottle(config.getThrottle(), config.getThrottleLimit());
    }
    startListeners();
    saveThread.scheduleAtFixedRate(new TimerTask() {
      @Override public void run() {
        if (getReconnectHandler() != null) {
          getReconnectHandler().save();
        }
      }
    }, 0, TimeUnit.MINUTES.toMillis(5));
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override public void run() {
        independentThreadStop(getTranslation("restart"), false);
      }
    });
  }

  public void startListeners() {
    for (final ListenerInfo info : config.getListeners()) {
      if (info.isProxyProtocol()) {
        getLogger().log(Level.WARNING, "Using PROXY protocol for listener {0}, please ensure this listener is adequately firewalled.", info.getSocketAddress());
        if (connectionThrottle != null) {
          connectionThrottle = null;
          getLogger().log(Level.WARNING, "Since PROXY protocol is in use, internal connection throttle has been disabled.");
        }
      }
      ChannelFutureListener listener = new ChannelFutureListener() {
        @Override public void operationComplete(ChannelFuture future) throws Exception {
          if (future.isSuccess()) {
            listeners.add(future.channel());
            getLogger().log(Level.INFO, "Listening on {0}", info.getSocketAddress());
          } else {
            getLogger().log(Level.WARNING, "Could not bind to host " + info.getSocketAddress(), future.cause());
          }
        }
      };
      new ServerBootstrap().channel(PipelineUtils.getServerChannel(info.getSocketAddress())).option(ChannelOption.SO_REUSEADDR, true).childAttr(PipelineUtils.LISTENER, info).childHandler(PipelineUtils.SERVER_CHILD).group(bossEventLoopGroup, workerEventLoopGroup).localAddress(info.getSocketAddress()).bind().addListener(listener);
      if (info.isQueryEnabled()) {
        Preconditions.checkArgument(info.getSocketAddress() instanceof InetSocketAddress, "Can only create query listener on UDP address");
        ChannelFutureListener bindListener = new ChannelFutureListener() {
          @Override public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
              listeners.add(future.channel());
              getLogger().log(Level.INFO, "Started query on {0}", future.channel().localAddress());
            } else {
              getLogger().log(Level.WARNING, "Could not bind to host " + info.getSocketAddress(), future.cause());
            }
          }
        };
        new RemoteQuery(this, info).start(PipelineUtils.getDatagramChannel(), new InetSocketAddress(info.getHost().getAddress(), info.getQueryPort()), queryEventLoopGroup, bindListener);
      }
    }
  }

  public void stopListeners() {
    for (Channel listener : listeners) {
      getLogger().log(Level.INFO, "Closing listener {0}", listener);
      try {
        listener.close().syncUninterruptibly();
      } catch (ChannelException ex) {
        getLogger().severe("Could not close listen thread");
      }
    }
    listeners.clear();
  }

  @Override public void stop() {
    stop(getTranslation("restart"));
  }

  @Override public void stop(final String reason) {
    new Thread("Shutdown Thread") {
      @Override public void run() {
        independentThreadStop(reason, true);
      }
    }.start();
  }

  @SuppressFBWarnings(value = "DM_EXIT") @SuppressWarnings(value = { "TooBroadCatch" }) private void independentThreadStop(final String reason, boolean callSystemExit) {
    shutdownLock.lock();
    if (!isRunning) {
      shutdownLock.unlock();
      return;
    }
    isRunning = false;
    stopListeners();
    getLogger().info("Closing pending connections");
    connectionLock.readLock().lock();
    try {
      getLogger().log(Level.INFO, "Disconnecting {0} connections", connections.size());
      for (UserConnection user : connections.values()) {
        user.disconnect(reason);
      }
    }  finally {
      connectionLock.readLock().unlock();
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException ex) {
    }
    if (reconnectHandler != null) {
      getLogger().info("Saving reconnect locations");
      reconnectHandler.save();
      reconnectHandler.close();
    }
    saveThread.cancel();
    getLogger().info("Disabling plugins");
    for (Plugin plugin : Lists.reverse(new ArrayList<>(pluginManager.getPlugins()))) {
      try {
        plugin.onDisable();
        for (Handler handler : plugin.getLogger().getHandlers()) {
          handler.close();
        }
      } catch (Throwable t) {
        getLogger().log(Level.SEVERE, "Exception disabling plugin " + plugin.getDescription().getName(), t);
      }
      getScheduler().cancel(plugin);
      plugin.getExecutorService().shutdownNow();
    }
    getLogger().info("Closing IO threads");
    bossEventLoopGroup.shutdownGracefully();
    workerEventLoopGroup.shutdownGracefully();
    queryEventLoopGroup.shutdownGracefully();
    while (true) {
      try {
        bossEventLoopGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        workerEventLoopGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        queryEventLoopGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        break;
      } catch (InterruptedException ignored) {
      }
    }
    getLogger().info("Thank you and goodbye");
    for (Handler handler : getLogger().getHandlers()) {
      handler.close();
    }
    shutdownLock.unlock();
    if (callSystemExit) {
      System.exit(0);
    }
  }

  public void broadcast(DefinedPacket packet) {
    connectionLock.readLock().lock();
    try {
      for (UserConnection con : connections.values()) {
        con.unsafe().sendPacket(packet);
      }
    }  finally {
      connectionLock.readLock().unlock();
    }
  }

  @Override public String getName() {
    return "BotFilter";
  }

  @Override public String getVersion() {
    return (BungeeCord.class.getPackage().getImplementationVersion() == null) ? "unknown" : BungeeCord.class.getPackage().getImplementationVersion();
  }

  public void reloadMessages() {
    File file = new File("messages.properties");
    if (file.isFile()) {
      try (FileReader rd = new FileReader(file)) {
        customBundle = new PropertyResourceBundle(rd);
      } catch (IOException ex) {
        getLogger().log(Level.SEVERE, "Could not load custom messages.properties", ex);
      }
    }
  }

  @Override public String getTranslation(String name, Object... args) {
    String translation = "<translation \'" + name + "\' missing>";
    try {
      translation = MessageFormat.format(customBundle != null && customBundle.containsKey(name) ? customBundle.getString(name) : baseBundle.getString(name), args);
    } catch (MissingResourceException ex) {
    }
    return translation;
  }

  @Override @SuppressWarnings(value = { "unchecked" }) public Collection<ProxiedPlayer> getPlayers() {
    connectionLock.readLock().lock();
    try {
      return Collections.unmodifiableCollection(new HashSet(connections.values()));
    }  finally {
      connectionLock.readLock().unlock();
    }
  }

  @Override public int getOnlineCount() {
    return connections.size();
  }

  @Override public int getOnlineCountBF(boolean fake) {
    int online = connections.size();
    if (fake) {
      online = FakeOnlineUtils.getInstance().getFakeOnline(online);
    }
    if (Settings.IMP.SHOW_ONLINE) {
      online += botFilter.getOnlineOnFilter();
    }
    return online;
  }

  @Override public ProxiedPlayer getPlayer(String name) {
    connectionLock.readLock().lock();
    try {
      return connections.get(name);
    }  finally {
      connectionLock.readLock().unlock();
    }
  }

  public UserConnection getPlayerByOfflineUUID(UUID name) {
    connectionLock.readLock().lock();
    try {
      return connectionsByOfflineUUID.get(name);
    }  finally {
      connectionLock.readLock().unlock();
    }
  }

  @Override public ProxiedPlayer getPlayer(UUID uuid) {
    connectionLock.readLock().lock();
    try {
      return connectionsByUUID.get(uuid);
    }  finally {
      connectionLock.readLock().unlock();
    }
  }

  @Override public Map<String, ServerInfo> getServers() {
    return config.getServers();
  }

  @Override public ServerInfo getServerInfo(String name) {
    return getServers().get(name);
  }

  @Override @Synchronized(value = "pluginChannels") public void registerChannel(String channel) {
    pluginChannels.add(channel);
  }

  @Override @Synchronized(value = "pluginChannels") public void unregisterChannel(String channel) {
    pluginChannels.remove(channel);
  }

  @Override @Synchronized(value = "pluginChannels") public Collection<String> getChannels() {
    return Collections.unmodifiableCollection(pluginChannels);
  }

  public PluginMessage registerChannels(int protocolVersion) {
    if (protocolVersion >= ProtocolConstants.MINECRAFT_1_13) {
      return new PluginMessage("minecraft:register", Util.format(Iterables.transform(pluginChannels, PluginMessage.MODERNISE), "\u0000").getBytes(Charsets.UTF_8), false);
    }
    return new PluginMessage("REGISTER", Util.format(pluginChannels, "\u0000").getBytes(Charsets.UTF_8), false);
  }

  @Override public int getProtocolVersion() {
    return ProtocolConstants.SUPPORTED_VERSION_IDS.get(ProtocolConstants.SUPPORTED_VERSION_IDS.size() - 1);
  }

  @Override public String getGameVersion() {
    return ProtocolConstants.SUPPORTED_VERSIONS.get(0) + "-" + ProtocolConstants.SUPPORTED_VERSIONS.get(ProtocolConstants.SUPPORTED_VERSIONS.size() - 1);
  }

  @Override public ServerInfo constructServerInfo(String name, InetSocketAddress address, String motd, boolean restricted) {
    return constructServerInfo(name, (SocketAddress) address, motd, restricted);
  }

  @Override public ServerInfo constructServerInfo(String name, SocketAddress address, String motd, boolean restricted) {
    return new BungeeServerInfo(name, address, motd, restricted);
  }

  @Override public CommandSender getConsole() {
    return ConsoleCommandSender.getInstance();
  }

  @Override public void broadcast(String message) {
    broadcast(TextComponent.fromLegacyText(message));
  }

  @Override public void broadcast(BaseComponent... message) {
    getConsole().sendMessage(BaseComponent.toLegacyText(message));
    for (ProxiedPlayer player : getPlayers()) {
      player.sendMessage(message);
    }
  }

  @Override public void broadcast(BaseComponent message) {
    getConsole().sendMessage(message.toLegacyText());
    for (ProxiedPlayer player : getPlayers()) {
      player.sendMessage(message);
    }
  }

  public void addConnection(UserConnection con) {
    connectionLock.writeLock().lock();
    try {
      connections.put(con.getName(), con);
      connectionsByUUID.put(con.getUniqueId(), con);
      connectionsByOfflineUUID.put(con.getPendingConnection().getOfflineId(), con);
    }  finally {
      connectionLock.writeLock().unlock();
    }
  }

  public void removeConnection(UserConnection con) {
    connectionLock.writeLock().lock();
    try {
      if (connections.get(con.getName()) == con) {
        connections.remove(con.getName());
        connectionsByUUID.remove(con.getUniqueId());
        connectionsByOfflineUUID.remove(con.getPendingConnection().getOfflineId());
      }
    }  finally {
      connectionLock.writeLock().unlock();
    }
  }

  @Override public Collection<String> getDisabledCommands() {
    return config.getDisabledCommands();
  }

  @Override public Collection<ProxiedPlayer> matchPlayer(final String partialName) {
    Preconditions.checkNotNull(partialName, "partialName");
    ProxiedPlayer exactMatch = getPlayer(partialName);
    if (exactMatch != null) {
      return Collections.singleton(exactMatch);
    }
    return Sets.newHashSet(Iterables.filter(getPlayers(), new Predicate<ProxiedPlayer>() {
      @Override public boolean apply(ProxiedPlayer input) {
        return (input == null) ? false : input.getName().toLowerCase(Locale.ROOT).startsWith(partialName.toLowerCase(Locale.ROOT));
      }
    }));
  }

  @Override public Title createTitle() {
    return new BungeeTitle();
  }
}