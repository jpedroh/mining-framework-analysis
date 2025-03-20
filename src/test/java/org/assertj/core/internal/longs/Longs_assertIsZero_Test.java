package org.assertj.core.internal.longs;
import static org.assertj.core.test.TestData.someInfo;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.internal.Longs;
import org.assertj.core.internal.LongsBaseTest;
import org.junit.Test;

/**
 * Tests for <code>{@link Longs#assertIsNegative(AssertionInfo, Comparable)}</code>.
 * 
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Longs_assertIsZero_Test extends LongsBaseTest {
  @Test public void should_succeed_since_actual_is_zero() {
    longs.assertIsZero(someInfo(), 0L);
  }

  @Test public void should_fail_since_actual_is_not_zero() {

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/internal/longs/Longs_assertIsZero_Test.java/left.java
    try {
      longs.assertIsZero(someInfo(), 2L);
    } catch (AssertionError e) {
      assertThat(e.getMessage()).isEqualTo("expected:<[0]L> but was:<[2]L>");
    }
=======
    thrown.expectAssertionError("expected:<[0]L> but was:<[2]L>");
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/internal/longs/Longs_assertIsZero_Test.java/right.java

    longs.assertIsZero(someInfo(), 2l);
  }

  @Test public void should_succeed_since_actual_is_not_zero_whatever_custom_comparison_strategy_is() {
    longsWithAbsValueComparisonStrategy.assertIsNotZero(someInfo(), 1L);
  }

  @Test public void should_fail_since_actual_is_zero_whatever_custom_comparison_strategy_is() {
    thrown.expectAssertionError("%nExpecting:%n <0L>%nnot to be equal to:%n <0L>%n");
    longsWithAbsValueComparisonStrategy.assertIsNotZero(someInfo(), 0L);
  }
}