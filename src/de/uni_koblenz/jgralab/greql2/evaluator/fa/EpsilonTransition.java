package de.uni_koblenz.jgralab.greql2.evaluator.fa;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;

/**
 * A epsilon transition which may fire without any restrictions. Epsilon
 * transitions are used during thompson-construction of the NFAs, but they will
 * be eliminated before path search.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EpsilonTransition extends Transition {
  /**
	 * creates a new epsilon-transition from start to end
	 * 
	 * @param start
	 *            state where this transition should start
	 * @param end
	 *            state where this transition should end
	 */
  public EpsilonTransition(State start, State end) {
    super(start, end);
  }

  /**
	 * Copy-constructor, creates a copy of the given transition
	 */
  protected EpsilonTransition(EpsilonTransition t, boolean addToStates) {
    super(t, addToStates);
  }

  /**
	 * returns a copy of this transition
	 */
  @Override public Transition copy(boolean addToStates) {
    return new EpsilonTransition(this, addToStates);
  }

  @Override public boolean equalSymbol(Transition t) {
    if (t instanceof EpsilonTransition) {
      return true;
    }
    return false;
  }

  @Override public boolean isEpsilon() {
    return true;
  }

  /**
	 * This method should not be called because all epsilon-transitions should
	 * be eliminated before the pathsearch strarts
	 */
  @Override public boolean accepts(Vertex v, Edge e, InternalGreqlEvaluator evaluator) {
    throw new UnsupportedOperationException("EpsilonTransition.accepts(...) has been called. That should not happen, there should be no epsilon-transitions in the DFA used for path search. Check the DFA-Constructor");
  }

  /**
	 * returns the vertex of the datagraph which can be visited after this
	 * transition has fired. This is the vertex at the end of the edge
	 */
  @Override public Vertex getNextVertex(Vertex v, Edge e) {
    if (e.getAlpha() == v) {
      return e.getOmega();
    } else {
      return e.getAlpha();
    }
  }

  /**
	 * returns a string which describes the edge
	 */
  @Override public String edgeString() {
    String desc = "EpsilonTransition";
    return desc;
  }

  @Override public String prettyPrint() {
    return "epsilon";
  }

  @Override public boolean consumesEdge() {
    return false;
  }
}