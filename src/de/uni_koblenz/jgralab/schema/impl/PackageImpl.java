package de.uni_koblenz.jgralab.schema.impl;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.pcollections.ArrayPSet;
import org.pcollections.PSet;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.NamedElement;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

public class PackageImpl extends NamedElementImpl implements Package {
  final Map<String, Domain> domains = new TreeMap<String, Domain>();

  final Map<String, EdgeClass> edgeClasses = new TreeMap<String, EdgeClass>();

  private final Map<String, Package> subPackages = new TreeMap<String, Package>();

  final Map<String, VertexClass> vertexClasses = new TreeMap<String, VertexClass>();

  /**
	 * Creates a new <code>DefaultPackage</code> in the given Schema.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>p = PackageImpl.createDefaultPackage(schema);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none<br/>
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> p is the newly created <code>DefaultPackage</code>
	 * for this schema
	 * </p>
	 * 
	 * @param schema
	 *            the schema containing the new <code>DefaultPackage</code>
	 * @return the newly created <code>DefaultPackage</code> for the given
	 *         schema
	 * @throws SchemaException
	 *             if the <code>DefaultPackage</code> already exists in the
	 *             given schema
	 */
  static PackageImpl createDefaultPackage(Schema schema) {
    assert schema.getDefaultPackage() == null : "DefaultPackage already created!";
    return new PackageImpl((SchemaImpl) schema);
  }

  /**
	 * Constructor for the default package
	 * 
	 * @param schema
	 */
  private PackageImpl(SchemaImpl schema) {
    this(Package.DEFAULTPACKAGE_NAME, null, schema);
  }

  protected PackageImpl(String simpleName, PackageImpl parentPackage, SchemaImpl schema) {
    super(simpleName, parentPackage, schema);
    if (parentPackage != null) {
      parentPackage.addSubPackage(this);
    }
    schema.addPackage(this);
  }

  void addDomain(Domain dom) {
    assert dom.getPackage() == this : "The domain does not belong into this package (" + getQualifiedName() + "). It belongs here: " + dom.getPackageName();
    assert !domains.containsKey(dom.getSimpleName()) && !domains.containsValue(dom) : "This package (" + getQualifiedName() + ") already contains a domain called " + dom.getSimpleName();
    domains.put(dom.getSimpleName(), dom);
  }

  /**
	 * Adds the EdgeClass <code>ec</code> to this Package.
	 * 
	 * @param ec
	 *            an EdgeClass
	 */
  void addEdgeClass(EdgeClass ec) {
    assert ec.getPackage() == this : "The edge class \'" + ec.getQualifiedName() + "\' does not belong into the package \'" + getQualifiedName() + "\' but into \'" + ec.getPackageName() + "\'.";
    assert !edgeClasses.containsKey(ec.getSimpleName()) && !edgeClasses.containsValue(ec) : "This package (" + getQualifiedName() + ") already contains an edge class called " + ec.getSimpleName();
    if (!(isDefaultPackage() && ec.getSimpleName().equals(EdgeClass.DEFAULTEDGECLASS_NAME))) {
      edgeClasses.put(ec.getSimpleName(), ec);
    }
  }

  /**
	 * Adds the subpackage <code>subPkg</code> to this Package.
	 * 
	 * @param subPkg
	 *            a subpackage
	 */
  void addSubPackage(Package subPkg) {
    assert subPkg.getPackage() == this : "The subpackage does not belong into the package \'" + getQualifiedName() + "\' but into \'" + subPkg.getPackageName() + "\'.";
    assert !subPackages.containsKey(subPkg.getSimpleName()) : "The package \'" + getQualifiedName() + "\' already contains a subpackage called \'" + subPkg.getSimpleName() + "\'.";
    subPackages.put(subPkg.getSimpleName(), subPkg);
  }

