package de.uni_koblenz.jgralab.greql2.optimizer.condexp;
import java.util.ArrayList;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Graph;
import de.uni_koblenz.jgralab.greql2.schema.Greql2;

/**
 * TODO: (heimdall) Comment class!
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ConditionalExpression extends Formula {
  protected Expression condition;

  protected Formula trueFormula, falseFormula;

  public ConditionalExpression(GreqlEvaluator eval, Expression condition, Formula trueExp, Formula falseExp) {
    super(eval);
    this.condition = condition;
    trueFormula = trueExp;
    falseFormula = falseExp;
  }

  @Override public String toString() {
    return "(v" + condition.getId() + ") ? " + trueFormula + " : " + falseFormula + ";";
  }

  @Override public Expression toExpression() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/condexp/ConditionalExpression.java/left.java
    Greql2Graph
=======
    Greql2
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/condexp/ConditionalExpression.java/right.java
     syntaxgraph = greqlEvaluator.getSyntaxGraph();
    de.uni_koblenz.jgralab.greql2.schema.ConditionalExpression cond = syntaxgraph.createConditionalExpression();
    syntaxgraph.createIsConditionOf(condition, cond);
    syntaxgraph.createIsTrueExprOf(trueFormula.toExpression(), cond);
    syntaxgraph.createIsFalseExprOf(falseFormula.toExpression(), cond);
    return cond;
  }

  @Override protected ArrayList<Expression> getNonConstantTermExpressions() {
    throw new UnsupportedOperationException("Intentionally not implemented.");
  }

  @Override protected Formula calculateReplacementFormula(Expression exp, Literal literal) {
    throw new UnsupportedOperationException("Intentionally not implemented.");
  }

  @Override public Formula simplify() {
    throw new UnsupportedOperationException("Intentionally not implemented.");
  }

  @Override public double getSelectivity() {
    throw new UnsupportedOperationException("Intentionally not implemented.");
  }

  @Override public boolean equals(Object o) {
    throw new UnsupportedOperationException("Intentionally not implemented.");
  }

  @Override public int hashCode() {
    throw new UnsupportedOperationException("Intentionally not implemented.");
  }
}