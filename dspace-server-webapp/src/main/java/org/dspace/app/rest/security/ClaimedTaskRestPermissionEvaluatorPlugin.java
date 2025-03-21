package org.dspace.app.rest.security;
import java.io.Serializable;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.model.ClaimedTaskRest;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.service.EPersonService;
import org.dspace.services.RequestService;
import org.dspace.services.model.Request;
import org.dspace.xmlworkflow.storedcomponents.ClaimedTask;
import org.dspace.xmlworkflow.storedcomponents.service.ClaimedTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * An authenticated user is allowed to interact with a claimed task only if he own it
 * claim.
 * 
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@Component public class ClaimedTaskRestPermissionEvaluatorPlugin extends RestObjectPermissionEvaluatorPlugin {
  private static final Logger log = LoggerFactory.getLogger(ClaimedTaskRestPermissionEvaluatorPlugin.class);

  @Autowired private RequestService requestService;

  @Autowired private ClaimedTaskService claimedTaskService;

  @Autowired private EPersonService ePersonService;

  @Override public boolean hasDSpacePermission(Authentication authentication, Serializable targetId, String targetType, DSpaceRestPermission permission) {
    if (!StringUtils.equalsIgnoreCase(ClaimedTaskRest.NAME, targetType)) {
      return false;
    }
    Request request = requestService.getCurrentRequest();
    Context context = ContextUtil.obtainContext(request.getHttpServletRequest());
    EPerson ePerson = null;
    try {
      ePerson = ePersonService.findByEmail(context, (String) authentication.getPrincipal());
      if (ePerson == null) {
        return false;
      }
      int dsoId = Integer.parseInt(targetId.toString());
      ClaimedTask claimedTask = claimedTaskService.find(context, dsoId);
      if (claimedTask == null) {
        return true;
      }
      if (ePerson.equals(claimedTask.getOwner())) {
        return true;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
    }
    return false;
  }
}