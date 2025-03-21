package org.sonar.plugins.php.phpunit;
import com.thoughtworks.xstream.XStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.php.PhpPlugin;
import org.sonar.plugins.php.api.Php;
import java.io.File;

/**
 * The Class PhpUnitSensor is used by the plugin to collect coverage metrics from PHPUnit report.
 */
public class PhpUnitSensor implements Sensor {
  private static final Logger LOGGER = LoggerFactory.getLogger(PhpUnitSensor.class);

  private final Settings settings;

  private final 
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/e174348192516286606c1ff5d6646cad3a168ac0/sonar-php-plugin/src/main/java/org/sonar/plugins/php/phpunit/PhpUnitSensor.java/left.java
  PhpUnitOverallCoverageResultParser
=======
  FilePredicates
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/e174348192516286606c1ff5d6646cad3a168ac0/sonar-php-plugin/src/main/java/org/sonar/plugins/php/phpunit/PhpUnitSensor.java/right.java
   
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/e174348192516286606c1ff5d6646cad3a168ac0/sonar-php-plugin/src/main/java/org/sonar/plugins/php/phpunit/PhpUnitSensor.java/left.java
  overallCoverageParser
=======
  filePredicates
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/e174348192516286606c1ff5d6646cad3a168ac0/sonar-php-plugin/src/main/java/org/sonar/plugins/php/phpunit/PhpUnitSensor.java/right.java
  ;

  private final PhpUnitCoverageResultParser coverageParser;

  private final PhpUnitItCoverageResultParser itCoverageParser;

  private final PhpUnitResultParser parser;

  private final FileSystem fileSystem;

  public PhpUnitSensor(FileSystem fileSystem, Settings settings, PhpUnitResultParser parser, PhpUnitCoverageResultParser coverageParser, PhpUnitItCoverageResultParser itCoverageParser, PhpUnitOverallCoverageResultParser overallCoverageParser) {
    this.fileSystem = fileSystem;
    this.filePredicates = fileSystem.predicates();
    this.settings = settings;
    this.parser = parser;
    this.coverageParser = coverageParser;
    this.itCoverageParser = itCoverageParser;
    this.overallCoverageParser = overallCoverageParser;
  }

  /**
   * {@inheritDoc}
   */
  @Override public void analyse(Project project, SensorContext context) {
    parseReport(PhpPlugin.PHPUNIT_TESTS_REPORT_PATH_KEY, parser);
    parseReport(PhpPlugin.PHPUNIT_COVERAGE_REPORT_PATH_KEY, coverageParser);
    parseReport(PhpPlugin.PHPUNIT_IT_COVERAGE_REPORT_PATH_KEY, itCoverageParser);
    parseReport(PhpPlugin.PHPUNIT_OVERALL_COVERAGE_REPORT_PATH_KEY, overallCoverageParser);
  }

  private void parseReport(String reportPathKey, PhpUnitParser parser) {
    String msg = PhpUnitCoverageResultParser.class.isInstance(parser) ? "coverage" : "tests";
    String reportPath = settings.getString(reportPathKey);
    if (reportPath != null) {
      File xmlFile = getIOFile(reportPath);
      if (xmlFile.exists()) {
        LOGGER.info("Analyzing PHPUnit " + msg + " report: " + reportPath);
        try {
          parser.parse(xmlFile);
        } catch (XStreamException e) {
          throw new SonarException("Report file is invalid, plugin will stop.", e);
        }
      } else {
        LOGGER.info("PHPUnit xml " + msg + " report not found: " + reportPath);
      }
    } else {
      LOGGER.info("No PHPUnit " + msg + " report provided (see \'" + reportPathKey + "\' property)");
    }
  }

  /**
   * Returns a java.io.File for the given path.
   * If path is not absolute, returns a File with module base directory as parent path.
   */
  private File getIOFile(String path) {
    File file = new File(path);
    if (!file.isAbsolute()) {
      file = new File(fileSystem.baseDir(), path);
    }
    return file;
  }

  /**
   * {@inheritDoc}
   */
  @Override public boolean shouldExecuteOnProject(Project project) {
    return fileSystem.hasFiles(filePredicates.and(filePredicates.hasLanguage(Php.KEY), filePredicates.hasType(InputFile.Type.MAIN)));
  }

  /**
   * {@inheritDoc}
   */
  @Override public String toString() {
    return "PHPUnit Sensor";
  }
}