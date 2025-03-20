package com.github.maven_nar;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Create the nar.properties file.
 * 
 * @goal nar-prepare-package
 * @phase prepare-package
 * @requiresProject
 * @author GDomjan
 */
@Mojo(name = "nar-prepare-package", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresProject = true) public class NarPreparePackageMojo extends AbstractNarMojo {
  public final void narExecute() throws MojoExecutionException, MojoFailureException {
    getLayout().prepareNarInfo(getTargetDirectory(), getMavenProject(), getNarInfo(), this);
    getNarInfo().writeToDirectory(classesDirectory);
  }
}