package com.premiumminds.billy.spain.test.fixtures;
import java.util.ArrayList;
import java.util.List;
import com.premiumminds.billy.core.test.fixtures.MockGenericInvoiceEntity;
import com.premiumminds.billy.spain.persistence.entities.ESGenericInvoiceEntity;
import com.premiumminds.billy.spain.services.entities.ESGenericInvoiceEntry;
import com.premiumminds.billy.spain.services.entities.ESPayment;

public class MockESGenericInvoiceEntity extends MockGenericInvoiceEntity implements ESGenericInvoiceEntity {
  private static final long serialVersionUID = 1L;

  protected boolean cancelled;

  protected boolean billed;

  protected String eacCode;

  protected List<ESPayment> payments;

  public MockESGenericInvoiceEntity() {
    this.payments = new ArrayList<>();
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ESGenericInvoiceEntry> getEntries() {
    return (List<ESGenericInvoiceEntry>) (List<?>) super.getEntries();
  }

  @Override public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  @Override public void setBilled(boolean billed) {
    this.billed = billed;
  }

  @Override public boolean isCancelled() {
    return this.cancelled;
  }

  @Override public boolean isBilled() {
    return this.billed;
  }

  @Override public String getEACCode() {
    return this.eacCode;
  }

  @Override public void setEACCode(String eacCode) {
    this.eacCode = eacCode;
  }

  @Override public List<ESPayment> getPayments() {
    return this.payments;
  }
}