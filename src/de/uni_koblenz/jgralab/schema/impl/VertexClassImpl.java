package de.uni_koblenz.jgralab.schema.impl;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.IncidenceDirection;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

public final class VertexClassImpl extends GraphElementClassImpl<VertexClass, Vertex> implements VertexClass {
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
    VertexClass vc = schema.getDefaultGraphClass().createVertexClass(DEFAULTVERTEXCLASS_NAME);
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
  protected VertexClassImpl(String simpleName, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
  Package
=======
  PackageImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
   pkg, GraphClassImpl gc) {
    super(simpleName, pkg, gc, gc.vertexClassDag);
    parentPackage.addVertexClass(this);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.register()
=======
    graphClass.addVertexClass(this)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
  }

  @Override protected void register() {
    ((PackageImpl) this.parentPackage).addVertexClass(this);
    ((GraphClassImpl) this.graphClass).addVertexClass(this);
  }

  @Override public String getVariableName() {
    return "vc_" + this.getQualifiedName().replace('.', '_');
  }

  void addInIncidenceClass(IncidenceClass incClass) {
    if (incClass.getVertexClass() != this) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
      this.throwSchemaException();
=======
      throwSchemaException(incClass);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.checkDuplicateRolenames(incClass);
=======
    checkDuplicateRolenames(incClass);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.inIncidenceClasses.add(incClass)
=======
    inIncidenceClasses.add(incClass)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
  }

  void addOutIncidenceClass(IncidenceClass incClass) {
    if (incClass.getVertexClass() != this) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
      this.throwSchemaException();
=======
      throwSchemaException(incClass);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.checkDuplicateRolenames(incClass);
=======
    checkDuplicateRolenames(incClass);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.outIncidenceClasses.add(incClass)
=======
    outIncidenceClasses.add(incClass)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
  }

  private void checkDuplicateRolenames(IncidenceClass incClass) {
    String rolename = incClass.getOpposite().getRolename();
    if (rolename.isEmpty()) {
      return;
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.checkDuplicatedRolenameForACyclicIncidence(incClass);
=======
    checkDuplicatedRolenameForACyclicIncidence(incClass);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.checkDuplicatedRolenameForAllIncidences(incClass, this.getAllInIncidenceClasses());
=======
    checkDuplicatedRolenameForAllIncidences(incClass, getAllInIncidenceClasses());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.checkDuplicatedRolenameForAllIncidences(incClass, this.getAllOutIncidenceClasses());
=======
    checkDuplicatedRolenameForAllIncidences(incClass, getAllOutIncidenceClasses());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
  }

  private void checkDuplicatedRolenameForACyclicIncidence(IncidenceClass incClass) {
    String rolename = incClass.getOpposite().getRolename();
    VertexClass oppositeVertexClass = incClass.getOpposite().getVertexClass();
    boolean equalRolenames = incClass.getRolename().equals(rolename);
    boolean identicalClasses = this == oppositeVertexClass;
    if (equalRolenames && identicalClasses) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
      this.throwSchemaException(incClass);
=======
      throw new SchemaException("The rolename " + incClass.getRolename() + " may be not used at both ends of the reflexive edge class " + incClass.getEdgeClass().getQualifiedName());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    }
  }

  private void checkDuplicatedRolenameForAllIncidences(IncidenceClass incClass, Set<IncidenceClass> incidenceSet) {
    String rolename = incClass.getOpposite().getRolename();
    if (rolename.isEmpty()) {
      return;
    }
    for (IncidenceClass incidence : incidenceSet) {
      if (incidence == incClass) {
        continue;
      }
      if (incidence.getOpposite().getRolename().equals(rolename)) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
        this.throwSchemaExceptionRolenameUsedTwice(incidence);
=======
        throw new SchemaException("The rolename " + incidence.getOpposite().getRolename() + " is used twice at class " + getQualifiedName());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
      }
    }
  }

  private void throwSchemaExceptionRolenameUsedTwice(IncidenceClass incidence) {
    throw new SchemaException("The rolename " + incidence.getOpposite().getRolename() + " is used twice at class " + this.getQualifiedName());
  }

  private void throwSchemaException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
  IncidenceClass incClass
