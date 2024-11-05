package com.amashchenko.maven.plugin.gitflow;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.release.versions.VersionParseException;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

@Mojo(name = "feature-start", aggregator = true) public class GitFlowFeatureStartMojo extends AbstractGitFlowMojo {
  @Parameter(property = "skipFeatureVersion", defaultValue = "false") private boolean skipFeatureVersion = false;

  @Parameter private String featureNamePattern;

  @Parameter(property = "pushRemote", defaultValue = "false") private boolean pushRemote;

  @Parameter(property = "featureName") private String featureName;

  @Override public void execute() throws MojoExecutionException, MojoFailureException {
    validateConfiguration();
    try {
      initGitFlowConfig();
      checkUncommittedChanges();
      if (fetchRemote) {
        gitFetchRemoteAndCreate(gitFlowConfig.getDevelopmentBranch());
        gitFetchRemoteAndCompare(gitFlowConfig.getDevelopmentBranch());
      }
      String featureBranchName = null;
      if (settings.isInteractiveMode()) {
        try {
          while (StringUtils.isBlank(featureBranchName)) {
            featureBranchName = prompter.prompt("What is a name of feature branch? " + gitFlowConfig.getFeatureBranchPrefix());
            if (!validateBranchName(featureBranchName, featureNamePattern, false)) {
              featureBranchName = null;
            }
          }
        } catch (PrompterException e) {
          throw new MojoFailureException("feature-start", e);
        }
      } else {
        validateBranchName(featureName, featureNamePattern, true);
        featureBranchName = featureName;
      }
      if (StringUtils.isBlank(featureBranchName)) {
        throw new MojoFailureException("Feature name is blank.");
      }
      featureBranchName = StringUtils.deleteWhitespace(featureBranchName);
      final boolean featureBranchExists = gitCheckBranchExists(gitFlowConfig.getFeatureBranchPrefix() + featureBranchName);
      if (featureBranchExists) {
        throw new MojoFailureException("Feature branch with that name already exists. Cannot start feature.");
      }
      gitCreateAndCheckout(gitFlowConfig.getFeatureBranchPrefix() + featureBranchName, gitFlowConfig.getDevelopmentBranch());
      if (!skipFeatureVersion && !tychoBuild) {
        final String currentVersion = getCurrentProjectVersion();
        final String version = new GitFlowVersionInfo(currentVersion, getVersionPolicy()).featureVersion(featureBranchName);
        if (StringUtils.isNotBlank(version)) {
          mvnSetVersions(version);
          Map<String, String> properties = new HashMap<>();
          properties.put("version", version);
          properties.put("featureName", featureBranchName);
          gitCommit(commitMessages.getFeatureStartMessage(), properties);
        }
      }
      if (installProject) {
        mvnCleanInstall();
      }
      if (pushRemote) {
        gitPush(gitFlowConfig.getFeatureBranchPrefix() + featureBranchName, false);
      }
    } catch (CommandLineException | VersionParseException e) {
      throw new MojoFailureException("feature-start", e);
    }
  }

  private boolean validateBranchName(String name, String pattern, boolean failOnError) throws MojoFailureException, CommandLineException {
    boolean valid = true;
    if (StringUtils.isNotBlank(name) && validBranchName(name)) {
      if (StringUtils.isNotBlank(pattern) && !name.matches(pattern)) {
        final String error = "The name of the branch doesn\'t match \'" + pattern + "\' pattern.";
        getLog().warn(error);
        valid = false;
        if (failOnError) {
          throw new MojoFailureException(error);
        }
      }
    } else {
      final String error = "The name of the branch is not valid or blank.";
      getLog().warn(error);
      valid = false;
      if (failOnError) {
        throw new MojoFailureException(error);
      }
    }
    return valid;
  }
}