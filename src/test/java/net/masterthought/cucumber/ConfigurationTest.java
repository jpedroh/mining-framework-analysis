package net.masterthought.cucumber;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.util.Collection;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class ConfigurationTest {
  @Rule public ExpectedException thrown = ExpectedException.none();

  private static final File outputDirectory = new File("abc");

  private final String projectName = "123";

  @Test public void setStatusFlags_SetsFlags() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    boolean failsIfMissingIn = true;
    boolean failsIFPendingIn = false;
    boolean failsIfSkippedIn = true;
    boolean failsIfUndefinedIn = false;
    configuration.setStatusFlags(failsIfSkippedIn, failsIFPendingIn, failsIfUndefinedIn, failsIfMissingIn);
    assertThat(configuration.failsIfSkipped()).isEqualTo(failsIfSkippedIn);
    assertThat(configuration.failsIFPending()).isEqualTo(failsIFPendingIn);
    assertThat(configuration.failsIfUndefined()).isEqualTo(failsIfUndefinedIn);
    assertThat(configuration.failsIfMissing()).isEqualTo(failsIfMissingIn);
  }

  @Test public void isParallelTesting_ReturnsParallelTesting() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    boolean parallelTesting = true;
    configuration.setParallelTesting(parallelTesting);
    boolean parallel = configuration.isParallelTesting();
    assertThat(parallel).isEqualTo(parallelTesting);
  }

  @Test public void getJenkinsBasePath_OnSampleBath_ReturnsJenkinsPath() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    String basePath = "abc321";
    configuration.setJenkinsBasePath(basePath);
    String path = configuration.getJenkinsBasePath();
    assertThat(path).isEqualTo(basePath);
  }

  @Test public void getJenkinsBasePath_OnEmptyPath_ReturnsJenkinsPath() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    String basePath = StringUtils.EMPTY;
    configuration.setJenkinsBasePath(basePath);
    String path = configuration.getJenkinsBasePath();
    assertThat(path).isEqualTo("/");
  }

  @Test public void isRunWithJenkins_ReturnsRunWithJenkins() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    boolean runWithJenkins = true;
    configuration.setRunWithJenkins(runWithJenkins);
    boolean run = configuration.isRunWithJenkins();
    assertThat(run).isEqualTo(runWithJenkins);
  }

  @Test public void getReportDirectory_ReturnsOutputDirectory() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    File dir = configuration.getReportDirectory();
    assertThat(dir).isEqualTo(outputDirectory);
  }

  @Test public void getBuildNumber_ReturnsBuildNumber() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    String buildNumber = "123xyz";
    configuration.setBuildNumber(buildNumber);
    String build = configuration.getBuildNumber();
    assertThat(build).isEqualTo(buildNumber);
  }

  @Test public void getProjectName_ReturnsProjectName() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    String name = configuration.getProjectName();
    assertThat(name).isEqualTo(projectName);
  }

  @Test public void getTagsToExcludeFromChart_ReturnsEmptyList() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    Collection<Pattern> patterns = configuration.getTagsToExcludeFromChart();
    assertThat(patterns).isEmpty();
  }

  @Test public void getTagsToExcludeFromChart_addPatterns_ReturnsListWithAllPatterns() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    String somePattern = "@specificTagNameToExclude";
    String anotherPattern = "@some.Regex.Pattern";
    configuration.setTagsToExcludeFromChart(somePattern, anotherPattern);
    Collection<Pattern> patterns = configuration.getTagsToExcludeFromChart();
    assertThat(patterns).extractingResultOf("pattern").containsOnly(somePattern, anotherPattern);
  }

  @Test public void setTagsToExcludeFromChart_OnInvalidRegexPattern_ThrowsValidationException() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    thrown.expect(ValidationException.class);
    configuration.setTagsToExcludeFromChart("\\invalid.regex\\");
  }
}