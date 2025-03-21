package de.deepamehta.core.impl.service;
import de.deepamehta.core.Association;
import de.deepamehta.core.AssociationDefinition;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.DeepaMehtaTransaction;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.ResultSet;
import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.Type;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.model.CompositeValue;
import de.deepamehta.core.model.DeepaMehtaObjectModel;
import de.deepamehta.core.model.IndexMode;
import de.deepamehta.core.model.RoleModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicDeletionModel;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.model.TopicRoleModel;
import de.deepamehta.core.service.ChangeReport;
import de.deepamehta.core.util.JavaUtils;
import de.deepamehta.core.service.ClientState;
import de.deepamehta.core.service.Directives;
import de.deepamehta.core.util.DeepaMehtaUtils;
import org.codehaus.jettison.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * DeepaMehtaObject implementation that takes a DeepaMehtaObjectModel and attaches it to the DB.
 *
 * Method name conventions and semantics:
 *  - getXX()           Reads from memory (model).
 *  - setXX(arg)        Writes to memory (model) and DB. Elementary operation.
 *  - updateXX(arg)     Compares arg with current value (model) and calls setXX() method(s) if required.
 *                      Can be called with arg=null which indicates no update is requested.
 *                      Typically returns nothing.
 *  - fetchXX()         Fetches value from DB.
 *  - storeXX()         Stores current value (model) to DB.
 */
abstract class AttachedDeepaMehtaObject implements DeepaMehtaObject {
  private static final String LABEL_SEPARATOR = " ";

  private DeepaMehtaObjectModel model;

  protected final EmbeddedService dms;

  private Logger logger = Logger.getLogger(getClass().getName());

  AttachedDeepaMehtaObject(EmbeddedService dms) {
    this.model = null;
    this.dms = dms;
  }

  AttachedDeepaMehtaObject(DeepaMehtaObjectModel model, EmbeddedService dms) {
    if (model.getUri() == null) {
      model.setUri("");
    }
    if (model.getSimpleValue() == null) {
      model.setSimpleValue("");
    }
    this.model = model;
    this.dms = dms;
  }

  @Override public long getId() {
    return model.getId();
  }

  @Override public String getUri() {
    return model.getUri();
  }

  @Override public void setUri(String uri) {
    model.setUri(uri);
    storeUri(uri);
  }

  @Override public String getTypeUri() {
    return model.getTypeUri();
  }

  @Override public void setTypeUri(String typeUri) {
    model.setTypeUri(typeUri);
    storeTypeUri();
  }

  @Override public SimpleValue getSimpleValue() {
    return model.getSimpleValue();
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
    if (value == null) {
      throw new IllegalArgumentException("Tried to set a null SimpleValue (" + this + ")");
    }
    model.setSimpleValue(value);
    storeAndIndexValue(value);
  }

  @Override public CompositeValue getCompositeValue() {
    return model.getCompositeValue();
  }

  @Override public void setCompositeValue(CompositeValue comp, ClientState clientState, Directives directives) {
    DeepaMehtaTransaction tx = dms.beginTx();
    try {
      updateCompositeValue(comp, clientState, directives);
      refreshLabel();
      tx.success();
    } catch (Exception e) {
      logger.warning("ROLLBACK!");
      throw new RuntimeException("Setting composite value failed (" + comp + ")", e);
    } finally {
      tx.finish();
    }
  }

  @Override public void updateChildTopic(AssociationDefinition assocDef, TopicModel newChildTopic, ClientState clientState, Directives directives) {
    updateChildTopics(assocDef, true, newChildTopic, null, clientState, directives);
  }

  @Override public void updateChildTopics(AssociationDefinition assocDef, List<TopicModel> newChildTopics, ClientState clientState, Directives directives) {
    updateChildTopics(assocDef, false, null, newChildTopics, clientState, directives);
  }

  @Override public SimpleValue getChildTopicValue(String assocDefUri) {
    return fetchChildTopicValue(getAssocDef(assocDefUri));
  }

  @Override public void setChildTopicValue(String assocDefUri, SimpleValue value) {
    getCompositeValue().put(assocDefUri, value.value());
    storeChildTopicValue(assocDefUri, value);
    refreshLabel();
  }

