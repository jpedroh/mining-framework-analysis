package de.deepamehta.plugins.accesscontrol;
import de.deepamehta.plugins.accesscontrol.event.PostLoginUserListener;
import de.deepamehta.plugins.accesscontrol.event.PostLogoutUserListener;
import de.deepamehta.plugins.accesscontrol.model.AccessControlList;
import de.deepamehta.plugins.accesscontrol.model.ACLEntry;
import de.deepamehta.plugins.accesscontrol.model.Credentials;
import de.deepamehta.plugins.accesscontrol.model.Permissions;
import de.deepamehta.plugins.accesscontrol.model.UserRole;
import de.deepamehta.plugins.accesscontrol.service.AccessControlService;
import de.deepamehta.plugins.workspaces.service.WorkspacesService;
import de.deepamehta.core.Association;
import de.deepamehta.core.AssociationType;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.Type;
import de.deepamehta.core.ViewConfiguration;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.model.CompositeValueModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.model.TopicRoleModel;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.DeepaMehtaEvent;
import de.deepamehta.core.service.Directives;
import de.deepamehta.core.service.EventListener;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.accesscontrol.AccessControlException;
import de.deepamehta.core.service.accesscontrol.Operation;
import de.deepamehta.core.service.event.AllPluginsActiveListener;
import de.deepamehta.core.service.event.IntroduceTopicTypeListener;
import de.deepamehta.core.service.event.IntroduceAssociationTypeListener;
import de.deepamehta.core.service.event.PostCreateAssociationListener;
import de.deepamehta.core.service.event.PostCreateTopicListener;
import de.deepamehta.core.service.event.PostUpdateTopicListener;
import de.deepamehta.core.service.event.PreGetAssociationListener;
import de.deepamehta.core.service.event.PreGetTopicListener;
import de.deepamehta.core.service.event.PreSendAssociationTypeListener;
import de.deepamehta.core.service.event.PreSendTopicTypeListener;
import de.deepamehta.core.service.event.ResourceRequestFilterListener;
import de.deepamehta.core.service.event.ServiceRequestFilterListener;
import de.deepamehta.core.storage.spi.DeepaMehtaTransaction;
import de.deepamehta.core.util.DeepaMehtaUtils;
import de.deepamehta.core.util.JavaUtils;
import org.codehaus.jettison.json.JSONObject;
import com.sun.jersey.spi.container.ContainerRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

@Path(value = "/accesscontrol") @Consumes(value = "application/json") @Produces(value = "application/json") public class AccessControlPlugin extends PluginActivator implements AccessControlService, AllPluginsActiveListener, PreGetTopicListener, PreGetAssociationListener, PostCreateTopicListener, PostCreateAssociationListener, PostUpdateTopicListener, IntroduceTopicTypeListener, IntroduceAssociationTypeListener, ServiceRequestFilterListener, ResourceRequestFilterListener, PreSendTopicTypeListener, PreSendAssociationTypeListener {
  private static final boolean READ_REQUIRES_LOGIN = Boolean.getBoolean("dm4.security.read_requires_login");

  private static final boolean WRITE_REQUIRES_LOGIN = Boolean.getBoolean("dm4.security.write_requires_login");

  private static final String SUBNET_FILTER = System.getProperty("dm4.security.subnet_filter");

  private static final String AUTHENTICATION_REALM = "DeepaMehta";

  private static final String DEFAULT_USERNAME = "admin";

  private static final String DEFAULT_PASSWORD = "";

  private static final String MEMBERSHIP_TYPE = "dm4.accesscontrol.membership";

  private static final AccessControlList DEFAULT_INSTANCE_ACL = new AccessControlList(new ACLEntry(Operation.WRITE, UserRole.CREATOR, UserRole.OWNER, UserRole.MEMBER));

  private static final AccessControlList DEFAULT_USER_ACCOUNT_ACL = new AccessControlList(new ACLEntry(Operation.WRITE, UserRole.CREATOR, UserRole.OWNER));

