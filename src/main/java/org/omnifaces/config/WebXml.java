package org.omnifaces.config;
import static org.omnifaces.util.Faces.getServletContext;
import static org.omnifaces.util.Faces.hasContext;
import static org.omnifaces.util.Utils.isEmpty;
import static org.omnifaces.util.Utils.isNumber;
import static org.omnifaces.util.Xml.createDocument;
import static org.omnifaces.util.Xml.getNodeList;
import static org.omnifaces.util.Xml.getTextContent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.webapp.FacesServlet;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.HashMap;

public enum WebXml {
  INSTANCE
  ;

  private static final Logger logger = Logger.getLogger(WebXml.class.getName());

  private static final String WEB_XML = "/WEB-INF/web.xml";

  private static final String WEB_FRAGMENT_XML = "META-INF/web-fragment.xml";

  private static final String XPATH_WELCOME_FILE = "welcome-file-list/welcome-file";

  private static final String XPATH_EXCEPTION_TYPE = "error-page/exception-type";

  private static final String XPATH_LOCATION = "location";

  private static final String XPATH_ERROR_PAGE_500_LOCATION = "error-page[error-code=500]/location";

  private static final String XPATH_ERROR_PAGE_DEFAULT_LOCATION = "error-page[not(error-code) and not(exception-type)]/location";

  private static final String XPATH_FORM_LOGIN_PAGE = "login-config[auth-method=\'FORM\']/form-login-config/form-login-page";

  private static final String XPATH_FORM_ERROR_PAGE = "login-config[auth-method=\'FORM\']/form-login-config/form-error-page";

  private static final String XPATH_SECURITY_CONSTRAINT = "security-constraint";

  private static final String XPATH_WEB_RESOURCE_URL_PATTERN = "web-resource-collection/url-pattern";

  private static final String XPATH_AUTH_CONSTRAINT = "auth-constraint";

  private static final String XPATH_AUTH_CONSTRAINT_ROLE_NAME = "auth-constraint/role-name";

  private static final String XPATH_SESSION_TIMEOUT = "session-config/session-timeout";

  private static final String ERROR_NOT_INITIALIZED = "WebXml is not initialized yet. Please use #init(ServletContext) method to manually initialize it.";

  private static final String ERROR_URL_MUST_START_WITH_SLASH = "URL must start with \'/\': \'%s\'";

  private static final String LOG_INITIALIZATION_ERROR = "WebXml failed to initialize. Perhaps your web.xml contains a typo?";

  private final AtomicBoolean initialized = new AtomicBoolean();

  private List<String> welcomeFiles;

  private Map<Class<Throwable>, String> errorPageLocations;

  private String formLoginPage;

  private String formErrorPage;

  private Map<String, Set<String>> securityConstraints;

  private int sessionTimeout;

  private void init() {
    if (!initialized.get() && hasContext()) {
      init(getServletContext());
    }
  }

  public WebXml init(ServletContext servletContext) {
    if (servletContext != null && !initialized.getAndSet(true)) {
      try {
        Element webXml = loadWebXml(servletContext).getDocumentElement();
        XPath xpath = XPathFactory.newInstance().newXPath();
        welcomeFiles = parseWelcomeFiles(webXml, xpath);
        errorPageLocations = parseErrorPageLocations(webXml, xpath);
        formLoginPage = parseFormLoginPage(webXml, xpath);
        formErrorPage = parseFormErrorPage(webXml, xpath);
        securityConstraints = parseSecurityConstraints(webXml, xpath);
        sessionTimeout = parseSessionTimeout(webXml, xpath);
      } catch (Exception e) {
        initialized.set(false);
        logger.log(Level.SEVERE, LOG_INITIALIZATION_ERROR, e);
        throw new UnsupportedOperationException(e);
      }
    }
    return this;
  }

  public String findErrorPageLocation(Throwable exception) {
    checkInitialized();
    String location = null;
    for (Class<?> cls = exception.getClass(); cls != null && location == null; cls = cls.getSuperclass()) {
      location = errorPageLocations.get(cls);
    }
    return (location == null) ? errorPageLocations.get(null) : location;
  }

