package com.amashchenko.maven.plugin.gitflow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

@Mojo(name = "hotfix-finish", aggregator = true) public class GitFlowHotfixFinishMojo extends AbstractGitFlowMojo {
  @Parameter(property = "skipTag", defaultValue = "false") private boolean skipTag = false;

  @Parameter(property = "keepBranch", defaultValue = "false") private boolean keepBranch = false;

  @Parameter(property = "skipTestProject", defaultValue = "false") private boolean skipTestProject = false;

  @Parameter(property = "pushRemote", defaultValue = "true") private boolean pushRemote;

  @Parameter(property = "preHotfixGoals") private String preHotfixGoals;

  @Parameter(property = "postHotfixGoals") private String postHotfixGoals;

  @Parameter(property = "hotfixVersion") private String hotfixVersion;

  @Parameter(property = "gpgSignTag", defaultValue = "false") private boolean gpgSignTag = false;

  @Parameter(property = "useSnapshotInHotfix", defaultValue = "false") private boolean useSnapshotInHotfix;

  @Parameter(property = "skipMergeProdBranch", defaultValue = "false") private boolean skipMergeProdBranch = false;

  @Parameter(property = "skipMergeDevBranch", defaultValue = "false") private boolean skipMergeDevBranch = false;

  @Override public void execute() throws MojoExecutionException, MojoFailureException {
    validateConfiguration(preHotfixGoals, postHotfixGoals);
    try {
      checkUncommittedChanges();
      String hotfixBranchName = null;
      if (settings.isInteractiveMode()) {
        hotfixBranchName = promptBranchName();
      } else {
        if (StringUtils.isNotBlank(hotfixBranch)) {
          if (!hotfixBranch.startsWith(gitFlowConfig.getHotfixBranchPrefix())) {
            throw new MojoFailureException("The hotfixBranch parameter doesn\'t start with hotfix branch prefix.");
          }
          if (!gitCheckBranchExists(hotfixBranch)) {
            throw new MojoFailureException("Hotfix branch with name \'" + hotfixBranch + "\' doesn\'t exist. Cannot finish hotfix.");
          }
          hotfixBranchName = hotfixBranch;
        } else {
          if (StringUtils.isNotBlank(hotfixVersion)) {
            final String branch = gitFlowConfig.getHotfixBranchPrefix() + hotfixVersion;
            if (!gitCheckBranchExists(branch)) {
              throw new MojoFailureException("Hotfix branch with name \'" + branch + "\' doesn\'t exist. Cannot finish hotfix.");
            }
            hotfixBranchName = branch;
          }
        }
      }
      if (StringUtils.isBlank(hotfixBranchName)) {
        throw new MojoFailureException("Hotfix branch name to finish is blank.");
      }
      String supportBranchName = null;
      boolean supportHotfix = hotfixBranchName.startsWith(gitFlowConfig.getHotfixBranchPrefix() + gitFlowConfig.getSupportBranchPrefix());
      if (supportHotfix) {
        supportBranchName = hotfixBranchName.substring(gitFlowConfig.getHotfixBranchPrefix().length());
        supportBranchName = supportBranchName.substring(0, supportBranchName.lastIndexOf('/'));
      }
      if (fetchRemote) {
        gitFetchRemoteAndCompare(hotfixBranchName);
        if (supportBranchName != null) {
          gitFetchRemoteAndCreate(supportBranchName);
          gitFetchRemoteAndCompare(supportBranchName);
        } else {
          if (notSameProdDevName()) {
            gitFetchRemoteAndCreate(gitFlowConfig.getDevelopmentBranch());
            gitFetchRemoteAndCompare(gitFlowConfig.getDevelopmentBranch());
          }
          gitFetchRemoteAndCreate(gitFlowConfig.getProductionBranch());
          gitFetchRemoteAndCompare(gitFlowConfig.getProductionBranch());
        }
      }
      gitCheckout(hotfixBranchName);
      if (!skipTestProject) {
        mvnCleanTest();
      }
      if (StringUtils.isNotBlank(preHotfixGoals)) {
        mvnRun(preHotfixGoals);
      }
      String currentHotfixVersion = getCurrentProjectVersion();
      Map<String, String> messageProperties = new HashMap<>();
      messageProperties.put("version", currentHotfixVersion);
      if (useSnapshotInHotfix && ArtifactUtils.isSnapshot(currentHotfixVersion)) {
        String commitVersion = currentHotfixVersion.replace("-" + Artifact.SNAPSHOT_VERSION, "");
        mvnSetVersions(commitVersion);
        messageProperties.put("version", commitVersion);
        gitCommit(commitMessages.getHotfixFinishMessage(), messageProperties);
      }
      if (supportBranchName != null) {
        gitCheckout(supportBranchName);
        gitMergeNoff(hotfixBranchName, commitMessages.getHotfixFinishSupportMergeMessage(), messageProperties);
      } else {
        if (!skipMergeProdBranch) {
          gitCheckout(gitFlowConfig.getProductionBranch());
          gitMergeNoff(hotfixBranchName, commitMessages.getHotfixFinishMergeMessage(), messageProperties);
        }
      }
      final String currentVersion = getCurrentProjectVersion();
      if (!skipTag) {
        String tagVersion = currentVersion;
        if ((tychoBuild || useSnapshotInHotfix) && ArtifactUtils.isSnapshot(tagVersion)) {
          tagVersion = tagVersion.replace("-" + Artifact.SNAPSHOT_VERSION, "");
        }
        Map<String, String> properties = new HashMap<>();
        properties.put("version", tagVersion);
        gitTag(gitFlowConfig.getVersionTagPrefix() + tagVersion, commitMessages.getTagHotfixMessage(), gpgSignTag, properties);
      }
      if (skipMergeProdBranch && (supportBranchName == null)) {
        gitCheckout(gitFlowConfig.getProductionBranch());
      }
      if (StringUtils.isNotBlank(postHotfixGoals)) {
        mvnRun(postHotfixGoals);
      }
      final String releaseBranch = gitFindBranches(gitFlowConfig.getReleaseBranchPrefix(), true);
      if (supportBranchName == null) {
        if (StringUtils.isNotBlank(releaseBranch)) {
          gitCheckout(releaseBranch);
          String releaseBranchVersion = getCurrentProjectVersion();
          if (!currentVersion.equals(releaseBranchVersion)) {
            mvnSetVersions(currentVersion);
            gitCommit(commitMessages.getUpdateReleaseToAvoidConflictsMessage());
          }
          messageProperties.put("version", currentVersion);
          gitMergeNoff(hotfixBranchName, commitMessages.getHotfixFinishReleaseMergeMessage(), messageProperties);
          if (!currentVersion.equals(releaseBranchVersion)) {
            mvnSetVersions(releaseBranchVersion);
            gitCommit(commitMessages.getUpdateReleaseBackPreMergeStateMessage());
          }
        } else {
          if (!skipMergeDevBranch) {
            GitFlowVersionInfo developVersionInfo = new GitFlowVersionInfo(currentVersion, getVersionPolicy());
            if (notSameProdDevName()) {
              gitCheckout(gitFlowConfig.getDevelopmentBranch());
              developVersionInfo = new GitFlowVersionInfo(getCurrentProjectVersion(), getVersionPolicy());
              mvnSetVersions(currentVersion);
              gitCommit(commitMessages.getHotfixVersionUpdateMessage());
              messageProperties.put("version", currentVersion);
              gitMergeNoff(hotfixBranchName, commitMessages.getHotfixFinishDevMergeMessage(), messageProperties);
              GitFlowVersionInfo hotfixVersionInfo = new GitFlowVersionInfo(currentVersion, getVersionPolicy());
              if (developVersionInfo.compareTo(hotfixVersionInfo) < 0) {
                developVersionInfo = hotfixVersionInfo;
              }
            }
            final String nextSnapshotVersion = developVersionInfo.getSnapshotVersionString();
            if (StringUtils.isBlank(nextSnapshotVersion)) {
              throw new MojoFailureException("Next snapshot version is blank.");
            }
            mvnSetVersions(nextSnapshotVersion);
            Map<String, String> properties = new HashMap<>();
            properties.put("version", nextSnapshotVersion);
            gitCommit(commitMessages.getHotfixFinishMessage(), properties);
          }
        }
      }
      if (installProject) {
        mvnCleanInstall();
      }
      if (pushRemote) {
        if (supportBranchName != null) {
          gitPush(supportBranchName, !skipTag);
        } else {
          gitPush(gitFlowConfig.getProductionBranch(), !skipTag);
          if (StringUtils.isNotBlank(releaseBranch)) {
            gitPush(releaseBranch, !skipTag);
          } else {
            if (StringUtils.isBlank(releaseBranch) && notSameProdDevName()) {
              gitPush(gitFlowConfig.getDevelopmentBranch(), !skipTag);
            }
          }
        }
        if (!keepBranch) {
          gitPushDelete(hotfixBranchName);
        }
      }
      if (!keepBranch) {
        if (skipMergeProdBranch) {
          gitBranchDeleteForce(hotfixBranchName);
        } else {
          gitBranchDelete(hotfixBranchName);
        }
      }
    } catch (Exception e) {
      throw new MojoFailureException("hotfix-finish", e);
    }
  }

