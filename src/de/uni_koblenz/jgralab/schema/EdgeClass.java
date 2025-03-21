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

package de.uni_koblenz.jgralab.schema;

import de.uni_koblenz.jgralab.Edge;

/**
 * Interface for Edge/Aggregation/Composition classes, instances of this class
 * represent an schema element.
 * 
 * @author ist@uni-koblenz.de
 */
/**
 * Interface for edge classes. Instances of this class represent a grUML
 * EdgeClass schema element.
 *
 * @author ist@uni-koblenz.de
 */
public interface EdgeClass extends GraphElementClass<EdgeClass, Edge> {

	public static final String DEFAULTEDGECLASS_NAME = "Edge";

	public static final String DEFAULTEDGECLASS_NAME = "Edge";

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/EdgeClass.java/left.java
	 * adds a superclass to the list of superclasses, all attributes get
	 * inherited from those classes
	 * 
	 * @param superClass
	 *            the edge class to be added to the list of superclasses if an
	 *            attribute name exists in superClass and in this class
	 * 
	 */
	public void addSuperClass(EdgeClass superClass);

	public IncidenceClass getFrom();

	public IncidenceClass getTo();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.schema.AttributedElementClass#getSchemaClass()
	 */
	public Class<? extends Edge> getSchemaClass();

||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/EdgeClass.java/base.java
	 * adds a superclass to the list of superclasses,
	 * all attributes get inherited from those classes
	 * @param superClass the edge class to be added to the
	 * list of superclasses if an attribute name exists in superClass and in this class
	 * 
	 */
	public void addSuperClass(EdgeClass superClass) ;

	/**
	 * @return the vertex class where the edge class originates
	 */
	public VertexClass getFrom();
	
	/**
	 * @return the maximum multiplicity at the from-side
	 */
	public int getFromMax();

	/**
	 * @return the minimum multiplicity at the from-side
	 */
	public int getFromMin();

	/**
	 * @return the rolename on the from-side
	 */
	public String getFromRolename();
	
	/**
	 * @return the set of rolenames that are redefined by the rolename on the from-side
	 */
	public Set<String> getRedefinedFromRoles();
	
	/**
	 * Redefines the <code>redefinedRoleName</code> with the rolename
	 * defined while the creation of that edge.
	 * That means on the one hand, that edges of this class have the new role name
	 * as roleName on the from-end and on the other hand that the redefined
	 * edge is not longer allowed at the from-vertex class of this edge 
	 * @param redefinedRoleName the rolename to redefine
	 */
	public void redefineFromRole(String redefinedRoleName);
	
	/**
	 * Redefines all <code>redefinedRoleNames</code> with the rolename
	 * defined while the creation of that edge
	 * That means on the one hand, that edges of this class have the new role name
	 * as roleName on the from-end and on the other hand that the redefined
	 * edges are not longer allowed at the from-vertex class of this edge 
	 * @param redefinedRoleNames the rolenames to redefine
	 */
	public void redefineFromRole(Set<String> redefinedRoleNames);

	/**
	 * @return the vertex class where the edge class closes 
	 */
	public VertexClass getTo();

	/**
	 * @return the maximum multiplicity at the to-side
	 */
	public int getToMax();

	/**
	 * @return the minimum mulitplicity at the to-side
	 */
	public int getToMin();

	/**
	 * @return the rolename on the to-side
	 */
	public String getToRolename();
	
	/**
	 * @return the set of rolenames that are redefined by the rolename on the to-side
	 */
	public Set<String> getRedefinedToRoles();
	
	/**
	 * Redefines the <code>redefinedRoleName</code> with the rolename
	 * defined while the creation of that edge
	 * That means on the one hand, that edges of this class have the new role name
	 * as roleName on the to-end and on the other hand that the redefined
	 * edge is not longer allowed at the to-vertex class of this edge 
	 * @param redefinedRoleName the rolename to redefine
	 */
	public void redefineToRole(String redefinedRoleName);
	
	/**
	 * Redefines all <code>redefinedRoleNames</code> with the rolename
	 * defined while the creation of that edge
	 * That means on the one hand, that edges of this class have the new role name
	 * as roleName on the to-end and on the other hand that the redefined
	 * edges are not longer allowed at the to-vertex class of this edge 
	 * @param redefinedRoleNames the rolenames to redefine
	 */
	public void redefineToRole(Set<String> redefinedRoleNames);
	
	/**
	 * @return true, if the connectable VertexClasses and cardinalities of this EdgeClass
	 * satisfy the restrictions of its superclasses 
	 */
	public boolean checkConnectionRestrictions();
	
	/**
	 * Tries to merge the cardinalities of the edges endpoints
	 * @return true if a merge was done successfull, false if no merge was needed or if a merge is not possible
	 *
	 */
	public boolean mergeConnectionCardinalities() ;
	
	
	/**
	 * Tries to merge the VertexClasses of the edges endpoints
	 * @return true if a merge was done successfull, false if no merge was needed
	 * or if a merge is not possible
	 */
	public boolean mergeConnectionVertexClasses() ;
	
	
	/**
	 * @return returns the DirectedEdgeClass-Object consisting of this edge class with 
	 * direction EdgeDirection.IN
	 *
	 */
	public DirectedEdgeClass getInEdgeClass();
	
	/**
	 * @return returns the DirectedEdgeClass-Object consisting of this edge class with 
	 * direction EdgeDirection.OUT
	 *
	 */
	public DirectedEdgeClass getOutEdgeClass();
	
	/*
	 * (non-Javadoc)
	 * @see de.uni_koblenz.jgralab.schema.AttributedElementClass#getM1Class()
	 */
	public Class<? extends Edge> getM1Class();
	
=======
	 * adds a superclass to the list of superclasses, all attributes get
	 * inherited from those classes
	 *
	 * @param superClass
	 *            the edge class to be added to the list of superclasses if an
	 *            attribute name exists in superClass and in this class
	 *
	 */
	public void addSuperClass(EdgeClass superClass);

	public IncidenceClass getFrom();

	public IncidenceClass getTo();

>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/EdgeClass.java/right.java
}
