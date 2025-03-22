package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Aggregation;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;
import de.uni_koblenz.jgralab.greql2.schema.ThisEdge;

/**
 * Evaluates a ThisEdge vertex in the GReQL-2 Syntaxgraph.
 *
 * @author ist@uni-koblenz.de
 *
 */
public class ThisEdgeEvaluator extends VariableEvaluator {
  /**
	 * @param eval
	 *            the GreqlEvaluator this VertexEvaluator belongs to
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
  public ThisEdgeEvaluator(ThisEdge vertex, GreqlEvaluator eval) {
    super(vertex, eval);
  }

  @Override protected List<VertexEvaluator> calculateDependingExpressions() {
    Queue<Greql2Vertex> queue = new LinkedList<Greql2Vertex>();
    List<VertexEvaluator> dependingEvaluators = new ArrayList<VertexEvaluator>();
    queue.add(vertex);
    while (!queue.isEmpty()) {
      Greql2Vertex currentVertex = queue.poll();
      VertexEvaluator eval = vertexEvalMarker.getMark(currentVertex);
      if ((eval != null) && (!dependingEvaluators.contains(eval)) && (!(eval instanceof PathDescriptionEvaluator)) && (!(eval instanceof DeclarationEvaluator)) && (!(eval instanceof SimpleDeclarationEvaluator))) {
        dependingEvaluators.add(eval);
      }
      Greql2Aggregation currentEdge = currentVertex.getFirstGreql2AggregationIncidence(EdgeDirection.OUT);
      while (currentEdge != null) {
        Greql2Vertex nextVertex = (Greql2Vertex) currentEdge.getThat();
        if (!(nextVertex instanceof PathDescription)) {
          queue.add(nextVertex);
        }
        currentEdge = currentEdge.getNextGreql2AggregationIncidence(EdgeDirection.OUT);
      }
    }
    return dependingEvaluators;
  }
}