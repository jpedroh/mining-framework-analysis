package org.jgrapht.alg.color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

/**
 * Color refinement algorithm that finds the coarsest stable coloring of a graph based on a given
 * <code>alpha</code> coloring as described in the following
 * <a href="https://doi.org/10.1007/s00224-016-9686-0">paper</a>: C. Berkholz, P. Bonsma, and M.
 * Grohe. Tight lower and upper bounds for the complexity of canonical colour refinement. Theory of
 * Computing Systems, 60(4), p581--614, 2017.
 * 
 * <p>
 * The complexity of this algorithm is $O((|V| + |E|)log |V|)$.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Christoph Grüne
 * @author Daniel Mock
 * @author Oliver Feith
 */
public class ColorRefinementAlgorithm<V extends java.lang.Object, E extends java.lang.Object> implements VertexColoringAlgorithm<V> {
  private final Graph<V, E> graph;

  private final Coloring<V> alpha;

  /**
     * Construct a new coloring algorithm.
     *
     * @param graph the input graph
     * @param alpha the coloring on the graph to be refined
     */
  public ColorRefinementAlgorithm(Graph<V, E> graph, Coloring<V> alpha) {
    this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
    this.alpha = Objects.requireNonNull(alpha, "alpha cannot be null");
    if (!isAlphaConsistent(alpha, graph)) {
      throw new IllegalArgumentException("alpha is not a valid surjective l-coloring for the given graph.");
    }
  }

  /**
     * Construct a new coloring algorithm.
     *
     * @param graph the input graph
     */
  public ColorRefinementAlgorithm(Graph<V, E> graph) {
    this(graph, getDefaultAlpha(graph.vertexSet()));
  }

  /**
     * Calculates a canonical surjective k-coloring of the given graph such that the classes of the
     * coloring form the coarsest stable partition that refines alpha.
     *
     * @return the calculated coloring
     */
  @Override public Coloring<V> getColoring() {
    ColoringRepresentation rep = new ColoringRepresentation(graph, alpha);
    Deque<Integer> refineStack = getSortedStack(alpha);
    while (!refineStack.isEmpty()) {
      Integer currentColor = refineStack.pop();
      Set<Integer> adjacentColors = calculateColorDegrees(currentColor, rep);
      List<Integer> colorsToBeSplit = adjacentColors.stream().filter((c) -> rep.minColorDegree[c] < rep.maxColorDegree[c]).collect(Collectors.toCollection(ArrayList::new));
      colorsToBeSplit.sort(Comparator.comparingInt((o) -> o));
      colorsToBeSplit.forEach((color) -> splitUpColor(color, refineStack, rep));
      cleanupColorDegrees(adjacentColors, rep);
    }
    return new ColoringImpl<>(rep.coloring, rep.coloring.size());
  }

  /**
     * Helper method that calculates the color degree for every vertex and the maximum and minimum
     * color degree for every color.
     *
     * @param refiningColor color to refine
     * @param rep the coloring representation
     * @return the list of all colors that have at least one vertex with colorDegree >= 1
     */
  private Set<Integer> calculateColorDegrees(int refiningColor, ColoringRepresentation rep) {
    int n = graph.vertexSet().size();
    Set<Integer> adjacentColors = new LinkedHashSet<>(n);
    for (V v : rep.colorClasses.get(refiningColor)) {
      Set<V> inNeighborhood = graph.incomingEdgesOf(v).stream().map((e) -> Graphs.getOppositeVertex(graph, e, v)).collect(Collectors.toSet());
      for (V w : inNeighborhood) {
        rep.colorDegree.put(w, rep.colorDegree.get(w) + 1);
        if (rep.colorDegree.get(w) == 1) {
          rep.positiveDegreeColorClasses.get(rep.coloring.get(w)).add(w);
        }
        adjacentColors.add(rep.coloring.get(w));
        if (rep.colorDegree.get(w) > rep.maxColorDegree[rep.coloring.get(w)]) {
          rep.maxColorDegree[rep.coloring.get(w)] = rep.colorDegree.get(w);
        }
      }
    }
    for (Integer c : adjacentColors) {
      if (rep.colorClasses.get(c).size() != rep.positiveDegreeColorClasses.get(c).size()) {
        rep.minColorDegree[c] = 0;
      } else {
        rep.minColorDegree[c] = rep.maxColorDegree[c];
        for (V v : rep.positiveDegreeColorClasses.get(c)) {
          if (rep.colorDegree.get(v) < rep.minColorDegree[c]) {
            rep.minColorDegree[c] = rep.colorDegree.get(v);
          }
        }
      }
    }
    return adjacentColors;
  }

  /**
     * Helper method that cleanups the internal representation of color degrees for a new iteration.
     *
     * @param adjacentColors the list of all colors that have at least one vertex with colorDegree
     *        >= 1
     * @param rep the coloring representation
     */
  private void cleanupColorDegrees(Set<Integer> adjacentColors, ColoringRepresentation rep) {
    adjacentColors.stream().forEach((c) -> {
      for (V v : rep.positiveDegreeColorClasses.get(c)) {
        rep.colorDegree.put(v, 0);
      }
      rep.maxColorDegree[c] = 0;
      rep.positiveDegreeColorClasses.put(c, new ArrayList<>());
    });
  }

