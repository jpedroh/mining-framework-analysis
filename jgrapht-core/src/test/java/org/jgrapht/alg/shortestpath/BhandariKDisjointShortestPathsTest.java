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

import static org.junit.Assert.*;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.graph.*;
import org.junit.*;

/**
 * 
 * Tests for the {@link BhandariKDisjointShortestPaths} class.
 * 
 * @author Assaf Mizrachi
 */
public class BhandariKDisjointShortestPathsTest extends KDisjointShortestPathsTestCase
{

    /**
     * Tests two joint paths from 1 to 4, negative edges exist in path.
     * 
     * Edges expected in path 1 --------------- {@literal 1 --> 2}, w=-1 {@literal 2 --> 6}, w=-3
     * {@literal 6 --> 4}, w= 3
     * 
     * Edges expected in path 2 --------------- {@literal 1 --> 5}, w=-2 {@literal 5 --> 3}, w= 2
     * {@literal 3 --> 4}, w=-1
     * 
     * Edges expected in no path --------------- {@literal 2 --> 3}, w=-1
     * 
     */
    @Test
    public void testTwoDisjointPathsNegative()
    {
        DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph =
            new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);

        DefaultWeightedEdge e12 = graph.addEdge(1, 2);
        // this edge should not be used
        DefaultWeightedEdge e23 = graph.addEdge(2, 3);
        DefaultWeightedEdge e34 = graph.addEdge(3, 4);
        DefaultWeightedEdge e15 = graph.addEdge(1, 5);
        DefaultWeightedEdge e53 = graph.addEdge(5, 3);
        DefaultWeightedEdge e26 = graph.addEdge(2, 6);
        DefaultWeightedEdge e64 = graph.addEdge(6, 4);

