package com.amashchenko.maven.plugin.gitflow;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.release.policy.version.VersionPolicy;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang3.SystemUtils;

public abstract class AbstractGitFlowMojo extends AbstractMojo {
  private static final String VERSIONS_MAVEN_PLUGIN_SET_GOAL = "org.codehaus.mojo:versions-maven-plugin:set";

  private static final String VERSIONS_MAVEN_PLUGIN_SET_PROPERTY_GOAL = "org.codehaus.mojo:versions-maven-plugin:set-property";

  private static final String TYCHO_VERSIONS_PLUGIN_SET_GOAL = "org.eclipse.tycho:tycho-versions-plugin:set-version";

  protected static final String LS = System.getProperty("line.separator");

  private static final int SUCCESS_EXIT_CODE = 0;

  private static final Pattern MAVEN_DISALLOWED_PATTERN = Pattern.compile("[&|;]");

  private final Commandline cmdGit = new Commandline();

  private final Commandline cmdMvn = new Commandline();

  @Parameter(defaultValue = "${gitFlowConfig}") protected GitFlowConfig gitFlowConfig;

  @Parameter(defaultValue = "${commitMessages}") protected CommitMessages commitMessages;

  @Parameter(defaultValue = "false") protected boolean tychoBuild;

  @Parameter(property = "installProject", defaultValue = "false") protected boolean installProject = false;

  @Parameter(property = "fetchRemote", defaultValue = "true") protected boolean fetchRemote;

  @Parameter(property = "verbose", defaultValue = "false") private boolean verbose = false;

  @Parameter(property = "argLine") private String argLine;

  @Parameter(property = "gpgSignCommit", defaultValue = "false") private boolean gpgSignCommit = false;

  @Parameter(property = "versionsForceUpdate", defaultValue = "false") private boolean versionsForceUpdate = false;

  @Parameter(property = "versionProperty") private String versionProperty;

  @Parameter(property = "skipUpdateVersion") private boolean skipUpdateVersion = false;

  @Parameter(property = "commitMessagePrefix") private String commitMessagePrefix;

  @Parameter(property = "mvnExecutable") private String mvnExecutable;

  @Parameter(property = "gitExecutable") private String gitExecutable;

  @Parameter(property = "projectVersionPolicyId") private String projectVersionPolicyId;

  @Parameter(defaultValue = "${session}", readonly = true) protected MavenSession mavenSession;

  @Component protected ProjectBuilder projectBuilder;

  @Component protected Prompter prompter;

  @Parameter(defaultValue = "${settings}", readonly = true) protected Settings settings;

  @Component protected Map<String, VersionPolicy> versionPolicies;

  private void initExecutables() {
    if (StringUtils.isBlank(cmdMvn.getExecutable())) {
      if (StringUtils.isBlank(mvnExecutable)) {
        final String javaCommand = mavenSession.getSystemProperties().getProperty("sun.java.command", "");
        final boolean wrapper = javaCommand.startsWith("org.apache.maven.wrapper.MavenWrapperMain");
        if (wrapper) {
          mvnExecutable = "." + SystemUtils.FILE_SEPARATOR + "mvnw";
        } else {
          mvnExecutable = "mvn";
        }
      }
      cmdMvn.setExecutable(mvnExecutable);
    }
    if (StringUtils.isBlank(cmdGit.getExecutable())) {
      if (StringUtils.isBlank(gitExecutable)) {
        gitExecutable = "git";
      }
      cmdGit.setExecutable(gitExecutable);
    }
  }

  protected void validateConfiguration(String... params) throws MojoFailureException {
    if (StringUtils.isNotBlank(argLine) && MAVEN_DISALLOWED_PATTERN.matcher(argLine).find()) {
      throw new MojoFailureException("The argLine doesn\'t match allowed pattern.");
    }
    if (params != null && params.length > 0) {
      for (String p : params) {
        if (StringUtils.isNotBlank(p) && MAVEN_DISALLOWED_PATTERN.matcher(p).find()) {
          throw new MojoFailureException("The \'" + p + "\' value doesn\'t match allowed pattern.");
        }
      }
    }
  }

  protected String getCurrentProjectVersion() throws MojoFailureException {
    final MavenProject reloadedProject = reloadProject(mavenSession.getCurrentProject());
    if (reloadedProject.getVersion() == null) {
      throw new MojoFailureException("Cannot get current project version. This plugin should be executed from the parent project.");
    }
    return reloadedProject.getVersion();
  }

