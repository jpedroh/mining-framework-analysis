package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.HashSet;
import java.util.Set;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.EdgeRestriction;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.IsBooleanPredicateOfEdgeRestriction;
import de.uni_koblenz.jgralab.greql2.schema.IsRoleIdOf;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeIdOf;
import de.uni_koblenz.jgralab.greql2.schema.RoleId;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

/**
 * Evaluates an edge restriction, edges can be restricted with TypeIds and Roles
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgeRestrictionEvaluator extends VertexEvaluator<EdgeRestriction> {
  private VertexEvaluator<? extends Expression> predicateEvaluator = null;

  public VertexEvaluator<? extends Expression> getPredicateEvaluator() {
    return predicateEvaluator;
  }

  /**
	 * The JValueTypeCollection which holds all the allowed and forbidden types
	 */
  private TypeCollection typeCollection = null;

  /**
	 * Returns the typeCollection
	 */
  public TypeCollection getTypeCollection(InternalGreqlEvaluator evaluator) {
    if (typeCollection == null) {
      evaluate(evaluator);
    }
    return typeCollection;
  }

  /**
	 * the valid role of an edge
	 */
  private Set<String> validRoles;

  /**
	 * @return the valid edge role
	 */
  public Set<String> getEdgeRoles() {
    return validRoles;
  }

  /**
	 * creates a new EdgeRestriction evaluator
	 * 
	 * @param vertex
	 * @param eval
	 */
  public EdgeRestrictionEvaluator(EdgeRestriction vertex, QueryImpl query) {
    super(vertex, query);
  }

  /**
	 * evaluates the EdgeRestriction, creates the typeList and the validEdgeRole
	 */
  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    if (typeCollection == null) {
      typeCollection = new TypeCollection();
      IsTypeIdOf typeInc = vertex.getFirstIsTypeIdOfIncidence(EdgeDirection.IN);
      while (typeInc != null) {
        TypeIdEvaluator typeEval = (TypeIdEvaluator) query.getVertexEvaluator(typeInc.getAlpha());
        typeCollection.addTypes((TypeCollection) typeEval.getResult(evaluator));
        typeInc = typeInc.getNextIsTypeIdOfIncidence(EdgeDirection.IN);
      }
    }
    if (vertex.getFirstIsRoleIdOfIncidence() != null) {
      validRoles = new HashSet<String>();
      for (IsRoleIdOf e : vertex.getIsRoleIdOfIncidences()) {
        RoleId role = e.getAlpha();
        validRoles.add(role.get_name());
      }
    }
    IsBooleanPredicateOfEdgeRestriction predInc = vertex.getFirstIsBooleanPredicateOfEdgeRestrictionIncidence(EdgeDirection.IN);
    if (predInc != null) {
      predicateEvaluator = query.getVertexEvaluator(predInc.getAlpha());
    }
    return null;
  }
}