        graph.setEdgeWeight(e12, -20);
        graph.setEdgeWeight(e23, -1);
        graph.setEdgeWeight(e34, -10);
        graph.setEdgeWeight(e15, -2);
        graph.setEdgeWeight(e53, 2);
        graph.setEdgeWeight(e26, -3);
        graph.setEdgeWeight(e64, 3);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);

        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 4, 5);

        assertEquals(2, pathList.size());

        GraphPath<Integer, DefaultWeightedEdge> expectedP1 =
            new GraphWalk<>(graph, Arrays.asList(1, 2, 6, 4), -20);
        assertEquals(expectedP1, pathList.get(0));
        assertEquals(3, pathList.get(0).getLength());
        assertEquals(-20.0, pathList.get(0).getWeight(), 0.0);

        GraphPath<Integer, DefaultWeightedEdge> expectedP2 =
            new GraphWalk<>(graph, Arrays.asList(1, 5, 3, 4), -10);
        assertEquals(expectedP2, pathList.get(1));
        assertEquals(3, pathList.get(1).getLength());
        assertEquals(-10.0, pathList.get(1).getWeight(), 0.0);
    }


    @Override
    protected <V, E> KShortestPathAlgorithm<V, E> getKShortestPathAlgorithm(Graph<V, E> graph)
    {
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/1d1ee81e47c5d22b559dadc871138d181fd665d7/jgrapht-core/src/test/java/org/jgrapht/alg/shortestpath/BhandariKDisjointShortestPathsTest.java/left.java
        return new BhandariKDisjointShortestPaths<>(graph);
||||||| /usr/src/app/output/jgrapht/jgrapht/1d1ee81e47c5d22b559dadc871138d181fd665d7/jgrapht-core/src/test/java/org/jgrapht/alg/shortestpath/BhandariKDisjointShortestPathsTest.java/base.java
        DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph =
            new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);

        DefaultWeightedEdge e12 = graph.addEdge(1, 2);
        // this edge should not be used
        DefaultWeightedEdge e23 = graph.addEdge(2, 3);
        DefaultWeightedEdge e34 = graph.addEdge(3, 4);
        DefaultWeightedEdge e15 = graph.addEdge(1, 5);
        DefaultWeightedEdge e53 = graph.addEdge(5, 3);
        DefaultWeightedEdge e26 = graph.addEdge(2, 6);
        DefaultWeightedEdge e64 = graph.addEdge(6, 4);

        DefaultWeightedEdge e21 = graph.addEdge(2, 1);
        // this edge should not be used
        DefaultWeightedEdge e32 = graph.addEdge(3, 2);
        DefaultWeightedEdge e43 = graph.addEdge(4, 3);
        DefaultWeightedEdge e51 = graph.addEdge(5, 1);
        DefaultWeightedEdge e35 = graph.addEdge(3, 5);
        DefaultWeightedEdge e62 = graph.addEdge(6, 2);
        DefaultWeightedEdge e46 = graph.addEdge(4, 6);

        graph.setEdgeWeight(e12, 1);
        graph.setEdgeWeight(e23, 1);
        graph.setEdgeWeight(e34, 1);
        graph.setEdgeWeight(e15, 3);
        graph.setEdgeWeight(e53, 3);
        graph.setEdgeWeight(e26, 2);
        graph.setEdgeWeight(e64, 2);

        graph.setEdgeWeight(e21, 1);
        graph.setEdgeWeight(e32, 1);
        graph.setEdgeWeight(e43, 1);
        graph.setEdgeWeight(e51, 3);
        graph.setEdgeWeight(e35, 3);
        graph.setEdgeWeight(e62, 2);
        graph.setEdgeWeight(e46, 2);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);

        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 4, 5);

        assertEquals(2, pathList.size());

        GraphPath<Integer, DefaultWeightedEdge> expectedP1 =
            new GraphWalk<>(graph, Arrays.asList(1, 2, 6, 4), 5);
        assertEquals(expectedP1, pathList.get(0));
        assertEquals(3, pathList.get(0).getLength());
        assertEquals(5.0, pathList.get(0).getWeight(), 0.0);

        GraphPath<Integer, DefaultWeightedEdge> expectedP2 =
            new GraphWalk<>(graph, Arrays.asList(1, 5, 3, 4), 7);
        assertEquals(expectedP2, pathList.get(1));
        assertEquals(3, pathList.get(1).getLength());
        assertEquals(7.0, pathList.get(1).getWeight(), 0.0);
    }

    /**
     * Tests three joint paths from 1 to 5 Edges expected in path 1 ---------------
     * {@literal 1 --> 4}, w=4 {@literal 4 --> 5}, w=1
     * 
     * Edges expected in path 2 --------------- {@literal 1 --> 2}, w=1 {@literal 2 --> 5}, w=6
     * 
     * Edges expected in path 3 --------------- {@literal 1 --> 3}, w=4 {@literal 3 --> 5}, w=5
     * 
     * Edges expected in no path --------------- {@literal 2 --> 3}, w=1 {@literal 3 --> 4}, w=1
     */
    @Test
    public void testThreeDisjointPaths()
    {
        Graph<Integer, DefaultWeightedEdge> graph = createThreeDisjointPathsGraph();

        graph.getEdge(1, 2);
        graph.getEdge(2, 5);
        graph.getEdge(1, 3);
        graph.getEdge(3, 5);
        graph.getEdge(1, 4);
        graph.getEdge(4, 5);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);

        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 5, 5);

        assertEquals(3, pathList.size());

        GraphPath<Integer, DefaultWeightedEdge> expectedP1 =
            new GraphWalk<>(graph, Arrays.asList(1, 4, 5), 5);
        assertEquals(expectedP1, pathList.get(0));
        assertEquals(2, pathList.get(0).getLength());
        assertEquals(5.0, pathList.get(0).getWeight(), 0.0);

        GraphPath<Integer, DefaultWeightedEdge> expectedP2 =
            new GraphWalk<>(graph, Arrays.asList(1, 2, 5), 7);
        assertEquals(expectedP2, pathList.get(1));
        assertEquals(2, pathList.get(1).getLength());
        assertEquals(7.0, pathList.get(1).getWeight(), 0.0);

        GraphPath<Integer, DefaultWeightedEdge> expectedP3 =
            new GraphWalk<>(graph, Arrays.asList(1, 3, 5), 9);
        assertEquals(expectedP3, pathList.get(2));
        assertEquals(2, pathList.get(2).getLength());
        assertEquals(9.0, pathList.get(2).getWeight(), 0.0);

    }

    private Graph<Integer, DefaultWeightedEdge> createThreeDisjointPathsGraph()
    {
        DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph =
            new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);

        DefaultWeightedEdge e12 = graph.addEdge(1, 2);
        DefaultWeightedEdge e25 = graph.addEdge(2, 5);
        DefaultWeightedEdge e13 = graph.addEdge(1, 3);
        DefaultWeightedEdge e35 = graph.addEdge(3, 5);
        DefaultWeightedEdge e14 = graph.addEdge(1, 4);
        DefaultWeightedEdge e45 = graph.addEdge(4, 5);
        DefaultWeightedEdge e23 = graph.addEdge(2, 3);
        DefaultWeightedEdge e34 = graph.addEdge(3, 4);

        graph.setEdgeWeight(e12, 1);
        graph.setEdgeWeight(e25, 6);
        graph.setEdgeWeight(e13, 4);
        graph.setEdgeWeight(e35, 5);
        graph.setEdgeWeight(e14, 4);
        graph.setEdgeWeight(e45, 1);
        graph.setEdgeWeight(e23, 1);
        graph.setEdgeWeight(e34, 1);

        return graph;
    }

    @Test
    public void testGraphIsNotChanged()
    {
        Graph<Integer, DefaultWeightedEdge> source = createThreeDisjointPathsGraph();
        Graph<Integer, DefaultWeightedEdge> destination = new DefaultDirectedWeightedGraph<>(
            source.getVertexSupplier(), source.getEdgeSupplier());
        Graphs.addGraph(destination, source);

        Map<DefaultWeightedEdge, Double> originalWeightMap = new HashMap<>();
        for (DefaultWeightedEdge e : source.edgeSet()) {
            originalWeightMap.put(e, source.getEdgeWeight(e));
        }

        new BhandariKDisjointShortestPaths<>(source).getPaths(1, 5, 5);

        assertEquals(destination, source);

        Map<DefaultWeightedEdge, Double> weightMap = new HashMap<>();
        for (DefaultWeightedEdge e : source.edgeSet()) {
            weightMap.put(e, source.getEdgeWeight(e));
        }

        assertEquals(originalWeightMap, weightMap);
    }

    /**
     * Only single disjoint path should exist on the line
     */
    @Test
    public void testLinear()
    {
        Graph<Integer,
            DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(
                SupplierUtil.createIntegerSupplier(1),
                SupplierUtil.createDefaultWeightedEdgeSupplier());
        GraphGenerator<Integer, DefaultWeightedEdge, Integer> graphGenerator =
            new LinearGraphGenerator<>(20);
        graphGenerator.generateGraph(graph);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);
        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 20, 2);

        assertEquals(1, pathList.size());
        assertEquals(19, pathList.get(0).getLength());
        assertEquals(19.0, pathList.get(0).getWeight(), 0.0);

        for (int i = 1; i < 21; i++) {
            assertTrue(pathList.get(0).getVertexList().contains(i));
        }
    }

    /**
     * Exactly single disjoint path should exist on the ring
     */
    @Test
    public void testRing()
    {
        Graph<Integer,
            DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(
                SupplierUtil.createIntegerSupplier(1),
                SupplierUtil.createDefaultWeightedEdgeSupplier());
        GraphGenerator<Integer, DefaultWeightedEdge, Integer> graphGenerator =
            new RingGraphGenerator<>(20);
        graphGenerator.generateGraph(graph);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);
        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 10, 2);

        assertEquals(1, pathList.size());
        assertEquals(9, pathList.get(0).getLength());
        assertEquals(9.0, pathList.get(0).getWeight(), 0.0);

        for (int i = 1; i < 10; i++) {
            assertTrue(pathList.get(0).getVertexList().contains(i));
        }
    }

    /**
     * Exactly single disjoint path should exist on the ring
     */
    @Test
    public void testClique()
    {
        Graph<Integer,
            DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(
                SupplierUtil.createIntegerSupplier(1),
                SupplierUtil.createDefaultWeightedEdgeSupplier());
        GraphGenerator<Integer, DefaultWeightedEdge, Integer> graphGenerator =
            new CompleteGraphGenerator<>(20);
        graphGenerator.generateGraph(graph);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);

        for (int i = 2; i < 20; i++) {
            List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, i, 2);
            assertEquals(2, pathList.size());
        }
    }

    /**
     * Exactly single disjoint path should exist on the ring
     */
    @Test
    public void testStar()
    {
        Graph<Integer,
            DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(
                SupplierUtil.createIntegerSupplier(1),
                SupplierUtil.createDefaultWeightedEdgeSupplier());
        GraphGenerator<Integer, DefaultWeightedEdge, Integer> graphGenerator =
            new StarGraphGenerator<>(20);
        graphGenerator.generateGraph(graph);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);

        for (int i = 2; i < 20; i++) {
            List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(i, 1, 2);
            assertEquals(1, pathList.size());
        }
