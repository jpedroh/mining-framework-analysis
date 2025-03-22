package edu.princeton.cs.algs4;
import java.util.Comparator;

/**
 *  The {@code Selection} class provides static methods for sorting an
 *  array using selection sort.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/21elementary">Section 2.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Selection {
  private Selection() {
  }

  /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
  public static void sort(Comparable[] a) {
    int n = a.length;
    for (int i = 0; i < n; i++) {
      int min = i;
      for (int j = i + 1; j < n; j++) {
        if (less(a[j], a[min])) {
          min = j;
        }
      }
      exch(a, i, min);
      assert isSorted(a, 0, i);
    }
    assert isSorted(a);
  }

  /**
     * Rearranges the array in ascending order, using a comparator.
     * @param a the array
     * @param comparator the comparator specifying the order
     */
  public static void sort(Object[] a, Comparator comparator) {
    int n = a.length;
    for (int i = 0; i < n; i++) {
      int min = i;
      for (int j = i + 1; j < n; j++) {
        if (less(comparator, a[j], a[min])) {
          min = j;
        }
      }
      exch(a, i, min);
      assert isSorted(a, comparator, 0, i);
    }
    assert isSorted(a, comparator);
  }

  /***************************************************************************
    *  Helper sorting functions.
    ***************************************************************************/
  private static boolean less(Comparable v, Comparable w) {
    return v.compareTo(w) < 0;
  }

  private static boolean less(Comparator comparator, Object v, Object w) {
    return comparator.compare(v, w) < 0;
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

  private static boolean isSorted(Object[] a, Comparator comparator) {
    return isSorted(a, comparator, 0, a.length - 1);
  }

  private static boolean isSorted(Object[] a, Comparator comparator, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++) {
      if (less(comparator, a[i], a[i - 1])) {
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
     * Reads in a sequence of strings from standard input; selection sorts them; 
     * and prints them to standard output in ascending order. 
     */
  public static void main(String[] args) {
    String[] a = StdIn.readAllStrings();
    Selection.sort(a);
    show(a);
  }
}