  private MavenProject reloadProject(MavenProject project) throws MojoFailureException {
    try {
      ProjectBuildingResult result = projectBuilder.build(project.getFile(), mavenSession.getProjectBuildingRequest());
      return result.getProject();
    } catch (Exception e) {
      throw new MojoFailureException("Error re-loading project info", e);
    }
  }

  protected boolean notSameProdDevName() {
    return !gitFlowConfig.getProductionBranch().equals(gitFlowConfig.getDevelopmentBranch());
  }

  protected void checkUncommittedChanges() throws MojoFailureException, CommandLineException {
    getLog().info("Checking for uncommitted changes.");
    if (executeGitHasUncommitted()) {
      throw new MojoFailureException("You have some uncommitted files. Commit or discard local changes in order to proceed.");
    }
  }

  protected void checkSnapshotDependencies() throws MojoFailureException {
    getLog().info("Checking for SNAPSHOT versions in dependencies.");
    List<String> snapshots = new ArrayList<>();
    List<String> builtArtifacts = new ArrayList<>();
    List<MavenProject> projects = mavenSession.getProjects();
    for (MavenProject project : projects) {
      final MavenProject reloadedProject = reloadProject(project);
      builtArtifacts.add(reloadedProject.getGroupId() + ":" + reloadedProject.getArtifactId() + ":" + reloadedProject.getVersion());
      List<Dependency> dependencies = reloadedProject.getDependencies();
      for (Dependency d : dependencies) {
        String id = d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion();
        if (!builtArtifacts.contains(id) && ArtifactUtils.isSnapshot(d.getVersion())) {
          snapshots.add(reloadedProject + " -> " + d);
        }
      }
    }
    if (!snapshots.isEmpty()) {
      for (String s : snapshots) {
        getLog().warn(s);
      }
      throw new MojoFailureException("There is some SNAPSHOT dependencies in the project, see warnings above." + " Change them or ignore with `allowSnapshots` property.");
    }
  }

  protected boolean validBranchName(final String branchName) throws MojoFailureException, CommandLineException {
    CommandResult res = executeGitCommandExitCode("check-ref-format", "--allow-onelevel", branchName);
    return res.getExitCode() == SUCCESS_EXIT_CODE;
  }

  private boolean executeGitHasUncommitted() throws MojoFailureException, CommandLineException {
    boolean uncommited = false;
    final CommandResult diffCommandResult = executeGitCommandExitCode("diff", "--no-ext-diff", "--ignore-submodules", "--quiet", "--exit-code");
    String error = null;
    if (diffCommandResult.getExitCode() == SUCCESS_EXIT_CODE) {
      final CommandResult diffIndexCommandResult = executeGitCommandExitCode("diff-index", "--cached", "--quiet", "--ignore-submodules", "HEAD", "--");
      if (diffIndexCommandResult.getExitCode() != SUCCESS_EXIT_CODE) {
        error = diffIndexCommandResult.getError();
        uncommited = true;
      }
    } else {
      error = diffCommandResult.getError();
      uncommited = true;
    }
    if (StringUtils.isNotBlank(error)) {
      throw new MojoFailureException(error);
    }
    return uncommited;
  }

  protected void initGitFlowConfig() throws MojoFailureException, CommandLineException {
    gitSetConfig("gitflow.branch.master", gitFlowConfig.getProductionBranch());
    gitSetConfig("gitflow.branch.develop", gitFlowConfig.getDevelopmentBranch());
    gitSetConfig("gitflow.prefix.feature", gitFlowConfig.getFeatureBranchPrefix());
    gitSetConfig("gitflow.prefix.release", gitFlowConfig.getReleaseBranchPrefix());
    gitSetConfig("gitflow.prefix.hotfix", gitFlowConfig.getHotfixBranchPrefix());
    gitSetConfig("gitflow.prefix.support", gitFlowConfig.getSupportBranchPrefix());
    gitSetConfig("gitflow.prefix.versiontag", gitFlowConfig.getVersionTagPrefix());
    gitSetConfig("gitflow.origin", gitFlowConfig.getOrigin());
  }

  private void gitSetConfig(final String name, String value) throws MojoFailureException, CommandLineException {
    if (value == null || value.isEmpty()) {
      value = "\"\"";
    }
    executeGitCommandExitCode("config", name, value);
  }

