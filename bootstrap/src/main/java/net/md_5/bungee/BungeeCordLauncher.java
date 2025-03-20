package net.md_5.bungee;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.command.ConsoleCommandSender;

public class BungeeCordLauncher {
  private static int VERSION = 211;

  public static void main(String[] args) throws Exception {
    Security.setProperty("networkaddress.cache.ttl", "30");
    Security.setProperty("networkaddress.cache.negative.ttl", "10");
    OptionParser parser = new OptionParser();
    parser.allowsUnrecognizedOptions();
    parser.acceptsAll(Arrays.asList("v", "version"));
    parser.acceptsAll(Arrays.asList("noconsole"));
    OptionSet options = parser.parse(args);
    if (options.has("version")) {
      System.out.println(Bootstrap.class.getPackage().getImplementationVersion());
      return;
    }
    if (System.getProperty("IReallyKnowWhatIAmDoingISwear") == null && checkUpdate()) {
      System.err.println("*** \u0412\u041d\u0418\u041c\u0410\u041d\u0418\u0415! \u041d\u0430\u0439\u0434\u0435\u043d\u0430 \u043d\u043e\u0432\u0430\u044f \u0432\u0435\u0440\u0441\u0438\u044f***");
      System.err.println("*** \u041d\u043e\u0432\u0430\u044f \u0432\u0435\u0440\u0441\u0438\u044f \u0442\u0443\u0442: ***");
      System.err.println("*** http://www.rubukkit.org/threads/137038/ ***");
      System.err.println("*** \u0420\u0435\u043a\u043e\u043c\u0435\u043d\u0434\u0443\u044e \u043e\u0431\u043d\u043e\u0432\u0438\u0442\u044c\u0441\u044f. ***");
      System.err.println("*** \u0417\u0430\u043f\u0443\u0441\u043a \u0447\u0435\u0440\u0435\u0437 5 \u0441\u0435\u043a\u0443\u043d\u0434 ***");
      Thread.sleep(TimeUnit.SECONDS.toMillis(5));
    }
    BungeeCord bungee = new BungeeCord();
    ProxyServer.setInstance(bungee);
    bungee.getLogger().info("\u0412\u043a\u043b\u044e\u0447\u0430\u044e BungeCord BotFilter " + bungee.getGameVersion() + " \u043e\u0442 vk.com/Leymooo_s");
    bungee.start();
    if (!options.has("noconsole")) {
      String line;
      while (bungee.isRunning && (line = bungee.getConsoleReader().readLine(">")) != null) {
        if (!bungee.getPluginManager().dispatchCommand(ConsoleCommandSender.getInstance(), line)) {
          bungee.getConsole().sendMessage(
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/d8441fb2ff4932b211cab72d0e4a34bc1206affb/bootstrap/src/main/java/net/md_5/bungee/BungeeCordLauncher.java/left.java
          ChatColor.RED + "\u041a\u043e\u043c\u0430\u043d\u0434\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430 :("
=======
          new ComponentBuilder("Command not found").color(ChatColor.RED).create()
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/d8441fb2ff4932b211cab72d0e4a34bc1206affb/bootstrap/src/main/java/net/md_5/bungee/BungeeCordLauncher.java/right.java
          );
        }
      }
    }
  }

  private static boolean checkUpdate() {
    try {
      System.out.println("[BotFilter] \u041f\u0440\u043e\u0432\u0435\u0440\u044f\u044e \u043d\u0430\u043b\u0438\u0447\u0435\u0435 \u043e\u0431\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u0439");
      URL url = new URL("http://151.80.108.152/gg-version.txt");
      URLConnection conn = url.openConnection();
      conn.setConnectTimeout(1500);
      try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
        return Integer.parseInt(in.readLine()) != VERSION;
      }
    } catch (IOException | NumberFormatException ex) {
      System.err.println("[BotFilter] \u041d\u0435 \u043c\u043e\u0433\u0443 \u043f\u0440\u043e\u0432\u0435\u0440\u0438\u0442\u044c \u043e\u0431\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u0435");
    }
    return false;
  }
}