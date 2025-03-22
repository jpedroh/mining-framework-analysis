package org.jgrapht.alg;
import java.util.*;
import junit.framework.*;
import org.jgrapht.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;

/**
 * @author Tom Larkworthy
 * @version $Id: FloydWarshallShortestPathsTest.java 715 2010-06-13 01:25:00Z perfecthash $
 */
public class FloydWarshallShortestPathsTest extends TestCase {
  public void testCompareWithDijkstra() {
    RandomGraphGenerator<Integer, DefaultWeightedEdge> gen = new RandomGraphGenerator<>(10, 15);
    VertexFactory<Integer> f = new VertexFactory<Integer>() {
      int gid;

      @Override public Integer createVertex() {
        return gid++;
      }
    };
    for (int i = 0; i < 10; i++) {
      SimpleDirectedGraph<Integer, DefaultWeightedEdge> directed = new SimpleDirectedGraph<>(DefaultWeightedEdge.class);
      gen.generateGraph(directed, f, new HashMap<>());
      FloydWarshallShortestPaths<Integer, DefaultWeightedEdge> fw = new FloydWarshallShortestPaths<>(directed);
      for (Integer v1 : directed.vertexSet()) {
        for (Integer v2 : directed.vertexSet()) {
          double fwSp = fw.shortestDistance(v1, v2);
          double dijSp = new DijkstraShortestPath<>(directed, v1, v2).getPathLength();
          assertTrue((Math.abs(dijSp - fwSp) < .01) || (Double.isInfinite(fwSp) && Double.isInfinite(dijSp)));
          GraphPath<Integer, DefaultWeightedEdge> path = fw.getShortestPath(v1, v2);
          if (path != null) {
            this.verifyPath(directed, path, fw.shortestDistance(v1, v2));
          }
        }
      }
      SimpleGraph<Integer, DefaultWeightedEdge> undirected = new SimpleGraph<>(DefaultWeightedEdge.class);
      gen.generateGraph(undirected, f, new HashMap<>());
      fw = new FloydWarshallShortestPaths<>(undirected);
      for (Integer v1 : undirected.vertexSet()) {
        for (Integer v2 : undirected.vertexSet()) {
          double fwSp = fw.shortestDistance(v1, v2);
          double dijSp = new DijkstraShortestPath<>(undirected, v1, v2).getPathLength();
          assertTrue((Math.abs(dijSp - fwSp) < .01) || (Double.isInfinite(fwSp) && Double.isInfinite(dijSp)));
          GraphPath<Integer, DefaultWeightedEdge> path = fw.getShortestPath(v1, v2);
          if (path != null) {
            this.verifyPath(undirected, path, fw.shortestDistance(v1, v2));
            List<Integer> vertexPath = Graphs.getPathVertexList(path);
            assertEquals(fw.getFirstHop(v1, v2), vertexPath.get(1));
            assertEquals(fw.getLastHop(v1, v2), vertexPath.get(vertexPath.size() - 2));
          }
        }
      }
    }
  }

  /**
     * Verify whether the path calculated by FloydWarshallShortestPaths is an actual valid path.
     */
  private <V extends java.lang.Object, E extends java.lang.Object> void verifyPath(Graph<V, E> graph, GraphPath<V, E> path, double pathCost) {
    assertEquals(pathCost, path.getWeight(), .00000001);
    double verifiedEdgeCost = 0;
    List<V> vertexList = new ArrayList<>();
    vertexList.add(path.getStartVertex());
    V v = path.getStartVertex();
    for (E e : path.getEdgeList()) {
      assertNotNull(e);
      verifiedEdgeCost += graph.getEdgeWeight(e);
      try {
        v = Graphs.getOppositeVertex(graph, e, v);
      } catch (IllegalArgumentException ex) {
        fail("Invalid path encountered: the sequence of edges does not present a valid path through the graph");
      }
    }
    assertEquals(pathCost, verifiedEdgeCost, .00000001);
    assertEquals(path.getStartVertex(), path.getVertexList().get(0));
    assertEquals(path.getEndVertex(), path.getVertexList().get(path.getLength()));
  }

  private static UndirectedGraph<String, DefaultEdge> createStringGraph() {
    UndirectedGraph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
    String v1 = "v1";
    String v2 = "v2";
    String v3 = "v3";
    String v4 = "v4";
    g.addVertex(v1);
    g.addVertex(v2);
    g.addVertex(v3);
    g.addVertex(v4);
    g.addEdge(v1, v2);
    g.addEdge(v2, v3);
    g.addEdge(v3, v1);
    g.addEdge(v3, v4);
    return g;
  }

  public void testDiameter() {
    UndirectedGraph<String, DefaultEdge> stringGraph = createStringGraph();
    FloydWarshallShortestPaths<String, DefaultEdge> testFWPath = new FloydWarshallShortestPaths<>(stringGraph);
    double diameter = testFWPath.getDiameter();
    assertEquals(2.0, diameter);
  }

  public void testEmptyDiameter() {
    DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    FloydWarshallShortestPaths<String, DefaultEdge> fw = new FloydWarshallShortestPaths<>(graph);
    double diameter = fw.getDiameter();
    assertEquals(0.0, diameter);
  }

  public void testEdgeLessDiameter() {
    DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    String a = "a", b = "b";
    graph.addVertex(a);
    graph.addVertex(b);
    FloydWarshallShortestPaths<String, DefaultEdge> fw = new FloydWarshallShortestPaths<>(graph);
    double diameter = fw.getDiameter();
    assertEquals(0.0, diameter);
    assertNull(fw.getFirstHop(a, b));
    assertNull(fw.getLastHop(a, b));
  }

  public void testWeightedEdges() {
    SimpleDirectedGraph<String, DefaultWeightedEdge> weighted = new SimpleDirectedGraph<>(DefaultWeightedEdge.class);
    weighted.addVertex("a");
    weighted.addVertex("b");
    DefaultWeightedEdge edge = weighted.addEdge("a", "b");
    weighted.setEdgeWeight(edge, 5.0);
    FloydWarshallShortestPaths<String, DefaultWeightedEdge> fw = new FloydWarshallShortestPaths<>(weighted);
    double sD = fw.shortestDistance("a", "b");
    assertEquals(5.0, sD, 0.1);
    GraphPath<String, DefaultWeightedEdge> path = fw.getShortestPath("a", "b");
    assertNotNull(path);
    assertEquals(Collections.singletonList(edge), path.getEdgeList());
    assertEquals("a", path.getStartVertex());
    assertEquals("b", path.getEndVertex());
    assertEquals(5.0, path.getWeight());
    assertEquals(weighted, path.getGraph());
    assertNull(fw.getShortestPath("b", "a"));
    List<String> vertexPath = Graphs.getPathVertexList(path);
    assertEquals(fw.getFirstHop("a", "b"), vertexPath.get(1));
    assertEquals(fw.getLastHop("a", "b"), vertexPath.get(vertexPath.size() - 2));
  }
}