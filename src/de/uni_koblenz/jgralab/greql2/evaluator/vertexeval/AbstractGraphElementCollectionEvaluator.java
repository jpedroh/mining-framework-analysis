package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeRestrOfExpression;
import de.uni_koblenz.jgralab.greql2.schema.TypeId;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AbstractGraphElementCollectionEvaluator.java/left.java
/**
 * This class is the base class for all VertexEvaluators, that construct an
 * element collection, for instance EdgeSetExpressionEvaluator. But it is not
 * the base for Forward- or BackwardVertexSetEvaluator, because these are
 * PathSearchEvaluators.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class AbstractGraphElementCollectionEvaluator<V extends Expression> extends VertexEvaluator<V> {
  public AbstractGraphElementCollectionEvaluator(V vertex, Query query) {
    super(vertex, query);
  }

  private TypeCollection typeCollection = null;

  protected TypeCollection getTypeCollection(InternalGreqlEvaluator evaluator) {
    if (typeCollection == null) {
      typeCollection = new TypeCollection();
      IsTypeRestrOfExpression inc = ((Expression) getVertex()).getFirstIsTypeRestrOfExpressionIncidence(EdgeDirection.IN);
      while (inc != null) {
        if (inc.getAlpha() instanceof TypeId) {
          TypeIdEvaluator typeEval = (TypeIdEvaluator) query.getVertexEvaluator(inc.getAlpha());
          typeCollection.addTypes((TypeCollection) typeEval.getResult(evaluator));
        }
        inc = inc.getNextIsTypeRestrOfExpressionIncidence(EdgeDirection.IN);
      }
    }
    return typeCollection;
  }
}
=======
/**
 * This class is the base class for all VertexEvaluators, that construct an
 * element collection, for instance EdgeSetExpressionEvaluator. But it is not
 * the base for Forward- or BackwardVertexSetEvaluator, because these are
 * PathSearchEvaluators.
 *
 * @author ist@uni-koblenz.de  
 *
 */
public abstract class AbstractGraphElementCollectionEvaluator extends VertexEvaluator {
  public AbstractGraphElementCollectionEvaluator(GreqlEvaluator eval) {
    super(eval);
  }

  private TypeCollection typeCollection = null;

  protected TypeCollection getTypeCollection() {
    if (typeCollection == null) {
      typeCollection = new TypeCollection();
      IsTypeRestrOfExpression inc = ((Expression) getVertex()).getFirstIsTypeRestrOfExpressionIncidence(EdgeDirection.IN);
      while (inc != null) {
        if (inc.getAlpha() instanceof TypeId) {
          TypeIdEvaluator typeEval = (TypeIdEvaluator) vertexEvalMarker.getMark(inc.getAlpha());
          typeCollection.addTypes((TypeCollection) typeEval.getResult());
        }
        inc = inc.getNextIsTypeRestrOfExpressionIncidence(EdgeDirection.IN);
      }
    }
    return typeCollection;
  }
}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AbstractGraphElementCollectionEvaluator.java/right.java
