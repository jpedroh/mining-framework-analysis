package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.OptionalPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * Evaluates an optional path description. Creates a NFA that accepts the
 * optional path description.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class OptionalPathDescriptionEvaluator extends PathDescriptionEvaluator<OptionalPathDescription> {
  /**
	 * Creates a new OptionalPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public OptionalPathDescriptionEvaluator(OptionalPathDescription vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    PathDescription p = vertex.getFirstIsOptionalPathOfIncidence(EdgeDirection.IN).getAlpha();
    PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) query.getVertexEvaluator(p);
    return NFA.createOptionalPathDescriptionNFA(pathEval.getNFA(evaluator));
  }
}