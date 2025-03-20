package org.assertj.core.api;
import org.assertj.core.groups.Tuple;
import org.assertj.core.util.CheckReturnValue;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Assertions for {@link Map}s.
 * <p>
 * To create a new instance of this class, invoke <code>{@link Assertions#assertThat(Map)}</code>.
 * </p>
 *
 * @author David DIDIER
 * @author Yvonne Wang
 * @author Alex Ruiz
 * @author Mikhail Mazursky
 * @author Nicolas François
 */
public class MapAssert<KEY extends java.lang.Object, VALUE extends java.lang.Object> extends AbstractMapAssert<MapAssert<KEY, VALUE>, Map<KEY, VALUE>, KEY, VALUE> {
  public MapAssert(Map<KEY, VALUE> actual) {
    super(actual, MapAssert.class);
  }

  @SafeVarargs @Override public final MapAssert<KEY, VALUE> contains(Map.Entry<? extends KEY, ? extends VALUE>... entries) {
    return super.contains(entries);
  }

  @SafeVarargs @Override public final MapAssert<KEY, VALUE> containsAnyOf(Map.Entry<? extends KEY, ? extends VALUE>... entries) {
    return super.containsAnyOf(entries);
  }

  @SafeVarargs @Override public final MapAssert<KEY, VALUE> containsOnly(Map.Entry<? extends KEY, ? extends VALUE>... entries) {
    return super.containsOnly(entries);
  }

  @SafeVarargs @Override public final MapAssert<KEY, VALUE> containsExactly(Map.Entry<? extends KEY, ? extends VALUE>... entries) {
    return super.containsExactly(entries);
  }

  @SafeVarargs @Override public final MapAssert<KEY, VALUE> containsKeys(KEY... keys) {
    return super.containsKeys(keys);
  }

  @SafeVarargs @Override public final MapAssert<KEY, VALUE> containsOnlyKeys(KEY... keys) {
    return super.containsOnlyKeys(keys);
  }

  @SafeVarargs @Override public final MapAssert<KEY, VALUE> containsValues(VALUE... values) {
    return super.containsValues(values);
  }

  @SafeVarargs @Override public final MapAssert<KEY, VALUE> doesNotContainKeys(KEY... keys) {
    return super.doesNotContainKeys(keys);
  }

  @SafeVarargs @Override public final MapAssert<KEY, VALUE> doesNotContain(Map.Entry<? extends KEY, ? extends VALUE>... entries) {
    return super.doesNotContain(entries);
  }

  @SafeVarargs @Override public final AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> extracting(Function<? super Map<KEY, VALUE>, Object>... extractors) {
    return super.extracting(extractors);
  }

  @SafeVarargs @Override public final AbstractListAssert<?, List<? extends Tuple>, Tuple, ObjectAssert<Tuple>> extractingFromEntries(Function<? super Map.Entry<KEY, VALUE>, Object>... extractors) {
    return super.extractingFromEntries(extractors);
  }
}