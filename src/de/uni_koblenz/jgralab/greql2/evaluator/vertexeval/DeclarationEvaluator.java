package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import java.util.List;
import org.pcollections.PVector;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclaration;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclarationLayer;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsConstraintOf;
import de.uni_koblenz.jgralab.greql2.schema.IsSimpleDeclOf;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.Variable;

/**
 * Evaluates a Declaration vertex in the GReQL-2 Syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class DeclarationEvaluator extends VertexEvaluator<Declaration> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/right.java


  /**
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
  public DeclarationEvaluator(Declaration vertex, Query query) {
    super(vertex, query);
  }

  @Override public VariableDeclarationLayer evaluate(InternalGreqlEvaluator evaluator) {
    ArrayList<VertexEvaluator<? extends Expression>> constraintList = new ArrayList<VertexEvaluator<? extends Expression>>();
    for (IsConstraintOf consInc : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/left.java
    vertex.getIsConstraintOfIncidences(EdgeDirection.IN)
=======
    vertex.getIsConstraintOfIncidences(EdgeDirection.IN)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/left.java
      VertexEvaluator<? extends Expression> curEval = query.getVertexEvaluator(consInc.getAlpha());
=======
      VertexEvaluator curEval = vertexEvalMarker.getMark(consInc.getAlpha());
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/right.java

      if (curEval != null) {
        constraintList.add(curEval);
      }
    }
    List<VariableDeclaration> varDeclList = new ArrayList<VariableDeclaration>();
    for (IsSimpleDeclOf inc : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/left.java
    vertex.getIsSimpleDeclOfIncidences(EdgeDirection.IN)
=======
    vertex.getIsSimpleDeclOfIncidences(EdgeDirection.IN)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/right.java
    ) {
      SimpleDeclaration simpleDecl = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/left.java
      inc.getAlpha()
=======
      (SimpleDeclaration) inc.getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/right.java
      ;
      SimpleDeclarationEvaluator simpleDeclEval = (SimpleDeclarationEvaluator) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/left.java
      query
=======
      vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/right.java
      .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/left.java
      getVertexEvaluator(simpleDecl)
=======
      getMark(simpleDecl)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/right.java
      ;
      @SuppressWarnings(value = { "unchecked" }) PVector<VariableDeclaration> resultCollection = (PVector<VariableDeclaration>) simpleDeclEval.getResult(evaluator);
      for (VariableDeclaration v : resultCollection) {
        varDeclList.add(v);
      }
    }
    VariableDeclarationLayer declarationLayer = new VariableDeclarationLayer(vertex, varDeclList, constraintList);
    return declarationLayer;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * Returns the number of combinations of the variables this vertex defines
	 */
  public long getDefinedVariableCombinations(GraphSize graphSize) {
    long combinations = 1;
    Iterator<Variable> iter = getDefinedVariables().iterator();
    while (iter.hasNext()) {
      VariableEvaluator veval = (VariableEvaluator) vertexEvalMarker.getMark(iter.next());
      combinations *= veval.getVariableCombinations(graphSize);
    }
    return combinations;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/DeclarationEvaluator.java/right.java
}