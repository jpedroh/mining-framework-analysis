package org.jgrapht.alg.color;
import java.util.*;
import org.jgrapht.*;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

/**
 * Brown graph coloring algorithm.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Michael Behrisch
 */
public class BrownBacktrackColoring<V extends java.lang.Object, E extends java.lang.Object> implements VertexColoringAlgorithm<V> {
  private final List<V> vertexList;

  private final int[][] neighbors;

  private final Map<V, Integer> indexMap;

  private int[] partialColorAssignment;

  private int[] colorCount;

  private BitSet[] allowedColors;

  private int chi;

  private int[] completeColorAssignment;

  private Coloring<V> vertexColoring;

  /**
     * Construct a new Brown backtracking algorithm.
     * 
     * @param graph the input graph
     */
  public BrownBacktrackColoring(Graph<V, E> graph) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    Objects.requireNonNull(graph, "Graph cannot be null");
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/e9c2ae5e4bc145c7cb69daed5efea1a772be815c/jgrapht-core/src/main/java/org/jgrapht/alg/color/BrownBacktrackColoring.java/right.java

    final int numVertices = graph.vertexSet().size();
    vertexList = new ArrayList<>(numVertices);
    neighbors = new int[numVertices][];
    indexMap = new HashMap<>(numVertices);
    for (V vertex : graph.vertexSet()) {
      neighbors[vertexList.size()] = new int[graph.edgesOf(vertex).size()];
      indexMap.put(vertex, vertexList.size());
      vertexList.add(vertex);
    }
    for (int i = 0; i < numVertices; i++) {
      int nbIndex = 0;
      final V vertex = vertexList.get(i);
      for (E e : graph.edgesOf(vertex)) {
        neighbors[i][nbIndex++] = indexMap.get(Graphs.getOppositeVertex(graph, e, vertex));
      }
    }
  }

  private void recursiveColor(int pos) {
    colorCount[pos] = colorCount[pos - 1];
    allowedColors[pos].set(0, colorCount[pos] + 1);
    for (int i = 0; i < neighbors[pos].length; i++) {
      final int nb = neighbors[pos][i];
      if (partialColorAssignment[nb] > 0) {
        allowedColors[pos].clear(partialColorAssignment[nb]);
      }
    }
    for (int i = 1; (i <= colorCount[pos]) && (colorCount[pos] < chi); i++) {
      if (allowedColors[pos].get(i)) {
        partialColorAssignment[pos] = i;
        if (pos < (neighbors.length - 1)) {
          recursiveColor(pos + 1);
        } else {
          chi = colorCount[pos];
          System.arraycopy(partialColorAssignment, 0, completeColorAssignment, 0, partialColorAssignment.length);
        }
      }
    }
    if ((colorCount[pos] + 1) < chi) {
      colorCount[pos]++;
      partialColorAssignment[pos] = colorCount[pos];
      if (pos < (neighbors.length - 1)) {
        recursiveColor(pos + 1);
      } else {
        chi = colorCount[pos];
        System.arraycopy(partialColorAssignment, 0, completeColorAssignment, 0, partialColorAssignment.length);
      }
    }
    partialColorAssignment[pos] = 0;
  }

  private void lazyComputeColoring() {
    if (vertexColoring != null) {
      return;
    }
    chi = neighbors.length + 1;
    partialColorAssignment = new int[neighbors.length];
    completeColorAssignment = new int[neighbors.length];
    partialColorAssignment[0] = 1;
    colorCount = new int[neighbors.length];
    colorCount[0] = 1;
    allowedColors = new BitSet[neighbors.length];
    for (int i = 0; i < neighbors.length; i++) {
      allowedColors[i] = new BitSet(1);
    }
    recursiveColor(1);
    Map<V, Integer> colorMap = new LinkedHashMap<>();
    for (int i = 0; i < vertexList.size(); i++) {
      colorMap.put(vertexList.get(i), completeColorAssignment[i]);
    }
    vertexColoring = new ColoringImpl<>(colorMap, chi);
  }

  /**
     * Returns the <a href="http://mathworld.wolfram.com/ChromaticNumber.html">chromatic number</a> of the input graph
     * @return chromatic number of the graph
     */
  public int getChromaticNumber() {
    lazyComputeColoring();
    return vertexColoring.getNumberColors();
  }

  @Override public Coloring<V> getColoring() {
    lazyComputeColoring();
    return vertexColoring;
  }
}