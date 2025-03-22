package edu.princeton.cs.algs4;

/**
 *  The {@code Cycle} class represents a data type for 
 *  determining whether an undirected graph has a cycle.
 *  The <em>hasCycle</em> operation determines whether the graph has
 *  a cycle and, if so, the <em>cycle</em> operation returns one.
 *  <p>
 *  This implementation uses depth-first search.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>
 *  (in the worst case),
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  Afterwards, the <em>hasCycle</em> operation takes constant time;
 *  the <em>cycle</em> operation takes time proportional
 *  to the length of the cycle.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/41graph">Section 4.1</a>   
 *  of <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Cycle {
  private boolean[] marked;

  private int[] edgeTo;

  private Stack<Integer> cycle;

  /**
     * Determines whether the undirected graph {@code G} has a cycle and,
     * if so, finds such a cycle.
     *
     * @param G the undirected graph
     */
  public Cycle(Graph G) {
    if (hasSelfLoop(G)) {
      return;
    }
    if (hasParallelEdges(G)) {
      return;
    }
    marked = new boolean[G.V()];
    edgeTo = new int[G.V()];
    for (int v = 0; v < G.V(); v++) {
      if (!marked[v]) {
        dfs(G, -1, v);
      }
    }
  }

  private boolean hasSelfLoop(Graph G) {
    for (int v = 0; v < G.V(); v++) {
      for (int w : G.adj(v)) {
        if (v == w) {
          cycle = new Stack<Integer>();
          cycle.push(v);
          cycle.push(v);
          return true;
        }
      }
    }
    return false;
  }

  private boolean hasParallelEdges(Graph G) {
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++) {
      for (int w : G.adj(v)) {
        if (marked[w]) {
          cycle = new Stack<Integer>();
          cycle.push(v);
          cycle.push(w);
          cycle.push(v);
          return true;
        }
        marked[w] = true;
      }
      for (int w : G.adj(v)) {
        marked[w] = false;
      }
    }
    return false;
  }

  /**
     * Returns true if the graph {@code G} has a cycle.
     *
     * @return {@code true} if the graph has a cycle; {@code false} otherwise
     */
  public boolean hasCycle() {
    return cycle != null;
  }

  /**
     * Returns a cycle in the graph {@code G}.
     * @return a cycle if the graph {@code G} has a cycle,
     *         and {@code null} otherwise
     */
  public Iterable<Integer> cycle() {
    return cycle;
  }

  private void dfs(Graph G, int u, int v) {
    marked[v] = true;
    for (int w : G.adj(v)) {
      if (cycle != null) {
        return;
      }
      if (!marked[w]) {
        edgeTo[w] = v;
        dfs(G, v, w);
      } else {
        if (w != u) {
          cycle = new Stack<Integer>();
          for (int x = v; x != w; x = edgeTo[x]) {
            cycle.push(x);
          }
          cycle.push(w);
          cycle.push(v);
        }
      }
    }
  }

  /**
     * Unit tests the {@code Cycle} data type.
     */
  public static void main(String[] args) {
    In in = new In(args[0]);
    Graph G = new Graph(in);
    Cycle finder = new Cycle(G);
    if (finder.hasCycle()) {
      for (int v : finder.cycle()) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    } else {
      StdOut.println("Graph is acyclic");
    }
  }
}