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
import java.util.SortedSet;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.schema.exception.DuplicateAttributeException;
import de.uni_koblenz.jgralab.schema.exception.SchemaClassAccessException;
/**
 * This is the base class of any <code>GraphClass</code>/
 * <code>VertexClass</code>/<code>EdgeClass</code>/<code>AggregationClass</code>
 * /<code>CompositionClass</code>.
 * 
 * <p>
 * <b>Note:</b> in the following, <code>attrElement</code>, and
 * <code>attrElement'</code> , will represent the states of the given
 * <code>AttributedElementClass</code> before, respectively after, any
 * operation.
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
import de.uni_koblenz.jgralab.NoSuchAttributeException;
/**
 * This is the base class of any <code>GraphClass</code>/
 * <code>VertexClass</code>/<code>EdgeClass</code>/<code>AggregationClass</code>
 * /<code>CompositionClass</code>.
 *
 * <p>
 * <b>Note:</b> in the following, <code>attrElement</code>, and
 * <code>attrElement'</code> , will represent the states of the given
 * <code>AttributedElementClass</code> before, respectively after, any
 * operation.
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
public interface AttributedElementClass<SC extends AttributedElementClass<SC, IC>, IC extends AttributedElement<SC, IC>>
		extends NamedElement {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java

	/**
	 * Adds a new attribute <code>anAttribute</code> to this element.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.addAttribute(anAttribute);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> <code>anAttribute´s</code> name must be distinct
	 * from all of this <code>attrElement´s</code> direct and inherited
	 * attributes´ names.
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> In addition to the direct and inherited
	 * attributes(s) of <code>attrElement</code>, <code>attrElement'</code>
	 * holds a new attribute with the specified <code>name</code> and
	 * <code>domain</code>.
	 * </p>
	 * 
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
	/**
	 * Sets the package of this Domain to <code>p</code>.
	 * 
	 * @param p
	 *            the package of this Domain.
	 */
	public void setPackage(Package p);

	/**
	 * @return the package of this Domain, may be <code>null</code> for
	 *         domains not associated to a package.
	 */
	public Package getPackage();

	/**
	 * @param anAttributedElementClass
	 *            the class to search for
	 * @return true, if anAttributedElementClass is a direct or indirect
	 *         superclass of this class
	 */
	public boolean isSuperClassOf(
			AttributedElementClass anAttributedElementClass);

	/**
	 * @param anAttributedElementClass
	 *            the class to search for
	 * @return true, if anAttributedElementClass is a direct superclass of this
	 *         class
	 */
	public boolean isDirectSuperClassOf(
			AttributedElementClass anAttributedElementClass);

	/**
	 * @param anAttributedElementClass
	 *            the class to search for
	 * @return true, if anAttributedElementClass is a direct subclass of this
	 *         class
	 */
	public boolean isDirectSubClassOf(
			AttributedElementClass anAttributedElementClass);

	/**
	 * @param anAttributedElementClass
	 *            the class to search for
	 * @return true, if anAttributedElementClass is a direct or indirect
	 *         subclass of this class
	 */
	public boolean isSubClassOf(AttributedElementClass anAttributedElementClass);

	/**
	 * Tests if the given Class is a subclass of this class or equal to this
	 * class
	 * 
	 * @param anAttributedElementClass
	 *            the class to test
	 * @return true iff the given AttributedElementClass is a subclass of this
	 *         class or is this class itself
	 */
	public boolean isSuperClassOfOrEquals(
			AttributedElementClass anAttributedElementClass);

	/**
	 * @return the set of direct superclasses this class is subclass of
	 */
	public Set<AttributedElementClass> getDirectSuperClasses();

	/**
	 * @return the set of direct and indirect superclasses
	 */
	public Set<AttributedElementClass> getAllSuperClasses();

	/**
	 * @return the unique name of the element in the package without the fully
	 *         qualified package name
	 */
	public String getSimpleName();

	/**
	 * @return the unique name of the element in the schema, if there is only
	 *         one class in the schema with this short name, the short name is
	 *         returned. Otherwise, the fully qualified package name is returned
	 *         in a camel-cased underscored manner
	 */
	public String getUniqueName();

	/**
	 * @return the fully qualified name of this element in the schema. This is
	 *         the fully qualified name of the package the element is located in
	 *         together with the short name of the element
	 */
	public String getQualifiedName();

	/**
	 * @return the fully qualified name of this element in the schema. This is
	 *         the fully qualified name of the package the element is located in
	 *         together with the short name of the element
	 */
	@Deprecated
	public String getName();

	/**
	 * adds a new attribute to the element
	 * 
	 * @param name
	 *            the attribute name
	 * @param domain
	 *            the attribute domain
	 */
	public void addAttribute(String name, Domain domain);

	/**
	 * adds a new attribute to the element
	 * 
=======

	/**
	 * Adds a new attribute <code>anAttribute</code> to this element.
	 *
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.addAttribute(anAttribute);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> <code>anAttribute´s</code> name must be distinct
	 * from all of this <code>attrElement´s</code> direct and inherited
	 * attributes´ names.
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> In addition to the direct and inherited
	 * attributes(s) of <code>attrElement</code>, <code>attrElement'</code>
	 * holds a new attribute with the specified <code>name</code> and
	 * <code>domain</code>.
	 * </p>
	 *
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java
	 * @param anAttribute
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java
	 *            the new attribute to be added to this element
	 * 
	 * @throws DuplicateAttributeException
	 *             if this element has a direct or inherited attribute with the
	 *             same <code>name</code>
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
	 *            the attribute to be added
=======
	 *            the new attribute to be added to this element
	 *
	 * @throws DuplicateAttributeException
	 *             if this element has a direct or inherited attribute with the
	 *             same <code>name</code>
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java
	 */
	public void addAttribute(Attribute anAttribute);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java
	 * Adds an attribute with the given <code>name</code>, <code>domain</code>,
	 * and default value to this element.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.addAttribute(name, domain, "7");</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b>
	 * <ul>
	 * <li>The new attributes <code>name</code> must be distinct from all of
	 * this <code>attrElements</code> direct and inherited attributes names.</li>
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> In addition to the direct and inherited
	 * attributes(s) of <code>attrElement</code>, <code>attrElement'</code>
	 * holds a new attribute with the specified <code>name</code> and
	 * <code>domain</code>.
	 * </p>
	 * 
	 * @param name
	 *            a unique <code>name</code> in this element´s list of direct
	 *            and inherited attributes
	 * @param domain
	 *            the <code>domain</code> of the new <code>Attribute</code>
	 * 
	 * @param defaultValueAsString
	 *            a String representing the default value of the nerw Attribute
	 *            in TG value syntax, or null if no default value is to be
	 *            specified
	 * @throws DuplicateAttributeException
	 *             if this element has a direct or inherited attribute with the
	 *             same <code>name</code>
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
	 * adds a whole list of attributes to the element
	 * 
	 * @param attrs
	 *            the list of attributes to be appended
