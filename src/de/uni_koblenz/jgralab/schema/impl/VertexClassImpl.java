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

public class VertexClassImpl extends GraphElementClassImpl<VertexClass, Vertex> implements VertexClass {
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
  protected VertexClassImpl(String simpleName, Package pkg, GraphClass aGraphClass) {
    super(simpleName, pkg, aGraphClass);
    this.register();
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
      this.throwSchemaException();
    }
    this.checkDuplicateRolenames(incClass);
    this.inIncidenceClasses.add(incClass);
  }

  void addOutIncidenceClass(IncidenceClass incClass) {
    if (incClass.getVertexClass() != this) {
      this.throwSchemaException();
    }
    this.checkDuplicateRolenames(incClass);
    this.outIncidenceClasses.add(incClass);
  }

  private void checkDuplicateRolenames(IncidenceClass incClass) {
    String rolename = incClass.getOpposite().getRolename();
    if (rolename.isEmpty()) {
      return;
    }
    this.checkDuplicatedRolenameForACyclicIncidence(incClass);
    this.checkDuplicatedRolenameForAllIncidences(incClass, this.getAllInIncidenceClasses());
    this.checkDuplicatedRolenameForAllIncidences(incClass, this.getAllOutIncidenceClasses());
  }

  private void checkDuplicatedRolenameForACyclicIncidence(IncidenceClass incClass) {
    String rolename = incClass.getOpposite().getRolename();
    VertexClass oppositeVertexClass = incClass.getOpposite().getVertexClass();
    boolean equalRolenames = incClass.getRolename().equals(rolename);
    boolean identicalClasses = this == oppositeVertexClass;
    if (equalRolenames && identicalClasses) {
      this.throwSchemaException(incClass);
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
        this.throwSchemaExceptionRolenameUsedTwice(incidence);
      }
    }
  }

  private void throwSchemaExceptionRolenameUsedTwice(IncidenceClass incidence) {
    throw new SchemaException("The rolename " + incidence.getOpposite().getRolename() + " is used twice at class " + this.getQualifiedName());
  }

  private void throwSchemaException(IncidenceClass incClass) {
    throw new SchemaException("The rolename " + incClass.getRolename() + " may be not used at both ends of the reflexive edge class " + incClass.getEdgeClass().getQualifiedName());
  }

  private void throwSchemaException() {
    throw new SchemaException("IncidenceClasses may be added only to vertices they are connected to");
  }

  @Override public void addSuperClass(VertexClass superClass) {
    if ((superClass == this) || (superClass == null)) {
      return;
    }
    this.checkDuplicateRolenames(superClass);
    super.addSuperClass(superClass);
    if (!superClass.equals(getSchema().getDefaultVertexClass())) {
      ((GraphClassImpl) getSchema().getGraphClass()).getVertexCsDag().createEdge(superClass, this);
    }
  }

  private void checkDuplicateRolenames(VertexClass superClass) {
    this.checkDuplicatedRolenamesAgainstAllIncidences(superClass.getAllInIncidenceClasses());
    this.checkDuplicatedRolenamesAgainstAllIncidences(superClass.getAllOutIncidenceClasses());
  }

  private void checkDuplicatedRolenamesAgainstAllIncidences(Set<IncidenceClass> incidences) {
    for (IncidenceClass incidence : incidences) {
      this.checkDuplicateRolenames(incidence);
    }
  }

  /**
	 * For a vertexclass A are all edgeclasses valid froms, which (1) run from A
	 * to a B or (2) run from a superclass of A to a B and whose end b at B is
	 * not redefined by A or a superclass of A
	 *
	 */
  @Override public Set<IncidenceClass> getValidFromFarIncidenceClasses() {
    if (this.isFinished()) {
      return validFromFarIncidenceClasses;
    }
    Set<IncidenceClass> validFromInc = new HashSet<IncidenceClass>();
    for (IncidenceClass ic : this.getAllOutIncidenceClasses()) {
      IncidenceClass farInc = ic.getEdgeClass().getTo();
      validFromInc.add(farInc);
    }
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
    Set<IncidenceClass> temp = new HashSet<IncidenceClass>(validFromInc);
    for (IncidenceClass ic : temp) {
      validFromInc.removeAll(ic.getRedefinedIncidenceClasses());
    }
    return validFromInc;
  }

  @Override public Set<IncidenceClass> getValidToFarIncidenceClasses() {
    if (this.isFinished()) {
      return validToFarIncidenceClasses;
    }
    Set<IncidenceClass> validToInc = new HashSet<IncidenceClass>();
    for (IncidenceClass ic : this.getAllInIncidenceClasses()) {
      IncidenceClass farInc = ic.getEdgeClass().getFrom();
      validToInc.add(farInc);
    }
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
    Set<IncidenceClass> temp = new HashSet<IncidenceClass>(validToInc);
    for (IncidenceClass ic : temp) {
      validToInc.removeAll(ic.getRedefinedIncidenceClasses());
    }
    return validToInc;
  }

  @Override public Set<EdgeClass> getValidFromEdgeClasses() {
    if (this.isFinished()) {
      return validFromEdgeClasses;
    }
    Set<EdgeClass> validFrom = new HashSet<EdgeClass>();
    for (IncidenceClass ic : this.getValidFromFarIncidenceClasses()) {
      if (!ic.getEdgeClass().isInternal()) {
        validFrom.add(ic.getEdgeClass());
      }
    }
    return validFrom;
  }

  @Override public Set<EdgeClass> getValidToEdgeClasses() {
    if (this.isFinished()) {
      return validToEdgeClasses;
    }
    Set<EdgeClass> validTo = new HashSet<EdgeClass>();
    for (IncidenceClass ic : this.getValidToFarIncidenceClasses()) {
      if (!ic.getEdgeClass().isInternal()) {
        validTo.add(ic.getEdgeClass());
      }
    }
    return validTo;
  }

  public Set<IncidenceClass> getOwnInIncidenceClasses() {
    return this.inIncidenceClasses;
  }

  public Set<IncidenceClass> getOwnOutIncidenceClasses() {
    return this.outIncidenceClasses;
  }

  @Override public Set<IncidenceClass> getAllInIncidenceClasses() {
    if (this.isFinished()) {
      return allInIncidenceClasses;
    }
    Set<IncidenceClass> incidenceClasses = new HashSet<IncidenceClass>();
    incidenceClasses.addAll(this.inIncidenceClasses);
    for (VertexClass vc : getDirectSuperClasses()) {
      incidenceClasses.addAll(vc.getAllInIncidenceClasses());
    }
    return incidenceClasses;
  }

  @Override public Set<IncidenceClass> getAllOutIncidenceClasses() {
    if (this.isFinished()) {
      return allOutIncidenceClasses;
    }
    Set<IncidenceClass> incidenceClasses = new HashSet<IncidenceClass>();
    incidenceClasses.addAll(this.outIncidenceClasses);
    for (VertexClass vc : getDirectSuperClasses()) {
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
    allInIncidenceClasses = new HashSet<IncidenceClass>();
    allInIncidenceClasses.
<<<<<<< /usr/src/app/output/jgralab/jgralab/38c4420a04fe3eaee476aa33fe789c1d5d849eeb/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    addAll(inIncidenceClasses)
=======
    allInIncidenceClasses.addAll(this.inIncidenceClasses)
>>>>>>> /usr/src/app/output/jgralab/jgralab/38c4420a04fe3eaee476aa33fe789c1d5d849eeb/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
    allOutIncidenceClasses = new HashSet<IncidenceClass>();
    allOutIncidenceClasses.
<<<<<<< /usr/src/app/output/jgralab/jgralab/38c4420a04fe3eaee476aa33fe789c1d5d849eeb/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/left.java
    addAll(outIncidenceClasses)
=======
    allOutIncidenceClasses.addAll(this.outIncidenceClasses)
>>>>>>> /usr/src/app/output/jgralab/jgralab/38c4420a04fe3eaee476aa33fe789c1d5d849eeb/src/de/uni_koblenz/jgralab/schema/impl/VertexClassImpl.java/right.java
    ;
    for (VertexClass vc : getDirectSuperClasses()) {
      allInIncidenceClasses.addAll(vc.getAllInIncidenceClasses());
      allOutIncidenceClasses.addAll(vc.getAllOutIncidenceClasses());
    }
    allInIncidenceClasses = Collections.unmodifiableSet(allInIncidenceClasses);
    allOutIncidenceClasses = Collections.unmodifiableSet(allOutIncidenceClasses);
    validFromFarIncidenceClasses = Collections.unmodifiableSet(getValidFromFarIncidenceClasses());
    validToFarIncidenceClasses = Collections.unmodifiableSet(getValidToFarIncidenceClasses());
    validFromEdgeClasses = Collections.unmodifiableSet(getValidFromEdgeClasses());
    validToEdgeClasses = Collections.unmodifiableSet(getValidToEdgeClasses());
    farRoleNameToEdgeClass = new HashMap<String, DirectedSchemaEdgeClass>();
    for (IncidenceClass ic : getOwnAndInheritedFarIncidenceClasses()) {
      farRoleNameToEdgeClass.put(ic.getRolename(), getDirectedEdgeClassForFarEndRole(ic.getRolename()));
    }
    farRoleNameToEdgeClass = Collections.unmodifiableMap(farRoleNameToEdgeClass);
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

  @Override public boolean isValidFromFor(EdgeClass ec) {
    return this.getValidFromEdgeClasses().contains(ec);
  }

  @Override public boolean isValidToFor(EdgeClass ec) {
    return this.getValidToEdgeClasses().contains(ec);
  }

  @Override protected void reopen() {
    allInIncidenceClasses = null;
    allOutIncidenceClasses = null;
    validFromFarIncidenceClasses = null;
    validToFarIncidenceClasses = null;
    validFromEdgeClasses = null;
    validToEdgeClasses = null;
    inIncidenceClasses = new HashSet<IncidenceClass>(inIncidenceClasses);
    outIncidenceClasses = new HashSet<IncidenceClass>(outIncidenceClasses);
    farRoleNameToEdgeClass = null;
    for (IncidenceClass ic : inIncidenceClasses) {
      ((IncidenceClassImpl) ic).reopen();
    }
    for (IncidenceClass ic : outIncidenceClasses) {
      ((IncidenceClassImpl) ic).reopen();
    }
    super.reopen();
  }

  @Override public DirectedSchemaEdgeClass getDirectedEdgeClassForFarEndRole(String roleName) {
    if (isFinished()) {
      return farRoleNameToEdgeClass.get(roleName);
    }
    for (IncidenceClass ic : getOwnAndInheritedFarIncidenceClasses()) {
      if (roleName.equals(ic.getRolename())) {
        EdgeClass ec = ic.getEdgeClass();
        return new DirectedSchemaEdgeClass(ec, (getValidFromEdgeClasses().contains(ec) ? EdgeDirection.OUT : EdgeDirection.IN));
      }
    }
    return null;
  }
}