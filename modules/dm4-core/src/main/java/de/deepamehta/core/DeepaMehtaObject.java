package de.deepamehta.core;

import de.deepamehta.core.model.CompositeValueModel;
import de.deepamehta.core.model.DeepaMehtaObjectModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.service.Directives;
import de.deepamehta.core.service.ResultList;
import java.util.List;


public interface DeepaMehtaObject extends Identifiable , JSONEnabled {
    // === Model ===

    // --- ID ---

    long getId();

    // --- URI ---

    String getUri();

    void setUri(String uri);

    // --- Type URI ---

    String getTypeUri();

    void setTypeUri(String typeUri);

    // --- Simple Value ---

    SimpleValue getSimpleValue();

    void setSimpleValue(String value);

    void setSimpleValue(int value);

    void setSimpleValue(long value);

    void setSimpleValue(boolean value);

    void setSimpleValue(SimpleValue value);

    // --- Composite Value ---

    CompositeValue getCompositeValue();

    public abstract void setCompositeValue(CompositeValueModel comp, Directives directives);

    // ---

    void loadChildTopics();

    void loadChildTopics(String childTypeUri);

    // ---

    DeepaMehtaObjectModel getModel();

    // === Updating ===
    public abstract void update(DeepaMehtaObjectModel model, Directives directives);

    // ---
    public abstract void updateChildTopic(TopicModel newChildTopic, AssociationDefinition assocDef, Directives directives);

    public abstract void updateChildTopics(List<TopicModel> newChildTopics, AssociationDefinition assocDef, Directives directives);

    // === Deletion ===

    /**
     * Deletes the DeepaMehta object in its entirety, that is
     * - the object itself (the <i>parent</i>)
     * - all child topics associated via "dm4.core.composition", recusively
     * - all the remaining direct associations, e.g. "dm4.core.instantiation"
     */
    void delete(Directives directives);

    // === Traversal ===

    // --- Topic Retrieval ---

    /**
     * Fetches and returns a related topic or <code>null</code> if no such topic extists.
     *
     * @param   assocTypeUri        may be null
     * @param   myRoleTypeUri       may be null
     * @param   othersRoleTypeUri   may be null
     * @param   othersTopicTypeUri  may be null
     */
    RelatedTopic getRelatedTopic(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri,
                                 String othersTopicTypeUri, boolean fetchComposite, boolean fetchRelatingComposite);

    ResultList<RelatedTopic> getRelatedTopics(String assocTypeUri, int maxResultSize);

    /**
     * @param   assocTypeUri        may be null
     * @param   myRoleTypeUri       may be null
     * @param   othersRoleTypeUri   may be null
     * @param   othersTopicTypeUri  may be null
     * @param   fetchComposite
     * @param   fetchRelatingComposite
     * @param   maxResultSize       Result size limit. Pass 0 for no limit.
     */
    ResultList<RelatedTopic> getRelatedTopics(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri,
                                    String othersTopicTypeUri, boolean fetchComposite, boolean fetchRelatingComposite,
                                    int maxResultSize);

    /**
     * @param   assocTypeUris       may *not* be null
     * @param   myRoleTypeUri       may be null
     * @param   othersRoleTypeUri   may be null
     * @param   othersTopicTypeUri  may be null
     */
    ResultList<RelatedTopic> getRelatedTopics(List assocTypeUris, String myRoleTypeUri, String othersRoleTypeUri,
                                    String othersTopicTypeUri, boolean fetchComposite, boolean fetchRelatingComposite,
                                    int maxResultSize);

    // --- Association Retrieval ---

    Association getAssociation(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri,
                                                                            long othersTopicId);

    List<Association> getAssociations();

    // === Properties ===

    Object getProperty(String propUri);

    boolean hasProperty(String propUri);

    void setProperty(String propUri, Object propValue, boolean addToIndex);

    void removeProperty(String propUri);

    // === Misc ===

    Object getDatabaseVendorObject();
}