=======
	 * Adds an attribute with the given <code>name</code>, <code>domain</code>,
	 * and default value to this element.
	 *
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.addAttribute(name, domain, "7");</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b>
	 * <ul>
	 * <li>The new attributes <code>name</code> must be distinct from all of
	 * this <code>attrElements</code> direct and inherited attributes names.</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> In addition to the direct and inherited
	 * attributes(s) of <code>attrElement</code>, <code>attrElement'</code>
	 * holds a new attribute with the specified <code>name</code> and
	 * <code>domain</code>.
	 * </p>
	 *
	 * @param name
	 *            a unique <code>name</code> in this element´s list of direct
	 *            and inherited attributes
	 * @param domain
	 *            the <code>domain</code> of the new <code>Attribute</code>
	 *
	 * @param defaultValueAsString
	 *            a String representing the default value of the nerw Attribute
	 *            in TG value syntax, or null if no default value is to be
	 *            specified
	 * @throws DuplicateAttributeException
	 *             if this element has a direct or inherited attribute with the
	 *             same <code>name</code>
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java
	 */
	public void addAttribute(String name, Domain domain,
			String defaultValueAsString);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java
	 * Adds an attribute with the given <code>name</code> and
	 * <code>domain</code> to this element. The attribute does not have a
	 * default value.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.addAttribute(name, domain);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b>
	 * <ul>
	 * <li>The new attributes <code>name</code> must be distinct from all of
	 * this <code>attrElements</code> direct and inherited attributes names.</li>
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> In addition to the direct and inherited
	 * attributes(s) of <code>attrElement</code>, <code>attrElement'</code>
	 * holds a new attribute with the specified <code>name</code> and
	 * <code>domain</code>.
	 * </p>
	 * 
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
=======
	 * Adds an attribute with the given <code>name</code> and
	 * <code>domain</code> to this element. The attribute does not have a
	 * default value.
	 *
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.addAttribute(name, domain);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b>
	 * <ul>
	 * <li>The new attributes <code>name</code> must be distinct from all of
	 * this <code>attrElements</code> direct and inherited attributes names.</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> In addition to the direct and inherited
	 * attributes(s) of <code>attrElement</code>, <code>attrElement'</code>
	 * holds a new attribute with the specified <code>name</code> and
	 * <code>domain</code>.
	 * </p>
	 *
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java
	 * @param name
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java
	 *            a unique <code>name</code> in this element´s list of direct
	 *            and inherited attributes
	 * @param domain
	 *            the <code>domain</code> of the new <code>Attribute</code>
	 * 
	 * @throws DuplicateAttributeException
	 *             if this element has a direct or inherited attribute with the
	 *             same <code>name</code>
	 */
	public void addAttribute(String name, Domain domain);

	/**
	 * Adds a {@link Constraint} to this attributed element. Constraints are
	 * greql2 predicates, that can be used to validate the graph.
	 * 
	 * <p>
	 * <b>Note:</b> Constraints are not inheritable.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.addConstraint(constr);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attrElement'.getConstraints().size >= 0</code></li>
	 * <li>
	 * <code>attrElement'.getConstraints().size() == attrElement.getConstraints().size() + 1</code>
	 * , if for each constraint <code>c</code> of <code>attrElement</code> the
	 * following condition holds: <code>!constr.equals(c)</code></li>
	 * <li><code>attrElement'.getConstraints()</code> does not contain any
	 * inherited constraints from possible superclasses of
	 * <code>attrElement</code></li>
	 * </ul>
	 * </p>
	 * </p>
	 * 
	 * @param constraint
	 *            a {@link Constraint} to add to this element
	 */
	public void addConstraint(Constraint constraint);

	/**
	 * Checks if this element or a superclass has an attribute with the given
	 * <code>name</code>.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>containsAttr = attrElement.containsAttribute(name);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> The <code>name</code> must not be empty.
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> none
	 * </p>
	 * 
	 * @param name
	 *            the <code>name</code> of the attribute to search for
	 * 
	 * @return <code>true</code>, if the element or a superclass contains an
	 *         attribute with the specified <code>name</code>
	 * 
	 */
	public boolean containsAttribute(String name);

	/**
	 * Returns all direct and indirect subclasses of this element.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>subClasses = attrElement.getAllSubClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>subClasses != null</code></li>
	 * <li><code>subClasses.size() >= 0</code></li>
	 * <li><code>subClasses</code> holds all of <code>attrElement´s</code>
	 * direct and indirect subclasses</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct and indirect subclasses of this element
	 */
	public Set<AttributedElementClass> getAllSubClasses();

	/**
	 * Lists all direct and indirect superclasses of this element.
	 * 
	 * <p>
	 * <b>Note:</b> Each instance of a subclass of
	 * <code>AttributedElementClass</code> has a dedicated default superclass at
	 * the top of its inheritance hierarchy. Please consult the specifications
	 * of the used subclass for details.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>superClasses = attrElement.getAllSuperClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>superClasses != null </code></li>
	 * <li><code>superClasses.size() >= 0</code></li>
	 * <li><code>superClasses</code> holds all of <code>attrElement´s</code>
	 * direct and indirect superclasses (including the default superclass)</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct and indirect superclasses of this element
	 */
	public Set<AttributedElementClass> getAllSuperClasses();

	/**
	 * Fetches the attribute with the specified <code>name</code> from this
	 * element or it´s direct and indirect superclasses.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attr = attrElement.getAttribute(name);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attr</code> is a direct or inherited attribute of
	 * <code>attrElement</code> and has the specified <code>name</code></li>
	 * <li><code>attr == null </code>, if <code>attrElement</code> has no direct
	 * or inherited attribute with the given <code>name</code</li>
	 * </ul>
	 * </p>
	 * 
	 * @param name
	 *            the <code>name</code> of the attribute
	 * @return the attribute with the specified <code>name</code> or
	 *         <code>null</code> if no such attribute was found in this element
	 *         and it´s superclasses
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
	 *            the name of the attribute
	 * @return the attribute with the specified name
=======
	 *            a unique <code>name</code> in this element´s list of direct
	 *            and inherited attributes
	 * @param domain
	 *            the <code>domain</code> of the new <code>Attribute</code>
	 *
	 * @throws DuplicateAttributeException
	 *             if this element has a direct or inherited attribute with the
	 *             same <code>name</code>
	 */
	public void addAttribute(String name, Domain domain);

	/**
	 * Adds a {@link Constraint} to this attributed element. Constraints are
	 * greql2 predicates, that can be used to validate the graph.
	 *
	 * <p>
	 * <b>Note:</b> Constraints are not inheritable.
	 * </p>
	 *
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.addConstraint(constr);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attrElement'.getConstraints().size >= 0</code></li>
	 * <li>
	 * <code>attrElement'.getConstraints().size() == attrElement.getConstraints().size() + 1</code>
	 * , if for each constraint <code>c</code> of <code>attrElement</code> the
	 * following condition holds: <code>!constr.equals(c)</code></li>
	 * <li><code>attrElement'.getConstraints()</code> does not contain any
	 * inherited constraints from possible superclasses of
	 * <code>attrElement</code></li>
	 * </ul>
	 * </p>
	 * </p>
	 *
	 * @param constraint
	 *            a {@link Constraint} to add to this element
	 */
	public void addConstraint(Constraint constraint);

	/**
	 * Checks if this element or a superclass has an attribute with the given
	 * <code>name</code>.
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code>containsAttr = attrElement.containsAttribute(name);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> The <code>name</code> must not be empty.
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> none
	 * </p>
	 *
	 * @param name
	 *            the <code>name</code> of the attribute to search for
	 *
	 * @return <code>true</code>, if the element or a superclass contains an
	 *         attribute with the specified <code>name</code>
	 *
	 */
	public boolean containsAttribute(String name);

	/**
	 * Returns all direct and indirect subclasses of this element.
	 *
	 * <p>
	 * <b>Pattern:</b> <code>subClasses = attrElement.getAllSubClasses();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>subClasses != null</code></li>
	 * <li><code>subClasses.size() >= 0</code></li>
	 * <li><code>subClasses</code> holds all of <code>attrElement´s</code>
	 * direct and indirect subclasses</li>
	 * </ul>
	 * </p>
	 *
	 * @return a Set of all direct and indirect subclasses of this element
	 */
	public Set<SC> getAllSubClasses();

	/**
	 * Lists all direct and indirect superclasses of this element.
	 *
	 * <p>
	 * <b>Note:</b> Each instance of a subclass of
	 * <code>AttributedElementClass</code> has a dedicated default superclass at
	 * the top of its inheritance hierarchy. Please consult the specifications
	 * of the used subclass for details.
	 * </p>
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code>superClasses = attrElement.getAllSuperClasses();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>superClasses != null </code></li>
	 * <li><code>superClasses.size() >= 0</code></li>
	 * <li><code>superClasses</code> holds all of <code>attrElement´s</code>
	 * direct and indirect superclasses (including the default superclass)</li>
	 * </ul>
	 * </p>
	 *
	 * @return a Set of all direct and indirect superclasses of this element
	 */
	public Set<SC> getAllSuperClasses();

	/**
	 * Fetches the attribute with the specified <code>name</code> from this
	 * element or it´s direct and indirect superclasses.
	 *
	 * <p>
	 * <b>Pattern:</b> <code>attr = attrElement.getAttribute(name);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attr</code> is a direct or inherited attribute of
	 * <code>attrElement</code> and has the specified <code>name</code></li>
	 * <li><code>attr == null </code>, if <code>attrElement</code> has no direct
	 * or inherited attribute with the given <code>name</code</li>
	 * </ul>
	 * </p>
	 *
	 * @param name
	 *            the <code>name</code> of the attribute
	 * @return the attribute with the specified <code>name</code> or
	 *         <code>null</code> if no such attribute was found in this element
	 *         and it´s superclasses
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java
	 */
	public Attribute getAttribute(String name);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java
	 * Gets the direct and inherited attribute count for this element.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attrCount = attrElement.getAttributeCount();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attrCount >= 0</code></li>
	 * <li><code>attrCount</code> equals the number of
	 * <code>attrElement´s</code> direct and inherited attributes</li>
	 * </ul>
	 * </p>
	 * 
	 * @return the number of this element´s direct and inherited attributes
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
	 * @param name
	 *            the name of the attribute
	 * @return the attribute with the specified name
