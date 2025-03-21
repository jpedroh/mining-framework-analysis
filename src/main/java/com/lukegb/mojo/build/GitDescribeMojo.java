package com.lukegb.mojo.build;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.List;
import java.util.regex.Pattern;
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
     * If true, pass the `--dirty` flag to git-describe.
     *
     * @parameter default-value=false
     */
  private boolean dirty;


<<<<<<< /usr/src/app/output/lukegb/gitdescribe-maven-plugin/8537d4687a51ced12400731b2a25204cfaa349ab/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/left.java
  /**
     * If true, pass the `--tags` flag to git-describe.
     *
     * @parameter default-value=false
     */
  private boolean tags;
=======
  /**
     * The name of the build property that will contain the git commit count.
     *
     * @parameter default-value="git.commit.count"
     */
  private String commitCountProperty;
>>>>>>> /usr/src/app/output/lukegb/gitdescribe-maven-plugin/8537d4687a51ced12400731b2a25204cfaa349ab/src/main/java/com/lukegb/mojo/build/GitDescribeMojo.java/right.java


  /**
     * The &lt;mark&gt; value for the `--dirty` parameter.
     *
     * @parameter default-value="dirty"
     */
  private String dirtyMark;

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

  private String[] buildDescribeCommand() {
    List<String> args = new ArrayList<String>();
    args.add("git");
    args.add("describe");
    if (dirty) {
      args.add("--dirty=" + dirtyMark);
    }
    if (tags) {
      args.add("--tags");
    }
    return args.toArray(new String[args.size()]);
  }

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

  protected String getDescribeProperty() {
    return getProperty(descriptionProperty);
  }

  protected String getProperty(String property) {
    return project.getProperties().getProperty(property);
  }

  private void setDescribeProperty(String describer) {
    setProperty(descriptionProperty, describer);
  }

  /**
     * Setter for commitCountProperty.
     */
  private void setCommitCountProperty(String count) {
    setProperty(commitCountProperty, count);
  }

  private void setProperty(String property, String value) {
    if (value != null) {
      project.getProperties().put(property, value);
    }
  }

  private static String firstNonNull(String... strings) {
    for (String string : strings) {
      if (string != null) {
        return string;
      }
    }
    return null;
  }
}