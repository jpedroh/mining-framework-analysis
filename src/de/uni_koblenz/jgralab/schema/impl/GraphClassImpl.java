/*
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
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
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
 * JGraLab - The Java graph laboratory
 * (c) 2006-2008 Institute for Software Technology
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
=======
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
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
 */

package de.uni_koblenz.jgralab.schema.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.InheritanceException;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;
import de.uni_koblenz.jgralab.schema.Attribute;

public final class GraphClassImpl extends
		AttributedElementClassImpl<GraphClass, Graph> implements GraphClass {

	private Map<String, EdgeClass> edgeClasses = new HashMap<String, EdgeClass>();

	private Map<String, GraphElementClass<?, ?>> graphElementClasses = new HashMap<String, GraphElementClass<?, ?>>();

	private Map<String, VertexClass> vertexClasses = new HashMap<String, VertexClass>();

	private DirectedAcyclicGraph<EdgeClass> edgeCsDag = new DirectedAcyclicGraph<EdgeClass>();

	private DirectedAcyclicGraph<VertexClass> vertexCsDag = new DirectedAcyclicGraph<VertexClass>();

	static GraphClass createDefaultGraphClass(SchemaImpl schema) {
		assert schema.getDefaultPackage() != null : "DefaultPackage has not yet been created!";
		assert schema.getDefaultGraphClass() == null : "DefaultGraphClass already created!";
		GraphClass gc = new GraphClassImpl(schema);
		gc.setAbstract(true);
		((GraphClassImpl) gc).setInternal(true);
		return gc;
	}

	private GraphClassImpl(SchemaImpl schema) {
		this(DEFAULTGRAPHCLASS_NAME, schema);
	}

	/**
	 * Creates the <b>sole</b> <code>GraphClass</code> in the
	 * <code>Schema</code>, that holds all <code>GraphElementClasses</code>/
	 * <code>EdgeClasses</code>/ <code>VertexClasses</code>/
	 * <code>AggregationClasses</code>/ <code>CompositionClasses</code>.
	 * <p>
	 * <b>Caution:</b> The <code>GraphClass</code> should only be created by
	 * using
	 * {@link de.uni_koblenz.jgralab.schema.Schema#createGraphClass(String qualifiedName)}
	 * in <code>Schema</code>. Unfortunately, due to restrictions in Java, the
	 * visibility of this constructor cannot be changed without causing serious
	 * issues in the program.
	 * </p>
	 * 
	 * @param qn
	 *            a unique name in the <code>Schema</code>
	 * @param aSchema
	 *            the <code>Schema</code> containing this
	 *            <code>GraphClass</code>
	 */

	protected GraphClassImpl(String gcName, SchemaImpl schema) {
		super(gcName, schema.getDefaultPackage(), schema);
		register();
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	void addEdgeClass(EdgeClass ec) {
		if (edgeClasses.containsKey(ec.getQualifiedName())) {
			throw new SchemaException("Duplicate edge class name '"
					+ ec.getQualifiedName() + "'");
		}
		if (graphElementClasses.containsKey(ec.getQualifiedName())) {
			throw new SchemaException("Edge class name '"
					+ ec.getQualifiedName()
					+ "' already used as vertex class name");
		}
		graphElementClasses.put(ec.getQualifiedName(), ec);
		edgeClasses.put(ec.getQualifiedName(), ec);
		edgeCsDag.createNode(ec);
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
	void addEdgeClass(EdgeClass ec) 
=======
	void addEdgeClass(EdgeClass ec) {
		if (edgeClasses.containsKey(ec.getQualifiedName())) {
			throw new SchemaException("Duplicate edge class name '"
					+ ec.getQualifiedName() + "'");
		}
		if (graphElementClasses.containsKey(ec.getQualifiedName())) {
			throw new SchemaException("Edge class name '"
					+ ec.getQualifiedName()
					+ "' already used as vertex class name");
		}
		graphElementClasses.put(ec.getQualifiedName(), ec);
		edgeClasses.put(ec.getQualifiedName(), ec);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	void addVertexClass(VertexClass vc) {
		if (vertexClasses.containsKey(vc.getQualifiedName())) {
			throw new SchemaException("Duplicate vertex class name '"
					+ vc.getQualifiedName() + "'");
		}
		if (graphElementClasses.containsKey(vc.getQualifiedName())) {
			throw new SchemaException("Vertex class name '"
					+ vc.getQualifiedName()
					+ "' already used as edge class name");
		}

		graphElementClasses.put(vc.getQualifiedName(), vc);
		vertexClasses.put(vc.getQualifiedName(), vc);
		vertexCsDag.createNode(vc);
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
	void addVertexClass(VertexClass vc) 
=======
	void addVertexClass(VertexClass vc) {
		if (vertexClasses.containsKey(vc.getQualifiedName())) {
			throw new SchemaException("Duplicate vertex class name '"
					+ vc.getQualifiedName() + "'");
		}
		if (graphElementClasses.containsKey(vc.getQualifiedName())) {
			throw new SchemaException("Vertex class name '"
					+ vc.getQualifiedName()
					+ "' already used as edge class name");
		}

		graphElementClasses.put(vc.getQualifiedName(), vc);
		vertexClasses.put(vc.getQualifiedName(), vc);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

	@Override
	public void addSuperClass(GraphClass superClass) {
		// only the internal abstract base class "Graph" can be a superclass
		if (!superClass.getQualifiedName().equals(
				getSchema().getDefaultGraphClass().getQualifiedName())) {
			throw new InheritanceException(
					"GraphClass can not be generealized.");
		}
		super.addSuperClass(superClass);
	}

	@Override
	protected final void register() {
		assert parentPackage == getSchema().getDefaultPackage() : "The GraphClass must be in the default package.";
		((PackageImpl) parentPackage).addGraphClass(this);
		if (!getSimpleName().equals(GraphClass.DEFAULTGRAPHCLASS_NAME)) {
			((SchemaImpl) getSchema()).setGraphClass(this);
		}
	}

	@Override
	public String getVariableName() {
		return "gc_" + getQualifiedName().replace('.', '_');
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	@Override
	public EdgeClass createEdgeClass(String qualifiedName, VertexClass from,
			int fromMin, int fromMax, String fromRoleName,
			AggregationKind aggrFrom, VertexClass to, int toMin, int toMax,
			String toRoleName, AggregationKind aggrTo) {

		if (isFinished()) {
			throw new SchemaException("No changes to finished schema!");
		}

		if (!(aggrFrom == AggregationKind.NONE)
				&& !(aggrTo == AggregationKind.NONE)) {
			throw new SchemaException(
					"At least one end of each class must be of AggregationKind NONE at EdgeClass "
							+ qualifiedName);
		}
		String[] qn = SchemaImpl.splitQualifiedName(qualifiedName);
		Package parent = ((SchemaImpl) getSchema())
				.createPackageWithParents(qn[0]);
		EdgeClassImpl ec = new EdgeClassImpl(qn[1], parent, this, from,
				fromMin, fromMax, fromRoleName, aggrFrom, to, toMin, toMax,
				toRoleName, aggrTo);
		if (!ec.getQualifiedName().equals(EdgeClass.DEFAULTEDGECLASS_NAME)) {
			EdgeClass s = getSchema().getDefaultEdgeClass();
			ec.addSuperClass(s);
		}
		return ec;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
=======
	@Override
	public EdgeClass createEdgeClass(String qualifiedName, VertexClass from,
			int fromMin, int fromMax, String fromRoleName,
			AggregationKind aggrFrom, VertexClass to, int toMin, int toMax,
			String toRoleName, AggregationKind aggrTo) {
		assertNotFinished();
		if (!(aggrFrom == AggregationKind.NONE)
				&& !(aggrTo == AggregationKind.NONE)) {
			throw new SchemaException(
					"At least one end of each class must be of AggregationKind NONE at EdgeClass "
							+ qualifiedName);
		}
		String[] qn = SchemaImpl.splitQualifiedName(qualifiedName);
		PackageImpl parent = schema.createPackageWithParents(qn[0]);
		EdgeClassImpl ec = new EdgeClassImpl(qn[1], parent, this, from,
				fromMin, fromMax, fromRoleName, aggrFrom, to, toMin, toMax,
				toRoleName, aggrTo);
		if (defaultEdgeClass != null) {
			ec.addSuperClass(defaultEdgeClass);
		}
		return ec;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	@Override
	public VertexClass createVertexClass(String qualifiedName) {
		if (isFinished()) {
			throw new SchemaException("No changes to finished schema!");
		}

		String[] qn = SchemaImpl.splitQualifiedName(qualifiedName);
		Package parent = ((SchemaImpl) getSchema())
				.createPackageWithParents(qn[0]);
		VertexClassImpl vc = new VertexClassImpl(qn[1], parent, this);
		vc.addSuperClass(getSchema().getDefaultVertexClass());
		return vc;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
=======
	@Override
	public VertexClass createVertexClass(String qualifiedName) {
		assertNotFinished();

		String[] qn = SchemaImpl.splitQualifiedName(qualifiedName);
		PackageImpl parent = ((SchemaImpl) getSchema())
				.createPackageWithParents(qn[0]);
		VertexClassImpl vc = new VertexClassImpl(qn[1], parent, this);
		if (defaultVertexClass != null) {
			vc.addSuperClass(defaultVertexClass);
		}
		return vc;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

	@Override
	public boolean knowsOwn(GraphElementClass<?, ?> aGraphElementClass) {
		return (graphElementClasses.containsKey(aGraphElementClass
				.getQualifiedName()));
	}

	@Override
	public boolean knowsOwn(String qn) {
		return (graphElementClasses.containsKey(qn));
	}

	@Override
	public boolean knows(GraphElementClass<?, ?> aGraphElementClass) {
		if (graphElementClasses.containsKey(aGraphElementClass
				.getQualifiedName())) {
			return true;
		}
		for (AttributedElementClass<?, ?> superClass : directSuperClasses) {
			if (((GraphClass) superClass).knows(aGraphElementClass)) {
				return true;
			}
		}
		return false;
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	@Override
	public boolean knows(String qn) {
		if (graphElementClasses.containsKey(qn)) {
			return true;
		}
		for (AttributedElementClass<?, ?> superClass : directSuperClasses) {
			if (((GraphClass) superClass).knows(qn)) {
				return true;
			}
		}
		return false;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
=======
	@Override
	public boolean knows(String qn) {
		return graphElementClasses.containsKey(qn);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	@Override
	public GraphElementClass<?, ?> getGraphElementClass(String qn) {
		if (graphElementClasses.containsKey(qn)) {
			return graphElementClasses.get(qn);
		}
		for (AttributedElementClass<?, ?> superClass : directSuperClasses) {
			if (((GraphClass) superClass).knows(qn)) {
				return ((GraphClass) superClass).getGraphElementClass(qn);
			}
		}
		return null;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
	@Override
	public GraphElementClass getGraphElementClass(QualifiedName qn) {
		if (graphElementClasses.containsKey(qn))
			return graphElementClasses.get(qn);
		for (AttributedElementClass superClass : directSuperClasses) {
			if (((GraphClass) superClass).knows(qn))
				return ((GraphClass) superClass).getGraphElementClass(qn);
		}
		return null;
	}
=======
	@Override
	public GraphElementClass<?, ?> getGraphElementClass(String qn) {
		return graphElementClasses.get(qn);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	public String getDescriptionString() {
		StringBuilder output = new StringBuilder("GraphClassImpl '"
				+ getQualifiedName() + "'");
		if (isAbstract()) {
			output.append(" (abstract)");
		}
		output.append(": \n");

		output.append("subClasses of '" + getQualifiedName() + "': ");
		Iterator<GraphClass> it = getAllSubClasses().iterator();
		while (it.hasNext()) {
			output.append("'" + ((GraphClassImpl) it.next()).getQualifiedName()
					+ "' ");
		}

		output.append("\nsuperClasses of '" + getQualifiedName() + "': ");
		Iterator<GraphClass> it2 = getAllSuperClasses().iterator();
		while (it2.hasNext()) {
			output.append("'"
					+ ((GraphClassImpl) it2.next()).getQualifiedName() + "' ");
		}
		output.append(attributesToString());

		output.append("\n\nGraphElementClasses of '" + getQualifiedName()
				+ "':\n\n");
		Iterator<GraphElementClass<?, ?>> it3 = graphElementClasses.values()
				.iterator();
		while (it3.hasNext()) {
			output.append(it3.next().toString() + "\n");
		}
		return output.toString();
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
	@Override
	public String toString() {
		String output = "GraphClassImpl '" + super.getName() + "'";
		if (isAbstract())
			output += " (abstract)";
		output += ": \n";

		output += "subClasses of '" + super.getName() + "': ";
		Iterator<AttributedElementClass> it = getAllSubClasses().iterator();
		while (it.hasNext()) {
			output += "'" + ((GraphClassImpl) it.next()).getName() + "' ";
		}

		output += "\nsuperClasses of '" + super.getName() + "': ";
		Iterator<AttributedElementClass> it2 = getAllSuperClasses().iterator();
		while (it2.hasNext()) {
			output += "'" + ((GraphClassImpl) it2.next()).getName() + "' ";
		}
		output += attributesToString();

		output += "\n\nGraphElementClasses of '" + super.getName() + "':\n\n";
		Iterator<GraphElementClass> it3 = graphElementClasses.values()
				.iterator();
		while (it3.hasNext()) {
			output += it3.next().toString() + "\n";
		}
		return output;
	}
=======
	public String getDescriptionString() {
		StringBuilder output = new StringBuilder("GraphClassImpl '"
				+ getQualifiedName() + "'");
		if (isAbstract()) {
			output.append(" (abstract)");
		}
		output.append(":\n");
		output.append(attributesToString());
		output.append("\n\nGraphElementClasses of '" + getQualifiedName()
				+ "':\n\n");
		Iterator<GraphElementClass<?, ?>> it3 = graphElementClasses.values()
				.iterator();
		while (it3.hasNext()) {
			output.append(it3.next().toString() + "\n");
		}
		return output.toString();
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

	@Override
	public List<GraphElementClass<?, ?>> getGraphElementClasses() {
		return new ArrayList<GraphElementClass<?, ?>>(
				graphElementClasses.values());
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	@Override
	public List<EdgeClass> getEdgeClasses() {
		return edgeCsDag.getNodesInTopologicalOrder();
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
=======
	@Override
	public List<EdgeClass> getEdgeClasses() {
		return edgeClassDag.getNodesInTopologicalOrder();
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	@Override
	public List<VertexClass> getVertexClasses() {
		return vertexCsDag.getNodesInTopologicalOrder();
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
=======
	@Override
	public List<VertexClass> getVertexClasses() {
		return vertexClassDag.getNodesInTopologicalOrder();
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	@Override
	public VertexClass getVertexClass(String qn) {
		VertexClass vc = vertexClasses.get(qn);
		if (vc != null) {
			return vc;
		}
		for (GraphClass superclass : directSuperClasses) {
			vc = superclass.getVertexClass(qn);
			if (vc != null) {
				return vc;
			}
		}
		return null;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
=======
	@Override
	public VertexClass getVertexClass(String qn) {
		return vertexClasses.get(qn);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	@Override
	public EdgeClass getEdgeClass(String qn) {
		EdgeClass ec = edgeClasses.get(qn);
		if (ec != null) {
			return ec;
		}
		for (GraphClass superclass : directSuperClasses) {
			ec = superclass.getEdgeClass(qn);
			if (ec != null) {
				return ec;
			}
		}
		return null;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
=======
	@Override
	public EdgeClass getEdgeClass(String qn) {
		return edgeClasses.get(qn);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

	@Override
	public int getEdgeClassCount() {
		return edgeClasses.size();
	}

	@Override
	public int getVertexClassCount() {
		return vertexClasses.size();
	}

	protected DirectedAcyclicGraph<EdgeClass> getEdgeCsDag() {
		return edgeCsDag;
	}

	protected DirectedAcyclicGraph<VertexClass> getVertexCsDag() {
		return vertexCsDag;
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
	@Override
	protected void finish() {
		for (VertexClass vc : vertexCsDag.getNodesInTopologicalOrder()) {
			((VertexClassImpl) vc).finish();
		}
		for (EdgeClass ec : edgeCsDag.getNodesInTopologicalOrder()) {
			((EdgeClassImpl) ec).finish();
		}
		super.finish();
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/base.java
=======
	@Override
	protected void finish() {
		assertNotFinished();
		vertexClassDag.finish();
		edgeClassDag.finish();
		for (VertexClass vc : vertexClassDag.getNodesInTopologicalOrder()) {
			((VertexClassImpl) vc).finish();
		}
		for (EdgeClass ec : edgeClassDag.getNodesInTopologicalOrder()) {
			((EdgeClassImpl) ec).finish();
		}
		super.finish();
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

	@Override
	protected void reopen() {
		for (VertexClass vc : vertexCsDag.getNodesInTopologicalOrder()) {
			((VertexClassImpl) vc).reopen();
		}
		for (EdgeClass ec : edgeCsDag.getNodesInTopologicalOrder()) {
			((EdgeClassImpl) ec).reopen();
		}
		super.reopen();
	}

	DirectedAcyclicGraph<VertexClass> vertexClassDag = new DirectedAcyclicGraph<VertexClass>(
			true);

	DirectedAcyclicGraph<EdgeClass> edgeClassDag = new DirectedAcyclicGraph<EdgeClass>(
			true);

	private VertexClassImpl defaultVertexClass;

	private EdgeClassImpl defaultEdgeClass;

	/**
	 * Creates the <b>sole</b> <code>GraphClass</code> in the
	 * <code>Schema</code>, that holds all <code>GraphElementClasses</code>/
	 * <code>EdgeClasses</code>/ <code>VertexClasses</code>/
	 * <code>AggregationClasses</code>/ <code>CompositionClasses</code>.
	 * <p>
	 * <b>Caution:</b> The <code>GraphClass</code> should only be created by
	 * using
	 * {@link de.uni_koblenz.jgralab.schema.Schema#createGraphClass(String qualifiedName)}
	 * in <code>Schema</code>. Unfortunately, due to restrictions in Java, the
	 * visibility of this constructor cannot be changed without causing serious
	 * issues in the program.
	 * </p>
	 * 
	 * @param qn
	 *            a unique name in the <code>Schema</code>
	 * @param aSchema
	 *            the <code>Schema</code> containing this
	 *            <code>GraphClass</code>
	 */

	@Override
	public VertexClass getDefaultVertexClass() {
		return defaultVertexClass;
	}

	private VertexClassImpl createDefaultVertexClass() {
		VertexClassImpl vc = new VertexClassImpl(
				VertexClass.DEFAULTVERTEXCLASS_NAME,
				(PackageImpl) schema.getDefaultPackage(), this);
		vc.setAbstract(true);
		vc.setInternal(true);
		return vc;
	}

	private EdgeClassImpl createDefaultEdgeClass() {
		assert getDefaultVertexClass() != null : "Default VertexClass has not yet been created!";
		assert getDefaultEdgeClass() == null : "Default EdgeClass already created!";
		EdgeClassImpl ec = new EdgeClassImpl(EdgeClass.DEFAULTEDGECLASS_NAME,
				(PackageImpl) schema.getDefaultPackage(), this,
				defaultVertexClass, 0, Integer.MAX_VALUE, "",
				AggregationKind.NONE, defaultVertexClass, 0, Integer.MAX_VALUE,
				"", AggregationKind.NONE);
		ec.setAbstract(true);
		ec.setInternal(true);
		return ec;
	}

	@Override
	public EdgeClass getDefaultEdgeClass() {
		return defaultEdgeClass;
	}

	@Override
	public boolean hasOwnAttributes() {
		return hasAttributes();
	}

	@Override
	public Attribute getOwnAttribute(String name) {
		return getAttribute(name);
	}

	@Override
	public int getOwnAttributeCount() {
		return getAttributeCount();
	}

	@Override
	public List<Attribute> getOwnAttributeList() {
		return getAttributeList();
	}
}
