package org.organicdesign.fp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 Represents transformations to be carried out on a collection.  This class also implements the
 methods defined in Realizable so that sub-classes can just implement foldLeft and not have to
 worry about any Realizable functions.
 @param <T>
 */
public interface Transformable<T extends java.lang.Object> extends Realizable<T> {
  /**
     Lazily applies the given function to each item in the underlying data source, and returns
     a View with one item for each result.
     @param func a function that returns a new value for any value in the input
     @return a lazy view of the same size as the input (may contain duplicates) containing the
     return values of the given function in the same order as the input values.
     */
  <U extends java.lang.Object> Transformable<U> map(Function<T, U> func);

  /**
     Lazily applies the filter function to the underlying data source and returns a new view
     containing only the items for which the filter returned true
     @param func a function that returns true for items to keep, false for items to drop
     @return a lazy view of only the filtered items.
     */
  Transformable<T> filter(Predicate<T> func);

  /**
     Eagerly processes the entire data source for side effects.
     @param se the function to do the processing
     */
  void forEach(Consumer<T> se);

  /**
     Deprecated: use filter(...).take(1) instead.
     Eagerly returns the first item matching the given predicate.
     @param pred the test that the item needs to pass
     @return the first item that passes the test, or null if no such item is found
     */
  @Deprecated T firstMatching(Predicate<T> pred);

  /**
     One of the two higher-order functions that can produce more output items than input items
     (when u is a collection). FlatMap is the other, but foldLeft is eager while flatMap is lazy.

     @return an eagerly evaluated result which could be a single value like a sum, or a collection.
     @param u the accumulator and starting value.  This will be passed to the function on the
     first iteration to be combined with the first member of the underlying data source.  For some
     operations you'll need to pass an identity, e.g. for a sum, pass 0, for a product, pass 1 as
     this parameter.
     @param fun combines each value in the list with the result so far.  The initial result is u.
     */
  <U extends java.lang.Object> U foldLeft(U u, BiFunction<U, T, U> fun);

  @Override default ArrayList<T> toJavaArrayList() {
    return foldLeft(new ArrayList<T>(), (ts, t) -> {
      ts.add(t);
      return ts;
    });
  }

  @Override default List<T> toJavaUnmodList() {
    return Collections.unmodifiableList(toJavaArrayList());
  }

  @Override default <U extends java.lang.Object> HashMap<T, U> toJavaHashMap(final Function<T, U> f1) {
    return foldLeft(new HashMap<T, U>(), (ts, t) -> {
      ts.put(t, f1.apply(t));
      return ts;
    });
  }

  @Override default <U extends java.lang.Object> Map<T, U> toJavaUnmodMap(Function<T, U> f1) {
    return Collections.unmodifiableMap(toJavaHashMap(f1));
  }

  @Override default <U extends java.lang.Object> HashMap<U, T> toReverseJavaHashMap(final Function<T, U> f1) {
    return foldLeft(new HashMap<U, T>(), (ts, t) -> {
      ts.put(f1.apply(t), t);
      return ts;
    });
  }

  @Override default <U extends java.lang.Object> Map<U, T> toReverseJavaUnmodMap(Function<T, U> f1) {
    return Collections.unmodifiableMap(toReverseJavaHashMap(f1));
  }

  @Override default TreeSet<T> toJavaTreeSet(Comparator<? super T> comparator) {
    return foldLeft(new TreeSet<T>(comparator), (ts, t) -> {
      ts.add(t);
      return ts;
    });
  }

  @Override default TreeSet<T> toJavaTreeSet() {
    return toJavaTreeSet(null);
  }

  @Override default SortedSet<T> toJavaUnmodSortedSet(Comparator<? super T> comparator) {
    return Collections.unmodifiableSortedSet(toJavaTreeSet(comparator));
  }

  @Override default SortedSet<T> toJavaUnmodSortedSet() {
    return toJavaUnmodSortedSet(null);
  }

  @Override default HashSet<T> toJavaHashSet() {
    return foldLeft(new HashSet<T>(), (ts, t) -> {
      ts.add(t);
      return ts;
    });
  }

  @Override default Set<T> toJavaUnmodSet() {
    return Collections.unmodifiableSet(toJavaHashSet());
  }

  @Override @SuppressWarnings(value = { "unchecked" }) default T[] toArray() {
    ArrayList<T> al = toJavaArrayList();
    return al.toArray((T[]) new Object[al.size()]);
  }
}