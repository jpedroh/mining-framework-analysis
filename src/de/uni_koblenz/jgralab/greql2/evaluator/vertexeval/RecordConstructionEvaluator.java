package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
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
public class RecordConstructionEvaluator extends VertexEvaluator {
  private RecordConstruction vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  /**
	 * Creates a new RecordConstructionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public RecordConstructionEvaluator(RecordConstruction vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  @Override public Record evaluate() {
    RecordImpl resultRecord = RecordImpl.empty();
    IsRecordElementOf inc = vertex.getFirstIsRecordElementOfIncidence(EdgeDirection.IN);
    while (inc != null) {
      RecordElement currentElement = inc.getAlpha();
      RecordElementEvaluator vertexEval = (RecordElementEvaluator) vertexEvalMarker.getMark(currentElement);
      resultRecord = resultRecord.plus(vertexEval.getId(), vertexEval.getResult());
      inc = inc.getNextIsRecordElementOfIncidence(EdgeDirection.IN);
    }
    return resultRecord;
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCostsRecordConstruction(this, graphSize);
  }

  @Override public long calculateEstimatedCardinality(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCardinalityRecordConstruction(this, graphSize);
  }
}