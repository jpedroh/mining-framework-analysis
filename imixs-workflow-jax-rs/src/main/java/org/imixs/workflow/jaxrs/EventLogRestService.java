package org.imixs.workflow.jaxrs;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import javax.ws.rs.PUT;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.engine.EventLogService;
import org.imixs.workflow.engine.index.SearchService;
import org.imixs.workflow.engine.jpa.EventLog;
import org.imixs.workflow.xml.XMLDataCollection;
import org.imixs.workflow.xml.XMLDataCollectionAdapter;
import jakarta.ejb.Stateless;
import org.imixs.workflow.xml.XMLDocument;
import jakarta.persistence.OptimisticLockException;
import org.imixs.workflow.xml.XMLDocumentAdapter;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The EventLogRestService supports methods to access the event log entries by
 * different kind of request URIs
 * 
 * @author rsoika
 * 
 */
@Path(value = "/eventlog") @Produces(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_XML }) @Stateless public class EventLogRestService {
  @Inject private EventLogService eventLogService;

  @Context private HttpServletRequest servletRequest;

  private static Logger logger = Logger.getLogger(EventLogRestService.class.getName());

  /**
     * Returns all eventLog entries.
     * 
     * @param pageSize  - page size
     * @param pageIndex - page index (default = 0)
     * @param items     - optional list of items
     * @return result set.
     * 
     * @param maxCount - max count of returned eventLogEntries (default 99)
     * @return - xmlDataCollection containing all matching eventLog entries
     */
  @GET @Path(value = "/") public XMLDataCollection getAllEventLogEntries(@DefaultValue(value = "" + SearchService.DEFAULT_PAGE_SIZE) @QueryParam(value = "pageSize") int pageSize, @DefaultValue(value = "0") @QueryParam(value = "pageIndex") int pageIndex) {
    logger.finest("......get all eventLogEntries");
    int firstResult = pageIndex * pageSize;
    List<EventLog> eventLogEntries = eventLogService.findAllEvents(firstResult, pageSize);
    List<ItemCollection> result = new ArrayList<ItemCollection>();
    for (EventLog eventLog : eventLogEntries) {
      result.add(buildItemCollection(eventLog));
    }
    return XMLDataCollectionAdapter.getDataCollection(result);
  }

  /**
     * Returns a set of eventLog entries for a given topic. Multiple topics can be
     * separated by a swung dash (~).
     * 
     * @param topic    - topic to search event log entries.
     * @param maxCount - max count of returned eventLogEntries (default 99)
     * @return - xmlDataCollection containing all matching eventLog entries
     */
  @GET @Path(value = "/{topic}") public XMLDataCollection getEventLogEntriesByTopic(@PathParam(value = "topic") String topic, @DefaultValue(value = "99") @QueryParam(value = "maxCount") int maxCount) {
    logger.finest("......get eventLogEntry by topic: " + topic);
    String[] topicList = topic.split("~");
    List<EventLog> eventLogEntries = eventLogService.findEventsByTopic(maxCount, topicList);
    List<ItemCollection> result = new ArrayList<ItemCollection>();
    for (EventLog eventLog : eventLogEntries) {
      result.add(buildItemCollection(eventLog));
    }
    return XMLDataCollectionAdapter.getDataCollection(result);
  }

  /**
     * This method locks an eventLog entry for processing. The topic will be
     * suffixed with '.lock' to indicate that this topic is locked by a process. If
     * a lock is successful a client can exclusive process this eventLog entry.
     * 
     * @param id - id of the event log entry
     * @return the method returns a Response OK in case of a successful lock.
     */
  @POST @Path(value = "/lock/{id}") public Response lockEventLogEntry(@PathParam(value = "id") String id) {
    EventLog _eventLogEntry = eventLogService.getEvent(id);
    if (_eventLogEntry != null) {
      try {
        if (eventLogService.lock(_eventLogEntry)) {
          return Response.status(Response.Status.OK).build();
        } else {
          return Response.status(Response.Status.CONFLICT).build();
        }
      } catch (OptimisticLockException e) {
        logger.info("...unable to lock EventLock: " + e.getMessage());
      }
    }
    return Response.status(Response.Status.CONFLICT).build();
  }

  /**
     * This method unlocks an eventLog entry. The topic suffix '.lock' will be
     * removed.
     * 
     * @param id - id of the event log entry
     */
  @POST @Path(value = "/unlock/{id}") public Response unlockEventLogEntry(@PathParam(value = "id") String id) {
    EventLog _eventLogEntry = eventLogService.getEvent(id);
    if (_eventLogEntry != null) {
      try {
        if (eventLogService.unlock(_eventLogEntry)) {
          return Response.status(Response.Status.OK).build();
        } else {
          return Response.status(Response.Status.CONFLICT).build();
        }
      } catch (OptimisticLockException e) {
        logger.info("...unable to lock EventLock: " + e.getMessage());
      }
    }
    return Response.status(Response.Status.CONFLICT).build();
  }

  /**
     * This method unlocks eventlog entries which are older than 1 minute. We assume
     * that these events are deadlocks.
     *
     * @param interval - interval in millis
     * @param topic    - topic to search event log entries.
     */
  @POST @Path(value = "/release/{interval}/{topic}") public void releaseDeadLocks(@PathParam(value = "interval") long deadLockInterval, @PathParam(value = "topic") String topic) {
    logger.finest("......releaseDeadLocks: " + topic);
    String[] topicList = topic.split("~");
    eventLogService.releaseDeadLocks(deadLockInterval, topicList);
  }

  /**
     * Deletes a eventLog entry by its $uniqueID
     * 
     * @param name of report or uniqueid
     */
  @DELETE @Path(value = "/{id}") public void deleteEventLogEntry(@PathParam(value = "id") String id) {
    eventLogService.removeEvent(id);
  }

  /**
     * Creates/updates a new event log entry.
     *
     * @param topic    - the topic of the event.
     * @param id       - uniqueId of the document to be assigned to the event
     * @param document - optional document data to be stored in the event log entry
     */
  @PUT @Path(value = "/{topic}/{id}") public void createEventLogEntry(@PathParam(value = "topic") String topic, @PathParam(value = "id") String refID, XMLDocument xmlworkitem) {
    if (xmlworkitem != null) {
      eventLogService.createEvent(topic, refID, XMLDocumentAdapter.putDocument(xmlworkitem));
    } else {
      eventLogService.createEvent(topic, refID);
    }
  }

  /**
     * This helper method converts a EventLog entity into a ItemCollection.
     * 
     * @param eventLog - event log entity
     * @return - ItemCollection
     */
  private ItemCollection buildItemCollection(EventLog eventLog) {
    if (eventLog == null) {
      return null;
    }
    ItemCollection itemColEvent = new ItemCollection();
    itemColEvent.setItemValue("id", eventLog.getId());
    itemColEvent.setItemValue("ref", eventLog.getRef());
    itemColEvent.setItemValue("created", eventLog.getCreated());
    itemColEvent.setItemValue("topic", eventLog.getTopic());
    itemColEvent.setItemValue("data", eventLog.getData());
    return itemColEvent;
  }
}