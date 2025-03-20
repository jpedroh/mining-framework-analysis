package org.assertj.core.api;
import org.assertj.core.internal.Failures;

/**
 * Common failures.
 * 
 * @author Alex Ruiz
 * @author Yvonne Wang
 * @author Joel Costigliola
 */
public final class Fail {
  /**
   * Sets whether we remove elements related to AssertJ from assertion error stack trace.
   * @param removeAssertJRelatedElementsFromStackTrace flag.
   */
  public static void setRemoveAssertJRelatedElementsFromStackTrace(boolean removeAssertJRelatedElementsFromStackTrace) {
    Failures.instance().setRemoveAssertJRelatedElementsFromStackTrace(removeAssertJRelatedElementsFromStackTrace);
  }

  /**
   * Fails with the given message.
   * @param failureMessage error message.
   * @throws AssertionError with the given message.
   */
  public static void fail(String failureMessage) {
    throw Failures.instance().failure(failureMessage);
  }

  /**
   * Throws an {@link AssertionError} with the given message built as {@link String#format(String, Object...)}.
   * 
   * @param failureMessage error message.
   * @param args Arguments referenced by the format specifiers in the format string.
   * @throws AssertionError with the given built message.
   */
  public static void fail(String failureMessage, Object... args) {
    fail(String.format(failureMessage, args));
  }

  /**
   * Throws an {@link AssertionError} with the given message and with the {@link Throwable} that caused the failure.
   * @param failureMessage the description of the failed assertion. It can be {@code null}.
   * @param realCause cause of the error.
   * @throws AssertionError with the given message and with the {@link Throwable} that caused the failure.
   */
  public static void fail(String failureMessage, Throwable realCause) {
    AssertionError error = Failures.instance().failure(failureMessage);
    error.initCause(realCause);
    throw error;
  }

  /**
   * Throws an {@link AssertionError} with a message explaining that a {@link Throwable} of given class was expected to be thrown
   * but had not been.
   * @param throwableClass the Throwable class that was expected to be thrown.
   * @throws AssertionError with a message explaining that a {@link Throwable} of given class was expected to be thrown but had
   *           not been.
   *
   * {@link Fail#shouldHaveThrown(Class)} can be used as a replacement.
   */
  public static void failBecauseExceptionWasNotThrown(Class<? extends Throwable> throwableClass) {
    shouldHaveThrown(throwableClass);
  }

  /**
   * Throws an {@link AssertionError} with a message explaining that a {@link Throwable} of given class was expected to be thrown
   * but had not been.
   * @param throwableClass the Throwable class that was expected to be thrown.
   * @throws AssertionError with a message explaining that a {@link Throwable} of given class was expected to be thrown but had
   *           not been.
   */
  public static void shouldHaveThrown(Class<? extends Throwable> throwableClass) {
    throw Failures.instance().expectedThrowableNotThrown(throwableClass);
  }

  /**
   * This constructor is protected to make it possible to subclass this class. Since all its methods are static, there is no point
   * on creating a new instance of it.
   */
  protected Fail() {
  }
}