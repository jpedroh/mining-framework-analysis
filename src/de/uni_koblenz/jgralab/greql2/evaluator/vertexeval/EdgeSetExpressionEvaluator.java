package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PSet;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.EdgeSetExpression;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.EdgeClass;

/**
 * Calculates a subset of the datagraph edges
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgeSetExpressionEvaluator extends ElementSetExpressionEvaluator {
  /**
	 * Creates a new ElementSetExpressionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public EdgeSetExpressionEvaluator(EdgeSetExpression vertex, GreqlEvaluator eval) {
    super(vertex, eval);
  }

  @Override public PSet<Edge> evaluate() {
    Graph datagraph = greqlEvaluator.getDatagraph();
    PSet<Edge> resultSet = JGraLab.set();
    Edge currentEdge = datagraph.getFirstEdge();
    TypeCollection typeCollection = getTypeCollection();
    while (currentEdge != null) {
      EdgeClass edgeClass = currentEdge.getAttributedElementClass();
      if (typeCollection.acceptsType(edgeClass)) {
        resultSet = resultSet.plus(currentEdge);
      }
      currentEdge = currentEdge.getNextEdge();
    }
    return resultSet;
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCostsEdgeSetExpression(this, graphSize);
  }

  @Override public long calculateEstimatedCardinality(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCardinalityEdgeSetExpression(this, graphSize);
  }
}