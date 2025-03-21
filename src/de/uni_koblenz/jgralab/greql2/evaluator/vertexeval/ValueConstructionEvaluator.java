package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import org.pcollections.PCollection;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.IsPartOf;
import de.uni_koblenz.jgralab.greql2.schema.ValueConstruction;

/**
 * This is the abstract base class for all ValueConstructions
 * 
 * @author ist@uni-koblenz.de
 * 
 */
abstract public class ValueConstructionEvaluator<V extends ValueConstruction> extends VertexEvaluator<V> {
  private ArrayList<VertexEvaluator<? extends Expression>> partEvaluators = null;

  public ValueConstructionEvaluator(V vertex, QueryImpl query) {
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