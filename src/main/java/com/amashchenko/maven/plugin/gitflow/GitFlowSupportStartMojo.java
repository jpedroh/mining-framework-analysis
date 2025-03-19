package com.amashchenko.maven.plugin.gitflow;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * The git flow support start mojo.
 *
 * @since 1.5.0
 */
@Mojo(name = "support-start", aggregator = true) public class GitFlowSupportStartMojo extends AbstractGitFlowMojo {
  /**
     * Whether to push to the remote.
     *
     * @since 1.6.0
     */
  @Parameter(property = "pushRemote", defaultValue = "true") private boolean pushRemote;

  /**
     * Tag name to use in non-interactive mode.
     *
     * @since 1.9.0
     */
  @Parameter(property = "tagName") private String tagName;

  /**
     * Branch name to use in non-interactive mode.
     *
     * @since 1.10.1
     */
  @Parameter(property = "supportBranchName", defaultValue = "") private String supportBranchName;

  /**
     * support version to use instead of the default version.
     *
     * @since 1.10.1
     */
  @Parameter(property = "supportVersion", defaultValue = "") private String supportVersion;

  /** {@inheritDoc} */
  @Override public void execute() throws MojoExecutionException, MojoFailureException {
    validateConfiguration();
    try {
      initGitFlowConfig();
      checkUncommittedChanges();
      String tag = null;
      if (settings.isInteractiveMode()) {
        String tagsStr = gitFindTags();
        if (StringUtils.isBlank(tagsStr)) {
          throw new MojoFailureException("There are no tags.");
        }
        try {
          tag = prompter.prompt("Choose tag to start support branch", Arrays.asList(tagsStr.split("\\r?\\n")));
        } catch (PrompterException e) {
          throw new MojoFailureException("support-start", e);
        }
      } else {
        if (StringUtils.isNotBlank(tagName)) {
          if (gitCheckTagExists(tagName)) {
            tag = tagName;
          } else {
            throw new MojoFailureException("The tag \'" + tagName + "\' doesn\'t exist.");
          }
        } else {
          getLog().info("The tagName is blank. Using the last tag.");
          tag = gitFindLastTag();
        }
      }
      if (StringUtils.isBlank(tag)) {
        throw new MojoFailureException("Tag is blank.");
      }
      if (StringUtils.isBlank(supportBranchName)) {
        supportBranchName = tag;
      }
      final boolean supportBranchExists = gitCheckBranchExists(gitFlowConfig.getSupportBranchPrefix() + supportBranchName);
      if (supportBranchExists) {
        throw new MojoFailureException("Support branch with that name already exists.");
      }
      gitCreateAndCheckout(gitFlowConfig.getSupportBranchPrefix() + supportBranchName, tag);
      if (!settings.isInteractiveMode() && StringUtils.isNotBlank(supportVersion)) {
        String projectVersion = supportVersion;
        mvnSetVersions(projectVersion);
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("version", projectVersion);
        gitCommit(commitMessages.getSuportStartMessage(), properties);
      }
      if (installProject) {
        mvnCleanInstall();
      }
      if (pushRemote) {
        gitPush(gitFlowConfig.getSupportBranchPrefix() + supportBranchName, false);
      }
    } catch (CommandLineException e) {
      throw new MojoFailureException("support-start", e);
    }
  }
}