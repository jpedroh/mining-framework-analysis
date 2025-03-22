package edu.princeton.cs.algs4;

/**
 *  The {@code DepthFirstDirectedPaths} class represents a data type for finding
 *  directed paths from a source vertex <em>s</em> to every
 *  other vertex in the digraph.
 *  <p>
 *  This implementation uses depth-first search.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  It uses extra space (not including the graph) proportional to <em>V</em>.
 *  <p>
 *  For additional documentation,  
 *  see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of  
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne. 
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class DepthFirstDirectedPaths {
  private boolean[] marked;

  private int[] edgeTo;

  private final int s;

  /**
     * Computes a directed path from {@code s} to every other vertex in digraph {@code G}.
     * @param G the digraph
     * @param s the source vertex
     */
  public DepthFirstDirectedPaths(Digraph G, int s) {
    marked = new boolean[G.V()];
    edgeTo = new int[G.V()];
    this.s = s;
    dfs(G, s);
  }

  private void dfs(Digraph G, int v) {
    marked[v] = true;
    for (int w : G.adj(v)) {
      if (!marked[w]) {
        edgeTo[w] = v;
        dfs(G, w);
      }
    }
  }

  /**
     * Is there a directed path from the source vertex {@code s} to vertex {@code v}?
     * @param v the vertex
     * @return {@code true} if there is a directed path from the source
     *   vertex {@code s} to vertex {@code v}, {@code false} otherwise
     */
  public boolean hasPathTo(int v) {
    return marked[v];
  }

  /**
     * Returns a directed path from the source vertex {@code s} to vertex {@code v}, or
     * {@code null} if no such path.
     * @param v the vertex
     * @return the sequence of vertices on a directed path from the source vertex
     *   {@code s} to vertex {@code v}, as an Iterable
     */
  public Iterable<Integer> pathTo(int v) {
    if (!hasPathTo(v)) {
      return null;
    }
    Stack<Integer> path = new Stack<Integer>();
    for (int x = v; x != s; x = edgeTo[x]) {
      path.push(x);
    }
    path.push(s);
    return path;
  }

  /**
     * Unit tests the {@code DepthFirstDirectedPaths} data type.
     */
  public static void main(String[] args) {
    In in = new In(args[0]);
    Digraph G = new Digraph(in);
    int s = Integer.parseInt(args[1]);
    DepthFirstDirectedPaths dfs = new DepthFirstDirectedPaths(G, s);
    for (int v = 0; v < G.V(); v++) {
      if (dfs.hasPathTo(v)) {
        StdOut.printf("%d to %d:  ", s, v);
        for (int x : dfs.pathTo(v)) {
          if (x == s) {
            StdOut.print(x);
          } else {
            StdOut.print("-" + x);
          }
        }
        StdOut.println();
      } else {
        StdOut.printf("%d to %d:  not connected\n", s, v);
      }
    }
  }
}