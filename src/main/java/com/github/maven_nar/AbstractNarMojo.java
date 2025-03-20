package com.github.maven_nar;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * @author Mark Donszelmann
 */
public abstract class AbstractNarMojo extends AbstractMojo implements NarConstants {
  /**
     * Skip running of NAR plugins (any) altogether.
     * 
     * @parameter property="nar.skip" default-value="false"
     */
  @Parameter(property = "nar.skip", defaultValue = "false") private boolean skip;

  /**
     * Skip the tests. Listens to Maven's general 'maven.skip.test'.
     * 
     * @parameter property="maven.test.skip"
     */
  @Parameter(property = "maven.test.skip") boolean skipTests;

  /**
     * Ignore errors and failures.
     * 
     * @parameter property="nar.ignore" default-value="false"
     */
  @Parameter(property = "nar.ignore", defaultValue = "false") private boolean ignore;

  /**
     * The Architecture for the nar, Some choices are: "x86", "i386", "amd64", "ppc", "sparc", ... Defaults to a derived
     * value from ${os.arch}
     * 
     * @parameter property="nar.arch"
     */
  @Parameter(property = "nar.arch") private String architecture;

  /**
     * The Operating System for the nar. Some choices are: "Windows", "Linux", "MacOSX", "SunOS", ... Defaults to a
     * derived value from ${os.name} FIXME table missing
     * 
     * @parameter property="nar.os"
     */
  @Parameter(property = "nar.os") private String os;

  /**
     * Architecture-OS-Linker name. Defaults to: arch-os-linker.
     * 
     * @parameter default-value=""
     */
  @Parameter(defaultValue = "") private String aol;

  /**
     * Linker
     * 
     * @parameter default-value=""
     */
  @Parameter private Linker linker;

  /**
     * @parameter property="project.build.directory"
     * @readonly
     */
  @Parameter(property = "project.build.directory", readonly = true) private File outputDirectory;

  /**
     * @parameter property="project.build.outputDirectory"
     * @readonly
     */
  @Parameter(property = "project.build.outputDirectory", readonly = true) protected File classesDirectory;

  /**
     * Name of the output
     *  - for jni default-value="${project.artifactId}-${project.version}"
     *  - for libs default-value="${project.artifactId}-${project.version}"
     *  - for exe default-value="${project.artifactId}"
     *  -- for tests default-value="${test.name}"
     * 
     * @parameter 
     */
  @Parameter private String output;

  /**
     * @parameter property="project.basedir"
     * @readonly
     */
  @Parameter(property = "project.basedir", readonly = true) private File baseDir;

  /**
     * Target directory for Nar file construction. Defaults to "${project.build.directory}/nar" for "nar-compile" goal
     * 
     * @parameter default-value=""
     */
  @Parameter private File targetDirectory;

  /**
     * Target directory for Nar test construction. Defaults to "${project.build.directory}/test-nar" for "nar-testCompile" goal
     * 
     * @parameter default-value=""
     */
  @Parameter private File testTargetDirectory;

  /**
     * Target directory for Nar file unpacking. Defaults to "${targetDirectory}"
     * 
     * @parameter default-value=""
     */
  @Parameter private File unpackDirectory;

  /**
     * Target directory for Nar test unpacking. Defaults to "${testTargetDirectory}"
     * 
     * @parameter default-value=""
     */
  @Parameter private File testUnpackDirectory;

  /**
     * List of classifiers which you want download/unpack/assemble 
     * Example ppc-MacOSX-g++, x86-Windows-msvc, i386-Linux-g++.
     * Not setting means all.
     * 
     * @parameter default-value=""
     */
  @Parameter protected List<String> classifiers;

  /**
     * List of libraries to create
     *
     * @parameter default-value=""
     */
  @Parameter protected List<Library> libraries;

  /**
     * Layout to be used for building and unpacking artifacts
     * 
     * @parameter property="nar.layout" default-value="com.github.maven_nar.NarLayout21"
     * @required
     */
  @Parameter(property = "nar.layout", defaultValue = "com.github.maven_nar.NarLayout21", required = true) private String layout;

  private NarLayout narLayout;

  /**
     * @parameter property="project"
     * @readonly
     * @required
     */
  @Component private MavenProject mavenProject;

  private AOL aolId;

  private NarInfo narInfo;

  /**
     * Javah info
     * 
     * @parameter default-value=""
     */
  @Parameter private Javah javah;

