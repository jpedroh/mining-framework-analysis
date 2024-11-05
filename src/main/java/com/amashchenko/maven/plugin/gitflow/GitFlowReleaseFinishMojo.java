package com.amashchenko.maven.plugin.gitflow;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

@Mojo(name = "release-finish", aggregator = true) public class GitFlowReleaseFinishMojo extends AbstractGitFlowMojo {
  @Parameter(property = "skipTag", defaultValue = "false") private boolean skipTag = false;

  @Parameter(property = "keepBranch", defaultValue = "false") private boolean keepBranch = false;

  @Parameter(property = "skipTestProject", defaultValue = "false") private boolean skipTestProject = false;

  @Parameter(property = "allowSnapshots", defaultValue = "false") private boolean allowSnapshots = false;

  @Parameter(property = "releaseRebase", defaultValue = "false") private boolean releaseRebase = false;

  @Parameter(property = "releaseMergeNoFF", defaultValue = "true") private boolean releaseMergeNoFF = true;

  @Parameter(property = "pushRemote", defaultValue = "true") private boolean pushRemote;

  @Parameter(property = "releaseMergeFFOnly", defaultValue = "false") private boolean releaseMergeFFOnly = false;

  @Parameter(property = "digitsOnlyDevVersion", defaultValue = "false") private boolean digitsOnlyDevVersion = false;

  @Parameter(property = "developmentVersion", defaultValue = "") private String developmentVersion = "";

  @Parameter(property = "versionDigitToIncrement") private Integer versionDigitToIncrement;

  @Parameter(property = "commitDevelopmentVersionAtStart", defaultValue = "false") private boolean commitDevelopmentVersionAtStart;

  @Parameter(property = "preReleaseGoals") private String preReleaseGoals;

  @Parameter(property = "postReleaseGoals") private String postReleaseGoals;

  @Parameter(property = "gpgSignTag", defaultValue = "false") private boolean gpgSignTag = false;

  @Parameter(property = "useSnapshotInRelease", defaultValue = "false") private boolean useSnapshotInRelease;

