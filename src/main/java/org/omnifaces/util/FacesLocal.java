package org.omnifaces.util;
import static javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY;
import static org.omnifaces.util.Servlets.prepareRedirectURL;
import static org.omnifaces.util.Utils.encodeURL;
import static org.omnifaces.util.Utils.isAnyEmpty;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.ProjectStage;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewMetadata;
import javax.faces.view.facelets.FaceletContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.omnifaces.component.ParamHolder;
import static java.util.Arrays.asList;
import java.util.LinkedHashMap;
import javax.faces.FactoryFinder;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

public final class FacesLocal {
  private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

  private static final int DEFAULT_SENDFILE_BUFFER_SIZE = 10240;

  private static final String SENDFILE_HEADER = "%s;filename=\"%2$s\"; filename*=UTF-8\'\'%2$s";

  private static final String ERROR_NO_VIEW = "There is no view.";

  private static final String[] FACELET_CONTEXT_KEYS = { FaceletContext.FACELET_CONTEXT_KEY, "com.sun.faces.facelets.FACELET_CONTEXT", "javax.faces.FACELET_CONTEXT" };

  private FacesLocal() {
  }

  public static String getServerInfo(FacesContext context) {
    return getServletContext(context).getServerInfo();
  }

  public static boolean isDevelopment(FacesContext context) {
    return context.getApplication().getProjectStage() == ProjectStage.Development;
  }

  public static String getMapping(FacesContext context) {
    ExternalContext externalContext = context.getExternalContext();
    if (externalContext.getRequestPathInfo() == null) {
      String path = externalContext.getRequestServletPath();
      return path.substring(path.lastIndexOf('.'));
    } else {
      return externalContext.getRequestServletPath();
    }
  }

