package jmetal.experiments.settings;
import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.omopso.OMOPSO;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.NonUniformMutation;
import jmetal.operators.mutation.UniformMutation;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm OMOPSO
 */
public class OMOPSO_Settings extends Settings {
  private int swarmSize_;

  private int maxIterations_;

  private int archiveSize_;

  private double perturbationIndex_;

  private double mutationProbability_;

  /**
   * Constructor
   * @throws JMException 
   */
  public OMOPSO_Settings(String problem) throws JMException {
    super(problem);
    Object[] problemParams = { "Real" };
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    swarmSize_ = 100;
    maxIterations_ = 250;
    archiveSize_ = 100;
    perturbationIndex_ = 0.5;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
  }

  /**
   * Configure OMOPSO with user-defined parameter experiments.settings
   * @return A OMOPSO algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Mutation uniformMutation;
    Mutation nonUniformMutation;
    algorithm = new OMOPSO();
    algorithm.setProblem(problem_);
    algorithm.setInputParameter("swarmSize", swarmSize_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("maxIterations", maxIterations_);
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("perturbation", perturbationIndex_);
    uniformMutation = new UniformMutation(parameters);
    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("perturbation", perturbationIndex_);
    parameters.put("maxIterations", maxIterations_);
    nonUniformMutation = new NonUniformMutation(parameters);
    algorithm.addOperator("uniformMutation", uniformMutation);
    algorithm.addOperator("nonUniformMutation", nonUniformMutation);
    return algorithm;
  }

  /**
   * Configure dMOPSO with user-defined parameter experiments.settings
   * @return A dMOPSO algorithm object
   */
  @Override public Algorithm configure(Properties configuration) throws JMException {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm = new OMOPSO();
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/OMOPSO_Settings.java/right.java

    algorithm.setProblem(problem_);
    swarmSize_ = Integer.parseInt(configuration.getProperty("swarmSize", String.valueOf(swarmSize_)));
    maxIterations_ = Integer.parseInt(configuration.getProperty("maxIterations", String.valueOf(maxIterations_)));
    archiveSize_ = Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize_)));
    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    perturbationIndex_ = Double.parseDouble(configuration.getProperty("perturbationIndex", String.valueOf(mutationProbability_)));
    return configure();
  }
}