  @Override public ResultSet<RelatedTopic> getRelatedTopics(String assocTypeUri, int maxResultSize, ClientState clientState) {
    return getRelatedTopics(assocTypeUri, null, null, null, false, false, maxResultSize, clientState);
  }

  @Override public Set<Association> getAssociations() {
    return getAssociations(null);
  }

  @Override public ChangeReport update(DeepaMehtaObjectModel model, ClientState clientState, Directives directives) {
    ChangeReport report = new ChangeReport();
    updateUri(model.getUri());
    updateTypeUri(model.getTypeUri(), report);
    if (getType().getDataTypeUri().equals("dm4.core.composite")) {
      updateCompositeValue(model.getCompositeValue(), clientState, directives);
      refreshLabel();
    } else {
      updateSimpleValue(model.getSimpleValue());
    }
    return report;
  }

  /**
     * Deletes all sub-topics of this DeepaMehta object (associated via "dm4.core.composition", recursively) and
     * deletes all the remaining direct associations of this DeepaMehta object.
     * <p>
     * Note: deletion of the object itself is up to the subclasses.
     */
  @Override public void delete(Directives directives) {
    ResultSet<RelatedTopic> partTopics = getRelatedTopics("dm4.core.composition", "dm4.core.whole", "dm4.core.part", null, false, false, 0, null);
    for (Topic partTopic : partTopics) {
      partTopic.delete(directives);
    }
    for (Association assoc : getAssociations()) {
      try {
        assoc.delete(directives);
      } catch (IllegalStateException e) {
        if (e.getMessage().matches("Node\\[\\d+\\] has been deleted in this tx")) {
          logger.info("### Association " + assoc.getId() + " has already been deleted in this transaction. " + "This can happen while deleting a topic with direct associations A1 and A2 while A2 points " + "to A1");
        } else {
          throw e;
        }
      }
    }
  }

  @Override public JSONObject toJSON() {
    return model.toJSON();
  }

  @Override public boolean equals(Object o) {
    return ((AttachedDeepaMehtaObject) o).model.equals(model);
  }

  @Override public int hashCode() {
    return model.hashCode();
  }

  @Override public String toString() {
    return model.toString();
  }

  public DeepaMehtaObjectModel getModel() {
    return model;
  }

  protected final void setModel(DeepaMehtaObjectModel model) {
    this.model = model;
  }

  protected abstract String className();

  protected abstract void storeUri(String uri);

  protected abstract void storeTypeUri();

  protected abstract SimpleValue storeValue(SimpleValue value);

  protected abstract void indexValue(IndexMode indexMode, String indexKey, SimpleValue value, SimpleValue oldValue);

  protected abstract Type getType();

  protected abstract RoleModel createRoleModel(String roleTypeUri);

  void store(ClientState clientState, Directives directives) {
    if (getType().getDataTypeUri().equals("dm4.core.composite")) {
      CompositeValue comp = getCompositeValue();
      model.setCompositeValue(new CompositeValue());
      updateCompositeValue(comp, clientState, directives);
      refreshLabel();
    } else {
      storeAndIndexValue(getSimpleValue());
    }
  }

  /**
     * Called from {@link EmbeddedService#attach} (indirectly)
     */
  void loadComposite() {
    CompositeValue comp = fetchComposite();
    model.setCompositeValue(comp);
  }

  private void updateUri(String newUri) {
    if (newUri != null) {
      String uri = getUri();
      if (!uri.equals(newUri)) {
        logger.info("### Changing URI from \"" + uri + "\" -> \"" + newUri + "\"");
        setUri(newUri);
      }
    }
  }

  private void updateTypeUri(String newTypeUri, ChangeReport report) {
    if (newTypeUri != null) {
      String typeUri = getTypeUri();
      if (!typeUri.equals(newTypeUri)) {
        logger.info("### Changing type URI from \"" + typeUri + "\" -> \"" + newTypeUri + "\"");
        report.typeUriChanged(typeUri, newTypeUri);
        setTypeUri(newTypeUri);
      }
    }
  }

