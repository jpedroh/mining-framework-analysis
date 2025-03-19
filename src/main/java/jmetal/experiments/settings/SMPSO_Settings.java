package jmetal.experiments.settings;
import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.smpso.SMPSO;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm SMPSO
 */
public class SMPSO_Settings extends Settings {
  private int swarmSize_;

  private int maxIterations_;

  private int archiveSize_;

  private double mutationDistributionIndex_;

  private double mutationProbability_;

  private double c1Max_;

  private double c1Min_;

  private double c2Max_;

  private double c2Min_;

  private double weightMax_;

  private double weightMin_;

  private double changeVelocity1_;

  private double changeVelocity2_;

  /**
   * Constructor
   * @throws JMException 
   */
  public SMPSO_Settings(String problem) throws JMException {
    super(problem);
    Object[] problemParams = { "Real" };
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    swarmSize_ = 100;
    maxIterations_ = 250;
    archiveSize_ = 100;
    mutationDistributionIndex_ = 20.0;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    c1Max_ = 2.5;
    c1Min_ = 1.5;
    c2Max_ = 2.5;
    c2Min_ = 1.5;
    weightMax_ = 0.1;
    weightMin_ = 0.1;
    changeVelocity1_ = -1;
    changeVelocity2_ = -1;
  }

  /**
   * Configure SMPSO with user-defined parameter experiments.settings
   * @return A SMPSO algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Mutation mutation;
    algorithm = new SMPSO();
    algorithm.setProblem(problem_);
    algorithm.setInputParameter("swarmSize", swarmSize_);
    algorithm.setInputParameter("maxIterations", maxIterations_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("c1Min", c1Min_);
    algorithm.setInputParameter("c1Max", c1Max_);
    algorithm.setInputParameter("c2Min", c2Min_);
    algorithm.setInputParameter("c2Max", c2Max_);
    algorithm.setInputParameter("weightMin", weightMin_);
    algorithm.setInputParameter("weightMax", weightMax_);
    algorithm.setInputParameter("changeVelocity1", changeVelocity1_);
    algorithm.setInputParameter("changeVelocity2", changeVelocity2_);
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);
    algorithm.addOperator("mutation", mutation);
    return algorithm;
  }

  /**
   * Configure SMPSO with user-defined parameter experiments.settings
   * @return A SMPSO algorithm object
   */
  @Override public Algorithm configure(Properties configuration) throws JMException {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm = new SMPSO();
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/SMPSO_Settings.java/right.java

    algorithm.setProblem(problem_);
    swarmSize_ = Integer.parseInt(configuration.getProperty("swarmSize", String.valueOf(swarmSize_)));
    maxIterations_ = Integer.parseInt(configuration.getProperty("maxIterations", String.valueOf(maxIterations_)));
    archiveSize_ = Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize_)));
    c1Min_ = Double.parseDouble(configuration.getProperty("C1Min", String.valueOf(c1Min_)));
    c1Max_ = Double.parseDouble(configuration.getProperty("C1Max", String.valueOf(c1Max_)));
    c2Min_ = Double.parseDouble(configuration.getProperty("C2Min", String.valueOf(c2Min_)));
    c2Min_ = Double.parseDouble(configuration.getProperty("C2Max", String.valueOf(c2Max_)));
    weightMin_ = Double.parseDouble(configuration.getProperty("weightMin", String.valueOf(weightMin_)));
    weightMax_ = Double.parseDouble(configuration.getProperty("weightMax", String.valueOf(weightMax_)));

<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm.setInputParameter("c1Min", c1Min_);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/SMPSO_Settings.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm.setInputParameter("c1Max", c1Max_);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/SMPSO_Settings.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm.setInputParameter("c2Min", c2Min_);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/SMPSO_Settings.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm.setInputParameter("c2Max", c2Max_);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/SMPSO_Settings.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm.setInputParameter("weightMin", weightMin_);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/SMPSO_Settings.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm.setInputParameter("weightMax", weightMax_);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/SMPSO_Settings.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm.setInputParameter("changeVelocity1", changeVelocity1_);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/SMPSO_Settings.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm.setInputParameter("changeVelocity2", changeVelocity2_);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/SMPSO_Settings.java/right.java

    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex_)));
    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex_)));
    return configure();
  }
}