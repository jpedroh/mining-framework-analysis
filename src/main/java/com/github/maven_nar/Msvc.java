package com.github.maven_nar;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.tools.ant.types.Environment.Variable;
import org.codehaus.plexus.util.StringUtils;
import com.github.maven_nar.cpptasks.CCTask;
import com.github.maven_nar.cpptasks.CompilerDef;
import com.github.maven_nar.cpptasks.LinkerDef;
import com.github.maven_nar.cpptasks.types.SystemIncludePath;
import com.google.common.collect.Sets;

public class Msvc {
  @Parameter private File home;

  private AbstractNarMojo mojo;

  private final Set<String> paths = new LinkedHashSet<>();

  /**
     * VisualStudio Linker version required, the values should be-
     *      7.1 for VS 2003
     *      8.0 for VS 2005
     *      9.0  for VS 2008
     *     10.0   for VS 2010
     *     11.0   for VS 2012
     *      12.00  for VS 2013
     *     14.0  for VS 2015
     *     15.0 for VS 2017
     */
  @Parameter(defaultValue = "") private String version;

  @Parameter private File windowsSdkHome;

  @Parameter private String windowsSdkVersion;

  @Parameter private String tempPath;

  private File windowsHome;

  private String toolPathWindowsSDK;

  private String toolPathLinker;

  private List<File> sdkIncludes = new ArrayList<>();

  private List<File> sdkLibs = new ArrayList<>();

  private Set<String> libsRequired = Sets.newHashSet("ucrt", "um", "shared", "winrt");

  @Parameter(defaultValue = "false") private boolean force_requested_arch;

  private boolean addIncludePath(final CCTask task, final File home, final String subDirectory) throws MojoExecutionException {
    if (home == null) {
      return false;
    }
    final File file = new File(home, subDirectory);
    if (file.exists()) {
      return addIncludePathToTask(task, file);
    }
    return false;
  }

  private boolean addIncludePathToTask(final CCTask task, final File file) throws MojoExecutionException {
    try {
      final SystemIncludePath includePath = task.createSysIncludePath();
      final String fullPath = file.getCanonicalPath();
      includePath.setPath(fullPath);
      return true;
    } catch (final IOException e) {
      throw new MojoExecutionException("Unable to add system include: " + file.getAbsolutePath(), e);
    }
  }

  private boolean addPath(final File home, final String path) {
    if (home != null) {
      final File directory = new File(home, path);
      if (directory.exists()) {
        try {
          final String fullPath = directory.getCanonicalPath();
          this.paths.add(fullPath);
          return true;
        } catch (final IOException e) {
          throw new IllegalArgumentException("Unable to get path: " + directory, e);
        }
      }
    }
    return false;
  }

  static boolean isMSVC(final AbstractNarMojo mojo) {
    return isMSVC(mojo.getLinker().getName());
  }

  static boolean isMSVC(final String name) {
    return "msvc".equalsIgnoreCase(name);
  }

  public void configureCCTask(final CCTask task) throws MojoExecutionException {
    if (OS.WINDOWS.equals(mojo.getOS()) && isMSVC(mojo)) {
      addIncludePath(task, this.home, "VC/include");
      addIncludePath(task, this.home, "VC/atlmfc/include");
      if (compareVersion(this.windowsSdkVersion, "7.1A") <= 0) {
        if (this.version.equals("8.0")) {
          for (File sdkInclude : sdkIncludes) {
            addIncludePathToTask(task, sdkInclude);
            mojo.getLog().debug(" configureCCTask add to Path-- " + sdkInclude.getAbsolutePath());
          }
        } else {
          addIncludePath(task, this.windowsSdkHome, "include");
        }
      } else {
        for (File sdkInclude : sdkIncludes) {
          addIncludePathToTask(task, sdkInclude);
        }
      }
      task.addEnv(getPathVariable());
      Variable envVariable = new Variable();
      envVariable.setKey("SystemRoot");
      envVariable.setValue(this.windowsHome.getAbsolutePath());
      task.addEnv(envVariable);
      envVariable = new Variable();
      envVariable.setKey("TMP");
      envVariable.setValue(getTempPath());
      task.addEnv(envVariable);
      final String envInclude = System.getenv("INCLUDE");
      if (envInclude != null) {
        for (final String path : envInclude.split(";")) {
          addIncludePathToTask(task, new File(path));
        }
      }
    }
  }

