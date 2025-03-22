package org.jgrapht.graph;
import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;
import org.jgrapht.*;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.util.SupplierUtil;

/**
 * A directed acyclic graph (DAG).
 * 
 * <p>
 * Implements a DAG that can be modified (vertices &amp; edges added and removed), is guaranteed to
 * remain acyclic, and provides fast topological order iteration. An attempt to add an edge which
 * would induce a cycle throws an {@link IllegalArgumentException}.
 *
 * <p>
 * This is done using a dynamic topological sort which is based on the algorithm described in "David
 * J. Pearce &amp; Paul H. J. Kelly. A dynamic topological sort algorithm for directed acyclic
 * graphs. Journal of Experimental Algorithmics, 11, 2007." (see
 * <a href="http://www.mcs.vuw.ac.nz/~djp/files/PK-JEA07.pdf">paper</a> or
 * <a href="http://doi.acm.org/10.1145/1187436.1210590">ACM link</a> for details). The
 * implementation differs from the algorithm specified in the above paper in some ways, perhaps most
 * notably in that the topological ordering is stored by default using two hash maps, which will
 * have some effects on the runtime, but also allow for vertex addition and removal. This storage
 * mechanism can be adjusted by subclasses.
 * 
 * <p>
 * The complexity of adding a new edge in the graph depends on the number of edges incident to the
 * "affected region", and should in general be faster than recomputing the whole topological
 * ordering from scratch. For details about the complexity parameters and running times, see the
 * previously mentioned paper.
 *
 * <p>
 * This class makes no claims to thread safety, and concurrent usage from multiple threads will
 * produce undefined results.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Peter Giles
 */
public class DirectedAcyclicGraph<V extends java.lang.Object, E extends java.lang.Object> extends SimpleDirectedGraph<V, E> implements Iterable<V> {
  private static final long serialVersionUID = 4522128427004938150L;

  private static final String EDGE_WOULD_INDUCE_A_CYCLE = "Edge would induce a cycle";

  private final Comparator<V> topoComparator;

  private final TopoOrderMap<V> topoOrderMap;

  private int maxTopoIndex = 0;

  private int minTopoIndex = 0;

  private transient long topoModCount = 0;

  /**
     * The visited strategy factory to use. Subclasses can change this.
     */
  private final VisitedStrategyFactory visitedStrategyFactory;

  /**
     * Construct a directed acyclic graph.
     * 
     * @param edgeClass the edge class
     */
  public DirectedAcyclicGraph(Class<? extends E> edgeClass) {
    this(null, SupplierUtil.createSupplier(edgeClass), false);
  }

