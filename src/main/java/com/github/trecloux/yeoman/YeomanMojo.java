package com.github.trecloux.yeoman;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.File;
import java.io.IOException;

@Mojo(name = "build", defaultPhase = LifecyclePhase.PREPARE_PACKAGE) public class YeomanMojo extends AbstractMojo {
  @Parameter(defaultValue = "yo", required = true) File yeomanProjectDirectory;

  @Parameter(defaultValue = "${os.name}", readonly = true) String osName;

  @Parameter(property = "yo.build.skip", defaultValue = "false") boolean skipBuild;

  @Parameter(property = "yo.test.skip", defaultValue = "false") boolean skipTests;

  @Parameter(property = "yo.skip", defaultValue = "false") boolean skip;

  @Parameter(property = "npm.install.skip", defaultValue = "false") boolean skipNpmInstall;

  @Parameter(property = "bower.install.skip", defaultValue = "false") boolean skipBowerInstall;

  @Parameter(defaultValue = "false", required = false) boolean useNpmCache;

  @Parameter(defaultValue = "install", required = true) String npmInstallArgs;

  @Parameter(defaultValue = "bower", required = true) String bowerVariant;

  @Parameter(defaultValue = "install --no-color", required = true) String bowerInstallArgs;

  @Parameter(defaultValue = "grunt", required = true) String buildTool;

  @Parameter(defaultValue = "test --no-color", required = true) String testArgs;

  @Parameter(defaultValue = "build --no-color", required = true) String buildArgs;

  @Deprecated @Parameter(required = false) String gruntTestArgs;

  @Deprecated @Parameter(required = false) String gruntBuildArgs;

  public void execute() throws MojoExecutionException {
    handleDeprecatedParameters();
    if (skip) {
      getLog().info("Skipping Yeoman Execution");
    } else {
      npmInstall();
      bowerInstall();
      build();
    }
  }

  private void handleDeprecatedParameters() {
    if (gruntTestArgs != null) {
      testArgs = gruntTestArgs;
    }
    if (gruntBuildArgs != null) {
      buildArgs = gruntBuildArgs;
    }
  }

  void npmInstall() throws MojoExecutionException {
    if (skipNpmInstall) {
      getLog().info("Skipping \'npm install\' Execution");
    } else {
      logToolVersion("node");
      logToolVersion("npm");
      logAndExecuteCommand(getNpmExecutor() + npmInstallArgs);
    }

<<<<<<< Unknown file: This is a bug in JDime.
=======
    logAndExecuteCommand(getNpmExecutor() + npmInstallArgs);
>>>>>>> /usr/src/app/output/trecloux/yeoman-maven-plugin/5f3e6016a840a88e18f554d79d9d94de21a070e9/src/main/java/com/github/trecloux/yeoman/YeomanMojo.java/right.java
  }

  void bowerInstall() throws MojoExecutionException {
    if (skipBowerInstall) {
      getLog().info("Skipping \'bower install\' Execution");
    } else {
      logToolVersion(bowerVariant);
      logAndExecuteCommand(bowerVariant + " " + bowerInstallArgs);
    }
  }

  void build() throws MojoExecutionException {
    logToolVersion(buildTool);
    if (!skipTests) {
      logAndExecuteCommand(buildTool + " " + testArgs);
    }
    if (!skipBuild) {
      logAndExecuteCommand(buildTool + " " + buildArgs);
    }
  }

  void logToolVersion(final String toolName) throws MojoExecutionException {
    getLog().info(toolName + " version :");
    executeCommand(toolName + " --version");
  }

  void logAndExecuteCommand(String command) throws MojoExecutionException {
    logCommand(command);
    executeCommand(command);
  }

  void logCommand(String command) {
    getLog().info("--------------------------------------");
    getLog().info("         " + command.toUpperCase());
    getLog().info("--------------------------------------");
  }

  void executeCommand(String command) throws MojoExecutionException {
    try {
      if (isWindows()) {
        command = "cmd /c " + command;
      }
      CommandLine cmdLine = CommandLine.parse(command);
      DefaultExecutor executor = new DefaultExecutor();
      executor.setWorkingDirectory(yeomanProjectDirectory);
      executor.execute(cmdLine);
    } catch (IOException e) {
      throw new MojoExecutionException("Error during : " + command, e);
    }
  }

  private String getNpmExecutor() {
    String npmExecutor = "npm ";
    if (useNpmCache) {
      npmExecutor = "npm-cache ";
    }
    return npmExecutor;
  }

  private boolean isWindows() {
    return osName.startsWith("Windows");
  }
}