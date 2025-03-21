package de.deepamehta.plugins.webservice;
import de.deepamehta.core.Association;
import de.deepamehta.core.AssociationType;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.ResultSet;
import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.model.AssociationTypeModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.model.TopicTypeModel;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.ClientState;
import de.deepamehta.core.service.Directives;
import de.deepamehta.core.service.PluginInfo;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import java.util.Set;
import java.util.logging.Logger;

@Path(value = "/core") @Consumes(value = "application/json") @Produces(value = "application/json") public class WebservicePlugin extends PluginActivator {
  private Logger logger = Logger.getLogger(getClass().getName());

  @GET @Path(value = "/topic/{id}") public Topic getTopic(@PathParam(value = "id") long topicId, @QueryParam(value = "fetch_composite") @DefaultValue(value = "true") boolean fetchComposite, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.getTopic(topicId, fetchComposite, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/topic/by_value/{key}/{value}") public Topic getTopic(@PathParam(value = "key") String key, @PathParam(value = "value") SimpleValue value, @QueryParam(value = "fetch_composite") @DefaultValue(value = "true") boolean fetchComposite, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.getTopic(key, value, fetchComposite, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/topic/by_type/{type_uri}") public ResultSet<Topic> getTopics(@PathParam(value = "type_uri") String typeUri, @QueryParam(value = "fetch_composite") @DefaultValue(value = "false") boolean fetchComposite, @QueryParam(value = "max_result_size") int maxResultSize, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.getTopics(typeUri, fetchComposite, maxResultSize, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/topic") public Set<Topic> searchTopics(@QueryParam(value = "search") String searchTerm, @QueryParam(value = "field") String fieldUri, @QueryParam(value = "wholeword") boolean wholeWord, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.searchTopics(searchTerm, fieldUri, wholeWord, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @POST @Path(value = "/topic") public Topic createTopic(TopicModel model, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.createTopic(model, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @PUT @Path(value = "/topic") public Directives updateTopic(TopicModel model, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.updateTopic(model, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @DELETE @Path(value = "/topic/{id}") public Directives deleteTopic(@PathParam(value = "id") long topicId, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.deleteTopic(topicId, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/association/{id}") public Association getAssociation(@PathParam(value = "id") long assocId, @QueryParam(value = "fetch_composite") @DefaultValue(value = "true") boolean fetchComposite, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.getAssociation(assocId, fetchComposite, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/association/{assoc_type_uri}/{topic1_id}/{topic2_id}/{role_type1_uri}/{role_type2_uri}") public Association getAssociation(@PathParam(value = "assoc_type_uri") String assocTypeUri, @PathParam(value = "topic1_id") long topic1Id, @PathParam(value = "topic2_id") long topic2Id, @PathParam(value = "role_type1_uri") String roleTypeUri1, @PathParam(value = "role_type2_uri") String roleTypeUri2, @QueryParam(value = "fetch_composite") @DefaultValue(value = "true") boolean fetchComposite, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.getAssociation(assocTypeUri, topic1Id, topic2Id, roleTypeUri1, roleTypeUri2, fetchComposite, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/association/multiple/{topic1_id}/{topic2_id}") public Set<Association> getAssociations(@PathParam(value = "topic1_id") long topic1Id, @PathParam(value = "topic2_id") long topic2Id) {
    try {
      return dms.getAssociations(topic1Id, topic2Id);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/association/multiple/{topic1_id}/{topic2_id}/{assoc_type_uri}") public Set<Association> getAssociations(@PathParam(value = "topic1_id") long topic1Id, @PathParam(value = "topic2_id") long topic2Id, @PathParam(value = "assoc_type_uri") String assocTypeUri) {
    try {
      return dms.getAssociations(topic1Id, topic2Id, assocTypeUri);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @POST @Path(value = "/association") public Association createAssociation(AssociationModel model, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.createAssociation(model, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @PUT @Path(value = "/association") public Directives updateAssociation(AssociationModel model, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.updateAssociation(model, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @DELETE @Path(value = "/association/{id}") public Directives deleteAssociation(@PathParam(value = "id") long assocId, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.deleteAssociation(assocId, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/topictype") public Set<String> getTopicTypeUris() {
    try {
      return dms.getTopicTypeUris();
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/topictype/{uri}") public TopicType getTopicType(@PathParam(value = "uri") String uri, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.getTopicType(uri, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/topictype/all") public Set<TopicType> getAllTopicTypes(@HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.getAllTopicTypes(clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @POST @Path(value = "/topictype") public TopicType createTopicType(TopicTypeModel topicTypeModel, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.createTopicType(topicTypeModel, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @PUT @Path(value = "/topictype") public Directives updateTopicType(TopicTypeModel model, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.updateTopicType(model, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/assoctype") public Set<String> getAssociationTypeUris() {
    try {
      return dms.getAssociationTypeUris();
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/assoctype/{uri}") public AssociationType getAssociationType(@PathParam(value = "uri") String uri, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.getAssociationType(uri, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/assoctype/all") public Set<AssociationType> getAssociationAllTypes(@HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.getAllAssociationTypes(clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @POST @Path(value = "/assoctype") public AssociationType createAssociationType(AssociationTypeModel assocTypeModel, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      return dms.createAssociationType(assocTypeModel, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/plugin") public Set<PluginInfo> getPluginInfo() {
    try {
      return dms.getPluginInfo();
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/topic/{id}/related_topics") public ResultSet<RelatedTopic> getTopicRelatedTopics(@PathParam(value = "id") long topicId, @QueryParam(value = "assoc_type_uri") String assocTypeUri, @QueryParam(value = "my_role_type_uri") String myRoleTypeUri, @QueryParam(value = "others_role_type_uri") String othersRoleTypeUri, @QueryParam(value = "others_topic_type_uri") String othersTopicTypeUri, @QueryParam(value = "max_result_size") int maxResultSize, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      Topic topic = dms.getTopic(topicId, false, clientState);
      return getRelatedTopics(topic, "topic", assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri, maxResultSize, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @GET @Path(value = "/association/{id}/related_topics") public ResultSet<RelatedTopic> getAssociationRelatedTopics(@PathParam(value = "id") long assocId, @QueryParam(value = "assoc_type_uri") String assocTypeUri, @QueryParam(value = "my_role_type_uri") String myRoleTypeUri, @QueryParam(value = "others_role_type_uri") String othersRoleTypeUri, @QueryParam(value = "others_topic_type_uri") String othersTopicTypeUri, @QueryParam(value = "max_result_size") int maxResultSize, @HeaderParam(value = "Cookie") ClientState clientState) {
    try {
      Association assoc = dms.getAssociation(assocId, false, clientState);
      return getRelatedTopics(assoc, "association", assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri, maxResultSize, clientState);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  private ResultSet<RelatedTopic> getRelatedTopics(DeepaMehtaObject object, String objectInfo, String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri, int maxResultSize, ClientState clientState) {
    String operation = "Retrieving related topics of " + objectInfo + " " + object.getId();
    String paramInfo = "(assocTypeUri=\"" + assocTypeUri + "\", myRoleTypeUri=\"" + myRoleTypeUri + "\", othersRoleTypeUri=\"" + othersRoleTypeUri + "\", othersTopicTypeUri=\"" + othersTopicTypeUri + "\", maxResultSize=" + maxResultSize + ")";
    try {
      logger.info(operation + " " + paramInfo);
      return object.getRelatedTopics(assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri, false, false, maxResultSize, clientState);
    } catch (Exception e) {
      throw new RuntimeException(operation + " failed " + paramInfo, e);
    }
  }
}