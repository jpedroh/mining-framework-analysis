package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Aggregation;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;
import de.uni_koblenz.jgralab.greql2.schema.ThisVertex;

/**
 * Evaluates a Variable vertex in the GReQL-2 Syntaxgraph. Provides access to
 * the variable value using the method getResult(..), because it should make no
 * difference for other VertexEvaluators, if a vertex is root of a complex
 * subgraph or a variable. Also provides a method to set the variable value.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ThisVertexEvaluator extends VariableEvaluator<ThisVertex> {
  /**
	 * @param eval
	 *            the GreqlEvaluator this VertexEvaluator belongs to
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
  public ThisVertexEvaluator(ThisVertex vertex, QueryImpl query) {
    super(vertex, query);
  }

  @SuppressWarnings(value = { "unchecked" }) @Override protected List<VertexEvaluator<? extends Expression>> calculateDependingExpressions() {
    Queue<Greql2Vertex> queue = new LinkedList<Greql2Vertex>();
    List<VertexEvaluator<? extends Expression>> dependingEvaluators = new ArrayList<VertexEvaluator<? extends Expression>>();
    queue.add(vertex);
    while (!queue.isEmpty()) {
      Greql2Vertex currentVertex = queue.poll();
      VertexEvaluator<?> eval = query.getVertexEvaluator(currentVertex);
      if ((eval != null) && (!dependingEvaluators.contains(eval)) && (!(eval instanceof PathDescriptionEvaluator)) && (!(eval instanceof DeclarationEvaluator)) && (!(eval instanceof SimpleDeclarationEvaluator))) {
        dependingEvaluators.add((VertexEvaluator<? extends Expression>) eval);
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