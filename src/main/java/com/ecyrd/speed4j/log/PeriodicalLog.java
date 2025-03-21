package com.ecyrd.speed4j.log;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.management.*;
import com.ecyrd.speed4j.ConfigurationException;
import com.ecyrd.speed4j.StopWatch;

/**
 *  A Periodical log which can also expose its attributes via JMX.
 *  <p>
 *  The JMX name is based on the name of the Log.  So if you don't set
 *  it via {@link #setName(String)}, you'll end up something that Speed4J
 *  picks up on its own.  Normally, if you use the property file to
 *  configure Speed4J, this gets automatically assigned for you.
 *  <p>
 *  The PeriodicalLog starts a new Thread to collect the statistics. Don't forget to shut it down with a call to {@link #shutdown()},
 *  or else you might risk a memory leak.  This is a common problem with e.g. web applications, where redeployment
 *  regularly causes these.
 *  <p>
 *  In a web app, you could set up your own ServletContextListener to ensure the proper shutdown:
 *  <pre>
 *  class MyListener implements ServletContextListener {
 *     public void contextInitialized(ServletContextEvent sce) {}
 *     
 *     public void contextDestroyed(ServletContextEvent sce) {
 *         StopWatchFactory.getInstance("myLoggerName").shutdown();
 *     }
 *  }
 *  </pre>
 *  PeriodicalLog adds its own JVM-wide shutdown hook, so you don't need to prepare for that.3
 */
public class PeriodicalLog extends Slf4jLog implements DynamicMBean {
  private static final 
<<<<<<< /usr/src/app/output/jalkanen/speed4j/18370b95449b0062c7f3286734bb393cf47e02a1/src/main/java/com/ecyrd/speed4j/log/PeriodicalLog.java/left.java
  String
=======
  ScheduledExecutorService
>>>>>>> /usr/src/app/output/jalkanen/speed4j/18370b95449b0062c7f3286734bb393cf47e02a1/src/main/java/com/ecyrd/speed4j/log/PeriodicalLog.java/right.java
   
<<<<<<< /usr/src/app/output/jalkanen/speed4j/18370b95449b0062c7f3286734bb393cf47e02a1/src/main/java/com/ecyrd/speed4j/log/PeriodicalLog.java/left.java
  JMX_QUEUE_LENGTH = "StopWatchQueueLength"
=======
  scheduler = Executors.newScheduledThreadPool(1)
>>>>>>> /usr/src/app/output/jalkanen/speed4j/18370b95449b0062c7f3286734bb393cf47e02a1/src/main/java/com/ecyrd/speed4j/log/PeriodicalLog.java/right.java
  ;

  private static final int ATTRS_PER_ITEM = 6;

  private static final String ATTR_POSTFIX_MAX = "/max";

  private static final String ATTR_POSTFIX_MIN = "/min";

  private static final String ATTR_POSTFIX_STDDEV = "/stddev";

  private static final String ATTR_POSTFIX_AVG = "/avg";

  private static final String ATTR_POSTFIX_COUNT = "/count";

  private static final String ATTR_POSTFIX_95 = "/95";

  private Queue<StopWatch> m_queue = new ConcurrentLinkedQueue<StopWatch>();

  private Thread m_collectorThread;

  private boolean m_running = true;

  private int m_periodSeconds = 30;

  private MBeanServer m_mbeanServer = null;

  private String[] m_jmxAttributes = null;

  private MBeanInfo m_beanInfo;

  private Map<String, JmxStatistics> m_jmxStatistics;

