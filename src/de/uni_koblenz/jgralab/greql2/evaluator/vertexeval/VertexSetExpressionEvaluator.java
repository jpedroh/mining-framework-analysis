package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PSet;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.VertexSetExpression;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

/**
 * construct a subset of the datagraph vertices. For instance, the expression
 * V:{Department} will be evaluated by this evaluator, it will construct the set
 * of vertices in the datagraph that have the type Department or a type that is
 * derived from Department
 *
 * @author ist@uni-koblenz.de
 *
 */
public class VertexSetExpressionEvaluator extends ElementSetExpressionEvaluator {
  /**
	 * Creates a new ElementSetExpressionEvaluator for the given vertex
	 *
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public VertexSetExpressionEvaluator(VertexSetExpression vertex, GreqlEvaluator eval) {
    super(vertex, eval);
  }

  @Override public Object evaluate() {
    Graph datagraph = greqlEvaluator.getDatagraph();
    TypeCollection typeCollection = getTypeCollection();
    PSet<Vertex> resultSet = null;
    String indexKey = null;
    if (GreqlEvaluator.VERTEX_INDEXING) {
      indexKey = typeCollection.toString();
      resultSet = GreqlEvaluator.getVertexIndex(datagraph, indexKey);
    }
    if (resultSet == null) {
      long startTime = System.currentTimeMillis();
      resultSet = JGraLab.set();
      Vertex currentVertex = datagraph.getFirstVertex();
      while (currentVertex != null) {
        if (typeCollection.acceptsType(currentVertex.getAttributedElementClass())) {
          resultSet = resultSet.plus(currentVertex);
        }
        currentVertex = currentVertex.getNextVertex();
      }
      if (GreqlEvaluator.VERTEX_INDEXING) {
        if ((System.currentTimeMillis() - startTime) > greqlEvaluator.getIndexTimeBarrier()) {
          GreqlEvaluator.addVertexIndex(datagraph, indexKey, resultSet);
        }
      }
    }
    return resultSet;
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsVertexSetExpression(this, graphSize);
  }

  @Override public long calculateEstimatedCardinality(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCardinalityVertexSetExpression(this, graphSize);
  }
}