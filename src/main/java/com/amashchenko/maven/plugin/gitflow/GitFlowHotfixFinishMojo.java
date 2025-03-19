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
import org.apache.maven.shared.release.versions.VersionParseException;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * The git flow hotfix finish mojo.
 * 
 */
@Mojo(name = "hotfix-finish", aggregator = true) public class GitFlowHotfixFinishMojo extends AbstractGitFlowMojo {
  /** Whether to skip tagging the hotfix in Git. */
  @Parameter(property = "skipTag", defaultValue = "false") private boolean skipTag = false;

  /** Whether to keep hotfix branch after finish. */
  @Parameter(property = "keepBranch", defaultValue = "false") private boolean keepBranch = false;

  /**
     * Whether to skip calling Maven test goal before merging the branch.
     * 
     * @since 1.0.5
     */
  @Parameter(property = "skipTestProject", defaultValue = "false") private boolean skipTestProject = false;

  /**
     * Whether to push to the remote.
     * 
     * @since 1.3.0
     */
  @Parameter(property = "pushRemote", defaultValue = "true") private boolean pushRemote;

  /** {@inheritDoc} */
  @Override public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      checkUncommittedChanges();
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
      if (settings.isInteractiveMode()) {
        hotfixNumber = "1";
      }
      try {
        while (StringUtils.isBlank(hotfixNumber)) {
          hotfixNumber = prompter.prompt(str.toString(), numberedList);
        }
      } catch (PrompterException e) {
        getLog().error(e);
      }
      String hotfixBranchName = null;
      if (hotfixNumber != null) {
        int num = Integer.parseInt(hotfixNumber);
        hotfixBranchName = branches[num - 1];
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
      if (supportBranchName != null) {
        gitCheckout(supportBranchName);
      } else {
        gitCheckout(gitFlowConfig.getProductionBranch());
      }
      gitMergeNoff(hotfixBranchName);
      final String hotfixVersion = getCurrentProjectVersion();
      if (!skipTag) {
        String tagVersion = hotfixVersion;
        if (tychoBuild && ArtifactUtils.isSnapshot(tagVersion)) {
          tagVersion = tagVersion.replace("-" + Artifact.SNAPSHOT_VERSION, "");
        }
        gitTag(gitFlowConfig.getVersionTagPrefix() + tagVersion, commitMessages.getTagHotfixMessage());
      }
      final String releaseBranch = gitFindBranches(gitFlowConfig.getReleaseBranchPrefix(), true);
      if (supportBranchName == null) {
        if (StringUtils.isNotBlank(releaseBranch)) {
          gitCheckout(releaseBranch);
          gitMergeNoff(hotfixBranchName);
        } else {
          GitFlowVersionInfo developVersionInfo = new GitFlowVersionInfo(hotfixVersion);
          if (notSameProdDevName()) {
            gitCheckout(gitFlowConfig.getDevelopmentBranch());
            developVersionInfo = new GitFlowVersionInfo(getCurrentProjectVersion());
            mvnSetVersions(hotfixVersion);
            gitCommit("update to hotfix version");
            gitMergeNoff(hotfixBranchName);
            GitFlowVersionInfo hotfixVersionInfo = new GitFlowVersionInfo(hotfixVersion);
            if (developVersionInfo.compareTo(hotfixVersionInfo) < 0) {
              developVersionInfo = hotfixVersionInfo;
            }
          }
          final String nextSnapshotVersion = developVersionInfo.nextSnapshotVersion();
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
    } catch (CommandLineException e) {
      getLog().error(e);
    } catch (VersionParseException e) {
      getLog().error(e);
    }
  }
}