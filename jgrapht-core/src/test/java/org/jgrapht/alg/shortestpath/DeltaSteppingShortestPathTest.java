package org.jgrapht.alg.shortestpath;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;


public class DeltaSteppingShortestPathTest {
    @Test
    public void test1() {
        Graph<String, DefaultWeightedEdge> graph = new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);

        String s = "s";
        String t = "t";
        String y = "y";
        String x = "x";
        String z = "z";

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

        ShortestPathAlgorithm.SingleSourcePaths<String, DefaultWeightedEdge> paths =
                new DeltaSteppingShortestPath<>(graph, 1.0, false).getPaths(s);

        assertEquals(0d, paths.getWeight(s),1e-9);
        assertEquals(8d, paths.getWeight(t),1e-9);
        assertEquals(5d, paths.getWeight(y),1e-9);
        assertEquals(9d, paths.getWeight(x),1e-9);
        assertEquals(7d, paths.getWeight(z),1e-9);

        ShortestPathAlgorithm.SingleSourcePaths<String, DefaultWeightedEdge> deltaPaths =
                new DeltaSteppingShortestPath<>(graph, 10.0, false).getPaths(s);

        assertEquals(0d, deltaPaths.getWeight(s),1e-9);
        assertEquals(8d, deltaPaths.getWeight(t),1e-9);
        assertEquals(5d, deltaPaths.getWeight(y),1e-9);
        assertEquals(9d, deltaPaths.getWeight(x),1e-9);
        assertEquals(7d, deltaPaths.getWeight(z),1e-9);
    }
}