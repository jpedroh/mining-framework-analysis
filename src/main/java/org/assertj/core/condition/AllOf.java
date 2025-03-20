package org.assertj.core.condition;
import org.assertj.core.api.Condition;

/**
 * Returns {@code true} if all of the joined conditions is satisfied.
 * @param <T> the type of object this condition accepts.
 * 
 * @author Yvonne Wang
 * @author Mikhail Mazursky
 */
public class AllOf<T extends java.lang.Object> extends Join<T> {
  /**
   * Creates a new <code>{@link AllOf}</code>
   * @param <T> the type of object the given condition accept.
   * @param conditions the conditions to evaluate.
   * @return the created {@code AnyOf}.
   * @throws NullPointerException if the given array is {@code null}.
   * @throws NullPointerException if any of the elements in the given array is {@code null}.
   */
  @SafeVarargs public static <T extends java.lang.Object> Condition<T> allOf(Condition<? super T>... conditions) {
    return new AllOf<T>(conditions);
  }

  /**
   * Creates a new <code>{@link AllOf}</code>
   * @param <T> the type of object the given condition accept.
   * @param conditions the conditions to evaluate.
   * @return the created {@code AnyOf}.
   * @throws NullPointerException if the given iterable is {@code null}.
   * @throws NullPointerException if any of the elements in the given iterable is {@code null}.
   */
  public static <T extends java.lang.Object> Condition<T> allOf(Iterable<? extends Condition<? super T>> conditions) {
    return new AllOf<T>(conditions);
  }

  @SafeVarargs private AllOf(Condition<? super T>... conditions) {
    super(conditions);
  }

  private AllOf(Iterable<? extends Condition<? super T>> conditions) {
    super(conditions);
  }

  /** {@inheritDoc} */
  @Override public boolean matches(T value) {
    for (Condition<? super T> condition : conditions) {
      if (!condition.matches(value)) {
        return false;
      }
    }
    return true;
  }

  @Override public String toString() {
    return String.format("all of:<%s>", conditions);
  }
}