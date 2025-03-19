//  NSGAIIPermutation_Settings.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

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

<<<<<<< /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/left.java
  private int populationSize_  ;
  private int maxEvaluations_  ;
||||||| /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/base.java
  public int populationSize_  ;
  public int maxEvaluations_  ;
=======
  public int populationSize_;
  public int maxEvaluations_;
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/right.java

<<<<<<< /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/left.java
  private double mutationProbability_  ;
  private double crossoverProbability_ ;
||||||| /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/base.java
  public double mutationProbability_  ;
  public double crossoverProbability_ ;
=======
  public double mutationProbability_;
  public double crossoverProbability_;
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/right.java

  /**
   * Constructor
   * @throws JMException 
   */
<<<<<<< /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/left.java
  public NSGAIIPermutation_Settings(String problem) throws JMException {
    super(problem) ;
    
    Object [] problemParams = {"Permutation"};
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

	    // Default experiments.settings
    populationSize_ = 100   ;
    maxEvaluations_ = 25000 ;
||||||| /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/base.java
  public NSGAIIPermutation_Settings(String problem) {
    super(problem) ;
    
    Object [] problemParams = {"Permutation"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
      Configuration.logger_.log(Level.SEVERE, "Unable to get problem", e);
    }      
    
    // Default experiments.settings
    populationSize_ = 100   ;
    maxEvaluations_ = 25000 ;
=======
  public NSGAIIPermutation_Settings(String problem) {
    super(problem);

    Object[] problemParams = {"Permutation"};
    try {
      problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
      Configuration.logger_.log(Level.SEVERE, "Unable to get problem", e);
    }
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/right.java

<<<<<<< /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/left.java
    mutationProbability_  = 1.0/problem_.getNumberOfVariables();
    crossoverProbability_ = 0.9 ; 
  } 
  
||||||| /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/base.java
    mutationProbability_  = 1.0/problem_.getNumberOfVariables();
    crossoverProbability_ = 0.9 ; 
  } // NSGAIIPermutation_Settings
  
=======
    // Default experiments.settings
    populationSize_ = 100;
    maxEvaluations_ = 25000;

    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability_ = 0.9;
  }

>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/right.java
  /**
   * Configure NSGAII with user-defined parameter experiments.settings
   *
   * @return A NSGAII algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator selection;
    Operator crossover;
    Operator mutation;

    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator() ;

    // Creating the algorithm.
    algorithm = new NSGAII(evaluator);
    algorithm.setProblem(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);


    // Mutation and Crossover Permutation codification
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", crossoverProbability_);
    crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover", parameters);

    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    mutation = MutationFactory.getMutationOperator("SwapMutation", parameters);

<<<<<<< /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/left.java
    return algorithm ;
  } 
||||||| /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/base.java
    return algorithm ;
  } // configure
=======
    // Selection Operator 
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

    // Add the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    return algorithm;
  }
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/right.java

  /**
   * Configure NSGAII with user-defined parameter experiments.settings
   *
   * @return A NSGAII algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
<<<<<<< /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/left.java
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(maxEvaluations_)));
||||||| /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/base.java
    Algorithm algorithm ;
    Selection selection ;
    Crossover crossover ;
    Mutation mutation  ;

    // Creating the algorithm.
    algorithm = new NSGAII(problem_) ;

    // Algorithm parameters
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(maxEvaluations_)));
    algorithm.setInputParameter("populationSize",populationSize_);
    algorithm.setInputParameter("maxEvaluations",maxEvaluations_);
=======
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    Mutation mutation;

    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator() ;

    // Creating the algorithm.
    algorithm = new NSGAII(evaluator);
    algorithm.setProblem(problem_);

    // Algorithm parameters
    populationSize_ = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxEvaluations_ = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/right.java

<<<<<<< /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/left.java
    crossoverProbability_ = Double.parseDouble(configuration.getProperty("crossoverProbability",String.valueOf(crossoverProbability_)));
    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
||||||| /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/base.java
    // Mutation and Crossover for Real codification
    crossoverProbability_ = Double.parseDouble(configuration.getProperty("crossoverProbability",String.valueOf(crossoverProbability_)));

    HashMap<String, Object> parameters = new HashMap<String, Object>() ;
    parameters.put("probability", crossoverProbability_) ;
    crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover", parameters);

    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
    parameters = new HashMap<String, Object>() ;
    parameters.put("probability", mutationProbability_) ;
    mutation = MutationFactory.getMutationOperator("SwapMutation", parameters);
=======
    // Mutation and Crossover for Real codification
    crossoverProbability_ = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability_)));

    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", crossoverProbability_);
    crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover", parameters);

    mutationProbability_ = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    mutation = MutationFactory.getMutationOperator("SwapMutation", parameters);
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/right.java

<<<<<<< /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/left.java
    return configure() ;
||||||| /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/base.java
    // Selection Operator
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);

    return algorithm ;
=======
    // Selection Operator
    parameters = new HashMap<String, Object>();
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

    // Add the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    return algorithm;
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/experiments/settings/NSGAIIPermutation_Settings.java/right.java
  }
}
