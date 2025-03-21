package org.sonar.plugins.ldap;
import org.junit.Test;
import org.sonar.api.config.Settings;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Here we test the settingsManagers ability to cope with multiple ldap servers.
 */
public class LdapSettingsManagerTest {
  /**
	 * Test there are 2 @link{org.sonar.plugins.ldap.LdapContextFactory}s found.
	 * 
	 * @throws Exception
	 *             This is not expected.
	 */
  @Test public void testContextFactories() throws Exception {
    LdapSettingsManager settingsManager = new LdapSettingsManager(generateMultipleLdapSettingsWithUserAndGroupMapping());
    assertThat(settingsManager.getContextFactories().size()).isEqualTo(2);
    assertThat(settingsManager.getContextFactories().size()).isEqualTo(2);
  }

  /**
	 * Test there are 2 @link{org.sonar.plugins.ldap.LdapUserMapping}s found.
	 * 
	 * @throws Exception
	 *             This is not expected.
	 */
  @Test public void testUserMappings() throws Exception {
    LdapSettingsManager settingsManager = new LdapSettingsManager(generateMultipleLdapSettingsWithUserAndGroupMapping());
    assertThat(settingsManager.getUserMappings().size()).isEqualTo(2);
    assertThat(settingsManager.getUserMappings().size()).isEqualTo(2);
  }

  /**
	 * Test there are 2 @link{org.sonar.plugins.ldap.LdapGroupMapping}s found.
	 * 
	 * @throws Exception
	 *             This is not expected.
	 */
  @Test public void testGroupMappings() throws Exception {
    LdapSettingsManager settingsManager = new LdapSettingsManager(generateMultipleLdapSettingsWithUserAndGroupMapping());
    assertThat(settingsManager.getGroupMappings().size()).isEqualTo(2);
    assertThat(settingsManager.getGroupMappings().size()).isEqualTo(2);
  }

  /**
	 * Test what happens when no configuration is set. Normally there will be a
	 * contextFactory, but the autodiscovery doesn't work for the test server.
	 * 
	 * @throws Exception
	 */
  @Test public void testEmptySettings() throws Exception {
    LdapSettingsManager settingsManager = new LdapSettingsManager(new Settings());
    assertThat(settingsManager.getContextFactories().size()).isEqualTo(0);
    assertThat(settingsManager.getUserMappings().size()).isEqualTo(0);
    assertThat(settingsManager.getGroupMappings().size()).isEqualTo(0);
  }

  private Settings generateMultipleLdapSettingsWithUserAndGroupMapping() {
    Settings settings = new Settings();
    settings.setProperty("ldap.url", "/users.example.org.ldif").setProperty("ldap.user.baseDn", "ou=users,dc=example,dc=org").setProperty("ldap.group.baseDn", "ou=groups,dc=example,dc=org").setProperty("ldap.group.request", "(&(objectClass=posixGroup)(memberUid={uid}))");
    settings.setProperty("ldap1.url", "/users.infosupport.com.ldif").setProperty("ldap1.user.baseDn", "ou=users,dc=infosupport,dc=com").setProperty("ldap1.group.baseDn", "ou=groups,dc=infosupport,dc=com").setProperty("ldap1.group.request", "(&(objectClass=posixGroup)(memberUid={uid}))");
    return settings;
  }
}