package org.omnifaces.util;
import static javax.faces.FactoryFinder.APPLICATION_FACTORY;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.faces.context.Flash;
import javax.faces.context.PartialViewContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewMetadata;
import javax.faces.view.facelets.FaceletContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.omnifaces.component.ParamHolder;
import javax.faces.render.RenderKit;
import org.omnifaces.config.FacesConfigXml;

public final class Faces {
  private Faces() {
  }

  public static FacesContext getContext() {
    return FacesContext.getCurrentInstance();
  }

  public static FacesContext getContext(ELContext elContext) {
    return (FacesContext) elContext.getContext(FacesContext.class);
  }

  public static void setContext(FacesContext context) {
    FacesContextSetter.setCurrentInstance(context);
  }

  private abstract static class FacesContextSetter extends FacesContext {
    protected static void setCurrentInstance(FacesContext context) {
      FacesContext.setCurrentInstance(context);
    }
  }

  public static boolean hasContext() {
    return getContext() != null;
  }

  public static ExternalContext getExternalContext() {
    return getContext().getExternalContext();
  }

  public static Application getApplication() {
    return getContext().getApplication();
  }

  public static Application getApplicationFromFactory() {
    return ((ApplicationFactory) FactoryFinder.getFactory(APPLICATION_FACTORY)).getApplication();
  }

  public static String getImplInfo() {
    Package jsfPackage = FacesContext.class.getPackage();
    return jsfPackage.getImplementationTitle() + " " + jsfPackage.getImplementationVersion();
  }

  public static String getServerInfo() {
    return FacesLocal.getServerInfo(getContext());
  }

  public static boolean isDevelopment() {
    return FacesLocal.isDevelopment(getContext());
  }

  public static String getMapping() {
    return FacesLocal.getMapping(getContext());
  }

  public static boolean isPrefixMapping() {
    return isPrefixMapping(getMapping());
  }

  public static boolean isPrefixMapping(String mapping) {
    return (mapping.charAt(0) == '/');
  }

  public static PhaseId getCurrentPhaseId() {
    return getContext().getCurrentPhaseId();
  }

  public static void validationFailed() {
    getContext().validationFailed();
  }

  public static boolean isValidationFailed() {
    return getContext().isValidationFailed();
  }

  public static ELContext getELContext() {
    return getContext().getELContext();
  }

  public static <T extends java.lang.Object> T evaluateExpressionGet(String expression) {
    return FacesLocal.evaluateExpressionGet(getContext(), expression);
  }

  public static void evaluateExpressionSet(String expression, Object value) {
    FacesLocal.evaluateExpressionSet(getContext(), expression, value);
  }

  public static <T extends java.lang.Object> T resolveExpressionGet(Object base, String property) {
    return FacesLocal.resolveExpressionGet(getContext(), base, property);
  }

  public static void resolveExpressionSet(Object base, String property, Object value) {
    FacesLocal.resolveExpressionSet(getContext(), base, property, value);
  }

  public static <T extends java.lang.Object> T getContextAttribute(String name) {
    return FacesLocal.getContextAttribute(getContext(), name);
  }

  public static void setContextAttribute(String name, Object value) {
    FacesLocal.setContextAttribute(getContext(), name, value);
  }

  public static UIViewRoot getViewRoot() {
    return getContext().getViewRoot();
  }

  public static void setViewRoot(String viewId) {
    FacesLocal.setViewRoot(getContext(), viewId);
  }

  public static String getViewId() {
    return FacesLocal.getViewId(getContext());
  }

  public static ViewDeclarationLanguage getViewDeclarationLanguage() {
    return FacesLocal.getViewDeclarationLanguage(getContext());
  }

  public static String normalizeViewId(String path) {
    return FacesLocal.normalizeViewId(getContext(), path);
  }

  public static Collection<UIViewParameter> getViewParameters() {
    return FacesLocal.getViewParameters(getContext());
  }

  public static Map<String, List<String>> getViewParameterMap() {
    return FacesLocal.getViewParameterMap(getContext());
  }

  public static Map<String, Object> getMetadataAttributes(String viewId) {
    return FacesLocal.getMetadataAttributes(getContext(), viewId);
  }

  public static Map<String, Object> getMetadataAttributes() {
    return FacesLocal.getMetadataAttributes(getContext());
  }

  public static <T extends java.lang.Object> T getMetadataAttribute(String viewId, String name) {
    return FacesLocal.getMetadataAttribute(getContext(), viewId, name);
  }

