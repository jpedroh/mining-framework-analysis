package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.RecordElement;
import de.uni_koblenz.jgralab.greql2.schema.RecordId;

/**
 * Evaluates a record element, this is for instance name:"element" in the
 * record-construction rec( name:"element")
 * 
 * @author ist@uni-koblenz.de November 2006
 * 
 */
public class RecordElementEvaluator extends VertexEvaluator<RecordElement> {
  private String id = null;

  private VertexEvaluator<? extends Expression> expEval = null;

  public String getId() {
    if (id == null) {
      RecordId idVertex = vertex.getFirstIsRecordIdOfIncidence(EdgeDirection.IN).getAlpha();
      id = idVertex.get_name();
    }
    return id;
  }

  /**
	 * Creates a new RecordConstructionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public RecordElementEvaluator(RecordElement vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    if (expEval == null) {
      Expression recordElementExp = vertex.getFirstIsRecordExprOfIncidence(EdgeDirection.IN).getAlpha();
      expEval = query.getVertexEvaluator(recordElementExp);
    }
    return expEval.getResult(evaluator);
  }
}