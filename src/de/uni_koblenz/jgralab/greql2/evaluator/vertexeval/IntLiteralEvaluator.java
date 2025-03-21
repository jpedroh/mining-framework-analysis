package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IntLiteral;

/**
 * Evaluates a Integer Literal, that means, provides access to the literal value
 * using the getResult(...)-Method. This is needed, because is should make no
 * difference for the other VertexEvaluators, if a value is the result of a
 * maybe complex evaluation or if it is a literal.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class IntLiteralEvaluator extends VertexEvaluator<IntLiteral> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntLiteralEvaluator.java/right.java


  public IntLiteralEvaluator(IntLiteral vertex, Query query) {
    super(vertex, query);
  }

  @Override public Integer evaluate(InternalGreqlEvaluator evaluator) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntLiteralEvaluator.java/left.java
    vertex.get_intValue()
=======
    vertex.get_intValue()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntLiteralEvaluator.java/right.java
    ;
  }
}