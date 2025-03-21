package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import org.pcollections.PCollection;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsPartOf;
import de.uni_koblenz.jgralab.greql2.schema.ValueConstruction;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/left.java
/**
 * This is the abstract base class for all ValueConstructions
 * 
 * @author ist@uni-koblenz.de
 * 
 */
abstract public class ValueConstructionEvaluator<V extends ValueConstruction> extends VertexEvaluator<V> {
  private ArrayList<VertexEvaluator<? extends Expression>> partEvaluators = null;

  public ValueConstructionEvaluator(V vertex, Query query) {
    super(vertex, query);
  }

  public final PCollection<Object> createValue(PCollection<Object> collection, InternalGreqlEvaluator evaluator) {
    if (partEvaluators == null) {
      int partCount = 0;
      IsPartOf inc = vertex.getFirstIsPartOfIncidence(EdgeDirection.IN);
      while (inc != null) {
        partCount++;
        inc = inc.getNextIsPartOfIncidence(EdgeDirection.IN);
      }
      inc = vertex.getFirstIsPartOfIncidence(EdgeDirection.IN);
      partEvaluators = new ArrayList<VertexEvaluator<? extends Expression>>(partCount);
      while (inc != null) {
        Expression currentExpression = inc.getAlpha();
        VertexEvaluator<? extends Expression> vertexEval = query.getVertexEvaluator(currentExpression);
        partEvaluators.add(vertexEval);
        inc = inc.getNextIsPartOfIncidence(EdgeDirection.IN);
      }
    }
    for (int i = 0; i < partEvaluators.size(); i++) {
      collection = collection.plus(partEvaluators.get(i).getResult(evaluator));
    }
    return collection;
  }
}
=======
/**
 * This is the abstract base class for all ValueConstructions
 *
 * @author ist@uni-koblenz.de
 *
 */
abstract public class ValueConstructionEvaluator extends VertexEvaluator {
  protected ValueConstruction vertex;

  private ArrayList<VertexEvaluator> partEvaluators = null;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  public ValueConstructionEvaluator(ValueConstruction vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  public final PCollection<Object> createValue(PCollection<Object> collection) {
    if (partEvaluators == null) {
      int partCount = 0;
      IsPartOf inc = vertex.getFirstIsPartOfIncidence(EdgeDirection.IN);
      while (inc != null) {
        partCount++;
        inc = inc.getNextIsPartOfIncidence(EdgeDirection.IN);
      }
      inc = vertex.getFirstIsPartOfIncidence(EdgeDirection.IN);
      partEvaluators = new ArrayList<VertexEvaluator>(partCount);
      while (inc != null) {
        Expression currentExpression = (Expression) inc.getAlpha();
        VertexEvaluator vertexEval = vertexEvalMarker.getMark(currentExpression);
        partEvaluators.add(vertexEval);
        inc = inc.getNextIsPartOfIncidence(EdgeDirection.IN);
      }
    }
    for (int i = 0; i < partEvaluators.size(); i++) {
      collection = collection.plus(partEvaluators.get(i).getResult());
    }
    return collection;
  }
}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/right.java
