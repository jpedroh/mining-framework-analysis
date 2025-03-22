package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import org.pcollections.PCollection;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsPartOf;
import de.uni_koblenz.jgralab.greql2.schema.ValueConstruction;

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