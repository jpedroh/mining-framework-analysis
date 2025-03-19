package org.organicdesign.fp.collections;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import org.organicdesign.fp.collections.PersistentTreeMap.Box;
import org.organicdesign.fp.function.Fn2;
import org.organicdesign.fp.oneOf.Option;
import org.organicdesign.fp.tuple.Tuple2;
import static org.organicdesign.fp.collections.UnmodIterator.emptyUnmodIterator;

/**
 Rich Hickey's immutable rendition of Phil Bagwell's Hash Array Mapped Trie.

 Uses path copying for persistence,
 HashCollision leaves vs. extended hashing,
 Node polymorphism vs. conditionals,
 No sub-tree pools or root-resizing.
 Any errors are my own (said Rich, but now says Glen 2015-06-06).

 This file is a derivative work based on a Clojure collection licensed under the Eclipse Public
 License 1.0 Copyright Rich Hickey.  Errors are Glen Peterson's.
 */
public class PersistentHashMap<K extends java.lang.Object, V extends java.lang.Object> extends AbstractUnmodMap<K, V> implements ImMap<K, V>, Serializable {
  private static class Iter<K extends java.lang.Object, V extends java.lang.Object, R extends java.lang.Object> implements UnmodIterator<R> {
    private boolean seen = false;

    private final UnmodIterator<R> rootIter;

    private final Fn2<K, V, R> aFn;

    private final V nullValue;

    private Iter(UnmodIterator<R> ri, Fn2<K, V, R> aFn, V nv) {
      rootIter = ri;
      this.aFn = aFn;
      nullValue = nv;
    }

    @Override public boolean hasNext() {
      if (!seen) {
        return true;
      } else {
        return rootIter.hasNext();
      }
    }

    @Override public R next() {
      if (!seen) {
        seen = true;
        return aFn.apply(null, nullValue);
      } else {
        return rootIter.next();
      }
    }
  }

  private static int mask(int hash, int shift) {
    return (hash >>> shift) & 0x01f;
  }

  @SuppressWarnings(value = { "unchecked" }) private static <K extends java.lang.Object> K k(Object[] array, int i) {
    return (K) array[i];
  }

  @SuppressWarnings(value = { "unchecked" }) private static <V extends java.lang.Object> V v(Object[] array, int i) {
    return (V) array[i];
  }

  @SuppressWarnings(value = { "unchecked" }) private static <K extends java.lang.Object, V extends java.lang.Object> INode<K, V> iNode(Object[] array, int i) {
    return (INode<K, V>) array[i];
  }

  final public static PersistentHashMap<Object, Object> EMPTY = new PersistentHashMap<>(null, 0, null, false, null);

  @SuppressWarnings(value = { "unchecked" }) public static <K extends java.lang.Object, V extends java.lang.Object> PersistentHashMap<K, V> empty() {
    return (PersistentHashMap<K, V>) EMPTY;
  }

  /** Works around some type inference limitations of Java 8. */
  public static <K extends java.lang.Object, V extends java.lang.Object> MutHashMap<K, V> emptyMutable() {
    return PersistentHashMap.<K, V>empty().mutable();
  }

  @SuppressWarnings(value = { "unchecked" }) public static <K extends java.lang.Object, V extends java.lang.Object> PersistentHashMap<K, V> empty(Equator<K> e) {
    return new PersistentHashMap<>(e, 0, null, false, null);
  }

  /** Works around some type inference limitations of Java 8. */
  public static <K extends java.lang.Object, V extends java.lang.Object> MutHashMap<K, V> emptyMutable(Equator<K> e) {
    return PersistentHashMap.<K, V>empty(e).mutable();
  }

  /**
     Returns a new PersistentHashMap of the given keys and their paired values, skipping any null
     Entries.
     */
  @SuppressWarnings(value = { "WeakerAccess" }) public static <K extends java.lang.Object, V extends java.lang.Object> PersistentHashMap<K, V> ofEq(Equator<K> eq, Iterable<Map.Entry<K, V>> es) {
    if (es == null) {
      return empty(eq);
    }
    MutHashMap<K, V> map = emptyMutable(eq);
    for (Map.Entry<K, V> entry : es) {
      if (entry != null) {
        map.assoc(entry.getKey(), entry.getValue());
      }
    }
    return map.immutable();
  }

  /**
     Returns a new PersistentHashMap of the given keys and their paired values.  There is also a
     varargs version of this method: {@link org.organicdesign.fp.StaticImports#map(Map.Entry...)}.  Use
     the {@link org.organicdesign.fp.StaticImports#tup(Object, Object)} method to define key/value
     pairs briefly and easily.

     @param kvPairs Key/value pairs (to go into the map).  In the case of a duplicate key, later
     values in the input list overwrite the earlier ones.  The resulting map can contain zero or one
     null key and any number of null values.  Null k/v pairs will be silently ignored.

     @return a new PersistentHashMap of the given key/value pairs
      */
  public static <K extends java.lang.Object, V extends java.lang.Object> PersistentHashMap<K, V> of(Iterable<Map.Entry<K, V>> kvPairs) {
    if (kvPairs == null) {
      return empty();
    }
    PersistentHashMap<K, V> m = empty();
    MutHashMap<K, V> map = m.mutable();
    for (Map.Entry<K, V> entry : kvPairs) {
      if (entry != null) {
        map.assoc(entry.getKey(), entry.getValue());
      }
    }
    return map.immutable();
  }

