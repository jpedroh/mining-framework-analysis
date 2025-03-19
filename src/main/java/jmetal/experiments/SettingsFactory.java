package jmetal.experiments;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.lang.reflect.Constructor;
import java.util.logging.Level;

/**
 * This class represents a factory for Setting object
 */
public class SettingsFactory {
  /**
   * Creates a experiments.settings object
   * @param algorithmName Name of the algorithm
   * @param params Parameters
   * @return The experiments.settings object
   * @throws JMException
   */
  public Settings getSettingsObject(String algorithmName, Object[] params) throws JMException {
    String base = "jmetal.experiments.settings." + algorithmName + "_Settings";
    try {
      Class problemClass = Class.forName(base);
      Constructor[] constructors = problemClass.getConstructors();
      int i = 0;
      while ((i < constructors.length) && (constructors[i].getParameterTypes().length != params.length)) {
        i++;
      }
      return (Settings) constructors[i].newInstance(params);
    } catch (Exception e) {
      Configuration.logger_.log(Level.SEVERE, "SettingsFactory.getSettingsObject: " + "Settings \'" + base + "\' does not exist. " + "Please, check the algorithm name in jmetal/metaheuristics", e);
      throw new JMException("Exception in " + base + ".getSettingsObject()");
    }
  }
}