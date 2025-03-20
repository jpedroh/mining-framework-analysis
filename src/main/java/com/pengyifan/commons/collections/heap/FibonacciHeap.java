package com.pengyifan.commons.collections.heap;
import com.google.common.collect.Lists;
import edu.stanford.nlp.util.ErasureUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.List;

/**
 * This class implements a Fibonacci heap. Much of the code in this class is
 * based on the algorithms in the "Introduction to Algorithms" by Cormen,
 * Leiserson, and Rivest in Chapter 21.
 * <p>
 * The amortized cost of most of these methods is O(1), making it a very fast
 * data structure. Several have an actual running time of O(1). extractMin()
 * and delete() have O(log n) amortized running times because they do the heap
 * consolidation.
 * 
 * @author Yifan Peng
 * @version 09/06/2011
 */
public class FibonacciHeap<E extends java.lang.Object> {
  private static final double oneOverLogPhi = 1.0 / Math.log((1.0 + Math.sqrt(5.0)) / 2.0);

  /**
   * Points to the root of a tree containing a minimum key. If the heap is
   * empty, then min = NIL.
   */
  private Entry<E> min;

  /**
   * The number of nodes currently in the heap.
   */
  private int n;

  /**
   * Creates and returns a new heap containing no elements.
   */
  public FibonacciHeap() {
    min = null;
  }

  /**
   * Assigns to node x within heap the new key value k, which is assumed to be
   * no greater than its current key value.
   * <p>
   * Amortized cost: O(1)
   * 
   * @param x node to decrease the key of
   * @param key new key value for node x
   * 
   * @throws IllegalArgumentException if k is larger than x.key value.
   */
  public void decreaseKey(Entry<E> x, int key) {
    if (key > x.key) {
      throw new IllegalArgumentException("new key is greater than current key: " + key + '>' + x.key);
    }
    x.key = key;
    Entry<E> y = x.parent;
    if ((y != null) && (x.key < y.key)) {
      cut(x, y);
      cascadingCut(y);
    }
    if (x.key < min.key) {
      min = x;
    }
  }

  /**
   * Cut the link between x and its parent y, making x a root.
   * <p>
   * Running time: O(1)
   * 
   * @param x child of y to be removed from y's child list
   * @param y parent of x about to lose a child
   */
  protected void cut(Entry<E> x, Entry<E> y) {
    y.child = removeNode(y.child, x);
    y.degree--;
    min = concatenateNode(min, x);
    x.parent = null;
    x.mark = false;
  }

  /**
   * Cut y from its parent and recurses its way up the tree until either a root
   * or an unmarked node is found.
   * <p>
   * Running time: O(1) exclusive of recursive calls.
   * 
   * @param y node to perform cascading cut on
   */
  private void cascadingCut(Entry<E> y) {
    Entry<E> z = y.parent;
    if (z != null) {
      if (!y.mark) {
        y.mark = true;
      } else {
        cut(y, z);
        cascadingCut(z);
      }
    }
  }

  /**
   * Deletes node x from heap. Assume there is no key value of -NUB_VALUE
   * currently in the heap.
   * <p>
   * Amortized cost: O(log n)
   * 
   * @param x node to remove from heap
   */
  public void delete(Entry<E> x) {
    decreaseKey(x, Integer.MIN_VALUE);
    extractMin();
  }

  /**
   * Inserts node x, whose key field has already been filled in, into heap.
   * <p>
   * Actual cost: O(1)
   * <p>
   * Amortized cost: O(1)
   * 
   * @param x new node to insert into heap
   */
  public void insert(Entry<E> x) {
    x.degree = 0;
    x.parent = null;
    x.child = null;
    x.left = x;
    x.right = x;
    x.mark = false;
    min = concatenateNode(min, x);
    if (min == null || x.key < min.key) {
      min = x;
    }
    n++;
  }

  private Entry<E> concatenateNode(Entry<E> list, Entry<E> node) {
    if (list == null) {
      node.left = node;
      node.right = node;
      return node;
    } else {
      node.left = list;
      node.right = list.right;
      list.right = node;
      node.right.left = node;
      return list;
    }
  }

  /**
   * Returns a pointer to the node in heap whose key is minimum.
   * <p>
   * Running time: O(1) actual
   * 
   * @return a pointer to the node in heap whose key is minimum
   */
  public Entry minimum() {
    return min;
  }

