package edu.princeton.cs.algs4;
import java.util.Iterator;

/**
 *  The {@code NonrecursiveDFS} class represents a data type for finding
 *  the vertices connected to a source vertex <em>s</em> in the undirected
 *  graph.
 *  <p>
 *  This implementation uses a nonrecursive version of depth-first search
 *  with an explicit stack.
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
public class NonrecursiveDFS {
  private boolean[] marked;

  /**
     * Computes the vertices connected to the source vertex {@code s} in the graph {@code G}.
     * @param G the graph
     * @param s the source vertex
     */
  public NonrecursiveDFS(Graph G, int s) {
    marked = new boolean[G.V()];
    Iterator<Integer>[] adj = (Iterator<Integer>[]) new Iterator[G.V()];
    for (int v = 0; v < G.V(); v++) {
      adj[v] = G.adj(v).iterator();
    }
    Stack<Integer> stack = new Stack<Integer>();
    marked[s] = true;
    stack.push(s);
    while (!stack.isEmpty()) {
      int v = stack.peek();
      if (adj[v].hasNext()) {
        int w = adj[v].next();
        if (!marked[w]) {
          marked[w] = true;
          stack.push(w);
        }
      } else {
        stack.pop();
      }
    }
  }

  /**
     * Is vertex {@code v} connected to the source vertex {@code s}?
     * @param v the vertex
     * @return {@code true} if vertex {@code v} is connected to the source vertex {@code s},
     *    and {@code false} otherwise
     */
  public boolean marked(int v) {
    return marked[v];
  }

  /**
     * Unit tests the {@code NonrecursiveDFS} data type.
     */
  public static void main(String[] args) {
    In in = new In(args[0]);
    Graph G = new Graph(in);
    int s = Integer.parseInt(args[1]);
    NonrecursiveDFS dfs = new NonrecursiveDFS(G, s);
    for (int v = 0; v < G.V(); v++) {
      if (dfs.marked(v)) {
        StdOut.print(v + " ");
      }
    }
    StdOut.println();
  }
}