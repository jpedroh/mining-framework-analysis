package de.deepamehta.topicmaps;
import de.deepamehta.topicmaps.model.TopicmapViewmodel;
import de.deepamehta.core.Association;
import de.deepamehta.core.RelatedAssociation;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.model.AssociationRoleModel;
import de.deepamehta.core.model.ChildTopicsModel;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.model.TopicRoleModel;
import de.deepamehta.core.model.topicmaps.AssociationViewModel;
import de.deepamehta.core.model.topicmaps.TopicViewModel;
import de.deepamehta.core.model.topicmaps.ViewProperties;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.util.DeepaMehtaUtils;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

@Path(value = "/topicmap") @Consumes(value = "application/json") @Produces(value = "application/json") public class TopicmapsPlugin extends PluginActivator implements TopicmapsService {
  private static final String TOPIC_MAPCONTEXT = "dm4.topicmaps.topic_mapcontext";

  private static final String ASSOCIATION_MAPCONTEXT = "dm4.topicmaps.association_mapcontext";

  private static final String ROLE_TYPE_TOPICMAP = "dm4.core.default";

  private static final String ROLE_TYPE_TOPIC = "dm4.topicmaps.topicmap_topic";

  private static final String ROLE_TYPE_ASSOCIATION = "dm4.topicmaps.topicmap_association";

  private static final String PROP_X = "dm4.topicmaps.x";

  private static final String PROP_Y = "dm4.topicmaps.y";

  private static final String PROP_VISIBILITY = "dm4.topicmaps.visibility";

  private Map<String, TopicmapRenderer> topicmapRenderers = new HashMap();

  private List<ViewmodelCustomizer> viewmodelCustomizers = new ArrayList();

  private Logger logger = Logger.getLogger(getClass().getName());

  public TopicmapsPlugin() {
    registerTopicmapRenderer(new DefaultTopicmapRenderer());
  }

  @POST @Override public Topic createTopicmap(@QueryParam(value = "name") String name, @QueryParam(value = "renderer_uri") String topicmapRendererUri, @QueryParam(value = "private") boolean isPrivate) {
    logger.info("Creating topicmap \"" + name + "\" (topicmapRendererUri=\"" + topicmapRendererUri + "\", isPrivate=" + isPrivate + ")");
    return dm4.createTopic(mf.newTopicModel("dm4.topicmaps.topicmap", mf.newChildTopicsModel().put("dm4.topicmaps.name", name).put("dm4.topicmaps.topicmap_renderer_uri", topicmapRendererUri).put("dm4.topicmaps.private", isPrivate).put("dm4.topicmaps.state", getTopicmapRenderer(topicmapRendererUri).initialTopicmapState(mf))));
  }

  @GET @Path(value = "/{id}") @Override public TopicmapViewmodel getTopicmap(@PathParam(value = "id") long topicmapId, @QueryParam(value = "include_childs") boolean includeChilds) {
    try {
      logger.info("Loading topicmap " + topicmapId + " (includeChilds=" + includeChilds + ")");
      Topic topicmapTopic = dm4.getTopic(topicmapId).loadChildTopics();
      Map<Long, TopicViewModel> topics = fetchTopics(topicmapTopic, includeChilds);
      Map<Long, AssociationViewModel> assocs = fetchAssociations(topicmapTopic);
      return new TopicmapViewmodel(topicmapTopic.getModel(), topics, assocs);
    } catch (Exception e) {
      throw new RuntimeException("Fetching topicmap " + topicmapId + " failed", e);
    }
  }

  @Override public boolean isTopicInTopicmap(long topicmapId, long topicId) {
    return fetchTopicRefAssociation(topicmapId, topicId) != null;
  }

  @Override public boolean isAssociationInTopicmap(long topicmapId, long assocId) {
    return fetchAssociationRefAssociation(topicmapId, assocId) != null;
  }

  @POST @Path(value = "/{id}/topic/{topic_id}") @Override public void addTopicToTopicmap(@PathParam(value = "id") final long topicmapId, @PathParam(value = "topic_id") final long topicId, final ViewProperties viewProps) {
    try {
      dm4.getAccessControl().runWithoutWorkspaceAssignment(new Callable<Void>() {
        @Override public Void call() {
          if (isTopicInTopicmap(topicmapId, topicId)) {
            throw new RuntimeException("The topic is already added");
          }
          Association assoc = dm4.createAssociation(mf.newAssociationModel(TOPIC_MAPCONTEXT, mf.newTopicRoleModel(topicmapId, ROLE_TYPE_TOPICMAP), mf.newTopicRoleModel(topicId, ROLE_TYPE_TOPIC)));
          storeViewProperties(assoc, viewProps);
          return null;
        }
      });
    } catch (Exception e) {
      throw new RuntimeException("Adding topic " + topicId + " to topicmap " + topicmapId + " failed " + "(viewProps=" + viewProps + ")", e);
    }
  }

