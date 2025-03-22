package edu.princeton.cs.algs4;

/**
 *  The {@code TarjanSCC} class represents a data type for 
 *  determining the strong components in a digraph.
 *  The <em>id</em> operation determines in which strong component
 *  a given vertex lies; the <em>areStronglyConnected</em> operation
 *  determines whether two vertices are in the same strong component;
 *  and the <em>count</em> operation determines the number of strong
 *  components.

 *  The <em>component identifier</em> of a component is one of the
 *  vertices in the strong component: two vertices have the same component
 *  identifier if and only if they are in the same strong component.

 *  <p>
 *  This implementation uses Tarjan's algorithm.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>
 *  (in the worst case),
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  Afterwards, the <em>id</em>, <em>count</em>, and <em>areStronglyConnected</em>
 *  operations take constant time.
 *  For alternate implementations of the same API, see
 *  {@link KosarajuSharirSCC} and {@link GabowSCC}.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class TarjanSCC {
  private boolean[] marked;

  private int[] id;

  private int[] low;

  private int pre;

  private int count;

  private Stack<Integer> stack;

  /**
     * Computes the strong components of the digraph {@code G}.
     * @param G the digraph
     */
  public TarjanSCC(Digraph G) {
    marked = new boolean[G.V()];
    stack = new Stack<Integer>();
    id = new int[G.V()];
    low = new int[G.V()];
    for (int v = 0; v < G.V(); v++) {
      if (!marked[v]) {
        dfs(G, v);
      }
    }
    assert check(G);
  }

  private void dfs(Digraph G, int v) {
    marked[v] = true;
    low[v] = pre++;
    int min = low[v];
    stack.push(v);
    for (int w : G.adj(v)) {
      if (!marked[w]) {
        dfs(G, w);
      }
      if (low[w] < min) {
        min = low[w];
      }
    }
    if (min < low[v]) {
      low[v] = min;
      return;
    }
    int w;
    do {
      w = stack.pop();
      id[w] = count;
      low[w] = G.V();
    } while(w != v);
    count++;
  }

  /**
     * Returns the number of strong components.
     * @return the number of strong components
     */
  public int count() {
    return count;
  }

  /**
     * Are vertices {@code v} and {@code w} in the same strong component?
     * @param v one vertex
     * @param w the other vertex
     * @return {@code true} if vertices {@code v} and {@code w} are in the same
     *     strong component, and {@code false} otherwise
     */
  public boolean stronglyConnected(int v, int w) {
    return id[v] == id[w];
  }

  /**
     * Returns the component id of the strong component containing vertex {@code v}.
     * @param v the vertex
     * @return the component id of the strong component containing vertex {@code v}
     */
  public int id(int v) {
    return id[v];
  }

  private boolean check(Digraph G) {
    TransitiveClosure tc = new TransitiveClosure(G);
    for (int v = 0; v < G.V(); v++) {
      for (int w = 0; w < G.V(); w++) {
        if (stronglyConnected(v, w) != (tc.reachable(v, w) && tc.reachable(w, v))) {
          return false;
        }
      }
    }
    return true;
  }

  /**
     * Unit tests the {@code TarjanSCC} data type.
     */
  public static void main(String[] args) {
    In in = new In(args[0]);
    Digraph G = new Digraph(in);
    TarjanSCC scc = new TarjanSCC(G);
    int m = scc.count();
    StdOut.println(m + " components");
    Queue<Integer>[] components = (Queue<Integer>[]) new Queue[m];
    for (int i = 0; i < m; i++) {
      components[i] = new Queue<Integer>();
    }
    for (int v = 0; v < G.V(); v++) {
      components[scc.id(v)].enqueue(v);
    }
    for (int i = 0; i < m; i++) {
      for (int v : components[i]) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    }
  }
}