=======
	 * Gets the direct and inherited attribute count for this element.
	 *
	 * <p>
	 * <b>Pattern:</b> <code>attrCount = attrElement.getAttributeCount();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attrCount >= 0</code></li>
	 * <li><code>attrCount</code> equals the number of
	 * <code>attrElement´s</code> direct and inherited attributes</li>
	 * </ul>
	 * </p>
	 *
	 * @return the number of this element´s direct and inherited attributes
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java
	 */
	public int getAttributeCount();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java
	 * Returns all of this element´s direct and inherited attributes in natural
	 * order.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attrs = attrElement.getAttributeList();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attrs != null</code></li>
	 * <li><code>attrs.size() >= 0</code></li>
	 * <li><code>attrs</code> contains every of <code>attrElement´s</code>
	 * direct and inherited attributes
	 * <li>the attributes in <code>attrs</code> are sorted lexicographically by
	 * their qualified name</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a SortedSet of attributes of this element and all inherited
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
	 * @return the list of attributes of this element without inherited
	 *         attributes
	 */
	public SortedSet<Attribute> getOwnAttributeList();

	/**
	 * @return the list of attributes of this element and all inherited
=======
	 * Returns all of this element´s direct and inherited attributes in natural
	 * order.
	 *
	 * <p>
	 * <b>Pattern:</b> <code>attrs = attrElement.getAttributeList();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attrs != null</code></li>
	 * <li><code>attrs.size() >= 0</code></li>
	 * <li><code>attrs</code> contains every of <code>attrElement´s</code>
	 * direct and inherited attributes
	 * <li>the attributes in <code>attrs</code> are sorted lexicographically by
	 * their qualified name</li>
	 * </ul>
	 * </p>
	 *
	 * @return a SortedSet of attributes of this element and all inherited
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java
	 *         attributes
	 */
	public SortedSet<Attribute> getAttributeList();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java
	 * Returns this element's Set of {@link Constraint}s.
	 * 
	 * <p>
	 * Constraints are greql2 predicates, that can be used to validate the
	 * graph. Constraints are bound to a specific attributed element and are not
	 * inheritable.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>constrs = attrElement.getConstraints();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>constrs != null</code></li>
	 * <li><code>constrs.size() >= 0</code></li>
	 * <li><code>constrs</code> contains all of this element's constraints</li>
	 * <li><code>constrs</code> does not contain any inherited constraint</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all {@link Constraint}s of this attributed element
	 */
	public Set<Constraint> getConstraints();

	/**
	 * Lists all direct subclasses of this element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>subClasses = attrElement.getDirectSubClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>subClasses != null</code></li>
	 * <li><code>subClasses.size() >= 0</code></li>
	 * <li><code>subClasses</code> holds all of <code>attrElement´s</code>
	 * direct subclasses</li>
	 * <li><code>subClasses</code> does not hold any of
	 * <code>attrElement´s</code> inherited subclasses</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct subclasses of this element
	 */
	public Set<AttributedElementClass> getDirectSubClasses();

	/**
	 * Returns all direct superclasses of this element.
	 * 
	 * <p>
	 * <b>Note:</b> Each instance of a subclass of
	 * <code>AttributedElementClass</code> has one default direct superclass.
	 * Please consult the specifications of the used subclass for details.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>superClasses = attrElement.getDirectSuperClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>superClasses != null</code></li>
	 * <li><code>superClasses.size() >= 0</code></li>
	 * <li><code>superClasses</code> holds all of <code>attrElement´s</code>
	 * direct superclasses (including the default superclass)</li>
	 * <li><code>superClasses</code> does not hold any of
	 * <code>attrElement´s</code> inherited superclasses
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct superclasses of this element
	 */
	public Set<AttributedElementClass> getDirectSuperClasses();

	/**
	 * Returns the schema interface for this attributed element.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>schemaClass = attrElement.getSchemaClass();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> not yet defined
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> not yet defined
	 * </p>
	 * 
	 * @return the schema interface class for this element
	 * 
	 * @throws SchemaClassAccessException
	 *             if reflection exceptions occur.
	 */
	public Class<? extends AttributedElement> getSchemaClass();

	/**
	 * Returns the schema implementation class for this attributed element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>schemaImplClass = attrElement.getSchemaImplementationClass();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> not yet defined
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> not yet defined
	 * </p>
	 * 
	 * @return the schema implementation class for this element
	 * 
	 * @throws SchemaClassAccessException
	 *             if:
	 *             <ul>
	 *             <li>this element is abstract</li>
	 *             <li>there are reflection exceptions</li>
	 *             </ul>
	 */
	public Class<? extends AttributedElement> getSchemaImplementationClass();

	/**
	 * Fetches the attribute with the specified <code>name</code> from this
	 * element.
	 * <p>
	 * Unlike
	 * {@link de.uni_koblenz.jgralab.schema.AttributedElementClass#getAttribute(String)
	 * getAttribute(String name)}, this method does not consider inherited
	 * attributes.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attr = attrElement.getOwnAttribute(name);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attr</code> is a direct attribute of <code>attrElement</code>
	 * and has the specified <code>name</code></li>
	 * <li><code>attr == null </code>, if <code>attrElement</code> has no direct
	 * attribute with the given <code>name</code</li>
	 * </ul>
	 * </p>
	 * 
	 * @param name
	 *            the <code>name</code> of the attribute
	 * @return the attribute with the specified <code>name</code> or
	 *         <code>null</code> if no such attribute was found directly in this
	 *         element
	 */
	public Attribute getOwnAttribute(String name);

	/**
	 * Gets the attribute count for this element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>attrCount = attrElement.getOwnAttributeCount();</code>
	 * </p>
	 * 
	 * <p>
	 * Unlike
	 * {@link de.uni_koblenz.jgralab.schema.AttributedElementClass#getAttributeCount()
	 * getAttributeCount()}, this method does not count inherited attributes.
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attrCount >= 0</code></li>
	 * <li><code>attrCount</code> equals the number of
	 * <code>attrElement´s</code> direct attributes</li>
	 * <li><code>attrCount</code> does not contain inherited attributes</li>
	 * </ul>
	 * </p>
	 * 
	 * @return the number of this element´s direct attributes
	 */
	public int getOwnAttributeCount();

	/**
	 * Returns all of this element´s attributes.
	 * 
	 * <p>
	 * Unlike
	 * {@link de.uni_koblenz.jgralab.schema.AttributedElementClass#getAttributeList()
	 * getAttributeList()}, this method does not consider inherited attributes.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attrs = attrElement.getOwnAttributeList();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attrs != null</code></li>
	 * <li><code>attrs.size() >= 0</code></li>
	 * <li><code>attrs</code> contains all attributes of
	 * <code>attrElement´s</code> direct attributes</li>
	 * <li><code>attrs</code> does not contain any inherited attributes of
	 * <code>attrElement</code></li>
	 * <li>the attributes in <code>attrs</code> are sorted lexicographically by
	 * their qualified name</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a SortedSet of attributes of this element
	 */
	public SortedSet<Attribute> getOwnAttributeList();

	/**
	 * Retrieves the name used for elements of this AttributedElementClass in
	 * files created by the code generator.
	 * 
	 * @return the variable name.
	 */
	public String getVariableName();

	/**
	 * Checks if this element has direct or inherited attributes.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>hasAttributes = attrElement.hasAttributes();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>hasAttributes</code> has one of the
	 * following values:
	 * <ul>
	 * <li><code>true</code> if one of the following or both occur:
	 * <ul>
	 * <li><code>attrElement</code> has direct attributes</li>
	 * <li><code>attrElement</code> has inherited attributes</li>
	 * </ul>
	 * </li>
	 * <li><code>false</code> if the above is not met</li>
	 * </ul>
	 * </p>
	 * 
	 * @return <code>true</code>, if the element has own or inherited
	 *         attributes, <code>false</code> otherwise
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
	 * @param name
	 *            the name of the attribute to search for
	 * @return true, if the element or its superclasses contains an attribute
	 *         with the specified name
	 */
	public boolean containsAttribute(String name);

	/**
	 * @return the number of attributes this element holds including inherited
	 *         attributes
	 */
	public int getAttributeCount();

	/**
	 * @return the number of attributes this element holds without inherited
	 *         attributes
	 */
	public int getOwnAttributeCount();

	/**
	 * this method has to be implemented by each specialised class, it has to
	 * return a textual representation of that class
	 */
	public abstract String toString();

	/**
	 * @return true, if the element may not have any instances
	 */
	public boolean isAbstract();

	/**
	 * defines if the element may or may not have any instances
	 * 
	 * @param isAbstract
	 *            true or false
	 */
	public void setAbstract(boolean isAbstract);

	/**
	 * @return all direct subclasses of this class
	 */
	public Set<AttributedElementClass> getDirectSubClasses();

	/**
	 * @return all direct and indirect subclasses of this class
	 */
	public Set<AttributedElementClass> getAllSubClasses();

	/**
	 * @return true, if the element has own or inherited attributes, false
	 *         otherwise
=======
	 * Returns this element's Set of {@link Constraint}s.
	 *
	 * <p>
	 * Constraints are greql2 predicates, that can be used to validate the
	 * graph. Constraints are bound to a specific attributed element and are not
	 * inheritable.
	 * </p>
	 *
	 * <p>
	 * <b>Pattern:</b> <code>constrs = attrElement.getConstraints();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>constrs != null</code></li>
	 * <li><code>constrs.size() >= 0</code></li>
	 * <li><code>constrs</code> contains all of this element's constraints</li>
	 * <li><code>constrs</code> does not contain any inherited constraint</li>
	 * </ul>
	 * </p>
	 *
	 * @return a Set of all {@link Constraint}s of this attributed element
	 */
	public Set<Constraint> getConstraints();

	/**
	 * Lists all direct subclasses of this element.
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code>subClasses = attrElement.getDirectSubClasses();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>subClasses != null</code></li>
	 * <li><code>subClasses.size() >= 0</code></li>
	 * <li><code>subClasses</code> holds all of <code>attrElement´s</code>
	 * direct subclasses</li>
	 * <li><code>subClasses</code> does not hold any of
	 * <code>attrElement´s</code> inherited subclasses</li>
	 * </ul>
	 * </p>
	 *
	 * @return a Set of all direct subclasses of this element
	 */
	public Set<SC> getDirectSubClasses();

	/**
	 * Returns all direct superclasses of this element.
	 *
	 * <p>
	 * <b>Note:</b> Each instance of a subclass of
	 * <code>AttributedElementClass</code> has one default direct superclass.
	 * Please consult the specifications of the used subclass for details.
	 * </p>
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code>superClasses = attrElement.getDirectSuperClasses();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>superClasses != null</code></li>
	 * <li><code>superClasses.size() >= 0</code></li>
	 * <li><code>superClasses</code> holds all of <code>attrElement´s</code>
	 * direct superclasses (including the default superclass)</li>
	 * <li><code>superClasses</code> does not hold any of
	 * <code>attrElement´s</code> inherited superclasses
	 * </ul>
	 * </p>
	 *
	 * @return a Set of all direct superclasses of this element
	 */
	public Set<SC> getDirectSuperClasses();

	/**
	 * Returns the schema interface for this attributed element.
	 *
	 * <p>
	 * <b>Pattern:</b> <code>schemaClass = attrElement.getSchemaClass();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> not yet defined
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> not yet defined
	 * </p>
	 *
	 * @return the schema interface class for this element
	 *
	 * @throws SchemaClassAccessException
	 *             if reflection exceptions occur.
	 */
	public Class<IC> getSchemaClass();

	/**
	 * Returns the schema implementation class for this attributed element.
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code>schemaImplClass = attrElement.getSchemaImplementationClass();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> not yet defined
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> not yet defined
	 * </p>
	 *
	 * @return the schema implementation class for this element
	 *
	 * @throws SchemaClassAccessException
	 *             if:
	 *             <ul>
	 *             <li>this element is abstract</li>
	 *             <li>there are reflection exceptions</li>
	 *             </ul>
	 */
	public Class<IC> getSchemaImplementationClass();

	/**
	 * Fetches the attribute with the specified <code>name</code> from this
	 * element.
	 * <p>
	 * Unlike
	 * {@link de.uni_koblenz.jgralab.schema.AttributedElementClass#getAttribute(String)
	 * getAttribute(String name)}, this method does not consider inherited
	 * attributes.
	 * </p>
	 *
	 * <p>
	 * <b>Pattern:</b> <code>attr = attrElement.getOwnAttribute(name);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attr</code> is a direct attribute of <code>attrElement</code>
	 * and has the specified <code>name</code></li>
	 * <li><code>attr == null </code>, if <code>attrElement</code> has no direct
	 * attribute with the given <code>name</code</li>
	 * </ul>
	 * </p>
	 *
	 * @param name
	 *            the <code>name</code> of the attribute
	 * @return the attribute with the specified <code>name</code> or
	 *         <code>null</code> if no such attribute was found directly in this
	 *         element
	 */
	public Attribute getOwnAttribute(String name);

	/**
	 * Gets the attribute count for this element.
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code>attrCount = attrElement.getOwnAttributeCount();</code>
	 * </p>
	 *
	 * <p>
	 * Unlike
	 * {@link de.uni_koblenz.jgralab.schema.AttributedElementClass#getAttributeCount()
	 * getAttributeCount()}, this method does not count inherited attributes.
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attrCount >= 0</code></li>
	 * <li><code>attrCount</code> equals the number of
	 * <code>attrElement´s</code> direct attributes</li>
	 * <li><code>attrCount</code> does not contain inherited attributes</li>
	 * </ul>
	 * </p>
	 *
	 * @return the number of this element´s direct attributes
	 */
	public int getOwnAttributeCount();

	/**
	 * Returns all of this element´s attributes.
	 *
	 * <p>
	 * Unlike
	 * {@link de.uni_koblenz.jgralab.schema.AttributedElementClass#getAttributeList()
	 * getAttributeList()}, this method does not consider inherited attributes.
	 * </p>
	 *
	 * <p>
	 * <b>Pattern:</b> <code>attrs = attrElement.getOwnAttributeList();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attrs != null</code></li>
	 * <li><code>attrs.size() >= 0</code></li>
	 * <li><code>attrs</code> contains all attributes of
	 * <code>attrElement´s</code> direct attributes</li>
	 * <li><code>attrs</code> does not contain any inherited attributes of
	 * <code>attrElement</code></li>
	 * <li>the attributes in <code>attrs</code> are sorted lexicographically by
	 * their qualified name</li>
	 * </ul>
	 * </p>
	 *
	 * @return a SortedSet of attributes of this element
	 */
	public SortedSet<Attribute> getOwnAttributeList();

	/**
	 * Retrieves the name used for elements of this AttributedElementClass in
	 * files created by the code generator.
	 *
	 * @return the variable name.
	 */
	public String getVariableName();

	/**
	 * Checks if this element has direct or inherited attributes.
	 *
	 * <p>
	 * <b>Pattern:</b> <code>hasAttributes = attrElement.hasAttributes();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> <code>hasAttributes</code> has one of the
	 * following values:
	 * <ul>
	 * <li><code>true</code> if one of the following or both occur:
	 * <ul>
	 * <li><code>attrElement</code> has direct attributes</li>
	 * <li><code>attrElement</code> has inherited attributes</li>
	 * </ul>
	 * </li>
	 * <li><code>false</code> if the above is not met</li>
	 * </ul>
	 * </p>
	 *
	 * @return <code>true</code>, if the element has own or inherited
	 *         attributes, <code>false</code> otherwise
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java
	 */
	public boolean hasAttributes();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java
	 * Checks if this element has own attributes, that are not inherited.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>hasOwnAttributes = attrElement.hasOwnAttributes();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>hasOwnAttributes</code> has one of the
	 * following values:
	 * <ul>
	 * <li><code>true</code> if <code>attrElement</code> has direct attributes</li>
	 * <li><code>false</code> if <coed>attrElement</code> has:</li>
	 * <ul>
	 * <li>only inherited attributes</code>
	 * <li>no attributes at all</code>
	 * </ul>
	 * </ul>
	 * </p>
	 * 
	 * @return <code>true</code>, if the element has own attributes,
	 *         <code>false</code> otherwise
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
	 * Checks if this AttributedElementClass has own attributes
	 * 
	 * @return true if this AttributedElementClass contains at least one
	 *         non-inherited attributed, false otherwise
