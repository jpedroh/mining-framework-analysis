package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.EdgePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeRestrOfExpression;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

/**
 * Creates a NFA wich accepts a single edge out of the --edge-> - clause
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgePathDescriptionEvaluator extends PrimaryPathDescriptionEvaluator {
  public EdgePathDescriptionEvaluator(EdgePathDescription vertex, GreqlEvaluator eval) {
    super(vertex, eval);
  }

  @Override public NFA evaluate() {
    Edge evalEdge = vertex.getFirstIsEdgeExprOfIncidence();
    VertexEvaluator edgeEval = null;
    if (evalEdge != null) {
      edgeEval = vertexEvalMarker.getMark(evalEdge.getAlpha());
    }
    TypeCollection typeCollection = new TypeCollection();
    IsTypeRestrOfExpression inc = vertex.getFirstIsTypeRestrOfExpressionIncidence(EdgeDirection.IN);
    EdgeRestrictionEvaluator edgeRestEval = null;
    VertexEvaluator predicateEvaluator = null;
    if (inc != null) {
      edgeRestEval = (EdgeRestrictionEvaluator) vertexEvalMarker.getMark(inc.getAlpha());
      typeCollection.addTypes(edgeRestEval.getTypeCollection());
      predicateEvaluator = edgeRestEval.getPredicateEvaluator();
    }
    createdNFA = NFA.createEdgePathDescriptionNFA(getEdgeDirection(vertex), typeCollection, getEdgeRoles(edgeRestEval), edgeEval, predicateEvaluator, vertexEvalMarker);
    return createdNFA;
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsEdgePathDescription(this, graphSize);
  }
}