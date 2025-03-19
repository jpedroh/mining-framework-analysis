package org.organicdesign.fp.collections;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 A wrapper that turns a PersistentTreeMap into a set.

 This file is a derivative work based on a Clojure collection licensed under the Eclipse Public
 License 1.0 Copyright Rich Hickey
*/
public class PersistentHashSet<E extends java.lang.Object> extends AbstractUnmodSet<E> implements ImSet<E>, Serializable {
  public static final PersistentHashSet<Object> EMPTY = new PersistentHashSet<>(PersistentHashMap.EMPTY);

  @SuppressWarnings(value = { "unchecked" }) public static <E extends java.lang.Object> PersistentHashSet<E> empty() {
    return (PersistentHashSet<E>) EMPTY;
  }

  /** Works around some type inference limitations of Java 8. */
  public static <E extends java.lang.Object> MutHashSet<E> emptyMutable() {
    return PersistentHashSet.<E>empty().mutable();
  }

  public static <E extends java.lang.Object> PersistentHashSet<E> empty(Equator<E> eq) {
    return new PersistentHashSet<>(PersistentHashMap.empty(eq));
  }

  /** Works around some type inference limitations of Java 8. */
  public static <E extends java.lang.Object> MutHashSet<E> emptyMutable(Equator<E> eq) {
    return empty(eq).mutable();
  }

  /**
     Returns a new PersistentHashSet of the values.  The vararg version of this method is
     {@link org.organicdesign.fp.StaticImports#set(Object...)}   If the input contains duplicate
     elements, later values overwrite earlier ones.

     @param elements The items to put into a vector.
     @return a new PersistentHashSet of the given elements.
     */
  public static <E extends java.lang.Object> PersistentHashSet<E> of(Iterable<E> elements) {
    PersistentHashSet<E> empty = empty();
    MutSet<E> ret = empty.mutable();
    for (E e : elements) {
      ret.put(e);
    }
    return (PersistentHashSet<E>) ret.immutable();
  }

  public static <E extends java.lang.Object> PersistentHashSet<E> ofEq(Equator<E> eq, Iterable<E> init) {
    MutSet<E> ret = emptyMutable(eq);
    for (E e : init) {
      ret.put(e);
    }
    return (PersistentHashSet<E>) ret.immutable();
  }

  @SuppressWarnings(value = { "unchecked" }) public static <E extends java.lang.Object> PersistentHashSet<E> ofMap(ImMap<E, ?> map) {
    return new PersistentHashSet<>((ImMap<E, E>) map);
  }

  private final ImMap<E, E> impl;

  private PersistentHashSet(ImMap<E, E> i) {
    impl = i;
  }

  private static final long serialVersionUID = 20160904155600L;

  private static class SerializationProxy<K extends java.lang.Object> implements Serializable {
    private static final long serialVersionUID = 20160904155600L;

    private final int size;

    private transient ImMap<K, K> theMap;

    SerializationProxy(ImMap<K, K> phm) {
      size = phm.size();
      theMap = phm;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      for (Map.Entry<K, ?> entry : theMap) {
        s.writeObject(entry.getKey());
      }
    }

    @SuppressWarnings(value = { "unchecked" }) private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      MutMap tempMap = PersistentHashMap.<K, K>empty().mutable();
      for (int i = 0; i < size; i++) {
        K k = (K) s.readObject();
        tempMap = tempMap.assoc(k, k);
      }
      theMap = tempMap.immutable();
    }

    private Object readResolve() {
      return PersistentHashSet.ofMap(theMap);
    }
  }

  private Object writeReplace() {
    return new SerializationProxy<>(impl);
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    throw new InvalidObjectException("Proxy required");
  }

  @Override public boolean contains(Object key) {
    return impl.containsKey(key);
  }

  /** Returns the Equator used by this set for equals comparisons and hashCodes */
  public Equator<E> equator() {
    return impl.equator();
  }

  @Override public PersistentHashSet<E> without(E key) {
    if (contains(key)) {
      return new PersistentHashSet<>(impl.without(key));
    }
    return this;
  }

  @Override public PersistentHashSet<E> put(E o) {
    if (contains(o)) {
      return this;
    }
    return new PersistentHashSet<>(impl.assoc(o, o));
  }

  @Override public UnmodIterator<E> iterator() {
    return impl.keyIterator();
  }

  @Override public int size() {
    return impl.size();
  }

  public MutHashSet<E> mutable() {
    return new MutHashSet<>(impl.mutable());
  }


<<<<<<< /usr/src/app/output/glenkpeterson/j-sicle/bf4de20006b5405bf4c84c50a037ef0b28a060ab/src/main/java/org/organicdesign/fp/collections/PersistentHashSet.java/left.java
  public static final class MutHashSet<E extends java.lang.Object> extends AbstractUnmodSet<E> implements MutSet<E> {
    MutMap<E, E> impl;

    MutHashSet(MutMap<E, E> impl) {
      this.impl = impl;
    }

    @Override public int size() {
      return impl.size();
    }

    @Override public MutHashSet<E> put(E val) {
      MutMap<E, E> m = impl.assoc(val, val);
      if (m != impl) {
        this.impl = m;
      }
      return this;
    }

    @Override public UnmodIterator<E> iterator() {
      return impl.map((e) -> e.getKey()).iterator();
    }

    @SuppressWarnings(value = { "unchecked" }) @Override public boolean contains(Object key) {
      return impl.entry((E) key).isSome();
    }

    @Override public MutHashSet<E> without(E key) {
      MutMap<E, E> m = impl.without(key);
      if (m != impl) {
        this.impl = m;
      }
      return this;
    }

    @Override public PersistentHashSet<E> immutable() {
      return new PersistentHashSet<>(impl.immutable());
    }
  }
=======
  public static final class MutableHashSet<E extends java.lang.Object> extends AbstractUnmodSet<E> implements MutableSet<E> {
    MutableMap<E, E> impl;

    MutableHashSet(MutableMap<E, E> impl) {
      this.impl = impl;
    }

    @Override public int size() {
      return impl.size();
    }

    @Override public MutableHashSet<E> put(E val) {
      MutableMap<E, E> m = impl.assoc(val, val);
      if (m != impl) {
        this.impl = m;
      }
      return this;
    }

    @Override public UnmodIterator<E> iterator() {
      return impl.keyIterator();
    }

    @SuppressWarnings(value = { "unchecked" }) @Override public boolean contains(Object key) {
      return impl.entry((E) key).isSome();
    }

    @Override public MutableHashSet<E> without(E key) {
      MutableMap<E, E> m = impl.without(key);
      if (m != impl) {
        this.impl = m;
      }
      return this;
    }

    @Override public PersistentHashSet<E> immutable() {
      return new PersistentHashSet<>(impl.immutable());
    }
  }
>>>>>>> /usr/src/app/output/glenkpeterson/j-sicle/bf4de20006b5405bf4c84c50a037ef0b28a060ab/src/main/java/org/organicdesign/fp/collections/PersistentHashSet.java/right.java
}