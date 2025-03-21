package org.dspace.browse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.core.LogHelper;
import org.dspace.sort.OrderFormat;
import org.dspace.sort.SortOption;

/**
 * This class does most of the actual grunt work of preparing a browse
 * result.  It takes in to a couple of available methods (depending on your
 * desired browse type) a BrowserScope object, and uses this to produce a
 * BrowseInfo object which is sufficient to describe to the User Interface
 * the results of the requested browse
 *
 * @author Richard Jones
 */
public class BrowseEngine {
  /**
     * the logger for this class
     */
  private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(BrowseEngine.class);

  /**
     * the browse scope which is the basis for our browse
     */
  private BrowserScope scope;

  /**
     * the DSpace context
     */
  private final Context context;

  /**
     * The Data Access Object for the browse tables
     */
  private final BrowseDAO dao;

  /**
     * The Browse Index associated with the Browse Scope
     */
  private BrowseIndex browseIndex;

  /**
     * Create a new instance of the Browse engine, using the given DSpace
     * Context object.  This will automatically assign a Data Access Object
     * for the Browse Engine, based on the brand of the provided DBMS.
     *
     * @param context the DSpace context
     * @throws BrowseException if browse error
     */
  public BrowseEngine(Context context) throws BrowseException {
    this.context = context;
    dao = BrowseDAOFactory.getInstance(context);
  }

  /**
     * Perform a standard browse, which will return a BrowseInfo
     * object that represents the results for the current page, the
     * total number of results, the range, and information to construct
     * previous and next links on any web page
     *
     * @param bs the scope of the browse
     * @return the results of the browse
     * @throws BrowseException if browse error
     */
  public BrowseInfo browse(BrowserScope bs) throws BrowseException {
    log.debug(LogHelper.getHeader(context, "browse", ""));
    this.scope = bs;
    browseIndex = scope.getBrowseIndex();
    if (browseIndex.isMetadataIndex() && !scope.isSecondLevel()) {
      return browseByValue(scope);
    } else {
      return browseByItem(scope);
    }
  }

  /**
     * Perform a limited browse, which only returns the results requested,
     * without any extraneous information.  To perform a full browse, use
     * BrowseEngine.browse() above.  This supports Item browse only, and does
     * not currently support focus or values.  This method is used, for example,
     * to generate the Recently Submitted Items results.
     *
     * @param bs the scope of the browse
     * @return the results of the browse
     * @throws BrowseException if browse error
     */
  public BrowseInfo browseMini(BrowserScope bs) throws BrowseException {
    log.info(LogHelper.getHeader(context, "browse_mini", ""));
    this.scope = bs;
    browseIndex = scope.getBrowseIndex();
    dao.setTable(browseIndex.getTableName());
    dao.setAscending(scope.isAscending());
    if (scope.inCollection() || scope.inCommunity()) {
      if (scope.inCollection()) {
        Collection col = (Collection) scope.getBrowseContainer();
        dao.setContainerTable("collection2item");
        dao.setContainerIDField("collection_id");
        dao.setContainerID(col.getID());
      } else {
        if (scope.inCommunity()) {
          Community com = (Community) scope.getBrowseContainer();
          dao.setContainerTable("communities2item");
          dao.setContainerIDField("community_id");
          dao.setContainerID(com.getID());
        }
      }
    }
    dao.setOffset(scope.getOffset());
    dao.setLimit(scope.getResultsPerPage());
    String orderBy = browseIndex.getSortField(scope.isSecondLevel());
    if (scope.getSortBy() > 0) {
      orderBy = "sort_" + Integer.toString(scope.getSortBy());
    }
    dao.setOrderField(orderBy);
    List<Item> results = dao.doQuery();
    BrowseInfo browseInfo = new BrowseInfo(results, 0, scope.getResultsPerPage(), 0);
    browseInfo.setBrowseIndex(browseIndex);
    browseInfo.setSortOption(scope.getSortOption());
    browseInfo.setAscending(scope.isAscending());
    if (scope.inCollection() || scope.inCommunity()) {
      browseInfo.setBrowseContainer(scope.getBrowseContainer());
    }
    browseInfo.setResultsPerPage(scope.getResultsPerPage());
    browseInfo.setEtAl(scope.getEtAl());
    return browseInfo;
  }

