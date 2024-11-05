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

@Mojo(name = "hotfix-start", aggregator = true) public class GitFlowHotfixStartMojo extends AbstractGitFlowMojo {
  @Parameter(property = "pushRemote", defaultValue = "false") private boolean pushRemote;

  @Parameter(property = "fromBranch") private String fromBranch;

  @Parameter(property = "hotfixVersion") private String hotfixVersion;

  @Parameter(property = "useSnapshotInHotfix", defaultValue = "false") private boolean useSnapshotInHotfix;

  @Override public void execute() throws MojoExecutionException, MojoFailureException {
    validateConfiguration();
    try {
      initGitFlowConfig();
      checkUncommittedChanges();
      String branchName = gitFlowConfig.getProductionBranch();
      final String supportBranchesStr = gitFindBranches(gitFlowConfig.getSupportBranchPrefix(), false);
      final String[] supportBranches;
      if (StringUtils.isNotBlank(supportBranchesStr)) {
        supportBranches = supportBranchesStr.split("\\r?\\n");
      } else {
        supportBranches = null;
      }
      if (settings.isInteractiveMode()) {
        if (supportBranches != null && supportBranches.length > 0) {
          String[] branches = new String[supportBranches.length + 1];
          for (int i = 0; i < supportBranches.length; i++) {
            branches[i] = supportBranches[i];
          }
          branches[supportBranches.length] = gitFlowConfig.getProductionBranch();
          List<String> numberedList = new ArrayList<>();
          StringBuilder str = new StringBuilder("Branches:").append(LS);
          for (int i = 0; i < branches.length; i++) {
            str.append((i + 1) + ". " + branches[i] + LS);
            numberedList.add(String.valueOf(i + 1));
          }
          str.append("Choose branch to hotfix");
          String branchNumber = null;
          try {
            while (StringUtils.isBlank(branchNumber)) {
              branchNumber = prompter.prompt(str.toString(), numberedList);
            }
          } catch (PrompterException e) {
            throw new MojoFailureException("hotfix-start", e);
          }
          if (branchNumber != null) {
            int num = Integer.parseInt(branchNumber);
            branchName = branches[num - 1];
          }
          if (StringUtils.isBlank(branchName)) {
            throw new MojoFailureException("Branch name is blank.");
          }
        }
      } else {
        if (StringUtils.isNotBlank(fromBranch)) {
          if (fromBranch.equals(gitFlowConfig.getProductionBranch()) || contains(supportBranches, fromBranch)) {
            branchName = fromBranch;
          } else {
            throw new MojoFailureException("The fromBranch is not production or support branch.");
          }
        }
      }
      gitCheckout(branchName);
      if (fetchRemote) {
        gitFetchRemoteAndCompare(branchName);
      }
      final String currentVersion = getCurrentProjectVersion();
      final String defaultVersion = new GitFlowVersionInfo(currentVersion, getVersionPolicy()).hotfixVersion(tychoBuild, hotfixVersionDigitToIncrement);
      if (defaultVersion == null) {
        throw new MojoFailureException("Cannot get default project version.");
      }
      String version = null;
      if (settings.isInteractiveMode()) {
        try {
          while (version == null) {
            version = prompter.prompt("What is the hotfix version? [" + defaultVersion + "]");
            if (!"".equals(version) && (!GitFlowVersionInfo.isValidVersion(version) || !validBranchName(version))) {
              getLog().info("The version is not valid.");
              version = null;
            }
          }
        } catch (PrompterException e) {
          throw new MojoFailureException("hotfix-start", e);
        }
      } else {
        if (StringUtils.isNotBlank(hotfixVersion) && (!GitFlowVersionInfo.isValidVersion(hotfixVersion) || !validBranchName(hotfixVersion))) {
          throw new MojoFailureException("The hotfix version \'" + hotfixVersion + "\' is not valid.");
        } else {
          version = hotfixVersion;
        }
      }
      if (StringUtils.isBlank(version)) {
        getLog().info("Version is blank. Using default version.");
        version = defaultVersion;
      }
      String branchVersionPart = version.replace('/', '_');
      String hotfixBranchName = gitFlowConfig.getHotfixBranchPrefix() + branchVersionPart;
      if (!gitFlowConfig.getProductionBranch().equals(branchName)) {
        hotfixBranchName = gitFlowConfig.getHotfixBranchPrefix() + branchName + "/" + branchVersionPart;
      }
      final boolean hotfixBranchExists = gitCheckBranchExists(hotfixBranchName);
      if (hotfixBranchExists) {
        throw new MojoFailureException("Hotfix branch with that name already exists. Cannot start hotfix.");
      }
      gitCreateAndCheckout(hotfixBranchName, branchName);
      if (!version.equals(currentVersion)) {
        String projectVersion = version;
        if (useSnapshotInHotfix && !ArtifactUtils.isSnapshot(version)) {
          projectVersion = version + "-" + Artifact.SNAPSHOT_VERSION;
        }
        if (useSnapshotInHotfix && mavenSession.getUserProperties().get("useSnapshotInHotfix") != null) {
          getLog().warn("The useSnapshotInHotfix parameter is set from the command line." + " Don\'t forget to use it in the finish goal as well." + " It is better to define it in the project\'s pom file.");
        }
        mvnSetVersions(projectVersion);
        Map<String, String> properties = new HashMap<>();
        properties.put("version", projectVersion);
        gitCommit(commitMessages.getHotfixStartMessage(), properties);
      }
      if (installProject) {
        mvnCleanInstall();
      }
      if (pushRemote) {
        gitPush(hotfixBranchName, false);
      }
    } catch (CommandLineException | VersionParseException e) {
      throw new MojoFailureException("hotfix-start", e);
    }
  }

  private boolean contains(String[] arr, String str) {
    if (arr != null && str != null) {
      for (String a : arr) {
        if (str.equals(a)) {
          return true;
        }
      }
    }
    return false;
  }

  @Parameter(property = "hotfixVersionDigitToIncrement") private Integer hotfixVersionDigitToIncrement;
}