  public void configureLinker(final LinkerDef linker) throws MojoExecutionException {
    final String os = mojo.getOS();
    if (os.equals(OS.WINDOWS) && isMSVC(mojo)) {
      final String arch = mojo.getArchitecture();
      if ("x86".equals(arch)) {
        linker.addLibraryDirectory(this.home, "VC/lib");
        linker.addLibraryDirectory(this.home, "VC/atlmfc/lib");
      } else {
        linker.addLibraryDirectory(this.home, "VC/lib/" + arch);
        linker.addLibraryDirectory(this.home, "VC/atlmfc/lib/" + arch);
      }
      String sdkArch = arch;
      if ("amd64".equals(arch)) {
        sdkArch = "x64";
      }
      if (compareVersion(this.windowsSdkVersion, "8.0") < 0) {
        if ("x86".equals(arch)) {
          linker.addLibraryDirectory(this.windowsSdkHome, "lib");
        } else {
          linker.addLibraryDirectory(this.windowsSdkHome, "lib/" + sdkArch);
        }
      } else {
        for (File sdkLib : sdkLibs) {
          linker.addLibraryDirectory(sdkLib, sdkArch);
        }
      }
      final String envLib = System.getenv("LIB");
      if (envLib != null) {
        for (final String path : envLib.split(";")) {
          linker.addLibraryDirectory(new File(path));
        }
      }
    }
  }

  private String getTempPath() {
    if (null == tempPath) {
      tempPath = System.getenv("TMP");
      if (null == tempPath) {
        tempPath = System.getenv("TEMP");
      }
      if (null == tempPath) {
        tempPath = "C:\\Temp";
      }
    }
    return tempPath;
  }

  public Variable getPathVariable() {
    if (this.paths.isEmpty()) {
      return null;
    }
    final Variable pathVariable = new Variable();
    pathVariable.setKey("PATH");
    pathVariable.setValue(StringUtils.join(this.paths.iterator(), File.pathSeparator));
    return pathVariable;
  }

  public String getVersion() {
    return this.version;
  }

  public String getWindowsSdkVersion() {
    return this.windowsSdkVersion;
  }

  private void init() throws MojoFailureException, MojoExecutionException {
    final String mojoOs = this.mojo.getOS();
    if (NarUtil.isWindows() && OS.WINDOWS.equals(mojoOs) && isMSVC(mojo)) {
      windowsHome = new File(System.getenv("SystemRoot"));
      initVisualStudio();
      if (this.version.equals("8.0")) {
        initWindowsSdk8();
        initPath8();
      } else {
        initWindowsSdk();
        initPath();
      }
    } else {
      this.version = "";
      this.windowsSdkVersion = "";
      this.windowsHome = null;
    }
  }

