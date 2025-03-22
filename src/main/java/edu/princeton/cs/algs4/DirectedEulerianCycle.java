package edu.princeton.cs.algs4;
import java.util.Iterator;

/**
 *  The {@code DirectedEulerianCycle} class represents a data type
 *  for finding an Eulerian cycle or path in a digraph.
 *  An <em>Eulerian cycle</em> is a cycle (not necessarily simple) that
 *  uses every edge in the digraph exactly once.
 *  <p>
 *  This implementation uses a nonrecursive depth-first search.
 *  The constructor runs in O(<Em>E</em> + <em>V</em>) time,
 *  and uses O(<em>V</em>) extra space, where <em>E</em> is the
 *  number of edges and <em>V</em> the number of vertices
 *  All other methods take O(1) time.
 *  <p>
 *  To compute Eulerian paths in digraphs, see {@link DirectedEulerianPath}.
 *  To compute Eulerian cycles and paths in undirected graphs, see
 *  {@link EulerianCycle} and {@link EulerianPath}.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 * 
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Nate Liu
 */
public class DirectedEulerianCycle {
  private Stack<Integer> cycle = null;

  /**
     * Computes an Eulerian cycle in the specified digraph, if one exists.
     * 
     * @param G the digraph
     */
  public DirectedEulerianCycle(Digraph G) {
    if (G.E() == 0) {
      return;
    }
    for (int v = 0; v < G.V(); v++) {
      if (G.outdegree(v) != G.indegree(v)) {
        return;
      }
    }
    Iterator<Integer>[] adj = (Iterator<Integer>[]) new Iterator[G.V()];
    for (int v = 0; v < G.V(); v++) {
      adj[v] = G.adj(v).iterator();
    }
    int s = nonIsolatedVertex(G);
    Stack<Integer> stack = new Stack<Integer>();
    stack.push(s);
    cycle = new Stack<Integer>();
    while (!stack.isEmpty()) {
      int v = stack.pop();
      while (adj[v].hasNext()) {
        stack.push(v);
        v = adj[v].next();
      }
      cycle.push(v);
    }
    if (cycle.size() != G.E() + 1) {
      cycle = null;
    }
    assert certifySolution(G);
  }

  /**
     * Returns the sequence of vertices on an Eulerian cycle.
     * 
     * @return the sequence of vertices on an Eulerian cycle;
     *         {@code null} if no such cycle
     */
  public Iterable<Integer> cycle() {
    return cycle;
  }

  /**
     * Returns true if the digraph has an Eulerian cycle.
     * 
     * @return {@code true} if the digraph has an Eulerian cycle;
     *         {@code false} otherwise
     */
  public boolean hasEulerianCycle() {
    return cycle != null;
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
  private static boolean hasEulerianCycle(Digraph G) {
    if (G.E() == 0) {
      return false;
    }
    for (int v = 0; v < G.V(); v++) {
      if (G.outdegree(v) != G.indegree(v)) {
        return false;
      }
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

  private boolean certifySolution(Digraph G) {
    if (hasEulerianCycle() == (cycle() == null)) {
      return false;
    }
    if (hasEulerianCycle() != hasEulerianCycle(G)) {
      return false;
    }
    if (cycle == null) {
      return true;
    }
    if (cycle.size() != G.E() + 1) {
      return false;
    }
    return true;
  }

  private static void unitTest(Digraph G, String description) {
    StdOut.println(description);
    StdOut.println("-------------------------------------");
    StdOut.print(G);
    DirectedEulerianCycle euler = new DirectedEulerianCycle(G);
    StdOut.print("Eulerian cycle: ");
    if (euler.hasEulerianCycle()) {
      for (int v : euler.cycle()) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    } else {
      StdOut.println("none");
    }
    StdOut.println();
  }

  /**
     * Unit tests the {@code DirectedEulerianCycle} data type.
     */
  public static void main(String[] args) {
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    Digraph G1 = DigraphGenerator.eulerianCycle(V, E);
    unitTest(G1, "Eulerian cycle");
    Digraph G2 = DigraphGenerator.eulerianPath(V, E);
    unitTest(G2, "Eulerian path");
    Digraph G3 = new Digraph(V);
    unitTest(G3, "empty digraph");
    Digraph G4 = new Digraph(V);
    int v4 = StdRandom.uniform(V);
    G4.addEdge(v4, v4);
    unitTest(G4, "single self loop");
    Digraph H1 = DigraphGenerator.eulerianCycle(V / 2, E / 2);
    Digraph H2 = DigraphGenerator.eulerianCycle(V - V / 2, E - E / 2);
    int[] perm = new int[V];
    for (int i = 0; i < V; i++) {
      perm[i] = i;
    }
    StdRandom.shuffle(perm);
    Digraph G5 = new Digraph(V);
    for (int v = 0; v < H1.V(); v++) {
      for (int w : H1.adj(v)) {
        G5.addEdge(perm[v], perm[w]);
      }
    }
    for (int v = 0; v < H2.V(); v++) {
      for (int w : H2.adj(v)) {
        G5.addEdge(perm[V / 2 + v], perm[V / 2 + w]);
      }
    }
    unitTest(G5, "Union of two disjoint cycles");
    Digraph G6 = DigraphGenerator.simple(V, E);
    unitTest(G6, "simple digraph");
    Digraph G7 = new Digraph(new In("eulerianD.txt"));
    unitTest(G7, "4-vertex Eulerian digraph");
  }
}