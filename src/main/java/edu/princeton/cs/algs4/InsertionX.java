package edu.princeton.cs.algs4;

/**
 *  The {@code InsertionX} class provides static methods for sorting
 *  an array using an optimized version of insertion sort (with half exchanges
 *  and a sentinel).
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/21elementary">Section 2.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class InsertionX {
  private InsertionX() {
  }

  /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
  public static void sort(Comparable[] a) {
    int n = a.length;
    int exchanges = 0;
    for (int i = n - 1; i > 0; i--) {
      if (less(a[i], a[i - 1])) {
        exch(a, i, i - 1);
        exchanges++;
      }
    }
    if (exchanges == 0) {
      return;
    }
    for (int i = 2; i < n; i++) {
      Comparable v = a[i];
      int j = i;
      while (less(v, a[j - 1])) {
        a[j] = a[j - 1];
        j--;
      }
      a[j] = v;
    }
    assert isSorted(a);
  }

  /***************************************************************************
    *  Helper sorting functions.
    ***************************************************************************/
  private static boolean less(Comparable v, Comparable w) {
    return v.compareTo(w) < 0;
  }

  private static void exch(Object[] a, int i, int j) {
    Object swap = a[i];
    a[i] = a[j];
    a[j] = swap;
  }

  /***************************************************************************
    *  Check if array is sorted - useful for debugging.
    ***************************************************************************/
  private static boolean isSorted(Comparable[] a) {
    for (int i = 1; i < a.length; i++) {
      if (less(a[i], a[i - 1])) {
        return false;
      }
    }
    return true;
  }

  private static void show(Comparable[] a) {
    for (int i = 0; i < a.length; i++) {
      StdOut.println(a[i]);
    }
  }

  /**
     * Reads in a sequence of strings from standard input; insertion sorts them;
     * and prints them to standard output in ascending order.
     */
  public static void main(String[] args) {
    String[] a = StdIn.readAllStrings();
    InsertionX.sort(a);
    show(a);
  }
}