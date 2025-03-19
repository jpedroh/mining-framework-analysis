package org.rdfhdt.hdt.hdt.impl;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import org.rdfhdt.hdt.dictionary.Dictionary;
import org.rdfhdt.hdt.dictionary.DictionaryFactory;
import org.rdfhdt.hdt.dictionary.DictionaryPrivate;
import org.rdfhdt.hdt.dictionary.TempDictionary;
import org.rdfhdt.hdt.dictionary.impl.FourSectionDictionary;
import org.rdfhdt.hdt.enums.ResultEstimationType;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.exceptions.IllegalFormatException;
import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.hdt.HDTPrivate;
import org.rdfhdt.hdt.hdt.HDTVersion;
import org.rdfhdt.hdt.hdt.HDTVocabulary;
import org.rdfhdt.hdt.hdt.TempHDT;
import org.rdfhdt.hdt.header.Header;
import org.rdfhdt.hdt.header.HeaderFactory;
import org.rdfhdt.hdt.header.HeaderPrivate;
import org.rdfhdt.hdt.iterator.DictionaryTranslateIterator;
import org.rdfhdt.hdt.iterator.DictionaryTranslateIteratorBuffer;
import org.rdfhdt.hdt.listener.ProgressListener;
import org.rdfhdt.hdt.options.ControlInfo;
import org.rdfhdt.hdt.options.ControlInformation;
import org.rdfhdt.hdt.options.HDTOptions;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TempTriples;
import org.rdfhdt.hdt.triples.TripleID;
import org.rdfhdt.hdt.triples.TripleString;
import org.rdfhdt.hdt.triples.Triples;
import org.rdfhdt.hdt.triples.TriplesFactory;
import org.rdfhdt.hdt.triples.TriplesPrivate;
import org.rdfhdt.hdt.util.StopWatch;
import org.rdfhdt.hdt.util.StringUtil;
import org.rdfhdt.hdt.util.io.CountInputStream;
import org.rdfhdt.hdt.util.io.IOUtil;
import org.rdfhdt.hdt.util.listener.IntermediateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of HDT interface
 *
 */
public class HDTImpl implements HDTPrivate {
  private static final Logger log = LoggerFactory.getLogger(HDTImpl.class);

  private HDTOptions spec;

  protected HeaderPrivate header;

  protected DictionaryPrivate dictionary;

  protected TriplesPrivate triples;

  private String hdtFileName;

  private String baseUri;

  private boolean isMapped;

  private boolean isClosed = false;

  private void createComponents() {
    header = HeaderFactory.createHeader(spec);
    dictionary = DictionaryFactory.createDictionary(spec);
    triples = TriplesFactory.createTriples(spec);
  }

  @Override public void populateHeaderStructure(String baseUri) {
    if (baseUri == null || baseUri.length() == 0) {
      throw new IllegalArgumentException("baseURI cannot be empty");
    }
    if (isClosed) {
      throw new IllegalStateException("Cannot add header to a closed HDT.");
    }
    header.insert(baseUri, HDTVocabulary.RDF_TYPE, HDTVocabulary.HDT_DATASET);
    header.insert(baseUri, HDTVocabulary.RDF_TYPE, HDTVocabulary.VOID_DATASET);
    header.insert(baseUri, HDTVocabulary.VOID_TRIPLES, triples.getNumberOfElements());
    header.insert(baseUri, HDTVocabulary.VOID_PROPERTIES, dictionary.getNpredicates());
    header.insert(baseUri, HDTVocabulary.VOID_DISTINCT_SUBJECTS, dictionary.getNsubjects());
    header.insert(baseUri, HDTVocabulary.VOID_DISTINCT_OBJECTS, dictionary.getNobjects());
    String formatNode = "_:format";
    String dictNode = "_:dictionary";
    String triplesNode = "_:triples";
    String statisticsNode = "_:statistics";
    String publicationInfoNode = "_:publicationInformation";
    header.insert(baseUri, HDTVocabulary.HDT_FORMAT_INFORMATION, formatNode);
    header.insert(formatNode, HDTVocabulary.HDT_DICTIONARY, dictNode);
    header.insert(formatNode, HDTVocabulary.HDT_TRIPLES, triplesNode);
    header.insert(baseUri, HDTVocabulary.HDT_STATISTICAL_INFORMATION, statisticsNode);
    header.insert(baseUri, HDTVocabulary.HDT_PUBLICATION_INFORMATION, publicationInfoNode);
    dictionary.populateHeader(header, dictNode);
    triples.populateHeader(header, triplesNode);
    header.insert(statisticsNode, HDTVocabulary.HDT_SIZE, getDictionary().size() + getTriples().size());
    header.insert(publicationInfoNode, HDTVocabulary.DUBLIN_CORE_ISSUED, StringUtil.formatDate(new Date()));
  }

