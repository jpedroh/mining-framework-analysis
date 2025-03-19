package org.junit.contrib.truth.subjects;
import org.junit.contrib.truth.FailureStrategy;
import org.junit.contrib.truth.TestVerb;
import org.junit.contrib.truth.util.GwtCompatible;
import org.junit.contrib.truth.util.GwtIncompatible;

/**
 * Propositions for arbitrarily typed subjects and for properties
 * of Object
 *
 * @author David Saff
 * @author Christian Gruber (cgruber@israfil.net)
 */
@GwtCompatible(emulated = true) public class Subject<S extends Subject<S, T>, T extends java.lang.Object> {
  private final FailureStrategy failureStrategy;

  private final T subject;

  private final And<S> chain;

  public Subject(FailureStrategy failureStrategy, T subject) {
    this.failureStrategy = failureStrategy;
    this.subject = subject;
    this.chain = new And<S>() {
      @SuppressWarnings(value = { "unchecked" }) @Override public S and() {
        return (S) Subject.this;
      }
    };
  }

  /**
   * A method which wraps the current Subject concrete
   * subtype in a chaining "And" object.
   */
  protected final And<S> nextChain() {
    return chain;
  }

  public And<S> is(T other) {
    if (getSubject() == null) {
      if (other != null) {
        fail("is", other);
      }
    } else {
      if (!getSubject().equals(other)) {
        fail("is", other);
      }
    }
    return nextChain();
  }

  public And<S> isNull() {
    if (getSubject() != null) {
      failWithoutSubject("is null");
    }
    return nextChain();
  }

  public And<S> isNotNull() {
    if (getSubject() == null) {
      failWithoutSubject("is not null");
    }
    return nextChain();
  }

  public And<S> isEqualTo(Object other) {
    if (getSubject() == null) {
      if (other != null) {
        fail("is equal to", other);
      }
    } else {
      if (!getSubject().equals(other)) {
        fail("is equal to", other);
      }
    }
    return nextChain();
  }

  public And<S> isNotEqualTo(Object other) {
    if (getSubject() == null) {
      if (other == null) {
        fail("is not equal to", other);
      }
    } else {
      if (getSubject().equals(other)) {
        fail("is not equal to", other);
      }
    }
    return nextChain();
  }

  @GwtIncompatible(value = "Class.isInstance") public And<S> isA(Class<?> clazz) {
    if (!clazz.isInstance(getSubject())) {
      fail("is a", clazz.getName());
    }
    return nextChain();
  }

  @GwtIncompatible(value = "Class.isInstance") public And<S> isNotA(Class<?> clazz) {
    if (clazz.isInstance(getSubject())) {
      fail("is not a", clazz.getName());
    }
    return nextChain();
  }

  protected T getSubject() {
    return subject;
  }

  protected TestVerb check() {
    return new TestVerb(failureStrategy);
  }

  /**
   * Assembles a failure message and passes such to the FailureStrategy
   * @param verb the act being asserted
   * @param messageParts the expectations against which the subject is compared
   */
  protected void fail(String verb, Object... messageParts) {
    String message = "Not true that ";
    message += "<" + getSubject() + "> " + verb;
    for (Object part : messageParts) {
      message += " <" + part + ">";
    }
    failureStrategy.fail(message);
  }

  protected void failWithoutSubject(String verb) {
    String message = "Not true that ";
    message += "the subject " + verb;
    failureStrategy.fail(message);
  }

  public static interface And<C extends java.lang.Object> {
    /**
     * Returns the next object in the chain of anded objects.
     */
    C and();
  }
}