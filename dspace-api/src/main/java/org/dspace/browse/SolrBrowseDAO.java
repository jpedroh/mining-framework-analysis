package org.dspace.browse;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.discovery.DiscoverFacetField;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverQuery.SORT_ORDER;
import org.dspace.discovery.DiscoverResult;
import org.dspace.discovery.DiscoverResult.FacetResult;
import org.dspace.discovery.DiscoverResult.SearchDocument;
import org.dspace.discovery.IndexableObject;
import org.dspace.discovery.SearchService;
import org.dspace.discovery.SearchServiceException;
import org.dspace.discovery.SearchUtils;
import org.dspace.discovery.configuration.DiscoveryConfiguration;
import org.dspace.discovery.configuration.DiscoveryConfigurationParameters;
import org.dspace.discovery.indexobject.IndexableItem;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 * @author Andrea Bollini (CILEA)
 * @author Adán Román Ruiz at arvo.es (bugfix)
 * @author Panagiotis Koutsourakis (National Documentation Centre) (bugfix)
 * @author Kostas Stamatis (National Documentation Centre) (bugfix)
 */
public class SolrBrowseDAO implements BrowseDAO {
  public SolrBrowseDAO(Context context) {
    this.context = context;
  }

  static private class FacetValueComparator implements Comparator, Serializable {
    @Override public int compare(Object o1, Object o2) {
      String s1 = "";
      String s2 = "";
      if (o1 instanceof FacetResult && o2 instanceof String) {
        FacetResult c = (FacetResult) o1;
        s1 = c.getSortValue();
        s2 = (String) o2;
      } else {
        if (o2 instanceof FacetResult && o1 instanceof String) {
          FacetResult c = (FacetResult) o2;
          s1 = (String) o1;
          s2 = c.getSortValue();
        }
      }
      return s1.compareTo(s2);
    }
  }

  /**
     * Log4j log
     */
  private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(SolrBrowseDAO.class);

  /**
     * The DSpace context
     */
  private final Context context;

  /**
     * table(s) to select from
     */
  private String table = null;

  /**
     * field to look for focus value in
     */
  private String focusField = null;

  /**
     * value to start browse from in focus field
     */
  private String focusValue = null;

  private String startsWith = null;

  /**
     * field to look for value in
     */
  private String valueField = null;

  /**
     * value to restrict browse to (e.g. author name)
     */
  private String value = null;

  private String authority = null;

  /**
     * exact or partial matching of the value
     */
  private boolean valuePartial = false;

  /**
     * the table that defines the mapping for the relevant container
     */
  private String containerTable = null;

  /**
     * the name of the field which contains the container id (e.g.
     * collection_id)
     */
  private String containerIDField = null;

  /**
     * the container we are constraining to
     */
  private DSpaceObject container = null;

  /**
     * the column that we are sorting results by
     */
  private String orderField = null;

  /**
     * whether to sort results ascending or descending
     */
  private boolean ascending = true;

  /**
     * the limit of number of results to return
     */
  private int limit = -1;

  /**
     * the offset of the start point
     */
  private int offset = 0;

  /**
     * whether to use the equals comparator in value comparisons
     */
  private boolean equalsComparator = true;

  /**
     * whether this is a distinct browse or not
     */
  private boolean distinct = false;

  private String facetField;

  protected AuthorizeService authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();

  SearchService searcher = DSpaceServicesFactory.getInstance().getServiceManager().getServiceByName(SearchService.class.getName(), SearchService.class);

  private DiscoverResult sResponse = null;

  private boolean showFrequencies;

  private DiscoverResult getSolrResponse() throws BrowseException {
    if (sResponse == null) {
      DiscoverQuery query = new DiscoverQuery();
      addLocationScopeFilter(query);
      addStatusFilter(query);
      if (distinct) {
        DiscoverFacetField dff;
        if (StringUtils.isNotBlank(startsWith)) {
          dff = new DiscoverFacetField(facetField, DiscoveryConfigurationParameters.TYPE_TEXT, -1, DiscoveryConfigurationParameters.SORT.VALUE, startsWith);
        } else {
          dff = new DiscoverFacetField(facetField, DiscoveryConfigurationParameters.TYPE_TEXT, -1, DiscoveryConfigurationParameters.SORT.VALUE);
        }
        query.addFacetField(dff);
        query.setFacetMinCount(1);
        query.setMaxResults(0);
      } else {
        query.setMaxResults(limit);
        if (offset > 0) {
          query.setStart(offset);
        }
        if (authority != null) {
          query.addFilterQueries("{!field f=" + facetField + "_authority_filter}" + authority);
        } else {
          if (value != null && !valuePartial) {
            query.addFilterQueries("{!field f=" + facetField + "_value_filter}" + value);
          } else {
            if (valuePartial) {
              query.addFilterQueries("{!field f=" + facetField + "_partial}" + value);
            }
          }
        }
        if (StringUtils.isNotBlank(startsWith) && orderField != null) {
          query.addFilterQueries("bi_" + orderField + "_sort:" + ClientUtils.escapeQueryChars(startsWith) + "*");
        }
        query.addFilterQueries("search.resourcetype:" + IndexableItem.TYPE);
        if (orderField != null) {
          query.setSortField("bi_" + orderField + "_sort", ascending ? SORT_ORDER.asc : SORT_ORDER.desc);
        }
      }
      try {
        sResponse = searcher.search(context, query);
      } catch (SearchServiceException e) {
        throw new BrowseException(e);
      }
    }
    return sResponse;
  }

