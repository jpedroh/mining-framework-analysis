package org.sonar.plugins.ldap;
import org.sonar.api.config.Settings;
import java.util.HashMap;
import java.util.Map;

/**
 * The LdapSettingsManager will parse the settings. This class is also
 * responsible to cope with multiple ldap servers.
 */
public class LdapSettingsManager {
  /**
	 * The base ldap index variable.
	 */
  public static final String LDAP = "ldap";

  private Settings settings;

  private Map<String, LdapUserMapping> userMappings = null;

  private Map<String, LdapGroupMapping> groupMappings = null;

  private Map<String, LdapContextFactory> contextFactories;

  /**
	 * Create an instance of the settings manager.
	 * 
	 * @param settings
	 *            The settings to use.
	 */
  public LdapSettingsManager(Settings settings) {
    this.settings = settings;
  }

  /**
	 * Get all the @link{LdapUserMapping}s available in the settings.
	 * 
	 * @return A @link{Map} with all the @link{LdapUserMapping} objects. The key
	 *         is the prefix used in the settings (ldap = Default).
	 */
  public Map<String, LdapUserMapping> getUserMappings() {
    if (userMappings == null) {
      userMappings = new HashMap<String, LdapUserMapping>();
      int index = 1;
      String ldapIndex = LDAP;
      while (settings.getString(ldapIndex + ".user.baseDn") != null) {
        userMappings.put(ldapIndex, new LdapUserMapping(settings, ldapIndex));
        ldapIndex = LDAP + index;
        index++;
      }
    }
    return userMappings;
  }

  /**
	 * Get all the @link{LdapGroupMapping}s available in the settings.
	 * 
	 * @return A @link{Map} with all the @link{LdapGroupMapping} objects. The
	 *         key is the prefix used in the settings (ldap = Default).
	 */
  public Map<String, LdapGroupMapping> getGroupMappings() {
    if (groupMappings == null) {
      groupMappings = new HashMap<String, LdapGroupMapping>();
      int index = 1;
      String ldapIndex = LDAP;
      while (settings.getString(ldapIndex + ".group.baseDn") != null) {
        groupMappings.put(ldapIndex, new LdapGroupMapping(settings, ldapIndex));
        ldapIndex = LDAP + index;
        index++;
      }
    }
    return groupMappings;
  }

  /**
	 * Get all the @link{LdapContextFactory}s available in the settings.
	 * 
	 * @return A @link{Map} with all the @link{LdapContextFactory} objects. The
	 *         key is the prefix used in the settings (ldap = Default).
	 */
  public Map<String, LdapContextFactory> getContextFactories() {
    if (contextFactories == null) {
      contextFactories = new HashMap<String, LdapContextFactory>();
      int index = 1;
      String ldapIndex = LDAP;
      while (settings.getString(ldapIndex + ".url") != null || LdapAutodiscovery.getLdapServer(settings.getString(ldapIndex + ".realm")) != null) {
        contextFactories.put(ldapIndex, new LdapContextFactory(settings, ldapIndex));
        ldapIndex = LDAP + index;
        index++;
      }
    }
    return contextFactories;
  }
}