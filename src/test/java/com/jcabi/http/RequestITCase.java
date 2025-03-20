package com.jcabi.http;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Integration case for {@link com.jcabi.http.request.ApacheRequest}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@RunWith(value = Parameterized.class) public final class RequestITCase {
  /**
     * Type of request.
     */
  private final transient Class<? extends Request> type;

  /**
     * Public ctor.
     * @param req Request type
     */
  public RequestITCase(final Class<? extends Request> req) {
    this.type = req;
  }

  /**
     * Parameters.
     * @return Array of args
     */
  @Parameterized.Parameters public static Collection<Object[]> primeNumbers() {
    return Arrays.asList(new Object[] { ApacheRequest.class }, new Object[] { JdkRequest.class });
  }

  /**
     * BaseRequest can fetch HTTP request and process HTTP response.
     * @throws Exception If something goes wrong inside
     */
  @Test public void sendsHttpRequestAndProcessesHttpResponse() throws Exception {
    this.request(new URI(
<<<<<<< /usr/src/app/output/jcabi/jcabi-http/c693112cf706413bb4c690b2706766db77111cb1/src/test/java/com/jcabi/http/RequestITCase.java/left.java
    "https://http.jcabi.com"
=======
    "http://www.jare.io"
>>>>>>> /usr/src/app/output/jcabi/jcabi-http/c693112cf706413bb4c690b2706766db77111cb1/src/test/java/com/jcabi/http/RequestITCase.java/right.java
    )).fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_OK).as(XmlResponse.class).assertXPath("/xhtml:html");
  }

  /**
     * BaseRequest can process not-OK response.
     * @throws Exception If something goes wrong inside
     */
  @Test public void processesNotOkHttpResponse() throws Exception {
    this.request(new URI(
<<<<<<< /usr/src/app/output/jcabi/jcabi-http/c693112cf706413bb4c690b2706766db77111cb1/src/test/java/com/jcabi/http/RequestITCase.java/left.java
    "https://http.jcabi.com/file-not-found.txt"
=======
    "http://www.jare.io/file-not-found.txt"
>>>>>>> /usr/src/app/output/jcabi/jcabi-http/c693112cf706413bb4c690b2706766db77111cb1/src/test/java/com/jcabi/http/RequestITCase.java/right.java
    )).fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_NOT_FOUND);
  }

  /**
     * BaseRequest can throw a correct exception on connection error.
     * @throws Exception If something goes wrong inside
     */
  @Test(expected = IOException.class) public void continuesOnConnectionError() throws Exception {
    this.request(new URI("http://localhost:6868/")).method(Request.GET).fetch();
  }

  /**
     * Make a request.
     * @param uri URI to start with
     * @return Request
     * @throws Exception If fails
     */
  private Request request(final URI uri) throws Exception {
    return this.type.getDeclaredConstructor(URI.class).newInstance(uri);
  }
}