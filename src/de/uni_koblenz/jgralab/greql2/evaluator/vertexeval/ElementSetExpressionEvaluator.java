package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.schema.ElementSetExpression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ElementSetExpressionEvaluator.java/left.java
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
  public ElementSetExpressionEvaluator(V vertex, Query query) {
    super(vertex, query);
  }
}
=======
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
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ElementSetExpressionEvaluator.java/right.java
