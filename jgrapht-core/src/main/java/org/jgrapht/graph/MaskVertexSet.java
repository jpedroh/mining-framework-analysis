package org.jgrapht.graph;
import java.util.*;
import org.jgrapht.util.*;
import org.jgrapht.util.PrefetchIterator.*;

/**
 * Helper for {@link MaskSubgraph}.
 *
 * @author Guillaume Boulmier
 * @since July 5, 2007
 */
class MaskVertexSet<V extends java.lang.Object, E extends java.lang.Object> extends AbstractSet<V> {
  private MaskFunctor<V, E> mask;

  private Set<V> vertexSet;

  private transient TypeUtil<V> vertexTypeDecl = null;

  public MaskVertexSet(Set<V> vertexSet, MaskFunctor<V, E> mask) {
    this.vertexSet = vertexSet;
    this.mask = mask;
  }

  /**
     * @see java.util.Collection#contains(java.lang.Object)
     */
  @Override public boolean contains(Object o) {
    V v = (V) o;
    return vertexSet.contains(v) && !mask.isVertexMasked(v);
  }

  /**
     * @see java.util.Set#iterator()
     */
  @Override public Iterator<V> iterator() {
    return new PrefetchIterator<V>(new MaskVertexSetNextElementFunctor());
  }

  /**
     * @see java.util.Set#size()
     */
  @Override public int size() {

<<<<<<< /usr/src/app/output/jgrapht/jgrapht/09493beacf894be0d9ecc446171ee7963830d048/jgrapht-core/src/main/java/org/jgrapht/graph/MaskVertexSet.java/left.java
    if (this.size == -1) {
      this.size = 0;
      for (Iterator<V> iter = iterator(); iter.hasNext(); iter.next()) {
        this.size++;
      }
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    return (int) vertexSet.stream().filter((v) -> contains(v)).count();
  }

  private class MaskVertexSetNextElementFunctor implements NextElementFunctor<V> {
    private Iterator<V> iter;

    public MaskVertexSetNextElementFunctor() {
      this.iter = MaskVertexSet.this.vertexSet.iterator();
    }

    @Override public V nextElement() throws NoSuchElementException {
      V element = this.iter.next();
      while (MaskVertexSet.this.mask.isVertexMasked(element)) {
        element = this.iter.next();
      }
      return element;
    }
  }
}