package com.github.maven_nar;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Downloads and unpacks any dependent NAR files. This includes the noarch and aol type NAR files.
 * 
 * @goal nar-download
 * @phase process-sources
 * @requiresProject
 * @requiresDependencyResolution compile
 * @author Mark Donszelmann
 */
@Mojo(name = "nar-download", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = true, requiresDependencyResolution = ResolutionScope.COMPILE) public class NarDownloadMojo extends AbstractDependencyMojo {
}