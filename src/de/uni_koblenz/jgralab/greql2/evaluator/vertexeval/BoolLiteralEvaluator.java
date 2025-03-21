package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.schema.BoolLiteral;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;

/**
 * Evaluates a boolean literal, that means, provides access to the literal value
 * using the getResult(...)-Method. This is needed, because is should make no
 * difference for the other VertexEvaluators, if a value is the result of a
 * maybe complex evaluation or if it is a literal.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class BoolLiteralEvaluator extends VertexEvaluator<BoolLiteral> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BoolLiteralEvaluator.java/right.java


  public BoolLiteralEvaluator(BoolLiteral vertex, Query query) {
    super(vertex, query);
  }

  @Override public Boolean evaluate(InternalGreqlEvaluator evaluator) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BoolLiteralEvaluator.java/left.java
    vertex.is_boolValue()
=======
    vertex.is_boolValue()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BoolLiteralEvaluator.java/right.java
    ;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public double calculateEstimatedSelectivity(GraphSize graphSize) {
    if (vertex.is_boolValue()) {
      return 1;
    }
    return 0;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BoolLiteralEvaluator.java/right.java
}