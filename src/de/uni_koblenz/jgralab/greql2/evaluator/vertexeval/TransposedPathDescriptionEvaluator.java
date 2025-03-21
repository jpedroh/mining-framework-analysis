package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;
import de.uni_koblenz.jgralab.greql2.schema.TransposedPathDescription;

/**
 * Evaluates a TransposedPathDescription vertex. Creates a NFA, which accepts
 * the PathDescription the vertex describes.
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public class TransposedPathDescriptionEvaluator extends PathDescriptionEvaluator<TransposedPathDescription> {
  /**
	 * Creates a new TransposedPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public TransposedPathDescriptionEvaluator(TransposedPathDescription vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    PathDescription p = vertex.getFirstIsTransposedPathOfIncidence(EdgeDirection.IN).getAlpha();
    PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) query.getVertexEvaluator(p);
    return NFA.createTransposedPathDescriptionNFA(pathEval.getNFA(evaluator));
  }
}