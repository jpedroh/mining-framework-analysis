package edu.princeton.cs.algs4;

/**
 *  The {@code AcyclicLP} class represents a data type for solving the
 *  single-source longest paths problem in edge-weighted directed
 *  acyclic graphs (DAGs). The edge weights can be positive, negative, or zero.
 *  <p>
 *  This implementation uses a topological-sort based algorithm.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  Afterwards, the {@code distTo()} and {@code hasPathTo()} methods take
 *  constant time and the {@code pathTo()} method takes time proportional to the
 *  number of edges in the longest path returned.
 *  <p>
 *  For additional documentation,   
 *  see <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of   
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne. 
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class AcyclicLP {
  private double[] distTo;

  private DirectedEdge[] edgeTo;

  /**
     * Computes a longest paths tree from {@code s} to every other vertex in
     * the directed acyclic graph {@code G}.
     * @param G the acyclic digraph
     * @param s the source vertex
     * @throws IllegalArgumentException if the digraph is not acyclic
     * @throws IllegalArgumentException unless 0 &le; {@code s} &le; {@code V} - 1
     */
  public AcyclicLP(EdgeWeightedDigraph G, int s) {
    distTo = new double[G.V()];
    edgeTo = new DirectedEdge[G.V()];
    for (int v = 0; v < G.V(); v++) {
      distTo[v] = Double.NEGATIVE_INFINITY;
    }
    distTo[s] = 0.0;
    Topological topological = new Topological(G);
    if (!topological.hasOrder()) {
      throw new IllegalArgumentException("Digraph is not acyclic.");
    }
    for (int v : topological.order()) {
      for (DirectedEdge e : G.adj(v)) {
        relax(e);
      }
    }
  }

  private void relax(DirectedEdge e) {
    int v = e.from(), w = e.to();
    if (distTo[w] < distTo[v] + e.weight()) {
      distTo[w] = distTo[v] + e.weight();
      edgeTo[w] = e;
    }
  }

  /**
     * Returns the length of a longest path from the source vertex {@code s} to vertex {@code v}.
     * @param v the destination vertex
     * @return the length of a longest path from the source vertex {@code s} to vertex {@code v};
     *    {@code Double.NEGATIVE_INFINITY} if no such path
     */
  public double distTo(int v) {
    return distTo[v];
  }

  /**
     * Is there a path from the source vertex {@code s} to vertex {@code v}?
     * @param v the destination vertex
     * @return {@code true} if there is a path from the source vertex
     *    {@code s} to vertex {@code v}, and {@code false} otherwise
     */
  public boolean hasPathTo(int v) {
    return distTo[v] > Double.NEGATIVE_INFINITY;
  }

  /**
     * Returns a longest path from the source vertex {@code s} to vertex {@code v}.
     * @param v the destination vertex
     * @return a longest path from the source vertex {@code s} to vertex {@code v}
     *    as an iterable of edges, and {@code null} if no such path
     */
  public Iterable<DirectedEdge> pathTo(int v) {
    if (!hasPathTo(v)) {
      return null;
    }
    Stack<DirectedEdge> path = new Stack<DirectedEdge>();
    for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
      path.push(e);
    }
    return path;
  }

  /**
     * Unit tests the {@code AcyclicLP} data type.
     */
  public static void main(String[] args) {
    In in = new In(args[0]);
    int s = Integer.parseInt(args[1]);
    EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);
    AcyclicLP lp = new AcyclicLP(G, s);
    for (int v = 0; v < G.V(); v++) {
      if (lp.hasPathTo(v)) {
        StdOut.printf("%d to %d (%.2f)  ", s, v, lp.distTo(v));
        for (DirectedEdge e : lp.pathTo(v)) {
          StdOut.print(e + "   ");
        }
        StdOut.println();
      } else {
        StdOut.printf("%d to %d         no path\n", s, v);
      }
    }
  }
}