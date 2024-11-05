package sortpom.parameter;
import java.io.File;

public class PluginParameters {
  public final File pomFile;

  public final boolean createBackupFile;

  public final String backupFileExtension;

  public final String violationFilename;

  public final String encoding;

  public final LineSeparatorUtil lineSeparatorUtil;

  public final String indentCharacters;

  public final boolean expandEmptyElements;

  public final String predefinedSortOrder;

  public final String customSortOrderFile;

  public final DependencySortOrder sortDependencies;

  public final DependencySortOrder sortDependencyExclusions;

  public final DependencySortOrder sortPlugins;

  public final boolean sortProperties;

  public final boolean sortModules;

  public final boolean keepBlankLines;

  public final boolean indentBlankLines;

  public final VerifyFailType verifyFailType;

  public final boolean ignoreLineSeparators;

  public final boolean keepTimestamp;

  private PluginParameters(File pomFile, boolean createBackupFile, String backupFileExtension, String violationFilename, String encoding, LineSeparatorUtil lineSeparatorUtil, boolean expandEmptyElements, boolean keepBlankLines, String indentCharacters, boolean indentBlankLines, String predefinedSortOrder, String customSortOrderFile, DependencySortOrder sortDependencies, DependencySortOrder sortDependencyExclusions, DependencySortOrder sortPlugins, boolean sortProperties, boolean sortModules, VerifyFailType verifyFailType, boolean ignoreLineSeparators, boolean keepTimestamp) {
    this.pomFile = pomFile;
    this.createBackupFile = createBackupFile;
    this.backupFileExtension = backupFileExtension;
    this.violationFilename = violationFilename;
    this.encoding = encoding;
    this.lineSeparatorUtil = lineSeparatorUtil;
    this.indentCharacters = indentCharacters;
    this.expandEmptyElements = expandEmptyElements;
    this.predefinedSortOrder = predefinedSortOrder;
    this.customSortOrderFile = customSortOrderFile;
    this.sortDependencies = sortDependencies;
    this.sortDependencyExclusions = sortDependencyExclusions;
    this.sortPlugins = sortPlugins;
    this.sortProperties = sortProperties;
    this.sortModules = sortModules;
    this.keepBlankLines = keepBlankLines;
    this.indentBlankLines = indentBlankLines;
    this.verifyFailType = verifyFailType;
    this.ignoreLineSeparators = ignoreLineSeparators;
    this.keepTimestamp = keepTimestamp;
  }

  public static Builder builder() {
    return new PluginParameters.Builder();
  }

  public static class Builder {
    private File pomFile;

    private boolean createBackupFile;

    private String backupFileExtension;

    private String violationFilename;

    private String encoding;

    private LineSeparatorUtil lineSeparatorUtil;

    private String indentCharacters;

    private boolean indentBlankLines;

    private boolean expandEmptyElements;

    private String predefinedSortOrder;

    private String customSortOrderFile;

    private DependencySortOrder sortDependencies;

    private DependencySortOrder sortDependencyExclusions;

    private DependencySortOrder sortPlugins;

    private boolean sortProperties;

    private boolean sortModules;

    private boolean keepBlankLines;

    private VerifyFailType verifyFailType;

    private boolean ignoreLineSeparators;

    private boolean keepTimestamp;

    private Builder() {
    }

    public Builder setPomFile(final File pomFile) {
      this.pomFile = pomFile;
      return this;
    }

    public Builder setFileOutput(final boolean createBackupFile, final String backupFileExtension, String violationFilename, boolean keepTimestamp) {
      this.createBackupFile = createBackupFile;
      this.backupFileExtension = backupFileExtension;
      this.violationFilename = violationFilename;
      this.keepTimestamp = keepTimestamp;
      return this;
    }

    public Builder setEncoding(final String encoding) {
      this.encoding = encoding;
      return this;
    }

    public Builder setSortOrder(final String customSortOrderFile, final String predefinedSortOrder) {
      this.customSortOrderFile = customSortOrderFile;
      this.predefinedSortOrder = predefinedSortOrder;
      return this;
    }

    public Builder setSortEntities(final String sortDependencies, final String sortDependencyExclusions, final String sortPlugins, final boolean sortProperties, final boolean sortModules) {
      this.sortDependencies = new DependencySortOrder(sortDependencies);
      this.sortDependencyExclusions = new DependencySortOrder(sortDependencyExclusions);
      this.sortPlugins = new DependencySortOrder(sortPlugins);
      this.sortProperties = sortProperties;
      this.sortModules = sortModules;
      return this;
    }

