package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.BoolLiteral;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;

/**
 * Evaluates a boolean literal, that means, provides access to the literal value
 * using the getResult(...)-Method. This is needed, because is should make no
 * difference for the other VertexEvaluators, if a value is the result of a
 * maybe complex evaluation or if it is a literal.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class BoolLiteralEvaluator extends VertexEvaluator {
  /**
	 * The BoolLiteral this evluator provides acces to
	 */
  private BoolLiteral vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  public BoolLiteralEvaluator(BoolLiteral vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  @Override public Boolean evaluate() {
    return vertex.is_boolValue();
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return new VertexCosts(1, 1, 1);
  }

  @Override public double calculateEstimatedSelectivity(GraphSize graphSize) {
    if (vertex.is_boolValue()) {
      return 1;
    }
    return 0;
  }
}