  public boolean isAccessAllowed(String url, String role) {
    checkInitialized();
    if (url.charAt(0) != ('/')) {
      throw new IllegalArgumentException(String.format(ERROR_URL_MUST_START_WITH_SLASH, url));
    }
    String uri = url;
    if (url.length() > 1 && url.charAt(url.length() - 1) == '/') {
      uri = url.substring(0, url.length() - 1);
    }
    Set<String> roles = findExactMatchRoles(uri);
    if (roles == null) {
      roles = findPrefixMatchRoles(uri);
    }
    if (roles == null) {
      roles = findSuffixMatchRoles(uri);
    }
    return isRoleMatch(roles, role);
  }

  private Set<String> findExactMatchRoles(String url) {
    for (Entry<String, Set<String>> entry : securityConstraints.entrySet()) {
      if (isExactMatch(entry.getKey(), url)) {
        return entry.getValue();
      }
    }
    return null;
  }

  private Set<String> findPrefixMatchRoles(String url) {
    for (String path = url, urlMatch = ""; !path.isEmpty(); path = path.substring(0, path.lastIndexOf('/'))) {
      Set<String> roles = null;
      for (Entry<String, Set<String>> entry : securityConstraints.entrySet()) {
        if (urlMatch.length() < entry.getKey().length() && isPrefixMatch(entry.getKey(), path)) {
          urlMatch = entry.getKey();
          roles = entry.getValue();
        }
      }
      if (roles != null) {
        return roles;
      }
    }
    return null;
  }

  private Set<String> findSuffixMatchRoles(String url) {
    if (url.contains(".")) {
      for (Entry<String, Set<String>> entry : securityConstraints.entrySet()) {
        if (isSuffixMatch(url, entry.getKey())) {
          return entry.getValue();
        }
      }
    }
    return null;
  }

  private static boolean isExactMatch(String urlPattern, String url) {
    return url.equals(urlPattern.endsWith("/*") ? urlPattern.substring(0, urlPattern.length() - 2) : urlPattern);
  }

  private static boolean isPrefixMatch(String urlPattern, String url) {
    return urlPattern.endsWith("/*") ? url.startsWith(urlPattern.substring(0, urlPattern.length() - 2)) : false;
  }

  private static boolean isSuffixMatch(String urlPattern, String url) {
    return urlPattern.startsWith("*.") ? url.endsWith(urlPattern.substring(1)) : false;
  }

  private static boolean isRoleMatch(Set<String> roles, String role) {
    return roles == null || roles.contains(role) || (role != null && roles.contains("*"));
  }

  public List<String> getWelcomeFiles() {
    checkInitialized();
    return welcomeFiles;
  }

  public Map<Class<Throwable>, String> getErrorPageLocations() {
    checkInitialized();
    return errorPageLocations;
  }

  public String getFormLoginPage() {
    checkInitialized();
    return formLoginPage;
  }

  public String getFormErrorPage() {
    checkInitialized();
    return formErrorPage;
  }

  public Map<String, Set<String>> getSecurityConstraints() {
    checkInitialized();
    return securityConstraints;
  }

  public int getSessionTimeout() {
    checkInitialized();
    return sessionTimeout;
  }

  private void checkInitialized() {
    init();
    if (!initialized.get()) {
      throw new IllegalStateException(ERROR_NOT_INITIALIZED);
    }
  }

  private static Document loadWebXml(ServletContext context) throws IOException, SAXException {
    List<URL> webXmlURLs = new ArrayList<URL>();
    webXmlURLs.add(context.getResource(WEB_XML));
    webXmlURLs.addAll(Collections.list(Thread.currentThread().getContextClassLoader().getResources(WEB_FRAGMENT_XML)));
    return createDocument(webXmlURLs);
  }

  private static List<String> parseWelcomeFiles(Element webXml, XPath xpath) throws XPathExpressionException {
    NodeList welcomeFileList = getNodeList(webXml, xpath, XPATH_WELCOME_FILE);
    List<String> welcomeFiles = new ArrayList<String>(welcomeFileList.getLength());
    for (int i = 0; i < welcomeFileList.getLength(); i++) {
      welcomeFiles.add(getTextContent(welcomeFileList.item(i)));
    }
    return Collections.unmodifiableList(welcomeFiles);
  }