  private static String PROP_CREATOR = "dm4.accesscontrol.creator";

  private static String PROP_OWNER = "dm4.accesscontrol.owner";

  private static String PROP_ACL = "dm4.accesscontrol.acl";

  private static DeepaMehtaEvent POST_LOGIN_USER = new DeepaMehtaEvent(PostLoginUserListener.class) {
    @Override public void deliver(EventListener listener, Object... params) {
      ((PostLoginUserListener) listener).postLoginUser((String) params[0]);
    }
  };

  private static DeepaMehtaEvent POST_LOGOUT_USER = new DeepaMehtaEvent(PostLogoutUserListener.class) {
    @Override public void deliver(EventListener listener, Object... params) {
      ((PostLogoutUserListener) listener).postLogoutUser((String) params[0]);
    }
  };

  @Inject private WorkspacesService wsService;

  @Context private HttpServletRequest request;

  private Logger logger = Logger.getLogger(getClass().getName());

  @POST @Path(value = "/login") @Override public void login() {
  }

  @POST @Path(value = "/logout") @Override public void logout() {
    _logout(request);
    if (READ_REQUIRES_LOGIN) {
      throw401Unauthorized();
    }
  }

  @GET @Path(value = "/user") @Produces(value = "text/plain") @Override public String getUsername() {
    try {
      HttpSession session = request.getSession(false);
      if (session == null) {
        return null;
      }
      return username(session);
    } catch (IllegalStateException e) {
      return null;
    }
  }

  @Override public Topic getUsername(String username) {
    return dms.getTopic("dm4.accesscontrol.username", new SimpleValue(username), false);
  }

  @GET @Path(value = "/topic/{id}") @Override public Permissions getTopicPermissions(@PathParam(value = "id") long topicId) {
    return getPermissions(topicId);
  }

  @GET @Path(value = "/association/{id}") @Override public Permissions getAssociationPermissions(@PathParam(value = "id") long assocId) {
    return getPermissions(assocId);
  }

  @Override public String getCreator(DeepaMehtaObject object) {
    return object.hasProperty(PROP_CREATOR) ? (String) object.getProperty(PROP_CREATOR) : null;
  }

  @Override public void setCreator(DeepaMehtaObject object, String username) {
    DeepaMehtaTransaction tx = dms.beginTx();
    try {
      object.setProperty(PROP_CREATOR, username, true);
      tx.success();
    } catch (Exception e) {
      logger.warning("ROLLBACK!");
      throw new RuntimeException("Setting the creator of " + info(object) + " failed (username=" + username + ")", e);
    } finally {
      tx.finish();
    }
  }

  @Override public String getOwner(DeepaMehtaObject object) {
    return object.hasProperty(PROP_OWNER) ? (String) object.getProperty(PROP_OWNER) : null;
  }

  @Override public void setOwner(DeepaMehtaObject object, String username) {
    DeepaMehtaTransaction tx = dms.beginTx();
    try {
      object.setProperty(PROP_OWNER, username, true);
      tx.success();
    } catch (Exception e) {
      logger.warning("ROLLBACK!");
      throw new RuntimeException("Setting the owner of " + info(object) + " failed (username=" + username + ")", e);
    } finally {
      tx.finish();
    }
  }

  @Override public AccessControlList getACL(DeepaMehtaObject object) {
    try {
      if (object.hasProperty(PROP_ACL)) {
        return new AccessControlList(new JSONObject((String) object.getProperty(PROP_ACL)));
      } else {
        return new AccessControlList();
      }
    } catch (Exception e) {
      throw new RuntimeException("Fetching the ACL of " + info(object) + " failed", e);
    }
  }

  @Override public void setACL(DeepaMehtaObject object, AccessControlList acl) {
    DeepaMehtaTransaction tx = dms.beginTx();
    try {
      object.setProperty(PROP_ACL, acl.toJSON().toString(), false);
      tx.success();
    } catch (Exception e) {
      logger.warning("ROLLBACK!");
      throw new RuntimeException("Setting the ACL of " + info(object) + " failed", e);
    } finally {
      tx.finish();
    }
  }

