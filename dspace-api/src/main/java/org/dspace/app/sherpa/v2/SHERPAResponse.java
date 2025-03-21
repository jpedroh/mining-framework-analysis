package org.dspace.app.sherpa.v2;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Model class for the SHERPAv2 API (JSON) response for a publication (journal) search
 * The structure and approached used is quite different to the simple v1 API used previously
 * The structure is based on journal data, which in turn contains data about publishers and policies
 *
 * @see SHERPAJournal
 *
 * @author Kim Shepherd
 *
 */
public class SHERPAResponse implements Serializable {
  private static final long serialVersionUID = 2732963970169240597L;

  private boolean error;

  private String message;

  private SHERPASystemMetadata metadata;

  private List<SHERPAJournal> journals;

  private int id;

  private String uri;

  @JsonIgnore private Date retrievalTime = new Date();

  public enum SHERPAFormat {
    JSON,
    XML
  }



  private static Logger log = LogManager.getLogger();

  /**
     * Parse SHERPA v2 API for a given format
     * @param input - input stream from the HTTP response content
     * @param format - requested format
     * @throws IOException
     */
  public SHERPAResponse(InputStream input, SHERPAFormat format) throws IOException {
    if (format == SHERPAFormat.JSON) {
      parseJSON(input);
    }
  }

  /**
     * Create an empty SHERPAResponse representation
     */
  public SHERPAResponse() {
  }

  /**
     * Parse the SHERPA v2 API JSON and construct Romeo policy data for display
     * This method does not return a value, but rather populates the metadata and journals objects
     * with data parsed from the JSON.
     * @param jsonData - the JSON input stream from the API result response body
     */
  private void parseJSON(InputStream jsonData) throws IOException {
    InputStreamReader streamReader = new InputStreamReader(jsonData, StandardCharsets.UTF_8);
    JSONTokener jsonTokener = new JSONTokener(streamReader);
    JSONObject httpResponse;
    try {
      httpResponse = new JSONObject(jsonTokener);
      if (httpResponse.has("items")) {
        JSONArray items = httpResponse.getJSONArray("items");
        if (items.length() > 0) {
          metadata = new SHERPASystemMetadata();
          this.journals = new ArrayList<>();
          for (int itemIndex = 0; itemIndex < items.length(); itemIndex++) {
            List<SHERPAPublisher> sherpaPublishers = new ArrayList<>();
            List<SHERPAPublisherPolicy> policies = new ArrayList<>();
            SHERPAPublisher sherpaPublisher = new SHERPAPublisher();
            SHERPAJournal sherpaJournal = new SHERPAJournal();
            JSONObject item = items.getJSONObject(itemIndex);
            if (item.has("system_metadata")) {
              JSONObject systemMetadata = item.getJSONObject("system_metadata");
              metadata = parseSystemMetadata(systemMetadata);
            }
            if (item.has("publisher_policy")) {
              JSONArray publisherPolicies = item.getJSONArray("publisher_policy");
              for (int i = 0; i < publisherPolicies.length(); i++) {
                JSONObject policy = publisherPolicies.getJSONObject(i);
                String moniker = null;
                if (policy.has("internal_moniker")) {
                  moniker = policy.getString("internal_moniker");
                }
                if ("Open access option".equalsIgnoreCase(moniker)) {
                  log.debug("This is the Open access options policy - a special case");
                  if (policy.has("urls")) {
                    JSONArray urls = policy.getJSONArray("urls");
                    for (int u = 0; u < urls.length(); u++) {
                      JSONObject url = urls.getJSONObject(u);
                      if (url.has("description") && "Open Access".equalsIgnoreCase(url.getString("description"))) {
                        log.debug("Found OA paid access url: " + url.getString("url"));
                        sherpaPublisher.setPaidAccessDescription(url.getString("description"));
                        sherpaPublisher.setPaidAccessUrl(url.getString("url"));
                        break;
                      }
                    }
                  }
                  continue;
                }
                SHERPAPublisherPolicy sherpaPublisherPolicy = parsePublisherPolicy(policy);
                policies.add(sherpaPublisherPolicy);
              }
              if (item.has("publishers")) {
                JSONArray publishers = item.getJSONArray("publishers");
                if (publishers.length() > 0) {
                  JSONObject publisherElement = publishers.getJSONObject(0);
                  if (publisherElement.has("publisher")) {
                    JSONObject publisher = publisherElement.getJSONObject("publisher");
                    sherpaPublisher.setName(parsePublisherName(publisher));
                    sherpaPublisher.setUri(parsePublisherURL(publisher));
                  }
                }
              }
              sherpaJournal = parseJournal(item, sherpaPublisher.getName());
            }
            sherpaPublishers.add(sherpaPublisher);
            sherpaJournal.setPublisher(sherpaPublisher);
            sherpaJournal.setPublishers(sherpaPublishers);
            sherpaJournal.setPolicies(policies);
            this.journals.add(sherpaJournal);
          }
        } else {
          error = true;
          message = "No results found";
        }
      } else {
        error = true;
        message = "No results found";
      }
    } catch (JSONException e) {
      log.error("Failed to parse SHERPA response", e);
      error = true;
    } finally {
      streamReader.close();
    }
  }

