package com.github.maven_nar;
import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Runs make on the GNU style generated Makefile
 * 
 * @goal nar-gnu-make
 * @phase compile
 * @requiresProject
 * @author Mark Donszelmann
 */
@Mojo(name = "nar-gnu-make", requiresProject = true, defaultPhase = LifecyclePhase.COMPILE) public class NarGnuMakeMojo extends AbstractGnuMojo {
  /**
     * Space delimited list of arguments to pass to make
     *
     * @parameter default-value=""
     */
  @Parameter(defaultValue = "") private String gnuMakeArgs;

  /**
     * Comma delimited list of environment variables to setup before running make
     *
     * @parameter default-value=""
     */
  @Parameter(defaultValue = "") private String gnuMakeEnv;

  /**
     * Boolean to control if we should skip 'make install' after the make
     *
     * @parameter default-value="false"
     */
  @Parameter private boolean gnuMakeInstallSkip;

  public final void narExecute() throws MojoExecutionException, MojoFailureException {
    if (!useGnu()) {
      return;
    }
    File srcDir = getGnuAOLSourceDirectory();
    if (srcDir.exists()) {
      String[] args = null;
      String[] env = null;
      if (gnuMakeArgs != null) {
        args = gnuMakeArgs.split(" ");
      }
      if (gnuMakeEnv != null) {
        env = gnuMakeEnv.split(",");
      }
      getLog().info("Running GNU make");
      int result = NarUtil.runCommand("make", args, srcDir, env, getLog());
      if (result != 0) {
        throw new MojoExecutionException("\'make\' errorcode: " + result);
      }
      if (!gnuMakeInstallSkip) {
        getLog().info("Running make install");
        if (args != null) {
          gnuMakeArgs = gnuMakeArgs + " install";
          args = gnuMakeArgs.split(" ");
        } else {
          args = new String[] { "install" };
        }
        result = NarUtil.runCommand("make", args, srcDir, null, getLog());
        if (result != 0) {
          throw new MojoExecutionException("\'make install\' errorcode: " + result);
        }
      }
    }
  }
}