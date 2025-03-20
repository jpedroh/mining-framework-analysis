package org.assertj.core.condition;
import static java.util.Collections.unmodifiableCollection;
import java.util.*;
import org.assertj.core.api.Condition;
import org.assertj.core.util.VisibleForTesting;

/**
 * Join of two or more <code>{@link Condition}</code>s.
 * @param <T> the type of object this condition accepts.
 * 
 * @author Yvonne Wang
 * @author Mikhail Mazursky
 */
public abstract class Join<T extends java.lang.Object> extends Condition<T> {
  @VisibleForTesting final Collection<Condition<? super T>> conditions;

  /**
   * Creates a new </code>{@link Join}</code>.
   * @param conditions the conditions to join.
   * @throws NullPointerException if the given array is {@code null}.
   * @throws NullPointerException if any of the elements in the given array is {@code null}.
   */
  @SafeVarargs protected Join(Condition<? super T>... conditions) {
    if (conditions == null) {
      throw conditionsIsNull();
    }
    this.conditions = new ArrayList<Condition<? super T>>();
    for (Condition<? super T> condition : conditions) {
      this.conditions.add(notNull(condition));
    }
  }

  /**
   * Creates a new </code>{@link Join}</code>.
   * @param conditions the conditions to join.
   * @throws NullPointerException if the given iterable is {@code null}.
   * @throws NullPointerException if any of the elements in the given iterable is {@code null}.
   */
  protected Join(Iterable<? extends Condition<? super T>> conditions) {
    if (conditions == null) {
      throw conditionsIsNull();
    }
    this.conditions = new ArrayList<Condition<? super T>>();
    for (Condition<? super T> condition : conditions) {
      this.conditions.add(notNull(condition));
    }
  }

  private static NullPointerException conditionsIsNull() {
    return new NullPointerException("The given conditions should not be null");
  }

  private static <T extends java.lang.Object> Condition<T> notNull(Condition<T> condition) {
    if (condition == null) {
      throw new NullPointerException("The given conditions should not have null entries");
    }
    return condition;
  }

  /**
   * Returns the conditions to join.
   * @return the conditions to join.
   */
  protected final Collection<Condition<? super T>> conditions() {
    return unmodifiableCollection(conditions);
  }
}