  /**
	 * @param spec2
	 */
  public HDTImpl(HDTOptions spec) {
    this.spec = spec;
    createComponents();
  }

  @Override public void loadFromHDT(InputStream input, ProgressListener listener) throws IOException {
    ControlInfo ci = new ControlInformation();
    IntermediateListener iListener = new IntermediateListener(listener);
    ci.clear();
    ci.load(input);
    String hdtFormat = ci.getFormat();
    if (!hdtFormat.equals(HDTVocabulary.HDT_CONTAINER)) {
      throw new IllegalFormatException("This software (v" + HDTVersion.HDT_VERSION + ".x.x) cannot open this version of HDT File (" + hdtFormat + ")");
    }
    ci.clear();
    ci.load(input);
    iListener.setRange(0, 5);
    header = HeaderFactory.createHeader(ci);
    header.load(input, ci, iListener);
    try {
      IteratorTripleString it = header.search("", HDTVocabulary.RDF_TYPE, HDTVocabulary.HDT_DATASET);
      if (it.hasNext()) {
        this.baseUri = it.next().getSubject().toString();
      }
    } catch (NotFoundException e) {
      log.error("Unexpected exception.", e);
    }
    ci.clear();
    ci.load(input);
    iListener.setRange(5, 60);
    dictionary = DictionaryFactory.createDictionary(ci);
    dictionary.load(input, ci, iListener);
    ci.clear();
    ci.load(input);
    iListener.setRange(60, 100);
    triples = TriplesFactory.createTriples(ci);
    triples.load(input, ci, iListener);
    isClosed = false;
  }

