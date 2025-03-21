package conf;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;
import com.google.inject.Inject;
import controllers.ApiController;
import controllers.ApplicationController;
import controllers.ArticleController;
import controllers.LoginLogoutController;

public class Routes implements ApplicationRoutes {
  @Inject NinjaProperties ninjaProperties;

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
    if (!ninjaProperties.isProd()) {
      router.GET().route("/setup").with(ApplicationController.class, "setup");
    }
    router.GET().route("/login").with(LoginLogoutController.class, "login");
    router.POST().route("/login").with(LoginLogoutController.class, "loginPost");
    router.GET().route("/logout").with(LoginLogoutController.class, "logout");
    router.GET().route("/article/new").with(ArticleController.class, "articleNew");
    router.POST().route("/article/new").with(ArticleController.class, "articleNewPost");
    router.GET().route("/article/{id}").with(ArticleController.class, "articleShow");
    router.GET().route("/api/{username}/articles.json").with(ApiController.class, "getArticlesJson");
    router.GET().route("/api/{username}/article/{id}.json").with(ApiController.class, "getArticleJson");
    router.GET().route("/api/{username}/articles.xml").with(ApiController.class, "getArticlesXml");
    router.POST().route("/api/{username}/article.json").with(ApiController.class, "postArticleJson");
    router.POST().route("/api/{username}/article.xml").with(ApiController.class, "postArticleXml");
    router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
    router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
    router.GET().route("/.*").with(ApplicationController.class, "index");
  }
}