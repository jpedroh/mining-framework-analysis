package net.tridentsdk.util;
import net.tridentsdk.Trident;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.docs.Volatile;
import net.tridentsdk.plugin.TridentPluginHandler;
import org.apache.log4j.*;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Random;

@Volatile(policy = "Init FIRST", reason = "Requires SLF4J to be configured", fix = "first static block in main class") public final class TridentLogger {
  private static final String[] ERRORS = { "Aw, Mazen! Really?", "I feel funny", "9 + 10 does not equal 21", "Dang", "Thanks, Obama", "God dammit Tony Abbot, the fax didn\'t go through", "This wasn\'t supposed to happen. It did anyways.", "Bukkit 1.8 not found, contact Dinnerbone", "Huston, we have a problem", "Oh great, a stacktrace. Can\'t we write good software for once?", "Trust me this isn\'t a bug, it\'s a feature!", "Vilsol screamed your JVM to death", "Herobrine", "1 + 1 = 3" };

  private TridentLogger() {
  }

  @InternalUseOnly public static void init() {
    ConsoleAppender console = new ConsoleAppender();
    String PATTERN = "%d{dd MMM HH:mm} [%p] %m%n";
    console.setLayout(new PatternLayout(PATTERN));
    console.setThreshold(Level.INFO);
    console.activateOptions();
    Logger.getRootLogger().addAppender(console);
    FileAppender fa = new FileAppender();
    fa.setName("FileLogger");
    fa.setFile("trident.log");
    fa.setLayout(new PatternLayout(PATTERN));
    fa.setThreshold(Level.DEBUG);
    fa.setAppend(true);
    fa.activateOptions();
    Logger.getRootLogger().addAppender(fa);
  }

  private static Class<?> getCaller() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    StackTraceElement element = elements[4];
    try {
      return Class.forName(element.getClassName());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static org.slf4j.Logger getLogger() {
    return LoggerFactory.getLogger(getCaller());
  }

  public static void log(String item) {
    getLogger().info(item);
  }

  public static void error(String message) {
    getLogger().error(message);
  }

  public static void error(Throwable throwable) {
    getLogger().error("========  BEGIN ERROR =========");
    getLogger().error("");
    Random rand = new Random();
    int randomNum = rand.nextInt((ERRORS.length - 1) + 1) + 0;
    getLogger().error(ERRORS[randomNum]);
    getLogger().error("Error occurred in thread \"" + Thread.currentThread().getName() + "\": " + throwable.getMessage());
    getLogger().error("======== Generating Debug Information =========");
    StackTraceElement main = throwable.getStackTrace()[0];
    getLogger().error("Class:  " + main.getClassName());
    getLogger().error("Method: " + main.getMethodName());
    getLogger().error("Line:   " + main.getLineNumber());
    getLogger().error("========   Ending Debug Information   =========");
    getLogger().error("");
    getLogger().error("======== Printing Stacktrace =========");
    for (StackTraceElement element : throwable.getStackTrace()) {
      getLogger().error("    at " + element.getClassName() + "." + element.getMethodName() + "(...) : " + (element.getLineNumber() > 0 ? element.getLineNumber() : "Native method"));
    }
    getLogger().error("========  Ending Stacktrace  =========");
    getLogger().error("");
    getLogger().error("========     Server info    =========");
    getLogger().error("Trident version: " + Trident.getVersion());
    getLogger().error("Plugins:         " + Arrays.toString(TridentPluginHandler.getPluginExecutorFactory().values().toArray()));
    getLogger().error("Java:            version " + System.getProperty("java.version") + " distributed by " + System.getProperty("java.vendor"));
    getLogger().error("OS:              running " + System.getProperty("os.name") + " version " + System.getProperty("os.version") + " " + System.getProperty("os.arch"));
    getLogger().error("======== Ending Server info =========");
    getLogger().error("");
    getLogger().error("========      END ERROR     =========");
  }
}