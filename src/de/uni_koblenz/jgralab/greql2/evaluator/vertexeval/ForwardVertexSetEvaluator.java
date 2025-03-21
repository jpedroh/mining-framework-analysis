package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;
import de.uni_koblenz.jgralab.greql2.funlib.graph.ReachableVertices;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.ForwardVertexSet;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * Evaluates a ForwardVertexSet
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ForwardVertexSetEvaluator extends PathSearchEvaluator<ForwardVertexSet> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/right.java


  public ForwardVertexSetEvaluator(ForwardVertexSet vertex, Query query) {
    super(vertex, query);
  }

  private boolean initialized = false;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/left.java
  private VertexEvaluator<? extends Expression> startEval = null;
=======
  private VertexEvaluator startEval = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/right.java


  private final void initialize(InternalGreqlEvaluator evaluator) {
    PathDescription p = (PathDescription) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/left.java
    vertex.getFirstIsPathOfIncidence(EdgeDirection.IN).getAlpha()
=======
    vertex.getFirstIsPathOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/left.java
    PathDescriptionEvaluator<?> pathDescEval = (PathDescriptionEvaluator<?>) query.getVertexEvaluator(p);
=======
    PathDescriptionEvaluator pathDescEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(p);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/right.java

    Expression startExpression = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/left.java
    vertex.getFirstIsStartExprOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Expression) vertex.getFirstIsStartExprOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/right.java
    ;
    startEval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/left.java
    getVertexEvaluator(startExpression)
=======
    getMark(startExpression)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ForwardVertexSetEvaluator.java/right.java
    ;
    searchAutomaton = new DFA(pathDescEval.getNFA(evaluator));
    initialized = true;
  }

  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    if (!initialized) {
      initialize(evaluator);
    }
    Vertex startVertex = null;
    startVertex = (Vertex) startEval.getResult(evaluator);
    return ReachableVertices.search(startVertex, searchAutomaton);
  }
}