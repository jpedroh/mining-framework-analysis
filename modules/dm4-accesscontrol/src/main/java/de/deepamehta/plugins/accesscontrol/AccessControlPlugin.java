package de.deepamehta.plugins.accesscontrol;

import com.sun.jersey.spi.container.ContainerRequest;
import de.deepamehta.core.Association;
import de.deepamehta.core.AssociationType;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.Type;
import de.deepamehta.core.ViewConfiguration;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.model.ChildTopicsModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.model.TopicRoleModel;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.DeepaMehtaEvent;
import de.deepamehta.core.service.EventListener;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Transactional;
import de.deepamehta.core.service.accesscontrol.AccessControlException;
import de.deepamehta.core.service.accesscontrol.Operation;
import de.deepamehta.core.service.event.AllPluginsActiveListener;
import de.deepamehta.core.service.event.IntroduceAssociationTypeListener;
import de.deepamehta.core.service.event.IntroduceTopicTypeListener;
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
import de.deepamehta.plugins.accesscontrol.event.PostLoginUserListener;
import de.deepamehta.plugins.accesscontrol.event.PostLogoutUserListener;
import de.deepamehta.plugins.accesscontrol.model.ACLEntry;
import de.deepamehta.plugins.accesscontrol.model.AccessControlList;
import de.deepamehta.plugins.accesscontrol.model.Credentials;
import de.deepamehta.plugins.accesscontrol.model.Permissions;
import de.deepamehta.plugins.accesscontrol.model.UserRole;
import de.deepamehta.plugins.accesscontrol.service.AccessControlService;
import de.deepamehta.plugins.workspaces.service.WorkspacesService;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONObject;


@Path("/accesscontrol")
@Consumes("application/json")
@Produces("application/json")
public class AccessControlPlugin extends PluginActivator implements AccessControlService , AllPluginsActiveListener , PreGetTopicListener , PreGetAssociationListener , PostCreateTopicListener , PostCreateAssociationListener , PostUpdateTopicListener , IntroduceTopicTypeListener , IntroduceAssociationTypeListener , ServiceRequestFilterListener , ResourceRequestFilterListener , PreSendTopicTypeListener , PreSendAssociationTypeListener {
    // ------------------------------------------------------------------------------------------------------- Constants
    // Security settings
    // ------------------------------------------------------------------------------------------------------- Constants

    // Security settings
    private static final boolean READ_REQUIRES_LOGIN  = Boolean.getBoolean("dm4.security.read_requires_login");

    private static final boolean WRITE_REQUIRES_LOGIN = Boolean.getBoolean("dm4.security.write_requires_login");

    private static final String SUBNET_FILTER         = System.getProperty("dm4.security.subnet_filter");

    private static final String AUTHENTICATION_REALM = "DeepaMehta";

    // Default user account
    // Default user account
    private static final String DEFAULT_USERNAME = "admin";

    private static final String DEFAULT_PASSWORD = "";

    // Associations
    private static final String MEMBERSHIP_TYPE = "dm4.accesscontrol.membership";

    // Default ACLs
    private static final AccessControlList DEFAULT_INSTANCE_ACL = new AccessControlList(new ACLEntry(Operation.WRITE, UserRole.CREATOR, UserRole.OWNER, UserRole.MEMBER));

    private static final AccessControlList DEFAULT_USER_ACCOUNT_ACL = new AccessControlList(new ACLEntry(Operation.WRITE, UserRole.CREATOR, UserRole.OWNER));

    // Property URIs
    private static String PROP_CREATOR = "dm4.accesscontrol.creator";

    private static String PROP_OWNER = "dm4.accesscontrol.owner";

    private static String PROP_ACL = "dm4.accesscontrol.acl";

    // Events
    private static DeepaMehtaEvent POST_LOGIN_USER = new DeepaMehtaEvent(PostLoginUserListener.class) {
        @Override
        public void deliver(EventListener listener, Object... params) {
            ((PostLoginUserListener) (listener)).postLoginUser(((String) (params[0])));
        }
    };

