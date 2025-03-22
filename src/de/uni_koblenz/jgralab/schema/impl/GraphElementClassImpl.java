package de.uni_koblenz.jgralab.schema.impl;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.pcollections.ArrayPVector;
import org.pcollections.PSet;
import org.pcollections.PVector;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

public abstract class GraphElementClassImpl<SC extends GraphElementClass<SC, IC>, IC extends GraphElement<SC, IC>> extends AttributedElementClassImpl<SC, IC> implements GraphElementClass<SC, IC> {
  protected final 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/left.java
  GraphClass
=======
  GraphClassImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/right.java
   graphClass;

  /**
	 * The list of attributes. Only the own attributes of this class are stored
	 * here, no inherited attributes.
	 */
  protected PVector<Attribute> ownAttributes;

  /**
	 * The subclasses of this class - only set if the schema is finished. The
	 * HashSet is used to speed up isInstance test.
	 */
  protected PSet<SC> allSubClasses;

  protected HashSet<SC> allSubClassesHash;

  /**
	 * The superclasses of this class - only set if the schema is finished. The
	 * HashSet is used to speed up isInstance test.
	 */
  protected PSet<SC> allSuperClasses;

  protected HashSet<SC> allSuperClassesHash;

  /**
	 * A {@link DirectedAcyclicGraph} representing the generalization hierarchy.
	 * Edges direction is from superclass to subclass.
	 */
  protected final DirectedAcyclicGraph<GraphElementClass<SC, IC>> subclassDag;

  /**
	 * delegates its constructor to the generalized class
	 * 
	 * @param qn
	 *            the unique identifier of the element in the schema
	 */
  @SuppressWarnings(value = { "unchecked" }) protected GraphElementClassImpl(String simpleName, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/left.java
  Package
=======
  PackageImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/right.java
   pkg, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/left.java
  GraphClass
=======
  GraphClassImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/right.java
   graphClass, DirectedAcyclicGraph<SC> dag) {
    super(simpleName, pkg, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/left.java
    graphClass.getSchema()
=======
    graphClass.schema
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/right.java
    );
    ownAttributes = ArrayPVector.empty();
    subclassDag = (DirectedAcyclicGraph<GraphElementClass<SC, IC>>) dag;
    subclassDag.createNode(this);
    this.graphClass = graphClass;
  }

  @Override public void addAttribute(Attribute anAttribute) {
    assertNotFinished();
    if (subclassContainsAttribute(anAttribute.getName())) {
      throw new SchemaException("Duplicate attribute \'" + anAttribute.getName() + "\' in AttributedElementClass \'" + getQualifiedName() + "\'. A derived AttributedElementClass already contains this Attribute.");
    }
    super.addAttribute(anAttribute);
    TreeSet<Attribute> s = new TreeSet<Attribute>(ownAttributes);
    s.add(anAttribute);
    ownAttributes = ArrayPVector.<Attribute>empty().plusAll(s);
  }

  @Override public GraphClass getGraphClass() {
    return graphClass;
  }

