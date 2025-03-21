package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;
import de.uni_koblenz.jgralab.greql2.schema.TransposedPathDescription;

/**
 * Evaluates a TransposedPathDescription vertex. Creates a NFA, which accepts
 * the PathDescription the vertex describes.
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public class TransposedPathDescriptionEvaluator extends PathDescriptionEvaluator<TransposedPathDescription> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TransposedPathDescriptionEvaluator.java/right.java


  /**
	 * Creates a new TransposedPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public TransposedPathDescriptionEvaluator(TransposedPathDescription vertex, Query query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    PathDescription p = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TransposedPathDescriptionEvaluator.java/left.java
    vertex.getFirstIsTransposedPathOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (PathDescription) vertex.getFirstIsTransposedPathOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TransposedPathDescriptionEvaluator.java/right.java
    ;
    PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TransposedPathDescriptionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TransposedPathDescriptionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TransposedPathDescriptionEvaluator.java/left.java
    getVertexEvaluator(p)
=======
    getMark(p)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TransposedPathDescriptionEvaluator.java/right.java
    ;
    return NFA.createTransposedPathDescriptionNFA(pathEval.getNFA(evaluator));
  }
}