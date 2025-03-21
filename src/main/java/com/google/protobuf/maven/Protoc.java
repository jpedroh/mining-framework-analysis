package com.google.protobuf.maven;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import java.io.File;
import java.util.List;
import java.util.Set;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;

/**
 * This class represents an invokable configuration of the {@code protoc} compiler.
 * The actual executable is invoked using the plexus {@link Commandline}.
 *
 * @author gak@google.com (Gregory Kick)
 */
final class Protoc {
  private static final String LOG_PREFIX = "[PROTOC] ";

  private final String executable;

  private final ImmutableSet<File> protoPathElements;

  private final ImmutableSet<File> protoFiles;

  private final File javaOutputDirectory;

  private final File 
<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
  descriptorSetFile
=======
  cppOutputDirectory
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java
  ;


<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
  private final boolean includeImportsInDescriptorSet;
=======
  /**
     * A directory into which Python source files will be generated.
     */
  private final File pythonOutputDirectory;
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java


  private final CommandLineUtils.StringStreamConsumer output;

  private final CommandLineUtils.StringStreamConsumer error;

  /**
     * Constructs a new instance. This should only be used by the {@link Builder}.
     *
     * @param executable The path to the {@code protoc} executable.
     * @param protoPath The directories in which to search for imports.
     * @param protoFiles The proto source files to compile.
     * @param javaOutputDirectory The directory into which the java source files will be generated.
     * @param descriptorSetFile The directory into which a descriptor set will be generated;
     * if {@code null}, no descriptor set will be written
     * @param includeImportsInDescriptorSet If {@code true}, dependencies will be included in the descriptor set.
     */
  private Protoc(final String executable, final ImmutableSet<File> protoPath, final ImmutableSet<File> protoFiles, final File javaOutputDirectory, 
<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
  File descriptorSetFile
=======
  final File cppOutputDirectory
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java
  , 
<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
  boolean includeImportsInDescriptorSet
=======
  final File pythonOutputDirectory
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java
  ) {
    this.executable = checkNotNull(executable, "executable");
    this.protoPathElements = checkNotNull(protoPath, "protoPath");
    this.protoFiles = checkNotNull(protoFiles, "protoFiles");
    this.javaOutputDirectory = javaOutputDirectory;
    this.
<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
    descriptorSetFile
=======
    cppOutputDirectory
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java
     = 
<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
    descriptorSetFile
=======
    cppOutputDirectory
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java
    ;
    this.
<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
    includeImportsInDescriptorSet
=======
    pythonOutputDirectory
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java
     = 
<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
    includeImportsInDescriptorSet
=======
    pythonOutputDirectory
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java
    ;
    this.error = new CommandLineUtils.StringStreamConsumer();
    this.output = new CommandLineUtils.StringStreamConsumer();
  }

  /**
     * Invokes the {@code protoc} compiler using the configuration specified at construction.
     *
     * @return The exit status of {@code protoc}.
     * @throws CommandLineException
     */
  public int execute() throws CommandLineException {
    Commandline cl = new Commandline();
    cl.setExecutable(executable);
    cl.addArguments(buildProtocCommand().toArray(new String[] {  }));
    return CommandLineUtils.executeCommandLine(cl, null, output, error);
  }

  /**
     * Creates the command line arguments.
     * <p/>
     * This method has been made visible for testing only.
     *
     * @return A list consisting of the executable followed by any arguments.
     */
  public ImmutableList<String> buildProtocCommand() {
    final List<String> command = newLinkedList();
    for (final File protoPathElement : protoPathElements) {
      command.add("--proto_path=" + protoPathElement);
    }
    if (javaOutputDirectory != null) {
      command.add("--java_out=" + javaOutputDirectory);
    }
    if (cppOutputDirectory != null) {
      command.add("--cpp_out=" + cppOutputDirectory);
    }
    if (pythonOutputDirectory != null) {
      command.add("--python_out=" + pythonOutputDirectory);
    }
    for (final File protoFile : protoFiles) {
      command.add(protoFile.toString());
    }
    if (descriptorSetFile != null) {
      command.add("--descriptor_set_out=" + descriptorSetFile);
      if (includeImportsInDescriptorSet) {
        command.add("--include_imports");
      }
    }
    return ImmutableList.copyOf(command);
  }

