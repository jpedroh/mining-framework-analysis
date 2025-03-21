package org.hdiv.session;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.context.RequestContext;
import org.hdiv.exception.HDIVException;
import org.hdiv.idGenerator.PageIdGenerator;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;

/**
 * A custom wrapper for http session request that returns a wrapped http session.
 * 
 * @author Roberto Velasco
 */
public class SessionHDIV implements ISession, BeanFactoryAware {
  /**
	 * Prefix for the key of the pages stored in session.
	 */
  protected final HTTPSessionCache cache = new HTTPSessionCache();

  /**
	 * Commons Logging instance.
	 */
  private static final Log log = LogFactory.getLog(SessionHDIV.class);

  /**
	 * The root interface for accessing a Spring bean container.
	 * 
	 * @see org.springframework.beans.factory.BeanFactory
	 */
  private BeanFactory beanFactory;

  /**
	 * The pageIdGeneratorName
	 */
  private String pageIdGeneratorName = Constants.PAGE_ID_GENERATOR_NAME;

  /**
	 * Obtains from the user session the page identifier for the current request.
	 * 
	 * @param context Context holder for request-specific state.
	 * @return Returns the pageId.
	 */
  public final int getPageId(final RequestContext context) {
    HttpSession session = context.getRequest().getSession();
    PageIdGenerator pageIdGenerator = (PageIdGenerator) session.getAttribute(pageIdGeneratorName);
    if (pageIdGenerator == null) {
      pageIdGenerator = beanFactory.getBean(PageIdGenerator.class);
    }
    if (pageIdGenerator == null) {
      throw new HDIVException("session.nopageidgenerator");
    }
    int id = pageIdGenerator.getNextPageId();
    if (id <= 0) {
      throw new HDIVException("Incorrect PageId generated [" + id + "]. PageId must be greater than 0.");
    }
    session.setAttribute(pageIdGeneratorName, pageIdGenerator);
    return id;
  }

  /**
	 * Returns the page with id <code>pageId</code>.
	 * 
	 * @param context Context holder for request-specific state.
	 * @param pageId page id
	 * @return Returns the page with id <code>pageId</code>.
	 * @since HDIV 2.0.4
	 */
  public IPage getPage(final RequestContext context, final int pageId) {
    try {
      return cache.findPage(new SimpleCacheKey(context, pageId));
    } catch (final IllegalStateException e) {
      throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT, e);
    }
  }

  /**
	 * It adds a new page to the user session.
	 * 
	 * @param context Context holder for request-specific state.
	 * @param page Page with all the information about states
	 */
  public void addPage(final RequestContext context, final IPage page) {
    addPageToSession(context, page, false);
  }

  /**
	 * It adds a partial page to the user session.
	 * 
	 * @param context Context holder for request-specific state.
	 * @param page Page with all the information about states
	 */
  public void addPartialPage(final RequestContext context, final IPage page) {
    addPageToSession(context, page, true);
  }

  /**
	 * Deletes from session the data related to the finished flows. This means a memory consumption optimization because
	 * useless objects of type <code>IPage</code> are deleted.
	 * 
	 * @param context Context holder for request-specific state.
	 * @param conversationId finished flow identifier
	 * @since HDIV 2.0.3
	 */
  public void removeEndedPages(final RequestContext context, final String conversationId) {
    if (cache instanceof HTTPSessionCache) {
      cache.removeEndedPages(context, conversationId);
    } else {
      log.error("Remove ended pages not supported by cache:" + cache);
    }
  }

  /**
	 * Obtains the state identifier <code>stateId</code> related to the page identifier <code>pageId</code>.
	 * 
	 * @param context Context holder for request-specific state.
	 * @return State identifier <code>stateId</code> throws HDIVException If the state doesn't exist a new HDIV
	 * exception is thrown.
	 */
  public IState getState(final RequestContext context, final int pageId, final int stateId) {
    try {
      return getPage(context, pageId).getState(stateId);
    } catch (final Exception e) {
      throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT, e);
    }
  }

  /**
	 * Internal method to add a new IPage instance to {@link HttpSession}
	 * 
	 * @param context {@link RequestContext} instance
	 * @param page IPage instance
	 * @param isPartial If is partial page
	 * 
	 * @since HDIV 2.1.5
	 */
  protected void addPageToSession(final RequestContext context, final IPage page, final boolean isPartial) {
    cache.insertPage(new SimpleCacheKey(context, page.getId()), page);
  }

  /**
	 * Callback that supplies the owning factory to a bean instance. Invoked after population of normal bean properties
	 * but before an init callback like InitializingBean's afterPropertiesSet or a custom init-method.
	 * 
	 * @param beanFactory owning BeanFactory (may not be null). The bean can immediately call methods on the factory.
	 */
  public void setBeanFactory(final BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
    if (cache instanceof HTTPSessionCache) {
      cache.setBeanFactory(beanFactory);
    }
  }

  public String getAttribute(final RequestContext context, final String name) {
    Assert.notNull(context);
    Assert.notNull(name);
    return (String) context.getSession().getAttribute(name);
  }

  @SuppressWarnings(value = { "unchecked" }) public <T extends java.lang.Object> T getAttribute(final RequestContext context, final String name, final Class<T> requiredType) {
    Assert.notNull(context);
    Assert.notNull(name);
    Assert.notNull(requiredType);
    Object result = context.getSession().getAttribute(name);
    if (result == null) {
      return null;
    } else {
      if (requiredType.isInstance(result)) {
        return (T) result;
      } else {
        throw new IllegalArgumentException("Attibute with name \'" + name + "\' is not of required type " + requiredType.getCanonicalName());
      }
    }
  }

  public void setAttribute(final RequestContext context, final String name, final Object value) {
    Assert.notNull(context);
    Assert.notNull(name);
    context.getSession().setAttribute(name, value);
  }

  public void removeAttribute(final RequestContext context, final String name) {
    Assert.notNull(context);
    Assert.notNull(name);
    context.getSession().removeAttribute(name);
  }

  /**
	 * @param pageIdGeneratorName The pageIdGeneratorName to set.
	 */
  public void setPageIdGeneratorName(final String pageIdGeneratorName) {
    this.pageIdGeneratorName = pageIdGeneratorName;
  }

  @Deprecated public final void addPage(final RequestContext context, final int pageId, final IPage page) {
    addPage(context, page);
  }

  @Deprecated public final void addPartialPage(final RequestContext context, final int pageId, final IPage page) {
    addPartialPage(context, page);
  }
}