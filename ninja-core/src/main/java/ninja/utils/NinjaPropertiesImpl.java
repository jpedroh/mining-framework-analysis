package ninja.utils;
import java.io.File;
import java.util.Properties;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

@Singleton public class NinjaPropertiesImpl implements NinjaProperties {
  private static final Logger logger = LoggerFactory.getLogger(NinjaPropertiesImpl.class);

  private NinjaMode ninjaMode;

  private String contextPath = "";

  private final String ERROR_KEY_NOT_FOUND = "Key %s does not exist. Please include it in your application.conf. Otherwise this app will not work";

  /**
     * This is the final configuration holding all information from 
     * 1. application.conf. 
     * 2. Special properties for the mode you are running on extracted 
     *    from application.conf 
     * 3. An external configuration file defined
     *    by a system property and on the classpath.
     */
  private CompositeConfiguration compositeConfiguration;

  public NinjaPropertiesImpl(NinjaMode ninjaMode) {
    this.ninjaMode = ninjaMode;
    compositeConfiguration = new CompositeConfiguration();
    PropertiesConfiguration defaultConfiguration = null;
    Configuration prefixedDefaultConfiguration = null;
    PropertiesConfiguration externalConfiguration = null;
    Configuration prefixedExternalConfiguration = null;
    defaultConfiguration = SwissKnife.loadConfigurationInUtf8(NinjaProperties.CONF_FILE_LOCATION_BY_CONVENTION);
    if (defaultConfiguration != null) {
      prefixedDefaultConfiguration = defaultConfiguration.subset("%" + ninjaMode.name());
      if (NinjaMode.dev == ninjaMode) {
        defaultConfiguration.setReloadingStrategy(new FileChangedReloadingStrategy());
      }
    } else {
      String errorMessage = String.format("Error reading configuration file. Make sure you got a default config file %s", NinjaProperties.CONF_FILE_LOCATION_BY_CONVENTION);
      logger.error(errorMessage);
      throw new RuntimeException(errorMessage);
    }
    String ninjaExternalConf = System.getProperty(NINJA_EXTERNAL_CONF);
    if (ninjaExternalConf != null) {
      externalConfiguration = SwissKnife.loadConfigurationInUtf8(ninjaExternalConf);
      if (externalConfiguration == null) {
        String errorMessage = String.format("Ninja was told to use an external configuration%n" + " %s = %s %n." + "But the corresponding file cannot be found.%n" + " Make sure it is visible to this application and on the classpath.", NINJA_EXTERNAL_CONF, ninjaExternalConf);
        logger.error(errorMessage);
        throw new RuntimeException(errorMessage);
      } else {
        final boolean shouldReload = Boolean.getBoolean(NINJA_EXTERNAL_RELOAD);
        if (shouldReload) {
          externalConfiguration.setReloadingStrategy(new FileChangedReloadingStrategy());
        }
        prefixedExternalConfiguration = externalConfiguration.subset("%" + ninjaMode.name());
      }
    }
    if (prefixedExternalConfiguration != null) {
      compositeConfiguration.addConfiguration(prefixedExternalConfiguration);
    }
    if (externalConfiguration != null) {
      compositeConfiguration.addConfiguration(externalConfiguration);
    }
    if (prefixedDefaultConfiguration != null) {
      compositeConfiguration.addConfiguration(prefixedDefaultConfiguration);
    }
    if (defaultConfiguration != null) {
      compositeConfiguration.addConfiguration(defaultConfiguration);
    }
    NinjaPropertiesImplTool.checkThatApplicationSecretIsSet(isProd(), new File("").getAbsolutePath(), defaultConfiguration, compositeConfiguration);
  }

  @Override public String get(String key) {
    String value;
    try {
      value = compositeConfiguration.getString(key);
    } catch (Exception e) {
      value = null;
    }
    return value;
  }

  @Override public String getOrDie(String key) {
    String value = get(key);
    if (value == null) {
      logger.error(String.format(ERROR_KEY_NOT_FOUND, key));
      throw new RuntimeException(String.format(ERROR_KEY_NOT_FOUND, key));
    } else {
      return value;
    }
  }

  @Override public Integer getInteger(String key) {
    Integer value;
    try {
      value = compositeConfiguration.getInt(key);
    } catch (Exception e) {
      value = null;
    }
    return value;
  }

  @Override public Integer getIntegerOrDie(String key) {
    Integer value = getInteger(key);
    if (value == null) {
      logger.error(String.format(ERROR_KEY_NOT_FOUND, key));
      throw new RuntimeException(String.format(ERROR_KEY_NOT_FOUND, key));
    } else {
      return value;
    }
  }

  @Override public Boolean getBooleanOrDie(String key) {
    Boolean value = getBoolean(key);
    if (value == null) {
      logger.error(String.format(ERROR_KEY_NOT_FOUND, key));
      throw new RuntimeException(String.format(ERROR_KEY_NOT_FOUND, key));
    } else {
      return value;
    }
  }

  @Override public Boolean getBoolean(String key) {
    Boolean value;
    try {
      value = compositeConfiguration.getBoolean(key);
    } catch (Exception e) {
      value = null;
    }
    return value;
  }

  public void setProperty(String key, String value) {
    compositeConfiguration.setProperty(key, value);
  }

  public void bindProperties(Binder binder) {
    Names.bindProperties(binder, ConfigurationConverter.getProperties(compositeConfiguration));
  }

  @Override public boolean isProd() {
    return (ninjaMode.equals(NinjaMode.prod));
  }

  @Override public boolean isDev() {
    return (ninjaMode.equals(NinjaMode.dev));
  }

  @Override public boolean isTest() {
    return (ninjaMode.equals(NinjaMode.test));
  }

  /**
     * Get the context path on which the application is running
     * 
     * That means:
     * - when running on root the context path is empty
     * - when running on context there is NEVER a trailing slash
     * 
     * We conform to the following rules:
     * Returns the portion of the request URI that indicates the context of the 
     * request. The context path always comes first in a request URI. 
     * The path starts with a "/" character but does not end with a "/" character. 
     * For servlets in the default (root) context, this method returns "". 
     * The container does not decode this string.
     * 
     * As outlined by: http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#getContextPath()
     * 
     * @return the context-path with a leading "/" or "" if running on root
     */
  @Override public String getContextPath() {
    return contextPath;
  }

  @Override public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  @Override public Properties getAllCurrentNinjaProperties() {
    return ConfigurationConverter.getProperties(compositeConfiguration);
  }

  @Override public String[] getStringArray(String key) {
    String value = compositeConfiguration.getString(key);
    if (value != null) {
      return Iterables.toArray(Splitter.on(",").trimResults().omitEmptyStrings().split(value), String.class);
    } else {
      return null;
    }
  }

  @Override public String getWithDefault(String key, String defaultValue) {
    String value = get(key);
    if (value != null) {
      return value;
    } else {
      return defaultValue;
    }
  }

  @Override public Integer getIntegerWithDefault(String key, Integer defaultValue) {
    Integer value = getInteger(key);
    if (value != null) {
      return value;
    } else {
      return defaultValue;
    }
  }

  @Override public Boolean getBooleanWithDefault(String key, Boolean defaultValue) {
    Boolean value = getBoolean(key);
    if (value != null) {
      return value;
    } else {
      return defaultValue;
    }
  }
}