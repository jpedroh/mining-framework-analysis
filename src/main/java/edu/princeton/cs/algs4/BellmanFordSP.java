package edu.princeton.cs.algs4;

/**
 *  The {@code BellmanFordSP} class represents a data type for solving the
 *  single-source shortest paths problem in edge-weighted digraphs with
 *  no negative cycles. 
 *  The edge weights can be positive, negative, or zero.
 *  This class finds either a shortest path from the source vertex <em>s</em>
 *  to every other vertex or a negative cycle reachable from the source vertex.
 *  <p>
 *  This implementation uses the Bellman-Ford-Moore algorithm.
 *  The constructor takes time proportional to <em>V</em> (<em>V</em> + <em>E</em>)
 *  in the worst case, where <em>V</em> is the number of vertices and <em>E</em>
 *  is the number of edges.
 *  Afterwards, the {@code distTo()}, {@code hasPathTo()}, and {@code hasNegativeCycle()}
 *  methods take constant time; the {@code pathTo()} and {@code negativeCycle()}
 *  method takes time proportional to the number of edges returned.
 *  <p>
 *  For additional documentation,    
 *  see <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of    
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne. 
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class BellmanFordSP {
  private double[] distTo;

  private DirectedEdge[] edgeTo;

  private boolean[] onQueue;

  private Queue<Integer> queue;

  private int cost;

  private Iterable<DirectedEdge> cycle;

  /**
     * Computes a shortest paths tree from {@code s} to every other vertex in
     * the edge-weighted digraph {@code G}.
     * @param G the acyclic digraph
     * @param s the source vertex
     * @throws IllegalArgumentException unless 0 &le; {@code s} &le; {@code V} - 1
     */
  public BellmanFordSP(EdgeWeightedDigraph G, int s) {
    distTo = new double[G.V()];
    edgeTo = new DirectedEdge[G.V()];
    onQueue = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++) {
      distTo[v] = Double.POSITIVE_INFINITY;
    }
    distTo[s] = 0.0;
    queue = new Queue<Integer>();
    queue.enqueue(s);
    onQueue[s] = true;
    while (!queue.isEmpty() && !hasNegativeCycle()) {
      int v = queue.dequeue();
      onQueue[v] = false;
      relax(G, v);
    }
    assert check(G, s);
  }

  private void relax(EdgeWeightedDigraph G, int v) {
    for (DirectedEdge e : G.adj(v)) {
      int w = e.to();
      if (distTo[w] > distTo[v] + e.weight()) {
        distTo[w] = distTo[v] + e.weight();
        edgeTo[w] = e;
        if (!onQueue[w]) {
          queue.enqueue(w);
          onQueue[w] = true;
        }
      }
      if (cost++ % G.V() == 0) {
        findNegativeCycle();
        if (hasNegativeCycle()) {
          return;
        }
      }
    }
  }

  /**
     * Is there a negative cycle reachable from the source vertex {@code s}?
     * @return {@code true} if there is a negative cycle reachable from the
     *    source vertex {@code s}, and {@code false} otherwise
     */
  public boolean hasNegativeCycle() {
    return cycle != null;
  }

  /**
     * Returns a negative cycle reachable from the source vertex {@code s}, or {@code null}
     * if there is no such cycle.
     * @return a negative cycle reachable from the soruce vertex {@code s} 
     *    as an iterable of edges, and {@code null} if there is no such cycle
     */
  public Iterable<DirectedEdge> negativeCycle() {
    return cycle;
  }

  private void findNegativeCycle() {
    int V = edgeTo.length;
    EdgeWeightedDigraph spt = new EdgeWeightedDigraph(V);
    for (int v = 0; v < V; v++) {
      if (edgeTo[v] != null) {
        spt.addEdge(edgeTo[v]);
      }
    }
    EdgeWeightedDirectedCycle finder = new EdgeWeightedDirectedCycle(spt);
    cycle = finder.cycle();
  }

  /**
     * Returns the length of a shortest path from the source vertex {@code s} to vertex {@code v}.
     * @param v the destination vertex
     * @return the length of a shortest path from the source vertex {@code s} to vertex {@code v};
     *    {@code Double.POSITIVE_INFINITY} if no such path
     * @throws UnsupportedOperationException if there is a negative cost cycle reachable
     *    from the source vertex {@code s}
     */
  public double distTo(int v) {
    if (hasNegativeCycle()) {
      throw new UnsupportedOperationException("Negative cost cycle exists");
    }
    return distTo[v];
  }

  /**
     * Is there a path from the source {@code s} to vertex {@code v}?
     * @param v the destination vertex
     * @return {@code true} if there is a path from the source vertex
     *    {@code s} to vertex {@code v}, and {@code false} otherwise
     */
  public boolean hasPathTo(int v) {
    return distTo[v] < Double.POSITIVE_INFINITY;
  }

  /**
     * Returns a shortest path from the source {@code s} to vertex {@code v}.
     * @param v the destination vertex
     * @return a shortest path from the source {@code s} to vertex {@code v}
     *    as an iterable of edges, and {@code null} if no such path
     * @throws UnsupportedOperationException if there is a negative cost cycle reachable
     *    from the source vertex {@code s}
     */
  public Iterable<DirectedEdge> pathTo(int v) {
    if (hasNegativeCycle()) {
      throw new UnsupportedOperationException("Negative cost cycle exists");
    }
    if (!hasPathTo(v)) {
      return null;
    }
    Stack<DirectedEdge> path = new Stack<DirectedEdge>();
    for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
      path.push(e);
    }
    return path;
  }

  private boolean check(EdgeWeightedDigraph G, int s) {
    if (hasNegativeCycle()) {
      double weight = 0.0;
      for (DirectedEdge e : negativeCycle()) {
        weight += e.weight();
      }
      if (weight >= 0.0) {
        System.err.println("error: weight of negative cycle = " + weight);
        return false;
      }
    } else {
      if (distTo[s] != 0.0 || edgeTo[s] != null) {
        System.err.println("distanceTo[s] and edgeTo[s] inconsistent");
        return false;
      }
      for (int v = 0; v < G.V(); v++) {
        if (v == s) {
          continue;
        }
        if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
          System.err.println("distTo[] and edgeTo[] inconsistent");
          return false;
        }
      }
      for (int v = 0; v < G.V(); v++) {
        for (DirectedEdge e : G.adj(v)) {
          int w = e.to();
          if (distTo[v] + e.weight() < distTo[w]) {
            System.err.println("edge " + e + " not relaxed");
            return false;
          }
        }
      }
      for (int w = 0; w < G.V(); w++) {
        if (edgeTo[w] == null) {
          continue;
        }
        DirectedEdge e = edgeTo[w];
        int v = e.from();
        if (w != e.to()) {
          return false;
        }
        if (distTo[v] + e.weight() != distTo[w]) {
          System.err.println("edge " + e + " on shortest path not tight");
          return false;
        }
      }
    }
    StdOut.println("Satisfies optimality conditions");
    StdOut.println();
    return true;
  }

  /**
     * Unit tests the {@code BellmanFordSP} data type.
     */
  public static void main(String[] args) {
    In in = new In(args[0]);
    int s = Integer.parseInt(args[1]);
    EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);
    BellmanFordSP sp = new BellmanFordSP(G, s);
    if (sp.hasNegativeCycle()) {
      for (DirectedEdge e : sp.negativeCycle()) {
        StdOut.println(e);
      }
    } else {
      for (int v = 0; v < G.V(); v++) {
        if (sp.hasPathTo(v)) {
          StdOut.printf("%d to %d (%5.2f)  ", s, v, sp.distTo(v));
          for (DirectedEdge e : sp.pathTo(v)) {
            StdOut.print(e + "   ");
          }
          StdOut.println();
        } else {
          StdOut.printf("%d to %d           no path\n", s, v);
        }
      }
    }
  }
}