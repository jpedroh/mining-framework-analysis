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

import java.util.Set;

import de.uni_koblenz.jgralab.Vertex;

/**
 * Represents a VertexClass in the Schema.
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

import de.uni_koblenz.jgralab.schema.impl.DirectedSchemaEdgeClass;

/**
 * Represents a VertexClass in the Schema.
 *
 * @author ist@uni-koblenz.de
 */
public interface VertexClass extends GraphElementClass<VertexClass, Vertex> {

	public final static String DEFAULTVERTEXCLASS_NAME = "Vertex";

	/**
	 * adds a superclass to the list of superclasses, all attributes get
	 * inherited from those classes
	 *
	 * @param superClass
	 *            the vertex class to be added to the list of superclasses
	 *
	 */
	public void addSuperClass(VertexClass superClass);

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/left.java
	// public Set<IncidenceClass> getOwnInIncidenceClasses();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/base.java
	/**
	 * adds an edge class to the list of edge classes to which the vertex class
	 * may be connected to, only used internally!
	 * 
	 * @param anEdgeClass
	 */
	public void addEdgeClass(EdgeClass anEdgeClass);
=======
	public Set<IncidenceClass> getAllInIncidenceClasses();

	public Set<IncidenceClass> getAllOutIncidenceClasses();

	public Set<IncidenceClass> getValidFromFarIncidenceClasses();

	public Set<IncidenceClass> getValidToFarIncidenceClasses();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/left.java
	// public Set<IncidenceClass> getOwnOutIncidenceClasses();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/base.java
	/**
	 * @return the edge classes to which the vertex class may be connected to
	 */
	public Set<EdgeClass> getOwnEdgeClasses();
=======
	/**
	 * @return The set of {@link IncidenceClass}es that can be accessed by role
	 *         name from instances of this vertex class
	 */
	public Set<IncidenceClass> getOwnAndInheritedFarIncidenceClasses();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/left.java
	public Set<IncidenceClass> getAllInIncidenceClasses();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/base.java
	/**
	 * @return all EdgeClasses (including subclasses) which may connect to this
	 *         vertexclass
	 */
	public Set<EdgeClass> getEdgeClasses();
=======
	/**
	 * @param roleName
	 * @return the {@link EdgeClass} corresponding to the far-end
	 *         <code>roleName</code> including its direction from the view of
	 *         this vertex class
	 */
	public DirectedSchemaEdgeClass getDirectedEdgeClassForFarEndRole(
			String roleName);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/left.java
	public Set<IncidenceClass> getAllOutIncidenceClasses();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/base.java
	/**
	 * @return the set of EdgeClasses that may start at this VertexClass
	 */
	public Set<EdgeClass> getValidFromEdgeClasses();
=======
	/**
	 * @param ec
	 * @return true, iff edges of class <code>ec</code> may start at vertices of
	 *         this vertex class
	 */
	public boolean isValidFromFor(EdgeClass ec);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/left.java
	public Set<IncidenceClass> getValidFromFarIncidenceClasses();

	public Set<IncidenceClass> getValidToFarIncidenceClasses();

	// public Set<IncidenceClass> getOwnAndInheritedFarIncidenceClasses();

	public void addInIncidenceClass(IncidenceClass ic);

	public void addOutIncidenceClass(IncidenceClass ic);

	@Override
	public Class<? extends Vertex> getSchemaClass();

||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/base.java
	/**
	 * @return the set of EdgeClasses that may end at this VertexClass
	 */
=======
	/**
	 * @param ec
	 * @return true, iff edges of class <code>ec</code> may end at vertices of
	 *         this vertex class
	 */
	public boolean isValidToFor(EdgeClass ec);

>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/VertexClass.java/right.java
	public Set<EdgeClass> getValidToEdgeClasses();

	public Set<EdgeClass> getValidFromEdgeClasses();

	public Set<EdgeClass> getConnectedEdgeClasses();

	public Set<EdgeClass> getOwnConnectedEdgeClasses();

}
