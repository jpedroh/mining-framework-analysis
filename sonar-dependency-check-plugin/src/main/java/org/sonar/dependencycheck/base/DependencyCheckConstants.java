package org.sonar.dependencycheck.base;

public final class DependencyCheckConstants {
  public static final String REPORT_PATH_PROPERTY = "sonar.dependencyCheck.reportPath";

  public static final String HTML_REPORT_PATH_PROPERTY = "sonar.dependencyCheck.htmlReportPath";

  public static final String SEVERITY_CRITICAL = "sonar.dependencyCheck.severity.critical";

  public static final String SEVERITY_MAJOR = "sonar.dependencyCheck.severity.major";

  public static final Float SEVERITY_CRITICAL_DEFAULT = 7.0f;

  public static final Float SEVERITY_MAJOR_DEFAULT = 4.0f;

  public static final String 
<<<<<<< /usr/src/app/output/stevespringett/dependency-check-sonar-plugin/f0032e903f332c22e4beb65b317edcafb7fdf2cf/sonar-dependency-check-plugin/src/main/java/org/sonar/dependencycheck/base/DependencyCheckConstants.java/left.java
  REPORT_PATH_DEFAULT = "${WORKSPACE}/dependency-check-report.xml"
=======
  SEVERITY_MINOR = "sonar.dependencyCheck.severity.minor"
>>>>>>> /usr/src/app/output/stevespringett/dependency-check-sonar-plugin/f0032e903f332c22e4beb65b317edcafb7fdf2cf/sonar-dependency-check-plugin/src/main/java/org/sonar/dependencycheck/base/DependencyCheckConstants.java/right.java
  ;

  public static final String HTML_REPORT_PATH_DEFAULT = "${WORKSPACE}/dependency-check-report.html";

  private DependencyCheckConstants() {
  }
}