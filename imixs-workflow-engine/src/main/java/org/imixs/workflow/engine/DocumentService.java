package org.imixs.workflow.engine;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.annotation.Resource;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.WorkflowKernel;
import org.imixs.workflow.engine.index.DefaultOperator;
import org.imixs.workflow.engine.index.SearchService;
import org.imixs.workflow.engine.index.SortOrder;
import org.imixs.workflow.engine.index.UpdateService;
import org.imixs.workflow.engine.jpa.Document;
import org.imixs.workflow.exceptions.AccessDeniedException;
import org.imixs.workflow.exceptions.InvalidAccessException;
import org.imixs.workflow.exceptions.PluginException;
import org.imixs.workflow.exceptions.QueryException;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * The DocumentService is used to save and load instances of ItemCollections
 * into a Database. The DocumentService throws an AccessDeniedException if the
 * CallerPrincipal is not allowed to save or read a specific Document from the
 * database. So the DocumentService can be used to save business objects into a
 * database with individual read- or writeAccess restrictions.
 * <p>
 * The Bean holds an instance of an EntityPersistenceManager for the persistence
 * unit 'org.imixs.workflow.jpa' to manage the Document entity bean class. The
 * Document entity bean is used to store the attributes of a ItemCollection into
 * the connected database.
 * <p>
 * The save() method persists any instance of an ItemCollection. If a
 * ItemCollection is saved the first time the DocumentService generates the
 * attribute $uniqueid which will be included in the ItemCollection returned by
 * this method. If a ItemCollection was saved before the method updates the
 * corresponding Document Object.
 * <p>
 * The load() and find() methods are used to read ItemCollections from the
 * database. The remove() method deletes a saved ItemCollection from the
 * database.
 * <p>
 * All methods expect and return Instances of the object
 * org.imixs.workflow.ItemCollection which is no entity EJB. So these objects
 * are not managed by any instance of an EntityPersistenceManager.
 * <p>
 * A collection of ItemCollections can be read using the find() method using EQL
 * syntax.
 * <p>
 * 
 * Additional to the basic functionality to save and load instances of the
 * object org.imixs.workflow.ItemCollection the method also manages the read-
 * and writeAccess for each instance of an ItemCollection. Therefore the save()
 * method scans an ItemCollection for the attributes '$ReadAccess' and
 * '$WriteAccess'. The DocumentService verifies in each call of the save()
 * load(), remove() and find() methods if the current callerPrincipal is granted
 * to the affected entities. If an ItemCollection was saved with read- or
 * writeAccess the access to an Instance of a saved ItemCollection will be
 * protected for a callerPrincipal with missing read- or writeAccess.
 * <p>
 * 
 * @see org.imixs.workflow.engine.jpa.Document
 * @author rsoika
 * @version 1.0
 * 
 */
@DeclareRoles(value = { "org.imixs.ACCESSLEVEL.NOACCESS", "org.imixs.ACCESSLEVEL.READERACCESS", "org.imixs.ACCESSLEVEL.AUTHORACCESS", "org.imixs.ACCESSLEVEL.EDITORACCESS", "org.imixs.ACCESSLEVEL.MANAGERACCESS" }) @RolesAllowed(value = { "org.imixs.ACCESSLEVEL.NOACCESS", "org.imixs.ACCESSLEVEL.READERACCESS", "org.imixs.ACCESSLEVEL.AUTHORACCESS", "org.imixs.ACCESSLEVEL.EDITORACCESS", "org.imixs.ACCESSLEVEL.MANAGERACCESS" }) @Stateless public class DocumentService {
  public static final String ACCESSLEVEL_NOACCESS = "org.imixs.ACCESSLEVEL.NOACCESS";

  public static final String ACCESSLEVEL_READERACCESS = "org.imixs.ACCESSLEVEL.READERACCESS";

  public static final String ACCESSLEVEL_AUTHORACCESS = "org.imixs.ACCESSLEVEL.AUTHORACCESS";

  public static final String ACCESSLEVEL_EDITORACCESS = "org.imixs.ACCESSLEVEL.EDITORACCESS";

  public static final String ACCESSLEVEL_MANAGERACCESS = "org.imixs.ACCESSLEVEL.MANAGERACCESS";

  public static final String EVENTLOG_TOPIC_INDEX_ADD = "index.add";

  public static final String EVENTLOG_TOPIC_INDEX_REMOVE = "index.remove";

  public static final String READACCESS = "$readaccess";

  public static final String WRITEACCESS = "$writeaccess";

  public static final String ISAUTHOR = "$isAuthor";

  public static final String NOINDEX = "$noindex";

  public static final String IMMUTABLE = "$immutable";

  public static final String VERSION = "$version";

  private static final String REGEX_UUID = "([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})|([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}-[0-9]{13,15})";

  public static final String USER_GROUP_LIST = "org.imixs.USER.GROUPLIST";

  private static final String REGEX_OLDUID = "([0-9a-f]{8}-.*|[0-9a-f]{11}-.*)";

