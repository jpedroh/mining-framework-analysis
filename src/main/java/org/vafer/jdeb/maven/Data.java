package org.vafer.jdeb.maven;
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
import static org.vafer.jdeb.maven.MissingSourceBehavior.FAIL;
import static org.vafer.jdeb.maven.MissingSourceBehavior.IGNORE;

public final class Data implements DataProducer {
  private File src;

  public void setSrc(File src) {
    this.src = src;
  }

  private String destinationName;

  public void setDestinationName(String destinationName) {
    this.destinationName = destinationName;
  }

  private String type;

  public void setType(String type) {
    this.type = type;
  }

  public void setIncludes(String includes) {
    includePatterns = splitPatterns(includes);
  }

  private String[] includePatterns;

  public void setExcludes(String excludes) {
    excludePatterns = splitPatterns(excludes);
  }

  private String[] excludePatterns;

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

  private MissingSourceBehavior missingSrc = FAIL;

  public void setMissingSrc(String missingSrc) {
    MissingSourceBehavior value = MissingSourceBehavior.valueOf(missingSrc.trim().toUpperCase());
    if (value == null) {
      throw new IllegalArgumentException("Unknown " + MissingSourceBehavior.class.getSimpleName() + ": " + missingSrc);
    }
    this.missingSrc = value;
  }
}