  /**
     * Helper method for splitting up a color.
     *
     * @param color the color to split the color class for
     * @param refineStack the stack containing all colors that have to be refined
     * @param rep the coloring representation
     */
  private void splitUpColor(Integer color, Deque<Integer> refineStack, ColoringRepresentation rep) {
    Map<Integer, Integer> numColorDegree = new HashMap<>();
    for (int i = 1; i <= rep.maxColorDegree[color]; ++i) {
      numColorDegree.put(i, 0);
    }
    numColorDegree.put(0, rep.colorClasses.get(color).size() - rep.positiveDegreeColorClasses.get(color).size());
    for (V v : rep.positiveDegreeColorClasses.get(color)) {
      numColorDegree.put(rep.colorDegree.get(v), numColorDegree.get(rep.colorDegree.get(v)) + 1);
    }
    int maxColorDegreeIndex = 0;
    for (int i = 1; i <= rep.maxColorDegree[color]; ++i) {
      if (numColorDegree.get(i) > numColorDegree.get(maxColorDegreeIndex)) {
        maxColorDegreeIndex = i;
      }
    }
    Map<Integer, Integer> newMapping = new HashMap<>();
    boolean isCurrentColorInStack = refineStack.contains(color);
    int currentMaxColorDegree = rep.maxColorDegree[color];
    for (int i = 0; i <= currentMaxColorDegree; ++i) {
      if (numColorDegree.get(i) >= 1) {
        if (i == rep.minColorDegree[color]) {
          newMapping.put(i, color);
          if (!isCurrentColorInStack && maxColorDegreeIndex != i) {
            refineStack.push(newMapping.get(i));
          }
        } else {
          newMapping.put(i, ++rep.lastUsedColor);
          if (isCurrentColorInStack || i != maxColorDegreeIndex) {
            refineStack.push(newMapping.get(i));
          }
        }
      }
    }
    for (V v : rep.positiveDegreeColorClasses.get(color)) {
      if (!newMapping.get(rep.colorDegree.get(v)).equals(color)) {
        rep.colorClasses.get(color).remove(v);
        rep.colorClasses.get(newMapping.get(rep.colorDegree.get(v))).add(v);
        rep.coloring.replace(v, newMapping.get(rep.colorDegree.get(v)));
      }
    }
  }

  /**
     * Checks whether alpha is a valid surjective l-coloring for the given graph
     *
     * @param alpha the surjective l-coloring to be checked
     * @param graph the graph that is colored by alpha
     * @return whether alpha is a valid surjective l-coloring for the given graph
     */
  private boolean isAlphaConsistent(Coloring<V> alpha, Graph<V, E> graph) {
    if (alpha.getColors().size() != graph.vertexSet().size()) {
      return false;
    }
    if (alpha.getColorClasses().size() != alpha.getNumberColors()) {
      return false;
    }
    for (V v : graph.vertexSet()) {
      if (!alpha.getColors().containsKey(v)) {
        return false;
      }
      Integer currentColor = alpha.getColors().get(v);
      if (currentColor + 1 > alpha.getNumberColors() || currentColor < 0) {
        return false;
      }
    }
    return true;
  }

  /**
     * Returns a coloring such that all vertices have the same (zero) color.
     *
     * @param vertices the vertices that should be colored
     * @return the all-0 coloring
     */
  private static <V extends java.lang.Object> Coloring<V> getDefaultAlpha(Set<V> vertices) {
    Map<V, Integer> alpha = new HashMap<>();
    for (V v : vertices) {
      alpha.put(v, 0);
    }
    return new ColoringImpl<>(alpha, 1);
  }

  /**
     * Returns a canonically sorted stack of all colors of alpha. It is important that alpha is
     * consistent.
     *
     * @param alpha the surjective l-coloring
     * @return a canonically sorted stack of all colors of alpha
     */
  private Deque<Integer> getSortedStack(Coloring<V> alpha) {
    int numberColors = alpha.getNumberColors();
    Deque<Integer> stack = new ArrayDeque<>(graph.vertexSet().size());
    for (int i = numberColors - 1; i >= 0; --i) {
      stack.push(i);
    }
    return stack;
  }

  private class ColoringRepresentation {
    /**
         * mapping from all colors to their classes
         */
    HashMap<Integer, List<V>> colorClasses;

    /**
         * mapping from color to their classes, whereby every vertex in the classes has
         * colorDegree(v) >= 1
         */
    HashMap<Integer, List<V>> positiveDegreeColorClasses;

    /**
         * mapping from color to its maximum color degree
         */
    int[] maxColorDegree;

    /**
         * mapping from color to its minimum color degree
         */
    int[] minColorDegree;

    /**
         * mapping from vertex to the vertex color degree (number of neighbors with different
         * colors)
         */
    Map<V, Integer> colorDegree;

    /**
         * The actual coloring
         */
    Map<V, Integer> coloring;

    /**
         * Last used color
         */
    int lastUsedColor;

    public ColoringRepresentation(Graph<V, E> graph, Coloring<V> alpha) {
      int n = graph.vertexSet().size();
      this.colorClasses = new HashMap<>(n);
      this.positiveDegreeColorClasses = new HashMap<>(n);
      this.maxColorDegree = new int[n];
      this.minColorDegree = new int[n];
      this.colorDegree = new HashMap<>();
      this.coloring = new HashMap<>();
      for (int c = 0; c < n; ++c) {
        colorClasses.put(c, new ArrayList<>());
        positiveDegreeColorClasses.put(c, new ArrayList<>());
      }
      for (V v : graph.vertexSet()) {
        colorClasses.get(alpha.getColors().get(v)).add(v);
        colorDegree.put(v, 0);
        coloring.put(v, alpha.getColors().get(v));
      }
      lastUsedColor = alpha.getNumberColors() - 1;
    }
  }
}