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

package de.uni_koblenz.jgralab.schema;

import java.util.List;

import de.uni_koblenz.jgralab.Graph;

/**
 * Represents a <code>GraphClass</code> in the <code>Schema</code>, that holds
 * all <code>GraphElementClasses</code>.
 * 
 * <p>
 * <b>Note:</b> in the following, <code>graphClass</code>, and
 * <code>graphClass'</code>, will represent the states of the given
 * <code>GraphClass</code> before, respectively after, any operation.
 * </p>
 * 
 * <p>
 * <b>Note:</b> in the following it is understood that method arguments differ
 * from <code>null</code>. Therefore there will be no preconditions addressing
 * this matter.
 * </p>
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
 * Represents a <code>GraphClass</code> in the <code>Schema</code>, that holds
 * all <code>GraphElementClasses</code>.
 * 
 * <p>
 * <b>Note:</b> in the following, <code>graphClass</code>, and
 * <code>graphClass'</code>, will represent the states of the given
 * <code>GraphClass</code> before, respectively after, any operation.
 * </p>
 * 
 * <p>
 * <b>Note:</b> in the following it is understood that method arguments differ
 * from <code>null</code>. Therefore there will be no preconditions addressing
 * this matter.
 * </p>
 * 
 * @author ist@uni-koblenz.de
 */
public interface GraphClass extends AttributedElementClass<GraphClass, Graph> {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/GraphClass.java/left.java
	public final static String DEFAULTGRAPHCLASS_NAME = "Graph";
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/GraphClass.java/base.java
	/**
	 * creates an edge class between from and to with the edgeclassname name
	 * 
	 * @param name
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the edge class starts
	 * @param to
	 *            the vertex class where the edge class ends
	 * @return the created edge class
	 */
	public EdgeClass createEdgeClass(QualifiedName name, VertexClass from,
			VertexClass to) ;

	/**
	 * creates an edge class between vertex class from with the rolename
	 * fromRoleName and vertex class to with the rolename toRoleName and the
	 * edgeclassname name
	 * 
	 * @param name
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the edge class starts
	 * @param fromRoleName
	 *            the unique rolename of the 'from'-end
	 * @param to
	 *            the vertex class where the edge class ends
	 * @param toRoleName
	 *            the unique rolename of the 'to'-end
	 * @return the created edge class
	 */
	public EdgeClass createEdgeClass(QualifiedName name, VertexClass from,
			String fromRoleName, VertexClass to, String toRoleName)
			;

	/**
	 * creates an edge class between vertex class from, multiplicity fromMin and
	 * fromMax, and vertex class to, multiplicity toMin and toMax with the
	 * edgeclassname name
	 * 
	 * @param name
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the edge class starts
	 * @param fromMin
	 *            the minimum multiplicity of the edge class on the 'from'-end
	 * @param fromMax
	 *            the maximum multiplicity of the edge class on the 'from'-end
	 * @param to
	 *            the vertex class where the edge class ends
	 * @param toMin
	 *            the minimum multiplicity of the edge class on the 'to'-end
	 * @param toMax
	 *            the maximum multiplicity of the edge class on the 'to-end
	 * @return the created edge class
	 */
	public EdgeClass createEdgeClass(QualifiedName name, VertexClass from,
			int fromMin, int fromMax, VertexClass to, int toMin, int toMax)
			;
=======
	/**
	 * creates a vertex class with the vertexclassname name
	 * 
	 * @param qualifiedName
	 *            the qualified name of the vertex class to be created
	 * @return the created vertex class
	 */
	public VertexClass createVertexClass(String qualifiedName);

	/**
	 * @return the default VertexClass of the schema, that is the VertexClass
	 *         with the name "Vertex"
	 */
	public VertexClass getDefaultVertexClass();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/GraphClass.java/right.java

