package com.premiumminds.billy.portugal.persistence.dao;
import com.premiumminds.billy.core.persistence.dao.DAOCustomer;
import com.premiumminds.billy.portugal.persistence.entities.PTCustomerEntity;

public interface DAOPTCustomer extends DAOCustomer {
  @Override public PTCustomerEntity getEntityInstance();
}