package org.mitre.dsmiley.httpproxy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.RequestLine;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

/**
 * @author David Smiley - dsmiley@mitre.org
 */
@SuppressWarnings(value = { "deprecation", "rawtypes" }) public class ProxyServletTest {
  private static final Log log = LogFactory.getLog(ProxyServletTest.class);

  /**
   * From Apache httpcomponents/httpclient. Note httpunit has a similar thing called PseudoServlet but it is
   * not as good since you can't even make it echo the request back.
   */
  protected LocalTestServer localTestServer;

  /** From Meterware httpunit. */
  protected ServletRunner servletRunner;

  private ServletUnitClient sc;

  protected String targetBaseUri;

  protected String sourceBaseUri;

  protected String servletName = ProxyServlet.class.getName();

  protected String servletPath = "/proxyMe";

  @Before public void setUp() throws Exception {
    localTestServer = new LocalTestServer(null, null);
    localTestServer.start();
    localTestServer.register("/targetPath*", new RequestInfoHandler());
    servletRunner = new ServletRunner();
    Properties servletProps = new Properties();
    servletProps.setProperty("http.protocol.handle-redirects", "false");
    servletProps.setProperty(ProxyServlet.P_LOG, "true");
    servletProps.setProperty(ProxyServlet.P_FORWARDEDFOR, "true");
    setUpServlet(servletProps);
    sc = servletRunner.newClient();
    sc.getClientProperties().setAutoRedirect(false);
  }

  protected void setUpServlet(Properties servletProps) {
    servletProps.putAll(servletProps);
    targetBaseUri = "http://localhost:" + localTestServer.getServiceAddress().getPort() + "/targetPath";
    servletProps.setProperty("targetUri", targetBaseUri);
    servletRunner.registerServlet(servletPath + "/*", servletName, servletProps);
    sourceBaseUri = "http://localhost/proxyMe";
  }

  @After public void tearDown() throws Exception {
    servletRunner.shutDown();
    localTestServer.stop();
  }

  private static String[] testUrlSuffixes = new String[] { "", "/pathInfo", "/pathInfo/%23%25abc", "?q=v", "/p?q=v", "/p?query=note:Leitbild", "/p?query=note%3ALeitbild", "/p?id=p%20i", "/p%20i", 
<<<<<<< /usr/src/app/output/mitre/http-proxy-servlet/071ba56e5c6a23713f816b83237bb237ef8872e9/src/test/java/org/mitre/dsmiley/httpproxy/ProxyServletTest.java/left.java
  "/p?id=p+i"
=======
  "/pathwithquestionmark%3F%3F?from=1&to=10"
>>>>>>> /usr/src/app/output/mitre/http-proxy-servlet/071ba56e5c6a23713f816b83237bb237ef8872e9/src/test/java/org/mitre/dsmiley/httpproxy/ProxyServletTest.java/right.java
   };

  protected boolean doTestUrlSuffix(String urlSuffix) {
    return true;
  }

  @Test public void testGet() throws Exception {
    for (String urlSuffix : testUrlSuffixes) {
      if (doTestUrlSuffix(urlSuffix) == false) {
        continue;
      }
      execAssert(makeGetMethodRequest(sourceBaseUri + urlSuffix));
    }
  }

  @Test public void testPost() throws Exception {
    for (String urlSuffix : testUrlSuffixes) {
      if (doTestUrlSuffix(urlSuffix) == false) {
        continue;
      }
      execAndAssert(makePostMethodRequest(sourceBaseUri + urlSuffix));
    }
  }

