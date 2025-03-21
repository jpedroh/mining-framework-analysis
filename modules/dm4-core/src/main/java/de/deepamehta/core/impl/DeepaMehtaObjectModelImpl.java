package de.deepamehta.core.impl;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.model.AssociationDefinitionModel;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.model.ChildTopicsModel;
import de.deepamehta.core.model.DeepaMehtaObjectModel;
import de.deepamehta.core.model.IndexMode;
import de.deepamehta.core.model.RelatedTopicModel;
import de.deepamehta.core.model.RoleModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.model.TopicDeletionModel;
import de.deepamehta.core.model.TopicReferenceModel;
import de.deepamehta.core.model.TypeModel;
import de.deepamehta.core.service.DeepaMehtaEvent;
import de.deepamehta.core.service.Directive;
import de.deepamehta.core.service.Directives;
import de.deepamehta.core.util.JavaUtils;
import org.codehaus.jettison.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

class DeepaMehtaObjectModelImpl implements DeepaMehtaObjectModel {
  private static final String LABEL_CHILD_SEPARATOR = " ";

  private static final String LABEL_TOPIC_SEPARATOR = ", ";

  long id;

  String uri;

  String typeUri;

  SimpleValue value;

  ChildTopicsModelImpl childTopics;

  PersistenceLayer pl;

  EventManager em;

  ModelFactoryImpl mf;

  Logger logger = Logger.getLogger(getClass().getName());

  DeepaMehtaObjectModelImpl(long id, String uri, String typeUri, SimpleValue value, ChildTopicsModelImpl childTopics, PersistenceLayer pl) {
    this.id = id;
    this.uri = uri;
    this.typeUri = typeUri;
    this.value = value;
    this.childTopics = childTopics != null ? childTopics : pl.mf.newChildTopicsModel();
    this.pl = pl;
    this.em = pl.em;
    this.mf = pl.mf;
  }

  DeepaMehtaObjectModelImpl(DeepaMehtaObjectModelImpl object) {
    this(object.getId(), object.getUri(), object.getTypeUri(), object.getSimpleValue(), object.getChildTopicsModel(), object.pl);
  }

  @Override public long getId() {
    return id;
  }

  @Override public void setId(long id) {
    this.id = id;
  }

  @Override public String getUri() {
    return uri;
  }

  @Override public void setUri(String uri) {
    this.uri = uri;
  }

  @Override public String getTypeUri() {
    return typeUri;
  }

  @Override public void setTypeUri(String typeUri) {
    this.typeUri = typeUri;
  }

  @Override public SimpleValue getSimpleValue() {
    return value;
  }

  @Override public void setSimpleValue(String value) {
    setSimpleValue(new SimpleValue(value));
  }

  @Override public void setSimpleValue(int value) {
    setSimpleValue(new SimpleValue(value));
  }

  @Override public void setSimpleValue(long value) {
    setSimpleValue(new SimpleValue(value));
  }

  @Override public void setSimpleValue(boolean value) {
    setSimpleValue(new SimpleValue(value));
  }

  @Override public void setSimpleValue(SimpleValue value) {
    this.value = value;
  }

  @Override public ChildTopicsModelImpl getChildTopicsModel() {
    return childTopics;
  }

  @Override public void setChildTopicsModel(ChildTopicsModel childTopics) {
    this.childTopics = (ChildTopicsModelImpl) childTopics;
  }

  @Override public void set(DeepaMehtaObjectModel object) {
    setId(object.getId());
    setUri(object.getUri());
    setTypeUri(object.getTypeUri());
    setSimpleValue(object.getSimpleValue());
    setChildTopicsModel(object.getChildTopicsModel());
  }

  @Override public RoleModel createRoleModel(String roleTypeUri) {
    throw new RuntimeException("Not implemented");
  }

