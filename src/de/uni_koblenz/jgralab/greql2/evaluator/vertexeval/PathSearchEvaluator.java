package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;
import de.uni_koblenz.jgralab.greql2.schema.PathExpression;

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
  public PathSearchEvaluator(V vertex, QueryImpl query) {
    super(vertex, query);
    searchAutomaton = null;
  }
}