  /**
     * Logs execution parameters on debug level to the specified logger.
     * All log messages will be prefixed with "{@value #LOG_PREFIX}".
     *
     * @param log a logger.
     */
  public void logExecutionParameters(final Log log) {
    if (log.isDebugEnabled()) {
      if (log.isDebugEnabled()) {
        log.debug(LOG_PREFIX + "Executable: ");
        log.debug(LOG_PREFIX + ' ' + executable);
      }
      if (protoPathElements != null && !protoPathElements.isEmpty()) {
        log.debug(LOG_PREFIX + "Protobuf import paths:");
        for (final File protoPathElement : protoPathElements) {
          log.debug(LOG_PREFIX + ' ' + protoPathElement);
        }
      }
      if (javaOutputDirectory != null) {
        log.debug(LOG_PREFIX + "Java output directory:");
        log.debug(LOG_PREFIX + ' ' + javaOutputDirectory);
      }
      if (descriptorSetFile != null) {
        log.debug(LOG_PREFIX + "Descriptor set output file:");
        log.debug(LOG_PREFIX + ' ' + descriptorSetFile);
        log.debug(LOG_PREFIX + "Include imports:");
        log.debug(LOG_PREFIX + ' ' + includeImportsInDescriptorSet);
      }
      log.debug(LOG_PREFIX + "Protobuf descriptors:");
      for (final File protoFile : protoFiles) {
        log.debug(LOG_PREFIX + ' ' + protoFile);
      }
      final List<String> cl = buildProtocCommand();
      if (cl != null && !cl.isEmpty()) {
        log.debug(LOG_PREFIX + "Command line options:");
        log.debug(LOG_PREFIX + Joiner.on(' ').join(cl));
      }
    }
  }

  /**
     * @return the output
     */
  public String getOutput() {
    return output.getOutput();
  }

  /**
     * @return the error
     */
  public String getError() {
    return error.getOutput();
  }

  static final class Builder {
    private final String executable;

    private File javaOutputDirectory;

    private final Set<File> protopathElements;

    private File 
<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
    descriptorSetFile
=======
    cppOutputDirectory
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java
    ;

    private final Set<File> protoFiles;


<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
    private boolean includeImportsInDescriptorSet;
=======
    /**
         * A directory into which Python source files will be generated.
         */
    private File pythonOutputDirectory;
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java


    /**
         * Constructs a new builder. The two parameters are present as they are
         * required for all {@link Protoc} instances.
         *
         * @param executable The path to the {@code protoc} executable.
         * @param javaOutputDirectory The directory into which the java source files will be generated.
         * @throws NullPointerException If either of the arguments are {@code null}.
         * @throws IllegalArgumentException If the {@code javaOutputDirectory} is not a directory.
         */
    Builder(final String executable) {
      this.executable = checkNotNull(executable, "executable");
      this.protoFiles = newHashSet();
      this.protopathElements = newHashSet();
    }

    /**
         * Sets the directory into which Java source files will be generated.
         *
         * @param javaOutputDirectory a directory into which Java source files will be generated.
         * @return this builder instance.
         * @throws NullPointerException if {@code javaOutputDirectory} is {@code null}.
         * @throws IllegalArgumentException if {@code javaOutputDirectory} is not a directory.
         */
    public Builder setJavaOutputDirectory(final File javaOutputDirectory) {
      this.javaOutputDirectory = checkNotNull(javaOutputDirectory, "\'javaOutputDirectory\' is null");
      checkArgument(javaOutputDirectory.isDirectory(), "\'javaOutputDirectory\' is not a directory: " + javaOutputDirectory);
      return this;
    }

    /**
         * Sets the directory into which C++ source files will be generated.
         *
         * @param cppOutputDirectory a directory into which C++ source files will be generated.
         * @return this builder instance.
         * @throws NullPointerException if {@code cppOutputDirectory} is {@code null}.
         * @throws IllegalArgumentException if {@code cppOutputDirectory} is not a directory.
         */
    public Builder setCppOutputDirectory(final File cppOutputDirectory) {
      this.cppOutputDirectory = checkNotNull(cppOutputDirectory, "\'cppOutputDirectory\' is null");
      checkArgument(cppOutputDirectory.isDirectory(), "\'cppOutputDirectory\' is not a directory: " + cppOutputDirectory);
      return this;
    }

