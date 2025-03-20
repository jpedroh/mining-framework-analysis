package com.github.maven_nar;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Keeps track of resources
 * 
 * @author Mark Donszelmann
 */
public abstract class AbstractResourcesMojo extends AbstractNarMojo {
  /**
     * Binary directory
     * 
     * @parameter default-value="bin"
     * @required
     */
  @Parameter(defaultValue = "bin", required = true) private String resourceBinDir;

  /**
     * Include directory
     * 
     * @parameter default-value="include"
     * @required
     */
  @Parameter(defaultValue = "include", required = true) private String resourceIncludeDir;

  /**
     * Library directory
     * 
     * @parameter default-value="lib"
     * @required
     */
  @Parameter(defaultValue = "lib", required = true) private String resourceLibDir;

  /**
     * To look up Archiver/UnArchiver implementations
     * 
     * @component role="org.codehaus.plexus.archiver.manager.ArchiverManager"
     * @required
     */
  @Component(role = org.codehaus.plexus.archiver.manager.ArchiverManager.class) private ArchiverManager archiverManager;

  protected final int copyIncludes(File srcDir) throws IOException, MojoExecutionException, MojoFailureException {
    int copied = 0;
    File includeDir = new File(srcDir, resourceIncludeDir);
    if (includeDir.exists()) {
      File includeDstDir = getLayout().getIncludeDirectory(getTargetDirectory(), getMavenProject().getArtifactId(), getMavenProject().getVersion());
      getLog().debug("Copying includes from " + includeDir + " to " + includeDstDir);
      copied += NarUtil.copyDirectoryStructure(includeDir, includeDstDir, null, NarUtil.DEFAULT_EXCLUDES);
    }
    return copied;
  }

  protected final int copyBinaries(File srcDir, String aol) throws IOException, MojoExecutionException, MojoFailureException {
    int copied = 0;
    File binDir = new File(srcDir, resourceBinDir);
    if (binDir.exists()) {
      File binDstDir = getLayout().getBinDirectory(getTargetDirectory(), getMavenProject().getArtifactId(), getMavenProject().getVersion(), aol);
      getLog().debug("Copying binaries from " + binDir + " to " + binDstDir);
      copied += NarUtil.copyDirectoryStructure(binDir, binDstDir, null, NarUtil.DEFAULT_EXCLUDES);
    }
    return copied;
  }

  protected final int copyLibraries(File srcDir, String aol) throws MojoFailureException, IOException, MojoExecutionException {
    int copied = 0;
    File libDir = new File(srcDir, resourceLibDir);
    if (libDir.exists()) {
      if (getLibraries().isEmpty()) {
        getLog().warn("Appear to have library resources, but not Libraries are defined");
      }
      for (Iterator i = getLibraries().iterator(); i.hasNext(); ) {
        Library library = (Library) i.next();
        String type = library.getType();
        File typedLibDir = new File(libDir, type);
        if (typedLibDir.exists()) {
          libDir = typedLibDir;
        }
        File libDstDir = getLayout().getLibDirectory(getTargetDirectory(), getMavenProject().getArtifactId(), getMavenProject().getVersion(), aol, type);
        getLog().debug("Copying libraries from " + libDir + " to " + libDstDir);
        String includes = "**/*." + NarProperties.getInstance(getMavenProject()).getProperty(NarUtil.getAOLKey(aol) + "." + type + ".extension");
        if (new AOL(aol).getOS().equals(OS.WINDOWS) && type.equals(Library.SHARED)) {
          includes += ",**/*.lib";
        }
        copied += NarUtil.copyDirectoryStructure(libDir, libDstDir, includes, NarUtil.DEFAULT_EXCLUDES);
      }
    }
    return copied;
  }

  protected final void copyResources(File srcDir, String aol) throws MojoExecutionException, MojoFailureException {
    int copied = 0;
    try {
      copied += copyIncludes(srcDir);
      copied += copyBinaries(srcDir, aol);
      copied += copyLibraries(srcDir, aol);
      File classesDirectory = new File(getOutputDirectory(), "classes");
      classesDirectory.mkdirs();
      List jars = FileUtils.getFiles(srcDir, "**/*.jar", null);
      for (Iterator i = jars.iterator(); i.hasNext(); ) {
        File jar = (File) i.next();
        getLog().debug("Unpacking jar " + jar);
        UnArchiver unArchiver;
        unArchiver = archiverManager.getUnArchiver(NarConstants.NAR_ROLE_HINT);
        unArchiver.setSourceFile(jar);
        unArchiver.setDestDirectory(classesDirectory);
        unArchiver.extract();
      }
    } catch (IOException e) {
      throw new MojoExecutionException("NAR: Could not copy resources for " + aol, e);
    } catch (NoSuchArchiverException e) {
      throw new MojoExecutionException("NAR: Could not find archiver for " + aol, e);
    } catch (ArchiverException e) {
      throw new MojoExecutionException("NAR: Could not unarchive jar file for " + aol, e);
    }
    getLog().info("Copied " + copied + " resources for " + aol);
  }
}