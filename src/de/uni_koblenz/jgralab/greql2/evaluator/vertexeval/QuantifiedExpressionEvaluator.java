package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclarationLayer;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.QuantificationType;
import de.uni_koblenz.jgralab.greql2.schema.QuantifiedExpression;
import de.uni_koblenz.jgralab.greql2.schema.Quantifier;

/**
 * Evaluates a QuantifiedExpression, a QuantifiedExpression is something like
 * "using FOO: exists s: FOO @ s = true".
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class QuantifiedExpressionEvaluator extends VertexEvaluator<QuantifiedExpression> {
  private VariableDeclarationLayer declarationLayer = null;

  private QuantificationType quantificationType = null;

  private boolean initialized = false;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/left.java
  private VertexEvaluator<? extends Expression> predicateEvaluator = null;
=======
  private VertexEvaluator predicateEvaluator = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/right.java


  /**
	 * @param eval
	 *            the GreqlEvaluator this VertexEvaluator belongs to
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
  public QuantifiedExpressionEvaluator(QuantifiedExpression vertex, Query query) {
    super(vertex, query);
  }

  private void initialize(InternalGreqlEvaluator evaluator) {
    Declaration d = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/left.java
    vertex.getFirstIsQuantifiedDeclOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Declaration) vertex.getFirstIsQuantifiedDeclOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/right.java
    ;
    DeclarationEvaluator declEval = (DeclarationEvaluator) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/left.java
    getVertexEvaluator(d)
=======
    getMark(d)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/right.java
    ;
    declarationLayer = (VariableDeclarationLayer) declEval.getResult(evaluator);
    Quantifier quantifier = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/left.java
    vertex.getFirstIsQuantifierOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Quantifier) vertex.getFirstIsQuantifierOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/right.java
    ;
    quantificationType = quantifier.get_type();
    Expression b = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/left.java
    vertex.getFirstIsBoundExprOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Expression) vertex.getFirstIsBoundExprOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/right.java
    ;
    predicateEvaluator = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/left.java
    getVertexEvaluator(b)
=======
    getMark(b)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/right.java
    ;
    initialized = true;
  }

  /**
	 * evaluates the QuantifiedEx
	 */
  @Override public Boolean evaluate(InternalGreqlEvaluator evaluator) {
    if (!initialized) {
      initialize(evaluator);
    }
    boolean foundTrue = false;
    declarationLayer.reset();
    switch (quantificationType) {
      case EXISTS:
      while (declarationLayer.iterate(null)) {
        Object tempResult = predicateEvaluator.getResult(evaluator);
        if (tempResult instanceof Boolean) {
          if ((Boolean) tempResult) {
            return Boolean.TRUE;
          }
        }
      }
      return Boolean.FALSE;
      case EXISTSONE:
      while (declarationLayer.iterate(null)) {
        Object tempResult = predicateEvaluator.getResult(evaluator);
        if (tempResult instanceof Boolean) {
          if ((Boolean) tempResult) {
            if (foundTrue == true) {
              return Boolean.FALSE;
            } else {
              foundTrue = true;
            }
          }
        }
      }
      if (foundTrue) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
      case FORALL:
      while (declarationLayer.iterate(null)) {
        Object tempResult = predicateEvaluator.getResult(evaluator);
        if (tempResult instanceof Boolean) {
          if (!(Boolean) tempResult) {
            return Boolean.FALSE;
          }
        }
      }
      return Boolean.TRUE;
      default:
      throw new RuntimeException("FIXME: Unhandled quantification type " + quantificationType);
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCostsQuantifiedExpression(this, graphSize);
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/QuantifiedExpressionEvaluator.java/right.java
}