package org.jgrapht.graph;

import org.jgrapht.Graph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SerializationTestUtils
{
    private static String v1 = "v1";
    private static String v2 = "v2";
    private static String v3 = "v3";
    private static final List<String> vertexList = Arrays.asList(v1, v2, v3);

    public static String getV1()
    {
        return v1;
    }

    public static String getV2()
    {
        return v2;
    }

    public static String getV3()
    {
        return v3;
    }

    public static List<String> getVertexList()
    {
        return vertexList;
    }

    public static Object serializeAndDeserialize(Object obj)
        throws Exception
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);

        out.writeObject(obj);
        out.flush();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bin);

        obj = in.readObject();
        return obj;
    }

    public static void addAllVertices(Graph<String, DefaultEdge> graph, List<String> vertices)
    {
        for (String v : vertices) {
            graph.addVertex(v);
        }
    }

    public static <V, E> void assertContainsAllVertices(Graph<V, E> graph, List<V> vertices)
    {
        for (V v : vertices) {
            assertTrue(graph.containsVertex(v));
        }
    }

    public static <V, E> void checkEdgesOf(Graph<V, E> graph, List<Integer> edges, List<V> vertices)
    {
        if (edges.size() != vertices.size()) {
            throw new IllegalArgumentException(
                "the size of list of #edges and vertices should match");
        }
        for (int i = 0; i < edges.size(); i++) {
            assertEquals(edges.get(i).intValue(), graph.edgesOf(vertices.get(i)).size());
        }
    }
}