  protected String gitFindBranches(final String branchName, final boolean firstMatch) throws MojoFailureException, CommandLineException {
    return gitFindBranches("refs/heads/", branchName, firstMatch);
  }

  private String gitFindBranches(final String refs, final String branchName, final boolean firstMatch) throws MojoFailureException, CommandLineException {
    String wildcard = "*";
    if (branchName.endsWith("/")) {
      wildcard = "**";
    }
    String branches;
    if (firstMatch) {
      branches = executeGitCommandReturn("for-each-ref", "--count=1", "--format=\"%(refname:short)\"", refs + branchName + wildcard);
    } else {
      branches = executeGitCommandReturn("for-each-ref", "--format=\"%(refname:short)\"", refs + branchName + wildcard);
    }
    branches = removeQuotes(branches);
    branches = StringUtils.strip(branches);
    return branches;
  }

  protected String gitFindTags() throws MojoFailureException, CommandLineException {
    String tags = executeGitCommandReturn("for-each-ref", "--sort=*authordate", "--format=\"%(refname:short)\"", "refs/tags/");
    tags = removeQuotes(tags);
    return tags;
  }

  protected String gitFindLastTag() throws MojoFailureException, CommandLineException {
    String tag = executeGitCommandReturn("for-each-ref", "--sort=\"-version:refname\"", "--sort=-taggerdate", "--count=1", "--format=\"%(refname:short)\"", "refs/tags/");
    tag = removeQuotes(tag);
    tag = tag.replaceAll("\\r?\\n", "");
    return tag;
  }

  private String removeQuotes(String str) {
    return StringUtils.replace(str, "\"", "");
  }

  protected String gitCurrentBranch() throws MojoFailureException, CommandLineException {
    String name = executeGitCommandReturn("symbolic-ref", "-q", "--short", "HEAD");
    name = StringUtils.strip(name);
    return name;
  }

  protected boolean gitCheckBranchExists(final String branchName) throws MojoFailureException, CommandLineException {
    CommandResult commandResult = executeGitCommandExitCode("show-ref", "--verify", "--quiet", "refs/heads/" + branchName);
    return commandResult.getExitCode() == SUCCESS_EXIT_CODE;
  }

  protected boolean gitCheckTagExists(final String tagName) throws MojoFailureException, CommandLineException {
    CommandResult commandResult = executeGitCommandExitCode("show-ref", "--verify", "--quiet", "refs/tags/" + tagName);
    return commandResult.getExitCode() == SUCCESS_EXIT_CODE;
  }

  protected void gitCheckout(final String branchName) throws MojoFailureException, CommandLineException {
    getLog().info("Checking out \'" + branchName + "\' branch.");
    executeGitCommand("checkout", branchName);
  }

  protected void gitCreateAndCheckout(final String newBranchName, final String fromBranchName) throws MojoFailureException, CommandLineException {
    getLog().info("Creating a new branch \'" + newBranchName + "\' from \'" + fromBranchName + "\' and checking it out.");
    executeGitCommand("checkout", "-b", newBranchName, fromBranchName);
  }

  protected void gitCreateBranch(final String newBranchName, final String fromBranchName) throws MojoFailureException, CommandLineException {
    getLog().info("Creating a new branch \'" + newBranchName + "\' from \'" + fromBranchName + "\'.");
    executeGitCommand("branch", newBranchName, fromBranchName);
  }

  private String replaceProperties(String message, Map<String, String> map) {
    if (map != null) {
      for (Entry<String, String> entr : map.entrySet()) {
        message = StringUtils.replace(message, "@{" + entr.getKey() + "}", entr.getValue());
      }
    }
    return message;
  }

  protected void gitCommit(final String message) throws MojoFailureException, CommandLineException {
    gitCommit(message, null);
  }

  protected void gitCommit(String message, Map<String, String> messageProperties) throws MojoFailureException, CommandLineException {
    if (StringUtils.isNotBlank(commitMessagePrefix)) {
      message = commitMessagePrefix + message;
    }
    message = replaceProperties(message, messageProperties);
    if (gpgSignCommit) {
      getLog().info("Committing changes. GPG-signed.");
      executeGitCommand("commit", "-a", "-S", "-m", message);
    } else {
      getLog().info("Committing changes.");
      executeGitCommand("commit", "-a", "-m", message);
    }
  }

