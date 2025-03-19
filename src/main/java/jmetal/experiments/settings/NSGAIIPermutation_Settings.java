package jmetal.experiments.settings;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import jmetal.util.evaluator.SolutionSetEvaluator;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm NSGA-II (permutation encoding)
 */
public class NSGAIIPermutation_Settings extends Settings {
  private int populationSize_;

  private int maxEvaluations_;

  private double mutationProbability_;

  private double crossoverProbability_;

  /**
   * Constructor
   * @throws JMException 
   */
  public NSGAIIPermutation_Settings(String problem) throws JMException {
    super(problem);
    Object[] problemParams = { "Permutation" };
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    populationSize_ = 100;
    maxEvaluations_ = 25000;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability_ = 0.9;
  }

  /**
   * Configure NSGAII with user-defined parameter experiments.settings
   * @return A NSGAII algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator selection;
    Operator crossover;
    Operator mutation;
    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator();
    algorithm = new NSGAII(evaluator);
    algorithm.setProblem(problem_);
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", crossoverProbability_);
    crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover", parameters);
    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    mutation = MutationFactory.getMutationOperator("SwapMutation", parameters);
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);
    return algorithm;
  }

  /**
   * Configure NSGAII with user-defined parameter experiments.settings
   * @return A NSGAII algorithm object
   */
  @Override public Algorithm configure(Properties configuration) throws JMException {
    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator();

<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm = new NSGAII(evaluator);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/right.java

    algorithm.setProblem(problem_);
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxEvaluations_ = Integer.parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    crossoverProbability_ = Double.parseDouble(configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability_)));
    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));

<<<<<<< Unknown file: This is a bug in JDime.
=======
    parameters = new HashMap<String, Object>();
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/right.java

    return configure();
  }
}