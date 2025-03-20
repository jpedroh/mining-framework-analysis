package org.simmetrics.metrics;
import java.util.List;
import org.simmetrics.ListMetric;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Implements the matching coefficient algorithm providing a similarity measure
 * between two lists.
 * <p>
 * The matching coefficient between two lists is defined as ratio of elements
 * that occur in both lists and elements that exclusively occur in either list.
 * This metric is identical to Jaccard similarity. However repeated elements are
 * considered as distinct occurrences.
 * 
 * <p>
 * <code>
 * similarity(a,b) = (a A b)|  / (|a or b|)
 * </code>
 * 
 * <p>
 * The A operation takes the list intersection of <code>a</code> and
 * <code>b</code>. This is a list <code>c</code> such that each element in has a
 * 1-to-1 relation to an element in both <code>a</code> and <code>b</code>. E.g.
 * the list intersection of <code>[ab,ab,ab,ac]</code> and
 * <code>[ab,ab,ad]</code> is <code>[ab,ab]</code>. *
 * <p>
 * This metric is identical to Jaccard but is insensitive to repeated tokens.
 * The list <code>["a","a","b"]</code> is identical to
 * <code>["a","b","b"]</code>.
 * 
 * 
 * @see JaccardSimilarity
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Simple_matching_coefficient">Wikipedia
 *      - Simple Matching Coefficient</a>
 * 
 * @author mpkorstanje
 * 
 * @param <T>
 *            type of the token
 * 
 */
public class MatchingCoefficient<T extends java.lang.Object> implements ListMetric<T> {
  @Override public float compare(List<T> a, List<T> b) {
    if (a.isEmpty() && b.isEmpty()) {
      return 1.0f;
    }
    if (a.isEmpty() || b.isEmpty()) {
      return 0.0f;
    }
    int intersection = 0;
    Multiset<T> bCopy = HashMultiset.create(b);
    for (T token : a) {
      if (bCopy.remove(token)) {
        intersection++;
      }
    }
    return intersection / (float) (a.size() + b.size() - intersection);
  }

  @Override public String toString() {
    return "MatchingCoefficient";
  }
}