package org.jgrapht.alg.vertexcover;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.util.*;

/**
 * Finds a minimum vertex cover in a undirected graph. The implementation relies on a recursive
 * algorithm. At each recursive step, the algorithm picks a unvisited vertex v and distinguishes two
 * cases: either v has to be added to the vertex cover or all of its neighbors.
 *
 * In pseudo code, the algorithm (simplified) looks like this:
 *
 * <pre>
 * <code>
 *  VC(G):
 *  if V = ∅ then return ∅
 *  Choose an arbitrary node v ∈ G
 *  G1 := (V − {v}, { e ∈ E | v ∈/ e })
 *  G2 := (V − {v} − N(v), { e ∈ E | e ∩ (N(v) ∪ {v})= ∅ })
 *  if |{v} ∪ VC(G1)| ≤ |N(v) ∪ VC(G2)| then
 *    return {v} ∪ VC(G1)
 *  else
 *    return N(v) ∪ VC(G2)
 * </code>
 * </pre>
 *
 * To speed up the implementation, memoization and a bounding procedure are used. The current
 * implementation solves instances with 150-250 vertices efficiently to optimality.
 *
 * TODO JK: determine runtime complexity and add it to class description. TODO JK: run this class
 * through a performance profiler
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Joris Kinable
 */
public class RecursiveExactVCImpl<V extends java.lang.Object, E extends java.lang.Object> implements MinimumWeightedVertexCoverAlgorithm<V, E>, VertexCoverAlgorithm<V> {
  /** Input graph **/
  private Graph<V, E> graph;

  /** Number of vertices in the graph **/
  private int N;

  /**
     * Neighbor cache TODO JK: It might be worth trying to replace the neighbors index by a bitset
     * view. As such, all operations can be simplified to bitset operations, which may improve the
     * algorithm's performance.
     **/
  private NeighborCache<V, E> neighborCache;

  /** Map for memoization **/
  private Map<BitSet, BitSetCover> memo;

  /**
     * Ordered list of vertices which will be iteratively considered to be included in a matching
     **/
  private List<V> vertices;

  /** Mapping of a vertex to its index in the list of vertices **/
  private Map<V, Integer> vertexIDDictionary;

  /**
     * Maximum weight of the vertex cover. In case there is no weight assigned to the vertices, the
     * weight of the cover equals the cover's cardinality.
     */
  private double upperBoundOnVertexCoverWeight;

  /** Indicates whether we are solving a weighted or unweighted version of the problem **/
  private boolean weighted;

  private Map<V, Double> vertexWeightMap = null;

  /**
     * Temporary constructor to ensure one-version-backwards-compatibility
     * @deprecated this constructor will be removed in the next release
     */
  @Deprecated public RecursiveExactVCImpl() {
    graph = null;
    vertexWeightMap = null;
  }

  /**
     * Constructs a new GreedyVCImpl instance
     * @param graph input graph
     */
  public RecursiveExactVCImpl(Graph<V, E> graph) {
    this.graph = GraphTests.requireUndirected(graph);
    this.vertexWeightMap = graph.vertexSet().stream().collect(Collectors.toMap(Function.identity(), (vertex) -> 1.0));
    weighted = false;
  }

  /**
     * Constructs a new GreedyVCImpl instance
     * @param graph input graph
     * @param vertexWeightMap mapping of vertex weights
     */
  public RecursiveExactVCImpl(Graph<V, E> graph, Map<V, Double> vertexWeightMap) {
    this.graph = GraphTests.requireUndirected(graph);
    this.vertexWeightMap = Objects.requireNonNull(vertexWeightMap);
    weighted = true;
  }

