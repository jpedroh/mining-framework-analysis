package org.imixs.workflow.engine.lucene;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.ClassicAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.WorkflowKernel;
import org.imixs.workflow.engine.DocumentService;
import org.imixs.workflow.engine.EventLogService;
import org.imixs.workflow.engine.adminp.AdminPService;
import org.imixs.workflow.engine.index.IndexEvent;
import org.imixs.workflow.engine.index.SchemaService;
import org.imixs.workflow.engine.jpa.EventLog;
import org.imixs.workflow.exceptions.IndexException;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * This session ejb provides functionality to maintain a local Lucene index.
 * 
 * @version 1.0
 * @author rsoika
 */
@Stateless public class LuceneIndexService {
  public static final int EVENTLOG_ENTRY_FLUSH_COUNT = 16;

  public static final String ANONYMOUS = "ANONYMOUS";

  public static final String DEFAULT_ANALYZER = "org.apache.lucene.analysis.standard.ClassicAnalyzer";

  public static final String DEFAULT_INDEX_DIRECTORY = "imixs-workflow-index";

  public static final String TAXONOMY_INDEXFIELD_PRAFIX = ".taxonomy";

  @PersistenceContext(unitName = "org.imixs.workflow.jpa") private EntityManager manager;

  private SimpleDateFormat luceneDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

  @Inject @ConfigProperty(name = "lucence.indexDir", defaultValue = DEFAULT_INDEX_DIRECTORY) private String luceneIndexDir;

  @Inject @ConfigProperty(name = "lucence.analyzerClass", defaultValue = DEFAULT_ANALYZER) private String luceneAnalyzerClass;

  @Inject private LuceneItemAdapter luceneItemAdapter;

  private static Logger logger = Logger.getLogger(LuceneIndexService.class.getName());

  @Inject private AdminPService adminPService;

  @Inject private EventLogService eventLogService;

  @Inject private SchemaService schemaService;

  @Inject protected Event<IndexEvent> indexEvents;

  public String getLuceneIndexDir() {
    return luceneIndexDir.trim();
  }

  public void setLuceneIndexDir(String luceneIndexDir) {
    if (luceneIndexDir != null) {
      this.luceneIndexDir = luceneIndexDir.trim();
    }
  }

  public String getLuceneAnalyzerClass() {
    return luceneAnalyzerClass;
  }

  public void setLuceneAnalyzerClass(String luceneAnalyzerClass) {
    this.luceneAnalyzerClass = luceneAnalyzerClass;
  }