  @Override public void execute() throws MojoExecutionException, MojoFailureException {
    validateConfiguration(preReleaseGoals, postReleaseGoals);
    try {
      checkUncommittedChanges();
      String releaseBranch = gitFindBranches(gitFlowConfig.getReleaseBranchPrefix(), false).trim();
      if (StringUtils.isBlank(releaseBranch)) {
        if (fetchRemote) {
          releaseBranch = gitFetchAndFindRemoteBranches(gitFlowConfig.getOrigin(), gitFlowConfig.getReleaseBranchPrefix(), false).trim();
          if (StringUtils.isBlank(releaseBranch)) {
            throw new MojoFailureException("There is no remote or local release branch.");
          }
          releaseBranch = releaseBranch.substring(gitFlowConfig.getOrigin().length() + 1);
          if (StringUtils.countMatches(releaseBranch, gitFlowConfig.getReleaseBranchPrefix()) > 1) {
            throw new MojoFailureException("More than one remote release branch exists. Cannot finish release.");
          }
          gitCreateAndCheckout(releaseBranch, gitFlowConfig.getOrigin() + "/" + releaseBranch);
        } else {
          throw new MojoFailureException("There is no release branch.");
        }
      }
      if (StringUtils.countMatches(releaseBranch, gitFlowConfig.getReleaseBranchPrefix()) > 1) {
        throw new MojoFailureException("More than one release branch exists. Cannot finish release.");
      }
      if (!allowSnapshots) {
        gitCheckout(releaseBranch);
        checkSnapshotDependencies();
      }
      if (fetchRemote) {
        gitFetchRemoteAndCompare(releaseBranch);
        gitFetchRemoteAndCreate(gitFlowConfig.getDevelopmentBranch());
        gitFetchRemoteAndCompare(gitFlowConfig.getDevelopmentBranch());
        if (notSameProdDevName()) {
          gitFetchRemoteAndCreate(gitFlowConfig.getProductionBranch());
          gitFetchRemoteAndCompare(gitFlowConfig.getProductionBranch());
        }
      }
      gitCheckout(releaseBranch);
      if (!skipTestProject) {
        mvnCleanTest();
      }
      if (StringUtils.isNotBlank(preReleaseGoals)) {
        mvnRun(preReleaseGoals);
      }
      String currentReleaseVersion = getCurrentProjectVersion();
      Map<String, String> messageProperties = new HashMap<>();
      messageProperties.put("version", currentReleaseVersion);
      if (useSnapshotInRelease && ArtifactUtils.isSnapshot(currentReleaseVersion)) {
        String commitVersion = currentReleaseVersion.replace("-" + Artifact.SNAPSHOT_VERSION, "");
        mvnSetVersions(commitVersion);
        messageProperties.put("version", commitVersion);
        gitCommit(commitMessages.getReleaseFinishMessage(), messageProperties);
      }
      if (!skipReleaseMergeProdBranch) {
        gitCheckout(gitFlowConfig.getProductionBranch());
        gitMerge(releaseBranch, releaseRebase, releaseMergeNoFF, releaseMergeFFOnly, commitMessages.getReleaseFinishMergeMessage(), messageProperties);
      }
      final String currentVersion = getCurrentProjectVersion();
      if (!skipTag) {
        String tagVersion = currentVersion;
        if ((tychoBuild || useSnapshotInRelease) && ArtifactUtils.isSnapshot(currentVersion)) {
          tagVersion = currentVersion.replace("-" + Artifact.SNAPSHOT_VERSION, "");
        }
        messageProperties.put("version", tagVersion);
        gitTag(gitFlowConfig.getVersionTagPrefix() + tagVersion, commitMessages.getTagReleaseMessage(), gpgSignTag, messageProperties);
      }
      if (StringUtils.isNotBlank(postReleaseGoals)) {
        mvnRun(postReleaseGoals);
      }
      if (notSameProdDevName()) {
        gitCheckout(gitFlowConfig.getDevelopmentBranch());
        final String developReleaseVersion = getCurrentProjectVersion();
        if (commitDevelopmentVersionAtStart && useSnapshotInRelease) {
          mvnSetVersions(currentVersion);
          gitCommit(commitMessages.getUpdateDevToAvoidConflictsMessage());
        }
        gitMerge(releaseBranch, releaseRebase, releaseMergeNoFF, false, commitMessages.getReleaseFinishDevMergeMessage(), messageProperties);
        if (commitDevelopmentVersionAtStart && useSnapshotInRelease) {
          mvnSetVersions(developReleaseVersion);
          gitCommit(commitMessages.getUpdateDevBackPreMergeStateMessage());
        }
      }
      if (commitDevelopmentVersionAtStart && !notSameProdDevName()) {
        getLog().warn("The commitDevelopmentVersionAtStart will not have effect. " + "It can be enabled only when there are separate branches for development and production.");
        commitDevelopmentVersionAtStart = false;
      }
      if (!commitDevelopmentVersionAtStart) {
        final String nextSnapshotVersion;
        if (!settings.isInteractiveMode() && StringUtils.isNotBlank(developmentVersion)) {
          nextSnapshotVersion = developmentVersion;
        } else {
          GitFlowVersionInfo versionInfo = new GitFlowVersionInfo(currentVersion, getVersionPolicy());
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
      }
      if (installProject) {
        mvnCleanInstall();
      }
      if (pushRemote) {
        gitPush(gitFlowConfig.getProductionBranch(), !skipTag);
        if (notSameProdDevName()) {
          gitPush(gitFlowConfig.getDevelopmentBranch(), !skipTag);
        }
        if (!keepBranch) {
          gitPushDelete(releaseBranch);
        }
      }
      if (!keepBranch) {
        gitBranchDelete(releaseBranch);
      }
    } catch (Exception e) {
      throw new MojoFailureException("release-finish", e);
    }
  }

  @Parameter(property = "skipReleaseMergeProdBranch", defaultValue = "false") private boolean skipReleaseMergeProdBranch = false;
}