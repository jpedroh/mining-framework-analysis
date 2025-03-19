package org.rdfhdt.hdt.util;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import com.sun.management.HotSpotDiagnosticMXBean;

/**
 * @author mario.arias
 *
 */
public class ProfilingUtil {
  private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";

  private static volatile HotSpotDiagnosticMXBean hotspotMBean;

  private ProfilingUtil() {
  }

  /**
	 * Call this method from your application whenever you want to dump the heap snapshot into a file.
	 *
	 * @param fileName name of the heap dump file
	 * @param live flag that tells whether to dump
	 *             only the live objects
	 */
  public static void dumpHeap(String fileName, boolean live) {
    initHotspotMBean();
    try {
      hotspotMBean.dumpHeap(fileName, live);
    } catch (RuntimeException re) {
      throw re;
    } catch (Exception exp) {
      throw new RuntimeException(exp);
    }
  }

  private static void initHotspotMBean() {
    if (hotspotMBean == null) {
      synchronized (ProfilingUtil.class) {
        if (hotspotMBean == null) {
          hotspotMBean = getHotspotMBean();
        }
      }
    }
  }

  private static HotSpotDiagnosticMXBean getHotspotMBean() {
    try {
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();
      HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server, HOTSPOT_BEAN_NAME, HotSpotDiagnosticMXBean.class);
      return bean;
    } catch (RuntimeException re) {
      throw re;
    } catch (Exception exp) {
      throw new RuntimeException(exp);
    }
  }

  public static String tidyFileSize(long size) {
    long calcSize;
    String str;
    if (size >= 1024 * 1024 * 1024) {
      calcSize = (long) (((double) size) / (1024 * 1024 * 1024));
      str = "" + calcSize + "GB";
    } else {
      if (size >= 1024 * 1024) {
        calcSize = (long) (((double) size) / (1024 * 1024));
        str = "" + calcSize + "MB";
      } else {
        if (size >= 1024) {
          calcSize = (long) (((double) size) / (1024));
          str = "" + calcSize + "KB";
        } else {
          calcSize = size;
          str = "" + calcSize + "B";
        }
      }
    }
    return str;
  }

  /**
	 * A method for getting a property denoting a size in bytes (like size of cache for example)
	 * 
	 * If the property is not set the method returns -1, else it checks if it ends with a
	 * k, K, m, M, g or G and multiplies the number before with the appropriate power of 2 before
	 * returning it.
	 */
  public static long parseSize(String property) {
    if (property == null || property.equals("")) {
      return -1;
    }
    property = property.trim();
    char lastChar = property.charAt(property.length() - 1);
    switch (lastChar) {
      case 'k':
      case 'K':
      return Long.parseLong(property.substring(0, property.length() - 1)) * 1024;
      case 'm':
      case 'M':
      return Long.parseLong(property.substring(0, property.length() - 1)) * 1024 * 1024;
      case 'g':
      case 'G':
      return Long.parseLong(property.substring(0, property.length() - 1)) * 1024 * 1024 * 1024;
      default:
      return Long.parseLong(property);
    }
  }

  public static void showMemory(String label) {
    System.out.println(label + ": " + getMemory());
  }

  public static String getMemory() {
    return tidyFileSize(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " / " + tidyFileSize(Runtime.getRuntime().totalMemory()) + " / " + tidyFileSize(Runtime.getRuntime().maxMemory());
  }
}