  /**
     * The home of the Java system. Defaults to a derived value from ${java.home} which is OS specific.
     * 
     * @parameter default-value=""
     * @readonly
     */
  @Parameter(readonly = true) private File javaHome;

  protected final void validate() throws MojoFailureException, MojoExecutionException {
    linker = NarUtil.getLinker(linker, getLog());
    architecture = NarUtil.getArchitecture(architecture);
    os = NarUtil.getOS(os);
    aolId = NarUtil.getAOL(mavenProject, architecture, os, linker, aol, getLog());
    Model model = mavenProject.getModel();
    Properties properties = model.getProperties();
    properties.setProperty("nar.arch", getArchitecture());
    properties.setProperty("nar.os", getOS());
    properties.setProperty("nar.linker", getLinker().getName());
    properties.setProperty("nar.aol", aolId.toString());
    properties.setProperty("nar.aol.key", aolId.getKey());
    model.setProperties(properties);
    if (targetDirectory == null) {
      targetDirectory = new File(mavenProject.getBuild().getDirectory(), "nar");
    }
    if (testTargetDirectory == null) {
      testTargetDirectory = new File(mavenProject.getBuild().getDirectory(), "test-nar");
    }
    if (unpackDirectory == null) {
      unpackDirectory = targetDirectory;
    }
    if (testUnpackDirectory == null) {
      testUnpackDirectory = testTargetDirectory;
    }
  }

  protected final String getOutput(boolean versioned) throws MojoExecutionException {
    if (output != null && !output.trim().isEmpty()) {
      return output;
    } else {
      if (versioned) {
        return getMavenProject().getArtifactId() + "-" + getMavenProject().getVersion();
      } else {
        return getMavenProject().getArtifactId();
      }
    }
  }

  protected final String getArchitecture() {
    return architecture;
  }

  protected final String getOS() {
    return os;
  }

  protected final AOL getAOL() throws MojoFailureException, MojoExecutionException {
    return aolId;
  }

  protected final Linker getLinker() {
    return linker;
  }

  protected final File getBasedir() {
    return baseDir;
  }

  protected final File getOutputDirectory() {
    return outputDirectory;
  }

  protected final File getTargetDirectory() {
    return targetDirectory;
  }

  protected final File getTestTargetDirectory() {
    return testTargetDirectory;
  }

  protected File getUnpackDirectory() {
    return unpackDirectory;
  }

  protected final File getTestUnpackDirectory() {
    return testUnpackDirectory;
  }

  protected final NarLayout getLayout() throws MojoExecutionException {
    if (narLayout == null) {
      narLayout = AbstractNarLayout.getLayout(layout, getLog());
    }
    return narLayout;
  }

  protected final MavenProject getMavenProject() {
    return mavenProject;
  }

  public final void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      getLog().info(getClass().getName() + " skipped");
      return;
    }
    try {
      validate();
      narExecute();
    } catch (MojoFailureException mfe) {
      if (ignore) {
        getLog().warn("IGNORED: " + mfe.getMessage());
      } else {
        throw mfe;
      }
    } catch (MojoExecutionException mee) {
      if (ignore) {
        getLog().warn("IGNORED: " + mee.getMessage());
      } else {
        throw mee;
      }
    }
  }

  public abstract void narExecute() throws MojoFailureException, MojoExecutionException;

  protected NarInfo getNarInfo() throws MojoExecutionException {
    if (narInfo == null) {
      String groupId = getMavenProject().getGroupId();
      String artifactId = getMavenProject().getArtifactId();
      String path = "META-INF/nar/" + groupId + "/" + artifactId + "/" + NarInfo.NAR_PROPERTIES;
      File propertiesFile = new File(classesDirectory, path);
      if (!propertiesFile.exists()) {
        propertiesFile = new File(getMavenProject().getBasedir(), "src/main/resources/" + path);
      }
      narInfo = new NarInfo(groupId, artifactId, getMavenProject().getVersion(), getLog(), propertiesFile);
    }
    return narInfo;
  }

  protected final List<Library> getLibraries() {
    if (libraries == null) {
      libraries = Collections.EMPTY_LIST;
    }
    return libraries;
  }

  protected final Javah getJavah() {
    if (javah == null) {
      javah = new Javah();
    }
    javah.setAbstractCompileMojo(this);
    return javah;
  }

  protected final File getJavaHome(AOL aol) throws MojoExecutionException {
    return getNarInfo().getProperty(aol, "javaHome", NarUtil.getJavaHome(javaHome, getOS()));
  }
}