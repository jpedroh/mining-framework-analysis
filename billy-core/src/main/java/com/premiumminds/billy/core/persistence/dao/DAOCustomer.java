package com.premiumminds.billy.core.persistence.dao;
import java.util.List;
import com.premiumminds.billy.core.persistence.entities.CustomerEntity;

public interface DAOCustomer extends DAO<CustomerEntity> {
  List<CustomerEntity> getAllActiveCustomers();
}