package org.simmetrics.metrics;
import static org.simmetrics.metrics.Math.intersection;
import static java.lang.Math.sqrt;
import java.util.Set;
import org.simmetrics.SetMetric;

/**
 * Calculates the Tanimoto similarity coefficient over two sets. The
 * similarity is defined as the cosine of the angle between the sets
 * expressed as sparse vectors.
 * <p>
 * <code>
 * similarity(a,b) = a·b / (||a|| * ||b||)
 * </code>
 * <p>
 * The cosine similarity is identical to the Tanimoto coefficient, but unlike
 * Tanimoto the occurrence (cardinality) of an entry is taken into account. E.g.
 * {@code [hello, world]} and {@code [hello, world, hello, world]} would be
 * identical when compared with Tanimoto but are dissimilar when the cosine
 * similarity is used.
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @see CosineSimilarity
 * @see <a href="http://en.wikipedia.org/wiki/Cosine_similarity">Wikipedia
 *      Cosine similarity</a>
 * 
 * @param <T>
 *            type of the token
 */
public final class TanimotoCoefficient<T extends java.lang.Object> implements SetMetric<T> {
  @Override public float compare(Set<T> a, Set<T> b) {
    if (a.isEmpty() && b.isEmpty()) {
      return 1.0f;
    }
    if (a.isEmpty() || b.isEmpty()) {
      return 0.0f;
    }
    return (float) (intersection(a, b).size() / (sqrt(a.size()) * sqrt(b.size())));
  }

  @Override public String toString() {
    return "TanimotoCoefficient";
  }
}