/*
 * JGraLab - The Java Graph Laboratory
 *
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
 * Copyright (C) 2006-2011 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
 *               ist@uni-koblenz.de
=======
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
 *
 * For bug reports, documentation and further information, visit
 *
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
 *                         http://jgralab.uni-koblenz.de
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
=======
 *                         https://github.com/jgralab/jgralab
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
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
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.Constraint;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.exception.DuplicateAttributeException;
import de.uni_koblenz.jgralab.schema.exception.InheritanceException;
import de.uni_koblenz.jgralab.schema.exception.SchemaClassAccessException;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;
import de.uni_koblenz.jgralab.schema.impl.compilation.SchemaClassManager;
import java.util.List;
import org.pcollections.ArrayPSet;
import org.pcollections.ArrayPVector;
import org.pcollections.PSet;
import org.pcollections.PVector;

public abstract class AttributedElementClassImpl<SC extends AttributedElementClass<SC, IC>, IC extends AttributedElement<SC, IC>>
		extends NamedElementImpl implements AttributedElementClass<SC, IC> {

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	 * the list of attributes. Only the own attributes of this class are stored
	 * here, no inherited attributes
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	 * toggles if this class is only for internal use
=======
	 * the list of all attributes. Own attributes and inherited attributes are
	 * stored here - but only if the schema is finish
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	private final TreeSet<Attribute> attributeList = new TreeSet<Attribute>();
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	private boolean internal = false;
=======
	protected PVector<Attribute> allAttributes;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	 * the list of all attributes. Own attributes and inherited attributes are
	 * stored here - but only if the schema is finish
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	 * the package this attributed element class belongs to
=======
	 * A set of {@link Constraint}s which can be used to validate the graph.
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	private SortedSet<Attribute> allAttributeList;
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	private Package pkg;
=======
	protected PSet<Constraint> constraints;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	 * A set of {@link Constraint}s which can be used to validate the graph.
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	 * the immediate super classes of this class
=======
	 * maps each attribute to an index
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	protected HashSet<Constraint> constraints = new HashSet<Constraint>(1);
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	protected HashSet<AttributedElementClass> directSuperClasses;
=======
	protected HashMap<String, Integer> attributeIndex;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java

	/**
	 * true if the schema is finish
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	protected Set<SC> directSubClasses = new HashSet<SC>();
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	protected HashSet<AttributedElementClass> directSubClasses;
=======
	protected boolean finished;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	 * the sub classes of this class - only set if the schema is finish
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	 * a list of attributes which belongs to the m2 element
	 * (edgeclass/vertexclass/graphclass). Only the own attributes of this class
	 * are stored here, no inherited attributes
=======
	 * true if element class is abstract
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	protected Set<SC> allSubClasses;
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	private TreeSet<Attribute> attributeList;
=======
	private boolean isAbstract;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	/**
	 * the immediate super classes of this class
	 */
	protected Set<SC> directSuperClasses = new HashSet<SC>();

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
	private boolean isAbstract = false;

	private boolean internal = false;
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	/**
	 * a unique identifier of the m2 element in the schema
	 * (edgeclass/vertexclass/graphclass)
	 */
	private QualifiedName qName;

	/**
	 * defines the m2 element as abstract, i.e. that it may not have any
	 * instances
	 */
	private boolean isAbstract = false;
