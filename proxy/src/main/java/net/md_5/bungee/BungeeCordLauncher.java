package net.md_5.bungee;
import java.security.Security;
import java.util.Arrays;
import java.util.logging.Level;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.command.ConsoleCommandSender;

public class BungeeCordLauncher {
  public static void main(String[] args) throws Exception {
    Security.setProperty("networkaddress.cache.ttl", "30");
    Security.setProperty("networkaddress.cache.negative.ttl", "10");
    if (System.getProperty("jdk.util.jar.enableMultiRelease") == null) {
      System.setProperty("jdk.util.jar.enableMultiRelease", "force");
    }
    OptionParser parser = new OptionParser();
    parser.allowsUnrecognizedOptions();
    parser.acceptsAll(Arrays.asList("help"), "Show the help");
    parser.acceptsAll(Arrays.asList("v", "version"), "Print version and exit");
    parser.acceptsAll(Arrays.asList("noconsole"), "Disable console input");
    OptionSet options = parser.parse(args);
    if (options.has("help")) {
      parser.printHelpOn(System.out);
      return;
    }
    if (options.has("version")) {
      System.out.println(BungeeCord.class.getPackage().getImplementationVersion());
      return;
    }
    BungeeCord bungee = new BungeeCord();
    ProxyServer.setInstance(bungee);
    bungee.getLogger().log(Level.WARNING, "\u0412\u043a\u043b\u044e\u0447\u0430\u044e BungeCord BotFilter {0} \u043e\u0442 vk.com/Leymooo_s (http://rubukkit.org/threads/137038)", bungee.getGameVersion());
    bungee.start();
    if (!options.has("noconsole")) {
      String line;
      while (bungee.isRunning && (line = bungee.getConsoleReader().readLine(">")) != null) {
        if (!bungee.getPluginManager().dispatchCommand(ConsoleCommandSender.getInstance(), line)) {
          bungee.getConsole().sendMessage(new ComponentBuilder("\u041a\u043e\u043c\u0430\u043d\u0434\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430 :(").color(ChatColor.RED).create());
        }
      }
    }
  }
}