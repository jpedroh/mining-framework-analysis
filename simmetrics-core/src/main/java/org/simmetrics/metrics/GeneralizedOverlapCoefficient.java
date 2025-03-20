package org.simmetrics.metrics;
import static org.simmetrics.metrics.Math.intersection;
import static java.lang.Math.min;
import org.simmetrics.MultisetMetric;
import com.google.common.collect.Multiset;

/**
 * The generalized overlap coefficient measures the overlap between two
 * multisets. The similarity is defined as the size of the intersection divided
 * by the smaller of the size of the two sets
 * <p>
 * <code>
 * similarity(q,r) = ∣q ∩ r∣ / min{∣q∣, ∣r∣}
 * </code>
 * <p>
 * Unlike the overlap coefficient the occurrence (cardinality) of an entry is
 * taken into account. E.g. {@code [hello, world]} and
 * {@code [hello, world, hello, world]} would be identical when compared with
 * the overlap coefficient index but are dissimilar when the generalized version
 * is used.
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @param <T>
 *            type of the token
 * 
 * @see OverlapCoefficient
 * @see <a href="http://en.wikipedia.org/wiki/Overlap_coefficient">Wikipedia -
 *      Overlap Coefficient</a>
 */
public final class GeneralizedOverlapCoefficient<T extends java.lang.Object> implements MultisetMetric<T> {
  @Override public float compare(Multiset<T> a, Multiset<T> b) {
    if (a.isEmpty() && b.isEmpty()) {
      return 1.0f;
    }
    if (a.isEmpty() || b.isEmpty()) {
      return 0.0f;
    }
    return intersection(a, b).size() / (float) min(a.size(), b.size());
  }

  @Override public String toString() {
    return "GeneralizedOverlapCoefficient";
  }
}