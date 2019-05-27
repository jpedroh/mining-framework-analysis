/*
 * (C) Copyright 2019-2019, by Dimitrios Michail and Contributors.
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
package org.jgrapht.io;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;
import org.junit.*;

import java.io.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link JsonImporter}.
 * 
 * @author Dimitrios Michail
 */
public class JSONImporterTest
{

    @Test
    public void testUndirectedUnweighted()
        throws ImportException
    {
        // @formatter:off
        String input = "{\n"
                     + "  \"nodes\": [\n"    
                     + "  { \"id\":\"1\" },\n"
                     + "  { \"id\":\"2\" },\n"
                     + "  { \"id\":\"3\" },\n"
                     + "  { \"id\":\"4\" }\n"
                     + "  ],\n"
                     + "  \"edges\": [\n"    
                     + "  { \"source\":\"1\", \"target\":\"2\" },\n"
                     + "  { \"source\":\"1\", \"target\":\"3\" }\n"
                     + "  ]\n"
                     + "}";
        // @formatter:on

        Graph<String,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(true).allowingSelfLoops(true)
                .vertexSupplier(SupplierUtil.createStringSupplier())
                .edgeSupplier(SupplierUtil.DEFAULT_EDGE_SUPPLIER).buildGraph();

        VertexProvider<String> vp = (label, attributes) -> label;
        EdgeProvider<String, DefaultEdge> ep =
            (from, to, label, attributes) -> g.getEdgeSupplier().get();

        JSONImporter<String, DefaultEdge> importer = new JSONImporter<>(vp, ep);
        importer.importGraph(g, new StringReader(input));

        assertEquals(4, g.vertexSet().size());
        assertEquals(2, g.edgeSet().size());
        assertTrue(g.containsVertex("1"));
        assertTrue(g.containsVertex("2"));
        assertTrue(g.containsVertex("3"));
        assertTrue(g.containsVertex("4"));
        assertTrue(g.containsEdge("1", "2"));
        assertTrue(g.containsEdge("1", "3"));
    }

}
