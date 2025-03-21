package hudson.plugins.emailext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlNumberInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import hudson.FilePath;
import hudson.Functions;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.User;
import hudson.plugins.emailext.plugins.trigger.AbortedTrigger;
import hudson.plugins.emailext.plugins.trigger.AlwaysTrigger;
import hudson.plugins.emailext.plugins.trigger.BuildingTrigger;
import hudson.plugins.emailext.plugins.trigger.FirstFailureTrigger;
import hudson.plugins.emailext.plugins.trigger.FirstUnstableTrigger;
import hudson.plugins.emailext.plugins.trigger.FixedTrigger;
import hudson.plugins.emailext.plugins.trigger.FixedUnhealthyTrigger;
import hudson.plugins.emailext.plugins.trigger.ImprovementTrigger;
import hudson.plugins.emailext.plugins.trigger.NotBuiltTrigger;
import hudson.plugins.emailext.plugins.trigger.PreBuildScriptTrigger;
import hudson.plugins.emailext.plugins.trigger.PreBuildTrigger;
import hudson.plugins.emailext.plugins.trigger.RegressionTrigger;
import hudson.plugins.emailext.plugins.trigger.ScriptTrigger;
import hudson.plugins.emailext.plugins.trigger.SecondFailureTrigger;
import hudson.plugins.emailext.plugins.trigger.StatusChangedTrigger;
import hudson.plugins.emailext.plugins.trigger.StillFailingTrigger;
import hudson.plugins.emailext.plugins.trigger.StillUnstableTrigger;
import hudson.plugins.emailext.plugins.trigger.SuccessTrigger;
import hudson.plugins.emailext.plugins.trigger.UnstableTrigger;
import hudson.plugins.emailext.plugins.trigger.XNthFailureTrigger;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.util.Secret;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.mail.Authenticator;
import jenkins.model.Jenkins;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.recipes.LocalData;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class ExtendedEmailPublisherDescriptorTest {
  @Rule public JenkinsRule j = new JenkinsRule();

  @Test public void testGlobalConfigDefaultState() throws Exception {
    HtmlPage page = j.createWebClient().goTo("configure");
    assertEquals("Should be at the Configure System page", "Configure System [Jenkins]", page.getTitleText());
    HtmlTextInput smtpHost = page.getElementByName("_.smtpHost");
    assertNotNull("SMTP Server should be present", smtpHost);
    assertEquals("SMTP Server should be blank by default", "", smtpHost.getText());
    HtmlNumberInput smtpPort = page.getElementByName("_.smtpPort");
    assertNotNull("SMTP Port should be present", smtpPort);
    assertEquals("SMTP Port should be 25 by default", "25", smtpPort.getText());
    HtmlTextInput defaultSuffix = page.getElementByName("_.defaultSuffix");
    assertNotNull("Default suffix should be present", defaultSuffix);
    assertEquals("Default suffix should be blank by default", "", defaultSuffix.getText());
    HtmlSelect contentType = page.getElementByName("_.defaultContentType");
    assertNotNull("Content type selection should be present", contentType);
    assertEquals("Plain text should be selected by default", "text/plain", contentType.getSelectedOptions().get(0).getValueAttribute());
    HtmlCheckBoxInput precedenceBulk = page.getElementByName("_.precedenceBulk");
    assertNotNull("Precedence Bulk should be present", precedenceBulk);
    assertFalse("Add precedence bulk should not be checked by default", precedenceBulk.isChecked());
    HtmlTextInput defaultRecipients = page.getElementByName("_.defaultRecipients");
    assertNotNull("Default Recipients should be present", defaultRecipients);
    assertEquals("Default recipients should be blank by default", "", defaultRecipients.getText());
    HtmlTextInput defaultReplyTo = page.getElementByName("_.defaultReplyTo");
    assertNotNull("Default Reply-to should be present", defaultReplyTo);
    assertEquals("Default Reply-To should be blank by default", "", defaultReplyTo.getText());
    HtmlTextInput emergencyReroute = page.getElementByName("_.emergencyReroute");
    assertNotNull("Emergency Reroute should be present", emergencyReroute);
    assertEquals("Emergency Reroute should be blank by default", "", emergencyReroute.getText());
    HtmlTextInput allowedDomains = page.getElementByName("_.allowedDomains");
    assertNotNull("Allowed Domains should be present", allowedDomains);
    assertEquals("Allowed Domains should be blank by default", "", allowedDomains.getText());
    HtmlTextInput excludedRecipients = page.getElementByName("_.excludedCommitters");
    assertNotNull("Excluded Recipients should be present", excludedRecipients);
    assertEquals("Excluded Recipients should be blank by default", "", excludedRecipients.getText());
    HtmlTextInput defaultSubject = page.getElementByName("_.defaultSubject");
    assertNotNull("Default Subject should be present", defaultSubject);
    assertEquals("Default Subject should be set", "$PROJECT_NAME - Build # $BUILD_NUMBER - $BUILD_STATUS!", defaultSubject.getText());
    HtmlNumberInput maxAttachmentSize = page.getElementByName("_.maxAttachmentSizeMb");
    assertNotNull("Max attachment size should be present", maxAttachmentSize);
    assertThat("Max attachment size should be blank or -1 by default", maxAttachmentSize.getText(), is(oneOf("", "-1")));
    HtmlTextArea defaultContent = page.getElementByName("_.defaultBody");
    assertNotNull("Default content should be present", defaultContent);
    assertEquals("Default content should be set by default", "$PROJECT_NAME - Build # $BUILD_NUMBER - $BUILD_STATUS:\n\nCheck console output at $BUILD_URL to view the results.", defaultContent.getText());
    HtmlTextArea defaultPresendScript = page.getElementByName("_.defaultPresendScript");
    assertNotNull("Default presend script should be present", defaultPresendScript);
    assertEquals("Default presend script should be blank by default", "", defaultPresendScript.getText());
    HtmlTextArea defaultPostsendScript = page.getElementByName("_.defaultPostsendScript");
    assertNotNull("Default postsend script should be present", defaultPostsendScript);
    assertEquals("Default postsend script should be blank by default", "", defaultPostsendScript.getText());
    HtmlCheckBoxInput debugMode = page.getElementByName("_.debugMode");
    assertNotNull("Debug mode should be present", debugMode);
    assertFalse("Debug mode should not be checked by default", debugMode.isChecked());
    HtmlCheckBoxInput adminRequiredForTemplateTesting = page.getElementByName("_.adminRequiredForTemplateTesting");
    assertNotNull("Admin required for template testing should be present", adminRequiredForTemplateTesting);
    assertFalse("Admin required for template testing should be unchecked by default", adminRequiredForTemplateTesting.isChecked());
    HtmlCheckBoxInput watchingEnabled = page.getElementByName("_.watchingEnabled");
    assertNotNull("Watching enable should be present", watchingEnabled);
    assertFalse("Watching enable should be unchecked by default", watchingEnabled.isChecked());
    HtmlCheckBoxInput allowUnregisteredEnabled = page.getElementByName("_.allowUnregisteredEnabled");
    assertNotNull("Allow unregistered should be present", allowUnregisteredEnabled);
    assertFalse("Allow unregistered should be unchecked by default", allowUnregisteredEnabled.isChecked());
    assertThrows("defaultClasspath section should not be present", ElementNotFoundException.class, () -> page.getElementByName("defaultClasspath"));
  }

  @Test @Issue(value = "JENKINS-20030") public void testGlobalConfigSimpleRoundTrip() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    HtmlPage page = j.createWebClient().goTo("configure");
    HtmlTextInput defaultRecipients = page.getElementByName("_.defaultRecipients");
    defaultRecipients.setValueAttribute("mickey@disney.com");
    j.submit(page.getFormByName("config"));
    assertEquals("mickey@disney.com", descriptor.getDefaultRecipients());
  }

  @Test @Issue(value = "JENKINS-63367") public void testSmtpPortRetainsSetValue() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    JenkinsRule.WebClient client = j.createWebClient();
    HtmlPage page = client.goTo("configure");
    HtmlNumberInput smtpPort = page.getElementByName("_.smtpPort");
    smtpPort.setValueAttribute("587");
    j.submit(page.getFormByName("config"));
    assertEquals("587", descriptor.getMailAccount().getSmtpPort());
    page = client.goTo("configure");
    smtpPort = page.getElementByName("_.smtpPort");
    assertEquals("587", smtpPort.getValueAttribute());
  }

  @Test @Issue(value = "JENKINS-20133") public void testPrecedenceBulkSettingRoundTrip() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    HtmlPage page = j.createWebClient().goTo("configure");
    HtmlCheckBoxInput addPrecedenceBulk = page.getElementByName("_.precedenceBulk");
    addPrecedenceBulk.setChecked(true);
    j.submit(page.getFormByName("config"));
    assertTrue(descriptor.getPrecedenceBulk());
  }

  @Test @Issue(value = "JENKINS-20133") public void testListIDRoundTrip() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    HtmlPage page = j.createWebClient().goTo("configure");
    HtmlTextInput listId = page.getElementByName("_.listId");
    listId.setValueAttribute("hammer");
    j.submit(page.getFormByName("config"));
    assertEquals("hammer", descriptor.getListId());
  }

  @Test public void testAdvancedProperties() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    HtmlPage page = j.createWebClient().goTo("configure");
    HtmlTextArea advProperties = page.getElementByName("_.advProperties");
    advProperties.setText("mail.smtp.starttls.enable=true");
    j.submit(page.getFormByName("config"));
    assertEquals("mail.smtp.starttls.enable=true", descriptor.getMailAccount().getAdvProperties());
  }

  @Test public void defaultTriggers() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    HtmlPage page = j.createWebClient().goTo("configure");
    assertEquals("Should be at the Configure System page", "Configure System [Jenkins]", page.getTitleText());
    List<DomElement> settings = page.getByXPath(".//div[@class=\'advancedLink\' and span[starts-with(@id, \'yui-gen\')]/span[@class=\'first-child\']/button[./text()=\'Default Triggers...\']]");
    assertEquals(1, settings.size());
    DomNode div = settings.get(0);
    DomNode advancedBody = div.getNextSibling();
    assertEquals("div", advancedBody.getLocalName());
    DomNode tbody = advancedBody.getFirstChild();
    assertEquals("div", tbody.getLocalName());
    assertFalse(tbody.getChildNodes().isEmpty());
    List<DomNode> nodes = div.getByXPath(".//button[./text()=\'Default Triggers...\']");
    assertEquals(1, nodes.size());
    HtmlButton defaultTriggers = (HtmlButton) nodes.get(0);
    defaultTriggers.click();
    String[] selectedTriggers = { "hudson.plugins.emailext.plugins.trigger.AbortedTrigger", "hudson.plugins.emailext.plugins.trigger.PreBuildTrigger", "hudson.plugins.emailext.plugins.trigger.FixedTrigger", "hudson.plugins.emailext.plugins.trigger.RegressionTrigger" };
    List<DomNode> failureTrigger = page.getByXPath(".//input[@json=\'hudson.plugins.emailext.plugins.trigger.FailureTrigger\']");
    assertEquals(1, failureTrigger.size());
    HtmlCheckBoxInput failureTriggerCheckBox = (HtmlCheckBoxInput) failureTrigger.get(0);
    assertTrue(failureTriggerCheckBox.isChecked());
    failureTriggerCheckBox.setChecked(false);
    for (String selectedTrigger : selectedTriggers) {
      List<DomNode> triggerItems = page.getByXPath(".//input[@name=\'_.defaultTriggerIds\' and @json=\'" + selectedTrigger + "\']");
      assertEquals(1, triggerItems.size());
      HtmlCheckBoxInput checkBox = (HtmlCheckBoxInput) triggerItems.get(0);
      checkBox.setChecked(true);
    }
    j.submit(page.getFormByName("config"));
    assertArrayEquals(selectedTriggers, descriptor.getDefaultTriggerIds().toArray(new String[0]));
  }

  @Test public void groovyClassPath() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    HtmlPage page = j.createWebClient().goTo("configure");
    assertEquals("Should be at the Configure System page", "Configure System [Jenkins]", page.getTitleText());
    List<DomElement> nodes = page.getByXPath(".//div[contains(@class, \'setting-name\') and ./text()=\'Additional groovy classpath\'] | .//div[contains(@class, \'jenkins-form-label\') and ./text()=\'Additional groovy classpath\']");
    assertEquals(1, nodes.size());
    HtmlDivision settingName = (HtmlDivision) nodes.get(0);
    nodes = settingName.getByXPath("../div[contains(@class, \'setting-name\')]/div[@class=\'repeated-container\']/div[@name=\'defaultClasspath\']");
    assertEquals("Should not have any class path setup by default", 0, nodes.size());
    nodes = settingName.getByXPath("../div[@class=\'setting-main\']/div[@class=\'repeated-container\' and span[starts-with(@id, \'yui-gen\')]/span[@class=\'first-child\']/button[./text()=\'Add\']]");
    assertEquals(1, nodes.size());
    HtmlDivision div = (HtmlDivision) nodes.get(0);
    nodes = div.getByXPath(".//button[./text()=\'Add\']");
    HtmlButton addButton = (HtmlButton) nodes.get(0);
    addButton.click();
    nodes = settingName.getByXPath("../div[@class=\'setting-main\']/div[@class=\'repeated-container\']/div[@name=\'defaultClasspath\']");
    assertEquals(1, nodes.size());
    div = (HtmlDivision) nodes.get(0);
    String divClass = div.getAttribute("class");
    assertTrue(divClass.contains("first") && divClass.contains("last") && divClass.contains("only"));
    nodes = div.getByXPath(".//input[@name=\'_.path\' and @type=\'text\']");
    assertEquals(1, nodes.size());
    HtmlTextInput path = (HtmlTextInput) nodes.get(0);
    path.setText("/path/to/classes");
    addButton.click();
    nodes = settingName.getByXPath("../div[@class=\'setting-main\']/div[@class=\'repeated-container\']/div[@name=\'defaultClasspath\' and contains(@class, \'last\')]");
    assertEquals(1, nodes.size());
    div = (HtmlDivision) nodes.get(0);
    divClass = div.getAttribute("class");
    assertTrue(divClass.contains("last"));
    assertFalse(divClass.contains("first") || divClass.contains("only"));
    nodes = div.getByXPath(".//input[@name=\'_.path\' and @type=\'text\']");
    assertEquals(1, nodes.size());
    path = (HtmlTextInput) nodes.get(0);
    path.setText("/other/path/to/classes");
    j.submit(page.getFormByName("config"));
    String[] classpath = { "/path/to/classes", "/other/path/to/classes" };
    assertArrayEquals(classpath, descriptor.getDefaultClasspath().stream().map(GroovyScriptPath::getPath).toArray(String[]::new));
  }

  @Test public void managePermissionShouldAccess() {
    final String USER = "user";
    final String MANAGER = "manager";
    j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
    j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(Jenkins.READ).everywhere().to(USER).grant(Jenkins.READ).everywhere().to(MANAGER).grant(Jenkins.MANAGE).everywhere().to(MANAGER));
    try (ACLContext c = ACL.as(User.getById(USER, true))) {
      Collection<Descriptor> descriptors = Functions.getSortedDescriptorsForGlobalConfigUnclassified();
      assertEquals("Global configuration should not be accessible to READ users", 0, descriptors.size());
    }
    try (ACLContext c = ACL.as(User.getById(MANAGER, true))) {
      Collection<Descriptor> descriptors = Functions.getSortedDescriptorsForGlobalConfigUnclassified();
      Optional<Descriptor> found = descriptors.stream().filter((descriptor) -> descriptor instanceof ExtendedEmailPublisherDescriptor).findFirst();
      assertTrue("Global configuration should be accessible to MANAGE users", found.isPresent());
    }
  }

  @Test public void noAuthenticatorIsCreatedWhenCredentialsIsBlank() {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    String from = "test@example.com";
    MailAccount ma = new MailAccount();
    ma.setAddress(from);
    ma.setSmtpHost("smtp.example.com");
    ma.setSmtpPort("25");
    ma.setCredentialsId(null);
    ExtendedEmailPublisher publisher = Mockito.mock(ExtendedEmailPublisher.class);
    Run<?, ?> run = Mockito.mock(Run.class);
    FilePath workspace = Mockito.mock(FilePath.class);
    Launcher launcher = Mockito.mock(Launcher.class);
    TaskListener listener = Mockito.mock(TaskListener.class);
    ExtendedEmailPublisherContext context = new ExtendedEmailPublisherContext(publisher, run, workspace, launcher, listener);
    descriptor.setAddAccounts(Collections.singletonList(ma));
    BiFunction<MailAccount, Run<?, ?>, Authenticator> authenticatorProvider = Mockito.mock(BiFunction.class);
    descriptor.setAuthenticatorProvider(authenticatorProvider);
    descriptor.createSession(ma, context);
    ArgumentCaptor<MailAccount> mailAccountCaptor = ArgumentCaptor.forClass(MailAccount.class);
    ArgumentCaptor<Run<?, ?>> runCaptor = ArgumentCaptor.forClass(Run.class);
    Mockito.verify(authenticatorProvider, Mockito.never()).apply(mailAccountCaptor.capture(), runCaptor.capture());
  }

  @Test public void testFixEmptyAndTrimNormal() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    UsernamePasswordCredentials c = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, "email-ext", "Username/password for SMTP", "smtpUsername", "password");
    CredentialsProvider.lookupStores(j.jenkins).iterator().next().addCredentials(Domain.global(), c);
    MailAccount ma = new MailAccount();
    ma.setAddress("example@example.com");
    ma.setSmtpHost("smtp.example.com");
    ma.setSmtpPort("25");
    ma.setCredentialsId("email-ext");
    descriptor.setMailAccount(ma);
    descriptor.setDefaultSuffix("@example.com");
    descriptor.setCharset("UTF-8");
    descriptor.setEmergencyReroute("emergency@example.com");
    j.submit(j.createWebClient().goTo("configure").getFormByName("config"));
    assertEquals("example@example.com", descriptor.getMailAccount().getAddress());
    assertEquals("smtp.example.com", descriptor.getMailAccount().getSmtpHost());
    assertEquals("25", descriptor.getMailAccount().getSmtpPort());
    assertEquals("email-ext", descriptor.getMailAccount().getCredentialsId());
    assertEquals("@example.com", descriptor.getDefaultSuffix());
    assertEquals("UTF-8", descriptor.getCharset());
    assertEquals("emergency@example.com", descriptor.getEmergencyReroute());
  }

  @Test public void testFixEmptyAndTrimExtraSpaces() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    UsernamePasswordCredentials c = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, "email-ext", "Username/password for SMTP", "smtpUsername", "password");
    CredentialsProvider.lookupStores(j.jenkins).iterator().next().addCredentials(Domain.global(), c);
    MailAccount ma = new MailAccount();
    ma.setAddress("       example@example.com      ");
    ma.setSmtpHost("      smtp.example.com      ");
    ma.setSmtpPort("      25      ");
    ma.setCredentialsId("     email-ext     ");
    descriptor.setMailAccount(ma);
    descriptor.setDefaultSuffix("      @example.com      ");
    descriptor.setCharset("      UTF-8      ");
    descriptor.setEmergencyReroute("      emergency@example.com      ");
    j.submit(j.createWebClient().goTo("configure").getFormByName("config"));
    assertEquals("example@example.com", descriptor.getMailAccount().getAddress());
    assertEquals("smtp.example.com", descriptor.getMailAccount().getSmtpHost());
    assertEquals("25", descriptor.getMailAccount().getSmtpPort());
    assertEquals("email-ext", descriptor.getMailAccount().getCredentialsId());
    assertEquals("@example.com", descriptor.getDefaultSuffix());
    assertEquals("UTF-8", descriptor.getCharset());
    assertEquals("emergency@example.com", descriptor.getEmergencyReroute());
  }

  @Test public void testFixEmptyAndTrimEmptyString() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    MailAccount ma = new MailAccount();
    ma.setAddress("");
    ma.setSmtpHost("");
    ma.setSmtpPort("");
    ma.setCredentialsId("");
    descriptor.setMailAccount(ma);
    descriptor.setDefaultSuffix("");
    descriptor.setCharset("");
    descriptor.setEmergencyReroute("");
    j.submit(j.createWebClient().goTo("configure").getFormByName("config"));
    assertNull(descriptor.getMailAccount().getAddress());
    assertNull(descriptor.getMailAccount().getSmtpHost());
    assertEquals("25", descriptor.getMailAccount().getSmtpPort());
    assertNull(descriptor.getMailAccount().getCredentialsId());
    assertNull(descriptor.getDefaultSuffix());
    assertEquals("UTF-8", descriptor.getCharset());
    assertEquals("", descriptor.getEmergencyReroute());
  }

  @Test public void testFixEmptyAndTrimNull() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    MailAccount ma = new MailAccount();
    ma.setAddress(null);
    ma.setSmtpHost(null);
    ma.setSmtpPort(null);
    ma.setCredentialsId(null);
    descriptor.setMailAccount(ma);
    descriptor.setDefaultSuffix(null);
    descriptor.setCharset(null);
    descriptor.setEmergencyReroute(null);
    j.submit(j.createWebClient().goTo("configure").getFormByName("config"));
    assertNull(descriptor.getMailAccount().getAddress());
    assertNull(descriptor.getMailAccount().getSmtpHost());
    assertEquals("25", descriptor.getMailAccount().getSmtpPort());
    assertNull(descriptor.getMailAccount().getCredentialsId());
    assertNull(descriptor.getDefaultSuffix());
    assertEquals("UTF-8", descriptor.getCharset());
    assertEquals("", descriptor.getEmergencyReroute());
  }

  @LocalData @Test public void persistedConfigurationBeforeJCasC() {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    assertEquals("admin@example.com", descriptor.getAdminAddress());
    assertEquals("smtp.example.com", descriptor.getMailAccount().getSmtpHost());
    assertEquals("@example.com", descriptor.getDefaultSuffix());
    assertNotNull(descriptor.getMailAccount().getCredentialsId());
    List<StandardCredentials> creds = CredentialsProvider.lookupCredentials(StandardCredentials.class);
    assertEquals(2, creds.size());
    for (StandardCredentials c : creds) {
      assertEquals(UsernamePasswordCredentialsImpl.class, c.getClass());
      assertEquals("Migrated from email-ext username/password", c.getDescription());
    }
    assertEquals("mail.smtp.ssl.trust=example.com", descriptor.getMailAccount().getAdvProperties());
    assertTrue(descriptor.getMailAccount().isUseSsl());
    assertTrue(descriptor.getMailAccount().isDefaultAccount());
    assertEquals("2525", descriptor.getMailAccount().getSmtpPort());
    assertEquals("UTF-8", descriptor.getCharset());
    assertEquals(1, descriptor.getAddAccounts().size());
    MailAccount additionalAccount = descriptor.getAddAccounts().get(0);
    assertEquals("admin@example2.com", additionalAccount.getAddress());
    assertEquals("smtp.example2.com", additionalAccount.getSmtpHost());
    assertEquals("2626", additionalAccount.getSmtpPort());
    assertNotNull(additionalAccount.getCredentialsId());
    assertTrue(additionalAccount.isUseSsl());
    assertFalse(additionalAccount.isDefaultAccount());
    assertEquals("mail.smtp.ssl.trust=example2.com", additionalAccount.getAdvProperties());
    assertEquals("text/html", descriptor.getDefaultContentType());
    assertEquals("<list.example.com>", descriptor.getListId());
    assertTrue(descriptor.getPrecedenceBulk());
    assertEquals("default@example.com", descriptor.getDefaultRecipients());
    assertEquals("noreply@example.com", descriptor.getDefaultReplyTo());
    assertEquals("emergency@example.com", descriptor.getEmergencyReroute());
    assertEquals("@example.com", descriptor.getAllowedDomains());
    assertEquals("excluded@example.com", descriptor.getExcludedCommitters());
    assertEquals("$PROJECT_NAME - Build #$BUILD_NUMBER - $BUILD_STATUS", descriptor.getDefaultSubject());
    assertEquals(44040192, descriptor.getMaxAttachmentSize());
    assertEquals("$PROJECT_NAME - Build # $BUILD_NUMBER - $BUILD_STATUS", descriptor.getDefaultBody());
    assertEquals("build.previousBuild.result.toString().equals(\'FAILURE\')", descriptor.getDefaultPresendScript());
    assertEquals("build.result.toString().equals(\'FAILURE\')", descriptor.getDefaultPostsendScript());
    assertEquals(1, descriptor.getDefaultClasspath().size());
    assertEquals("classes", descriptor.getDefaultClasspath().get(0).getPath());
    assertTrue(descriptor.isDebugMode());
    assertTrue(descriptor.isAdminRequiredForTemplateTesting());
    assertTrue(descriptor.isWatchingEnabled());
    assertTrue(descriptor.isAllowUnregisteredEnabled());
    assertEquals(20, descriptor.getDefaultTriggerIds().size());
    assertThat(descriptor.getDefaultTriggerIds(), containsInAnyOrder(AbortedTrigger.class.getName(), AlwaysTrigger.class.getName(), BuildingTrigger.class.getName(), FirstFailureTrigger.class.getName(), FirstUnstableTrigger.class.getName(), FixedTrigger.class.getName(), FixedUnhealthyTrigger.class.getName(), ImprovementTrigger.class.getName(), NotBuiltTrigger.class.getName(), PreBuildScriptTrigger.class.getName(), PreBuildTrigger.class.getName(), RegressionTrigger.class.getName(), ScriptTrigger.class.getName(), SecondFailureTrigger.class.getName(), StatusChangedTrigger.class.getName(), StillFailingTrigger.class.getName(), StillUnstableTrigger.class.getName(), SuccessTrigger.class.getName(), UnstableTrigger.class.getName(), XNthFailureTrigger.class.getName()));
  }

  @Issue(value = "JENKINS-63846") @LocalData @Test public void persistedConfigurationBeforeDefaultAddress() {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    assertEquals("admin@example.com", descriptor.getAdminAddress());
    assertNull(descriptor.getMailAccount().getAddress());
    assertEquals("smtp.example.com", descriptor.getMailAccount().getSmtpHost());
    assertEquals("@example.com", descriptor.getDefaultSuffix());
    assertNotNull(descriptor.getMailAccount().getCredentialsId());
    List<StandardCredentials> creds = CredentialsProvider.lookupCredentials(StandardCredentials.class);
    assertEquals(2, creds.size());
    for (StandardCredentials c : creds) {
      assertEquals(UsernamePasswordCredentialsImpl.class, c.getClass());
      assertEquals("Migrated from email-ext username/password", c.getDescription());
    }
    assertEquals("mail.smtp.ssl.trust=example.com", descriptor.getMailAccount().getAdvProperties());
    assertTrue(descriptor.getMailAccount().isUseSsl());
    assertTrue(descriptor.getMailAccount().isDefaultAccount());
    assertEquals("2525", descriptor.getMailAccount().getSmtpPort());
    assertEquals("UTF-8", descriptor.getCharset());
    assertEquals(1, descriptor.getAddAccounts().size());
    MailAccount additionalAccount = descriptor.getAddAccounts().get(0);
    assertEquals("admin@example2.com", additionalAccount.getAddress());
    assertEquals("smtp.example2.com", additionalAccount.getSmtpHost());
    assertEquals("2626", additionalAccount.getSmtpPort());
    assertNotNull(additionalAccount.getCredentialsId());
    assertTrue(additionalAccount.isUseSsl());
    assertFalse(additionalAccount.isDefaultAccount());
    assertEquals("mail.smtp.ssl.trust=example2.com", additionalAccount.getAdvProperties());
    assertEquals("text/html", descriptor.getDefaultContentType());
    assertEquals("<list.example.com>", descriptor.getListId());
    assertTrue(descriptor.getPrecedenceBulk());
    assertEquals("default@example.com", descriptor.getDefaultRecipients());
    assertEquals("noreply@example.com", descriptor.getDefaultReplyTo());
    assertEquals("emergency@example.com", descriptor.getEmergencyReroute());
    assertEquals("@example.com", descriptor.getAllowedDomains());
    assertEquals("excluded@example.com", descriptor.getExcludedCommitters());
    assertEquals("$PROJECT_NAME - Build #$BUILD_NUMBER - $BUILD_STATUS", descriptor.getDefaultSubject());
    assertEquals(44040192, descriptor.getMaxAttachmentSize());
    assertEquals("$PROJECT_NAME - Build # $BUILD_NUMBER - $BUILD_STATUS", descriptor.getDefaultBody());
    assertEquals("build.previousBuild.result.toString().equals(\'FAILURE\')", descriptor.getDefaultPresendScript());
    assertEquals("build.result.toString().equals(\'FAILURE\')", descriptor.getDefaultPostsendScript());
    assertEquals(1, descriptor.getDefaultClasspath().size());
    assertEquals("classes", descriptor.getDefaultClasspath().get(0).getPath());
    assertTrue(descriptor.isDebugMode());
    assertTrue(descriptor.isAdminRequiredForTemplateTesting());
    assertTrue(descriptor.isWatchingEnabled());
    assertTrue(descriptor.isAllowUnregisteredEnabled());
    assertEquals(20, descriptor.getDefaultTriggerIds().size());
    assertThat(descriptor.getDefaultTriggerIds(), containsInAnyOrder(AbortedTrigger.class.getName(), AlwaysTrigger.class.getName(), BuildingTrigger.class.getName(), FirstFailureTrigger.class.getName(), FirstUnstableTrigger.class.getName(), FixedTrigger.class.getName(), FixedUnhealthyTrigger.class.getName(), ImprovementTrigger.class.getName(), NotBuiltTrigger.class.getName(), PreBuildScriptTrigger.class.getName(), PreBuildTrigger.class.getName(), RegressionTrigger.class.getName(), ScriptTrigger.class.getName(), SecondFailureTrigger.class.getName(), StatusChangedTrigger.class.getName(), StillFailingTrigger.class.getName(), StillUnstableTrigger.class.getName(), SuccessTrigger.class.getName(), UnstableTrigger.class.getName(), XNthFailureTrigger.class.getName()));
  }

  @Test @LocalData public void persistedConfigurationWithCredentialId() throws Exception {
    ExtendedEmailPublisherDescriptor descriptor = j.jenkins.getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
    assertEquals("admin@example.com", descriptor.getAdminAddress());
    assertNull(descriptor.getMailAccount().getAddress());
    assertEquals("smtp.example.com", descriptor.getMailAccount().getSmtpHost());
    assertEquals("@example.com", descriptor.getDefaultSuffix());
    assertEquals("email-ext-admin", descriptor.getMailAccount().getCredentialsId());
    assertEquals("mail.smtp.ssl.trust=example.com", descriptor.getMailAccount().getAdvProperties());
    assertTrue(descriptor.getMailAccount().isUseSsl());
    assertTrue(descriptor.getMailAccount().isDefaultAccount());
    assertEquals("2525", descriptor.getMailAccount().getSmtpPort());
    assertEquals("UTF-8", descriptor.getCharset());
    assertEquals(1, descriptor.getAddAccounts().size());
    MailAccount additionalAccount = descriptor.getAddAccounts().get(0);
    assertEquals("admin@example2.com", additionalAccount.getAddress());
    assertEquals("smtp.example2.com", additionalAccount.getSmtpHost());
    assertEquals("2626", additionalAccount.getSmtpPort());
    assertEquals("email-ext-admin2", additionalAccount.getCredentialsId());
    assertTrue(additionalAccount.isUseSsl());
    assertFalse(additionalAccount.isDefaultAccount());
    assertEquals("mail.smtp.ssl.trust=example2.com", additionalAccount.getAdvProperties());
    assertEquals("text/html", descriptor.getDefaultContentType());
    assertEquals("<list.example.com>", descriptor.getListId());
    assertTrue(descriptor.getPrecedenceBulk());
    assertEquals("default@example.com", descriptor.getDefaultRecipients());
    assertEquals("noreply@example.com", descriptor.getDefaultReplyTo());
    assertEquals("emergency@example.com", descriptor.getEmergencyReroute());
    assertEquals("@example.com", descriptor.getAllowedDomains());
    assertEquals("excluded@example.com", descriptor.getExcludedCommitters());
    assertEquals("$PROJECT_NAME - Build #$BUILD_NUMBER - $BUILD_STATUS", descriptor.getDefaultSubject());
    assertEquals(44040192, descriptor.getMaxAttachmentSize());
    assertEquals("$PROJECT_NAME - Build # $BUILD_NUMBER - $BUILD_STATUS", descriptor.getDefaultBody());
    assertEquals("build.previousBuild.result.toString().equals(\'FAILURE\')", descriptor.getDefaultPresendScript());
    assertEquals("build.result.toString().equals(\'FAILURE\')", descriptor.getDefaultPostsendScript());
    assertEquals(1, descriptor.getDefaultClasspath().size());
    assertEquals("classes", descriptor.getDefaultClasspath().get(0).getPath());
    assertTrue(descriptor.isDebugMode());
    assertTrue(descriptor.isAdminRequiredForTemplateTesting());
    assertTrue(descriptor.isWatchingEnabled());
    assertTrue(descriptor.isAllowUnregisteredEnabled());
    assertEquals(20, descriptor.getDefaultTriggerIds().size());
    assertThat(descriptor.getDefaultTriggerIds(), containsInAnyOrder(AbortedTrigger.class.getName(), AlwaysTrigger.class.getName(), BuildingTrigger.class.getName(), FirstFailureTrigger.class.getName(), FirstUnstableTrigger.class.getName(), FixedTrigger.class.getName(), FixedUnhealthyTrigger.class.getName(), ImprovementTrigger.class.getName(), NotBuiltTrigger.class.getName(), PreBuildScriptTrigger.class.getName(), PreBuildTrigger.class.getName(), RegressionTrigger.class.getName(), ScriptTrigger.class.getName(), SecondFailureTrigger.class.getName(), StatusChangedTrigger.class.getName(), StillFailingTrigger.class.getName(), StillUnstableTrigger.class.getName(), SuccessTrigger.class.getName(), UnstableTrigger.class.getName(), XNthFailureTrigger.class.getName()));
  }
}