package edu.princeton.cs.algs4;

/**
 *  The {@code Quick3string} class provides static methods for sorting an
 *  array of strings using 3-way radix quicksort.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/51radix">Section 5.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Quick3string {
  private static final int CUTOFF = 15;

  private Quick3string() {
  }

  /**  
     * Rearranges the array of strings in ascending order.
     *
     * @param a the array to be sorted
     */
  public static void sort(String[] a) {
    StdRandom.shuffle(a);
    sort(a, 0, a.length - 1, 0);
    assert isSorted(a);
  }

  private static int charAt(String s, int d) {
    assert d >= 0 && d <= s.length();
    if (d == s.length()) {
      return -1;
    }
    return s.charAt(d);
  }

  private static void sort(String[] a, int lo, int hi, int d) {
    if (hi <= lo + CUTOFF) {
      insertion(a, lo, hi, d);
      return;
    }
    int lt = lo, gt = hi;
    int v = charAt(a[lo], d);
    int i = lo + 1;
    while (i <= gt) {
      int t = charAt(a[i], d);
      if (t < v) {
        exch(a, lt++, i++);
      } else {
        if (t > v) {
          exch(a, i, gt--);
        } else {
          i++;
        }
      }
    }
    sort(a, lo, lt - 1, d);
    if (v >= 0) {
      sort(a, lt, gt, d + 1);
    }
    sort(a, gt + 1, hi, d);
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
    assert v.substring(0, d).equals(w.substring(0, d));
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

  private static boolean isSorted(String[] a) {
    for (int i = 1; i < a.length; i++) {
      if (a[i].compareTo(a[i - 1]) < 0) {
        return false;
      }
    }
    return true;
  }

  /**
     * Reads in a sequence of fixed-length strings from standard input;
     * 3-way radix quicksorts them;
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