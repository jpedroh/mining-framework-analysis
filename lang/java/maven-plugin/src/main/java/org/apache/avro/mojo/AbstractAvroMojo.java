package org.apache.avro.mojo;
import org.apache.avro.LogicalTypes;
import java.io.File;
import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.compiler.specific.SpecificCompiler;
import java.io.IOException;
import org.apache.avro.generic.GenericData;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import java.lang.reflect.InvocationTargetException;
import org.apache.maven.plugin.AbstractMojo;
import java.net.MalformedURLException;
import org.apache.maven.plugin.MojoExecutionException;
import java.net.URL;
import org.apache.maven.project.MavenProject;
import java.net.URLClassLoader;
import org.apache.maven.shared.model.fileset.FileSet;
import java.util.ArrayList;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import java.util.Arrays;
import java.util.List;

/**
 * Base for Avro Compiler Mojos.
 */
public abstract class AbstractAvroMojo extends AbstractMojo {
  /**
   * The source directory of avro files. This directory is added to the classpath
   * at schema compiling time. All files can therefore be referenced as classpath
   * resources following the directory structure under the source directory.
   *
   * @parameter property="sourceDirectory"
   *            default-value="${basedir}/src/main/avro"
   */
  private File sourceDirectory;

  /**
   * @parameter property="outputDirectory"
   *            default-value="${project.build.directory}/generated-sources/avro"
   */
  private File outputDirectory;

  /**
   * @parameter property="sourceDirectory"
   *            default-value="${basedir}/src/test/avro"
   */
  private File testSourceDirectory;

  /**
   * @parameter property="outputDirectory"
   *            default-value="${project.build.directory}/generated-test-sources/avro"
   */
  private File testOutputDirectory;

  /**
   * The field visibility indicator for the fields of the generated class, as
   * string values of SpecificCompiler.FieldVisibility. The text is case
   * insensitive.
   *
   * @parameter default-value="PRIVATE"
   */
  private String fieldVisibility;

  /**
   * A list of files or directories that should be compiled first thus making them
   * importable by subsequently compiled schemas. Note that imported files should
   * not reference each other.
   *
   * @parameter
   */
  protected String[] imports;

  /**
   * A set of Ant-like exclusion patterns used to prevent certain files from being
   * processed. By default, this set is empty such that no files are excluded.
   *
   * @parameter
   */
  protected String[] excludes = new String[0];

  /**
   * A set of Ant-like exclusion patterns used to prevent certain files from being
   * processed. By default, this set is empty such that no files are excluded.
   *
   * @parameter
   */
  protected String[] testExcludes = new String[0];

  /**
   * The Java type to use for Avro strings. May be one of CharSequence, String or
   * Utf8. CharSequence by default.
   *
   * @parameter property="stringType"
   */
  protected String stringType = "CharSequence";

  /**
   * The directory (within the java classpath) that contains the velocity
   * templates to use for code generation. The default value points to the
   * templates included with the avro-maven-plugin.
   *
   * @parameter property="templateDirectory"
   */
  protected String templateDirectory = "/org/apache/avro/compiler/specific/templates/java/classic/";

  /**
   * The qualified names of classes which the plugin will look up, instantiate
   * (through an empty constructor that must exist) and set up to be injected into
   * Velocity templates by Avro compiler.
   *
   * @parameter property="velocityToolsClassesNames"
   */
  protected String[] velocityToolsClassesNames = new String[0];

  /**
   * Generated record schema classes will extend this class.
   *
   * @parameter property="recordSpecificClass"
   */
  private String recordSpecificClass = "org.apache.avro.specific.SpecificRecordBase";

  /**
   * Generated error schema classes will extend this class.
   *
   * @parameter property="errorSpecificClass"
   */
  private String errorSpecificClass = "org.apache.avro.specific.SpecificExceptionBase";

  /**
   * The createOptionalGetters parameter enables generating the getOptional...
   * methods that return an Optional of the requested type. This works ONLY on
   * Java 8+
   *
   * @parameter property="createOptionalGetters"
   */
  protected boolean createOptionalGetters = false;

  /**
   * The gettersReturnOptional parameter enables generating get... methods that
   * return an Optional of the requested type. This works ONLY on Java 8+
   *
   * @parameter property="gettersReturnOptional"
   */
  protected boolean gettersReturnOptional = false;

  /**
   * The optionalGettersForNullableFieldsOnly parameter works in conjunction with
   * gettersReturnOptional option. If it is set, Optional getters will be
   * generated only for fields that are nullable. If the field is mandatory,
   * regular getter will be generated. This works ONLY on Java 8+.
   *
   * @parameter property="optionalGettersForNullableFieldsOnly"
   */
  protected boolean optionalGettersForNullableFieldsOnly = false;

  /**
   * Determines whether or not to create setters for the fields of the record. The
   * default is to create setters.
   *
   * @parameter default-value="true"
   */
  protected boolean createSetters;

