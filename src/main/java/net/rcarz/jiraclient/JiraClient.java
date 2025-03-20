package net.rcarz.jiraclient;
import java.net.URI;
import java.util.List;
import net.sf.json.JSONObject;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * A simple JIRA REST client.
 */
public class JiraClient {
  private RestClient restclient = null;

  private String username = null;

  /**
     * Creates a JIRA client.
     *
     * @param uri Base URI of the JIRA server
     */
  public JiraClient(String uri) {
    this(uri, null);
  }

  /**
     * Creates an authenticated JIRA client.
     *
     * @param uri Base URI of the JIRA server
     * @param creds Credentials to authenticate with
     */
  public JiraClient(String uri, ICredentials creds) {
    DefaultHttpClient httpclient = new DefaultHttpClient();
    restclient = new RestClient(httpclient, creds, URI.create(uri));
    if (creds != null) {
      username = creds.getLogonName();
    }
  }

  /**
     * Creates a new issue in the given project.
     *
     * @param project Key of the project to create in
     * @param issueType Name of the issue type to create
     *
     * @return a fluent create instance
     *
     * @throws JiraException when something goes wrong
     */
  public Issue.FluentCreate createIssue(String project, String issueType) throws JiraException {
    return Issue.create(restclient, project, issueType);
  }

  /**
     * Retreives the issue with the given key.
     *
     * @param key Issue key (PROJECT-123)
     *
     * @return an issue instance
     *
     * @throws JiraException when something goes wrong
     */
  public Issue getIssue(String key) throws JiraException {
    return Issue.get(restclient, key);
  }

  /**
     * Search for issues with the given query.
     *
     * @param jql JQL statement
     *
     * @return a search result structure with results
     *
     * @throws JiraException when the search fails
     */
  public Issue.SearchResult searchIssues(String jql) throws JiraException {
    return Issue.search(restclient, jql);
  }

  /**
     * Get a list of options for a custom field
     *
     * @param field field id
     * @param project Key of the project context
     * @param issueType Name of the issue type 
     *
     * @return a search result structure with results
     *
     * @throws JiraException when the search fails
     */
  public List<CustomFieldOption> getCustomFieldAllowedValues(String field, String project, String issueType) throws JiraException {
    JSONObject createMetadata = (JSONObject) Issue.getCreateMetadata(restclient, project, issueType);
    JSONObject fieldMetadata = (JSONObject) createMetadata.get(field);
    List<CustomFieldOption> customFieldOptions = Field.getResourceArray(CustomFieldOption.class, fieldMetadata.get("allowedValues"), restclient);
    return customFieldOptions;
  }

  /**
     * Get a list of options for a components
     *
     * @param project Key of the project context
     * @param issueType Name of the issue type 
     *
     * @return a search result structure with results
     *
     * @throws JiraException when the search fails
     */
  public List<Component> getComponentsAllowedValues(String project, String issueType) throws JiraException {
    JSONObject createMetadata = (JSONObject) Issue.getCreateMetadata(restclient, project, issueType);
    JSONObject fieldMetadata = (JSONObject) createMetadata.get(Field.COMPONENTS);
    List<Component> componentOptions = Field.getResourceArray(Component.class, fieldMetadata.get("allowedValues"), restclient);
    return componentOptions;
  }

  public RestClient getRestClient() {
    return restclient;
  }

  public String getSelf() {
    return username;
  }
}