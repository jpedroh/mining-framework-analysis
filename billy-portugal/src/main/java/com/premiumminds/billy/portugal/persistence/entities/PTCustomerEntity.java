package com.premiumminds.billy.portugal.persistence.entities;
import com.premiumminds.billy.core.persistence.entities.CustomerEntity;
import com.premiumminds.billy.portugal.services.entities.PTCustomer;

public interface PTCustomerEntity extends CustomerEntity, PTCustomer {
  public void setReferralName(String referralName);
}