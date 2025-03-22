package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PSet;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.funlib.graph.ReachableVertices;
import de.uni_koblenz.jgralab.greql2.schema.BackwardVertexSet;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * evaluates a BackwardVertexSet
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class BackwardVertexSetEvaluator extends PathSearchEvaluator {
  BackwardVertexSet vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  public BackwardVertexSetEvaluator(BackwardVertexSet vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  private boolean initialized = false;

  private VertexEvaluator targetEval = null;

  private final void initialize() {
    PathDescription p = (PathDescription) vertex.getFirstIsPathOfIncidence(EdgeDirection.IN).getAlpha();
    PathDescriptionEvaluator pathDescEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(p);
    Expression targetExpression = (Expression) vertex.getFirstIsTargetExprOfIncidence(EdgeDirection.IN).getAlpha();
    targetEval = vertexEvalMarker.getMark(targetExpression);
    NFA revertedNFA = NFA.revertNFA(pathDescEval.getNFA());
    searchAutomaton = new DFA(revertedNFA);
    initialized = true;
  }

  @Override public PSet<Vertex> evaluate() {
    if (!initialized) {
      initialize();
    }
    Vertex targetVertex = null;
    targetVertex = (Vertex) targetEval.getResult();
    return ReachableVertices.search(targetVertex, searchAutomaton);
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCostsBackwardVertexSet(this, graphSize);
  }

  @Override public long calculateEstimatedCardinality(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCardinalityBackwardVertexSet(this, graphSize);
  }
}