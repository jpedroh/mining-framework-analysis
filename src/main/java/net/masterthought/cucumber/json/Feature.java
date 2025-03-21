package net.masterthought.cucumber.json;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import net.masterthought.cucumber.Configuration;
import java.util.Arrays;
import net.masterthought.cucumber.Reportable;
import java.util.List;
import net.masterthought.cucumber.json.support.Durationable;
import org.apache.commons.lang.StringUtils;
import net.masterthought.cucumber.json.support.Status;
import net.masterthought.cucumber.json.support.StatusCounter;
import net.masterthought.cucumber.util.Util;

public class Feature implements Reportable, Durationable {
  private final String id = null;

  private final String name = null;

  @JsonProperty(value = "uri") private final String uri = null;

  private final String description = null;

  private final String keyword = null;

  private final Integer line = null;

  private Element[] elements = new Element[0];

  private final Tag[] tags = new Tag[0];

  private String reportFileName;

  /**
     * When feature if executed against different devices, platforms or targets,
     * then the file name tells for which qualifier the tests were executed.
     * */
  private String qualifier;

  /**
     * Collects those of elements which are scenarios, not eg background.
     */
  private final List<Element> scenarios = new ArrayList<>();

  private final StatusCounter elementsCounter = new StatusCounter();

  private final StatusCounter stepsCounter = new StatusCounter();

  private Status featureStatus;

  private long duration;

  public String getId() {
    return id;
  }

  public String getUri() {
    return uri;
  }

  public void addElements(Element[] newElements) {
    Element[] both = Arrays.copyOf(elements, elements.length + newElements.length);
    System.arraycopy(newElements, 0, both, elements.length, newElements.length);
    elements = both;
  }

  public Element[] getElements() {
    return elements;
  }

  public String getReportFileName() {
    return reportFileName;
  }

  public String getQualifier() {
    return qualifier;
  }

  /**
     * @param qualifier name of the JSON file with report, used for parallel testing
     */
  public void setQualifier(String qualifier) {
    this.qualifier = qualifier;
  }

  public Tag[] getTags() {
    return tags;
  }

  @Override public Status getStatus() {
    return featureStatus;
  }

  @Override public String getName() {
    return StringUtils.defaultString(name);
  }

  public String getKeyword() {
    return StringUtils.defaultString(keyword);
  }

  public Integer getLine() {
    return line;
  }

  public String getDescription() {
    return StringUtils.defaultString(description);
  }

  @Override public int getFeatures() {
    return 1;
  }

  @Override public int getPassedFeatures() {
    return getStatus().isPassed() ? 1 : 0;
  }

  @Override public int getFailedFeatures() {
    return getStatus().isPassed() ? 0 : 1;
  }

  @Override public int getScenarios() {
    return scenarios.size();
  }

  @Override public int getSteps() {
    return stepsCounter.size();
  }

  @Override public int getPassedSteps() {
    return stepsCounter.getValueFor(Status.PASSED);
  }

  @Override public int getFailedSteps() {
    return stepsCounter.getValueFor(Status.FAILED);
  }

  @Override public int getPendingSteps() {
    return stepsCounter.getValueFor(Status.PENDING);
  }

  @Override public int getSkippedSteps() {
    return stepsCounter.getValueFor(Status.SKIPPED);
  }

  @Override public int getUndefinedSteps() {
    return stepsCounter.getValueFor(Status.UNDEFINED);
  }

  @Override public long getDuration() {
    return duration;
  }

  @Override public String getFormattedDuration() {
    return Util.formatDuration(duration);
  }

  @Override public int getPassedScenarios() {
    return elementsCounter.getValueFor(Status.PASSED);
  }

  @Override public int getFailedScenarios() {
    return elementsCounter.getValueFor(Status.FAILED);
  }

  /**
     * Sets additional information and calculates values which should be calculated during object creation.
     *
     * @param jsonFileNo    index of the JSON file
     * @param configuration configuration for the report
     */
  public void setMetaData(int jsonFileNo, Configuration configuration) {
    for (Element element : elements) {
      element.setMetaData(this, configuration);
      if (element.isScenario()) {
        scenarios.add(element);
      }
    }
    reportFileName = calculateReportFileName(jsonFileNo);
    featureStatus = calculateFeatureStatus();
    calculateSteps();
  }

  private String calculateReportFileName(int jsonFileNo) {
    String fileName = "report-feature_";
    if (jsonFileNo > 0) {
      fileName += jsonFileNo + "_";
    }
    fileName += Util.toValidFileName(uri);
    fileName += ".html";
    return fileName;
  }

  private Status calculateFeatureStatus() {
    StatusCounter statusCounter = new StatusCounter();
    for (Element element : elements) {
      statusCounter.incrementFor(element.getStatus());
    }
    return statusCounter.getFinalStatus();
  }

  private void calculateSteps() {
    for (Element element : elements) {
      if (element.isScenario()) {
        elementsCounter.incrementFor(element.getStatus());
      }
      for (Step step : element.getSteps()) {
        stepsCounter.incrementFor(step.getResult().getStatus());
        duration += step.getDuration();
      }
    }
  }
}