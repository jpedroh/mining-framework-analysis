package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import java.util.Map;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.UndefinedVariableException;
import de.uni_koblenz.jgralab.greql2.exception.UnknownTypeException;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsIdOf;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;
import de.uni_koblenz.jgralab.greql2.schema.Variable;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
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
public class Greql2ExpressionEvaluator extends VertexEvaluator {
  /**
	 * The Greql2Expression-Vertex this evaluator evaluates
	 */
  private Greql2Expression vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  /**
	 * The varibles that are defined via the <code>using</code> clause. They are
	 * called bound or also free variables
	 */
  private Map<String, Object> boundVariables;

  boolean boundVariablesChanged = true;

  protected void setBoundVariables(Map<String, Object> boundVariables) {
    this.boundVariables = boundVariables;
    result = null;
    boundVariablesChanged = true;
  }

  private void initializeBoundVariables() {
    IsBoundVarOf inc = vertex.getFirstIsBoundVarOfIncidence(EdgeDirection.IN);
    while (inc != null) {
      Variable currentBoundVariable = inc.getAlpha();
      Object variableValue = boundVariables.get(currentBoundVariable.get_name());
      if (variableValue == null) {
        throw new UndefinedVariableException(currentBoundVariable, createSourcePositions(inc));
      }
      VariableEvaluator variableEval = (VariableEvaluator) vertexEvalMarker.getMark(currentBoundVariable);
      variableEval.setValue(variableValue);
      inc = inc.getNextIsBoundVarOfIncidence(EdgeDirection.IN);
    }
  }

  /**
	 * @param eval
	 *            the GreqlEvaluator this VertexEvaluator belongs to
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
  public Greql2ExpressionEvaluator(Greql2Expression vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
    boundVariables = eval.getVariables();
    boundVariablesChanged = true;
  }

  /**
	 * sets the values of all bound variables and evaluates the queryexpression
	 */
  @Override public Object evaluate() {
    if (boundVariablesChanged) {
      initializeBoundVariables();
      boundVariablesChanged = false;
    }
    if (vertex.get_importedTypes() != null && graph != null) {
      Schema graphSchema = graph.getSchema();
      for (String importedType : vertex.get_importedTypes()) {
        if (importedType.endsWith(".*")) {
          String packageName = importedType.substring(0, importedType.length() - 2);
          Package p = graphSchema.getPackage(packageName);
          if (p == null) {
            throw new UnknownTypeException(packageName, new ArrayList<SourcePosition>());
          }
          for (VertexClass elem : p.getVertexClasses().values()) {
            greqlEvaluator.addKnownType(elem);
          }
          for (EdgeClass elem : p.getEdgeClasses().values()) {
            greqlEvaluator.addKnownType(elem);
          }
        } else {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/Greql2ExpressionEvaluator.java/left.java
          AttributedElementClass
=======
          GraphElementClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/Greql2ExpressionEvaluator.java/right.java
          <?, ?> elemClass = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/Greql2ExpressionEvaluator.java/left.java
          graphSchema.getAttributedElementClass(importedType)
=======
          graphSchema.getGraphClass().getGraphElementClass(importedType)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/Greql2ExpressionEvaluator.java/right.java
          ;
          if (elemClass == null) {
            throw new UnknownTypeException(importedType, new ArrayList<SourcePosition>());
          }
          greqlEvaluator.addKnownType(elemClass);
        }
      }
    }
    Expression boundExpression = vertex.getFirstIsQueryExprOfIncidence(EdgeDirection.IN).getAlpha();
    VertexEvaluator eval = vertexEvalMarker.getMark(boundExpression);
    Object result = eval.getResult();
    IsIdOf storeInc = vertex.getFirstIsIdOfIncidence(EdgeDirection.IN);
    if (storeInc != null) {
      VertexEvaluator storeEval = vertexEvalMarker.getMark(storeInc.getAlpha());
      String varName = storeEval.getResult().toString();
      boundVariables.put(varName, result);
    }
    return result;
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCostsGreql2Expression(this, graphSize);
  }
}