  private void addStatusFilter(DiscoverQuery query) {
    try {
      if (!authorizeService.isAdmin(context) && (authorizeService.isCommunityAdmin(context) || authorizeService.isCollectionAdmin(context))) {
        query.addFilterQueries(searcher.createLocationQueryForAdministrableItems(context));
      }
    } catch (SQLException ex) {
      log.error("Error looking up authorization rights of current user", ex);
    }
  }

  private void addLocationScopeFilter(DiscoverQuery query) {
    if (container != null) {
      if (containerIDField.startsWith("collection")) {
        query.addFilterQueries("location.coll:" + container.getID());
      } else {
        if (containerIDField.startsWith("community")) {
          query.addFilterQueries("location.comm:" + container.getID());
        }
      }
    }
    DiscoveryConfiguration discoveryConfiguration = SearchUtils.getDiscoveryConfiguration(container);
    discoveryConfiguration.getDefaultFilterQueries().forEach(query::addFilterQueries);
  }

  @Override public int doCountQuery() throws BrowseException {
    DiscoverResult resp = getSolrResponse();
    int count = 0;
    if (distinct) {
      List<FacetResult> facetResults = resp.getFacetResult(facetField);
      count = facetResults.size();
    } else {
      count = (int) resp.getTotalSearchResults();
      sResponse = null;
    }
    return count;
  }

  @Override public List doValueQuery() throws BrowseException {
    DiscoverResult resp = getSolrResponse();
    List<FacetResult> facet = resp.getFacetResult(facetField);
    int count = doCountQuery();
    int start = offset > 0 ? offset : 0;
    int max = limit > 0 ? limit : count;
    List<String[]> result = new ArrayList<>();
    if (ascending) {
      for (int i = start; i < (start + max) && i < count; i++) {
        FacetResult c = facet.get(i);
        String freq = showFrequencies ? String.valueOf(c.getCount()) : "";
        result.add(new String[] { c.getDisplayedValue(), c.getAuthorityKey(), freq });
      }
    } else {
      for (int i = count - start - 1; i >= count - (start + max) && i >= 0; i--) {
        FacetResult c = facet.get(i);
        String freq = showFrequencies ? String.valueOf(c.getCount()) : "";
        result.add(new String[] { c.getDisplayedValue(), c.getAuthorityKey(), freq });
      }
    }
    return result;
  }

  @Override public List<Item> doQuery() throws BrowseException {
    DiscoverResult resp = getSolrResponse();
    List<Item> bitems = new ArrayList<>();
    for (IndexableObject solrDoc : resp.getIndexableObjects()) {
      Item item = ((IndexableItem) solrDoc).getIndexedObject();
      bitems.add(item);
    }
    return bitems;
  }

  @Override public String doMaxQuery(String column, String table, int itemID) throws BrowseException {
    DiscoverQuery query = new DiscoverQuery();
    query.setQuery("search.resourceid:" + itemID + " AND search.resourcetype:" + IndexableItem.TYPE);
    query.setMaxResults(1);
    DiscoverResult resp = null;
    try {
      resp = searcher.search(context, query);
    } catch (SearchServiceException e) {
      throw new BrowseException(e);
    }
    if (resp.getTotalSearchResults() > 0) {
      SearchDocument doc = resp.getSearchDocument(resp.getIndexableObjects().get(0)).get(0);
      return (String) doc.getSearchFieldValues(column).get(0);
    }
    return null;
  }

