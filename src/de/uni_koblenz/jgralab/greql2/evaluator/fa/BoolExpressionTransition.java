package de.uni_koblenz.jgralab.greql2.evaluator.fa;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.graphmarker.GraphMarker;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ThisVertexEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
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
  private final VertexEvaluator<? extends Expression> boolExpressionEvaluator;

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
    if (bt.boolExpressionEvaluator == boolExpressionEvaluator) {
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
  public BoolExpressionTransition(State start, State end, VertexEvaluator<? extends Expression> boolEval, Query query) {
    super(start, end);
    boolExpressionEvaluator = boolEval;
    ThisVertex v = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/fa/BoolExpressionTransition.java/left.java
    (ThisVertex) query.getQueryGraph().getFirstVertex(ThisVertex.class)
=======
    graphMarker.getGraph().getFirstVertex(ThisVertex.class)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/fa/BoolExpressionTransition.java/right.java
    ;
    if (v != null) {
      thisVertexEvaluator = (ThisVertexEvaluator) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/fa/BoolExpressionTransition.java/left.java
      query
=======
      graphMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/fa/BoolExpressionTransition.java/right.java
      .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/fa/BoolExpressionTransition.java/left.java
      getVertexEvaluator(v)
=======
      getMark(v)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/fa/BoolExpressionTransition.java/right.java
      ;
    }
  }

  @Override public boolean isEpsilon() {
    return false;
  }

  @Override public boolean accepts(Vertex v, Edge e, InternalGreqlEvaluator evaluator) {
    if (thisVertexEvaluator != null) {
      thisVertexEvaluator.setValue(v, evaluator);
    }
    Object res = boolExpressionEvaluator.getResult(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/fa/BoolExpressionTransition.java/left.java
    evaluator
=======
>>>>>>> Unknown file: This is a bug in JDime.
    );
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