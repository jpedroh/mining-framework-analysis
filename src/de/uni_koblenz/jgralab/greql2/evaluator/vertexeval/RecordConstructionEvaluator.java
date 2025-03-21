package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.IsRecordElementOf;
import de.uni_koblenz.jgralab.greql2.schema.RecordConstruction;
import de.uni_koblenz.jgralab.greql2.schema.RecordElement;
import de.uni_koblenz.jgralab.impl.RecordImpl;

/**
 * Evaluates a record construction, this is for instance rec( name:"element")
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class RecordConstructionEvaluator extends VertexEvaluator<RecordConstruction> {
  /**
	 * Creates a new RecordConstructionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public RecordConstructionEvaluator(RecordConstruction vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public Record evaluate(InternalGreqlEvaluator evaluator) {
    RecordImpl resultRecord = RecordImpl.empty();
    IsRecordElementOf inc = vertex.getFirstIsRecordElementOfIncidence(EdgeDirection.IN);
    while (inc != null) {
      RecordElement currentElement = inc.getAlpha();
      RecordElementEvaluator vertexEval = (RecordElementEvaluator) query.getVertexEvaluator(currentElement);
      resultRecord = resultRecord.plus(vertexEval.getId(), vertexEval.getResult(evaluator));
      inc = inc.getNextIsRecordElementOfIncidence(EdgeDirection.IN);
    }
    return resultRecord;
  }
}