=======
  IncidenceClass ic
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
  ) {
    throw new SchemaException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    "The rolename " + incClass.getRolename() + " may be not used at both ends of the reflexive edge class " + incClass.getEdgeClass().getQualifiedName()
=======
    "Try to add IncidenceClass ending at \'" + ic.getVertexClass().getQualifiedName() + "\' to VertexClass \'" + getQualifiedName() + "\'.IncidenceClasses may be added only to VertexClasses they are connected to."
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    );
  }

  private void throwSchemaException() {
    throw new SchemaException("IncidenceClasses may be added only to vertices they are connected to");
  }

  @Override public void addSuperClass(VertexClass superClass) {
    assertNotFinished();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    if ((superClass == this) || (superClass == null)) {
      return;
    }
=======
    if (superClass == this) {
      return;
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.checkDuplicateRolenames(superClass);
=======
    checkDuplicateRolenames(superClass);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java

    super.addSuperClass(superClass);
    if (!superClass.equals(this.getSchema().getDefaultVertexClass())) {
      ((GraphClassImpl) this.getSchema().getGraphClass()).getVertexCsDag().createEdge(superClass, this);
    }
  }

  private void checkDuplicateRolenames(VertexClass superClass) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.checkDuplicatedRolenamesAgainstAllIncidences(superClass.getAllInIncidenceClasses());
=======
    checkDuplicatedRolenamesAgainstAllIncidences(superClass.getAllInIncidenceClasses());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.checkDuplicatedRolenamesAgainstAllIncidences(superClass.getAllOutIncidenceClasses());
=======
    checkDuplicatedRolenamesAgainstAllIncidences(superClass.getAllOutIncidenceClasses());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
  }

  private void checkDuplicatedRolenamesAgainstAllIncidences(Set<IncidenceClass> incidences) {
    for (IncidenceClass incidence : incidences) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
      this.checkDuplicateRolenames(incidence);
=======
      checkDuplicateRolenames(incidence);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    }
  }

  /**
	 * For a vertexclass A are all edgeclasses valid froms, which (1) run from A
	 * to a B or (2) run from a superclass of A to a B and whose end b at B is
	 * not redefined by A or a superclass of A
	 *
	 */
  @Override public Set<IncidenceClass> getValidFromFarIncidenceClasses() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    if (this.isFinished()) {
      return this.validFromFarIncidenceClasses;
    }
=======
    if (isFinished()) {
      return validFromFarIncidenceClasses;
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java

    Set<IncidenceClass> validFromInc = new HashSet<IncidenceClass>();
    for (IncidenceClass ic : this.getAllOutIncidenceClasses()) {
      IncidenceClass farInc = ic.getEdgeClass().getTo();
      validFromInc.add(farInc);
    }
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
    Set<IncidenceClass> temp = new HashSet<IncidenceClass>(validFromInc);
    for (IncidenceClass ic : temp) {
      validFromInc.removeAll(ic.getRedefinedIncidenceClasses());
    }
    return validFromInc;
  }

  @Override public Set<IncidenceClass> getValidToFarIncidenceClasses() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    if (this.isFinished()) {
      return this.validToFarIncidenceClasses;
    }
=======
    if (isFinished()) {
      return validToFarIncidenceClasses;
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java

    Set<IncidenceClass> validToInc = new HashSet<IncidenceClass>();
    for (IncidenceClass ic : this.getAllInIncidenceClasses()) {
      IncidenceClass farInc = ic.getEdgeClass().getFrom();
      validToInc.add(farInc);
    }
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
    Set<IncidenceClass> temp = new HashSet<IncidenceClass>(validToInc);
    for (IncidenceClass ic : temp) {
      validToInc.removeAll(ic.getRedefinedIncidenceClasses());
    }
    return validToInc;
  }

  @Override public Set<EdgeClass> getValidFromEdgeClasses() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    if (this.isFinished()) {
      return this.validFromEdgeClasses;
    }
=======
    if (isFinished()) {
      return validFromEdgeClasses;
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java

    Set<EdgeClass> validFrom = new HashSet<EdgeClass>();
    for (IncidenceClass ic : this.getValidFromFarIncidenceClasses()) {
      if (!ic.getEdgeClass().isInternal()) {
        validFrom.add(ic.getEdgeClass());
      }
    }
    return validFrom;
  }

  @Override public Set<EdgeClass> getValidToEdgeClasses() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    if (this.isFinished()) {
      return this.validToEdgeClasses;
    }
