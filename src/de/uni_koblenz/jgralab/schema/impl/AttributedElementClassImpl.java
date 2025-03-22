package de.uni_koblenz.jgralab.schema.impl;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.pcollections.ArrayPSet;
import org.pcollections.ArrayPVector;
import org.pcollections.PSet;
import org.pcollections.PVector;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.Constraint;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.exception.DuplicateAttributeException;
import de.uni_koblenz.jgralab.schema.exception.InheritanceException;
import de.uni_koblenz.jgralab.schema.exception.SchemaClassAccessException;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;
import de.uni_koblenz.jgralab.schema.impl.compilation.SchemaClassManager;

public abstract class AttributedElementClassImpl<SC extends AttributedElementClass<SC, IC>, IC extends AttributedElement<SC, IC>> extends NamedElementImpl implements AttributedElementClass<SC, IC> {
  /**
	 * the list of attributes. Only the own attributes of this class are stored
	 * here, no inherited attributes
	 */
  private protected final 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
  TreeSet
=======
  PVector
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
  <Attribute> 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
  attributeList = new TreeSet<Attribute>()
=======
  allAttributes
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
  ;

  /**
	 * the list of all attributes. Own attributes and inherited attributes are
	 * stored here - but only if the schema is finish
	 */
  private SortedSet<Attribute> allAttributeList;

  /**
	 * A set of {@link Constraint}s which can be used to validate the graph.
	 */
  protected 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
  HashSet
=======
  PSet
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
  <Constraint> constraints = new HashSet<Constraint>(1);

  /**
	 * the immediate sub classes of this class
	 */
  protected Set<SC> directSubClasses = new HashSet<SC>();

  /**
	 * the sub classes of this class - only set if the schema is finish
	 */
  protected Set<SC> allSubClasses;

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
  private protected boolean finished = false;

  /**
	 * true if element class is abstract
	 */
  private boolean isAbstract = false;

  private boolean internal = false;

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
  private Class<IC> schemaImplementationClass;

  /**
	 * builds a new attributed element class
	 *
	 * @param qn
	 *            the unique identifier of the element in the schema
	 */
  protected AttributedElementClassImpl(String simpleName, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
  Package
=======
  PackageImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
   pkg, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
  Schema
=======
  SchemaImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
   schema) {
    super(simpleName, pkg, schema);
    allAttributes = ArrayPVector.empty();
    constraints = ArrayPSet.empty();
  }

  @Override public void addAttribute(Attribute anAttribute) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    if (finished) {
      throw new SchemaException("No changes to finished schema!");
    }
=======
    assertNotFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java

    if (containsAttribute(anAttribute.getName())) {
      throw new 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
      DuplicateAttributeException
=======
      SchemaException
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
      (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
      anAttribute.getName()
=======
      "Duplicate attribute \'" + anAttribute.getName() + "\' in AttributedElementClass \'" + getQualifiedName() + "\'"
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
      , getQualifiedName());
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    if (subclassContainsAttribute(anAttribute.getName())) {
      throw new DuplicateAttributeException("Duplicate Attribute \'" + anAttribute.getName() + "\' in AttributedElementClass \'" + getQualifiedName() + "\'. " + "A derived AttributedElementClass already contains this Attribute.");
    }
=======
    TreeSet<Attribute> s = new TreeSet<Attribute>(allAttributes);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    attributeList
=======
    s
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
    .add(anAttribute);
    allAttributes = ArrayPVector.<Attribute>empty().plusAll(s);
  }

  @Override public void addAttribute(String name, Domain domain, String defaultValueAsString) {
    addAttribute(new AttributeImpl(name, domain, this, defaultValueAsString));
  }

  @Override public void addAttribute(String name, Domain domain) {
    addAttribute(new AttributeImpl(name, domain, this, null));
  }

