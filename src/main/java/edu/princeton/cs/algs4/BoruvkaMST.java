package edu.princeton.cs.algs4;

/**
 *  The {@code BoruvkaMST} class represents a data type for computing a
 *  <em>minimum spanning tree</em> in an edge-weighted graph.
 *  The edge weights can be positive, zero, or negative and need not
 *  be distinct. If the graph is not connected, it computes a <em>minimum
 *  spanning forest</em>, which is the union of minimum spanning trees
 *  in each connected component. The {@code weight()} method returns the 
 *  weight of a minimum spanning tree and the {@code edges()} method
 *  returns its edges.
 *  <p>
 *  This implementation uses <em>Boruvka's algorithm</em> and the union-find
 *  data type.
 *  The constructor takes time proportional to <em>E</em> log <em>V</em>
 *  and extra space (not including the graph) proportional to <em>V</em>,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  Afterwards, the {@code weight()} method takes constant time
 *  and the {@code edges()} method takes time proportional to <em>V</em>.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *  For alternate implementations, see {@link LazyPrimMST}, {@link PrimMST},
 *  and {@link KruskalMST}.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class BoruvkaMST {
  private static final double FLOATING_POINT_EPSILON = 1E-12;

  private Bag<Edge> mst = new Bag<Edge>();

  private double weight;

  /**
     * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
     * @param G the edge-weighted graph
     */
  public BoruvkaMST(EdgeWeightedGraph G) {
    UF uf = new UF(G.V());
    for (int t = 1; t < G.V() && mst.size() < G.V() - 1; t = t + t) {
      Edge[] closest = new Edge[G.V()];
      for (Edge e : G.edges()) {
        int v = e.either(), w = e.other(v);
        int i = uf.find(v), j = uf.find(w);
        if (i == j) {
          continue;
        }
        if (closest[i] == null || less(e, closest[i])) {
          closest[i] = e;
        }
        if (closest[j] == null || less(e, closest[j])) {
          closest[j] = e;
        }
      }
      for (int i = 0; i < G.V(); i++) {
        Edge e = closest[i];
        if (e != null) {
          int v = e.either(), w = e.other(v);
          if (!uf.connected(v, w)) {
            mst.add(e);
            weight += e.weight();
            uf.union(v, w);
          }
        }
      }
    }
    assert check(G);
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

  private static boolean less(Edge e, Edge f) {
    return e.weight() < f.weight();
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
     * Unit tests the {@code BoruvkaMST} data type.
     */
  public static void main(String[] args) {
    In in = new In(args[0]);
    EdgeWeightedGraph G = new EdgeWeightedGraph(in);
    BoruvkaMST mst = new BoruvkaMST(G);
    for (Edge e : mst.edges()) {
      StdOut.println(e);
    }
    StdOut.printf("%.5f\n", mst.weight());
  }
}