package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib.FunctionInfo;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
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

  public PathExistenceEvaluator(PathExistence vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    PathDescription p = (PathDescription) vertex.getFirstIsPathOfIncidence(EdgeDirection.IN).getAlpha();
    PathDescriptionEvaluator<?> pathDescEval = (PathDescriptionEvaluator<?>) query.getVertexEvaluator(p);
    Expression startExpression = vertex.getFirstIsStartExprOfIncidence(EdgeDirection.IN).getAlpha();
    VertexEvaluator<? extends Expression> startEval = query.getVertexEvaluator(startExpression);
    Object res = startEval.getResult(evaluator);
    if (res == null) {
      return null;
    }
    Vertex startVertex = (Vertex) res;
    Expression targetExpression = vertex.getFirstIsTargetExprOfIncidence(EdgeDirection.IN).getAlpha();
    VertexEvaluator<? extends Expression> targetEval = query.getVertexEvaluator(targetExpression);
    Vertex targetVertex = null;
    res = targetEval.getResult(evaluator);
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