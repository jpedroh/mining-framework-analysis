package org.sonar.plugins.php.phpunit;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.utils.ParsingUtils;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.php.api.Php;
import org.sonar.plugins.php.phpunit.xml.TestCase;
import org.sonar.plugins.php.phpunit.xml.TestSuite;
import org.sonar.plugins.php.phpunit.xml.TestSuites;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class PhpUnitResultParser.
 */
public class PhpUnitResultParser implements BatchExtension, PhpUnitParser {
  private static final double PERCENT = 100d;

  private static final double MILLISECONDS = 1000d;

  private static final int PRECISION = 1;

  /**
   * The logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(PhpUnitResultParser.class);

  /**
   * The context.
   */
  private SensorContext context;

  /**
   * The project.
   */
  private FileSystem fileSystem;

  private FilePredicates filePredicates;

  /**
   * Instantiates a new php unit result parser.
   *
   * @param project the project
   * @param context the context
   */
  public PhpUnitResultParser(SensorContext context, FileSystem fileSystem) {
    super();
    this.context = context;
    this.fileSystem = fileSystem;
    this.filePredicates = fileSystem.predicates();
  }

  /**
   * Gets the test suites.
   *
   * @param report the report
   * @return the test suites
   */
  protected TestSuites getTestSuites(File report) {
    InputStream inputStream = null;
    try {
      XStream xstream = new XStream();
      xstream.setClassLoader(getClass().getClassLoader());
      xstream.aliasSystemAttribute("fileName", "class");
      xstream.processAnnotations(TestSuites.class);
      xstream.processAnnotations(TestSuite.class);
      xstream.processAnnotations(TestCase.class);
      inputStream = new FileInputStream(report);
      TestSuites testSuites = (TestSuites) xstream.fromXML(inputStream);
      LOG.debug("Tests suites: " + testSuites.getTestSuites());
      return testSuites;
    } catch (IOException e) {
      throw new SonarException("Can\'t read PhpUnit report : " + report.getAbsolutePath(), e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  /**
   * Gets the php file pointed by the report.
   *
   * @param report the unit test report
   */
  private InputFile getUnitTestInputFile(PhpUnitTestReport report) {
    return fileSystem.inputFile(fileSystem.predicates().and(filePredicates.hasPath(report.getFile()), filePredicates.hasType(InputFile.Type.TEST), filePredicates.hasLanguage(Php.KEY)));
  }


<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/e174348192516286606c1ff5d6646cad3a168ac0/sonar-php-plugin/src/main/java/org/sonar/plugins/php/phpunit/PhpUnitResultParser.java/left.java
  @VisibleForTesting Resource getUnitTestResource(String filename) {
    File testFile;
    try {
      testFile = new File(filename);
    } catch (NullPointerException e) {
      LOG.warn("Unit test resource not found: " + filename);
      return null;
    }
    Resource resource = org.sonar.api.resources.File.fromIOFile(testFile, project);
    if (resource == null) {
      resource = org.sonar.api.resources.File.fromIOFile(testFile, moduleFileSystem.testDirs());
    }
    return resource;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
   * Insert zero when no reports can be found.
   */
  private void insertZeroWhenNoReports() {
    context.saveMeasure(CoreMetrics.TESTS, 0.0);
  }

  /**
   * Collect the metrics found.
   *
   * @param reportFile the reports directories to be scan
   */
  public void parse(File reportFile) {
    if (reportFile == null) {
      insertZeroWhenNoReports();
    } else {
      LOG.debug("Parsing file: " + reportFile.getAbsolutePath());
      parseFile(reportFile);
    }
  }

  /**
   * Parses the report file.
   *
   * @param report the report file
   */
  private void parseFile(File report) {
    TestSuites testSuites = getTestSuites(report);
    List<PhpUnitTestReport> fileReports = readSuites(testSuites);
    for (PhpUnitTestReport fileReport : fileReports) {
      saveTestReportMeasures(fileReport);
    }
  }

  /**
   * Launches {@see PhpTestSuiteReader#readSuite(TestSuite)} for all its descendants.
   *
   * @param testSuites the test suites
   * @return List<PhpUnitTestReport> A list of all test reports
   */
  private List<PhpUnitTestReport> readSuites(TestSuites testSuites) {
    List<PhpUnitTestReport> result = new ArrayList<PhpUnitTestReport>();
    for (TestSuite testSuite : testSuites.getTestSuites()) {
      PhpTestSuiteReader reader = new PhpTestSuiteReader();
      reader.readSuite(testSuite, null);
      result.addAll(reader.getReportsPerClass());
    }
    return result;
  }

  /**
   * Saves the measures contained in the test report.
   *
   * @param fileReport the unit test report
   */
  protected void saveTestReportMeasures(PhpUnitTestReport fileReport) {
    if (!fileReport.isValid()) {
      return;
    }
    InputFile unitTestFile = getUnitTestInputFile(fileReport);
    if (unitTestFile != null) {
      double testsCount = fileReport.getTests() - fileReport.getSkipped();
      if (fileReport.getSkipped() > 0) {
        context.saveMeasure(unitTestFile, CoreMetrics.SKIPPED_TESTS, (double) fileReport.getSkipped());
      }
      double duration = Math.round(fileReport.getTime() * MILLISECONDS);
      context.saveMeasure(unitTestFile, CoreMetrics.TEST_EXECUTION_TIME, duration);
      context.saveMeasure(unitTestFile, CoreMetrics.TESTS, testsCount);
      context.saveMeasure(unitTestFile, CoreMetrics.TEST_ERRORS, (double) fileReport.getErrors());
      context.saveMeasure(unitTestFile, CoreMetrics.TEST_FAILURES, (double) fileReport.getFailures());
      if (testsCount > 0) {
        double passedTests = testsCount - fileReport.getErrors() - fileReport.getFailures();
        double percentage = passedTests * PERCENT / testsCount;
        context.saveMeasure(unitTestFile, CoreMetrics.TEST_SUCCESS_DENSITY, ParsingUtils.scaleValue(percentage));
      }
      saveTestsDetails(fileReport);
    } else {
      LOG.debug("Following file is not located in the test folder specified in the Sonar configuration: " + fileReport.getFile() + ". The test results won\'t be reported in Sonar.");
    }
  }

  /**
   * Save tests details.
   *
   * @param fileReport the file report
   */
  private void saveTestsDetails(PhpUnitTestReport fileReport) {
    StringBuilder details = new StringBuilder();
    details.append("<tests-details>");
    for (TestCase detail : fileReport.getDetails()) {
      double time = ParsingUtils.scaleValue(detail.getTime() * MILLISECONDS, PRECISION);
      details.append("<testcase status=\"").append(detail.getStatus()).append("\" time=\"");
      details.append(time).append("\" name=\"").append(detail.getName().replaceAll(" ", "_")).append("\"");
      boolean isError = TestCase.STATUS_ERROR.equals(detail.getStatus());
      if (isError || TestCase.STATUS_FAILURE.equals(detail.getStatus())) {
        details.append(">").append(isError ? "<error message=\"" : "<failure message=\"");
        details.append(StringEscapeUtils.escapeXml(detail.getErrorMessage())).append("\"><![CDATA[");
        details.append(StringEscapeUtils.escapeXml(detail.getStackTrace())).append("]]>");
        details.append(isError ? "</error>" : "</failure>").append("</testcase>");
      } else {
        details.append("/>");
      }
    }
    details.append("</tests-details>");
    InputFile unitTestFile = getUnitTestInputFile(fileReport);
    if (unitTestFile != null) {
      context.saveMeasure(unitTestFile, new Measure(CoreMetrics.TEST_DATA, details.toString()));
    }
  }
}