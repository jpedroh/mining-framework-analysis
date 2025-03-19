package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy;
import org.uma.jmetal.algorithm.impl.AbstractEvolutionStrategy;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import java.util.*;

/**
 * Class implementing the CMA-ES algorithm
 */
public class CovarianceMatrixAdaptationEvolutionStrategy extends AbstractEvolutionStrategy<DoubleSolution, DoubleSolution> {
  private Comparator<Solution> comparator;

  private int lambda;

  private int evaluations;

  private int maxEvaluations;

  private double[] typicalX;

  private DoubleProblem problem;

  /**
   * CMA-ES state variables
   */
  private double[] distributionMean;

  private double sigma;

  private double[][] c;

  private double[] pathsC;

  private double[] pathsSigma;

  private int mu;

  private double[] weights;

  private double muEff;

  private double cumulationC;

  private double cumulationSigma;

  private double c1;

  private double cmu;

  private double dampingSigma;

  private double[][] b;

  private double[] diagD;

  private double[][] invSqrtC;

  private int eigenEval;

  private double chiN;

  private DoubleSolution bestSolutionEver = null;

  private Random rand;

  /**
   * Constructor
   */
  private CovarianceMatrixAdaptationEvolutionStrategy(Builder builder) {
    this.problem = builder.problem;
    this.lambda = builder.lambda;
    this.maxEvaluations = builder.maxEvaluations;
    this.typicalX = builder.typicalX;
    long seed = System.currentTimeMillis();
    rand = new Random(seed);
    comparator = new ObjectiveComparator(0);
    initializeInternalParameters();
  }

  public int getLambda() {
    return lambda;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public static class Builder {
    private DoubleProblem problem;

    private static final int DEFAULT_LAMBDA = 10;

    private static final int DEFAULT_MAX_EVALUATIONS = 1000000;

    private int lambda;

    private int maxEvaluations;

    private double[] typicalX;

    public Builder(DoubleProblem problem) {
      this.problem = problem;
      lambda = DEFAULT_LAMBDA;
      maxEvaluations = DEFAULT_MAX_EVALUATIONS;
    }

    public Builder setLambda(int lambda) {
      this.lambda = lambda;
      return this;
    }

    public Builder setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations;
      return this;
    }

    public Builder setTypicalX(double[] typicalX) {
      this.typicalX = typicalX;
      return this;
    }

    public CovarianceMatrixAdaptationEvolutionStrategy build() {
      return new CovarianceMatrixAdaptationEvolutionStrategy(this);
    }
  }

  @Override protected void initProgress() {
    evaluations = 0;
  }