  /**
     * Construct a directed acyclic graph.
     *
     * @param vertexSupplier the vertex supplier
     * @param edgeSupplier the edge supplier
     * @param weighted if true the graph will be weighted, otherwise not
     */
  public DirectedAcyclicGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
    this(vertexSupplier, edgeSupplier, new VisitedBitSetImpl(), new TopoVertexBiMap<>(), weighted);
  }

  /**
     * Construct a directed acyclic graph.
     * 
     * @param edgeClass the edge class
     * @param weighted if true the graph will be weighted, otherwise not
     * @deprecated Use suppliers instead 
     */
  @Deprecated public DirectedAcyclicGraph(Class<? extends E> edgeClass, boolean weighted) {
    this(null, SupplierUtil.createSupplier(edgeClass), weighted);
  }

  /**
     * Construct a directed acyclic graph.
     * 
     * @param ef the edge factory
     * @deprecated Use suppliers instead 
     */
  @Deprecated public DirectedAcyclicGraph(EdgeFactory<V, E> ef) {
    this(ef, new VisitedBitSetImpl(), new TopoVertexBiMap<>(), false);
  }

  /**
     * Construct a directed acyclic graph.
     * 
     * @param ef the edge factory
     * @param weighted if true the graph will be weighted, otherwise not
     * @deprecated Use suppliers instead 
     */
  @Deprecated public DirectedAcyclicGraph(EdgeFactory<V, E> ef, boolean weighted) {
    this(ef, new VisitedBitSetImpl(), new TopoVertexBiMap<>(), weighted);
  }

  /**
     * Construct a directed acyclic graph.
     * 
     * @param ef the edge factory
     * @param visitedStrategyFactory the visited strategy factory. Subclasses can change this
     *        implementation to adjust the performance tradeoffs.
     * @param topoOrderMap the topological order map. For performance reasons, subclasses can change
     *        the way this class stores the topological order.
     * @param weighted if true the graph will be weighted, otherwise not
     * @deprecated Use suppliers instead 
     */
  @Deprecated protected DirectedAcyclicGraph(EdgeFactory<V, E> ef, VisitedStrategyFactory visitedStrategyFactory, TopoOrderMap<V> topoOrderMap, boolean weighted) {
    super(ef, weighted);
    this.visitedStrategyFactory = Objects.requireNonNull(visitedStrategyFactory, "Visited factory cannot be null");
    this.topoOrderMap = Objects.requireNonNull(topoOrderMap, "Topological order map cannot be null");
    this.topoComparator = new TopoComparator();
  }

  /**
     * Construct a directed acyclic graph.
     * 
     * @param vertexSupplier the vertex supplier
     * @param edgeSupplier the edge supplier
     * @param visitedStrategyFactory the visited strategy factory. Subclasses can change this
     *        implementation to adjust the performance tradeoffs.
     * @param topoOrderMap the topological order map. For performance reasons, subclasses can change
     *        the way this class stores the topological order.
     * @param weighted if true the graph will be weighted, otherwise not
     */
  protected DirectedAcyclicGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, VisitedStrategyFactory visitedStrategyFactory, TopoOrderMap<V> topoOrderMap, boolean weighted) {
    super(vertexSupplier, edgeSupplier, weighted);
    this.visitedStrategyFactory = Objects.requireNonNull(visitedStrategyFactory, "Visited factory cannot be null");
    this.topoOrderMap = Objects.requireNonNull(topoOrderMap, "Topological order map cannot be null");
    this.topoComparator = new TopoComparator();
  }

  /**
     * Create a builder for this kind of graph.
     * 
     * @param edgeClass class on which to base factory for edges
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return a builder for this kind of graph
     */
  public static <V extends java.lang.Object, E extends java.lang.Object> GraphBuilder<V, E, ? extends DirectedAcyclicGraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
    return new GraphBuilder<>(new DirectedAcyclicGraph<>(edgeClass));
  }

  /**
     * Create a builder for this kind of graph.
     * 
     * @param edgeSupplier edge supplier for the edges
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return a builder for this kind of graph
     */
  public static <V extends java.lang.Object, E extends java.lang.Object> GraphBuilder<V, E, ? extends DirectedAcyclicGraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
    return new GraphBuilder<>(new DirectedAcyclicGraph<>(null, edgeSupplier, false));
  }

  /**
     * Create a builder for this kind of graph.
     * 
     * @param ef the edge factory of the new graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return a builder for this kind of graph
     * @deprecated Use suppliers instead 
     */
  @Deprecated public static <V extends java.lang.Object, E extends java.lang.Object> GraphBuilder<V, E, ? extends DirectedAcyclicGraph<V, E>> createBuilder(EdgeFactory<V, E> ef) {
    return new GraphBuilder<>(new DirectedAcyclicGraph<>(ef));
  }

  @Override public GraphType getType() {
    return new DefaultGraphType.Builder().directed().weighted(super.getType().isWeighted()).allowMultipleEdges(false).allowSelfLoops(false).allowCycles(false).build();
  }

  @Override public V addVertex() {
    V v = super.addVertex();
    if (v != null) {
      ++maxTopoIndex;
      topoOrderMap.putVertex(maxTopoIndex, v);
      ++topoModCount;
    }
    return v;
  }

  @Override public boolean addVertex(V v) {
    boolean added = super.addVertex(v);
    if (added) {
      ++maxTopoIndex;
      topoOrderMap.putVertex(maxTopoIndex, v);
      ++topoModCount;
    }
    return added;
  }

  @Override public boolean removeVertex(V v) {
    boolean removed = super.removeVertex(v);
    if (removed) {
      Integer topoIndex = topoOrderMap.removeVertex(v);
      if (topoIndex == minTopoIndex) {
        while ((minTopoIndex < 0) && (topoOrderMap.getVertex(minTopoIndex) == null)) {
          ++minTopoIndex;
        }
      }
      if (topoIndex == maxTopoIndex) {
        while ((maxTopoIndex > 0) && (topoOrderMap.getVertex(maxTopoIndex) == null)) {
          --maxTopoIndex;
        }
      }
      ++topoModCount;
    }
    return removed;
  }

  /**
     * {@inheritDoc}
     * 
     * <p>
     * The complexity of adding a new edge in the graph depends on the number of edges incident to
     * the "affected region", and should in general be faster than recomputing the whole topological
     * ordering from scratch.
     * 
     * @throws IllegalArgumentException if the edge would induce a cycle in the graph
     */
  @Override public E addEdge(V sourceVertex, V targetVertex) {
    assertVertexExist(sourceVertex);
    assertVertexExist(targetVertex);
    E result;
    try {
      updateDag(sourceVertex, targetVertex);
      result = super.addEdge(sourceVertex, targetVertex);
    } catch (CycleFoundException e) {
      throw new IllegalArgumentException(EDGE_WOULD_INDUCE_A_CYCLE);
    }
    return result;
  }

  /**
     * {@inheritDoc}
     * 
     * <p>
     * The complexity of adding a new edge in the graph depends on the number of edges incident to
     * the "affected region", and should in general be faster than recomputing the whole topological
     * ordering from scratch.
     * 
     * @throws IllegalArgumentException if the edge would induce a cycle in the graph
     */
  @Override public boolean addEdge(V sourceVertex, V targetVertex, E e) {
    if (e == null) {
      throw new NullPointerException();
    } else {
      if (containsEdge(e)) {
        return false;
      }
    }
    assertVertexExist(sourceVertex);
    assertVertexExist(targetVertex);
    boolean result;
    try {
      updateDag(sourceVertex, targetVertex);
      result = super.addEdge(sourceVertex, targetVertex, e);
    } catch (CycleFoundException ex) {
      throw new IllegalArgumentException(EDGE_WOULD_INDUCE_A_CYCLE);
    }
    return result;
  }

  /**
     * Get the ancestors of a vertex.
     * 
     * @param vertex the vertex to get the ancestors of
     * @return {@link Set} of ancestors of a vertex
     */
  public Set<V> getAncestors(V vertex) {
    EdgeReversedGraph<V, E> reversedGraph = new EdgeReversedGraph<>(this);
    Iterator<V> iterator = new DepthFirstIterator<>(reversedGraph, vertex);
    Set<V> ancestors = new HashSet<>();
    if (iterator.hasNext()) {
      iterator.next();
    }
    iterator.forEachRemaining(ancestors::add);
    return ancestors;
  }

  /**
     * Get the descendants of a vertex.
     * 
     * @param vertex the vertex to get the descendants of
     * @return {@link Set} of descendants of a vertex
     */
  public Set<V> getDescendants(V vertex) {
    Iterator<V> iterator = new DepthFirstIterator<>(this, vertex);
    Set<V> descendants = new HashSet<>();
    if (iterator.hasNext()) {
      iterator.next();
    }
    iterator.forEachRemaining(descendants::add);
    return descendants;
  }

  /**
     * Returns a topological order iterator.
     * 
     * @return a topological order iterator
     */
  public Iterator<V> iterator() {
    return new TopoIterator();
  }

  /**
     * Update as if a new edge is added.
     * 
     * @param sourceVertex the source vertex
     * @param targetVertex the target vertex
     */
  private void updateDag(V sourceVertex, V targetVertex) throws CycleFoundException {
    Integer lb = topoOrderMap.getTopologicalIndex(targetVertex);
    Integer ub = topoOrderMap.getTopologicalIndex(sourceVertex);
    if (lb < ub) {
      Set<V> df = new HashSet<>();
      Set<V> db = new HashSet<>();
      Region affectedRegion = new Region(lb, ub);
      VisitedStrategy visited = visitedStrategyFactory.getVisitedStrategy(affectedRegion);
      dfsF(targetVertex, df, visited, affectedRegion);
      dfsB(sourceVertex, db, visited, affectedRegion);
      reorder(df, db, visited);
      ++topoModCount;
    }
  }

  /**
     * Depth first search forward, building up the set (df) of forward-connected vertices in the
     * Affected Region
     *
     * @param vertex the vertex being visited
     * @param df the set we are populating with forward connected vertices in the Affected Region
     * @param visited a simple data structure that lets us know if we already visited a node with a
     *        given topo index
     *
     * @throws CycleFoundException if a cycle is discovered
     */
  private void dfsF(V initialVertex, Set<V> df, VisitedStrategy visited, Region affectedRegion) throws CycleFoundException {
    Deque<V> vertices = new ArrayDeque<>();
    vertices.push(initialVertex);
    while (!vertices.isEmpty()) {
      V vertex = vertices.pop();
      int topoIndex = topoOrderMap.getTopologicalIndex(vertex);
      if (visited.getVisited(topoIndex)) {
        continue;
      }
      visited.setVisited(topoIndex);
      df.add(vertex);
      for (E outEdge : outgoingEdgesOf(vertex)) {
        V nextVertex = getEdgeTarget(outEdge);
        Integer nextVertexTopoIndex = topoOrderMap.getTopologicalIndex(nextVertex);
        if (nextVertexTopoIndex == affectedRegion.finish) {
          try {
            for (V visitedVertex : df) {
              visited.clearVisited(topoOrderMap.getTopologicalIndex(visitedVertex));
            }
          } catch (UnsupportedOperationException e) {
          }
          throw new CycleFoundException();
        }
        if (affectedRegion.isIn(nextVertexTopoIndex) && !visited.getVisited(nextVertexTopoIndex)) {
          vertices.push(nextVertex);
        }
      }
    }
  }

  /**
     * Depth first search backward, building up the set (db) of back-connected vertices in the
     * Affected Region
     *
     * @param vertex the vertex being visited
     * @param db the set we are populating with back-connected vertices in the AR
     * @param visited
     */
  private void dfsB(V initialVertex, Set<V> db, VisitedStrategy visited, Region affectedRegion) {
    Deque<V> vertices = new ArrayDeque<>();
    vertices.push(initialVertex);
    while (!vertices.isEmpty()) {
      V vertex = vertices.pop();
      int topoIndex = topoOrderMap.getTopologicalIndex(vertex);
      if (visited.getVisited(topoIndex)) {
        continue;
      }
      visited.setVisited(topoIndex);
      db.add(vertex);
      for (E inEdge : incomingEdgesOf(vertex)) {
        V previousVertex = getEdgeSource(inEdge);
        Integer previousVertexTopoIndex = topoOrderMap.getTopologicalIndex(previousVertex);
        if (affectedRegion.isIn(previousVertexTopoIndex) && !visited.getVisited(previousVertexTopoIndex)) {
          vertices.push(previousVertex);
        }
      }
    }
  }

  @SuppressWarnings(value = { "unchecked" }) private void reorder(Set<V> df, Set<V> db, VisitedStrategy visited) {
    List<V> topoDf = new ArrayList<>(df);
    List<V> topoDb = new ArrayList<>(db);
    topoDf.sort(topoComparator);
    topoDb.sort(topoComparator);
    SortedSet<Integer> availableTopoIndices = new TreeSet<>();
    V[] bigL = (V[]) new Object[df.size() + db.size()];
    int lIndex = 0;
    boolean clearVisited = true;
    for (V vertex : topoDb) {
      Integer topoIndex = topoOrderMap.getTopologicalIndex(vertex);
      availableTopoIndices.add(topoIndex);
      bigL[lIndex++] = vertex;
      if (clearVisited) {
        try {
          visited.clearVisited(topoIndex);
        } catch (UnsupportedOperationException e) {
          clearVisited = false;
        }
      }
    }
    for (V vertex : topoDf) {
      Integer topoIndex = topoOrderMap.getTopologicalIndex(vertex);
      availableTopoIndices.add(topoIndex);
      bigL[lIndex++] = vertex;
      if (clearVisited) {
        try {
          visited.clearVisited(topoIndex);
        } catch (UnsupportedOperationException e) {
          clearVisited = false;
        }
      }
    }
    lIndex = 0;
    for (Integer topoIndex : availableTopoIndices) {
      V vertex = bigL[lIndex++];
      topoOrderMap.putVertex(topoIndex, vertex);
    }
  }

  protected interface TopoOrderMap<V extends java.lang.Object> extends Serializable {
    /**
         * Add a vertex at the given topological index.
         *
         * @param index the topological index
         * @param vertex the vertex
         */
    void putVertex(Integer index, V vertex);

    /**
         * Get the vertex at the given topological index.
         *
         * @param index the topological index
         * @return vertex the vertex
         */
    V getVertex(Integer index);

    /**
         * Get the topological index of the given vertex.
         *
         * @param vertex the vertex
         * @return the index that the vertex is at, or null if the vertex isn't in the topological
         *         ordering
         */
    Integer getTopologicalIndex(V vertex);

    /**
         * Remove the given vertex from the topological ordering.
         *
         * @param vertex the vertex
         * @return the index that the vertex was at, or null if the vertex wasn't in the topological
         *         ordering
         */
    Integer removeVertex(V vertex);

    /**
         * Remove all vertices from the topological ordering.
         */
    void removeAllVertices();
  }

  protected interface VisitedStrategy {
    /**
         * Mark the given topological index as visited.
         *
         * @param index the topological index
         */
    void setVisited(int index);

    /**
         * Get if the given topological index has been visited.
         *
         * @param index the topological index
         * @return true if the given topological index has been visited, false otherwise
         */
    boolean getVisited(int index);

    /**
         * Clear the visited state of the given topological index.
         *
         * @param index the index
         * @throws UnsupportedOperationException if the implementation doesn't support (or doesn't
         *         need) clearance. For example, if the factory creates a new instance every time,
         *         it is a waste of cycles to reset the state after the search of the Affected
         *         Region is done, so an UnsupportedOperationException *should* be thrown.
         */
    void clearVisited(int index) throws UnsupportedOperationException;
  }

  protected interface VisitedStrategyFactory extends Serializable {
    /**
         * Create a new instance of {@link VisitedStrategy}.
         * 
         * @param affectedRegion the affected region
         * @return a new instance of {@link VisitedStrategy} for the affected region
         */
    VisitedStrategy getVisitedStrategy(Region affectedRegion);
  }

  protected static class TopoVertexBiMap<V extends java.lang.Object> implements TopoOrderMap<V> {
    private static final long serialVersionUID = 1L;

    private final Map<Integer, V> topoToVertex = new HashMap<>();

    private final Map<V, Integer> vertexToTopo = new HashMap<>();

    /**
         * Constructor
         */
    public TopoVertexBiMap() {
    }

    @Override public void putVertex(Integer index, V vertex) {
      topoToVertex.put(index, vertex);
      vertexToTopo.put(vertex, index);
    }

    @Override public V getVertex(Integer index) {
      return topoToVertex.get(index);
    }

    @Override public Integer getTopologicalIndex(V vertex) {
      return vertexToTopo.get(vertex);
    }

    @Override public Integer removeVertex(V vertex) {
      Integer topoIndex = vertexToTopo.remove(vertex);
      if (topoIndex != null) {
        topoToVertex.remove(topoIndex);
      }
      return topoIndex;
    }

    @Override public void removeAllVertices() {
      vertexToTopo.clear();
      topoToVertex.clear();
    }
  }

  protected class TopoVertexMap implements TopoOrderMap<V> {
    private static final long serialVersionUID = 1L;

    private final List<V> topoToVertex = new ArrayList<>();

    private final Map<V, Integer> vertexToTopo = new HashMap<>();

    /**
         * Constructor
         */
    public TopoVertexMap() {
    }

    @Override public void putVertex(Integer index, V vertex) {
      int translatedIndex = translateIndex(index);
      while ((translatedIndex + 1) > topoToVertex.size()) {
        topoToVertex.add(null);
      }
      topoToVertex.set(translatedIndex, vertex);
      vertexToTopo.put(vertex, index);
    }

    @Override public V getVertex(Integer index) {
      return topoToVertex.get(translateIndex(index));
    }

    @Override public Integer getTopologicalIndex(V vertex) {
      return vertexToTopo.get(vertex);
    }

    @Override public Integer removeVertex(V vertex) {
      Integer topoIndex = vertexToTopo.remove(vertex);
      if (topoIndex != null) {
        topoToVertex.set(translateIndex(topoIndex), null);
      }
      return topoIndex;
    }

    @Override public void removeAllVertices() {
      vertexToTopo.clear();
      topoToVertex.clear();
    }

    /**
         * We translate the topological index to an ArrayList index. We have to do this because
         * topological indices can be negative, and we want to do it because we can make better use
         * of space by only needing an ArrayList of size |AR|.
         *
         * @return the ArrayList index
         */
    private int translateIndex(int index) {
      if (index >= 0) {
        return 2 * index;
      }
      return -1 * ((index * 2) - 1);
    }
  }

  protected static class Region implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int start;

    private final int finish;

    /**
         * Construct a new region.
         * 
         * @param start the start of the region
         * @param finish the end of the region (inclusive)
         */
    public Region(int start, int finish) {
      if (start > finish) {
        throw new IllegalArgumentException("(start > finish): invariant broken");
      }
      this.start = start;
      this.finish = finish;
    }

    /**
         * Get the size of the region.
         * 
         * @return the size of the region
         */
    public int getSize() {
      return (finish - start) + 1;
    }

    /**
         * Check if index is in the region.
         * 
         * @param index the index to check
         * @return true if the index is in the region, false otherwise
         */
    public boolean isIn(int index) {
      return (index >= start) && (index <= finish);
    }

    /**
         * Get the start of the region.
         * 
         * @return the start of the region
         */
    public int getStart() {
      return start;
    }

    /**
         * Get the end of the region (inclusive).
         * 
         * @return the end of the region (inclusive)
         */
    public int getFinish() {
      return finish;
    }
  }

  protected static class VisitedBitSetImpl implements VisitedStrategy, VisitedStrategyFactory {
    private static final long serialVersionUID = 1L;

    private final BitSet visited = new BitSet();

    private Region affectedRegion;

    /**
         * Constructor
         */
    public VisitedBitSetImpl() {
    }

    @Override public VisitedStrategy getVisitedStrategy(Region affectedRegion) {
      this.affectedRegion = affectedRegion;
      return this;
    }

    @Override public void setVisited(int index) {
      visited.set(translateIndex(index), true);
    }

    @Override public boolean getVisited(int index) {
      return visited.get(translateIndex(index));
    }

    @Override public void clearVisited(int index) throws UnsupportedOperationException {
      visited.clear(translateIndex(index));
    }

    /**
         * We translate the topological index to an ArrayList index. We have to do this because
         * topological indices can be negative, and we want to do it because we can make better use
         * of space by only needing an ArrayList of size |AR|.
         *
         * @return the ArrayList index
         */
    private int translateIndex(int index) {
      return index - affectedRegion.start;
    }
  }

  protected static class VisitedArrayListImpl implements VisitedStrategy, VisitedStrategyFactory {
    private static final long serialVersionUID = 1L;

    private final List<Boolean> visited = new ArrayList<>();

    private Region affectedRegion;

    /**
         * Constructor
         */
    public VisitedArrayListImpl() {
    }

    @Override public VisitedStrategy getVisitedStrategy(Region affectedRegion) {
      int minSize = (affectedRegion.finish - affectedRegion.start) + 1;
      while (visited.size() < minSize) {
        visited.add(Boolean.FALSE);
      }
      this.affectedRegion = affectedRegion;
      return this;
    }

    @Override public void setVisited(int index) {
      visited.set(translateIndex(index), Boolean.TRUE);
    }

    @Override public boolean getVisited(int index) {
      return visited.get(translateIndex(index));
    }

    @Override public void clearVisited(int index) throws UnsupportedOperationException {
      visited.set(translateIndex(index), Boolean.FALSE);
    }

    /**
         * We translate the topological index to an ArrayList index. We have to do this because
         * topological indices can be negative, and we want to do it because we can make better use
         * of space by only needing an ArrayList of size |AR|.
         *
         * @return the ArrayList index
         */
    private int translateIndex(int index) {
      return index - affectedRegion.start;
    }
  }

  protected static class VisitedHashSetImpl implements VisitedStrategy, VisitedStrategyFactory {
    private static final long serialVersionUID = 1L;

    private final Set<Integer> visited = new HashSet<>();

    /**
         * Constructor
         */
    public VisitedHashSetImpl() {
    }

    @Override public VisitedStrategy getVisitedStrategy(Region affectedRegion) {
      visited.clear();
      return this;
    }

    @Override public void setVisited(int index) {
      visited.add(index);
    }

    @Override public boolean getVisited(int index) {
      return visited.contains(index);
    }

    @Override public void clearVisited(int index) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }
  }

  protected static class VisitedArrayImpl implements VisitedStrategy, VisitedStrategyFactory {
    private static final long serialVersionUID = 1L;

    private final boolean[] visited;

    private final Region region;

    /**
         * Constructs empty instance
         */
    public VisitedArrayImpl() {
      this(null);
    }

    /**
         * Construct an empty instance for a region.
         * 
         * @param region the region
         */
    public VisitedArrayImpl(Region region) {
      if (region == null) {
        this.visited = null;
        this.region = null;
      } else {
        this.region = region;
        visited = new boolean[region.getSize()];
      }
    }

    @Override public VisitedStrategy getVisitedStrategy(Region affectedRegion) {
      return new VisitedArrayImpl(affectedRegion);
    }

    @Override public void setVisited(int index) {
      visited[index - region.start] = true;
    }

    @Override public boolean getVisited(int index) {
      return visited[index - region.start];
    }

    @Override public void clearVisited(int index) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }
  }

  private static class CycleFoundException extends Exception {
    private static final long serialVersionUID = 5583471522212552754L;
  }

  private class TopoComparator implements Comparator<V>, Serializable {
    private static final long serialVersionUID = 8144905376266340066L;

    @Override public int compare(V o1, V o2) {
      return topoOrderMap.getTopologicalIndex(o1).compareTo(topoOrderMap.getTopologicalIndex(o2));
    }
  }

  private class TopoIterator implements Iterator<V> {
    private int currentTopoIndex;

    private final long expectedTopoModCount = topoModCount;

    private Integer nextIndex = null;

    public TopoIterator() {
      currentTopoIndex = minTopoIndex - 1;
    }

    @Override public boolean hasNext() {
      if (expectedTopoModCount != topoModCount) {
        throw new ConcurrentModificationException();
      }
      nextIndex = getNextIndex();
      return nextIndex != null;
    }

    @Override public V next() {
      if (expectedTopoModCount != topoModCount) {
        throw new ConcurrentModificationException();
      }
      if (nextIndex == null) {
        nextIndex = getNextIndex();
      }
      if (nextIndex == null) {
        throw new NoSuchElementException();
      }
      currentTopoIndex = nextIndex;
      nextIndex = null;
      return topoOrderMap.getVertex(currentTopoIndex);
    }

    @Override public void remove() {
      if (expectedTopoModCount != topoModCount) {
        throw new ConcurrentModificationException();
      }
      V vertexToRemove;
      if ((vertexToRemove = topoOrderMap.getVertex(currentTopoIndex)) != null) {
        topoOrderMap.removeVertex(vertexToRemove);
      } else {
        throw new IllegalStateException();
      }
    }

    private Integer getNextIndex() {
      for (int i = currentTopoIndex + 1; i <= maxTopoIndex; i++) {
        if (topoOrderMap.getVertex(i) != null) {
          return i;
        }
      }
      return null;
    }
  }
}