package edu.princeton.cs.algs4;
import java.util.Comparator;

/**
 *  The {@code MergeX} class provides static methods for sorting an
 *  array using an optimized version of mergesort.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/22mergesort">Section 2.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class MergeX {
  private static final int CUTOFF = 7;

  private MergeX() {
  }

  private static void merge(Comparable[] src, Comparable[] dst, int lo, int mid, int hi) {
    assert isSorted(src, lo, mid);
    assert isSorted(src, mid + 1, hi);
    int i = lo, j = mid + 1;
    for (int k = lo; k <= hi; k++) {
      if (i > mid) {
        dst[k] = src[j++];
      } else {
        if (j > hi) {
          dst[k] = src[i++];
        } else {
          if (less(src[j], src[i])) {
            dst[k] = src[j++];
          } else {
            dst[k] = src[i++];
          }
        }
      }
    }
    assert isSorted(dst, lo, hi);
  }

  private static void sort(Comparable[] src, Comparable[] dst, int lo, int hi) {
    if (hi <= lo + CUTOFF) {
      insertionSort(dst, lo, hi);
      return;
    }
    int mid = lo + (hi - lo) / 2;
    sort(dst, src, lo, mid);
    sort(dst, src, mid + 1, hi);
    if (!less(src[mid + 1], src[mid])) {
      System.arraycopy(src, lo, dst, lo, hi - lo + 1);
      return;
    }
    merge(src, dst, lo, mid, hi);
  }

  /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
  public static void sort(Comparable[] a) {
    Comparable[] aux = a.clone();
    sort(aux, a, 0, a.length - 1);
    assert isSorted(a);
  }

  private static void insertionSort(Comparable[] a, int lo, int hi) {
    for (int i = lo; i <= hi; i++) {
      for (int j = i; j > lo && less(a[j], a[j - 1]); j--) {
        exch(a, j, j - 1);
      }
    }
  }

  /*******************************************************************
     *  Utility methods.
     *******************************************************************/
  private static void exch(Object[] a, int i, int j) {
    Object swap = a[i];
    a[i] = a[j];
    a[j] = swap;
  }

  private static boolean less(Comparable a, Comparable b) {
    return a.compareTo(b) < 0;
  }

  private static boolean less(Object a, Object b, Comparator comparator) {
    return comparator.compare(a, b) < 0;
  }

  /**
     * Rearranges the array in ascending order, using the provided order.
     * @param a the array to be sorted
     */
  public static void sort(Object[] a, Comparator comparator) {
    Object[] aux = a.clone();
    sort(aux, a, 0, a.length - 1, comparator);
    assert isSorted(a, comparator);
  }

  private static void merge(Object[] src, Object[] dst, int lo, int mid, int hi, Comparator comparator) {
    assert isSorted(src, lo, mid, comparator);
    assert isSorted(src, mid + 1, hi, comparator);
    int i = lo, j = mid + 1;
    for (int k = lo; k <= hi; k++) {
      if (i > mid) {
        dst[k] = src[j++];
      } else {
        if (j > hi) {
          dst[k] = src[i++];
        } else {
          if (less(src[j], src[i], comparator)) {
            dst[k] = src[j++];
          } else {
            dst[k] = src[i++];
          }
        }
      }
    }
    assert isSorted(dst, lo, hi, comparator);
  }

  private static void sort(Object[] src, Object[] dst, int lo, int hi, Comparator comparator) {
    if (hi <= lo + CUTOFF) {
      insertionSort(dst, lo, hi, comparator);
      return;
    }
    int mid = lo + (hi - lo) / 2;
    sort(dst, src, lo, mid, comparator);
    sort(dst, src, mid + 1, hi, comparator);
    if (!less(src[mid + 1], src[mid], comparator)) {
      System.arraycopy(src, lo, dst, lo, hi - lo + 1);
      return;
    }
    merge(src, dst, lo, mid, hi, comparator);
  }

  private static void insertionSort(Object[] a, int lo, int hi, Comparator comparator) {
    for (int i = lo; i <= hi; i++) {
      for (int j = i; j > lo && less(a[j], a[j - 1], comparator); j--) {
        exch(a, j, j - 1);
      }
    }
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
    return isSorted(a, 0, a.length - 1, comparator);
  }

  private static boolean isSorted(Object[] a, int lo, int hi, Comparator comparator) {
    for (int i = lo + 1; i <= hi; i++) {
      if (less(a[i], a[i - 1], comparator)) {
        return false;
      }
    }
    return true;
  }

  private static void show(Object[] a) {
    for (int i = 0; i < a.length; i++) {
      StdOut.println(a[i]);
    }
  }

  /**
     * Reads in a sequence of strings from standard input; mergesorts them
     * (using an optimized version of mergesort); 
     * and prints them to standard output in ascending order. 
     */
  public static void main(String[] args) {
    String[] a = StdIn.readAllStrings();
    MergeX.sort(a);
    show(a);
  }
}