  /**
     * Flush the EventLog cache. This method is called by the LuceneSerachService
     * only.
     * <p>
     * The method flushes the cache in smaller blocks of the given junkSize. to
     * avoid a heap size problem. The default flush size is 16. The eventLog cache
     * is tracked by the flag 'dirtyIndex'.
     * <p>
     * issue #439 - The method returns false if the event log contains more entries
     * as defined by the given JunkSize. In this case the caller should recall the
     * method which runs always in a new transaction. The goal of this mechanism is
     * to reduce the event log even in cases the outer transaction breaks.
     * 
     * @see LuceneSearchService
     * @return true if the the complete event log was flushed. If false the method
     *         must be recalled.
     */
  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW) public boolean flushEventLog(int junkSize) {
    long total = 0;
    long count = 0;
    boolean dirtyIndex = true;
    long l = System.currentTimeMillis();
    while (dirtyIndex) {
      try {
        dirtyIndex = !flushEventLogByCount(EVENTLOG_ENTRY_FLUSH_COUNT);
        if (dirtyIndex) {
          total = total + EVENTLOG_ENTRY_FLUSH_COUNT;
          count = count + EVENTLOG_ENTRY_FLUSH_COUNT;
          if (count >= 100) {
            logger.finest("...flush event log: " + total + " entries in " + (System.currentTimeMillis() - l) + "ms...");
            count = 0;
          }
          if (total >= junkSize) {
            logger.finest("...flush event: Issue #439  -> total count >=" + total + " flushEventLog will be continued...");
            return false;
          }
        }
      } catch (IndexException e) {
        logger.warning("...unable to flush lucene event log: " + e.getMessage());
        return true;
      }
    }
    return true;
  }

  /**
     * This method forces an update of the full text index. The method also creates
     * the index directory if it does not yet exist.
     */
  public void rebuildIndex(Directory indexDir) throws IOException {
    IndexWriterConfig indexWriterConfig;
    indexWriterConfig = new IndexWriterConfig(new ClassicAnalyzer());
    indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    IndexWriter indexWriter = new IndexWriter(indexDir, indexWriterConfig);
    indexWriter.close();
    logger.info("...rebuild lucene index job created...");
    ItemCollection job = new ItemCollection();
    job.replaceItemValue("numinterval", 2);
    job.replaceItemValue("job", AdminPService.JOB_REBUILD_INDEX);
    adminPService.createJob(job);
  }

  /**
     * This method adds a collection of documents to the Lucene index. The documents
     * are added immediately to the index. Calling this method within a running
     * transaction leads to a uncommitted reads in the index. For transaction
     * control, it is recommended to use instead the the method updateDocumetns()
     * which takes care of uncommitted reads.
     * <p>
     * This method is used by the JobHandlerRebuildIndex only.
     * 
     * @param documents of ItemCollections to be indexed
     * @throws IndexException
     */
  public void indexDocuments(Collection<ItemCollection> documents) {
    IndexWriter indexWriter = null;
    DirectoryTaxonomyWriter taxonomyWriter = null;
    long ltime = System.currentTimeMillis();
    try {
      indexWriter = createIndexWriter();
      taxonomyWriter = createTaxonomyWriter();
      for (ItemCollection workitem : documents) {
        if (!workitem.getItemValueBoolean(DocumentService.NOINDEX)) {
          Term term = new Term("$uniqueid", workitem.getItemValueString("$uniqueid"));
          logger.finest("......lucene add/update uncommitted workitem \'" + workitem.getItemValueString(WorkflowKernel.UNIQUEID) + "\' to index...");
          Document lucenedoc = createDocument(workitem);
          updateLuceneIndex(term, lucenedoc, indexWriter, taxonomyWriter);
        }
      }
    } catch (IOException luceneEx) {
      logger.warning("lucene error: " + luceneEx.getMessage());
      throw new IndexException(IndexException.INVALID_INDEX, "Unable to update lucene search index", luceneEx);
    } finally {
      if (indexWriter != null) {
        logger.finest("......lucene close IndexWriter...");
        try {
          indexWriter.close();
        } catch (CorruptIndexException e) {
          throw new IndexException(IndexException.INVALID_INDEX, "Unable to close lucene IndexWriter: ", e);
        } catch (IOException e) {
          throw new IndexException(IndexException.INVALID_INDEX, "Unable to close lucene IndexWriter: ", e);
        }
      }
      if (taxonomyWriter != null) {
        logger.finest("......lucene close taxonomyWriter...");
        try {
          taxonomyWriter.close();
        } catch (CorruptIndexException e) {
          throw new IndexException(IndexException.INVALID_INDEX, "Unable to close lucene taxonomyWriter: ", e);
        } catch (IOException e) {
          throw new IndexException(IndexException.INVALID_INDEX, "Unable to close lucene taxonomyWriter: ", e);
        }
      }
    }
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("... update index block in " + (System.currentTimeMillis() - ltime) + " ms (" + documents.size() + " workitems total)");
    }
  }

  /**
     * This method flushes a given count of eventLogEntries. The method return true
     * if no more eventLogEntries exist.
     * 
     * @param count the max size of a eventLog engries to remove.
     * @return true if the cache was totally flushed.
     */
  protected boolean flushEventLogByCount(int count) {
    Date lastEventDate = null;
    boolean cacheIsEmpty = true;
    DirectoryTaxonomyWriter taxonomyWriter = null;
    IndexWriter indexWriter = null;
    long l = System.currentTimeMillis();
    logger.finest("......flush eventlog cache....");
    List<EventLog> events = eventLogService.findEventsByTopic(count + 1, DocumentService.EVENTLOG_TOPIC_INDEX_ADD, DocumentService.EVENTLOG_TOPIC_INDEX_REMOVE);
    if (events != null && events.size() > 0) {
      try {
        indexWriter = createIndexWriter();
        taxonomyWriter = createTaxonomyWriter();
        int _counter = 0;
        for (EventLog eventLogEntry : events) {
          Term term = new Term("$uniqueid", eventLogEntry.getRef());
          org.imixs.workflow.engine.jpa.Document doc = manager.find(org.imixs.workflow.engine.jpa.Document.class, eventLogEntry.getRef());
          if (doc != null && DocumentService.EVENTLOG_TOPIC_INDEX_ADD.equals(eventLogEntry.getTopic())) {
            long l2 = System.currentTimeMillis();
            ItemCollection workitem = new ItemCollection();
            workitem.setAllItems(doc.getData());
            if (!workitem.getItemValueBoolean(DocumentService.NOINDEX)) {
              Document lucenedoc = createDocument(workitem);
              updateLuceneIndex(term, lucenedoc, indexWriter, taxonomyWriter);
              logger.finest("......lucene add/update workitem \'" + doc.getId() + "\' to index in " + (System.currentTimeMillis() - l2) + "ms");
            }
          } else {
            long l2 = System.currentTimeMillis();
            indexWriter.deleteDocuments(term);
            logger.finest("......lucene remove workitem \'" + term + "\' from index in " + (System.currentTimeMillis() - l2) + "ms");
          }
          lastEventDate = eventLogEntry.getCreated().getTime();
          eventLogService.removeEvent(eventLogEntry);
          _counter++;
          if (_counter >= count) {
            cacheIsEmpty = false;
            break;
          }
        }
      } catch (IOException luceneEx) {
        logger.warning("...unable to flush lucene event log: " + luceneEx.getMessage());
        return true;
      } finally {
        if (indexWriter != null) {
          logger.finest("......lucene close IndexWriter...");
          try {
            indexWriter.close();
          } catch (CorruptIndexException e) {
            throw new IndexException(IndexException.INVALID_INDEX, "Unable to close lucene IndexWriter: ", e);
          } catch (IOException e) {
            throw new IndexException(IndexException.INVALID_INDEX, "Unable to close lucene IndexWriter: ", e);
          }
        }
        if (taxonomyWriter != null) {
          logger.finest("......lucene close taxoWriter...");
          try {
            taxonomyWriter.close();
          } catch (CorruptIndexException e) {
            throw new IndexException(IndexException.INVALID_INDEX, "Unable to close lucene taxonomyWriter: ", e);
          } catch (IOException e) {
            throw new IndexException(IndexException.INVALID_INDEX, "Unable to close lucene taxonomyWriter: ", e);
          }
        }
      }
    }
    logger.fine("...flushEventLog - " + events.size() + " events in " + (System.currentTimeMillis() - l) + " ms - last log entry: " + lastEventDate);
    return cacheIsEmpty;
  }

  /**
     * THis helper method is used to write the lucene document into the search index
     * and into the taxonomy index.
     * 
     * @param term
     * @param lucenedoc
     * @param indexWriter
     * @param taxoWriter
     * @throws IOException
     */
  private void updateLuceneIndex(Term term, Document lucenedoc, IndexWriter indexWriter, DirectoryTaxonomyWriter taxoWriter) throws IOException {
    FacetsConfig config = getFacetsConfig();
    indexWriter.updateDocument(term, config.build(taxoWriter, lucenedoc));
  }

  public FacetsConfig getFacetsConfig() {
    FacetsConfig config = new FacetsConfig();
    List<String> indexFieldListCategory = schemaService.getFieldListCategory();
    for (String aFieldname : indexFieldListCategory) {
      aFieldname = aFieldname.toLowerCase().trim();
      config.setMultiValued(aFieldname + TAXONOMY_INDEXFIELD_PRAFIX, true);
    }
    return config;
  }

  /**
     * This method creates a lucene document based on a ItemCollection. The Method
     * creates for each field specified in the FieldList a separate index field for
     * the lucene document.
     * 
     * The property 'AnalyzeIndexFields' defines if a indexfield value should by
     * analyzed by the Lucene Analyzer (default=false)
     * 
     * @param document - the Imixs document to be indexed
     * @return - a lucene document instance
     */
  @SuppressWarnings(value = { "unchecked" }) protected Document createDocument(ItemCollection document) {
    String sValue = null;
    Document doc = new Document();
    String textContent = "";
    List<String> searchFieldList = schemaService.getFieldList();
    for (String aFieldname : searchFieldList) {
      sValue = "";
      List<?> vValues = document.getItemValue(aFieldname);
      if (vValues.size() == 0) {
        continue;
      }
      for (Object o : vValues) {
        if (o == null) {
          continue;
        }
        if (o instanceof Calendar || o instanceof Date) {
          String sDateValue;
          if (o instanceof Calendar) {
            sDateValue = luceneDateFormat.format(((Calendar) o).getTime());
          } else {
            sDateValue = luceneDateFormat.format((Date) o);
          }
          sValue += sDateValue + ",";
        } else {
          sValue += o.toString() + ",";
        }
      }
      if (sValue != null) {
        textContent += sValue + ",";
      }
    }
    if (indexEvents != null) {
      IndexEvent indexEvent = new IndexEvent(IndexEvent.ON_INDEX_UPDATE, document);
      indexEvent.setTextContent(textContent);
      indexEvents.fire(indexEvent);
      textContent = indexEvent.getTextContent();
    } else {
      logger.warning("Missing CDI support for Event<IndexEvent> !");
    }
    logger.finest("......add lucene field content=" + textContent);
    doc.add(new TextField("content", textContent, Store.NO));
    List<String> _localFieldListStore = new ArrayList<String>();
    _localFieldListStore.addAll(schemaService.getFieldListStore());
    List<String> indexFieldListAnalyze = schemaService.getFieldListAnalyze();
    for (String aFieldname : indexFieldListAnalyze) {
      addItemValues(doc, document, aFieldname, true, _localFieldListStore.contains(aFieldname));
      _localFieldListStore.remove(aFieldname);
    }
    List<String> indexFieldListNoAnalyze = schemaService.getFieldListNoAnalyze();
    for (String aFieldname : indexFieldListNoAnalyze) {
      addItemValues(doc, document, aFieldname, false, _localFieldListStore.contains(aFieldname));
    }
    doc.add(new StringField("$uniqueid", document.getItemValueString("$uniqueid"), Store.YES));
    List<String> vReadAccess = (List<String>) document.getItemValue(DocumentService.READACCESS);
    if (vReadAccess.size() == 0 || (vReadAccess.size() == 1 && "".equals(vReadAccess.get(0).toString()))) {
      sValue = ANONYMOUS;
      doc.add(new StringField(DocumentService.READACCESS, sValue, Store.NO));
    } else {
      sValue = "";
      for (String sReader : vReadAccess) {
        doc.add(new StringField(DocumentService.READACCESS, sReader, Store.NO));
      }
    }
    List<String> indexFieldListCategory = schemaService.getFieldListCategory();
    for (String aFieldname : indexFieldListCategory) {
      aFieldname = aFieldname.toLowerCase().trim();
      List<?> valueList = document.getItemValue(aFieldname);
      if (valueList.size() > 0 && valueList.get(0) != null) {
        for (Object singleValue : valueList) {
          String stringValue = luceneItemAdapter.convertItemValue(singleValue);
          doc.add(new FacetField(aFieldname + TAXONOMY_INDEXFIELD_PRAFIX, stringValue));
        }
      }
    }
    return doc;
  }

  /**
     * adds a field value into a Lucene document. The parameter store specifies if
     * the value will become part of the Lucene document which is optional.
     * 
     * @param doc          an existing lucene document
     * @param workitem     the workitem containg the values
     * @param itemName     the Fieldname inside the workitem
     * @param analyzeValue indicates if the value should be parsed by the analyzer
     * @param store        indicates if the value will become part of the Lucene
     *                     document
     */
  protected void addItemValues(final Document doc, final ItemCollection workitem, final String _itemName, final boolean analyzeValue, final boolean store) {
    String itemName = _itemName;
    if (itemName == null) {
      return;
    }
    itemName = itemName.toLowerCase().trim();
    List<?> vValues = workitem.getItemValue(itemName);
    if (vValues.size() == 0) {
      return;
    }
    if (vValues.get(0) == null) {
      return;
    }
    boolean firstValue = true;
    for (Object singleValue : vValues) {
      IndexableField indexableField = null;
      if (store) {
        indexableField = luceneItemAdapter.adaptItemValue(itemName, singleValue, analyzeValue, Store.YES);
      } else {
        indexableField = luceneItemAdapter.adaptItemValue(itemName, singleValue, analyzeValue, Store.NO);
      }
      doc.add(indexableField);
      if (!analyzeValue && firstValue == true) {
        SortedDocValuesField sortedDocField = luceneItemAdapter.adaptSortableItemValue(itemName, singleValue);
        doc.add(sortedDocField);
      }
      firstValue = false;
    }
  }

  /**
     * This method creates a new instance of a lucene IndexWriter.
     * 
     * The location of the lucene index in the filesystem is read from the
     * imixs.properties
     * 
     * @return
     * @throws IOException
     */
  protected IndexWriter createIndexWriter() throws IOException {
    logger.finest("......createIndexWriter...");
    Directory indexDir = createIndexDirectory();
    IndexWriterConfig indexWriterConfig;
    try {
      indexWriterConfig = new IndexWriterConfig((Analyzer) Class.forName(luceneAnalyzerClass).newInstance());
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new IndexException(IndexException.INVALID_INDEX, "Unable to create analyzer \'" + luceneAnalyzerClass + "\'", e);
    }
    return new IndexWriter(indexDir, indexWriterConfig);
  }

  /**
     * Create taxonomyWriter in a separate directory from the main index with the
     * paefix '_taxÄ'
     *
     * @return
     * @throws IOException
     */
  protected DirectoryTaxonomyWriter createTaxonomyWriter() throws IOException {
    logger.finest("......createTaxonomyWriter...");
    Directory taxoDir = createTaxonomyDirectory();
    return new DirectoryTaxonomyWriter(taxoDir);
  }

  /**
     * Creates a Lucene FSDirectory Instance. The method uses the property
     * LockFactory to set a custom LockFactory.
     * 
     * For example: org.apache.lucene.store.SimpleFSLockFactory
     * 
     * @return
     * @throws IOException
     */
  public Directory createIndexDirectory() throws IOException {
    logger.finest("......create lucene Index Directory - path=" + getLuceneIndexDir());
    Path luceneIndexDir = Paths.get(getLuceneIndexDir());
    Directory indexDir = FSDirectory.open(luceneIndexDir);
    if (!DirectoryReader.indexExists(indexDir)) {
      logger.info("...lucene index directory is empty or does not yet exist, initialize the index now....");
      rebuildIndex(indexDir);
    }
    return indexDir;
  }

  /**
     * Creates a Lucene FSDirectory Instance. The method uses the property
     * LockFactory to set a custom LockFactory.
     * <p>
     * The taxonomy directory is identified by the LuceneIndexDir with the praefix
     * '_tax'
     * 
     * @return
     * @throws IOException
     */
  public Directory createTaxonomyDirectory() throws IOException {
    String sPath = getLuceneIndexDir();
    if (sPath.endsWith("/")) {
      sPath = sPath.substring(0, sPath.lastIndexOf("/"));
    }
    sPath = sPath + "_tax";
    logger.finest("......create lucene taxonomy Directory - path=" + sPath);
    Path luceneIndexDir = Paths.get(sPath);
    Directory indexDir = FSDirectory.open(luceneIndexDir);
    if (!DirectoryReader.indexExists(indexDir)) {
      logger.info("...lucene taxonomy directory is empty or does not yet exist, initialize the Taxonomy index now....");
      rebuildIndex(indexDir);
    }
    return indexDir;
  }
}