package wrm;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Compilation of all scss files from inputpath to outputpath using includePaths
 *
 * @goal compile
 * @phase generate-resources
 */
public class CompilationMojo extends AbstractSassMojo {
  /**
         * Copy source files to output directory.
         *
         * @parameter default-value="false"
         */
  private boolean copySourceToOutput;

  public void execute() throws MojoExecutionException, MojoFailureException {
    validateConfig();
    compiler = initCompiler();
    inputPath = inputPath.replaceAll("\\\\", "/");
    getLog().debug("Input Path=" + inputPath);
    getLog().debug("Output Path=" + outputPath);
    try {
      compile();
    } catch (Exception e) {
      throw new MojoExecutionException("Failed", e);
    }
  }


<<<<<<< /usr/src/app/output/warmuuh/libsass-maven-plugin/5ca7b3f086d9f586184824abd3d00aab62d629cf/src/main/java/wrm/CompilationMojo.java/left.java
  private boolean processFile(Path inputRootPath, Path inputFilePath) throws IOException {
    getLog().debug("Processing File " + inputFilePath);
    Path relativeInputPath = inputRootPath.relativize(inputFilePath);
    Path outputRootPath = this.outputPath.toPath();
    Path outputFilePath = outputRootPath.resolve(relativeInputPath);
    String fileExtension = getFileExtension();
    outputFilePath = Paths.get(outputFilePath.toAbsolutePath().toString().replaceFirst("\\." + fileExtension + "$", ".css"));
    Path sourceMapRootPath = Paths.get(this.sourceMapOutputPath);
    Path sourceMapOutputPath = sourceMapRootPath.resolve(relativeInputPath);
    sourceMapOutputPath = Paths.get(sourceMapOutputPath.toAbsolutePath().toString().replaceFirst("\\.scss$", ".css.map"));
    if (copySourceToOutput) {
      Path inputOutputPath = outputRootPath.resolve(relativeInputPath);
      inputOutputPath.toFile().mkdirs();
      Files.copy(inputFilePath, inputOutputPath, REPLACE_EXISTING);
      inputFilePath = inputOutputPath;
    }
    Output out;
    try {
      out = compiler.compileFile(inputFilePath.toAbsolutePath().toString(), outputFilePath.toAbsolutePath().toString(), sourceMapOutputPath.toAbsolutePath().toString());
    } catch (CompilationException e) {
      getLog().error(e.getMessage());
      getLog().debug(e);
      return false;
    }
    getLog().debug("Compilation finished.");
    writeContentToFile(outputFilePath, out.getCss());
    if (out.getSourceMap() != null) {
      writeContentToFile(sourceMapOutputPath, out.getSourceMap());
    }
    return true;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.
}