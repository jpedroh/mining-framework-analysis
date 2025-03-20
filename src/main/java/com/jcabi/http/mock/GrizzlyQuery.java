package com.jcabi.http.mock;
import com.jcabi.aspects.Immutable;
import com.jcabi.http.ImmutableHeader;
import com.jcabi.immutable.ArrayMap;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Mock HTTP query/request.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.10
 */
@Immutable final class GrizzlyQuery implements MkQuery {
  /**
     * The encoding to use.
     */
  private static final String ENCODING = "UTF-8";

  /**
     * The Charset to use.
     */
  private static final Charset CHARSET = Charset.forName(ENCODING);

  /**
     * HTTP request method.
     */
  private final transient String mtd;

  /**
     * HTTP request content.
     */
  private final transient byte[] content;

  /**
     * HTTP request URI.
     */
  private final transient String home;

  /**
     * HTTP request headers.
     */
  private final transient ArrayMap<String, List<String>> hdrs;

  /**
     * Ctor.
     * @param request Grizzly request
     * @throws IOException If fails
     */
  GrizzlyQuery(final GrizzlyRequest request) throws IOException {
    request.setCharacterEncoding(GrizzlyQuery.ENCODING);
    this.home = GrizzlyQuery.uri(request);
    this.mtd = request.getMethod();
    this.hdrs = GrizzlyQuery.headers(request);
    final byte[] buffer = new byte[8192];
    final InputStream input = request.getInputStream();
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    for (int bytes = input.read(buffer); bytes != -1; bytes = input.read(buffer)) {
      output.write(buffer, 0, bytes);
    }
    this.content = output.toByteArray();
  }

  @Override public URI uri() {
    return URI.create(this.home);
  }

  @Override public String method() {
    return this.mtd;
  }

  @Override public Map<String, List<String>> headers() {
    return Collections.unmodifiableMap(this.hdrs);
  }

  @Override public String body() {
    return new String(this.content, GrizzlyQuery.CHARSET);
  }

  /**
     * Fetch URI from the request.
     * @param request Request
     * @return URI
     */
  private static String uri(final GrizzlyRequest request) {
    final StringBuilder uri = new StringBuilder(request.getRequestURI());
    final String query = request.getQueryString();
    if (query != null && !query.isEmpty()) {
      uri.append('?').append(query);
    }
    return uri.toString();
  }

  /**
     * Fetch headers from the request.
     * @param request Request
     * @return Headers
     */
  private static ArrayMap<String, List<String>> headers(final GrizzlyRequest request) {
    final ConcurrentMap<String, List<String>> headers = new ConcurrentHashMap<String, List<String>>(0);
    final Enumeration<?> names = request.getHeaderNames();
    while (names.hasMoreElements()) {
      final String name = names.nextElement().toString();
      headers.put(ImmutableHeader.normalize(name), GrizzlyQuery.headers(request, name));
    }
    return new ArrayMap<String, List<String>>(headers);
  }

  /**
     * Get headers by name.
     * @param request Grizzly request
     * @param name Name of header
     * @return List of values
     */
  private static List<String> headers(final GrizzlyRequest request, final String name) {
    final List<String> list = new LinkedList<String>();
    final Enumeration<?> values = request.getHeaders(name);
    while (values.hasMoreElements()) {
      list.add(values.nextElement().toString());
    }
    return list;
  }
}