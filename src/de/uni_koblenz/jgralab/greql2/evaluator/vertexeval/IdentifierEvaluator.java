package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.Identifier;

/**
 * Evaluates an identifier vertex in the GReQL syntaxgraph. Does nothing but
 * allow the access to the identifier name via VertexEvaluator.getResult
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class IdentifierEvaluator extends VertexEvaluator {
  Identifier vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  public IdentifierEvaluator(Identifier vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  @Override public String evaluate() {
    return vertex.get_name();
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return new VertexCosts(1, 1, 1);
  }
}