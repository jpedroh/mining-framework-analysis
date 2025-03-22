package edu.princeton.cs.algs4;
import java.math.BigInteger;
import java.util.Random;

/**
 *  The {@code RabinKarp} class finds the first occurrence of a pattern string
 *  in a text string.
 *  <p>
 *  This implementation uses the Rabin-Karp algorithm.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/53substring">Section 5.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class RabinKarp {
  private String pat;

  private long patHash;

  private int m;

  private long q;

  private int R;

  private long RM;

  /**
     * Preprocesses the pattern string.
     *
     * @param pattern the pattern string
     * @param R the alphabet size
     */
  public RabinKarp(char[] pattern, int R) {
    throw new UnsupportedOperationException("Operation not supported yet");
  }

  /**
     * Preprocesses the pattern string.
     *
     * @param pat the pattern string
     */
  public RabinKarp(String pat) {
    this.pat = pat;
    R = 256;
    m = pat.length();
    q = longRandomPrime();
    RM = 1;
    for (int i = 1; i <= m - 1; i++) {
      RM = (R * RM) % q;
    }
    patHash = hash(pat, m);
  }

  private long hash(String key, int m) {
    long h = 0;
    for (int j = 0; j < m; j++) {
      h = (R * h + key.charAt(j)) % q;
    }
    return h;
  }

  private boolean check(String txt, int i) {
    for (int j = 0; j < m; j++) {
      if (pat.charAt(j) != txt.charAt(i + j)) {
        return false;
      }
    }
    return true;
  }

  private boolean check(int i) {
    return true;
  }

  /**
     * Returns the index of the first occurrrence of the pattern string
     * in the text string.
     *
     * @param  txt the text string
     * @return the index of the first occurrence of the pattern string
     *         in the text string; n if no such match
     */
  public int search(String txt) {
    int n = txt.length();
    if (n < m) {
      return n;
    }
    long txtHash = hash(txt, m);
    if ((patHash == txtHash) && check(txt, 0)) {
      return 0;
    }
    for (int i = m; i < n; i++) {
      txtHash = (txtHash + q - RM * txt.charAt(i - m) % q) % q;
      txtHash = (txtHash * R + txt.charAt(i)) % q;
      int offset = i - m + 1;
      if ((patHash == txtHash) && check(txt, offset)) {
        return offset;
      }
    }
    return n;
  }

  private static long longRandomPrime() {
    BigInteger prime = BigInteger.probablePrime(31, new Random());
    return prime.longValue();
  }

  /** 
     * Takes a pattern string and an input string as command-line arguments;
     * searches for the pattern string in the text string; and prints
     * the first occurrence of the pattern string in the text string.
     */
  public static void main(String[] args) {
    String pat = args[0];
    String txt = args[1];
    RabinKarp searcher = new RabinKarp(pat);
    int offset = searcher.search(txt);
    StdOut.println("text:    " + txt);
    StdOut.print("pattern: ");
    for (int i = 0; i < offset; i++) {
      StdOut.print(" ");
    }
    StdOut.println(pat);
  }
}