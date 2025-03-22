package com.roncoo.pay.app.settlement;
import com.roncoo.pay.app.settlement.scheduled.SettScheduled;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 结算定时任务.(分商户统计账户历史进行汇总)
 * 龙果学院：www.roncoo.com
 * @author zenghao
 */
@Component public class SettTask {
  private static final Log LOG = LogFactory.getLog(SettTask.class);

  private static final long MILLIS = 1000L;

  @Autowired private SettScheduled settScheduled;

  @Scheduled(cron = "0 0 11 * * ?") public void runTask() {
    try {
      LOG.debug("\u6267\u884c(\u6bcf\u65e5\u5f85\u7ed3\u7b97\u6570\u636e\u6c47\u603b)\u4efb\u52a1\u5f00\u59cb");
      settScheduled.launchDailySettCollect();
      LOG.debug("\u6267\u884c(\u6bcf\u65e5\u5f85\u7ed3\u7b97\u6570\u636e\u6c47\u603b)\u4efb\u52a1\u7ed3\u675f");
      Thread.sleep(MILLIS);
      LOG.debug("\u6267\u884c(\u5b9a\u671f\u81ea\u52a8\u7ed3\u7b97)\u4efb\u52a1\u5f00\u59cb");
      settScheduled.launchAutoSett();
      LOG.debug("\u6267\u884c(\u5b9a\u671f\u81ea\u52a8\u7ed3\u7b97)\u4efb\u52a1\u7ed3\u675f");
    } catch (Exception e) {
      LOG.error("SettTask execute error:", e);
    } finally {
      System.exit(0);
      LOG.debug("SettTask Complete");
    }
  }
}