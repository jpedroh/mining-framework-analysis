package de.deepamehta.plugins.workspaces;

import de.deepamehta.core.Association;
import de.deepamehta.core.AssociationType;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.Type;
import de.deepamehta.core.model.ChildTopicsModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.Cookies;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.event.IntroduceAssociationTypeListener;
import de.deepamehta.core.service.event.IntroduceTopicTypeListener;
import de.deepamehta.core.service.event.PostCreateAssociationListener;
import de.deepamehta.core.service.event.PostCreateTopicListener;
import de.deepamehta.core.storage.spi.DeepaMehtaTransaction;
import de.deepamehta.plugins.facets.model.FacetValue;
import de.deepamehta.plugins.facets.service.FacetsService;
import de.deepamehta.plugins.workspaces.service.WorkspacesService;
import java.util.logging.Logger;


public class WorkspacesPlugin extends PluginActivator implements WorkspacesService , IntroduceTopicTypeListener , IntroduceAssociationTypeListener , PostCreateTopicListener , PostCreateAssociationListener {
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------------- Constants

    private static final String DEFAULT_WORKSPACE_NAME = "DeepaMehta";

    // ### TODO: "dm4.workspaces..."
    private static final String DEFAULT_WORKSPACE_URI = "de.workspaces.deepamehta";     // ### TODO: "dm4.workspaces..."

    private static final String DEFAULT_WORKSPACE_TYPE_URI = "dm4.workspaces.type.public";

    // Property URIs
    // Property URIs
    private static final String PROP_WORKSPACE_ID = "dm4.workspaces.workspace_id";

    // ---------------------------------------------------------------------------------------------- Instance Variables
    // ---------------------------------------------------------------------------------------------- Instance Variables

    @Inject
    private FacetsService facetsService;

    private Logger logger = Logger.getLogger(getClass().getName());

    // -------------------------------------------------------------------------------------------------- Public Methods
    // ****************************************
    // *** WorkspacesService Implementation ***
    // ****************************************
    @Override
    public Topic getAssignedWorkspace(long id) {
        if (!dms.hasProperty(id, PROP_WORKSPACE_ID)) {
            return null;
        }
        // 
        long workspaceId = ((Long) (dms.getProperty(id, PROP_WORKSPACE_ID)));
        return dms.getTopic(workspaceId);
    }

    @Override
    public boolean isAssignedToWorkspace(Topic topic, long workspaceId) {
        // ### TODO: check property instead facet
        return facetsService.hasFacet(topic.getId(), "dm4.workspaces.workspace_facet", workspaceId);
    }

    // ---

    @Override
    public Topic getDefaultWorkspace() {
        return fetchDefaultWorkspace();
    }

    // ---

    @Override
    public void assignToWorkspace(DeepaMehtaObject object, long workspaceId) {
        checkArgument(workspaceId);
        //
        _assignToWorkspace(object, workspaceId);
    }

    @Override
    public void assignTypeToWorkspace(Type type, long workspaceId) {
        checkArgument(workspaceId);
        //
        _assignToWorkspace(type, workspaceId);
        for (Topic configTopic : type.getViewConfig().getConfigTopics()) {
            _assignToWorkspace(configTopic, workspaceId);
        }
    }

    // ---
    @Override
    public Topic createWorkspace(String name, String uri, String workspaceTypeUri) {
        logger.info(("Creating workspace \"" + name) + "\"");
        return dms.createTopic(new TopicModel(uri, "dm4.workspaces.workspace", new ChildTopicsModel().put("dm4.workspaces.name", name).putRef("dm4.workspaces.type", workspaceTypeUri)));
    }

    // ****************************
    // *** Hook Implementations ***
    // ****************************
    /**
     * Creates the "Default" workspace.
     */
    @Override
    public void postInstall() {
        createWorkspace(DEFAULT_WORKSPACE_NAME, DEFAULT_WORKSPACE_URI, DEFAULT_WORKSPACE_TYPE_URI);
    }

    // ********************************
    // *** Listener Implementations ***
    // ********************************



    @Override
    public void introduceTopicType(TopicType topicType) {
        long workspaceId = -1;
        try {
            workspaceId = workspaceIdForType(topicType);
            if (workspaceId == -1) {
                return;
            }
            //
            assignTypeToWorkspace(topicType, workspaceId);
        } catch (Exception e) {
            throw new RuntimeException("Assigning topic type \"" + topicType.getUri() + "\" to workspace " +
                workspaceId + " failed", e);
        }
    }

    @Override
    public void introduceAssociationType(AssociationType assocType) {
        long workspaceId = -1;
        try {
            workspaceId = workspaceIdForType(assocType);
            if (workspaceId == -1) {
                return;
            }
            //
            assignTypeToWorkspace(assocType, workspaceId);
        } catch (Exception e) {
            throw new RuntimeException("Assigning association type \"" + assocType.getUri() + "\" to workspace " +
                workspaceId + " failed", e);
        }
    }

