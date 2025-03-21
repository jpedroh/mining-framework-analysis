package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IntermediateVertexPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IsSubPathOf;

/**
 * Evaluates an IntermediateVertexPathDescription.
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public class IntermediateVertexPathDescriptionEvaluator extends PathDescriptionEvaluator<IntermediateVertexPathDescription> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/right.java


  /**
	 * Creates a new IntermediateVertexPathDescriptionEvaluator for the given
	 * vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public IntermediateVertexPathDescriptionEvaluator(IntermediateVertexPathDescription vertex, Query query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    IsSubPathOf inc = vertex.getFirstIsSubPathOfIncidence(EdgeDirection.IN);
    PathDescriptionEvaluator<?> firstEval = (PathDescriptionEvaluator<?>) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/left.java
    getVertexEvaluator(inc.getAlpha())
=======
    getMark(inc.getAlpha())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/right.java
    ;
    NFA firstNFA = firstEval.getNFA(evaluator);
    inc = inc.getNextIsSubPathOfIncidence(EdgeDirection.IN);
    PathDescriptionEvaluator<?> secondEval = (PathDescriptionEvaluator<?>) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/left.java
    getVertexEvaluator(inc.getAlpha())
=======
    getMark(inc.getAlpha())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/right.java
    ;
    NFA secondNFA = secondEval.getNFA(evaluator);
    VertexEvaluator<? extends Expression> vertexEval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/left.java
    getVertexEvaluator(vertex.getFirstIsIntermediateVertexOfIncidence(EdgeDirection.IN).getAlpha())
=======
    getMark(vertex.getFirstIsIntermediateVertexOfIncidence(EdgeDirection.IN).getAlpha())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntermediateVertexPathDescriptionEvaluator.java/right.java
    ;
    return NFA.createIntermediateVertexPathDescriptionNFA(firstNFA, vertexEval, secondNFA);
  }
}