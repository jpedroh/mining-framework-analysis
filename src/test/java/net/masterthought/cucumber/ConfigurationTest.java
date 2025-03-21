package net.masterthought.cucumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import org.junit.Test;
import net.masterthought.cucumber.json.support.Status;
import net.masterthought.cucumber.presentation.PresentationMode;
import net.masterthought.cucumber.reducers.ReducingMethod;
import net.masterthought.cucumber.sorting.SortingMethod;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class ConfigurationTest {
  private static final File outputDirectory = new File("abc");

  private final String projectName = "123";

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

  @Test public void isTrendsAvailable_OnNoTrendsPage_ReturnsFalse() {
    final int limit = -1;
    File file = new File("ble");
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.setTrends(file, limit);
    assertThat(configuration.isTrendsAvailable()).isFalse();
  }

  @Test public void isTrendsAvailable_OnNoTrendsFile_ReturnsFalse() {
    final int limit = 10;
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.setTrends(null, limit);
    assertThat(configuration.isTrendsAvailable()).isFalse();
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

  @Test public void getDirectorySuffix_ReturnsDirectorySuffix() {
    String directorySuffix = "test";
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.setDirectorySuffix(directorySuffix);
    assertThat(configuration.getDirectorySuffix()).isEqualTo(directorySuffix);
  }

  @Test public void getDirectorySuffixWithSeparator_ReturnsDirectorySuffixWithSeparator() {
    String directorySuffix = "test";
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.setDirectorySuffix(directorySuffix);
    assertThat(configuration.getDirectorySuffixWithSeparator()).isEqualTo(ReportBuilder.SUFFIX_SEPARATOR + directorySuffix);
  }

  @Test public void getDirectorySuffixWithSeparatorForEmptySuffix_ReturnsEmptyString() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    assertThat(configuration.getDirectorySuffixWithSeparator()).isEqualTo("");
  }

  @Test public void getQualifier_ReturnsQualifierWhenSet() {
    String jsonFile = "test";
    String qualifier = "test";
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.setQualifier(jsonFile, qualifier);
    assertThat(configuration.getQualifier(jsonFile)).isEqualTo(qualifier);
  }

  @Test public void getQualifier_ReturnsNullWhenNotSet() {
    String jsonFile = "test";
    Configuration configuration = new Configuration(outputDirectory, projectName);
    assertThat(configuration.getQualifier(jsonFile)).isNull();
  }

  @Test public void getQualifier_ReturnsNullWhenSetThenRemoved() {
    String jsonFile = "test";
    String qualifier = "test";
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.setQualifier(jsonFile, qualifier);
    configuration.removeQualifier(jsonFile);
    assertThat(configuration.getQualifier(jsonFile)).isNull();
  }

  @Test public void isQualifierSet_ReturnsTrueWhenSet() {
    String jsonFile = "test";
    String qualifier = "test";
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.setQualifier(jsonFile, qualifier);
    assertThat(configuration.isQualifierSet(jsonFile)).isTrue();
  }

  @Test public void isQualifierSet_ReturnsTrueWhenNotSet() {
    String jsonFile = "test";
    Configuration configuration = new Configuration(outputDirectory, projectName);
    assertThat(configuration.isQualifierSet(jsonFile)).isFalse();
  }

  @Test public void isQualifierSet_ReturnsTrueWhenSetThenRemoved() {
    String jsonFile = "test";
    String qualifier = "test";
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.setQualifier(jsonFile, qualifier);
    configuration.removeQualifier(jsonFile);
    assertThat(configuration.isQualifierSet(jsonFile)).isFalse();
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
    assertThatThrownBy(() -> configuration.setTagsToExcludeFromChart("\\invalid.regex\\")).isInstanceOf(ValidationException.class);
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

  @Test public void setSortingMethod_SetsSortingMethod() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    SortingMethod sortingMethod = SortingMethod.NATURAL;
    configuration.setSortingMethod(sortingMethod);
    assertThat(configuration.getSortingMethod()).isEqualTo(sortingMethod);
  }

  @Test public void addReducingMethod_AddsReducingMethod() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    ReducingMethod reducingMethod = ReducingMethod.MERGE_FEATURES_BY_ID;
    configuration.addReducingMethod(reducingMethod);
    assertThat(configuration.getReducingMethods()).containsOnly(reducingMethod);
  }

  @Test public void containsReducingMethod_ChecksExistenceOfReducingMethod() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    configuration.addReducingMethod(ReducingMethod.MERGE_FEATURES_BY_ID);
    assertThat(configuration.containsReducingMethod(ReducingMethod.MERGE_FEATURES_BY_ID)).isTrue();
  }

  @Test public void addPresentationMode_AddsPresentationMode() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    PresentationMode presentationMode = PresentationMode.EXPAND_ALL_STEPS;
    configuration.addPresentationModes(presentationMode);
    assertThat(configuration.containsPresentationMode(presentationMode)).isTrue();
  }

  @Test public void addClassificationFiles_addsPropertyFiles() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    List<String> propertiesFiles = new ArrayList<>();
    propertiesFiles.add("properties-1.properties");
    propertiesFiles.add("properties-2.properties");
    configuration.addClassificationFiles(propertiesFiles);
    List<String> returnedPropertiesFiles = configuration.getClassificationFiles();
    assertThat(returnedPropertiesFiles).hasSize(2);
    assertThat(returnedPropertiesFiles).containsExactly(("properties-1.properties"), ("properties-2.properties"));
  }

  @Test public void getNotFailingStatuses_ReturnsNotFailingStatuses() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    Status notFailingStatus = Status.SKIPPED;
    configuration.setNotFailingStatuses(Collections.singleton(notFailingStatus));
    Set<Status> statuses = configuration.getNotFailingStatuses();
    assertThat(statuses).containsExactly(notFailingStatus);
  }

  @Test public void setNotFailingStatuses_SkipsNullValues() {
    Configuration configuration = new Configuration(outputDirectory, projectName);
    Status notFailingStatus = Status.SKIPPED;
    configuration.setNotFailingStatuses(Collections.singleton(notFailingStatus));
    configuration.setNotFailingStatuses(null);
    assertThat(configuration.getNotFailingStatuses()).containsExactly(notFailingStatus);
  }
}