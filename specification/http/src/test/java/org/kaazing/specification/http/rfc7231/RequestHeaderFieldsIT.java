/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

<<<<<<< /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/left.java
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7231/request.header");
||||||| /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/base.java
=======
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7231/request.header.fields");
>>>>>>> /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/right.java

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-5.1">RFC 7230 section 5.1: Controls</a>.
     * @throws Exception when k3po fails.
     */

    @Test
    @Specification({
        "expectation.responds.with.417/request",
        "expectation.responds.with.417/response" })
    public void serverShouldRespondToMeetableExpectWith417() throws Exception {
        // A server that receives an Expect field-value other than 100-continue
        // MAY respond with a 417 (Expectation Failed) status code to indicate
        // that the unexpected expectation cannot be met.
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-5.1">RFC 7230 section 5.1: Controls</a>.
     * @throws Exception when k3po fails.
     */

<<<<<<< /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/left.java
    @Test
    @Specification({
        "intermediary.decrement.max.forward.header/request",
        "intermediary.decrement.max.forward.header/response" })
    public void intermediaryMustDecrementMaxForwardHeaderOnOptionsOrTraceRequest() throws Exception {
        k3po.finish();
    }
||||||| /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/base.java
=======
    @Test
    @Ignore("Not Implemented")
    public void intermediaryMustDecrementMaxForwardHeaderOnOptionsOrTraceRequest() throws Exception {
        k3po.finish();
    }
>>>>>>> /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/right.java

    /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-5.1">RFC 7230 section 5.1: Controls</a>.
     * @throws Exception when k3po fails.
     */

<<<<<<< /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/left.java
    @Test
    @Specification({
        "intermediary.responds.zero.max.forward/request",
        "intermediary.responds.zero.max.forward/response" })
    public void intermediaryThatReceivesMaxForwardOfZeroOnOptionsOrTraceMustRespondToRequest() throws Exception {
        k3po.finish();
    }
||||||| /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/base.java
=======
    @Test
    @Ignore("Not Implemented")
    public void intermediaryThatReceivesMaxForwardOfZeroOnOptionsOrTraceMustRespondToRequest() throws Exception {
        k3po.finish();
    }
>>>>>>> /usr/src/app/output/k3po/k3po/1b2a7eaed60d376ab87a8130c29b2ec957bca767/specification/http/src/test/java/org/kaazing/specification/http/rfc7231/RequestHeaderFieldsIT.java/right.java

    /**
     * starts k3po rule.
     */

    @Test
    @Ignore("not complete")
    @Specification({"server.responds.to.unmeetable.expect.with.417/request",
                    "server.responds.to.unmeetable.expect.with.417/response" })
    public void serverRespondsToUnmeetableExpectWith417() throws Exception {
        // A server that receives an Expect field-value that is doesn't recognize
        // MAY respond with a 417 (Expectation Failed) status code to indicate
        // that the unexpected expectation cannot be met.
        k3po.finish();
    }
}
