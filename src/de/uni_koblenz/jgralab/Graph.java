/*
 * JGraLab - The Java Graph Laboratory
 *
 * Copyright (C) 2006-2011 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 *
 * For bug reports, documentation and further information, visit
 *
 *                         http://jgralab.uni-koblenz.de
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

package de.uni_koblenz.jgralab;

import java.io.DataOutputStream;

import java.util.Comparator;

import java.util.Map;

import org.pcollections.POrderedSet;

import de.uni_koblenz.jgralab.eca.ECARuleManagerInterface;

import de.uni_koblenz.jgralab.schema.EdgeClass;

import de.uni_koblenz.jgralab.schema.EnumDomain;

import de.uni_koblenz.jgralab.schema.GraphClass;

import de.uni_koblenz.jgralab.schema.RecordDomain;

import de.uni_koblenz.jgralab.schema.VertexClass;

import de.uni_koblenz.jgralab.trans.CommitFailedException;

import de.uni_koblenz.jgralab.trans.InvalidSavepointException;

import de.uni_koblenz.jgralab.trans.Savepoint;

import de.uni_koblenz.jgralab.trans.Transaction;

/**
 * The interface Graph is the base of all JGraLab graphs. It provides access to
 * global graph properties and to the Vertex and Edge sequence. Creation and
 * removal of vertices and edges, as well as validity checks, are provided.
 *
 * Additionally, convenient methods for traversal, either based on separate
 * calls (getFirst/getNext) or on Iterables, can be used to traverse the graph.
 *
 * @author ist@uni-koblenz.de
 */

/*
 * JGraLab - The Java Graph Laboratory
 *
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 *
 * For bug reports, documentation and further information, visit
 *
 *                         https://github.com/jgralab/jgralab
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

/**
 * The interface Graph is the base of all JGraLab graphs. It provides access to
 * global graph properties and to the Vertex and Edge sequence. Creation and
 * removal of vertices and edges, as well as validity checks, are provided.
 *
 * Additionally, convenient methods for traversal, either based on separate
 * calls (getFirst/getNext) or on Iterables, can be used to traverse the graph.
 *
 * @author ist@uni-koblenz.de
 */
public interface Graph extends AttributedElement<GraphClass, Graph> {

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Creates a vertex of the specified {@link VertexClass} and adds the new
	 * vertex to the Graph.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Creates a instance of the given class and adds this vertex to the graph
=======
	 * Creates a {@link Vertex} of the specified {@link VertexClass} and adds the new
	 * vertex to this {@link Graph}.
	 * 
	 * @param vc 
	 * 			the {@link VertexClass} of the new {@link Vertex}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public <T extends Vertex> T createVertex(VertexClass vc);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Creates an edge of the specified {@link EdgeClass} <code>ec</code> that
	 * connects <code>alpha</code> and </code>omega</code> vertices and adds the
	 * new edge to this Graph.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Creates a instance of the given class and adds this edge to the graph
=======
	 * Creates an {@link Edge} of the specified {@link EdgeClass} <code>ec</code> that
	 * connects <code>alpha</code> and </code>omega</code> vertices and adds the
	 * new edge to this {@link Graph}.
	 * 
	 * @param ec
	 * 			the {@link EdgeClass} of the new {@link Edge}
	 * @param alpha
	 * 			the alpha {@link Vertex} of the new {@link Edge}
	 * @param omega
	 * 			the omega {@link Vertex} of the new {@link Edge}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public <T extends Edge> T createEdge(EdgeClass ec, Vertex alpha,
			Vertex omega);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Retrieves the enum constant of <code>enumDomain</code> given by
	 * <code>constantName</code>.
	 *
	 * @param enumDomain
	 * @param constantName
	 * @return
	 */
	public Object getEnumConstant(EnumDomain enumDomain, String constantName);

	/**
	 * Creates a record of type <code>RecordDomain</code> with values as
	 * specified by <code>values</code>
	 *
	 * @param recordDomain
	 * @param values
	 * @return
	 */
	public Record createRecord(RecordDomain recordDomain,
			Map<String, Object> values);

	/**
	 * Checks whether this graph has changed with respect to the given
	 * <code>previousVersion</code>. Every change in the graph, e.g. adding,
	 * creating and reordering of edges and vertices or changes of attributes of
	 * the graph, an edge or a vertex are treated as a change.
	 *
	 * @param previousVersion
	 *            The version to check against
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * This method checks, if the graph is currently being loaded
	 * @return true if the graph is being loaded
	 */
	public boolean isLoading();

	/**
	 * Sets the loading flag. 
	 * @param isLoading
	 */
	public void setLoading(boolean isLoading);

	/**
	 * This method is called as soon as the loading of a graph is completed.
	 * It is used internally and one should not touch it
	 */
	public void internalLoadingCompleted();
	
	/**
	 * This method is called as soon as the loading of a graph is completed
	 * One may use it to perform own operations as soon as the loading is completed
	 */
	public void loadingCompleted();

	/**
	 * Checks if the graph has changed with respect to the given
	 * <code>aGraphVersion</code>. Every change in the graph, e.g. adding,
	 * creating and reordering of edges and vertices or changes of attributes of
	 * the graph, an edge or a vertex are treated as a change.
	 * 
	 * @param aGraphVersion
	 *            The graphVersion to check against
=======
	 * Retrieves the enum constant of an {@link EnumDomain} given by
	 * <code>constantName</code>.
	 *
	 * @param enumDomain
	 * 			the {@link EnumDomain} to create a constant for
	 * @param constantName
	 * 			the {@code String} value of the constant to create
	 * 
	 * @return the retrieved constant
	 */
	public Object getEnumConstant(EnumDomain enumDomain, String constantName);

	/**
	 * Creates a {@link Record} of type {@link RecordDomain} with values as
	 * specified by <code>values</code>.
	 *
	 * @param recordDomain
	 * 			the {@link RecordDomain} to create a {@link Record} for
	 * @param values
	 * 			the {@code Map} with the records components
	 * 
	 * @return the created {@link Record}
	 */
	public Record createRecord(RecordDomain recordDomain,
			Map<String, Object> values);