  public static boolean isPrefixMapping(FacesContext context) {
    return Faces.isPrefixMapping(getMapping(context));
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T evaluateExpressionGet(FacesContext context, String expression) {
    if (expression == null) {
      return null;
    }
    return (T) context.getApplication().evaluateExpressionGet(context, expression, Object.class);
  }

  public static void evaluateExpressionSet(FacesContext context, String expression, Object value) {
    ELContext elContext = context.getELContext();
    ValueExpression valueExpression = context.getApplication().getExpressionFactory().createValueExpression(elContext, expression, Object.class);
    valueExpression.setValue(elContext, value);
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T resolveExpressionGet(FacesContext context, Object base, String property) {
    ELResolver elResolver = context.getApplication().getELResolver();
    return (T) elResolver.getValue(context.getELContext(), base, property);
  }

  public static void resolveExpressionSet(FacesContext context, Object base, String property, Object value) {
    ELResolver elResolver = context.getApplication().getELResolver();
    elResolver.setValue(context.getELContext(), base, property, value);
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getContextAttribute(FacesContext context, String name) {
    return (T) context.getAttributes().get(name);
  }

  public static void setContextAttribute(FacesContext context, String name, Object value) {
    context.getAttributes().put(name, value);
  }

  public static void setViewRoot(FacesContext context, String viewId) {
    context.setViewRoot(context.getApplication().getViewHandler().createView(context, viewId));
  }

  public static String getViewId(FacesContext context) {
    UIViewRoot viewRoot = context.getViewRoot();
    return (viewRoot != null) ? viewRoot.getViewId() : null;
  }

  public static ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context) {
    return context.getApplication().getViewHandler().getViewDeclarationLanguage(context, context.getViewRoot().getViewId());
  }

  public static String normalizeViewId(FacesContext context, String path) {
    String mapping = getMapping(context);
    if (Faces.isPrefixMapping(mapping)) {
      if (path.startsWith(mapping)) {
        return path.substring(mapping.length());
      }
    } else {
      if (path.endsWith(mapping)) {
        return path.substring(0, path.lastIndexOf('.')) + Utils.coalesce(getInitParameter(context, ViewHandler.FACELETS_SUFFIX_PARAM_NAME), ViewHandler.DEFAULT_FACELETS_SUFFIX);
      }
    }
    return path;
  }

  public static Collection<UIViewParameter> getViewParameters(FacesContext context) {
    UIViewRoot viewRoot = context.getViewRoot();
    return (viewRoot != null) ? ViewMetadata.getViewParameters(viewRoot) : Collections.<UIViewParameter>emptyList();
  }

  public static Map<String, List<String>> getViewParameterMap(FacesContext context) {
    Collection<UIViewParameter> viewParameters = getViewParameters(context);
    if (viewParameters.isEmpty()) {
      return new LinkedHashMap<>(0);
    }
    Map<String, List<String>> parameterMap = new LinkedHashMap<String, List<String>>(viewParameters.size());
    for (UIViewParameter viewParameter : viewParameters) {
      String value = viewParameter.getStringValue(context);
      if (value != null) {
        parameterMap.put(viewParameter.getName(), asList(value));
      }
    }
    return parameterMap;
  }

  public static Map<String, Object> getMetadataAttributes(FacesContext context, String viewId) {
    ViewHandler viewHandler = context.getApplication().getViewHandler();
    ViewDeclarationLanguage vdl = viewHandler.getViewDeclarationLanguage(context, viewId);
    ViewMetadata metadata = vdl.getViewMetadata(context, viewId);
    return (metadata != null) ? metadata.createMetadataView(context).getAttributes() : Collections.<String, Object>emptyMap();
  }

  public static Map<String, Object> getMetadataAttributes(FacesContext context) {
    return context.getViewRoot().getAttributes();
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getMetadataAttribute(FacesContext context, String viewId, String name) {
    return (T) getMetadataAttributes(context, viewId).get(name);
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getMetadataAttribute(FacesContext context, String name) {
    return (T) getMetadataAttributes(context).get(name);
  }

  public static Locale getLocale(FacesContext context) {
    Locale locale = null;
    UIViewRoot viewRoot = context.getViewRoot();
    if (viewRoot != null) {
      locale = viewRoot.getLocale();
    }
    if (locale == null) {
      Locale clientLocale = context.getExternalContext().getRequestLocale();
      if (getSupportedLocales(context).contains(clientLocale)) {
        locale = clientLocale;
      }
    }
    if (locale == null) {
      locale = context.getApplication().getDefaultLocale();
    }
    if (locale == null) {
      locale = Locale.getDefault();
    }
    return locale;
  }

  public static Locale getDefaultLocale(FacesContext context) {
    return context.getApplication().getDefaultLocale();
  }

  public static List<Locale> getSupportedLocales(FacesContext context) {
    Application application = context.getApplication();
    List<Locale> supportedLocales = new ArrayList<Locale>();
    Locale defaultLocale = application.getDefaultLocale();
    if (defaultLocale != null) {
      supportedLocales.add(defaultLocale);
    }
    for (Iterator<Locale> iter = application.getSupportedLocales(); iter.hasNext(); ) {
      Locale supportedLocale = iter.next();
      if (!supportedLocale.equals(defaultLocale)) {
        supportedLocales.add(supportedLocale);
      }
    }
    return supportedLocales;
  }

  public static void setLocale(FacesContext context, Locale locale) {
    UIViewRoot viewRoot = context.getViewRoot();
    if (viewRoot == null) {
      throw new IllegalStateException(ERROR_NO_VIEW);
    }
    viewRoot.setLocale(locale);
  }

  public static ResourceBundle getMessageBundle(FacesContext context) {
    String messageBundle = context.getApplication().getMessageBundle();
    if (messageBundle == null) {
      return null;
    }
    return ResourceBundle.getBundle(messageBundle, getLocale(context));
  }

  public static ResourceBundle getResourceBundle(FacesContext context, String var) {
    return context.getApplication().getResourceBundle(context, var);
  }

  public static void navigate(FacesContext context, String outcome) {
    context.getApplication().getNavigationHandler().handleNavigation(context, null, outcome);
  }

  public static String getBookmarkableURL(FacesContext context, Map<String, List<String>> params, boolean includeViewParams) {
    String viewId = getViewId(context);
    if (viewId == null) {
      throw new IllegalStateException(ERROR_NO_VIEW);
    }
    return getBookmarkableURL(context, viewId, params, includeViewParams);
  }

  public static String getBookmarkableURL(FacesContext context, String viewId, Map<String, List<String>> params, boolean includeViewParams) {
    Map<String, List<String>> map = new HashMap<String, List<String>>();
    if (params != null) {
      for (Entry<String, List<String>> param : params.entrySet()) {
        addParamToMapIfNecessary(map, param.getKey(), param.getValue());
      }
    }
    return context.getApplication().getViewHandler().getBookmarkableURL(context, viewId, map, includeViewParams);
  }

  public static String getBookmarkableURL(FacesContext context, Collection<? extends ParamHolder> params, boolean includeViewParams) {
    String viewId = getViewId(context);
    if (viewId == null) {
      throw new IllegalStateException(ERROR_NO_VIEW);
    }
    return getBookmarkableURL(context, viewId, params, includeViewParams);
  }

  public static String getBookmarkableURL(FacesContext context, String viewId, Collection<? extends ParamHolder> params, boolean includeViewParams) {
    Map<String, List<String>> map = new HashMap<String, List<String>>();
    if (params != null) {
      for (ParamHolder param : params) {
        addParamToMapIfNecessary(map, param.getName(), param.getValue());
      }
    }
    return context.getApplication().getViewHandler().getBookmarkableURL(context, viewId, map, includeViewParams);
  }

  private static void addParamToMapIfNecessary(Map<String, List<String>> map, String name, Object value) {
    if (isAnyEmpty(name, value)) {
      return;
    }
    List<String> values = map.get(name);
    if (values == null) {
      values = new ArrayList<String>(1);
      map.put(name, values);
    }
    values.add(value.toString());
  }

  public static FaceletContext getFaceletContext(FacesContext context) {
    Map<Object, Object> contextAttributes = context.getAttributes();
    for (String key : FACELET_CONTEXT_KEYS) {
      FaceletContext faceletContext = (FaceletContext) contextAttributes.get(key);
      if (faceletContext != null) {
        return faceletContext;
      }
    }
    return null;
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getFaceletAttribute(FacesContext context, String name) {
    return (T) getFaceletContext(context).getAttribute(name);
  }

  public static void setFaceletAttribute(FacesContext context, String name, Object value) {
    getFaceletContext(context).setAttribute(name, value);
  }

  public static HttpServletRequest getRequest(FacesContext context) {
    return (HttpServletRequest) context.getExternalContext().getRequest();
  }

  public static boolean isAjaxRequest(FacesContext context) {
    return context.getPartialViewContext().isAjaxRequest();
  }

  public static Map<String, String> getRequestParameterMap(FacesContext context) {
    return context.getExternalContext().getRequestParameterMap();
  }

  public static String getRequestParameter(FacesContext context, String name) {
    return getRequestParameterMap(context).get(name);
  }

  public static Map<String, String[]> getRequestParameterValuesMap(FacesContext context) {
    return context.getExternalContext().getRequestParameterValuesMap();
  }

  public static String[] getRequestParameterValues(FacesContext context, String name) {
    return getRequestParameterValuesMap(context).get(name);
  }

  public static Map<String, String> getRequestHeaderMap(FacesContext context) {
    return context.getExternalContext().getRequestHeaderMap();
  }

  public static String getRequestHeader(FacesContext context, String name) {
    return getRequestHeaderMap(context).get(name);
  }

  public static Map<String, String[]> getRequestHeaderValuesMap(FacesContext context) {
    return context.getExternalContext().getRequestHeaderValuesMap();
  }

  public static String[] getRequestHeaderValues(FacesContext context, String name) {
    return getRequestHeaderValuesMap(context).get(name);
  }

  public static String getRequestContextPath(FacesContext context) {
    return context.getExternalContext().getRequestContextPath();
  }

  public static String getRequestServletPath(FacesContext context) {
    return context.getExternalContext().getRequestServletPath();
  }

  public static String getRequestPathInfo(FacesContext context) {
    return context.getExternalContext().getRequestPathInfo();
  }

  public static String getRequestHostname(FacesContext context) {
    return Servlets.getRequestHostname(getRequest(context));
  }

  public static String getRequestBaseURL(FacesContext context) {
    return Servlets.getRequestBaseURL(getRequest(context));
  }

  public static String getRequestDomainURL(FacesContext context) {
    return Servlets.getRequestDomainURL(getRequest(context));
  }

  public static String getRequestURL(FacesContext context) {
    return getRequest(context).getRequestURL().toString();
  }

  public static String getRequestURI(FacesContext context) {
    return getRequest(context).getRequestURI();
  }

  public static String getRequestQueryString(FacesContext context) {
    return getRequest(context).getQueryString();
  }

  public static Map<String, List<String>> getRequestQueryStringMap(FacesContext context) {
    return Servlets.getRequestQueryStringMap(getRequest(context));
  }

  public static String getRequestURLWithQueryString(FacesContext context) {
    return Servlets.getRequestURLWithQueryString(getRequest(context));
  }

  public static String getRequestURIWithQueryString(FacesContext context) {
    return Servlets.getRequestURIWithQueryString(getRequest(context));
  }

  public static String getForwardRequestURI(FacesContext context) {
    return Servlets.getForwardRequestURI(getRequest(context));
  }

  public static String getForwardRequestQueryString(FacesContext context) {
    return Servlets.getForwardRequestQueryString(getRequest(context));
  }

  public static String getForwardRequestURIWithQueryString(FacesContext context) {
    return Servlets.getForwardRequestURIWithQueryString(getRequest(context));
  }

  public static String getRemoteAddr(FacesContext context) {
    String forwardedFor = getRequestHeader(context, "X-Forwarded-For");
    if (!Utils.isEmpty(forwardedFor)) {
      return forwardedFor.split("\\s*,\\s*", 2)[0];
    }
    return getRequest(context).getRemoteAddr();
  }

  public static HttpServletResponse getResponse(FacesContext context) {
    return (HttpServletResponse) context.getExternalContext().getResponse();
  }

  public static int getResponseBufferSize(FacesContext context) {
    return context.getExternalContext().getResponseBufferSize();
  }

  public static String getResponseCharacterEncoding(FacesContext context) {
    return context.getExternalContext().getResponseCharacterEncoding();
  }

  public static void setResponseStatus(FacesContext context, int status) {
    context.getExternalContext().setResponseStatus(status);
  }

  public static void redirect(FacesContext context, String url, String... paramValues) throws IOException {
    ExternalContext externalContext = context.getExternalContext();
    externalContext.getFlash().setRedirect(true);
    externalContext.redirect(prepareRedirectURL(getRequest(context), url, paramValues));
  }

  public static void redirectPermanent(FacesContext context, String url, String... paramValues) {
    ExternalContext externalContext = context.getExternalContext();
    externalContext.getFlash().setRedirect(true);
    externalContext.setResponseStatus(SC_MOVED_PERMANENTLY);
    externalContext.setResponseHeader("Location", prepareRedirectURL(getRequest(context), url, paramValues));
    externalContext.setResponseHeader("Connection", "close");
    context.responseComplete();
  }

  public static void responseSendError(FacesContext context, int status, String message) throws IOException {
    context.getExternalContext().responseSendError(status, message);
    context.responseComplete();
    if (!Faces.hasContext()) {
      Faces.setContext(context);
    }
  }

  public static void addResponseHeader(FacesContext context, String name, String value) {
    context.getExternalContext().addResponseHeader(name, value);
  }

  public static boolean isResponseCommitted(FacesContext context) {
    return context.getExternalContext().isResponseCommitted();
  }

  public static void responseReset(FacesContext context) {
    context.getExternalContext().responseReset();
  }

  public static boolean isRenderResponse(FacesContext context) {
    return context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE;
  }

  public static void login(FacesContext context, String username, String password) throws ServletException {
    getRequest(context).login(username, password);
  }

  public static boolean authenticate(FacesContext context) throws ServletException, IOException {
    return getRequest(context).authenticate(getResponse(context));
  }

  public static void logout(FacesContext context) throws ServletException {
    getRequest(context).logout();
  }

  public static String getRemoteUser(FacesContext context) {
    return context.getExternalContext().getRemoteUser();
  }

  public static boolean isUserInRole(FacesContext context, String role) {
    return context.getExternalContext().isUserInRole(role);
  }

  public static String getRequestCookie(FacesContext context, String name) {
    Cookie cookie = (Cookie) context.getExternalContext().getRequestCookieMap().get(name);
    return (cookie != null) ? Utils.decodeURL(cookie.getValue()) : null;
  }

  public static void addResponseCookie(FacesContext context, String name, String value, int maxAge) {
    addResponseCookie(context, name, value, getRequestHostname(context), null, maxAge);
  }

  public static void addResponseCookie(FacesContext context, String name, String value, String path, int maxAge) {
    addResponseCookie(context, name, value, getRequestHostname(context), path, maxAge);
  }

  public static void addResponseCookie(FacesContext context, String name, String value, String domain, String path, int maxAge) {
    ExternalContext externalContext = context.getExternalContext();
    Map<String, Object> properties = new HashMap<String, Object>();
    if (domain != null && !domain.equals("localhost")) {
      properties.put("domain", domain);
    }
    if (path != null) {
      properties.put("path", path);
    }
    properties.put("maxAge", maxAge);
    properties.put("secure", ((HttpServletRequest) externalContext.getRequest()).isSecure());
    externalContext.addResponseCookie(name, encodeURL(value), properties);
  }

  public static void removeResponseCookie(FacesContext context, String name, String path) {
    addResponseCookie(context, name, null, path, 0);
  }

  public static HttpSession getSession(FacesContext context) {
    return getSession(context, true);
  }

  public static HttpSession getSession(FacesContext context, boolean create) {
    return (HttpSession) context.getExternalContext().getSession(create);
  }

  public static String getSessionId(FacesContext context) {
    HttpSession session = getSession(context, false);
    return (session != null) ? session.getId() : null;
  }

  public static void invalidateSession(FacesContext context) {
    context.getExternalContext().invalidateSession();
  }

  public static boolean hasSession(FacesContext context) {
    return getSession(context, false) != null;
  }

  public static boolean isSessionNew(FacesContext context) {
    HttpSession session = getSession(context, false);
    return (session != null && session.isNew());
  }

  public static long getSessionCreationTime(FacesContext context) {
    return getSession(context).getCreationTime();
  }

  public static long getSessionLastAccessedTime(FacesContext context) {
    return getSession(context).getLastAccessedTime();
  }

  public static int getSessionMaxInactiveInterval(FacesContext context) {
    return getSession(context).getMaxInactiveInterval();
  }

  public static void setSessionMaxInactiveInterval(FacesContext context, int seconds) {
    getSession(context).setMaxInactiveInterval(seconds);
  }

  public static boolean hasSessionTimedOut(FacesContext context) {
    HttpServletRequest request = getRequest(context);
    return request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid();
  }

  public static ServletContext getServletContext(FacesContext context) {
    return (ServletContext) context.getExternalContext().getContext();
  }

  @SuppressWarnings(value = { "unchecked" }) public static Map<String, String> getInitParameterMap(FacesContext context) {
    return context.getExternalContext().getInitParameterMap();
  }

  public static String getInitParameter(FacesContext context, String name) {
    return context.getExternalContext().getInitParameter(name);
  }

  public static String getMimeType(FacesContext context, String name) {
    String mimeType = context.getExternalContext().getMimeType(name);
    if (mimeType == null) {
      mimeType = DEFAULT_MIME_TYPE;
    }
    return mimeType;
  }

  public static URL getResource(FacesContext context, String path) throws MalformedURLException {
    return context.getExternalContext().getResource(path);
  }

  public static InputStream getResourceAsStream(FacesContext context, String path) {
    return context.getExternalContext().getResourceAsStream(path);
  }

  public static Set<String> getResourcePaths(FacesContext context, String path) {
    return context.getExternalContext().getResourcePaths(path);
  }

  public static String getRealPath(FacesContext context, String webContentPath) {
    return context.getExternalContext().getRealPath(webContentPath);
  }

  public static Map<String, Object> getRequestMap(FacesContext context) {
    return context.getExternalContext().getRequestMap();
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getRequestAttribute(FacesContext context, String name) {
    return (T) getRequestMap(context).get(name);
  }

  public static void setRequestAttribute(FacesContext context, String name, Object value) {
    getRequestMap(context).put(name, value);
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T removeRequestAttribute(FacesContext context, String name) {
    return (T) getRequestMap(context).remove(name);
  }

  public static Flash getFlash(FacesContext context) {
    return context.getExternalContext().getFlash();
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getFlashAttribute(FacesContext context, String name) {
    return (T) getFlash(context).get(name);
  }

  public static void setFlashAttribute(FacesContext context, String name, Object value) {
    getFlash(context).put(name, value);
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T removeFlashAttribute(FacesContext context, String name) {
    return (T) getFlash(context).remove(name);
  }

  public static Map<String, Object> getViewMap(FacesContext context) {
    return context.getViewRoot().getViewMap();
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getViewAttribute(FacesContext context, String name) {
    return (T) getViewMap(context).get(name);
  }

  public static void setViewAttribute(FacesContext context, String name, Object value) {
    getViewMap(context).put(name, value);
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T removeViewAttribute(FacesContext context, String name) {
    return (T) getViewMap(context).remove(name);
  }

  public static Map<String, Object> getSessionMap(FacesContext context) {
    return context.getExternalContext().getSessionMap();
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getSessionAttribute(FacesContext context, String name) {
    return (T) getSessionMap(context).get(name);
  }

  public static void setSessionAttribute(FacesContext context, String name, Object value) {
    getSessionMap(context).put(name, value);
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T removeSessionAttribute(FacesContext context, String name) {
    return (T) getSessionMap(context).remove(name);
  }

  public static Map<String, Object> getApplicationMap(FacesContext context) {
    return context.getExternalContext().getApplicationMap();
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getApplicationAttribute(FacesContext context, String name) {
    return (T) getApplicationMap(context).get(name);
  }

  public static void setApplicationAttribute(FacesContext context, String name, Object value) {
    getApplicationMap(context).put(name, value);
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T removeApplicationAttribute(FacesContext context, String name) {
    return (T) getApplicationMap(context).remove(name);
  }

  public static void sendFile(FacesContext context, File file, boolean attachment) throws IOException {
    sendFile(context, new FileInputStream(file), file.getName(), file.length(), attachment);
  }

  public static void sendFile(FacesContext context, byte[] content, String filename, boolean attachment) throws IOException {
    sendFile(context, new ByteArrayInputStream(content), filename, content.length, attachment);
  }

  public static void sendFile(FacesContext context, InputStream content, String filename, boolean attachment) throws IOException {
    sendFile(context, content, filename, -1, attachment);
  }

  private static void sendFile(FacesContext context, InputStream input, String filename, long contentLength, boolean attachment) throws IOException {
    ExternalContext externalContext = context.getExternalContext();
    externalContext.setResponseBufferSize(DEFAULT_SENDFILE_BUFFER_SIZE);
    externalContext.setResponseContentType(getMimeType(context, filename));
    externalContext.setResponseHeader("Content-Disposition", String.format(SENDFILE_HEADER, (attachment ? "attachment" : "inline"), encodeURL(filename)));
    if (((HttpServletRequest) externalContext.getRequest()).isSecure()) {
      externalContext.setResponseHeader("Cache-Control", "public");
      externalContext.setResponseHeader("Pragma", "public");
    }
    if (contentLength != -1) {
      externalContext.setResponseHeader("Content-Length", String.valueOf(contentLength));
    }
    long size = Utils.stream(input, externalContext.getResponseOutputStream());
    if (contentLength == -1 && !externalContext.isResponseCommitted()) {
      externalContext.setResponseHeader("Content-Length", String.valueOf(size));
    }
    context.responseComplete();
  }

  public static RenderKit getRenderKit(FacesContext context) {
    UIViewRoot view = context.getViewRoot();
    String renderKitId = (view != null) ? view.getRenderKitId() : context.getApplication().getViewHandler().calculateRenderKitId(context);
    return ((RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY)).getRenderKit(context, renderKitId);
  }

  public static void refresh(FacesContext context) throws IOException {
    redirect(context, getRequestURI(context));
  }

  public static void refreshWithQueryString(FacesContext context) throws IOException {
    redirect(context, getRequestURIWithQueryString(context));
  }
}