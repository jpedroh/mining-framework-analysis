package org.kaazing.specification.http.rfc7231;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

/**
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7231#section-5">RFC 7231 section 5:
 * Request Header Fields</a>.
 */
public class RequestHeaderFieldsIT {
  private final K3poRule k3po = new K3poRule().setScriptRoot(
<<<<<<< /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/left.java
  "org/kaazing/specification/http/rfc7231/request.header"
=======
  "org/kaazing/specification/http/rfc7231/request.header.fields"
>>>>>>> /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/right.java
  );

  private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

  @Rule public final TestRule chain = outerRule(k3po).around(timeout);


<<<<<<< /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/left.java
  /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-5.1">RFC 7230 section 5.1: Controls</a>.
     */
  @Test @Specification(value = { "expectation.responds.with.417/request", "expectation.responds.with.417/response" }) public void serverShouldRespondToMeetableExpectWith417() throws Exception {
    k3po.finish();
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-5.1">RFC 7230 section 5.1: Controls</a>.
     * @throws Exception when k3po fails.
     */
  @Test @Ignore(value = "not complete") @Specification(value = { "server.responds.to.unmeetable.expect.with.417/request", "server.responds.to.unmeetable.expect.with.417/response" }) public void serverRespondsToUnmeetableExpectWith417() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-5.1">RFC 7230 section 5.1: Controls</a>.
     */
  @Test @Specification(value = { "intermediary.decrement.max.forward.header/request", "intermediary.decrement.max.forward.header/response" }) public void intermediaryMustDecrementMaxForwardHeaderOnOptionsOrTraceRequest() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-5.1">RFC 7230 section 5.1: Controls</a>.
     */
  @Test @Specification(value = { "intermediary.responds.zero.max.forward/request", "intermediary.responds.zero.max.forward/response" }) public void intermediaryThatReceivesMaxForwardOfZeroOnOptionsOrTraceMustRespondToRequest() throws Exception {
    k3po.finish();
  }
}