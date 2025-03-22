package edu.princeton.cs.algs4;
import java.util.Iterator;

/**
 *  The {@code DirectedEulerianPath} class represents a data type
 *  for finding an Eulerian path in a digraph.
 *  An <em>Eulerian path</em> is a path (not necessarily simple) that
 *  uses every edge in the digraph exactly once.
 *  <p>
 *  This implementation uses a nonrecursive depth-first search.
 *  The constructor runs in O(E + V) time, and uses O(V) extra space,
 *  where E is the number of edges and V the number of vertices
 *  All other methods take O(1) time.
 *  <p>
 *  To compute Eulerian cycles in digraphs, see {@link DirectedEulerianCycle}.
 *  To compute Eulerian cycles and paths in undirected graphs, see
 *  {@link EulerianCycle} and {@link EulerianPath}.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 * 
 * @author Robert Sedgewick
 * @author Kevin Wayne
 * @author Nate Liu
 */
public class DirectedEulerianPath {
  private Stack<Integer> path = null;

  /**
     * Computes an Eulerian path in the specified digraph, if one exists.
     * 
     * @param G the digraph
     */
  public DirectedEulerianPath(Digraph G) {
    int deficit = 0;
    int s = nonIsolatedVertex(G);
    for (int v = 0; v < G.V(); v++) {
      if (G.outdegree(v) > G.indegree(v)) {
        deficit += (G.outdegree(v) - G.indegree(v));
        s = v;
      }
    }
    if (deficit > 1) {
      return;
    }
    if (s == -1) {
      s = 0;
    }
    Iterator<Integer>[] adj = (Iterator<Integer>[]) new Iterator[G.V()];
    for (int v = 0; v < G.V(); v++) {
      adj[v] = G.adj(v).iterator();
    }
    Stack<Integer> stack = new Stack<Integer>();
    stack.push(s);
    path = new Stack<Integer>();
    while (!stack.isEmpty()) {
      int v = stack.pop();
      while (adj[v].hasNext()) {
        stack.push(v);
        v = adj[v].next();
      }
      path.push(v);
    }
    if (path.size() != G.E() + 1) {
      path = null;
    }
    assert check(G);
  }

  /**
     * Returns the sequence of vertices on an Eulerian path.
     * 
     * @return the sequence of vertices on an Eulerian path;
     *         {@code null} if no such path
     */
  public Iterable<Integer> path() {
    return path;
  }

  /**
     * Returns true if the digraph has an Eulerian path.
     * 
     * @return {@code true} if the digraph has an Eulerian path;
     *         {@code false} otherwise
     */
  public boolean hasEulerianPath() {
    return path != null;
  }

  private static int nonIsolatedVertex(Digraph G) {
    for (int v = 0; v < G.V(); v++) {
      if (G.outdegree(v) > 0) {
        return v;
      }
    }
    return -1;
  }

  /**************************************************************************
     *
     *  The code below is solely for testing correctness of the data type.
     *
     **************************************************************************/
  private static boolean hasEulerianPath(Digraph G) {
    if (G.E() == 0) {
      return true;
    }
    int deficit = 0;
    for (int v = 0; v < G.V(); v++) {
      if (G.outdegree(v) > G.indegree(v)) {
        deficit += (G.outdegree(v) - G.indegree(v));
      }
    }
    if (deficit > 1) {
      return false;
    }
    Graph H = new Graph(G.V());
    for (int v = 0; v < G.V(); v++) {
      for (int w : G.adj(v)) {
        H.addEdge(v, w);
      }
    }
    int s = nonIsolatedVertex(G);
    BreadthFirstPaths bfs = new BreadthFirstPaths(H, s);
    for (int v = 0; v < G.V(); v++) {
      if (H.degree(v) > 0 && !bfs.hasPathTo(v)) {
        return false;
      }
    }
    return true;
  }

  private boolean check(Digraph G) {
    if (hasEulerianPath() == (path() == null)) {
      return false;
    }
    if (hasEulerianPath() != hasEulerianPath(G)) {
      return false;
    }
    if (path == null) {
      return true;
    }
    if (path.size() != G.E() + 1) {
      return false;
    }
    return true;
  }

  private static void unitTest(Digraph G, String description) {
    StdOut.println(description);
    StdOut.println("-------------------------------------");
    StdOut.print(G);
    DirectedEulerianPath euler = new DirectedEulerianPath(G);
    StdOut.print("Eulerian path:  ");
    if (euler.hasEulerianPath()) {
      for (int v : euler.path()) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    } else {
      StdOut.println("none");
    }
    StdOut.println();
  }

  /**
     * Unit tests the {@code DirectedEulerianPath} data type.
     */
  public static void main(String[] args) {
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    Digraph G1 = DigraphGenerator.eulerianCycle(V, E);
    unitTest(G1, "Eulerian cycle");
    Digraph G2 = DigraphGenerator.eulerianPath(V, E);
    unitTest(G2, "Eulerian path");
    Digraph G3 = new Digraph(G2);
    G3.addEdge(StdRandom.uniform(V), StdRandom.uniform(V));
    unitTest(G3, "one random edge added to Eulerian path");
    Digraph G4 = new Digraph(V);
    int v4 = StdRandom.uniform(V);
    G4.addEdge(v4, v4);
    unitTest(G4, "single self loop");
    Digraph G5 = new Digraph(V);
    G5.addEdge(StdRandom.uniform(V), StdRandom.uniform(V));
    unitTest(G5, "single edge");
    Digraph G6 = new Digraph(V);
    unitTest(G6, "empty digraph");
    Digraph G7 = DigraphGenerator.simple(V, E);
    unitTest(G7, "simple digraph");
    Digraph G8 = new Digraph(new In("eulerianD.txt"));
    unitTest(G8, "4-vertex Eulerian digraph");
  }
}