package de.uni_koblenz.jgralab.greql2.evaluator.fa;
import java.util.BitSet;
import java.util.Set;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ThisEdgeEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.GReQLDirection;
import de.uni_koblenz.jgralab.greql2.schema.ThisEdge;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;

/**
 * This transition accepts a SimplePathDescription. A SimplePathDescription is
 * for instance something like v -->{isExprOf} w.
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public class SimpleTransition extends Transition {
  protected VertexEvaluator<? extends Expression> predicateEvaluator;

  public VertexEvaluator getPredicateEvaluator() {
    return predicateEvaluator;
  }

  protected ThisEdgeEvaluator thisEdgeEvaluator;

  /**
	 * The collection of types that are accepted by this transition
	 */
  protected TypeCollection typeCollection;

  public TypeCollection getTypeCollection() {
    return typeCollection;
  }

  /**
	 * an edge may have valid roles. This set holds the valid roles at the other
	 * end of an edge accepted by this transition. If the transition is valid
	 * for no explicit role, this set is null
	 */
  protected Set<String> validToEdgeRoles;

  public Set<String> getValidToRoles() {
    return validToEdgeRoles;
  }

  /**
	 * an edge may have valid roles. This set holds the valid roles at the other
	 * end of an edge accepted by this transition. If the transition is valid
	 * for no explicit role, this set is null
	 */
  protected Set<String> validFromEdgeRoles;

  public Set<String> getValidFromRoles() {
    return validFromEdgeRoles;
  }

  /**
	 * this transition may accept edges in direction in, out or any
	 */
  protected GReQLDirection validDirection;

  public GReQLDirection getAllowedDirection() {
    return validDirection;
  }

  /**
	 * returns a string which describes the edge
	 */
  @Override public String edgeString() {
    String desc = "SimpleTransition (Dir:" + validDirection.toString();
    if (typeCollection != null) {
      desc = desc + "\n " + typeCollection.toString() + "\n ";
    }
    desc += ")";
    return desc;
  }

  @Override public boolean equalSymbol(Transition t) {
    if (!(t instanceof SimpleTransition)) {
      return false;
    }
    SimpleTransition et = (SimpleTransition) t;
    if (!typeCollection.equals(et.typeCollection)) {
      return false;
    }
    if (!validDirection.equals(et.validDirection)) {
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
  protected SimpleTransition(SimpleTransition t, boolean addToStates) {
    super(t, addToStates);
    validDirection = t.validDirection;
    typeCollection = new TypeCollection(t.typeCollection);
    predicateEvaluator = t.predicateEvaluator;
    thisEdgeEvaluator = t.thisEdgeEvaluator;
    validToEdgeRoles = t.validToEdgeRoles;
    validFromEdgeRoles = t.validFromEdgeRoles;
  }

  /**
	 * returns a copy of this transition
	 */
  @Override public Transition copy(boolean addToStates) {
    return new SimpleTransition(this, addToStates);
  }

  /**
	 * Creates a new transition from start state to end state. The Transition
	 * accepts all edges that have the right direction, role, startVertexType,
	 * endVertexType, edgeType and even it's possible to define a specific edge.
	 * 
	 * @param start
	 *            The state where this transition starts
	 * @param end
	 *            The state where this transition ends
	 * @param dir
	 *            The direction of the accepted edges, may be EdeDirection.IN,
	 *            EdgeDirection.OUT or EdgeDirection.ANY
	 */
  public SimpleTransition(State start, State end, GReQLDirection dir) {
    super(start, end);
    validDirection = dir;
    typeCollection = new TypeCollection();
  }

  /**
	 * Creates a new transition from start state to end state. The Transition
	 * accepts all edges that have the right direction, role, startVertexType,
	 * endVertexType, edgeType and even it's possible to define a specific edge.
	 * This constructor creates a transition to accept a simplePathDescription
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
	 */
  public SimpleTransition(State start, State end, GReQLDirection dir, TypeCollection typeCollection, Set<String> roles, VertexEvaluator<? extends Expression> predicateEvaluator, QueryImpl query) {
    super(start, end);
    validDirection = dir;
    validToEdgeRoles = roles;
    validFromEdgeRoles = null;
    this.typeCollection = typeCollection;
    this.predicateEvaluator = predicateEvaluator;
    ThisEdge v = (ThisEdge) query.getQueryGraph().getFirstVertex(ThisEdge.class);
    if (v != null) {
      thisEdgeEvaluator = (ThisEdgeEvaluator) query.getVertexEvaluator(v);
    }
  }

  @Override public void reverse() {
    super.reverse();
    if (validDirection == GReQLDirection.IN) {
      validDirection = GReQLDirection.OUT;
    } else {
      if (validDirection == GReQLDirection.OUT) {
        validDirection = GReQLDirection.IN;
      }
    }
    Set<String> tempSet = validFromEdgeRoles;
    validFromEdgeRoles = validToEdgeRoles;
    validToEdgeRoles = tempSet;
  }

  @Override public boolean isEpsilon() {
    return false;
  }

  @Override public boolean accepts(Vertex v, Edge e, InternalGreqlEvaluator evaluator) {
    if (e == null) {
      return false;
    }
    if (validDirection == GReQLDirection.OUT) {
      if (!e.isNormal()) {
        return false;
      }
    } else {
      if (validDirection == GReQLDirection.IN) {
        if (e.isNormal()) {
          return false;
        }
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
      EdgeClass ec = e.getAttributedElementClass();
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
        EdgeClass edgeClass = e.getAttributedElementClass();
        if (!typeCollection.acceptsType(edgeClass)) {
          return false;
        }
      }
    }
    if (predicateEvaluator != null) {
      if (thisEdgeEvaluator != null) {
        thisEdgeEvaluator.setValue(e, evaluator);
      }
      Object res = predicateEvaluator.getResult(evaluator);
      if (res instanceof Boolean) {
        return (Boolean) res;
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
    for (AttributedElementClass<?, ?> c : typeCollection.getAllowedTypes()) {
      b.append(delim);
      b.append(c.getSimpleName());
      delim = ",";
    }
    String symbol = "<->";
    if (validDirection == GReQLDirection.IN) {
      symbol = "<--";
    } else {
      if (validDirection == GReQLDirection.OUT) {
        symbol = "-->";
      }
    }
    return symbol + "{" + b + "}";
  }

  @Override public boolean consumesEdge() {
    return true;
  }
}