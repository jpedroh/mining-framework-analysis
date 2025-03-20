package com.premiumminds.billy.portugal.persistence.entities;
import java.util.List;
import com.premiumminds.billy.portugal.services.entities.PTInvoiceEntry;
import com.premiumminds.billy.portugal.services.entities.PTPayment;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice;

public interface PTSimpleInvoiceEntity extends PTInvoiceEntity, PTSimpleInvoice {
  @SuppressWarnings(value = { "unchecked" }) @Override public List<PTInvoiceEntry> getEntries();

  @SuppressWarnings(value = { "unchecked" }) @Override public List<PTPayment> getPayments();

  public void setClientType(CLIENTTYPE type);
}