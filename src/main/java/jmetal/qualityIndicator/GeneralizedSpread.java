package jmetal.qualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.util.Arrays;

/**
 * This class implements the generalized spread metric for two or more dimensions.
 * It can be used also as command line program just by typing. 
 * $ java jmetal.qualityIndicator.GeneralizedSpread <solutionFrontFile> <trueFrontFile> <getNumberOfObjectives>
 * Reference: A. Zhou, Y. Jin, Q. Zhang, B. Sendhoff, and E. Tsang
 *           Combining model-based and genetics-based offspring generation for 
 *           multi-objective optimization using a convergence criterion, 
 *           2006 IEEE Congress on Evolutionary Computation, 2006, pp. 3234-3241.
 */
public class GeneralizedSpread {
  public static jmetal.qualityIndicator.util.MetricsUtil utils_;

  /**
   * Constructor
   * Creates a new instance of GeneralizedSpread
   */
  public GeneralizedSpread() {
    utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
  }

  /**
   *  Calculates the generalized spread metric. Given the 
   *  pareto front, the true pareto front as <code>double []</code>
   *  and the number of objectives, the method return the value for the
   *  metric.
   *  @param paretoFront The pareto front.
   *  @param paretoTrueFront The true pareto front.
   *  @param numberOfObjectives The number of objectives.
   *  @return the value of the generalized spread metric
   **/
  public double generalizedSpread(double[][] paretoFront, double[][] paretoTrueFront, int numberOfObjectives) {
    double[] maximumValue;
    double[] minimumValue;
    double[][] normalizedFront;
    double[][] normalizedParetoFront;
    maximumValue = utils_.getMaximumValues(paretoTrueFront, numberOfObjectives);
    minimumValue = utils_.getMinimumValues(paretoTrueFront, numberOfObjectives);
    normalizedFront = utils_.getNormalizedFront(paretoFront, maximumValue, minimumValue);
    normalizedParetoFront = utils_.getNormalizedFront(paretoTrueFront, maximumValue, minimumValue);
    double[][] extremValues = new double[numberOfObjectives][numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      Arrays.sort(normalizedParetoFront, new jmetal.qualityIndicator.util.ValueComparator(i));
      System.arraycopy(normalizedParetoFront[normalizedParetoFront.length - 1], 0, extremValues[i], 0, numberOfObjectives);
    }
    int numberOfPoints = normalizedFront.length;
    int numberOfTruePoints = normalizedParetoFront.length;
    Arrays.sort(normalizedFront, new jmetal.qualityIndicator.util.LexicoGraphicalComparator());
    if (utils_.distance(normalizedFront[0], normalizedFront[normalizedFront.length - 1]) == 0.0) {
      return 1.0;
    } else {
      double dmean = 0.0;
      for (double[] aNormalizedFront : normalizedFront) {
        dmean += utils_.distanceToNearestPoint(aNormalizedFront, normalizedFront);
      }
      dmean = dmean / (numberOfPoints);
      double dExtrems = 0.0;
      for (double[] extremValue : extremValues) {
        dExtrems += utils_.distanceToClosedPoint(extremValue, normalizedFront);
      }
      double mean = 0.0;
      for (double[] aNormalizedFront : normalizedFront) {
        mean += Math.abs(utils_.distanceToNearestPoint(aNormalizedFront, normalizedFront) - dmean);
      }
      return (dExtrems + mean) / (dExtrems + (numberOfPoints * dmean));
    }
  }

  /**
   * This class can be invoked from the command line. Three params are required:
   * 1) the name of the file containing the front,
   * 2) the name of the file containig the true Pareto front
   * 3) the number of objectives
   *
   * @throws JMException
   */
  public static void main(String[] args) throws JMException {
    if (args.length < 3) {
      throw new JMException("Error using GeneralizedSpread. " + "Usage: \n java GeneralizedSpread" + " <SolutionFrontFile> " + " <TrueFrontFile> + <getNumberOfObjectives>");
    }
    GeneralizedSpread qualityIndicator = new GeneralizedSpread();
    double[][] solutionFront = utils_.readFront(args[0]);
    double[][] trueFront = utils_.readFront(args[1]);
    double value = qualityIndicator.generalizedSpread(solutionFront, trueFront, new Integer(args[2]));
    Configuration.logger_.info("" + value);
  }
}