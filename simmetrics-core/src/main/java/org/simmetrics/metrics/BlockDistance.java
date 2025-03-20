package org.simmetrics.metrics;
import static java.lang.Math.abs;
import static org.simmetrics.metrics.Math.union;
import org.simmetrics.MultisetDistance;
import org.simmetrics.MultisetMetric;
import com.google.common.collect.Multiset;

/**
 * Calculates the block distance and similarity over two multisets. Also known
 * as L1 Distance or City block distance.
 * <p>
 * <code>
 * similarity(a,b) = 1 - distance(a,b) / (∣a∣ + ∣b∣)
 * distance(a,b) = ∣∣a - b∣∣₁
 * </code>
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Taxicab_geometry">Wikipedia -
 *      Taxicab geometry</a>
 * @param <T>
 *            type of token
 */
public final class BlockDistance<T extends java.lang.Object> implements MultisetMetric<T>, MultisetDistance<T> {
  @Override public float compare(Multiset<T> a, Multiset<T> b) {
    if (a.isEmpty() && b.isEmpty()) {
      return 1.0f;
    }
    if (a.isEmpty() || b.isEmpty()) {
      return 0.0f;
    }
    return 1.0f - distance(a, b) / (a.size() + b.size());
  }

  @Override public float distance(Multiset<T> a, Multiset<T> b) {
    float distance = 0;
    for (T token : union(a, b).elementSet()) {
      float frequencyInA = a.count(token);
      float frequencyInB = b.count(token);
      distance += abs(frequencyInA - frequencyInB);
    }
    return distance;
  }

  @Override public String toString() {
    return "BlockDistance";
  }
}