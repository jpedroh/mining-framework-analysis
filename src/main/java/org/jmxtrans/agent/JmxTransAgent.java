package org.jmxtrans.agent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jmxtrans.agent.properties.NoPropertiesSourcePropertiesLoader;
import org.jmxtrans.agent.properties.PropertiesLoader;
import org.jmxtrans.agent.properties.UrlOrFilePropertiesLoader;
import org.jmxtrans.agent.util.logging.Logger;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public class JmxTransAgent {
  private static Logger logger = Logger.getLogger(JmxTransAgent.class.getName());

  private static final String PROPERTIES_SYSTEM_PROPERTY_NAME = JmxTransAgent.class.getName() + ".properties";

  @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL") public static boolean DIAGNOSTIC = Boolean.valueOf(System.getProperty(JmxTransAgent.class.getName() + ".diagnostic", "false"));

  public static void agentmain(String configFile, Instrumentation inst) {
    initializeAgent(configFile);
  }

  public static void premain(final String configFile, Instrumentation inst) {
    final int delayInSecs = Integer.parseInt(System.getProperty("jmxtrans.agent.premain.delay", "0"));
    if (delayInSecs > 0) {
      logger.info("jmxtrans agent initialization delayed by " + delayInSecs + " seconds");
      new Thread("jmxtrans-agent-delayed-starter-" + delayInSecs + "secs") {
        @Override public void run() {
          try {
            Thread.sleep(delayInSecs * 1000);
          } catch (InterruptedException e) {
            Thread.interrupted();
            return;
          }
          initializeAgent(configFile);
        }
      }.start();
    } else {
      initializeAgent(configFile);
    }
  }

  private static void initializeAgent(String configFile) {
    dumpDiagnosticInfo();
    if (configFile == null || configFile.isEmpty()) {
      String msg = "JmxTransExporter configurationFile must be defined";
      logger.log(Level.SEVERE, msg);
      throw new IllegalStateException(msg);
    }
    try {
      PropertiesLoader propertiesLoader = createPropertiesLoader();
      JmxTransExporterBuilder jmxTransExporterBuilder = new JmxTransExporterBuilder(propertiesLoader);
      JmxTransConfigurationLoader 
<<<<<<< /usr/src/app/output/jmxtrans/jmxtrans-agent/60e40f2ad4d067e75c9433b56c139e15ba4642f5/src/main/java/org/jmxtrans/agent/JmxTransAgent.java/left.java
      config = jmxTransExporterBuilder.build(configLoader)
=======
      configurationLoader = new JmxTransConfigurationXmlLoader(configFile)
>>>>>>> /usr/src/app/output/jmxtrans/jmxtrans-agent/60e40f2ad4d067e75c9433b56c139e15ba4642f5/src/main/java/org/jmxtrans/agent/JmxTransAgent.java/right.java
      ;
      JmxTransExporter jmxTransExporter = new JmxTransExporter(configurationLoader);
      jmxTransExporter.start();
      logger.info("JmxTransAgent started with configuration \'" + configFile + "\'");

<<<<<<< /usr/src/app/output/jmxtrans/jmxtrans-agent/60e40f2ad4d067e75c9433b56c139e15ba4642f5/src/main/java/org/jmxtrans/agent/JmxTransAgent.java/left.java
      if (config.getConfigReloadInterval() >= 0) {
        setupConfigReloadWatcher(jmxTransExporter, config, configLoader, jmxTransExporterBuilder);
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.
    } catch (Exception e) {
      String msg = "Exception loading JmxTransExporter from \'" + configFile + "\'";
      logger.log(Level.SEVERE, msg, e);
      throw new IllegalStateException(msg, e);
    }
  }

  private static PropertiesLoader createPropertiesLoader() {
    String configuredPath = System.getProperty(PROPERTIES_SYSTEM_PROPERTY_NAME);
    if (configuredPath == null) {
      return new NoPropertiesSourcePropertiesLoader();
    }
    logger.log(Level.INFO, "Will use properties file \'" + configuredPath + "\' for resolving placeholders");
    return new UrlOrFilePropertiesLoader(configuredPath);
  }


<<<<<<< /usr/src/app/output/jmxtrans/jmxtrans-agent/60e40f2ad4d067e75c9433b56c139e15ba4642f5/src/main/java/org/jmxtrans/agent/JmxTransAgent.java/left.java
  private static void setupConfigReloadWatcher(JmxTransExporter jmxTransExporter, JmxTransExporterConfiguration initialConfiguration, ConfigurationDocumentLoader configLoader, JmxTransExporterBuilder jmxTransExporterBuilder) {
    ConfigReloadWatcher watcher = new ConfigReloadWatcher(jmxTransExporter, initialConfiguration, configLoader, jmxTransExporterBuilder);
    watcher.start();
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  public static void dumpDiagnosticInfo() {
    if (!JmxTransAgent.DIAGNOSTIC) {
      return;
    }
    Runnable runnable = new Runnable() {
      @Override public void run() {
        while (JmxTransAgent.DIAGNOSTIC) {
          String prefix = new Timestamp(System.currentTimeMillis()) + " [jmxtrans-agent] ";
          System.out.println(prefix + "JMXTRANS-AGENT DIAGNOSTIC INFO");
          System.out.println(prefix + "Logger level: " + Logger.level);
          Set<ObjectInstance> objectInstances = ManagementFactory.getPlatformMBeanServer().queryMBeans(null, null);
          List<ObjectName> objectNames = new ArrayList<>();
          for (ObjectInstance objectInstance : objectInstances) {
            objectNames.add(objectInstance.getObjectName());
          }
          Collections.sort(objectNames);
          System.out.println(prefix + "ManagementFactory.getPlatformMBeanServer().queryMBeans(null, null)");
          for (ObjectName objectName : objectNames) {
            System.out.println(prefix + "\t" + objectName);
          }
          System.out.println(prefix + "ENF OF JMXTRANS-AGENT DIAGNOSING INFO");
          try {
            Thread.sleep(TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS));
          } catch (InterruptedException e) {
            e.printStackTrace();
            break;
          }
        }
      }
    };
    Thread thread = new Thread(runnable);
    thread.setName("jmxtrans-agent-diagnostic");
    thread.setDaemon(true);
    thread.start();
  }
}