  @POST @Path(value = "/user/{username}/workspace/{workspace_id}") @Override public void createMembership(@PathParam(value = "username") String username, @PathParam(value = "workspace_id") long workspaceId) {
    try {
      dms.createAssociation(new AssociationModel(MEMBERSHIP_TYPE, new TopicRoleModel(getUsernameOrThrow(username).getId(), "dm4.core.default"), new TopicRoleModel(workspaceId, "dm4.core.default")), null);
    } catch (Exception e) {
      throw new RuntimeException("Creating membership for user \"" + username + "\" and workspace " + workspaceId + " failed", e);
    }
  }

  @Override public boolean isMember(String username, long workspaceId) {
    return dms.getAccessControl().isMember(username, workspaceId);
  }

  @GET @Path(value = "/creator/{username}/topics") @Override public Collection<Topic> getTopicsByCreator(@PathParam(value = "username") String username) {
    return dms.getTopicsByProperty(PROP_CREATOR, username);
  }

  @GET @Path(value = "/owner/{username}/topics") @Override public Collection<Topic> getTopicsByOwner(@PathParam(value = "username") String username) {
    return dms.getTopicsByProperty(PROP_OWNER, username);
  }

  @GET @Path(value = "/creator/{username}/assocs") @Override public Collection<Association> getAssociationsByCreator(@PathParam(value = "username") String username) {
    return dms.getAssociationsByProperty(PROP_CREATOR, username);
  }

  @GET @Path(value = "/owner/{username}/assocs") @Override public Collection<Association> getAssociationsByOwner(@PathParam(value = "username") String username) {
    return dms.getAssociationsByProperty(PROP_OWNER, username);
  }

  @Override public void postInstall() {
    logger.info("Creating \"admin\" user account");
    Topic adminAccount = createUserAccount(new Credentials(DEFAULT_USERNAME, DEFAULT_PASSWORD));
    setupAccessControl(adminAccount, DEFAULT_USER_ACCOUNT_ACL, DEFAULT_USERNAME);
  }

  @Override public void init() {
    logger.info("Security settings:" + "\n    dm4.security.read_requires_login=" + READ_REQUIRES_LOGIN + "\n    dm4.security.write_requires_login=" + WRITE_REQUIRES_LOGIN + "\n    dm4.security.subnet_filter=\"" + SUBNET_FILTER + "\"");
  }

  /**
     * Setup access control for the default user and the default topicmap.
     *   1) create membership for default user and default workspace
     *   2) setup access control for default workspace
     *   3) assign default topicmap to default workspace
     *   4) setup access control for default topicmap
     */
  @Override public void allPluginsActive() {
    Topic defaultWorkspace = wsService.getDefaultWorkspace();
    createDefaultMembership(defaultWorkspace);
    setupDefaultAccessControl(defaultWorkspace, "default workspace (\"DeepaMehta\")");
    Topic defaultTopicmap = fetchDefaultTopicmap();
    if (defaultTopicmap != null) {
      assignDefaultTopicmapToDefaultWorkspace(defaultTopicmap, defaultWorkspace);
      setupDefaultAccessControl(defaultTopicmap, "default topicmap (\"untitled\")");
    }
  }

  @Override public void preGetTopic(long topicId) {
    checkReadPermission(topicId);
  }

  @Override public void preGetAssociation(long assocId) {
    checkReadPermission(assocId);
    long[] playerIds = dms.getPlayerIds(assocId);
    checkReadPermission(playerIds[0]);
    checkReadPermission(playerIds[1]);
  }

  @Override public void postCreateTopic(Topic topic, Directives directives) {
    if (isUserAccount(topic)) {
      setupUserAccountAccessControl(topic);
    } else {
      setupDefaultAccessControl(topic);
    }
    joinIfWorkspace(topic);
  }

