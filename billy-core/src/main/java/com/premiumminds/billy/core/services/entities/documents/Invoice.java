package com.premiumminds.billy.core.services.entities.documents;
import java.util.Collection;

public interface Invoice extends GenericInvoice {
  @Override public Collection<InvoiceEntry> getEntries();
}