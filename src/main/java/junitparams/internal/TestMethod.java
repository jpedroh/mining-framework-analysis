package junitparams.internal;
import junitparams.FileParameters;
import junitparams.Parameters;
import junitparams.internal.parameters.ParametersReader;
import org.junit.Ignore;
import org.junit.runner.Description;
import junitparams.testnaming.MacroSubstitutionNamingStrategy;
import org.junit.runners.model.FrameworkMethod;
import junitparams.testnaming.TestCaseNamingStrategy;
import org.junit.runners.model.TestClass;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for a test method
 * 
 * @author Pawel Lipinski
 */
public class TestMethod {
  private FrameworkMethod frameworkMethod;

  private Class<?> testClass;

  private ParametersReader parametersReader;

  private TestCaseNamingStrategy namingStrategy;

  private Object[] cachedParameters;

  public TestMethod(FrameworkMethod method, TestClass testClass) {
    this.frameworkMethod = method;
    this.testClass = testClass.getJavaClass();

<<<<<<< /usr/src/app/output/pragmatists/junitparams/762ec95b8e26869a888d9125b48be59927d1de81/src/main/java/junitparams/internal/TestMethod.java/left.java
    namingStrategy
=======
    parametersReader
>>>>>>> /usr/src/app/output/pragmatists/junitparams/762ec95b8e26869a888d9125b48be59927d1de81/src/main/java/junitparams/internal/TestMethod.java/right.java
     = new 
<<<<<<< /usr/src/app/output/pragmatists/junitparams/762ec95b8e26869a888d9125b48be59927d1de81/src/main/java/junitparams/internal/TestMethod.java/left.java
    MacroSubstitutionNamingStrategy
=======
    ParametersReader
>>>>>>> /usr/src/app/output/pragmatists/junitparams/762ec95b8e26869a888d9125b48be59927d1de81/src/main/java/junitparams/internal/TestMethod.java/right.java
    (
<<<<<<< /usr/src/app/output/pragmatists/junitparams/762ec95b8e26869a888d9125b48be59927d1de81/src/main/java/junitparams/internal/TestMethod.java/left.java
    this
=======
    testClass()
>>>>>>> /usr/src/app/output/pragmatists/junitparams/762ec95b8e26869a888d9125b48be59927d1de81/src/main/java/junitparams/internal/TestMethod.java/right.java
    , frameworkMethod);
  }

  public String name() {
    return frameworkMethod.getName();
  }

  public static List<TestMethod> listFrom(List<FrameworkMethod> annotatedMethods, TestClass testClass) {
    List<TestMethod> methods = new ArrayList<TestMethod>();
    for (FrameworkMethod frameworkMethod : annotatedMethods) {
      methods.add(new TestMethod(frameworkMethod, testClass));
    }
    return methods;
  }

  @Override public int hashCode() {
    return frameworkMethod.hashCode();
  }

  @Override public boolean equals(Object obj) {
    if (!(obj instanceof TestMethod)) {
      return false;
    }
    if (!frameworkMethod.getName().equals(((TestMethod) obj).frameworkMethod.getName())) {
      return false;
    }
    if (!frameworkMethod.getMethod().getParameterTypes().equals(((TestMethod) obj).frameworkMethod.getMethod().getParameterTypes())) {
      return false;
    }
    return true;
  }

  Class<?> testClass() {
    return testClass;
  }

  public boolean isIgnored() {
    if (frameworkMethod.getAnnotation(Ignore.class) != null) {
      return true;
    }
    if (isParameterised() && parametersSets().length == 0) {
      return true;
    }
    return false;
  }

  public boolean isNotIgnored() {
    return !isIgnored();
  }

  public Annotation[] annotations() {
    return frameworkMethod.getAnnotations();
  }

  public <T extends java.lang.annotation.Annotation> T getAnnotation(Class<? extends Annotation> annotationType) {
    return (T) frameworkMethod.getAnnotation(annotationType);
  }

  Description describe() {
    if (isNotIgnored() && !describeFlat()) {
      Description parametrised = Description.createSuiteDescription(name());
      Object[] params = parametersSets();
      for (int i = 0; i < params.length; i++) {
        Object paramSet = params[i];
        String name = namingStrategy.getTestCaseName(i, paramSet);
        String uniqueMethodId = Utils.stringify(paramSet, i) + " (" + name() + ")";
        parametrised.addChild(Description.createTestDescription(testClass().getName(), name, uniqueMethodId));
      }
      return parametrised;
    } else {
      return Description.createTestDescription(testClass(), name(), annotations());
    }
  }

  private boolean describeFlat() {
    return System.getProperty("JUnitParams.flat") != null;
  }

  public Object[] parametersSets() {
    if (cachedParameters == null) {
      cachedParameters = parametersReader.read();
    }
    return cachedParameters;
  }

  public boolean isParameterised() {
    return frameworkMethod.getMethod().isAnnotationPresent(Parameters.class) || frameworkMethod.getMethod().isAnnotationPresent(FileParameters.class);
  }

  void warnIfNoParamsGiven() {
    if (isNotIgnored() && isParameterised() && parametersSets().length == 0) {
      System.err.println("Method " + name() + " gets empty list of parameters, so it\'s being ignored!");
    }
  }

  public FrameworkMethod frameworkMethod() {
    return frameworkMethod;
  }
}