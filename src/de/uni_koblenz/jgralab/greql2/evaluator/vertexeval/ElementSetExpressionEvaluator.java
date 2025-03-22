package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.ElementSetExpression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;

/**
 * This is the base class for VertexSetExpressionEvaluator and
 * EdgeSetExpressionEvaluator
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public abstract class ElementSetExpressionEvaluator extends AbstractGraphElementCollectionEvaluator {
  /**
	 * The ElementSetExpression-Vertex this evaluator evaluates
	 */
  protected ElementSetExpression vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  /**
	 * Creates a new ElementSetExpressionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public ElementSetExpressionEvaluator(ElementSetExpression vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }
}