package de.uni_koblenz.jgralab.greql2.optimizer;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.exception.OptimizerException;
import de.uni_koblenz.jgralab.greql2.schema.Greql2;

/**
 * This interface should be implemented by all optimizers, that could be used
 * with GReQL 2.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface Optimizer {
  /**
	 * Optimizes the given GReQL 2 syntaxgraph. The given syntaxgraph is
	 * optimized after the method finished.
	 * 
	 * @param eval
	 *            the GreqlEvaluator, which calls this method
	 * @param syntaxgraph
	 *            The GReQL 2 syntaxgraph to optimize
	 * @return <code>true</code> if a transformation was done,
	 *         <code>false</code> if this {@link Optimizer} couldn't do
	 *         anything.
	 * @throws OptimizerException
	 *             on failures while optimization
	 */
  public boolean optimize(GreqlEvaluator eval, Greql2 syntaxgraph) throws OptimizerException;

  /**
	 * @return true, if this optimizer and the given one are logical equivalent,
	 *         that means, if the optimization result will be the same
	 */
  public boolean isEquivalent(Optimizer optimizer);
}