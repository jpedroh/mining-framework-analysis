package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.AlternativePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsAlternativePathOf;

/**
 * Evaluates an alternative path description. Creates a NFA that accepts the
 * alternative path description.
 *
 * @author ist@uni-koblenz.de
 *
 */
public class AlternativePathDescriptionEvaluator extends PathDescriptionEvaluator {
  /**
	 * The AlternativePathDescription-Vertex this evaluator evaluates
	 */
  private AlternativePathDescription vertex;

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
  public AlternativePathDescriptionEvaluator(AlternativePathDescription vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  /**
	 * Calculate the costs to re-evaluate this vertex if the given variable
	 * changes. Assumes, that also the variables "right" of the given one
	 * change.
	 */
  @Override public NFA evaluate() {
    IsAlternativePathOf inc = vertex.getFirstIsAlternativePathOfIncidence(EdgeDirection.IN);
    ArrayList<NFA> nfaList = new ArrayList<NFA>();
    while (inc != null) {
      PathDescriptionEvaluator pathEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(inc.getAlpha());
      nfaList.add(pathEval.getNFA());
      inc = inc.getNextIsAlternativePathOfIncidence(EdgeDirection.IN);
    }
    return NFA.createAlternativePathDescriptionNFA(nfaList);
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsAlternativePathDescription(this, graphSize);
  }
}