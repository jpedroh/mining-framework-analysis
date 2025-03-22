/*
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/left.java
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
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/base.java
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
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/right.java
 */

package de.uni_koblenz.jgralab.schema.impl;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceDirection;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

public class EdgeClassImpl extends GraphElementClassImpl<EdgeClass, Edge>
		implements EdgeClass {

	private IncidenceClass from, to;

	static EdgeClass createDefaultEdgeClass(Schema schema) {
		assert schema.getDefaultGraphClass() != null : "DefaultGraphClass has not yet been created!";
		assert schema.getDefaultVertexClass() != null : "DefaultVertexClass has not yet been created!";
		assert schema.getDefaultEdgeClass() == null : "DefaultEdgeClass already created!";
		EdgeClass ec = schema.getDefaultGraphClass().createEdgeClass(
				DEFAULTEDGECLASS_NAME, schema.getDefaultVertexClass(), 0,
				Integer.MAX_VALUE, "", AggregationKind.NONE,
				schema.getDefaultVertexClass(), 0, Integer.MAX_VALUE, "",
				AggregationKind.NONE);
		ec.setAbstract(true);
		((EdgeClassImpl) ec).setInternal(true);
		return ec;
	}

	/**
	 * builds a new edge class
	 * 
	 * @param qn
	 *            the unique identifier of the edge class in the schema
	 * @param from
	 *            the vertex class from which the edge class may connect from
	 * @param fromMin
	 *            the minimum multiplicity of the 'from' vertex class,
	 *            represents the minimum allowed number of connections from the
	 *            edge class to the 'from' vertex class
	 * @param fromMax
	 *            the maximum multiplicity of the 'from' vertex class,
	 *            represents the maximum allowed number of connections from the
	 *            edge class to the 'from' vertex class
	 * @param fromRoleName
	 *            a name which identifies the 'from' side of the edge class in a
	 *            unique way
	 * @param to
	 *            the vertex class to which the edge class may connect to
	 * @param toMin
	 *            the minimum multiplicity of the 'to' vertex class, represents
	 *            the minimum allowed number of connections from the edge class
	 *            to the 'to' vertex class
	 * @param toMax
	 *            the minimum multiplicity of the 'to' vertex class, represents
	 *            the maximum allowed number of connections from the edge class
	 *            to the 'to' vertex class
	 * @param toRoleName
	 *            a name which identifies the 'to' side of the edge class in a
	 *            unique way
	 */

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/left.java
	protected EdgeClassImpl(String simpleName,Package pkg,
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/base.java
	protected EdgeClassImpl(String simpleName,QualifiedName qn,
=======
	protected EdgeClassImpl(String simpleName,PackageImpl pkg,
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/right.java
			GraphClassImpl gc, VertexClass from, int fromMin, int fromMax,
			String fromRoleName, AggregationKind aggrFrom, VertexClass to,
			int toMin, int toMax, String toRoleName, AggregationKind aggrTo) {
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/left.java
		super(simpleName, pkg, aGraphClass);
		IncidenceClass fromInc = createIncidenceClass(from, fromRoleName,
				fromMin, fromMax, IncidenceDirection.OUT, aggrFrom);
		IncidenceClass toInc = createIncidenceClass(to, toRoleName, toMin,
				toMax, IncidenceDirection.IN, aggrTo);
		this.from = fromInc;
		this.to = toInc;
		((VertexClassImpl) from).addOutIncidenceClass(fromInc);
		((VertexClassImpl) to).addInIncidenceClass(toInc);
		register();
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/base.java
		super(qn, aGraphClass);
		this.from = from;
		this.to = to;
		this.fromMin = fromMin;
		this.fromMax = fromMax;
		this.toMin = toMin;
		this.toMax = toMax;
		this.fromRolename = fromRoleName;
		this.toRolename = toRoleName;
		redefinedFromRoles = new HashSet<String>();
		redefinedToRoles = new HashSet<String>();
		inEdgeClass = new DirectedEdgeClass(this, EdgeDirection.IN);
		outEdgeClass = new DirectedEdgeClass(this, EdgeDirection.OUT);
=======
		super(simpleName, pkg, gc, gc.edgeClassDag);
		IncidenceClass fromInc = new IncidenceClassImpl(this, from,
				fromRoleName, fromMin, fromMax, IncidenceDirection.OUT,
				aggrFrom);
		IncidenceClass toInc = new IncidenceClassImpl(this, to, toRoleName,
				toMin, toMax, IncidenceDirection.IN, aggrTo);
		this.from = fromInc;
		this.to = toInc;
		((VertexClassImpl) from).addOutIncidenceClass(fromInc);
		((VertexClassImpl) to).addInIncidenceClass(toInc);
		parentPackage.addEdgeClass(this);
		graphClass.addEdgeClass(this);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/right.java
	}

	protected IncidenceClass createIncidenceClass(VertexClass vrtxCls,
			String rolename, int min, int max, IncidenceDirection dir,
			AggregationKind aggr) {
		return new IncidenceClassImpl(this, vrtxCls, rolename, min, max, dir,
				aggr);
	}

	@Override
	protected void register() {
		((PackageImpl) parentPackage).addEdgeClass(this);
		((GraphClassImpl) graphClass).addEdgeClass(this);
	}

	@Override
	public String getVariableName() {
		return "ec_" + getQualifiedName().replace('.', '_');
	}

	@Override
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/left.java
	public void addSuperClass(EdgeClass superClass) {
		// checked in super
		// if(isFinished()){
		// throw new SchemaException("No changes to finished schema!");
		// }
		if ((superClass == this) || (superClass == null)) {
			return;
		}
		checkIncidenceClassSpecialization(getFrom(), superClass.getFrom());
		checkIncidenceClassSpecialization(getTo(), superClass.getTo());
		super.addSuperClass(superClass);
		if (!superClass.equals(getSchema().getDefaultEdgeClass())) {
			((GraphClassImpl) getSchema().getGraphClass()).getEdgeCsDag()
					.createEdge(superClass, (this));
		}
		((IncidenceClassImpl) getFrom()).addSubsettedIncidenceClass(superClass
				.getFrom());
		((IncidenceClassImpl) getTo()).addSubsettedIncidenceClass(superClass
				.getTo());
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/base.java
	public void addSuperClass(EdgeClass superClass) 
=======
	public void addSuperClass(EdgeClass superClass) {
		assertNotFinished();
		if (superClass == this) {
			return;
		}
		checkIncidenceClassSpecialization(getFrom(), superClass.getFrom());
		checkIncidenceClassSpecialization(getTo(), superClass.getTo());
		super.addSuperClass(superClass);

		((IncidenceClassImpl) getFrom()).addSubsettedIncidenceClass(superClass
				.getFrom());
		((IncidenceClassImpl) getTo()).addSubsettedIncidenceClass(superClass
				.getTo());
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/right.java

	@Override @Override
	public final IncidenceClass getFrom() {
		return from;
	}

	@Override @Override
	public final IncidenceClass getTo() {
		return to;
	}

	/**
	 * checks if the incidence classes own and inherited are compatible, i.e. if
	 * the upper multiplicity of own is lower or equal than the one of inherited
	 * and so on
	 * 
	 * @param special
	 * @param general
	 * @throws SchemaException
	 *             upon illegal combinations
	 */

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/left.java
	static void checkIncidenceClassSpecialization(IncidenceClass special,
			IncidenceClass general) {
		// Vertex same
		if ((!general.getVertexClass().isSuperClassOfOrEquals(
				special.getVertexClass()))) {
			String dir = special.getDirection() == IncidenceDirection.OUT ? "Alpha"
					: "Omega";
			throw new SchemaException(
					"An IncidenceClass may specialize only IncidenceClasses whose connected vertex class "
							+ "is identical or a superclass of the own one. Offending EdgeClasses are "
							+ special.getEdgeClass().getQualifiedName()
							+ " which wants to specialize "
							+ general.getEdgeClass().getQualifiedName()
							+ " at end " + dir);
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/base.java
	public boolean checkConnectionRestrictions() {
		Iterator<? extends AttributedElementClass> iter = directSuperClasses
				.iterator();
		while (iter.hasNext()) {
			EdgeClass ec = (EdgeClass) iter.next();
			if (to != ec.getTo() && !to.isSubClassOf(ec.getTo()))
				return false;
			if (toMin < ec.getToMin() || toMin > ec.getToMax())
				return false;
			if (toMax > ec.getToMax() || toMax < ec.getToMin())
				return false;
			if (from != ec.getFrom() && !from.isSubClassOf(ec.getFrom()))
				return false;
			if (fromMin < ec.getFromMin() || fromMin > ec.getFromMax())
				return false;
			if (fromMax > ec.getFromMax() || fromMax < ec.getFromMin())
				return false;
=======
	static void checkIncidenceClassSpecialization(IncidenceClass special,
			IncidenceClass general) {
		// Vertex same
		if (!(general.getVertexClass().equals(special.getVertexClass()) || general
				.getVertexClass().isSuperClassOf(special.getVertexClass()))) {
			String dir = special.getDirection() == IncidenceDirection.OUT ? "Alpha"
					: "Omega";
			throw new SchemaException(
					"An IncidenceClass may specialize only IncidenceClasses whose connected vertex class "
							+ "is identical or a superclass of the own one. Offending EdgeClasses are "
							+ special.getEdgeClass().getQualifiedName()
							+ " which wants to specialize "
							+ general.getEdgeClass().getQualifiedName()
							+ " at end " + dir);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/right.java
		}
		// Multiplicities
		if (special.getMax() > general.getMax()) {
			String dir = special.getDirection() == IncidenceDirection.OUT ? "Alpha"
					: "Omega";
			throw new SchemaException(
					"The multiplicity of an edge class may not be larger than "
							+ "the multiplicities of its superclass. Offending EdgeClasses are "
							+ special.getEdgeClass().getQualifiedName()
							+ " and "
							+ general.getEdgeClass().getQualifiedName()
							+ " at end " + dir);
		}

		// name clashes
		if (general.getRolename().equals(special.getRolename())
				&& !general.getRolename().isEmpty()
				&& !special.getRolename().isEmpty()) {
			String dir = special.getDirection() == IncidenceDirection.OUT ? "Alpha"
					: "Omega";
			throw new SchemaException(
					"An IncidenceClass may only redefine (or subset) an IncidenceClass with a different name. Offending"
							+ "EdgeClasses are "
							+ special.getEdgeClass().getQualifiedName()
							+ " and "
							+ general.getEdgeClass().getQualifiedName()
							+ " at end " + dir);
		}
		for (IncidenceClass ic : general.getSubsettedIncidenceClasses()) {
			if (ic.getRolename().equals(special.getRolename())
					&& !general.getRolename().isEmpty()
					&& !ic.getRolename().isEmpty()) {
				String dir = ic.getDirection() == IncidenceDirection.OUT ? "Alpha"
						: "Omega";
				throw new SchemaException(
						"An IncidenceClass may only redefine (or subset) an IncidenceClass with a different name. Offending"
								+ "EdgeClasses are "
								+ special.getEdgeClass().getQualifiedName()
								+ " and "
								+ ic.getEdgeClass().getQualifiedName()
								+ " at end " + dir);
			}
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/left.java
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/base.java
		if ((mostSpecialTo != getTo()) || (mostSpecialFrom != getFrom())) {
			to = mostSpecialTo;
			from = mostSpecialFrom;
			return true;
		}
		return false;
=======
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/EdgeClassImpl.java/right.java
	}
}
