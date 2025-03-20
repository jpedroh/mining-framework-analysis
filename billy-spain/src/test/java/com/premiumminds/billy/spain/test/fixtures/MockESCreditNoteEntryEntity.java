package com.premiumminds.billy.spain.test.fixtures;
import com.premiumminds.billy.core.test.fixtures.MockGenericInvoiceEntryEntity;
import com.premiumminds.billy.spain.persistence.entities.ESCreditNoteEntryEntity;
import com.premiumminds.billy.spain.services.entities.ESInvoice;

public class MockESCreditNoteEntryEntity extends MockGenericInvoiceEntryEntity implements ESCreditNoteEntryEntity {
  private static final long serialVersionUID = 1L;

  private ESInvoice reference;

  private String reason;

  public MockESCreditNoteEntryEntity() {
  }

  @Override public ESInvoice getReference() {
    return this.reference;
  }

  @Override public String getReason() {
    return this.reason;
  }

  @Override public void setReference(ESInvoice reference) {
    this.reference = reference;
  }

  @Override public void setReason(String reason) {
    this.reason = reason;
  }
}