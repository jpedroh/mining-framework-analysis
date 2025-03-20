package com.github.maven_nar.cpptasks;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Environment;
import org.apache.commons.io.FilenameUtils;
import com.github.maven_nar.cpptasks.compiler.CompilerConfiguration;
import com.github.maven_nar.cpptasks.compiler.LinkType;
import com.github.maven_nar.cpptasks.compiler.Linker;
import com.github.maven_nar.cpptasks.compiler.LinkerConfiguration;
import com.github.maven_nar.cpptasks.compiler.Processor;
import com.github.maven_nar.cpptasks.compiler.ProcessorConfiguration;
import com.github.maven_nar.cpptasks.compiler.AbstractCompiler;
import com.github.maven_nar.cpptasks.compiler.CommandLineCompilerConfiguration;
import com.github.maven_nar.cpptasks.ide.ProjectDef;
import com.github.maven_nar.cpptasks.types.CompilerArgument;
import com.github.maven_nar.cpptasks.types.ConditionalFileSet;
import com.github.maven_nar.cpptasks.types.DefineSet;
import com.github.maven_nar.cpptasks.types.IncludePath;
import com.github.maven_nar.cpptasks.types.LibrarySet;
import com.github.maven_nar.cpptasks.types.LinkerArgument;
import com.github.maven_nar.cpptasks.types.SystemIncludePath;
import com.github.maven_nar.cpptasks.types.SystemLibrarySet;

/**
 * Compile and link task.
 * 
 * <p>
 * This task can compile various source languages and produce executables,
 * shared libraries (aka DLL's) and static libraries. Compiler adaptors are
 * currently available for several C/C++ compilers, FORTRAN, MIDL and Windows
 * Resource files.
 * </p>
 * 
 * @author Adam Murdoch
 * @author Curt Arnold
 */
public class CCTask extends Task {
  class Core extends Thread {
    private final CCTask task;

    private final CompilerConfiguration config;

    private final File objDir;

    private final List<String> sourceFiles;

    private final boolean relentless;

    private final CCTaskProgressMonitor monitor;

    private Exception compileException;

    Core(final CCTask task, final int coreNo, final CompilerConfiguration config, final File objDir, final List<String> set, final boolean relentless, final CCTaskProgressMonitor monitor) {
      super("Core " + coreNo);
      this.task = task;
      this.config = config;
      this.objDir = objDir;
      this.sourceFiles = set;
      this.relentless = relentless;
      this.monitor = monitor;
    }

    public Exception getException() {
      return this.compileException;
    }

    @Override public void run() {
      super.run();
      try {
        String[] sources = new String[this.sourceFiles.size()];
        sources = this.sourceFiles.toArray(sources);
        this.config.compile(this.task, this.objDir, sources, this.relentless, this.monitor);
      } catch (final Exception ex) {
        if (this.compileException == null) {
          this.compileException = ex;
        }
      }
    }
  }

  class Progress extends Thread {
    private boolean stop = false;

    private final File objDir;

    private final int rebuildCount;

    public Progress(final File objDir, final int rebuildCount) {
      this.objDir = objDir;
      this.rebuildCount = rebuildCount;
    }

    public void exit() {
      this.stop = true;
    }

    @Override public void run() {
      if (this.rebuildCount < 10) {
        return;
      }
      try {
        final FileFilter updatedFiles = new FileFilter() {
          private final long startTime = System.currentTimeMillis();

          @Override public boolean accept(final File file) {
            return file.lastModified() > this.startTime && !file.getName().endsWith(".xml");
          }
        };
        while (!this.stop) {
          System.err.print("\r" + this.objDir.listFiles(updatedFiles).length + " / " + this.rebuildCount + " files compiled...");
          System.err.print("\r");
          System.err.flush();
          if (!this.stop) {
            Thread.sleep(5000);
          }
        }
      } catch (final InterruptedException e) {
      }
      System.err.print("\r                                                                    ");
      System.err.print("\r");
      System.err.flush();
      log(Integer.toString(this.rebuildCount) + " files were compiled.");
    }
  }

  private static class ProjectFileCollector implements FileVisitor {
    private final List<File> files;

    /**
     * Creates a new ProjectFileCollector.
     * 
     * @param files
     *          vector for collected files.
     */
    public ProjectFileCollector(final List<File> files) {
      this.files = files;
    }

    /**
     * Called for each file to be considered for collection.
     * 
     * @param parentDir
     *          parent directory
     * @param filename
     *          filename within directory
     */
    @Override public void visit(final File parentDir, final String filename) {
      this.files.add(new File(parentDir, filename));
    }
  }

  private static class SystemLibraryCollector implements FileVisitor {
    private final Hashtable<String, File> libraries;

    private final Linker linker;

    public SystemLibraryCollector(final Linker linker, final Hashtable<String, File> libraries) {
      this.linker = linker;
      this.libraries = libraries;
    }

    @Override public void visit(final File basedir, final String filename) {
      if (this.linker.bid(filename) > 0) {
        final File libfile = new File(basedir, filename);
        final String key = this.linker.getLibraryKey(libfile);
        this.libraries.put(key, libfile);
      }
    }
  }

  private static final ProcessorConfiguration[] EMPTY_CONFIG_ARRAY = new ProcessorConfiguration[0];

  /**
   * Builds a Hashtable to targets needing to be rebuilt keyed by compiler
   * configuration
   */
  public static Map<CompilerConfiguration, Vector<TargetInfo>> getTargetsToBuildByConfiguration(final Map<String, TargetInfo> targets) {
    final Map<CompilerConfiguration, Vector<TargetInfo>> targetsByConfig = new HashMap<>();
    for (final TargetInfo target : targets.values()) {
      if (target.getRebuild()) {
        Vector<TargetInfo> targetsForSameConfig = targetsByConfig.get(target.getConfiguration());
        if (targetsForSameConfig != null) {
          targetsForSameConfig.addElement(target);
        } else {
          targetsForSameConfig = new Vector<>();
          targetsForSameConfig.addElement(target);
          targetsByConfig.put((CompilerConfiguration) target.getConfiguration(), targetsForSameConfig);
        }
      }
    }
    return targetsByConfig;
  }

