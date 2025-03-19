package jmetal.experiments.settings;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.moead.pMOEAD;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm MOEA/D
 */
public class pMOEAD_Settings extends Settings {
  private double cr_;

  private double f_;

  private int populationSize_;

  private int maxEvaluations_;

  private double mutationProbability_;

  private double mutationDistributionIndex_;

  private String dataDirectory_;

  private int t_;

  private double delta_;

  private int nr_;

  private int numberOfThreads_;

  /**
   * Constructor
   * @throws JMException 
   */
  public pMOEAD_Settings(String problem) throws JMException {
    super(problem);
    Object[] problemParams = { "Real" };
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    cr_ = 1.0;
    f_ = 0.5;
    populationSize_ = 600;
    maxEvaluations_ = 150000;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    mutationDistributionIndex_ = 20;
    t_ = 60;
    delta_ = 0.9;
    nr_ = 6;
    dataDirectory_ = "MOEAD_Weights";
    numberOfThreads_ = 4;
  }

  /**
   * Configure the algorithm with the specified parameter experiments.settings
   *
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    algorithm = new pMOEAD();
    algorithm.setProblem(problem_);
    algorithm.setInputParameter("numberOfThreads", numberOfThreads_);
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    algorithm.setInputParameter("dataDirectory", dataDirectory_);
    algorithm.setInputParameter("T", t_);
    algorithm.setInputParameter("delta", delta_);
    algorithm.setInputParameter("nr", nr_);
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("CR", cr_);
    parameters.put("F", f_);
    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);
    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    return algorithm;
  }

  /**
   * Configure pMOEAD with user-defined parameter experiments.settings
   * @return A pMOEAD algorithm object
   */
  @Override public Algorithm configure(Properties configuration) throws JMException {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm = new pMOEAD();
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/pMOEAD_Settings.java/right.java

    algorithm.setProblem(problem_);
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxEvaluations_ = Integer.parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    numberOfThreads_ = Integer.parseInt(configuration.getProperty("numberOfThreads", String.valueOf(numberOfThreads_)));
    dataDirectory_ = configuration.getProperty("dataDirectory", dataDirectory_);
    delta_ = Double.parseDouble(configuration.getProperty("delta", String.valueOf(delta_)));
    t_ = Integer.parseInt(configuration.getProperty("T", String.valueOf(t_)));
    nr_ = Integer.parseInt(configuration.getProperty("nr", String.valueOf(nr_)));
    return configure();
  }
}