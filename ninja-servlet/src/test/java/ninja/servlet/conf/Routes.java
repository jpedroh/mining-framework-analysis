package ninja.servlet.conf;
import controller.DummyControllerForTesting;
import ninja.Router;
import ninja.application.ApplicationRoutes;

/**
 * Application Routes Stub to be used in unit tests
 *
 * @author avarabyeu
 */
public class Routes implements ApplicationRoutes {
  @Override public void init(Router router) {
    router.GET().route("/").with(DummyControllerForTesting.class, "dummyMethod");
  }
}