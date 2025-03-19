package org.organicdesign.fp;
import java.util.Arrays;
import org.organicdesign.fp.collections.ImList;
import java.util.Comparator;
import org.organicdesign.fp.collections.ImMap;
import java.util.Map;
import org.organicdesign.fp.collections.ImSet;
import org.organicdesign.fp.collections.ImSortedMap;
import org.organicdesign.fp.collections.ImSortedSet;
import org.organicdesign.fp.collections.MutableList;
import org.organicdesign.fp.collections.MutableUnsortedMap;
import org.organicdesign.fp.collections.MutableUnsortedSet;
import org.organicdesign.fp.collections.PersistentHashMap;
import org.organicdesign.fp.collections.PersistentHashSet;
import org.organicdesign.fp.collections.PersistentTreeMap;
import org.organicdesign.fp.collections.PersistentTreeSet;
import org.organicdesign.fp.collections.PersistentVector;
import org.organicdesign.fp.collections.UnmodIterable;
import org.organicdesign.fp.tuple.Tuple2;
import org.organicdesign.fp.tuple.Tuple3;
import org.organicdesign.fp.xform.Xform;

/**
 <p>A mini data definition language composed of vec(), tup(), map(), set(), plus xform() which makes
 java.util collections transformable.</p>

 <pre><code>import org.organicdesign.fp.StaticImports.*

 // Create a new vector of integers
 vec(1, 2, 3, 4);

 // Create a new set of Strings
 set("a", "b", "c");

 // Create a tuple of an int and a string (a type-safe heterogeneous container)
 tup("a", 1);

 // Create a map with a few key value pairs
 map(tup("a", 1), tup("b", 2), tup("c", 3);</code></pre>

 <p>vec(), map(), and set() are the only three methods in this project to take varargs.  I tried
 writing out versions that took multiple type-safe arguments, but IntelliJ presented you with a
 menu of all of them for auto-completion which was overwhelming, so I reverted to varargs.  Also,
 varargs relax some type safety rules (variance) for data definition in a generally helpful (rarely
 dangerous) way.</p>

 <p>If you're used to Clojure/JSON, you'll find that what's a map (dictionary) in those languages
 usually becomes a tuple in Paguro. A true map data structure in a type-safe language is
 homogeneous, meaning that every member is of the same type (or a descendant of a common ancestor).
 Tuples are designed to contain unrelated data types and enforce those types.</p>

 <p>As with any usage of import *, there could be issues if you import 2 different versions of this
 file in your classpath.  Java needs a data definition language so badly that I think it is worth
 the risk.  Also, I don't anticipate this file changing much, except to add more tup()
 implementations, which shouldn't break anything.  Let me know if you find that the danger outweighs
 convenience or have advice on what to do about it.</p>
 */
@SuppressWarnings(value = { "UnusedDeclaration" }) public final class StaticImports {
  private StaticImports() {
    throw new UnsupportedOperationException("No instantiation");
  }

  /**
     Returns a new PersistentHashMap of the given keys and their paired values.  Use the
     {@link StaticImports#tup(Object, Object)} method to define those key/value pairs briefly and
     easily.  This data definition method is one of the three methods in this project that support
     varargs.

     @param kvPairs Key/value pairs (to go into the map).  In the case of a duplicate key, later
     values in the input list overwrite the earlier ones.  The resulting map can contain zero or one
     null key and any number of null values.  Null k/v pairs will be silently ignored.

     @return a new PersistentHashMap of the given key/value pairs
     */
  @SafeVarargs public static <K extends java.lang.Object, V extends java.lang.Object> ImMap<K, V> map(Map.Entry<K, V>... kvPairs) {
    if ((kvPairs == null) || (kvPairs.length < 1)) {
      return PersistentHashMap.empty();
    }
    return PersistentHashMap.of(Arrays.asList(kvPairs));
  }

  /**
     Returns a new MutableUnsortedMap of the given keys and their paired values.  Use the
     {@link StaticImports#tup(Object, Object)} method to define those key/value pairs briefly and
     easily.  This data definition method is one of the few methods in this project that support
     varargs.

     @param kvPairs Key/value pairs (to go into the map).  In the case of a duplicate key, later
     values in the input list overwrite the earlier ones.  The resulting map can contain zero or one
     null key and any number of null values.  Null k/v pairs will be silently ignored.

     @return a new MutableUnsortedMap of the given key/value pairs
     */
  @SafeVarargs public static <K extends java.lang.Object, V extends java.lang.Object> MutableUnsortedMap<K, V> mutableMap(Map.Entry<K, V>... kvPairs) {
    MutableUnsortedMap<K, V> ret = PersistentHashMap.emptyMutable();
    if (kvPairs == null) {
      return ret;
    }
    for (Map.Entry<K, V> me : kvPairs) {
      ret.assoc(me);
    }
    return ret;
  }

