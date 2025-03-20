package com.premiumminds.billy.spain.test.fixtures;
import com.premiumminds.billy.core.test.fixtures.MockSupplierEntity;
import com.premiumminds.billy.spain.persistence.entities.ESSupplierEntity;

public class MockESSupplierEntity extends MockSupplierEntity implements ESSupplierEntity {
  /**
     *
     */
  private static final long serialVersionUID = 1L;

  private String referral;

  public MockESSupplierEntity() {
  }

  @Override public String getReferralName() {
    return this.referral;
  }

  @Override public void setReferralName(String referralName) {
    this.referral = referralName;
  }
}