  @Override public void postCreateAssociation(Association assoc, Directives directives) {
    setupDefaultAccessControl(assoc);
  }

  @Override public void postUpdateTopic(Topic topic, TopicModel newModel, TopicModel oldModel, Directives directives) {
    if (topic.getTypeUri().equals("dm4.accesscontrol.user_account")) {
      Topic usernameTopic = topic.getCompositeValue().getTopic("dm4.accesscontrol.username");
      Topic passwordTopic = topic.getCompositeValue().getTopic("dm4.accesscontrol.password");
      String newUsername = usernameTopic.getSimpleValue().toString();
      TopicModel oldUsernameTopic = oldModel.getCompositeValueModel().getTopic("dm4.accesscontrol.username", null);
      String oldUsername = oldUsernameTopic != null ? oldUsernameTopic.getSimpleValue().toString() : "";
      if (!newUsername.equals(oldUsername)) {
        if (!oldUsername.equals("")) {
          throw new RuntimeException("Changing a Username is not supported (tried \"" + oldUsername + "\" -> \"" + newUsername + "\")");
        }
        logger.info("### Username has changed from \"" + oldUsername + "\" -> \"" + newUsername + "\". Setting \"" + newUsername + "\" as the new owner of 3 topics:\n" + "    - User Account topic (ID " + topic.getId() + ")\n" + "    - Username topic (ID " + usernameTopic.getId() + ")\n" + "    - Password topic (ID " + passwordTopic.getId() + ")");
        setOwner(topic, newUsername);
        setOwner(usernameTopic, newUsername);
        setOwner(passwordTopic, newUsername);
      }
    }
  }

  @Override public void introduceTopicType(TopicType topicType) {
    setupDefaultAccessControl(topicType);
  }

  @Override public void introduceAssociationType(AssociationType assocType) {
    setupDefaultAccessControl(assocType);
  }

  @Override public void serviceRequestFilter(ContainerRequest containerRequest) {
    requestFilter(request);
  }

  @Override public void resourceRequestFilter(HttpServletRequest servletRequest) {
    requestFilter(servletRequest);
  }

  @Override public void preSendTopicType(TopicType topicType) {
    if (topicType.getUri().equals("dm4.core.meta_meta_type")) {
      enrichWithPermissions(topicType, createPermissions(false));
      return;
    }
    enrichWithPermissions(topicType, getPermissions(topicType.getId()));
  }

  @Override public void preSendAssociationType(AssociationType assocType) {
    enrichWithPermissions(assocType, getPermissions(assocType.getId()));
  }

  private Topic createUserAccount(Credentials cred) {
    return dms.createTopic(new TopicModel("dm4.accesscontrol.user_account", new CompositeValueModel().put("dm4.accesscontrol.username", cred.username).put("dm4.accesscontrol.password", cred.password)));
  }

  private boolean isUserAccount(Topic topic) {
    String typeUri = topic.getTypeUri();
    return typeUri.equals("dm4.accesscontrol.user_account") || typeUri.equals("dm4.accesscontrol.username") || typeUri.equals("dm4.accesscontrol.password");
  }

  /**
     * Fetches the default user ("admin").
     *
     * @throws  RuntimeException    If the default user doesn't exist.
     *
     * @return  The default user (a Topic of type "Username" / <code>dm4.accesscontrol.username</code>).
     */
  private Topic getUsernameOrThrow(String username) {
    Topic usernameTopic = getUsername(username);
    if (usernameTopic == null) {
      throw new RuntimeException("User \"" + username + "\" does not exist");
    }
    return usernameTopic;
  }

  private void joinIfWorkspace(Topic topic) {
    if (topic.getTypeUri().equals("dm4.workspaces.workspace")) {
      String username = getUsername();
      if (username != null) {
        createMembership(username, topic.getId());
      }
    }
  }