  /**
     * Browse the archive by the full item browse mechanism.  This produces a
     * BrowseInfo object which contains full BrowseItem objects as its result
     * set.
     *
     * @param bs the scope of the browse
     * @return the results of the browse
     * @throws BrowseException if browse error
     */
  private BrowseInfo browseByItem(BrowserScope bs) throws BrowseException {
    log.info(LogHelper.getHeader(context, "browse_by_item", ""));
    try {
      dao.setTable(browseIndex.getTableName());
      if (scope.getBrowseIndex() != null && OrderFormat.TITLE.equals(scope.getBrowseIndex().getDataType())) {
        dao.setStartsWith(normalizeJumpToValue(scope.getStartsWith()));
      } else {
        dao.setStartsWith(StringUtils.lowerCase(scope.getStartsWith()));
      }
      dao.setAscending(scope.isAscending());
      String rawValue = null;
      if (scope.hasFilterValue() && scope.isSecondLevel()) {
        String value = scope.getFilterValue();
        rawValue = value;
        value = OrderFormat.makeSortString(value, scope.getFilterValueLang(), scope.getBrowseIndex().getDataType());
        dao.setAuthorityValue(scope.getAuthorityValue());
        if (scope.isSecondLevel()) {
          dao.setFilterValueField("value");
          dao.setFilterValue(rawValue);
        } else {
          dao.setFilterValueField("sort_value");
          dao.setFilterValue(value);
        }
        dao.setFilterValuePartial(scope.getFilterValuePartial());
        dao.setFilterMappingTables(browseIndex.getDistinctTableName(), browseIndex.getMapTableName());
      }
      if (scope.inCollection() || scope.inCommunity()) {
        if (scope.inCollection()) {
          Collection col = (Collection) scope.getBrowseContainer();
          dao.setContainerTable("collection2item");
          dao.setContainerIDField("collection_id");
          dao.setContainerID(col.getID());
        } else {
          if (scope.inCommunity()) {
            Community com = (Community) scope.getBrowseContainer();
            dao.setContainerTable("communities2item");
            dao.setContainerIDField("community_id");
            dao.setContainerID(com.getID());
          }
        }
      }
      String orderBy = browseIndex.getSortField(scope.isSecondLevel());
      if (scope.getSortBy() > 0) {
        orderBy = "sort_" + Integer.toString(scope.getSortBy());
      }
      dao.setOrderField(orderBy);
      int total = getTotalResults();
      int offset = scope.getOffset();
      String rawFocusValue = null;
      if (offset < 1 && (scope.hasJumpToItem() || scope.hasJumpToValue() || scope.hasStartsWith())) {
        rawFocusValue = getJumpToValue();
        String focusValue = normalizeJumpToValue(rawFocusValue);
        log.debug("browsing using focus: " + focusValue);
      }
      dao.setOffset(offset);
      dao.setLimit(scope.getResultsPerPage());
      List<Item> results = null;
      if (total > 0) {
        results = dao.doQuery();
        if (results.isEmpty()) {
          offset = total - scope.getResultsPerPage();
          if (offset < 0) {
            offset = 0;
          }
          dao.setOffset(offset);
          results = dao.doQuery();
        }
      } else {
        results = new ArrayList<>();
      }
      BrowseInfo browseInfo = new BrowseInfo(results, offset, total, offset);
      if (offset + scope.getResultsPerPage() < total) {
        browseInfo.setNextOffset(offset + scope.getResultsPerPage());
      }
      if (offset - scope.getResultsPerPage() > -1) {
        browseInfo.setPrevOffset(offset - scope.getResultsPerPage());
      }
      browseInfo.setBrowseIndex(browseIndex);
      browseInfo.setSortOption(scope.getSortOption());
      browseInfo.setAscending(scope.isAscending());
      browseInfo.setBrowseLevel(scope.getBrowseLevel());
      browseInfo.setValue(rawValue);
      browseInfo.setAuthority(scope.getAuthorityValue());
      browseInfo.setFocus(rawFocusValue);
      if (scope.hasJumpToItem()) {
        browseInfo.setFocusItem(scope.getJumpToItem());
      }
      browseInfo.setStartsWith(scope.hasStartsWith());
      if (scope.inCollection() || scope.inCommunity()) {
        browseInfo.setBrowseContainer(scope.getBrowseContainer());
      }
      browseInfo.setResultsPerPage(scope.getResultsPerPage());
      browseInfo.setEtAl(scope.getEtAl());
      return browseInfo;
    } catch (SQLException e) {
      log.error("caught exception: ", e);
      throw new BrowseException(e);
    }
  }

