package de.uni_koblenz.jgralab.greql2.optimizer.condexp;
import java.util.ArrayList;
import de.uni_koblenz.jgralab.graphmarker.GraphMarker;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.optimizer.OptimizerUtility;
import de.uni_koblenz.jgralab.greql2.schema.Expression;

/**
 * TODO: (heimdall) Comment class!
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class NonConstantTerm extends Formula {
  protected Expression expression;

  public NonConstantTerm(Expression exp) {
    expression = exp;
  }

  @Override public String toString() {
    return "v" + expression.getId();
  }

  @Override public Expression toExpression() {
    return expression;
  }

  @Override protected ArrayList<Expression> getNonConstantTermExpressions() {
    ArrayList<Expression> exps = new ArrayList<Expression>();
    exps.add(expression);
    return exps;
  }

  @Override protected Formula calculateReplacementFormula(Expression exp, Literal literal) {
    if (expression == exp) {
      return literal;
    }
    return this;
  }

  @Override public Formula simplify() {
    return this;
  }

  @Override public double getSelectivity() {
    GraphSize graphSize = null;
    if (greqlEvaluator.getDatagraph() != null) {
      graphSize = new GraphSize(greqlEvaluator.getDatagraph());
    } else {
      graphSize = OptimizerUtility.getDefaultGraphSize();
    }
    GraphMarker<VertexEvaluator> marker = greqlEvaluator.getVertexEvaluatorGraphMarker();
    VertexEvaluator veval = marker.getMark(expression);
    double selectivity = veval.calculateEstimatedSelectivity(graphSize);
    logger.finer("selectivity[" + this + "] = " + selectivity);
    return selectivity;
  }

  @Override public boolean equals(Object o) {
    if (o instanceof NonConstantTerm) {
      NonConstantTerm nct = (NonConstantTerm) o;
      return expression == nct.expression;
    }
    return false;
  }

  @Override public int hashCode() {
    return expression.hashCode();
  }
}