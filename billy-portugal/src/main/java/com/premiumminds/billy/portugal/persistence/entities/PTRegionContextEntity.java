package com.premiumminds.billy.portugal.persistence.entities;
import com.premiumminds.billy.core.persistence.entities.ContextEntity;
import com.premiumminds.billy.portugal.services.entities.PTRegionContext;

public interface PTRegionContextEntity extends ContextEntity, PTRegionContext {
  public void setRegionCode(String regionCode);
}