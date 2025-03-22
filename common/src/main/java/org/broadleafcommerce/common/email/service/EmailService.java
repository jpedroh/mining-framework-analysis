package org.broadleafcommerce.common.email.service;
import org.broadleafcommerce.common.email.domain.EmailTarget;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import java.util.Map;

/**
 * @author jfischer
 *
 * @deprecated in favor of {@link org.broadleafcommerce.common.notification.service.NotificationDispatcher}
 */
@Deprecated public interface EmailService {
  public boolean sendTemplateEmail(String emailAddress, EmailInfo emailInfo, Map<String, Object> props);

  public boolean sendTemplateEmail(EmailTarget emailTarget, EmailInfo emailInfo, Map<String, Object> props);

  public boolean sendBasicEmail(EmailInfo emailInfo, EmailTarget emailTarget, Map<String, Object> props);
}