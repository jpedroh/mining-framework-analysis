package org.tynamo.security.internal.services.impl;
import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.web.util.WebUtils;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.internal.services.RequestImpl;
import org.apache.tapestry5.internal.services.ResponseImpl;
import org.apache.tapestry5.internal.services.TapestrySessionFactory;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ComponentEventLinkEncoder;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.LocalizationSetter;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.internal.services.LoginContextService;

public class LoginContextServiceImpl implements LoginContextService {
  protected final String loginPage;

  protected final String defaultSuccessPage;

  protected final String unauthorizedPage;

  protected final HttpServletRequest servletRequest;

  protected final HttpServletResponse servletResponse;

  protected final ComponentEventLinkEncoder linkEncoder;

  protected final TapestrySessionFactory sessionFactory;

  protected final RequestGlobals requestGlobals;

  protected final String requestEncoding;

  private final LinkSource linkSource;

  private final LocalizationSetter localizationSetter;

  public LoginContextServiceImpl(@Inject @Symbol(value = SecuritySymbols.SUCCESS_URL) String successUrl, @Inject @Symbol(value = SecuritySymbols.LOGIN_URL) String loginUrl, @Inject @Symbol(value = SecuritySymbols.UNAUTHORIZED_URL) String unauthorizedUrl, @Inject @Symbol(value = SymbolConstants.CHARSET) String requestEncoding, HttpServletRequest serlvetRequest, HttpServletResponse servletResponse, LocalizationSetter localizationSetter, LinkSource linkSource, ComponentEventLinkEncoder linkEncoder, TapestrySessionFactory sessionFactory, RequestGlobals requestGlobals) {
    this.servletRequest = serlvetRequest;
    this.servletResponse = servletResponse;
    this.linkSource = linkSource;
    this.linkEncoder = linkEncoder;
    this.sessionFactory = sessionFactory;
    this.requestGlobals = requestGlobals;
    this.localizationSetter = localizationSetter;
    this.requestEncoding = requestEncoding;
    this.loginPage = urlToPage(loginUrl);
    this.defaultSuccessPage = urlToPage(successUrl);
    this.unauthorizedPage = urlToPage(unauthorizedUrl);
  }

  @Override public String getLoginPage() {
    return loginPage;
  }

  @Override public String getSuccessPage() {
    return defaultSuccessPage;
  }

  @Override public String getUnauthorizedPage() {
    return unauthorizedPage;
  }

  @Override public String getLoginURL() {
    return getLoginPage();
  }

  @Override public String getSuccessURL() {
    return getSuccessPage();
  }

  @Override public String getUnauthorizedURL() {
    return getUnauthorizedPage();
  }

  private static String urlToPage(String url) {
    if (url.charAt(0) == '/') {
      url = url.substring(1);
    }
    return url;
  }

  @Override public String getLocalelessPathWithinApplication() {
    String path = WebUtils.getPathWithinApplication(servletRequest);
    String locale = getLocaleFromPath(path);
    return locale == null ? path : path.substring(locale.length() + 1);
  }

  @Override public String getLocaleFromPath(String path) {
    String[] split = path.substring(1).split("/");
    if (split.length > 1 && !"".equals(split[0])) {
      String possibleLocaleName = split[0];
      return localizationSetter.isSupportedLocaleName(possibleLocaleName) ? possibleLocaleName : null;
    }
    return null;
  }

  public void removeSavedRequest() {
    Cookie cookie = new Cookie(WebUtils.SAVED_REQUEST_KEY, null);
    cookie.setPath(getContextPath());
    cookie.setMaxAge(0);
    servletResponse.addCookie(cookie);
  }

  private Cookie createSavedRequestCookie(String contextPath) {
    String requestUri;
    final Request request = new RequestImpl(servletRequest, requestEncoding, sessionFactory);
    requestGlobals.storeRequestResponse(request, new ResponseImpl(servletRequest, servletResponse));
    Cookie cookie = new Cookie(WebUtils.SAVED_REQUEST_KEY, "");
    cookie.setPath(contextPath);
    if (!"GET".equalsIgnoreCase(servletRequest.getMethod())) {
      ComponentEventRequestParameters eventParameters = linkEncoder.decodeComponentEventRequest(request);
      if (eventParameters != null) {
        requestUri = createPageRenderLink(eventParameters);
      } else {
        cookie.setMaxAge(0);
        return cookie;
      }
    } else {
      ComponentEventRequestParameters eventParameters = linkEncoder.decodeComponentEventRequest(request);
      if (eventParameters != null) {
        requestUri = createPageRenderLink(eventParameters);
      } else {
        requestUri = WebUtils.getRequestUri(servletRequest);
        if (servletRequest.getQueryString() != null) {
          requestUri += "?" + servletRequest.getQueryString();
        }
      }
    }
    cookie.setValue(requestUri);
    return cookie;
  }

  private String createPageRenderLink(ComponentEventRequestParameters eventParameters) {
    EventContext eventContext = eventParameters.getPageActivationContext();
    Link link = linkSource.createPageRenderLink(eventParameters.getActivePageName(), true, (Object[]) eventContext.toStrings());
    return link.toRedirectURI();
  }

  private String getContextPath() {
    String contextPath = servletRequest.getContextPath();
    if ("".equals(contextPath)) {
      contextPath = "/";
    }
    return contextPath;
  }

  @Override public void saveRequest() {
    servletResponse.addCookie(createSavedRequestCookie(getContextPath()));
  }

  @Override @Deprecated public void saveRequest(String contextPath) {
    servletResponse.addCookie(createSavedRequestCookie(contextPath));
  }

  @Override public void redirectToSavedRequest(String fallbackUrl) throws IOException {
    Cookie[] cookies = servletRequest.getCookies();
    String requestUri = null;
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (WebUtils.SAVED_REQUEST_KEY.equals(cookie.getName())) {
          requestUri = cookie.getValue();
          cookie.setMaxAge(0);
          servletResponse.addCookie(cookie);
          break;
        }
      }
    }
    if (requestUri == null) {
      requestUri = fallbackUrl.startsWith(getContextPath()) ? fallbackUrl : getContextPath() + fallbackUrl;
    }
    servletResponse.setStatus(303);
    servletResponse.setHeader("Location", servletResponse.encodeRedirectURL(requestUri));
    servletResponse.flushBuffer();
  }
}