package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.StringLiteral;

/**
 * Evaluates a String Literal, that means, provides access to the literal value
 * using the getResult(...)-Method. This is needed, because is should make no
 * difference for the other VertexEvaluators, if a value is the result of a
 * maybe complex evaluation or if it is a literal.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class StringLiteralEvaluator extends VertexEvaluator<StringLiteral> {
  public StringLiteralEvaluator(StringLiteral vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public String evaluate(InternalGreqlEvaluator evaluator) {
    return vertex.get_stringValue();
  }
}