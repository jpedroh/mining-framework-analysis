package org.simmetrics.metrics;
import static java.util.Collections.frequency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.simmetrics.ListMetric;
import static java.lang.Math.abs;

/**
 * Implements the Block distance algorithm whereby vector space block distance
 * between tokens is used to determine a similarity.
 * 
 * Also known as L1 Distance or City block distance.
 * 
 * 
 * @author Sam Chapman
 * @version 1.1
 * @param <T>
 *            type of token
 */
public class BlockDistance<T extends java.lang.Object> implements ListMetric<T> {
  @Override public float compare(List<T> a, List<T> b) {
    if (a.isEmpty() && b.isEmpty()) {
      return 0.0f;
    }
    final float totalPossible = a.size() + b.size();
    return (totalPossible - getInnerUnNormalizedSimilarity(a, b)) / totalPossible;
  }

  private static <T extends java.lang.Object> float getInnerUnNormalizedSimilarity(final List<T> a, final List<T> b) {
    final Set<T> all = new HashSet<>();
    all.addAll(a);
    all.addAll(b);
    int totalDistance = 0;
    for (T token : all) {
      int frequencyInA = frequency(a, token);
      int frequencyInB = frequency(b, token);
      totalDistance += abs(frequencyInA - frequencyInB);
    }
    return totalDistance;
  }

  @Override public String toString() {
    return "BlockDistance";
  }
}