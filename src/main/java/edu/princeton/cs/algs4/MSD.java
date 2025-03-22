package edu.princeton.cs.algs4;

/**
 *  The {@code MSD} class provides static methods for sorting an
 *  array of extended ASCII strings or integers using MSD radix sort.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/51radix">Section 5.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class MSD {
  private static final int BITS_PER_BYTE = 8;

  private static final int BITS_PER_INT = 32;

  private static final int R = 256;

  private static final int CUTOFF = 15;

  private MSD() {
  }

  /**
     * Rearranges the array of extended ASCII strings in ascending order.
     *
     * @param a the array to be sorted
     */
  public static void sort(String[] a) {
    int n = a.length;
    String[] aux = new String[n];
    sort(a, 0, n - 1, 0, aux);
  }

  private static int charAt(String s, int d) {
    assert d >= 0 && d <= s.length();
    if (d == s.length()) {
      return -1;
    }
    return s.charAt(d);
  }

  private static void sort(String[] a, int lo, int hi, int d, String[] aux) {
    if (hi <= lo + CUTOFF) {
      insertion(a, lo, hi, d);
      return;
    }
    int[] count = new int[R + 2];
    for (int i = lo; i <= hi; i++) {
      int c = charAt(a[i], d);
      count[c + 2]++;
    }
    for (int r = 0; r < R + 1; r++) {
      count[r + 1] += count[r];
    }
    for (int i = lo; i <= hi; i++) {
      int c = charAt(a[i], d);
      aux[count[c + 1]++] = a[i];
    }
    for (int i = lo; i <= hi; i++) {
      a[i] = aux[i - lo];
    }
    for (int r = 0; r < R; r++) {
      sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
    }
  }

  private static void insertion(String[] a, int lo, int hi, int d) {
    for (int i = lo; i <= hi; i++) {
      for (int j = i; j > lo && less(a[j], a[j - 1], d); j--) {
        exch(a, j, j - 1);
      }
    }
  }

  private static void exch(String[] a, int i, int j) {
    String temp = a[i];
    a[i] = a[j];
    a[j] = temp;
  }

  private static boolean less(String v, String w, int d) {
    for (int i = d; i < Math.min(v.length(), w.length()); i++) {
      if (v.charAt(i) < w.charAt(i)) {
        return true;
      }
      if (v.charAt(i) > w.charAt(i)) {
        return false;
      }
    }
    return v.length() < w.length();
  }

  /**
     * Rearranges the array of 32-bit integers in ascending order.
     * Currently assumes that the integers are nonnegative.
     *
     * @param a the array to be sorted
     */
  public static void sort(int[] a) {
    int n = a.length;
    int[] aux = new int[n];
    sort(a, 0, n - 1, 0, aux);
  }

  private static void sort(int[] a, int lo, int hi, int d, int[] aux) {
    if (hi <= lo + CUTOFF) {
      insertion(a, lo, hi, d);
      return;
    }
    int[] count = new int[R + 1];
    int mask = R - 1;
    int shift = BITS_PER_INT - BITS_PER_BYTE * d - BITS_PER_BYTE;
    for (int i = lo; i <= hi; i++) {
      int c = (a[i] >> shift) & mask;
      count[c + 1]++;
    }
    for (int r = 0; r < R; r++) {
      count[r + 1] += count[r];
    }
    for (int i = lo; i <= hi; i++) {
      int c = (a[i] >> shift) & mask;
      aux[count[c]++] = a[i];
    }
    for (int i = lo; i <= hi; i++) {
      a[i] = aux[i - lo];
    }
    if (d == 4) {
      return;
    }
    if (count[0] > 0) {
      sort(a, lo, lo + count[0] - 1, d + 1, aux);
    }
    for (int r = 0; r < R; r++) {
      if (count[r + 1] > count[r]) {
        sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
      }
    }
  }

  private static void insertion(int[] a, int lo, int hi, int d) {
    for (int i = lo; i <= hi; i++) {
      for (int j = i; j > lo && a[j] < a[j - 1]; j--) {
        exch(a, j, j - 1);
      }
    }
  }

  private static void exch(int[] a, int i, int j) {
    int temp = a[i];
    a[i] = a[j];
    a[j] = temp;
  }

  /**
     * Reads in a sequence of extended ASCII strings from standard input;
     * MSD radix sorts them;
     * and prints them to standard output in ascending order.
     */
  public static void main(String[] args) {
    String[] a = StdIn.readAllStrings();
    int n = a.length;
    sort(a);
    for (int i = 0; i < n; i++) {
      StdOut.println(a[i]);
    }
  }
}