package io.github.benas.easyproperties;
import io.github.benas.easyproperties.api.PropertiesInjector;
import org.junit.Before;
import org.junit.Test;
import static io.github.benas.easyproperties.PropertiesInjectorBuilder.aNewPropertiesInjectorBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesInjectorImplTest {
  private PropertiesInjector propertiesInjector;

  @Before public void setUp() {
    propertiesInjector = aNewPropertiesInjectorBuilder().registerAnnotationProcessor(MyCustomAnnotation.class, new MyCustomAnnotationProcessor()).build();
  }

  @Test public void testCustomAnnotationProcessor() {
    Config config = new Config();
    propertiesInjector.injectProperties(config);
    assertThat(config.getCustom()).isEqualTo("foo");
  }

  @Test public void testConfigurationHotReloading() throws Exception {
    EmbeddedDatabase database = new EmbeddedDatabaseBuilder().setName("test").addScript("database.sql").build();
    System.setProperty("sp", "foo");
    HotReloadableConfig config = new HotReloadableConfig();
    propertiesInjector.injectProperties(config);
    assertThat(config.getSystemProperty()).isEqualTo("foo");
    assertThat(config.getName()).isEqualTo("Foo");
    System.setProperty("sp", "bar");
    new JdbcTemplate(database).update("update ApplicationProperties set value = ? where key = ?", "Bar", "name");
    sleep(2 * 1000);
    assertThat(config.getSystemProperty()).isEqualTo("bar");
    assertThat(config.getName()).isEqualTo("Bar");
    database.shutdown();
  }
}