  private final static Logger logger = Logger.getLogger(DocumentService.class.getName());

  public static final String OPERATION_NOTALLOWED = "OPERATION_NOTALLOWED";

  public static final String INVALID_PARAMETER = "INVALID_PARAMETER";

  public static final String INVALID_UNIQUEID = "INVALID_UNIQUEID";

  @Resource SessionContext ctx;

  @Resource(name = "ACCESS_ROLES") private String accessRoles = "";

  @Resource(name = "DISABLE_OPTIMISTIC_LOCKING") private Boolean disableOptimisticLocking = false;

  @PersistenceContext(unitName = "org.imixs.workflow.jpa") private EntityManager manager;

  @Inject private UpdateService indexUpdateService;

  @Inject private SearchService indexSearchService;

  @Inject private EventLogService eventLogService;

  @Inject protected Event<DocumentEvent> documentEvents;

  @Inject protected Event<UserGroupEvent> userGroupEvents;

  @Inject @ConfigProperty(name = "index.defaultOperator", defaultValue = "AND") private String indexDefaultOperator;

  /**
	 * Returns a comma separated list of additional Access-Roles defined for this
	 * service
	 * 
	 * @return
	 */
  public String getAccessRoles() {
    return accessRoles;
  }

  public void setAccessRoles(String accessRoles) {
    this.accessRoles = accessRoles;
  }

  /**
	 * returns the disable optimistic locking status
	 * 
	 * @return - true if optimistic locking is disabled
	 */
  public void setDisableOptimisticLocking(Boolean disableOptimisticLocking) {
    this.disableOptimisticLocking = disableOptimisticLocking;
  }

  public Boolean getDisableOptimisticLocking() {
    return disableOptimisticLocking;
  }

  /**
	 * This method returns a list of user names, roles and application groups the
	 * user belongs to.
	 * <p>
	 * A client can extend the list of user groups associated with a userId by
	 * reacting on the CDI event 'UserGrouptEvent'.
	 * 
	 * @see UserGroupEvent
	 * @return
	 */
  public List<String> getUserNameList() {
    List<String> userNameList = new Vector<String>();
    userNameList.add(ctx.getCallerPrincipal().getName().toString());
    String roleList = "org.imixs.ACCESSLEVEL.READERACCESS,org.imixs.ACCESSLEVEL.AUTHORACCESS,org.imixs.ACCESSLEVEL.EDITORACCESS,org.imixs.ACCESSLEVEL.MANAGERACCESS," + accessRoles;
    StringTokenizer roleListTokens = new StringTokenizer(roleList, ",");
    while (roleListTokens.hasMoreTokens()) {
      try {
        String testRole = roleListTokens.nextToken().trim();
        if (!"".equals(testRole) && ctx.isCallerInRole(testRole)) {
          userNameList.add(testRole);
        }
      } catch (Exception e) {
      }
    }
    if (userGroupEvents != null) {
      UserGroupEvent groupEvent = new UserGroupEvent(ctx.getCallerPrincipal().getName().toString());
      userGroupEvents.fire(groupEvent);
      if (groupEvent.getGroups() != null) {
        userNameList.addAll(groupEvent.getGroups());
      }
    } else {
      logger.warning("Missing CDI support for Event<UserGroupEvent> !");
    }
    return userNameList;
  }

