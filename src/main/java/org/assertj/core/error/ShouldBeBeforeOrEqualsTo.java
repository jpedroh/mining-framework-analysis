package org.assertj.core.error;
import org.assertj.core.internal.ComparisonStrategy;
import org.assertj.core.internal.StandardComparisonStrategy;

/**
 * Creates an error message indicating that an assertion that verifies that a {@link Object} is before or equals to another one
 * failed.
 * 
 * @author Joel Costigliola
 */
public class ShouldBeBeforeOrEqualsTo extends BasicErrorMessageFactory {
  /**
   * Creates a new </code>{@link ShouldBeBeforeOrEqualsTo}</code>.
   * @param actual the actual value in the failed assertion.
   * @param other the value used in the failed assertion to compare the actual value to.
   * @param comparisonStrategy the {@link ComparisonStrategy} used to evaluate assertion.
   * @return the created {@code ErrorMessageFactory}.
   */
  public static ErrorMessageFactory shouldBeBeforeOrEqualsTo(Object actual, Object other, ComparisonStrategy comparisonStrategy) {
    return new ShouldBeBeforeOrEqualsTo(actual, other, comparisonStrategy);
  }

  /**
   * Creates a new </code>{@link ShouldBeBeforeOrEqualsTo}</code>.
   * @param actual the actual value in the failed assertion.
   * @param other the value used in the failed assertion to compare the actual value to.
   * @return the created {@code ErrorMessageFactory}.
   */
  public static ErrorMessageFactory shouldBeBeforeOrEqualsTo(Object actual, Object other) {
    return new ShouldBeBeforeOrEqualsTo(actual, other, StandardComparisonStrategy.instance());
  }

  private ShouldBeBeforeOrEqualsTo(Object actual, Object other, ComparisonStrategy comparisonStrategy) {
    super("\nExpecting:\n  <%s>\nto be before or equals to:\n  <%s>%s", actual, other, comparisonStrategy);
  }
}