package de.hilling.junit.cdi.microprofile;

import de.hilling.junit.cdi.scope.TestSuiteScoped;
import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Prevent any problems with non-resolved global properties.
 */
@TestSuiteScoped
public class DummyConfigSource implements ConfigSource {

    @Override
    public Map<String, String> getProperties() {
        return new HashMap<>();
    }

    @Override
    public Set<String> getPropertyNames() {
        return Collections.singleton("dummy");
    }

    @Override
    public String getValue(String propertyName) {
        return "1";
    }

    @Override
    public String getName() {
        return "test-config";
    }
}
