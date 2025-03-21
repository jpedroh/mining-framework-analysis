package org.hdiv.state.scope;
import javax.servlet.http.HttpSession;
import org.hdiv.context.RequestContext;
import org.hdiv.session.ISession;

/**
 * <p>
 * {@link StateScope} that stores states at user level.
 * </p>
 * <p>
 * States scoped to 'user' are stored at {@link HttpSession} and are shared by all the pages of the same user.
 * </p>
 *
 * @since 2.1.7
 */
public class UserSessionStateScope extends AbstractStateScope {
  public UserSessionStateScope() {
    super(StateScopeType.USER_SESSION);
  }

  private static final String USER_STATE_CACHE_ATTR = ScopedStateCache.class.getCanonicalName();

  protected ISession session;

  @Override public ScopedStateCache getStateCache(final RequestContext context) {
    ScopedStateCache cache = session.getAttribute(context, USER_STATE_CACHE_ATTR, ScopedStateCache.class);
    return cache;
  }

  @Override public void setStateCache(final RequestContext context, final ScopedStateCache cache) {
    session.setAttribute(context, USER_STATE_CACHE_ATTR, cache);
  }

  /**
	 * @param session the session to set
	 */
  public void setSession(final ISession session) {
    this.session = session;
  }
}