  private int maxCores = 0;

  private boolean ordered = false;

  /** The compiler definitions. */
  private final Vector<CompilerDef> _compilers = new Vector<>();

  /** The library sets. */
  private final Vector _libsets = new Vector();

  /** The linker definitions. */
  private final Vector<LinkerDef> _linkers = new Vector<>();

  /** The object directory. */
  private File _objDir;

  /** The output file. */
  private File _outfile;

  /** The linker definitions. */
  private final Vector<TargetDef> targetPlatforms = new Vector<>();

  /** The distributer definitions. */
  private final Vector<DistributerDef> distributers = new Vector<>();

  private final Vector<VersionInfo> versionInfos = new Vector<>();

  private final Vector<ProjectDef> projects = new Vector<>();

  private boolean projectsOnly = false;

  private boolean decorateLinkerOptions = true;

  /**
   * If true, stop build on compile failure.
   */
  protected boolean failOnError = true;

  /**
   * Content that appears in <cc>and also in <compiler>are maintained by a
   * captive CompilerDef instance
   */
  private final CompilerDef compilerDef = new CompilerDef();

  /** The OS390 dataset to build to object to */
  private String dataset;

  /**
   * 
   * Depth of dependency checking
   * 
   * Values < 0 indicate full dependency checking Values >= 0 indicate
   * partial dependency checking and for superficial compilation checks. Will
   * throw BuildException before attempting link
   */
  private int dependencyDepth = -1;

  /**
   * Content that appears in <cc>and also in <linker>are maintained by a
   * captive CompilerDef instance
   */
  private final LinkerDef linkerDef = new LinkerDef();

  /**
   * contains the subsystem, output type and
   * 
   */
  private final LinkType linkType = new LinkType();

  /**
   * The property name which will be set with the physical filename of the
   * file that is generated by the linker
   */
  private String outputFileProperty;

  /**
   * if relentless = true, compilations should attempt to compile as many
   * files as possible before throwing a BuildException
   */
  private boolean relentless;

  /**
   * At which log-level do we log command-lines in the build?
   */
  private int commandLogLevel = Project.MSG_VERBOSE;

  public CCTask() {
  }

  /**
   * Adds a compiler definition or reference.
   * 
   * @param compiler
   *          compiler
   * @throws NullPointerException
   *           if compiler is null
   */
  public void addConfiguredCompiler(final CompilerDef compiler) {
    if (compiler == null) {
      throw new NullPointerException("compiler");
    }
    compiler.setProject(getProject());
    this._compilers.addElement(compiler);
  }

  /**
   * Adds a compiler command-line arg. Argument will be inherited by all
   * nested compiler elements that do not have inherit="false".
   * 
   */
  public void addConfiguredCompilerArg(final CompilerArgument arg) {
    this.compilerDef.addConfiguredCompilerArg(arg);
  }

  /**
   * Adds a defineset. Will be inherited by all compiler elements that do not
   * have inherit="false".
   * 
   * @param defs
   *          Define set
   */
  public void addConfiguredDefineset(final DefineSet defs) {
    this.compilerDef.addConfiguredDefineset(defs);
  }

  /**
   * Adds a distributer definition or reference (Non-functional prototype).
   * 
   * @param distributer
   *          distributer
   * @throws NullPointerException
   *           if compiler is null
   */
  public void addConfiguredDistributer(final DistributerDef distributer) {
    if (distributer == null) {
      throw new NullPointerException("distributer");
    }
    distributer.setProject(getProject());
    this.distributers.addElement(distributer);
  }

  /**
   * Adds a linker definition. The first linker that is not disqualified by
   * its "if" and "unless" attributes will perform the link. If no child
   * linker element is active, the linker implied by the cc elements name or
   * classname attribute will be used.
   * 
   * @param linker
   *          linker
   * @throws NullPointerException
   *           if linker is null
   */
  public void addConfiguredLinker(final LinkerDef linker) {
    if (linker == null) {
      throw new NullPointerException("linker");
    }
    linker.setProject(getProject());
    this._linkers.addElement(linker);
  }

  /**
   * Adds a linker command-line arg. Argument will be inherited by all nested
   * linker elements that do not have inherit="false".
   */
  public void addConfiguredLinkerArg(final LinkerArgument arg) {
    this.linkerDef.addConfiguredLinkerArg(arg);
  }

  /**
   * Adds a target definition or reference (Non-functional prototype).
   * 
   * @param target
   *          target
   * @throws NullPointerException
   *           if compiler is null
   */
  public void addConfiguredTarget(final TargetDef target) {
    if (target == null) {
      throw new NullPointerException("target");
    }
    target.setProject(getProject());
    this.targetPlatforms.addElement(target);
  }

  /**
   * Adds desriptive version information to be included in the
   * generated file. The first active version info block will
   * be used.
   */
  public void addConfiguredVersioninfo(final VersionInfo newVersionInfo) {
    newVersionInfo.setProject(this.getProject());
    this.versionInfos.addElement(newVersionInfo);
  }

  /**
   * Add an environment variable to the launched process.
   */
  public void addEnv(final Environment.Variable var) {
    this.compilerDef.addEnv(var);
    for (int i = 0; i < this._compilers.size(); i++) {
      final CompilerDef currentCompilerDef = this._compilers.elementAt(i);
      currentCompilerDef.addEnv(var);
    }
    this.linkerDef.addEnv(var);
  }

