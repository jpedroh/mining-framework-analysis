package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PSet;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.EdgeSetExpression;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.EdgeClass;

/**
 * Calculates a subset of the datagraph edges
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgeSetExpressionEvaluator extends ElementSetExpressionEvaluator<EdgeSetExpression> {
  /**
	 * Creates a new ElementSetExpressionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public EdgeSetExpressionEvaluator(EdgeSetExpression vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public PSet<Edge> evaluate(InternalGreqlEvaluator evaluator) {
    PSet<Edge> resultSet = JGraLab.set();
    Edge currentEdge = query.getQueryGraph().getFirstEdge();
    TypeCollection typeCollection = getTypeCollection(evaluator);
    while (currentEdge != null) {
      EdgeClass edgeClass = currentEdge.getAttributedElementClass();
      if (typeCollection.acceptsType(edgeClass)) {
        resultSet = resultSet.plus(currentEdge);
      }
      currentEdge = currentEdge.getNextEdge();
    }
    return resultSet;
  }
}