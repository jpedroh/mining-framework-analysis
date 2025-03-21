package br.com.caelum.vraptor.environment;
import java.io.IOException;
import org.slf4j.Logger;
import java.io.InputStream;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * A default environment based on a string.
 * 
 * @author Alexandre Atoji
 * @author Andrew Kurauchi
 * @author Guilherme Silveira
 */
public class DefaultEnvironment implements Environment {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultEnvironment.class);

  private final Properties properties = new Properties();

  private final String environment;

  public DefaultEnvironment(String environment) throws IOException {
    if (environment == null || environment.equals("")) {
      environment = "development";
    }
    this.environment = environment;

<<<<<<< /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/left.java
    LOG.info("Using vraptor environment " + environment);
=======
    loadAndPut("environment");
>>>>>>> /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/right.java

    loadAndPut(environment);
  }

  private void loadAndPut(String environment) throws IOException {
    String name = "/" + environment + ".properties";
    InputStream stream = DefaultEnvironment.class.getResourceAsStream(name);
    Properties properties = new Properties();
    if (properties != null) {
      properties.load(stream);
      this.properties.putAll(properties);
    } else {
      LOG.warn("Could not find the file \'" + environment + ".properties\' to load. If you ask for any property, null will be returned");
    }
  }

  @Override public boolean supports(String feature) {
    return Boolean.parseBoolean(get(feature));
  }

  @Override public boolean has(String key) {
    return properties.containsKey(key);
  }

  @Override public String get(String key) {
    if (!has(key)) {
      throw new NoSuchElementException("Key " + key + " not found in environment " + environment);
    }
    return properties.getProperty(key);
  }

  @Override public String get(String key, String defaultValue) {
    if (has(key)) {
      return get(key);
    }
    return defaultValue;
  }

  @Override public void set(String key, String value) {
    this.properties.setProperty(key, value);
  }

  @Override public Iterable<String> getKeys() {
    return 
<<<<<<< /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/left.java
    properties.stringPropertyNames()
=======
    (Iterable<String>) this.properties.stringPropertyNames()
>>>>>>> /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/right.java
    ;
  }

  @Override public URL getResource(String name) {
    URL resource = DefaultEnvironment.class.getResource("/" + environment + name);
    if (resource != null) {
      return resource;
    }
    return DefaultEnvironment.class.getResource(name);
  }

  @Override public String getName() {
    return environment;
  }
}