  /**
   * Adds a source file set.
   * 
   * Files in these filesets will be auctioned to the available compiler
   * configurations, with the default compiler implied by the cc element
   * bidding last. If no compiler is interested in the file, it will be
   * passed to the linker.
   * 
   * To have a file be processed by a particular compiler configuration, add
   * a fileset to the corresponding compiler element.
   */
  public void addFileset(final ConditionalFileSet srcSet) {
    this.compilerDef.addFileset(srcSet);
  }

  /**
   * Adds a library set.
   * 
   * Library sets will be inherited by all linker elements that do not have
   * inherit="false".
   * 
   * @param libset
   *          library set
   * @throws NullPointerException
   *           if libset is null.
   */
  public void addLibset(final LibrarySet libset) {
    if (libset == null) {
      throw new NullPointerException("libset");
    }
    this.linkerDef.addLibset(libset);
  }

  /**
   * Specifies the generation of IDE project file. Experimental.
   * 
   * @param projectDef
   *          project file generation specification
   */
  public void addProject(final ProjectDef projectDef) {
    if (projectDef == null) {
      throw new NullPointerException("projectDef");
    }
    this.projects.addElement(projectDef);
  }

  /**
   * Adds a system library set. Timestamps and locations of system library
   * sets are not used in dependency analysis.
   * 
   * Essential libraries (such as C Runtime libraries) should not be
   * specified since the task will attempt to identify the correct libraries
   * based on the multithread, debug and runtime attributes.
   * 
   * System library sets will be inherited by all linker elements that do not
   * have inherit="false".
   * 
   * @param libset
   *          library set
   * @throws NullPointerException
   *           if libset is null.
   */
  public void addSyslibset(final SystemLibrarySet libset) {
    if (libset == null) {
      throw new NullPointerException("libset");
    }
    this.linkerDef.addSyslibset(libset);
  }

  /**
   * Checks all targets that are not forced to be rebuilt or are missing
   * object files to be checked for modified include files
   * 
   * @return total number of targets to be rebuilt
   * 
   */
  protected int checkForChangedIncludeFiles(final Map<String, TargetInfo> targets) {
    int potentialTargets = 0;
    int definiteTargets = 0;
    Iterator<TargetInfo> targetEnum = targets.values().iterator();
    while (targetEnum.hasNext()) {
      final TargetInfo target = targetEnum.next();
      if (!target.getRebuild()) {
        potentialTargets++;
      } else {
        definiteTargets++;
      }
    }
    if (potentialTargets > 0) {
      log("Starting dependency analysis for " + Integer.toString(potentialTargets) + " files.");
      final DependencyTable dependencyTable = new DependencyTable(this._objDir);
      try {
        dependencyTable.load();
      } catch (final Exception ex) {
        log("Problem reading dependencies.xml: " + ex.toString());
      }
      targetEnum = targets.values().iterator();
      while (targetEnum.hasNext()) {
        final TargetInfo target = targetEnum.next();
        if (!target.getRebuild() && dependencyTable.needsRebuild(this, target, this.dependencyDepth)) {
          target.mustRebuild();
        }
      }
      dependencyTable.commit(this);
    }
    int currentTargets = 0;
    targetEnum = targets.values().iterator();
    while (targetEnum.hasNext()) {
      final TargetInfo target = targetEnum.next();
      if (target.getRebuild()) {
        currentTargets++;
      }
    }
    if (potentialTargets > 0) {
      log(Integer.toString(potentialTargets - currentTargets + definiteTargets) + " files are up to date.");
      log(Integer.toString(currentTargets - definiteTargets) + " files to be recompiled from dependency analysis.");
    }
    log(Integer.toString(currentTargets) + " total files to be compiled.");
    return currentTargets;
  }

  protected LinkerConfiguration collectExplicitObjectFiles(final Vector<File> objectFiles, final Vector<File> sysObjectFiles, final VersionInfo versionInfo) {
    ProcessorConfiguration linkerConfig = null;
    LinkerDef selectedLinkerDef = null;
    Linker selectedLinker = null;
    final Hashtable<String, File> sysLibraries = new Hashtable<>();
    final TargetDef targetPlatform = getTargetPlatform();
    FileVisitor objCollector = null;
    FileVisitor sysLibraryCollector = null;
    for (int i = 0; i < this._linkers.size(); i++) {
      final LinkerDef currentLinkerDef = this._linkers.elementAt(i);
      if (currentLinkerDef.isActive()) {
        selectedLinkerDef = currentLinkerDef;
        selectedLinker = currentLinkerDef.getProcessor().getLinker(this.linkType);
        if (selectedLinker != null) {
          linkerConfig = currentLinkerDef.createConfiguration(this, this.linkType, this.linkerDef, targetPlatform, versionInfo);
          if (linkerConfig != null) {
            objCollector = new ObjectFileCollector(selectedLinker, objectFiles);
            sysLibraryCollector = new SystemLibraryCollector(selectedLinker, sysLibraries);
            if (currentLinkerDef.hasFileSets()) {
              currentLinkerDef.visitFiles(objCollector);
            }
            selectedLinkerDef.visitUserLibraries(selectedLinker, objCollector);
          }
          break;
        }
      }
    }
    if (linkerConfig == null) {
      linkerConfig = this.linkerDef.createConfiguration(this, this.linkType, null, targetPlatform, versionInfo);
      selectedLinker = this.linkerDef.getProcessor().getLinker(this.linkType);
      objCollector = new ObjectFileCollector(selectedLinker, objectFiles);
      sysLibraryCollector = new SystemLibraryCollector(selectedLinker, sysLibraries);
    }
    if (selectedLinkerDef == null || selectedLinkerDef.getInherit()) {
      this.linkerDef.visitUserLibraries(selectedLinker, objCollector);
      this.linkerDef.visitSystemLibraries(selectedLinker, sysLibraryCollector);
    }
    if (selectedLinkerDef != null) {
      selectedLinkerDef.visitSystemLibraries(selectedLinker, sysLibraryCollector);
    }
    final Enumeration<File> sysLibEnum = sysLibraries.elements();
    while (sysLibEnum.hasMoreElements()) {
      sysObjectFiles.addElement(sysLibEnum.nextElement());
    }
    return (LinkerConfiguration) linkerConfig;
  }

