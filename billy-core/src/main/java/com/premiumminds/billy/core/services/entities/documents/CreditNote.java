package com.premiumminds.billy.core.services.entities.documents;
import java.util.Collection;

public interface CreditNote extends GenericInvoice {
  @Override public Collection<? extends GenericInvoiceEntry> getEntries();
}