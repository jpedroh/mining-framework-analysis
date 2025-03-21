package org.apache.commons.lang3;
import java.io.IOException;
import org.apache.commons.lang3.Functions.FailableBiConsumer;
import java.io.UncheckedIOException;
import org.apache.commons.lang3.Functions.FailableBiFunction;
import java.lang.reflect.UndeclaredThrowableException;
import org.apache.commons.lang3.Functions.FailableCallable;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.Functions.FailableConsumer;
import java.util.function.BiConsumer;
import org.apache.commons.lang3.Functions.FailableFunction;
import java.util.function.BiFunction;
import org.apache.commons.lang3.Functions.FailableSupplier;
import org.junit.jupiter.api.DisplayName;
import java.util.function.BiPredicate;
import org.junit.jupiter.api.Test;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunctionsTest {
  static class SomeException extends Exception {
    private static final long serialVersionUID = -4965704778119283411L;

    private Throwable t;

    SomeException(String pMsg) {
      super(pMsg);
    }
  }

  static class Testable {
    private Throwable t;

    Testable(Throwable pTh) {
      t = pTh;
    }

    void setThrowable(Throwable pThrowable) {
      t = pThrowable;
    }

    void test() throws Throwable {
      test(t);
    }

    void test(Throwable pThrowable) throws Throwable {
      if (pThrowable != null) {
        throw pThrowable;
      }
    }

    Integer testInt() throws Throwable {
      return testInt(t);
    }

    boolean testBool() throws Throwable {
      return testBool(t);
    }

    Integer testInt(Throwable pThrowable) throws Throwable {
      if (pThrowable != null) {
        throw pThrowable;
      }
      return 0;
    }

    boolean testBool(Throwable pThrowable) throws Throwable {
      if (pThrowable != null) {
        throw pThrowable;
      }
      return false;
    }
  }

  static class FailureOnOddInvocations {
    private static int invocation;

    private static void throwOnOdd() throws SomeException {
      final int i = ++invocation;
      if (i % 2 == 1) {
        throw new SomeException("Odd Invocation: " + i);
      }
    }

    static boolean failingBool() throws SomeException {
      throwOnOdd();
      return true;
    }

    FailureOnOddInvocations() throws SomeException {
      throwOnOdd();
    }
  }

  static class CloseableObject {
    private boolean closed;

    void run(Throwable pTh) throws Throwable {
      if (pTh != null) {
        throw pTh;
      }
    }

    void reset() {
      closed = false;
    }

    void close() {
      closed = true;
    }

    boolean isClosed() {
      return closed;
    }
  }

  @Test void testRunnable() {
    FailureOnOddInvocations.invocation = 0;
    UndeclaredThrowableException e = assertThrows(UndeclaredThrowableException.class, () -> Functions.run(FailureOnOddInvocations::new));
    final Throwable cause = e.getCause();
    assertNotNull(cause);
    assertTrue(cause instanceof SomeException);
    assertEquals("Odd Invocation: 1", cause.getMessage());
    Functions.run(FailureOnOddInvocations::new);
  }

  @Test void testAsRunnable() {
    FailureOnOddInvocations.invocation = 0;
    Runnable runnable = Functions.asRunnable(() -> new FailureOnOddInvocations());
    UndeclaredThrowableException e = assertThrows(UndeclaredThrowableException.class, () -> runnable.run());
    final Throwable cause = e.getCause();
    assertNotNull(cause);
    assertTrue(cause instanceof SomeException);
    assertEquals("Odd Invocation: 1", cause.getMessage());
    runnable.run();
  }

  @Test void testCallable() {
    FailureOnOddInvocations.invocation = 0;
    UndeclaredThrowableException e = assertThrows(UndeclaredThrowableException.class, () -> Functions.run(FailureOnOddInvocations::new));
    final Throwable cause = e.getCause();
    assertNotNull(cause);
    assertTrue(cause instanceof SomeException);
    assertEquals("Odd Invocation: 1", cause.getMessage());
    final FailureOnOddInvocations instance = Functions.call(FailureOnOddInvocations::new);
    assertNotNull(instance);
  }

  @Test void testAsCallable() {
    FailureOnOddInvocations.invocation = 0;
    final FailableCallable<FailureOnOddInvocations, SomeException> failableCallable = () -> {
      return new FailureOnOddInvocations();
    };
    final Callable<FailureOnOddInvocations> callable = Functions.asCallable(failableCallable);
    UndeclaredThrowableException e = assertThrows(UndeclaredThrowableException.class, () -> callable.call());
    final Throwable cause = e.getCause();
    assertNotNull(cause);
    assertTrue(cause instanceof SomeException);
    assertEquals("Odd Invocation: 1", cause.getMessage());
    final FailureOnOddInvocations instance;
    try {
      instance = callable.call();
    } catch (Exception ex) {
      throw Functions.rethrow(ex);
    }
    assertNotNull(instance);
  }

  @Test void testAcceptConsumer() {
    final IllegalStateException ise = new IllegalStateException();
    final Testable testable = new Testable(ise);
    Throwable e = assertThrows(IllegalStateException.class, () -> Functions.accept(Testable::test, testable));
    assertSame(ise, e);
    final Error error = new OutOfMemoryError();
    testable.setThrowable(error);
    e = assertThrows(OutOfMemoryError.class, () -> Functions.accept(Testable::test, testable));
    assertSame(error, e);
    final IOException ioe = new IOException("Unknown I/O error");
    testable.setThrowable(ioe);
    e = assertThrows(UncheckedIOException.class, () -> Functions.accept(Testable::test, testable));
    final Throwable t = e.getCause();
    assertNotNull(t);
    assertSame(ioe, t);
    testable.setThrowable(null);
    Functions.accept(Testable::test, testable);
  }

  @Test void testAsConsumer() {
    final IllegalStateException ise = new IllegalStateException();
    final Testable testable = new Testable(ise);
    final Consumer<Testable> consumer = Functions.asConsumer((t) -> t.test());
    Throwable e = assertThrows(IllegalStateException.class, () -> consumer.accept(testable));
    assertSame(ise, e);
    final Error error = new OutOfMemoryError();
    testable.setThrowable(error);
    e = assertThrows(OutOfMemoryError.class, () -> consumer.accept(testable));
    assertSame(error, e);
    final IOException ioe = new IOException("Unknown I/O error");
    testable.setThrowable(ioe);
    e = assertThrows(UncheckedIOException.class, () -> consumer.accept(testable));
    final Throwable t = e.getCause();
    assertNotNull(t);
    assertSame(ioe, t);
    testable.setThrowable(null);
    Functions.accept(Testable::test, testable);
  }

  @Test void testAcceptBiConsumer() {
    final IllegalStateException ise = new IllegalStateException();
    final Testable testable = new Testable(null);
    Throwable e = assertThrows(IllegalStateException.class, () -> Functions.accept(Testable::test, testable, ise));
    assertSame(ise, e);
    final Error error = new OutOfMemoryError();
    e = assertThrows(OutOfMemoryError.class, () -> Functions.accept(Testable::test, testable, error));
    assertSame(error, e);
    final IOException ioe = new IOException("Unknown I/O error");
    testable.setThrowable(ioe);
    e = assertThrows(UncheckedIOException.class, () -> Functions.accept(Testable::test, testable, ioe));
    final Throwable t = e.getCause();
    assertNotNull(t);
    assertSame(ioe, t);
    testable.setThrowable(null);
    Functions.accept(Testable::test, testable, (Throwable) null);
  }

  @Test void testAsBiConsumer() {
    final IllegalStateException ise = new IllegalStateException();
    final Testable testable = new Testable(null);
    final FailableBiConsumer<Testable, Throwable, Throwable> failableBiConsumer = (t, th) -> {
      t.setThrowable(th);
      t.test();
    };
    final BiConsumer<Testable, Throwable> consumer = Functions.asBiConsumer(failableBiConsumer);
    Throwable e = assertThrows(IllegalStateException.class, () -> consumer.accept(testable, ise));
    assertSame(ise, e);
    final Error error = new OutOfMemoryError();
    e = assertThrows(OutOfMemoryError.class, () -> consumer.accept(testable, error));
    assertSame(error, e);
    final IOException ioe = new IOException("Unknown I/O error");
    testable.setThrowable(ioe);
    e = assertThrows(UncheckedIOException.class, () -> consumer.accept(testable, ioe));
    final Throwable t = e.getCause();
    assertNotNull(t);
    assertSame(ioe, t);
    consumer.accept(testable, null);
  }

  @Test void testApplyFunction() {
    final IllegalStateException ise = new IllegalStateException();
    final Testable testable = new Testable(ise);
    Throwable e = assertThrows(IllegalStateException.class, () -> Functions.apply(Testable::testInt, testable));
    assertSame(ise, e);
    final Error error = new OutOfMemoryError();
    testable.setThrowable(error);
    e = assertThrows(OutOfMemoryError.class, () -> Functions.apply(Testable::testInt, testable));
    assertSame(error, e);
    final IOException ioe = new IOException("Unknown I/O error");
    testable.setThrowable(ioe);
    e = assertThrows(UncheckedIOException.class, () -> Functions.apply(Testable::testInt, testable));
    final Throwable t = e.getCause();
    assertNotNull(t);
    assertSame(ioe, t);
    testable.setThrowable(null);
    final Integer i = Functions.apply(Testable::testInt, testable);
    assertNotNull(i);
    assertEquals(0, i.intValue());
  }

  @Test void testAsFunction() {
    final IllegalStateException ise = new IllegalStateException();
    final Testable testable = new Testable(ise);
    final FailableFunction<Throwable, Integer, Throwable> failableFunction = (th) -> {
      testable.setThrowable(th);
      return Integer.valueOf(testable.testInt());
    };
    final Function<Throwable, Integer> function = Functions.asFunction(failableFunction);
    Throwable e = assertThrows(IllegalStateException.class, () -> function.apply(ise));
    assertSame(ise, e);
    final Error error = new OutOfMemoryError();
    testable.setThrowable(error);
    e = assertThrows(OutOfMemoryError.class, () -> function.apply(error));
    assertSame(error, e);
    final IOException ioe = new IOException("Unknown I/O error");
    testable.setThrowable(ioe);
    e = assertThrows(UncheckedIOException.class, () -> function.apply(ioe));
    final Throwable t = e.getCause();
    assertNotNull(t);
    assertSame(ioe, t);
    assertEquals(0, function.apply(null));
  }

  @Test @DisplayName(value = "Test that asPredicate(FailablePredicate) is converted to -> Predicate ") public void testAsPredicate() {

<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    final IllegalStateException ise = new IllegalStateException();
=======
    FailureOnOddInvocations.invocation = 0;
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java

    final Testable testable = new Testable(ise);
    final Functions.FailablePredicate<Throwable, Object, Throwable> failablePredicate = 
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    (th) -> {
      testable.setThrowable(th);
      return testable.testBool();
    }
=======
    (t) -> FailureOnOddInvocations.failingBool()
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    ;
    final Predicate<
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    Throwable
=======
    ?
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    > predicate = Functions.asPredicate(failablePredicate);

<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    Throwable
=======
    UndeclaredThrowableException
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
     e = assertThrows(
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    IllegalStateException
=======
    UndeclaredThrowableException
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    .class, () -> predicate.test(
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    ise
=======
    null
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    ));
    assertSame(ise, e);
    final 
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    Error
=======
    Throwable
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
     
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    error = new OutOfMemoryError()
=======
    cause = e.getCause()
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    ;
    testable.setThrowable(error);
    e = assertThrows(OutOfMemoryError.class, () -> predicate.test(error));

<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    assertSame(error, e)
=======
    assertNotNull(cause)
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    ;
    final IOException ioe = new IOException("Unknown I/O error");

<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    testable.setThrowable(ioe);
=======
    assertTrue(cause instanceof SomeException);
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java


<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    e = assertThrows(UncheckedIOException.class, () -> predicate.test(ioe));
=======
    assertEquals("Odd Invocation: 1", cause.getMessage());
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java


<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    final Throwable t = e.getCause();
=======
    final boolean instance = predicate.test(null);
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java

    assertNotNull(
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    t
=======
    instance
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    );
    assertSame(ioe, t);
    assertEquals(false, predicate.test(null));
  }

  @Test void testApplyBiFunction() {
    final IllegalStateException ise = new IllegalStateException();
    final Testable testable = new Testable(null);
    Throwable e = assertThrows(IllegalStateException.class, () -> Functions.apply(Testable::testInt, testable, ise));
    assertSame(ise, e);
    final Error error = new OutOfMemoryError();
    e = assertThrows(OutOfMemoryError.class, () -> Functions.apply(Testable::testInt, testable, error));
    assertSame(error, e);
    final IOException ioe = new IOException("Unknown I/O error");
    e = assertThrows(UncheckedIOException.class, () -> Functions.apply(Testable::testInt, testable, ioe));
    final Throwable t = e.getCause();
    assertNotNull(t);
    assertSame(ioe, t);
    final Integer i = Functions.apply(Testable::testInt, testable, (Throwable) null);
    assertNotNull(i);
    assertEquals(0, i.intValue());
  }

  @Test @DisplayName(value = "Test that asPredicate(FailableBiPredicate) is converted to -> BiPredicate ") public void testAsBiPredicate() {

<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    final IllegalStateException ise = new IllegalStateException();
=======
    FailureOnOddInvocations.invocation = 0;
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java

    final Testable testable = new Testable(ise);
    final Functions.FailableBiPredicate<Throwable, Object, Throwable, Object, Throwable> failableBiPredicate = 
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    (th1, th2) -> {
      testable.setThrowable(th1);
      return testable.testBool();
    }
=======
    (t1, t2) -> FailureOnOddInvocations.failingBool()
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    ;
    final BiPredicate<
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    Throwable
=======
    ?
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    , 
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    Throwable
=======
    ?
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    > predicate = Functions.asBiPredicate(failableBiPredicate);

<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    Throwable
=======
    UndeclaredThrowableException
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
     e = assertThrows(
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    IllegalStateException
=======
    UndeclaredThrowableException
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    .class, () -> predicate.test(
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    ise
=======
    null
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    , 
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    ise
=======
    null
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    ));
    assertSame(ise, e);
    final 
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    Error
=======
    Throwable
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
     
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    error = new OutOfMemoryError()
=======
    cause = e.getCause()
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    ;
    testable.setThrowable(error);
    e = assertThrows(OutOfMemoryError.class, () -> predicate.test(error, error));

<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    assertSame(error, e)
=======
    assertNotNull(cause)
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    ;
    final IOException ioe = new IOException("Unknown I/O error");

<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    testable.setThrowable(ioe);
=======
    assertTrue(cause instanceof SomeException);
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java


<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    e = assertThrows(UncheckedIOException.class, () -> predicate.test(ioe, ioe));
=======
    assertEquals("Odd Invocation: 1", cause.getMessage());
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java


<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    final Throwable t = e.getCause();
=======
    final boolean instance = predicate.test(null, null);
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java

    assertNotNull(
<<<<<<< /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/left.java
    t
=======
    instance
>>>>>>> /usr/src/app/output/apache/commons-lang/3dc5b155952bbe0a6904496cc8e87f3a512bcb34/src/test/java/org/apache/commons/lang3/FunctionsTest.java/right.java
    );
    assertSame(ioe, t);
    assertEquals(false, predicate.test(null, null));
  }

  @Test void testAsBiFunction() {
    final IllegalStateException ise = new IllegalStateException();
    final Testable testable = new Testable(ise);
    final FailableBiFunction<Testable, Throwable, Integer, Throwable> failableBiFunction = (t, th) -> {
      t.setThrowable(th);
      return Integer.valueOf(t.testInt());
    };
    final BiFunction<Testable, Throwable, Integer> biFunction = Functions.asBiFunction(failableBiFunction);
    Throwable e = assertThrows(IllegalStateException.class, () -> biFunction.apply(testable, ise));
    assertSame(ise, e);
    final Error error = new OutOfMemoryError();
    testable.setThrowable(error);
    e = assertThrows(OutOfMemoryError.class, () -> biFunction.apply(testable, error));
    assertSame(error, e);
    final IOException ioe = new IOException("Unknown I/O error");
    testable.setThrowable(ioe);
    e = assertThrows(UncheckedIOException.class, () -> biFunction.apply(testable, ioe));
    final Throwable t = e.getCause();
    assertNotNull(t);
    assertSame(ioe, t);
    assertEquals(0, biFunction.apply(testable, null).intValue());
  }

  @Test void testGetFromSupplier() {
    FailureOnOddInvocations.invocation = 0;
    UndeclaredThrowableException e = assertThrows(UndeclaredThrowableException.class, () -> Functions.run(FailureOnOddInvocations::new));
    final Throwable cause = e.getCause();
    assertNotNull(cause);
    assertTrue(cause instanceof SomeException);
    assertEquals("Odd Invocation: 1", cause.getMessage());
    final FailureOnOddInvocations instance = Functions.call(FailureOnOddInvocations::new);
    assertNotNull(instance);
  }

  @Test void testAsSupplier() {
    FailureOnOddInvocations.invocation = 0;
    final FailableSupplier<FailureOnOddInvocations, Throwable> failableSupplier = () -> new FailureOnOddInvocations();
    final Supplier<FailureOnOddInvocations> supplier = Functions.asSupplier(failableSupplier);
    UndeclaredThrowableException e = assertThrows(UndeclaredThrowableException.class, () -> supplier.get());
    final Throwable cause = e.getCause();
    assertNotNull(cause);
    assertTrue(cause instanceof SomeException);
    assertEquals("Odd Invocation: 1", cause.getMessage());
    final FailureOnOddInvocations instance = supplier.get();
    assertNotNull(instance);
  }

  @Test void testTryWithResources() {
    final CloseableObject co = new CloseableObject();
    final FailableConsumer<Throwable, ? extends Throwable> consumer = co::run;
    final IllegalStateException ise = new IllegalStateException();
    Throwable e = assertThrows(IllegalStateException.class, () -> Functions.tryWithResources(() -> consumer.accept(ise), co::close));
    assertSame(ise, e);
    assertTrue(co.isClosed());
    co.reset();
    final Error error = new OutOfMemoryError();
    e = assertThrows(OutOfMemoryError.class, () -> Functions.tryWithResources(() -> consumer.accept(error), co::close));
    assertSame(error, e);
    assertTrue(co.isClosed());
    co.reset();
    final IOException ioe = new IOException("Unknown I/O error");
    UncheckedIOException uioe = assertThrows(UncheckedIOException.class, () -> Functions.tryWithResources(() -> consumer.accept(ioe), co::close));
    final IOException cause = uioe.getCause();
    assertSame(ioe, cause);
    assertTrue(co.isClosed());
    co.reset();
    Functions.tryWithResources(() -> consumer.accept(null), co::close);
    assertTrue(co.isClosed());
  }

  @Test void testRethrowNull() {
    assertThrows(NullPointerException.class, () -> Functions.rethrow(null));
  }
}