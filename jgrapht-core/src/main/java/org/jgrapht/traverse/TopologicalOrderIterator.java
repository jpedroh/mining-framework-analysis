package org.jgrapht.traverse;
import java.util.*;
import org.jgrapht.*;
import org.jgrapht.util.*;

/**
 * A topological ordering iterator for a directed acyclic graph.
 * 
 * <p>
 * A topological order is a permutation <tt>p</tt> of the vertices of a graph such that an edge
 * <tt>(i,j)</tt> implies that <tt>i</tt> appears before <tt>j</tt> in <tt>p</tt>. For more
 * information see <a href="https://en.wikipedia.org/wiki/Topological_sorting">wikipedia</a> or
 * <a href="http://mathworld.wolfram.com/TopologicalSort.html">wolfram</a>.
 *
 * <p>
 * The iterator crosses components but does not track them, it only tracks visited vertices. The
 * iterator will detect (at some point) if the graph is not a directed acyclic graph and throw a
 * {@link IllegalArgumentException}.
 * 
 * <p>
 * For this iterator to work correctly the graph must not be modified during iteration. Currently
 * there are no means to ensure that, nor to fail-fast. The results of such modifications are
 * undefined.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Marden Neubert
 * @author Dimitrios Michail
 * @since December 2004
 */
public class TopologicalOrderIterator<V extends java.lang.Object, E extends java.lang.Object> extends AbstractGraphIterator<V, E> {
  private static final String GRAPH_IS_NOT_A_DAG = "Graph is not a DAG";

  private Queue<V> queue;

  private Map<V, ModifiableInteger> inDegreeMap;

  private int remainingVertices;

  private V cur;

