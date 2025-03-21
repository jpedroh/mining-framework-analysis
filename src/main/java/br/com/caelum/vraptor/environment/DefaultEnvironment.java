package br.com.caelum.vraptor.environment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default environment based on a string.
 *
 * @author Alexandre Atoji
 * @author Andrew Kurauchi
 * @author Guilherme Silveira
 */
public class DefaultEnvironment implements Environment {
<<<<<<< /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/left.java
	
	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultEnvironment.class);
	private final Properties properties;
	private final String environment;
	
	public DefaultEnvironment(String environment) throws IOException {
		if (environment == null || environment.equals("")) {
			environment = "development";
		}
		this.environment = environment;
		LOG.info("Using vraptor environment " + environment);
		String name = "/" + environment + ".properties";
		InputStream stream = DefaultEnvironment.class.getResourceAsStream(name);
		this.properties = new Properties();
		if (stream != null) {
			this.properties.load(stream);
		} else {
			LOG.warn("Could not find the file " + name
					+ " to load. If you ask for any property, null will be returned");
		}
	}
||||||| /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/base.java
	
	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultEnvironment.class);
	private final Properties properties;
	private String environment;
	
	public DefaultEnvironment(String environment) throws IOException {
		if (environment == null || environment.equals("")) {
			environment = "development";
		}
		this.environment = environment;
		String name = "/" + environment + ".properties";
		InputStream stream = DefaultEnvironment.class.getResourceAsStream(name);
		this.properties = new Properties();
		if (stream != null) {
			this.properties.load(stream);
		} else {
			LOG.warn("Could not find the file " + name
					+ " to load. If you ask for any property, null will be returned");
		}
	}
=======
>>>>>>> /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/right.java

<<<<<<< /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/left.java
	@Override
	public boolean supports(String feature) {
		return Boolean.parseBoolean(get(feature));
	}
||||||| /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/base.java
	public boolean supports(String feature) {
		return Boolean.parseBoolean(get(feature));
	}
=======
    private static final Logger LOG = LoggerFactory.getLogger(DefaultEnvironment.class);
    private final Properties properties = new Properties();
    private String environment;
>>>>>>> /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/right.java

<<<<<<< /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/left.java
	@Override
	public boolean has(String key) {
		return properties.containsKey(key);
	}
||||||| /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/base.java
	public boolean has(String key) {
		return properties.containsKey(key);
	}
=======
    public DefaultEnvironment(String environment) throws IOException {
        if (environment == null || environment.equals("")) {
            environment = "development";
        }
        this.environment = environment;
>>>>>>> /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/right.java

<<<<<<< /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/left.java
	@Override
	public String get(String key) {
		if(!has(key)) {
			throw new NoSuchElementException("Key " + key + " not found in environment " + environment);
		}
		return properties.getProperty(key);
	}
	
	@Override
	public String get(String key, String defaultValue) {
	    if (has(key)) {
	        return get(key);
	    }
	    return defaultValue;
	}
||||||| /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/base.java
	public String get(String key) {
		if(!has(key)) {
			throw new NoSuchElementException("Key " + key + " not found in environment " + environment);
		}
		return properties.getProperty(key);
	}
=======
        loadAndPut("environment");
        loadAndPut(environment);
    }
>>>>>>> /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/right.java

    private void loadAndPut(String environment) throws IOException {
        String name = "/" + environment + ".properties";
        InputStream stream = DefaultEnvironment.class.getResourceAsStream(name);

<<<<<<< /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/left.java
	@Override
	public Iterable<String> getKeys() {
		return properties.stringPropertyNames();
	}
||||||| /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/base.java
	@Override
	public Iterable<String> getKeys() {
		return (Iterable<String>) properties.stringPropertyNames();
	}
=======
        Properties properties = new Properties();
>>>>>>> /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/right.java

        if (properties != null) {
            properties.load(stream);
            this.properties.putAll(properties);
        } else {
            LOG.warn("Could not find the file '" + environment + ".properties' to load. If you ask for any property, null will be returned");
        }
    }

<<<<<<< /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/left.java
	@Override
	public String getName() {
		return environment;
	}

||||||| /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/base.java
	@Override
	public String getName() {
		return environment;
	}
=======
    public boolean supports(String feature) {
        return Boolean.parseBoolean(get(feature));
    }

    public boolean has(String key) {
        return properties.containsKey(key);
    }

    public String get(String key) {
        if(!has(key)) {
            throw new NoSuchElementException("Key " + key + " not found in environment " + environment);
        }
        return properties.getProperty(key);
    }

    @Override
    public void set(String key, String value) {
        this.properties.setProperty(key, value);
    }

    @Override
    public Iterable<String> getKeys() {
        return (Iterable<String>) this.properties.stringPropertyNames();
    }

    @Override
    public URL getResource(String name) {
        URL resource = DefaultEnvironment.class.getResource("/" + environment + name);
        if (resource != null) {
            return resource;
        }
        return DefaultEnvironment.class.getResource(name);
    }

    @Override
    public String getName() {
        return environment;
    }
>>>>>>> /usr/src/app/output/caelum/vraptor-environment/57463c6eb5148d0640081dab2ba0099937ba72ed/src/main/java/br/com/caelum/vraptor/environment/DefaultEnvironment.java/right.java

}
