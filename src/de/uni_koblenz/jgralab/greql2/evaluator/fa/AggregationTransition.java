package de.uni_koblenz.jgralab.greql2.evaluator.fa;
import java.util.Set;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.GraphMarker;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ThisEdgeEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.ThisEdge;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;

/**
 * This transition accepts an AggregationPathDescription. Am
 * AggregationPathDescription is for instance something like v --<>{isExprOf} w.
 * 
 * @author ist@uni-koblenz.de
 */
public class AggregationTransition extends Transition {
  private VertexEvaluator predicateEvaluator;

  private ThisEdgeEvaluator thisEdgeEvaluator;

  /**
	 * The collection of types that are accepted by this transition
	 */
  protected TypeCollection typeCollection;

  /**
	 * an edge may have valid roles. This set holds the valid roles for this
	 * transition. If the transition is valid for all roles, this set is null
	 */
  protected Set<String> validToEdgeRoles;

  protected Set<String> validFromEdgeRoles;

  protected boolean aggregateFrom;

  /**
	 * returns a string which describes the edge
	 */
  @Override public String edgeString() {
    String desc = "AggregationTransition (aggregateFrom:" + aggregateFrom;
    if (typeCollection != null) {
      desc = desc + "\n " + typeCollection.toString() + "\n ";
    }
    desc += ")";
    return desc;
  }

  @Override public boolean equalSymbol(Transition t) {
    if (!(t instanceof AggregationTransition)) {
      return false;
    }
    AggregationTransition et = (AggregationTransition) t;
    if (!typeCollection.equals(et.typeCollection)) {
      return false;
    }
    if (aggregateFrom != et.aggregateFrom) {
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
    if (validFromEdgeRoles != null) {
      if (et.validFromEdgeRoles == null) {
        return false;
      }
      if (!validFromEdgeRoles.equals(et.validFromEdgeRoles)) {
        return false;
      }
    } else {
      if (et.validFromEdgeRoles != null) {
        return false;
      }
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
  protected AggregationTransition(AggregationTransition t, boolean addToStates) {
    super(t, addToStates);
    aggregateFrom = t.aggregateFrom;
    typeCollection = new TypeCollection(t.typeCollection);
    validToEdgeRoles = t.validToEdgeRoles;
    predicateEvaluator = t.predicateEvaluator;
    thisEdgeEvaluator = t.thisEdgeEvaluator;
    validToEdgeRoles = t.validToEdgeRoles;
    validFromEdgeRoles = t.validFromEdgeRoles;
  }

  /**
	 * returns a copy of this transition
	 */
  @Override public Transition copy(boolean addToStates) {
    return new AggregationTransition(this, addToStates);
  }

  /**
	 * Creates a new transition from start state to end state. The Transition
	 * accepts all aggregations that have the right aggregation direction, role,
	 * startVertexType, endVertexType, edgeType and even it's possible to define
	 * a specific edge. This constructor creates a transition to accept a
	 * simplePathDescription
	 * 
	 * @param start
	 *            The state where this transition starts
	 * @param end
	 *            The state where this transition ends
	 * @param aggregateFrom
	 *            The direction of the aggregation, true for an aggregation with
	 *            the aggregation end at the near vertex, false for an
	 *            aggregation with the aggregation end at the far vertex
	 * @param typeCollection
	 *            The types which restrict the possible edges
	 * @param roles
	 *            The set of accepted edge role names, or null if any role is
	 *            accepted
	 */
  public AggregationTransition(State start, State end, boolean aggregateFrom, TypeCollection typeCollection, Set<String> roles, VertexEvaluator predicateEvaluator, GraphMarker<VertexEvaluator> graphMarker) {
    super(start, end);
    this.aggregateFrom = aggregateFrom;
    this.validToEdgeRoles = roles;
    this.validFromEdgeRoles = null;
    this.typeCollection = typeCollection;
    this.predicateEvaluator = predicateEvaluator;
    Vertex v = graphMarker.getGraph().getFirstVertex(ThisEdge.class);
    if (v != null) {
      thisEdgeEvaluator = (ThisEdgeEvaluator) graphMarker.getMark(v);
    }
  }

  @Override public void reverse() {
    super.reverse();
    aggregateFrom = !aggregateFrom;
    Set<String> tempSet = validFromEdgeRoles;
    validFromEdgeRoles = validToEdgeRoles;
    validToEdgeRoles = tempSet;
  }

  @Override public boolean isEpsilon() {
    return false;
  }

  @Override public boolean accepts(Vertex v, Edge e) {
    if (e == null) {
      return false;
    }
    if (aggregateFrom) {
      if (e.getThatAggregationKind() == AggregationKind.NONE) {
        return false;
      }
    } else {
      if (e.getThisAggregationKind() == AggregationKind.NONE) {
        return false;
      }
    }
    Set<String> validEdgeRoles = validToEdgeRoles;
    boolean checkToEdgeRoles = true;
    if (validEdgeRoles == null) {
      validEdgeRoles = validFromEdgeRoles;
      checkToEdgeRoles = false;
    }
    boolean rolesOnly = (validEdgeRoles != null) && (typeCollection.getAllowedTypes().size() == 0) && (typeCollection.getForbiddenTypes().size() == 0);
    boolean acceptedByRole = false;
    if (validEdgeRoles != null) {
      EdgeClass ec = (EdgeClass) e.getAttributedElementClass();
      Set<String> roles = null;
      if (e.isNormal() == checkToEdgeRoles) {
        roles = ec.getTo().getAllRoles();
      } else {
        roles = ec.getFrom().getAllRoles();
      }
      for (String role : roles) {
        if (validEdgeRoles.contains(role)) {
          acceptedByRole = true;
          break;
        }
      }
    }
    if (rolesOnly) {
      if (!acceptedByRole) {
        return false;
      }
    } else {
      if (!acceptedByRole) {
        AttributedElementClass edgeClass = e.getAttributedElementClass();
        if (!typeCollection.acceptsType(edgeClass)) {
          return false;
        }
      }
    }
    if (predicateEvaluator != null) {
      thisEdgeEvaluator.setValue(e);
      Object res = predicateEvaluator.getResult();
      if (res instanceof Boolean) {
        if (((Boolean) res).equals(Boolean.TRUE)) {
          return true;
        }
      }
      return false;
    }
    return true;
  }

  /**
	 * returns the vertex of the datagraph which can be visited after this
	 * transition has fired. This is the vertex at the end of the edge
	 */
  @Override public Vertex getNextVertex(Vertex v, Edge e) {
    return e.getThat();
  }

  @Override public String prettyPrint() {
    StringBuilder b = new StringBuilder();
    String delim = "";
    for (AttributedElementClass c : typeCollection.getAllowedTypes()) {
      b.append(delim);
      b.append(c.getSimpleName());
      delim = ",";
    }
    String symbol = "--<>";
    if (aggregateFrom) {
      symbol = "<>--";
    }
    return symbol + "{" + b + "}";
  }

  @Override public boolean consumesEdge() {
    return true;
  }
}