package com.jcabi.http.request;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.ImmutableHeader;
import com.jcabi.http.Request;
import com.jcabi.http.RequestBody;
import com.jcabi.http.RequestURI;
import com.jcabi.http.Response;
import com.jcabi.http.Wire;
import com.jcabi.immutable.Array;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Implementation of {@link Request} that always returns the same
 * response, specified in the constructor.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.9
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@Immutable @EqualsAndHashCode(of = "base") @Loggable(value = Loggable.DEBUG) @SuppressWarnings(value = { "PMD.TooManyMethods" }) public final class FakeRequest implements Request {

<<<<<<< /usr/src/app/output/jcabi/jcabi-http/b33d320e5a794ad393369a0c141a3afbc1ff53e4/src/main/java/com/jcabi/http/request/FakeRequest.java/left.java
  /**
     * An empty immutable {@code byte} array.
     */
  private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
=======
  /**
     * The Charset to use.
     */
  private static final Charset CHARSET = Charset.forName("UTF-8");
>>>>>>> /usr/src/app/output/jcabi/jcabi-http/b33d320e5a794ad393369a0c141a3afbc1ff53e4/src/main/java/com/jcabi/http/request/FakeRequest.java/right.java


  /**
     * Base request.
     * @checkstyle ParameterNumber (15 lines)
     */
  private final transient Request base = new BaseRequest(new Wire() {
    @Override public Response send(final Request req, final String home, final String method, final Collection<Map.Entry<String, String>> headers, final byte[] text) {
      return new DefaultResponse(req, FakeRequest.this.code, FakeRequest.this.phrase, FakeRequest.this.hdrs, FakeRequest.this.content);
    }
  }, "http://localhost:12345/see-FakeRequest-class");

  /**
     * Status code.
     */
  private final transient int code;

  /**
     * Reason phrase.
     */
  private final transient String phrase;

  /**
     * Headers.
     */
  private final transient Array<Map.Entry<String, String>> hdrs;

  /**
     * Content received.
     */
  private final transient byte[] content;

  /**
     * Public ctor.
     */
  public FakeRequest() {
    this(HttpURLConnection.HTTP_OK, "OK", Collections.<Map.Entry<String, String>>emptyList(), FakeRequest.EMPTY_BYTE_ARRAY);
  }

  /**
     * Public ctor.
     * @param status HTTP status code to return
     * @param reason HTTP reason
     * @param headers HTTP headers
     * @param body HTTP body
     * @checkstyle ParameterNumber (10 lines)
     */
  public FakeRequest(final int status, @NotNull(message = "HTTP reason can\'t be NULL") final String reason, @NotNull(message = "list of headers can\'t be NULL") final Collection<Map.Entry<String, String>> headers, @NotNull(message = "body can\'t be NULL") final byte[] body) {
    this.code = status;
    this.phrase = reason;
    this.hdrs = new Array<Map.Entry<String, String>>(headers);
    this.content = body.clone();
  }

  @Override public String toString() {
    return this.base.toString();
  }

  @Override @NotNull public RequestURI uri() {
    return this.base.uri();
  }

  @Override public Request header(@NotNull(message = "header name can\'t be NULL") final String name, @NotNull(message = "header value can\'t be NULL") final Object value) {
    return this.base.header(name, value);
  }

  @Override public Request reset(@NotNull(message = "header name can\'t be NULL") final String name) {
    return this.base.reset(name);
  }

  @Override public RequestBody body() {
    return this.base.body();
  }

  @Override public Request method(@NotNull(message = "method can\'t be NULL") final String method) {
    return this.base.method(method);
  }

  @Override public Response fetch() throws IOException {
    return this.base.fetch();
  }

  @Override public <T extends Wire> Request through(final Class<T> type, final Object... args) {
    return this.base.through(type, args);
  }

  /**
     * Make a similar request, with the provided status code.
     * @param status The code
     * @return New request
     */
  public FakeRequest withStatus(final int status) {
    return new FakeRequest(status, this.phrase, this.hdrs, this.content);
  }

  /**
     * Make a similar request, with the provided reason line.
     * @param reason Reason line
     * @return New request
     */
  public FakeRequest withReason(final String reason) {
    return new FakeRequest(this.code, reason, this.hdrs, this.content);
  }

  /**
     * Make a similar request, with the provided HTTP header.
     * @param name Name of the header
     * @param value Value of it
     * @return New request
     */
  public FakeRequest withHeader(final String name, final String value) {
    return new FakeRequest(this.code, this.phrase, this.hdrs.with(new ImmutableHeader(name, value)), this.content);
  }

  /**
     * Make a similar request, with the provided body.
     * @param text Body
     * @return New request
     */
  public FakeRequest withBody(final String text) {
    return this.withBody(text.getBytes(FakeRequest.CHARSET));
  }

  /**
     * Make a similar request, with the provided body.
     * @param body Body
     * @return New request
     */
  public FakeRequest withBody(final byte[] body) {
    return new FakeRequest(this.code, this.phrase, this.hdrs, body);
  }
}