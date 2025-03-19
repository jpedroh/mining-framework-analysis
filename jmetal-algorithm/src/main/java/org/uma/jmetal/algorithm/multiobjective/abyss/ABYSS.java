package org.uma.jmetal.algorithm.multiobjective.abyss;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.impl.localsearch.MutationLocalSearch;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import java.util.Collections;
import java.util.List;

/**
 * Created by cbarba on 25/3/15.
 */
public class ABYSS extends AbstractABYSS<DoubleSolution> {
  public ABYSS(int numberOfSubranges, int solutionSetSize, int refSet1Size, int refSet2Size, int archiveSize, int maxEvaluations, CrowdingDistanceArchive archive, CrossoverOperator crossoverOperator, MutationLocalSearch improvement, DoubleProblem problem) {
    super(numberOfSubranges, solutionSetSize, refSet1Size, refSet2Size, archiveSize, maxEvaluations, archive, crossoverOperator, improvement, problem);
  }

  /**
     * Runs of the AbYSS algorithm.
     * as a result of the algorithm execution
     */
  @Override public void run() {
    try {
      DoubleSolution solution;
      initialListSolution();
      int newSolutions = 0;
      while (evaluations < maxEvaluations) {
        referenceSetUpdate(true);
        newSolutions = subSetGeneration();
        while (newSolutions > 0) {
          referenceSetUpdate(false);
          if (evaluations < maxEvaluations) {
            newSolutions = subSetGeneration();
          } else {
            newSolutions = 0;
          }
        }
        if (evaluations < maxEvaluations) {
          solutionSet.clear();
          for (int i = 0; i < refSet1.size(); i++) {
            solution = refSet1.get(i);
            marked.setAttribute(solution, false);
            solution = (DoubleSolution) improvementOperator.execute(solution);
            evaluations += improvementOperator.getEvaluations();
            solutionSet.add(solution);
          }
          refSet1.clear();
          refSet2.clear();
          archive.computeDistance();
          Collections.sort(archive.getSolutionList(), crowdingDistanceComparator);
          int insert = solutionSetSize / 2;
          if (insert > archive.getSolutionList().size()) {
            insert = archive.getSolutionList().size();
          }
          if (insert > (solutionSetSize - solutionSet.size())) {
            insert = solutionSetSize - solutionSet.size();
          }
          for (int i = 0; i < insert; i++) {
            solution = archive.getSolutionList().get(i);
            marked.setAttribute(solution, false);
            solutionSet.add(solution);
          }
          while (solutionSet.size() < solutionSetSize) {
            solution = diversificationGeneration();
            if (problem instanceof ConstrainedProblem) {
              ((ConstrainedProblem) problem).evaluateConstraints(solution);
            }
            problem.evaluate(solution);
            evaluations++;
            solution = (DoubleSolution) improvementOperator.execute(solution);
            evaluations += improvementOperator.getEvaluations();
            marked.setAttribute(solution, false);
            solutionSet.add(solution);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public List<? extends Solution> getResult() {
    return archive.getSolutionList();
  }

  private void initialListSolution() {
    try {
      DoubleSolution solution;
      for (int i = 0; i < this.solutionSetSize; i++) {
        solution = super.diversificationGeneration();
        problem.evaluate(solution);
        if (problem instanceof ConstrainedProblem) {
          ((ConstrainedProblem) problem).evaluateConstraints(solution);
        }
        evaluations++;
        solution = (DoubleSolution) improvementOperator.execute(solution);
        marked.setAttribute(solution, false);
        if (strenghtRawFitness.getAttribute(solution) == null) {
          strenghtRawFitness.setAttribute(solution, 0.0);
        }
        evaluations += improvementOperator.getEvaluations();
        solutionSet.add(solution);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}