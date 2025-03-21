package com.zaubersoftware.gnip4j.api.model;
import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * TODO Descripcion de la clase. Los comenterios van en castellano.
 *
 *
 * @author Juan F. Codagnone
 * @since May 28, 2011
 */
public class Url implements Serializable {
  private static final long serialVersionUID = 1L;

  private String url;

  @JsonProperty(value = "expanded_url") private String expandedUrl;

  @JsonProperty(value = "expanded_status") private Integer expandedStatus;

  @JsonProperty(value = "expanded_url_title") private String expandedUrlTitle;

  @JsonProperty(value = "expanded_url_description") private String expandedUrlDescription;

  public final String getUrl() {
    return url;
  }

  public final void setUrl(final String url) {
    this.url = url;
  }

  public final String getExpandedUrl() {
    return expandedUrl;
  }

  public final void setExpandedUrl(final String expandedUrl) {
    this.expandedUrl = expandedUrl;
  }

  public Integer getExpandedStatus() {
    return expandedStatus;
  }

  public void setExpandedStatus(final Integer expandedStatus) {
    this.expandedStatus = expandedStatus;
  }

  public String getExpandedUrlTitle() {
    return expandedUrlTitle;
  }

  public void setExpandedUrlTitle(final String expandedUrlTitle) {
    this.expandedUrlTitle = expandedUrlTitle;
  }

  public String getExpandedUrlDescription() {
    return expandedUrlDescription;
  }

  public void setExpandedUrlDescription(final String expandedUrlDescription) {
    this.expandedUrlDescription = expandedUrlDescription;
  }
}