  private void updateSimpleValue(SimpleValue newValue) {
    if (newValue != null) {
      SimpleValue value = getSimpleValue();
      if (!value.equals(newValue)) {
        logger.info("### Changing simple value from \"" + value + "\" -> \"" + newValue + "\"");
        setSimpleValue(newValue);
      }
    }
  }

  private void updateCompositeValue(CompositeValue newComp, ClientState clientState, Directives directives) {
    try {
      for (AssociationDefinition assocDef : getType().getAssocDefs().values()) {
        String assocDefUri = assocDef.getUri();
        String cardinalityUri = assocDef.getPartCardinalityUri();
        TopicModel newChildTopic = null;
        List<TopicModel> newChildTopics = null;
        boolean one = false;
        if (cardinalityUri.equals("dm4.core.one")) {
          newChildTopic = newComp.getTopic(assocDefUri, null);
          if (newChildTopic == null) {
            continue;
          }
          one = true;
        } else {
          if (cardinalityUri.equals("dm4.core.many")) {
            newChildTopics = newComp.getTopics(assocDefUri, null);
            if (newChildTopics == null) {
              continue;
            }
          } else {
            throw new RuntimeException("\"" + cardinalityUri + "\" is an unexpected cardinality URI");
          }
        }
        updateChildTopics(assocDef, one, newChildTopic, newChildTopics, clientState, directives);
      }
    } catch (Exception e) {
      throw new RuntimeException("Updating the composite value of " + className() + " " + getId() + " failed (newComp=" + newComp + ")", e);
    }
  }

  private void updateChildTopics(AssociationDefinition assocDef, boolean one, TopicModel newChildTopic, List<TopicModel> newChildTopics, ClientState clientState, Directives directives) {
    String assocTypeUri = assocDef.getTypeUri();
    if (assocTypeUri.equals("dm4.core.composition_def")) {
      if (one) {
        updateCompositionOne(assocDef, newChildTopic, clientState, directives);
      } else {
        updateCompositionMany(assocDef, newChildTopics, clientState, directives);
      }
    } else {
      if (assocTypeUri.equals("dm4.core.aggregation_def")) {
        if (one) {
          updateAggregationOne(assocDef, newChildTopic, clientState, directives);
        } else {
          updateAggregationMany(assocDef, newChildTopics, clientState, directives);
        }
      } else {
        throw new RuntimeException("Association type \"" + assocTypeUri + "\" not supported");
      }
    }
  }

  private void updateCompositionOne(AssociationDefinition assocDef, TopicModel newChildTopic, ClientState clientState, Directives directives) {
    Topic childTopic = fetchChildTopic(assocDef, newChildTopic);
    if (childTopic != null) {
      childTopic.update(newChildTopic, clientState, directives);
    } else {
      childTopic = dms.createTopic(newChildTopic, null);
      associateChildTopic(assocDef, childTopic.getId());
    }
    updateCompositeModel(assocDef, childTopic.getModel());
  }

  private void updateCompositionMany(AssociationDefinition assocDef, List<TopicModel> newChildTopics, ClientState clientState, Directives directives) {
    for (TopicModel newChildTopic : newChildTopics) {
      if (newChildTopic instanceof TopicDeletionModel) {
        deleteChildTopic(assocDef, newChildTopic, clientState, directives);
      } else {
        updateCompositionOne(assocDef, newChildTopic, clientState, directives);
      }
    }
  }

  private void deleteChildTopic(AssociationDefinition assocDef, TopicModel childTopic, ClientState clientState, Directives directives) {
    dms.getTopic(childTopic.getId(), false, null).delete(directives);
    updateCompositeModelDeletion(assocDef, childTopic);
  }

