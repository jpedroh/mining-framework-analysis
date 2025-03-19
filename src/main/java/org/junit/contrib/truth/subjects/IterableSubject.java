package org.junit.contrib.truth.subjects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.contrib.truth.FailureStrategy;

/**
 * @author Kevin Bourrillion
 */
public class IterableSubject<S extends IterableSubject<S, T, C>, T extends java.lang.Object, C extends Iterable<T>> extends Subject<S, C> {
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

  public And<S> hasContentsInOrder(Object... expected) {
    List<Object> target = new ArrayList<Object>();
    for (Object t : getSubject()) {
      target.add(t);
    }
    check().that(target).isEqualTo(Arrays.asList(expected));
    return nextChain();
  }

  public And<S> hasContentsAnyOrder(Object... expected) {
    check().that(createFakeMultiset(getSubject())).isEqualTo(createFakeMultiset(Arrays.asList(expected)));
    return nextChain();
  }

  private static Map<Object, Integer> createFakeMultiset(Iterable<?> iterable) {
    Map<Object, Integer> map = new HashMap<Object, Integer>();
    for (Object t : iterable) {
      Integer count = map.get(t);
      map.put(t, (count == null) ? 1 : count + 1);
    }
    return map;
  }
}