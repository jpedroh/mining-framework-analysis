package conf;
import com.google.inject.AbstractModule;
import ninja.utils.NinjaProperties;

public class Module extends AbstractModule {
  NinjaProperties ninjaProperties;

  public Module(NinjaProperties ninjaProperties) {
    if (ninjaProperties == null) {
      throw new IllegalArgumentException("Received null as an instance of NinjaProperties");
    }
    this.ninjaProperties = ninjaProperties;
  }

  @Override protected void configure() {
    bind(DummyInterfaceForTesting.class).to(DummyClassForTesting.class);
  }

  public static interface DummyInterfaceForTesting {
  }

  public static class DummyClassForTesting implements DummyInterfaceForTesting {
  }
}