	/**
	 * creates an edge class between vertex class from, multiplicity fromMin and
	 * fromMax with the rolename fromRoleName, and vertex class to, multiplicity
	 * toMin and toMax with the rolename toRoleName and the edgeclassname name
	 * 
	 * @param qualifiedName
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the edge class starts
	 * @param fromMin
	 *            the minimum multiplicity of the edge class on the 'from'-end
	 * @param fromMax
	 *            the maximum multiplicity of the edge class on the 'from'-end
	 * @param fromRoleName
	 *            the unique rolename of the 'from'-end
	 * @param aggrFrom
	 *            the aggregation kind of the 'from' end
	 * @param to
	 *            the vertex class where the edge class ends
	 * @param toMin
	 *            the minimum multiplicity of the edge class on the 'to'-end
	 * @param toMax
	 *            the maximum multiplicity of the edge class on the 'to-end
	 * @param toRoleName
	 *            the unique rolename of the 'to'-end
	 * @param aggrTo
	 *            the aggregation kind of the 'to' end
	 * @return the created edge class
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/GraphClass.java/left.java
	public EdgeClass createEdgeClass(String qualifiedName, VertexClass from,
			int fromMin, int fromMax, String fromRoleName,
			AggregationKind aggrFrom, VertexClass to, int toMin, int toMax,
			String toRoleName, AggregationKind aggrTo);

	/**
	 * creates a vertex class with the vertexclassname name
	 * 
	 * @param qualifiedName
	 *            the qualified name of the vertex class to be created
	 * @return the created vertex class
	 */
	public VertexClass createVertexClass(String qualifiedName);
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/GraphClass.java/base.java
	public EdgeClass createEdgeClass(QualifiedName name, VertexClass from,
			int fromMin, int fromMax, String fromRoleName, VertexClass to,
			int toMin, int toMax, String toRoleName) ;

	/**
	 * creates an aggregation class between from and to with the
	 * aggregationclassname name
	 * 
	 * @param name
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the aggregation class starts
	 * @param aggregateFrom
	 *            set to TRUE, if the aggregation is on the 'from'-side of the
	 *            aggregation class, set to FALSE, if the aggregation is on the
	 *            'to'-side of the aggregation class
	 * @param to
	 *            the vertex class where the aggregation class ends
	 * @return the created aggregation class
	 */
	public AggregationClass createAggregationClass(QualifiedName name,
			VertexClass from, boolean aggregateFrom, VertexClass to)
			;

	/**
	 * creates an aggregation class between vertex class from with the rolename
	 * fromRoleName and vertex class to with the rolename toRoleName and the
	 * aggregationclassname name
	 * 
	 * @param name
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the aggregation class starts
	 * @param fromRoleName
	 *            the unique rolename of the 'from'-end
	 * @param aggregateFrom
	 *            set to TRUE, if the aggregation is on the 'from'-side of the
	 *            aggregation class, set to FALSE, if the aggregation is on the
	 *            'to'-side of the aggregation class
	 * @param to
	 *            the vertex class where the aggregation class ends
	 * @param toRoleName
	 *            the unique rolename of the 'to'-end
	 * @return the created aggregation class
	 */
	public AggregationClass createAggregationClass(QualifiedName name,
			VertexClass from, String fromRoleName, boolean aggregateFrom,
			VertexClass to, String toRoleName) ;

	/**
	 * creates an aggregation class between vertex class from, multiplicity
	 * fromMin and fromMax, and vertex class to, multiplicity toMin and toMax
	 * with the aggregationclassname name
	 * 
	 * @param name
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the aggregation class starts
	 * @param fromMin
	 *            the minimum multiplicity of the aggregation class on the
	 *            'from'-end
	 * @param fromMax
	 *            the maximum multiplicity of the aggregation class on the
	 *            'from'-end
	 * @param aggregateFrom
	 *            set to TRUE, if the aggregation is on the 'from'-side of the
	 *            aggregation class, set to FALSE, if the aggregation is on the
	 *            'to'-side of the aggregation class
	 * @param to
	 *            the vertex class where the aggregation class ends
	 * @param toMin
	 *            the minimum multiplicity of the aggregation class on the
	 *            'to'-end
	 * @param toMax
	 *            the maximum multiplicity of the aggregation class on the
	 *            'to-end
	 * @return the created aggregation class
	 */
	public AggregationClass createAggregationClass(QualifiedName name,
			VertexClass from, int fromMin, int fromMax, boolean aggregateFrom,
			VertexClass to, int toMin, int toMax) ;

	/**
	 * creates an aggregation class between vertex class from, multiplicity
	 * fromMin and fromMax with the rolename fromRoleName, and vertex class to,
	 * multiplicity toMin and toMax with the rolename toRoleName and the
	 * aggregationclassname name
	 * 
	 * @param name
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the aggregation class starts
	 * @param fromMin
	 *            the minimum multiplicity of the aggregation class on the
	 *            'from'-end
	 * @param fromMax
	 *            the maximum multiplicity of the aggregation class on the
	 *            'from'-end
	 * @param fromRoleName
	 *            the unique rolename of the 'from'-end
	 * @param aggregateFrom
	 *            set to TRUE, if the aggregation is on the 'from'-side of the
	 *            aggregation class, set to FALSE, if the aggregation is on the
	 *            'to'-side of the aggregation class
	 * @param to
	 *            the vertex class where the aggregation class ends
	 * @param toMin
	 *            the minimum multiplicity of the aggregation class on the
	 *            'to'-end
	 * @param toMax
	 *            the maximum multiplicity of the aggregation class on the
	 *            'to-end
	 * @param toRoleName
	 *            the unique rolename of the 'to'-end
	 * @return the created aggregation class
	 */
	public AggregationClass createAggregationClass(QualifiedName name,
			VertexClass from, int fromMin, int fromMax, String fromRoleName,
			boolean aggregateFrom, VertexClass to, int toMin, int toMax,
			String toRoleName) ;

