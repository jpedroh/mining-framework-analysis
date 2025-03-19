package jmetal.experiments;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.encodings.solutiontype.ArrayRealSolutionType;
import jmetal.encodings.solutiontype.BinaryRealSolutionType;
import jmetal.encodings.solutiontype.BinarySolutionType;
import jmetal.encodings.solutiontype.RealSolutionType;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.util.JMException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

/**
 * Class representing Settings objects. 
 */
public abstract class Settings {
  protected Problem problem_;

  protected String problemName_;

  protected String paretoFrontFile_;

  /**
   * Constructor
   */
  public Settings() {
  }

  /**
   * Constructor
   */
  public Settings(String problemName) throws JMException {
    problemName_ = problemName;
  }

  /**
   * Default configure method
   * @return An algorithm with the default configuration
   * @throws jmetal.util.JMException
   */
  abstract public Algorithm configure() throws JMException;

  /**
   * Configure method based on reading a properties file
   * @param configuration Properties file
   * @return A algorithm with a the configuration contained in the properties file
   */
  public Algorithm configure(Properties configuration) throws JMException {
    return null;
  }

  /**
   * Configure method. Change the default configuration
   * @param settings
   * @return A problem with the experiments.settings indicated as argument
   * @throws jmetal.util.JMException
   * @throws ClassNotFoundException 
   */
  public final Algorithm configure(HashMap settings) throws JMException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
    if (settings != null) {
      Field[] fields = this.getClass().getFields();
      for (int i = 0; i < fields.length; i++) {
        if (fields[i].getName().endsWith("_")) {
          if (fields[i].getType().equals(int.class) || fields[i].getType().equals(Integer.class)) {
            if (settings.containsKey(fields[i].getName())) {
              Integer value = (Integer) settings.get(fields[i].getName());
              fields[i].setInt(this, value.intValue());
            }
          } else {
            if (fields[i].getType().equals(double.class) || fields[i].getType().equals(Double.class)) {
              Double value = (Double) settings.get(fields[i].getName());
              if (settings.containsKey(fields[i].getName())) {
                if ("mutationProbability_".equals(fields[i].getName()) && value == null) {
                  if ((RealSolutionType.class == problem_.getSolutionType().getClass()) || (ArrayRealSolutionType.class == problem_.getSolutionType().getClass())) {
                    value = 1.0 / problem_.getNumberOfVariables();
                  } else {
                    if (BinarySolutionType.class == problem_.getSolutionType().getClass() || BinaryRealSolutionType.class == problem_.getSolutionType().getClass()) {
                      int length = problem_.getNumberOfBits();
                      value = 1.0 / length;
                    } else {
                      int length = 0;
                      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
                        length += problem_.getLength(j);
                      }
                      value = 1.0 / length;
                    }
                  }
                  fields[i].setDouble(this, value);
                } else {
                  fields[i].setDouble(this, value);
                }
              }
            } else {
              Object value = settings.get(fields[i].getName());
              if (value != null) {
                if (fields[i].getType().equals(Crossover.class)) {
                  Object value2 = CrossoverFactory.getCrossoverOperator((String) value, settings);
                  value = value2;
                }
                if (fields[i].getType().equals(Mutation.class)) {
                  Object value2 = MutationFactory.getMutationOperator((String) value, settings);
                  value = value2;
                }
                fields[i].set(this, value);
              }
            }
          }
        }
      }
      for (int i = 0; i < fields.length; i++) {
        if (fields[i].getType().equals(Crossover.class) || fields[i].getType().equals(Mutation.class)) {
          Operator operator = (Operator) fields[i].get(this);
          String tmp = fields[i].getName();
          String aux = fields[i].getName().substring(0, tmp.length() - 1);
          for (int j = 0; j < fields.length; j++) {
            if (i != j) {
              if (fields[j].getName().startsWith(aux)) {
                tmp = fields[j].getName().substring(aux.length(), fields[j].getName().length() - 1);
                if (fields[j].get(this) != null) {
                  if (fields[j].getType().equals(int.class) || fields[j].getType().equals(Integer.class)) {
                    operator.setParameter(tmp, fields[j].getInt(this));
                  } else {
                    if (fields[j].getType().equals(double.class) || fields[j].getType().equals(Double.class)) {
                      operator.setParameter(tmp, fields[j].getDouble(this));
                    }
                  }
                }
              }
            }
          }
        }
      }
      paretoFrontFile_ = (String) settings.get("paretoFrontFileList_");
    }
    return configure();
  }

  /**
   * Changes the problem to solve
   * @param problem
   */
  void setProblem(Problem problem) {
    problem_ = problem;
  }

  /**
   * Returns the problem
   */
  Problem getProblem() {
    return problem_;
  }
}