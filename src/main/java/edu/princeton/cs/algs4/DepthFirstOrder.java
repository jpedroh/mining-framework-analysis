package edu.princeton.cs.algs4;

/**
 *  The {@code DepthFirstOrder} class represents a data type for 
 *  determining depth-first search ordering of the vertices in a digraph
 *  or edge-weighted digraph, including preorder, postorder, and reverse postorder.
 *  <p>
 *  This implementation uses depth-first search.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>
 *  (in the worst case),
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  Afterwards, the <em>preorder</em>, <em>postorder</em>, and <em>reverse postorder</em>
 *  operation takes take time proportional to <em>V</em>.
 *  <p>
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class DepthFirstOrder {
  private boolean[] marked;

  private int[] pre;

  private int[] post;

  private Queue<Integer> preorder;

  private Queue<Integer> postorder;

  private int preCounter;

  private int postCounter;

  /**
     * Determines a depth-first order for the digraph {@code G}.
     * @param G the digraph
     */
  public DepthFirstOrder(Digraph G) {
    pre = new int[G.V()];
    post = new int[G.V()];
    postorder = new Queue<Integer>();
    preorder = new Queue<Integer>();
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++) {
      if (!marked[v]) {
        dfs(G, v);
      }
    }
    assert check(G);
  }

  /**
     * Determines a depth-first order for the edge-weighted digraph {@code G}.
     * @param G the edge-weighted digraph
     */
  public DepthFirstOrder(EdgeWeightedDigraph G) {
    pre = new int[G.V()];
    post = new int[G.V()];
    postorder = new Queue<Integer>();
    preorder = new Queue<Integer>();
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++) {
      if (!marked[v]) {
        dfs(G, v);
      }
    }
  }

  private void dfs(Digraph G, int v) {
    marked[v] = true;
    pre[v] = preCounter++;
    preorder.enqueue(v);
    for (int w : G.adj(v)) {
      if (!marked[w]) {
        dfs(G, w);
      }
    }
    postorder.enqueue(v);
    post[v] = postCounter++;
  }

  private void dfs(EdgeWeightedDigraph G, int v) {
    marked[v] = true;
    pre[v] = preCounter++;
    preorder.enqueue(v);
    for (DirectedEdge e : G.adj(v)) {
      int w = e.to();
      if (!marked[w]) {
        dfs(G, w);
      }
    }
    postorder.enqueue(v);
    post[v] = postCounter++;
  }

  /**
     * Returns the preorder number of vertex {@code v}.
     * @param v the vertex
     * @return the preorder number of vertex {@code v}
     */
  public int pre(int v) {
    return pre[v];
  }

  /**
     * Returns the postorder number of vertex {@code v}.
     * @param v the vertex
     * @return the postorder number of vertex {@code v}
     */
  public int post(int v) {
    return post[v];
  }

  /**
     * Returns the vertices in postorder.
     * @return the vertices in postorder, as an iterable of vertices
     */
  public Iterable<Integer> post() {
    return postorder;
  }

  /**
     * Returns the vertices in preorder.
     * @return the vertices in preorder, as an iterable of vertices
     */
  public Iterable<Integer> pre() {
    return preorder;
  }

  /**
     * Returns the vertices in reverse postorder.
     * @return the vertices in reverse postorder, as an iterable of vertices
     */
  public Iterable<Integer> reversePost() {
    Stack<Integer> reverse = new Stack<Integer>();
    for (int v : postorder) {
      reverse.push(v);
    }
    return reverse;
  }

  private boolean check(Digraph G) {
    int r = 0;
    for (int v : post()) {
      if (post(v) != r) {
        StdOut.println("post(v) and post() inconsistent");
        return false;
      }
      r++;
    }
    r = 0;
    for (int v : pre()) {
      if (pre(v) != r) {
        StdOut.println("pre(v) and pre() inconsistent");
        return false;
      }
      r++;
    }
    return true;
  }

  /**
     * Unit tests the {@code DepthFirstOrder} data type.
     */
  public static void main(String[] args) {
    In in = new In(args[0]);
    Digraph G = new Digraph(in);
    DepthFirstOrder dfs = new DepthFirstOrder(G);
    StdOut.println("   v  pre post");
    StdOut.println("--------------");
    for (int v = 0; v < G.V(); v++) {
      StdOut.printf("%4d %4d %4d\n", v, dfs.pre(v), dfs.post(v));
    }
    StdOut.print("Preorder:  ");
    for (int v : dfs.pre()) {
      StdOut.print(v + " ");
    }
    StdOut.println();
    StdOut.print("Postorder: ");
    for (int v : dfs.post()) {
      StdOut.print(v + " ");
    }
    StdOut.println();
    StdOut.print("Reverse postorder: ");
    for (int v : dfs.reversePost()) {
      StdOut.print(v + " ");
    }
    StdOut.println();
  }
}