package edu.princeton.cs.algs4;

/**
 *  The {@code EulerianCycle} class represents a data type
 *  for finding an Eulerian cycle or path in a graph.
 *  An <em>Eulerian cycle</em> is a cycle (not necessarily simple) that
 *  uses every edge in the graph exactly once.
 *  <p>
 *  This implementation uses a nonrecursive depth-first search.
 *  The constructor runs in O(<Em>E</em> + <em>V</em>) time,
 *  and uses O(<em>E</em> + <em>V</em>) extra space, where <em>E</em> is the
 *  number of edges and <em>V</em> the number of vertices
 *  All other methods take O(1) time.
 *  <p>
 *  To compute Eulerian paths in graphs, see {@link EulerianPath}.
 *  To compute Eulerian cycles and paths in digraphs, see
 *  {@link DirectedEulerianCycle} and {@link DirectedEulerianPath}.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/41graph">Section 4.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 * 
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Nate Liu
 */
public class EulerianCycle {
  private Stack<Integer> cycle = new Stack<Integer>();

  private static class Edge {
    private final int v;

    private final int w;

    private boolean isUsed;

    public Edge(int v, int w) {
      this.v = v;
      this.w = w;
      isUsed = false;
    }

    public int other(int vertex) {
      if (vertex == v) {
        return w;
      } else {
        if (vertex == w) {
          return v;
        } else {
          throw new IllegalArgumentException("Illegal endpoint");
        }
      }
    }
  }

  /**
     * Computes an Eulerian cycle in the specified graph, if one exists.
     * 
     * @param G the graph
     */
  public EulerianCycle(Graph G) {
    if (G.E() == 0) {
      return;
    }
    for (int v = 0; v < G.V(); v++) {
      if (G.degree(v) % 2 != 0) {
        return;
      }
    }
    Queue<Edge>[] adj = (Queue<Edge>[]) new Queue[G.V()];
    for (int v = 0; v < G.V(); v++) {
      adj[v] = new Queue<Edge>();
    }
    for (int v = 0; v < G.V(); v++) {
      int selfLoops = 0;
      for (int w : G.adj(v)) {
        if (v == w) {
          if (selfLoops % 2 == 0) {
            Edge e = new Edge(v, w);
            adj[v].enqueue(e);
            adj[w].enqueue(e);
          }
          selfLoops++;
        } else {
          if (v < w) {
            Edge e = new Edge(v, w);
            adj[v].enqueue(e);
            adj[w].enqueue(e);
          }
        }
      }
    }
    int s = nonIsolatedVertex(G);
    Stack<Integer> stack = new Stack<Integer>();
    stack.push(s);
    cycle = new Stack<Integer>();
    while (!stack.isEmpty()) {
      int v = stack.pop();
      while (!adj[v].isEmpty()) {
        Edge edge = adj[v].dequeue();
        if (edge.isUsed) {
          continue;
        }
        edge.isUsed = true;
        stack.push(v);
        v = edge.other(v);
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
     * Returns true if the graph has an Eulerian cycle.
     * 
     * @return {@code true} if the graph has an Eulerian cycle;
     *         {@code false} otherwise
     */
  public boolean hasEulerianCycle() {
    return cycle != null;
  }

  private static int nonIsolatedVertex(Graph G) {
    for (int v = 0; v < G.V(); v++) {
      if (G.degree(v) > 0) {
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
  private static boolean hasEulerianCycle(Graph G) {
    if (G.E() == 0) {
      return false;
    }
    for (int v = 0; v < G.V(); v++) {
      if (G.degree(v) % 2 != 0) {
        return false;
      }
    }
    int s = nonIsolatedVertex(G);
    BreadthFirstPaths bfs = new BreadthFirstPaths(G, s);
    for (int v = 0; v < G.V(); v++) {
      if (G.degree(v) > 0 && !bfs.hasPathTo(v)) {
        return false;
      }
    }
    return true;
  }

  private boolean certifySolution(Graph G) {
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
    int first = -1, last = -1;
    for (int v : cycle()) {
      if (first == -1) {
        first = v;
      }
      last = v;
    }
    if (first != last) {
      return false;
    }
    return true;
  }

  private static void unitTest(Graph G, String description) {
    StdOut.println(description);
    StdOut.println("-------------------------------------");
    StdOut.print(G);
    EulerianCycle euler = new EulerianCycle(G);
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
     * Unit tests the {@code EulerianCycle} data type.
     */
  public static void main(String[] args) {
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    Graph G1 = GraphGenerator.eulerianCycle(V, E);
    unitTest(G1, "Eulerian cycle");
    Graph G2 = GraphGenerator.eulerianPath(V, E);
    unitTest(G2, "Eulerian path");
    Graph G3 = new Graph(V);
    unitTest(G3, "empty graph");
    Graph G4 = new Graph(V);
    int v4 = StdRandom.uniform(V);
    G4.addEdge(v4, v4);
    unitTest(G4, "single self loop");
    Graph H1 = GraphGenerator.eulerianCycle(V / 2, E / 2);
    Graph H2 = GraphGenerator.eulerianCycle(V - V / 2, E - E / 2);
    int[] perm = new int[V];
    for (int i = 0; i < V; i++) {
      perm[i] = i;
    }
    StdRandom.shuffle(perm);
    Graph G5 = new Graph(V);
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
    Graph G6 = GraphGenerator.simple(V, E);
    unitTest(G6, "simple graph");
  }
}