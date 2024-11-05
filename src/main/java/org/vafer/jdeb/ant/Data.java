package org.vafer.jdeb.ant;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.tools.ant.types.PatternSet;
import org.vafer.jdeb.DataConsumer;
import org.vafer.jdeb.DataProducer;
import org.vafer.jdeb.producers.DataProducerArchive;
import org.vafer.jdeb.producers.DataProducerDirectory;
import org.vafer.jdeb.producers.DataProducerFile;

public final class Data extends PatternSet implements DataProducer {
  private final Collection mapperWrapper = new ArrayList();

  private File src;

  private String type;

  private String destinationName;

  public void setSrc(final File pSrc) {
    src = pSrc;
  }

  public void setType(final String pType) {
    type = pType;
  }

  public void setDst(String pDestinationName) {
    destinationName = pDestinationName;
  }

  public void addMapper(final Mapper pMapper) {
    mapperWrapper.add(pMapper);
  }

  public void produce(final DataConsumer pReceiver) throws IOException {
    if (!src.exists()) {
      throw new FileNotFoundException("Data source not found : " + src);
    }
    org.vafer.jdeb.mapping.Mapper[] mappers = new org.vafer.jdeb.mapping.Mapper[mapperWrapper.size()];
    final Iterator it = mapperWrapper.iterator();
    for (int i = 0; i < mappers.length; i++) {
      mappers[i] = ((Mapper) it.next()).createMapper();
    }
    if ("file".equalsIgnoreCase(type)) {
      new DataProducerFile(src, destinationName, getIncludePatterns(getProject()), getExcludePatterns(getProject()), mappers).produce(pReceiver);
      return;
    }
    if ("archive".equalsIgnoreCase(type)) {
      new DataProducerArchive(src, getIncludePatterns(getProject()), getExcludePatterns(getProject()), mappers).produce(pReceiver);
      return;
    }
    if ("directory".equalsIgnoreCase(type)) {
      new DataProducerDirectory(src, getIncludePatterns(getProject()), getExcludePatterns(getProject()), mappers).produce(pReceiver);
      return;
    }
    throw new IOException("Unknown type \'" + type + "\' (file|directory|archive) for " + src);
  }
}