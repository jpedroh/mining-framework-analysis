package org.sonar.dependencycheck.base;

public final class DependencyCheckConstants {
  public static final String JSON_REPORT_PATH_PROPERTY = "sonar.dependencyCheck.jsonReportPath";

  public static final String HTML_REPORT_PATH_PROPERTY = "sonar.dependencyCheck.htmlReportPath";

  public static final String SEVERITY_BLOCKER = "sonar.dependencyCheck.severity.blocker";

  public static final String SEVERITY_CRITICAL = "sonar.dependencyCheck.severity.critical";

  public static final String SEVERITY_MAJOR = "sonar.dependencyCheck.severity.major";

  public static final String SEVERITY_MINOR = "sonar.dependencyCheck.severity.minor";

  public static final String SUMMARIZE_PROPERTY = "sonar.dependencyCheck.summarize";

  public static final String SKIP_PROPERTY = "sonar.dependencyCheck.skip";

  public static final String SECURITY_HOTSPOT = "sonar.dependencyCheck.securityHotspot";

  public static final String USE_FILEPATH = "sonar.dependencyCheck.useFilePath";

  public static final Float SEVERITY_BLOCKER_DEFAULT = 9.0f;

  public static final Float SEVERITY_CRITICAL_DEFAULT = 7.0f;

  public static final Float SEVERITY_MAJOR_DEFAULT = 4.0f;

  public static final Float SEVERITY_MINOR_DEFAULT = 0.0f;

  public static final String JSON_REPORT_PATH_DEFAULT = "${WORKSPACE}/dependency-check-report.json";

  public static final String HTML_REPORT_PATH_DEFAULT = "${WORKSPACE}/dependency-check-report.html";

  public static final Boolean SUMMARIZE_PROPERTY_DEFAULT = Boolean.FALSE;

  public static final Boolean SKIP_PROPERTY_DEFAULT = Boolean.FALSE;

  public static final Boolean SECURITY_HOTSPOT_DEFAULT = Boolean.FALSE;

  public static final Boolean USE_FILEPATH_DEFAULT = Boolean.FALSE;

  public static final String REPOSITORY_KEY = "OWASP";

  public static final String LANGUAGE_KEY = "neutral";

  public static final String RULE_KEY = "UsingComponentWithKnownVulnerability";

  public static final String RULE_KEY_WITH_SECURITY_HOTSPOT = "UsingComponentWithKnownVulnerabilitySecurityHotspot";

  public static final String SUB_CATEGORY_SEVERITIES = "Severities";

  public static final String SUB_CATEGORY_PATHS = "Paths";

  public static final String SUB_CATEGORY_GENERAL = "General";

  private DependencyCheckConstants() {
  }
}