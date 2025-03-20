package com.premiumminds.billy.spain.test.fixtures;
import com.premiumminds.billy.core.test.fixtures.MockCustomerEntity;
import com.premiumminds.billy.spain.persistence.entities.ESCustomerEntity;

public class MockESCustomerEntity extends MockCustomerEntity implements ESCustomerEntity {
  private static final long serialVersionUID = 1L;

  private String referralName;

  @Override public String getReferralName() {
    return this.referralName;
  }

  @Override public void setReferralName(String referralName) {
    this.referralName = referralName;
  }
}