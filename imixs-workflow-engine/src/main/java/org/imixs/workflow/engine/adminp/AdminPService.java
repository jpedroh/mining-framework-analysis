package org.imixs.workflow.engine.adminp;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.annotation.Resource;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RunAs;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.Plugin;
import org.imixs.workflow.WorkflowKernel;
import org.imixs.workflow.engine.DocumentService;
import org.imixs.workflow.exceptions.AccessDeniedException;
import jakarta.ejb.EJBException;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;

/**
 * The AmdinPService provides a mechanism to start long running jobs. Those jobs
 * can be used to update workitems in a scheduled batch process. This is called
 * a AdminP-Process. The result of a adminp process is documented into an log
 * entity from type='adminp'. The job description is stored in the field
 * '$WorkflowSummary'. The current startpos and maxcount are stored in the
 * configuration entity in the properties 'numStart' 'numMaxCount'
 * 
 * The service provides methods to create and start different types of jobs. The
 * job type is stored in the field 'job':
 * 
 * RenameUserJob:
 * 
 * This job is to replace entries in the fields $WriteAccess, $ReadAccess and
 * owner. An update request is stored in a adminp entity containing alll
 * necessary informations. The service starts a timer instances for each update
 * process
 * 
 * 
 * LuceneRebuildIndexJob:
 * 
 * This job is to update the lucene index.
 * 
 * 
 * @see AdminPController
 * 
 * @author rsoika
 * 
 */
@DeclareRoles(value = { "org.imixs.ACCESSLEVEL.MANAGERACCESS" }) @RunAs(value = "org.imixs.ACCESSLEVEL.MANAGERACCESS") @Stateless public class AdminPService {
  public static final String JOB_RENAME_USER = "RENAME_USER";

  public static final String JOB_REBUILD_INDEX = "JOB_REBUILD_INDEX";

  public static final String JOB_UPGRADE = "UPGRADE";

  public static final String JOB_MIGRATION = "MIGRATION";

  public static final int DEFAULT_INTERVAL = 60;

  @Resource SessionContext ctx;

  @Resource jakarta.ejb.TimerService timerService;

  @Inject DocumentService documentService;

  @Inject JobHandlerUpgradeWorkitems jobHandlerUpgradeWorkitems;

  @Inject JobHandlerRenameUser jobHandlerRenameUser;

  @Inject JobHandlerRebuildIndex jobHandlerRebuildIndex;

  @Inject @Any private Instance<JobHandler> jobHandlers;

  @Inject @Any private Instance<Plugin> plugins;

  private static Logger logger = Logger.getLogger(AdminPService.class.getName());

  /**
     * This Method starts a new TimerService for a given job.
     * 
     * The method loads configuration from a ItemCollection (timerdescription) with
     * the following informations:
     * 
     * datstart - Date Object
     * 
     * datstop - Date Object
     * 
     * numInterval - Integer Object (interval in seconds)
     * 
     * id - String - unique identifier for the schedule Service.
     * 
     * The param 'id' should contain a unique identifier (e.g. the EJB Name) as only
     * one scheduled Workflow should run inside a WorkflowInstance. If a timer with
     * the id is already running the method stops this timer object first and
     * reschedules the timer.
     * 
     * The method throws an exception if the timerdescription contains invalid
     * attributes or values.
     * 
     * @throws AccessDeniedException
     */
  public ItemCollection createJob(ItemCollection adminp) throws AccessDeniedException {
    adminp.replaceItemValue("type", "adminp");
    adminp.replaceItemValue("$snapshot.history", 1);
    String jobtype = adminp.getItemValueString("job");
    adminp.replaceItemValue(WorkflowKernel.UNIQUEID, WorkflowKernel.generateUniqueID());
    int interval = adminp.getItemValueInteger("numInterval");
    if (interval <= 0) {
      interval = DEFAULT_INTERVAL;
      adminp.replaceItemValue("numInterval", Long.valueOf(interval));
    }
    Calendar cal = Calendar.getInstance();
    Date terminationDate = cal.getTime();
    cal.add(Calendar.HOUR, 24);
    adminp.replaceItemValue("datTerminate", terminationDate);
    adminp = documentService.save(adminp);
    Timer timer = timerService.createTimer(terminationDate, (interval * 1000), adminp.getItemValueString(WorkflowKernel.UNIQUEID));
    logger.info("Job " + jobtype + " (" + timer.getInfo().toString() + ") started... ");
    return adminp;
  }

  /**
     * Stops a running job and deletes the job configuration.
     * 
     * @param id
     * @return
     * @throws AccessDeniedException
     */
  public void deleteJob(String id) throws AccessDeniedException {
    ItemCollection adminp = cancelTimer(id);
    if (adminp != null) {
      documentService.remove(adminp);
    }
  }

