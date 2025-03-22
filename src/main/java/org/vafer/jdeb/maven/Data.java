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

  private 
<<<<<<< /usr/src/app/output/tcurdt/jdeb/6ea8aa24025f263125de78a3fd3b0c586c332328/src/main/java/org/vafer/jdeb/maven/Data.java/left.java
  String
=======
  MissingSourceBehavior
>>>>>>> /usr/src/app/output/tcurdt/jdeb/6ea8aa24025f263125de78a3fd3b0c586c332328/src/main/java/org/vafer/jdeb/maven/Data.java/right.java
   
<<<<<<< /usr/src/app/output/tcurdt/jdeb/6ea8aa24025f263125de78a3fd3b0c586c332328/src/main/java/org/vafer/jdeb/maven/Data.java/left.java
  destinationName
=======
  missingSrc = FAIL
>>>>>>> /usr/src/app/output/tcurdt/jdeb/6ea8aa24025f263125de78a3fd3b0c586c332328/src/main/java/org/vafer/jdeb/maven/Data.java/right.java
  ;

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

  @Override public void produce(final DataConsumer pReceiver) throws IOException {
    if (src != null && !src.exists()) {
      if (missingSrc == IGNORE) {
        return;
      } else {
        throw new FileNotFoundException("Data source not found : " + src);
      }
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
    throw new IOException("Unknown type \'" + type + "\' (file|directory|archive) for " + src);
  }
}