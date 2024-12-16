package cc.redpen.validator;

import cc.redpen.DocumentValidatorException;
import cc.redpen.config.SymbolTable;
import cc.redpen.config.ValidatorConfiguration;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * Factory class of validators.
 */
public class ValidatorFactory {
  private static final List<String> VALIDATOR_PACKAGES = new ArrayList<>();

  static {
    addValidatorPackage("cc.redpen.validator");
    addValidatorPackage("cc.redpen.validator.sentence");
    addValidatorPackage("cc.redpen.validator.section");
  }

  // can be made public if package needs to be added outside RedPen.
  private static void addValidatorPackage(String packageToAdd) {
    VALIDATOR_PACKAGES.add(packageToAdd);
  }

  public static Validator<?> getInstance(ValidatorConfiguration config, SymbolTable symbolTable) throws DocumentValidatorException {
    try {
      for (String validatorPackage : VALIDATOR_PACKAGES) {
        String validatorClassName = ((validatorPackage + ".") + config.getConfigurationName()) + "Validator";
        try {
          Class<?> clazz = Class.forName(validatorClassName);
          // ensure the class implements Validator
          boolean implementsValidator = false;
          for (Class<?> aClass : clazz.getInterfaces()) {
            if (aClass.equals(Validator.class)) {
              implementsValidator = true;
              break;
            }
          }
          if (!implementsValidator) {
            throw new RuntimeException(validatorClassName + " doesn't implement cc.redpen.validator.Validator");
          }
          Constructor<?> constructor = clazz.getConstructor(ValidatorConfiguration.class, cc.redpen.config.CharacterTable.class);
          return ((Validator<?>) (constructor.newInstance(config, characterTable)));
        } catch (java.lang.ClassNotFoundException ignore) {
        }
      }
    } catch (java.lang.NoSuchMethodException | InvocationTargetException | java.lang.IllegalAccessException | java.lang.InstantiationException e) {
      throw new RuntimeException(e);
    }
    throw new DocumentValidatorException("There is no such Validator: " + config.getConfigurationName());
  }
}