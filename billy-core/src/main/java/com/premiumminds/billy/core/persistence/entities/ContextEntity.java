package com.premiumminds.billy.core.persistence.entities;
import com.premiumminds.billy.core.services.entities.Context;

public interface ContextEntity extends Context, BaseEntity {
  public void setName(String name);

  public void setDescription(String description);

  public <T extends ContextEntity> void setParentContext(T parent);
}