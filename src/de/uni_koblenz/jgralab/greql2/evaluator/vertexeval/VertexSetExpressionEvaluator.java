package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PSet;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
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
public class VertexSetExpressionEvaluator extends ElementSetExpressionEvaluator<VertexSetExpression> {
  /**
	 * Creates a new ElementSetExpressionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public VertexSetExpressionEvaluator(VertexSetExpression vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    TypeCollection typeCollection = getTypeCollection(evaluator);
    PSet<Vertex> resultSet = null;
    if (resultSet == null) {
      resultSet = JGraLab.set();
      Vertex currentVertex = query.getQueryGraph().getFirstVertex();
      while (currentVertex != null) {
        if (typeCollection.acceptsType(currentVertex.getAttributedElementClass())) {
          resultSet = resultSet.plus(currentVertex);
        }
        currentVertex = currentVertex.getNextVertex();
      }
    }
    return resultSet;
  }
}