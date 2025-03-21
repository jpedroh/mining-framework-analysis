package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.HashSet;
import java.util.Set;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.schema.EdgeRestriction;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
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
public class EdgeRestrictionEvaluator extends VertexEvaluator<EdgeRestriction> {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/left.java
  private VertexEvaluator<? extends Expression> predicateEvaluator = null;
=======
  private VertexEvaluator predicateEvaluator = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/right.java



<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/left.java
  public VertexEvaluator<? extends Expression> getPredicateEvaluator() {
    return predicateEvaluator;
  }
=======
  public VertexEvaluator getPredicateEvaluator() {
    return predicateEvaluator;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/right.java


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
  public EdgeRestrictionEvaluator(EdgeRestriction vertex, Query query) {
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
        TypeIdEvaluator typeEval = (TypeIdEvaluator) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/left.java
        query
=======
        vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/right.java
        .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/left.java
        getVertexEvaluator(typeInc.getAlpha())
=======
        getMark(typeInc.getAlpha())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/right.java
        ;
        typeCollection.addTypes((TypeCollection) typeEval.getResult(evaluator));
        typeInc = typeInc.getNextIsTypeIdOfIncidence(EdgeDirection.IN);
      }
    }
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/left.java
    vertex.getFirstIsRoleIdOfIncidence()
=======
    vertex.getFirstIsRoleIdOfIncidence()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/right.java
     != null) {
      validRoles = new HashSet<String>();
      for (IsRoleIdOf e : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/left.java
      vertex.getIsRoleIdOfIncidences()
=======
      vertex.getIsRoleIdOfIncidences()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/right.java
      ) {
        RoleId role = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/left.java
        e.getAlpha()
=======
        (RoleId) e.getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/right.java
        ;
        validRoles.add(role.get_name());
      }
    }
    IsBooleanPredicateOfEdgeRestriction predInc = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/left.java
    vertex.getFirstIsBooleanPredicateOfEdgeRestrictionIncidence(EdgeDirection.IN)
=======
    vertex.getFirstIsBooleanPredicateOfEdgeRestrictionIncidence(EdgeDirection.IN)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/right.java
    ;
    if (predInc != null) {
      predicateEvaluator = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/left.java
      query
=======
      vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/right.java
      .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/left.java
      getVertexEvaluator(predInc.getAlpha())
=======
      getMark(predInc.getAlpha())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgeRestrictionEvaluator.java/right.java
      ;
    }
    return null;
  }
}