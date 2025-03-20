package com.jcabi.http.wire;
import com.jcabi.aspects.Immutable;
import com.jcabi.http.ImmutableHeader;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.Wire;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.HttpHeaders;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.codec.binary.Base64;

/**
 * Wire with HTTP basic authentication based on user info of URI.
 *
 * <p>This wire converts user info from URI into
 * {@code "Authorization"} HTTP header, for example:
 *
 * <pre> String html = new JdkRequest("http://jeff:12345@example.com")
 *   .through(BasicAuthWire.class)
 *   .fetch()
 *   .body();</pre>
 *
 * <p>In this example, an additional HTTP header {@code Authorization}
 * will be added with a value {@code Basic amVmZjoxMjM0NQ==}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.10
 * @see <a href="http://tools.ietf.org/html/rfc2617">RFC 2617 "HTTP Authentication: Basic and Digest Access Authentication"</a>
 */
@Immutable @ToString @EqualsAndHashCode(of = "origin") public final class BasicAuthWire implements Wire {
  /**
     * The encoding to use.
     */
  private static final String ENCODING = "UTF-8";

  /**
     * The Charset to use.
     */
  private static final Charset CHARSET = Charset.forName(ENCODING);

  /**
     * Original wire.
     */
  private final transient Wire origin;

  /**
     * Public ctor.
     * @param wire Original wire
     */
  public BasicAuthWire(@NotNull(message = "wire can\'t be NULL") final Wire wire) {
    this.origin = wire;
  }

  /**
     * {@inheritDoc}
     * @checkstyle ParameterNumber (7 lines)
     */
  @Override public Response send(final Request req, final String home, final String method, final Collection<Map.Entry<String, String>> headers, final byte[] content) throws IOException {
    final Collection<Map.Entry<String, String>> hdrs = new LinkedList<Map.Entry<String, String>>();
    boolean absent = true;
    for (final Map.Entry<String, String> header : headers) {
      if (header.getKey().equals(HttpHeaders.AUTHORIZATION)) {
        absent = false;
      }
      hdrs.add(header);
    }
    final String info = URI.create(home).getUserInfo();
    if (absent && info != null) {
      final String[] parts = info.split(":", 2);
      try {
        hdrs.add(new ImmutableHeader(HttpHeaders.AUTHORIZATION, Logger.format("Basic %s", Base64.encodeBase64String(Logger.format("%s:%s", URLEncoder.encode(parts[0], BasicAuthWire.ENCODING), URLEncoder.encode(parts[1], BasicAuthWire.ENCODING)).getBytes(BasicAuthWire.CHARSET)))));
      } catch (final UnsupportedEncodingException ex) {
        throw new IllegalStateException(ex);
      }
    }
    return this.origin.send(req, home, method, hdrs, content);
  }
}