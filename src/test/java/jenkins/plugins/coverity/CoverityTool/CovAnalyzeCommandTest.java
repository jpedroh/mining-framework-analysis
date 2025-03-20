package jenkins.plugins.coverity.CoverityTool;
import jenkins.plugins.coverity.CoverityPublisher;
import jenkins.plugins.coverity.InvocationAssistance;
import jenkins.plugins.coverity.TaOptionBlock;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertEquals;

public class CovAnalyzeCommandTest extends CommandTestBase {
  @Test public void addMisraConfigurationTest() throws IOException, InterruptedException {
    File misraConfigFile = new File("misraConfigFile");
    try {
      misraConfigFile.createNewFile();
      InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, true, misraConfigFile.getPath(), StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
      CoverityPublisher publisher = new CoverityPublisher(null, invocationAssistance, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, null, null);
      ICommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
      setExpectedArguments(new String[] { "cov-analyze", "--dir", "TestDir", "--misra-config", misraConfigFile.getPath() });
      covAnalyzeCommand.runCommand();
      consoleLogger.verifyLastMessage("[Coverity] cov-analyze command line arguments: " + actualArguments.toString());
    }  finally {
      misraConfigFile.delete();
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void CovAnalyzeCommand_MisraConfigurationTest() throws IOException {
    mocker.replay();
    File misraConfigFile = new File("misraConfigFile");
    misraConfigFile.createNewFile();
    InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, true, misraConfigFile.getPath(), StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
    CoverityPublisher publisher = new CoverityPublisher(null, invocationAssistance, false, false, false, false, false, null, null);
    CovCommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    List<String> covAnalyzeArguments = covAnalyzeCommand.getCommandLines();
    assertEquals(5, covAnalyzeArguments.size());
    checkCommandLineArg(covAnalyzeArguments, "cov-analyze");
    checkCommandLineArg(covAnalyzeArguments, "--dir");
    checkCommandLineArg(covAnalyzeArguments, "TestDir");
    checkCommandLineArg(covAnalyzeArguments, "--misra-config");
    checkCommandLineArg(covAnalyzeArguments, misraConfigFile.getPath());
    assertEquals(0, covAnalyzeArguments.size());
    misraConfigFile.delete();
  }
>>>>>>> /usr/src/app/output/jenkinsci/coverity-plugin/5846a9f06a151725dff014e3a580a9349a20e8ec/src/test/java/jenkins/plugins/coverity/CoverityTool/CovAnalyzeCommandTest.java/right.java


  @Test public void addMisraConfigurationTest_WithEmptyMisraConfigFilePath() throws IOException, InterruptedException {
    InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, true, StringUtils.EMPTY, StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
    CoverityPublisher publisher = new CoverityPublisher(null, invocationAssistance, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, null, null);
    ICommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    try {
      covAnalyzeCommand.runCommand();
      Assert.fail("RuntimeException should have been thrown");
    } catch (RuntimeException e) {
      assertEquals("Misra configuration file is required to run Misra analysis.", e.getMessage());
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void CovAnalyzeCommand_MisraConfigurationTest_WithEmptyMisraConfigFilePath() {
    mocker.replay();
    InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, true, StringUtils.EMPTY, StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
    CoverityPublisher publisher = new CoverityPublisher(null, invocationAssistance, false, false, false, false, false, null, null);
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("Misra configuration file is required to run Misra analysis.");
    CovCommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
  }
>>>>>>> /usr/src/app/output/jenkinsci/coverity-plugin/5846a9f06a151725dff014e3a580a9349a20e8ec/src/test/java/jenkins/plugins/coverity/CoverityTool/CovAnalyzeCommandTest.java/right.java


  @Test public void addMisraConfigurationTest_WithInvalidMisraConfigFilePath() throws IOException, InterruptedException {
    File misraConfigFile = new File("misraConfigFile");
    InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, true, misraConfigFile.getPath(), StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
    CoverityPublisher publisher = new CoverityPublisher(null, invocationAssistance, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, null, null);
    ICommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    try {
      covAnalyzeCommand.runCommand();
      Assert.fail("RuntimeException should have been thrown");
    } catch (RuntimeException e) {
      assertEquals("Could not find MISRA configuration file at \"" + misraConfigFile.getAbsolutePath() + "\"", e.getMessage());
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void CovAnalyzeCommand_MisraConfigurationTest_WithInvalidMisraConfigFilePath() {
    mocker.replay();
    File misraConfigFile = new File("misraConfigFile");
    InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, true, misraConfigFile.getPath(), StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
    CoverityPublisher publisher = new CoverityPublisher(null, invocationAssistance, false, false, false, false, false, null, null);
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("Could not find MISRA configuration file at \"" + misraConfigFile.getAbsolutePath() + "\"");
    CovCommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
  }
>>>>>>> /usr/src/app/output/jenkinsci/coverity-plugin/5846a9f06a151725dff014e3a580a9349a20e8ec/src/test/java/jenkins/plugins/coverity/CoverityTool/CovAnalyzeCommandTest.java/right.java


  @Test public void additionalArgumentsTest() throws IOException, InterruptedException {
    InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, "additionalArgs", StringUtils.EMPTY, StringUtils.EMPTY, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
    CoverityPublisher publisher = new CoverityPublisher(null, invocationAssistance, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, null, null);
    ICommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    setExpectedArguments(new String[] { "cov-analyze", "--dir", "TestDir", "additionalArgs" });
    covAnalyzeCommand.runCommand();
    consoleLogger.verifyLastMessage("[Coverity] cov-analyze command line arguments: " + actualArguments.toString());
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void CovAnalyzeCommand_AdditionalArgumentsTest() {
    mocker.replay();
    InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, "additionalArgs", StringUtils.EMPTY, StringUtils.EMPTY, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
    CoverityPublisher publisher = new CoverityPublisher(null, invocationAssistance, false, false, false, false, false, null, null);
    CovCommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    List<String> covAnalyzeArguments = covAnalyzeCommand.getCommandLines();
    assertEquals(4, covAnalyzeArguments.size());
    checkCommandLineArg(covAnalyzeArguments, "cov-analyze");
    checkCommandLineArg(covAnalyzeArguments, "--dir");
    checkCommandLineArg(covAnalyzeArguments, "TestDir");
    checkCommandLineArg(covAnalyzeArguments, "additionalArgs");
    assertEquals(0, covAnalyzeArguments.size());
  }
>>>>>>> /usr/src/app/output/jenkinsci/coverity-plugin/5846a9f06a151725dff014e3a580a9349a20e8ec/src/test/java/jenkins/plugins/coverity/CoverityTool/CovAnalyzeCommandTest.java/right.java


  @Test public void additionalArgumentsTest_WithParseException() throws IOException, InterruptedException {
    InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, "\'", StringUtils.EMPTY, StringUtils.EMPTY, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
    CoverityPublisher publisher = new CoverityPublisher(null, invocationAssistance, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, null, null);
    ICommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    try {
      covAnalyzeCommand.runCommand();
      Assert.fail("RuntimeException should have been thrown");
    } catch (RuntimeException e) {
      assertEquals("ParseException occurred during tokenizing the cov analyze additional arguments.", e.getMessage());
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void CovAnalyzeCommand_AdditionalArgumentsTest_WithParseException() {
    mocker.replay();
    InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, "\'", StringUtils.EMPTY, StringUtils.EMPTY, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
    CoverityPublisher publisher = new CoverityPublisher(null, invocationAssistance, false, false, false, false, false, null, null);
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("ParseException occurred during tokenizing the cov analyze additional arguments.");
    new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
  }
>>>>>>> /usr/src/app/output/jenkinsci/coverity-plugin/5846a9f06a151725dff014e3a580a9349a20e8ec/src/test/java/jenkins/plugins/coverity/CoverityTool/CovAnalyzeCommandTest.java/right.java


  @Test public void addTestAdvisorConfigurationTest_WithEmptyStripPath() throws IOException, InterruptedException {
    File taPolicyFile = new File("taPolicyFile");
    try {
      taPolicyFile.createNewFile();
      TaOptionBlock taOptionBlock = new TaOptionBlock(StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, taPolicyFile.getPath(), "Path2Strip", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
      CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, taOptionBlock, null);
      ICommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
      setExpectedArguments(new String[] { "cov-analyze", "--dir", "TestDir", "--test-advisor", "--test-advisor-policy", taPolicyFile.getPath(), "--strip-path", "Path2Strip" });
      covAnalyzeCommand.runCommand();
      consoleLogger.verifyLastMessage("[Coverity] cov-analyze command line arguments: " + actualArguments.toString());
    }  finally {
      taPolicyFile.delete();
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void CovAnalyzeCommand_TestAdvisorConfigurationTest_WithEmptyStripPath() throws IOException {
    mocker.replay();
    File taPolicyFile = new File("taPolicyFile");
    taPolicyFile.createNewFile();
    TaOptionBlock taOptionBlock = new TaOptionBlock(StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, taPolicyFile.getPath(), "Path2Strip", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, taOptionBlock, null);
    CovCommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    List<String> covAnalyzeArguments = covAnalyzeCommand.getCommandLines();
    assertEquals(8, covAnalyzeArguments.size());
    checkCommandLineArg(covAnalyzeArguments, "cov-analyze");
    checkCommandLineArg(covAnalyzeArguments, "--dir");
    checkCommandLineArg(covAnalyzeArguments, "TestDir");
    checkCommandLineArg(covAnalyzeArguments, "--test-advisor");
    checkCommandLineArg(covAnalyzeArguments, "--test-advisor-policy");
    checkCommandLineArg(covAnalyzeArguments, taPolicyFile.getPath());
    checkCommandLineArg(covAnalyzeArguments, "--strip-path");
    checkCommandLineArg(covAnalyzeArguments, "Path2Strip");
    assertEquals(0, covAnalyzeArguments.size());
    taPolicyFile.delete();
  }
>>>>>>> /usr/src/app/output/jenkinsci/coverity-plugin/5846a9f06a151725dff014e3a580a9349a20e8ec/src/test/java/jenkins/plugins/coverity/CoverityTool/CovAnalyzeCommandTest.java/right.java


  @Test public void addTestAdvisorConfigurationTest_WithEmptyTaPolicyFilePath() throws IOException, InterruptedException {
    TaOptionBlock taOptionBlock = new TaOptionBlock(StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, taOptionBlock, null);
    ICommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    try {
      covAnalyzeCommand.runCommand();
      Assert.fail("RuntimeException should have been thrown");
    } catch (RuntimeException e) {
      assertEquals("Test Advisor Policy File is required to run the Test Advisor.", e.getMessage());
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void CovAnalyzeCommand_TestAdvisorConfigurationTest_WithEmptyTaPolicyFilePath() {
    mocker.replay();
    TaOptionBlock taOptionBlock = new TaOptionBlock(StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, taOptionBlock, null);
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("Test Advisor Policy File is required to run the Test Advisor.");
    CovCommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
  }
>>>>>>> /usr/src/app/output/jenkinsci/coverity-plugin/5846a9f06a151725dff014e3a580a9349a20e8ec/src/test/java/jenkins/plugins/coverity/CoverityTool/CovAnalyzeCommandTest.java/right.java


  @Test public void addTestAdvisorConfigurationTest_WithInvalidTaPolicyFilePath() throws IOException, InterruptedException {
    File taPolicyFile = new File("taPolicyFile");
    TaOptionBlock taOptionBlock = new TaOptionBlock(StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, taPolicyFile.getPath(), "Path2Strip", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, taOptionBlock, null);
    ICommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    try {
      covAnalyzeCommand.runCommand();
      Assert.fail("RuntimeException should have been thrown");
    } catch (RuntimeException e) {
      assertEquals("Could not find test policy file at \"" + taPolicyFile.getAbsolutePath() + "\"", e.getMessage());
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void CovAnalyzeCommand_TestAdvisorConfigurationTest_WithInvalidTaPolicyFilePath() {
    mocker.replay();
    File taPolicyFile = new File("taPolicyFile");
    TaOptionBlock taOptionBlock = new TaOptionBlock(StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, taPolicyFile.getPath(), "Path2Strip", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false);
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, taOptionBlock, null);
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("Could not find test policy file at \"" + taPolicyFile.getAbsolutePath() + "\"");
    CovCommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
  }
>>>>>>> /usr/src/app/output/jenkinsci/coverity-plugin/5846a9f06a151725dff014e3a580a9349a20e8ec/src/test/java/jenkins/plugins/coverity/CoverityTool/CovAnalyzeCommandTest.java/right.java


  @Test public void cannotExecuteTest() throws IOException, InterruptedException {
    CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, null, null);
    ICommand covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars);
    covAnalyzeCommand.runCommand();
    consoleLogger.verifyLastMessage("[Coverity] Skipping command because it can\'t be executed");
  }
}