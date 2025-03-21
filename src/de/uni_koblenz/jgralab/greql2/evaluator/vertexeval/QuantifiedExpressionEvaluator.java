package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclarationLayer;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
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

  private VertexEvaluator<? extends Expression> predicateEvaluator = null;

  /**
	 * @param eval
	 *            the GreqlEvaluator this VertexEvaluator belongs to
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
  public QuantifiedExpressionEvaluator(QuantifiedExpression vertex, QueryImpl query) {
    super(vertex, query);
  }

  private void initialize(InternalGreqlEvaluator evaluator) {
    Declaration d = vertex.getFirstIsQuantifiedDeclOfIncidence(EdgeDirection.IN).getAlpha();
    DeclarationEvaluator declEval = (DeclarationEvaluator) query.getVertexEvaluator(d);
    declarationLayer = (VariableDeclarationLayer) declEval.getResult(evaluator);
    Quantifier quantifier = vertex.getFirstIsQuantifierOfIncidence(EdgeDirection.IN).getAlpha();
    quantificationType = quantifier.get_type();
    Expression b = vertex.getFirstIsBoundExprOfIncidence(EdgeDirection.IN).getAlpha();
    predicateEvaluator = query.getVertexEvaluator(b);
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
}