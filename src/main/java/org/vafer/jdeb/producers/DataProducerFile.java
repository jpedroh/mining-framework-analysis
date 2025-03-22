package org.vafer.jdeb.producers;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.vafer.jdeb.DataConsumer;
import org.vafer.jdeb.DataProducer;
import org.vafer.jdeb.mapping.Mapper;

/**
 * DataProducer representing a single file
 * For cross-platform permissions and ownerships you probably want to use a Mapper, too.
 *
 * @author Torsten Curdt
 */
public final class DataProducerFile extends AbstractDataProducer implements DataProducer {
  private final File file;

  private final String destinationName;

  public DataProducerFile(final File pFile, String pDestinationName, String[] pIncludes, String[] pExcludes, Mapper[] pMapper) {
    super(pIncludes, pExcludes, pMapper);
    file = pFile;
    destinationName = pDestinationName;
  }

  public void produce(final DataConsumer pReceiver) throws IOException {
    String fileName;
    if (destinationName != null && destinationName.trim().length() > 0) {
      fileName = destinationName.trim();
    } else {
      fileName = file.getName();
    }
    TarArchiveEntry entry = Producers.defaultFileEntryWithName(fileName);
    entry = map(entry);
    entry.setSize(file.length());
    Producers.produceInputStreamWithEntry(pReceiver, new FileInputStream(file), entry);
  }
}