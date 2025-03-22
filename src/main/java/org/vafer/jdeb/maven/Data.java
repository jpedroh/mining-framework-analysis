package org.vafer.jdeb.maven;
import static org.vafer.jdeb.maven.MissingSourceBehavior.FAIL;
import static org.vafer.jdeb.maven.MissingSourceBehavior.IGNORE;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.vafer.jdeb.DataConsumer;
import org.vafer.jdeb.DataProducer;
import org.vafer.jdeb.producers.DataProducerArchive;
import org.vafer.jdeb.producers.DataProducerDirectory;
import org.vafer.jdeb.producers.DataProducerFile;
import org.vafer.jdeb.producers.DataProducerPathTemplate;

/**
 * Maven "data" element acting as a factory for DataProducers. So far Archive and
 * Directory producers are supported. Both support the usual ant pattern set
 * matching.
 *
 * @author Bryan Sant <bryan.sant@gmail.com>
 */
public final class Data implements DataProducer {
  private File src;

  /**
     * @parameter expression="${src}"
     * @required
     */
  public void setSrc(File src) {
    this.src = src;
  }

  private String type;

  /**
     * @parameter expression="${type}"
     */
  public void setType(String type) {
    this.type = type;
  }

  /**
     * @parameter expression="${includes}" alias="includes"
     */
  public void setIncludes(String includes) {
    includePatterns = splitPatterns(includes);
  }


<<<<<<< /usr/src/app/output/tcurdt/jdeb/171fbd3ff576afba81e75fd2e766b838610fb7a3/src/main/java/org/vafer/jdeb/maven/Data.java/left.java
  /**
     * @parameter expression="${paths}"
     */
  private String[] paths;
=======
  private MissingSourceBehavior missingSrc = FAIL;
>>>>>>> /usr/src/app/output/tcurdt/jdeb/171fbd3ff576afba81e75fd2e766b838610fb7a3/src/main/java/org/vafer/jdeb/maven/Data.java/right.java


  /**
     * @parameter expression="${missingSrc}"
     */
  public void setMissingSrc(String missingSrc) {
    MissingSourceBehavior value = MissingSourceBehavior.valueOf(missingSrc.trim().toUpperCase());
    if (value == null) {
      throw new IllegalArgumentException("Unknown " + MissingSourceBehavior.class.getSimpleName() + ": " + missingSrc);
    }
    this.missingSrc = value;
  }

  private String[] includePatterns;

  /**
     * @parameter expression="${excludes}" alias="excludes"
     */
  public void setExcludes(String excludes) {
    excludePatterns = splitPatterns(excludes);
  }

  private String[] excludePatterns;

  /**
     * @parameter expression="${mapper}"
     */
  private Mapper mapper;

  void setPaths(String[] paths) {
    this.paths = paths;
  }

  public String[] splitPatterns(String patterns) {
    String[] result = null;
    if (patterns != null && patterns.length() > 0) {
      List tokens = new ArrayList();
      StringTokenizer tok = new StringTokenizer(patterns, ", ", false);
      while (tok.hasMoreTokens()) {
        tokens.add(tok.nextToken());
      }
      result = (String[]) tokens.toArray(new String[tokens.size()]);
    }
    return result;
  }

  public void produce(final DataConsumer pReceiver) throws IOException {
    if (src != null && !src.exists()) {
      if (missingSrc == IGNORE) {
        return;
      } else {
        throw new FileNotFoundException("Data source not found : " + src);
      }
    }
    if (src == null && (paths == null || paths.length == 0)) {
      throw new RuntimeException("src or paths not set");
    }
    org.vafer.jdeb.mapping.Mapper[] mappers = null;
    if (mapper != null) {
      mappers = new org.vafer.jdeb.mapping.Mapper[] { mapper.createMapper() };
    }
    if ("file".equalsIgnoreCase(type)) {
      new DataProducerFile(src, includePatterns, excludePatterns, mappers).produce(pReceiver);
      return;
    }
    if ("archive".equalsIgnoreCase(type)) {
      new DataProducerArchive(src, includePatterns, excludePatterns, mappers).produce(pReceiver);
      return;
    }
    if ("directory".equalsIgnoreCase(type)) {
      new DataProducerDirectory(src, includePatterns, excludePatterns, mappers).produce(pReceiver);
      return;
    }
    if ("template".equalsIgnoreCase(type)) {
      new DataProducerPathTemplate(paths, includePatterns, excludePatterns, mappers).produce(pReceiver);
      return;
    }
    throw new IOException("Unknown type \'" + type + "\' (file|directory|archive|template) for " + src);
  }
}