package org.assertj.core.internal;
import static org.assertj.core.data.MapEntry.entry;
import static org.assertj.core.error.ShouldBeEmpty.shouldBeEmpty;
import static org.assertj.core.error.ShouldBeNullOrEmpty.shouldBeNullOrEmpty;
import static org.assertj.core.error.ShouldContain.shouldContain;
import static org.assertj.core.error.ShouldContainExactly.shouldContainExactly;
import static org.assertj.core.error.ShouldContainKeys.shouldContainKeys;
import static org.assertj.core.error.ShouldContainOnly.shouldContainOnly;
import static org.assertj.core.error.ShouldContainOnlyKeys.shouldContainOnlyKeys;
import static org.assertj.core.error.ShouldContainValue.shouldContainValue;
import static org.assertj.core.error.ShouldNotBeEmpty.shouldNotBeEmpty;
import static org.assertj.core.error.ShouldNotContain.shouldNotContain;
import static org.assertj.core.error.ShouldNotContainKey.shouldNotContainKey;
import static org.assertj.core.error.ShouldNotContainValue.shouldNotContainValue;
import static org.assertj.core.internal.Arrays.assertIsArray;
import static org.assertj.core.internal.CommonValidations.checkSizes;
import static org.assertj.core.internal.CommonValidations.hasSameSizeAsCheck;
import static org.assertj.core.util.Objects.areEqual;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.data.MapEntry;
import org.assertj.core.util.VisibleForTesting;

/**
 * Reusable assertions for <code>{@link Map}</code>s.
 * 
 * @author Alex Ruiz
 * @author Nicolas François
 */
public class Maps {
  private static final Maps INSTANCE = new Maps();

  /**
   * Returns the singleton instance of this class.
   * 
   * @return the singleton instance of this class.
   */
  public static Maps instance() {
    return INSTANCE;
  }

  @VisibleForTesting Failures failures = Failures.instance();

  @VisibleForTesting Maps() {
  }

  /**
   * Asserts that the given {@code Map} is {@code null} or empty.
   * 
   * @param info contains information about the assertion.
   * @param actual the given map.
   * @throws AssertionError if the given {@code Map} is not {@code null} *and* contains one or more entries.
   */
  public void assertNullOrEmpty(AssertionInfo info, Map<?, ?> actual) {
    if (actual == null || actual.isEmpty()) {
      return;
    }
    throw failures.failure(info, shouldBeNullOrEmpty(actual));
  }

  /**
   * Asserts that the given {@code Map} is empty.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @throws AssertionError if the given {@code Map} is {@code null}.
   * @throws AssertionError if the given {@code Map} is not empty.
   */
  public void assertEmpty(AssertionInfo info, Map<?, ?> actual) {
    assertNotNull(info, actual);
    if (actual.isEmpty()) {
      return;
    }
    throw failures.failure(info, shouldBeEmpty(actual));
  }

  /**
   * Asserts that the given {@code Map} is not empty.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @throws AssertionError if the given {@code Map} is {@code null}.
   * @throws AssertionError if the given {@code Map} is empty.
   */
  public void assertNotEmpty(AssertionInfo info, Map<?, ?> actual) {
    assertNotNull(info, actual);
    if (!actual.isEmpty()) {
      return;
    }
    throw failures.failure(info, shouldNotBeEmpty());
  }

  /**
   * Asserts that the number of entries in the given {@code Map} is equal to the expected one.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @param expectedSize the expected size of {@code actual}.
   * @throws AssertionError if the given {@code Map} is {@code null}.
   * @throws AssertionError if the number of entries in the given {@code Map} is different than the expected one.
   */
  public void assertHasSize(AssertionInfo info, Map<?, ?> actual, int expectedSize) {
    assertNotNull(info, actual);
    checkSizes(actual, actual.size(), expectedSize, info);
  }

  /**
   * Asserts that the number of entries in the given {@code Map} has the same size as the other {@code Iterable}.
   * 
   * @param info contains information about the assertion.
   * @param map the given {@code Map}.
   * @param other the group to compare
   * @throws AssertionError if the given {@code Map} is {@code null}.
   * @throws AssertionError if the given {@code Iterable} is {@code null}.
   * @throws AssertionError if the number of entries in the given {@code Map} does not have the same size.
   */
  public void assertHasSameSizeAs(AssertionInfo info, Map<?, ?> map, Iterable<?> other) {
    assertNotNull(info, map);
    hasSameSizeAsCheck(info, map, other, map.size());
  }