=======
    if (isFinished()) {
      return validToEdgeClasses;
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java

    Set<EdgeClass> validTo = new HashSet<EdgeClass>();
    for (IncidenceClass ic : this.getValidToFarIncidenceClasses()) {
      if (!ic.getEdgeClass().isInternal()) {
        validTo.add(ic.getEdgeClass());
      }
    }
    return validTo;
  }

  public Set<IncidenceClass> getOwnInIncidenceClasses() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.inIncidenceClasses
=======
    inIncidenceClasses
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
  }

  public Set<IncidenceClass> getOwnOutIncidenceClasses() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.outIncidenceClasses
=======
    outIncidenceClasses
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
  }

  @Override public Set<IncidenceClass> getAllInIncidenceClasses() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    if (this.isFinished()) {
      return this.allInIncidenceClasses;
    }
=======
    if (isFinished()) {
      return allInIncidenceClasses;
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java

    Set<IncidenceClass> incidenceClasses = new HashSet<IncidenceClass>();
    incidenceClasses.addAll(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.inIncidenceClasses
=======
    inIncidenceClasses
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    );
    for (VertexClass vc : this.getDirectSuperClasses()) {
      incidenceClasses.addAll(vc.getAllInIncidenceClasses());
    }
    return incidenceClasses;
  }

  @Override public Set<IncidenceClass> getAllOutIncidenceClasses() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    if (this.isFinished()) {
      return this.allOutIncidenceClasses;
    }
=======
    if (isFinished()) {
      return allOutIncidenceClasses;
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java

    Set<IncidenceClass> incidenceClasses = new HashSet<IncidenceClass>();
    incidenceClasses.addAll(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.outIncidenceClasses
=======
    outIncidenceClasses
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    );
    for (VertexClass vc : this.getDirectSuperClasses()) {
      incidenceClasses.addAll(vc.getAllOutIncidenceClasses());
    }
    return incidenceClasses;
  }

  @Override public Set<IncidenceClass> getOwnAndInheritedFarIncidenceClasses() {
    Set<IncidenceClass> result = new HashSet<IncidenceClass>();
    for (IncidenceClass ic : this.getAllInIncidenceClasses()) {
      result.add(ic.getEdgeClass().getFrom());
      for (IncidenceClass sup : ic.getSubsettedIncidenceClasses()) {
        result.add(sup.getEdgeClass().getFrom());
      }
    }
    for (IncidenceClass ic : this.getAllOutIncidenceClasses()) {
      result.add(ic.getEdgeClass().getTo());
      for (IncidenceClass sup : ic.getSubsettedIncidenceClasses()) {
        result.add(sup.getEdgeClass().getTo());
      }
    }
    return result;
  }

  @Override public Set<EdgeClass> getConnectedEdgeClasses() {
    Set<EdgeClass> result = new HashSet<EdgeClass>();
    for (IncidenceClass ic : this.getAllInIncidenceClasses()) {
      result.add(ic.getEdgeClass());
    }
    for (IncidenceClass ic : this.getAllOutIncidenceClasses()) {
      result.add(ic.getEdgeClass());
    }
    return result;
  }

  @Override public Set<EdgeClass> getOwnConnectedEdgeClasses() {
    Set<EdgeClass> result = new HashSet<EdgeClass>();
    for (IncidenceClass ic : this.getOwnInIncidenceClasses()) {
      result.add(ic.getEdgeClass());
    }
    for (IncidenceClass ic : this.getOwnOutIncidenceClasses()) {
      result.add(ic.getEdgeClass());
    }
    return result;
  }

  @Override protected void finish() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.allInIncidenceClasses = new HashSet<IncidenceClass>()
=======
    allInIncidenceClasses = new HashSet<IncidenceClass>()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.allInIncidenceClasses.addAll(this.inIncidenceClasses)
=======
    allInIncidenceClasses.addAll(inIncidenceClasses)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.allOutIncidenceClasses = new HashSet<IncidenceClass>()
