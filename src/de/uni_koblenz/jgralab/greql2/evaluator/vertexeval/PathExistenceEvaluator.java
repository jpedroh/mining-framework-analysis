package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib.FunctionInfo;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;
import de.uni_koblenz.jgralab.greql2.schema.PathExistence;

/**
 * Evaluates a path existence, that's the question if there is a path of a
 * specific regular form form startVertex to targetVertex
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class PathExistenceEvaluator extends PathSearchEvaluator<PathExistence> {
  private FunctionInfo fi;


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/right.java


  public PathExistenceEvaluator(PathExistence vertex, Query query) {
    super(vertex, query);
  }

  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    PathDescription p = (PathDescription) vertex.getFirstIsPathOfIncidence(EdgeDirection.IN).getAlpha();
    PathDescriptionEvaluator<?> pathDescEval = (PathDescriptionEvaluator<?>) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/left.java
    getVertexEvaluator(p)
=======
    getMark(p)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/right.java
    ;
    Expression startExpression = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/left.java
    vertex.getFirstIsStartExprOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Expression) vertex.getFirstIsStartExprOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/right.java
    ;
    VertexEvaluator<? extends Expression> startEval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/left.java
    getVertexEvaluator(startExpression)
=======
    getMark(startExpression)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/right.java
    ;
    Object res = startEval.getResult(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/left.java
    evaluator
=======
>>>>>>> Unknown file: This is a bug in JDime.
    );
    if (res == null) {
      return null;
    }
    Vertex startVertex = (Vertex) res;
    Expression targetExpression = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/left.java
    vertex.getFirstIsTargetExprOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Expression) vertex.getFirstIsTargetExprOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/right.java
    ;
    VertexEvaluator<? extends Expression> targetEval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/left.java
    getVertexEvaluator(targetExpression)
=======
    getMark(targetExpression)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/right.java
    ;
    Vertex targetVertex = null;
    res = targetEval.getResult(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathExistenceEvaluator.java/left.java
    evaluator
=======
>>>>>>> Unknown file: This is a bug in JDime.
    );
    if (res == null) {
      return null;
    }
    targetVertex = (Vertex) res;
    if (searchAutomaton == null) {
      searchAutomaton = pathDescEval.getNFA(evaluator).getDFA();
    }
    Object[] arguments = new Object[3];
    arguments[0] = startVertex;
    arguments[1] = targetVertex;
    arguments[2] = searchAutomaton;
    if (fi == null) {
      fi = FunLib.getFunctionInfo("isReachable");
    }
    return FunLib.apply(fi, arguments);
  }
}