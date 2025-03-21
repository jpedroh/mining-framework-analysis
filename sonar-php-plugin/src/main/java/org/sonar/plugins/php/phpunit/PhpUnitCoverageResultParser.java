package org.sonar.plugins.php.phpunit;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.ParsingUtils;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.php.api.Php;
import org.sonar.plugins.php.phpunit.xml.CoverageNode;
import org.sonar.plugins.php.phpunit.xml.FileNode;
import org.sonar.plugins.php.phpunit.xml.LineNode;
import org.sonar.plugins.php.phpunit.xml.MetricsNode;
import org.sonar.plugins.php.phpunit.xml.PackageNode;
import org.sonar.plugins.php.phpunit.xml.ProjectNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class PhpUnitCoverageResultParser.
 */
public class PhpUnitCoverageResultParser implements BatchExtension, PhpUnitParser {
  private static final Map<Resource, Measure> MEASURES_BY_RESOURCE = new HashMap<Resource, Measure>();

  private static final Logger LOG = LoggerFactory.getLogger(PhpUnitCoverageResultParser.class);

  private final SensorContext context;

  private final FileSystem fileSystem;

  protected Metric LINE_COVERAGE = CoreMetrics.LINE_COVERAGE;

  protected Metric LINES_TO_COVER = CoreMetrics.LINES_TO_COVER;

  protected Metric UNCOVERED_LINES = CoreMetrics.UNCOVERED_LINES;

  protected Metric COVERAGE_LINE_HITS_DATA = CoreMetrics.COVERAGE_LINE_HITS_DATA;

  /**
   * Instantiates a new php unit coverage result parser.
   *
   * @param context the context
   */
  public PhpUnitCoverageResultParser(SensorContext context, FileSystem fileSystem) {
    super();
    this.context = context;
    this.fileSystem = fileSystem;
  }

  /**
   * Parses PHPUnit coverage file.
   *
   * @param coverageReportFile the coverage report file
   */
  public void parse(File coverageReportFile) {
    LOG.debug("Parsing file: " + coverageReportFile.getAbsolutePath());
    parseFile(coverageReportFile);
  }

  /**
   * Parses the file.
   *
   * @param coverageReportFile the coverage report file
   */
  private void parseFile(File coverageReportFile) {
    CoverageNode coverage = getCoverage(coverageReportFile);
    List<ProjectNode> projects = coverage.getProjects();
    if (projects != null && !projects.isEmpty()) {
      ProjectNode projectNode = projects.get(0);
      LOG.info("Project: " + projectNode.getName());
      parseFileNodes(projectNode.getFiles());
      parsePackagesNodes(projectNode.getPackages());
      saveMeasureForMissingFiles();
    }
  }

  /**
   * Set default 0 value for files that do not have coverage metrics because they were not touched by any test,
   * and thus not present in the coverage report file.
   */
  private void saveMeasureForMissingFiles() {
    FilePredicate mainFilesPredicate = fileSystem.predicates().and(fileSystem.predicates().hasType(InputFile.Type.MAIN), fileSystem.predicates().hasLanguage(Php.KEY));
    for (InputFile phpFile : fileSystem.inputFiles(mainFilesPredicate)) {
      org.sonar.api.resources.File resource = org.sonar.api.resources.File.create(phpFile.relativePath());
      if (
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/e174348192516286606c1ff5d6646cad3a168ac0/sonar-php-plugin/src/main/java/org/sonar/plugins/php/phpunit/PhpUnitCoverageResultParser.java/left.java
      resource != null && context.getMeasure(resource, LINE_COVERAGE) == null
=======
      context.getMeasure(resource, CoreMetrics.LINE_COVERAGE) == null
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/e174348192516286606c1ff5d6646cad3a168ac0/sonar-php-plugin/src/main/java/org/sonar/plugins/php/phpunit/PhpUnitCoverageResultParser.java/right.java
      ) {
        LOG.debug("Coverage metrics have not been set on \'{}\': default values will be inserted.", phpFile.file().getName());
        context.saveMeasure(resource, LINE_COVERAGE, 0.0);
        Measure ncloc = context.getMeasure(resource, CoreMetrics.NCLOC);
        if (ncloc != null && context.getMeasure(LINES_TO_COVER) == null) {
          context.saveMeasure(resource, LINES_TO_COVER, ncloc.getValue());
        }
        if (ncloc != null && context.getMeasure(UNCOVERED_LINES) == null) {
          context.saveMeasure(resource, UNCOVERED_LINES, ncloc.getValue());
        }
      }
    }
  }

