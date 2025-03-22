package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
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
public class IteratedPathDescriptionEvaluator extends PathDescriptionEvaluator {
  /**
	 * The IteratedPathDescription-Vertex this evaluator evaluates
	 */
  private IteratedPathDescription vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  /**
	 * Creates a new IteratedPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public IteratedPathDescriptionEvaluator(IteratedPathDescription vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  @Override public NFA evaluate() {
    PathDescription p = (PathDescription) vertex.getFirstIsIteratedPathOfIncidence(EdgeDirection.IN).getAlpha();
    PathDescriptionEvaluator pathEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(p);
    NFA createdNFA = NFA.createIteratedPathDescriptionNFA(pathEval.getNFA(), vertex.get_times() == IterationType.STAR);
    return createdNFA;
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsIteratedPathDescription(this, graphSize);
  }
}