=======
	 * Checks if this element has own attributes, that are not inherited.
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code>hasOwnAttributes = attrElement.hasOwnAttributes();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> <code>hasOwnAttributes</code> has one of the
	 * following values:
	 * <ul>
	 * <li><code>true</code> if <code>attrElement</code> has direct attributes</li>
	 * <li><code>false</code> if <coed>attrElement</code> has:</li>
	 * <ul>
	 * <li>only inherited attributes</code>
	 * <li>no attributes at all</code>
	 * </ul>
	 * </ul>
	 * </p>
	 *
	 * @return <code>true</code>, if the element has own attributes,
	 *         <code>false</code> otherwise
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java
	 */
	public boolean hasOwnAttributes();
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java

	/**
	 * States if this attributed element is abstract. Abstract elements can´t
	 * have instances.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>isAbstract = attrElement.isAbstract();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isAbstract</code> is:
	 * <ul>
	 * <li><code>true</code> if <code>attrElement</code> is abstract and
	 * therefore may not have any instances</li>
	 * <li>otherwise <code>false</code>
	 * </ul>
	 * 
	 * @return <code>true</code>, if the element is abstract , otherwise
	 *         <code>false</code>
	 */
	public boolean isAbstract();

	/**
	 * Checks if the current element is a direct subclass of another attributed
	 * element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isDirectSubClass = attrElement.isDirectSubClassOf(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isDirectSubClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> attributed element is a
	 * direct superclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> attributed
	 * element are the same</li>
	 * <li>the <code>other</code> attributed element is not a direct superclass
	 * of <code>attrElement</code></li>
	 * <li>the <code>other</code> attributed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param anAttributedElementClass
	 *            the possible superclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct subclass of this element, otherwise <code>false</code>
	 */
	public boolean isDirectSubClassOf(
			AttributedElementClass anAttributedElementClass);

	/**
	 * Checks if the current element is a direct superclass of another
	 * attributed element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isDirectSuperClass = attrElement.isDirectSuperClassOf(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isDirectSuperClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> attributed element is a
	 * direct subclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> attributed
	 * element are the same</li>
	 * <li>the <code>other</code> attributed element is not a direct subclass of
	 * <code>attrElement</code></li>
	 * <li>the <code>other</code> attributed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param anAttributedElementClass
	 *            the possible subclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct subclass of this element, otherwise <code>false</code>
	 */
	public boolean isDirectSuperClassOf(
			AttributedElementClass anAttributedElementClass);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