  /**
     * Parse system metadata and return populated SHERPASystemMetadata object
     * @param systemMetadata
     */
  private SHERPASystemMetadata parseSystemMetadata(JSONObject systemMetadata) {
    SHERPASystemMetadata metadata = new SHERPASystemMetadata();
    if (systemMetadata.has("uri")) {
      this.uri = systemMetadata.getString("uri");
      metadata.setUri(this.uri);
    } else {
      log.error("SHERPA URI missing for API response item");
    }
    if (systemMetadata.has("id")) {
      this.id = systemMetadata.getInt("id");
      metadata.setId(this.id);
    } else {
      log.error("SHERPA internal ID missing for API response item");
    }
    if (systemMetadata.has("date_created")) {
      metadata.setDateCreated(systemMetadata.getString("date_created"));
    }
    if (systemMetadata.has("date_modified")) {
      metadata.setDateModified(systemMetadata.getString("date_modified"));
    }
    if (systemMetadata.has("publicly_visible")) {
      metadata.setPubliclyVisible("yes".equals(systemMetadata.getString("publicly_visible")));
    }
    if (systemMetadata.has("listed_in_doaj")) {
      metadata.setPubliclyVisible("yes".equals(systemMetadata.getString("listed_in_doaj")));
    }
    return metadata;
  }

  /**
     * Parse journal JSON data and return populated bean
     * This method also takes publisherName as a string to help construct some
     * legacy labels
     * @param item - the main result item JSON (which is the closest thing to an actual 'journal')
     * @param publisherName - the parsed publisher name
     * @return
     */
  private SHERPAJournal parseJournal(JSONObject item, String publisherName) {
    SHERPAJournal sherpaJournal = new SHERPAJournal();
    if (item.has("title")) {
      JSONArray titles = item.getJSONArray("title");
      if (titles.length() > 0) {
        List<String> titleList = new ArrayList<>();
        for (int t = 0; t < titles.length(); t++) {
          JSONObject title = titles.getJSONObject(t);
          if (title.has("title")) {
            titleList.add(title.getString("title").trim());
          }
        }
        sherpaJournal.setTitles(titleList);
        if (titleList.size() > 0) {
          sherpaJournal.setRomeoPub(publisherName + ": " + titleList.get(0));
          sherpaJournal.setZetoPub(publisherName + ": " + titleList.get(0));
          log.debug("Found journal title: " + titleList.get(0));
        }
      }
    }
    if (item.has("url")) {
      sherpaJournal.setUrl(item.getString("url"));
    }
    if (item.has("issns")) {
      JSONArray issns = item.getJSONArray("issns");
      List<String> issnList = new ArrayList<>();
      for (int ii = 0; ii < issns.length(); ii++) {
        JSONObject issn = issns.getJSONObject(ii);
        issnList.add(issn.getString("issn").trim());
      }
      sherpaJournal.setIssns(issnList);
    }
    if (item.has("listed_in_doaj")) {
      sherpaJournal.setInDOAJ("yes".equals(item.getString("listed_in_doaj")));
    }
    return sherpaJournal;
  }

