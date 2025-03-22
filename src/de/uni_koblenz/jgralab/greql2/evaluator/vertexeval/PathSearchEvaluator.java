package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;

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