  /**
   * Adds an include path.
   * 
   * Include paths will be inherited by nested compiler elements that do not
   * have inherit="false".
   */
  public IncludePath createIncludePath() {
    return this.compilerDef.createIncludePath();
  }

  /**
   * Specifies precompilation prototype file and exclusions. Inherited by all
   * compilers that do not have inherit="false".
   * 
   */
  public PrecompileDef createPrecompile() throws BuildException {
    return this.compilerDef.createPrecompile();
  }

  /**
   * Adds a system include path. Locations and timestamps of files located
   * using the system include paths are not used in dependency analysis.
   * 
   * 
   * Standard include locations should not be specified. The compiler
   * adapters should recognized the settings from the appropriate environment
   * variables or configuration files.
   * 
   * System include paths will be inherited by nested compiler elements that
   * do not have inherit="false".
   */
  public SystemIncludePath createSysIncludePath() {
    return this.compilerDef.createSysIncludePath();
  }

  /**
   * Executes the task. Compiles the given files.
   * 
   * @throws BuildException
   *           if someting goes wrong with the build
   */
  @Override public void execute() throws BuildException {
    if (this._objDir == null) {
      if (this._outfile != null) {
        this._objDir = new File(this._outfile.getParent());
      } else {
        this._objDir = new File(".");
      }
    }
    if (!this._objDir.exists()) {
      throw new BuildException("Object directory does not exist");
    }
    final TargetHistoryTable objHistory = new TargetHistoryTable(this, this._objDir);
    VersionInfo versionInfo = null;
    final Enumeration<VersionInfo> versionEnum = this.versionInfos.elements();
    while (versionEnum.hasMoreElements()) {
      versionInfo = versionEnum.nextElement();
      versionInfo = versionInfo.merge();
      if (versionInfo.isActive()) {
        break;
      } else {
        versionInfo = null;
      }
    }
    final Vector<File> objectFiles = new Vector<>();
    final Vector<File> sysObjectFiles = new Vector<>();
    final LinkerConfiguration linkerConfig = collectExplicitObjectFiles(objectFiles, sysObjectFiles, versionInfo);
    final Map<String, TargetInfo> targets = getTargets(linkerConfig, objectFiles, versionInfo, this._outfile);
    TargetInfo linkTarget = null;
    if (this._outfile != null) {
      linkTarget = getLinkTarget(linkerConfig, objectFiles, sysObjectFiles, targets, versionInfo);
    }
    if (this.projects.size() > 0) {
      final List<File> files = new ArrayList<>();
      final ProjectFileCollector matcher = new ProjectFileCollector(files);
      for (int i = 0; i < this._compilers.size(); i++) {
        final CompilerDef currentCompilerDef = this._compilers.elementAt(i);
        if (currentCompilerDef.isActive() && currentCompilerDef.hasFileSets()) {
          currentCompilerDef.visitFiles(matcher);
        }
      }
      this.compilerDef.visitFiles(matcher);
      final Enumeration<ProjectDef> iter = this.projects.elements();
      while (iter.hasMoreElements()) {
        final ProjectDef projectDef = iter.nextElement();
        if (projectDef.isActive()) {
          projectDef.execute(this, files, targets, linkTarget);
        }
      }
    }
    if (this.projectsOnly) {
      return;
    }
    objHistory.markForRebuild(targets);
    final CCTaskProgressMonitor monitor = new CCTaskProgressMonitor(objHistory, versionInfo);
    final int rebuildCount = checkForChangedIncludeFiles(targets);
    if (rebuildCount > 0) {
      BuildException compileException = null;
      final Map<CompilerConfiguration, Vector<TargetInfo>> targetsByConfig = getTargetsToBuildByConfiguration(targets);
      final ArrayList<Vector<TargetInfo>> targetVectorsPreComp = new ArrayList<>();
      final ArrayList<Vector<TargetInfo>> targetVectors = new ArrayList<>();
      int index = 0;
      for (final Map.Entry<CompilerConfiguration, Vector<TargetInfo>> targetsForConfig : targetsByConfig.entrySet()) {
        final CompilerConfiguration config = targetsForConfig.getKey();
        if (config.isPrecompileGeneration()) {
          targetVectorsPreComp.add(targetsForConfig.getValue());
        } else {
          targetVectors.add(targetsForConfig.getValue());
        }
      }
      final Progress progress = new Progress(getObjdir(), rebuildCount);
      progress.start();
      compileException = runTargetPool(monitor, compileException, targetVectorsPreComp);
      if (compileException == null || this.relentless) {
        compileException = runTargetPool(monitor, compileException, targetVectors);
      }
      progress.exit();
      try {
        progress.join();
      } catch (final InterruptedException ex) {
      }
      try {
        objHistory.commit();
      } catch (final IOException ex) {
        this.log("Error writing history.xml: " + ex.toString());
      }
      if (compileException != null) {
        if (this.failOnError) {
          throw compileException;
        } else {
          log(compileException.getMessage(), Project.MSG_ERR);
          return;
        }
      }
    }
    if (this.dependencyDepth >= 0) {
      throw new BuildException("All files at depth " + Integer.toString(this.dependencyDepth) + " from changes successfully compiled.\n" + "Remove or change dependencyDepth to -1 to perform full compilation.");
    }
    if (linkTarget != null) {
      final TargetHistoryTable linkHistory = getLinkHistory(objHistory);
      linkHistory.markForRebuild(linkTarget);
      final File output = linkTarget.getOutput();
      if (linkTarget.getRebuild()) {
        final LinkerConfiguration linkConfig = (LinkerConfiguration) linkTarget.getConfiguration();
        log("Linking...");
        log("Starting link {" + linkConfig.getIdentifier() + "}");
        if (this.failOnError) {
          linkConfig.link(this, linkTarget);
        } else {
          try {
            linkConfig.link(this, linkTarget);
          } catch (final BuildException ex) {
            log(ex.getMessage(), Project.MSG_ERR);
            return;
          }
        }
        if (this.outputFileProperty != null) {
          getProject().setProperty(this.outputFileProperty, output.getAbsolutePath());
        }
        linkHistory.update(linkTarget);
        try {
          linkHistory.commit();
        } catch (final IOException ex) {
          log("Error writing link history.xml: " + ex.toString());
        }
      } else {
        if (this.outputFileProperty != null) {
          getProject().setProperty(this.outputFileProperty, output.getAbsolutePath());
        }
      }
    }
  }

