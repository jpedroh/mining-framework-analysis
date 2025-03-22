package edu.princeton.cs.algs4;

/**
 *  The {@code BipartiteMatching} class represents a data type for computing a
 *  <em>maximum (cardinality) matching</em> and a
 *  <em>minimum (cardinality) vertex cover</em> in a bipartite graph.
 *  A <em>bipartite graph</em> in a graph whose vertices can be partitioned
 *  into two disjoint sets such that every edge has one endpoint in either set.
 *  A <em>matching</em> in a graph is a subset of its edges with no common
 *  vertices. A <em>maximum matching</em> is a matching with the maximum number
 *  of edges.
 *  A <em>perfect matching</em> is a matching which matches all vertices in the graph.
 *  A <em>vertex cover</em> in a graph is a subset of its vertices such that
 *  every edge is incident to at least one vertex. A <em>minimum vertex cover</em>
 *  is a vertex cover with the minimum number of vertices.
 *  By Konig's theorem, in any biparite
 *  graph, the maximum number of edges in matching equals the minimum number
 *  of vertices in a vertex cover.
 *  The maximum matching problem in <em>nonbipartite</em> graphs is
 *  also important, but all known algorithms for this more general problem
 *  are substantially more complicated.
 *  <p>
 *  This implementation uses the <em>alternating path algorithm</em>.
 *  It is equivalent to reducing to the maximum flow problem and running
 *  the augmenting path algorithm on the resulting flow network, but it
 *  does so with less overhead.
 *  The order of growth of the running time in the worst case is
 *  (<em>E</em> + <em>V</em>) <em>V</em>,
 *  where <em>E</em> is the number of edges and <em>V</em> is the number
 *  of vertices in the graph. It uses extra space (not including the graph)
 *  proportional to <em>V</em>.
 *  <p>
 *  See also {@link HopcroftKarp}, which solves the problem in  O(<em>E</em> sqrt(<em>V</em>))
 *  using the Hopcroft-Karp algorithm and
 *  <a href = "http://algs4.cs.princeton.edu/65reductions/BipartiteMatchingToMaxflow.java.html">BipartiteMatchingToMaxflow</a>,
 *  which solves the problem in O(<em>E V</em>) time via a reduction to maxflow.
 *  <p>
 *  For additional documentation, see
 *  <a href="http://algs4.cs.princeton.edu/65reductions">Section 6.5</a>
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class BipartiteMatching {
  private static final int UNMATCHED = -1;

  private final int V;

  private BipartiteX bipartition;

  private int cardinality;

  private int[] mate;

  private boolean[] inMinVertexCover;

  private boolean[] marked;

  private int[] edgeTo;

  /**
     * Determines a maximum matching (and a minimum vertex cover)
     * in a bipartite graph.
     *
     * @param  G the bipartite graph
     * @throws IllegalArgumentException if {@code G} is not bipartite
     */
  public BipartiteMatching(Graph G) {
    bipartition = new BipartiteX(G);
    if (!bipartition.isBipartite()) {
      throw new IllegalArgumentException("graph is not bipartite");
    }
    this.V = G.V();
    mate = new int[V];
    for (int v = 0; v < V; v++) {
      mate[v] = UNMATCHED;
    }
    while (hasAugmentingPath(G)) {
      int t = -1;
      for (int v = 0; v < G.V(); v++) {
        if (!isMatched(v) && edgeTo[v] != -1) {
          t = v;
          break;
        }
      }
      for (int v = t; v != -1; v = edgeTo[edgeTo[v]]) {
        int w = edgeTo[v];
        mate[v] = w;
        mate[w] = v;
      }
      cardinality++;
    }
    inMinVertexCover = new boolean[V];
    for (int v = 0; v < V; v++) {
      if (bipartition.color(v) && !marked[v]) {
        inMinVertexCover[v] = true;
      }
      if (!bipartition.color(v) && marked[v]) {
        inMinVertexCover[v] = true;
      }
    }
    assert certifySolution(G);
  }

  private boolean hasAugmentingPath(Graph G) {
    marked = new boolean[V];
    edgeTo = new int[V];
    for (int v = 0; v < V; v++) {
      edgeTo[v] = -1;
    }
    Queue<Integer> queue = new Queue<Integer>();
    for (int v = 0; v < V; v++) {
      if (bipartition.color(v) && !isMatched(v)) {
        queue.enqueue(v);
        marked[v] = true;
      }
    }
    while (!queue.isEmpty()) {
      int v = queue.dequeue();
      for (int w : G.adj(v)) {
        if (isResidualGraphEdge(v, w)) {
          if (!marked[w]) {
            edgeTo[w] = v;
            marked[w] = true;
            if (!isMatched(w)) {
              return true;
            }
            queue.enqueue(w);
          }
        }
      }
    }
    return false;
  }

  private boolean isResidualGraphEdge(int v, int w) {
    if ((mate[v] != w) && bipartition.color(v)) {
      return true;
    }
    if ((mate[v] == w) && !bipartition.color(v)) {
      return true;
    }
    return false;
  }

  /**
     * Returns the vertex to which the specified vertex is matched in
     * the maximum matching computed by the algorithm.
     *
     * @param  v the vertex
     * @return the vertex to which vertex {@code v} is matched in the
     *         maximum matching; {@code -1} if the vertex is not matched
     * @throws IllegalArgumentException unless {@code 0 &le; v &lt; V}
     *
     */
  public int mate(int v) {
    validate(v);
    return mate[v];
  }

  /**
     * Returns true if the specified vertex is matched in the maximum matching
     * computed by the algorithm.
     *
     * @param  v the vertex
     * @return {@code true} if vertex {@code v} is matched in maximum matching;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 &le; v &lt; V}
     *
     */
  public boolean isMatched(int v) {
    validate(v);
    return mate[v] != UNMATCHED;
  }

  /**
     * Returns the number of edges in a maximum matching.
     *
     * @return the number of edges in a maximum matching
     */
  public int size() {
    return cardinality;
  }

  /**
     * Returns true if the graph contains a perfect matching.
     * That is, the number of edges in a maximum matching is equal to one half
     * of the number of vertices in the graph (so that every vertex is matched).
     *
     * @return {@code true} if the graph contains a perfect matching;
     *         {@code false} otherwise
     */
  public boolean isPerfect() {
    return cardinality * 2 == V;
  }

  /**
     * Returns true if the specified vertex is in the minimum vertex cover
     * computed by the algorithm.
     *
     * @param  v the vertex
     * @return {@code true} if vertex {@code v} is in the minimum vertex cover;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 &le; v &lt; V}
     */
  public boolean inMinVertexCover(int v) {
    validate(v);
    return inMinVertexCover[v];
  }

  private void validate(int v) {
    if (v < 0 || v >= V) {
      throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V - 1));
    }
  }

  /**************************************************************************
     *
     *  The code below is solely for testing correctness of the data type.
     *
     **************************************************************************/
  private boolean certifySolution(Graph G) {
    for (int v = 0; v < V; v++) {
      if (mate(v) == -1) {
        continue;
      }
      if (mate(mate(v)) != v) {
        return false;
      }
    }
    int matchedVertices = 0;
    for (int v = 0; v < V; v++) {
      if (mate(v) != -1) {
        matchedVertices++;
      }
    }
    if (2 * size() != matchedVertices) {
      return false;
    }
    int sizeOfMinVertexCover = 0;
    for (int v = 0; v < V; v++) {
      if (inMinVertexCover(v)) {
        sizeOfMinVertexCover++;
      }
    }
    if (size() != sizeOfMinVertexCover) {
      return false;
    }
    boolean[] isMatched = new boolean[V];
    for (int v = 0; v < V; v++) {
      int w = mate[v];
      if (w == -1) {
        continue;
      }
      if (v == w) {
        return false;
      }
      if (v >= w) {
        continue;
      }
      if (isMatched[v] || isMatched[w]) {
        return false;
      }
      isMatched[v] = true;
      isMatched[w] = true;
    }
    for (int v = 0; v < V; v++) {
      if (mate(v) == -1) {
        continue;
      }
      boolean isEdge = false;
      for (int w : G.adj(v)) {
        if (mate(v) == w) {
          isEdge = true;
        }
      }
      if (!isEdge) {
        return false;
      }
    }
    for (int v = 0; v < V; v++) {
      for (int w : G.adj(v)) {
        if (!inMinVertexCover(v) && !inMinVertexCover(w)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
     * Unit tests the {@code HopcroftKarp} data type.
     * Takes three command-line arguments {@code V1}, {@code V2}, and {@code E};
     * creates a random bipartite graph with {@code V1} + {@code V2} vertices
     * and {@code E} edges; computes a maximum matching and minimum vertex cover;
     * and prints the results.
     */
  public static void main(String[] args) {
    int V1 = Integer.parseInt(args[0]);
    int V2 = Integer.parseInt(args[1]);
    int E = Integer.parseInt(args[2]);
    Graph G = GraphGenerator.bipartite(V1, V2, E);
    if (G.V() < 1000) {
      StdOut.println(G);
    }
    BipartiteMatching matching = new BipartiteMatching(G);
    StdOut.printf("Number of edges in max matching        = %d\n", matching.size());
    StdOut.printf("Number of vertices in min vertex cover = %d\n", matching.size());
    StdOut.printf("Graph has a perfect matching           = %b\n", matching.isPerfect());
    StdOut.println();
    if (G.V() >= 1000) {
      return;
    }
    StdOut.print("Max matching: ");
    for (int v = 0; v < G.V(); v++) {
      int w = matching.mate(v);
      if (matching.isMatched(v) && v < w) {
        StdOut.print(v + "-" + w + " ");
      }
    }
    StdOut.println();
    StdOut.print("Min vertex cover: ");
    for (int v = 0; v < G.V(); v++) {
      if (matching.inMinVertexCover(v)) {
        StdOut.print(v + " ");
      }
    }
    StdOut.println();
  }
}