  private String promptBranchName() throws MojoFailureException, CommandLineException {
    String hotfixBranches = gitFindBranches(gitFlowConfig.getHotfixBranchPrefix(), false);
    if (!gitFlowConfig.getHotfixBranchPrefix().endsWith("/")) {
      String supportHotfixBranches = gitFindBranches(gitFlowConfig.getHotfixBranchPrefix() + "*/*", false);
      hotfixBranches = hotfixBranches + supportHotfixBranches;
    }
    if (StringUtils.isBlank(hotfixBranches)) {
      throw new MojoFailureException("There are no hotfix branches.");
    }
    String[] branches = hotfixBranches.split("\\r?\\n");
    List<String> numberedList = new ArrayList<>();
    StringBuilder str = new StringBuilder("Hotfix branches:").append(LS);
    for (int i = 0; i < branches.length; i++) {
      str.append((i + 1) + ". " + branches[i] + LS);
      numberedList.add(String.valueOf(i + 1));
    }
    str.append("Choose hotfix branch to finish");
    String hotfixNumber = null;
    try {
      while (StringUtils.isBlank(hotfixNumber)) {
        hotfixNumber = prompter.prompt(str.toString(), numberedList);
      }
    } catch (PrompterException e) {
      throw new MojoFailureException("hotfix-finish", e);
    }
    String hotfixBranchName = null;
    if (hotfixNumber != null) {
      int num = Integer.parseInt(hotfixNumber);
      hotfixBranchName = branches[num - 1];
    }
    return hotfixBranchName;
  }

  @Parameter(property = "hotfixBranch") private String hotfixBranch;
}