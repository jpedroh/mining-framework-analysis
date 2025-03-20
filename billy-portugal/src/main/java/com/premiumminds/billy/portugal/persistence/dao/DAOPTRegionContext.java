package com.premiumminds.billy.portugal.persistence.dao;
import com.premiumminds.billy.core.persistence.dao.DAOContext;
import com.premiumminds.billy.portugal.persistence.entities.PTRegionContextEntity;

public interface DAOPTRegionContext extends DAOContext {
  @Override public PTRegionContextEntity getEntityInstance();
}