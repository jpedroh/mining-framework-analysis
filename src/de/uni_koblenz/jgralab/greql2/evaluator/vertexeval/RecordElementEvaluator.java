package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
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


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/right.java


  public String getId() {
    if (id == null) {
      RecordId idVertex = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/left.java
      vertex.getFirstIsRecordIdOfIncidence(EdgeDirection.IN).getAlpha()
=======
      (RecordId) vertex.getFirstIsRecordIdOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/right.java
      ;
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
  public RecordElementEvaluator(RecordElement vertex, Query query) {
    super(vertex, query);
  }

  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    if (expEval == null) {
      Expression recordElementExp = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/left.java
      vertex.getFirstIsRecordExprOfIncidence(EdgeDirection.IN).getAlpha()
=======
      (Expression) vertex.getFirstIsRecordExprOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/right.java
      ;
      expEval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/left.java
      query
=======
      vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/right.java
      .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/left.java
      getVertexEvaluator(recordElementExp)
=======
      getMark(recordElementExp)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/right.java
      ;
    }
    return expEval.getResult(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/left.java
    evaluator
=======
>>>>>>> Unknown file: This is a bug in JDime.
    );
  }
}