package de.uni_koblenz.jgralab.greql2.optimizer.condexp;
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
public class ConditionalExpressionUnit {
  private Expression condition;

  private Formula trueFormula, falseFormula;

  private double influenceCostRatio = -1;

  public ConditionalExpressionUnit(Expression exp, Formula origFormula) {
    condition = exp;
    trueFormula = origFormula.calculateReplacementFormula(condition, new True()).simplify();
    falseFormula = origFormula.calculateReplacementFormula(condition, new False()).simplify();
  }

  private double calculateInfluenceCostRatio() {
    Formula boolDiff = new Not(new Equiv(trueFormula, new Not(falseFormula)));
    boolDiff = boolDiff.simplify();
    double selectivity = boolDiff.getSelectivity();
    VertexEvaluator veval = greqlEvaluator.getVertexEvaluatorGraphMarker().getMark(condition);
    GraphSize graphSize = null;
    graphSize = OptimizerUtility.getDefaultGraphSize();
    long costs = veval.getInitialSubtreeEvaluationCosts(graphSize);
    return selectivity / costs;
  }

  ConditionalExpression toConditionalExpression() {
    return new ConditionalExpression(condition, trueFormula.optimize(), falseFormula.optimize());
  }

  public double getInfluenceCostRatio() {
    if (influenceCostRatio == -1) {
      influenceCostRatio = calculateInfluenceCostRatio();
    }
    return influenceCostRatio;
  }
}