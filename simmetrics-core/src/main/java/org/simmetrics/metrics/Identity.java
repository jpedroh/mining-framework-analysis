package org.simmetrics.metrics;
import static com.google.common.base.Preconditions.checkNotNull;
import org.simmetrics.Distance;
import org.simmetrics.Metric;

/**
 * Identity metric that returns 1.0 when the inputs are equals, and 0.0 when
 * they're not. 
 * <p>
 * <code>
 * similarity(a,b) = equals(a,b) ? 1.0f : 0.0f
 * </code>
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @param <T>
 *            type of token
 */
public final class Identity<T extends java.lang.Object> implements Metric<T>, Distance<T> {
  @Override public float compare(T a, T b) {
    checkNotNull(a);
    checkNotNull(b);
    return a.equals(b) ? 1.0f : 0.0f;
  }

  @Override public float distance(T a, T b) {
    checkNotNull(a);
    checkNotNull(b);
    return a.equals(b) ? 0.0f : 1.0f;
  }

  @Override public String toString() {
    return "Identity";
  }
}