package jenkins.plugins.coverity.CoverityTool;
import jenkins.plugins.coverity.CoverityPublisher;
import jenkins.plugins.coverity.TaOptionBlock;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.assertEquals;

public class CovCaptureCommandTest extends CommandTestBase {
  @Test public void addTAdvisorConfigurationTest() throws IOException, InterruptedException {
    TaOptionBlock taOptionBlock = new TaOptionBlock(StringUtils.EMPTY, false, false, true, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, "Jacoco", true, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, taOptionBlock, null);
    ICommand covCaptureCommand = new CovCaptureCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    setExpectedArguments(new String[] { "cov-capture", "--dir", "TestDir", "--java-coverage", "Jacoco", "--java-test", "junit" });
    covCaptureCommand.runCommand();
    consoleLogger.verifyLastMessage("[Coverity] cov-capture command line arguments: " + actualArguments.toString());
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void CovCaptureCommand_TestAdvisorConfigurationTest() {
    mocker.replay();
    TaOptionBlock taOptionBlock = new TaOptionBlock(StringUtils.EMPTY, false, false, true, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, "Jacoco", true, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, taOptionBlock, null);
    CovCommand covCaptureCommand = new CovCaptureCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    List<String> covCaptureArguments = covCaptureCommand.getCommandLines();
    assertEquals(7, covCaptureArguments.size());
    checkCommandLineArg(covCaptureArguments, "cov-capture");
    checkCommandLineArg(covCaptureArguments, "--dir");
    checkCommandLineArg(covCaptureArguments, "TestDir");
    checkCommandLineArg(covCaptureArguments, "--java-coverage");
    checkCommandLineArg(covCaptureArguments, "Jacoco");
    checkCommandLineArg(covCaptureArguments, "--java-test");
    checkCommandLineArg(covCaptureArguments, "junit");
    assertEquals(0, covCaptureArguments.size());
  }
>>>>>>> /usr/src/app/output/jenkinsci/coverity-plugin/5846a9f06a151725dff014e3a580a9349a20e8ec/src/test/java/jenkins/plugins/coverity/CoverityTool/CovCaptureCommandTest.java/right.java


  @Test public void customTestCommandTest() throws IOException, InterruptedException {
    TaOptionBlock taOptionBlock = new TaOptionBlock("CustomTestCommand", false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, taOptionBlock, null);
    ICommand covCaptureCommand = new CovCaptureCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    setExpectedArguments(new String[] { "cov-capture", "--dir", "TestDir", "CustomTestCommand" });
    covCaptureCommand.runCommand();
    consoleLogger.verifyLastMessage("[Coverity] cov-capture command line arguments: " + actualArguments.toString());
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void CovCaptureCommand_CustomTestCommandTest() {
    mocker.replay();
    TaOptionBlock taOptionBlock = new TaOptionBlock("CustomTestCommand", false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, taOptionBlock, null);
    CovCommand covCaptureCommand = new CovCaptureCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    List<String> covCaptureArguments = covCaptureCommand.getCommandLines();
    assertEquals(4, covCaptureArguments.size());
    checkCommandLineArg(covCaptureArguments, "cov-capture");
    checkCommandLineArg(covCaptureArguments, "--dir");
    checkCommandLineArg(covCaptureArguments, "TestDir");
    checkCommandLineArg(covCaptureArguments, "CustomTestCommand");
    assertEquals(0, covCaptureArguments.size());
  }
>>>>>>> /usr/src/app/output/jenkinsci/coverity-plugin/5846a9f06a151725dff014e3a580a9349a20e8ec/src/test/java/jenkins/plugins/coverity/CoverityTool/CovCaptureCommandTest.java/right.java


  @Test public void customTestCommandTest_WithParseException() throws IOException, InterruptedException {
    TaOptionBlock taOptionBlock = new TaOptionBlock("\'", false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, taOptionBlock, null);
    ICommand covCaptureCommand = new CovCaptureCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    try {
      covCaptureCommand.runCommand();
      Assert.fail("RuntimeException should have been thrown");
    } catch (RuntimeException e) {
      assertEquals("ParseException occurred during tokenizing the cov capture custom test command.", e.getMessage());
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void CovCaptureCommand_CustomTestCommandTest_WithParseException() {
    mocker.replay();
    TaOptionBlock taOptionBlock = new TaOptionBlock("\'", false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, taOptionBlock, null);
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("ParseException occurred during tokenizing the cov capture custom test command.");
    CovCommand covCaptureCommand = new CovCaptureCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
  }
>>>>>>> /usr/src/app/output/jenkinsci/coverity-plugin/5846a9f06a151725dff014e3a580a9349a20e8ec/src/test/java/jenkins/plugins/coverity/CoverityTool/CovCaptureCommandTest.java/right.java


  @Test public void cannotExecuteTest() throws IOException, InterruptedException {
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, null, null);
    ICommand covCaptureCommand = new CovCaptureCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    covCaptureCommand.runCommand();
    consoleLogger.verifyLastMessage("[Coverity] Skipping command because it can\'t be executed");
  }
}