  /**
   * Asserts that the number of entries in the given {@code Map} has the same size as the other array.
   * 
   * @param info contains information about the assertion.
   * @param map the given {@code Map}.
   * @param other the group to compare
   * @throws AssertionError if the given {@code Map} is {@code null}.
   * @throws AssertionError if the given array is {@code null}.
   * @throws AssertionError if the number of entries in the given {@code Map} does not have the same size.
   */
  public void assertHasSameSizeAs(AssertionInfo info, Map<?, ?> map, Object other) {
    assertNotNull(info, map);
    assertIsArray(info, other);
    hasSameSizeAsCheck(info, map, other, map.size());
  }

  /**
   * Asserts that the size of the given {@code Map} is equal to the size of the other {@code Map}.
   *
   * @param info contains information about the assertion.
   * @param map the given {@code Map}.
   * @param other the other {@code Map} to compare
   * @throws NullPointerException if the other {@code Map} is {@code null}.
   * @throws AssertionError if the given {@code Map} is {@code null}.
   * @throws AssertionError if the size of the given {@code Map} is not equal to the other {@code Map} size
   */
  public void assertHasSameSizeAs(AssertionInfo info, Map<?, ?> map, Map<?, ?> other) {
    assertNotNull(info, map);
    hasSameSizeAsCheck(info, map, other, map.size());
  }

  /**
   * Asserts that the given {@code Map} contains the given entries, in any order.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @param entries the entries that are expected to be in the given {@code Map}.
   * @throws NullPointerException if the array of entries is {@code null}.
   * @throws IllegalArgumentException if the array of entries is empty.
   * @throws NullPointerException if any of the entries in the given array is {@code null}.
   * @throws AssertionError if the given {@code Map} is {@code null}.
   * @throws AssertionError if the given {@code Map} does not contain the given entries.
   */
  public void assertContains(AssertionInfo info, Map<?, ?> actual, MapEntry[] entries) {
    failIfNull(entries);
    assertNotNull(info, actual);
    if (actual.isEmpty() && entries.length == 0) {
      return;
    }
    failIfEmptySinceActualIsNotEmpty(entries);
    Set<MapEntry> notFound = new LinkedHashSet<MapEntry>();
    for (MapEntry entry : entries) {
      if (!containsEntry(actual, entry)) {
        notFound.add(entry);
      }
    }
    if (notFound.isEmpty()) {
      return;
    }
    throw failures.failure(info, shouldContain(actual, entries, notFound));
  }

  /**
   * Asserts that the given {@code Map} does not contain the given entries.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @param entries the entries that are expected to be in the given {@code Map}.
   * @throws NullPointerException if the array of entries is {@code null}.
   * @throws IllegalArgumentException if the array of entries is empty.
   * @throws NullPointerException if any of the entries in the given array is {@code null}.
   * @throws AssertionError if the given {@code Map} is {@code null}.
   * @throws AssertionError if the given {@code Map} contains any of the given entries.
   */
  public void assertDoesNotContain(AssertionInfo info, Map<?, ?> actual, MapEntry[] entries) {
    failIfNullOrEmpty(entries);
    assertNotNull(info, actual);
    Set<MapEntry> found = new LinkedHashSet<MapEntry>();
    for (MapEntry entry : entries) {
      if (containsEntry(actual, entry)) {
        found.add(entry);
      }
    }
    if (found.isEmpty()) {
      return;
    }
    throw failures.failure(info, shouldNotContain(actual, entries, found));
  }

  /**
   * Verifies that the actual map contain the given key.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @param keys the given keys
   * @throws AssertionError if the actual map is {@code null}.
   * @throws AssertionError if the actual map not contains the given key.
   */
  public <K extends java.lang.Object, V extends java.lang.Object> void assertContainsKeys(AssertionInfo info, Map<K, V> actual, @SuppressWarnings(value = { "unchecked" }) K... keys) {
    assertNotNull(info, actual);
    Set<K> notFound = new LinkedHashSet<K>();
    for (K key : keys) {
      if (!actual.containsKey(key)) {
        notFound.add(key);
      }
    }
    if (notFound.isEmpty()) {
      return;
    }
    throw failures.failure(info, shouldContainKeys(actual, notFound));
  }

  /**
   * Verifies that the actual map not contains the given key.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @param key the given key
   * @throws AssertionError if the actual map is {@code null}.
   * @throws AssertionError if the actual map contains the given key.
   */
  public <K extends java.lang.Object, V extends java.lang.Object> void assertDoesNotContainKey(AssertionInfo info, Map<K, V> actual, K key) {
    assertNotNull(info, actual);
    if (!actual.containsKey(key)) {
      return;
    }
    throw failures.failure(info, shouldNotContainKey(actual, key));
  }

