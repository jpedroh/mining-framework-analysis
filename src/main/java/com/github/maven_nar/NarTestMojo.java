package com.github.maven_nar;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

/**
 * Tests NAR files. Runs Native Tests and executables if produced.
 * 
 * @goal nar-test
 * @phase test
 * @requiresProject
 * @requiresDependencyResolution test
 * @author Mark Donszelmann
 */
@Mojo(name = "nar-test", defaultPhase = LifecyclePhase.TEST, requiresProject = true, requiresDependencyResolution = ResolutionScope.TEST) public class NarTestMojo extends AbstractCompileMojo {
  /**
     * The classpath elements of the project being tested.
     * 
     * @parameter default-value="${project.testClasspathElements}"
     * @required
     * @readonly
     */
  @Parameter(defaultValue = "${project.testClasspathElements}", required = true, readonly = true) private List classpathElements;

  /**
     * Directory for test resources. Defaults to src/test/resources
     * 
     * @parameter default-value="${basedir}/src/test/resources"
     * @required
     */
  @Parameter(defaultValue = "${basedir}/src/test/resources", required = true) private File testResourceDirectory;

  @Override protected List getArtifacts() {
    return getMavenProject().getTestArtifacts();
  }

  protected File getUnpackDirectory() {
    return getTestUnpackDirectory() == null ? super.getUnpackDirectory() : getTestUnpackDirectory();
  }

  public final void narExecute() throws MojoExecutionException, MojoFailureException {
    if (skipTests) {
      getLog().info("Tests are skipped");
    } else {
      super.narExecute();
      for (Iterator i = getTests().iterator(); i.hasNext(); ) {
        runTest((Test) i.next());
      }
      for (Iterator i = getLibraries().iterator(); i.hasNext(); ) {
        runExecutable((Library) i.next());
      }
    }
  }

  private void runTest(Test test) throws MojoExecutionException, MojoFailureException {
    if (test.shouldRun()) {
      String name = test.getName() + (getOS().equals(OS.WINDOWS) ? ".exe" : "");
      File path = new File(getTestTargetDirectory(), "bin");
      path = new File(path, getAOL().toString());
      path = new File(path, name);
      if (!path.exists()) {
        getLog().warn("Skipping non-existing test " + path);
        return;
      }
      File workingDir = new File(getTestTargetDirectory(), "test-reports");
      workingDir.mkdirs();
      try {
        int copied = 0;
        if (testResourceDirectory.exists()) {
          copied += NarUtil.copyDirectoryStructure(testResourceDirectory, workingDir, null, NarUtil.DEFAULT_EXCLUDES);
        }
        getLog().info("Copied " + copied + " test resources");
      } catch (IOException e) {
        throw new MojoExecutionException("NAR: Could not copy test resources", e);
      }
      getLog().info("Running test " + name + " in " + workingDir);
      List args = test.getArgs();
      int result = NarUtil.runCommand(path.toString(), (String[]) args.toArray(new String[args.size()]), workingDir, generateEnvironment(), getLog());
      if (result != 0) {
        throw new MojoFailureException("Test " + name + " failed with exit code: " + result + " 0x" + Integer.toHexString(result));
      }
    }
  }

  private void runExecutable(Library library) throws MojoExecutionException, MojoFailureException {
    if (library.getType().equals(Library.EXECUTABLE) && library.shouldRun()) {
      MavenProject project = getMavenProject();
      String extension = getOS().equals(OS.WINDOWS) ? ".exe" : "";
      File executable = new File(getLayout().getBinDirectory(getTargetDirectory(), getMavenProject().getArtifactId(), getMavenProject().getVersion(), getAOL().toString()), project.getArtifactId() + extension);
      if (!executable.exists()) {
        getLog().warn("Skipping non-existing executable " + executable);
        return;
      }
      getLog().info("Running executable " + executable);
      List args = library.getArgs();
      int result = NarUtil.runCommand(executable.getPath(), (String[]) args.toArray(new String[args.size()]), null, generateEnvironment(), getLog());
      if (result != 0) {
        throw new MojoFailureException("Test " + executable + " failed with exit code: " + result + " 0x" + Integer.toHexString(result));
      }
    }
  }

  private String[] generateEnvironment() throws MojoExecutionException, MojoFailureException {
    List env = new ArrayList();
    Set sharedPaths = new HashSet();
    for (Iterator i = getLibraries().iterator(); i.hasNext(); ) {
      Library lib = (Library) i.next();
      if (lib.getType().equals(Library.SHARED)) {
        File path = getLayout().getLibDirectory(getTargetDirectory(), getMavenProject().getArtifactId(), getMavenProject().getVersion(), getAOL().toString(), lib.getType());
        getLog().debug("Adding path to shared library: " + path);
        sharedPaths.add(path);
      }
    }
    String classifier = getAOL() + "-shared";
    List narArtifacts = getNarArtifacts();
    List dependencies = getNarManager().getAttachedNarDependencies(narArtifacts, classifier);
    for (Iterator d = dependencies.iterator(); d.hasNext(); ) {
      Artifact dependency = (Artifact) d.next();
      getLog().debug("Looking for dependency " + dependency);
      dependency.isSnapshot();
      File libDirectory = getLayout().getLibDirectory(getUnpackDirectory(), dependency.getArtifactId(), dependency.getBaseVersion(), getAOL().toString(), Library.SHARED);
      sharedPaths.add(libDirectory);
    }
    if (sharedPaths.size() > 0) {
      String sharedPath = "";
      for (Iterator i = sharedPaths.iterator(); i.hasNext(); ) {
        sharedPath += ((File) i.next()).getPath();
        if (i.hasNext()) {
          sharedPath += File.pathSeparator;
        }
      }
      String sharedEnv = NarUtil.addLibraryPathToEnv(sharedPath, null, getOS());
      env.add(sharedEnv);
    }
    if (getOS().equals(OS.WINDOWS)) {
      env.add("SystemRoot=" + NarUtil.getEnv("SystemRoot", "SystemRoot", "C:\\Windows"));
    }
    env.add("CLASSPATH=" + StringUtils.join(classpathElements.iterator(), File.pathSeparator));
    return env.size() > 0 ? (String[]) env.toArray(new String[env.size()]) : null;
  }
}