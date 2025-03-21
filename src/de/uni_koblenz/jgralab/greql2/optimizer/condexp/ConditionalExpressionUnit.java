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

<<<<<<< Unknown file: This is a bug in JDime.
=======
  private GreqlEvaluator greqlEvaluator;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/condexp/ConditionalExpressionUnit.java/right.java


  private Expression condition;

  private Formula trueFormula, falseFormula;

  private double influenceCostRatio = -1;

  public ConditionalExpressionUnit(Expression exp, Formula origFormula) {
    greqlEvaluator = origFormula.greqlEvaluator;
    condition = exp;
    trueFormula = origFormula.calculateReplacementFormula(condition, new True(greqlEvaluator)).simplify();
    falseFormula = origFormula.calculateReplacementFormula(condition, new False(greqlEvaluator)).simplify();
  }

  private double calculateInfluenceCostRatio() {
    Formula boolDiff = new Not(greqlEvaluator, new Equiv(greqlEvaluator, trueFormula, new Not(greqlEvaluator, falseFormula)));
    boolDiff = boolDiff.simplify();
    double selectivity = boolDiff.getSelectivity();
    VertexEvaluator veval = greqlEvaluator.getVertexEvaluatorGraphMarker().getMark(condition);
    GraphSize graphSize = null;
    graphSize = OptimizerUtility.getDefaultGraphSize();
    long costs = veval.getInitialSubtreeEvaluationCosts(graphSize);
    return selectivity / costs;
  }

  ConditionalExpression toConditionalExpression() {
    return new ConditionalExpression(greqlEvaluator, condition, trueFormula.optimize(), falseFormula.optimize());
  }

  public double getInfluenceCostRatio() {
    if (influenceCostRatio == -1) {
      influenceCostRatio = calculateInfluenceCostRatio();
    }
    return influenceCostRatio;
  }
}