package edu.princeton.cs.algs4;

/**
 *  The {@code DirectedCycleX} class represents a data type for 
 *  determining whether a digraph has a directed cycle.
 *  The <em>hasCycle</em> operation determines whether the digraph has
 *  a directed cycle and, and of so, the <em>cycle</em> operation
 *  returns one.
 *  <p>
 *  This implementation uses a nonrecursive, queue-based algorithm.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>
 *  (in the worst case),
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  Afterwards, the <em>hasCycle</em> operation takes constant time;
 *  the <em>cycle</em> operation takes time proportional
 *  to the length of the cycle.
 *  <p>
 *  See {@link DirectedCycle} for a recursive version that uses depth-first search.
 *  See {@link Topological} or {@link TopologicalX} to compute a topological order
 *  when the digraph is acyclic.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class DirectedCycleX {
  private Stack<Integer> cycle;

  public DirectedCycleX(Digraph G) {
    int[] indegree = new int[G.V()];
    for (int v = 0; v < G.V(); v++) {
      indegree[v] = G.indegree(v);
    }
    Queue<Integer> queue = new Queue<Integer>();
    for (int v = 0; v < G.V(); v++) {
      if (indegree[v] == 0) {
        queue.enqueue(v);
      }
    }
    for (int j = 0; !queue.isEmpty(); j++) {
      int v = queue.dequeue();
      for (int w : G.adj(v)) {
        indegree[w]--;
        if (indegree[w] == 0) {
          queue.enqueue(w);
        }
      }
    }
    int[] edgeTo = new int[G.V()];
    int root = -1;
    for (int v = 0; v < G.V(); v++) {
      if (indegree[v] == 0) {
        continue;
      } else {
        root = v;
      }
      for (int w : G.adj(v)) {
        if (indegree[w] > 0) {
          edgeTo[w] = v;
        }
      }
    }
    if (root != -1) {
      boolean[] visited = new boolean[G.V()];
      while (!visited[root]) {
        visited[root] = true;
        root = edgeTo[root];
      }
      cycle = new Stack<Integer>();
      int v = root;
      do {
        cycle.push(v);
        v = edgeTo[v];
      } while(v != root);
      cycle.push(root);
    }
    assert check();
  }

  /**
     * Returns a directed cycle if the digraph has a directed cycle, and {@code null} otherwise.
     * @return a directed cycle (as an iterable) if the digraph has a directed cycle,
     *    and {@code null} otherwise
     */
  public Iterable<Integer> cycle() {
    return cycle;
  }

  /**
     * Does the digraph have a directed cycle?
     * @return {@code true} if the digraph has a directed cycle, {@code false} otherwise
     */
  public boolean hasCycle() {
    return cycle != null;
  }

  private boolean check() {
    if (hasCycle()) {
      int first = -1, last = -1;
      for (int v : cycle()) {
        if (first == -1) {
          first = v;
        }
        last = v;
      }
      if (first != last) {
        System.err.printf("cycle begins with %d and ends with %d\n", first, last);
        return false;
      }
    }
    return true;
  }

  public static void main(String[] args) {
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    int F = Integer.parseInt(args[2]);
    Digraph G = DigraphGenerator.dag(V, E);
    for (int i = 0; i < F; i++) {
      int v = StdRandom.uniform(V);
      int w = StdRandom.uniform(V);
      G.addEdge(v, w);
    }
    StdOut.println(G);
    DirectedCycleX finder = new DirectedCycleX(G);
    if (finder.hasCycle()) {
      StdOut.print("Directed cycle: ");
      for (int v : finder.cycle()) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    } else {
      StdOut.println("No directed cycle");
    }
    StdOut.println();
  }
}