package com.github.maven_nar;
import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Abstract GNU Mojo keeps configuration
 * 
 * @author Mark Donszelmann
 */
public abstract class AbstractGnuMojo extends AbstractResourcesMojo {
  /**
     * Use GNU goals on Windows
     * 
     * @parameter expresssion="nar.gnu.useonwindows" default-value="false"
     * @required
     */
  @Parameter(defaultValue = "nar.gnu.useonwindows", required = true) private boolean gnuUseOnWindows;

  /**
     * Source directory for GNU style project
     * 
     * @parameter default-value="${basedir}/src/gnu"
     * @required
     */
  @Parameter(defaultValue = "${basedir}/src/gnu") private File gnuSourceDirectory;

  /**
     * Directory in which gnu sources are copied and "configured"
     * 
     * @parameter default-value="${project.build.directory}/nar/gnu"
     * @required
     */
  @Parameter(defaultValue = "${project.build.directory}/nar/gnu") private File gnuTargetDirectory;

  /**
     * @return
     * @throws MojoFailureException
     * @throws MojoExecutionException 
     */
  protected final File getGnuAOLSourceDirectory() throws MojoFailureException, MojoExecutionException {
    return new File(getGnuAOLDirectory(), "src");
  }

  /**
     * @return
     * @throws MojoFailureException
     * @throws MojoExecutionException 
     */
  protected final File getGnuAOLTargetDirectory() throws MojoFailureException, MojoExecutionException {
    return new File(getGnuAOLDirectory(), "target");
  }

  protected final File getGnuSourceDirectory() {
    return gnuSourceDirectory;
  }

  /**
     * @return
     * @throws MojoFailureException
     * @throws MojoExecutionException 
     */
  private File getGnuAOLDirectory() throws MojoFailureException, MojoExecutionException {
    return new File(gnuTargetDirectory, getAOL().toString());
  }

  /**
     * Returns true if we do not want to use GNU on Windows
     * 
     * @return
     */
  protected final boolean useGnu() {
    return gnuUseOnWindows || !OS.WINDOWS.equals(NarUtil.getOS(null));
  }
}