=======
        DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph =
            new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);

        DefaultWeightedEdge e12 = graph.addEdge(1, 2);
        // this edge should not be used
        DefaultWeightedEdge e23 = graph.addEdge(2, 3);
        DefaultWeightedEdge e34 = graph.addEdge(3, 4);
        DefaultWeightedEdge e15 = graph.addEdge(1, 5);
        DefaultWeightedEdge e53 = graph.addEdge(5, 3);
        DefaultWeightedEdge e26 = graph.addEdge(2, 6);
        DefaultWeightedEdge e64 = graph.addEdge(6, 4);

        DefaultWeightedEdge e21 = graph.addEdge(2, 1);
        // this edge should not be used
        DefaultWeightedEdge e32 = graph.addEdge(3, 2);
        DefaultWeightedEdge e43 = graph.addEdge(4, 3);
        DefaultWeightedEdge e51 = graph.addEdge(5, 1);
        DefaultWeightedEdge e35 = graph.addEdge(3, 5);
        DefaultWeightedEdge e62 = graph.addEdge(6, 2);
        DefaultWeightedEdge e46 = graph.addEdge(4, 6);

        graph.setEdgeWeight(e12, 1);
        graph.setEdgeWeight(e23, 1);
        graph.setEdgeWeight(e34, 1);
        graph.setEdgeWeight(e15, 3);
        graph.setEdgeWeight(e53, 3);
        graph.setEdgeWeight(e26, 2);
        graph.setEdgeWeight(e64, 2);

        graph.setEdgeWeight(e21, 1);
        graph.setEdgeWeight(e32, 1);
        graph.setEdgeWeight(e43, 1);
        graph.setEdgeWeight(e51, 3);
        graph.setEdgeWeight(e35, 3);
        graph.setEdgeWeight(e62, 2);
        graph.setEdgeWeight(e46, 2);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);

        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 4, 5);

        assertEquals(2, pathList.size());

        GraphPath<Integer, DefaultWeightedEdge> expectedP1 =
            new GraphWalk<>(graph, Arrays.asList(1, 2, 6, 4), 5);
        assertEquals(expectedP1, pathList.get(0));
        assertEquals(3, pathList.get(0).getLength());
        assertEquals(5.0, pathList.get(0).getWeight(), 0.0);

        GraphPath<Integer, DefaultWeightedEdge> expectedP2 =
            new GraphWalk<>(graph, Arrays.asList(1, 5, 3, 4), 7);
        assertEquals(expectedP2, pathList.get(1));
        assertEquals(3, pathList.get(1).getLength());
        assertEquals(7.0, pathList.get(1).getWeight(), 0.0);
    }

    /**
     * Tests three joint paths from 1 to 5. 
     * <p>
     * Edges expected in path 1 --------------- {@literal 1 --> 4}, w=4 {@literal 4 --> 5}, w=1
     * <p>
     * Edges expected in path 2 --------------- {@literal 1 --> 2}, w=1 {@literal 2 --> 5}, w=6
     * <p>
     * Edges expected in path 3 --------------- {@literal 1 --> 3}, w=4 {@literal 3 --> 5}, w=5
     * <p>
     * Edges expected in no path --------------- {@literal 2 --> 3}, w=1 {@literal 3 --> 4}, w=1
     */
    @Test
    public void testThreeDisjointPaths()
    {
        Graph<Integer, DefaultWeightedEdge> graph = createThreeDisjointPathsGraph();

        graph.getEdge(1, 2);
        graph.getEdge(2, 5);
        graph.getEdge(1, 3);
        graph.getEdge(3, 5);
        graph.getEdge(1, 4);
        graph.getEdge(4, 5);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);

        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 5, 5);

        assertEquals(3, pathList.size());

        GraphPath<Integer, DefaultWeightedEdge> expectedP1 =
            new GraphWalk<>(graph, Arrays.asList(1, 4, 5), 5);
        assertEquals(expectedP1, pathList.get(0));
        assertEquals(2, pathList.get(0).getLength());
        assertEquals(5.0, pathList.get(0).getWeight(), 0.0);

        GraphPath<Integer, DefaultWeightedEdge> expectedP2 =
            new GraphWalk<>(graph, Arrays.asList(1, 2, 5), 7);
        assertEquals(expectedP2, pathList.get(1));
        assertEquals(2, pathList.get(1).getLength());
        assertEquals(7.0, pathList.get(1).getWeight(), 0.0);

        GraphPath<Integer, DefaultWeightedEdge> expectedP3 =
            new GraphWalk<>(graph, Arrays.asList(1, 3, 5), 9);
        assertEquals(expectedP3, pathList.get(2));
        assertEquals(2, pathList.get(2).getLength());
        assertEquals(9.0, pathList.get(2).getWeight(), 0.0);

    }

    private Graph<Integer, DefaultWeightedEdge> createThreeDisjointPathsGraph()
    {
        DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph =
            new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);

        DefaultWeightedEdge e12 = graph.addEdge(1, 2);
        DefaultWeightedEdge e25 = graph.addEdge(2, 5);
        DefaultWeightedEdge e13 = graph.addEdge(1, 3);
        DefaultWeightedEdge e35 = graph.addEdge(3, 5);
        DefaultWeightedEdge e14 = graph.addEdge(1, 4);
        DefaultWeightedEdge e45 = graph.addEdge(4, 5);
        DefaultWeightedEdge e23 = graph.addEdge(2, 3);
        DefaultWeightedEdge e34 = graph.addEdge(3, 4);

        graph.setEdgeWeight(e12, 1);
        graph.setEdgeWeight(e25, 6);
        graph.setEdgeWeight(e13, 4);
        graph.setEdgeWeight(e35, 5);
        graph.setEdgeWeight(e14, 4);
        graph.setEdgeWeight(e45, 1);
        graph.setEdgeWeight(e23, 1);
        graph.setEdgeWeight(e34, 1);

        return graph;
    }

    @Test
    public void testGraphIsNotChanged()
    {
        Graph<Integer, DefaultWeightedEdge> source = createThreeDisjointPathsGraph();
        Graph<Integer, DefaultWeightedEdge> destination = new DefaultDirectedWeightedGraph<>(
            source.getVertexSupplier(), source.getEdgeSupplier());
        Graphs.addGraph(destination, source);

        Map<DefaultWeightedEdge, Double> originalWeightMap = new HashMap<>();
        for (DefaultWeightedEdge e : source.edgeSet()) {
            originalWeightMap.put(e, source.getEdgeWeight(e));
        }

        new BhandariKDisjointShortestPaths<>(source).getPaths(1, 5, 5);

        assertEquals(destination, source);

        Map<DefaultWeightedEdge, Double> weightMap = new HashMap<>();
        for (DefaultWeightedEdge e : source.edgeSet()) {
            weightMap.put(e, source.getEdgeWeight(e));
        }

        assertEquals(originalWeightMap, weightMap);
    }

    /**
     * Only single disjoint path should exist on the line
     */
    @Test
    public void testLinear()
    {
        Graph<Integer,
            DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(
                SupplierUtil.createIntegerSupplier(1),
                SupplierUtil.createDefaultWeightedEdgeSupplier());
        GraphGenerator<Integer, DefaultWeightedEdge, Integer> graphGenerator =
            new LinearGraphGenerator<>(20);
        graphGenerator.generateGraph(graph);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);
        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 20, 2);

        assertEquals(1, pathList.size());
        assertEquals(19, pathList.get(0).getLength());
        assertEquals(19.0, pathList.get(0).getWeight(), 0.0);

        for (int i = 1; i < 21; i++) {
            assertTrue(pathList.get(0).getVertexList().contains(i));
        }
    }

    /**
     * Exactly single disjoint path should exist on the ring
     */
    @Test
    public void testRing()
    {
        Graph<Integer,
            DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(
                SupplierUtil.createIntegerSupplier(1),
                SupplierUtil.createDefaultWeightedEdgeSupplier());
        GraphGenerator<Integer, DefaultWeightedEdge, Integer> graphGenerator =
            new RingGraphGenerator<>(20);
        graphGenerator.generateGraph(graph);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);
        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 10, 2);

        assertEquals(1, pathList.size());
        assertEquals(9, pathList.get(0).getLength());
        assertEquals(9.0, pathList.get(0).getWeight(), 0.0);

        for (int i = 1; i < 10; i++) {
            assertTrue(pathList.get(0).getVertexList().contains(i));
        }
    }

    /**
     * Exactly single disjoint path should exist on the ring
     */
    @Test
    public void testClique()
    {
        Graph<Integer,
            DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(
                SupplierUtil.createIntegerSupplier(1),
                SupplierUtil.createDefaultWeightedEdgeSupplier());
        GraphGenerator<Integer, DefaultWeightedEdge, Integer> graphGenerator =
            new CompleteGraphGenerator<>(20);
        graphGenerator.generateGraph(graph);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);

        for (int i = 2; i < 20; i++) {
            List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, i, 2);
            assertEquals(2, pathList.size());
        }
    }

    /**
     * Exactly single disjoint path should exist on the ring
     */
    @Test
    public void testStar()
    {
        Graph<Integer,
            DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(
                SupplierUtil.createIntegerSupplier(1),
                SupplierUtil.createDefaultWeightedEdgeSupplier());
        GraphGenerator<Integer, DefaultWeightedEdge, Integer> graphGenerator =
            new StarGraphGenerator<>(20);
        graphGenerator.generateGraph(graph);

        BhandariKDisjointShortestPaths<Integer, DefaultWeightedEdge> alg =
            new BhandariKDisjointShortestPaths<>(graph);

        for (int i = 2; i < 20; i++) {
            List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(i, 1, 2);
            assertEquals(1, pathList.size());
        }
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/1d1ee81e47c5d22b559dadc871138d181fd665d7/jgrapht-core/src/test/java/org/jgrapht/alg/shortestpath/BhandariKDisjointShortestPathsTest.java/right.java
    }
}
