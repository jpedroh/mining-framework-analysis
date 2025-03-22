package edu.princeton.cs.algs4;

/**
 *  The {@code LZW} class provides static methods for compressing
 *  and expanding a binary input using LZW compression over the 8-bit extended
 *  ASCII alphabet with 12-bit codewords.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/55compress">Section 5.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick  
 *  @author Kevin Wayne
 */
public class LZW {
  private static final int R = 256;

  private static final int L = 4096;

  private static final int W = 12;

  private LZW() {
  }

  /**
     * Reads a sequence of 8-bit bytes from standard input; compresses
     * them using LZW compression with 12-bit codewords; and writes the results
     * to standard output.
     */
  public static void compress() {
    String input = BinaryStdIn.readString();
    TST<Integer> st = new TST<Integer>();
    for (int i = 0; i < R; i++) {
      st.put("" + (char) i, i);
    }
    int code = R + 1;
    while (input.length() > 0) {
      String s = st.longestPrefixOf(input);
      BinaryStdOut.write(st.get(s), W);
      int t = s.length();
      if (t < input.length() && code < L) {
        st.put(input.substring(0, t + 1), code++);
      }
      input = input.substring(t);
    }
    BinaryStdOut.write(R, W);
    BinaryStdOut.close();
  }

  /**
     * Reads a sequence of bit encoded using LZW compression with
     * 12-bit codewords from standard input; expands them; and writes
     * the results to standard output.
     */
  public static void expand() {
    String[] st = new String[L];
    int i;
    for (i = 0; i < R; i++) {
      st[i] = "" + (char) i;
    }
    st[i++] = "";
    int codeword = BinaryStdIn.readInt(W);
    if (codeword == R) {
      return;
    }
    String val = st[codeword];
    while (true) {
      BinaryStdOut.write(val);
      codeword = BinaryStdIn.readInt(W);
      if (codeword == R) {
        break;
      }
      String s = st[codeword];
      if (i == codeword) {
        s = val + val.charAt(0);
      }
      if (i < L) {
        st[i++] = val + s.charAt(0);
      }
      val = s;
    }
    BinaryStdOut.close();
  }

  /**
     * Sample client that calls {@code compress()} if the command-line
     * argument is "-" an {@code expand()} if it is "+".
     */
  public static void main(String[] args) {
    if (args[0].equals("-")) {
      compress();
    } else {
      if (args[0].equals("+")) {
        expand();
      } else {
        throw new IllegalArgumentException("Illegal command line argument");
      }
    }
  }
}