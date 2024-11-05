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

  @Parameter(property = "branchName", defaultValue = "") private String branchName = "";

  @Override public void execute() throws MojoExecutionException, MojoFailureException {
    validateConfiguration(preHotfixGoals, postHotfixGoals);
    try {
      checkUncommittedChanges();
      if (!settings.isInteractiveMode() && StringUtils.isNotBlank(branchName)) {
        hotfixNumber = getIndexNumberByBranchName(branches, branchName);
      }
      String hotfixBranchName = null;
      if (settings.isInteractiveMode()) {
        hotfixBranchName = promptBranchName();
      } else {
        if (StringUtils.isNotBlank(hotfixVersion)) {
          final String branch = gitFlowConfig.getHotfixBranchPrefix() + hotfixVersion;
          if (!gitCheckBranchExists(branch)) {
            throw new MojoFailureException("Hotfix branch with name \'" + branch + "\' doesn\'t exist. Cannot finish hotfix.");
          }
          hotfixBranchName = branch;
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
          gitFetchRemoteAndCompare(supportBranchName);
        } else {
          if (notSameProdDevName()) {
            gitFetchRemoteAndCompare(gitFlowConfig.getDevelopmentBranch());
          }
          gitFetchRemoteAndCompare(gitFlowConfig.getProductionBranch());
        }
      }
      if (!skipTestProject) {
        gitCheckout(hotfixBranchName);
        mvnCleanTest();
      }
      if (StringUtils.isNotBlank(preHotfixGoals)) {
        gitCheckout(hotfixBranchName);
        mvnRun(preHotfixGoals);
      }
      if (supportBranchName != null) {
        gitCheckout(supportBranchName);
      } else {
        gitCheckout(gitFlowConfig.getProductionBranch());
      }
      gitMergeNoff(hotfixBranchName);
      final String currentVersion = getCurrentProjectVersion();
      if (!skipTag) {
        String tagVersion = currentVersion;
        if (tychoBuild && ArtifactUtils.isSnapshot(tagVersion)) {
          tagVersion = tagVersion.replace("-" + Artifact.SNAPSHOT_VERSION, "");
        }
        gitTag(gitFlowConfig.getVersionTagPrefix() + tagVersion, commitMessages.getTagHotfixMessage());
      }
      if (StringUtils.isNotBlank(postHotfixGoals)) {
        mvnRun(postHotfixGoals);
      }
      final String releaseBranch = gitFindBranches(gitFlowConfig.getReleaseBranchPrefix(), true);
      if (supportBranchName == null) {
        if (StringUtils.isNotBlank(releaseBranch)) {
          gitCheckout(releaseBranch);
          gitMergeNoff(hotfixBranchName);
        } else {
          GitFlowVersionInfo developVersionInfo = new GitFlowVersionInfo(currentVersion);
          if (notSameProdDevName()) {
            gitCheckout(gitFlowConfig.getDevelopmentBranch());
            developVersionInfo = new GitFlowVersionInfo(getCurrentProjectVersion());
            mvnSetVersions(currentVersion);
            gitCommit("update to hotfix version");
            gitMergeNoff(hotfixBranchName);
            GitFlowVersionInfo hotfixVersionInfo = new GitFlowVersionInfo(currentVersion);
            if (developVersionInfo.compareTo(hotfixVersionInfo) < 0) {
              developVersionInfo = hotfixVersionInfo;
            }
          }
          final String nextSnapshotVersion = developVersionInfo.getSnapshotVersionString();
          if (StringUtils.isBlank(nextSnapshotVersion)) {
            throw new MojoFailureException("Next snapshot version is blank.");
          }
          mvnSetVersions(nextSnapshotVersion);
          Map<String, String> properties = new HashMap<String, String>();
          properties.put("version", nextSnapshotVersion);
          gitCommit(commitMessages.getHotfixFinishMessage(), properties);
        }
      }
      if (installProject) {
        mvnCleanInstall();
      }
      if (!keepBranch) {
        gitBranchDelete(hotfixBranchName);
      }
      if (pushRemote) {
        if (supportBranchName != null) {
          gitPush(supportBranchName, !skipTag);
        } else {
          gitPush(gitFlowConfig.getProductionBranch(), !skipTag);
          if (StringUtils.isBlank(releaseBranch) && notSameProdDevName()) {
            gitPush(gitFlowConfig.getDevelopmentBranch(), !skipTag);
          }
        }
        if (!keepBranch) {
          gitPushDelete(hotfixBranchName);
        }
      }
    } catch (Exception e) {
      throw new MojoFailureException("hotfix-finish", e);
    }
  }

  private String getIndexNumberByBranchName(final String[] branches, final String branchName) {
    for (int i = 0; i < branches.length; i++) {
      if (branchName.equals(branches[i])) {
        return String.valueOf(i + 1);
      }
      i++;
    }
    return "";
  }

  @Parameter(property = "preHotfixGoals") private String preHotfixGoals;

  @Parameter(property = "postHotfixGoals") private String postHotfixGoals;

  @Parameter(property = "hotfixVersion") private String hotfixVersion;

  private String promptBranchName() throws MojoFailureException, CommandLineException {
    final String hotfixBranches = gitFindBranches(gitFlowConfig.getHotfixBranchPrefix(), false);
    if (StringUtils.isBlank(hotfixBranches)) {
      throw new MojoFailureException("There are no hotfix branches.");
    }
    String[] branches = hotfixBranches.split("\\r?\\n");
    List<String> numberedList = new ArrayList<String>();
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
}