  @Override protected void updateProgress() {
    evaluations += lambda;
    updateInternalParameters();
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override protected List<DoubleSolution> createInitialPopulation() {
    List<DoubleSolution> population = new ArrayList<>(lambda);
    for (int i = 0; i < lambda; i++) {
      DoubleSolution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    for (DoubleSolution solution : population) {
      problem.evaluate(solution);
    }
    return population;
  }

  @Override protected List<DoubleSolution> selection(List<DoubleSolution> population) {
    return population;
  }

  @Override protected List<DoubleSolution> reproduction(List<DoubleSolution> population) {
    List<DoubleSolution> offspringPopulation = new ArrayList<>(lambda);
    for (int iNk = 0; iNk < lambda; iNk++) {
      offspringPopulation.add(sampleSolution());
    }
    return offspringPopulation;
  }

  @Override protected List<DoubleSolution> replacement(List<DoubleSolution> population, List<DoubleSolution> offspringPopulation) {
    return offspringPopulation;
  }

  @Override public DoubleSolution getResult() {
    return bestSolutionEver;
  }

  private void initializeInternalParameters() {
    int numberOfVariables = problem.getNumberOfVariables();
    if (typicalX != null) {
      distributionMean = typicalX;
    } else {
      distributionMean = new double[numberOfVariables];
      for (int i = 0; i < numberOfVariables; i++) {
        distributionMean[i] = rand.nextDouble();
      }
    }
    sigma = 0.3;
    mu = (int) Math.floor(lambda / 2);
    weights = new double[mu];
    double sum = 0;
    for (int i = 0; i < mu; i++) {
      weights[i] = (Math.log(mu + 1 / 2) - Math.log(i + 1));
      sum += weights[i];
    }
    for (int i = 0; i < mu; i++) {
      weights[i] = weights[i] / sum;
    }
    double sum1 = 0;
    double sum2 = 0;
    for (int i = 0; i < mu; i++) {
      sum1 += weights[i];
      sum2 += weights[i] * weights[i];
    }
    muEff = sum1 * sum1 / sum2;
    cumulationC = (4 + muEff / numberOfVariables) / (numberOfVariables + 4 + 2 * muEff / numberOfVariables);
    cumulationSigma = (muEff + 2) / (numberOfVariables + muEff + 5);
    c1 = 2 / ((numberOfVariables + 1.3) * (numberOfVariables + 1.3) + muEff);
    cmu = Math.min(1 - c1, 2 * (muEff - 2 + 1 / muEff) / ((numberOfVariables + 2) * (numberOfVariables + 2) + muEff));
    dampingSigma = 1 + 2 * Math.max(0, Math.sqrt((muEff - 1) / (numberOfVariables + 1)) - 1) + cumulationSigma;
    diagD = new double[numberOfVariables];
    pathsC = new double[numberOfVariables];
    pathsSigma = new double[numberOfVariables];
    b = new double[numberOfVariables][numberOfVariables];
    c = new double[numberOfVariables][numberOfVariables];
    invSqrtC = new double[numberOfVariables][numberOfVariables];
    for (int i = 0; i < numberOfVariables; i++) {
      pathsC[i] = 0;
      pathsSigma[i] = 0;
      diagD[i] = 1;
      for (int j = 0; j < numberOfVariables; j++) {
        b[i][j] = 0;
        invSqrtC[i][j] = 0;
      }
      for (int j = 0; j < i; j++) {
        c[i][j] = 0;
      }
      b[i][i] = 1;
      c[i][i] = diagD[i] * diagD[i];
      invSqrtC[i][i] = 1;
    }
    eigenEval = 0;
    chiN = Math.sqrt(numberOfVariables) * (1 - 1 / (4 * numberOfVariables) + 1 / (21 * numberOfVariables * numberOfVariables));
  }

  private void updateInternalParameters() {
    int numberOfVariables = problem.getNumberOfVariables();
    double[] oldDistributionMean = new double[numberOfVariables];
    System.arraycopy(distributionMean, 0, oldDistributionMean, 0, numberOfVariables);
    Collections.sort(getPopulation(), comparator);
    storeBest();
    updateDistributionMean();
    int hsig = updateEvolutionPaths(oldDistributionMean);
    adaptCovarianceMatrix(oldDistributionMean, hsig);
    double psxps = CMAESUtils.norm(pathsSigma);
    sigma *= Math.exp((cumulationSigma / dampingSigma) * (Math.sqrt(psxps) / chiN - 1));
    decomposeCovarianceMatrix();
  }

  private void updateDistributionMean() {
    int numberOfVariables = problem.getNumberOfVariables();
    for (int i = 0; i < numberOfVariables; i++) {
      distributionMean[i] = 0.;
      for (int iNk = 0; iNk < mu; iNk++) {
        double variableValue = (double) getPopulation().get(iNk).getVariableValue(i);
        distributionMean[i] += weights[iNk] * variableValue;
      }
    }
  }

  private int updateEvolutionPaths(double[] oldDistributionMean) {
    int numberOfVariables = problem.getNumberOfVariables();
    double[] artmp = new double[numberOfVariables];
    for (int i = 0; i < numberOfVariables; i++) {
      artmp[i] = 0;
      for (int j = 0; j < numberOfVariables; j++) {
        artmp[i] += invSqrtC[i][j] * (distributionMean[j] - oldDistributionMean[j]) / sigma;
      }
    }
    for (int i = 0; i < numberOfVariables; i++) {
      pathsSigma[i] = (1. - cumulationSigma) * pathsSigma[i] + Math.sqrt(cumulationSigma * (2. - cumulationSigma) * muEff) * artmp[i];
    }
    double psxps = CMAESUtils.norm(pathsSigma);
    int hsig = 0;
    if ((Math.sqrt(psxps) / Math.sqrt(1. - Math.pow(1. - cumulationSigma, 2. * evaluations / lambda)) / chiN) < (1.4 + 2. / (numberOfVariables + 1.))) {
      hsig = 1;
    }
    for (int i = 0; i < numberOfVariables; i++) {
      pathsC[i] = (1. - cumulationC) * pathsC[i] + hsig * Math.sqrt(cumulationC * (2. - cumulationC) * muEff) * (distributionMean[i] - oldDistributionMean[i]) / sigma;
    }
    return hsig;
  }

  private void adaptCovarianceMatrix(double[] oldDistributionMean, int hsig) {
    int numberOfVariables = problem.getNumberOfVariables();
    for (int i = 0; i < numberOfVariables; i++) {
      for (int j = 0; j <= i; j++) {
        c[i][j] = (1 - c1 - cmu) * c[i][j] + c1 * (pathsC[i] * pathsC[j] + (1 - hsig) * cumulationC * (2. - cumulationC) * c[i][j]);
        for (int k = 0; k < mu; k++) {
          double valueI = getPopulation().get(k).getVariableValue(i);
          double valueJ = getPopulation().get(k).getVariableValue(j);
          c[i][j] += cmu * weights[k] * (valueI - oldDistributionMean[i]) * (valueJ - oldDistributionMean[j]) / sigma / sigma;
        }
      }
    }
  }

  private void decomposeCovarianceMatrix() {
    int numberOfVariables = problem.getNumberOfVariables();
    if (evaluations - eigenEval > lambda / (c1 + cmu) / numberOfVariables / 10) {
      eigenEval = evaluations;
      for (int i = 0; i < numberOfVariables; i++) {
        for (int j = 0; j <= i; j++) {
          b[i][j] = b[j][i] = c[i][j];
        }
      }
      double[] offdiag = new double[numberOfVariables];
      CMAESUtils.tred2(numberOfVariables, b, diagD, offdiag);
      CMAESUtils.tql2(numberOfVariables, diagD, offdiag, b);
      if (CMAESUtils.checkEigenSystem(numberOfVariables, c, diagD, b) > 0) {
        evaluations = maxEvaluations;
      }
      for (int i = 0; i < numberOfVariables; i++) {
        if (diagD[i] < 0) {
          JMetalLogger.logger.severe("CovarianceMatrixAdaptationEvolutionStrategy.updateDistribution:" + " WARNING - an eigenvalue has become negative.");
          evaluations = maxEvaluations;
        }
        diagD[i] = Math.sqrt(diagD[i]);
      }
      double[][] artmp2 = new double[numberOfVariables][numberOfVariables];
      for (int i = 0; i < numberOfVariables; i++) {
        for (int j = 0; j < numberOfVariables; j++) {
          artmp2[i][j] = b[i][j] * (1 / diagD[j]);
        }
      }
      for (int i = 0; i < numberOfVariables; i++) {
        for (int j = 0; j < numberOfVariables; j++) {
          invSqrtC[i][j] = 0.0;
          for (int k = 0; k < numberOfVariables; k++) {
            invSqrtC[i][j] += artmp2[i][k] * b[j][k];
          }
        }
      }
    }
  }

  private DoubleSolution sampleSolution() {
    DoubleSolution solution = problem.createSolution();
    int numberOfVariables = problem.getNumberOfVariables();
    double[] artmp = new double[numberOfVariables];
    double sum;
    for (int i = 0; i < numberOfVariables; i++) {
      artmp[i] = diagD[i] * rand.nextGaussian();
    }
    for (int i = 0; i < numberOfVariables; i++) {
      sum = 0.0;
      for (int j = 0; j < numberOfVariables; j++) {
        sum += b[i][j] * artmp[j];
      }
      double value = distributionMean[i] + sigma * sum;
      if (value > problem.getUpperBound(i)) {
        value = problem.getUpperBound(i);
      } else {
        if (value < problem.getLowerBound(i)) {
          value = problem.getLowerBound(i);
        }
      }
      solution.setVariableValue(i, value);
    }
    return solution;
  }

  private void storeBest() {
    if ((bestSolutionEver == null) || (bestSolutionEver.getObjective(0) > getPopulation().get(0).getObjective(0))) {
      bestSolutionEver = getPopulation().get(0);
    }
  }
}