  @Override public JSONObject toJSON() {
    try {
      setDefaults();
      return new JSONObject().put("id", id).put("uri", uri).put("typeUri", typeUri).put("value", value.value()).put("childs", childTopics.toJSON());
    } catch (Exception e) {
      throw new RuntimeException("Serialization failed", e);
    }
  }

  @Override public DeepaMehtaObjectModel clone() {
    try {
      DeepaMehtaObjectModel object = (DeepaMehtaObjectModel) super.clone();
      object.setChildTopicsModel(childTopics.clone());
      return object;
    } catch (Exception e) {
      throw new RuntimeException("Cloning a DeepaMehtaObjectModel failed", e);
    }
  }

  @Override public boolean equals(Object o) {
    return ((DeepaMehtaObjectModel) o).getId() == id;
  }

  @Override public int hashCode() {
    return ((Long) id).hashCode();
  }

  @Override public String toString() {
    try {
      return getClass().getSimpleName() + " " + toJSON().toString(4);
    } catch (Exception e) {
      throw new RuntimeException("Prettyprinting failed", e);
    }
  }

  String className() {
    throw new UnsupportedOperationException();
  }

  DeepaMehtaObject instantiate() {
    throw new UnsupportedOperationException();
  }

  DeepaMehtaObjectModelImpl createModelWithChildTopics(ChildTopicsModel childTopics) {
    throw new UnsupportedOperationException();
  }

  TypeModelImpl getType() {
    throw new UnsupportedOperationException();
  }

  List<AssociationModelImpl> getAssociations() {
    throw new UnsupportedOperationException();
  }

  RelatedTopicModelImpl getRelatedTopic(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri) {
    throw new UnsupportedOperationException();
  }

  List<RelatedTopicModelImpl> getRelatedTopics(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri) {
    throw new UnsupportedOperationException();
  }

  List<RelatedTopicModelImpl> getRelatedTopics(List assocTypeUris, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri) {
    throw new UnsupportedOperationException();
  }

  void storeUri() {
    throw new UnsupportedOperationException();
  }

  void storeTypeUri() {
    throw new UnsupportedOperationException();
  }

  /**
     * Stores and indexes the simple value of this object model.
     * Determines the index key and index modes.
     */
  void storeSimpleValue() {
    throw new UnsupportedOperationException();
  }

  /**
     * Indexes the simple value of the given object model according to the given index mode.
     * <p>
     * Called to index existing topics/associations once an index mode has been added to a type definition.
     */
  void indexSimpleValue(IndexMode indexMode) {
    throw new UnsupportedOperationException();
  }

  void storeProperty(String propUri, Object propValue, boolean addToIndex) {
    throw new UnsupportedOperationException();
  }

  void removeProperty(String propUri) {
    throw new UnsupportedOperationException();
  }

  void _delete() {
    throw new UnsupportedOperationException();
  }

  /**
     * @throws  AccessControlException
     */
  void checkReadAccess() {
    throw new UnsupportedOperationException();
  }

  /**
     * @throws  AccessControlException
     */
  void checkWriteAccess() {
    throw new UnsupportedOperationException();
  }

  DeepaMehtaEvent getPreUpdateEvent() {
    throw new UnsupportedOperationException();
  }

  DeepaMehtaEvent getPostUpdateEvent() {
    throw new UnsupportedOperationException();
  }

  DeepaMehtaEvent getPreDeleteEvent() {
    throw new UnsupportedOperationException();
  }

  DeepaMehtaEvent getPostDeleteEvent() {
    throw new UnsupportedOperationException();
  }

  Directive getUpdateDirective() {
    throw new UnsupportedOperationException();
  }

  Directive getDeleteDirective() {
    throw new UnsupportedOperationException();
  }

  void preCreate() {
  }

  void postCreate() {
  }

  void preUpdate(DeepaMehtaObjectModel updateModel) {
  }

  void postUpdate(DeepaMehtaObjectModel updateModel, DeepaMehtaObjectModel oldObject) {
  }

