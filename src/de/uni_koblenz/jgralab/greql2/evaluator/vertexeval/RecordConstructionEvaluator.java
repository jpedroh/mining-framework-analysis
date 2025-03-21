package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
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
public class RecordConstructionEvaluator extends VertexEvaluator<RecordConstruction> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordConstructionEvaluator.java/right.java


  /**
	 * Creates a new RecordConstructionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public RecordConstructionEvaluator(RecordConstruction vertex, Query query) {
    super(vertex, query);
  }

  @Override public Record evaluate(InternalGreqlEvaluator evaluator) {
    RecordImpl resultRecord = RecordImpl.empty();
    IsRecordElementOf inc = vertex.getFirstIsRecordElementOfIncidence(EdgeDirection.IN);
    while (inc != null) {
      RecordElement currentElement = inc.getAlpha();
      RecordElementEvaluator vertexEval = (RecordElementEvaluator) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordConstructionEvaluator.java/left.java
      query
=======
      vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordConstructionEvaluator.java/right.java
      .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordConstructionEvaluator.java/left.java
      getVertexEvaluator(currentElement)
=======
      getMark(currentElement)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordConstructionEvaluator.java/right.java
      ;
      resultRecord = resultRecord.plus(vertexEval.getId(), vertexEval.getResult(evaluator));
      inc = inc.getNextIsRecordElementOfIncidence(EdgeDirection.IN);
    }
    return resultRecord;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCostsRecordConstruction(this, graphSize);
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordConstructionEvaluator.java/right.java
}