  private void createDefaultMembership(Topic defaultWorkspace) {
    String operation = "Creating membership for default user (\"admin\") and default workspace (\"DeepaMehta\")";
    try {
      if (isMember(DEFAULT_USERNAME, defaultWorkspace.getId())) {
        logger.info("### " + operation + " ABORTED -- membership already exists");
        return;
      }
      logger.info("### " + operation);
      createMembership(DEFAULT_USERNAME, defaultWorkspace.getId());
    } catch (Exception e) {
      throw new RuntimeException(operation + " failed", e);
    }
  }

  private void assignDefaultTopicmapToDefaultWorkspace(Topic defaultTopicmap, Topic defaultWorkspace) {
    String operation = "Assigning the default topicmap (\"untitled\") to the default workspace (\"DeepaMehta\")";
    try {
      Topic workspace = wsService.getAssignedWorkspace(defaultTopicmap.getId());
      if (workspace != null) {
        logger.info("### Assigning the default topicmap (\"untitled\") to a workspace ABORTED -- " + "already assigned to workspace \"" + workspace.getSimpleValue() + "\"");
        return;
      }
      logger.info("### " + operation);
      wsService.assignToWorkspace(defaultTopicmap, defaultWorkspace.getId());
    } catch (Exception e) {
      throw new RuntimeException(operation + " failed", e);
    }
  }

  private void setupDefaultAccessControl(Topic topic, String topicInfo) {
    String operation = "Setup access control for the " + topicInfo;
    try {
      if (getCreator(topic) != null) {
        logger.info("### " + operation + " ABORTED -- already setup");
        return;
      }
      logger.info("### " + operation);
      setupAccessControl(topic, DEFAULT_INSTANCE_ACL, DEFAULT_USERNAME);
    } catch (Exception e) {
      throw new RuntimeException(operation + " failed", e);
    }
  }

  private Topic fetchDefaultTopicmap() {
    return dms.getTopic("uri", new SimpleValue("dm4.topicmaps.default_topicmap"), false);
  }

  private void requestFilter(HttpServletRequest request) {
    logger.fine("##### " + request.getMethod() + " " + request.getRequestURL() + "\n      ##### \"Authorization\"=\"" + request.getHeader("Authorization") + "\"" + "\n      ##### " + info(request.getSession(false)));
    checkRequestOrigin(request);
    checkAuthorization(request);
  }

  private void checkRequestOrigin(HttpServletRequest request) {
    String remoteAddr = request.getRemoteAddr();
    boolean allowed = JavaUtils.isInRange(remoteAddr, SUBNET_FILTER);
    logger.fine("Remote address=\"" + remoteAddr + "\", dm4.security.subnet_filter=\"" + SUBNET_FILTER + "\" => " + (allowed ? "ALLOWED" : "FORBIDDEN"));
    if (!allowed) {
      throw403Forbidden();
    }
  }

  private void checkAuthorization(HttpServletRequest request) {
    boolean authorized;
    if (request.getSession(false) != null) {
      authorized = true;
    } else {
      String authHeader = request.getHeader("Authorization");
      if (authHeader != null) {
        authorized = tryLogin(new Credentials(authHeader), request);
      } else {
        authorized = !isLoginRequired(request);
      }
    }
    if (!authorized) {
      throw401Unauthorized();
    }
  }

  private boolean isLoginRequired(HttpServletRequest request) {
    return request.getMethod().equals("GET") ? READ_REQUIRES_LOGIN : WRITE_REQUIRES_LOGIN;
  }

  /**
     * Checks weather the credentials are valid and if so logs the user in.
     *
     * @return  true if the credentials are valid.
     */
  private boolean tryLogin(Credentials cred, HttpServletRequest request) {
    String username = cred.username;
    if (checkCredentials(cred)) {
      logger.info("##### Logging in as \"" + username + "\" => SUCCESSFUL!");
      _login(username, request);
      return true;
    } else {
      logger.info("##### Logging in as \"" + username + "\" => FAILED!");
      return false;
    }
  }

