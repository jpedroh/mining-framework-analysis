package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.Set;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.Direction;
import de.uni_koblenz.jgralab.greql2.schema.GReQLDirection;
import de.uni_koblenz.jgralab.greql2.schema.PrimaryPathDescription;


<<<<<<< /usr/src/app/output/jgralab/jgralab/693df1d429d23b9f14b883047743fb3c1d785f91/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PrimaryPathDescriptionEvaluator.java/left.java
/**
 * abstract baseclass for SimplePathDescription and EdgePathDescription
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public abstract class PrimaryPathDescriptionEvaluator<V extends PrimaryPathDescription> extends PathDescriptionEvaluator<V> {
  public PrimaryPathDescriptionEvaluator(V vertex, QueryImpl query) {
    super(vertex, query);
  }

  /**
	 * Returns the edge direction this pathDescription accepts
	 */
  protected Transition.AllowedEdgeDirection getEdgeDirection(PrimaryPathDescription vertex) {
    Transition.AllowedEdgeDirection validDirection = Transition.AllowedEdgeDirection.ANY;
    Edge dirEdge = vertex.getFirstIsDirectionOfIncidence(EdgeDirection.IN);
    if (dirEdge != null) {
      Direction dirVertex = (Direction) dirEdge.getAlpha();
      if (dirVertex.get_dirValue() == "in") {
        validDirection = Transition.AllowedEdgeDirection.IN;
      } else {
        if (dirVertex.get_dirValue() == "out") {
          validDirection = Transition.AllowedEdgeDirection.OUT;
        }
      }
    }
    return validDirection;
  }

  /**
	 * Returns the set of edge role this PathDescription accepts
	 */
  protected Set<String> getEdgeRoles(EdgeRestrictionEvaluator edgeRestEval) {
    if (edgeRestEval == null) {
      return null;
    }
    return edgeRestEval.getEdgeRoles();
  }
}
=======
/**
 * abstract baseclass for SimplePathDescription and EdgePathDescription
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public abstract class PrimaryPathDescriptionEvaluator extends PathDescriptionEvaluator {
  protected PrimaryPathDescription vertex;

  private GReQLDirection validDirection = null;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  public PrimaryPathDescriptionEvaluator(PrimaryPathDescription vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  /**
	 * Returns the edge direction this pathDescription accepts
	 */
  protected GReQLDirection getEdgeDirection(PrimaryPathDescription vertex) {
    if (validDirection == null) {
      validDirection = GReQLDirection.INOUT;
      Edge dirEdge = vertex.getFirstIsDirectionOfIncidence(EdgeDirection.IN);
      if (dirEdge != null) {
        Direction dirVertex = (Direction) dirEdge.getAlpha();
        validDirection = dirVertex.get_dirValue();
      }
    }
    return validDirection;
  }

  /**
	 * Returns the set of edge role this PathDescription accepts
	 */
  protected Set<String> getEdgeRoles(EdgeRestrictionEvaluator edgeRestEval) {
    if (edgeRestEval == null) {
      return null;
    }
    return edgeRestEval.getEdgeRoles();
  }
}
>>>>>>> /usr/src/app/output/jgralab/jgralab/693df1d429d23b9f14b883047743fb3c1d785f91/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PrimaryPathDescriptionEvaluator.java/right.java
