package org.jgrapht.alg.shortestpath;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;


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
    }
}