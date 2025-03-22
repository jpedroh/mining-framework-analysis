package org.jgrapht.alg;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.UnionFind;
import java.util.*;

/**
 * Used to calculate Tarjan's Lowest Common Ancestors Algorithm
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Leo Crawford
 *
 * @see org.jgrapht.alg.lca.TarjanLCAFinder
 */
@Deprecated public class TarjanLowestCommonAncestor<V extends java.lang.Object, E extends java.lang.Object> {
  private Graph<V, E> g;

  /**
     * Create an instance with a reference to the graph that we will find LCAs for
     * 
     * @param g the input graph
     */
  public TarjanLowestCommonAncestor(Graph<V, E> g) {
    this.g = g;
  }

  /**
     * Calculate the LCM between <code>a</code> and <code>b</code> treating <code>start</code> as
     * the root we want to search from.
     * 
     * @param start the root of subtree
     * @param a the first vertex
     * @param b the second vertex
     * @return the least common ancestor
     */
  public V calculate(V start, V a, V b) {
    List<LcaRequestResponse<V>> list = new LinkedList<>();
    list.add(new LcaRequestResponse<>(a, b));
    return calculate(start, list).get(0);
  }

  /**
     * Calculate the LCM's between a set of pairs (<code>a</code> and <code>
     * b</code>) treating <code>start</code> as the root we want to search from, and setting the LCA
     * of each pair in its LCA field.
     * 
     * @param start the root of the subtree
     * @param lrr a list of requests-response objects. The answer if stored on these objects at the
     *        LCA field.
     * @return the LCMs
     */
  public List<V> calculate(V start, List<LcaRequestResponse<V>> lrr) {
    return new Worker(lrr).calculate(start);
  }

  private class Worker {
    private UnionFind<V> uf = new UnionFind<>(Collections.<V>emptySet());

    private Map<V, V> ancestors = new HashMap<>();

    private Set<V> black = new HashSet<>();

    private List<LcaRequestResponse<V>> lrr;

    private MultiMap<V> lrrMap;

    private Worker(List<LcaRequestResponse<V>> lrr) {
      this.lrr = lrr;
      this.lrrMap = new MultiMap<>();
      for (LcaRequestResponse<V> r : lrr) {
        lrrMap.getOrCreate(r.getA()).add(r);
        lrrMap.getOrCreate(r.getB()).add(r);
      }
    }

    /**
         * Calculates the LCM as described by
         * http://en.wikipedia.org/wiki/Tarjan's_off-line_lowest_common_ancestors_algorithm
         * <code>function TarjanOLCA(u) MakeSet(u); u.ancestor := u; for each v
         * in u.children do TarjanOLCA(v); Union(u,v); Find(u).ancestor := u;
         * u.colour := black; for each v such that {u,v} in P do if v.colour ==
         * black print "Tarjan's Lowest Common Ancestor of " + u + " and " + v +
         * " is " + Find(v).ancestor + ".";</code>
         *
         * @param u the starting node (called recursively)
         *
         * @return the LCM if found, if not null
         */
    private List<V> calculate(final V u) {
      uf.addElement(u);
      ancestors.put(u, u);
      for (E vEdge : g.edgesOf(u)) {
        if (g.getEdgeSource(vEdge).equals(u)) {
          V v = g.getEdgeTarget(vEdge);
          calculate(v);
          uf.union(u, v);
          ancestors.put(uf.find(u), u);
        }
      }
      black.add(u);
      Set<LcaRequestResponse<V>> requestsForNodeU = lrrMap.get(u);
      if (requestsForNodeU != null) {
        for (LcaRequestResponse<V> rr : requestsForNodeU) {
          if (black.contains(rr.getB()) && rr.getA().equals(u)) {
            rr.setLca(ancestors.get(uf.find(rr.getB())));
          }
          if (black.contains(rr.getA()) && rr.getB().equals(u)) {
            rr.setLca(ancestors.get(uf.find(rr.getA())));
          }
        }
        lrrMap.remove(u);
      }
      List<V> result = new LinkedList<>();
      for (LcaRequestResponse<V> current : lrr) {
        result.add(current.getLca());
      }
      return result;
    }
  }

  public static class LcaRequestResponse<V extends java.lang.Object> {
    private V a, b, lca;

    /**
         * Create a new LCA request response data transfer object.
         * 
         * @param a the first vertex of the request
         * @param b the second vertex of the request
         */
    public LcaRequestResponse(V a, V b) {
      this.a = a;
      this.b = b;
    }

    /**
         * Get the first vertex of the request
         * 
         * @return the first vertex of the request
         */
    public V getA() {
      return a;
    }

    /**
         * Get the second vertex of the request
         * 
         * @return the second vertex of the request
         */
    public V getB() {
      return b;
    }

    /**
         * Get the least common ancestor
         * 
         * @return the least common ancestor
         */
    public V getLca() {
      return lca;
    }

    void setLca(V lca) {
      this.lca = lca;
    }
  }

  @SuppressWarnings(value = { "serial" }) private static final class MultiMap<V extends java.lang.Object> extends HashMap<V, Set<LcaRequestResponse<V>>> {
    public Set<LcaRequestResponse<V>> getOrCreate(V key) {
      if (!containsKey(key)) {
        put(key, new HashSet<>());
      }
      return get(key);
    }
  }
}