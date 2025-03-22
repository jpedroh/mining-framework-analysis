package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;
import de.uni_koblenz.jgralab.greql2.funlib.graph.ReachableVertices;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.ForwardVertexSet;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * Evaluates a ForwardVertexSet
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ForwardVertexSetEvaluator extends PathSearchEvaluator {
  private ForwardVertexSet vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  public ForwardVertexSetEvaluator(ForwardVertexSet vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  private boolean initialized = false;

  private VertexEvaluator startEval = null;

  private final void initialize() {
    PathDescription p = (PathDescription) vertex.getFirstIsPathOfIncidence(EdgeDirection.IN).getAlpha();
    PathDescriptionEvaluator pathDescEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(p);
    Expression startExpression = (Expression) vertex.getFirstIsStartExprOfIncidence(EdgeDirection.IN).getAlpha();
    startEval = vertexEvalMarker.getMark(startExpression);
    searchAutomaton = new DFA(pathDescEval.getNFA());
    initialized = true;
  }

  @Override public Object evaluate() {
    if (!initialized) {
      initialize();
    }
    Vertex startVertex = null;
    startVertex = (Vertex) startEval.getResult();
    return ReachableVertices.search(startVertex, searchAutomaton);
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsForwardVertexSet(this, graphSize);
  }

  @Override public long calculateEstimatedCardinality(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCardinalityForwardVertexSet(this, graphSize);
  }
}