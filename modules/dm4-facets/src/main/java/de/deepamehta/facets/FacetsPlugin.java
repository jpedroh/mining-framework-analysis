package de.deepamehta.facets;
import de.deepamehta.core.Association;
import de.deepamehta.core.AssociationDefinition;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.model.ChildTopicsModel;
import de.deepamehta.core.model.RelatedTopicModel;
import de.deepamehta.core.model.facets.FacetValueModel;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.util.DeepaMehtaUtils;
import org.codehaus.jettison.json.JSONObject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import java.util.List;
import java.util.logging.Logger;

@Path(value = "/facet") @Consumes(value = "application/json") @Produces(value = "application/json") public class FacetsPlugin extends PluginActivator implements FacetsService {
  private Logger logger = Logger.getLogger(getClass().getName());

  @GET @Path(value = "/{facet_type_uri}/topic/{id}") @Override public RelatedTopic getFacet(@PathParam(value = "id") long topicId, @PathParam(value = "facet_type_uri") String facetTypeUri) {
    return getFacet(dm4.getTopic(topicId), facetTypeUri);
  }

  @Override public RelatedTopic getFacet(DeepaMehtaObject object, String facetTypeUri) {
    return fetchChildTopic(object, getAssocDef(facetTypeUri));
  }

  @GET @Path(value = "/multi/{facet_type_uri}/topic/{id}") @Override public List<RelatedTopic> getFacets(@PathParam(value = "id") long topicId, @PathParam(value = "facet_type_uri") String facetTypeUri) {
    return getFacets(dm4.getTopic(topicId), facetTypeUri);
  }

  @Override public List<RelatedTopic> getFacets(DeepaMehtaObject object, String facetTypeUri) {
    return fetchChildTopics(object, getAssocDef(facetTypeUri));
  }

  @GET @Path(value = "/topic/{id}") @Override public Topic getFacettedTopic(@PathParam(value = "id") long topicId, @QueryParam(value = "facet_type_uri") List<String> facetTypeUris) {
    try {
      Topic topic = dm4.getTopic(topicId);
      ChildTopicsModel childTopics = topic.getChildTopics().getModel();
      for (String facetTypeUri : facetTypeUris) {
        String childTypeUri = getChildTypeUri(facetTypeUri);
        if (!isMultiFacet(facetTypeUri)) {
          Topic value = getFacet(topic, facetTypeUri);
          if (value != null) {
            childTopics.put(childTypeUri, value.getModel());
          }
        } else {
          List<RelatedTopic> values = getFacets(topic, facetTypeUri);
          childTopics.put(childTypeUri, DeepaMehtaUtils.<RelatedTopicModel>toModelList(values));
        }
      }
      return topic;
    } catch (Exception e) {
      throw new RuntimeException("Getting facetted topic " + topicId + " failed (facetTypeUris=" + facetTypeUris + ")", e);
    }
  }

  @POST @Path(value = "/{facet_type_uri}/topic/{id}") @Override public void addFacetTypeToTopic(@PathParam(value = "id") long topicId, @PathParam(value = "facet_type_uri") String facetTypeUri) {
    dm4.createAssociation(mf.newAssociationModel("dm4.core.instantiation", mf.newTopicRoleModel(topicId, "dm4.core.instance"), mf.newTopicRoleModel(facetTypeUri, "dm4.facets.facet")));
  }

  @PUT @Path(value = "/{facet_type_uri}/topic/{id}") @Override public void updateFacet(@PathParam(value = "id") long topicId, @PathParam(value = "facet_type_uri") String facetTypeUri, FacetValueModel value) {
    try {
      updateFacet(dm4.getTopic(topicId), facetTypeUri, value);
    } catch (Exception e) {
      throw new RuntimeException("Updating facet \"" + facetTypeUri + "\" of topic " + topicId + " failed (value=" + value + ")", e);
    }
  }

  @Override public void updateFacet(DeepaMehtaObject object, String facetTypeUri, FacetValueModel value) {
    AssociationDefinition assocDef = getAssocDef(facetTypeUri);
    if (!isMultiFacet(facetTypeUri)) {
      object.updateChildTopic(value.getTopic(), assocDef);
    } else {
      object.updateChildTopics(value.getTopics(), assocDef);
    }
  }

  @Override public boolean hasFacet(long topicId, String facetTypeUri, long facetTopicId) {
    String assocTypeUri = getAssocDef(facetTypeUri).getInstanceLevelAssocTypeUri();
    Association assoc = dm4.getAssociation(assocTypeUri, topicId, facetTopicId, "dm4.core.parent", "dm4.core.child");
    return assoc != null;
  }

  /**
     * Fetches and returns a child topic or <code>null</code> if no such topic extists.
     * <p>
     * Note: There is a principal copy in DeepaMehtaObjectImpl but here the precondition is different:
     * The given association definition must not necessarily originate from the given object's type definition.
     * ### TODO: meanwhile we have the ValueStorage. Can we use its method instead?
     */
  private RelatedTopic fetchChildTopic(DeepaMehtaObject object, AssociationDefinition assocDef) {
    String assocTypeUri = assocDef.getInstanceLevelAssocTypeUri();
    String othersTypeUri = assocDef.getChildTypeUri();
    return object.getRelatedTopic(assocTypeUri, "dm4.core.parent", "dm4.core.child", othersTypeUri);
  }

  /**
     * Fetches and returns child topics.
     * <p>
     * Note: There is a principal copy in DeepaMehtaObjectImpl but here the precondition is different:
     * The given association definition must not necessarily originate from the given object's type definition.
     * ### TODO: meanwhile we have the ValueStorage. Can we use its method instead?
     */
  private List<RelatedTopic> fetchChildTopics(DeepaMehtaObject object, AssociationDefinition assocDef) {
    String assocTypeUri = assocDef.getInstanceLevelAssocTypeUri();
    String othersTypeUri = assocDef.getChildTypeUri();
    return object.getRelatedTopics(assocTypeUri, "dm4.core.parent", "dm4.core.child", othersTypeUri);
  }

  private boolean isMultiFacet(String facetTypeUri) {
    return getAssocDef(facetTypeUri).getChildCardinalityUri().equals("dm4.core.many");
  }

  private String getChildTypeUri(String facetTypeUri) {
    return getAssocDef(facetTypeUri).getChildTypeUri();
  }

  private AssociationDefinition getAssocDef(String facetTypeUri) {
    return dm4.getTopicType(facetTypeUri).getAssocDefs().iterator().next();
  }
}