  /**
	 * adds a superClass to this class
	 * 
	 * @param superClass
	 *            the class to add as superclass
	 */
  protected void addSuperClass(SC superClass) {
    assertNotFinished();
    if (superClass == this) {
      return;
    }
    subclassDag.createEdge(superClass, this);
    for (Attribute a : superClass.getAttributeList()) {
      if (getOwnAttribute(a.getName()) != null) {
        throw new SchemaException("Cannot add " + superClass.getQualifiedName() + " as superclass of " + getQualifiedName() + ". Cause: Attribute " + a.getName() + " is declared in both classes");
      }
    }
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public PSet<SC> getDirectSubClasses() {
    return (PSet<SC>) subclassDag.getDirectSucccessors(this);
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public PSet<SC> getDirectSuperClasses() {
    return (PSet<SC>) subclassDag.getDirectPredecessors(this);
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public Set<SC> getAllSubClasses() {
    if (finished) {
      return allSubClasses;
    }
    return (Set<SC>) subclassDag.getAllSuccessorsInTopologicalOrder(this);
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public Set<SC> getAllSuperClasses() {
    if (finished) {
      return allSuperClasses;
    }
    return (Set<SC>) subclassDag.getAllPredecessorsInTopologicalOrder(this);
  }

  @Override public final boolean isSubClassOf(SC anAttributedElementClass) {
    if (finished) {
      return allSuperClassesHash.contains(anAttributedElementClass);
    }
    return getAllSuperClasses().contains(anAttributedElementClass);
  }

  @Override public final boolean isSuperClassOf(SC anAttributedElementClass) {
    if (finished) {
      return allSubClassesHash.contains(anAttributedElementClass);
    }
    return getAllSubClasses().contains(anAttributedElementClass);
  }

  private boolean subclassContainsAttribute(String name) {
    for (SC subClass : getAllSubClasses()) {
      if (subClass.getAttribute(name) != null) {
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings(value = { "unchecked" }) @Override protected void finish() {
    allSuperClasses = (PSet<SC>) subclassDag.getAllPredecessorsInTopologicalOrder(this);
    allSuperClassesHash = new HashSet<SC>(allSuperClasses);
    allSubClasses = (PSet<SC>) subclassDag.getAllSuccessorsInTopologicalOrder(this);
    allSubClassesHash = new HashSet<SC>(allSubClasses);
    TreeSet<Attribute> s = new TreeSet<Attribute>(ownAttributes);
    for (AttributedElementClass<SC, IC> superClass : subclassDag.getDirectPredecessors(this)) {
      s.addAll(superClass.getAttributeList());
    }
    allAttributes = ArrayPVector.<Attribute>empty().plusAll(s);
    super.finish();
  }

  @Override public int getAttributeCount() {
    if (finished) {
      return allAttributes.size();
    }
    int attrCount = getOwnAttributeCount();
    for (AttributedElementClass<SC, IC> superClass : subclassDag.getDirectPredecessors(this)) {
      attrCount += superClass.getAttributeCount();
    }
    return attrCount;
  }

  @Override public List<Attribute> getAttributeList() {
    if (finished) {
      return allAttributes;
    }
    TreeSet<Attribute> attrList = new TreeSet<Attribute>();
    attrList.addAll(ownAttributes);
    for (AttributedElementClass<SC, IC> superClass : subclassDag.getDirectPredecessors(this)) {
      attrList.addAll(superClass.getAttributeList());
    }
    return ArrayPVector.<Attribute>empty().plusAll(attrList);
  }

  @Override public Attribute getAttribute(String name) {
    if (finished) {
      return super.getAttribute(name);
    }
    Attribute ownAttr = getOwnAttribute(name);
    if (ownAttr != null) {
      return ownAttr;
    }
    for (AttributedElementClass<SC, IC> superClass : subclassDag.getDirectPredecessors(this)) {
      Attribute inheritedAttr = superClass.getAttribute(name);
      if (inheritedAttr != null) {
        return inheritedAttr;
      }
    }
    return null;
  }

  @Override public Attribute getOwnAttribute(String name) {
    for (Attribute a : ownAttributes) {
      if (a.getName().equals(name)) {
        return a;
      }
    }
    return null;
  }

  @Override public int getOwnAttributeCount() {
    return ownAttributes.size();
  }

  @Override public List<Attribute> getOwnAttributeList() {
    return ownAttributes;
  }

  @Override public boolean hasOwnAttributes() {
    return !ownAttributes.isEmpty();
  }

  public String getDescriptionString() {
    StringBuilder output = new StringBuilder(this.getClass().getSimpleName() + " \'" + getQualifiedName() + "\'");
    if (isAbstract()) {
      output.append(" (abstract)");
    }
    output.append(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/left.java
    ": \n"
=======
    ":\n"
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/right.java
    );
    output.append(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/left.java
    "subClasses of \'"
=======
    "Subclasses of \'"
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/right.java
     + getQualifiedName() + "\': ");
    for (SC aec : getAllSubClasses()) {
      output.append("\'" + aec.getQualifiedName() + "\' ");
    }
    output.append(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/left.java
    "\nsuperClasses of \'"
=======
    "\nSuperclasses of \'"
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/right.java
     + getQualifiedName() + "\': ");
    for (SC aec : getAllSuperClasses()) {
      output.append("\'" + aec.getQualifiedName() + "\' ");
    }
    output.append(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/left.java
    "\ndirectSuperClasses of \'"
=======
    "\nDirect Superclasses of \'"
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/right.java
     + getQualifiedName() + "\': ");
    for (SC aec : getDirectSuperClasses()) {
      output.append("\'" + aec.getQualifiedName() + "\' ");
    }
    output.append(attributesToString());
    output.append("\n");
    return output.toString();
  }
}