  public static <T extends java.lang.Object> T getMetadataAttribute(String name) {
    return FacesLocal.getMetadataAttribute(getContext(), name);
  }

  public static Locale getLocale() {
    return FacesLocal.getLocale(getContext());
  }

  public static Locale getDefaultLocale() {
    return FacesLocal.getDefaultLocale(getContext());
  }

  public static List<Locale> getSupportedLocales() {
    return FacesLocal.getSupportedLocales(getContext());
  }

  public static void setLocale(Locale locale) {
    FacesLocal.setLocale(getContext(), locale);
  }

  public static ResourceBundle getMessageBundle() {
    return FacesLocal.getMessageBundle(getContext());
  }

  public static ResourceBundle getResourceBundle(String var) {
    return FacesLocal.getResourceBundle(getContext(), var);
  }

  public static void navigate(String outcome) {
    FacesLocal.navigate(getContext(), outcome);
  }

  public static String getBookmarkableURL(Map<String, List<String>> params, boolean includeViewParams) {
    return FacesLocal.getBookmarkableURL(getContext(), params, includeViewParams);
  }

  public static String getBookmarkableURL(String viewId, Map<String, List<String>> params, boolean includeViewParams) {
    return FacesLocal.getBookmarkableURL(getContext(), viewId, params, includeViewParams);
  }

  public static String getBookmarkableURL(Collection<? extends ParamHolder> params, boolean includeViewParams) {
    return FacesLocal.getBookmarkableURL(getContext(), params, includeViewParams);
  }

  public static String getBookmarkableURL(String viewId, Collection<? extends ParamHolder> params, boolean includeViewParams) {
    return FacesLocal.getBookmarkableURL(getContext(), viewId, params, includeViewParams);
  }

  public static FaceletContext getFaceletContext() {
    return FacesLocal.getFaceletContext(getContext());
  }

  public static <T extends java.lang.Object> T getFaceletAttribute(String name) {
    return FacesLocal.getFaceletAttribute(getContext(), name);
  }

  public static void setFaceletAttribute(String name, Object value) {
    FacesLocal.setFaceletAttribute(getContext(), name, value);
  }

  public static HttpServletRequest getRequest() {
    return FacesLocal.getRequest(getContext());
  }

  public static boolean isAjaxRequest() {
    return FacesLocal.isAjaxRequest(getContext());
  }

  public static boolean isPostback() {
    return getContext().isPostback();
  }

  public static Map<String, String> getRequestParameterMap() {
    return FacesLocal.getRequestParameterMap(getContext());
  }

  public static String getRequestParameter(String name) {
    return FacesLocal.getRequestParameterMap(getContext()).get(name);
  }

  public static Map<String, String[]> getRequestParameterValuesMap() {
    return FacesLocal.getRequestParameterValuesMap(getContext());
  }

  public static String[] getRequestParameterValues(String name) {
    return FacesLocal.getRequestParameterValuesMap(getContext()).get(name);
  }

  public static Map<String, String> getRequestHeaderMap() {
    return FacesLocal.getRequestHeaderMap(getContext());
  }

  public static String getRequestHeader(String name) {
    return FacesLocal.getRequestHeaderMap(getContext()).get(name);
  }

  public static Map<String, String[]> getRequestHeaderValuesMap() {
    return FacesLocal.getRequestHeaderValuesMap(getContext());
  }

  public static String[] getRequestHeaderValues(String name) {
    return FacesLocal.getRequestHeaderValuesMap(getContext()).get(name);
  }

  public static String getRequestContextPath() {
    return FacesLocal.getRequestContextPath(getContext());
  }

  public static String getRequestServletPath() {
    return FacesLocal.getRequestServletPath(getContext());
  }

  public static String getRequestPathInfo() {
    return FacesLocal.getRequestPathInfo(getContext());
  }

  public static String getRequestHostname() {
    return FacesLocal.getRequestHostname(getContext());
  }

  public static String getRequestBaseURL() {
    return FacesLocal.getRequestBaseURL(getContext());
  }

  public static String getRequestDomainURL() {
    return FacesLocal.getRequestDomainURL(getContext());
  }

  public static String getRequestURL() {
    return FacesLocal.getRequestURL(getContext());
  }

  public static String getRequestURI() {
    return FacesLocal.getRequestURI(getContext());
  }

  public static String getRequestQueryString() {
    return FacesLocal.getRequestQueryString(getContext());
  }

  public static Map<String, List<String>> getRequestQueryStringMap() {
    return FacesLocal.getRequestQueryStringMap(getContext());
  }

