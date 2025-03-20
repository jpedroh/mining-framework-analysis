package com.premiumminds.billy.spain.persistence.dao;
import com.premiumminds.billy.core.persistence.dao.DAOContext;
import com.premiumminds.billy.spain.persistence.entities.ESRegionContextEntity;

public interface DAOESRegionContext extends DAOContext {
  @Override public ESRegionContextEntity getEntityInstance();
}