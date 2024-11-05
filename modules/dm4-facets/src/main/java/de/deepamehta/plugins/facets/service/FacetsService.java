package de.deepamehta.plugins.facets.service;
import de.deepamehta.plugins.facets.model.FacetValue;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.service.Directives;
import de.deepamehta.core.service.PluginService;
import java.util.List;

public interface FacetsService extends PluginService {
  Topic getFacet(long topicId, String facetTypeUri);

  /**
     * Retrieves a facet value.
     * ### TODO: rename to getFacetValue
     * ### TODO: add fetchComposite parameter
     *
     * @param   object          The facetted object: a topic, association, a type ...
     * @param   facetTypeUri    URI of the facet type.
     *
     * @return  The retrieved facet value (including its child topics) or <code>null</code> if no such topic extists.
     */
  Topic getFacet(DeepaMehtaObject object, String facetTypeUri);

  List<RelatedTopic> getFacets(long topicId, String facetTypeUri);

  /**
     * Retrieves the values of a multi-facet.
     * ### TODO: rename to getFacetValues
     * ### TODO: add fetchComposite parameter
     *
     * @param   object          The facetted object: a topic, association, a type ...
     * @param   facetTypeUri    URI of the facet type.
     *
     * @return  The retrieved facet values (including their child topics). The list may be empty.
     */
  List<RelatedTopic> getFacets(DeepaMehtaObject object, String facetTypeUri);

  Topic getFacettedTopic(long topicId, List<String> facetTypeUris);

  void addFacetTypeToTopic(long topicId, String facetTypeUri);

  void updateFacet(long topicId, String facetTypeUri, FacetValue value);

  /**
     * Updates a facet.
     *
     * @param   object          The facetted object: a topic, association, a type ...
     * @param   facetTypeUri    URI of the facet type.
     * @param   facetValue      The new facet value.
     * @param   directives      Must be not null at least for aggregated facets ### FIXME: directives are ignored
     */
  void updateFacet(DeepaMehtaObject object, String facetTypeUri, FacetValue value, Directives directives);

  boolean hasFacet(long topicId, String facetTypeUri, long facetTopicId);
}