  @Override public VertexCoverAlgorithm.VertexCover<V> getVertexCover() {
    this.graph = GraphTests.requireUndirected(graph);
    memo = new HashMap<>();
    vertices = new ArrayList<>(graph.vertexSet());
    neighborCache = new NeighborCache<>(graph);
    vertexIDDictionary = new HashMap<>();
    N = vertices.size();
    vertices.sort(Comparator.comparingDouble((v) -> vertexWeightMap.get(v) / graph.degreeOf(v)));
    for (int i = 0; i < vertices.size(); i++) {
      vertexIDDictionary.put(vertices.get(i), i);
    }
    upperBoundOnVertexCoverWeight = this.calculateUpperBound();
    BitSetCover vertexCover = this.calculateCoverRecursively(0, new BitSet(N), 0);
    Set<V> verticesInCover = new LinkedHashSet<>();
    for (int i = vertexCover.bitSetCover.nextSetBit(0); i >= 0 && i < N; i = vertexCover.bitSetCover.nextSetBit(i + 1)) {
      verticesInCover.add(vertices.get(i));
    }
    return new VertexCoverAlgorithm.VertexCoverImpl<>(verticesInCover, vertexCover.weight);
  }

  @Override public MinimumVertexCoverAlgorithm.VertexCover<V> getVertexCover(Graph<V, E> graph) {
    Map<V, Double> vertexWeightMap = graph.vertexSet().stream().collect(Collectors.toMap(Function.identity(), (vertex) -> 1.0));
    weighted = false;
    return this.getVertexCover(graph, vertexWeightMap);
  }

  @Override public MinimumVertexCoverAlgorithm.VertexCover<V> getVertexCover(Graph<V, E> graph, Map<V, Double> vertexWeightMap) {
    this.graph = GraphTests.requireUndirected(graph);
    memo = new HashMap<>();
    vertices = new ArrayList<>(graph.vertexSet());
    neighborCache = new NeighborCache<>(graph);
    vertexIDDictionary = new HashMap<>();
    this.vertexWeightMap = vertexWeightMap;
    this.weighted = vertexWeightMap != null;
    N = vertices.size();
    vertices.sort(Comparator.comparingDouble((V v) -> vertexWeightMap.get(v) / graph.degreeOf(v)));
    for (int i = 0; i < vertices.size(); i++) {
      vertexIDDictionary.put(vertices.get(i), i);
    }
    upperBoundOnVertexCoverWeight = this.calculateUpperBound();
    BitSetCover vertexCover = this.calculateCoverRecursively(0, new BitSet(N), 0);
    Set<V> verticesInCover = new LinkedHashSet<>();
    for (int i = vertexCover.bitSetCover.nextSetBit(0); i >= 0 && i < N; i = vertexCover.bitSetCover.nextSetBit(i + 1)) {
      verticesInCover.add(vertices.get(i));
    }
    return new MinimumVertexCoverAlgorithm.VertexCoverImpl<>(verticesInCover, vertexCover.weight);
  }