  void preDelete() {
  }

  void postDelete() {
  }

  final void updateWithChildTopics(ChildTopicsModel childTopics) {
    update(createModelWithChildTopics(childTopics));
  }

  /**
     * @param   updateModel    The data to update.
     *              If the URI is <code>null</code> it is not updated.
     *              If the type URI is <code>null</code> it is not updated.
     *              If the simple value is <code>null</code> it is not updated.
     */
  final void update(DeepaMehtaObjectModelImpl updateModel) {
    try {
      logger.info("Updating " + objectInfo() + " (typeUri=\"" + typeUri + "\")");
      DeepaMehtaObjectModel oldObject = clone();
      em.fireEvent(getPreUpdateEvent(), instantiate(), updateModel);
      preUpdate(updateModel);
      _updateUri(updateModel.getUri());
      _updateTypeUri(updateModel.getTypeUri());
      new ValueUpdater(pl).update(updateModel, this);
      postUpdate(updateModel, oldObject);
      DeepaMehtaObject object = instantiate();
      Directives.get().add(getUpdateDirective(), object);
      em.fireEvent(getPostUpdateEvent(), object, updateModel, oldObject);
    } catch (Exception e) {
      throw new RuntimeException("Updating " + objectInfo() + " failed (typeUri=\"" + typeUri + "\")", e);
    }
  }

  final void updateUri(String uri) {
    setUri(uri);
    storeUri();
  }

  final void updateTypeUri(String typeUri) {
    setTypeUri(typeUri);
    storeTypeUri();
  }

  final void updateSimpleValue(SimpleValue value) {
    if (value == null) {
      throw new IllegalArgumentException("Tried to set a null SimpleValue (" + this + ")");
    }
    setSimpleValue(value);
    storeSimpleValue();
  }