    public Builder setTriggers(boolean ignoreLineSeparators) {
      this.ignoreLineSeparators = ignoreLineSeparators;
      return this;
    }

    public PluginParameters build() {
      return new PluginParameters(pomFile, createBackupFile, backupFileExtension, violationFilename, encoding, lineSeparatorUtil, expandEmptyElements, spaceBeforeCloseEmptyElement, keepBlankLines, indentCharacters, indentBlankLines, indentSchemaLocation, predefinedSortOrder, customSortOrderFile, sortDependencies, sortDependencyExclusions, sortPlugins, sortProperties, sortModules, sortExecutions, verifyFailType, verifyFailOn, ignoreLineSeparators, keepTimestamp);
    }

    private boolean indentSchemaLocation;

    private boolean spaceBeforeCloseEmptyElement;

    private boolean sortExecutions;

    private VerifyFailOnType verifyFailOn;

    public Builder setFormatting(final String lineSeparator, final boolean expandEmptyElements, final boolean spaceBeforeCloseEmptyElement, final boolean keepBlankLines) {
      this.lineSeparatorUtil = new LineSeparatorUtil(lineSeparator);
      this.expandEmptyElements = expandEmptyElements;
      this.spaceBeforeCloseEmptyElement = spaceBeforeCloseEmptyElement;
      this.keepBlankLines = keepBlankLines;
      return this;
    }

    public Builder setIndent(final int nrOfIndentSpace, final boolean indentBlankLines, boolean indentSchemaLocation) {
      this.indentCharacters = new IndentCharacters(nrOfIndentSpace).getIndentCharacters();
      this.indentBlankLines = indentBlankLines;
      this.indentSchemaLocation = indentSchemaLocation;
      return this;
    }

    public Builder setSortEntities(final String sortDependencies, final String sortPlugins, final boolean sortProperties, final boolean sortModules, boolean sortExecutions) {
      this.sortDependencies = new DependencySortOrder(sortDependencies);
      this.sortPlugins = new DependencySortOrder(sortPlugins);
      this.sortProperties = sortProperties;
      this.sortModules = sortModules;
      this.sortExecutions = sortExecutions;
      return this;
    }

    public Builder setVerifyFail(String verifyFail, String verifyFailOn) {
      this.verifyFailType = VerifyFailType.fromString(verifyFail);
      this.verifyFailOn = VerifyFailOnType.fromString(verifyFailOn);
      return this;
    }
  }

  public final boolean spaceBeforeCloseEmptyElement;

  public final boolean sortExecutions;

  public final boolean indentSchemaLocation;

  public final VerifyFailOnType verifyFailOn;

  private PluginParameters(File pomFile, boolean createBackupFile, String backupFileExtension, String violationFilename, String encoding, LineSeparatorUtil lineSeparatorUtil, boolean expandEmptyElements, boolean spaceBeforeCloseEmptyElement, boolean keepBlankLines, String indentCharacters, boolean indentBlankLines, boolean indentSchemaLocation, String predefinedSortOrder, String customSortOrderFile, DependencySortOrder sortDependencies, DependencySortOrder sortPlugins, boolean sortProperties, boolean sortModules, boolean sortExecutions, VerifyFailType verifyFailType, VerifyFailOnType verifyFailOn, boolean ignoreLineSeparators, boolean keepTimestamp) {
    this.pomFile = pomFile;
    this.createBackupFile = createBackupFile;
    this.backupFileExtension = backupFileExtension;
    this.violationFilename = violationFilename;
    this.encoding = encoding;
    this.lineSeparatorUtil = lineSeparatorUtil;
    this.indentCharacters = indentCharacters;
    this.expandEmptyElements = expandEmptyElements;
    this.spaceBeforeCloseEmptyElement = spaceBeforeCloseEmptyElement;
    this.predefinedSortOrder = predefinedSortOrder;
    this.customSortOrderFile = customSortOrderFile;
    this.sortDependencies = sortDependencies;
    this.sortPlugins = sortPlugins;
    this.sortProperties = sortProperties;
    this.sortModules = sortModules;
    this.sortExecutions = sortExecutions;
    this.keepBlankLines = keepBlankLines;
    this.indentBlankLines = indentBlankLines;
    this.indentSchemaLocation = indentSchemaLocation;
    this.verifyFailType = verifyFailType;
    this.verifyFailOn = verifyFailOn;
    this.ignoreLineSeparators = ignoreLineSeparators;
    this.keepTimestamp = keepTimestamp;
  }
}