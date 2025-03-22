package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IntermediateVertexPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IsSubPathOf;

/**
 * Evaluates an IntermediateVertexPathDescription.
 *
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 *
 */
public class IntermediateVertexPathDescriptionEvaluator extends PathDescriptionEvaluator {
  /**
	 * The IntermediateVertexPathDescription-Vertex this evaluator evaluates
	 */
  private IntermediateVertexPathDescription vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  /**
	 * Creates a new IntermediateVertexPathDescriptionEvaluator for the given
	 * vertex
	 *
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public IntermediateVertexPathDescriptionEvaluator(IntermediateVertexPathDescription vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  @Override public NFA evaluate() {
    IsSubPathOf inc = vertex.getFirstIsSubPathOfIncidence(EdgeDirection.IN);
    PathDescriptionEvaluator firstEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(inc.getAlpha());
    NFA firstNFA = firstEval.getNFA();
    inc = inc.getNextIsSubPathOfIncidence(EdgeDirection.IN);
    PathDescriptionEvaluator secondEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(inc.getAlpha());
    NFA secondNFA = secondEval.getNFA();
    VertexEvaluator vertexEval = vertexEvalMarker.getMark(vertex.getFirstIsIntermediateVertexOfIncidence(EdgeDirection.IN).getAlpha());
    return NFA.createIntermediateVertexPathDescriptionNFA(firstNFA, vertexEval, secondNFA);
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsIntermediateVertexPathDescription(this, graphSize);
  }
}