  private boolean checkCredentials(Credentials cred) {
    Topic username = getUsername(cred.username);
    if (username == null) {
      return false;
    }
    return matches(username, cred.password);
  }

  private void _login(String username, HttpServletRequest request) {
    HttpSession session = request.getSession();
    session.setAttribute("username", username);
    logger.info("##### Creating new " + info(session));
    dms.fireEvent(POST_LOGIN_USER, username);
  }

  private void _logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    String username = username(session);
    logger.info("##### Logging out from " + info(session));
    session.invalidate();
    dms.fireEvent(POST_LOGOUT_USER, username);
  }

  /**
     * Prerequisite: username is not <code>null</code>.
     *
     * @param   password    The encrypted password.
     */
  private boolean matches(Topic username, String password) {
    return password(fetchUserAccount(username)).equals(password);
  }

  /**
     * Prerequisite: username is not <code>null</code>.
     */
  private Topic fetchUserAccount(Topic username) {
    Topic userAccount = username.getRelatedTopic("dm4.core.composition", "dm4.core.child", "dm4.core.parent", "dm4.accesscontrol.user_account", true, false);
    if (userAccount == null) {
      throw new RuntimeException("Data inconsistency: there is no User Account topic for username \"" + username.getSimpleValue() + "\" (username=" + username + ")");
    }
    return userAccount;
  }

  private String username(HttpSession session) {
    String username = (String) session.getAttribute("username");
    if (username == null) {
      throw new RuntimeException("Session data inconsistency: \"username\" attribute is missing");
    }
    return username;
  }

  /**
     * @return  The encryted password of the specified User Account.
     */
  private String password(Topic userAccount) {
    return userAccount.getCompositeValue().getString("dm4.accesscontrol.password");
  }

  private void throw401Unauthorized() {
    String authScheme = READ_REQUIRES_LOGIN ? "Basic" : "xBasic";
    throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).header("WWW-Authenticate", authScheme + " realm=" + AUTHENTICATION_REALM).header("Content-Type", "text/html").entity("You\'re not authorized. Sorry.").build());
  }

  private void throw403Forbidden() {
    throw new WebApplicationException(Response.status(Status.FORBIDDEN).header("Content-Type", "text/html").entity("Access is forbidden. Sorry.").build());
  }

  /**
     * Sets the logged in user as the creator and the owner of the specified object
     * and creates a default access control entry for it.
     *
     * If no user is logged in, nothing is performed.
     */
  private void setupDefaultAccessControl(DeepaMehtaObject object) {
    setupAccessControl(object, DEFAULT_INSTANCE_ACL);
  }

  private void setupDefaultAccessControl(Type type) {
    try {
      String username = getUsername();
      if (username == null) {
        username = DEFAULT_USERNAME;
        setupViewConfigAccessControl(type.getViewConfig());
      }
      setupAccessControl(type, DEFAULT_INSTANCE_ACL, username);
    } catch (Exception e) {
      throw new RuntimeException("Setting up access control for " + info(type) + " failed (" + type + ")", e);
    }
  }

  private void setupUserAccountAccessControl(Topic topic) {
    setupAccessControl(topic, DEFAULT_USER_ACCOUNT_ACL);
  }

  private void setupViewConfigAccessControl(ViewConfiguration viewConfig) {
    for (Topic configTopic : viewConfig.getConfigTopics()) {
      setupAccessControl(configTopic, DEFAULT_INSTANCE_ACL, DEFAULT_USERNAME);
    }
  }

  private void setupAccessControl(DeepaMehtaObject object, AccessControlList acl) {
    try {
      String username = getUsername();
      if (username == null) {
        logger.fine("Setting up access control for " + info(object) + " ABORTED -- no user is logged in");
        return;
      }
      setupAccessControl(object, acl, username);
    } catch (Exception e) {
      throw new RuntimeException("Setting up access control for " + info(object) + " failed (" + object + ")", e);
    }
  }

  /**
     * @param   username    must not be null.
     */
  private void setupAccessControl(DeepaMehtaObject object, AccessControlList acl, String username) {
    setCreator(object, username);
    setOwner(object, username);
    setACL(object, acl);
  }

  /**
     * @param   objectId    a topic ID, or an association ID
     */
  private void checkReadPermission(long objectId) {
    String username = getUsername();
    if (!hasPermission(username, Operation.READ, objectId)) {
      throw new AccessControlException(userInfo(username) + " has no READ permission for object " + objectId);
    }
  }

  /**
     * Checks if a user is permitted to perform an operation on an object (topic or association).
     * If so, <code>true</code> is returned.
     *
     * @param   username    the logged in user, or <code>null</code> if no user is logged in.
     * @param   objectId    a topic ID, or an association ID.
     */
  private boolean hasPermission(String username, Operation operation, long objectId) {
    return dms.getAccessControl().hasPermission(username, operation, objectId);
  }

  private Permissions getPermissions(long objectId) {
    return createPermissions(hasPermission(getUsername(), Operation.WRITE, objectId));
  }

  /**
     * Checks if a user is the creator of the object.
     * If so, <code>true</code> is returned.
     *
     * Prerequisite: a user is logged in (<code>username</code> is not <code>null</code>).
     *
     * @param   username    a Topic of type "Username" (<code>dm4.accesscontrol.username</code>). ### FIXDOC
     */
  private void enrichWithPermissions(Type type, Permissions permissions) {
    CompositeValueModel typePermissions = permissions(type);
    typePermissions.put(Operation.WRITE.uri, permissions.get(Operation.WRITE.uri));
  }

  private CompositeValueModel permissions(DeepaMehtaObject object) {
    TopicModel permissionsTopic = object.getCompositeValue().getModel().getTopic("dm4.accesscontrol.permissions", null);
    CompositeValueModel permissions;
    if (permissionsTopic != null) {
      permissions = permissionsTopic.getCompositeValueModel();
    } else {
      permissions = new CompositeValueModel();
      object.getCompositeValue().getModel().put("dm4.accesscontrol.permissions", permissions);
    }
    return permissions;
  }

  private Permissions createPermissions(boolean write) {
    return new Permissions().add(Operation.WRITE, write);
  }

  private String info(DeepaMehtaObject object) {
    if (object instanceof TopicType) {
      return "topic type \"" + object.getUri() + "\" (id=" + object.getId() + ")";
    } else {
      if (object instanceof AssociationType) {
        return "association type \"" + object.getUri() + "\" (id=" + object.getId() + ")";
      } else {
        if (object instanceof Topic) {
          return "topic " + object.getId() + " (typeUri=\"" + object.getTypeUri() + "\", uri=\"" + object.getUri() + "\")";
        } else {
          if (object instanceof Association) {
            return "association " + object.getId() + " (typeUri=\"" + object.getTypeUri() + "\")";
          } else {
            throw new RuntimeException("Unexpected object: " + object);
          }
        }
      }
    }
  }

  private String userInfo(String username) {
    return "user " + (username != null ? "\"" + username + "\"" : "<anonymous>");
  }

  private String info(HttpSession session) {
    return "session" + (session != null ? " " + session.getId() + " (username=" + username(session) + ")" : ": null");
  }

  private String info(HttpServletRequest request) {
    StringBuilder info = new StringBuilder();
    info.append("    " + request.getMethod() + " " + request.getRequestURI() + "\n");
    Enumeration<String> e1 = request.getHeaderNames();
    while (e1.hasMoreElements()) {
      String name = e1.nextElement();
      info.append("\n    " + name + ":");
      Enumeration<String> e2 = request.getHeaders(name);
      while (e2.hasMoreElements()) {
        String header = e2.nextElement();
        info.append(" " + header);
      }
    }
    return info.toString();
  }
}