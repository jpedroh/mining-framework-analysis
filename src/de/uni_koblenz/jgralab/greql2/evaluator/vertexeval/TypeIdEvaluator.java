package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import java.util.List;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.exception.UnknownTypeException;
import de.uni_koblenz.jgralab.greql2.schema.TypeId;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.GraphElementClass;

/**
 * Creates a List of types out of the TypeId-Vertex.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class TypeIdEvaluator extends VertexEvaluator<TypeId> {
  public TypeIdEvaluator(TypeId vertex, QueryImpl query) {
    super(vertex, query);
  }

  /**
	 * Creates a list of types from this TypeId-Vertex
	 * 
	 * @param schema
	 *            the schema of the datagraph
	 * @return the generated list of types
	 */
  protected List<GraphElementClass<?, ?>> createTypeList(InternalGreqlEvaluator evaluator) {
    ArrayList<GraphElementClass<?, ?>> returnTypes = new ArrayList<GraphElementClass<?, ?>>();
    GraphElementClass<?, ?> elemClass = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/3ecb9c7c133da9fe37fca7b4089fc0a4eeea96fc/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/left.java
    evaluator.getAttributedElementClass(vertex.get_name())
=======
    schema.getGraphClass().getGraphElementClass(vertex.get_name())
>>>>>>> /usr/src/app/output/jgralab/jgralab/3ecb9c7c133da9fe37fca7b4089fc0a4eeea96fc/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TypeIdEvaluator.java/right.java
    ;
    if (elemClass == null) {
      elemClass = query.getKnownType(vertex.get_name());
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

  @Override public TypeCollection evaluate(InternalGreqlEvaluator evaluator) {
    List<GraphElementClass<?, ?>> typeList = createTypeList(evaluator);
    return new TypeCollection(typeList, vertex.is_excluded());
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