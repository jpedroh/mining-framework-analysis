package com.github.maven_nar;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.manager.ArchiverManager;

/**
 * Jar up the NAR files and attach them to the projects main artifact (for installation and deployment).
 * 
 * @goal nar-package
 * @phase package
 * @requiresProject
 * @author Mark Donszelmann
 */
@Mojo(name = "nar-package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true) public class NarPackageMojo extends AbstractNarMojo {
  /**
     * To look up Archiver/UnArchiver implementations
     * 
     * @component role="org.codehaus.plexus.archiver.manager.ArchiverManager"
     * @required
     */
  @Component(role = org.codehaus.plexus.archiver.manager.ArchiverManager.class) private ArchiverManager archiverManager;

  /**
     * Used for attaching the artifact in the project
     * 
     * @component
     */
  @Component private MavenProjectHelper projectHelper;

  public final void narExecute() throws MojoExecutionException, MojoFailureException {
    getLayout().attachNars(getTargetDirectory(), archiverManager, projectHelper, getMavenProject());
  }
}