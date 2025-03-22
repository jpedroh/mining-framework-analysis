package org.movsim.input;

public class ProjectMetaData {
  private static ProjectMetaData singleton = new ProjectMetaData();

  private String projectName = "";

  private String pathToProjectXmlFile;

  private String outputPath;

  private String xodrFileName;

  private String xodrPath;

  private boolean instantaneousFileOutput = true;

  private boolean onlyValidation = false;

  private boolean writeInternalXml = false;

  /** Needed for Applet */
  private boolean xmlFromResources = false;

  /**
     * private constructor: singleton pattern.
     */
  private ProjectMetaData() {
  }

  /**
     * Gets the single instance of ProjectMetaDataImpl.
     * 
     * @return single instance of ProjectMetaDataImpl
     */
  public static ProjectMetaData getInstance() {
    return singleton;
  }

  public String getProjectName() {
    return projectName;
  }

  /**
     * Sets the project name.
     * 
     * @param projectName
     *            the new project name
     */
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getPathToProjectXmlFile() {
    return pathToProjectXmlFile;
  }

  /**
     * Sets the path to project xml file.
     * 
     * @param pathToProjectXmlFile
     *            the new path to project xml file
     */
  public void setPathToProjectXmlFile(String pathToProjectXmlFile) {
    this.pathToProjectXmlFile = pathToProjectXmlFile;
  }

  public String getOutputPath() {
    return outputPath;
  }

  /**
     * Sets the output path.
     * 
     * @param outputPath
     *            the new output path
     */
  public void setOutputPath(String outputPath) {
    this.outputPath = outputPath;
  }

  public void setXodrFilename(String xodrFilename) {
    this.xodrFileName = xodrFilename;
  }

  public String getXodrFilename() {
    return xodrFileName;
  }

  public void setXodrPath(String xodrPath) {
    this.xodrPath = xodrPath;
  }

  public String getXodrPath() {
    return xodrPath;
  }

  public boolean isInstantaneousFileOutput() {
    return instantaneousFileOutput;
  }

  /**
     * Sets the instantaneous file output.
     * 
     * @param instantaneousFileOutput
     *            the new instantaneous file output
     */
  public void setInstantaneousFileOutput(boolean instantaneousFileOutput) {
    this.instantaneousFileOutput = instantaneousFileOutput;
  }

  public boolean isOnlyValidation() {
    return onlyValidation;
  }

  /**
     * Sets the only validation.
     * 
     * @param onlyValidation
     *            the new only validation
     */
  public void setOnlyValidation(boolean onlyValidation) {
    this.onlyValidation = onlyValidation;
  }

  public boolean isWriteInternalXml() {
    return writeInternalXml;
  }

  /**
     * Sets the write internal xml.
     * 
     * @param writeInternalXml
     *            the new write internal xml
     */
  public void setWriteInternalXml(boolean writeInternalXml) {
    this.writeInternalXml = writeInternalXml;
  }

  public boolean isXmlFromResources() {
    return xmlFromResources;
  }

  /**
     * Sets the xml from resources.
     * 
     * @param xmlFromResources
     *            the new xml from resources
     */
  public void setXmlFromResources(boolean xmlFromResources) {
    this.xmlFromResources = xmlFromResources;
  }
}