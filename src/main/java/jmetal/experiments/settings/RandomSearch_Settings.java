package jmetal.experiments.settings;
import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.randomSearch.RandomSearch;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm RandomSearch
 */
public class RandomSearch_Settings extends Settings {
  private int maxEvaluations_ = 25000;

  /**
   * Constructor
   * @param problem Problem to solve
   * @throws JMException 
   */
  public RandomSearch_Settings(String problem) throws JMException {
    super(problem);
    Object[] problemParams = { "Real" };
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
  }

  /**
   * Configure the random search algorithm with default parameter experiments.settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    algorithm = new RandomSearch();
    algorithm.setProblem(problem_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    return algorithm;
  }

  /**
   * Configure SMPSO with user-defined parameter experiments.settings
   * @return A SMPSO algorithm object
   */
  @Override public Algorithm configure(Properties configuration) throws JMException {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    algorithm = new RandomSearch();
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/RandomSearch_Settings.java/right.java

    algorithm.setProblem(problem_);
    maxEvaluations_ = Integer.parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    return configure();
  }
}