package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.IteratedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IterationType;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * Evaluates an iterated path description. Creates a NFA that accepts the
 * iterated path description.
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public class IteratedPathDescriptionEvaluator extends PathDescriptionEvaluator<IteratedPathDescription> {
  /**
	 * Creates a new IteratedPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public IteratedPathDescriptionEvaluator(IteratedPathDescription vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    PathDescription p = vertex.getFirstIsIteratedPathOfIncidence(EdgeDirection.IN).getAlpha();
    PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) query.getVertexEvaluator(p);
    NFA createdNFA = NFA.createIteratedPathDescriptionNFA(pathEval.getNFA(evaluator), vertex.get_times() == IterationType.STAR);
    return createdNFA;
  }
}