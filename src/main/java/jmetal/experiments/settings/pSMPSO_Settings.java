package jmetal.experiments.settings;
import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.smpso.pSMPSO;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.parallel.MultithreadedEvaluator;
import jmetal.util.parallel.SynchronousParallelTaskExecutor;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm pSMPSO
 */
public class pSMPSO_Settings extends Settings {
  private int swarmSize_;

  private int maxIterations_;

  private int archiveSize_;

  private double mutationDistributionIndex_;

  private double mutationProbability_;

  private int numberOfThreads_;

  /**
   * Constructor
   * @throws JMException 
   */
  public pSMPSO_Settings(String problem) throws JMException {
    super(problem);
    Object[] problemParams = { "Real" };
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    swarmSize_ = 100;
    maxIterations_ = 250;
    archiveSize_ = 100;
    mutationDistributionIndex_ = 20.0;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    numberOfThreads_ = 8;
  }

  /**
   * Configure SMPSO with user-defined parameter experiments.settings
   * @return A SMPSO algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Mutation mutation;
    SynchronousParallelTaskExecutor parallelEvaluator = new MultithreadedEvaluator(numberOfThreads_);
    algorithm = new pSMPSO();
    algorithm.setProblem(problem_);
    ((pSMPSO) algorithm).setEvaluator(parallelEvaluator);
    algorithm.setInputParameter("swarmSize", swarmSize_);
    algorithm.setInputParameter("maxIterations", maxIterations_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);
    algorithm.addOperator("mutation", mutation);
    return algorithm;
  }

  /**
   * Configure pSMPSO with user-defined parameter experiments.settings
   * @return A pSMPSO algorithm object
   */
  @Override public Algorithm configure(Properties configuration) throws JMException {
    numberOfThreads_ = Integer.parseInt(configuration.getProperty("numberOfThreads", String.valueOf(numberOfThreads_)));

<<<<<<< Unknown file: This is a bug in JDime.
=======
    SynchronousParallelTaskExecutor parallelEvaluator = new MultithreadedEvaluator(numberOfThreads_);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/pSMPSO_Settings.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm = new pSMPSO();
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/pSMPSO_Settings.java/right.java

    algorithm.setProblem(problem_);
    ((pSMPSO) algorithm).setEvaluator(parallelEvaluator);
    swarmSize_ = Integer.parseInt(configuration.getProperty("swarmSize", String.valueOf(swarmSize_)));
    maxIterations_ = Integer.parseInt(configuration.getProperty("maxIterations", String.valueOf(maxIterations_)));
    archiveSize_ = Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize_)));
    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex_)));
    return configure();
  }
}