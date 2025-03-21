package net.masterthought.cucumber;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
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

  @Test public void isParallelTesting_ReturnsParallelTesting() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    boolean parallelTesting = true;
    configuration.setParallelTesting(parallelTesting);
    boolean parallel = configuration.isParallelTesting();
    assertThat(parallel).isEqualTo(parallelTesting);
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

  @Test public void getTrendsStatsFile_ReturnsTrendsFile() {
    File file = new File("ble");
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.setTrendsStatsFile(file);
    assertThat(configuration.getTrendsStatsFile()).isEqualTo(file);
  }

  @Test public void isTrendsStatsFile_ChecksIfTrendsFileWasSet() {
    File file = new File("ble");
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.setTrendsStatsFile(file);
    assertThat(configuration.isTrendsStatsFile()).isTrue();
  }

  @Test public void getTrendsLimit_ReturnsLimitForTrends() {
    final int limit = 123;
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.setTrends(null, limit);
    assertThat(configuration.getTrendsLimit()).isEqualTo(limit);
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

  @Test public void addClassifications_AddsClassification() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    final String classificationName = "Browser";
    final String classificationValue = "Firefox 1.0";
    configuration.addClassifications(classificationName, classificationValue);
    assertThat(configuration.getClassifications()).hasSize(1);
    Map.Entry<String, String> classification = configuration.getClassifications().get(0);
    assertThat(classification.getKey()).isEqualTo(classificationName);
    assertThat(classification.getValue()).isEqualTo(classificationValue);
  }

  @Test public void addPropertiesFiles_getPropertyFiles() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    List<String> propertiesFiles = new ArrayList<>();
    propertiesFiles.add("properties-1.properties");
    propertiesFiles.add("properties-2.properties");
    configuration.addClassificationFiles(propertiesFiles);
    List<String> returnedPropertiesFiles = configuration.getClassificationFiles();
    assertThat(returnedPropertiesFiles).hasSize(2);
    assertThat(returnedPropertiesFiles).containsExactly(("properties-1.properties"), ("properties-2.properties"));
  }
}