package jmetal.qualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;

/**
 * This class implements the unary epsilon additive indicator as proposed in
 * E. Zitzler, E. Thiele, L. Laummanns, M., Fonseca, C., and Grunert da Fonseca.
 * V (2003): Performance Assessment of Multiobjective Optimizers: An Analysis and
 * Review. The code is the a Java version of the original metric implementation
 * by Eckart Zitzler.
 * It can be used also as a command line program just by typing
 * $java jmetal.qualityIndicator.Epsilon <solutionFrontFile> <trueFrontFile> <getNumberOfObjectives>
 */
public class Epsilon {
  private int dim_;

  public jmetal.qualityIndicator.util.MetricsUtil utils_ = new jmetal.qualityIndicator.util.MetricsUtil();

  private int[] obj_;

  private int method_;

  /**
   * Returns the epsilon indicator.
   * @param b True Pareto front
   * @param a Solution front
   * @return the value of the epsilon indicator
   * @throws JMException
   */
  public double epsilon(double[][] b, double[][] a, int dim) throws JMException {
    int i, j, k;
    double eps, eps_j = 0.0, eps_k = 0.0, eps_temp;
    dim_ = dim;
    setParameters();
    if (method_ == 0) {
      eps = Double.MIN_VALUE;
    } else {
      eps = 0;
    }
    for (i = 0; i < a.length; i++) {
      for (j = 0; j < b.length; j++) {
        for (k = 0; k < dim_; k++) {
          switch (method_) {
            case 0:
            if (obj_[k] == 0) {
              eps_temp = b[j][k] - a[i][k];
            } else {
              eps_temp = a[i][k] - b[j][k];
            }
            break;
            default:
            if ((a[i][k] < 0 && b[j][k] > 0) || (a[i][k] > 0 && b[j][k] < 0) || (a[i][k] == 0 || b[j][k] == 0)) {
              throw new JMException("Error in data file");
            }
            if (obj_[k] == 0) {
              eps_temp = b[j][k] / a[i][k];
            } else {
              eps_temp = a[i][k] / b[j][k];
            }
            break;
          }
          if (k == 0) {
            eps_k = eps_temp;
          } else {
            if (eps_k < eps_temp) {
              eps_k = eps_temp;
            }
          }
        }
        if (j == 0) {
          eps_j = eps_k;
        } else {
          if (eps_j > eps_k) {
            eps_j = eps_k;
          }
        }
      }
      if (i == 0) {
        eps = eps_j;
      } else {
        if (eps < eps_j) {
          eps = eps_j;
        }
      }
    }
    return eps;
  }

  /**
   * Returns the additive-epsilon value of the paretoFront. This method call to
   * the calculate epsilon-indicator one
   *
   * @throws JMException
   * @throws NumberFormatException
   */
  public static void main(String[] args) throws NumberFormatException, JMException {
    double indicatorvalue;
    if (args.length < 2) {
      throw new JMException("Error using Epsilon. Type: \n java AdditiveEpsilon " + "<FrontFile>" + "<TrueFrontFile> + <getNumberOfObjectives>");
    }
    Epsilon qualityIndicator = new Epsilon();
    double[][] solutionFront = qualityIndicator.utils_.readFront(args[0]);
    double[][] trueFront = qualityIndicator.utils_.readFront(args[1]);
    indicatorvalue = qualityIndicator.epsilon(trueFront, solutionFront, new Integer(args[2]));
    Configuration.logger_.info("" + 
<<<<<<< /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/qualityIndicator/Epsilon.java/left.java
    indicatorvalue
=======
    ind_value
>>>>>>> /usr/src/app/output/jmetal/jmetal/bf795549bfebe6116221d4d2136b42de3fc0004e/src/main/java/jmetal/qualityIndicator/Epsilon.java/right.java
    );
  }

  /**
   * Established the default parameters
   */
  void setParameters() {
    int i;
    obj_ = new int[dim_];
    for (i = 0; i < dim_; i++) {
      obj_[i] = 0;
    }
    method_ = 0;
  }
}