  /**
     * Parse a publisher_policy JSON data and return a populated bean
     * @param policy - each publisher policy node in the JSON array
     * @return populated SHERPAPublisherPolicy object
     */
  private SHERPAPublisherPolicy parsePublisherPolicy(JSONObject policy) {
    SHERPAPublisherPolicy sherpaPublisherPolicy = new SHERPAPublisherPolicy();
    String moniker = null;
    if (policy.has("internal_moniker")) {
      moniker = policy.getString("internal_moniker");
      sherpaPublisherPolicy.setInternalMoniker(moniker);
    }
    if (policy.has("urls")) {
      JSONArray urls = policy.getJSONArray("urls");
      Map<String, String> copyrightLinks = new TreeMap<>();
      for (int u = 0; u < urls.length(); u++) {
        JSONObject url = urls.getJSONObject(u);
        if (url.has("description") && url.has("url")) {
          log.debug("Setting copyright URL: " + url.getString("url"));
          copyrightLinks.put(url.getString("url"), url.getString("description"));
        }
      }
      sherpaPublisherPolicy.setUrls(copyrightLinks);
    }
    int submittedOption = 0;
    int acceptedOption = 0;
    int publishedOption = 0;
    int currentOption = 0;
    if (policy.has("permitted_oa")) {
      List<String> allowed = new ArrayList<>();
      JSONArray permittedOA = policy.getJSONArray("permitted_oa");
      List<SHERPAPermittedVersion> permittedVersions = new ArrayList<>();
      for (int p = 0; p < permittedOA.length(); p++) {
        JSONObject permitted = permittedOA.getJSONObject(p);
        if (permitted.has("article_version")) {
          JSONArray versions = permitted.getJSONArray("article_version");
          for (int v = 0; v < versions.length(); v++) {
            SHERPAPermittedVersion permittedVersion = parsePermittedVersion(permitted, v);
            allowed.add(permittedVersion.getArticleVersion());
            if ("submitted".equals(permittedVersion.getArticleVersion())) {
              submittedOption++;
              currentOption = submittedOption;
            } else {
              if ("accepted".equals(permittedVersion.getArticleVersion())) {
                acceptedOption++;
                currentOption = acceptedOption;
              } else {
                if ("published".equals(permittedVersion.getArticleVersion())) {
                  publishedOption++;
                  currentOption = publishedOption;
                }
              }
            }
            permittedVersion.setOption(currentOption);
            permittedVersions.add(permittedVersion);
          }
        }
        if (allowed.contains("submitted")) {
          sherpaPublisherPolicy.setPreArchiving("can");
        }
        if (allowed.contains("accepted")) {
          sherpaPublisherPolicy.setPostArchiving("can");
        }
        if (allowed.contains("published")) {
          sherpaPublisherPolicy.setPubArchiving("can");
        }
      }
      sherpaPublisherPolicy.setPermittedVersions(permittedVersions);
    }
    return sherpaPublisherPolicy;
  }

