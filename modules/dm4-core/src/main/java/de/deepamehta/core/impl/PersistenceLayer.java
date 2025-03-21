package de.deepamehta.core.impl;
import de.deepamehta.core.Association;
import de.deepamehta.core.AssociationType;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.model.AssociationTypeModel;
import de.deepamehta.core.model.DeepaMehtaObjectModel;
import de.deepamehta.core.model.RelatedAssociationModel;
import de.deepamehta.core.model.RelatedTopicModel;
import de.deepamehta.core.model.RoleModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.model.TopicTypeModel;
import de.deepamehta.core.service.accesscontrol.AccessControlException;
import de.deepamehta.core.storage.spi.DeepaMehtaStorage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Storage vendor agnostic access control on top of vendor specific storage.
 *
 * 2 kinds of methods:
 *   - access controlled: get/create/update
 *   - direct DB access: fetch/store (as derived from storage impl)
 *
 * ### TODO: no instatiations here
 * ### TODO: hold storage object in instance variable (instead deriving) to make direct DB access more explicit
 */
public final class PersistenceLayer extends StorageDecorator {
  private static final String URI_PREFIX_TOPIC_TYPE = "domain.project.topic_type_";

  private static final String URI_PREFIX_ASSOCIATION_TYPE = "domain.project.assoc_type_";

  private static final String URI_PREFIX_ROLE_TYPE = "domain.project.role_type_";

  TypeStorage typeStorage;

  ValueStorage valueStorage;

  EventManager em;

  ModelFactoryImpl mf;

  private final Logger logger = Logger.getLogger(getClass().getName());

  public PersistenceLayer(DeepaMehtaStorage storage) {
    super(storage);
    this.em = new EventManager();
    this.mf = (ModelFactoryImpl) storage.getModelFactory();
    this.typeStorage = new TypeStorage(this);
    this.valueStorage = new ValueStorage(this);
    mf.pl = this;
    bootstrapTypeCache();
  }

  Topic getTopic(long topicId) {
    try {
      return checkReadAccessAndInstantiate(fetchTopic(topicId));
    } catch (Exception e) {
      throw new RuntimeException("Fetching topic " + topicId + " failed", e);
    }
  }

  TopicImpl getTopicByUri(String uri) {
    return getTopicByValue("uri", new SimpleValue(uri));
  }

  TopicImpl getTopicByValue(String key, SimpleValue value) {
    try {
      TopicModelImpl topic = fetchTopic(key, value);
      return topic != null ? this.<TopicImpl>checkReadAccessAndInstantiate(topic) : null;
    } catch (Exception e) {
      throw new RuntimeException("Fetching topic failed (key=\"" + key + "\", value=\"" + value + "\")", e);
    }
  }

  List<Topic> getTopicsByValue(String key, SimpleValue value) {
    try {
      return checkReadAccessAndInstantiate(fetchTopics(key, value));
    } catch (Exception e) {
      throw new RuntimeException("Fetching topics failed (key=\"" + key + "\", value=\"" + value + "\")", e);
    }
  }

  List<Topic> getTopicsByType(String topicTypeUri) {
    try {
      return checkReadAccessAndInstantiate(_getTopicType(topicTypeUri).getAllInstances());
    } catch (Exception e) {
      throw new RuntimeException("Fetching topics by type failed (topicTypeUri=\"" + topicTypeUri + "\")", e);
    }
  }

  List<Topic> searchTopics(String searchTerm, String fieldUri) {
    try {
      return checkReadAccessAndInstantiate(queryTopics(fieldUri, new SimpleValue(searchTerm)));
    } catch (Exception e) {
      throw new RuntimeException("Searching topics failed (searchTerm=\"" + searchTerm + "\", fieldUri=\"" + fieldUri + "\")", e);
    }
  }

  Iterable<Topic> getAllTopics() {
    return new TopicIterable(this);
  }

