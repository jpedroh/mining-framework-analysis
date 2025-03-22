package org.jgrapht.alg.interfaces;
import java.util.*;
import org.jgrapht.*;

/**
 * Computes a vertex cover in an undirected graph. A vertex cover of a graph is a set of vertices
 * such that each edge of the graph is incident to at least one vertex in the set. A minimum vertex
 * cover is a vertex cover having the smallest possible number of vertices for a given graph. The
 * size of a minimum vertex cover of a graph G is known as the vertex cover number. A vertex cover
 * of minimum weight is a vertex cover where the sum of weights assigned to the individual vertices
 * in the cover has been minimized. The minimum vertex cover problem is a special case of the
 * minimum weighted vertex cover problem where all vertices have equal weight.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Joris Kinable
 * @deprecated replaced by {@link VertexCoverAlgorithm}
 */
@Deprecated public interface MinimumVertexCoverAlgorithm<V extends java.lang.Object, E extends java.lang.Object> {
  /**
     * Computes a vertex cover; all vertices are considered to have equal weight.
     *
     * @param graph the graph
     * @return a vertex cover
     */
  VertexCover<V> getVertexCover(Graph<V, E> graph);

  @Deprecated interface VertexCover<V extends java.lang.Object> extends Iterable<V> {
    /**
         * Returns the weight of the vertex cover. When solving the minimum weighted vertex cover
         * problem, the weight returned is the sum of the weights of the vertices in the cover. When
         * solving the unweighted variant, the cardinality of the vertex cover is returned instead.
         * 
         * @return weight of the vertex cover
         */
    double getWeight();

    /**
         * Set of vertices constituting the vertex cover
         * 
         * @return vertices in the vertex cover
         */
    Set<V> getVertices();

    /**
         * Returns an iterator over the vertices in the cover.
         * 
         * @return iterator over the vertices in the cover.
         */
    @Override default Iterator<V> iterator() {
      return getVertices().iterator();
    }
  }

  class VertexCoverImpl<V extends java.lang.Object> implements VertexCover<V> {
    protected Set<V> cover;

    protected double weight;

    public VertexCoverImpl(Set<V> cover, double weight) {
      this.cover = cover;
      this.weight = weight;
    }

    @Override public double getWeight() {
      return weight;
    }

    @Override public Set<V> getVertices() {
      return cover;
    }

    @Override public String toString() {
      return "Cover(" + this.getWeight() + "): " + this.getVertices().toString();
    }
  }
}