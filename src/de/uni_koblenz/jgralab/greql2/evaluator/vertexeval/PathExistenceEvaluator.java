package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
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
public class PathExistenceEvaluator extends PathSearchEvaluator {
  /**
	 * this is the PathExistence vertex in the GReQL Syntaxgraph this evaluator
	 * evaluates
	 */
  private PathExistence vertex;

  private FunctionInfo fi;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  public PathExistenceEvaluator(PathExistence vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  @Override public Object evaluate() {
    PathDescription p = (PathDescription) vertex.getFirstIsPathOfIncidence(EdgeDirection.IN).getAlpha();
    PathDescriptionEvaluator pathDescEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(p);
    Expression startExpression = (Expression) vertex.getFirstIsStartExprOfIncidence(EdgeDirection.IN).getAlpha();
    VertexEvaluator startEval = vertexEvalMarker.getMark(startExpression);
    Object res = startEval.getResult();
    if (res == null) {
      return null;
    }
    Vertex startVertex = (Vertex) res;
    Expression targetExpression = (Expression) vertex.getFirstIsTargetExprOfIncidence(EdgeDirection.IN).getAlpha();
    VertexEvaluator targetEval = vertexEvalMarker.getMark(targetExpression);
    Vertex targetVertex = null;
    res = targetEval.getResult();
    if (res == null) {
      return null;
    }
    targetVertex = (Vertex) res;
    if (searchAutomaton == null) {
      searchAutomaton = pathDescEval.getNFA().getDFA();
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

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsPathExistence(this, graphSize);
  }

  @Override public double calculateEstimatedSelectivity(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateSelectivityPathExistence(this, graphSize);
  }
}