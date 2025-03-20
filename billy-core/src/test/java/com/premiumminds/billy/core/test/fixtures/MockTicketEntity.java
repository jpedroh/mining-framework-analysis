package com.premiumminds.billy.core.test.fixtures;
import java.util.Date;
import com.premiumminds.billy.core.persistence.entities.TicketEntity;
import com.premiumminds.billy.core.services.UID;

public class MockTicketEntity extends MockBaseEntity implements TicketEntity {
  private static final long serialVersionUID = 1L;

  private UID objectUID;

  private Date creationDate;

  private Date processDate;

  public MockTicketEntity() {
  }

  @Override public UID getObjectUID() {
    return this.objectUID;
  }

  @Override public Date getCreationDate() {
    return this.creationDate;
  }

  @Override public Date getProcessDate() {
    return this.processDate;
  }

  @Override public void setObjectUID(UID objectUID) {
    this.objectUID = objectUID;
  }

  @Override public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override public void setProcessDate(Date processDate) {
    this.processDate = processDate;
  }
}