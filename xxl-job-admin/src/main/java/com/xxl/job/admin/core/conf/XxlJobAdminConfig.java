package com.xxl.job.admin.core.conf;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.admin.dao.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Component public class XxlJobAdminConfig implements InitializingBean, DisposableBean {
  private static XxlJobAdminConfig adminConfig = null;

  public static XxlJobAdminConfig getAdminConfig() {
    return adminConfig;
  }

  private XxlJobScheduler xxlJobScheduler;

  @Override public void afterPropertiesSet() throws Exception {
    adminConfig = this;
    xxlJobScheduler = new XxlJobScheduler();
    xxlJobScheduler.init();
  }

  @Override public void destroy() throws Exception {
    xxlJobScheduler.destroy();
  }

  @Value(value = "${xxl.job.i18n}") private String i18n;

  @Value(value = "${xxl.job.accessToken}") private String accessToken;

  @Value(value = "${spring.mail.username}") private String emailUserName;


<<<<<<< /usr/src/app/output/xuxueli/xxl-job/c3a34c24ecda1f4a53be68f770e46b5b921578a5/xxl-job-admin/src/main/java/com/xxl/job/admin/core/conf/XxlJobAdminConfig.java/left.java
  @Value(value = "${xxl.job.hook.path:hooks}") private String hookPath;
=======
  @Value(value = "${xxl.job.triggerpool.fast.max}") private int triggerPoolFastMax;
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/c3a34c24ecda1f4a53be68f770e46b5b921578a5/xxl-job-admin/src/main/java/com/xxl/job/admin/core/conf/XxlJobAdminConfig.java/right.java


  @Value(value = "${xxl.job.triggerpool.slow.max}") private int triggerPoolSlowMax;

  @Value(value = "${xxl.job.logretentiondays}") private int logretentiondays;

  @Resource private XxlJobLogDao xxlJobLogDao;

  @Resource private XxlJobInfoDao xxlJobInfoDao;

  @Resource private XxlJobRegistryDao xxlJobRegistryDao;

  @Resource private XxlJobGroupDao xxlJobGroupDao;

  @Resource private XxlJobLogReportDao xxlJobLogReportDao;

  @Resource private JavaMailSender mailSender;

  @Resource private DataSource dataSource;

  public String getI18n() {
    return i18n;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getEmailUserName() {
    return emailUserName;
  }

  public int getTriggerPoolFastMax() {
    if (triggerPoolFastMax < 200) {
      return 200;
    }
    return triggerPoolFastMax;
  }

  public int getTriggerPoolSlowMax() {
    if (triggerPoolSlowMax < 100) {
      return 100;
    }
    return triggerPoolSlowMax;
  }

  public int getLogretentiondays() {
    if (logretentiondays < 7) {
      return -1;
    }
    return logretentiondays;
  }

  public XxlJobLogDao getXxlJobLogDao() {
    return xxlJobLogDao;
  }

  public XxlJobInfoDao getXxlJobInfoDao() {
    return xxlJobInfoDao;
  }

  public XxlJobRegistryDao getXxlJobRegistryDao() {
    return xxlJobRegistryDao;
  }

  public XxlJobGroupDao getXxlJobGroupDao() {
    return xxlJobGroupDao;
  }

  public XxlJobLogReportDao getXxlJobLogReportDao() {
    return xxlJobLogReportDao;
  }

  public JavaMailSender getMailSender() {
    return mailSender;
  }

  public DataSource getDataSource() {
    return dataSource;
  }
}