  public static String getRequestURLWithQueryString() {
    return FacesLocal.getRequestURLWithQueryString(getContext());
  }

  public static String getRequestURIWithQueryString() {
    return FacesLocal.getRequestURIWithQueryString(getContext());
  }

  public static String getForwardRequestURI() {
    return FacesLocal.getForwardRequestURI(getContext());
  }

  public static String getForwardRequestQueryString() {
    return FacesLocal.getForwardRequestQueryString(getContext());
  }

  public static String getForwardRequestURIWithQueryString() {
    return FacesLocal.getForwardRequestURIWithQueryString(getContext());
  }

  public static String getRemoteAddr() {
    return FacesLocal.getRemoteAddr(getContext());
  }

  public static HttpServletResponse getResponse() {
    return FacesLocal.getResponse(getContext());
  }

  public static int getResponseBufferSize() {
    return FacesLocal.getResponseBufferSize(getContext());
  }

  public static String getResponseCharacterEncoding() {
    return FacesLocal.getResponseCharacterEncoding(getContext());
  }

  public static void setResponseStatus(int status) {
    FacesLocal.setResponseStatus(getContext(), status);
  }

  public static void redirect(String url, String... paramValues) throws IOException {
    FacesLocal.redirect(getContext(), url, paramValues);
  }

  public static void redirectPermanent(String url, String... paramValues) {
    FacesLocal.redirectPermanent(getContext(), url, paramValues);
  }

  public static void responseSendError(int status, String message) throws IOException {
    FacesLocal.responseSendError(getContext(), status, message);
  }

  public static void addResponseHeader(String name, String value) {
    FacesLocal.addResponseHeader(getContext(), name, value);
  }

  public static boolean isResponseCommitted() {
    return FacesLocal.isResponseCommitted(getContext());
  }

  public static void responseReset() {
    FacesLocal.responseReset(getContext());
  }

  public static void renderResponse() {
    getContext().renderResponse();
  }

  public static boolean isRenderResponse() {
    return FacesLocal.isRenderResponse(getContext());
  }

  public static void responseComplete() {
    getContext().responseComplete();
  }

  public static boolean isResponseComplete() {
    return getContext().getResponseComplete();
  }

  public static void login(String username, String password) throws ServletException {
    FacesLocal.login(getContext(), username, password);
  }

  public static boolean authenticate() throws ServletException, IOException {
    return FacesLocal.authenticate(getContext());
  }

  public static void logout() throws ServletException {
    FacesLocal.logout(getContext());
  }

  public static String getRemoteUser() {
    return FacesLocal.getRemoteUser(getContext());
  }

  public static boolean isUserInRole(String role) {
    return FacesLocal.isUserInRole(getContext(), role);
  }

  public static String getRequestCookie(String name) {
    return FacesLocal.getRequestCookie(getContext(), name);
  }

  public static void addResponseCookie(String name, String value, int maxAge) {
    FacesLocal.addResponseCookie(getContext(), name, value, maxAge);
  }

  public static void addResponseCookie(String name, String value, String path, int maxAge) {
    FacesLocal.addResponseCookie(getContext(), name, value, path, maxAge);
  }

  public static void addResponseCookie(String name, String value, String domain, String path, int maxAge) {
    FacesLocal.addResponseCookie(getContext(), name, value, domain, path, maxAge);
  }

  public static void removeResponseCookie(String name, String path) {
    FacesLocal.removeResponseCookie(getContext(), name, path);
  }

  public static HttpSession getSession() {
    return FacesLocal.getSession(getContext());
  }

  public static HttpSession getSession(boolean create) {
    return FacesLocal.getSession(getContext(), create);
  }

  public static String getSessionId() {
    return FacesLocal.getSessionId(getContext());
  }

  public static void invalidateSession() {
    FacesLocal.invalidateSession(getContext());
  }

  public static boolean hasSession() {
    return FacesLocal.hasSession(getContext());
  }

  public static boolean isSessionNew() {
    return FacesLocal.isSessionNew(getContext());
  }

  public static long getSessionCreationTime() {
    return FacesLocal.getSessionCreationTime(getContext());
  }

  public static long getSessionLastAccessedTime() {
    return FacesLocal.getSessionLastAccessedTime(getContext());
  }

  public static int getSessionMaxInactiveInterval() {
    return FacesLocal.getSessionMaxInactiveInterval(getContext());
  }

  public static void setSessionMaxInactiveInterval(int seconds) {
    FacesLocal.setSessionMaxInactiveInterval(getContext(), seconds);
  }

