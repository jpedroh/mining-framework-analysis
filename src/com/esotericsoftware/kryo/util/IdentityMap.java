package com.esotericsoftware.kryo.util;

/** An unordered map that uses identity comparison for the object keys. Null keys are not allowed. No allocation is done except
 * when growing the table size.
 * <p>
 * This class performs fast contains and remove (typically O(1), worst case O(n) but that is rare in practice). Add may be
 * slightly slower, depending on hash collisions. Load factors greater than 0.91 greatly increase the chances to resize to the
 * next higher POT size.
 * <p>
 * Unordered sets and maps are not designed to provide especially fast iteration.
 * <p>
 * This implementation uses linear probing with the backward shift algorithm for removal. Linear probing continues to work even
 * when all hashCodes collide, just more slowly.
 * @author Tommy Ettinger
 * @author Nathan Sweet */
public class IdentityMap<K extends java.lang.Object, V extends java.lang.Object> extends ObjectMap<K, V> {
  /** Creates a new map with an initial capacity of 51 and a load factor of 0.75 */
  public IdentityMap() {
    super();
  }

  /** Creates a new map with a load factor of 0.75
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two. */
  public IdentityMap(int initialCapacity) {
    super(initialCapacity);
  }

  /** Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two. */
  public IdentityMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  /** Creates a new map identical to the specified map. */
  public IdentityMap(IdentityMap<K, V> map) {
    super(map);
  }

  protected int place(K item) {
    return System.identityHashCode(item) & mask;
  }

  public <T extends K> V get(T key) {
    for (int i = place(key); ; i = i + 1 & mask) {
      K other = keyTable[i];
      if (other == null) {
        return null;
      }
      if (other == key) {
        return valueTable[i];
      }
    }
  }

  public V get(K key, V defaultValue) {
    for (int i = place(key); ; i = i + 1 & mask) {
      K other = keyTable[i];
      if (other == null) {
        return defaultValue;
      }
      if (other == key) {
        return valueTable[i];
      }
    }
  }

  int locateKey(K key) {
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null.");
    }
    K[] keyTable = this.keyTable;
    for (int i = place(key); ; i = i + 1 & mask) {
      K other = keyTable[i];
      if (other == null) {
        return -(i + 1);
      }
      if (other == key) {
        return i;
      }
    }
  }

  public int hashCode() {
    int h = size;
    K[] keyTable = this.keyTable;
    V[] valueTable = this.valueTable;
    for (int i = 0, n = keyTable.length; i < n; i++) {
      K key = keyTable[i];
      if (key != null) {
        h += System.identityHashCode(key);
        V value = valueTable[i];
        if (value != null) {
          h += value.hashCode();
        }
      }
    }
    return h;
  }
}