	/**
	 * Checks whether this {@link Graph} has changed with respect to the given
	 * <code>previousVersion</code>. Every change in this {@link Graph}, e.g. adding,
	 * creating and reordering of {@link Edge} and {@link Vertex} instances or changes 
	 * of attributes of the graph, an {@link Edge} or a {@link Vertex} are treated as a change.
	 *
	 * @param previousVersion
	 *            The version to check against
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 * @return <code>true</code> if the internal graph version of the graph is
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 *         different from the <code>previousVersion</code>.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 *         different from the given version <code>aGraphVersion</code>.
=======
	 *         different from the <code>previousVersion</code>
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public boolean isGraphModified(long previousVersion);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the version counter of this graph.
	 *
	 * @return the graph version
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Changes the graph version, should be called whenever the graph is
	 * changed, all changes like adding, creating and reordering of edges and
	 * vertices or changes of attributes of the graph, an edge or a vertex are
	 * treated as a change.
	 */
	public void graphModified();

	/**
	 * sets the internal graph version of this graph to graphVersion. This
	 * method is needed to load a graph without changing its version back to
	 * zero
	 * 
	 * @param graphVersion
	 */
	public void setGraphVersion(long graphVersion);

	/**
	 * @return the internal graph version
	 * @see #graphModified()
=======
	 * Returns the version counter of this {@link Graph}.
	 *
	 * @return the graph version
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 * @see #isGraphModified(long)
	 */
	public long getGraphVersion();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * @return true if this graph contains the given vertex <code>v</code>.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Checks if the graph-structure has changed with respect to the given
	 * <code>graphStructureVersion</code>. Changes in the graph structure are
	 * creation and deletion as well as reordering of vertices and edges, but
	 * not changes of attribute values.
	 * 
	 * @return <code>true</code> if the internal graph structure version of
	 *         the graph is different from the given version
	 *         <code>graphStructureVersion</code>.
=======
	 * @return true if this {@link Graph} contains the given {@link Vertex} <code>v</code>.
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public boolean containsVertex(Vertex v);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * @return true if this graph contains the given edge <code>e</code>.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Changes the graph structure version, should be called whenever the
	 * structure of the graph is changed, for instance by creation and deletion
	 * or reordering of vertices and edges
	 */
	public void vertexListModified();

	/**
	 * @return the internal graph structure version
	 * @see #vertexListModified()
	 * @see #isVertexListModified(long)
	 */
	public long getVertexListVersion();

	/**
	 * Checks if the graph-structure has changed with respect to the given
	 * <code>graphStructureVersion</code>. Changes in the graph structure are
	 * creation and deletion as well as reordering of vertices and edges, but
	 * not changes of attribute values.
	 * 
	 * @return <code>true</code> if the internal graph structure version of
	 *         the graph is different from the given version
	 *         <code>graphStructureVersion</code>.
	 */
	public boolean isEdgeListModified(long edgeListVersion);

	/**
	 * Changes the graph structure version, should be called whenever the
	 * structure of the graph is changed, for instance by creation and deletion
	 * or reordering of vertices and edges
	 */
	public void edgeListModified();

	/**
	 * @return the internal edge list version
	 * @see #edgeListModified()
	 * @see #isEdgeListModified(long)
	 */
	public long getEdgeListVersion();

	/**
	 * adds the given vertex object to this graph. if the vertex' id is 0, a
	 * valid id is set, otherwise the vertex' current id is used if possible.
	 * Should only be used by m1-Graphs derived from Graph. To create a new
	 * Vertex as user, use the appropriate methods from the derived Graphs like
	 * <code>createStreet(...)</code>
	 * 
	 * @param newVertex
	 *            the Vertex to add
	 * @throws GraphException
	 *             if a vertex with the same id already exists
	 */
	void addVertex(Vertex newVertex);
	

	/**
	 * @return true iff this graph contains the given vertex
	 */
	boolean containsVertex(Vertex v);

	/**
	 * adds the given edge object to this graph. if the edges id is 0, a valid
	 * id is set, otherwise the edges current id is used if possible. Should
	 * only be used by m1-Graphs derived from Graph. To create a new Edge as
	 * user, use the appropriate methods from the derived Graphs like
	 * <code>createStreet(...)</code>
	 * 
	 * @param newEdge
	 *            the edge to add
	 * @param alpha
	 *            the vertex the new edge should start at
	 * @param omega
	 *            the vertex the new edge should end at
	 * @throws GraphException
	 *             if a edge with the same id already exists
	 */
	void addEdge(Edge newEdge, Vertex alpha, Vertex omega);

	/**
	 * @return true iff this graph contains the given edge
=======
	 * @return true if this {@link Graph} contains the given {@link Edge} <code>e</code>.
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	boolean containsEdge(Edge e);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Removes the vertex <code>v</code> from the vertex sequence of this graph.
	 * Also, any edges incident to vertex <code>v</code> are deleted. If
	 * <code>v</code> is the parent of a composition, all child vertices are
	 * also deleted.
	 *
	 * Preconditions: v.isValid()
	 *
	 * Postconditions: !v.isValid() && !containsVertex(v) &&
	 * getVertex(v.getId()) == null
	 *
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * removes the specified vertex from vSeq and erases its attributes
	 * 
=======
	 * Removes the {@link Vertex} <code>v</code> from the vertex sequence of this {@link Graph}.
	 * Also, any edges incident to {@link Vertex} <code>v</code> are deleted. If
	 * <code>v</code> is the parent of a composition, all child vertices are
	 * also deleted.
	 *
	 * Preconditions: v.isValid()
	 *
	 * Postconditions: !v.isValid() && !containsVertex(v) &&
	 * getVertex(v.getId()) == null
	 *
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 * @param v
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 *            the Vertex to be deleted
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 *            the id of the vertex to be deleted
=======
	 *            the {@link Vertex} to be deleted
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public void deleteVertex(Vertex v);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Removes the edge <code>e</code> from the edge sequence of this graph.
	 * This implies changes to the incidence lists of the alpha and omega vertex
	 * of <code>e</code>.
	 *
	 * Preconditions: e.isValid()
	 *
	 * Postconditions: !e.isValid() && !containsEdge(e) && getEdge(e.getId()) ==
	 * null
	 *
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Callback function for triggered actions just before a vertex is actually
	 * deleted.
	 * 
	 * @param v
	 *            the deleted vertex
	 */
	public void vertexDeleted(Vertex v);

