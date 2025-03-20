package org.assertj.core.internal.longs;
import static org.assertj.core.test.TestData.someInfo;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.internal.Longs;
import org.assertj.core.internal.LongsBaseTest;
import org.junit.Test;

/**
 * Tests for <code>{@link Longs#assertIsNegative(AssertionInfo, Long)}</code>.
 * 
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Longs_assertIsNegative_Test extends LongsBaseTest {
  @Test public void should_succeed_since_actual_is_negative() {
    longs.assertIsNegative(someInfo(), -6L);
  }

  @Test public void should_fail_since_actual_is_not_negative() {
    thrown.expectAssertionError("%nExpecting:%n <6L>%nto be less than:%n <0L> ");
    longs.assertIsNegative(someInfo(), 6L);
  }

  @Test public void should_fail_since_actual_can_not_be_negative_according_to_custom_comparison_strategy() {
    thrown.expectAssertionError("%nExpecting:%n <-1L>%nto be less than:%n <0L> when comparing values using \'AbsValueComparator\'");
    longsWithAbsValueComparisonStrategy.assertIsNegative(someInfo(), -1L);
  }

  @Test public void should_fail_since_actual_is_not_negative_according_to_custom_comparison_strategy() {
    thrown.expectAssertionError("%nExpecting:%n <1L>%nto be less than:%n <0L> when comparing values using \'AbsValueComparator\'");
    longsWithAbsValueComparisonStrategy.assertIsNegative(someInfo(), 1L);
  }
}