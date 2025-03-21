/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.xmlworkflow.state.actions.processingaction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.dspace.app.util.Util;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.workflow.WorkflowException;
import org.dspace.xmlworkflow.factory.XmlWorkflowServiceFactory;
import org.dspace.xmlworkflow.service.XmlWorkflowService;
import org.dspace.xmlworkflow.state.Step;
import org.dspace.xmlworkflow.state.actions.ActionResult;
import org.dspace.xmlworkflow.storedcomponents.XmlWorkflowItem;

/**
 * Processing class of an action where a single user has
 * been assigned and they can either accept/reject the workflow item
 * or reject the task
 *
 * @author Bram De Schouwer (bram.deschouwer at dot com)
 * @author Kevin Van de Velde (kevin at atmire dot com)
 * @author Ben Bosman (ben at atmire dot com)
 * @author Mark Diggory (markd at atmire dot com)
 */
public class SingleUserReviewAction extends ProcessingAction {

    public static final int OUTCOME_REJECT = 1;

    protected static final String SUBMIT_DECLINE_TASK = "submit_decline_task";

    @Override
    public void activate(Context c, XmlWorkflowItem wfItem) {
        // empty
    }

    @Override
    public ActionResult execute(Context c, XmlWorkflowItem wfi, Step step, HttpServletRequest request)
        throws SQLException, AuthorizeException, IOException, WorkflowException {
        if (super.isOptionInParam(request)) {
            switch (Util.getSubmitButton(request, SUBMIT_CANCEL)) {
                case SUBMIT_APPROVE:
                    return processAccept(c, wfi);
                case SUBMIT_REJECT:
                    if (wfi.getSubmitter() == null) {
                        // If the original submitter is no longer there, delete the task
                        return processDelete(c, wfi);
                    } else {
                        return super.processRejectPage(c, wfi, request);
                    }
                case SUBMIT_DECLINE_TASK:
                    return processDecline(c, wfi);
                default:
                    return new ActionResult(ActionResult.TYPE.TYPE_CANCEL);
            }
        }
        return new ActionResult(ActionResult.TYPE.TYPE_CANCEL);
    }

    /**
     * Accept the workflow item => last step in workflow so will be archived
     * Info on step & reviewer will be added on metadata dc.description.provenance of resulting item
     */
    public ActionResult processAccept(Context c, XmlWorkflowItem wfi) throws SQLException, AuthorizeException {
        super.addApprovedProvenance(c, wfi);
        return new ActionResult(ActionResult.TYPE.TYPE_OUTCOME, ActionResult.OUTCOME_COMPLETE);
    }

    @Override
    public List<String> getOptions() {
        List<String> options = new ArrayList<>();
        options.add(SUBMIT_APPROVE);
        options.add(SUBMIT_REJECT);
        options.add(SUBMIT_DECLINE_TASK);
        return options;
    }

<<<<<<< /usr/src/app/output/dspace/dspace/d0c507df643b23e8ef877db855d5388390a5f800/dspace-api/src/main/java/org/dspace/xmlworkflow/state/actions/processingaction/SingleUserReviewAction.java/left.java
    public ActionResult processMainPage(Context c, XmlWorkflowItem wfi, Step step, HttpServletRequest request)
        throws SQLException, AuthorizeException {
        if (request.getParameter(SUBMIT_APPROVE) != null) {
            //Delete the tasks
            addApprovedProvenance(c, wfi);

            return new ActionResult(ActionResult.TYPE.TYPE_OUTCOME, ActionResult.OUTCOME_COMPLETE);
        } else if (request.getParameter(SUBMIT_REJECT) != null) {
            // Make sure we indicate which page we want to process
            if (wfi.getSubmitter() == null) {
                request.setAttribute("page", SUBMITTER_IS_DELETED_PAGE);
            } else {
                request.setAttribute("page", REJECT_PAGE);
            }
            // We have pressed reject item, so take the user to a page where they can reject
            return new ActionResult(ActionResult.TYPE.TYPE_PAGE);
        } else if (request.getParameter(SUBMIT_DECLINE_TASK) != null) {
            return new ActionResult(ActionResult.TYPE.TYPE_OUTCOME, OUTCOME_REJECT);

        } else {
            //We pressed the leave button so return to our submissions page
            return new ActionResult(ActionResult.TYPE.TYPE_SUBMISSION_PAGE);
        }
    }

