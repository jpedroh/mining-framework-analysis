package de.uni_koblenz.jgralab.greql2.evaluator.fa;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.GraphMarker;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ThisVertexEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.ThisVertex;
import de.uni_koblenz.jgralab.greql2.serialising.GreqlSerializer;

/**
 * This transition may fire, if the VertexEvaluator it holds as attribute
 * returns true as result
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class BoolExpressionTransition extends Transition {
  private VertexEvaluator boolExpressionEvaluator;

  private ThisVertexEvaluator thisVertexEvaluator;

  /**
	 * returns a string which describes the edge
	 */
  @Override public String edgeString() {
    String desc = "BoolExpressionTransition";
    return desc;
  }

  @Override public boolean equalSymbol(Transition t) {
    if (!(t instanceof BoolExpressionTransition)) {
      return false;
    }
    BoolExpressionTransition bt = (BoolExpressionTransition) t;
    if (bt.boolExpressionEvaluator == this.boolExpressionEvaluator) {
      return true;
    }
    return false;
  }

  /**
	 * Copy-constructor, creates a copy of the given transition
	 */
  protected BoolExpressionTransition(BoolExpressionTransition t, boolean addToStates) {
    super(t, addToStates);
    boolExpressionEvaluator = t.boolExpressionEvaluator;
    thisVertexEvaluator = t.thisVertexEvaluator;
  }

  /**
	 * returns a copy of this transition
	 */
  @Override public Transition copy(boolean addToStates) {
    return new BoolExpressionTransition(this, addToStates);
  }

  /**
	 * Creates a new transition from start state to end state.
	 */
  public BoolExpressionTransition(State start, State end, VertexEvaluator boolEval, GraphMarker<VertexEvaluator> graphMarker) {
    super(start, end);
    boolExpressionEvaluator = boolEval;
    Vertex v = graphMarker.getGraph().getFirstVertex(ThisVertex.class);
    if (v != null) {
      thisVertexEvaluator = (ThisVertexEvaluator) graphMarker.getMark(v);
    }
  }

  @Override public boolean isEpsilon() {
    return false;
  }

  @Override public boolean accepts(Vertex v, Edge e) {
    if (thisVertexEvaluator != null) {
      thisVertexEvaluator.setValue(v);
    }
    Object res = boolExpressionEvaluator.getResult();
    if (res instanceof Boolean && ((Boolean) res).equals(Boolean.TRUE)) {
      return true;
    }
    return false;
  }

  @Override public Vertex getNextVertex(Vertex v, Edge e) {
    return v;
  }

  @Override public String prettyPrint() {
    return "IntermediateVertex " + GreqlSerializer.serializeVertex(boolExpressionEvaluator.getVertex());
  }

  @Override public boolean consumesEdge() {
    return false;
  }
}