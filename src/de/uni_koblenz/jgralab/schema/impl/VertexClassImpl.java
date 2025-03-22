/*
 * JGraLab - The Java Graph Laboratory
 *
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
 * Copyright (C) 2006-2011 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
 *               ist@uni-koblenz.de
=======
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
 *
 * For bug reports, documentation and further information, visit
 *
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
 *                         http://jgralab.uni-koblenz.de
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
=======
 *                         https://github.com/jgralab/jgralab
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
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

package de.uni_koblenz.jgralab.schema.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;
import de.uni_koblenz.jgralab.schema.IncidenceDirection;

public final class VertexClassImpl extends
GraphElementClassImpl<VertexClass, Vertex> implements VertexClass {

	/**
	 * the own in IncidenceClasses
	 */
	private Set<IncidenceClass> inIncidenceClasses = new HashSet<IncidenceClass>();
	/**
	 * the in IncidenceClasses - only set if schema is finish
	 */
	private Set<IncidenceClass> allInIncidenceClasses;
	/**
	 * the own out IncidenceClasses
	 */
	private Set<IncidenceClass> outIncidenceClasses = new HashSet<IncidenceClass>();
	/**
	 * the out IncidenceClasses - only set if schema is finish
	 */
	private Set<IncidenceClass> allOutIncidenceClasses;
	/**
	 * the valid from far IncidenceClasses - only set if schema is finished
	 */
	private Set<IncidenceClass> validFromFarIncidenceClasses;
	/**
	 * the valid from EdgeClasses - only set if schema is finished
	 */
	private Set<EdgeClass> validFromEdgeClasses;
	/**
	 * the valid to EdgeClasses - only set if schema is finished
	 */
	private Set<EdgeClass> validToEdgeClasses;
	/**
	 * the valid to far IncidenceClasses - only set if schema is finished
	 */
	private Set<IncidenceClass> validToFarIncidenceClasses;
	private Map<String, DirectedSchemaEdgeClass> farRoleNameToEdgeClass;
	static VertexClass createDefaultVertexClass(Schema schema) {
		assert schema.getDefaultGraphClass() != null : "DefaultGraphClass has not yet been created!";
		assert schema.getDefaultVertexClass() == null : "DefaultVertexClass already created!";
		VertexClass vc = schema.getDefaultGraphClass().createVertexClass(
				DEFAULTVERTEXCLASS_NAME);
		vc.setAbstract(true);
		((VertexClassImpl) vc).setInternal(true);
		return vc;
	}
	/**
	 * builds a new vertex class object
	 *
	 * @param qn
	 *            the unique identifier of the vertex class in the schema
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	protected VertexClassImpl(String simpleName, Package pkg,
			GraphClass aGraphClass) {
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
	protected VertexClassImpl(QualifiedName qn, GraphClass aGraphClass) {
=======
	protected VertexClassImpl(String simpleName, PackageImpl pkg,
			GraphClassImpl gc) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		super(simpleName, pkg, aGraphClass);
		this.register();
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
		super(qn, aGraphClass);
		associatedEdges = new HashSet<DirectedEdgeClass>();
=======
		super(simpleName, pkg, gc, gc.vertexClassDag);
		parentPackage.addVertexClass(this);
		graphClass.addVertexClass(this);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
	}
	@Override
	protected void register() {
		((PackageImpl) this.parentPackage).addVertexClass(this);
		((GraphClassImpl) this.graphClass).addVertexClass(this);
	}
	@Override
	public String getVariableName() {
		return "vc_" + this.getQualifiedName().replace('.', '_');
	}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	void addInIncidenceClass(IncidenceClass incClass) {
		if (incClass.getVertexClass() != this) {
			this.throwSchemaException();
		}
		this.checkDuplicateRolenames(incClass);
		this.inIncidenceClasses.add(incClass);
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	void addInIncidenceClass(IncidenceClass incClass) {
		if (incClass.getVertexClass() != this) {
			throwSchemaException(incClass);
		}
		checkDuplicateRolenames(incClass);
		inIncidenceClasses.add(incClass);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	void addOutIncidenceClass(IncidenceClass incClass) {
		if (incClass.getVertexClass() != this) {
			this.throwSchemaException();
		}
		this.checkDuplicateRolenames(incClass);
		this.outIncidenceClasses.add(incClass);
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	void addOutIncidenceClass(IncidenceClass incClass) {
		if (incClass.getVertexClass() != this) {
			throwSchemaException(incClass);
		}
		checkDuplicateRolenames(incClass);
		outIncidenceClasses.add(incClass);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	private void checkDuplicateRolenames(IncidenceClass incClass) {

		String rolename = incClass.getOpposite().getRolename();

		if (rolename.isEmpty()) {
			return;
		}

		this.checkDuplicatedRolenameForACyclicIncidence(incClass);

		this.checkDuplicatedRolenameForAllIncidences(incClass,
				this.getAllInIncidenceClasses());
		this.checkDuplicatedRolenameForAllIncidences(incClass,
				this.getAllOutIncidenceClasses());
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	private void checkDuplicateRolenames(IncidenceClass incClass) {
		String rolename = incClass.getOpposite().getRolename();
		if (rolename.isEmpty()) {
			return;
		}
		checkDuplicatedRolenameForACyclicIncidence(incClass);
		checkDuplicatedRolenameForAllIncidences(incClass,
				getAllInIncidenceClasses());
		checkDuplicatedRolenameForAllIncidences(incClass,
				getAllOutIncidenceClasses());
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	private void checkDuplicatedRolenameForACyclicIncidence(
			IncidenceClass incClass) {

		String rolename = incClass.getOpposite().getRolename();
		VertexClass oppositeVertexClass = incClass.getOpposite()
				.getVertexClass();

		boolean equalRolenames = incClass.getRolename().equals(rolename);
		boolean identicalClasses = this == oppositeVertexClass;

		if (equalRolenames && identicalClasses) {
			this.throwSchemaException(incClass);
		}
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	private void checkDuplicatedRolenameForACyclicIncidence(
			IncidenceClass incClass) {
		String rolename = incClass.getOpposite().getRolename();
		VertexClass oppositeVertexClass = incClass.getOpposite()
				.getVertexClass();
		boolean equalRolenames = incClass.getRolename().equals(rolename);
		boolean identicalClasses = this == oppositeVertexClass;
		if (equalRolenames && identicalClasses) {
			throw new SchemaException(
					"The rolename "
							+ incClass.getRolename()
							+ " may be not used at both ends of the reflexive edge class "
							+ incClass.getEdgeClass().getQualifiedName());
		}
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	private void checkDuplicatedRolenameForAllIncidences(
			IncidenceClass incClass, Set<IncidenceClass> incidenceSet) {

		String rolename = incClass.getOpposite().getRolename();

		if (rolename.isEmpty()) {
			return;
		}

		for (IncidenceClass incidence : incidenceSet) {
			if (incidence == incClass) {
				continue;
			}
			if (incidence.getOpposite().getRolename().equals(rolename)) {
				this.throwSchemaExceptionRolenameUsedTwice(incidence);
			}
		}
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	private void checkDuplicatedRolenameForAllIncidences(
			IncidenceClass incClass, Set<IncidenceClass> incidenceSet) {
		String rolename = incClass.getOpposite().getRolename();
		if (rolename.isEmpty()) {
			return;
		}
		for (IncidenceClass incidence : incidenceSet) {
			if (incidence == incClass) {
				continue;
			}
			if (incidence.getOpposite().getRolename().equals(rolename)) {
				throw new SchemaException("The rolename "
						+ incidence.getOpposite().getRolename()
						+ " is used twice at class " + getQualifiedName());
			}
		}
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
	private void throwSchemaExceptionRolenameUsedTwice(IncidenceClass incidence) {
		throw new SchemaException("The rolename "
				+ incidence.getOpposite().getRolename()
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
				+ " is used twice at class " + this.getQualifiedName());
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
				+ " is used twice at class " + );
=======
				+ " is used twice at class " + getQualifiedName());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
	}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	private void throwSchemaException(IncidenceClass incClass) {
		throw new SchemaException("The rolename " + incClass.getRolename()
				+ " may be not used at both ends of the reflexive edge class "
				+ incClass.getEdgeClass().getQualifiedName());
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	private void throwSchemaException(IncidenceClass ic) {
		throw new SchemaException(
				"Try to add IncidenceClass ending at '"
						+ ic.getVertexClass().getQualifiedName()
						+ "' to VertexClass '"
						+ getQualifiedName()
						+ "'.IncidenceClasses may be added only to VertexClasses they are connected to.");
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
	private void throwSchemaException() {
		throw new SchemaException(
				"IncidenceClasses may be added only to vertices they are connected to");
	}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	@Override
	public void addSuperClass(VertexClass superClass) {
		// Checked in super class
		// if(isFinished()){
		// throw new SchemaException("No changes to finished schema!");
		// }

		if ((superClass == this) || (superClass == null)) {
			return;
		}
		this.checkDuplicateRolenames(superClass);
		super.addSuperClass(superClass);
		if (!superClass.equals(this.getSchema().getDefaultVertexClass())) {
			((GraphClassImpl) this.getSchema().getGraphClass()).getVertexCsDag()
			.createEdge(superClass, this);
		}
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	@Override
	public void addSuperClass(VertexClass superClass) {
		assertNotFinished();
		if (superClass == this) {
			return;
		}
		checkDuplicateRolenames(superClass);
		super.addSuperClass(superClass);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
	private void checkDuplicateRolenames(VertexClass superClass) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		this.checkDuplicatedRolenamesAgainstAllIncidences(superClass
				.getAllInIncidenceClasses());
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
		;
=======
		checkDuplicatedRolenamesAgainstAllIncidences(superClass
				.getAllInIncidenceClasses());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		this.checkDuplicatedRolenamesAgainstAllIncidences(superClass
				.getAllOutIncidenceClasses());
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
		;
=======
		checkDuplicatedRolenamesAgainstAllIncidences(superClass
				.getAllOutIncidenceClasses());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
	}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	private void checkDuplicatedRolenamesAgainstAllIncidences(
			Set<IncidenceClass> incidences) {
		for (IncidenceClass incidence : incidences) {
			this.checkDuplicateRolenames(incidence);
		}
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	private void checkDuplicatedRolenamesAgainstAllIncidences(
			Set<IncidenceClass> incidences) {
		for (IncidenceClass incidence : incidences) {
			checkDuplicateRolenames(incidence);
		}
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
	/**
	 * For a vertexclass A are all edgeclasses valid froms, which (1) run from A
	 * to a B or (2) run from a superclass of A to a B and whose end b at B is
	 * not redefined by A or a superclass of A
	 *
	 */
	@Override
	public
	@Override Set<IncidenceClass> getValidFromFarIncidenceClasses() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		if (this.isFinished()) {
			return this.validFromFarIncidenceClasses;
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		if (isFinished()) {
			return validFromFarIncidenceClasses;
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java

		Set<IncidenceClass> validFromInc = new HashSet<IncidenceClass>();
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		for (IncidenceClass ic : this.getAllOutIncidenceClasses()) {
			IncidenceClass farInc = ic.getEdgeClass().getTo();
			validFromInc.add(farInc);
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		for (IncidenceClass ic : getAllOutIncidenceClasses()) {
			IncidenceClass farInc = ic.getEdgeClass().getTo();
			validFromInc.add(farInc);
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		for (VertexClass aec : this.getAllSuperClasses()) {
			VertexClass vc = aec;
			if (vc.isInternal()) {
				continue;
			}
			for (IncidenceClass ic : vc.getAllOutIncidenceClasses()) {
				IncidenceClass farInc = ic.getEdgeClass().getTo();
				validFromInc.add(farInc);
			}
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		for (VertexClass aec : getAllSuperClasses()) {
			VertexClass vc = aec;
			if (vc.isInternal()) {
				continue;
			}
			for (IncidenceClass ic : vc.getAllOutIncidenceClasses()) {
				IncidenceClass farInc = ic.getEdgeClass().getTo();
				validFromInc.add(farInc);
			}
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
		Set<IncidenceClass> temp = new HashSet<IncidenceClass>(validFromInc);
		for (IncidenceClass ic : temp) {
			validFromInc.removeAll(ic.getRedefinedIncidenceClasses());
		}

		return validFromInc;
	}
	@Override
	public
	@Override Set<IncidenceClass> getValidToFarIncidenceClasses() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		if (this.isFinished()) {
			return this.validToFarIncidenceClasses;
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		if (isFinished()) {
			return validToFarIncidenceClasses;
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
		Set<IncidenceClass> validToInc = new HashSet<IncidenceClass>();
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		for (IncidenceClass ic : this.getAllInIncidenceClasses()) {
			IncidenceClass farInc = ic.getEdgeClass().getFrom();
			validToInc.add(farInc);
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		for (IncidenceClass ic : getAllInIncidenceClasses()) {
			IncidenceClass farInc = ic.getEdgeClass().getFrom();
			validToInc.add(farInc);
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		for (VertexClass aec : this.getAllSuperClasses()) {
			VertexClass vc = aec;
			if (vc.isInternal()) {
				continue;
			}
			for (IncidenceClass ic : vc.getAllInIncidenceClasses()) {
				IncidenceClass farInc = ic.getEdgeClass().getFrom();
				validToInc.add(farInc);
			}
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		for (VertexClass aec : getAllSuperClasses()) {
			VertexClass vc = aec;
			if (vc.isInternal()) {
				continue;
			}
			for (IncidenceClass ic : vc.getAllInIncidenceClasses()) {
				IncidenceClass farInc = ic.getEdgeClass().getFrom();
				validToInc.add(farInc);
			}
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
		Set<IncidenceClass> temp = new HashSet<IncidenceClass>(validToInc);
		for (IncidenceClass ic : temp) {
			validToInc.removeAll(ic.getRedefinedIncidenceClasses());
		}

		return validToInc;
	}
	@Override
	public
	@Override Set<EdgeClass> getValidFromEdgeClasses() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		if (this.isFinished()) {
			return this.validFromEdgeClasses;
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		if (isFinished()) {
			return validFromEdgeClasses;
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
		// System.err.print("+");
		Set<EdgeClass> validFrom = new HashSet<EdgeClass>();
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		for (IncidenceClass ic : this.getValidFromFarIncidenceClasses()) {
			if (!ic.getEdgeClass().isInternal()) {
				validFrom.add(ic.getEdgeClass());
			}
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		for (IncidenceClass ic : getValidFromFarIncidenceClasses()) {
			if (!ic.getEdgeClass().isInternal()) {
				validFrom.add(ic.getEdgeClass());
			}
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
		return validFrom;
	}
	@Override
	public
	@Override Set<EdgeClass> getValidToEdgeClasses() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		if (this.isFinished()) {
			return this.validToEdgeClasses;
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		if (isFinished()) {
			return validToEdgeClasses;
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
		// System.err.print("-");
		Set<EdgeClass> validTo = new HashSet<EdgeClass>();
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		for (IncidenceClass ic : this.getValidToFarIncidenceClasses()) {
			if (!ic.getEdgeClass().isInternal()) {
				validTo.add(ic.getEdgeClass());
			}
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		for (IncidenceClass ic : getValidToFarIncidenceClasses()) {
			if (!ic.getEdgeClass().isInternal()) {
				validTo.add(ic.getEdgeClass());
			}
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
		return validTo;
	}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	public Set<IncidenceClass> getOwnInIncidenceClasses() {
		return this.inIncidenceClasses;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	public Set<IncidenceClass> getOwnInIncidenceClasses() {
		return inIncidenceClasses;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	public Set<IncidenceClass> getOwnOutIncidenceClasses() {
		return this.outIncidenceClasses;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	public Set<IncidenceClass> getOwnOutIncidenceClasses() {
		return outIncidenceClasses;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	@Override
	public Set<IncidenceClass> getAllInIncidenceClasses() {
		if (this.isFinished()) {
			return this.allInIncidenceClasses;
		}
		Set<IncidenceClass> incidenceClasses = new HashSet<IncidenceClass>();
		incidenceClasses.addAll(this.inIncidenceClasses);
		for (VertexClass vc : this.getDirectSuperClasses()) {
			incidenceClasses.addAll(vc.getAllInIncidenceClasses());
		}
		return incidenceClasses;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	@Override
	public Set<IncidenceClass> getAllInIncidenceClasses() {
		if (isFinished()) {
			return allInIncidenceClasses;
		}
		Set<IncidenceClass> incidenceClasses = new HashSet<IncidenceClass>();
		incidenceClasses.addAll(inIncidenceClasses);
		for (VertexClass vc : getDirectSuperClasses()) {
			incidenceClasses.addAll(vc.getAllInIncidenceClasses());
		}
		return incidenceClasses;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	@Override
	public Set<IncidenceClass> getAllOutIncidenceClasses() {

		if (this.isFinished()) {
			return this.allOutIncidenceClasses;
		}
		Set<IncidenceClass> incidenceClasses = new HashSet<IncidenceClass>();
		incidenceClasses.addAll(this.outIncidenceClasses);
		for (VertexClass vc : this.getDirectSuperClasses()) {
			incidenceClasses.addAll(vc.getAllOutIncidenceClasses());
		}
		return incidenceClasses;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	@Override
	public Set<IncidenceClass> getAllOutIncidenceClasses() {
		if (isFinished()) {
			return allOutIncidenceClasses;
		}
		Set<IncidenceClass> incidenceClasses = new HashSet<IncidenceClass>();
		incidenceClasses.addAll(outIncidenceClasses);
		for (VertexClass vc : getDirectSuperClasses()) {
			incidenceClasses.addAll(vc.getAllOutIncidenceClasses());
		}
		return incidenceClasses;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
	@Override
	public
	@Override Set<IncidenceClass> getOwnAndInheritedFarIncidenceClasses() {
		Set<IncidenceClass> result = new HashSet<IncidenceClass>();
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		for (IncidenceClass ic : this.getAllInIncidenceClasses()) {
			result.add(ic.getEdgeClass().getFrom());
			for (IncidenceClass sup : ic.getSubsettedIncidenceClasses()) {
				result.add(sup.getEdgeClass().getFrom());
			}
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		for (IncidenceClass ic : getAllInIncidenceClasses()) {
			result.add(ic.getEdgeClass().getFrom());
			for (IncidenceClass sup : ic.getSubsettedIncidenceClasses()) {
				result.add(sup.getEdgeClass().getFrom());
			}
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
		for (IncidenceClass ic : this.getAllOutIncidenceClasses()) {
			result.add(ic.getEdgeClass().getTo());
			for (IncidenceClass sup : ic.getSubsettedIncidenceClasses()) {
				result.add(sup.getEdgeClass().getTo());
			}
		}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
		for (IncidenceClass ic : getAllOutIncidenceClasses()) {
			result.add(ic.getEdgeClass().getTo());
			for (IncidenceClass sup : ic.getSubsettedIncidenceClasses()) {
				result.add(sup.getEdgeClass().getTo());
			}
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
		return result;
	}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	@Override
	public Set<EdgeClass> getConnectedEdgeClasses() {
		Set<EdgeClass> result = new HashSet<EdgeClass>();
		for (IncidenceClass ic : this.getAllInIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		for (IncidenceClass ic : this.getAllOutIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		return result;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	@Override
	public Set<EdgeClass> getConnectedEdgeClasses() {
		Set<EdgeClass> result = new HashSet<EdgeClass>();
		for (IncidenceClass ic : getAllInIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		for (IncidenceClass ic : getAllOutIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		return result;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	@Override
	public Set<EdgeClass> getOwnConnectedEdgeClasses() {
		Set<EdgeClass> result = new HashSet<EdgeClass>();
		for (IncidenceClass ic : this.getOwnInIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		for (IncidenceClass ic : this.getOwnOutIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		return result;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	@Override
	public Set<EdgeClass> getOwnConnectedEdgeClasses() {
		Set<EdgeClass> result = new HashSet<EdgeClass>();
		for (IncidenceClass ic : getOwnInIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		for (IncidenceClass ic : getOwnOutIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		return result;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	@Override
	protected void finish() {

		this.allInIncidenceClasses = new HashSet<IncidenceClass>();
		this.allInIncidenceClasses.addAll(this.inIncidenceClasses);

		this.allOutIncidenceClasses = new HashSet<IncidenceClass>();
		this.allOutIncidenceClasses.addAll(this.outIncidenceClasses);


		for (VertexClass vc : this.getDirectSuperClasses()) {
			this.allInIncidenceClasses.addAll(vc.getAllInIncidenceClasses());
			this.allOutIncidenceClasses.addAll(vc.getAllOutIncidenceClasses());
		}

		this.allInIncidenceClasses = Collections
				.unmodifiableSet(this.allInIncidenceClasses);
		this.allOutIncidenceClasses = Collections
				.unmodifiableSet(this.allOutIncidenceClasses);

		this.validFromFarIncidenceClasses = Collections
				.unmodifiableSet(this.getValidFromFarIncidenceClasses());
		this.validToFarIncidenceClasses = Collections
				.unmodifiableSet(this.getValidToFarIncidenceClasses());

		this.validFromEdgeClasses = Collections
				.unmodifiableSet(this.getValidFromEdgeClasses());
		this.validToEdgeClasses = Collections
				.unmodifiableSet(this.getValidToEdgeClasses());

		this.farRoleNameToEdgeClass = new HashMap<String, DirectedSchemaEdgeClass>();
		for (IncidenceClass ic : this.getOwnAndInheritedFarIncidenceClasses()) {
			this.farRoleNameToEdgeClass.put(ic.getRolename(),
					this.getDirectedEdgeClassForFarEndRole(ic.getRolename()));
		}
		this.farRoleNameToEdgeClass = Collections
				.unmodifiableMap(this.farRoleNameToEdgeClass);

		this.inIncidenceClasses = Collections.unmodifiableSet(this.inIncidenceClasses);
		this.outIncidenceClasses = Collections.unmodifiableSet(this.outIncidenceClasses);

		for (IncidenceClass ic : this.inIncidenceClasses) {
			((IncidenceClassImpl) ic).finish();
		}
		for (IncidenceClass ic : this.outIncidenceClasses) {
			((IncidenceClassImpl) ic).finish();
		}

		super.finish();
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	@Override
	protected void finish() {
		allInIncidenceClasses = new HashSet<IncidenceClass>();
		allInIncidenceClasses.addAll(inIncidenceClasses);

		allOutIncidenceClasses = new HashSet<IncidenceClass>();
		allOutIncidenceClasses.addAll(outIncidenceClasses);

		for (VertexClass vc : getDirectSuperClasses()) {
			allInIncidenceClasses.addAll(vc.getAllInIncidenceClasses());
			allOutIncidenceClasses.addAll(vc.getAllOutIncidenceClasses());
		}

		allInIncidenceClasses = Collections
				.unmodifiableSet(allInIncidenceClasses);
		allOutIncidenceClasses = Collections
				.unmodifiableSet(allOutIncidenceClasses);

		validFromFarIncidenceClasses = Collections
				.unmodifiableSet(getValidFromFarIncidenceClasses());
		validToFarIncidenceClasses = Collections
				.unmodifiableSet(getValidToFarIncidenceClasses());

		validFromEdgeClasses = Collections
				.unmodifiableSet(getValidFromEdgeClasses());
		validToEdgeClasses = Collections
				.unmodifiableSet(getValidToEdgeClasses());

		farRoleNameToEdgeClass = new HashMap<String, DirectedSchemaEdgeClass>();
		for (IncidenceClass ic : getOwnAndInheritedFarIncidenceClasses()) {
			String role = ic.getRolename();
			if (role == null || role.length() == 0) {
				continue;
			}
			farRoleNameToEdgeClass.put(role,
					getDirectedEdgeClassForFarEndRole(role));
		}
		farRoleNameToEdgeClass = Collections
				.unmodifiableMap(farRoleNameToEdgeClass);

		inIncidenceClasses = Collections.unmodifiableSet(inIncidenceClasses);
		outIncidenceClasses = Collections.unmodifiableSet(outIncidenceClasses);

		for (IncidenceClass ic : inIncidenceClasses) {
			((IncidenceClassImpl) ic).finish();
		}
		for (IncidenceClass ic : outIncidenceClasses) {
			((IncidenceClassImpl) ic).finish();
		}
		super.finish();
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	@Override
	public boolean isValidFromFor(EdgeClass ec) {
		return this.getValidFromEdgeClasses().contains(ec);
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	@Override
	public boolean isValidFromFor(EdgeClass ec) {
		return getValidFromEdgeClasses().contains(ec);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	@Override
	public boolean isValidToFor(EdgeClass ec) {
		return this.getValidToEdgeClasses().contains(ec);
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	@Override
	public boolean isValidToFor(EdgeClass ec) {
		return getValidToEdgeClasses().contains(ec);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
	@Override
	protected void reopen() {
		this.allInIncidenceClasses = null;
		this.allOutIncidenceClasses = null;
		this.validFromFarIncidenceClasses = null;
		this.validToFarIncidenceClasses = null;
		this.validFromEdgeClasses = null;
		this.validToEdgeClasses = null;
		this.inIncidenceClasses = new HashSet<IncidenceClass>(this.inIncidenceClasses);
		this.outIncidenceClasses = new HashSet<IncidenceClass>(this.outIncidenceClasses);
		this.farRoleNameToEdgeClass = null;

		for (IncidenceClass ic : this.inIncidenceClasses) {
			((IncidenceClassImpl) ic).reopen();
		}
		for (IncidenceClass ic : this.outIncidenceClasses) {
			((IncidenceClassImpl) ic).reopen();
		}

		super.reopen();
	}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
	@Override
	public DirectedSchemaEdgeClass getDirectedEdgeClassForFarEndRole(
			String roleName) {
		if (this.isFinished()) {
			return this.farRoleNameToEdgeClass.get(roleName);
		}
		for (IncidenceClass ic : this.getOwnAndInheritedFarIncidenceClasses()) {
			if (roleName.equals(ic.getRolename())) {
				EdgeClass ec = ic.getEdgeClass();
				return new DirectedSchemaEdgeClass(
						ec,
						(this.getValidFromEdgeClasses().contains(ec) ? EdgeDirection.OUT
								: EdgeDirection.IN));
			}
		}
		return null;
	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/base.java
=======
	@Override
	public DirectedSchemaEdgeClass getDirectedEdgeClassForFarEndRole(
			String roleName) {
		if (isFinished()) {
			return farRoleNameToEdgeClass.get(roleName);
		}
		for (IncidenceClass ic : getOwnAndInheritedFarIncidenceClasses()) {
			String role = ic.getRolename();
			if (roleName.equals(ic.getRolename())) {
				EdgeClass ec = ic.getEdgeClass();
				return new DirectedSchemaEdgeClass(
						ec,
						(ic.getDirection() == IncidenceDirection.IN ? EdgeDirection.OUT
								: EdgeDirection.IN));
			}
		}
		return null;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
	/**
	 * the own in IncidenceClasses
	 */
	/**
	 * the in IncidenceClasses - only set if schema is finish
	 */
	/**
	 * the own out IncidenceClasses
	 */
	/**
	 * the out IncidenceClasses - only set if schema is finish
	 */
	/**
	 * the valid from far IncidenceClasses - only set if schema is finished
	 */
	/**
	 * the valid from EdgeClasses - only set if schema is finished
	 */
	/**
	 * the valid to EdgeClasses - only set if schema is finished
	 */
	/**
	 * the valid to far IncidenceClasses - only set if schema is finished
	 */
	/**
	 * builds a new vertex class object
	 */
	/**
	 * For a vertexclass A are all edgeclasses valid froms, which (1) run from A
	 * to a B or (2) run from a superclass of A to a B and whose end b at B is
	 * not redefined by A or a superclass of A
	 * 
	 */
}
