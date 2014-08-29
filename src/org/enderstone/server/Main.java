package org.enderstone.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.enderstone.PingCommand;
import org.enderstone.server.commands.vanila.TeleportCommand;
import org.enderstone.server.commands.enderstone.VersionCommand;
import org.enderstone.server.commands.vanila.StopCommand;
import org.enderstone.server.commands.vanila.TellCommand;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.play.PacketKeepAlive;
import org.enderstone.server.packet.play.PacketOutUpdateHealth;
import org.enderstone.server.regions.EnderWorld;
import org.enderstone.server.uuid.UUIDFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

public class Main implements Runnable {

	public static final String NAME = "Enderstone";
	public static final String VERSION = "1.0.0";
	public static final String PROTOCOL_VERSION = "1.7.6";
	private static final int EXCEPTED_SLEEP_TIME = 1000 / 20;
	private static final int CANT_KEEP_UP_TIMEOUT = -10000;
	private static final int MAX_SLEEP = 100;
	public static final Set<Integer> PROTOCOL = Collections.unmodifiableSet(new HashSet<Integer>() {
		private static final long serialVersionUID = 1L;

		{
			this.add(5); // 1.7.9
		}
	});
	public static final String[] AUTHORS = new String[] { "bigteddy98", "ferrybig", "timbayens" };
	public static final Random random = new Random();
	public volatile Thread mainThread;
	public final List<Thread> listenThreads = new CopyOnWriteArrayList<>();

	public Properties prop = null;
	public UUIDFactory uuidFactory = new UUIDFactory();
	public String FAVICON = null;
	public int port;
	public final EnderWorld mainWorld = new EnderWorld();
	public volatile boolean isRunning = true;
	public final CommandMap commands;

	{
		commands = new CommandMap();
		commands.registerCommand(new TellCommand());
		commands.registerCommand(new PingCommand());
		commands.registerCommand(new VersionCommand());
		commands.registerCommand(new TeleportCommand());
		commands.registerCommand(new StopCommand());
	}

	private static Main instance;

	public final List<EnderPlayer> onlinePlayers = new ArrayList<>();
	private final List<Runnable> sendToMainThread = Collections.synchronizedList(new ArrayList<Runnable>());

	public static Main getInstance() {
		return instance;
	}

	public void sendToMainThread(Runnable run) {
		synchronized (sendToMainThread) {
			sendToMainThread.add(run);
		}
	}

	public static void main(String[] args) {
		new Main().run();
	}

	@Override
	public void run() {
		Main.instance = this;
		EnderLogger.info("Starting " + NAME + " " + VERSION + " server version " + PROTOCOL_VERSION + ".");
		EnderLogger.info("Authors: " + Arrays.asList(AUTHORS).toString());
		EnderLogger.info("Loading config.ender file...");
		this.loadConfigFromDisk();
		EnderLogger.info("Loaded config.ender file!");

		EnderLogger.info("Loading favicon...");
		try {
			if (readFavicon())
				EnderLogger.info("Loaded server-icon.png!");
		} catch (FileNotFoundException e) {
			EnderLogger.info("server-icon.png not found!");
		} catch (IOException e) {
			EnderLogger.warn("Error while reading server-icon.png!");
			EnderLogger.exception(e);
		}

		ThreadGroup nettyListeners = new ThreadGroup(Thread.currentThread().getThreadGroup(), "Netty Listeners");
		EnderLogger.info("Starting Netty listeners... [" + this.port + "]");
		for (final int nettyPort : new int[] { this.port }) {

			Thread t;
			(t = new Thread(nettyListeners, new Runnable() {

				@Override
				public void run() {
					EnderLogger.info("[Netty] Started Netty Server at port " + nettyPort + "...");
					EventLoopGroup bossGroup = new NioEventLoopGroup();
					EventLoopGroup workerGroup = new NioEventLoopGroup();

					try {
						ServerBootstrap bootstrap = new ServerBootstrap();
						bootstrap.group(bossGroup, workerGroup);
						bootstrap.channel(NioServerSocketChannel.class);
						bootstrap.childHandler(new MinecraftServerInitializer());

						bootstrap.bind(nettyPort).sync().channel().closeFuture().sync();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} finally {
						scheduleShutdown();
						bossGroup.shutdownGracefully();
						workerGroup.shutdownGracefully();
					}
					EnderLogger.info("[Netty] Stopped Netty Server at port " + nettyPort + "...");
				}
			}, "Netty listener-" + nettyPort)).start();
			this.listenThreads.add(t);
		}

		EnderLogger.info("Initializing main Server Thread...");
		(mainThread = new Thread(new Runnable() {
			long lastTick = System.currentTimeMillis();
			long tick = 0;

			@Override
			@SuppressWarnings("SleepWhileInLoop")
			public void run() {
				EnderLogger.info("[ServerThread] Main Server Thread initialized and started!");
				EnderLogger.info("[ServerThread] " + NAME + " Server started, " + PROTOCOL_VERSION + " clients can now connect to port " + port + "!");

				try {
					while (Main.this.isRunning) {
						mainServerTick();
					}
				} catch (InterruptedException e) {
					Main.this.isRunning = false;
					Thread.currentThread().interrupt();
				} catch (RuntimeException ex) {
					EnderLogger.error("[ServerThread] CRASH REPORT! (this should not happen!)");
					EnderLogger.error("[ServerThread] Main thread has shutdown, this shouldn't happen!");
					EnderLogger.exception(ex);
					EnderLogger.error("[ServerThread] Server is inside tick " + tick);
					EnderLogger.error("[ServerThread] Last tick was in " + new Date(lastTick).toString());
				} finally {
					Main.this.isRunning = false;
					Main.getInstance().directShutdown();
					EnderLogger.info("[ServerThread] Main Server Thread stopped!");
				}
			}

			private void mainServerTick() throws InterruptedException {
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
				synchronized (sendToMainThread) {
					for (Runnable run : sendToMainThread) {
						try {
							run.run();
						} catch (Exception e) {
							EnderLogger.warn("Problem while executing task " + run.toString());
							EnderLogger.exception(e);
						}
					}
					sendToMainThread.clear();
				}

				try {
					serverTick();
				} catch (Exception e) {
					EnderLogger.error("Problem while running ServerTick()");
					EnderLogger.exception(e);
				}
				this.lastTick += Main.EXCEPTED_SLEEP_TIME;
				long sleepTime = (lastTick) - System.currentTimeMillis();
				if (sleepTime < Main.CANT_KEEP_UP_TIMEOUT) {
					this.warn("Can't keep up! " + (sleepTime / Main.EXCEPTED_SLEEP_TIME) + " ticks behind!");
					this.lastTick = System.currentTimeMillis();
				} else if (sleepTime > Main.MAX_SLEEP) {
					this.warn("Did the system time change?");
					this.lastTick = System.currentTimeMillis();
				} else if (sleepTime > 0) {
					Thread.sleep(sleepTime);
				}
				tick++;
			}

			public void warn(String warn) {
				EnderLogger.warn("[ServerThread] [tick-" + tick + "] " + warn);
			}
		}, "Enderstone server thread.")).start();

		ThreadGroup shutdownHooks = new ThreadGroup(Thread.currentThread().getThreadGroup(), "Shutdown hooks");
		Runtime.getRuntime().addShutdownHook(new Thread(shutdownHooks, new Runnable() {

			@Override
			public void run() {
				Main.this.scheduleShutdown();
				boolean interrupted = false;
				boolean joined = false;
				do {
					try {
						mainThread.join();
						joined = true;
					} catch (InterruptedException ex) {
						interrupted = true;
					}
				} while (!joined);
				if (interrupted)
					Thread.currentThread().interrupt();
			}
		}, "Server stopping"));
	}

