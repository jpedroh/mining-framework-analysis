package org.wltea.analyzer.help;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

public class Sleep {
  public static final ESLogger logger = Loggers.getLogger("ik-analyzer");

  public enum Type {
    MSEC,
    SEC,
    MIN,
    HOUR
  }



  public static void sleep(Type type, int num) {
    try {
      switch (type) {
        case MSEC:
        Thread.sleep(num);
        return;
        case SEC:
        Thread.sleep(num * 1000L);
        return;
        case MIN:
        Thread.sleep(num * 60 * 1000L);
        return;
        case HOUR:
        Thread.sleep(num * 60 * 60 * 1000L);
        return;
        default:
        System.err.println("\u8f93\u5165\u7c7b\u578b\u9519\u8bef\uff0c\u5e94\u4e3aMSEC,SEC,MIN,HOUR\u4e4b\u4e00");
        return;
      }
    } catch (InterruptedException e) {
      logger.error(e.getMessage(), e);
    }
  }
}