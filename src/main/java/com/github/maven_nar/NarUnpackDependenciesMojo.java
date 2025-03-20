package com.github.maven_nar;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Goal that unpacks the project dependencies from the repository to a defined location.
 * Unpacking happens in the local repository, and also sets flags on binaries and corrects static libraries.
 * 
 * @goal nar-unpack-dependencies
 * @phase process-test-sources
 * @requiresProject
 * @requiresDependencyResolution test
 * @author Mark Donszelmann
 */
@Mojo(name = "nar-unpack-dependencies", defaultPhase = LifecyclePhase.PROCESS_TEST_SOURCES, requiresDependencyResolution = ResolutionScope.TEST, requiresProject = true) public class NarUnpackDependenciesMojo extends NarDownloadDependenciesMojo {
}