  private BuildException runTargetPool(final CCTaskProgressMonitor monitor, BuildException compileException, final ArrayList<Vector<TargetInfo>> targetVectors) {
    int index;
    for (final Vector<TargetInfo> targetsForConfig : targetVectors) {
      final CompilerConfiguration config = (CompilerConfiguration) targetsForConfig.elementAt(0).getConfiguration();
      int noOfCores = Runtime.getRuntime().availableProcessors();
      log("Found " + noOfCores + " processors available");
      if (this.maxCores > 0) {
        noOfCores = Math.min(this.maxCores, noOfCores);
        log("Limited processors to " + noOfCores);
      }
      final int noOfFiles = targetsForConfig.size();
      if (noOfFiles < noOfCores) {
        noOfCores = noOfFiles;
        log("Limited used processors to " + noOfCores);
      }
      if (this.ordered) {
        noOfCores = 1;
        log("Limited processors to 1 due to ordering of source files");
      }
      final List<String>[] sourceFiles = new List[noOfCores];
      for (int j = 0; j < sourceFiles.length; j++) {
        sourceFiles[j] = new ArrayList<>(noOfFiles / sourceFiles.length);
      }
      final Enumeration<TargetInfo> targetsEnum = targetsForConfig.elements();
      index = 0;
      while (targetsEnum.hasMoreElements()) {
        final TargetInfo targetInfo = targetsEnum.nextElement();
        sourceFiles[index++].add(targetInfo.getSources()[0].toString());
        index %= sourceFiles.length;
      }
      final Core[] cores = new Core[noOfCores];
      for (int j = 0; j < cores.length; j++) {
        cores[j] = new Core(this, j, config, this._objDir, sourceFiles[j], this.relentless, monitor);
        log("\nStarting Core " + j + " with " + sourceFiles[j].size() + " source files...");
      }
      for (final Core core : cores) {
        core.start();
      }
      boolean alive = false;
      try {
        do {
          alive = false;
          for (int j = 0; j < cores.length; j++) {
            if (cores[j] != null) {
              if (cores[j].isAlive()) {
                alive = true;
              } else {
                final Exception exception = cores[j].getException();
                if (exception != null) {
                  if (compileException == null && exception instanceof BuildException) {
                    compileException = (BuildException) exception;
                  } else {
                    log(cores[j].getName() + " " + exception + "                                  ", Project.MSG_ERR);
                  }
                  if (!this.relentless) {
                    cores[j] = null;
                    alive = false;
                    break;
                  }
                }
                cores[j] = null;
              }
            }
          }
          if (alive) {
            Thread.sleep(Math.min(5000, sourceFiles[0].size() * 2000));
          }
        } while(alive);
      } catch (final InterruptedException e) {
        break;
      }
      for (final Core core : cores) {
        if (core != null) {
          core.interrupt();
          log(core.getName() + " interrupted                                          ");
        }
      }
      if (!this.relentless && compileException != null) {
        break;
      }
    }
    return compileException;
  }

  /**
   * Get the commandLogLevel
   * 
   * @return The current commandLogLevel
   */
  public int getCommandLogLevel() {
    return this.commandLogLevel;
  }

  /**
   * Gets the dataset.
   * 
   * @return Returns a String
   */
  public String getDataset() {
    return this.dataset;
  }

  /**
   * Gets debug state.
   * 
   * @return true if building for debugging
   */
  public boolean getDebug() {
    return this.compilerDef.getDebug(null, 0);
  }

  /**
   * Gets the failonerror flag.
   * 
   * @return the failonerror flag
   */
  public boolean getFailonerror() {
    return this.failOnError;
  }

  protected TargetHistoryTable getLinkHistory(final TargetHistoryTable objHistory) {
    final File outputFileDir = new File(this._outfile.getParent());
    if (this._objDir.equals(outputFileDir)) {
      return objHistory;
    }
    return new TargetHistoryTable(this, outputFileDir);
  }

  protected TargetInfo getLinkTarget(final LinkerConfiguration linkerConfig, final Vector<File> objectFiles, final Vector<File> sysObjectFiles, final Map<String, TargetInfo> compileTargets, final VersionInfo versionInfo) {
    for (final TargetInfo compileTarget : compileTargets.values()) {
      final int bid = linkerConfig.bid(compileTarget.getOutput().toString());
      if (bid > 0) {
        objectFiles.addElement(compileTarget.getOutput());
      }
    }
    final File[] objectFileArray = new File[objectFiles.size()];
    objectFiles.copyInto(objectFileArray);
    final File[] sysObjectFileArray = new File[sysObjectFiles.size()];
    sysObjectFiles.copyInto(sysObjectFileArray);
    final String baseName = this._outfile.getName();
    final String[] fullNames = linkerConfig.getOutputFileNames(baseName, versionInfo);
    final File outputFile = new File(this._outfile.getParent(), fullNames[0]);
    return new TargetInfo(linkerConfig, objectFileArray, sysObjectFileArray, outputFile, linkerConfig.getRebuild());
  }

