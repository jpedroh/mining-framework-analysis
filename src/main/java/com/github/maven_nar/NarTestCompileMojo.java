package com.github.maven_nar;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.github.maven_nar.cpptasks.CCTask;
import com.github.maven_nar.cpptasks.CUtil;
import com.github.maven_nar.cpptasks.CompilerDef;
import com.github.maven_nar.cpptasks.LinkerDef;
import com.github.maven_nar.cpptasks.OutputTypeEnum;
import com.github.maven_nar.cpptasks.RuntimeType;
import com.github.maven_nar.cpptasks.SubsystemEnum;
import com.github.maven_nar.cpptasks.types.LibrarySet;
import com.github.maven_nar.cpptasks.types.LibraryTypeEnum;
import com.github.maven_nar.cpptasks.types.LinkerArgument;
import com.github.maven_nar.cpptasks.types.SystemLibrarySet;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Compiles native test source files.
 * 
 * @goal nar-testCompile
 * @phase test-compile
 * @requiresDependencyResolution test
 * @author Mark Donszelmann
 */
@Mojo(name = "nar-testCompile", defaultPhase = LifecyclePhase.TEST_COMPILE, requiresDependencyResolution = ResolutionScope.TEST) public class NarTestCompileMojo extends AbstractCompileMojo {
  /**
     * Skip running of NAR integration test plugins.
     * 
     * @parameter property="skipNar" default-value="false"
     */
  @Parameter(property = "skipNar") protected boolean skipNar;

  @Override protected List getArtifacts() {
    return getMavenProject().getTestArtifacts();
  }

  protected File getUnpackDirectory() {
    return getTestUnpackDirectory() == null ? super.getUnpackDirectory() : getTestUnpackDirectory();
  }

  public final void narExecute() throws MojoExecutionException, MojoFailureException {
    if (skipTests) {
      getLog().info("Not compiling test sources");
    } else {
      super.narExecute();
      unpackAttachedNars(getAllAttachedNarArtifacts(getNarArtifacts()));
      getTestTargetDirectory().mkdirs();
      for (Iterator i = getTests().iterator(); i.hasNext(); ) {
        createTest(getAntProject(), (Test) i.next());
      }
    }
  }

