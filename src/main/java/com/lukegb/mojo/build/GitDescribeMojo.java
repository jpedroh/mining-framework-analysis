package com.lukegb.mojo.build;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmException;

/**
 * Goal which sets project properties for describer from the
 * current Git repository.
 *
 * @author Luke Granger-Brown
 * @goal gitdescribe
 * @requiresProject
 * @since 1.0-beta-4
 */
public class GitDescribeMojo extends AbstractMojo {
  /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @readonly
     */
  private MavenProject project;

  /**
     * Local directory to be used to issue SCM actions
     *
     * @parameter expression="${maven.changeSet.scmDirectory}" default-value="${basedir}
     * @since 1.0
     */
  private File scmDirectory;

  /**
     * String to append to git describe/shorttag output
     *
     * @parameter default-value=""
     * @deprecated superseded by outputSuffix.
     */
  @Deprecated private String outputPostfix;

  /**
     * String to append to git describe/shorttag output.
     *
     * @parameter default-value=""
     */
  private String outputSuffix;

  /**
     * String to prepend to git describe/shorttag output
     *
     * @parameter default-value="git-"
     */
  private String outputPrefix;

  /**
     * String indicating full output if getting version fails
     *
     * @parameter default-value="unknown"
     */
  private String failOutput;

  /**
     * The name of the build property that will contain the output of git describe.
     *
     * @parameter default-value="describe"
     */
  private String descriptionProperty;

  /**
     * The name of the build property that will contain the git commit count.
     *
     * @parameter default-value="git.commit.count"
     */
  private String commitCountProperty;

  /**
     * If true, pass the `--dirty` flag to git-describe.
     *
     * @parameter default-value=false
     */
  private boolean dirty;

  /**
     * The &lt;mark&gt; value for the `--dirty` parameter.
     *
     * @parameter default-value="dirty"
     */
  private String dirtyMark;

  /**
     * If true, pass the `--tags` flag to git-describe.
     *
     * @parameter default-value=false
     */
  private boolean 
<<<<<<< /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/left.java
  tags
=======
  longFlag
>>>>>>> /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/right.java
  ;

  /**
     * If true, set the properties on reactor projects.
     *
     * @parameter default-value="false"
     */
  private boolean setReactorProjectsProperties;

  /**
     * The projects in the reactor.
     *
     * @parameter expression="${reactorProjects}"
     * @readonly
     */
  private List reactorProjects;

  /**
     * Perform the task for which this plugin exists.
     * i.e. try to shove the Git Describe property into Maven
     */
  public void execute() throws MojoExecutionException {
    try {
      String previousDescribe = getDescribeProperty();
      if (previousDescribe == null) {
        String describe = getDescriber();
        getLog().info("Setting Git Describe: " + describe);
        setDescribeProperty(describe);
        setCommitCountProperty(getCommitCount(describe));
      }
    } catch (ScmException e) {
      throw new MojoExecutionException("SCM Exception", e);
    }
  }

  /**
     * Fetch the value of the main describer property.
     *
     * @return git describe output with prefix and suffix appended
     */
  protected String getDescriber() throws ScmException, MojoExecutionException {
    outputPrefix = firstNonNull(outputPrefix, "");
    outputSuffix = firstNonNull(outputSuffix, outputPostfix, "");
    String line = commandExecutor(buildDescribeCommand());
    if (line == null) {
      String commandtwo[] = { "git", "log", "--pretty=format:\"%h\"" };
      line = commandExecutor(commandtwo);
      if (line == null) {
        line = failOutput;
      }
    }
    return outputPrefix + line + outputSuffix;
  }

  /**
     * Build a String array containing the git command to run.
     *
     * @return array of String containing the command (including arguments) to run
     */
  private String[] buildDescribeCommand() {

<<<<<<< /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/left.java
    List<String> args = new ArrayList<String>();
=======
    ArrayList command = new ArrayList();
>>>>>>> /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/right.java


<<<<<<< /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/left.java
    args
=======
    command
>>>>>>> /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/right.java
    .add("git");

<<<<<<< /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/left.java
    args
=======
    command
>>>>>>> /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/right.java
    .add("describe");
    if (dirty) {

<<<<<<< /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/left.java
      args
=======
      command
>>>>>>> /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/right.java
      .add("--dirty=" + dirtyMark);
    }
    if (
<<<<<<< /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/left.java
    tags
=======
    longFlag
>>>>>>> /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/right.java
    ) {

<<<<<<< /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/left.java
      args
=======
      command
>>>>>>> /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/right.java
      .add(
<<<<<<< /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/left.java
      "--tags"
=======
      "--long"
>>>>>>> /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/right.java
      );
    }
    return 
<<<<<<< /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/left.java
    args.toArray(new String[args.size()])
=======
    (String[]) command.toArray(new String[command.size()])
>>>>>>> /usr/src/app/output/lukegb/gitdescribe-maven-plugin/ef23ebbbc45456d85db155436a821f99e9bc24f2/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/right.java
    ;
  }

  /**
     * Run a given command, passed as an array of Strings.
     *
     * @param command   the command (including parameters) to execute
     * @return          output of command to stdout
     */
  private String commandExecutor(String[] command) {
    try {
      Process p = new ProcessBuilder(command).directory(scmDirectory).start();
      InputStream is = p.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
      line = br.readLine();
      return line;
    } catch (Exception e) {
      return null;
    }
  }

  /**
     * Parse the default output of git describe to fetch a commit number.
     *
     * @param describer     output of git describe command
     * @return              version number as string
     */
  private String getCommitCount(String describer) {
    Pattern pattern = Pattern.compile("-(\\d+)-g[0-9a-f]{7}$");
    Matcher matcher = pattern.matcher(describer);
    if (!matcher.find()) {
      return failOutput;
    }
    String count = matcher.group(1);
    return count;
  }

  /**
     * Getter for descriptionProperty.
     */
  protected String getDescribeProperty() {
    return getProperty(descriptionProperty);
  }

  /**
     * Generic property fetcher.
     */
  protected String getProperty(String property) {
    return project.getProperties().getProperty(property);
  }

  /**
     * Setter for descriptionProperty.
     */
  private void setDescribeProperty(String describer) {
    setProperty(descriptionProperty, describer);
  }

  /**
     * Setter for commitCountProperty.
     */
  private void setCommitCountProperty(String count) {
    setProperty(commitCountProperty, count);
  }

  /**
     * Generic property setter.
     */
  private void setProperty(String property, String value) {
    if (value != null) {
      project.getProperties().put(property, value);
      if (setReactorProjectsProperties && reactorProjects != null) {
        for (Object reactorProject : reactorProjects) {
          MavenProject nextProj = (MavenProject) reactorProject;
          nextProj.getProperties().put(property, value);
        }
      }
    }
  }

  /**
     * Takes some Strings and returns the first, non-null String.
     *
     * @param strings   the Strings which we should go through
     * @return          the first non-null String passed.
     */
  private static String firstNonNull(String... strings) {
    for (String string : strings) {
      if (string != null) {
        return string;
      }
    }
    return null;
  }
}