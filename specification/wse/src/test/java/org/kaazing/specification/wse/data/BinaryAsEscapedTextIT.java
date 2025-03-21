package org.kaazing.specification.wse.data;
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

public class BinaryAsEscapedTextIT {
  private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/wse/data/binary.as.escaped.text");

  private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

  @Rule public final TestRule chain = outerRule(k3po).around(timeout);

  @Test @Specification(value = { "echo.escaped.characters/request", "echo.escaped.characters/response" }) public void shouldEchoEscapedCharacters() throws Exception {
    k3po.finish();
  }

  @Test @Specification(value = { "echo.non.escaped.characters/request", "echo.non.escaped.characters/response" }) public void shouldEchoNonEscapedCharacters() throws Exception {
    k3po.finish();
  }

  @Test @Specification(value = { "echo.payload.length.0/request", "echo.payload.length.0/response" }) public void shouldEchoFrameWithPayloadLength0() throws Exception {
    k3po.finish();
  }

  @Test @Ignore(value = 
<<<<<<< /usr/src/app/output/k3po/k3po/084cc0b426e3b6e5c33eb91da884a1b46aa411e7/specification/wse/src/test/java/org/kaazing/specification/wse/data/BinaryAsEscapedTextIT.java/left.java
  "To be completed when wse spec is complete"
=======
  "Escaping is underspecified, see https://github.com/k3po/k3po/pull/280/files"
>>>>>>> /usr/src/app/output/k3po/k3po/084cc0b426e3b6e5c33eb91da884a1b46aa411e7/specification/wse/src/test/java/org/kaazing/specification/wse/data/BinaryAsEscapedTextIT.java/right.java
  ) @Specification(value = { "echo.payload.length.127/request", "echo.payload.length.127/response" }) public void shouldEchoFrameWithPayloadLength127() throws Exception {
    k3po.finish();
  }
}