  protected void gitMerge(final String branchName, boolean rebase, boolean noff, boolean ffonly, String message, Map<String, String> messageProperties) throws MojoFailureException, CommandLineException {
    String sign = "";
    if (gpgSignCommit) {
      sign = "-S";
    }
    String msgParam = "";
    String msg = "";
    if (StringUtils.isNotBlank(message)) {
      if (StringUtils.isNotBlank(commitMessagePrefix)) {
        message = commitMessagePrefix + message;
      }
      msgParam = "-m";
      msg = replaceProperties(message, messageProperties);
    }
    if (rebase) {
      getLog().info("Rebasing \'" + branchName + "\' branch.");
      executeGitCommand("rebase", sign, branchName);
    } else {
      if (ffonly) {
        getLog().info("Merging (--ff-only) \'" + branchName + "\' branch.");
        executeGitCommand("merge", "--ff-only", sign, branchName);
      } else {
        if (noff) {
          getLog().info("Merging (--no-ff) \'" + branchName + "\' branch.");
          executeGitCommand("merge", "--no-ff", sign, branchName, msgParam, msg);
        } else {
          getLog().info("Merging \'" + branchName + "\' branch.");
          executeGitCommand("merge", sign, branchName, msgParam, msg);
        }
      }
    }
  }

  protected void gitMergeNoff(final String branchName, final String message, final Map<String, String> messageProperties) throws MojoFailureException, CommandLineException {
    gitMerge(branchName, false, true, false, message, messageProperties);
  }

  protected void gitMergeSquash(final String branchName) throws MojoFailureException, CommandLineException {
    getLog().info("Squashing \'" + branchName + "\' branch.");
    executeGitCommand("merge", "--squash", branchName);
  }

  protected void gitTag(final String tagName, String message, boolean gpgSignTag, Map<String, String> messageProperties) throws MojoFailureException, CommandLineException {
    message = replaceProperties(message, messageProperties);
    if (gpgSignTag) {
      getLog().info("Creating GPG-signed \'" + tagName + "\' tag.");
      executeGitCommand("tag", "-a", "-s", tagName, "-m", message);
    } else {
      getLog().info("Creating \'" + tagName + "\' tag.");
      executeGitCommand("tag", "-a", tagName, "-m", message);
    }
  }

  protected void gitBranchDelete(final String branchName) throws MojoFailureException, CommandLineException {
    getLog().info("Deleting \'" + branchName + "\' branch.");
    executeGitCommand("branch", "-d", branchName);
  }

  protected void gitBranchDeleteForce(final String branchName) throws MojoFailureException, CommandLineException {
    getLog().info("Deleting (-D) \'" + branchName + "\' branch.");
    executeGitCommand("branch", "-D", branchName);
  }

  protected void gitFetchRemoteAndCreate(final String branchName) throws MojoFailureException, CommandLineException {
    if (!gitCheckBranchExists(branchName)) {
      getLog().info("Local branch \'" + branchName + "\' doesn\'t exist. Trying to fetch and check it out from \'" + gitFlowConfig.getOrigin() + "\'.");
      gitFetchRemote(branchName);
      gitCreateAndCheckout(branchName, gitFlowConfig.getOrigin() + "/" + branchName);
    }
  }

  protected void gitFetchRemoteAndCompare(final String branchName) throws MojoFailureException, CommandLineException {
    if (gitFetchRemote(branchName)) {
      getLog().info("Comparing local branch \'" + branchName + "\' with remote \'" + gitFlowConfig.getOrigin() + "/" + branchName + "\'.");
      String revlistout = executeGitCommandReturn("rev-list", "--left-right", "--count", branchName + "..." + gitFlowConfig.getOrigin() + "/" + branchName);
      String[] counts = org.apache.commons.lang3.StringUtils.split(revlistout, '\t');
      if (counts != null && counts.length > 1 && !"0".equals(org.apache.commons.lang3.StringUtils.deleteWhitespace(counts[1]))) {
        throw new MojoFailureException("Remote branch \'" + gitFlowConfig.getOrigin() + "/" + branchName + "\' is ahead of the local branch \'" + branchName + "\'. Execute git pull.");
      }
    }
  }

  protected String gitFetchAndFindRemoteBranches(final String remoteName, final String branchName, final boolean firstMatch) throws MojoFailureException, CommandLineException {
    gitFetchRemote();
    return gitFindBranches("refs/remotes/" + remoteName + "/", branchName, firstMatch);
  }

