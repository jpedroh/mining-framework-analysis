package jmetal.qualityIndicator;
import jmetal.core.SolutionSet;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class R2 {
  public jmetal.qualityIndicator.util.MetricsUtil utils_;

  double[][] matrix_ = null;

  double[][] lambda_ = null;

  int nObj_ = 0;

  /**
   * Constructor
   * Creates a new instance of the R2 indicator for a problem with two objectives
   * and 100 lambda vectors
   */
  public R2() {
    utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
    nObj_ = 2;
    lambda_ = new double[100][2];
    for (int n = 0; n < 100; n++) {
      double a = 1.0 * n / (100 - 1);
      lambda_[n][0] = a;
      lambda_[n][1] = 1 - a;
    }
  }

  /**
   * Constructor
   * Creates a new instance of the R2 indicator for a problem with two objectives
   * and N lambda vectors
   */
  public R2(int nVectors) {
    utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
    nObj_ = 2;
    lambda_ = new double[nVectors][2];
    for (int n = 0; n < nVectors; n++) {
      double a = 1.0 * n / (nVectors - 1);
      lambda_[n][0] = a;
      lambda_[n][1] = 1 - a;
    }
  }

  /**
   * Constructor
   * Creates a new instance of the R2 indicator for nDimensiosn
   * It loads the weight vectors from the file fileName
   */
  public R2(int nObj, String file) {
    utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
    nObj_ = nObj;
    try {
      FileInputStream fis = new FileInputStream(file);
      InputStreamReader isr = new InputStreamReader(fis);
      BufferedReader br = new BufferedReader(isr);
      int numberOfObjectives = 0;
      int i = 0;
      int j = 0;
      String aux = br.readLine();
      LinkedList<double[]> list = new LinkedList<double[]>();
      while (aux != null) {
        StringTokenizer st = new StringTokenizer(aux);
        j = 0;
        numberOfObjectives = st.countTokens();
        double[] vector = new double[nObj];
        while (st.hasMoreTokens()) {
          double value = new Double(st.nextToken());
          vector[j++] = value;
        }
        list.add(vector);
        aux = br.readLine();
      }
      br.close();
      lambda_ = new double[list.size()][];
      int index = 0;
      for (double[] aList : list) {
        lambda_[index++] = aList;
      }
    } catch (Exception e) {
      Configuration.logger_.log(Level.SEVERE, "initUniformWeight: failed when reading for file: " + file, e);
    }
  }

  /**
   * Returns the element contributing the most to the R2 indicator
   */
  public int getBest(double[][] approximation, double[][] paretoFront) {
    int index_best = -1;
    double value = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < approximation.length; i++) {
      double aux = this.R2Without(approximation, paretoFront, i);
      if (aux > value) {
        index_best = i;
        value = aux;
      }
    }
    return index_best;
  }

  /**
   * This class can be call from the command line. At least three parameters are
   * required: 1) the name of the file containing the front, 2) the number of
   * objectives 2) a file containing the reference point / the Optimal Pareto
   * front for normalizing 3) the file containing the weight vector
   * 
   * @throws JMException
   */
  public static void main(String[] args) throws JMException {
    if (args.length < 3) {
      throw new JMException("Error using R2. Usage: \n java jmetal.qualityIndicator.Hypervolume " + "<SolutionFrontFile> " + "<TrueFrontFile> " + "<getNumberOfObjectives>");
    }
    R2 qualityIndicator;
    int nObj = new Integer(args[2]);
    if (nObj == 2 && args.length == 3) {
      qualityIndicator = new R2();
    } else {
      qualityIndicator = new R2(nObj, args[3]);
    }
    double[][] approximationFront = qualityIndicator.utils_.readFront(args[0]);
    double[][] paretoFront = qualityIndicator.utils_.readFront(args[1]);
    double value = qualityIndicator.r2(approximationFront, paretoFront);
    Configuration.logger_.info("" + value);
    Configuration.logger_.info("" + qualityIndicator.R2Without(approximationFront, paretoFront, 1));
    Configuration.logger_.info("" + qualityIndicator.R2Without(approximationFront, paretoFront, 15));
    Configuration.logger_.info("" + qualityIndicator.R2Without(approximationFront, paretoFront, 25));
    Configuration.logger_.info("" + qualityIndicator.R2Without(approximationFront, paretoFront, 75));
  }

  /**
   * Returns the R2 indicator value of a given front
   */
  private double R2Without(double[][] approximation, double[][] paretoFront, int index) {
    double[] maximumValue;
    double[] minimumValue;
    double[][] normalizedApproximation;
    double[][] normalizedParetoFront;
    maximumValue = utils_.getMaximumValues(paretoFront, nObj_);
    minimumValue = utils_.getMinimumValues(paretoFront, nObj_);
    normalizedApproximation = utils_.getNormalizedFront(approximation, maximumValue, minimumValue);
    normalizedParetoFront = utils_.getNormalizedFront(paretoFront, maximumValue, minimumValue);
    matrix_ = new double[approximation.length][lambda_.length];
    for (int i = 0; i < approximation.length; i++) {
      for (int j = 0; j < lambda_.length; j++) {
        matrix_[i][j] = lambda_[j][0] * Math.abs(normalizedApproximation[i][0]);
        for (int n = 1; n < nObj_; n++) {
          matrix_[i][j] = Math.max(matrix_[i][j], lambda_[j][n] * Math.abs(normalizedApproximation[i][n]));
        }
      }
    }
    double sumWithout = 0.0;
    for (int i = 0; i < lambda_.length; i++) {
      double tmp;
      if (index != 0) {
        tmp = matrix_[0][i];
      } else {
        tmp = matrix_[1][i];
      }
      for (int j = 0; j < approximation.length; j++) {
        if (j != index) {
          tmp = Math.min(tmp, matrix_[j][i]);
        }
      }
      sumWithout += tmp;
    }
    return sumWithout / (double) lambda_.length;
  }

  /**
   * Returns the element contributing the less to the R2
   */
  public int getWorst(double[][] approximation, double[][] paretoFront) {
    int index_worst = -1;
    double value = Double.POSITIVE_INFINITY;
    for (int i = 0; i < approximation.length; i++) {
      double aux = this.R2Without(approximation, paretoFront, i);
      if (aux < value) {
        index_worst = i;
        value = aux;
      }
    }
    return index_worst;
  }

  /**
   * Returns the element contributing the most to the R2
   */
  public int getBest(SolutionSet set) {
    double[][] approximationFront = set.writeObjectivesToMatrix();
    double[][] trueFront = set.writeObjectivesToMatrix();
    return this.getBest(approximationFront, trueFront);
  }

  /**
   * Returns the element contributing the less to the R2
   */
  public int getWorst(SolutionSet set) {
    double[][] approximationFront = set.writeObjectivesToMatrix();
    double[][] trueFront = set.writeObjectivesToMatrix();
    return this.getWorst(approximationFront, trueFront);
  }

  /**
   * Returns the element contributing the most to the R2 indicator
   */
  public int[] getNBest(double[][] approximation, double[][] paretoFront, int N) {
    int[] index_bests = new int[approximation.length];
    double[] values = new double[approximation.length];
    for (int i = 0; i < approximation.length; i++) {
      values[i] = this.R2Without(approximation, paretoFront, i);
      index_bests[i] = i;
    }
    for (int i = 0; i < approximation.length; i++) {
      for (int j = i; j < approximation.length; j++) {
        if (values[j] < values[i]) {
          double aux = values[j];
          values[j] = values[i];
          values[i] = aux;
          int aux_index = index_bests[j];
          index_bests[j] = index_bests[i];
          index_bests[i] = aux_index;
        }
      }
    }
    int[] res = new int[N];
    System.arraycopy(index_bests, 0, res, 0, N);
    return res;
  }

  /**
   * Returns the indexes of the N best solutions according to this indicator
   */
  public int[] getNBest(SolutionSet set, int N) {
    double[][] approximationFront = set.writeObjectivesToMatrix();
    double[][] trueFront = set.writeObjectivesToMatrix();
    return this.getNBest(approximationFront, trueFront, N);
  }

  /**
   * Returns the R2 indicator value of a given front
   * @param front The front 
   * @param trueParetoFront The true Pareto front
   * @param numberOfObjectives The number of objectives
   * @param lambda A vector containing the lambda vectors for R2
   */
  public double r2(double[][] approximation, double[][] paretoFront) {
    double[] maximumValue;
    double[] minimumValue;
    double[][] normalizedApproximation;
    double[][] normalizedParetoFront;
    maximumValue = utils_.getMaximumValues(paretoFront, nObj_);
    minimumValue = utils_.getMinimumValues(paretoFront, nObj_);
    normalizedApproximation = utils_.getNormalizedFront(approximation, maximumValue, minimumValue);
    normalizedParetoFront = utils_.getNormalizedFront(paretoFront, maximumValue, minimumValue);
    matrix_ = new double[approximation.length][lambda_.length];
    for (int i = 0; i < approximation.length; i++) {
      for (int j = 0; j < lambda_.length; j++) {
        matrix_[i][j] = lambda_[j][0] * Math.abs(normalizedApproximation[i][0]);
        for (int n = 1; n < nObj_; n++) {
          matrix_[i][j] = Math.max(matrix_[i][j], lambda_[j][n] * Math.abs(normalizedApproximation[i][n]));
        }
      }
    }
    double sum = 0.0;
    for (int i = 0; i < lambda_.length; i++) {
      double tmp = matrix_[0][i];
      for (int j = 1; j < approximation.length; j++) {
        tmp = Math.min(tmp, matrix_[j][i]);
      }
      sum += tmp;
    }
    return sum / (double) lambda_.length;
  }


<<<<<<< /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/qualityIndicator/R2.java/left.java
  /**
   * Returns the R2 indicator of a given population, using as a reference
   * point 0, 0. Normalization is using taking into account the population itself
   * @param set
   * @return
   */
  public double R2(SolutionSet set) {
    double[][] approximationFront = set.writeObjectivesToMatrix();
    double[][] trueFront = set.writeObjectivesToMatrix();
    return this.r2(approximationFront, trueFront);
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
   * Returns the R2 indicator of a given population, using as a reference point
   * 0, 0. Normalization is using taking into account the population itself
   */
  public double r2(SolutionSet set) {
    double[][] approximationFront = set.writeObjectivesToMatrix();
    double[][] trueFront = set.writeObjectivesToMatrix();
    return this.r2(approximationFront, trueFront);
  }

  /**
   * Returns the R2 indicator value of a given front
   */
  public double R2Without(SolutionSet set, int index) {
    double[][] approximationFront = set.writeObjectivesToMatrix();
    double[][] trueFront = set.writeObjectivesToMatrix();
    return this.r2(approximationFront, trueFront);
  }
}