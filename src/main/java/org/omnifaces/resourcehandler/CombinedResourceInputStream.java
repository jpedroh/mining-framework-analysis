package org.omnifaces.resourcehandler;
import static org.omnifaces.util.Faces.getRequestDomainURL;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.faces.application.Resource;
import org.omnifaces.util.Utils;

/**
 * This {@link InputStream} implementation takes care that all in the constructor given resources are been read in
 * sequence.
 * @author Bauke Scholtz
 */
public final class CombinedResourceInputStream extends InputStream {
  private static final byte[] CRLF = { '\r', '\n' };

  private List<InputStream> streams;

  private Iterator<InputStream> streamIterator;

  private InputStream currentStream;

  /**
	 * Creates an instance of {@link CombinedResourceInputStream} based on the given resources. For each resource, the
	 * {@link InputStream} will be obtained and hold in an iterable collection.
	 * @param resources The resources to be read.
	 * @throws IOException If something fails at I/O level.
	 */
  public CombinedResourceInputStream(Set<Resource> resources) throws IOException {
    streams = new ArrayList<>();
    String domainURL = getRequestDomainURL();
    for (Resource resource : resources) {
      InputStream stream;
      try {
        stream = resource.getInputStream();
      } catch (Exception richFacesDoesNotSupportThis) {
        stream = new URL(domainURL + resource.getRequestPath()).openStream();
      }
      streams.add(stream);
      streams.add(new ByteArrayInputStream(CRLF));
    }
    streamIterator = streams.iterator();
    streamIterator.hasNext();
    currentStream = streamIterator.next();
  }

  /**
	 * For each resource, read until its {@link InputStream#read()} returns <code>-1</code> and then iterate to the
	 * {@link InputStream} of the next resource, if any available, else return <code>-1</code>.
	 */
  @Override public int read() throws IOException {
    int read = -1;
    while ((read = currentStream.read()) == -1) {
      if (streamIterator.hasNext()) {
        currentStream = streamIterator.next();
      } else {
        break;
      }
    }
    return read;
  }

  /**
	 * Closes the {@link InputStream} of each resource. Whenever the {@link InputStream#close()} throws an
	 * {@link IOException} for the first time, it will be caught and be thrown after all resources have been closed.
	 * Any {@link IOException} which is thrown by a subsequent close will be ignored by design.
	 */
  @Override public void close() throws IOException {
    IOException caught = null;
    for (InputStream stream : streams) {
      IOException e = Utils.close(stream);
      if (caught == null) {
        caught = e;
      }
    }
    if (caught != null) {
      throw caught;
    }
  }
}