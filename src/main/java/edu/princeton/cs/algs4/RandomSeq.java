package edu.princeton.cs.algs4;

/**
 *  The {@code RandomSeq} class is a client that prints out a pseudorandom
 *  sequence of real numbers in a given range.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/11model">Section 1.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class RandomSeq {
  private RandomSeq() {
  }

  /**
     * Reads in two command-line arguments lo and hi and prints n uniformly
     * random real numbers in [lo, hi) to standard output.
     */
  public static void main(String[] args) {
    int n = Integer.parseInt(args[0]);
    if (args.length == 1) {
      for (int i = 0; i < n; i++) {
        double x = StdRandom.uniform();
        StdOut.println(x);
      }
    } else {
      if (args.length == 3) {
        double lo = Double.parseDouble(args[1]);
        double hi = Double.parseDouble(args[2]);
        for (int i = 0; i < n; i++) {
          double x = StdRandom.uniform(lo, hi);
          StdOut.printf("%.2f\n", x);
        }
      } else {
        throw new IllegalArgumentException("Invalid number of arguments");
      }
    }
  }
}