  public static boolean hasSessionTimedOut() {
    return FacesLocal.hasSessionTimedOut(getContext());
  }

  public static ServletContext getServletContext() {
    return FacesLocal.getServletContext(getContext());
  }

  public static Map<String, String> getInitParameterMap() {
    return FacesLocal.getInitParameterMap(getContext());
  }

  public static String getInitParameter(String name) {
    return FacesLocal.getInitParameter(getContext(), name);
  }

  public static String getMimeType(String name) {
    return FacesLocal.getMimeType(getContext(), name);
  }

  public static URL getResource(String path) throws MalformedURLException {
    return FacesLocal.getResource(getContext(), path);
  }

  public static InputStream getResourceAsStream(String path) {
    return FacesLocal.getResourceAsStream(getContext(), path);
  }

  public static Set<String> getResourcePaths(String path) {
    return FacesLocal.getResourcePaths(getContext(), path);
  }

  public static String getRealPath(String webContentPath) {
    return FacesLocal.getRealPath(getContext(), webContentPath);
  }

  public static Map<String, Object> getRequestMap() {
    return FacesLocal.getRequestMap(getContext());
  }

  public static <T extends java.lang.Object> T getRequestAttribute(String name) {
    return FacesLocal.getRequestAttribute(getContext(), name);
  }

  public static void setRequestAttribute(String name, Object value) {
    FacesLocal.setRequestAttribute(getContext(), name, value);
  }

  public static <T extends java.lang.Object> T removeRequestAttribute(String name) {
    return FacesLocal.removeRequestAttribute(getContext(), name);
  }

  public static Flash getFlash() {
    return FacesLocal.getFlash(getContext());
  }

  public static <T extends java.lang.Object> T getFlashAttribute(String name) {
    return FacesLocal.getFlashAttribute(getContext(), name);
  }

  public static void setFlashAttribute(String name, Object value) {
    FacesLocal.setFlashAttribute(getContext(), name, value);
  }

  public static <T extends java.lang.Object> T removeFlashAttribute(String name) {
    return FacesLocal.removeFlashAttribute(getContext(), name);
  }

  public static Map<String, Object> getViewMap() {
    return FacesLocal.getViewMap(getContext());
  }

  public static <T extends java.lang.Object> T getViewAttribute(String name) {
    return FacesLocal.getViewAttribute(getContext(), name);
  }

  public static void setViewAttribute(String name, Object value) {
    FacesLocal.setViewAttribute(getContext(), name, value);
  }

  public static <T extends java.lang.Object> T removeViewAttribute(String name) {
    return FacesLocal.removeViewAttribute(getContext(), name);
  }

  public static Map<String, Object> getSessionMap() {
    return FacesLocal.getSessionMap(getContext());
  }

  public static <T extends java.lang.Object> T getSessionAttribute(String name) {
    return FacesLocal.getSessionAttribute(getContext(), name);
  }

  public static void setSessionAttribute(String name, Object value) {
    FacesLocal.setSessionAttribute(getContext(), name, value);
  }

  public static <T extends java.lang.Object> T removeSessionAttribute(String name) {
    return FacesLocal.removeSessionAttribute(getContext(), name);
  }

  public static Map<String, Object> getApplicationMap() {
    return FacesLocal.getApplicationMap(getContext());
  }

  public static <T extends java.lang.Object> T getApplicationAttribute(String name) {
    return FacesLocal.getApplicationAttribute(getContext(), name);
  }

  public static void setApplicationAttribute(String name, Object value) {
    FacesLocal.setApplicationAttribute(getContext(), name, value);
  }

  public static <T extends java.lang.Object> T removeApplicationAttribute(String name) {
    return FacesLocal.removeApplicationAttribute(getContext(), name);
  }

  public static void sendFile(File file, boolean attachment) throws IOException {
    FacesLocal.sendFile(getContext(), file, attachment);
  }

  public static void sendFile(byte[] content, String filename, boolean attachment) throws IOException {
    FacesLocal.sendFile(getContext(), content, filename, attachment);
  }

  public static void sendFile(InputStream content, String filename, boolean attachment) throws IOException {
    FacesLocal.sendFile(getContext(), content, filename, attachment);
  }

  public static RenderKit getRenderKit() {
    return FacesLocal.getRenderKit(getContext());
  }

  public static void refresh() throws IOException {
    FacesLocal.refresh(getContext());
  }

  public static void refreshWithQueryString() throws IOException {
    FacesLocal.refreshWithQueryString(getContext());
  }
}