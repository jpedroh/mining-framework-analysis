package jmetal.experiments.settings;
import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.smsemoa.SMSEMOA;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm SMSEMOA
 */
public class SMSEMOA_Settings extends Settings {
  private int populationSize_;

  private int maxEvaluations_;

  private double mutationProbability_;

  private double crossoverProbability_;

  private double crossoverDistributionIndex_;

  private double mutationDistributionIndex_;

  private double offset_;

  /**
   * Constructor
   * @throws JMException 
   */
  public SMSEMOA_Settings(String problem) throws JMException {
    super(problem);
    Object[] problemParams = { "Real" };
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    populationSize_ = 100;
    maxEvaluations_ = 25000;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability_ = 0.9;
    crossoverDistributionIndex_ = 20.0;
    mutationDistributionIndex_ = 20.0;
    offset_ = 100.0;
  }

  /**
   * Configure SMSEMOA with user-defined parameter experiments.settings
   * @return A SMSEMOA algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    Mutation mutation;
    algorithm = new SMSEMOA();
    algorithm.setProblem(problem_);
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    algorithm.setInputParameter("offset", offset_);
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", crossoverProbability_);
    parameters.put("distributionIndex", crossoverDistributionIndex_);
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);
    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("RandomSelection", parameters);
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);
    return algorithm;
  }

  /**
   * Configure SMSEMOA with user-defined parameter experiments.settings
   * @return A SMSEMOA algorithm object
   */
  @Override public Algorithm configure(Properties configuration) throws JMException {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm = new SMSEMOA();
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/SMSEMOA_Settings.java/right.java

    algorithm.setProblem(problem_);
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxEvaluations_ = Integer.parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    offset_ = Double.parseDouble(configuration.getProperty("offset", String.valueOf(offset_)));
    crossoverProbability_ = Double.parseDouble(configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability_)));
    crossoverDistributionIndex_ = Double.parseDouble(configuration.getProperty("crossoverDistributionIndex", String.valueOf(crossoverDistributionIndex_)));
    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex_)));
    ;
    return configure();
  }
}