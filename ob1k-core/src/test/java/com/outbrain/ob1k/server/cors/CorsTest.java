package com.outbrain.ob1k.server.cors;

import com.outbrain.ob1k.server.Server;
import com.outbrain.ob1k.server.builder.ServerBuilder;
import com.outbrain.ob1k.server.services.SimpleTestServiceImpl;
import io.netty.handler.codec.http.HttpMethod;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.junit.Assert;
import org.junit.Test;
import static com.outbrain.swinfra.metrics.DummyMetricFactory.newDummyMetricFactory;
import static io.netty.handler.codec.http.HttpHeaders.Names.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static io.netty.handler.codec.http.HttpHeaders.Names.ACCESS_CONTROL_ALLOW_HEADERS;
import static io.netty.handler.codec.http.HttpHeaders.Names.ACCESS_CONTROL_ALLOW_METHODS;
import static io.netty.handler.codec.http.HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN;
import static io.netty.handler.codec.http.HttpHeaders.Names.ACCESS_CONTROL_EXPOSE_HEADERS;
import static io.netty.handler.codec.http.HttpHeaders.Names.ACCESS_CONTROL_MAX_AGE;
import static io.netty.handler.codec.http.HttpHeaders.Names.ACCESS_CONTROL_REQUEST_METHOD;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.DATE;
import static io.netty.handler.codec.http.HttpHeaders.Names.ORIGIN;
import static io.netty.handler.codec.http.HttpHeaders.Names.VARY;
import static org.asynchttpclient.Dsl.asyncHttpClient;


/**
 * @author Doug Chimento &lt;dchimento@outbrain.com&gt;
 */
public class CorsTest {
  @Test
  public void testCorsConfig() throws Exception {
    CorsConfig corsConfig = CorsConfig.withAnyOrigin().allowCredentials().maxAge(10).allowedRequestHeaders("Content-Type", "Accept", "Origin").allowedRequestMethods("GET", "POST", "OPTIONS").exposeHeaders("X-Exposed").build();
    final Server server = ServerBuilder.newBuilder().contextPath("/test").configure(( c) -> c.useMetricFactory(newDummyMetricFactory())).configure(( b) -> b.withCors(corsConfig)).service(( builder) -> builder.register(new SimpleTestServiceImpl(), "/simple")).build();
    final int port = server.start().getPort();
    final String uri = String.format("http://localhost:%s/test/simple/nextRandom", port);
    AsyncHttpClient c = asyncHttpClient();
    Response r = c.prepareOptions(uri).addHeader("Origin", "http://blah.com").addHeader("X-Exposed", "true").addHeader(ACCESS_CONTROL_REQUEST_METHOD, "POST").execute().get();
    Assert.assertEquals(200, r.getStatusCode());
    Assert.assertFalse(r.getHeaders(ACCESS_CONTROL_ALLOW_HEADERS).isEmpty());
    Assert.assertEquals("0", r.getHeader(CONTENT_LENGTH));
    Assert.assertEquals("http://blah.com", r.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
    Assert.assertEquals("origin", r.getHeader(VARY).toLowerCase());
    Assert.assertEquals("10", r.getHeader(ACCESS_CONTROL_MAX_AGE));
    Assert.assertEquals("true", r.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS));
    Assert.assertNotNull(r.getHeader(DATE));
    r = c.prepareGet(uri).addHeader("Origin", "http://blah.com").addHeader("X-Exposed", "true").execute().get();
    Assert.assertEquals(200, r.getStatusCode());
    Assert.assertEquals("origin", r.getHeader(VARY).toLowerCase());
    Assert.assertEquals("http://blah.com", r.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
    Assert.assertEquals("true", r.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS));
    Assert.assertEquals("X-Exposed", r.getHeader(ACCESS_CONTROL_EXPOSE_HEADERS));
    Assert.assertTrue(r.getHeaders(ACCESS_CONTROL_ALLOW_HEADERS).isEmpty());
  }

