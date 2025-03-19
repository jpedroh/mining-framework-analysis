package org.rdfhdt.hdtjena.cache;
import org.apache.jena.graph.Node;

/**
 * @author mario.arias
 *
 */
public class DictionaryCacheArray implements DictionaryCache {
  private Node[] array;

  final int capacity;

  int numentries;

  public DictionaryCacheArray(int capacity) {
    array = null;
    numentries = 0;
    this.capacity = capacity;
  }

  @Override public Node get(int id) {
    if (array == null) {
      return null;
    }
    if (id > array.length) {
      throw new IndexOutOfBoundsException();
    }
    return array[id - 1];
  }

  public void put(int id, Node node) {
    if (array == null) {
      array = new Node[(int) capacity];
    }
    if (array[id - 1] == null) {
      numentries++;
    }
    array[id - 1] = node;
  }

  @Override public int size() {
    return numentries;
  }

  @Override public void clear() {
    array = null;
  }
}