package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsSequenceElementOf;
import de.uni_koblenz.jgralab.greql2.schema.SequentialPathDescription;

public class SequentialPathDescriptionEvaluator extends PathDescriptionEvaluator {
  /**
	 * The SequentialPathDescription-Vertex this evaluator evaluates
	 */
  private SequentialPathDescription vertex;

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
  public SequentialPathDescriptionEvaluator(SequentialPathDescription vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  @Override public NFA evaluate() {
    IsSequenceElementOf inc = vertex.getFirstIsSequenceElementOfIncidence(EdgeDirection.IN);
    ArrayList<NFA> nfaList = new ArrayList<NFA>();
    while (inc != null) {
      PathDescriptionEvaluator pathEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(inc.getAlpha());
      nfaList.add(pathEval.getNFA());
      inc = inc.getNextIsSequenceElementOfIncidence(EdgeDirection.IN);
    }
    return NFA.createSequentialPathDescriptionNFA(nfaList);
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsSequentialPathDescription(this, graphSize);
  }
}