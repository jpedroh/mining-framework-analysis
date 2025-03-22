package de.uni_koblenz.jgralab.greql2.evaluator.costmodel;

/**
 * Modells the costs a evaluation of a subtree causes.
 * 
 * It's a 3-Tupol containing
 * 
 * <ul>
 * <li>the costs of evaluating this vertex itself once (ownEvaluationCosts),</li>
 * <li>the costs all evaluations of this vertex that are needed when evaluating
 * the current query and</li> *
 * <li>the costs of evaluating the whole subtree below this vertex plus
 * ownEvaluationCosts (subtreeEvaluationCosts).</li>
 * </ul>
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class VertexCosts {
  /**
	 * The evaluation costs of the whole subtree
	 */
  public long subtreeEvaluationCosts = 0;

  /**
	 * The costs <b>one</b> evaluation of the root-vertex causes
	 */
  public long ownEvaluationCosts = 0;

  /**
	 * The costs all evaluations of the root-vertex
	 */
  public long iteratedEvaluationCosts = 0;

  /**
	 * Creates a new VertexCosts object
	 * 
	 * @param own
	 *            the costs for <b>one</b> evaluation of the vertex this object
	 *            represents
	 * @param iterated
	 *            the costs for <b>all</b> evaluations of the vertex this object
	 *            represents
	 * @param subtree
	 *            the costs for the evaluation of the subtree with the vertex as
	 *            root
	 */
  public VertexCosts(long own, long iterated, long subtree) {
    ownEvaluationCosts = own;
    iteratedEvaluationCosts = iterated;
    subtreeEvaluationCosts = subtree;
  }
}