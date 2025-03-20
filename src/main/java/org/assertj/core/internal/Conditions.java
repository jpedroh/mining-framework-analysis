package org.assertj.core.internal;
import static java.lang.String.format;
import static org.assertj.core.error.ShouldBe.shouldBe;
import static org.assertj.core.error.ShouldHave.shouldHave;
import static org.assertj.core.error.ShouldNotBe.shouldNotBe;
import static org.assertj.core.error.ShouldNotHave.shouldNotHave;
import static org.assertj.core.util.Preconditions.checkNotNull;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Condition;
import org.assertj.core.util.VisibleForTesting;

/**
 * Verifies that a value satisfies a <code>{@link Condition}</code>.
 * 
 * @author Alex Ruiz
 */
public class Conditions {
  private static final Conditions INSTANCE = new Conditions();

  /**
   * Returns the singleton instance of this class.
   * @return the singleton instance of this class.
   */
  public static Conditions instance() {
    return INSTANCE;
  }

  @VisibleForTesting Failures failures = Failures.instance();

  @VisibleForTesting Conditions() {
  }

  /**
   * Asserts that the actual value satisfies the given <code>{@link Condition}</code>.
   * @param <T> the type of the actual value and the type of values that given {@code Condition} takes.
   * @param info contains information about the assertion.
   * @param actual the actual value.
   * @param condition the given {@code Condition}.
   * @throws NullPointerException if the given {@code Condition} is {@code null}.
   * @throws AssertionError if the actual value does not satisfy the given {@code Condition}.
   */
  public <T extends java.lang.Object> void assertIs(AssertionInfo info, T actual, Condition<? super T> condition) {
    assertIsNotNull(condition);
    if (condition.matches(actual)) {
      return;
    }
    throw failures.failure(info, shouldBe(actual, condition));
  }

  /**
   * Asserts that the actual value does not satisfy the given <code>{@link Condition}</code>.
   * @param <T> the type of the actual value and the type of values that given {@code Condition} takes.
   * @param info contains information about the assertion.
   * @param actual the actual value.
   * @param condition the given {@code Condition}.
   * @throws NullPointerException if the given {@code Condition} is {@code null}.
   * @throws AssertionError if the actual value satisfies the given {@code Condition}.
   */
  public <T extends java.lang.Object> void assertIsNot(AssertionInfo info, T actual, Condition<? super T> condition) {
    assertIsNotNull(condition);
    if (!condition.matches(actual)) {
      return;
    }
    throw failures.failure(info, shouldNotBe(actual, condition));
  }

  /**
   * Asserts that the actual value satisfies the given <code>{@link Condition}</code>.
   * @param <T> the type of the actual value and the type of values that given {@code Condition} takes.
   * @param info contains information about the assertion.
   * @param actual the actual value.
   * @param condition the given {@code Condition}.
   * @throws NullPointerException if the given {@code Condition} is {@code null}.
   * @throws AssertionError if the actual value does not satisfy the given {@code Condition}.
   */
  public <T extends java.lang.Object> void assertHas(AssertionInfo info, T actual, Condition<? super T> condition) {
    assertIsNotNull(condition);
    if (condition.matches(actual)) {
      return;
    }
    throw failures.failure(info, shouldHave(actual, condition));
  }

  /**
   * Asserts that the actual value does not satisfy the given <code>{@link Condition}</code>.
   * @param <T> the type of the actual value and the type of values that given {@code Condition} takes.
   * @param info contains information about the assertion.
   * @param actual the actual value.
   * @param condition the given {@code Condition}.
   * @throws NullPointerException if the given {@code Condition} is {@code null}.
   * @throws AssertionError if the actual value satisfies the given {@code Condition}.
   */
  public <T extends java.lang.Object> void assertDoesNotHave(AssertionInfo info, T actual, Condition<? super T> condition) {
    assertIsNotNull(condition);
    if (!condition.matches(actual)) {
      return;
    }
    throw failures.failure(info, shouldNotHave(actual, condition));
  }

  /**
   * Asserts the the given <code>{@link Condition}</code> is not null.
   * @param condition the given {@code Condition}.
   * @throws NullPointerException if the given {@code Condition} is {@code null}.
   */
  public void assertIsNotNull(Condition<?> condition) {
    assertIsNotNull(condition, "The condition to evaluate should not be null");
  }

  /**
   * Asserts the the given <code>{@link Condition}</code> is not null.
   * @param condition the given {@code Condition}.
   * @param  format as in {@link String#format(String, Object...)}
   * @param  args as in {@link String#format(String, Object...)}
   * @throws NullPointerException if the given {@code Condition} is {@code null}.
   */
  public void assertIsNotNull(Condition<?> condition, String format, Object... args) {
    checkNotNull(condition, format(format, args));
  }
}