    private static DeepaMehtaEvent POST_LOGOUT_USER = new DeepaMehtaEvent(PostLogoutUserListener.class) {
        @Override
        public void deliver(EventListener listener, Object... params) {
            ((PostLogoutUserListener) (listener)).postLogoutUser(((String) (params[0])));
        }
    };

    // ---------------------------------------------------------------------------------------------- Instance Variables
    // ---------------------------------------------------------------------------------------------- Instance Variables

    @Inject
    private WorkspacesService wsService;

    @Context
    private HttpServletRequest request;

    private Logger logger = Logger.getLogger(getClass().getName());

    // -------------------------------------------------------------------------------------------------- Public Methods



    // *******************************************
    // *** AccessControlService Implementation ***
    // *******************************************



    // === Session ===

    @POST
    @Path("/login")
    @Override
    public void login() {
        // Note: the actual login is performed by the request filter. See requestFilter().
    }

    @POST
    @Path("/logout")
    @Override
    public void logout() {
        _logout(request);
        //
        // For a "private" DeepaMehta installation: emulate a HTTP logout by forcing the webbrowser to bring up its
        // login dialog and to forget the former Authorization information. The user is supposed to press "Cancel".
        // The login dialog can't be used to login again.
        if (READ_REQUIRES_LOGIN) {
            throw401Unauthorized();
        }
    }

    // === User ===

    @GET
    @Path("/user")
    @Produces("text/plain")
    @Override
    public String getUsername() {
        try {
            HttpSession session = request.getSession(false);    // create=false
            if (session == null) {
                return null;
            }
            return username(session);
        } catch (IllegalStateException e) {
            // Note: if not invoked through network no request (and thus no session) is available.
            // This happens e.g. while starting up.
            return null;    // user is unknown
        }
    }

    @Override
    public Topic getUsername(String username) {
        return dms.getTopic("dm4.accesscontrol.username", new SimpleValue(username));
    }

    // === Permissions ===
    @GET
    @Path("/topic/{id}")
    @Override
    public Permissions getTopicPermissions(@PathParam("id")
    long topicId) {
        return getPermissions(topicId);
    }

    @GET
    @Path("/association/{id}")
    @Override
    public Permissions getAssociationPermissions(@PathParam("id")
    long assocId) {
        return getPermissions(assocId);
    }

    // === Creator ===
    @Override
    public String getCreator(DeepaMehtaObject object) {
        return object.hasProperty(PROP_CREATOR) ? ((String) (object.getProperty(PROP_CREATOR))) : null;
    }

    @Override
    public void setCreator(DeepaMehtaObject object, String username) {
        try {
            // addToIndex=true
            object.setProperty(PROP_CREATOR, username, true);
        } catch (java.lang.Exception e) {
            throw new RuntimeException(((("Setting the creator of " + info(object)) + " failed (username=") + username) + ")", e);
        }
    }

    // === Owner ===
    @Override
    public String getOwner(DeepaMehtaObject object) {
        // ### TODO: delegate to Core's AccessControl.owner()?
        return object.hasProperty(PROP_OWNER) ? ((String) (object.getProperty(PROP_OWNER))) : null;
    }

    @Override
    public void setOwner(DeepaMehtaObject object, String username) {
        try {
            // addToIndex=true
            object.setProperty(PROP_OWNER, username, true);
        } catch (java.lang.Exception e) {
            throw new RuntimeException(((("Setting the owner of " + info(object)) + " failed (username=") + username) + ")", e);
        }
    }

    // === Access Control List ===
    @Override
    public AccessControlList getACL(DeepaMehtaObject object) {
        try {
            if (object.hasProperty(PROP_ACL)) {
                return new AccessControlList(new JSONObject(((String) (object.getProperty(PROP_ACL)))));
            } else {
                return new AccessControlList();
            }
        } catch (java.lang.Exception e) {
            throw new RuntimeException(("Fetching the ACL of " + info(object)) + " failed", e);
        }
    }

    @Override
    public void setACL(DeepaMehtaObject object, AccessControlList acl) {
        try {
            // addToIndex=false
            object.setProperty(PROP_ACL, acl.toJSON().toString(), false);
        } catch (java.lang.Exception e) {
            throw new RuntimeException(("Setting the ACL of " + info(object)) + " failed", e);
        }
    }

