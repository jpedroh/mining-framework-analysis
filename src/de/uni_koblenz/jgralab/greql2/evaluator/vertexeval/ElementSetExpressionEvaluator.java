package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.ElementSetExpression;

/**
 * This is the base class for VertexSetExpressionEvaluator and
 * EdgeSetExpressionEvaluator
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public abstract class ElementSetExpressionEvaluator<V extends ElementSetExpression> extends AbstractGraphElementCollectionEvaluator<V> {
  /**
	 * Creates a new ElementSetExpressionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public ElementSetExpressionEvaluator(V vertex, QueryImpl query) {
    super(vertex, query);
  }
}