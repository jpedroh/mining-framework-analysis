package com.premiumminds.billy.spain.persistence.entities;
import com.premiumminds.billy.core.persistence.entities.SupplierEntity;
import com.premiumminds.billy.spain.services.entities.ESSupplier;

public interface ESSupplierEntity extends ESSupplier, SupplierEntity {
  public void setReferralName(String referralName);
}