  /**
     * Browse the archive by single values (such as the name of an author).  This
     * produces a BrowseInfo object that contains Strings as the results of
     * the browse
     *
     * @param bs the scope of the browse
     * @return the results of the browse
     * @throws BrowseException if browse error
     */
  private BrowseInfo browseByValue(BrowserScope bs) throws BrowseException {
    log.info(LogHelper.getHeader(context, "browse_by_value", "focus=" + bs.getJumpToValue()));
    try {
      dao.setTable(browseIndex.getDistinctTableName());
      dao.setStartsWith(normalizeJumpToValue(scope.getStartsWith()));
      dao.setDistinct(true);
      dao.setAscending(scope.isAscending());
      dao.setEnableBrowseFrequencies(browseIndex.isDisplayFrequencies());
      if (browseIndex.isDisplayFrequencies()) {
        dao.setFilterMappingTables(null, browseIndex.getMapTableName());
      }
      if (scope.inCollection() || scope.inCommunity()) {
        if (!browseIndex.isDisplayFrequencies()) {
          dao.setFilterMappingTables(null, browseIndex.getMapTableName());
        }
        if (scope.inCollection()) {
          Collection col = (Collection) scope.getBrowseContainer();
          dao.setContainerTable("collection2item");
          dao.setContainerIDField("collection_id");
          dao.setContainerID(col.getID());
        } else {
          if (scope.inCommunity()) {
            Community com = (Community) scope.getBrowseContainer();
            dao.setContainerTable("communities2item");
            dao.setContainerIDField("community_id");
            dao.setContainerID(com.getID());
          }
        }
      }
      int total = getTotalResults(true);
      dao.setOrderField("sort_value");
      dao.setJumpToField("sort_value");
      int offset = scope.getOffset();
      String rawFocusValue = null;
      if (offset < 1 && scope.hasJumpToValue() || scope.hasStartsWith()) {
        rawFocusValue = getJumpToValue();
      }
      dao.setOffset(offset);
      dao.setLimit(scope.getResultsPerPage());
      List<String[]> results = null;
      if (total > 0) {
        results = dao.doValueQuery();
        if (results.isEmpty()) {
          offset = total - scope.getResultsPerPage();
          if (offset < 0) {
            offset = 0;
          }
          dao.setOffset(offset);
          results = dao.doValueQuery();
        }
      } else {
        results = new ArrayList<>();
      }
      BrowseInfo browseInfo = new BrowseInfo(results, offset, total, offset);
      if (offset + scope.getResultsPerPage() < total) {
        browseInfo.setNextOffset(offset + scope.getResultsPerPage());
      }
      if (offset - scope.getResultsPerPage() > -1) {
        browseInfo.setPrevOffset(offset - scope.getResultsPerPage());
      }
      browseInfo.setBrowseIndex(browseIndex);
      browseInfo.setSortOption(scope.getSortOption());
      browseInfo.setAscending(scope.isAscending());
      browseInfo.setBrowseLevel(scope.getBrowseLevel());
      browseInfo.setFocus(rawFocusValue);
      browseInfo.setStartsWith(scope.hasStartsWith());
      if (scope.inCollection() || scope.inCommunity()) {
        browseInfo.setBrowseContainer(scope.getBrowseContainer());
      }
      browseInfo.setResultsPerPage(scope.getResultsPerPage());
      return browseInfo;
    } catch (SQLException e) {
      log.error("caught exception: ", e);
      throw new BrowseException(e);
    }
  }