  /**
   * A set of fully qualified class names of custom
   * {@link org.apache.avro.Conversion} implementations to add to the compiler.
   * The classes must be on the classpath at compile time and whenever the Java
   * objects are serialized.
   *
   * @parameter property="customConversions"
   */
  protected String[] customConversions = new String[0];

  /**
   * A set of fully qualified class names of custom
   * {@link org.apache.avro.LogicalTypes.LogicalTypeFactory} implementations to
   * add to the compiler. The classes must be on the classpath at compile time and
   * whenever the Java objects are serialized.
   *
   * @parameter property="customLogicalTypeFactories"
   */
  protected String[] customLogicalTypeFactories = new String[0];

  /**
   * Determines whether or not to use Java classes for decimal types
   *
   * @parameter default-value="false"
   */
  protected boolean enableDecimalLogicalType;

  /**
   * The current Maven project.
   *
   * @parameter default-value="${project}"
   * @readonly
   * @required
   */
  protected MavenProject project;

  @Override public void execute() throws MojoExecutionException {
    boolean hasSourceDir = null != sourceDirectory && sourceDirectory.isDirectory();
    boolean hasImports = null != imports;
    boolean hasTestDir = null != testSourceDirectory && testSourceDirectory.isDirectory();
    if (!hasSourceDir && !hasTestDir) {
      throw new MojoExecutionException("neither sourceDirectory: " + sourceDirectory + " or testSourceDirectory: " + testSourceDirectory + " are directories");
    }
    if (hasImports) {
      checkImportPaths();
      for (String importedFile : imports) {
        File file = new File(importedFile);
        if (file.isDirectory()) {
          String[] includedFiles = getIncludedFiles(file.getAbsolutePath(), excludes, getIncludes());
          getLog().info("Importing Directory: " + file.getAbsolutePath());
          getLog().debug("Importing Directory Files: " + Arrays.toString(includedFiles));
          compileFiles(includedFiles, file, outputDirectory);
        } else {
          if (file.isFile()) {
            getLog().info("Importing File: " + file.getAbsolutePath());
            compileFiles(new String[] { file.getName() }, file.getParentFile(), outputDirectory);
          }
        }
      }
    }
    if (hasSourceDir) {
      String[] includedFiles = getIncludedFiles(sourceDirectory.getAbsolutePath(), excludes, getIncludes());
      compileFiles(includedFiles, sourceDirectory, outputDirectory);
    }
    if (hasImports || hasSourceDir) {
      project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    }
    if (hasTestDir) {
      String[] includedFiles = getIncludedFiles(testSourceDirectory.getAbsolutePath(), testExcludes, getTestIncludes());
      compileFiles(includedFiles, testSourceDirectory, testOutputDirectory);
      project.addTestCompileSourceRoot(testOutputDirectory.getAbsolutePath());
    }
  }

  private void checkImportPaths() throws MojoExecutionException {
    for (String importedFile : imports) {
      File file = new File(importedFile);
      if (!file.exists()) {
        throw new MojoExecutionException("Path " + file.getAbsolutePath() + " does not exist");
      }
    }
  }

  private String[] getIncludedFiles(String absPath, String[] excludes, String[] includes) {
    final FileSetManager fileSetManager = new FileSetManager();
    final FileSet fs = new FileSet();
    fs.setDirectory(absPath);
    fs.setFollowSymlinks(false);
    if (imports != null) {
      String importExclude = null;
      for (String importFile : this.imports) {
        File file = new File(importFile);
        if (file.isDirectory()) {
          importExclude = file.getName() + "/**";
        } else {
          if (file.isFile()) {
            importExclude = "**/" + file.getName();
          }
        }
        fs.addExclude(importExclude);
      }
    }
    for (String include : includes) {
      fs.addInclude(include);
    }
    for (String exclude : excludes) {
      fs.addExclude(exclude);
    }
    return fileSetManager.getIncludedFiles(fs);
  }

  private void compileFiles(String[] files, File sourceDir, File outDir) throws MojoExecutionException {
    final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try {

<<<<<<< /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/AbstractAvroMojo.java/left.java
      Thread.currentThread().setContextClassLoader(createClassLoader());
=======
      loadLogicalTypesFactories();
>>>>>>> /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/AbstractAvroMojo.java/right.java

      for (String filename : files) {
        try {
          loadLogicalTypesFactories();
          doCompile(filename, sourceDir, outDir);
        } catch (IOException e) {
          throw new MojoExecutionException("Error compiling protocol file " + filename + " to " + outDir, e);
        }
      }
    } 
<<<<<<< /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/AbstractAvroMojo.java/left.java
    catch (MalformedURLException | DependencyResolutionRequiredException e) {
      throw new MojoExecutionException("Cannot locate classpath entries", e);
    }
=======
    catch (IOException e) {
      throw new MojoExecutionException("Error while loading logical types factories ", e);
    }
>>>>>>> /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/AbstractAvroMojo.java/right.java
     finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
    this.doCompile(files, sourceDir, outDir);
  }

