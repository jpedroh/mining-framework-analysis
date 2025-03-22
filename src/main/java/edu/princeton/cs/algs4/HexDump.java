package edu.princeton.cs.algs4;

/**
 *  The {@code HexDump} class provides a client for displaying the contents
 *  of a binary file in hexadecimal.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/55compress">Section 5.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *  <p>
 *  See also {@link BinaryDump} and {@link PictureDump}.
 *  For more full-featured versions, see the Unix utilities
 *  {@code od} (octal dump) and {@code hexdump} (hexadecimal dump).
 *  <p>
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class HexDump {
  private HexDump() {
  }

  /**
     * Reads in a sequence of bytes from standard input and writes
     * them to standard output using hexademical notation, k hex digits
     * per line, where k is given as a command-line integer (defaults
     * to 16 if no integer is specified); also writes the number
     * of bits.
     */
  public static void main(String[] args) {
    int bytesPerLine = 16;
    if (args.length == 1) {
      bytesPerLine = Integer.parseInt(args[0]);
    }
    int i;
    for (i = 0; !BinaryStdIn.isEmpty(); i++) {
      if (bytesPerLine == 0) {
        BinaryStdIn.readChar();
        continue;
      }
      if (i == 0) {
        StdOut.printf("");
      } else {
        if (i % bytesPerLine == 0) {
          StdOut.printf("\n", i);
        } else {
          StdOut.print(" ");
        }
      }
      char c = BinaryStdIn.readChar();
      StdOut.printf("%02x", c & 0xff);
    }
    if (bytesPerLine != 0) {
      StdOut.println();
    }
    StdOut.println((i * 8) + " bits");
  }
}