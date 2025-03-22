package edu.princeton.cs.algs4;

/**
 *  The {@code LongestRepeatedSubstring} class provides a {@link SuffixArray}
 *  client for computing the longest repeated substring of a string that
 *  appears at least twice. The repeated substrings may overlap (but must
 *  be distinct).
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/63suffix">Section 6.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *  <p>
 *  See also {@link LongestCommonSubstring}.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class LongestRepeatedSubstring {
  private LongestRepeatedSubstring() {
  }

  /**
     * Returns the longest repeated substring of the specified string.
     *
     * @param  text the string
     * @return the longest repeated substring that appears in {@code text};
     *         the empty string if no such string
     */
  public static String lrs(String text) {
    int n = text.length();
    SuffixArray sa = new SuffixArray(text);
    String lrs = "";
    for (int i = 1; i < n; i++) {
      int length = sa.lcp(i);
      if (length > lrs.length()) {
        lrs = text.substring(sa.index(i), sa.index(i) + length);
      }
    }
    return lrs;
  }

  /**
     * Unit tests the {@code lrs()} method.
     */
  public static void main(String[] args) {
    String text = StdIn.readAll().replaceAll("\\s+", " ");
    StdOut.println("\'" + lrs(text) + "\'");
  }
}