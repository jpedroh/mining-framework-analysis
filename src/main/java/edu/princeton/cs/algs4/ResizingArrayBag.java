package edu.princeton.cs.algs4;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  The {@code ResizingArrayBag} class represents a bag (or multiset) of 
 *  generic items. It supports insertion and iterating over the 
 *  items in arbitrary order.
 *  <p>
 *  This implementation uses a resizing array.
 *  See {@link LinkedBag} for a version that uses a singly-linked list.
 *  The <em>add</em> operation takes constant amortized time; the
 *  <em>isEmpty</em>, and <em>size</em> operations
 *  take constant time. Iteration takes time proportional to the number of items.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/13stacks">Section 1.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class ResizingArrayBag<Item extends java.lang.Object> implements Iterable<Item> {
  private Item[] a;

  private int n;

  /**
     * Initializes an empty bag.
     */
  public ResizingArrayBag() {
    a = (Item[]) new Object[2];
    n = 0;
  }

  /**
     * Is this bag empty?
     * @return true if this bag is empty; false otherwise
     */
  public boolean isEmpty() {
    return n == 0;
  }

  /**
     * Returns the number of items in this bag.
     * @return the number of items in this bag
     */
  public int size() {
    return n;
  }

  private void resize(int capacity) {
    assert capacity >= n;
    Item[] temp = (Item[]) new Object[capacity];
    for (int i = 0; i < n; i++) {
      temp[i] = a[i];
    }
    a = temp;
  }

  /**
     * Adds the item to this bag.
     * @param item the item to add to this bag
     */
  public void add(Item item) {
    if (n == a.length) {
      resize(2 * a.length);
    }
    a[n++] = item;
  }

  /**
     * Returns an iterator that iterates over the items in the bag in arbitrary order.
     * @return an iterator that iterates over the items in the bag in arbitrary order
     */
  public Iterator<Item> iterator() {
    return new ArrayIterator();
  }

  private class ArrayIterator implements Iterator<Item> {
    private int i = 0;

    public boolean hasNext() {
      return i < n;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    public Item next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return a[i++];
    }
  }

  /**
     * Unit tests the {@code ResizingArrayBag} data type.
     */
  public static void main(String[] args) {
    ResizingArrayBag<String> bag = new ResizingArrayBag<String>();
    bag.add("Hello");
    bag.add("World");
    bag.add("how");
    bag.add("are");
    bag.add("you");
    for (String s : bag) {
      StdOut.println(s);
    }
  }
}