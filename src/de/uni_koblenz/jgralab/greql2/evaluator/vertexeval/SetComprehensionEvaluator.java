package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PCollection;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.SetComprehension;

/**
 * Evaluates a SetComprehension vertex in the GReQL-2 Syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class SetComprehensionEvaluator extends ComprehensionEvaluator {
  /**
	 * The SetComprehension-Vertex this evaluator evaluates
	 */
  private SetComprehension vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public SetComprehension getVertex() {
    return vertex;
  }

  /**
	 * Creates a new SetComprehensionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public SetComprehensionEvaluator(SetComprehension vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  @Override protected PCollection<Object> getResultDatastructure() {
    return JGraLab.set();
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsSetComprehension(this, graphSize);
  }

  @Override public long calculateEstimatedCardinality(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCardinalitySetComprehension(this, graphSize);
  }
}