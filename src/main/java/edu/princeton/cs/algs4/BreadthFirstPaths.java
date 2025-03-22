package edu.princeton.cs.algs4;

/**
 *  The {@code BreadthFirstPaths} class represents a data type for finding
 *  shortest paths (number of edges) from a source vertex <em>s</em>
 *  (or a set of source vertices)
 *  to every other vertex in an undirected graph.
 *  <p>
 *  This implementation uses breadth-first search.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  It uses extra space (not including the graph) proportional to <em>V</em>.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/41graph">Section 4.1</a>   
 *  of <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class BreadthFirstPaths {
  private static final int INFINITY = Integer.MAX_VALUE;

  private boolean[] marked;

  private int[] edgeTo;

  private int[] distTo;

  /**
     * Computes the shortest path between the source vertex {@code s}
     * and every other vertex in the graph {@code G}.
     * @param G the graph
     * @param s the source vertex
     */
  public BreadthFirstPaths(Graph G, int s) {
    marked = new boolean[G.V()];
    distTo = new int[G.V()];
    edgeTo = new int[G.V()];
    bfs(G, s);
    assert check(G, s);
  }

  /**
     * Computes the shortest path between any one of the source vertices in {@code sources}
     * and every other vertex in graph {@code G}.
     * @param G the graph
     * @param sources the source vertices
     */
  public BreadthFirstPaths(Graph G, Iterable<Integer> sources) {
    marked = new boolean[G.V()];
    distTo = new int[G.V()];
    edgeTo = new int[G.V()];
    for (int v = 0; v < G.V(); v++) {
      distTo[v] = INFINITY;
    }
    bfs(G, sources);
  }

  private void bfs(Graph G, int s) {
    Queue<Integer> q = new Queue<Integer>();
    for (int v = 0; v < G.V(); v++) {
      distTo[v] = INFINITY;
    }
    distTo[s] = 0;
    marked[s] = true;
    q.enqueue(s);
    while (!q.isEmpty()) {
      int v = q.dequeue();
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          edgeTo[w] = v;
          distTo[w] = distTo[v] + 1;
          marked[w] = true;
          q.enqueue(w);
        }
      }
    }
  }

  private void bfs(Graph G, Iterable<Integer> sources) {
    Queue<Integer> q = new Queue<Integer>();
    for (int s : sources) {
      marked[s] = true;
      distTo[s] = 0;
      q.enqueue(s);
    }
    while (!q.isEmpty()) {
      int v = q.dequeue();
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          edgeTo[w] = v;
          distTo[w] = distTo[v] + 1;
          marked[w] = true;
          q.enqueue(w);
        }
      }
    }
  }

  /**
     * Is there a path between the source vertex {@code s} (or sources) and vertex {@code v}?
     * @param v the vertex
     * @return {@code true} if there is a path, and {@code false} otherwise
     */
  public boolean hasPathTo(int v) {
    return marked[v];
  }

  /**
     * Returns the number of edges in a shortest path between the source vertex {@code s}
     * (or sources) and vertex {@code v}?
     * @param v the vertex
     * @return the number of edges in a shortest path
     */
  public int distTo(int v) {
    return distTo[v];
  }

  /**
     * Returns a shortest path between the source vertex {@code s} (or sources)
     * and {@code v}, or {@code null} if no such path.
     * @param v the vertex
     * @return the sequence of vertices on a shortest path, as an Iterable
     */
  public Iterable<Integer> pathTo(int v) {
    if (!hasPathTo(v)) {
      return null;
    }
    Stack<Integer> path = new Stack<Integer>();
    int x;
    for (x = v; distTo[x] != 0; x = edgeTo[x]) {
      path.push(x);
    }
    path.push(x);
    return path;
  }

  private boolean check(Graph G, int s) {
    if (distTo[s] != 0) {
      StdOut.println("distance of source " + s + " to itself = " + distTo[s]);
      return false;
    }
    for (int v = 0; v < G.V(); v++) {
      for (int w : G.adj(v)) {
        if (hasPathTo(v) != hasPathTo(w)) {
          StdOut.println("edge " + v + "-" + w);
          StdOut.println("hasPathTo(" + v + ") = " + hasPathTo(v));
          StdOut.println("hasPathTo(" + w + ") = " + hasPathTo(w));
          return false;
        }
        if (hasPathTo(v) && (distTo[w] > distTo[v] + 1)) {
          StdOut.println("edge " + v + "-" + w);
          StdOut.println("distTo[" + v + "] = " + distTo[v]);
          StdOut.println("distTo[" + w + "] = " + distTo[w]);
          return false;
        }
      }
    }
    for (int w = 0; w < G.V(); w++) {
      if (!hasPathTo(w) || w == s) {
        continue;
      }
      int v = edgeTo[w];
      if (distTo[w] != distTo[v] + 1) {
        StdOut.println("shortest path edge " + v + "-" + w);
        StdOut.println("distTo[" + v + "] = " + distTo[v]);
        StdOut.println("distTo[" + w + "] = " + distTo[w]);
        return false;
      }
    }
    return true;
  }

  /**
     * Unit tests the {@code BreadthFirstPaths} data type.
     */
  public static void main(String[] args) {
    In in = new In(args[0]);
    Graph G = new Graph(in);
    int s = Integer.parseInt(args[1]);
    BreadthFirstPaths bfs = new BreadthFirstPaths(G, s);
    for (int v = 0; v < G.V(); v++) {
      if (bfs.hasPathTo(v)) {
        StdOut.printf("%d to %d (%d):  ", s, v, bfs.distTo(v));
        for (int x : bfs.pathTo(v)) {
          if (x == s) {
            StdOut.print(x);
          } else {
            StdOut.print("-" + x);
          }
        }
        StdOut.println();
      } else {
        StdOut.printf("%d to %d (-):  not connected\n", s, v);
      }
    }
  }
}