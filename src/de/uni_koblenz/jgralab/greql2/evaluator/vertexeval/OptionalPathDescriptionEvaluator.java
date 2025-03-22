package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.OptionalPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * Evaluates an optional path description. Creates a NFA that accepts the
 * optional path description.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class OptionalPathDescriptionEvaluator extends PathDescriptionEvaluator {
  /**
	 * The IteratedPathDescription-Vertex this evaluator evaluates
	 */
  private OptionalPathDescription vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  /**
	 * Creates a new OptionalPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public OptionalPathDescriptionEvaluator(OptionalPathDescription vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  @Override public NFA evaluate() {
    PathDescription p = (PathDescription) vertex.getFirstIsOptionalPathOfIncidence(EdgeDirection.IN).getAlpha();
    PathDescriptionEvaluator pathEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(p);
    return NFA.createOptionalPathDescriptionNFA(pathEval.getNFA());
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsOptionalPathDescription(this, graphSize);
  }
}