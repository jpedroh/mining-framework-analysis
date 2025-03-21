package net.nicoulaj.maven.plugins.checksum.execution.target;
import org.codehaus.plexus.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import net.nicoulaj.maven.plugins.checksum.artifacts.ArtifactListener;

/**
 * An {@link ExecutionTarget} that writes digests to a CSV file.
 *
 * @author <a href="mailto:julien.nicoulaud@gmail.com">Julien Nicoulaud</a>
 * @since 1.0
 */
public class CsvSummaryFileTarget implements ExecutionTarget {
  /**
     * The line separator character.
     */
  public static final String LINE_SEPARATOR = System.getProperty("line.separator");

  /**
     * The CSV column separator character.
     */
  public static final String CSV_COLUMN_SEPARATOR = ",";

  /**
     * The CSV comment marker character.
     */
  public static final String CSV_COMMENT_MARKER = "#";

  /**
     * Encoding to use for generated files.
     */
  protected String encoding;

  /**
     * The association file => (algorithm,hashcode).
     */
  protected Map<File, Map<String, String>> filesHashcodes;

  /**
     * The set of algorithms encountered.
     */
  protected SortedSet<String> algorithms;

  /**
     * The target file where the summary is written.
     */
  protected File summaryFile;

  /**
     * List of listeners which are notified every time a CSV file is created.
     *
     * @since 1.3
     */
  protected final Iterable<? extends ArtifactListener> artifactListeners;

  /**
     * Build a new instance of {@link CsvSummaryFileTarget}.
     *
     * @param summaryFile the file to which the summary should be written.
     * @param encoding    the encoding to use for generated files.
     */
  public CsvSummaryFileTarget(File summaryFile, String encoding, Iterable<? extends ArtifactListener> artifactListeners) {
    this.summaryFile = summaryFile;
    this.encoding = encoding;
    this.artifactListeners = artifactListeners;
  }

  /**
     * {@inheritDoc}
     */
  @Override public void init() {
    filesHashcodes = new HashMap<File, Map<String, String>>();
    algorithms = new TreeSet<String>();
  }

  /**
     * {@inheritDoc}
     */
  public void write(String digest, File file, String algorithm) {
    if (!filesHashcodes.containsKey(file)) {
      filesHashcodes.put(file, new HashMap<String, String>());
    }
    Map<String, String> fileHashcodes = filesHashcodes.get(file);
    fileHashcodes.put(algorithm, digest);
    algorithms.add(algorithm);
  }

  /**
     * {@inheritDoc}
     */
  @Override public void close() throws ExecutionTargetCloseException {
    StringBuilder sb = new StringBuilder();
    sb.append(CSV_COMMENT_MARKER).append("File");
    for (String algorithm : algorithms) {
      sb.append(CSV_COLUMN_SEPARATOR).append(algorithm);
    }
    for (File file : filesHashcodes.keySet()) {
      sb.append(LINE_SEPARATOR).append(file.getName());
      Map<String, String> fileHashcodes = filesHashcodes.get(file);
      for (String algorithm : algorithms) {
        sb.append(CSV_COLUMN_SEPARATOR);
        if (fileHashcodes.containsKey(algorithm)) {
          sb.append(fileHashcodes.get(algorithm));
        }
      }
    }
    sb.append(LINE_SEPARATOR);
    FileUtils.mkdir(summaryFile.getParent());
    try {
      FileUtils.fileWrite(summaryFile.getPath(), encoding, sb.toString());
      for (ArtifactListener artifactListener : artifactListeners) {
        artifactListener.artifactCreated(summaryFile, "csv");
      }
    } catch (IOException e) {
      throw new ExecutionTargetCloseException(e.getMessage());
    }
  }
}