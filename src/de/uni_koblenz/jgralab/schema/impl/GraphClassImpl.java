package de.uni_koblenz.jgralab.schema.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.InheritanceException;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

public final class GraphClassImpl extends AttributedElementClassImpl<GraphClass, Graph> implements GraphClass {
  private Map<String, EdgeClass> edgeClasses = new HashMap<String, EdgeClass>();

  private Map<String, GraphElementClass<?, ?>> graphElementClasses = new HashMap<String, GraphElementClass<?, ?>>();

  private Map<String, VertexClass> vertexClasses = new HashMap<String, VertexClass>();

  DirectedAcyclicGraph<EdgeClass> 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
  edgeCsDag = new DirectedAcyclicGraph<EdgeClass>()
=======
  edgeClassDag = new DirectedAcyclicGraph<EdgeClass>(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
  ;

  DirectedAcyclicGraph<VertexClass> 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
  vertexCsDag = new DirectedAcyclicGraph<VertexClass>()
=======
  vertexClassDag = new DirectedAcyclicGraph<VertexClass>(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
  ;

  static GraphClass createDefaultGraphClass(SchemaImpl schema) {
    assert schema.getDefaultPackage() != null : "DefaultPackage has not yet been created!";
    assert schema.getDefaultGraphClass() == null : "DefaultGraphClass already created!";
    GraphClass gc = new GraphClassImpl(schema);
    gc.setAbstract(true);
    ((GraphClassImpl) gc).setInternal(true);
    return gc;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private VertexClassImpl defaultVertexClass;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java


  private GraphClassImpl(SchemaImpl schema) {
    this(DEFAULTGRAPHCLASS_NAME, schema);
  }

  private EdgeClassImpl defaultEdgeClass;

  /**
	 * Creates the <b>sole</b> <code>GraphClass</code> in the
	 * <code>Schema</code>, that holds all <code>GraphElementClasses</code>/
	 * <code>EdgeClasses</code>/ <code>VertexClasses</code>/
	 * <code>AggregationClasses</code>/ <code>CompositionClasses</code>.
	 * <p>
	 * <b>Caution:</b> The <code>GraphClass</code> should only be created by
	 * using
	 * {@link de.uni_koblenz.jgralab.schema.Schema#createGraphClass(String qualifiedName)}
	 * in <code>Schema</code>. Unfortunately, due to restrictions in Java, the
	 * visibility of this constructor cannot be changed without causing serious
	 * issues in the program.
	 * </p>
	 * 
	 * @param qn
	 *            a unique name in the <code>Schema</code>
	 * @param aSchema
	 *            the <code>Schema</code> containing this
	 *            <code>GraphClass</code>
	 */

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
  protected
=======
>>>>>>> Unknown file: This is a bug in JDime.
   GraphClassImpl(String gcName, SchemaImpl schema) {
    super(gcName, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    schema.getDefaultPackage()
=======
    (PackageImpl) schema.getDefaultPackage()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
    , schema);

<<<<<<< Unknown file: This is a bug in JDime.
=======
    parentPackage.addGraphClass(this);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    schema.setGraphClass(this);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    defaultVertexClass = createDefaultVertexClass();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    register()
=======
    defaultEdgeClass = createDefaultEdgeClass()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
    ;
  }

  @Override public VertexClass getDefaultVertexClass() {
    return defaultVertexClass;
  }

  private VertexClassImpl createDefaultVertexClass() {
    VertexClassImpl vc = new VertexClassImpl(VertexClass.DEFAULTVERTEXCLASS_NAME, (PackageImpl) schema.getDefaultPackage(), this);
    vc.setAbstract(true);
    vc.setInternal(true);
    return vc;
  }

  private EdgeClassImpl createDefaultEdgeClass() {
    assert getDefaultVertexClass() != null : "Default VertexClass has not yet been created!";
    assert getDefaultEdgeClass() == null : "Default EdgeClass already created!";
    EdgeClassImpl ec = new EdgeClassImpl(EdgeClass.DEFAULTEDGECLASS_NAME, (PackageImpl) schema.getDefaultPackage(), this, defaultVertexClass, 0, Integer.MAX_VALUE, "", AggregationKind.NONE, defaultVertexClass, 0, Integer.MAX_VALUE, "", AggregationKind.NONE);
    ec.setAbstract(true);
    ec.setInternal(true);
    return ec;
  }

  @Override public EdgeClass getDefaultEdgeClass() {
    return defaultEdgeClass;
  }

  void addEdgeClass(EdgeClass ec) {
    if (edgeClasses.containsKey(ec.getQualifiedName())) {
      throw new SchemaException("Duplicate edge class name \'" + ec.getQualifiedName() + "\'");
    }
    if (graphElementClasses.containsKey(ec.getQualifiedName())) {
      throw new SchemaException("Edge class name \'" + ec.getQualifiedName() + "\' already used as vertex class name");
    }
    graphElementClasses.put(ec.getQualifiedName(), ec);
    edgeClasses.put(ec.getQualifiedName(), ec);
    edgeCsDag.createNode(ec);
  }

  void addVertexClass(VertexClass vc) {
    if (vertexClasses.containsKey(vc.getQualifiedName())) {
      throw new SchemaException("Duplicate vertex class name \'" + vc.getQualifiedName() + "\'");
    }
    if (graphElementClasses.containsKey(vc.getQualifiedName())) {
      throw new SchemaException("Vertex class name \'" + vc.getQualifiedName() + "\' already used as edge class name");
    }
    graphElementClasses.put(vc.getQualifiedName(), vc);
    vertexClasses.put(vc.getQualifiedName(), vc);
    vertexCsDag.createNode(vc);
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
  @Override public void addSuperClass(GraphClass superClass) {
    if (!superClass.getQualifiedName().equals(getSchema().getDefaultGraphClass().getQualifiedName())) {
      throw new InheritanceException("GraphClass can not be generealized.");
    }
    super.addSuperClass(superClass);
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override protected final void register() {
    assert parentPackage == getSchema().getDefaultPackage() : "The GraphClass must be in the default package.";
    ((PackageImpl) parentPackage).addGraphClass(this);
    if (!getSimpleName().equals(GraphClass.DEFAULTGRAPHCLASS_NAME)) {
      ((SchemaImpl) getSchema()).setGraphClass(this);
    }
  }

  @Override public String getVariableName() {
    return "gc_" + getQualifiedName().replace('.', '_');
  }

  @Override public EdgeClass createEdgeClass(String qualifiedName, VertexClass from, int fromMin, int fromMax, String fromRoleName, AggregationKind aggrFrom, VertexClass to, int toMin, int toMax, String toRoleName, AggregationKind aggrTo) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    if (isFinished()) {
      throw new SchemaException("No changes to finished schema!");
    }
=======
    assertNotFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

    if (!(aggrFrom == AggregationKind.NONE) && !(aggrTo == AggregationKind.NONE)) {
      throw new SchemaException("At least one end of each class must be of AggregationKind NONE at EdgeClass " + qualifiedName);
    }
    String[] qn = SchemaImpl.splitQualifiedName(qualifiedName);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    Package
=======
    PackageImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
     parent = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    ((SchemaImpl) getSchema()).createPackageWithParents(qn[0])
=======
    schema.createPackageWithParents(qn[0])
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
    ;
    EdgeClassImpl ec = new EdgeClassImpl(qn[1], parent, this, from, fromMin, fromMax, fromRoleName, aggrFrom, to, toMin, toMax, toRoleName, aggrTo);
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    !ec.getQualifiedName().equals(EdgeClass.DEFAULTEDGECLASS_NAME)
=======
    defaultEdgeClass != null
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
      EdgeClass s = getSchema().getDefaultEdgeClass();
=======
>>>>>>> Unknown file: This is a bug in JDime.

      ec.addSuperClass(defaultEdgeClass);
    }
    return ec;
  }

  @Override public VertexClass createVertexClass(String qualifiedName) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    if (isFinished()) {
      throw new SchemaException("No changes to finished schema!");
    }
=======
    assertNotFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

    String[] qn = SchemaImpl.splitQualifiedName(qualifiedName);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    Package
=======
    PackageImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
     parent = ((SchemaImpl) getSchema()).createPackageWithParents(qn[0]);
    VertexClassImpl vc = new VertexClassImpl(qn[1], parent, this);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    vc.addSuperClass(getSchema().getDefaultVertexClass());
=======
    if (defaultVertexClass != null) {
      vc.addSuperClass(defaultVertexClass);
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java

    return vc;
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
  @Override public boolean knowsOwn(GraphElementClass<?, ?> aGraphElementClass) {
    return (graphElementClasses.containsKey(aGraphElementClass.getQualifiedName()));
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public boolean knowsOwn(String qn) {
    return (graphElementClasses.containsKey(qn));
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
  @Override public boolean knows(GraphElementClass<?, ?> aGraphElementClass) {
    if (graphElementClasses.containsKey(aGraphElementClass.getQualifiedName())) {
      return true;
    }
    for (AttributedElementClass<?, ?> superClass : directSuperClasses) {
      if (((GraphClass) superClass).knows(aGraphElementClass)) {
        return true;
      }
    }
    return false;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public boolean knows(String qn) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    if (graphElementClasses.containsKey(qn)) {
      return true;
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    for (AttributedElementClass<?, ?> superClass : directSuperClasses) {
      if (((GraphClass) superClass).knows(qn)) {
        return true;
      }
    }
    return graphElementClasses.containsKey(qn);
  }

  @Override public GraphElementClass<?, ?> getGraphElementClass(String qn) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    if (graphElementClasses.containsKey(qn)) {
      return graphElementClasses.get(qn);
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    for (AttributedElementClass<?, ?> superClass : directSuperClasses) {
      if (((GraphClass) superClass).knows(qn)) {
        return ((GraphClass) superClass).getGraphElementClass(qn);
      }
    }
    return graphElementClasses.get(qn);
  }

  public String getDescriptionString() {
    StringBuilder output = new StringBuilder("GraphClassImpl \'" + getQualifiedName() + "\'");
    if (isAbstract()) {
      output.append(" (abstract)");
    }
    output.append(": \n");
    output.append("subClasses of \'" + getQualifiedName() + "\': ");
    Iterator<GraphClass> it = getAllSubClasses().iterator();
    while (it.hasNext()) {
      output.append("\'" + ((GraphClassImpl) it.next()).getQualifiedName() + "\' ");
    }
    output.append(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    "\nsuperClasses of \'" + getQualifiedName() + "\': "
=======
    ":\n"
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
    );
    Iterator<GraphClass> it2 = getAllSuperClasses().iterator();
    while (it2.hasNext()) {
      output.append("\'" + ((GraphClassImpl) it2.next()).getQualifiedName() + "\' ");
    }
    output.append(attributesToString());
    output.append("\n\nGraphElementClasses of \'" + getQualifiedName() + "\':\n\n");
    Iterator<GraphElementClass<?, ?>> it3 = graphElementClasses.values().iterator();
    while (it3.hasNext()) {
      output.append(it3.next().toString() + "\n");
    }
    return output.toString();
  }

  @Override public List<GraphElementClass<?, ?>> getGraphElementClasses() {
    return new ArrayList<GraphElementClass<?, ?>>(graphElementClasses.values());
  }

  @Override public List<EdgeClass> getEdgeClasses() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    edgeCsDag
=======
    edgeClassDag
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
    .getNodesInTopologicalOrder();
  }

  @Override public List<VertexClass> getVertexClasses() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    vertexCsDag
=======
    vertexClassDag
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
    .getNodesInTopologicalOrder();
  }

  @Override public VertexClass getVertexClass(String qn) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    VertexClass vc = vertexClasses.get(qn);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    if (vc != null) {
      return vc;
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    for (GraphClass superclass : directSuperClasses) {
      vc = superclass.getVertexClass(qn);
      if (vc != null) {
        return vc;
      }
    }
    return vertexClasses.get(qn);
  }

  @Override public EdgeClass getEdgeClass(String qn) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    EdgeClass ec = edgeClasses.get(qn);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    if (ec != null) {
      return ec;
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    for (GraphClass superclass : directSuperClasses) {
      ec = superclass.getEdgeClass(qn);
      if (ec != null) {
        return ec;
      }
    }
    return edgeClasses.get(qn);
  }

  @Override public int getEdgeClassCount() {
    return edgeClasses.size();
  }

  @Override public int getVertexClassCount() {
    return vertexClasses.size();
  }

  protected DirectedAcyclicGraph<EdgeClass> getEdgeCsDag() {
    return edgeCsDag;
  }

  protected DirectedAcyclicGraph<VertexClass> getVertexCsDag() {
    return vertexCsDag;
  }

  @Override protected void finish() {
    assertNotFinished();
    vertexClassDag.finish();
    edgeClassDag.finish();
    for (VertexClass vc : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    vertexCsDag
=======
    vertexClassDag
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
    .getNodesInTopologicalOrder()) {
      ((VertexClassImpl) vc).finish();
    }
    for (EdgeClass ec : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/left.java
    edgeCsDag
=======
    edgeClassDag
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/GraphClassImpl.java/right.java
    .getNodesInTopologicalOrder()) {
      ((EdgeClassImpl) ec).finish();
    }
    super.finish();
  }

  @Override protected void reopen() {
    for (VertexClass vc : vertexCsDag.getNodesInTopologicalOrder()) {
      ((VertexClassImpl) vc).reopen();
    }
    for (EdgeClass ec : edgeCsDag.getNodesInTopologicalOrder()) {
      ((EdgeClassImpl) ec).reopen();
    }
    super.reopen();
  }

  @Override public boolean hasOwnAttributes() {
    return hasAttributes();
  }

  @Override public Attribute getOwnAttribute(String name) {
    return getAttribute(name);
  }

  @Override public int getOwnAttributeCount() {
    return getAttributeCount();
  }

  @Override public List<Attribute> getOwnAttributeList() {
    return getAttributeList();
  }
}