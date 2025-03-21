package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsSequenceElementOf;
import de.uni_koblenz.jgralab.greql2.schema.SequentialPathDescription;

public class SequentialPathDescriptionEvaluator extends PathDescriptionEvaluator<SequentialPathDescription> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SequentialPathDescriptionEvaluator.java/right.java


  /**
	 * Creates a new IteratedPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public SequentialPathDescriptionEvaluator(SequentialPathDescription vertex, Query query) {
    super(vertex, query);
  }

  @Override public NFA evaluate(InternalGreqlEvaluator evaluator) {
    IsSequenceElementOf inc = vertex.getFirstIsSequenceElementOfIncidence(EdgeDirection.IN);
    ArrayList<NFA> nfaList = new ArrayList<NFA>();
    while (inc != null) {
      PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SequentialPathDescriptionEvaluator.java/left.java
      query
=======
      vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SequentialPathDescriptionEvaluator.java/right.java
      .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SequentialPathDescriptionEvaluator.java/left.java
      getVertexEvaluator(inc.getAlpha())
=======
      getMark(inc.getAlpha())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/SequentialPathDescriptionEvaluator.java/right.java
      ;
      nfaList.add(pathEval.getNFA(evaluator));
      inc = inc.getNextIsSequenceElementOfIncidence(EdgeDirection.IN);
    }
    return NFA.createSequentialPathDescriptionNFA(nfaList);
  }
}