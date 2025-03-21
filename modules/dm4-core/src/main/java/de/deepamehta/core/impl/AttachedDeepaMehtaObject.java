package de.deepamehta.core.impl;
import de.deepamehta.core.Association;
import de.deepamehta.core.AssociationDefinition;
import de.deepamehta.core.CompositeValue;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.ResultSet;
import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.Type;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.model.CompositeValueModel;
import de.deepamehta.core.model.DeepaMehtaObjectModel;
import de.deepamehta.core.model.IndexMode;
import de.deepamehta.core.model.RelatedTopicModel;
import de.deepamehta.core.model.RoleModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.model.TopicRoleModel;
import de.deepamehta.core.service.ClientState;
import de.deepamehta.core.service.Directives;
import de.deepamehta.core.storage.spi.DeepaMehtaTransaction;
import org.codehaus.jettison.json.JSONObject;
import java.util.Iterator;
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
 *  - fetchXX()         Fetches value from DB.              ### FIXDOC
 *  - storeXX()         Stores current value (model) to DB. ### FIXDOC
 */
abstract class AttachedDeepaMehtaObject implements DeepaMehtaObject {
  private DeepaMehtaObjectModel model;

  protected final EmbeddedService dms;

  private AttachedCompositeValue childTopics;

  private Logger logger = Logger.getLogger(getClass().getName());

  AttachedDeepaMehtaObject(DeepaMehtaObjectModel model, EmbeddedService dms) {
    this.model = model;
    this.dms = dms;
    this.childTopics = new AttachedCompositeValue(model.getCompositeValueModel(), this, dms);
  }

  @Override public long getId() {
    return model.getId();
  }

  @Override public String getUri() {
    return model.getUri();
  }

  @Override public void setUri(String uri) {
    model.setUri(uri);
    storeUri();
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
    dms.valueStorage.setSimpleValue(getModel(), value);
  }

  @Override public AttachedCompositeValue getCompositeValue() {
    return childTopics;
  }

  @Override public void setCompositeValue(CompositeValueModel comp, ClientState clientState, Directives directives) {
    DeepaMehtaTransaction tx = dms.beginTx();
    try {
      getCompositeValue().update(comp, clientState, directives);
      tx.success();
    } catch (Exception e) {
      logger.warning("ROLLBACK!");
      throw new RuntimeException("Setting composite value failed (" + comp + ")", e);
    } finally {
      tx.finish();
    }
  }

  @Override public DeepaMehtaObjectModel getModel() {
    return model;
  }

  @Override public void update(DeepaMehtaObjectModel newModel, ClientState clientState, Directives directives) {
    updateUri(newModel.getUri());
    updateTypeUri(newModel.getTypeUri());
    if (getType().getDataTypeUri().equals("dm4.core.composite")) {
      getCompositeValue().update(newModel.getCompositeValueModel(), clientState, directives);
    } else {
      updateSimpleValue(newModel.getSimpleValue());
    }
  }

  @Override public void updateChildTopic(TopicModel newChildTopic, AssociationDefinition assocDef, ClientState clientState, Directives directives) {
    getCompositeValue().updateChildTopics(newChildTopic, null, assocDef, clientState, directives);
  }

  @Override public void updateChildTopics(List<TopicModel> newChildTopics, AssociationDefinition assocDef, ClientState clientState, Directives directives) {
    getCompositeValue().updateChildTopics(null, newChildTopics, assocDef, clientState, directives);
  }

  @Override public RelatedTopic getRelatedTopic(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri, boolean fetchComposite, boolean fetchRelatingComposite, ClientState clientState) {
    RelatedTopicModel topic = fetchRelatedTopic(assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri);
    return topic != null ? dms.attach(topic, fetchComposite, fetchRelatingComposite, clientState) : null;
  }

  @Override public ResultSet<RelatedTopic> getRelatedTopics(String assocTypeUri, int maxResultSize, ClientState clientState) {
    return getRelatedTopics(assocTypeUri, null, null, null, false, false, maxResultSize, clientState);
  }

  @Override public ResultSet<RelatedTopic> getRelatedTopics(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri, boolean fetchComposite, boolean fetchRelatingComposite, int maxResultSize, ClientState clientState) {
    ResultSet<RelatedTopicModel> topics = fetchRelatedTopics(assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri, maxResultSize);
    return dms.attach(topics, fetchComposite, fetchRelatingComposite, clientState);
  }

  /**
     * Deletes all sub-topics of this DeepaMehta object (associated via "dm4.core.composition", recursively) and
     * deletes all the remaining direct associations of this DeepaMehta object.
     * <p>
     * Note: deletion of the object itself is up to the subclasses.
     */
  @Override public void delete(Directives directives) {
    if (directives == null) {
      throw new IllegalArgumentException("directives is null");
    }
    ResultSet<RelatedTopic> childTopics = getRelatedTopics("dm4.core.composition", "dm4.core.parent", "dm4.core.child", null, false, false, 0, null);
    for (Topic childTopic : childTopics) {
      childTopic.delete(directives);
    }
    for (Association assoc : getAssociations()) {
      assoc.delete(directives);
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

  abstract String className();

  abstract void storeUri();

  abstract void storeTypeUri();

  abstract RelatedTopicModel fetchRelatedTopic(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri);

  abstract ResultSet<RelatedTopicModel> fetchRelatedTopics(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri, String othersTopicTypeUri, int maxResultSize);

  Type getType() {
    return dms.valueStorage.getType(getModel());
  }

  private void updateUri(String newUri) {
    if (newUri == null) {
      return;
    }
    String uri = getUri();
    if (!uri.equals(newUri)) {
      logger.info("### Changing URI of " + className() + " " + getId() + " from \"" + uri + "\" -> \"" + newUri + "\"");
      setUri(newUri);
    }
  }

  private void updateTypeUri(String newTypeUri) {
    if (newTypeUri == null) {
      return;
    }
    String typeUri = getTypeUri();
    if (!typeUri.equals(newTypeUri)) {
      logger.info("### Changing type URI of " + className() + " " + getId() + " from \"" + typeUri + "\" -> \"" + newTypeUri + "\"");
      setTypeUri(newTypeUri);
    }
  }

  private void updateSimpleValue(SimpleValue newValue) {
    if (newValue == null) {
      return;
    }
    SimpleValue value = getSimpleValue();
    if (!value.equals(newValue)) {
      logger.info("### Changing simple value of " + className() + " " + getId() + " from \"" + value + "\" -> \"" + newValue + "\"");
      setSimpleValue(newValue);
    }
  }
}