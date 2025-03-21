package org.hdiv.state.scope;
import javax.servlet.ServletContext;
import org.hdiv.context.RequestContext;
import org.springframework.web.context.ServletContextAware;

/**
 * <p>
 * {@link StateScope} that stores states at application level.
 * </p>
 * <p>
 * States scoped to 'app' are stored at {@link ServletContext} and are shared by all the users of the application.
 * </p>
 *
 * @since 2.1.7
 */
public final class AppStateScope extends AbstractStateScope implements ServletContextAware {
  public AppStateScope() {
    super(StateScopeType.APP);
  }

  private static final String APP_STATE_CONTEXT_ATTR = ScopedStateCache.class.getCanonicalName();

  protected ServletContext servletContext;

  @Override public ScopedStateCache getStateCache(final RequestContext context) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    ScopedStateCache cache = (ScopedStateCache) this.servletContext.getAttribute(APP_STATE_CONTEXT_ATTR);
>>>>>>> /usr/src/app/output/hdiv/hdiv/2f9993190a5d9e153c693e8060ef8050f2fe2baa/hdiv-core/src/main/java/org/hdiv/state/scope/AppStateScope.java/right.java

    return (ScopedStateCache) context.getRequest().getSession().getServletContext().getAttribute(APP_STATE_CONTEXT_ATTR);
  }

  @Override public void setStateCache(final RequestContext context, final ScopedStateCache cache) {
    this.servletContext.setAttribute(APP_STATE_CONTEXT_ATTR, cache);
  }

  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }
}