package com.woonoz.proxy.servlet;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpec;
import org.apache.http.message.BasicHeader;

public class UrlRewriterImpl implements UrlRewriter {
  private final URL targetServer;

  private final HttpServletRequest servletRequest;

  public UrlRewriterImpl(HttpServletRequest servletRequest, final URL targetServer) {
    this.targetServer = targetServer;
    this.servletRequest = servletRequest;
  }

  public String rewriteHost(final String host) throws URISyntaxException, MalformedURLException {
    URI hostAsUri = new URI("http://" + host);
    if (hostIsSameAsServletHost(hostAsUri)) {
      return getTargetHostString();
    } else {
      return host;
    }
  }

  public URI rewriteUri(URI url) throws URISyntaxException, MalformedURLException {
    if (requestedUrlPointsToServlet(url)) {
      final String targetPath = rewritePathIfNeeded(url.getPath());
      return URIUtils.createURI(targetServer.getProtocol(), targetServer.getHost(), targetServer.getPort(), targetPath, servletRequest.getQueryString(), null);
    } else {
      return url;
    }
  }

  public String rewriteCookie(String headerValue) throws URISyntaxException, InvalidCookieException {
    BestMatchSpec parser = new BestMatchSpec();
    List<Cookie> cookies;
    try {
      cookies = parser.parse(new BasicHeader("Set-Cookie", headerValue), new CookieOrigin(targetServer.getHost(), getPortOrDefault(targetServer), targetServer.getPath(), false));
    } catch (MalformedCookieException e) {
      throw new InvalidCookieException(e);
    }
    if (cookies.size() != 1) {
      throw new InvalidCookieException();
    }
    Cookie cookie = rewriteCookiePathIfNeeded(cookies.get(0));
    CookieFormatter cookieFormatter = CookieFormatter.createFromApacheCookie(cookie);
    return cookieFormatter.asString();
  }

  private Cookie rewriteCookiePathIfNeeded(Cookie cookie) {
    if (removeTrailingSlashes(cookie.getPath()).equals(removeTrailingSlashes(targetServer.getPath()))) {
      BasicClientCookie newCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
      newCookie.setPath(servletRequest.getContextPath() + servletRequest.getServletPath());
      return newCookie;
    } else {
      return cookie;
    }
  }

  private String rewritePathIfNeeded(String requestedPath) {
    String servletURI = servletRequest.getContextPath() + servletRequest.getServletPath();
    if (
<<<<<<< /usr/src/app/output/mbaechler/proxy-servlet/456846e2b56eb09964f65677180bd70ee107d33e/src/main/java/com/woonoz/proxy/servlet/UrlRewriterImpl.java/left.java
    targetServer.getPath().length() != 0 && requestIsSubpathOfServlet(requestedPath)
=======
    !targetServer.getPath().isEmpty() && requestIsSubpathOfServlet(requestedPath)
>>>>>>> /usr/src/app/output/mbaechler/proxy-servlet/456846e2b56eb09964f65677180bd70ee107d33e/src/main/java/com/woonoz/proxy/servlet/UrlRewriterImpl.java/right.java
    ) {
      return appendPathFragments(targetServer.getPath(), requestedPath.substring(servletURI.length()));
    } else {
      return requestedPath;
    }
  }

  private static String appendPathFragments(final String firstPart, final String secondPart) {
    return removeTrailingSlashes(firstPart) + "/" + removeLeadingSlashes(secondPart);
  }

  private static String removeTrailingSlashes(final String text) {

<<<<<<< /usr/src/app/output/mbaechler/proxy-servlet/456846e2b56eb09964f65677180bd70ee107d33e/src/main/java/com/woonoz/proxy/servlet/UrlRewriterImpl.java/left.java
    if (text.length() == 0) {
      return text;
    }
=======
    if (text.isEmpty()) {
      return text;
    }
>>>>>>> /usr/src/app/output/mbaechler/proxy-servlet/456846e2b56eb09964f65677180bd70ee107d33e/src/main/java/com/woonoz/proxy/servlet/UrlRewriterImpl.java/right.java

    final CharacterIterator it = new StringCharacterIterator(text);
    Character c = it.last();
    while (c.equals('/')) {
      c = it.previous();
    }
    return text.substring(0, it.getIndex() + 1);
  }

  private static String removeLeadingSlashes(final String text) {

<<<<<<< /usr/src/app/output/mbaechler/proxy-servlet/456846e2b56eb09964f65677180bd70ee107d33e/src/main/java/com/woonoz/proxy/servlet/UrlRewriterImpl.java/left.java
    if (text.length() == 0) {
      return text;
    }
=======
    if (text.isEmpty()) {
      return text;
    }
>>>>>>> /usr/src/app/output/mbaechler/proxy-servlet/456846e2b56eb09964f65677180bd70ee107d33e/src/main/java/com/woonoz/proxy/servlet/UrlRewriterImpl.java/right.java

    final CharacterIterator it = new StringCharacterIterator(text);
    Character c = it.first();
    while (c.equals('/')) {
      c = it.next();
    }
    return text.substring(it.getIndex());
  }

  private boolean requestedUrlPointsToServlet(final URI requestedUrl) throws MalformedURLException {
    return hostIsSameAsServletHost(requestedUrl) && requestIsSubpathOfServlet(requestedUrl.getPath());
  }

  private boolean hostIsSameAsServletHost(final URI requestedUrl) throws MalformedURLException {
    return requestedUrl.getHost().equals(getServletHost()) && getPortOrDefault(requestedUrl.toURL()) == getServletPort();
  }

  private boolean requestIsSubpathOfServlet(final String requestedUrl) {
    return requestedUrl.startsWith(servletRequest.getRequestURI());
  }

  private String getTargetHostString() {
    int port = getTargetPort();
    if (port == -1) {
      return getTargetHost();
    } else {
      return getTargetHost() + ":" + getTargetPort();
    }
  }

  private String getServletHost() {
    return servletRequest.getServerName();
  }

  private int getServletPort() {
    return servletRequest.getServerPort();
  }

  private String getTargetHost() {
    return targetServer.getHost();
  }

  private int getTargetPort() {
    return targetServer.getPort();
  }

  private int getPortOrDefault(URL url) {
    final int port = url.getPort();
    if (port == -1) {
      return url.getDefaultPort();
    } else {
      return port;
    }
  }
}