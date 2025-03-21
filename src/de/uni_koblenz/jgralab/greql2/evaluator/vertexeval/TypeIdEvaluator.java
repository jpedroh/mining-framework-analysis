package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import java.util.List;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.exception.UnknownTypeException;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.TypeId;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;

/**
 * Creates a List of types out of the TypeId-Vertex.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class TypeIdEvaluator extends VertexEvaluator<TypeId> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java


  public TypeIdEvaluator(TypeId vertex, Query query) {
    super(vertex, query);
  }

  /**
	 * Creates a list of types from this TypeId-Vertex
	 * 
	 * @param schema
	 *            the schema of the datagraph
	 * @return the generated list of types
	 */
  protected List<AttributedElementClass<?, ?>> createTypeList(InternalGreqlEvaluator evaluator) {
    ArrayList<AttributedElementClass<?, ?>> returnTypes = new ArrayList<AttributedElementClass<?, ?>>();

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
    AttributedElementClass elemClass = evaluator.getAttributedElementClass(vertex.get_name());
=======
    AttributedElementClass<?, ?> elemClass = schema.getAttributedElementClass(vertex.get_name());
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java

    if (elemClass == null) {
      elemClass = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
      evaluator.getKnownType(vertex.get_name())
=======
      greqlEvaluator.getKnownType(vertex.get_name())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
      ;
      if (elemClass == null) {
        throw new UnknownTypeException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
        vertex.get_name()
=======
        vertex.get_name()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
        , createPossibleSourcePositions());
      } else {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
        vertex.set_name(elemClass.getQualifiedName())
=======
        vertex.set_name(elemClass.getQualifiedName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
        ;
      }
    }
    returnTypes.add(elemClass);
    if (!
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
    vertex.is_type()
=======
    vertex.is_type()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
    ) {
      returnTypes.addAll(elemClass.getAllSubClasses());
    }
    return returnTypes;
  }

  @Override public 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
  TypeCollection
=======
  Object
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
   evaluate(InternalGreqlEvaluator evaluator) {
    List<
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
    AttributedElementClass
=======
    AttributedElementClass<?, ?>
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
    > typeList = createTypeList(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
    evaluator
=======
    greqlEvaluator.getDatagraph().getSchema()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
    );
    return new TypeCollection(typeList, vertex.is_excluded());
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCostsTypeId(this, graphSize);
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java


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