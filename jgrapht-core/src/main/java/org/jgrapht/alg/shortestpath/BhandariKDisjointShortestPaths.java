/*
 * (C) Copyright 2018-2018, by Assaf Mizrachi and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.alg.shortestpath;
import org.jgrapht.alg.util.*;

import java.util.*;

import org.jgrapht.*;

/**
 * An implementation of Bhandari algorithm for finding $K$ edge-<em>disjoint</em> shortest paths.
 * The algorithm determines the $k$ edge-disjoint shortest simple paths in increasing order of
 * weight. Weights can be negative (but no negative cycle is allowed). Only directed simple graphs
 * are allowed.
 *
 * <p>
 * The algorithm is running $k$ sequential Bellman-Ford iterations to find the shortest path at each
 * step. Hence, yielding a complexity of $k$*O(Bellman-Ford).
 * 
 * <ul>
 * <li>Bhandari, Ramesh 1999. Survivable networks: algorithms for diverse routing. 477. Springer. p.
 * 46. ISBN 0-7923-8381-8.
 * <li>Iqbal, F. and Kuipers, F. A. 2015.
 * <a href="https://www.nas.ewi.tudelft.nl/people/Fernando/papers/Wiley.pdf"> Disjoint Paths in
 * Networks </a>. Wiley Encyclopedia of Electrical and Electronics Engineering. 1–11.
 * </ul>
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Assaf Mizrachi
 * @since February 12, 2018
 */
public class BhandariKDisjointShortestPaths<V, E> extends BaseKDisjointShortestPathsAlgorithm<V, E>
{
    /**
     * Creates a new instance of the algorithm.
     *
     * @param graph graph on which shortest paths are searched.
     *
     * @throws IllegalArgumentException if the graph is null.
     * @throws IllegalArgumentException if the graph is undirected.
     * @throws IllegalArgumentException if the graph is not simple.
     */
    public BhandariKDisjointShortestPaths(Graph<V, E> graph)
    {
        super(graph);
    }

    @Override
    protected void prepare(List<E> previousPath)
    {

        V source, target;
        E reversedEdge;

        // replace previous path edges with reversed edges with negative weight
        for (E originalEdge : previousPath) {
            source = workingGraph.getEdgeSource(originalEdge);
            target = workingGraph.getEdgeTarget(originalEdge);
            workingGraph.removeEdge(originalEdge);
            reversedEdge = workingGraph.addEdge(target, source);
            if (reversedEdge != null) {
                workingGraph.setEdgeWeight(reversedEdge, -workingGraph.getEdgeWeight(originalEdge));
            }
        }
    }

    @Override
    protected GraphPath<V, E> calculateShortestPath(V startVertex, V endVertex)
    {
        return new BellmanFordShortestPath<>(this.workingGraph).getPath(startVertex, endVertex);
    }

    /**
     * Iterate over all paths to remove overlapping edges (i.e. those edges contained in more than 
     * one path).
     * Two edges are considered as overlapping in case both edges connect the same vertex pair, 
     * disregarding direction.
     * At the end of this method, each path contains unique edges but not necessarily connecting the
     * start to end vertex.
     * 
     */
    private void findOverlappingEdges()
    {
        Map<UnorderedPair<V, V>, Integer> edgeOccurrenceCount = new HashMap<>();
        for (List<E> path : pathList) {
            for (E e : path) {                
                V v = this.workingGraph.getEdgeSource(e);
                V u = this.workingGraph.getEdgeTarget(e);                
                UnorderedPair<V, V> edgePair = new UnorderedPair<>(v, u);
                
                if (edgeOccurrenceCount.containsKey(edgePair)) {
                    edgeOccurrenceCount.put(edgePair, 2);
                } else {
                    edgeOccurrenceCount.put(edgePair, 1);
                }
            }
        }

        this.overlappingEdges = pathList.stream().flatMap(List::stream).filter(
            e -> edgeOccurrenceCount.get(new UnorderedPair<>(
                this.workingGraph.getEdgeSource(e), 
                this.workingGraph.getEdgeTarget(e))) > 1)
            .collect(Collectors.toSet());
    }

}
