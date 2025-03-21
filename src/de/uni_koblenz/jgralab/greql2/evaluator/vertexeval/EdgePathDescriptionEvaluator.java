package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.EdgePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.IsEdgeRestrOf;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

/**
 * Creates a NFA wich accepts a single edge out of the --edge-> - clause
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgePathDescriptionEvaluator extends PrimaryPathDescriptionEvaluator<EdgePathDescription> {
  public EdgePathDescriptionEvaluator(EdgePathDescription vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    Edge evalEdge = vertex.getFirstIsEdgeExprOfIncidence();
    VertexEvaluator<? extends Expression> edgeEval = null;
    if (evalEdge != null) {
      edgeEval = query.getVertexEvaluator((Expression) evalEdge.getAlpha());
    }
    TypeCollection typeCollection = new TypeCollection();
    IsEdgeRestrOf inc = vertex.getFirstIsEdgeRestrOfIncidence(EdgeDirection.IN);
    EdgeRestrictionEvaluator edgeRestEval = null;
    VertexEvaluator<? extends Expression> predicateEvaluator = null;
    if (inc != null) {
      edgeRestEval = (EdgeRestrictionEvaluator) query.getVertexEvaluator(inc.getAlpha());
      typeCollection.addTypes(edgeRestEval.getTypeCollection(evaluator));
      predicateEvaluator = edgeRestEval.getPredicateEvaluator();
    }
    createdNFA = NFA.createEdgePathDescriptionNFA(getEdgeDirection(vertex), typeCollection, getEdgeRoles(edgeRestEval), edgeEval, predicateEvaluator, query);
    return createdNFA;
  }
}