package org.assertj.core.api;
import java.util.List;
import org.assertj.core.util.VisibleForTesting;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * Same as {@link SoftAssertions}, but with the following differences: <br/>
 * First, it's a junit rule, which can be used without having to call {@link SoftAssertions#assertAll() assertAll()},
 * example:
 *
 * <pre><code class='java'>
 *   public class SoftlyTest {
 *
 *     &#064;Rule
 *     public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
 *
 *     &#064;Test
 *     public void testSoftly() throws Exception {
 *       softly.assertThat(1).isEqualTo(2);
 *       softly.assertThat(Lists.newArrayList(1, 2)).containsOnly(1, 2);
 *     }
 *  }
 * </code></pre>
 *
 * Second, the failures are recognized by IDE's (like IntelliJ IDEA) which open a comparison window.
 */
public class JUnitSoftAssertions extends AbstractStandardSoftAssertions implements TestRule {
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override public void evaluate() throws Throwable {
        base.evaluate();
        MultipleFailureException.assertEmpty(proxies.errorsCollected());
      }
    };
  }

  @VisibleForTesting List<Throwable> getErrors() {
    return proxies.errorsCollected();
  }
}