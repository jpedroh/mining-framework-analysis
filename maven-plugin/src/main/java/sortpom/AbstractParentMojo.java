package sortpom;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.File;

abstract class AbstractParentMojo extends AbstractMojo {
  @Parameter(property = "sort.pomFile", defaultValue = "${project.file}") File pomFile;

  @Parameter(property = "sort.createBackupFile", defaultValue = "true") boolean createBackupFile;

  @Parameter(property = "sort.backupFileExtension", defaultValue = ".bak") String backupFileExtension;

  @Parameter(property = "sort.encoding", defaultValue = "UTF-8") String encoding;

  @Parameter(property = "sort.lineSeparator", defaultValue = "${line.separator}") String lineSeparator;

  @Parameter(property = "sort.expandEmptyElements", defaultValue = "true") boolean expandEmptyElements;

  @Parameter(property = "sort.keepBlankLines", defaultValue = "false") boolean keepBlankLines;

  @Parameter(property = "sort.nrOfIndentSpace", defaultValue = "2") int nrOfIndentSpace;

  @Parameter(property = "sort.indentBlankLines", defaultValue = "false") boolean indentBlankLines;

  @Parameter(property = "sort.predefinedSortOrder") String predefinedSortOrder;

  @Parameter(property = "sort.sortOrderFile") String sortOrderFile;

  @Parameter(property = "sort.sortDependencies") String sortDependencies;

  @Parameter(property = "sort.sortDependencyExclusions") String sortDependencyExclusions;

  @Parameter(property = "sort.sortPlugins") String sortPlugins;

  @Parameter(property = "sort.sortProperties", defaultValue = "false") boolean sortProperties;

  @Parameter(property = "sort.sortModules", defaultValue = "false") boolean sortModules;

  @Parameter(property = "sort.skip", defaultValue = "false") private boolean skip;

  @Parameter(property = "sort.keepTimestamp", defaultValue = "false") boolean keepTimestamp;

  final SortPomImpl sortPomImpl = new SortPomImpl();

  @Override public void execute() throws MojoFailureException {
    if (skip) {
      getLog().info("Skipping Sortpom");
    } else {
      setup();
      sortPom();
    }
  }

  protected abstract void sortPom() throws MojoFailureException;

  protected abstract void setup() throws MojoFailureException;

  @Parameter(property = "sort.spaceBeforeCloseEmptyElement", defaultValue = "true") boolean spaceBeforeCloseEmptyElement;

  @Parameter(property = "sort.ignoreLineSeparators", defaultValue = "true") boolean ignoreLineSeparators;

  @Parameter(property = "sort.indentSchemaLocation", defaultValue = "false") boolean indentSchemaLocation;

  @Parameter(property = "sort.sortExecutions", defaultValue = "false") boolean sortExecutions;
}