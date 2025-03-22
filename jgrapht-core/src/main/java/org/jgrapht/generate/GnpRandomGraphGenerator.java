package org.jgrapht.generate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.jgrapht.Graph;

/**
 * Create a random graph based on the $G(n, p)$ Erdős–Rényi model. See the Wikipedia article for
 * details and references about <a href="https://en.wikipedia.org/wiki/Random_graph">Random
 * Graphs</a> and the
 * <a href="https://en.wikipedia.org/wiki/Erd%C5%91s%E2%80%93R%C3%A9nyi_model">Erdős–Rényi model</a>
 * .
 * 
 * <p>
 * In the $G(n, p)$ model, a graph is constructed by connecting nodes randomly. Each edge is included
 * in the graph with probability $p$ independent from every other edge. The complexity of the
 * generator is $O(n^2)$ where $n$ is the number of vertices.
 * 
 * <p>
 * For the $G(n, M)$ model please see {@link GnmRandomGraphGenerator}.
 *
 * @author Dimitrios Michail
 * @since September 2016
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @see GnmRandomGraphGenerator
 */
public class GnpRandomGraphGenerator<V extends java.lang.Object, E extends java.lang.Object> implements GraphGenerator<V, E, V> {
  private static final boolean DEFAULT_ALLOW_LOOPS = false;

  private final Random rng;

  private final int n;

  private final double p;

  private final boolean createLoops;

  /**
     * Create a new $G(n, p)$ random graph generator. The generator does not create self-loops.
     * 
     * @param n the number of nodes
     * @param p the edge probability
     */
  public GnpRandomGraphGenerator(int n, double p) {
    this(n, p, new Random(), DEFAULT_ALLOW_LOOPS);
  }

  /**
     * Create a new $G(n, p)$ random graph generator. The generator does not create self-loops.
     * 
     * @param n the number of nodes
     * @param p the edge probability
     * @param seed seed for the random number generator
     */
  public GnpRandomGraphGenerator(int n, double p, long seed) {
    this(n, p, new Random(seed), DEFAULT_ALLOW_LOOPS);
  }

  /**
     * Create a new $G(n, p)$ random graph generator.
     * 
     * @param n the number of nodes
     * @param p the edge probability
     * @param seed seed for the random number generator
     * @param loops whether the generated graph may create loops
     */
  public GnpRandomGraphGenerator(int n, double p, long seed, boolean createLoops) {
    this(n, p, new Random(seed), createLoops);
  }

  /**
     * Create a new $G(n, p)$ random graph generator.
     * 
     * @param n the number of nodes
     * @param p the edge probability
     * @param rng the random number generator to use
     * @param loops whether the generated graph may create loops
     */
  public GnpRandomGraphGenerator(int n, double p, Random rng, boolean createLoops) {
    if (n < 0) {
      throw new IllegalArgumentException("number of vertices must be non-negative");
    }
    this.n = n;
    if (p < 0.0 || p > 1.0) {
      throw new IllegalArgumentException("not valid probability of edge existence");
    }
    this.p = p;
    this.rng = Objects.requireNonNull(rng);
    this.createLoops = createLoops;
  }

  /**
     * Generates a random graph based on the $G(n, p)$ model.
     * 
     * @param target the target graph
     * @param resultMap not used by this generator, can be null
     */
  @Override public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
    if (n == 0) {
      return;
    }
    if (createLoops && !target.getType().isAllowingSelfLoops()) {
      throw new IllegalArgumentException("Provided graph does not support self-loops");
    }
    int previousVertexSetSize = target.vertexSet().size();
    Map<Integer, V> vertices = new HashMap<>(n);
    for (int i = 0; i < n; i++) {
      V v = target.addVertex();
      vertices.put(i, v);
    }
    if (target.vertexSet().size() != previousVertexSetSize + n) {
      throw new IllegalArgumentException("Vertex factory did not produce " + n + " distinct vertices.");
    }
    boolean isDirected = target.getType().isDirected();
    for (int i = 0; i < n; i++) {
      for (int j = i; j < n; j++) {
        if (i == j) {
          if (!createLoops) {
            continue;
          }
        }
        V s = null;
        V t = null;
        if (rng.nextDouble() < p) {
          s = vertices.get(i);
          t = vertices.get(j);
          target.addEdge(s, t);
        }
        if (isDirected) {
          if (rng.nextDouble() < p) {
            if (s == null) {
              s = vertices.get(i);
              t = vertices.get(j);
            }
            target.addEdge(t, s);
          }
        }
      }
    }
  }
}