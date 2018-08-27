/*
 * (C) Copyright 2018-2018, by Semen Chudakov and Contributors.
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

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.util.SupplierUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test case for {@link DeltaSteppingShortestPath}.
 *
 * @author Semen Chudakov
 */
public class DeltaSteppingShortestPathTest {

    private static final String s = "s";
    private static final String t = "t";
    private static final String y = "y";
    private static final String x = "x";
    private static final String z = "z";

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Test
    public void testEmptyGraph() {
        Graph<String, DefaultWeightedEdge> graph = new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);
        graph.addVertex(s);

        new DeltaSteppingShortestPath<>(graph).getPaths(s);
    }

    @Test
    public void testNegativeWeightEdge() {
        Graph<String, DefaultWeightedEdge> graph = new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);
        Graphs.addAllVertices(graph, Arrays.asList(s, t));
        Graphs.addEdge(graph, s, t, -10.0);

        exception.expect(IllegalArgumentException.class);
        new DeltaSteppingShortestPath<>(graph).getPaths(s);
    }

    @Test
    public void testGetPath() {
        Graph<String, DefaultWeightedEdge> graph = create();

        assertEquals(Arrays.asList(s), new DeltaSteppingShortestPath<>(graph).getPath(s, s).getVertexList());
        assertEquals(Arrays.asList(s, y, t), new DeltaSteppingShortestPath<>(graph).getPath(s, t).getVertexList());
        assertEquals(Arrays.asList(s, y, t, x), new DeltaSteppingShortestPath<>(graph).getPath(s, x).getVertexList());
        assertEquals(Arrays.asList(s, y), new DeltaSteppingShortestPath<>(graph).getPath(s, y).getVertexList());
        assertEquals(Arrays.asList(s, y, z), new DeltaSteppingShortestPath<>(graph).getPath(s, z).getVertexList());
    }

    @Test
    public void testGetPaths1() {
        Graph<String, DefaultWeightedEdge> graph = create();

        ShortestPathAlgorithm.SingleSourcePaths<String, DefaultWeightedEdge> paths1 =
                new DeltaSteppingShortestPath<>(graph, 0.999).getPaths(s);

        assertEquals(0d, paths1.getWeight(s), 1e-9);
        assertEquals(8d, paths1.getWeight(t), 1e-9);
        assertEquals(5d, paths1.getWeight(y), 1e-9);
        assertEquals(9d, paths1.getWeight(x), 1e-9);
        assertEquals(7d, paths1.getWeight(z), 1e-9);

        ShortestPathAlgorithm.SingleSourcePaths<String, DefaultWeightedEdge> paths2 =
                new DeltaSteppingShortestPath<>(graph, 5.0).getPaths(s);

        assertEquals(0d, paths2.getWeight(s), 1e-9);
        assertEquals(8d, paths2.getWeight(t), 1e-9);
        assertEquals(5d, paths2.getWeight(y), 1e-9);
        assertEquals(9d, paths2.getWeight(x), 1e-9);
        assertEquals(7d, paths2.getWeight(z), 1e-9);

        ShortestPathAlgorithm.SingleSourcePaths<String, DefaultWeightedEdge> path3 =
                new DeltaSteppingShortestPath<>(graph, 11.0).getPaths(s);

        assertEquals(0d, path3.getWeight(s), 1e-9);
        assertEquals(8d, path3.getWeight(t), 1e-9);
        assertEquals(5d, path3.getWeight(y), 1e-9);
        assertEquals(9d, path3.getWeight(x), 1e-9);
        assertEquals(7d, path3.getWeight(z), 1e-9);

        ShortestPathAlgorithm.SingleSourcePaths<String, DefaultWeightedEdge> path4 =
                new DeltaSteppingShortestPath<>(graph).getPaths(s);

        assertEquals(0d, path4.getWeight(s), 1e-9);
        assertEquals(8d, path4.getWeight(t), 1e-9);
        assertEquals(5d, path4.getWeight(y), 1e-9);
        assertEquals(9d, path4.getWeight(x), 1e-9);
        assertEquals(7d, path4.getWeight(z), 1e-9);
    }

    @Test
    public void testGetPaths2() {
        int n = 1000;
        int p = 100;
        int numOfIterations = 100;
        int source = 0;

        GraphGenerator<Integer, DefaultWeightedEdge, Integer> generator =
                new GnpRandomGraphGenerator<>(n, p / n);
        DirectedWeightedPseudograph<Integer, DefaultWeightedEdge> graph;
        for (int i = 0; i < numOfIterations; i++) {
            graph = new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);
            graph.setVertexSupplier(SupplierUtil.createIntegerSupplier());
            generator.generateGraph(graph);

            ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> deltaSteppingShortestPaths =
                    new DeltaSteppingShortestPath<>(graph).getPaths(source);
            ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> dijkstraShortestPaths =
                    new DijkstraShortestPath<>(graph).getPaths(source);
            assertEqualPaths(deltaSteppingShortestPaths, dijkstraShortestPaths, graph.vertexSet());
        }
    }

    private Graph<String, DefaultWeightedEdge> create() {
        Graph<String, DefaultWeightedEdge> graph = new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(graph, Arrays.asList(s, t, y, x, z));

        Graphs.addEdge(graph, s, t, 10);
        Graphs.addEdge(graph, s, y, 5);

        Graphs.addEdge(graph, t, y, 2);
        Graphs.addEdge(graph, t, x, 1);

        Graphs.addEdge(graph, y, t, 3);
        Graphs.addEdge(graph, y, z, 2);
        Graphs.addEdge(graph, y, x, 9);

        Graphs.addEdge(graph, x, z, 4);

        Graphs.addEdge(graph, z, x, 6);
        Graphs.addEdge(graph, z, s, 7);

        return graph;
    }

    private void assertEqualPaths(ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> paths1,
                                  ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> paths2,
                                  Set<Integer> vertexSet) {
        for (Integer sink : vertexSet) {
            GraphPath<Integer, DefaultWeightedEdge> path1 = paths1.getPath(sink);
            GraphPath<Integer, DefaultWeightedEdge> path2 = paths2.getPath(sink);
            if(path1 == null){
                assertNull(path2);
            }else{
                assertEquals(paths1.getPath(sink).getEdgeList(), paths2.getPath(sink).getEdgeList());
            }
        }
    }
}