package thymeleafexamples.springmail.service;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service public class EmailService {
  private static final String BACKGROUND_IMAGE = "mail/images/background.png";

  private static final String LOGO_BACKGROUND_IMAGE = "mail/images/logo-background.png";

  private static final String THYMELEAF_BANNER_IMAGE = "mail/images/thymeleaf-banner.png";

  private static final String THYMELEAF_LOGO_IMAGE = "mail/images/thymeleaf-logo.png";

  private static final String PNG_MIME = "image/png";

  @Autowired private JavaMailSender mailSender;

  @Autowired private TemplateEngine htmlTemplateEngine;


<<<<<<< /usr/src/app/output/thymeleaf/thymeleafexamples-springmail/158fb6406b10311f114054c66f6206dc7e915917/src/main/java/thymeleafexamples/springmail/service/EmailService.java/left.java
  @Autowired private TemplateEngine textTemplateEngine;
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Autowired private TemplateEngine stringTemplateEngine;

  public void sendTextMail(final String recipientName, final String recipientEmail, final Locale locale) throws MessagingException {
    final Context ctx = new Context(locale);
    ctx.setVariable("name", recipientName);
    ctx.setVariable("subscriptionDate", new Date());
    ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
    message.setSubject("Example plain TEXT email");
    message.setFrom("thymeleaf@example.com");
    message.setTo(recipientEmail);
    final String context = this.textTemplateEngine.process("email-text", ctx);
    message.setText(context);
    this.mailSender.send(mimeMessage);
  }

  public void sendSimpleMail(final String recipientName, final String recipientEmail, final Locale locale) throws MessagingException {
    final Context ctx = new Context(locale);
    ctx.setVariable("name", recipientName);
    ctx.setVariable("subscriptionDate", new Date());
    ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
    message.setSubject("Example HTML email (simple)");
    message.setFrom("thymeleaf@example.com");
    message.setTo(recipientEmail);
    final String htmlContent = this.htmlTemplateEngine.process("email-simple", ctx);
    message.setText(htmlContent, true);
    this.mailSender.send(mimeMessage);
  }

  public void sendMailWithAttachment(final String recipientName, final String recipientEmail, final String attachmentFileName, final byte[] attachmentBytes, final String attachmentContentType, final Locale locale) throws MessagingException {
    final Context ctx = new Context(locale);
    ctx.setVariable("name", recipientName);
    ctx.setVariable("subscriptionDate", new Date());
    ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    message.setSubject("Example HTML email with attachment");
    message.setFrom("thymeleaf@example.com");
    message.setTo(recipientEmail);
    final String htmlContent = this.htmlTemplateEngine.process("email-withattachment", ctx);
    message.setText(htmlContent, true);
    final InputStreamSource attachmentSource = new ByteArrayResource(attachmentBytes);
    message.addAttachment(attachmentFileName, attachmentSource, attachmentContentType);
    this.mailSender.send(mimeMessage);
  }

  public void sendMailWithInline(final String recipientName, final String recipientEmail, final String imageResourceName, final byte[] imageBytes, final String imageContentType, final Locale locale) throws MessagingException {
    final Context ctx = new Context(locale);
    ctx.setVariable("name", recipientName);
    ctx.setVariable("subscriptionDate", new Date());
    ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
    ctx.setVariable("imageResourceName", imageResourceName);
    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    message.setSubject("Example HTML email with inline image");
    message.setFrom("thymeleaf@example.com");
    message.setTo(recipientEmail);
    final String htmlContent = this.htmlTemplateEngine.process("email-inlineimage", ctx);
    message.setText(htmlContent, true);
    final InputStreamSource imageSource = new ByteArrayResource(imageBytes);
    message.addInline(imageResourceName, imageSource, imageContentType);
    this.mailSender.send(mimeMessage);
  }

  public void sendEditableMail(final String recipientName, final String recipientEmail, final String htmlContent, final Locale locale) throws MessagingException {
    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    message.setSubject("Example editable HTML email");
    message.setFrom("thymeleaf@example.com");
    message.setTo(recipientEmail);
    final Context ctx = new Context(locale);
    ctx.setVariable("name", recipientName);
    ctx.setVariable("subscriptionDate", new Date());
    ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
    final String output = 
<<<<<<< /usr/src/app/output/thymeleaf/thymeleafexamples-springmail/158fb6406b10311f114054c66f6206dc7e915917/src/main/java/thymeleafexamples/springmail/service/EmailService.java/left.java
    stringTemplateEngine.process(htmlContent, ctx)
=======
    this.templateEngine.process(htmlContent, ctx)
>>>>>>> /usr/src/app/output/thymeleaf/thymeleafexamples-springmail/158fb6406b10311f114054c66f6206dc7e915917/src/main/java/thymeleafexamples/springmail/service/EmailService.java/right.java
    ;
    message.setText(output, true);
    message.addInline("background", new ClassPathResource(BACKGROUND_IMAGE), PNG_MIME);
    message.addInline("logo-background", new ClassPathResource(LOGO_BACKGROUND_IMAGE), PNG_MIME);
    message.addInline("thymeleaf-banner", new ClassPathResource(THYMELEAF_BANNER_IMAGE), PNG_MIME);
    message.addInline("thymeleaf-logo", new ClassPathResource(THYMELEAF_LOGO_IMAGE), PNG_MIME);
    this.mailSender.send(mimeMessage);
  }
}