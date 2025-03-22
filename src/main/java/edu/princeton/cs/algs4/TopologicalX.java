package edu.princeton.cs.algs4;

/**
 *  The {@code TopologicalX} class represents a data type for 
 *  determining a topological order of a directed acyclic graph (DAG).
 *  Recall, a digraph has a topological order if and only if it is a DAG.
 *  The <em>hasOrder</em> operation determines whether the digraph has
 *  a topological order, and if so, the <em>order</em> operation
 *  returns one.
 *  <p>
 *  This implementation uses a nonrecursive, queue-based algorithm.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>
 *  (in the worst case),
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  Afterwards, the <em>hasOrder</em> and <em>rank</em> operations takes constant time;
 *  the <em>order</em> operation takes time proportional to <em>V</em>.
 *  <p>
 *  See {@link DirectedCycle}, {@link DirectedCycleX}, and
 *  {@link EdgeWeightedDirectedCycle} to compute a
 *  directed cycle if the digraph is not a DAG.
 *  See {@link Topological} for a recursive version that uses depth-first search.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class TopologicalX {
  private Queue<Integer> order;

  private int[] rank;

  /**
     * Determines whether the digraph {@code G} has a topological order and, if so,
     * finds such a topological order.
     * @param G the digraph
     */
  public TopologicalX(Digraph G) {
    int[] indegree = new int[G.V()];
    for (int v = 0; v < G.V(); v++) {
      indegree[v] = G.indegree(v);
    }
    rank = new int[G.V()];
    order = new Queue<Integer>();
    int count = 0;
    Queue<Integer> queue = new Queue<Integer>();
    for (int v = 0; v < G.V(); v++) {
      if (indegree[v] == 0) {
        queue.enqueue(v);
      }
    }
    for (int j = 0; !queue.isEmpty(); j++) {
      int v = queue.dequeue();
      order.enqueue(v);
      rank[v] = count++;
      for (int w : G.adj(v)) {
        indegree[w]--;
        if (indegree[w] == 0) {
          queue.enqueue(w);
        }
      }
    }
    if (count != G.V()) {
      order = null;
    }
    assert check(G);
  }

  /**
     * Determines whether the edge-weighted digraph {@code G} has a
     * topological order and, if so, finds such a topological order.
     * @param G the digraph
     */
  public TopologicalX(EdgeWeightedDigraph G) {
    int[] indegree = new int[G.V()];
    for (int v = 0; v < G.V(); v++) {
      indegree[v] = G.indegree(v);
    }
    rank = new int[G.V()];
    order = new Queue<Integer>();
    int count = 0;
    Queue<Integer> queue = new Queue<Integer>();
    for (int v = 0; v < G.V(); v++) {
      if (indegree[v] == 0) {
        queue.enqueue(v);
      }
    }
    for (int j = 0; !queue.isEmpty(); j++) {
      int v = queue.dequeue();
      order.enqueue(v);
      rank[v] = count++;
      for (DirectedEdge e : G.adj(v)) {
        int w = e.to();
        indegree[w]--;
        if (indegree[w] == 0) {
          queue.enqueue(w);
        }
      }
    }
    if (count != G.V()) {
      order = null;
    }
    assert check(G);
  }

  /**
     * Returns a topological order if the digraph has a topologial order,
     * and {@code null} otherwise.
     * @return a topological order of the vertices (as an interable) if the
     *    digraph has a topological order (or equivalently, if the digraph is a DAG),
     *    and {@code null} otherwise
     */
  public Iterable<Integer> order() {
    return order;
  }

  /**
     * Does the digraph have a topological order?
     * @return {@code true} if the digraph has a topological order (or equivalently,
     *    if the digraph is a DAG), and {@code false} otherwise
     */
  public boolean hasOrder() {
    return order != null;
  }

  /**
     * The the rank of vertex {@code v} in the topological order;
     * -1 if the digraph is not a DAG
     * @return the position of vertex {@code v} in a topological order
     *    of the digraph; -1 if the digraph is not a DAG
     * @throws IndexOutOfBoundsException unless {@code v} is between 0 and
     *    <em>V</em> &minus; 1
     */
  public int rank(int v) {
    validateVertex(v);
    if (hasOrder()) {
      return rank[v];
    } else {
      return -1;
    }
  }

  private boolean check(Digraph G) {
    if (hasOrder()) {
      boolean[] found = new boolean[G.V()];
      for (int i = 0; i < G.V(); i++) {
        found[rank(i)] = true;
      }
      for (int i = 0; i < G.V(); i++) {
        if (!found[i]) {
          System.err.println("No vertex with rank " + i);
          return false;
        }
      }
      for (int v = 0; v < G.V(); v++) {
        for (int w : G.adj(v)) {
          if (rank(v) > rank(w)) {
            System.err.printf("%d-%d: rank(%d) = %d, rank(%d) = %d\n", v, w, v, rank(v), w, rank(w));
            return false;
          }
        }
      }
      int r = 0;
      for (int v : order()) {
        if (rank(v) != r) {
          System.err.println("order() and rank() inconsistent");
          return false;
        }
        r++;
      }
    }
    return true;
  }

  private boolean check(EdgeWeightedDigraph G) {
    if (hasOrder()) {
      boolean[] found = new boolean[G.V()];
      for (int i = 0; i < G.V(); i++) {
        found[rank(i)] = true;
      }
      for (int i = 0; i < G.V(); i++) {
        if (!found[i]) {
          System.err.println("No vertex with rank " + i);
          return false;
        }
      }
      for (int v = 0; v < G.V(); v++) {
        for (DirectedEdge e : G.adj(v)) {
          int w = e.to();
          if (rank(v) > rank(w)) {
            System.err.printf("%d-%d: rank(%d) = %d, rank(%d) = %d\n", v, w, v, rank(v), w, rank(w));
            return false;
          }
        }
      }
      int r = 0;
      for (int v : order()) {
        if (rank(v) != r) {
          System.err.println("order() and rank() inconsistent");
          return false;
        }
        r++;
      }
    }
    return true;
  }

  private void validateVertex(int v) {
    int V = rank.length;
    if (v < 0 || v >= V) {
      throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V - 1));
    }
  }

  /**
     * Unit tests the {@code TopologicalX} data type.
     */
  public static void main(String[] args) {
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    int F = Integer.parseInt(args[2]);
    Digraph G1 = DigraphGenerator.dag(V, E);
    EdgeWeightedDigraph G2 = new EdgeWeightedDigraph(V);
    for (int v = 0; v < G1.V(); v++) {
      for (int w : G1.adj(v)) {
        G2.addEdge(new DirectedEdge(v, w, 0.0));
      }
    }
    for (int i = 0; i < F; i++) {
      int v = StdRandom.uniform(V);
      int w = StdRandom.uniform(V);
      G1.addEdge(v, w);
      G2.addEdge(new DirectedEdge(v, w, 0.0));
    }
    StdOut.println(G1);
    StdOut.println();
    StdOut.println(G2);
    TopologicalX topological1 = new TopologicalX(G1);
    if (!topological1.hasOrder()) {
      StdOut.println("Not a DAG");
    } else {
      StdOut.print("Topological order: ");
      for (int v : topological1.order()) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    }
    TopologicalX topological2 = new TopologicalX(G2);
    if (!topological2.hasOrder()) {
      StdOut.println("Not a DAG");
    } else {
      StdOut.print("Topological order: ");
      for (int v : topological2.order()) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    }
  }
}