    /**
         * Sets the directory into which Python source files will be generated.
         *
         * @param pythonOutputDirectory a directory into which Python source files will be generated.
         * @return this builder instance.
         * @throws NullPointerException if {@code pythonOutputDirectory} is {@code null}.
         * @throws IllegalArgumentException if {@code pythonOutputDirectory} is not a directory.
         */
    public Builder setPythonOutputDirectory(final File pythonOutputDirectory) {
      this.pythonOutputDirectory = checkNotNull(pythonOutputDirectory, "\'pythonOutputDirectory\' is null");
      checkArgument(pythonOutputDirectory.isDirectory(), "\'pythonOutputDirectory\' is not a directory: " + pythonOutputDirectory);
      return this;
    }

    /**
         * Adds a proto file to be compiled. Proto files must be on the protopath
         * and this method will fail if a proto file is added without first adding a
         * parent directory to the protopath.
         *
         * @param protoFile
         * @return The builder.
         * @throws IllegalStateException If a proto file is added without first
         * adding a parent directory to the protopath.
         * @throws NullPointerException If {@code protoFile} is {@code null}.
         */
    public Builder addProtoFile(File protoFile) {
      checkNotNull(protoFile);
      checkArgument(protoFile.isFile());
      checkArgument(protoFile.getName().endsWith(".proto"));
      checkProtoFileIsInProtopath(protoFile);
      protoFiles.add(protoFile);
      return this;
    }

    public Builder withDescriptorSetFile(File descriptorSetFile, boolean includeImports) {
      checkNotNull(descriptorSetFile, "descriptorSetFile");
      checkArgument(descriptorSetFile.getParentFile().isDirectory());
      this.descriptorSetFile = descriptorSetFile;
      this.includeImportsInDescriptorSet = includeImports;
      return this;
    }

    private void checkProtoFileIsInProtopath(File protoFile) {
      assert protoFile.isFile();
      checkState(checkProtoFileIsInProtopathHelper(protoFile.getParentFile()));
    }

    private boolean checkProtoFileIsInProtopathHelper(File directory) {
      assert directory.isDirectory();
      if (protopathElements.contains(directory)) {
        return true;
      } else {
        final File parentDirectory = directory.getParentFile();
        return parentDirectory != null && checkProtoFileIsInProtopathHelper(parentDirectory);
      }
    }

    /**
         * @see #addProtoFile(File)
         */
    public Builder addProtoFiles(Iterable<File> protoFiles) {
      for (File protoFile : protoFiles) {
        addProtoFile(protoFile);
      }
      return this;
    }

    /**
         * Adds the {@code protopathElement} to the protopath.
         *
         * @param protopathElement A directory to be searched for imported protocol buffer definitions.
         * @return The builder.
         * @throws NullPointerException If {@code protopathElement} is {@code null}.
         * @throws IllegalArgumentException If {@code protpathElement} is not a
         * directory.
         */
    public Builder addProtoPathElement(File protopathElement) {
      checkNotNull(protopathElement);
      checkArgument(protopathElement.isDirectory());
      protopathElements.add(protopathElement);
      return this;
    }

    /**
         * @see #addProtoPathElement(File)
         */
    public Builder addProtoPathElements(Iterable<File> protopathElements) {
      for (File protopathElement : protopathElements) {
        addProtoPathElement(protopathElement);
      }
      return this;
    }

    /**
         * Validates the internal state for consistency and completeness.
         */
    private void validateState() {
      checkState(!protoFiles.isEmpty());
      checkState(javaOutputDirectory != null || cppOutputDirectory != null || pythonOutputDirectory != null, "At least one of these properties must be set: " + "\'javaOutputDirectory\', \'cppOutputDirectory\' or \'pythonOutputDirectory\'");
    }

    /**
         * @return A configured {@link Protoc} instance.
         * @throws IllegalStateException If no proto files have been added.
         */
    public Protoc build() {
      validateState();
      return new Protoc(executable, ImmutableSet.copyOf(protopathElements), ImmutableSet.copyOf(protoFiles), javaOutputDirectory, 
<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
      descriptorSetFile
=======
      cppOutputDirectory
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java
      , 
<<<<<<< /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/left.java
      includeImportsInDescriptorSet
=======
      pythonOutputDirectory
>>>>>>> /usr/src/app/output/dtrott/maven-protoc-plugin/281edcc995e2f5511028659d923d7fe3d64cb2f1/src/main/java/com/google/protobuf/maven/Protoc.java/right.java
      );
    }
  }
}