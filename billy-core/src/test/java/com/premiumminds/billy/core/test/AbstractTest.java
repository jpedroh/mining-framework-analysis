package com.premiumminds.billy.core.test;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.premiumminds.billy.core.CoreDependencyModule;
import com.premiumminds.billy.core.test.fixtures.MockBaseEntity;

public class AbstractTest {
  private static Injector injector;

  protected final static String YML_CONFIGS_DIR = "src/test/resources/yml/";

  @BeforeClass public static void setUpClass() {
    AbstractTest.injector = Guice.createInjector(Modules.override(new CoreDependencyModule()).with(new MockDependencyModule()));
  }

  public <T extends java.lang.Object> T getInstance(Class<T> clazz) {
    return AbstractTest.injector.getInstance(clazz);
  }

  public <T extends java.lang.Object> T getMock(Class<T> clazz) {
    return Mockito.mock(clazz);
  }

  @SuppressWarnings(value = { "unchecked" }) public <T extends MockBaseEntity> T createMockEntity(Class<T> clazz, String path) {
    Yaml yaml = new Yaml(new Constructor(clazz));
    try {
      return (T) yaml.load(new BufferedReader(new FileReader(path)));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}