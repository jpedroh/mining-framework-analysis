package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.OptionalPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * Evaluates an optional path description. Creates a NFA that accepts the
 * optional path description.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class OptionalPathDescriptionEvaluator extends PathDescriptionEvaluator<OptionalPathDescription> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/OptionalPathDescriptionEvaluator.java/right.java


  /**
	 * Creates a new OptionalPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public OptionalPathDescriptionEvaluator(OptionalPathDescription vertex, Query query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    PathDescription p = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/OptionalPathDescriptionEvaluator.java/left.java
    vertex.getFirstIsOptionalPathOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (PathDescription) vertex.getFirstIsOptionalPathOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/OptionalPathDescriptionEvaluator.java/right.java
    ;
    PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/OptionalPathDescriptionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/OptionalPathDescriptionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/OptionalPathDescriptionEvaluator.java/left.java
    getVertexEvaluator(p)
=======
    getMark(p)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/OptionalPathDescriptionEvaluator.java/right.java
    ;
    return NFA.createOptionalPathDescriptionNFA(pathEval.getNFA(evaluator));
  }
}