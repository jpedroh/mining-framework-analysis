package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PCollection;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.schema.SetComprehension;

/**
 * Evaluates a SetComprehension vertex in the GReQL-2 Syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class SetComprehensionEvaluator extends 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SetComprehensionEvaluator.java/left.java
ComprehensionEvaluator<SetComprehension>
=======
ComprehensionEvaluator
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SetComprehensionEvaluator.java/right.java
 {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public SetComprehension getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SetComprehensionEvaluator.java/right.java


  /**
	 * Creates a new SetComprehensionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public SetComprehensionEvaluator(SetComprehension vertex, Query query) {
    super(vertex, query);
  }

  @Override protected PCollection<Object> getResultDatastructure(InternalGreqlEvaluator evaluator) {
    return JGraLab.set();
  }
}