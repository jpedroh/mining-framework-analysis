package edu.princeton.cs.algs4;

/**
 *  The {@code EulerianPath} class represents a data type
 *  for finding an Eulerian path in a graph.
 *  An <em>Eulerian path</em> is a path (not necessarily simple) that
 *  uses every edge in the graph exactly once.
 *  <p>
 *  This implementation uses a nonrecursive depth-first search.
 *  The constructor runs in O(<em>E</em> + <em>V</em>) time,
 *  and uses O(<em>E</em> + <em>V</em>) extra space,
 *  where <em>E</em> is the number of edges and <em>V</em> the number of vertices
 *  All other methods take O(1) time.
 *  <p>
 *  To compute Eulerian cycles in graphs, see {@link EulerianCycle}.
 *  To compute Eulerian cycles and paths in digraphs, see
 *  {@link DirectedEulerianCycle} and {@link DirectedEulerianPath}.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/41graph">Section 4.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 * 
 * @author Robert Sedgewick
 * @author Kevin Wayne
 * @author Nate Liu
 */
public class EulerianPath {
  private Stack<Integer> path = null;

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
     * Computes an Eulerian path in the specified graph, if one exists.
     * 
     * @param G the graph
     */
  public EulerianPath(Graph G) {
    int oddDegreeVertices = 0;
    int s = nonIsolatedVertex(G);
    for (int v = 0; v < G.V(); v++) {
      if (G.degree(v) % 2 != 0) {
        oddDegreeVertices++;
        s = v;
      }
    }
    if (oddDegreeVertices > 2) {
      return;
    }
    if (s == -1) {
      s = 0;
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
    Stack<Integer> stack = new Stack<Integer>();
    stack.push(s);
    path = new Stack<Integer>();
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
      path.push(v);
    }
    if (path.size() != G.E() + 1) {
      path = null;
    }
    assert certifySolution(G);
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
     * Returns true if the graph has an Eulerian path.
     * 
     * @return {@code true} if the graph has an Eulerian path;
     *         {@code false} otherwise
     */
  public boolean hasEulerianPath() {
    return path != null;
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
  private static boolean hasEulerianPath(Graph G) {
    if (G.E() == 0) {
      return true;
    }
    int oddDegreeVertices = 0;
    for (int v = 0; v < G.V(); v++) {
      if (G.degree(v) % 2 != 0) {
        oddDegreeVertices++;
      }
    }
    if (oddDegreeVertices > 2) {
      return false;
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

  private static void unitTest(Graph G, String description) {
    StdOut.println(description);
    StdOut.println("-------------------------------------");
    StdOut.print(G);
    EulerianPath euler = new EulerianPath(G);
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
     * Unit tests the {@code EulerianPath} data type.
     */
  public static void main(String[] args) {
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    Graph G1 = GraphGenerator.eulerianCycle(V, E);
    unitTest(G1, "Eulerian cycle");
    Graph G2 = GraphGenerator.eulerianPath(V, E);
    unitTest(G2, "Eulerian path");
    Graph G3 = new Graph(G2);
    G3.addEdge(StdRandom.uniform(V), StdRandom.uniform(V));
    unitTest(G3, "one random edge added to Eulerian path");
    Graph G4 = new Graph(V);
    int v4 = StdRandom.uniform(V);
    G4.addEdge(v4, v4);
    unitTest(G4, "single self loop");
    Graph G5 = new Graph(V);
    G5.addEdge(StdRandom.uniform(V), StdRandom.uniform(V));
    unitTest(G5, "single edge");
    Graph G6 = new Graph(V);
    unitTest(G6, "empty graph");
    Graph G7 = GraphGenerator.simple(V, E);
    unitTest(G7, "simple graph");
  }
}