  private void parsePackagesNodes(List<PackageNode> packages) {
    if (packages != null) {
      for (PackageNode packageNode : packages) {
        parseFileNodes(packageNode.getFiles());
      }
    }
  }

  private void parseFileNodes(List<FileNode> fileNodes) {
    if (fileNodes != null) {
      for (FileNode file : fileNodes) {
        saveCoverageMeasure(file);
      }
    }
  }

  /**
   * Saves the required metrics found on the fileNode
   *
   * @param fileNode the file
   */
  protected void saveCoverageMeasure(FileNode fileNode) {
    InputFile inputFile = fileSystem.inputFile(fileSystem.predicates().hasAbsolutePath(fileNode.getName()));
    if (inputFile != null) {
      org.sonar.api.resources.File phpFile = org.sonar.api.resources.File.create(inputFile.relativePath());
      PropertiesBuilder<Integer, Integer> lineHits = new PropertiesBuilder<Integer, Integer>(COVERAGE_LINE_HITS_DATA);
      if (fileNode.getLines() != null) {
        for (LineNode line : fileNode.getLines()) {
          saveLineMeasure(line, lineHits);
        }
      }
      MetricsNode metrics = fileNode.getMetrics();
      Measure measure = lineHits.build();
      logMeasureByResource(phpFile, measure);
      context.saveMeasure(phpFile, measure);
      double totalStatementsCount = metrics.getTotalStatementsCount();
      double uncoveredLines = totalStatementsCount - metrics.getCoveredStatements();
      double lineCoverage = 0;
      if (metrics.getCoveredStatements() != 0) {
        lineCoverage = metrics.getCoveredStatements() / totalStatementsCount;
      }
      context.saveMeasure(phpFile, LINES_TO_COVER, totalStatementsCount);
      context.saveMeasure(phpFile, UNCOVERED_LINES, uncoveredLines);
      context.saveMeasure(phpFile, LINE_COVERAGE, ParsingUtils.scaleValue(lineCoverage * 100.0));
    }
  }

  private void logMeasureByResource(Resource resource, Measure measure) {
    if (LOG.isDebugEnabled()) {
      Measure alreadySaved = MEASURES_BY_RESOURCE.get(resource);
      if (alreadySaved == null) {
        MEASURES_BY_RESOURCE.put(resource, measure);
      } else {
        LOG.debug("Measure {} already saved for resoruce {}", measure, resource);
      }
    }
  }

  /**
   * Save line measure.
   *
   * @param line     the line
   * @param lineHits the line hits
   */
  private void saveLineMeasure(LineNode line, PropertiesBuilder<Integer, Integer> lineHits) {
    lineHits.add(line.getNum(), line.getCount());
  }

  /**
   * Gets the coverage.
   *
   * @param coverageReportFile the coverage report file
   * @return the coverage
   */
  private CoverageNode getCoverage(File coverageReportFile) {
    InputStream inputStream = null;
    try {
      XStream xstream = new XStream();
      xstream.setClassLoader(getClass().getClassLoader());
      xstream.aliasSystemAttribute("classType", "class");
      xstream.processAnnotations(CoverageNode.class);
      xstream.processAnnotations(ProjectNode.class);
      xstream.processAnnotations(FileNode.class);
      xstream.processAnnotations(MetricsNode.class);
      xstream.processAnnotations(LineNode.class);
      inputStream = new FileInputStream(coverageReportFile);
      return (CoverageNode) xstream.fromXML(inputStream);
    } catch (IOException e) {
      throw new SonarException("Can\'t read phpUnit report: " + coverageReportFile.getName(), e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }
}