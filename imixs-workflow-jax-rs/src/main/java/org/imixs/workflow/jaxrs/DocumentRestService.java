package org.imixs.workflow.jaxrs;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.WorkflowKernel;
import org.imixs.workflow.engine.DocumentService;
import org.imixs.workflow.engine.index.SchemaService;
import org.imixs.workflow.engine.index.SearchService;
import org.imixs.workflow.exceptions.AccessDeniedException;
import org.imixs.workflow.exceptions.ImixsExceptionHandler;
import org.imixs.workflow.exceptions.QueryException;
import org.imixs.workflow.xml.XMLCount;
import org.imixs.workflow.xml.XMLDataCollectionAdapter;
import org.imixs.workflow.xml.XMLDocument;
import org.imixs.workflow.xml.XMLDocumentAdapter;
import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The DocumentService provides methods to access the DocumentService EJB
 * 
 * @author rsoika
 * 
 */
@Path(value = "/documents") @Produces(value = { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML }) @Stateless public class DocumentRestService {
  @Inject private DocumentService documentService;

  @Inject private SchemaService schemaService;

  @jakarta.ws.rs.core.Context private HttpServletRequest servletRequest;

  private static Logger logger = Logger.getLogger(DocumentRestService.class.getName());

  @GET @Produces(value = MediaType.APPLICATION_XHTML_XML) public StreamingOutput getRoot() {
    return new StreamingOutput() {
      public void write(OutputStream out) throws IOException, WebApplicationException {
        out.write("<div class=\"root\">".getBytes());
        out.write("<a href=\"/{uniqueid}\" type=\"application/xml\" rel=\"{uniqueid}\"/>".getBytes());
        out.write("</div>".getBytes());
      }
    };
  }

  @GET @Produces(value = "text/html") @Path(value = "/help") public StreamingOutput getHelpHTML() {
    return new StreamingOutput() {
      public void write(OutputStream out) throws IOException, WebApplicationException {
        out.write("<html><head>".getBytes());
        out.write("<style>".getBytes());
        out.write("table {padding:0px;width: 100%;margin-left: -2px;margin-right: -2px;}".getBytes());
        out.write("body,td,select,input,li {font-family: Verdana, Helvetica, Arial, sans-serif;font-size: 13px;}".getBytes());
        out.write("table th {color: white;background-color: #bbb;text-align: left;font-weight: bold;}".getBytes());
        out.write("table th,table td {font-size: 12px;}".getBytes());
        out.write("table tr.a {background-color: #ddd;}".getBytes());
        out.write("table tr.b {background-color: #eee;}".getBytes());
        out.write("</style>".getBytes());
        out.write("</head><body>".getBytes());
        out.write("<h1>Imixs-Document REST Service</h1>".getBytes());
        out.write("<p>See the <a href=\"http://www.imixs.org/xml/restservice/documentservice.html\" target=\"_blank\">Imixs REST Service API</a> for more information about this Service.</p>".getBytes());
        out.write("</body></html>".getBytes());
      }
    };
  }

