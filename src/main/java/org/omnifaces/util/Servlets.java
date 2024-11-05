package org.omnifaces.util;
import static java.util.regex.Pattern.quote;
import static javax.faces.application.ProjectStage.Development;
import static javax.faces.application.ProjectStage.PROJECT_STAGE_JNDI_NAME;
import static javax.faces.application.ProjectStage.PROJECT_STAGE_PARAM_NAME;
import static org.omnifaces.util.JNDI.lookup;
import static org.omnifaces.util.Utils.UTF_8;
import static org.omnifaces.util.Utils.decodeURL;
import static org.omnifaces.util.Utils.encodeURL;
import static org.omnifaces.util.Utils.isEmpty;
import static org.omnifaces.util.Utils.startsWithOneOf;
import static org.omnifaces.util.Utils.unmodifiableSet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.faces.application.Application;
import javax.faces.application.ResourceHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import org.omnifaces.component.ParamHolder;

public final class Servlets {
  private static final Set<String> FACES_AJAX_HEADERS = unmodifiableSet("partial/ajax", "partial/process");

  private static final String FACES_AJAX_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<partial-response><redirect url=\"%s\"></redirect></partial-response>";

  private static Boolean facesDevelopment;

  private Servlets() {
  }

  public static String getRequestHostname(HttpServletRequest request) {
    try {
      return new URL(request.getRequestURL().toString()).getHost();
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static String getRequestDomainURL(HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    return url.substring(0, url.length() - request.getRequestURI().length());
  }

  public static String getRequestBaseURL(HttpServletRequest request) {
    return getRequestDomainURL(request) + request.getContextPath() + "/";
  }

  public static String getRequestRelativeURI(HttpServletRequest request) {
    return request.getRequestURI().substring(request.getContextPath().length());
  }

  public static String getRequestRelativeURIWithoutPathParameters(HttpServletRequest request) {
    return request.getPathInfo() == null ? request.getServletPath() : request.getServletPath() + request.getPathInfo();
  }

  public static String getRequestURLWithQueryString(HttpServletRequest request) {
    StringBuffer requestURL = request.getRequestURL();
    String queryString = request.getQueryString();
    return (queryString == null) ? requestURL.toString() : requestURL.append('?').append(queryString).toString();
  }

  public static String getRequestURIWithQueryString(HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    String queryString = request.getQueryString();
    return (queryString == null) ? requestURI : (requestURI + "?" + queryString);
  }

  public static Map<String, List<String>> getRequestQueryStringMap(HttpServletRequest request) {
    String queryString = request.getQueryString();
    if (isEmpty(queryString)) {
      return new LinkedHashMap<>(0);
    }
    return toParameterMap(queryString);
  }

  public static String getForwardRequestURI(HttpServletRequest request) {
    return (String) request.getAttribute("javax.servlet.forward.request_uri");
  }

  public static String getForwardRequestQueryString(HttpServletRequest request) {
    return (String) request.getAttribute("javax.servlet.forward.query_string");
  }

  public static String getForwardRequestURIWithQueryString(HttpServletRequest request) {
    String requestURI = getForwardRequestURI(request);
    String queryString = getForwardRequestQueryString(request);
    return (queryString == null) ? requestURI : (requestURI + "?" + queryString);
  }

  public static Map<String, List<String>> toParameterMap(String queryString) {
    String[] parameters = queryString.split(quote("&"));
    Map<String, List<String>> parameterMap = new LinkedHashMap<String, List<String>>(parameters.length);
    for (String parameter : parameters) {
      if (parameter.contains("=")) {
        String[] pair = parameter.split(quote("="));
        String key = decodeURL(pair[0]);
        String value = (pair.length > 1 && !isEmpty(pair[1])) ? decodeURL(pair[1]) : "";
        List<String> values = parameterMap.get(key);
        if (values == null) {
          values = new ArrayList<String>(1);
          parameterMap.put(key, values);
        }
        values.add(value);
      }
    }
    return parameterMap;
  }

  public static String toQueryString(Map<String, List<String>> parameterMap) {
    StringBuilder queryString = new StringBuilder();
    for (Entry<String, List<String>> entry : parameterMap.entrySet()) {
      if (isEmpty(entry.getKey())) {
        continue;
      }
      String name = encodeURL(entry.getKey());
      for (String value : entry.getValue()) {
        if (value == null) {
          continue;
        }
        if (queryString.length() > 0) {
          queryString.append("&");
        }
        queryString.append(name).append("=").append(encodeURL(value));
      }
    }
    return queryString.toString();
  }

  public static String getRequestCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          return decodeURL(cookie.getValue());
        }
      }
    }
    return null;
  }

  public static void addResponseCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge) {
    addResponseCookie(request, response, name, value, getRequestHostname(request), null, maxAge);
  }

  public static void addResponseCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String path, int maxAge) {
    addResponseCookie(request, response, name, value, getRequestHostname(request), path, maxAge);
  }

  public static void addResponseCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String domain, String path, int maxAge) {
    Cookie cookie = new Cookie(name, encodeURL(value));
    if (domain != null && !domain.equals("localhost")) {
      cookie.setDomain(domain);
    }
    if (path != null) {
      cookie.setPath(path);
    }
    cookie.setMaxAge(maxAge);
    cookie.setSecure(request.isSecure());
    response.addCookie(cookie);
  }

  public static void removeResponseCookie(HttpServletRequest request, HttpServletResponse response, String name, String path) {
    addResponseCookie(request, response, name, null, path, 0);
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getApplicationAttribute(ServletContext context, String name) {
    return (T) context.getAttribute(name);
  }

  public static boolean isFacesAjaxRequest(HttpServletRequest request) {
    return FACES_AJAX_HEADERS.contains(request.getHeader("Faces-Request"));
  }

  public static boolean isFacesResourceRequest(HttpServletRequest request) {
    return request.getRequestURI().startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER + "/");
  }

  public static boolean isFacesDevelopment(ServletContext context) {
    if (facesDevelopment != null) {
      return facesDevelopment;
    }
    String projectStage = null;
    try {
      projectStage = lookup(PROJECT_STAGE_JNDI_NAME);
    } catch (IllegalStateException ignore) {
      return false;
    }
    if (projectStage == null) {
      projectStage = context.getInitParameter(PROJECT_STAGE_PARAM_NAME);
    }
    facesDevelopment = Development.name().equals(projectStage);
    return facesDevelopment;
  }

  public static void facesRedirect(HttpServletRequest request, HttpServletResponse response, String url, String... paramValues) throws IOException {
    String redirectURL = prepareRedirectURL(request, url, paramValues);
    if (isFacesAjaxRequest(request)) {
      setNoCacheHeaders(response);
      response.setContentType("text/xml");
      response.setCharacterEncoding(UTF_8.name());
      response.getWriter().printf(FACES_AJAX_REDIRECT_XML, redirectURL);
    } else {
      response.sendRedirect(redirectURL);
    }
  }

  static String prepareRedirectURL(HttpServletRequest request, String url, String... paramValues) {
    String redirectURL = url;
    if (!startsWithOneOf(url, "http://", "https://", "/")) {
      redirectURL = request.getContextPath() + "/" + url;
    }
    if (isEmpty(paramValues)) {
      return redirectURL;
    }
    Object[] encodedParams = new Object[paramValues.length];
    for (int i = 0; i < paramValues.length; i++) {
      encodedParams[i] = encodeURL(paramValues[i]);
    }
    return String.format(redirectURL, encodedParams);
  }

  public static String toQueryString(List<ParamHolder> params) {
    StringBuilder queryString = new StringBuilder();
    for (ParamHolder param : params) {
      if (isEmpty(param.getName())) {
        continue;
      }
      Object value = param.getValue();
      if (value == null) {
        continue;
      }
      if (queryString.length() > 0) {
        queryString.append("&");
      }
      queryString.append(encodeURL(param.getName())).append("=").append(encodeURL(value.toString()));
    }
    return queryString.toString();
  }

  public static void setCacheHeaders(HttpServletResponse response, long expires) {
    if (expires > 0) {
      response.setHeader("Cache-Control", "public,max-age=" + expires + ",must-revalidate");
      response.setDateHeader("Expires", System.currentTimeMillis() + SECONDS.toMillis(expires));
      response.setHeader("Pragma", "");
    } else {
      setNoCacheHeaders(response);
    }
  }

  public static void setNoCacheHeaders(HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
    response.setDateHeader("Expires", 0);
    response.setHeader("Pragma", "no-cache");
  }
}