  /**
     * Parse permitted version JSON and populate new bean from the data
     * @param permitted - each 'permitted_oa' node in the JSON array
     * @return populated SHERPAPermittedVersion object
     */
  private SHERPAPermittedVersion parsePermittedVersion(JSONObject permitted, int index) {
    SHERPAPermittedVersion permittedVersion = new SHERPAPermittedVersion();
    String articleVersion = "unknown";
    if (permitted.has("article_version")) {
      JSONArray versions = permitted.getJSONArray("article_version");
      articleVersion = versions.getString(index);
      permittedVersion.setArticleVersion(articleVersion);
      log.debug("Added allowed version: " + articleVersion + " to list");
    }
    if (permitted.has("conditions")) {
      List<String> conditionList = new ArrayList<>();
      JSONArray conditions = permitted.getJSONArray("conditions");
      for (int c = 0; c < conditions.length(); c++) {
        conditionList.add(conditions.getString(c).trim());
      }
      permittedVersion.setConditions(conditionList);
    }
    List<String> prerequisites = new ArrayList<>();
    if (permitted.has("prerequisites")) {
      JSONObject prereqs = permitted.getJSONObject("prerequisites");
      if (prereqs.has("prerequisites_phrases")) {
        JSONArray phrases = prereqs.getJSONArray("prerequisites_phrases");
        for (int pp = 0; pp < phrases.length(); pp++) {
          JSONObject phrase = phrases.getJSONObject(pp);
          if (phrase.has("phrase")) {
            prerequisites.add(phrase.getString("phrase").trim());
          }
        }
      }
    }
    permittedVersion.setPrerequisites(prerequisites);
    List<String> sherpaLocations = new ArrayList<>();
    if (permitted.has("location")) {
      JSONObject locations = permitted.getJSONObject("location");
      if (locations.has("location_phrases")) {
        JSONArray locationPhrases = locations.getJSONArray("location_phrases");
        if (locationPhrases.length() > 0) {
          for (int l = 0; l < locationPhrases.length(); l++) {
            JSONObject locationPhrase = locationPhrases.getJSONObject(l);
            if (locationPhrase.has("phrase")) {
              sherpaLocations.add(locationPhrase.getString("phrase").trim());
            }
          }
        }
      }
    }
    permittedVersion.setLocations(sherpaLocations);
    List<String> sherpaLicenses = new ArrayList<>();
    if (permitted.has("license")) {
      JSONArray licences = permitted.getJSONArray("license");
      for (int l = 0; l < licences.length(); l++) {
        JSONObject licence = licences.getJSONObject(l);
        if (licence.has("license_phrases")) {
          JSONArray phrases = licence.getJSONArray("license_phrases");
          for (int ll = 0; ll < phrases.length(); ll++) {
            JSONObject phrase = phrases.getJSONObject(ll);
            if (phrase.has("phrase")) {
              sherpaLicenses.add(phrase.getString("phrase").trim());
            }
          }
        }
      }
    }
    permittedVersion.setLicenses(sherpaLicenses);
    if (permitted.has("embargo")) {
      JSONObject embargo = permitted.getJSONObject("embargo");
      SHERPAEmbargo SHERPAEmbargo = new SHERPAEmbargo(embargo.getInt("amount"), embargo.getString("units"));
      permittedVersion.setEmbargo(SHERPAEmbargo);
    }
    return permittedVersion;
  }

  /**
     * Parse publisher array and return the first name string found
     * @param publisher - array of publisher JSON data
     * @return first publisher name found (trimmed String)
     */
  private String parsePublisherName(JSONObject publisher) {
    String name = null;
    if (publisher.has("name")) {
      JSONArray publisherNames = publisher.getJSONArray("name");
      if (publisherNames.length() > 0) {
        JSONObject publisherName = publisherNames.getJSONObject(0);
        if (publisherName.has("name")) {
          name = publisherName.getString("name").trim();
        }
      }
    }
    return name;
  }

  /**
     * Parse publisher URL from the json data
     * @param publisher - publisher object (from JSON array)
     * @return publisher URL as string
     */
  private String parsePublisherURL(JSONObject publisher) {
    if (publisher.has("url")) {
      return publisher.getString("url");
    }
    return null;
  }

  /**
     * Create new response object to be handled as an error
     * @param message - the message to render in logs or error pages
     */
  public SHERPAResponse(String message) {
    this.message = message;
    this.error = true;
  }

  public boolean isError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<SHERPAJournal> getJournals() {
    return journals;
  }

  public SHERPASystemMetadata getMetadata() {
    return metadata;
  }

  public Date getRetrievalTime() {
    return retrievalTime;
  }
}