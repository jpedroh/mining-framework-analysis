package com.xxl.job.admin.core.conf;
import com.xxl.job.admin.core.alarm.JobAlarmer;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.admin.dao.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;

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

  @Value(value = "${xxl.job.oauth.enable}") private int oauthEnable;

  @Value(value = 
<<<<<<< /usr/src/app/output/xuxueli/xxl-job/7d756c2e2d9a6604e827744d2ffc2a2f48df9f86/xxl-job-admin/src/main/java/com/xxl/job/admin/core/conf/XxlJobAdminConfig.java/left.java
  "${xxl.job.oauth.clientId}"
=======
  "${spring.mail.from}"
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/7d756c2e2d9a6604e827744d2ffc2a2f48df9f86/xxl-job-admin/src/main/java/com/xxl/job/admin/core/conf/XxlJobAdminConfig.java/right.java
  ) private String 
<<<<<<< /usr/src/app/output/xuxueli/xxl-job/7d756c2e2d9a6604e827744d2ffc2a2f48df9f86/xxl-job-admin/src/main/java/com/xxl/job/admin/core/conf/XxlJobAdminConfig.java/left.java
  clientId
=======
  emailFrom
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/7d756c2e2d9a6604e827744d2ffc2a2f48df9f86/xxl-job-admin/src/main/java/com/xxl/job/admin/core/conf/XxlJobAdminConfig.java/right.java
  ;

  @Value(value = "${xxl.job.i18n}") private String i18n;

  @Value(value = "${xxl.job.oauth.clientSecret}") private String clientSecret;

  @Value(value = "${xxl.job.oauth.redirectUrl}") private String redirectUrl;

  @Value(value = "${xxl.job.oauth.authorizeUrl}") private String authorizeUrl;

  @Value(value = "${xxl.job.oauth.accessTokenUrl}") private String accessTokenUrl;

  @Value(value = "${xxl.job.oauth.resourceOwnerDetailUrl}") private String resourceOwnerDetailUrl;

  @Value(value = "${xxl.job.accessToken}") private String accessToken;

  @Value(value = "${xxl.job.triggerpool.fast.max}") private int triggerPoolFastMax;

  @Value(value = "${xxl.job.triggerpool.slow.max}") private int triggerPoolSlowMax;

  @Value(value = "${xxl.job.logretentiondays}") private int logretentiondays;

  @Resource private XxlJobLogDao xxlJobLogDao;

  @Resource private XxlJobInfoDao xxlJobInfoDao;

  @Resource private XxlJobRegistryDao xxlJobRegistryDao;

  @Resource private XxlJobGroupDao xxlJobGroupDao;

  @Resource private XxlJobLogReportDao xxlJobLogReportDao;

  @Resource private JavaMailSender mailSender;

  @Resource private DataSource dataSource;

  @Resource private JobAlarmer jobAlarmer;

  public String getI18n() {
    if (!Arrays.asList("zh_CN", "zh_TC", "en").contains(i18n)) {
      return "zh_CN";
    }
    return i18n;
  }

  public int getOauthEnable() {
    return oauthEnable;
  }

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  public String getAccessTokenUrl() {
    return accessTokenUrl;
  }

  public String getAuthorizeUrl() {
    return authorizeUrl;
  }

  public String getResourceOwnerDetailUrl() {
    return resourceOwnerDetailUrl;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getEmailFrom() {
    return emailFrom;
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

  public JobAlarmer getJobAlarmer() {
    return jobAlarmer;
  }
}