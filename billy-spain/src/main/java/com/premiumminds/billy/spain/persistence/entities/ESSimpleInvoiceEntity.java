package com.premiumminds.billy.spain.persistence.entities;
import java.util.List;
import com.premiumminds.billy.spain.services.entities.ESInvoiceEntry;
import com.premiumminds.billy.spain.services.entities.ESPayment;
import com.premiumminds.billy.spain.services.entities.ESSimpleInvoice;

public interface ESSimpleInvoiceEntity extends ESInvoiceEntity, ESSimpleInvoice {
  @SuppressWarnings(value = { "unchecked" }) @Override public List<ESInvoiceEntry> getEntries();

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ESPayment> getPayments();

  public void setClientType(CLIENTTYPE type);
}