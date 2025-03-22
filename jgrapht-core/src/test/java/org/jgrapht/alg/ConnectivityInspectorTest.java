/*
 * (C) Copyright 2003-2016, by Barak Naveh and Contributors.
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
package org.jgrapht.alg;

import org.jgrapht.graph.*;

import junit.framework.*;

/**
 * .
 * 
 * @author Barak Naveh
 */
public class ConnectivityInspectorTest extends TestCase {
	// ~ Static fields/initializers
	// ---------------------------------------------
	private static final String V1 = "v1";
	private static final String V2 = "v2";
	private static final String V3 = "v3";
	private static final String V4 = "v4";
	private static final String[] Vertices = {V1, V2, V3, V4};
	// ~ Instance fields
	// --------------------------------------------------------
	//
	DefaultEdge e1;
	DefaultEdge e2;
	DefaultEdge e3;
	DefaultEdge e3_b;
	DefaultEdge u;
	ConnectivityInspector<String, DefaultEdge> inspector;
	// ~ Methods
	// ----------------------------------------------------------------
	/**
	 * .
	 * 
	 * @return a graph
	 */
    public Pseudograph<String, DefaultEdge> create() {
    	Pseudograph<String, DefaultEdge> g = new Pseudograph<>(
    			DefaultEdge.class);

    	assertEquals(0, g.vertexSet().size());
    	g.addVertex(V1);
    	assertEquals(1, g.vertexSet().size());
    	g.addVertex(V2);
    	assertEquals(2, g.vertexSet().size());
    	g.addVertex(V3);
    	assertEquals(3, g.vertexSet().size());
    	g.addVertex(V4);
    	assertEquals(4, g.vertexSet().size());

    	assertEquals(0, g.edgeSet().size());

    	e1 = g.addEdge(V1, V2);
    	assertEquals(1, g.edgeSet().size());

    	e2 = g.addEdge(V2, V3);
    	assertEquals(2, g.edgeSet().size());

    	e3 = g.addEdge(V3, V1);
    	assertEquals(3, g.edgeSet().size());

    	e3_b = g.addEdge(V3, V1);
    	assertEquals(4, g.edgeSet().size());
    	assertNotNull(e3_b);

    	u = g.addEdge(V1, V1);
    	assertEquals(5, g.edgeSet().size());
    	u = g.addEdge(V1, V1);
    	assertEquals(6, g.edgeSet().size());

    	return g;
    }
	/**
	 * .
	 */
    public void testDirectedGraph() {
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/left.java
    	ListenableDirectedGraph<String, DefaultEdge> g = new ListenableDirectedGraph<String, DefaultEdge>(
    			DefaultEdge.class);
    	g.addVertex(V1);
    	g.addVertex(V2);
    	g.addVertex(V3);
||||||| /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/base.java
        ListenableDirectedGraph<String, DefaultEdge> g =
            new ListenableDirectedGraph<String, DefaultEdge>(
                DefaultEdge.class);
        g.addVertex(V1);
        g.addVertex(V2);
        g.addVertex(V3);
=======
        ListenableDirectedGraph<String, DefaultEdge> g =
            new ListenableDirectedGraph<>(DefaultEdge.class);
        g.addVertex(V1);
        g.addVertex(V2);
        g.addVertex(V3);
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/right.java

    	g.addEdge(V1, V2);

<<<<<<< /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/left.java
    	inspector = new ConnectivityInspector<String, DefaultEdge>(
    			g);
    	g.addGraphListener(inspector);
||||||| /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/base.java
        ConnectivityInspector<String, DefaultEdge> inspector =
            new ConnectivityInspector<String, DefaultEdge>(g);
        g.addGraphListener(inspector);
=======
        ConnectivityInspector<String, DefaultEdge> inspector = new ConnectivityInspector<>(g);
        g.addGraphListener(inspector);
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/right.java

    	assertEquals(false, inspector.isGraphConnected());

    	g.addEdge(V1, V3);

    	assertEquals(true, inspector.isGraphConnected());
    }
	/**
	 * .
	 */
    public void testIsGraphConnected() {
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/left.java
    	Pseudograph<String, DefaultEdge> g = create();
    	inspector = new ConnectivityInspector<String, DefaultEdge>(
    			g);
||||||| /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/base.java
        Pseudograph<String, DefaultEdge> g = create();
        ConnectivityInspector<String, DefaultEdge> inspector =
            new ConnectivityInspector<String, DefaultEdge>(g);
=======
        Pseudograph<String, DefaultEdge> g = create();
        ConnectivityInspector<String, DefaultEdge> inspector = new ConnectivityInspector<>(g);
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/right.java

    	assertEquals(false, inspector.isGraphConnected());

<<<<<<< /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/left.java
    	g.removeVertex(V4);
    	inspector = new ConnectivityInspector<String, DefaultEdge>(g);
    	assertEquals(true, inspector.isGraphConnected());
||||||| /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/base.java
        g.removeVertex(V4);
        inspector = new ConnectivityInspector<String, DefaultEdge>(g);
        assertEquals(true, inspector.isGraphConnected());
=======
        g.removeVertex(V4);
        inspector = new ConnectivityInspector<>(g);
        assertEquals(true, inspector.isGraphConnected());
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/right.java

    	g.removeVertex(V1);
    	assertEquals(1, g.edgeSet().size());

    	g.removeEdge(e2);
    	g.addEdge(V2, V2);
    	assertEquals(1, g.edgeSet().size());

<<<<<<< /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/left.java
    	inspector = new ConnectivityInspector<String, DefaultEdge>(g);
    	assertEquals(false, inspector.isGraphConnected());
||||||| /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/base.java
        inspector = new ConnectivityInspector<String, DefaultEdge>(g);
        assertEquals(false, inspector.isGraphConnected());
=======
        inspector = new ConnectivityInspector<>(g);
        assertEquals(false, inspector.isGraphConnected());
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/f5175d87a484118c5b29556af75dc15b2438191b/jgrapht-core/src/test/java/org/jgrapht/alg/ConnectivityInspectorTest.java/right.java
    }
	/**
	 * .
	 */
	/**
	 * .
	 */
	/**
	 * .
	 */
	// ~ Test cases for isComplete / incompleteVertices methods
	// ---------------------------------------------
	public void testUndirectedGraphWithOneVertex_is_Complete() {
		UndirectedGraph<String, DefaultEdge> g = 
				this.createUndirectedGraph(1);
		inspector = new ConnectivityInspector<String, DefaultEdge>(
				g);

		assertTrue(inspector.isComplete());
		
		assertTrue(inspector.incompleteVertices().isEmpty());
	}
	public void testUndirectedGraphWithTwoVerticesAndNoEdges_isNot_Complete() {
		UndirectedGraph<String, DefaultEdge> g = 
				this.createUndirectedGraph(2);
		inspector = 
				new ConnectivityInspector<String, DefaultEdge>(g);

		assertFalse(inspector.isComplete());
		
		Set<String> set = inspector.incompleteVertices();
		
		assertEquals(set.size(), g.vertexSet().size());
		assertTrue(set.contains(V1));
		assertTrue(set.contains(V2));
	}
	public void testUndirectedGraphWithTwoVertices_And_OneEdge_is_Complete() {
		UndirectedGraph<String, DefaultEdge> g = 
				this.createUndirectedGraph(2);
		g.addEdge(V1, V2);
		inspector = new ConnectivityInspector<String, DefaultEdge>(g);

		assertTrue(inspector.isComplete());
	}
	public void testDirectedGraphWithOneVertex_is_Complete() {
		DirectedGraph<String, DefaultEdge> g = this.createDirectedGraph(1);
		inspector = new ConnectivityInspector<String, DefaultEdge>(g);

		assertTrue(inspector.isComplete());
		
		Set<String> set = inspector.incompleteVertices();
		assertTrue(set.isEmpty());
	}
	public void testDirectedGraphWithTwoVertices_and_OneEdge_isNot_Complete() {
		DirectedGraph<String, DefaultEdge> g = this.createDirectedGraph(2);
		g.addEdge(V1, V2);
		inspector = new ConnectivityInspector<String, DefaultEdge>(g);

		assertFalse(inspector.isComplete());
		
		Set<String> set = inspector.incompleteVertices();
		assertEquals(1, set.size());
		assertTrue(set.contains(V2));
	}
	public void testDirectedGraphWithTwoVertices_and_twoEdges_is_Complete() {
		DirectedGraph<String, DefaultEdge> g = this.createDirectedGraph(2);
		g.addEdge(V1, V2);
		g.addEdge(V2, V1);
		inspector = new ConnectivityInspector<String, DefaultEdge>(g);

		assertTrue(inspector.isComplete());

		assertTrue(inspector.incompleteVertices().isEmpty());
	}
	public void testDirectedGraphPartlyIncomplete() {
		DirectedGraph<String, DefaultEdge> g = this.createDirectedGraph(3);
		inspector = new ConnectivityInspector<String, DefaultEdge>(g);

		g.addEdge(V1, V2);
		g.addEdge(V1, V3);
		
		g.addEdge(V2, V1);
		g.addEdge(V2, V3);
	
		Set<String> set = inspector.incompleteVertices();
		assertTrue(set.toString(), set.contains(V3));
	}
	public void testAllDirectedGeneratedCompleteGraphs_are_Complete() {

		for (int vertices = 1; vertices < 20; vertices++) {
			
			CompleteGraphGenerator<String, DefaultEdge> generator = 
					new CompleteGraphGenerator<String, DefaultEdge>(vertices);

			DirectedGraph<String, DefaultEdge> g = 
					new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

			generator.generateGraph(g, new SimpleVertexFactory(),	null);

			inspector = 
					new ConnectivityInspector<String, DefaultEdge>(g);

			assertTrue(inspector.isComplete());
		}
	}
	public void testAllUndirectedGeneratedCompleteGraphs_are_Complete() {

		for (int vertices = 1; vertices < 20; vertices++) {
			
			CompleteGraphGenerator<String, DefaultEdge> generator = 
					new CompleteGraphGenerator<String, DefaultEdge>(vertices);

			UndirectedGraph<String, DefaultEdge> g = 
					new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

			generator.generateGraph(g, new SimpleVertexFactory(),null);

			inspector = 
					new ConnectivityInspector<String, DefaultEdge>(g);

			assertTrue(inspector.isComplete());
		}
	}
	public void testMultigraph_NoMultipleEdges() {
		Multigraph<String, DefaultEdge> mg = new Multigraph<String, DefaultEdge>(DefaultEdge.class);
		inspector = new ConnectivityInspector<String, DefaultEdge>(mg);

		addVertices(3, mg);
		assertEquals(3, mg.vertexSet().size());
		mg.addEdge(V1, V2);
		mg.addEdge(V1, V3);

		assertEquals(false, inspector.isComplete());

		mg.addEdge(V2, V3);
		assertEquals(true, inspector.isComplete());
	}
	public void testMultigraph_WithMultipleEdges() {
		Multigraph<String, DefaultEdge> mg = new Multigraph<String, DefaultEdge>(DefaultEdge.class);
		inspector = new ConnectivityInspector<String, DefaultEdge>(mg);

		// Empty graph is complete
		assertTrue(inspector.isComplete());
		
		addVertices(3, mg);
		
		// All vertices are incomplete
		assertEquals(3, inspector.incompleteVertices().size());
		
		mg.addEdge(V1, V2);
		mg.addEdge(V1, V2);
		mg.addEdge(V2, V1);
		mg.addEdge(V1, V3);
		mg.addEdge(V1, V3);

		Set<String> incompleteVertices = inspector.incompleteVertices();
		assertEquals(2, incompleteVertices.size());

		mg.addEdge(V2, V3);
		mg.addEdge(V2, V3);
		incompleteVertices = inspector.incompleteVertices();
		assertEquals(0, incompleteVertices.size());
	}
	public void testSelfLoops_in_DefaultDirectedGraph() {
		DefaultDirectedGraph<String, DefaultEdge> dg = 
				new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		inspector = new ConnectivityInspector<String, DefaultEdge>(dg);
		
		// Empty graph is complete
		assertTrue(inspector.isComplete());

		addVertices(3, dg);
		
		// All vertices are incomplete
		assertEquals(3, inspector.incompleteVertices().size());
		
		dg.addEdge(V1, V1);
		dg.addEdge(V2, V2);
		dg.addEdge(V3, V3);
		
		// All vertices are incomplete
		assertEquals(3, inspector.incompleteVertices().size());
		
		dg.addEdge(V1, V2);
		dg.addEdge(V1, V3);
		
		// V2 and V3 are incomplete
		assertEquals(2, inspector.incompleteVertices().size());

		dg.addEdge(V2, V3);
		
		// V2 and V3 are still incomplete
		assertEquals(2, inspector.incompleteVertices().size());
		
		dg.addEdge(V2, V1);
		
		// V3 is incomplete
		assertEquals(1, inspector.incompleteVertices().size());

		dg.addEdge(V3, V1);
		dg.addEdge(V3, V2);

		assertEquals(0, inspector.incompleteVertices().size());

	}
	// ~ Helper methods to create graphs
	// ---------------------------------------------
	private UndirectedGraph<String, DefaultEdge> createUndirectedGraph(int vertices) 
	{
		UndirectedGraph<String, DefaultEdge> g = 
				new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		
		addVertices(vertices, g);
		
		return g;
	}
	private DirectedGraph<String, DefaultEdge> createDirectedGraph(int vertices)
	{
		DirectedGraph<String, DefaultEdge> g = 
				new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		
		addVertices(vertices, g);
		
		return g;
	}
	private void addVertices(int vertices, Graph<String, DefaultEdge> g) {
		for (int i = 0; i < vertices; i++) {
			g.addVertex(Vertices[i]);
		}
	}
	class SimpleVertexFactory implements VertexFactory<String> {
		private int i = 0;

		public String createVertex() {
			return "Vertex " + (++i);
		}

	}
    // ~ Static fields/initializers ---------------------------------------------
    // ~ Instance fields --------------------------------------------------------
    // ~ Methods ----------------------------------------------------------------
    /**
     * .
     */
    /**
     * .
     */
}

// End ConnectivityInspectorTest.java
