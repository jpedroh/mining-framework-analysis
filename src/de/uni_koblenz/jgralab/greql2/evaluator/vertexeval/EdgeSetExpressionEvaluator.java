package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PSet;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
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
  public EdgeSetExpressionEvaluator(EdgeSetExpression vertex, Query query) {
    super(vertex, query);
  }

  @Override public PSet<Edge> evaluate(InternalGreqlEvaluator evaluator) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    Graph datagraph = greqlEvaluator.getDatagraph();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeSetExpressionEvaluator.java/right.java

    PSet<Edge> resultSet = JGraLab.set();
    Edge currentEdge = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeSetExpressionEvaluator.java/left.java
    query.getQueryGraph().getFirstEdge()
=======
    datagraph.getFirstEdge()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeSetExpressionEvaluator.java/right.java
    ;
    TypeCollection typeCollection = getTypeCollection(evaluator);
    while (currentEdge != null) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeSetExpressionEvaluator.java/left.java
      AttributedElementClass
=======
      EdgeClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeSetExpressionEvaluator.java/right.java
       edgeClass = currentEdge.getAttributedElementClass();
      if (typeCollection.acceptsType(edgeClass)) {
        resultSet = resultSet.plus(currentEdge);
      }
      currentEdge = currentEdge.getNextEdge();
    }
    return resultSet;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCostsEdgeSetExpression(this, graphSize);
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeSetExpressionEvaluator.java/right.java
}