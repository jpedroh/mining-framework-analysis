package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PSet;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
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
  public VertexSetExpressionEvaluator(VertexSetExpression vertex, Query query) {
    super(vertex, query);
  }

  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    Graph datagraph = greqlEvaluator.getDatagraph();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/VertexSetExpressionEvaluator.java/right.java

    TypeCollection typeCollection = getTypeCollection(evaluator);
    PSet<Vertex> resultSet = null;

<<<<<<< Unknown file: This is a bug in JDime.
=======
    if (GreqlEvaluator.VERTEX_INDEXING) {
      indexKey = typeCollection.toString();
      resultSet = GreqlEvaluator.getVertexIndex(datagraph, indexKey);
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/VertexSetExpressionEvaluator.java/right.java

    if (resultSet == null) {
      resultSet = JGraLab.set();
      Vertex currentVertex = query.getQueryGraph().getFirstVertex();
      while (currentVertex != null) {
        if (typeCollection.acceptsType(currentVertex.getAttributedElementClass())) {
          resultSet = resultSet.plus(currentVertex);
        }
        currentVertex = currentVertex.getNextVertex();
      }

<<<<<<< Unknown file: This is a bug in JDime.
=======
      if (GreqlEvaluator.VERTEX_INDEXING) {
        if ((System.currentTimeMillis() - startTime) > greqlEvaluator.getIndexTimeBarrier()) {
          GreqlEvaluator.addVertexIndex(datagraph, indexKey, resultSet);
        }
      }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/VertexSetExpressionEvaluator.java/right.java
    }
    return resultSet;
  }
}