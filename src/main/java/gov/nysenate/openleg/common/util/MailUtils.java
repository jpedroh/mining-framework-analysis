package gov.nysenate.openleg.common.util;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PreDestroy;
import javax.mail.*;
import java.util.List;
import java.util.Properties;

/**
 * Contains methods that can be used to interact with mail servers
 */
@Service public class MailUtils {
  private static final Logger logger = LoggerFactory.getLogger(MailUtils.class);

  private final String smtpUser, smtpPass;

  private final Properties mailProperties;

  private final OpenLegEnvironment environment;

  private Store store;

  private Folder sourceFolder, archiveFolder, partialFolder;

  @Autowired public MailUtils(EventBus eventBus, @Value(value = "${mail.smtp.host}") String host, @Value(value = "${mail.smtp.port}") String port, @Value(value = "${mail.smtp.auth:false}") boolean auth, @Value(value = "${mail.smtp.user:}") String smtpUser, @Value(value = "${mail.smtp.password:}") String smtpPass, @Value(value = "${mail.debug:false}") boolean debug, @Value(value = "${mail.smtp.starttls.enable:false}") boolean stlsEnable, @Value(value = "${mail.smtp.ssl.enable:true}") boolean sslEnable, @Value(value = "${mail.smtp.ssl.protocols:TLSv1.3}") String sslProtocol, @Value(value = "${mail.store.protocol:imaps}") String storeProtocol, @Value(value = "${mail.imaps.ssl.protocols:TLSv1.3}") String imapsSSLProtocol, @Value(value = "${mail.smtp.connectiontimeout:5000}") String connTimeout, @Value(value = "${mail.smtp.timeout:5000}") String smtpTimeout, @Value(value = "${mail.smtp.writetimeout:5000}") String writeTimeout, OpenLegEnvironment environment) {
    this.eventBus = eventBus;
    this.smtpUser = smtpUser;
    this.smtpPass = smtpPass;
    this.mailProperties = new Properties();
    mailProperties.put("mail.smtp.host", host);
    mailProperties.put("mail.smtp.port", port);
    mailProperties.put("mail.smtp.auth", auth);
    mailProperties.put("mail.smtp.pass", smtpPass);
    mailProperties.put("mail.smtp.starttls.enable", stlsEnable);
    mailProperties.put("mail.smtp.ssl.enable", sslEnable);
    mailProperties.put("mail.smtp.ssl.protocols", sslProtocol);
    mailProperties.put("mail.smtp.user", smtpUser);
    if (!environment.getEmailFromAddress().isBlank()) {
      mailProperties.put("mail.smtp.from", environment.getEmailFromAddress());
    }
    mailProperties.put("mail.debug", debug);
    mailProperties.put("mail.store.protocol", storeProtocol);
    mailProperties.put("mail.imaps.ssl.protocols", imapsSSLProtocol);
    mailProperties.put("mail.smtp.connectiontimeout", connTimeout);
    mailProperties.put("mail.smtp.timeout", smtpTimeout);
    mailProperties.put("mail.smtp.writetimeout", writeTimeout);
    this.environment = environment;
  }

