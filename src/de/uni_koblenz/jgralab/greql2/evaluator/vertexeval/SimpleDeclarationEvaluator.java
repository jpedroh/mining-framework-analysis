package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PVector;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeExprOf;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.Variable;

/**
 * Evaluates a simple declaration. Creates a VariableDeclaration-object, that
 * provides methods to iterate over all possible values.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class SimpleDeclarationEvaluator extends VertexEvaluator<SimpleDeclaration> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimpleDeclarationEvaluator.java/right.java


  /**
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
  public SimpleDeclarationEvaluator(SimpleDeclaration vertex, Query query) {
    super(vertex, query);
  }

  /**
	 * returns a JValueList of VariableDeclaration objects
	 */
  @Override public PVector<VariableDeclaration> evaluate(InternalGreqlEvaluator evaluator) {
    IsTypeExprOf inc = vertex.getFirstIsTypeExprOfIncidence(EdgeDirection.IN);
    Expression typeExpression = inc.getAlpha();
    VertexEvaluator<? extends Expression> exprEval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimpleDeclarationEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimpleDeclarationEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimpleDeclarationEvaluator.java/left.java
    getVertexEvaluator(typeExpression)
=======
    getMark(typeExpression)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimpleDeclarationEvaluator.java/right.java
    ;
    PVector<VariableDeclaration> varDeclList = JGraLab.vector();
    IsDeclaredVarOf varInc = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimpleDeclarationEvaluator.java/left.java
    vertex.getFirstIsDeclaredVarOfIncidence(EdgeDirection.IN)
=======
    vertex.getFirstIsDeclaredVarOfIncidence(EdgeDirection.IN)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimpleDeclarationEvaluator.java/right.java
    ;
    while (varInc != null) {
      VariableDeclaration varDecl = new VariableDeclaration(varInc.getAlpha(), exprEval, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimpleDeclarationEvaluator.java/left.java
      (VariableEvaluator<Variable>) query.getVertexEvaluator(varInc.getAlpha())
=======
      (Variable) varInc.getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimpleDeclarationEvaluator.java/right.java
      , exprEval, vertex, greqlEvaluator);
      varDeclList = varDeclList.plus(varDecl);
      varInc = varInc.getNextIsDeclaredVarOfIncidence(EdgeDirection.IN);
    }
    return varDeclList;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public void calculateNeededAndDefinedVariables() {
    neededVariables = new HashSet<Variable>();
    definedVariables = new HashSet<Variable>();
    IsDeclaredVarOf varInc = vertex.getFirstIsDeclaredVarOfIncidence(EdgeDirection.IN);
    while (varInc != null) {
      definedVariables.add((Variable) varInc.getAlpha());
      varInc = varInc.getNextIsDeclaredVarOfIncidence(EdgeDirection.IN);
    }
    IsTypeExprOf typeInc = vertex.getFirstIsTypeExprOfIncidence(EdgeDirection.IN);
    if (typeInc != null) {
      VertexEvaluator veval = vertexEvalMarker.getMark(typeInc.getAlpha());
      if (veval != null) {
        neededVariables.addAll(veval.getNeededVariables());
      }
    }
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SimpleDeclarationEvaluator.java/right.java
}