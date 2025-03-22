package edu.princeton.cs.algs4;

/**
 *  The {@code WhiteFilter} class provides a client for reading in a <em>whitelist</em>
 *  of words from a file; then, reading in a sequence of words from standard input,
 *  printing out each word that appears in the file.
 *  It is useful as a test client for various symbol table implementations.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/35applications">Section 3.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class WhiteFilter {
  private WhiteFilter() {
  }

  public static void main(String[] args) {
    SET<String> set = new SET<String>();
    In in = new In(args[0]);
    while (!in.isEmpty()) {
      String word = in.readString();
      set.add(word);
    }
    while (!StdIn.isEmpty()) {
      String word = StdIn.readString();
      if (set.contains(word)) {
        StdOut.println(word);
      }
    }
  }
}