package com.xxl.job.admin.core.scheduler;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.thread.*;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.client.ExecutorBizClient;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xuxueli 2018-10-28 00:18:17
 */
public class XxlJobScheduler {
  private static final Logger logger = LoggerFactory.getLogger(XxlJobScheduler.class);

  public void init() throws Exception {
    initI18n();
    JobTriggerPoolHelper.toStart();
    JobRegistryHelper.getInstance().start();
    JobFailMonitorHelper.getInstance().start();
    JobCompleteHelper.getInstance().start();
    JobLogReportHelper.getInstance().start();
    JobScheduleHelper.getInstance().start();
    logger.info(">>>>>>>>> init xxl-job admin success.");
  }

  public void destroy() throws Exception {
    JobScheduleHelper.getInstance().toStop();
    JobLogReportHelper.getInstance().toStop();
    JobCompleteHelper.getInstance().toStop();
    JobFailMonitorHelper.getInstance().toStop();
    JobRegistryHelper.getInstance().toStop();
    JobTriggerPoolHelper.toStop();
  }

  private void initI18n() {
    for (ExecutorBlockStrategyEnum item : ExecutorBlockStrategyEnum.values()) {
      item.setTitle(I18nUtil.getString("jobconf_block_".concat(item.name())));
    }
  }

  private static ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();

  public static ExecutorBiz getExecutorBiz(String address) throws Exception {
    if (address == null || address.trim().length() == 0) {
      return null;
    }
    address = address.trim();
    ExecutorBiz executorBiz = executorBizRepository.get(address);
    if (executorBiz != null) {
      return executorBiz;
    }
    executorBiz = new ExecutorBizClient(address, XxlJobAdminConfig.getAdminConfig().getAccessToken());
    executorBizRepository.put(address, executorBiz);
    return executorBiz;
  }
}