package org.sonar.plugins.buildbreaker;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.api.rule.Severity;
import java.util.Arrays;
import java.util.List;

/**
 * Registers the plugin with SonarQube and defines the available configuration properties.
 */
@Properties(value = { @Property(key = BuildBreakerPlugin.SKIP_KEY, defaultValue = "false", name = "Skip quality gate check", description = "If set to true, the quality gate is not checked.  By default the build will break if the project does not pass the quality gate.", global = true, project = true, type = PropertyType.BOOLEAN), @Property(key = BuildBreakerPlugin.QUERY_MAX_ATTEMPTS_KEY, defaultValue = "30", name = "API query max attempts", description = "The maximum number of queries to the API when waiting for report processing.  The build will break if this is reached." + "<br/>" + BuildBreakerPlugin.TOTAL_WAIT_TIME_DESCRIPTION, global = true, project = true, type = PropertyType.INTEGER), @Property(key = BuildBreakerPlugin.QUERY_INTERVAL_KEY, defaultValue = "10000", name = "API query interval (ms)", description = "The interval between queries to the API when waiting for report processing." + "<br/>" + BuildBreakerPlugin.TOTAL_WAIT_TIME_DESCRIPTION, global = true, project = true, type = PropertyType.INTEGER), @Property(key = BuildBreakerPlugin.FORBIDDEN_CONF_KEY, name = "Forbidden configuration parameters", description = "Comma-separated list of <code>key=value</code> pairs that should break the build.", global = true, project = false), @Property(key = BuildBreakerPlugin.
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-build-breaker/0a474ef6d91248a199a65c9ca24556e598116fd1/src/main/java/org/sonar/plugins/buildbreaker/BuildBreakerPlugin.java/left.java
FAIL_FOR_ISSUES_WITH_SEVERITY_KEY
=======
ALTERNATIVE_SERVER_URL_KEY
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-build-breaker/0a474ef6d91248a199a65c9ca24556e598116fd1/src/main/java/org/sonar/plugins/buildbreaker/BuildBreakerPlugin.java/right.java
, name = 
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-build-breaker/0a474ef6d91248a199a65c9ca24556e598116fd1/src/main/java/org/sonar/plugins/buildbreaker/BuildBreakerPlugin.java/left.java
"Severity to fail preview analysis"
=======
"Alternative server URL"
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-build-breaker/0a474ef6d91248a199a65c9ca24556e598116fd1/src/main/java/org/sonar/plugins/buildbreaker/BuildBreakerPlugin.java/right.java
, description = 
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-build-breaker/0a474ef6d91248a199a65c9ca24556e598116fd1/src/main/java/org/sonar/plugins/buildbreaker/BuildBreakerPlugin.java/left.java
"Fails the build for preview analysis modes if the severity of issues is equal or more severe"
=======
"URL to use for web service requests. If unset, uses the <code>serverUrl</code> property from " + "<code>${sonar.working.directory}/report-task.txt</code>."
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-build-breaker/0a474ef6d91248a199a65c9ca24556e598116fd1/src/main/java/org/sonar/plugins/buildbreaker/BuildBreakerPlugin.java/right.java
, type = PropertyType.SINGLE_SELECT_LIST, options = { BuildBreakerPlugin.FAIL_FOR_ISSUES_DISABLED, Severity.INFO, Severity.MINOR, Severity.MAJOR, Severity.CRITICAL, Severity.BLOCKER }, defaultValue = Severity.MAJOR, global = true, project = 
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-build-breaker/0a474ef6d91248a199a65c9ca24556e598116fd1/src/main/java/org/sonar/plugins/buildbreaker/BuildBreakerPlugin.java/left.java
true
=======
false
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-build-breaker/0a474ef6d91248a199a65c9ca24556e598116fd1/src/main/java/org/sonar/plugins/buildbreaker/BuildBreakerPlugin.java/right.java
) }) public class BuildBreakerPlugin extends SonarPlugin {
  public static final String SKIP_KEY = "sonar.buildbreaker.skip";

  public static final String QUERY_MAX_ATTEMPTS_KEY = "sonar.buildbreaker.queryMaxAttempts";

  public static final String QUERY_INTERVAL_KEY = "sonar.buildbreaker.queryInterval";

  public static final String TOTAL_WAIT_TIME_DESCRIPTION = "Total wait time is <code>" + BuildBreakerPlugin.QUERY_MAX_ATTEMPTS_KEY + " * " + BuildBreakerPlugin.QUERY_INTERVAL_KEY + "</code>.";

  public static final String BUILD_BREAKER_LOG_STAMP = "[BUILD BREAKER] ";

  public static final String FORBIDDEN_CONF_KEY = "sonar.buildbreaker.forbiddenConf";

  public static final String 
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-build-breaker/0a474ef6d91248a199a65c9ca24556e598116fd1/src/main/java/org/sonar/plugins/buildbreaker/BuildBreakerPlugin.java/left.java
  FAIL_FOR_ISSUES_WITH_SEVERITY_KEY = "sonar.buildbreaker.preview.failForIssuesWithSeverity"
=======
  ALTERNATIVE_SERVER_URL_KEY = "sonar.buildbreaker.alternativeServerUrl"
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-build-breaker/0a474ef6d91248a199a65c9ca24556e598116fd1/src/main/java/org/sonar/plugins/buildbreaker/BuildBreakerPlugin.java/right.java
  ;

  public static final String FAIL_FOR_ISSUES_DISABLED = "DISABLED";

  @Override public List getExtensions() {
    return Arrays.asList(ForbiddenConfigurationBreaker.class, QualityGateBreaker.class, BasicIssuesBuildBreaker.class);
  }
}