  @Override public void addTopicToTopicmap(long topicmapId, long topicId, int x, int y, boolean visibility) {
    addTopicToTopicmap(topicmapId, topicId, new ViewProperties(x, y, visibility));
  }

  @POST @Path(value = "/{id}/association/{assoc_id}") @Override public void addAssociationToTopicmap(@PathParam(value = "id") final long topicmapId, @PathParam(value = "assoc_id") final long assocId) {
    try {
      dm4.getAccessControl().runWithoutWorkspaceAssignment(new Callable<Void>() {
        @Override public Void call() {
          if (isAssociationInTopicmap(topicmapId, assocId)) {
            throw new RuntimeException("The association is already added");
          }
          dm4.createAssociation(mf.newAssociationModel(ASSOCIATION_MAPCONTEXT, mf.newTopicRoleModel(topicmapId, ROLE_TYPE_TOPICMAP), mf.newAssociationRoleModel(assocId, ROLE_TYPE_ASSOCIATION)));
          return null;
        }
      });
    } catch (Exception e) {
      throw new RuntimeException("Adding association " + assocId + " to topicmap " + topicmapId + " failed", e);
    }
  }

  @PUT @Path(value = "/{id}/topic/{topic_id}") @Override public void setViewProperties(@PathParam(value = "id") long topicmapId, @PathParam(value = "topic_id") long topicId, ViewProperties viewProps) {
    storeViewProperties(topicmapId, topicId, viewProps);
  }

  @PUT @Path(value = "/{id}/topic/{topic_id}/{x}/{y}") @Override public void setTopicPosition(@PathParam(value = "id") long topicmapId, @PathParam(value = "topic_id") long topicId, @PathParam(value = "x") int x, @PathParam(value = "y") int y) {
    storeViewProperties(topicmapId, topicId, new ViewProperties(x, y));
  }

  @PUT @Path(value = "/{id}/topic/{topic_id}/{visibility}") @Override public void setTopicVisibility(@PathParam(value = "id") long topicmapId, @PathParam(value = "topic_id") long topicId, @PathParam(value = "visibility") boolean visibility) {
    storeViewProperties(topicmapId, topicId, new ViewProperties(visibility));
  }

  @DELETE @Path(value = "/{id}/association/{assoc_id}") @Override public void removeAssociationFromTopicmap(@PathParam(value = "id") long topicmapId, @PathParam(value = "assoc_id") long assocId) {
    try {
      Association assoc = fetchAssociationRefAssociation(topicmapId, assocId);
      if (assoc == null) {
        throw new RuntimeException("Association " + assocId + " is not contained in topicmap " + topicmapId);
      }
      assoc.delete();
    } catch (Exception e) {
      throw new RuntimeException("Removing association " + assocId + " from topicmap " + topicmapId + " failed ", e);
    }
  }

  @PUT @Path(value = "/{id}") @Override public void setClusterPosition(@PathParam(value = "id") long topicmapId, ClusterCoords coords) {
    for (ClusterCoords.Entry entry : coords) {
      setTopicPosition(topicmapId, entry.topicId, entry.x, entry.y);
    }
  }

  @PUT @Path(value = "/{id}/translation/{x}/{y}") @Override public void setTopicmapTranslation(@PathParam(value = "id") long topicmapId, @PathParam(value = "x") int transX, @PathParam(value = "y") int transY) {
    try {
      ChildTopicsModel topicmapState = mf.newChildTopicsModel().put("dm4.topicmaps.state", mf.newChildTopicsModel().put("dm4.topicmaps.translation", mf.newChildTopicsModel().put("dm4.topicmaps.translation_x", transX).put("dm4.topicmaps.translation_y", transY)));
      dm4.updateTopic(mf.newTopicModel(topicmapId, topicmapState));
    } catch (Exception e) {
      throw new RuntimeException("Setting translation of topicmap " + topicmapId + " failed (transX=" + transX + ", transY=" + transY + ")", e);
    }
  }

  @Override public void registerTopicmapRenderer(TopicmapRenderer renderer) {
    logger.info("### Registering topicmap renderer \"" + renderer.getClass().getName() + "\"");
    topicmapRenderers.put(renderer.getUri(), renderer);
  }

  @Override public void registerViewmodelCustomizer(ViewmodelCustomizer customizer) {
    logger.info("### Registering viewmodel customizer \"" + customizer.getClass().getName() + "\"");
    viewmodelCustomizers.add(customizer);
  }

  @Override public void unregisterViewmodelCustomizer(ViewmodelCustomizer customizer) {
    logger.info("### Unregistering viewmodel customizer \"" + customizer.getClass().getName() + "\"");
    if (!viewmodelCustomizers.remove(customizer)) {
      throw new RuntimeException("Unregistering viewmodel customizer failed (customizer=" + customizer + ")");
    }
  }

