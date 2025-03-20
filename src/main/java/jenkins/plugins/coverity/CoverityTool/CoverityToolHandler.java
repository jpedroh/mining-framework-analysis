package jenkins.plugins.coverity.CoverityTool;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Executor;
import hudson.model.Node;
import hudson.model.Result;
import jenkins.plugins.coverity.CIMInstance;
import jenkins.plugins.coverity.CIMStream;
import jenkins.plugins.coverity.CoverityLauncherDecorator;
import jenkins.plugins.coverity.CoverityPublisher;
import jenkins.plugins.coverity.CoverityTempDir;
import jenkins.plugins.coverity.CoverityToolInstallation;
import jenkins.plugins.coverity.CoverityUtils;
import jenkins.plugins.coverity.InvocationAssistance;
import jenkins.plugins.coverity.ws.DefectReader;

/**
 * CoverityToolHandler handles the actual executing of Coverity executables.
 */
public class CoverityToolHandler {
  public void perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, CoverityPublisher publisher) throws Exception {
    EnvVars envVars = build.getEnvironment(listener);
    CoverityTempDir temp = build.getAction(CoverityTempDir.class);
    Node node = Executor.currentExecutor().getOwner().getNode();
    CoverityToolInstallation installation = CoverityUtils.findToolInstallationForBuild(node, build.getEnvironment(listener), listener);
    String home = installation != null ? installation.getHome() : null;
    CoverityUtils.checkDir(launcher.getChannel(), home);
    listener.getLogger().println("[Coverity] Tools installation \'" + installation.getName() + "\' with directory \'" + installation.getHome() + "\'");
    CIMStream cimStream = publisher.getCimStream();
    CIMInstance cim = publisher.getDescriptor().getInstance(publisher);
    boolean useAdvancedParser = false;
    InvocationAssistance invocationAssistance = publisher.getInvocationAssistance();
    if (invocationAssistance != null && invocationAssistance.getUseAdvancedParser()) {
      useAdvancedParser = true;
    }
    if (!envVars.containsKey("COV_IDIR")) {
      if (temp != null) {
        envVars.put("COV_IDIR", temp.getTempDir().getRemote());
      }
    }
    if (!envVars.containsKey("COV_ANALYSIS_ROOT")) {
      envVars.put("COV_ANALYSIS_ROOT", home);
    }
    try {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(true);
      Command covBuildScriptCommand = new CovBuildScriptCommand(build, launcher, listener, publisher, home, envVars);
      int result = covBuildScriptCommand.runCommand();
      if (result != 0) {
        listener.getLogger().println("[Coverity] cov-build returned " + result + ", aborting...");
        build.setResult(Result.FAILURE);
        return;
      }
    }  finally {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(false);
    }
    try {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(true);
      Command postCovBuildCommand = new PostCovBuildCommand(build, launcher, listener, publisher, envVars);
      int result = postCovBuildCommand.runCommand();
      if (result != 0) {
        listener.getLogger().println("[Coverity] post cov-build command returned " + result + ", aborting...");
        build.setResult(Result.FAILURE);
        return;
      }
    }  finally {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(false);
    }
    try {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(true);
      Command covEmitJavaCommand = new CovEmitJavaCommand(build, launcher, listener, publisher, home, envVars, useAdvancedParser);
      int result = covEmitJavaCommand.runCommand();
      if (result != 0) {
        listener.getLogger().println("[Coverity] cov-emit-java returned " + result + ", aborting...");
        build.setResult(Result.FAILURE);
        return;
      }
    }  finally {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(false);
    }
    try {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(true);
      Command covCaptureCommand = new CovCaptureCommand(build, launcher, listener, publisher, home, envVars);
      int result = covCaptureCommand.runCommand();
      if (result != 0) {
        listener.getLogger().println("[Coverity] cov-capture returned " + result + ", aborting...");
        build.setResult(Result.FAILURE);
        return;
      }
    }  finally {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(false);
    }
    try {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(true);
      Command covManageHistoryCommand = new CovManageHistoryCommand(build, launcher, listener, publisher, home, envVars, cimStream, cim);
      int result = covManageHistoryCommand.runCommand();
      if (result != 0) {
        listener.getLogger().println("[Coverity] cov-manage-history returned " + result + ", aborting...");
        build.setResult(Result.FAILURE);
        return;
      }
    }  finally {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(false);
    }
    try {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(true);
      Command covImportScmCommand = new CovImportScmCommand(build, launcher, listener, publisher, home, envVars);
      int result = covImportScmCommand.runCommand();
      if (result != 0) {
        listener.getLogger().println("[Coverity] cov-import-scm returned " + result + ", aborting...");
        build.setResult(Result.FAILURE);
        return;
      }
    }  finally {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(false);
    }
    try {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(true);
      Command covAnalyzeCommand = new CovAnalyzeCommand(build, launcher, listener, publisher, home, envVars);
      int result = covAnalyzeCommand.runCommand();
      if (result != 0) {
        listener.getLogger().println("[Coverity] cov-analyze returned " + result + ", aborting...");
        build.setResult(Result.FAILURE);
        return;
      }
    }  finally {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(false);
    }
    try {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(true);
      Command postCovAnalyzeCommand = new PostCovAnalyzeCommand(build, launcher, listener, publisher, envVars);
      int result = postCovAnalyzeCommand.runCommand();
      if (result != 0) {
        listener.getLogger().println("[Coverity] post cov-analyze command returned " + result + ", aborting...");
        build.setResult(Result.FAILURE);
        return;
      }
    }  finally {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(false);
    }
    try {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(true);
      Command covImportMsvscaCommand = new CovImportMsvscaCommand(build, launcher, listener, publisher, home, envVars, build.getWorkspace());
      int result = covImportMsvscaCommand.runCommand();
      if (result != 0) {
        listener.getLogger().println("[Coverity] cov-import-msvsca returned " + result + ", aborting...");
        build.setResult(Result.FAILURE);
        return;
      }
    }  finally {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(false);
    }
    try {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(true);
      Command covCommitDefectsCommand = new CovCommitDefectsCommand(build, launcher, listener, publisher, home, envVars, cimStream, cim);
      int result = covCommitDefectsCommand.runCommand();
      if (result != 0) {
        listener.getLogger().println("[Coverity] cov-commit-defects returned " + result + ", aborting...");
        build.setResult(Result.FAILURE);
        return;
      }
    }  finally {
      CoverityLauncherDecorator.CoverityPostBuildAction.set(false);
    }
    if (!publisher.isSkipFetchingDefects()) {
      DefectReader defectReader = new DefectReader(build, listener, publisher);
      defectReader.getLatestDefectsForBuild();
    }
  }
}