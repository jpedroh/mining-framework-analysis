package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;
import de.uni_koblenz.jgralab.greql2.schema.ExponentiatedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * Evaluates an exponentiated path description. Creates a NFA that accepts the
 * exponentiated path description.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ExponentiatedPathDescriptionEvaluator extends PathDescriptionEvaluator<ExponentiatedPathDescription> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/right.java


  /**
	 * Creates a new ExponentiatedPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public ExponentiatedPathDescriptionEvaluator(ExponentiatedPathDescription vertex, Query query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    PathDescription p = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/left.java
    vertex.getFirstIsExponentiatedPathOfIncidence().getAlpha()
=======
    (PathDescription) vertex.getFirstIsExponentiatedPathOfIncidence().getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/right.java
    ;
    PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/left.java
    getVertexEvaluator(p)
=======
    getMark(p)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/right.java
    ;
    VertexEvaluator<? extends Expression> exponentEvaluator = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/left.java
    getVertexEvaluator(vertex.getFirstIsExponentOfIncidence(EdgeDirection.IN).getAlpha())
=======
    getMark(vertex.getFirstIsExponentOfIncidence(EdgeDirection.IN).getAlpha())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/right.java
    ;
    Object exponentValue = exponentEvaluator.getResult(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/left.java
    evaluator
=======
>>>>>>> Unknown file: This is a bug in JDime.
    );
    int exponent = 0;
    if (exponentValue instanceof Integer) {
      exponent = (Integer) exponentValue;
    } else {
      throw new GreqlException("Exponent of ExponentiatedPathDescription is not convertable to integer value");
    }
    return NFA.createExponentiatedPathDescriptionNFA(pathEval.getNFA(evaluator), exponent);
  }
}