  /**
     * Updates memory.
     */
  private void updateCompositeModel(AssociationDefinition assocDef, TopicModel topic) {
    CompositeValue comp = getCompositeValue();
    String assocDefUri = assocDef.getUri();
    String cardinalityUri = assocDef.getPartCardinalityUri();
    if (cardinalityUri.equals("dm4.core.one")) {
      comp.put(assocDefUri, topic);
    } else {
      if (cardinalityUri.equals("dm4.core.many")) {
        List<TopicModel> topics = comp.getTopics(assocDefUri, null);
        if (topics == null) {
          topics = new ArrayList();
          comp.put(assocDefUri, topics);
        }
        topics.remove(topic);
        topics.add(topic);
      } else {
        throw new RuntimeException("\"" + cardinalityUri + "\" is an unexpected cardinality URI");
      }
    }
  }

  /**
     * Updates memory.
     */
  private void updateCompositeModelDeletion(AssociationDefinition assocDef, TopicModel topic) {
    CompositeValue comp = getCompositeValue();
    String assocDefUri = assocDef.getUri();
    List<TopicModel> topics = comp.getTopics(assocDefUri, null);
    topics.remove(topic);
  }

  private void updateAggregationOne(AssociationDefinition assocDef, TopicModel newChildTopic, ClientState clientState, Directives directives) {
    RelatedTopic childTopic = fetchChildTopic(assocDef, false);
    if (childTopic != null) {
      childTopic.getAssociation().delete(directives);
    }
    Topic topic = createAssignment(assocDef, newChildTopic);
    updateCompositeModelOne(assocDef, topic.getModel());
  }

  private void updateAggregationMany(AssociationDefinition assocDef, List<TopicModel> newChildTopics, ClientState clientState, Directives directives) {
    for (RelatedTopic childTopic : fetchChildTopics(assocDef, false)) {
      childTopic.getAssociation().delete(directives);
    }
    List<TopicModel> topics = new ArrayList();
    for (TopicModel newChildTopic : newChildTopics) {
      Topic topic = createAssignment(assocDef, newChildTopic);
      topics.add(topic.getModel());
    }
    updateCompositeModelMany(assocDef, topics);
  }

  /**
     * Updates the DB.
     */
  Topic createAssignment(AssociationDefinition assocDef, TopicModel newChildTopic) {
    long childTopicId = newChildTopic.getId();
    String childTopicUri = newChildTopic.getUri();
    if (childTopicId != -1) {
      associateChildTopic(assocDef, childTopicId);
      return fetchChildTopic(assocDef, childTopicId, false);
    } else {
      if (!childTopicUri.equals("")) {
        associateChildTopic(assocDef, childTopicUri);
        return fetchChildTopic(assocDef, childTopicUri, false);
      } else {
        Topic childTopic = dms.createTopic(newChildTopic, null);
        associateChildTopic(assocDef, childTopic.getId());
        return childTopic;
      }
    }
  }

  /**
     * Updates memory.
     */
  private void updateCompositeModelOne(AssociationDefinition assocDef, TopicModel topic) {
    getCompositeValue().put(assocDef.getUri(), topic);
  }

  /**
     * Updates memory.
     */
  private void updateCompositeModelMany(AssociationDefinition assocDef, List<TopicModel> topics) {
    getCompositeValue().put(assocDef.getUri(), topics);
  }

  private CompositeValue fetchComposite() {
    try {
      CompositeValue comp = new CompositeValue();
      for (AssociationDefinition assocDef : getType().getAssocDefs().values()) {
        String cardinalityUri = assocDef.getPartCardinalityUri();
        if (cardinalityUri.equals("dm4.core.one")) {
          Topic childTopic = fetchChildTopic(assocDef, true);
          if (childTopic != null) {
            comp.put(assocDef.getUri(), childTopic.getModel());
          }
        } else {
          if (cardinalityUri.equals("dm4.core.many")) {
            ResultSet<RelatedTopic> childTopics = fetchChildTopics(assocDef, true);
            comp.put(assocDef.getUri(), DeepaMehtaUtils.toTopicModels(childTopics));
          } else {
            throw new RuntimeException("\"" + cardinalityUri + "\" is an unexpected cardinality URI");
          }
        }
      }
      return comp;
    } catch (Exception e) {
      throw new RuntimeException("Fetching the " + className() + "\'s composite failed (" + this + ")", e);
    }
  }

