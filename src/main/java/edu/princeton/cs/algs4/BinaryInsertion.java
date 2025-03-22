package edu.princeton.cs.algs4;

/**
 *  The {@code BinaryInsertion} class provides a static method for sorting an
 *  array using an optimized binary insertion sort with half exchanges.
 *  <p>
 *  This implementation makes ~ n lg n compares for any array of length n.
 *  However, in the worst case, the running time is quadratic because the
 *  number of array accesses can be proportional to n^2 (e.g, if the array
 *  is reverse sorted). As such, it is not suitable for sorting large
 *  arrays (unless the number of inversions is small).
 *  <p>
 *  The sorting algorithm is stable and uses O(1) extra memory.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/21elementary">Section 2.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Ivan Pesin
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class BinaryInsertion {
  private BinaryInsertion() {
  }

  /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
  public static void sort(Comparable[] a) {
    int n = a.length;
    for (int i = 1; i < n; i++) {
      Comparable v = a[i];
      int lo = 0, hi = i;
      while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (less(v, a[mid])) {
          hi = mid;
        } else {
          lo = mid + 1;
        }
      }
      for (int j = i; j > lo; --j) {
        a[j] = a[j - 1];
      }
      a[lo] = v;
    }
    assert isSorted(a);
  }

  /***************************************************************************
    *  Helper sorting function.
    ***************************************************************************/
  private static boolean less(Comparable v, Comparable w) {
    return v.compareTo(w) < 0;
  }

  /***************************************************************************
    *  Check if array is sorted - useful for debugging.
    ***************************************************************************/
  private static boolean isSorted(Comparable[] a) {
    return isSorted(a, 0, a.length - 1);
  }

  private static boolean isSorted(Comparable[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++) {
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
    BinaryInsertion.sort(a);
    show(a);
  }
}