	/**
	 * Callback function for triggered actions just after a vertex was added.
	 * 
	 * @param v
	 *            the deleted vertex
	 */
	public void vertexAdded(Vertex v);

	/**
	 * removes this edge from eSeq and erases its attributes
	 * 
=======
	 * Removes the {@link Edge} <code>e</code> from the edge sequence of this {@link Graph}.
	 * This implies changes to the incidence lists of the alpha and omega {@link Vertex}
	 * of <code>e</code>.
	 *
	 * Preconditions: e.isValid()
	 *
	 * Postconditions: !e.isValid() && !containsEdge(e) && getEdge(e.getId()) ==
	 * null
	 *
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 * @param e
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 *            the Edge to be deleted
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 *            the edge to be deleted
	 * 
=======
	 *            the {@link Edge} to be deleted
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public void deleteEdge(Edge e);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the first Vertex in the vertex sequence of this Graph.
	 *
	 * @return the first Vertex, or null if this graph contains no vertices.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Callback function for triggered actions just before an edge is actually
	 * deleted.
	 * 
	 * @param e
	 *            the deleted edge
	 */
	public void edgeDeleted(Edge e);

	/**
	 * Callback function for triggered actions just after an edge was added.
	 * 
	 * @param e
	 *            the deleted vertex
	 */
	public void edgeAdded(Edge e);

	/**
	 * @return the first vertex object of vSeq
=======
	 * Returns the first {@link Vertex} in the vertex sequence of this {@link Graph}.
	 *
	 * @return the first {@link Vertex}, or null if this {@link Graph} contains no vertices.
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Vertex getFirstVertex();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the last Vertex in the vertex sequence of this Graph.
	 *
	 * @return the last Vertex, or null if this graph contains no vertices.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * @param aVertexClass
	 * @return the first vertex object of class aVertexClass in vSeq
=======
	 * Returns the last {@link Vertex} in the vertex sequence of this {@link Graph}.
	 *
	 * @return the last {@link Vertex}, or null if this graph contains no vertices.
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Vertex getLastVertex();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the first Vertex of the specified <code>vertexClass</code>
	 * (including subclasses) in the vertex sequence of this Graph.
	 *
	 * @param vertexClass
	 *            a VertexClass (i.e. an instance of schema.VertexClass)
	 *
	 * @return the first Vertex, or null if this graph contains no vertices of
	 *         the specified <code>vertexClass</code>.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * @param aVertexClass
	 * @param explicitType
	 *            if set to true, only vertices which are explicitly of the
	 *            given edge class will be retrieved, otherwise also vertices of
	 *            subclasses of the given EdgeClass will be retrieved
	 * @return the first vertex object of explicit class aVertexClass in vSeq
=======
	 * Returns the first {@link Vertex} of the specified {@link VertexClass}
	 * (including subclasses) in the vertex sequence of this {@link Graph}.
	 *
	 * @param vertexClass
	 *            a {@link VertexClass} (i.e. an instance of schema.VertexClass)
	 *
	 * @return the first {@link Vertex}, or null if this {@link Graph} contains no vertices of
	 *         the specified {@link VertexClass}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Vertex getFirstVertex(VertexClass vertexClass);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the first Vertex of the specified <code>vertexClass</code>
	 * (including subclasses) in the vertex sequence of this Graph.
	 *
	 * @param vertexClass
	 *            a VertexClass (i.e. an schema interface extending Vertex)
	 *
	 * @return the first Vertex, or null if this graph contains no vertices of
	 *         the specified <code>vertexClass</code>.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * @param aVertexClass
	 * @return the first vertex object of class aVertexClass in vSeq
=======
	 * Returns the first {@link Vertex} of the specified VertexClass
	 * (including subclasses) in the vertex sequence of this {@link Graph}.
	 *
	 * @param vertexClass
	 *            a VertexClass (i.e. an schema interface extending {@link Vertex})
	 *
	 * @return the first {@link Vertex}, or null if this {@link Graph} contains no vertices of
	 *         the specified <code>vertexClass</code>.
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the first Edge in the edge sequence of this Graph.
	 *
	 * @return the first Edge, or null if this graph contains no edges.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * @param aVertexClass
	 * @param explicitType
	 *            if set to true, only vertices which are explicitly of the
	 *            given edge class will be retrieved, otherwise also vertices of
	 *            subclasses of the given EdgeClass will be retrieved
	 * @return the first vertex object of explicit class aVertexClass in vSeq
=======
	 * Returns the first {@link Edge} in the edge sequence of this {@link Graph}.
	 *
	 * @return the first {@link Edge}, or null if this {@link Graph} contains no edges.
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Edge getFirstEdge();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the last Edge in the edge sequence of this Graph.
	 *
	 * @return the last Edge, or null if this graph contains no edges.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * @param aVertex
	 * @return the next vertex object in vSeq of aVertex
=======
	 * Returns the last {@link Edge} in the edge sequence of this {@link Graph}.
	 *
	 * @return the last Edge, or null if this graph contains no edges.
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Edge getLastEdge();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the first Edge of the specified <code>edgeClass</code> (including
	 * subclasses) in the edge sequence of this Graph.
	 *
	 * @param edgeClass
	 *            an EdgeClass (i.e. an instance of schema.EdgeClass)
	 *
	 * @return the first Edge, or null if this graph contains no edges of the
	 *         specified <code>edgeClass</code>.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * @param aVertex
	 *            the current vertex
	 * @param aVertexClass
	 *            the class of the next vertex
	 * @return the next vertex in vSeq of class aVertexClass or its superclasses
=======
	 * Returns the first {@link Edge} of the specified {@link EdgeClass} (including
	 * subclasses) in the edge sequence of this {@link Graph}.
	 *
	 * @param edgeClass
	 *            an {@link EdgeClass} (i.e. an instance of schema.EdgeClass)
	 *
	 * @return the first {@link Edge}, or null if this {@link Graph} contains no edges of the
	 *         specified {@link EdgeClass}.
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Edge getFirstEdge(EdgeClass edgeClass);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the first Edge of the specified <code>edgeClass</code> (including
	 * subclasses) in the edge sequence of this Graph.
	 *
	 * @param edgeClass
	 *            an EdgeClass (i.e. an schema interface extending Edge)
	 *
	 * @return the first Edge, or null if this graph contains no edges of the
	 *         specified <code>edgeClass</code>.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * @param aVertex
	 *            the current vertex
	 * @param aM1VertexClass
	 *            the javaclass of the next vertex
	 * @return the next vertex in vSeq of class aVertexClass or its superclasses
=======
	 * Returns the first {@link Edge} of the specified <code>edgeClass</code> (including
	 * subclasses) in the edge sequence of this {@link Graph}.
	 *
	 * @param edgeClass
	 *            an EdgeClass (i.e. an schema interface extending Edge)
	 *
	 * @return the first {@link Edge}, or null if this {@link Graph} contains no edges of the
	 *         specified <code>edgeClass</code>.
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Edge getFirstEdge(Class<? extends Edge> edgeClass);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the Vertex with the specified <code>id</code> if such a vertex
	 * exists in this Graph.
	 *
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * @param aVertex
	 *            the current vertex
	 * @param aVertexClass
	 *            the class of the next vertex
	 * @param explicitType
	 *            if set to true, only vertices which are explicitly of the
	 *            given edge class will be retrieved, otherwise also vertices of
	 *            subclasses of the given EdgeClass will be retrieved
	 * @return the next vertex in vSeq of explicit class aVertexClass
	 */
	public Vertex getNextVertexOfClass(Vertex aVertex,
			VertexClass aVertexClass, boolean explicitType);

