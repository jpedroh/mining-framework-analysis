package com.premiumminds.billy.spain.test.fixtures;
import com.premiumminds.billy.core.persistence.entities.ContextEntity;
import com.premiumminds.billy.core.services.entities.Context;
import com.premiumminds.billy.core.test.fixtures.MockContextEntity;
import com.premiumminds.billy.spain.persistence.entities.ESRegionContextEntity;

public class MockESRegionContextEntity extends MockContextEntity implements ESRegionContextEntity {
  private static final long serialVersionUID = 1L;

  public String regionCode;

  public MockESRegionContextEntity() {
  }

  @Override public <T extends ContextEntity> void setParentContext(T parent) {
    super.setParentContext(parent);
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public Context getParentContext() {
    return super.getParentContext();
  }
}