package com.premiumminds.billy.core.persistence.entities;
import java.io.Serializable;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.entities.Entity;

/**
 * @author Francisco Vargas
 *
 *         Represents the entity with all basic fields needed for a valid billy
 *         entity
 */
public interface BaseEntity extends Entity, Serializable {
  @Override public void setUID(UID uid);

  public void initializeEntityDates();
}