  @Override public int doOffsetQuery(String column, String value, boolean isAscending) throws BrowseException {
    DiscoverQuery query = new DiscoverQuery();
    addLocationScopeFilter(query);
    addStatusFilter(query);
    query.setMaxResults(0);
    query.addFilterQueries("search.resourcetype:" + IndexableItem.TYPE);
    if (authority != null) {
      query.addFilterQueries("{!field f=" + facetField + "_authority_filter}" + authority);
    } else {
      if (this.value != null && !valuePartial) {
        query.addFilterQueries("{!field f=" + facetField + "_value_filter}" + this.value);
      } else {
        if (valuePartial) {
          query.addFilterQueries("{!field f=" + facetField + "_partial}" + this.value);
        }
      }
    }
    if (isAscending) {
      query.setQuery("bi_" + column + "_sort" + ": [* TO \"" + value + "\"}");
    } else {
      query.setQuery("bi_" + column + "_sort" + ": {\"" + value + "\" TO *]");
      query.addFilterQueries("-(bi_" + column + "_sort" + ":" + value + "*)");
    }
    DiscoverResult resp = null;
    try {
      resp = searcher.search(context, query);
    } catch (SearchServiceException e) {
      throw new BrowseException(e);
    }
    return (int) resp.getTotalSearchResults();
  }

  @Override public int doDistinctOffsetQuery(String column, String value, boolean isAscending) throws BrowseException {
    DiscoverResult resp = getSolrResponse();
    List<FacetResult> facets = resp.getFacetResult(facetField);
    Comparator comparator = new SolrBrowseDAO.FacetValueComparator();
    Collections.sort(facets, comparator);
    int x = Collections.binarySearch(facets, value, comparator);
    int ascValue = (x >= 0) ? x : -(x + 1);
    if (isAscending) {
      return ascValue;
    } else {
      return doCountQuery() - ascValue;
    }
  }

  @Override public boolean isEnableBrowseFrequencies() {
    return showFrequencies;
  }

  @Override public void setEnableBrowseFrequencies(boolean enableBrowseFrequencies) {
    showFrequencies = enableBrowseFrequencies;
  }

  @Override public DSpaceObject getContainer() {
    return container;
  }

  @Override public String getContainerIDField() {
    return containerIDField;
  }

  @Override public String getContainerTable() {
    return containerTable;
  }

  @Override public String[] getCountValues() {
    return null;
  }

  @Override public String getJumpToField() {
    return focusField;
  }

  @Override public String getJumpToValue() {
    return focusValue;
  }

  @Override public void setStartsWith(String startsWith) {
    this.startsWith = startsWith;
  }

  @Override public String getStartsWith() {
    return startsWith;
  }

  @Override public int getLimit() {
    return limit;
  }

  @Override public int getOffset() {
    return offset;
  }

  @Override public String getOrderField() {
    return orderField;
  }

  @Override public String[] getSelectValues() {
    return null;
  }

  @Override public String getTable() {
    return table;
  }

  @Override public String getFilterValue() {
    return value;
  }

  @Override public String getFilterValueField() {
    return valueField;
  }

  @Override public boolean isAscending() {
    return ascending;
  }

  @Override public boolean isDistinct() {
    return this.distinct;
  }

  @Override public void setAscending(boolean ascending) {
    this.ascending = ascending;
  }

  @Override public void setContainer(DSpaceObject container) {
    this.container = container;
  }

  @Override public void setContainerIDField(String containerIDField) {
    this.containerIDField = containerIDField;
  }

  @Override public void setContainerTable(String containerTable) {
    this.containerTable = containerTable;
  }

  @Override public void setCountValues(String[] fields) {
  }

  @Override public void setDistinct(boolean bool) {
    this.distinct = bool;
  }

  @Override public void setEqualsComparator(boolean equalsComparator) {
    this.equalsComparator = equalsComparator;
  }

  @Override public void setJumpToField(String focusField) {
    this.focusField = focusField;
  }

  @Override public void setJumpToValue(String focusValue) {
    this.focusValue = focusValue;
  }

  @Override public void setLimit(int limit) {
    this.limit = limit;
  }

  @Override public void setOffset(int offset) {
    this.offset = offset;
  }

  @Override public void setOrderField(String orderField) {
    this.orderField = orderField;
  }

  @Override public void setSelectValues(String[] selectValues) {
  }

  @Override public void setTable(String table) {
    facetField = table;
  }

  @Override public void setFilterMappingTables(String tableDis, String tableMap) {
    if (tableDis != null) {
      this.facetField = tableDis;
    }
  }

  @Override public void setFilterValue(String value) {
    this.value = value;
  }

  @Override public void setFilterValuePartial(boolean part) {
    this.valuePartial = part;
  }

  @Override public void setFilterValueField(String valueField) {
    this.valueField = valueField;
  }

  @Override public boolean useEqualsComparator() {
    return equalsComparator;
  }

  @Override public String getAuthorityValue() {
    return authority;
  }

  @Override public void setAuthorityValue(String value) {
    this.authority = value;
  }
}