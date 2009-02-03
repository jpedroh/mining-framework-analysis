/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2009 Institute for Software Technology
 *               University of Koblenz-Landau, Germany
 *
 *               ist@uni-koblenz.de
 *
 * Please report bugs to http://serres.uni-koblenz.de/bugzilla
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.uni_koblenz.jgralab;

import de.uni_koblenz.jgralab.schema.EdgeClass;

/**
 * represents a signed edge, has an orientation
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface Edge extends GraphElement {

	/**
	 * @return the signed id of the incidence, corresponding edge has abs(id)
	 *         for id
	 */
	public int getId();

	/**
	 * @return the next incidence object in iSeq of current vertex
	 */
	public Edge getNextEdge();

	/**
	 * @return the previous incidence object in iSeq of current vertex
	 */
	public Edge getPrevEdge();

	/**
	 * @param orientation
	 *            the orientation the next incidence should have
	 * @return the next incidence object in iSeq of current vertex
	 */
	public Edge getNextEdge(EdgeDirection orientation);

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @return the next incidence in iSeq where the corresponding edge is of
	 *         class anEdgeClass
	 */
	public Edge getNextEdgeOfClass(EdgeClass anEdgeClass);

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @return the next incidence in iSeq where the corresponding edge is of
	 *         class anEdgeClass
	 */
	public Edge getNextEdgeOfClass(Class<? extends Edge> anEdgeClass);

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param orientation
	 *            the orientation the next incidence should have
	 * @return the next incidence in iSeq where the corresponding edge is of
	 *         class anEdgeClass
	 */
	public Edge getNextEdgeOfClass(EdgeClass anEdgeClass,
			EdgeDirection orientation);

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param orientation
	 *            the orientation the next incidence should have
	 * @return the next incidence in iSeq where the corresponding edge is of
	 *         class anEdgeClass
	 */
	public Edge getNextEdgeOfClass(Class<? extends Edge> anEdgeClass,
			EdgeDirection orientation);

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return the next incidence in iSeq where the corresponding edge is of
	 *         explicit class anEdgeClass
	 */
	public Edge getNextEdgeOfClass(EdgeClass anEdgeClass, boolean noSubclasses);

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return the next incidence in iSeq where the corresponding edge is of
	 *         explicit class anEdgeClass
	 */
	public Edge getNextEdgeOfClass(Class<? extends Edge> anEdgeClass,
			boolean noSubclasses);

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param orientation
	 *            the orientation the next incidence should have
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return the next incidence in iSeq where the corresponding edge is of
	 *         explicit class anEdgeClass
	 */
	public Edge getNextEdgeOfClass(EdgeClass anEdgeClass,
			EdgeDirection orientation, boolean noSubclasses);

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param orientation
	 *            the orientation the next incidence should have
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return the next incidence in iSeq where the corresponding edge is of
	 *         explicit class anEdgeClass
	 */
	public Edge getNextEdgeOfClass(Class<? extends Edge> anEdgeClass,
			EdgeDirection orientation, boolean noSubclasses);

	/**
	 * @return the "this" vertex object, described in the orientation chapter of
	 *         the thesis
	 */
	public Vertex getThis();

	/**
	 * @return the "that" vertex object, described in the orientation chapter of
	 *         the thesis
	 */
	public Vertex getThat();

	/**
	 * @return the rolename of the edge at the this-vertex
	 */
	public String getThisRole();

	/**
	 * @return the rolename of the edge at the that-vertex
	 */
	public String getThatRole();

	/**
	 * @return next edge in eSeq
	 */
	public Edge getNextEdgeInGraph();

	/**
	 * @return previous edge in eSeq
	 */
	public Edge getPrevEdgeInGraph();

	/**
	 * @param anEdgeClass
	 * @return next edge of anEdgeClass or its superclasses in eSeq
	 */
	public Edge getNextEdgeOfClassInGraph(EdgeClass anEdgeClass);

	/**
	 * @param anEdgeClass
	 * @return next edge of anEdgeClass or its superclasses in eSeq
	 */
	public Edge getNextEdgeOfClassInGraph(Class<? extends Edge> anEdgeClass);

	/**
	 * @param anEdgeClass
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return next edge object of explicit anEdgeClass in eSeq
	 */
	public Edge getNextEdgeOfClassInGraph(EdgeClass anEdgeClass,
			boolean noSubclasses);

	/**
	 * @param anEdgeClass
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return next edge object of explicit anEdgeClass in eSeq
	 */
	public Edge getNextEdgeOfClassInGraph(Class<? extends Edge> anEdgeClass,
			boolean noSubclasses);

	/**
	 * @return the alpha vertex of this edge
	 */
	public Vertex getAlpha();

	/**
	 * @return the omega vertex of this edge
	 */
	public Vertex getOmega();

	/**
	 * @param e
	 * @return true if this edge is somewhere before e in the lambda sequence of
	 *         the this-vertex
	 */
	public boolean isBefore(Edge e);

	/**
	 * @param e
	 * @return true if this edge is somewhere after e in the lambda sequence of
	 *         the this-vertex
	 */
	public boolean isAfter(Edge e);

	/**
	 * @param e
	 * @return true if this edge is somewhere before e in eSeq
	 */
	public boolean isBeforeInGraph(Edge e);

	/**
	 * puts this edge immediately before e in eSeq
	 * 
	 * @param e
	 */
	public void putBeforeInGraph(Edge e);

	/**
	 * @param e
	 * @return true if this edge is somewhere after e in eSeq
	 */
	public boolean isAfterInGraph(Edge e);

	/**
	 * puts this edge immediately after anEdge in eSeq
	 * 
	 * @param e
	 */
	public void putAfterInGraph(Edge e);

	/**
	 * removes this edge from eSeq and erases its attributes @ if used on an
	 * incidence
	 */
	public void delete();

	/**
	 * sets the alpha vertex to v
	 * 
	 * @param v
	 *            a vertex
	 */
	void setAlpha(Vertex v);

	/**
	 * sets the omega vertex to v
	 * 
	 * @param v
	 *            a vertex
	 */
	void setOmega(Vertex v);

	/**
	 * sets the this vertex to v
	 * 
	 * @param v
	 *            a vertex
	 */
	void setThis(Vertex v);

	/**
	 * sets the that vertex to v
	 * 
	 * @param v
	 *            a vertex
	 */
	void setThat(Vertex v);

	/**
	 * puts this edge immediately before the given edge <code>e</code> in the
	 * incidence list of the <code>this-vertex</code> of this edge. This does
	 * neither affect the global edge sequence eSeq nor the alpha or omega
	 * vertices, only the order of the edges at the <code>this-vertex</code>
	 * of this edge is changed.
	 */
	public void putEdgeBefore(Edge e);

	/**
	 * puts this edge after the after given edge <code>previousEdge</code> in
	 * the incidence list of the <code>this-vertex</code> of this edge. This
	 * does neither affect the global edge sequence eSeq nor the alpha or omega
	 * vertices, only the order of the edges at the <code>this-vertex</code>
	 * of this edge is changed.
	 */
	public void putEdgeAfter(Edge e);

	/**
	 * returns the normal edge of this edge
	 */
	public Edge getNormalEdge();

	/**
	 * returns the reversed edge of this edge, e.g. for the edge -1 the reversed
	 * edge is 1, for the edge 1 the reversed edge is -1.
	 */
	public Edge getReversedEdge();

	/**
	 * returns true if this edge is the "normal" edge, false otherwise
	 */
	public boolean isNormal();

	/**
	 * returns true if this Edge is still present in the Graph (i.e. not
	 * deleted). This check is equivalent to getGraph().containsEdge(this).
	 */
	public boolean isValid();
}