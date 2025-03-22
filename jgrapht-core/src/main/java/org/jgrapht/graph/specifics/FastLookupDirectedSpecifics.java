package org.jgrapht.graph.specifics;
import java.util.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.*;

/**
 * Fast implementation of DirectedSpecifics. This class uses additional data structures to improve
 * the performance of methods which depend on edge retrievals, e.g. getEdge(V u, V v),
 * containsEdge(V u, V v),addEdge(V u, V v). A disadvantage is an increase in memory consumption. If
 * memory utilization is an issue, use a {@link DirectedSpecifics} instead.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Joris Kinable
 */
public class FastLookupDirectedSpecifics<V extends java.lang.Object, E extends java.lang.Object> extends DirectedSpecifics<V, E> {
  private static final long serialVersionUID = 4089085208843722263L;

  protected Map<Pair<V, V>, ArrayUnenforcedSet<E>> touchingVerticesToEdgeMap;

  /**
     * Construct a new fast lookup directed specifics.
     * 
     * @param abstractBaseGraph the graph for which these specifics are for
     */
  public FastLookupDirectedSpecifics(AbstractBaseGraph<V, E> abstractBaseGraph) {
    this(abstractBaseGraph, new LinkedHashMap<>(), new ArrayUnenforcedSetEdgeSetFactory<>());
  }

  /**
     * Construct a new fast lookup directed specifics.
     * 
     * @param abstractBaseGraph the graph for which these specifics are for
     * @param vertexMap map for the storage of vertex edge sets
     */
  public FastLookupDirectedSpecifics(AbstractBaseGraph<V, E> abstractBaseGraph, Map<V, DirectedEdgeContainer<V, E>> vertexMap) {
    this(abstractBaseGraph, vertexMap, new ArrayUnenforcedSetEdgeSetFactory<>());
  }

  /**
     * Construct a new fast lookup directed specifics.
     * 
     * @param abstractBaseGraph the graph for which these specifics are for
     * @param vertexMap a container to use the edges
     */
  public FastLookupDirectedSpecifics(AbstractBaseGraph<V, E> abstractBaseGraph, Map<V, DirectedEdgeContainer<V, E>> vertexMap, EdgeSetFactory<V, E> edgeSetFactory) {
    super(abstractBaseGraph, vertexMap, edgeSetFactory);
    this.touchingVerticesToEdgeMap = new HashMap<>();
  }

  /**
     * {@inheritDoc}
     */
  @Override public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
    if (abstractBaseGraph.containsVertex(sourceVertex) && abstractBaseGraph.containsVertex(targetVertex)) {
      Set<E> edges = touchingVerticesToEdgeMap.get(new Pair<>(sourceVertex, targetVertex));
      return edges == null ? Collections.emptySet() : new ArrayUnenforcedSet<>(edges);
    } else {
      return null;
    }
  }

  /**
     * {@inheritDoc}
     */
  @Override public E getEdge(V sourceVertex, V targetVertex) {
    List<E> edges = touchingVerticesToEdgeMap.get(new Pair<>(sourceVertex, targetVertex));
    if (edges == null || edges.isEmpty()) {
      return null;
    } else {
      return edges.get(0);
    }
  }

  /**
     * {@inheritDoc}
     */
  @Override public void addEdgeToTouchingVertices(E e) {
    V source = abstractBaseGraph.getEdgeSource(e);
    V target = abstractBaseGraph.getEdgeTarget(e);
    getEdgeContainer(source).addOutgoingEdge(e);
    getEdgeContainer(target).addIncomingEdge(e);
    Pair<V, V> vertexPair = new Pair<>(source, target);
    if (!touchingVerticesToEdgeMap.containsKey(vertexPair)) {
      ArrayUnenforcedSet<E> edgeSet = new ArrayUnenforcedSet<>();
      edgeSet.add(e);
      touchingVerticesToEdgeMap.put(vertexPair, edgeSet);
    } else {
      touchingVerticesToEdgeMap.get(vertexPair).add(e);
    }
  }

  /**
     * {@inheritDoc}
     */
  @Override public void removeEdgeFromTouchingVertices(E e) {
    V source = abstractBaseGraph.getEdgeSource(e);
    V target = abstractBaseGraph.getEdgeTarget(e);
    getEdgeContainer(source).removeOutgoingEdge(e);
    getEdgeContainer(target).removeIncomingEdge(e);
    Pair<V, V> vertexPair = new Pair<>(source, target);
    if (touchingVerticesToEdgeMap.containsKey(vertexPair)) {
      ArrayUnenforcedSet<E> edgeSet = touchingVerticesToEdgeMap.get(vertexPair);
      edgeSet.remove(e);
      if (edgeSet.isEmpty()) {
        touchingVerticesToEdgeMap.remove(vertexPair);
      }
    }
  }
}