  /**
	 * Adds the VertexClass <code>vc</code> to this Package.
	 * 
	 * @param vc
	 *            a VertexClass
	 */
  void addVertexClass(VertexClass vc) {
    assert vc.getPackage() == this : "The vertex class \'" + vc.getQualifiedName() + "\' does not belong into the package \'" + getQualifiedName() + "\' but into \'" + vc.getPackageName() + "\'";
    assert !vertexClasses.containsKey(vc.getSimpleName()) && !vertexClasses.containsValue(vc) : "This package (" + getQualifiedName() + ") already contains a vertex class called \"" + vc.getSimpleName() + "\"";
    if (!(isDefaultPackage() && vc.getSimpleName().equals(VertexClass.DEFAULTVERTEXCLASS_NAME))) {
      vertexClasses.put(vc.getSimpleName(), vc);
    }
  }

  @Override public boolean containsNamedElement(String sn) {
    return domains.containsKey(sn) || edgeClasses.containsKey(sn) || vertexClasses.containsKey(sn) || subPackages.containsKey(sn);
  }

  @Override public PSet<Domain> getDomains() {
    return ArrayPSet.<Domain>empty().plusAll(domains.values());
  }

  @Override public PSet<EdgeClass> getEdgeClasses() {
    return ArrayPSet.<EdgeClass>empty().plusAll(edgeClasses.values());
  }

  @Override public Schema getSchema() {
    return schema;
  }

  @Override public Package getSubPackage(String sn) {
    return subPackages.get(sn);
  }

  @Override public PSet<Package> getSubPackages() {
    return ArrayPSet.<Package>empty().plusAll(subPackages.values());
  }

  @Override public PSet<VertexClass> getVertexClasses() {
    return ArrayPSet.<VertexClass>empty().plusAll(vertexClasses.values());
  }

  @Override public boolean isDefaultPackage() {
    return this == schema.getDefaultPackage();
  }

  @Override public String toString() {
    return "package " + qualifiedName;
  }

  @Override public String getUniqueName() {
    return qualifiedName;
  }

  @Override public void setQualifiedName(String newQName) {
    if (schema.getDefaultPackage() == this) {
      throw new SchemaException("Cannot rename the default package.");
    }
    if (qualifiedName.equals(newQName)) {
      return;
    }
    if (schema.knows(newQName)) {
      throw new SchemaException(newQName + " is already known to the schema.");
    }
    String[] ps = SchemaImpl.splitQualifiedName(newQName);
    String newPackageName = ps[0];
    String newSimpleName = ps[1];
    if (!NamedElementImpl.PACKAGE_NAME_PATTERN.matcher(newSimpleName).matches()) {
      throw new SchemaException("Invalid package name \'" + newSimpleName + "\'.");
    }
    unregister();
    qualifiedName = newQName;
    simpleName = newSimpleName;
    parentPackage = schema.createPackageWithParents(newPackageName);
    List<NamedElement> l = new LinkedList<NamedElement>();
    l.addAll(vertexClasses.values());
    l.addAll(edgeClasses.values());
    l.addAll(subPackages.values());
    for (NamedElement ne : l) {
      ne.setQualifiedName(qualifiedName + "." + ne.getSimpleName());
    }
    register();
  }

  @Override protected final void register() {
    super.register();
    parentPackage.subPackages.put(simpleName, this);
  }

  @Override protected final void unregister() {
    super.unregister();
    parentPackage.subPackages.remove(simpleName);
  }

  @Override public void delete() {
    schema.assertNotFinished();
    if (isDefaultPackage()) {
      throw new SchemaException("The default package cannot be deleted.");
    }
    if ((domains.size() != 0) || (vertexClasses.size() != 0) || (edgeClasses.size() != 0)) {
      throw new SchemaException("Only empty packages can be deleted!");
    }
    parentPackage.subPackages.remove(simpleName);
    schema.packages.remove(qualifiedName);
    schema.namedElements.remove(qualifiedName);
  }

  @Override public EdgeClass getEdgeClass(String simpleName) {
    return edgeClasses.get(simpleName);
  }

  @Override public Domain getDomain(String simpleName) {
    return domains.get(simpleName);
  }

  @Override public VertexClass getVertexClass(String simpleName) {
    return vertexClasses.get(simpleName);
  }
}