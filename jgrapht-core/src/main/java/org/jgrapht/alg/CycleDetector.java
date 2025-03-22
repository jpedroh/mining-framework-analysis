package org.jgrapht.alg;
import java.util.*;
import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.traverse.*;

/**
 * Performs cycle detection on a graph. The <i>inspected graph</i> is specified at construction time
 * and cannot be modified. Currently, the detector supports only directed graphs.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author John V. Sichi
 * @since Sept 16, 2004
 */
public class CycleDetector<V extends java.lang.Object, E extends java.lang.Object> {
  /**
     * Graph on which cycle detection is being performed.
     */
  private Graph<V, E> graph;

  /**
     * Creates a cycle detector for the specified graph. Currently only directed graphs are
     * supported.
     *
     * @param graph the DirectedGraph in which to detect cycles
     */
  public CycleDetector(Graph<V, E> graph) {
    this.graph = 
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/alg/CycleDetector.java/left.java
    Objects.requireNonNull(graph, "Graph cannot be null")
=======
    GraphTests.requireDirected(graph, "Graph must be directed")
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/alg/CycleDetector.java/right.java
    ;
  }

  /**
     * Performs yes/no cycle detection on the entire graph.
     *
     * @return true iff the graph contains at least one cycle
     */
  public boolean detectCycles() {
    try {
      execute(null, null);
    } catch (CycleDetectedException ex) {
      return true;
    }
    return false;
  }

  /**
     * Performs yes/no cycle detection on an individual vertex.
     *
     * @param v the vertex to test
     *
     * @return true if v is on at least one cycle
     */
  public boolean detectCyclesContainingVertex(V v) {
    try {
      execute(null, v);
    } catch (CycleDetectedException ex) {
      return true;
    }
    return false;
  }

  /**
     * Finds the vertex set for the subgraph of all cycles.
     *
     * @return set of all vertices which participate in at least one cycle in this graph
     */
  public Set<V> findCycles() {
    StrongConnectivityAlgorithm<V, E> inspector = new KosarajuStrongConnectivityInspector<>(graph);
    List<Set<V>> components = inspector.stronglyConnectedSets();
    Set<V> set = new LinkedHashSet<>();
    for (Set<V> component : components) {
      if (component.size() > 1) {
        set.addAll(component);
      } else {
        V v = component.iterator().next();
        if (graph.containsEdge(v, v)) {
          set.add(v);
        }
      }
    }
    return set;
  }

  /**
     * Finds the vertex set for the subgraph of all cycles which contain a particular vertex.
     *
     * <p>
     * REVIEW jvs 25-Aug-2006: This implementation is not guaranteed to cover all cases. If you want
     * to be absolutely certain that you report vertices from all cycles containing v, it's safer
     * (but less efficient) to use StrongConnectivityAlgorithm instead and return the strongly
     * connected component containing v.
     *
     * @param v the vertex to test
     *
     * @return set of all vertices reachable from v via at least one cycle
     */
  public Set<V> findCyclesContainingVertex(V v) {
    Set<V> set = new LinkedHashSet<>();
    execute(set, v);
    return set;
  }

  private void execute(Set<V> s, V v) {
    ProbeIterator<V, E> iter = new ProbeIterator<>(graph, s, v);
    while (iter.hasNext()) {
      iter.next();
    }
  }

  private static class CycleDetectedException extends RuntimeException {
    private static final long serialVersionUID = 3834305137802950712L;
  }

  private static class ProbeIterator<V extends java.lang.Object, E extends java.lang.Object> extends DepthFirstIterator<V, E> {
    private List<V> path;

    private Set<V> cycleSet;

    private V root;

    ProbeIterator(Graph<V, E> graph, Set<V> cycleSet, V startVertex) {
      super(graph, startVertex);
      this.path = new ArrayList<>();
      this.cycleSet = cycleSet;
      this.root = startVertex;
    }

    /**
         * {@inheritDoc}
         */
    @Override protected void encounterVertexAgain(V vertex, E edge) {
      super.encounterVertexAgain(vertex, edge);
      int i;
      if (root != null) {
        if (vertex.equals(root)) {
          i = 0;
        } else {
          if ((cycleSet != null) && cycleSet.contains(vertex)) {
            i = 0;
          } else {
            return;
          }
        }
      } else {
        i = path.indexOf(vertex);
      }
      if (i > -1) {
        if (cycleSet == null) {
          throw new CycleDetectedException();
        } else {
          for ( ; i < path.size(); ++i) {
            cycleSet.add(path.get(i));
          }
        }
      }
    }

    /**
         * {@inheritDoc}
         */
    @Override protected V provideNextVertex() {
      V v = super.provideNextVertex();
      for (int i = path.size() - 1; i >= 0; --i) {
        if (graph.containsEdge(path.get(i), v)) {
          break;
        }
        path.remove(i);
      }
      path.add(v);
      return v;
    }
  }
}