	/**
	 * creates an composition class between from and to with the
	 * compositionclassname name
	 * 
	 * @param name
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the composition class starts
	 * @param compositeFrom
	 *            set to TRUE, if the composition is on the 'from'-side of the
	 *            composition class, set to FALSE, if the composition is on the
	 *            'to'-side of the composition class
	 * @param to
	 *            the vertex class where the composition class ends
	 * @return the created composition class
	 */
	public CompositionClass createCompositionClass(QualifiedName name,
			VertexClass from, boolean compositeFrom, VertexClass to)
			;

	/**
	 * creates a composition class between vertex class from with the rolename
	 * fromRoleName and vertex class to with the rolename toRoleName and the
	 * compositionclassname name
	 * 
	 * @param name
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the composition class starts
	 * @param fromRoleName
	 *            the unique rolename of the 'from'-end
	 * @param compositeFrom
	 *            set to TRUE, if the composition is on the 'from'-side of the
	 *            composition class, set to FALSE, if the composition is on the
	 *            'to'-side of the composition class
	 * @param to
	 *            the vertex class where the composition class ends
	 * @param toRoleName
	 *            the unique rolename of the 'to'-end
	 * @return the created composition class
	 */
	public CompositionClass createCompositionClass(QualifiedName name,
			VertexClass from, String fromRoleName, boolean compositeFrom,
			VertexClass to, String toRoleName) ;

	/**
	 * creates a composition class between vertex class from, multiplicity
	 * fromMin and fromMax, and vertex class to, multiplicity toMin and toMax
	 * with the compositionclassname name
	 * 
	 * @param name
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the composition class starts
	 * @param fromMin
	 *            the minimum multiplicity of the composition class on the
	 *            'from'-end
	 * @param fromMax
	 *            the maximum multiplicity of the composition class on the
	 *            'from'-end
	 * @param compositeFrom
	 *            set to TRUE, if the composition is on the 'from'-side of the
	 *            composition class, set to FALSE, if the composition is on the
	 *            'to'-side of the composition class
	 * @param to
	 *            the vertex class where the composition class ends
	 * @param toMin
	 *            the minimum multiplicity of the composition class on the
	 *            'to'-end
	 * @param toMax
	 *            the maximum multiplicity of the composition class on the
	 *            'to-end
	 * @return the created composition class
	 */
	public CompositionClass createCompositionClass(QualifiedName name,
			VertexClass from, int fromMin, int fromMax, boolean compositeFrom,
			VertexClass to, int toMin, int toMax) ;

	/**
	 * creates a composition class between vertex class from, multiplicity
	 * fromMin and fromMax with the rolename fromRoleName, and vertex class to,
	 * multiplicity toMin and toMax with the rolename toRoleName and the
	 * composition classname name
	 * 
	 * @param name
	 *            a unique name in the schema
	 * @param from
	 *            the vertex class where the composition class starts
	 * @param fromMin
	 *            the minimum multiplicity of the composition class on the
	 *            'from'-end
	 * @param fromMax
	 *            the maximum multiplicity of the composition class on the
	 *            'from'-end
	 * @param fromRoleName
	 *            the unique rolename of the 'from'-end
	 * @param compositeFrom
	 *            set to TRUE, if the composition is on the 'from'-side of the
	 *            composition class, set to FALSE, if the composition is on the
	 *            'to'-side of the composition class
	 * @param to
	 *            the vertex class where the composition class ends
	 * @param toMin
	 *            the minimum multiplicity of the composition class on the
	 *            'to'-end
	 * @param toMax
	 *            the maximum multiplicity of the composition class on the
	 *            'to-end
	 * @param toRoleName
	 *            the unique rolename of the 'to'-end
	 * @return the created composition class
	 */
	public CompositionClass createCompositionClass(QualifiedName name,
			VertexClass from, int fromMin, int fromMax, String fromRoleName,
			boolean compositeFrom, VertexClass to, int toMin, int toMax,
			String toRoleName) ;

	/**
	 * creates a vertex class with the vertexclassname name
	 * 
	 * @param name
	 *            the name of the vertex class to be created
	 * @return the created vertex class
	 */
	public VertexClass createVertexClass(QualifiedName name) ;

