package org.apache.roller.weblogger.ui.rendering.model;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.roller.util.DateUtil;
import org.apache.roller.weblogger.WebloggerException;
import org.apache.roller.weblogger.business.URLStrategy;
import org.apache.roller.weblogger.business.WeblogEntryManager;
import org.apache.roller.weblogger.business.Weblogger;
import org.apache.roller.weblogger.business.WebloggerFactory;
import org.apache.roller.weblogger.business.search.FieldConstants;
import org.apache.roller.weblogger.business.search.IndexManager;
import org.apache.roller.weblogger.business.search.operations.SearchOperation;
import org.apache.roller.weblogger.config.WebloggerRuntimeConfig;
import org.apache.roller.weblogger.pojos.WeblogEntry;
import org.apache.roller.weblogger.pojos.WeblogEntryWrapperComparator;
import org.apache.roller.weblogger.pojos.wrapper.WeblogCategoryWrapper;
import org.apache.roller.weblogger.pojos.wrapper.WeblogEntryWrapper;
import org.apache.roller.weblogger.ui.rendering.pagers.SearchResultsPager;
import org.apache.roller.weblogger.ui.rendering.pagers.WeblogEntriesPager;
import org.apache.roller.weblogger.ui.rendering.util.WeblogSearchRequest;
import org.apache.roller.weblogger.util.I18nMessages;

/**
 * Extends normal page renderer model to represent search results.
 * 
 * Also adds some new methods which are specific only to search results.
 */
public class SearchResultsModel extends PageModel {
  public static final int RESULTS_PER_PAGE = 10;

  WeblogSearchRequest searchRequest = null;

  private URLStrategy urlStrategy = null;

  private Map<Date, TreeSet<WeblogEntryWrapper>> results = new TreeMap<Date, TreeSet<WeblogEntryWrapper>>(Collections.reverseOrder());

  private SearchResultsPager pager = null;

  private int hits = 0;

  private int offset = 0;

  private int limit = 0;

  private Set categories = new TreeSet();

  private boolean websiteSpecificSearch = true;

  private String errorMessage = null;

  @Override public void init(Map initData) throws WebloggerException {
    searchRequest = (WeblogSearchRequest) initData.get("searchRequest");
    if (searchRequest == null) {
      throw new WebloggerException("expected searchRequest from init data");
    }
    urlStrategy = (URLStrategy) initData.get("urlStrategy");
    if (urlStrategy == null) {
      urlStrategy = WebloggerFactory.getWeblogger().getUrlStrategy();
    }
    super.init(initData);
    if (searchRequest.getQuery() == null) {
      pager = new SearchResultsPager(urlStrategy, searchRequest, results, false);
      return;
    }
    IndexManager indexMgr = WebloggerFactory.getWeblogger().getIndexManager();
    SearchOperation search = new SearchOperation(indexMgr);
    search.setTerm(searchRequest.getQuery());
    if (WebloggerRuntimeConfig.isSiteWideWeblog(searchRequest.getWeblogHandle())) {
      this.websiteSpecificSearch = false;
    } else {
      search.setWebsiteHandle(searchRequest.getWeblogHandle());
    }
    if (StringUtils.isNotEmpty(searchRequest.getWeblogCategoryName())) {
      search.setCategory(searchRequest.getWeblogCategoryName());
    }
    if (searchRequest.getLocale() != null) {
      search.setLocale(searchRequest.getLocale());
    }
    indexMgr.executeIndexOperationNow(search);
    if (search.getResultsCount() == -1) {
      this.errorMessage = I18nMessages.getMessages(searchRequest.getLocaleInstance()).getString("error.searchProblem");
    } else {
      TopFieldDocs docs = search.getResults();
      ScoreDoc[] hitsArr = docs.scoreDocs;
      this.hits = search.getResultsCount();
      convertHitsToEntries(hitsArr, search);
    }
    pager = new SearchResultsPager(urlStrategy, searchRequest, results, (hits > (offset + limit)));
  }

  /**
	 * Is this page showing search results?
	 */
  @Override public boolean isSearchResults() {
    return true;
  }

  @Override public WeblogEntriesPager getWeblogEntriesPager() {
    return pager;
  }

  @Override public WeblogEntriesPager getWeblogEntriesPager(String category) {
    return pager;
  }

  /**
	 * Convert hits to entries.
	 * 
	 * @param hits
	 *            the hits
	 * @param search
	 *            the search
	 * @throws WebloggerException
	 *             the weblogger exception
	 */
  private void convertHitsToEntries(ScoreDoc[] hits, SearchOperation search) throws WebloggerException {
    this.offset = searchRequest.getPageNum() * RESULTS_PER_PAGE;
    if (this.offset >= hits.length) {
      this.offset = 0;
    }
    this.limit = RESULTS_PER_PAGE;
    if (this.offset + this.limit > hits.length) {
      this.limit = hits.length - this.offset;
    }
    try {
      TreeSet<String> categorySet = new TreeSet<String>();
      Weblogger roller = WebloggerFactory.getWeblogger();
      WeblogEntryManager weblogMgr = roller.getWeblogEntryManager();
      WeblogEntry entry;
      Document doc;
      String handle;
      Timestamp now = new Timestamp(new Date().getTime());
      for (int i = offset; i < offset + limit; i++) {
        doc = search.getSearcher().doc(hits[i].doc);
        handle = doc.getField(FieldConstants.WEBSITE_HANDLE).stringValue();
        entry = weblogMgr.getWeblogEntry(doc.getField(FieldConstants.ID).stringValue());
        if (!(websiteSpecificSearch && handle.equals(searchRequest.getWeblogHandle())) && doc.getField(FieldConstants.CATEGORY) != null) {
          categorySet.add(doc.getField(FieldConstants.CATEGORY).stringValue());
        }
        if (entry != null && entry.getPubTime().before(now)) {
          addEntryToResults(WeblogEntryWrapper.wrap(entry, urlStrategy));
        }
      }
      if (!categorySet.isEmpty()) {
        this.categories = categorySet;
      }
    } catch (IOException e) {
      throw new WebloggerException(e);
    }
  }

  private void addEntryToResults(WeblogEntryWrapper entry) {
    Date midnight = DateUtil.getStartOfDay(entry.getPubTime());
    TreeSet<WeblogEntryWrapper> set = this.results.get(midnight);
    if (set == null) {
      set = new TreeSet<WeblogEntryWrapper>(new WeblogEntryWrapperComparator());
      this.results.put(midnight, set);
    }
    set.add(entry);
  }

  public String getTerm() {
    String query = searchRequest.getQuery();
    return (query == null) ? "" : StringEscapeUtils.escapeXml10(query);
  }

  public String getRawTerm() {
    return (searchRequest.getQuery() == null) ? "" : searchRequest.getQuery();
  }

  public int getHits() {
    return hits;
  }

  public int getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }

  public Map getResults() {
    return results;
  }

  public Set getCategories() {
    return categories;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getWeblogCategoryName() {
    return searchRequest.getWeblogCategoryName();
  }

  @Override public WeblogCategoryWrapper getWeblogCategory() {
    if (searchRequest.getWeblogCategory() != null) {
      return WeblogCategoryWrapper.wrap(searchRequest.getWeblogCategory(), urlStrategy);
    }
    return null;
  }
}