  private final Equator<K> equator;

  private final int size;

  private transient final INode<K, V> root;

  private final boolean hasNull;

  private final V nullValue;

  private PersistentHashMap(Equator<K> eq, int sz, INode<K, V> root, boolean hasNull, V nullValue) {
    this.equator = (eq == null) ? Equator.defaultEquator() : eq;
    this.size = sz;
    this.root = root;
    this.hasNull = hasNull;
    this.nullValue = nullValue;
  }

  private static final long serialVersionUID = 20160903192900L;

  private static class SerializationProxy<K extends java.lang.Object, V extends java.lang.Object> implements Serializable {
    private final Equator<K> equator;

    private final int size;

    private transient ImMap<K, V> theMap;

    SerializationProxy(PersistentHashMap<K, V> phm) {
      equator = phm.equator;
      size = phm.size;
      theMap = phm;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      for (UnEntry<K, V> entry : theMap) {
        s.writeObject(entry.getKey());
        s.writeObject(entry.getValue());
      }
    }

    private static final long serialVersionUID = 20160827174100L;

    @SuppressWarnings(value = { "unchecked" }) private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      MutMap tempMap = new PersistentHashMap<K, V>(equator, 0, null, false, null).mutable();
      for (int i = 0; i < size; i++) {
        tempMap.assoc(s.readObject(), s.readObject());
      }
      theMap = tempMap.immutable();
    }

