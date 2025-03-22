package com.xxl.job.admin.core.schedule;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.jobbean.RemoteHttpJobBean;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.thread.JobFailMonitorHelper;
import com.xxl.job.admin.core.thread.JobRegistryMonitorHelper;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.invoker.call.CallType;
import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.net.impl.jetty.server.JettyServerHandler;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.serialize.Serializer;
import org.eclipse.jetty.server.Request;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  调度中心核心类
 * base quartz scheduler util
 * @author xuxueli 2015-12-19 16:13:53
 */
public final class XxlJobDynamicScheduler {
  private static final Logger logger = LoggerFactory.getLogger(XxlJobDynamicScheduler.class);

  private static Scheduler scheduler;

  public void setScheduler(Scheduler scheduler) {
    XxlJobDynamicScheduler.scheduler = scheduler;
  }

  public void start() throws Exception {
    Assert.notNull(scheduler, "quartz scheduler is null");
    initI18n();
    JobRegistryMonitorHelper.getInstance().start();
    JobFailMonitorHelper.getInstance().start();
    initRpcProvider();
    logger.info(">>>>>>>>> init xxl-job admin success.");
  }

  private static JettyServerHandler jettyServerHandler;

  public void destroy() throws Exception {
    JobTriggerPoolHelper.toStop();
    JobRegistryMonitorHelper.getInstance().toStop();
    JobFailMonitorHelper.getInstance().toStop();
    stopRpcProvider();
  }


<<<<<<< /usr/src/app/output/xuxueli/xxl-job/36a114cac368477d23daf49693ccd6ea4e591f6e/xxl-job-admin/src/main/java/com/xxl/job/admin/core/schedule/XxlJobDynamicScheduler.java/left.java
  public void setAccessToken(String accessToken) {
    XxlJobDynamicScheduler.accessToken = accessToken;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  private void initI18n() {
    for (ExecutorBlockStrategyEnum item : ExecutorBlockStrategyEnum.values()) {
      item.setTitle(I18nUtil.getString("jobconf_block_".concat(item.name())));
    }
  }

  private void initRpcProvider() {
    XxlRpcProviderFactory xxlRpcProviderFactory = new XxlRpcProviderFactory();
    xxlRpcProviderFactory.initConfig(NetEnum.JETTY, Serializer.SerializeEnum.HESSIAN.getSerializer(), null, 0, XxlJobAdminConfig.getAdminConfig().getAccessToken(), null, null);
    xxlRpcProviderFactory.addService(AdminBiz.class.getName(), null, XxlJobAdminConfig.getAdminConfig().getAdminBiz());
    jettyServerHandler = new JettyServerHandler(xxlRpcProviderFactory);
  }

  private void stopRpcProvider() throws Exception {
    new XxlRpcInvokerFactory().stop();
  }

  public static void invokeAdminService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    jettyServerHandler.handle(null, new Request(null, null), request, response);
  }

  private static ConcurrentHashMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();

  public static ExecutorBiz getExecutorBiz(String address) throws Exception {
    if (address == null || address.trim().length() == 0) {
      return null;
    }
    address = address.trim();
    ExecutorBiz executorBiz = executorBizRepository.get(address);
    if (executorBiz != null) {
      return executorBiz;
    }
    executorBiz = (ExecutorBiz) new XxlRpcReferenceBean(NetEnum.JETTY, Serializer.SerializeEnum.HESSIAN.getSerializer(), CallType.SYNC, ExecutorBiz.class, null, 10000, address, XxlJobAdminConfig.getAdminConfig().getAccessToken(), null).getObject();
    executorBizRepository.put(address, executorBiz);
    return executorBiz;
  }

  /**
     * fill job info
     *
     * @param jobInfo
     */
  public static void fillJobInfo(XxlJobInfo jobInfo) {
    String group = String.valueOf(jobInfo.getJobGroup());
    String name = String.valueOf(jobInfo.getId());
    TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
    try {
      Trigger trigger = scheduler.getTrigger(triggerKey);
      if (trigger != null && trigger instanceof CronTriggerImpl) {
        String cronExpression = ((CronTriggerImpl) trigger).getCronExpression();
        jobInfo.setJobCron(cronExpression);
      }
      TriggerState triggerState = scheduler.getTriggerState(triggerKey);
      if (triggerState != null) {
        jobInfo.setJobStatus(triggerState.name());
      }
    } catch (SchedulerException e) {
      logger.error(e.getMessage(), e);
    }
  }

  /**
     * addJob
     *  将任务添加到scheduler器中
     * @param jobName
     * @param jobGroup
     * @param cronExpression
     * @return
     * @throws SchedulerException
     */
  public static boolean addJob(String jobName, String jobGroup, String cronExpression) throws SchedulerException {
    TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
    JobKey jobKey = new JobKey(jobName, jobGroup);
    if (scheduler.checkExists(triggerKey)) {
      return true;
    }
    CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
    CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
    Class<? extends Job> jobClass_ = RemoteHttpJobBean.class;
    JobDetail jobDetail = JobBuilder.newJob(jobClass_).withIdentity(jobKey).build();
    Date date = scheduler.scheduleJob(jobDetail, cronTrigger);
    logger.info(">>>>>>>>>>> addJob success, jobDetail:{}, cronTrigger:{}, date:{}", jobDetail, cronTrigger, date);
    return true;
  }

  /**
     * unscheduleJob
     * 移除任务
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
  public static boolean removeJob(String jobName, String jobGroup) throws SchedulerException {
    TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
    if (scheduler.checkExists(triggerKey)) {
      scheduler.unscheduleJob(triggerKey);
    }
    logger.info(">>>>>>>>>>> removeJob success, triggerKey:{}", triggerKey);
    return true;
  }

  /**
     * updateJobCron
     *
     * @param jobGroup
     * @param jobName
     * @param cronExpression
     * @return
     * @throws SchedulerException
     */
  public static boolean updateJobCron(String jobGroup, String jobName, String cronExpression) throws SchedulerException {
    TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
    if (!scheduler.checkExists(triggerKey)) {
      return true;
    }
    CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
    String oldCron = oldTrigger.getCronExpression();
    if (oldCron.equals(cronExpression)) {
      return true;
    }
    CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
    oldTrigger = oldTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
    scheduler.rescheduleJob(triggerKey, oldTrigger);
    logger.info(">>>>>>>>>>> resumeJob success, JobGroup:{}, JobName:{}", jobGroup, jobName);
    return true;
  }
}