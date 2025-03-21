package org.cloudfoundry.uaa.identityzones;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;

/**
 * The entity response payload for Identity Zone
 */
public abstract class AbstractIdentityZone {
  /**
     * Whether the identity zone is active
     */
  @JsonProperty(value = "active") @Nullable abstract Boolean getActive();

  /**
     * The configuration
     */
  @JsonProperty(value = "config") @Nullable abstract IdentityZoneConfiguration getConfiguration();

  /**
     * The creation date of the identity zone
     */
  @JsonProperty(value = "created") abstract Long getCreatedAt();

  /**
     * The description of the identity zone
     */
  @JsonProperty(value = "description") @Nullable abstract String getDescription();

  /**
     * The id of the identity zone
     */
  @JsonProperty(value = "id") abstract String getId();

  /**
     * The last modification date of the identity zone
     */
  @JsonProperty(value = "last_modified") abstract Long getLastModified();

  /**
     * The name of the identity zone
     */
  @JsonProperty(value = "name") abstract String getName();

  /**
     * The unique sub domain. It will be converted into lowercase upon creation.
     */
  @JsonProperty(value = "subdomain") abstract String getSubdomain();

  /**
     * The version of the identity zone.
     */
  @JsonProperty(value = "version") @Nullable abstract Integer getVersion();
}