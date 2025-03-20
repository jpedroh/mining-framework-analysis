package com.premiumminds.billy.portugal.persistence.dao;
import com.premiumminds.billy.core.persistence.dao.DAOContact;
import com.premiumminds.billy.portugal.persistence.entities.PTContactEntity;

public interface DAOPTContact extends DAOContact {
  @Override public PTContactEntity getEntityInstance();
}