  /**
     * Return the focus value.
     *
     * @return the focus value to use
     * @throws BrowseException if browse error
     */
  private String getJumpToValue() throws BrowseException {
    log.debug(LogHelper.getHeader(context, "get_focus_value", ""));
    if (scope.hasJumpToValue()) {
      log.debug(LogHelper.getHeader(context, "get_focus_value_return", "return=" + scope.getJumpToValue()));
      return scope.getJumpToValue();
    }
    if (scope.hasStartsWith()) {
      log.debug(LogHelper.getHeader(context, "get_focus_value_return", "return=" + scope.getStartsWith()));
      return scope.getStartsWith();
    }
    int id = scope.getJumpToItem();
    String tableName = browseIndex.getTableName();
    SortOption so = scope.getSortOption();
    if (so == null || so.getNumber() == 0) {
      if (browseIndex.getSortOption() != null) {
        so = browseIndex.getSortOption();
      }
    }
    String col = "sort_1";
    if (so != null && so.getNumber() > 0) {
      col = "sort_" + Integer.toString(so.getNumber());
    }
    String max = dao.doMaxQuery(col, tableName, id);
    log.debug(LogHelper.getHeader(context, "get_focus_value_return", "return=" + max));
    return max;
  }

  /**
     * Convert the value into an offset into the table for this browse
     *
     * @param value value
     * @return the focus value to use
     * @throws BrowseException if browse error
     */
  private int getOffsetForValue(String value) throws BrowseException {
    SortOption so = scope.getSortOption();
    if (so == null || so.getNumber() == 0) {
      if (browseIndex.getSortOption() != null) {
        so = browseIndex.getSortOption();
      }
    }
    String col = "sort_1";
    if (so != null && so.getNumber() > 0) {
      col = "sort_" + Integer.toString(so.getNumber());
    }
    return dao.doOffsetQuery(col, value, scope.isAscending());
  }

  /**
     * Convert the value into an offset into the table for this browse
     *
     * @param value value
     * @return the focus value to use
     * @throws BrowseException if browse error
     */
  private int getOffsetForDistinctValue(String value) throws BrowseException {
    if (!browseIndex.isMetadataIndex()) {
      throw new IllegalArgumentException("getOffsetForDistinctValue called when not a metadata index");
    }
    return dao.doDistinctOffsetQuery("sort_value", value, scope.isAscending());
  }

  /**
     * Return a normalized focus value. If there is no normalization that can be performed,
     * return the focus value that is passed in.
     *
     * @param value a focus value to normalize
     * @return the normalized focus value
     * @throws BrowseException if browse error
     */
  private String normalizeJumpToValue(String value) throws BrowseException {
    if (scope.hasJumpToValue()) {
      return OrderFormat.makeSortString(scope.getJumpToValue(), scope.getJumpToValueLang(), scope.getBrowseIndex().getDataType());
    } else {
      if (scope.hasStartsWith()) {
        return OrderFormat.makeSortString(scope.getStartsWith(), null, scope.getBrowseIndex().getDataType());
      }
    }
    return value;
  }

  /**
     * Get the total number of results for the browse.  This is the same as
     * calling getTotalResults(false)
     *
     * @return total
     * @throws SQLException    if database error
     * @throws BrowseException if browse error
     */
  private int getTotalResults() throws SQLException, BrowseException {
    return getTotalResults(false);
  }

  /**
     * Get the total number of results.  The argument determines whether this is a distinct
     * browse or not as this has an impact on how results are counted
     *
     * @param distinct is this a distinct browse or not
     * @return the total number of results available in this type of browse
     * @throws SQLException    if database error
     * @throws BrowseException if browse error
     */
  private int getTotalResults(boolean distinct) throws SQLException, BrowseException {
    log.debug(LogHelper.getHeader(context, "get_total_results", "distinct=" + distinct));
    dao.setDistinct(distinct);
    String[] select = { "*" };
    dao.setCountValues(select);
    String focusField = dao.getJumpToField();
    String focusValue = dao.getJumpToValue();
    int limit = dao.getLimit();
    int offset = dao.getOffset();
    dao.setJumpToField(null);
    dao.setJumpToValue(null);
    dao.setLimit(-1);
    dao.setOffset(-1);
    int count = dao.doCountQuery();
    dao.setJumpToField(focusField);
    dao.setJumpToValue(focusValue);
    dao.setLimit(limit);
    dao.setOffset(offset);
    dao.setCountValues(null);
    log.debug(LogHelper.getHeader(context, "get_total_results_return", "return=" + count));
    return count;
  }
}