  /**
     * Creates a new Store and new Folders. Ensures we can reconnect if the connection fails.
     */
  public void createCheckMailConnection() throws MessagingException {
    if (
<<<<<<< /usr/src/app/output/nysenate/openlegislation/55f02d1256aabf0841995fe75f1beb820ed4c6bc/src/main/java/gov/nysenate/openleg/common/util/MailUtils.java/left.java
    store != null
=======
    this.store != null && this.store.isConnected()
>>>>>>> /usr/src/app/output/nysenate/openlegislation/55f02d1256aabf0841995fe75f1beb820ed4c6bc/src/main/java/gov/nysenate/openleg/common/util/MailUtils.java/right.java
    ) {
      return;
    }
    try {
      store = getStore();
      this.sourceFolder = navigateToFolder(environment.getEmailReceivingFolder(), store);
      this.archiveFolder = navigateToFolder(environment.getEmailProcessedFolder(), store);
      this.partialFolder = navigateToFolder(environment.getEmailPartialDaybreakFolder(), store);
      if (sourceFolder != null) {
        sourceFolder.open(Folder.READ_WRITE);
      }
    } catch (MessagingException ex) {

<<<<<<< /usr/src/app/output/nysenate/openlegislation/55f02d1256aabf0841995fe75f1beb820ed4c6bc/src/main/java/gov/nysenate/openleg/common/util/MailUtils.java/left.java
      eventBus.post(new Notification(PROCESS_WARNING, LocalDateTime.now(), "Can\'t connect to checkMail.", ex.getMessage()));
=======
      destroy();
>>>>>>> /usr/src/app/output/nysenate/openlegislation/55f02d1256aabf0841995fe75f1beb820ed4c6bc/src/main/java/gov/nysenate/openleg/common/util/MailUtils.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
      if (environment.isCheckmailEnabled()) {
        logger.info("Unable to connect to email account: " + environment.getEmailHost(), ex);
      }
>>>>>>> /usr/src/app/output/nysenate/openlegislation/55f02d1256aabf0841995fe75f1beb820ed4c6bc/src/main/java/gov/nysenate/openleg/common/util/MailUtils.java/right.java
    }
  }

  @PreDestroy private void destroy() {
    try {
      if (store != null) {
        store.close();
      }
    } catch (MessagingException ignored) {
    }
  }

  public Message[] getIncomingMessages() throws MessagingException {
    try {
      return sourceFolder == null || Thread.currentThread().isInterrupted() ? new Message[0] : sourceFolder.getMessages();
    } catch (MessagingException ex) {
      destroy();
      throw ex;
    }
  }

  /**
     * Moves messages from the source folder to the partial or archive folder,
     * then deletes the emails from the source folder.
     */
  public void moveMessages(List<Message> messages, boolean toArchive) throws MessagingException {
    if (sourceFolder == null) {
      return;
    }
    try {
      sourceFolder.copyMessages(messages.toArray(new Message[0]), toArchive ? archiveFolder : partialFolder);
      for (Message message : messages) {
        message.setFlag(Flags.Flag.DELETED, true);
      }
      sourceFolder.expunge();
    } catch (MessagingException ex) {
      destroy();
      throw ex;
    }
  }

  /**
     * Gets an authenticated smtp mail session
     * @return Session
     */
  public Session getSmtpSession() {
    var auth = new Authenticator() {
      private final PasswordAuthentication pa = new PasswordAuthentication(smtpUser, smtpPass);

      @Override protected PasswordAuthentication getPasswordAuthentication() {
        return pa;
      }
    };
    return Session.getInstance(mailProperties, auth);
  }

  /**
     * Establishes a connection to a mail server and returns the resulting connection object.
     * The store must be closed on its own.
     *
     * @return Store
     * @throws MessagingException if a connection cannot be established
     */
  private Store getStore() throws MessagingException {
    if (Thread.currentThread().isInterrupted()) {
      return null;
    }
    Store store = Session.getInstance(mailProperties).getStore();
    try {
      store.connect(environment.getEmailHost(), environment.getEmailUser(), environment.getEmailPass());
    } catch (MessagingException ex) {
      store.close();
      throw ex;
    }
    return store;
  }

  /**
     * Navigates through the given mail store to get the folder specified by the given path
     *
     * @param path  The path to navigate to
     * @param store The mail store to navigate through
     * @return Folder - the resulting folder
     * @throws MessagingException If the folder cannot be found
     */
  private static Folder navigateToFolder(String path, Store store) throws MessagingException {
    if (store == null) {
      return null;
    }
    String[] splitPath = path.split("/");
    Folder folder = store.getFolder(splitPath[0]);
    for (int i = 1; i < splitPath.length; i++) {
      folder = folder.getFolder(splitPath[i]);
    }
    return folder;
  }
}