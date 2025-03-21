package net.masterthought.cucumber.json;
import static org.assertj.core.api.Assertions.assertThat;
import mockit.Deencapsulation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import net.masterthought.cucumber.generators.integrations.PageTest;
import net.masterthought.cucumber.json.support.Status;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class FeatureTest extends PageTest {
  @Before public void setUp() {
    setUpWithJson(SAMPLE_JSON);
  }

  @Test public void getDeviceName_ReturnsDeviceName() {
    Feature feature = features.get(0);
    String deviceName = feature.getDeviceName();
    assertThat(deviceName).isEqualTo("sample");
  }

  @Test public void getId_ReturnsID() {
    Feature feature = features.get(1);
    String id = feature.getId();
    assertThat(id).isEqualTo("account-holder-withdraws-more-cash");
  }

  @Test public void getElements_ReturnsElements() {
    Feature feature = features.get(0);
    Element[] elements = feature.getElements();
    assertThat(elements).hasSize(2);
    assertThat(elements[0].getName()).isEqualTo("Activate Credit Card");
  }

  @Test public void getReportFileName_ReturnsFileName() {
    Feature feature = features.get(1);
    String fileName = feature.getReportFileName();
    assertThat(fileName).isEqualTo("report-feature_net-masterthought-example-ATMK-feature.html");
  }

  @Test public void getReportFileName_OnParallelTesting_ReturnsFileNameWithNumberPostfix() {
    configuration.setParallelTesting(true);
    setUpWithJson(SAMPLE_JSON);
    Feature feature = features.get(2);
    String fileName = feature.getReportFileName();
    assertThat(fileName).isEqualTo("report-feature_net-masterthought-example-ATMK-feature_sample.html");
  }

  @Test public void getTags_ReturnsTags() {
    Element element = features.get(1).getElements()[0];
    Tag[] tags = element.getTags();
    assertThat(tags).hasSize(1);
    assertThat(tags[0].getName()).isEqualTo("@checkout");
  }

  @Test public void getStatus_ReturnsStatus() {
    Feature feature = features.get(1);
    Status status = feature.getStatus();
    assertThat(status).isEqualTo(Status.FAILED);
  }

  @Test public void getName_ReturnsName() {
    Feature feature = features.get(0);
    String name = feature.getName();
    assertThat(name).isEqualTo("1st feature");
  }

  @Test public void getKeyword_ReturnsKeyword() {
    Feature feature = features.get(0);
    String keyword = feature.getKeyword();
    assertThat(keyword).isEqualTo("Feature");
  }

  @Test public void getDescription_ReturnsDescription() {
    Feature feature = features.get(0);
    String description = feature.getDescription();
    assertThat(description).isEqualTo("This is description of the feature");
  }

  @Test public void getFeatures_ReturnsOne() {
    Feature feature = features.get(0);
    int featureCounter = feature.getFeatures();
    assertThat(featureCounter).isEqualTo(1);
  }

  @Test public void getXXXFeatures_OnPassedFeature_ReturnsFeaturesForStatus() {
    Feature passedFeature = features.get(0);
    assertThat(passedFeature.getPassedFeatures()).isEqualTo(1);
    assertThat(passedFeature.getFailedFeatures()).isEqualTo(0);
  }

  @Test public void getXXXFeatures_OnFAiledFeature_ReturnsFeaturesForStatus() {
    Feature failedFeature = features.get(1);
    assertThat(failedFeature.getPassedFeatures()).isEqualTo(0);
    assertThat(failedFeature.getFailedFeatures()).isEqualTo(1);
  }

  @Test public void getScenarios_ReturnsNumberOfScenarios() {
    Feature feature = features.get(0);
    int scenarioCounter = feature.getScenarios();
    assertThat(scenarioCounter).isEqualTo(1);
  }

  @Test public void getXXXScenarios_ReturnsScenariosForStatus() {
    Feature feature = features.get(1);
    assertThat(feature.getPassedScenarios()).isEqualTo(1);
    assertThat(feature.getFailedScenarios()).isEqualTo(2);
  }

  @Test public void getSteps_ReturnsNumberOfSteps() {
    Feature feature = features.get(0);
    int stepsCounter = feature.getSteps();
    assertThat(stepsCounter).isEqualTo(10);
  }

  @Test public void getXXXSteps_ReturnsStepsForStatus() {
    Feature passingFeature = features.get(0);
    Feature feature2 = features.get(1);
    assertThat(passingFeature.getPassedSteps()).isEqualTo(10);
    assertThat(feature2.getFailedSteps()).isEqualTo(1);
    assertThat(feature2.getSkippedSteps()).isEqualTo(2);
    assertThat(feature2.getPendingSteps()).isEqualTo(1);
    assertThat(feature2.getUndefinedSteps()).isEqualTo(3);
  }

  @Test public void getDuration_ReturnsDuration() {
    Feature feature = features.get(0);
    long duration = feature.getDuration();
    assertThat(duration).isEqualTo(99263122889L);
  }

  @Test public void getFormattedDuration_ReturnsFormattedDuration() {
    Feature feature = features.get(1);
    String formattedDuration = feature.getFormattedDuration();
    assertThat(formattedDuration).isEqualTo("092ms");
  }

  @Test public void getJsonFile_ReturnsFileName() {
    Feature feature = features.get(0);
    String fileName = feature.getJsonFile();
    assertThat(fileName).endsWith(SAMPLE_JSON);
  }

  @Test public void calculateDeviceName_ReturnsDeviceName() {
    Feature feature = new Feature();
    final String jsonFileName = "json_filename_without_extension";
    Deencapsulation.setField(feature, "jsonFile", jsonFileName + ".json");
    String deviceName = Deencapsulation.invoke(feature, "calculateDeviceName");
    assertThat(deviceName).isEqualTo(jsonFileName);
  }

  @Test public void calculateDeviceName_OnFileWithoutExtension_ReturnsDeviceName() {
    Feature feature = new Feature();
    final String jsonFileName = "json_filename_without_extension";
    Deencapsulation.setField(feature, "jsonFile", jsonFileName);
    String deviceName = Deencapsulation.invoke(feature, "calculateDeviceName");
    assertThat(deviceName).isEqualTo(jsonFileName);
  }

  @Test public void getFailedCause_ReturnsFailuresMap() {
    String expectedFailedScenarioName = "Account may not have sufficient funds";
    String expectedFailedStepName = "the card is valid";
    String expectedFailedStepResultErrorMessage = "Error message not found.";
    String[] expectedFailureData = { expectedFailedScenarioName, expectedFailedStepName, expectedFailedStepResultErrorMessage };
    Map<String, String[]> expectedFailedScenariosMap = new HashMap<>(1);
    expectedFailedScenariosMap.put("0", expectedFailureData);
    Feature failedFeature = features.get(1);
    Map<String, String[]> returnedFailedScenariosMap = failedFeature.getFailedCause();
    Assert.assertEquals(returnedFailedScenariosMap.size(), expectedFailedScenariosMap.size());
    String[] expectedFailedScenariosMapKeys = new String[expectedFailedScenariosMap.size()];
    expectedFailedScenariosMap.keySet().toArray(expectedFailedScenariosMapKeys);
    String[] returnedFailedScenariosMapKeys = new String[returnedFailedScenariosMap.size()];
    returnedFailedScenariosMap.keySet().toArray(returnedFailedScenariosMapKeys);
    Assert.assertArrayEquals(returnedFailedScenariosMapKeys, expectedFailedScenariosMapKeys);
    String[][] expectedFailedScenariosMapValues = new String[expectedFailedScenariosMap.values().size()][2];
    expectedFailedScenariosMap.values().toArray(expectedFailedScenariosMapValues);
    String[][] returnedFailedScenariosMapValues = new String[returnedFailedScenariosMap.values().size()][2];
    returnedFailedScenariosMap.values().toArray(returnedFailedScenariosMapValues);
    Assert.assertArrayEquals(returnedFailedScenariosMapValues, expectedFailedScenariosMapValues);
  }
}