	/**
	 * addSuperClass can not be called for GraphClass and always throws a SchemaException.
	 * @param superClass
	 *            a graph class
	 */
	public void addSuperClass(GraphClass superClass) ;

	/**
	 * addSubClass can not be called for GraphClass and always throws a SchemaException.
	 * @param subClass
	 *            a graph class
	 */
	public void addSubClass(GraphClass subClass);

	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knowsOwn(GraphElementClass aGraphElementClass);
	
	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knowsOwn(QualifiedName aGraphElementClass);

	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class name
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knows(GraphElementClass aGraphElementClass);
	
	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class name
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knows(QualifiedName aGraphElementClass);
=======
	public EdgeClass createEdgeClass(String qualifiedName, VertexClass from,
			int fromMin, int fromMax, String fromRoleName,
			AggregationKind aggrFrom, VertexClass to, int toMin, int toMax,
			String toRoleName, AggregationKind aggrTo);

	/**
	 * @return the default EdgeClass of the schema, that is the EdgeClass with
	 *         the name "Edge"
	 */
	public EdgeClass getDefaultEdgeClass();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/GraphClass.java/right.java

	/**
	 * @param name
	 *            the name to search for
	 * @return the contained graph element class with the name name
	 */
	public GraphElementClass<?, ?> getGraphElementClass(String name);

	/**
	 * @return a list of all EdgeClasses this graphclass knows, including
	 *         inherited EdgeClasses. This list is sorted topologically
	 *         according to the inheritance hierarchy and only includes the
	 *         classes of this schema, i.e, not the default edge class.
	 */
	public List<EdgeClass> getEdgeClasses();

	/**
	 * @return a list of all the edge/vertex/aggregation/composition classes of
	 *         this graph class, including inherited classes
	 */
	public List<GraphElementClass<?, ?>> getGraphElementClasses();

	/**
	 * @return a list of all the vertex classes of this graph class, including
	 *         inherited vertex classes. This list is sorted topologically
	 *         according to the inheritance hierarchy and only includes the
	 *         classes of this schema, i.e, not the default vertex class.
	 */
	public List<VertexClass> getVertexClasses();

	/**
	 * Returns the VertexClass with the given name. This GraphClass and the
	 * superclasses will be searched for a VertexClass with this name
	 * 
	 * @param name
	 *            the name of the VertexClass to search for
	 * @return the VertexClass with the given name or null, if no such
	 *         VertexClass exists
	 */
	public VertexClass getVertexClass(String name);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/GraphClass.java/left.java
	/**
	 * Returns the number of VertexClasses defined in this GraphClass.
	 * 
	 * @return the number of VertexClasses defined in this GraphClass.
	 */
	public int getVertexClassCount();

	/**
	 * Returns the EdgeClass with the given name. This GraphClass and the
	 * superclasses will be searched for a EdgeClass with this name
	 * 
	 * @param name
	 *            the name of the EdgeClass to search for
	 * @return the EdgeClass with the given name or null, if no such EdgeClass
	 *         exists
	 */
	public EdgeClass getEdgeClass(String name);

	/**
	 * Returns the number of EdgeClasses (that is Edge-/Aggregation- and
	 * CompositionClasses) defined in this GraphClass.
	 * 
	 * @return the number of EdgeClasses defined in this GraphClass.
	 */
	public int getEdgeClassCount();

	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knowsOwn(GraphElementClass<?, ?> aGraphElementClass);

	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knowsOwn(String aGraphElementClass);

	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class name
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knows(GraphElementClass<?, ?> aGraphElementClass);

	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class name
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knows(String aGraphElementClass);
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/GraphClass.java/base.java
=======
	/**
	 * Returns the number of VertexClasses defined in this GraphClass.
	 * 
	 * @return the number of VertexClasses defined in this GraphClass.
	 */
	public int getVertexClassCount();

	/**
	 * Returns the EdgeClass with the given name. This GraphClass and the
	 * superclasses will be searched for a EdgeClass with this name
	 * 
	 * @param name
	 *            the name of the EdgeClass to search for
	 * @return the EdgeClass with the given name or null, if no such EdgeClass
	 *         exists
	 */
	public EdgeClass getEdgeClass(String name);

	/**
	 * Returns the number of EdgeClasses (that is Edge-/Aggregation- and
	 * CompositionClasses) defined in this GraphClass.
	 * 
	 * @return the number of EdgeClasses defined in this GraphClass.
	 */
	public int getEdgeClassCount();

	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knowsOwn(String aGraphElementClass);

	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class name
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knows(String aGraphElementClass);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/GraphClass.java/right.java
}