  private void initPath() throws MojoExecutionException {
    final String mojoArchitecture = this.mojo.getArchitecture();
    final String osArchitecture = NarUtil.getArchitecture(null);
    final boolean matchMojo = false;
    if (force_requested_arch) {
      if ("amd64".equals(mojoArchitecture) && !matchMojo) {
        addPath(this.home, "VC/bin/amd64");
        toolPathLinker = new File(this.home, "VC/bin/amd64").getAbsolutePath();
      } else {
        addPath(this.home, "VC/bin");
        toolPathLinker = new File(this.home, "VC/bin").getAbsolutePath();
      }
    } else {
      if (!osArchitecture.equals(mojoArchitecture) && !matchMojo) {
        if (!addPath(this.home, "VC/bin/" + osArchitecture + "_" + mojoArchitecture)) {
          throw new MojoExecutionException("Unable to find compiler for architecture " + mojoArchitecture + ".\n" + new File(this.home, "VC/bin/" + osArchitecture + "_" + mojoArchitecture));
        }
        toolPathLinker = new File(this.home, "VC/bin/" + osArchitecture + "_" + mojoArchitecture).getAbsolutePath();
      }
    }
    if (null == toolPathLinker) {
      if ("amd64".equals(mojoArchitecture)) {
        toolPathLinker = new File(this.home, "VC/bin/amd64").getAbsolutePath();
        if (!new File(toolPathLinker).exists()) {
          final String envVCToolsInstallDir = System.getenv("VCToolsInstallDir");
          if (envVCToolsInstallDir != null) {
            toolPathLinker = new File(envVCToolsInstallDir, "bin/HostX64/x64").getAbsolutePath();
          }
        }
      } else {
        toolPathLinker = new File(this.home, "VC/bin").getAbsolutePath();
      }
    }
    if ("amd64".equals(osArchitecture) && !matchMojo) {
      addPath(this.home, "VC/bin/amd64");
    } else {
      addPath(this.home, "VC/bin");
    }
    addPath(this.home, "VC/VCPackages");
    addPath(this.home, "Common7/Tools");
    addPath(this.home, "Common7/IDE");
    if (compareVersion(this.windowsSdkVersion, "7.1A") <= 0) {
      if ("amd64".equals(osArchitecture) && !matchMojo) {
        addPath(this.windowsSdkHome, "bin/x64");
      }
      addPath(this.windowsSdkHome, "bin");
    } else {
      if ("amd64".equals(osArchitecture) && !matchMojo) {
        addPath(this.windowsSdkHome, "bin/x64");
      }
      addPath(this.windowsSdkHome, "bin/x86");
    }
    if ("amd64".equals(mojoArchitecture)) {
      toolPathWindowsSDK = new File(this.windowsSdkHome, "bin/x64").getAbsolutePath();
    } else {
      if (compareVersion(this.windowsSdkVersion, "7.1A") <= 0) {
        toolPathWindowsSDK = new File(this.windowsSdkHome, "bin").getAbsolutePath();
      } else {
        toolPathWindowsSDK = new File(this.windowsSdkHome, "bin/x86").getAbsolutePath();
      }
    }
    addPath(this.windowsHome, "System32");
    addPath(this.windowsHome, "");
    addPath(this.windowsHome, "System32/wbem");
  }

  private void initPath8() throws MojoExecutionException {
    addPath(this.windowsHome, "System32");
    addPath(this.windowsHome, "");
    addPath(this.windowsHome, "System32/wbem");
  }

