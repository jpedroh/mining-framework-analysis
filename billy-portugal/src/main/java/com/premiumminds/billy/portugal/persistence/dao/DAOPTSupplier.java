package com.premiumminds.billy.portugal.persistence.dao;
import com.premiumminds.billy.core.persistence.dao.DAOSupplier;
import com.premiumminds.billy.portugal.persistence.entities.PTSupplierEntity;

public interface DAOPTSupplier extends DAOSupplier {
  @Override public PTSupplierEntity getEntityInstance();
}