  /**
   * Deletes the node from heap whose key is minimum, returning a pointer to
   * the node.
   * <p>
   * Amortized cost: O(log n)
   * 
   * @return a pointer to the node in heap whose key is minimum
   */
  public Entry<E> extractMin() {
    Entry<E> z = min;
    if (z != null) {
      if (z.child != null) {
        for (Entry<E> x : z.child.nodelist()) {
          min = concatenateNode(min, x);
          x.parent = null;
        }
      }
      min = removeNode(min, z);
      if (z == z.right) {
        min = null;
      } else {
        min = z.right;
        consolidate();
      }
      n--;
    }
    return z;
  }

  /**
   * Reduce the number of trees in the heap.
   */
  private void consolidate() {
    int arraySize = ((int) Math.floor(Math.log(n) * oneOverLogPhi)) + 1;
    Entry<E>[] array = ErasureUtils.mkTArray(min.getClass(), arraySize);
    if (min != null) {
      for (Entry<E> w : min.nodelist()) {
        Entry<E> x = w;
        int d = w.degree;
        while (array[d] != null) {
          Entry<E> y = array[d];
          if (x.key > y.key) {
            Entry<E> tmp = x;
            x = y;
            y = tmp;
          }
          link(y, x);
          array[d] = null;
          d++;
        }
        array[d] = x;
      }
    }
    min = null;
    for (int i = 0; i < array.length; i++) {
      if (array[i] != null) {
        min = concatenateNode(min, array[i]);
        if (min == null || array[i].key < min.key) {
          min = array[i];
        }
      }
    }
  }

  /**
   * Make node y a child of node x.
   * <p>
   * Actual cost: O(1)
   * 
   * @param y node to become child
   * @param x node to become parent
   */
  protected void link(Entry<E> y, Entry<E> x) {
    min = removeNode(min, y);
    y.parent = x;
    y.right = y;
    y.left = y;
    x.child = concatenateNode(x.child, y);
    x.degree++;
    y.mark = false;
  }

  /**
   * Remove node from list.
   *
   * @return if the list is empty, return null
   */
  private Entry<E> removeNode(Entry<E> list, Entry<E> node) {
    if (node.left == node) {
      return null;
    }
    node.left.right = node.right;
    node.right.left = node.left;
    return list;
  }

  /**
   * Add heap h into the heap.
   * <p>
   * Actual cost: O(1)
   * 
   * @param h heap
   */
  public void union(FibonacciHeap<E> h) {
    min = concatenateList(min, h.min);
    if (min == null || (h.min != null && h.min.key < min.key)) {
      min = h.min;
    }
    n += h.n;
  }

  private Entry<E> concatenateList(Entry<E> list1, Entry<E> list2) {
    if (list1 != null && list2 != null) {
      list1.right.left = list2.left;
      list2.left.right = list1.right;
      list1.right = list2;
      list2.left = list1;
      return list1;
    } else {
      if (list1 == null) {
        return list2;
      } else {
        return list1;
      }
    }
  }

  public static class Entry<E extends java.lang.Object> {
    /**
     * Any one of its children
     */
    Entry<E> child;

    /**
     * Left sibling
     */
    Entry<E> left;

    /**
     * Its parent
     */
    Entry<E> parent;

    /**
     * Right sibling
     */
    Entry<E> right;

    /**
     * Whether this node has lost a child since the last time this node was
     * made the child of another node.
     */
    boolean mark;

    /**
     * Key for this node
     */
    int key;

    /**
     * Value for this node
     */
    E obj;

    /**
     * The number of children in the child list
     */
    int degree;

    /**
     * Returns the key corresponding to this entry.
     * 
     * @return the key corresponding to this entry
     */
    public final int getKey() {
      return key;
    }

    /**
     * Returns the value corresponding to this entry.
     * 
     * @return the value corresponding to this entry
     */
    public final E getObject() {
      return obj;
    }

    public Entry(int key, E obj) {
      this.key = key;
      this.obj = obj;
    }

    /**
     * @return node list of the same level
     */
    public List<Entry<E>> nodelist() {
      List<Entry<E>> list = Lists.newArrayList();
      list.add(this);
      Entry<E> next = right;
      while (next != this) {
        list.add(next);
        next = next.right;
      }
      return list;
    }

    @Override public String toString() {
      return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("key", key).append("value", obj).append("mark", mark).toString();
    }
  }
}