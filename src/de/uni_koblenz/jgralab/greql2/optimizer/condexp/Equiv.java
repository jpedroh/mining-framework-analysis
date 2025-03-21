package de.uni_koblenz.jgralab.greql2.optimizer.condexp;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.Expression;

/**
 * TODO: (heimdall) Comment class!
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class Equiv extends BinaryOperator {
  public Equiv(GreqlEvaluator eval, Formula lhs, Formula rhs) {
    super(eval, lhs, rhs);
  }

  @Override protected Formula calculateReplacementFormula(Expression exp, Literal literal) {
    throw new UnsupportedOperationException("Intentionally not implemented.");
  }

  @Override public Formula simplify() {
    Formula lhs = leftHandSide.simplify();
    Formula rhs = rightHandSide.simplify();
    return new Equiv(greqlEvaluator, lhs, rhs);
  }

  @Override public Expression toExpression() {
    throw new UnsupportedOperationException("Intentionally not implemented.");
  }

  @Override public String toString() {
    return "(" + leftHandSide + " <=> " + rightHandSide + ")";
  }

  @Override public double getSelectivity() {
    double leftSel = leftHandSide.getSelectivity();
    double rightSel = rightHandSide.getSelectivity();
    double selectivity = 1 - (1 - leftSel * rightSel) * (1 - (1 - leftSel) * (1 - rightSel));
    logger.finer("selectivity[" + this + "] = " + selectivity);
    return selectivity;
  }

  @Override public boolean equals(Object o) {
    if (o instanceof Equiv) {
      Equiv equiv = (Equiv) o;
      return leftHandSide.equals(equiv.leftHandSide) && rightHandSide.equals(equiv.rightHandSide);
    }
    return false;
  }

  @Override public int hashCode() {
    return hashCode(21);
  }
}