  public int getMaxCores() {
    return this.maxCores;
  }

  public File getObjdir() {
    return this._objDir;
  }

  public File getOutfile() {
    return this._outfile;
  }

  /**
   * Gets output type.
   * 
   * @return output type
   */
  public String getOuttype() {
    return this.linkType.getOutputType();
  }

  /**
   * Gets subsystem name.
   * 
   * @return Subsystem name
   */
  public String getSubsystem() {
    return this.linkType.getSubsystem();
  }

  public TargetDef getTargetPlatform() {
    return null;
  }

  /**
   * This method collects a Hashtable, keyed by output file name, of
   * TargetInfo's for every source file that is specified in the filesets of
   * the <cc>and nested <compiler>elements. The TargetInfo's contain the
   * appropriate compiler configurations for their possible compilation
   * 
   */
  private Map<String, TargetInfo> getTargets(final LinkerConfiguration linkerConfig, final Vector<File> objectFiles, final VersionInfo versionInfo, final File outputFile) {
    final List<String> order = new ArrayList<>();
    final Map<String, TargetInfo> targets = new TreeMap<>(new Comparator<String>() {
      @Override public int compare(String f0, String f1) {
        if (order.isEmpty()) {
          return f0.compareTo(f1);
        }
        String compf0 = FilenameUtils.getBaseName(f0);
        String compf1 = FilenameUtils.getBaseName(f1);
        compf0 = FilenameUtils.removeExtension(compf0);
        compf1 = FilenameUtils.removeExtension(compf1);
        final int i0 = order.indexOf(compf0);
        final int i1 = order.indexOf(compf1);
        if (i0 < 0 && i1 < 0) {
          return f0.compareTo(f1);
        } else {
          CCTask.this.ordered = true;
          if (i0 > 0 && i1 > 0) {
            return i0 == i1 ? 0 : i0 < i1 ? -1 : +1;
          } else {
            if (i1 < 0) {
              return -1;
            } else {
              return +1;
            }
          }
        }
      }
    });
    final TargetDef targetPlatform = getTargetPlatform();
    order.clear();
    for (int i = 0; i < this._compilers.size(); i++) {
      final CompilerDef currentCompilerDef = this._compilers.elementAt(i);
      if (currentCompilerDef.isActive()) {
        final List<String> compilerFileOrder = currentCompilerDef.getOrder();
        if (compilerFileOrder != null) {
          order.addAll(compilerFileOrder);
        }
      }
    }
    final Vector<ProcessorConfiguration> biddingProcessors = new Vector<>(this._compilers.size());
    for (int i = 0; i < this._compilers.size(); i++) {
      final CompilerDef currentCompilerDef = this._compilers.elementAt(i);
      if (currentCompilerDef.isActive()) {
        final ProcessorConfiguration config = currentCompilerDef.createConfiguration(this, this.linkType, this.compilerDef, targetPlatform, versionInfo);
        final PrecompileDef precompileDef = currentCompilerDef.getActivePrecompile(this.compilerDef);
        CommandLineCompilerConfiguration commandLineConfig = (CommandLineCompilerConfiguration) config;
        AbstractCompiler compiler = (AbstractCompiler) commandLineConfig.getCompiler();
        compiler.setWorkDir(currentCompilerDef.getWorkDir());
        ProcessorConfiguration[] localConfigs = new ProcessorConfiguration[] { config };
        if (precompileDef != null) {
          final File prototype = precompileDef.getPrototype();
          if (!prototype.exists()) {
            throw new BuildException("prototype (" + prototype.toString() + ") does not exist.");
          }
          if (prototype.isDirectory()) {
            throw new BuildException("prototype (" + prototype.toString() + ") is a directory.");
          }
          final String[] exceptFiles = precompileDef.getExceptFiles();
          final CompilerConfiguration[] configs = ((CompilerConfiguration) config).createPrecompileConfigurations(prototype, exceptFiles);
          if (configs != null && configs.length == 2) {
            final TargetMatcher matcher = new TargetMatcher(this, this._objDir, new ProcessorConfiguration[] { configs[0] }, linkerConfig, objectFiles, targets, versionInfo);
            matcher.visit(new File(prototype.getParent()), prototype.getName());
            biddingProcessors.addElement(configs[1]);
            localConfigs = new ProcessorConfiguration[2];
            localConfigs[0] = configs[1];
            localConfigs[1] = config;
          }
        }
        if (currentCompilerDef.hasFileSets()) {
          final TargetMatcher matcher = new TargetMatcher(this, this._objDir, localConfigs, linkerConfig, objectFiles, targets, versionInfo);
          currentCompilerDef.visitFiles(matcher);
        }
        biddingProcessors.addElement(config);
      }
    }
    if (this._compilers.size() == 0) {
      final ProcessorConfiguration config = this.compilerDef.createConfiguration(this, this.linkType, null, targetPlatform, versionInfo);
      biddingProcessors.addElement(config);
      final ProcessorConfiguration[] bidders = new ProcessorConfiguration[biddingProcessors.size()];
      biddingProcessors.copyInto(bidders);
      final TargetMatcher matcher = new TargetMatcher(this, this._objDir, bidders, linkerConfig, objectFiles, targets, versionInfo);
      this.compilerDef.visitFiles(matcher);
      if (outputFile != null && versionInfo != null) {
        final boolean isDebug = linkerConfig.isDebug();
        try {
          linkerConfig.getLinker().addVersionFiles(versionInfo, this.linkType, outputFile, isDebug, this._objDir, matcher);
        } catch (final IOException ex) {
          throw new BuildException(ex);
        }
      }
    }
    return targets;
  }

