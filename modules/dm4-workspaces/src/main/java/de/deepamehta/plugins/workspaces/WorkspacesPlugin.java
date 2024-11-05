package de.deepamehta.plugins.workspaces;
import de.deepamehta.plugins.workspaces.service.WorkspacesService;
import de.deepamehta.plugins.facets.model.FacetValue;
import de.deepamehta.plugins.facets.service.FacetsService;
import de.deepamehta.core.Association;
import de.deepamehta.core.AssociationType;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.Type;
import de.deepamehta.core.model.CompositeValueModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.Cookies;
import de.deepamehta.core.service.Directives;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.event.IntroduceAssociationTypeListener;
import de.deepamehta.core.service.event.IntroduceTopicTypeListener;
import de.deepamehta.core.service.event.PostCreateAssociationListener;
import de.deepamehta.core.service.event.PostCreateTopicListener;
import de.deepamehta.core.storage.spi.DeepaMehtaTransaction;
import java.util.logging.Logger;

public class WorkspacesPlugin extends PluginActivator implements WorkspacesService, IntroduceTopicTypeListener, IntroduceAssociationTypeListener, PostCreateTopicListener, PostCreateAssociationListener {
  private static final String DEFAULT_WORKSPACE_NAME = "DeepaMehta";

  private static final String DEFAULT_WORKSPACE_URI = "de.workspaces.deepamehta";

  private static final String DEFAULT_WORKSPACE_TYPE_URI = "dm4.workspaces.type.public";

  private static final String PROP_WORKSPACE_ID = "dm4.workspaces.workspace_id";

  @Inject private FacetsService facetsService;

  private Logger logger = Logger.getLogger(getClass().getName());

  @Override public Topic getAssignedWorkspace(long id) {
    if (!dms.hasProperty(id, PROP_WORKSPACE_ID)) {
      return null;
    }
    long workspaceId = (Long) dms.getProperty(id, PROP_WORKSPACE_ID);
    return dms.getTopic(workspaceId, true);
  }

  @Override public boolean isAssignedToWorkspace(Topic topic, long workspaceId) {
    return facetsService.hasFacet(topic.getId(), "dm4.workspaces.workspace_facet", workspaceId);
  }

  @Override public Topic getDefaultWorkspace() {
    return fetchDefaultWorkspace();
  }

  @Override public void assignToWorkspace(DeepaMehtaObject object, long workspaceId) {
    checkArgument(workspaceId);
    _assignToWorkspace(object, workspaceId);
  }

  @Override public void assignTypeToWorkspace(Type type, long workspaceId) {
    checkArgument(workspaceId);
    _assignToWorkspace(type, workspaceId);
    for (Topic configTopic : type.getViewConfig().getConfigTopics()) {
      _assignToWorkspace(configTopic, workspaceId);
    }
  }

  @Override public Topic createWorkspace(String name, String uri, String workspaceTypeUri) {
    logger.info("Creating workspace \"" + name + "\"");
    return dms.createTopic(new TopicModel(uri, "dm4.workspaces.workspace", new CompositeValueModel().put("dm4.workspaces.name", name).putRef("dm4.workspaces.type", workspaceTypeUri)));
  }

  /**
     * Creates the "Default" workspace.
     */
  @Override public void postInstall() {
    createWorkspace(DEFAULT_WORKSPACE_NAME, DEFAULT_WORKSPACE_URI, DEFAULT_WORKSPACE_TYPE_URI);
  }

  @Override public void introduceTopicType(TopicType topicType) {
    long workspaceId = -1;
    try {
      workspaceId = workspaceIdForType(topicType);
      if (workspaceId == -1) {
        return;
      }
      assignTypeToWorkspace(topicType, workspaceId);
    } catch (Exception e) {
      throw new RuntimeException("Assigning topic type \"" + topicType.getUri() + "\" to workspace " + workspaceId + " failed", e);
    }
  }

  @Override public void introduceAssociationType(AssociationType assocType) {
    long workspaceId = -1;
    try {
      workspaceId = workspaceIdForType(assocType);
      if (workspaceId == -1) {
        return;
      }
      assignTypeToWorkspace(assocType, workspaceId);
    } catch (Exception e) {
      throw new RuntimeException("Assigning association type \"" + assocType.getUri() + "\" to workspace " + workspaceId + " failed", e);
    }
  }

  /**
     * Assigns every created topic to the current workspace.
     */
  @Override public void postCreateTopic(Topic topic, Directives directives) {
    long workspaceId = -1;
    try {
      if (isOwnTopic(topic)) {
        return;
      }
      workspaceId = workspaceId();
      if (workspaceId == -1) {
        return;
      }
      assignToWorkspace(topic, workspaceId);
    } catch (Exception e) {
      throw new RuntimeException("Assigning topic " + topic.getId() + " to workspace " + workspaceId + " failed", e);
    }
  }

  /**
     * Assigns every created association to the current workspace.
     */
  @Override public void postCreateAssociation(Association assoc, Directives directives) {
    long workspaceId = -1;
    try {
      if (isOwnAssociation(assoc)) {
        return;
      }
      workspaceId = workspaceId();
      if (workspaceId == -1) {
        return;
      }
      assignToWorkspace(assoc, workspaceId);
    } catch (Exception e) {
      throw new RuntimeException("Assigning association " + assoc.getId() + " to workspace " + workspaceId + " failed", e);
    }
  }

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
      if (isDeepaMehtaStandardType(type)) {
        Topic defaultWorkspace = fetchDefaultWorkspace();
        if (defaultWorkspace != null) {
          return defaultWorkspace.getId();
        }
      }
    }
    return -1;
  }

  private void _assignToWorkspace(DeepaMehtaObject object, long workspaceId) {
    FacetValue value = new FacetValue("dm4.workspaces.workspace").addRef(workspaceId);
    facetsService.updateFacet(object, "dm4.workspaces.workspace_facet", value, new Directives());
    DeepaMehtaTransaction tx = dms.beginTx();
    try {
      object.setProperty(PROP_WORKSPACE_ID, workspaceId, false);
      tx.success();
    } catch (Exception e) {
      logger.warning("ROLLBACK!");
      throw new RuntimeException("Storing workspace assignment of object " + object.getId() + " failed (workspaceId=" + workspaceId + ")", e);
    } finally {
      tx.finish();
    }
  }

  private boolean isDeepaMehtaStandardType(Type type) {
    return type.getUri().startsWith("dm4.");
  }

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

  private Topic fetchDefaultWorkspace() {
    return dms.getTopic("uri", new SimpleValue(DEFAULT_WORKSPACE_URI), false);
  }

  /**
     * Checks if the topic with the specified ID exists and is a Workspace. If not, an exception is thrown.
     */
  private void checkArgument(long topicId) {
    String typeUri = dms.getTopic(topicId, false).getTypeUri();
    if (!typeUri.equals("dm4.workspaces.workspace")) {
      throw new IllegalArgumentException("Topic " + topicId + " is not a workspace (but of type \"" + typeUri + "\")");
    }
  }
}