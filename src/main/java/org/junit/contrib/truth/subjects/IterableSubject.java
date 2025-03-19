package org.junit.contrib.truth.subjects;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.contrib.truth.FailureStrategy;
import org.junit.contrib.truth.util.GwtCompatible;

/**
 * @author Kevin Bourrillion
 */
@GwtCompatible public class IterableSubject<S extends IterableSubject<S, T, C>, T extends java.lang.Object, C extends Iterable<T>> extends Subject<S, C> {
  @SuppressWarnings(value = { "unchecked", "rawtypes" }) public static <T extends java.lang.Object, C extends Iterable<T>> IterableSubject<? extends IterableSubject<?, T, C>, T, C> create(FailureStrategy failureStrategy, Iterable<T> list) {
    return new IterableSubject(failureStrategy, list);
  }

  protected IterableSubject(FailureStrategy failureStrategy, C list) {
    super(failureStrategy, list);
  }

  public And<S> contains(Object item) {
    for (Object t : getSubject()) {
      if (item == t || item != null && item.equals(t)) {
        return nextChain();
      }
    }
    fail("contains", item);
    throw new AssertionError();
  }

  /**
   * Attests that a Collection contains the provided object or fails.
   */
  public And<S> isEmpty() {
    if (getSubject().iterator().hasNext()) {
      fail("isEmpty");
    }
    return nextChain();
  }

  /**
   * Asserts that the items are supplied in the order given by the iterable. For
   * Collections and other things which contain items but may not have guaranteed
   * iteration order, this method should be overridden.
   */
  public And<S> iteratesOverSequence(Object... expectedItems) {
    Iterator<T> actualItems = getSubject().iterator();
    for (Object expected : expectedItems) {
      if (!actualItems.hasNext()) {
        fail("iterates through", Arrays.asList(expectedItems));
      } else {
        Object actual = actualItems.next();
        if (actual == expected || actual != null && actual.equals(expected)) {
          continue;
        } else {
          fail("iterates through", Arrays.asList(expectedItems));
        }
      }
    }
    if (actualItems.hasNext()) {
      fail("iterates through", Arrays.asList(expectedItems));
    }
    return nextChain();
  }
}