  TopicImpl createTopic(TopicModelImpl model) {
    try {
      return updateValues(model, null).instantiate();
    } catch (Exception e) {
      throw new RuntimeException("Creating topic failed, model=" + model, e);
    }
  }

  TopicImpl _createTopic(TopicModelImpl model) {
    return _createTopic(model, null);
  }

  /**
     * Creates a new topic in the DB.
     * No child topics are created.
     */
  private TopicImpl _createTopic(TopicModelImpl model, String uriPrefix) {
    try {
      em.fireEvent(CoreEvent.PRE_CREATE_TOPIC, model);
      model.preCreate();
      storeTopic(model);
      if (model.getType().isSimple()) {
        model.storeSimpleValue();
      }
      createTopicInstantiation(model.getId(), model.getTypeUri());
      if (uriPrefix != null && model.getUri().equals("")) {
        model.updateUri(uriPrefix + model.getId());
      }
      TopicImpl topic = model.instantiate();
      model.postCreate();
      em.fireEvent(CoreEvent.POST_CREATE_TOPIC, topic);
      return topic;
    } catch (Exception e) {
      throw new RuntimeException("Creating topic failed, model=" + model + ", uriPrefix=" + uriPrefix, e);
    }
  }

  void updateTopic(TopicModelImpl updateModel) {
    try {
      TopicModelImpl model = fetchTopic(updateModel.getId());
      updateTopic(model, updateModel);
      em.fireEvent(CoreEvent.POST_UPDATE_TOPIC_REQUEST, model.instantiate());
    } catch (Exception e) {
      throw new RuntimeException("Fetching and updating topic " + updateModel.getId() + " failed", e);
    }
  }

  void updateTopic(TopicModelImpl topic, TopicModelImpl updateModel) {
    try {
      topic.checkWriteAccess();
      topic.update(updateModel);
    } catch (Exception e) {
      throw new RuntimeException("Updating topic " + topic.getId() + " failed", e);
    }
  }

  /**
     * Convenience.
     */
  void deleteTopic(long topicId) {
    try {
      deleteTopic(fetchTopic(topicId));
    } catch (Exception e) {
      throw new RuntimeException("Fetching and deleting topic " + topicId + " failed", e);
    }
  }

  void deleteTopic(TopicModelImpl topic) {
    try {
      topic.checkWriteAccess();
      topic.delete();
    } catch (Exception e) {
      throw new RuntimeException("Deleting topic " + topic.getId() + " failed", e);
    }
  }

  Association getAssociation(long assocId) {
    try {
      return checkReadAccessAndInstantiate(fetchAssociation(assocId));
    } catch (Exception e) {
      throw new RuntimeException("Fetching association " + assocId + " failed", e);
    }
  }

  Association getAssociationByValue(String key, SimpleValue value) {
    try {
      AssociationModelImpl assoc = fetchAssociation(key, value);
      return assoc != null ? this.<Association>checkReadAccessAndInstantiate(assoc) : null;
    } catch (Exception e) {
      throw new RuntimeException("Fetching association failed (key=\"" + key + "\", value=\"" + value + "\")", e);
    }
  }

  List<Association> getAssociationsByValue(String key, SimpleValue value) {
    try {
      return checkReadAccessAndInstantiate(fetchAssociations(key, value));
    } catch (Exception e) {
      throw new RuntimeException("Fetching associationss failed (key=\"" + key + "\", value=\"" + value + "\")", e);
    }
  }

  Association getAssociation(String assocTypeUri, long topic1Id, long topic2Id, String roleTypeUri1, String roleTypeUri2) {
    String info = "assocTypeUri=\"" + assocTypeUri + "\", topic1Id=" + topic1Id + ", topic2Id=" + topic2Id + ", roleTypeUri1=\"" + roleTypeUri1 + "\", roleTypeUri2=\"" + roleTypeUri2 + "\"";
    try {
      AssociationModelImpl assoc = fetchAssociation(assocTypeUri, topic1Id, topic2Id, roleTypeUri1, roleTypeUri2);
      return assoc != null ? this.<Association>checkReadAccessAndInstantiate(assoc) : null;
    } catch (Exception e) {
      throw new RuntimeException("Fetching association failed (" + info + ")", e);
    }
  }

