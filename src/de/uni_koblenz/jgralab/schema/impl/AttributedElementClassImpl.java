/*
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
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
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
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
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
 */

package de.uni_koblenz.jgralab.schema.impl;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.Constraint;
import de.uni_koblenz.jgralab.schema.exception.DuplicateAttributeException;
import de.uni_koblenz.jgralab.schema.exception.InheritanceException;
import de.uni_koblenz.jgralab.schema.exception.SchemaClassAccessException;
import de.uni_koblenz.jgralab.schema.impl.compilation.SchemaClassManager;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
public abstract class AttributedElementClassImpl extends NamedElementImpl implements AttributedElementClass<SC, IC> {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
public abstract class AttributedElementClassImpl  implements AttributedElementClass<SC, IC> {
=======
public abstract class AttributedElementClassImpl <SC extends AttributedElementClass<SC, IC>, IC extends AttributedElement<SC, IC>>
		extends NamedElementImpl implements AttributedElementClass<SC, IC> {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java

	/**
	 * the list of attributes. Only the own attributes of this class are stored
	 * here, no inherited attributes
	 */
	private final TreeSet<Attribute> attributeList = new TreeSet<Attribute>();
	/**
	 * A set of {@link Constraint}s which can be used to validate the graph.
	 */
	protected HashSet<Constraint> constraints = new HashSet<Constraint>(1);
	/**
	 * the immediate sub classes of this class
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	protected HashSet<AttributedElementClass> directSubClasses = new HashSet<AttributedElementClass>();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
=======
	protected Set<SC> directSubClasses = new HashSet<SC>();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	/**
	 * the immediate super classes of this class
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	protected HashSet<AttributedElementClass> directSuperClasses = new HashSet<AttributedElementClass>();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
=======
	protected Set<SC> directSuperClasses = new HashSet<SC>();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	/**
	 * true if element class is abstract
	 */
	private boolean isAbstract = false;
	/**
	 * The class object representing the generated interface for this
	 * AttributedElementClass
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	private Class<? extends AttributedElement> schemaClass;
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
=======
	private Class<IC> schemaClass;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	/**
	 * The class object representing the implementation class for this
	 * AttributedElementClass. This may be either the generated class or a
	 * subclass of this
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	private Class<? extends AttributedElement> schemaImplementationClass;
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
=======
	private Class<IC> schemaImplementationClass;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	protected AttributedElementClassImpl(String simpleName, Package pkg,
			Schema schema) {
		super(simpleName, pkg, schema);
	}
	@Override
	public
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	@Override void addAttribute(Attribute anAttribute) {
		if (containsAttribute(anAttribute.getName())) {
			throw new DuplicateAttributeException(anAttribute.getName(),
					getQualifiedName());
		}
		// Check if a subclass already contains an attribute with that name. In
		// that case, it may not be added, too.
		if (subclassContainsAttribute(anAttribute.getName())) {
			throw new DuplicateAttributeException(
					"Duplicate Attribute '"
							+ anAttribute.getName()
							+ "' in AttributedElementClass '"
							+ getQualifiedName()
							+ "'. "
							+ "A derived AttributedElementClass already contains this Attribute.");
		}
		attributeList.add(anAttribute);
	}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	@Override void addAttribute(Attribute anAttribute) 
=======
	@Override void addAttribute(Attribute anAttribute) {
		if (finished) {
			throw new SchemaException("No changes to finished schema!");
		}

		if (containsAttribute(anAttribute.getName())) {
			throw new DuplicateAttributeException(anAttribute.getName(),
					getQualifiedName());
		}
		// Check if a subclass already contains an attribute with that name. In
		// that case, it may not be added, too.
		if (subclassContainsAttribute(anAttribute.getName())) {
			throw new DuplicateAttributeException(
					"Duplicate Attribute '"
							+ anAttribute.getName()
							+ "' in AttributedElementClass '"
							+ getQualifiedName()
							+ "'. "
							+ "A derived AttributedElementClass already contains this Attribute.");
		}
		attributeList.add(anAttribute);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	@Override
	public void addAttribute(String name, Domain domain,
			String defaultValueAsString) {
		addAttribute(new AttributeImpl(name, domain, this, defaultValueAsString));
	}
	@Override
	public void addAttribute(String name, Domain domain) {
		addAttribute(new AttributeImpl(name, domain, this, null));
	}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	@Override
	public void addConstraint(Constraint constraint) {
		constraints.add(constraint);
	}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
=======
	@Override
	public void addConstraint(Constraint constraint) {
		if (finished) {
			throw new SchemaException("No changes to finished schema!");
		}
		constraints.add(constraint);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	@SuppressWarnings("unchecked")
	protected void addSuperClass(SC superClass) {
		if (finished) {
			throw new SchemaException("No changes to finished schema!");
		}

		if ((superClass == this) || (superClass == null)) {
			return;
		}
		directSuperClasses.remove(getSchema().getDefaultGraphClass());
		directSuperClasses.remove(getSchema().getDefaultEdgeClass());
		directSuperClasses.remove(getSchema().getDefaultVertexClass());

		for (Attribute a : superClass.getAttributeList()) {
			if (getOwnAttribute(a.getName()) != null) {
				throw new InheritanceException("Cannot add "
						+ superClass.getQualifiedName() + " as superclass of "
						+ getQualifiedName() + ", cause: Attribute "
						+ a.getName() + " is declared in both classes");
			}
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
		if (superClass.isSubClassOf((SC) this)) {
			// for (AttributedElementClass attr :
			// superClass.getAllSuperClasses()) {
			// System.out.println(attr.getQualifiedName());
			// }
			// System.out.println();
			throw new InheritanceException(
					"Cycle in class hierarchie for classes: "
							+ getQualifiedName() + " and "
							+ superClass.getQualifiedName());
		}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
		if (superClass.isSubClassOf((SC) this)) 
=======
		if (superClass.isSubClassOf((SC) this)) {
			throw new InheritanceException(
					"Cycle in class hierarchie for classes: "
							+ getQualifiedName() + " and "
							+ superClass.getQualifiedName());
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
		directSuperClasses.add(superClass);
		((AttributedElementClassImpl<SC, IC>) superClass).directSubClasses
				.add((SC) this);
	}
	/**
	 * @return a textual representation of all attributes the element holds
	 */
	protected String attributesToString() {
		StringBuilder output = new StringBuilder("\nSelf Attributes:\n");
		Iterator<Attribute> it = attributeList.iterator();
		Attribute a;
		while (it.hasNext()) {
			a = it.next();
			output.append(a.toString() + "\n");
		}
		output.append("\nSelf + Inherited Attributes:\n");
		it = getAttributeList().iterator();
		while (it.hasNext()) {
			a = it.next();
			output.append(a.toString() + "\n");
		}
		return output.toString();
	}
	@Override
	public boolean containsAttribute(String name) {
		return (getAttribute(name) != null);
	}
	@Override
	public Set<SC> getAllSubClasses() {
		if (finished) {
			return allSubClasses;
		}

		Set<SC> returnSet = new HashSet<SC>();
		for (SC subclass : directSubClasses) {
			returnSet.add(subclass);
			returnSet.addAll(subclass.getAllSubClasses());
		}
		return returnSet;
	}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	@Override
	public Set<AttributedElementClass> getAllSuperClasses() {
		HashSet<AttributedElementClass> allSuperClasses = new HashSet<AttributedElementClass>();
		allSuperClasses.addAll(directSuperClasses);
		for (AttributedElementClass superClass : directSuperClasses) {
			allSuperClasses.addAll(superClass.getAllSuperClasses());
		}
		return allSuperClasses;
	}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	@Override
	public Set<AttributedElementClass> getAllSuperClasses() {
		HashSet<AttributedElementClass> allSuperClasses = new HashSet<AttributedElementClass>();
		allSuperClasses.addAll(directSuperClasses);
		for (AttributedElementClass superClass : directSuperClasses) {
			// System.out.println("Getting superclasses for class: " +
			// superClass.getName());
			allSuperClasses.addAll(superClass.getAllSuperClasses());
		}
		return allSuperClasses;
	}
=======
	@Override
	public Set<SC> getAllSuperClasses() {
		if (finished) {
			return allSuperClasses;
		}

		HashSet<SC> allSuperClasses = new HashSet<SC>();
		allSuperClasses.addAll(directSuperClasses);
		for (SC superClass : directSuperClasses) {
			allSuperClasses.addAll(superClass.getAllSuperClasses());
		}
		return allSuperClasses;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	@Override
	public Attribute getAttribute(String name) {
		// TODO ask if Attributes save as map
		if (finished) {
			Iterator<Attribute> it = allAttributeList.iterator();
			Attribute a;
			while (it.hasNext()) {
				a = it.next();
				if (a.getName().equals(name)) {
					return a;
				}
			}
		}

		Attribute ownAttr = getOwnAttribute(name);
		if (ownAttr != null) {
			return ownAttr;
		}
		for (SC superClass : directSuperClasses) {
			Attribute inheritedAttr = superClass.getAttribute(name);
			if (inheritedAttr != null) {
				return inheritedAttr;
			}
		}
		return null;
	}
	@Override
	public int getAttributeCount() {
		if (finished) {
			return allAttributeList.size();
		}
		int attrCount = getOwnAttributeCount();
		for (SC superClass : directSuperClasses) {
			attrCount += superClass.getAttributeCount();
		}
		return attrCount;
	}
	@Override
	public SortedSet<Attribute> getAttributeList() {
		if (finished) {
			return allAttributeList;
		}

		TreeSet<Attribute> attrList = new TreeSet<Attribute>();
		attrList.addAll(attributeList);
		for (SC superClass : directSuperClasses) {
			attrList.addAll(superClass.getAttributeList());
		}
		return attrList;
	}
	@Override
	public Set<Constraint> getConstraints() {
		return constraints;
	}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	@Override
	public Set<AttributedElementClass> getDirectSubClasses() {
		return directSubClasses;
	}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	@Override
	public Set<AttributedElementClass> getDirectSubClasses() {
		return directSubClasses;
	}
=======
	@Override
	public Set<SC> getDirectSubClasses() {
		return directSubClasses;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	@SuppressWarnings("unchecked")
	@Override
	public
	@SuppressWarnings("unchecked")
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	@Override Class<? extends AttributedElement> getSchemaClass() {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	@Override  getSchemaClass() {
=======
	@Override Class<IC> getSchemaClass() {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
		if (schemaClass == null) {
			String schemaClassName = getSchema().getPackagePrefix() + "."
					+ getQualifiedName();
			try {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
				schemaClass = (Class<? extends AttributedElement>) Class
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
				schemaClass = () Class
=======
				schemaClass = (Class<IC>) Class
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
						.forName(schemaClassName, true, SchemaClassManager
								.instance(getSchema().getQualifiedName()));
			} catch (ClassNotFoundException e) {
				throw new SchemaClassAccessException(
						"Can't load (generated) schema class for AttributedElementClass '"
								+ getQualifiedName() + "'", e);
			}
		}
		return schemaClass;
	}
	@SuppressWarnings("unchecked")
	@Override @Override
	public Class<IC> getSchemaImplementationClass() {
		if (isAbstract()) {
			throw new SchemaClassAccessException(
					"Can't get (generated) schema implementation class. AttributedElementClass '"
							+ getQualifiedName() + "' is abstract!");
		}
		if (schemaImplementationClass == null) {
			try {
				Field f = getSchemaClass().getField("IMPLEMENTATION_CLASS");
				schemaImplementationClass = (Class<IC>) f.get(schemaClass);
			} catch (SecurityException e) {
				throw new SchemaClassAccessException(e);
			} catch (NoSuchFieldException e) {
				throw new SchemaClassAccessException(e);
			} catch (IllegalArgumentException e) {
				throw new SchemaClassAccessException(e);
			} catch (IllegalAccessException e) {
				throw new SchemaClassAccessException(e);
			}
		}
		return schemaImplementationClass;
	}
	@Override
	public Attribute getOwnAttribute(String name) {
		Iterator<Attribute> it = attributeList.iterator();
		Attribute a;
		while (it.hasNext()) {
			a = it.next();
			if (a.getName().equals(name)) {
				return a;
			}
		}
		return null;
	}
	@Override
	public int getOwnAttributeCount() {
		return attributeList.size();
	}
	@Override
	public SortedSet<Attribute> getOwnAttributeList() {
		return attributeList;
	}
	@Override
	public boolean hasAttributes() {
		return !getAttributeList().isEmpty();
	}
	@Override
	public boolean hasOwnAttributes() {
		return !attributeList.isEmpty();
	}
	@Override
	public boolean isAbstract() {
		return isAbstract;
	}
	@Override
	public boolean isDirectSubClassOf(SC anAttributedElementClass) {
		return directSuperClasses.contains(anAttributedElementClass);
	}
	@Override
	public boolean isInternal() {
		Schema s = getSchema();
		return ((this == s.getDefaultEdgeClass())
				|| (this == s.getDefaultGraphClass()) || (this == s
				.getDefaultVertexClass()));
	}
	@Override
	public boolean isSubClassOf(SC anAttributedElementClass) {
		return getAllSuperClasses().contains(anAttributedElementClass);
	}
	@Override
	public boolean isSuperClassOf(SC anAttributedElementClass) {
		return anAttributedElementClass.getAllSuperClasses().contains(this);
	}
	@Override
	public boolean isSuperClassOfOrEquals(SC anAttributedElementClass) {
		return ((this == anAttributedElementClass) || (isSuperClassOf(anAttributedElementClass)));
	}
	@Override
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	protected boolean subclassContainsAttribute(String name) {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
		for (AttributedElementClass subClass : getAllSubClasses()) {
			Attribute subclassAttr = subClass.getAttribute(name);
			if (subclassAttr != null) {
				return true;
			}
		}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
=======
		for (SC subClass : getAllSubClasses()) {
			Attribute subclassAttr = subClass.getAttribute(name);
			if (subclassAttr != null) {
				return true;
			}
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
		return false;
	}
	/**
	 * the list of attributes. Only the own attributes of this class are stored
	 * here, no inherited attributes
	 */
	/**
	 * the list of all attributes. Own attributes and inherited attributes are
	 * stored here - but only if the schema is finish
	 */
	private SortedSet<Attribute> allAttributeList;
	/**
	 * A set of {@link Constraint}s which can be used to validate the graph.
	 */
	/**
	 * the sub classes of this class - only set if the schema is finish
	 */
	protected Set<SC> allSubClasses;
	/**
	 * maps each attribute to an index
	 */
	protected HashMap<String, Integer> attributeIndex;
	/**
	 * the super classes of this class - only set if the schema is finish
	 */
	protected Set<SC> allSuperClasses;
	/**
	 * true if the schema is finish
	 */
	private boolean finished = false;
	/**
	 * true if element class is abstract
	 */
	/**
	 * builds a new attributed element class
	 *
	 * @param qn
	 *            the unique identifier of the element in the schema
	 */
	/**
	 * adds a superClass to this class
	 *
	 * @param superClass
	 *            the class to add as superclass
	 */
	@Override
	public Set<SC> getDirectSuperClasses() {
		return directSuperClasses;
	}
	@Override
	public boolean isDirectSuperClassOf(SC anAttributedElementClass) {
		return ((AttributedElementClassImpl<SC, IC>) anAttributedElementClass).directSuperClasses
				.contains(this);
	}
	void setInternal(Boolean b) {
		internal = b;
	}
	/**
	 * Called if the schema is finished, saves complete subclass, superclass and
	 * attribute list
	 */
	protected void finish() {
		allSuperClasses = new HashSet<SC>();
		allSuperClasses.addAll(directSuperClasses);
		for (SC superClass : directSuperClasses) {
			allSuperClasses.addAll(superClass.getAllSuperClasses());
		}

		allSubClasses = new HashSet<SC>();
		allSubClasses.addAll(directSubClasses);
		for (SC subClass : directSubClasses) {
			allSubClasses.addAll(subClass.getAllSubClasses());
		}

		allAttributeList = new TreeSet<Attribute>();
		allAttributeList.addAll(attributeList);
		for (SC superClass : directSuperClasses) {
			allAttributeList.addAll(superClass.getAttributeList());
		}

		directSubClasses = Collections.unmodifiableSet(directSubClasses);
		directSuperClasses = Collections.unmodifiableSet(directSuperClasses);
		allSuperClasses = Collections.unmodifiableSet(allSuperClasses);
		allSubClasses = Collections.unmodifiableSet(allSubClasses);
		allAttributeList = Collections.unmodifiableSortedSet(allAttributeList);

		attributeIndex = new HashMap<String, Integer>();
		int i = 0;
		for (Attribute a : allAttributeList) {
			attributeIndex.put(a.getName(), i);
			++i;
		}

		finished = true;
	}
	/**
	 * Called if the schema is reopen
	 */
	protected void reopen() {
		directSubClasses = new HashSet<SC>(directSubClasses);
		directSuperClasses = new HashSet<SC>(directSuperClasses);
		allSuperClasses = null;
		allSubClasses = null;
		allAttributeList = null;

		finished = false;
	}
	protected boolean isFinished() {
		return finished;
	}
	@Override
	public int getAttributeIndex(String name) {
		Integer i = null;
		if(finished) {
			i = attributeIndex.get(name);
		}
		else {
			int j = 0;
			for (Attribute a : getAttributeList()) {
				if(a.getName().equals(name)) {
					i = Integer.valueOf(j);
					break;
				}
				++j;
			}
		}

		if (i != null) {
			return i.intValue();
		} else {
			throw new NoSuchAttributeException(this.getSimpleName()
					+ " doesn't contain an attribute " + name);
		}

	}
}
