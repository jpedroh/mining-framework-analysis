package de.deepamehta.core.impl;
import de.deepamehta.core.Association;
import de.deepamehta.core.RelatedAssociation;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.model.RelatedAssociationModel;
import de.deepamehta.core.model.RelatedTopicModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.service.Directive;
import de.deepamehta.core.service.Directives;
import de.deepamehta.core.service.ResultList;
import de.deepamehta.core.storage.spi.DeepaMehtaTransaction;
import java.util.List;
import java.util.logging.Logger;

/**
 * A topic that is attached to the {@link DeepaMehtaService}.
 */
class AttachedTopic extends AttachedDeepaMehtaObject implements Topic {
  private Logger logger = Logger.getLogger(getClass().getName());

  AttachedTopic(TopicModel model, EmbeddedService dms) {
    super(model, dms);
  }

  @Override public void update(TopicModel model, Directives directives) {
    _update(model, directives);
    dms.fireEvent(CoreEvent.POST_UPDATE_TOPIC_REQUEST, this);
  }

  @Override public void delete(Directives directives) {
    DeepaMehtaTransaction tx = dms.beginTx();
    try {
      dms.fireEvent(CoreEvent.PRE_DELETE_TOPIC, this, directives);
      super.delete(directives);
      logger.info("Deleting " + this);
      directives.add(Directive.DELETE_TOPIC, this);
      dms.storageDecorator.deleteTopic(getId());
      tx.success();
      dms.fireEvent(CoreEvent.POST_DELETE_TOPIC, this, directives);
    } catch (Exception e) {
      logger.warning("ROLLBACK!");
      throw new RuntimeException("Deleting topic failed (" + this + ")", e);
    } finally {
      tx.finish();
    }
  }

  @Override public TopicModel getModel() {
    return (TopicModel) super.getModel();
  }

  @Override public RelatedAssociation getRelatedAssociation(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersAssocTypeUri, boolean fetchComposite, boolean fetchRelatingComposite) {
    RelatedAssociationModel assoc = dms.storageDecorator.fetchTopicRelatedAssociation(getId(), assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersAssocTypeUri);
    return assoc != null ? dms.instantiateRelatedAssociation(assoc, fetchComposite, fetchRelatingComposite, true) : null;
  }

  @Override public List<RelatedAssociation> getRelatedAssociations(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersAssocTypeUri, boolean fetchComposite, boolean fetchRelatingComposite) {
    List<RelatedAssociationModel> assocs = dms.storageDecorator.fetchTopicRelatedAssociations(getId(), assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersAssocTypeUri);
    return dms.instantiateRelatedAssociations(assocs, fetchComposite, fetchRelatingComposite);
  }

  @Override public ResultList<RelatedTopic> getRelatedTopics(List assocTypeUris, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri, boolean fetchComposite, boolean fetchRelatingComposite, int maxResultSize) {
    ResultList<RelatedTopicModel> topics = dms.storageDecorator.fetchTopicRelatedTopics(getId(), assocTypeUris, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri, maxResultSize);
    return dms.instantiateRelatedTopics(topics, fetchComposite, fetchRelatingComposite);
  }

  @Override public Association getAssociation(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, long othersTopicId) {
    AssociationModel assoc = dms.storageDecorator.fetchAssociation(assocTypeUri, getId(), othersTopicId, myRoleTypeUri, othersRoleTypeUri);
    return assoc != null ? dms.instantiateAssociation(assoc, false, true) : null;
  }

  @Override public List<Association> getAssociations() {
    return dms.instantiateAssociations(dms.storageDecorator.fetchTopicAssociations(getId()), false);
  }

  @Override public void setProperty(String propUri, Object propValue, boolean addToIndex) {
    dms.storageDecorator.storeTopicProperty(getId(), propUri, propValue, addToIndex);
  }

  @Override public void removeProperty(String propUri) {
    dms.storageDecorator.removeTopicProperty(getId(), propUri);
  }

  /**
     * Convenience method.
     */
  TopicType getTopicType() {
    return (TopicType) getType();
  }

  /**
     * Low-level update method which does not fire the POST_UPDATE_TOPIC_REQUEST event.
     * <p>
     * Called multiple times while updating a composite value (see AttachedCompositeValue).
     * POST_UPDATE_TOPIC_REQUEST on the other hand must be fired only once (per update request).
     */
  void _update(TopicModel model, Directives directives) {
    logger.info("Updating topic " + getId() + " (new " + model + ")");
    dms.fireEvent(CoreEvent.PRE_UPDATE_TOPIC, this, model, directives);
    TopicModel oldModel = getModel().clone();
    super.update(model, directives);
    addUpdateDirective(directives);
    dms.fireEvent(CoreEvent.POST_UPDATE_TOPIC, this, model, oldModel, directives);
  }

  @Override String className() {
    return "topic";
  }

  @Override void addUpdateDirective(Directives directives) {
    directives.add(Directive.UPDATE_TOPIC, this);
  }

  @Override final void storeUri() {
    dms.storageDecorator.storeTopicUri(getId(), getUri());
  }

  @Override final void storeTypeUri() {
    reassignInstantiation();
    dms.storageDecorator.storeTopicTypeUri(getId(), getTypeUri());
  }

  @Override final RelatedTopicModel fetchRelatedTopic(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri) {
    return dms.storageDecorator.fetchTopicRelatedTopic(getId(), assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri);
  }

  @Override final ResultList<RelatedTopicModel> fetchRelatedTopics(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri, int maxResultSize) {
    return dms.storageDecorator.fetchTopicRelatedTopics(getId(), assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri, maxResultSize);
  }

  private void reassignInstantiation() {
    fetchInstantiation().delete(new Directives());
    dms.createTopicInstantiation(getId(), getTypeUri());
  }

  private Association fetchInstantiation() {
    RelatedTopic topicType = getRelatedTopic("dm4.core.instantiation", "dm4.core.instance", "dm4.core.type", "dm4.core.topic_type", false, false);
    if (topicType == null) {
      throw new RuntimeException("Topic " + getId() + " is not associated to a topic type");
    }
    return topicType.getRelatingAssociation();
  }
}