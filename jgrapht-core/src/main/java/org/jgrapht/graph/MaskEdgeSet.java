package org.jgrapht.graph;
import java.util.*;
import org.jgrapht.*;
import org.jgrapht.util.*;
import org.jgrapht.util.PrefetchIterator.*;

/**
 * Helper for {@link MaskSubgraph}.
 *
 * @author Guillaume Boulmier
 * @since July 5, 2007
 */
class MaskEdgeSet<V extends java.lang.Object, E extends java.lang.Object> extends AbstractSet<E> {
  private Set<E> edgeSet;

  private Graph<V, E> graph;

  private MaskFunctor<V, E> mask;

  private transient TypeUtil<E> edgeTypeDecl = null;

  public MaskEdgeSet(Graph<V, E> graph, Set<E> edgeSet, MaskFunctor<V, E> mask) {
    this.graph = graph;
    this.edgeSet = edgeSet;
    this.mask = mask;
  }

  /**
     * @see java.util.Collection#contains(java.lang.Object)
     */
  @Override public boolean contains(Object o) {
    E e = (E) o;
    return edgeSet.contains(e) && !mask.isEdgeMasked(e) && !mask.isVertexMasked(graph.getEdgeSource(e)) && !mask.isVertexMasked(graph.getEdgeTarget(e));
  }

  /**
     * @see java.util.Set#iterator()
     */
  @Override public Iterator<E> iterator() {
    return new PrefetchIterator<E>(new MaskEdgeSetNextElementFunctor());
  }

  /**
     * @see java.util.Set#size()
     */
  @Override public int size() {

<<<<<<< /usr/src/app/output/jgrapht/jgrapht/09493beacf894be0d9ecc446171ee7963830d048/jgrapht-core/src/main/java/org/jgrapht/graph/MaskEdgeSet.java/left.java
    if (this.size == -1) {
      this.size = 0;
      for (Iterator<E> iter = iterator(); iter.hasNext(); iter.next()) {
        this.size++;
      }
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    return (int) edgeSet.stream().filter((e) -> contains(e)).count();
  }

  private class MaskEdgeSetNextElementFunctor implements NextElementFunctor<E> {
    private Iterator<E> iter;

    public MaskEdgeSetNextElementFunctor() {
      this.iter = MaskEdgeSet.this.edgeSet.iterator();
    }

    @Override public E nextElement() throws NoSuchElementException {
      E edge = this.iter.next();
      while (isMasked(edge)) {
        edge = this.iter.next();
      }
      return edge;
    }

    private boolean isMasked(E edge) {
      return MaskEdgeSet.this.mask.isEdgeMasked(edge) || MaskEdgeSet.this.mask.isVertexMasked(MaskEdgeSet.this.graph.getEdgeSource(edge)) || MaskEdgeSet.this.mask.isVertexMasked(MaskEdgeSet.this.graph.getEdgeTarget(edge));
    }
  }
}