package org.simmetrics.metrics;
import static org.simmetrics.metrics.Math.intersection;
import org.simmetrics.MultisetDistance;
import org.simmetrics.MultisetMetric;
import com.google.common.collect.Multiset;

/**
 * Calculates the generalized Jaccard distance and similarity coefficient over
 * two multisets. The similarity is defined as the size of the intersection
 * divided by the size of the union of the sample sets. The distance is obtained
 * by subtracting the Jaccard coefficient from 1.
 * <p>
 * <code>
 * similarity(a,b) = ∣a ∩ b∣ / ∣a ∪ b∣
 * <br>
 * distance(a,b) = 1 - similarity(a,b)
 * </code>
 * <p>
 * When <code>∣a ∪ b∣</code> is empty the multisets have no elements in common.
 * In this case the similarity is 0 by definition.
 * <p>
 * Unlike the Jaccard index the occurrence (cardinality) of an entry is taken
 * into account. E.g. {@code [hello, world]} and
 * {@code [hello, world, hello, world]} would be identical when compared with
 * the Jaccard index but are dissimilar when the generalized version is used.
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @see Jaccard
 * @see <a href="http://en.wikipedia.org/wiki/Jaccard_index">Wikipedia - Jaccard
 *      index</a>
 * 
 * @param <T>
 *            type of the token
 * 
 */
public final class GeneralizedJaccard<T extends java.lang.Object> implements MultisetMetric<T>, MultisetDistance<T> {
  @Override public float compare(Multiset<T> a, Multiset<T> b) {
    if (a.isEmpty() && b.isEmpty()) {
      return 1.0f;
    }
    if (a.isEmpty() || b.isEmpty()) {
      return 0.0f;
    }
    final int intersection = intersection(a, b).size();
    return intersection / (float) (a.size() + b.size() - intersection);
  }

  @Override public float distance(Multiset<T> a, Multiset<T> b) {
    return 1.0f - compare(a, b);
  }

  @Override public String toString() {
    return "GeneralizedJaccard";
  }
}