    // === Workspaces ===
    @POST
    @Path("/user/{username}/workspace/{workspace_id}")
    @Transactional
    @Override
    public void createMembership(@PathParam("username")
    String username, @PathParam("workspace_id")
    long workspaceId) {
        try {
            dms.createAssociation(new AssociationModel(MEMBERSHIP_TYPE, new TopicRoleModel(getUsernameOrThrow(username).getId(), "dm4.core.default"), new TopicRoleModel(workspaceId, "dm4.core.default")));
        } catch (java.lang.Exception e) {
            throw new RuntimeException(((("Creating membership for user \"" + username) + "\" and workspace ") + workspaceId) + " failed", e);
        }
    }

    @Override
    public boolean isMember(String username, long workspaceId) {
        return dms.getAccessControl().isMember(username, workspaceId);
    }

    // === Retrieval ===
    @GET
    @Path("/creator/{username}/topics")
    @Override
    public Collection<Topic> getTopicsByCreator(@PathParam("username")
    String username) {
        return dms.getTopicsByProperty(PROP_CREATOR, username);
    }

    @GET
    @Path("/owner/{username}/topics")
    @Override
    public Collection<Topic> getTopicsByOwner(@PathParam("username")
    String username) {
        return dms.getTopicsByProperty(PROP_OWNER, username);
    }

    @GET
    @Path("/creator/{username}/assocs")
    @Override
    public Collection<Association> getAssociationsByCreator(@PathParam("username")
    String username) {
        return dms.getAssociationsByProperty(PROP_CREATOR, username);
    }

    @GET
    @Path("/owner/{username}/assocs")
    @Override
    public Collection<Association> getAssociationsByOwner(@PathParam("username")
    String username) {
        return dms.getAssociationsByProperty(PROP_OWNER, username);
    }

    // ****************************
    // *** Hook Implementations ***
    // ****************************



    @Override
    public void postInstall() {
        logger.info("Creating \"admin\" user account");
        Topic adminAccount = createUserAccount(new Credentials(DEFAULT_USERNAME, DEFAULT_PASSWORD));
        // Note 1: the admin account needs to be setup for access control itself.
        // At post-install time our listeners are not yet registered. So we must setup manually here.
        // Note 2: at post-install time there is no user session. So we call setupAccessControl() directly
        // instead of (the higher-level) setupUserAccountAccessControl().
        setupAccessControl(adminAccount, DEFAULT_USER_ACCOUNT_ACL, DEFAULT_USERNAME);
        // ### TODO: setup access control for the admin account's Username and Password topics.
        // However, they are not strictly required for the moment.
    }

    @Override
    public void init() {
        logger.info("Security settings:" +
            "\n    dm4.security.read_requires_login=" + READ_REQUIRES_LOGIN +
            "\n    dm4.security.write_requires_login=" + WRITE_REQUIRES_LOGIN +
            "\n    dm4.security.subnet_filter=\""+ SUBNET_FILTER + "\"");
    }

    // ********************************
    // *** Listener Implementations ***
    // ********************************
    /**
     * Setup access control for the default user and the default topicmap.
     * 1) create membership for default user and default workspace
     * 2) setup access control for default workspace
     * 3) assign default topicmap to default workspace
     * 4) setup access control for default topicmap
     */
    @Override
    public void allPluginsActive() {
        DeepaMehtaTransaction tx = dms.beginTx();
        try {
            Topic defaultWorkspace = wsService.getDefaultWorkspace();
            //
            // 1) create membership for default user and default workspace
            createDefaultMembership(defaultWorkspace);
            // 2) setup access control for default workspace
            setupDefaultAccessControl(defaultWorkspace, "default workspace (\"DeepaMehta\")");
            // 
            Topic defaultTopicmap = fetchDefaultTopicmap();
            if (defaultTopicmap != null) {
                // 3) assign default topicmap to default workspace
                assignDefaultTopicmapToDefaultWorkspace(defaultTopicmap, defaultWorkspace);
                // 4) setup access control for default topicmap
                setupDefaultAccessControl(defaultTopicmap, "default topicmap (\"untitled\")");
            }
            // 
            tx.success();
        } catch (java.lang.Exception e) {
            logger.warning(("ROLLBACK! (" + this) + ")");
            throw new RuntimeException(("Setting up " + this) + " failed", e);
        } finally {
            tx.finish();
        }
    }

