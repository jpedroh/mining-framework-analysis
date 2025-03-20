package org.simmetrics.metrics;
import java.util.ArrayList;
import java.util.List;
import org.simmetrics.ListMetric;

/**
 * Measures the similarity between two lists. Idea taken from <a
 * href="http://www.catalysoft.com/articles/StrikeAMatch.html">How to Strike a
 * Match</a>.
 * 
 * <p>
 * <code>
 * similarity(a,b) = 2 * |(a A b)|  / (|a| + |b|)
 * </code>
 * 
 * <p>
 * The A operation takes the list intersection of <code>a</code> and
 * <code>b</code>. This is a list <code>c</code> such that each element in has a
 * 1-to-1 relation to an element in both <code>a</code> and <code>b</code>. E.g.
 * the list intersection of <code>[ab,ab,ab,ac]</code> and
 * <code>[ab,ab,ad]</code> is <code>[ab,ab]</code>.
 * 
 * <p>
 * This metric is very similar to Dice's coefficient however Simon White used
 * the list intersection rather then the set intersection to prevent list of
 * duplicates from scoring a perfect match against a list with single elements.
 * E.g. 'GGGGG' should not be identical to 'GG'.
 * 
 * 
 * 
 * @see DiceSimilarity
 * 
 * @author mpkorstanje
 * @param <T>
 *            type of the token
 * 
 */
public class SimonWhite<T extends java.lang.Object> implements ListMetric<T> {
  @Override public float compare(List<T> a, List<T> b) {
    if (a.isEmpty() || b.isEmpty()) {
      return 0.0f;
    }
    b = new ArrayList<>(b);
    int union = a.size() + b.size();
    int intersection = 0;
    for (T token : a) {
      if (b.remove(token)) {
        intersection++;
      }
    }
    return 2.0f * intersection / union;
  }

  @Override public String toString() {
    return "SimonWhite";
  }
}