  /**
     * Construct a topological order iterator.
     * 
     * <p>
     * Traversal will start at one of the graph's <i>sources</i>. See the definition of source at
     * <a href="http://mathworld.wolfram.com/Source.html">
     * http://mathworld.wolfram.com/Source.html</a>. In case of partial order, tie-breaking is
     * arbitrary.
     *
     * @param graph the directed graph to be iterated
     */
  public TopologicalOrderIterator(
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/left.java
  DirectedGraph<V, E> graph
=======
  Graph<V, E> dg
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/right.java
  ) {
    this(graph, (Comparator<V>) null);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * Creates a new topological order iterator over the directed graph specified, with a
     * user-supplied queue implementation to allow customized control over tie-breaking in case of
     * partial order. Traversal will start at one of the graph's <i>sources</i>. See the definition
     * of source at <a href="http://mathworld.wolfram.com/Source.html">
     * http://mathworld.wolfram.com/Source.html</a>.
     *
     * @param dg the directed graph to be iterated.
     * @param queue queue to use for tie-break in case of partial order (e.g. a PriorityQueue can be
     *        used to break ties according to vertex priority); must be initially empty
     */
  public TopologicalOrderIterator(Graph<V, E> dg, Queue<V> queue) {
    this(dg, queue, new HashMap<>());
  }
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/right.java


  /**
     * Construct a topological order iterator.
     * 
     * Creates a new topological order iterator over the directed graph specified, with a
     * user-supplied queue implementation to allow customized control over tie-breaking in case of
     * partial order. Traversal will start at one of the graph's <i>sources</i>. See the definition
     * of source at <a href="http://mathworld.wolfram.com/Source.html">
     * http://mathworld.wolfram.com/Source.html</a>.
     *
     * @param graph the directed graph to be iterated.
     * @param queue queue to use for tie-break in case of partial order (e.g. a PriorityQueue can be
     *        used to break ties according to vertex priority); must be initially empty
     * @deprecated in favor of {@link #TopologicalOrderIterator(DirectedGraph, Comparator)}
     */
  @Deprecated public TopologicalOrderIterator(
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/left.java
  DirectedGraph<V, E> graph
=======
  Graph<V, E> dg
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/right.java
  , Queue<V> queue) {
    super(graph);
    this.queue = Objects.requireNonNull(queue, "Queue must not be null");
    if (!queue.isEmpty()) {
      throw new IllegalArgumentException("Queue must be empty");
    }
    this.inDegreeMap = new HashMap<>();
    for (V v : graph.vertexSet()) {
      int d = 0;
      for (E e : specifics.incomingEdgesOf(v)) {
        V u = Graphs.getOppositeVertex(graph, e, v);
        if (v.equals(u)) {
          throw new IllegalArgumentException(GRAPH_IS_NOT_A_DAG);
        }
        d++;
      }
      inDegreeMap.put(v, new ModifiableInteger(d));
      if (d == 0) {
        queue.offer(v);
      }
    }
    this.remainingVertices = graph.vertexSet().size();
  }

  /**
     * Construct a topological order iterator.
     * 
     * <p>
     * Traversal will start at one of the graph's <i>sources</i>. See the definition of source at
     * <a href="http://mathworld.wolfram.com/Source.html">
     * http://mathworld.wolfram.com/Source.html</a>. In case of partial order, a comparator is used
     * to break ties.
     *
     * @param graph the directed graph to be iterated
     * @param comparator comparator in order to break ties in case of partial order
     */
  public TopologicalOrderIterator(
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/left.java
  DirectedGraph<V, E> graph
=======
  Graph<V, E> dg
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/right.java
  , Comparator<V> comparator) {
    super(graph);
    if (comparator == null) {
      this.queue = new LinkedList<>();
    } else {
      this.queue = new PriorityQueue<>(comparator);
    }
    this.inDegreeMap = new HashMap<>();
    for (V v : graph.vertexSet()) {
      int d = 0;
      for (E e : specifics.incomingEdgesOf(v)) {
        V u = Graphs.getOppositeVertex(graph, e, v);
        if (v.equals(u)) {
          throw new IllegalArgumentException(GRAPH_IS_NOT_A_DAG);
        }
        d++;
      }
      inDegreeMap.put(v, new ModifiableInteger(d));
      if (d == 0) {
        queue.offer(v);
      }
    }
    this.remainingVertices = graph.vertexSet().size();
  }

  /**
     * {@inheritDoc}
     * 
     * Always returns true since the iterator does not care about components.
     */
  @Override public boolean isCrossComponentTraversal() {
    return true;
  }

  /**
     * {@inheritDoc}
     * 
     * Trying to disable the cross components nature of this iterator will result into throwing a
     * {@link IllegalArgumentException}.
     */
  @Override public void setCrossComponentTraversal(boolean crossComponentTraversal) {
    if (!crossComponentTraversal) {
      throw new IllegalArgumentException("Iterator is always cross-component");
    }
  }

  @Override public boolean hasNext() {
    if (cur != null) {
      return true;
    }
    cur = advance();
    if (cur != null && nListeners != 0) {
      fireVertexTraversed(createVertexTraversalEvent(cur));
    }
    return cur != null;
  }

  @Override public V next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    V result = cur;
    cur = null;
    if (nListeners != 0) {
      fireVertexFinished(createVertexTraversalEvent(result));
    }
    return result;
  }

  private V advance() {
    V result = queue.poll();
    if (result != null) {
      for (E e : specifics.edgesOf(result)) {
        V other = Graphs.getOppositeVertex(graph, e, result);
        ModifiableInteger inDegree = inDegreeMap.get(other);
        if (inDegree.value > 0) {
          inDegree.value--;
          if (inDegree.value == 0) {
            queue.offer(other);
          }
        }
      }
      --remainingVertices;
    } else {
      if (remainingVertices > 0) {
        throw new IllegalArgumentException(GRAPH_IS_NOT_A_DAG);
      }
    }
    return result;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * Initializes the internal traversal object structure. Sets up the internal queue with the
     * directed graph vertices and creates the control structure for the in-degrees.
     *
     * @param dg the directed graph to be iterated.
     * @param queue initializer for queue
     * @param inDegreeMap initializer for inDegreeMap
     *
     * @return start vertex
     */
  private static <V extends java.lang.Object, E extends java.lang.Object> V initialize(Graph<V, E> dg, Queue<V> queue, Map<V, ModifiableInteger> inDegreeMap) {
    GraphTests.requireDirected(dg);
    for (V vertex : dg.vertexSet()) {
      int inDegree = dg.inDegreeOf(vertex);
      inDegreeMap.put(vertex, new ModifiableInteger(inDegree));
      if (inDegree == 0) {
        queue.offer(vertex);
      }
    }
    if (queue.isEmpty()) {
      return null;
    } else {
      return queue.peek();
    }
  }
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/right.java
}