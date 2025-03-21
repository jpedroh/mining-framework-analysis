package org.sonar.plugins.ldap;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.util.HashMap;

/**
 * @author Evgeny Mandrikov
 */
public class Krb5LoginConfiguration extends Configuration {
  private static final AppConfigurationEntry[] CONFIG_LIST = new AppConfigurationEntry[1];

  static {
    String loginModule = "com.sun.security.auth.module.Krb5LoginModule";
    AppConfigurationEntry.LoginModuleControlFlag flag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
    CONFIG_LIST[0] = new AppConfigurationEntry(loginModule, flag, new HashMap<String, Object>());
  }

  /**
	 * Creates a new instance of Krb5LoginConfiguration.
	 */
  public Krb5LoginConfiguration() {
    super();
  }

  /**
	 * Interface method requiring us to return all the LoginModules we know
	 * about.
	 */
  public AppConfigurationEntry[] getAppConfigurationEntry(String applicationName) {
    return CONFIG_LIST.clone();
  }

  /**
	 * Interface method for reloading the configuration. We don't need this.
	 */
  public void refresh() {
  }
}