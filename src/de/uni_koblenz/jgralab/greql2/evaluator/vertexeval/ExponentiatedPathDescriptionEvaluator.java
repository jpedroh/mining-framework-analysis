package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;
import de.uni_koblenz.jgralab.greql2.schema.ExponentiatedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * Evaluates an exponentiated path description. Creates a NFA that accepts the
 * exponentiated path description.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ExponentiatedPathDescriptionEvaluator extends PathDescriptionEvaluator {
  /**
	 * The ExponentiatedPathDescription-Vertex this evaluator evaluates
	 */
  private ExponentiatedPathDescription vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  /**
	 * Creates a new ExponentiatedPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public ExponentiatedPathDescriptionEvaluator(ExponentiatedPathDescription vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  @Override public NFA evaluate() {
    PathDescription p = (PathDescription) vertex.getFirstIsExponentiatedPathOfIncidence().getAlpha();
    PathDescriptionEvaluator pathEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(p);
    VertexEvaluator exponentEvaluator = vertexEvalMarker.getMark(vertex.getFirstIsExponentOfIncidence(EdgeDirection.IN).getAlpha());
    Object exponentValue = exponentEvaluator.getResult();
    int exponent = 0;
    if (exponentValue instanceof Integer) {
      exponent = (Integer) exponentValue;
    } else {
      throw new GreqlException("Exponent of ExponentiatedPathDescription is not convertable to integer value");
    }
    return NFA.createExponentiatedPathDescriptionNFA(pathEval.getNFA(), exponent);
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsExponentiatedPathDescription(this, graphSize);
  }
}