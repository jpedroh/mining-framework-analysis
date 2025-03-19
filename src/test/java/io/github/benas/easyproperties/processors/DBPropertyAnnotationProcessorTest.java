package io.github.benas.easyproperties.processors;
import io.github.benas.easyproperties.annotations.DBProperty;
import io.github.benas.easyproperties.api.PropertyInjectionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import static org.assertj.core.api.Assertions.assertThat;

public class DBPropertyAnnotationProcessorTest extends AbstractAnnotationProcessorTest {
  private EmbeddedDatabase embeddedDatabase;

  @Before public void setUp() throws Exception {
    super.setUp();
    embeddedDatabase = new EmbeddedDatabaseBuilder().setName("test").addScript("database.sql").build();
  }

  @Test public void testPropertyInjectionFromDatabase() {
    Bean bean = new Bean();
    propertiesInjector.injectProperties(bean);
    assertThat(bean.getName()).isEqualTo("Foo");
  }

  @Test(expected = PropertyInjectionException.class) public void whenConfigurationIsMissing_thenShouldThrowAnException() throws Exception {
    BeanWithInvalidConfiguration bean = new BeanWithInvalidConfiguration();
    propertiesInjector.injectProperties(bean);
  }

  @Test public void whenKeyIsMissing_thenShouldSilentlyIgnoreTheField() {
    BeanWithInvalidKey bean = new BeanWithInvalidKey();
    propertiesInjector.injectProperties(bean);
    assertThat(bean.getName()).isNull();
  }

  @After public void shutdownEmbeddedDatabase() throws Exception {
    embeddedDatabase.shutdown();
  }

  public class Bean {
    @DBProperty(configuration = "database.properties", key = "name") private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public class BeanWithInvalidConfiguration {
    @DBProperty(configuration = "blah.properties", key = "name") private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public class BeanWithInvalidKey {
    @DBProperty(configuration = "database.properties", key = "blah") private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}