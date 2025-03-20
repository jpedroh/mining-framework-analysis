package com.premiumminds.billy.core.test.fixtures;
import java.util.Date;
import com.premiumminds.billy.core.persistence.entities.BaseEntity;
import com.premiumminds.billy.core.services.UID;

public class MockBaseEntity implements BaseEntity {
  private static final long serialVersionUID = 1L;

  public Integer id;

  public UID uid;

  public Date createTimestamp;

  public Date updateTimestamp;

  public boolean isNew;

  public MockBaseEntity() {
    this.isNew = true;
  }

  @Override public boolean isNew() {
    return this.isNew;
  }

  @Override public Integer getID() {
    return this.id;
  }

  @Override public UID getUID() {
    return this.uid;
  }

  @Override public void setUID(UID uid) {
    this.uid = uid;
  }

  @Override public Date getCreateTimestamp() {
    return this.createTimestamp;
  }

  @Override public Date getUpdateTimestamp() {
    return this.updateTimestamp;
  }

  @Override public void initializeEntityDates() {
    this.createTimestamp = this.updateTimestamp = new Date();
  }
}