package com.jcabi.http;
import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import javax.net.ssl.SSLContext;

/**
 * Wire.
 *
 * <p>An instance of this interface can be used in
 * {@link Request#through(Class,Object...)} to decorate
 * an existing {@code wire}, for example:
 *
 * <pre> String html = new JdkRequest("http://google.com")
 *   .through(VerboseWire.class)
 *   .through(RetryWire.class)
 *   .header("Accept", "text/html")
 *   .fetch()
 *   .body();</pre>
 *
 * <p>Every {@code Wire} decorator passed to {@code through()} method
 * wraps a previously existing one.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.9
 */
@Immutable public interface Wire {
  /**
     * Send request and return response.
     * @param req Request
     * @param home URI to fetch
     * @param method HTTP method
     * @param headers Headers
     * @param content HTTP body
     * @param connect The connect timeout
     * @param read The read timeout
     * @param sslcontext SSL Context
     * @return Response obtained
     * @throws IOException if fails
     * @checkstyle ParameterNumber (6 lines)
     */
  Response send(Request req, String home, String method, Collection<Map.Entry<String, String>> headers, InputStream content, int connect, int read, SSLContext sslcontext) throws IOException;
}