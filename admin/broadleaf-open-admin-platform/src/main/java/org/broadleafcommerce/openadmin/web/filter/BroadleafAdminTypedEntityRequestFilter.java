package org.broadleafcommerce.openadmin.web.filter;
import org.apache.commons.collections4.iterators.IteratorEnumeration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.TypedEntity;
import org.broadleafcommerce.common.service.GenericEntityService;
import org.broadleafcommerce.common.web.BroadleafWebRequestProcessor;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * Responsible for setting the necessary attributes on the BroadleafRequestContext
 * 
 * @author Jon Fleschler (jfleschler)
 */
@Component(value = "blAdminTypedEntityRequestFilter") public class BroadleafAdminTypedEntityRequestFilter extends AbstractBroadleafAdminRequestFilter {
  private final Log LOG = LogFactory.getLog(BroadleafAdminTypedEntityRequestFilter.class);

  @Autowired @Qualifier(value = "blAdminRequestProcessor") protected BroadleafWebRequestProcessor requestProcessor;

  @Autowired @Qualifier(value = "blAdminNavigationService") protected AdminNavigationService adminNavigationService;

  @Autowired @Qualifier(value = "blAdminSecurityRemoteService") protected SecurityVerifier adminRemoteSecurityService;

  @Autowired @Qualifier(value = "blGenericEntityService") GenericEntityService genericEntityService;

  @Override public void doFilterInternalUnlessIgnored(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
    if (isRequestForTypedEntity(request, response)) {
      return;
    }
    filterChain.doFilter(request, response);
  }

  @SuppressWarnings(value = { "unchecked" }) public boolean isRequestForTypedEntity(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    String servletPath = request.getServletPath();
    if (!servletPath.contains(":")) {
      return false;
    }
    String sectionKey = getSectionKeyFromRequest(request);
    AdminSection typedEntitySection = adminNavigationService.findAdminSectionByURI(sectionKey);
    if (typedEntitySection == null) {
      return false;
    }
    TypedEntity typedEntity = getTypedEntityFromServletPathId(servletPath, typedEntitySection.getCeilingEntity());
    if (typedEntity != null && !typeMatchesAdminSection(typedEntity, sectionKey)) {
      String redirectUrl = getTypeAdminSectionMismatchUrl(typedEntity, typedEntitySection.getCeilingEntity(), request.getRequestURI(), sectionKey);
      response.sendRedirect(redirectUrl);
      return true;
    }
    if (!adminUserHasAccess(typedEntitySection)) {
      if (LOG.isDebugEnabled()) {
        AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
        LOG.debug(String.format("User %s does not have access to %s", adminUser.getLogin(), typedEntitySection));
      }
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
      return true;
    }
    request.setAttribute("typedEntitySection", typedEntitySection);
    String type = getEntityTypeFromRequest(request);
    final String forwardPath = servletPath;
    String typedFieldName = getTypeFieldName(typedEntitySection);
    final Map parameters = new LinkedHashMap(request.getParameterMap());
    if (typedFieldName != null) {
      parameters.put(typedFieldName, new String[] { type.substring(1).toUpperCase() });
    }
    final HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
      @Override public String getParameter(String name) {
        String[] response = (String[]) parameters.get(name);
        if (ArrayUtils.isEmpty(response)) {
          return null;
        } else {
          return response[0];
        }
      }

      @Override public Map getParameterMap() {
        return parameters;
      }

      @Override public Enumeration getParameterNames() {
        return new IteratorEnumeration(parameters.keySet().iterator());
      }

      @Override public String[] getParameterValues(String name) {
        return (String[]) parameters.get(name);
      }

      @Override public String getServletPath() {
        return forwardPath;
      }
    };
    requestProcessor.process(new ServletWebRequest(wrapper, response));
    wrapper.getRequestDispatcher(wrapper.getServletPath()).forward(wrapper, response);
    return true;
  }

  protected TypedEntity getTypedEntityFromServletPathId(String servletPath, String ceilingEntity) {
    int idBegIndex = servletPath.indexOf("/", 1);
    if (idBegIndex > 0) {
      String id = servletPath.substring(idBegIndex + 1, servletPath.length());
      if (StringUtils.isNumeric(id)) {
        Object objectEntity = genericEntityService.readGenericEntity(ceilingEntity, Long.valueOf(id));
        if (TypedEntity.class.isAssignableFrom(objectEntity.getClass())) {
          return (TypedEntity) objectEntity;
        }
      }
    }
    return null;
  }

  protected String getTypeAdminSectionMismatchUrl(TypedEntity typedEntity, String ceilingEntity, String uri, String sectionKey) {
    int lastDotIndex = StringUtils.lastIndexOf(ceilingEntity, ".");
    String ceilingEntityType = StringUtils.substring(ceilingEntity, lastDotIndex + 1).toLowerCase();
    String entityType = typedEntity.getType().getType().toLowerCase();
    if (StringUtils.equals(entityType, "standard") || StringUtils.equals(ceilingEntityType, entityType)) {
      return StringUtils.replace(uri, sectionKey, "/" + ceilingEntityType);
    }
    return StringUtils.replace(uri, sectionKey, "/" + ceilingEntityType + ":" + entityType);
  }

  protected boolean typeMatchesAdminSection(TypedEntity typedEntity, String sectionKey) {
    String urlType = StringUtils.substring(sectionKey, sectionKey.indexOf(":") + 1);
    if (!StringUtils.equalsIgnoreCase(typedEntity.getType().getType(), urlType)) {
      return false;
    }
    return true;
  }

  protected boolean adminUserHasAccess(AdminSection typedEntitySection) {
    AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
    for (AdminPermission permission : adminUser.getAllPermissions()) {
      if (typedEntitySection.getPermissions().contains(permission)) {
        return true;
      }
    }
    for (AdminRole role : adminUser.getAllRoles()) {
      for (AdminPermission permission : role.getAllPermissions()) {
        if (typedEntitySection.getPermissions().contains(permission)) {
          return true;
        }
      }
    }
    return false;
  }

  protected String getEntityTypeFromRequest(HttpServletRequest request) {
    String uri = request.getServletPath();
    int typeIndex = uri.indexOf(":");
    int endIndex = uri.indexOf("/", typeIndex);
    String type;
    if (endIndex > 0) {
      type = uri.substring(typeIndex, endIndex);
    } else {
      type = uri.substring(typeIndex);
    }
    return type;
  }

  protected String getSectionKeyFromRequest(HttpServletRequest request) {
    String uri = request.getServletPath();
    String sectionKey = uri.replace("/status", "");
    int endIndex = sectionKey.indexOf("/", 1);
    if (endIndex > 0) {
      sectionKey = sectionKey.substring(0, endIndex);
    }
    return sectionKey;
  }

  protected String getTypeFieldName(AdminSection adminSection) {
    try {
      DynamicEntityDao dynamicEntityDao = getDynamicEntityDao(adminSection.getCeilingEntity());
      Class<?> implClass = dynamicEntityDao.getCeilingImplClass(adminSection.getCeilingEntity());
      return ((TypedEntity) implClass.newInstance()).getTypeFieldName();
    } catch (Exception e) {
      return null;
    }
  }

  protected DynamicEntityDao getDynamicEntityDao(String className) {
    return PersistenceManagerFactory.getPersistenceManager(className).getDynamicEntityDao();
  }

  @Override public int getOrder() {
    return FilterOrdered.POST_SECURITY_LOW + 1000;
  }
}