package org.sonar.plugins.ldap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.security.ExternalGroupsProvider;
import org.sonar.api.security.ExternalUsersProvider;
import org.sonar.api.security.LoginPasswordAuthenticator;
import org.sonar.api.security.SecurityRealm;
import java.util.Map;

/**
 * @author Evgeny Mandrikov
 */
public class LdapRealm extends SecurityRealm {
  private static final Logger LOG = LoggerFactory.getLogger(LdapRealm.class);

  private LdapUsersProvider usersProvider;

  private LdapGroupsProvider groupsProvider;

  private LdapAuthenticator authenticator;

  private final LdapSettingsManager settingsManager;

  public LdapRealm(Settings settings) {
    settingsManager = new LdapSettingsManager(settings);
  }

  @Override public String getName() {
    return "LDAP";
  }

  /**
	 * Initializes LDAP realm and tests connection.
	 * 
	 * @throws org.sonar.api.utils.SonarException
	 *             if a NamingException was thrown during test
	 */
  @Override public void init() {
    Map<String, LdapContextFactory> contextFactories = settingsManager.getContextFactories();
    Map<String, LdapUserMapping> userMappings = settingsManager.getUserMappings();
    usersProvider = new LdapUsersProvider(contextFactories, userMappings);
    authenticator = new LdapAuthenticator(contextFactories, userMappings);
    Map<String, LdapGroupMapping> groupMappings = settingsManager.getGroupMappings();
    if (groupMappings.size() == 0) {
      LOG.info("Groups will not be synchronized, because property \'ldap.group.baseDn\' is empty for every ldap exampleServer.");
    } else {
      LOG.info("{}", groupMappings);
      groupsProvider = new LdapGroupsProvider(contextFactories, userMappings, groupMappings);
    }
    for (LdapContextFactory contextFactory : contextFactories.values()) {
      contextFactory.testConnection();
    }
  }

  @Override public LoginPasswordAuthenticator getLoginPasswordAuthenticator() {
    return authenticator;
  }

  @Override public ExternalUsersProvider getUsersProvider() {
    return usersProvider;
  }

  @Override public ExternalGroupsProvider getGroupsProvider() {
    return groupsProvider;
  }
}