  public boolean isDecorateLinkerOptions() {
    return this.decorateLinkerOptions;
  }

  /**
   * Sets the default compiler adapter. Use the "name" attribute when the
   * compiler is a supported compiler.
   * 
   * @param classname
   *          fully qualified classname which implements CompilerAdapter
   */
  public void setClassname(final String classname) {
    this.compilerDef.setClassname(classname);
    this.linkerDef.setClassname(classname);
  }

  /**
   * Set commandLogLevel
   * 
   * ( CUtil.runCommand() will honor this... )
   * 
   * @param commandLogLevel
   *          The log-level for command-logs, default is MSG_VERBOSE.
   */
  public void setCommandLogLevel(final int commandLogLevel) {
    this.commandLogLevel = commandLogLevel;
  }

  /**
   * Sets the dataset for OS/390 builds.
   * 
   * @param dataset
   *          The dataset to set
   */
  public void setDataset(final String dataset) {
    this.dataset = dataset;
  }

  /**
   * Enables or disables generation of debug info.
   */
  public void setDebug(final boolean debug) {
    this.compilerDef.setDebug(debug);
    this.linkerDef.setDebug(debug);
  }

  public void setDecorateLinkerOptions(final boolean decorateLinkerOptions) {
    this.decorateLinkerOptions = decorateLinkerOptions;
  }

  /**
   * Deprecated.
   * 
   * Controls the depth of the dependency evaluation. Used to do a quick
   * check of changes before a full build.
   * 
   * Any negative value which will perform full dependency checking. Positive
   * values will truncate dependency checking. A value of 0 will cause only
   * those files that changed to be recompiled, a value of 1 which cause
   * files that changed or that explicitly include a file that changed to be
   * recompiled.
   * 
   * Any non-negative value will cause a BuildException to be thrown before
   * attempting a link or completing the task.
   * 
   */
  public void setDependencyDepth(final int depth) {
    this.dependencyDepth = depth;
  }

  /**
   * Enables generation of exception handling code
   */
  public void setExceptions(final boolean exceptions) {
    this.compilerDef.setExceptions(exceptions);
  }

  /**
   * Indicates whether the build will continue
   * even if there are compilation errors; defaults to true.
   * 
   * @param fail
   *          if true halt the build on failure
   */
  public void setFailonerror(final boolean fail) {
    this.failOnError = fail;
  }

  /**
   * Enables or disables incremental linking.
   * 
   * @param incremental
   *          new state
   */
  public void setIncremental(final boolean incremental) {
    this.linkerDef.setIncremental(incremental);
  }

  /**
   * Set use of libtool.
   * 
   * If set to true, the "libtool " will be prepended to the command line for
   * compatible processors
   * 
   * @param libtool
   *          If true, use libtool.
   */
  public void setLibtool(final boolean libtool) {
    this.compilerDef.setLibtool(libtool);
    this.linkerDef.setLibtool(libtool);
  }

  /**
   * Sets the output file type. Supported values "executable", "shared", and
   * "static". Deprecated, specify outtype instead.
   * 
   * @deprecated
   */
  @Deprecated public void setLink(final OutputTypeEnum outputType) {
    this.linkType.setOutputType(outputType);
  }

  public void setLinkCPP(final boolean linkCPP) {
    this.linkType.setLinkCPP(linkCPP);
  }

  public void setLinkFortran(final boolean linkFortran) {
    this.linkType.setLinkFortran(linkFortran);
  }

  public void setLinkFortranMain(final boolean linkFortranMain) {
    this.linkType.setLinkFortranMain(linkFortranMain);
  }

  public void setMaxCores(final int maxCores) {
    this.maxCores = maxCores;
  }

  /**
   * Enables or disables generation of multithreaded code
   * 
   * @param multi
   *          If true, generated code may be multithreaded.
   */
  public void setMultithreaded(final boolean multi) {
    this.compilerDef.setMultithreaded(multi);
  }

  /**
   * Sets type of the default compiler and linker.
   * 
   * <table width="100%" border="1">
   * <thead>Supported compilers </thead>
   * <tr>
   * <td>gcc (default)</td>
   * <td>GCC C++ compiler</td>
   * </tr>
   * <tr>
   * <td>g++</td>
   * <td>GCC C++ compiler</td>
   * </tr>
   * <tr>
   * <td>c++</td>
   * <td>GCC C++ compiler</td>
   * </tr>
   * <tr>
   * <td>g77</td>
   * <td>GNU FORTRAN compiler</td>
   * </tr>
   * <tr>
   * <td>msvc</td>
   * <td>Microsoft Visual C++</td>
   * </tr>
   * <tr>
   * <td>bcc</td>
   * <td>Borland C++ Compiler</td>
   * </tr>
   * <tr>
   * <td>msrc</td>
   * <td>Microsoft Resource Compiler</td>
   * </tr>
   * <tr>
   * <td>brc</td>
   * <td>Borland Resource Compiler</td>
   * </tr>
   * <tr>
   * <td>df</td>
   * <td>Compaq Visual Fortran Compiler</td>
   * </tr>
   * <tr>
   * <td>midl</td>
   * <td>Microsoft MIDL Compiler</td>
   * </tr>
   * <tr>
   * <td>icl</td>
   * <td>Intel C++ compiler for Windows (IA-32)</td>
   * </tr>
   * <tr>
   * <td>ecl</td>
   * <td>Intel C++ compiler for Windows (IA-64)</td>
   * </tr>
   * <tr>
   * <td>icc</td>
   * <td>Intel C++ compiler for Linux (IA-32)</td>
   * </tr>
   * <tr>
   * <td>ecc</td>
   * <td>Intel C++ compiler for Linux (IA-64)</td>
   * </tr>
   * <tr>
   * <td>CC</td>
   * <td>Sun ONE C++ compiler</td>
   * </tr>
   * <tr>
   * <td>aCC</td>
   * <td>HP aC++ C++ Compiler</td>
   * </tr>
   * <tr>
   * <td>os390</td>
   * <td>OS390 C Compiler</td>
   * </tr>
   * <tr>
   * <td>os400</td>
   * <td>Icc Compiler</td>
   * </tr>
   * <tr>
   * <td>sunc89</td>
   * <td>Sun C89 C Compiler</td>
   * </tr>
   * <tr>
   * <td>xlC</td>
   * <td>VisualAge C Compiler</td>
   * </tr>
   * <tr>
   * <td>uic</td>
   * <td>Qt user interface compiler (creates .h, .cpp and moc_*.cpp files).</td>
   * </tr>
   * <tr>
   * <td>moc</td>
   * <td>Qt meta-object compiler</td>
   * </tr>
   * <tr>
   * <td>xpidl</td>
   * <td>Mozilla xpidl compiler (creates .h and .xpt files).</td>
   * </tr>
   * <tr>
   * <td>wcl</td>
   * <td>OpenWatcom C/C++ compiler</td>
   * </tr>
   * <tr>
   * <td>wfl</td>
   * <td>OpenWatcom FORTRAN compiler</td>
   * </tr>
   * </table>
   * 
   */
  public void setName(final CompilerEnum name) {
    this.compilerDef.setName(name);
    final Processor compiler = this.compilerDef.getProcessor();
    final Linker linker = compiler.getLinker(this.linkType);
    this.linkerDef.setProcessor(linker);
  }

