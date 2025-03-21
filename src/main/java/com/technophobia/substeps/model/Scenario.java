package com.technophobia.substeps.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.runner.Description;

public class Scenario extends RootFeature {
  @Override public String toString() {
    return "Scenario: " + description;
  }

  private String description;

  private List<Step> steps;

  private Background background = null;

  private List<ExampleParameter> exampleParameters = null;

  private String[] paramNames = null;

  private boolean outline;

  private int scenarioLineNumber;

  private int exampleKeysLineNumber;

  private Description junitDescription;

  /**
     * @return the background
     */
  public Background getBackground() {
    return background;
  }

  private int sourceStartOffset = -1;

  public void setBackground(final Background background) {
    this.background = background;
  }

  private int sourceStartLineNumber = -1;

  private int sourceEndOffset = -1;

  public boolean hasBackground() {
    return background != null;
  }

  /**
     * @return the junitDescription
     */
  public Description getJunitDescription() {
    return junitDescription;
  }

  /**
     * @param junitDescription
     *            the junitDescription to set
     */
  public void setJunitDescription(final Description junitDescription) {
    this.junitDescription = junitDescription;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public List<Step> getSteps() {
    return steps;
  }

  public List<ExampleParameter> getExampleParameters() {
    return exampleParameters;
  }

  public void setOutline(final boolean outline) {
    this.outline = outline;
  }

  public int getScenarioLineNumber() {
    return scenarioLineNumber;
  }

  public int getExampleKeysLineNumber() {
    return exampleKeysLineNumber;
  }

  public void setScenarioLineNumber(final int scenarioLineNumber) {
    this.scenarioLineNumber = scenarioLineNumber;
  }

  public void setExampleKeysLineNumber(final int exampleKeysLineNumber) {
    this.exampleKeysLineNumber = exampleKeysLineNumber;
  }

  /**
     * @param cukeArg
     */
  public void addStep(final Step cukeArg) {
    if (cukeArg != null) {
      if (steps == null) {
        steps = new ArrayList<Step>();
      }
      steps.add(cukeArg);
    }
  }

  /**
     * @param split
     */
  public void addExampleKeys(final String[] split) {
    paramNames = split;
    exampleParameters = new ArrayList<ExampleParameter>();
  }

  public void addExampleValues(final int lineNumber, final String[] split) {
    final Map<String, String> row = new HashMap<String, String>();
    for (int i = 1; i < split.length; i++) {
      row.put(paramNames[i].trim(), split[i].trim());
    }
    exampleParameters.add(new ExampleParameter(lineNumber, row));
  }

  /**
     * @return
     */
  public boolean isOutline() {
    return outline;
  }

  /**
	 * @return the sourceStartOffset
	 */
  public int getSourceStartOffset() {
    return sourceStartOffset;
  }

  /**
	 * @param sourceStartOffset the sourceStartOffset to set
	 */
  public void setSourceStartOffset(final int sourceStartOffset) {
    this.sourceStartOffset = sourceStartOffset;
  }

  /**
	 * @return the sourceStartLineNumber
	 */
  public int getSourceStartLineNumber() {
    return sourceStartLineNumber;
  }

  /**
	 * @param sourceStartLineNumber the sourceStartLineNumber to set
	 */
  public void setSourceStartLineNumber(final int sourceStartLineNumber) {
    this.sourceStartLineNumber = sourceStartLineNumber;
  }

  /**
	 * @param end
	 */
  public void setSourceEndOffset(final int end) {
    this.sourceEndOffset = end;
  }

  /**
	 * @return the sourceEndOffset
	 */
  public int getSourceEndOffset() {
    return sourceEndOffset;
  }
}