=======

	/**
	 * States if this attributed element is abstract. Abstract elements can´t
	 * have instances.
	 *
	 * <p>
	 * <b>Pattern:</b> <code>isAbstract = attrElement.isAbstract();</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> <code>isAbstract</code> is:
	 * <ul>
	 * <li><code>true</code> if <code>attrElement</code> is abstract and
	 * therefore may not have any instances</li>
	 * <li>otherwise <code>false</code>
	 * </ul>
	 *
	 * @return <code>true</code>, if the element is abstract , otherwise
	 *         <code>false</code>
	 */
	public boolean isAbstract();

	/**
	 * Checks if the current element is a direct subclass of another attributed
	 * element.
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isDirectSubClass = attrElement.isDirectSubClassOf(other);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> <code>isDirectSubClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> attributed element is a
	 * direct superclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> attributed
	 * element are the same</li>
	 * <li>the <code>other</code> attributed element is not a direct superclass
	 * of <code>attrElement</code></li>
	 * <li>the <code>other</code> attributed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 *
	 * @param anAttributedElementClass
	 *            the possible superclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct subclass of this element, otherwise <code>false</code>
	 */
	public boolean isDirectSubClassOf(SC anAttributedElementClass);

	/**
	 * Checks if the current element is a direct superclass of another
	 * attributed element.
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isDirectSuperClass = attrElement.isDirectSuperClassOf(other);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> <code>isDirectSuperClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> attributed element is a
	 * direct subclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> attributed
	 * element are the same</li>
	 * <li>the <code>other</code> attributed element is not a direct subclass of
	 * <code>attrElement</code></li>
	 * <li>the <code>other</code> attributed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 *
	 * @param anAttributedElementClass
	 *            the possible subclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct subclass of this element, otherwise <code>false</code>
	 */
	public boolean isDirectSuperClassOf(SC anAttributedElementClass);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java

	/**
	 * @return true, if this AttributedElementClass is only for internal use
	 */
	public boolean isInternal();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java
	 * Checks if the current element is a direct or indirect subclass of another
	 * attributed element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSubClass = attrElement.isSubClassOf(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isSubClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> attributed element is a
	 * direct or inherited superclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> attributed
	 * element are the same</li>
	 * <li>the <code>other</code> attributed element is not a direct or
	 * inherited superclass of <code>attrElement</code></li>
	 * <li>the <code>other</code> attributed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param anAttributedElementClass
	 *            the possible superclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct or indirect subclass of this element, otherwise
	 *         <code>false</code>
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
	 * sets the <code>internal</code>-flag of this AttributedElementClass, if
	 * this is set, the AttributedElementClass is only for internal use (for
	 * instance, the AttributedElementClasses 'Graph', 'Vertex', 'Edge',
	 * 'Composition' and 'Aggregation' are such AttributedElementClasses only
	 * for internal use
	 * 
	 * @param internal
	 */
	public void setInternal(boolean internal);

	/**
	 * @return the schema this AttributedElementClass belongs to
	 */
	public Schema getSchema();

	/**
	 * @return the M1 interface class for this AttributedElementClass if
	 *         reflection exceptions occures
	 */
	public Class<? extends AttributedElement> getM1Class();

	/**
	 * @return the M1 implementation class for this AttributedElementClass if
	 *         this AttributedElementClass is abstract, or upon reflection
	 *         exceptions
	 */
	public Class<? extends AttributedElement> getM1ImplementationClass();

	/**
	 * Returns the least common superclass of this class and the given class
	 * <code>other</code>
	 * 
	 * @return the least common superclass
=======
	 * Checks if the current element is a direct or indirect subclass of another
	 * attributed element.
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSubClass = attrElement.isSubClassOf(other);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> <code>isSubClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> attributed element is a
	 * direct or inherited superclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> attributed
	 * element are the same</li>
	 * <li>the <code>other</code> attributed element is not a direct or
	 * inherited superclass of <code>attrElement</code></li>
	 * <li>the <code>other</code> attributed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 *
	 * @param anAttributedElementClass
	 *            the possible superclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct or indirect subclass of this element, otherwise
	 *         <code>false</code>
	 */
	public boolean isSubClassOf(SC anAttributedElementClass);

	/**
	 * Checks if the current element is a direct or inherited superclass of
	 * another attributed element.
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSuperClass = attrElement.isSuperClass(other);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> <code>isSuperClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> attributed element is a
	 * direct or inherited subclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> attributed
	 * element are the same</li>
	 * <li>the <code>other</code> attributed element is not a direct or indirect
	 * subclass of <code>attrElement</code></li>
	 * <li>the <code>other</code> attributed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 *
	 * @param anAttributedElementClass
	 *            the possible subclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct or indirect subclass of this element, otherwise
	 *         <code>false</code>
	 */
	public boolean isSuperClassOf(SC anAttributedElementClass);

	/**
	 * Tests if the current element equals another attributed element or is
	 * another attributes element´s direct or indirect superclass.
	 *
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSuperClassOrEquals = attrElement.isSuperClassOfOrEquals(other);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> <code>isSuperClassOrEquals</code> is:
	 * <ul>
	 * <li><code>true</code> if one of the following occurs:
	 * <ul>
	 * <li>the <code>other</code> attributed element is a direct or indirect
	 * subclass of this element</li>
	 * <li><code>attrElement == other</code></li>
	 * </ul>
	 * </li>
	 * <li><code>false</code> if the <code>other</code> attributed element has
	 * no relation with <code>attrElement</code> (not the same, not a direct or
	 * indirect subclass)</li>
	 * </ul>
	 * </p>
	 *
	 * @param anAttributedElementClass
	 *            the possible subclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct or indirect subclass of this element or <code>this</code>
	 *         attributed element itself, otherwise <code>false</code>
	 */
	public boolean isSuperClassOfOrEquals(SC anAttributedElementClass);

	/**
	 * Defines if this attributed element is abstract. Abstract elements can´t
	 * have instances.
	 *
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.setAbstract(value);</code>
	 * </p>
	 *
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 *
	 * <p>
	 * <b>Postconditions:</b> <code>attrElement'</code> is abstract and no new
	 * instances can be created
	 * </p>
	 *
	 * @param isAbstract
	 *            the new value defining the state of this attributed element
	 */
	public void setAbstract(boolean isAbstract);

	/**
	 * <p>
	 * Returns an index for the attribute with the given name.
	 * </p>
	 *
	 * <p>
	 * The index can be used to dereference the attribute's value in the generic
	 * implementation. If an attribute by the given name does not exist, a
	 * {@link NoSuchAttributeException} will be thrown.
	 * </p>
	 *
	 * @param name
	 *            The attributes's name
	 * @return An index for the attribute.
	 * @throws NoSuchAttributeException
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java
	public boolean isSubClassOf(AttributedElementClass anAttributedElementClass);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
	public AttributedElementClass getLeastCommonSuperclass(
			AttributedElementClass other);
=======
	public int getAttributeIndex(String name) throws NoSuchAttributeException;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/left.java
	/**
	 * Checks if the current element is a direct or inherited superclass of
	 * another attributed element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSuperClass = attrElement.isSuperClass(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isSuperClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> attributed element is a
	 * direct or inherited subclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> attributed
	 * element are the same</li>
	 * <li>the <code>other</code> attributed element is not a direct or indirect
	 * subclass of <code>attrElement</code></li>
	 * <li>the <code>other</code> attributed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param anAttributedElementClass
	 *            the possible subclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct or indirect subclass of this element, otherwise
	 *         <code>false</code>
	 */
	public boolean isSuperClassOf(
			AttributedElementClass anAttributedElementClass);

	/**
	 * Tests if the current element equals another attributed element or is
	 * another attributes element´s direct or indirect superclass.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSuperClassOrEquals = attrElement.isSuperClassOfOrEquals(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isSuperClassOrEquals</code> is:
	 * <ul>
	 * <li><code>true</code> if one of the following occurs:
	 * <ul>
	 * <li>the <code>other</code> attributed element is a direct or indirect
	 * subclass of this element</li>
	 * <li><code>attrElement == other</code></li>
	 * </ul>
	 * </li>
	 * <li><code>false</code> if the <code>other</code> attributed element has
	 * no relation with <code>attrElement</code> (not the same, not a direct or
	 * indirect subclass)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param anAttributedElementClass
	 *            the possible subclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct or indirect subclass of this element or <code>this</code>
	 *         attributed element itself, otherwise <code>false</code>
	 */
	public boolean isSuperClassOfOrEquals(
			AttributedElementClass anAttributedElementClass);

	/**
	 * Defines if this attributed element is abstract. Abstract elements can´t
	 * have instances.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.setAbstract(value);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>attrElement'</code> is abstract and no new
	 * instances can be created
	 * </p>
	 * 
	 * @param isAbstract
	 *            the new value defining the state of this attributed element
	 */
	public void setAbstract(boolean isAbstract);

||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/base.java
	/**
	 * Returns the least common superclass of this class and the classes in the
	 * set <code>other</code>
	 * 
	 * @return the least common superclass
	 */
	public AttributedElementClass getLeastCommonSuperclass(
			Set<? extends AttributedElementClass> other);
=======
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/AttributedElementClass.java/right.java
}
