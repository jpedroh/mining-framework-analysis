package org.assertj.core.api;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.test.ExpectedException.none;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.test.ExpectedException;
import org.junit.Rule;
import org.junit.Test;

public class Assertions_assertThat_with_Throwable_Test {
  @Rule public ExpectedException thrown = none();

  @Test public void should_build_ThrowableAssert_with_runtime_exception_thrown() {
    assertThatThrownBy(() -> {
      throw new IllegalArgumentException("something was wrong");
    }).isInstanceOf(IllegalArgumentException.class).hasMessage("something was wrong");
  }

  @Test public void should_build_ThrowableAssert_with_throwable_thrown() {
    assertThatThrownBy(() -> {
      throw new Throwable("something was wrong");
    }).isInstanceOf(Throwable.class).hasMessage("something was wrong");
  }

  @Test public void should_fail_if_no_throwable_was_thrown() {

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/api/Assertions_assertThat_with_Throwable_Test.java/left.java
    try {
      assertThatThrownBy(() -> {
      }).hasMessage("yo");
    } catch (AssertionError e) {
      assertThat(e).hasMessage("Expecting code to raise a throwable.");
      return;
    }
=======
    thrown.expectAssertionError("Expecting code to raise a throwable.");
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/api/Assertions_assertThat_with_Throwable_Test.java/right.java

    assertThatThrownBy(notRaisingException()).hasMessage("yo");
  }

  @Test public void can_capture_exception_and_then_assert_following_AAA_or_BDD_style() {
    Throwable boom = catchThrowable(raisingException("boom!!!!"));
    assertThat(boom).isInstanceOf(Exception.class).hasMessageContaining("boom");
  }

  @Test public void fail_with_good_message_when_assertion_is_failing() {
    thrown.expectAssertionErrorWithMessageContaining("Expecting message:", "<\"yo\">", "but was:", "<\"boom\">");
    assertThatThrownBy(raisingException("boom")).hasMessage("yo");
  }

  private ThrowingCallable raisingException(final String reason) {
    return () -> {
      throw new Exception(reason);
    };
  }
}