  private void initVisualStudio() throws MojoFailureException, MojoExecutionException {
    mojo.getLog().debug(" -- Searching for usable VisualStudio ");
    mojo.getLog().debug("Linker version is  " + this.version);
    if (this.version != null && this.version.trim().length() > 1) {
      String internalVersion;
      Pattern r = Pattern.compile("(\\d+)\\.*(\\d)");
      Matcher matcher = r.matcher(this.version);
      if (matcher.find()) {
        internalVersion = matcher.group(1) + matcher.group(2);
        this.version = matcher.group(1) + "." + matcher.group(2);
      } else {
        throw new MojoExecutionException("msvc.version must be the internal version in the form 10.0 or 120");
      }
      if (this.home == null) {
        final String commontToolsVar = System.getenv("VS" + internalVersion + "COMNTOOLS");
        if (commontToolsVar != null && commontToolsVar.trim().length() > 0) {
          final File commonToolsDirectory = new File(commontToolsVar);
          if (commonToolsDirectory.exists()) {
            this.home = commonToolsDirectory.getParentFile().getParentFile();
          }
        }
      }
      mojo.getLog().debug(String.format(" VisualStudio %1s (%2s) found %3s ", this.version, internalVersion, this.home));
    } else {
      this.version = "";
      for (final Entry<String, String> entry : System.getenv().entrySet()) {
        final String key = entry.getKey();
        final String value = entry.getValue();
        final Pattern versionPattern = Pattern.compile("VS(\\d+)(\\d)COMNTOOLS");
        final Matcher matcher = versionPattern.matcher(key);
        if (matcher.matches()) {
          final String version = matcher.group(1) + "." + matcher.group(2);
          if (versionStringComparator.compare(version, this.version) > 0) {
            final File commonToolsDirectory = new File(value);
            if (commonToolsDirectory.exists()) {
              this.version = version;
              this.home = commonToolsDirectory.getParentFile().getParentFile();
              mojo.getLog().debug(String.format(" VisualStudio %1s (%2s) found %3s ", this.version, matcher.group(1) + matcher.group(2), this.home));
            }
          }
        }
      }
      if (this.version.length() == 0) {
        final TextStream out = new StringTextStream();
        final TextStream err = new StringTextStream();
        final TextStream dbg = new StringTextStream();
        NarUtil.runCommand("link", new String[] { "/?" }, null, null, out, err, dbg, null, true);
        final Pattern p = Pattern.compile("(\\d+\\.\\d+)\\.\\d+(\\.\\d+)?");
        final Matcher m = p.matcher(out.toString());
        if (m.find()) {
          this.version = m.group(1);
          mojo.getLog().debug(String.format(" VisualStudio Not found but link runs and reports version %1s (%2s)", this.version, m.group(0)));
        } else {
          throw new MojoExecutionException("msvc.version not specified and no VS<Version>COMNTOOLS environment variable can be found");
        }
      }
    }
  }

  private final Comparator<String> versionStringComparator = new Comparator<String>() {
    @Override public int compare(String o1, String o2) {
      DefaultArtifactVersion version1 = new DefaultArtifactVersion(o1);
      DefaultArtifactVersion version2 = new DefaultArtifactVersion(o2);
      return version1.compareTo(version2);
    }
  };

  private final Comparator<File> versionComparator = new Comparator<File>() {
    @Override public int compare(File o1, File o2) {
      String firstDir = o2.getName(), secondDir = o1.getName();
      if (firstDir.charAt(0) == 'v') {
        firstDir = firstDir.substring(1, firstDir.length() - 1);
        secondDir = secondDir.substring(1, secondDir.length() - 1);
      }
      String[] firstVersionString = firstDir.split("\\."), secondVersionString = secondDir.split("\\.");
      int maxIdx = Math.min(firstVersionString.length, secondVersionString.length);
      int deltaVer;
      try {
        for (int i = 0; i < maxIdx; i++) {
          if ((deltaVer = Integer.parseInt(firstVersionString[i]) - Integer.parseInt(secondVersionString[i])) != 0) {
            return deltaVer;
          }
        }
      } catch (NumberFormatException e) {
        return firstDir.compareTo(secondDir);
      }
      if (firstVersionString.length > maxIdx) {
        return 1;
      } else {
        if (secondVersionString.length > maxIdx) {
          return -1;
        }
      }
      return 0;
    }
  };

  private boolean foundSDK = false;

