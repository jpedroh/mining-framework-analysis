package org.imixs.workflow.engine;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.QuerySelector;
import org.imixs.workflow.engine.scheduler.Scheduler;
import org.imixs.workflow.engine.scheduler.SchedulerException;
import org.imixs.workflow.engine.scheduler.SchedulerService;
import org.imixs.workflow.exceptions.AccessDeniedException;
import org.imixs.workflow.exceptions.ModelException;
import org.imixs.workflow.exceptions.QueryException;

/**
 * This EJB implements a Imixs Scheduler Interface and scans workitems for
 * scheduled activities.
 * <p>
 * The configuration of the scheduler is based on the Imixs Scheduler API.
 * 
 * @author rsoika
 * @version 1.0
 */
public class WorkflowScheduler implements Scheduler {
  final static public String NAME = "org.imixs.workflow.scheduler";

  final static public int OFFSET_SECONDS = 0;

  final static public int OFFSET_MINUTES = 1;

  final static public int OFFSET_HOURS = 2;

  final static public int OFFSET_DAYS = 3;

  final static public int OFFSET_WORKDAYS = 4;

  final static private int MAX_WORKITEM_COUNT = 1000;

  private static Logger logger = Logger.getLogger(WorkflowScheduler.class.getName());

  @Inject private WorkflowService workflowService;

  @Inject private DocumentService documentService;

  @Inject private ModelService modelService;

  @Inject private SchedulerService schedulerService;

  @Inject @Any protected Instance<QuerySelector> selectors;

  @Resource private SessionContext ctx;

  private int iProcessWorkItems = 0;

  private List<String> unprocessedIDs = null;

