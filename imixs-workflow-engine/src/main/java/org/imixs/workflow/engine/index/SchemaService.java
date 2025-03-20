package org.imixs.workflow.engine.index;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.WorkflowKernel;
import org.imixs.workflow.engine.DocumentService;
import org.imixs.workflow.exceptions.QueryException;

/**
 * The IndexSchemaService provides the index Schema.
 * <p>
 * The schema is defined by the following properties:
 * 
 * <ul>
 * <li>index.fields - content which will be indexed</li>
 * <li>index.fields.analyze - fields indexed as analyzed keyword fields</li>
 * <li>index.fields.noanalyze - fields indexed without analyze</li>
 * <li>index.fields.store - fields stored in the index</li>
 * <li>index.operator - default operator</li>
 * <li>index.splitwhitespace - split text on whitespace prior to analysis</li>
 * </ul>
 * 
 * 
 * @version 1.0
 * @author rsoika
 */
@Singleton public class SchemaService {
  public static final String ANONYMOUS = "ANONYMOUS";

  @Inject @ConfigProperty(name = "index.fields") Optional<String> indexFields;

  @Inject @ConfigProperty(name = "index.fields.analyze") Optional<String> indexFieldsAnalyze;

  @Inject @ConfigProperty(name = "index.fields.noanalyze") Optional<String> indexFieldsNoAnalyze;

  @Inject @ConfigProperty(name = "index.fields.store") Optional<String> indexFieldsStore;

  @Inject @ConfigProperty(name = "index.fields.category") Optional<String> indexFieldsCategory;

  @Inject private DocumentService documentService;

  private List<String> fieldList = null;

  private List<String> fieldListAnalyze = null;

  private List<String> fieldListNoAnalyze = null;

  private List<String> fieldListStore = null;

  private List<String> fieldListCategory = null;

  private Set<String> uniqueFieldList = null;

  public static List<String> DEFAULT_SEARCH_FIELD_LIST = Arrays.asList("$workflowsummary", "$workflowabstract");

  public static List<String> DEFAULT_NOANALYZE_FIELD_LIST = Arrays.asList("$modelversion", "$taskid", "$processid", "$workitemid", "$uniqueidref", "type", "$writeaccess", "$snapshotid", "$modified", "$created", "namcreator", "$creator", "$editor", "$lasteditor", "$workflowgroup", "$workflowstatus", "txtworkflowgroup", "name", "txtname", "$owner", "namowner", "txtworkitemref", "$workitemref", "$uniqueidsource", "$uniqueidversions", "$lasttask", "$lastevent", "$lasteventdate", "$file.count", "$file.names");

  public static List<String> DEFAULT_STORE_FIELD_LIST = Arrays.asList("type", "$taskid", "$writeaccess", "$snapshotid", "$modelversion", "$workflowsummary", "$workflowabstract", "$workflowgroup", "$workflowstatus", "$modified", "$created", "$lasteventdate", "$creator", "$editor", "$lasteditor", "$owner", "namowner");

  public static List<String> DEFAULT_CATEGORY_FIELD_LIST = Arrays.asList("type", "$taskid", "$workflowgroup", "$workflowstatus", "$creator", "$editor", "$owner");

  private static Logger logger = Logger.getLogger(SchemaService.class.getName());