  private BitSetCover calculateCoverRecursively(int indexNextCandidate, BitSet visited, double accumulatedWeight) {
    if (memo.containsKey(visited)) {
      return memo.get(visited).copy();
    }
    int indexNextVertex = -1;
    Set<V> neighbors = Collections.emptySet();
    for (int index = visited.nextClearBit(indexNextCandidate); index >= 0 && index < N; index = visited.nextClearBit(index + 1)) {
      neighbors = new LinkedHashSet<>(neighborCache.neighborsOf(vertices.get(index)));
      for (Iterator<V> it = neighbors.iterator(); it.hasNext(); ) {
        if (visited.get(vertexIDDictionary.get(it.next()))) {
          it.remove();
        }
      }
      if (!neighbors.isEmpty()) {
        indexNextVertex = index;
        break;
      }
    }
    if (indexNextVertex == -1) {
      BitSetCover vertexCover = new BitSetCover(N, 0);
      if (accumulatedWeight <= upperBoundOnVertexCoverWeight) {
        upperBoundOnVertexCoverWeight = accumulatedWeight - 1;
      }
      return vertexCover;
    } else {
      if (accumulatedWeight >= upperBoundOnVertexCoverWeight) {
        return new BitSetCover(N, N);
      }
    }
    BitSet visitedRightBranch = (BitSet) visited.clone();
    visitedRightBranch.set(indexNextVertex);
    for (V v : neighbors) {
      visitedRightBranch.set(vertexIDDictionary.get(v));
    }
    double weight = this.getWeight(neighbors);
    BitSetCover rightCover = calculateCoverRecursively(indexNextVertex + 1, visitedRightBranch, accumulatedWeight + weight);
    List<Integer> neighborsIndices = neighbors.stream().map(vertexIDDictionary::get).collect(Collectors.toList());
    rightCover.addAllVertices(neighborsIndices, weight);
    BitSet visitedLeftBranch = (BitSet) visited.clone();
    visitedLeftBranch.set(indexNextVertex);
    weight = vertexWeightMap.get(vertices.get(indexNextVertex));
    BitSetCover leftCover = calculateCoverRecursively(indexNextVertex + 1, visitedLeftBranch, accumulatedWeight + weight);
    leftCover.addVertex(indexNextVertex, weight);
    if (leftCover.weight <= rightCover.weight) {
      memo.put(visited, leftCover.copy());
      return leftCover;
    } else {
      memo.put(visited, rightCover.copy());
      return rightCover;
    }
  }

  /**
     * Returns the weight of a collection of vertices. In case of the unweighted vertex cover
     * problem, the return value is the cardinality of the collection. In case of the weighted
     * version, the return value is the sum of the weights of the vertices
     * 
     * @param vertices vertices
     * @return the total weight of the vertices in the collection.
     */
  private double getWeight(Collection<V> vertices) {
    if (weighted) {
      return vertices.stream().map(vertexWeightMap::get).reduce(0d, Double::sum);
    } else {
      return vertices.size();
    }
  }

  /**
     * Calculates a cheap upper bound on the optimum solution. Currently, we return the best
     * solution found by either the greedy heuristic, or Clarkson's 2-approximation. Neither of
     * these 2 algorithms dominates the other. //TODO JK: Are there better bounding procedures?
     */
  private double calculateUpperBound() {
    return Math.min(new GreedyVCImpl<>(graph, vertexWeightMap).getVertexCover().getWeight(), new ClarksonTwoApproxVCImpl<>(graph, vertexWeightMap).getVertexCover().getWeight());
  }

  protected class BitSetCover {
    protected BitSet bitSetCover;

    protected double weight;

    /**
         * Construct a new empty vertex cover as a BitSet.
         * 
         * @param size initial capacity of the BitSet
         * @param initialWeight the initial weight
         */
    protected BitSetCover(int size, int initialWeight) {
      bitSetCover = new BitSet(size);
      this.weight = initialWeight;
    }

    /**
         * Copy constructor
         * 
         * @param vertexCover the input vertex cover to copy
         */
    protected BitSetCover(BitSetCover vertexCover) {
      this.bitSetCover = (BitSet) vertexCover.bitSetCover.clone();
      this.weight = vertexCover.weight;
    }

    /**
         * Copy a vertex cover.
         * 
         * @return a copy of the vertex cover
         */
    protected BitSetCover copy() {
      return new BitSetCover(this);
    }

    /**
         * Add a vertex in the vertex cover.
         * 
         * @param vertexIndex the index of the vertex
         * @param weight the weight of the vertex
         */
    protected void addVertex(int vertexIndex, double weight) {
      bitSetCover.set(vertexIndex);
      this.weight += weight;
    }

    /**
         * Add multiple vertices in the vertex cover.
         * 
         * @param vertexIndices the index of the vertices
         * @param totalWeight the total weight of the vertices
         */
    protected void addAllVertices(List<Integer> vertexIndices, double totalWeight) {
      vertexIndices.forEach(bitSetCover::set);
      this.weight += totalWeight;
    }
  }
}