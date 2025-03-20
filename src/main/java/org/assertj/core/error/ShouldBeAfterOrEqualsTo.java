package org.assertj.core.error;
import java.util.Date;
import org.assertj.core.internal.*;

/**
 * Creates an error message indicating that an assertion that verifies that a {@link Date} is after or equals to another
 * one
 * failed.
 * 
 * @author Joel Costigliola
 */
public class ShouldBeAfterOrEqualsTo extends BasicErrorMessageFactory {
  /**
   * Creates a new </code>{@link ShouldBeAfterOrEqualsTo}</code>.
   * 
   * @param actual the actual value in the failed assertion.
   * @param other the value used in the failed assertion to compare the actual value to.
   * @param comparisonStrategy the {@link ComparisonStrategy} used to evaluate assertion.
   * @return the created {@code ErrorMessageFactory}.
   */
  public static ErrorMessageFactory shouldBeAfterOrEqualsTo(Object actual, Object other, ComparisonStrategy comparisonStrategy) {
    return new ShouldBeAfterOrEqualsTo(actual, other, comparisonStrategy);
  }

  /**
   * Creates a new </code>{@link ShouldBeAfterOrEqualsTo}</code>.
   * 
   * @param actual the actual value in the failed assertion.
   * @param other the value used in the failed assertion to compare the actual value to.
   * @return the created {@code ErrorMessageFactory}.
   */
  public static ErrorMessageFactory shouldBeAfterOrEqualsTo(Object actual, Object other) {
    return new ShouldBeAfterOrEqualsTo(actual, other, StandardComparisonStrategy.instance());
  }

  private ShouldBeAfterOrEqualsTo(Object actual, Object other, ComparisonStrategy comparisonStrategy) {
    super("\nExpecting:\n  <%s>\nto be after or equals to:\n  <%s>%s", actual, other, comparisonStrategy);
  }
}