  /**
   * Verifies that the actual map contains only the given keys and nothing else, in any order.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @param keys the keys that are expected to be in the given {@code Map}.
   * @throws NullPointerException if the array of keys is {@code null}.
   * @throws IllegalArgumentException if the array of keys is empty.
   * @throws AssertionError if the given {@code Map} is {@code null}.
   * @throws AssertionError if the given {@code Map} does not contain the given keys or if the given {@code Map}
   *           contains keys that are not in the given array.
   */
  public <K extends java.lang.Object, V extends java.lang.Object> void assertContainsOnlyKeys(AssertionInfo info, Map<K, V> actual, @SuppressWarnings(value = { "unchecked" }) K... keys) {
    assertNotNull(info, actual);
    failIfNull(keys);
    if (actual.isEmpty() && keys.length == 0) {
      return;
    }
    failIfEmpty(keys);
    Set<K> notFound = new LinkedHashSet<K>();
    Set<K> notExpected = new LinkedHashSet<K>();
    compareActualMapAndExpectedKeys(actual, keys, notExpected, notFound);
    if (notFound.isEmpty() && notExpected.isEmpty()) {
      return;
    }
    throw failures.failure(info, shouldContainOnlyKeys(actual, keys, notFound, notExpected));
  }

  /**
   * Verifies that the actual map contain the given value.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @param value the given value
   * @throws AssertionError if the actual map is {@code null}.
   * @throws AssertionError if the actual map not contains the given value.
   */
  public <K extends java.lang.Object, V extends java.lang.Object> void assertContainsValue(AssertionInfo info, Map<K, V> actual, V value) {
    assertNotNull(info, actual);
    if (actual.containsValue(value)) {
      return;
    }
    throw failures.failure(info, shouldContainValue(actual, value));
  }

  /**
   * Verifies that the actual map not contains the given value.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @param value the given value
   * @throws AssertionError if the actual map is {@code null}.
   * @throws AssertionError if the actual map contains the given value.
   */
  public <K extends java.lang.Object, V extends java.lang.Object> void assertDoesNotContainValue(AssertionInfo info, Map<K, V> actual, V value) {
    assertNotNull(info, actual);
    if (!actual.containsValue(value)) {
      return;
    }
    throw failures.failure(info, shouldNotContainValue(actual, value));
  }

  /**
   * Verifies that the actual map contains only the given entries and nothing else, in any order.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @param entries the entries that should be in the actual map.
   * @throws AssertionError if the actual map is {@code null}.
   * @throws NullPointerException if the given entries array is {@code null}.
   * @throws IllegalArgumentException if the given entries array is empty.
   * @throws AssertionError if the actual map does not contain the given entries, i.e. the actual map contains some or
   *           none of the given entries, or the actual map contains more entries than the given ones.
   */
  public <K extends java.lang.Object, V extends java.lang.Object> void assertContainsOnly(AssertionInfo info, Map<K, V> actual, MapEntry... entries) {
    doCommonContainsCheck(info, actual, entries);
    if (actual.isEmpty() && entries.length == 0) {
      return;
    }
    failIfEmpty(entries);
    Set<MapEntry> notFound = new LinkedHashSet<MapEntry>();
    Set<MapEntry> notExpected = new LinkedHashSet<MapEntry>();
    compareActualMapAndExpectedEntries(actual, entries, notExpected, notFound);
    if (notFound.isEmpty() && notExpected.isEmpty()) {
      return;
    }
    throw failures.failure(info, shouldContainOnly(actual, entries, notFound, notExpected));
  }

