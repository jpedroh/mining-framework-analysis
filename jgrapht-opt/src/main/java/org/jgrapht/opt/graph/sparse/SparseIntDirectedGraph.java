package org.jgrapht.opt.graph.sparse;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.opt.graph.sparse.specifics.AbstractSparseSpecificsGraph;
import org.jgrapht.opt.graph.sparse.specifics.IncomingNoReindexSparseDirectedSpecifics;
import org.jgrapht.opt.graph.sparse.specifics.NoIncomingNoReindexSparseDirectedSpecifics;
import org.jgrapht.opt.graph.sparse.specifics.SparseGraphSpecifics;

/**
 * A sparse directed graph.
 *
 * <p>
 * Assuming the graph has $n$ vertices, the vertices are numbered from $0$ to $n-1$. Similarly,
 * edges are numbered from $0$ to $m-1$ where $m$ is the total number of edges.
 * 
 * <p>
 * It stores two boolean incidence matrix of the graph (rows are vertices and columns are edges) as
 * Compressed Sparse Rows (CSR). Constant time source and target lookups are provided by storing the
 * edge lists in arrays. This is a classic format for write-once read-many use cases. Thus, the
 * graph is unmodifiable.
 * 
 * <p>
 * The question of whether a sparse or dense representation is more appropriate is highly dependent
 * on various factors such as the graph, the machine running the algorithm and the algorithm itself.
 * Wilkinson defined a matrix as "sparse" if it has enough zeros that it pays to take advantage of
 * them. For more details see
 * <ul>
 * <li>Wilkinson, J. H. 1971. Linear algebra; part II: the algebraic eigenvalue problem. In Handbook
 * for Automatic Computation, J. H. Wilkinson and C. Reinsch, Eds. Vol. 2. Springer-Verlag, Berlin,
 * New York.</li>
 * </ul>
 * 
 * Additional information about sparse representations can be found in the
 * <a href="https://en.wikipedia.org/wiki/Sparse_matrix">wikipedia</a>.
 * 
 * @author Dimitrios Michail
 */
public class SparseIntDirectedGraph extends AbstractSparseSpecificsGraph<SparseGraphSpecifics> {
  protected static final String UNMODIFIABLE = "this graph is unmodifiable";

  /**
     * Create a new graph from an edge list.
     * 
     * @param numVertices the number of vertices
     * @param edges the edge list
     */
  public SparseIntDirectedGraph(int numVertices, List<Pair<Integer, Integer>> edges) {
    this(numVertices, edges.size(), () -> edges.stream(), IncomingEdgesSupport.FULL_INCOMING_EDGES);
  }

  /**
     * Create a new graph from an edge list.
     * 
     * @param numVertices the number of vertices
     * @param edges the edge list
     * @param incomingEdgesSupport whether to support incoming edges or not
     */
  public SparseIntDirectedGraph(int numVertices, List<Pair<Integer, Integer>> edges, IncomingEdgesSupport incomingEdgesSupport) {
    this(numVertices, edges.size(), () -> edges.stream(), incomingEdgesSupport);
  }

  /**
     * Create a new graph from an edge stream.
     * 
     * @param numVertices the number of vertices
     * @param numEdges the number of edges
     * @param edges the edge stream
     * @param incomingEdgesSupport whether to support incoming edges or not
     */
  public SparseIntDirectedGraph(int numVertices, int numEdges, Supplier<Stream<Pair<Integer, Integer>>> edges, IncomingEdgesSupport incomingEdgesSupport) {
    super(() -> {
      switch (incomingEdgesSupport) {
        case FULL_INCOMING_EDGES:
        return new IncomingNoReindexSparseDirectedSpecifics(numVertices, numEdges, edges, false);
        case LAZY_INCOMING_EDGES:
        return new IncomingNoReindexSparseDirectedSpecifics(numVertices, numEdges, edges, true);
        case NO_INCOMING_EDGES:
        default:
        return new NoIncomingNoReindexSparseDirectedSpecifics(numVertices, numEdges, edges);
      }
    });
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * {@inheritDoc}
     * 
     * This operation costs $O(d)$ where $d$ is the out-degree of the source vertex.
     */
  @Override public Integer getEdge(Integer sourceVertex, Integer targetVertex) {
    if (sourceVertex < 0 || sourceVertex >= outIncidenceMatrix.rows()) {
      return null;
    }
    if (targetVertex < 0 || targetVertex >= outIncidenceMatrix.rows()) {
      return null;
    }
    Iterator<Integer> it = outIncidenceMatrix.nonZerosPositionIterator(sourceVertex);
    while (it.hasNext()) {
      int eId = it.next();
      if (getEdgeTarget(eId).equals(targetVertex)) {
        return eId;
      }
    }
    return null;
  }
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/1c5fff14bb909af58ffd1c1231584e0f8caea5a2/jgrapht-opt/src/main/java/org/jgrapht/opt/graph/sparse/SparseIntDirectedGraph.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * {@inheritDoc}
     * 
     * This operation costs $O(d)$ where $d$ is the out-degree of the source vertex.
     */
  @Override public Set<Integer> getAllEdges(Integer sourceVertex, Integer targetVertex) {
    if (sourceVertex < 0 || sourceVertex >= outIncidenceMatrix.rows()) {
      return null;
    }
    if (targetVertex < 0 || targetVertex >= outIncidenceMatrix.rows()) {
      return null;
    }
    Set<Integer> result = new LinkedHashSet<>();
    Iterator<Integer> it = outIncidenceMatrix.nonZerosPositionIterator(sourceVertex);
    while (it.hasNext()) {
      int eId = it.next();
      if (getEdgeTarget(eId).equals(targetVertex)) {
        result.add(eId);
      }
    }
    return result;
  }
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/1c5fff14bb909af58ffd1c1231584e0f8caea5a2/jgrapht-opt/src/main/java/org/jgrapht/opt/graph/sparse/SparseIntDirectedGraph.java/right.java
}