  @Override public void loadFromHDT(String hdtFileName, ProgressListener listener) throws IOException {
    InputStream in;
    if (hdtFileName.endsWith(".gz")) {
      in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(hdtFileName)));
    } else {
      in = new CountInputStream(new BufferedInputStream(new FileInputStream(hdtFileName)));
    }
    loadFromHDT(in, listener);
    in.close();
    this.hdtFileName = hdtFileName;
    isClosed = false;
  }

  @Override public void mapFromHDT(File f, long offset, ProgressListener listener) throws IOException {
    this.hdtFileName = f.toString();
    this.isMapped = true;
    CountInputStream input;
    if (hdtFileName.endsWith(".gz")) {
      File old = f;
      hdtFileName = hdtFileName.substring(0, hdtFileName.length() - 3);
      f = new File(hdtFileName);
      if (!f.exists()) {
        System.err.println("We cannot map a gzipped HDT, decompressing into " + hdtFileName + " first.");
        IOUtil.decompressGzip(old, f);
        System.err.println("Gzipped HDT successfully decompressed. You might want to delete " + old.getAbsolutePath() + " to save disk space.");
      } else {
        System.err.println("We cannot map a gzipped HDT, using " + hdtFileName + " instead.");
      }
    }
    input = new CountInputStream(new BufferedInputStream(new FileInputStream(hdtFileName)));
    ControlInfo ci = new ControlInformation();
    IntermediateListener iListener = new IntermediateListener(listener);
    ci.clear();
    ci.load(input);
    String hdtFormat = ci.getFormat();
    if (!hdtFormat.equals(HDTVocabulary.HDT_CONTAINER)) {
      throw new IllegalFormatException("This software (v" + HDTVersion.HDT_VERSION + ".x.x) cannot open this version of HDT File (" + hdtFormat + ")");
    }
    ci.clear();
    ci.load(input);
    iListener.setRange(0, 5);
    header = HeaderFactory.createHeader(ci);
    header.load(input, ci, iListener);
    try {
      IteratorTripleString it = header.search("", HDTVocabulary.RDF_TYPE, HDTVocabulary.HDT_DATASET);
      if (it.hasNext()) {
        this.baseUri = it.next().getSubject().toString();
      }
    } catch (NotFoundException e) {
      log.error("Unexpected exception.", e);
    }
    ci.clear();
    input.mark(1024);
    ci.load(input);
    input.reset();
    iListener.setRange(5, 60);
    dictionary = DictionaryFactory.createDictionary(ci);
    dictionary.mapFromFile(input, f, iListener);
    ci.clear();
    input.mark(1024);
    ci.load(input);
    input.reset();
    iListener.setRange(60, 100);
    triples = TriplesFactory.createTriples(ci);
    triples.mapFromFile(input, f, iListener);
    input.close();
    isClosed = false;
  }

  @Override public void saveToHDT(OutputStream output, ProgressListener listener) throws IOException {
    ControlInfo ci = new ControlInformation();
    IntermediateListener iListener = new IntermediateListener(listener);
    ci.clear();
    ci.setType(ControlInfo.Type.GLOBAL);
    ci.setFormat(HDTVocabulary.HDT_CONTAINER);
    ci.save(output);
    ci.clear();
    ci.setType(ControlInfo.Type.HEADER);
    header.save(output, ci, iListener);
    ci.clear();
    ci.setType(ControlInfo.Type.DICTIONARY);
    dictionary.save(output, ci, iListener);
    ci.clear();
    ci.setType(ControlInfo.Type.TRIPLES);
    triples.save(output, ci, iListener);
  }

  @Override public void saveToHDT(String fileName, ProgressListener listener) throws IOException {
    OutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
    saveToHDT(out, listener);
    out.close();
    this.hdtFileName = fileName;
  }

  @Override public IteratorTripleString search(CharSequence subject, CharSequence predicate, CharSequence object) throws NotFoundException {
    if (isClosed) {
      throw new IllegalStateException("Cannot search an already closed HDT");
    }
    TripleID triple = new TripleID(dictionary.stringToId(subject, TripleComponentRole.SUBJECT), dictionary.stringToId(predicate, TripleComponentRole.PREDICATE), dictionary.stringToId(object, TripleComponentRole.OBJECT));
    if (triple.isNoMatch()) {
      return new IteratorTripleString() {
        @Override public TripleString next() {
          return null;
        }

        @Override public boolean hasNext() {
          return false;
        }

        @Override public TripleString previous() {
          return null;
        }

        @Override public ResultEstimationType numResultEstimation() {
          return ResultEstimationType.EXACT;
        }

        @Override public boolean hasPrevious() {
          return false;
        }

        @Override public void goToStart() {
        }

        @Override public long estimatedNumResults() {
          return 0;
        }
      };
    }
    if (isMapped) {
      try {
        return new DictionaryTranslateIteratorBuffer(triples.search(triple), (FourSectionDictionary) dictionary, subject, predicate, object);
      } catch (NullPointerException e) {
        e.printStackTrace();
        return new DictionaryTranslateIterator(triples.search(triple), dictionary, subject, predicate, object);
      }
    } else {
      return new DictionaryTranslateIterator(triples.search(triple), dictionary, subject, predicate, object);
    }
  }

  @Override public Header getHeader() {
    return header;
  }

  @Override public Dictionary getDictionary() {
    return dictionary;
  }

  @Override public Triples getTriples() {
    return triples;
  }

  @Override public long size() {
    if (isClosed) {
      return 0;
    }
    return dictionary.size() + triples.size();
  }

  public void loadFromParts(HeaderPrivate h, DictionaryPrivate d, TriplesPrivate t) {
    this.header = h;
    this.dictionary = d;
    this.triples = t;
    isClosed = false;
  }

  public void loadFromModifiableHDT(TempHDT modHdt, ProgressListener listener) {
    modHdt.reorganizeDictionary(listener);
    modHdt.reorganizeTriples(listener);
    TempTriples modifiableTriples = (TempTriples) modHdt.getTriples();
    TempDictionary modifiableDictionary = (TempDictionary) modHdt.getDictionary();
    if (triples.getClass().equals(modifiableTriples.getClass())) {
      triples = modifiableTriples;
    } else {
      triples.load(modifiableTriples, listener);
    }
    if (dictionary.getClass().equals(modifiableDictionary.getClass())) {
      dictionary = (DictionaryPrivate) modifiableDictionary;
    } else {
      dictionary.load(modifiableDictionary, listener);
    }
    this.baseUri = modHdt.getBaseURI();
    isClosed = false;
  }

  @Override public void loadOrCreateIndex(ProgressListener listener) {
    if (triples.getNumberOfElements() == 0) {
      return;
    }
    ControlInfo ci = new ControlInformation();
    String indexName = hdtFileName + HDTVersion.get_index_suffix("-");
    indexName = indexName.replaceAll("\\.hdt\\.gz", "hdt");
    String versionName = indexName;
    File ff = new File(indexName);
    if (!ff.isFile() || !ff.canRead()) {
      indexName = hdtFileName + (".index");
      indexName = indexName.replaceAll("\\.hdt\\.gz", "hdt");
      ff = new File(indexName);
    }
    CountInputStream in = null;
    try {
      in = new CountInputStream(new BufferedInputStream(new FileInputStream(ff)));
      ci.load(in);
      if (isMapped) {
        triples.mapIndex(in, new File(indexName), ci, listener);
      } else {
        triples.loadIndex(in, ci, listener);
      }
    } catch (Exception e) {
      if (e instanceof FileNotFoundException) {
      } else {
        System.out.println("Error reading .hdt.index, generating a new one. The error was: " + e.getMessage());
        e.printStackTrace();
      }
      StopWatch st = new StopWatch();
      triples.generateIndex(listener);

<<<<<<< /usr/src/app/output/rdfhdt/hdt-java/cd88626a4e665e548f0981fdd26994e4819de68c/hdt-java-core/src/main/java/org/rdfhdt/hdt/hdt/impl/HDTImpl.java/left.java
      if (this.hdtFileName != null) {
        FileOutputStream out = null;
        try {
          out = new FileOutputStream(versionName);
          ci.clear();
          triples.saveIndex(out, ci, listener);
          out.close();
          System.out.println("Index generated and saved in " + st.stopAndShow());
        } catch (IOException e2) {
          System.err.println("Error writing index file.");
          e2.printStackTrace();
        } finally {
          IOUtil.closeQuietly(out);
        }
      }
=======
      FileOutputStream out = null;
>>>>>>> /usr/src/app/output/rdfhdt/hdt-java/cd88626a4e665e548f0981fdd26994e4819de68c/hdt-java-core/src/main/java/org/rdfhdt/hdt/hdt/impl/HDTImpl.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
      try {
        out = new FileOutputStream(versionName);
        ci.clear();
        triples.saveIndex(out, ci, listener);
        out.close();
      } catch (IOException e2) {
      } finally {
        IOUtil.closeQuietly(out);
      }
>>>>>>> /usr/src/app/output/rdfhdt/hdt-java/cd88626a4e665e548f0981fdd26994e4819de68c/hdt-java-core/src/main/java/org/rdfhdt/hdt/hdt/impl/HDTImpl.java/right.java
    } finally {
      IOUtil.closeQuietly(in);
    }
  }

  @Override public String getBaseURI() {
    return baseUri;
  }

  protected void setTriples(TriplesPrivate triples) {
    this.triples = triples;
  }

  @Override public void close() throws IOException {
    isClosed = true;
    dictionary.close();
    triples.close();
  }

  @Override public String toString() {
    return String.format("HDT[file=%s,#triples=%d]", hdtFileName, triples.getNumberOfElements());
  }

  public String getHDTFileName() {
    return hdtFileName;
  }

  public boolean isClosed() {
    return isClosed;
  }

  public boolean isMapped() {
    return isMapped;
  }
}