  /**
     * Fetches and returns a child topic or <code>null</code> if no such topic extists.
     */
  private RelatedTopic fetchChildTopic(String assocDefUri, boolean fetchComposite) {
    return fetchChildTopic(getAssocDef(assocDefUri), fetchComposite);
  }

  /**
     * Fetches and returns a child topic or <code>null</code> if no such topic extists.
     */
  private RelatedTopic fetchChildTopic(AssociationDefinition assocDef, boolean fetchComposite) {
    String assocTypeUri = assocDef.getInstanceLevelAssocTypeUri();
    String myRoleTypeUri = assocDef.getWholeRoleTypeUri();
    String othersRoleTypeUri = assocDef.getPartRoleTypeUri();
    String othersTopicTypeUri = assocDef.getPartTopicTypeUri();
    return getRelatedTopic(assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri, fetchComposite, false, null);
  }

  private Topic fetchChildTopic(AssociationDefinition assocDef, long childTopicId, boolean fetchComposite) {
    Topic childTopic = dms.getTopic(childTopicId, fetchComposite, null);
    String assocTypeUri = assocDef.getInstanceLevelAssocTypeUri();
    String myRoleTypeUri = assocDef.getWholeRoleTypeUri();
    String othersRoleTypeUri = assocDef.getPartRoleTypeUri();
    AssociationModel assoc = dms.storage.getAssociation(assocTypeUri, getId(), childTopicId, myRoleTypeUri, othersRoleTypeUri);
    if (assoc == null) {
      throw new RuntimeException("Topic " + childTopicId + " is not a child of topic " + getId() + " according to " + assocDef);
    }
    return childTopic;
  }

  private Topic fetchChildTopic(AssociationDefinition assocDef, String childTopicUri, boolean fetchComposite) {
    Topic childTopic = dms.getTopic("uri", new SimpleValue(childTopicUri), fetchComposite, null);
    String assocTypeUri = assocDef.getInstanceLevelAssocTypeUri();
    String myRoleTypeUri = assocDef.getWholeRoleTypeUri();
    String othersRoleTypeUri = assocDef.getPartRoleTypeUri();
    AssociationModel assoc = dms.storage.getAssociation(assocTypeUri, getId(), childTopic.getId(), myRoleTypeUri, othersRoleTypeUri);
    if (assoc == null) {
      throw new RuntimeException("Topic with URI \"" + childTopicUri + "\" is not a child of topic " + getId() + " according to " + assocDef);
    }
    return childTopic;
  }

  /**
     * Fetches and returns the child topic that matches an update topic model,
     * or <code>null</code> if no such topic extists.
     */
  private Topic fetchChildTopic(AssociationDefinition assocDef, TopicModel newChildTopic) {
    String cardinalityUri = assocDef.getPartCardinalityUri();
    if (cardinalityUri.equals("dm4.core.one")) {
      return fetchChildTopic(assocDef, true);
    } else {
      long childTopicId = newChildTopic.getId();
      if (childTopicId != -1) {
        return fetchChildTopic(assocDef, childTopicId, true);
      } else {
        return null;
      }
    }
  }

  private ResultSet<RelatedTopic> fetchChildTopics(AssociationDefinition assocDef, boolean fetchComposite) {
    String assocTypeUri = assocDef.getInstanceLevelAssocTypeUri();
    String myRoleTypeUri = assocDef.getWholeRoleTypeUri();
    String othersRoleTypeUri = assocDef.getPartRoleTypeUri();
    String othersTopicTypeUri = assocDef.getPartTopicTypeUri();
    return getRelatedTopics(assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri, fetchComposite, false, 0, null);
  }

  private SimpleValue fetchChildTopicValue(AssociationDefinition assocDef) {
    Topic childTopic = fetchChildTopic(assocDef, false);
    if (childTopic != null) {
      return childTopic.getSimpleValue();
    }
    return null;
  }