  /**
     * PostContruct event - The method loads the lucene index properties from the
     * imixs.properties file from the classpath. If no properties are defined the
     * method terminates.
     * 
     */
  @PostConstruct void init() {
    boolean debug = logger.isLoggable(Level.FINE);
    if (debug) {
      logger.finest("......lucene FulltextFieldList=" + indexFields);
      logger.finest("......lucene IndexFieldListAnalyze=" + indexFieldsAnalyze);
      logger.finest("......lucene IndexFieldListNoAnalyze=" + indexFieldsNoAnalyze);
      logger.finest("......lucene IndexFieldListStore=" + indexFieldsStore);
      logger.finest("......lucene IndexFieldListCategory=" + indexFieldsCategory);
    }
    fieldList = new ArrayList<String>();
    fieldList.addAll(DEFAULT_SEARCH_FIELD_LIST);
    if (indexFields.isPresent() && !indexFields.get().isEmpty()) {
      StringTokenizer st = new StringTokenizer(indexFields.get(), ",");
      while (st.hasMoreElements()) {
        String sName = st.nextToken().toLowerCase().trim();
        if (!"$uniqueid".equals(sName) && !"$readaccess".equals(sName) && !fieldList.contains(sName)) {
          fieldList.add(sName);
        }
      }
    }
    fieldListNoAnalyze = new ArrayList<String>();
    fieldListNoAnalyze.addAll(DEFAULT_NOANALYZE_FIELD_LIST);
    if (indexFieldsNoAnalyze.isPresent() && !indexFieldsNoAnalyze.get().isEmpty()) {
      StringTokenizer st = new StringTokenizer(indexFieldsNoAnalyze.get(), ",");
      while (st.hasMoreElements()) {
        String sName = st.nextToken().toLowerCase().trim();
        if (!"$uniqueid".equals(sName) && !"$readaccess".equals(sName) && !fieldListNoAnalyze.contains(sName)) {
          fieldListNoAnalyze.add(sName);
        }
      }
    }
    fieldListAnalyze = new ArrayList<String>();
    if (indexFieldsAnalyze.isPresent() && !indexFieldsAnalyze.get().isEmpty()) {
      StringTokenizer st = new StringTokenizer(indexFieldsAnalyze.get(), ",");
      while (st.hasMoreElements()) {
        String sName = st.nextToken().toLowerCase().trim();
        if (!"$uniqueid".equals(sName) && !"$readaccess".equals(sName) && !fieldListAnalyze.contains(sName) && !fieldListNoAnalyze.contains(sName)) {
          fieldListAnalyze.add(sName);
        }
      }
    }
    fieldListStore = new ArrayList<String>();
    fieldListStore.addAll(DEFAULT_STORE_FIELD_LIST);
    if (indexFieldsStore.isPresent() && !indexFieldsStore.get().isEmpty()) {
      StringTokenizer st = new StringTokenizer(indexFieldsStore.get(), ",");
      while (st.hasMoreElements()) {
        String sName = st.nextToken().toLowerCase().trim();
        if (!fieldListStore.contains(sName)) {
          fieldListStore.add(sName);
        }
      }
    }
    for (String fieldName : fieldListStore) {
      if (!fieldListAnalyze.contains(fieldName) && !fieldListNoAnalyze.contains(fieldName)) {
        fieldListAnalyze.add(fieldName);
      }
    }
    fieldListCategory = new ArrayList<String>();
    fieldListCategory.addAll(DEFAULT_CATEGORY_FIELD_LIST);
    if (indexFieldsCategory.isPresent() && !indexFieldsCategory.get().isEmpty()) {
      StringTokenizer st = new StringTokenizer(indexFieldsCategory.get(), ",");
      while (st.hasMoreElements()) {
        String sName = st.nextToken().toLowerCase().trim();
        if (!fieldListCategory.contains(sName)) {
          fieldListCategory.add(sName);
        }
      }
    }
    uniqueFieldList = new HashSet<String>();
    uniqueFieldList.add(WorkflowKernel.UNIQUEID);
    uniqueFieldList.add(DocumentService.READACCESS);
    uniqueFieldList.addAll(fieldListStore);
    uniqueFieldList.addAll(fieldListAnalyze);
    uniqueFieldList.addAll(fieldListNoAnalyze);
  }

  /**
     * Returns the field list defining the default content of the schema. The values
     * of those items are only searchable by fulltext search
     * 
     * @return
     */
  public List<String> getFieldList() {
    return fieldList;
  }

  /**
     * Returns the analyzed field list of the schema. The values of those items are
     * searchable by a field search. The values are analyzed.
     * 
     * @return
     */
  public List<String> getFieldListAnalyze() {
    return fieldListAnalyze;
  }

  /**
     * Returns the no-analyze field list of the schema. The values of those items
     * are searchable by field search. The values are not analyzed.
     * 
     * @return
     */
  public List<String> getFieldListNoAnalyze() {
    return fieldListNoAnalyze;
  }

  /**
     * Returns the field list of items stored in the index.
     * 
     * @return
     */
  public List<String> getFieldListStore() {
    return fieldListStore;
  }

  /**
     * Returns a unique list of all fields part of the index schema.
     * 
     * @return
     */
  public Set<String> getUniqueFieldList() {
    return uniqueFieldList;
  }

  /**
     * Returns the field list of category fields.
     * 
     * @return
     */
  public List<String> getFieldListCategory() {
    return fieldListCategory;
  }

  /**
     * Returns the Lucene schema configuration
     * 
     * @return
     */
  public ItemCollection getConfiguration() {
    ItemCollection config = new ItemCollection();
    config.replaceItemValue("lucence.fulltextFieldList", fieldList);
    config.replaceItemValue("lucence.indexFieldListAnalyze", fieldListAnalyze);
    config.replaceItemValue("lucence.indexFieldListNoAnalyze", fieldListNoAnalyze);
    config.replaceItemValue("lucence.indexFieldListStore", fieldListStore);
    return config;
  }

