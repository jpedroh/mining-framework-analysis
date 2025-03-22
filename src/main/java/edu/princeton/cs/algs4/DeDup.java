package edu.princeton.cs.algs4;

/**
 *  The {@code DeDup} class provides a client for reading in a sequence of
 *  words from standard input and printing each word, removing any duplicates.
 *  It is useful as a test client for various symbol table implementations.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/35applications">Section 3.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class DeDup {
  private DeDup() {
  }

  public static void main(String[] args) {
    SET<String> set = new SET<String>();
    while (!StdIn.isEmpty()) {
      String key = StdIn.readString();
      if (!set.contains(key)) {
        set.add(key);
        StdOut.println(key);
      }
    }
  }
}