  Association getAssociationBetweenTopicAndAssociation(String assocTypeUri, long topicId, long assocId, String topicRoleTypeUri, String assocRoleTypeUri) {
    String info = "assocTypeUri=\"" + assocTypeUri + "\", topicId=" + topicId + ", assocId=" + assocId + ", topicRoleTypeUri=\"" + topicRoleTypeUri + "\", assocRoleTypeUri=\"" + assocRoleTypeUri + "\"";
    logger.info(info);
    try {
      AssociationModelImpl assoc = fetchAssociationBetweenTopicAndAssociation(assocTypeUri, topicId, assocId, topicRoleTypeUri, assocRoleTypeUri);
      return assoc != null ? this.<Association>checkReadAccessAndInstantiate(assoc) : null;
    } catch (Exception e) {
      throw new RuntimeException("Fetching association failed (" + info + ")", e);
    }
  }

  List<Association> getAssociationsByType(String assocTypeUri) {
    try {
      return checkReadAccessAndInstantiate(_getAssociationType(assocTypeUri).getAllInstances());
    } catch (Exception e) {
      throw new RuntimeException("Fetching associations by type failed (assocTypeUri=\"" + assocTypeUri + "\")", e);
    }
  }

  List<Association> getAssociations(long topic1Id, long topic2Id) {
    return getAssociations(null, topic1Id, topic2Id);
  }

  List<Association> getAssociations(String assocTypeUri, long topic1Id, long topic2Id) {
    return getAssociations(assocTypeUri, topic1Id, topic2Id, null, null);
  }

  List<Association> getAssociations(String assocTypeUri, long topic1Id, long topic2Id, String roleTypeUri1, String roleTypeUri2) {
    return instantiate(_getAssociations(assocTypeUri, topic1Id, topic2Id, roleTypeUri1, roleTypeUri2));
  }

  /**
     * Fetches from DB and filters READables. No instantiation.
     *
     * ### TODO: drop this. Use the new traversal methods instead.
     */
  Iterable<AssociationModelImpl> _getAssociations(String assocTypeUri, long topic1Id, long topic2Id, String roleTypeUri1, String roleTypeUri2) {
    logger.fine("assocTypeUri=\"" + assocTypeUri + "\", topic1Id=" + topic1Id + ", topic2Id=" + topic2Id + ", roleTypeUri1=\"" + roleTypeUri1 + "\", roleTypeUri2=\"" + roleTypeUri2 + "\"");
    try {
      return filterReadables(fetchAssociations(assocTypeUri, topic1Id, topic2Id, roleTypeUri1, roleTypeUri2));
    } catch (Exception e) {
      throw new RuntimeException("Fetching associations between topics " + topic1Id + " and " + topic2Id + " failed (assocTypeUri=\"" + assocTypeUri + "\", roleTypeUri1=\"" + roleTypeUri1 + "\", roleTypeUri2=\"" + roleTypeUri2 + "\")", e);
    }
  }

  Iterable<Association> getAllAssociations() {
    return new AssociationIterable(this);
  }

  long[] getPlayerIds(long assocId) {
    return fetchPlayerIds(assocId);
  }

  /**
     * Convenience.
     */
  AssociationImpl createAssociation(String typeUri, RoleModel roleModel1, RoleModel roleModel2) {
    return createAssociation(mf.newAssociationModel(typeUri, roleModel1, roleModel2));
  }