	/**
	 * @param aVertex
	 *            the current vertex
	 * @param aM1VertexClass
	 *            the class of the next vertex
	 * @param explicitType
	 *            if set to true, only vertices which are explicitly of the
	 *            given edge class will be retrieved, otherwise also vertices of
	 *            subclasses of the given EdgeClass will be retrieved
	 * @return the next vertex in vSeq of explicit class aVertexClass
	 */
	public Vertex getNextVertexOfClass(Vertex aVertex,
			Class<? extends Vertex> aM1VertexClass, boolean explicitType);


	/**
	 * @return first edge object of eSeq
	 */
	public Edge getFirstEdgeInGraph();

	/**
	 * @param anEdgeClass
	 * @return the first edge object of anEdgeClass in eSeq
	 */
	public Edge getFirstEdgeOfClassInGraph(EdgeClass anEdgeClass);

	/**
	 * @param anEdgeClass
	 * @return the first edge object of anEdgeClass in eSeq
	 */
	public Edge getFirstEdgeOfClassInGraph(Class<? extends Edge> anEdgeClass);

	/**
	 * @param anEdgeClass
	 * @param explicitType
	 *            if set to true, only edges which are explicitly of the given
	 *            edge class will be retrieved, otherwise also edges of
	 *            subclasses of the given EdgeClass will be retrieved
	 * @return the first edge object of explicit anEdgeClass in eSeq
	 */
	public Edge getFirstEdgeOfClassInGraph(EdgeClass anEdgeClass,
			boolean explicitType);

	/**
	 * @param anEdgeClass
	 * @param explicitType
	 *            if set to true, only edges which are explicitly of the given
	 *            edge class will be retrieved, otherwise also edges of
	 *            subclasses of the given EdgeClass will be retrieved
	 * @return the first edge object of explicit anEdgeClass in eSeq
	 */
	public Edge getFirstEdgeOfClassInGraph(Class<? extends Edge> anEdgeClass,
			boolean explicitType);

	/**
	 * @param anEdge
	 * @return the next edge object in eSeq of anEdge
	 */
	public Edge getNextEdgeInGraph(Edge anEdge);

	/**
	 * @param anEdge
	 *            the current edge
	 * @param anEdgeClass
	 * @return the next object of anEdgeClass or its superclasses in eSeq
	 */
	public Edge getNextEdgeOfClassInGraph(Edge anEdge, EdgeClass anEdgeClass);

	/**
	 * @param anEdge
	 *            the current edge
	 * @param anEdgeClass
	 * @return the next object of anEdgeClass or its superclasses in eSeq
	 */
	public Edge getNextEdgeOfClassInGraph(Edge anEdge,
			Class<? extends Edge> anEdgeClass);

	/**
	 * @param anEdge
	 *            the current edge
	 * @param anEdgeClass
	 * @param explicitType
	 *            if set to true, only edges which are explicitly of the given
	 *            edge class will be retrieved, otherwise also edges of
	 *            subclasses of the given EdgeClass will be retrieved
	 * @return the next object of explicit anEdgeClass in eSeq
	 */
	public Edge getNextEdgeOfClassInGraph(Edge anEdge, EdgeClass anEdgeClass,
			boolean explicitType);

	/**
	 * @param anEdge
	 *            the current edge
	 * @param anEdgeClass
	 * @param explicitType
	 *            if set to true, only edges which are explicitly of the given
	 *            edge class will be retrieved, otherwise also edges of
	 *            subclasses of the given EdgeClass will be retrieved
	 * @return the next object of explicit anEdgeClass in eSeq
	 */
	public Edge getNextEdgeOfClassInGraph(Edge anEdge,
			Class<? extends Edge> anEdgeClass, boolean explicitType);

	/**
	 * @param v
	 *            a vertex
	 * @return the first edge in the incidence list of v
	 */
	public Edge getFirstEdge(Vertex v);

	/**
	 * @param v
	 *            a vertex
	 * @param orientation
	 *            the orientation the next incidence should have
	 * @return the first edge in the incidence list of v with the specified
	 *         orientation
	 */
	public Edge getFirstEdge(Vertex v, EdgeDirection orientation);

	public Edge getFirstEdgeOfClass(Vertex v, EdgeClass ec);

	public Edge getFirstEdgeOfClass(Vertex v, EdgeClass ec, boolean noSubclasses);

	public Edge getFirstEdgeOfClass(Vertex v, EdgeClass ec,
			EdgeDirection orientation, boolean noSubclasses);

	public Edge getFirstEdgeOfClass(Vertex v, Class<? extends Edge> ec);

	public Edge getFirstEdgeOfClass(Vertex v, Class<? extends Edge> ec,
			boolean noSubclasses);

	public Edge getFirstEdgeOfClass(Vertex v, Class<? extends Edge> ec,
			EdgeDirection orientation, boolean noSubclasses);

