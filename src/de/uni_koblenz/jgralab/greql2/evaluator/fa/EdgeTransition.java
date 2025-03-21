package de.uni_koblenz.jgralab.greql2.evaluator.fa;
import java.util.Set;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

/**
 * This transition accepts only one edge. Because this edge may be a variable or
 * even the result of an expression containing a variable, a reference to the
 * VertexEvaluator which evaluates this variable/expression is stored in this
 * transition and the result of this evaluator is acceptes.
 * 
 * This transition accepts the greql2 syntax: --{edge}->
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgeTransition extends SimpleTransition {
  /**
	 * In GReQL 2 it is possible to specify an explicit edge. Cause this edge
	 * may be a variable or the result of an expression containing a variable,
	 * the VertexEvalutor which evaluates this edge expression is stored here so
	 * the result can be used as allowed edge
	 */
  private final VertexEvaluator<?> allowedEdgeEvaluator;

  /**
	 * returns a string which describes the edge
	 */
  @Override public String edgeString() {
    String desc = "EdgeTransition";
    return desc;
  }

  @Override public boolean equalSymbol(Transition t) {
    if (!(t instanceof EdgeTransition)) {
      return false;
    }
    EdgeTransition et = (EdgeTransition) t;
    if (!typeCollection.equals(et.typeCollection)) {
      return false;
    }
    if (validToEdgeRoles != null) {
      if (et.validToEdgeRoles == null) {
        return false;
      }
      if (!validToEdgeRoles.equals(et.validToEdgeRoles)) {
        return false;
      }
    } else {
      if (et.validToEdgeRoles != null) {
        return false;
      }
    }
    if (validFromEdgeRoles == null) {
      if (et.validFromEdgeRoles != null) {
        return false;
      }
    } else {
      if (et.validFromEdgeRoles == null) {
        return false;
      }
      if (!validFromEdgeRoles.equals(et.validFromEdgeRoles)) {
        return false;
      }
    }
    if (allowedEdgeEvaluator != et.allowedEdgeEvaluator) {
      return false;
    }
    if (validDirection != et.validDirection) {
      return false;
    }
    if (predicateEvaluator != null) {
      if (et.predicateEvaluator == null) {
        return false;
      }
      if (!predicateEvaluator.equals(et.predicateEvaluator)) {
        return false;
      }
    } else {
      if (et.predicateEvaluator != null) {
        return false;
      }
    }
    return true;
  }

  /**
	 * Copy-constructor, creates a copy of the given transition
	 */
  protected EdgeTransition(EdgeTransition t, boolean addToStates) {
    super(t, addToStates);
    allowedEdgeEvaluator = t.allowedEdgeEvaluator;
  }

  /**
	 * returns a copy of this transition
	 */
  @Override public Transition copy(boolean addToStates) {
    return new EdgeTransition(this, addToStates);
  }

  /**
	 * Creates a new transition from start state to end state. The Transition
	 * accepts all edges that have the right direction, role, startVertexType,
	 * endVertexType, edgeType and even it's possible to define a specific edge.
	 * This constructor creates a transition to accept a EdgePathDescription
	 * 
	 * @param start
	 *            The state where this transition starts
	 * @param end
	 *            The state where this transition ends
	 * @param dir
	 *            The direction of the accepted edges, may be EdeDirection.IN,
	 *            EdgeDirection.OUT or EdgeDirection.ANY
	 * @param typeCollection
	 *            The types which restrict the possible edges
	 * @param roles
	 *            The set of accepted edge role names, or null if any role is
	 *            accepted
	 * @param edgeEval
	 *            If this is set, only the resulting edge of this evaluator will
	 *            be accepted
	 */
  public EdgeTransition(State start, State end, AllowedEdgeDirection dir, TypeCollection typeCollection, Set<String> roles, VertexEvaluator<?> edgeEval, VertexEvaluator<? extends Expression> predicateEval, QueryImpl query) {
    super(start, end, dir, typeCollection, roles, predicateEval, query);
    allowedEdgeEvaluator = edgeEval;
  }

  @Override public boolean accepts(Vertex v, Edge e, InternalGreqlEvaluator evaluator) {
    if (!super.accepts(v, e, evaluator)) {
      return false;
    }
    if (allowedEdgeEvaluator != null) {
      Edge allowedEdge = ((Edge) allowedEdgeEvaluator.getResult(evaluator)).getNormalEdge();
      if (e.getNormalEdge() != allowedEdge) {
        return false;
      }
    }
    return true;
  }

  public boolean consumedEdge() {
    return true;
  }
}