    private void addApprovedProvenance(Context c, XmlWorkflowItem wfi) throws SQLException, AuthorizeException {
        //Add the provenance for the accept
        String now = DCDate.getCurrent().toString();

        // Get user's name + email address
        String usersName = XmlWorkflowServiceFactory.getInstance().getXmlWorkflowService()
                                                    .getEPersonName(c.getCurrentUser());

        String provDescription = getProvenanceStartId() + " Approved for entry into archive by "
            + usersName + " on " + now + " (GMT) ";

        // Add to item as a DC field
        itemService.addMetadata(c, wfi.getItem(), MetadataSchemaEnum.DC.getName(), "description", "provenance", "en",
                                provDescription);
        itemService.update(c, wfi.getItem());
    }

    public ActionResult processRejectPage(Context c, XmlWorkflowItem wfi, Step step, HttpServletRequest request)
||||||| /usr/src/app/output/dspace/dspace/d0c507df643b23e8ef877db855d5388390a5f800/dspace-api/src/main/java/org/dspace/xmlworkflow/state/actions/processingaction/SingleUserReviewAction.java/base.java
    public ActionResult processMainPage(Context c, XmlWorkflowItem wfi, Step step, HttpServletRequest request)
        throws SQLException, AuthorizeException {
        if (request.getParameter(SUBMIT_APPROVE) != null) {
            //Delete the tasks
            addApprovedProvenance(c, wfi);

            return new ActionResult(ActionResult.TYPE.TYPE_OUTCOME, ActionResult.OUTCOME_COMPLETE);
        } else if (request.getParameter(SUBMIT_REJECT) != null) {
            // Make sure we indicate which page we want to process
            if (wfi.getSubmitter() == null) {
                request.setAttribute("page", SUBMITTER_IS_DELETED_PAGE);
            } else {
                request.setAttribute("page", REJECT_PAGE);
            }
            // We have pressed reject item, so take the user to a page where he can reject
            return new ActionResult(ActionResult.TYPE.TYPE_PAGE);
        } else if (request.getParameter(SUBMIT_DECLINE_TASK) != null) {
            return new ActionResult(ActionResult.TYPE.TYPE_OUTCOME, OUTCOME_REJECT);

        } else {
            //We pressed the leave button so return to our submissions page
            return new ActionResult(ActionResult.TYPE.TYPE_SUBMISSION_PAGE);
        }
    }

    private void addApprovedProvenance(Context c, XmlWorkflowItem wfi) throws SQLException, AuthorizeException {
        //Add the provenance for the accept
        String now = DCDate.getCurrent().toString();

        // Get user's name + email address
        String usersName = XmlWorkflowServiceFactory.getInstance().getXmlWorkflowService()
                                                    .getEPersonName(c.getCurrentUser());

        String provDescription = getProvenanceStartId() + " Approved for entry into archive by "
            + usersName + " on " + now + " (GMT) ";

        // Add to item as a DC field
        itemService.addMetadata(c, wfi.getItem(), MetadataSchemaEnum.DC.getName(), "description", "provenance", "en",
                                provDescription);
        itemService.update(c, wfi.getItem());
    }

    public ActionResult processRejectPage(Context c, XmlWorkflowItem wfi, Step step, HttpServletRequest request)
=======
    /**
     * Since original submitter no longer exists, workflow item is permanently deleted
     */
    private ActionResult processDelete(Context c, XmlWorkflowItem wfi)
>>>>>>> /usr/src/app/output/dspace/dspace/d0c507df643b23e8ef877db855d5388390a5f800/dspace-api/src/main/java/org/dspace/xmlworkflow/state/actions/processingaction/SingleUserReviewAction.java/right.java
        throws SQLException, AuthorizeException, IOException {
        EPerson user = c.getCurrentUser();
        c.turnOffAuthorisationSystem();
        WorkspaceItem workspaceItem = XmlWorkflowServiceFactory.getInstance().getXmlWorkflowService()
            .abort(c, wfi, user);
        ContentServiceFactory.getInstance().getWorkspaceItemService().deleteAll(c, workspaceItem);
        c.restoreAuthSystemState();
        return new ActionResult(ActionResult.TYPE.TYPE_SUBMISSION_PAGE);
    }

    /**
     * Selected reviewer declines to review task, then the workflow is aborted and restarted
     */
    private ActionResult processDecline(Context c, XmlWorkflowItem wfi)
        throws SQLException, IOException, AuthorizeException, WorkflowException {
        EPerson user = c.getCurrentUser();
        c.turnOffAuthorisationSystem();
        XmlWorkflowService xmlWorkflowService = XmlWorkflowServiceFactory.getInstance().getXmlWorkflowService();
        WorkspaceItem workspaceItem = xmlWorkflowService.abort(c, wfi, user);
        xmlWorkflowService.start(c, workspaceItem);
        c.restoreAuthSystemState();
        return new ActionResult(ActionResult.TYPE.TYPE_SUBMISSION_PAGE);
    }

}