  private void loadLogicalTypesFactories() throws IOException, MojoExecutionException {
    try (URLClassLoader classLoader = createClassLoader()) {
      for (String factory : customLogicalTypeFactories) {
        Class<LogicalTypes.LogicalTypeFactory> logicalTypeFactoryClass = (Class<LogicalTypes.LogicalTypeFactory>) classLoader.loadClass(factory);
        LogicalTypes.LogicalTypeFactory factoryInstance = logicalTypeFactoryClass.getDeclaredConstructor().newInstance();
        LogicalTypes.register(factoryInstance);
      }
    } catch (DependencyResolutionRequiredException | ClassNotFoundException e) {
      throw new IOException(e);
    } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
      throw new MojoExecutionException("Failed to instantiate logical type factory class", e);
    }
  }

  protected SpecificCompiler.FieldVisibility getFieldVisibility() {
    try {
      String upper = String.valueOf(this.fieldVisibility).trim().toUpperCase();
      return SpecificCompiler.FieldVisibility.valueOf(upper);
    } catch (IllegalArgumentException e) {
      return SpecificCompiler.FieldVisibility.PRIVATE;
    }
  }

  protected List<Object> instantiateAdditionalVelocityTools() {
    final List<Object> velocityTools = new ArrayList<>(velocityToolsClassesNames.length);
    for (String velocityToolClassName : velocityToolsClassesNames) {
      try {
        Class<?> klass = Class.forName(velocityToolClassName);
        velocityTools.add(klass.getDeclaredConstructor().newInstance());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return velocityTools;
  }

  protected abstract void doCompile(String filename, File sourceDirectory, File outputDirectory) throws IOException;

  protected void doCompile(
<<<<<<< /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/AbstractAvroMojo.java/left.java
  File sourceFileForModificationDetection
=======
  String[] files
>>>>>>> /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/AbstractAvroMojo.java/right.java
  , 
<<<<<<< /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/AbstractAvroMojo.java/left.java
  Schema schema
=======
  File sourceDirectory
>>>>>>> /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/AbstractAvroMojo.java/right.java
  , File outputDirectory) throws IOException, MojoExecutionException {

<<<<<<< /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/AbstractAvroMojo.java/left.java
    doCompile(sourceFileForModificationDetection, new SpecificCompiler(schema), outputDirectory);
=======
    for (String filename : files) {
      try {
        doCompile(filename, sourceDirectory, outputDirectory);
      } catch (IOException e) {
        throw new MojoExecutionException("Error compiling protocol file " + filename + " to " + outputDirectory, e);
      }
    }
>>>>>>> /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/AbstractAvroMojo.java/right.java
  }

  protected void doCompile(File sourceFileForModificationDetection, Protocol protocol, File outputDirectory) throws IOException {
    doCompile(sourceFileForModificationDetection, new SpecificCompiler(protocol), outputDirectory);
  }

  private void doCompile(File sourceFileForModificationDetection, SpecificCompiler compiler, File outputDirectory) throws IOException {
    compiler.setTemplateDir(templateDirectory);
    compiler.setStringType(GenericData.StringType.valueOf(stringType));
    compiler.setFieldVisibility(getFieldVisibility());
    compiler.setCreateOptionalGetters(createOptionalGetters);
    compiler.setGettersReturnOptional(gettersReturnOptional);
    compiler.setOptionalGettersForNullableFieldsOnly(optionalGettersForNullableFieldsOnly);
    compiler.setCreateSetters(createSetters);
    compiler.setEnableDecimalLogicalType(enableDecimalLogicalType);
    try {
      final URLClassLoader classLoader = createClassLoader();
      for (String customConversion : customConversions) {
        compiler.addCustomConversion(classLoader.loadClass(customConversion));
      }
    } catch (ClassNotFoundException | DependencyResolutionRequiredException e) {
      throw new IOException(e);
    }
    compiler.setOutputCharacterEncoding(project.getProperties().getProperty("project.build.sourceEncoding"));
    compiler.setAdditionalVelocityTools(instantiateAdditionalVelocityTools());
    compiler.setRecordSpecificClass(this.recordSpecificClass);
    compiler.setErrorSpecificClass(this.errorSpecificClass);
    compiler.compileToDestination(sourceFileForModificationDetection, outputDirectory);
  }

  protected List<URL> findClasspath() throws DependencyResolutionRequiredException, MalformedURLException {
    final List<URL> urls = appendElements(project.getRuntimeClasspathElements());
    urls.addAll(appendElements(project.getTestClasspathElements()));
    return urls;
  }

  protected URLClassLoader createClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
    final List<URL> urls = findClasspath();
    return new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
  }

  private List<URL> appendElements(List<String> runtimeClasspathElements) throws MalformedURLException {
    if (runtimeClasspathElements == null) {
      return new ArrayList<>();
    }
    List<URL> runtimeUrls = new ArrayList<>(runtimeClasspathElements.size());
    for (String runtimeClasspathElement : runtimeClasspathElements) {
      runtimeUrls.add(new File(runtimeClasspathElement).toURI().toURL());
    }
    return runtimeUrls;
  }

  protected abstract String[] getIncludes();

  protected abstract String[] getTestIncludes();
}