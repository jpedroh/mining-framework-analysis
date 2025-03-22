package org.jgrapht.generate;
import java.util.*;
import org.jgrapht.*;

/**
 * Generates a <a href="http://mathworld.wolfram.com/HypercubeGraph.html">hyper cube graph</a> of
 * any size. This is a graph that can be represented by bit strings, so for an n-dimensial hypercube
 * each vertex resembles an n-length bit string. Then, two vertices are adjacent if and only if
 * their bitstring differ by exactly one element.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Andrew Newell
 * @since Dec 21, 2008
 */
public class HyperCubeGraphGenerator<V extends java.lang.Object, E extends java.lang.Object> implements GraphGenerator<V, E, V> {
  private int dim;

  /**
     * Creates a new generator
     *
     * @param dim the dimension of the hypercube
     */
  public HyperCubeGraphGenerator(int dim) {
    this.dim = dim;
  }

  @Override public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
    int order = (int) Math.pow(2, dim);
    LinkedList<V> vertices = new LinkedList<>();
    for (int i = 0; i < order; i++) {
      V newVertex = target.addVertex();
      vertices.add(newVertex);
      if (resultMap != null) {
        StringBuilder s = new StringBuilder(Integer.toBinaryString(i));
        while (s.length() < dim) {
          s.insert(0, "0");
        }
        resultMap.put(s.toString(), newVertex);
      }
    }
    for (int i = 0; i < order; i++) {
      for (int j = i + 1; j < order; j++) {
        for (int z = 0; z < dim; z++) {
          if ((j ^ i) == (1 << z)) {
            target.addEdge(vertices.get(i), vertices.get(j));
            break;
          }
        }
      }
    }
  }
}