	/**
	 * @param e
	 *            an edge
	 * @return the next edge in the incidence list of this(e)
	 */
	public Edge getNextEdge(Edge e);

	/**
	 * @param e
	 *            an edge
	 * @param orientation
	 *            the orientation the next incidence should have
	 * @return the next edge in the incidence list of this(e) with the specified
	 *         orientation
	 */
	public Edge getNextEdge(Edge e, EdgeDirection orientation);

	public Edge getNextEdgeOfClass(Edge e, EdgeClass ec);

	public Edge getNextEdgeOfClass(Edge e, EdgeClass ec, boolean noSubclasses);

	public Edge getNextEdgeOfClass(Edge e, EdgeClass ec,
			EdgeDirection orientation, boolean noSubclasses);

	public Edge getNextEdgeOfClass(Edge e, Class<? extends Edge> ec);

	public Edge getNextEdgeOfClass(Edge e, Class<? extends Edge> ec,
			boolean noSubclasses);

	public Edge getNextEdgeOfClass(Edge e, Class<? extends Edge> ec,
			EdgeDirection orientation, boolean noSubclasses);

	/**
	 * @param v
	 *            the vertex object which degree is to be determined
	 * @return the degree of vertex v
	 */
	public int getDegree(Vertex v);

	/**
	 * @param v
	 *            the vertex object which degree is to be determined
	 * @param orientation
	 *            the orientation the next incidence should have are counted
	 */
	public int getDegree(Vertex v, EdgeDirection orientation);

	/**
=======
	 * Returns the {@link Vertex} with the specified <code>id</code> if such a vertex
	 * exists in this {@link Graph}.
	 *
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 * @param id
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 *            the id of the vertex (must be > 0)
	 * @return the Vertex, or null if no such vertex exists
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 *            the id of the vertex
	 * @return vertex with id-number id
=======
	 *            the id of the {@link Vertex} (must be > 0)
	 * @return the {@link Vertex}, or null if no such vertex exists
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Vertex getVertex(int id);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the oriented Edge with the specified <code>id</code> if such an
	 * edge exists in this Graph. If <code>id</code> is positive, the normal
	 * edge is returned, otherwise, the reversed Edge is returned.
	 *
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
=======
	 * Returns the oriented {@link Edge} with the specified <code>id</code> if such an
	 * edge exists in this {@link Graph}. If <code>id</code> is positive, the normal
	 * edge is returned, otherwise, the reversed Edge is returned.
	 *
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 * @param id
	 *            the id of the edge (must be != 0)
	 * @return the Edge, or null if no such edge exists
	 */
	public Edge getEdge(int id);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the number of vertices in this Graph.
	 *
	 * @return the number of vertices
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * @param e
	 *            the edge object which alpha vertex has to be determined
	 * @return the alpha vertex of edge e
	 */
	public Vertex getAlpha(Edge e);

	/**
	 * @param e
	 *            the edge object which omega vertex has to be determined
	 * @return the omega vertex of edge e
	 */
	public Vertex getOmega(Edge e);

	/**
	 * Puts source somewhere before target in vSeq
	 * 
	 * @param source
	 *            a vertex
	 * @param target
	 *            a vertex
	 */
	public void putAfterVertex(Vertex target, Vertex source);

	/**
	 * Checks whether <code>source</code> is after <code>target</code> in
	 * the global vertex sequence of this graph.
	 * 
	 * @param target
	 *            a Vertex
	 * @param source
	 *            another Vertex
	 * @return true if source is after target in the global vertex sequence of
	 *         this graph
	 */
	public boolean isAfterVertex(Vertex target, Vertex source);

	/**
	 * Checks whether <code>source</code> is before <code>target</code> in
	 * the global vertex sequence of this graph.
	 * 
	 * @param target
	 *            a Vertex
	 * @param source
	 *            another Vertex
	 * @return true if source is before target in the global vertex sequence of
	 *         this graph
	 */
	public boolean isBeforeVertex(Vertex target, Vertex source);

	/**
	 * puts source somewhere after target in eSeq
	 * 
	 * @param source
	 *            an edge
	 * @param target
	 *            an edge
	 */
	public void putAfterEdgeInGraph(Edge target, Edge source);

	/**
	 * puts source somewhere before target in vSeq
	 * 
	 * @param source
	 *            a vertex
	 * @param target
	 *            a vertex
	 */
	public void putBeforeVertex(Vertex target, Vertex source);

	/**
	 * puts source somewhere before target in eSeq
	 * 
	 * @param source
	 *            an edge
	 * @param target
	 *            an edge
	 */
	public void putBeforeEdgeInGraph(Edge target, Edge source);

	/**
	 * Checks whether <code>source</code> is after <code>target</code> in
	 * the global edge sequence of this graph.
	 * 
	 * @param target
	 *            an Edge
	 * @param source
	 *            another Edge
	 * @return true if source is after target in the global edge sequence of
	 *         this graph
	 */
	public boolean isAfterEdgeInGraph(Edge target, Edge source);

	/**
	 * Checks whether <code>source</code> is before <code>target</code> in
	 * the global edge sequence of this graph.
	 * 
	 * @param target
	 *            an Edge
	 * @param source
	 *            another Edge
	 * @return true if source is before target in the global edge sequence of
	 *         this graph
	 */
	public boolean isBeforeEdgeInGraph(Edge target, Edge source);

	/**
	 * puts the given edge <code>edge</code> before the given edge
	 * <code>nextEdge</code> in the incidence list. This does neither affect
	 * the global edge sequence eSeq nor the alpha or omega vertices, only the
	 * order of the edges at the <code>this-vertex</code> of e is changed
	 */
	public void putEdgeBefore(Edge edge, Edge nextEdge);

	/**
	 * puts the given edge <code>edge</code> after the given edge
	 * <code>previousEdge</code> in the incidence list. This does neither
	 * affect the global edge sequence eSeq nor the alpha or omega vertices,
	 * only the order of the edges at the <code>this-vertex</code> of e is
	 * changed
	 */
	public void putEdgeAfter(Edge edge, Edge previousEdge);