  /**
     *  Creates an instance of PeriodicalLog.
     */
  public PeriodicalLog() {
    m_collectorThread = new CollectorThread();
    m_collectorThread.setName("Speed4J PeriodicalLog Collector Thread");
    m_collectorThread.setDaemon(true);
    scheduler.scheduleWithFixedDelay(m_collectorThread, m_periodSeconds, m_periodSeconds, TimeUnit.SECONDS);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override public void run() {
        shutdown();
      }
    });
  }

  @Override public void log(StopWatch sw) {
    m_queue.add(sw.freeze());
  }

  /**
     *  Set the tags which are shown via JMX.
     *  
     *  @param value A comma-separated list of tags which are shown via JMX.
     */
  public void setJmx(String value) {
    m_jmxAttributes = value.split(",");
    m_mbeanServer = ManagementFactory.getPlatformMBeanServer();
    try {
      buildMBeanInfo();
      if (m_mbeanServer.isRegistered(getJMXName())) {
        m_mbeanServer.unregisterMBean(getJMXName());
      }
      m_mbeanServer.registerMBean(this, getJMXName());
    } catch (InstanceAlreadyExistsException e) {
    } catch (Exception e) {
      throw new ConfigurationException(e);
    }
  }

  /**
     *  Shuts down the collector thread and removes the JMX bean
     *  if it is registered.  It is <i>very</i> important to call this
     *  or else you risk a memory leak.
     */
  @Override public void shutdown() {
    m_running = false;
    scheduler.shutdown();
    try {
      if (m_mbeanServer != null && m_mbeanServer.isRegistered(getJMXName())) {
        m_mbeanServer.unregisterMBean(getJMXName());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
     *  The name under which this Log should be exposed as a JMX bean.
     *  
     *  @return A ready-to-use ObjectName.
     *  
     *  @throws MalformedObjectNameException If your name is faulty.
     */
  private ObjectName getJMXName() throws MalformedObjectNameException {
    return new ObjectName("Speed4J: name=" + getName());
  }

  /**
     *  Empties the queue and calculates the results.
     *  Thread-safety is done as follows: In order to avoid concurrent modifications,
     *  we have a thread-safe Queue object.  We pull StopWatches from the head of
     *  the queue, at which point we become the sole owners of the object.
     *  <p>
     *  If the queue has objects which are newer than what we're supposed to handle,
     *  we leave them in the queue and stop processing at that time.
     *  <p>
     *  This should be the only method that changes the statistics object, so it does
     *  not require locking either.
     *  <p>
     *  TODO: There is a known problem if there are tons of Threads and our calculations take
     *  a very long time, and Thread.sleep() becomes inaccurate: the finalMoment will start 
     *  to slip forward.
     */
  private void doLog(long lastRun, long finalMoment) {
    if (m_log == null || !m_log.isInfoEnabled()) {
      return;
    }
    StopWatch sw;
    HashMap<String, CollectedStatistics> stats = new HashMap<String, CollectedStatistics>();
    while (null != (sw = m_queue.peek())) {
      if (sw.getCreationTime() > finalMoment) {
        break;
      }
      m_queue.remove();
      CollectedStatistics cs = stats.get(sw.getTag());
      if (cs == null) {
        cs = new CollectedStatistics();
        stats.put(sw.getTag(), cs);
      }
      cs.add(sw);
    }
    printf("Statistics from %tc to %tc", new Date(lastRun), new Date(finalMoment));
    printf("Tag                                       Avg(ms)      Min      Max  Std Dev     95th   Count");
    for (Map.Entry<String, CollectedStatistics> e : stats.entrySet()) {
      CollectedStatistics cs = e.getValue();
      printf("%-40s %8.2f %8.2f %8.2f %8.2f %8.2f %7d", e.getKey(), cs.getAverageMS(), cs.getMin(), cs.getMax(), cs.getStdDev(), cs.getPercentile(95), cs.getInvocations());
    }
    if (m_jmxAttributes != null) {
      m_jmxStatistics = new ConcurrentHashMap<String, PeriodicalLog.JmxStatistics>();
      for (String name : m_jmxAttributes) {
        String n = name.trim();
        CollectedStatistics cs = stats.get(n);
        if (cs == null) {
          continue;
        }
        JmxStatistics js = new JmxStatistics();
        js.count = cs.getInvocations();
        js.max = cs.getMax();
        js.min = cs.getMin();
        js.mean = cs.getAverageMS();
        js.perc95 = cs.getPercentile(95);
        js.stddev = cs.getStdDev();
        m_jmxStatistics.put(n, js);
      }
    }
    printf("");
  }

  /**
     *  Writes to the internal logger, just like ye goode olde C printf().
     *  
     *  @param pattern Pattern to write to (see {@link Formatter#format(String, Object...)}
     *  @param args Arguments for the pattern.
     */
  private void printf(String pattern, Object... args) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    formatter.format(pattern, args);
    m_log.info(sb.toString());
  }

  private class CollectorThread extends Thread {
    long lastRun = System.currentTimeMillis();

    @Override public void run() {
      long now = System.currentTimeMillis();
      doLog(lastRun, now);
      lastRun = now;
    }
  }

  /**
     *  Set the logging period in seconds.  For example, a value of 5
     *  would log every 5 seconds, at 0,5,10,15,20,25,30,35,40,45,50, and 55 seconds
     *  after the full minute.
     *  
     *  @param periodSeconds The period in seconds.
     */
  public void setPeriod(int periodSeconds) {
    m_periodSeconds = periodSeconds;
  }

  public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
    if (attribute.equals(JMX_QUEUE_LENGTH)) {
      return m_queue.size();
    }
    Map<String, JmxStatistics> stats = m_jmxStatistics;
    if (stats != null) {
      String key = attribute.substring(0, attribute.lastIndexOf('/'));
      String postfix = attribute.substring(attribute.lastIndexOf('/'));
      JmxStatistics cs = stats.get(key);
      if (cs == null) {
        return null;
      }
      if (postfix.equals(ATTR_POSTFIX_AVG)) {
        return cs.mean;
      }
      if (postfix.equals(ATTR_POSTFIX_MAX)) {
        return cs.max;
      }
      if (postfix.equals(ATTR_POSTFIX_MIN)) {
        return cs.min;
      }
      if (postfix.equals(ATTR_POSTFIX_STDDEV)) {
        return cs.stddev;
      }
      if (postfix.equals(ATTR_POSTFIX_COUNT)) {
        return cs.count;
      }
      if (postfix.equals(ATTR_POSTFIX_95)) {
        return cs.perc95;
      }
      throw new AttributeNotFoundException(attribute);
    }
    return null;
  }

  public AttributeList getAttributes(String[] attributes) {
    AttributeList ls = new AttributeList();
    for (String s : attributes) {
      try {
        ls.add(new Attribute(s, getAttribute(s)));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return ls;
  }

  public MBeanInfo getMBeanInfo() {
    return m_beanInfo;
  }

  public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
    return null;
  }

  public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
  }

  public AttributeList setAttributes(AttributeList attributes) {
    return null;
  }

  /**
     *  Builds the MBeanInfo for all the exposed attributes.
     *  
     *  @throws IntrospectionException
     */
  private void buildMBeanInfo() throws IntrospectionException {
    MBeanAttributeInfo[] attributes = null;
    if (m_jmxAttributes != null) {
      attributes = new MBeanAttributeInfo[m_jmxAttributes.length * ATTRS_PER_ITEM + 1];
      for (int i = 0; i < m_jmxAttributes.length; i++) {
        String name = m_jmxAttributes[i].trim();
        attributes[ATTRS_PER_ITEM * i] = new MBeanAttributeInfo(name + ATTR_POSTFIX_AVG, "double", "Average value (in milliseconds)", true, false, false);
        attributes[ATTRS_PER_ITEM * i + 1] = new MBeanAttributeInfo(name + ATTR_POSTFIX_STDDEV, "double", "Standard Deviation", true, false, false);
        attributes[ATTRS_PER_ITEM * i + 2] = new MBeanAttributeInfo(name + ATTR_POSTFIX_MIN, "double", "Minimum value", true, false, false);
        attributes[ATTRS_PER_ITEM * i + 3] = new MBeanAttributeInfo(name + ATTR_POSTFIX_MAX, "double", "Maximum value", true, false, false);
        attributes[ATTRS_PER_ITEM * i + 4] = new MBeanAttributeInfo(name + ATTR_POSTFIX_COUNT, "int", "Number of invocations", true, false, false);
        attributes[ATTRS_PER_ITEM * i + 5] = new MBeanAttributeInfo(name + ATTR_POSTFIX_95, "double", "95th percentile", true, false, false);
      }
      attributes[attributes.length - 1] = new MBeanAttributeInfo(JMX_QUEUE_LENGTH, "int", "Current StopWatch processing queue length (i.e. how many StopWatches are currently unprocessed)", true, false, false);
    }
    MBeanOperationInfo[] operations = null;
    MBeanConstructorInfo[] constructors = null;
    MBeanNotificationInfo[] notifications = null;
    m_beanInfo = new MBeanInfo(getClass().getName(), "PeriodicalLog for logger " + getName(), attributes, constructors, operations, notifications);
  }

  private static class JmxStatistics {
    public double mean;

    public double stddev;

    public double min;

    public double max;

    public int count;

    public double perc95;
  }
}