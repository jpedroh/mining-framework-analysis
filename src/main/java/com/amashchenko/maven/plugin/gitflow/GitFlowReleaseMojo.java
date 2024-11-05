package com.amashchenko.maven.plugin.gitflow;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.StringUtils;

@Mojo(name = "release", aggregator = true) public class GitFlowReleaseMojo extends AbstractGitFlowMojo {
  @Parameter(property = "skipTag", defaultValue = "false") private boolean skipTag = false;

  @Parameter(property = "skipTestProject", defaultValue = "false") private boolean skipTestProject = false;

  @Parameter(property = "allowSnapshots", defaultValue = "false") private boolean allowSnapshots = false;

  @Parameter(property = "releaseRebase", defaultValue = "false") private boolean releaseRebase = false;

  @Parameter(property = "releaseMergeNoFF", defaultValue = "true") private boolean releaseMergeNoFF = true;

  @Parameter(property = "pushRemote", defaultValue = "true") private boolean pushRemote;

  @Parameter(property = "releaseVersion", defaultValue = "") private String releaseVersion = "";

  @Parameter(property = "releaseMergeFFOnly", defaultValue = "false") private boolean releaseMergeFFOnly = false;

  @Parameter(property = "digitsOnlyDevVersion", defaultValue = "false") private boolean digitsOnlyDevVersion = false;

  @Parameter(property = "developmentVersion", defaultValue = "") private String developmentVersion = "";

  @Parameter(property = "versionDigitToIncrement") private Integer versionDigitToIncrement;

  @Parameter(property = "preReleaseGoals") private String preReleaseGoals;

  @Parameter(property = "postReleaseGoals") private String postReleaseGoals;

  @Parameter(property = "gpgSignTag", defaultValue = "false") private boolean gpgSignTag = false;

  @Override public void execute() throws MojoExecutionException, MojoFailureException {
    validateConfiguration(preReleaseGoals, postReleaseGoals);
    try {
      initGitFlowConfig();
      checkUncommittedChanges();
      final String releaseBranch = gitFindBranches(gitFlowConfig.getReleaseBranchPrefix(), true);
      if (StringUtils.isNotBlank(releaseBranch)) {
        throw new MojoFailureException("Release branch already exists. Cannot start release.");
      }
      if (fetchRemote) {
        gitFetchRemoteAndCreate(gitFlowConfig.getDevelopmentBranch());
        gitFetchRemoteAndCompare(gitFlowConfig.getDevelopmentBranch());
        if (notSameProdDevName()) {
          gitFetchRemoteAndCreate(gitFlowConfig.getProductionBranch());
          gitFetchRemoteAndCompare(gitFlowConfig.getProductionBranch());
        }
      }
      gitCheckout(gitFlowConfig.getDevelopmentBranch());
      if (!allowSnapshots) {
        checkSnapshotDependencies();
      }
      if (!skipTestProject) {
        mvnCleanTest();
      }
      final String currentVersion = getCurrentProjectVersion();
      String defaultVersion = null;
      if (tychoBuild) {
        defaultVersion = currentVersion;
      } else {
        defaultVersion = new GitFlowVersionInfo(currentVersion, getVersionPolicy()).getReleaseVersionString();
      }
      if (defaultVersion == null) {
        throw new MojoFailureException("Cannot get default project version.");
      }
      String version = null;
      if (settings.isInteractiveMode()) {
        try {
          while (version == null) {
            version = prompter.prompt("What is release version? [" + defaultVersion + "]");
            if (!"".equals(version) && (!GitFlowVersionInfo.isValidVersion(version) || !validBranchName(version))) {
              getLog().info("The version is not valid.");
              version = null;
            }
          }
        } catch (PrompterException e) {
          throw new MojoFailureException("release", e);
        }
      } else {
        version = releaseVersion;
      }
      if (StringUtils.isBlank(version)) {
        getLog().info("Version is blank. Using default version.");
        version = defaultVersion;
      }
      if (StringUtils.isNotBlank(preReleaseGoals)) {
        mvnRun(preReleaseGoals);
      }
      Map<String, String> messageProperties = new HashMap<>();
      messageProperties.put("version", version);
      if (!version.equals(currentVersion)) {
        mvnSetVersions(version);
        gitCommit(commitMessages.getReleaseStartMessage(), messageProperties);
      }
      if (!skipReleaseMergeProdBranch && notSameProdDevName()) {
        gitCheckout(gitFlowConfig.getProductionBranch());
        gitMerge(gitFlowConfig.getDevelopmentBranch(), releaseRebase, releaseMergeNoFF, releaseMergeFFOnly, commitMessages.getReleaseFinishMergeMessage(), messageProperties);
      }
      if (!skipTag) {
        if (tychoBuild && ArtifactUtils.isSnapshot(version)) {
          version = version.replace("-" + Artifact.SNAPSHOT_VERSION, "");
        }
        messageProperties.put("version", version);
        gitTag(gitFlowConfig.getVersionTagPrefix() + version, commitMessages.getTagReleaseMessage(), gpgSignTag, messageProperties);
      }
      if (StringUtils.isNotBlank(postReleaseGoals)) {
        mvnRun(postReleaseGoals);
      }
      if (notSameProdDevName()) {
        gitCheckout(gitFlowConfig.getDevelopmentBranch());
      }
      final String nextSnapshotVersion;
      if (!settings.isInteractiveMode() && StringUtils.isNotBlank(developmentVersion)) {
        nextSnapshotVersion = developmentVersion;
      } else {
        GitFlowVersionInfo versionInfo = new GitFlowVersionInfo(version, getVersionPolicy());
        if (digitsOnlyDevVersion) {
          versionInfo = versionInfo.digitsVersionInfo();
        }
        nextSnapshotVersion = versionInfo.nextSnapshotVersion(versionDigitToIncrement);
      }
      if (StringUtils.isBlank(nextSnapshotVersion)) {
        throw new MojoFailureException("Next snapshot version is blank.");
      }
      mvnSetVersions(nextSnapshotVersion);
      messageProperties.put("version", nextSnapshotVersion);
      gitCommit(commitMessages.getReleaseFinishMessage(), messageProperties);
      if (installProject) {
        mvnCleanInstall();
      }
      if (pushRemote) {
        gitPush(gitFlowConfig.getProductionBranch(), !skipTag);
        if (notSameProdDevName()) {
          gitPush(gitFlowConfig.getDevelopmentBranch(), !skipTag);
        }
      }
    } catch (Exception e) {
      throw new MojoFailureException("release", e);
    }
  }

  @Parameter(property = "skipReleaseMergeProdBranch", defaultValue = "false") private boolean skipReleaseMergeProdBranch = false;
}