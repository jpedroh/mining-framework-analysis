package org.rdfhdt.hdt.dictionary.impl.section;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.rdfhdt.hdt.dictionary.DictionarySectionPrivate;
import org.rdfhdt.hdt.listener.ProgressListener;
import org.rdfhdt.hdt.options.HDTSpecification;
import org.rdfhdt.hdt.util.io.CountInputStream;

/**
 * @author mario.arias
 *
 */
public class DictionarySectionFactory {
  private DictionarySectionFactory() {
  }

  public static DictionarySectionPrivate loadFrom(InputStream input, ProgressListener listener) throws IOException {
    if (!input.markSupported()) {
      throw new IllegalArgumentException("Need support for mark()/reset(). Please wrap the InputStream with a BufferedInputStream");
    }
    input.mark(64);
    int dictType = input.read();
    input.reset();
    input.mark(64);
    DictionarySectionPrivate section = null;
    switch (dictType) {
      case PFCDictionarySection.TYPE_INDEX:
      try {
        section = new PFCDictionarySection(new HDTSpecification());
        section.load(input, listener);
      } catch (IllegalArgumentException e) {
        section = new PFCDictionarySectionBig(new HDTSpecification());
        section.load(input, listener);
      }
      return section;
      default:
      throw new IOException("DictionarySection implementation not available for id " + dictType);
    }
  }

  public static DictionarySectionPrivate loadFrom(CountInputStream input, File f, ProgressListener listener) throws IOException {
    input.mark(64);
    int dictType = input.read();
    input.reset();
    input.mark(64);
    switch (dictType) {
      case PFCDictionarySection.TYPE_INDEX:
      DictionarySectionPrivate section = new PFCDictionarySectionMap(input, f);
      return section;
      default:
      throw new IOException("DictionarySection implementation not available for id " + dictType);
    }
  }
}