=======
	private boolean internal;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java

	/**
	 * The class object representing the generated interface for this
	 * AttributedElementClass
	 */
	private Class<IC> schemaClass;

	/**
	 * The class object representing the implementation class for this
	 * AttributedElementClass. This may be either the generated class or a
	 * subclass of this
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	private Class<IC> schemaImplementationClass;

	/**
	 * builds a new attributed element class
	 *
	 * @param qn
	 *            the unique identifier of the element in the schema
	 */
	protected AttributedElementClassImpl(String simpleName, Package pkg,
			Schema schema) {
		super(simpleName, pkg, schema);
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	private Class<? extends AttributedElement> m1ImplementationClass;

	@Override
	public void setPackage(Package p) {
		pkg = p;
	}

	@Override
	public Package getPackage() {
		return pkg;
	}

	/**
	 * builds a new attributed element class
	 * 
	 * @param qn
	 *            the unique identifier of the element in the schema
	 */
	public AttributedElementClassImpl(QualifiedName qn) {
		qName = qn;
		m1Class = null;
		m1ImplementationClass = null;
		attributeList = new TreeSet<Attribute>();
		directSubClasses = new HashSet<AttributedElementClass>();
		directSuperClasses = new HashSet<AttributedElementClass>();
	}

	@Override
	public String getName() {
		return getQualifiedName();
	}

	@Override
	public String getSimpleName() {
		return qName.getSimpleName();
	}

	@Override
	public String getQualifiedName() {
		return qName.getQualifiedName();
	}

	@Override
	public String getQualifiedName(Package pkg) {
		if (this.pkg == pkg) {
			return qName.getSimpleName();
		} else if (this.pkg.isDefaultPackage()) {
			return "." + qName.getSimpleName();
		} else {
			return qName.getQualifiedName();
		}
	}

	@Override
	public String getUniqueName() {
		return qName.getUniqueName();
	}

	@Override
	public void setUniqueName(String uniqueName) {
		qName.setUniqueName(this, uniqueName);
	}

	@Override
	public String getPackageName() {
		return qName.getPackageName();
	}

	@Override
	public String getDirectoryName() {
		return qName.getDirectoryName();
	}

	@Override
	public String getPathName() {
		return qName.getPathName();
	}

	@Override
	public QualifiedName getQName() {
		return qName;
	}

	@Override
	public void addAttribute(String name, Domain domain) {
		addAttribute(new AttributeImpl(name, domain));
=======
	private Class<IC> schemaImplementationClass;

	protected AttributedElementClassImpl(String simpleName, PackageImpl pkg,
			SchemaImpl schema) {
		super(simpleName, pkg, schema);
		allAttributes = ArrayPVector.empty();
		constraints = ArrayPSet.empty();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	}

	@Override
	public void addAttribute(Attribute anAttribute) {
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
		if (finished) {
			throw new SchemaException("No changes to finished schema!");
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
		if (containsAttribute(anAttribute.getName())) {
			throw new SchemaException("duplicate attribute name '"
					+ anAttribute.getName() + "' in class '" + getName() + "'");
=======
		assertNotFinished();

		if (containsAttribute(anAttribute.getName())) {
			throw new SchemaException("Duplicate attribute '"
					+ anAttribute.getName() + "' in AttributedElementClass '"
					+ getQualifiedName() + "'");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java

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
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
		if (Schema.reservedTGWords.contains(anAttribute.getName())
				|| Schema.reservedJavaWords.contains(anAttribute.getName())) {
			throw new SchemaException("The name " + anAttribute.getName()
					+ " may not be used as "
					+ "attribute name because it is a reserved word.");
		}
		attributeList.add(anAttribute);
=======
		TreeSet<Attribute> s = new TreeSet<Attribute>(allAttributes);
		s.add(anAttribute);
		allAttributes = ArrayPVector.<Attribute> empty().plusAll(s);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	}

	@Override
	public void addAttribute(String name, Domain domain,
			String defaultValueAsString) {
		addAttribute(new AttributeImpl(name, domain, this, defaultValueAsString));
	}

	@Override
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	public void addAttribute(String name, Domain domain) {
		addAttribute(new AttributeImpl(name, domain, this, null));
	}

	@Override
	public void addConstraint(Constraint constraint) {
		if (finished) {
			throw new SchemaException("No changes to finished schema!");
		}
		constraints.add(constraint);
	}

	/**
	 * adds a superClass to this class
	 *
	 * @param superClass
	 *            the class to add as superclass
	 */
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
		if (superClass.isSubClassOf((SC) this)) {
			throw new InheritanceException(
					"Cycle in class hierarchie for classes: "
							+ getQualifiedName() + " and "
							+ superClass.getQualifiedName());
		}
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
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	public Attribute getOwnAttribute(String name) {
		Iterator<Attribute> it = attributeList.iterator();
		Attribute a;
		while (it.hasNext()) {
			a = (Attribute) it.next();
			if (a.getName().equals(name))
				return a;
		}
		return null;
	}

	@Override
	public Attribute getAttribute(String name) {
		Attribute ownAttr = getOwnAttribute(name);
		if (ownAttr != null)
			return ownAttr;
		for (AttributedElementClass superClass : directSuperClasses) {
			Attribute inheritedAttr = superClass.getAttribute(name);
			if (inheritedAttr != null)
				return inheritedAttr;
		}
		return null;
	}

	@Override
	public SortedSet<Attribute> getOwnAttributeList() {
		return attributeList;
	}

	@Override
	public SortedSet<Attribute> getAttributeList() {
		TreeSet<Attribute> attrList = new TreeSet<Attribute>();
		attrList.addAll(attributeList);
		for (AttributedElementClass superClass : directSuperClasses) {
			attrList.addAll(superClass.getAttributeList());
		}
		return attrList;
	}

	@Override
	public boolean containsAttribute(String name) {
		return (getAttribute(name) != null);
	}

	@Override
	public int getOwnAttributeCount() {
		return attributeList.size();
	}

	@Override
	public int getAttributeCount() {
		int attrCount = getOwnAttributeCount();
		for (AttributedElementClass superClass : directSuperClasses)
			attrCount += superClass.getAttributeCount();
		return attrCount;
	}

	@Override
	public abstract String toString();

	/**
	 * @return a textual representation of all attributes the element holds
	 */
	protected String attributesToString() {
		String output = "\nSelf Attributes:\n";
		Iterator<Attribute> it = attributeList.iterator();
		Attribute a;
		while (it.hasNext()) {
			a = it.next();
			output += a.toString() + "\n";
=======
	public void addAttribute(String name, Domain domain) {
		addAttribute(new AttributeImpl(name, domain, this, null));
	}

	@Override
	public void addConstraint(Constraint constraint) {
		assertNotFinished();
		constraints = constraints.plus(constraint);
	}

	/**
	 * @return a textual representation of all attributes the element holds
	 */
	protected String attributesToString() {
		StringBuilder output = new StringBuilder("Attributes:\n");
		for (Attribute a : getAttributeList()) {
			output.append("\t" + a.toString() + "\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
		output += "\nSelf + Inherited Attributes:\n";
		it = getAttributeList().iterator();
		while (it.hasNext()) {
			a = it.next();
			output += a.toString() + "\n";
		}
		return output;
	}
=======
		return output.toString();
	}

	@Override
	public boolean containsAttribute(String name) {
		if (finished) {
			return attributeIndex.containsKey(name);
		}
		return (getAttribute(name) != null);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
		Set<SC> returnSet = new HashSet<SC>();
		for (SC subclass : directSubClasses) {
			returnSet.add(subclass);
			returnSet.addAll(subclass.getAllSubClasses());
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	@Override
	public boolean isAbstract() {
		return isAbstract;
	}

	@Override
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	/**
	 * adds a superClass to this class
	 * 
	 * @param superClass
	 *            the class to add as superclass
	 */
	protected void addSuperClass(AttributedElementClass superClass) {
		if ((superClass == this) || (superClass == null))
			return;
		if (DEBUG) {
			System.out.println("Adding superclass: "
					+ superClass.getQualifiedName() + " to class "
					+ this.getName());
		}
		directSuperClasses.remove(getSchema().getDefaultGraphClass());
		directSuperClasses.remove(getSchema().getDefaultEdgeClass());
		directSuperClasses.remove(getSchema().getDefaultVertexClass());
		directSuperClasses.remove(getSchema().getDefaultAggregationClass());
		directSuperClasses.remove(getSchema().getDefaultCompositionClass());
		for (Attribute a : superClass.getAttributeList()) {
			if (getOwnAttribute(a.getName()) != null)
				throw new SchemaException("Cannot add "
						+ superClass.getQualifiedName() + " as superclass of "
						+ getName() + ", cause: Attribute " + a.getName()
						+ " is declared in both classes");
		}
		if (superClass.isSubClassOf(this))
			throw new GraphException("Cycle in class hierarchie for classes: "
					+ getName() + " and " + superClass.getQualifiedName());
		directSuperClasses.add(superClass);
		((AttributedElementClassImpl) superClass).directSubClasses.add(this);
	}

	// /**
	// * adds a subclass to the list of subclasses, all attributes of this class
	// * and all superclasses get inherited to those classes
	// *
	// * @param subClass
	// * the AttributedElementClass to be added to the list of subclasses
	// */
	// protected void addSubClass(AttributedElementClass subClass) {
	//		
	// // subClasses.add(subClass);
	// // Iterator<AttributedElementClass> it = getAllSuperClasses().iterator();
	// // AttributedElementClass a;
	// // while (it.hasNext()) {
	// // a = it.next();
	// // if (DEBUG)
	// // System.out.println("Adding subclass " + subClass.getQualifiedName()
	// // + " to superclass " + a.getQualifiedName());
	// // ((AttributedElementClassImpl) a).addSubClass(subClass);
	// // }
	// }

	@Override
	public boolean isSuperClassOf(
			AttributedElementClass anAttributedElementClass) {
		// System.out.println(this.getName() + " is superclass of " +
		// anAttributedElementClass.getName() + ": " +
		// anAttributedElementClass.getAllSuperClasses().contains(this));
		return anAttributedElementClass.getAllSuperClasses().contains(this);
	}

	@Override
	public boolean isDirectSuperClassOf(
			AttributedElementClass anAttributedElementClass) {
		return (((AttributedElementClassImpl) anAttributedElementClass).directSuperClasses
				.contains(this));
	}

	@Override
	public boolean isSuperClassOfOrEquals(
			AttributedElementClass anAttributedElementClass) {
		return ((this == anAttributedElementClass) || (isSuperClassOf(anAttributedElementClass)));
	}

	@Override
	public boolean isSubClassOf(AttributedElementClass anAttributedElementClass) {
		return getAllSuperClasses().contains(anAttributedElementClass);
	}

	@Override
	public boolean isDirectSubClassOf(
			AttributedElementClass anAttributedElementClass) {
		return directSuperClasses.contains(anAttributedElementClass);
	}

	@Override
	public Set<AttributedElementClass> getDirectSuperClasses() {
		return new HashSet<AttributedElementClass>(directSuperClasses);
	}

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

	@Override
	public Set<AttributedElementClass> getAllSubClasses() {
		Set<AttributedElementClass> returnSet = new HashSet<AttributedElementClass>();
		for (AttributedElementClass subclass : directSubClasses) {
			returnSet.add(subclass);
			returnSet.addAll(subclass.getAllSubClasses());
=======
	@Override
	public Attribute getAttribute(String name) {
		for (Attribute a : allAttributes) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		return null;
	}

	@Override
	public int getAttributeCount() {
		return allAttributes.size();
	}

	@Override
	public List<Attribute> getAttributeList() {
		return allAttributes;
	}

	@Override
	public Set<Constraint> getConstraints() {
		return constraints;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<IC> getSchemaClass() {
		if (schemaClass == null) {
			String schemaClassName = schema.getPackagePrefix() + "."
					+ getQualifiedName();
			try {
				schemaClass = (Class<IC>) Class.forName(schemaClassName, true,
						SchemaClassManager.instance(schema.getQualifiedName()));
			} catch (ClassNotFoundException e) {
				throw new SchemaClassAccessException(
						"Can't load (generated) schema class for AttributedElementClass '"
								+ getQualifiedName() + "'", e);
			}
		}
		return schemaClass;
	}

	@SuppressWarnings("unchecked")
	@Override
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
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
		return returnSet;
	}

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

	@Override
	public Set<SC> getDirectSubClasses() {
		return directSubClasses;
	}

	@Override
	public Set<SC> getDirectSuperClasses() {
		return directSuperClasses;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<IC> getSchemaClass() {
		if (schemaClass == null) {
			String schemaClassName = getSchema().getPackagePrefix() + "."
					+ getQualifiedName();
			try {
				schemaClass = (Class<IC>) Class.forName(schemaClassName, true,
						SchemaClassManager.instance(getSchema()
								.getQualifiedName()));
			} catch (ClassNotFoundException e) {
				throw new SchemaClassAccessException(
						"Can't load (generated) schema class for AttributedElementClass '"
								+ getQualifiedName() + "'", e);
			}
		}
		return schemaClass;
	}

	@SuppressWarnings("unchecked")
	@Override
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
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
		return returnSet;
	}

	@Override
	public Set<AttributedElementClass> getDirectSubClasses() {
		return directSubClasses;
=======
		return schemaImplementationClass;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	}

	@Override
	public boolean hasAttributes() {
		return !getAttributeList().isEmpty();
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
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
	public boolean isDirectSuperClassOf(SC anAttributedElementClass) {
		return ((AttributedElementClassImpl<SC, IC>) anAttributedElementClass).directSuperClasses
				.contains(this);
	}

	@Override
	public boolean isInternal() {
		return internal;
	}

	void setInternal(Boolean b) {
		internal = b;
	}

	@Override
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	public boolean isSubClassOf(SC anAttributedElementClass) {
		return getAllSuperClasses().contains(anAttributedElementClass);
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	public int compareTo(AttributedElementClass another) {
		return qName.compareTo(another.getQName());
=======
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
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
		for (SC subClass : getAllSubClasses()) {
			Attribute subclassAttr = subClass.getAttribute(name);
			if (subclassAttr != null) {
				return true;
			}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	@SuppressWarnings("unchecked")
	public Class<? extends AttributedElement> getM1Class() {
		if (m1Class == null) {
			String m1ClassName = getSchema().getPackageName() + "." + getName();
			try {
				m1Class = (Class<? extends AttributedElement>) Class.forName(
						m1ClassName, true, M1ClassManager.instance());
			} catch (ClassNotFoundException e) {
				throw new SchemaException(
						"Can't load M1 class for AttributedElementClass '"
								+ getName() + "'", e);
			}
=======
	/**
	 * Called if the schema is finished, saves complete subclass, superclass and
	 * attribute list
	 */
	protected void finish() {
		assert allAttributes != null;

		attributeIndex = new HashMap<String, Integer>();
		int i = 0;
		for (Attribute a : allAttributes) {
			attributeIndex.put(a.getName(), i);
			++i;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
		return false;
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
		return (Class<? extends AttributedElement>) m1Class;
=======

		finished = true;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
	/**
	 * Called if the schema is finished, saves complete subclass, superclass and
	 * attribute list
	 */
	protected void finish() {
		allSuperClasses = new HashSet<SC>();
		allSuperClasses.addAll(directSuperClasses);
		for (SC superClass : directSuperClasses) {
			allSuperClasses.addAll(superClass.getAllSuperClasses());
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
	@SuppressWarnings("unchecked")
	public Class<? extends AttributedElement> getM1ImplementationClass() {
		if (isAbstract()) {
			throw new SchemaException(
					"Can't get M1 implementation class. AttributedElementClass '"
							+ getName() + "' is abstract!");
=======
	protected boolean isFinished() {
		return finished;
	}

	protected void assertNotFinished() {
		if (finished) {
			throw new SchemaException(
					"No changes allowed in a finished Schema.");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java

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
		Integer i;
		if(isFinished()) {
			i = attributeIndex.get(name);
		}
		else {
			int j = 0;
			for (Attribute a : getAttributeList()) {
				if(a.getName().equals(name)) {
					break;
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
		if (m1ImplementationClass == null)
			try {
				Field f = getM1Class().getField("IMPLEMENTATION_CLASS");
				m1ImplementationClass = (Class<? extends AttributedElement>) f
						.get(m1Class);
			} catch (SecurityException e) {
				throw new SchemaException(e);
			} catch (NoSuchFieldException e) {
				throw new SchemaException(e);
			} catch (IllegalArgumentException e) {
				throw new SchemaException(e);
			} catch (IllegalAccessException e) {
				throw new SchemaException(e);
			}
		return (Class<? extends AttributedElement>) m1ImplementationClass;
	}

	@Override
	public AttributedElementClass getLeastCommonSuperclass(
			AttributedElementClass other) {
		HashSet<AttributedElementClass> classes = new HashSet<AttributedElementClass>();
		classes.add(this);
		classes.add(other);
		return calculateLeastCommonSuperclass(classes);
	}

	@Override
	public AttributedElementClass getLeastCommonSuperclass(
			Set<? extends AttributedElementClass> other) {
		HashSet<AttributedElementClass> classes = new HashSet<AttributedElementClass>();
		classes.add(this);
		classes.addAll(other);
		return calculateLeastCommonSuperclass(classes);
	}

	public static AttributedElementClass calculateLeastCommonSuperclass(
			Set<? extends AttributedElementClass> classes) {
		AttributedElementClass leastCommon = null;
		for (AttributedElementClass a : classes) {
			boolean leastCommonCandidate = true;
			for (AttributedElementClass b : classes) {
				// if (a == null)
				// System.out.println(" A is null");
				if (!a.isSuperClassOfOrEquals(b)) {
					// System.out.println(a.getName() + " is not a superclass of
					// " + b.getName());
					leastCommonCandidate = false;
					break;
=======
	}

	@Override
	public int getAttributeIndex(String name) {
		if (finished) {
			Integer i = attributeIndex.get(name);
			if (i != null) {
				return i;
			}
		} else {
			int i = 0;
			for (Attribute a : getAttributeList()) {
				if (a.getName().equals(name)) {
					return i;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
				}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
				++j;
			}
			i = Integer.valueOf(j);
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
			}
			if (leastCommonCandidate) {
				// System.out.println("Found least common candidate: " +
				// leastCommon);
				if ((leastCommon == null) || (a.isSubClassOf(leastCommon)))
					leastCommon = a;
			}
=======
				++i;
			}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java

		if (i != null && i < allAttributeList.size()) {
			return i.intValue();
		} else {
			throw new NoSuchAttributeException(this.getSimpleName()
					+ " doesn't contain an attribute " + name);
		}

	}
||||||| /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/base.java
		if (leastCommon == null) {
			// return null;
			HashSet<AttributedElementClass> classesWithDirectSuperclasses = new HashSet<AttributedElementClass>();
			classesWithDirectSuperclasses.addAll(classes);
			for (AttributedElementClass a : classes) {
				classesWithDirectSuperclasses.addAll(a.getDirectSuperClasses());
			}
			leastCommon = calculateLeastCommonSuperclass(classesWithDirectSuperclasses);
		}
		return leastCommon;
	}

=======
		throw new NoSuchAttributeException(getQualifiedName()
				+ " doesn't contain an attribute '" + name + "'");
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
}