  /**
   * Do not propagate old environment when new environment variables are
   * specified.
   */
  public void setNewenvironment(final boolean newenv) {
    this.compilerDef.setNewenvironment(newenv);
    for (int i = 0; i < this._compilers.size(); i++) {
      final CompilerDef currentCompilerDef = this._compilers.elementAt(i);
      currentCompilerDef.setNewenvironment(newenv);
    }
    this.linkerDef.setNewenvironment(newenv);
  }

  /**
   * Sets the destination directory for object files.
   * 
   * Generally this should be a property expression that evaluates to
   * distinct debug and release object file directories.
   * 
   * @param dir
   *          object directory
   */
  public void setObjdir(final File dir) {
    if (dir == null) {
      throw new NullPointerException("dir");
    }
    this._objDir = dir;
  }

  /**
   * Sets optimization.
   * 
   * @param optimization
   */
  public void setOptimize(final OptimizationEnum optimization) {
    this.compilerDef.setOptimize(optimization);
  }

  /**
   * Sets the output file name. If not specified, the task will only compile
   * files and not attempt to link. If an extension is not specified, the
   * task may use a system appropriate extension and prefix, for example,
   * outfile="example" may result in "libexample.so" being created.
   * 
   * @param outfile
   *          output file name
   */
  public void setOutfile(final File outfile) {
    if (outfile == null || outfile.toString().length() > 0) {
      this._outfile = outfile;
    }
  }

  /**
   * Specifies the name of a property to set with the physical filename that
   * is produced by the linker
   */
  public void setOutputFileProperty(final String outputFileProperty) {
    this.outputFileProperty = outputFileProperty;
  }

  /**
   * Sets the output file type. Supported values "executable", "shared", and
   * "static".
   */
  public void setOuttype(final OutputTypeEnum outputType) {
    this.linkType.setOutputType(outputType);
  }

  /**
   * Sets the project.
   */
  @Override public void setProject(final Project project) {
    super.setProject(project);
    this.compilerDef.setProject(project);
    this.linkerDef.setProject(project);
  }

  public void setProjectsOnly(final boolean value) {
    this.projectsOnly = value;
  }

  /**
   * If set to true, all files will be rebuilt.
   * 
   * @param rebuildAll
   *          If true, all files will be rebuilt. If false, up to
   *          date files will not be rebuilt.
   */
  public void setRebuild(final boolean rebuildAll) {
    this.compilerDef.setRebuild(rebuildAll);
    this.linkerDef.setRebuild(rebuildAll);
  }

  /**
   * If set to true, compilation errors will not stop the task until all
   * files have been attempted.
   * 
   * @param relentless
   *          If true, don't stop on the first compilation error
   * 
   */
  public void setRelentless(final boolean relentless) {
    this.relentless = relentless;
  }

  /**
   * Enables run-time type information.
   */
  public void setRtti(final boolean rtti) {
    this.compilerDef.setRtti(rtti);
  }

  /**
   * Sets the type of runtime library, possible values "dynamic", "static".
   */
  public void setRuntime(final RuntimeType rtlType) {
    this.linkType.setStaticRuntime(rtlType.getIndex() == 1);
  }

  /**
   * Sets the nature of the subsystem under which that the program will
   * execute.
   * 
   * <table width="100%" border="1">
   * <thead>Supported subsystems </thead>
   * <tr>
   * <td>gui</td>
   * <td>Graphical User Interface</td>
   * </tr>
   * <tr>
   * <td>console</td>
   * <td>Command Line Console</td>
   * </tr>
   * <tr>
   * <td>other</td>
   * <td>Other</td>
   * </tr>
   * </table>
   * 
   * @param subsystem
   *          subsystem
   * @throws NullPointerException
   *           if subsystem is null
   */
  public void setSubsystem(final SubsystemEnum subsystem) {
    if (subsystem == null) {
      throw new NullPointerException("subsystem");
    }
    this.linkType.setSubsystem(subsystem);
  }

  /**
   * Enumerated attribute with the values "none", "severe", "default",
   * "production", "diagnostic", and "aserror".
   */
  public void setWarnings(final WarningLevelEnum level) {
    this.compilerDef.setWarnings(level);
  }
}