  /**
     * This method processes the timeout event. The method loads the corresponding
     * job description (adminp entity) and delegates the processing to the
     * corresponding JobHandler.
     * 
     * @param timer
     */
  @Timeout public void scheduleTimer(jakarta.ejb.Timer timer) {
    String sTimerID = null;
    boolean debug = logger.isLoggable(Level.FINE);
    long lProfiler = System.currentTimeMillis();
    sTimerID = timer.getInfo().toString();
    ItemCollection adminp = documentService.load(sTimerID);
    try {
      if (adminp == null) {
        logger.info("Process " + sTimerID + " was removed - timer will be canceled");
        timer.cancel();
        return;
      }
      String job = adminp.getItemValueString("job");
      logger.info("Job " + job + " (" + adminp.getUniqueID() + ")  processing...");
      JobHandler jobHandler = null;
      if (job.equals(JOB_RENAME_USER)) {
        jobHandler = jobHandlerRenameUser;
      }
      if (job.equals(JOB_UPGRADE)) {
        jobHandler = jobHandlerUpgradeWorkitems;
      }
      if (job.equals(JOB_REBUILD_INDEX) || job.equals("REBUILD_LUCENE_INDEX")) {
        jobHandler = jobHandlerRebuildIndex;
      }
      if (jobHandler == null) {
        jobHandler = findJobHandlerByName(job);
      }
      if (jobHandler != null) {
        adminp.replaceItemValue("$workflowStatus", "PROCESSING");
        adminp = documentService.save(adminp);
        adminp = jobHandler.run(adminp);
        if (adminp.getItemValueBoolean("iscompleted")) {
          timer.cancel();
          adminp.replaceItemValue("$workflowStatus", "COMPLETED");
          logger.info("Job " + job + " (" + adminp.getUniqueID() + ") completed - timer stopped");
        } else {
          adminp.replaceItemValue("$workflowStatus", "WAITING");
        }
      } else {
        logger.warning("Unable to start AdminP Job. JobHandler class \'" + job + "\' not defined!");
        timer.cancel();
        adminp.replaceItemValue("$workflowStatus", "FAILED");
        logger.info("Job " + adminp.getUniqueID() + " - timer stopped");
      }
    } catch (AdminPException e) {
      e.printStackTrace();
      timer.cancel();
      logger.severe("AdminP job \'" + sTimerID + "\' failed - " + e.getMessage());
      if (adminp != null) {
        adminp.replaceItemValue("$workflowStatus", "FAILED");
        adminp.replaceItemValue("errormessage", e.getMessage());
      }
    } finally {
      try {
        if (adminp != null) {
          adminp = documentService.save(adminp);
        } else {
          logger.warning("Unable to update adminp job status - adminp document is null!");
        }
      } catch (AccessDeniedException | EJBException e2) {
        logger.warning("Unable to update adminp job status - reason: " + e2.getMessage());
        if (debug) {
          e2.printStackTrace();
        }
      }
    }
    logger.fine("...timer call finished successfull after " + ((System.currentTimeMillis()) - lProfiler) + " ms");
  }

  /**
     * This method returns a n injected JobHandler by name or null if no JobHandler
     * with the requested class name is injected.
     * 
     * @param jobHandlerClassName
     * @return jobHandler class or null if not found
     */
  private JobHandler findJobHandlerByName(String jobHandlerClassName) {
    if (jobHandlerClassName == null || jobHandlerClassName.isEmpty()) {
      return null;
    }
    if (jobHandlers == null || !jobHandlers.iterator().hasNext()) {
      logger.finest("......no CDI jobHandlers injected");
      return null;
    }
    for (JobHandler jobHandler : this.jobHandlers) {
      if (jobHandler.getClass().getName().equals(jobHandlerClassName)) {
        logger.finest("......CDI JobHandler \'" + jobHandlerClassName + "\' successful injected");
        return jobHandler;
      }
    }
    return null;
  }

  /**
     * This method cancels a timer by ID. If a timer configuration exits, the method
     * returns the document entity.
     * 
     * @param id
     * @return
     */
  private ItemCollection cancelTimer(String id) {
    logger.finest("......cancelTimer - id:" + id + " ....");
    ItemCollection adminp = documentService.load(id);
    if (adminp == null) {
      logger.warning("failed to load timer data ID:" + id + " ");
    }
    Timer timer = this.findTimer(id);
    if (timer != null) {
      timer.cancel();
      logger.info("cancelTimer - id:" + id + " successful.");
    } else {
      logger.info("cancelTimer - id:" + id + " failed - timer does no longer exist.");
    }
    if (adminp != null) {
      adminp.replaceItemValue("txtTimerStatus", "Stopped");
    }
    return adminp;
  }

  /**
     * This method returns a timer for a corresponding id if such a timer object
     * exists.
     * 
     * @param id
     * @return Timer
     * @throws Exception
     */
  private Timer findTimer(String id) {
    if (id == null || id.isEmpty()) {
      return null;
    }
    for (Object obj : timerService.getTimers()) {
      Timer timer = (jakarta.ejb.Timer) obj;
      if (timer.getInfo() instanceof String) {
        String timerid = timer.getInfo().toString();
        if (id.equals(timerid)) {
          return timer;
        }
      }
    }
    logger.warning("findTimer - id:" + id + " does no longer exist.");
    return null;
  }
}