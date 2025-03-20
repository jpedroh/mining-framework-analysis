package org.imixs.workflow.engine.index;
import java.util.Arrays;
import java.util.List;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.engine.DocumentService;
import org.imixs.workflow.exceptions.IndexException;
import jakarta.ejb.Stateless;

/**
 * The UpdateService defines methods to update the search index. These methods
 * are called by the DocuentService.
 * <p>
 * The method updateIndex(documents) writes documents immediately into the
 * index.
 * <p>
 * The method updateIndex() updates the search index based on the eventLog.
 * <p>
 * The UpdateService provides also the default index schema.
 * 
 * @see SchemaService
 * @version 1.0
 * @author rsoika
 */
public interface UpdateService {
  /**
     * This method adds a collection of documents to the index. The documents are
     * added immediately to the index. Calling this method within a running
     * transaction leads to a uncommitted reads in the index. For transaction
     * control, it is recommended to use instead the the method
     * documentService.addDocumentToIndex() which takes care of uncommitted reads.
     * <p>
     * This method is used by the JobHandlerRebuildIndex only.
     * 
     * @param documents of ItemCollections to be indexed
     * @throws IndexException
     */
  public void updateIndex(List<ItemCollection> documents);

  /**
     * This method updates the search index based on the eventLog. Documents are
     * added by the DocumentService as events to the EventLogService. This ensures
     * that only committed documents are added into the index.
     * 
     * @see DocumentService
     */
  public void updateIndex();
}