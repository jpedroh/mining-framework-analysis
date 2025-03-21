package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.AlternativePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IsAlternativePathOf;

/**
 * Evaluates an alternative path description. Creates a NFA that accepts the
 * alternative path description.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class AlternativePathDescriptionEvaluator extends PathDescriptionEvaluator<AlternativePathDescription> {
  /**
	 * Creates a new IteratedPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public AlternativePathDescriptionEvaluator(AlternativePathDescription vertex, QueryImpl query) {
    super(vertex, query);
  }

  /**
	 * Calculate the costs to re-evaluate this vertex if the given variable
	 * changes. Assumes, that also the variables "right" of the given one
	 * change.
	 */
  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    IsAlternativePathOf inc = vertex.getFirstIsAlternativePathOfIncidence(EdgeDirection.IN);
    ArrayList<NFA> nfaList = new ArrayList<NFA>();
    while (inc != null) {
      PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) query.getVertexEvaluator(inc.getAlpha());
      nfaList.add(pathEval.getNFA(evaluator));
      inc = inc.getNextIsAlternativePathOfIncidence(EdgeDirection.IN);
    }
    return NFA.createAlternativePathDescriptionNFA(nfaList);
  }
}