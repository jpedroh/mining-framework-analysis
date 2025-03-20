package com.github.maven_nar;
import java.util.List;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Downloads any dependent NAR files. This includes the noarch and aol type NAR files.
 * 
 * @goal nar-download-dependencies
 * @phase process-sources
 * @requiresProject
 * @requiresDependencyResolution test
 * @author Mark Donszelmann
 */
@Mojo(name = "nar-download-dependencies", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = true, requiresDependencyResolution = ResolutionScope.TEST) public class NarDownloadDependenciesMojo extends AbstractDependencyMojo {
  @Override protected List getArtifacts() {
    return getMavenProject().getTestArtifacts();
  }
}