  /**
	 * This method returns true, if at least one element of the current UserNameList
	 * is contained in a given name list. The comparison is case sensitive!
	 * 
	 * @param nameList
	 * @return
	 */
  public boolean isUserContained(List<String> nameList) {
    if (nameList == null) {
      return false;
    }
    List<String> userNameList = getUserNameList();
    for (String aName : nameList) {
      if (aName != null && !aName.isEmpty()) {
        if (userNameList.stream().anyMatch(aName::equals)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
	 * Test if the caller has a given security role.
	 * 
	 * @param rolename
	 * @return true if user is in role
	 */
  public boolean isUserInRole(String rolename) {
    try {
      return ctx.isCallerInRole(rolename);
    } catch (Exception e) {
      return false;
    }
  }

  /**
	 * This Method saves an ItemCollection into the database. If the ItemCollection
	 * is saved the first time the method generates a uniqueID ('$uniqueid') which
	 * can be used to identify the ItemCollection by its ID. If the ItemCollection
	 * was saved before, the method updates the existing ItemCollection stored in
	 * the database.
	 * 
	 * <p>
	 * The Method returns an updated instance of the ItemCollection containing the
	 * attributes $modified, $created, and $uniqueId
	 * 
	 * <p>
	 * The method throws an AccessDeniedException if the CallerPrincipal is not
	 * allowed to save or update the ItemCollection in the database. The
	 * CallerPrincipial should have at least the access Role
	 * org.imixs.ACCESSLEVEL.AUTHORACCESS
	 * 
	 * <p>
	 * The method adds/updates the document into the lucene index.
	 * 
	 * <p>
	 * The method returns a itemCollection without the $VersionNumber from the
	 * persisted entity. (see issue #226)
	 * <p>
	 * 
	 * <p>
	 * issue #230:
	 * 
	 * The document will be marked as 'saved' so that the methods load() and
	 * getDocumentsByQuery() can evaluate this flag. Depending on the state, the
	 * methods can decide the correct behavior. In general we detach a document in
	 * the load() and getDocumentsByQuery() method. This is for performance reasons
	 * and the fact, that a ItemCollection can hold byte arrays which will be copied
	 * by reference. In cases where these methods are called after a document was
	 * saved (document is now managed), in one single transaction, the detach call
	 * will discard the changes made by the save() method. For that reason we flag
	 * the entity and evaluate this flag in the load method evaluates the save
	 * status.
	 * 
	 * 
	 * @param ItemCollection to be saved
	 * @return updated ItemCollection
	 * @throws AccessDeniedException
	 */
  public ItemCollection save(ItemCollection document) throws AccessDeniedException {
    boolean debug = logger.isLoggable(Level.FINE);
    long lSaveTime = System.currentTimeMillis();
    if (debug) {
      logger.finest("......save - ID=" + document.getUniqueID() + ", provided version=" + document.getItemValueInteger(VERSION));
    }
    Document persistedDocument = null;
    manager.setFlushMode(FlushModeType.COMMIT);
    String sID = document.getItemValueString(WorkflowKernel.UNIQUEID);
    if (!sID.isEmpty() && !isValidUIDPattern(sID)) {
      throw new InvalidAccessException(INVALID_PARAMETER, "invalid UUID pattern - " + sID);
    }
    if (!sID.isEmpty()) {
      persistedDocument = manager.find(Document.class, sID);
      if (debug && persistedDocument == null) {
        logger.finest("......Document \'" + sID + "\' not found!");
      }
    }
    if (persistedDocument == null) {
      if (!(ctx.isCallerInRole(ACCESSLEVEL_MANAGERACCESS) || ctx.isCallerInRole(ACCESSLEVEL_EDITORACCESS) || ctx.isCallerInRole(ACCESSLEVEL_AUTHORACCESS))) {
        throw new AccessDeniedException(OPERATION_NOTALLOWED, "You are not allowed to perform this operation");
      }
      persistedDocument = new Document(sID);
      Date datCreated = document.getItemValueDate("$created");
      if (datCreated != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(datCreated);
        persistedDocument.setCreated(cal);
      }
      if (debug) {
        logger.finest("......persist activeEntity");
      }
      manager.persist(persistedDocument);
    } else {
      if (!isCallerAuthor(persistedDocument) || !isCallerReader(persistedDocument)) {
        throw new AccessDeniedException(OPERATION_NOTALLOWED, "You are not allowed to perform this operation");
      }
      if (ItemCollection.createByReference(persistedDocument.getData()).getItemValueBoolean(IMMUTABLE)) {
        throw new AccessDeniedException(OPERATION_NOTALLOWED, "Operation not allowed, document is immutable!");
      }
    }
    if (debug) {
      logger.finest("......save - ID=" + document.getUniqueID() + " managed version=" + persistedDocument.getVersion());
    }
    document.removeItem(ISAUTHOR);
    String aType = document.getItemValueString("type");
    if ("".equals(aType)) {
      aType = "document";
      document.replaceItemValue("type", aType);
    }
    persistedDocument.setType(aType);
    document.replaceItemValue("$uniqueid", persistedDocument.getId());
    document.replaceItemValue("$created", persistedDocument.getCreated().getTime());
    Calendar cal = Calendar.getInstance();
    persistedDocument.setModified(cal);
    document.replaceItemValue("$modified", cal.getTime());
    if (documentEvents != null) {
      documentEvents.fire(new DocumentEvent(document, DocumentEvent.ON_DOCUMENT_SAVE));
    } else {
      logger.warning("Missing CDI support for Event<DocumentEvent> !");
    }
    if ((!persistedDocument.getId().equals(document.getUniqueID())) || (!persistedDocument.getCreated().getTime().equals(document.getItemValueDate("$created")))) {
      throw new InvalidAccessException(InvalidAccessException.INVALID_ID, "Invalid data after DocumentEvent \'ON_DOCUMENT_SAVE\'.");
    }
    if (disableOptimisticLocking) {
      document.removeItem("$Version");
    }
    if (!disableOptimisticLocking && document.hasItem(VERSION) && document.getItemValueInteger(VERSION) > 0) {
      int version = document.getItemValueInteger(VERSION);
      persistedDocument.setVersion(version);
    }
    ItemCollection clone = (ItemCollection) document.clone();
    persistedDocument.setData(clone.getAllItems());
    document.removeItem(VERSION);
    document.replaceItemValue(ISAUTHOR, isCallerAuthor(persistedDocument));
    if (!document.getItemValueBoolean(NOINDEX)) {
      addDocumentToIndex(document);
    } else {
      removeDocumentFromIndex(document.getUniqueID());
    }
    persistedDocument.setPending(true);
    if (debug) {
      logger.fine("...\'" + document.getUniqueID() + "\' saved in " + (System.currentTimeMillis() - lSaveTime) + "ms");
    }
    return document;
  }

  /**
	 * This method adds a single document into the to the Lucene index. Before the
	 * document is added to the index, a new eventLog is created. The document will
	 * be indexed after the method flushEventLog is called. This method is called by
	 * the LuceneSearchService finder methods.
	 * <p>
	 * The method supports committed read. This means that a running transaction
	 * will not read an uncommitted document from the Lucene index.
	 * 
	 * 
	 * @param documentContext
	 */
  public void addDocumentToIndex(ItemCollection document) {
    if (!document.getItemValueBoolean(DocumentService.NOINDEX)) {
      eventLogService.createEvent(EVENTLOG_TOPIC_INDEX_ADD, document.getUniqueID());
    }
  }

  /**
	 * This method adds a new eventLog for a document to be deleted from the index.
	 * The document will be removed from the index after the method fluschEventLog
	 * is called. This method is called by the LuceneSearchService finder method
	 * only.
	 * 
	 * 
	 * @param uniqueID of the workitem to be removed
	 * @throws PluginException
	 */
  public void removeDocumentFromIndex(String uniqueID) {
    boolean debug = logger.isLoggable(Level.FINE);
    long ltime = System.currentTimeMillis();
    eventLogService.createEvent(EVENTLOG_TOPIC_INDEX_REMOVE, uniqueID);
    if (debug) {
      logger.fine("... update eventLog cache in " + (System.currentTimeMillis() - ltime) + " ms (1 document to be removed)");
    }
  }

  /**
	 * This method saves a workitem in a new transaction. The method can be used by
	 * plugins to isolate a save request from the current transaction context.
	 * 
	 * To call this method a EJB session context is necessary: <code>
	 * 		workitem= sessionContext.getBusinessObject(EntityService.class)
						.saveByNewTransaction(workitem);
	 * </code>
	 * 
	 * @param itemcol
	 * @return
	 * @throws AccessDeniedException
	 */
  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW) public ItemCollection saveByNewTransaction(ItemCollection itemcol) throws AccessDeniedException {
    return save(itemcol);
  }

  /**
	 * This method loads an ItemCollection from the Database. The method expects a
	 * valid $uniqueID to identify the Document entity saved before into the
	 * database. The method returns null if no Document with the corresponding ID
	 * exists.
	 * <p>
	 * The method checks if the CallerPrincipal has read access to Document stored
	 * in the database. If not, the method returns null.
	 * <p>
	 * <strong>Note:</strong> The method dose not throw an AccessDeniedException if
	 * the user is not allowed to read the entity to prevent a aggressor with
	 * informations about the existence of that specific Document.
	 * <p>
	 * The CallerPrincipial need to have at least the access level
	 * org.imixs.ACCESSLEVEL.READACCESS
	 * 
	 * <p>
	 * issue #230
	 * 
	 * In case a document is not flagged (not saved during same transaction), we
	 * detach the loaded entity. In case a document is flagged (saved during save
	 * transaction) we may not detach it, but make a deepCopy (clone) of the
	 * document instance. This will avoid the effect, that data written to a
	 * document get lost in a long running transaction with save and load calls.
	 * 
	 * @param id - the $uniqueid of the ItemCollection to be loaded
	 * @return ItemCollection object or null if the Document dose not exist or the
	 *         CallerPrincipal hat insufficient read access.
	 * 
	 */
  public ItemCollection load(String id) {
    boolean debug = logger.isLoggable(Level.FINE);
    long lLoadTime = System.currentTimeMillis();
    Document persistedDocument = null;
    if (id == null || id.isEmpty()) {
      return null;
    }
    persistedDocument = manager.find(Document.class, id);
    if (persistedDocument != null && isCallerReader(persistedDocument)) {
      ItemCollection result = null;
      if (persistedDocument.isPending()) {
        if (debug) {
          logger.finest("......clone manged entity \'" + id + "\' pending status=" + persistedDocument.isPending());
        }
        result = new ItemCollection(persistedDocument.getData());
      } else {
        result = new ItemCollection();
        result.setAllItems(persistedDocument.getData());
        manager.detach(persistedDocument);
      }
      updateMetaData(result, persistedDocument);
      if (documentEvents != null) {
        documentEvents.fire(new DocumentEvent(result, DocumentEvent.ON_DOCUMENT_LOAD));
      } else {
        logger.warning("Missing CDI support for Event<DocumentEvent> !");
      }
      if (debug) {
        logger.fine("...\'" + result.getUniqueID() + "\' loaded in " + (System.currentTimeMillis() - lLoadTime) + "ms");
      }
      return result;
    } else {
      return null;
    }
  }

  /**
	 * This method removes an ItemCollection from the database. If the
	 * CallerPrincipal is not allowed to access the ItemColleciton the method throws
	 * an AccessDeniedException.
	 * <p>
	 * The CallerPrincipial should have at least the access Role
	 * org.imixs.ACCESSLEVEL.AUTHORACCESS
	 * <p>
	 * Also the method removes the document form the lucene index.
	 * 
	 * 
	 * @param ItemCollection to be removed
	 * @throws AccessDeniedException
	 */
  public void remove(ItemCollection document) throws AccessDeniedException {
    if (document == null) {
      return;
    }
    Document persistedDocument = null;
    String sID = document.getItemValueString("$uniqueid");
    persistedDocument = manager.find(Document.class, sID);
    if (persistedDocument != null) {
      if (!isCallerReader(persistedDocument) || !isCallerAuthor(persistedDocument)) {
        throw new AccessDeniedException(OPERATION_NOTALLOWED, "remove - You are not allowed to perform this operation");
      }
      if (documentEvents != null) {
        documentEvents.fire(new DocumentEvent(document, DocumentEvent.ON_DOCUMENT_DELETE));
      } else {
        logger.warning("Missing CDI support for Event<DocumentEvent> !");
      }
      manager.remove(persistedDocument);
      if (!document.getItemValueBoolean(NOINDEX)) {
        removeDocumentFromIndex(document.getUniqueID());
      }
    } else {
      throw new AccessDeniedException(INVALID_UNIQUEID, "remove - invalid $uniqueid");
    }
  }

  /**
	 * Returns the total hits for a given search query. The provided search term
	 * will be extended with a users roles to test the read access level of each
	 * workitem matching the search term. The usernames and user roles will be
	 * search lowercase!
	 * 
	 * @see search(String, int, int, Sort, Operator)
	 * 
	 * @param sSearchTerm
	 * @return total hits of search result
	 * @throws QueryException in case the searchterm is not understandable.
	 */
  public int count(String searchTerm) throws QueryException {
    return count(searchTerm, 0);
  }

  /**
	 * Returns the total hits for a given search query. The provided search term
	 * will be extended with a users roles to test the read access level of each
	 * workitem matching the search term. The usernames and user roles will be
	 * search lowercase!
	 * 
	 * The optional param 'maxResult' can be set to overwrite the
	 * DEFAULT_MAX_SEARCH_RESULT.
	 * 
	 * @see search(String, int, int, Sort, Operator)
	 * 
	 * @param sSearchTerm
	 * @param maxResult       - max search result
	 * @param defaultOperator - optional to change the default search operator
	 * 
	 * @return total hits of search result
	 * @throws QueryException in case the searchterm is not understandable.
	 */
  public int count(String sSearchTerm, int maxResult) throws QueryException {
    indexUpdateService.updateIndex();
    return indexSearchService.getTotalHits(sSearchTerm, maxResult, null);
  }

  /**
	 * Returns the total pages for a given search term and a given page size.
	 * 
	 * @see count(String sSearchTerm)
	 * 
	 * @param searchTerm
	 * @param pageSize
	 * @return total pages of search result
	 * @throws QueryException in case the searchterm is not understandable.
	 */
  public int countPages(String searchTerm, int pageSize) throws QueryException {
    double pages = 1;
    double count = count(searchTerm);
    if (count > 0) {
      pages = Math.ceil(count / pageSize);
    }
    return ((int) pages);
  }

  /**
	 * The method returns a list of ItemCollections from the search-index. The
	 * method expects an valid Lucene search term.
	 * <p>
	 * The method returns only ItemCollections which are readable by the
	 * CallerPrincipal. With the pageSize and pageNumber it is possible to paginate.
	 * <p>
	 * The Transactiontype REQUIRES_NEW ensure that during the processing lifecycle
	 * an external service call did not overwrite the current document jpa object
	 * (see Issue #634)
	 * 
	 * @param searchTerm - Lucene search term
	 * @param pageSize   - total docs per page
	 * @param pageIndex  - number of page to start (default = 0)
	 * @return list of ItemCollection elements
	 * @throws QueryException
	 * 
	 * @see org.imixs.workflow.engine.index.SearchService
	 */
  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW) public List<ItemCollection> find(String searchTerm, int pageSize, int pageIndex) throws QueryException {
    return find(searchTerm, pageSize, pageIndex, null, false);
  }

  /**
	 * The method returns a sorted list of ItemCollections from the search-index.
	 * The result list can be sorted by a sortField and a sort direction.
	 * <p>
	 * The method expects an valid Lucene search term. The method returns only
	 * ItemCollections which are readable by the CallerPrincipal. With the pageSize
	 * and pageNumber it is possible to paginate.
	 * <p>
	 * The Transactiontype REQUIRES_NEW ensure that during the processing lifecycle
	 * an external service call did not overwrite the current document jpa object
	 * (see Issue #634)
	 * 
	 * @param searchTerm  - Lucene search term
	 * @param pageSize    - total docs per page
	 * @param pageIndex   - number of page to start (default = 0)
	 * 
	 * @param sortBy      -optional field to sort the result
	 * @param sortReverse - optional sort direction
	 * 
	 * @return list of ItemCollection elements
	 * @throws QueryException
	 * 
	 * @see org.imixs.workflow.engine.index.SearchService
	 */
  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW) public List<ItemCollection> find(String searchTerm, int pageSize, int pageIndex, String sortBy, boolean sortReverse) throws QueryException {
    boolean debug = logger.isLoggable(Level.FINE);
    if (debug) {
      logger.finest("......find - SearchTerm=" + searchTerm + "  , pageSize=" + pageSize + " pageNumber=" + pageIndex + " , sortBy=" + sortBy + " reverse=" + sortReverse);
    }
    SortOrder sortOrder = null;
    if (sortBy != null && !sortBy.isEmpty()) {
      sortOrder = new SortOrder(sortBy, sortReverse);
    }
    indexUpdateService.updateIndex();
    DefaultOperator defaultOperator = null;
    if (indexDefaultOperator != null && "OR".equals(indexDefaultOperator.toUpperCase())) {
      defaultOperator = DefaultOperator.OR;
    } else {
      defaultOperator = DefaultOperator.AND;
    }
    return indexSearchService.search(searchTerm, pageSize, pageIndex, sortOrder, defaultOperator, false);
  }

  /**
	 * The method returns a sorted list of Document Stubs from the search-index. A
	 * document stub contains only the items stored in the search index. These items
	 * can be defined by the property <code>lucence.indexFieldListStore</code>. See
	 * the LuceneUpdateService for details.
	 * <p>
	 * The result list can be sorted by a sortField and a sort direction.
	 * <p>
	 * The method expects an valid Lucene search term. The method returns only
	 * ItemCollections which are readable by the CallerPrincipal. With the pageSize
	 * and pageNumber it is possible to paginate.
	 * <p>
	 * 
	 * @param searchTerm  - Lucene search term
	 * @param pageSize    - total docs per page
	 * @param pageIndex   - number of page to start (default = 0)
	 * 
	 * @param sortBy      -optional field to sort the result
	 * @param sortReverse - optional sort direction
	 * 
	 * @return list of ItemCollection elements
	 * @throws QueryException
	 * 
	 * @see org.imixs.workflow.engine.index.SearchService
	 */
  public List<ItemCollection> findStubs(String searchTerm, int pageSize, int pageIndex, String sortBy, boolean sortReverse) throws QueryException {
    boolean debug = logger.isLoggable(Level.FINE);
    if (debug) {
      logger.finest("......find - SearchTerm=" + searchTerm + "  , pageSize=" + pageSize + " pageNumber=" + pageIndex + " , sortBy=" + sortBy + " reverse=" + sortReverse);
    }
    SortOrder sortOrder = null;
    if (sortBy != null && !sortBy.isEmpty()) {
      sortOrder = new SortOrder(sortBy, sortReverse);
    }
    indexUpdateService.updateIndex();
    DefaultOperator defaultOperator = null;
    ;
    if (indexDefaultOperator != null && "OR".equals(indexDefaultOperator.toUpperCase())) {
      defaultOperator = DefaultOperator.OR;
    } else {
      defaultOperator = DefaultOperator.AND;
    }
    return indexSearchService.search(searchTerm, pageSize, pageIndex, sortOrder, defaultOperator, true);
  }

  /**
	 * The method returns a collection of ItemCollections referred by a $uniqueid.
	 * <p>
	 * The method returns only ItemCollections which are readable by the
	 * CallerPrincipal. With the pageSize and pageNumber it is possible to paginate.
	 * <p>
	 * The Transactiontype REQUIRES_NEW ensure that during the processing lifecycle
	 * an external service call did not overwrite the current document jpa object
	 * (see Issue #634)
	 * 
	 * @param uniqueIdRef - $uniqueId to be referred by the collected documents
	 * @param pageSize    - total docs per page
	 * @param pageIndex   - number of page to start (default = 0)
	 * @return resultset
	 * 
	 */
  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW) public List<ItemCollection> findDocumentsByRef(String uniqueIdRef, int pageSize, int pageIndex) {
    String searchTerm = "(" + "$uniqueidref:\"" + uniqueIdRef + "\")";
    try {
      return find(searchTerm, pageSize, pageIndex);
    } catch (QueryException e) {
      logger.severe("findDocumentsByRef - invalid query: " + e.getMessage());
      return null;
    }
  }