    // ---

    @Override
    public void preGetTopic(long topicId) {
        checkReadPermission(topicId);
    }

    @Override
    public void preGetAssociation(long assocId) {
        checkReadPermission(assocId);
        //
        long[] playerIds = dms.getPlayerIds(assocId);
        checkReadPermission(playerIds[0]);
        checkReadPermission(playerIds[1]);
    }

    // ---

    @Override
    public void postCreateTopic(Topic topic) {
        if (isUserAccount(topic)) {
            setupUserAccountAccessControl(topic);
        } else {
            setupDefaultAccessControl(topic);
        }
        //
        // when a workspace is created its creator joins automatically
        joinIfWorkspace(topic);
    }

    @Override
    public void postCreateAssociation(Association assoc) {
        setupDefaultAccessControl(assoc);
    }

    // ---
    @Override
    public void postUpdateTopic(Topic topic, TopicModel newModel, TopicModel oldModel) {
        if (topic.getTypeUri().equals("dm4.accesscontrol.user_account")) {
            Topic usernameTopic = topic.getChildTopics().getTopic("dm4.accesscontrol.username");
            Topic passwordTopic = topic.getChildTopics().getTopic("dm4.accesscontrol.password");
            String newUsername = usernameTopic.getSimpleValue().toString();
            TopicModel oldUsernameTopic = oldModel.getChildTopicsModel().getTopic("dm4.accesscontrol.username", null);
            String oldUsername = (oldUsernameTopic != null) ? oldUsernameTopic.getSimpleValue().toString() : "";
            if (!newUsername.equals(oldUsername)) {
                // 
                if (!oldUsername.equals("")) {
                    throw new RuntimeException(((("Changing a Username is not supported (tried \"" + oldUsername) + "\" -> \"") + newUsername) + "\")");
                }
                // 
                logger.info((((((((((((((("### Username has changed from \"" + oldUsername) + "\" -> \"") + newUsername) + "\". Setting \"") + newUsername) + "\" as the new owner of 3 topics:\n") + "    - User Account topic (ID ") + topic.getId()) + ")\n") + "    - Username topic (ID ") + usernameTopic.getId()) + ")\n") + "    - Password topic (ID ") + passwordTopic.getId()) + ")");
                setOwner(topic, newUsername);
                setOwner(usernameTopic, newUsername);
                setOwner(passwordTopic, newUsername);
            }
        }
    }

    // ---

    @Override
    public void introduceTopicType(TopicType topicType) {
        setupDefaultAccessControl(topicType);
    }

    @Override
    public void introduceAssociationType(AssociationType assocType) {
        setupDefaultAccessControl(assocType);
    }

    // ---

    @Override
    public void serviceRequestFilter(ContainerRequest containerRequest) {
        // Note: we pass the injected HttpServletRequest
        requestFilter(request);
    }

    @Override
    public void resourceRequestFilter(HttpServletRequest servletRequest) {
        // Note: for the resource filter no HttpServletRequest is injected
        requestFilter(servletRequest);
    }

    // ---
    // ### TODO: make the types cachable (like topics/associations). That is, don't deliver the permissions along
    // with the types (don't use the preSend{}Type hooks). Instead let the client request the permissions separately.
    @Override
    public void preSendTopicType(TopicType topicType) {
        // Note: the permissions for "Meta Meta Type" must be set manually.
        // This type doesn't exist in DB. Fetching its ACL entries would fail.
        if (topicType.getUri().equals("dm4.core.meta_meta_type")) {
            // write=false
            enrichWithPermissions(topicType, createPermissions(false));
            return;
        }
        //
        enrichWithPermissions(topicType, getPermissions(topicType.getId()));
    }

    @Override
    public void preSendAssociationType(AssociationType assocType) {
        enrichWithPermissions(assocType, getPermissions(assocType.getId()));
    }

