package com.premiumminds.billy.portugal.persistence.dao;
import com.premiumminds.billy.core.persistence.dao.DAOAddress;
import com.premiumminds.billy.portugal.persistence.entities.PTAddressEntity;

public interface DAOPTAddress extends DAOAddress {
  @Override public PTAddressEntity getEntityInstance();
}