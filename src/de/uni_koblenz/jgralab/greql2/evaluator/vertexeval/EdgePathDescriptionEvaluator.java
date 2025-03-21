package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.EdgePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeRestrOfExpression;
import de.uni_koblenz.jgralab.greql2.schema.IsEdgeRestrOf;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

/**
 * Creates a NFA wich accepts a single edge out of the --edge-> - clause
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgePathDescriptionEvaluator extends PrimaryPathDescriptionEvaluator<EdgePathDescription> {
  public EdgePathDescriptionEvaluator(EdgePathDescription vertex, Query query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    Edge evalEdge = vertex.getFirstIsEdgeExprOfIncidence();
    VertexEvaluator<? extends Expression> edgeEval = null;
    if (evalEdge != null) {
      edgeEval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
      query
=======
      vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
      .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
      getVertexEvaluator((Expression) evalEdge.getAlpha())
=======
      getMark(evalEdge.getAlpha())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
      ;
    }
    TypeCollection typeCollection = new TypeCollection();

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
    IsEdgeRestrOf
=======
    IsTypeRestrOfExpression
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
     inc = vertex.
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
    getFirstIsEdgeRestrOfIncidence(EdgeDirection.IN)
=======
    getFirstIsTypeRestrOfExpressionIncidence(EdgeDirection.IN)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
    ;
    EdgeRestrictionEvaluator edgeRestEval = null;

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
    VertexEvaluator<? extends Expression> predicateEvaluator = null;
=======
    VertexEvaluator predicateEvaluator = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java

    if (inc != null) {
      edgeRestEval = (EdgeRestrictionEvaluator) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
      query
=======
      vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
      .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
      getVertexEvaluator(inc.getAlpha())
=======
      getMark(inc.getAlpha())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
      ;
      typeCollection.addTypes(edgeRestEval.getTypeCollection(evaluator));
      predicateEvaluator = edgeRestEval.getPredicateEvaluator();
    }
    createdNFA = NFA.createEdgePathDescriptionNFA(getEdgeDirection(vertex), typeCollection, getEdgeRoles(edgeRestEval), edgeEval, predicateEvaluator, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
    );
    return createdNFA;
  }
}