  /**
	 * Returns an unordered list of all documents of a specific type. The method
	 * throws an InvalidAccessException in case no type attribute is defined.
	 * <p>
	 * The Transactiontype REQUIRES_NEW ensure that during the processing lifecycle
	 * an external service call did not overwrite the current document jpa object
	 * (see Issue #634)
	 * 
	 * @param type
	 * @return
	 * @throws InvalidAccessException
	 */
  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW) public List<ItemCollection> getDocumentsByType(String type) {
    if (type == null || type.isEmpty()) {
      throw new InvalidAccessException(INVALID_PARAMETER, "undefined type attribute");
    }
    String query = "SELECT document FROM Document AS document ";
    query += " WHERE document.type = \'" + type + "\'";
    query += " ORDER BY document.created DESC";
    return getDocumentsByQuery(query);
  }

  /**
	 * Returns all documents of by JPQL statement
	 * <p>
	 * The Transactiontype REQUIRES_NEW ensure that during the processing lifecycle
	 * an external service call did not overwrite the current document jpa object
	 * (see Issue #634)
	 * 
	 * @param query - JPQL statement
	 * @return
	 * 
	 */
  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW) public List<ItemCollection> getDocumentsByQuery(String query) {
    return getDocumentsByQuery(query, -1);
  }

  /**
	 * Returns all documents of by JPQL statement.
	 * <p>
	 * The Transactiontype REQUIRES_NEW ensure that during the processing lifecycle
	 * an external service call did not overwrite the current document jpa object
	 * (see Issue #634)
	 * 
	 * @param query     - JPQL statement
	 * @param maxResult - maximum result set
	 * @return
	 * 
	 */
  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW) public List<ItemCollection> getDocumentsByQuery(String query, int maxResult) {
    return getDocumentsByQuery(query, 0, maxResult);
  }

  /**
	 * Returns all documents of by JPQL statement.
	 * <p>
	 * The Transactiontype REQUIRES_NEW ensure that during the processing lifecycle
	 * an external service call did not overwrite the current document jpa object
	 * (see Issue #634)
	 * 
	 * @param query       - JPQL statement
	 * @param firstResult - first result
	 * @param maxResult   - maximum result set
	 * @return - result set
	 * 
	 */
  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW) public List<ItemCollection> getDocumentsByQuery(String query, int firstResult, int maxResult) {
    boolean debug = logger.isLoggable(Level.FINE);
    List<ItemCollection> result = new ArrayList<ItemCollection>();
    Query q = manager.createQuery(query);
    if (maxResult > 0) {
      q.setMaxResults(maxResult);
    }
    if (firstResult > 0) {
      q.setFirstResult(firstResult);
    }
    long l = System.currentTimeMillis();
    @SuppressWarnings(value = { "unchecked" }) Collection<Document> documentList = q.getResultList();
    if (documentList == null) {
      if (debug) {
        logger.finest("......getDocumentsByQuery - no ducuments found.");
      }
      return result;
    }
    for (Document doc : documentList) {
      if (isCallerReader(doc)) {
        ItemCollection _tmp = null;
        if (doc.isPending()) {
          if (debug) {
            logger.finest("......clone manged entity \'" + doc.getId() + "\' pending status=" + doc.isPending());
          }
          _tmp = new ItemCollection(doc.getData());
        } else {
          _tmp = new ItemCollection();
          _tmp.setAllItems(doc.getData());
          manager.detach(doc);
        }
        updateMetaData(_tmp, doc);
        result.add(_tmp);
        if (documentEvents != null) {
          documentEvents.fire(new DocumentEvent(_tmp, DocumentEvent.ON_DOCUMENT_LOAD));
        }
      }
    }
    if (debug) {
      logger.fine("...getDocumentsByQuery - found " + documentList.size() + " documents in " + (System.currentTimeMillis() - l) + " ms");
    }
    return result;
  }

  /**
	 * This method creates a backup of the result set form a Lucene search query.
	 * The document list will be stored into the file system. The method stores the
	 * Map from the ItemCollection to be independent from version upgrades. To
	 * manage large dataSets the method reads the documents in smaller blocks
	 * 
	 * @param entities
	 * @throws IOException
	 * @throws QueryException
	 */
  public void backup(String query, String filePath) throws IOException, QueryException {
    boolean hasMoreData = true;
    int JUNK_SIZE = 100;
    long totalcount = 0;
    int pageIndex = 0;
    int icount = 0;
    logger.info("backup - starting...");
    logger.info("backup - query=" + query);
    logger.info("backup - target=" + filePath);
    if (filePath == null || filePath.isEmpty()) {
      logger.severe("Invalid FilePath!");
      return;
    }
    FileOutputStream fos = new FileOutputStream(filePath);
    ObjectOutputStream out = new ObjectOutputStream(fos);
    while (hasMoreData) {
      Collection<ItemCollection> col = find(query, JUNK_SIZE, pageIndex);
      totalcount = totalcount + col.size();
      logger.info("backup - processing...... " + col.size() + " documents read....");
      if (col.size() < JUNK_SIZE) {
        hasMoreData = false;
        logger.finest("......all data read.");
      } else {
        pageIndex++;
        logger.finest("......next page...");
      }
      for (ItemCollection aworkitem : col) {
        Map<?, ?> hmap = aworkitem.getAllItems();
        out.writeObject(hmap);
        icount++;
      }
    }
    out.close();
    logger.info("backup - finished: " + icount + " documents read totaly.");
  }

  /**
	 * This method restores a backup from the file system and imports the Documents
	 * into the database.
	 * 
	 * @param filepath
	 * @throws IOException
	 */
  @SuppressWarnings(value = { "rawtypes", "unchecked" }) public void restore(String filePath) throws IOException {
    int JUNK_SIZE = 100;
    long totalcount = 0;
    long errorCount = 0;
    int icount = 0;
    FileInputStream fis = new FileInputStream(filePath);
    ObjectInputStream in = new ObjectInputStream(fis);
    logger.info("...starting restor form file " + filePath + "...");
    long l = System.currentTimeMillis();
    while (true) {
      try {
        Map hmap = (Map) in.readObject();
        ItemCollection itemCol = new ItemCollection(hmap);
        itemCol.removeItem(VERSION);
        itemCol = ctx.getBusinessObject(DocumentService.class).saveByNewTransaction(itemCol);
        totalcount++;
        icount++;
        if (icount >= JUNK_SIZE) {
          icount = 0;
          logger.info("...restored " + totalcount + " document in " + (System.currentTimeMillis() - l) + "ms....");
          l = System.currentTimeMillis();
        }
      } catch (java.io.EOFException eofe) {
        break;
      } catch (ClassNotFoundException e) {
        errorCount++;
        logger.warning("...error importing workitem at position " + (totalcount + errorCount) + " Error: " + e.getMessage());
      } catch (AccessDeniedException e) {
        errorCount++;
        logger.warning("...error importing workitem at position " + (totalcount + errorCount) + " Error: " + e.getMessage());
      }
    }
    in.close();
    String loginfo = "Import successfull! " + totalcount + " Entities imported. " + errorCount + " Errors.  Import FileName:" + filePath;
    logger.info(loginfo);
  }

  /**
	 * Verifies if the caller has write access to the current ItemCollection
	 * 
	 * @return
	 */
  public boolean isAuthor(ItemCollection itemcol) {
    @SuppressWarnings(value = { "unchecked" }) List<String> writeAccessList = itemcol.getItemValue(WRITEACCESS);
    if (ctx.isCallerInRole(ACCESSLEVEL_NOACCESS)) {
      return false;
    }
    if (ctx.isCallerInRole(ACCESSLEVEL_MANAGERACCESS) || ctx.isCallerInRole(ACCESSLEVEL_EDITORACCESS)) {
      return true;
    }
    if (ctx.isCallerInRole(ACCESSLEVEL_AUTHORACCESS)) {
      if (isUserContained(writeAccessList)) {
        return true;
      }
    }
    return false;
  }

  /**
	 * This method udates the metadata of a new loaded ItemCollection based on the
	 * document attributes.
	 * <p>
	 * The metadata which is updated is:
	 * <ul>
	 * <li>$version - set to current version if OptimisticLocking is not
	 * disabled</li>
	 * <li>$modified - the modify timestamp from the document entity</li>
	 * <li>$isauthor - computed on the current users access level</li>
	 * </ul>
	 * 
	 * @see issue #497
	 * @param itemColection
	 * @param doc
	 */
  private void updateMetaData(ItemCollection itemColection, Document doc) {
    if (disableOptimisticLocking) {
      itemColection.removeItem(VERSION);
    } else {
      itemColection.replaceItemValue(VERSION, doc.getVersion());
    }
    itemColection.replaceItemValue("$modified", doc.getModified().getTime());
    itemColection.replaceItemValue(ISAUTHOR, isCallerAuthor(doc));
  }

  /**
	 * This method checks if the Caller Principal has read access for the document.
	 * 
	 * @return true if user has readaccess
	 */
  private boolean isCallerReader(Document document) {
    ItemCollection itemcol = ItemCollection.createByReference(document.getData());
    @SuppressWarnings(value = { "unchecked" }) List<String> readAccessList = itemcol.getItemValue(READACCESS);
    if (ctx.isCallerInRole(ACCESSLEVEL_NOACCESS)) {
      return false;
    }
    if (ctx.isCallerInRole(ACCESSLEVEL_MANAGERACCESS)) {
      return true;
    }
    if (isEmptyList(readAccessList) || isUserContained(readAccessList)) {
      return true;
    }
    return false;
  }

  /**
	 * Verifies if the caller has write access to the given ItemCollection
	 * (document).
	 * 
	 * @return true if the current user has author access
	 */
  private boolean isCallerAuthor(Document document) {
    ItemCollection itemcol = ItemCollection.createByReference(document.getData());
    return isAuthor(itemcol);
  }

  /**
	 * This method returns true if the given list is empty or contains only null or
	 * '' values.
	 * 
	 * @param aList
	 * @return
	 */
  public boolean isEmptyList(List<String> aList) {
    if (aList == null || aList.size() == 0) {
      return true;
    }
    for (String aEntry : aList) {
      if (aEntry != null && !aEntry.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /**
	 * This method returns true if the given id is a valid UUID or SnapshotID (UUI +
	 * timestamp
	 * <p>
	 * We also need to support the old uid formats
	 * <code>4832b09a1a-20c38abd-1519421083952</code>
	 * 
	 * @param uid
	 * @return
	 */
  public boolean isValidUIDPattern(String uid) {
    boolean valid = uid.matches(REGEX_UUID);
    if (!valid) {
      valid = uid.matches(REGEX_OLDUID);
    }
    return valid;
  }
}