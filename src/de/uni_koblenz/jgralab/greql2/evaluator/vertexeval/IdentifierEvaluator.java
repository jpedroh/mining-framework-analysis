package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.Identifier;

/**
 * Evaluates an identifier vertex in the GReQL syntaxgraph. Does nothing but
 * allow the access to the identifier name via VertexEvaluator.getResult
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class IdentifierEvaluator extends VertexEvaluator<Identifier> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IdentifierEvaluator.java/right.java


  public IdentifierEvaluator(Identifier vertex, Query query) {
    super(vertex, query);
  }

  @Override public String evaluate(InternalGreqlEvaluator evaluator) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IdentifierEvaluator.java/left.java
    vertex.get_name()
=======
    vertex.get_name()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IdentifierEvaluator.java/right.java
    ;
  }
}