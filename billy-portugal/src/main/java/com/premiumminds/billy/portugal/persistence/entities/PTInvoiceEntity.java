package com.premiumminds.billy.portugal.persistence.entities;
import java.util.List;
import com.premiumminds.billy.core.services.entities.Payment;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoiceEntry;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;

public interface PTInvoiceEntity extends PTGenericInvoiceEntity, PTInvoice {
  @Override public <T extends GenericInvoiceEntry> List<T> getEntries();

  @Override public <T extends Payment> List<T> getPayments();
}