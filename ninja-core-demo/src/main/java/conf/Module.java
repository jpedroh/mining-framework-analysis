package conf;
import com.google.inject.AbstractModule;
import etc.GreetingService;
import etc.GreetingServiceImpl;

public class Module extends AbstractModule {
  public Module() {
    super();
  }

  @Override protected void configure() {
    bind(GreetingService.class).to(GreetingServiceImpl.class);
  }


<<<<<<< /usr/src/app/output/ninjaframework/ninja/4bbabcbc03c02b8b20481b1f47ad180f54f5d4f6/ninja-core-demo/src/main/java/conf/Module.java/left.java
  @Override protected ServletModule setupServlets() {
    bind(NinjaServletDispatcher.class).asEagerSingleton();
    bind(DemoServletFilter.class).asEagerSingleton();
    return new ServletModule() {
      @Override protected void configureServlets() {
        filter("/*").through(DemoServletFilter.class);
        serve("/*").with(NinjaServletDispatcher.class);
      }
    };
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.
}