=======
    allOutIncidenceClasses = new HashSet<IncidenceClass>()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.allOutIncidenceClasses.addAll(this.outIncidenceClasses)
=======
    allOutIncidenceClasses.addAll(outIncidenceClasses)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
    for (VertexClass vc : this.getDirectSuperClasses()) {
      this.allInIncidenceClasses.addAll(vc.getAllInIncidenceClasses());
      this.allOutIncidenceClasses.addAll(vc.getAllOutIncidenceClasses());
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.allInIncidenceClasses = Collections.unmodifiableSet(this.allInIncidenceClasses)
=======
    allInIncidenceClasses = Collections.unmodifiableSet(allInIncidenceClasses)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.allOutIncidenceClasses = Collections.unmodifiableSet(this.allOutIncidenceClasses)
=======
    allOutIncidenceClasses = Collections.unmodifiableSet(allOutIncidenceClasses)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.validFromFarIncidenceClasses = Collections.unmodifiableSet(this.getValidFromFarIncidenceClasses())
=======
    validFromFarIncidenceClasses = Collections.unmodifiableSet(getValidFromFarIncidenceClasses())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.validToFarIncidenceClasses = Collections.unmodifiableSet(this.getValidToFarIncidenceClasses())
=======
    validToFarIncidenceClasses = Collections.unmodifiableSet(getValidToFarIncidenceClasses())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.validFromEdgeClasses = Collections.unmodifiableSet(this.getValidFromEdgeClasses())
=======
    validFromEdgeClasses = Collections.unmodifiableSet(getValidFromEdgeClasses())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.validToEdgeClasses = Collections.unmodifiableSet(this.getValidToEdgeClasses())
=======
    validToEdgeClasses = Collections.unmodifiableSet(getValidToEdgeClasses())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.farRoleNameToEdgeClass = new HashMap<String, DirectedSchemaEdgeClass>()
=======
    farRoleNameToEdgeClass = new HashMap<String, DirectedSchemaEdgeClass>()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
    for (IncidenceClass ic : this.getOwnAndInheritedFarIncidenceClasses()) {
      this.farRoleNameToEdgeClass.put(ic.getRolename(), this.getDirectedEdgeClassForFarEndRole(ic.getRolename()));
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.farRoleNameToEdgeClass = Collections.unmodifiableMap(this.farRoleNameToEdgeClass)
=======
    farRoleNameToEdgeClass = Collections.unmodifiableMap(farRoleNameToEdgeClass)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.inIncidenceClasses = Collections.unmodifiableSet(this.inIncidenceClasses)
=======
    inIncidenceClasses = Collections.unmodifiableSet(inIncidenceClasses)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.outIncidenceClasses = Collections.unmodifiableSet(this.outIncidenceClasses)
=======
    outIncidenceClasses = Collections.unmodifiableSet(outIncidenceClasses)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
    for (IncidenceClass ic : this.inIncidenceClasses) {
      ((IncidenceClassImpl) ic).finish();
    }
    for (IncidenceClass ic : this.outIncidenceClasses) {
      ((IncidenceClassImpl) ic).finish();
    }
    super.finish();
  }

  @Override public boolean isValidFromFor(EdgeClass ec) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.getValidFromEdgeClasses().contains(ec)
=======
    getValidFromEdgeClasses().contains(ec)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
  }

  @Override public boolean isValidToFor(EdgeClass ec) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    this.getValidToEdgeClasses().contains(ec)
=======
    getValidToEdgeClasses().contains(ec)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
  }

  @Override protected void reopen() {
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

  @Override public DirectedSchemaEdgeClass getDirectedEdgeClassForFarEndRole(String roleName) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    if (this.isFinished()) {
      return this.farRoleNameToEdgeClass.get(roleName);
    }
=======
    if (isFinished()) {
      return farRoleNameToEdgeClass.get(roleName);
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java

    for (IncidenceClass ic : this.getOwnAndInheritedFarIncidenceClasses()) {
      if (roleName.equals(ic.getRolename())) {
        EdgeClass ec = ic.getEdgeClass();
        return new DirectedSchemaEdgeClass(ec, (this.getValidFromEdgeClasses().contains(ec) ? EdgeDirection.OUT : EdgeDirection.IN));
      }
    }
    return null;
  }
}