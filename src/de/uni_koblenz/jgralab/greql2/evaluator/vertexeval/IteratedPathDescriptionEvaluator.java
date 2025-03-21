package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IteratedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IterationType;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * Evaluates an iterated path description. Creates a NFA that accepts the
 * iterated path description.
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public class IteratedPathDescriptionEvaluator extends PathDescriptionEvaluator<IteratedPathDescription> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IteratedPathDescriptionEvaluator.java/right.java


  /**
	 * Creates a new IteratedPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public IteratedPathDescriptionEvaluator(IteratedPathDescription vertex, Query query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    PathDescription p = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IteratedPathDescriptionEvaluator.java/left.java
    vertex.getFirstIsIteratedPathOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (PathDescription) vertex.getFirstIsIteratedPathOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IteratedPathDescriptionEvaluator.java/right.java
    ;
    PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IteratedPathDescriptionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IteratedPathDescriptionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IteratedPathDescriptionEvaluator.java/left.java
    getVertexEvaluator(p)
=======
    getMark(p)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IteratedPathDescriptionEvaluator.java/right.java
    ;
    NFA createdNFA = NFA.createIteratedPathDescriptionNFA(pathEval.getNFA(evaluator), vertex.get_times() == IterationType.STAR);
    return createdNFA;
  }
}