    // ------------------------------------------------------------------------------------------------- Private Methods
    private Topic createUserAccount(Credentials cred) {
        return dms.createTopic(new TopicModel("dm4.accesscontrol.user_account", new ChildTopicsModel().put("dm4.accesscontrol.username", cred.username).put("dm4.accesscontrol.password", cred.password)));
    }

    private boolean isUserAccount(Topic topic) {
        String typeUri = topic.getTypeUri();
        return typeUri.equals("dm4.accesscontrol.user_account")
            || typeUri.equals("dm4.accesscontrol.username")
            || typeUri.equals("dm4.accesscontrol.password");
    }

    /**
     * Fetches the default user ("admin").
     *
     * @throws  RuntimeException    If the default user doesn't exist.
     *
     * @return  The default user (a Topic of type "Username" / <code>dm4.accesscontrol.username</code>).
     */
    /* ### private Topic fetchDefaultUser() {
        return getUsernameOrThrow(DEFAULT_USERNAME);
    } */
    private Topic getUsernameOrThrow(String username) {
        Topic usernameTopic = getUsername(username);
        if (usernameTopic == null) {
            throw new RuntimeException(("User \"" + username) + "\" does not exist");
        }
        return usernameTopic;
    }

    private void joinIfWorkspace(Topic topic) {
        if (topic.getTypeUri().equals("dm4.workspaces.workspace")) {
            String username = getUsername();
            // Note: when the default workspace is created there is no user logged in yet.
            // The default user is assigned to the default workspace later on (see allPluginsActive()).
            if (username != null) {
                createMembership(username, topic.getId());
            }
        }
    }

    // === All Plugins Activated ===
    private void createDefaultMembership(Topic defaultWorkspace) {
        String operation = "Creating membership for default user (\"admin\") and default workspace (\"DeepaMehta\")";
        try {
            // abort if membership already exists
            if (isMember(DEFAULT_USERNAME, defaultWorkspace.getId())) {
                logger.info(("### " + operation) + " ABORTED -- membership already exists");
                return;
            }
            //
            logger.info("### " + operation);
            createMembership(DEFAULT_USERNAME, defaultWorkspace.getId());
        } catch (java.lang.Exception e) {
            throw new RuntimeException(operation + " failed", e);
        }
    }

