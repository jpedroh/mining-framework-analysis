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
 * Maven "data" elment acting as a factory for DataProducers. So far Archive and
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


<<<<<<< /usr/src/app/output/tcurdt/jdeb/7659ef627d8f6658067f3c004438b25002a856da/src/main/java/org/vafer/jdeb/maven/Data.java/left.java
  private String destinationName;
=======
  /**
     * @parameter expression="${paths}"
     */
  private String[] paths;
>>>>>>> /usr/src/app/output/tcurdt/jdeb/7659ef627d8f6658067f3c004438b25002a856da/src/main/java/org/vafer/jdeb/maven/Data.java/right.java


  private String type;

  /**
     * @parameter expression="${dst}"
     * @required
     */
  public void setDestinationName(String destinationName) {
    this.destinationName = destinationName;
  }

  /**
     * @parameter expression="${type}"
     */
  public void setType(String type) {
    this.type = type;
  }

  private MissingSourceBehavior missingSrc = FAIL;

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

  /**
     * @parameter expression="${includes}" alias="includes"
     */
  public void setIncludes(String includes) {
    includePatterns = splitPatterns(includes);
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
      new DataProducerFile(src, destinationName, includePatterns, excludePatterns, mappers).produce(pReceiver);
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