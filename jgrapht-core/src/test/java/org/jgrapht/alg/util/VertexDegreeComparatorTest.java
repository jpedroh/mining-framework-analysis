package org.jgrapht.alg.util;
import java.util.*;
import org.jgrapht.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;
import junit.framework.*;

/**
 * Unit tests for VertexDegreeComparator
 *
 * @author Joris Kinable
 */
public class VertexDegreeComparatorTest extends TestCase {
  protected static final int TEST_REPEATS = 20;

  private GraphGenerator<Integer, DefaultEdge, Integer> randomGraphGenerator;

  public VertexDegreeComparatorTest() {
    randomGraphGenerator = new GnmRandomGraphGenerator<>(100, 1000, 0);
  }

  public void testVertexDegreeComparator() {
    for (int repeat = 0; repeat < TEST_REPEATS; repeat++) {
      Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
      randomGraphGenerator.generateGraph(graph, new IntegerVertexFactory(), new HashMap<>());
      List<Integer> vertices = new ArrayList<>(graph.vertexSet());
      Collections.sort(vertices, new VertexDegreeComparator<>(graph, VertexDegreeComparator.Order.ASCENDING));
      for (int i = 0; i < vertices.size() - 1; i++) {
        assertTrue(graph.degreeOf(vertices.get(i)) <= graph.degreeOf(vertices.get(i + 1)));
      }
      Collections.sort(vertices, new VertexDegreeComparator<>(graph, VertexDegreeComparator.Order.DESCENDING));
      for (int i = 0; i < vertices.size() - 1; i++) {
        assertTrue(graph.degreeOf(vertices.get(i)) >= graph.degreeOf(vertices.get(i + 1)));
      }
    }
  }
}