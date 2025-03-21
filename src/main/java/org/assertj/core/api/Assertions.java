package org.assertj.core.api;
import java.nio.file.Path;

/**
 * Entry point for assertion methods for different data types. Each method in this class is a static factory for the
 * type-specific assertion objects. The purpose of this class is to make test code more readable.
 * <p>
 * For example:
 * <pre><code class='java'> int removed = employees.removeFired();
 * {@link Assertions#assertThat(int) assertThat}(removed).{@link IntegerAssert#isZero isZero}();
 *
 * List&lt;Employee&gt; newEmployees = employees.hired(TODAY);
 * {@link Assertions#assertThat(Iterable) assertThat}(newEmployees).{@link IterableAssert#hasSize(int) hasSize}(6);</code></pre>
 * <p/>
 * </p>
 *
 * @author Alex Ruiz
 * @author Yvonne Wang
 * @author David DIDIER
 * @author Ted Young
 * @author Joel Costigliola
 * @author Matthieu Baechler
 * @author Mikhail Mazursky
 * @author Nicolas François
 * @author Julien Meddah
 * @author William Delanoue
 * @author Turbo87
 * @author dorzey
 */
public class Assertions extends Java6Assertions {
  /**
   * Creates a new instance of {@link PathAssert}
   *
   * @param actual the path to test
   * @return the created assertion object
   */
  public static AbstractPathAssert<?> assertThat(Path actual) {
    return new PathAssert(actual);
  }

  /**
   * Creates a new </code>{@link Assertions}</code>.
   */
  protected Assertions() {
  }
}