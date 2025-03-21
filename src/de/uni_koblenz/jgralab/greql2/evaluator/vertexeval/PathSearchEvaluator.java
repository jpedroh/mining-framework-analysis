package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;
import de.uni_koblenz.jgralab.greql2.schema.PathExpression;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathSearchEvaluator.java/left.java
/**
 * Abstract baseclass for all regular pathsearches, that are PathExistence,
 * ForwardVertexSet and BackwardVertexSet
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class PathSearchEvaluator<V extends PathExpression> extends VertexEvaluator<V> {
  /**
	 * The DFA used for PathSearch
	 */
  protected DFA searchAutomaton;

  /**
	 * this is the GReQL-Function which evaluates the pathexistence
	 */
  public PathSearchEvaluator(V vertex, Query query) {
    super(vertex, query);
    searchAutomaton = null;
  }
}
=======
/**
 * Abstract baseclass for all regular pathsearches, that are PathExistence,
 * ForwardVertexSet and BackwardVertexSet
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class PathSearchEvaluator extends VertexEvaluator {
  /**
	 * The DFA used for PathSearch
	 */
  protected DFA searchAutomaton;

  /**
	 * this is the GReQL-Function which evaluates the pathexistence
	 */
  public PathSearchEvaluator(GreqlEvaluator eval) {
    super(eval);
    searchAutomaton = null;
  }
}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathSearchEvaluator.java/right.java
