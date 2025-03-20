package com.github.maven_nar.cpptasks.gcc;
import java.io.File;
import java.util.Vector;
import com.github.maven_nar.cpptasks.CCTask;
import com.github.maven_nar.cpptasks.CUtil;
import com.github.maven_nar.cpptasks.VersionInfo;
import com.github.maven_nar.cpptasks.compiler.CommandLineLinker;
import com.github.maven_nar.cpptasks.compiler.CommandLineLinkerConfiguration;
import com.github.maven_nar.cpptasks.compiler.LinkType;
import com.github.maven_nar.cpptasks.types.LibrarySet;
import com.github.maven_nar.cpptasks.types.LibraryTypeEnum;

/**
 * Abstract adapter for ld-like linkers
 *
 * @author Curt Arnold
 */
public abstract class AbstractLdLinker extends CommandLineLinker {
  private final String outputPrefix;

  protected AbstractLdLinker(final String command, final String identifierArg, final String[] extensions, final String[] ignoredExtensions, final String outputPrefix, final String outputSuffix, final boolean isLibtool, final AbstractLdLinker libtoolLinker) {
    super(command, identifierArg, extensions, ignoredExtensions, outputSuffix, isLibtool, libtoolLinker);
    this.outputPrefix = outputPrefix;
  }

  @Override protected void addBase(final CCTask task, final long base, final Vector<String> args) {
    if (base >= 0) {
      args.addElement("--image-base");
      args.addElement(Long.toHexString(base));
    }
  }

  @Override protected void addEntry(final CCTask task, final String entry, final Vector<String> args) {
    if (entry != null) {
      args.addElement("-e");
      args.addElement(entry);
    }
  }

  @Override protected void addImpliedArgs(final CCTask task, final boolean debug, final LinkType linkType, final Vector<String> args) {
    if (debug) {
      args.addElement("-g");
    }
    if (isDarwin()) {
      if (linkType.isPluginModule()) {
        args.addElement("-bundle");
      } else {
        if (linkType.isJNIModule()) {
          args.addElement("-dynamic");
          args.addElement("-bundle");
        } else {
          if (linkType.isSharedLibrary()) {
            args.addElement("-dynamiclib");
          }
        }
      }
    } else {
      if (linkType.isStaticRuntime()) {
        args.addElement("-static");
      }
      if (linkType.isPluginModule()) {
        args.addElement("-shared");
      } else {
        if (linkType.isSharedLibrary()) {
          args.addElement("-shared");
        }
      }
    }
  }

  @Override protected void addIncremental(final CCTask task, final boolean incremental, final Vector<String> args) {
    if (incremental) {
      args.addElement("-i");
    }
  }

  @Override protected void addLibraryPath(final Vector<String> preargs, final String path) {
    preargs.addElement("-L" + path);
  }

  protected int addLibraryPatterns(final String[] libnames, final StringBuffer buf, final String prefix, final String extension, final String[] patterns, final int offset) {
    for (int i = 0; i < libnames.length; i++) {
      buf.setLength(0);
      buf.append(prefix);
      buf.append(libnames[i]);
      buf.append(extension);
      patterns[offset + i] = buf.toString();
    }
    return offset + libnames.length;
  }

  @Override protected String[] addLibrarySets(final CCTask task, final LibrarySet[] libsets, final Vector<String> preargs, final Vector<String> midargs, final Vector<String> endargs) {
    final Vector<String> libnames = new Vector<String>();
    super.addLibrarySets(task, libsets, preargs, midargs, endargs);
    LibraryTypeEnum previousLibraryType = null;
    for (final LibrarySet libset : libsets) {
      final LibrarySet set = libset;
      final File libdir = set.getDir(null);
      final String[] libs = set.getLibs();
      if (libdir != null) {
        String relPath = libdir.getAbsolutePath();
        final File currentDir = new File(".");

<<<<<<< /usr/src/app/output/maven-nar/nar-maven-plugin/cb61e829410f4f86501ba652f1a211fc264121df/src/main/java/com/github/maven_nar/cpptasks/gcc/AbstractLdLinker.java/left.java
        if (currentDir != null && currentDir.getParentFile() != null) {
          relPath = CUtil.getRelativePath(currentDir.getParentFile().getAbsolutePath(), libdir);
        }
=======
        if (currentDir.getParentFile() != null) {
          relPath = CUtil.getRelativePath(currentDir.getParentFile().getAbsolutePath(), libdir);
        }
>>>>>>> /usr/src/app/output/maven-nar/nar-maven-plugin/cb61e829410f4f86501ba652f1a211fc264121df/src/main/java/com/github/maven_nar/cpptasks/gcc/AbstractLdLinker.java/right.java

        if (set.getType() != null && "framework".equals(set.getType().getValue()) && isDarwin()) {
          endargs.addElement("-F" + relPath);
        } else {
          endargs.addElement("-L" + relPath);
        }
      }
      if (set.getType() != previousLibraryType) {
        if (set.getType() != null && "static".equals(set.getType().getValue())) {
          if (!isDarwin()) {
            endargs.addElement(getStaticLibFlag());
            previousLibraryType = set.getType();
          }
        } else {
          if (set.getType() == null || !"framework".equals(set.getType().getValue()) && !isDarwin()) {
            endargs.addElement(getDynamicLibFlag());
            previousLibraryType = set.getType();
          }
        }
      }
      final StringBuffer buf = new StringBuffer("-l");
      if (set.getType() != null && "framework".equals(set.getType().getValue()) && isDarwin()) {
        buf.setLength(0);
        endargs.addElement("-framework");
      }
      final int initialLength = buf.length();
      for (final String lib : libs) {
        buf.setLength(initialLength);
        buf.append(lib);
        libnames.addElement(lib);
        endargs.addElement(buf.toString());
      }
    }
    if (previousLibraryType != null && previousLibraryType.getValue().equals("static") && !isDarwin()) {
      endargs.addElement(getDynamicLibFlag());
    }
    final String rc[] = new String[libnames.size()];
    for (int i = 0; i < libnames.size(); i++) {
      rc[i] = libnames.elementAt(i);
    }
    return rc;
  }

