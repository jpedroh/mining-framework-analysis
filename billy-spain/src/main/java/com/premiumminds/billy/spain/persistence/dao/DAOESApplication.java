package com.premiumminds.billy.spain.persistence.dao;
import com.premiumminds.billy.core.persistence.dao.DAOApplication;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.spain.persistence.entities.ESApplicationEntity;

public interface DAOESApplication extends DAOApplication {
  @Override public ESApplicationEntity getEntityInstance();

  @Override public ESApplicationEntity get(UID uid);
}