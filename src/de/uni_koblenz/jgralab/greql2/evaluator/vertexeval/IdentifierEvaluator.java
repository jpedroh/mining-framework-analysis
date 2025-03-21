package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.Identifier;

/**
 * Evaluates an identifier vertex in the GReQL syntaxgraph. Does nothing but
 * allow the access to the identifier name via VertexEvaluator.getResult
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class IdentifierEvaluator extends VertexEvaluator<Identifier> {
  public IdentifierEvaluator(Identifier vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public String evaluate(InternalGreqlEvaluator evaluator) {
    return vertex.get_name();
  }
}