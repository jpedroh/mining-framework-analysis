package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;
import de.uni_koblenz.jgralab.greql2.schema.ExponentiatedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * Evaluates an exponentiated path description. Creates a NFA that accepts the
 * exponentiated path description.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ExponentiatedPathDescriptionEvaluator extends PathDescriptionEvaluator<ExponentiatedPathDescription> {
  /**
	 * Creates a new ExponentiatedPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public ExponentiatedPathDescriptionEvaluator(ExponentiatedPathDescription vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    PathDescription p = vertex.getFirstIsExponentiatedPathOfIncidence().getAlpha();
    PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) query.getVertexEvaluator(p);
    VertexEvaluator<? extends Expression> exponentEvaluator = query.getVertexEvaluator(vertex.getFirstIsExponentOfIncidence(EdgeDirection.IN).getAlpha());
    Object exponentValue = exponentEvaluator.getResult(evaluator);
    int exponent = 0;
    if (exponentValue instanceof Integer) {
      exponent = (Integer) exponentValue;
    } else {
      throw new GreqlException("Exponent of ExponentiatedPathDescription is not convertable to integer value");
    }
    return NFA.createExponentiatedPathDescriptionNFA(pathEval.getNFA(evaluator), exponent);
  }
}