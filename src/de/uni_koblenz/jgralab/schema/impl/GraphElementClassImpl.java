package de.uni_koblenz.jgralab.schema.impl;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.Package;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/left.java
public abstract class GraphElementClassImpl extends AttributedElementClassImpl implements GraphElementClass {
  protected GraphClass graphClass;

  /**
	 * delegates its constructor to the generalized class
	 * 
	 * @param qn
	 *            the unique identifier of the element in the schema
	 */
  protected GraphElementClassImpl(String simpleName, Package pkg, GraphClass graphClass) {
    super(simpleName, pkg, graphClass.getSchema());
    this.graphClass = graphClass;
  }

  @Override public GraphClass getGraphClass() {
    return graphClass;
  }

  public String getDescriptionString() {
    StringBuilder output = new StringBuilder(this.getClass().getSimpleName() + " \'" + getQualifiedName() + "\'");
    if (isAbstract()) {
      output.append(" (abstract)");
    }
    output.append(": \n");
    output.append("subClasses of \'" + getQualifiedName() + "\': ");
    for (AttributedElementClass aec : getAllSubClasses()) {
      output.append("\'" + aec.getQualifiedName() + "\' ");
    }
    output.append("\nsuperClasses of \'" + getQualifiedName() + "\': ");
    for (AttributedElementClass aec : getAllSuperClasses()) {
      output.append("\'" + aec.getQualifiedName() + "\' ");
    }
    output.append("\ndirectSuperClasses of \'" + getQualifiedName() + "\': ");
    for (AttributedElementClass aec : getDirectSuperClasses()) {
      output.append("\'" + aec.getQualifiedName() + "\' ");
    }
    output.append(attributesToString());
    if (this instanceof VertexClass) {
      output.append("outgoing edge classes: ");
      output.append("\n");
      output.append("incomming edge classes: ");
      output.append("\n");
    }
    output.append("\n");
    return output.toString();
  }
}
=======
public abstract class GraphElementClassImpl<SC extends GraphElementClass<SC, IC>, IC extends GraphElement<SC, IC>> extends AttributedElementClassImpl<SC, IC> implements GraphElementClass<SC, IC> {
  protected GraphClass graphClass;

  /**
	 * delegates its constructor to the generalized class
	 * 
	 * @param qn
	 *            the unique identifier of the element in the schema
	 */
  protected GraphElementClassImpl(String simpleName, Package pkg, GraphClass graphClass) {
    super(simpleName, pkg, graphClass.getSchema());
    this.graphClass = graphClass;
  }

  @Override public GraphClass getGraphClass() {
    return graphClass;
  }

  public String getDescriptionString() {
    StringBuilder output = new StringBuilder(this.getClass().getSimpleName() + " \'" + getQualifiedName() + "\'");
    if (isAbstract()) {
      output.append(" (abstract)");
    }
    output.append(": \n");
    output.append("subClasses of \'" + getQualifiedName() + "\': ");
    for (SC aec : getAllSubClasses()) {
      output.append("\'" + aec.getQualifiedName() + "\' ");
    }
    output.append("\nsuperClasses of \'" + getQualifiedName() + "\': ");
    for (SC aec : getAllSuperClasses()) {
      output.append("\'" + aec.getQualifiedName() + "\' ");
    }
    output.append("\ndirectSuperClasses of \'" + getQualifiedName() + "\': ");
    for (SC aec : getDirectSuperClasses()) {
      output.append("\'" + aec.getQualifiedName() + "\' ");
    }
    output.append(attributesToString());
    output.append("\n");
    return output.toString();
  }
}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/impl/GraphElementClassImpl.java/right.java