	private boolean readFavicon() throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			BufferedImage image = ImageIO.read(new File("server-icon.png"));
			if (image.getWidth() == 64 && image.getHeight() == 64) {
				ImageIO.write(image, "png", baos);
				baos.flush();
				FAVICON = "data:image/png;base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());
				return true;
			} else {
				EnderLogger.warn("Your server-icon.png needs to be 64*64!");
				return false;
			}
		}
	}

	public void saveConfigToDisk(boolean defaultt) {
		try (OutputStream output = new FileOutputStream("config.ender")) {
			if (defaultt) {
				prop.setProperty("motd", "Another Enderstone server!");
				prop.setProperty("port", "25565");
				prop.setProperty("max-players", "20");
				prop.setProperty("view-distance", "7");
			}
			prop.store(output, "Enderstone Server Config!");
		} catch (IOException e1) {
			EnderLogger.exception(e1);
		}
	}

	public Properties loadConfigFromDisk() {
		prop = new Properties();
		try (InputStream input = new FileInputStream("config.ender")) {
			prop.load(input);
			port = Integer.parseInt(prop.getProperty("port"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.saveConfigToDisk(true);
			this.loadConfigFromDisk();
		} catch (IOException e) {
			EnderLogger.exception(e);
		}
		return prop;
	}

	public EnderPlayer getPlayer(String name) {
		for (EnderPlayer ep : this.onlinePlayers) {
			if (ep.getPlayerName().equals(name)) {
				return ep;
			}
		}
		return null;
	}

	private int latestKeepAlive = 0;
	private int latestChunkUpdate = 0;

	private void serverTick() {
		if ((latestKeepAlive++ & 0b0111_1111) == 0) { // faster than % 127 == 0
			for (EnderPlayer p : onlinePlayers) {
				p.getNetworkManager().sendPacket(new PacketKeepAlive(p.keepAliveID = random.nextInt(Integer.MAX_VALUE)));
			}
		}

		if ((latestChunkUpdate++ & 0b0001_1111) == 0) { // faster than % 31 == 0
			for (EnderPlayer p : onlinePlayers) {
				mainWorld.doChunkUpdatesForPlayer(p, p.chunkInformer, 10);
				p.updatePlayers(onlinePlayers);
			}
		}
	}

	/**
	 * Schedule a server shutdown, calling this methodes says to the main thread
	 * that the server need to shutdown
	 */
	public void scheduleShutdown() {
		this.mainThread.interrupt();
		isRunning = false;
	}

	/**
	 * Any mainthread-shutdown logic belongs to this method
	 */
	private void directShutdown() {
		if (this.mainThread != null) {
			this.mainThread.interrupt();
		}
		for (Thread t : this.listenThreads) {
			t.interrupt();
		}
		boolean interrupted = false;
		for (Thread t : this.listenThreads) {
			boolean joined = false;
			do {
				try {
					t.join();
					joined = true;
				} catch (InterruptedException ex) {
					interrupted = true;
				}
			} while (!joined);
		}
		if (interrupted)
			Thread.currentThread().interrupt();
	}

	public EnderPlayer getPlayer(int entityId) {
		for (EnderPlayer ep : this.onlinePlayers) {
			if (ep.getEntityId() == entityId) {
				return ep;
			}
		}
		return null;
	}
}
