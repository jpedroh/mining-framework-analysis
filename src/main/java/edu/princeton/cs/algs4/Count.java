package edu.princeton.cs.algs4;

/**
 *  The {@code Count} class provides an {@link Alphabet} client for reading
 *  in a piece of text and computing the frequency of occurrence of each
 *  character over a given alphabet.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/55compress">Section 5.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Count {
  private Count() {
  }

  /**
     * Reads in text from standard input; calculates the frequency of
     * occurrence of each character over the alphabet specified as a
     * commmand-line argument; and prints the frequencies to standard
     * output.
     */
  public static void main(String[] args) {
    Alphabet alphabet = new Alphabet(args[0]);
    final int R = alphabet.radix();
    int[] count = new int[R];
    while (StdIn.hasNextChar()) {
      char c = StdIn.readChar();
      if (alphabet.contains(c)) {
        count[alphabet.toIndex(c)]++;
      }
    }
    for (int c = 0; c < R; c++) {
      StdOut.println(alphabet.toChar(c) + " " + count[c]);
    }
  }
}