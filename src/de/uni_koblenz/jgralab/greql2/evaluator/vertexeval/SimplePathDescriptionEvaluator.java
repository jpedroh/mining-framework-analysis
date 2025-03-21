package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.IsEdgeRestrOf;
import de.uni_koblenz.jgralab.greql2.schema.SimplePathDescription;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

/**
 * Evaluates a SimplePathDescription, that is something link v -->{isExprOf} w.
 * Creates a NFA which accepts the simplePath the vertex to evaluate describes.
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public class SimplePathDescriptionEvaluator extends PrimaryPathDescriptionEvaluator<SimplePathDescription> {
  public SimplePathDescriptionEvaluator(SimplePathDescription vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    TypeCollection typeCollection = new TypeCollection();
    EdgeRestrictionEvaluator edgeRestEval = null;
    VertexEvaluator<? extends Expression> predicateEvaluator = null;
    for (IsEdgeRestrOf inc : vertex.getIsEdgeRestrOfIncidences(EdgeDirection.IN)) {
      edgeRestEval = (EdgeRestrictionEvaluator) query.getVertexEvaluator(inc.getAlpha());
      typeCollection.addTypes(edgeRestEval.getTypeCollection(evaluator));
      predicateEvaluator = edgeRestEval.getPredicateEvaluator();
    }
    createdNFA = NFA.createSimplePathDescriptionNFA(getEdgeDirection(vertex), typeCollection, getEdgeRoles(edgeRestEval), predicateEvaluator, query);
    return createdNFA;
  }
}