  @GET @Path(value = "/{id}") @Produces(value = "text/html") public InputStream getTopicmapInWebclient() {
    return invokeWebclient();
  }

  @GET @Path(value = "/{id}/topic/{topic_id}") @Produces(value = "text/html") public InputStream getTopicmapAndTopicInWebclient() {
    return invokeWebclient();
  }

  private Map<Long, TopicViewModel> fetchTopics(Topic topicmapTopic, boolean includeChilds) {
    Map<Long, TopicViewModel> topics = new HashMap();
    List<RelatedTopic> relTopics = topicmapTopic.getRelatedTopics(TOPIC_MAPCONTEXT, "dm4.core.default", "dm4.topicmaps.topicmap_topic", null);
    if (includeChilds) {
      DeepaMehtaUtils.loadChildTopics(relTopics);
    }
    for (RelatedTopic topic : relTopics) {
      topics.put(topic.getId(), createTopicViewModel(topic));
    }
    return topics;
  }

  private Map<Long, AssociationViewModel> fetchAssociations(Topic topicmapTopic) {
    Map<Long, AssociationViewModel> assocs = new HashMap();
    List<RelatedAssociation> relAssocs = topicmapTopic.getRelatedAssociations(ASSOCIATION_MAPCONTEXT, "dm4.core.default", "dm4.topicmaps.topicmap_association", null);
    for (RelatedAssociation assoc : relAssocs) {
      assocs.put(assoc.getId(), mf.newAssociationViewModel(assoc.getModel()));
    }
    return assocs;
  }

  private TopicViewModel createTopicViewModel(RelatedTopic topic) {
    try {
      ViewProperties viewProps = fetchViewProperties(topic.getRelatingAssociation());
      invokeViewmodelCustomizers(topic, viewProps);
      return mf.newTopicViewModel(topic.getModel(), viewProps);
    } catch (Exception e) {
      throw new RuntimeException("Creating viewmodel for topic " + topic.getId() + " failed", e);
    }
  }

  private Association fetchTopicRefAssociation(long topicmapId, long topicId) {
    return dm4.getAssociation(TOPIC_MAPCONTEXT, topicmapId, topicId, ROLE_TYPE_TOPICMAP, ROLE_TYPE_TOPIC);
  }

  private Association fetchAssociationRefAssociation(long topicmapId, long assocId) {
    return dm4.getAssociationBetweenTopicAndAssociation(ASSOCIATION_MAPCONTEXT, topicmapId, assocId, ROLE_TYPE_TOPICMAP, ROLE_TYPE_ASSOCIATION);
  }

  private ViewProperties fetchViewProperties(Association mapcontextAssoc) {
    int x = (Integer) mapcontextAssoc.getProperty(PROP_X);
    int y = (Integer) mapcontextAssoc.getProperty(PROP_Y);
    boolean visibility = (Boolean) mapcontextAssoc.getProperty(PROP_VISIBILITY);
    return new ViewProperties(x, y, visibility);
  }

  private void storeViewProperties(long topicmapId, long topicId, ViewProperties viewProps) {
    try {
      Association assoc = fetchTopicRefAssociation(topicmapId, topicId);
      if (assoc == null) {
        throw new RuntimeException("Topic " + topicId + " is not contained in topicmap " + topicmapId);
      }
      storeViewProperties(assoc, viewProps);
    } catch (Exception e) {
      throw new RuntimeException("Storing view properties of topic " + topicId + " failed " + "(viewProps=" + viewProps + ")", e);
    }
  }

  private void storeViewProperties(Association mapcontextAssoc, ViewProperties viewProps) {
    for (String propUri : viewProps) {
      mapcontextAssoc.setProperty(propUri, viewProps.get(propUri), false);
    }
  }

  private void invokeViewmodelCustomizers(RelatedTopic topic, ViewProperties viewProps) {
    for (ViewmodelCustomizer customizer : viewmodelCustomizers) {
      invokeViewmodelCustomizer(customizer, topic, viewProps);
    }
  }

  private void invokeViewmodelCustomizer(ViewmodelCustomizer customizer, RelatedTopic topic, ViewProperties viewProps) {
    try {
      customizer.enrichViewProperties(topic, viewProps);
    } catch (Exception e) {
      throw new RuntimeException("Invoking viewmodel customizer for topic " + topic.getId() + " failed " + "(customizer=\"" + customizer.getClass().getName() + "\")", e);
    }
  }

  private TopicmapRenderer getTopicmapRenderer(String rendererUri) {
    TopicmapRenderer renderer = topicmapRenderers.get(rendererUri);
    if (renderer == null) {
      throw new RuntimeException("\"" + rendererUri + "\" is an unknown topicmap renderer");
    }
    return renderer;
  }

  private InputStream invokeWebclient() {
    return dm4.getPlugin("de.deepamehta.webclient").getStaticResource("/web/index.html");
  }
}