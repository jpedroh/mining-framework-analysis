package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.exception.UndefinedVariableException;
import de.uni_koblenz.jgralab.greql2.exception.UnknownTypeException;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Expression;
import de.uni_koblenz.jgralab.greql2.schema.Identifier;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsIdOf;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;
import de.uni_koblenz.jgralab.greql2.schema.Variable;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * Evaluates a Greql2Expression vertex in the GReQL-2 Syntaxgraph. A
 * GReQL2-Expression is the rootvertex of the GReQL-2Syntaxgraph. It contains
 * the bound/free variables, that are defined via "using" and binds them to the
 * values in the variableMap of the Greql2Evaluator.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class Greql2ExpressionEvaluator extends VertexEvaluator<Greql2Expression> {
  private void initializeBoundVariables(InternalGreqlEvaluator evaluator) {
    IsBoundVarOf inc = vertex.getFirstIsBoundVarOfIncidence(EdgeDirection.IN);
    while (inc != null) {
      Variable currentBoundVariable = inc.getAlpha();
      Object variableValue = evaluator.getVariable(currentBoundVariable.get_name());
      if (variableValue == null) {
        throw new UndefinedVariableException(currentBoundVariable, createSourcePositions(inc));
      }
      VariableEvaluator<Variable> variableEval = (VariableEvaluator<Variable>) query.getVertexEvaluator(currentBoundVariable);
      variableEval.setValue(variableValue, evaluator);
      inc = inc.getNextIsBoundVarOfIncidence(EdgeDirection.IN);
    }
  }

  /**
	 * @param eval
	 *            the GreqlEvaluator this VertexEvaluator belongs to
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
  public Greql2ExpressionEvaluator(Greql2Expression vertex, QueryImpl query) {
    super(vertex, query);
  }

  /**
	 * sets the values of all bound variables and evaluates the queryexpression
	 */
  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    initializeBoundVariables(evaluator);
    Schema graphSchema = evaluator.getSchemaOfDataGraph();
    if (vertex.get_importedTypes() != null && graphSchema != null) {
      for (String importedType : vertex.get_importedTypes()) {
        if (importedType.endsWith(".*")) {
          String packageName = importedType.substring(0, importedType.length() - 2);
          Package p = graphSchema.getPackage(packageName);
          if (p == null) {
            throw new UnknownTypeException(packageName, new ArrayList<SourcePosition>());
          }
          for (VertexClass elem : p.getVertexClasses().values()) {
            query.addKnownType(elem);
          }
          for (EdgeClass elem : p.getEdgeClasses().values()) {
            query.addKnownType(elem);
          }
        } else {
          GraphElementClass<?, ?> elemClass = graphSchema.getGraphClass().getGraphElementClass(importedType);
          if (elemClass == null) {
            throw new UnknownTypeException(importedType, new ArrayList<SourcePosition>());
          }
          query.addKnownType(elemClass);
        }
      }
    }
    Expression boundExpression = vertex.getFirstIsQueryExprOfIncidence(EdgeDirection.IN).getAlpha();
    VertexEvaluator<? extends Expression> eval = query.getVertexEvaluator(boundExpression);
    Object result = eval.getResult(evaluator);
    IsIdOf storeInc = vertex.getFirstIsIdOfIncidence(EdgeDirection.IN);
    if (storeInc != null) {
      VertexEvaluator<Identifier> storeEval = query.getVertexEvaluator(storeInc.getAlpha());
      String varName = storeEval.getResult(evaluator).toString();
      evaluator.setVariable(varName, result);
    }
    return result;
  }
}