  @Override protected void addMap(final CCTask task, final boolean map, final Vector<String> args) {
    if (map) {
      args.addElement("-M");
    }
  }

  @Override protected void addStack(final CCTask task, final int stack, final Vector<String> args) {
    if (stack > 0) {
      args.addElement("--stack");
      args.addElement(Integer.toString(stack));
    }
  }

  @Override public String getCommandFileSwitch(final String commandFile) {
    throw new IllegalStateException("ld does not support command files");
  }

  protected String getDynamicLibFlag() {
    return "-Bdynamic";
  }

  /**
   * Returns library path.
   *
   */
  protected File[] getEnvironmentIncludePath() {
    return CUtil.getPathFromEnvironment("LIB", ":");
  }

  @Override public String getLibraryKey(final File libfile) {
    final String libname = libfile.getName();
    final int lastDot = libname.lastIndexOf('.');
    if (lastDot >= 0) {
      return libname.substring(0, lastDot);
    }
    return libname;
  }

  /**
   * Returns library path.
   *
   */
  @Override public File[] getLibraryPath() {
    return new File[0];
  }

  @Override public String[] getLibraryPatterns(final String[] libnames, final LibraryTypeEnum libType) {
    final StringBuffer buf = new StringBuffer();
    int patternCount = libnames.length;
    if (libType == null) {
      patternCount *= 2;
    }
    final String[] patterns = new String[patternCount];
    int offset = 0;
    if (libType == null || "static".equals(libType.getValue())) {
      offset = addLibraryPatterns(libnames, buf, "lib", ".a", patterns, 0);
    }
    if (libType != null && "framework".equals(libType.getValue()) && isDarwin()) {
      for (final String libname : libnames) {
        buf.setLength(0);
        buf.append(libname);
        buf.append(".framework/");
        buf.append(libname);
        patterns[offset++] = buf.toString();
      }
    } else {
      if (libType == null || !"static".equals(libType.getValue())) {
        if (isHPUX()) {
          offset = addLibraryPatterns(libnames, buf, "lib", ".sl", patterns, offset);
        } else {
          offset = addLibraryPatterns(libnames, buf, "lib", ".so", patterns, offset);
        }
      }
    }
    return patterns;
  }

  @Override public int getMaximumCommandLength() {
    return isWindows() ? 20000 : Integer.MAX_VALUE;
  }

  @Override public String[] getOutputFileNames(final String baseName, final VersionInfo versionInfo) {
    final String[] baseNames = super.getOutputFileNames(baseName, versionInfo);
    if (this.outputPrefix.length() > 0) {
      for (int i = 0; i < baseNames.length; i++) {
        baseNames[i] = this.outputPrefix + baseNames[i];
      }
    }
    return baseNames;
  }

  @Override public String[] getOutputFileSwitch(final String outputFile) {
    return GccProcessor.getOutputFileSwitch("-o", outputFile);
  }

  protected String getStaticLibFlag() {
    return "-Bstatic";
  }

  @Override public boolean isCaseSensitive() {
    return true;
  }

  protected boolean isHPUX() {
    final String osname = System.getProperty("os.name").toLowerCase();
    if (osname.indexOf("hp") >= 0 && osname.indexOf("ux") >= 0) {
      return true;
    }
    return false;
  }

  /**
   * Prepares argument list for exec command. Will return null if command
   * line would exceed allowable command line buffer.
   *
   * @param outputFile
   *          linker output file
   * @param sourceFiles
   *          linker input files (.obj, .o, .res)
   * @param config
   *          linker configuration
   * @return arguments for runTask
   */
  @Override public String[] prepareArguments(final CCTask task, final String outputDir, final String outputFile, final String[] sourceFiles, final CommandLineLinkerConfiguration config) {
    final String[] libnames = config.getLibraryNames();
    if (libnames == null || libnames.length == 0) {
      return super.prepareArguments(task, outputDir, outputFile, sourceFiles, config);
    }
    final String[] localSources = sourceFiles.clone();
    int extra = 0;
    for (final String libname : libnames) {
      for (int j = 0; j < localSources.length; j++) {
        if (localSources[j] != null && localSources[j].indexOf(libname) > 0 && localSources[j].indexOf("lib") > 0) {
          final String filename = new File(localSources[j]).getName();
          if (filename.startsWith("lib") && filename.substring(3).startsWith(libname)) {
            final String extension = filename.substring(libname.length() + 3);
            if (extension.equals(".a") || extension.equals(".so") || extension.equals(".sl")) {
              localSources[j] = null;
              extra++;
            }
          }
        }
      }
    }
    if (extra == 0) {
      return super.prepareArguments(task, outputDir, outputFile, sourceFiles, config);
    }
    final String[] finalSources = new String[localSources.length - extra];
    int index = 0;
    for (final String localSource : localSources) {
      if (localSource != null) {
        finalSources[index++] = localSource;
      }
    }
    return super.prepareArguments(task, outputDir, outputFile, finalSources, config);
  }
}