  @SuppressWarnings(value = { "unchecked" }) private static Map<Class<Throwable>, String> parseErrorPageLocations(Element webXml, XPath xpath) throws XPathExpressionException, ClassNotFoundException {
    Map<Class<Throwable>, String> errorPageLocations = new HashMap<Class<Throwable>, String>();
    NodeList exceptionTypes = getNodeList(webXml, xpath, XPATH_EXCEPTION_TYPE);
    for (int i = 0; i < exceptionTypes.getLength(); i++) {
      Node node = exceptionTypes.item(i);
      Class<Throwable> exceptionClass = (Class<Throwable>) Class.forName(getTextContent(node));
      String exceptionLocation = xpath.compile(XPATH_LOCATION).evaluate(node.getParentNode()).trim();
      Class<Throwable> key = (exceptionClass == Throwable.class) ? null : exceptionClass;
      if (!errorPageLocations.containsKey(key)) {
        errorPageLocations.put(key, exceptionLocation);
      }
    }
    if (!errorPageLocations.containsKey(null)) {
      String defaultLocation = xpath.compile(XPATH_ERROR_PAGE_500_LOCATION).evaluate(webXml).trim();
      if (isEmpty(defaultLocation)) {
        defaultLocation = xpath.compile(XPATH_ERROR_PAGE_DEFAULT_LOCATION).evaluate(webXml).trim();
      }
      if (!isEmpty(defaultLocation)) {
        errorPageLocations.put(null, defaultLocation);
      }
    }
    return Collections.unmodifiableMap(errorPageLocations);
  }

  private static String parseFormLoginPage(Element webXml, XPath xpath) throws XPathExpressionException {
    String formLoginPage = xpath.compile(XPATH_FORM_LOGIN_PAGE).evaluate(webXml).trim();
    return isEmpty(formLoginPage) ? null : formLoginPage;
  }

  private static String parseFormErrorPage(Element webXml, XPath xpath) throws XPathExpressionException {
    String formErrorPage = xpath.compile(XPATH_FORM_ERROR_PAGE).evaluate(webXml).trim();
    return isEmpty(formErrorPage) ? null : formErrorPage;
  }

  private static Map<String, Set<String>> parseSecurityConstraints(Element webXml, XPath xpath) throws XPathExpressionException {
    Map<String, Set<String>> securityConstraints = new LinkedHashMap<String, Set<String>>();
    NodeList constraints = getNodeList(webXml, xpath, XPATH_SECURITY_CONSTRAINT);
    for (int i = 0; i < constraints.getLength(); i++) {
      Node constraint = constraints.item(i);
      Set<String> roles = null;
      NodeList auth = getNodeList(constraint, xpath, XPATH_AUTH_CONSTRAINT);
      if (auth.getLength() > 0) {
        NodeList authRoles = getNodeList(constraint, xpath, XPATH_AUTH_CONSTRAINT_ROLE_NAME);
        roles = new HashSet<String>(authRoles.getLength());
        for (int j = 0; j < authRoles.getLength(); j++) {
          roles.add(getTextContent(authRoles.item(j)));
        }
        roles = Collections.unmodifiableSet(roles);
      }
      NodeList urlPatterns = getNodeList(constraint, xpath, XPATH_WEB_RESOURCE_URL_PATTERN);
      for (int j = 0; j < urlPatterns.getLength(); j++) {
        String urlPattern = getTextContent(urlPatterns.item(j));
        securityConstraints.put(urlPattern, roles);
      }
    }
    return Collections.unmodifiableMap(securityConstraints);
  }

  private static int parseSessionTimeout(Element webXml, XPath xpath) throws XPathExpressionException {
    String sessionTimeout = xpath.compile(XPATH_SESSION_TIMEOUT).evaluate(webXml).trim();
    return isNumber(sessionTimeout) ? Integer.parseInt(sessionTimeout) : -1;
  }
}