  /**
     * Deletes this object's direct associations, and the object itself.
     */
  final void delete() {
    try {
      em.fireEvent(getPreDeleteEvent(), instantiate());
      preDelete();
      for (AssociationModelImpl assoc : getAssociations()) {
        assoc.delete();
      }
      logger.info("Deleting " + objectInfo() + " (typeUri=\"" + typeUri + "\")");
      _delete();
      postDelete();
      Directives.get().add(getDeleteDirective(), this);
      em.fireEvent(getPostDeleteEvent(), this);
    } catch (IllegalStateException e) {
      if (e.getMessage().equals("Node[" + id + "] has been deleted in this tx")) {
        logger.info("### Association " + id + " has already been deleted in this transaction. This can " + "happen while deleting a topic with associations A1 and A2 while A2 points to A1 (" + this + ")");
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw new RuntimeException("Deleting " + objectInfo() + " failed (typeUri=\"" + typeUri + "\")", e);
    }
  }

  final void _updateChildTopics(ChildTopicsModelImpl updateModel) {
    try {
      for (AssociationDefinitionModel assocDef : getType().getAssocDefs()) {
        String assocDefUri = assocDef.getAssocDefUri();
        String cardinalityUri = assocDef.getChildCardinalityUri();
        RelatedTopicModelImpl newChildTopic = null;
        List<RelatedTopicModelImpl> newChildTopics = null;
        if (cardinalityUri.equals("dm4.core.one")) {
          newChildTopic = updateModel.getTopicOrNull(assocDefUri);
          if (newChildTopic == null) {
            continue;
          }
        } else {
          if (cardinalityUri.equals("dm4.core.many")) {
            newChildTopics = updateModel.getTopicsOrNull(assocDefUri);
            if (newChildTopics == null) {
              continue;
            }
          } else {
            throw new RuntimeException("\"" + cardinalityUri + "\" is an unexpected cardinality URI");
          }
        }
        updateChildTopics(newChildTopic, newChildTopics, assocDef);
      }
      _calculateLabelAndUpdate();
    } catch (Exception e) {
      throw new RuntimeException("Updating the child topics of " + objectInfo() + " failed", e);
    }
  }

  final void updateChildTopics(RelatedTopicModelImpl newChildTopic, List<RelatedTopicModelImpl> newChildTopics, AssociationDefinitionModel assocDef) {
    loadChildTopics(assocDef);
    String assocTypeUri = assocDef.getTypeUri();
    boolean one = newChildTopic != null;
    if (assocTypeUri.equals("dm4.core.composition_def")) {
      if (one) {
        updateCompositionOne(newChildTopic, assocDef);
      } else {
        updateCompositionMany(newChildTopics, assocDef);
      }
    } else {
      if (assocTypeUri.equals("dm4.core.aggregation_def")) {
        if (one) {
          updateAggregationOne(newChildTopic, assocDef);
        } else {
          updateAggregationMany(newChildTopics, assocDef);
        }
      } else {
        throw new RuntimeException("Association type \"" + assocTypeUri + "\" not supported");
      }
    }
  }

  /**
     * Loads the child topics which are not loaded already.
     */
  final DeepaMehtaObjectModel loadChildTopics() {
    for (AssociationDefinitionModel assocDef : getType().getAssocDefs()) {
      loadChildTopics(assocDef);
    }
    return this;
  }

  /**
     * Loads the child topics for the given assoc def, provided they are not loaded already.
     */
  final DeepaMehtaObjectModel loadChildTopics(String assocDefUri) {
    try {
      return loadChildTopics(getAssocDef(assocDefUri));
    } catch (Exception e) {
      throw new RuntimeException("Loading \"" + assocDefUri + "\" child topics of " + objectInfo() + " failed", e);
    }
  }

  /**
     * Calculates the simple value that is to be indexed for this object.
     *
     * HTML tags are stripped from HTML values. Non-HTML values are returned directly. ### FIXDOC
     */
  SimpleValue getIndexValue() {
    return value;
  }

  boolean uriChange(String newUri, String compareUri) {
    return newUri != null && !newUri.equals(compareUri);
  }

  boolean isSimple() {
    String dataTypeUri = getType().getDataTypeUri();
    return dataTypeUri.equals("dm4.core.text") || dataTypeUri.equals("dm4.core.html") || dataTypeUri.equals("dm4.core.number") || dataTypeUri.equals("dm4.core.boolean");
  }

  private void setDefaults() {
    if (getUri() == null) {
      setUri("");
    }
    if (getSimpleValue() == null) {
      setSimpleValue("");
    }
  }

  /**
     * Recursively loads child topics (model) and updates this attached object cache accordingly. ### FIXDOC
     * If the child topics are loaded already nothing is performed.
     *
     * @param   assocDef    the child topics according to this association definition are loaded.
     *                      <p>
     *                      Note: the association definition must not necessarily originate from the parent object's
     *                      type definition. It may originate from a facet definition as well.
     */
  DeepaMehtaObjectModel loadChildTopics(AssociationDefinitionModel assocDef) {
    String assocDefUri = assocDef.getAssocDefUri();
    if (!childTopics.has(assocDefUri)) {
      logger.fine("### Lazy-loading \"" + assocDefUri + "\" child topic(s) of " + objectInfo());
      pl.valueStorage.fetchChildTopics(this, assocDef);
    }
    return this;
  }

  private void _updateUri(String newUri) {
    if (uriChange(newUri, uri)) {
      logger.info("### Changing URI of " + objectInfo() + ": \"" + uri + "\" -> \"" + newUri + "\"");
      updateUri(newUri);
    }
  }

  private void _updateTypeUri(String newTypeUri) {
    if (newTypeUri != null && !newTypeUri.equals(typeUri)) {
      logger.info("### Changing type URI of " + objectInfo() + ": \"" + typeUri + "\" -> \"" + newTypeUri + "\"");
      updateTypeUri(newTypeUri);
    }
  }

  final void _updateSimpleValue(SimpleValue newValue) {
    if (newValue != null && !newValue.equals(value)) {
      logger.info("### Changing simple value of " + objectInfo() + ": \"" + value + "\" -> \"" + newValue + "\"");
      updateSimpleValue(newValue);
    }
  }

  private void updateCompositionOne(RelatedTopicModelImpl newChildTopic, AssociationDefinitionModel assocDef) {
    RelatedTopicModelImpl childTopic = childTopics.getTopicOrNull(assocDef.getAssocDefUri());
    if (newChildTopic instanceof TopicDeletionModel) {
      deleteChildTopicOne(childTopic, assocDef, true);
    } else {
      if (newChildTopic instanceof TopicReferenceModel) {
        createAssignmentOne(childTopic, (TopicReferenceModelImpl) newChildTopic, assocDef, true);
      } else {
        if (childTopic != null) {
          updateRelatedTopic(childTopic, newChildTopic);
        } else {
          createChildTopicOne(newChildTopic, assocDef);
        }
      }
    }
  }

  private void updateCompositionMany(List<RelatedTopicModelImpl> newChildTopics, AssociationDefinitionModel assocDef) {
    for (RelatedTopicModelImpl newChildTopic : newChildTopics) {
      long childTopicId = newChildTopic.getId();
      if (newChildTopic instanceof TopicDeletionModel) {
        deleteChildTopicMany(childTopicId, assocDef, true);
      } else {
        if (newChildTopic instanceof TopicReferenceModel) {
          createAssignmentMany((TopicReferenceModelImpl) newChildTopic, assocDef);
        } else {
          if (childTopicId != -1) {
            updateChildTopicMany(newChildTopic, assocDef);
          } else {
            createChildTopicMany(newChildTopic, assocDef);
          }
        }
      }
    }
  }

  private void updateAggregationOne(RelatedTopicModelImpl newChildTopic, AssociationDefinitionModel assocDef) {
    RelatedTopicModelImpl childTopic = childTopics.getTopicOrNull(assocDef.getAssocDefUri());
    if (newChildTopic instanceof TopicDeletionModel) {
      deleteChildTopicOne(childTopic, assocDef, false);
    } else {
      if (newChildTopic instanceof TopicReferenceModel) {
        createAssignmentOne(childTopic, (TopicReferenceModelImpl) newChildTopic, assocDef, false);
      } else {
        if (newChildTopic.getId() != -1) {
          updateChildTopicOne(newChildTopic, assocDef);
        } else {
          if (childTopic != null) {
            childTopic.getRelatingAssociation().delete();
          }
          createChildTopicOne(newChildTopic, assocDef);
        }
      }
    }
  }

  private void updateAggregationMany(List<RelatedTopicModelImpl> newChildTopics, AssociationDefinitionModel assocDef) {
    for (RelatedTopicModelImpl newChildTopic : newChildTopics) {
      long childTopicId = newChildTopic.getId();
      if (newChildTopic instanceof TopicDeletionModel) {
        deleteChildTopicMany(childTopicId, assocDef, false);
      } else {
        if (newChildTopic instanceof TopicReferenceModel) {
          createAssignmentMany((TopicReferenceModelImpl) newChildTopic, assocDef);
        } else {
          if (childTopicId != -1) {
            updateChildTopicMany(newChildTopic, assocDef);
          } else {
            createChildTopicMany(newChildTopic, assocDef);
          }
        }
      }
    }
  }

  private void updateChildTopicOne(RelatedTopicModelImpl newChildTopic, AssociationDefinitionModel assocDef) {
    RelatedTopicModelImpl childTopic = childTopics.getTopicOrNull(assocDef.getAssocDefUri());
    if (childTopic == null || childTopic.getId() != newChildTopic.getId()) {
      throw new RuntimeException("Topic " + newChildTopic.getId() + " is not a child of " + objectInfo() + " according to " + assocDef);
    }
    updateRelatedTopic(childTopic, newChildTopic);
  }

  private void updateChildTopicMany(RelatedTopicModelImpl newChildTopic, AssociationDefinitionModel assocDef) {
    RelatedTopicModelImpl childTopic = childTopics.findChildTopicById(newChildTopic.getId(), assocDef);
    if (childTopic == null) {
      throw new RuntimeException("Topic " + newChildTopic.getId() + " is not a child of " + objectInfo() + " according to " + assocDef);
    }
    updateRelatedTopic(childTopic, newChildTopic);
  }

  private void updateRelatedTopic(RelatedTopicModelImpl childTopic, RelatedTopicModelImpl newChildTopic) {
    childTopic.update(newChildTopic);
    updateRelatingAssociation(childTopic, newChildTopic);
  }

  private void updateRelatingAssociation(RelatedTopicModelImpl childTopic, RelatedTopicModelImpl newChildTopic) {
    childTopic.getRelatingAssociation().update(newChildTopic.getRelatingAssociation());
  }

  private void createChildTopicOne(RelatedTopicModelImpl newChildTopic, AssociationDefinitionModel assocDef) {
    createAndAssociateChildTopic(newChildTopic, assocDef);
    childTopics.putInChildTopics(newChildTopic, assocDef);
  }

  private void createChildTopicMany(RelatedTopicModelImpl newChildTopic, AssociationDefinitionModel assocDef) {
    createAndAssociateChildTopic(newChildTopic, assocDef);
    childTopics.addToChildTopics(newChildTopic, assocDef);
  }

  private void createAndAssociateChildTopic(RelatedTopicModelImpl childTopic, AssociationDefinitionModel assocDef) {
    pl.createTopic(childTopic);
    associateChildTopic(childTopic, assocDef);
  }

  private void createAssignmentOne(RelatedTopicModelImpl childTopic, TopicReferenceModelImpl newChildTopic, AssociationDefinitionModel assocDef, boolean deleteChildTopic) {
    if (childTopic != null) {
      if (newChildTopic.isReferingTo(childTopic)) {
        updateRelatingAssociation(childTopic, newChildTopic);
        return;
      }
      if (deleteChildTopic) {
        childTopic.delete();
      } else {
        childTopic.getRelatingAssociation().delete();
      }
    }
    resolveRefAndAssociateChildTopic(newChildTopic, assocDef);
    childTopics.putInChildTopics(newChildTopic, assocDef);
  }

  private void createAssignmentMany(TopicReferenceModelImpl newChildTopic, AssociationDefinitionModel assocDef) {
    RelatedTopicModelImpl childTopic = childTopics.findChildTopicByRef(newChildTopic, assocDef);
    if (childTopic != null) {
      updateRelatingAssociation(childTopic, newChildTopic);
      return;
    }
    resolveRefAndAssociateChildTopic(newChildTopic, assocDef);
    childTopics.addToChildTopics(newChildTopic, assocDef);
  }

  /**
     * Creates an association between our parent object ("Parent" role) and the referenced topic ("Child" role).
     * The association type is taken from the given association definition.
     *
     * @return  the resolved child topic.
     */
  private void resolveRefAndAssociateChildTopic(TopicReferenceModel childTopicRef, AssociationDefinitionModel assocDef) {
    pl.valueStorage.resolveReference(childTopicRef);
    associateChildTopic(childTopicRef, assocDef);
  }

  private void associateChildTopic(RelatedTopicModel childTopic, AssociationDefinitionModel assocDef) {
    pl.valueStorage.associateChildTopic(this, childTopic, assocDef);
  }

  private void deleteChildTopicOne(RelatedTopicModelImpl childTopic, AssociationDefinitionModel assocDef, boolean deleteChildTopic) {
    if (childTopic == null) {
      return;
    }
    if (deleteChildTopic) {
      childTopic.delete();
    } else {
      childTopic.getRelatingAssociation().delete();
    }
    childTopics.removeChildTopic(assocDef);
  }

  private void deleteChildTopicMany(long childTopicId, AssociationDefinitionModel assocDef, boolean deleteChildTopic) {
    RelatedTopicModelImpl childTopic = childTopics.findChildTopicById(childTopicId, assocDef);
    if (childTopic == null) {
      return;
    }
    if (deleteChildTopic) {
      childTopic.delete();
    } else {
      childTopic.getRelatingAssociation().delete();
    }
    childTopics.removeFromChildTopics(childTopic, assocDef);
  }

  private void _calculateLabelAndUpdate() {
    List<String> labelAssocDefUris = null;
    try {
      labelAssocDefUris = getLabelAssocDefUris();
      for (String assocDefUri : labelAssocDefUris) {
        loadChildTopics(assocDefUri);
      }
      calculateLabelAndUpdate();
    } catch (Exception e) {
      throw new RuntimeException("Calculating and updating label of " + objectInfo() + " failed (assoc defs involved: " + labelAssocDefUris + ")", e);
    }
  }

  /**
     * Calculates the label for this object model and updates it in both, memory (in-place), and DB.
     * <p>
     * Prerequisites:
     * 1) this object model is a composite.
     * 2) this object model contains all the child topic models involved in the label calculation.
     *    Note: this method does not load any child topics from DB.
     *
     * ### TODO: make private
     */
  void calculateLabelAndUpdate() {
    try {
      updateSimpleValue(new SimpleValue(calculateLabel()));
    } catch (Exception e) {
      throw new RuntimeException("Calculating and updating label of " + objectInfo() + " failed", e);
    }
  }

  /**
     * Calculates the label for this object model recursively. Recursion ends at a simple object model.
     * <p>
     * Note: called from this class only but can't be private as called on a different object.
     *
     * ### TODO: drop it
     */
  String calculateLabel() {
    if (isSimple()) {
      return getSimpleValue().toString();
    } else {
      StringBuilder builder = new StringBuilder();
      for (String assocDefUri : getLabelAssocDefUris()) {
        appendLabel(calculateChildLabel(assocDefUri), builder, LABEL_CHILD_SEPARATOR);
      }
      return builder.toString();
    }
  }

  private String calculateChildLabel(String assocDefUri) {
    Object value = getChildTopicsModel().get(assocDefUri);
    if (value == null) {
      return "";
    }
    if (value instanceof TopicModel) {
      return ((TopicModelImpl) value).calculateLabel();
    } else {
      if (value instanceof List) {
        StringBuilder builder = new StringBuilder();
        for (TopicModelImpl childTopic : (List<TopicModelImpl>) value) {
          appendLabel(childTopic.calculateLabel(), builder, LABEL_TOPIC_SEPARATOR);
        }
        return builder.toString();
      } else {
        throw new RuntimeException("Unexpected value in a ChildTopicsModel: " + value);
      }
    }
  }

  private void appendLabel(String label, StringBuilder builder, String separator) {
    if (builder.length() > 0 && label.length() > 0) {
      builder.append(separator);
    }
    builder.append(label);
  }

  /**
     * Prerequisite: this is a composite model.
     */
  private List<String> getLabelAssocDefUris() {
    TypeModelImpl type = getType();
    List<String> labelConfig = type.getLabelConfig();
    if (labelConfig.size() > 0) {
      return labelConfig;
    } else {
      List<String> assocDefUris = new ArrayList();
      Iterator<? extends AssociationDefinitionModel> i = type.getAssocDefs().iterator();
      if (i.hasNext()) {
        assocDefUris.add(i.next().getAssocDefUri());
      }
      return assocDefUris;
    }
  }

  private AssociationDefinitionModel getAssocDef(String assocDefUri) {
    return getType().getAssocDef(assocDefUri);
  }

  String objectInfo() {
    return className() + " " + id;
  }
}