  private void createTest(Project antProject, Test test) throws MojoExecutionException, MojoFailureException {
    String type = "test";
    CCTask task = new CCTask();
    task.setProject(antProject);
    SubsystemEnum subSystem = new SubsystemEnum();
    subSystem.setValue("console");
    task.setSubsystem(subSystem);
    OutputTypeEnum outTypeEnum = new OutputTypeEnum();
    outTypeEnum.setValue(Library.EXECUTABLE);
    task.setOuttype(outTypeEnum);
    File outDir = new File(getTestTargetDirectory(), "bin");
    outDir = new File(outDir, getAOL().toString());
    outDir.mkdirs();
    File outFile = new File(outDir, test.getName());
    getLog().debug("NAR - output: \'" + outFile + "\'");
    task.setOutfile(outFile);
    File objDir = new File(getTestTargetDirectory(), "obj");
    objDir = new File(objDir, getAOL().toString());
    objDir.mkdirs();
    task.setObjdir(objDir);
    task.setFailonerror(failOnError(getAOL()));
    task.setLibtool(useLibtool(getAOL()));
    RuntimeType runtimeType = new RuntimeType();
    runtimeType.setValue(getRuntime(getAOL()));
    task.setRuntime(runtimeType);
    Cpp cpp = getCpp();
    if (cpp != null) {
      CompilerDef cppCompiler = getCpp().getTestCompiler(type, test.getName());
      if (cppCompiler != null) {
        task.addConfiguredCompiler(cppCompiler);
      }
    }
    C c = getC();
    if (c != null) {
      CompilerDef cCompiler = c.getTestCompiler(type, test.getName());
      if (cCompiler != null) {
        task.addConfiguredCompiler(cCompiler);
      }
    }
    Fortran fortran = getFortran();
    if (fortran != null) {
      CompilerDef fortranCompiler = getFortran().getTestCompiler(type, test.getName());
      if (fortranCompiler != null) {
        task.addConfiguredCompiler(fortranCompiler);
      }
    }
    getJava().addIncludePaths(task, type);
    List depLibs = getNarArtifacts();
    for (Iterator i = depLibs.iterator(); i.hasNext(); ) {
      Artifact artifact = (Artifact) i.next();
      File include = getLayout().getIncludeDirectory(getUnpackDirectory(), artifact.getArtifactId(), artifact.getBaseVersion());
      if (!include.exists()) {
        include = getLayout().getIncludeDirectory(getTestUnpackDirectory(), artifact.getArtifactId(), artifact.getBaseVersion());
      }
      if (include.exists()) {
        task.createIncludePath().setPath(include.getPath());
      }
    }
    File jniIncludeDir = getJavah().getJniDirectory();
    if (jniIncludeDir.exists()) {
      task.createIncludePath().setPath(jniIncludeDir.getPath());
    }
    LinkerDef linkerDefinition = getLinker().getTestLinker(this, antProject, getOS(), getAOL().getKey() + ".linker.", type);
    task.addConfiguredLinker(linkerDefinition);
    File includeDir = getLayout().getIncludeDirectory(getTargetDirectory(), getMavenProject().getArtifactId(), getMavenProject().getVersion());
    File libDir = getLayout().getLibDirectory(getTargetDirectory(), getMavenProject().getArtifactId(), getMavenProject().getVersion(), getAOL().toString(), test.getLink());
    if (includeDir.exists()) {
      task.createIncludePath().setLocation(includeDir);
    }
    if (libDir.exists()) {
      LibrarySet libSet = new LibrarySet();
      libSet.setProject(antProject);
      String libs = getNarInfo().getLibs(getAOL());
      getLog().debug("Searching for parent to link with " + libs);
      libSet.setLibs(new CUtil.StringArrayBuilder(libs));
      LibraryTypeEnum libType = new LibraryTypeEnum();
      libType.setValue(test.getLink());
      libSet.setType(libType);
      libSet.setDir(libDir);
      task.addLibset(libSet);
    }
    List depLibOrder = getDependencyLibOrder();
    if ((depLibOrder != null) && !depLibOrder.isEmpty()) {
      List tmp = new LinkedList();
      for (Iterator i = depLibOrder.iterator(); i.hasNext(); ) {
        String depToOrderName = (String) i.next();
        for (Iterator j = depLibs.iterator(); j.hasNext(); ) {
          NarArtifact dep = (NarArtifact) j.next();
          String depName = dep.getGroupId() + ":" + dep.getArtifactId();
          if (depName.equals(depToOrderName)) {
            tmp.add(dep);
            j.remove();
          }
        }
      }
      tmp.addAll(depLibs);
      depLibs = tmp;
    }
    for (Iterator i = depLibs.iterator(); i.hasNext(); ) {
      NarArtifact dependency = (NarArtifact) i.next();
      String binding = dependency.getNarInfo().getBinding(getAOL(), Library.NONE);
      getLog().debug("Using Binding: " + binding);
      AOL aol = getAOL();
      aol = dependency.getNarInfo().getAOL(getAOL());
      getLog().debug("Using Library AOL: " + aol.toString());
      if (!binding.equals(Library.JNI) && !binding.equals(Library.NONE) && !binding.equals(Library.EXECUTABLE)) {
        File dir = getLayout().getLibDirectory(getUnpackDirectory(), dependency.getArtifactId(), dependency.getBaseVersion(), aol.toString(), binding);
        getLog().debug("Looking for Library Directory: " + dir);
        if (!dir.exists()) {
          getLog().debug("Library Directory " + dir + " does NOT exist.");
          dir = getLayout().getLibDirectory(getTestUnpackDirectory(), dependency.getArtifactId(), dependency.getBaseVersion(), aol.toString(), binding);
          getLog().debug("Looking for Library Directory: " + dir);
        }
        if (dir.exists()) {
          LibrarySet libSet = new LibrarySet();
          libSet.setProject(antProject);
          String libs = dependency.getNarInfo().getLibs(getAOL());
          if ((libs != null) && !libs.equals("")) {
            getLog().debug("Using LIBS = " + libs);
            libSet.setLibs(new CUtil.StringArrayBuilder(libs));
            libSet.setDir(dir);
            task.addLibset(libSet);
          }
        } else {
          getLog().debug("Library Directory " + dir + " does NOT exist.");
        }
        String options = dependency.getNarInfo().getOptions(getAOL());
        if ((options != null) && !options.equals("")) {
          getLog().debug("Using OPTIONS = " + options);
          LinkerArgument arg = new LinkerArgument();
          arg.setValue(options);
          linkerDefinition.addConfiguredLinkerArg(arg);
        }
        String sysLibs = dependency.getNarInfo().getSysLibs(getAOL());
        if ((sysLibs != null) && !sysLibs.equals("")) {
          getLog().debug("Using SYSLIBS = " + sysLibs);
          SystemLibrarySet sysLibSet = new SystemLibrarySet();
          sysLibSet.setProject(antProject);
          sysLibSet.setLibs(new CUtil.StringArrayBuilder(sysLibs));
          task.addSyslibset(sysLibSet);
        }
      }
    }
    getJava().addRuntime(task, getJavaHome(getAOL()), getOS(), getAOL().getKey() + ".java.");
    try {
      task.execute();
    } catch (BuildException e) {
      throw new MojoExecutionException("NAR: Test-Compile failed", e);
    }
  }
}