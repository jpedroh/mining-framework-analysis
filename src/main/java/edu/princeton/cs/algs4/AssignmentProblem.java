package edu.princeton.cs.algs4;

/**
 *  The {@code AssignmentProblem} class represents a data type for computing
 *  an optimal solution to an <em>n</em>-by-<em>n</em> <em>assignment problem</em>.
 *  The assignment problem is to find a minimum weight matching in an
 *  edge-weighted complete bipartite graph.
 *  <p>
 *  The data type supplies methods for determining the optimal solution
 *  and the corresponding dual solution.
 *  <p>
 *  This implementation uses the <em>successive shortest paths algorithm</em>.
 *  The order of growth of the running time in the worst case is
 *  O(<em>n</em>^3 log <em>n</em>) to solve an <em>n</em>-by-<em>n</em>
 *  instance.
 *  <p>
 *  See also {@code WeightedBipartiteMatching}, which solves the problem
 *  in O(<em>E V</em> log <em>V</em>) time in the worst case
 *  for bipartite graphs with <em>V</em> vertices and <em>E</em> edges.
 *  <p>
 *  For additional documentation, see
 *  <a href="http://algs4.cs.princeton.edu/65reductions">Section 6.5</a>
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class AssignmentProblem {
  private static final int UNMATCHED = -1;

  private int n;

  private double[][] weight;

  private double minWeight;

  private double[] px;

  private double[] py;

  private int[] xy;

  private int[] yx;

  /**
     * Determines an optimal solution to the assignment problem.
     *
     * @param  weight the <em>n</em>-by-<em>n</em> matrix of weights
     * @throws IllegalArgumentException unless all weights are nonnegative
     * @throws NullPointerException if {@code weight} is {@code null}
     */
  public AssignmentProblem(double[][] weight) {
    n = weight.length;
    this.weight = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (weight[i][j] < minWeight) {
          minWeight = weight[i][j];
        }
        this.weight[i][j] = weight[i][j];
      }
    }
    px = new double[n];
    py = new double[n];
    xy = new int[n];
    yx = new int[n];
    for (int i = 0; i < n; i++) {
      xy[i] = UNMATCHED;
    }
    for (int j = 0; j < n; j++) {
      yx[j] = UNMATCHED;
    }
    for (int k = 0; k < n; k++) {
      assert isDualFeasible();
      assert isComplementarySlack();
      augment();
    }
    assert certifySolution();
  }

  private void augment() {
    EdgeWeightedDigraph G = new EdgeWeightedDigraph(2 * n + 2);
    int s = 2 * n, t = 2 * n + 1;
    for (int i = 0; i < n; i++) {
      if (xy[i] == UNMATCHED) {
        G.addEdge(new DirectedEdge(s, i, 0.0));
      }
    }
    for (int j = 0; j < n; j++) {
      if (yx[j] == UNMATCHED) {
        G.addEdge(new DirectedEdge(n + j, t, py[j]));
      }
    }
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (xy[i] == j) {
          G.addEdge(new DirectedEdge(n + j, i, 0.0));
        } else {
          G.addEdge(new DirectedEdge(i, n + j, reducedCost(i, j)));
        }
      }
    }
    DijkstraSP spt = new DijkstraSP(G, s);
    for (DirectedEdge e : spt.pathTo(t)) {
      int i = e.from(), j = e.to() - n;
      if (i < n) {
        xy[i] = j;
        yx[j] = i;
      }
    }
    for (int i = 0; i < n; i++) {
      px[i] += spt.distTo(i);
    }
    for (int j = 0; j < n; j++) {
      py[j] += spt.distTo(n + j);
    }
  }

  private double reducedCost(int i, int j) {
    return (weight[i][j] - minWeight) + px[i] - py[j];
  }

  /**
     * Returns the dual optimal value for the specified row.
     *
     * @param  i the row index
     * @return the dual optimal value for row {@code i}
     * @throws IndexOutOfBoundsException unless {@code 0 &le; i &lt; N}
     *
     */
  public double dualRow(int i) {
    validate(i);
    return px[i];
  }

  /**
     * Returns the dual optimal value for the specified column.
     *
     * @param  j the column index
     * @return the dual optimal value for column {@code j}
     * @throws IndexOutOfBoundsException unless {@code 0 &le; j &lt; n}
     *
     */
  public double dualCol(int j) {
    validate(j);
    return py[j];
  }

  /**
     * Returns the column associated with the specified row in the optimal solution.
     *
     * @param  i the row index
     * @return the column matched to row {@code i} in the optimal solution
     * @throws IndexOutOfBoundsException unless {@code 0 &le; i &lt; n}
     *
     */
  public int sol(int i) {
    validate(i);
    return xy[i];
  }

  /**
     * Returns the total weight of the optimal solution
     *
     * @return the total weight of the optimal solution
     *
     */
  public double weight() {
    double total = 0.0;
    for (int i = 0; i < n; i++) {
      if (xy[i] != UNMATCHED) {
        total += weight[i][xy[i]];
      }
    }
    return total;
  }

  private void validate(int i) {
    if (i < 0 || i >= n) {
      throw new IndexOutOfBoundsException();
    }
  }

  /**************************************************************************
     *
     *  The code below is solely for testing correctness of the data type.
     *
     **************************************************************************/
  private boolean isDualFeasible() {
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (reducedCost(i, j) < 0) {
          StdOut.println("Dual variables are not feasible");
          return false;
        }
      }
    }
    return true;
  }

  private boolean isComplementarySlack() {
    for (int i = 0; i < n; i++) {
      if ((xy[i] != UNMATCHED) && (reducedCost(i, xy[i]) != 0)) {
        StdOut.println("Primal and dual variables are not complementary slack");
        return false;
      }
    }
    return true;
  }

  private boolean isPerfectMatching() {
    boolean[] perm = new boolean[n];
    for (int i = 0; i < n; i++) {
      if (perm[xy[i]]) {
        StdOut.println("Not a perfect matching");
        return false;
      }
      perm[xy[i]] = true;
    }
    for (int j = 0; j < n; j++) {
      if (xy[yx[j]] != j) {
        StdOut.println("xy[] and yx[] are not inverses");
        return false;
      }
    }
    for (int i = 0; i < n; i++) {
      if (yx[xy[i]] != i) {
        StdOut.println("xy[] and yx[] are not inverses");
        return false;
      }
    }
    return true;
  }

  private boolean certifySolution() {
    return isPerfectMatching() && isDualFeasible() && isComplementarySlack();
  }

  /**
     * Unit tests the {@code AssignmentProblem} data type.
     * Takes a command-line argument n; creates a random n-by-n matrix;
     * solves the n-by-n assignment problem; and prints the optimal
     * solution.
     */
  public static void main(String[] args) {
    int n = Integer.parseInt(args[0]);
    double[][] weight = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        weight[i][j] = StdRandom.uniform(900) + 100;
      }
    }
    AssignmentProblem assignment = new AssignmentProblem(weight);
    StdOut.printf("weight = %.0f\n", assignment.weight());
    StdOut.println();
    if (n >= 20) {
      return;
    }
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (j == assignment.sol(i)) {
          StdOut.printf("*%.0f ", weight[i][j]);
        } else {
          StdOut.printf(" %.0f ", weight[i][j]);
        }
      }
      StdOut.println();
    }
  }
}