package org.vafer.jdeb.ant;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.PatternSet;
import org.vafer.jdeb.DataConsumer;
import org.vafer.jdeb.DataProducer;
import org.vafer.jdeb.producers.*;

/**
 * Ant "data" element acting as a factory for DataProducers.
 * So far Archive and Directory producers are supported.
 * Both support the usual ant pattern set matching.
 *
 * @author Torsten Curdt
 */
public final class Data extends PatternSet implements DataProducer {
  private final Collection<Mapper> mapperWrapper = new ArrayList<Mapper>();

  private File src;

  private String type;

  private Boolean conffile;

  private String destinationName;

  public void setSrc(File src) {
    this.src = src;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setConffile(Boolean conffile) {
    this.conffile = conffile;
  }

  public Boolean getConffile() {
    return this.conffile;
  }

  public void setDst(String destinationName) {
    this.destinationName = destinationName;
  }

  public void addMapper(Mapper mapper) {
    mapperWrapper.add(mapper);
  }

  public void produce(final DataConsumer pReceiver) throws IOException {
    if (src == null || !src.exists()) {
      throw new FileNotFoundException("Data source not found : " + src);
    }
    final org.vafer.jdeb.mapping.Mapper[] mappers = new org.vafer.jdeb.mapping.Mapper[mapperWrapper.size()];
    final Iterator<Mapper> it = mapperWrapper.iterator();
    for (int i = 0; i < mappers.length; i++) {
      mappers[i] = it.next().createMapper();
    }
    final Project project = getProject();
    ProducerFactory.KnownType knownType = ProducerFactory.KnownType.forString(type);
    if (knownType == null) {
      return;
    }
    final DataProducer p = ProducerFactory.create(knownType, new ProducerFactory.Params() {
      @Override public File getSource() {
        return src;
      }

      @Override public String getDestination() {
        return destinationName;
      }

      @Override public String[] getIncludePatterns() {
        return Data.this.getIncludePatterns(project);
      }

      @Override public String[] getExcludePatterns() {
        return Data.this.getExcludePatterns(project);
      }

      @Override public org.vafer.jdeb.mapping.Mapper[] getMappers() {
        return mappers;
      }
    });
    p.produce(pReceiver);
  }
}