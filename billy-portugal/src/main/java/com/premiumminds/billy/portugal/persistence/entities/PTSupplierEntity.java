package com.premiumminds.billy.portugal.persistence.entities;
import com.premiumminds.billy.core.persistence.entities.SupplierEntity;
import com.premiumminds.billy.portugal.services.entities.PTSupplier;

public interface PTSupplierEntity extends PTSupplier, SupplierEntity {
  public void setReferralName(String referralName);
}