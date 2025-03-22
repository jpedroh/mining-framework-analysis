package org.jgrapht.traverse;
import org.jgrapht.Graph;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A breadth-first iterator for a directed or undirected graph.
 * 
 * <p>
 * For this iterator to work correctly the graph must not be modified during iteration. Currently
 * there are no means to ensure that, nor to fail-fast. The results of such modifications are
 * undefined.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Barak Naveh
 * @since Jul 19, 2003
 */
public class BreadthFirstIterator<V extends java.lang.Object, E extends java.lang.Object> extends CrossComponentIterator<V, E, Object> {
  private Deque<V> queue = new ArrayDeque<>();

  /**
     * Creates a new breadth-first iterator for the specified graph.
     *
     * @param g the graph to be iterated.
     */
  public BreadthFirstIterator(Graph<V, E> g) {
    this(g, 
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/eeb3686eb78c03308977f010b359938ed00d8e52/jgrapht-core/src/main/java/org/jgrapht/traverse/BreadthFirstIterator.java/left.java
    (V) null
=======
    g.vertexSet()
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/eeb3686eb78c03308977f010b359938ed00d8e52/jgrapht-core/src/main/java/org/jgrapht/traverse/BreadthFirstIterator.java/right.java
    );
  }

  /**
     * Creates a new breadth-first iterator for the specified graph. Iteration will start at the
     * specified start vertex and will be limited to the connected component that includes that
     * vertex. If the specified start vertex is <code>null</code>, iteration will start at an
     * arbitrary vertex and will not be limited, that is, will be able to traverse all the graph.
     *
     * @param g the graph to be iterated.
     * @param startVertex the vertex iteration to be started.
     */
  public BreadthFirstIterator(Graph<V, E> g, V startVertex) {
    super(g, startVertex);
  }

  /**
     * Creates a new breadth-first iterator for the specified graph. Iteration will start at the
     * specified start vertices and will be limited to the connected component that includes those
     * vertices. If the specified start vertices is <code>null</code>, iteration will start at an
     * arbitrary vertex and will not be limited, that is, will be able to traverse all the graph.
     *
     * @param g the graph to be iterated.
     * @param startVertices the vertices iteration to be started.
     */
  public BreadthFirstIterator(Graph<V, E> g, Iterable<V> startVertices) {
    super(g, startVertices);
  }

  /**
     * @see CrossComponentIterator#isConnectedComponentExhausted()
     */
  @Override protected boolean isConnectedComponentExhausted() {
    return queue.isEmpty();
  }

  /**
     * @see CrossComponentIterator#encounterVertex(Object, Object)
     */
  @Override protected void encounterVertex(V vertex, E edge) {
    putSeenData(vertex, null);
    queue.add(vertex);
  }

  /**
     * @see CrossComponentIterator#encounterVertexAgain(Object, Object)
     */
  @Override protected void encounterVertexAgain(V vertex, E edge) {
  }

  /**
     * @see CrossComponentIterator#provideNextVertex()
     */
  @Override protected V provideNextVertex() {
    return queue.removeFirst();
  }
}