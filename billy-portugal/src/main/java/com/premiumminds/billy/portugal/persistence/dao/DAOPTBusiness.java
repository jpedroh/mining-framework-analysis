package com.premiumminds.billy.portugal.persistence.dao;
import com.premiumminds.billy.core.persistence.dao.DAOBusiness;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;

public interface DAOPTBusiness extends DAOBusiness {
  @Override public PTBusinessEntity getEntityInstance();

  @Override public PTBusinessEntity get(UID uid);
}