  /**
     * Stores a child's topic value in the database. If the child topic does not exist it is created.
     *
     * @param   assocDefUri     The "axis" that leads to the child: the URI of an {@link AssociationDefinition}.
     * @param   value           The value to set. If <code>null</code> nothing is set. The child topic is potentially
     *                          created and returned anyway.
     *
     * @return  The child topic.
     */
  private Topic storeChildTopicValue(String assocDefUri, final SimpleValue value) {
    try {
      AssociationDefinition assocDef = getAssocDef(assocDefUri);
      Topic childTopic = fetchChildTopic(assocDef, false);
      if (childTopic != null) {
        if (value != null) {
          childTopic.setSimpleValue(value);
        }
      } else {
        String topicTypeUri = assocDef.getPartTopicTypeUri();
        childTopic = dms.createTopic(new TopicModel(topicTypeUri, value), null);
        associateChildTopic(assocDef, childTopic.getId());
      }
      return childTopic;
    } catch (Exception e) {
      throw new RuntimeException("Storing child topic value failed (parentTopic=" + this + ",\nassocDefUri=" + assocDefUri + ",\nvalue=\"" + value + "\")", e);
    }
  }

  private void storeAndIndexValue(SimpleValue value) {
    SimpleValue oldValue = storeValue(value);
    indexValue(value, oldValue);
  }

  private void indexValue(SimpleValue value, SimpleValue oldValue) {
    Type type = getType();
    String indexKey = type.getUri();
    if (type.getDataTypeUri().equals("dm4.core.html")) {
      value = new SimpleValue(JavaUtils.stripHTML(value.toString()));
      if (oldValue != null) {
        oldValue = new SimpleValue(JavaUtils.stripHTML(oldValue.toString()));
      }
    }
    for (IndexMode indexMode : type.getIndexModes()) {
      indexValue(indexMode, indexKey, value, oldValue);
    }
  }

  /**
     * Prerequisite: this is a composite object.
     */
  private void refreshLabel() {
    try {
      String label;
      if (getType().getLabelConfig().size() > 0) {
        label = buildLabel();
      } else {
        label = buildDefaultLabel();
      }
      setSimpleValue(label);
    } catch (Exception e) {
      throw new RuntimeException("Refreshing the " + className() + "\'s label failed", e);
    }
  }

  /**
     * Builds this object's label according to its type's label configuration.
     */
  private String buildLabel() {
    Type type = getType();
    if (type.getDataTypeUri().equals("dm4.core.composite")) {
      StringBuilder label = new StringBuilder();
      for (String assocDefUri : type.getLabelConfig()) {
        Topic childTopic = fetchChildTopic(assocDefUri, false);
        if (childTopic != null) {
          String l = ((AttachedDeepaMehtaObject) childTopic).buildLabel();
          if (label.length() > 0 && l.length() > 0) {
            label.append(LABEL_SEPARATOR);
          }
          label.append(l);
        }
      }
      return label.toString();
    } else {
      return getSimpleValue().toString();
    }
  }

  private String buildDefaultLabel() {
    Type type = getType();
    if (type.getDataTypeUri().equals("dm4.core.composite")) {
      Iterator<AssociationDefinition> i = type.getAssocDefs().values().iterator();
      if (i.hasNext()) {
        AssociationDefinition assocDef = i.next();
        Topic childTopic = fetchChildTopic(assocDef, false);
        if (childTopic != null) {
          return ((AttachedDeepaMehtaObject) childTopic).buildDefaultLabel();
        }
      }
      return "";
    } else {
      return getSimpleValue().toString();
    }
  }

  private void associateChildTopic(AssociationDefinition assocDef, long childTopicId) {
    dms.createAssociation(assocDef.getInstanceLevelAssocTypeUri(), createRoleModel(assocDef.getWholeRoleTypeUri()), new TopicRoleModel(childTopicId, assocDef.getPartRoleTypeUri()));
  }

  private void associateChildTopic(AssociationDefinition assocDef, String childTopicUri) {
    dms.createAssociation(assocDef.getInstanceLevelAssocTypeUri(), createRoleModel(assocDef.getWholeRoleTypeUri()), new TopicRoleModel(childTopicUri, assocDef.getPartRoleTypeUri()));
  }

  private AssociationDefinition getAssocDef(String assocDefUri) {
    return getType().getAssocDef(assocDefUri);
  }
}