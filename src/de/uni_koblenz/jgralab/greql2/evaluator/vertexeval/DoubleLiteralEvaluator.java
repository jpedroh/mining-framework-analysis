package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.DoubleLiteral;

/**
 * Evaluates a Double Literal, that means, provides access to the literal value
 * using the getResult(...)-Method. This is needed, because it should make no
 * difference for the other VertexEvaluators, if a value is the result of a
 * maybe complex evaluation or if it is a literal.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class DoubleLiteralEvaluator extends VertexEvaluator<DoubleLiteral> {
  public DoubleLiteralEvaluator(DoubleLiteral vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public Double evaluate(InternalGreqlEvaluator evaluator) {
    return vertex.get_doubleValue();
  }
}