package com.premiumminds.billy.portugal.test.fixtures;
import com.premiumminds.billy.core.test.fixtures.MockSupplierEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTSupplierEntity;

public class MockPTSupplierEntity extends MockSupplierEntity implements PTSupplierEntity {
  /**
     *
     */
  private static final long serialVersionUID = 1L;

  private String referral;

  public MockPTSupplierEntity() {
  }

  @Override public String getReferralName() {
    return this.referral;
  }

  @Override public void setReferralName(String referralName) {
    this.referral = referralName;
  }
}