  private void initWindowsSdk() throws MojoExecutionException {
    if (this.windowsSdkVersion != null && this.windowsSdkVersion.trim().equals("")) {
      this.windowsSdkVersion = null;
    }
    mojo.getLog().debug(" -- Searching for usable WindowSDK ");
    for (final File directory : Arrays.asList(new File("C:/Program Files (x86)/Windows Kits"), new File("C:/Program Files (x86)/Microsoft SDKs/Windows"), new File("C:/Program Files/Windows Kits"), new File("C:/Program Files/Microsoft SDKs/Windows"))) {
      if (directory.exists()) {
        final File[] kitDirectories = directory.listFiles();
        Arrays.sort(kitDirectories, versionComparator);
        if (kitDirectories != null) {
          for (final File kitDirectory : kitDirectories) {
            if (new File(kitDirectory, "Include").exists()) {
              String kitVersion = kitDirectory.getName();
              if (kitVersion.charAt(0) == 'v') {
                kitVersion = kitVersion.substring(1);
              }
              if (this.windowsSdkVersion != null && compareVersion(kitVersion, this.windowsSdkVersion) > 0) {
                continue;
              }
              mojo.getLog().debug(String.format(" WindowSDK %1s found %2s", kitVersion, kitDirectory.getAbsolutePath()));
              if (kitVersion.matches("\\d+\\.\\d+?[A-Z]?")) {
                legacySDK(kitDirectory);
              } else {
                if (kitVersion.matches("\\d+?")) {
                  addNewSDKLibraries(kitDirectory);
                }
              }
            }
          }
          if (libsRequired.size() == 0) {
            break;
          }
        }
      }
    }
    if (!foundSDK) {
      throw new MojoExecutionException("msvc.windowsSdkVersion not specified and versions cannot be found");
    }
    mojo.getLog().debug(String.format(" Using WindowSDK %1s found %2s", this.windowsSdkVersion, this.windowsSdkHome));
  }

  private void addNewSDKLibraries(final File kitDirectory) {
    List<File> kitVersionDirectories = Arrays.asList(new File(kitDirectory, "Include").listFiles());
    Collections.sort(kitVersionDirectories, versionComparator);
    ListIterator<File> kitVersionDirectoriesIt = kitVersionDirectories.listIterator();
    File kitVersionDirectory = null;
    while (kitVersionDirectoriesIt.hasNext() && (kitVersionDirectory = kitVersionDirectoriesIt.next()) != null) {
      if (new File(kitVersionDirectory, "ucrt").exists()) {
        break;
      }
    }
    if (kitVersionDirectory != null) {
      String version = kitVersionDirectory.getName();
      mojo.getLog().debug(String.format(" Latest Win %1s KitDir at %2s", kitVersionDirectory.getName(), kitVersionDirectory.getAbsolutePath()));
      File includeDir = new File(kitDirectory, "Include/" + version);
      File libDir = new File(kitDirectory, "Lib/" + version);
      addSDKLibs(includeDir, libDir);
      setKit(kitDirectory);
    }
  }

  private void setKit(File home) {
    if (!foundSDK) {
      if (this.windowsSdkVersion == null) {
        this.windowsSdkVersion = home.getName();
      }
      if (this.windowsSdkHome == null) {
        this.windowsSdkHome = home;
      }
      foundSDK = true;
    }
  }

  private void legacySDK(final File kitDirectory) {
    File includeDir = new File(kitDirectory, "Include");
    File libDir = new File(kitDirectory, "Lib");
    if (includeDir.exists() && libDir.exists()) {
      File usableLibDir = null;
      for (final File libSubDir : libDir.listFiles()) {
        final File um = new File(libSubDir, "um");
        if (um.exists()) {
          usableLibDir = libSubDir;
        }
      }
      if (null == usableLibDir) {
        usableLibDir = libDir.listFiles()[0];
      }
      addSDKLibs(includeDir, usableLibDir);
      setKit(kitDirectory);
    }
  }

  private void addSDKLibs(File includeDir, File libdir) {
    final File[] libs = includeDir.listFiles();
    for (final File libIncludeDir : libs) {
      if (libsRequired.remove(libIncludeDir.getName())) {
        mojo.getLog().debug(String.format(" Using directory %1s for library %2s", libIncludeDir.getAbsolutePath(), libIncludeDir.getName()));
        sdkIncludes.add(libIncludeDir);
        sdkLibs.add(new File(libdir, libIncludeDir.getName()));
      }
    }
  }

  private void initWindowsSdk8() throws MojoExecutionException {
    final String osArchitecture = NarUtil.getArchitecture(null);
    File VCINSTALLDIR = new File(this.home, "VC");
    File PlatformSDKIncludeDir = new File(VCINSTALLDIR.getAbsolutePath() + File.separator + "PlatformSDK", "include");
    File SDKIncludeDir = new File(VCINSTALLDIR.getAbsolutePath() + File.separator + "SDK" + File.separator + "v2.0", "include");
    sdkIncludes.add(PlatformSDKIncludeDir);
    sdkIncludes.add(SDKIncludeDir);
    this.windowsSdkHome = this.home;
  }