	/**
	 * inserts the given edge <code>edge</code> at the given position
	 * <code>pos</code> in the incidence list. This does neither affect the
	 * global edge sequence eSeq nor the alpha or omega vertices, only the order
	 * of the edges at the <code>this-vertex</code> of e is changed
	 * 
	 * @throws GraphException if the edges this-vertex and the given vertex are
	 *        not identical
	 */
	public void insertEdgeAt(Vertex vertex, Edge edge, int pos);

	/**
	 * @return the maximum number of vertices which can be stored in the graph
	 *         before the arrays are expanded
	 */
	public int getMaxVCount();

	/**
	 * @return the maximum number of edges which can be stored in the graph
	 *         before the arrays are expanded
	 */
	public int getMaxECount();

	/**
	 * @return the current number of vertices stored in the graph
=======
	 * Returns the number of vertices in this {@link Graph}.
	 *
	 * @return the number of vertices
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public int getVCount();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the number of edges in this Graph.
	 *
	 * @return the number of edges
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * @return the current number of edges stored in the graph
=======
	 * Returns the number of edges in this {@link Graph}.
	 *
	 * @return the number of edges
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public int getECount();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the <code>id</code> of this Graph. JGraLab assigns a 128 bit
	 * random id to all Graphs upon creation. This initial id is most likely
	 * (but not guaranteed) unique.
	 *
	 * @return the id of this graph
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * @return the id of the graph
=======
	 * Returns the <code>id</code> of this {@link Graph}. JGraLab assigns a 128 bit
	 * random id to all Graphs upon creation. This initial id is most likely
	 * (but not guaranteed) unique.
	 *
	 * @return the id of this graph
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public String getId();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns an Iterable which iterates over all edges of this Graph in the
	 * order determined by the edge sequence.
	 *
	 * @return an Iterable for all edges
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * sets the id of the graph
	 * 
	 * @param id
	 */
	public void setId(String id);

	/**
	 * Sets the start vertex of the given edge <code>e</code> to
	 * <code>alpha</code>. Also removes the edge from the incidence sequence
	 * of its old alpha vertex
	 */
	public void setAlpha(Edge e, Vertex alpha);

	/**
	 * Sets the end vertex of the given edge <code>e</code> to
	 * <code>omega</code> Also removes the edge from the incidence sequence of
	 * its old omega vertex
	 */
	public void setOmega(Edge e, Vertex omega);

	/**
	 * Using this method, one can simply iterate over all edges of this graph
	 * using the advanced for-loop
	 * 
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
=======
	 * Returns an {@code Iterable} which iterates over all edges of this {@link Graph} in the
	 * order determined by the edge sequence.
	 *
	 * @return an {@code Iterable} for all edges
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Iterable<Edge> edges();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns an Iterable which iterates over all edges of this Graph which
	 * have the specified <code>edgeClass</code> (including subclasses), in the
	 * order determined by the edge sequence.
	 *
	 * @param edgeClass
	 *            an EdgeClass (i.e. instance of schema.EdgeClass)
	 *
	 * @return an Iterable for all edges of the specified <code>edgeClass</code>
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Using this method, one can simply iterate over all edges of this graph
	 * using the advanced for-loop
	 * 
	 * @param eclass
	 *            the EdgeClass of the edges which should be iterated
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
=======
	 * Returns an {@code Iterable} which iterates over all edges of this {@link Graph} which
	 * have the specified {@link EdgeClass} (including subclasses), in the
	 * order determined by the edge sequence.
	 *
	 * @param edgeClass
	 *            an {@link EdgeClass} (i.e. instance of schema.EdgeClass)
	 *
	 * @return an {@code Iterable} for all edges of the specified {@link EdgeClass}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Iterable<Edge> edges(EdgeClass edgeClass);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns an Iterable which iterates over all edges of this Graph which
	 * have the specified <code>edgeClass</code> (including subclasses), in the
	 * order determined by the edge sequence.
	 *
	 * @param edgeClass
	 *            an EdgeClass (i.e. an schema interface extending Edge)
	 *
	 * @return an Iterable for all edges of the specified <code>edgeClass</code>
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Using this method, one can simply iterate over all edges of this graph
	 * using the advanced for-loop
	 * 
	 * @param eclass
	 *            the M1-Class of the edges which should be iterated
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
=======
	 * Returns an {@code Iterable} which iterates over all edges of this {@link Graph} which
	 * have the specified <code>edgeClass</code> (including subclasses), in the
	 * order determined by the edge sequence.
	 *
	 * @param edgeClass
	 *            an EdgeClass (i.e. an schema interface extending Edge)
	 *
	 * @return an {@code Iterable} for all edges of the specified <code>edgeClass</code>
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Iterable<Edge> edges(Class<? extends Edge> edgeClass);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns the list of reachable vertices.
	 *
	 * @param startVertex
	 *            a start vertex
	 * @param pathDescription
	 *            a GReQL path description
	 * @param vertexType
	 *            the type of the reachable vertices (acts as implicit
	 *            GoalRestriction)
	 * @return a List of all vertices of type <code>vertexType</code> reachable
	 *         from <code>startVertex</code> using the given
	 *         <code>pathDescription</code>
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Using this method, one can simply iterate over all aggregations of this
	 * graph using the advanced for-loop
	 * 
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
=======
	 * Returns the list of reachable vertices.
	 *
	 * @param startVertex
	 *            a start {@link Vertex}
	 * @param pathDescription
	 *            a GReQL path description
	 * @param vertexType
	 *            the type of the reachable vertices (acts as implicit
	 *            GoalRestriction)
	 * @return a List of all vertices of type <code>vertexType</code> reachable
	 *         from <code>startVertex</code> using the given
	 *         <code>pathDescription</code>
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public <T extends Vertex> POrderedSet<T> reachableVertices(
			Vertex startVertex, String pathDescription, Class<T> vertexType);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns an Iterable which iterates over all vertices of this Graph in the
	 * order determined by the vertex sequence.
	 *
	 * @return an Iterable for all vertices
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Using this method, one can simply iterate over all compositions of this
	 * graph using the advanced for-loop
	 * 
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
	 */
	public Iterable<Composition> compositions();

