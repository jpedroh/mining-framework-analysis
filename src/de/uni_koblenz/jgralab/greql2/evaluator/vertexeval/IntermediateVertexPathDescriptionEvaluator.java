package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.IntermediateVertexPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IsSubPathOf;

/**
 * Evaluates an IntermediateVertexPathDescription.
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public class IntermediateVertexPathDescriptionEvaluator extends PathDescriptionEvaluator<IntermediateVertexPathDescription> {
  /**
	 * Creates a new IntermediateVertexPathDescriptionEvaluator for the given
	 * vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public IntermediateVertexPathDescriptionEvaluator(IntermediateVertexPathDescription vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    IsSubPathOf inc = vertex.getFirstIsSubPathOfIncidence(EdgeDirection.IN);
    PathDescriptionEvaluator<?> firstEval = (PathDescriptionEvaluator<?>) query.getVertexEvaluator(inc.getAlpha());
    NFA firstNFA = firstEval.getNFA(evaluator);
    inc = inc.getNextIsSubPathOfIncidence(EdgeDirection.IN);
    PathDescriptionEvaluator<?> secondEval = (PathDescriptionEvaluator<?>) query.getVertexEvaluator(inc.getAlpha());
    NFA secondNFA = secondEval.getNFA(evaluator);
    VertexEvaluator<? extends Expression> vertexEval = query.getVertexEvaluator(vertex.getFirstIsIntermediateVertexOfIncidence(EdgeDirection.IN).getAlpha());
    return NFA.createIntermediateVertexPathDescriptionNFA(firstNFA, vertexEval, secondNFA);
  }
}