    private void assignDefaultTopicmapToDefaultWorkspace(Topic defaultTopicmap, Topic defaultWorkspace) {
        String operation = "Assigning the default topicmap (\"untitled\") to the default workspace (\"DeepaMehta\")";
        try {
            // abort if already assigned
            Topic workspace = wsService.getAssignedWorkspace(defaultTopicmap.getId());
            if (workspace != null) {
                logger.info((("### Assigning the default topicmap (\"untitled\") to a workspace ABORTED -- " + "already assigned to workspace \"") + workspace.getSimpleValue()) + """);
                return;
            }
            //
            logger.info("### " + operation);
            wsService.assignToWorkspace(defaultTopicmap, defaultWorkspace.getId());
        } catch (java.lang.Exception e) {
            throw new RuntimeException(operation + " failed", e);
        }
    }

    private void setupDefaultAccessControl(Topic topic, String topicInfo) {
        String operation = "Setup access control for the " + topicInfo;
        try {
            // Note: we only check for creator assignment.
            // If an object has a creator assignment it is expected to have an ACL entry as well.
            if (getCreator(topic) != null) {
                logger.info(("### " + operation) + " ABORTED -- already setup");
                return;
            }
            //
            logger.info("### " + operation);
            setupAccessControl(topic, DEFAULT_INSTANCE_ACL, DEFAULT_USERNAME);
        } catch (java.lang.Exception e) {
            throw new RuntimeException(operation + " failed", e);
        }
    }

    private Topic fetchDefaultTopicmap() {
        // Note: the Access Control plugin does not DEPEND on the Topicmaps plugin but is designed to work TOGETHER
        // with the Topicmaps plugin.
        // Currently the Access Control plugin needs to know some Topicmaps internals e.g. the URI of the default
        // topicmap. ### TODO: make "optional plugin dependencies" an explicit concept. Plugins must be able to ask
        // the core weather a certain plugin is installed (regardles weather it is activated already) and would wait
        // for its service only if installed.
        return dms.getTopic("uri", new SimpleValue("dm4.topicmaps.default_topicmap"));
    }

    // === Request Filter ===

    private void requestFilter(HttpServletRequest request) {
        logger.fine("##### " + request.getMethod() + " " + request.getRequestURL() +
            "\n      ##### \"Authorization\"=\"" + request.getHeader("Authorization") + "\"" +
            "\n      ##### " + info(request.getSession(false)));    // create=false
        //
        checkRequestOrigin(request);    // throws WebApplicationException
        checkAuthorization(request);    // throws WebApplicationException
    }

    // ---

    private void checkRequestOrigin(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        boolean allowed = JavaUtils.isInRange(remoteAddr, SUBNET_FILTER);
        //
        logger.fine("Remote address=\"" + remoteAddr + "\", dm4.security.subnet_filter=\"" + SUBNET_FILTER +
            "\" => " + (allowed ? "ALLOWED" : "FORBIDDEN"));
        //
        if (!allowed) {
            throw403Forbidden();    // throws WebApplicationException
        }
    }

    private void checkAuthorization(HttpServletRequest request) {
        boolean authorized;
        if (request.getSession(false) != null) {    // create=false
            authorized = true;
        } else {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                // Note: if login fails we are NOT authorized, even if no login is required
                authorized = tryLogin(new Credentials(authHeader), request);
            } else {
                authorized = !isLoginRequired(request);
            }
        }
        //
        if (!authorized) {
            throw401Unauthorized(); // throws WebApplicationException
        }
    }

    // ---

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

    // ---

    private void _login(String username, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("username", username);
        logger.info("##### Creating new " + info(session));
        //
        dms.fireEvent(POST_LOGIN_USER, username);
    }

    private void _logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);    // create=false
        String username = username(session);                // save username before invalidating
        logger.info("##### Logging out from " + info(session));
        //
        session.invalidate();
        //
        dms.fireEvent(POST_LOGOUT_USER, username);
    }

    // ---

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
        Topic userAccount = username.getRelatedTopic("dm4.core.composition", "dm4.core.child", "dm4.core.parent",
            "dm4.accesscontrol.user_account");
        if (userAccount == null) {
            throw new RuntimeException("Data inconsistency: there is no User Account topic for username \"" +
                username.getSimpleValue() + "\" (username=" + username + ")");
        }
        return userAccount;
    }

    // ---

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
        return userAccount.getChildTopics().getString("dm4.accesscontrol.password");
    }

    // ---

    private void throw401Unauthorized() {
        // Note: a non-private DM installation (read_requires_login=false) utilizes DM's login dialog and must suppress
        // the browser's login dialog. To suppress the browser's login dialog a contrived authentication scheme "xBasic"
        // is used (see http://loudvchar.blogspot.ca/2010/11/avoiding-browser-popup-for-401.html)
        String authScheme = READ_REQUIRES_LOGIN ? "Basic" : "xBasic";
        throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
            .header("WWW-Authenticate", authScheme + " realm=" + AUTHENTICATION_REALM)
            .header("Content-Type", "text/html")    // for text/plain (default) Safari provides no Web Console
            .entity("You're not authorized. Sorry.")
            .build());
    }

    private void throw403Forbidden() {
        throw new WebApplicationException(Response.status(Status.FORBIDDEN)
            .header("Content-Type", "text/html")    // for text/plain (default) Safari provides no Web Console
            .entity("Access is forbidden. Sorry.")
            .build());
    }

    // === Create ACL Entries ===

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
            // 
            if (username == null) {
                username = DEFAULT_USERNAME;
                setupViewConfigAccessControl(type.getViewConfig());
            }
            //
            setupAccessControl(type, DEFAULT_INSTANCE_ACL, username);
        } catch (java.lang.Exception e) {
            throw new RuntimeException(((("Setting up access control for " + info(type)) + " failed (") + type) + ")", e);
        }
    }

    // ---

    private void setupUserAccountAccessControl(Topic topic) {
        setupAccessControl(topic, DEFAULT_USER_ACCOUNT_ACL);
    }

    private void setupViewConfigAccessControl(ViewConfiguration viewConfig) {
        for (Topic configTopic : viewConfig.getConfigTopics()) {
            setupAccessControl(configTopic, DEFAULT_INSTANCE_ACL, DEFAULT_USERNAME);
        }
    }

    // ---

    private void setupAccessControl(DeepaMehtaObject object, AccessControlList acl) {
        try {
            String username = getUsername();
            // Note: when no user is logged in we do NOT fallback to the default user for the access control setup.
            // This would not help in gaining data consistency because the topics/associations created so far
            // (BEFORE the Access Control plugin is activated) would still have no access control setup.
            // Note: for types the situation is different. The type-introduction mechanism (see introduceTopicType()
            // handler above) ensures EVERY type is catched (regardless of plugin activation order). For instances on
            // the other hand we don't have such a mechanism (and don't want one either).
            if (username == null) {
                logger.fine("Setting up access control for " + info(object) + " ABORTED -- no user is logged in");
                return;
            }
            //
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

    // === Determine Permissions ===
    /**
     * @param   objectId    a topic ID, or an association ID
     */
    private void checkReadPermission(long objectId) {
        String username = getUsername();
        if (!hasPermission(username, Operation.READ, objectId)) {
            throw new AccessControlException((userInfo(username) + " has no READ permission for object ") + objectId);
        }
    }

    // ---
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

    // ########## Legacy code follows ...
    /**
     * @param   objectId    a topic ID, or an association ID.
     */
    private Permissions getPermissions(long objectId) {
        return createPermissions(hasPermission(getUsername(), Operation.WRITE, objectId));
    }

    // ---
    /**
     * Checks if a user is allowed to perform an operation on an object (topic or association).
     * If so, <code>true</code> is returned.
     *
     * @param   username    the logged in user (a Topic of type "Username" / <code>dm4.accesscontrol.username</code>),
     *                      or <code>null</code> if no user is logged in.
     */
    /* ### private boolean hasPermission(String username, Operation operation, DeepaMehtaObject object) {
        try {
            logger.fine("Determining permission for " + userInfo(username) + " to " + operation + " " + info(object));
            UserRole[] userRoles = getACL(object).getUserRoles(operation);
            for (UserRole userRole : userRoles) {
                logger.fine("There is an ACL entry for user role " + userRole);
                if (userOccupiesRole(username, userRole, object)) {
                    logger.fine("=> ALLOWED");
                    return true;
                }
            }
            logger.fine("=> DENIED");
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Determining permission for " + info(object) + " failed (" +
                userInfo(username) + ", operation=" + operation + ")", e);
        }
    } */
    /**
     * Checks if a user occupies a role with regard to the specified object.
     * If so, <code>true</code> is returned.
     *
     * @param   username    the logged in user (a Topic of type "Username" / <code>dm4.accesscontrol.username</code>),
     *                      or <code>null</code> if no user is logged in.
     */
    /* ### private boolean userOccupiesRole(String username, UserRole userRole, DeepaMehtaObject object) {
        switch (userRole) {
        case EVERYONE:
            return true;
        case USER:
            return username != null;
        case MEMBER:
            return username != null && userIsMember(username, object);
        case OWNER:
            return username != null && userIsOwner(username, object);
        case CREATOR:
            return username != null && userIsCreator(username, object);
        default:
            throw new RuntimeException(userRole + " is an unsupported user role");
        }
    } */
    // ---
    /**
     * Checks if a user is a member of any workspace the object is assigned to.
     * If so, <code>true</code> is returned.
     *
     * Prerequisite: a user is logged in (<code>username</code> is not <code>null</code>).
     *
     * @param   username    a Topic of type "Username" (<code>dm4.accesscontrol.username</code>). ### FIXDOC
     * @param   object      the object in question.
     */
    /* ### private boolean userIsMember(String username, DeepaMehtaObject object) {
        Topic usernameTopic = getUsernameOrThrow(username);
        Topic workspace = wsService.getAssignedWorkspace(object);
        logger.fine(info(object) + " is assigned to workspace \"" + workspace.getSimpleValue() + "\"");
        if (wsService.isAssignedToWorkspace(usernameTopic, workspace.getId())) {    // ### TODO: use Membership
            logger.fine(userInfo(username) + " IS member of workspace " + workspace);
            return true;
        } else {
            logger.fine(userInfo(username) + " is NOT member of workspace " + workspace);
            return false;
        }
    } */
    /**
     * Checks if a user is the owner of the object.
     * If so, <code>true</code> is returned.
     *
     * Prerequisite: a user is logged in (<code>username</code> is not <code>null</code>).
     *
     * @param   username    a Topic of type "Username" (<code>dm4.accesscontrol.username</code>). ### FIXDOC
     */
    /* ### private boolean userIsOwner(String username, DeepaMehtaObject object) {
        String owner = getOwner(object);
        logger.fine("The owner is " + userInfo(owner));
        return owner != null && owner.equals(username);
    } */
    /**
     * Checks if a user is the creator of the object.
     * If so, <code>true</code> is returned.
     *
     * Prerequisite: a user is logged in (<code>username</code> is not <code>null</code>).
     *
     * @param   username    a Topic of type "Username" (<code>dm4.accesscontrol.username</code>). ### FIXDOC
     */
    /* ### private boolean userIsCreator(String username, DeepaMehtaObject object) {
        String creator = getCreator(object);
        logger.fine("The creator is " + userInfo(creator));
        return creator != null && creator.equals(username);
    } */
    // ---
    private void enrichWithPermissions(Type type, Permissions permissions) {
        // Note: we must extend/override possibly existing permissions.
        // Consider a type update: directive UPDATE_TOPIC_TYPE is followed by UPDATE_TOPIC, both on the same object.
        // ### TODO: rethink this and possibly simplify the code. Meanwhile CREATE is dropped and we enrich with
        // only *one* permission (WRITE).
        ChildTopicsModel typePermissions = permissions(type);
        typePermissions.put(Operation.WRITE.uri, permissions.get(Operation.WRITE.uri));
    }

    private ChildTopicsModel permissions(DeepaMehtaObject object) {
        // Note 1: "dm4.accesscontrol.permissions" is a contrived URI. There is no such type definition.
        // Permissions are for transfer only, recalculated for each request, not stored in DB.
        // Note 2: The permissions topic exists only in the object's model (see note below).
        // There is no corresponding topic in the attached composite value. So we must query the model here.
        // (object.getChildTopics().getTopic(...) would not work)
        TopicModel permissionsTopic = object.getChildTopics().getModel().getTopic("dm4.accesscontrol.permissions", null);
        ChildTopicsModel permissions;
        if (permissionsTopic != null) {
            permissions = permissionsTopic.getChildTopicsModel();
        } else {
            permissions = new ChildTopicsModel();
            // Note: we put the permissions topic directly in the model here (instead of the attached composite value).
            // The "permissions" topic is for transfer only. It must not be stored in the DB (as it would when putting
            // it in the attached composite value).
            object.getChildTopics().getModel().put("dm4.accesscontrol.permissions", permissions);
        }
        return permissions;
    }

    // ---

    private Permissions createPermissions(boolean write) {
        return new Permissions().add(Operation.WRITE, write);
    }

    // === Logging ===

    private String info(DeepaMehtaObject object) {
        if (object instanceof TopicType) {
            return "topic type \"" + object.getUri() + "\" (id=" + object.getId() + ")";
        } else if (object instanceof AssociationType) {
            return "association type \"" + object.getUri() + "\" (id=" + object.getId() + ")";
        } else if (object instanceof Topic) {
            return "topic " + object.getId() + " (typeUri=\"" + object.getTypeUri() + "\", uri=\"" + object.getUri() +
                "\")";
        } else if (object instanceof Association) {
            return "association " + object.getId() + " (typeUri=\"" + object.getTypeUri() + "\")";
        } else {
            throw new RuntimeException("Unexpected object: " + object);
        }
    }

    private String userInfo(String username) {
        return "user " + (username != null ? "\"" + username + "\"" : "<anonymous>");
    }

    private String info(HttpSession session) {
        return "session" + (session != null ? " " + session.getId() +
            " (username=" + username(session) + ")" : ": null");
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