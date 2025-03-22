package com.xxl.job.admin.core.util;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.Charset;

/**
 * 邮件发送.Util
 *
 * @author xuxueli 2016-3-12 15:06:20
 */
public class MailUtil {
  private static Logger logger = LoggerFactory.getLogger(MailUtil.class);

  /**
	 *
	 * @param toAddress		收件人邮箱
	 * @param mailSubject	邮件主题
	 * @param mailBody		邮件正文
	 * @return
	 */
  public static boolean sendMail(String toAddress, String mailSubject, String mailBody) {
    try {
      HtmlEmail email = new HtmlEmail();
      email.setSSLOnConnect(true);
      email.setHostName(XxlJobAdminConfig.getAdminConfig().getMailHost());

<<<<<<< /usr/src/app/output/xuxueli/xxl-job/c350052db6b1cc501dc09abc913df9113e6f2f5e/xxl-job-admin/src/main/java/com/xxl/job/admin/core/util/MailUtil.java/left.java
      if (XxlJobAdminConfig.getAdminConfig().isMailSSL()) {
        email.setSslSmtpPort(XxlJobAdminConfig.getAdminConfig().getMailPort());
        email.setSSLOnConnect(true);
      } else {
        email.setSmtpPort(Integer.valueOf(XxlJobAdminConfig.getAdminConfig().getMailPort()));
      }
=======
      int port = Integer.valueOf(XxlJobAdminConfig.getAdminConfig().getMailPort());
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/c350052db6b1cc501dc09abc913df9113e6f2f5e/xxl-job-admin/src/main/java/com/xxl/job/admin/core/util/MailUtil.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
      email.setSslSmtpPort(port + "");
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/c350052db6b1cc501dc09abc913df9113e6f2f5e/xxl-job-admin/src/main/java/com/xxl/job/admin/core/util/MailUtil.java/right.java

      email.setAuthenticator(new DefaultAuthenticator(XxlJobAdminConfig.getAdminConfig().getMailUsername(), XxlJobAdminConfig.getAdminConfig().getMailPassword()));
      email.setCharset("UTF-8");
      email.setFrom(XxlJobAdminConfig.getAdminConfig().getMailUsername(), XxlJobAdminConfig.getAdminConfig().getMailSendNick());
      email.addTo(toAddress);
      email.setSubject(mailSubject);
      email.setMsg(mailBody);
      email.setSocketTimeout(180000);
      email.setSocketConnectionTimeout(180000);
      email.send();
      return true;
    } catch (EmailException e) {
      logger.error(e.getMessage(), e);
    }
    return false;
  }
}