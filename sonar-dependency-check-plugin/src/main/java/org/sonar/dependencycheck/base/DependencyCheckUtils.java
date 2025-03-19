package org.sonar.dependencycheck.base;
import org.codehaus.staxmate.SMInputFactory;
import org.sonar.api.batch.rule.Severity;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;

public final class DependencyCheckUtils {
  private DependencyCheckUtils() {
  }

  public static SMInputFactory newStaxParser() throws FactoryConfigurationError {
    XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
    xmlFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    xmlFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
    xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
    xmlFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
    return new SMInputFactory(xmlFactory);
  }

  public static Severity cvssToSonarQubeSeverity(Float cvssScore, Float critical, Float major, Double minor) {
    if (
<<<<<<< /usr/src/app/output/stevespringett/dependency-check-sonar-plugin/f0032e903f332c22e4beb65b317edcafb7fdf2cf/sonar-dependency-check-plugin/src/main/java/org/sonar/dependencycheck/base/DependencyCheckUtils.java/left.java
    critical.floatValue() > 0
=======
    critical.doubleValue() >= 0
>>>>>>> /usr/src/app/output/stevespringett/dependency-check-sonar-plugin/f0032e903f332c22e4beb65b317edcafb7fdf2cf/sonar-dependency-check-plugin/src/main/java/org/sonar/dependencycheck/base/DependencyCheckUtils.java/right.java
     && cvssScore.floatValue() >= critical.floatValue()) {
      return Severity.CRITICAL;
    } else {
      if (
<<<<<<< /usr/src/app/output/stevespringett/dependency-check-sonar-plugin/f0032e903f332c22e4beb65b317edcafb7fdf2cf/sonar-dependency-check-plugin/src/main/java/org/sonar/dependencycheck/base/DependencyCheckUtils.java/left.java
      major.floatValue() > 0
=======
      major.doubleValue() >= 0
>>>>>>> /usr/src/app/output/stevespringett/dependency-check-sonar-plugin/f0032e903f332c22e4beb65b317edcafb7fdf2cf/sonar-dependency-check-plugin/src/main/java/org/sonar/dependencycheck/base/DependencyCheckUtils.java/right.java
       && cvssScore.floatValue() >= major.floatValue()) {
        return Severity.MAJOR;
      } else {
        if (minor.doubleValue() >= 0 && score >= minor.doubleValue()) {
          return Severity.MINOR;
        } else {
          return Severity.INFO;
        }
      }
    }
  }
}