package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import java.util.List;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.UnknownTypeException;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.TypeId;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * Creates a List of types out of the TypeId-Vertex.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class TypeIdEvaluator extends VertexEvaluator {
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  private TypeId vertex;

  public TypeIdEvaluator(TypeId vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  /**
	 * Creates a list of types from this TypeId-Vertex
	 * 
	 * @param schema
	 *            the schema of the datagraph
	 * @return the generated list of types
	 */
  protected List<
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
  AttributedElementClass
=======
  GraphElementClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
  <?, ?>> createTypeList(Schema schema) {
    ArrayList<
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
    AttributedElementClass
=======
    GraphElementClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
    <?, ?>> returnTypes = new ArrayList<
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
    AttributedElementClass
=======
    GraphElementClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
    <?, ?>>();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
    AttributedElementClass
=======
    GraphElementClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
    <?, ?> elemClass = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
    schema.getAttributedElementClass(vertex.get_name())
=======
    schema.getGraphClass().getGraphElementClass(vertex.get_name())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
    ;
    if (elemClass == null) {
      elemClass = greqlEvaluator.getKnownType(vertex.get_name());
      if (elemClass == null) {
        throw new UnknownTypeException(vertex.get_name(), createPossibleSourcePositions());
      } else {
        vertex.set_name(elemClass.getQualifiedName());
      }
    }
    returnTypes.add(elemClass);
    if (!vertex.is_type()) {
      returnTypes.addAll(elemClass.getAllSubClasses());
    }
    return returnTypes;
  }

  @Override public Object evaluate() {
    List<
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
    AttributedElementClass
=======
    GraphElementClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
    <?, ?>> typeList = createTypeList(greqlEvaluator.getDatagraph().getSchema());
    return new TypeCollection(typeList, vertex.is_excluded());
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCostsTypeId(this, graphSize);
  }

  @Override public double calculateEstimatedSelectivity(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateSelectivityTypeId(this, graphSize);
  }

  @Override public String getLoggingName() {
    StringBuilder name = new StringBuilder();
    name.append(vertex.getAttributedElementClass().getQualifiedName());
    if (vertex.is_type()) {
      name.append("-type");
    }
    if (vertex.is_excluded()) {
      name.append("-excluded");
    }
    return name.toString();
  }
}