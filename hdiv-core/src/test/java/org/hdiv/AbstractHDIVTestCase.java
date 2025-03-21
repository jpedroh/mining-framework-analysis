package org.hdiv;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.context.RequestContext;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.init.RequestInitializer;
import org.hdiv.listener.InitListener;
import org.hdiv.util.HDIVUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * HDIV test parent class.
 * 
 * @author Gotzon Illarramendi
 */
public abstract class AbstractHDIVTestCase extends TestCase {
  private static final Log log = LogFactory.getLog(AbstractHDIVTestCase.class);

  /**
	 * Pattern to check if the memory strategy is being used
	 */
  protected static final String MEMORY_PATTERN = "([0-9]+-){2}[A-Za-z0-9]+";

  /**
	 * Compiled MEMORY_PATTERN
	 */
  protected Pattern memoryPattern = Pattern.compile(MEMORY_PATTERN);

  /**
	 * Spring Factory
	 */
  private ApplicationContext applicationContext = null;

  /**
	 * Hdiv config for this app.
	 */
  private HDIVConfig config;

  private InitListener initListener;

  private MockHttpServletRequest mockRequest;

  private MockHttpServletResponse mockResponse;

  private RequestContext requestContext;

  private String[] files = { "/org/hdiv/config/hdiv-core-applicationContext.xml", "/org/hdiv/config/hdiv-config.xml", "/org/hdiv/config/hdiv-validations.xml", "/org/hdiv/config/applicationContext-extra.xml" };

  protected final void setUp() throws Exception {
    preSetUp();
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/path/testAction.do");
    HttpServletResponse response = new MockHttpServletResponse();
    HttpSession httpSession = request.getSession();
    ServletContext servletContext = httpSession.getServletContext();
    this.requestContext = new RequestContext(request, response);
    this.mockRequest = request;
    this.mockResponse = (MockHttpServletResponse) response;
    XmlWebApplicationContext webApplicationContext = new XmlWebApplicationContext();
    webApplicationContext.setServletContext(servletContext);
    webApplicationContext.setConfigLocations(files);
    servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
    webApplicationContext.refresh();
    this.applicationContext = webApplicationContext;
    this.config = this.applicationContext.getBean(HDIVConfig.class);
    this.postCreateHdivConfig(this.config);
    this.initListener = new InitListener();
    ServletContextEvent servletContextEvent = new ServletContextEvent(servletContext);
    this.initListener.contextInitialized(servletContextEvent);
    HttpSessionEvent httpSessionEvent = new HttpSessionEvent(httpSession);
    this.initListener.sessionCreated(httpSessionEvent);
    RequestInitializer requestInitializer = this.applicationContext.getBean(RequestInitializer.class);
    requestInitializer.initRequest(request, response);
    DataComposerFactory dataComposerFactory = this.applicationContext.getBean(DataComposerFactory.class);
    IDataComposer dataComposer = dataComposerFactory.newInstance(request);
    HDIVUtil.setDataComposer(dataComposer, request);
    if (log.isDebugEnabled()) {
      log.debug("Hdiv test context initialized");
    }
    onSetUp();
  }

  /**
	 * Hook method for test initialization
	 * 
	 * @throws Exception
	 */
  protected abstract void onSetUp() throws Exception;

  /**
	 * Hook method for test pre-initialization
	 * 
	 * @throws Exception
	 */
  protected void preSetUp() throws Exception {
  }

  /**
	 * Hook method for test end
	 * 
	 * @throws Exception
	 */
  protected void onTearDown() throws Exception {
  }

  /**
	 * Hook method for test pre-end
	 * 
	 * @throws Exception
	 */
  protected void preTearDown() throws Exception {
  }

  @Override protected void tearDown() throws Exception {
    preTearDown();
    RequestInitializer requestInitializer = this.applicationContext.getBean(RequestInitializer.class);
    requestInitializer.endRequest(mockRequest, mockResponse);
    HttpSessionEvent httpSessionEvent = new HttpSessionEvent(mockRequest.getSession());
    this.initListener.sessionDestroyed(httpSessionEvent);
    ServletContextEvent servletContextEvent = new ServletContextEvent(mockRequest.getSession().getServletContext());
    this.initListener.contextDestroyed(servletContextEvent);
    ((ConfigurableApplicationContext) this.applicationContext).close();
    onTearDown();
  }

  /**
	 * Hook method for {@link HDIVConfig} customization
	 * 
	 * @param config
	 */
  protected void postCreateHdivConfig(HDIVConfig config) {
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  /**
	 * @return the config
	 */
  public HDIVConfig getConfig() {
    return config;
  }

  public RequestContext getRequestContext() {
    return requestContext;
  }

  public MockHttpServletRequest getMockRequest() {
    return mockRequest;
  }

  public MockHttpServletResponse getMockResponse() {
    return mockResponse;
  }

  /**
	 * Return the configuration files
	 * 
	 * @return files configuration files
	 */
  protected String[] getFiles() {
    return files;
  }

  /**
	 * Set the configuration files
	 * 
	 * @param files configuration files
	 */
  protected void setFiles(String[] files) {
    this.files = files;
  }
}