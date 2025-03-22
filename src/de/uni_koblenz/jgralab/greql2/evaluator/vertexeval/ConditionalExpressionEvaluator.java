package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.ConditionalExpression;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;

/**
 * Evaluates a ConditionalExpression vertex in the GReQL-2 Syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ConditionalExpressionEvaluator extends VertexEvaluator {
  /**
	 * The ConditionalExpression-Vertex this evaluator evaluates
	 */
  private ConditionalExpression vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  /**
	 * Creates a new ConditionExpressionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public ConditionalExpressionEvaluator(ConditionalExpression vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  /**
	 * evaluates the conditional expression
	 */
  @Override public Object evaluate() {
    Expression condition = (Expression) vertex.getFirstIsConditionOfIncidence(EdgeDirection.IN).getAlpha();
    VertexEvaluator conditionEvaluator = vertexEvalMarker.getMark(condition);
    Object conditionResult = conditionEvaluator.getResult();
    Expression expressionToEvaluate = null;
    Boolean value = (Boolean) conditionResult;
    if (value.booleanValue()) {
      expressionToEvaluate = (Expression) vertex.getFirstIsTrueExprOfIncidence(EdgeDirection.IN).getAlpha();
    } else {
      expressionToEvaluate = (Expression) vertex.getFirstIsFalseExprOfIncidence(EdgeDirection.IN).getAlpha();
    }
    if (expressionToEvaluate != null) {
      VertexEvaluator exprEvaluator = vertexEvalMarker.getMark(expressionToEvaluate);
      result = exprEvaluator.getResult();
    } else {
      result = null;
    }
    return result;
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCostsConditionalExpression(this, graphSize);
  }
}