  /**
     * Creates a new association in the DB.
     */
  AssociationImpl createAssociation(AssociationModelImpl model) {
    try {
      em.fireEvent(CoreEvent.PRE_CREATE_ASSOCIATION, model);
      model.preCreate();
      storeAssociation(model);
      updateValues(model, null);
      createAssociationInstantiation(model.getId(), model.getTypeUri());
      AssociationImpl assoc = model.instantiate();
      model.postCreate();
      em.fireEvent(CoreEvent.POST_CREATE_ASSOCIATION, assoc);
      return assoc;
    } catch (Exception e) {
      throw new RuntimeException("Creating association failed, model=" + model, e);
    }
  }

  void updateAssociation(AssociationModelImpl updateModel) {
    try {
      AssociationModelImpl model = fetchAssociation(updateModel.getId());
      updateAssociation(model, updateModel);
    } catch (Exception e) {
      throw new RuntimeException("Fetching and updating association " + updateModel.getId() + " failed", e);
    }
  }

  void updateAssociation(AssociationModelImpl assoc, AssociationModelImpl updateModel) {
    try {
      checkAssociationWriteAccess(assoc.getId());
      assoc.update(updateModel);
    } catch (Exception e) {
      throw new RuntimeException("Updating association " + assoc.getId() + " failed, assoc=" + assoc + ", updateModel=" + updateModel, e);
    }
  }