	/**
	 * Using this method, one can simply iterate over all vertices of this graph
	 * using the advanced for-loop
	 * 
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
=======
	 * Returns an {@code Iterable} which iterates over all vertices of this {@link Graph} in the
	 * order determined by the vertex sequence.
	 *
	 * @return an {@code Iterable} for all vertices
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Iterable<Vertex> vertices();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns an Iterable which iterates over all vertices of this Graph which
	 * have the specified <code>vertexClass</code> (including subclasses), in
	 * the order determined by the vertex sequence.
	 *
	 * @param vertexclass
	 *            a VertexClass (i.e. instance of schema.VertexClass)
	 *
	 * @return an Iterable for all vertices of the specified
	 *         <code>vertexClass</code>
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Using this method, one can simply iterate over all vertices of this graph
	 * using the advanced for-loop
	 * 
	 * @param vclass
	 *            the VertexClass of the vertices which should be iterated
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
=======
	 * Returns an {@code Iterable} which iterates over all vertices of this {@link Graph} which
	 * have the specified {@link VertexClass} (including subclasses), in
	 * the order determined by the vertex sequence.
	 *
	 * @param vertexclass
	 *            a {@link VertexClass} (i.e. instance of schema.VertexClass)
	 *
	 * @return an {@code Iterable} for all vertices of the specified
	 *         {@link VertexClass}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Iterable<Vertex> vertices(VertexClass vertexclass);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	 * Returns an Iterable which iterates over all vertices of this Graph which
	 * have the specified <code>vertexClass</code> (including subclasses), in
	 * the order determined by the vertex sequence.
	 *
	 * @param vertexClass
	 *            a VertexClass (i.e. a schema interface extending Vertex)
	 *
	 * @return a iterable for all vertices of the specified
	 *         <code>vertexClass</code>
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
	 * Using this method, one can simply iterate over all vertices of this graph
	 * using the advanced for-loop
	 * 
	 * @param vclass
	 *            the M1-Class of the vertices which should be iterated
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
=======
	 * Returns an {@link Iterable} which iterates over all vertices of this {@link Graph} which
	 * have the specified <code>vertexClass</code> (including subclasses), in
	 * the order determined by the vertex sequence.
	 *
	 * @param vertexClass
	 *            a VertexClass (i.e. a schema interface extending Vertex)
	 *
	 * @return a {@code Iterable} for all vertices of the specified
	 *         <code>vertexClass</code>
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
	 */
	public Iterable<Vertex> vertices(Class<? extends Vertex> vertexClass);

	// ---- transaction support ----
	/**
	 * @return a read-write-<code>Transaction</code>
	 */
	public Transaction newTransaction();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/left.java
	/**
	 * @return a read-only-<code>Transaction</code>
	 */
	public Transaction newReadOnlyTransaction();

	/**
	 * Sets the given <code>transaction</code> as the active
	 * <code>Transaction</code> for the current thread.
	 *
	 * @param transaction
	 */
	public void setCurrentTransaction(Transaction transaction);

	/**
	 * @return the currently active <code>Transaction</code> in the current
	 *         thread
	 */
	public Transaction getCurrentTransaction();

	/**
	 * Delegates to {@link Graph#getCurrentTransaction()
	 * getCurrentTransaction()}.
	 *
	 * @throws CommitFailedException
	 *             if commit fails
	 */
	public void commit() throws CommitFailedException;

	/**
	 * Delegates to {@link Graph#getCurrentTransaction()
	 * getCurrentTransaction()}.
	 */
	public void abort();

	/**
	 * Delegates to {@link Graph#getCurrentTransaction()
	 * getCurrentTransaction()}.
	 *
	 * @return the defined <code>Savepoint</code>
	 */
	public Savepoint defineSavepoint();

	/**
	 * Delegates to {@link Graph#getCurrentTransaction()
	 * getCurrentTransaction()}.
	 *
	 * @param savepoint
	 *            the <code>Savepoint</code> to be restored.
	 *
	 * @throws InvalidSavepointException
	 */
	public void restoreSavepoint(Savepoint savepoint)
			throws InvalidSavepointException;

	/**
	 * Tells whether this graph instance supports transactions.
	 *
	 * @return true if this graph instance supports transactions.
	 */
	public boolean hasTransactionSupport();

	/**
	 * Sorts the vertex sequence according to the given comparator in ascending
	 * order.
	 *
	 * @param comp
	 *            the comparator defining the desired vertex order.
	 */
	public void sortVertices(Comparator<Vertex> comp);

	/**
	 * Sorts the edge sequence according to the given comparator in ascending
	 * order.
	 *
	 * @param comp
	 *            the comparator defining the desired edge order.
	 */
	public void sortEdges(Comparator<Edge> comp);

	/**
	 * Registers the given <code>newListener</code> to the internal listener
	 * list.
	 *
	 * @param newListener
	 *            the new <code>GraphStructureChangedListener</code> to
	 *            register.
	 */
	public void addGraphStructureChangedListener(
			GraphStructureChangedListener newListener);

	/**
	 * Removes the given <code>listener</code> from the internal listener list.
	 *
	 * @param listener
	 *            the <code>GraphStructureChangedListener</code> to be removed.
	 */
	public void removeGraphStructureChangedListener(
			GraphStructureChangedListener listener);

	/**
	 * Removes all <code>GraphStructureChangedListener</code> from the internal
	 * listener list.
	 */
	public void removeAllGraphStructureChangedListeners();

	/**
	 * Returns the amount of registered
	 * <code>GraphStructureChangedListener</code>s.
	 *
	 * @return the amount of registered
	 *         <code>GraphStructureChangedListener</code>s
	 */
	public int getGraphStructureChangedListenerCount();

	public ECARuleManagerInterface getECARuleManager();

	public boolean hasECARuleManager();

	public TraversalContext setTraversalContext(TraversalContext tc);

	public TraversalContext getTraversalContext();

	public GraphFactory getGraphFactory();

	public void setGraphFactory(GraphFactory graphFactory);

	public void save(String filename) throws GraphIOException;

	public void save(String filename, ProgressFunction pf)
			throws GraphIOException;

	public void save(DataOutputStream out) throws GraphIOException;

	public void save(DataOutputStream out, ProgressFunction pf)
			throws GraphIOException;

	@Override
	public GraphClass getAttributedElementClass();
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/base.java
=======
	/**
	 * @return a read-only-<code>Transaction</code>
	 */
	public Transaction newReadOnlyTransaction();

