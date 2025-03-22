package edu.princeton.cs.algs4;

/**
 *  The {@code Knuth} class provides a client for reading in a 
 *  sequence of strings and <em>shuffling</em> them using the Knuth (or Fisher-Yates)
 *  shuffling algorithm. This algorithm guarantees to rearrange the
 *  elements in uniformly random order, under
 *  the assumption that Math.random() generates independent and
 *  uniformly distributed numbers between 0 and 1.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/11model">Section 1.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *  See {@link StdRandom} for versions that shuffle arrays and
 *  subarrays of objects, doubles, and ints.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Knuth {
  private Knuth() {
  }

  /**
     * Rearranges an array of objects in uniformly random order
     * (under the assumption that {@code Math.random()} generates independent
     * and uniformly distributed numbers between 0 and 1).
     * @param a the array to be shuffled
     */
  public static void shuffle(Object[] a) {
    int n = a.length;
    for (int i = 0; i < n; i++) {
      int r = i + (int) (Math.random() * (n - i));
      Object swap = a[r];
      a[r] = a[i];
      a[i] = swap;
    }
  }

  /**
     * Reads in a sequence of strings from standard input, shuffles
     * them, and prints out the results.
     */
  public static void main(String[] args) {
    String[] a = StdIn.readAllStrings();
    Knuth.shuffle(a);
    for (int i = 0; i < a.length; i++) {
      StdOut.println(a[i]);
    }
  }
}