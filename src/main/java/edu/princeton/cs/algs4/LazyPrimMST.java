package edu.princeton.cs.algs4;

/**
 *  The {@code LazyPrimMST} class represents a data type for computing a
 *  <em>minimum spanning tree</em> in an edge-weighted graph.
 *  The edge weights can be positive, zero, or negative and need not
 *  be distinct. If the graph is not connected, it computes a <em>minimum
 *  spanning forest</em>, which is the union of minimum spanning trees
 *  in each connected component. The {@code weight()} method returns the 
 *  weight of a minimum spanning tree and the {@code edges()} method
 *  returns its edges.
 *  <p>
 *  This implementation uses a lazy version of <em>Prim's algorithm</em>
 *  with a binary heap of edges.
 *  The constructor takes time proportional to <em>E</em> log <em>E</em>
 *  and extra space (not including the graph) proportional to <em>E</em>,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  Afterwards, the {@code weight()} method takes constant time
 *  and the {@code edges()} method takes time proportional to <em>V</em>.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *  For alternate implementations, see {@link PrimMST}, {@link KruskalMST},
 *  and {@link BoruvkaMST}.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class LazyPrimMST {
  private static final double FLOATING_POINT_EPSILON = 1E-12;

  private double weight;

  private Queue<Edge> mst;

  private boolean[] marked;

  private MinPQ<Edge> pq;

  /**
     * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
     * @param G the edge-weighted graph
     */
  public LazyPrimMST(EdgeWeightedGraph G) {
    mst = new Queue<Edge>();
    pq = new MinPQ<Edge>();
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++) {
      if (!marked[v]) {
        prim(G, v);
      }
    }
    assert check(G);
  }

  private void prim(EdgeWeightedGraph G, int s) {
    scan(G, s);
    while (!pq.isEmpty()) {
      Edge e = pq.delMin();
      int v = e.either(), w = e.other(v);
      assert marked[v] || marked[w];
      if (marked[v] && marked[w]) {
        continue;
      }
      mst.enqueue(e);
      weight += e.weight();
      if (!marked[v]) {
        scan(G, v);
      }
      if (!marked[w]) {
        scan(G, w);
      }
    }
  }

  private void scan(EdgeWeightedGraph G, int v) {
    assert !marked[v];
    marked[v] = true;
    for (Edge e : G.adj(v)) {
      if (!marked[e.other(v)]) {
        pq.insert(e);
      }
    }
  }

  /**
     * Returns the edges in a minimum spanning tree (or forest).
     * @return the edges in a minimum spanning tree (or forest) as
     *    an iterable of edges
     */
  public Iterable<Edge> edges() {
    return mst;
  }

  /**
     * Returns the sum of the edge weights in a minimum spanning tree (or forest).
     * @return the sum of the edge weights in a minimum spanning tree (or forest)
     */
  public double weight() {
    return weight;
  }

  private boolean check(EdgeWeightedGraph G) {
    double totalWeight = 0.0;
    for (Edge e : edges()) {
      totalWeight += e.weight();
    }
    if (Math.abs(totalWeight - weight()) > FLOATING_POINT_EPSILON) {
      System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n", totalWeight, weight());
      return false;
    }
    UF uf = new UF(G.V());
    for (Edge e : edges()) {
      int v = e.either(), w = e.other(v);
      if (uf.connected(v, w)) {
        System.err.println("Not a forest");
        return false;
      }
      uf.union(v, w);
    }
    for (Edge e : G.edges()) {
      int v = e.either(), w = e.other(v);
      if (!uf.connected(v, w)) {
        System.err.println("Not a spanning forest");
        return false;
      }
    }
    for (Edge e : edges()) {
      uf = new UF(G.V());
      for (Edge f : mst) {
        int x = f.either(), y = f.other(x);
        if (f != e) {
          uf.union(x, y);
        }
      }
      for (Edge f : G.edges()) {
        int x = f.either(), y = f.other(x);
        if (!uf.connected(x, y)) {
          if (f.weight() < e.weight()) {
            System.err.println("Edge " + f + " violates cut optimality conditions");
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
     * Unit tests the {@code LazyPrimMST} data type.
     */
  public static void main(String[] args) {
    In in = new In(args[0]);
    EdgeWeightedGraph G = new EdgeWeightedGraph(in);
    LazyPrimMST mst = new LazyPrimMST(G);
    for (Edge e : mst.edges()) {
      StdOut.println(e);
    }
    StdOut.printf("%.5f\n", mst.weight());
  }
}