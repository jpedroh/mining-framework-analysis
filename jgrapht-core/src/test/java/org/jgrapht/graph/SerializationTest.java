/*
 * (C) Copyright 2003-2018, by John V Sichi and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.graph;

import org.junit.Test;

import java.util.Arrays;

import static org.jgrapht.graph.SerializationTestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * SerializationTest tests serialization and deserialization of JGraphT objects.
 *
 * @author John V. Sichi
 */
public class SerializationTest
{
    /**
     * Tests serialization of DirectedMultigraph.
     */
    @SuppressWarnings("unchecked") @Test public void testSerialization_DirectedMultigraph()
        throws Exception
    {
        DirectedMultigraph<String, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);
        addAllVertices(graph, getVertexList());
        graph.addEdge(getV1(), getV2());
        graph.addEdge(getV2(), getV3());
        graph.addEdge(getV2(), getV3());

        graph = (DirectedMultigraph<String, DefaultEdge>) serializeAndDeserialize(graph);
        assertContainsAllVertices(graph, getVertexList());
        assertTrue(graph.containsEdge(getV1(), getV2()));
        assertTrue(graph.containsEdge(getV2(), getV3()));
        checkEdgesOf(graph, Arrays.asList(1, 3, 2), getVertexList());
    }

    /**
     * Tests serialization of DirectedAcyclicGraph
     */
    @SuppressWarnings("unchecked") @Test public void testSerialization_DirectedAcyclicGraph()
        throws Exception
    {
        DirectedAcyclicGraph<String, DefaultEdge> graph1 =
            new DirectedAcyclicGraph<>(DefaultEdge.class);
        addAllVertices(graph1, getVertexList());
        graph1.addEdge(getV1(), getV2());
        graph1.addEdge(getV2(), getV3());
        graph1.addEdge(getV1(), getV3());

        DirectedAcyclicGraph<String, DefaultEdge> graph2 =
            (DirectedAcyclicGraph<String, DefaultEdge>) serializeAndDeserialize(graph1);
        assertContainsAllVertices(graph2, getVertexList());
        assertTrue(graph2.containsEdge(getV1(), getV2()));
        assertTrue(graph2.containsEdge(getV2(), getV3()));
        assertTrue(graph2.containsEdge(getV1(), getV3()));
        checkEdgesOf(graph2, Arrays.asList(2, 2, 2), getVertexList());

        assertEquals(graph1.toString(), graph2.toString());
    }

    /**
     * Tests serialization of DefaultDirectedGraph.
     */
    @SuppressWarnings("unchecked") @Test public void testSerialization_DefaultDirectedGraph()
        throws Exception
    {
        DefaultDirectedGraph<String, DefaultEdge> graph1 =
            new DefaultDirectedGraph<>(DefaultEdge.class);
        addAllVertices(graph1, getVertexList());
        graph1.addEdge(getV1(), getV2());
        graph1.addEdge(getV2(), getV3());
        graph1.addEdge(getV3(), getV1()); // contains loop

        DefaultDirectedGraph<String, DefaultEdge> graph2 =
            (DefaultDirectedGraph<String, DefaultEdge>) serializeAndDeserialize(graph1);

        assertContainsAllVertices(graph2, getVertexList());
        assertTrue(graph2.containsEdge(getV1(), getV2()));
        assertTrue(graph2.containsEdge(getV2(), getV3()));
        assertTrue(graph2.containsEdge(getV3(), getV1()));
        checkEdgesOf(graph2, Arrays.asList(2, 2, 2), getVertexList());

        assertEquals(graph1.toString(), graph2.toString());
    }

    /**
     * Tests serialization of DefaultUndirectedGraph
     */
    @SuppressWarnings("unchecked") @Test public void testSerialization_DefaultUndirectedGraph()
        throws Exception
    {
        DefaultUndirectedGraph<String, DefaultEdge> graph1 =
            new DefaultUndirectedGraph<>(DefaultEdge.class);
        addAllVertices(graph1, getVertexList());
        graph1.addEdge(getV1(), getV2());
        graph1.addEdge(getV2(), getV3());
        graph1.addEdge(getV3(), getV1()); // contains loop

        DefaultUndirectedGraph<String, DefaultEdge> graph2 =
            (DefaultUndirectedGraph<String, DefaultEdge>) serializeAndDeserialize(graph1);

        assertContainsAllVertices(graph2, getVertexList());

        assertTrue(graph2.containsEdge(getV1(), getV2()));
        assertTrue(graph2.containsEdge(getV2(), getV1()));

        assertTrue(graph2.containsEdge(getV2(), getV3()));
        assertTrue(graph2.containsEdge(getV3(), getV2()));

        assertTrue(graph2.containsEdge(getV3(), getV1()));
        assertTrue(graph2.containsEdge(getV1(), getV3()));

        checkEdgesOf(graph2, Arrays.asList(2, 2, 2), getVertexList());

        assertEquals(graph1.toString(), graph2.toString());
    }
}
