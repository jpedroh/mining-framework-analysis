package com.github.maven_nar;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SingleTargetSourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Sets up the javah configuration
 * 
 * @author Mark Donszelmann
 */
public class Javah {
  /**
     * Javah command to run.
     * 
     * @parameter default-value="javah"
     */
  @Parameter(defaultValue = "javah") private String name = "javah";

  /**
     * Add boot class paths. By default none.
     * 
     * @parameter
     */
  @Parameter private List bootClassPaths = new ArrayList();

  /**
     * Add class paths. By default the classDirectory directory is included and all dependent classes.
     * 
     * @parameter
     */
  @Parameter private List classPaths = new ArrayList();

  /**
     * The target directory into which to generate the output.
     * 
     * @parameter default-value="${project.build.directory}/nar/javah-include"
     * @required
     */
  @Parameter(defaultValue = "${project.build.directory}/nar/javah-include", required = true) private File jniDirectory;

  /**
     * The class directory to scan for class files with native interfaces.
     * 
     * @parameter default-value="${project.build.directory}/classes"
     * @required
     */
  @Parameter(defaultValue = "${project.build.directory}/classes", required = true) private File classDirectory;

  /**
     * The set of files/patterns to include Defaults to "**\/*.class"
     * 
     * @parameter
     */
  @Parameter private Set includes = new HashSet();

  /**
     * A list of exclusion filters.
     * 
     * @parameter
     */
  @Parameter private Set excludes = new HashSet();

  /**
     * A list of class names e.g. from java.sql.* that are also passed to javah.
     *
     * @parameter
     */
  @Parameter private Set extraClasses = new HashSet();

  /**
     * The granularity in milliseconds of the last modification date for testing whether a source needs recompilation
     * 
     * @parameter default-value="0"
     * @required
     */
  @Parameter(defaultValue = "0", required = true) private int staleMillis = 0;

  /**
     * The directory to store the timestampfile for the processed aid files. Defaults to jniDirectory.
     * 
     * @parameter
     */
  @Parameter private File timestampDirectory;

  /**
     * The timestampfile for the processed class files. Defaults to name of javah.
     * 
     * @parameter
     */
  @Parameter private File timestampFile;

  private AbstractNarMojo mojo;

  public Javah() {
  }

  public final void setAbstractCompileMojo(AbstractNarMojo abstractNarMojo) {
    this.mojo = abstractNarMojo;
  }

  protected final List getClassPaths() throws MojoExecutionException {
    if (classPaths.isEmpty()) {
      try {
        classPaths.addAll(mojo.getMavenProject().getCompileClasspathElements());
      } catch (DependencyResolutionRequiredException e) {
        throw new MojoExecutionException("JAVAH, cannot get classpath", e);
      }
    }
    return classPaths;
  }

  protected final File getJniDirectory() {
    if (jniDirectory == null) {
      jniDirectory = new File(mojo.getMavenProject().getBuild().getDirectory(), "nar/javah-include");
    }
    return jniDirectory;
  }

  protected final File getClassDirectory() {
    if (classDirectory == null) {
      classDirectory = new File(mojo.getMavenProject().getBuild().getDirectory(), "classes");
    }
    return classDirectory;
  }

  protected final Set getIncludes() {
    NarUtil.removeNulls(includes);
    if (includes.isEmpty()) {
      includes.add("**/*.class");
    }
    return includes;
  }

  protected final File getTimestampDirectory() {
    if (timestampDirectory == null) {
      timestampDirectory = getJniDirectory();
    }
    return timestampDirectory;
  }

  protected final File getTimestampFile() {
    if (timestampFile == null) {
      timestampFile = new File(name);
    }
    return timestampFile;
  }

  public final void execute() throws MojoExecutionException, MojoFailureException {
    getClassDirectory().mkdirs();
    try {
      SourceInclusionScanner scanner = new StaleSourceScanner(staleMillis, getIncludes(), excludes);
      if (getTimestampDirectory().exists()) {
        scanner.addSourceMapping(new SingleTargetSourceMapping(".class", getTimestampFile().getPath()));
      } else {
        scanner.addSourceMapping(new SuffixMapping(".class", ".dummy"));
      }
      Set classes = scanner.getIncludedSources(getClassDirectory(), getTimestampDirectory());
      if (!classes.isEmpty()) {
        Set files = new HashSet();
        for (Iterator i = classes.iterator(); i.hasNext(); ) {
          String file = ((File) i.next()).getPath();
          JavaClass clazz = NarUtil.getBcelClass(file);
          Method[] method = clazz.getMethods();
          for (int j = 0; j < method.length; j++) {
            if (method[j].isNative()) {
              files.add(clazz.getClassName());
            }
          }
        }
        if (!files.isEmpty()) {
          getJniDirectory().mkdirs();
          getTimestampDirectory().mkdirs();
          String javah = getJavah();
          mojo.getLog().info("Running " + javah + " compiler on " + files.size() + " classes...");
          int result = NarUtil.runCommand(javah, generateArgs(files), null, null, mojo.getLog());
          if (result != 0) {
            throw new MojoFailureException(javah + " failed with exit code " + result + " 0x" + Integer.toHexString(result));
          }
          FileUtils.fileWrite(getTimestampDirectory() + "/" + getTimestampFile(), "");
        }
      }
    } catch (InclusionScanException e) {
      throw new MojoExecutionException("JAVAH: Class scanning failed", e);
    } catch (IOException e) {
      throw new MojoExecutionException("JAVAH: IO Exception", e);
    } catch (ClassFormatException e) {
      throw new MojoExecutionException("JAVAH: Class could not be inspected", e);
    }
  }

  private String[] generateArgs(Set classes) throws MojoExecutionException {
    List args = new ArrayList();
    if (!bootClassPaths.isEmpty()) {
      args.add("-bootclasspath");
      args.add(StringUtils.join(bootClassPaths.iterator(), File.pathSeparator));
    }
    args.add("-classpath");
    args.add(StringUtils.join(getClassPaths().iterator(), File.pathSeparator));
    args.add("-d");
    args.add(getJniDirectory().getPath());
    if (mojo.getLog().isDebugEnabled()) {
      args.add("-verbose");
    }
    if (classes != null) {
      for (Iterator i = classes.iterator(); i.hasNext(); ) {
        args.add(i.next());
      }
    }
    if (extraClasses != null) {
      for (Iterator i = extraClasses.iterator(); i.hasNext(); ) {
        args.add(i.next());
      }
    }
    return (String[]) args.toArray(new String[args.size()]);
  }

  private String getJavah() throws MojoExecutionException, MojoFailureException {
    String javah = null;
    Toolchain toolchain = getToolchain();
    if (toolchain != null) {
      javah = toolchain.findTool("javah");
    }
    if (javah == null) {
      File javahFile = new File(mojo.getJavaHome(mojo.getAOL()), "bin");
      javah = new File(javahFile, name).getAbsolutePath();
    }
    if (javah == null) {
      throw new MojoExecutionException("NAR: Cannot find \'javah\' in Toolchain or on JavaHome");
    }
    return javah;
  }

  private Toolchain getToolchain() {
    Toolchain toolChain = null;
    ToolchainManager toolchainManager = ((NarJavahMojo) mojo).getToolchainManager();
    if (toolchainManager != null) {
      toolChain = toolchainManager.getToolchainFromBuildContext("jdk", ((NarJavahMojo) mojo).getSession());
    }
    return toolChain;
  }
}