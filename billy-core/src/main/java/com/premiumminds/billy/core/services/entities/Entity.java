package com.premiumminds.billy.core.services.entities;
import java.io.Serializable;
import java.util.Date;
import com.premiumminds.billy.core.services.UID;

/**
 * @author Francisco Vargas
 *
 *         The Billy services basic entity definition. All service entity
 *         definitions should extend or implement BaseEntity.
 */
public interface Entity extends Serializable {
  public Integer getID();

  /**
     * Gets the entity unique identifier.
     *
     * @return The unique identifier.
     */
  public UID getUID();

  /**
     * Sets the unique identifier for the entity. Should only be used in very
     * well controlled contexts.
     *
     * @param uid
     *        The unique identifier to be set.
     */
  public void setUID(UID uid);

  /**
     * Gets the entity creation date.
     *
     * @return The creation date.
     */
  public Date getCreateTimestamp();

  /**
     * Gets the date of the last update of the entity.
     *
     * @return The update date.
     */
  public Date getUpdateTimestamp();

  /**
     * Tells whether or not the entity is a new one
     *
     * @return true if the entity is new
     */
  public boolean isNew();
}