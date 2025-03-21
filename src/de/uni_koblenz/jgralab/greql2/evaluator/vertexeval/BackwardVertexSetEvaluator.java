package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PSet;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.funlib.graph.ReachableVertices;
import de.uni_koblenz.jgralab.greql2.schema.BackwardVertexSet;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * evaluates a BackwardVertexSet
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class BackwardVertexSetEvaluator extends PathSearchEvaluator<BackwardVertexSet> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/right.java


  public BackwardVertexSetEvaluator(BackwardVertexSet vertex, Query query) {
    super(vertex, query);
  }

  private boolean initialized = false;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/left.java
  private VertexEvaluator<? extends Expression> targetEval = null;
=======
  private VertexEvaluator targetEval = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/right.java


  private final void initialize(InternalGreqlEvaluator evaluator) {
    PathDescription p = (PathDescription) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/left.java
    vertex.getFirstIsPathOfIncidence(EdgeDirection.IN).getAlpha()
=======
    vertex.getFirstIsPathOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/left.java
    PathDescriptionEvaluator<?> pathDescEval = (PathDescriptionEvaluator<?>) query.getVertexEvaluator(p);
=======
    PathDescriptionEvaluator pathDescEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(p);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/right.java

    Expression targetExpression = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/left.java
    vertex.getFirstIsTargetExprOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Expression) vertex.getFirstIsTargetExprOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/right.java
    ;
    targetEval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/left.java
    getVertexEvaluator(targetExpression)
=======
    getMark(targetExpression)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/right.java
    ;
    NFA revertedNFA = NFA.revertNFA(pathDescEval.getNFA(evaluator));
    searchAutomaton = new DFA(revertedNFA);
    initialized = true;
  }

  @Override public PSet<Vertex> evaluate(InternalGreqlEvaluator evaluator) {
    if (!initialized) {
      initialize(evaluator);
    }
    Vertex targetVertex = null;
    targetVertex = (Vertex) targetEval.getResult(evaluator);
    return ReachableVertices.search(targetVertex, searchAutomaton);
  }
}