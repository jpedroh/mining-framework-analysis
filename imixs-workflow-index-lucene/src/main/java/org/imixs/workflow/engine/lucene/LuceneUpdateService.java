package org.imixs.workflow.engine.lucene;
import java.util.List;
import java.util.logging.Logger;
import jakarta.inject.Inject;
import javax.ejb.AccessTimeout;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.engine.index.UpdateService;
import org.imixs.workflow.exceptions.IndexException;
import jakarta.ejb.Singleton;

/**
 * The LuceneUpdateService provides methods to write Imixs Workitems into a
 * Lucene search index. With the method <code>addWorkitem()</code> a
 * ItemCollection can be added to a lucene search index. The service init method
 * reads the property file 'imixs.properties' from the current classpath to
 * determine the configuration.
 * 
 * <ul>
 * <li>The property "IndexDir" defines the location of the lucene index
 * <li>The property "FulltextFieldList" lists all fields which should be
 * searchable after a workitem was updated
 * <li>The property "IndexFieldList" lists all fields which should be indexed as
 * keywords by the lucene search engine
 * </ul>
 * 
 * The singleton pattern is used to avoid conflicts within multi-thread
 * scenarios. The service is used by the LucenPlugin to update the lucene index
 * during a workflow processing step.
 * 
 * 
 * @see http://stackoverflow.com/questions/34880347/why-did-lucene-indexwriter-
 *      did-not-update-the-index-when-called-from-a-web-modul
 * @see LucenePlugin
 * @version 1.2
 * @author rsoika
 */
@Singleton @AccessTimeout(value = 30000) public class LuceneUpdateService implements UpdateService {
  @Inject private LuceneIndexService luceneIndexService;

  private static Logger logger = Logger.getLogger(LuceneUpdateService.class.getName());

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
  @Override public void updateIndex(List<ItemCollection> documents) {
    luceneIndexService.indexDocuments(documents);
  }

  /**
     * This method flush the event log.
     */
  @Override public void updateIndex() {
    long ltime = System.currentTimeMillis();
    int flushCount = 0;
    while (luceneIndexService.flushEventLog(2048) == false) {
      flushCount = +2048;
      logger.info("...flush event log: " + flushCount + " entries updated in " + (System.currentTimeMillis() - ltime) + "ms ...");
    }
    long updateTime = (System.currentTimeMillis() - ltime);
    if (updateTime > 5000) {
      logger.warning("...Slow lucene updateIndex take " + (updateTime) + "ms !");
    }
  }
}