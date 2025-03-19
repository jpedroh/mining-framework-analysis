package io.jsonwebtoken.lang;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to help with the manipulation of working with Maps.
 *
 * @since 0.11.0
 */
public final class Maps {
  private Maps() {
  }

  /**
     * Creates a new map builder with a single entry.
     * <p> Typical usage: <pre>{@code
     * Map<K,V> result = Maps.of("key1", value1)
     *     .and("key2", value2)
     *     // ...
     *     .build();
     * }</pre>
     *
     * @param key   the key of an map entry to be added
     * @param value the value of map entry to be added
     * @param <K>   the maps key type
     * @param <V>   the maps value type
     *              Creates a new map builder with a single entry.
     */
  public static <K extends java.lang.Object, V extends java.lang.Object> MapBuilder<K, V> of(K key, V value) {
    return new HashMapBuilder<K, V>().and(key, value);
  }

  public interface MapBuilder<K extends java.lang.Object, V extends java.lang.Object> extends Builder<Map<K, V>> {
    /**
         * Add a new entry to this map builder
         *
         * @param key   the key of an map entry to be added
         * @param value the value of map entry to be added
         * @return the current MapBuilder to allow for method chaining.
         */
    MapBuilder<K, V> and(K key, V value);

    /**
         * Returns the resulting Map object from this MapBuilder.
         *
         * @return the resulting Map object from this MapBuilder.
         */
    Map<K, V> build();
  }

  private static class HashMapBuilder<K extends java.lang.Object, V extends java.lang.Object> implements MapBuilder<K, V> {
    private final Map<K, V> data = new HashMap<>();

    public MapBuilder<K, V> and(K key, V value) {
      data.put(key, value);
      return this;
    }

    public Map<K, V> build() {
      return Collections.unmodifiableMap(data);
    }
  }
}