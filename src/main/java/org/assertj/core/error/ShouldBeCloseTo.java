package org.assertj.core.error;
import static java.lang.String.format;
import static org.assertj.core.util.DateUtil.formatAsDatetimeWithMs;
import java.time.temporal.Temporal;
import java.util.Date;

/**
 * Creates an error message indicating that an assertion that verifies that a {@link Date} is close to another one from some delta
 * failed.
 *
 * @author Joel Costigliola
 */
public class ShouldBeCloseTo extends BasicErrorMessageFactory {
  /**
   * Creates a new </code>{@link ShouldBeCloseTo}</code>.
   * @param actual the actual value in the failed assertion.
   * @param other the value used in the failed assertion to compare the actual value to.
   * @param deltaInMilliseconds the delta used for date comparison, expressed in milliseconds.
   * @param difference the difference in milliseconds between actual and other dates.
   * @return the created {@code ErrorMessageFactory}.
   */
  public static ErrorMessageFactory shouldBeCloseTo(Date actual, Date other, long deltaInMilliseconds, long difference) {
    return new ShouldBeCloseTo(actual, other, deltaInMilliseconds, difference);
  }

  /**
   * Creates a new </code>{@link ShouldBeCloseTo}</code>.
   * @param actual the actual value in the failed assertion.
   * @param other the value used in the failed assertion to compare the actual value to.
   * @param differenceDescription detailed difference description message.
   * @return the created {@code ErrorMessageFactory}.
   * @since 3.7.0
   */
  public static ErrorMessageFactory shouldBeCloseTo(Temporal actual, Temporal other, String differenceDescription) {
    return new ShouldBeCloseTo(actual, other, differenceDescription);
  }

  private ShouldBeCloseTo(Date actual, Date other, long deltaInMilliseconds, long difference) {
    super(format("%nExpecting:%n <%s>%nto be close to:%n <%s>%nby less than %sms but difference was %sms", formatAsDatetimeWithMs(actual), formatAsDatetimeWithMs(other), deltaInMilliseconds, difference));
  }

  private ShouldBeCloseTo(Temporal actual, Temporal other, String differenceDescription) {
    super(format("%nExpecting:%n <%s>%nto be close to:%n <%s>%n%s", actual, other, differenceDescription));
  }
}