  @Test
  public void testCorsNullOrigin() throws Exception {
    final CorsConfig corsConfig = CorsConfig.withAnyOrigin().allowNullOrigin().maxAge(10).allowedRequestHeaders("Content-Type", "Accept", "Origin").allowedRequestMethods("GET", "POST", "OPTIONS").exposeHeaders("X-Exposed").build();
    final Server server = ServerBuilder.newBuilder().contextPath("/test").configure(( c) -> c.useMetricFactory(newDummyMetricFactory())).configure(( b) -> b.withCors(corsConfig)).service(( builder) -> builder.register(new SimpleTestServiceImpl(), "/simple")).build();
    final int port = server.start().getPort();
    final String uri = String.format("http://localhost:%s/test/simple/nextRandom", port);
    AsyncHttpClient c = asyncHttpClient();
    Response r = c.prepareOptions(uri).addHeader(ORIGIN, "null").addHeader(ACCESS_CONTROL_REQUEST_METHOD, "POST").execute().get();
    Assert.assertEquals(200, r.getStatusCode());
    Assert.assertFalse(r.getHeaders(ACCESS_CONTROL_ALLOW_HEADERS).isEmpty());
    Assert.assertEquals("0", r.getHeader(CONTENT_LENGTH));
    Assert.assertNull(r.getHeader(VARY));
    Assert.assertEquals("10", r.getHeader(ACCESS_CONTROL_MAX_AGE));
    Assert.assertTrue(r.getHeader(DATE) != null);
  }

  @Test
  public void testCorsOrigin() throws Exception {
    final CorsConfig corsConfig = CorsConfig.withOrigin("http://cors.com").build();
    final Server server = ServerBuilder.newBuilder().contextPath("/test").configure(( c) -> c.useMetricFactory(newDummyMetricFactory())).configure(( b) -> b.withCors(corsConfig)).service(( builder) -> builder.register(new SimpleTestServiceImpl(), "/simple")).build();
    final int port = server.start().getPort();
    final String uri = String.format("http://localhost:%s/test/simple/nextRandom", port);
    AsyncHttpClient c = asyncHttpClient();
    Response r = c.prepareOptions(uri).addHeader(ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.POST.name()).execute().get();
    //Origin request will produce 501 because OPTIONS method is not supported
    Assert.assertEquals(501, r.getStatusCode());
  }

  @Test
  public void testNoCorsDefault() throws Exception {
    final Server server = ServerBuilder.newBuilder().contextPath("/test").configure(( c) -> c.useMetricFactory(newDummyMetricFactory())).service(( builder) -> builder.register(new SimpleTestServiceImpl(), "/simple")).build();
    final int port = server.start().getPort();
    final String uri = String.format("http://localhost:%s/test/simple/nextRandom", port);
    AsyncHttpClient c = asyncHttpClient();
    Response r = c.prepareOptions(uri).addHeader(ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.POST.name()).execute().get();
    //Origin request will produce 501 because OPTIONS method is not supported
    Assert.assertEquals(501, r.getStatusCode());
  }

  @Test
  public void testNoCors() throws Exception {
    final Server server = ServerBuilder.newBuilder().contextPath("/test").configure(( c) -> c.useMetricFactory(newDummyMetricFactory())).configure(( c) -> c.withCors(new CorsConfig.Builder().disable().build())).service(( builder) -> builder.register(new SimpleTestServiceImpl(), "/simple")).build();
    final int port = server.start().getPort();
    final String uri = String.format("http://localhost:%s/test/simple/nextRandom", port);
    AsyncHttpClient c = asyncHttpClient();
    Response r = c.prepareOptions(uri).addHeader(ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.POST.name()).execute().get();
    //Origin request will produce 501 because OPTIONS method is not supported
    Assert.assertEquals(501, r.getStatusCode());
  }

  @Test
  public void testShortCircuit() throws Exception {
    final Server server = ServerBuilder.newBuilder().contextPath("/test").configure(( c) -> c.useMetricFactory(newDummyMetricFactory())).configure(( c) -> c.withCors(CorsConfig.withOrigin("outbrain.com").shortCircuit().build())).service(( builder) -> builder.register(new SimpleTestServiceImpl(), "/simple")).build();
    final int port = server.start().getPort();
    final String uri = String.format("http://localhost:%s/test/simple/nextRandom", port);
    AsyncHttpClient c = asyncHttpClient();
    Response r = c.prepareOptions(uri).addHeader(ORIGIN, "SomethingSomethingDarkside.com").addHeader(ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.POST.name()).execute().get();
    Assert.assertEquals(403, r.getStatusCode());
  }
}