    // ---

    /**
     * Assigns every created topic to the current workspace.
     */
    @Override
    public void postCreateTopic(Topic topic) {
        long workspaceId = -1;
        try {
            // Note: we must avoid vicious circles
            if (isOwnTopic(topic)) {
                return;
            }
            //
            workspaceId = workspaceId();
            // Note: when there is no current workspace (because no user is logged in) we do NOT fallback to assigning
            // the default workspace. This would not help in gaining data consistency because the topics created so far
            // (BEFORE the Workspaces plugin is activated) would still have no workspace assignment.
            // Note: for types the situation is different. The type-introduction mechanism (see introduceTopicType()
            // handler above) ensures EVERY type is catched (regardless of plugin activation order). For instances on
            // the other hand we don't have such a mechanism (and don't want one either).
            if (workspaceId == -1) {
                return;
            }
            //
            assignToWorkspace(topic, workspaceId);
        } catch (Exception e) {
            throw new RuntimeException("Assigning topic " + topic.getId() + " to workspace " + workspaceId +
                " failed", e);
        }
    }

    /**
     * Assigns every created association to the current workspace.
     */
    @Override
    public void postCreateAssociation(Association assoc) {
        long workspaceId = -1;
        try {
            // Note: we must avoid vicious circles
            if (isOwnAssociation(assoc)) {
                return;
            }
            //
            workspaceId = workspaceId();
            // Note: when there is no current workspace (because no user is logged in) we do NOT fallback to assigning
            // the default workspace. This would not help in gaining data consistency because the associations created
            // so far (BEFORE the Workspaces plugin is activated) would still have no workspace assignment.
            // Note: for types the situation is different. The type-introduction mechanism (see introduceTopicType()
            // handler above) ensures EVERY type is catched (regardless of plugin activation order). For instances on
            // the other hand we don't have such a mechanism (and don't want one either).
            if (workspaceId == -1) {
                return;
            }
            //
            assignToWorkspace(assoc, workspaceId);
        } catch (Exception e) {
            throw new RuntimeException("Assigning association " + assoc.getId() + " to workspace " + workspaceId +
                " failed", e);
        }
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

    private long workspaceId() {
        Cookies cookies = Cookies.get();
        if (!cookies.has("dm4_workspace_id")) {
            return -1;
        }
        return cookies.getLong("dm4_workspace_id");
    }

    private long workspaceIdForType(Type type) {
        long workspaceId = workspaceId();
        if (workspaceId != -1) {
            return workspaceId;
        } else {
            // assign types of the DeepaMehta standard distribution to the default workspace
            if (isDeepaMehtaStandardType(type)) {
                Topic defaultWorkspace = fetchDefaultWorkspace();
                // Note: the default workspace is NOT required to exist ### TODO: think about it
                if (defaultWorkspace != null) {
                    return defaultWorkspace.getId();
                }
            }
        }
        return -1;
    }

    // ---
    private void _assignToWorkspace(DeepaMehtaObject object, long workspaceId) {
        // 1) create assignment association
        // Note 1: we are refering to an existing workspace. So we must add a topic reference.
        // Note 2: workspace_facet is a multi-facet. So we must call addRef() (as opposed to putRef()).
        FacetValue value = new FacetValue("dm4.workspaces.workspace").addRef(workspaceId);
        facetsService.updateFacet(object, "dm4.workspaces.workspace_facet", value);
        // 
        // 2) store assignment property
        object.setProperty(PROP_WORKSPACE_ID, workspaceId, false);// addToIndex=false

    }

    // --- Helper ---

    private boolean isDeepaMehtaStandardType(Type type) {
        return type.getUri().startsWith("dm4.");
    }

    // ---

    private boolean isOwnTopic(Topic topic) {
        return topic.getTypeUri().startsWith("dm4.workspaces.");
    }

    private boolean isOwnAssociation(Association assoc) {
        if (assoc.getTypeUri().equals("dm4.core.aggregation")) {
            Topic topic = assoc.getTopic("dm4.core.child");
            if (topic != null && topic.getTypeUri().equals("dm4.workspaces.workspace")) {
                return true;
            }
        }
        return false;
    }

    // ---

    private Topic fetchDefaultWorkspace() {
        return dms.getTopic("uri", new SimpleValue(DEFAULT_WORKSPACE_URI));
    }

    /**
     * Checks if the topic with the specified ID exists and is a Workspace. If not, an exception is thrown.
     */
    private void checkArgument(long topicId) {
        String typeUri = dms.getTopic(topicId).getTypeUri();
        if (!typeUri.equals("dm4.workspaces.workspace")) {
            throw new IllegalArgumentException("Topic " + topicId + " is not a workspace (but of type \"" + typeUri +
                "\")");
        }
    }
}