  /**
     * Returns the extended search term for a given query. The search term will be
     * extended with a users roles to test the read access level of each workitem
     * matching the search term.
     * 
     * @param sSearchTerm
     * @return extended search term
     * @throws QueryException in case the searchtem is not understandable.
     */
  public String getExtendedSearchTerm(String sSearchTerm) throws QueryException {
    if (sSearchTerm == null || "".equals(sSearchTerm)) {
      logger.warning("No search term provided!");
      return "";
    }
    if (!documentService.isUserInRole(DocumentService.ACCESSLEVEL_MANAGERACCESS)) {
      List<String> userNameList = documentService.getUserNameList();
      String sAccessTerm = "($readaccess:" + ANONYMOUS;
      for (String aRole : userNameList) {
        if (!"".equals(aRole)) {
          sAccessTerm += " OR $readaccess:\"" + aRole + "\"";
        }
      }
      sAccessTerm += ") AND ";
      sSearchTerm = sAccessTerm + sSearchTerm;
    }
    logger.finest("......lucene final searchTerm=" + sSearchTerm);
    return sSearchTerm;
  }

  /**
     * This helper method escapes special characters found in a lucene search term.
     * The method can be used by clients to prepare a search phrase.
     * <p>
     * Special characters are characters that are part of the lucene query syntax
     * <p>
     * <code>+ - && || ! ( ) { } [ ] ^ " ~ * ? : \ /</code>
     * <p>
     * Clients should use the method normalizeSearchTerm() instead of
     * escapeSearchTerm() to prepare a user input for a lucene search.
     * 
     * @see normalizeSearchTerm
     * @param searchTerm
     * @param ignoreBracket - if true brackes will not be escaped.
     * @return escaped search term
     */
  public String escapeSearchTerm(String searchTerm, boolean ignoreBracket) {
    if (searchTerm == null || searchTerm.isEmpty()) {
      return searchTerm;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < searchTerm.length(); i++) {
      char c = searchTerm.charAt(i);
      if (c == '\\' || c == '+' || c == '-' || c == '!' || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '?' || c == '|' || c == '&' || c == '/') {
        sb.append('\\');
      }
      if (!ignoreBracket && (c == '(' || c == ')')) {
        sb.append('\\');
      }
      sb.append(c);
    }
    return sb.toString();
  }

  public String escapeSearchTerm(String searchTerm) {
    return escapeSearchTerm(searchTerm, false);
  }

  /**
     * This method normalizes a search term. The method can be used by clients to
     * prepare a search phrase. The serach term will be lowercased and special
     * characters will be replaced by a blank separator
     * <p>
     * e.g. 'europe/berlin' will be normalized to 'europe berlin'
     * <p>
     * In case the searchTerm contains numbers the method escapes special characters
     * instead of replacing with a blank:
     * <p>
     * e.g. 'r-555/333' will be converted into 'r\-555\/333'
     * <p>
     * Special characters are characters that are part of the lucene query syntax
     * <p>
     * <code>+ - && || ! ( ) { } [ ] ^ " ~ ? : \ /</code>
     * <p>
     * 
     * @param searchTerm
     * @return normalized search term
     * 
     */
  public String normalizeSearchTerm(String searchTerm) {
    if (searchTerm == null) {
      return "";
    }
    if (searchTerm.trim().isEmpty()) {
      return "";
    }
    searchTerm = searchTerm.toLowerCase();
    if (containsDigit(searchTerm)) {
      return escapeSearchTerm(searchTerm, false);
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < searchTerm.length(); i++) {
      char c = searchTerm.charAt(i);
      if (c == '\\' || c == '+' || c == '-' || c == '!' || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '?' || c == '|' || c == '&' || c == '/') {
        sb.append(' ');
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
     * Test if a string contains a number. Seems to be faster than regex.
     * <p>
     * See:
     * https://stackoverflow.com/questions/18590901/check-if-a-string-contains-numbers-java
     * 
     * @param s
     * @return
     */
  private boolean containsDigit(String s) {
    boolean containsDigit = false;
    if (s != null && !s.isEmpty()) {
      for (char c : s.toCharArray()) {
        if (containsDigit = Character.isDigit(c)) {
          break;
        }
      }
    }
    return containsDigit;
  }
}