  @Override public void addConstraint(Constraint constraint) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    if (finished) {
      throw new SchemaException("No changes to finished schema!");
    }
=======
    assertNotFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    constraints.add(constraint);
=======
    constraints = constraints.plus(constraint);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
  }

  /**
	 * adds a superClass to this class
	 *
	 * @param superClass
	 *            the class to add as superclass
	 */
  @SuppressWarnings(value = { "unchecked" }) protected void addSuperClass(SC superClass) {
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
        throw new InheritanceException("Cannot add " + superClass.getQualifiedName() + " as superclass of " + getQualifiedName() + ", cause: Attribute " + a.getName() + " is declared in both classes");
      }
    }
    if (superClass.isSubClassOf((SC) this)) {
      throw new InheritanceException("Cycle in class hierarchie for classes: " + getQualifiedName() + " and " + superClass.getQualifiedName());
    }
    directSuperClasses.add(superClass);
    ((AttributedElementClassImpl<SC, IC>) superClass).directSubClasses.add((SC) this);
  }

  /**
	 * @return a textual representation of all attributes the element holds
	 */
  protected String attributesToString() {
    StringBuilder output = new StringBuilder(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    "\nSelf Attributes:\n"
=======
    "Attributes:\n"
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
    );

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    Iterator<Attribute> it = attributeList.iterator();
=======
    for (Attribute a : getAttributeList()) {
      output.append("\t" + a.toString() + "\n");
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java

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

  @Override public boolean containsAttribute(String name) {
    if (finished) {
      return attributeIndex.containsKey(name);
    }
    return (getAttribute(name) != null);
  }

  @Override public Set<SC> getAllSubClasses() {
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

  @Override public Set<SC> getAllSuperClasses() {
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

  @Override public Attribute getAttribute(String name) {
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
    for (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    SC
=======
    Attribute
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
     superClass : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    directSuperClasses
=======
    allAttributes
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
    ) {
      Attribute inheritedAttr = superClass.getAttribute(name);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
      if (inheritedAttr != null) {
        return inheritedAttr;
      }
=======
      if (a.getName().equals(name)) {
        return a;
      }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
    }
    return null;
  }

  @Override public int getAttributeCount() {
    if (finished) {
      return allAttributeList.size();
    }
    int attrCount = getOwnAttributeCount();
    for (SC superClass : directSuperClasses) {
      attrCount += superClass.getAttributeCount();
    }
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    attrCount
=======
    allAttributes.size()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
    ;
  }

  @Override public 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
  SortedSet
=======
  List
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
  <Attribute> getAttributeList() {
    if (finished) {
      return allAttributeList;
    }
    TreeSet<Attribute> attrList = new TreeSet<Attribute>();
    attrList.addAll(attributeList);
    for (SC superClass : directSuperClasses) {
      attrList.addAll(superClass.getAttributeList());
    }
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    attrList
=======
    allAttributes
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
    ;
  }

  @Override public Set<Constraint> getConstraints() {
    return constraints;
  }

  @Override public Set<SC> getDirectSubClasses() {
    return directSubClasses;
  }

  @Override public Set<SC> getDirectSuperClasses() {
    return directSuperClasses;
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public Class<IC> getSchemaClass() {
    if (schemaClass == null) {
      String schemaClassName = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
      getSchema().getPackagePrefix()
=======
      schema.getPackagePrefix()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
       + "." + getQualifiedName();
      try {
        schemaClass = (Class<IC>) Class.forName(schemaClassName, true, SchemaClassManager.instance(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
        getSchema().getQualifiedName()
=======
        schema.getQualifiedName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
        ));
      } catch (ClassNotFoundException e) {
        throw new SchemaClassAccessException("Can\'t load (generated) schema class for AttributedElementClass \'" + getQualifiedName() + "\'", e);
      }
    }
    return schemaClass;
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public Class<IC> getSchemaImplementationClass() {
    if (isAbstract()) {
      throw new SchemaClassAccessException("Can\'t get (generated) schema implementation class. AttributedElementClass \'" + getQualifiedName() + "\' is abstract!");
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

  @Override public Attribute getOwnAttribute(String name) {
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

  @Override public int getOwnAttributeCount() {
    return attributeList.size();
  }

  @Override public SortedSet<Attribute> getOwnAttributeList() {
    return attributeList;
  }

  @Override public boolean hasAttributes() {
    return !getAttributeList().isEmpty();
  }

  @Override public boolean hasOwnAttributes() {
    return !attributeList.isEmpty();
  }

  @Override public boolean isAbstract() {
    return isAbstract;
  }

  @Override public boolean isDirectSubClassOf(SC anAttributedElementClass) {
    return directSuperClasses.contains(anAttributedElementClass);
  }

  @Override public boolean isDirectSuperClassOf(SC anAttributedElementClass) {
    return ((AttributedElementClassImpl<SC, IC>) anAttributedElementClass).directSuperClasses.contains(this);
  }

  @Override public boolean isInternal() {
    return internal;
  }

  void setInternal(Boolean b) {
    internal = b;
  }

  @Override public boolean isSubClassOf(SC anAttributedElementClass) {
    return getAllSuperClasses().contains(anAttributedElementClass);
  }

  @Override public boolean isSuperClassOf(SC anAttributedElementClass) {
    return anAttributedElementClass.getAllSuperClasses().contains(this);
  }

  @Override public boolean isSuperClassOfOrEquals(SC anAttributedElementClass) {
    return ((this == anAttributedElementClass) || (isSuperClassOf(anAttributedElementClass)));
  }

  @Override public void setAbstract(boolean isAbstract) {
    this.isAbstract = isAbstract;
  }

  protected boolean subclassContainsAttribute(String name) {
    for (SC subClass : getAllSubClasses()) {
      Attribute subclassAttr = subClass.getAttribute(name);
      if (subclassAttr != null) {
        return true;
      }
    }
    return false;
  }

  /**
	 * Called if the schema is finished, saves complete subclass, superclass and
	 * attribute list
	 */
  protected void finish() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    allSuperClasses = new HashSet<SC>();
=======
    assert allAttributes != null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java

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
    for (Attribute a : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    allAttributeList
=======
    allAttributes
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
    ) {
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

  protected void assertNotFinished() {
    if (finished) {
      throw new SchemaException("No changes allowed in a finished Schema.");
    }
  }

  @Override public int getAttributeIndex(String name) {
    Integer i;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    if (isFinished()) {
      i = attributeIndex.get(name);
    } else {
      int j = 0;
      for (Attribute a : getAttributeList()) {
        if (a.getName().equals(name)) {
          break;
        }
        ++j;
      }
      i = Integer.valueOf(j);
    }
=======
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
        }
        ++i;
      }
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/left.java
    if (i != null && i < allAttributeList.size()) {
      return i.intValue();
    } else {
      throw new NoSuchAttributeException(this.getSimpleName() + " doesn\'t contain an attribute " + name);
    }
=======
    throw new NoSuchAttributeException(getQualifiedName() + " doesn\'t contain an attribute \'" + name + "\'");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/AttributedElementClassImpl.java/right.java
  }
}