  private boolean gitFetchRemote() throws MojoFailureException, CommandLineException {
    return gitFetchRemote("");
  }

  private boolean gitFetchRemote(final String branchName) throws MojoFailureException, CommandLineException {
    getLog().info("Fetching remote branch \'" + gitFlowConfig.getOrigin() + " " + branchName + "\'.");
    CommandResult result = executeGitCommandExitCode("fetch", "--quiet", gitFlowConfig.getOrigin(), branchName);
    boolean success = result.getExitCode() == SUCCESS_EXIT_CODE;
    if (!success) {
      getLog().warn("There were some problems fetching remote branch \'" + gitFlowConfig.getOrigin() + " " + branchName + "\'. You can turn off remote branch fetching by setting the \'fetchRemote\' parameter to false.");
    }
    return success;
  }

  protected void gitPush(final String branchName, boolean pushTags) throws MojoFailureException, CommandLineException {
    getLog().info("Pushing \'" + branchName + "\' branch to \'" + gitFlowConfig.getOrigin() + "\'.");
    List<String> args = new ArrayList<>();
    args.add("push");
    args.add("--quiet");
    args.add("-u");
    if (pushTags) {
      args.add("--follow-tags");
    } else {
      executeGitCommand("push", "--quiet", "-u", gitFlowConfig.getOrigin(), branchName);
    }
    if (StringUtils.isNotBlank(gitPushOptions)) {
      try {
        String[] opts = CommandLineUtils.translateCommandline(gitPushOptions);
        for (String opt : opts) {
          args.add("--push-option=" + opt);
        }
      } catch (Exception e) {
        throw new CommandLineException(e.getMessage(), e);
      }
    }
    args.add(gitFlowConfig.getOrigin());
    args.add(branchName);
    executeGitCommand(args.toArray(new String[0]));
  }

  protected void gitPushDelete(final String branchName) throws MojoFailureException, CommandLineException {
    getLog().info("Deleting remote branch \'" + branchName + "\' from \'" + gitFlowConfig.getOrigin() + "\'.");
    CommandResult result = executeGitCommandExitCode("push", "--delete", gitFlowConfig.getOrigin(), branchName);
    if (result.getExitCode() != SUCCESS_EXIT_CODE) {
      getLog().warn("There were some problems deleting remote branch \'" + branchName + "\' from \'" + gitFlowConfig.getOrigin() + "\'.");
    }
  }

  protected void mvnSetVersions(final String version) throws MojoFailureException, CommandLineException {
    getLog().info("Updating version(s) to \'" + version + "\'.");
    String newVersion = "-DnewVersion=" + version;
    String grp = "";
    String art = "";
    if (versionsForceUpdate) {
      grp = "-DgroupId=";
      art = "-DartifactId=";
    }
    if (tychoBuild) {
      String prop = "";
      if (StringUtils.isNotBlank(versionProperty)) {
        prop = "-Dproperties=" + versionProperty;
        getLog().info("Updating property \'" + versionProperty + "\' to \'" + version + "\'.");
      }
      executeMvnCommand(TYCHO_VERSIONS_PLUGIN_SET_GOAL, prop, newVersion, "-Dtycho.mode=maven");
    } else {
      boolean runCommand = false;
      List<String> args = new ArrayList<>();
      args.add("-DgenerateBackupPoms=false");
      args.add(newVersion);
      if (!skipUpdateVersion) {
        runCommand = true;
        args.add(VERSIONS_MAVEN_PLUGIN_SET_GOAL);
        args.add(grp);
        args.add(art);
      }
      if (StringUtils.isNotBlank(versionProperty)) {
        runCommand = true;
        getLog().info("Updating property \'" + versionProperty + "\' to \'" + version + "\'.");
        args.add(VERSIONS_MAVEN_PLUGIN_SET_PROPERTY_GOAL);
        args.add("-Dproperty=" + versionProperty);
      }
      if (runCommand) {
        executeMvnCommand(args.toArray(new String[0]));
        if (updateOutputTimestamp) {
          String timestamp = getCurrentProjectOutputTimestamp();
          if (timestamp != null && timestamp.length() > 1) {
            if (StringUtils.isNumeric(timestamp)) {
              timestamp = String.valueOf(System.currentTimeMillis() / 1000l);
            } else {
              DateFormat df = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'");
              df.setTimeZone(TimeZone.getTimeZone("UTC"));
              timestamp = df.format(new Date());
            }
            getLog().info("Updating property \'" + REPRODUCIBLE_BUILDS_PROPERTY + "\' to \'" + timestamp + "\'.");
            executeMvnCommand(VERSIONS_MAVEN_PLUGIN_SET_PROPERTY_GOAL, "-DgenerateBackupPoms=false", "-Dproperty=" + REPRODUCIBLE_BUILDS_PROPERTY, "-DnewVersion=" + timestamp);
          }
        }
      }
    }
  }

