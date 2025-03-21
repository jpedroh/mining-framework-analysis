package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PCollection;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.SetComprehension;

/**
 * Evaluates a SetComprehension vertex in the GReQL-2 Syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class SetComprehensionEvaluator extends ComprehensionEvaluator<SetComprehension> {
  /**
	 * Creates a new SetComprehensionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public SetComprehensionEvaluator(SetComprehension vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override protected PCollection<Object> getResultDatastructure(InternalGreqlEvaluator evaluator) {
    return JGraLab.set();
  }
}