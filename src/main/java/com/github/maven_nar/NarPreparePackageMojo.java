package com.github.maven_nar;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Jars up the NAR files.
 * 
 * @goal nar-prepare-package
 * @phase prepare-package
 * @requiresProject
 * @author GDomjan
 */
public class NarPreparePackageMojo extends AbstractNarMojo {
  public final void narExecute() throws MojoExecutionException, MojoFailureException {
    getLayout().prepareNarInfo(getTargetDirectory(), getMavenProject(), getNarInfo(), this);
    getNarInfo().writeToDirectory(classesDirectory);
  }
}