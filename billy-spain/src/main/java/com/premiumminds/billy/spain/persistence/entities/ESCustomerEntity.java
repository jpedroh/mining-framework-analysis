package com.premiumminds.billy.spain.persistence.entities;
import com.premiumminds.billy.core.persistence.entities.CustomerEntity;
import com.premiumminds.billy.spain.services.entities.ESCustomer;

public interface ESCustomerEntity extends CustomerEntity, ESCustomer {
  public void setReferralName(String referralName);
}