  @Test public void testRedirect() throws IOException, SAXException {
    final String COOKIE_SET_HEADER = "Set-Cookie";
    localTestServer.register("/targetPath*", new HttpRequestHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        response.setHeader(HttpHeaders.LOCATION, request.getFirstHeader("xxTarget").getValue());
        response.setHeader(COOKIE_SET_HEADER, "JSESSIONID=1234; path=/;");
        response.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
      }
    });
    GetMethodWebRequest request = makeGetMethodRequest(sourceBaseUri + "/%64%69%72%2F");
    assertRedirect(request, "/dummy", "/dummy");
    assertRedirect(request, targetBaseUri + "/dummy?a=b", sourceBaseUri + "/dummy?a=b");
    assertRedirect(request, targetBaseUri + "/sample%20url", sourceBaseUri + "/sample%20url");
    assertRedirect(request, targetBaseUri + "/sample%20url?a=b", sourceBaseUri + "/sample%20url?a=b");
    assertRedirect(request, targetBaseUri + "/sample%20url?a=b#frag", sourceBaseUri + "/sample%20url?a=b#frag");
    assertRedirect(request, targetBaseUri + "/sample+url", sourceBaseUri + "/sample+url");
    assertRedirect(request, targetBaseUri + "/sample+url?a=b", sourceBaseUri + "/sample+url?a=b");
    assertRedirect(request, targetBaseUri + "/sample+url?a=b#frag", sourceBaseUri + "/sample+url?a=b#frag");
    assertRedirect(request, targetBaseUri + "/sample+url?a+b=b%20c#frag%23", sourceBaseUri + "/sample+url?a+b=b%20c#frag%23");
    assertRedirect(request, "http://blackhole.org/dir/file.ext?a=b#c", "http://blackhole.org/dir/file.ext?a=b#c");
  }

  private void assertRedirect(GetMethodWebRequest request, String origRedirect, String resultRedirect) throws IOException, SAXException {
    request.setHeaderField("xxTarget", origRedirect);
    WebResponse rsp = sc.getResponse(request);
    assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, rsp.getResponseCode());
    assertEquals("", rsp.getText());
    String gotLocation = rsp.getHeaderField(HttpHeaders.LOCATION);
    assertEquals(resultRedirect, gotLocation);
    assertEquals("!Proxy!" + servletName + "JSESSIONID=1234;path=" + servletPath, rsp.getHeaderField("Set-Cookie"));
  }

  @Test public void testSendFile() throws Exception {
    final PostMethodWebRequest request = new PostMethodWebRequest(rewriteMakeMethodUrl("http://localhost/proxyMe"), true);
    InputStream data = new ByteArrayInputStream("testFileData".getBytes("UTF-8"));
    request.selectFile("fileNameParam", "fileName", data, "text/plain");
    WebResponse rsp = execAndAssert(request);
    assertTrue(rsp.getText().contains("Content-Type: multipart/form-data; boundary="));
  }

  @Test public void testProxyWithUnescapedChars() throws Exception {
    execAssert(makeGetMethodRequest(sourceBaseUri + "?fq={!f=field}"), "?fq=%7B!f=field%7D");
    execAssert(makeGetMethodRequest(sourceBaseUri + "?fq=%7B!f=field%7D"));
  }

  /** http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html */
  @SuppressWarnings(value = { "unchecked" }) @Test public void testHopByHopHeadersOnSource() throws Exception {
    final String HEADER = "Proxy-Authenticate";
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        assertNull(request.getFirstHeader(HEADER));
        response.setHeader(HEADER, "from-server");
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    req.getHeaders().put(HEADER, "from-client");
    WebResponse rsp = execAndAssert(req, "");
    assertNull(rsp.getHeaderField(HEADER));
  }

  @Test public void testWithExistingXForwardedFor() throws Exception {
    final String FOR_HEADER = "X-Forwarded-For";
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Header xForwardedForHeader = request.getFirstHeader(FOR_HEADER);
        assertEquals("192.168.1.1, 127.0.0.1", xForwardedForHeader.getValue());
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    req.setHeaderField(FOR_HEADER, "192.168.1.1");
    WebResponse rsp = execAndAssert(req, "");
  }

  @Test public void testEnabledXForwardedFor() throws Exception {
    final String FOR_HEADER = "X-Forwarded-For";
    final String PROTO_HEADER = "X-Forwarded-Proto";
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Header xForwardedForHeader = request.getFirstHeader(FOR_HEADER);
        Header xForwardedProtoHeader = request.getFirstHeader(PROTO_HEADER);
        assertEquals("127.0.0.1", xForwardedForHeader.getValue());
        assertEquals("http", xForwardedProtoHeader.getValue());
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    WebResponse rsp = execAndAssert(req, "");
  }

  @Test public void testCopyRequestHeaderToProxyRequest() throws Exception {
    final String HEADER = "HEADER_TO_TEST";
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Header headerToTest = request.getFirstHeader(HEADER);
        assertEquals("VALUE_TO_TEST", headerToTest.getValue());
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    req.setHeaderField(HEADER, "VALUE_TO_TEST");
    execAndAssert(req, "");
  }

  @Test public void testCopyProxiedRequestHeadersToResponse() throws Exception {
    final String HEADER = "HEADER_TO_TEST";
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        response.setHeader(HEADER, "VALUE_TO_TEST");
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    WebResponse rsp = execAndAssert(req, "");
    assertEquals("VALUE_TO_TEST", rsp.getHeaderField(HEADER));
  }

  @Test public void testSetCookie() throws Exception {
    final String HEADER = "Set-Cookie";
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        response.setHeader(HEADER, "JSESSIONID=1234; Path=/proxy/path/that/we/dont/want; Expires=Wed, 13 Jan 2021 22:23:01 GMT; Domain=.foo.bar.com; HttpOnly");
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    WebResponse rsp = execAndAssert(req, "");
    assertEquals("!Proxy!" + servletName + "JSESSIONID=1234;path=" + servletPath, rsp.getHeaderField(HEADER));
  }

  @Test public void testSetCookie2() throws Exception {
    final String HEADER = "Set-Cookie2";
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        response.setHeader(HEADER, "JSESSIONID=1234; Path=/proxy/path/that/we/dont/want; Max-Age=3600; Domain=.foo.bar.com; Secure");
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    WebResponse rsp = execAndAssert(req, "");
    assertEquals("!Proxy!" + servletName + "JSESSIONID=1234;path=" + servletPath, rsp.getHeaderField("Set-Cookie"));
  }

  @Test public void testPreserveCookie() throws Exception {
    servletRunner = new ServletRunner();
    Properties servletProps = new Properties();
    servletProps.setProperty("http.protocol.handle-redirects", "false");
    servletProps.setProperty(ProxyServlet.P_LOG, "true");
    servletProps.setProperty(ProxyServlet.P_FORWARDEDFOR, "true");
    servletProps.setProperty(ProxyServlet.P_PRESERVECOOKIES, "true");
    setUpServlet(servletProps);
    sc = servletRunner.newClient();
    sc.getClientProperties().setAutoRedirect(false);
    final String HEADER = "Set-Cookie";
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        response.setHeader(HEADER, "JSESSIONID=1234; Path=/proxy/path/that/we/dont/want; Expires=Wed, 13 Jan 2021 22:23:01 GMT; Domain=.foo.bar.com; HttpOnly");
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    WebResponse rsp = execAndAssert(req, "");
    assertEquals("JSESSIONID=1234;path=" + servletPath, rsp.getHeaderField(HEADER));
  }

  @Test public void testSetCookieHttpOnly() throws Exception {
    final String HEADER = "Set-Cookie";
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        response.setHeader(HEADER, "JSESSIONID=1234; Path=/proxy/path/that/we/dont/want/; HttpOnly");
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    WebResponse rsp = execAndAssert(req, "");
    assertEquals("!Proxy!" + servletName + "JSESSIONID=1234;path=" + servletPath, rsp.getHeaderField(HEADER));
  }

  @Test public void testSendCookiesToProxy() throws Exception {
    final StringBuffer captureCookieValue = new StringBuffer();
    final String HEADER = "Cookie";
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        captureCookieValue.append(request.getHeaders(HEADER)[0].getValue());
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    req.setHeaderField(HEADER, "LOCALCOOKIE=ABC; " + "!Proxy!" + servletName + "JSESSIONID=1234; " + "!Proxy!" + servletName + "COOKIE2=567; " + "LOCALCOOKIELAST=ABCD");
    WebResponse rsp = execAndAssert(req, "");
    assertEquals("JSESSIONID=1234; COOKIE2=567", captureCookieValue.toString());
  }

  /**
   * If we're proxying a remote service that tries to set cookies, we need to make sure the cookies are not captured
   * by the httpclient in the ProxyServlet, otherwise later requests from ALL users will all access the remote proxy
   * with the same cookie as the first user
   */
  @Test public void testMultipleRequestsWithDiffCookies() throws Exception {
    final AtomicInteger requestCounter = new AtomicInteger(1);
    final StringBuffer captureCookieValue = new StringBuffer();
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (request.getFirstHeader("Cookie") != null) {
          captureCookieValue.append(request.getFirstHeader("Cookie"));
        } else {
          response.setHeader("Set-Cookie", "JSESSIONID=USER_" + requestCounter.getAndIncrement() + "_SESSION");
        }
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    WebResponse rsp = execAndAssert(req, "");
    assertEquals("", captureCookieValue.toString());
    assertEquals("USER_1_SESSION", sc.getCookieJar().getCookie("!Proxy!" + servletName + "JSESSIONID").getValue());
    sc.clearContents();
    req = makeGetMethodRequest(sourceBaseUri);
    rsp = execAndAssert(req, "");
    assertEquals("", captureCookieValue.toString());
    assertEquals("USER_2_SESSION", sc.getCookieJar().getCookie("!Proxy!" + servletName + "JSESSIONID").getValue());
  }

  @Test public void testRedirectWithBody() throws Exception {
    final String CONTENT = "-This-Shall-Not-Pass-";
    localTestServer.register("/targetPath/test", new HttpRequestHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        response.setHeader(HttpHeaders.LOCATION, targetBaseUri + "/test/");
        response.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
        response.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setEntity(new ByteArrayEntity(CONTENT.getBytes("UTF-8")));
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri + "/test");
    WebResponse rsp = sc.getResponse(req);
    assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, rsp.getResponseCode());
    assertEquals(String.valueOf(CONTENT.length()), rsp.getHeaderField(HttpHeaders.CONTENT_LENGTH));
    assertEquals(CONTENT, rsp.getText());
    assertEquals(sourceBaseUri + "/test/", rsp.getHeaderField(HttpHeaders.LOCATION));
  }

  @Test public void testPreserveHost() throws Exception {
    servletRunner = new ServletRunner();
    Properties servletProps = new Properties();
    servletProps.setProperty("http.protocol.handle-redirects", "false");
    servletProps.setProperty(ProxyServlet.P_LOG, "true");
    servletProps.setProperty(ProxyServlet.P_FORWARDEDFOR, "true");
    servletProps.setProperty(ProxyServlet.P_PRESERVEHOST, "true");
    setUpServlet(servletProps);
    sc = servletRunner.newClient();
    sc.getClientProperties().setAutoRedirect(false);
    final String HEADER = "Host";
    final String[] proxyHost = new String[1];
    localTestServer.register("/targetPath*", new RequestInfoHandler() {
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        proxyHost[0] = request.getHeaders(HEADER)[0].getValue();
        super.handle(request, response, context);
      }
    });
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    req.setHeaderField(HEADER, "SomeHost");
    execAndAssert(req, "");
    assertEquals("SomeHost", proxyHost[0]);
  }

  @Test public void testUseSystemProperties() throws Exception {
    System.setProperty("http.proxyHost", "foo.blah.nonexisting.dns.name");
    servletRunner = new ServletRunner();
    Properties servletProps = new Properties();
    servletProps.setProperty(ProxyServlet.P_LOG, "true");
    servletProps.setProperty(ProxyServlet.P_USESYSTEMPROPERTIES, "true");
    targetBaseUri = "http://www.google.com";
    servletProps.setProperty(ProxyServlet.P_TARGET_URI, targetBaseUri);
    servletRunner.registerServlet(servletPath + "/*", servletName, servletProps);
    sc = servletRunner.newClient();
    sc.getClientProperties().setAutoRedirect(false);
    GetMethodWebRequest req = makeGetMethodRequest(sourceBaseUri);
    try {
      execAssert(req);
      fail("UnknownHostException expected.");
    } catch (UnknownHostException e) {
    } finally {
      System.clearProperty("http.proxyHost");
    }
  }

  private WebResponse execAssert(GetMethodWebRequest request, String expectedUri) throws Exception {
    return execAndAssert(request, expectedUri);
  }

  protected WebResponse execAssert(GetMethodWebRequest request) throws Exception {
    return execAndAssert(request, null);
  }

  protected WebResponse execAndAssert(PostMethodWebRequest request) throws Exception {
    request.setParameter("abc", "ABC");
    WebResponse rsp = execAndAssert(request, null);
    assertTrue(rsp.getText().contains("ABC"));
    return rsp;
  }

  protected WebResponse execAndAssert(WebRequest request, String expectedUri) throws Exception {
    WebResponse rsp = sc.getResponse(request);
    assertEquals(HttpStatus.SC_OK, rsp.getResponseCode());
    final String text = rsp.getText();
    assertTrue(text.startsWith("REQUESTLINE:"));
    String expectedTargetUri = getExpectedTargetUri(request, expectedUri);
    String expectedFirstLine = "REQUESTLINE: " + (request instanceof GetMethodWebRequest ? "GET" : "POST");
    expectedFirstLine += " " + expectedTargetUri + " HTTP/1.1";
    String firstTextLine = text.substring(0, text.indexOf(System.getProperty("line.separator")));
    assertEquals(expectedFirstLine, firstTextLine);
    Dictionary headers = request.getHeaders();
    Enumeration headerNameEnum = headers.keys();
    while (headerNameEnum.hasMoreElements()) {
      String headerName = (String) headerNameEnum.nextElement();
      assertTrue(text.contains(headerName));
    }
    return rsp;
  }

  protected String getExpectedTargetUri(WebRequest request, String expectedUri) throws MalformedURLException, URISyntaxException {
    if (expectedUri == null) {
      expectedUri = request.getURL().toString().substring(sourceBaseUri.length());
    }
    return new URI(this.targetBaseUri).getPath() + expectedUri;
  }

  protected GetMethodWebRequest makeGetMethodRequest(final String url) {
    return makeMethodRequest(url, GetMethodWebRequest.class);
  }

  protected PostMethodWebRequest makePostMethodRequest(final String url) {
    return makeMethodRequest(url, PostMethodWebRequest.class);
  }

  @SuppressWarnings(value = { "unchecked" }) private <M extends java.lang.Object> M makeMethodRequest(String incomingUrl, Class<M> clazz) {
    log.info("Making request to url " + incomingUrl);
    final String url = rewriteMakeMethodUrl(incomingUrl);
    String urlNoQuery;
    final String queryString;
    int qIdx = url.indexOf('?');
    if (qIdx == -1) {
      urlNoQuery = url;
      queryString = null;
    } else {
      urlNoQuery = url.substring(0, qIdx);
      queryString = url.substring(qIdx + 1);
    }
    if (clazz == PostMethodWebRequest.class) {
      return (M) new PostMethodWebRequest(urlNoQuery) {
        @Override public String getQueryString() {
          return queryString;
        }

        @Override protected String getURLString() {
          return url;
        }
      };
    } else {
      if (clazz == GetMethodWebRequest.class) {
        return (M) new GetMethodWebRequest(urlNoQuery) {
          @Override public String getQueryString() {
            return queryString;
          }

          @Override protected String getURLString() {
            return url;
          }
        };
      }
    }
    throw new IllegalArgumentException(clazz.toString());
  }

  protected String rewriteMakeMethodUrl(String url) {
    return url;
  }

  protected static class RequestInfoHandler implements HttpRequestHandler {
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(baos, false);
      final RequestLine rl = request.getRequestLine();
      pw.println("REQUESTLINE: " + rl);
      for (Header header : request.getAllHeaders()) {
        pw.println(header.getName() + ": " + header.getValue());
      }
      pw.println("BODY: (below)");
      pw.flush();
      if (request instanceof HttpEntityEnclosingRequest) {
        HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) request;
        HttpEntity entity = enclosingRequest.getEntity();
        byte[] body = EntityUtils.toByteArray(entity);
        baos.write(body);
      }
      response.setStatusCode(200);
      response.setReasonPhrase("TESTREASON");
      response.setEntity(new ByteArrayEntity(baos.toByteArray()));
    }
  }
}