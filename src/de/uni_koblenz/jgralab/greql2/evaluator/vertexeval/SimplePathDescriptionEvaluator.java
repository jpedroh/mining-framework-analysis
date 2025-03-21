package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
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
  public SimplePathDescriptionEvaluator(SimplePathDescription vertex, Query query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    TypeCollection typeCollection = new TypeCollection();
    EdgeRestrictionEvaluator edgeRestEval = null;

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimplePathDescriptionEvaluator.java/left.java
    VertexEvaluator<? extends Expression> predicateEvaluator = null;
=======
    VertexEvaluator predicateEvaluator = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimplePathDescriptionEvaluator.java/right.java

    for (IsEdgeRestrOf inc : vertex.getIsEdgeRestrOfIncidences(EdgeDirection.IN)) {
      edgeRestEval = (EdgeRestrictionEvaluator) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimplePathDescriptionEvaluator.java/left.java
      query
=======
      vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimplePathDescriptionEvaluator.java/right.java
      .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimplePathDescriptionEvaluator.java/left.java
      getVertexEvaluator(inc.getAlpha())
=======
      getMark(inc.getAlpha())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimplePathDescriptionEvaluator.java/right.java
      ;
      typeCollection.addTypes(edgeRestEval.getTypeCollection(evaluator));
      predicateEvaluator = edgeRestEval.getPredicateEvaluator();
    }
    createdNFA = NFA.createSimplePathDescriptionNFA(getEdgeDirection(vertex), typeCollection, getEdgeRoles(edgeRestEval), predicateEvaluator, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimplePathDescriptionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimplePathDescriptionEvaluator.java/right.java
    );
    return createdNFA;
  }
}