  public void setMojo(final AbstractNarMojo mojo) throws MojoFailureException, MojoExecutionException {
    if (mojo != this.mojo) {
      this.mojo = mojo;
      init();
    }
  }

  @Override public String toString() {
    return "VS Home-" + this.home + "\nSDKHome-" + this.windowsSdkHome;
  }

  public String getToolPath() {
    return this.toolPathLinker;
  }

  public String getSDKToolPath() {
    return this.toolPathWindowsSDK;
  }

  public void setToolPath(CompilerDef compilerDef, String name) {
    if ("res".equals(name) || "mc".equals(name) || "idl".equals(name)) {
      compilerDef.setToolPath(this.toolPathWindowsSDK);
    } else {
      compilerDef.setToolPath(this.toolPathLinker);
    }
  }

  public int compareVersion(Object o1, Object o2) {
    String version1 = (String) o1;
    String version2 = (String) o2;
    VersionTokenizer tokenizer1 = new VersionTokenizer(version1);
    VersionTokenizer tokenizer2 = new VersionTokenizer(version2);
    int number1 = 0, number2 = 0;
    String suffix1 = "", suffix2 = "";
    while (tokenizer1.MoveNext()) {
      if (!tokenizer2.MoveNext()) {
        do {
          number1 = tokenizer1.getNumber();
          suffix1 = tokenizer1.getSuffix();
          if (number1 != 0 || suffix1.length() != 0) {
            return 1;
          }
        } while(tokenizer1.MoveNext());
        return 0;
      }
      number1 = tokenizer1.getNumber();
      suffix1 = tokenizer1.getSuffix();
      number2 = tokenizer2.getNumber();
      suffix2 = tokenizer2.getSuffix();
      if (number1 < number2) {
        return -1;
      }
      if (number1 > number2) {
        return 1;
      }
      boolean empty1 = suffix1.length() == 0;
      boolean empty2 = suffix2.length() == 0;
      if (empty1 && empty2) {
        continue;
      }
      if (empty1) {
        return 1;
      }
      if (empty2) {
        return -1;
      }
      int result = suffix1.compareTo(suffix2);
      if (result != 0) {
        return result;
      }
    }
    if (tokenizer2.MoveNext()) {
      do {
        number2 = tokenizer2.getNumber();
        suffix2 = tokenizer2.getSuffix();
        if (number2 != 0 || suffix2.length() != 0) {
          return -1;
        }
      } while(tokenizer2.MoveNext());
      return 0;
    }
    return 0;
  }

  class VersionTokenizer {
    private final String _versionString;

    private final int _length;

    private int _position;

    private int _number;

    private String _suffix;

    private boolean _hasValue;

    public int getNumber() {
      return _number;
    }

    public String getSuffix() {
      return _suffix;
    }

    public boolean hasValue() {
      return _hasValue;
    }

    public VersionTokenizer(String versionString) {
      if (versionString == null) {
        throw new IllegalArgumentException("versionString is null");
      }
      _versionString = versionString;
      _length = versionString.length();
    }

    public boolean MoveNext() {
      _number = 0;
      _suffix = "";
      _hasValue = false;
      if (_position >= _length) {
        return false;
      }
      _hasValue = true;
      while (_position < _length) {
        char c = _versionString.charAt(_position);
        if (c < '0' || c > '9') {
          break;
        }
        _number = _number * 10 + (c - '0');
        _position++;
      }
      int suffixStart = _position;
      while (_position < _length) {
        char c = _versionString.charAt(_position);
        if (c == '.') {
          break;
        }
        _position++;
      }
      _suffix = _versionString.substring(suffixStart, _position);
      if (_position < _length) {
        _position++;
      }
      return true;
    }
  }
}