package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.ConditionalExpression;
import de.uni_koblenz.jgralab.greql2.schema.Expression;

/**
 * Evaluates a ConditionalExpression vertex in the GReQL-2 Syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ConditionalExpressionEvaluator extends VertexEvaluator<ConditionalExpression> {
  /**
	 * Creates a new ConditionExpressionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public ConditionalExpressionEvaluator(ConditionalExpression vertex, QueryImpl query) {
    super(vertex, query);
  }

  /**
	 * evaluates the conditional expression
	 */
  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    Expression condition = vertex.getFirstIsConditionOfIncidence(EdgeDirection.IN).getAlpha();
    VertexEvaluator<? extends Expression> conditionEvaluator = query.getVertexEvaluator(condition);
    Object conditionResult = conditionEvaluator.getResult(evaluator);
    Expression expressionToEvaluate = null;
    Boolean value = (Boolean) conditionResult;
    if (value.booleanValue()) {
      expressionToEvaluate = vertex.getFirstIsTrueExprOfIncidence(EdgeDirection.IN).getAlpha();
    } else {
      expressionToEvaluate = vertex.getFirstIsFalseExprOfIncidence(EdgeDirection.IN).getAlpha();
    }
    Object result = null;
    if (expressionToEvaluate != null) {
      VertexEvaluator<? extends Expression> exprEvaluator = query.getVertexEvaluator(expressionToEvaluate);
      result = exprEvaluator.getResult(evaluator);
      evaluator.setLocalEvaluationResult(vertex, result);
    } else {
      evaluator.removeLocalEvaluationResult(vertex);
      result = null;
    }
    return result;
  }
}