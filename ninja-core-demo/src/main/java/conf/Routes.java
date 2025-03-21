package conf;
import com.google.inject.Inject;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;
import controllers.ApplicationController;
import controllers.AsyncController;
import controllers.FilterController;
import controllers.I18nController;
import controllers.InjectionExampleController;
import controllers.PersonController;
import controllers.UdpPingController;
import controllers.UploadController;

public class Routes implements ApplicationRoutes {
  private NinjaProperties ninjaProperties;

  @Inject public Routes(NinjaProperties ninjaProperties) {
    this.ninjaProperties = ninjaProperties;
  }

  /**
     * Using a (almost) nice DSL we can configure the router.
     * 
     * The second argument NinjaModuleDemoRouter contains all routes of a
     * submodule. By simply injecting it we activate the routes.
     * 
     * @param router
     *            The default router of this application
     */
  @Override public void init(Router router) {
    router.GET().route("/").with(ApplicationController.class, "index");
    router.GET().route("/examples").with(ApplicationController.class, "examples");
    router.GET().route("/user/{id}/{email}/userDashboard").with(ApplicationController.class, "userDashboard");
    router.GET().route("/validation").with(ApplicationController.class, "validation");
    router.GET().route("/redirect").with(ApplicationController.class, "redirect");
    router.GET().route("/session").with(ApplicationController.class, "session");
    router.GET().route("/htmlEscaping").with(ApplicationController.class, "htmlEscaping");
    router.GET().route("/person").with(PersonController.class, "getPerson");
    router.POST().route("/person").with(PersonController.class, "postPerson");
    router.GET().route("/contactForm").with(ApplicationController.class, "contactForm");
    router.POST().route("/contactForm").with(ApplicationController.class, "postContactForm");
    router.GET().route("/udpcount").with(UdpPingController.class, "getCount");
    router.GET().route("/filter").with(FilterController.class, "filter");
    router.GET().route("/teapot").with(FilterController.class, "teapot");
    router.GET().route("/injection").with(InjectionExampleController.class, "injection");
    router.GET().route("/async").with(AsyncController.class, "asyncEcho");
    router.GET().route("/i18n").with(I18nController.class, "index");
    router.GET().route("/upload").with(UploadController.class, "upload");
    router.POST().route("/uploadFinish").with(UploadController.class, "uploadFinish");
    router.GET().route("/form").with(ApplicationController.class, "form");
    router.POST().route("/form").with(ApplicationController.class, "form");
    if (!ninjaProperties.isProd()) {
      router.GET().route("/_test/testPage").with(ApplicationController.class, "testPage");
    }
    router.GET().route("/assets/.*").with(AssetsController.class, "serve");
  }
}