  protected void mvnCleanTest() throws MojoFailureException, CommandLineException {
    getLog().info("Cleaning and testing the project.");
    if (tychoBuild) {
      executeMvnCommand("clean", "verify");
    } else {
      executeMvnCommand("clean", "test");
    }
  }

  protected void mvnCleanInstall() throws MojoFailureException, CommandLineException {
    getLog().info("Cleaning and installing the project.");
    executeMvnCommand("clean", "install");
  }

  protected void mvnRun(final String goals) throws Exception {
    getLog().info("Running Maven goals: " + goals);
    executeMvnCommand(CommandLineUtils.translateCommandline(goals));
  }

  private String executeGitCommandReturn(final String... args) throws CommandLineException, MojoFailureException {
    return executeCommand(cmdGit, true, null, args).getOut();
  }

  private CommandResult executeGitCommandExitCode(final String... args) throws CommandLineException, MojoFailureException {
    return executeCommand(cmdGit, false, null, args);
  }

  private void executeGitCommand(final String... args) throws CommandLineException, MojoFailureException {
    executeCommand(cmdGit, true, null, args);
  }

  private void executeMvnCommand(final String... args) throws CommandLineException, MojoFailureException {
    executeCommand(cmdMvn, true, argLine, args);
  }

  private CommandResult executeCommand(final Commandline cmd, final boolean failOnError, final String argStr, final String... args) throws CommandLineException, MojoFailureException {
    initExecutables();
    if (getLog().isDebugEnabled()) {
      getLog().debug(cmd.getExecutable() + " " + StringUtils.join(args, " ") + (argStr == null ? "" : " " + argStr));
    }
    cmd.clearArgs();
    cmd.addArguments(args);
    if (StringUtils.isNotBlank(argStr)) {
      cmd.createArg().setLine(argStr);
    }
    final StringBufferStreamConsumer out = new StringBufferStreamConsumer(verbose);
    final CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();
    final int exitCode = CommandLineUtils.executeCommandLine(cmd, out, err);
    String errorStr = err.getOutput();
    String outStr = out.getOutput();
    if (failOnError && exitCode != SUCCESS_EXIT_CODE) {
      if (StringUtils.isBlank(errorStr) && StringUtils.isNotBlank(outStr)) {
        errorStr = outStr;
      }
      throw new MojoFailureException(errorStr);
    }
    return new CommandResult(exitCode, outStr, errorStr);
  }

  private static class CommandResult {
    private final int exitCode;

    private final String out;

    private final String error;

    private CommandResult(final int exitCode, final String out, final String error) {
      this.exitCode = exitCode;
      this.out = out;
      this.error = error;
    }

    public int getExitCode() {
      return exitCode;
    }

    public String getOut() {
      return out;
    }

    public String getError() {
      return error;
    }
  }

  public void setArgLine(String argLine) {
    this.argLine = argLine;
  }

  protected VersionPolicy getVersionPolicy() {
    if (StringUtils.isNotBlank(projectVersionPolicyId)) {
      VersionPolicy versionPolicy = versionPolicies.get(projectVersionPolicyId);
      if (versionPolicy == null) {
        throw new IllegalArgumentException("No implementation found for projectVersionPolicyId: " + projectVersionPolicyId);
      }
      return versionPolicy;
    }
    return null;
  }

  private static final String REPRODUCIBLE_BUILDS_PROPERTY = "project.build.outputTimestamp";

  @Parameter(property = "updateOutputTimestamp", defaultValue = "true") private boolean updateOutputTimestamp = true;

  @Parameter(property = "gitPushOptions") private String gitPushOptions;

  private String getCurrentProjectOutputTimestamp() throws MojoFailureException {
    final MavenProject reloadedProject = reloadProject(mavenSession.getCurrentProject());
    return reloadedProject.getProperties().getProperty(REPRODUCIBLE_BUILDS_PROPERTY);
  }
}