  /**
     * returns a single document defined by $uniqueid
     * 
     * Regex for
     * 
     * UID - e.g: bcc776f9-4e5a-4272-a613-9f5ebf35354d
     * 
     * Snapshot: bcc776f9-4e5a-4272-a613-9f5ebf35354d-9b6655
     * 
     * deprecated format : 132d37bfd51-9a7868
     * 
     * @param uniqueid
     * @return
     */
  @GET @Path(value = "/{uniqueid : ([0-9a-f]{8}-.*|[0-9a-f]{11}-.*)}") public Response getDocument(@PathParam(value = "uniqueid") String uniqueid, @QueryParam(value = "items") String items, @QueryParam(value = "format") String format) {
    ItemCollection document = null;
    try {
      document = documentService.load(uniqueid);
      if (document == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return convertResult(document, items, format);
  }

  /**
     * Returns a resultset for a lucene Search Query
     * 
     * @param query
     * @param pageSize
     * @param pageIndex
     * @param items
     * @return
     */
  @GET @Path(value = "/search/{query}") public Response findDocumentsByQuery(@PathParam(value = "query") String query, @DefaultValue(value = "-1") @QueryParam(value = "pageSize") int pageSize, @DefaultValue(value = "0") @QueryParam(value = "pageIndex") int pageIndex, @QueryParam(value = "sortBy") String sortBy, @QueryParam(value = "sortReverse") boolean sortReverse, @QueryParam(value = "items") String items, @QueryParam(value = "format") String format) {
    List<ItemCollection> result = null;
    try {
      String decodedQuery = URLDecoder.decode(query, "UTF-8");
      result = documentService.find(decodedQuery, pageSize, pageIndex, sortBy, sortReverse);
    } catch (Exception e) {
      logger.warning("Invalid Search Query: " + e.getMessage());
      ItemCollection error = new ItemCollection();
      error.setItemValue("$error_message", e.getMessage());
      error.setItemValue("$error_code", "" + Response.Status.NOT_ACCEPTABLE);
      return Response.ok(XMLDataCollectionAdapter.getDataCollection(error)).status(Response.Status.NOT_ACCEPTABLE).build();
    }
    return convertResultList(result, items, format);
  }

  /**
     * Returns a resultset for a JPQL statement
     * 
     * @param query - JPQL statement
     * @param pageSize - page size
     * @param pageIndex - page index (default = 0)
     * @param items - optional list of items
     * @return result set.
     */
  @GET @Path(value = "/jpql/{query}") public Response findDocumentsByJPQL(@PathParam(value = "query") String query, @DefaultValue(value = "" + SearchService.DEFAULT_PAGE_SIZE) @QueryParam(value = "pageSize") int pageSize, @DefaultValue(value = "0") @QueryParam(value = "pageIndex") int pageIndex, @QueryParam(value = "items") String items, @QueryParam(value = "format") String format) {
    List<ItemCollection> result = null;
    try {
      String decodedQuery = URLDecoder.decode(query, "UTF-8");
      int firstResult = pageIndex * pageSize;
      result = documentService.getDocumentsByQuery(decodedQuery, firstResult, pageSize);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return convertResultList(result, items, format);
  }

  /**
     * Returns a total hits for a lucene Search Query
     * 
     * @param query
     * @param pageSize
     * @param pageIndex
     * @param items
     * @return
     */
  @GET @Path(value = "/count/{query}") public Response countTotalHitsByQuery(@PathParam(value = "query") String query, @DefaultValue(value = "-1") @QueryParam(value = "maxResult") int maxResult, @QueryParam(value = "format") String format) {
    XMLCount xmlcount = new XMLCount();
    String decodedQuery;
    try {
      decodedQuery = URLDecoder.decode(query, "UTF-8");
      xmlcount.count = (long) documentService.count(decodedQuery, maxResult);
    } catch (UnsupportedEncodingException | QueryException e) {
      xmlcount.count = 0l;
      logger.severe(e.getMessage());
    }
    if ("json".equals(format)) {
      return Response.ok(xmlcount).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).build();
    } else {
      if ("xml".equals(format)) {
        return Response.ok(xmlcount).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML).build();
      } else {
        return Response.ok(xmlcount).build();
      }
    }
  }

  /**
     * Returns the total pages for a lucene Search Query and a given page size.
     * 
     * @param query
     * @param pageSize
     * @param pageIndex
     * @param items
     * @return
     */
  @GET @Path(value = "/countpages/{query}") public Response countTotalPagesByQuery(@PathParam(value = "query") String query, @DefaultValue(value = "-1") @QueryParam(value = "pageSize") int pageSize, @QueryParam(value = "format") String format) {
    XMLCount xmlcount = new XMLCount();
    String decodedQuery;
    try {
      decodedQuery = URLDecoder.decode(query, "UTF-8");
      xmlcount.count = (long) documentService.countPages(decodedQuery, pageSize);
    } catch (UnsupportedEncodingException | QueryException e) {
      xmlcount.count = 0l;
      logger.severe(e.getMessage());
    }
    if ("json".equals(format)) {
      return Response.ok(xmlcount).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).build();
    } else {
      if ("xml".equals(format)) {
        return Response.ok(xmlcount).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML).build();
      } else {
        return Response.ok(xmlcount).build();
      }
    }
  }

  /**
     * The method saves a document provided in xml format. The caller need to be
     * assigned to the access role 'org.imixs.ACCESSLEVEL.MANAGERACCESS'
     * 
     * Note: the method merges the content of the given document into an existing
     * one because the DocumentService method save() did not merge an entity. But
     * the rest service typically consumes only a subset of attributes. So this is
     * the reason why we merge the entity here. In different to the behavior of the
     * DocumentService the WorkflowService method process() did this merge
     * automatically.
     * 
     * @param xmlworkitem - document to be saved
     * @param items       - optional item list to be returned in the result
     * @return
     */
  @POST @Produces(value = MediaType.APPLICATION_XML) @Consumes(value = { MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON }) public Response postDocument(XMLDocument xmlworkitem, @QueryParam(value = "items") String items) {
    if (servletRequest.isUserInRole("org.imixs.ACCESSLEVEL.MANAGERACCESS") == false) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    ItemCollection workitem;
    workitem = XMLDocumentAdapter.putDocument(xmlworkitem);
    if (workitem == null) {
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }
    try {
      ItemCollection currentInstance = documentService.load(workitem.getItemValueString(WorkflowKernel.UNIQUEID));
      if (currentInstance != null) {
        currentInstance.replaceAllItems(workitem.getAllItems());
        workitem = currentInstance;
      }
      workitem.removeItem("$error_code");
      workitem.removeItem("$error_message");
      workitem = documentService.save(workitem);
    } catch (AccessDeniedException e) {
      logger.severe(e.getMessage());
      workitem = ImixsExceptionHandler.addErrorMessage(e, workitem);
    } catch (RuntimeException e) {
      logger.severe(e.getMessage());
      workitem = ImixsExceptionHandler.addErrorMessage(e, workitem);
    }
    try {
      if (workitem.hasItem("$error_code")) {
        logger.severe(workitem.getItemValueString("$error_code") + ": " + workitem.getItemValueString("$error_message"));
        return Response.ok(XMLDataCollectionAdapter.getDataCollection(workitem), MediaType.APPLICATION_XML).status(Response.Status.NOT_ACCEPTABLE).build();
      } else {
        return Response.ok(XMLDataCollectionAdapter.getDataCollection(workitem, DocumentRestService.getItemList(items)), MediaType.APPLICATION_XML).build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }
  }

  /**
     * Delegater putEntity @PUT
     * 
     * @see putWorkitemDefault
     * @param xmlworkitem - document to be saved
     * @param items       - optional item list to be returned in the result
     * @return
     */
  @PUT @Produces(value = MediaType.APPLICATION_XML) @Consumes(value = { MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON }) public Response putDocument(XMLDocument xmlworkitem, @QueryParam(value = "items") String items) {
    logger.finest("putDocument @PUT /  delegate to POST....");
    return postDocument(xmlworkitem, items);
  }

  /**
     * This method deletes an entity
     * 
     */
  @DELETE @Path(value = "/{uniqueid : ([0-9a-f]{8}-.*|[0-9a-f]{11}-.*)}") public Response deleteEntity(@PathParam(value = "uniqueid") String uniqueid) {
    if (servletRequest.isUserInRole("org.imixs.ACCESSLEVEL.MANAGERACCESS") == false) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    ItemCollection entity = documentService.load(uniqueid);
    if (entity != null) {
      documentService.remove(entity);
    }
    return Response.status(Response.Status.OK).build();
  }

  /**
     * This method creates a backup of the result set form a JQPL query. The entity
     * list will be stored into the file system. The backup can be restored by
     * calling the restore method
     * 
     * 
     * @param query
     * @param filepath - path in server filesystem
     * @param snapshots - opitonal backup snapshots only
     * @return
     */
  @PUT @Path(value = "/backup/{query}") public Response backup(@PathParam(value = "query") String query, @QueryParam(value = "filepath") String filepath, @QueryParam(value = "snapshots") boolean snapshots) {
    if (servletRequest.isUserInRole("org.imixs.ACCESSLEVEL.MANAGERACCESS") == false) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      String decodedQuery = URLDecoder.decode(query, "UTF-8");
      documentService.backup(decodedQuery, filepath, snapshots);
    } catch (IOException e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    } catch (QueryException e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    return Response.status(Response.Status.OK).build();
  }

  /**
     * This method restores a backup from the fileSystem
     * 
     * @param filepath - path in server fileSystem
     * @return
     */
  @GET @Path(value = "/restore") public Response restore(@QueryParam(value = "filepath") String filepath) {
    if (servletRequest.isUserInRole("org.imixs.ACCESSLEVEL.MANAGERACCESS") == false) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      documentService.restore(filepath);
    } catch (IOException e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    return Response.status(Response.Status.OK).build();
  }

  /**
     * Returns the IndexFieldListNoAnalyse from the lucensUpdateService
     * 
     * @return
     * @throws Exception
     */
  @GET @Path(value = "/configuration") @Produces(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON }) public Response getConfiguration(@QueryParam(value = "format") String format) throws Exception {
    if (servletRequest.isUserInRole("org.imixs.ACCESSLEVEL.MANAGERACCESS") == false) {
      return null;
    }
    ItemCollection config = schemaService.getConfiguration();
    return convertResult(config, null, format);
  }

  /**
     * This method returns a List object from a given comma separated string. The
     * method returns null if no elements are found. The provided parameter looks
     * typical like this: <code>
     *   txtWorkflowStatus,numProcessID,txtName
     * </code>
     * 
     * @param items
     * @return
     */
  protected static List<String> getItemList(String items) {
    if (items == null || "".equals(items)) {
      return null;
    }
    Vector<String> v = new Vector<String>();
    StringTokenizer st = new StringTokenizer(items, ",");
    while (st.hasMoreTokens()) {
      v.add(st.nextToken());
    }
    return v;
  }

  /**
     * This method converts a single ItemCollection into a Jax-rs response object.
     * <p>
     * The method expects optional items and format string (json|xml)
     * <p>
     * In case the result set is null, than the method returns an empty collection.
     * 
     * @param result list of ItemCollection
     * @param items  - optional item list
     * @param format - optional format string (json|xml)
     * @return jax-rs Response object.
     */
  protected Response convertResult(ItemCollection workitem, String items, String format) {
    if (workitem == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    if ("json".equals(format)) {
      return Response.ok(XMLDataCollectionAdapter.getDataCollection(workitem, DocumentRestService.getItemList(items))).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).build();
    } else {
      if ("xml".equals(format)) {
        return Response.ok(XMLDataCollectionAdapter.getDataCollection(workitem, DocumentRestService.getItemList(items))).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML).build();
      } else {
        return Response.ok(XMLDataCollectionAdapter.getDataCollection(workitem, DocumentRestService.getItemList(items))).build();
      }
    }
  }

  /**
     * This method converts a ItemCollection List into a Jax-rs response object.
     * <p>
     * The method expects optional items and format string (json|xml)
     * <p>
     * In case the result set is null, than the method returns an empty collection.
     * 
     * @param result list of ItemCollection
     * @param items  - optional item list
     * @param format - optional format string (json|xml)
     * @return jax-rs Response object.
     */
  public Response convertResultList(List<ItemCollection> result, String items, String format) {
    if (result == null) {
      result = new ArrayList<ItemCollection>();
    }
    if ("json".equals(format)) {
      return Response.ok(XMLDataCollectionAdapter.getDataCollection(result, DocumentRestService.getItemList(items))).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).build();
    } else {
      if ("xml".equals(format)) {
        return Response.ok(XMLDataCollectionAdapter.getDataCollection(result, DocumentRestService.getItemList(items))).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML).build();
      } else {
        return Response.ok(XMLDataCollectionAdapter.getDataCollection(result, DocumentRestService.getItemList(items))).build();
      }
    }
  }
}