  /**
     * This method checks if a workitem (doc) is in due. There are 4 different cases
     * which will be compared: The case is determined by the keyScheduledBaseObject
     * of the activity entity
     * 
     * Basis : keyScheduledBaseObject "last process"=1, "last Modification"=2
     * "Creation"=3 "Field"=4
     * 
     * The logic is not the best one but it works. So we are open for any kind of
     * improvements
     * 
     * @return true if workitem is is due
     */
  public boolean workItemInDue(ItemCollection doc, ItemCollection docActivity) {
    try {
      int iCompareType = -1;
      int iOffsetUnit = -1;
      int iOffset = 0;
      Date dateTimeCompare = null;
      String suniqueid = doc.getItemValueString("$uniqueid");
      String sDelayUnit = docActivity.getItemValueString("keyActivityDelayUnit");
      try {
        iOffsetUnit = Integer.parseInt(sDelayUnit);
        if (iOffsetUnit < 1 || iOffsetUnit > 4) {
          logger.warning("error parsing delay in ActivityEntity " + docActivity.getItemValueInteger("numProcessID") + "." + docActivity.getItemValueInteger("numActivityID") + " : unsuported keyActivityDelayUnit=" + sDelayUnit);
          return false;
        }
      } catch (NumberFormatException nfe) {
        logger.warning("error parsing delay in ActivityEntity " + docActivity.getItemValueInteger("numProcessID") + "." + docActivity.getItemValueInteger("numActivityID") + " :" + nfe.getMessage());
        return false;
      }
      iOffset = docActivity.getItemValueInteger("numActivityDelay");
      if ("1".equals(sDelayUnit)) {
        sDelayUnit = "minutes";
      }
      if ("2".equals(sDelayUnit)) {
        sDelayUnit = "hours";
      }
      if ("3".equals(sDelayUnit)) {
        sDelayUnit = "days";
      }
      if ("4".equals(sDelayUnit)) {
        sDelayUnit = "workdays";
      }
      logger.finest("......" + suniqueid + " offset =" + iOffset + " " + sDelayUnit);
      iCompareType = docActivity.getItemValueInteger("keyScheduledBaseObject");
      Date dateTimeNow = Calendar.getInstance().getTime();
      switch (iCompareType) {
        case 1:
        {
          logger.finest("......" + suniqueid + ": CompareType = last event");
          if (!doc.hasItem("$lastEventDate")) {
            logger.info("migrating $lasteventdate...");
            if (doc.hasItem("$lastProcessingDate")) {
              doc.replaceItemValue("$lastEventDate", doc.getItemValue("$lastProcessingDate"));
            } else {
              doc.replaceItemValue("$lastEventDate", doc.getItemValue("timWorkflowLastAccess"));
            }
          }
          dateTimeCompare = doc.getItemValueDate("$lastEventDate");
          if (dateTimeCompare == null) {
            logger.warning(suniqueid + ": item \'$lastEventDate\' is missing!");
            return false;
          }
          logger.finest("......" + suniqueid + ": $lastEventDate=" + dateTimeCompare);
          dateTimeCompare = adjustBaseDate(dateTimeCompare, iOffsetUnit, iOffset);
          if (dateTimeCompare != null) {
            return dateTimeCompare.before(dateTimeNow);
          } else {
            return false;
          }
        }
        case 2:
        {
          logger.finest("......" + suniqueid + ": CompareType = last modify");
          dateTimeCompare = doc.getItemValueDate("$modified");
          logger.finest("......" + suniqueid + ": modified=" + dateTimeCompare);
          dateTimeCompare = adjustBaseDate(dateTimeCompare, iOffsetUnit, iOffset);
          if (dateTimeCompare != null) {
            return dateTimeCompare.before(dateTimeNow);
          } else {
            return false;
          }
        }
        case 3:
        {
          logger.finest("......" + suniqueid + ": CompareType = creation");
          dateTimeCompare = doc.getItemValueDate("$created");
          logger.finest("......" + suniqueid + ": doc.getCreated() =" + dateTimeCompare);
          dateTimeCompare = adjustBaseDate(dateTimeCompare, iOffsetUnit, iOffset);
          if (dateTimeCompare != null) {
            return dateTimeCompare.before(dateTimeNow);
          } else {
            return false;
          }
        }
        case 4:
        {
          String sNameOfField = docActivity.getItemValueString("keyTimeCompareField");
          logger.finest("......" + suniqueid + ": CompareType = field: \'" + sNameOfField + "\'");
          if (!doc.hasItem(sNameOfField)) {
            logger.finest("......" + suniqueid + ": CompareType =" + sNameOfField + " no value found!");
            return false;
          }
          dateTimeCompare = doc.getItemValueDate(sNameOfField);
          logger.finest("......" + suniqueid + ": " + sNameOfField + "=" + dateTimeCompare);
          dateTimeCompare = adjustBaseDate(dateTimeCompare, iOffsetUnit, iOffset);
          if (dateTimeCompare != null) {
            logger.finest("......" + suniqueid + ": Compare " + dateTimeCompare + " <-> " + dateTimeNow);
            if (dateTimeCompare.before(dateTimeNow)) {
              logger.finest("......" + suniqueid + " isInDue!");
            }
            return dateTimeCompare.before(dateTimeNow);
          } else {
            return false;
          }
        }
        default:
        {
          logger.warning("Time Base is not defined, verify model!");
          return false;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
     * This method adds workdays (MONDAY - FRIDAY) to a given calendar object. If
     * the number of days is negative than this method subtracts the working days
     * from the calendar object.
     * 
     * 
     * @param cal
     * @param days
     * @return new calendar instance
     */
  public Calendar addWorkDays(final Calendar baseDate, final int days) {
    Calendar resultDate = null;
    Calendar workCal = Calendar.getInstance();
    workCal.setTime(baseDate.getTime());
    int currentWorkDay = workCal.get(Calendar.DAY_OF_WEEK);
    if (currentWorkDay == Calendar.SATURDAY) {
      workCal.add(Calendar.DAY_OF_MONTH, (days < 0 ? -1 : +2));
      currentWorkDay = workCal.get(Calendar.DAY_OF_WEEK);
    }
    if (currentWorkDay == Calendar.SUNDAY) {
      workCal.add(Calendar.DAY_OF_MONTH, (days < 0 ? -2 : +1));
      currentWorkDay = workCal.get(Calendar.DAY_OF_WEEK);
    }
    if (currentWorkDay >= Calendar.MONDAY && currentWorkDay <= Calendar.FRIDAY) {
      boolean inCurrentWeek = false;
      if (days > 0) {
        inCurrentWeek = (currentWorkDay + days < 7);
      } else {
        inCurrentWeek = (currentWorkDay + days > 1);
      }
      if (inCurrentWeek) {
        workCal.add(Calendar.DAY_OF_MONTH, days);
        resultDate = workCal;
      } else {
        int totalDays = 0;
        int daysInCurrentWeek = 0;
        if (days > 0) {
          daysInCurrentWeek = Calendar.SATURDAY - currentWorkDay;
          totalDays = daysInCurrentWeek + 2;
        } else {
          daysInCurrentWeek = -(currentWorkDay - Calendar.SUNDAY);
          totalDays = daysInCurrentWeek - 2;
        }
        int restTotalDays = days - daysInCurrentWeek;
        int x = restTotalDays / 5;
        totalDays += restTotalDays + (x * 2);
        workCal.add(Calendar.DAY_OF_MONTH, totalDays);
        resultDate = workCal;
      }
    }
    if (resultDate != null) {
      logger.finest("......addWorkDays (" + baseDate.getTime() + ") + " + days + " = (" + resultDate.getTime() + ")");
    }
    return resultDate;
  }

  /**
     * This method process scheduled workitems. The method updates the property
     * 'datLastRun'
     * 
     * Because of bug: https://java.net/jira/browse/GLASSFISH-20673 we check the
     * imixsDayOfWeek
     * 
     * @param timer
     * @throws AccessDeniedException
     */
  @Override public ItemCollection run(ItemCollection configItemCollection) throws SchedulerException {
    iProcessWorkItems = 0;
    unprocessedIDs = new ArrayList<String>();
    try {
      List<String> modelVersions = modelService.getVersions();
      Collections.sort(modelVersions, Collections.reverseOrder());
      for (String version : modelVersions) {
        Collection<ItemCollection> scheduledEvents = findScheduledEvents(version);
        schedulerService.logMessage("...Model=" + version + " (" + scheduledEvents.size() + " scheduled events)", configItemCollection, null);
        for (ItemCollection aactivityEntity : scheduledEvents) {
          processWorkListByEvent(aactivityEntity, configItemCollection);
        }
      }
    } catch (Exception e) {
      logger.severe("Error processing worklist: " + e.getMessage());
      if (logger.isLoggable(Level.FINE)) {
        e.printStackTrace();
      }
    }
    schedulerService.logMessage("================================", configItemCollection, null);
    schedulerService.logMessage(iProcessWorkItems + " workitems processed in total", configItemCollection, null);
    if (unprocessedIDs.size() > 0) {
      schedulerService.logWarning(unprocessedIDs.size() + " workitems could not be processed:", configItemCollection, null);
      for (String aid : unprocessedIDs) {
        schedulerService.logWarning("          " + aid, configItemCollection, null);
      }
    }
    configItemCollection.replaceItemValue("numWorkItemsProcessed", iProcessWorkItems);
    configItemCollection.replaceItemValue("numWorkItemsUnprocessed", unprocessedIDs.size());
    return configItemCollection;
  }

  /**
     * This method collects all scheduled workflow events. A scheduled workflow
     * event is identified by the attribute keyScheduledActivity="1"
     * 
     * The method goes through the latest or a specific Model Version
     * @throws ModelException 
     * 
     */
  protected Collection<ItemCollection> findScheduledEvents(String aModelVersion) throws ModelException {
    Vector<ItemCollection> vectorActivities = new Vector<ItemCollection>();
    Collection<ItemCollection> colProcessList = null;
    colProcessList = modelService.getModel(aModelVersion).findAllTasks();
    for (ItemCollection aprocessentity : colProcessList) {
      int processid = aprocessentity.getItemValueInteger("numprocessid");
      logger.finest("......analyse processentity \'" + processid + "\'");
      Collection<ItemCollection> aActivityList = modelService.getModel(aModelVersion).findAllEventsByTask(processid);
      for (ItemCollection aactivityEntity : aActivityList) {
        logger.finest("......analyse acitity \'" + aactivityEntity.getItemValueString("txtname") + "\'");
        if ("1".equals(aactivityEntity.getItemValueString("keyScheduledActivity"))) {
          vectorActivities.add(aactivityEntity);
        }
      }
    }
    return vectorActivities;
  }

  /**
     * This method processes all workitems for a specific scheduled event element of
     * a workflow model. A scheduled event element can define a selector
     * (txtscheduledview). If no selector is defined, the default selector is used:
     * <p>
     * {@code
     * ($taskid:"[TASKID]" AND $modelversion:"[MODELVERSION]")
     * }
     * <p>
     * In case an old modelversion was deleted, the method tries to migrate to the
     * lates model version. (issue #482)
     * 
     * @param event - a event model element
     * @throws ModelException 
     * @throws QueryException 
     * @throws Exception
     */
  protected void processWorkListByEvent(ItemCollection event, ItemCollection configItemCollection) throws ModelException, QueryException {
    int taskID = event.getItemValueInteger("numprocessid");
    int eventID = event.getItemValueInteger("numActivityID");
    String modelVersionEvent = event.getItemValueString("$modelversion");
    ItemCollection taskElement = modelService.getModel(modelVersionEvent).getTask(taskID);
    String workflowGroup = taskElement.getItemValueString("txtworkflowgroup");
    String searchTerm = null;
    searchTerm = event.getItemValueString("txtscheduledview");
    if (searchTerm.isEmpty()) {
      searchTerm = "($taskid:\"" + taskID + "\" AND $workflowgroup:\"" + workflowGroup + "\")";
    }
    int currentPageIndex = 0;
    List<ItemCollection> worklistCollector = new ArrayList<ItemCollection>();
    while (true) {
      List<ItemCollection> worklist = null;
      String classPattern = "^[a-z][a-z0-9_]*(\\.[A-Za-z0-9_]+)+$";
      if (Pattern.compile(classPattern).matcher(searchTerm).find()) {
        QuerySelector selector = findSelectorByName(searchTerm);
        if (selector != null) {
          schedulerService.logMessage("......CDI selector = " + searchTerm, configItemCollection, null);
          worklist = selector.find(MAX_WORKITEM_COUNT, currentPageIndex);
        }
      } else {
        schedulerService.logMessage("......selector = " + searchTerm, configItemCollection, null);
        worklist = documentService.find(searchTerm, MAX_WORKITEM_COUNT, currentPageIndex);
      }
      if (worklist.size() == 0) {
        break;
      } else {
        logger.finest("......" + worklist.size() + " workitems found in total, collect due date...");
        collectWorkitemsInDue(event, modelVersionEvent, worklist, worklistCollector);
        if (worklist.size() < MAX_WORKITEM_COUNT) {
          break;
        } else {
          currentPageIndex++;
        }
      }
      if (worklistCollector.size() >= MAX_WORKITEM_COUNT) {
        schedulerService.logMessage("...more than " + MAX_WORKITEM_COUNT + " workitems in due found in current selector!", configItemCollection, null);
        break;
      }
      schedulerService.logMessage("...verify next " + MAX_WORKITEM_COUNT + " workitems for current selector...", configItemCollection, null);
    }
    if (worklistCollector.size() > 0) {
      schedulerService.logMessage("......processing " + worklistCollector.size() + " workitems in due...", configItemCollection, null);
      for (ItemCollection workitem : worklistCollector) {
        workitem.setEventID(eventID);
        try {
          logger.finest("......getBusinessObject.....");
          workitem = workflowService.processWorkItemByNewTransaction(workitem);
          iProcessWorkItems++;
        } catch (Exception e) {
          logger.warning("error processing workitem: " + workitem.getUniqueID() + " Error=" + e.getMessage());
          if (logger.isLoggable(Level.FINEST)) {
            e.printStackTrace();
          }
          unprocessedIDs.add(workitem.getUniqueID());
        }
      }
    }
  }

  /**
     * This helper method iterates over a collection of workitems and tests for each
     * workitem if its duedate matches a given BPMN event. If so the workitem is
     * added to the given workitemCollector.
     * <p>
     * The method is called form processWorklistByEvent which iterates over a all
     * worktems selected by specific selector.
     * <p>
     * In case an old modelversion was deleted, the method tries to migrate to the
     * lates model version. (issue #482)
     * 
     * @param event
     * @param eventID
     * @param modelVersionEvent
     * @param worklist
     */
  private void collectWorkitemsInDue(ItemCollection event, String modelVersionEvent, List<ItemCollection> worklist, List<ItemCollection> collector) {
    for (ItemCollection workitem : worklist) {
      String type = workitem.getType();
      if (type.endsWith("deleted")) {
        continue;
      }
      if (workitem.getItemValueBoolean("$immutable")) {
        continue;
      }
      if (!modelVersionEvent.equals(workitem.getModelVersion())) {
        try {
          modelService.getModel(workitem.getModelVersion());
          logger.finest("......skip because model version is older than current version...");
          continue;
        } catch (ModelException me) {
          logger.fine("...deprecated model version \'" + workitem.getModelVersion() + "\' no longer exists -> migrating to new model version \'" + modelVersionEvent + "\'");
          workitem.model(modelVersionEvent);
        }
      }
      if (workItemInDue(workitem, event)) {
        logger.finest("......document " + workitem.getUniqueID() + "is in due");
        collector.add(workitem);
      }
    }
  }

  /**
     * This method adjusts a given base date for a amount of delay
     * 
     * 
     * @param baseDate   date object to be adjusted
     * 
     * @param offsetUnit - time unit (0=sec, 1=min, 2=hours, 3=days, 4=workdays)
     * @param offset     offset for adjustment
     * @return new date object
     */
  private Date adjustBaseDate(Date baseDate, int offsetUnit, int offset) {
    if (baseDate != null) {
      if (offsetUnit == OFFSET_WORKDAYS) {
        Calendar baseCal = Calendar.getInstance();
        baseCal.setTime(baseDate);
        return addWorkDays(baseCal, offset).getTime();
      } else {
        if (offsetUnit == OFFSET_MINUTES) {
          offset *= 60;
        } else {
          if (offsetUnit == OFFSET_HOURS) {
            offset *= 3600;
          } else {
            if (offsetUnit == OFFSET_DAYS) {
              offset *= 3600 * 24;
            }
          }
        }
        Calendar calTimeCompare = Calendar.getInstance();
        calTimeCompare.setTime(baseDate);
        calTimeCompare.add(Calendar.SECOND, offset);
        return calTimeCompare.getTime();
      }
    } else {
      return null;
    }
  }

  /**
     * This method returns an injected Plugin by name or null if no plugin with the
     * requested class name is injected.
     * 
     * @param selectorClassName
     * @return plugin class or null if not found
     */
  private QuerySelector findSelectorByName(String selectorClassName) {
    if (selectorClassName == null || selectorClassName.isEmpty()) {
      return null;
    }
    boolean debug = logger.isLoggable(Level.FINE);
    if (selectors == null || !selectors.iterator().hasNext()) {
      if (debug) {
        logger.finest("......no CDI selectors injected");
      }
      return null;
    }
    for (QuerySelector selector : this.selectors) {
      if (selector.getClass().getName().equals(selectorClassName)) {
        if (debug) {
          logger.finest("......CDI selector \'" + selectorClassName + "\' successful injected");
        }
        return selector;
      }
    }
    return null;
  }
}