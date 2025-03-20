package com.seanchenxi.gwt.storage.client.cache;
import com.seanchenxi.gwt.storage.client.StorageKey;
import java.util.HashMap;

/**
 * Default implementation of {@link StorageCache}
 */
class StorageMemoryCache implements StorageCache {
  private final HashMap<StorageKey<?>, Object> map;

  public StorageMemoryCache() {
    map = new HashMap<StorageKey<?>, Object>();
  }

  @Override public void clear() {
    map.clear();
  }

  @Override public <T extends java.lang.Object> boolean containsValue(T value) {
    return map.containsValue(value);
  }

  @Override @SuppressWarnings(value = { "unchecked" }) public <T extends java.lang.Object> T get(StorageKey<T> key) {
    Object val = map.get(key);
    return val != null ? (T) val : null;
  }

  @Override @SuppressWarnings(value = { "unchecked" }) public <T extends java.lang.Object> T put(StorageKey<T> key, T value) {
    Object old = map.put(key, value);
    return old != null ? (T) old : null;
  }

  @Override @SuppressWarnings(value = { "unchecked" }) public <T extends java.lang.Object> T remove(StorageKey<T> key) {
    Object val = map.remove(key);
    return val != null ? (T) val : null;
  }
}