    private Object readResolve() {
      return theMap;
    }
  }

  private Object writeReplace() {
    return new SerializationProxy<>(this);
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    throw new InvalidObjectException("Proxy required");
  }

  /** {@inheritDoc} */
  @Override public Equator<K> equator() {
    return equator;
  }

  @Override public PersistentHashMap<K, V> assoc(K key, V val) {
    if (key == null) {
      if (hasNull && (val == nullValue)) {
        return this;
      }
      return new PersistentHashMap<>(equator, hasNull ? size : size + 1, root, true, val);
    }
    Box<Box> addedLeaf = new Box<>(null);
    INode<K, V> newroot = (root == null ? BitmapIndexedNode.empty(equator) : root);
    newroot = newroot.assoc(0, equator.hash(key), key, val, addedLeaf);
    if (newroot == root) {
      return this;
    }
    return new PersistentHashMap<>(equator, addedLeaf.val == null ? size : size + 1, newroot, hasNull, nullValue);
  }

  @Override public MutHashMap<K, V> mutable() {
    return new MutHashMap<>(this);
  }

  @Override public Option<UnmodMap.UnEntry<K, V>> entry(K key) {
    if (key == null) {
      return hasNull ? Option.some(Tuple2.of(null, nullValue)) : Option.none();
    }
    if (root == null) {
      return Option.none();
    }
    UnEntry<K, V> entry = root.find(0, equator.hash(key), key);
    return Option.someOrNullNoneOf(entry);
  }

  @Override public UnmodIterator<UnEntry<K, V>> iterator() {
    return iterator(Tuple2::of);
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public UnmodIterator<K> keyIterator() {
    return iterator(Fn2.Singletons.FIRST);
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public UnmodIterator<V> valIterator() {
    return iterator(Fn2.Singletons.SECOND);
  }

  private <R extends java.lang.Object> UnmodIterator<R> iterator(Fn2<K, V, R> aFn) {
    final UnmodIterator<R> rootIter = (root == null) ? emptyUnmodIterator() : root.iterator(aFn);
    return (hasNull) ? new Iter<>(rootIter, aFn, nullValue) : rootIter;
  }

  /** {@inheritDoc} */
  @Override public int size() {
    return size;
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public PersistentHashMap<K, V> without(K key) {
    if (key == null) {
      return hasNull ? new PersistentHashMap<>(equator, size - 1, root, false, null) : this;
    }
    if (root == null) {
      return this;
    }
    INode<K, V> newroot = root.without(0, equator.hash(key), key);
    if (newroot == root) {
      return this;
    }
    return new PersistentHashMap<>(equator, size - 1, newroot, hasNull, nullValue);
  }


<<<<<<< /usr/src/app/output/glenkpeterson/j-sicle/bf4de20006b5405bf4c84c50a037ef0b28a060ab/src/main/java/org/organicdesign/fp/collections/PersistentHashMap.java/left.java
  public static final class MutHashMap<K extends java.lang.Object, V extends java.lang.Object> extends AbstractUnmodMap<K, V> implements MutMap<K, V> {
    private AtomicReference<Thread> edit;

    private final Equator<K> equator;

    private INode<K, V> root;

    private int count;

    private boolean hasNull;

    private V nullValue;

    private final Box<Box> leafFlag = new Box<>(null);

    private MutHashMap(PersistentHashMap<K, V> m) {
      this(m.equator(), new AtomicReference<>(Thread.currentThread()), m.root, m.size, m.hasNull, m.nullValue);
    }

    private MutHashMap(Equator<K> e, AtomicReference<Thread> edit, INode<K, V> root, int count, boolean hasNull, V nullValue) {
      this.equator = (e == null) ? Equator.defaultEquator() : e;
      this.edit = edit;
      this.root = root;
      this.count = count;
      this.hasNull = hasNull;
      this.nullValue = nullValue;
    }

    @Override public Equator<K> equator() {
      return equator;
    }

    @Override public MutHashMap<K, V> assoc(K key, V val) {
      ensureEditable();
      if (key == null) {
        if (this.nullValue != val) {
          this.nullValue = val;
        }
        if (!hasNull) {
          this.count++;
          this.hasNull = true;
        }
        return this;
      }
      leafFlag.val = null;
      INode<K, V> n = (root == null ? BitmapIndexedNode.empty(equator) : root);
      n = n.assoc(edit, 0, equator.hash(key), key, val, leafFlag);
      if (n != this.root) {
        this.root = n;
      }
      if (leafFlag.val != null) {
        this.count++;
      }
      return this;
    }

    @Override public Option<UnEntry<K, V>> entry(K key) {
      ensureEditable();
      if (key == null) {
        return hasNull ? Option.some(Tuple2.of(null, nullValue)) : Option.none();
      }
      if (root == null) {
        return Option.none();
      }
      UnEntry<K, V> entry = root.find(0, equator.hash(key), key);
      return Option.someOrNullNoneOf(entry);
    }

    @Override public UnmodIterator<UnEntry<K, V>> iterator() {
      final UnmodIterator<UnEntry<K, V>> rootIter = (root == null) ? emptyUnmodIterator() : root.iterator();
      return (hasNull) ? new Iter<>(rootIter, nullValue) : rootIter;
    }

    @Override public final MutHashMap<K, V> without(K key) {
      ensureEditable();
      if (key == null) {
        if (!hasNull) {
          return this;
        }
        hasNull = false;
        nullValue = null;
        this.count--;
        return this;
      }
      if (root == null) {
        return this;
      }
      leafFlag.val = null;
      INode<K, V> n = root.without(edit, 0, equator.hash(key), key, leafFlag);
      if (n != root) {
        this.root = n;
      }
      if (leafFlag.val != null) {
        this.count--;
      }
      return this;
    }

    @Override public final PersistentHashMap<K, V> immutable() {
      ensureEditable();
      edit.set(null);
      return new PersistentHashMap<>(equator, count, root, hasNull, nullValue);
    }

    @Override public final int size() {
      ensureEditable();
      return count;
    }

    private void ensureEditable() {
      if (edit.get() == null) {
        throw new IllegalAccessError("Mutable used after immutable! call");
      }
    }
  }
=======
  public static final class MutableHashMap<K extends java.lang.Object, V extends java.lang.Object> extends AbstractUnmodMap<K, V> implements MutableMap<K, V> {
    private AtomicReference<Thread> edit;

    private final Equator<K> equator;

    private INode<K, V> root;

    private int count;

    private boolean hasNull;

    private V nullValue;

    private final Box<Box> leafFlag = new Box<>(null);

    private MutableHashMap(PersistentHashMap<K, V> m) {
      this(m.equator(), new AtomicReference<>(Thread.currentThread()), m.root, m.size, m.hasNull, m.nullValue);
    }

    private MutableHashMap(Equator<K> e, AtomicReference<Thread> edit, INode<K, V> root, int count, boolean hasNull, V nullValue) {
      this.equator = (e == null) ? Equator.defaultEquator() : e;
      this.edit = edit;
      this.root = root;
      this.count = count;
      this.hasNull = hasNull;
      this.nullValue = nullValue;
    }

    @Override public Equator<K> equator() {
      return equator;
    }

    @Override public MutableHashMap<K, V> assoc(K key, V val) {
      ensureEditable();
      if (key == null) {
        if (this.nullValue != val) {
          this.nullValue = val;
        }
        if (!hasNull) {
          this.count++;
          this.hasNull = true;
        }
        return this;
      }
      leafFlag.val = null;
      INode<K, V> n = (root == null ? BitmapIndexedNode.empty(equator) : root);
      n = n.assoc(edit, 0, equator.hash(key), key, val, leafFlag);
      if (n != this.root) {
        this.root = n;
      }
      if (leafFlag.val != null) {
        this.count++;
      }
      return this;
    }

    @Override public Option<UnEntry<K, V>> entry(K key) {
      ensureEditable();
      if (key == null) {
        return hasNull ? Option.some(Tuple2.of(null, nullValue)) : Option.none();
      }
      if (root == null) {
        return Option.none();
      }
      UnEntry<K, V> entry = root.find(0, equator.hash(key), key);
      return Option.someOrNullNoneOf(entry);
    }

    @Override public UnmodIterator<UnEntry<K, V>> iterator() {
      return iterator(Tuple2::of);
    }

    @SuppressWarnings(value = { "unchecked" }) @Override public UnmodIterator<K> keyIterator() {
      return iterator(Fn2.Singletons.FIRST);
    }

    @SuppressWarnings(value = { "unchecked" }) @Override public UnmodIterator<V> valIterator() {
      return iterator(Fn2.Singletons.SECOND);
    }

    private <R extends java.lang.Object> UnmodIterator<R> iterator(Fn2<K, V, R> aFn) {
      final UnmodIterator<R> rootIter = (root == null) ? emptyUnmodIterator() : root.iterator(aFn);
      return (hasNull) ? new Iter<>(rootIter, aFn, nullValue) : rootIter;
    }

    @Override public final MutableHashMap<K, V> without(K key) {
      ensureEditable();
      if (key == null) {
        if (!hasNull) {
          return this;
        }
        hasNull = false;
        nullValue = null;
        this.count--;
        return this;
      }
      if (root == null) {
        return this;
      }
      leafFlag.val = null;
      INode<K, V> n = root.without(edit, 0, equator.hash(key), key, leafFlag);
      if (n != root) {
        this.root = n;
      }
      if (leafFlag.val != null) {
        this.count--;
      }
      return this;
    }

    @Override public final PersistentHashMap<K, V> immutable() {
      ensureEditable();
      edit.set(null);
      return new PersistentHashMap<>(equator, count, root, hasNull, nullValue);
    }

    @Override public final int size() {
      ensureEditable();
      return count;
    }

    private void ensureEditable() {
      if (edit.get() == null) {
        throw new IllegalAccessError("Mutable used after immutable! call");
      }
    }
  }
>>>>>>> /usr/src/app/output/glenkpeterson/j-sicle/bf4de20006b5405bf4c84c50a037ef0b28a060ab/src/main/java/org/organicdesign/fp/collections/PersistentHashMap.java/right.java


  private interface INode<K extends java.lang.Object, V extends java.lang.Object> {
    INode<K, V> assoc(int shift, int hash, K key, V val, Box<Box> addedLeaf);

    INode<K, V> without(int shift, int hash, K key);

    UnEntry<K, V> find(int shift, int hash, K key);

    INode<K, V> assoc(AtomicReference<Thread> edit, int shift, int hash, K key, V val, Box<Box> addedLeaf);

    INode<K, V> without(AtomicReference<Thread> edit, int shift, int hash, K key, Box<Box> removedLeaf);

    <R extends java.lang.Object> UnmodIterator<R> iterator(Fn2<K, V, R> aFn);
  }

  private final static class ArrayNode<K extends java.lang.Object, V extends java.lang.Object> implements INode<K, V>, UnmodIterable<UnEntry<K, V>> {
    private final Equator<K> equator;

    int count;

    final INode<K, V>[] array;

    final AtomicReference<Thread> edit;

    ArrayNode(Equator<K> eq, AtomicReference<Thread> edit, int count, INode<K, V>[] array) {
      this.equator = eq;
      this.array = array;
      this.edit = edit;
      this.count = count;
    }

    @Override public INode<K, V> assoc(int shift, int hash, K key, V val, Box<Box> addedLeaf) {
      int idx = mask(hash, shift);
      INode<K, V> node = array[idx];
      if (node == null) {
        BitmapIndexedNode<K, V> e = BitmapIndexedNode.empty(equator);
        INode<K, V> n = e.assoc(shift + 5, hash, key, val, addedLeaf);
        return new ArrayNode<>(equator, null, count + 1, cloneAndSet(array, idx, n));
      }
      INode<K, V> n = node.assoc(shift + 5, hash, key, val, addedLeaf);
      if (n == node) {
        return this;
      }
      return new ArrayNode<>(equator, null, count, cloneAndSet(array, idx, n));
    }

    @Override public INode<K, V> without(int shift, int hash, K key) {
      int idx = mask(hash, shift);
      INode<K, V> node = array[idx];
      if (node == null) {
        return this;
      }
      INode<K, V> n = node.without(shift + 5, hash, key);
      if (n == node) {
        return this;
      }
      if (n == null) {
        if (count <= 8) {
          return pack(null, idx);
        }
        return new ArrayNode<>(equator, null, count - 1, cloneAndSet(array, idx, null));
      } else {
        return new ArrayNode<>(equator, null, count, cloneAndSet(array, idx, n));
      }
    }

    @Override public UnmodMap.UnEntry<K, V> find(int shift, int hash, K key) {
      int idx = mask(hash, shift);
      INode<K, V> node = array[idx];
      if (node == null) {
        return null;
      }
      return node.find(shift + 5, hash, key);
    }

    @Override public UnmodIterator<UnEntry<K, V>> iterator() {
      return iterator(Tuple2::of);
    }

    @Override public <R extends java.lang.Object> UnmodIterator<R> iterator(Fn2<K, V, R> aFn) {
      return new Iter<>(array, aFn);
    }

    private ArrayNode<K, V> ensureEditable(AtomicReference<Thread> edit) {
      if (this.edit == edit) {
        return this;
      }
      return new ArrayNode<>(equator, edit, count, this.array.clone());
    }

    private ArrayNode<K, V> editAndSet(AtomicReference<Thread> edit, int i, INode<K, V> n) {
      ArrayNode<K, V> editable = ensureEditable(edit);
      editable.array[i] = n;
      return editable;
    }

    private INode<K, V> pack(AtomicReference<Thread> edit, int idx) {
      Object[] newArray = new Object[2 * (count - 1)];
      int j = 1;
      int bitmap = 0;
      for (int i = 0; i < idx; i++) {
        if (array[i] != null) {
          newArray[j] = array[i];
          bitmap |= 1 << i;
          j += 2;
        }
      }
      for (int i = idx + 1; i < array.length; i++) {
        if (array[i] != null) {
          newArray[j] = array[i];
          bitmap |= 1 << i;
          j += 2;
        }
      }
      return new BitmapIndexedNode<>(equator, edit, bitmap, newArray);
    }

    @Override public INode<K, V> assoc(AtomicReference<Thread> edit, int shift, int hash, K key, V val, Box<Box> addedLeaf) {
      int idx = mask(hash, shift);
      INode<K, V> node = array[idx];
      if (node == null) {
        BitmapIndexedNode<K, V> en = BitmapIndexedNode.empty(equator);
        ArrayNode<K, V> editable = editAndSet(edit, idx, en.assoc(edit, shift + 5, hash, key, val, addedLeaf));
        editable.count++;
        return editable;
      }
      INode<K, V> n = node.assoc(edit, shift + 5, hash, key, val, addedLeaf);
      if (n == node) {
        return this;
      }
      return editAndSet(edit, idx, n);
    }

    @Override public INode<K, V> without(AtomicReference<Thread> edit, int shift, int hash, K key, Box<Box> removedLeaf) {
      int idx = mask(hash, shift);
      INode<K, V> node = array[idx];
      if (node == null) {
        return this;
      }
      INode<K, V> n = node.without(edit, shift + 5, hash, key, removedLeaf);
      if (n == node) {
        return this;
      }
      if (n == null) {
        if (count <= 8) {
          return pack(edit, idx);
        }
        ArrayNode<K, V> editable = editAndSet(edit, idx, null);
        editable.count--;
        return editable;
      }
      return editAndSet(edit, idx, n);
    }

    @Override public String toString() {
      return UnmodIterable.toString("ArrayNode", this);
    }

    private static class Iter<K extends java.lang.Object, V extends java.lang.Object, R extends java.lang.Object> implements UnmodIterator<R> {
      private final INode<K, V>[] array;

      private Fn2<K, V, R> aFn;

      private int i = 0;

      private UnmodIterator<R> nestedIter;

      private Iter(INode<K, V>[] array, Fn2<K, V, R> aFn) {
        this.array = array;
        this.aFn = aFn;
      }

      @Override public boolean hasNext() {
        while (true) {
          if (nestedIter != null) {
            if (nestedIter.hasNext()) {
              return true;
            } else {
              nestedIter = null;
            }
          }
          if (i < array.length) {
            INode<K, V> node = array[i++];
            if (node != null) {
              nestedIter = node.iterator(aFn);
            }
          } else {
            return false;
          }
        }
      }

      @Override public R next() {
        if (hasNext()) {
          return nestedIter.next();
        } else {
          throw new NoSuchElementException();
        }
      }
    }
  }

  @SuppressWarnings(value = { "unchecked" }) private final static class BitmapIndexedNode<K extends java.lang.Object, V extends java.lang.Object> implements INode<K, V> {
    static <K extends java.lang.Object, V extends java.lang.Object> BitmapIndexedNode<K, V> empty(Equator<K> e) {
      return new BitmapIndexedNode(e, null, 0, new Object[0]);
    }

    private final Equator<K> equator;

    int bitmap;

    Object[] array;

    final AtomicReference<Thread> edit;

    @Override public String toString() {
      return "BitmapIndexedNode(" + bitmap + "," + Arrays.toString(array) + "," + edit + ")";
    }

    final int index(int bit) {
      return Integer.bitCount(bitmap & (bit - 1));
    }

    BitmapIndexedNode(Equator<K> equator, AtomicReference<Thread> edit, int bitmap, Object[] array) {
      this.equator = equator;
      this.bitmap = bitmap;
      this.array = array;
      this.edit = edit;
    }

    @Override public INode<K, V> assoc(int shift, int hash, K key, V val, Box<Box> addedLeaf) {
      int bit = bitpos(hash, shift);
      int idx = index(bit);
      if ((bitmap & bit) != 0) {
        K keyOrNull = k(array, 2 * idx);
        Object valOrNode = array[2 * idx + 1];
        if (keyOrNull == null) {
          INode<K, V> n = ((INode) valOrNode).assoc(shift + 5, hash, key, val, addedLeaf);
          if (n == valOrNode) {
            return this;
          }
          return new BitmapIndexedNode<>(equator, null, bitmap, cloneAndSet(array, 2 * idx + 1, n));
        }
        if (equator.eq(key, keyOrNull)) {
          if (val == valOrNode) {
            return this;
          }
          return new BitmapIndexedNode<>(equator, null, bitmap, cloneAndSet(array, 2 * idx + 1, val));
        }
        addedLeaf.val = addedLeaf;
        return new BitmapIndexedNode<>(equator, null, bitmap, cloneAndSet(array, 2 * idx, 2 * idx + 1, createNode(equator, shift + 5, keyOrNull, valOrNode, hash, key, val)));
      } else {
        int n = Integer.bitCount(bitmap);
        if (n >= 16) {
          INode[] nodes = new INode[32];
          int jdx = mask(hash, shift);
          nodes[jdx] = empty(equator).assoc(shift + 5, hash, key, val, addedLeaf);
          int j = 0;
          for (int i = 0; i < 32; i++) {
            if (((bitmap >>> i) & 1) != 0) {
              if (array[j] == null) {
                nodes[i] = (INode) array[j + 1];
              } else {
                nodes[i] = empty(equator).assoc(shift + 5, equator.hash(k(array, j)), k(array, j), array[j + 1], addedLeaf);
              }
              j += 2;
            }
          }
          return new ArrayNode(equator, null, n + 1, nodes);
        } else {
          Object[] newArray = new Object[2 * (n + 1)];
          System.arraycopy(array, 0, newArray, 0, 2 * idx);
          newArray[2 * idx] = key;
          addedLeaf.val = addedLeaf;
          newArray[2 * idx + 1] = val;
          System.arraycopy(array, 2 * idx, newArray, 2 * (idx + 1), 2 * (n - idx));
          return new BitmapIndexedNode<>(equator, null, bitmap | bit, newArray);
        }
      }
    }

    @Override public INode<K, V> without(int shift, int hash, K key) {
      int bit = bitpos(hash, shift);
      if ((bitmap & bit) == 0) {
        return this;
      }
      int idx = index(bit);
      K keyOrNull = (K) array[2 * idx];
      Object valOrNode = array[2 * idx + 1];
      if (keyOrNull == null) {
        INode<K, V> n = ((INode) valOrNode).without(shift + 5, hash, key);
        if (n == valOrNode) {
          return this;
        }
        if (n != null) {
          return new BitmapIndexedNode<>(equator, null, bitmap, cloneAndSet(array, 2 * idx + 1, n));
        }
        if (bitmap == bit) {
          return null;
        }
        return new BitmapIndexedNode<>(equator, null, bitmap ^ bit, removePair(array, idx));
      }
      if (equator.eq(key, keyOrNull)) {
        return new BitmapIndexedNode<>(equator, null, bitmap ^ bit, removePair(array, idx));
      }
      return this;
    }

    @Override public UnEntry<K, V> find(int shift, int hash, K key) {
      int bit = bitpos(hash, shift);
      if ((bitmap & bit) == 0) {
        return null;
      }
      int idx = index(bit);
      K keyOrNull = k(array, 2 * idx);
      Object valOrNode = array[2 * idx + 1];
      if (keyOrNull == null) {
        return ((INode) valOrNode).find(shift + 5, hash, key);
      }
      if (equator.eq(key, keyOrNull)) {
        return Tuple2.of(keyOrNull, (V) valOrNode);
      }
      return null;
    }

    @Override public <R extends java.lang.Object> UnmodIterator<R> iterator(Fn2<K, V, R> aFn) {
      return new NodeIter<>(array, aFn);
    }

    private BitmapIndexedNode<K, V> ensureEditable(AtomicReference<Thread> edit) {
      if (this.edit == edit) {
        return this;
      }
      int n = Integer.bitCount(bitmap);
      Object[] newArray = new Object[n >= 0 ? 2 * (n + 1) : 4];
      System.arraycopy(array, 0, newArray, 0, 2 * n);
      return new BitmapIndexedNode<>(equator, edit, bitmap, newArray);
    }

    private BitmapIndexedNode<K, V> editAndSet(AtomicReference<Thread> edit, int i, Object a) {
      BitmapIndexedNode editable = ensureEditable(edit);
      editable.array[i] = a;
      return editable;
    }

    private BitmapIndexedNode<K, V> editAndSet(AtomicReference<Thread> edit, int i, int j, Object b) {
      BitmapIndexedNode editable = ensureEditable(edit);
      editable.array[i] = null;
      editable.array[j] = b;
      return editable;
    }

    private BitmapIndexedNode<K, V> editAndRemovePair(AtomicReference<Thread> edit, int bit, int i) {
      if (bitmap == bit) {
        return null;
      }
      BitmapIndexedNode<K, V> editable = ensureEditable(edit);
      editable.bitmap ^= bit;
      System.arraycopy(editable.array, 2 * (i + 1), editable.array, 2 * i, editable.array.length - 2 * (i + 1));
      editable.array[editable.array.length - 2] = null;
      editable.array[editable.array.length - 1] = null;
      return editable;
    }

    @Override public INode<K, V> assoc(AtomicReference<Thread> edit, int shift, int hash, K key, V val, Box<Box> addedLeaf) {
      int bit = bitpos(hash, shift);
      int idx = index(bit);
      if ((bitmap & bit) != 0) {
        K keyOrNull = k(array, 2 * idx);
        Object valOrNode = array[2 * idx + 1];
        if (keyOrNull == null) {
          INode<K, V> n = ((INode<K, V>) valOrNode).assoc(edit, shift + 5, hash, key, val, addedLeaf);
          if (n == valOrNode) {
            return this;
          }
          return editAndSet(edit, 2 * idx + 1, n);
        }
        if (equator.eq(key, keyOrNull)) {
          if (val == valOrNode) {
            return this;
          }
          return editAndSet(edit, 2 * idx + 1, val);
        }
        addedLeaf.val = addedLeaf;
        return editAndSet(edit, 2 * idx, 2 * idx + 1, createNode(equator, edit, shift + 5, keyOrNull, valOrNode, hash, key, val));
      } else {
        int n = Integer.bitCount(bitmap);
        if (n * 2 < array.length) {
          addedLeaf.val = addedLeaf;
          BitmapIndexedNode<K, V> editable = ensureEditable(edit);
          System.arraycopy(editable.array, 2 * idx, editable.array, 2 * (idx + 1), 2 * (n - idx));
          editable.array[2 * idx] = key;
          editable.array[2 * idx + 1] = val;
          editable.bitmap |= bit;
          return editable;
        }
        if (n >= 16) {
          INode[] nodes = new INode[32];
          int jdx = mask(hash, shift);
          nodes[jdx] = empty(equator).assoc(edit, shift + 5, hash, key, val, addedLeaf);
          int j = 0;
          for (int i = 0; i < 32; i++) {
            if (((bitmap >>> i) & 1) != 0) {
              if (array[j] == null) {
                nodes[i] = (INode) array[j + 1];
              } else {
                nodes[i] = empty(equator).assoc(edit, shift + 5, equator.hash(k(array, j)), k(array, j), array[j + 1], addedLeaf);
              }
              j += 2;
            }
          }
          return new ArrayNode(equator, edit, n + 1, nodes);
        } else {
          Object[] newArray = new Object[2 * (n + 4)];
          System.arraycopy(array, 0, newArray, 0, 2 * idx);
          newArray[2 * idx] = key;
          addedLeaf.val = addedLeaf;
          newArray[2 * idx + 1] = val;
          System.arraycopy(array, 2 * idx, newArray, 2 * (idx + 1), 2 * (n - idx));
          BitmapIndexedNode<K, V> editable = ensureEditable(edit);
          editable.array = newArray;
          editable.bitmap |= bit;
          return editable;
        }
      }
    }

    @Override public INode<K, V> without(AtomicReference<Thread> edit, int shift, int hash, K key, Box<Box> removedLeaf) {
      int bit = bitpos(hash, shift);
      if ((bitmap & bit) == 0) {
        return this;
      }
      int idx = index(bit);
      K keyOrNull = k(array, 2 * idx);
      Object valOrNode = array[2 * idx + 1];
      if (keyOrNull == null) {
        INode<K, V> n = ((INode) valOrNode).without(edit, shift + 5, hash, key, removedLeaf);
        if (n == valOrNode) {
          return this;
        }
        if (n != null) {
          return editAndSet(edit, 2 * idx + 1, n);
        }
        if (bitmap == bit) {
          return null;
        }
        return editAndRemovePair(edit, bit, idx);
      }
      if (equator.eq(key, keyOrNull)) {
        removedLeaf.val = removedLeaf;
        return editAndRemovePair(edit, bit, idx);
      }
      return this;
    }
  }

  private final static class HashCollisionNode<K extends java.lang.Object, V extends java.lang.Object> implements INode<K, V> {
    private final Equator<K> equator;

    final int hash;

    int count;

    Object[] array;

    final AtomicReference<Thread> edit;

    HashCollisionNode(Equator<K> eq, AtomicReference<Thread> edit, int hash, int count, Object... array) {
      this.equator = eq;
      this.edit = edit;
      this.hash = hash;
      this.count = count;
      this.array = array;
    }

    @Override public INode<K, V> assoc(int shift, int hash, K key, V val, Box<Box> addedLeaf) {
      if (hash == this.hash) {
        int idx = findIndex(key);
        if (idx != -1) {
          if (array[idx + 1] == val) {
            return this;
          }
          return new HashCollisionNode<>(equator, null, hash, count, cloneAndSet(array, idx + 1, val));
        }
        Object[] newArray = new Object[2 * (count + 1)];
        System.arraycopy(array, 0, newArray, 0, 2 * count);
        newArray[2 * count] = key;
        newArray[2 * count + 1] = val;
        addedLeaf.val = addedLeaf;
        return new HashCollisionNode<>(equator, edit, hash, count + 1, newArray);
      }
      return new BitmapIndexedNode<K, V>(equator, null, bitpos(this.hash, shift), new Object[] { null, this }).assoc(shift, hash, key, val, addedLeaf);
    }

    @Override public INode<K, V> without(int shift, int hash, K key) {
      int idx = findIndex(key);
      if (idx == -1) {
        return this;
      }
      if (count == 1) {
        return null;
      }
      return new HashCollisionNode<>(equator, null, hash, count - 1, removePair(array, idx / 2));
    }

    @Override public UnmodMap.UnEntry<K, V> find(int shift, int hash, K key) {
      int idx = findIndex(key);
      if (idx < 0) {
        return null;
      }
      if (equator.eq(key, k(array, idx))) {
        return Tuple2.of(k(array, idx), v(array, idx + 1));
      }
      return null;
    }

    @Override public <R extends java.lang.Object> UnmodIterator<R> iterator(Fn2<K, V, R> aFn) {
      return new NodeIter<>(array, aFn);
    }

    private int findIndex(K key) {
      for (int i = 0; i < 2 * count; i += 2) {
        if (equator.eq(key, k(array, i))) {
          return i;
        }
      }
      return -1;
    }

    private HashCollisionNode<K, V> ensureEditable(AtomicReference<Thread> edit) {
      if (this.edit == edit) {
        return this;
      }
      Object[] newArray = new Object[2 * (count + 1)];
      System.arraycopy(array, 0, newArray, 0, 2 * count);
      return new HashCollisionNode<>(equator, edit, hash, count, newArray);
    }

    private HashCollisionNode<K, V> ensureEditable(AtomicReference<Thread> edit, int count, Object[] array) {
      if (this.edit == edit) {
        this.array = array;
        this.count = count;
        return this;
      }
      return new HashCollisionNode<>(equator, edit, hash, count, array);
    }

    private HashCollisionNode<K, V> editAndSet(AtomicReference<Thread> edit, int i, Object a) {
      HashCollisionNode<K, V> editable = ensureEditable(edit);
      editable.array[i] = a;
      return editable;
    }

    private HashCollisionNode<K, V> editAndSet(AtomicReference<Thread> edit, int i, Object a, int j, Object b) {
      HashCollisionNode<K, V> editable = ensureEditable(edit);
      editable.array[i] = a;
      editable.array[j] = b;
      return editable;
    }

    @Override public INode<K, V> assoc(AtomicReference<Thread> edit, int shift, int hash, K key, V val, Box<Box> addedLeaf) {
      if (hash == this.hash) {
        int idx = findIndex(key);
        if (idx != -1) {
          if (array[idx + 1] == val) {
            return this;
          }
          return editAndSet(edit, idx + 1, val);
        }
        if (array.length > 2 * count) {
          addedLeaf.val = addedLeaf;
          HashCollisionNode<K, V> editable = editAndSet(edit, 2 * count, key, 2 * count + 1, val);
          editable.count++;
          return editable;
        }
        Object[] newArray = new Object[array.length + 2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = key;
        newArray[array.length + 1] = val;
        addedLeaf.val = addedLeaf;
        return ensureEditable(edit, count + 1, newArray);
      }
      return new BitmapIndexedNode<K, V>(equator, edit, bitpos(this.hash, shift), new Object[] { null, this, null, null }).assoc(edit, shift, hash, key, val, addedLeaf);
    }

    @Override public INode<K, V> without(AtomicReference<Thread> edit, int shift, int hash, K key, Box<Box> removedLeaf) {
      int idx = findIndex(key);
      if (idx == -1) {
        return this;
      }
      removedLeaf.val = removedLeaf;
      if (count == 1) {
        return null;
      }
      HashCollisionNode<K, V> editable = ensureEditable(edit);
      editable.array[idx] = editable.array[2 * count - 2];
      editable.array[idx + 1] = editable.array[2 * count - 1];
      editable.array[2 * count - 2] = editable.array[2 * count - 1] = null;
      editable.count--;
      return editable;
    }
  }

  private static <K extends java.lang.Object, V extends java.lang.Object> INode<K, V>[] cloneAndSet(INode<K, V>[] array, int i, INode<K, V> a) {
    INode<K, V>[] clone = array.clone();
    clone[i] = a;
    return clone;
  }

  private static Object[] cloneAndSet(Object[] array, int i, Object a) {
    Object[] clone = array.clone();
    clone[i] = a;
    return clone;
  }

  private static Object[] cloneAndSet(Object[] array, int i, int j, Object b) {
    Object[] clone = array.clone();
    clone[i] = null;
    clone[j] = b;
    return clone;
  }

  private static Object[] removePair(Object[] array, int i) {
    Object[] newArray = new Object[array.length - 2];
    System.arraycopy(array, 0, newArray, 0, 2 * i);
    System.arraycopy(array, 2 * (i + 1), newArray, 2 * i, newArray.length - 2 * i);
    return newArray;
  }

  private static <K extends java.lang.Object, V extends java.lang.Object> INode<K, V> createNode(Equator<K> equator, int shift, K key1, V val1, int key2hash, K key2, V val2) {
    int key1hash = equator.hash(key1);
    if (key1hash == key2hash) {
      return new HashCollisionNode<>(equator, null, key1hash, 2, new Object[] { key1, val1, key2, val2 });
    }
    Box<Box> addedLeaf = new Box<>(null);
    AtomicReference<Thread> edit = new AtomicReference<>();
    return BitmapIndexedNode.<K, V>empty(equator).assoc(edit, shift, key1hash, key1, val1, addedLeaf).assoc(edit, shift, key2hash, key2, val2, addedLeaf);
  }

  private static <K extends java.lang.Object, V extends java.lang.Object> INode<K, V> createNode(Equator<K> equator, AtomicReference<Thread> edit, int shift, K key1, V val1, int key2hash, K key2, V val2) {
    int key1hash = equator.hash(key1);
    if (key1hash == key2hash) {
      return new HashCollisionNode<>(equator, null, key1hash, 2, new Object[] { key1, val1, key2, val2 });
    }
    Box<Box> addedLeaf = new Box<>(null);
    return BitmapIndexedNode.<K, V>empty(equator).assoc(edit, shift, key1hash, key1, val1, addedLeaf).assoc(edit, shift, key2hash, key2, val2, addedLeaf);
  }

  private static int bitpos(int hash, int shift) {
    return 1 << mask(hash, shift);
  }

  private static final class NodeIter<K extends java.lang.Object, V extends java.lang.Object, R extends java.lang.Object> implements UnmodIterator<R> {
    final Object[] array;

    private int mutableIndex = 0;

    private Fn2<K, V, R> aFn;

    private R nextEntry = null;

    private boolean absent = true;

    private UnmodIterator<R> nextIter;

    NodeIter(Object[] array, Fn2<K, V, R> aFn) {
      this.array = array;
      this.aFn = aFn;
    }

    private boolean advance() {
      while (mutableIndex < array.length) {
        int i = mutableIndex;
        mutableIndex = i + 2;
        if (array[i] != null) {
          nextEntry = aFn.apply(k(array, i), v(array, i + 1));
          absent = false;
          return true;
        } else {
          INode<K, V> node = iNode(array, i + 1);
          if (node != null) {
            UnmodIterator<R> iter = node.iterator(aFn);
            if (iter != null && iter.hasNext()) {
              nextIter = iter;
              return true;
            }
          }
        }
      }
      return false;
    }

    @Override public boolean hasNext() {
      if (!absent || nextIter != null) {
        return true;
      }
      return advance();
    }

    @Override public R next() {
      R ret = nextEntry;
      if (!absent) {
        nextEntry = null;
        absent = true;
        return ret;
      } else {
        if (nextIter != null) {
          ret = nextIter.next();
          if (!nextIter.hasNext()) {
            nextIter = null;
          }
          return ret;
        } else {
          if (advance()) {
            return next();
          }
        }
      }
      throw new NoSuchElementException();
    }
  }
}