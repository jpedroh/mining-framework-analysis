package hudson.tasks.junit;
import hudson.XmlFile;
import hudson.util.HeapSpaceStringConverter;
import hudson.util.XStream2;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.jvnet.hudson.test.Bug;
import com.thoughtworks.xstream.XStream;
import org.jvnet.hudson.test.Issue;
import static org.junit.Assert.*;

/**
 * Tests the JUnit result XML file parsing in {@link TestResult}.
 *
 * @author dty
 */
public class TestResultTest {
  private File getDataFile(String name) throws URISyntaxException {
    return new File(TestResultTest.class.getResource(name).toURI());
  }

  /**
     * Verifies that all suites of an Eclipse Plug-in Test Suite are collected.
     * These suites don't differ by name (and timestamp), the y differ by 'id'.
     */
  @Test public void testIpsTests() throws Exception {
    TestResult testResult = new TestResult();
    testResult.parse(getDataFile("eclipse-plugin-test-report.xml"));
    Collection<SuiteResult> suites = testResult.getSuites();
    assertEquals("Wrong number of test suites", 16, suites.size());
    int testCaseCount = 0;
    for (SuiteResult suite : suites) {
      testCaseCount += suite.getCases().size();
    }
    assertEquals("Wrong number of test cases", 3366, testCaseCount);
  }

  /**
     * This test verifies compatibility of JUnit test results persisted to
     * XML prior to the test code refactoring.
     * 
     * @throws Exception
     */
  @Test public void testXmlCompatibility() throws Exception {
    XmlFile xmlFile = new XmlFile(XSTREAM, getDataFile("junitResult.xml"));
    TestResult result = (TestResult) xmlFile.read();
    result.tally();
    assertEquals(9, result.getTotalCount());
    assertEquals(1, result.getSkipCount());
    assertEquals(1, result.getFailCount());
    assertEquals(0.576, result.getDuration(), 0.0001);
    Collection<SuiteResult> suites = result.getSuites();
    assertEquals(6, suites.size());
    List<CaseResult> failedTests = result.getFailedTests();
    assertEquals(1, failedTests.size());
    SuiteResult failedSuite = result.getSuite("broken");
    assertNotNull(failedSuite);
    CaseResult failedCase = failedSuite.getCase("becomeUglier");
    assertNotNull(failedCase);
    assertFalse(failedCase.isSkipped());
    assertFalse(failedCase.isPassed());
    assertEquals(5, failedCase.getFailedSince());
  }

  /**
     * When test methods are parametrized, they can occur multiple times in the testresults XMLs.
     * Test that these are counted correctly.
     */
  @Issue(value = "JENKINS-13214") @Test public void testDuplicateTestMethods() throws IOException, URISyntaxException {
    TestResult testResult = new TestResult();
    testResult.parse(getDataFile("JENKINS-13214/27449.xml"));
    testResult.parse(getDataFile("JENKINS-13214/27540.xml"));
    testResult.parse(getDataFile("JENKINS-13214/29734.xml"));
    testResult.tally();
    assertEquals("Wrong number of test suites", 1, testResult.getSuites().size());
    assertEquals("Wrong number of test cases", 3, testResult.getTotalCount());
  }

  @Bug(value = 12457) public void testTestSuiteDistributedOverMultipleFilesIsCountedAsOne() throws IOException, URISyntaxException {
    TestResult testResult = new TestResult();
    testResult.parse(getDataFile("JENKINS-12457/TestSuite_a1.xml"));
    testResult.parse(getDataFile("JENKINS-12457/TestSuite_a2.xml"));
    testResult.tally();
    assertEquals("Wrong number of testsuites", 1, testResult.getSuites().size());
    assertEquals("Wrong number of test cases", 2, testResult.getTotalCount());
    assertEquals("Wrong duration for test result", 172.98, testResult.getDuration(), 0.1);
  }

  /**
     * A common problem is that people parse TEST-*.xml as well as TESTS-TestSuite.xml.
     * See http://jenkins.361315.n4.nabble.com/Problem-with-duplicate-build-execution-td371616.html for discussion.
     */
  public void testDuplicatedTestSuiteIsNotCounted() throws IOException, URISyntaxException {
    TestResult testResult = new TestResult();
    testResult.parse(getDataFile("JENKINS-12457/TestSuite_b.xml"));
    testResult.parse(getDataFile("JENKINS-12457/TestSuite_b_duplicate.xml"));
    testResult.tally();
    assertEquals("Wrong number of testsuites", 1, testResult.getSuites().size());
    assertEquals("Wrong number of test cases", 1, testResult.getTotalCount());
    assertEquals("Wrong duration for test result", 1.0, testResult.getDuration(), 0.01);
  }

  /**
     * Check result when test failed with both error and failure messages,
     * for example, with Error in teardown phase but with failure on the call
     */
  @Test public void testErrorAndFailMessages() throws Exception {
    TestResult testResult = new TestResult();
    testResult.parse(getDataFile("junit-report-error-failure-details.xml"));
    testResult.tally();
    assertEquals("Just one test failed", 1, testResult.getFailCount());
    CaseResult cr = testResult.getFailedTests().get(0);
    assertEquals("Wrong error stacktrace", "error message for test WhooHoo", cr.getErrorDetails());
    assertEquals("Wrong failure stacktrace", "failure message for test WhooHoo", cr.getFailureDetails());
  }

  @Issue(value = "JENKINS-41134") @Test public void testMerge() throws IOException, URISyntaxException {
    TestResult first = new TestResult();
    TestResult second = new TestResult();
    first.parse(getDataFile("JENKINS-41134/TestSuite_first.xml"));
    second.parse(getDataFile("JENKINS-41134/TestSuite_second.xml"));
    assertEquals("Fail count should be 0", 0, first.getFailCount());
    first.merge(second);
    assertEquals("Fail count should now be 1", 1, first.getFailCount());
    first = new TestResult();
    second = new TestResult();
    first.parse(getDataFile("JENKINS-41134/TestSuite_first.xml"));
    second.parse(getDataFile("JENKINS-41134/TestSuite_second_dup_first.xml"));
    assertEquals("Fail count should be 0", 0, first.getFailCount());
    first.merge(second);
    assertEquals("Fail count should now be 1", 1, first.getFailCount());
  }

  private static final XStream XSTREAM = new XStream2();

  static {
    XSTREAM.alias("result", TestResult.class);
    XSTREAM.alias("suite", SuiteResult.class);
    XSTREAM.alias("case", CaseResult.class);
    XSTREAM.registerConverter(new HeapSpaceStringConverter(), 100);
  }
}