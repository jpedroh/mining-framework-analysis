package jmetal.operators.selection;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import java.util.Comparator;
import java.util.HashMap;

/**
 * This class implements a selection operator used for selecting the best 
 * solution in a SolutionSet according to a given comparator
 */
public class BestSolutionSelection extends Selection {
  /**
   *
   */
  private static final long serialVersionUID = 7515153213699830920L;

  private Comparator<Solution> comparator_;

  @SuppressWarnings(value = { "unchecked" }) public BestSolutionSelection(HashMap<String, Object> parameters) {
    super(parameters);
    comparator_ = null;
    Object obj = parameters.get("comparator");
    if (obj instanceof Comparator<?>) {
      comparator_ = (Comparator<Solution>) obj;
    }
  }

  /**
   * Performs the operation
   * @param object Object representing a SolutionSet.
   * @return the best solution found
   */
  public Object execute(Object object) {
    SolutionSet solutionSet = (SolutionSet) object;
    if (solutionSet.size() == 0) {
      return null;
    }
    int bestSolution;
    bestSolution = 0;
    for (int i = 1; i < solutionSet.size(); i++) {
      if (comparator_.compare(solutionSet.get(i), solutionSet.get(bestSolution)) < 0) {
        bestSolution = i;
      }
    }
    return bestSolution;
  }
}