package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.HashSet;
import java.util.Set;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.EdgeRestriction;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
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
public class EdgeRestrictionEvaluator extends VertexEvaluator {
  /**
	 * The EdgeRestriction vertex in the GReQL Syntaxgraph
	 */
  private EdgeRestriction vertex;

  private VertexEvaluator predicateEvaluator = null;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  public VertexEvaluator getPredicateEvaluator() {
    return predicateEvaluator;
  }

  /**
	 * The JValueTypeCollection which holds all the allowed and forbidden types
	 */
  private TypeCollection typeCollection = null;

  /**
	 * Returns the typeCollection
	 */
  public TypeCollection getTypeCollection() {
    if (typeCollection == null) {
      evaluate();
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
  public EdgeRestrictionEvaluator(EdgeRestriction vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  /**
	 * evaluates the EdgeRestriction, creates the typeList and the validEdgeRole
	 */
  @Override public Object evaluate() {
    if (typeCollection == null) {
      typeCollection = new TypeCollection();
      IsTypeIdOf typeInc = vertex.getFirstIsTypeIdOfIncidence(EdgeDirection.IN);
      while (typeInc != null) {
        TypeIdEvaluator typeEval = (TypeIdEvaluator) vertexEvalMarker.getMark(typeInc.getAlpha());
        typeCollection.addTypes((TypeCollection) typeEval.getResult());
        typeInc = typeInc.getNextIsTypeIdOfIncidence(EdgeDirection.IN);
      }
    }
    if (vertex.getFirstIsRoleIdOfIncidence() != null) {
      validRoles = new HashSet<String>();
      for (IsRoleIdOf e : vertex.getIsRoleIdOfIncidences()) {
        RoleId role = (RoleId) e.getAlpha();
        validRoles.add(role.get_name());
      }
    }
    IsBooleanPredicateOfEdgeRestriction predInc = vertex.getFirstIsBooleanPredicateOfEdgeRestrictionIncidence(EdgeDirection.IN);
    if (predInc != null) {
      predicateEvaluator = vertexEvalMarker.getMark(predInc.getAlpha());
    }
    return null;
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsEdgeRestriction(this, graphSize);
  }
}