	/**
	 * Sets the given <code>transaction</code> as the active
	 * {@link Transaction} for the current thread.
	 *
	 * @param transaction
	 */
	public void setCurrentTransaction(Transaction transaction);

	/**
	 * @return the currently active <code>Transaction</code> in the current
	 *         thread
	 */
	public Transaction getCurrentTransaction();

	/**
	 * Delegates to {@link Graph#getCurrentTransaction()
	 * getCurrentTransaction()}.
	 *
	 * @throws CommitFailedException
	 *             if commit fails
	 */
	public void commit() throws CommitFailedException;

	/**
	 * Delegates to {@link Graph#getCurrentTransaction()
	 * getCurrentTransaction()}.
	 */
	public void abort();

	/**
	 * Delegates to {@link Graph#getCurrentTransaction()
	 * getCurrentTransaction()}.
	 *
	 * @return the defined <code>Savepoint</code>
	 */
	public Savepoint defineSavepoint();

	/**
	 * Delegates to {@link Graph#getCurrentTransaction()
	 * getCurrentTransaction()}.
	 *
	 * @param savepoint
	 *            the <code>Savepoint</code> to be restored.
	 *
	 * @throws InvalidSavepointException
	 */
	public void restoreSavepoint(Savepoint savepoint)
			throws InvalidSavepointException;

	/**
	 * Tells whether this graph instance supports transactions.
	 *
	 * @return true if this graph instance supports transactions.
	 */
	public boolean hasTransactionSupport();

	/**
	 * Sorts the vertex sequence according to the given comparator in ascending
	 * order.
	 *
	 * @param comp
	 *            the comparator defining the desired vertex order.
	 */
	public void sortVertices(Comparator<Vertex> comp);

	/**
	 * Sorts the edge sequence according to the given comparator in ascending
	 * order.
	 *
	 * @param comp
	 *            the comparator defining the desired edge order.
	 */
	public void sortEdges(Comparator<Edge> comp);

	/**
	 * Registers the given <code>newListener</code> to the internal listener
	 * list.
	 *
	 * @param newListener
	 *            the new <code>GraphStructureChangedListener</code> to
	 *            register.
	 */
	public void addGraphStructureChangedListener(
			GraphStructureChangedListener newListener);

	/**
	 * Removes the given <code>listener</code> from the internal listener list.
	 *
	 * @param listener
	 *            the <code>GraphStructureChangedListener</code> to be removed.
	 */
	public void removeGraphStructureChangedListener(
			GraphStructureChangedListener listener);

	/**
	 * Removes all <code>GraphStructureChangedListener</code> from the internal
	 * listener list.
	 */
	public void removeAllGraphStructureChangedListeners();

	/**
	 * Returns the amount of registered
	 * <code>GraphStructureChangedListener</code>s.
	 *
	 * @return the amount of registered
	 *         <code>GraphStructureChangedListener</code>s
	 */
	public int getGraphStructureChangedListenerCount();

	/**
	 * Returns the {@link de.uni_koblenz.jgralab.eca.ECARuleManager} of
	 * this {@link Graph}, if the {@link de.uni_koblenz.jgralab.eca.ECARuleManager}
	 * is not instantiated, an instance is created
	 * 
	 * @return the {@link de.uni_koblenz.jgralab.eca.ECARuleManager} of this 
	 * 			{@link Graph}
	 */
	public ECARuleManagerInterface getECARuleManager();

	/**
	 * @return whether the {@link de.uni_koblenz.jgralab.eca.ECARuleManager}
	 * 			of this {@link Graph} is instantiated
	 */
	public boolean hasECARuleManager();

	public TraversalContext setTraversalContext(TraversalContext tc);

	public TraversalContext getTraversalContext();

	/**
	 * Returns the {@link GraphFactory} this {@link Graph} uses to 
	 * create {@link Vertex} and {@link Edge} instances
	 * 
	 * @return the {@link GraphFactory} of this {@link Graph}
	 */
	public GraphFactory getGraphFactory();

	/**
	 * Set the {@link GraphFactory} this {@link Graph} uses to 
	 * create {@link Vertex} and {@link Edge} instances
	 * 
	 * @param graphFactory
	 * 				the {@link GraphFactory} to replace the current
	 * 				{@link GraphFactory} of this {@link Graph}
	 */
	public void setGraphFactory(GraphFactory graphFactory);

	/**
	 * Saves this {@link Graph} to the file named <code>filename</code>. 
	 * 
	 * @param filename
	 * 				the name of the TG file to be written
	 * 
	 * @throws GraphIOException
	 * 				if an IOException occurs
	 */
	public void save(String filename) throws GraphIOException;

	/**
	 * Saves this {@link Graph} to the file named <code>filename</code>. 
	 * A {@link ProgressFunction} <code>pf</code> can be used to monitor progress.
	 * 
	 * @param filename
	 * 				the name of the TG file to be written
	 * @param pf
	 * 				a {@link ProgressFunction}, may be <code>null</code>
	 * 
	 * @throws GraphIOException 
	 * 				if an IOException occurs
	 */
	public void save(String filename, ProgressFunction pf)
			throws GraphIOException;

	/**
	 * Saves this {@link Graph} to the stream <code>out</code>. 
	 * The stream is <em>not</em> closed.
	 * 
	 * @param out
	 * 			a DataOutputStream
	 *            
	 * @throws GraphIOException
	 *             if an IOException occurs
	 */
	public void save(DataOutputStream out) throws GraphIOException;

	/**
	 * Saves this {@link Graph} to the stream <code>out</code>. 
	 * A {@link ProgressFunction} <code>pf</code> can be used to monitor progress.
	 * The stream is <em>not</em> closed.
	 * 
	 * @param out
	 * 			a DataOutputStream
	 * @param pf
	 *            a {@link ProgressFunction}, may be <code>null</code>
	 *            
	 * @throws GraphIOException
	 *             if an IOException occurs
	 */
	public void save(DataOutputStream out, ProgressFunction pf)
			throws GraphIOException;
	/**
	 * @return the {@link GraphClass} of this
	 *         {@link Graph}
	 */
	@Override
	public GraphClass getAttributedElementClass();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/Graph.java/right.java
}
