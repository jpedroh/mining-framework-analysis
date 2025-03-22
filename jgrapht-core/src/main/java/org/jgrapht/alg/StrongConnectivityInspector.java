package org.jgrapht.alg;
import java.util.*;
import org.jgrapht.*;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.graph.*;

/**
 * <p>Complements the {@link org.jgrapht.alg.ConnectivityInspector} class with
 * the capability to compute the strongly connected components of a directed
 * graph. The algorithm is implemented after "Cormen et al: Introduction to
 * agorithms", Chapter 22.5. It has a running time of O(V + E).</p>
 *
 * <p>Unlike {@link org.jgrapht.alg.ConnectivityInspector}, this class does not
 * implement incremental inspection. The full algorithm is executed at the first
 * call of {@link StrongConnectivityInspector#stronglyConnectedSets()} or {@link
 * StrongConnectivityInspector#isStronglyConnected()}.</p>
 *
 * @author Christian Soltenborn
 * @author Christian Hammer
 * @since Feb 2, 2005
 */
public class StrongConnectivityInspector<V extends java.lang.Object, E extends java.lang.Object> implements StrongConnectivityAlgorithm<V, E> {
  private final DirectedGraph<V, E> graph;

  private LinkedList<VertexData<V>> orderedVertices;

  private List<Set<V>> stronglyConnectedSets;

  private List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs;

  private Map<V, VertexData<V>> vertexToVertexData;

  /**
     * The constructor of the StrongConnectivityInspector class.
     *
     * @param directedGraph the graph to inspect
     *
     * @throws IllegalArgumentException
     */
  public StrongConnectivityInspector(DirectedGraph<V, E> directedGraph) {
    if (directedGraph == null) {
      throw new IllegalArgumentException("null not allowed for graph!");
    }
    graph = directedGraph;
    vertexToVertexData = null;
    orderedVertices = null;
    stronglyConnectedSets = null;
    stronglyConnectedSubgraphs = null;
  }

  /**
     * Returns the graph inspected by the StrongConnectivityInspector.
     *
     * @return the graph inspected by this StrongConnectivityInspector
     */
  public DirectedGraph<V, E> getGraph() {
    return graph;
  }

  /**
     * Returns true if the graph of this <code>
     * StronglyConnectivityInspector</code> instance is strongly connected.
     *
     * @return true if the graph is strongly connected, false otherwise
     */
  public boolean isStronglyConnected() {
    return stronglyConnectedSets().size() == 1;
  }

  /**
     * Computes a {@link List} of {@link Set}s, where each set contains vertices
     * which together form a strongly connected component within the given
     * graph.
     *
     * @return <code>List</code> of <code>Set</code> s containing the strongly
     * connected components
     */
  public List<Set<V>> stronglyConnectedSets() {
    if (stronglyConnectedSets == null) {
      orderedVertices = new LinkedList<VertexData<V>>();
      stronglyConnectedSets = new Vector<Set<V>>();
      createVertexData();
      for (VertexData<V> data : vertexToVertexData.values()) {
        if (!data.isDiscovered()) {
          dfsVisit(graph, data, null);
        }
      }
      DirectedGraph<V, E> inverseGraph = new EdgeReversedGraph<V, E>(graph);
      resetVertexData();
      for (VertexData<V> data : orderedVertices) {
        if (!data.isDiscovered()) {
          Set<V> set = new HashSet<V>();
          stronglyConnectedSets.add(set);
          dfsVisit(inverseGraph, data, set);
        }
      }
      orderedVertices = null;
      vertexToVertexData = null;
    }
    return stronglyConnectedSets;
  }

  /**
     * <p>Computes a list of {@link DirectedSubgraph}s of the given graph. Each
     * subgraph will represent a strongly connected component and will contain
     * all vertices of that component. The subgraph will have an edge (u,v) iff
     * u and v are contained in the strongly connected component.</p>
     *
     * <p>NOTE: Calling this method will first execute {@link
     * StrongConnectivityInspector#stronglyConnectedSets()}. If you don't need
     * subgraphs, use that method.</p>
     *
     * @return a list of subgraphs representing the strongly connected
     * components
     */
  public List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs() {
    if (stronglyConnectedSubgraphs == null) {
      List<Set<V>> sets = stronglyConnectedSets();
      stronglyConnectedSubgraphs = new Vector<DirectedSubgraph<V, E>>(sets.size());
      for (Set<V> set : sets) {
        stronglyConnectedSubgraphs.add(new DirectedSubgraph<V, E>(graph, set, null));
      }
    }
    return stronglyConnectedSubgraphs;
  }

  private void createVertexData() {
    vertexToVertexData = new HashMap<V, VertexData<V>>(graph.vertexSet().size());
    for (V vertex : graph.vertexSet()) {
      vertexToVertexData.put(vertex, new VertexData2<V>(vertex, false, false));
    }
  }

  private void dfsVisit(DirectedGraph<V, E> visitedGraph, VertexData<V> vertexData, Set<V> vertices) {
    Deque<VertexData<V>> stack = new ArrayDeque<VertexData<V>>();
    stack.add(vertexData);
    while (!stack.isEmpty()) {
      VertexData<V> data = stack.removeLast();
      if (!data.isDiscovered()) {
        data.setDiscovered(true);
        if (vertices != null) {
          vertices.add(data.getVertex());
        }
        stack.add(new VertexData1<V>(data, true, true));
        for (E edge : visitedGraph.outgoingEdgesOf(data.getVertex())) {
          VertexData<V> targetData = vertexToVertexData.get(visitedGraph.getEdgeTarget(edge));
          if (!targetData.isDiscovered()) {
            stack.add(targetData);
          }
        }
      } else {
        if (data.isFinished()) {
          if (vertices == null) {
            orderedVertices.addFirst(data.getFinishedData());
          }
        }
      }
    }
  }

  private void resetVertexData() {
    for (VertexData<V> data : vertexToVertexData.values()) {
      data.setDiscovered(false);
      data.setFinished(false);
    }
  }

  private static abstract class VertexData<V extends java.lang.Object> {
    private byte bitfield;

    private VertexData(boolean discovered, boolean finished) {
      this.bitfield = 0;
      setDiscovered(discovered);
      setFinished(finished);
    }

    private boolean isDiscovered() {
      if ((bitfield & 1) == 1) {
        return true;
      }
      return false;
    }

    private boolean isFinished() {
      if ((bitfield & 2) == 2) {
        return true;
      }
      return false;
    }

    private void setDiscovered(boolean discovered) {
      if (discovered) {
        bitfield |= 1;
      } else {
        bitfield &= ~1;
      }
    }

    private void setFinished(boolean finished) {
      if (finished) {
        bitfield |= 2;
      } else {
        bitfield &= ~2;
      }
    }

    abstract VertexData<V> getFinishedData();

    abstract V getVertex();
  }

  private static final class VertexData1<V extends java.lang.Object> extends VertexData<V> {
    private final VertexData<V> finishedData;

    private VertexData1(VertexData<V> finishedData, boolean discovered, boolean finished) {
      super(discovered, finished);
      this.finishedData = finishedData;
    }

    VertexData<V> getFinishedData() {
      return finishedData;
    }

    V getVertex() {
      return null;
    }
  }

  private static final class VertexData2<V extends java.lang.Object> extends VertexData<V> {
    private final V vertex;

    private VertexData2(V vertex, boolean discovered, boolean finished) {
      super(discovered, finished);
      this.vertex = vertex;
    }

    VertexData<V> getFinishedData() {
      return null;
    }

    V getVertex() {
      return vertex;
    }
  }
}