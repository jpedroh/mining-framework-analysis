package de.uni_koblenz.jgralab.greql2.evaluator.fa;
import org.pcollections.PCollection;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.serialising.GreqlSerializer;

public class IntermediateVertexTransition extends Transition {
  /**
	 * this transition may only fire, if the end-vertex of the edge e is part of
	 * the result of this VertexEvaluator
	 */
  public VertexEvaluator<?> intermediateVertexEvaluator;

  /**
	 * returns true if this transition and the given transition t accept the
	 * same edges
	 */
  @Override public boolean equalSymbol(Transition t) {
    if (!(t instanceof IntermediateVertexTransition)) {
      return false;
    }
    IntermediateVertexTransition vt = (IntermediateVertexTransition) t;
    if (intermediateVertexEvaluator != vt.intermediateVertexEvaluator) {
      return false;
    }
    return true;
  }

  /**
	 * creates a new transition, which accepts a intermediate vertex
	 * 
	 * @param start
	 *            state where this transition should start
	 * @param end
	 *            state where this transition should
	 * @param intermediateVertices
	 *            the collection of intermediate vertices
	 */
  public IntermediateVertexTransition(State start, State end, VertexEvaluator<?> intermediateVertices) {
    super(start, end);
    intermediateVertexEvaluator = intermediateVertices;
  }

  /**
	 * Copy-constructor, creates a copy of the given transition
	 */
  protected IntermediateVertexTransition(IntermediateVertexTransition t, boolean addToStates) {
    super(t, addToStates);
    intermediateVertexEvaluator = t.intermediateVertexEvaluator;
  }

  /**
	 * returns a copy of this transition
	 */
  @Override public Transition copy(boolean addToStates) {
    return new IntermediateVertexTransition(this, addToStates);
  }

  /**
	 * Checks if the transition is an epsilon-transition
	 * 
	 * @return true if this transition is an epsilon-transition, false otherwise
	 */
  @Override public boolean isEpsilon() {
    return false;
  }

  /**
	 * returns a string which describes the edge
	 */
  @Override public String edgeString() {
    String desc = "IndermediateVertexTransition";
    return desc;
  }

  @Override public boolean accepts(Vertex v, Edge e, InternalGreqlEvaluator evaluator) {
    if (intermediateVertexEvaluator != null) {
      Object tempRes = intermediateVertexEvaluator.getResult(evaluator);
      if (tempRes instanceof PCollection) {
        @SuppressWarnings(value = { "unchecked" }) PCollection<Vertex> intermediateVertices = (PCollection<Vertex>) tempRes;
        return intermediateVertices.contains(v);
      } else {
        Vertex intermediateVertex = (Vertex) tempRes;
        return v == intermediateVertex;
      }
    }
    return false;
  }

  /**
	 * returns the vertex of the datagraph which can be visited after this
	 * transition has fired. This is the vertex itself
	 */
  @Override public Vertex getNextVertex(Vertex v, Edge e) {
    return v;
  }

  @Override public String prettyPrint() {
    return "IntermediateVertex " + GreqlSerializer.serializeVertex(intermediateVertexEvaluator.getVertex());
  }

  @Override public boolean consumesEdge() {
    return false;
  }
}