  /**
     Returns a new MutableUnsortedSet of the values.  This data definition method is one of the few
     methods in this project that support varargs.  If the input contains duplicate elements, later
     values overwrite earlier ones.
     */
  @SafeVarargs public static <T extends java.lang.Object> MutableUnsortedSet<T> mutableSet(T... items) {
    MutableUnsortedSet<T> ret = PersistentHashSet.emptyMutable();
    if (items == null) {
      return ret;
    }
    for (T t : items) {
      ret.put(t);
    }
    return ret;
  }

  /**
     Returns a MutableVector of the given items.  This data definition method is one of the
     few methods in this project that support varargs.
     */
  @SafeVarargs static public <T extends java.lang.Object> MutableList<T> mutableVec(T... items) {
    MutableList<T> ret = PersistentVector.emptyMutable();
    if (items == null) {
      return ret;
    }
    for (T t : items) {
      ret.append(t);
    }
    return ret;
  }

  /**
     Returns a new PersistentHashSet of the values.  This data definition method is one of the three
     methods in this project that support varargs.  If the input contains duplicate elements, later
     values overwrite earlier ones.
     */
  @SafeVarargs public static <T extends java.lang.Object> ImSet<T> set(T... items) {
    if ((items == null) || (items.length < 1)) {
      return PersistentHashSet.empty();
    }
    return PersistentHashSet.of(Arrays.asList(items));
  }

  /**
     Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs.  Use
     the tup() method to define those key/value pairs briefly and easily.  The keys are sorted
     according to the comparator you provide.

     @param comp A comparator (on the keys) that defines the sort order inside the new map.  This
     becomes a permanent part of the map and all sub-maps or appended maps derived from it.  If you
     want to use a null key, make sure the comparator treats nulls correctly in all circumstances!

     @param kvPairs Key/value pairs (to go into the map).  In the case of a duplicate key, later
     values in the input list overwrite the earlier ones.  The resulting map can contain zero or one
     null key (if your comparator knows how to sort nulls) and any number of null values.  Null k/v
     pairs will be silently ignored.

     @return a new PersistentTreeMap of the specified comparator and the given key/value pairs
     */
  public static <K extends java.lang.Object, V extends java.lang.Object> ImSortedMap<K, V> sortedMap(Comparator<? super K> comp, Iterable<Map.Entry<K, V>> kvPairs) {
    return PersistentTreeMap.ofComp(comp, kvPairs);
  }

  /**
     Returns a new PersistentTreeMap of the given comparable keys and their paired values, sorted in
     the default ordering of the keys.  Use the tup() method to define those key/value pairs briefly
     and easily.

     @param kvPairs Key/value pairs (to go into the map).  In the case of a duplicate key, later
     values overwrite earlier ones.
     @return a new PersistentTreeMap of the specified comparator and the given key/value pairs which
     uses the default comparator defined on the element type.
     */
  public static <K extends Comparable<K>, V extends java.lang.Object> ImSortedMap<K, V> sortedMap(Iterable<Map.Entry<K, V>> kvPairs) {
    return PersistentTreeMap.of(kvPairs);
  }

  /**
     Returns a new PersistentTreeSet of the given comparator and items.

     @param comp A comparator that defines the sort order of elements in the new set.  This
     becomes part of the set (it's not for pre-sorting).
     @param elements items to go into the set.  In the case of duplicates, later elements overwrite
     earlier ones.
     @return a new PersistentTreeSet of the specified comparator and the given elements
     */
  public static <T extends java.lang.Object> ImSortedSet<T> sortedSet(Comparator<? super T> comp, Iterable<T> elements) {
    return Xform.of(elements).toImSortedSet(comp);
  }

  /** Returns a new PersistentTreeSet of the given comparable items. */
  public static <T extends Comparable<T>> ImSortedSet<T> sortedSet(Iterable<T> items) {
    return PersistentTreeSet.of(items);
  }

  /** Returns a new Tuple2 of the given items. */
  public static <T extends java.lang.Object, U extends java.lang.Object> Tuple2<T, U> tup(T t, U u) {
    return Tuple2.of(t, u);
  }

  /** Returns a new Tuple3 of the given items. */
  public static <T extends java.lang.Object, U extends java.lang.Object, V extends java.lang.Object> Tuple3<T, U, V> tup(T t, U u, V v) {
    return Tuple3.of(t, u, v);
  }

  /**
     Returns a new PersistentVector of the given items.  This data definition method is one of the
     three methods in this project that support varargs.
     */
  @SafeVarargs static public <T extends java.lang.Object> ImList<T> vec(T... items) {
    if ((items == null) || (items.length < 1)) {
      return PersistentVector.empty();
    }
    return PersistentVector.ofIter(Arrays.asList(items));
  }

  /** Wrap a regular Java collection or other iterable outside this project to perform a transformation on it. */
  public static <T extends java.lang.Object> UnmodIterable<T> xform(Iterable<T> iterable) {
    return Xform.of(iterable);
  }

  /**
     If you need to wrap a regular Java array outside this project to perform
     a transformation on it, this method is the most convenient, efficient way to do so.
     */
  @SafeVarargs public static <T extends java.lang.Object> UnmodIterable<T> xformArray(T... items) {
    return Xform.of(Arrays.asList(items));
  }
}