  /**
     * Convenience.
     */
  void deleteAssociation(long assocId) {
    try {
      deleteAssociation(fetchAssociation(assocId));
    } catch (IllegalStateException e) {
      if (e.getMessage().equals("Node[" + assocId + "] has been deleted in this tx")) {
        logger.info("### Association " + assocId + " has already been deleted in this transaction. " + "This can happen while delete-multi.");
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw new RuntimeException("Fetching and deleting association " + assocId + " failed", e);
    }
  }

  void deleteAssociation(AssociationModelImpl assoc) {
    try {
      checkAssociationWriteAccess(assoc.getId());
      assoc.delete();
    } catch (Exception e) {
      throw new RuntimeException("Deleting association " + assoc.getId() + " failed", e);
    }
  }

  void createTopicInstantiation(long topicId, String topicTypeUri) {
    try {
      AssociationModel assoc = mf.newAssociationModel("dm4.core.instantiation", mf.newTopicRoleModel(topicTypeUri, "dm4.core.type"), mf.newTopicRoleModel(topicId, "dm4.core.instance"));
      storeAssociation(assoc);
      storeAssociationValue(assoc.getId(), assoc.getSimpleValue());
      createAssociationInstantiation(assoc.getId(), assoc.getTypeUri());
    } catch (Exception e) {
      throw new RuntimeException("Associating topic " + topicId + " with topic type \"" + topicTypeUri + "\" failed", e);
    }
  }

  void createAssociationInstantiation(long assocId, String assocTypeUri) {
    try {
      AssociationModel assoc = mf.newAssociationModel("dm4.core.instantiation", mf.newTopicRoleModel(assocTypeUri, "dm4.core.type"), mf.newAssociationRoleModel(assocId, "dm4.core.instance"));
      storeAssociation(assoc);
      storeAssociationValue(assoc.getId(), assoc.getSimpleValue());
    } catch (Exception e) {
      throw new RuntimeException("Associating association " + assocId + " with association type \"" + assocTypeUri + "\" failed", e);
    }
  }

  TopicTypeImpl getTopicType(String uri) {
    return checkReadAccessAndInstantiate(_getTopicType(uri));
  }

  TopicTypeImpl getTopicTypeImplicitly(long topicId) {
    checkTopicReadAccess(topicId);
    return _getTopicType(typeUri(topicId)).instantiate();
  }

  AssociationTypeImpl getAssociationType(String uri) {
    return checkReadAccessAndInstantiate(_getAssociationType(uri));
  }

  AssociationTypeImpl getAssociationTypeImplicitly(long assocId) {
    checkAssociationReadAccess(assocId);
    return _getAssociationType(typeUri(assocId)).instantiate();
  }

  List<TopicType> getAllTopicTypes() {
    try {
      List<TopicType> topicTypes = new ArrayList();
      for (String uri : getTopicTypeUris()) {
        topicTypes.add(_getTopicType(uri).instantiate());
      }
      return topicTypes;
    } catch (Exception e) {
      throw new RuntimeException("Fetching all topic types failed", e);
    }
  }

  List<AssociationType> getAllAssociationTypes() {
    try {
      List<AssociationType> assocTypes = new ArrayList();
      for (String uri : getAssociationTypeUris()) {
        assocTypes.add(_getAssociationType(uri).instantiate());
      }
      return assocTypes;
    } catch (Exception e) {
      throw new RuntimeException("Fetching all association types failed", e);
    }
  }

  TopicTypeImpl createTopicType(TopicTypeModelImpl model) {
    try {
      em.fireEvent(CoreEvent.PRE_CREATE_TOPIC_TYPE, model);
      createType(model, URI_PREFIX_TOPIC_TYPE);
      TopicTypeImpl topicType = model.instantiate();
      em.fireEvent(CoreEvent.INTRODUCE_TOPIC_TYPE, topicType);
      return topicType;
    } catch (Exception e) {
      throw new RuntimeException("Creating topic type \"" + model.getUri() + "\" failed", e);
    }
  }

  AssociationTypeImpl createAssociationType(AssociationTypeModelImpl model) {
    try {
      em.fireEvent(CoreEvent.PRE_CREATE_ASSOCIATION_TYPE, model);
      createType(model, URI_PREFIX_ASSOCIATION_TYPE);
      AssociationTypeImpl assocType = model.instantiate();
      em.fireEvent(CoreEvent.INTRODUCE_ASSOCIATION_TYPE, assocType);
      return assocType;
    } catch (Exception e) {
      throw new RuntimeException("Creating association type \"" + model.getUri() + "\" failed", e);
    }
  }

  void updateTopicType(TopicTypeModelImpl updateModel) {
    try {
      TopicModelImpl topic = fetchTopic(updateModel.getId());
      topic.checkWriteAccess();
      _getTopicType(topic.getUri()).update(updateModel);
    } catch (Exception e) {
      throw new RuntimeException("Updating topic type failed, updateModel=" + updateModel, e);
    }
  }

  void updateAssociationType(AssociationTypeModelImpl updateModel) {
    try {
      TopicModelImpl topic = fetchTopic(updateModel.getId());
      topic.checkWriteAccess();
      _getAssociationType(topic.getUri()).update(updateModel);
    } catch (Exception e) {
      throw new RuntimeException("Updating association type failed, updateModel=" + updateModel, e);
    }
  }

  void deleteTopicType(String topicTypeUri) {
    try {
      TypeModelImpl type = _getTopicType(topicTypeUri);
      type.checkWriteAccess();
      type.delete();
    } catch (Exception e) {
      throw new RuntimeException("Deleting topic type \"" + topicTypeUri + "\" failed", e);
    }
  }

  void deleteAssociationType(String assocTypeUri) {
    try {
      TypeModelImpl type = _getAssociationType(assocTypeUri);
      type.checkWriteAccess();
      type.delete();
    } catch (Exception e) {
      throw new RuntimeException("Deleting association type \"" + assocTypeUri + "\" failed", e);
    }
  }

  Topic createRoleType(TopicModelImpl model) {
    String typeUri = model.getTypeUri();
    if (typeUri == null) {
      model.setTypeUri("dm4.core.role_type");
    } else {
      if (!typeUri.equals("dm4.core.role_type")) {
        throw new IllegalArgumentException("A role type is supposed to be of type \"dm4.core.role_type\" " + "(found: \"" + typeUri + "\")");
      }
    }
    return _createTopic(model, URI_PREFIX_ROLE_TYPE);
  }

  TopicTypeModelImpl _getTopicType(String uri) {
    return typeStorage.getTopicType(uri);
  }

  AssociationTypeModelImpl _getAssociationType(String uri) {
    return typeStorage.getAssociationType(uri);
  }

  DeepaMehtaObject getObject(long id) {
    return checkReadAccessAndInstantiate(fetchObject(id));
  }

  List<RelatedTopicModelImpl> getTopicRelatedTopics(long topicId, String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri) {
    return filterReadables(fetchTopicRelatedTopics(topicId, assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri));
  }

  List<RelatedTopicModelImpl> getTopicRelatedTopics(long topicId, List<String> assocTypeUris, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri) {
    return filterReadables(fetchTopicRelatedTopics(topicId, assocTypeUris, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri));
  }

  RelatedAssociationModelImpl getTopicRelatedAssociation(long topicId, String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersAssocTypeUri) {
    RelatedAssociationModelImpl assoc = fetchTopicRelatedAssociation(topicId, assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersAssocTypeUri);
    return assoc != null ? checkReadAccess(assoc) : null;
  }

  List<RelatedAssociationModelImpl> getTopicRelatedAssociations(long topicId, String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersAssocTypeUri) {
    return filterReadables(fetchTopicRelatedAssociations(topicId, assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersAssocTypeUri));
  }

  List<AssociationModelImpl> getTopicAssociations(long topicId) {
    return filterReadables(fetchTopicAssociations(topicId));
  }

  List<RelatedTopicModelImpl> getAssociationRelatedTopics(long assocId, String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri) {
    return filterReadables(fetchAssociationRelatedTopics(assocId, assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri));
  }

  List<RelatedTopicModelImpl> getAssociationRelatedTopics(long assocId, List<String> assocTypeUris, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri) {
    return filterReadables(fetchAssociationRelatedTopics(assocId, assocTypeUris, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri));
  }

  RelatedAssociationModelImpl getAssociationRelatedAssociation(long assocId, String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersAssocTypeUri) {
    RelatedAssociationModelImpl assoc = fetchAssociationRelatedAssociation(assocId, assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersAssocTypeUri);
    return assoc != null ? checkReadAccess(assoc) : null;
  }

  List<RelatedAssociationModelImpl> getAssociationRelatedAssociations(long assocId, String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersAssocTypeUri) {
    return filterReadables(fetchAssociationRelatedAssociations(assocId, assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersAssocTypeUri));
  }

  List<AssociationModelImpl> getAssociationAssociations(long assocId) {
    return filterReadables(fetchAssociationAssociations(assocId));
  }

  RelatedTopicModelImpl getRelatedTopic(long objectId, String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri) {
    RelatedTopicModelImpl topic = fetchRelatedTopic(objectId, assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri);
    return topic != null ? checkReadAccess(topic) : null;
  }

  List<RelatedTopicModelImpl> getRelatedTopics(long objectId, String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri) {
    return filterReadables(fetchRelatedTopics(objectId, assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri));
  }

  List<Topic> getTopicsByProperty(String propUri, Object propValue) {
    return checkReadAccessAndInstantiate(fetchTopicsByProperty(propUri, propValue));
  }

  List<Topic> getTopicsByPropertyRange(String propUri, Number from, Number to) {
    return checkReadAccessAndInstantiate(fetchTopicsByPropertyRange(propUri, from, to));
  }

  List<Association> getAssociationsByProperty(String propUri, Object propValue) {
    return checkReadAccessAndInstantiate(fetchAssociationsByProperty(propUri, propValue));
  }

  List<Association> getAssociationsByPropertyRange(String propUri, Number from, Number to) {
    return checkReadAccessAndInstantiate(fetchAssociationsByPropertyRange(propUri, from, to));
  }

  <O extends java.lang.Object> O checkReadAccessAndInstantiate(DeepaMehtaObjectModelImpl model) {
    return (O) checkReadAccess(model).instantiate();
  }

  <O extends java.lang.Object> List<O> checkReadAccessAndInstantiate(List<? extends DeepaMehtaObjectModelImpl> models) {
    return instantiate(filterReadables(models));
  }

  private <M extends DeepaMehtaObjectModelImpl> List<M> filterReadables(List<M> models) {
    Iterator<? extends DeepaMehtaObjectModelImpl> i = models.iterator();
    while (i.hasNext()) {
      if (!hasReadAccess(i.next())) {
        i.remove();
      }
    }
    return models;
  }

  boolean hasReadAccess(DeepaMehtaObjectModelImpl model) {
    try {
      checkReadAccess(model);
      return true;
    } catch (AccessControlException e) {
      return false;
    }
  }

  <M extends DeepaMehtaObjectModelImpl> M checkReadAccess(M model) {
    model.checkReadAccess();
    return model;
  }

  void checkTopicReadAccess(long topicId) {
    em.fireEvent(CoreEvent.CHECK_TOPIC_READ_ACCESS, topicId);
  }

  void checkAssociationReadAccess(long assocId) {
    em.fireEvent(CoreEvent.CHECK_ASSOCIATION_READ_ACCESS, assocId);
  }

  void checkTopicWriteAccess(long topicId) {
    em.fireEvent(CoreEvent.CHECK_TOPIC_WRITE_ACCESS, topicId);
  }

  void checkAssociationWriteAccess(long assocId) {
    em.fireEvent(CoreEvent.CHECK_ASSOCIATION_WRITE_ACCESS, assocId);
  }

  <O extends java.lang.Object> List<O> instantiate(Iterable<? extends DeepaMehtaObjectModelImpl> models) {
    List<O> objects = new ArrayList();
    for (DeepaMehtaObjectModelImpl model : models) {
      objects.add((O) model.instantiate());
    }
    return objects;
  }

  private List<String> getTopicTypeUris() {
    try {
      List<String> topicTypeUris = new ArrayList();
      topicTypeUris.add("dm4.core.topic_type");
      topicTypeUris.add("dm4.core.assoc_type");
      topicTypeUris.add("dm4.core.meta_type");
      for (TopicModel topicType : filterReadables(fetchTopics("typeUri", new SimpleValue("dm4.core.topic_type")))) {
        topicTypeUris.add(topicType.getUri());
      }
      return topicTypeUris;
    } catch (Exception e) {
      throw new RuntimeException("Fetching list of topic type URIs failed", e);
    }
  }

  private List<String> getAssociationTypeUris() {
    try {
      List<String> assocTypeUris = new ArrayList();
      for (TopicModel assocType : filterReadables(fetchTopics("typeUri", new SimpleValue("dm4.core.assoc_type")))) {
        assocTypeUris.add(assocType.getUri());
      }
      return assocTypeUris;
    } catch (Exception e) {
      throw new RuntimeException("Fetching list of association type URIs failed", e);
    }
  }

  private void createType(TypeModelImpl model, String uriPrefix) {
    TopicModelImpl typeTopic = mf.newTopicModel(model);
    _createTopic(typeTopic, uriPrefix);
    model.id = typeTopic.id;
    model.uri = typeTopic.uri;
    typeStorage.storeType(model);
  }

  private String typeUri(long objectId) {
    return (String) fetchProperty(objectId, "typeUri");
  }

  private void bootstrapTypeCache() {
    TopicTypeModelImpl metaMetaType = mf.newTopicTypeModel("dm4.core.meta_meta_type", "Meta Meta Type", "dm4.core.text");
    metaMetaType.setTypeUri("dm4.core.meta_meta_meta_type");
    typeStorage.putInTypeCache(metaMetaType);
  }

  private <M extends DeepaMehtaObjectModelImpl> M updateValues(M updateModel, M targetObject) {
    M value = new ValueUpdater(this).update(updateModel, targetObject).value;
    if (value == null) {
      throw new RuntimeException("ValueUpdater yields no result");
    }
    return value;
  }
}