  /**
   * Verifies that the actual map contains only the given entries and nothing else, <b>in order</b>.<br>
   * This assertion should only be used with map that have a consistent iteration order (i.e. don't use it with
   * {@link java.util.HashMap}).
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code Map}.
   * @param entries the given entries.
   * @throws NullPointerException if the given entries array is {@code null}.
   * @throws AssertionError if the actual map is {@code null}.
   * @throws IllegalArgumentException if the given entries array is empty.
   * @throws AssertionError if the actual map does not contain the given entries with same order, i.e. the actual map
   *           contains some or none of the given entries, or the actual map contains more entries than the given ones
   *           or entries are the same but the order is not.
   */
  public <K extends java.lang.Object, V extends java.lang.Object> void assertContainsExactly(AssertionInfo info, Map<K, V> actual, MapEntry... entries) {
    doCommonContainsCheck(info, actual, entries);
    if (actual.isEmpty() && entries.length == 0) {
      return;
    }
    failIfEmpty(entries);
    assertHasSameSizeAs(info, actual, entries);
    Set<MapEntry> notFound = new LinkedHashSet<MapEntry>();
    Set<MapEntry> notExpected = new LinkedHashSet<MapEntry>();
    compareActualMapAndExpectedEntries(actual, entries, notExpected, notFound);
    if (notExpected.isEmpty() && notFound.isEmpty()) {
      int index = 0;
      for (K keyFromActual : actual.keySet()) {
        if (!areEqual(keyFromActual, entries[index].key)) {
          MapEntry actualEntry = entry(keyFromActual, actual.get(keyFromActual));
          throw failures.failure(info, shouldContainExactly(actualEntry, entries[index], index));
        }
        index++;
      }
      return;
    }
    throw failures.failure(info, shouldContainExactly(actual, entries, notFound, notExpected));
  }

  private <K extends java.lang.Object, V extends java.lang.Object> void compareActualMapAndExpectedKeys(Map<K, V> actual, K[] keys, Set<K> notExpected, Set<K> notFound) {
    Map<K, V> actualEntries = new LinkedHashMap<K, V>(actual);
    for (K key : keys) {
      if (actualEntries.containsKey(key)) {
        actualEntries.remove(key);
      } else {
        notFound.add(key);
      }
    }
    for (K key : actualEntries.keySet()) {
      notExpected.add(key);
    }
  }

  private <K extends java.lang.Object, V extends java.lang.Object> void compareActualMapAndExpectedEntries(Map<K, V> actual, MapEntry[] entries, Set<MapEntry> notExpected, Set<MapEntry> notFound) {
    Map<K, V> expectedEntries = entriesToMap(entries);
    Map<K, V> actualEntries = new LinkedHashMap<K, V>(actual);
    for (Map.Entry<K, V> entry : expectedEntries.entrySet()) {
      if (containsEntry(actualEntries, entry(entry.getKey(), entry.getValue()))) {
        actualEntries.remove(entry.getKey());
      } else {
        notFound.add(entry(entry.getKey(), entry.getValue()));
      }
    }
    for (Map.Entry<K, V> entry : actualEntries.entrySet()) {
      notExpected.add(entry(entry.getKey(), entry.getValue()));
    }
  }

  private <K extends java.lang.Object, V extends java.lang.Object> void doCommonContainsCheck(AssertionInfo info, Map<K, V> actual, MapEntry[] entries) {
    assertNotNull(info, actual);
    failIfNull(entries);
  }

  @SuppressWarnings(value = { "unchecked" }) private static <K extends java.lang.Object, V extends java.lang.Object> Map<K, V> entriesToMap(MapEntry[] entries) {
    Map<K, V> expectedEntries = new LinkedHashMap<K, V>();
    for (MapEntry entry : entries) {
      expectedEntries.put((K) entry.key, (V) entry.value);
    }
    return expectedEntries;
  }

  private static <K extends java.lang.Object> void failIfEmpty(K[] keys) {
    if (keys.length == 0) {
      throw new IllegalArgumentException("The array of keys to look for should not be empty");
    }
  }

  private static void failIfEmpty(MapEntry[] entries) {
    if (entries.length == 0) {
      throw new IllegalArgumentException("The array of entries to look for should not be empty");
    }
  }

  private static void failIfNullOrEmpty(MapEntry[] entries) {
    failIfNull(entries);
    failIfEmpty(entries);
  }

  private static <K extends java.lang.Object> void failIfNull(K[] keys) {
    if (keys == null) {
      throw new NullPointerException("The array of keys to look for should not be null");
    }
  }

  private static void failIfNull(MapEntry[] entries) {
    if (entries == null) {
      throw new NullPointerException("The array of entries to look for should not be null");
    }
  }

  private boolean containsEntry(Map<?, ?> actual, MapEntry entry) {
    if (entry == null) {
      throw new NullPointerException("Entries to look for should not be null");
    }
    if (!actual.containsKey(entry.key)) {
      return false;
    }
    return areEqual(actual.get(entry.key), entry.value);
  }

  private void assertNotNull(AssertionInfo info, Map<?, ?> actual) {
    Objects.instance().assertNotNull(info, actual);
  }

  private static void failIfEmptySinceActualIsNotEmpty(MapEntry[] values) {
    if (values.length == 0) {
      throw new AssertionError("actual is not empty");
    }
  }
}