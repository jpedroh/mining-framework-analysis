package jmetal.experiments.settings;
import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.paes.PAES;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm PAES
 */
public class PAES_Settings extends Settings {
  private int maxEvaluations_;

  private int archiveSize_;

  private int biSections_;

  private double mutationProbability_;

  private double mutationDistributionIndex_;

  /**
   * Constructor
   * @throws JMException 
   */
  public PAES_Settings(String problem) throws JMException {
    super(problem);
    Object[] problemParams = { "Real" };
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    maxEvaluations_ = 25000;
    archiveSize_ = 100;
    biSections_ = 5;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    mutationDistributionIndex_ = 20.0;
  }

  /**
   * Configure the MOCell algorithm with default parameter experiments.settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Mutation mutation;
    algorithm = new PAES();
    algorithm.setProblem(problem_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    algorithm.setInputParameter("biSections", biSections_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);
    algorithm.addOperator("mutation", mutation);
    return algorithm;
  }

  /**
   * Configure PAES with user-defined parameter experiments.settings
   * @return A PAES algorithm object
   */
  @Override public Algorithm configure(Properties configuration) throws JMException {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm = new PAES();
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/PAES_Settings.java/right.java

    algorithm.setProblem(problem_);
    archiveSize_ = Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize_)));
    maxEvaluations_ = Integer.parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    biSections_ = Integer.parseInt(configuration.getProperty("biSections", String.valueOf(biSections_)));
    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex_)));
    return configure();
  }
}