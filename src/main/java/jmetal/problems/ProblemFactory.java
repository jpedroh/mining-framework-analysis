package jmetal.problems;
import jmetal.core.Problem;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.lang.reflect.Constructor;
import java.util.logging.Level;

/**
 * This class represents a factory for problems
 */
public class ProblemFactory {
  /**
   * Creates an object representing a problem
   * @param name Name of the problem
   * @param params Parameters characterizing the problem
   * @return The object representing the problem
   * @throws JMException
   */
  public Problem getProblem(String name, Object[] params) throws JMException {
    String base = "jmetal.problems.";
    if ("TSP".equals(name) || "OneMax".equals(name)) {
      base += "singleObjective.";
    } else {
      if ("mQAP".equals(name)) {
        base += "mqap.";
      } else {
        if ("DTLZ".equalsIgnoreCase(name.substring(0, name.length() - 1))) {
          base += "DTLZ.";
        } else {
          if ("WFG".equalsIgnoreCase(name.substring(0, name.length() - 1))) {
            base += "WFG.";
          } else {
            if ("UF".equalsIgnoreCase(name.substring(0, name.length() - 1))) {
              base += "cec2009Competition.";
            } else {
              if ("UF".equalsIgnoreCase(name.substring(0, name.length() - 2))) {
                base += "cec2009Competition.";
              } else {
                if ("ZDT".equalsIgnoreCase(name.substring(0, name.length() - 1))) {
                  base += "ZDT.";
                } else {
                  if ("ZZJ07".equalsIgnoreCase(name.substring(0, name.length() - 3))) {
                    base += "ZZJ07.";
                  } else {
                    if ("LZ09".equalsIgnoreCase(name.substring(0, name.length() - 3))) {
                      base += "LZ09.";
                    } else {
                      if ("ZZJ07".equalsIgnoreCase(name.substring(0, name.length() - 4))) {
                        base += "ZZJ07.";
                      } else {
                        if ("LZ06".equalsIgnoreCase(name.substring(0, name.length() - 3))) {
                          base += "LZ06.";
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    try {
      Class<?> problemClass = Class.forName(base + name);
      Constructor<?>[] constructors = problemClass.getConstructors();
      int i = 0;
      while ((i < constructors.length) && (constructors[i].getParameterTypes().length != params.length)) {
        i++;
      }
      return (Problem) constructors[i].newInstance(params);
    } catch (Exception e) {
      Configuration.logger_.log(Level.SEVERE, "ProblemFactory.getProblem: " + "Problem \'" + name + "\' does not exist. " + "Please, check the problem names in jmetal/problems", e);
      throw new JMException("Exception in " + name + ".getProblem()");
    }
  }
}