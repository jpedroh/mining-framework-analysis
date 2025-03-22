package edu.princeton.cs.algs4;

/**
 *  The {@code Average} class provides a client for reading in a sequence
 *  of real numbers and printing out their average.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/11model">Section 1.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Average {
  private Average() {
  }

  /**
     * Reads in a sequence of real numbers from standard input and prints
     * out their average to standard output.
     */
  public static void main(String[] args) {
    int count = 0;
    double sum = 0.0;
    while (!StdIn.isEmpty()) {
      double value = StdIn.readDouble();
      sum += value;
      count++;
    }
    double average = sum / count;
    StdOut.println("Average is " + average);
  }
}