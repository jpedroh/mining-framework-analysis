package edu.princeton.cs.algs4;

/**
 *  The {@code TransitiveClosure} class represents a data type for 
 *  computing the transitive closure of a digraph.
 *  <p>
 *  This implementation runs depth-first search from each vertex.
 *  The constructor takes time proportional to <em>V</em>(<em>V</em> + <em>E</em>)
 *  (in the worst case) and uses space proportional to <em>V</em><sup>2</sup>,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  <p>
 *  For large digraphs, you may want to consider a more sophisticated algorithm.
 *  <a href = "http://www.cs.hut.fi/~enu/thesis.html">Nuutila</a> proposes two
 *  algorithm for the problem (based on strong components and an interval representation)
 *  that runs in <em>E</em> + <em>V</em> time on typical digraphs.
 *
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class TransitiveClosure {
  private DirectedDFS[] tc;

  /**
     * Computes the transitive closure of the digraph {@code G}.
     * @param G the digraph
     */
  public TransitiveClosure(Digraph G) {
    tc = new DirectedDFS[G.V()];
    for (int v = 0; v < G.V(); v++) {
      tc[v] = new DirectedDFS(G, v);
    }
  }

  /**
     * Is there a directed path from vertex {@code v} to vertex {@code w} in the digraph?
     * @param v the source vertex
     * @param w the target vertex
     * @return {@code true} if there is a directed path from {@code v} to {@code w},
     *    {@code false} otherwise
     */
  public boolean reachable(int v, int w) {
    return tc[v].marked(w);
  }

  /**
     * Unit tests the {@code TransitiveClosure} data type.
     */
  public static void main(String[] args) {
    In in = new In(args[0]);
    Digraph G = new Digraph(in);
    TransitiveClosure tc = new TransitiveClosure(G);
    StdOut.print("     ");
    for (int v = 0; v < G.V(); v++) {
      StdOut.printf("%3d", v);
    }
    StdOut.println();
    StdOut.println("--------------------------------------------");
    for (int v = 0; v < G.V(); v++) {
      StdOut.printf("%3d: ", v);
      for (int w = 0; w < G.V(); w++) {
        if (tc.reachable(v, w)) {
          StdOut.printf("  T");
        } else {
          StdOut.printf("   ");
        }
      }
      StdOut.println();
    }
  }
}