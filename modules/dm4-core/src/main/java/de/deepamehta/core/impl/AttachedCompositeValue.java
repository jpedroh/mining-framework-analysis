package de.deepamehta.core.impl;
import de.deepamehta.core.AssociationDefinition;
import de.deepamehta.core.CompositeValue;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.model.CompositeValueModel;
import de.deepamehta.core.model.RelatedTopicModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicDeletionModel;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.service.ClientState;
import de.deepamehta.core.service.Directives;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class AttachedCompositeValue implements CompositeValue {
  /**
     * Internal representation.
     * Key: String, value: AttachedTopic or List<AttachedTopic>
     */
  private Map<String, Object> childTopics = new HashMap();

  private CompositeValueModel model;

  private AttachedDeepaMehtaObject parent;

  private EmbeddedService dms;

  private Logger logger = Logger.getLogger(getClass().getName());

  AttachedCompositeValue(CompositeValueModel model, AttachedDeepaMehtaObject parent, EmbeddedService dms) {
    this.model = model;
    this.parent = parent;
    this.dms = dms;
    initChildTopics(model);
  }

  @Override public Topic getTopic(String childTypeUri) {
    requireChildTopics(getAssocDef(childTypeUri));
    return _getTopic(childTypeUri);
  }

  @Override public Topic getTopic(String childTypeUri, Topic defaultTopic) {
    requireChildTopics(getAssocDef(childTypeUri));
    return _getTopic(childTypeUri, defaultTopic);
  }

  @Override public List<Topic> getTopics(String childTypeUri) {
    requireChildTopics(getAssocDef(childTypeUri));
    return _getTopics(childTypeUri);
  }

  @Override public List<Topic> getTopics(String childTypeUri, List<Topic> defaultValue) {
    requireChildTopics(getAssocDef(childTypeUri));
    return _getTopics(childTypeUri, defaultValue);
  }

  @Override public String getString(String childTypeUri) {
    return getTopic(childTypeUri).getSimpleValue().toString();
  }

  @Override public int getInt(String childTypeUri) {
    return getTopic(childTypeUri).getSimpleValue().intValue();
  }

  @Override public long getLong(String childTypeUri) {
    return getTopic(childTypeUri).getSimpleValue().longValue();
  }

  @Override public double getDouble(String childTypeUri) {
    return getTopic(childTypeUri).getSimpleValue().doubleValue();
  }

  @Override public boolean getBoolean(String childTypeUri) {
    return getTopic(childTypeUri).getSimpleValue().booleanValue();
  }

  @Override public Object getObject(String childTypeUri) {
    return getTopic(childTypeUri).getSimpleValue().value();
  }

  @Override public CompositeValue getComposite(String childTypeUri) {
    return getTopic(childTypeUri).getCompositeValue();
  }

  @Override public Object get(String childTypeUri) {
    return childTopics.get(childTypeUri);
  }

  @Override public boolean has(String childTypeUri) {
    return childTopics.containsKey(childTypeUri);
  }

  @Override public Iterable<String> childTypeUris() {
    return childTopics.keySet();
  }

  @Override public int size() {
    return childTopics.size();
  }

  @Override public CompositeValue set(String childTypeUri, TopicModel value, ClientState clientState, Directives directives) {
    return _update(childTypeUri, value, clientState, directives);
  }

  @Override public CompositeValue set(String childTypeUri, Object value, ClientState clientState, Directives directives) {
    return _update(childTypeUri, new TopicModel(childTypeUri, new SimpleValue(value)), clientState, directives);
  }

  @Override public CompositeValue set(String childTypeUri, CompositeValueModel value, ClientState clientState, Directives directives) {
    return _update(childTypeUri, new TopicModel(childTypeUri, value), clientState, directives);
  }

  @Override public CompositeValue setRef(String childTypeUri, long refTopicId, ClientState clientState, Directives directives) {
    return _update(childTypeUri, new TopicModel(refTopicId, childTypeUri), clientState, directives);
  }

  @Override public CompositeValue setRef(String childTypeUri, String refTopicUri, ClientState clientState, Directives directives) {
    return _update(childTypeUri, new TopicModel(refTopicUri, childTypeUri), clientState, directives);
  }

  @Override public CompositeValue remove(String childTypeUri, long topicId, ClientState clientState, Directives directives) {
    return _update(childTypeUri, new TopicDeletionModel(topicId), clientState, directives);
  }

  @Override public CompositeValueModel getModel() {
    return model;
  }

  void update(CompositeValueModel newComp, ClientState clientState, Directives directives) {
    try {
      for (AssociationDefinition assocDef : parent.getType().getAssocDefs()) {
        String childTypeUri = assocDef.getChildTypeUri();
        String cardinalityUri = assocDef.getChildCardinalityUri();
        TopicModel newChildTopic = null;
        List<TopicModel> newChildTopics = null;
        if (cardinalityUri.equals("dm4.core.one")) {
          newChildTopic = newComp.getTopic(childTypeUri, null);
          if (newChildTopic == null) {
            continue;
          }
        } else {
          if (cardinalityUri.equals("dm4.core.many")) {
            newChildTopics = newComp.getTopics(childTypeUri, null);
            if (newChildTopics == null) {
              continue;
            }
          } else {
            throw new RuntimeException("\"" + cardinalityUri + "\" is an unexpected cardinality URI");
          }
        }
        updateChildTopics(newChildTopic, newChildTopics, assocDef, clientState, directives);
      }
      dms.valueStorage.refreshLabel(parent.getModel());
    } catch (Exception e) {
      throw new RuntimeException("Updating composite value of " + parent.className() + " " + parent.getId() + " failed (newComp=" + newComp + ")", e);
    }
  }

  void updateChildTopics(TopicModel newChildTopic, List<TopicModel> newChildTopics, AssociationDefinition assocDef, ClientState clientState, Directives directives) {
    requireChildTopics(assocDef);
    String assocTypeUri = assocDef.getTypeUri();
    boolean one = newChildTopic != null;
    if (assocTypeUri.equals("dm4.core.composition_def")) {
      if (one) {
        updateCompositionOne(newChildTopic, assocDef, clientState, directives);
      } else {
        updateCompositionMany(newChildTopics, assocDef, clientState, directives);
      }
    } else {
      if (assocTypeUri.equals("dm4.core.aggregation_def")) {
        if (one) {
          updateAggregationOne(newChildTopic, assocDef, clientState, directives);
        } else {
          updateAggregationMany(newChildTopics, assocDef, clientState, directives);
        }
      } else {
        throw new RuntimeException("Association type \"" + assocTypeUri + "\" not supported");
      }
    }
  }

  private Topic _getTopic(String childTypeUri) {
    Topic topic = (Topic) childTopics.get(childTypeUri);
    if (topic == null) {
      throw new RuntimeException("Child topic of type \"" + childTypeUri + "\" not found in " + childTopics);
    }
    return topic;
  }

  private Topic _getTopic(String childTypeUri, Topic defaultTopic) {
    Topic topic = (Topic) childTopics.get(childTypeUri);
    return topic != null ? topic : defaultTopic;
  }

  private List<Topic> _getTopics(String childTypeUri) {
    try {
      List<Topic> topics = (List<Topic>) childTopics.get(childTypeUri);
      if (topics == null) {
        throw new RuntimeException("Child topics of type \"" + childTypeUri + "\" not found in " + childTopics);
      }
      return topics;
    } catch (ClassCastException e) {
      getModel().throwInvalidAccess(childTypeUri, e);
      return null;
    }
  }

  private List<Topic> _getTopics(String childTypeUri, List<Topic> defaultValue) {
    try {
      List<Topic> topics = (List<Topic>) childTopics.get(childTypeUri);
      return topics != null ? topics : defaultValue;
    } catch (ClassCastException e) {
      getModel().throwInvalidAccess(childTypeUri, e);
      return null;
    }
  }

  private CompositeValue _update(String childTypeUri, TopicModel newChildTopic, ClientState clientState, Directives directives) {
    updateChildTopics(newChildTopic, null, getAssocDef(childTypeUri), clientState, directives);
    dms.valueStorage.refreshLabel(parent.getModel());
    return this;
  }

  private void updateCompositionOne(TopicModel newChildTopic, AssociationDefinition assocDef, ClientState clientState, Directives directives) {
    Topic childTopic = _getTopic(assocDef.getChildTypeUri(), null);
    if (childTopic != null) {
      childTopic.update(newChildTopic, clientState, directives);
    } else {
      childTopic = dms.createTopic(newChildTopic, clientState);
      dms.valueStorage.associateChildTopic(childTopic.getId(), parent.getModel(), assocDef, clientState);
      putInCompositeValue(childTopic, assocDef);
    }
  }

  private void updateCompositionMany(List<TopicModel> newChildTopics, AssociationDefinition assocDef, ClientState clientState, Directives directives) {
    for (TopicModel newChildTopic : newChildTopics) {
      long childTopicId = newChildTopic.getId();
      if (newChildTopic instanceof TopicDeletionModel) {
        Topic childTopic = findChildTopic(childTopicId, assocDef);
        if (childTopic == null) {
          continue;
        }
        childTopic.delete(directives);
        removeFromCompositeValue(childTopic, assocDef);
      } else {
        if (childTopicId != -1) {
          Topic childTopic = findChildTopic(childTopicId, assocDef);
          if (childTopic == null) {
            throw new RuntimeException("Topic " + childTopicId + " is not a child of " + parent.className() + " " + parent.getId() + " according to " + assocDef);
          }
          childTopic.update(newChildTopic, clientState, directives);
        } else {
          Topic childTopic = dms.createTopic(newChildTopic, clientState);
          dms.valueStorage.associateChildTopic(childTopic.getId(), parent.getModel(), assocDef, clientState);
          addToCompositeValue(childTopic, assocDef);
        }
      }
    }
  }

  private void updateAggregationOne(TopicModel newChildTopic, AssociationDefinition assocDef, ClientState clientState, Directives directives) {
    RelatedTopic childTopic = (RelatedTopic) _getTopic(assocDef.getChildTypeUri(), null);
    if (dms.valueStorage.isReference(newChildTopic)) {
      if (childTopic != null) {
        if (isReferingTo(newChildTopic, childTopic)) {
          return;
        }
        childTopic.getRelatingAssociation().delete(directives);
        Topic topic = dms.valueStorage.associateChildTopic(newChildTopic, parent.getModel(), assocDef, clientState);
        putInCompositeValue(topic, assocDef);
      } else {
        Topic topic = dms.valueStorage.associateChildTopic(newChildTopic, parent.getModel(), assocDef, clientState);
        putInCompositeValue(topic, assocDef);
      }
    } else {
      if (childTopic != null) {
        childTopic.getRelatingAssociation().delete(directives);
      }
      Topic topic = dms.createTopic(newChildTopic, clientState);
      dms.valueStorage.associateChildTopic(topic.getId(), parent.getModel(), assocDef, clientState);
      putInCompositeValue(topic, assocDef);
    }
  }

  private void updateAggregationMany(List<TopicModel> newChildTopics, AssociationDefinition assocDef, ClientState clientState, Directives directives) {
    for (TopicModel newChildTopic : newChildTopics) {
      long childTopicId = newChildTopic.getId();
      if (newChildTopic instanceof TopicDeletionModel) {
        RelatedTopic childTopic = findChildTopic(childTopicId, assocDef);
        if (childTopic == null) {
          continue;
        }
        childTopic.getRelatingAssociation().delete(directives);
        removeFromCompositeValue(childTopic, assocDef);
      } else {
        if (dms.valueStorage.isReference(newChildTopic)) {
          if (isReferingTo(newChildTopic, assocDef)) {
            continue;
          }
          Topic topic = dms.valueStorage.associateChildTopic(newChildTopic, parent.getModel(), assocDef, clientState);
          addToCompositeValue(topic, assocDef);
        } else {
          Topic topic = dms.createTopic(newChildTopic, clientState);
          dms.valueStorage.associateChildTopic(topic.getId(), parent.getModel(), assocDef, clientState);
          addToCompositeValue(topic, assocDef);
        }
      }
    }
  }

  /**
     * Lazy-loads child topics (model) and updates this attached object cache accordingly.
     *
     * @param   assocDef    the child topics according to this association definition are loaded.
     *                      Note: the association definition must not necessarily originate from this object's
     *                      type definition. It may originate from a facet definition as well.
     */
  private void requireChildTopics(AssociationDefinition assocDef) {
    String childTypeUri = assocDef.getChildTypeUri();
    if (!has(childTypeUri)) {
      logger.fine("### Lazy-loading \"" + childTypeUri + "\" child topic(s) of " + parent.className() + " " + parent.getId());
      dms.valueStorage.fetchChildTopics(parent.getModel(), assocDef);
      reinit(childTypeUri);
    }
  }

  private void initChildTopics(CompositeValueModel model) {
    for (String childTypeUri : model.keys()) {
      initChildTopics(model, childTypeUri);
    }
  }

  private void initChildTopics(CompositeValueModel model, String childTypeUri) {
    Object value = model.get(childTypeUri);
    if (value == null) {
      return;
    }
    if (value instanceof TopicModel) {
      TopicModel childTopic = (TopicModel) value;
      childTopics.put(childTypeUri, createTopic(childTopic));
    } else {
      if (value instanceof List) {
        List<Topic> topics = new ArrayList();
        childTopics.put(childTypeUri, topics);
        for (TopicModel childTopic : (List<TopicModel>) value) {
          topics.add(createTopic(childTopic));
        }
      } else {
        throw new RuntimeException("Unexpected value in a CompositeValueModel: " + value);
      }
    }
  }

  private Topic createTopic(TopicModel model) {
    if (model instanceof RelatedTopicModel) {
      return new AttachedRelatedTopic((RelatedTopicModel) model, dms);
    } else {
      return new AttachedTopic(model, dms);
    }
  }

  private void reinit(String childTypeUri) {
    initChildTopics(model, childTypeUri);
  }

  /**
     * Puts a single-valued child. An existing value is overwritten.
     */
  private void put(String childTypeUri, Topic topic) {
    childTopics.put(childTypeUri, topic);
  }

  /**
     * Adds a value to a multiple-valued child.
     */
  private void add(String childTypeUri, Topic topic) {
    List<Topic> topics = _getTopics(childTypeUri, null);
    if (topics == null) {
      topics = new ArrayList();
      childTopics.put(childTypeUri, topics);
    }
    topics.add(topic);
  }

  /**
     * Removes a value from a multiple-valued child.
     */
  private void remove(String childTypeUri, Topic topic) {
    List<Topic> topics = _getTopics(childTypeUri, null);
    if (topics != null) {
      topics.remove(topic);
    }
  }

  /**
     * For single-valued childs
     */
  private void putInCompositeValue(Topic childTopic, AssociationDefinition assocDef) {
    String childTypeUri = assocDef.getChildTypeUri();
    put(childTypeUri, childTopic);
    getModel().put(childTypeUri, childTopic.getModel());
  }

  /**
     * For multiple-valued childs
     */
  private void addToCompositeValue(Topic childTopic, AssociationDefinition assocDef) {
    String childTypeUri = assocDef.getChildTypeUri();
    add(childTypeUri, childTopic);
    getModel().add(childTypeUri, childTopic.getModel());
  }

  /**
     * For multiple-valued childs
     */
  private void removeFromCompositeValue(Topic childTopic, AssociationDefinition assocDef) {
    String childTypeUri = assocDef.getChildTypeUri();
    remove(childTypeUri, childTopic);
    getModel().remove(childTypeUri, childTopic.getModel());
  }

  private RelatedTopic findChildTopic(long childTopicId, AssociationDefinition assocDef) {
    List<Topic> childTopics = _getTopics(assocDef.getChildTypeUri(), new ArrayList());
    for (Topic childTopic : childTopics) {
      if (childTopic.getId() == childTopicId) {
        return (RelatedTopic) childTopic;
      }
    }
    return null;
  }

  /**
     * Checks weather the specified topic reference refers to any of the child topics.
     *
     * @param   assocDef    the child topics according to this association definition are considered.
     */
  private boolean isReferingTo(TopicModel topicRef, AssociationDefinition assocDef) {
    List<Topic> childTopics = _getTopics(assocDef.getChildTypeUri(), new ArrayList());
    for (Topic childTopic : childTopics) {
      if (isReferingTo(topicRef, childTopic)) {
        return true;
      }
    }
    return false;
  }

  /**
     * Checks weather the specified topic reference refers the specified topic.
     */
  private boolean isReferingTo(TopicModel topicRef, Topic topic) {
    if (dms.valueStorage.isReferenceById(topicRef)) {
      return topicRef.getId() == topic.getId();
    } else {
      if (dms.valueStorage.isReferenceByUri(topicRef)) {
        return topicRef.getUri().equals(topic.getUri());
      } else {
        throw new RuntimeException("Not a topic reference (" + topicRef + ")");
      }
    }
  }

  private AssociationDefinition getAssocDef(String assocDefUri) {
    return parent.getType().getAssocDef(assocDefUri);
  }
}